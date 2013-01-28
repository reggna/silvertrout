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
// XML parser
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import silvertrout.Channel;

/**
 *
 * @author Reeen
 */
public class PackageServiceProviderFactory {

    public PackageServiceProviderFactory() {
    }

    public Posten getServiceProviderPosten() {
        return new Posten();
    }

    public Schenker getServiceProviderSchenker() {
        return new Schenker();
    }

    public abstract class PackageServiceProvider <P extends PackageServiceProviderFactory.Package> {

        public String name;
        public String baseURL;

        public abstract boolean isServiceProvider(String id);

        public abstract ArrayList<PackageServiceProviderFactory.PackageEvent> fetch(P p);

        protected String tryToGetTextContent(Element element, String tag) {
            if (element.getElementsByTagName(tag).getLength() > 0) {
                return element.getElementsByTagName(tag).item(0).getTextContent();
            } else {
                return "";
            }
        }

        @Override
        public String toString() {
            return name + " (" + baseURL + ")";
        }
    }
    
    public class Package {

        public String id;
        public PackageServiceProvider provider = null;
        public String receiverNickname;
        public DateTime lastDateTime;
        public final ArrayList<PackageEvent> events = new ArrayList<PackageEvent>();
        public Channel channel;

        @Override
        public String toString() {
            return "Package " + id + " on route to " + receiverNickname + ".";
        }
    }

    public class PackageEvent {

        public String description;
        public String location;
        public DateTime dateTime;

        @Override
        public String toString() {
            return dateTime.toString("yyyy-MM-dd HH:mm") + " : " + description + ", " + location;
        }
    }

    public class Posten extends PackageServiceProvider<Posten.PostenPackage> {

        public Posten() {
            name = "Posten AB";
            baseURL = "http://logistics.postennorden.com/wsp/rest-services/ntt-service-rest/api/shipment.xml?id={INSERT_ID}&locale=sv&consumerId=4617779c-d862-4508-91fc-1adf7be36001";
        }

        public class PostenPackage extends PackageServiceProviderFactory.Package {

            String sender = "";
            String service = "";
            String receiverName = "";
            String receiverStreet;
            String receiverPostalCode = "";
            String receiverCity = "";
            String receiverCountry = "";
            String estimatedTOA = "";
            String weight = "";

            @Override
            public String toString() {
                String delivery = "";
                if(!estimatedTOA.equals(""))
                    delivery = "Expected time of delivery is " + estimatedTOA;
                return "Package (" + id + ", " + weight + ") added by " + receiverNickname +
                        " on route to " +  receiverName + " " + receiverStreet + " " +
                        receiverPostalCode + " " + receiverCity + " " + receiverCountry + ". " +
                        delivery;
            }
        }

