package me.corperateraider.recipes;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class XItem extends XBlock {
	
	public XItem(String name, String properties, int id, int data) {super(name, properties, id, data);}

	@Override
	public boolean onRightClick(Player p, ItemStack inHand, Block b) {
		return false;
	}

	@Override
	public boolean onLeftClick(Player p, ItemStack inHand, Block b) {
		return false;
	}

}
