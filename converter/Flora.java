package converter;

import java.util.Random;

import me.corperateraider.generator.Generator;
import me.corperateraider.generator.MathHelper;
import net.minecraft.server.v1_7_R1.Block;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;

public class Flora extends Generator{
	private static double chance;
	public static Random r = new Random(0);
	public static void generate(World w, int x, int z, java.util.Random rand){
		r = rand;
		
		x+=r.nextInt()%16;
		z+=r.nextInt()%16;
		
		int y = w.getHighestBlockYAt(x, z);
		
		chance = r.nextFloat()*100;
		
		switch(w.getBiome(x, z)){
		case BEACH://Palmen
			Palme(w,x,y,z);
			break;
		case BIRCH_FOREST://Birken + Gras
			Birkenwald(w,x,y,z,1.0);
			break;
		case BIRCH_FOREST_HILLS:
			Birkenwald(w,x,y,z,0.7);
			break;
		case BIRCH_FOREST_HILLS_MOUNTAINS:
			Birkenwald(w,x,y,z,0.3);
			break;
		case BIRCH_FOREST_MOUNTAINS:
			Birkenwald(w,x,y,z,0.5);
			break;
		case COLD_BEACH://Deathbush?
			break;
		case COLD_TAIGA:
			Fichtenwald(w,x,y,z,1.0);
			break;
		case COLD_TAIGA_HILLS:
			Fichtenwald(w,x,y,z,0.7);
			break;
		case COLD_TAIGA_MOUNTAINS:
			Fichtenwald(w,x,y,z,0.5);
			break;
		case DEEP_OCEAN://Algen?
			break;
		case DESERT://Deathbush
			DeathBush(w,x,y,z,1.0);
			break;
		case DESERT_HILLS:
			DeathBush(w,x,y,z,0.7);
			break;
		case DESERT_MOUNTAINS:
			DeathBush(w,x,y,z,0.4);
			break;
		case EXTREME_HILLS:
			Grass(w,x,y,z,0.5,false);
			break;
		case EXTREME_HILLS_MOUNTAINS:
			Grass(w,x,y,z,0.4,false);
			break;
		case EXTREME_HILLS_PLUS:
			Grass(w,x,y,z,0.27,false);
			break;
		case EXTREME_HILLS_PLUS_MOUNTAINS:
			Grass(w,x,y,z,0.15,false);
			break;
		case FLOWER_FOREST:
			Wiese(w,x,y,z,5.0);
			break;
		case FOREST:
			Eichenwald(w,x,y,z,r);
			break;
		case FOREST_HILLS:
			Eichenwald(w,x,y,z,r);
			break;
		case FROZEN_OCEAN://Eisb�ume?
			break;
		case FROZEN_RIVER://wohl ehr nix, au�er versunkene St�mme -> ehr in die Dekorationsecke
			break;
		case HELL://wird niemals aufgerufen... vllt ja doch noch
			break;
		case ICE_MOUNTAINS://Schneegl�ckchen?
			break;
		case ICE_PLAINS:
			Grass(w,x,y,z,0.1,false);
			break;
		case ICE_PLAINS_SPIKES://Schneegl�ckchen?
			break;
		case JUNGLE:
			Jungle(w,x,y,z,1.0);
			break;
		case JUNGLE_EDGE:
			Jungle(w,x,y,z,0.8);
			break;
		case JUNGLE_EDGE_MOUNTAINS:
			Jungle(w,x,y,z,0.4);
			break;
		case JUNGLE_HILLS:
			Jungle(w,x,y,z,0.7);
			break;
		case JUNGLE_MOUNTAINS:
			Jungle(w,x,y,z,0.5);
			break;
		case MEGA_SPRUCE_TAIGA:
			Fichtenwald(w,x,y,z,2.0);// >1 hei�t extrahoch
			break;
		case MEGA_SPRUCE_TAIGA_HILLS:
			Fichtenwald(w,x,y,z,1.7);
			break;
		case MEGA_TAIGA:
			Fichtenwald(w,x,y,z,2.0);
			break;
		case MEGA_TAIGA_HILLS:
			Fichtenwald(w,x,y,z,1.7);
			break;
		case MESA://nix
			break;
		case MESA_BRYCE://auch nix
			break;
		case MESA_PLATEAU://ebenfalls nix: vllt tote St�mme?
			break;
		case MESA_PLATEAU_FOREST:
			Eichenwald(w,x,y,z,r);
			break;
		case MESA_PLATEAU_FOREST_MOUNTAINS:
			Eichenwald(w,x,y,z,r);
			break;
		case MESA_PLATEAU_MOUNTAINS://nix
			break;
		case MUSHROOM_ISLAND:
			Pilze(w,x,y,z);
			break;
		case MUSHROOM_SHORE:
			Pilze(w,x,y,z);
			break;
		case OCEAN://Algen?
			break;
		case PLAINS:
			Grass(w,x,y,z,1.0,true);
			break;
		case RIVER://nix
			break;
		case ROOFED_FOREST:
			Märchenwald(w,x,y,z,1.0);
			break;
		case ROOFED_FOREST_MOUNTAINS:
			Märchenwald(w,x,y,z,0.5);
			break;
		case SAVANNA:
			Akazien(w,x,y,z,1.0);
			break;
		case SAVANNA_MOUNTAINS:
			Akazien(w,x,y,z,0.5);
			break;
		case SAVANNA_PLATEAU:
			Akazien(w,x,y,z,1.0);
			break;
		case SAVANNA_PLATEAU_MOUNTAINS:
			Akazien(w,x,y,z,0.7);
			break;
		case SKY://Ende, also erstmal nix(?)
			break;
		case SMALL_MOUNTAINS:
			Grass(w,x,y,z,0.6,false);
			break;
		case STONE_BEACH://nix
			break;
		case SUNFLOWER_PLAINS:
			Sonnenblumen(w,x,y,z);
			break;
		case SWAMPLAND:
			Eichenwald(w,x,y,z,r);
			break;
		case SWAMPLAND_MOUNTAINS:
			Eichenwald(w,x,y,z,r);
			break;
		case TAIGA:
			Fichtenwald(w,x,y,z,1.0);
			break;
		case TAIGA_HILLS:
			Fichtenwald(w,x,y,z,0.7);
			break;
		case TAIGA_MOUNTAINS:
			Fichtenwald(w,x,y,z,0.7);
			break;
		default://was wei� ich? ;)
			break;
		}
	}
	
