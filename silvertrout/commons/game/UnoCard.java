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
package silvertrout.commons.game;
import silvertrout.commons.Color;
public class UnoCard{
    
    private static final String[] ranks = {"0","1","2","3","4","5","6","7","8","9","S","DT","R","WILD","WD4"};
    private static final String[] colors = {"green", "red", "blue", "yellow"};
    private int rank;
    private int color;
    public UnoCard(int c, int r) throws Exception{
        if(c > 4 || c < -1 || r < 0 || r > 15)
            throw new Exception("Unable to create card: c"+ c +" r" +r);
        color = c;
        rank = r;
    }
    public UnoCard(String s) throws Exception{
        try{
            color = getColor(s.charAt(0));
            rank = getRank(s.split(" ")[1].toUpperCase());
        }catch(ArrayIndexOutOfBoundsException e){
            color = -1;
            rank = getRank(s.toUpperCase());
        }
    }
    public UnoCard(char c, int r) throws Exception{
        this(getColor(c), r);
    }

    @Override
    public boolean equals(Object c){
        return color == ((UnoCard)c).color && rank == ((UnoCard)c).rank;
    }

    public boolean match(UnoCard c){
        return color == c.color || rank == c.rank || c.color == -1;
    }

    public static int getColor(char s){
        for(int i = 0; i < colors.length; i++)
            if(s == colors[i].charAt(0))
                return i;
        return -1;
    }
    public void setColor(int i){
        color = i;
    }

    public static int getRank(String s){
        for(int i = 0; i < ranks.length; i++)
            if(ranks[i].equals(s))
                return i;
        return -1;
    }
    public int getRank(){
        return rank;
    }
    public int getColor(){
        return color;
    }

    @Override
    public String toString(){
        if(color == -1){
            if(rank == 13) return Color.white("[")+Color.green("W")+Color.red("I")+Color.yellow("L")+Color.blue("D")+Color.white("]");
            else if(rank == 14) return Color.white("[") + Color.green("W") + Color.red("D4") + Color.white("]");
            else return "[c"+color+"r"+rank+"]";
        }
        String s = "";
        switch(rank){
            case 10: s = "S"; break;
            case 11: s = "DT"; break;
            case 12: s = "R"; break;
            case 13: s = "WILD"; break;
            case 14: s = "WD4"; break;
            default: s = ""+ rank;
        }
        switch(color){
            case 0: return Color.green("["+s+"]"); // Green
            case 1: return Color.red("["+s+"]"); // Red
            case 2: return Color.blue("["+s+"]"); // Blue
            case 3: return Color.yellow("["+s+"]"); // Yellow
            default: return "[c"+color+"r"+rank+"]";
        }
    }
    public static String toString(Object[] cards){
        String s ="";
        for(Object c: cards){
            s +=c+Color.white(" ");
        }
        return s;
    }
    public int getValue(){
        switch(rank){
            case 10: return 20;
            case 11: return 20;
            case 12: return 20;
            case 13: return 50;
            case 14: return 50;
            default: return rank;
        }
    }
}