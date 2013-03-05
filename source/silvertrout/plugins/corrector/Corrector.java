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

package silvertrout.plugins.corrector;

import java.util.LinkedList;
import java.util.List;

import silvertrout.Channel;
import silvertrout.User;
import silvertrout.commons.ConnectHelper;

/**
 * 
 * @author reggna
 */
public class Corrector extends silvertrout.Plugin {

    // Max content length (in bytes) to grab to check for header
    private static final int maxContentLength = 4096;
    private static final String key = "";

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        // if(user.getNickname().equals("reggna")){
        List<String> c = getCorrect(message);
        for (String s : c)
            if (channel != null)
                channel.sendPrivmsg(s);
            else
                getNetwork().getConnection().sendPrivmsg(user.getNickname(), s);
        // }
    }

    /**
     * 
     * @param message
     * @return
     */
    public static List<String> getCorrect(String m) {
        System.out.println("Correcting " + m);
        LinkedList<String> list = new LinkedList<String>();
        String xml = ConnectHelper.Connect("http", "api.libris.kb.se",
                "/bibspell/spell?query=" + m + "&key=" + key, 80,
                maxContentLength, null, null);

        for (String s : xml.split("<term changed = 'true'>")) {
            System.out.println("XML" + s);
            if (s.startsWith("<"))
                continue;
            list.add(s.substring(0, s.indexOf("<")));
            System.out.println("Found " + s.substring(0, s.indexOf("<")));
        }
        return list;
    }
}
