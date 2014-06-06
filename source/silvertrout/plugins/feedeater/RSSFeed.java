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
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import silvertrout.commons.EscapeUtils;

class RSSFeed extends Feed {

    public RSSFeed(Document doc) {

        NodeList nodes = doc.getElementsByTagName("channel");

        if (nodes.getLength() == 1) {

            Node chan = nodes.item(0);
            NodeList elements = chan.getChildNodes();

            // Loop through feed items
            for (int i = 0; i < elements.getLength(); i++) {
                Node element = elements.item(i);
                String nodeName = element.getNodeName();
                String nodeContent = element.getTextContent();

                if (nodeName.equals("title")) {
                    title       = cleanData(nodeContent).split(":|-|\\||\\(|\\[", 2)[0].trim();
                } else if (nodeName.equals("description")) {
                    description = cleanData(nodeContent);
                }
            }

        } else {
            System.out.println("Failed to add RSS feed");
            System.out.println("Nodes length is: " + nodes.getLength());
        }

    }

    private String cleanData(String data) {
        data = StringEscapeUtils.unescapeHtml4(data);
        data = EscapeUtils.normalizeSpaces(data);
        return data;
    }

    @Override
    protected Collection<FeedItem> getFeedItems() {

        // Create feed items list
        ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();

        try {
            // Fetch feed items
            HttpURLConnection con = (HttpURLConnection) getUrl().openConnection();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(con.getInputStream());

            NodeList nodes = doc.getElementsByTagName("item");

            for (int i = 0; i < nodes.getLength(); i++) {
                NodeList childNodes = nodes.item(i).getChildNodes();
                String fid = null, ftitle = null, fcontent = null, flink = null;

                for (int j = 0; j < childNodes.getLength(); j++) {
                    String nodeName = childNodes.item(j).getNodeName();
                    String nodeContent = childNodes.item(j).getTextContent();

                    if (nodeName.equals("title")) {
                        ftitle   = cleanData(nodeContent);
                    } else if (nodeName.equals("link")) {
                        flink    = cleanData(nodeContent);
                    } else if (nodeName.equals("description")) {
                        fcontent = cleanData(nodeContent);
                    } else if (nodeName.equals("guid")) {
                        fid      = cleanData(nodeContent);
                    }
                }
                // If we have an old rss feed we need to use link as id
                if (fid == null) {
                    fid = flink;
                }

                // Add to feed item list.
                feedItems.add(new FeedItem(fid, ftitle, fcontent, flink));
            }
        } catch (Exception e) {
            System.out.println("Fail in XML parser");
            e.printStackTrace();
        }
        return feedItems;
    }

}
