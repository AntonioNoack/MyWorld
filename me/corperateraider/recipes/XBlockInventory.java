package me.corperateraider.recipes;

import java.util.ArrayList;
import java.util.HashMap;

import me.corperateraider.myworld.BlockListener;
import me.corperateraider.myworld.Plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import converter.MetaString;

public class XBlockInventory extends XBlock {

	static HashMap<String, Block> players = new HashMap<>();
	
	String invname, staticInv;// InventarKontruktion
	int invsize=1;
	@Deprecated
	public XBlockInventory(String name, String properties, int id, int data, String invname) {
		super(name, properties, id, data);
		this.invname=invname;
	}
	
	public XBlockInventory(String name, String properties, int id, int data, String invname, String inv, int invsize) {
		super(name, properties, id, data);
		this.invname=invname;
		this.invsize = invsize;
		staticInv=(inv+"   ").substring(0, invsize*9);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean onRightClick(Player p, ItemStack inHand, Block b) {
		
		if(p.isSneaking()) return false;
		if(!BlockListener.blockBreakIsOK(p, b))
			return true;
		
		if(b.getY()<16){
			// shift in die untere Welt :)
			double[] c = BlockListener.cooOfNotherWorld(b.getX(), b.getY(), b.getZ());
			if(c[1]>16){// 0. Schicht wird ja sonst vernachlässigt :) -> es wirde einen Stackoverflow geben...
				return onRightClick(p, inHand, b.getWorld().getBlockAt((int)c[0], (int)c[1], (int)c[2]));
			}
		}
		
		for(Block sec:players.values()){
			if(sec!=null && sec.getLocation().distanceSquared(b.getLocation())<1){
				p.sendMessage(Plugin.serfix+"This inventory is in use!");
				return true;
			}
		}
		
		Inventory inv = Bukkit.createInventory(p, invsize*9, "§0"+invname);
		if(staticInv!=null){

			ArrayList<ItemStack> items = null;
			int index=0;
			for(MetadataValue mv:b.getMetadata(MetaString.inventory)){
				if(mv.value() instanceof ArrayList){
					items = (ArrayList<ItemStack>) mv.value();
				}
			}
			int sil = staticInv.length();
			for(int i=0;i<sil;i++){
				switch(staticInv.charAt(i)){
				case ' ':
					if(items!=null && index<items.size()){
						inv.setItem(i, items.get(index++));
					}
					break;
				case 'd':
					inv.setItem(i, new ItemStack(Material.DIAMOND));
					break;
				case 'x':// freies Feld
					inv.setItem(i, new XStack("§0Nothing", 102).i);
					break;
				case 'X':// schließe das Fenster
					inv.setItem(i, new XStack("§4EXIT", 106).i);
					break;
				}
			}
			
			players.put(p.getName(), b);
		}
		
		p.openInventory(inv);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public ItemStack[] getContents(Block b){
		if(staticInv!=null){
			ArrayList<ItemStack> list = null;
			for(MetadataValue mv:b.getMetadata(MetaString.inventory)){
				if(mv.value() instanceof ArrayList){
					list = (ArrayList<ItemStack>) mv.value();
					break;
				}
			}
			if(list!=null){
				return list.toArray(new ItemStack[list.size()]);
			} else return new ItemStack[]{};
		} else return new ItemStack[]{};
	}
	
	@Override
	public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
		// es wird nicht nur der Block sondern auch aller Inhalt gedropt :)
		ItemStack[] content = getContents(b);
		ItemStack[] ret = new ItemStack[content.length+1];
		int i=0;
		for(;i<content.length;i++){
			ret[i]=content[i];
		}
		ret[i] = new XStack(name, id, data, RecipeManager.x()).i;
		
		// außerdem wird das alles zum letztem Mal ausgeführt, sodass die Daten sicherheitshalber gelöscht werden
		// (man könnte sie auch beim Setzen löscheln, aber so wird eventuell Platz gespart)
		
		b.removeMetadata(MetaString.inventory, Plugin.instance);
		b.removeMetadata(MetaString.deathProtected, Plugin.instance);
		
		Bukkit.broadcastMessage(p.getName()+" closed "+b.getType().name());
		
		return ret;
	}

	@Override
	public boolean onLeftClick(Player p, ItemStack inHand, Block b) {// eventuell per Stock drehbar, aber ich glaube nicht, dass wir genug Blöcke der richtigen Eigenschaft dafür übrig haben...
		// do nothing...
		return false;
	}
	
	public boolean InventoryClick(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		
		if(e.getRawSlot()>=staticInv.length()){
			return false;
		}
		
		if(staticInv!=null){
			char c = staticInv.charAt(e.getRawSlot());
			if(c=='X'){
				e.setCancelled(true);
				p.closeInventory();
			} else if(c!=' '){
				e.setCancelled(true);
			}
			return false;
		} else {
			return true;
		}
	}

	public void InventorySave(HumanEntity p, Inventory i, InventoryCloseEvent e) {
		if(players.containsKey(p.getName())){
			Block b = players.get(p.getName());
			XBlockInventory old = getBlockInventory(i.getName());
			if(this.equals(old)){
				// speichere...
				ArrayList<ItemStack> toSave = new ArrayList<>();
				for(int j=0;j<staticInv.length();j++){
					if(staticInv.charAt(j)==' '){
						toSave.add(i.getItem(j));
					}
				}
				b.setMetadata(MetaString.inventory, new FixedMetadataValue(Plugin.instance, toSave));
			} else {
				Bukkit.broadcastMessage("Different block!");
			}
			players.put(p.getName(), null);
			
			Bukkit.getPlayer(p.getName()).sendMessage(Plugin.serfix+i.getTitle().substring(2)+" saved!");
		}
	}
}
