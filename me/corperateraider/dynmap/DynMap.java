package me.corperateraider.dynmap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.corperateraider.generator.MathHelper;
import me.corperateraider.myworld.Rank;
import net.webbukkit.HTTPRequestEvent;

/**
 * sendet die Daten zum ClientBrowser... der das dann schick rendert :)
 * */
public class DynMap {
	
	static int index;
	static Message[] messages = new Message[256];
	private static class Message {
		public String txt;
		public long time;
		public Message(String txt, long time){
			this.txt=txt;this.time=time;
		}
	}
	public static void addMessage(Player p, long time, String txt){
		messages[++index&0xff]=new Message("ACT "+Rank.getNickName(p.getName())+": "+txt, time);
	}
	
	/**
	 * Protokoll:
	 * 		jede Request beginnt mit /_
	 * 			p = players
	 * 			c = chunks
	 * 				zoom.x.z -> gibt den ganzen Chunk ab dem Hauptlayer aus
	 * 			
	 * */
	
	public static void answer(HTTPRequestEvent event){
		String request = event.getRequest().split(" ")[1];
		if(request.length()<3){
			return;
		}
		char c = request.charAt(2);
		if(request.startsWith("/_")){
			request = request.substring(3);// es ist ja bekannt, dass das verwendet wurde
			switch(c){
			case 'p':
				event.setImage(ReloadPlayers.getData(request));
				break;
			case 'c':
				event.setImage(ReloadChunks.getData(request));
				break;
			case 't':
				event.setMessage(System.currentTimeMillis()+"");
				break;
			case 's':// sende Nachricht...
				int i=request.indexOf(',');
				if(i<0){
					event.setMessage("seperate username from text with a ,!");
				} else {
					String name=request.substring(0, i), message=request.substring(i+1).replace('&', '§');
					Bukkit.broadcastMessage("<WEB: "+name+"> "+message);
					messages[++index&0xff]=new Message("WEB "+name+": "+message, System.currentTimeMillis());
					event.setMessage("ok");
				}
				break;
			case 'm':// bekomme die letzten 256 ChatNachrichten mit ID
				long startTime = MathHelper.stringToLong(request, 0);
				String ret="<script>chattime = "+(index==0?"0":messages[index&0xff].time)+";</script>\n";
				for(i=0;i<256;i++){
					Message m = messages[(index+256-i)&0xff];
					if(m!=null && m.time>startTime){
						ret+="<p>"+m.txt+"</p>\n";
					}
				}
				event.setMessage(ret);
				break;
			default:
				System.out.println("Unbekannte DynMap-Request: /_"+c+request);
				event.setImage(Reload.getData(request));
				break;
			}
		}// else unbekannt :)
	}
}
