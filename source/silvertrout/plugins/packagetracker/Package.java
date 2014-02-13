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
package silvertrout.plugins.packagetracker;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import silvertrout.Channel;

abstract public class Package {

    public class Event {

        public String description;
        public String location;
        public DateTime dateTime;

        @Override
        public String toString() {
            return dateTime.toString("yyyy-MM-dd HH:mm") + " : " + description + ", " + location;
        }
    }

    public static final int PACKAGE_TTL = 14; // TTL in days

    private final String id;
    private final String receiverNickname;
    private DateTime created = new DateTime();
    protected DateTime updated = new DateTime(0);
    public List<Event> events = new ArrayList<Event>();
    public Channel channel;

    public Package(String id, String receiverNickname) {
        this.id = id;
        this.receiverNickname = receiverNickname;
    }

    public Package(Package p) {
        this.id = p.id;
        this.receiverNickname = p.receiverNickname;
        this.created = p.created;
        this.updated = p.updated;
        this.events = p.events;
        this.channel = p.channel;
    }

    public String getId() {
        return id;
    }

    public String getReceiverNickname() {
        return receiverNickname;
    }

    public DateTime getCreated() {
        return created;
    }

    public DateTime getupdated() {
        return updated;
    }

    /**
     * Update package
     * 
     * @return
     */
    public List<Event> update() {
        List<Event> all = getEvents();
        List<Event> events = new ArrayList<Event>();
        DateTime latest = updated;

        for (Event event : all) {
            if (event.dateTime.isAfter(latest)) {
                latest = event.dateTime;
            }
            if (event.dateTime.isAfter(updated)) {
                events.add(event);
            }
        }
        updated = latest;

        return events;
    }

    protected List<Event> getEvents() {
        return new ArrayList<Event>();
    }

    @Override
    public String toString() {
        return "Package " + id + " on route to " + getReceiverNickname() + ".";
    }

    /**
     * Check if package have expired, either if it was created and not updated for a while
     * 
     * @return true if package have expired
     */
    public boolean expired() {
        Duration timeSinceUpdated = new Duration(new DateTime(created), null);
        Duration timeSinceCreated = new Duration(new DateTime(updated), null);

        return timeSinceUpdated.isLongerThan(Duration.standardDays(PACKAGE_TTL))
                && timeSinceCreated.isLongerThan(Duration.standardDays(PACKAGE_TTL));
    }
}