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
package silvertrout.plugins.packagetracker;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import silvertrout.commons.XMLUtils;

public class SchenkerPackageProvider extends PackageProvider {

    public SchenkerPackageProvider() {
        name = "Schenker PrivPak";
        baseURL = "http://privpakportal.schenker.nu/TrackAndTrace/packagexml.aspx?packageid=";
    }

    public class SchenkerPackage extends Package {
        String customer = "";
        String service = "";
        String recieverZipCode = "";
        String recieverCity = "";
        String dateSent = "";
        String dateDelivered = "";
        String weight = "";

        public SchenkerPackage(Package p) {
            super(p);
        }

        @Override
        public List<Package.Event> getEvents() {
            ArrayList<Package.Event> events = new ArrayList<Package.Event>();

            // Connect and fetch package information:
            try {

                URL url = new URL(baseURL + getId());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document doc = builder.parse(con.getInputStream());
                Element root = doc.getDocumentElement();

                customer = XMLUtils.tryToGetTextContent(root, "customername");
                service = XMLUtils.tryToGetTextContent(root, "servicename");
                recieverZipCode = XMLUtils.tryToGetTextContent(root, "receiverzipcode");
                recieverCity = XMLUtils.tryToGetTextContent(root, "receivercity");
                dateSent = XMLUtils.tryToGetTextContent(root, "datesent");
                dateDelivered = XMLUtils.tryToGetTextContent(root, "datedelivered");
                weight = XMLUtils.tryToGetTextContent(root, "actualweight");

                NodeList eventList = doc.getElementsByTagName("event");
                for (int i = 0; i < eventList.getLength(); i++) {
                    Package.Event pe = new Package.Event();
                    String date = null, time = null;

                    NodeList eventListNodes = eventList.item(i).getChildNodes();
                    for (int j = 0; j < eventListNodes.getLength(); j++) {
                        Node n = eventListNodes.item(j);
                        if (n.getNodeName().equals("date")) {
                            date = n.getTextContent().replace("-", "");
                        } else if (n.getNodeName().equals("time")) {
                            time = n.getTextContent().replace(":", "");
                        } else if (n.getNodeName().equals("location")) {
                            pe.location = n.getTextContent();
                        } else if (n.getNodeName().equals("description")) {
                            pe.description = n.getTextContent();
                        }
                    }

                    if (date != null && time != null) {
                        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmm");
                        pe.dateTime = dtf.parseDateTime(date + time);
                    } else if (date != null) {
                        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
                        pe.dateTime = dtf.parseDateTime(date);
                    } else if (time != null) {
                        DateTimeFormatter dtf = DateTimeFormat.forPattern("HHmm");
                        pe.dateTime = dtf.parseDateTime(time);
                    }
                    events.add(pe);
                }

            } catch (Exception e) {
                System.out.println("Failed to update package " + getId());
                e.printStackTrace();
                return new ArrayList<Package.Event>();
            }

            return events;
        }

    }

    @Override
    public boolean isServiceProvider(String id) {

        try {
            URL url = new URL(baseURL + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(con.getInputStream());

            // A 'programevent' is returned both for erronous IDs and
            // IDs that havent been registered in the system yet
            if (doc.getElementsByTagName("programevent").getLength() > 0) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            System.out.println("Failed while parsing " + id + " for " + this);
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Package upgrade(Package p) {
        return new SchenkerPackage(p);
    }

}