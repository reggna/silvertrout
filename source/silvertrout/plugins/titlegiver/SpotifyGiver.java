package silvertrout.plugins.titlegiver;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Fetch artist, album and track information from Spotify
 *
 * This is done by using the Spotify Metadata API (or Web API) which returns
 * information in XML format from a search or lookup. This class does lookup
 * request on Spotify URIs and Spotify HTTP URLs.
 *
 * @author Gustav Tiger <tiggex@gmail.com>
 *
 */
public class SpotifyGiver {

    static final String LOOKUP_URL = "http://ws.spotify.com/lookup/1/?uri=";

    /** Format: Arist */
    static final String ARTIST_FORMAT = "[Spotify - Artist] %s";
    /** Format: Artist, album, year */
    static final String ALBUM_FORMAT = "[Spotify - Album] %s - %s (%d)";
    /** Format: Artist, album, #, track, length */
    static final String TRACK_FORMAT = "[Spotify - Track] %s - %s - #%d %s (%.2fs)";

    static final String URI_FORMAT = "spotify:(album|artist|track):([a-zA-Z0-9]+)";
    static final String HTTP_FORMAT = "http://open.spotify.com/(album|artist|track)/([a-zA-Z0-9]+)";

    private static List<Element> getSubElement(Element root, String tag) {
        List<Element> results = new ArrayList<Element>();
        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
                    && node.getNodeName() == tag) {
                results.add((Element) list.item(i));
            }
        }
        return results;
    }

    private static List<String> getValueEx(Element root, String... tags) {
        List<String> result = new ArrayList<String>();
        if (tags.length == 0) {
            result.add(root.getTextContent().trim());
        } else {
            for (Element element : getSubElement(root, tags[0])) {
                result.addAll(getValueEx(element,
                        Arrays.copyOfRange(tags, 1, tags.length)));
            }
        }
        return result;
    }

    private static String getValue(Element e, String... tags) {
        List<String> strings = getValueEx(e, tags);
        String result = "";
        for (int i = 0; i < strings.size() - 2; i++) {
            result += strings.get(i) + ", ";
        }
        if (strings.size() > 1) {
            result += strings.get(strings.size() - 2) + " & ";
        }
        if (strings.size() > 0) {
            return result + strings.get(strings.size() - 1);
        }
        return null;
    }

    private static String lookup(String uri) {
        System.out.println(uri);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(new URL(LOOKUP_URL + uri).openStream());

            Element e = d.getDocumentElement();
            if (e.getNodeName().equals("artist")) {
                return String.format(ARTIST_FORMAT, getValue(e, "name"));
            } else if (e.getNodeName().equals("album")) {
                String album = getValue(e, "name");
                String artist = getValue(e, "artist", "name");
                int year = Integer.parseInt(getValue(e, "released"));
                return String.format(ALBUM_FORMAT, artist, album, year);
            } else if (e.getNodeName().equals("track")) {
                String track = getValue(e, "name");
                String artist = getValue(e, "artist", "name");
                String album = getValue(e, "album", "name");
                int tracknum = Integer.parseInt(getValue(e, "track-number"));
                double length = Double.parseDouble(getValue(e, "length"));
                return String.format(TRACK_FORMAT, artist, album, tracknum,
                        track, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Set<String> parse(String message, String regexp) {
        Set<String> uris = new HashSet<String>();
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(message);
        while (m.find()) {
            String text = lookup("spotify:" + m.group(1) + ":" + m.group(2));
            if (text != null) {
                uris.add(text);
            }
        }
        return uris;
    }

    public static Collection<String> parse(String message) {
        Set<String> result = parse(message, HTTP_FORMAT);
        result.addAll(parse(message, URI_FORMAT));
        return result;
    }
}
