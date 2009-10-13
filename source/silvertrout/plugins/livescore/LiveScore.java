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
package silvertrout.plugins.livescore;

import java.util.Random;
import java.util.Map;

import silvertrout.Channel;
import silvertrout.User;

/**
 *
 * @see silvertrout.Plugin
 * @see silvertrout.plugins
 */
public class LiveScore extends silvertrout.Plugin {


    public void onTick(int ticks) {
        if (ticks%60 == 0){
            ;
        }
    }
    
    @Override
    public void onPrivmsg(User user, Channel channel, String message) {

        if (channel != null) {


            if (message.equals("!glass")) {

                channel.sendPrivmsg("meddelande");

            }
        }
    }
}
