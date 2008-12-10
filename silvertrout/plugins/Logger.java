package jbt.plugins;

import jbt.commons.SystemTime;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;

import jbt.Channel;
import jbt.User;

/**
 *
 * @author (reggna)
 */

public class Logger extends jbt.Plugin {

  public void onPrivmsg(User user, Channel channel, String message) {
    if(channel != null)
      writeToLog(getFile(channel), "<" + user.getNickname() + "> " + message);
  }
  
  
  public void onPart(User user, Channel channel, String partMessage) {
    if(channel != null)
      writeToLog(getFile(channel), "-!- " + user.getNickname() + " " + 
          user.toString() + " has left " + channel.getName() +" (" 
          + partMessage + ")");
  }

  public void onJoin(User user, Channel channel) {
    if(channel != null)
      writeToLog(getFile(channel), "-!- " + user.getNickname() + " " + 
          user.toString() + " has joined " + channel.getName());
  }
  
  /* TODO:
    This won't work, since user.getChannels() will return null
  */
  public void onQuit(User user, String quitMessage) {
    if(user.getChannels() == null) return;
    for (Channel channel: user.getChannels()){
      writeToLog(getFile(channel), "-!- " + user.getNickname() + " " + 
          user.toString() + " has quit (" + quitMessage + ")");
    }
  }
  
  public void onKick(User user, Channel channel, User kicked, String kickReason) {
    if(channel != null)
      writeToLog(getFile(channel), "-!- " + user.getNickname() + " " + 
          user.toString() + " has left " + channel.getName() +" (" + 
          kickReason + ")");
  }

  public void onTopic(User user, Channel channel, String oldTopic) {
    if(channel != null)
      writeToLog(getFile(channel), "-!- " + user.getNickname() + 
          " changed the topic of " + channel.getName() +" to: " +
          channel.getTopic());
  }

  public void onNick(User user, String oldNickname) {
    for (Channel channel: user.getChannels()){
      writeToLog(getFile(channel), "-!- " + oldNickname + " is now known as " + 
          user.getNickname());
    }
  }
  
  public File getFile(Channel channel){
    try{
      /* create the folder if the folder has been deleted */
      String dir = "jbt/plugins/Logger/" + getNetwork().name;
      (new File(dir)).mkdirs();
      
      String file =  channel.getName().substring(1) + ".log";
      return new File(dir + "/" + file);
    } catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
  
  public boolean writeToLog(File f, String message){
    try{
      f.createNewFile();
      /* create the file and a PrintStream to add data at the end of the file*/
      PrintStream out = new PrintStream(new FileOutputStream(f, true), true, "UTF-8");
      
      /* print a logmessage to the file */
      out.println(SystemTime.getCurrentTime() + " " + message );
      //out.flush(); // not needed, autoflush is on.
      out.close();
      
      return true;
    }catch(Exception e){
      e.printStackTrace();
      return false;
    }
  }
}
