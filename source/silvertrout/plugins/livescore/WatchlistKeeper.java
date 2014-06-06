package silvertrout.plugins.livescore;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashSet;
import silvertrout.Channel;

/**
 *
 * @author Hasse
 */
public class WatchlistKeeper {

    HashSet<String> watchlist;
    final String WATCHLISTFILE = "watchlist.txt";

    public ArrayList<Follower> getWatchlist(Channel channel) {

        ArrayList<Follower> followers = new ArrayList<Follower>();
        HashSet<String> watchlist = new HashSet<String>();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(WATCHLISTFILE));
            try {
                String line = null;
                line = input.readLine();
                String name = "";
                if (line == null || line.length()<1){
                    return new ArrayList<Follower>();
                }
                name = line.split("=")[1];
                while ((line = input.readLine()) != null) {
                    if (line.startsWith("USER")) {
                        followers.add(new Follower(name, channel, watchlist));
                        name = line.split("=")[1];
                        watchlist = new HashSet<String>();
                    } else {
                        watchlist.add(line);
                    }
                }
                if (name.length() > 0){
                    followers.add(new Follower(name, channel, watchlist));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return followers;
    }

    public void saveWatchlist(ArrayList<Follower> followers) {
        HashSet<String> watchlist;
        String toFile = "";


        try {
            // Create file
            FileWriter fstream = new FileWriter(WATCHLISTFILE);
            BufferedWriter out = new BufferedWriter(fstream);
            for (Follower f : followers) {
                toFile = "USER=" + f.name;
                out.write(toFile);
                out.newLine();
                watchlist = f.watchlist;
                for (String following : watchlist) {
                    toFile = following;
                    out.write(toFile);
                    out.newLine();
                }
            }
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void hoho() {
        ArrayList<Follower> followers = new ArrayList<Follower>();
        HashSet<String> watchlist = new HashSet<String>();
        watchlist.add("team=Portugal");
        watchlist.add("team=Portugal");
        watchlist.add("country=Sweden");
        followers.add(new Follower("Hasse", null, watchlist));
        watchlist = new HashSet<String>();
        watchlist.add("team=Brazil");
        watchlist.add("team=Honduras");
        watchlist.add("country=IFK");
        followers.add(new Follower("Patric", null, watchlist));
        WatchlistKeeper wk = new WatchlistKeeper();
        wk.saveWatchlist(followers);
        ArrayList<Follower> fo = wk.getWatchlist(null);
        System.out.println("TJAAAA!");
        for (Follower f : fo) {
            System.out.println(f.name);
            HashSet<String> list = f.watchlist;
            for (String following : list) {
                System.out.println(following);
            }
        }
    }
}

