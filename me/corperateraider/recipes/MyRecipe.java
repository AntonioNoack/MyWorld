package me.corperateraider.recipes;

import org.bukkit.inventory.ItemStack;

public abstract class MyRecipe {
	
	public boolean notNew;
	
	public ItemStack result;
	
	public String[] rec;
	public XMaterial[] mats, toUse;
	public XMaterial[][] matz;
	public XEffect[] effects;
	
	public String show(){
		return "";
	}
	
	public String getResultName() {
		String s = result.hasItemMeta()?result.getItemMeta().getDisplayName():"§f"+result.getType().name();
		return s.substring(s.charAt(2)=='§'?4:2);
	}
	
	/**
	 * gibt die Kennzahl des Rezeptergebnisses zurück
	 * */
	public String magic(){
		return (result.hasItemMeta()?result.getItemMeta().getDisplayName()+"xx":"xx").substring(0, 2);
	}
	
	public static String magic(ItemStack result){
		return (result.hasItemMeta()?result.getItemMeta().getDisplayName()+"xx":"xx").substring(0, 2);
	}
}
