package me.corperateraider.myworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.corperateraider.dynmap.ReloadChunks;
import me.corperateraider.generator.Generator;
import me.corperateraider.generator.MathHelper;
import me.corperateraider.generator.NASAHeight;
import me.corperateraider.generator.Random;
import me.corperateraider.generator.WüstenDeko;
import me.corperateraider.myworld.Sprache.Used;
import me.corperateraider.recipes.MyRecipe;
import me.corperateraider.recipes.RecipeManager;
import me.corperateraider.recipes.SRecipe;
import me.corperateraider.recipes.XBlock;
import me.corperateraider.recipes.XMaterial;
import me.corperateraider.reload.Jena;
import me.corperateraider.reload.SpawnBuilder;
import me.corperateraider.reload.SpawnManager;
import me.corperateraider.reload.UserReport;
import me.corperateraider.weather.Weather;
import net.dynamicdev.anticheat.AntiCheat;
import net.webbukkit.HTTPRequestEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import converter.Converter;
import converter.MetaString;
import converter.StringCompare;
import converter.TPS;
import converter.WebServer;

public class Plugin extends JavaPlugin {
	
	public static boolean beta = false,
			isServerInOffline = false;
	
	/**
	 * find things in code with !! or !!! or ?? or ???
	 * @throws IOException 
	 * */
	static int anzahlProbleme, lines, words;
	public static void main(String[] args) throws IOException{
		
		if(args==null || args.length==0){
			main(new String[]{"C:/users/antonio/desktop/plugins/myworld/src"});
			System.out.println(anzahlProbleme+" "+lines+" "+words);
		} else {
			File f = new File(args[0]);
			for(File file:f.listFiles()){
				if(file.isDirectory()){
					main(new String[]{file.getAbsolutePath()});
				} else {
					int line=0;
					String ret="";
					BufferedReader read = new BufferedReader(new FileReader(file));
					for(String s = ""; s!=null; s = read.readLine()){
						if(s.contains("!!")){
							ret+="\t! "+line+"\n";
							anzahlProbleme++;
						} else if(s.contains("??")){
							ret+="\t? "+line+"\n";
							anzahlProbleme++;
						}
						line++;
						words+=s.split(" ").length;
					}
					lines+=line-1;
					read.close();
					if(ret.length()>0){
						System.out.println(file.getName()+"\n"+ret);
					}
				}
			}
		}
		
	}
	
	/**
	--------------
	Sonderrechte des K�nigs nur innerhalb der Stadt...
	--------------
	Postservice?
	--------------
	Test all generated regions
		Terrain
		Weather
		Mobs
		Caves, nearly done (obsidian holes? - makes the way to nether to easy)
		Treasures
		PvP
		Done: Chat
		Done: Plotsservice
		...
	---------------
		Done: Xray control via anticheat+
	---------------
	Recipes for Conquest + support of this
		Done: about 60%
	---------------
	Weather+Seasons
		Done: Snow, Leaves, spawning of snowmen
		Todo: improve snow?
	---------------
		Done: Effects of own recipes
	---------------
	Things in code with "!!!"
	---------------
	Badlands-deco
		Done: Oase, Obelisk, Pyramide
		Todo: irgendwas f�r die Vulkane, Dungeons, vllt Tempel
	---------------
		Done: Policethread running?
	---------------
	ruins improving?
	---------------
		Done: plots visualizing
	---------------
	caves
		mehr Dekoration
			Lianen muss es noch irgendwo in der Welt geben...
	---------------
	Can get all blocks?
		Missing: Melone, Lianen, Spinnennetz, Glowstone, Sattel, Namensschild, ...?
		
		Lianen
			Nametag
		Glowstone -> theoretisch schon
			Akazien + Dark Oak
		Schwamm
			K�rbis dank Schneem�nnern
		cracked+chisseled stone bricks
		compressed ice - in work@recipemanager
		dead bush
		alle gro�en Blumen?
			all flower types??
			lily pad (seerose)
			skulls of monsters + players on death
			sattel
			melone
	---------------
	Biomtauscher
	Wetterbiome
	---------------
	extra dungeons?
	---------------
		Done: mob spawn system, inkl. Verzauberungen und so...
		Todo: maybe snowmen, enderdragon special drops like dias, armor, swords, ...
	---------------
		Done: hoster
			https://www.bisecthosting.com/ looks good :) -> is good :)
			12 cores, Linux, 2GB Ram = 24 slots for 6$/month
	---------------
	donation button ;)
		Todo: place paypal button on any site...
	---------------
		Done: nolagg compatible? -> integrated
		Todo: check if all done
	---------------
	extra marriages?
		By King via ranks
		Todo: /message@king
			-> like email account for each player
	---------------
		Done: spawn ender dragon
			Ritual, Make strong ender dragon, drops
	---------------
	bug fixes
		Done: tp between worlds
		Open: items, worldtype@spawnbuilder
	---------------
		Done: buy/sell/rename plots as king
	---------------
		Trees
		Todo: more :)
			bot. Garten + things in RuinPopulator
	---------------
	Extra Herausforderungen zum Sammeln:
		L�ufer 1km, 10km, 100km, 1000km, 10kkm, 100Mm, 1Gm...
		Schwimmer, Flieger, Springer,...
		M�rder 10x, 100x, 1000x, 10kx, 100kx
		Zeit auf dem Server (naja)
		Miner 10x 100x 1kx, 10kx, 100kx
		
		+ Belohnungen :)
			Geld / Dias / seltene Stoffe
	---------------
		Done: Sinnlos, da man die Statistiken auch so als Spieler sehen kann...
			"Gesch�pfter Wert berechnen lassen xD"
	---------------
		Done: AfK
	---------------
	Sprachen?
		Done: Engine, En, DE
		Todo: Fr, Es
	
	
	
	*/
	
	public static String prefix = ChatColor.WHITE+"["+ChatColor.RED+"Kingdom"+ChatColor.WHITE+"] ", badprefix = "�4[�fWildlings�4]�f ", serfix="[�2Server�f] ";
	public static String[] spawnArray = new String[]{"-15", "25", "-20"};
	public static int width = 524288;// halbe Weite des Rechteckes...
	public static World world;
	public static final String myWorldName="myWorld";
	public static String kingsname = "/";
	public static boolean isQueen = false;
	public static int amountOfKingsmoney;
	public static Plugin instance;
	public static ArrayList<Thread> threads = new ArrayList<>();

	public static WebServer webServer;
	public static long websiteUntil;
	
	public static String[] br = new String[]{
		"§aUse '/report <message>' to report players or bugs",
		"§aUse '/help recipes' to get information about new recipes :)",
		"§4Remember to use Optifine and Conquest!",
		"§cThank you for playing on §4miner952x.mcserver.ws:25637",
		"§bDid you know the world is §45632 blocks deep§b?",
		"§bDid you know there are §4180 new blocks and 194 new recipes§b registered?",
		"§bDid you know the map is generated from real map data? It's a city in Germany",
		"§aUse '/claim <plotname>' to claim your own plot, but don't expect you get much space for 100gn",
		"§aSee the stats on the right? That's your position and your money",
		"§2Ideas for the server? Just create a '/report #idea <message>'",
		"§cNobody answeres? You might can't hear them because they are far away.",
		"§cDied? Aground in the dream world? Just die again ;)",
		"§aUse '/list' to get interesting statistics :)",
		"§bProject for more than 2 years :D @WorldGenerator",
		"§4Annoyed by AntiCheat+? I am sorry. Please create a '/report' to let me know.",
		"§bCan't break logs by hand? Did you try in reallife? Just use twigs from leaves.",
		"§7Server by Antonio Noack = Miner952x :), Updates soon :)",
		"§bShaft collapsed? Maybe build some stabilisation :)",
		"§bLaggs in Nether? I know...",
		"§bUse '/sign' to get information about how to create a chest shop :) - you should own a plot for that.",
		"§aDon't like the layout features? Use '/layout false' to disable them :)",
		"Did you know there is a webserver on the SAME port? When I get it there will be a life map(my own and better dynmap :P)"
	};
	
	public Plugin(){
		instance = this;
	}
	
	@Override
	public void onEnable(){

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TPS(), 100L, 1);
		
		NASAHeight.ini(this.getDataFolder());
		Converter.ini(this.getDataFolder());
		
