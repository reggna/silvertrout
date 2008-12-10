package jbt.plugins;

import java.util.ArrayList;

// XML parser stuff
import java.io.IOException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

// URL and URL connection
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;

// JBT internal
import jbt.commons.EscapeUtils;
import jbt.Channel;
import jbt.User;

// TODO: save feeds in file
// Format: name "desc" url lastguid, channel
// TODO: make more robust. Partly done.
// TODO: better channel management
// TODO: check if channel is already active.
// TODO: Channel list for each feed?
// TODO: move print of first item to onPrivmsg
// TODO: make it more configurable. Use const for spam limit, update time, etc


public class FeedEater extends jbt.Plugin {

  public class Feed {
    public String  title;
    public String  description;
    public URL     url;
    public String  lastGuid;
    public Channel channel;
  }
  
  public class FeedItem {
    public String  title;
    public String  description;
    public String  guid;
    public String  link;
  
  }

  ArrayList<Feed> feeds;
    
  public FeedEater() {
    feeds = new ArrayList<Feed>();
  }

  public boolean add(String url, Channel channel) {
    
    // Set up a new feed
    Feed feed = new Feed();
    try {
    
      feed.url         = new URL(url);
      feed.lastGuid    = "";
      feed.channel     = channel;
  
      // Fetch channel:
      HttpURLConnection con = (HttpURLConnection)feed.url.openConnection();
              
      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true);
      DocumentBuilder builder           = domFactory.newDocumentBuilder();
      Document doc                      = builder.parse(con.getInputStream());

      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath          = factory.newXPath();
      XPathExpression expr = xpath.compile("//channel");

      Object result        = expr.evaluate(doc, XPathConstants.NODESET);
      NodeList nodes       = (NodeList)result;

      if(nodes.getLength() == 1) {

        Node chan         = nodes.item(0);
        NodeList elements = chan.getChildNodes();

        // Loop through feed items
        for (int i = 0; i < elements.getLength(); i++) {
          Node element       = elements.item(i);
          String nodeName    = element.getNodeName();
          String nodeContent = element.getTextContent();
          
          if(nodeName.equals("title")) {
            feed.title = EscapeUtils.unescapeHtml(nodeContent);
          } else if(nodeName.equals("description")) {
            feed.description = EscapeUtils.unescapeHtml(nodeContent);
          }
        }
      } else {
        System.out.println("Failed to add feed");
        System.out.println("Nodes length is: " + nodes.getLength());
        return false;
      }
    } catch(Exception e) {
      System.out.println("Failed to add feed");
      e.printStackTrace();
      return false;
    }
   
    // Fetch first item. Print it and save its guid as the feeds last guid.
    ArrayList<FeedItem> items = fetchItems(feed, "", 1);
    System.out.println("Got " + items.size() + " items");
    if(items.size() == 1) {
      System.out.println("First is " + items.get(0).title + ", " + items.get(0).guid);
      feed.lastGuid = items.get(0).guid;
      print(feed, items.get(0));
    } else {
      System.out.println("Gor more than one item!");
      return false;
    }

