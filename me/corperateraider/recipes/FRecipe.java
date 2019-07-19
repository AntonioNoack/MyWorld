package me.corperateraider.recipes;

import net.minecraft.server.v1_7_R1.ItemStack;

/**
 * registered furnace recipe...
 * */
public class FRecipe extends MyRecipe {
	
	public FRecipe(org.bukkit.inventory.ItemStack result, org.bukkit.inventory.ItemStack burn){
		toUse = new XMaterial[]{new XMaterial(burn)};
		this.result = result;
	}
	
	public FRecipe(Object o2, Object o){
		
		notNew = true;
		
		toUse = new XMaterial[]{new XMaterial(convertItemStack(o2))};
		toUse[0].useData=false;// wird ja bei keinem MCeigenem Rezept verwendet
		this.result = convertItemStack(o);
	}
	
	@SuppressWarnings("deprecation")
	public String show(){
		return "§2"+result.getAmount()+"x "+getResultName()+" "+result.getTypeId()+":"+result.getData().getData()+" §c\u2668\n§f   "+toUse[0].amount+"x "+toUse[0].toString();
	}
	
	@SuppressWarnings("deprecation")
	private org.bukkit.inventory.ItemStack convertItemStack(Object o) {
		ItemStack in = (ItemStack) o;
		return new org.bukkit.inventory.ItemStack(switchName(in.getItem().getName()), in.count, (short) 0);
	}
	
	private static int switchName(String name){
		switch(name){
		case "item.fish":
			return 0;
		case "item.coal":
			return 263;
		case "tile.log":
			return 17;
		case "item.beefCooked":
			return 364;
		case "item.beefRaw":
			return 363;
		case "item.chickenCooked":
			return 366;
		case "item.chickenRaw":
			return 365;
		case "item.porkchopCooked":
			return 320;
		case "item.porkchopRaw":
			return 319;
		case "tile.stone":
			return 1;
		case "tile.stonebrick":
			return 4;
		case "item.brick":
			return 336;
		case "item.emerald":
			return 388;
		case "tile.oreEmerald":
			return 129;
		case "tile.oreGold":
			return 14;
		case "tile.oreIron":
			return 15;
		case "tile.oreCoal":
			return 16;
		case "tile.oreLapis":
			return 21;
		case "tile.oreDiamond":
			return 56;
		case "tile.oreRedstone":
			return 73;
		case "item.diamond":
			return 264;
		case "item.ingotIron":
			return 265;
		case "item.ingotGold":
			return 266;
		case "item.redstone":
			return 331;
		case "tile.glass":
			return 20;
		case "tile.sand":
			return 12;
		case "item.clay":
			return 337;
		case "tile.clay":
			return 82;
		case "tile.clayHardened":
			return 159;
		case "item.dyePowder":
			return 351;
		case "tile.cactus":
			return 81;
		case "item.netherbrick":
			return 405;
		case "tile.hellrock":
			return 87;
		case "item.potato":
			return 392;
		case "item.potatoBaked":
			return 393;
		case "item.netherquartz":
			return 406;
		case "tile.netherquartz":
			return 153;
		default:
			System.out.println("Unknown item: "+name);
		}
		return 0;
	}
	
}
