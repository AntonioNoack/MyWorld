package me.corperateraider.recipes;

import org.bukkit.enchantments.Enchantment;

public class XEnchantment {
	public Enchantment ench;
	public int level;
	public double chance;
	public XEnchantment(Enchantment ench, int level){
		this.ench=ench;
		this.level=level;
	}
	
	public XEnchantment(Enchantment ench, int level, double chance){
		this.ench=ench;
		this.level=level;
		this.chance=chance;
	}
}
