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


import java.util.LinkedList;
import java.util.Map;
import java.util.Arrays;



import silvertrout.Channel;
import silvertrout.User;
import silvertrout.commons.game.UnoDeck;
import silvertrout.commons.game.UnoCard;
import silvertrout.commons.Color;

/**
 * JBT-plugin for the the classical game UNO.
 *
 * Start the game by type "!uno"
 * Join the game by typ "join"
 * Individual cars are delt by private message
 * Play a card with "play" or "p"
 * Draw a card with "draw" or "d"
 * Skipp your turn with "pass", "skipp" or "s"
 *
 * 
 * @author reggna
 * @version Beta 0.1
 */
public class UNO extends silvertrout.Plugin {

    /* the name of the channel in which the game will run: */
    private String channelName;

    private enum State { IDLE, WAITING, GAME}
    private State state = State.IDLE;

    /* the tick in which the game was initiated: */
    private int startTick = 0;
    /* the tick in which the latest move was done */
    private int latestTick = 0;

    /* is the turn order in reverse? */
    private boolean reverse = false;

    private LinkedList<Player> players;

    private UnoCard topCard;
    private UnoDeck deck;
    private int drawnCards = 0;
    private final int maxDrawnCards = 1;

    @Override
    public void onPrivmsg(User user, Channel channel, String message) {
        if(message.equals("!unohelp")){
            user.sendPrivmsg("Type !uno to initiate a new game of UNO. This cann't be done while a game is still running. End the current game with !unoend.");
            user.sendPrivmsg("Playing UNO is easy. All you have to do is type 'p color rank' (eg 'p y 7' would play a yellow 7) to play a card.");
            user.sendPrivmsg("If you are unable (or unwilling) to match the card lying on top of the pile, type 'd' to draw one (1) new card. You may then play the card, or type 's' to skip your turn.");
        }else if(message.equals("!unoend") && state != State.IDLE){
            state = State.IDLE;
            getNetwork().getChannel(channelName).sendPrivmsg("Game ended by " + user.getNickname() + ".");
        }else if(channel.getName().equals(channelName)){
            if(state == State.IDLE){
                if(message.equals("!uno")){
                    channel.sendPrivmsg("A new game will start in 20 s.");
                    startGame();
                    channel.sendPrivmsg("Type !join to join the game.");
                }
            }else if(state == State.WAITING){
                if(message.equals("!join")){
                    /* make sure there is no duplicated players */
                    for(Player p: players){
                        if(p.user.getNickname().equals(user.getNickname()))
                            return;
                    }
                    addPlayer(user);
                    //channel.giveVoice(user);
                }
            }else if(state == State.GAME){
                if(message.equals("t")){
                    for(Player p: players)
                        channel.sendPrivmsg(p.user.getNickname()+ ": " + p.cards.size());
                }
                if(user.getNickname().equals(players.getFirst().user.getNickname())){
                    if(topCard.getColor() == -1){
                        if(message.startsWith("c ")){
                            int color = UnoCard.getColor(message.charAt(2));
                            topCard.setColor(color);
                            if(topCard.getColor() != -1){
                                if(topCard.getRank()==14)
                                    skip();
                                else
                                    nextPlayer();
                            }
                                
                        }
                        return;
                    }
                    if(message.startsWith("p ")){ // someone is laying a card
                        try{
                            UnoCard c = new UnoCard(message.substring(2));
                            /* check if the player have that card */
                            if(players.getFirst().cards.contains(c)){
                                /* check if the card match the card that most recently was played */
                                if(topCard.match(c)){
                                    switch(players.getFirst().cards.size()){
                                        case 1: endGame(); return;
                                        case 2: channel.sendPrivmsg(user.getNickname() + " has UNO!");
                                        default: handleCard(c);
                                    }
                                }
                            } //else getNetwork().getChannel(channelName).sendPrivmsg("You do not have: " +c);
                        }catch(Exception e){
                            //e.printStackTrace();
                            return;
                        }
                    }else if(message.equals("d")){
                        if(drawnCards < maxDrawnCards){
                            drawnCards++;
                            UnoCard c = deck.drawCard();
                            players.getFirst().cards.add(c);
                            players.getFirst().user.sendPrivmsg("You picked up: " +c.toString());
/*                            if(drawnCards == 1)
                                channel.sendPrivmsg(user.getNickname() + " has drawn "+ drawnCards +" card");
                            else
                                channel.sendPrivmsg(user.getNickname() + " has drawn "+ drawnCards +" card"); */
                        }
                    }else if(message.equals("s")){
                        if(drawnCards >= maxDrawnCards)
                            nextPlayer();
                    }
                    //channel.sendPrivmsg(message);
                }
            }
        }
    }

    private void startGame(){
        players = new LinkedList<Player>();
        deck = new UnoDeck();
        /*for(Object c: deck.toArray()){
            System.out.println(c);
        }*/

        startTick = getNetwork().getTick();
        latestTick = getNetwork().getTick();
        reverse = false;
        
        drawnCards = 0;
        topCard = deck.drawCard();

        state = State.WAITING;
    }

