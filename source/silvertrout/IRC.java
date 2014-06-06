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
package silvertrout;

import java.io.IOException;
import java.util.ArrayList;
import silvertrout.settings.NetworkSettings;
import silvertrout.settings.Settings;
import silvertrout.settings.Settings.ConfigurationParseException;

/**
 *
 *
 */
public class IRC {

    /**
     *
     */
    public ArrayList<Network> networks;
    /**
     *
     */
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
            try {
                User me = new User(networkSettings.getNickname(), null, null,
                        networkSettings.getUsername(),
                        networkSettings.getRealname(), false);
                connect(new Network(this, networkSettings, me));
            } catch (IOException e) {
                System.err.println("Could not connect to network "
                        + networkSettings.getName() + ": " + e.getMessage());
            }
        }
        System.out.println("Done creating networks");
    }

    /**
     * Add a Network to the Network List
     *
     * @param n - The Network to add to the List
     */
    public synchronized void connect(Network n) {
        networks.add(n);
    }

    /**
     *
     * @return
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     *
     * @return
     */
    public ArrayList<Network> getNetworks() {
        return networks;
    }

    /**
     *
     * @param args The command line arguments is not used in this program.
     */
    public static void main(String[] args) {

        System.out.println(
  "   _______ __ __                    _______                    __   \n"
+ "  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_ \n"
+ "  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|\n"
+ "  |_______|__|__| \\___/|_____|__|    |___|  |__| |_____|_____||____|\n"
+ "  \n"
+ "  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav Sohtell\n"
+ "  \n"
+ "  This program is free software: you can redistribute it and/or modify\n"
+ "  it under the terms of the GNU General Public License as published by\n"
+ "  the Free Software Foundation, either version 3 of the License, or\n"
+ "  (at your option) any later version.\n"
+ "  \n"
+ "  This program is distributed in the hope that it will be useful,\n"
+ "  but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
+ "  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
+ "  GNU General Public License for more details.\n");

        try {
            IRC bot = new IRC();
            System.out.println("Done IRC.constructor");
        } catch(Exception e) {
            System.out.println("An unrecoverable error has occured. Please "
                + "file a bug report at "
                + "http://code.google.com/p/silvertrout/issues/entry and"
                + "report it. Be sure to include the following text:\n\n");
            e.printStackTrace();
        }
    }
}
