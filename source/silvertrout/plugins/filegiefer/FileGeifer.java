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
package silvertrout.plugins.filegeifer;

import silvertrout.Channel;
import silvertrout.User;

import silvertrout.plugins.dccfilesender.*;

import java.io.*;

/**
 *
 **
 */
public class FileGeifer extends silvertrout.Plugin {

    @Override
    public void onPrivmsg(User from, Channel to, String message) {
        String dir = "/home/tigge/Photos/Pictures";

        if (message.startsWith("!list")) {
            getNetwork().getConnection().sendPrivmsg(from.getNickname(), "List of files in " + dir + "\n");

            File file = new File(dir);
            String[] fileList = file.list();

            for (int i = 0; i < fileList.length; i++) {
                getNetwork().getConnection().sendPrivmsg(from.getNickname(), " * " + fileList[i]);
            }

        } else if (message.startsWith("!gief")) {
            File file = new File(dir + "/" + message.substring(6));
            if (file.getParent().equals(dir)) {
                DCCFileSender send = new DCCFileSender(file, from, getNetwork());
                send.startSend();
            } else {
                System.out.println(file.getAbsoluteFile() + " is not in " + dir);
                System.out.println(file.getParent() + " != " + dir);
            }

        }

    }
}