    feeds.add(feed);
    return true;
    
  }
  
  public boolean remove(int index) {
    if(index > 0 && index < feeds.size()) {
      feeds.remove(index);
      return true;
    }
    return false;
  }
  
  private void print(Feed feed, FeedItem feedItem) {
    String title       = feedItem.title;
    String description = feedItem.description;
    String link        = feedItem.link;
  
    getNetwork().sendPrivmsg(feed.channel.getName(), 
        title + ": " + description + " - " + link);
  }
  
  private ArrayList<FeedItem> fetchItems(Feed feed, String until) {
    return fetchItems(feed, until, -1);
  }
  
  private ArrayList<FeedItem> fetchItems(Feed feed, String until, int fetchLimit) {
  
    // Create feed items list
    ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();
    
    try {      
      // Fetch feed items
      HttpURLConnection con = (HttpURLConnection)feed.url.openConnection();
              
      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true);
      DocumentBuilder builder           = domFactory.newDocumentBuilder();
      Document doc                      = builder.parse(con.getInputStream());

      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath          = factory.newXPath();
      XPathExpression expr = xpath.compile("//item");

      Object result        = expr.evaluate(doc, XPathConstants.NODESET);
      NodeList nodes       = (NodeList) result;

      // Loop through feed items
      if(fetchLimit < 0)fetchLimit = nodes.getLength();
      
      for (int i = 0; i < fetchLimit; i++) {
        NodeList childNodes = nodes.item(i).getChildNodes();
        FeedItem fi = new FeedItem();
        
        for(int j = 0; j < childNodes.getLength(); j++) {
          String nodeName    = childNodes.item(j).getNodeName();
          String nodeContent = childNodes.item(j).getTextContent();
          
          if(nodeName.equals("title")) {
            fi.title = EscapeUtils.unescapeHtml(nodeContent);
          } else if(nodeName.equals("link")) {
            fi.link = EscapeUtils.unescapeHtml(nodeContent);
          } else if(nodeName.equals("description")) {
            fi.description = EscapeUtils.unescapeHtml(nodeContent);
          } else if(nodeName.equals("guid")) {
            fi.guid = EscapeUtils.unescapeHtml(nodeContent);
          }
        }
        // If we have an old rss feed we need to use link as guid
        if(fi.guid == null)
          fi.guid = fi.link;        
        // Done fetching new items
        if(fi.guid.equals(feed.lastGuid))
          break;
        // Add to feed item list.
        feedItems.add(fi);
      } 
    } catch(Exception e) {
      System.out.println("Fail in XML parser");
      e.printStackTrace();
    }
    return feedItems;
  }

  @Override
  public void onPrivmsg(User from, Channel to, String message) {
  
    String[] parts = message.split("\\s");    
    String command = parts[0].toLowerCase();
    
    // List feeds:
    if(parts.length == 1 && command.equals("!listfeeds")) {
      System.out.println("List feeds");
      for(int i = 0; i < feeds.size(); i++) {
        getNetwork().sendPrivmsg(to.getName(), i + ". " 
            + feeds.get(i).title + " (" + feeds.get(i).url + " = " 
            + feeds.get(i).lastGuid + ") - " + feeds.get(i).description);
      }
    // Add feed:
    } else if(parts.length == 2 && command.equals("!addfeed")) {
      if(add(parts[1], to)) {
        Feed feed = feeds.get(feeds.size() - 1);
        getNetwork().sendPrivmsg(to.getName(), "Added feed: "
          + feed.title + " - " + feed.description + " - " + feed.url);
      } else {
        getNetwork().sendPrivmsg(to.getName(), "Failed to add feed");
      }
    } else if(command.equals("!removefeed")) {
      if(remove(Integer.parseInt(parts[1]))) {
        getNetwork().sendPrivmsg(to.getName(), "Removed feed");
      } else {
        getNetwork().sendPrivmsg(to.getName(), "Failed to remove feed");
      }
      
    }
    
  }

  @Override
  public void onTick(int t) {
    //System.out.println("t = " + t + ", " + (t % (60 * 1)));
    if((t % (60 * 5)) == 0) {
      //System.out.println("Trying to find updated pages...");
      for(Feed feed: feeds) {
        ArrayList<FeedItem> feedItems = fetchItems(feed, feed.lastGuid);
        int i = 0;
        for(FeedItem feedItem: feedItems) {
          // Update last guid
          if(i == 0)
            feed.lastGuid = feedItem.guid;
          // Spam stop. TODO: Do a better solution. In fact convert this for
          // each loop to using an iterator or something better.
          else if(i > 2) {
            getNetwork().sendPrivmsg(feed.channel.getName(), 
                (feedItems.size() - i) + " more items are not printed to "
                + "prevent massive spam... Sorry!");
            break;
          }
          print(feed, feedItem);
          i++;
        }
      }
    }
  }
  
}
