package jbt.plugins;

import java.util.Random;
import jbt.Channel;
import jbt.User;

public class CandyMan extends jbt.Plugin {

  private Random r;

  public CandyMan() {
    r   = new Random(); 
  }
  
  @Override
  public void onPrivmsg(User user, Channel channel, String message) {
  
    // Only in channels:
    if(channel != null) {
    
      String[] icecreams = { "Daimstrut","Magnum","Top Hat", "Cornetto", "Solero", 
                             "Nogger", "88:an", "Tip Top"};
      String[] candy     = { "Toblerone", "Daimpåse", "Twix", "påse med Bilar", 
                             "Kexchoklad", "stor påse med Chips" };
      String[] drugs     = { "haschbrownie", "joint", "lina kokain", 
                             "spruta heroin", "lapp lsd" };
      
      String what = new String();
      
      if(message.equals("!glass")) {
        what = icecreams[r.nextInt(icecreams.length)];
      } else if(message.equals("!godis")) {
        what = candy[r.nextInt(candy.length)];
      } else if(message.equals("!knark")) {
        what = drugs[r.nextInt(drugs.length)];
      } else {
        return;
      }
      
      if(r.nextInt(10) > 7) {
        channel.sendAction("ger reggna en " + what);
      } else {
        channel.sendAction("ger " + user.getNickname() + " en " + what);
      }
    }
  }

}
