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
package silvertrout.commons.game;

public class Trophy {

    public enum Value {

        PLATINUM, GOLD, SILVER, BRONZE
    };
    private String name;
    private String archivment;
    private Value value;

    public Trophy(String name, String archivment, Value value) {
        this.name = name;
        this.archivment = archivment;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getArchivment() {
        return this.archivment;
    }

    public Value getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        Trophy t = (Trophy) o;
        return t.name.equals(this.name);
    }
}

