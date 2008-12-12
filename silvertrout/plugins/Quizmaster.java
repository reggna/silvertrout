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
package silvertrout.plugins;

import java.util.Collections;
import java.util.Map;
import java.util.LinkedList;
import java.util.Random;
import java.util.Calendar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import silvertrout.Channel;
import silvertrout.User;
import silvertrout.Modes;

import silvertrout.commons.game.ScoreManager;

public class Quizmaster extends silvertrout.Plugin {

    public class Question {

        public String question;
        public String answer;
    }
    private LinkedList<Question> questions;
    private ScoreManager scoreManager;
    private Random rand;
    private Question currentQuestion;
    private String currentAnswerString;
    private String channelName = "#superquiz";
    private String[] grad = {
        "Untersturmführer", "Obersturmführer",
        "Hauptsturmführer", "Sturmbannführer",
        "Obersturmbannführer", "Standartenführer",
        "Oberführer", "Brigadeführer",
        "Gruppenführer", "Obergruppenführer",
        "Oberstgruppenführer", "Reichsführer-SS"
    };
    private int questionTime = 60;
    private int hintTime = 10;
    private int startTime;
    private int currentTime;
    private int statTime = 0;
    private int unanswerdQuestions;
    private long startMiliTime;
    private boolean running;

    public Quizmaster() {

        questions = new LinkedList<Question>();
        scoreManager = new ScoreManager(new File("/home/tigge/Personal" + "/Programming/jbt/plugins/Quizmaster_scores"));

        running = false;
        rand = new Random();
    }

    public void awardScore(String nick) {
        boolean found = false;
        long miliSec = Calendar.getInstance().getTimeInMillis() - startMiliTime;
        double time = ((double) miliSec / 1000.0);

        scoreManager.addScore(nick, 1);
        int newScore = scoreManager.getScore(nick);

        if (newScore % 100 == 0) {
            getNetwork().getConnection().sendPrivmsg(channelName, "Einen guten Job! " + printNick(nick));
        }

        getNetwork().getConnection().sendPrivmsg(channelName, "Rätt svar var \"" + currentQuestion.answer + "\". " + nick + " (" + time + " sek) har nu " + newScore + "p.");
    }

