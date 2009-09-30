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

/**
 *
 **
 */
public class KeepAlive extends silvertrout.Plugin {

    @Override
    public void onDisconnected() {
        // no such feature
        //getNetwork().connect();
    }

    @Override
    public void onPing(String id) {
        System.out.println("KA: onPing(id) = (" + id + ")");
        getNetwork().getConnection().sendRaw("PONG " + id);
    }

    /**
     *
     * @param nick
     * @param channelName
     */
    @Override
    public void onInvite(User nick, String channelName) {
        System.out.println("KA: onInvite(n, c) = (" + nick + ", " + channelName + ")");
        getNetwork().getConnection().join(channelName);
    }
}
