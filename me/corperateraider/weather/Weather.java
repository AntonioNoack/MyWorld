package me.corperateraider.weather;

import java.util.ArrayList;
import java.util.List;

import me.corperateraider.generator.Generator;
import me.corperateraider.generator.MathHelper;
import me.corperateraider.generator.Perlin2D;
import me.corperateraider.generator.Random;
import me.corperateraider.myworld.Plugin;
import me.corperateraider.reload.Jena;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import converter.MetaString;

/**
 * Der Manager für die Temperatur, das Wachstum und den Schnee in der Stadt
 * */

public class Weather extends MathHelper {
	
	public static ArrayList<Remember> todo = new ArrayList<>();
	private static ArrayList<Remember> next;
	
	static boolean calculating = false;
	
	public static void ini(){
		Random r = new Random(Random.l2);
		s1 = new Perlin2D(r.nextLong(), 4);
		s2 = new Perlin2D(r.nextLong(), 8);
		s3 = new Perlin2D(r.nextLong(), 16);
	}
	
	@SuppressWarnings({"deprecation"})
	public static boolean load(Chunk c, boolean updateothers){
		
		if(c.getWorld()!=Plugin.world) return true;
		
		WeatherTree.makeBush(c, false);
		
		if(!c.isLoaded()){
			return false;
		}
		
		long timer = System.currentTimeMillis();
		int basey;
		if(Jena.type4gen(c.getX()*16, c.getZ()*16)<2 && (basey=Generator.basey(c.getX()*16, c.getZ()*16))>4703){// in der Stadt
			
			int thistime, lasttime;
			double that, r, delta = (that=wachstumsFaktor(thistime=jetztTime()))-wachstumsFaktor(lasttime=getTime(c));
			int deltatime = Math.abs(thistime-lasttime);
			
			if(deltatime>0){// die Chance auf Veränderung liegt bei mehr als 1 Block
				
				c.getBlock(0, 0, 0).setMetadata(MetaString.weather, new FixedMetadataValue(Plugin.instance, jetztTime()));
				
				World w=c.getWorld();
				int bx = c.getX()*16, bz = c.getZ()*16;
				
				Random rand = new Random(bx, 0, bz);
				
				for(int i=0;i<16;i++){
					for(int j=0;j<16;j++){
						
						r=rand.next()*0.1-0.05;
						// 1.2 bis -0.4, Mitte also bei 0.4
						if(that+r<0.0){//Schneegrenze :)
							//0-05 = 0
							// -10 = 1
							// -15 = 2
							// -20 = 3
							// -25 = 4
							// -30 = 5
							
							if(random()<abs(delta)*2){// ab delta = 0.5 zu 100%
								if(allowedToChange(w.getBiome(bx+i, bz+j)))
									w.setBiome(bx+i, bz+j, Biome.COLD_TAIGA);
							} else if(random()<abs(delta)){// ab delta = 1 zu 100%
								if(allowedToChange(w.getBiome(bx+i, bz+j)))
									w.setBiome(bx+i, bz+j, Biome.PLAINS);
							}
							
							int y = w.getHighestBlockYAt(bx+i, bz+j)-1;
							Block b = w.getBlockAt(bx+i, y, bz+j);
							
							int h = maxSnow(b);
							if(h==8){
								if(b.getType()==Material.SNOW){
									b.setType(Material.AIR);
								}
							} else {
								// 0 > that = wachstumsfaktor... > -0.4
								h = (int) (((that+r)*(-20) - h) * perl(i+bx, j+bz));
								double add = Jena.h(bx+i, bz+j)-basey;
								
								if(abs(add-y)<2){
									h+=(add-(int)add)*8-2;
								}
								
								h = min(max(h, 0), 7);
								
								if(y>0){
									int m;
									if((m=b.getTypeId())!=78 && m!=0 && ((m=w.getBlockAt(bx+i, y+1, bz+j).getTypeId())==78 || m==0)){
										w.getBlockAt(bx+i,y+1,bz+j).setTypeIdAndData(78, (byte) h, false);
									}
								}
							}
							
						} else if(that+r<0.8){// Freundlich
							if(random()<abs(delta*3)){
								if(allowedToChange(w.getBiome(bx+i, bz+j)))
									w.setBiome(bx+i, bz+j, Biome.PLAINS);
							}
							
							Block b = w.getHighestBlockAt(bx+i, bz+j);
							if(b.getType()==Material.SNOW){
								b.setType(Material.AIR);
							}
						} else {// Sommer
							if(random()<abs(delta)*2){// ab delta = 0.5 zu 100%
								if(allowedToChange(w.getBiome(bx+i, bz+j)))
									w.setBiome(bx+i, bz+j, Biome.JUNGLE);
							} else if(random()<abs(delta)){// ab delta = 1 zu 100%
								if(allowedToChange(w.getBiome(bx+i, bz+j)))
									w.setBiome(bx+i, bz+j, Biome.PLAINS);
							}
				
							Block b = w.getHighestBlockAt(bx+i, bz+j);
							if(b.getType()==Material.SNOW){
								b.setType(Material.AIR);
							}
						}
					}
				}
				
				generated++;
				if(generated%100==0){
					System.out.println("  +");
				}
			}
		}
		
		timeused+=System.currentTimeMillis()-timer;
		if(timeused>10000){
			System.out.println("W "+generated+" in "+timeused);
			timeused = generated = 0;
		}
		
		if(!calculating && updateothers && todo.size()>0 && lasttested<System.currentTimeMillis()){
			calculating = true;
			
			next = new ArrayList<>(todo);
			
			long time = System.currentTimeMillis();
			for(Remember r:next){
				if(r.t<time && !r.done){
					r.done=load(r.c, false);
					r.t = System.currentTimeMillis()+1000;
				}
			}
			for(int i=next.size()-1;i>=0;i--){
				if(next.get(i).done){
					next.remove(i);
				}
			}
			
			todo = next;
			lasttested = System.currentTimeMillis()+1000;
			calculating = false;
		}
		return true;
	}
	
