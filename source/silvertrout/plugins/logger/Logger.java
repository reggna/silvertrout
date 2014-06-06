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
package silvertrout.plugins.logger;

import silvertrout.commons.SystemTime;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;

import silvertrout.Channel;
import silvertrout.User;

/**
 *
 * @author (reggna)
 */
public class Logger extends silvertrout.Plugin {

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        if (channel != null) {
            writeToLog(getFile(channel.getName()), "<" + user.getNickname() + "> " + message);
        }else{
            writeToLog(getFile(user.getNickname()), "<" + user.getNickname() + "> " + message);
        }
    }

    @Override
    public void onPart(User user, Channel channel, String partMessage) {
        if (channel != null) {
            writeToLog(getFile(channel.getName()), "-!- " + user.getNickname() + " " +
                    user.toString() + " has left " + channel.getName() + " (" + partMessage + ")");
        }
    }

    @Override
    public void onJoin(User user, Channel channel) {
        if (channel != null) {
            writeToLog(getFile(channel.getName()), "-!- " + user.getNickname() + " " +
                    user.toString() + " has joined " + channel.getName());
        }
    }

    /**
     * Todo: May work..?
     */
    @Override
    public void onQuit(User user, String quitMessage) {
        for (Channel channel : getNetwork().getChannels()) {
            if (channel.getUsers().containsKey(user)) {
                writeToLog(getFile(channel.getName()), "-!- " + user.getNickname() + " " +
                        user.toString() + " has quit (" + quitMessage + ")");
            }
        }
    }

    @Override
    public void onKick(User user, Channel channel, User kicked, String kickReason) {
        if (channel != null) {
            writeToLog(getFile(channel.getName()), "-!- " + user.getNickname() + " " +
                    user.toString() + " has left " + channel.getName() + " (" +
                    kickReason + ")");
        }
    }

    @Override
    public void onTopic(User user, Channel channel, String oldTopic) {
        if (channel != null) {
            writeToLog(getFile(channel.getName()), "-!- " + user.getNickname() +
                    " changed the topic of " + channel.getName() + " to: " +
                    channel.getTopic());
        }
    }

    @Override
    public void onNick(User user, String oldNickname) {
        for (Channel channel : getNetwork().getChannels()) {
            if (channel.getUsers().containsKey(user)) {
                writeToLog(getFile(channel.getName()), "-!- " + oldNickname +
                        " is now known as " + user.getNickname());
            }
        }
    }

    @Override
    public void onSendmsg(User user, String message) {
        writeToLog(getFile(user.getNickname()), "<" + getNetwork().getMyUser().getNickname() + "> " + message);
    }

    /**
     *
     * @param s
     * @return
     */
    public File getFile(String s) {
        try {
            /* create the folder if the folder has been deleted */
            String dir = "silvertrout/plugins/Logger/" + getNetwork().getNetworkSettings().getName();
            (new File(dir)).mkdirs();

            String file = s + ".log";
            return new File(dir + "/" + file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param f
     * @param message
     * @return
     */
    public boolean writeToLog(File f, String message) {
        try {
            f.createNewFile();
            /* create the file and a PrintStream to add data at the end of the file*/
            PrintStream out = new PrintStream(new FileOutputStream(f, true), true, "UTF-8");

            /* print a logmessage to the file */
            out.println(SystemTime.getCurrentTime() + " " + message);
            //out.flush(); // not needed, autoflush is on.
            out.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
