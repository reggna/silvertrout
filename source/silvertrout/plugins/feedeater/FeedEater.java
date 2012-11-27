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
package silvertrout.plugins.feedeater;

import java.net.MalformedURLException;

// XML parser stuff
import java.util.logging.Level;
import java.util.logging.Logger;

// URL and URL connection
import java.net.URL;

// JBT internal
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import silvertrout.Channel;
import silvertrout.User;

// TODO: save feeds in file
// Format: name "desc" url lastguid, channel
// TODO: make more robust. Partly done.
// TODO: better channel management
// TODO: check if channel is already active.
// TODO: Channel list for each feed?
// TODO: move print of first item to onPrivmsg
// TODO: make it more configurable. Use const for spam limit, update time, etc
/**
 *
 **
 */
public class FeedEater extends silvertrout.Plugin {

    private final Set<Feed> feeds = new LinkedHashSet<Feed>();

    public static final int UPDATE_ITEM_LIMIT = 3;
    public static final int UPDATE_INTERVAL   = 60;

    /**
     *
     */
    public FeedEater() {
        
    }

    /**
     *
     * @param url
     * @param channel
     * @return
     */
    public Feed add(String urlString, Channel channel) {
        try {
            URL url = new URL(urlString);

            Feed feed = FeedFactory.getFeed(url);

            feed.setUrl(url);
            feed.setChannel(channel);
            
            feeds.add(feed);
            return feed;
        } catch (MalformedURLException ex) {
            Logger.getLogger(FeedEater.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param index
     * @return
     */
    public boolean remove(int index) {
        if (index > 0 && index <= feeds.size()) {
            feeds.remove(feeds.toArray()[index - 1]);
            return true;
        }
        return false;
    }

    private void print(Feed feed, FeedItem feedItem) {
        String title   = feedItem.getTitle();
        String content = feedItem.getContent();
        String link    = feedItem.getLink();

        getNetwork().getConnection().sendPrivmsg(feed.getChannel().getName(),
                "[" + feed.getTitle() + "] \u0002" + title + "\u000f: " + content
                + " - " + link);
    }

    private Collection<FeedItem> fetchItems(Feed feed) {
        return feed.update();
    }

    public void update() {
        //System.out.println("Trying to find updated pages...");
        for (Feed feed : feeds) {
            Collection<FeedItem> feedItems = fetchItems(feed);
            int itemnumber = 0;
            for (FeedItem feedItem : feedItems) {
                if ((feedItems.size() - itemnumber) > 1 && itemnumber >= UPDATE_ITEM_LIMIT) {
                    getNetwork().getConnection().sendPrivmsg(
                            feed.getChannel().getName(),
                            (feedItems.size() - itemnumber) 
                            + " more items are not printed to "
                            + "prevent spamming... Sorry!");
                    break;
                }
                print(feed, feedItem);
                itemnumber++;
            }
        }
    }

    @Override
    public void onPrivmsg(User from, Channel to, String message) {

        String[] parts = message.split("\\s");
        String command = parts[0].toLowerCase();

        // List feeds:
        if (parts.length == 1 && command.equals("!listfeeds")) {
            System.out.println("List feeds");
            Iterator<Feed> iterator = feeds.iterator();
            for(int i = 0; iterator.hasNext(); i++) {
                Feed feed = iterator.next();
                getNetwork().getConnection().sendPrivmsg(to.getName(), 
                        (i + 1) + ". " + feed.getTitle() + " ("
                        + feed.getUrl() + ")");
            }
        // Add feed:
        } else if (parts.length == 2 && command.equals("!addfeed")) {
            Feed feed = add(parts[1], to);
            if (feed != null) {
                getNetwork().getConnection().sendPrivmsg(to.getName(), 
                        "Added feed: " + feed.getTitle() + " - "
                        + feed.getDescription());
            } else {
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        "Failed to add feed");
            }
        } else if (command.equals("!removefeed")) {
            if (remove(Integer.parseInt(parts[1]))) {
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        "Removed feed");
            } else {
                getNetwork().getConnection().sendPrivmsg(to.getName(),
                        "Failed to remove feed");
            }

        } else if (command.equals("!updatefeeds")) {
            update();
        }

    }

    @Override
    public void onTick(int t) {
        //System.out.println("t = " + t + ", " + (t % (60 * 1)));
        if ((t % UPDATE_INTERVAL) == 0) {
            update();
        }
    }
}
