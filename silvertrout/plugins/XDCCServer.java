package silvertrout.plugins;

import java.io.File;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import silvertrout.Plugin;
import silvertrout.User;
import silvertrout.commons.Callback;

/**
 * Provides XDCC server features.
 * @author Jonas "Jaif" Färdig
 * @version 0.1
 * @todo Refresh the list, error handling, commenting
 */
public class XDCCServer extends Plugin implements Callback{

	private String folder;
	private Semaphore slots;
	private ConcurrentMap<UUID,User> active = new ConcurrentHashMap<UUID,User>();
	private Map<Integer,File> list = new HashMap<Integer, File>();
	private Queue<XDCCRequest> requestQueue = new ArrayDeque<XDCCRequest>();

	private final static int DEFAULT_MAX_SLOTS = 5;

	@Override
	public void onLoad(Map<String, String> settings) {
		folder = settings.containsKey("folder") ? settings.get("folder") : ".";
		slots = new Semaphore(settings.containsKey("slots") ? Integer.parseInt(settings.get("slots")) : DEFAULT_MAX_SLOTS);

		generateList();
	}

	@Override
	public void onPrivmsg(User user, String message) {
		String msg[] = message.toLowerCase().split(" ");
		if(msg[0].equals("xdcc")){
			if(msg[1].equals("list")){
				sendList(user);
			}else if(msg[1].equals("send")){
				//TODO error handling
				int fileNo = Integer.parseInt(msg[2].replaceAll("#", "")); 

				if(list.containsKey(fileNo)){
					if(slots.tryAcquire()){
						DCCFileSender send = new DCCFileSender(list.get(fileNo), user, getNetwork(), this);
						UUID id = send.startSend();
						active.put(id, user);
					}else{
						requestQueue.add(new XDCCRequest(user,fileNo));
					}
				}
			}else if(msg[1].equals("remove")){
				Iterator<XDCCRequest> it = requestQueue.iterator();
				
				if(msg.length > 2){
					while(it.hasNext()){
						XDCCRequest temp = it.next();
						//TODO error handling
						int fileNo = Integer.parseInt(msg[2].replaceAll("#", ""));
						
						if(temp.fileNumber == fileNo && temp.user.equals(user)){
							requestQueue.remove();
						}
					}
				}else{	
					while(it.hasNext()){
						
						if(it.next().user.equals(user)){
							requestQueue.remove();
						}
					}
				}
			}
		}
	}

	@Override
	public void callback(UUID id, String[] args) {
		active.remove(id);

		slots.release();
	}

	@Override
	public void onQuit(User user, String quitMessage) {
		Iterator<XDCCRequest> it = requestQueue.iterator();
		
		while(it.hasNext()){
			if(it.next().user.equals(user)){
				it.remove();
			}
		}
	}

	@Override
	public void onTick(int ticks) {
		if(!requestQueue.isEmpty() && slots.availablePermits() > 0){
			for(XDCCRequest r : requestQueue){
				if(!active.containsValue(r.user)){
					if(slots.tryAcquire()){
						DCCFileSender send = new DCCFileSender(list.get(r.fileNumber), r.user, getNetwork(), this);
						UUID id = send.startSend();
						active.put(id, r.user);
						requestQueue.remove(r);
					}
				}
			}
		}
	}

	private void sendList(User u){
		getNetwork().getConnection().sendPrivmsg(u, "XDCC List:");

		for(Map.Entry<Integer, File> e : list.entrySet()){
			getNetwork().getConnection().sendPrivmsg(u, "#" + e.getKey() + ": " + e.getValue().getName());
		}
	}

	private void generateList(){
		int n = 1;
		for(File f : new File(folder).listFiles()){
			list.put(n++, f);
		}
	}

	/**
	 * 
	 */
	private class XDCCRequest{
		final User user;
		final int fileNumber;

		/**
		 * @param f
		 * @param u
		 */
		public XDCCRequest(User u, int fileNumber) {
			this.fileNumber = fileNumber;
			this.user = u;
		}
	}
}
