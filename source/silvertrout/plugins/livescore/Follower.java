/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package silvertrout.plugins.livescore;

import java.util.ArrayList;
import java.util.HashSet;
import silvertrout.Channel;

/**
 *
 * @author Hasse
 */
public class Follower {
    //ArrayList<String> watchlist;
    HashSet<String> watchlist;
    Channel channel;
    String name;
    public Follower(String name, Channel channel, HashSet<String> watchlist){
        this.name = name;
        this.channel = channel;
        this.watchlist = watchlist;
    }
    public HashSet<String> getWatchList(){
        return watchlist;
    }
    public String getName(){
        return name;
    }
    public Channel getChannel(){
        return channel;
    }
    public void addToWatchlist(HashSet<String> watchlist){
        for (String following : watchlist){
            this.watchlist.add(following);
        }
    }
    public boolean removeFromWatchlist(HashSet<String> watchlist){
        boolean t = false;
        for (String following : watchlist){
            t = this.watchlist.remove(following);
        }
        return t;
    }
    public boolean equals (Object o){
        if (o instanceof Follower)
            return this.name.equals(((Follower)o).name);
        else if (o instanceof String)
            return this.name.equals((String)o);
        return false;
    }
}
