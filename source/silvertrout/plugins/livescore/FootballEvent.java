/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package silvertrout.plugins.livescore;

/**
 *
 * @author Hasse
 */
public class FootballEvent {

    String matchtime = "";
    String score = null;
    String playername = "";
    boolean goal = false;
    boolean yellowcard = false;
    boolean redcard = false;
    boolean gamestatechange = false;

    public FootballEvent(String matchtime, String score, String playername, String imglink) {
        this.matchtime = matchtime;
        this.score = score;
        this.playername = playername;
        if (imglink.endsWith("goal.gif"))
            goal = true;
        else if (imglink.endsWith("red.gif"))
            redcard = true;
        else if (imglink.endsWith("yellow.gif"))
            yellowcard = true;
        else
            gamestatechange = true;
    }
}
