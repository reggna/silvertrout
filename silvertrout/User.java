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
package silvertrout;


/**
 * 
 * 
 * 
 */
public class User {

    private String nickname;
    private String hostname;
    private String server;
    private String username;
    private String realname;
    private boolean secureConnection;

    /**
     * A basic constructor to create a new User
     *
     * @param nickname - The nickname to set to the new User
     */
    public User(String nickname) {
        this.nickname = nickname;
    }

    public User(String nickname, String hostname, String server, String username, String realname, boolean secureConnection) {
        this.nickname = nickname;
        this.hostname = hostname;
        this.server = server;
        this.username = username;
        this.realname = realname;
        this.secureConnection = secureConnection;
    }

    /**
     * A method to fetch the user's nickname
     *
     * @return The nickname of the user
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * A method to set a user's nickname
     *
     * @param nickname - The nickname to set to the user
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHostname() {
        return hostname;
    }

    public String getRealname() {
        return realname;
    }

    public boolean isSecureConnection() {
        return secureConnection;
    }

    public String getServer() {
        return server;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Returnes the user as a String
     *
     * @return A string consisting of the user's nickname, hostname, realname and the server the user is connected to.
     */
    @Override
    public String toString() {
        return "[" + nickname + "@" + hostname + " (" + server + ") " + realname + " : " + hashCode() + "]";
    }

    /**
     * If the nickname is different they are not the same
     * If both have server set and the servers are not equal they are not the same
     * Otherwise they are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if ((this.nickname == null) ? (other.nickname != null) : !this.nickname.equals(other.nickname)) {
            return false;
        }
        if (this.server != null && other.server != null && !this.server.equals(other.server)) {
            return false;
        }
        return true;
    }

    /**
     * An arbitrary hashcode algorithm based on nickname only
     * @return hash of nickname
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.nickname != null ? this.nickname.hashCode() : 0);
        return hash;
    }
}

