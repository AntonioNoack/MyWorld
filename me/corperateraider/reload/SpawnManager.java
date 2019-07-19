package me.corperateraider.reload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import me.corperateraider.generator.DrehMatrix;
import me.corperateraider.generator.MathHelper;
import me.corperateraider.generator.Random;
import me.corperateraider.myworld.AntiLagg;
import me.corperateraider.myworld.BlockListener;
import me.corperateraider.myworld.Plugin;
import me.corperateraider.recipes.RecipeManager;
import me.corperateraider.recipes.XEnchantment;
import me.corperateraider.weather.Weather;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpawnManager extends MathHelper {
	
	static Type[]
			normal	= new Type[]{Type.Zomb, Type.Skel, Type.WSkel, Type.SP, Type.Creeper},
			mount	= new Type[]{Type.Zomb, Type.Skel, Type.WSkel, Type.SP, Type.Creeper, Type.Slime, Type.Silver, Type.GSP, Type.Witch},
			nether	= new Type[]{Type.Blaze, Type.Ghast, Type.MSlime, Type.NZomb, Type.WSkel, Type.Silver},
			end		= new Type[]{Type.Enderman},
			nutz 	= new Type[]{Type.Schaf, Type.Kuh, Type.Schwein, Type.Huhn, Type.Pferd};
	
	static boolean isInArr(EntityType e, Type[] arr){
		for(Type t:arr){
			if(t.t==e){
				if(e==EntityType.SKELETON){return false;}
				if(t.t!=EntityType.SKELETON || !t.wither)
					return true;
			}
		}
		return false;
	}
	
	static boolean isInLayer(int y, EntityType e){
		return isInArr(e, typearrByY(y));
	}
	
	static Type[] typearrByY(int y){
		if(y>4703){
			return normal;
		} else if(y>1300){
			return mount;
		} else if(y>240){
			return nether;
		} else {
			return end;
		}
	}
	
	static Type byLayer(int y){
		Type[] arr = typearrByY(y);
		return arr[MathHelper.random.nextInt(arr.length)];
	}
	static long lastTick;
	public static boolean spawnEntityRemoveOld(Entity old, Location l, int px, int py, int realy, int pz){
		
		int oben = 0, unten = 0, animal = 0;
		int minY = Plugin.world.getHighestBlockYAt(l)-9;
		
		for(Entity e:l.getChunk().getEntities()){
			if(e.getLocation().getBlockY()>minY){
				oben++;
			} else {
				unten++;
			}
			if(!AntiLagg.isHostile(e)){
				animal++;
			}
		}
		
		if(oben>20 || unten>20){
			BlockListener.controlAnimals(l.getChunk());
			return true;
		}
		
		if(!AntiLagg.isHostile(old) && animal>1){
			return true;
		}
		if(old.getLocation().getBlockY()>minY){
			if(oben>19){
				return true;
			}
		} else {
			if(unten>6){// 14 wären sonst *100 Chunks 1500
				return true;
			}
		}
		
		// wenn das Tier oberhalb der Grenze und freundlich ODER eine Fledermaus ist bleibt es erhalten
		if((!AntiLagg.isHostile(old) && realy>4703) || old.getType()==EntityType.BAT){
			
			if(isInArr(old.getType(), nutz)){
				Type type = nutz[MathHelper.random.nextInt(5)];
				if(type==Type.Pferd && old.getType()!=EntityType.HORSE && random.next()<0.7){
					type = nutz[MathHelper.random.nextInt(5)];
				}
				if(type==Type.Kuh && random()<0.01){
					type = Type.Pilzkuh;
				}
				if(type.t!=old.getType()){
					
					old = l.getWorld().spawnEntity(l, type.t);
					if(old instanceof Ageable && random()<0.1){
						((Ageable)old).setBaby();
					}
					if(type==Type.Pferd){
						Horse horse = (Horse) old;
						horse.setStyle(Style.values()[MathHelper.random.nextInt(Style.values().length)]);
						if(random()<0.2){
							horse.setVariant(random()<0.5?Variant.DONKEY:Variant.MULE);
						} else if(random()<0.01){
							horse.setVariant(random()<0.5?Variant.SKELETON_HORSE:Variant.UNDEAD_HORSE);
						} else horse.setVariant(Variant.HORSE);
					}
					
					return true;
				} else return false;
			} else return false;
		}
		
		// das Tier ist böse, also spawne ein neues um sicherzugehen, wenn der ausgewählte Typ nicht stimmt
		boolean remove = !isInLayer(realy, old.getType());
		Type type = byLayer(realy);
		if(realy>4703){
			if(random.next()<0.03 && Weather.isWinter()){
				type = Type.Snowman;
				remove = true;
			} else if(random.next()<0.01){
				type = Type.Villager;
				remove = true;
			}
		}
		
		if(remove){
			Entity e = old.getWorld().spawnEntity(l, type.t);
			if(type.t==EntityType.SKELETON && type.wither){
				((Skeleton)e).setSkeletonType(SkeletonType.WITHER);
			}
			old.remove();
			old = e;
		}
		
		Location trueloc = BlockListener.trueLocation(l).add(0, -4800, 0);
		
		// gebe dem Entity seine Spezialeigenschaften :)
		
		double level = getLevel(trueloc);
		
		if(isInArr(old.getType(), new Type[]{Type.Skel, Type.Zomb, Type.NZomb, Type.WSkel})){
			armor((LivingEntity) old, level, !type.wither);
		}
		
		Damageable d = (Damageable) old;
		
		makeHealthy(d, getLives(level, d.getHealth()));
		
		if(old instanceof LivingEntity){
			((LivingEntity) old).setRemoveWhenFarAway(true);
		}
		
		//if(health>40){
		//	sk.setCustomName(witherskelnames[(health-40)/45]);
		//}
		//sk.setRemoveWhenFarAway(true);
		
		//if(Math.random()<0.1){sk.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000, 1));}
		//if(Math.random()<0.1){sk.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, Math.random()<0.3?1:Math.random()<0.5?2:3));}
		//if(Math.random()<0.1){sk.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, Math.random()<0.3?1:Math.random()<0.5?2:3));}
		
		//static String[] witherskelnames = new String[]{"Night Keeper", "Hard Skeleton", "Deathly Skeleton", "Man of Nightwatch", "§cEvil Pandora", "§4Devils Son"};
		
		
		return remove;
	}
	
	/*
	 * Levelübersicht:
	 * Level	xLeben	Rüstung
	 * 0:		0.7		ohne
	 * 1:		1.0		Leder
	 * 2:		1.2		Leder verz
	 * 3:		1.5		Eisen
	 * 4:		2.0		Eisen verz
	 * 5:		2.3		Gold
	 * 6:		...		Gold verz
	 * 7:				Dia
	 * 8:				Dia verz
	 * 9:				imaginär ^^
	 * */
	
	static XEnchantment e(int max, double chance, Enchantment e){
		return new XEnchantment(e, max, chance);}
	static XEnchantment[][] ench = new XEnchantment[][]{{
		// Helm
		e(3, 0.1, Enchantment.DURABILITY),
		e(3, 0.01, Enchantment.OXYGEN),
		e(4, 0.1, Enchantment.PROTECTION_ENVIRONMENTAL),
		e(4, 0.1, Enchantment.PROTECTION_EXPLOSIONS),
		e(4, 0.1, Enchantment.PROTECTION_FIRE),
		e(4, 0.1, Enchantment.PROTECTION_PROJECTILE),
		e(3, 0.1, Enchantment.THORNS),
		e(3, 0.01, Enchantment.WATER_WORKER)
	},{// Brustpanzer
		e(3, 0.1, Enchantment.DURABILITY),
		e(4, 0.1, Enchantment.PROTECTION_ENVIRONMENTAL),
		e(4, 0.1, Enchantment.PROTECTION_EXPLOSIONS),
		e(4, 0.1, Enchantment.PROTECTION_FIRE),
		e(4, 0.1, Enchantment.PROTECTION_PROJECTILE),
		e(3, 0.1, Enchantment.THORNS)
	},{// Hose
		e(3, 0.1, Enchantment.DURABILITY),
		e(4, 0.1, Enchantment.PROTECTION_ENVIRONMENTAL),
		e(4, 0.1, Enchantment.PROTECTION_EXPLOSIONS),
		e(4, 0.1, Enchantment.PROTECTION_FIRE),
		e(4, 0.1, Enchantment.PROTECTION_PROJECTILE),
		e(3, 0.1, Enchantment.THORNS)
	},{// Schuhe
		e(3, 0.1, Enchantment.DURABILITY),
		e(4, 0.1, Enchantment.PROTECTION_ENVIRONMENTAL),
		e(4, 0.1, Enchantment.PROTECTION_EXPLOSIONS),
		e(4, 0.1, Enchantment.PROTECTION_FALL),
		e(4, 0.1, Enchantment.PROTECTION_FIRE),
		e(4, 0.1, Enchantment.PROTECTION_PROJECTILE),
		e(3, 0.1, Enchantment.THORNS)
	},{// Waffe: Schwert
		e(3, 0.1, Enchantment.DURABILITY),
		e(5, 0.1, Enchantment.DAMAGE_ALL),
		e(5, 0.1, Enchantment.DAMAGE_ARTHROPODS),
		e(5, 0.1, Enchantment.DAMAGE_UNDEAD),
		e(2, 0.01, Enchantment.FIRE_ASPECT),
		e(2, 0.1, Enchantment.KNOCKBACK),
		e(3, 0.01, Enchantment.LOOT_BONUS_MOBS)
	},{// Waffe: Axt/Spitzhacke/Schaufel
		e(3, 0.1, Enchantment.DURABILITY),
		e(3, 0.01, Enchantment.DIG_SPEED),
		e(5, 0.1, Enchantment.DAMAGE_ALL),
		e(5, 0.1, Enchantment.DAMAGE_ARTHROPODS),
		e(5, 0.1, Enchantment.DAMAGE_UNDEAD),
		e(2, 0.1, Enchantment.KNOCKBACK),
		e(3, 0.01, Enchantment.LOOT_BONUS_BLOCKS),
		e(3, 0.01, Enchantment.SILK_TOUCH),
	},{// Bogen
		e(3, 0.1, Enchantment.DURABILITY),
		e(5, 0.1, Enchantment.ARROW_DAMAGE),
		e(1, 0.01, Enchantment.ARROW_FIRE),
		e(1, 0.002, Enchantment.ARROW_INFINITE),
		e(2, 0.1, Enchantment.ARROW_KNOCKBACK)
	}};
	
	/**
	 * IDs:
	 * 0 = Helm
	 * 1 = Brustpanzer
	 * 2 = Hose
	 * 3 = Schuhe
	 * 4 = Waffe (oben ist es 4=Schwert, 5=Werkzeug, 6=Bogen!)
	 * 5 = Bogen
	 * */
	@SuppressWarnings("deprecation")
	static ItemStack getArmorpart(double lev, int id){
		int level = min(max((int)(lev+random()*2-1), 8), 0);
		if(level == 0) return null;
		ItemStack r;
		if(id<4){
			switch((level-1)/2){
			case 0:// Leder
				r = new ItemStack(id+298, 1);
				
				LeatherArmorMeta am = (LeatherArmorMeta) r.getItemMeta();
				am.setColor(Color.fromRGB((int) (255-min(32*lev, 255)), 0, 0));
				r.setItemMeta(am);
				
				break;
			case 1:// Eisen
				r = new ItemStack(id+306, 1);
				break;
			case 2:// Gold
				r = new ItemStack(id+314, 1);
				break;
			case 3:// Dia
				r = new ItemStack(id+310, 1);
				break;
			default:
				return null;
			}
			if(level>1 && level%2==0){// verzaubere :)
				for(XEnchantment x:ench[id]){
					if(random()<x.chance){
						r.addUnsafeEnchantment(x.ench, (int) (sq(random())*x.level)+1);
					}
				}
			}
			return r;
		} else {
			if(id==5){
				// unten in der Hölle dürfte es ja stärkere Pfeilattacken geben...
				// also... Verzauberungschance steigt mit Level :)
				r = new ItemStack(261, 1);
				for(XEnchantment x:ench[6]){
					if(random()<x.chance*lev*0.125){
						r.addUnsafeEnchantment(x.ench, (int) (sq(random())*x.level)+1);
					}
				}
			} else {
				r = new ItemStack(id4Waffe[(level-1)/2][id=(int)(sq(random())*4)]);
				for(XEnchantment x:ench[id==0?4:5]){
					if(random()<x.chance*lev*0.125){
						r.addUnsafeEnchantment(x.ench, (int) (sq(random())*x.level)+1);
					}
				}
			}
			return null;
		}
	}
	
	static final int[][] id4Waffe = new int[][]{
		{272, 273, 274, 275},
		{267, 258, 257, 256},
		{283, 286, 285, 284},
		{276, 279, 278, 277},
	};
	
	static int getLives(double level, double before){
		return max((int) (Math.pow(1.25, level)*before), 1);
	}
	
	static double getLevel(Location l){
		return min(l.length()*0.0004+3*MathHelper.random(), 8);
	}
	
	static void armor(LivingEntity e, double level, boolean likesBow){
		
		EntityEquipment ee = e.getEquipment();
		
		ee.setArmorContents(new ItemStack[]{
			getArmorpart(level, 0),
			getArmorpart(level, 1),
			getArmorpart(level, 2),
			getArmorpart(level, 3),
			getArmorpart(level, (likesBow?random()<0.95:random()>0.95)?4:5)
		});
		
		if(ee.getBoots()!=null)ee.setBootsDropChance(0.01f);
		if(ee.getLeggings()!=null)ee.setLeggingsDropChance(0.01f);
		if(ee.getChestplate()!=null)ee.setChestplateDropChance(0.01f);
		if(ee.getHelmet()!=null)ee.setHelmetDropChance(0.01f);
		if(ee.getItemInHand()!=null)ee.setLeggingsDropChance(0.01f);
	}
	
	static enum Type {
		WSkel(1, true), Skel(0.3, false), Zomb(0.3, EntityType.ZOMBIE), NZomb(0.4, EntityType.PIG_ZOMBIE), SP(0.2, EntityType.SPIDER), GSP(0.2, EntityType.CAVE_SPIDER),
		Blaze(0.3, EntityType.BLAZE), Ghast(0.15, EntityType.GHAST), Silver(0.1, EntityType.SILVERFISH), Enderman(0.5, EntityType.ENDERMAN), Witch(0.2, EntityType.WITCH),
		Slime(0.1, EntityType.SLIME), MSlime(0.2, EntityType.MAGMA_CUBE), Creeper(0.1, EntityType.CREEPER), Snowman(1, EntityType.SNOWMAN), Villager(1, EntityType.VILLAGER),
		Schaf(1, EntityType.SHEEP), Kuh(1, EntityType.COW), Huhn(1, EntityType.CHICKEN), Schwein(1, EntityType.PIG), Pferd(1, EntityType.HORSE), Pilzkuh(1, EntityType.MUSHROOM_COW);
		
		/**
		 * veraltet...
		 * */
		double life;
		/**
		 * Prefers the sword?
		 * */
		boolean wither = true;
		/**
		 * Der eigentliche Typ
		 * */
		EntityType t;
		
		Type(double multiLife, EntityType t){
			life = multiLife;
			this.t=t;
		}
		
		Type(double multiLife, boolean wither){
			life = multiLife;
			t = EntityType.SKELETON;
			this.wither = wither;
		}
	}
	
	public boolean oldEntityget(Entity e, Location l, int px, int py, int realy, int pz){
		if(e.hasMetadata("x")){
			return true;
		}
		
		EntityType type;
		if(l.getWorld().getChunkAt(l.getWorld().getBlockAt(l)).getEntities().length<50){//max spawn rate :) - naja ^^
			if(l.getWorld().getBlockAt(px,py,pz).getLightLevel()>MathHelper.random()){// nette Mobs
				e = l.getWorld().spawnEntity(l, type=getNiceType(px,realy,pz));
				e.setMetadata("x", new org.bukkit.metadata.FixedMetadataValue(Plugin.instance, 0));
				switch(type){
				case BAT:
					break;
				case CHICKEN:
					break;
				case COW:
					break;
				case HORSE:
					break;
				case IRON_GOLEM:
					break;
				case MINECART:
					break;
				case MUSHROOM_COW:
					break;
				case OCELOT:
					break;
				case PIG:
					break;
				case SHEEP:
					break;
				case SNOWMAN:
					break;
				case SQUID://tritt nicht auf, da Tintenfische einfach in Ruhe gelassen werden :)
					break;
				case VILLAGER:
					break;
				case WOLF:
					break;
				default:break;
				}
			} else {// Monster
				e = l.getWorld().spawnEntity(l, type=getBadType(px,realy,pz));
				e.setMetadata("x", new org.bukkit.metadata.FixedMetadataValue(Plugin.instance, 0));
				
				double level = 0;
				
				switch(type){
				case BLAZE:
					break;
				case CAVE_SPIDER:
					break;
				case CREEPER:
					break;
				case ENDERMAN:
					break;
				case ENDER_DRAGON:
					break;
				case GHAST:
					break;
				case GIANT:
					break;
				case HORSE:// Wenn Reiter gehen ja :)
					break;
				case MAGMA_CUBE:
					break;
				case MINECART_MOB_SPAWNER:
					break;
				case PIG_ZOMBIE:// vorallem Goldrüstung, vllt sogar Käserüstung :D
					
					break;
				case PRIMED_TNT:// ?
					break;
				case SILVERFISH:
					break;
				case SKELETON:
					break;
				case SLIME:
					break;
				case SPIDER:
					break;
				case WITCH:
					Witch wi = (Witch) e;
					makeHealthy(wi, 1.0*MathHelper.random.nextInt(20)+20);
					wi.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN, 1));
					break;
				case WITHER:
					break;
				case WOLF:// Böse :)
					Wolf wo = (Wolf) e;
					wo.setRemoveWhenFarAway(true);
					break;
				case ZOMBIE:
					Zombie z = (Zombie) e;
					if(is(0.01)){
						z.setVillager(true);
					}
					if(is(0.03)){
						z.setBaby(true);
						makeHealthy(z, 1.0*MathHelper.random.nextInt(20)+20);
					} else {
						z.getEquipment().setBoots(boots(level+next()*2));
						z.getEquipment().setChestplate(chestplate(level+next()*2));
						z.getEquipment().setHelmet(helmet(level+next()*2));
						z.getEquipment().setLeggings(leggings(level+next()*2));
						z.getEquipment().setItemInHand(item(level+next()*2));
					}
					
					break;
				default:break;
				}
			}
		}
		
		return false;
	}
	
	private static void makeHealthy(Damageable e, double h){
		e.setMaxHealth(h);
		e.setHealth(h);
	}
	
	/**
	 * l = Level :)
	 * 0-1 = nix
	 * 1-2 = Holz
	 * 2-3 = Stein
	 * 3-4 = Eisen
	 * 4-5 = Gold
	 * 5-6 = Diamant
	 * */
	private ItemStack item(double l) {
		return null;
	}

	private ItemStack boots(double l){
		return null;
	}
	
	private ItemStack chestplate(double l){
		return null;
	}
	
	private ItemStack leggings(double l){
		return null;
	}
	
	private ItemStack helmet(double l){
		return null;
	}

	private EntityType getNiceType(int x, int realy, int z) {
		return EntityType.SHEEP;
	}
	
	private EntityType getBadType(int x, int realy, int z){
		return EntityType.WITCH;
	}
	
	private double next(){
		return MathHelper.random();
	}
	
	private boolean is(double max){
		return MathHelper.random()<max;
	}

	public static void kill(LivingEntity e) {
		if(e.getType()==EntityType.ENDER_DRAGON){
			
			double d = ((Damageable)e).getMaxHealth();
			Iterator<PotionEffect> it = e.getActivePotionEffects().iterator();
			if(it.hasNext()){
				PotionEffect p = it.next();
				if(p.getType()==PotionEffectType.INCREASE_DAMAGE){
					d*=1.3+0.3*p.getAmplifier();
				}
			}
			d*=0.02;// max Leben sind 2000... mit Amplifier 3800...
			e.getWorld().dropItemNaturally(e.getLocation(), new ItemStack(Material.DIAMOND, max(10, min(64, random.round(d)))));
			e.getWorld().dropItemNaturally(e.getLocation(), new ItemStack(Material.QUARTZ_BLOCK, random.nextInt(64)+1));
			e.getWorld().dropItemNaturally(e.getLocation(), new ItemStack(Material.OBSIDIAN, random.nextInt(64)+1));
			e.getWorld().dropItemNaturally(e.getLocation(), new ItemStack(Material.NETHER_BRICK, random.nextInt(64)+1));
			e.getWorld().dropItemNaturally(e.getLocation(), new ItemStack(Material.NETHERRACK, random.nextInt(64)+1));
			if(random.next()<0.5){
				e.getWorld().dropItemNaturally(e.getLocation(), RecipeManager.telescopeBow);
			}
		}
	}
	
	static final String dragonpref = "§4[>Dragon<] §c";
	static int[][] dragonS, dragonM, dragonL;
	public static void ini(File data) throws IOException {
		makeDragonMap(0, new File(data, "SDrache.png"));
		makeDragonMap(1, new File(data, "MDrache.png"));
		makeDragonMap(2, new File(data, "LDrache.png"));
	}
	
	static void makeDragonMap(int index, File file) throws IOException {
		BufferedImage img = ImageIO.read(file);
		int[][] map = null;
		switch(index){
		case 0:map=dragonS=new int[img.getWidth()][img.getHeight()];break;
		case 1:map=dragonM=new int[img.getWidth()][img.getHeight()];break;
		case 2:map=dragonL=new int[img.getWidth()][img.getHeight()];break;
		}
		for(int w=img.getWidth()-1;w>=0;w--){
			for(int h=img.getHeight()-1;h>=0;h--){
				switch(img.getRGB(w, h)&0xffffff){
				case 0x00ffff:map[w][h] =  57;break;
				case 0xffc900:map[w][h] =  41;break;
				case 0x990000:map[w][h] =  87;break;
				case 0x470000:map[w][h] = 112;break;
				case 0x220429:map[w][h] =  49;break;
				case 0xffffff:map[w][h] = 155;break;
				case 0x0019d2:map[w][h] = 121;break;
				default:map[w][h]=0;
				}
			}
		}
	}
	
	static long dragonspawntime = 0;
	
	/**
	 * Der Spieler hat einen Netherstern gedroppt und auf den Endsteinblock, das Herz des Drachen, geklickt
	 * <br>Der Netherstern liegt dabei auf dem Endsteinblock oder leicht daneben
	 * */
	@SuppressWarnings("deprecation")
	public static void checkDragon(World w, Player p, int x, int y, int z){
		if(dragonspawntime > System.currentTimeMillis())
			return;
		
		Location ok=null;
		entities:for(Entity e:p.getNearbyEntities(8, 8, 8)){
			if(e instanceof Item){
				ItemStack i = ((Item)e).getItemStack();
				if(i.getTypeId() == 399){
					if(i.getAmount()==1){
						e.remove();
					} else {
						i.setAmount(i.getAmount()-1);
					}
					ok = e.getLocation();
					break entities;
				}
			}
		}
		if(ok!=null){
			ok = new Location(ok.getWorld(), x, y, z);
			// in welche Richtung guckt der Drache?
			// eindeutige Merkmale sind der Schwanz und die Schwanzspitze
			
			// unsere DrehMatrix :D
			DrehMatrix m = null;
			boolean found = false;
			// auf dem Boden... gedreht um n*90°
			xyz:for(int a=0;a<4;a++){
				for(int b=0;b<4;b++){
					if((m=new DrehMatrix().rotZ(a*Tf4).rotY(b*Tf4)).add(ok, -1, 0, 9).getBlock().getType()==Material.NETHER_BRICK){
						found = true;
						break xyz;
					}
				}
				if((m=new DrehMatrix().rotZ(a*Tf4).rotX(Tf4)).add(ok, -1, 0, 9).getBlock().getType()==Material.NETHER_BRICK){
					found = true;
					break xyz;
				}
				if((m=new DrehMatrix().rotZ(a*Tf4).rotX(3*Tf4)).add(ok, -1, 0, 9).getBlock().getType()==Material.NETHER_BRICK){
					found = true;
					break xyz;
				}
			}
			if(found){
				// entscheide, um welchen Drachen es sich handelt :)
				// m.show(p);
				int[][] that;String name;
				int px, pz;// die Position des Obsidianblocks :)
				if(m.add(ok, 0, 0, -3).getBlock().getType()==Material.DIAMOND_BLOCK){
					// kleiner Drache
					that = dragonS;
					name = "small";
					px = 7;pz = 3;
				} else if(m.add(ok, -1, 0, 3).getBlock().getType()==Material.NETHER_BRICK){
					// großer Drache
					that = dragonL;
					name = "big";
					px = 11;pz = 4;
				} else {
					// mittlerer Drache
					that = dragonM;
					name = "middle";
					px = 9;pz = 4;
				}
				
				if(that == null){
					p.sendMessage(dragonpref+"ERROR: no image loaded. Please report the owner, Miner952x");
					dragonspawntime = System.currentTimeMillis()+20000;
					return;
				}
				
				int id, mx=that.length, mz=that[0].length;
				testdragon:for(int a=0;a<mx;a++){// width = x
					for(int b=0;b<mz;b++){// height = z
						if((id=that[a][b])!=-1){
							if(m.add(ok, a-px, 0, b-pz).getBlock().getTypeId()!=id){
								m = null;
								Bukkit.broadcastMessage(a+" "+b+" "+id);
								break testdragon;
							}
						}
					}
				}
				
				if(m!=null){
					
					for(int a=0;a<mx;a++){// width = x
						for(int b=0;b<mz;b++){// height = z
							if((id=that[a][b])>0){
								m.add(ok, a-px, 0, b-pz).getBlock().setType(Material.COAL_BLOCK);
							}
						}
					}
					
					p.sendMessage(dragonpref+"Congratulations :D. A dragon of the size "+name.toUpperCase()+" can be created!");
					// spawne drumrum ruhig später noch Enderkristalle :D
					
					EnderDragon e = (EnderDragon)w.spawnEntity(ok.add(0, 2, 0), EntityType.ENDER_DRAGON);
					
					switch(name){
					case "small":
						makeHealthy(e, random.nextInt(200)+100);
						if(random()<0.333){
							e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1));
							e.setCustomName("Dangerous Ender Dragon");
						} else {
							e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
						}
						break;
					case "middle":
						makeHealthy(e, random.nextInt(500)+500);
						if(random()<0.333){
							e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 2));
							e.setCustomName("Hard Ender Dragon");
						} else if(random()<0.5){
							e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1));
							e.setCustomName("Dangerous Ender Dragon");
						} else {
							e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
						}
						break;
					case "big":
						makeHealthy(e, random.nextInt(1000)+1000);
						if(random()<0.333){
							e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 3));
							e.setCustomName("Majestic Ender Dragon");
						} else if(random()<0.5){
							e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 2));
							e.setCustomName("Hard Ender Dragon");
						} else {
							e.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1));
							e.setCustomName("Dangerous Ender Dragon");
						}
						
						break;
					}
				} else {
					p.sendMessage(dragonpref+"You are missing sth...");
					w.dropItemNaturally(ok, new ItemStack(Material.NETHER_STAR));
				}
			} else {
				p.sendMessage(dragonpref+"You need more than a hearth!");
				w.dropItemNaturally(ok, new ItemStack(Material.NETHER_STAR));
			}
		} else {
			// mache nichts, da es viel zu häufig passiert, dass man im Ende auf Endstone klickt
			//p.sendMessage(dragonpref+"You should try to use a Netherstar as energy source!");
		}
		
		dragonspawntime = System.currentTimeMillis()+5000;
	}
	
	public static String getPassword(String name){
		Random r = new Random(name.hashCode());
		return (char)(65+r.nextInt(26))+""+r.nextInt(1000);
	}
}
