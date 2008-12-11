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

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;

import java.net.SocketException;
import java.net.Socket;
import java.net.UnknownHostException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Network class that handles connection to the network. Sending and
 * 
 * 
 */
public class Network implements Runnable {

    public enum State {

        CONNECTED, DISCONNECTED
    };
    private String name;
    private String host;
    private int port;
    private User me;
    private ArrayList<Channel> channels;
    /** nickname -> user object */
    private Map<String, User> users;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private LinkedList<String> outputQueue;
    private Timer timer;
    private int ticks;
    private ConcurrentHashMap<String, Plugin> plugins;
    private State state;
    private IRC irc;

    /**
     * Create and connect to a new Network,
     *
     * @param irc  - Reference to the top
     * @param name - The name of the Network
     * @param host - The server's ip
     * @param port - The port to connect to
     * @param me   - Contains settings for nickname, username, realname
     */
    public Network(IRC irc, String name, String host, int port, User me) {
        this.irc = irc;

        // Init variables:
        this.plugins = new ConcurrentHashMap<String, Plugin>();
        this.outputQueue = new LinkedList<String>();

        // Load plugins:
        for (Entry<String, Map<String, String>> plugin : getIrc().getSettings().getPluginsFor(name).entrySet()) {
            // settings = entry.getValue();
            loadPlugin(plugin.getKey());
        }

        // Ticks executes once every second:
        ticks = 0;
        timer = new Timer();
        TimerTask ticker = new TimerTask() {

            @Override
            public void run() {
                Network.this.tick();
            }
        };
        timer.schedule(ticker, 0, 1000);

        // Connect to server
        connect(name, host, port, me);
    }

