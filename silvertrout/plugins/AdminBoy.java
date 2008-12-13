/*   _______ __ __                    _______                    __   
 *  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_ 
 *  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|
 *  |_______|__|__| \___/|_____|__|    |___|  |__| |_____|_____||____|
 * 
 *  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav Sothell
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
import silvertrout.Channel;
import silvertrout.Modes;

import java.util.Iterator;
import java.util.Map;

public class AdminBoy extends silvertrout.Plugin {

  // Password for AdminBoy
  private String password;
  
  @Override
  public void onPrivmsg(User user, String message) {
    System.out.println("AB: onPrivmsg(u, m) = (" + user + ", " + message + ")");
  
    String[] parts = message.split("\\s");
    if(parts.length > 1 && parts[0].equals(password)) {
      String cmd = parts[1].toLowerCase();
      
      // Channel commandos: (!op, !kick, !etc)
      if(parts.length > 3 && (cmd.equals("!kick") 
          || cmd.equals("!deop") || cmd.equals("!op") 
          || cmd.equals("!voice") || cmd.equals("!devoice") 
          || cmd.equals("!halfop") || cmd.equals("!dehalfop"))){
          
        parts = message.split("\\s", 4);
          
        if(getNetwork().existsChannel(parts[2])) {
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
          getNetwork().getConnection().join(parts[2]);
          user.sendPrivmsg("Jag mår bra när jag får vara i " + parts[2] + ".");
        } else if(cmd.equals("!part")) {
          getNetwork().getConnection().part(parts[2]);
          user.sendPrivmsg("Tråkigt att du inte vill ha mig kvar i " + parts[2] +".");
        } else if(cmd.equals("!loadplugin")) {
          getNetwork().loadPlugin(parts[2]);
          user.sendPrivmsg(parts[2] + " har laddats.");
        } else if(cmd.equals("!unloadplugin")) {
          getNetwork().unloadPlugin(parts[2]);
          user.sendPrivmsg(parts[2] +" har avaktiverats.");
        } else if(cmd.equals("!users")) {
            Channel chan = getNetwork().getChannel(parts[2]);
            user.sendPrivmsg(chan.getName() + " har "
                + chan.getUsers().size() + " användare");
            String  usrlst  = "";
            for(Map.Entry<User, Modes> ue: chan.getUsers().entrySet()) {
              usrlst += ue.getKey().getNickname() + "[" + ue.getValue().get() + "], ";
            }
            user.sendPrivmsg(usrlst);
          }
      
      // Unknown commands:
      } else {
        user.sendPrivmsg("Kommandot " + cmd + " kan inte hanteras av mig");
      }
    
    }
  }
  
    @Override
    public void onLoad(Map<String,String> settings){
        password = settings.get("password");
        if(password == null) password = "password";
    }
  
  private String getHelp() {
    return "Just nu finns följande kommandon tillgängliga: !join !part " +
        "!loadplugin !unloadplugin !channels !listplugins !users !op !deop " +
        "!voice !devoice !kick";
  }
  
  private String getHelp(String command){
    if(command.equals("!help") || command.equals("help"))
      return "!help [kommando]: Returnerar kommandolista, alternativt hjälptext för det givna kommando.";
    else if(command.equals("!part") || command.equals("part"))
      return "!part [#kanal]: Kommenderar jbt att lämna en bestämd kanal.";
    else if(command.equals("!join") || command.equals("join"))
      return "!part [#kanal]: Beordrar jbt att ansluta sig till en ny kanal.";
    else if(command.equals("!loadplugin") || command.equals("loadplugin"))
      return "!loadplugin [plugin]: Befaller jbt att ladda ett nytt plugin.";
    else if(command.equals("!unloadplugin") || command.equals("unloadplugin"))
      return "!unloadplugin [plugin]: Ger jpb i uppdrag att lossa en bestämd insticksmodul.";
    else if(command.equals("!channels") || command.equals("channels"))
      return "!channels: Listar de kanaler som jbt är aktiv på.";
    else if(command.equals("!listplugins") || command.equals("listplugins"))
      return "!listplugins: Listar de insticksmoduler som är laddade på nätverket.";
    else if (command.equals("!users") || command.equals("users"))
      return "!users [#kanal]: Listar användare (och deras lägen) i en kanal där jbt finns";
    else
      return "Känner nog inte riktigt till det där kommandot, prata med Tigge så fixar han det. :)";
  }


}
