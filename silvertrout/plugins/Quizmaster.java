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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Random;
import java.util.Calendar;
import java.util.Comparator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.net.URISyntaxException;

import silvertrout.Channel;
import silvertrout.User;
import silvertrout.Modes;

import silvertrout.commons.game.ScoreManager;
import silvertrout.commons.game.Trophy;
import silvertrout.commons.game.TrophyManager;

public class Quizmaster extends silvertrout.Plugin {

    private enum State { RUNNING, RUNNING_QUESTION, NOT_RUNNING };

    private class Question {
        public String question;
        public String answer;
        public String category;
    }

    // Settings:
    private final int                  voiceInterval        = 60;
    private final int                  hintTime             = 7;
    private final int                  hintCount            = 8;
    private final int                  waitTime             = 3;
    private final int                  rankInterval         = 50;    
    
    // Variables:
    
    private final LinkedList<Question> questions = new LinkedList<Question>();
    private final Random               rand      = new Random();
    private ScoreManager               scoreManager;
    private TrophyManager              trophyManager;
    private String                     channelName;
        
    private Question                   currentQuestion;
    private String                     currentAnswerString;

    private String[]                   grad = 
            {
            "\"Tought\"", "Cell", "Egg", "Embryo", "Fetus", "Neonate", 
            "Toddler", "Child", 
            
            "Preschooler", "Lower Primary School Student", 
            "Upper Primary School Student", "Lower Secondary School Student",
            "Upper Secondary School Student", "Bachelor Student", 
            "Master Student", 
            
            "Volunteer", "Intern", "Receptionist", "Personal Secretary", 
            "Personal Assistant", "Clerk", "Executive Secretary", 
            "Executive Assistant", "Foreman", "Supervisor", "Manager",
            "Superintendent",
            
            "Associate Vice President", "Senior Vice President", 
            "Executive Vice President", "Chief Officer", 
            "Chief Executive Officer", "Chairman of the Board",
            
            "Apprentice", "Apprentice-Companion", "Brother",
            "Commander", "Master", "Grand Master"
            };
            

    
    private int                        startTime;
    private int                        currentTime;
    private int                        endTime;
    
    private int                        statTime             = 0;
    
    private int                        unanswerdQuestions   = 0;
    
    private int                        answerStreak         = 0;
    private String                     answerStreakNickname = new String();
    
    private long                       startMiliTime;
    
    private State                      state                = State.NOT_RUNNING;

