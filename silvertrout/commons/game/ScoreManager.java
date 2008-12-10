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
package silvertrout.commons.game;

import java.util.Collections;
import java.util.LinkedList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class ScoreManager {

    public class Score implements Comparable<Score> {

        public String nick;
        public int score;

        @Override
        public int compareTo(Score s) {
            return s.score - score;
        }
        ;
    }
    private LinkedList<Score> scores;
    private File scoreFile;

    public ScoreManager(File scoreFile) {
        this.scoreFile = scoreFile;
        this.scores = new LinkedList<Score>();

        loadScores(this.scoreFile);
    }

    private void loadScores(File f) {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(f));

            while (true) {
                String nick = fr.readLine();
                String score = fr.readLine();
                String x = fr.readLine();
                if (nick == null || score == null || x == null) {
                    break;
                }

                Score s = new Score();
                s.nick = nick;
                s.score = Integer.parseInt(score);
                scores.add(s);
            }
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
                fw.write(s.nick + "\n" + String.valueOf(s.score) + "\n\n");
            }
            fw.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public int getScore(String nickname) {
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).nick.equals(nickname)) {
                Score s = scores.get(i);
                return s.score;
            }
        }
        return 0;
    }

    public int getPosition(String nickname) {
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).nick.equals(nickname)) {
                Score s = scores.get(i);
                return i + 1;
            }
        }
        return -1;
    }

    public Score[] getTop(int amount) {
        if (scores.size() < amount) {
            amount = scores.size();
        }
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

    public void addScore(String nickname, int score) {
        setScore(nickname, score + getScore(nickname));
    }

    public void setScore(String nickname, int score) {
        // Update old score
        boolean found = false;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).nick.equals(nickname)) {
                Score s = scores.get(i);
                s.score = score;
                scores.set(i, s);
                found = true;
                break;
            }
        }
        // Or add a new one if not found
        if (!found) {
            Score s = new Score();
            s.nick = nickname;
            s.score = score;
            scores.add(s);
        }

        // Resort and save
        Collections.sort(scores);
        saveScores(scoreFile);
    }
}

