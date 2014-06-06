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
package silvertrout.plugins.versioncontroleater;

import java.util.ArrayList;
import java.util.Map;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;



import silvertrout.commons.EscapeUtils;
import silvertrout.Channel;
import silvertrout.User;


// TODO: save config
/**
 *
 **
 */
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

    /**
     *
     */
    public class Repository {
        String  type;
        String  path;
        String  username;
        String  password;
        String  lastId;
        Channel channel;
    }

    /**
     *
     */
    public class CheckThread extends Thread {

        final ArrayList<String> messages = new ArrayList<String>();
        Repository repository;

        CheckThread(Repository r) {
            repository = r;
        }
        // TODO: SVN; use xml instead?
        // TODO: GIT, implement
        // TODO: CVS, implement
        public void run() {

            // SVN
            // =================================================================
            // I reccommend using anonymous SVN, a public guest account with
            // read only access or a public svn key. These are safe the safe
            // choises. Other methods might not work or have security issues.
            // Read on for more information about this.
            //
            //
            // Tunneled SVN + SSH - This is only going to work with public keys.
            // When using password open ssh opens a password input prompt in
            // TTY. This is not trivial to solve, but a solution could be to use
            // an external library like Trilead SSH or some other native Java
            // code library.
            //
            // There might also be possible to directly access the TTYwith some
            // kind of library or perhaps something that can be written from
            // scratch.
            //
            // A third option might be to try to hack something together with
            // SSH_ASKPASS and stuff. One would need to make sure there is no
            // TTY present (no idea how to do that) and then set the SSH_ASKPASS
            // and the DISPLAY environment variables to something.
            //
            //
            // Authorization for SVN - This works well altough there might be a
            // secrity issue with it. The password could be seen with a simple
            // comamnd like 'ps' due to the fact that it is sent as an argument
            // to the svn program.
            //
            //
            // Ordinary anonymous SVN - No problem here.
            //
            //
            //
            if(repository.type.equals("SVN")) {
                try {
                    ProcessBuilder pb = null;


                    if(repository.path.startsWith("svn+ssh")) {
                        pb = new ProcessBuilder(binarySVN, "log", repository.path, "--limit", "10", "--incremental");
                    } else if(repository.username != null && repository.password != null) {
                        pb = new ProcessBuilder(binarySVN, "log", repository.path, "--limit", "10", "--incremental",
                                "--username", repository.username, "--password", repository.password);
                    } else {
                        pb = new ProcessBuilder(binarySVN, "log", repository.path, "--limit", "10", "--incremental");
                    }

                    pb = pb.redirectErrorStream(true);
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
                                    messages.add("r" + rev + " - " + user + " - " + date
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

    /**
     *
     */
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

                // Print messages (if any)
                if(!ct.messages.isEmpty()) {
                    ct.repository.channel.sendPrivmsg("New commit in repository:\n");
                    for(String message: ct.messages) {
                        ct.repository.channel.sendPrivmsg(message);
                    }
                }
                threads.remove(ct);
                break;
            }
        }
    }

    /**
     *
     * @param type
     * @param path
     * @param username
     * @param password
     * @param channel
     */
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

}
