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
package silvertrout.plugins;

import silvertrout.User;
import silvertrout.Channel;


/**
 *
 **
 */
public class Reloader extends silvertrout.Plugin {

    // Password for AdminBoy
    private String password = "password";

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {

        String[] parts = message.split("\\s");

        if (parts.length > 1 && parts[0].equals(password)) {
            String cmd = parts[1].toLowerCase();
            if (parts.length > 2) {
                if (cmd.equals("!reloadplugin")) {
                    getNetwork().unloadPlugin(parts[2]);
                    getNetwork().getConnection().sendPrivmsg(user.getNickname(), parts[2] + " har avaktiverats.");
                    getNetwork().loadPlugin(parts[2]);
                    getNetwork().getConnection().sendPrivmsg(user.getNickname(), parts[2] + " har laddats.");
                }
            }

        }
    }
}
