package me.corperateraider.generator;

import java.util.ArrayList;

import org.bukkit.World;

public class OreManager extends WorldGen {
	
	public OreManager(){
		
		// Golderz
		ores.add(new Ore(14, 0, 41, 0.4){
			// 5000: 0
			// 2000: 0.5
			//  -1k: 0
			@Override public double fac(double d) {
				return 0.5-sq(d-2000)*0.000000111;
			}
		});
		
		// Eisenerz
		ores.add(new Ore(15, 0, 42, 0.4){
			// 5376: 0
			// 1200: 3
			// 1200' 0
			// 1000: 0
			// -> 0: 0
			@Override public double fac(double d) {
				return d<1000?0:-0.00075453*d+4.064-33.29/(d-989.94);
			}
		});
		
		// Kohleerz
		ores.add(new Ore(16, 0, 173, 0.3){
			@Override public double fac(double d) {
				return d<1000?0:-0.001333*d+10.3333-9750/(d-250);
			}
		});
		
		// Diaerz
		ores.add(new Ore(56, 0, 57, 0.2){
			//4300: 0
			//4000: 0.01
			//   0: 1
			@Override public double fac(double d) {
				return (-0.1556+1.0/(d*0.001293+0.86532))*2.2;
			}
		});
		
		// Smaragderz
		ores.add(new Ore(129, 0, 133, 0.33){
			@Override public double fac(double d) {
				return 0.2;
			}
		});
				
		// Quarzerz
		ores.add(new Ore(153, 0, 155, 0.32){
			// 1344: 0
			//  784: 5
			//  224: 0
			@Override public double fac(double d) {
				return 5.0-sq(d-784)*0.00001595;
			}
		});
		
		// Lapislazulierz :)
		ores.add(new Ore(21, 0, 22, 0.4){
			// wie Gold
			@Override public double fac(double d) {
				return 0.5-sq(d-2000)*0.000000111;
			}
		});
		
		// Redstoneerz... alias Blut
		ores.add(new Ore(73, 0, 152, 0.7){
			// auch wie Gold? -> ja :)
			// abgesehen davon sollte Gold ja seltener sein, Redstone also häufiger auftreten
			// durch Größe der Adern behoben :)
			@Override public double fac(double d) {
				return 0.5-sq(d-2000)*0.000000111;
			}
		});
	}
	
	World w;
	int cx, cz;
	public static ArrayList<Ore> ores = new ArrayList<>();
	
	public static abstract class Ore {
		public int id, data, specialID;
		public double size;
		public Ore(int id, int data, int specialID, double size){
			this.id=id;this.data=data;this.size=size;this.specialID=specialID;
		}
		
		/**
		 * Gives the chance and size of a ore-"sphere"
		 * */
		public abstract double fac(double d);
	}
	
	public Random b = new Random(0);

	@Override
	public void a(long seed, int cx, int basey, int cz, short[][] ret) {
		for(int a=-1;a<=1;a++){
			for(int b=-1;b<=1;b++){
				// berechne alle auftretenden Erzvorkommen :)
				this.b.setSeed((Random.l1*(cx+a)) ^ (Random.l2*(cz+b)) ^ (Random.l3*basey) ^ seed);
				genOres(ret, a*16, basey, b*16, cx*16, cz*16);
			}
		}
	}

	/**
	 * ist vllt schneller als per getBlock und setBlock
	 * data value is not supported
	 * */
	public void genOres(short[][] ret, int dx, int basey, int dz, int cx, int cz) {
		Random r;
		int x, y, z;
		short id, special;
		double size;
		
		for(int dy=-16;dy<272;dy+=16){
			r = new Random(cx+dx, dy+basey, cz+dz);
			if(b.next()<0.89 && b.next()>0.89){
				for(Ore o:ores){
					
					if((size = o.fac(y = r.nextInt(16) + dy + basey))>r.next()){
						
						y-=basey;
						
						id = (short) o.id;
						special = (short) o.specialID;
						size = 1+size*(r.next()*0.5+0.75);
						size *= o.size;
						
						x = r.nextInt(16) + dx;
						z = r.nextInt(16) + dz;
						
						
						double x1=r.next()*6-3, x2=r.next()*6-3, x3=r.next()*6-3, y1=r.next()*6-3, y2=r.next()*6-3, y3=r.next()*6-3, z1=r.next()*6-3, z2=r.next()*6-3, z3=r.next()*6-3,
								ssize=size*size, r2=size*1.2;
						int border=(int) (r2*1.2+2);
						int as=max(-x,-border), ae=min(16-x,border), bs=max(-y,-border), be=min(256-y,border), cs=max(-z,-border), ce=min(16-z,border);
						boolean ore=false;
						
						f:for(int a=as;a<ae;a++){
							for(int b=bs;b<be;b++){
								for(int c=cs;c<ce;c++){
									if(sq(x1-a)+sq(y1-b)+sq(z1-c)<ssize || sq(x2-a)+sq(y2-b)+sq(z2-c)<ssize || sq(x3-a)+sq(y3-b)+sq(z3-c)<ssize){// im Innerem...
										ore = true;
										break f;
									}
								}
							}
						}
						int bl;
						if(ore)for(int a=as;a<ae;a++){
							for(int b=bs;b<be;b++){
								for(int c=cs;c<ce;c++){
									if(sq(x1-a)+sq(y1-b)+sq(z1-c)<ssize || sq(x2-a)+sq(y2-b)+sq(z2-c)<ssize || sq(x3-a)+sq(y3-b)+sq(z3-c)<ssize){// im Innerem...
										if((bl=getBlock(ret, a+x, b+y, c+z))>0 && bl!=10){// alles außer Lava und Luft :)
											if(this.b.next()<0.01){
												setBlock(ret, a+x, b+y, c+z, special);
											} else {
												setBlock(ret, a+x, b+y, c+z, id);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
