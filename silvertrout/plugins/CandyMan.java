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

import java.util.Random;
import silvertrout.Channel;
import silvertrout.User;

public class CandyMan extends silvertrout.Plugin {

    private Random r;

    public CandyMan() {
        r = new Random();
    }

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
System.out.println("GODIS!");
        // Only in channels:
        if (channel != null) {

            String[] icecreams = {"Daimstrut", "Magnum", "Top Hat", "Cornetto", "Solero",
                "Nogger", "88:an", "Tip Top"};
            String[] candy = {"Toblerone", "Daimpåse", "Twix", "påse med Bilar",
                "Kexchoklad", "stor påse med Chips"};
            String[] drugs = {"haschbrownie", "joint", "lina kokain",
                "spruta heroin", "lapp lsd"};

            String what = new String();

            if (message.equals("!glass")) {
                what = icecreams[r.nextInt(icecreams.length)];
            } else if (message.equals("!godis")) {
                what = candy[r.nextInt(candy.length)];
                
            } else if (message.equals("!knark")) {
                what = drugs[r.nextInt(drugs.length)];
            } else {
                return;
            }

            if (r.nextInt(10) > 7) {
                channel.sendAction("ger reggna en " + what);
            } else {
                channel.sendAction("ger " + user.getNickname() + " en " + what);
            }
        }
    }
}
