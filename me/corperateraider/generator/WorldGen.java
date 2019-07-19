package me.corperateraider.generator;

import java.util.ArrayList;

import me.corperateraider.recipes.RecipeManager;
import me.corperateraider.recipes.XBlock;
import me.corperateraider.recipes.XStack;

import org.bukkit.inventory.ItemStack;


public abstract class WorldGen extends MathHelper {

	protected Random b = new Random(0);
	
	/**
	 * generate the map :)
	 * */
	public abstract void a(long seed, int cx, int basey, int cz, short[][] ret);
	
	public void setBlock(short[][] result, int x, int y, int z, short blockId){
		if(y<0 || y>255 || x<0 || x>15 || z<0 || z>15){
			return;
		}
		if(result[y >> 4]==null){
			result[y >> 4] = new short[4096];
		}
		result[y >> 4][((y&0xF)<<8) | (z << 4) | x] = blockId;
	}
	
	public short getBlock(short[][] result, int x, int y, int z){
		if(y<0 || y>255 || x<0 || x>15 || z<0 || z>15){
			return -1;
		}
		if(result[y >> 4]==null){
			return -1;
		}
		return result[y >> 4][((y&0xF)<<8) | (z << 4) | x];
	}
	
	public static ItemStack[] trashChest(Random r){
		if(r.next()<0.9){
			return new ItemStack[]{};
		} else {
			ArrayList<ItemStack> add = new ArrayList<>();
			for(int i=0;i<10;i++){
				if(r.next()<0.03){
					add.add(XBlock.getForRandom(1, r.nextInt(16)+1));
				}
				if(r.next()<0.03){
					add.add(XBlock.getForRandom(3, r.nextInt(16)+1));
				}
				if(r.next()<0.03){
					add.add(XBlock.getForRandom(4, r.nextInt(16)+1));
				}
				if(r.next()<0.01){
					add.add(XBlock.getForRandom(30, r.nextInt(5)+1));
				}
				if(r.next()<0.03){
					add.add(XBlock.getForRandom(280, r.nextInt(3)+1));
				}
				if(r.next()<0.01){
					add.add(new XStack(r.nextIntSQ(16)+1, null, 17, r.nextInt(4)).i);
				}
				if(r.next()<0.01){
					add.add(new XStack(1, null, (r.nextBoolean()?268:272)+r.nextInt(4)).i);
				}
				if(r.next()<0.01){
					add.add(new XStack(1, null, r.nextBoolean()?290:291).i);
				}
				if((r.rawNext()&0xffff)==5462){
					add.add(RecipeManager.telescopeBow);
				}
			}
			return add.toArray(new ItemStack[add.size()]);
		}
	}
}