	static boolean allowedToChange(Biome b){
		return b==Biome.PLAINS || b==Biome.JUNGLE || b==Biome.COLD_TAIGA;
	}
	static long timeused, lasttested;
	static int generated;
	
	public static class Remember {
		long t;boolean done;
		Chunk c;
		public Remember(Chunk c, long time){
			t=time;this.c=c;
		}
	}
	
	public static class RememberMakeBushes {
		long t;boolean done;
		Chunk c;
		public RememberMakeBushes(Chunk c, long time){
			t=time;this.c=c;
		}
	}
	
	static Perlin2D s1, s2, s3;
	static double perl(int x, int z){
		return (s1.getNoiseAt(x, z)+s2.getNoiseAt(x, z)+s3.getNoiseAt(x, z))*0.33333;
	}
	
	/**
	 * gibt den maximalen Schneespiegel zurück.
	 * 8 =   0% = niemals Schnee
	 * 0 = 100% = maximaler Schnee
	 * -> wird von der eigentlichen Höhe abgezogen :)
	 * */
	@SuppressWarnings("deprecation")
	public static int maxSnow(Block b) {
		// ab 11 nach mc-Wiki bildet sich kein Schnee mehr...
		int r=b.getLightFromBlocks();
		if(r>10){
			return 8;
		} else {
			return max(r-2, exclusiveID(b.getTypeId()));
		}
	}
	
	public static int exclusiveID(int id){
		switch(id){
		case 0:
		case 6:
		case 8:
		case 9:
		case 10:
		case 11:
		case 25:
		case 26:
		case 27:
		case 28:
		case 30:// Spinnennetze...
		case 31:
		case 37:
		case 38:
		case 39:
		case 40:
		case 43://-> ja so kann man die Felder frei halten, ist also gut so
		case 44:
		case 50:
		case 51:
		case 54:
		case 55:
		case 59:
		case 63:
		case 64:
		case 65:
		case 66:
		case 68:
		case 69:
		case 70:
		case 71:
		case 72:
		case 75:
		case 76:
		case 77:
		case 78:// auf Schnee logischer Weise kein weiterer Schnee :)
		case 81:
		case 90:// naja
		case 91:
		case 92:
		case 93:
		case 94:
		case 96:
		case 104:
		case 105:
		case 106:
		case 111:
		case 115:
		case 117:
		case 119:// naja
		case 120:
		case 122:
		case 126:
		case 127:
		case 131:
		case 132:
		case 138:
		case 140:
		case 141:
		case 142:
		case 144:
		case 145:
		case 146:
		case 147:
		case 148:
		case 149:
		case 150:
		case 151:
		case 157:
		case 160:
		case 171:
		case 175:
		case 182:
		//case   4:// nein! ist für Straßen zwar sehr gut so, aber lieber Straßen aus doppelten Halbstufen -> damit sind diese eindeutiger gebaut und wir brauchen das hier nicht
			return 8;
		case 48:
			return 4;
		}
		return 0;
	}

