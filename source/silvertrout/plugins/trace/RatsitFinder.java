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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import silvertrout.commons.EscapeUtils;

public class RatsitFinder {

    private static final String LOGIN_EMAIL    = "tigge@hotmail.com";
    private static final String LOGIN_PASSWORD = "xxxxxxxx";

    private static final String URL_SERVER     = "https://www.ratsit.se";
    private static final String URL_LOGIN_PATH = "Login.aspx";

    private static String extract(String string, String from, String to) {
        int start = string.indexOf(from) + from.length();
        int end   = string.indexOf(to, start);
        return string.substring(start, end);
    }

    private static String getViewstate(String page) {
        return extract(page, "id=\"__VIEWSTATE\" value=\"", "\"");
    }

    private static String getConnectionData(HttpURLConnection con) throws IOException {
            // Read in response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String result = "", decodedString = "";
            while ((decodedString = in.readLine()) != null) {
                result += decodedString;
            }
            in.close();
            return result;

    }

    private static void login() {

    }


    public static String getInformation(String personalIdentityNumber) {
        try {

            // Login (pre phase)
            // =================================================================

            HttpURLConnection l = (HttpURLConnection)(new URL(
                    "https://www.ratsit.se/Login.aspx").openConnection());
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
            cookies = cookies.substring(0, cookies.lastIndexOf(";"));
            // Read in response
            String presult   = getConnectionData(l);
            String viewstate = getViewstate(presult);

            // Login
            // =================================================================

            URL               url = new URL(URL_SERVER + "/" + URL_LOGIN_PATH);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            String formdata = "__LASTFOCUS=&__EVENTTARGET=&__EVENTARGUMENT="
                    + "&__PREVIOUSPAGE=V0PtOYkGb6LNV_cxLWTyWA2"
                    + "&ctl00%24cphMain%24txtEmail="
                    + URLEncoder.encode(LOGIN_EMAIL, "UTF-8")
                    + "&ctl00%24cphMain%24txtPassword="
                    + URLEncoder.encode(LOGIN_PASSWORD, "UTF-8")
                    + "&ctl00%24cphMain%24cmdLogin.x=45"
                    + "&ctl00%24cphMain%24cmdLogin.y=9"
                    + "&__VIEWSTATE=" + URLEncoder.encode(viewstate, "UTF-8");

            con.setDoOutput(true);
            con.setInstanceFollowRedirects(true);
            con.setRequestProperty("Cookie", cookies);
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
            con.setRequestProperty("Referer", "https://www.ratsit.se/Login.aspx");

            // Post request
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(formdata);
            out.close();

            // Read in response
            String result = getConnectionData(con);

            // Searching (pre step)
            // =================================================================

            HttpURLConnection conps = (HttpURLConnection)(new URL(
                    "http://www.ratsit.se/BC/Search.aspx").openConnection());
            conps.setDoOutput(true);
            conps.setInstanceFollowRedirects(true);
            conps.setRequestProperty("Cookie", cookies);
            conps.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
            conps.setRequestProperty("Referer", "http://www.ratsit.se/BC/Search.aspx");

            String spresult = getConnectionData(conps);
            viewstate       = getViewstate(spresult);

            // Searching
            // =================================================================

            HttpURLConnection cons = (HttpURLConnection)(new URL(
                    "http://www.ratsit.se/BC/Search.aspx").openConnection());

            cons.setDoOutput(true);
            cons.setInstanceFollowRedirects(true);
            cons.setRequestProperty("Cookie", cookies);
            cons.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
            cons.setRequestProperty("Referer", "http://www.ratsit.se/BC/Search.aspx");

            String searchData =
                "__LASTFOCUS=&__EVENTTARGET=&__EVENTARGUMENT="
                + "&__VIEWSTATE=" + URLEncoder.encode(viewstate, "UTF-8")
                + "&ctl00%24cphMain%24txtFirstName="
                + "&ctl00%24cphMain%24txtLastName="
                + "&ctl00%24cphMain%24txtBirthDate="
                + URLEncoder.encode(personalIdentityNumber, "UTF-8")
                + "&ctl00%24cphMain%24txtAddress="
                + "&ctl00%24cphMain%24txtZipCode="
                + "&ctl00%24cphMain%24txtCity="
                + "&ctl00%24cphMain%24cmdButton.x=1"
                + "&ctl00%24cphMain%24cmdButton.y=1";

            OutputStreamWriter outs = new OutputStreamWriter(cons.getOutputStream());
            outs.write(searchData);
            outs.close();

            // Read in response
            String sresult = getConnectionData(cons);
            String resultLink = extract(sresult,
                    "<td class=\"GridLink\"><a href=\"", "\"");

            // Result
            // =================================================================

            HttpURLConnection conr = (HttpURLConnection)(new URL(
                    "http://www.ratsit.se/BC/" + resultLink).openConnection());

            conr.setInstanceFollowRedirects(true);
            conr.setRequestProperty("Cookie", cookies);
            conr.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
            conr.setRequestProperty("Referer", "http://www.ratsit.se/BC/Search.aspx");

            System.out.println(resultLink);
            // Read in response
            String rresult = getConnectionData(conr);
            String civils = EscapeUtils.stripHtml(extract(rresult,
                    "nd:</a></td><td class=\"GridCell\">", "</td>"));
            System.out.println(civils);
            if(civils.contains("Gift med ")){
                HashMap<String,String> personInfo = new HashMap<String,String>();
                personInfo.put("firstname", Trace.substring(civils, "Gift med ", " "));
                String s = civils.substring(0, civils.lastIndexOf(" "));
                s = s.substring(s.lastIndexOf(" ")+1);
                personInfo.put("lastname", s);
                personInfo.put("ssn", Trace.substring(civils, "(", ")").substring(0,8));
                Trace.getSSN(personInfo);
                civils = civils.replaceAll("XXXX", personInfo.get("ssn").substring(9));
            }
            String bolag = extract(rresult, 
                    "sengagemang:</a></td><td class=\"GridCell\">", "</td>");

            return EscapeUtils.normalizeSpaces(
                    "Civilstånd: " + civils
                    + ", Bolagsengagemang: " + EscapeUtils.stripHtml(bolag));

        } catch (IOException ex) {
            Logger.getLogger(RatsitFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }

}
