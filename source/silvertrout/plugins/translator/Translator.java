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
package silvertrout.plugins.translator;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import java.util.Map;
import java.util.HashMap;

import silvertrout.Channel;
import silvertrout.User;

/**
 *
 **
 */
public class Translator extends silvertrout.Plugin {
    private static final int maxContentLength = 4096;
    // Max content length (in bytes) to grab to check for translations
    //private static final int maxContentLength = 16384;
    Map<String, Language[]> users = new HashMap<String, Language[]>();
    @Override
    public void onLoad(Map<String, String> settings){
        Translate.setHttpReferrer("http://code.google.com/p/silvertrout/");
    }

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        if(message.startsWith("!clearlang")) users.clear();
        else if(message.startsWith("!setlang")){
            String[] langString;
            try{
                langString = message.substring(11).split("-");
            }catch(Exception e){
                users.remove(user.getNickname());
                return;
            }
            if(langString.length < 2){
                 users.remove(user.getNickname());
                 return;
            }
            Language[] lang = { Language.fromString(langString[0]),
                                Language.fromString(langString[1]) };
            if(lang[0] == null || lang[1] == null)
                users.remove(user.getNickname());
            else users.put(user.getNickname(), lang);
        }else if(message.startsWith("!listlang")){
            String m = "";
            for (Language l : Language.values())
                m+= l.toString() + " ";
            channel.sendAction(m);
        }else{
            if(!users.containsKey(user.getNickname())) return;
            try{
                channel.sendPrivmsg(Translate.execute(message, users.get(user.getNickname())[0], users.get(user.getNickname())[1]));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}