	public static void unload(Chunk c){
		// könnte zwar sagen, ob es schon geschneit hat, aber naja... vllt unter einem anderem Wert...
		//c.getBlock(0, 0, 0).setMetadata(MetaString.weather, new FixedMetadataValue(Plugin.instance, jetztTime()));
	}
	
	/**
	 * Höhepunkt an Frühling-Sommer, tief bei Herbst-Winter
	 * von 1.2 bis -0.4
	 * */
	public static double wachstumsFaktor(Chunk c){
		if(Jena.h(c.getX()*16, c.getZ()*16)<2){
			return cos(Tf256*jetztTime())*0.8+0.4;
		} else return 0.3;
	}
	
	public static double wachstumsFaktor(int time){
		return cos(Tf256*time)*0.8+0.4;
	}
	
	public static String nowTime(boolean english){
		int t = jetztTime();
		switch(t/32){
		case 0:return english?"§esummer(late)" :"§eSommer(spät)";
		case 1:return english?"§afall(early)"  :"§aHerbst(früh)";
		case 2:return english?"§2fall(late)"   :"§2Herbst(spät)";
		case 3:return english?"§7winter(early)":"§7Winter(früh)";
		case 4:return english?"§7winter(late)" :"§7Winter(spät)";
		case 5:return english?"§2spring(early)":"§2Frühling(früh)";
		case 6:return english?"§aspring(late)" :"§aFrühling(spät)";
		case 7:return english?"§esummer(early)":"§eSommer(früh)";
		}
		return "unknown";
	}
	
	public static int getTime(Chunk c){
		List<MetadataValue> l = c.getBlock(0, 0, 0).getMetadata(MetaString.weather);
		if(l.size()>0){
			return l.get(0).asByte();
		} else {
			return 0;
		}
	}
	
	public static boolean isChildAndReady(Chunk c){
		return c.isLoaded() && !c.getBlock(0, 0, 0).hasMetadata(MetaString.weather);
	}
	
	public static void setTime(int time){
		delta = 0;
		delta = (int) (256 + time - jetztTime());
	}
	
	public static int delta=0;
	
	/**
	 * 248.936 bei 13,25d am 18.12. um 10:12, d.h....
	 * noch 6d bis Weihnachten, ca 2h
	 * dann sinds... 117,535 mehr, d.h. 366.471 d.h.110, d.h. beginnender Winter, d.h. perfekt :D
	 * 
	 * Zeit:<br>
	 * 0=256:	Hochsommer<br>
	 *  64:		Herbst<br>
	 * 128:		Winter<br>
	 * 192: 	Frühling<br>
	 * <br>
	 * Schnee von 96 bis 160
	 * */
	
	public static int jetztTime(){// nicht vergessen im Antilaggteil auf die Chunkupdatefunktion zu achten :)
		//return (int) (((System.currentTimeMillis()/4471875)+delta)&0xff); // 6.21 Tage -> nein doch lieber genau 13.25 Tage, damit 1 Jahr ~ 2 Wochen bzw. 19 Wochen beträgt (besser für die allgemeine Rechnung, da es so egal ob 2 Wochenendtage oder 1 Wochenendtag ist)
		//return (int) (((System.currentTimeMillis()>>12)+delta)&0xff);// etwa 5min
		//return (int)(((((System.currentTimeMillis()>>10) * 199) >> 19)+delta)&0xff);// 7.99 Tage <- Unsinn! :D
		return 200;
	}
	
	public static boolean isWinter(){
		int j = jetztTime();
		return j>=96 && j<160;
	}



}
