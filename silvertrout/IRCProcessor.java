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
package silvertrout;

/**
 *
 * @author Gussoh
 */
public class IRCProcessor {

    private Network network;

    public IRCProcessor(Network network) {
        this.network = network;
    }

    /**
     * Process a message to decide if the message is a commandline or a
     * message to a channel.
     *
     * @param msg - The message to process.
     */
    void process(Message msg) {

        String cmd = msg.command;
        User user = network.getUser(msg.nickname);

        // Handle replies / error (Possible TODO: move error handeling):
        if (msg.isReply()) {
            switch (msg.reply) {
                case Message.RPL_TOPIC: {
                    network.onTopic(network.getChannel(msg.params.get(1)), msg.params.get(2));
                    break;
                }
                case Message.RPL_ENDOFMOTD:
                case Message.ERR_NOMOTD: {
                    System.out.println("!!! We are connected");
                    // Change state of network to connected:
                    // TODO: Does this really work?! at all?
                    //state = State.CONNECTED;
                    network.onConnect();
                    break;
                }
                case Message.RPL_NAMREPLY: {
                    String[] namlist = msg.params.get(3).split("\\s");

                    Channel channel = network.getChannel(msg.params.get(2));

                    if (channel == null) {
                        System.out.println("Message.RPL_NAMREPLY: Channel is null: " + msg.params.get(2));
                    } else {
                        network.onNames(channel, namlist);
                    }

                    break;
                }
            }
            return;

        // Handle commands:
        } else if (msg.isCommand()) {

            // Pre stuff
            // =================================================================
            String oldTopic = new String();
            String oldNickname = new String();
            // Catch old topic and change it to the new one
            if (cmd.equals("TOPIC")) {
                Channel channel = network.getChannel(msg.params.get(0));
                if (channel == null) {
                    System.out.println("TOPIC: Channel was null! " + msg.params.get(0));
                    return;
                }
                oldTopic = channel.getTopic();
                network.onTopic(channel, msg.params.get(1));
            // Add new user / channel
            } else if (cmd.equals("JOIN")) {

                if (user == network.getMyUser()) {
                    network.onJoin(msg.params.get(0));
                } else {
                    Channel channel = network.getChannel(msg.params.get(0));
                    if (channel == null) {
                        System.out.println("JOIN: Channel was null! " + msg.params.get(0) + ", user: " + msg.nickname);
                        return;
                    }

                    if (user == null) {
                        network.onJoin(msg.nickname, channel);
                    } else {
                        network.onJoin(user, channel);
                    }
                }
            // Catch old nickname
            } else if (cmd.equals("NICK")) {
                oldNickname = user.getNickname();
                network.onNick(user, msg.params.get(0));
            // TODO order, args, callback first or not?
            } else if (cmd.equals("MODE")) {
                // Channel
                if (network.existsChannel(msg.params.get(0))) {
                    Channel channel = network.getChannel(msg.params.get(0));
                    for (int i = 1; i < msg.params.size(); i++) {
                        if (msg.params.get(i).startsWith("+") || msg.params.get(i).startsWith("-")) {

                            String modes = msg.params.get(i);
                            int affects = modes.length() - 1;
                            char sign = modes.charAt(0);

                            for (int j = 0; j + i + 1 < msg.params.size() && j < modes.length() - 1; j++) {

                                char mode = modes.charAt(j + 1);
                                User modeReceiverUser = network.getUser(msg.params.get(i + j + 1));

                                /*System.out.println("trying to give "
                                + msg.params.get(i + j + 1) + " "
                                + sign + " " + mode
                                + " on channel "
                                + channel.getName() + "(" + i
                                + "," + j + ")");*/

                                if (channel != null) {
                                    if (sign == '+') {
                                        network.onGiveMode(user, channel, modeReceiverUser, mode);
                                    } else if (sign == '-') {
                                        network.onTakeMode(user, channel, modeReceiverUser, mode);
                                    }
                                } else {
                                    System.out.println("MODE: channel was null. A mode change on user level?");
                                }
                            }
                        }
                    }
                // User:
                } else {
                    //System.out.println(msg.params.get(0) + " is not a valid " + "channel?");
                    // TODO.. or not?
                }
            }

            // more commands
            if (cmd.equals("TOPIC")) {
                //p.onTopic(user, getChannel(msg.params.get(0)), oldTopic);
                // already done
            } else if (cmd.equals("PING")) {
                //p.onPing(msg.params.get(0));
                network.onPing(msg.params.get(0));
            } else if (cmd.equals("NOTICE")) {
                //p.onNotice(user, getChannel(msg.params.get(0)), msg.params.get(1));
                if (network.existsChannel(msg.params.get(0))) {
                    if(user == null) {
                        network.onNotice(msg.nickname, network.getChannel(msg.params.get(0)), msg.params.get(1));
                    } else {
                        network.onNotice(user, network.getChannel(msg.params.get(0)), msg.params.get(1));
                    }
                } else { // a private notice to us instead
                    if(user == null) {
                        network.onNotice(msg.nickname, msg.params.get(1));
                    } else {
                        network.onNotice(user, msg.params.get(1));
                    }
                    network.onNotice(user, msg.params.get(1));
                }
            } else if (cmd.equals("PRIVMSG")) {
                //p.onPrivmsg(user, getChannel(msg.params.get(0)), msg.params.get(1));
                if (network.existsChannel(msg.params.get(0))) {
                    // known user in channel
                    network.onPrivmsg(user, network.getChannel(msg.params.get(0)), msg.params.get(1));
                } else if (network.existsUser(msg.params.get(0))) {
                    // known user in private chat
                    network.onPrivmsg(network.getUser(msg.params.get(0)), msg.params.get(1));
                } else {
                    // unknown user in private chat
                    network.onPrivmsg(msg.params.get(0), msg.params.get(1));
                }
            } else if (cmd.equals("INVITE")) {
                //p.onInvite(user, msg.params.get(1));
                network.onInvite(user, msg.params.get(1));
            } else if (cmd.equals("KICK")) {
                //p.onKick(user, getChannel(msg.params.get(0)), getUser(msg.params.get(1)), msg.params.get(2));
                network.onKick(user, network.getChannel(msg.params.get(0)), network.getUser(msg.params.get(1)), msg.params.get(2));
            } else if (cmd.equals("JOIN")) {
                //p.onJoin(getUser(msg.nickname), getChannel(msg.params.get(0)));
                // Already caught
            } else if (cmd.equals("PART")) {
                if (user == network.getMyUser()) {
                    network.onPart(network.getChannel(msg.params.get(0)), (msg.params.size() > 1) ? msg.params.get(1) : null);
                } else {
                    //p.onPart(user, getChannel(msg.params.get(0)), msg.params.get(1));
                    network.onPart(user, network.getChannel(msg.params.get(0)), (msg.params.size() > 1) ? msg.params.get(1) : null);
                }
            } else if (cmd.equals("QUIT")) {
                //p.onQuit(user, msg.params.get(0));
                network.onQuit(user, msg.params.get(0));
            } else if (cmd.equals("NICK")) {
                //p.onNick(user, oldNickname);
                //already caught
            }


            // Post stuff
            // =========================================================
            // Part - We parted, or user parted from channel we are in
            if (cmd.equals("PART")) {
                /*if (user == getMyUser()) {
                //removeChannel(msg.params.get(0));
                // caught above
                } else {
                //getChannel(msg.params.get(0)).delUser(user);
                // caught above
                }*/
                // Quit - User quit, TODO: could this be us?
            } else if (cmd.equals("QUIT")) {
                /*for (Channel c : channels) {
                //c.delUser(user);
                }
                //users.remove(user.getNickname());
                //Already caught and done!
                 */
            }

        }
    }
}
