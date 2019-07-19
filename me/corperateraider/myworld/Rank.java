package me.corperateraider.myworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import me.corperateraider.generator.MathHelper;
import me.corperateraider.myworld.Sprache.Used;
import me.corperateraider.reload.SpawnBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Rank extends MathHelper {
	
	public static ArrayList<String> hasPassword = new ArrayList<>();
	
	public static void firstTime(Player p){
		if(Rank.players.containsKey(p.getName())){
			return;
		}
		
		String lang=null;
		try {
			lang = getLanguage(p);
		} catch (NoSuchFieldException|SecurityException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
			e.printStackTrace();
		}
		if(lang!=null && lang.length()>2){
			Used u;
			Sprache.pls.put(p.getName(), u=Sprache.Used.byShortcut(lang.substring(0, 2)));
			Rank.putNickName(p.getName(), p.getName());
			Rank.players.put(p.getName(), zero);
			
			if(u!=Used.English){
				p.sendMessage(Sprache.select(p.getName(),
						"Detected english client language...",
						Plugin.serfix+"deutsche Spracheinstellung wurde festgestellt und gemerkt. Zum ändern /lang",
						Plugin.serfix+"Detected Frensh language. I am sorry this is not really supported. If you help to translate other Frensh people may can play here with Frensh texts :)",
						Plugin.serfix+"Detected Spanish language. I am sorry this is not really supported. If you help to translate other Spanish people may can play here with Spanish texts :)"));
			}
		}
	}
	
	private static String getLanguage(Player p) throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Object ep = getMethod("getHandle", p.getClass()).invoke(p, (Object[]) null);
		Field f = ep.getClass().getDeclaredField("locale");
		f.setAccessible(true);
		String language = (String) f.get(ep);
		return language;
	}
	
	private static Method getMethod(String name, Class<?> clazz) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (m.getName().equals(name)){
				return m;
			}
		}
		return null;
	}
	
	private static HashMap<String, String> nicknames = new HashMap<>();
	static HashMap<String, Rank> players = new HashMap<>();
	static HashMap<String, Rank> ranks = new HashMap<>();
	
	public static String getNickName(String name){
		if(nicknames.containsKey(name)){
			return nicknames.get(name);
		} else return name;
	}
	
	public static void putNickName(String name, String nickname){
		nicknames.put(name, nickname);
	}
	
	public static Set<String> getKeySet(){
		return nicknames.keySet();
	}
	
	static String prefix = Plugin.prefix+"@Rank";
	
	public static void analyseCommand(String[] s, Player king, boolean removeNotSet){
		if(s!=null && s.length==4){
			if(s[0]!=null && s[0].length()>1){
				if(s[0].startsWith("#")){// regelt die Permissoons für einen Rank. Diese sind so aufgeschrieben: "a,b,build c,d,e,f\ng,h"
					s[0]=s[0].substring(1);
					if(removeNotSet){
						Rank rank = ranks.get(s[0]);
						if(rank == null){
							if(king!=null)king.sendMessage(prefix+".error No rank '"+s[0]+"'");
						} else {
							// lösche die Rechte aus dem Rang (nicht ganz richtig, falls dieser noch auf einem anderem Schild steht, aber ok)
							ranks.put(s[0], zero);
							king.sendMessage(prefix+".success Deleted rank '"+s[0]+"'");
						}
					} else {
						// gebe dem Rang seine Rechte
						if(ranks.containsKey(s[0])){
							if(king!=null)king.sendMessage(prefix+".warning Rank data can not be written on two signs! The new rank will only contain the data of this sign!");
						}
						ranks.put(s[0], new Rank(s[0], ((s[1]==null?"":s[1])+"-"+(s[2]==null?"":s[2])+"-"+(s[3]==null?"":s[3])).replace("---", "-").replace("--", "-").split("-")));
						if(king!=null)king.sendMessage(prefix+".success Added rank '"+s[0]+"' with permissions "+permissionsAsString(ranks.get(s[0]).permissions));
					}
					s[0]="#"+s[0];
				} else if(s[0].startsWith("@")){
					Rank rank = ranks.get(s[0].substring(1));
					Player p;
					if(rank == null){
						if(king!=null)king.sendMessage(prefix+".error There is no rank with the name '"+s[0].substring(1)+"'");
					} else {
						if(king!=null)king.sendMessage(prefix+"."+rank.label);
						for(int i=1;i<4;i++){
							if(s[i].length()>0){
								if(removeNotSet){
									if(rank.equals(players.get(s[i]))){
										players.remove(s[i]);
										if((p=Bukkit.getPlayer(s[i]))!=null && p.isOnline()){
											setNickNameGetJoinMessage(s[i], p);
											if(king!=null)king.sendMessage("removed "+s[i]+"(online)");
										} else {
											if(king!=null)king.sendMessage("removed "+s[i]+"(online");
										}
									}
								} else {
									players.put(s[i], rank);
									if((p=Bukkit.getPlayer(s[i]))!=null && p.isOnline()){
										setNickNameGetJoinMessage(s[i], p);
										if(king!=null)king.sendMessage("added "+s[i]+"(online)");
									} else {
										nicknames.put(s[i], s[i]);
										if(king!=null)king.sendMessage("added "+s[i]+"(offline)");
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static String randColor() {
		return "§"+("2379abce".charAt(MathHelper.random.nextInt(8)));
	}
	
	public static String setNickNameGetJoinMessage(String name, Player p){
		if(Rank.nicknames.containsKey(name)){
			String nickname=Rank.nicknames.get(name), ret;
			ret = " + "+nickname;
			boolean randC=false;
			if(Plugin.kingsname.equalsIgnoreCase(name)){
				p.setDisplayName(name = "§4"+(Plugin.isQueen?"Queen":"King")+" "+nickname+"§f");
				if(nickname.length()<15){
					nickname="§4"+nickname;
				}
			} else if(Rank.players.containsKey(name) && Rank.players.get(name)!=zero){
				String label = Rank.players.get(name).label;
				p.setDisplayName(name = label+" "+nickname);
				randC=!label.startsWith("§");
			} else {
				randC=true;
				p.setDisplayName(name = Rank.nicknames.get(name));
			}
			if(randC && nickname.length()<15){
				p.setDisplayName(nickname=randColor()+nickname);
				p.setPlayerListName(nickname);
			} else {
				p.setPlayerListName(nickname);
			}
			return ret;
		} else {
			if(name.length()<15){
				p.setPlayerListName(randColor()+name);
			}
			return " + "+name+" @ISS";
		}
	}

	public static String getQuitMessage(String name){
		if(Rank.nicknames.containsKey(name)){
			return " §c- "+Rank.nicknames.get(name);
		} else {
			return " §c- "+name+" @ISS";
		}
	}
	
	static Rank zero;
	public static void ini() throws IOException{
		// load all names[rank]
		ranks.put("none", zero=new Rank("zero"));
		File f = new File(Plugin.instance.getDataFolder(), "data/rank.sec");
		if(f.exists()){
			BufferedReader read = new BufferedReader(new FileReader(f));
			Plugin.kingsname=read.readLine();
			String[] ss;
			for(String s=read.readLine();s!=null;s=read.readLine()){
				// #King -street -fly -kill -imprison ...
				// Originalname Sprache Spitzname Rang
				if(s.startsWith("#") && !s.equalsIgnoreCase("#zero -")){
					ss = s.split(" ");
					ranks.put(ss[0].substring(1), new Rank(ss[0].substring(1), s.replaceFirst(ss[0], "").split("-")));
				} else if((ss=s.split(" ")).length==4 && !s.contains("/")){
					Sprache.pls.put(ss[0], Used.byShortcut(ss[1]));
					nicknames.put(ss[0], ss[2]);
					if(ranks.containsKey(ss[3])){
						players.put(ss[0], ranks.get(ss[3]));
					} else {
						players.put(ss[0], zero);
						if(!ss[3].equalsIgnoreCase("zero")){
							System.out.println("No such rank: "+ss[3]+" @"+ss[0]+" "+ss[2]);
						}
					}
				} else if((ss=s.split(" ")).length==6 && !s.contains("/")){// name sprache spitzname rangname x.y.z x.y.z
					Sprache.pls.put(ss[0], Used.byShortcut(ss[1]));
					nicknames.put(ss[0], ss[2]);
					if(ranks.containsKey(ss[3])){
						players.put(ss[0], ranks.get(ss[3]));
					} else {
						players.put(ss[0], zero);
						if(!ss[3].equalsIgnoreCase("zero")){
							System.out.println("No such rank: "+ss[3]+" @"+ss[0]+" "+ss[2]);
						}
					}
					if(!ss[4].equals("null"))
						SpawnBuilder.deathMapNormal.put(ss[0], ss[4]);
					if(!ss[5].equals("null"))
						SpawnBuilder.deathMapDream.put(ss[0], ss[5]);
				}
			}
			read.close();	
		} else {
			System.out.println("Couldn´t load rank.sec!");
		}
		
		f = new File(Plugin.instance.getDataFolder(), "data/pass.sec");
		if(f.exists()){
			BufferedReader read = new BufferedReader(new FileReader(f));
			for(String s = read.readLine();s!=null;s = read.readLine()){
				hasPassword.add(s);
			}
			read.close();
		}
	}
	
	public static void save(File folder) throws IOException{
		
		File rename = new File(folder, "/data/rank.sec");
		try {
			rename.renameTo(new File(folder, "/data/rank."+(System.currentTimeMillis()/7200000)+".sec"));// sollte sicher speichern :), alle 2h
		} catch(Exception e){
			e.printStackTrace();
		}
		
		File file = new File(folder, "data/rank.sec");
		
		FileWriter fw = new FileWriter(file);
		fw.write(Plugin.kingsname+"\n");
		fw.flush();
		for(Rank r:ranks.values()){
			fw.write("#"+r.label+" "+permissionsAsString(r.permissions)+"\n");
		}
		for(String name:nicknames.keySet()){
			if(nicknames.containsKey(name)){
				if(players.containsKey(name)){
					fw.write(name+" "+Sprache.getPls(name).shortcut+" "+nicknames.get(name)+" "+players.get(name).label+" "
				+(SpawnBuilder.deathMapNormal.containsKey(name)?SpawnBuilder.deathMapNormal.get(name):"null")+" "
				+(SpawnBuilder.deathMapDream .containsKey(name)?SpawnBuilder.deathMapDream .get(name):"null")+"\n");
				}
			} else System.out.println("nokey "+name);
		}
		fw.flush();
		fw.close();
		
		fw = new FileWriter(new File(folder, "data/pass.sec"));
		for(String s:hasPassword){
			fw.write(s+"\n");
		}
		fw.flush();
		fw.close();
	}
	
	public static String permissionsAsString(ArrayList<String> ps) {
		String ret = "";
		for(String s:ps){
			if(!s.equals(" "))
				ret+="-"+s;
		}
		return ret+"-";
	}

	ArrayList<String> permissions;String label;
	public Rank(String label, String... permission){
		this.label=label;
		permissions = new ArrayList<>();
		for(String s:permission){
			permissions.add(s);
		}
	}
	
	public static boolean hasPermisson(String name, String permission){
		if(Plugin.kingsname.equalsIgnoreCase(name)){
			return true;
		} else if(players.containsKey(name)){
			String[] key = permission.split("-");
			Rank r = players.get(name);
			if(key[0].equals("build")){
				int s;
				for(String p:r.permissions){
					if(p.equalsIgnoreCase(permission)){
						return true;
					} else if(p.startsWith("build ")){
						if(p.equals("build *")){
							return true;
						} else if((s=p.length())==permission.length()){
							for(int i=6;i<s;s++){
								if(!(permission.charAt(i)=='*' || permission.charAt(s)==p.charAt(s))){
									return false;
								}
							}
							return true;
						}
					}
				}
			} else {
				for(String p:r.permissions){
					if(p.equalsIgnoreCase(permission)){
						return true;
					}
				}
			}
		} else return false;
		return false;
	}
	
	static long amount, x;
	static boolean working;
	public static boolean kingPay(long thatamount){
		if(working){
			Bukkit.broadcastMessage(Bank.prefix+"We are still working at "+(Plugin.isQueen?"Queen":"King")+"s transactions. Please wait a moment!");
		} else {
			working = true;
			amount = thatamount;
			Player p = Bukkit.getPlayer(Plugin.kingsname);
			if(p!=null){
				int k = (int) (amount%81);
				if(Math.random()*81<k){
					amount = amount/81+1;
				} else amount = amount/81;
				if((x=Bank.getBilance("@king")/81)>=amount){
					if(Bank.substract("@king", amount*81)){
						amount = -amount;// soll ja abgezogen werden :)
						runThread();
						return true;
					} else {
						Bank.sendErrMessage(p, 0);
					}
				} else {
					p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
							"Your kingdom hasn´t god enought money! Raise some taxes, sell plots, open a shop or ask for donations!\nIf a donation is higher than your amount of money, the donator will be the new king!",
							"Dein Königreich hat nicht genug Geld! Erhöhe Steuern, verkaufe Grundstücke, eröffne einen Laden oder frage nach Spenden!\nWenn jemand mehr Geld überweist als zu hast, wird dieser Spieler der neue König!", null, null));
				}
			} else {
				Bukkit.broadcastMessage(Plugin.prefix+"Sb wanted to pay with "+(Plugin.isQueen?"Queen":"King")+"s money!");
			}
			working = false;// wird vom Hauptprozess, der geht, nie erreicht
		}
		return false;
	}
	
	public static void runThread(){
		if(amount>0){
			new Thread(new Runnable(){
				@Override public void run() {// füge das gegebene Geld dem Turm hinzu
					
					amount+=x;
					
					int k=0, a=1048576;
					World w = Plugin.world;
					
					f4:for(int h=116;h<928;h++){
						System.out.println(h);
						for(int i=-15;i<16;i++){
							for(int j=-15;j<16;j++){
								if(sq(i+0.3)+sq(j+0.1)<232){
									k++;
									//untersuche Block° k
									if(k>x){
										if(k>amount){
											break f4;
										} else {
											if(h<256){
												w.getBlockAt(a+i, h, j).setType(Material.GOLD_BLOCK);
											}
											if(h>=224 && h<480){
												w.getBlockAt(a+i, h-224, a+j).setType(Material.GOLD_BLOCK);
											}
											if(h>=448 && h<704){
												w.getBlockAt(i, h-448, a+j).setType(Material.GOLD_BLOCK);
											}
											if(h>=672 && h<928){
												w.getBlockAt(i, h-672, j).setType(Material.GOLD_BLOCK);
											}
											try {
												Thread.sleep(50);} catch (InterruptedException e) {}
										}
									}
								}
							}
						}
					}
					working = false;
				}
			}).start();
		} else {
			new Thread(new Runnable(){
				@Override public void run() {// entferne das Gold aus dem Turm
					
					x+=amount;
					amount=x-amount;
					
					int k=0, a=1048576;
					World w = Plugin.world;
					System.out.println("newtha");
					f4:for(int h=116;h<928;h++){
						System.out.println(h);
						for(int i=-15;i<16;i++){
							for(int j=-15;j<16;j++){
								if(sq(i+0.3)+sq(j+0.1)<232){
									k++;
									//untersuche Block° k
									if(k>x){
										if(k>amount){
											break f4;
										} else {
											if(h<256){
												w.getBlockAt(a+i, h, j).setType(Material.AIR);
											}
											if(h>=224 && h<480){
												w.getBlockAt(a+i, h-224, a+j).setType(Material.AIR);
											}
											if(h>=448 && h<704){
												w.getBlockAt(i, h-448, a+j).setType(Material.AIR);
											}
											if(h>=672 && h<928){
												w.getBlockAt(i, h-672, j).setType(Material.AIR);
											}
											try {
												Thread.sleep(50);} catch (InterruptedException e) {}
										}
									}
								}
							}
						}
					}
					working = false;
				}
			}).start();
		}
	}
	
	public static boolean kingGet(Player p, long thatamount) throws InterruptedException{
		if(thatamount%81!=0){
			p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
					"The "+(Plugin.isQueen?"Queen":"King")+" can only accept multiples of 81, because of his storage system.",
					(Plugin.isQueen?"Die Königin":"Der König")+" kann aufgrund seines Lagersystemes nur ganzzahlige Vielfache von 81 akzeptieren.", null, null));
			return false;
		} else if(Bank.getBilance(p)>=thatamount){
			if(!working){
				working = true;
				amount = thatamount;
				if((x=Bank.getBilance("@king"))<amount && !p.getName().equalsIgnoreCase(Plugin.kingsname)){
					if(Bank.substract(p.getName(), amount)){
						Bank.newKing(p.getName(), amount);
						amount -= x;
						amount /=81;
						x/=81;
						runThread();
						return true;
					} else {
						Bank.sendErrMessage(p, 0);
						return false;
					}
				} else {
					Bank.pay(p, "@king", amount);
					p.sendMessage(Bank.prefix+Sprache.select(p.getName(),
							"Transaction successful!",
							"Überweisung erfolgreich!", null, null));
					amount /= 81;
					x/=81;
					runThread();
					return true;
				}
			} else {
				p.sendMessage(Bank.prefix+Sprache.select(p.getName(), 
						"We are still working at "+(Plugin.isQueen?"Queen":"King")+"s transactions. Please wait a moment!",
						"Wir arbeiten noch an einer älteren Transaktion... Bitte gedulde dich noch einen Moment.", null, null));
				return false;
			}
		} else {
			Bank.sendErrMessage(p, 1);
			return false;
		}
	}
}
