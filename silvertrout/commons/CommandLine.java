package jbt.commons;

import java.util.Map;
import java.util.HashMap;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.lang.IllegalArgumentException;

public class CommandLine {
  
  private String command;
  
  private Map<String,String> options = new HashMap<String,String>();
  
  public CommandLine(String line){
    /* check that the first letter in the line is an exclamation point 
    /* and save the command, else throw an exception*/
    Pattern pt           = Pattern.compile("^!(\\S+)");
    Matcher mt           = pt.matcher(line);
    if(mt.find())
      command = mt.group(1);
    else
      throw new IllegalArgumentException("\nThe line \n" + line + "\nis not a "+
          "command line");
    
    /* check for all hyphen followed by a letter, and save it and the rest of */
    /* the parameter in options */
    pt = Pattern.compile("-(\\S+?)\\s([^-]*)");
    mt.usePattern(pt);
    while(mt.find()){
      options.put(mt.group(1), mt.group(2));
      System.out.println(mt.group(1) + "\t" + mt.group(2));
    }
  }
  
  public String getCommand(){
    return command;
  }
  
  public boolean keyExist(String s){
    return options.containsKey(s);
  }
  
  public String getParam(String key){
    return options.get(key);
  }
  
  
  public static void main(String[] args){
    CommandLine c;
    try{
      c = new CommandLine("!unloadplugin -p password -pl Reloader");
    } catch(Exception e){
      e.printStackTrace();
      return;
    }
    System.out.println("Command: " + c.getCommand());
    System.out.println("Does -help exist? " + c.keyExist("help"));
    System.out.println("Parameters with -s: " + c.getParam("s"));
  }
  
}

