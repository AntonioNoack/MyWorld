package me.corperateraider.recipes;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import converter.MetaString;
import me.corperateraider.generator.MathHelper;
import me.corperateraider.myworld.BlockListener;
import me.corperateraider.myworld.Plugin;

public abstract class XBlock {
	static XMakeTexPack onlydeco = new XMakeTexPack();
	static ArrayList<XBlock> registered = new ArrayList<>();
	
	@SuppressWarnings("deprecation")
	public static XBlock GoldMünzen,
	Stein0// besondere Drops f�r die Steine?
		= new XBlockDeco(true, "�7Stone", null, 1, 0, XStack.cobble){
		@Override public boolean onRightClick(Player p, ItemStack inHand, Block b){
			
			if(!BlockListener.blockBreakIsOK(p, b))
				return true;
			
			if(inHand.getType()==Material.SNOW_BALL){
				// subtrahiere einen Schneeball :)
				b.setData((byte) 3);
				return true;
			} else if(inHand.getType()==Material.VINE){
				// subrahiere eine Liane
				b.setData((byte) 1);
				return true;
			}
			return false;
		}
	},
	SteinMoos1
		= new XBlockDeco(true, "�aMossy Stone", null, 1, 1, q(XStack.cobble, 1.0), q(XStack.lianen, 0.111)),
	SteinMoos2
		= new XBlockDeco(true, "�2Very Mossy Stone", null, 1, 2, q(XStack.cobble, 1.0), q(XStack.lianen, 0.333)),
	SteinSchnee1
		= new XBlockDeco(true, "�7Snowy Stone", null, 1, 3, q(XStack.cobble, 1.0), q(XStack.schneeball, 0.111)),
	SteinSchnee2
		= new XBlockDeco(true, "Very Snowy Stone", null, 1, 4, q(XStack.cobble, 1.0), q(XStack.schneeball, 0.333)),
	SteinDunkel
		= new XBlockDeco(true, "�8Dark Stone", null, 1, 5, XStack.cobble),
	SteinHell
		= new XBlockDeco(true, "�7Light Stone", null, 1, 6, XStack.cobble),
	SteinFliesen
		= new XBlockDeco("Stone Squares", null, 1, 7),
	SteinWeg
		= new XBlockDeco("Flagstones", null, 1, 8),
	SteinBeton
		= new XBlockDeco("Concrete", null, 1, 9),
	SteinGlitzer
		= new XBlockDeco("�8Mineral Rock", null, 1, 10),
	SteinPlatten
		= new XBlockDeco("�7Big Flagstones", null, 1, 11),
	GrasRau
		= new XBlockDeco("Gras II", null, 2, 1){
		public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			return new ItemStack[]{inHand.getEnchantmentLevel(Enchantment.SILK_TOUCH)>0?XStack.gras:XStack.erde};
		}
	},
	ErdeAusgrabung
		= new XBlockDeco("Excavation", null, 3, 4),
	ErdeKalt
		= new XBlockDeco("�7Snowy Dirt", null, 3, 5, q(XStack.erde, 1.0), q(XStack.schneeball, 0.1)),
	ErdeSteinMoos1
		= new XBlockDeco("Stony Dirt", null, 3, 6, q(XStack.erde, 1.0), q(XStack.flint, 0.1)),
	ErdeSteinMoos2
		= new XBlockDeco("�2Stony Dirt", null, 3, 7, q(XStack.erde, 1.0), q(XStack.flint, 0.1), q(XStack.lianen, 0.03)),
	ErdeTrocken
		= new XBlockDeco("Dry Dirt", null, 3, 8, q(XStack.erde, 1.0), q(XStack.flint, 0.333)),
	CobbleGemauerteWand
		= new XBlockDeco("Brick Wall", null, 4, 1),
	CobbleWeg1Mittelalter// besonders rund :)
		= new XBlockDeco("Rounded Cobble I", null, 4, 2),
	CobbleEinbruch
		= new XBlockDeco("Brocken Cobble", null, 4, 3),
	Cobble2
		= new XBlockDeco("Rounded Cobble II", null, 4, 4),
	CobbleSediment,
	CobbleGrob,
	CobbleWeg2Pflasterstein
		= new XBlockDeco("Cobbled Way", null, 4, 7),
	CobbleWeg3dunkel// Schieferweg :)
		= new XBlockDeco("Slate Way", null, 4, 8),
	PlankenDjungel// extra Muster :)
		= new XBlockDeco("Carved Oak Planks", null, 5, 6),
	PlankenHolzstapel
	 = new XBlockDeco("Stack of Wood", null, 5, 7){
		public boolean onRightClick(Player p, ItemStack inHand, Block b){
			
			if(!BlockListener.blockBreakIsOK(p, b))
				return true;
			
			if(inHand==null || inHand.getTypeId()==0){
				if(BlockListener.blockBreakIsOK(p, b)){
					
					b.setType(Material.AIR);
					b.getWorld().dropItemNaturally(b.getLocation(), new XStack(9, null, 17, 0).i);
					
					return true;
				} else {
					return false;
				}
				
			} else return false;
		}
	},
	PlankenAltEiche
		= new XBlockDeco("Old Oak Planks", null, 5, 8),
	PlankenAltFichte
		= new XBlockDeco("Old Spruce Planks", null, 5, 9),
	PlankenBühne
		= new XBlockDeco("�cStage", null, 5, 10),
	PlankenKiste//Barrelart... mal gucken vllt geht es ja :)
		= new XBlockInventory("Barrel", null, 5, 11, "Barrel", "        X", 1),
	PlankenFichtenBretter
		= new XBlockDeco("Simple Pit", null, 5, 12),
	PlankenEssensFass//Kiste f�r Essen :)
		= new XBlockInventory("Food Barrel", null, 5, 13, "Food Barrel", "    xxxxX"+"         "+"         "+"         "+"         ", 5),
	PlankenGartenHaus
		= new XBlockDeco("Wooden Hut Block", null, 5, 14),
	PlankenGestrichen
		= new XBlockDeco("Painted Planks", null, 5, 15),
	SandRot
		= new XBlockDeco("Red Sand", null, 12, 1),
	SandFein// mit Pflanzen, wenn an Rand zu Gras
		= new XBlockDeco("Fine Sand", null, 12, 2),
	KiesGrob
		= new XBlockDeco("Coarse Gravel", null, 13, 1, q(new XStack("�1�fCoarse Gravel", 13, 1).i, 0.9), q(XStack.flint, 0.1)),
	KiesBewachsen
		= new XBlockDeco("�2Gravel", null, 13, 2, q(XStack.gravel, 0.9), q(XStack.flint, 0.1), q(XStack.lianen, 0.1)),
	KiesGrobBewachsen
		= new XBlockDeco("�2Coarse Gravel", null, 13, 3, q(new XStack("�1�fCoarse Gravel", 13, 1).i, 0.9), q(XStack.flint, 0.1), q(XStack.lianen, 0.1)),
	KiesSauber// im normalem sind kleine Pfl�nzchen
		= new XBlockDeco("Fine Gravel", null, 13, 4, XStack.gravel),
	GolderzKiste
		= new XBlockDeco("Barrel of Gold Ore", null, 14, 1),
	GolderzGoldSack
		= new XBlockDeco("Gold Wallet", null, 14, 2),
	EisenerzKiste
		= new XBlockInventory("Barrel of Iron Ore", null, 15, 1, "Barrel Of Iron", "        X", 1),
	SchwammKoralle1Anemone
		= new XBlockDeco("Coral Anemone", null, 19, 1),
	SchwammKoralle2
		= new XBlockDeco("Yellow Carpet Anemone", null, 19, 2),
	SchwammKoralle3Blau
		= new XBlockDeco("Blue Carpet Anemone", null, 19, 3),
	GlasEinzel// das gleiche wie der Standart? dann auch .simpleChange
		= new XBlockDeco("Seperated Glass", null, 20, 1),
	GlasViereck1
		= new XBlockDeco.SimpleChange("Angular Glass", null, 20, 2, 20, 3),
	GlasNull1// l�sst sich mit der Viereck beliebig tauschen (f�r st�rkere Kanten)
		= new XBlockDeco.SimpleChange("Angular Glass", null, 20, 3, 20, 2, GlasViereck1),
	GlasViereck2
		= new XBlockDeco.SimpleChange("Older Glass", null, 20, 4, 20, 5),
	GlasNull2// l�sst sich mir der ViereckII beliebig tauschen -> auch schon Drachig
		= new XBlockDeco.SimpleChange("Older Glass", null, 20, 5, 20, 4, GlasViereck2),
	GlasBarriere
		= new XBlockDeco("Barrier", null, 20, 6),
	GlasDrachenfenster
		= new XBlockDeco.SimpleChange("Fine Windows", null, 20, 7, 20, 8),
	GlasNull3// auch wieder zum Tauschen f�r dickere R�nder bzw Muster
		= new XBlockDeco.SimpleChange("Fine Windows", null, 20, 8, 20, 7, GlasDrachenfenster),
	GlasStrick
		= new XBlockDeco("String Block", null, 20, 9),
	GlasNull4
		= new XBlockDeco("Rope Block", null, 20, 10),
	GlasEisengitter1eng
		= new XBlockDeco("Strong Prison Bars", null, 20, 8),
	GlasEisengitter2fest
		= new XBlockDeco("Old Iron Window", null, 20, 8),
	GlasSteinfenster
		= new XBlockDeco("Toilet/Stone Window", null, 20, 8),
	LapiserzKiste
		= new XBlockInventory("Barrel of Lapis Ore", null, 21, 1, "Barrel Of Lapis", "      X", 1),
	Lapisblock1Hell,
	Lapisblock2Hell,
	Lapisblock3normal,
	LapisblockMoosMauer
		= new XBlockDeco("Dungeon Wall", null, 22, 4),
	LapisblockGrüneMauer
		= new XBlockDeco("Dungeon Bricks", null, 22, 5),
	LapisblockGrüneKacheln
		= new XBlockDeco("Dungeon Tiles", null, 22, 6),
	LapisblockSteinGlitzerDunkel
		= new XBlockDeco("Magic Rock", null, 22, 7),
	SandsteinWand
		= new XBlockDeco("Chiseled Sandstone", null, 24, 3),
	SandsteinBordüre
		= new XBlockDeco("Sandstone Bordure", null, 24, 4),
	SandsteinNatur
		= new XBlockDeco("Natural Sandstone", null, 24, 5),
	SandsteinDrachen
		= new XBlockDeco("Fine Sandstone", null, 24, 6),
	SandsteinRot
		= new XBlockDeco("Red Sandstone", null, 24, 7),
	SandsteinRotSäule
		= new XBlockDeco.SimpleChange("Red Sandstone Pillar", null, 24, 8, 24, 9),
	SandsteinRotSäuleZeichen
		= new XBlockDeco.SimpleChange("Red Sandstone Pillar2", null, 24, 9, 24, 8, SandsteinRotSäule),
	SandsteinMoos
		= new XBlockDeco("Mossy Sandstone", null, 24, 10),
	NetzFlaschen//30.1 // von jedem abstellbar? w�re ja ne Idee :)
		= new XBlockDeco("Bottle", null, 30, 1),
	NetzRauch
		= new XBlockDeco("Smoke", null, 30, 2),
	NetzEisenkette// in Smallmountains aus Gold!, Djungel bewachsen, Ozean Fischkette
		= new XBlockDeco("Iron Chain", null, 30, 3),
	NetzSeltsam,//Busch?
	NetzSpeere
		= new XBlockDeco("Spears", null, 30, 5),
	NetzStrick
		= new XBlockDeco("Rope", null, 30, 6),
	NetzTierleder
		= new XBlockDeco("Leader", null, 30, 7, new ItemStack(334)),
	NetzFliegen
		= new XBlockDeco("Flies", null, 30, 8),
	NetzWeinrebe// in -Hills Stalaktiten, im Djungel Lianen, in Cold- Eiszapfen
		= new XBlockDeco("Wine/Stalactites", null, 30, 9),
	NetzKranhacken// in Ozean etwas anders... (auch etwas komisch)
		= new XBlockDeco("Cords", null, 30, 10),
	NetzSchmetterlinge// alias Lampen xD -> Gl�hw�rmchen?
		= new XBlockDeco("Butterflies", null, 30, 11),
	NetzAufgehangenes// Ozean = Fisch, IcePlaint=Wurst/Fleisch, SmallMountains = Geldsack, Jungle=Bananen
		= new XBlockDeco("Hung Up!", null, 30, 12),
	NetzVogel// ForestHills = Eule, TaigaHills+SmallMountains = Kr�he, Jungle,Forest = blauer Vogel, River = Ente, Ozean = M�ve
		= new XBlockDeco("Bird", null, 30, 13),
	NetzFrosch// ja 15 gibts nicht
		= new XBlockDeco("Frog", null, 30, 14),
	// Bl�mchen extra? -> ja
	BlumeBuschBlaubeeren
		= new XBlockDeco("�5Blueberry", null, 38, 9),
	BlumeBuschHimbeeren
		= new XBlockDeco("�4Strawberry", null, 38, 10),
	BlumeBuschBrombeeren
		= new XBlockDeco("�1Blackberry", null, 38, 11),
	BlumeKuhschelle
		= new XBlockDeco("�5Dane's Blood", null, 38, 12),
	BlumeKnabenkraut//=Orchideenart
		= new XBlockDeco("�4Fan Orchid", null, 38, 13),
	GoldMosaik,
	GoldRotOrnament
		= new XBlockDeco.SimpleChange("Red Gold", null, 41, 2, 41, 3),
	GoldNull1
		= new XBlockDeco.SimpleChange("Red Gold", null, 41, 3, 41, 2, GoldRotOrnament),
	GoldOrnament,
	GoldDeko,
	Eisen0
		= new XBlockDeco.SimpleChange(null, null, 42, 0, 42, 1),
	Eisen1
		= new XBlockDeco.SimpleChange(null, null, 42, 1, 42, 2, Eisen0),
	Eisen2
		= new XBlockDeco.SimpleChange(null, null, 42, 2, 42, 0, Eisen0),
	//Eisen3
	//	= new XBlockDeco.SimpleChange(null, null, 42, 3, 42, 4, Eisen0),
	//Eisen4
	//	= new XBlockDeco.SimpleChange(null, null, 42, 4, 42, 0, Eisen0),
	EisenPanzer
		= new XBlockDeco("�7Hardened Iron Block", null, 42, 5),
	//EisenRau = new XBlockDeco("", null, 42, 6), <- ziemlich gleich zu 0-4
	EisenRostig
		= new XBlockDeco("�cRusty Iron Block", null, 42, 7),
	Eisen1x1
		= new XBlockDeco("Iron Block", null, 42, 8),
	EisenAlsDreckVersteckt
		= new XBlockDeco("Iron Dirt", null, 42, 9),
	StufeDreckLow// alle au�er den Holzstufen sind auch wirklich belegt, da die Holzstufen sp�ter in mehr Variationen dazu kamen... deshalb gibts das :)
		= new XBlockDeco("Dirt Slab", null, 44, 2){
		@Override public ItemStack get(int amount){
			return XStack.dirt1Sl;
		}
		
		@Override public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			return new ItemStack[]{XStack.dirt1Sl};
		}
	},
	StufeDreckTop
		= new XBlockDeco("Dirt Slab", null, 44, 10){
		@Override public ItemStack get(int amount){
			return XStack.dirt1Sl;
		}
		
		@Override public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			return new ItemStack[]{XStack.dirt1Sl};
		}
	},
	StufeDreckDoppel // existiert nicht wirklich -> sieht auch aus wie Holz
		= new XBlockDeco("Dirt Dubble Slab", null, 43, 2){
		@Override public ItemStack get(int amount){
			return XStack.dirt2Sl;// <- tada wir sind ein Erdblock :D
		}
		
		@Override public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			return new ItemStack[]{XStack.dirt2Sl};
		}
	},
	ZiegelMoos
		= new XBlockDeco("�2Mossy Bricks", null, 45, 1, q(XStack.bricks, 1.0), q(XStack.lianen, 0.3)),
	ZiegelFachwerk
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 2, 45, 3),
	ZiegelFachwerk2links
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 3, 45, 4, ZiegelFachwerk),
	ZiegelFachwerk3rechts
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 4, 45, 5, ZiegelFachwerk),
	ZiegelFachwerk4x
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 5, 45, 6, ZiegelFachwerk),
	ZiegelFachwerk5A
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 6, 45, 7, ZiegelFachwerk),
	ZiegelFachwerk6V
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 7, 45, 8, ZiegelFachwerk),
	ZiegelFachwerk72er
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 8, 45, 9, ZiegelFachwerk),
	ZiegelFachwerk8Glinks
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 9, 45, 10, ZiegelFachwerk),
	ZiegelFachwerk9Grechts
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 10, 45, 11,ZiegelFachwerk),
	ZiegelFachwerk10parallel
		= new XBlockDeco.SimpleChange("Studwork Block", null, 45, 11, 45, 2, ZiegelFachwerk),
	BücherNormal
		= new XBlockDeco.SimpleChange(null, null, 47, 0, 47, 5){
		@Override public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			return new ItemStack[]{new XStack(MathHelper.random.nextInt(3)+1, null, Material.BOOK).i, new XStack(MathHelper.random.nextInt(6)+1, null, Material.WOOD).i};
		}
	},
	BücherBühne
		= new XBlockDeco("Lord's Table", null, 47, 1),
	BücherFlaschen
		= new XBlockDeco("Bottleshelf", null, 47, 2),
	BücherPapierrollen
		= new XBlockDeco("Postshelf", null, 47, 3),
	BücherEssensregal//???
		= new XBlockDeco("Shelf of Anything", null, 47, 4),
	BücherTrennregal
		= new XBlockDeco.SimpleChange("Bookshelf", null, 47, 5, 47, 0, BücherNormal),
	Cobblemoos2// Gras w�chst von oben runter... -> st�rker bewachsen
		= new XBlockDeco("�2Very Mossy Cobblestone", null, 4, 1),
	CobblemoosBricks
		= new XBlockDeco("Mossy Bricks", null, 48, 2),
	Cobblemoos3// feineres, helleres Moos -> leicht bewachsen
		= new XBlockDeco("�aBit Mossy Cobblestone", null, 48, 3),
	ObsidianFliesen
		= new XBlockDeco("Obsidian Tiles", null, 49, 1),
	// bei den Eichenstufen gibts noch andere :)
	DiaerzFass
		= new XBlockInventory("Barrel of Diamond Ore", null, 56, 1, "Dia Barrel", "xxx   xxX", 1),
	DiablockSpiegel
		= new XBlockDeco("Mirror", null, 57, 1),
	Werkbank1
		= new XBlockDeco.SimpleChange(null, null, 58, 0, 58, 1),
	Werkbank2// einzelne, sich nicht verbindende St�cke...
		= new XBlockDeco.SimpleChange(null, null, 58, 1, 58, 0, Werkbank1),
	// bei den Steinstufen gibts moosige...
	SteintrittSchachfeld//Meta2
		= new XBlockDeco("Chess", null, 70, 2),
	SteintrittSchulsachen
		= new XBlockDeco("School Equip", null, 70, 3),
	SteintrittSpielkarten
		= new XBlockDeco("Cards", null, 70, 4),
	SteintrittTeller
		= new XBlockDeco("Plate", null, 70, 5),
	SteintrittSteinUndKaputt
		= new XBlockDeco("Deco Plate", null, 70, 6),
	EisNebel
		= new XBlockDeco("Mist", null, 79, 1),
	EisPackeisSchmelzend
		= new XBlockDeco("�bLight Packed Ice", null, 79, 2),// Packeis ohne Schneedecke
	EisZiegelsteinwand
		= new XBlockDeco("�bIce Brick", null, 79, 3),
	EisKristalle
		= new XBlockDeco("Energy Crystal Wall", null, 79, 4),
	EisGrau,//leicht gr�ulicher als normal
	Schnee1
		= new XBlockDeco.SimpleChange("Solid Snow", null, 80, 1, 80, 2),
	Schnee2
		= new XBlockDeco.SimpleChange("Solid Snow", null, 80, 2, 80, 3),
	Schnee3
		= new XBlockDeco.SimpleChange("Solid Snow", null, 80, 3, 80, 4),
	Schnee4
		= new XBlockDeco.SimpleChange("Solid Snow", null, 80, 4, 80, 5),
	Schnee5
		= new XBlockDeco.SimpleChange("Solid Snow", null, 80, 5, 80, 6),
	Schnee6
		= new XBlockDeco.SimpleChange("Solid Snow", null, 80, 6, 80, 7),
	Schnee7
		= new XBlockDeco.SimpleChange("Solid Snow", null, 80, 7, 80, 1),
	LehmIgeErde
		= new XBlockDeco("Clay Dirt", null, 82, 1){
		@Override
		public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			return new ItemStack[]{new XStack(MathHelper.random.nextInt(4)+1, null, Material.CLAY).i};
		}
	},
	LehmFass
		= new XBlockInventory("Clay Barrel", null, 82, 2, "Clay Barrel", "x       X", 1),
	ZaunOak
		= new XBlockDeco("Oak Fence", null, 85, 0),
	ZaunSpruce
		= new XBlockDeco("Spruce Fence", null, 85, 1),
	ZaunBirchLog
		= new XBlockDeco("Birch Log Fence", null, 85, 2),
	ZaunSpruceLog
		= new XBlockDeco("Spruce Log Fence", null, 85, 3),
	ZaunJungleLog
		= new XBlockDeco("Jungle Log Fence", null, 85, 4),
	ZaunOakLog
		= new XBlockDeco("Oak Log Fence", null, 85, 5),
	ZaunDarkLog// M�rchenwald
		= new XBlockDeco("Dark Oak Log Fence", null, 85, 6),
	ZaunAcaciaLog// Akazie
		= new XBlockDeco("Acasia Log Fence", null, 85, 7),
	ZaunLightOak// naja...
		= new XBlockDeco("Light Oak Fence", null, 85, 8),
	SeelensandMatsch
		= new XBlockDeco("Mud", null, 88, 1),
	SeelensandAmeisen
		= new XBlockDeco("Anthill", null, 88, 2),
	SeelensandEtwasMehrRot
		= new XBlockDeco("Bloody Soul Sand", null, 88, 3),
	GlowstoneIndustrieWeiß//<-1; 0 sieht aus wie eine Lampe -.- muss ge�ndert werden... irgendwie...
		= new XBlockDeco("Industrial White Lamp", null, 89, 1),
	GlowstoneIndustrieGelb
		= new XBlockDeco("Industrial Orange Lamp", null, 89, 2),
	GlowstoneLapismauerLampe
		= new XBlockDeco("�2Dungeon Lamp", null, 89, 3),
	// normaler Eisenzaun ist ziemlich pr�chtig
	EisenzaunDunkel
		= new XBlockDeco("�8Dark Iron Fence", null, 101, 1),
	EisenzaunRostig
		= new XBlockDeco("�cRusty Iron Fence", null, 101, 2),
	EisenzaunStreifen
		= new XBlockDeco.SimpleChange("Iron Fence Stripes", null, 101, 3, 101, 4),
	EisenzaunKaro
		= new XBlockDeco.SimpleChange("Icon Fence Checked", null, 101, 4, 101, 5),
	EisenzaunSicher//Viele Verstrebungen
		= new XBlockDeco.SimpleChange("Icon Fence Secure", null, 101, 5, 101, 3),
	EisenzaunWaffen,
	EisenzaunWaffenModerner,
	EisenzaunAufgehangenes// wie oben, blo� eben gerade zum Blick nach NSWO
		= new XBlockDeco("Hung Up", null, 101, 8),
	EisenzaunHolzzaunsicher,
	EisenzaunAufgehangeneKlamotten
		= new XBlockDeco("Clothes line", null, 101, 10),
	EisenzaunSpanferkel,//Spanferkel/Feischst�ckchen/Ente
	EisenzaunVorhang,// normal rot, sonst auch blau, gelb, beige, wei�, schwarz oder gr�n
	EisenzaunGoldeneWaage
		= new XBlockDeco("Golden Weightbridge", null, 101, 13),
	EisenzaunZahnräder// gef�hrlich :), aber leider nicht animiert
		= new XBlockDeco("Sharp Wheels", null, 101, 14),// make damage?
	// ja, die 15 ist noch frei
	GlasscheibeEckig,
	GlasscheibeDrachenEckig,
	GlasscheibeDreck,//kA, was das sein soll xD
	GlasscheibeDrachenRund,
	GlasscheibeMaschennetz,
	GlasscheibeBarriere
		= new XBlockDeco("Barrier Pane", null, 102, 6),// 6 wirklich?
	GlasscheibeStrickzaun,
	GlasscheibeSchlinge,// zum Erh�ngen
	GlasscheibeSeile,// ordnen sich zueinander an
	GlasscheibeEisenGlasfenster,// h�sslig, teils rostig
	// Ziegelstein und Steinziegel-Treppen haben jeweils eine zweite Variante... also am besten austauschbar machen, auch wenn aufw�ndig...
	MyzeliumTrüffelkiste
		= new XBlockInventory("Barrel of Mushroom", null, 110, 1, "Barrel of Mush", "         ", 1),
	Brunnen//Meta 4! hat einen verschlossenen Deckel :) - per Biom lassen sich auch verschiedene Fl�ssigkeiten darstellen (Blut, Gr�n, Schwarz, Wasser, Tisch(was soll der da?))
		= new XBlockDeco("Fountain", null, 118, 4),
	EndsteinSkelett1// 2 auf 1 macht ein ganzes Skelett :), sonst K�pfe und von oben Knochen
		= new XBlockDeco.SimpleChange("Bones", null, 121, 8, 121, 9),
	EndsteinSkelett2// Grusel...
		= new XBlockDeco.SimpleChange("Bones", null, 121, 9, 121, 8, EndsteinSkelett1),
	// Enderdracheneiner je nach ID ne andere Farbe
	// bei der 126 gibts noch ne Karte... + neue Holzsorten... + etc.
	HolzstufeTatamitisch
		= new XBlockDeco("Tatami", null, 126, 6),
	HolzstufeKarte
		= new XBlockDeco("Map", null, 126, 14),
	HolzDoppelStufeTischUKarte
		= new XBlockDeco("x.x", null, 125, 6){
		@Override public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			return new ItemStack[]{HolzstufeTatamitisch.get(1), HolzstufeKarte.get(1)};
		}
	},
	HolzstufeHeu
		= new XBlockDeco("Hay Bale Slab", null, 126, 7){
		@Override public ItemStack get(int amount){
			return XStack.hay1Sl;
		}
		
		@Override public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			return new ItemStack[]{XStack.hay1Sl};
		}
	},
	HolzstufeHeu2
		= new XBlockDeco("Hay Bale Slab", null, 126, 15){
		@Override public ItemStack get(int amount){
			return XStack.hay1Sl;
		}
		
		@Override public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			return new ItemStack[]{XStack.hay1Sl};
		}
	},
	SmaragderzFass
		= new XBlockInventory("Barrel of Emerald", null, 129, 1, "Barrel of Emerald", "xxx   xxX", 1),
	SmaragdblockWeinregal1// 1 und 2 sind gleich, verbinden sich jedoch nicht untereinander -> Platz f�r Bier und Wein? eignet sich ja gut :)
		= new XBlockDeco.SimpleChange("Wine Rack", null, 133, 1, 133, 2),
	SmaragdblockWeinregal2// deshalb ineinander umtauschbar
		= new XBlockDeco.SimpleChange("Wine Rack", null, 133, 2, 133, 1, SmaragdblockWeinregal1),
	// wieder Treppenbesonderheiten... da bringen allg. Regeln wohl doch was...
	// Mauern ohne Ende...
	
	GoldenButter//new XStack("�eGolden Butter", 266, 1, x(), "�cCaution, Slippery!").i
		= new XItem("�eGolden Butter", null, 266, 1),
	GoldenCheese//new XStack(9, "�6Golden Cheese", 266, 2, x(), "�eShut up and enjoy the Cheese!").i
		= new XItem("�6Golden Cheese", null, 266, 2),
	
	BiomeChange// ...tja...
		= new XItem("Biome Changer", null, 369, 1){
		@Override public boolean onRightClick(Player p, ItemStack inHand, Block b){
			
			if(!BlockListener.blockBreakIsOK(p, b))
				return true;
			
			b.setBiome(Biome.values()[(b.getBiome().ordinal()+1)%Biome.values().length]);
			b.getWorld().refreshChunk(b.getX()/16, b.getZ()/16);
			ItemMeta meta = inHand.getItemMeta();
			meta.setDisplayName("�e"+b.getBiome().name());
			inHand.setItemMeta(meta);
			return false;
		}
		
		@Override public boolean onLeftClick(Player p, ItemStack inHand, Block b){
			
			if(!BlockListener.blockBreakIsOK(p, b))
				return true;
			
			b.setBiome(Biome.values()[(b.getBiome().ordinal()+Biome.values().length-1)%Biome.values().length]);
			b.getWorld().refreshChunk(b.getX()/16, b.getZ()/16);
			ItemMeta meta = inHand.getItemMeta();
			meta.setDisplayName("�e"+b.getBiome().name());
			inHand.setItemMeta(meta);
			return false;
		}
	},
	//...
	
	y
	;
	
	public static XQuantumStack q(ItemStack i, double chance){
		return new XQuantumStack(i, chance);
	}
	
	public static XQuantumStack q(XStack x, double chance){
		return new XQuantumStack(x.i, chance);
	}
	
	public static void init(){
		//new XBlock("�bIcy Shards", "Eiskristalle|repeat|1-4|2|2|", 79, 1){
		//	@Override public boolean onRightClick(Player p, ItemStack inHand) {return false;}
		//	@Override public boolean onLeftClick(Player p, ItemStack inHand) {return false;}
		//};
		
		//new XBlockInventory("�1LapisChest", "LapisChest", 22, 1, "Lapislazuli Chest")
		//.faces = new XBlockFace[]{new XBlockFace("sides", "random|1|null|null|")};
		GoldMünzen = new XBlockDeco("�6Coins", null, 70, 2);
		
		System.out.println("Registered "+registered.size()+" new Blocks");

	}
	
	String name, src;
	int id;
	public byte data;
	public boolean specialdrops = false;
	public XBlockFace[] faces;
	public ItemStack[] drops;
	public XQuantumStack[] quantumDrops;
	
	public XBlock(String name, String properties, int id, int data){
		this.id=id;this.data=(byte)data;src=properties;
		setName(name);
		registered.add(this);
	}
	
	public XBlock(String name, String properties, int id, int data, ItemStack... drop){
		this.id=id;this.data=(byte)data;src=properties;
		setName(name);
		drops = drop;
		registered.add(this);
	}
	
	public XBlock(String name, String properties, int id, int data, XQuantumStack... quantum){
		this.id=id;this.data=(byte)data;src=properties;
		setName(name);
		quantumDrops = quantum;
		registered.add(this);
	}
	
	private static final String color = "0123456789abcdef";
	private void setName(String name){
		if(name==null){
			return;
		} else if(name.startsWith("�")){
			this.name = "�"+color.charAt(data)+name;
		} else {
			this.name = "�"+color.charAt(data)+"�f"+name;
		}
	}
	
	/**Cares for the right mouse click event, returns true if event should be cancelled*/
	public abstract boolean onRightClick(Player p, ItemStack inHand, Block b);
	/**Cares for the left mouse click event, returns true if event should be cancelled*/
	public abstract boolean onLeftClick(Player p, ItemStack inHand, Block b);
	
	public ItemStack get(int amount){
		return new XStack(amount, name, id, data, RecipeManager.x()).i;
	}
	
	public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
		if(quantumDrops!=null){
			ArrayList<ItemStack> ret = new ArrayList<>();
			
			for(XQuantumStack q:quantumDrops){
				q.addMaybe(ret);
			}
			
			return ret.toArray(new ItemStack[ret.size()]);
		} else return drops == null || drops.length == 0 ? new ItemStack[]{new XStack(name, id, data, RecipeManager.x()).i} : drops;
	}

	@SuppressWarnings("deprecation")
	public static XBlock getBlock(Block b){
		int id=b.getTypeId(), data=b.getData();
		for(XBlock block:registered){
			if(block.id==id && block.data==data){
				return block;
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static XItem getItem(ItemStack i){
		int id=i.getTypeId(), data=i.getData().getData();
		for(XBlock block:registered){
			if(block instanceof XItem && block.id==id && block.data==data){
				return (XItem) block;
			}
		}
		return null;
	}
	
	public static XBlockInventory getBlockInventory(String name){
		name = name.substring(2);
		for(XBlock block:registered){
			if(block instanceof XBlockInventory && ((XBlockInventory) block).invname.equalsIgnoreCase(name)){
				return (XBlockInventory) block;
			}
		}
		return null;
	}

	// bei Holzstufen werden die Daten erst sp�ter erg�nzt, wenn man setData macht, gehen sie allerdings verloren
	@SuppressWarnings("deprecation")
	public static byte cancelPlace(Block b, Player p, ItemStack inHand, BlockPlaceEvent event) {
		
		if(b.getTypeId()==106){
			if(b.hasMetadata(MetaString.vines)){// es sind noch andere Lianen plaziert :)
				byte get = (byte) b.getMetadata(MetaString.vines).get(0).asByte(), is = b.getData();
				if(get!=is){
					b.setData((byte) (get | is));
				} else return -1;
			}
			b.setMetadata(MetaString.vines, new FixedMetadataValue(Plugin.instance, b.getData()));
			return b.getData();
		}
		
		if(!inHand.hasItemMeta()) return b.getData();
		String name = inHand.getItemMeta().getDisplayName();
		
		if(name.length()>3){
			name = name.substring(0, 2);
		}
		
		byte data = -1;int id = inHand.getTypeId();
		for(XBlock x:registered){
			if(name.equals(x.name==null?null:x.name.substring(0, 2)) && id==x.id){
				if(x.id==126 && name.equals("�6")){// Tatamitisch l�sst sich nur als dieser plazieren...
					b.setData((byte) 6);
					return 6;
				} else if((x.id==44 && x.data==2) || (x.id==126 && (x.data==6 || x.data==7))){
					return b.getData();
				}
				b.setData(data = x.data);
				return data;
			}
		}
		return b.getData();
	}

	@SuppressWarnings("deprecation")
	public static boolean cancelBreak(Block b, Player p, BlockBreakEvent event) {
		
		if(b.getTypeId()==106 && b.hasMetadata(MetaString.vines)){
			// interessante Liane :)
			byte got = b.getMetadata(MetaString.vines).get(0).asByte();
			
			// mehr als 1 boolean gesetzt? -> mehrere Lianen? !! Eventabfassung zum Rankenupdate w�re sehr praktisch :)
			if(got!=0 && got!=1 && got!=2 && got!=4 && got!=8){
				int count = ((got&1)==1?1:0)+((got&2)==2?1:0)+((got&4)==4?1:0)+((got&8)==8?1:0);
				if(p.getItemInHand().getTypeId()==359)
					b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.VINE, count));
				b.removeMetadata(MetaString.vines, Plugin.instance);
				b.setType(Material.AIR);
				return true;
			}
		}
		
		XBlock base = getBlock(b);
		if(base!=null){
			b.setType(Material.AIR);
			for(ItemStack drop:base.getDrops(p, p.getItemInHand(), b)){
				if(drop!=null){
					b.getWorld().dropItemNaturally(b.getLocation(), drop);
				}
			}
			return true;
		} else return false;
	}

	public static boolean cancelLeftClick(Block b, Player p, PlayerInteractEvent event) {
		XItem item = getItem(p.getItemInHand());
		if(item != null && item.onLeftClick(p, p.getItemInHand(), b)){
			return true;
		}
		XBlock base = getBlock(b);
		if(base!=null){
			return base.onLeftClick(p, p.getItemInHand(), b);
		} else return false;
	}

	@SuppressWarnings("deprecation")
	public static boolean cancelRightClick(Block b, Player p, PlayerInteractEvent event) {
		
		if(p.getItemInHand().getType()==Material.VINE && b.getType()==Material.VINE){
			
			//b = b.getWorld().getBlockAt(b.getLocation().add(event.getBlockFace().getModX(), event.getBlockFace().getModY(), event.getBlockFace().getModZ()));
			// wir haben unseren Lianenblock :)
			byte side = (byte) (event.getBlockFace().getModX()==0?event.getBlockFace().getModZ()>0?4:1:event.getBlockFace().getModX()>0?2:8);
			if(b.getType()==Material.VINE){// wenn da schon Lianen sind...
				if(b.hasMetadata(MetaString.vines)){// es ist mind. 1 Liane dort
					byte has = b.getMetadata(MetaString.vines).get(0).asByte();
					if((side & has) == 0){// etwas neues wird hinzugef�gt...
						Plugin.remove(p.getInventory(), Material.VINE, 1, (short) 0, null);
						b.setData(has=(byte) (side | has));
						b.setMetadata(MetaString.vines, new FixedMetadataValue(Plugin.instance, has));
					}
				}
			}
		}
		
		XItem item = getItem(p.getItemInHand());
		if(item != null && item.onRightClick(p, p.getItemInHand(), b)){
			return true;
		}
		XBlock base = getBlock(b);
		if(base!=null){
			return base.onRightClick(p, p.getItemInHand(), b);
		}
		return false;
	}
	
	public static boolean cancelInventoryClick(HumanEntity p, Inventory i, InventoryClickEvent e){
		XBlockInventory base = getBlockInventory(i.getName());
		if(base!=null){
			return base.InventoryClick(e);
		} else return false;
	}
	
	public static void closeInventory(HumanEntity p, Inventory i, InventoryCloseEvent e){
		XBlockInventory base = getBlockInventory(i.getName());
		if(base!=null){
			base.InventorySave(p, i, e);
		}
	}
	
	public static ItemStack getForRandom(int id, int amount){
		int data = MathHelper.random.nextInt(16);
		ArrayList<XBlock> possible = new ArrayList<>();
		for(XBlock x:registered){
			if(x.id==id){
				if(x.data==data){
					return x.get(amount);
				} else {
					possible.add(x);
				}
			}
		}
		if(possible.size()==0)return new XStack(amount, null, id, data).i;
		return possible.get(MathHelper.random.nextInt(possible.size())).get(amount);
	}
}
