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
package silvertrout.plugins.duper;

import java.util.HashMap;
import java.util.Map;

import silvertrout.commons.ConnectHelper;
import silvertrout.Channel;
import silvertrout.User;

public class Duper extends silvertrout.Plugin {

    private final String channelName = "#spam";
    private final int maxAnswers = 5;

    private HashMap<String, String> watchlist = new HashMap<String, String>();

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        if(!channel.getName().equals(channelName)) return;
        if(message.startsWith("!search")){
            message = message.substring(8).replace(' ', '+');
            String[] s = getPage(message);
            for(int i = 63; i <= 63 + maxAnswers; i++){
                if(!s[i].startsWith("20")) break;
                channel.sendPrivmsg(getTime(s[i]) + " - " + getName(s[i]));
            }
        }else if(message.startsWith("!add")){
            message = message.substring(8).replace(' ', '+');
            String[] s = getPage(message);
            if(watchlist.containsKey(message)){
                channel.sendPrivmsg("Adding \"" + message + "\" to watchlist." );
            }
            watchlist.put(message, getTime(s[63]));

        }
    }

    private static String[] getPage(String searchString){
        return ConnectHelper.Connect("http", "pre.scnsrc.net",
                    "/index.php?s=" + searchString + "&cat=", 80, 65536, null,
                    null).split("\r\n|\r|\n");
    }

    private String getName(String s){
        String[] ss = s.split("</a>");
        return trim(ss[ss.length-1]).substring(1);
    }

    private static String getTime(String s){
        return s.substring(0, 19);
    }

    private static String trim(String s){
        String t = "";
        boolean add = true;
        for(char c: s.toCharArray()){
            if(c == '<') add = false;
            if(add) t+= "" + c;
            if(c == '>') add = true;
        }
        return t;
    }

    @Override
    public void onTick(int ticks) {
        if(ticks % (60*100) == 0)
            checkAll();
    }

    private void checkAll(){
        for(Map.Entry<String, String> e: watchlist.entrySet()){
            check(e.getKey(), e.getValue());
        }
    }

    private void check(String searchString, String time){
        String[] s = getPage(searchString);
        for(int i = 63; !getTime(s[i]).equals(time); i++){
            if(!s[i].startsWith("20")) break;
            getNetwork().getChannel(channelName).sendPrivmsg(getTime(s[i]) + " - " + getName(s[i]));
        }
    }

    @Override
    public void onConnected() {
        // Join channel:
        if(!getNetwork().isInChannel(channelName)) {
            getNetwork().getConnection().join(channelName);
        }
    }
}