        @Override
        public boolean isServiceProvider(String id) {

            try {
                URL url = new URL(baseURL.replace("{INSERT_ID}", id));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document doc = builder.parse(con.getInputStream());

                // A Shipment-tag is used to indicate that the ID is valid for this provider
                if (doc.getElementsByTagName("Shipment").getLength() > 0) {
                    return true;
                } // A fault is returned if the ID is invalid (e.g. too short)
                else if (doc.getElementsByTagName("Fault").getLength() > 0) {
                    NodeList faults = doc.getElementsByTagName("Fault");
                    for (int i = 0; i < faults.getLength(); i++) {
                        NodeList fault = faults.item(i).getChildNodes();
                        for (int j = 0; j < fault.getLength(); j++) {
                            Element faultElement = (Element) fault.item(i);
                            String faultCode = faultElement.getElementsByTagName("faultCode").item(0).getTextContent();
                            String faultExplanation = faultElement.getElementsByTagName("explanationText").item(0).getTextContent();
                            System.out.println("Package ID invalid for " + name + ": " + faultExplanation + " (" + faultCode + ")");
                        }
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
        public ArrayList<PackageEvent> fetch(PostenPackage pack) {
            ArrayList<PackageEvent> events = new ArrayList<PackageEvent>();
            // Connect and fetch package information:
            try {

                URL url = new URL(baseURL.replace("{INSERT_ID}", pack.id));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document doc = builder.parse(con.getInputStream());

                // The sender (service customer)
                if (doc.getElementsByTagName("consignor").getLength() > 0) {
                    Element consignor = (Element) doc.getElementsByTagName("consignor").item(0);
                    pack.sender = tryToGetTextContent(consignor, "name");
                }
                // The service name
                if (doc.getElementsByTagName("service").getLength() > 0) {
                    Element service = (Element) doc.getElementsByTagName("service").item(0);
                    pack.service = tryToGetTextContent(service, "name");
                }
                // The receivers information
                if (doc.getElementsByTagName("consignee").getLength() > 0) {
                    Element consignee = (Element) doc.getElementsByTagName("consignee").item(0);
                    pack.receiverName = tryToGetTextContent(consignee, "name");

                    if (consignee.getElementsByTagName("address").getLength() > 0) {
                        Element address = (Element) consignee.getElementsByTagName("address").item(0);
                        pack.receiverStreet += tryToGetTextContent(address, "street1");
                        pack.receiverStreet += " " + tryToGetTextContent(address, "street2");
                        pack.receiverStreet += " " + tryToGetTextContent(address, "street3");
                        pack.receiverPostalCode = tryToGetTextContent(address, "postalCode");
                        pack.receiverCity = tryToGetTextContent(address, "city");
                        pack.receiverCountry = tryToGetTextContent(address, "country");
                    }

                    if (doc.getElementsByTagName("estimatedTimeOfArrival").getLength() > 0) {
                        pack.estimatedTOA = doc.getElementsByTagName("estimatedTimeOfArrival").item(0).getTextContent();
                    }
                    if (doc.getElementsByTagName("totalWeight").getLength() > 0) {
                        Element totalWeight = (Element) doc.getElementsByTagName("actualweight").item(0);
                        pack.weight = tryToGetTextContent(totalWeight, "value");
                    }

                    NodeList eventList = doc.getElementsByTagName("TrackingEvent");
                    System.out.println("Got " + eventList.getLength() + " events");

                    for (int i = 0; i < eventList.getLength(); i++) {
                        PackageEvent pe = new PackageEvent();

                        NodeList eventListNodes = eventList.item(i).getChildNodes();
                        for (int j = 0; j < eventListNodes.getLength(); j++) {
                            Node n = eventListNodes.item(j);
                            if (n.getNodeName().equals("eventTime")) {
                                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmm");
                                pe.dateTime = dtf.parseDateTime(n.getTextContent().replace("T", ""));
                            } else if (n.getNodeName().equals("eventDescription")) {
                                pe.description = n.getTextContent();
                            }
                        }

                        Element trackingEvent = (Element) eventList.item(i);
                        Element locationInfo = (Element) trackingEvent.getElementsByTagName("location").item(0);

                        String locationName = tryToGetTextContent((Element) locationInfo, "displayName");
                        String locationPostalCode = tryToGetTextContent((Element) locationInfo, "postalCode");
                        String locationCity = tryToGetTextContent((Element) locationInfo, "city");
                        String locationCountry = tryToGetTextContent((Element) locationInfo, "country");
                        String locationType = tryToGetTextContent((Element) locationInfo, "locationType");

                        pe.location = locationName + " " + locationPostalCode + " " + locationCity + " " + " " + locationCountry + " (" + locationType + ")";

                        if (pe.dateTime.isAfter(pack.lastDateTime)) {
                            events.add(pe);
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Failed to update package " + pack.id);
                e.printStackTrace();
                return new ArrayList<PackageEvent>();
            }

            return events;
        }
    }

    public class Schenker extends PackageServiceProvider<Schenker.SchenkerPackage> {

        public Schenker() {
            name = "Schenker PrivPak";
            baseURL = "http://privpakportal.schenker.nu/TrackAndTrace/packagexml.aspx?packageid=";
        }
        
        public class SchenkerPackage extends PackageServiceProviderFactory.Package {
            String customer = "";
            String service = "";
            String recieverZipCode = "";
            String recieverCity = "";
            String dateSent = "";
            String dateDelivered = "";
            String weight = "";
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
        public ArrayList<PackageEvent> fetch(SchenkerPackage pack) {
            ArrayList<PackageEvent> events = new ArrayList<PackageEvent>();
            SchenkerPackage p = (SchenkerPackage)pack;

            // Connect and fetch package information:
            try {

                URL url = new URL(p.provider.baseURL + p.id);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document doc = builder.parse(con.getInputStream());

                if (doc.getElementsByTagName("customername").getLength() > 0) {
                    p.customer = doc.getElementsByTagName("customername").item(0).getTextContent();
                }
                if (doc.getElementsByTagName("servicename").getLength() > 0) {
                    p.service = doc.getElementsByTagName("servicename").item(0).getTextContent();
                }
                if (doc.getElementsByTagName("receiverzipcode").getLength() > 0) {
                    p.recieverZipCode = doc.getElementsByTagName("receiverzipcode").item(0).getTextContent();
                }
                if (doc.getElementsByTagName("receivercity").getLength() > 0) {
                    p.recieverCity = doc.getElementsByTagName("receivercity").item(0).getTextContent();
                }
                if (doc.getElementsByTagName("datesent").getLength() > 0) {
                    p.dateSent = doc.getElementsByTagName("datesent").item(0).getTextContent();
                }
                if (doc.getElementsByTagName("datedelivered").getLength() > 0) {
                    p.dateDelivered = doc.getElementsByTagName("datedelivered").item(0).getTextContent();
                }
                if (doc.getElementsByTagName("actualweight").getLength() > 0) {
                    p.weight = doc.getElementsByTagName("actualweight").item(0).getTextContent();
                }

                NodeList eventList = doc.getElementsByTagName("event");
                System.out.println("Got " + eventList.getLength() + " events");
                for (int i = 0; i < eventList.getLength(); i++) {
                    PackageEvent pe = new PackageEvent();
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

                    if (pe.dateTime.isAfter(p.lastDateTime)) {
                        events.add(pe);
                    }
                }

            } catch (Exception e) {
                System.out.println("Failed to update package " + p.id);
                e.printStackTrace();
                return new ArrayList<PackageEvent>();
            }

            return events;
        }
    }
}
