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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.io.IOException;

public class ScoreManager {

    /**
     *
     */
    public class Score implements Comparable<Score> {

        public String                   nick;
        public HashMap<String, Integer> score = new HashMap<String, Integer>();

        @Override
        public int compareTo(Score s) {
            return s.getTotalScore() - getTotalScore();
        };
        
        public int getTotalScore() {
            int totalScore = 0;
            for(int s: score.values()) {
                totalScore += s;
            }
            return totalScore;
        }
        
        public int getScore(String part) {
            if(score.containsKey(part)) {
                return score.get(part);
            }        
            return 0;
        }
    }
    
    private final LinkedList<Score> scores    = new LinkedList<Score>();
    private       File              scoreFile;

    public ScoreManager(File scoreFile) {
        if(!scoreFile.exists()){
            try{
                scoreFile.getParentFile().mkdirs();
                scoreFile.createNewFile();
            } catch (IOException e){
                System.out.println("Unable to create score file");
                e.printStackTrace();
            }
        }
        this.scoreFile = scoreFile;
        loadScores(this.scoreFile);
    }

    private void loadScores(File f) {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(f));

            while (true) {
                String nick = fr.readLine();                
                if(nick == null || nick.equals(""))break;
                
                Score s = new Score();
                s.nick = nick;
                
                System.out.println("'" + nick + "'");
                
                while(true) {
                    String   score      = fr.readLine();
                    if(score == null || score.equals("")) break;
                    String[] scoreParts = score.split("\t");
                    
                    String   part       = scoreParts[0].trim();
                    int      partScore  = Integer.parseInt(scoreParts[1].trim());
                    
                    s.score.put(part, partScore);
                }
                scores.add(s);
            }
            System.out.println("loaded " + scores.size() + " scores");
            
            fr.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            Collections.sort(scores);
        }
    }

    public void saveScores(File f) {
        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(f));
            for (Score s : scores) {
                fw.write(s.nick + "\n");
                for(Map.Entry<String, Integer> e: s.score.entrySet()) {
                    fw.write(e.getKey() + "\t" + e.getValue() + "\n");
                }
            }
            fw.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public int getScore(String nickname, String what) {
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).nick.equals(nickname)) {
                Score s = scores.get(i);
                if(s.score.containsKey(what)) {
                    return s.score.get(what);
                }                
            }
        }
        return 0;
    }
    
    public int getTotalScore(String nickname) {
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).nick.equals(nickname)) {
                Score s = scores.get(i);
                return s.getTotalScore();
            }
        }
        return 0;
    }

    public int getPosition(String nickname) {
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).nick.equals(nickname)) {
                return i + 1;
            }
        }
        return -1;
    }

    public Score[] getTop(int amount) {
        // We cant fetch more then we have
        if (scores.size() < amount)amount = scores.size();
        // Fetching...
        Score[] topScores = new Score[amount];
        for (int i = 0; i < amount; i++) {
            topScores[i] = scores.get(i);
        }
        return topScores;
    }

    public boolean isTop(int amount, String nick) {
        if (scores.size() < amount) {
            amount = scores.size();
        }
        for (int i = 0; i < amount; i++) {
            if (scores.get(i).nick.equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void addScore(String nickname, String what, int score) {
        setScore(nickname, what, score + getScore(nickname, what));
    }

    public void setScore(String nickname, String what, int score) {
        // Update old score
        boolean found = false;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).nick.equals(nickname)) {
                Score s = scores.get(i);
                s.score.put(what, score);
                found = true;
                break;
            }
        }
        // Or add a new one if not found
        if (!found) {
            Score s = new Score();
            s.nick = nickname;
            s.score.put(what, score);
            scores.add(s);
        }

        // Re-sort and save
        Collections.sort(scores);
        saveScores(scoreFile);
    }
}

