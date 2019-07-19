package me.corperateraider.myworld;

import me.corperateraider.generator.DrehMatrix;
import me.corperateraider.generator.MathHelper;
import me.corperateraider.generator.Random;
import me.corperateraider.weather.Weather;
import me.corperateraider.weather.WeatherTree;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import converter.Converter;

public class Tree extends MathHelper {
	public static enum TreeType {
		TREE, BUSH, BIRCH, BIGTREE, REALOAK, FANTASY, GIANT, MANGROVE, PALME, FICHTE, DARKOAK, ACACIA, RANDOM, FALLENFICHTE;
	}
	
	/**
	 * Baumgenerator, aber NUR für den Weltgenerator, da er sonst an gleicher Stelle gleiche Bäume plaziert
	 * */
	public static boolean generateTree(World w, int x, int y, int z, int cx, int basey, int cz, TreeType type){
		
		if(y<-64 || y>256+64){
			return false;
		}
		
		switch(type){
		case ACACIA:
			break;
		case BIGTREE:
			return generateBigTree(w, x, y, z, cx, basey, cz);
		case BIRCH:
			return generateTree(w, x, y, z, cx, basey, cz, 2);
		case BUSH:
			return generateBush(w, x, y, z, cx, basey, cz, 0);
		case DARKOAK:
			break;
		case FANTASY:
			break;
		case FALLENFICHTE:
			//return generateFallenFichte(w, x, y, z, cx, basey, cz);
		case FICHTE:
			return generateFichte(w, x, y, z, cx, basey, cz);
		case GIANT:
			break;
		case MANGROVE:
			break;
		case PALME:
			return generatePalme(w, x, y, z, cx, basey, cz);
		case RANDOM:
			return generateTree(w, x, y, z, cx, basey, cz, TreeType.values()[new Random(x, y+basey, z).nextInt(TreeType.values().length-1)]);
		case REALOAK:
			break;
		case TREE:
			return generateTree(w, x, y, z, cx, basey, cz, 0);
		default:
			break;
		}
		return false;
	}

