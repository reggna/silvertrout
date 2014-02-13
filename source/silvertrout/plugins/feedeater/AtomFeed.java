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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import silvertrout.commons.EscapeUtils;

class AtomFeed extends Feed {

    public AtomFeed(Document doc) {

        Node feedNode = doc.getDocumentElement();
        NodeList elements = feedNode.getChildNodes();

        // Loop through feed items
        for (int i = 0; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            String nodeName = element.getNodeName();
            String nodeContent = element.getTextContent();

            if (nodeName.equals("title")) {
                title = EscapeUtils.unescapeAndStripHtml(nodeContent).split(
                        ":|-|\\||\\(|\\[", 2)[0].trim();
            }
        }

    }

    @Override
    protected Collection<FeedItem> getFeedItems() {

        // Create feed items list
        ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();

        try {
            // Fetch feed items
            HttpURLConnection con = (HttpURLConnection) getUrl()
                    .openConnection();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory
                    .newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(con.getInputStream());

            NodeList nodes = doc.getElementsByTagName("entry");

            for (int i = 0; i < nodes.getLength(); i++) {
                NodeList childNodes = nodes.item(i).getChildNodes();
                String fid = null, ftitle = null, fsummary = null, fcontent = null, flink = null;

                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (!(childNodes.item(j) instanceof Element)) {
                        continue;
                    }
                    Element node = (Element) childNodes.item(j);
                    String nodeName = childNodes.item(j).getNodeName();
                    String nodeContent = childNodes.item(j).getTextContent();

                    if (nodeName.equals("title")) {
                        ftitle = EscapeUtils.unescapeAndStripHtml(nodeContent);
                    } else if (nodeName.equals("link")) {
                        flink = EscapeUtils.unescapeAndStripHtml(node
                                .getAttribute("href"));
                    } else if (nodeName.equals("summary")) {
                        fsummary = EscapeUtils
                                .unescapeAndStripHtml(nodeContent);
                    } else if (nodeName.equals("content")) {
                        fcontent = EscapeUtils
                                .unescapeAndStripHtml(nodeContent);
                    } else if (nodeName.equals("id")) {
                        fid = EscapeUtils.unescapeAndStripHtml(nodeContent);
                    }
                }

                // Add to feed item list.
                feedItems.add(new FeedItem(fid, ftitle,
                        fsummary != null ? fsummary : fcontent, flink));
            }
        } catch (Exception e) {
            System.out.println("Fail in XML parser");
            e.printStackTrace();
        }
        return feedItems;
    }

}
