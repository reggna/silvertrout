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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import silvertrout.commons.XMLUtils;

public class PostenPackageProvider extends PackageProvider {
    private final String consumerID;

    public PostenPackageProvider(String consumerID) {
        name = "Posten AB";
        baseURL = "http://logistics.postennorden.com/wsp/rest-services/ntt-service-rest/api/shipment.xml?id={INSERT_ID}&locale=sv&consumerId=";
        this.consumerID = consumerID;
    }

    public class PostenPackage extends Package {

        String sender = "";
        String service = "";
        String receiverName = "";
        String receiverStreet;
        String receiverPostalCode = "";
        String receiverCity = "";
        String receiverCountry = "";
        String estimatedTOA = "";
        String weight = "";

        public PostenPackage(Package p) {
            super(p);
        }

        @Override
        protected ArrayList<PackageEvent> getEvents() {
            ArrayList<PackageEvent> events = new ArrayList<PackageEvent>();
            // Connect and fetch package information:
            try {
                URL url = new URL(baseURL.replace("{INSERT_ID}", getId()) + consumerID);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document doc = builder.parse(con.getInputStream());

                // The sender (service customer)
                if (doc.getElementsByTagName("consignor").getLength() > 0) {
                    Element consignor = (Element) doc.getElementsByTagName("consignor").item(0);
                    sender = XMLUtils.tryToGetTextContent(consignor, "name");
                }
                // The service name
                if (doc.getElementsByTagName("service").getLength() > 0) {
                    Element servicee = (Element) doc.getElementsByTagName("service").item(0);
                    service = XMLUtils.tryToGetTextContent(servicee, "name");
                }
                // The receivers information
                if (doc.getElementsByTagName("consignee").getLength() > 0) {
                    Element consignee = (Element) doc.getElementsByTagName("consignee").item(0);
                    receiverName = XMLUtils.tryToGetTextContent(consignee, "name");

                    if (consignee.getElementsByTagName("address").getLength() > 0) {
                        Element address = (Element) consignee.getElementsByTagName("address").item(
                                0);
                        receiverStreet += XMLUtils.tryToGetTextContent(address, "street1");
                        receiverStreet += " " + XMLUtils.tryToGetTextContent(address, "street2");
                        receiverStreet += " " + XMLUtils.tryToGetTextContent(address, "street3");
                        receiverPostalCode = XMLUtils.tryToGetTextContent(address, "postalCode");
                        receiverCity = XMLUtils.tryToGetTextContent(address, "city");
                        receiverCountry = XMLUtils.tryToGetTextContent(address, "country");
                    }

                    if (doc.getElementsByTagName("estimatedTimeOfArrival").getLength() > 0) {
                        estimatedTOA = doc.getElementsByTagName("estimatedTimeOfArrival").item(0)
                                .getTextContent();
                    }
                    if (doc.getElementsByTagName("totalWeight").getLength() > 0) {
                        Element totalWeight = (Element) doc.getElementsByTagName("totalWeight")
                                .item(0);
                        weight = XMLUtils.tryToGetTextContent(totalWeight, "value");
                    }

                    NodeList eventList = doc.getElementsByTagName("TrackingEvent");
                    for (int i = 0; i < eventList.getLength(); i++) {
                        PackageEvent pe = new PackageEvent();

                        NodeList eventListNodes = eventList.item(i).getChildNodes();
                        for (int j = 0; j < eventListNodes.getLength(); j++) {
                            Node n = eventListNodes.item(j);
                            if (n.getNodeName().equals("eventTime")) {
                                DateTimeFormatter dtf = DateTimeFormat
                                        .forPattern("yyyy-MM-dd'T'HH:mm:ss");
                                pe.dateTime = dtf.parseDateTime(n.getTextContent());
                            } else if (n.getNodeName().equals("eventDescription")) {
                                pe.description = n.getTextContent();
                            }
                        }

                        Element trackingEvent = (Element) eventList.item(i);
                        Element locationInfo = (Element) trackingEvent.getElementsByTagName(
                                "location").item(0);

                        String locationName = XMLUtils.tryToGetTextContent(locationInfo,
                                "displayName");
                        String locationPostalCode = XMLUtils.tryToGetTextContent(locationInfo,
                                "postalCode");
                        String locationCity = XMLUtils.tryToGetTextContent(locationInfo, "city");
                        String locationCountry = XMLUtils.tryToGetTextContent(locationInfo,
                                "country");
                        String locationType = XMLUtils.tryToGetTextContent(locationInfo,
                                "locationType");

                        pe.location = locationName + " " + locationPostalCode + " " + locationCity
                                + " " + " " + locationCountry + " (" + locationType + ")";
                        events.add(pe);
                    }
                }

            } catch (Exception e) {
                System.out.println("Failed to update package " + getId());
                e.printStackTrace();
                return new ArrayList<PackageEvent>();
            }

            return events;
        }

        @Override
        public String toString() {
            String delivery = "";
            if (!estimatedTOA.equals(""))
                delivery = "Expected time of delivery is " + estimatedTOA;
            return "Package (" + getId() + ", " + weight + ") added by " + receiverNickname
                    + " on route to " + receiverName + " " + receiverStreet + " "
                    + receiverPostalCode + " " + receiverCity + " " + receiverCountry + ". "
                    + delivery;
        }
    }

    @Override
    public boolean isServiceProvider(String id) {

        try {
            URL url = new URL(baseURL.replace("{INSERT_ID}", id) + consumerID);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(con.getInputStream());

            // A Shipment-tag is used to indicate that the ID is valid for
            // this provider
            if (doc.getElementsByTagName("Shipment").getLength() > 0) {
                System.out.println("Found shipment");
                return true;
            } // A fault is returned if the ID is invalid (e.g. too short)
            else if (doc.getElementsByTagName("Fault").getLength() > 0) {
                NodeList faults = doc.getElementsByTagName("Fault");
                System.out.println("Fault " + faults);
                for (int i = 0; i < faults.getLength(); i++) {
                    Element fault = (Element) faults.item(i);
                    String code = XMLUtils.tryToGetTextContent(fault, "faultCode");
                    String explanation = XMLUtils.tryToGetTextContent(fault, "explanationText");
                    System.out.println("Package ID invalid for " + name + ": " + explanation + " ("
                            + code + ")");

                }
                return false;
            }
            // Neither a shipmen nor a fault = incorrect
            else {
                return false;
            }

        } catch (Exception e) {
            System.out.println("Failed while parsing " + id + " for " + this);
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Package upgrade(Package p) {
        return new PostenPackage(p);
    }

}