    public void connect(String name, String host, int port, User me) {

        this.name = name;
        this.host = host;
        this.port = port;
        this.channels = new ArrayList<Channel>();
        this.users = new HashMap<String, User>();
        this.state = State.DISCONNECTED;
        this.me = me;

        this.users.put(this.me.getNickname(), this.me);

        // Connect to Server:
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host + ":" + port);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            e.printStackTrace();
            System.exit(1);
        }

        // Login (TODO: fix name and stuff)
        sendRaw("NICK " + this.me.getNickname());
        sendRaw("USER " + this.me.getUsername() + " 0 * :" + this.me.getRealname());

        // Start listening thread
        new Thread(this).start();
    }

    public void disconnect() {
        // Set state to disconnected:
        state = State.DISCONNECTED;
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: User Manager? {
    /**
     * Search for a user with a specified nickname
     *
     * @param nickname - The nickname of the user to search for
     * @return true iff the user with the specified nick exist
     */
    public boolean existsUser(String nickname) {
        return users.containsKey(nickname);
    }

    /**
     * Fetch a User with a specified nickname
     *
     * @param nickname - The nickname of the user to fetch
     * @return The user with the specified nickname
     */
    public User getUser(String nickname) {
        return users.get(nickname);
    }

    /**
     * Fetch a map of the users known on the Network
     *
     * @return A map containing all known users on the Network. With nickname as key and user object as value.
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Add a user to the Network
     *
     * @param nickname - The nickname of the user to add
     */
    public void addUser(String nickname) {
        users.put(nickname, new User(nickname));
    }

    /**
     * Return yourself
     *
     * @return the user representing yourself
     */
    public User getMyUser() {
        return me;
    }
    // } END User Manager

    // TODO: Channel Manager? {
    /**
     * Fetch a ArrayList containing all known channels on the Network
     *
     * @return all known channels on the Network
     */
    public ArrayList<Channel> getChannels() {
        return channels;
    }

    /**
     * Get a reference to the top level.
     * This is where the settings manage is available
     * @return the IRC object
     */
    public IRC getIrc() {
        return irc;
    }

    /**
     * Get the host the server tries to connect to
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the configured plugins for this network
     * @return the plugins
     */
    public ConcurrentHashMap<String, Plugin> getPlugins() {
        return plugins;
    }

    /**
     * Get the network name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the TCP port of the connection
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Search for a channel with the specified name
     *
     * @param name - The name of the channel to search for
     * @return ture iff the channel with the specified name is known on this Network
     */
    public boolean existsChannel(String name) {
        for (Channel c : channels) {
            if (c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a Channel with a specified name
     *
     * @param name - The name of the channel to search fo
     * @return The channel with the specified name, if the Channel does not exist returns null
     */
    public Channel getChannel(String name) {
        for (Channel c : channels) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Set the topic of a channel with a specified name
     *
     * @param channel - The name of the channel to change topic in
     * @param topic - The topic to set in the channel
     */
    /*public void setChannelTopic(String channel, String topic) {
    for (Channel c : channels) {
    if (c.getName().equals(channel)) {
    c.setTopic(topic);
    break;
    }
    }
    }*/
    /**
     * Add and join a channel in the NetWork. If the channel previously been joined, no action is taking place.
     *
     * @param channel - The name of the channel to join.
     */
    public void addChannel(String channel) {
        if (!existsChannel(channel)) {
            channels.add(new Channel(channel, this));
            System.out.println("Trying to join channel " + channel);
        }
    }

    /**
     * Remove and part a channel from the Network. If the user is not on the channel, no action is taking place.
     *
     * @param channel - The name of the channel to part from.
     */
    public void removeChannel(String channel) {
        for (Channel c : channels) {
            if (c.getName().equals(channel)) {
                channels.remove(c);
                System.out.println("Parting from channel " + channel);
                break;
            }
        }
    }
    // } END CHANNEL MANAGER (TODO ? )

    /**
     * Unload a plugin with the spicified name
     *
     * @param name - The name of the plugin to load
     */
    public synchronized boolean unloadPlugin(String name) {
        synchronized (plugins) {
            if (plugins.containsKey(name)) {
                plugins.get(name).onUnload();
                plugins.remove(name);

                // Clean up after us a bit (to allow reload)
                Runtime.getRuntime().runFinalization();
                while (true) {
                    long freeMemory = Runtime.getRuntime().freeMemory();
                    Runtime.getRuntime().gc();
                    if (freeMemory == Runtime.getRuntime().freeMemory()) {
                        break;
                    }
                }

                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Load plugin with the specified name from a file.
     *
     * @param  name  Name of the plugin to load
     */
    public synchronized boolean loadPlugin(String name) {
        if (!plugins.containsKey(name)) {
            synchronized (plugins) {
                try {
                    PluginClassLoader pcl = new PluginClassLoader();
                    Class<?> c = pcl.findClass("silvertrout.plugins." + name);
                    if (Plugin.class.isAssignableFrom(c)) {
                        Plugin p = (Plugin) c.newInstance();
                        p.setNetwork(this);
                        p.onLoad();
                        plugins.put(name, p);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     *Send a raw message to the network.
     *
     * @param message - the string to send to the Network
     */
    public synchronized void sendRaw(String message) {
        //System.out.println("Sent packet: " + message);
        outputQueue.add(message + "\r\n");
    }

    // Move a most of these to Channel och User: TODO TODO
    /**
     * Send a private message to either a user or a channel.
     *
     * @param to - The nick/name of the user/channel to send to
     * @param message - The string to send
     */
    public synchronized void sendPrivmsg(String to, String message) {
        sendRaw("PRIVMSG " + to + " :" + message);
    }

    /**
     * Send an action to either a user or a channel. Trigged on the command /me action
     *
     * @param to - The nick/name of the user/channel to send to
     * @param message - The action the user (you) are performing
     */
    public synchronized void sendAction(String to, String message) {
        sendPrivmsg(to, "ACTION " + message + "");
    }

    /**
     * Kick a user from a channel
     *
     * @param channel - The name of the Channel
     * @param who - The nick of the user to kick from the channel
     * @param message - The reason why the user is kicked
     */
    public synchronized void kick(String channel, String who, String message) {
        sendRaw("KICK " + channel + " " + who + " :" + message);
    }

    /**
     * Join a channel with the specified name
     *
     * @param channel - The name of the channel to join
     */
    public synchronized void join(String channel) {
        sendRaw("JOIN " + channel);
    }

    /**
     * Part from a channel with the specified name
     *
     * @param channel - The name of the channel to part from
     */
    public synchronized void part(String channel) {
        sendRaw("PART " + channel);
    }

    /**
     * Process a message to decide if the message is a commandline or a
     * message to a channel.
     *
     * @param msg - The message to process.
     */
    public synchronized void process(Message msg) {

        String cmd = msg.command;
        User usr = getUser(msg.nickname);

        // Handle replies / error (Possible TODO: move error handeling):
        if (msg.isReply()) {
            switch (msg.reply) {
                case Message.RPL_TOPIC: {
                    getChannel(msg.params.get(1)).setTopic(msg.params.get(2));
                    break;
                }
                case Message.RPL_ENDOFMOTD:
                case Message.ERR_NOMOTD: {
                    System.out.println("!!! We are connected");
                    // Change state of network to connected:
                    state = State.CONNECTED;
                    for (Plugin p : plugins.values()) {
                        p.onConnected();
                    }
                    break;
                }
                case Message.RPL_NAMREPLY: {
                    String[] namlist = msg.params.get(3).split("\\s");

                    for (int i = 0; i < namlist.length; i++) {
                        Channel channel = getChannel(msg.params.get(2));
                        if (channel == null) {
                            System.out.println("channel is null: " + msg.params.get(2) + " - " + namlist[i]);
                            continue;
                        }
                        String user = namlist[i];

                        if (user.startsWith("+") || user.startsWith("@") || user.startsWith("&") || user.startsWith("~")) {
                            if (!existsUser(user.substring(1))) {
                                addUser(user.substring(1));
                            }
                            if (user.startsWith("+")) {
                                channel.addUser(getUser(user.substring(1)), new Modes("v"));
                            } else if (user.startsWith("@") || user.startsWith("&") || user.startsWith("~")) {
                                channel.addUser(getUser(user.substring(1)), new Modes("o"));
                            }
                        } else {
                            if (!existsUser(user)) {
                                addUser(user);
                            }
                            channel.addUser(getUser(user), new Modes());
                        }
                    //System.out.println("*** Added user " + user + " to channel " + channel.getName() + ".");
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
                Channel channel = getChannel(msg.params.get(0));
                oldTopic = channel.getTopic();
                channel.setTopic(msg.params.get(1));
            // Add new user / channel
            } else if (cmd.equals("JOIN")) {
                if (usr == getMyUser()) {
                    addChannel(msg.params.get(0));
                } else {
                    if (usr == null) {
                        addUser(msg.nickname);
                    }
                    getChannel(msg.params.get(0)).addUser(getUser(msg.nickname), new Modes());
                }
            // Catch old nickname
            } else if (cmd.equals("NICK")) {
                oldNickname = usr.getNickname();
                usr.setNickname(msg.params.get(0));
            // TODO order, args, callback first or not?
            } else if (cmd.equals("MODE")) {
                // Channel
                if (existsChannel(msg.params.get(0))) {
                    Channel channel = getChannel(msg.params.get(0));
                    for (int i = 1; i < msg.params.size(); i++) {
                        if (msg.params.get(i).startsWith("+") || msg.params.get(i).startsWith("-")) {

                            String modes = msg.params.get(i);
                            int affects = modes.length() - 1;
                            char sign = modes.charAt(0);

                            for (int j = 0; j + i + 1 < msg.params.size() && j < modes.length() - 1; j++) {

                                char mode = modes.charAt(j + 1);
                                User user = getUser(msg.params.get(i + j + 1));

                                /*System.out.println("trying to give " 
                                + msg.params.get(i + j + 1) + " "
                                + sign + " " + mode
                                + " on channel "
                                + channel.getName() + "(" + i
                                + "," + j + ")");*/

                                if (sign == '+') {
                                    channel.getUsers().get(user).giveMode(mode);
                                } else if (sign == '-') {
                                    channel.getUsers().get(user).takeMode(mode);
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
            // Call plugin functions
            for (Plugin p : plugins.values()) {
                try {
                    if (cmd.equals("TOPIC")) {
                        p.onTopic(usr, getChannel(msg.params.get(0)), oldTopic);
                    } else if (cmd.equals("PING")) {
                        p.onPing(msg.params.get(0));
                    } else if (cmd.equals("NOTICE")) {
                        p.onNotice(usr, getChannel(msg.params.get(0)), msg.params.get(1));
                    } else if (cmd.equals("PRIVMSG")) {
                        p.onPrivmsg(usr, getChannel(msg.params.get(0)), msg.params.get(1));
                    } else if (cmd.equals("INVITE")) {
                        p.onInvite(usr, msg.params.get(1));
                    } else if (cmd.equals("KICK")) {
                        p.onKick(usr, getChannel(msg.params.get(0)),
                                getUser(msg.params.get(1)), msg.params.get(2));
                    } else if (cmd.equals("JOIN")) {
                        p.onJoin(getUser(msg.nickname), getChannel(msg.params.get(0)));
                    } else if (cmd.equals("PART")) {
                        if (msg.params.size() > 1) {
                            p.onPart(usr, getChannel(msg.params.get(0)), msg.params.get(1));
                        } else {
                            p.onPart(usr, getChannel(msg.params.get(0)), null);
                        }
                    } else if (cmd.equals("QUIT")) {
                        p.onQuit(usr, msg.params.get(0));
                    } else if (cmd.equals("NICK")) {
                        p.onNick(usr, oldNickname);
                    }
                } catch (Exception e) {
                    //System.out.println("Plugin crashed in " + cmd + " handler:");
                    e.getMessage();
                    e.printStackTrace();
                }
            }

            // Post stuff
            // =========================================================
            // Part - We parted, or user parted from channel we are in
            if (cmd.equals("PART")) {
                if (usr == getMyUser()) {
                    removeChannel(msg.params.get(0));
                } else {
                    getChannel(msg.params.get(0)).delUser(usr);
                }
            // Quit - User quit, TODO: could this be us?
            } else if (cmd.equals("QUIT")) {
                for (Channel c : channels) {
                    c.delUser(usr);
                }
                users.remove(usr.getNickname());
            }

        }
    }

    private synchronized void tick() {


        // Process output queue:
        if (!outputQueue.isEmpty() && out != null) {
            out.write(outputQueue.pop());
            out.flush();
        }

        // Call on tick handler in plugins
        for (Plugin p : plugins.values()) {
            try {
                p.onTick(ticks);
            } catch (Exception e) {
                System.out.println("Plugin crashed in onTick handler:");
                e.getMessage();
                e.printStackTrace();
            }
        }
        ticks++;
    }

    /**
     * SPRING!!!
     *
     *
     */
    @Override
    public void run() {
        try {

            String tmp;
            while ((tmp = in.readLine()) != null) {
                synchronized (this) {
                    this.process(new Message(tmp));
                }
            }
        //System.out.println("Disconnected okayly :) - We or the server ended the communication");

        } catch (SocketException e) {
            System.out.println("Disconnected by SocketException");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Disconnected by IOException");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Disconnected by other Exception");
            e.printStackTrace();
        }
        synchronized (this) {
            disconnect();
            // Clear channels and users:
            this.channels.clear();
            this.users.clear();
            // Tell all plugins:
            for (Plugin p : plugins.values()) {
                p.onDisconnected();
            }
        }
    }
}

