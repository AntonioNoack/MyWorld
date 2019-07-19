package me.corperateraider.recipes;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
public class XStack {
	
	public static final ItemStack
		stein	= new XStack(1, 0).i,
		gras	= new XStack(2, 0).i,
		erde	= new XStack(3, 0).i,
		cobble	= new XStack(4, 0).i,
		sand	= new XStack(12, 0).i,
		gravel	= new XStack(13, 0).i,
		dirt1Sl = new XStack(1, "§2Dirt Slab", 44, 2).i,
		dirt2Sl = new XStack(2, "§2Dirt Slab", 44, 2).i,
		dirt6Sl = new XStack(6, "§2Dirt Slab", 44, 2).i,
		
		hay1Sl = new XStack(1, "§7§2Hay Bale Slab", 126, 7).i,
		hay2Sl = new XStack(2, "§7§2Hay Bale Slab", 126, 7).i,
		hay6Sl = new XStack(6, "§7§2Hay Bale Slab", 126, 7).i,
		
		bricks	= new XStack(45, 0).i,
		lianen	= new XStack(106, 0).i,
		schneeball = new XStack(332, 0).i,
		flint	= new XStack(259, 0).i;

	
	public ItemStack i;
	private ItemMeta m;
	
	public XStack(int id, int data){
		i = new ItemStack(id, 1, (short) data);
	}
	
	public XStack(String name, int id, int data, XEnchantment[] ench, String... lore){
		i = new ItemStack(id, 1, (short) data);
		
		if(name!=null){
			m = i.getItemMeta();
			m.setDisplayName(name);
			m.setLore(lore(lore));
			i.setItemMeta(m);
		}
		
		for(XEnchantment x:ench){
			i.addUnsafeEnchantment(x.ench, x.level);
		}
	}
	
	public XStack(int amount, String name, int id, int data, XEnchantment[] ench, String... lore){
		i = new ItemStack(id, amount, (short) data);
		
		if(name!=null){
			m = i.getItemMeta();
			m.setDisplayName(name);
			m.setLore(lore(lore));
			i.setItemMeta(m);
		}
		
		for(XEnchantment x:ench){
			i.addUnsafeEnchantment(x.ench, x.level);
		}
	}
	
	public XStack(int amount, String name, Material material){
		i = new ItemStack(material, amount);
		
		if(name!=null){
			m = i.getItemMeta();
			m.setDisplayName(name);
			i.setItemMeta(m);
		}
	}
	
	
	public XStack(int amount, String name, int id){
		i = new ItemStack(id, amount);
		
		if(name!=null){
			m = i.getItemMeta();
			m.setDisplayName(name);
			i.setItemMeta(m);
		}
	}
	
	public XStack(int amount, String name, int id, int data){
		i = new ItemStack(id, amount, (short) data);
		
		if(name!=null){
			m = i.getItemMeta();
			m.setDisplayName(name);
			i.setItemMeta(m);
		}
	}
	
	public XStack(String name, int id, int data){
		i = new ItemStack(id, 1, (short) data);
		
		if(name!=null){
			m = i.getItemMeta();
			m.setDisplayName(name);
			i.setItemMeta(m);
		}
	}
	
	public XStack(String name, int id) {
		i = new ItemStack(id);
		
		if(name!=null){
			m = i.getItemMeta();
			m.setDisplayName(name);
			i.setItemMeta(m);
		}
	}


	public ArrayList<String> lore(String... lore){
		ArrayList<String> ret = new ArrayList<>();
		for(String s:lore){
			ret.add(s);
		}
		return ret;
	}
}