	public static boolean Bodenstrauch(World w, int x, int y, int z){
		@SuppressWarnings("deprecation")
		int b1 = w.getBlockTypeIdAt(x,y-1,z);
		if(b1==2 || b1==3){
			for(int a=-3;a<4;a++){
				for(int b=-2;b<4;b++){
					for(int c=-3;c<4;c++){
						if(a*a+b*b*2+c*c<9){
							sB(w, x+a, y+b, z+c, 18, 3);
						}
					}
				}
			}
			sB(w, x, y, z, 17, 3);
			
			return true;
		}else return false;
	}
	
	private static void Sonnenblumen(World w, int x, int y, int z) {
		if(chance<60){
			sB(w,x,y,z,175,0);
			sB(w,x,y,z,175,15);
		}
	}
	private static void Akazien(World w, int x, int y, int z, double d) {
		if(chance<60*d)
			sB(w,x,y,z,31,0);
		else if(chance<70*d){//Akazie
			w.generateTree(new Location(w,x,y,z), TreeType.ACACIA);
		}else if(chance<80*d){//kleine Eiche
			generateTree(w, x, y, z, false, 0);
		}
	}
	private static void Märchenwald(World w, int x, int y, int z, double d) {
		if(chance<40*d){
			w.generateTree(new Location(w,x,y,z), TreeType.DARK_OAK);
		}else{
			Wiese(w,x,y,z,0.6);
		}
	}
	public static void Pilze(World w, int x, int y, int z) {
		if(chance<15){
			w.generateTree(new Location(w,x,y,z), TreeType.BROWN_MUSHROOM);
		}else if(chance<30){
			w.generateTree(new Location(w,x,y,z), TreeType.RED_MUSHROOM);
		}
	}
	
