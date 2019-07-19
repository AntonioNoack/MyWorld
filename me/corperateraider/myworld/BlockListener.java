package me.corperateraider.myworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.corperateraider.dynmap.DynMap;
import me.corperateraider.generator.Generator;
import me.corperateraider.generator.MapsGenerator;
import me.corperateraider.generator.MathHelper;
import me.corperateraider.myworld.Prison.Prisoner;
import me.corperateraider.recipes.RecipeManager;
import me.corperateraider.recipes.XBlock;
import me.corperateraider.reload.Jena;
import me.corperateraider.reload.SpawnBuilder;
import me.corperateraider.reload.SpawnManager;
import me.corperateraider.weather.Weather;
import net.minecraft.server.v1_7_R1.ChunkProviderServer;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.util.LongHash;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.CachedServerIcon;
import org.bukkit.util.Vector;

import converter.Converter;
import converter.MetaString;
import converter.StringCompare;

public class BlockListener extends MathHelper implements Listener {
	
	public static String eventByKing="";
	Location zero;
	World myworld;
	CachedServerIcon[] icon4season;
	CachedServerIcon icon(){
		return icon4season[((Weather.jetztTime()+32)/64)%4];
	}
	
	public BlockListener(Plugin plugin) throws Exception {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		zero = new Location(Bukkit.getWorld(Plugin.myWorldName), 0, 4800, 0);
		icon4season = new CachedServerIcon[]{
		Bukkit.loadServerIcon(new File(plugin.getDataFolder(), "icon.png")),
		Bukkit.loadServerIcon(new File(plugin.getDataFolder(), "icon herbst.png")),
		Bukkit.loadServerIcon(new File(plugin.getDataFolder(), "icon winter.png")),
		Bukkit.loadServerIcon(new File(plugin.getDataFolder(), "icon spring.png"))
		};
	}
	
	long time;
	public void onTimeDoWitherkill(){
		if(time<System.currentTimeMillis()){	
			time = System.currentTimeMillis()+1000;
			Location z = new Location(myworld=Bukkit.getWorld(Plugin.myWorldName), 0, 4800, 0);
			for(Entity e:z.getWorld().getEntities()){
				if((e.getType()==EntityType.WITHER || e.getType()==EntityType.ENDER_DRAGON) && disSquaredBorderY(z, trueLocation(e.getLocation()), 1250)){
					e.remove();
					Bukkit.broadcastMessage(Plugin.prefix+"The magical shield killed a "+(e.getType()==EntityType.ENDER_DRAGON?"dragon":"wither")+"...");
				}
			}
		}
	}
	
	public static boolean disSquaredBorderY(Location l1, Location important, int border){
		return important.getY()>border && sq(l1.getX()-important.getX())+sq(l1.getZ()-important.getZ())<25E6;
	}
	
	/*@EventHandler(priority = EventPriority.HIGH)
	public void onWebsiteRequest(HTTPRequestEvent event) throws InterruptedException{
		
		String req = event.getRequest().split(" ")[1];
		
		if(req.equals("/")){
			req="/index.html";
		} else if(req.startsWith("/_")){
			DynMap.answer(event);
			return;
		}
		
		File f = new File(Plugin.instance.getDataFolder()+"/WWW"+req);
		if(f.isDirectory()){
			f = new File(f, "index.html");
		}
		if(f.exists() && !f.isDirectory()){
			try {
				event.setImage(IOUtils.toByteArray(new FileInputStream(f)));
				if(f.getName().endsWith(".html")){
					event.setContentType("text/html");
				} else event.setContentType("* / *");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("File not found: \""+req+"\" '"+f.getAbsolutePath()+"'");
		}
	}*/
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChunkload(org.bukkit.event.world.ChunkLoadEvent event){
		controlAnimals(event.getChunk());
		if(!event.isNewChunk()){
			Weather.load(event.getChunk(), true);
		} else {
			Weather.todo.add(new Weather.Remember(event.getChunk(), System.currentTimeMillis()+10000));
		}
	}
	
