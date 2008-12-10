package jbt.plugins;

import jbt.User;
import jbt.Channel;

public class KeepAlive extends jbt.Plugin {


  @Override
  public void onDisconnected() {
    getNetwork().connect("ChalmersIRC", "irc.chalmers.it", 6667);
  }

  @Override
  public void onPing(String id) {
    getNetwork().sendRaw("PONG " + id);
  }

  @Override
  public void onInvite(User nick, String channelName) {
    getNetwork().join(channelName);
  }

}
