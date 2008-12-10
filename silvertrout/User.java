package jbt;

import java.util.ArrayList;

/**
 * 
 * 
 * 
 */
public class User {

  private String             nickname;
  
  private String             hostname;
  private String             server;
  
  private String             realname;
  
  
  private boolean            secureConnection;
  
  private ArrayList<Channel> channels;
  
/**
 * A basic constructor to create a new User
 * 
 * @param nickname - The nickname to set to the new User
 */
  public User(String nickname) {
    this.nickname = nickname;
  }
  
  public User(String nickname, String hostname, String server,
      boolean secureConnection, ArrayList<Channel> channels) {
    this(nickname);
  }
  
  public ArrayList<Channel> getChannels(){
    return channels;
  }

/**
 * A method to fetch the user's nickname
 * 
 * @return The nickname of the user
 */
  public String getNickname() {
    return this.nickname;
  }

/**
 * A method to set a user's nickname
 * 
 * @param nickname - The nickname to set to the user
 */
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

/**
 * Returnes the user as a String
 * 
 * @return A string consisting of the user's nickname, hostname, realname and the server the user is connected to.
 */
  public String toString() {
    return "[" + nickname + "@" + hostname + " (" + server + ") " + realname + " : " + hashCode() + "]";
  }
}