	public static void generateGiantTree(World w, int x, int y, int z, double h){
		
		//Kurvenbaum bestehend aus Kurven dritten Grades
		double size=(nextFloat()),a=0,b=0,c=0;
		size*=12*size;
		if(h!=0)size=h;
		for(int i=0;i<size/3;i++){//Wurzeln
			if(i==0){
				a=nextFloat();
				b=nextFloat();
				c=nextFloat();
			}else{
				c = a*b*c/((a=nextFloat())*(b=nextFloat()));
				if(c>1) b = c = Math.sqrt(b*c);
				if(c>1) a = c = Math.sqrt(a*c);
			}
			kurvenWurzel(w, x, y, z, nextFloat() * 6.2831853F, a, b, c, size);
		}
		h = (a * b * c) * size * 8;
		if(h>size*2.7) h = size * 2.7;
		
		/*for(int i=0;i<size;i++){
			for(int a2=-5;a2<6;a2++){
				for(int b2=-5;b2<6;b2++){
					for(int c2=-5;c2<6;c2++){
						if(a2*a2+b2*b2+c2*c2<size*0.25){
							sB(w, x+a2, y+i+(int)h+b2, z+c2, 17, 0);
						}
					}
				}
			}
		}*/
		
		// Baue eine Krone
		//float s = (float) Math.sqrt(size*0.25);
		//ast(w, r, x, y+(float)h, z, 0, MathHelper.PI, (float) size, s, s, 0.1f, 0, false);
	}
	static long g=0;
	@SuppressWarnings("deprecation")
	public static void ast(World w, Random r, float x, float y, float z, float alpha, float beta, float l, float d1, float d2, float chanceAufäste, int k, boolean blatt){
		if(d1<0.01 || l<1){return;}
		if(Float.isNaN(y)){System.out.println("NaN"+k);return;}
		
		float dx=(float) (Math.sin(alpha)*Math.cos(beta)), dy = (float) Math.sin(beta), dz=(float) (Math.cos(alpha)*Math.cos(beta)), dd=(1f*d2-d1)/l;
		
		if(++g%500==0){
			System.out.println(g+": "+k);
		}
		
		for(int i=0;i<l;i++){
			
			if(r.nextFloat()<chanceAufäste){
				ast(w, r, x+i*dx, y+i*dy, z+i*dz, alpha+r.nextFloat()*1.6f-0.8f, beta+r.nextFloat()*1.6f-0.8f, l*0.9f, d1+dd*i, (d1+dd*i)*(r.nextFloat()*0.2f+0.2f), chanceAufäste, k+1, r.nextFloat() * k > 6 || blatt);
			}
			
			if(blatt && r.nextFloat()>0.8){
				int d,e;
				for(int a=-5;a<6;a++){
					for(int b=-5;b<6;b++){
						for(int c=-5;c<6;c++){
							if(a*a+b*b+c*c<25){
								d = (int) (x+a+dx*i);
								e = (int) (z+c+dz*i);
								if(((CraftWorld)w).getHandle().getChunkAt(d>>4,e>>4).getType(d&0xf, (int) (y+b+dy*i), e&0xf)!=Block.e(17)){
									w.getBlockAt((int) (x+i*dx+a), (int) (y+i*dy+b), (int) (z+i*dz+c)).setTypeId(18);
								}
							}
						}
					}
				}
			}
			w.getBlockAt((int) (x+i*dx), (int) (y+i*dy), (int) (z+i*dz)).setTypeId(17);
			for(float a=-d1+dd*i;a<d1+dd*i;a++){
				for(float b=-d1+dd*i;b<d1+dd*i;b++){
					for(float c=-d1+dd*i;c<d1+dd*i;c++){
						if(a*a+b*b+c*c<MathHelper.sq(d1+dd*i)){
							if((int) (y+i*dy+b)>16)
								w.getBlockAt((int) (x+i*dx+a), (int) (y+i*dy+b), (int) (z+i*dz+c)).setTypeId(17);
								//sB(w, (int) (x+i*dx+a), (int) (y+i*dy+b), (int) (z+i*dz+c), 17, 0);
						}
					}
				}
			}
		}
	}
	
	private static float nextFloat(){return r.nextFloat()*0.95f+0.05f;}
	private static int sh;
	public static void kurvenWurzel(World w, int x, int y, int z, float theta, double x3, double x2, double x1, double size) {
		double fx=Math.sin(theta)*size,fz=Math.cos(theta)*size;//Faktor f�r Drehnung
		for(double i=0;i<1.27;i+=0.003){
			sh = (int)((i-x3)*(i-x2)*(i-x1)*size*8);
			if(-sh<size*2.7){
				for(int a=-5;a<6;a++){
					for(int b=-5;b<6;b++){
						for(int c=-5;c<6;c++){
							if(a*a+b*b+c*c<size/4*(1.37-i)){
								sB(w, x+(int)(fx*i)+a, y-sh+b, z+(int)(fz*i)+c, 17, 12);
							}
						}
					}
				}
			}	
		}
	}
	
