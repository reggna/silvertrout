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
import silvertrout.commons.CommandLine;

import java.util.Map;

public class AdminBoy extends silvertrout.Plugin {

    /* Password for AdminBoy */
    private String password = "password";

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        try {
            CommandLine c = new CommandLine(message);
            /* check password */
            if (!c.getParam("p").equals(password)) {
                return;
            }


            /* !listplugins */
            if (c.getCommand().equals("listplugins")) {
                int number = 1;
                for (String p : getNetwork().plugins.keySet()) {
                    getNetwork().sendPrivmsg(user.getNickname(), "#" + (number++) + " - " + p);
                }
                return;
            }


            /* !channels -p password */
            if (c.getCommand().equals("channels")) {
                getNetwork().sendPrivmsg(user.getNickname(), "I am in " + getNetwork().getChannels().size() + " channels:");
                for (Channel ca : getNetwork().getChannels()) {
                    getNetwork().sendPrivmsg(user.getNickname(), "* " + ca.getName() + " (" + ca.getTopic() + ")");
                }
                return;
            }


            /* !loadplugin -p password -pl pluginname*/
            if (c.getCommand().equals("loadplugin")) {
                String p = c.getParam("pl");
                if (getNetwork().loadPlugin(p)) {
                    getNetwork().sendPrivmsg(user.getNickname(), p + " loaded.");
                } else {
                    getNetwork().sendPrivmsg(user.getNickname(), "Unable to load " + p + ".");
                }
                return;
            /* !unloadplugin -p password -pl pluginname */
            } else if (c.getCommand().equals("unloadplugin")) {
                String p = c.getParam("pl");
                if (getNetwork().unloadPlugin(p)) {
                    getNetwork().sendPrivmsg(user.getNickname(), p + " unloaded.");
                } else {
                    getNetwork().sendPrivmsg(user.getNickname(), "Unable to undload " + p + ".");
                }
                return;
            /* !reloadplugin -p password -pl pluginname */
            }


            /* !join -p password -c #channel */
            if (c.getCommand().equals("join")) {
                String ch = c.getParam("c");
                getNetwork().join(ch);
                getNetwork().sendPrivmsg(user.getNickname(), "Jag mår bra när jag får vara i " + ch + ".");
                return;
            /* !part -p password -c #channel */
            } else if (c.getCommand().equals("part")) {
                String ch = c.getParam("c");
                getNetwork().part(ch);
                getNetwork().sendPrivmsg(user.getNickname(), "Tråkigt att du inte vill ha mig kvar i " + ch + ".");
                return;
            /* !users -p password -c #channel */
            } else if (c.getCommand().equals("users")) {
                Channel chan = getNetwork().getChannel(c.getParam("c"));
                getNetwork().sendPrivmsg(user.getNickname(), chan.getName() + " har " + channel.getUsers().size() + " användare");
                String usrlst = "";
                for (Map.Entry<User, Modes> ue : chan.getUsers().entrySet()) {
                    usrlst += ue.getKey().getNickname() + "[" + ue.getValue().get() + "], ";
                }
                getNetwork().sendPrivmsg(user.getNickname(), usrlst);
                return;
            }


            /* !op -p password -c #channel -u username */
            if (c.getCommand().equals("op")) {
                Channel chan = getNetwork().getChannel(c.getParam("c"));
                User usr = getNetwork().getUser(c.getParam("u"));
                chan.giveOp(usr);
                return;
            /* !deop -p password -c #channel -u username */
            } else if (c.getCommand().equals("deop")) {
                Channel chan = getNetwork().getChannel(c.getParam("c"));
                User usr = getNetwork().getUser(c.getParam("u"));
                chan.deOp(usr);
                return;
            /* !voice -p password -c #channel -u username */
            } else if (c.getCommand().equals("voice")) {
                Channel chan = getNetwork().getChannel(c.getParam("c"));
                User usr = getNetwork().getUser(c.getParam("u"));
                chan.giveVoice(usr);
                return;
            /* !devoice -p password -c #channel -u username */
            } else if (c.getCommand().equals("devoice")) {
                Channel chan = getNetwork().getChannel(c.getParam("c"));
                User usr = getNetwork().getUser(c.getParam("u"));
                chan.deVoice(usr);
                return;
            /* !halfop -p password -c #channel -u username */
            } else if (c.getCommand().equals("halfop")) {
                Channel chan = getNetwork().getChannel(c.getParam("c"));
                User usr = getNetwork().getUser(c.getParam("u"));
                chan.giveHalfOp(usr);
                return;
            /* !dehalfop -p password -c #channel -u username */
            } else if (c.getCommand().equals("dehalfop")) {
                Channel chan = getNetwork().getChannel(c.getParam("c"));
                User usr = getNetwork().getUser(c.getParam("u"));
                chan.deHalfOp(usr);
                return;
            }


            /* !help -p password [-c command] */
            if (c.getCommand().equals("help")) {
                if (c.keyExist("c")) {
                    getNetwork().sendPrivmsg(user.getNickname(), getHelp(c.getParam("c")));
                } else {
                    getNetwork().sendPrivmsg(user.getNickname(), getHelp());
                }
                return;
            }

            /* no suitable command has been found */
            getNetwork().sendPrivmsg(user.getNickname(), "Kommandot kan inte hanteras av mig");
        } catch (Exception e) {
        }


    }

    private String getHelp() {
        return "Just nu finns följande kommandon tillgängliga: !join !part " +
                "!loadplugin !unloadplugin !channels !listplugins !users !op !deop " +
                "!voice !devoice !kick";
    }

    private String getHelp(String command) {
        if (command.equals("!help") || command.equals("help")) {
            return "!help [kommando]: Returnerar kommandolista, alternativt hjälptext för det givna kommando.";
        } else if (command.equals("!part") || command.equals("part")) {
            return "!part -c #channel: Kommenderar jbt att lämna en bestämd kanal.";
        } else if (command.equals("!join") || command.equals("join")) {
            return "!part -c #channel: Beordrar jbt att ansluta sig till en ny kanal.";
        } else if (command.equals("!loadplugin") || command.equals("loadplugin")) {
            return "!loadplugin -pl plugin: Befaller jbt att ladda ett nytt plugin.";
        } else if (command.equals("!unloadplugin") || command.equals("unloadplugin")) {
            return "!unloadplugin -pl plugin: Ger jpb i uppdrag att lossa en bestämd insticksmodul.";
        } else if (command.equals("!channels") || command.equals("channels")) {
            return "!channels: Listar de kanaler som jbt är aktiv på.";
        } else if (command.equals("!listplugins") || command.equals("listplugins")) {
            return "!listplugins: Listar de insticksmoduler som är laddade på nätverket.";
        } else if (command.equals("!users") || command.equals("users")) {
            return "!users -c #channel: Listar användare (och deras lägen) i en kanal där jbt finns";
        } else {
            return "Känner nog inte riktigt till det där kommandot, prata med Tigge så fixar han det. :)";
        }
    }
}
