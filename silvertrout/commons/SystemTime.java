package jbt.commons;

import java.util.GregorianCalendar;

public class SystemTime {

    public static String getCurrentTime(){
    /* get current time */
    GregorianCalendar c = new GregorianCalendar();
    return c.get(GregorianCalendar.YEAR) + "-" + 
          intToStringer(c.get(GregorianCalendar.MONTH)) + "-" +
          intToStringer(c.get(GregorianCalendar.DAY_OF_MONTH)) + " " + 
          intToStringer(c.get(GregorianCalendar.HOUR_OF_DAY)) + ":" + 
          intToStringer(c.get(GregorianCalendar.MINUTE)) + ":" + 
          intToStringer(c.get(GregorianCalendar.SECOND));
  
  }
  
  public static String intToStringer(int i){
    if(i < 10) return "0" + i;
    else return "" + i;
  }
  
}

