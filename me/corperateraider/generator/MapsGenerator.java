package me.corperateraider.generator;

import java.util.ArrayList;
import java.util.List;

import me.corperateraider.myworld.Plugin;
import me.corperateraider.reload.Jena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class MapsGenerator extends ChunkGenerator {
	short[][] result, filledStone;
	public static RuinPopulator ruinpop;
	
	static RuinPopulator ruin;
	
	public MapsGenerator(Plugin instance) {
		perlin();
		populators = new ArrayList<BlockPopulator>();
		populators.add(ruinpop=new RuinPopulator());
		result = new short[16][];
		for(int x=0;x<16;x++){
			for(int y=0;y<32;y++){
				for(int z=0;z<16;z++){
					setBlock(result, x, y, z, (short) 1);
				}
			}
		}
	}
	
	public Location getFixedSpawnLoaction(World w, java.util.Random neveruse){
		return new Location(w, 0, 100, 0);
	}

	ArrayList<BlockPopulator> populators;
	@Override public List<BlockPopulator> getDefaultPopulators(World world) {
		return populators;
	}
	
	public void setBlock(short[][] result, int x, int y, int z, short blockId){
		if(y<0 || y>255){
			return;
		}
		if(result[y >> 4]==null){
			result[y >> 4] = new short[4096];
		}
		result[y >> 4][((y&0xF)<<8) | (z << 4) | x] = blockId;
	}
	
	public short getBlock(short[][] result, int x, int y, int z){
		if(y<0 || y>255){
			return -1;
		}
		if(result[y >> 4]==null){
			return 0;
		}
		return result[y >> 4][((y&0xF)<<8) | (z << 4) | x];
	}
	
	public int getBlockY(short[][] result, int x, int z){
		for(int y=0;y<256;y++){
			if(result[y >> 4] != null && result[y >> 4][((y&0xF)<<8) | (z << 4) | x] != 0){
				return y;
			}
		}
		return 256;
	}
	
	@Override public byte[][] generateBlockSections(World world, java.util.Random random, int x, int z, BiomeGrid biomes) {
		System.out.println("# GBS_"+biomes.getBiome(0, 0).name());
		return new byte[world.getMaxHeight() / 16][];
	}
	
	Perlin2D ln1, ln2, ln4, ln8, lt1, lt2, lt4, lt8;
	
	public static final short
			obsidian = 49,
			endstein=121,
			stein=1,
			sand=12,
			sandstein=24,
			wasser=8,
			netherrack=87,
			netherbrick=112,
			lava=10,
			gras=2,
			clay=82,
			erde=3,
			air=0;
	
	@Override public short[][] generateExtBlockSections(World w, java.util.Random neveruse, int chunkX, int chunkZ, BiomeGrid biomes) {
		
		short[][] ret = new short[16][];
		
		long t = System.currentTimeMillis();
		
		int mx=chunkX*16, mz=chunkZ*16;
		int basey = Generator.basey(mx, mz);
		if(basey==-224) return result;
		
		mx = MathHelper.ori(mx);
		mz = MathHelper.ori(mz);

		Random r = new Random(mx, basey, mz);
		
		if(basey > 4703){
			for(int x=0;x<16;x++){
				for(int z=0;z<16;z++){//0.00024 / 0.072
					double f = Jena.type4gen(x+mx, z+mz);
					if(f==1){
						biomes.setBiome(x, z, Biome.PLAINS);
						// Stadt, also Lehm + Steinuntergrund
						// und Sandstein dann im Dekorierer, als Höhlenstreifen
						int sant = (int) Jena.h(x+mx, z+mz);
						sant -= basey;
						if(sant>0){
							setBlock(ret, x, sant--, z, gras);
							setBlock(ret, x, sant--, z, r.next()<0.7?clay:erde);
							while(r.next()>0.4)setBlock(ret, x, sant--, z, r.next()<0.7?clay:erde);
						}
						while(sant>=0){
							setBlock(ret, x, sant--, z, stein);
						}
					} else if(f<2){// Stadt -> Berge
						f--;
						
						// die Berge IMMER unter der Stadt
						int sant = (int) NASAHeight.getInterpolatedHeight(Jena.lng(MathHelper.ori(mx+x)), Jena.lat(MathHelper.ori(mz+z)))-140+4800-basey;
						int berg = (int) ((Jena.getMountains(x+mx, z+mz)+3500)*f)+1300;
						int berg995 = (int) (berg*0.99+r.next()*10)-basey;
						berg-=basey;
						
						biomes.setBiome(x, z, sant>berg?Biome.PLAINS:Biome.EXTREME_HILLS);
						
						for(int y=0;y<berg && y<256;y++){
							if(r.next()<0.01){
								setBlock(ret, x, y, z, lava);
							} else {
								if(y>berg995){
									setBlock(ret, x, y, z, netherbrick);
								} else {
									setBlock(ret, x, y, z, obsidian);
								}
							}
						}
						
						for(int y=MathHelper.max(0, berg);y<sant;y++){
							setBlock(ret, x, y, z, stein);
						}
						setBlock(ret, x, sant--, z, gras);
						setBlock(ret, x, sant--, z, r.next()<0.7?clay:erde);
						while(r.next()>0.4)setBlock(ret, x, sant--, z, r.next()<0.7?clay:erde);
						
					} else if(f==2){
						biomes.setBiome(x, z, Biome.EXTREME_HILLS);
						
						int berg = (int) Jena.getMountains(x+mx, z+mz)+4800;
						int berg995 = (int) (berg*0.99+r.next()*10)-basey;
						berg-=basey;
						
						for(int y=0;y<berg && y<256;y++){
							if(r.next()<0.01){
								setBlock(ret, x, y, z, lava);
							} else {
								if(y>berg995){
									setBlock(ret, x, y, z, netherbrick);
								} else {
									setBlock(ret, x, y, z, obsidian);
								}
							}
						}
					} else if(f<3){// Berge -> Sand
						f-=2;
						// die Berge sind IMMER unter dem Sand...
						int sant = (int) Jena.getDry(x+mx, z+mz)+4800-basey;
						int berg = (int) ((Jena.getMountains(x+mx, z+mz)+3500)*(1-f))+1300;
						int berg995 = (int) (berg*0.99+r.next()*10)-basey;
						berg-=basey;
						biomes.setBiome(x, z, berg>sant?Biome.EXTREME_HILLS:Biome.DESERT);
						int y=0;
						for(;y<berg && y<256;y++){
							if(r.next()<0.03){
								setBlock(ret, x, y, z, lava);
							} else {
								if(y>berg995){
									setBlock(ret, x, y, z, netherbrick);
								} else {
									setBlock(ret, x, y, z, obsidian);
								}
							}
						}
						sant-=3;
						for(;y<sant && y<256;y++){
							setBlock(ret, x, y, z, stein);
						}
						sant+=3;
						if(sant>berg){
							setBlock(ret, x, sant--, z, sand);
							setBlock(ret, x, sant--, z, sand);
							setBlock(ret, x, sant--, z, sand);
							do {
								setBlock(ret, x, sant--, z, sandstein);
							} while(r.next() > 0.3);
						}
						
					} else if(f==3){// Sand
						
						biomes.setBiome(x, z, Biome.DESERT);
						
						int sant = (int) Jena.getDry(x+mx, z+mz)+4800-basey;
						
						setBlock(ret, x, sant--, z, sand);
						setBlock(ret, x, sant--, z, sand);
						setBlock(ret, x, sant--, z, sand);
						
						do {
							setBlock(ret, x, sant--, z, sandstein);
						} while(r.next() > 0.3);
						
						while(sant>=0){
							setBlock(ret, x, sant--, z, stein);
						}
						
					} else if(f<=4){// Sand -> Meer
						if(f<4){
							biomes.setBiome(x, z, r.next()>f-3?r.next()*0.5>f-3?Biome.DESERT:Biome.BEACH:r.next()*0.5>f-3.5?Biome.OCEAN:Biome.DEEP_OCEAN);
						} else {
							biomes.setBiome(x, z, Biome.DEEP_OCEAN);
						}
						
						int waterline = 4800-basey;
						
						int d = (int) (r.next()*4+2);
						int my = (int) Jena.h(x+mx, z+mz) -d -basey;
						int y=0;
						
						if(my<waterline-10){// wenn um 10 kleiner als der Wasserspiegel ist: Wasseruntergrund: davor noch Strandsand :D
							double dh = Jena.getOzeanType(x+mx, z+mz);
							short dat = (short) (dh<1?3:dh<2?12:82);
							
							for(;y<my;y++){
								setBlock(ret, x, y, z, stein);
							}
							for(;d>=0;d--){
								setBlock(ret, x, y++, z, dat);
							}
							for(;y<waterline && y<256;y++){
								setBlock(ret, x, y, z, wasser);
							}
						} else {
							
							for(;y<my;y++){
								setBlock(ret, x, y, z, stein);
							}
							for(;d>=0;d--){
								setBlock(ret, x, y++, z, sand);
							}
							for(;y<waterline && y<256;y++){
								setBlock(ret, x, y, z, wasser);
							}
						}
						
					} else {// void...
						if(sq(mx+x)+sq(mz+z)<84100580001L){
							for(int y=0;y<256 && y+basey<4800;y++){
								setBlock(ret, x, y, z, obsidian);
							}
						}
					}
				}
			}
		} else if(basey == 0){// End
			double[][][] dat = new double[5][41][5];
			for(int b=0;b<41;b++){
				// ausgehend von 3 als Grenze...
				double m = new double[]{0.0,0.03,0.06,0.08,0.14,0.13,0.12,0.10,0.12,0.13,0.15,0.21,0.28,0.34,0.39,0.42,
						0.45,0.47,0.43,0.36,0.33,0.31,0.29,0.28,0.265,0.27,0.32,0.35,0.335,0.32,0.34,0.35,0.36,0.34,0.32,0.29,0.27,0.24,0.12,0.05,0.0}[b];
				for(int a=0;a<5;a++){
					for(int c=0;c<5;c++){
						dat[a][b][c] = m*(MathHelper.sq(MathHelper.sq(e1.getNoiseAt(a*4+mx, b*7, c*4+mz)))+MathHelper.sq(e2.getNoiseAt(a*4+mx, b*7, c*4+mz))+e3.getNoiseAt(a*4+mx, b*7, c*4+mz));
					}
				}
			}
			for(int a=0;a<16;a++){
				for(int b=1;b<160;b++){
					for(int c=0;c<16;c++){
						int d=a/4, e=b/4, f=c/4, a1=a%4, a2=4-a1, b1=b%4, b2=4-b1, c1=c%4, c2=4-c1;
						if(((dat[d][e][f]*c2+dat[d][e][f+1]*c1)*b2+(dat[d][e+1][f]*c2+dat[d][e+1][f+1]*c1)*b1)*a2+((dat[d+1][e][f]*c2+dat[d+1][e][f+1]*c1)*b2+(dat[d+1][e+1][f]*c2+dat[d+1][e+1][f+1]*c1)*b1)*a1>24){
							setBlock(ret, a, 30+b, c, endstein);
						}
					}
				}
			}
			for(int x=0;x<16;x++){
				for(int z=0;z<16;z++){
					
					biomes.setBiome(x, z, Biome.SKY);
					
					int y = lineNL(x+mx, z+mz)-dNL(x+mx, z+mz);
					for(int i=255;i>=y;i--){
						// lava an sich ist zwar ganz nett, doch Lavafälle passen nicht ins Ende
						setBlock(ret, x, i, z, netherrack);
					}
				}
			}
		} else if(basey == 224){// End->Nether
			for(int x=0;x<16;x++){
				for(int z=0;z<16;z++){
					
					biomes.setBiome(x, z, Biome.HELL);
					
					int y = lineNL(x+mx, z+mz)-224;
					for(int i=0;i<y;i++){
						setBlock(ret, x, i, z, r.next()<0.0003?lava:netherrack);
					}
					for(int i=y;i<256;i++){
						setBlock(ret, x, i, z, lava);
					}
				}
			}
		} else if(basey == 448 || basey == 672 || basey == 896){
			// fliegende Inseln wie im Ende, die aber dann in Soliden r.next()<0.001?lava:netherrack übergehen und an der Obsidianschicht enden
			if(basey == 448){
				int y = lineLava-basey;
				for(int x=0;x<16;x++){
					for(int z=0;z<16;z++){
						for(int i=0;i<y;i++){
							setBlock(ret, x, i, z, lava);
						}
					}
				}
			}
			double[][][] dat = new double[5][65][5];
			double dy;
			for(int y=0;y<65;y++){
				dy = (y*4+basey-448)*0.00175;
				for(int x=0;x<5;x++){
					for(int z=0;z<5;z++){
						dat[x][y][z] = dy*(MathHelper.sq(MathHelper.sq(e1.getNoiseAt(x*4+mx, (int) ((basey+y*4)*1.5f), z*4+mz)))+MathHelper.sq(e2.getNoiseAt(x*4+mx, (int) ((basey+y*4)*1.5f), z*4+mz))+e3.getNoiseAt(x*4+mx, (int) ((basey+y*4)*1.5f), z*4+mz));
					}
				}
			}
			for(int a=0;a<16;a++){
				for(int b=0;b<256;b++){
					for(int c=0;c<16;c++){
						int d=a/4, e=b/4, f=c/4, a1=a%4, a2=4-a1, b1=b%4, b2=4-b1, c1=c%4, c2=4-c1;
						if(((dat[d][e][f]*c2+dat[d][e][f+1]*c1)*b2+(dat[d][e+1][f]*c2+dat[d][e+1][f+1]*c1)*b1)*a2+((dat[d+1][e][f]*c2+dat[d+1][e][f+1]*c1)*b2+(dat[d+1][e+1][f]*c2+dat[d+1][e+1][f+1]*c1)*b1)*a1>24){
							setBlock(ret, a, b, c, r.next()<0.0003?lava:netherrack);
						}
					}
				}
			}
			for(int a=0;a<16;a++){
				for(int b=0;b<16;b++){
					biomes.setBiome(a, b, Biome.HELL);
				}
			}
		} else if(basey == 1120 || basey == 1344){// Stein, Obsidian, r.next()<0.001?lava:netherrack
			
			if(Jena.chunkTypeMountain(mx, mz)){// in den Bergen
				for(int x=0;x<16;x++){
					for(int z=0;z<16;z++){
						int berg = (int) ((Jena.getMountains(x+mx, z+mz)+3500)*(Jena.type4gen(mx+x, mz+z)-2))+1300;
						int berg995 = (int) (berg*0.99+r.next()*10)-basey;
						int y1=lineDN(mx+x, mz+z)-basey;
						berg-=basey;
						
						int y=0;
						if(berg995>255)
							berg995=255;
						if(berg>255)
							berg=255;
						if(y1>255)
							y1=255;
						
						
						for(;y<y1;y++){
							setBlock(ret, x, y, z, r.next()<0.003?lava:netherrack);
						}
						for(;y<berg995;y++){
							setBlock(ret, x, y, z, r.next()<0.01?lava:obsidian);
						}
						for(;y<berg;y++){
							setBlock(ret, x, y, z, stein);
						}
						for(;y<256;y++){
							setBlock(ret, x, y, z, stein);
						}
					}
				}
			} else {
				for(int x=0;x<16;x++){
					for(int z=0;z<16;z++){
						
						int y1=lineDN(mx+x, mz+z)-basey, y2=y1+dDN(mx+x, mz+z);
						int y=0;
						for(;y<y1;y++){
							setBlock(ret, x, y, z, r.next()<0.001?lava:netherrack);
						}
						for(;y<y2;y++){
							setBlock(ret, x, y, z, obsidian);
						}
						for(;y<256;y++){
							setBlock(ret, x, y, z, stein);
						}
					}
				}
			}
			
			for(int x=0;x<16;x++){
				for(int z=0;z<16;z++){
					biomes.setBiome(x, z, Biome.HELL);
				}
			}
		} else {//zwischen 1568 und 4703
			
			if(Jena.chunkTypeMountain(mx, mz)){// in den Bergen
				for(int x=0;x<16;x++){
					for(int z=0;z<16;z++){
						int berg = (int) ((Jena.getMountains(x+mx, z+mz)+3500)*(Jena.type4gen(mx+x, mz+z)-2))+1300;
						int berg995 = (int) (berg*0.99+r.next()*10)-basey;
						berg-=basey;
						
						int y=0;
						if(berg995>255)
							berg995=255;
						if(berg>255)
							berg=255;
						
						for(;y<berg995;y++){
							setBlock(ret, x, y, z, r.next()<0.01?lava:obsidian);
						}
						for(;y<berg;y++){
							setBlock(ret, x, y, z, r.next()<0.01?lava:netherbrick);
						}
						for(;y<256;y++){
							setBlock(ret, x, y, z, stein);
						}
					}
				}
			} else {
				for(int i=0;i<16;i++){
					for(int j=0;j<16;j++){
						biomes.setBiome(i, j, Biome.STONE_BEACH);
					}
				}
				for(int i=0;i<16;i++){
					for(int j=0;j<256;j++){
						for(int k=0;k<16;k++){
							setBlock(ret, i, j, k, stein);
						}
					}
				}
			}
		}
		
		long worldseed = w.getSeed();
		
		ores.a(worldseed, mx/16, basey, mz/16, ret);
		
		long seed = w.getSeed();
		
		if(basey>=1120){
			// Höhlen :D
			caves.a(	seed, mx/16, basey, mz/16, ret);
			canyon.a(	seed, mx/16, basey, mz/16, ret);
			deco.a(		seed, mx/16, basey, mz/16, ret);
			
		} else {
			deco.a(		seed, mx/16, basey, mz/16, ret);
		}
		
		generated++;
		if(generated%100==0){
			System.out.println("+");
		}
		
		timeused+=System.currentTimeMillis()-t;
		if(timeused>10000){
			System.out.println("G "+generated+" in "+timeused);
			timeused = generated = 0;
		}
		
		return ret;
	}
	
	static long timeused;
	static int generated;
	
	static WorldGenDecorator deco = new WorldGenDecorator();
	static WorldGenCanyon canyon = new WorldGenCanyon();
	static WorldGenCaves caves = new WorldGenCaves();
	static OreManager ores = new OreManager();
	
	
	static final double f16 = 1.0/16.0;
	
	static double middle(double a, double b, double c, double d, double x, double z){
		return (a*x+b*(1-x))*z + (c*x+d*(1-x))*(1-z);
	}
	
	static int sq(int i){return i*i;}
	static long sq(long l){return l*l;}
	
	static int lineLava = 512;
	
	static int lineDN(int x, int z){// 1200 +/- 100 Nether-Normal
		return (int) (1100+66.67*(n1.getNoiseAt(x, z)+n2.getNoiseAt(x, z)+n3.getNoiseAt(x, z)));
	}
	
	static int dDN(int x, int z){// 10 bis 100 dicke Nether-Normal
		return (int) (10+30*(n4.getNoiseAt(x, z)+n5.getNoiseAt(x, z)+n6.getNoiseAt(x, z)));
	}
	
	static int lineNL(int x, int z){// 256 +/- 10 Obergrenzlinie der Lava... -> besser Antilagg, wenn nicht so dick...+150
		return (int) (246+7*(l1.getNoiseAt(x, z)+l2.getNoiseAt(x, z)+l3.getNoiseAt(x, z)))+150;
	}
	
	static int dNL(int x, int z){// 10 bis 120 -> besser Antilagg, wenn nicht so dick...+150
		return (int) (10+2*MathHelper.sq(l4.getNoiseAt(x, z)+l5.getNoiseAt(x, z)+l6.getNoiseAt(x, z)))+150;
	}
	
	public static double H(int index, int x, int z){
		// Höhe zwischen 1668 und 4603; delta = 3135, Mitte auch :)
		switch(index){
		case 0:return 3135+3135*((h00.getNoiseAt(x, z)+h01.getNoiseAt(x, z)+h02.getNoiseAt(x, z)+h03.getNoiseAt(x, z))*0.5-1);
		case 1:return 3135+3135*((h10.getNoiseAt(x, z)+h11.getNoiseAt(x, z)+h12.getNoiseAt(x, z)+h13.getNoiseAt(x, z))*0.5-1);
		case 2:return 3135+3135*((h20.getNoiseAt(x, z)+h21.getNoiseAt(x, z)+h22.getNoiseAt(x, z)+h23.getNoiseAt(x, z))*0.5-1);
		}
		return Math.sin(x*0.02)*Math.sin(z*0.02)*100+2800*index;
	}
	
	static double fH(int index, int x, int z){
		switch(index){
		case 0:return (f00.getNoiseAt(x, z)+f01.getNoiseAt(x, z)+f02.getNoiseAt(x, z))*0.33333;
		case 1:return (f10.getNoiseAt(x, z)+f11.getNoiseAt(x, z)+f12.getNoiseAt(x, z))*0.33333;
		case 2:return (f20.getNoiseAt(x, z)+f21.getNoiseAt(x, z)+f22.getNoiseAt(x, z))*0.33333;
		}
		return 0;
	}
	
	static final int step = 3;
	public boolean isCutting05(int x, int y, int z){
		int g = getNoiseAt(x, y, z)<0.5?0:1+
				getNoiseAt(x+step, y, z)<0.5?0:1+
				getNoiseAt(x, y+step, z)<0.5?0:1+
				getNoiseAt(x+step, y+step, z)<0.5?0:1+
				getNoiseAt(x, y, z+step)<0.5?0:1+
				getNoiseAt(x+step, y, z+step)<0.5?0:1+
				getNoiseAt(x, y+step, z+step)<0.5?0:1+
				getNoiseAt(x+step, y+step, z+step)<0.5?0:1;
		return g>0 && g<8;
	}
	
	static double getNoiseAt(int x, int y, int z){
		return 0.33*(h1.getNoiseAt(x, y, z)+h2.getNoiseAt(x, y, z)+h3.getNoiseAt(x, y, z));
	}
	
	static void perlin(){
		Random r = new Random(Bukkit.getServer().getWorlds().get(0).getSeed());
		h1 = new Perlin3D(r.nextLong()&0xffffffff,25);
		h2 = new Perlin3D(r.nextLong()&0xffffffff,76);
		h3 = new Perlin3D(r.nextLong()&0xffffffff, 127);
		rh = new Perlin3D(r.nextLong()&0xffffffff, 423);
		
		// The END
		e1 = new Perlin3D(r.nextLong()&0xffffffff, 23);
		e2 = new Perlin3D(r.nextLong()&0xffffffff, 41);
		e3 = new Perlin3D(r.nextLong()&0xffffffff, 89);
		
		// Default-Netherline
		n1 = new Perlin2D(r.nextLong()&0xffffffff, 23);
		n2 = new Perlin2D(r.nextLong()&0xffffffff, 41);
		n3 = new Perlin2D(r.nextLong()&0xffffffff, 89);
		// Default-Netherline D
		n4 = new Perlin2D(r.nextLong()&0xffffffff, 23);
		n5 = new Perlin2D(r.nextLong()&0xffffffff, 41);
		n6 = new Perlin2D(r.nextLong()&0xffffffff, 89);
		
		// Lavaline
		l1 = new Perlin2D(r.nextLong()&0xffffffff,  6);
		l2 = new Perlin2D(r.nextLong()&0xffffffff, 12);
		l3 = new Perlin2D(r.nextLong()&0xffffffff, 24);
		// Lavaline D
		l4 = new Perlin2D(r.nextLong()&0xffffffff,  6);
		l5 = new Perlin2D(r.nextLong()&0xffffffff, 12);
		l6 = new Perlin2D(r.nextLong()&0xffffffff, 24);
		
		// Höhlen
		h00 = new Perlin2D(r.nextLong()&0xffffffff,  91);
		h01 = new Perlin2D(r.nextLong()&0xffffffff, 351);
		h02 = new Perlin2D(r.nextLong()&0xffffffff, 641);
		h03 = new Perlin2D(r.nextLong()&0xffffffff, 879);
		
		f00 = new Perlin2D(r.nextLong()&0xffffffff,  67);
		f01 = new Perlin2D(r.nextLong()&0xffffffff, 125);
		f02 = new Perlin2D(r.nextLong()&0xffffffff, 379);
		
		h10 = new Perlin2D(r.nextLong()&0xffffffff,  91);
		h11 = new Perlin2D(r.nextLong()&0xffffffff, 351);
		h12 = new Perlin2D(r.nextLong()&0xffffffff, 641);
		h13 = new Perlin2D(r.nextLong()&0xffffffff, 879);
		f10 = new Perlin2D(r.nextLong()&0xffffffff,  67);
		f11 = new Perlin2D(r.nextLong()&0xffffffff, 125);
		f12 = new Perlin2D(r.nextLong()&0xffffffff, 379);
		
		h20 = new Perlin2D(r.nextLong()&0xffffffff,  91);
		h21 = new Perlin2D(r.nextLong()&0xffffffff, 351);
		h22 = new Perlin2D(r.nextLong()&0xffffffff, 641);
		h23 = new Perlin2D(r.nextLong()&0xffffffff, 879);
		f20 = new Perlin2D(r.nextLong()&0xffffffff,  67);
		f21 = new Perlin2D(r.nextLong()&0xffffffff, 125);
		f22 = new Perlin2D(r.nextLong()&0xffffffff, 379);
	}
	
	static Perlin3D h1, h2, h3, rh, e1, e2, e3;
	static Perlin2D n1, n2, n3, n4, n5, n6, l1, l2, l3, l4, l5, l6, h00, h01, h02, h03, f00, f01, f02, h10, h11, h12, h13, f10, f11, f12, h20, h21, h22, h23, f20, f21, f22;

	public short shortByHeight(int u, int y){
		if(u==0){
			return 8;
		}else if(y< 5) return 12;
		else if(y<100) return 2;
		else if(y<150) return 1;
		else if(y<200) return 80;
		else return 79;
	}
}
