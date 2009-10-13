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
package silvertrout.plugins.erepwork;

import silvertrout.commons.ConnectHelper;
import silvertrout.Channel;

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
    private static final String channelName = "#erepublik";
    // Max content length (in bytes) to grab to check for header
    private static final int maxContentLength = 4096;
    private Channel channel = null;
    private HashMap<Integer, Worker> workerz = new HashMap<Integer, Worker>();
    private double rawStock = 0;

    @Override
    public void onTick(int ticks) {
        /* since the api only updates every half hour, we only check it each 30 minutes */
        if(ticks % 30*60 == 0){
            /* set channel if unset */
            if(channel == null) channel = getNetwork().getChannel(channelName);
            checkCompany();
        }
    }

    private void checkCompany(){
        String site = ConnectHelper.Connect(connection, server, file, port, maxContentLength);

        double newRawStock = Double.valueOf(fetch(site, "<raw-materials-in-stock>", "</raw-materials-in-stock>"));

        LinkedList<Worker> newWorkers = new LinkedList<Worker>();
        String[] workers = site.split("</employee>");
        for(int j = 0; j < workers.length-1; j++){
            String s = workers[j];
            Worker w = new Worker();
            /* set id of current worker */
            w.id = Integer.valueOf(fetch(s, "<citizen-id>", "</citizen-id>"));

            /* set the current skill values of the woker */
            String[] skills = s.substring(s.indexOf("<skills>")).split("</skill>");
            for(int i = 0; i< skills.length-1; i++){
                String ss = skills[i];
                double d = Double.valueOf(fetch(ss, "<value>", "</value>"));
                String domain = fetch(ss, "<domain>", "</domain>");
                if(domain.equals("manufacturing")) w.manu = d;
                else if(domain.equals("land")) w.land = d;
                else if(domain.equals("constructions")) w.cons = d;
            }
            
            /* set name and wellness */
            w.name = fetch(s, "<citizen-name>", "</citizen-name>");
            w.wellness = getWellness(w.id);

            newWorkers.add(w);
        }

        /* check each worker to see if he/she has worked since last check */
        for(Worker w: newWorkers){
            Worker worker = workerz.get(w.id);
            if(worker != null && worker.manu != w.manu){
                double productivity = worker.manu * (newWorkers.size()> 10 ? Math.max(1.0, 3.0-newWorkers.size()/10) : (1.0+(double)newWorkers.size()/10)) * (1.0+worker.wellness/50) *0.75;
                String print = worker.name + " has worked with skill "
                            + worker.manu + " and wellness " + worker.wellness
                            + ". With the current number of employees, this "
                            + "gives a total productivity of "+ productivity;
                if(channel == null) System.out.println(print);
                else channel.sendPrivmsg(print);
            }
        }

        /* print how much the stock of raw materials has changed, iff it has changed */
        if(rawStock != newRawStock){
            String print = "Raw materials in stock has changed from "
                        + rawStock +" to "+ newRawStock + " (" + newRawStock
                        + " - " + rawStock + " = "+ (newRawStock-rawStock) + ")";
            if(channel == null) System.out.println(print);
            else channel.sendPrivmsg(print);
            rawStock = newRawStock;
        }

        /* reset the varialbe workerz and update it with the current employees */
        workerz = new HashMap<Integer, Worker>();
        for(Worker w: newWorkers) workerz.put(w.id, w);
    }

    private double getWellness(int id){
        String citizens = ConnectHelper.Connect(connection, server, "/v1/feeds/citizens/" + id, port, maxContentLength);
        return Double.valueOf(fetch(citizens, "<wellness>", "</wellness>"));
    }

    private String fetch(String s, String startTag, String endTag){
        return s.substring(s.indexOf(startTag)+ startTag.length(), s.indexOf(endTag));
    }

    @Override
    public void onConnected() {
        // Join channel:
        if(!getNetwork().isInChannel(channelName)) {
            getNetwork().getConnection().join(channelName);
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
