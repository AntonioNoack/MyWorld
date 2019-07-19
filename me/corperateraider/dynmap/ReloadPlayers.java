package me.corperateraider.dynmap;

import me.corperateraider.myworld.BlockListener;
import me.corperateraider.myworld.Plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReloadPlayers extends Reload {

	public static byte[] getData(String request) {
		String ret="Spieler:";//! ""
		for(Player p:Bukkit.getOnlinePlayers()){
			if(p.getWorld()==Plugin.world){
				Location trueloc = BlockListener.trueLocation(p.getLocation());
				ret+=p.getName()
						+"."+trueloc.getBlockX()
						+"."+trueloc.getBlockY()
						+"."+trueloc.getBlockZ()+"\n";
			}
		}
		
		return ret.getBytes();
	}

	@SuppressWarnings("deprecation")
	public static int ID(ItemStack is){
		return is==null?0:is.getTypeId();
	}
}
