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

/**
 *
 **
 */
public class Color{
    public static String green(String s){
        return "\0039,1"+s+"\003";
    }

    public static String red(String s){
        return "\0034,1"+s+"\003";
    }

    public static String yellow(String s){
        return "\0038,1"+s+"\003";
    }

    public static String blue(String s){
        return "\00311,1"+s+"\003";
    }
    public static String white(String s){
        return "\0030,1"+s+"\003";
    }
    
    /**
     * 
     * Text Formatting
     * \002 = bold
     * \037 = underline
     * \026 is reverse
     * 
     */

    public static String bold(String s){
        return "\002"+s+"\002";
    }
    
    public static String underline(String s){
        return "\037"+s+"\037";
    }
    public static String reverse(String s){
        return "\026"+s+"\026";
    }
    
}