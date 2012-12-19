/*   _______ __ __                    _______                    __
 *  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_
 *  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|
 *  |_______|__|__| \___/|_____|__|    |___|  |__| |_____|_____||____|
 *
 *  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav "Gussoh" Sohtell
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package silvertrout.plugins.packagetracker;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import silvertrout.Channel;
import silvertrout.User;
import silvertrout.plugins.packagetracker.PackageServiceProviderFactory.*;
import silvertrout.plugins.packagetracker.PackageServiceProviderFactory.PackageServiceProvider.Package;
import silvertrout.plugins.packagetracker.PackageServiceProviderFactory.PackageServiceProvider.PackageEvent;

/**
 * Tracks packages from the Swedish postal service (Posten).
 *
 * Keeps up to date with shipping information by using Posten's or Schenker's
 * xml services. 
 * Posten's service is documented at http://services3.posten.se/c/online_steg_steg
 * with additional information on other pages linked from there.
 * Schenker's service is documented at http://www.schenker.nu/servlet/se.ementor.econgero.servlet.presentation.Main?data.node.id=26784&data.language.id=1
 * with information regarding different types of package id's at
 * http://www.privatpaket.se/servlet/se.ementor.econgero.servlet.presentation.Main?data.node.id=25039&data.language.id=1
 * 
 * Tried to find information about how to tell different service providers apart,
 * found the UPU S10 Standard http://pls.upu.int/document/2011/an/cep_c_4_gn_ep_4-1/src/d008_ad00_an01_p00_r00.pdf
 * Looks like there is no easy way to distinguish service providers.
 *
 * The plugin fetches updated information from their xml service and checks if
 * there are any new events. These are printed to the channel and stored for
 * users wanting to check the last status (e.g if missed the anouncement).
 *
 * Supplies commands: !listpackages, !addpackage ID, !statuspackage ID
 *                    and !removepackage ID.
 *s
 */
public class PackageTracker extends silvertrout.Plugin {

    private final ArrayList<Package> packages = new ArrayList<Package>();
    private final ArrayList<PackageServiceProvider> serviceProviders = new ArrayList<PackageServiceProvider>();
    private final int PACKAGE_TTL = 14; // TTL in days
    private final PackageServiceProviderFactory packageServiceProviderFactory;

    public PackageTracker() {
        packageServiceProviderFactory = new PackageServiceProviderFactory();
        serviceProviders.add(packageServiceProviderFactory.getServiceProviderPosten());
        serviceProviders.add(packageServiceProviderFactory.getServiceProviderSchenker());
    }
    
    private PackageServiceProvider findServiceProvider(Package p) {
        for (PackageServiceProvider provider : serviceProviders) {
            if(provider.isServiceProvider(p.id))
                return provider;
        }
        return null;
    }

    public boolean exists(String id)
    {
        for(Package p: packages)
        {
            if(p.id.equals(id))
            {
                return true;
            }
        }
        return false;
    }

    public boolean add(String id, Channel channel, String receiverNickname) {

        if(exists(id))return false;

        PlaceholderServiceProvider placeholderServiceProvider = packageServiceProviderFactory.getPlaceholderServiceProvider();
        Package p = placeholderServiceProvider.new Package();

        p.id       = id;
        p.channel  = channel;
        p.receiverNickname = receiverNickname;
        p.lastDateTime = new DateTime(0);
        
        update(p);

        packages.add(p);

        return true;
    }

    public boolean remove(String id)
    {
        for(Package p: packages)
        {
            if(p.id.equals(id))
            {
                packages.remove(p);
                return true;
            }
        }
        return false;
    }

    public Package get(String id)
    {
        for(Package p: packages)
        {
            if(p.id.equals(id))
            {
                return p;
            }
        }
        return null;
    }

    private void update(Package p) {
        
        // Has this package's TTL expired?
        Duration timeSinceUpdate = new Duration(new DateTime(p.lastDateTime), null);

        if (p.lastDateTime.isAfter(new DateTime(0)) && timeSinceUpdate.isLongerThan(Duration.standardDays(PACKAGE_TTL))) {

            getNetwork().getConnection().sendPrivmsg(p.channel.getName(), " * " + "Package has not been updated for "
                    + PACKAGE_TTL + " days - removing from PackageTracker!");
            getNetwork().getConnection().sendPrivmsg(p.channel.getName(), p.toString());

            packages.remove(p);
            return;
        }
        
        // New packages and packages not yet registered at the service provider
        // have a null provider
        if(p.provider == null) {
            p.provider = findServiceProvider(p);
            if(p.provider == null)
                return;
        }

        ArrayList<PackageEvent> events = p.provider.fetch(p);

        if(events.size() > 0) {

            p.events.addAll(events);
            String chan = p.channel.getName();

            getNetwork().getConnection().sendPrivmsg(chan, p.toString());

            for(PackageEvent event: events) {
                getNetwork().getConnection().sendPrivmsg(chan, " * " + event);

                if(event.dateTime.isAfter(p.lastDateTime)) {
                    p.lastDateTime = event.dateTime;
                }
            }
        }

    }

    @Override
    public void onPrivmsg(User from, Channel to, String message) {

        String[] parts = message.split("\\s");
        String command = parts[0].toLowerCase();

        // List packages:
        if (parts.length == 1 && command.equals("!listpackages")) {
            if(packages.size() > 0) {
                for (int i = 0; i < packages.size(); i++) {
                    Package p = packages.get(i);
                    getNetwork().getConnection().sendPrivmsg(
                            to.getName(), " " + (i + 1) + ". " + p);
                }
            } else {
               getNetwork().getConnection().sendPrivmsg(
                        to.getName(), " * There are no packages");
            }
        // Add package:
        } else if (parts.length == 2 && command.equals("!addpackage")) {
            if (add(parts[1], to, from.getNickname())) {
                Package p = packages.get(packages.size() - 1);
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        "Added: " + p);
            } else {
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        "Failed to add package");
            }
        // Remove package:
        } else if (command.equals("!removepackage")) {
            if (remove(parts[1])) {
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        "Removed: Package (" + parts[1] + ")");
            } else {
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        "Failed to remove package");
            }
        // Status on package:
        } else if (command.equals("!statuspackage")) {
            Package p = get(parts[1]);
            if(p != null) {
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        p.toString());
                if(p.events.size() > 0) {
                    getNetwork().getConnection().sendPrivmsg(to.getName(),
                        " * " + p.events.get(p.events.size() -1));
                } else {
                    getNetwork().getConnection().sendPrivmsg(to.getName(),
                        " * No events for this package");
                }
            } else {
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        "Could not find package");
            }
        }
    }

    @Override
    public void onTick(int t) {
        //System.out.println("t = " + t + ", " + (t % (60 * 1)));
        if ((t % (60 * 5)) == 0) {
            //System.out.println("Trying to find updated pages...");
            for (Package p : packages) {
                update(p);
            }
        }
    }
}
