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
package silvertrout.plugins.wordiz;

import java.util.Map;
import silvertrout.Channel;
import silvertrout.User;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.HashMap;

public class Wordiz extends silvertrout.Plugin {

    private final String channelName = "#spam";
    private final String fileName = "silvertrout/plugins/wordiz/words";
    private enum State { RUNNING, NOT_RUNNING, WAITING };

    private State state = State.NOT_RUNNING;
    private char[] characters = new char[3];
    private Channel channel;
    private int gameTick;
    private HashSet<String> words = new HashSet<String>();
    private HashSet<String> usedWords = new HashSet<String>();
    private HashMap<User, HashSet<String>> acceptedWords;
    private HashMap<User, HashSet<String>> notAcceptedWords;


    @Override
    public void onLoad(Map<String, String> settings) {
        try{
            BufferedReader stream = new BufferedReader(new FileReader(fileName));
            while(stream.ready()){
                words.add(stream.readLine().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected() {
        if(!getNetwork().isInChannel(channelName))
            getNetwork().getConnection().join(channelName);
    }

    @Override
    public void onJoin(Channel c) {
        if(c.getName().equals(channelName)) channel = c;
    }

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        if(message.contains(" "))return;
        /* break if the channel is not the channel that this plugin uses */
        if(channel == null || !channel.equals(this.channel)) return;
        if(state == State.WAITING) return;
        if(state == State.NOT_RUNNING){
            if(message.equals("!start")){
                channel.sendPrivmsg("Ett nytt spel startar om 5 sekunder.");
                state = State.WAITING;
                gameTick = getNetwork().getTick();
            }
            return;
        }
        /* now we know that state is RUNNING */
        String s = message.toLowerCase();
        if(usedWords.contains(s)) return;
        usedWords.add(s);
        if(isAccepted(s)){
            HashSet<String> hs;
            if(acceptedWords.containsKey(user)) hs = acceptedWords.get(user);
            else hs = new HashSet<String>();
            hs.add(s);
            acceptedWords.put(user, hs);
        } else {
            HashSet<String> hs;
            if(notAcceptedWords.containsKey(user)) hs = notAcceptedWords.get(user);
            else hs = new HashSet<String>();
            hs.add(s);
            notAcceptedWords.put(user, hs);
        }
    }

    private boolean isAccepted(String s){
        for(char c: characters)
            if(!s.contains(c+"")) return false;
        if(!words.contains(s)) return false;
        return true;
    }

    @Override
    public void onTick(int ticks) {
        if(state == State.NOT_RUNNING) return;
        if(state == State.WAITING && ticks - gameTick == 5)
            newGame();
        if(state == State.RUNNING && ticks - gameTick == 10)
            endGame();
    }

    private void endGame(){
        channel.sendPrivmsg("Omgången är slut!");
        for (Map.Entry<User, HashSet<String>> entry : acceptedWords.entrySet()) {
            if(entry.getValue().isEmpty()) continue;
            String w = "";
            int i = 0;
            for(String s: entry.getValue()){
                i += getPoints(s);
                w += "" + s + "("+ getPoints(s) + "), ";
            }
            w = w.substring(0,w.length()-2);
            channel.sendPrivmsg(entry.getKey().getNickname() + ": Godkända ord: " + w + " = " +i);
        }
        for (Map.Entry<User, HashSet<String>> entry : notAcceptedWords.entrySet()) {
            if(entry.getValue().isEmpty()) continue;
            String w = "";
            for(String s: entry.getValue()){
                w += s + ", ";
            }
            w = w.substring(0,w.length()-2);
            channel.sendPrivmsg(entry.getKey().getNickname() + ": Ej godkända ord: " + w);
        }
        state = State.WAITING;
        gameTick = getNetwork().getTick();
        //channel.sendPrivmsg("En ny omgång startar om 5 sekunder.");

    }
    private int getPoints(String s){
        int points = 0;
        for(char c: s.toCharArray()) points+=getPoints(c);
        return points;
    }
    private int getPoints(char c){
        switch (c) {
            case 'q': return 12;
            case 'z': return 10;
            case 'x': case 'c': case 'w': return 8;
            case 'j': case 'y': return 7;
            case 'u': case 'b': case 'ö': case 'p': case 'å': return 4;
            case 'ä': case 'f': case 'v': return 3;
            case 'o': case 'g': case 'k': case 'm': case 'h': return 2;
            default: return 1;
        }
    }
    private void newGame(){
        acceptedWords = new HashMap<User, HashSet<String>>();
        notAcceptedWords = new HashMap<User, HashSet<String>>();
        gameTick = getNetwork().getTick();
        state = State.RUNNING;
        do{
            characters = new char[3];
            for(int i = 0; i < 3; i++){
                characters[i] = (char)(Math.random() * 26 + 'a');
                for(int j = 0; j < i; j++){
                    if(characters[i] == characters[j]){
                        i--;break;
                    }
                }
            }
        }while(getPoints(new String(characters)) > 12);
        channel.sendPrivmsg("Okej, nu kör vi! Lite bokstäver att leka med: " + new String(characters));
    }

}

