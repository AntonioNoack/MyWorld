package me.corperateraider.reload;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.corperateraider.myworld.Plugin;
import me.corperateraider.myworld.Sprache;
import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.check.CheckType;

import org.bukkit.entity.Player;

public class UserReport {
	private static boolean inWork;
	private static int count=-1;
	private static HashMap<String, Report> map = new HashMap<>();
	public static void save(File folder, boolean really) throws IOException{
		count++;
		if((really || count%12==0) && map.size()>0){// normalerweise stündlich
			inWork = true;
			File f = new File(folder, "report");
			if(!f.exists()){
				f.mkdir();
			}
			
			f = new File(f, System.currentTimeMillis()/1000+".report");
			
			FileWriter write = new FileWriter(f);
			
			for(String name:map.keySet()){
				write.write(Sprache.select(name, "EN", "DE", "FR", "ES")+"."+name+"\n");
				Report r = map.get(name);
				for(TimedReport report:r.map){
					write.write("\t"+report.message+"\n");
				}
			}
			
			write.flush();
			write.close();
			
			map = new HashMap<>();
			
			inWork = false;
		}
	}
	
	public static void createReport(Player p, String message){
		int count = 0;
		while(inWork && count++<10){
			try {Thread.sleep(100);} catch (InterruptedException e) {}
		}
		if(count == 10){
			p.sendMessage(Plugin.prefix+"I am sorry but your report couldn't be created :( Please try later again...");
			return;
		} else {
			if(!map.containsKey(p.getName())){
				map.put(p.getName(), new Report());
			}
			if(map.get(p.getName()).add(p, message)){
				p.sendMessage(Plugin.prefix+"Saved message...");
			} // sonst ist irgendwas schief gegangen...
		}
	}
	static class Report {
		private int lenght = 0;
		private ArrayList<TimedReport> map = new ArrayList<>();
		public boolean add(Player p, String message){
			lenght+=message.length();
			if(lenght>10000){
				p.sendMessage(Plugin.prefix+"Your report gets to long. Try again later! ("+((12-count%12)*5)+" minutes)");
				AntiCheat.getManager().getUserManager().getUser(p.getName()).increaseLevel(CheckType.REPORT_SPAM);
				return false;
			} else {
				long time = System.currentTimeMillis();
				int index = map.size()-1;
				if(index<0){
					map.add(new TimedReport(message, System.currentTimeMillis()));
					return true;
				} else {
					TimedReport last = map.get(index);
					if(last.time+60000>time){// 1 Minute Schreibzeit für das nächste Stück :)
						if(last.message.length()+message.length()>500){
							p.sendMessage(Plugin.prefix+"Try to keep short! (otherwise you may be banned for spamming) This message will not be added!");
							return false;
						} else {
							last.message+=message;
							last.time=time;
							return true;
						}
					} else {
						map.add(new TimedReport(message, time));
						return true;
					}
				}
			}
		}
	}
	static class TimedReport {
		String message;long time;
		public TimedReport(String message, long time){
			this.message=message;this.time=time;
		}
	}
}
