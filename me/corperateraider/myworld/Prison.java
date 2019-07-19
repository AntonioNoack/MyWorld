package me.corperateraider.myworld;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Prison {
	static ArrayList<Prisoner> inside = new ArrayList<>();
	static boolean running;
	static class Prisoner {
		long fin;
		String name;
		public Prisoner(String name, long fin){
			if(!running){
				running = true;
				Thread t;
				Plugin.threads.add(t = new Thread(new Runnable(){

					@Override
					public void run() {
						while(true){
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {}
							long t = System.currentTimeMillis();
							
							for(Prisoner p:inside){
								if(p.fin<t){
									freeWithTP(p);
								}
							}
						}
					}
					
				}));
				t.start();
			}
			this.name=name;this.fin=fin;
			Player p = Bukkit.getPlayer(name);
			World w = Plugin.world;
			Location todrop = p.getLocation().add(new Vector(3,0,1));
			for(ItemStack i:p.getInventory().getContents()){
				if(i!=null){
					w.dropItemNaturally(todrop, i);
				}
			}
			Plugin.instance.onCommand(p, null, "xtp", new String[]{"57500", "100", "99593"});
			p.sendMessage(Plugin.badprefix+Sprache.select(p.getName(), 
					"Welcome in the prison. If you doesn´t want to return to the city after your time, use /free",
					"Willkommen im Gefängnis. Wenn du nach deiner Haftzeit nicht zurück in die Stadt möchtest, nutze den Befehl /free", null, null));
			inside.add(this);
		}
	}

	public static boolean hasInside(String name) {
		for(Prisoner p:inside){
			if(p.name.equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
	public static void freeWithoutTP(String name){
		k:for(Prisoner p:inside){
			if(p.name.equalsIgnoreCase(name)){
				inside.remove(p);
				break k;
			}
		}
	}
	
	public static void freeWithTP(String name){
		boolean ok=false;
		k:for(Prisoner p:inside){
			if(p.name.equalsIgnoreCase(name)){
				inside.remove(p);
				ok=true;
				break k;
			}
		}
		if(ok && Bukkit.getPlayer(name)!=null){
			Plugin.instance.onCommand(Bukkit.getPlayer(name), null, "xtp", Plugin.spawnArray);
		}
	}
	
	public static void freeWithTP(Prisoner p){
		inside.remove(p);
		Player pl;
		if((pl=Bukkit.getPlayer(p.name))!=null){
			Plugin.instance.onCommand(pl, null, "xtp", Plugin.spawnArray);
		}
	}
}
