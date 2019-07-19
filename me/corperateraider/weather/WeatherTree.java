package me.corperateraider.weather;

import me.corperateraider.generator.Generator;
import me.corperateraider.generator.MathHelper;
import me.corperateraider.generator.Random;
import me.corperateraider.myworld.Plugin;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import converter.MetaString;

public class WeatherTree extends MathHelper {
	
	public static final int max = 16384;
	public static Task[] todo = new Task[max];
	public static int todoWork, todoAdd;
	
	static class Task {
		Chunk c;
		boolean fast;
		public Task(Chunk c, boolean fast){
			this.c=c;this.fast=fast;
		}
	}
	
	static boolean workedLast = false;
	public static void tick(double tps){
		if(tps>10 && tps<21){// nicht überlastet und auch nicht nach einer überlasteten Phase
			for(int i=0;i<tps-10;i++){// sehr gut :)
				todoWork = min(todoAdd, todoWork)+1;
				Task t = todo[todoWork&(max-1)];
				
				if(todoWork>todoAdd){
					if(workedLast){
						workedLast = false;
					}
					break;
				} else if(t!=null){
					
					if((todoWork&(max-1))==0){
						System.out.println("WT working... "+todoWork);
					}
					
					// wenn keine Zeit benötigt wurde, weil z.B. die Chunks nicht geladen waren, wird nur 1/10 Schritt zu TPS-10 gegangen
					if(!makeBushNeededTime(t.fast, t.c) && MathHelper.random()>0.3){
						i--;
					}
					todo[todoWork&0xfff]=null;
					
					workedLast = true;
				}
			}
		}
	}
	
	public static final boolean t=true, f=false;
	public static boolean[][][] bush = new boolean[][][]
	{{{f,f,f,f,f},{f,t,t,t,f},{f,t,t,t,f},{f,f,f,f,f},{f,f,f,f,f}}
	,{{f,f,t,f,f},{t,t,t,t,t},{t,t,t,t,t},{f,f,t,f,f},{f,f,f,f,f}}
	,{{f,t,t,t,f},{t,t,t,t,t},{t,t,t,t,t},{f,t,t,t,f},{f,f,f,f,f}}
	,{{f,f,t,f,f},{t,t,t,t,t},{t,t,t,t,t},{f,f,t,f,f},{f,f,f,f,f}}
	,{{f,f,f,f,f},{f,t,t,t,f},{f,t,t,t,f},{f,f,f,f,f},{f,f,f,f,f}}
	},
	bush2 = new boolean[][][]
	{{{t,t,t,t,t},{t,t,t,t,t},{f,f,f,f,f},{f,f,f,f,f},{f,f,f,f,f}}
	,{{t,t,t,t,t},{t,t,t,t,t},{f,t,t,t,f},{f,f,t,f,f},{f,f,f,f,f}}
	,{{t,t,t,t,t},{t,t,t,t,t},{f,t,t,t,f},{f,t,t,t,f},{f,f,f,f,f}}
	,{{t,t,t,t,t},{t,t,t,t,t},{f,t,t,t,f},{f,f,t,f,f},{f,f,f,f,f}}
	,{{t,t,t,t,t},{t,t,t,t,t},{f,f,f,f,f},{f,f,f,f,f},{f,f,f,f,f}}
	};
	
	public static double fac(){
		return Weather.wachstumsFaktor(Weather.jetztTime());
	}
	
	static int skipped;
	public static void makeBush(Chunk chunk, boolean fast){

		todoAdd++;
		if(todoAdd>=todoWork+max){
			// wir haben keinen PLatz mehr und müssten altes Zeugs überschreiben...
			if(++skipped%1000==0){
				System.out.println("WT skipped 1k");
			}
			todoWork=todoAdd-max+1;// vllt wird einer übersehen... aber auf jedenfall wird nichts doppelt durchlaufen
		}
		
		todo[todoAdd&0xfff] = new Task(chunk, fast);
	}
	
	static boolean br;
	
