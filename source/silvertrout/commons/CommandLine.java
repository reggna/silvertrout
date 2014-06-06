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

import java.util.Map;
import java.util.HashMap;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 **
 */
public class CommandLine {

    /*
    class Command {
        String cmd;

        class Parameter {
            String  name;
            String  shortName;
            String  description;
            boolean mandatory;
            Type    type;
        }
        addParameter( ...)
    }

    addCommand(...) */

    private String command;
    private Map<String, String> options = new HashMap<String, String>();

    /**
     *
     * @param line
     */
    public CommandLine(String line) {
        /* check that the first letter in the line is an exclamation point
        /* and save the command, else throw an exception*/
        Pattern pt = Pattern.compile("^!(\\S+)");
        Matcher mt = pt.matcher(line);
        if (mt.find()) {
            command = mt.group(1);
        } else {
            throw new IllegalArgumentException("\nThe line \n" + line + "\nis not a " +
                    "command line");
        }

        /* check for all hyphen followed by a letter, and save it and the rest of */
        /* the parameter in options */
        pt = Pattern.compile("-(\\S+?)\\s([^-]*)");
        mt.usePattern(pt);
        while (mt.find()) {
            options.put(mt.group(1), mt.group(2));
            System.out.println(mt.group(1) + "\t" + mt.group(2));
        }
    }

    /**
     *
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     *
     * @param s
     * @return
     */
    public boolean keyExist(String s) {
        return options.containsKey(s);
    }

    /**
     *
     * @param key
     * @return
     */
    public String getParam(String key) {
        return options.get(key);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        CommandLine c;
        try {
            c = new CommandLine("!unloadplugin -p password -pl Reloader");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Command: " + c.getCommand());
        System.out.println("Does -help exist? " + c.keyExist("help"));
        System.out.println("Parameters with -s: " + c.getParam("s"));
    }
}

