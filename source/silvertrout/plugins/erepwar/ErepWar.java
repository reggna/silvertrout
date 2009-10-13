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
package silvertrout.plugins.erepwar;

import silvertrout.commons.ConnectHelper;
import silvertrout.Channel;

/**
 * 
 * @author reggna
 */
public class ErepWar extends silvertrout.Plugin {


    private static final int port = 80;
    private static final String connection = "http";
    private static final String server = "www.erepublik.com";
    private static final String file = "/rss/allMilitaryEvents";
    private static final String channelName = "#erepublik";
    // Max content length (in bytes) to grab to check for header
    private static final int maxContentLength = 16384;
    private Channel channel = null;
    private int lastID = 0;

    @Override
    public void onTick(int ticks) {
        if(ticks % 60 == 0){
            if(channel == null) channel = getNetwork().getChannel(channelName);
            checkFeed();
        }
    }
    private void checkFeed(){
        String site = ConnectHelper.Connect(connection, server, file, port, maxContentLength);
        String[] entries = site.split("</entry>");
        
        entries[0] = entries[0].substring(entries[0].indexOf("<entry>"));
        int newID = Integer.parseInt(fetch(entries[0], "?cat=", "</id>"));
        for(String s : entries){
            try{
                String title = fetch(s, "<title ", "</title>").substring(12);
                int id = Integer.parseInt(fetch(s, "?cat=", "</id>"));
                if(id == lastID){
                    lastID = newID;
                    return;
                }else if(title.contains("Sweden"))
                    //System.out.println(id + "\t" + title);
                    channel.sendPrivmsg(title);
            }catch(Exception e){}
        }
        lastID = newID;
    }
    
    private String fetch(String s, String startTag, String endTag){
        return s.substring(s.indexOf(startTag)+ startTag.length(), s.indexOf(endTag));
    }

    @Override
    public void onConnected() {
        // Join channel:
        if(!getNetwork().isInChannel(channelName)) {
            getNetwork().getConnection().join(channelName);
        }
    }
}
