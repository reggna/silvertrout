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

import java.util.List;
import java.util.Map;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;

import silvertrout.commons.EscapeUtils;
import silvertrout.commons.ConnectHelper;
import silvertrout.Channel;
import silvertrout.User;
import org.apache.commons.lang3.StringEscapeUtils;

public class OperatorFinder {

    static private String URL_SERVER = "https://pts.siriusit.net";
    static private String URL_PATH   = "actionrequest/PTS/etjansterStart"
            + "/svenska/Om_Webbplatsen/Nummerkapacitet_r/"
            + "Enskilt+nummer?__ac_/pts.SearchNumber";

    static private String TAG_START  = "<p class=\"portlet-font\">";
    static private String TAG_END    = "</p>";

    static private String[] TWO      = {"08"};
    static private String[] THREE    = {"010", "011", "013", "016", "018",
            "019", "020", "023", "026", "031", "033", "035", "036", "040",
            "042", "044", "046", "054", "060", "063", "070", "071", "072",
            "073", "074", "075", "076", "077", "078", "090", "099"};

    static private String getAreaCode(String number) {
      for(String two: TWO) {
          if(number.startsWith(two))return two;
      }
      for(String three: THREE) {
          if(number.startsWith(three))return three;
      }
      return number.substring(0, 4);
    }

    static private String getOperator(String areaCode, String number) throws Exception {


        // Pre step
        // ================================================================
        HttpURLConnection l = (HttpURLConnection)(
                new URL("https://pts.siriusit.net/net/PTS/etjansterStart"
                + "/svenska/Om_Webbplatsen/Nummerkapacitet_r"
                + "/Enskilt+nummer").openConnection());
        l.setDoInput(true);
        l.setDoOutput(false);

        // Fetch cookies needed for later.
        String cookies = "";
        for(Map.Entry<String, List<String>> header: l.getHeaderFields().entrySet()) {
            if(header.getKey() != null && header.getKey().equals("Set-Cookie")) {
                for(String s: header.getValue()) {
                    cookies += s.substring(0, s.indexOf(";")) + "; ";
                }
            }
        }
        cookies = cookies.substring(0, cookies.length() - 2);

        // Lookup step
        // ================================================================
          URL               url = new URL(URL_SERVER + "/" + URL_PATH);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        //con.setFollowRedirects(true);
        con.setDoOutput(true);
        con.setRequestProperty("Cookie", cookies);
        con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");

        //con.setRequestMethod("POST");

        // Post request
        OutputStreamWriter out = new OutputStreamWriter(
                con.getOutputStream());
        String outdata = "action=search"
                + "&ndc="    + URLEncoder.encode(areaCode.substring(1), "UTF-8")
                + "&number=" + URLEncoder.encode(number, "UTF-8")
                + "&search=" + URLEncoder.encode("Sök", "UTF-8") + "\r\n";
        out.write(outdata);
        out.close();

        // Read in response
          BufferedReader in = new BufferedReader(
                  new InputStreamReader(con.getInputStream()));

          String result = "", decodedString = "";
        while ((decodedString = in.readLine()) != null) {
            result += decodedString;
        }
        in.close();


        // Find number
        int spos = result.indexOf(TAG_START) + TAG_START.length();
        number   = result.substring(spos);
        int epos = number.indexOf(TAG_END);
        number   = number.substring(0, epos);

        return StringEscapeUtils.unescapeHtml4(number);
    }

    public static String getOperator(String number) {

        try {
            String areaCode = getAreaCode(number);
            number          = number.substring(areaCode.length());

            String operator = getOperator(areaCode, number);
            return operator;
        } catch(Exception e) {
            return null;
        }
    }

}
