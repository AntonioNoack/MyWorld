package me.corperateraider.dynmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import me.corperateraider.generator.MathHelper;
import me.corperateraider.myworld.Plugin;
import me.corperateraider.reload.Jena;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ReloadChunks {
	//seed 1249939948139613719
	static World world;
	public static void ini(){
		world = Plugin.world;
	}
	
	static final int s = 65536;//1024*1024/16
	
	/**
	 * +x und +z muss gerechnet werden damit man die echten Chunkkooridnaten bekommt
	 * 0: 4704-4928
	 * 1: 4928-5152
	 * 2: 5152-5376
	 * 3: 5376-5600..32
	 * */
	static final int[]
			x = new int[]{s, s, 0, 0},
			z = new int[]{0, s, s, 0};
	
	static class L {
		boolean fast;
		int x, z, zoom; public L(int x, int z, int zoom, boolean fast){
			this.x=x;this.z=z;this.zoom=zoom;this.fast=fast;
		}
		
		@Override
		public int hashCode(){
			return (16777216*zoom+65536*x+z)*(fast?1:-1);
		}
		
		@Override
		public String toString(){
			return x+"."+z+"."+zoom+"."+fast;
		}
	}
	
	public static HashMap<L, byte[]> cache = new HashMap<>();
	
	/**
	 * x.z.size.fast
	 * (zoom wird wohl soetwas wie Detailrate, da man aus weiter Entfernung wirklich nicht jeden Block braucht)
	 * */
	public static byte[] getData(String request) {
		
		if(request.length()<3)return "Fehler: ungültige Anfrage".getBytes();
		String[] pts = request.split("\\.");
		if(pts.length<4){
			return "Fehler: nicht genug Koordinaten".getBytes();
		} else System.out.println("ok "+request);
		// gibt irgendwie die Daten aller übereinanderliegenden Schichten an... das Rendern übernimmt trotzdem der Client...
		// Ansicht? macht der Client -> real 3D :D
		int x=MathHelper.stringToInt(pts[0], 0), z=MathHelper.stringToInt(pts[1], 0), size=MathHelper.stringToInt(pts[2], 16);
		if(size>255){
			return new byte[]{};
		}
		boolean fast = pts[3].equalsIgnoreCase("true") || pts[3].equals("0");
		System.out.println(x+"."+z+"."+size+"."+fast);
		byte[] ret;
		try {
			ret = chunkMap(x, z, size, fast);
		} catch (IOException e){
			e.printStackTrace();
			ret = new byte[]{};
		}
		return ret;
	}
	
	/**
	 * gibt eine Karte von 16x16 Chunks zurück
	 * das sind 256x256 Blöcke...
	 * @throws IOException 
	 * */
	public static byte[] chunkMap(int firstX16, int firstZ16, int size, boolean fast) throws IOException{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(0xff);// Reihenfolge für Js
		stream.write(0xfe);
		if(fast){
			for(int x=0;x<size;x++){
				for(int z=0;z<size;z++){
					chunkFast(firstX16+x, firstZ16+z, stream);
				}
			}
		} else {
			for(int x=0;x<size;x++){
				for(int z=0;z<size;z++){
					chunk(firstX16+x, firstZ16+z, stream);
				}
			}
		}
		
		// damit das letzte UTF-16 Zeichen auch ja komplett ist :)
		if(stream.size()%2==1){
			stream.write(0x00);
		}
		
		return stream.toByteArray();
	}
	
	public static void chunkFast(int x16, int z16, ByteArrayOutputStream out) throws IOException{
		out.write(int16((int) Jena.h(x16*16+8, z16*16+8)));
		double type = Jena.type(x16*16+8, z16*16+8);// geht ja bis 5...
		if(type==5){// Spezialfall, dass man außerhalb der Karte ist...
			out.write(0x0);
		} else {
			out.write((byte)(1+(int) (type*250)));
		}
	}
	
	/**
	 * schreibt einen Chunk1024 in den Stream
	 * metadaten? sind die wichtig??? vllt später
	 * @throws IOException 
	 * */
	@SuppressWarnings("deprecation")
	public static void chunk(int x16, int z16, ByteArrayOutputStream out) throws IOException{
		int cx16, cz16;
		for(int y4=0;y4<4;y4++){
			cx16 = x[y4]+x16; cz16 = z[y4]+z16;
			if(world.loadChunk(cx16, cz16, false)){// der Chunk wurde schon generiert... -> warte mal 1x heißt doch nicht alles...
				// es handelt sich um ein echtes Stück chunk...
				Chunk c = world.getChunkAt(cx16, cz16);
				if(c.isLoaded()){
					out.write(0x00);
					out.write(int16((int) Jena.h(x16*16+8, z16*16+8)));
					// schreibe für alle Chunkstreifen ihre pseudoIDs, vllt auch etwas komprimiert
					int ymax=y4==3?256:224;
					int[][][] towrite = new int[16][16][ymax+1];
					for(int x=0;x<16;x++){
						for(int z=0;z<16;z++){
							towrite[x][z][ymax] = (int) Jena.h(x16*16+x, z16*16+z)-(4707+y4*224);
							for(int y=0;y<ymax;y++){
								Block b = c.getBlock(x, y, z);
								towrite[x][z][y]=pseudoID(b.getTypeId(), b.getData());
							}
						}
					}
					for(int x=0;x<16;x++){
						for(int z=0;z<16;z++){
							int y=0;
							while(y<ymax && (x==15 || towrite[x+1][z][y]!=0) && (x==0 || towrite[x-1][z][y]!=0) && (z==15 || towrite[x][z+1][y]!=0) && (z==0 || towrite[x][z-1][y]!=0) && towrite[x][z][y+1]!=0){
								towrite[x][z][y]=1;
								y++;
							}
						}
					}
					for(int x=0;x<16;x++){
						for(int z=0;z<16;z++){
							int hy = MathHelper.min(towrite[x][z][ymax], ymax), y=0;
							while(y<hy && isStone(towrite[x][z][y])){
								y++;
							}
							
							if(y>0){// es ist mehr als 1 Block der gleichen Art vorhanden
								if(y>127){
									out.write(0xff);
									out.write(0x00);// nichts = Stein
									out.write(0x80 | (y-128));
									out.write(0x00);
								} else {
									out.write(0x80 | y);// wieviele Blöcke?
									out.write(0x00);// nichts
								}
							} else {y=0;}
							if(x==0 && z==0){
								System.out.println("0 "+y+" "+0);
							}
							int id = towrite[x][z][y], nextID = 0;
							for(;y<ymax;){
								int h = 1;
								y++;
								
								while(y<ymax && (nextID=towrite[x][z][y])==id){
									y++;
									h++;
								}
								
								if(x==0 && z==0)
									System.out.println((y-h)+" "+h+" "+id);
								
								
								if(h>127){
									out.write(0xff);
									out.write(id&0xff);// nichts = Stein
									out.write(0x80 | (h-127));
									out.write(id&0xff);
								} else if(h>1){// || id > 127
									out.write(0x80 | h);
									out.write(id);
								} else out.write(id&0x7f);
								id = nextID;
							}
						}
					}
				} else {
					out.write(0x01);
					// wird der Chunk noch geladen? mal gucken...
				}
			} else {
				// 5 Bytes: packetID, Höhe, typeID
				// der Chunk wurde noch nicht generiert -> zeige einen groben Plan indem du die Biom/BlockID + die Höhe zeigst
				// im Renderer kann ja Nebel erscheinen falls wir das hinbekommen (wäre echt episch :D)
				out.write(0x10);
				out.write(int16((int) Jena.h(x16*16+8, z16*16+8)));
				double type = Jena.type(x16*16+8, z16*16+8);// geht ja bis 5...
				if(type==5){// Spezialfall, dass man außerhalb der Karte ist...
					out.write(0x0);
				} else {
					out.write((byte)(1+(int) (type*250)));
				}
			}
		}
	}
	
	static byte[] int16(int i){
		return new byte[]{(byte) (i>>8), (byte) (i&0xff)};
	}
	
	private static boolean isStone(int i) {
		return i==0 || i==1 || i==3 || i==4 || i==8 || i==9 || i==10 || i==11 || i==13 || i==15 || i==16;
	}

	/**
	 * ähnliche Blöcke können die gleiche ID bekommen -> spart Platz=Traffic :)
	 * (dadurch dass die ID auf 128 beschränkt ist (wenn man die ID unbedingt braucht kann man davor auch die 1 setzen und bekommt so die Möglichkeit alle Bits zu senden :)))
	 * */
	private static int pseudoID(int realid, int data) {
		switch(realid){
		case 4://cobble
		case 67://cobblestufen
		case 109:
		case 48://moosiger cobble
		case 97://monster bricks
		case 98://bricks
		case 13://kies
		case 43:case 44://stufen
			return 1;// muss noch (an Holz und so) angepasst werden!
		case 37:case 38:case 39:// Pilze - braucht man nicht
		case 101:// Eisengitter
		case 175:// große Blumen
		case 106:
		case 40:
		case 50://fackel
		case 78://Schneeplatte
		case 68:
			return 0;
		case 82:return 3;// clay und erde sind ja ziemlich gleich in der Welt
		case 89:return 20;// Glowstone als Glas kann nicht schaden :)
		case 54:return 17;
		}
		return realid;
	}
}
