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
package silvertrout.plugins.trace;

import java.io.UnsupportedEncodingException;

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
 */
public class Trace extends silvertrout.Plugin {
    private String substring(String s, String start, String end){
        s = s.substring(s.indexOf(start)+start.length());
        return EscapeUtils.unescapeHtml(s.substring(0,s.indexOf(end)));
    }

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        String firstname, lastname, address, zipCode, locality, ssn, url = "";
        if (channel != null) {
            if (message.startsWith("!trace")) {
                message = message.substring(7);
                try{
                    url = "/query?search_word="
                            + java.net.URLEncoder.encode(message,
                            "iso-8859-1");
                }catch(UnsupportedEncodingException e){ /* not possible  */}
                /* fetch information from eniro */
                String eniro = ConnectHelper.Connect("http", "www.eniro.se",
                        url, 80, 16384, null, null);
                System.out.println(url);
                firstname = substring(eniro,
                        "<span class=\"given-name\">","<");
                lastname  = substring(eniro,
                        "<span class=\"family-name\">","<");
                address   = substring(eniro,
                        "<span class=\"street-address\">", "<");
                zipCode   = substring(eniro,
                        "<span class=\"postal-code\">", "<");
                locality  = substring(eniro,
                        "<span class=\"locality\">", "<");

                /* did something go wrong? */
                if(firstname.contains("/")){
                    try{
                        url = "/SearchMixed.aspx?vad="
                                + java.net.URLEncoder.encode(message,
                                "iso-8859-1");
                    }catch(UnsupportedEncodingException e){ /* not possible  */}
                    /* then we try hitta */
                    eniro = ConnectHelper.Connect("http", "hitta.se", url, 80,
                            524288, null, null);
                    System.out.println(url); 
                    firstname = substring(eniro,"var tooltipText = '<strong>",
                            " ");
                    lastname  = substring(eniro, "var tooltipText = '<strong>"
                            + firstname +"  ","<");
                    address   = substring(eniro, "<strong>Adress:</strong><br>",
                            "<");
                    zipCode   = substring(eniro, "var zipCode = '", "'");
                    locality  = substring(eniro, "var locality = \"", "\"");
                }
                
                /* still no match, then return */
                if(firstname.contains("/")){
                    channel.sendPrivmsg(user.getNickname() + ": N.N.");
                    return;
                }
                
                /* fetch ssn from upplysning */
                ssn = "";
                try{
                url = "/search.aspx?bs=S%F6k&what="
                        + java.net.URLEncoder.encode(firstname + " "
                        + lastname, "iso-8859-1") + "&where="
                        + java.net.URLEncoder.encode(address
                        + ", " + zipCode + " " + locality, "iso-8859-1");
                }catch(UnsupportedEncodingException e){ /* not possible  */}
                System.out.println(url);
                String upplysning = ConnectHelper.Connect("http",
                        "www.upplysning.se", url, 80, 16384, null, null);
                if(!upplysning.contains("<a href=\"show.aspx?id=")){
                    try{
                        url = "/search.aspx?bs=S%F6k&what="
                            + java.net.URLEncoder.encode(firstname + " "
                            + lastname, "iso-8859-1") + "&where="
                            + java.net.URLEncoder.encode(zipCode + " "
                            + locality, "iso-8859-1");
                    }catch(UnsupportedEncodingException e){ /* not possible  */}
                    System.out.println(url);
                    upplysning = ConnectHelper.Connect("http",
                            "www.upplysning.se", url, 80, 16384, null, null);
                }
                if(upplysning.contains("<a href=\"show.aspx?id=")){
                    ssn = Base64Coder.decodeString(substring(upplysning,
                            "<a href=\"show.aspx?id=","\"")).replaceAll(
                            "\\D", "");
                    ssn = ssn.substring(0, 8) + "-" + ssn.substring(8);
                }
                channel.sendPrivmsg(user.getNickname() + ": " + firstname + " "
                        + lastname + ", " + address + ", " + zipCode + " "
                        + locality + " " + ssn);
                
                // Fetch operator for a number
                String operator = OperatorFinder.getOperator(message);
                if(operator != null) {
                    channel.sendPrivmsg(user.getNickname() + ": " 
                            + message + ": " + operator);
                }

                // Fetch ratsit information
                if(ssn != null && !ssn.equals("")) {
                    String info = RatsitFinder.getInformation(ssn);
                    channel.sendPrivmsg(user.getNickname() + ": " + info);
                }
            }
        }
    }
}
