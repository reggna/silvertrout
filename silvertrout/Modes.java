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
package silvertrout;

/**
 * Modes
 *
 *
 */
public class Modes {

    private String modes;

    public Modes() {
        this.modes = new String();
    }

    public Modes(String modes) {
        this.modes = modes;
    }

    public String get() {
        return this.modes;
    }

    public void set(String modes) {
        this.modes = modes;
    }

    public void giveMode(char m) {
        if (!haveMode(m)) {
            this.modes += m;
        }
    }

    public void takeMode(char m) {
        String newModes = new String();
        for (int i = 0; i < this.modes.length(); i++) {
            if (this.modes.charAt(i) != m) {
                newModes += this.modes.charAt(i);
            }
        }
        this.modes = newModes;
    }

    public boolean haveMode(char m) {
        return (this.modes.indexOf(m) >= 0);
    }
}
