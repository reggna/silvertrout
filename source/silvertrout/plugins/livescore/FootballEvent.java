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
    public boolean equals (Object o){
        FootballEvent ev = (FootballEvent)o;
        if(!this.matchtime.equals(ev.matchtime))
            return false;
        if (!this.score.equals(ev.score))
            return false;
        if ( (this.yellowcard && !ev.yellowcard) || (!this.yellowcard && ev.yellowcard) )
            return false;
        if ( (this.redcard && !ev.redcard) || (!this.redcard && ev.redcard) )
            return false;
        if ( (this.goal && !ev.goal) || (!this.goal && ev.goal) )
            return false;
        if ( (this.gamestatechange && !ev.gamestatechange) || (!this.gamestatechange && ev.gamestatechange) )
            return false;
        return true;
    }
}