	protected static boolean generatePalme(World w, int x, int y, int z, int cx, int basey, int cz) {
		// Palme by Keralis https://www.youtube.com/watch?v=5Msfca6Sv5Q
		y++;
		Random r = new Random(x, basey+y, z);
		
		Location zero = new Location(w, x, y, z);
		
		DrehMatrix m = new DrehMatrix().rotY(r.nextInt(4)*Tf4);
		
		set(w, cx, cz, m.add(zero, 1,-1, 1), LOG, 3);
		set(w, cx, cz, m.add(zero, 1,-1, 0), LOG, 3);
		set(w, cx, cz, m.add(zero, 0,-1, 1), LOG, 3);
		
		set(w, cx, cz, m.add(zero, 1, 0, 1), LOG, 3);
		set(w, cx, cz, m.add(zero, 1, 0, 0), LOG, 3);
		set(w, cx, cz, m.add(zero, 0, 0, 1), LOG, 3);
		
		set(w, cx, cz, m.add(zero, 0, 1, 0), LOG, 3);
		set(w, cx, cz, m.add(zero, 1, 1, 0), LOG, 3);
		set(w, cx, cz, m.add(zero, 0, 1, 1), LOG, 3);
		
		set(w, cx, cz, m.add(zero, 0, 2, 0), LOG, 3);
		if(r.nextBoolean()){set(w, cx, cz, m.add(zero, 1, 2, 0), LOG, 3);
		} else {			set(w, cx, cz, m.add(zero, 0, 2, 1), LOG, 3);}
		
		set(w, cx, cz, m.add(zero, 0, 3, 0), LOG, 3);

		set(w, cx, cz, m.add(zero, 0, 4, 0), LOG, 3);
		set(w, cx, cz, m.add(zero,-1, 4,-1), LOG, 3);

		set(w, cx, cz, m.add(zero,-1, 5,-1), LOG, 3);

		set(w, cx, cz, m.add(zero,-1, 6,-1), LOG, 3);
		// nun kommt der nächste Stumpf drauf... also nächste Drehmatrix xD
		DrehMatrix m2 = new DrehMatrix().rotY(r.nextInt(4)*Tf4);
		for(int i=0;i<5;i++){
			set(w, cx, cz, m2.add(m.add(zero, -2, 6, -2), 0, i, 0), LOG, 3);
		}
		
		set(w, cx, cz, m2.add(m.add(zero, -2, 6, -2), 0, 1, 0).add( 1, 0, 0), kakao, 1+r.nextInt(3)*4);// an Ostwand, also +x
		set(w, cx, cz, m2.add(m.add(zero, -2, 6, -2), 0, 1, 0).add( 0, 0, 1), kakao, 2+r.nextInt(3)*4);// an Südwand, also +z
		set(w, cx, cz, m2.add(m.add(zero, -2, 6, -2), 0, 1, 0).add(-1, 0, 0), kakao, 3+r.nextInt(3)*4);// an Weswand, also -x
		set(w, cx, cz, m2.add(m.add(zero, -2, 6, -2), 0, 1, 0).add( 0, 0,-1), kakao, 0+r.nextInt(3)*4);// an Norwand, also -z
		
		for(int i=-2;i<3;i++){
			if(i==0)i++;
			set(w, cx, cz, m.add(zero, -2, 6, -2).add(i, 4, 0), LOG, 3+4);
			set(w, cx, cz, m.add(zero, -2, 6, -2).add(0, 4, i), LOG, 3+8);
			
			set(w, cx, cz, m.add(zero, -2, 6, -2).add(i, 5, 0), LEAVES, 7);
			set(w, cx, cz, m.add(zero, -2, 6, -2).add(0, 5, i), LEAVES, 7);
		}
		set(w, cx, cz, m.add(zero, -2, 6, -2).add(0, 5, 0), LEAVES, 7);
		// Blätter...
		for(int i=-3;i<4;i++){
			for(int j=-3;j<4;j++){
				if(i*i+j*j<13){
					set(w, cx, cz, m2.add(m.add(zero, -2, 6, -2), i, 4, j), LEAVES, 7);
				}
				if(i*i+j*j<5){
					set(w, cx, cz, m2.add(m.add(zero, -2, 6, -2), i, 3, j), LEAVES, 7);
				}
				if(i*i+j*j==1){
					set(w, cx, cz, m2.add(m.add(zero, -2, 6, -2), i, 2, j), LEAVES, 7);
				}
			}
		}
		DrehMatrix m3;
		double s = r.next()*T;
		for(int i=0;i<8;i++){
			m3 = new DrehMatrix().rotY(s+0.5*i*Tf4+r.next()*0.18);
			set(w, cx, cz, m3.add(m2.add(m.add(zero, -2, 6, -2), 0, 3, 0), 2, 0, 3), LEAVES, 7);
			set(w, cx, cz, m3.add(m2.add(m.add(zero, -2, 6, -2), 0, 3, 0), 3, 0, 3), LEAVES, 7);
			set(w, cx, cz, m3.add(m2.add(m.add(zero, -2, 6, -2), 0, 3, 0), 2, 0, 4), LEAVES, 7);
			int l1=r.next()<0.4?3:2, l2=l1+(r.next()<0.3?4:(r.next()<0.4?3:2));
			
			for(int j=0;j<l1;j++){
				set(w, cx, cz, m3.add(m2.add(m.add(zero, -2, 6, -2), 0, 2, 0), 3, -j, 4), LEAVES, 7);
			}
			for(int j=l1-1;j<l2;j++){
				set(w, cx, cz, m3.add(m2.add(m.add(zero, -2, 6, -2), 0, 2, 0), 3.8, -j, 4.8), LEAVES, 7);
			}
		}
		
		return true;
	}

