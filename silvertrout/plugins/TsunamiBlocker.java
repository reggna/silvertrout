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
package silvertrout.plugins;

import java.util.HashMap;

import silvertrout.Channel;
import silvertrout.User;

public class TsunamiBlocker extends silvertrout.Plugin {

    HashMap<User, Integer> scores;

    public TsunamiBlocker() {
        scores = new HashMap<User, Integer>();
    }

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        // Already scored:
        if (scores.containsKey(user)) {
            // Update old score:
            int score = scores.get(user) + 1;
            scores.put(user, score);

            // Excess flood
            if (score > 5) {
                channel.kick(user, "Don't spam in the channel please!");
                scores.put(user, 0);
            }

        // New user:
        } else {
            scores.put(user, 1);
        }
    }

    @Override
    public void onTick(int ticks) {
        // Decrease all scores:
        if (ticks % 7 == 0) {
            for (User user : scores.keySet()) {
                if (scores.get(user) > 0) {
                    scores.put(user, scores.get(user) - 1);
                }
            }
        }

    //System.out.println(scores);
    }
}
