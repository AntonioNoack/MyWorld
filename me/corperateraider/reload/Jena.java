package me.corperateraider.reload;

import org.bukkit.Bukkit;

import me.corperateraider.generator.MathHelper;
import me.corperateraider.generator.NASAHeight;
import me.corperateraider.generator.Perlin2D;
import me.corperateraider.generator.Random;

public class Jena extends MathHelper {
	
	// 1 = Stadt
	// 1-2 = Stadt+Berge
	// 2 = Berge
	// 2-3 = Berge+Wüste
	// 3 = Wüste
	// 3-4 = Wüste+Meer
	// 4 = Meer
	// 5 = Void
	
	// Keksrolle 50.929,11.5847
	// 0,00000899329100491033688868104394122
	// 0,00001426859197534387306660578734091
	public static double lng(int x){
		return 0.0000143*ori(x)+11.5846571;// 0.3% Abweichung...
	}
	
	public static double lat(int z){
		return -0.000009*ori(z)+50.928841;// 1% Abweichung -> irrelevant
	}
	
	public static double h(int x, int z){// Höhe relativ zu 0 um 4800
		
		x=ori(x);
		z=ori(z);
		
		double f = type(x, z);
		if(f==1){
			f=NASAHeight.getInterpolatedHeight(lng(x), lat(z));
			// je größer, umso mehr random...
			if(f>140){
				double d = Math.pow(f-140, 0.333)*1.2;
				return f+(m1.getNoiseAt(x, z)-0.5)*d+(m2.getNoiseAt(x, z)-0.5)*d*1.2+4660;
			} else return f+4660;
		} else if(f<2){
			f--;
			double s=NASAHeight.getInterpolatedHeight(lng(x), lat(z))-140, b=(getMountains(x, z)+3500)*f-3500;
			return (s>b?s:b)+4800;
		} else if(f==2){
			return getMountains(x, z)+4800;
		} else if(f<3){
			f-=2;
			double b=(getMountains(x, z)+3500)*(1-f)+1300, w=getDry(x, z)+4800;
			return b>w?b:w;
		} else if(f==3){
			return getDry(x, z)+4800;
		} else if(f<=4){
			f-=3;
			return getDry(x, z)*(1-f)+f*getSee(x, z)+4800;
		} else {
			return getSee(x, z)+4800;
		}
	}
	
	private static double getSee(int x, int z) {// Meer, also so 99% unter 0
		return 30-31*(m0.getNoiseAt(x, z)+m1.getNoiseAt(x, z)+m2.getNoiseAt(x, z)+m3.getNoiseAt(x, z)+m4.getNoiseAt(x, z));
	}

	public static double getDry(int x, int z) {// Wüste - eben ein paar Dünen...
		return (w2.getNoiseAt(x, 0.94f*z)+w3.getNoiseAt(x, 0.97f*z)+w4.getNoiseAt(x, z)+w5.getNoiseAt(x, z)+w6.getNoiseAt(x, z))*36;
	}

	public static double getMountains(int x, int z) {
		// in diesem Bereich wird noch eine Walze addiert, die dazu führt, dass der Rand auf jeden Fall eine durchgezogene Lavawand bildet
		if(x*x+z*z<21000*21000 && x*x+z*z>19000*19000){
			return (sq(b1.getNoiseAt(x, z))*76+b2.getNoiseAt(x, z)*91+b3.getNoiseAt(x, z)*147+b4.getNoiseAt(x, z)*265+b5.getNoiseAt(x, z)*423-200-Math.random()*5)+(1+cos(roundabs(0.001*PI*(Math.sqrt(x*x+z*z)-20000))))*200;
		}
		return sq(b1.getNoiseAt(x, z))*76+b2.getNoiseAt(x, z)*91+b3.getNoiseAt(x, z)*147+b4.getNoiseAt(x, z)*265+b5.getNoiseAt(x, z)*423-200-Math.random()*5;
	}
	
	public static double getOzeanType(int x, int z){
		return (b1.getNoiseAt(x, z)+b2.getNoiseAt(x, z))*1.5;
	}

	public static double type(int x, int z){
		
		x=ori(x);
		z=ori(z);
		
		double sq = 1.0*x*x+1.0*z*z;
		if(sq<100E6){//												10 Stadt
			return 1;
		} else if(sq<400E6){//										20 Stadt -> Berge
			return NASAHeight.getInterpolatedHeight(lng(x), lat(z))>(getMountains(x, z)+3500)*(sq=getFromMap(Math.sqrt(sq)-1.0E4, 1E4, x, z, 1))-3500?1+sq:2;
		} else if(sq<1225E6){//										35 Berge
			return 2;
		} else if(sq<3025E6){//										55 Berge -> Wüste
			return 2+getFromMap(Math.sqrt(sq)-3.5E4, 20000, x, z, 2);
		} else if(sq<7225E6){//										85 Wüste
			return 3;
		} else if(sq<13225E6){//									115 Wüste -> Meer
			return 3+getFromMap(Math.sqrt(sq)-8.5E4, 30000, x, z, 3);
		} else if(sq<84100E6){//									290 Meer
			return 4;
		} else return 5;
	}
	
