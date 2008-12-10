package silvertrout.plugins;

import java.util.regex.Pattern;
import java.util.regex.Matcher;



import silvertrout.commons.EscapeUtils;
import silvertrout.commons.ConnectHelper;
import silvertrout.Channel;
import silvertrout.User;

import silvertrout.commons.Base64Coder;

/**
 * JBT-plugin to fetch a web page's title and print it to the channel
 * 
 * Beta version with URL and URLConnection support. Tries to do some charset
 * conversion based on HTTP headers. TODO: Check meta tag for additional
 * information about charset.
 *
 * @author reggna
 * @author tigge
 * @version Beta 3.0
 */
public class Trace extends silvertrout.Plugin {

    public static String getName(String eniroInformation) {
        return getStuff(eniroInformation, "(?is)fileas=\"([^\"]+)");
    }

    public static String getAddress(String eniroInformation) {
        return getStuff(eniroInformation, "(?is)<span class=\"street-address\">([^<]+)");
    }

    public static String getPostalCode(String eniroInformation) {
        return getStuff(eniroInformation, "(?is)<span class=\"postal-code\">([^<]+)").replaceAll("\\D", "");
    }

    public static String getLocation(String eniroInformation) {
        return getStuff(eniroInformation, "(?is)<span class=\"locality\">([^<]+)").replaceAll("\\s+", "");
    }

    public static String getStuff(String m, String pattern) {
        Matcher mt = Pattern.compile(pattern).matcher(m);
        if (mt.find()) {
            return EscapeUtils.unescapeHtml(mt.group(1));
        } else {
            return null;
        }
    }

    public static String getEniroInformation(String phoneNumber) {
        return ConnectHelper.Connect("http", "personer.eniro.se", "/query?search_word=" + phoneNumber, 80, 16384);
    }

    public static String getSSN(String upplysningarInformation) {
        String ssn = Base64Coder.decodeString(getStuff(upplysningarInformation, "(?is)show\\.aspx\\?id=([^\"]+)")).replaceAll("\\D", "");
        return ssn.substring(0, 8) + "-" + ssn.substring(8);
    }

    public static String getUpplysningarInformation(String name, String location) {
        return ConnectHelper.Connect("http", "www.upplysning.se", "/search.aspx?bs=S%F6k&what=" + name + "&where=" + location, 80, 16384);
    }

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        if (channel != null) {
            String[] parts = message.split("\\s");
            if (parts.length == 2 && parts[0].equals("!trace")) {
                String ei = getEniroInformation(parts[1]);
                String ret = "";
                try {
                    String location = getLocation(ei);
                    ret = getName(ei) + ", " + getAddress(ei) + "   " + getPostalCode(ei) + " " + location + "    ";
                    location = java.net.URLEncoder.encode(location, "iso-8859-1");
                    String ui = getUpplysningarInformation(getName(ei).replaceAll(" ", "+"), getPostalCode(ei) + "+" + location);
                    ret += getSSN(ui);
                } catch (Exception e) {
                    e.printStackTrace();
                //sret = "Need more intertubez!";
                }
                channel.sendPrivmsg(user.getNickname() + ": " + ret);
            }
        }
    }
}
