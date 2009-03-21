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

import java.util.ArrayList;
import java.util.Map;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import silvertrout.commons.EscapeUtils;
import silvertrout.Channel;
import silvertrout.User;


// TODO: save config
public class VersionControlEater extends silvertrout.Plugin {

    // Check interval (in minutes)
    final int checkInterval   = 3;

    // Binary locations    
    final String binarySVN    = "/usr/bin/svn";
    final String binaryCVS    = "/usr/local/cvs";
    final String binaryGIT    = "/usr/local/git";    

    // Prepository list
    final ArrayList<Repository>  reps    = new ArrayList<Repository>(); 
    // Thread list
    final ArrayList<CheckThread> threads = new ArrayList<CheckThread>();

    public class Repository {
        String  type;
        String  path;
        String  username;
        String  password;
        String  lastId;
        Channel channel;
    }
    
    public class CheckThread extends Thread {
    
        final ArrayList<String> messages = new ArrayList<String>();
        Repository repository;
    
        CheckThread(Repository r) {
            repository = r;
        }
        // TODO: SVN; use xml instead?
        // TODO: SVN, support authentication (username, password)
        // TODO: SVN, support svn+ssh
        // TODO: GIT, implement
        // TODO: CVS, implement
        public void run() {
        
            // SVN
            // =================================================================
            if(repository.type.equals("SVN")) {
                try {
                    ProcessBuilder pb     = new ProcessBuilder(binarySVN, "log", repository.path, "--limit", "10", "--incremental");
                    Process        p      = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String newLastId      = null;

                    for(int item = 0; item < 10; item++) {
                        String line = reader.readLine();
                        if(line != null && line.equals("------------------------------------------------------------------------")) {
                            String   info      = reader.readLine();
                            String[] infoParts = info.split(" \\| ");
                            
                            int    rev   = Integer.parseInt(infoParts[0].substring(1));
                            String user  = infoParts[1];
                            String date  = infoParts[2];
                            int    lines = Integer.parseInt(infoParts[3].substring(0, infoParts[3].lastIndexOf(" ")));
                            
                            String message = reader.readLine();
                            for(int i = 0; i < lines; i++) {
                                message += reader.readLine() + "\n";
                            }
                            
                            // No last 
                            if(repository.lastId == null) {
                                newLastId = String.valueOf(rev);
                                break;
                            } else {
                                if(Integer.parseInt(repository.lastId) < rev) {
                                    
                                    if(newLastId == null)newLastId = String.valueOf(rev);
                                    // New item! - TODO
                                    messages.add(rev + " - " + user + " - " + date
                                            + message + "\n");
                                } else {
                                    break;
                                }
                            } 
                        } else {
                            if(line != null) {
                                System.out.println("Error occured?:");
                                System.out.println(line);
                            }
                            break;
                        }
                    }       
                    
                    // Update last id (latest revision)
                    if(newLastId != null) {
                        repository.lastId = newLastId;
                    }
                    
                    // Wait for program end
                    p.waitFor();
                    System.out.println(p.exitValue());
                } catch(java.io.IOException e) {
                    e.printStackTrace();
                } catch( java.lang.InterruptedException e) {
                    e.printStackTrace();
                }
            // Unsupported Version Control System
            // =================================================================
            } else {
                messages.add("Unsupported repository type: " + repository.type);
            }
        }

    }

    public VersionControlEater() {

    }

    // TODO: load reps.. save reps?
    @Override
    public void onLoad(Map<String,String> settings) {

    }

    /**
     * Check the specified repository for new commits. Creates a new 
     * CheckThread for the repository, adds it to the thread list and then 
     * starts it.
     *
     * @param  repository  The repository to check
     */
    void checkRepository(Repository repository) {
        CheckThread checkThread = new CheckThread(repository);
        threads.add(checkThread);
        checkThread.start();
    }

    /**
     * Check all repositorys for new commits.
     * @see checkRepository
     */    
    void checkRepositorys() {
        for(Repository rep: reps) {
            checkRepository(rep);
        }
    }
    
    void checkDoneMessages() {
        for(CheckThread ct: threads) {
            // Thread is done:
            if(!ct.isAlive()) {
                ct.repository.channel.sendPrivmsg("New commit in repository ?:\n");
                for(String message: ct.messages) {            
                    ct.repository.channel.sendPrivmsg(message);
                }
                threads.remove(ct);
                break; 
            }
        }
    }

    public void addRepository(String type, String path, String username, String password, Channel channel) {
    
        Repository r    = new Repository();
        r.type          = type;
        r.path          = path;
        r.username      = username;
        r.password      = password;
        r.lastId        = null;
        r.channel       = channel;
        reps.add(r);
        
    }

    // TODO: last commit, revision,
    // TODO: more ideas?
    // TODO: add
    @Override
    public void onPrivmsg(User from, Channel to, String message) {
       // Add new repository
        if(message.startsWith("!vce")) {
            String[] params = message.split("\\s");
            addRepository("SVN", params[1], null, null, to);
        }
    }

    @Override
    public void onTick(int t) {
        System.out.print(".");
        if(t % (60 * checkInterval) == 0) {
            checkRepositorys();
        }
        
        checkDoneMessages();
    }
    
    
    public static void main(String[] args) {
    
        VersionControlEater vce = new VersionControlEater();
        vce.addRepository("SVN", "http://silvertrout.googlecode.com/svn/trunk/", null, null, null);
        
        try {
            for(int i = 0; i < 10000; i++) {
                vce.onTick(i);
                Thread.sleep(1000);
            }
        } catch(java.lang.InterruptedException e) {
            e.printStackTrace();
        }   
    
    }
}
