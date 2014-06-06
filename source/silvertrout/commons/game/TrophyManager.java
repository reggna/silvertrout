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
package silvertrout.commons.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: heavy changes needed
/**
 *
 **
 */
public class TrophyManager {

    private List<Trophy> trophyList;
    private Map<String, List<Trophy>> trophyUser;
    private File trophyDirectory;
    private File trophyListFile;
    private File trophyUserFile;

    /**
     *
     * @param trophyDirectory
     */
    public TrophyManager(File trophyDirectory) {
        this.trophyDirectory = trophyDirectory;

        this.trophyList = new ArrayList<Trophy>();
        this.trophyUser = new HashMap<String, List<Trophy>>();

        trophyListFile = new File(trophyDirectory, "TrophyList");
        trophyUserFile = new File(trophyDirectory, "TrophyUser");

        loadTrophyList(trophyListFile);
        loadTrophyUser(trophyUserFile);
    }

    // Get trophies
    /**
     *
     * @param name
     * @return
     */
    public Trophy getTrophy(String name) {
        for (Trophy t : trophyList) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public List<Trophy> getTrophies() {
        return trophyList;
    }

    /**
     *
     * @param nickname
     * @return
     */
    public List<Trophy> getTrophies(String nickname) {
        if (trophyUser.get(nickname) == null) {
            return new ArrayList<Trophy>();
        }
        return trophyUser.get(nickname);
    }

    /**
     *
     * @param t
     * @param nickname
     * @return
     */
    public boolean haveTrophy(Trophy t, String nickname) {
        if (getTrophies(nickname).isEmpty()) {
            return false;
        }
        return getTrophies(nickname).contains(t);
    }

    // Add trophies
    /**
     *
     * @param t
     * @param nickname
     */
    public void addTrophy(Trophy t, String nickname) {
        System.out.println("Adding trophy " + t + ", " + nickname);
        if (trophyUser.get(nickname) == null) {
            List<Trophy> l = new ArrayList<Trophy>();
            trophyUser.put(nickname, l);
        }
        trophyUser.get(nickname).add(t);
        saveTrophyUser(trophyUserFile);
    }

    /**
     *
     * @param name
     * @param nickname
     */
    public void addTrophy(String name, String nickname) {
        addTrophy(getTrophy(name), nickname);
    }

    /**
     *
     * @param f
     */
    public void loadTrophyList(File f) {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(f));

            while (true) {

                String name = fr.readLine();
                String archivment = fr.readLine();
                String valueStr = fr.readLine();

                Trophy.Value value = Trophy.Value.BRONZE;
                for (Trophy.Value v : Trophy.Value.values()) {
                    if (v.name().equalsIgnoreCase(valueStr)) {
                        value = v;
                    }
                }

                trophyList.add(new Trophy(name, archivment, value));

                if (fr.readLine() == null) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param f
     */
    public void saveTrophyList(File f) {
        // Not implemented yet
    }

    /**
     *
     * @param f
     */
    public void loadTrophyUser(File f) {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(f));
            while (true) {
                String nickname = fr.readLine();
                if (nickname == null) {
                    break;
                }
                while (true) {
                    String trophyName = fr.readLine();
                    if (trophyName == null || trophyName.equals("")) {
                        break;
                    }
                    addTrophy(trophyName, nickname);
                }
            }
            fr.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param f
     */
    public void saveTrophyUser(File f) {
        System.out.println("Saving trophies to " + f);
        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(f));
            for (Map.Entry<String, List<Trophy>> e : trophyUser.entrySet()) {
                fw.write(e.getKey() + "\n");
                for (Trophy t : e.getValue()) {
                    fw.write(t.getName() + "\n");
                }
                fw.write("\n");
            }
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(TrophyManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}