	private static void kurvenWurzel(World w, int x, int y, int z, float theta, double x2, double x1, double size) {
		double fx=Math.sin(theta)*size,fz=Math.cos(theta)*size;//Faktor f�r Drehnung
		for(double i=0;i<1.27;i+=0.003){
			sh = (int)((i-x2)*(i-x1)*size*8);
			if(-sh<size*2.7){
				sB(w, x+(int)(fx*i), y-sh, z+(int)(fz*i), 17, 3);
			}
		}
	}
	public static void Palme(World w, int x, int y, int z) {
		//Kurve 2.Grades
		double size=(nextFloat()+0.5)*12,a=0,b=0;
		a=nextFloat();
		b=nextFloat();

		kurvenWurzel(w, x, y, z, nextFloat() * 6.2831853f, a, b, size);
	}
	
	private static void generateJungleTree(World w, int x, int y, int z){
		int h = r.nextInt(20)+16;
		
		// Dach
		for(int i=-6;i<7;i++){
			for(int j=-6;j<7;j++){
				for(int k=-3;k<5;k++){
					if((k<0 && k*k*4+i*i+j*j<30) || (k>=0 && k*k*8+i*i+j*j<30)){
						sB(w, x+i, y+k+h, z+j, 18, 3);
					}
				}
			}
		}
		
		// Stamm
		for(int i=h;i>-2;i--){
			sB(w,x+1,y+i,z+1,17,3);
			sB(w,x,y+i,z+1,17,3);
			sB(w,x+1,y+i,z,17,3);
			sB(w,x,y+i,z,17,3);
		}
		
		
		// Zweige
		
	}
	private static void Jungle(World w, int x, int y, int z, double d) {
		if(chance<7*d){
			generateJungleTree(w, x, y, z);
		}else if(chance<20*d){
			generateTree(w, x, y, z, chance%3==0, 3);
		}else if(chance<70*d){
			Bodenstrauch(w, x, y, z);
		}else{
			Wiese(w,x,y,z,1.0);
		}
	}
	private static void Eichenwald(World w, int x, int y, int z, Random r) {
		if(chance<7){
			generateGiantTree(w, x, y, z, 0);
		}else if(chance<20){
			generateTree(w, x, y, z, chance%5==0, 0);
		}else Wiese(w,x,y,z,0.7);
	}
	public static void Wiese(World w, int x, int y, int z, double d) {
		sB(w,x+1,y-1,z,2,0);
		sB(w,x+1,y,z,31,1);
	}
	public static void Grass(World w, int x, int y, int z, double d, boolean b) {
		if(chance<80*d){
			sB(w,x,y-1,z,2,0);
			sB(w,x,y,z,31,1);
		}else if(b){
			sB(w,x,y,z,38 - (int)chance % 10 == 9 ? 1 : 0,(int)chance % 10 == 9 ? 0: (int)chance % 10);//alle kleinen Blumentypen
		}else{
			Weihnachtsbaum(w,x,y,z,r.nextInt(100)==0);
		}
	}
	public static void DeathBush(World w, int x, int y, int z, double d) {
		if(chance<40*d){
			sB(w,x,y++,z,12,0);
			sB(w,x,y,z,31,0);
		}else if(chance<60*d){
			while(r.nextFloat()>0.3){
				sB(w,x,y++,z,81,0);
			}
		}
	}
	private static void Fichtenwald(World w, int x, int y, int z, double d) {
		if(chance<20){
			w.generateTree(new Location(w,x,y,z), TreeType.REDWOOD);
		}else if(chance<25){
			w.generateTree(new Location(w,x,y,z), TreeType.TALL_REDWOOD);
		}else Wiese(w,x,y,z,0.7);
	}
	private static void Birkenwald(World w, int x, int y, int z, double d) {
		if(chance==0){
			generateGiantTree(w, x, y, z, 0);
		}else if(chance<20){
			generateTree(w, x, y, z, false, 2);
		}else if(chance<25){
			generateTree(w, x, y, z, true, 2);
		}else Wiese(w,x,y,z,0.7);
	}
	
