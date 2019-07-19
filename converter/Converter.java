package converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import me.corperateraider.generator.MathHelper;
import me.corperateraider.generator.Random;

public class Converter extends MathHelper {
	public static void ini(File folder){
		if(new File("C:/Users/Antonio").exists()){
			Converter.folder = new File("C:/Users/Antonio/Desktop/HD/Cube");
		} else {
			Converter.folder = new File(folder, "map");
			System.out.println(Converter.folder.getAbsolutePath());
		}
	}
	static File folder;
	public static void main(String[] args) throws IOException {
		/*for(int i=1070;i<1251;i++){
			int k=0;
			for(int a=-i+1;a<i;a++){
				for(int b=-i+1;b<i;b++){
					if(a*a+b*b<i*i && a*a+b*b>MathHelper.sq(i-2)){
						loadMap(a*16, b*16);
						k++;
					}
				}
			}
			System.out.println(i+"\t\t"+k);
		}
		System.out.println("Fertiiiiiig");*/
		
		/*for(int i=-11;i<12;i++){
			for(int j=-11;j<12;j++){
				if(i*i+j*j<100 || sq(i+1)+j*j<100 || sq(i+1)+sq(j+1)<100 || i*i+sq(j+1)<100){
					write(i,j);
				}
			}
		}*/
		//makeWritable(-1,  0);
		//makeWritable( 0, -1);
		//makeWritable(-1, -1);
		
		//makeWritable(-1, 1);
		//s="";
		/*addInt(65, 8);
		addBool(false);
		System.out.println(s);*/
	}
	
	static void makeWritable(int x, int z) throws IOException{
		File f = new File(folder, "make/"+x+"."+z+".png");
		if(f.exists()){
			BufferedImage neu = ImageIO.read(f);
			BufferedImage out = new BufferedImage(1024, 1024, 2);
			for(int i=0;i<1024;i++){
				for(int j=0;j<1024;j++){
					out.setRGB(i, j, ((neu.getRGB(i*2, j*2)&0xff)<<24)+((neu.getRGB(i*2, j*2+1)&0xff)<<16)+((neu.getRGB(i*2+1, j*2)&0xff)<<8)+(neu.getRGB(i*2+1, j*2+1)&0xff));
				}
			}
			ImageIO.write(out, "png", new File(folder, "better/"+x+"."+z+".png"));
		} else {
			BufferedImage ori = ImageIO.read(new File(folder, x+"."+z+".png"));
			BufferedImage out = new BufferedImage(2048, 2048, 1);
			int c, s=0x10101;
			for(int i=0;i<1024;i++){
				for(int j=0;j<1024;j++){
					c=ori.getRGB(i, j);
					out.setRGB(i*2, j*2, s*(c>>24));
					out.setRGB(i*2, j*2+1, s*((c>>16)&0xff));
					out.setRGB(i*2+1, j*2, s*((c>>8)&0xff));
					out.setRGB(i*2+1, j*2+1, s*(c&0xff));
				}
			}
			ImageIO.write(out, "png", f);
		}
		
	}
	
	static double f180 = 1.0/180, f360 = 1.0/360, fPI = 1.0/Math.PI;
	/*static void loadMap(int x, int z){
		double lng = Jena.lat(z), lat = Jena.lng(x);
		double px = 131072 * (lat*f180 + 1), py = 131072 * (1 - Math.log(Math.tan((0.25 + lng*f360)*Math.PI))*fPI);

		MapsInterpreter.getExactlyRGBAt(px, py);
	}
	
	static int getID(int x, int z){
		int id = MapsInterpreter.getIdByLongAndLat(Jena.lng(x), Jena.lat(z));
		if(id==-1){
			System.out.println("https://www.google.de/maps/@"+Jena.lat(z)+","+Jena.lng(x)+",17z");
			
			double px = 131072 * (Jena.lng(x)*f180 + 1), py = 131072 * (1 - Math.log(Math.tan((0.25 + Jena.lat(z)*f360)*Math.PI))*fPI);//*256 gibt die Position besser an
			int rgb = MapsInterpreter.getRGBAt(px, py);
			System.out.println(((rgb>>16)&0xff)+" "+((rgb>>8)&0xff)+" "+(rgb&0xff));
			System.exit(-1);
		}
		
		return id;
	}*/
	
	/*static void write(int x, int z) throws IOException{
		x*=2048;
		z*=2048;
		BufferedImage img = new BufferedImage(1024, 1024, 2);
		
		for(int i=0;i<2048;i+=2){
			for(int j=0;j<2048;j+=2){
				img.setRGB(i/2, j/2, (getID(x+i,z+j)<<24)+(getID(x+i,z+j+1)<<16)+(getID(x+i+1,z+j)<<8)+(getID(x+i+1,z+j+1)));
			}
		}
		
		ImageIO.write(img, "png", new File("C:/Users/Antonio/Desktop/HD/cube/"+x/2048+"."+z/2048+".png"));
		System.out.println("C:/Users/Antonio/Desktop/HD/cube/"+x/2048+"."+z/2048+".png");
	}*/
	
	static HashMap<String, int[][]> maps = new HashMap<>();
	/**
	 * gibt die ID auf der Karte zurück; wenn es Teil einer Landschaft ist, wird diese geringfügig vermischt
	 * */
	public static int getCubeID(int x, int z){
		int r1, r2;
		if(landschaft(r1=getRealCubeID(x, z))){
			Random r = new Random(x*z);
			if(landschaft(r2=getRealCubeID(x+r.nextInt(128)-64, z+r.nextInt(128)-64))){
				return r2;
			} else return r1;
		} else return r1;
	}

	private static boolean landschaft(int i) {
		switch(i){
		case 1:case 2:case 3:case 4:case 11:case 16:case 17:case 18:case 60:case 61:case 62:case 63:case 71:case 72:case 73:case 74:case 75:return true;
		}
		return false;
	}

	public static int getRealCubeID(int x, int z) {
		x = ori(x);
		z = ori(z);
		
		int sx=x&0x7ff, sz=z&0x7ff;
		x&=~0x7ff;
		z&=~0x7ff;
		
		x=(int) Math.floor(1.0/2048*x);
		z=(int) Math.floor(1.0/2048*z);
		
		String key = x+"."+z;
		if(!maps.containsKey(key)){
			if(maps.size()>20){
				maps = new HashMap<>();
			}
			File f = new File(folder, key+".png");
			if(f.exists()){
				try {
					int argb;
					int[][] k = new int[2048][2048];
					BufferedImage img = ImageIO.read(f);
					for(int i=0;i<1024;i++){
						for(int j=0;j<1024;j++){
							argb = img.getRGB(i, j);
							k[i*2][j*2]=argb>>24;
							k[i*2][j*2+1]=(argb>>16)&0xff;
							k[i*2+1][j*2]=(argb>>8)&0xff;
							k[i*2+1][j*2+1]=argb&0xff;
						}
					}
					maps.put(key, k);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("No map for "+key+" @ "+sx+"/"+sz+"\n\t"+f.getAbsolutePath());
				maps.put(key, new int[1][1]);
				return 0;
			}
		}
		int[][] k=maps.get(key);
		return k.length>1?k[sx][sz]:0;
	}
}
