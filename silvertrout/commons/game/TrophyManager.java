package jbt.commons.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

// TODO: heavy changes needed


public class TrophyManager {
  
  private List<Trophy>              trophyList;
  private Map<String, List<Trophy>> trophyUser;
  
  private File                      trophyDirectory;
  
  private File                      trophyListFile;
  private File                      trophyUserFile;
  
  public TrophyManager(File trophyDirectory) {
    this.trophyDirectory = trophyDirectory;
    
    this.trophyList = new ArrayList<Trophy>();
    this.trophyUser = new HashMap<String, List<Trophy>>();
    
    trophyListFile = new File(trophyDirectory, "TrophyList");
    trophyUserFile = new File(trophyDirectory, "TrophyUser");
    
    loadTrophyList(trophyListFile);
    loadTrophyUser(trophyUserFile);
  }
  
  // Get trophies
  public Trophy getTrophy(String name) {
    for(Trophy t: trophyList) {
      if(t.getName().equals(name))return t;
    }
    return null;
  }
  public List<Trophy> getTrophies() {
    return trophyList;
  }
  public List<Trophy> getTrophies(String nickname) {
    if(trophyUser.get(nickname) == null)return new ArrayList<Trophy>();
    return trophyUser.get(nickname);
  }
  
  public boolean haveTrophy(Trophy t, String nickname) {
    if(getTrophies(nickname).isEmpty())return false;
    return getTrophies(nickname).contains(t);
  }
  
  // Add trophies
  public void addTrophy(Trophy t, String nickname) {
    if(trophyUser.get(nickname) == null) {
      List<Trophy> l = new ArrayList<Trophy>();
      trophyUser.put(nickname, l);
    }
    trophyUser.get(nickname).add(t);
  }
  
  public void addTrophy(String name, String nickname) {
    addTrophy(getTrophy(name), nickname);
  }
  
  public void loadTrophyList(File f) {
    try {
      BufferedReader fr = new BufferedReader(new FileReader(f));
      
      while(true) {
        
        String       name       = fr.readLine();
        String       archivment = fr.readLine();
        String       valueStr   = fr.readLine();
        
        Trophy.Value value      = Trophy.Value.BRONZE;
        for(Trophy.Value v: Trophy.Value.values()) {
          if(v.name().equalsIgnoreCase(valueStr))value = v;
        }
        
        trophyList.add(new Trophy(name, archivment, value));
        
        if(fr.readLine() == null)break;
      }
    } catch(Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    } 
  }
  public void saveTrophyList(File f) {
    // Not implemented yet
  }

  public void loadTrophyUser(File f) {
    try {
      BufferedReader fr = new BufferedReader(new FileReader(f));
      while(true) {
        String nickname = fr.readLine();
        if(nickname == null)break;
        while(true) {
          String trophyName = fr.readLine();
          if(trophyName == null || trophyName.equals(""))break;
          addTrophy(trophyName, nickname);
        }
      } 
    } catch(Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    } 
  }
  
  public void saveTrophyUser() {
    for(Map.Entry<String, List<Trophy>> e: trophyUser.entrySet()) {
      //TODO, print
      for(Trophy t: e.getValue()) {
        // TODO, print
      }
      // TODO print empty line
    }  
  }
}


