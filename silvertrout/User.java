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

import java.util.ArrayList;

/**
 * 
 * 
 * 
 */
public class User {

    private String nickname;
    private String hostname;
    private String server;
    private String realname;
    private boolean secureConnection;
    private ArrayList<Channel> channels;

    /**
     * A basic constructor to create a new User
     *
     * @param nickname - The nickname to set to the new User
     */
    public User(String nickname) {
        this.nickname = nickname;
    }

    public User(String nickname, String hostname, String server,
            boolean secureConnection, ArrayList<Channel> channels) {
        this(nickname);
    }

    public ArrayList<Channel> getChannels() {
        return channels;
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

    /**
     * Returnes the user as a String
     *
     * @return A string consisting of the user's nickname, hostname, realname and the server the user is connected to.
     */
    @Override
    public String toString() {
        return "[" + nickname + "@" + hostname + " (" + server + ") " + realname + " : " + hashCode() + "]";
    }
}

