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

import silvertrout.commons.EscapeUtils;

/**
 *
 */
public class FeedItem {

    private String id;
    private String title;
    private String content;
    private String link;
    
    public FeedItem(String id, String title, String content, String link) {
      this.id      = id;
      this.title   = EscapeUtils.unescapeAndStripHtml(title);
      this.content = EscapeUtils.unescapeAndStripHtml(content);
      this.link    = link;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FeedItem) {
            return getId().equals(((FeedItem)obj).getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (this.id != null ? this.id.hashCode() : 0);
    }

}
