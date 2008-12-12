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

package silvertrout.settings;

/**
 *
 * @author Gussoh
 */
public class NetworkSettings {

    /** Network name */
    private String name;
    private String host;
    private int port;
    private String username;
    private String nickname;
    private String realname;
    private String password = null;
    private String charset = "UTF-8";
    private boolean secure = false;

    public NetworkSettings(String name, String host, int port, String username, String nickname, String realname) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.nickname = nickname;
        this.realname = realname;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    /**
     * Get the require password for this connection.
     * If there is no password needed returns null
     * @return password if needed, otherwise null
     */
    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getRealname() {
        return realname;
    }

    public String getUsername() {
        return username;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isSecure() {
        return secure;
    }

    @Override
    public String toString() {
        return "Network Settings: \"" + getName() + "\": " + getHost() + ":" + getPort() + ", " + getUsername() + ", " + getNickname();
    }
}