	@SuppressWarnings("deprecation")
	private static boolean makeBushNeededTime(boolean fast, Chunk chunk){
		
		World w = chunk.getWorld();
		int bx=chunk.getX()*16, bz=chunk.getZ()*16;
		
		if(chunk.isLoaded() &&
				w.getChunkAt(bx+16, bz).isLoaded() &&
				w.getChunkAt(bx, bz+16).isLoaded() &&
				w.getChunkAt(bx-16, bz).isLoaded() &&
				w.getChunkAt(bx, bz-16).isLoaded() &&
				w.getChunkAt(bx+16, bz+16).isLoaded() &&
				w.getChunkAt(bx-16, bz+16).isLoaded() &&
				w.getChunkAt(bx-16, bz+16).isLoaded() &&
				w.getChunkAt(bx-16, bz-16).isLoaded()){
			
			if(chunk.getBlock(0, 0, 0).hasMetadata(MetaString.weatherTree) && MathHelper.abs(chunk.getBlock(0, 0, 0).getMetadata(MetaString.weatherTree).get(0).asInt()-Weather.jetztTime())<2){
				return false;
			}
			
			chunk.getBlock(0, 0, 0).setMetadata(MetaString.weatherTree, new FixedMetadataValue(Plugin.instance, Weather.jetztTime()));
			
			// irgendwas hierrunter verursacht neues Chunkladen... :( oje...
			int basey = basey(bx, bz);
			double fac = fac();
			
			int[][][] res = new int[24][270][24];
			if(fac>0){
				for(int x=-2;x<18;x++){
					for(int z=-2;z<18;z++){
						for(int y=0;y<256;y++){
							Block block;
							if(x>=0 && x<16 && z>=0 && z<16){
								block = chunk.getBlock(x, y, z);
							} else {
								// die Stelle nicht unbedingt...
								block = w.getBlockAt(bx+x, y, bz+z);
							}
							if(block.getType()==Material.LOG){
								
								int data = block.getData();
								
								boolean[][][] add = null;
								if(data%2==0){// Birke oder Eiche :)
									if(block.getData()>3){
										add = makeBush(byBlock(block, basey), fac, t);
									} else {
										while(++y<256 && chunk.getBlock(x, y, z).getType()==Material.LOG){}
										if(y<256){
											add = makeBush(byBlock(block, basey), fac, f);
										} else add = new boolean[5][5][5];
									}
								} else continue;
								
								data = (data&0x3)+1;
								
								x+=2;
								z+=2;
								for(int a=0;a<5;a++){
									for(int b=0;b<5;b++){
										for(int c=0;c<5;c++){
											if(add[a][b][c]){
												res[a+x][b+y][c+z] = data;
											}
										}
									}
								}
								x-=2;
								z-=2;
							}
						}
					}
				}
			}
			// hier drunter auch nicht unbedingt
			Block b;int type;
			for(int x=0;x<16;x++){
				for(int y=0;y<256;y++){
					for(int z=0;z<16;z++){
						if((type=res[x+4][y+1][z+4])>0){
							if((b=chunk.getBlock(x, y, z)).getType()==Material.AIR){// Blätter werden gesetzt :)
								if(fast){
									Generator.sB(w, x, y, z, 18, type-1);
								} else {
									b.setTypeIdAndData(18, (byte)(type-1), false);
								}
							}
						} else {
							byte data;
							if((b=chunk.getBlock(x, y, z)).getType()==Material.LEAVES && ((data=b.getData())&4)!=4 && data%2==0){// Blätter werden gelöscht, falls vorhanden...
								if(fast){
									Generator.sB(w, x, y, z, 0, 0);
								} else {
									b.setType(Material.AIR);
								}
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public static Random byBlock(Block bl, int basey){
		int x=bl.getX(), y=bl.getY(), z=bl.getZ();
		return new Random(x, basey+y, z);
		
	}
	
	/**
	 * one = schräger Block?
	 * */
	public static boolean[][][] makeBush(Random r, double fac, boolean one){
		boolean[][][] bush = one?WeatherTree.bush:WeatherTree.bush2;
		boolean[][][] q = new boolean[5][5][5], w;
		q[2][1][2] = true;
		
		if(fac>=1){
			return bush;
		} else if(fac<=0){
			return q;
		} else {
			for(int i=0;i<4;i++){
				w = new boolean[5][5][5];
				for(int b=0;b<5;b++){
					for(int a=0;a<5;a++){
						for(int c=0;c<5;c++){
							if(q[a][b][c]){
								w[a][b][c]=true;
								if(a>0 && r.next()<fac && bush[a-1][b][c]){w[a-1][b][c]=true;}
								if(b>0 && r.next()<fac && bush[a][b-1][c]){w[a][b-1][c]=true;}
								if(c>0 && r.next()<fac && bush[a][b][c-1]){w[a][b][c-1]=true;}
								
								if(a<4 && r.next()<fac && bush[a+1][b][c]){w[a+1][b][c]=true;}
								if(b<4 && r.next()<fac && bush[a][b+1][c]){w[a][b+1][c]=true;}
								if(c<4 && r.next()<fac && bush[a][b][c+1]){w[a][b][c+1]=true;}
							}
						}
					}
				}
				q = w;
			}
		}
		return q;
	}
}
