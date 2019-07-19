package me.corperateraider.reload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * Diese Klasse wird nicht verwendet, da sie zum nicht kleinem Teil nicht funktioniert...
 * */
public class MapPortrait extends MapRenderer {
	
	public static void ini(File f, String... s) throws IOException{
		for(String img:s){
			imgs.put("", ImageIO.read(new File(f, "imgs/"+img+".png")));
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void makeImage(String key, Location l){
		World w = l.getWorld();
		//int x=l.getBlockX(), y=l.getBlockY(), z=l.getBlockZ();
		
		MapView map = Bukkit.getServer().createMap(w);
		map.addRenderer(new MapPortrait(key));
		
		l = new Location(w, l.getBlockX(), l.getBlockY(), l.getBlockZ());
		
		ItemFrame f = (ItemFrame) w.spawnEntity(l, EntityType.ITEM_FRAME);
		//ItemStack s = new ItemStack(Material.MAP, 1, map.getId());
		f.setRotation(Rotation.FLIPPED);
		f.setFacingDirection(BlockFace.NORTH);
		f.setItem(new ItemStack(41,1));
	}
	
	static HashMap<String, BufferedImage> imgs = new HashMap<>();
	
	BufferedImage img;
	public MapPortrait(String key){
		img = imgs.get(key);
		if(img==null){
			img = new BufferedImage(1,1,1);
			System.out.println("[reload.MapPortrait] Cannot find key "+key);
		}
	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player p) {
		canvas.drawImage(0, 0, img);
		canvas.setCursors(new MapCursorCollection());
		p.sendMessage("k :)");
	}

}
