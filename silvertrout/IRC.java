/*   _______ __ __                    _______                    __   
 *  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_ 
 *  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|
 *  |_______|__|__| \___/|_____|__|    |___|  |__| |_____|_____||____|
 * 
 *  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav Sothell
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
package silvertrout;

import java.util.ArrayList;
import silvertrout.settings.NetworkSettings;
import silvertrout.settings.Settings;
import silvertrout.settings.Settings.ConfigurationParseException;

public class IRC {

    public ArrayList<Network> networks;
    public Settings settings;

    /**
     * A basic constructor
     *
     */
    public IRC() {
        try {
            settings = new Settings();
        } catch (ConfigurationParseException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        networks = new ArrayList<Network>();
        for (NetworkSettings networkSettings : settings.getNetworks()) {
            User me = new User(networkSettings.getNickname(), null, null, networkSettings.getUsername(), networkSettings.getRealname(), false);
            connect(new Network(this, networkSettings.getName(), networkSettings.getHost(), networkSettings.getPort(), me));
        }
    }

    /**
     * Add a Network to the Network List
     *
     * @param n - The Network to add to the List
     */
    public synchronized void connect(Network n) {
        networks.add(n);
    }

    public Settings getSettings() {
        return settings;
    }

    public ArrayList<Network> getNetworks() {
        return networks;
    }

    /**
     *
     * @param args The command line arguments is not used in this program.
     */
    public static void main(String[] args) {
        System.out.println("SilverTrout - Version unkown");
        IRC bot = new IRC();
    }
}