		try {
			XBlock.init();
			RecipeManager.init();
			
			// Cannot set item frames :(
			//System.out.println("Loading maps");
			//MapPortrait.ini(getDataFolder(), "test", "DDA", "biglogo");
			System.out.println("Loading Jena.ini()");
			Jena.ini();
			System.out.println("Loading Bank.ini()");
			Bank.ini();
			System.out.println("Loading Rank.ini()");
			Rank.ini();
			System.out.println("Loading Grundstueck.ini()");
			Grundstück.ini(getDataFolder());
			System.out.println("Loading SpawnManager.ini()");
			SpawnManager.ini(getDataFolder());
			System.out.println("Loading Weather.ini()");
			Weather.ini();
			System.out.println("Create World: "+myWorldName);
			createMyWorld();
			System.out.println("Adding Eventlistener");
			new BlockListener(this);
			System.out.println("Adding EarthAnimation(Spawn)");
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EarthAnimation(), 20*2, 20*2);// tick count
			System.out.println("Adding AntiLagg(alias laggremover)");
			AntiLagg.id=Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin.instance, new AntiLagg(), 100, 100);
			System.out.println("Adding Broadcaster");
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

				@Override
				public void run() {
					Bukkit.broadcastMessage(serfix+br[MathHelper.random.nextInt(br.length)]);
				}
				
			}, 12000L, 12000L);// alle 10min
			// !!! anticheat disabled
			//AntiCheat.load(getDataFolder());
			//anticheat = new AntiCheat();
			//anticheat.onEnable();
			
			HTTPRequestEvent.setCase404("<!DOCTYPE html><html><body><h1><b>404</b> Deine angefragte Seite wurde nicht gefunden / Your site wasn't found!</h1></body></html>");
			ReloadChunks.ini();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader read = new BufferedReader(new FileReader(new File(this.getDataFolder(), "data/motd.dat")));
			BlockListener.eventByKing=read.readLine();
			if(BlockListener.eventByKing==null)BlockListener.eventByKing="";
			read.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	static AntiCheat anticheat;
	
	public static void save(boolean really) throws IOException {
		System.out.println("Save()");
		Bank.save(instance.getDataFolder());
		Grundstück.save(instance.getDataFolder());
		Rank.save(instance.getDataFolder());
		//AntiCheat.save(instance.getDataFolder());
		UserReport.save(instance.getDataFolder(), really);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onDisable(){
		
		for(Thread t:threads){
			t.interrupt();
			t.stop();
		}
		
		try {
			save(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//anticheat.onDisable();
		
		Bukkit.getScheduler().cancelTasks(this);
	}
	
	
	
	static HashMap<String, String> playerTeleport = new HashMap<>();
	static HashMap<String, Boolean> isAfk = new HashMap<>();
	static HashMap<String, Location> isFlying = new HashMap<>();
	
	static boolean isFlying(String name){
		return isFlying.containsKey(name);
	}
	static boolean isAfk(String name){
		if(isAfk.containsKey(name)){
			return isAfk.get(name);
		} else return false;
	}
	
	public static void removeAfk(String name){
		if(isAfk.containsKey(name) && isAfk.get(name)){
			isAfk.put(name, false);
			Bukkit.broadcastMessage("§3+§f"+Rank.getNickName(name));
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(label.equalsIgnoreCase("anticheat") || label.equalsIgnoreCase("anti")){
			// AntiCheat gibt immer true zurück...
			return anticheat.onCommand(sender, cmd, label, args);
		}
		
		if(!(sender instanceof Player)){
			if(label.equalsIgnoreCase("w") && args.length>1){
				Player p = Bukkit.getPlayer(args[0]);
				if(p!=null && p.isOnline()){
					String m="§c[Console]§f";
					for(int i=1;i<args.length;i++){
						m+=" "+args[i];
					}
					p.sendMessage(m);
					System.out.println(p.getName()+" was '"+m+"' send.");
				} else System.out.println("Player not found :(");
			} else {
				System.out.println("No cheats aviable from console or command blocks!");
				System.out.println(TPS.memory());
				System.out.println("OS "+System.getProperty("os.name"));
				String osarch;
				System.out.println((osarch=System.getProperty("os.arch")).equals("amd64")?"x86_64":osarch);
				System.out.println("Version "+System.getProperty("os.version"));
				System.out.println("Java "+System.getProperty("java.version"));
				System.out.println("Processors "+Runtime.getRuntime().availableProcessors());
			}
			return false;
		}
		
		Player p = (Player) sender;
		if(Rank.hasPassword.contains(p.getName()) && BlockListener.loggedIn(p.getName()) && !label.equalsIgnoreCase("login") && !label.equalsIgnoreCase("report")){
			p.sendMessage(serfix+"You have to log in first.");
			return true;
		}
		if(label.equalsIgnoreCase("login")){
			if(Rank.hasPassword.contains(p.getName())){
				if(args.length==1){
					if(isServerInOffline){
						if(SpawnManager.getPassword(p.getName()).equalsIgnoreCase(args[0])){
							if(BlockListener.loggedIn(p.getName())){
								p.sendMessage(serfix+"Welcome back :)");
								p.removePotionEffect(PotionEffectType.SLOW);
								BlockListener.logIn.put(p.getName(), 100);
							} else {
								p.sendMessage(serfix+"Allready logged in!");
							}
						} else {
							p.sendMessage(serfix+"§4Wrong password!");
						}
					} else {
						p.sendMessage(serfix+"§bThanks, but we are in online mode now...");
					}
				} else if(args.length==0){
					return false;
				} else {
					p.sendMessage(serfix+"§4Wrong password!");
				}
			} else {
				p.sendMessage(serfix+"§bOK. Don't forget your password!\n"
						+ "Your password is §4"+SpawnManager.getPassword(p.getName()));
				Rank.hasPassword.add(p.getName());
			}
		} else if(label.equalsIgnoreCase("fly")){
			boolean fly = !isFlying(p.getName());
			if(fly){
				isFlying.put(p.getName(), p.getLocation());
			} else {
				p.teleport(isFlying.remove(p.getName()));
			}
			p.setAllowFlight(fly);
			p.sendMessage(serfix+Sprache.select(p.getName(), "Set spectator mode to "+(fly?"true":"false"), "Beobachtermodus "+(fly?"aktiviert":"deaktiviert"), null, null));
		} else if(label.equalsIgnoreCase("donate")){
			switch(Sprache.select(p.getName(), "1", "2", null, null)){
			case "1":
				p.sendMessage("There is no donation button jet, but you can donate via Paypal. My account: antonio-noack@gmx.de\n"
						+ "At the moment, you can get 1000gn per 1US$ if you want to. Send an email at antonio-noack@gmx.de to get your ingame money then.\n"
						+ "There are no special names or ranks, but if I am the king, you can get one as supporter or sth like this :)");
				break;
			case "2":
				p.sendMessage("Ich habe zwar noch keinen Spendebutton, aber ihr könnt über Paypal spenden: antonio-noack@gmx.de\n"
						+ "Wenn ihr Ingamegeld dafür wollt, könnt ihr 1000gn/US$ (~90ct) bekommen :)\n"
						+ "Solange ich der König bin könnt ihr auch gerne einen netten Rang und Rechte für farbiges Schreiben bekommen... "
						+ "wenn ichs nicht mehr bin fragt einfach den aktuellen König :)");
				break;
			}
		} else if(label.equalsIgnoreCase("accept")){
			if(playerTeleport.containsKey(p.getName())){
				String s;
				Player p2 = Bukkit.getPlayer((s=playerTeleport.get(p.getName())).split(" ")[0]);
				if(p2!=null){
					if(System.currentTimeMillis()<Long.parseLong(s.split(" ")[1])+120000){
						p2.sendMessage(prefix+Sprache.select(p2.getName(),
								"Teleport you to "+p.getDisplayName(),
								"Du wurdest zu "+p.getDisplayName()+" teleportiert.", null, null));
						p2.teleport(p);
						SpawnBuilder.change(p2, (BlockListener.trueLocation(p2.getLocation()).getBlockY()-16)/224, (BlockListener.trueLocation(p.getLocation()).getBlockY()-16)/224);
						playerTeleport.remove(p.getName());
					} else {
						p.sendMessage(prefix+Sprache.select(p.getName(),
								"The time of 120s is over!",
								"Die 120s-Frist ist vorüber!", null, null));
					}
				} else {
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"This player already left the server :(",
							"Dieser Spieler ist nicht mehr online :(", null, null));
				}
			} else {
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"No tp aviable!",
						"Kein tp verfügbar!", null, null));
			}
		} else if(label.equalsIgnoreCase("afk")){
			if(isAfk(p.getName())){
				isAfk.put(p.getName(), false);
				Bukkit.broadcastMessage("§3+§f"+Rank.getNickName(p.getName()));
			} else {
				final Player pl = p;
				final Location loc = p.getLocation().clone();
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
					@Override public void run(){
						if(pl.getLocation().distanceSquared(loc)<1){
							isAfk.put(pl.getName(), true);
							Bukkit.broadcastMessage("§3-§f"+Rank.getNickName(pl.getName()));
						} else {
							pl.sendMessage(Plugin.prefix+Sprache.select(pl.getName(),
									"Don't move! Your afk request was cancelled.",
									"Steh still! Deine afk-Anfrage wurde zurückgewiesen.", null, null));
						}
					}
				}, 100);
				
				p.sendMessage(Plugin.prefix+Sprache.select(p.getName(),
						"Don't move...",
						"Bewege dich nicht!", null, null));
			}
		} else if(label.equalsIgnoreCase("home") || label.equalsIgnoreCase("wakeup")){
			if(SpawnBuilder.deathMapNormal.containsKey(p.getName())){
				onCommand(p, null, "xtp", SpawnBuilder.deathMapNormal.get(p.getName()).split("\\."));
			} else onCommand(p, null, "xtp", spawnArray);
		} else if(label.equalsIgnoreCase("b") || label.equalsIgnoreCase("bilance") || label.equalsIgnoreCase("$$$")){
			if(args.length>0){
				switch(args[0].toLowerCase()){
				case "save":
					if(p.isOp()){
						int x=p.getLocation().getBlockX()/16, z=p.getLocation().getBlockZ()/16;
						for(int i=-5;i<6;i++){
							for(int j=-5;j<6;j++){
								p.getWorld().getChunkAt((x+i)*16, (z+j)*16).getBlock(0,0,0).setMetadata(MetaString.save, new FixedMetadataValue(this, p.getName()));
							}
						}
						p.sendMessage("done");
					} else p.sendMessage(serfix+"You need to be OP to use this command!");
					break;
				case "id":
					p.sendMessage(p.getLocation().add(0, -1, 0).getBlock().getTypeId()+":"+p.getLocation().add(0, -1, 0).getBlock().getData());
					break;
				case "ram":
				case "memory":
					p.sendMessage(TPS.memory());
					break;
				case "season":
					p.sendMessage(Weather.nowTime(Sprache.select(p.getName(), "t", "f", null, null).equals("t")) + " " + Weather.jetztTime());
					break;
				case "hdd":
					p.sendMessage("("+this.getDataFolder().getFreeSpace()/1024/1024/1024+"GB/"+this.getDataFolder().getUsableSpace()/1024/1024/1024+"GB)/"+this.getDataFolder().getTotalSpace()/1024/1024/1024+"GB");
					break;
				case "list":
					p.setPlayerListName(args.length==2?args[1].replace('&', '§').replace("\\&", "&"):"");
					break;
				case "rank":
					if(!p.isOp())break;
					int count=0;
					ArrayList<String[]> at = new ArrayList<>();
					for(int i=-29;i<30;i++){
						for(int j=-29;j<30;j++){
							if(i*i+j*j<900){
								for(int y=0;y<256;y++){
									Block b = world.getBlockAt(1024*1024+i, y, j);
									if(b.getTypeId()==54){
										Sign s = (Sign) b.getState();
										if(s.getLine(0).startsWith("#")){
											Rank.analyseCommand(s.getLines(), null, false);
											count++;
										} else if(s.getLine(0).startsWith("@")){
											at.add(s.getLines());
											count++;
										}
									}
								}
							}
						}
					}
					for(String[] s:at){
						Rank.analyseCommand(s, null, false);
					}
					p.sendMessage(serfix+"done... "+count+" signs encrypted!");
					break;
				case "webservice":
					if(args.length==2 && p.getName().equalsIgnoreCase("Miner952x")){
						if(args[1].equalsIgnoreCase("stop")){
							if(webServer!=null){
								try {
									webServer.close();
									webServer.t.interrupt();
								} catch (IOException e) {
									e.printStackTrace();
								}
								p.sendMessage(serfix+"Webserver stopped!");
							}
						} else {
							try {
								webServer = new WebServer(MathHelper.stringToInt(args[1], 80));
							} catch (IOException e) {
								e.printStackTrace();
								p.sendMessage("ERROR: "+e.getMessage());
							}
							p.sendMessage(serfix+"Webserver created!");
						}
					}
					break;
				/*case "file":
					if(args.length>1){
						File f = new File(args[1]);
						if(!f.exists()){
							p.sendMessage(serfix+args[1]+" doesn't exist!");
						} else {
							p.sendMessage(serfix+args[1]+" = "+f.getAbsolutePath());
							if(f.isDirectory()){
								for(File ff:f.listFiles()){
									if(f!=null)
										p.sendMessage(ff.getAbsolutePath());
								}
							}
						}
					} else {
						p.sendMessage(serfix+"Pls give me args..."+this.getDataFolder().getAbsolutePath());
					}
					break;
				case "cube":
					p.sendMessage("CubeID = "+Converter.getCubeID(MathHelper.ori(p.getLocation().getBlockX()), MathHelper.ori(p.getLocation().getBlockZ()))+
							"\nRealID = "+Converter.getRealCubeID(MathHelper.ori(p.getLocation().getBlockX()), MathHelper.ori(p.getLocation().getBlockZ())));
					break;*/
				default:
					Bank.sendStatus(p);
				}
			} else if(args.length==1 && p.getName().equalsIgnoreCase("Miner952x")){
				p.sendMessage(Weather.nowTime(false) + " " + Weather.jetztTime());
				Weather.setTime(Integer.parseInt(args[0]));
				p.sendMessage("-> "+Weather.nowTime(false) + " " + Weather.jetztTime());
				
			} else {
				Bank.sendStatus(p);
			}
		} else if(label.equalsIgnoreCase("bank")){
			if(args==null || args.length==0){
				Bank.sendStatus(p);
				p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
						"To get a list of all commands type '/bank ?'",
						"Um eine Liste aller Befehle zu bekommen, gib '/bank ?' ein :)", null, null));
			} else if(args[0].equalsIgnoreCase("?")){
				
				p.sendMessage(Bank.prefix+"\n"+Sprache.select(p.getName(), 
						  "How to get money back?\n"
						+ "    Click with an empty hand on a gold block\n"
						+ "How to earn money?\n"
						+ "    Type '/shop ?'\n"
						+ "How to deposit?\n"
						+ "    Click with the gold on a gold block!\n"
						+ "How to give another player money?\n"
						+ "    Type '/bank pay <player> <amount>' or '/p <player> <amount>'\n"
						+ "->interest rate<-\n"
						+ "    100%/year = 0.19%/day or 0.0026376%/minecraft day\n"
						+ "The only way to make businesses on signs is with your deposited money!\n"
						+ "You can use the commands '/b' and '/bank status' to get your balance.",
						
						  "Gold abbuchen?\n"
						+ "    Klicke mit einer leeren Hand auf einen Goldblock\n"
						+ "Gold verdienen?\n"
						+ "    Type '/shop ?'\n"
						+ "Gold einlagern?\n"
						+ "    Klicke mit dem Gold auf einen Goldblock!\n"
						+ "Wie gibt man jemandem Gold?\n"
						+ "    '/bank pay <Spielername> <Menge>' or '/p <Spielername> <Menge>'\n"
						+ "->Zinsen<-\n"
						+ "    100%/Jahr = 0.19%/Tag or 0.0026376%/Minecrafttag=20min\n"
						+ "Du kannst nur mit Gold von deinem Konto von Schildern kaufen!\n"
						+ "Mit den Befehlen '/b' und '/bank status' siehst du deine Goldmenge.", null, null));
			} else if(args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("b")){
				Bank.sendStatus(p);
			} else if(args[0].equalsIgnoreCase("pay")){
				if(args.length==3){
					long l;String n = p.getDisplayName();
					if(Bank.pay(p, args[1], l=parseLong(args[2]))){
						p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
								"Successfully send "+l+"gn to "+args[1],
								l+"gn wurden erfolgreich an "+args[1]+" gesendet", null, null));
						if((p=getPlayer(args[0]))!=null){
							p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
									n+" paid you "+l+"gn",
									n+" hat dir "+l+"gn überwiesen", null, null));
						}
					} else {
						Bank.sendErrMessage(p, 0);
					}
				} else {
					p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
							"Can't understand you. Try '/p <player> <amount>'",
							"Ich kann dich leider nicht verstehen. Probiere '/p <Spielername> <Geldmenge>'", null, null));
				}
			}
		} else if(label.equalsIgnoreCase("br") || label.equalsIgnoreCase("broadcast")){
			int k=0;
			Location tloc = BlockListener.trueLocation(p.getLocation());
			for(Player pl:Bukkit.getOnlinePlayers()){
				if(pl.getWorld()==world){
					k+=tloc.distance(BlockListener.trueLocation(pl.getLocation()));
				}
			}
			k/=1000;
			if(args==null || args.length==0){
				p.sendMessage(prefix+Sprache.select(p.getName(), 
						"You can broadcast a message via /br, but you have to pay 1gn/1000m for EVERY player on the server.\n"
						+ "   The costs are: ~"+k+"gn.",
						"Du kannst per /br einen Rundruf machen, musst aber pro 1000m Entfernung zu jedem Spieler 1gn zahlen.\n"
						+ "   Die Kosten laufen sich auf ~"+k+"gn.", null, null));
			} else {
				if(Bank.getBilance(p)<k){
					p.sendMessage(prefix+Sprache.select(p.getName(), 
							"You don't have enought money to broadcast more messages! It would cost "+k+"gn.",
							"Du hast nicht genug Geld um einen Rundruf zu machen. Es würde dich "+k+"gn kosten.", null, null));
				} else {
					if(Bank.substract(p.getName(), k)){
						String text = "";
						for(String add:args){
							text+=" "+add;
						}
						Bukkit.broadcastMessage("-> "+p.getDisplayName()+":"+text);
					} else {
						Bank.sendErrMessage(p, 0);
					}
				}
			}
		} else if(label.equalsIgnoreCase("claim") || label.equalsIgnoreCase("c")){
			if(args.length==1){
				String s = args[0];
				for(char c:s.toCharArray()){
					if(!((c>='a' && c<='z') || (c>='A' && c<='Z') || (c>='0' && c<='9') || c=='_' || c=='-')){
						p.sendMessage(prefix+Sprache.select(p.getName(),
								"Your plot name is invalid. You used: '"+c+"'. Allowed: A-Z, a-z, 0-9, '_' and '-'",
								"Dein Grundstücksname ist ungültig. Du hast '"+c+"' benutzt, es sind aber nur A-Z, a-z, 0-9, '_' und '-' erlaubt.", null, null));
						return true;
					}
				}
				if(s.length()>0){
					// ist der Spieler auf einem Grundstück?
					int x=MathHelper.ori(p.getLocation().getBlockX()), z=MathHelper.ori(p.getLocation().getBlockZ());
					if(Jena.type(x, z)<2){// wenn in Jena
						if(Grundstück.isGrundstück(x, z)){
							String oldowner;
							if((oldowner=Grundstück.isOwned(x, z))==null){
								int k;
								if(p.getName().equalsIgnoreCase(kingsname)){
									switch(k=Grundstück.getCosts(p, s, x, z, intByLong(Bank.getBilance("@king")), true)){
									case -1:// alles gut verlaufen
										break;
									default:
										p.sendMessage(prefix+Sprache.select(p.getName(), 
												"This plot costs "+k+". The "+(isQueen?"Queen":"King")+" only has "+Bank.getBilance("@king")+". Private plots can't be bought as "+(isQueen?"Queen":"King")+"!",
												"Dieses Grundstück kostet "+k+". "+(isQueen?"Die Königin":"Der König")+" hat nur "+Bank.getBilance("@king")+". Private Grundstücke können als "+(isQueen?"Königin":"König")+" nicht gekauft werden!", null, null));
										break;
									}
								} else {
									switch(k=Grundstück.getCosts(p, s, x, z, intByLong(Bank.getBilance(p)), true)){
									case -1:// alles gut verlaufen
										break;
									default:
										p.sendMessage(prefix+Sprache.select(p.getName(), 
												"This plot costs "+k+". You only have "+Bank.getBilance(p),
												"Dieses Grundstück kostet "+k+". Du hast aber nur "+Bank.getBilance(p), null, null));
										break;
									}
								}
							} else {
								p.sendMessage(prefix+Sprache.select(p.getName(),
										"This plot is allready claimed by "+oldowner+". Maybe she/he sells it.", 
										"Dieses Grundstück gehört bereits "+oldowner+". Vielleicht verkauft er/sie es.", null, null));
							}
						}
					}
				} else {
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"Invalid plot name: "+s,
							"Ungültiger Grundstücksname: "+s, null, null));
				}
			} else {
				p.sendMessage(prefix+Sprache.select(p.getName(), 
						"Please give your new plot a name. e.g. '/claim Santa_Maria'. Allowed chars: A-Z, a-z, 0-9, '_' and '-'",
						"Bitte gib deinem Grundstück einen Namen, z.B. '/claim Große_Villa'. Erlaubte Zeichen sind A-Z, a-z, 0-9, '_' und '-'", null, null));
			}
		} else if(label.equalsIgnoreCase("event")){
			if(Rank.hasPermisson(p.getName(), "e")){
				String ev = "";
				for(String s:args){
					ev+=" "+s;
				}
				BlockListener.eventByKing=ev.substring(1);
				p.sendMessage(serfix+"Set the MOTD-suffix to '"+ev+"'.");
			} else p.sendMessage(serfix+Sprache.select(p.getName(),
					"Only the king or sb with the permission 'e' can use this command",
					"Du hast nicht das nötige Recht 'e'!", null, null));
		} else if(label.equalsIgnoreCase("free")){
			if(Prison.hasInside(p.getName())){
				Prison.freeWithoutTP(p.getName());
			} else {
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"You are already free!",
						"Du bist schon frei!", null, null));
			}
		} else if(label.equalsIgnoreCase("modifiedby")){
			Block b = p.getLocation().getBlock().getChunk().getBlock(0, 0, 0);
			if(b.hasMetadata(MetaString.save)){
				p.sendMessage(serfix+Sprache.select(p.getName(),
						"This chunk was edited last by ",
						"Dieser Chunk wurde zuletzt bearbeitet von: ", null, null)+b.getMetadata(MetaString.save).get(0).asString());
			} else {
				p.sendMessage(serfix+Sprache.select(p.getName(),
						"This chunks wasn't edited until now!",
						"Dieser Chunk wurde noch nie bearbeitet!", null, null));
			}
		} else if(label.equalsIgnoreCase("name")){
			if(args.length==1){
				for(String key:Rank.getKeySet()){
					if(Rank.getNickName(key).equalsIgnoreCase(args[0])){
						p.sendMessage(prefix+args[0]+" = "+key);
						return true;
					}
				}
				p.sendMessage(prefix+args[0]+Sprache.select(p.getName(), " is no used nickname!", " ist kein Spitzname!", null, null));
			} else if(args.length==2 && args[0].equalsIgnoreCase("-")){
				p.sendMessage(prefix+Rank.getNickName(args[1])+" = "+args[1]);
			} else {
				p.sendMessage(prefix+Sprache.select(p.getName(), "Only one argument is allowed!", "Nur ein Argument ist erlaubt.", null, null));
			}
		} else if(label.equalsIgnoreCase("iamqueen") && !isQueen){
			if(p.getName().equalsIgnoreCase(kingsname)){
				isQueen = true;
				p.sendMessage(prefix+Sprache.select(p.getName(), "Welcome dear §cQueen", "Willkommen §cKönigin", null, null));
				Rank.setNickNameGetJoinMessage(p.getName(), p);
			} else p.sendMessage("Can only be changed by the King himself");
		} else if(label.equalsIgnoreCase("iamking") && isQueen){
			if(p.getName().equalsIgnoreCase(kingsname)){
				isQueen = false;
				p.sendMessage(prefix+Sprache.select(p.getName(), "Welcome dear §cKing", "Willkommen §cKönig", null, null));
				Rank.setNickNameGetJoinMessage(p.getName(), p);
			} else p.sendMessage("Can be only changed by the Queen herself");
		} else if(label.equalsIgnoreCase("layout")){
			boolean is = BlockListener.dolayout.containsKey(p.getName())?BlockListener.dolayout.get(p.getName()):true;
			BlockListener.dolayout.put(p.getName(), is=(args!=null && args.length>0 ? args[0].equalsIgnoreCase("true") || args[0].equals("1") : !is));
			p.sendMessage(prefix+Sprache.select(p.getName(), 
					"Set layout mode to "+is,
					"Layout-Modus wurde "+(is?"aktiv":"inaktiv")+" gestellt", null, null));
		} else if(label.equalsIgnoreCase("*") || label.equalsIgnoreCase("me")){
			p.sendMessage(prefix+Sprache.select(p.getName(),
				"Try to use *what you are doing* instead. It is much easier :D. You can _underline_ things too. /layout will disable this layout (so you can really write *as sth you do*)",
				"Nutze lieber *was du gerade machst*, denn das macht es deutlich einfacher und mit _kannst du sogar unterstreichen_. Mit dem Befehl /layout kannst du einstellen, ob die Schrift fett gemacht/unterstrichen werden soll.", null, null));
		} else if(label.equalsIgnoreCase("kiss")){
			Location l1=p.getLocation();World w=p.getWorld();
			Player rem=null;
			for(Player zwo:Bukkit.getOnlinePlayers()){
				if(p!=zwo && w==zwo.getWorld()){
					Location z1=zwo.getLocation();
					double dis2;
					if((dis2=z1.distanceSquared(l1))<0.4){// näher als 1m voneinander entfernt
						Location l2 = l1.add(l1.getDirection().multiply(z1.distance(l1)));
						if(l2.distanceSquared(z1)<0.2){
							// Spieler guckt diese Person an :)
							
							Location trueloc = BlockListener.trueLocation(l1.add(z1).multiply(0.5));
							if((dis2=l1.getDirection().distanceSquared(z1.getDirection().multiply(-1)))<0.16){
								// geküsster Spieler guckt diesen Spieler an...
								// richtiger Kuss
								
								Wolf wolf = (Wolf) world.spawnEntity(l1.add(z1).multiply(0.5).add(0,1,0), EntityType.WOLF);
								wolf.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 100));
								wolf.playEffect(EntityEffect.WOLF_HEARTS);
								wolf.remove();
								
								for(Player online:Bukkit.getOnlinePlayers()){
									if(BlockListener.trueLocation(online.getLocation()).distanceSquared(trueloc)<10000){
										online.sendMessage(Sprache.select(online.getName(),
												" §c\u2764 §o"+Rank.getNickName(p.getName())+" kissed "+Rank.getNickName(zwo.getName()),
												" §c\u2764 §o"+Rank.getNickName(p.getName())+" küsste "+Rank.getNickName(zwo.getName()), null, null));
									}
								}
							} else if(dis2>1){
								// Kuss in den Nacken :)
								
								Wolf wolf = (Wolf) world.spawnEntity(l1.add(z1).multiply(0.5).add(0,1,0), EntityType.WOLF);
								wolf.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 100));
								wolf.playEffect(EntityEffect.WOLF_HEARTS);
								wolf.remove();
								
								for(Player online:Bukkit.getOnlinePlayers()){
									if(BlockListener.trueLocation(online.getLocation()).distanceSquared(trueloc)<1000){
										online.sendMessage(Sprache.select(online.getName(),
												" §c\u2764 §o"+Rank.getNickName(p.getName())+" kissed "+Rank.getNickName(zwo.getName())+" slightly in the neck",
												" §c\u2764 §o"+Rank.getNickName(p.getName())+" küsste "+Rank.getNickName(zwo.getName())+" sanft in den Nacken", null, null));
									}
								}
							} else {
								// Kuss auf die Wange, also nicht ganz so viele Herzchen ;)
								
								Wolf wolf = (Wolf) world.spawnEntity(l1.add(z1).multiply(0.5).add(0,1,0), EntityType.WOLF);
								wolf.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 100));
								wolf.playEffect(EntityEffect.WOLF_HEARTS);
								wolf.remove();
								
								for(Player online:Bukkit.getOnlinePlayers()){
									if(BlockListener.trueLocation(online.getLocation()).distanceSquared(trueloc)<3000){
										online.sendMessage(Sprache.select(online.getName(),
												" §c\u2764 §o"+Rank.getNickName(p.getName())+" kissed "+Rank.getNickName(zwo.getName())+" on the cheek",
												" §c\u2764 §o"+Rank.getNickName(p.getName())+" küsste "+Rank.getNickName(zwo.getName())+" auf die Wange", null, null));
									}
								}
							}
							return true;
						}
					} else if(dis2<2){
						rem=zwo;
					}
				}
			}
			if(rem!=null){
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"Get closer...",
						"Kommt euch noch ein Stück näher...", null, null));
			} else {
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"Nobody to kiss",
						"Niemand in Kussreichweite", null, null));
			}
		} else if(label.equalsIgnoreCase("hug")){
			Location l1=p.getLocation();World w=p.getWorld();
			Player rem=null;
			for(Player zwo:Bukkit.getOnlinePlayers()){
				if(p!=zwo && w==zwo.getWorld()){
					Location z1=zwo.getLocation();
					double dis2;
					if((dis2=z1.distanceSquared(l1))<0.6){// 0.4=näher als 1m voneinander entfernt
						Location l2 = l1.add(l1.getDirection().multiply(z1.distance(l1)));
						if(l2.distanceSquared(z1)<0.2){
							// Spieler guckt diese Person an :)
							Location trueloc = BlockListener.trueLocation(l1.add(z1).multiply(0.5));
							for(Player online:Bukkit.getOnlinePlayers()){
								if(BlockListener.trueLocation(online.getLocation()).distanceSquared(trueloc)<3000){
									online.sendMessage(Sprache.select(online.getName(),
											" §o"+Rank.getNickName(p.getName())+" hugged "+Rank.getNickName(zwo.getName()),
											" §o"+Rank.getNickName(p.getName())+" umarmte "+Rank.getNickName(zwo.getName()), null, null));
								}
							}
							return true;
						}
					} else if(dis2<2){
						rem=zwo;
					}
				}
			}
			if(rem!=null){
				p.sendMessage(serfix+Sprache.select(p.getName(),
						"Get closer...",
						"Kommt euch noch ein Stück näher...", null, null));
			} else {
				p.sendMessage(serfix+Sprache.select(p.getName(),
						"Nobody to hug :(",
						"Niemand in Reichweite :(", null, null));
			}
		} else if(label.equalsIgnoreCase("blownkiss")){
			// !!! s.o.
			p.sendMessage(prefix+"I am sorry this feature isn't implemented jet. Maybe come close together and use /kiss or /hug ;)");
		} else if(label.equalsIgnoreCase("help") || label.equalsIgnoreCase("?") || label.equalsIgnoreCase("command")){
			if(args==null || args.length==0){
				p.sendMessage(serfix+Sprache.select(p.getName(),
						"§cHelp: list of all commands§f\n"
						+ (p.getName().equalsIgnoreCase(kingsname)?isQueen?
						  "   /§aIamKing§f Change your gender to male\n":
						  "   /§aIamQueen§f Change your gender to female\n":"")
						+ "   /§aafk§f Away from keyboard\n"
						+ "   /§abank§f Get information about the bank\n"
						+ "   /§ab§7[balance]§f Get your balance\n"
						+ "   /§abr§7[broadcast]§f <message>\n"
						+ "   /§ac§7[claim]§f <plotname> Claim a plot\n"
						+ "   /§alag§7[lagg]§f <mobs> <friendly> clear by kill\n"
						+ "   /§alayout§f <true/false> Add layout features?\n"
						+ "   /§alist§f <listname> Interesting statistics\n"
						+ "   /§ap§7[pay]§f <playername> <amount>\n"
						+ "   /§a$§7[price]§f Gives you a price for the plot, you're standing on\n"
						+ "   /§ar§7[rename]§f <plotname> <newplotname>\n"
						+ "   /§asp§7[splot, showplot]§f Shows the plot area you're standing on\n"
						+ "   /§atp§7[teleport]§f <name> Accept + §cCosts 1gn/100m ANYWAY�f\n"
						+ "   /§aaccept§f accept a tp offer\n"
						+ "   /§awakeup§7[home]§f tp to home\n"
						+ (Rank.hasPermisson(p.getName(), "spawn")?
						  "   /§aspawn§f Will tp you to spawn\n":"")
						+ "   /§aw§7[tell, wisper]§f <name> Wisper (1gn/use)\n"
						+ "   /§afly§f Change mode to spectator mode\n"
						+ "   /§akiss,hug§f a person",
						
						"§cHilfe: Liste aller Befehle§f\n"
								+ (p.getName().equalsIgnoreCase(kingsname)?isQueen?
								  "   /§aIamKing§f Change your gender to male\n":
								  "   /§aIamQueen§f Change your gender to female\n":"")
								+ "   /§aafk§f Away from keyboard\n"
								+ "   /§abank§f Informationen zur Bank\n"
								+ "   /§ab§7[balance]§f Kontostand anzeigen\n"
								+ "   /§abr§7[broadcast]§f <message> Rundruf\n"
								+ "   /§ac§7[claim]§f <plotname> Kaufe ein Grundstück\n"
								+ "   /§alag§7[lagg]§f <mobs> <friendly> clear by kill\n"
								+ "   /§alayout§f <true/false> Layout an/aus\n"
								+ "   /§alist§f <listname> Interessante Statistiken\n"
								+ "   /§ap§7[pay]§f <playername> <amount>\n"
								+ "   /§a$§7[price]§f Zeigt dir den Grundstückspreis(worauf du stehst)\n"
								+ "   /§ar§7[rename]§f <plotname> <newplotname>\n"
								+ "   /§asp§7[splot, showplot]§f Zeigt die Grundstücksfläche\n"
								+ "   /§atp§7[teleport]§f <name> Accept + §cKostet 1gn/100m TROTZDEM§f\n"
								+ "   /§aaccept§f akzeptiere das tp-Angebot\n"
								+ "   /§awakeup§7[home]§f Tp nach Hause\n"
								+ (Rank.hasPermisson(p.getName(), "spawn")?
								  "   /§aspawn§f Teleportiert dich zum Spawn\n":"")
								+ "   /§aw§7[tell, wisper]§f <name> Flüstern (1gn/Benutzung)\n"
								+ "   /§afly§f Wechsle in den Beobachtermodus\n"
								+ "   /§akiss,hug§f Küsse/Umarme jemanden", null, null));
			} else if(args[0].toLowerCase().startsWith("recipe")){
				if(args.length==1){
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"Please gimme moi some names to look for :) | Use 'in:' at the begining for ingredient search",
							"Bitte gib noch deinen Suchnamen ein. Wenn du 'in:' vor deine Namen schreibst, wird nach diesen Inhaltsstoffen gesucht", null, null));
				} else {
					
					// suche nach Inhaltsstoffem
					if(args[1].startsWith("in:")){
						
						String ret = prefix+Sprache.select(p.getName(), "§cIngredient Search§f:\n", "�cInhaltsstoffsuche�f:\n", null, null);
						
						args[1]=args[1].substring(3);
						
						String not="", result="", perfect="";
						
						Material m;
						
						int index=0;
						Material[] lookfor = new Material[100];
						for(int i=args[1].length()>0?1:2;i<args.length;i++){
							for(String part:args[i].toUpperCase().split(",")){
								if((m=Material.getMaterial(part))!=null){
									lookfor[index++]=m;
								} else {
									not+=", "+args[i];
								}
							}
						}
						
						if(not.length()>0){
							not = not.substring(2);
							ret+=Sprache.select(p.getName(), "   §aUnknown materials§f: ", "   §aUnbekannte Materialien§f: ", "   §aMatériaux inconnues§f: ", null)+not+"\n";
						}
						int t;
						// wenn nur ein Material angegeben wurde, dieses aber unbekannt ist...
						if(index==0 && args.length-(t=(args[1].length()>0?1:2))==1){
							not = args[t].toUpperCase();
							int best = 1000000, x;
							for(Material mat:Material.values()){
								if((x=StringCompare.computeLevenshteinDistance(not, mat.name()))<best){
									best = x;
									result = mat.name();
								} else if(x==best){
									result+=", "+mat.name();
								}
							}
							ret+=Sprache.select(p.getName(), "   §aOffres§f: ", "   §aVorschläge§f: ", null, null)+result+"\n";
							result = "";
						}
						
						int best=0, x, y;
						String perf;
						for(MyRecipe r:RecipeManager.recipes){
							x=y=0;
							for(XMaterial used:r instanceof SRecipe?r.mats:r.toUse){
								y++;
								for(int i=0;i<index;i++){
									if(used.m==lookfor[i]){
										x++;
										break;
									}
								}
							}
							
							// alle Inhaltsstoffe des Rezeptes sind erfüllt -> kommt 100%if auf die Liste :)
							if(y==x && y>0 && !perfect.contains(", "+(perf=r.getResultName()))){
								perfect+=", "+perf;
								if(x>best){
									best = x;
								}
							} else if(x>best){// mehr Stoffe als zuvor sind enthalten -> kommt nach ganz oben...
								best = x;
								result = ", "+r.getResultName();
							} else if(x==best && !result.contains(", "+(perf=r.getResultName()))){
								result += ", "+perf;
							}
							
						}
						
						if(perfect.length()>0){
							ret+=Sprache.select(p.getName(),
									"   §aPerfect results§f: ",
									"   §aPerfekte Ergebnisse§f: ", null, null)+perfect.substring(2)+"\n";
						}
						if(result.length()==0 || best==0){
							ret+=Sprache.select(p.getName(),
									"   §cNo results!",
									"§cKeine Ergebnisse!", null, null);
						} else {
							ret+=Sprache.select(p.getName(),
									"   §a"+best+" hit"+(best>1?"s":"")+" @§f",
									"   §a"+best+" §bereinstimmung"+(best>1?"en":"")+" bei§f ", null, null)+result.substring(2);
						}
						p.sendMessage(ret);
					} else {
						String s = "";
						for(int i=1;i<args.length;i++){
							s+=" "+args[i];
						}

						s=s.substring(1).toUpperCase();
						String ret = "\n  §c<"+s+">§f\n \n";
						
						int best=100000, x;
						ArrayList<MyRecipe> res = null;
						for(MyRecipe r:RecipeManager.recipes){
							if((x=StringCompare.computeLevenshteinDistance(s, r.getResultName().toUpperCase()))<best){
								best = x;
								res = new ArrayList<>();
								res.add(r);
							} else if(x==best){
								res.add(r);
							}
						}
						
						for(MyRecipe r:res){
							ret+=r.show()+"\n";
						}
						p.sendMessage(ret);
					}
				}
			} else switch(args[0].toLowerCase()){
			case "king":
				sendKingsmessages(p.getName());break;
			case "lag":
			case "lagg":
				p.sendMessage(prefix+"/§alag§7[lagg]§f <mobs> <friendly> kills entities to remove your lag. Use true/false or 1/0 as arguments.");break;
			default:
				p.sendMessage(prefix+Sprache.select(p.getName(), "No help found.", "Zu diesem Thema wurde keine Extrahilfe gefunden.", null, null));
			}
		} else if(label.equalsIgnoreCase("idontlikemyrank")){
			String old;
			if(Rank.players.containsKey(p.getName())){
				old = Rank.players.get(p.getName()).label;
				Rank.players.remove(p.getName());
				Rank.setNickNameGetJoinMessage(p.getName(), p);
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"Removed successfully your rank!",
						"Dein Rang wurde erfolgreich entfernt!", null, null));
				Player king = Bukkit.getPlayer(kingsname);
				if(king!=null && king.isOnline()){
					king.sendMessage(prefix+p.getName()+" removed his old rank '"+old+"'");
				}
			} else {
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"Can not remove sth you don't have!",
						"Du kannst nichts entfernen, was du nicht hast.", null, null));
			}
		} else if(label.equalsIgnoreCase("king")){
			p.sendMessage(prefix+Sprache.select(p.getName(),
					kingsname+" is your "+(Plugin.isQueen?"Queen":"King")+".",
					kingsname+" ist dein"+(isQueen?"e Königin.":" König."), null, null));
			onCommand(p, null, "name", new String[]{"-", kingsname});
		} else if(label.equalsIgnoreCase("lag") || label.equalsIgnoreCase("lagg")){
			boolean items=true, monsters=false, friendly2=false;
			switch(args.length){
			case 3:items	= p.isOp() && (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1"));
			case 2:friendly2	= args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("1");
			case 1:monsters = args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1");
			}
			AntiLagg.removeLagRelativeTo(p, items && !monsters, items, friendly2);
		} else if(label.equalsIgnoreCase("lang")){
			if(args==null || args.length==0){
				Sprache.pls.put(p.getName(), Used.English);
			} else {
				Sprache.pls.put(p.getName(), Used.byShortcut(args[0]));
			}
			p.sendMessage(Sprache.select(p.getName(),
					"Changed your language to English!",
					"Sprache auf Deutsch umgestellt!",
					"La langue �tait adapt�e au francais!",
					"Fue cambiado a espa�ol!"));
		} else if(label.equalsIgnoreCase("list")){
			if(args==null || args.length==0 || args[0].equals("?") || args[0].equalsIgnoreCase("help")){
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"§cHelp: list of all lists :)§f\n"
						+ "   �arichest�f: The three richest people\n"
						+ "   �aplots�f: Your plots: plotname x z\n"
						+ "   �applots�f: Public plots (may need a /rank to build there)\n"
						+ "   �aranks�f: All ranks and their permissions",
						
						"�cHilfe: Die Liste aller Listen :)�f\n"
						+ "   �arichest�f: Die drei reichsten Spieler\n"
						+ "   �aplots�f: Deine Grundstücke und x, sowie z\n"
						+ "   �applots�f: �ffentliche Grundstücke (k�nnen Rechte erfordern)\n"
						+ "   �aranks�f: Alle R�nge und deren Rechte", null, null));
			} else if(args[0].equalsIgnoreCase("richest")){
				String s;
				p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
						"Richest people:\n"+(s=Bank.richest3People()),
						"Reichste Spieler:\n"+s,
						null, null));
			} else if(args[0].equalsIgnoreCase("plots") || args[0].equalsIgnoreCase("plot")){
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"�cLists: your plots�f", "�cDeine Grundstücke�f", null, null)+Grundstück.getMyPlots(p.getName()));
			} else if(args[0].equalsIgnoreCase("pplots") || args[0].equalsIgnoreCase("publicplots")){
				p.sendMessage(prefix+Sprache.select(p.getName(), "�cLists: public plots�f", "�c�ffentliche Grundstücke�f", null, null)+Grundstück.getMyPlots("@king"));
			} else if(args[0].equalsIgnoreCase("ranks") || args[0].equalsIgnoreCase("rank")){
				if(args.length==2){
					args[1]=args[1].replace('&', '�');
					if(Rank.ranks.containsKey(args[1])){
						p.sendMessage(prefix+"�cLists: ranks:�f \""+args[1]+"\""+Rank.permissionsAsString(Rank.ranks.get(args[1]).permissions).replace("-", ", "));
					} else if(args[1].equalsIgnoreCase("zero")){
						p.sendMessage(prefix+"�cRank.zero�f has zero permissions... logically, isn't it?");
					} else {
						int k = MathHelper.stringToInt(args[1], 0)-1;
						if(k>=0 && k<Rank.ranks.size()){
							args[1] = Rank.ranks.keySet().toArray(new String[Rank.ranks.size()])[k];
							if(args[1].equalsIgnoreCase("none")){
								p.sendMessage(prefix+"�cRank.zero�f has zero permissions... logically, isn't it?");
							} else {
								Rank r = Rank.ranks.get(args[1]);
								p.sendMessage(prefix+"�cLists: ranks:�f \""+args[1]+"�f\""+Rank.permissionsAsString(r.permissions).replace("-", ", "));
								String ret="";
								for(String name:Rank.players.keySet()){
									if(Rank.players.get(name).equals(r)){
										ret+=", "+name;
									}
								}
								p.sendMessage("This rank is owned by "+ret.substring(2));
							}
						} else {
							p.sendMessage(prefix+Sprache.select(p.getName(),
									"�cLists: ranks�f\nNo rank with the name \""+args[1]+"�f\" found!",
									"�cListen: R�nge�f\nEs wurde kein Rang mit dem Namen \""+args[1]+"\" gefunden!", null, null));
						}
					}
				} else {
					String ret = prefix+"�cLists: ranks�f";
					int i=0;
					for(Rank r:Rank.ranks.values()){
						ret+="\n   ("+(++i)+") "+r.label+"�f";
					}
					if(p.getName().equalsIgnoreCase(kingsname)){
						p.sendMessage(ret+"\n"
								+ "   Use �c/list ranks <rankname>�f to get the permissions\n"
								+ "   Use �csigns with '#rankname'�f at first line to define a new rank\n"
								+ "  On the other lines, you can write its permissions\n"
								+ "   Use �csigns with '@rankname'�f @1.line to give a player his rank\n"
								+ "  On the other lines, you have to write theirs names\n"
								+ "   Use �c/name <playername>�f to get their right names :)\n"
								+ "   You can marry people via giving them the same name and\n"
								+ "  buy their plot, then give them the permission to build there :)\n"
								+ "  Of course you can take money for the plot and their marriage :)");
					} else {
						p.sendMessage(ret+"\n�aUse /list ranks <rankname> to see the permissions");
					}
				}
			}
		} else if(label.equalsIgnoreCase("price") || label.equals("$")){
			int x=MathHelper.ori(p.getLocation().getBlockX()), z=MathHelper.ori(p.getLocation().getBlockZ());
			if(Jena.type(x, z)<2){// wenn in Jena
				if(Grundstück.isGrundstück(x, z)){
					String oldowner;
					if((oldowner=Grundstück.isOwned(x, z))==null){
						int k;
						boolean king = p.getName().equalsIgnoreCase(kingsname);
						switch(k=Grundstück.getCosts(p, "#", x, z, intByLong(Bank.getBilance(king?"@king":p.getName())), false)){
						case -1:
							p.sendMessage(prefix+"�4You bought this plot for -1gn. This is an error. Please report to your admin: Miner952x");
							break;
						default:
							if(king){
								p.sendMessage(prefix+"This plot costs "+k+". Your grace has "+Bank.getBilance("@king"));
							} else p.sendMessage(prefix+Sprache.select(p.getName(),
									"This plot costs "+k+". (You have "+Bank.getBilance(p)+")",
									"Dieses Grundstück kostet "+k+". (Du hast "+Bank.getBilance(p)+")", null, null));
							break;
						}
					} else {
						p.sendMessage(prefix+Sprache.select(p.getName(), 
								"This plot is allready claimed by "+oldowner+". Maybe she/he sells it.",
								"Dieses Grundstück geh�rt bereits "+oldowner+". Vielleicht verkauft er es.", null, null));
					}
				} else {
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"This is no plot!",
							"Hier ist kein Grundstück!", null, null));
				}
			} else {
				p.sendMessage(badprefix+Sprache.select(p.getName(),
						"You're out of the kingdom. There are no rules!",
						"Du bist au�erhalb des K�nigreiches. Hier gibt es keine Regeln mehr!", null, null));
			}
			return true;
		} else if(label.equalsIgnoreCase("r") || label.equalsIgnoreCase("rename")){
			if(args.length==2){
				boolean ok = true;
				name:for(char c:args[1].toCharArray()){
					if(!"ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".contains(c+"")){
						ok = false;
						p.sendMessage(prefix+Sprache.select(p.getName(),
								"Your plot name can't contain the symbol "+c,
								"Dein Grundstücksname darf das Symbol '"+c+"' nicht enthalten.", null, null));
						break name;
					}
				}
				if(args[1].length()<3 || args[1].length()>16){
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"Your new plot name can't be longer than 16 or shorter than 3 symbols!",
							"Dein Grundstücksname kann nicht l�nger als 16 oder k�rzer als 3 Zeichen sein!", null, null));
				} else if(ok) try {
					if(Grundstück.rename(args[0], args[1], p.getName())){
						p.sendMessage(prefix+"Successfully renamed your plot to \""+args[1]+"\" :)");
					} else if(p.getName().equalsIgnoreCase(kingsname) && Grundstück.rename(args[0], args[1], "@king")){
						p.sendMessage(prefix+"Successfully renamed "+(Plugin.isQueen?"QUEEN":"KING")+"S plot to \""+args[1]+"\" :)");
					} else {
						p.sendMessage(prefix+Sprache.select(p.getName(), "You have no plot with the name "+args[0]+".", "Du hast kein Grundstück namens "+args[0]+"!", null, null));
					}
				} catch (IOException e) {
					e.printStackTrace();
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"Error occured! I am sorry...",
							"Tut mir leid, aber ein Fehler ist aufgetreten...",
							"Pardon. Il y avait une erreur", null));
				}
			} else {
				if(args.length==3 && p.getName().equalsIgnoreCase(kingsname)){
					boolean ok = true;
					name:for(char c:args[1].toCharArray()){
						if(!"ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".contains(c+"")){
							ok = false;
							p.sendMessage(prefix+Sprache.select(p.getName(), "Your name can't contain the symbol "+c, "Dein Name darf das Symbol '"+c+"' nicht enthalten!", null, null));
							break name;
						}
					}
					if(args[1].length()<3 || args[1].length()>16){
						p.sendMessage(prefix+"The new plot name can't be longer than 16 or shorter than 3 symbols!");
					} else if(ok) try {
						if(Grundstück.rename(args[0], args[1], "@king")){
							p.sendMessage(prefix+"Successfully renamed "+(Plugin.isQueen?"QUEEN":"KING")+"s plot to \""+args[1]+"\" :)");
						} else {
							p.sendMessage(prefix+"Your grace has no plot with the name "+args[0]+".");
						}
					} catch (IOException e) {
						e.printStackTrace();
						p.sendMessage(prefix+"Error occured! I am sorry...");
					}
				} else {
					p.sendMessage(prefix+"Your plotname mustn't contain spaces!");
				}
			}
		} else if(label.equalsIgnoreCase("rank") || label.equalsIgnoreCase("ranks")){
			if(args.length==0 && p.getName().equalsIgnoreCase(kingsname)){
				p.sendMessage("�f[�cFor the "+(Plugin.isQueen?"Queen":"King")+" only :)�f]: A list of all rank options\n seperate by - e.g. spawn-chatCol-build *\n"
						+ "�aspawn�f: /spawn\n"
						//+ "�axtp�f: /xtp <x> <y> <z>\n"
						+ "�acol�f: use colors in chat (e.g. &4)\n"
						+ "�abuild *�f: build everywhere on public plots\n"
						+ "�abuild <name>�f: build at this plot\n"
						+ "�abuild <n*me>�f: build at all plots with n<any char>me\n"
						+ "�apol�f: police, meaning put people into prison\n"
						+ "�aspol�f: super police, meaning can put police (wo)men into prison\n"
						+ "�ae�f: /event <MOTD suffix>\n"
						+ "-> may be more...");
			} else {
				onCommand(sender, cmd, "list", argsBefore("ranks", args));
			}
		} else if(label.equalsIgnoreCase("recipe")){
			onCommand(sender, cmd, "help", argsBefore("recipes", args));
		} else if(label.equalsIgnoreCase("reload")){
			p.sendMessage("�c[ERROR] �fUsing /reload is deprecated!");
		} else if(label.equalsIgnoreCase("report")){
			if(args==null || args.length==0){
				p.sendMessage(Plugin.prefix+Sprache.select(p.getName(),
						"This command can be used to create a report. Please see that I can't read everything if you write lot's of things :) Spammers may be banned!",
						"Wenn du ein ernstes Problem hast, bei dem dir kein normaler Spieler aber ein Admin helfen kann, schreibe es hier hinein :)", null, null));
			} else {
				String message = "";
				for(String s:args){
					message+=" "+s;
				}
				UserReport.createReport(p, message.substring(1));
			}
		} else if(label.equalsIgnoreCase("save")){
			if(Rank.hasPermisson(p.getName(), "save")){
				try {
					save(args.length==1);
					p.sendMessage(prefix+"Saved successfully :)");
				} catch(IOException e){
					p.sendMessage(prefix+"�4"+e.getClass().getName()+" ->\n�a"+e.getMessage()+" by "+e.getCause());
				}
			}
		} else if(label.equalsIgnoreCase("shop")){
			p.sendMessage(prefix+Sprache.select(p.getName(),
					"�2Create a SHOP via signs�f:\n"
					+ "Place the sign near a chest on your plot and write:\n"
					+ "   bank account\n"
					+ "   material or ID\n"
					+ "   price\n"
					+ "   amount\n"
					+ "\n"
					+ "e.g. 17 is every type of log, 17:1 is spruce, 17:2 only birch",
					"�2Erstelle einen Schilder-SHOP�f:"
					+ "Setze ein Schild neben eine Kiste und schreibe:\n"
					+ "   Bankaccount-Name\n"
					+ "   Material oder ID\n"
					+ "   Preis\n"
					+ "   Menge\n"
					+ "\n"
					+ "Bsp: 17 ist jeder Stammtyp, 17:1 nur Fichte und 17:2 nur Birke", null, null));
		} else if(label.equalsIgnoreCase("sp") || label.equalsIgnoreCase("splot") || label.equalsIgnoreCase("showplot")){
			int x = MathHelper.ori(p.getLocation().getBlockX()), z = MathHelper.ori(p.getLocation().getBlockZ());
			if(Grundstück.isGrundstück(x, z)){
				for(Grundstück g:Grundstück.place){
					if(g.contains(x-g.x, z-g.z)){
						if(args==null || args.length==0){
							g.showYourself(p, 5);
						} else {
							g.showYourself(p, MathHelper.stringToInt(args[0], 5));
						}
						return true;
					}
				}
				p.sendMessage(prefix+Sprache.select(p.getName(), 
						"This plot has to be paid once before you can see details!",
						"Dieses Grundstück muss einmal gekauft worden sein bevor du Details sehen kannst!", null, null));
			} else {
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"You have to stand on a plot!",
						"Bitte stelle dich auf das Grundstück!", null, null));
			}
		} else if(label.equalsIgnoreCase("spawn")){
			if(/*Rank.hasPermisson(p.getName(), "spawn") && */(p.getWorld()==world || p.getGameMode()==GameMode.CREATIVE)){
				//if(args.length==1){
				//	Prison.inside.add(new Prisoner(p.getName(), 10000+System.currentTimeMillis()));
				//} else
				
				onCommand(sender, null, "xtp", spawnArray);
			} else {
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"�cYou don't have the permission to use this command!",
						"�cDu hast nicht die ben�tigten Rechte um diesen Befehl zu nutzen.", null, null));
			}
		} else if(label.equalsIgnoreCase("suicide")){
			p.setHealth(0.0);
			p.damage(1000.0);
		} else if(label.equalsIgnoreCase("tp") || label.equalsIgnoreCase("teleport") || label.equalsIgnoreCase("tpa")){
			if(args==null || args.length!=1){
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"/tp <playername>, but it costs 1gn/100m!",
						"/tp <Spielername>, kostet aber 1gn/100m!", null, null));
			} else {
				Player p2 = getPlayer(args[0]);
				if(p2!=null){
					if(p2.getWorld()==p.getWorld()){
						int distance = (int) BlockListener.trueLocation(p.getLocation()).distance(BlockListener.trueLocation(p2.getLocation()));
						if(Bank.getBilance(p)>=distance/100){
							if(Bank.substract(p.getName(), distance/100)){
								p2.sendMessage(prefix+p.getDisplayName()+Sprache.select(p2.getName(), 
										" would like to tp to you. Do you /accept? The offer ends in 120s!",
										" w�rde sich gerne zu dir tpn. '/accept' -ierst du? Das Angebot endet in 120s!", null, null));
								p.sendMessage(prefix+Sprache.select(p.getName(),
										"Sent tp request to "+p2.getDisplayName(),
										"tp-Anfrage an "+p2.getDisplayName()+" gesendet ", null, null));
								playerTeleport.put(p2.getName(), p.getName()+" "+System.currentTimeMillis());
							} else {
								Bank.sendErrMessage(p, 0);
							}
						} else {
							p.sendMessage(prefix+Sprache.select(p.getName(), 
									"You haven't got enought money! You would need "+distance/100+" for the "+distance+"m.",
									"Du hast nicht genug Geld! Du br�uchtest "+distance/100+" f�r die "+distance+"m.", null, null));
						}
					} else {
						p.sendMessage(serfix+"/tp is only possible if you are in the same world!");
					}
				} else {
					p.sendMessage(prefix+Sprache.select(p.getName(),
							"Cannot find player "+args[0]+"!",
							"Ich kann keinen Spieler namen s"+args[0]+" finden!", null, null));
				}
			}
		} else if(label.equalsIgnoreCase("tps")){
			String s;
			p.sendMessage(prefix+(s="Ticks/second = "+Math.round(TPS.getTPS()*1000)*0.001));
			System.out.println(s);
		} else if(label.equalsIgnoreCase("xtp")){
			if(args!=null && args.length>0 && (cmd == null || p.isOp() || p.getGameMode()==GameMode.CREATIVE)){
				if(args.length==3){
					int h = Integer.parseInt(args[1]) + 4800, y = 0;
					
					int a, b=0;
					k:for(a=-2;a<3;a++){
						for(b=-2;b<3;b++){
							if((y=Generator.basey(a*2*width, b*2*width))<h && y+256>h){
								break k;
							}
						}
					}
					
					int x=p.getLocation().getBlockX(), z=p.getLocation().getBlockZ();
					p.teleport(new Location(world, Integer.parseInt(args[0])+a*2*width, h-y, Integer.parseInt(args[2])+b*2*width));
					
					if(p.getWorld()==world){
						SpawnBuilder.change(p, (Generator.basey(x, z)-16)/224, (h-16)/224);
					}
				}
			}
		} else if(label.equalsIgnoreCase("yourgrace")){
			if(p.getWorld().getName().equalsIgnoreCase(myWorldName)){// teleportiert den Spieler auf die ISS
				SpawnBuilder.onJoin(p, true, false);
			} else if(args==null || args.length==0){
				Rank.putNickName(p.getName(), p.getName());
				Rank.players.put(p.getName(), Rank.zero);
				p.sendMessage(SpawnBuilder.prefix+Sprache.select(p.getName(),
						"Great! You found all signs - I'll tp you in 3..2..1..",
						"Gro�artig! Du hast alle Schilder gefunden :). Du wirst in 3 Sekunden teleportiert...", null, null));
				final Player pl = p;
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
					@Override public void run(){
						pl.sendMessage(prefix+Sprache.select(pl.getName(),
								"Welcome in the City :)",
								"Willkommen in der Stadt :)",
								"Bienvenue � la ville :)",
								"Bienvenida a la ciudad :)"));
						if(pl.getGameMode()==GameMode.ADVENTURE)
							pl.setGameMode(GameMode.SURVIVAL);
						onCommand(pl, null, "xtp", spawnArray);
					}
				}, 60L);
				//p.sendMessage(SpawnBuilder.prefix+"Great! You have found all signs... but there is another task for you: because of this is a RPG-Server, you should give you a new name. e.g. \"Leonardo\". But all bank transfers have to be done with the original names!");
			} else if(args.length==1){
				String s = args[0];
				boolean ok = s.length()>3;
				if(s.length()>10){
					p.sendMessage(SpawnBuilder.prefix+Sprache.select(p.getName(),
							"Your name is to long: max 10 chars!",
							"Dein Name darf maximal 10 Zeichen lang sein!", null, null));
				} else if(ok){
					name:for(char c:s.toCharArray()){
						if(!"ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".contains(c+"")){
							ok = false;
							p.sendMessage(SpawnBuilder.prefix+Sprache.select(p.getName(),
									"Your name cannot contain the symbol "+c,
									"Dein Name darf folgendes Symbol nicht enthalten: "+c, null, null));
							break name;
						}
					}
					if(ok){
						name:for(String key:Rank.getKeySet()){
							if(!key.equalsIgnoreCase(p.getName()) && Rank.getNickName(key).equalsIgnoreCase(s)){
								ok = false;
								break name;
							}
						}
						if(ok){
							Rank.putNickName(p.getName(), s);
							Rank.players.put(p.getName(), Rank.zero);
							p.sendMessage(prefix+Sprache.select(p.getName(),
									"Welcome in the City :)",
									"Willkommen in der Stadt :)", null, null));
							p.setPlayerListName(s);
							p.setDisplayName(s);
							p.setCustomName(s);
							if(p.getGameMode()==GameMode.ADVENTURE)
								p.setGameMode(GameMode.SURVIVAL);
							onCommand(p, null, "xtp", spawnArray);
						} else {
							p.sendMessage(SpawnBuilder.prefix+Sprache.select(p.getName(),
									"Your name is already in use. Pick another one.",
									"Dein Name wird schon verwendet. Suche dir einen anderen!", null, null));
						}
					}
				} else {
					p.sendMessage(SpawnBuilder.prefix+Sprache.select(p.getName(), 
							"Your name has to be longer than 3 characters.",
							"Dein Name muss l�nger als drei Zeichen sein!", null, null));
				}
			} else {
				p.sendMessage(SpawnBuilder.prefix+Sprache.select(p.getName(), 
						"Your name mustn't contain spaces!",
						"Dein Name darf keine Leerzeichen enthalten!", null, null));
			}
		} else if(label.equalsIgnoreCase("myworld") && p.isOp()){
			if(args == null || args.length>0){
				if(args[0].equals("sun")){
					p.getWorld().setThundering(false);
					p.getWorld().setStorm(false);
					p.getWorld().setWeatherDuration(100000);
				} else if(args[0].equalsIgnoreCase("rain") || args[0].equalsIgnoreCase("rainy")){
					p.getWorld().setStorm(true);
					p.getWorld().setWeatherDuration(100000);
				} else if(args[0].equals("3")){
					int x=p.getLocation().getBlockX(), y=p.getLocation().getBlockY(), z=p.getLocation().getBlockZ();
					for(int i=-1;i<2;i++){
						for(int j=-1;j<2;j++){
							Tree.generateTree(p.getWorld(), x, y, z, (x/16+i)*16, 0, (z/16+j)*16, Tree.TreeType.valueOf(args.length==2?args[2].toUpperCase():"PALME"));
						}
					}
					p.sendMessage("3 done :)");
				} else if(args[0].equalsIgnoreCase("pyr")){
					
					int x=p.getLocation().getBlockX()/16*16, y=p.getLocation().getBlockY(), z=p.getLocation().getBlockZ()/16*16;
					for(int i=-5;i<6;i++){
						for(int j=-5;j<6;j++){
							WüstenDeko.generatePyramide(p.getWorld(), x, y, z, x+i*16, 4704, z+j*16);
						}
					}
					p.sendMessage("pyr done");
				} else {
					int x=MathHelper.ori(p.getLocation().getBlockX()), z=MathHelper.ori(p.getLocation().getBlockZ());
					p.sendMessage("lgbr>"+Jena.lng(x)+" "+Jena.lat(z));
					p.sendMessage("circ>"+(131072 * (Jena.lng(x)/180 + 1))+" / "+(131072 * (1 - Math.log(Math.tan((0.25 + Jena.lat(z)/360)*Math.PI))/Math.PI)));
					p.sendMessage("cube>"+(x/2048)+"."+(z/2048)+" @"+(x%2048)+"."+(z%2048));
				}
				return true;
			} else if(p.getLocation().getWorld().equals(this.getServer().getWorlds().get(0))){
				p.teleport(new Location(world, 0, 120, 0));
			}else{
				p.teleport(new Location(this.getServer().getWorlds().get(0), 0, 120, 0));
			}
			return true;
		} else if((label.equalsIgnoreCase("w") || label.equalsIgnoreCase("tell") || label.equalsIgnoreCase("wisper")) && args.length>1){
			if(args==null || args.length==0 || args.length==1){
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"You need to give a name and a message! Then a wisper will cost 1gn/use if your distance > 250m",
						"Du musst mir einen Namen und eine Nachricht geben. Jemandem etwas zuzufl�stern kostet 1gn, wenn die Entfernung >250m ist", null, null));
			} else {
				Player p2;
				if((p2=getPlayer(args[0]))!=null){
					boolean ok=false, shortdistance = BlockListener.trueLocation(p2.getLocation()).distanceSquared(BlockListener.trueLocation(p.getLocation()))<62500;
					if(shortdistance){
						ok=true;
					} else if(Bank.getBilance(p)<1){
						Bank.sendErrMessage(p, 1);
					} else if(Bank.substract(p.getName(), 1)){
						ok=true;
					} else {
						Bank.sendErrMessage(p, 0);
					}
					if(ok){
						String s = "";
						for(int i=1;i<args.length;i++){
							s+=" "+args[i];
						}
						p2.sendMessage("(<"+p.getDisplayName()+">"+s+")");
						p.sendMessage("(w>"+p.getDisplayName()+":"+s+")");
						// das sieht fies aus ist aber der einzige Weg um auch diesen Chat irgendwie kontrolloeren zu k�nnen
						System.out.println(p.getName()+"->"+p2.getName()+": "+s);
					}
				} else p.sendMessage(prefix+Sprache.select(p.getName(),
						"Unknown username!",
						"Unbekannter Spielername!", null, null));
			}
		} else if(label.equalsIgnoreCase("p") || label.equalsIgnoreCase("pay")){
			if(args.length==2){
				if(args[0].equalsIgnoreCase("@king")){
					long l=parseLong(args[1]);
					if(l>0){
						try {
							Rank.kingGet(p, l);
						} catch (InterruptedException e) {}
					} else {
						Bank.sendErrMessage(p, 2);
					}
				} else {
					long l;String n = p.getDisplayName();
					if(Bank.pay(p, args[0], l=parseLong(args[1]))){
						p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
								"Successfully send "+l+"gn to "+args[0],
								"Erfolgreich "+args[1]+"gn an "+args[0]+" �berwiesen", null, null));
						if((p=getPlayer(args[0]))!=null){
							p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
									n+" paid you "+l+"gn",
									n+" hat "+l+"gn an dich �berwiesen. Du hast nun "+Bank.getBilance(p)+"gn",null, null));
						}
					} else {
						Bank.sendErrMessage(p, 0);
					}
				}
			} else {
				p.sendMessage(prefix+Sprache.select(p.getName(),
						"Can't understand you. Try '/p <player> <amount>'",
						"Dieser Befehl ist mir unbekannt. Probiere doch mal '/p <Spielername> <Geldmenge>' um Geld zu �berweisen.", null, null));
			}
		} else if(label.equalsIgnoreCase("crowntower")){
			if(p.getName().equalsIgnoreCase(kingsname) && world.getBlockAt(1048576,115,0).getType()!=Material.OBSIDIAN){
				double d;
				Random r = new Random(System.nanoTime());
				int k, a=1048576;
				Block b;
				for(int i=-31;i<32;i++){
					for(int j=-31;j<32;j++){
						if((d=MathHelper.sq(i+0.3)+MathHelper.sq(j+0.1))<232){
							for(k=116;k<256;k++){
								if((b=world.getBlockAt(a+i,k,j)).getType()!=Material.GOLD_BLOCK){
									b.setType(Material.AIR);
								}
							}
							for(k=0;k<32;k++){
								if((b=world.getBlockAt(a+i,k,a+j)).getType()!=Material.GOLD_BLOCK){
									b.setType(Material.AIR);
								}
							}
						} else if(d<263.46){
							for(k=116;k<230;k++){
								world.getBlockAt(a+i,k,j).setType(r.next()<0.1?Material.GLOWSTONE:Material.GLASS);
							}
							for(;k<256;k++){
								world.getBlockAt(a+i,k,j).setType(Material.AIR);
							}
							for(k=0;k<6;k++){
								world.getBlockAt(a+i,k,a+j).setType(r.next()<0.1?Material.GLOWSTONE:Material.GLASS);
							}
							for(;k<32;k++){
								world.getBlockAt(a+i,k,a+j).setType(Material.AIR);
							}
						} else if(d<900){
							for(k=116;k<256;k++){
								world.getBlockAt(a+i,k,j).setType(Material.AIR);
							}
							for(k=0;k<32;k++){
								world.getBlockAt(a+i,k,a+j).setType(Material.AIR);
							}
						}
						if(d<900){
							for(k=101;k<116;k++){
								world.getBlockAt(a+i,k,j).setType(Material.OBSIDIAN);
							}
						}
					}
				}
				p.sendMessage(prefix+"Rebuilt your tower :)");
			} else {
				p.sendMessage(prefix+"Only the "+(Plugin.isQueen?"Queen":"King")+" can use this command, and only if the tower isn't already build. If it isn't, ask (Youtube) Antonio Noack for help.");
			}
		} else {
			System.out.println(prefix+"Do not know the command <"+label+">\nThis is a bug, so please report it :)");
			return false;
		}
		return true;
	}
	
	public static Player getPlayer(String name) {
		Player p = Bukkit.getPlayer(name);
		if(p==null){
			for(Player p1:Bukkit.getOnlinePlayers()){
				if(p1.getDisplayName().equalsIgnoreCase(name)){
					return p1;
				}
			}
			return null;
		} else {
			return p;
		}
	}

	private int intByLong(long bilance) {
		if(bilance>2147483647L) return Integer.MAX_VALUE;
		return (int) bilance;
	}

	static long parseLong(String s){
		long ret=0;
		for(int i=0;i<s.length();i++){
			if(s.charAt(i)>='0' && s.charAt(i)<='9'){
				ret = ret*10+s.charAt(i)-'0';
			}
		}
		return ret;
	}
	
	String rgb(int c) {
		return ((c>>16)&0xff)+":"+((c>>8)&0xff)+":"+(c&0xff);
	}
	
	public void createMyWorld() throws IOException{
		WorldCreator nw = new WorldCreator(myWorldName);
		long seed;
		nw.seed(seed=getServer().getWorlds().get(0).getSeed());
		Random.ini(seed);
		nw.generator(new me.corperateraider.generator.MapsGenerator(this));
		getServer().createMap(world = nw.createWorld());
		world.setSpawnLocation(1048576, 126, -24);
		// wird gebraucht!
		//world.setKeepSpawnInMemory(false);
		
		// das hingegen nicht
		for(int i=0;i<2;i++){
			Bukkit.getWorlds().get(i).setKeepSpawnInMemory(false);
		}
	}

	public static String[] argsBefore(String before, String[] args){
		String[] ret = new String[args.length+1];
		ret[0]=before;
		for(int i=0;i<args.length;i++){
			ret[i+1]=args[i];
		}
		return ret;
	}
	
	public static void sendKingsmessages(String name) {
		Player p = Bukkit.getPlayer(name);
		if(p!=null){
			p.sendMessage(prefix
					+
					Sprache.select(p.getName(), (name.equalsIgnoreCase(kingsname)?
					 "Congratulations! You are the new �cKING�f.\n"
					+ "'/? king' to see the rules again\n":"")
					+ "The following points describe your rights and duties.\n"
					+ " - remember: You are the �cmost important person�f in this game!\n"
					+ "�a[�2RANKS�a]�f\n"
					+ " - you can set ranks and their owners at the rank board.\n"
					+ " - you own all of them\n"
					+ " - more information at /rank\n"
					+ "�a[�2MONEY�a]�f\n"
					+ " - stored in the main tower\n"
					+ " - all transactions are done by you as the king!\n"
					+ "�a[�2$ VIA TAXES�a]�f\n"
					+ " - be fair!\n"
					+ " - write them down anywhere to show fairness\n"
					+ "�a[�2POLICE�a]�f\n"
					+ " - swords with the name 'Justice' carried by police men ban the hit player into the prison dimension\n"
					+ " - to make them simply put them in an anvil and rename them\n"
					+ " - type of the sword is like the duration\n"
					+ "�a[�2AREA OF INFLUENCE�a]�f\n"
					+ " - only until 20km away from city centre, until 1km depth\n"
					+ " - everyone can hear you!\n"
					+ "�a[�2ENEMIES�a]�f\n"
					+ " - outside this 20km, there is no law\n"
					+ " - anyone with more money than you can become the new king (/list richest) - don't forget that they can give money each other!\n"
					+ "�a[�2TIPPS�a]�f\n"
					+ " - be �cnice�f and �cfair�f\n"
					+ " - prefer nobody\n"
					+ " - try to keep traditions and events\n"
					+ " - try to make no enemies or persons, who would like to be the new king\n"
					+ " - nobody likes taxes: keep them as low as possible\n"
					+ " - maybe try to build a momument for your area to keep you in mind\n"
					+ " - try to keep your amount of money to symbolize �cstability�f and a �csuperior acting king�f\n"
					+ " - present you to every new player in the best way you can to win influence\n"
					+ "�aGood luck, beeing the most �2epic�a king ever :D",
					
					(name.equalsIgnoreCase(kingsname)?
					 "Herzlichen Gl�ckwunsch! Du bist der neue �cK�NIG�f.\n"
					+ "'/? king' um die Regeln erneut zu sehen\n":"")
					+ "Die folgenden Punkte beschreiben deine Rechte und Pflichten:\n"
					+ " - denk dran: Du bist die �cWichtigste Person�f in diesem Spiel!\n"
					+ "�a[�2R�NGE�a]�f\n"
					+ " - du kannst R�nge und Rechte am Goldenen Turm festlegen.\n"
					+ " - du selbst besitzt alle Rechte\n"
					+ " - mehr Infos: /rank\n"
					+ "�a[�2DEIN GOLD�a]�f\n"
					+ " - im Goldenem Turm gelagert\n"
					+ " - alle Gesch�fte werden mit dem K�nigskonto erledigt!\n"
					+ "�a[�2STEUEREINNAHMEN�a]�f\n"
					+ " - sei gerecht!\n"
					+ " - schreib sie irgendwo nieder um Gerechtigkeit zu zeigen\n"
					+ "�a[�2POLIZEI�a]�f\n"
					+ " - Schwerster namens 'Justice' verbannen den geschlagenen Spieler in die Gef�ngniswelt\n"
					+ " - um so ein Schwert zu bekommen, musst du es nur im Amboss umnennen\n"
					+ " - je st�rker der Schwerttyp(z.B. Eisen) umso l�nger die Haft\n"
					+ "�a[�2EINFLUSS�a]�f\n"
					+ " - nur bis 20km vom Spawn und 1km in die Tiefe\n"
					+ " - jeder kann dich h�ren!\n"
					+ "�a[�2FEINDE�a]�f\n"
					+ " - au�erhalb des K�nigreiches gelten deine Gesetze nicht\n"
					+ " - jeder mit mehr Geld als du kann der neue K�nig werden (/list richest) - Vergiss nicht, dass gegenseitiges Geldgeben auch m�glich ist!\n"
					+ "�a[�2TIPPS�a]�f\n"
					+ " - sei �cnett�f und �cgerecht�f\n"
					+ " - bevorzuge niemanden\n"
					+ " - versuche Traditionen und Feste zu erhalten\n"
					+ " - versuche dir keine Feinde bzw. throngierige Spieler zu schaffen\n"
					+ " - niemand mag Steuern: halte sie so niedirg wie nur m�glich\n"
					+ " - baue eventuell ein Monument um an deine Herrschaftszeit zu erinnern\n"
					+ " - versuche deinen Geldpegel konstant zu halten um �cStabilit�t�f und einen �c�berlegt handelnden K�nig�f zu symbolisieren\n"
					+ " - pr�sentiere dich jedem Spieler so gut wie nur m�glich um an Einfluss zu gewinnen\n"
					+ "�aViel Gl�ck, der �2epischste�a K�nig aller Zeiten zu werden :D", null, null));
		} else {
			System.out.println("Couldn't send kings message!");
		}
	}
	
	public static int remove(Inventory inventory, Material mat, int amount, short damage, ItemStack inHand){
		ItemStack[] contents = inventory.getContents();
		ItemStack item;
		
		for (int i = 0; i < contents.length; i++) {
			item = contents[i];
			if(item!=null && item!=inHand && item.getType().equals(mat) && (damage == -1 || item.getDurability()==damage)){
				
				amount-=item.getAmount();
				
				if(amount<0){
					item.setAmount(-amount);
					break;
				} else {
					inventory.setItem(i, null);
				}
			}
		}
		return amount;
	}
	
	public static int fromTo(Inventory inventory, Inventory add, Material mat, int amount, short damage, ItemStack inHand){
		ItemStack[] contents = inventory.getContents();
		ItemStack item;
		
		for (int i = 0; i < contents.length; i++) {
			item = contents[i];
			if(item!=null && item!=inHand && item.getType().equals(mat) && (damage == -1 || item.getDurability()==damage)){
				
				amount-=item.getAmount();
				
				if(amount<0){
					ItemStack add2=item.clone();
					add2.setAmount(item.getAmount()+amount);
					add.addItem(add2);
					item.setAmount(-amount);
					break;
				} else {
					add.addItem(item);
					inventory.setItem(i, null);
				}
			}
		}
		return amount;
	}
	
	public static int count(Inventory inventory, Material mat, short damage){
		ItemStack[] contents = inventory.getContents();
		int searchAmount = 0;
		for (ItemStack item : contents) {
			if(item!=null && item.getType().equals(mat) && (damage == -1 || item.getDurability()==damage)){
				searchAmount += item.getAmount();
			}
		}
		return searchAmount;
	}
}
