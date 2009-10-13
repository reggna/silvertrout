/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package silvertrout.plugins.livescore;

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

    public FootballGame(String country, String league, String hometeam, String awayteam, String gametime, String link, String result) {
        this.country = country;
        this.league = league;
        this.hometeam = hometeam;
        this.awayteam = awayteam;
        this.gametime = gametime;
        this.link = link;
        this.result = result;
    }
}
