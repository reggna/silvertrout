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

import java.util.Random;
import java.util.Map;

import silvertrout.Channel;
import silvertrout.User;

/**
 * CandyMan - Example plugin for Silvertrout IRC bot framework.
 * <p>
 * This is a rather useless plugin but it demonstrates some vital parts of the
 * framework so its at least a good example on how to do things. The plugin is
 * still a work in progress.
 * <p>
 * To create a plugin you create a class that extends {@link silvertrout.Plugin} 
 * and override (with @Override) the on-methods you want to implement. There are
 * many methods like this. A few examples are 
 * {@link silvertrout.Plugin#onTick onTick}, 
 * {@link silvertrout.Plugin#onPrivmsg onPrivmsg},
 * {@link silvertrout.Plugin#onGiveMode onGiveMode},
 * {@link silvertrout.Plugin#onLoad onLoad} and
 * {@link silvertrout.Plugin#onQuit onQuit}.
 * Make sure you put your plugin in the {@link silvertrout.plugins} package too.
 * <p>
 * If you plan to do computation or run external programs or something that may
 * take more then a 10th of second or so you may want to use threads so your 
 * plugin do not disrupt what the bot is otherwise doing.
 *
 * @see silvertrout.Plugin
 * @see silvertrout.plugins
 */
public class CandyMan extends silvertrout.Plugin {

    private Random r;

    /**
     * This function gets called when loading the plugin. You could also use the
     * constructor but if you instead use the onLoad function you get your 
     * settings and you can be sure that the plugin is connected to a network
     * - fetch with {@link #getNetwork()}.
     * <p>
     * You can of course combine an onLoad function with a constructor. Just 
     * make sure you know what is not initialized when using the constructor.
     * <p>
     * <b>Documentation from Plugin:</b>
     * {@inheritDoc}
     *
     * @param  settings  A map with String String pairs containing settings. 
     *                   This is read from the configuration file config.xml.
     */
    @Override
    public void onLoad(Map<String, String> settings) {
        r = new Random();
    }
    
    /**
     * This functions get called every time someone write something in a channel
     * or a private chat. 
     * <p>
     * <b>Documentation from Plugin:</b>
     * {@inheritDoc}
     */
    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        // Only in channels - as an alternative here we could instead overload
        // the onPrivmsg(User user, String message) that gets called when a user
        // contacts you directly with a private message.
        // 
        // As a default that function calls this function but sets the channel
        // parameter to null.
        if (channel != null) {

            // Our icecream list
            String[] icecreams = {"Daimstrut", "Magnum", "Top Hat", "Cornetto", 
                    "Solero", "Nogger", "88:an", "Tip Top"};
            // Our candy list
            String[] candy = {"Toblerone", "Daimpåse", "Twix", "påse med Bilar",
                    "Kexchoklad", "stor påse med Chips"};

            String what = new String();

            if (message.equals("!glass")) {
                what = icecreams[r.nextInt(icecreams.length)];
            } else if (message.equals("!godis")) {
                what = candy[r.nextInt(candy.length)];
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
