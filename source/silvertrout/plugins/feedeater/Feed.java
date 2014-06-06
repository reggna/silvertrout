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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import silvertrout.Channel;

/**
 *
 */
public abstract class Feed {

    protected String        title = "";
    protected String        description;
    private   URL           url;
    private   Channel       channel;
    private   Set<FeedItem> items;


    public void setUrl(URL url) {
        this.url   = url;
        this.items = new HashSet<FeedItem>();
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public URL getUrl() {
        return url;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Collection<FeedItem> update() {
        // Get items and filter out old items
        Collection<FeedItem> newItems = getFeedItems();
        newItems.removeAll(items);

        // Add new items
        // TODO: remove no longer used feed items?
        items.addAll(newItems);

        return newItems;
    }

    protected Collection<FeedItem> getFeedItems() {
        return new ArrayList<FeedItem>();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Feed) {
            return this.url.equals(((Feed)obj).url);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (this.url != null ? this.url.hashCode() : 0);
    }
}
