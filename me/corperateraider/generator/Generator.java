package me.corperateraider.generator;

import net.minecraft.server.v1_7_R1.BlockContainer;
import net.minecraft.server.v1_7_R1.Blocks;
import net.minecraft.server.v1_7_R1.ChunkSection;
import net.minecraft.server.v1_7_R1.IContainer;
import net.minecraft.server.v1_7_R1.TileEntity;
import net.minecraft.server.v1_7_R1.TileEntityMobSpawner;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The Generator is the base for all classes which want to deal with fast setting blocks
 * */
public class Generator extends MathHelper {
	
	@SuppressWarnings("deprecation")
	public static boolean setChest(World w, int x, int y, int z, ItemStack[] toAdd){
		w.getBlockAt(x,y,z).setTypeIdAndData(54, (byte) 0, false);
		
		BlockState b = w.getBlockAt(x,y,z).getState();
		if(b instanceof Chest && b.getX()==x && b.getY()==y && b.getZ()==z){
			Inventory inv = ((Chest) b).getBlockInventory();
			inv.addItem(toAdd);
			return true;
		} else {
			return false;
		}
	}
	
	public static void setSpawner(World w, int x, int y, int z, String type){
		net.minecraft.server.v1_7_R1.World world = ((CraftWorld)w).getHandle();
		world.setTypeAndData(x, y, z, Blocks.MOB_SPAWNER, 0, 2);
	    TileEntityMobSpawner localTileEntityMobSpawner = (TileEntityMobSpawner)world.getTileEntity(x, y, z);
	    if (localTileEntityMobSpawner != null) {
	    	localTileEntityMobSpawner.a().a(type);
	    } else {
	    	System.err.println("Failed to fetch mob spawner entity at (" + x + ", " + y + ", " + z + ")");
	    }
	}
	
	@SuppressWarnings("deprecation")
	public void spawnVillager(World w, int x, int y, int z, int profession){
		((Villager)w.spawnEntity(new Location(w,x,y,z), EntityType.VILLAGER)).setProfession(Profession.getProfession(profession));;
	}
	
	public void spawnSheep(World w, int x, int y, int z){
		w.spawnEntity(new Location(w,x,y,z), EntityType.SHEEP);
	}
	
	public void spawnPig(World w, int x, int y, int z){
		w.spawnEntity(new Location(w,x,y,z), EntityType.PIG);
	}
	
	public void spawnCow(World w, int x, int y, int z){
		w.spawnEntity(new Location(w,x,y,z), EntityType.COW);
	}
	
	public void spawnChicken(World w, int x, int y, int z){
		w.spawnEntity(new Location(w,x,y,z), EntityType.CHICKEN);
	}
	
	public void spawnHorse(World w, int x, int y, int z){
		w.spawnEntity(new Location(w,x,y,z), EntityType.HORSE);
	}
	
	static int baseY,nx,ny,nz;
	
	/*private static boolean isID(org.bukkit.World world, int i, int j, int k, int blockId){
		
		net.minecraft.server.v1_7_R1.World w = ((CraftWorld)world).getHandle();
		net.minecraft.server.v1_7_R1.Chunk chunk = w.getChunkAt(nx >> 4, nz >> 4);
		
		int i1 = k << 4 | i;
		if (j >= chunk.b[i1] - 1) {
			chunk.b[i1] = -999;
		}
		return chunk.getType(i, j, k) == net.minecraft.server.v1_7_R1.Block.e(blockId);
	}*/
	
	public static boolean sB(org.bukkit.World world, int x, int y, int z, int blockId, int data){//setBlock	
		is:if(y<32 || y>256-32){
			double[] ds = cooOfNotherWorld(x,y,z);
			nx=(int) ds[0];
			ny=(int) ds[1];
			nz=(int) ds[2];
			
			if(ny<0 || ny>255) break is;
			
			world.loadChunk(nx >> 4, nz >> 4);
			
			net.minecraft.server.v1_7_R1.World w = ((CraftWorld)world).getHandle();
			net.minecraft.server.v1_7_R1.Chunk chunk = w.getChunkAt(nx >> 4, nz >> 4);
			
			a(chunk, nx & 0xF, ny, nz & 0xF, net.minecraft.server.v1_7_R1.Block.e(blockId), data);
		}
		
		if(y<0 || y>255) return false;
		
		net.minecraft.server.v1_7_R1.World w = ((CraftWorld)world).getHandle();
		net.minecraft.server.v1_7_R1.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		return a(chunk, x & 0xF, y, z & 0xF, net.minecraft.server.v1_7_R1.Block.e(blockId), data);
	}
	
	public static boolean a(net.minecraft.server.v1_7_R1.Chunk chunk, int i, int j, int k, net.minecraft.server.v1_7_R1.Block block, int l){
		int i1 = k << 4 | i;
		if (j >= chunk.b[i1] - 1) {
			chunk.b[i1] = -999;
		}
		int j1 = chunk.heightMap[i1];
		net.minecraft.server.v1_7_R1.Block block1 = chunk.getType(i, j, k);
		int k1 = chunk.getData(i, j, k);
	
		if ((block1 == block) && (k1 == l)) {
			return false;
		}
	
		boolean flag = false;
		ChunkSection chunksection = chunk.i()[(j >> 4)];
		if (chunksection == null){
			if (block == Blocks.AIR) {
				return false;
			}
			chunksection = chunk.i()[(j >> 4)] = new ChunkSection(j >> 4 << 4, !chunk.world.worldProvider.g);
			flag = j >= j1;
		}
		int l1 = chunk.locX * 16 + i;
		int i2 = chunk.locZ * 16 + k;
		if (!chunk.world.isStatic) {
			block1.f(chunk.world, l1, j, i2, k1);
		}
		if (!(block1 instanceof IContainer)) {
			chunksection.setTypeId(i, j & 0xF, k, block);
		}
		if (!chunk.world.isStatic) {
			block1.remove(chunk.world, l1, j, i2, block1, k1);
		} else if (((block1 instanceof IContainer)) && (block1 != block)) {
			chunk.world.p(l1, j, i2);
		}
		if ((block1 instanceof IContainer)) {
			chunksection.setTypeId(i, j & 0xF, k, block);
		}
		if (chunksection.getTypeId(i, j & 0xF, k) != block) {
			return false;
		}
		chunksection.setData(i, j & 0xF, k, l);
		if (flag) {
			chunk.initLighting();
		}
		if ((block1 instanceof IContainer)){
			TileEntity tileentity = chunk.e(i, j, k);
			if (tileentity != null) {
				tileentity.u();
			}
		}
		if ((!chunk.world.isStatic) && ((!chunk.world.callingPlaceEvent) || ((block instanceof BlockContainer)))) {
			block.onPlace(chunk.world, l1, j, i2);
		}
		if ((block instanceof IContainer)){
			if (chunk.getType(i, j, k) != block) {
				return false;
			}
			TileEntity tileentity = chunk.e(i, j, k);
			if (tileentity == null){
				tileentity = ((IContainer)block).a(chunk.world, l);
				chunk.world.setTileEntity(l1, j, i2, tileentity);
			}
			if (tileentity != null) {
				tileentity.u();
			}
		}
		chunk.n = true;
		return true;
	}
}
