package jbt.plugins;

import jbt.User;
import jbt.Channel;
import jbt.Modes;

public class Personator extends jbt.Plugin {

  @Override
  public void onPrivmsg(User user, Channel channel, String message) {
    String[] parts = message.split("\\s", 4);
    
    if(parts.length == 4 && parts[0].equals("password")) {
      String command = parts[1].toLowerCase();
      if(command.equals("!say")) {
        getNetwork().sendPrivmsg(parts[2], parts[3]);
      } else if(command.equals("!action")) {
        getNetwork().sendAction(parts[2], parts[3]);
      }
    }
  }
}
