package jbt.commons;

import java.util.HashMap;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EscapeUtils {

  private static HashMap<String, String> xmlEnts;
  private static HashMap<String, String> htmlEnts;
  
  static {
  
    // Complete list of XML entities
    xmlEnts = new HashMap<String, String>();
    xmlEnts.put("&quot;", "\"");     xmlEnts.put("&amp;", "\u0026");
    xmlEnts.put("&apos;", "\u0027"); xmlEnts.put("&lt;",  "\u003c");
    xmlEnts.put("&gt;",   "\u003e");
  
    // Uncomplete list of HTML entities. 
    // TODO: should include all theses: 
    // http://www.w3.org/TR/REC-html40/sgml/entities.html
    htmlEnts = new HashMap<String,String>();
    htmlEnts.put("lt",     "<");      htmlEnts.put("gt",     ">");
    htmlEnts.put("amp",    "&");      htmlEnts.put("quot",   "\"");
    htmlEnts.put("agrave", "à");      htmlEnts.put("Agrave", "À");
    htmlEnts.put("acirc",  "\u00c4");      htmlEnts.put("auml",   "\u00e4");
    htmlEnts.put("Auml",   "Ä");      htmlEnts.put("Acirc",  "Â");
    htmlEnts.put("aring",  "\u00e5");      htmlEnts.put("Aring",  "\u00c5");
    htmlEnts.put("aelig",  "æ");      htmlEnts.put("AElig",  "Æ");
    htmlEnts.put("ccedil", "ç");      htmlEnts.put("Ccedil", "Ç");
    htmlEnts.put("eacute", "é");      htmlEnts.put("Eacute", "É");
    htmlEnts.put("egrave", "è");      htmlEnts.put("Egrave", "È");
    htmlEnts.put("ecirc",  "ê");      htmlEnts.put("Ecirc",  "Ê");
    htmlEnts.put("euml",   "ë");      htmlEnts.put("Euml",   "Ë");
    htmlEnts.put("ocirc",  "ô");      htmlEnts.put("Ocirc",  "Ô");
    htmlEnts.put("ouml",   "ö");      htmlEnts.put("Ouml",   "\u00d6");
    htmlEnts.put("oslash", "ø");      htmlEnts.put("Oslash", "Ø");
    htmlEnts.put("szlig",  "ß");      htmlEnts.put("ugrave", "ù");
    htmlEnts.put("Ugrave", "Ù");      htmlEnts.put("ucirc",  "û");
    htmlEnts.put("Ucirc",  "Û");      htmlEnts.put("uuml",   "ü");
    htmlEnts.put("Uuml",   "Ü");      htmlEnts.put("nbsp",   " ");
    htmlEnts.put("copy",   "\u00a9"); htmlEnts.put("reg",    "\u00ae");
    htmlEnts.put("euro",   "\u20a0"); htmlEnts.put("laquo",  "\u00ab");
    htmlEnts.put("raquo",  "\u00bb");
  }

  public static String unescapeHtml(String source) {
    String  pattern = "(?i)(&([A-Za-z]+|[\\#0-9]+);)";
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(source);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      if(m.group(2).startsWith("#")) {
        char c = (char)Integer.valueOf(m.group(2).substring(1)).intValue();
        m.appendReplacement(sb, Character.toString(c));
      } else {
        String value = (String)htmlEnts.get(m.group(2));
        if(value == null) {
          // TODO: throw new java.io.IOException("Could not decode entity &" + m.group(2) + ";");
          System.out.println("Could not decode entity &" + m.group(2) + ";");
        } else {
          m.appendReplacement(sb, value);
        }
      }
      
    }
    m.appendTail(sb);
    return sb.toString();
  }
  
  public static String unescapeXml(String source) {
    throw new java.lang.UnsupportedOperationException("Not implemented");
  }
  
  public static String escapeHtml(String source) {
    throw new java.lang.UnsupportedOperationException("Not implemented");
  }
  
  public static String escapeXml(String source) {
    throw new java.lang.UnsupportedOperationException("Not implemented");
  }
  
}

