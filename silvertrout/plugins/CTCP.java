package jbt.plugins;

import jbt.Channel;
import jbt.User;

import java.net.*;

public class CTCP extends jbt.Plugin {
  
  public static String quote(String s) {
    String quotedString = "";
    
    for(int i = 0; i < s.length(); i++) {
      switch(s.charAt(i)) {
       case '' : quotedString += "\\a";       break;
       case '\\' : quotedString += "\\\\";      break;
       default   : quotedString += s.charAt(i); break;
      }
    }
    quotedString += ""; return quotedString;
  }
  
  public static String deQuote(String s) {
    String deQuotedString = "";
    
    for(int i = 1; i < s.length() - 1; i++) {
      if(s.charAt(i) == '\\') {
        if(s.charAt(i + 1) == '\\') {
         deQuotedString += "\\"; i++;
        } else if(s.charAt(i + 1) == 'a') {
         deQuotedString += ""; i++;
        } 
      } else {
        deQuotedString += s.charAt(i);
      }    
    }
    return deQuotedString;
  }
  
  public static long ipToNumber(String n) {
    String[] parts = n.split(".");
    return (Long.parseLong(parts[0]) << 24) +
           (Long.parseLong(parts[1]) << 16) + 
           (Long.parseLong(parts[2]) <<  8) +
           (Long.parseLong(parts[3]) <<  0);
    
  }
  
  public static String ipToString(long n) {
    long a = (n & 0xFF000000) >> 24;
    long b = (n & 0x00FF0000) >> 16;
    long c = (n & 0x0000FF00) >>  8;
    long d = (n & 0x000000FF) >>  0;
    
    return "" + a + "." + b + "." + c + "." + d;
  }
  
  @Override
  public void onPrivmsg(User from, Channel to, String message) {
    
    System.out.println(message);
    
    // CTCP message:
    if(message.startsWith("") && message.endsWith("")) {
    
      System.out.println("we have ctcp! :)");
    
      String   deQuotedString = deQuote(message);
      String[] parts = deQuotedString.split(" ");
      
      if(parts[0].equals("DCC")) {
        if(parts[1].equals("CHAT")) {
          System.out.println("Got DCC CHAT message.");
        } else if(parts[1].equals("SEND")) {
          
          String name = parts[2];
          String host = ipToString(Long.parseLong(parts[3]));
          int    port = Integer.parseInt(parts[4]);
          int    size = Integer.parseInt(parts[5]);
          
          new DCCFileReciever(name, size, host, port);
        }        
      } else {
        /*
        case "ACTION"     :
        case "FINGER"     :
        case "VERSION"    :
        case "SOURCE"     :
        case "USERINFO"   :
        case "CLIENTINFO" :
        case "ERRMSG"     :
        case "PING"       :
        case "TIME"       :  */
        System.out.println("Got random CTCP message");
        for(int i = 0; i < parts.length; i++) {
          System.out.print(parts[i] + " + ");
        }
      }
        
      System.out.println(deQuotedString);
    }
  }
  
}
