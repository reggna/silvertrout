/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package silvertrout.plugins.livescore;

import java.util.ArrayList;
import silvertrout.Channel;

/**
 *
 * @author Hasse
 */
public class Follower {
    ArrayList<String> watchlist;
    Channel channel;
    String name;
    public Follower(String name, Channel channel, ArrayList<String> watchlist){
        this.name = name;
        this.channel = channel;
        this.watchlist = watchlist;
    }
    public ArrayList<String> getWatchList(){
        return watchlist;
    }
    public String getName(){
        return name;
    }
    public Channel getChannel(){
        return channel;
    }
    public void addToWatchlist(ArrayList<String> watchlist){
        for (String following : watchlist){
            this.watchlist.add(following);
        }
    }
    public void removeFromWatchlist(ArrayList<String> watchlist){
        for (String following : watchlist){
            this.watchlist.remove(following);
        }
    }
    public boolean equals (Object o){
        return this.name.equals(((Follower)o).name);
    }
}