	private static void generateTree(World w, int x, int y, int z, boolean large, int type){//birch = Birke oder Djungel
		// Stamm
		for(int a=-3;a<10*(r.nextFloat()+(large ? 0.5 : 0.2));a++){
			sB(w, x, y++, z, 17, type);
		}
		int m = large ? 2 : 1;
		// Krone
		for(int a=-m;a<=m;a++){
			for(int b=-m+1;b<=m;b++){
				for(int c=-m;c<=m;c++){
					if((a==0 || c==0) && c<2){
						sB(w, x, y+b, z, 17, type);
					}else if((a*a+((float)b+0.5)*(b+0.5)+c*c)<m*m){
						sB(w, x, y+b, z, 18, type);
					}
				}
			}
		}
	}

	private static void Weihnachtsbaum(World w, int x, int y, int z, boolean deko){
		
		int h = r.nextInt(60)+5;
		if(deko){
			h += 20 + h/3;
		}
		
		int hk = h/5;
		
		float k=0,j;
		int i=h;
		for(;i>h/5;i-=3){
			k = h * (1-(float)i/h) * 0.2f;
			
			for(int a=-hk;a<=hk;a++){
				for(int b=-hk;b<=hk;b++){
					for(int c=0;c>-hk;c--){
						j=a*a+b*b;
						if(c==0 && 0.2+a*a+b*b<(1-(double)i/h)*2){
							sB(w, x+a, y+i, z+b, 17, 1);
							sB(w, x+a, y+i-1, z+b, 17, 1);
							sB(w, x+a, y+i-2, z+b, 17, 1);
						}else if((float)j/(c*c) > 0.8f && (float)j/(c*c)<1.25f && a*a + b*b < (float)k*k){
							sB(w, x+a, y+c+i, z+b, 18, 5);
						}
					}
				}
			}
		}
		
		for(;i>0;i--){
			for(int a=-10;a<11;a++){
				for(int b=-10;b<=11;b++){
					if(a*a+b*b<(1-(float)i/h)*3){
						sB(w, x+a, y+i, z+b, 17, 1);
					}
				}
			}
		}
		
		for(;i>-h/2;i--){
			for(int a=-10;a<11;a++){
				for(int b=-10;b<=11;b++){
					if(a*a+b*b<(1+(float)i/h*2)*3){
						sB(w, x+a, y+i, z+b, 17, 1);
					}
				}
			}
		}
		
		if(deko){
			for(int a=0;a<h*3;a++){
				float theta = r.nextFloat() * 6.28319f;//Drehung um die Achse
				float s = h * (0.1f + r.nextFloat() * r.nextFloat() * 0.8f);//Position auf der Seite
				
				int px = (int) (MathHelper.sin(theta)*(1-(float)s/h)*h*0.2f);
				int py = (int) (s-(1-(float)s/h));
				int pz = (int) (MathHelper.cos(theta)*(1-(float)s/h)*h*0.2f);
				
				float size = (float)h/60;
				int id = 35;
				int data = r.nextInt(14)+1;
				if(data==7){data=0;id=89;}
				if(data==8){data=r.nextInt(3);id=155;}
				if(data==9){
					data=r.nextInt(14)+1;
					if(data==9){
						data=0;id=22;
					}
				}
				
				
				for(int d=(int) -size;d<size;d++){
					for(int d2=(int) -size;d2<size;d2++){
						for(int d3=(int) -size;d3<size;d3++){
							if(d*d+d2*d2+d3*d3<size*size){
								sB(w, x+px+d, y+py+d2, z+pz+d3, id, data);
							}
						}
					}
				}
			}
			
			h = (int) (h*0.92);
			
			//Spitze: Stern oder so
			
			float size = (float)h/30;
			
			for(int d=(int) -size;d<size;d++){
				for(int d2=(int) -size;d2<size;d2++){
					for(int d3=(int) -size;d3<size;d3++){
						if(d*d+d2*d2+d3*d3<size*size){
							sB(w, x+d, y+d2+h, z+d3, 41, 0);
						}
					}
				}
			}
		}
	}
}
