/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package silvertrout.plugins.livescore;

import java.util.ArrayList;

/**
 *
 * @author Hasse
 */
public class FootballGame {

    String country = "";
    String league = "";
    String hometeam = "";
    String awayteam = "";
    String gametime = "";
    String link = null;
    String result = "";
    ArrayList<FootballEvent> events;

    public FootballGame(String country, String league, String hometeam, String awayteam, String gametime, ArrayList<FootballEvent> events, String result) {
        this.country = country;
        this.league = league;
        this.hometeam = hometeam;
        this.awayteam = awayteam;
        this.gametime = gametime;
        this.events = events;
        this.result = result;
    }
}
