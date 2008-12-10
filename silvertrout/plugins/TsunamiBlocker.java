package jbt.plugins;

import java.util.HashMap;

import jbt.Channel;
import jbt.User;

public class TsunamiBlocker extends jbt.Plugin {

  HashMap<User, Integer> scores;

  public TsunamiBlocker() {
    scores = new HashMap<User, Integer>();
  }
  
  @Override
  public void onPrivmsg(User user, Channel channel, String message) {
    // Already scored:
    if(scores.containsKey(user)) {
      // Update old score:
      int score = scores.get(user) + 1;
      scores.put(user, score);
      
      // Excess flood
      if(score > 5) {
        channel.kick(user, "Don't spam in the channel please!");
        scores.put(user, 0);
      }
      
    // New user:
    } else {
      scores.put(user, 1);
    }
  }

  @Override
  public void onTick(int ticks) {
    // Decrease all scores:
    if(ticks % 7 == 0) {
      for(User user: scores.keySet()) {
        if(scores.get(user) > 0)
          scores.put(user, scores.get(user) - 1);
      }
    }
    
    //System.out.println(scores);
  }


}
