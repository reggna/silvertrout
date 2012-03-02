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

package silvertrout.plugins.adminboy;

import silvertrout.User;
import silvertrout.Channel;
import silvertrout.Modes;

import java.util.Iterator;
import java.util.Map;

public class AdminBoy extends silvertrout.Plugin {

  // Password for AdminBoy
  private String password;

  @Override
  public void onPrivmsg(User user, String message) {
    String[] parts = message.split("\\s");
    if(parts.length > 1 && parts[0].equals(password)) {
      String cmd = parts[1].toLowerCase();

      // Channel commandos: (!op, !kick, etc)
      if(parts.length > 3 && (cmd.equals("!kick") 
          || cmd.equals("!deop") || cmd.equals("!op") 
          || cmd.equals("!voice") || cmd.equals("!devoice") 
          || cmd.equals("!halfop") || cmd.equals("!dehalfop"))){

        parts = message.split("\\s", 4);

        if(getNetwork().isInChannel(parts[2])) {
          Channel chan = getNetwork().getChannel(parts[2]);
          User    usr  = getNetwork().getUser(parts[3]);
          String  rest = ""; if(parts.length > 4)rest = parts[4];

          if(cmd.equals("!kick")) {
            chan.kick(usr, rest);
          } else if(cmd.equals("!op")) {
            chan.giveOp(usr);
          } else if(cmd.equals("!deop")) {
            chan.deOp(usr);
          } else if(cmd.equals("!voice")) {
            chan.giveVoice(usr);
          } else if(cmd.equals("!devoice")) {
            chan.deVoice(usr);
          } else if(cmd.equals("!halfop")) {
            chan.deHalfOp(usr);
          } else if(cmd.equals("!dehalfop")) {
            chan.giveHalfOp(usr);
          }
        }
      // Help commands:
      } else if(parts.length >= 2 && cmd.equals("!help")) {
        if(parts.length > 2) {
          user.sendPrivmsg(getHelp(parts[2]));
        } else {
          user.sendPrivmsg(getHelp());
        }
      // Single commands: (!listplugins, !channels, etc)
      } else if(parts.length == 2) {
        if(cmd.equals("!listplugins")) {
          int number = 1;
          for(String p: getNetwork().getPlugins().keySet()) {
            user.sendPrivmsg("#" + (number++) + " - " + p);
          }
        } else if(cmd.equals("!channels")) {
          user.sendPrivmsg("I am in " + getNetwork().getChannels().size() + " channels:");
          for(Channel c: getNetwork().getChannels()) {
            user.sendPrivmsg("* " + c.getName() + " (" + c.getTopic() + ")");
          }
        }

      // Network commands:
      } else if(parts.length > 2) {
        if(cmd.equals("!join")) {

            if(!getNetwork().isInChannel(parts[2])) {
                user.sendPrivmsg("Joined " + parts[2] + ".");
                getNetwork().getConnection().join(parts[2]);
            } else {
                user.sendPrivmsg("Unable to join, already in channel: " + parts[2] + ".");
            }
        } else if(cmd.equals("!part")) {
            if(getNetwork().isInChannel(parts[2])) {
                user.sendPrivmsg("Leaving " + parts[2] +".");
                getNetwork().getConnection().part(parts[2]);
            } else {
                user.sendPrivmsg("Unable to leave, not in channel: " + parts[2] +".");
            }

        } else if(cmd.equals("!loadplugin")) {
            if(getNetwork().loadPlugin(parts[2])) {
                user.sendPrivmsg("Loaded plugin: " + parts[2] + ".");
            } else {
                user.sendPrivmsg("Unable to load plugin: " + parts[2] + ".");
            }
        } else if(cmd.equals("!unloadplugin")) {
            if(getNetwork().unloadPlugin(parts[2])) {
                user.sendPrivmsg("Unloaded plugin: " + parts[2] +".");
            } else {
                user.sendPrivmsg("Unable to unload plugin: " + parts[2] + ".");
            }
        } else if(cmd.equals("!users")) {
            Channel chan = getNetwork().getChannel(parts[2]);
            user.sendPrivmsg(chan.getName() + " has "
                + chan.getUsers().size() + " users:");
            String  usrlst  = "";
            for(Map.Entry<User, Modes> ue: chan.getUsers().entrySet()) {
              usrlst += ue.getKey().getNickname() + "[" + ue.getValue().get() + "], ";
            }
            user.sendPrivmsg(usrlst);
          }

      // Unknown commands:
      } else {
        user.sendPrivmsg("Unsupported command: " + cmd + ".");
      }

    }
  }

    @Override
    public void onLoad(Map<String,String> settings){
        password = settings.get("password");
        if(password == null) password = "password";
    }

  private String getHelp() {
    return "The following commands are currently supported: " +
        "!loadplugin !unloadplugin !channels !listplugins !users !op !deop " +
        "!voice !devoice !kick";
  }

  private String getHelp(String command){
    if(command.equals("!help") || command.equals("help"))
      return "!help [commmand]: Returns information about the given command.";
    else if(command.equals("!part") || command.equals("part"))
      return "!part [#channel]: Make the bot leave a given channel.";
    else if(command.equals("!join") || command.equals("join"))
      return "!join [#channel]: Make the bot join a given channel.";
    else if(command.equals("!loadplugin") || command.equals("loadplugin"))
      return "!loadplugin [plugin]: Dynamically load a plugin from the given class file.";
    else if(command.equals("!unloadplugin") || command.equals("unloadplugin"))
      return "!unloadplugin [plugin]: Unload a given plugin.";
    else if(command.equals("!channels") || command.equals("channels"))
      return "!channels: Returns the current channel list.";
    else if(command.equals("!listplugins") || command.equals("listplugins"))
      return "!listplugins: Returns a list of the active plugins.";
    else if (command.equals("!users") || command.equals("users"))
      return "!users [#channel]: Returns a user list from the given channel " +
             "(note that this only works if the bot in currently in the channel)";
    else
      return "Unrecognized command, use !help with no arguments for a list of available commands.";
  }


}
