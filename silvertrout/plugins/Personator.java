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

public class Personator extends silvertrout.Plugin {

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        String[] parts = message.split("\\s", 4);

        if (parts.length == 4 && parts[0].equals("password")) {
            String command = parts[1].toLowerCase();
            if (command.equals("!say")) {
                getNetwork().getConnection().sendPrivmsg(parts[2], parts[3]);
            } else if (command.equals("!action")) {
                getNetwork().getConnection().sendAction(parts[2], parts[3]);
            }
        }
    }
}
