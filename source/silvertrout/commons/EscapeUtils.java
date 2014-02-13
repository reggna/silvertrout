/*   _______ __ __                    _______                    __   
 *  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_ 
 *  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|
 *  |_______|__|__| \___/|_____|__|    |___|  |__| |_____|_____||____|
 * 
 *  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav"Gussoh" Sohtell
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

import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 **
 */
public class EscapeUtils {

    /**
     * Strip HTML/XML tags
     * 
     * This function strips all tags found. The data inside the tags are left
     * intact. This is function can be used to convert HTML pages to pure text.
     * <p>
     * Note that you still need to unescape HTML entities and perhaps normlize
     * the whitespaces of you want to do this.
     * 
     * @param source
     * @return
     */
    public static String stripHtml(String source) {
        return source.replaceAll("\\<.*?>", "");
    }

    /**
     * Normilize whitespaces
     * 
     * This function normalize whitespaces. It removes all groups of whitespaces
     * (spaces, tabs, and line breaks) and replaces them with just a single
     * space.
     * 
     * @param source
     *            The string to normalize
     * @return The normalized
     */
    public static String normalizeSpaces(String source) {
        return source.replaceAll("\\s+", " ");
    }

    /**
     * Apply unescapeHtml, stripHtml, and normalizeSpaces on a given String, and
     * return the result.
     * 
     * @param data
     *            the String to apply the functions on
     * @return the result after all functions has been applied
     * @see EscapeUtils.unescapeHtml
     * @see EscapeUtils.stripHtml
     * @see EscapeUtils.normalizeSpaces
     */
    public static String unescapeAndStripHtml(String data) {
        data = StringEscapeUtils.unescapeHtml4(data);
        data = EscapeUtils.stripHtml(data);
        data = EscapeUtils.normalizeSpaces(data);
        return data;
    }
}
