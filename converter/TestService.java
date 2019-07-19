package converter;

import java.util.HashMap;

import me.corperateraider.generator.OreManager;
import me.corperateraider.generator.Random;

public class TestService extends OreManager {
	
	static int[][] result;
	
	public static void main(String[] args){
		HashMap<Integer, String> names = new HashMap<>();
		double[] def = new double[8];
		//						anzahl*max/Chunk -> schnitt -> meins 		-> 1:	 20 wären schön :), vllt auch Kohle häufiger
		names.put(14, "Gold ");def[0]= 2*9/2;//    9			   61/k		-> 1:	147.5
		names.put(15, "Eisen");def[1]= 20*9/2;//  90			17687/k		-> 1:	  5.1
		names.put(16, "Kohle");def[2]=20*17/2;// 170			 9017/k		-> 1:	 18.9
		names.put(56, "Dia  ");def[3]= 1*8/2;//    4			   22.9/k	-> 1:	174.6
		names.put(129,"Smara");def[4]= 5.0/2;//    2			    1.9/k	-> 1:  1052.6
		names.put(153,"Quarz");def[5]= 16*14/2;//112			11000/k		-> 1:	 10.2
		names.put(21, "Lapis");def[6]= 1*7/2;//    3			  128/k		-> 1:	 23.4
		names.put(73, "RedSt");def[7]= 8*8/2;//   32			  197/k		-> 1:	162.4
		
		
		long seed;
		Random.ini(seed=System.currentTimeMillis());
		
		result = new int[256][25];
		
		TestService ore = new TestService();
		
		// 200 * 200 * 25 = 1Mio
		
		for(int basey=0;basey<25;basey++){
			
			// Menge in 10k Chunks...
			for(int i=0;i<200;i++){
				for(int j=0;j<200;j++){
					ore.a(seed, i, basey*224, j, null);
				}
			}
		}
		
		int k=0;
		for(Ore o:ores){
			int[] dat1 = result[o.id];
			int[] dat2 = result[o.specialID];
			
			// erstelle einen Graphen für jedes Erz...
			int ges=0, best=-1;
			for(int i=0;i<25;i++){
				ges=dat1[i]+9*dat2[i];
				if(ges>best){
					best = ges;
				}
				System.out.println(ges);
			}
			ges = best*25;
			System.out.println(names.get(o.id)+" "+(ges+500)/1000+"k/Mio");
			System.out.println("\t1:"+def[k++]*1000000/ges);
		}
	}
	
	@Override
	public void a(long seed, int cx, int basey, int cz, short[][] ret) {
		this.b.setSeed((Random.l1*(cx)) ^ (Random.l2*(cz)) ^ (Random.l3*basey) ^ seed);
		genOres(ret, 0, basey, 0, cx*16, cz*16);
	}
	
	@Override
	public void genOres(short[][] ret, int dx, int basey, int dz, int cx, int cz) {
		Random r;
		int x, y, z;
		short id, mantel, special;
		double size;
		
		for(int dy=-16;dy<272;dy+=16){
			r = new Random(cx+dx, dy+basey, cz+dz);
			if(b.next()<0.89 && b.next()>0.89){
				for(Ore o:ores){
					
					if((size = o.fac(y = r.nextInt(16) + dy + basey))>r.next()){
						
						y-=basey;
						
						id = (short) o.id;
						mantel = (short) (o.id==56?49:o.id==153?87:1);
						special = (short) o.specialID;
						size = 1+size*(r.next()*0.5+0.75);
						size *= o.size;
						
						x = r.nextInt(16) + dx;
						z = r.nextInt(16) + dz;
						
						
						double x1=r.next()*6-3, x2=r.next()*6-3, x3=r.next()*6-3, y1=r.next()*6-3, y2=r.next()*6-3, y3=r.next()*6-3, z1=r.next()*6-3, z2=r.next()*6-3, z3=r.next()*6-3,
								ssize=size*size, r2=size*1.2, sr2=r2*r2;
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
						
						if(ore)for(int a=as;a<ae;a++){
							for(int b=bs;b<be;b++){
								for(int c=cs;c<ce;c++){
									if(sq(x1-a)+sq(y1-b)+sq(z1-c)<ssize || sq(x2-a)+sq(y2-b)+sq(z2-c)<ssize || sq(x3-a)+sq(y3-b)+sq(z3-c)<ssize){// im Innerem...
										if(this.b.next()<0.01){
											result[special][basey/224]++;
										} else {
											result[id][basey/224]++;
										}
									} else if(sq(x1-a)+sq(y1-b)+sq(z1-c)<sr2 || sq(x2-a)+sq(y2-b)+sq(z2-c)<sr2 || sq(x3-a)+sq(y3-b)+sq(z3-c)<sr2){// Schale...
										if(y+basey<4600){
											result[mantel][basey/224]++;
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