    private void handleCard(UnoCard c){
        System.out.println(c + " is beeing handled");
        deck.throwCard(topCard);
        players.getFirst().cards.remove(c);
        topCard = c;
        if(c.getRank() == 14){ /* wd4 */
            UnoCard[] cards = { deck.drawCard(),deck.drawCard(),
                deck.drawCard(), deck.drawCard() };
            players.get(1).cards.addAll(Arrays.asList(cards));
            players.get(1).user.sendPrivmsg("You picked up: "+ UnoCard.toString(cards));
            getNetwork().getChannel(channelName).sendPrivmsg("Choose color: "+ Color.red("c r") + Color.white(",, ") + Color.blue("c b") + Color.white(",, ") + Color.yellow("c y") + Color.white(" or ") + Color.green("c g"));
        }else if(c.getRank() == 10){ /* skip */
            skip();
        } else if(c.getRank() == 11){ /* draw two and skip */
            UnoCard[] cards = { deck.drawCard(), deck.drawCard() };
            players.get(1).cards.addAll(Arrays.asList(cards));
            players.get(1).user.sendPrivmsg("You picked up: "+ UnoCard.toString(cards));
            skip();
        } else if(c.getRank() == 12){ /* reverse */
            reverse = !reverse;
            if(players.size()==2){
                skip();
            }
            if(reverse) getNetwork().getChannel(channelName).sendPrivmsg("<--");
            else getNetwork().getChannel(channelName).sendPrivmsg("-->");
            nextPlayer();
        } else if(c.getRank() == 13){ /* wild */
            getNetwork().getChannel(channelName).sendPrivmsg("Choose color: "+ Color.red("c r") + ", " + Color.blue("c b") +", " + Color.yellow("c y") + " or "+ Color.green("c g"));
        }else{
            nextPlayer();
        }
    }

    private void addPlayer(User u){
        getNetwork().getChannel(channelName).sendPrivmsg(u.getNickname() + " has joined the game.");
        Player p = new Player(u);
        for(int i = 0; i < 7; i++){
            p.cards.add(deck.drawCard());
        }
        p.sendHand();
        players.add(p);
    }

    private void nextPlayer(){
        players.getFirst().sendHand();
        drawnCards = 0;
        if(reverse){
            players.addFirst(players.removeLast());
        }else{
            players.addLast(players.removeFirst());
        }
        play();
    }
    private void skip(){
        if(reverse) players.addFirst(players.removeLast());
        else players.addLast(players.removeFirst());
        getNetwork().getChannel(channelName).sendPrivmsg(players.getFirst().user.getNickname()+" was skipped");
        nextPlayer();
    }

    private void play(){
        latestTick = getNetwork().getTick();
        getNetwork().getChannel(channelName).sendPrivmsg(players.getFirst().user.getNickname()+" is up: " +topCard);
        //players.getFirst().sendHand();
        
    }

    private void endGame(){
        getNetwork().getChannel(channelName).sendPrivmsg(players.getFirst().user.getNickname() +" has won the game.");
        int score = 0;
        for(int i = 1; i < players.size(); i++){
            getNetwork().getChannel(channelName).sendPrivmsg(players.get(i).user.getNickname() +": " + players.get(i).toString() + " ("+ players.get(i).getTotalHandScore() + ")");
            score += players.get(i).getTotalHandScore();
        }
        getNetwork().getChannel(channelName).sendPrivmsg("Total score:" +score);
        state = State.IDLE;
        /*for(Player p: players)
            getNetwork().getChannel(channelName).deVoice(p.user);*/
    }

    @Override
    public void onTick(int ticks){
        if(state == State.WAITING){
            if(ticks - startTick == 20){
                if(players.size()<=1){
                    getNetwork().getChannel(channelName).sendPrivmsg("Not enought players, game ended.");
                    state = State.IDLE;
                } else{
                    String cPlayers = "";
                    for(Player p: players)
                        cPlayers+=p.user.getNickname()+" ";
                    getNetwork().getChannel(channelName).sendPrivmsg("Starting a new game with: "+ cPlayers);
                    state = State.GAME;
                    latestTick = ticks;
                    startTick = ticks;
                    play();
                }
            }
        }else if(state == State.GAME){
            if(ticks - latestTick == 30){
                getNetwork().getChannel(channelName).sendPrivmsg("Time's up!");
                UnoCard c = deck.drawCard();
                players.getFirst().cards.add(c);
                players.getFirst().user.sendPrivmsg("You picked up: " +c.toString());
                nextPlayer();
            }
        }
    }
    @Override
    public void onLoad(Map<String, String> settings){
        channelName = settings.get("channel");
        if(channelName == null || !channelName.startsWith("#")) channelName = "#uno";
    }

    @Override
    public void onConnected() {
        // Join channel:
        if(!getNetwork().existsChannel(channelName)) {
            getNetwork().getConnection().join(channelName);
        }
    }

    @Override
    public void onPart(User user, Channel channel, String partMessage) {
        if(channel.getName().equals(channelName)){
            for(int i = 0; i < players.size(); i++){
                if(players.get(i).user.equals(user)){
                    players.remove(i);
                    getNetwork().getChannel(channelName).sendPrivmsg(user.getNickname() + " has left the game.");
                    return;
                }
            }
        }
    }
    
    private class Player{
        public LinkedList<UnoCard> cards = new LinkedList<UnoCard>();
        public User user;
        public Player(User u){
            user = u;
        }
        @Override
        public String toString(){
            return UnoCard.toString(cards.toArray());
        }

        private void sendHand(){
           user.sendPrivmsg(toString());
        }
        private int getTotalHandScore(){
            int i = 0;
            for(UnoCard c: cards)
                i+=c.getValue();
            return i; 
        }
    }

}
