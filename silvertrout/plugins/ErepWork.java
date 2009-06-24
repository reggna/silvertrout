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
package silvertrout.plugins;
import silvertrout.commons.ConnectHelper;
import silvertrout.Channel;
//import silvertrout.User;
import java.util.HashMap;

import java.util.LinkedList;
/**
 * 
 * @author reggna
 */
public class ErepWork extends silvertrout.Plugin {


    private static final int port = 80;
    private static final String connection = "http";
    private static final String server = "api.erepublik.com";
    private static final String file = "/v1/feeds/companies/192302";
    private static final String skillString = "manufacturing";
    private static final String channelName = "#erepublik";
    // Max content length (in bytes) to grab to check for header
    private static final int maxContentLength = 4096;
    private Channel channel = null;
    private LinkedList<Worker> workers = new LinkedList<Worker>();
    private HashMap<Integer, Worker> workerz = new HashMap<Integer, Worker>();
    private double rawStock = 0;

    @Override
    public void onTick(int ticks) {
        /* since the api only updates every half hour, we only check it each 30 minutes */
        if(ticks % 30*60 == 0){
            if(channel == null) channel = getNetwork().getChannel(channelName);
            if(channel != null) channel.sendAction("has joined! \\o/");
            checkCompany();
        }
    }
    private void checkCompany(){
        String site = ConnectHelper.Connect(connection, server, file, port, maxContentLength);

        double newRawStock = Double.valueOf(site.substring(site.indexOf("<raw-materials-in-stock>")+24,site.indexOf("</raw-materials-in-stock>")));

        LinkedList<Worker> newWorkers = new LinkedList<Worker>();
        while(site.indexOf("<employee>") != -1){
            Worker w = new Worker();
            w.id = Integer.valueOf(site.substring(site.indexOf("<citizen-id>")+12, site.indexOf("</citizen-id>")));
            site = site.substring(site.indexOf("<skills>"));
            while(site.indexOf("</skills>") > 10){
                double d = Double.valueOf(site.substring(site.indexOf("<value>")+7, site.indexOf("</value>")));
                String domain = site.substring(site.indexOf("<domain>")+8, site.indexOf("</domain>"));
                if(domain.equals("manufacturing")) w.manu = d;
                else if(domain.equals("land")) w.land = d;
                else if(domain.equals("constructions")) w.cons = d;
                site = site.substring(site.indexOf("</skill>")+8);
            }
            w.name = site.substring(site.indexOf("<citizen-name>")+14, site.indexOf("</citizen-name>"));
            String citizens = ConnectHelper.Connect(connection, server, "/v1/feeds/citizens/" + w.id, port, maxContentLength);
            w.wellness = Double.valueOf(citizens.substring(citizens.indexOf("<wellness>") +10, citizens.indexOf("</wellness>")));
            newWorkers.add(w);
            site = site.substring(site.indexOf("</employee>"));
        }
        for(Worker w: newWorkers){
            Worker worker = workerz.get(w.id);
            if(worker != null && worker.manu != w.manu){
                double productivity = worker.manu * (newWorkers.size()> 10 ? Math.max(1.0, 3.0-newWorkers.size()/10) : (1.0+(double)newWorkers.size()/10)) * (1.0+worker.wellness/50) *0.75;
                if(channel == null)
                    System.out.println(worker.name + " has worked with skill " + worker.manu + " and wellness " + worker.wellness + ". With the current number of employees, this gives a total productivity of "+ productivity);
                else
                    channel.sendPrivmsg(worker.name + " has worked with skill " + worker.manu + " and wellness " + worker.wellness + ". With the current number of employees, this gives a total productivity of "+ productivity);
            }
        }

        if(rawStock != newRawStock){
            if(channel == null)
                System.out.println("Raw materials in stock has changed from "+ rawStock +" to "+ newRawStock + " (" + newRawStock + " - " + rawStock + " = "+ (newRawStock-rawStock) + ")");
            else
                channel.sendPrivmsg("Raw materials in stock has changed from "+ rawStock +" to "+ newRawStock + " (" + newRawStock + " - " + rawStock + " = "+ (newRawStock-rawStock) + ")");
            rawStock = newRawStock;
        }
        workerz = new HashMap<Integer, Worker>();
        for(Worker w: newWorkers) workerz.put(w.id, w);
    }

    @Override
    public void onConnected() {
        // Join channel:
        if(!getNetwork().existsChannel(channelName)) {
            //getNetwork().getConnection().join(channelName);
        }
    }
    private class Worker{
        public String name;
        public int id;
        public double wellness;
        public double manu;
        public double land;
        public double cons;

    }
}