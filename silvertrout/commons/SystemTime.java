/*   _______ __ __                    _______                    __   
 *  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_ 
 *  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|
 *  |_______|__|__| \___/|_____|__|    |___|  |__| |_____|_____||____|
 * 
 *  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav Sothell
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

import java.util.GregorianCalendar;

public class SystemTime {

    public static String getCurrentTime() {
        /* get current time */
        GregorianCalendar c = new GregorianCalendar();
        return c.get(GregorianCalendar.YEAR) + "-" +
                intToStringer(c.get(GregorianCalendar.MONTH)) + "-" +
                intToStringer(c.get(GregorianCalendar.DAY_OF_MONTH)) + " " +
                intToStringer(c.get(GregorianCalendar.HOUR_OF_DAY)) + ":" +
                intToStringer(c.get(GregorianCalendar.MINUTE)) + ":" +
                intToStringer(c.get(GregorianCalendar.SECOND));

    }

    public static String intToStringer(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return "" + i;
        }
    }
}