    public void loadQuestions(File f) {

        try {
            BufferedReader fr = new BufferedReader(new FileReader(f));
            String category = fr.readLine();
            fr.readLine();
            while (true) {
                Question q = new Question();
                q.question = "[" + category + "] " + fr.readLine();
                q.answer = fr.readLine();
                fr.readLine();
                if (q.question == null || q.answer == null) {
                    break;
                }
                questions.add(q);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(questions);
    }

    public void newRound() {

        File qdir = new File(
                this.getClass().getResource(
                "/jbt/plugins/Quizmaster/Questions/").getFile());
        //System.out.println(qdir);

        //System.out.println(qdir.isDirectory());
        //System.out.println(qdir.exists());
        //System.out.println(qdir.canRead());

        File[] qfiles = qdir.listFiles();
        //System.out.println(qfiles);
        for (int i = 0; i < qfiles.length; i++) {
            if (qfiles[i].getName().endsWith(".quiz")) {
                loadQuestions(qfiles[i]);
            }
        }

        getNetwork().getConnection().sendPrivmsg(channelName, "En ny omgång starts med 20 utvalda" + " frågor av " + questions.size() + " totalt.");
        while (questions.size() > 20) {
            questions.removeLast();
        }

        newQuestion();
        running = true;
        unanswerdQuestions = 0;
    }

    public void endRound() {
        getNetwork().getConnection().sendPrivmsg(channelName, "Omgången är slut. Skriv !start för att" + " starta en ny omgång.");
        running = false;
    }

    public void newQuestion() {
        try {
            currentQuestion = questions.removeFirst();
        } catch (java.util.NoSuchElementException e) {
            endRound();
            return;
        }

        currentAnswerString = "";
        for (int i = 0; i < currentQuestion.answer.length(); i++) {
            if (Character.isLetterOrDigit(currentQuestion.answer.charAt(i))) {
                currentAnswerString += '.';
            } else {
                currentAnswerString += currentQuestion.answer.charAt(i);
            }
        }
        startTime = currentTime;
        startMiliTime = Calendar.getInstance().getTimeInMillis();
        getNetwork().getConnection().sendPrivmsg(channelName, "" + currentQuestion.question);
        getNetwork().getConnection().sendPrivmsg(channelName, currentAnswerString);
    }

    public void endQuestion(String winner) {

        if (winner == null) {
            getNetwork().getConnection().sendPrivmsg(channelName, "Rätt svar var \"" + currentQuestion.answer + "\". Ingen lyckades svara rätt.");
            unanswerdQuestions++;
        } else {

            // Award score
            awardScore(winner);
            unanswerdQuestions = 0;
        }

    }

    public void printStats(String sender) {
        String topten = new String();
        String lastone = "Du har inga poäng. :(";
        ScoreManager.Score[] topList = scoreManager.getTop(10);

        for (int i = 0; i < 10; i++) {
            if (topList[i].nick.equals(sender)) {
                lastone = printNick(sender) + " har " + topList[i].score + ". Gut gemacht!";
            }
            topten += "#" + (i + 1) + " " + topList[i].nick + " " + topList[i].score + "p - ";
        }


        if (topList.length > 10) {
            int pos = scoreManager.getPosition(sender);
            int score = scoreManager.getPosition(sender);
            if (pos != -1) {
                lastone = "Du har " + score + " och ligger på placering #" + pos;
            }
        }
        getNetwork().getConnection().sendPrivmsg(channelName, "Top10: " + topten + lastone);
    }

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {

        if (channel != null && channel.getName().equals(channelName)) {

            if (running) {
                if (message.compareToIgnoreCase(currentQuestion.answer) == 0) {
                    endQuestion(user.getNickname());
                    newQuestion();
                }
            } else {
                if (message.equals("!start")) {
                    newRound();
                // Start round of x questions
                }
            }
            if (message.equals("!stats")) {
                if (currentTime - statTime > 60) {
                    printStats(user.getNickname());
                    statTime = currentTime;
                }
            } else if (message.equals("!help")) {
                if (currentTime - statTime > 60) {
                    getNetwork().getConnection().sendPrivmsg(channelName, "Jag gillar reggna");
                }
            }
        }
    }

    public void giveHint() {
        int l = currentAnswerString.length();
        int h = (int) Math.ceil((double) l / 16.0);

        for (int i = 0; i < h && i < 150; i++) {
            int p = rand.nextInt(l);
            char c = currentQuestion.answer.charAt(p);

            if (currentAnswerString.charAt(p) == c) {
                h++;
                continue;
            }
            currentAnswerString = currentAnswerString.substring(0, p) + c + currentAnswerString.substring(p + 1, l);

        }

        if (currentAnswerString.equals(currentQuestion.answer)) {
            endQuestion(null);
            newQuestion();
        } else {
            getNetwork().getConnection().sendPrivmsg(channelName, currentAnswerString);
        }
    }

    @Override
    public void onTick(int ticks) {
        currentTime = ticks;

        if (running) {
            if (currentTime > startTime + questionTime) {
                endQuestion(null);
                if (unanswerdQuestions >= 5) {
                    endRound();
                } else {
                    newQuestion();
                }
            } else if ((currentTime - startTime) % hintTime == 0) {
                giveHint();
            }
        }

        // Do every minute
        if (ticks % 60 == 0) {

            // Only voice if we are in the channel and are an operator
            if (getNetwork().existsChannel(channelName)) {

                Channel channel = getNetwork().getChannel(channelName);
                User myUser = getNetwork().getMyUser();
                boolean operator = channel.getUsers().get(myUser).haveMode('o');

                if (operator) {
                    LinkedList<String> f = new LinkedList<String>();
                    Map<User, Modes> users = channel.getUsers();

                    for (User u : users.keySet()) {
                        if (users.get(u).haveMode('v')) {
                            // the user do have voice
                            if (!scoreManager.isTop(10, u.getNickname())) {
                                f.add("-v " + u.getNickname() + " ");
                            }
                        } else {
                            // the user do not have voice
                            if (scoreManager.isTop(10, u.getNickname())) {
                                f.add("+v " + u.getNickname() + " ");
                            }
                        }
                    }

                    for (int i = 0; i < f.size(); i += 4) {
                        String m = new String();
                        for (int j = i; j < f.size() && j < i + 4; j++) {
                            m += f.get(j);
                        }
                        getNetwork().getConnection().sendRaw("MODE " + channelName + " " + m);
                    }
                }
            }
        }
    }

    /*private boolean isTopTen(String nick) {
    for(int i = 0; i < 10; i++) {
    if(scores.get(i).nick.equals(nick))
    return true;
    }
    return false;
    }*/
    private String printNick(String nick) {

        int s = scoreManager.getScore(nick) / 100;
        if (s > 0) {
            if (s > grad.length) {
                return grad[grad.length - 1] + " " + nick;
            } else if (s > 0) {
                return grad[s - 1] + " " + nick;
            }
        }
        return nick;
    }

    @Override
    public void onConnected() {
        // Join quiz channel:
        if (!getNetwork().existsChannel(channelName)) {
            getNetwork().getConnection().join(channelName);
        }
    }
}
