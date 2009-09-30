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


import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.List;

import silvertrout.commons.EscapeUtils;
import silvertrout.commons.ConnectHelper;
import silvertrout.Channel;
import silvertrout.User;

/**
 *
 **
 */
public class Translator extends silvertrout.Plugin {

    // Max content length (in bytes) to grab to check for translations
    private static final int maxContentLength = 16384;

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        if(message.startsWith("!t") && channel != null){
            message = message.substring(3);
            /* engelska till svenska */
            String page = ConnectHelper.Connect("http", "lexin.nada.kth.se", 
                    "/cgi-bin/sve-eng", 80, maxContentLength, 
                    "sprak=malsprak&uppslagsord="+ message);
            String s = getEng(page); 
            if(s != null) channel.sendPrivmsg(user.getNickname() +": "+ s);
            s = getSwe(page);
            if(s != null) channel.sendPrivmsg(user.getNickname() +": "+ s);
            
            /* svenska till engelska: */
            String page2 = ConnectHelper.Connect("http", "lexin.nada.kth.se", 
                    "/cgi-bin/sve-eng", 80, maxContentLength, 
                    "sprak=kallsprak&uppslagsord="+ message);
            s = getEng(page2);
            if(s != null) channel.sendPrivmsg(user.getNickname() +": "+ s);
            s = getSwe(page2);
            if(s != null) channel.sendPrivmsg(user.getNickname() +": "+ s);
        }
    }

    private static String getSwe(String page){
        return getString(page, "(?:<DT>Svenskt uppslagsord<DD>|<DT>Svensk översättning<DD>)((.*))");
    }
    private static String getEng(String page){
        return getString(page, "(?:<DT>Engelskt uppslagsord<DD>|<DT>Engelsk översättning<DD>)((.*))");
    }
    private static String getString(String page, String pattern){
        Pattern pt = Pattern.compile(pattern);
        Matcher mt = pt.matcher(page);

        String s = "";
        if(mt.find())
            s = mt.group(1);
        else return null;
        while(mt.find()) {
            s += ", " + mt.group(1);
        }
        s = EscapeUtils.stripHtml(s).replaceAll("\\W?(\\(|\\[|\\{).*?(\\)|\\]|\\})","");
        s = s.replaceAll("\\W?(adj|adv|förk|interj|konj|prep|pron|räkne|subst).", ""); 
        String re = s;
        if(s.indexOf(", ") > -1)
            re = s.substring(0, s.indexOf(","));
        for(String ss: s.split(", ")){
            if(!re.contains(ss)) re += ", " + ss;
        }
        return re;
    }
}

