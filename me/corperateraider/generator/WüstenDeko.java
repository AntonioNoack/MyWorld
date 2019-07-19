package me.corperateraider.generator;

import me.corperateraider.myworld.Tree;
import me.corperateraider.reload.Jena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

public class WüstenDeko extends Tree {
	
	/**
	 * See, umrundet etwas von Gras, Brunnen 20%ig mit Goldblock drinnen + Palmen...							
	 * */
	public static boolean generateOase(World w, int x, int y, int z, int cx, int basey, int cz) {
		
		y = (int) Jena.h(x, z);
		if(y<4800) return false;
		y-=basey;
		
		Random r = new Random(x, basey+y, z);
		
		double
		r1=r.next()*5+3, r2=r.next()*5+3, r3=r.next()*5+3, 
		r4=r1*(r.next()*0.7+1.2), r5=r2*(r.next()*0.7+1.2), r6=r3*(r.next()*0.7+1.2),
		r7=r4*(r.next()*0.7+1.2), r8=r5*(r.next()*0.7+1.2), r9=r6*(r.next()*0.7+1.2),
		x1=r.next()*8-4, x2=r.next()*8-4, x3=r.next()*8-4, y1=r.next()*8-4, y2=r.next()*8-4, y3=r.next()*8-4, rx = sq(r7+r8+r9);
	
		
		
		for(int a=-48;a<49;a++){
			for(int b=-48;b<49;b++){
				if(sq(a-x1)+sq(b-y1)<sq(r1) || sq(a-x2)+sq(b-y2)<sq(r2) || sq(a-x3)+sq(b-y3)<sq(r3)){// im Teich
					set(w, cx, cz, x+a, y+2, z+b, 0, 0);
					set(w, cx, cz, x+a, y+1, z+b, 0, 0);
					set(w, cx, cz, x+a, y, z+b, 0, 0);
					set(w, cx, cz, x+a, y-1, z+b, 8, 0);
					set(w, cx, cz, x+a, y-2, z+b, 3, 0);
					set(w, cx, cz, x+a, y-3, z+b, 12, 0);
				} else if(sq(a-x1)+sq(b-y1)<sq(r4) || sq(a-x2)+sq(b-y2)<sq(r5) || sq(a-x3)+sq(b-y3)<sq(r6)){// auf dem Gras
					
					double fy=(a*a+b*b)*1.0/rx, h;
					int ty = MathHelper.max((int) ((h=Jena.h(x+a, z+b)-basey)*fy+(1-fy)*y), (int) h);
					
					set(w, cx, cz, x+a, ty  , z+b, 2, 0);
					set(w, cx, cz, x+a, ty-1, z+b, 3, 0);
					set(w, cx, cz, x+a, ty-2, z+b, 3, 0);
					set(w, cx, cz, x+a, ty-3, z+b, 12, 0);
					set(w, cx, cz, x+a, ty-4, z+b, 12, 0);
					
					if(x+a>=0 && z+b>=0 && x+a<cx+16 && z+b<cz+16){
						w.setBiome(x+a, z+b, Biome.PLAINS);
					}
					
					if(r.next()<0.4){
						set(w, cx, cz, x+a, ty+1, z+b, 31, 1);
					} else if(r.next()<0.3){
						int[] blume = RuinPopulator.blume(r);
						set(w, cx, cz, x+a, ty+1, z+b, blume[0], blume[1]);
					} else if(r.next()<0.03){// Palme :)
						generatePalme(w, x+a, ty, z+b, cx, basey, cz);
					}
				} else if(a*a+b*b<rx){// Gras-Sandgemisch
					
					double fy=(a*a+b*b)*1.0/rx, h;
					int ty = MathHelper.max((int) ((h=Jena.h(x+a, z+b)-basey)*fy+(1-fy)*y), (int) h);
					
					if(r.next()*rx>a*a+b*b){
						
						set(w, cx, cz, x+a, ty  , z+b, 2, 0);
						set(w, cx, cz, x+a, ty-1, z+b, 3, 0);
						set(w, cx, cz, x+a, ty-2, z+b, 12, 0);
						
						if(x+a>=0 && z+b>=0 && x+a<cx+16 && z+b<cz+16){
							w.setBiome(x+a, z+b, Biome.PLAINS);
						}
					} else {
						set(w, cx, cz, x+a, ty-2, z+b, 24, 0);
						set(w, cx, cz, x+a, ty-1, z+b, 12, 0);
						set(w, cx, cz, x+a, ty  , z+b, 12, 0);
					}

					set(w, cx, cz, x+a, ty+1, z+b, 0, 0);
				}
			}
		}
		
		int a = (int) (r.next()*20+4);
		int b = (int) ((r.next()*0.4+0.1)*a);
		
		x+=(int) ((r.next()-0.5)*16);
		z+=(int) ((r.next()-0.5)*16);
		
		for(int d=0;d<a;d++){
			set(w, cx, cz, x+1, y-d, z+1, 24, 0);
			set(w, cx, cz, x+1, y-d, z  , 24, 0);
			set(w, cx, cz, x+1, y-d, z-1, 24, 0);
			set(w, cx, cz, x-1, y-d, z+1, 24, 0);
			set(w, cx, cz, x-1, y-d, z  , 24, 0);
			set(w, cx, cz, x-1, y-d, z-1, 24, 0);
			set(w, cx, cz, x  , y-d, z+1, 24, 0);
			if(d>b){
				set(w, cx, cz, x, y-d, z, 8, 0);
			}
			set(w, cx, cz, x, y-d, z-1, 24, 0);
		}
		
		if(r.next()<0.1){
			set(w, cx, cz, x, y-a, z-1, 57, 0);
		} else
			set(w, cx, cz, x, y-a, z-1, 41, 0);
		
		return true;
	}
	
