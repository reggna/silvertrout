package jbt.plugins;

import jbt.User;
import jbt.Channel;
import jbt.Modes;

import java.util.Iterator;
import java.util.Map;

public class Reloader extends jbt.Plugin {

  // Password for AdminBoy
  private String password = "password";
  
  @Override
  public void onPrivmsg(User user, Channel channel, String message) {

    String[] parts = message.split("\\s");
    
    if(parts.length > 1 && parts[0].equals(password)) {
      String cmd = parts[1].toLowerCase();
      if(parts.length > 2) {
        if(cmd.equals("!reloadplugin")) {
          getNetwork().unloadPlugin(parts[2]);
          getNetwork().sendPrivmsg(user.getNickname(), parts[2] +" har avaktiverats.");
          getNetwork().loadPlugin(parts[2]);
          getNetwork().sendPrivmsg(user.getNickname(), parts[2] + " har laddats.");
        }
      }
      
    }
  }
}
