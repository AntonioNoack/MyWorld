package me.corperateraider.recipes;

import java.util.ArrayList;

import me.corperateraider.generator.MathHelper;
import me.corperateraider.myworld.Plugin;
import net.minecraft.server.v1_7_R1.CraftingManager;
import net.minecraft.server.v1_7_R1.IRecipe;
import net.minecraft.server.v1_7_R1.RecipesFurnace;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RecipeManager extends MathHelper {
	
	public static void event(PrepareItemCraftEvent event){
		
		if(Plugin.beta || event.isRepair()) return;
		
		// Kontrolle, ob die ersten vier Zeichen des Namens mit den Rezepten �bereinstimmen :)
		ItemStack res = event.getRecipe().getResult();
		Material type = res.getType();
		String magic = (res.hasItemMeta()?res.getItemMeta().getDisplayName()+"xx":"xx").substring(0, 2);
		
		// gibt es ein Rezept, dass dem angewendetem entspricht?
		MyRecipe goodResult = null;
		for(MyRecipe rec:recipes){
			if(magic.equals(rec.magic()) && type == rec.result.getType()){// die Ergebnisse sind identisch
				//goodResult = rec;
				// pr�fe das Inventar...
				CraftingInventory inv = event.getInventory();
				Recipe r = inv.getRecipe();
				if((r instanceof ShapelessRecipe) ^ (rec instanceof SRecipe)){// unterschiedlicher Typ -> dieses Rezept ist es nicht
					continue;
				}
				// teste ob alle Inhaltsangaben erf�llt sind :)
				
				ArrayList<XMaterial> todo = new ArrayList<>();
				
				if(rec instanceof SRecipe || (rec instanceof XRecipe && rec.mats!=null)){// teste ob die Mengen erf�llt sind
					f:for(XMaterial add:rec.mats){
						if(add!=null){
							for(XMaterial m:todo){
								if(m.m==add.m && m.data==add.data){
									m.amount++;
									continue f;
								}
							}
							todo.add(add.clone());
						}
					}
				} else if(!(rec instanceof FRecipe)){// teste alle Felder ob erf�llt...
					// da es verschiede Feldergr��en gibt, wird es nicht ganz so leicht...
					// ou doch das wird es, wenn man einfach nicht auf die Anordnung sondern nur auf die IDs achtet... theoretisch ja m�glich, solange es da keine Konflikte gibt...
					for(XMaterial[] arr:rec.matz){
						f:for(XMaterial add:arr){
							if(add!=null){
								for(XMaterial m:todo){
									if(m.m==add.m && m.data==add.data){
										m.amount++;
										continue f;
									}
								}
								todo.add(add.clone());
							}
						}
					}
				} else {
					// naja irgendwie Ofenrezept...
					continue;
				}
				
				boolean ok = false, srec = rec instanceof SRecipe;
				ItemStack[] contents = inv.getContents().clone();
				
				is:for(int i=rec instanceof SRecipe?1:0;i<contents.length;i++){
					ItemStack is=contents[i];
					if(is==null || (is.getType()==res.getType() && rec.magic().equalsIgnoreCase(XRecipe.magic(is))) || is.getType()==Material.AIR) continue;
					XMaterial m = new XMaterial(is, is.hasItemMeta()?is.getItemMeta().getDisplayName():is.getType().name());
					ok = false;
					may:for(XMaterial may:todo){
						if(m.m==may.m){
							if(!may.useData || m.data==may.data){
								ok = true;
								if(srec){
									may.amount-=is.getAmount();
								} else {
									may.amount--;
								}
								if(may.amount <= 0){
									todo.remove(may);
								}
								break may;
							}
						}
					}
					if(!ok){
						break is;
					}
				}
				
				if(ok && todo.size()==0){// perfektes Rezept :)
					return;
				}
			}
		}
		if(goodResult==null){
			// kein Rezept dieser Art ist eingetragen -> demzufolge ist das Rezept illegal!
			// es gibt also entweder ein Rezept mit passenden Zutaten und anderem Ergebnis oder gar keines...
			((Player) event.getInventory().getHolder()).sendMessage("illegal recipe!");
			event.getInventory().setResult(null);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void event(InventoryClickEvent event) {
		// es wird in das Inventar geklickt... wird gecancelled, wenn der Spieler versucht im Amboss einen Spezialgegenstand umzubenennen
		// bzw vllt ist es auch m�glich, die Benennung richtig zu stellen :)
		if(event.getInventory() instanceof AnvilInventory){
			if(event.getRawSlot()==2){// Spieler nimmt Item heraus...
				ItemStack ori = event.getInventory().getItem(0);
				String name;
				if(ori.hasItemMeta() && (name=ori.getItemMeta().getDisplayName()).startsWith("�")){
					
					// ori bietet also die Grundlage... finde nun die n�tigen Daten zur Umbenennung...
					// der Name wird zum Prefix...
					if(name.charAt(2)=='�'){// wenn wir zus�tzlich eine Namensfarbe haben...
						name = name.substring(0, 4);
					} else {
						name = name.substring(0, 2);
					}
					
					event.getCurrentItem().setTypeId(0);
					
					ItemStack i = new XStack(name+event.getCurrentItem().getItemMeta().getDisplayName().substring(name.length()/2), ori.getTypeId(), ori.getData().getData()).i;
					
					Player p = ((Player)event.getView().getPlayer());
					p.getInventory().addItem(i);
					p.updateInventory();
					
					event.setCancelled(true);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void event(PlayerItemConsumeEvent event) {
		ItemStack i = event.getItem();
		int id=i.getTypeId(), dat=i.getData().getData();
		if(id==349 && dat==3){
			for(XEffect x:pufferfish){
				if(random()<x.chance){
					event.getPlayer().addPotionEffect(x.effect);
				}
			}
			return;
		}
		Player p = event.getPlayer();
		if(id==297){//Brot
			int add=0;
			switch(dat){
			case 0:break;//default
			case 1:add=2;break;// roher Fisch
			case 2:add=6;break;// gebratener Fisch
			case 3:add=3;break;// Clownfisch
			case 4:add=2;break;// Kugelfisch
			case 5:add=2;break;// Butter/K�sebrot
			case 6:add=4;break;// Gehacktesbr�tchen
			case 7:add=7;break;// Schnitzelbr�tchen :D
			case 8:add=2;break;// Kartoffelbr�tchen
			case 9:add=6;break;// Bartkartoffelbr�tchen
			case 10:add=-3;break;// Br�tchen mit verrottetem Fleisch 
			case 11:add=1;break;//Algen
			}
			p.setFoodLevel(min(20, p.getFoodLevel()+add));
			if(add>2)
				p.addPotionEffect(x(PotionEffectType.HEALTH_BOOST, add-2, 1));
		} else if(id==373){
			// 9 oder -119
			int plus=0;
			switch(i.getDurability()){
			case 8201+128*3:plus+=5;// Schwarzes Bier
			case 8201+128*2:plus+=4;// Dunkles Bier
			case 8201+128*1:// Bier
				p.setFoodLevel(min(20, p.getFoodLevel()+plus+3));
			}
		}
		for(MyRecipe r:recipes){
			if(r.result.isSimilar(i)){
				
				p.sendMessage(r.effects.length+"");
				for(XEffect x:r.effects){
					if(random()<x.chance){
						p.addPotionEffect(x.effect);
					}
				}
				
				return;
			}
		}
		p.sendMessage(id+":"+dat);
	}
	
	@SuppressWarnings("deprecation")// Item wurde in die Hand genommen...
	public static void event(PlayerItemHeldEvent event) {
		if(event.getPlayer().isSneaking()){
			ItemStack i = event.getPlayer().getItemInHand();
			event.getPlayer().sendMessage(i.getTypeId()+":"+i.getData().getData());
		}
	}
	
	public static ArrayList<MyRecipe> recipes = new ArrayList<>();
	static XEffect[] pufferfish;
	
	public static ItemStack telescopeBow;
	
	/**
	 * Initialisierung aller Rezepte :)
	 * */
	@SuppressWarnings("deprecation")
	public static void init(){
		
		for(Object o:CraftingManager.getInstance().getRecipes()){
			if(o instanceof IRecipe){
				recipes.add(XRecipe.fromIRecipe((IRecipe) o));
			} else {
				System.out.println("not instanceof IRecipe: "+o+" of "+o.getClass().getName());
			}
		}
		
		//if(RecipesFurnace.getInstance().recipes.size()>0)
		for(Object o:RecipesFurnace.getInstance().recipes.keySet()){
			recipes.add(new FRecipe(o, RecipesFurnace.getInstance().recipes.get(o)));
		}
		
		int old;
		String ret = " and loaded "+(old=recipes.size())+" old ones";
		
		// Conquest...
		// das wird so einiges... oje...
		// und die Bl�cke m�ssen in XBlock eingetragen werden... also am Besten eigentlich, wenn die zuerst initialisiert werden und man dann die Objekte direkt nimmt...
		// ein neuer Block muss aber den alten mind 1x enthalten, wenn es dieser keine SpezialIDs kennt, da man sonst cheaten k�nnte...
		
		recipes.add(new XRecipe(XBlock.BiomeChange.get(1), "000010000", x(Material.BLAZE_POWDER), x(Material.BLAZE_ROD)));
		
		// B
		recipes.add(new XRecipe(XBlock.Brunnen.get(1), "0000 00 0", x(Material.IRON_INGOT)));
		
		recipes.add(new XRecipe(XBlock.BücherBühne.get(1), s("000", "010"), x(Material.CARPET), x(Material.BOOKSHELF)));
		recipes.add(new XRecipe(XBlock.BücherEssensregal.get(1), "000123000", x(Material.WOOD), x(Material.COOKED_FISH), x(Material.POTATO_ITEM), x(Material.COOKED_BEEF)));
		recipes.add(new XRecipe(XBlock.BücherEssensregal.get(1), "000123000", x(Material.WOOD), x(Material.COOKED_FISH), x(Material.POTATO_ITEM), x(Material.GRILLED_PORK)));
		recipes.add(new XRecipe(XBlock.BücherEssensregal.get(1), "000123000", x(Material.WOOD), x(Material.COOKED_FISH), x(Material.POTATO_ITEM), x(Material.COOKED_CHICKEN)));
		recipes.add(new XRecipe(XBlock.BücherEssensregal.get(1), "000123000", x(Material.WOOD), x(Material.COOKED_FISH), x(Material.CARROT_ITEM), x(Material.COOKED_BEEF)));
		recipes.add(new XRecipe(XBlock.BücherEssensregal.get(1), "000123000", x(Material.WOOD), x(Material.COOKED_FISH), x(Material.CARROT_ITEM), x(Material.GRILLED_PORK)));
		recipes.add(new XRecipe(XBlock.BücherEssensregal.get(1), "000123000", x(Material.WOOD), x(Material.COOKED_FISH), x(Material.CARROT_ITEM), x(Material.COOKED_CHICKEN)));
		recipes.add(new XRecipe(XBlock.BücherEssensregal.get(1), "000 1 000", x(Material.WOOD), x(Material.GOLDEN_CARROT)));
		recipes.add(new XRecipe(XBlock.BücherFlaschen.get(1), "000111000", x(Material.WOOD), x(Material.POTION)));
		recipes.add(new XRecipe(XBlock.BücherPapierrollen.get(1), "000111000", x(Material.WOOD), x(Material.PAPER)));
		
		// C
		//recipes.add(new SRecipe(XBlock.Cobble2.get(1)));
		//recipes.add(new SRecipe(XBlock.CobbleEinbruch.get(1)));
		//recipes.add(new SRecipe(XBlock.CobbleGemauerteWand.get(1)));
		//recipes.add(new SRecipe(XBlock.CobbleGrob.get(1)));
		
		//recipes.add(new SRecipe(XBlock.CobbleSediment.get(1)));
		recipes.add(new SRecipe(XBlock.CobbleWeg1Mittelalter.get(1), x(Material.MOSSY_COBBLESTONE)));
		//recipes.add(new SRecipe(XBlock.CobbleWeg2Pflasterstein.get(1)));
		//recipes.add(new SRecipe(XBlock.CobbleWeg3dunkel.get(1)));
		
		//recipes.add(new SRecipe(XBlock.Cobblemoos2.get(1)));
		//recipes.add(new SRecipe(XBlock.Cobblemoos3.get(1)));
		//recipes.add(new SRecipe(XBlock.CobblemoosBricks.get(1)));
		
		recipes.add(new FRecipe(new XStack(98, 1).i, new XStack(48, 0).i));// lass mossige Cobbles in mossige Steinziegel brennen :)
		recipes.add(new SRecipe(new XStack(98, 1).i, x(Material.SMOOTH_BRICK), x(Material.VINE)));
		recipes.add(new SRecipe(new XStack(48, 0).i, x(Material.VINE), x(Material.COBBLESTONE)));
		
		recipes.add(new FRecipe(new XStack(98, 2).i, new XStack(98, 0).i));
		
		// D
		recipes.add(new XRecipe(XBlock.DiaerzFass.get(1), "0 0010000", x(Material.WOOD_STEP), x(Material.DIAMOND_ORE)));
		
		// E
		recipes.add(new SRecipe(XBlock.EndsteinSkelett1.get(1), x(9, Material.BONE)));
		
		recipes.add(new SRecipe(XBlock.EisNebel.get(9), x(Material.WEB, XBlock.NetzRauch.data)));
		recipes.add(new SRecipe(XBlock.NetzRauch.get(1), x(9, Material.ICE, XBlock.EisNebel.data)));
		recipes.add(new SRecipe(new XStack(1, null, Material.PACKED_ICE).i, x(9, Material.ICE, 0)));
		
		recipes.add(new XRecipe(XBlock.EisZiegelsteinwand.get(4), s("00","00"), x(Material.ICE, 0)));
		recipes.add(new SRecipe(new XStack(1, null, Material.ICE).i, x(1, Material.ICE, XBlock.EisZiegelsteinwand.data)));
		
		recipes.add(new XRecipe(XBlock.EisKristalle.get(9), "000010000", x(Material.ICE), x(Material.EMERALD)));
		recipes.add(new XRecipe(XBlock.EisPackeisSchmelzend.get(4), s("00","00"), x(Material.PACKED_ICE)));
		
		recipes.add(new XRecipe(XBlock.EisenerzKiste.get(1), "0 0010000", x(Material.WOOD), x(Material.IRON_ORE)));
		
		recipes.add(new XRecipe(XBlock.EisenAlsDreckVersteckt.get(1), "000010000", x(8, Material.DIRT), x(Material.IRON_BLOCK)));
		recipes.add(new SRecipe(XBlock.Eisen1x1.get(1), x(Material.IRON_BLOCK)));
		//recipes.add(new SRecipe(XBlock.EisenPanzer.get(1), x(Material.IRON_BLOCK), x(Material.BLAZE_POWDER)));
		recipes.add(new XRecipe(XBlock.EisenRostig.get(8), "000010000", x(Material.IRON_BLOCK), x(Material.WATER_BUCKET)));
		
		recipes.add(new XRecipe(XBlock.EisenzaunRostig.get(8), "000010000", x(Material.IRON_FENCE), x(Material.WATER_BUCKET)));
		recipes.add(new XRecipe(XBlock.EisenzaunDunkel.get(8), "000010000", x(Material.IRON_FENCE), x(Material.INK_SACK, 0)));
		
		recipes.add(new XRecipe(XBlock.EisenzaunStreifen.get(3), s("0","0","0"), x(Material.IRON_FENCE)));
		recipes.add(new XRecipe(XBlock.EisenzaunStreifen.get(3), s("000"), x(Material.IRON_FENCE)));
		recipes.add(new XRecipe(XBlock.EisenzaunKaro.get(4), s("00","00"),  x(Material.IRON_FENCE, XBlock.EisenzaunStreifen.data)));
		recipes.add(new XRecipe(XBlock.EisenzaunSicher.get(9), "000000000", x(Material.IRON_FENCE, XBlock.EisenzaunStreifen.data)));
		recipes.add(new XRecipe(XBlock.EisenzaunZahnräder.get(9), " 0 010 0 ", x(Material.IRON_SWORD), x(Material.IRON_FENCE)));// wenn sie damage machen teurer!
		
		recipes.add(new XRecipe(XBlock.ErdeAusgrabung.get(3), s("000","111"), x(Material.DIRT), x(Material.BONE)));
		recipes.add(new SRecipe(XBlock.ErdeKalt.get(8), x(Material.SNOW_BALL), x(8, Material.DIRT)));
		recipes.add(new SRecipe(XBlock.ErdeKalt.get(8), x(Material.SNOW_BLOCK), x(8, Material.DIRT)));
		recipes.add(new SRecipe(XBlock.ErdeSteinMoos1.get(9), x(Material.GRAVEL), x(8, Material.DIRT)));
		recipes.add(new SRecipe(XBlock.ErdeSteinMoos2.get(8), x(Material.GRAVEL), x(7, Material.DIRT), x(Material.VINE)));
		recipes.add(new SRecipe(XBlock.ErdeTrocken.get(8), x(8, Material.DIRT), x(Material.BLAZE_POWDER)));
		

		// G
		recipes.add(new XRecipe(XBlock.GlasBarriere.get(8), "000010000", x(Material.GLASS), x(Material.POTION, 8238)));
		recipes.add(new XRecipe(XBlock.GlasscheibeBarriere.get(8), "000010000", x(Material.THIN_GLASS), x(Material.POTION, 8238)));
		
		recipes.add(new XRecipe(XBlock.GolderzGoldSack.get(1), "000010000", x(Material.STRING), x(Material.GOLD_ORE)));
		recipes.add(new XRecipe(XBlock.GolderzKiste.get(1), "0 0010000", x(Material.WOOD), x(Material.GOLD_ORE)));
		
		// K
		recipes.add(new XRecipe(XBlock.KiesGrob.get(4), "000010000", x(Material.GRAVEL), x(Material.WEB, 0)));
		recipes.add(new XRecipe(XBlock.KiesGrobBewachsen.get(4), "000010000", x(Material.GRAVEL), x(Material.VINE)));
		recipes.add(new SRecipe(XBlock.KiesBewachsen.get(4), x(4, Material.GRAVEL), x(Material.VINE)));
		recipes.add(new SRecipe(XBlock.KiesSauber.get(8), x(9, Material.GRAVEL, 0)));
		
		// N
		recipes.add(new SRecipe(XBlock.NetzAufgehangenes.get(1), x(Material.STRING), x(Material.RAW_FISH)));
		recipes.add(new SRecipe(XBlock.NetzAufgehangenes.get(1), x(Material.STRING), x(Material.POTATO_ITEM)));
		recipes.add(new SRecipe(XBlock.NetzAufgehangenes.get(1), x(Material.STRING), x(Material.CARROT_ITEM)));
		recipes.add(new SRecipe(XBlock.NetzAufgehangenes.get(1), x(Material.STRING), x(Material.PORK)));
		recipes.add(new SRecipe(XBlock.NetzAufgehangenes.get(1), x(Material.STRING), x(Material.RAW_BEEF)));
		
		recipes.add(new XRecipe(XBlock.NetzEisenkette.get(3), s("0", "0", "0"), x(Material.IRON_FENCE)));
		
		recipes.add(new XRecipe(XBlock.NetzFrosch.get(1), "00 100222", x(Material.SLIME_BALL), x(Material.ENDER_PEARL), x(Material.GRASS)));
		recipes.add(new XRecipe(XBlock.NetzFrosch.get(1), " 00001222", x(Material.SLIME_BALL), x(Material.ENDER_PEARL), x(Material.GRASS)));
		recipes.add(new SRecipe(XBlock.NetzFlaschen.get(1), x(Material.GLASS_BOTTLE)));
		recipes.add(new SRecipe(XBlock.NetzFlaschen.get(1), x(Material.POTION, 0)));
		recipes.add(new XRecipe(XBlock.NetzFliegen.get(1), "000010000", x(Material.ROTTEN_FLESH), x(Material.WEB, 0).t()));
		
		recipes.add(new XRecipe(XBlock.NetzKranhacken.get(1), " 0 1 1222", x(Material.IRON_FENCE), x(Material.STRING), x(Material.WOOD_STEP)));
		recipes.add(new FRecipe(XBlock.NetzRauch.get(1), new XStack(1, null, Material.SULPHUR).i));
		recipes.add(new SRecipe(XBlock.NetzRauch.get(1), x(4, Material.SULPHUR)));
		recipes.add(new SRecipe(XBlock.NetzRauch.get(3), x(9, Material.SULPHUR)));
		recipes.add(new SRecipe(XBlock.NetzSchmetterlinge.get(8), x(8, Material.WEB, XBlock.NetzFliegen.data), x(Material.GOLD_INGOT, 1)));
		//recipes.add(new SRecipe(XBlock.NetzSeltsam.get(1)));
		recipes.add(new XRecipe(XBlock.NetzSpeere.get(1), s("0","1","1"), x(Material.IRON_SWORD), x(Material.STICK)));
		
		recipes.add(new XRecipe(XBlock.NetzSpeere.get(1), "0  101111", x(Material.IRON_INGOT), x(Material.STICK)));
		recipes.add(new XRecipe(XBlock.NetzSpeere.get(1), "  0101111", x(Material.IRON_INGOT), x(Material.STICK)));
		
		//recipes.add(new SRecipe(XBlock.NetzStrick.get(1))); !!! muss noch �berlegt werden, wie die verschiedenen Stricke unterschieden werden...
		recipes.add(new SRecipe(XBlock.NetzTierleder.get(1), x(Material.LEATHER)));// !!! lieber durch rechtsklick...
		
		recipes.add(new XRecipe(XBlock.NetzVogel.get(1), " 0 121 2 ", x(Material.ENDER_PEARL), x(Material.FEATHER), x(Material.STICK)));
		recipes.add(new SRecipe(XBlock.NetzWeinrebe.get(1), x(9, Material.VINE)));// !!! lieber nur in der Natur gefunden... und irgendwie muss es noch geupdatet werden...
		
		// O
		recipes.add(new XRecipe(XBlock.ObsidianFliesen.get(4), "0000", x(Material.OBSIDIAN, 0)));
		
		// P
		//recipes.add(new SRecipe(XBlock.PlankenAltEiche.get(1)));
		//recipes.add(new SRecipe(XBlock.PlankenAltFichte.get(1)));
		recipes.add(new SRecipe(XBlock.PlankenBühne.get(1), x(Material.WOOD, 1), x(Material.CARPET, 14)));
		//recipes.add(new SRecipe(XBlock.PlankenDjungel.get(1)));
		recipes.add(new XRecipe(XBlock.PlankenEssensFass.get(1), "000010000", x(Material.WOOD), x(Material.APPLE)));
		//recipes.add(new SRecipe(XBlock.PlankenFichtenBretter.get(1)));
		recipes.add(new XRecipe(XBlock.PlankenGartenHaus.get(1), "   000000", x(Material.LOG, 2)));
		recipes.add(new XRecipe(XBlock.PlankenGestrichen.get(8), "000010000", x(Material.WOOD), x(Material.INK_SACK)));
		recipes.add(new XRecipe(XBlock.PlankenHolzstapel.get(1), "000000000", x(Material.LOG, 0)));
		//recipes.add(new SRecipe(XBlock.PlankenKiste.get(1)));
		
		// S
		recipes.add(new XRecipe(XBlock.SandRot.get(8), "000010000", x(Material.SAND), x(Material.INK_SACK, 1)));
		recipes.add(new XRecipe(XBlock.SandFein.get(8), "000010000", x(Material.SAND), x(Material.STRING)));
		
		recipes.add(new SRecipe(XBlock.SandsteinNatur.get(1), x(1, Material.SANDSTONE, 0)));
		recipes.add(new XRecipe(XBlock.SandsteinRot.get(1), "0000", x(Material.SAND, 1)));
		recipes.add(new XRecipe(XBlock.SandsteinRotSäule.get(1), "0000", x(Material.SANDSTONE, XBlock.SandsteinRot.data)));
		
		recipes.add(new SRecipe(XBlock.SteinBeton.get(8), x(3, Material.SAND), x(3, Material.GRAVEL), x(2, Material.CLAY), x(Material.WATER_BUCKET)));
		recipes.add(new SRecipe(XBlock.SteinDunkel.get(8), x(8, Material.STONE), x(Material.INK_SACK, 0)));
		recipes.add(new XRecipe(XBlock.SteinFliesen.get(9), "000000000", x(Material.STONE)));
		//recipes.add(new SRecipe(XBlock.SteinGlitzer.get(1)));
		recipes.add(new SRecipe(XBlock.SteinHell.get(8), x(8, Material.STONE), x(Material.BLAZE_POWDER)));
		recipes.add(new SRecipe(XBlock.SteinMoos1.get(8), x(8, Material.STONE), x(Material.VINE)));
		recipes.add(new SRecipe(XBlock.SteinMoos2.get(6), x(6, Material.STONE), x(2, Material.VINE)));
		recipes.add(new XRecipe(XBlock.SteinPlatten.get(7), " 0000000 ", x(Material.STONE)));
		recipes.add(new XRecipe(XBlock.SteinPlatten.get(7), "00 000 00", x(Material.STONE)));
		recipes.add(new SRecipe(XBlock.SteinSchnee1.get(8), x(8, Material.STONE), x(Material.SNOW_BALL)));
		recipes.add(new SRecipe(XBlock.SteinSchnee2.get(4), x(4, Material.STONE), x(Material.SNOW_BALL)));
		recipes.add(new SRecipe(XBlock.SteinSchnee2.get(7), x(7, Material.STONE), x(2, Material.SNOW_BALL)));
		recipes.add(new SRecipe(XBlock.SteinWeg.get(9), x(8, Material.STONE), x(Material.SAND)));
		
		recipes.add(new XRecipe(XBlock.SteintrittSchachfeld.get(1), "010101010", x(Material.CARPET, 0), x(Material.CARPET, 15)));
		recipes.add(new XRecipe(XBlock.SteintrittSchachfeld.get(1), "101010101", x(Material.CARPET, 0), x(Material.CARPET, 15)));
		recipes.add(new SRecipe(XBlock.SteintrittSchulsachen.get(1), x(Material.PAPER)));
		recipes.add(new SRecipe(XBlock.SteintrittSpielkarten.get(1), x(Material.PAPER), x(Material.INK_SACK, 0), x(Material.INK_SACK, 1)));
		recipes.add(new SRecipe(XBlock.SteintrittSteinUndKaputt.get(1), x(1, Material.STONE_PLATE)));
		
		recipes.add(new XRecipe(XStack.dirt6Sl, s("000"), x(Material.DIRT)));
		recipes.add(new XRecipe(XStack.hay6Sl, s("000"), x(Material.HAY_BLOCK)));
		
		// Z
		recipes.add(new XRecipe(XBlock.ZaunOak.get(3),		"0000001 1", x(Material.STICK), x(Material.WOOD, 0)));
		recipes.add(new XRecipe(XBlock.ZaunSpruce.get(3),	"0000001 1", x(Material.STICK), x(Material.WOOD, 1)));

		recipes.add(new XRecipe(XBlock.ZaunOakLog.get(3),	"0000001 1", x(Material.STICK), x(Material.LOG, 0)));
		recipes.add(new XRecipe(XBlock.ZaunSpruceLog.get(3),"0000001 1", x(Material.STICK), x(Material.LOG, 1)));
		recipes.add(new XRecipe(XBlock.ZaunBirchLog.get(3),	"0000001 1", x(Material.STICK), x(Material.LOG, 2)));
		recipes.add(new XRecipe(XBlock.ZaunJungleLog.get(3),"0000001 1", x(Material.STICK), x(Material.LOG, 3)));
		recipes.add(new XRecipe(XBlock.ZaunAcaciaLog.get(3),"0000001 1", x(Material.STICK), x(Material.LOG_2, 0)));
		recipes.add(new XRecipe(XBlock.ZaunDarkLog.get(3),	"0000001 1", x(Material.STICK), x(Material.LOG_2, 1)));
		
		recipes.add(new XRecipe(XBlock.ZaunLightOak.get(2), "000000   ", x(Material.STICK)));
		recipes.add(new XRecipe(XBlock.ZaunLightOak.get(2), "   000000", x(Material.STICK)));
		
		recipes.add(new SRecipe(XBlock.ZiegelMoos.get(3), x(3, Material.BRICK), x(Material.VINE)));
		recipes.add(new SRecipe(XBlock.ZiegelMoos.get(6), x(6, Material.BRICK), x(Material.VINE)));
		recipes.add(new XRecipe(XBlock.ZiegelFachwerk.get(5), "010101010", x(Material.BRICK), x(Material.WOOD)));
		
		
		
		
		//!!! Wolle f�rben
		
		
		
		
		
		
		// weiteres...
		
		recipes.add(new XRecipe(new XStack(1, null, Material.SADDLE).i, "0000101 1", x(Material.LEATHER_CHESTPLATE), x(Material.IRON_INGOT)));
		recipes.add(new SRecipe(new XStack(1, null, Material.NAME_TAG).i, x(2, Material.BOOK_AND_QUILL), x(Material.LEASH)));
		recipes.add(new XRecipe(new ItemStack(30), "010101010", x(Material.STRING), x(Material.STICK)));
		recipes.add(new XRecipe(telescopeBow=new XStack("�bTelescope Bow", 261, 0, x(x(Enchantment.SILK_TOUCH, 1))).i, "010121010", x(Material.DIAMOND), x(Material.GLASS), x(Material.BOW)));
		recipes.add(new SRecipe(new XStack("�7Salt", 353, 1, x()).i, x(4, Material.WATER_BUCKET)));
	
		// Bakterienkulturen xD
		
		recipes.add(new XRecipe(new XStack("�4Lactic Acid Bacteria", 373, 160).i, "010020000", x(Material.ROTTEN_FLESH), x(Material.POTION), x(Material.MILK_BUCKET)));
		
		// K�se und Butterspa� ... ein paar Sachen k�nnen auch noch die Notwendigkeit, in Maschienen gemacht zu sein, bekommen :)
		
		recipes.add(new SRecipe(XBlock.GoldenButter.get(1), x(Material.MILK_BUCKET), x(Material.GOLD_NUGGET)));
		recipes.add(new XRecipe(XBlock.GoldenCheese.get(9), "000010000", x(Material.MILK_BUCKET), x(Material.POTION, 160)));
		
		//recipes.add(new XRecipe((butterblock=new XStack("�eGolden Butter Block", 41, 1)).i, "000000000", x(Material.GOLD_INGOT, 1)));
		//recipes.add(new XRecipe((cheeseblock=new XStack("�6Golden Cheese Block", 41, 2)).i, "000000000", x(Material.GOLD_INGOT, 2)));
		
		recipes.add(new XRecipe(new XStack("�eButter Sword", 283, 0, x(x(Enchantment.DAMAGE_ARTHROPODS, 1)), "�eSword of Butter? So you said.").i, s("0","0","1"), x(Material.GOLD_INGOT, 1), x(Material.STICK)));

		recipes.add(new XRecipe(new XStack("�eButter Spade", 284, 0, x(x(Enchantment.SILK_TOUCH, 1)), "�eSpecial feature �Secret").i, s("0","1","1"), x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		
		recipes.add(new XRecipe(new XStack("�eButter Pickaxe", 285, 0, x(x(Enchantment.DIG_SPEED, 1)), "�ePickaxe of Butter... let�s try").i, "000 1  1 ", x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		
		recipes.add(new XRecipe(new XStack("�eButter Axe", 286, 0, x(x(Enchantment.PROTECTION_EXPLOSIONS, 1)), "�bFrozen �3POWER!").i, "00 01  1 ", x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�eButter Axe", 286, 0, x(x(Enchantment.PROTECTION_EXPLOSIONS, 1)), "�bFrozen �3POWER!").i, " 00 10 1 ", x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�eButter Axe", 286, 0, x(x(Enchantment.PROTECTION_EXPLOSIONS, 1)), "�bFrozen �3POWER!").i, "00 10 1  ", x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�eButter Axe", 286, 0, x(x(Enchantment.PROTECTION_EXPLOSIONS, 1)), "�bFrozen �3POWER!").i, " 00 01  1", x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		
		recipes.add(new XRecipe(new XStack("�eButter Hoe", 287, 0, x(x(Enchantment.KNOCKBACK, 1)), "�eCrops and butter, 3x times, please!").i, "00 0   1 ", x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�eButter Hoe", 287, 0, x(x(Enchantment.KNOCKBACK, 1)), "�eCrops and butter, 3x times, please!").i, " 00  0 1 ", x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�eButter Hoe", 287, 0, x(x(Enchantment.KNOCKBACK, 2)), "�eCrops and butter, 5x times, please!").i, "00  0 1  ", x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�eButter Hoe", 287, 0, x(x(Enchantment.KNOCKBACK, 1)), "�eCrops and butter, 3x times, please!").i, " 00 0   1", x(Material.GOLD_INGOT, 1), x(Material.STICK)));
		
		recipes.add(new XRecipe(new XStack("�eButter Helmet", 314, 0, x(x(Enchantment.PROTECTION_PROJECTILE, 1)), "�eDid u evr c sth +credible?").i, "0000 0   ", x(Material.GOLD_INGOT, 1)));
		recipes.add(new XRecipe(new XStack("�eButter Helmet", 314, 0, x(x(Enchantment.PROTECTION_PROJECTILE, 1)), "�eDid u evr c sth +credible?").i, "   0000 0", x(Material.GOLD_INGOT, 1)));
		
		recipes.add(new XRecipe(new XStack("�eButter Chestplate", 315, 0, x(x(Enchantment.PROTECTION_FIRE, 1)), "�eThe real power?").i, "0 0000000", x(Material.GOLD_INGOT, 1)));
		
		recipes.add(new XRecipe(new XStack("�eButter Leggings", 316, 0, x(x(Enchantment.PROTECTION_EXPLOSIONS, 1)), "�eSmooth legs?").i, "0000 00 0", x(Material.GOLD_INGOT, 1)));
		
		recipes.add(new XRecipe(new XStack("�eButter Boots", 317, 0, x(x(Enchantment.PROTECTION_FALL, 1)), "�eButter Boots :)").i, "0 00 0   ", x(Material.GOLD_INGOT, 1)));
		recipes.add(new XRecipe(new XStack("�eButter Boots", 317, 0, x(x(Enchantment.PROTECTION_FALL, 1)), "�eButter Boots :D").i, "   0 00 0", x(Material.GOLD_INGOT, 1)));
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		recipes.add(new XRecipe(new XStack("�6Cheese Sword", 283, 0, x(x(Enchantment.DAMAGE_ALL, 1)), "�eHoles don�t always cut!").i, "0  0  1  ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Sword", 283, 0, x(x(Enchantment.DAMAGE_ALL, 1)), "�eHoles don�t always cut!").i, " 0  0  1 ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Sword", 283, 0, x(x(Enchantment.DAMAGE_ALL, 1)), "�eHoles don�t always cut!").i, "  0  0  1", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		
		recipes.add(new XRecipe(new XStack("�6Cheese Spade", 284, 0, x(x(Enchantment.DIG_SPEED, 1)), "�eDon�t try to eat ;)").i, "0  1  1  ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Spade", 284, 0, x(x(Enchantment.DIG_SPEED, 1)), "�eDon�t try to eat ;)").i, " 0  1  1 ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Spade", 284, 0, x(x(Enchantment.DIG_SPEED, 1)), "�eDon�t try to eat ;)").i, "  0  1  1", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		
		recipes.add(new XRecipe(new XStack("�6Cheese Pickaxe", 285, 0, x(x(Enchantment.DIG_SPEED, 3)), "�eNever seen...").i, "000 1  1 ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		
		recipes.add(new XRecipe(new XStack("�6Cheese Axe", 286, 0, x(x(Enchantment.WATER_WORKER, 1)), "�eCheese in water?").i, "00 01  1 ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Axe", 286, 0, x(x(Enchantment.DAMAGE_ALL, 1)), "�eDoes a hole hurt?").i, " 00 10 1 ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Axe", 286, 0, x(x(Enchantment.DAMAGE_UNDEAD, 1)), "�eBakteria VS. Undeads?").i, "00 10 1  ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Axe", 286, 0, x(x(Enchantment.FIRE_ASPECT, 1)), "�eCheese fondue!!!").i, " 00 01  1", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		
		recipes.add(new XRecipe(new XStack("�6Cheese Hoe", 287, 0, x(), "�eDid you know, there are fish rolls?").i, "00 0   1 ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Hoe", 287, 0, x(), "�eDid you know, you can create beer or alcopops?").i, " 00  0 1 ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Hoe", 287, 0, x(), "�eDid you know, coal can be pushed into dias?").i, "00  0 1  ", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		recipes.add(new XRecipe(new XStack("�6Cheese Hoe", 287, 0, x(), "�eDid you know, it is planned to add machines?").i, " 00 0   1", x(Material.GOLD_INGOT, 2), x(Material.STICK)));
		
		recipes.add(new XRecipe(new XStack("�6Cheese Helmet", 314, 0, x(x(Enchantment.WATER_WORKER, 1)), "�eProtection via holes? But you have air.").i, "0000 0   ", x(Material.GOLD_INGOT, 2)));
		recipes.add(new XRecipe(new XStack("�6Cheese Helmet", 314, 0, x(x(Enchantment.WATER_WORKER, 1)), "�c#Th�ringen! Bratw�rste 4 all :D").i, "   0000 0", x(Material.GOLD_INGOT, 2)));
		
		recipes.add(new XRecipe(new XStack("�6Cheese Chestplate", 315, 0, x(x(Enchantment.PROTECTION_FIRE, 1)), "�eCheese Fondue!!!").i, "0 0000000", x(Material.GOLD_INGOT, 2)));
		
		recipes.add(new XRecipe(new XStack("�6Cheese Leggings", 316, 0, x(x(Enchantment.PROTECTION_EXPLOSIONS, 1)), "�eCheese Shock!").i, "0000 00 0", x(Material.GOLD_INGOT, 2)));
		
		recipes.add(new XRecipe(new XStack("�6Cheese Boots", 317, 0, x(x(Enchantment.PROTECTION_FALL, 1)), "�eLove Cheese 4 Ur life ^^").i, "0 00 0   ", x(Material.GOLD_INGOT, 2)));
		recipes.add(new XRecipe(new XStack("�6Cheese Boots", 317, 0, x(x(Enchantment.PROTECTION_FALL, 2)), "�eLove Cheese 4 Our life ^^").i, "   0 00 0", x(Material.GOLD_INGOT, 2)));
		
		// Br�tchen
		
		recipes.add(new XRecipe(new XStack("�2Seawead Bread", 297, 11, x(x(Enchantment.FIRE_ASPECT, 2))).i, "000010000", x(Material.GRASS, 1), x(Material.BREAD)));
		
		recipes.add(new SRecipe(new XStack("�3Pickled Herring Sandwich", 297, 1, x(x(Enchantment.LUCK, 1))).i, x(Material.BREAD), x(Material.RAW_FISH)));
		recipes.add(new SRecipe(new XStack("�3Cooked Herring Sandwich", 297, 2, x(x(Enchantment.LUCK, 1), x(Enchantment.FIRE_ASPECT, 1))).i, x(Material.BREAD), x(Material.COOKED_FISH)));
		
		recipes.add(new SRecipe(new XStack("�cPickled Salmon Sandwich", 297, 1, x(x(Enchantment.LUCK, 1))).i, x(Material.BREAD), x(Material.RAW_FISH, 1)));
		recipes.add(new SRecipe(new XStack("�cCooked Salmon Sandwich", 297, 2, x(x(Enchantment.LUCK, 1), x(Enchantment.FIRE_ASPECT, 1))).i, x(Material.BREAD), x(Material.COOKED_FISH, 1)));
		
		recipes.add(new SRecipe(new XStack("�4Clowfish Sandwich", 297, 3, x(x(Enchantment.LUCK, 3)), "�dIt�s forbidden, so try don�t to let you catch!").i, x(Material.BREAD), x(Material.RAW_FISH, 2)));
		recipes.add(new SRecipe(new XStack("�4Pufferfish Sandwich", 297, 4, x(x(Enchantment.LUCK, 10)), "�dMay be dangerous - maybe deadly!").i, pufferfish = new XEffect[]{x(x(PotionEffectType.HARM, 1, 3), 0.1), x(x(PotionEffectType.HARM, 1, 2), 0.2), x(x(PotionEffectType.HARM, 1, 1), 0.3), x(x(PotionEffectType.WEAKNESS, 50, 3), 0.3),
			x(x(PotionEffectType.WEAKNESS, 50, 2), 0.2), x(x(PotionEffectType.WEAKNESS, 50, 1), 0.1), x(x(PotionEffectType.INCREASE_DAMAGE, 30, 3))}, x(Material.BREAD), x(Material.RAW_FISH, 3)));
		
		recipes.add(new XRecipe(new XStack(8, "�eButter Sandwich", 297, 5, x(x(Enchantment.FIRE_ASPECT, 1))).i, "000010000", x(Material.BREAD), x(Material.GOLD_INGOT, 1)));
		recipes.add(new XRecipe(new XStack(8, "�eCheese Sandwich", 297, 5, x(x(Enchantment.FIRE_ASPECT, 1))).i, "000010000", x(Material.BREAD), x(Material.GOLD_INGOT, 2)));
		
		recipes.add(new XRecipe(new XStack(6, "�cMinced Meat Sandwich C", 297, 6, x(x(Enchantment.FIRE_ASPECT, 1))).i, "000111000", x(Material.BREAD), x(Material.RAW_BEEF)));
		recipes.add(new XRecipe(new XStack(6, "�cMinced Meat Sandwich P", 297, 6, x(x(Enchantment.FIRE_ASPECT, 1))).i, "000111000", x(Material.BREAD), x(Material.PORK)));
		recipes.add(new XRecipe(new XStack(6, "�cMinced Meat Sandwich 2C/P", 297, 6, x(x(Enchantment.FIRE_ASPECT, 1))).i, "000121000", x(Material.BREAD), x(Material.RAW_BEEF), x(Material.PORK)));
		recipes.add(new XRecipe(new XStack(6, "�cMinced Meat Sandwich C/2P", 297, 6, x(x(Enchantment.FIRE_ASPECT, 1))).i, "000121000", x(Material.BREAD), x(Material.PORK), x(Material.RAW_BEEF)));
		
		recipes.add(new SRecipe(new XStack("�cSchnitzel Sandwich", 297, 7, x(x(Enchantment.FIRE_ASPECT, 2))).i, x(Material.BREAD), x(Material.GRILLED_PORK)));
		recipes.add(new SRecipe(new XStack("�cBeef Sandwich", 297, 7, x(x(Enchantment.FIRE_ASPECT, 2))).i, x(Material.BREAD), x(Material.COOKED_BEEF)));
		recipes.add(new SRecipe(new XStack("�cChicken Sandwich", 297, 7, x(x(Enchantment.FIRE_ASPECT, 2))).i, x(Material.BREAD), x(Material.COOKED_CHICKEN)));
		
		recipes.add(new SRecipe(new XStack("�2Potato Bread", 297, 8, x(x(Enchantment.FIRE_ASPECT, 1))).i, x(Material.BREAD), x(3, Material.POTATO_ITEM)));
		recipes.add(new SRecipe(new XStack("�2Baked Potato Bread", 297, 9, x(x(Enchantment.FIRE_ASPECT, 2))).i, x(Material.BREAD), x(3, Material.BAKED_POTATO)));
		
		recipes.add(new SRecipe(new XStack("�4Dangerous Sandwich", 297, 10, x()).i, new XEffect[]{x(x(PotionEffectType.WEAKNESS, 30, 1), 0.3), x(x(PotionEffectType.POISON, 30, 1), 0.2), x(x(PotionEffectType.HUNGER, 20, 1), 0.3)}, x(Material.BREAD), x(Material.ROTTEN_FLESH)));
		
		// Bier -> Schwindeleffect
		
		recipes.add(new XRecipe(new XStack(3, "�dBeer",			373, 8201+128*1).i, "000111222", new XEffect[]{x(x(PotionEffectType.CONFUSION, 100, 1)), x(x(PotionEffectType.POISON, 25, 1), 0.3)}, x(Material.SUGAR), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack(3, "�5Dark Beer",	373, 8201+128*2).i, "000111222", new XEffect[]{x(x(PotionEffectType.CONFUSION, 200, 1)), x(x(PotionEffectType.POISON, 35, 1), 0.4)}, x(Material.SUGAR), x(Material.POTION, 8201+128*1), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack(3, "�4Black Beer",	373, 8201+128*3).i, "000111222", new XEffect[]{x(x(PotionEffectType.CONFUSION, 300, 2)), x(x(PotionEffectType.POISON, 50, 1), 0.5)}, x(Material.SUGAR), x(Material.POTION, 8201+128*2), x(Material.WHEAT)));
		
		// Alkopops
		
		recipes.add(new XRecipe(new XStack("�0Alcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK, 0), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack("�4Alcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK, 1), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack("�2Alcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK, 2), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack("�1Alcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK, 4), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack("�5Alcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK, 5), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack("�9Alcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK, 6), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack("�aAlcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK,10), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack("�eAlcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK,11), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack("�bAlcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK,12), x(Material.POTION), x(Material.WHEAT)));
		recipes.add(new XRecipe(new XStack("�dAlcopop", 373, 8198+128).i, "010020030", x(Material.SUGAR), x(Material.INK_SACK,13), x(Material.POTION), x(Material.WHEAT)));
		
		// Whisky
		
		////////////////////////////////////////
		ShapedRecipe r;
		ShapelessRecipe s;
		// old als Start geht solange old die letzte gemessene Gr��e nach allen MCeigenen und nicht-Plugin Rezepten ist. Sonst 0, Sicherheit wird schon sicher gestellt.
		for(int index=old;index<recipes.size();index++){
			MyRecipe res=recipes.get(index);
			if(res.notNew) continue;
			if(res instanceof XRecipe){
				XRecipe rec = (XRecipe) res;
				if(rec instanceof SRecipe){
					// formlos
					s = new ShapelessRecipe(rec.result);
					for(XMaterial m:rec.mats){
						if(m.useData){
							if(m.amount==1)s.addIngredient(m.m, m.data);
							else s.addIngredient(m.amount, m.m, m.data);
						} else {
							if(m.amount==1)s.addIngredient(m.m);
							else s.addIngredient(m.amount, m.m);
						}
					}
					Bukkit.addRecipe(s);
				} else {
					// mit Form
					r = new ShapedRecipe(rec.result);
					r.shape(rec.rec);
				
					for(int i=rec.toUse.length-1;i>-1;i--){
						if(rec.toUse[i].useData){
							r.setIngredient((char) (i+'0'), rec.toUse[i].m, rec.toUse[i].data);
						} else {
							r.setIngredient((char) (i+'0'), rec.toUse[i].m);
						}
					}
					
					Bukkit.addRecipe(r);
				}
			} else {
				// Ofenrezept
				FRecipe rec = (FRecipe) res;
				if(rec.toUse[0].useData){
					Bukkit.addRecipe(new FurnaceRecipe(rec.result, rec.toUse[0].m, rec.toUse[0].data));
				} else {
					Bukkit.addRecipe(new FurnaceRecipe(rec.result, rec.toUse[0].m));
				}
			}
		}
		
		System.out.println("Registered "+(recipes.size()-old)+" new Recipes"+ret);
	}
	
	static String[] s(String... x){
		return x;
	}
	
	static XEffect x(PotionEffect x, double chance) {
		return new XEffect(x, chance);
	}

	static XEffect x(PotionEffect x) {
		return new XEffect(x);
	}

	static PotionEffect x(PotionEffectType type, int seconds, int grade) {
		return new PotionEffect(type, seconds*20, grade-1);
	}

	static XEnchantment x(Enchantment x, int level){
		return new XEnchantment(x, level);
	}
	
	static XEnchantment[] x(XEnchantment... enchs){
		return enchs;
	}
	
	static XMaterial x(int amount, Material m, int data){
		return new XMaterial(amount, m, data);
	}
	
	static XMaterial x(int amount, Material m){
		return new XMaterial(amount, m);
	}
	
	static XMaterial x(Material m){
		return new XMaterial(m);
	}
	
	static XMaterial x(Material m, int i){
		return new XMaterial(m, i);
	}

	@SuppressWarnings("deprecation")
	public static void event(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		
		if(p.getItemInHand().getTypeId()==261 && event.isSneaking()){
			if(p.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH) && p.getItemInHand().getEnchantments().get(Enchantment.SILK_TOUCH)==1){
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 10));
			}
		}
	}
	
	//@SuppressWarnings("deprecation")
		public static void event(CraftItemEvent event) {
			
			
			//ItemStack res =	event.getRecipe().getResult();
			//Bukkit.broadcastMessage("C.I.E. "+res.getTypeId()+" "+res.getData());
			
			/*String name = res.getItemMeta().getDisplayName();
			x:for(XBlock x:XBlock.registered){
				if(name.equalsIgnoreCase(x.name)){
					event.setCurrentItem(x.get(res.getAmount()));
					break x;
				}
			}*/
			
			/*Inventory i = event.getInventory();
			final Player p = Bukkit.getPlayer(i.getViewers().get(0).getName());
			if(true || !event.getClick().isCreativeAction()){
				if(event.getClick().isShiftClick()){// alle
					p.sendMessage("Shift!");
					if(event.getRecipe().getResult().getItemMeta().getDisplayName().endsWith("Cheese Roll")){
						ItemStack bread = null, sword = i.getItem(i.first(Material.IRON_SWORD));
						find:for(ItemStack is:i.getContents()){
							if(is.getTypeId()==297 && (!is.hasItemMeta() || is.getItemMeta().getDisplayName()!="�eCheese Roll")){
								bread = is;
								break find;
							}
						}
						int amount = bread.getAmount();
						
						p.sendMessage(261-sword.getDurability() + ":" + amount);
						if(261-sword.getDurability()>=amount){
							sword.setAmount(261-sword.getDurability()+1);
							sword.setDurability((short) (sword.getDurability()+amount));
						} else {
							p.sendMessage(261-sword.getAmount()+" : "+amount);
						}
						
						new Thread(new Runnable(){
							@Override public void run() {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {}
								p.updateInventory();
							}
						}).start();
						
					}
				} else {// eines
					p.sendMessage("no shift!");
					if(event.getRecipe().getResult().getItemMeta().getDisplayName().endsWith("Cheese Roll")){
						if( i.getItem(event.getInventory().first(Material.IRON_SWORD)).getDurability()<261){
							i.getItem(event.getInventory().first(Material.IRON_SWORD)).setAmount(2);
							i.getItem(event.getInventory().first(Material.IRON_SWORD)).setDurability((short) (event.getInventory().getItem(event.getInventory().first(Material.IRON_SWORD)).getDurability()+1));
						}
						
						new Thread(new Runnable(){
							@Override public void run() {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {}
								p.updateInventory();
							}
						}).start();
						
					}
				}
			}*/
		}
}
