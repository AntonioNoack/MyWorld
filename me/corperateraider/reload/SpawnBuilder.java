package me.corperateraider.reload;

import java.util.HashMap;

import me.corperateraider.generator.Generator;
import me.corperateraider.generator.Random;
import me.corperateraider.myworld.Plugin;
import me.corperateraider.myworld.Rank;
import me.corperateraider.myworld.Sprache;
import net.minecraft.server.v1_7_R1.ChatSerializer;
import net.minecraft.server.v1_7_R1.EnumDifficulty;
import net.minecraft.server.v1_7_R1.EnumGamemode;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import net.minecraft.server.v1_7_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_7_R1.WorldType;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpawnBuilder extends Generator {
	
	public static HashMap<String, String> deathMapNormal=new HashMap<>(), deathMapDream=new HashMap<>();
	public static String prefix = "§c[§4ISS-7-AI§c]§a ";
	static World world;
	
	public static void onDeath(final Player p, final boolean himmel){
		final World w = Bukkit.getWorlds().get(0);
		
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
				
				Random r = new Random(p.getName().hashCode()*Random.l1);
				int x = (int) (100000*(r.next()-0.5)), z = (int) (100000*(r.next()-0.5)), y = w.getHighestBlockYAt(x, z);
				w.getBlockAt(x, y-1, z).setType(himmel?Material.GOLD_BLOCK:Material.CACTUS);
				if(himmel){
					w.getBlockAt(x+4, y=w.getHighestBlockYAt(x+4,z), z).setType(Material.DIRT);
					w.generateTree(new Location(w, x, y, z), TreeType.BIG_TREE);
				}
				
				p.teleport(new Location(w, x, y, z));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				p.teleport(new Location(w, x, y, z));
			}
		}).start();
	}
	
	@SuppressWarnings("deprecation")
	public static boolean sB(World w, int x, int y, int z, int id, int data){
		w.getBlockAt(x, y, z).setTypeIdAndData(id, (byte) data, true);
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static void change(Player p, int alty, int neuy){
		int id;
		System.out.println(ID(neuy)+"="+neuy+" "+ID(alty)+"="+alty);
		if((id=ID(neuy))!=ID(alty)){
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutRespawn(id, EnumDifficulty.HARD, WorldType.LARGE_BIOMES, p.getGameMode()==GameMode.CREATIVE?EnumGamemode.CREATIVE:EnumGamemode.SURVIVAL));
			p.updateInventory();
		}
	}
	
	public static int ID(int y){
		if(y==1 || (y>7 && y<20)){
			return 1;
		} else if(y>19){// nur [21-23]
			return 0;
		} else {
			return -1;
		}
	}
	
	public static void onJoin(final Player p, boolean dimensionegal, boolean isNew){
		
		Rank.firstTime(p);
		
		world = Bukkit.getWorlds().get(2);
		World w = p.getWorld();
		if(w == Plugin.world){
			change(p, 24, Generator.basey(p.getLocation().getBlockX(), p.getLocation().getBlockZ()-16)/224);
		}
		
		if(dimensionegal || w.getName().endsWith("_the_end") || isNew){
			
			// Spieler joint am Spawn -> er wird erst um die Erde herumteleportiert und dann in die Seitenteile des Schiffes teleportiert
			Thread tr = new Thread(new Runnable(){

				@Override
				public void run() {
					if(p.getGameMode()!=GameMode.CREATIVE)
						p.setGameMode(GameMode.ADVENTURE);
					else {
						p.sendMessage(prefix+Sprache.select(p.getName(),
								"What does a creative one is looking at the spawn?",
								"Was sucht jemand mit gm1 am Spawn?", null, null));
						Location l;
						l = new Location(world, 0.5, 68, 1.5);
						l.setDirection(new Vector(0, 0, -1));
						p.teleport(l);
						return;
					}
					
					Location l;
					l = new Location(world, 87, 6, 0);
					l.setDirection(new Vector(-1.4,0.1,0));
					p.teleport(l);
					
					sleep(3000);
					
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"Welcome at ISS-7! §4Don´t move!",
							"Willkommen auf der ISS-7! §4Steh still!", null, null));
					l = new Location(world, 60, 6, 60);
					l.setDirection(new Vector(-1,0.1,-1));
					p.teleport(l);
					sleep(1500);
					sendRaw(p, "{text:\""+prefix+"§2[AUDIO-VERSION]\",clickEvent:{action:open_url,value:\"https://www.youtube.com/c/AntonioNoack\"}}");
					l = new Location(world, 0, 6, 87);
					l.setDirection(new Vector(0,0.1,-1.4));
					p.teleport(l);
					sleep(7000);
					p.sendMessage(prefix+Sprache.select(p.getName(), 
							"I am the aritifical intelligence ISS-7-AI.",
							"Ich bin die künstlische Intelligenz ISS-7-AI", null, null));
					l = new Location(world, -60, 6, 60);
					l.setDirection(new Vector(1,0.1,-1));
					p.teleport(l);
					sleep(4500);
					p.sendMessage(prefix+Sprache.select(p.getName(), 
							"Your story isn´t easy to be told. It started very long ago.",
							"Deine Vergangenheit war nicht einfach, aber sie began vor langer langer Zeit.", null, null));
					l = new Location(world, -87, 6, 0);
					l.setDirection(new Vector(1.4,0.1,0));
					p.teleport(l);
					sleep(3200);
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"Do you know this planet 800km under your feet? This is the earth, and no, this is no dream. It is real.",
							"Kennst du noch diesen Planeten 800km unter deinen Füßen? Das ist die Erde und nein, das ist kein Traum, sondern echt.", null, null));
					l = new Location(world, -60, 6, -60);
					l.setDirection(new Vector(1,0.1,1));
					p.teleport(l);
					sleep(6000);
					p.sendMessage(prefix+Sprache.select(p.getName(), 
							"You are part of our recivilisation mission. As you know we had to left mother earth 7632 years ago,",
							"Du bist Teil unserer Wiederbevölkerungsmission. Wie du weißt, mussten wir die Erde vor 7632 Jahren verlassen,", null, null));
					l = new Location(world, 0, 6, -87);
					l.setDirection(new Vector(0,0.1,1.4));
					p.teleport(l);
					sleep(7000);
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"And we got it: we left her before she finally got destroid.",
							"Und wir haben es geschafft: wir haben sie verlassen können bevor sie gänzlich zerstört war.", null, null));
					l = new Location(world, 60, 6, -60);
					l.setDirection(new Vector(-1,0.1,1));
					p.teleport(l);
					sleep(6000);
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"Here, on our ISS-7, you will find advices for surviving and the code to reach offically the blue planet.",
							"Hier auf der ISS-7 wirst du Hinweise zum Überleben und dein Passwort um die Erde erreichen zu dürfen, finden(in englisch, weil internationaler Server)", null, null));
					l = new Location(world, 87, 6, 0);
					l.setDirection(new Vector(-1.4,0.1,0));
					p.teleport(l);
					sleep(8500);
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"Good luck - you are not alone!",
							"Viel Glück - du bist nicht allein!", null, null));
					l = new Location(world, 0.5, 68, 1.5);
					l.setDirection(new Vector(0, 0, -1));
					p.teleport(l);
				}
			});
			Plugin.threads.add(tr);
			tr.start();
		}
	}
	
	public static void sleep(int i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {}
	}
	
	public static void sendRaw(Player p, String s){
		//{text:\"Hello there!\",hoverEvent:{action:show_text,value:\"You found an easteregg :3\"}}
		Packet packet = new PacketPlayOutChat(ChatSerializer.a(s), true);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

}
