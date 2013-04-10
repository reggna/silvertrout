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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import silvertrout.Channel;
import silvertrout.User;

/**
 * Tracks packages from the Swedish postal service (Posten).
 * 
 * Keeps up to date with shipping information by using Posten's or Schenker's xml services. Posten's
 * service is documented at http://services3.posten.se/c/online_steg_steg with additional
 * information on other pages linked from there. Schenker's service is documented at http://www.
 * schenker.nu/servlet/se.ementor.econgero.servlet.presentation.Main?data
 * .node.id=26784&data.language.id=1 with information regarding different types of package id's at
 * http://www.privatpaket.se/servlet/se.ementor.econgero.servlet
 * .presentation.Main?data.node.id=25039&data.language.id=1
 * 
 * Tried to find information about how to tell different service providers apart, found the UPU S10
 * Standard http://pls.upu.int/document/2011/an/cep_c_4_gn_ep_4 -1/src/d008_ad00_an01_p00_r00.pdf
 * Looks like there is no easy way to distinguish service providers.
 * 
 * The plugin fetches updated information from their xml service and checks if there are any new
 * events. These are printed to the channel and stored for users wanting to check the last status
 * (e.g if missed the anouncement).
 * 
 * Supplies commands: !listpackages, !addpackage ID, !statuspackage ID and !removepackage ID.
 */
public class PackageTracker extends silvertrout.Plugin {

    static private class NullPackageProvider extends PackageProvider {
        static private class NullPackage extends Package {
            public NullPackage(String id) {
                super(id);
            }
        }

        @Override
        public boolean isServiceProvider(String id) {
            return true;
        }

        @Override
        public Package upgrade(Package p) {
            return null;
        }
    }

    private final List<Package> packages = new ArrayList<Package>();
    private final Map<String, Channel> channels = new HashMap<String, Channel>();

    @Override
    public void onLoad(Map<String, String> settings) {
        PackageProviderFactory.getInstance().addProvider(
                new PostenPackageProvider(settings.get("Posten.consumerID")));
        PackageProviderFactory.getInstance().addProvider(new SchenkerPackageProvider());

    }

    public boolean exists(String id) {
        for (Package p : packages) {
            if (p.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean add(String id, Channel channel, String receiverNickname) {
        if (exists(id))
            return false;

        Package p = new NullPackageProvider.NullPackage(id);
        p.receiverNickname = receiverNickname;

        packages.add(p);
        channels.put(id, channel);

        update();

        return true;
    }

    public boolean remove(String id) {
        for (Package p : packages) {
            if (p.getId().equals(id)) {
                packages.remove(p);
                channels.remove(id);
                return true;
            }
        }
        return false;
    }

    public Package get(String id) {
        for (Package p : packages) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private void update() {

        List<Package> packagesToRemove = new ArrayList<Package>();
        List<Package> packagesToAdd = new ArrayList<Package>();

        for (Package p : packages) {
            // Has this package's TTL expired?
            if (p.expired()) {
                getNetwork().getConnection().sendPrivmsg(
                        channels.get(p.getId()).getName(),
                        " * " + "Package has not been updated for " + Package.PACKAGE_TTL
                                + " days - removing from PackageTracker!");
                getNetwork().getConnection().sendPrivmsg(channels.get(p.getId()).getName(),
                        p.toString());
                packagesToRemove.add(p);
                continue;
            }

            // New packages and packages not yet registered at the service provider
            // have a null provider
            if (p instanceof NullPackageProvider.NullPackage) {
                Package np = PackageProviderFactory.getInstance().update(p);
                if (np != null) {
                    packagesToRemove.add(p);
                    packagesToAdd.add(np);
                    continue;
                }
            }
        }

        for (Package p : packagesToRemove) {
            packages.remove(p);
        }
        for (Package p : packagesToAdd) {
            packages.add(p);
        }

        // System.out.println("Trying to find updated pages...");
        for (Package p : packages) {
            updatePackage(p);
        }
    }

    private void updatePackage(Package p) {
        List<PackageEvent> events = p.update();

        if (events.size() > 0) {

            p.events.addAll(events);
            String chan = channels.get(p.getId()).getName();

            getNetwork().getConnection().sendPrivmsg(chan, p.toString());

            for (PackageEvent event : events) {
                getNetwork().getConnection().sendPrivmsg(chan, " * " + event);
            }
        }

    }

    @Override
    public void onPrivmsg(User from, Channel to, String message) {

        String[] parts = message.split("\\s");
        String command = parts[0].toLowerCase();

        // List packages:
        if (parts.length == 1 && command.equals("!listpackages")) {
            if (packages.size() > 0) {
                for (int i = 0; i < packages.size(); i++) {
                    Package p = packages.get(i);
                    getNetwork().getConnection()
                            .sendPrivmsg(to.getName(), " " + (i + 1) + ". " + p);
                }
            } else {
                getNetwork().getConnection().sendPrivmsg(to.getName(), " * There are no packages");
            }
        // Add package:
        } else if (parts.length == 2 && command.equals("!addpackage")) {
            if (add(parts[1], to, from.getNickname())) {
                Package p = packages.get(packages.size() - 1);
                // getNetwork().getConnection().sendPrivmsg(to.getName(), "Added: " + p);
            } else {
                getNetwork().getConnection().sendPrivmsg(to.getName(), "Failed to add package");
            }
        // Remove package:
        } else if (command.equals("!removepackage")) {
            if (remove(parts[1])) {
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        "Removed: Package (" + parts[1] + ")");
            } else {
                getNetwork().getConnection().sendPrivmsg(to.getName(), "Failed to remove package");
            }
        // Status on package:
        } else if (command.equals("!statuspackage")) {
            Package p = get(parts[1]);
            if (p != null) {
                getNetwork().getConnection().sendPrivmsg(to.getName(), p.toString());
                if (p.events.size() > 0) {
                    getNetwork().getConnection().sendPrivmsg(to.getName(),
                            " * " + p.events.get(p.events.size() - 1));
                } else {
                    getNetwork().getConnection().sendPrivmsg(to.getName(),
                            " * No events for this package");
                }
            } else {
                getNetwork().getConnection().sendPrivmsg(to.getName(), "Could not find package");
            }
        }
    }

    @Override
    public void onTick(int t) {
        if ((t % (60 * 5)) == 0) {
            update();
        }
    }
}
