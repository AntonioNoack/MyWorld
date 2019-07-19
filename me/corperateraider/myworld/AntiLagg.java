package me.corperateraider.myworld;

import java.util.List;

import me.corperateraider.generator.MathHelper;
import me.corperateraider.weather.Weather;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import converter.TPS;

public class AntiLagg implements Runnable {

	static final String prefix = "[AntiLagg by LaggRemover+myWorld] ";

	public static int id;
	
	World earth, world;
	int counter, chunksunloaded;
	boolean tr=true;
	long t, tges;
	
	public AntiLagg(){
		earth=EarthAnimation.w;
		world=Plugin.world;
		
		if(earth!=null){
			earth.setAutoSave(false);
		}
	}
	
	@Override public void run() {
		
		if(world.getPlayers().size()>0 && MathHelper.random()<0.1 && TPS.getTPS()>15){// ändern, wenn Jahreszeitenlänge geändert wird
			Chunk[] c = world.getLoadedChunks();
			if(c.length>0){
				Weather.load(c[MathHelper.random.nextInt(c.length)], true);
			}
		}
		
		if(world.getPlayers().size()==0 && world.getLoadedChunks().length>0){
			System.out.println("Unloading "+world.getLoadedChunks().length+" chunks...");
			for(Chunk c:world.getLoadedChunks()){
				c.unload();
			}
			System.out.println("...done!");
		}
		
		return;
		
		/*double tps = TPS.getTPS();
		if(tps!=20.0){
			return;
		}*/
		
		/*t=System.currentTimeMillis();
		
		if(earth != null && earth.getPlayers().size()==0){
			for (Chunk chunk : earth.getLoadedChunks()) {
				world.unloadChunk(chunk);
			}
		}
		
		//ArrayList<Position> poss = new ArrayList<>();
		ArrayList<Chunk> not = new ArrayList<>(), toload= new ArrayList<>();
		
		for(Chunk c: world.getLoadedChunks()){
			not.add(c);
		}
		
		for(Player p:world.getPlayers()){
			Chunk cc;
			//int x=0, z=0;
			
			int x=p.getLocation().getBlockX(), z=p.getLocation().getBlockZ();
			boolean top=p.getLocation().getBlockY()>230, bot=p.getLocation().getBlockX()<26;
			double[] plus=BlockListener.cooOfNotherWorld(x, 256, z), minus=BlockListener.cooOfNotherWorld(x, 0, z);
			int[] xz = new int[]{(int) plus[0], (int) plus[2], (int) minus[0], (int) minus[2]};
			
			if(top || bot)for(int i=-12;i<=12;i++){
				for(int j=-12;j<=12;j++){
					if(top)if(not.contains(cc=world.getChunkAt(xz[0]+i*12, xz[1]+j*12))){
						not.remove(cc);
					} else toload.add(cc);
					if(bot)if(not.contains(cc=world.getChunkAt(xz[2]+i*12, xz[3]+j*12))){
						not.remove(cc);
					} else toload.add(cc);
				}
			}
			
			for(int i=-12;i<=12;i++){
				for(int j=-12;j<=12;j++){
					if(not.contains(cc=world.getChunkAt(x+i*12, z+j*12))){
						not.remove(cc);
					} else toload.add(cc);
				}
			}
			//poss.add(new Position(BlockListener.trueLocation(p.getLocation())));
		}
		// zuladende Gebiete...
		//for(Chunk c:toload){
		//	c.load();
		//}
		// nicht betretende Gebiete...
		for(Chunk c:not){
			c.unload();
		}
		
		if(MathHelper.random()<1){// ändern, wenn Jahreszeitenlänge geändert wird
			Chunk[] c = world.getLoadedChunks();
			if(c.length>0){
				Chunk k;
				Weather.load(k=c[MathHelper.random.nextInt(c.length)], true);
				System.out.println("updated "+k.getX()*16+" . "+k.getZ()*16);
			}
		}
		
		/*boolean ok;
		Position pc;
		
		if(world != null){
			for(Chunk chunk:world.getLoadedChunks()){
				pc = new Position(BlockListener.trueLocation(new Location(world, chunk.getX()*16+8, 127, chunk.getZ()*16+8)));
				ok=false;
				s:for(Position p:poss){
					if(p.distSQy(pc, 4)<62500){
						ok=true;
						break s;
					}
				}
				if(!ok){
					chunksunloaded++;
					world.unloadChunk(chunk);
				}
			}
		}*/
		
		/*tges+=System.currentTimeMillis()-t;
		
		if(counter++%10==0 && chunksunloaded != 0){
			System.out.println("Unloaded "+chunksunloaded+" chunks :). Used "+tges+" ms.");
			tges=chunksunloaded=0;
		}*/
	}
	
	public static void removeLagRelativeTo(Player p, boolean onlyitems, boolean delitems, boolean friendlytoo){
		
		List<Entity> relatives = p.getNearbyEntities(50, 50, 50);
		int x=relatives.size();
		if(onlyitems){
			for(Entity e:relatives){
				if(e instanceof Item){
					e.remove();
					x--;
				}
			}
		} else if(x>50){
			if(friendlytoo){
				if(delitems){
					for(Entity e:relatives){
						e.remove();
						x--;
						if(x<50){
							break;
						}
					}
				} else {
					for(Entity e:relatives){
						if(!(e instanceof Item)){
							e.remove();
							x--;
							if(x<50){
								break;
							}
						}
						
					}
				}
			} else {
				if(delitems){
					for(Entity e:relatives){
						if(isHostile(e)){
							e.remove();
							x--;
							if(x<50){
								break;
							}
						}
						
					}
				} else {
					for(Entity e:relatives){
						if(isHostile(e) && !(e instanceof Item)){
							e.remove();
							x--;
							if(x<50){
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public static boolean isHostile(Entity ent){
		return ent instanceof Monster || (ent instanceof Wolf && ((Wolf)ent).isAngry());
	}
}