	private static boolean generateFichte(World w, int x, int y, int z, int cx, int basey, int cz){
		Random random = new Random(x, basey+y, z);
		
		if(random.next()<0.05){
			int l = random.nextIntSQ(10)+3;
			double alpha=random.next()*T, dx=sin(alpha), dz=cos(alpha);
			int type = (dx>dz?4:8)+1;
			for(int i=0;i<l;i++){
				int ax=(int) (x+i*dx), az=(int) (z+i*dz);
				if(ax>=cx && ax<cx+16 && az>=cz && az<cz+16){
					set(w, cx, cz, ax, w.getHighestBlockYAt(ax, az), az, LOG, type);
				}
			}
			return true;
		}
		
		int h = random.nextInt(random.nextInt(20))+4;
		for(int i=-3;i<h;i++){
			set(w, cx, cz, x, y+i, z, LOG, 1);
		}
		h--;
		
		set(w, cx, cz, x-1, y+h, z, LEAVES, 1);
		set(w, cx, cz, x+1, y+h, z, LEAVES, 1);
		set(w, cx, cz, x, y+h, z-1, LEAVES, 1);
		set(w, cx, cz, x, y+h, z+1, LEAVES, 1);
		set(w, cx, cz, x, y+h+1, z, LEAVES, 1);
		
		int sq, sq2;
		int k=2;
		for(int i=3;i<h;i+=k,k++){
			for(int c=1;c<k;c++){
				sq=(int) ((c+0.5)*(c+0.5));
				sq2=c*c/6;
				for(int a=-c-1;a<c+1;a++){
					for(int b=-c-1;b<c+1;b++){
						if(a*a+b*b<sq){
							if(a*a+b*b<sq2){
								set(w, cx, cz, x+a, y+h+k-i-c, z+b, LOG, 1);
							} else {
								set(w, cx, cz, x+a, y+h+k-i-c, z+b, LEAVES, 1);
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private static boolean generateBigTree(World w, int x, int y, int z, int cx, int basey, int cz){
		
		if(Converter.getRealCubeID(x, z)==30)return false;
		
		Random r = new Random(z^y, basey+y, x^y);
		
		boolean[][][] leaves = new boolean[20][260][20];
		
		int h = 5+r.nextInt(5);
		int i = -2;
		for(;i<=h;i++){
			set(w, cx, cz, x, y+i, z, LOG, 0);
		}
		
		for(i=3;i<h;i++){
			double alpha = r.next()*T*1000;
			double mx = sin(alpha)*0.999, mz = cos(alpha)*0.999, my = r.next();
			int type = mx>mz?4:8, tx, ty, tz;
			int l = r.nextInt(4)+2;
			for(int j=1;j<l;j++){
				set(w, cx, cz, tx=x+(int)(mx*j), ty=y+i+(int)(my*j), tz=z+(int)(mz+j), LOG, type);
				boolean[][][] add = WeatherTree.makeBush(new Random(tx, ty+basey, tz), Weather.wachstumsFaktor(Weather.jetztTime()), true);
				// mach es besser vorstellbar :)
				tx-=cx;
				ty--;
				tz-=cz;
				int aa=max(0,-tx), ab=max(0,-ty), ac=max(0,-tz), ba=min(5,20-tx), bb=min(5,260-ty), bc=min(5,20-tz);
				for(int a=aa;a<ba;a++){
					for(int b=ab;b<bb;b++){
						for(int c=ac;c<bc;c++){
							if(add[a][b][c]){
								leaves[a+tx][b+ty][c+tz]=true;
							}
						}
					}
				}
			}
		}
		
		boolean[][][] add = WeatherTree.makeBush(new Random(x, y+h+basey, z), Weather.wachstumsFaktor(Weather.jetztTime()), false);
		// macht es besser vorstellbar :)
		int tx=x-cx, ty=h+y-1, tz=z-cz;
		
		int aa=max(0,-tx), ab=max(0,-ty), ac=max(0,-tz), ba=min(5,20-tx), bb=min(5,260-ty), bc=min(5,20-tz);
		for(int a=aa;a<ba;a++){
			for(int b=ab;b<bb;b++){
				for(int c=ac;c<bc;c++){
					if(add[a][b][c]){
						leaves[a+tx][b+ty][c+tz]=true;
					}
				}
			}
		}
		
		for(int a=2;a<18;a++){
			for(int b=2;b<258;b++){
				for(int c=2;c<18;c++){
					if(leaves[a][b][c]){
						set(w, cx, cz, cx+a-2, b, cz+c-2, LEAVES, 0);
					}
				}
			}
		}
		
		return true;
	}
	
	private static boolean generateTree(World w, int x, int y, int z, int cx, int basey, int cz, int type){
		return littleBush(w, x, y, z, cx, basey, cz, type, new Random(x, y, z).nextInt(4)+2);
	}
	
	public static boolean littleBush(World w, int x, int y, int z, int cx, int basey, int cz, int type, int h){
		
		if(Converter.getRealCubeID(x, z)==30)return false;
		
		for(int i=-2;i<h;i++){
			set(w, cx, cz, x, y+i, z, LOG, type);
		}
		
		if(type%2==0){// Djungel und Spruce brauchen das ja nicht :)
			generateBush(w, x, y+h, z, cx, basey, cz, type);
		}
		
		return true;
	}
	
	private static boolean generateBush(World w, int x, int y, int z, int cx, int basey, int cz, int type){
		
		set(w, cx, cz, x, y, z, LOG, type);
		
		boolean[][][] add = WeatherTree.makeBush(new Random(x, y+basey, z), Weather.wachstumsFaktor(Weather.jetztTime()), type>3);
		x-=2;
		y--;
		z-=2;
		for(int a=0;a<5;a++){
			for(int b=0;b<5;b++){
				for(int c=0;c<5;c++){
					if(add[a][b][c]){
						set(w, cx, cz, x+a, y+b, z+c, LEAVES, type);
					}
				}
			}
		}
		x+=2;
		y++;
		z+=2;
		
		return true;
	}
	
	public static void setTypeAndData(World world, int x, int y, int z, int id, int data) {
		setTypeAndData(world.getBlockAt(x,y,z), id, data);
	}
	
	@SuppressWarnings("deprecation")
	private static void setTypeAndData(Block block, int id, int data) {
		block.setTypeIdAndData(id, (byte) data, false);
	}
}
