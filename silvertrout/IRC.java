package silvertrout;

import java.util.ArrayList;

public class IRC {

    public String network;
    public String server;
    public int port;
    public String nickname;
    public String fullname;
    public ArrayList<Network> networks;

    /**
     * A basic constructor
     *
     */
    public IRC() {
        networks = new ArrayList<Network>();
        connect(new Network("IT", "irc.chalmers.it", 6667));
    }

    /**
     * Add a Network to the Network List
     *
     * @param n - The Network to add to the List
     */
    public synchronized void connect(Network n) {
        networks.add(n);
    }

    /**
     *
     * @param args The command line arguments is not used in this program.
     */
    public static void main(String[] args) {
        System.out.println("IRC - goodboys version: 1");
        IRC bot = new IRC();
    }
}