	public static boolean typeMountain(double x, double z){
		return x*x+z*z>=100E6 && x*x+z*z<7225E6;
	}
	
	public static boolean chunkTypeMountain(double x, double z){
		return typeMountain(x, z) || typeMountain(x, z+15) || typeMountain(x+15, z) || typeMountain(x+15, z+15);
	}
	
	public static double type4gen(int x, int z){
		
		x=ori(x);
		z=ori(z);
		
		double sq = 1.0*x*x+1.0*z*z;
		if(sq<100E6){// Stadt
			return 1;
		} else if(sq<400E6){// Stadt -> Berge
			return 1+getFromMap(Math.sqrt(sq)-1.0E4, 1E4, x, z, 1);
		} else if(sq<1225E6){// Berge
			return 2;
		} else if(sq<3025E6){// Berge -> Wüste
			return 2+getFromMap(Math.sqrt(sq)-3.5E4, 2E4, x, z, 2);
		} else if(sq<7225E6){// Wüste
			return 3;
		} else if(sq<13225E6){// Wüste -> Meer
			return 3+getFromMap(Math.sqrt(sq)-8.5E4, 3E4, x, z, 3);
		} else if(sq<84100E6){// Meer
			return 4;
		} else return 5;
	}
	
	//						Distanz von 0|0, Strecke max
	static double getFromMap(double d, double s, int x, int z, int id) {
		/*if((s=d/s)<0.5){// in der vorderen Nase
			return 2*s*getNoiseAt(x, z, id);
		} else {// hinten...
			return 1-2*(s-1)*(getNoiseAt(x, z, id)-1);
		}*/
		return d/s;
	}
	
	private static Perlin2D sb1, sb2, sb3, bw1, bw2, bw3, wm1, wm2, wm3,
			b1, b2, b3, b4, b5, w2, w3, w4, w5, w6, m0, m1, m2, m3, m4;
	
	static boolean ini;
	public static void ini(){
		
		ini=true;
		
		Random r = new Random(Bukkit.getServer().getWorlds().get(0).getSeed());
		
		sb1 = new Perlin2D(r.nextLong(), 70);
		sb2 = new Perlin2D(r.nextLong(), 340);
		sb3 = new Perlin2D(r.nextLong(), 930);
		
		bw1 = new Perlin2D(r.nextLong(), 70);
		bw2 = new Perlin2D(r.nextLong(), 340);
		bw3 = new Perlin2D(r.nextLong(), 930);
		
		wm1 = new Perlin2D(r.nextLong(), 70);
		wm2 = new Perlin2D(r.nextLong(), 340);
		wm3 = new Perlin2D(r.nextLong(), 930);
		
		b1 = new Perlin2D(r.nextLong(), 16);
		b2 = new Perlin2D(r.nextLong(), 43);
		b3 = new Perlin2D(r.nextLong(), 108);
		b4 = new Perlin2D(r.nextLong(), 354);
		b5 = new Perlin2D(r.nextLong(), 847);
		
		w2 = new Perlin2D(r.nextLong(),  48);
		w3 = new Perlin2D(r.nextLong(),  96);
		w4 = new Perlin2D(r.nextLong(), 192);
		w5 = new Perlin2D(r.nextLong(), 384);
		w6 = new Perlin2D(r.nextLong(), 768);
		
		m0 = new Perlin2D(r.nextLong(),  50);
		m1 = new Perlin2D(r.nextLong(), 100);
		m2 = new Perlin2D(r.nextLong(), 200);
		m3 = new Perlin2D(r.nextLong(), 400);
		m4 = new Perlin2D(r.nextLong(), 800);
	}
	
	static double getNoiseAt(int x, int z, int id){
		if(!ini){ini();}
		switch(id){
		case 1:// Stadt-Berge
			return (sb1.getNoiseAt(x, z)+sb2.getNoiseAt(x, z)+sb3.getNoiseAt(x, z))/3;
		case 2:// Berge-Sand
			return (bw1.getNoiseAt(x, z)+bw2.getNoiseAt(x, z)+bw3.getNoiseAt(x, z))/3;
		case 3:// Sand-Meer
			return (wm1.getNoiseAt(x, z)+wm2.getNoiseAt(x, z)+wm3.getNoiseAt(x, z))/3;
		}
		return 0;
	}
}
