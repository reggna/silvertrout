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
package silvertrout.commons;

import java.nio.ByteBuffer;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.net.URL;
import java.net.HttpURLConnection;

public class ConnectHelper {

    // Content types the tile giver should check
    private static final String[] contentTypes = new String[]{"text/html",
        "application/xhtml+xml", "application/xml", "text/xml"};
    // Charset to fall back to if none was found
    private static final String fallbackCharset = "iso-8859-1";

    public static String Connect(String connectionType, String server, String file, int port, int maxContentLength) {
        try {
            // Set up connection to disallow output and allow input. It should follow
            // redirects but dont use a cache.
            URL urll = new URL(connectionType, server, port, file);
            HttpURLConnection con = (HttpURLConnection) urll.openConnection();

            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");

            con.setAllowUserInteraction(false);
            con.setDoInput(true);
            con.setDoOutput(false);
            con.setUseCaches(false);
            con.setInstanceFollowRedirects(true);

            // Find out charset and content type. As default, if we don't find a
            // charset in the HTML header we try to use ISO-8559-1.
            String patternContentType = "(?i)^([a-zA-Z0-9_\\-\\/]+)(?:;)+(?:\\s)*(?:" +
                    "charset=([a-zA-Z0-9_\\-]+))?+$";
            Pattern pc = Pattern.compile(patternContentType);
            System.out.println(con.getContentType());
            Matcher mc = pc.matcher(con.getContentType());
            String charset = null;
            String contentType = null;
            if (mc.find()) {
                contentType = mc.group(1);
                charset = mc.group(2);
            } else {
                System.out.println("Link does not contain content type");
                System.out.println(con.getContentType());
                return null;
            }

            // Check for content type. Only accept web pages.
            if (!okContentType(contentType)) {
                return null;
            }

            // Byte buffer (from content length):
            int contentLength = con.getContentLength();
            if (contentLength > maxContentLength || contentLength < 100) {
                contentLength = maxContentLength;
            }
            ByteBuffer bb = ByteBuffer.allocate(contentLength);

            try {
                while (true) {
                    byte[] tmp = new byte[256];
                    int cnt = con.getInputStream().read(tmp);
                    if (cnt == -1) {
                        break;
                    }
                    bb.put(tmp, 0, cnt);
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return null;
            } catch (java.nio.BufferOverflowException e) {
                e.printStackTrace();
            // TODO: Work around
            }

            if (charset == null) {
                String patternMeta = "(?i)<meta http-equiv=\"Content-Type\" " +
                        "content=\"([a-zA-Z0-9_\\-\\/]+)(?:; " +
                        "charset=([a-zA-Z0-9_\\-]+))?+\"";
                Pattern pm = Pattern.compile(patternMeta);
                Matcher mm = pm.matcher(new String(bb.array(), fallbackCharset));

                if (mm.find()) {
                    charset = mm.group(2);
                }
            }
            // If still no charset, fall back to fallbackCharset
            if (charset == null) {
                charset = fallbackCharset;
            }

            return new String(bb.array(), charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean okContentType(String contentType) {
        for (int i = 0; i < contentTypes.length; i++) {
            if (contentTypes[i].equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        System.out.println("damn content type!");
        return false;
    }
}

