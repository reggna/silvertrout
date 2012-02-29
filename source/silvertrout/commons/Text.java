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
public class Text {
	
	public static String color(String s, String color = "", Boolean background = false, String bgcolor = "")
	{
		
		HashMap colorMap = new HashMap();
		
		colorMap.put("white", "0");
		colorMap.put("black", "1");
		colorMap.put("darkblue", "2");
		colorMap.put("darkgreen", "3");
		colorMap.put("red", "4");
		colorMap.put("brown", "5");
		colorMap.put("magenta", "6");
		colorMap.put("orange", "7");
		colorMap.put("yellow", "8");
		colorMap.put("lightgreen", "9");
		colorMap.put("darkcyan", "10");
		colorMap.put("lightcyan", "11");
		colorMap.put("lightblue", "12");
		colorMap.put("pink", "13");
		colorMap.put("darkgrey", "14");
		colorMap.put("lightgrey", "15");
		
		if (background == true && bgcolor != "" && color != "")
		{
			return "\003" + colorMap.get(color) + "," + colorMap.get(bgcolor) + s + "\003";
		} 
		else if (background == true && bgcolor != "")
		{
			return "\003," + colorMap.get(bgcolor) + s + "\003";			
		}
		else if (color != "")
		{
			return "\003" + colorMap.get(color) + s + "\003";			
		}
		else 
		{
			return s;
		}
		
	}
	
	public static String format(String type = "", String s)
	{
		HashMap typeMap = new HashMap();
		
		typeMap.put("bold", "02");
		typeMap.put("reverse", "26");
		typeMap.put("underline", "37");
		
		if (type != "")
		{
			return "\0" + typeMap.get(type) + s + "\0" + typeMap.get(type);
		}
		else
		{
			return s;
		}
	}
	
}