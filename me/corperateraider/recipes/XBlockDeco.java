package me.corperateraider.recipes;

import me.corperateraider.myworld.BlockListener;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class XBlockDeco extends XBlock {
	public XBlockDeco(String name, String properties, int id, int data) {
		super(name, properties, id, data);}
	public XBlockDeco(String name, String properties, int id, int data, ItemStack... drop) {
		super(name, properties, id, data, drop);}
	public XBlockDeco(boolean needSilktouch, String name, String properties, int id, int data, ItemStack... drop) {
		super(name, properties, id, data, drop);
		silk = needSilktouch;
	}
	public XBlockDeco(String name, String properties, int id, int data, XQuantumStack... drop) {
		super(name, properties, id, data, drop);}
	public XBlockDeco(boolean needSilktouch, String name, String properties, int id, int data, XQuantumStack... drop) {
		super(name, properties, id, data, drop);
		silk = needSilktouch;
	}
	@Override public boolean onRightClick(Player p, ItemStack inHand, Block b) {return false;}
	@Override public boolean onLeftClick(Player p, ItemStack inHand, Block b) {return false;}
	
	public boolean silk;
	
	@SuppressWarnings("deprecation")
	@Override public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
		if(silk){
			if(inHand==null){
				return new ItemStack[]{};
			}
			int id = inHand.getTypeId();
			if(id==257 || id==274 || id==270 || id==278 || id==285){
				return inHand.getEnchantmentLevel(Enchantment.SILK_TOUCH)>0?new ItemStack[]{get(1)}:super.getDrops(p, inHand, b);
			} else {
				return new ItemStack[]{};
			}
		} else return super.getDrops(p, inHand, b);
	}
	
	public static class SimpleChange extends XBlockDeco {
		
		int  nextID;
		byte nextData;
		XBlock dropXBlock;
		
		public SimpleChange(String name, String properties, int id, int data, int nextID, int nextData) {
			super(name, properties, id, data);
			this.nextID = nextID;
			this.nextData = (byte) nextData;
		}
		
		public SimpleChange(String name, String properties, int id, int data, int nextID, int nextData, XBlock drop) {
			super(name, properties, id, data);
			this.nextID = nextID;
			this.nextData = (byte) nextData;
			dropXBlock = drop;
		}
		
		@Override
		public ItemStack get(int amount){
			return new XStack(amount, name, id, data, RecipeManager.x(), "§3#Chisel").i;
		}
		
		@Override public ItemStack[] getDrops(Player p, ItemStack inHand, Block b){
			if(dropXBlock!=null){
				return dropXBlock.getDrops(p, inHand, b);
			} else return super.getDrops(p, inHand, b);
		}
		
		@SuppressWarnings("deprecation")
		@Override public boolean onRightClick(Player p, ItemStack inHand, Block b) {
			
			if(!BlockListener.blockBreakIsOK(p, b))
				return true;
			
			if(inHand!=null && inHand.hasItemMeta() && inHand.getItemMeta().getDisplayName().toLowerCase().endsWith("chisel")){
				if(BlockListener.blockBreakIsOK(p, b)){
					b.setTypeIdAndData(nextID, nextData, false);
					return true;
				}
				return false;
			}
			return false;
		}
	}
}