    public Quizmaster() {

        String scoresPath   = "/silvertrout/plugins/Quizmaster/Scores/Scores";
        String trophiesPath = "/silvertrout/plugins/Quizmaster/Trophies";

        try {
            scoreManager  = new ScoreManager(new File(this.getClass().getResource(scoresPath).toURI()));
            trophyManager = new TrophyManager(new File(this.getClass().getResource(trophiesPath).toURI()));
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    /** Award trophy to user.
     *
     */
    public void awardTrophy(Trophy t, String nick) {
        if(!trophyManager.haveTrophy(t, nick)) {
            trophyManager.addTrophy(t, nick);
            getNetwork().getConnection().sendPrivmsg(channelName, nick 
                + ": You have earned a trophy - " + t.getName());
        }
    }
    
    public void awardScore(String nick) {
        // Calculate answer time, in seconds:
        long miliSec = Calendar.getInstance().getTimeInMillis() - startMiliTime;
        double time  = ((double)miliSec / 1000.0);
        
        // Calculate winning streak
        if(answerStreakNickname.equals(nick)) {
            answerStreak++;
        } else {
            answerStreakNickname = nick;
            answerStreak         = 1;
        }
        
        // Update scores:
        int oldScore = scoreManager.getTotalScore(nick);
        int oldPos   = scoreManager.getPosition(nick);
        scoreManager.addScore(nick, currentQuestion.category, 1);
        int newScore = scoreManager.getTotalScore(nick);
        int newPos   = scoreManager.getPosition(nick);
        
        // New rank
        if(newScore % rankInterval == 0)
            getNetwork().getConnection().sendPrivmsg(channelName, 
                    "Utmärkt jobbat! Din nya rank är: "+ printNick(nick));
            
        // Print message
        String msg = "Rätt svar var \"" + currentQuestion.answer + "\". ";
        if(answerStreak >= 3)    msg += "(" + answerStreak + " i rad) ";
        if(oldPos == -1)         msg += "(In på listan på placering " + newPos + ") ";
        else if(oldPos < newPos) msg += "(Upp " + (newPos-oldPos) + " placering(ar)) ";
        msg += nick + " (" + time +" sek) har nu " + newScore +"p.";
        getNetwork().getConnection().sendPrivmsg(channelName, msg);
        
        // Check for trophies won
        int year  = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day   = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        
        int hour  = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min   = Calendar.getInstance().get(Calendar.MINUTE);
        // First blood trophy
        if(newScore == 1)
            awardTrophy(trophyManager.getTrophy("First Blood"), nick);
        // Speedster trophy
        if(time < 3.0 && currentQuestion.answer.length() > 5)
            awardTrophy(trophyManager.getTrophy("Speedster"), nick);
        // Chain Reaction
        if(answerStreak >= 5)
            awardTrophy(trophyManager.getTrophy("Chain Reaction"), nick);
        // Chain Overload
        if(answerStreak >= 10)
            awardTrophy(trophyManager.getTrophy("Chain Overload"), nick);
        // Chain Overdose
        if(answerStreak >= 30)
            awardTrophy(trophyManager.getTrophy("Chain Overdose"), nick);
        // Elite!
        if(newScore == 1337)
            awardTrophy(trophyManager.getTrophy("Elite!"), nick);
        // Top Ten
        if(newScore > 100 && newPos <= 10)
            awardTrophy(trophyManager.getTrophy("Top Ten"), nick);
        // Top Three
        if(newScore > 300 && newPos <= 3)
            awardTrophy(trophyManager.getTrophy("Top Three"), nick);
        // Top Dog
        if(newScore > 1000 && newPos == 1)
            awardTrophy(trophyManager.getTrophy("Top Dog"), nick);
        // Säg ett datum, vilket som helst!
        if(month == 5 && day == 29)
            awardTrophy(trophyManager.getTrophy("Säg ett datum, vilket som helst!"), nick);
        // Endurance Master
        if(currentQuestion.answer.length() >= 30)
            awardTrophy(trophyManager.getTrophy("Endurance Master"), nick);
    }
    
    public void loadQuestions(File f) {

        try {
            BufferedReader fr = new BufferedReader(new FileReader(f));
            String category = fr.readLine();
            fr.readLine();
            while(true) {
                Question q = new Question();
                q.category = category;
                q.question = fr.readLine();
                q.answer   = fr.readLine();
                fr.readLine();
                if(q.question == null || q.answer == null)break;
                questions.add(q);
            }
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(questions);
    }
    
    public void newRound(java.util.Collection<String> categories) {
        try{
            File qdir = new File(this.getClass().getResource(
                    "/silvertrout/plugins/Quizmaster/Questions/").toURI());

            for(File d: qdir.listFiles()) {
                //System.out.println("Begin checking directory: " +d.getName());
                if(categories == null || categories.contains(d.getName())) {
                    if(d.isDirectory()) {
                        //System.out.println("Checking directory: " +d.getName());
                        for(File f: d.listFiles()) {
                            if(f.getName().endsWith(".quiz")) {
                                loadQuestions(f);
                                //System.out.println("Added file: " + f.getName());
                            }
                        }
                    }
                }
            }
            
            getNetwork().getConnection().sendPrivmsg(channelName, "En ny omgång"
                    + " startas. Totalt finns " + questions.size() + " frågor.");
            
            unanswerdQuestions = 0;
            state              = State.RUNNING;
            newQuestion();
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    public void endRound() {
        getNetwork().getConnection().sendPrivmsg(channelName, "Omgången är"
                + " slut. Skriv !start för att starta en ny omgång.");
        state = State.NOT_RUNNING;
    }
    
    public void newQuestion() {
        try {
            currentQuestion = questions.removeFirst();
        } catch(java.util.NoSuchElementException e) {
            endRound();
            return;
        }

        currentAnswerString = "";
        for(int i = 0; i < currentQuestion.answer.length(); i++) {
            if(Character.isLetterOrDigit(currentQuestion.answer.charAt(i))) {
                currentAnswerString += '.';
            } else {
                currentAnswerString += currentQuestion.answer.charAt(i);
            }
        }
        startTime     = currentTime;
        startMiliTime = Calendar.getInstance().getTimeInMillis();
        getNetwork().getConnection().sendPrivmsg(channelName, "" + "[" 
                + currentQuestion.category + "] " + currentQuestion.question);
        state   = State.RUNNING_QUESTION;
    }
    
    
    public void endQuestion(String winner) {
        if(winner == null) {
            getNetwork().getConnection().sendPrivmsg(channelName, "Rätt svar"
                    + " var \"" + currentQuestion.answer + "\". Ingen"
                    + " lyckades svara rätt.");
            unanswerdQuestions++;
            answerStreak = 0;
        } else {
            // Award score
            awardScore(winner);
            unanswerdQuestions = 0;
        }
        endTime = currentTime;
        state   = State.RUNNING;
    }
    
    public void printStats(String sender) {    
        String               topten  = new String();
        String               lastone = "Du har inga poäng ='(";
        ScoreManager.Score[] topList = scoreManager.getTop(10);
        
        if(topList.length == 0) {
            topten  = "Ingen är på listan än. Quizza hårdare!";
            lastone = "";
        }
        
        for(int i = 0; i < topList.length; i++) {
            if(topList[i].nick.equals(sender))
                lastone = printNick(sender) +" har " + topList[i].getTotalScore() + ". Bra jobbat!";
            topten += "#" + (i+1) + " " + topList[i].nick + " " + topList[i].getTotalScore() + "p - ";
        }
        
        
        if(topList.length > 10) {
            int pos   = scoreManager.getPosition(sender);
            int score = scoreManager.getTotalScore(sender);
            if(pos != -1)lastone = "Du har " + score 
                    + " och ligger på placering #" + pos;
        }
        getNetwork().getConnection().sendPrivmsg(channelName, "Top 10: " 
                + topten + lastone);
    }
    
    @Override
    public void onPrivmsg(User user, Channel channel, String message) {

        if(channel != null && channel.getName().equalsIgnoreCase(channelName)) {

            if(state == State.RUNNING_QUESTION) {
                // Answer to question
                if(message.compareToIgnoreCase(currentQuestion.answer) == 0) {
                    endQuestion(user.getNickname());
                }
            } else if(state == State.NOT_RUNNING) {
                // Start new round
                if(message.startsWith("!start")) {
                    String[] cat = message.substring(6).split("\\s");
                    newRound(null);
                }
            }
            
            if(message.equals("!stats")) {
                if(currentTime - statTime > 20) {
                    printStats(user.getNickname());
                    statTime = currentTime;
                }
            }
            else if(message.equals("!help")) {
                if(currentTime - statTime > 20)
                    getNetwork().getConnection().sendPrivmsg(channelName, 
                              "Skriv !start för att starta och !stats för att"
                            + " se tio i topp-listan och din egna poäng. För"
                            + " att titta vilka trophies du har kan du använda"
                            + " !trophies. Om du vill visa denna hjälp, skriv"
                            + " !help.");
            }
            else if(message.startsWith("!suggest")) {
                // TODO!
            }
            else if(message.startsWith("!report")) {
                // TODO!
            }
            else if(message.equals("!trophies")) {
            
                int have = trophyManager.getTrophies(user.getNickname()).size();
                int tot    = trophyManager.getTrophies().size();
            
                String msg = "Du har trophéerna: ";
                for(Trophy t: trophyManager.getTrophies(user.getNickname())) {
                    msg += t.getName() + ", ";
                }
                msg += have + "/" + tot + " - forsätt samla!";
                getNetwork().getConnection().sendPrivmsg(channelName, msg);
            }
            else if(message.equals("!listtrophies")) {
            
                int tot    = trophyManager.getTrophies().size();
                String msg = "Följande trophéer finns: ";
                for(Trophy t: trophyManager.getTrophies()) {
                    msg += t.getName() + ", ";
                }
                msg +=tot + " stycken - samla alla!";
                getNetwork().getConnection().sendPrivmsg(channelName, msg);
            }
        }
    }
    
    public void giveHint() {
        if(currentTime == startTime + hintTime){
            getNetwork().getConnection().sendPrivmsg(channelName, currentAnswerString);
        } else {
            int l = currentAnswerString.length();
            int h = (int)Math.ceil((double)l / (hintCount * 2));

            for(int i = 0; i < h && i < 150; i++) {
                int  p = rand.nextInt(l);
                char c = currentQuestion.answer.charAt(p);
                
                if(currentAnswerString.charAt(p) == c) {
                    h++; continue;
                }
                currentAnswerString = currentAnswerString.substring(0, p) + c
                        + currentAnswerString.substring(p + 1, l);

            }
            
            if(currentAnswerString.equals(currentQuestion.answer)) {
                endQuestion(null);
            } else {
                getNetwork().getConnection().sendPrivmsg(channelName, currentAnswerString);
            }
        }
    }
    
    @Override
    public void onTick(int ticks) {
        currentTime = ticks;
        //System.out.println(currentTime + ": " + state);
        if(state == State.RUNNING_QUESTION) {        
            // If we have a question that no one have answered in a while
            if(currentTime > startTime + hintTime * hintCount) {
                endQuestion(null);
                if(unanswerdQuestions >= 5) { 
                    endRound();
                } else {
                    newQuestion();
                }
            // Or if it is time to give a hint
            } else if(currentTime - startTime == hintTime * hintCount) {
                endQuestion(null);
            } else if((currentTime - startTime) % hintTime == 0) {
                giveHint();
            }         
        
        } else if(state == State.RUNNING) {
            // Time for a new question
            if(currentTime - endTime == waitTime) {
                newQuestion();
            }            
        }
        
        // Do every minute
        if(ticks % voiceInterval == 0) {
            
            // Only voice if we are in the channel and are an operator
            if(getNetwork().existsChannel(channelName)) {
            
                Channel channel  = getNetwork().getChannel(channelName);
                User    myUser   = getNetwork().getMyUser();
                boolean operator = channel.getUsers().get(myUser).haveMode('o');
                
                if(operator) {
                    LinkedList<String> f     = new LinkedList<String>();
                    Map<User, Modes>   users = channel.getUsers();
                    
                    for(User u: users.keySet()) {
                        if(users.get(u).haveMode('v')){
                            // the user do have voice
                            if(!scoreManager.isTop(10, u.getNickname()))
                                f.add("-v " + u.getNickname() + " ");
                        } else {
                            // the user do not have voice
                            if(scoreManager.isTop(10, u.getNickname()))
                                f.add("+v " + u.getNickname() + " ");
                        }
                    }
                    
                    for(int i = 0; i < f.size(); i += 4) {
                        String m = new String();
                        for(int j = i; j < f.size() && j < i + 4; j++) {
                            m += f.get(j);
                        }
                        getNetwork().getConnection().sendRaw("MODE " + channelName + " " + m);
                    }
                }
            }
        }
    }
    
    private String printNick(String nick){

        int s = scoreManager.getTotalScore(nick) / rankInterval;
        if(s > 0) {
            if(s > grad.length)
                return grad[grad.length-1] + " " + nick;
            else if(s > 0)
                return grad[s-1] + " " + nick;
        }
        return nick;
    }
    
    @Override
    public void onConnected() {
        // Join quiz channel:
        if(!getNetwork().existsChannel(channelName)) {
            getNetwork().getConnection().join(channelName);
        }
    }
    
    @Override
    public void onLoad(Map<String,String> settings){
        channelName = settings.get("channel");
        if(channelName == null || !channelName.startsWith("#")) channelName = "#superquizNG";
    }
}