	public static boolean generateObelisk(World w, int x, int y, int z, int cx, int basey, int cz) {
		
		y = (int) Jena.h(x, z);
		if(y<4800) return false;
		y-=basey;
		
		Random r = new Random(x, basey+y, z);
	
		
		int h = (int) (sq(r.next())*16+6);
		double rd = Math.sqrt(h);
		for(int a=(int) (-rd-1);a<rd;a++){
			for(int b=(int) (-rd-1);b<rd;b++){
				if(a*a+b*b<h){
					if(a*a+b*b<0.5*h){
						if(a*a+b*b!=0){
							set(w, cx, cz, x+a, y, z+b, 8, 0);
						}
					} else {
						set(w, cx, cz, x+a, y, z+b, 24, 2);
					}
					for(int i=1;i<4;i++){
						set(w, cx, cz, x+a, y-i, z+b, 24, 0);
					}
				}
				
			}
		}
		for(int a=0;a<h;a++){
			set(w, cx, cz, x, y+a, z, 24, 1);
		}
		rd = r.next();
		if(rd<0.2){
			set(w, cx, cz, x, y+h, z, 42, 0);
		} else if(rd<0.4){
			set(w, cx, cz, x, y+h, z, 22, 0);
		}  else if(rd<0.6){
			set(w, cx, cz, x, y+h, z, 8, 0);
		} else if(rd<0.78){
			set(w, cx, cz, x, y+h, z, 152, 0);
		} else if(rd<0.95){
			set(w, cx, cz, x, y+h, z, 41, 0);
		} else {
			set(w, cx, cz, x, y+h, z, 57, 0);
		}
		
		return true;
	}
	
	public static boolean generatePyramide(World w, int x, int y, int z, int cx, int basey, int cz) {

		y = (int) Jena.h(x, z);
		if(y<4800) return false;
		y-=basey;
		
		Random r = new Random(x, basey+y, z);
		
		// die W�nde drumherum
		
		int size = (int) (sq(r.next())*20+5);
		
		int id = r.nextBoolean()?22:r.next()<0.8?41:57;
		
		for(int i=-size;i<=size;i++){
			int d = size-Math.abs(i);// durchmesser
			for(int j=-d;j<=d;j++){
				if(i>0 && d==0){
					set(w, cx, cz, x, y+i, z, id, 0);
				} else if(i>1 && d==1){
					for(int a=-1;a<2;a++){
						set(w, cx, cz, x+j, y+i, z+a, id, 0);
					}
				} else if(i==1 || i==2 || (i>4 && i%4==3)){
					for(int k=-d;k<=d;k++){
						set(w, cx, cz, x+j, y+i, z+k, 24, 0);
					}
				} else {
					set(w, cx, cz, x+j, y+i, z+d, 24, 0);
					set(w, cx, cz, x+j, y+i, z-d, 24, 0);
					
					set(w, cx, cz, x+d, y+i, z+j, 24, 0);
					set(w, cx, cz, x-d, y+i, z+j, 24, 0);
				}
			}
		}
		y+=2;

		set(w, cx, cz, x, y  , z, 8, 0);
		set(w, cx, cz, x, y+1, z, 0, 0);
		set(w, cx, cz, x, y+2, z, 0, 0);
		set(w, cx, cz, x, y+3, z, 0, 0);
		if(x<cx+16 && x>=cx && z>=cz && z<cz+16){
			for(int i=size;i>0;i--){
				w.spawnEntity(new Location(w, x+0.5, y+1, z+0.5), EntityType.VILLAGER);
			}
		}
		return true;
	}
	
	public static boolean generateTempel(World w, int x, int y, int z, int cx, int basey, int cz) {
		
		y = (int) Jena.h(x, z);
		if(y<4800) return false;
		y-=basey;
		
		
		
		return true;
	}
}
