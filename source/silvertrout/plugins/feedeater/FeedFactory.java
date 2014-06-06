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

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

class FeedFactory {

    public static Feed getFeed(URL url) {
        try {
            // Fetch channel:
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(con.getInputStream());

            Element root = doc.getDocumentElement();
            String tag = root.getTagName();

            // RDF RSS 1.0 (TODO) or RSS 0.9/0.91/2.0
            if(tag.equals("rdf:RDF") || tag.equals("rss")) {
                return new RSSFeed(doc);
            // Atom feed
            } else if(tag.equals("feed")) {
                return new AtomFeed(doc);
            } else {
                throw new Exception("Could not determine feed type. Tag: " + tag);
            }

        } catch (Exception e) {
            System.out.println("Failed to add feed");
            e.printStackTrace();
            return null;
        }
    }

}