	public static void controlAnimals(Chunk ch){
		/*int a=c.getEntities().length;
		if(a>4){
			for(Entity e:c.getEntities()){
				e.remove();
				if(a--<4)break;
			}
		}*/
		
		for(Chunk c:Plugin.world.getLoadedChunks()){
			if(c.getEntities().length>5){
				for(Entity e:c.getEntities()){
					e.remove();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChunkdeload(org.bukkit.event.world.ChunkUnloadEvent event){
		
		if(new File("C:/Users/Antonio").exists()) return;
		
		World world;
		Chunk c = event.getChunk();
		if((world=event.getWorld())!=Plugin.world){
			if(!c.getBlock(0, 0, 0).hasMetadata(MetaString.save)){// der Chunk wurde durch einen Spieler ver�ndert
				event.setCancelled(true);
				try {
					ChunkProviderServer cps = ((CraftWorld)world).getHandle().chunkProviderServer;
					cps.unloadQueue.remove(c.getX(), c.getZ());
					cps.chunks.remove(LongHash.toLong(c.getX(), c.getZ()));
				} catch(Exception e){
					Bukkit.broadcastMessage("�4Unload error! See log! @"+world.getName());
					e.printStackTrace();
				}
			}
		} else {
			Position pc = new Position(BlockListener.trueLocation(new Location(world, c.getX()*16+8, 127, c.getZ()*16+8)));
			boolean ok=false;
			s:for(Player p:world.getPlayers()){			
				if(new Position(trueLocation(p.getLocation())).distSQy(pc, 4)<62500){
					ok=true;
					break s;
				}
			}
			if(!ok){// der Chunk darf ungeloaded werden ^^
				Weather.unload(event.getChunk());
				if(!c.getBlock(0, 0, 0).hasMetadata(MetaString.save)){// der Chunk wurde durch einen Spieler ver�ndert
					event.setCancelled(true);
					try {
						ChunkProviderServer cps = ((CraftWorld)world).getHandle().chunkProviderServer;
						cps.unloadQueue.remove(c.getX(), c.getZ());
						cps.chunks.remove(LongHash.toLong(c.getX(), c.getZ()));
					} catch(Exception e){
						Bukkit.broadcastMessage("�4Unload error! See log! @"+world.getName());
						e.printStackTrace();
					}
				}
				// sonst mach einfach weiter :)
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLeaveServer(org.bukkit.event.player.PlayerQuitEvent event){// logout
		Player p = event.getPlayer();
		event.setQuitMessage(Rank.getQuitMessage(p.getName()));
		if(Plugin.isFlying(p.getName())){
			p.teleport(Plugin.isFlying.get(p.getName()));
			Plugin.isFlying.remove(p.getName());
		}
		Plugin.removeAfk(event.getPlayer().getName());
		logIn.put(p.getName(), 100);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEating(org.bukkit.event.player.PlayerItemConsumeEvent event){
		RecipeManager.event(event);
	}
	
	/*@EventHandler(priority = EventPriority.HIGH)
	public void onHoldingItem(org.bukkit.event.player.PlayerItemHeldEvent event){
		RecipeManager.event(event);
	}*/
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onSneaking(org.bukkit.event.player.PlayerToggleSneakEvent event){
		if(Plugin.beta && event.getPlayer().getItemInHand()!=null && event.getPlayer().getItemInHand().getType()!=Material.AIR && event.getPlayer().getName().equalsIgnoreCase("Miner952x")){
			ItemStack s = event.getPlayer().getItemInHand();
			event.getPlayer().sendMessage(s.getTypeId()+":"+s.getData().getData()+", "+s.getDurability());
		}
		RecipeManager.event(event);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPrepareItemCraft(PrepareItemCraftEvent event){
		RecipeManager.event(event);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onItemMove(org.bukkit.event.inventory.InventoryClickEvent event){
		RecipeManager.event(event);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onCraftItem(CraftItemEvent event){
		RecipeManager.event(event);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e){
		// �berpr�fe die eigenen Bl�cke...
		if(XBlock.cancelInventoryClick(e.getWhoClicked(), e.getInventory(), e)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent e){
		// �berpr�fe die eigenen Bl�cke... -> abspeichern :)
		XBlock.closeInventory(e.getPlayer(), e.getInventory(), e);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryInteract(org.bukkit.event.inventory.InventoryInteractEvent e){
		// �berpr�fe die eigenen Bl�cke... -> abspeichern :)
		Bukkit.broadcastMessage(e.getEventName()+" "+e.getResult().name());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onReleaseUnknownCommand(org.bukkit.event.player.PlayerCommandPreprocessEvent event){
		if(!event.isCancelled() && event.getPlayer().getWorld()==Plugin.world){
			String command;
			if(Bukkit.getHelpMap().getHelpTopic(command=event.getMessage().split(" ")[0])==null){
				
				int min = 10000, res;
				boolean multi=false;
				HelpTopic topic=null;
				String result="", name;
				for(HelpTopic top:Bukkit.getHelpMap().getHelpTopics()){
					if((res=StringCompare.computeLevenshteinDistance(command, name=top.getName()))<min){
						min=res;
						result=name;
						multi=false;
						topic=top;
					} else if(min==res){
						result+=" "+name;
						multi=true;
					}
				}
				if(multi){
					event.getPlayer().sendMessage("�cNo command called �f"+command+"�c aviable. What about these?\n   �f"+result);
				} else {
					event.getPlayer().sendMessage("�cNo command called �f"+command+"�c aviable. What about '�f"+result+"�c'?\n   Info: �f"+topic.getShortText());
				}
				event.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onClick(PlayerInteractEvent event){
		
		onTimeDoWitherkill();
		
		if(event.getPlayer().getWorld()!=myworld && event.getClickedBlock().getTypeId()!=5) return;
		
		Player p = event.getPlayer();
		
		Block b = event.getClickedBlock();
		switch(event.getAction()){
		case LEFT_CLICK_AIR:
			break;
		case LEFT_CLICK_BLOCK:
			
			if(XBlock.cancelLeftClick(b, p, event)){
				event.setCancelled(true);
				return;
			}
			
			int k, bid;
			if((bid=b.getTypeId())==41){// auf Goldblock -> entweder Gold aus Hand in Bank einlagern oder von Bank abheben
				if(p.getItemInHand()==null || p.getItemInHand().getTypeId()==0){// nichts, hebe also Geld ab
					if(p.isSneaking()){// einzellnes Nugget
						if((k = (int) Bank.getBilance(p))>0){
							Bank.substract(p.getName().toLowerCase(), 1);
							p.getInventory().addItem(new ItemStack(371, 1));
						} else Bank.sendStatus(p);
					} else {// alles oder 64
						if((k = (int) Bank.getBilance(p))>63){
							Bank.substract(p.getName().toLowerCase(), 64);
							p.getInventory().addItem(new ItemStack(371, 64));
						} else {
							Bank.substract(p.getName().toLowerCase(), k);
							p.getInventory().addItem(new ItemStack(371, k));
						}
					}
				}
			} else if(bid==121){
				// Drachenspawning :D
				SpawnManager.checkDragon(myworld, p, b.getX(), b.getY(), b.getZ());
			}
			break;
		case PHYSICAL:
			break;
		case RIGHT_CLICK_AIR:
			
			if(p.getItemInHand().getType()==Material.MONSTER_EGG && p.getGameMode()==GameMode.CREATIVE){
				Location loc = p.getLocation();
				p.getWorld().regenerateChunk(loc.getBlockX()/16, loc.getBlockZ()/16);
			}
			break;
		case RIGHT_CLICK_BLOCK:
			
			if(XBlock.cancelRightClick(b, p, event)){
				event.setCancelled(true);
				return;
			}
			
			//if(p.getItemInHand().getType()==Material.WATER_BUCKET){ sinnvoll? vllt xD ehr wohl nicht, oder? naja ^^
			//	b.getWorld().getBlockAt(event.getBlockFace().getModX(),event.getBlockFace().getModY(),event.getBlockFace().getModZ()).setType(Material.WATER);
			//	return;
			//}
			
			bid = b.getTypeId();
			if(bid==41){// auf Goldblock -> Gold in Bank einlagern...
				if(p.getItemInHand()==null || p.getItemInHand().getTypeId()==0){
					
					// lagere alles! ein
					
					Bank.add(p.getName().toLowerCase(), (10000-Plugin.remove(p.getInventory(), Material.GOLD_BLOCK, 10000, (short) 0, null))*81);
					Bank.add(p.getName().toLowerCase(), (10000-Plugin.remove(p.getInventory(), Material.GOLD_INGOT, 10000, (short) 0, null))* 9);
					Bank.add(p.getName().toLowerCase(), (10000-Plugin.remove(p.getInventory(), Material.GOLD_NUGGET,10000, (short) 0, null))* 1);
					
					Bank.sendStatus(p);
					
					p.updateInventory();
					
				} else if((k=p.getItemInHand().getTypeId())==41){
					
					if(p.isSneaking()){// nur die Hand
						int amount = p.getItemInHand().getAmount();
						Bank.add(p.getName().toLowerCase(), (amount-Plugin.remove(p.getInventory(), Material.GOLD_BLOCK, amount, (short) 0, p.getItemInHand()))* 81);
					} else {// alles :)
						Bank.add(p.getName().toLowerCase(), (10000-Plugin.remove(p.getInventory(), Material.GOLD_BLOCK, 10000, (short) 0, p.getItemInHand()))* 81);
					}
					
					Bank.sendStatus(p);

					p.updateInventory();
					
				} else if((k=p.getItemInHand().getTypeId())==266 && p.getItemInHand().getDurability()==0){// Barren in der Hand...
					
					if(p.isSneaking()){// nur die Hand
						int amount = p.getItemInHand().getAmount();
						Bank.add(p.getName().toLowerCase(), (amount-Plugin.remove(p.getInventory(), Material.GOLD_INGOT, amount, (short) 0, null))* 9);
					} else {// alles :)
						Bank.add(p.getName().toLowerCase(), (10000-Plugin.remove(p.getInventory(), Material.GOLD_INGOT, 10000, (short) 0, null))* 9);
					}
					
					Bank.sendStatus(p);

					p.updateInventory();
					
				} else if(k==371){// nugget in der Hand...
					
					if(p.isSneaking()){// nur die Hand
						int amount = p.getItemInHand().getAmount();
						Bank.add(p.getName().toLowerCase(), amount-Plugin.remove(p.getInventory(), Material.GOLD_NUGGET, amount, (short) 0, null));
					} else {// alles :)
						Bank.add(p.getName().toLowerCase(), 10000-Plugin.remove(p.getInventory(), Material.GOLD_NUGGET, 10000, (short) 0, null));
					}
					
					Bank.sendStatus(p);

					p.updateInventory();
					
				}
			} else if(bid==68){// Schild an der Wand -> braucht Kiste im Umkreis von 1Block (drunter, links, rechts, oder dr�ber)
				
				Sign s = (Sign) b.getState();
				int x=b.getLocation().getBlockX(), y=b.getLocation().getBlockY(), z=b.getLocation().getBlockZ();
				World w = b.getWorld();
				
				String l0=s.getLine(0), l1=s.getLine(1), l2=s.getLine(2), l3=s.getLine(3);
				
				if(l0.equalsIgnoreCase("Sell plot!")){
					// Sell plot!
					// <username>
					//  <price>
					// <plotname>
					
					int price;
					long bank;
					if(isName(l1) && (price=isNumber(l2))>-1){
						if(Grundstück.isPlotnameByOwnerAndRealplot(l3, l1, Generator.ori(s.getX()), Generator.ori(s.getZ()))){
							if(p.getName().equalsIgnoreCase(l1)){
								p.sendMessage(Plugin.prefix+"This sign works now: you can sell your '"+l3+"' for "+price+"g.");
							} else if((bank=Bank.getBilance(p))>=price){
								p.sendMessage(Plugin.prefix+"If you want to buy this plot, you should break this sign!\n"+Grundstück.getInfoAbout(l3, price));
							} else {
								p.sendMessage("You cannot pay this price. You only have "+bank+"g.");
							}
						} else {
							p.sendMessage(Plugin.prefix+"This sign is damaged(any value is wrong). You cannot use it.");
						}
					} else p.sendMessage("241");
					return;
				}
				
				Chest chest = getChest(w, x, y-1, z);
				if(chest == null){chest = getChest(w, x, y+1, z);}
				
				if(l0==null || l1==null || l2==null || l3==null) return;
				if(chest==null){
					if(l0.equalsIgnoreCase(p.getName())){
						p.sendMessage(Plugin.serfix+"No chest found!");
					}
					return;
				}
				
				int price, count;
				if(isName(l0) && l1.length()>0 && (price=isNumber(l2))>0 && (count=isNumber(l3))>-1){
					
					int[] is = getItemStackByName(l1, p);
					int id=is[0], meta=is[1];
					boolean exact = l1.split(":").length==2 || meta!=-1;
					
					if(is[0]==-1){
						p.sendMessage(Plugin.serfix+"[ChestShop] Unknown material!");
						return;
					}
					
					if(Plugin.count(chest.getBlockInventory(), Material.getMaterial(id), (short) (exact?meta:-1))>=count){
						if(l0.equalsIgnoreCase(event.getPlayer().getName()) || (p.getName().equalsIgnoreCase(Plugin.kingsname) && l0.equalsIgnoreCase("@king"))){
							event.getPlayer().sendMessage(Plugin.serfix+"Everything is great :)");
						} else if(Bank.getBilance(event.getPlayer())>=price){
							
							Plugin.fromTo(chest.getBlockInventory(), p.getInventory(), Material.getMaterial(id), count, (short) (exact?meta:-1), null);
							
							Bank.substract(p.getName().toLowerCase(), price);
							Bank.add(l0, price);
							Bank.sendStatus(p);
							p.updateInventory();
						} else {
							Bank.sendErrMessage(p, 1);
						}
					} else {
						if(l0.equalsIgnoreCase(p.getName()) || (p.getName().equals(Plugin.kingsname) && l0.equalsIgnoreCase("@king"))){
							p.sendMessage(Plugin.serfix+" Your shop needs something to sell.");
						} else {
							p.sendMessage("["+l0+"] Sorry. This shop is sold out!");
						}
					}
				}
			} else if(bid == 54 || bid == 61 || bid == 62 || bid == 23 || bid == 158 || bid == 154 || bid == 146){
				if(!blockBreakIsOK(p, b)){
					event.getPlayer().closeInventory();
					event.setCancelled(true);
				} else if(b.getY()<16){// nimm die Kiste aus der anderen Welt oben -> Kisten unten sind Geheimverstecke :)
					event.setCancelled(true);

					double[] c = cooOfNotherWorld(b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ());
					p.sendMessage(c[0]+":"+c[1]+":"+c[2]+":"+b.getLocation().getX()+":"+b.getLocation().getY()+":"+b.getLocation().getZ());
					BlockState state = myworld.getBlockAt((int)c[0], (int)c[1], (int)c[2]).getState();
					if(state instanceof Chest){
						p.openInventory(((Chest)state).getBlockInventory());
					} else if(state instanceof Dropper){
						p.openInventory(((Dropper)state).getInventory());
					} else if(state instanceof Hopper){
						p.openInventory(((Hopper)state).getInventory());
					} else if(state instanceof Dispenser){
						p.openInventory(((Dispenser)state).getInventory());
					} else if(state instanceof BrewingStand){
						p.openInventory(((BrewingStand)state).getInventory());
					} else if(state instanceof Furnace){
						p.openInventory(((Furnace)state).getInventory());
					} else if(state instanceof Beacon){
						p.openInventory(((Beacon)state).getInventory());
					} else {
						p.sendMessage("�4This is an ERROR, caused at line 508 in BlockListener(onClick.Rightclickblock). Please inform your favorite admin! or create a report.");
					}
				}
			} else if(bid==121){
				// Drachenspawning :D
				SpawnManager.checkDragon(myworld, p, b.getX(), b.getY(), b.getZ());
			}
			break;
		default:
			break;
		
		}
	}

	@SuppressWarnings("deprecation")
	private int[] getItemStackByName(String s, Player p) {
		if(s.split(":").length==2){
			if(isNumber(s.split(":")[0])>-1){
				return new int[]{isNumber(s.split(":")[0]), isNumber(s.split(":")[1])};
			} else {
				Material mat = Material.getMaterial(s.split(":")[0]);
				if(mat!=null){
					return new int[]{new ItemStack(mat).getTypeId(), isNumber(s.split(":")[1])>-1?isNumber(s.split(":")[1]):0};
				} else {
					p.sendMessage("I am very sorry but this material is unknown.\n"
							+ "If you think the server should add it, simply send a report with '/rep your message...'"
							+ "If you want to use sell your things instand try to find out the id and meta of it and use them as your name. Example: '1:1' for granite");
					return new int[]{-1, -1};
				}
			}
		} else {
			return new int[]{isNumber(s), -1};
		}
	}

	public static void checkPos(Event ev, final Entity e){
		final Location l = e.getLocation();
		
		if(l.getBlockY()>256-16 && abs(l.getBlockX())<Plugin.width && abs(l.getBlockZ())<Plugin.width || (l.getBlockY()<16 && Generator.basey(l.getBlockX(), l.getBlockZ())==0)){
			return;
		}
		
		if(l.getBlockY()<16 || l.getBlockY()>256-16){

			float pitch = l.getPitch();
			Vector vec = l.getDirection(), vel = e.getVelocity();
			double[] ds=cooOfNotherWorld(l.getX(),l.getY(),l.getZ());
			
			final Location togo = new Location(l.getWorld(),ds[0],ds[1]+0.1,ds[2]).setDirection(vec);
			if(e.getPassenger()!=null){
				if(ev.getEventName().equalsIgnoreCase("VehicleUpdateEvent") || ev.getEventName().equalsIgnoreCase("PlayerMoveEvent")){
					final Entity pass = e.getPassenger();
					// auswerfen
					e.eject();
					// tp
					e.teleport(togo);
					pass.teleport(togo);
					// weltchange
					if(pass instanceof Player){
						int y;
						if(l.getBlockY()<16){
							SpawnBuilder.change((Player)pass, y=Generator.basey(l.getBlockX(), l.getBlockZ())/224, y-1);
						} else {
							SpawnBuilder.change((Player)pass, y=Generator.basey(l.getBlockX(), l.getBlockZ())/224-1, y+1);
						}
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable(){
							@Override public void run() {pass.teleport(togo);togo.add(0, 1, 0).getBlock().setType(Material.AIR);}
						}, 10L);
					}
					// aufsetzen
					e.setPassenger(pass);
					// geschwindigkeit zur�ckgeben
					e.setVelocity(vel);
				}
			} else if(e.getVehicle()!=null){
				checkPos(ev, e.getVehicle());
			} else {
				e.teleport(new Location(l.getWorld(),ds[0],ds[1],ds[2]).setDirection(vec));
				e.setVelocity(vel);
				
				if(e instanceof Player){
					int y;
					if(l.getBlockY()<16){
						SpawnBuilder.change((Player)e, y=Generator.basey(l.getBlockX(), l.getBlockZ())/224, y-1);
					} else {
						SpawnBuilder.change((Player)e, y=Generator.basey(l.getBlockX(), l.getBlockZ())/224-1, y+1);
					}
					
					// hoffentlich sicherer...
					Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable(){
						@Override public void run() {e.teleport(togo);}
					}, 10L);
				}
				
				e.getLocation().setPitch(pitch);
			}
		}
	}
	
	private static int abs(int i) {
		return i<0?-i:i;
	}
	
	@EventHandler
	@SuppressWarnings("deprecation")
	public void onPlayerPrelogin(org.bukkit.event.player.PlayerPreLoginEvent event){
		System.out.println("Prelogin:"+event.getName()+" from "+event.getAddress().getHostAddress());
	}
	
	@EventHandler
	public void onPlayerHurt(org.bukkit.event.entity.EntityDamageByEntityEvent event){
		
		onTimeDoWitherkill();
		
		if(event.getEntity().getWorld()!=myworld){
			return;
		}
		
		if(event.getEntity() instanceof Player && Plugin.isAfk(((Player)event.getEntity()).getName())){
			event.setCancelled(true);
			return;
		}
		
		if(event.getDamager() instanceof Player && Plugin.isFlying(((Player)event.getDamager()).getName())){
			Player p = ((Player)event.getDamager());
			p.sendMessage(Plugin.serfix+Sprache.select(p.getName(),
					"You can't make damage in spectator mode.",
					"Du darfst im Beobachtermodus niemandem Schaden zuf�gen, bist aber angreifbar!", null, null));
			event.setCancelled(true);
			return;
		}
		
		Player aktiv, passiv;
		if(trueLocation(event.getEntity().getLocation()).distanceSquared(zero) < 1E6 && Generator.basey(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())>4600){
			if(event.getDamager() instanceof Player){
				aktiv = (Player) event.getDamager();
				if(event.getEntity() instanceof Player){
					passiv = (Player) event.getEntity();
					int dur;
					if(Rank.hasPermisson(aktiv.getName(), "pol") && (dur=justice(aktiv.getItemInHand()))>0){
						if(passiv.getName().equalsIgnoreCase(Plugin.kingsname)){
							aktiv.sendMessage(Plugin.prefix+"You cannot put your "+(Plugin.isQueen?"Queen":"King")+" into prison!");
						} else if(Rank.hasPermisson(passiv.getName(), "pol") && !Rank.hasPermisson(aktiv.getName(), "spol")){
							aktiv.sendMessage(Plugin.prefix+"All time, "+passiv.getDisplayName()+" is also part of the police, you cannnot put him into prison!(Super-police can)");
						} else {
							Prison.inside.add(new Prisoner(passiv.getName(), dur*1000+System.currentTimeMillis()));
						}
					}
					((Player)event.getDamager()).sendMessage(Plugin.prefix+"PvP is disabled until 1000 blocks from the city centre!");
					event.setCancelled(true);
				} else switch(event.getEntityType()){// wenn man sich auf einem Grundstück befindet, kann man nur Monster t�ten, keine Tiere
				case BAT:
				case BOAT:
				case CHICKEN:
				case COW:
				case EGG:
				case HORSE:
				case IRON_GOLEM:
				case MINECART:
				case MINECART_CHEST:
				case MINECART_FURNACE:
				case MINECART_HOPPER:
				case MINECART_TNT:
				case MUSHROOM_COW:
				case OCELOT:
				case PAINTING:
				case PIG:
				case SHEEP:
				case SNOWMAN:
				case SQUID:
				case VILLAGER:
				case WITHER_SKULL:
				case WOLF:
					if(!blockBreakIsOK((Player) event.getDamager(), event.getEntity().getWorld().getBlockAt(event.getEntity().getLocation()))){
						event.setCancelled(true);
					}
					break;
				default:
				}
			}
		}
	}
	
	private int justice(ItemStack i) {
		if(i==null || !i.hasItemMeta()){
			return 0;
		} else if("Justice".equals(i.getItemMeta().getDisplayName())){
			switch(i.getType()){
			case DIAMOND_SWORD:
				return 3600*3;
			case GOLD_SWORD:
				return 3600;
			case IRON_SWORD:
				return 1200;
			case WOOD_SWORD:
				return  420;
			case STICK:
				return  120;
			default:return 0;
			}
		} else return 0;
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event){
		
		onTimeDoWitherkill();
		
		Location l = event.getLocation();
		
		if(l.getWorld().getChunkAt(l).getEntities().length>30 || l.getWorld().getName().endsWith("_the_end")){
			event.setCancelled(true);
			return;
		}
		
	
		if((event.getSpawnReason()==SpawnReason.DEFAULT || event.getSpawnReason()==SpawnReason.NATURAL || event.getSpawnReason()==SpawnReason.CHUNK_GEN) && event.getEntityType()!=EntityType.SILVERFISH){
			Location trueloc = trueLocation(event.getLocation());
			if(SpawnManager.spawnEntityRemoveOld(event.getEntity(), event.getLocation(), trueloc.getBlockX(), event.getLocation().getBlockY(), trueloc.getBlockY(), trueloc.getBlockZ())){
				event.setCancelled(true);
			}
		}
		
		/*Location trueloc = trueLocation(event.getLocation());
		if(trueloc.getBlockY()<224 && event.getEntityType()!=EntityType.ENDERMAN){
			World w=trueloc.getWorld();
			if(w.getBlockAt(event.getLocation().add(new Vector(0,1,0))).getType()==Material.AIR){
				trueloc.getWorld().spawnEntity(event.getLocation(), EntityType.ENDERMAN);
			}
			
			event.setCancelled(true);
		}*/
		/*if(event.getSpawnReason()!=SpawnReason.NATURAL && event.getSpawnReason()!=SpawnReason.CUSTOM){
			Bukkit.broadcastMessage(event.getSpawnReason().name());
			event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.SHEEP);
			event.setCancelled(true);
		}
		
		
		if(event.getEntity().getWorld()!=myworld){
			if(event.getEntity()!=Bukkit.getWorlds().get(0)){
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
			Location trueLoc = trueLocation(event.getEntity().getLocation());
			spawnmanager.get(event.getEntity(), event.getEntity().getLocation(), trueLoc.getBlockX(), event.getEntity().getLocation().getBlockY(), trueLoc.getBlockY(), trueLoc.getBlockZ());
		}*/
		
		
		
		
		/*Location trueloc;
		double d = (trueloc=trueLocation(event.getEntity().getLocation())).distanceSquared(zero);
		if((d-1E6)*0.00000044<Math.random() && Generator.basey(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())>4600){// in der Stadt
			if(event.getEntityType()==EntityType.SKELETON && Math.random()<0.01){
				Skeleton sk = event.getLocation().getWorld().spawn(event.getLocation(), Skeleton.class);
				makeWitherskeleton(sk, trueloc);
				event.setCancelled(true);
			}
		} else {
			if(event.getEntityType()==EntityType.SKELETON && ((Skeleton)event.getEntity()).getSkeletonType()==SkeletonType.NORMAL && Math.random()<0.7){
				Skeleton sk = event.getLocation().getWorld().spawn(event.getLocation(), Skeleton.class);
				makeWitherskeleton(sk, trueloc);
				event.setCancelled(true);
			}
		}
		
		if(d<25E6 && event.getEntityType()==EntityType.WITHER){
			Bukkit.broadcastMessage(Plugin.prefix+"It is forbidden to spawn �4WITHER�f in the city! Should a wither reach the magical shild 5km around the city, he will be killed!");
			event.setCancelled(true);
		}*/
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCreeperOderTNTExplosion(EntityExplodeEvent event){
		
		onTimeDoWitherkill();
		
		if(event.getLocation().getWorld().equals(Plugin.world)){
			double d = trueLocation(event.getEntity().getLocation()).distanceSquared(zero);
			if((d-1E6)*0.00000044<Math.random() && Generator.basey(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ())>4600){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBaumwachsevent(StructureGrowEvent event){//Baumwachsevent
		
		onTimeDoWitherkill();
		
		if(random()<Weather.wachstumsFaktor(event.getBlocks().get(0).getChunk())){
			event.setCancelled(true);
		}
	}
	
	/*@EventHandler
	public void onXPTeilchen(EntityTargetEvent event){//XP-Teilchen
		if(event.getEntity().getLocation().getWorld().getName()==Plugin.myWorldName){
			checkPos(event.getTarget());
		}
	}*/
	
	public static double distance(Location l1, Location l2){
		return trueLocation(l1).distanceSquared(trueLocation(l2));
	}
	
	public static Location trueLocation(Location falseLocation){return new Location(Plugin.world, Generator.ori(falseLocation.getBlockX()), Generator.basey(falseLocation.getBlockX(), falseLocation.getBlockZ())+falseLocation.getBlockY(), Generator.ori(falseLocation.getBlockZ()));}
	public static HashMap<String, Integer> logIn = new HashMap<>();
	public static boolean loggedIn(String name){
		return logIn.containsKey(name) && logIn.get(name)==0;
	}
	
	@EventHandler
	public void onCommand(org.bukkit.event.player.PlayerCommandPreprocessEvent e){
		if(loggedIn(e.getPlayer().getName())){
			String command = e.getMessage().substring(1);
			if(command.startsWith("login ") || command.startsWith("report ") || command.startsWith("help ")){
				//ok...
			} else {
				e.getPlayer().sendMessage(Plugin.serfix+"You have to login first. Aviable commands: help, report, login");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)//login
	public void onJoin(org.bukkit.event.player.PlayerJoinEvent event){//Join
		Player p = event.getPlayer();
		
		p.teleport(p.getLocation().add(0, 2, 0));
		
		boolean isNew = p.getStatistic(Statistic.PLAY_ONE_TICK)<100;
		SpawnBuilder.onJoin(p, false, isNew);
		String name = p.getName();
		
		if(isNew){
			Rank.setNickNameGetJoinMessage(name, p);
			event.setJoinMessage(SpawnBuilder.prefix+name+" joined the recivilisation project :)");
		} else {
			event.setJoinMessage(Rank.setNickNameGetJoinMessage(name, p));
			p.sendMessage(Sprache.select(p.getName(), "It's ", "Es ist im Moment ", null, null)+Weather.nowTime(Sprache.select(p.getName(), "t", "f", null, null).equals("t"))+"�f.");
		}
		
		if(Plugin.isServerInOffline){
			if(Rank.hasPassword.contains(p.getName())){
				logIn.put(p.getName(), 0);
				p.sendMessage(Plugin.serfix+"�cPlease login first! When your are sure you are you, and you forgot your password you have so send a report via /report.");
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000000, 1000));
			} else {
				p.sendMessage(Plugin.serfix+"�cThis server is running in �4offline mode�c. You can use '/login "+SpawnManager.getPassword(p.getName())+"'"+" to protect your account name.\n"
						+ "Once you used it use'll have to use it forever.\n"
						+ (p.getStatistic(Statistic.LEAVE_GAME)>0?"Your name was logged in allready so it might be insecure...[When a online player gets a problem the server mode will be changed to online!]":""));
			}
		}
		
		
	}
	
	public static HashMap<String, Boolean> dolayout = new HashMap<>();
	
	@EventHandler
	public void onSendChatmessage(AsyncPlayerChatEvent event){//XP-Teilchen
		
		// mache getannes noch zu kursivem �o
		
		onTimeDoWitherkill();
		
		Player p = event.getPlayer();
		String message = event.getMessage();
		
		if(p.getName().equalsIgnoreCase(Plugin.kingsname)){
			if(!dolayout.containsKey(p.getName())){
				dolayout.put(p.getName(), true);
			}
			if(dolayout.get(p.getName())){
				event.setMessage(message=StringCompare.convertIntoChat(message.replace('&', '�')));
			}
		} else if(p.getWorld()!=myworld){
			event.setMessage(message=StringCompare.convertIntoChat(message));
		} else {
			Location l = trueLocation(p.getLocation());
			
			if(Rank.hasPermisson(p.getName(), "col")){
				message = message.replace('&', '�');
			}
			
			if(!dolayout.containsKey(p.getName())){
				dolayout.put(p.getName(), true);
			}
			if(dolayout.get(p.getName())){
				message = StringCompare.convertIntoChat(message);
			}
					
			for(Player p2:Bukkit.getOnlinePlayers()){
				if(p.getWorld().equals(p2.getWorld())){
					double d = trueLocation(p2.getLocation()).distanceSquared(l);
					if(d<1E4){//100m
						p2.sendMessage("�f<"+p.getDisplayName()+"�f> "+message);
					} else if(d<6.25E4){//250m
						p2.sendMessage("�7<"+p.getDisplayName()+"�7> "+message);
					} else if(d<25E4){//500m
						p2.sendMessage("�8<"+p.getDisplayName()+"�8> "+message);
					} else if(d<6.25E6){//2500m
						p2.sendMessage("�0<"+p.getDisplayName()+"�0> "+message);
					} else if(p2.getName().equalsIgnoreCase("Miner952x")){
						p2.sendMessage("�1>"+p.getDisplayName()+"�1> "+message);
					}
				}
			}
			event.setCancelled(true);
			if(p.getName().equalsIgnoreCase(p.getDisplayName())){
				System.out.println("<"+p.getName()+"> "+message);
			} else {
				System.out.println("<"+p.getName()+"="+p.getDisplayName()+"> "+message);
			}
		}
		DynMap.addMessage(p, System.currentTimeMillis(), message);
	}
	
	public static boolean blockBreakIsOK(Player p, Block b){
		if(p!=null){
			if(Plugin.isFlying(p.getName())){
				p.sendMessage(Plugin.serfix+Sprache.select(p.getName(), 
						"You can't build in spectator mode!", 
						"Du darfst im Beobachtermodus nichts ver�ndern!", null, null));
				return false;
			}
			int x=p.getLocation().getBlockX(), z=p.getLocation().getBlockZ(), y=Generator.basey(x, z)+p.getLocation().getBlockY();
			x = Generator.ori(x);
			z = Generator.ori(z);
			if(b.hasMetadata(MetaString.deathProtected)){
				String s = b.getMetadata(MetaString.deathProtected).get(0).asString(), name=s.split(" ")[0];
				long last;
				if(name.equalsIgnoreCase(p.getName())){
					if((last=isNumber(s.split(" ")[1])-System.currentTimeMillis()/1000)>3600){
						p.sendMessage(Plugin.serfix+Sprache.select(p.getName(),
								"You have broken the sigil! Your chest stays save the next hour.",
								"Du hast das Sigil gebrochen! Deine Kiste ist noch 1h vor R�ubern sicher.", null, null));
						b.setMetadata(MetaString.deathProtected, new FixedMetadataValue(Plugin.instance, p.getName()+" "+(System.currentTimeMillis()/1000+3600)));
					} else {
						String time = secondsToTime(last);
						p.sendMessage(Plugin.serfix+Sprache.select(p.getName(),
								"You chest is only save for the next �4"+time+"!",
								"Deine Kiste ist nur noch �4"+time+"�f sicher!", null, null));
					}
					
					return true;
				} else if((last=isNumber(s.split(" ")[1])-System.currentTimeMillis()/1000)>0){
					String time=secondsToTime(last);
					p.sendMessage(Plugin.serfix+Sprache.select(p.getName(),
							"This Death Cheat(@"+name+") is still protected for the next "+time+"!",
							"Diese Grabeskiste(@"+name+") ist noch f�r die n�chsten "+time+" versiegelt!", null, null));
					return false;
				} else {
					p.sendMessage(Plugin.serfix+Sprache.select(p.getName(),
							"You found a deserted Death Chest!",
							"Du hast eine verlassene Grabeskiste gefunden!", null, null));
					b.removeMetadata(MetaString.deathProtected, Plugin.instance);
					return true;
				}
			}
			if(y>=4740){
				if(Jena.type(x, z)<2){
					if(x*x+z*z>900){
						boolean king = p.getName().equalsIgnoreCase(Plugin.kingsname);
						if(Grundstück.isGrundstück(x, z)){
							Grundstück g = Grundstück.get(x, z);
							if(g == null){
								// alles ok, mache sp�ter weiter
								// teste noch, ob keine Grundstücke in der N�he sind...
							} else {
								String owner = g.getOwner();
								if(owner.equalsIgnoreCase(p.getName())){
									// du bist der Besitzer...
									return true;
								} else if(king && owner.equals("@king")){
									// du bist der K�nig und das Grundstück ist eines deines Reiches...
									return true;
								} else if(g.isChangeAllowed(p.getName())){// hier ist ein Grundstück, doch es geh�rt nicht wirklich dir... darfst du nach K�nig drauf bauen?
									return true;
								} else {
									// fremdes Eigentum
									return false;
								}
							}
						}
						
						if(Grundstück.isNearProtectedGrundstück(p.getName(), x, z)){
							// du hast hier keine Rechte, also raus...
							return false;
						} else {
							// du darfst hier bauen, also Gl�ck Auf!
							return true;
						}
					} else if(sq(x+0.3)+sq(z+0.1)<263.46){
						p.sendMessage(Plugin.prefix+Sprache.select(p.getName(),
								"The area belongs to the Bank!",
								"Dieses Gebiet geh�rt zur Bank!", null, null));
						return false;
					} else if(p.getName().equalsIgnoreCase(Plugin.kingsname)){
						return true;
					} else {
						p.sendMessage(Plugin.prefix+Sprache.select(p.getName(),
								"Only the "+(Plugin.isQueen?"Queen":"King")+" can build in this area!",
								"Nur d"+(Plugin.isQueen?"ie K�nigin":"er K�nig")+" kann hier bauen!", null, null));
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockbreak(BlockBreakEvent event){
		
		onTimeDoWitherkill();
		
		if(event.getBlock().getWorld()!=myworld){
			if(event.getBlock().getWorld().getName().endsWith("_the_end") && !event.getPlayer().getName().equals("Miner952x")){
				if(random()<0.05){
					event.getPlayer().kickPlayer(Sprache.select(event.getPlayer().getName(),
							"�4Don�t break blocks!",
							"�4Mach nichts kaputt!", null, null));
				}
				event.setCancelled(true);
			} else {
				event.getBlock().getChunk().getBlock(0, 0, 0).setMetadata(MetaString.save, new FixedMetadataValue(Plugin.instance, event.getPlayer().getName()));
			}
			return;
		}
		
		Block b = event.getBlock();
		Player p = event.getPlayer();
		
		if(b.getTypeId()==68){// Schild an der Wand -> braucht Kiste im Umkreis von 1Block (drunter, links, rechts, oder dr�ber)
			
			Sign s = (Sign) b.getState();
			
			int orix=Generator.ori(s.getX()), oriz=Generator.ori(s.getZ());
			
			if(orix*orix+oriz*oriz<900 && p.getName().equalsIgnoreCase(Plugin.kingsname)){
				//Rank.analyseCommand(s.getLines(), p, true); <- wird nicht gebraucht, da die Linien hier noch nicht gesetzt werden :)
			} else if(s.getLine(0).equalsIgnoreCase("Sell plot!")){// hat schon s? eigentlich ja ja
				// Sell plot!
				// <username>
				//  <price>
				// <plotname>
				String l1=s.getLine(1), l2=s.getLine(2), l3=s.getLine(3);
				int price;
				if(isName(l1) && (price=isNumber(l2))>-1){
					if(Grundstück.isPlotnameByOwnerAndRealplot(l3, l1, orix, oriz)){
						if(p.getName().equalsIgnoreCase(l1)){
							p.sendMessage(Plugin.prefix+Sprache.select(p.getName(),
									"�eYou successfully removed your offer.",
									"�eDein Angebot wurde erfolgreich zur�ckgezogen.", null, null));
							return;
						} else if(Bank.getBilance(p)>=price){
							try {
								if(Grundstück.changeOwner(l1, p.getName(), l3)){
									if(Bank.substract(p.getName(), price)){
										Bank.add(l1, price);
										p.sendMessage(Plugin.prefix+Sprache.select(p.getName(),
												"�4Congratulations! You should chance the name of your plot to prevent bugs ;) /rename <originalname> <newname>",
												"�4Herzlichen Gl�ckwunsch! Du solltest sicherheitshalber den Grundstücksnamen �ndern! /rename <jetziger Name> <neuer Name>", null, null));
										return;
									} else {
										Bank.sendErrMessage(p, 0);
										event.setCancelled(true);
										return;
									}
								} else {
									p.sendMessage(Plugin.prefix+Sprache.select(p.getName(),
											"�4I am very sorry, but plot transfer failed!",
											"�4Tut mir leid, aber die Eigentums�bergabe mislang!", null, null));
									event.setCancelled(true);
									return;
								}
							} catch (IOException e) {
								e.printStackTrace();
								System.out.println("ERROR selling plot:"+l1+"."+l2+"."+l3);
								Bank.sendErrMessage(p, 0);
								event.setCancelled(true);
								return;
							}
						} else {
							Bank.sendErrMessage(p, 1);
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
		
		Chunk c;
		if(!(c=b.getChunk()).getBlock(0, 0, 0).hasMetadata(MetaString.save)){
			int bx=c.getX()*16, bz=c.getZ()*16;
			boolean mustRefresh=false;
			f:for(int x=0;x<16;x++){
				for(int z=0;z<16;z++){
					if(Converter.getRealCubeID(bx+x, bz+z)==30){
						mustRefresh = true;
						break f;
					}
				}
			}
			if(mustRefresh){
				MapsGenerator.ruinpop.populate(b.getWorld(), null, c);
			}
			c.getBlock(0, 0, 0).setMetadata(MetaString.save, new FixedMetadataValue(Plugin.instance, p.getName()));
		}
		
		if(p!=null && p.getGameMode()!=GameMode.CREATIVE && isGoodAsAHand(p.getItemInHand().getTypeId()) && !breakableByHand(b.getTypeId()) && !p.getName().equalsIgnoreCase("The_Aletheia")){
			if(Math.random()<0.1)p.sendMessage(Plugin.prefix+"You should try something different. #wooddoesntbreakbyhandsyouneedsticksfromleaves");
			event.setCancelled(true);
			return;
		}
		
		if(!blockBreakIsOK(p, b)){
			event.setCancelled(true);
			return;
		}
		
		if(XBlock.cancelBreak(b, p, event)){
			event.setCancelled(true);
			return;
		}
		
		if(b.hasMetadata(MetaString.deathProtected)){
			b.removeMetadata(MetaString.deathProtected, Plugin.instance);
		}
		final boolean save=true;//saveID(b.getTypeId());
		if(save){// Gras abzuspeichern ist �berbewertet ^^
			b.getChunk().getBlock(0, 0, 0).setMetadata(MetaString.save, new FixedMetadataValue(Plugin.instance, event.getPlayer().getName()));
		}
		
		if(b.getWorld().equals(Plugin.world)){
			if(b.getLocation().getBlockY()<32 || b.getLocation().getBlockY()>256-32){
				double[] ds = cooOfNotherWorld(b.getX(),b.getY(),b.getZ());
				Block b2=b.getWorld().getBlockAt((int)ds[0],(int)ds[1],(int)ds[2]);
				b2.setType(Material.AIR);
				if(save)
					b2.getChunk().getBlock(0, 0, 0).setMetadata(MetaString.save, new FixedMetadataValue(Plugin.instance, event.getPlayer().getName()));
			}
		}
		
		if(p!=null){
			if(p.getItemInHand()==null || p.getItemInHand().getTypeId()!=359){//Schere
				if(b.getTypeId()==18 || b.getTypeId()==261){// Bl�tter
					if(Math.random()<0.1){
						b.getWorld().dropItem(b.getLocation(), new ItemStack(280, Math.random()<0.2?2:1));
					}
				} else if(b.getTypeId()==31){// Gras
					if(random()<0.02){//St�cker :)
						b.getWorld().dropItem(b.getLocation(), new ItemStack(280));
					} else if(random()<0.01){
						b.getWorld().dropItem(b.getLocation(), new ItemStack(Material.SEEDS));
					}
				} else if(b.getTypeId()==2){
					if(Math.random()<0.01){
						b.getWorld().dropItem(b.getLocation(), new ItemStack(random.nextBoolean()?Material.CARROT_ITEM:Material.POTATO_ITEM));
					}
				}
			}
		}
	}
	
	/*private boolean saveID(int id) {
		switch(id){
		case 31:case 37:case 38:case 39:case 40:case 81:return false;
		}
		return true;
	}*/

	private boolean   isGoodAsAHand(int id) {return "-0-6-18-31-32-38-39-40-50-69-70-72-76-77-96-171-101-106-111-131-143-147-148-151-161-167-168-171-175-259-260-263-288-289-295-296-331-332-334-337-341-344-348-349-350-351-352-353-354-355-356-357-358-359-360-353-370-371-375-424-425-".contains("-"+id+"-");}
	private boolean breakableByHand(int id) {return "-6-12-18-20-30-31-32-37-38-39-40-59-61-69-72-76-77-78-79-80-81-82-83-85-86-96-171-102-103-104-105-106-107-111-131-141-142-143-161-171-".contains("-"+id+"-");}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onSignset(org.bukkit.event.block.SignChangeEvent event){
		
		Block b = event.getBlock();
		Player p = event.getPlayer();
		
		for(int i=0;i<event.getLines().length;i++){
			event.setLine(i, event.getLine(i).replace('&', '�').replace("\\�", "&"));
		}
		
		Plugin.removeAfk(p.getName());
		
		if(!blockBreakIsOK(p, b) || b.getWorld().getName().endsWith("_the_end")){
			if(p.getName().equalsIgnoreCase("Miner952x")){
				event.getBlock().getChunk().getBlock(0, 0, 0).setMetadata(MetaString.save, new FixedMetadataValue(Plugin.instance, event.getPlayer().getName()));
			} else event.setCancelled(true);
			return;
		}
		// !!! signupdate fehlt noch
		if(b.getWorld().equals(Plugin.world)){
			if(p.getName().equalsIgnoreCase(Plugin.kingsname)){
				if(sq(ori(b.getX()))+sq(ori(b.getZ()))<900){
					Rank.analyseCommand(event.getLines(), p, false);
				}
			} else if(event.getLine(0).equalsIgnoreCase("Sell plot!") && event.getLine(1).equalsIgnoreCase("@king")){
				p.sendMessage(Plugin.prefix+Sprache.select(p.getName(),
						"Don't cheat the "+(Plugin.isQueen?"Queen":"King")+"! If your are married the "+(Plugin.isQueen?"Queen":"King")+" has to split your properties.",
						"Versuche nicht "+(Plugin.isQueen?"die K�nigin":"den K�nig")+" zu hintergehen! When ihr verheiratet seid muss d"+(Plugin.isQueen?"ie K�nigin":"er K�nig")+" sich um eure Besitzesaufteilung k�mmern!", null, null));
				event.setLine(1, "<�4null�f>");
			}
		}
		
		event.getBlock().getChunk().getBlock(0, 0, 0).setMetadata(MetaString.save, new FixedMetadataValue(Plugin.instance, event.getPlayer().getName()));
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockplace(final BlockPlaceEvent event){
		
		onTimeDoWitherkill();
		
		Plugin.removeAfk(event.getPlayer().getName());
		
		if(event.getBlock().getWorld()!=myworld){
			if(event.getBlock().getWorld().getName().endsWith("_the_end") && !event.getPlayer().getName().equals("Miner952x")){
				event.setCancelled(true);
			} else {
				event.getBlock().getChunk().getBlock(0, 0, 0).setMetadata(MetaString.save, new FixedMetadataValue(Plugin.instance, event.getPlayer().getName()));
			}
			return;
		}
		
		Block b = event.getBlock();
		Player p = event.getPlayer();
		final boolean change = (b.getTypeId()==43 && b.getData()==2) || (b.getTypeId()==125 && b.getData()==7);
		
		// naja Xray eine Chance gegen Hacking - so muss niemand anfangen bzw man ist gleich auf
		/*if(b.getLocation().distanceSquared(p.getLocation().add(0, 1, 0))<1){
			event.setCancelled(true);
			if(random()<0.3){
				p.sendMessage(Plugin.prefix+"Sorry, Xray is disabled.\nPlease do not use texture/resource-packs, hacks or mods for that. Be a fair player!");
			}
			return;
		}*/
		
		if(!blockBreakIsOK(p, b)){
			event.setCancelled(true);
			return;
		}
		
		final byte data;
		if((data=XBlock.cancelPlace(b, p, p.getItemInHand(), event))==-1){
			event.setCancelled(true);
			return;
		}
		
		event.getBlock().getChunk().getBlock(0, 0, 0).setMetadata(MetaString.save, new FixedMetadataValue(Plugin.instance, event.getPlayer().getName()));
		
		if(b.getWorld().equals(Plugin.world)){
			
			if(change){
				b.setTypeIdAndData(b.getTypeId()==43?3:170, (byte) 0, false);
			}
			
			if(b.getLocation().getBlockY()<32 || b.getLocation().getBlockY()>256-32){
				double[] ds = cooOfNotherWorld(b.getX(), b.getY(), b.getZ());
				final int d=(int)ds[0], e=(int)ds[1], f=(int)ds[2], g=b.getX(), h=b.getY(), i=b.getZ();
				
				final World w = b.getWorld();
				
				new Thread(new Runnable(){
					@Override public void run() {
						try {
							Chunk c = w.getChunkAt(d, f);
							if(!c.isLoaded()){
								c.load();
							}
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
						Block b = w.getBlockAt(g, h, i);
						
						b.getChunk().getBlock(0, 0, 0).setMetadata(MetaString.save, new FixedMetadataValue(Plugin.instance, event.getPlayer().getName()));
						
						if(change){
							w.getBlockAt(d,e,f).setTypeIdAndData(b.getTypeId()==43?3:170, (byte) 0, false);
						} else {
							w.getBlockAt(d,e,f).setTypeIdAndData(b.getTypeId(), data, true);
						}
					}
				}).start();
			}
		}
	}
	
	@EventHandler
	public void onSave(WorldSaveEvent event){
		
		onTimeDoWitherkill();
		
		if(event.getWorld().getName().equalsIgnoreCase(Plugin.myWorldName)){
			try {
				Plugin.save(false);
			} catch (IOException e) {
				System.err.println("ERROR while saving data...");
				e.printStackTrace();
			}
		}
	}
	
	private static String num(int i, int j){
		String s = "          "+i;
		return s.substring(s.length()-j);
	}
	
	HashMap<String, String> hs = new HashMap<>();
	public void updateScoreboard(Player p, boolean atSpawn){
		if(!hs.containsKey(p.getName())){
			hs.put(p.getName(), "");
		}
		
		int h;
		if(atSpawn){
			h = p.getLocation().getBlockY() + 814120;
		} else {
			h = (-4800+Generator.basey(p.getLocation().getBlockX(), p.getLocation().getBlockZ())+p.getLocation().getBlockY());
		}
		
		if(hs.get(p.getName())!=p.getLocation().getBlockX() + ":" + h + ":" + p.getLocation().getBlockZ()){
			hs.put(p.getName(), p.getLocation().getBlockX() + ":" + h + ":" + p.getLocation().getBlockZ());
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			
			/*Objective name = board.registerNewObjective("213", "888");
			name.setDisplayName(p.getDisplayName()+"xD");
			name.setDisplaySlot(DisplaySlot.BELOW_NAME);
			
			name.getScore(Bukkit.getOfflinePlayer("Alexandra")).setScore(0);*/
			
			//OfflinePlayer op = p;

		    /*Team team = board.registerNewTeam("Helden");
		      
		    team.setDisplayName("�cGronkh");
		    team.setPrefix("0123456789abcdef");
		    team.setSuffix("0123456789abcdef");
		    team.setCanSeeFriendlyInvisibles(false);
		    
		   // team.addPlayer(op);

		    for(Player online : Bukkit.getOnlinePlayers()){
		    	team.addPlayer(online);
		    }
		    
		    for(Player online : Bukkit.getOnlinePlayers()){
		    	online.setScoreboard(board);
		    }
			*/
			
			Objective obj = board.registerNewObjective("213", "879");
			
			obj.setDisplayName(" = Stats = ");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			
			Score sc3 = obj.getScore(Bukkit.getOfflinePlayer("�cx"+num(ori(p.getLocation().getBlockX()), 7)));
			sc3.setScore(3);
			
			Score sc2 = obj.getScore(Bukkit.getOfflinePlayer("�ay"+num(h, 7)));
			sc2.setScore(2);
			
			Score sc1 = obj.getScore(Bukkit.getOfflinePlayer("�9z"+num(ori(p.getLocation().getBlockZ()), 7)));
			sc1.setScore(1);
			if(!atSpawn){
				Score sc0 = obj.getScore(Bukkit.getOfflinePlayer("�6g"+num((int) Bank.getBilance(p), 7)));
				sc0.setScore(0);
			}
			
			p.setScoreboard(board);
		}
	}
	
	@EventHandler
	public void onMinecartMove(VehicleMoveEvent event){//H�lt Minecarts in der richtigen Welt
		
		onTimeDoWitherkill();
		
		if(event.getVehicle().getLocation().getWorld().getName().equalsIgnoreCase(Plugin.myWorldName)){
			checkPos(event, event.getVehicle());
		}
	}
	
	/*@EventHandler BlockFormEvent
	public void onX(org.bukkit.event.block.BlockFormEvent event){
		onTimeDoWitherkill();}*/
	
	@EventHandler
	public void onSeedsGrow(org.bukkit.event.block.BlockGrowEvent event){

		onTimeDoWitherkill();
		
		if((event.getBlock().getLightFromBlocks()<9 && trueLocation(event.getBlock().getLocation()).getBlockY()<4730) || random()<Weather.wachstumsFaktor(event.getBlock().getChunk())){
			event.setCancelled(true);
		}
	}
	
	/*@EventHandler BlockIgniteEvent
	public void onX(org.bukkit.event.block.BlockIgniteEvent event){
		onTimeDoWitherkill();}*/
	
	/**
	 * A list of all possible pings :)
	 * */
	public static final String[] pingString = new String[]{
		"�6RPG-Hardcore | �eProblems? Youtube: Antonio Noack :)",
		"�6RPG-Hardcore | �5Be a part of us!",
		"�6RPG-Hardcore | �aSpring is coming :D!",
		"�6RPG-Hardcore | �4Remember to use Conquest!"
	};
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onServerListPing(ServerListPingEvent event) throws IllegalArgumentException, UnsupportedOperationException, Exception {
		System.out.println("ping by "+betterByte(event.getAddress().getAddress()[0])+"."+betterByte(event.getAddress().getAddress()[1])+"."+betterByte(event.getAddress().getAddress()[2])+"."+betterByte(event.getAddress().getAddress()[3]));
		event.setMotd(short64(pingString[random.nextInt(pingString.length)]+(eventByKing.length()>0?("�4 | �f"+eventByKing):"")));
		//event.setMaxPlayers(9001);
		event.setServerIcon(icon());
	}
	
	private String short64(String s){
		return s.length()>64?s.substring(0, 64):s;
	}
	
	int betterByte(byte b){
		return b<0?(b&0x7f)+128:b;
	}
	
	/**kann theoretisch auf Karten wie auf Bilder malen*/
	/*BufferedImage img;
	@EventHandler(priority=EventPriority.MONITOR)
	public void onServerListPifng(org.bukkit.event.server.MapInitializeEvent event) throws IllegalArgumentException, UnsupportedOperationException, Exception {
		event.getMap().addRenderer(new org.bukkit.map.MapRenderer(){
			@Override
			public void render(org.bukkit.map.MapView view, org.bukkit.map.MapCanvas canvas, Player p) {
				System.out.println("render"+view.getCenterX());
				if(img==null){
					try {
						canvas.drawImage(0, 0, img=ImageIO.read(new File("C:/Users/Antonio/Desktop/Plugins/_Server/test.png")));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					canvas.drawImage(0, 0, img);
				}
			}
		});
	}*/
	
	@EventHandler
	public void onDeath(org.bukkit.event.entity.EntityDeathEvent event){
		if(event.getEntity() instanceof Player){
			return;// unten drunter abgehandelt
		}
		if(event.getEntity().getType()==EntityType.SNOWMAN && random()<0.3){
			event.getDrops().add(new ItemStack(Material.PUMPKIN));
		}
		if(event.getEntity().getKiller()!=null){
			if(random()<0.01)event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), playerHead(event.getEntityType()==EntityType.SKELETON && ((Skeleton)event.getEntity()).getSkeletonType()==SkeletonType.WITHER?"WITHER":event.getEntityType().name()));
			SpawnManager.kill(event.getEntity());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(org.bukkit.event.entity.PlayerDeathEvent event){
		
		onTimeDoWitherkill();
		
		if(event.getEntity().getKiller()!=null){
			long tenPercent = Bank.getBilance(event.getEntity())/10;
			if(tenPercent > 0 && Bank.substract(event.getEntity().getName().toLowerCase(), tenPercent)){
				Bank.add(event.getEntity().getKiller().getName().toLowerCase(), tenPercent);
				event.getEntity().getKiller().sendMessage("You got "+tenPercent+"g by killing "+event.getEntity().getDisplayName());
				if(random()<0.1){
					event.getEntity().getKiller().getInventory().addItem(playerHead(event.getEntity().getName()));
				}
			}
		}
		
		final Player p = event.getEntity();
		boolean deathChest = false;
		if(p.getWorld()==Plugin.world){
			boolean b;
			event.getEntity().sendMessage((b=event.getEntity().getStatistic(Statistic.PLAYER_KILLS)/max(1, event.getEntity().getStatistic(Statistic.DEATHS))>3)?"�4[Mephisto->"+Rank.getNickName(p.getName())+"] Your travelling has an end. Enjoy your time! /wakeup":"�2[God->"+Rank.getNickName(p.getName())+"] You seem a bit sleepy. Have nice dreams :) /wakeup");
			
			String pos;
			if(!SpawnBuilder.deathMapDream.containsKey(p.getName()) || (pos=SpawnBuilder.deathMapDream.get(p.getName())).equals("null")){
				SpawnBuilder.onDeath(event.getEntity(), b);
			} else {
				
				final Location loc = new Location(Bukkit.getWorlds().get(0), MathHelper.isNumber(pos.split("\\.")[0]), MathHelper.isNumber(pos.split("\\.")[1], 70), MathHelper.isNumber(pos.split("\\.")[2]));
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable(){

					@Override public void run() {
						p.teleport(loc);
					}
					
				}, 10);
			}
			
			deathChest = true;
			
		} else if(event.getEntity().getWorld().getName().endsWith("spawn_the_end")){
			event.setDeathMessage(p.getName()+" died at ISS");
			Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable(){

				@Override public void run() {
					Location l = new Location(p.getWorld(), 0.5, 68, 1.5);
					l.setDirection(new Vector(0, 0, -1));
					p.teleport(l);
				}
				
			}, 10);
			
		} else {
			
			// wenn er im Traum stirbt kommt er in die Stadt zur�ck
			String pos;
			if(!SpawnBuilder.deathMapNormal.containsKey(p.getName()) || (pos=SpawnBuilder.deathMapNormal.get(p.getName())).equals("null")){
				SpawnBuilder.onJoin(event.getEntity(), true, false);
			} else {
				final String po = pos;
				Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable(){

					@Override public void run() {
						Plugin.instance.onCommand(p, null, "xtp", po.split("\\."));
					}
					
				}, 100);
			}
			
			deathChest = true;
		}
		
		if(deathChest){
			
			int x=p.getLocation().getBlockX(), y=(int) min(254, max(0, p.getLocation().getY()+0.999)), z=p.getLocation().getBlockZ();
			World w = p.getWorld();
			Block b = w.getBlockAt(x, y, z);
			if(b.getRelative(BlockFace.DOWN).getType()==Material.AIR){
				y--;
				b=w.getBlockAt(x, y, z);
			}
			Location trueLoc = w==Plugin.world?trueLocation(b.getLocation()).add(0,-4800,0):b.getLocation();
			
			// speichere...
			int i=0;
			ArrayList<ItemStack> toSave = new ArrayList<>();
			for(ItemStack s:p.getInventory().getArmorContents()){
				if(s!=null){
					toSave.add(s);
					i++;
				}
			}
			for(ItemStack s:p.getInventory().getContents()){
				if(s!=null){
					toSave.add(s);
					i++;
				}
			}
			event.getDrops().clear();
			if(i>0){
				b.setTypeIdAndData(5, (byte) 13, false);
				if(b.hasMetadata(MetaString.deathProtected)){
					b.removeMetadata(MetaString.deathProtected, Plugin.instance);
				}
				b.setMetadata(MetaString.deathProtected, new FixedMetadataValue(Plugin.instance, p.getName()+" "+(System.currentTimeMillis()/1000+604800)));
				b.setMetadata(MetaString.inventory, new FixedMetadataValue(Plugin.instance, toSave));
				p.sendMessage("[�2Server�f] Your �4Inventory Contents�f("+i+" Stacks) were saved �6at "+trueLoc.getBlockX()+" "+trueLoc.getBlockY()+" "+trueLoc.getBlockZ()+"�f. They are save for the next 7 reallife days.");
			} else {
				p.sendMessage("[�2Server�f] You had nothing in inventory, so nothing was saved!");
			}
		}
	}
	
	@EventHandler
	public void onBedEnter(org.bukkit.event.player.PlayerBedEnterEvent event){
		Player p = event.getPlayer();
		if(p.getWorld()==Plugin.world){
			Location trueloc = BlockListener.trueLocation(p.getLocation()).add(0,-4800,0);
			String pos=trueloc.getBlockX()+"."+trueloc.getBlockY()+"."+trueloc.getBlockZ();
			p.sendMessage(Plugin.serfix+"Set spawnlocation to "+pos);
			SpawnBuilder.deathMapNormal.put(p.getName(), pos);
		} else if(p.getWorld()==Bukkit.getWorlds().get(0)){
			Location trueloc = p.getLocation();
			String pos=trueloc.getBlockX()+"."+trueloc.getBlockY()+"."+trueloc.getBlockZ();
			p.sendMessage(Plugin.serfix+"Set spawnlocation to "+pos);
			SpawnBuilder.deathMapDream.put(p.getName(), pos);
		}
	}
	
	/*@EventHandler BlockFadeEvent
	public void onX(org.bukkit.event.block.BlockFadeEvent event){// Lava bewegt sich..
		
		//onTimeDoWitherkill();
		//System.out.println("Fade");
		// Feldblock geht kaputt
		//Bukkit.broadcastMessage(event.getNewState().getTypeId()+"");
	}*/
	
	/*@EventHandler BlockPhysicsEvent
	public void onX(org.bukkit.event.block.BlockPhysicsEvent event){
		// Sand/Kies, Kiste �ffnen und so...
		// falscher Schnee verschwindet... -> solange es den gibt, wird das Event gecancelled! -> gibts nicht mehr, also egal :)
		if(event.getBlock().getType()==Material.SNOW){
			
			System.out.println(event.getChangedType().name()+" blockphysics cancelled in blocklistener by snow decaying: "+event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(0, -1, 0)).getType().name());
			event.setCancelled(true);
		}
	}*/
	
	/*@EventHandler BlockFromToEvent
	public void onX(org.bukkit.event.block.BlockFromToEvent event){
		
		onTimeDoWitherkill();
		System.out.println("FormTo");
		// Lava
		Bukkit.broadcastMessage(event.getBlock().getState().getTypeId()+"");
	}*/

	@EventHandler
	public void onPlayermove(PlayerMoveEvent event){//H�lt den Spieler in der richtigen Welt
		
		onTimeDoWitherkill();
		
		Plugin.removeAfk(event.getPlayer().getName());
		
		if(event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(Plugin.myWorldName)){
			Player p = event.getPlayer();
			checkPos(event, (Entity)p);
			updateScoreboard(p, false);
		} else if(event.getPlayer().getLocation().getWorld().getName().endsWith("_the_end")){
			updateScoreboard(event.getPlayer(), true);
		}
	}
	
	@EventHandler
	public void onVehicleMinecartUpdate(org.bukkit.event.vehicle.VehicleUpdateEvent event){
		
		onTimeDoWitherkill();
		
		if(event.getVehicle().getLocation().getWorld().getName().equalsIgnoreCase(Plugin.myWorldName)){
			checkPos(event, event.getVehicle());
		}
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack playerHead(String name){
		boolean animal=false, ani=false;
		switch(name){
		case "CREEPER":
		case "WITHER":
		case "ZOMBIE":
		case "SKELETON":
			animal = true;
		}
		if(!animal){
			if(EntityType.fromName(name)!=null)name = "MHF_"+name;
			ani = true;
		}
		ItemStack ret = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.valueOf(animal?name:"PLAYER").ordinal());
		SkullMeta meta = (SkullMeta) ret.getItemMeta();
		meta.setOwner(name);
		if(ani)
			meta.setDisplayName("�f"+betterEntityName(name)+" Skull");
		else if(!animal)
			meta.setDisplayName(Rank.getNickName(name)+"'s Head");
		ret.setItemMeta(meta);
		return ret;
	}
	
	static String betterEntityName(String s){
		return s.charAt(4)+s.substring(5).toLowerCase();
	}
}
