package me.corperateraider.generator;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import me.corperateraider.myworld.Plugin;
import net.minecraft.util.org.apache.commons.io.IOUtils;
import converter.ZipArchiveExtractor;

public class NASAHeight extends MathHelper {
	
	static boolean ini;
	public static void ini(File folder){
		path = folder.getAbsolutePath()+"/";
		ini = true;
	}
	
	public static void main(String[] args) throws IOException{
		BufferedReader files = new BufferedReader(new FileReader(new File("C:/Users/Antonio/Desktop/eurasia.txt")));
		String s1= "C:/Users/Antonio/Desktop/HD/pre/", s2= "C:/Users/Antonio/Desktop/HD/pre2/";
		String ufolder = "http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/", folder="", file;
		for(String line=files.readLine();line!=null;line=files.readLine()){
			if(line.startsWith("//")){
				folder = ufolder+line.substring(2)+"/";
			} else {
				
				file=folder+(line=line.split(" ")[0]);
				line = line.substring(0, line.length()-8);
				
				if(!new File(path+line+".png").exists()){
					
					System.out.println("-> "+line);
					
					// download as zip -> save
					
					BufferedInputStream in = new BufferedInputStream(new URL(file).openStream());
					FileOutputStream fout = new FileOutputStream(s1+line+".hgt.zip");

			        byte data[] = new byte[1024];
			        int count;
			        while ((count = in.read(data, 0, 1024)) != -1) {
			            fout.write(data, 0, count);
			        }
			        fout.flush();
			        fout.close();
			        in.close();

					// entpacke zip
					ZipArchiveExtractor.extractArchive(new File(s1+line+".hgt.zip"), new File(s2+line));
					
					// konvertiere zu einem Bild...
					BufferedImage img = new BufferedImage(1201, 1201, 1);
					FileReader rea = new FileReader(new File(s2+line+"/"+line+".hgt"));
					
					data = IOUtils.toByteArray(rea);

					for(int i=0;i<data.length;i+=2){
						img.setRGB((i/2)/1201, (i/2)%1201, ByteToInt(data[i])*256+ByteToInt(data[i+1]));
					}
					
					//for(int r = rea.read();r>-1;r=rea.read()){
						//if((i%2)==0){// erste Zahl
						//	z=r;
						//	if(z>1)
						//		System.out.println(z);
						//} else {// zweite Zahl
						//	img.setRGB(i/2/1201, i/2%1201, z*256+r);
						//}
						//i++;
					//}
					rea.close();
					ImageIO.write(img, "png", new File(path+line+".png"));
				}
			}
		}
		files.close();
		
		/*int[][] d1, d2;
		d1 = getData(11, 49);
		d2 = getData(11, 50);
		
		System.out.println(d1[0][1200]+" "+d2[0][0]);*/

	}
	
	static int ByteToInt(byte by){
		int b = by;
		return b<0?128+(b&0x7f):b;
	}
	
	static HashMap<String, int[][]> maps = new HashMap<>();
	static String path;
	public static double getInterpolatedHeight(double lng, double lat){
		
		if(lng>180 || lng<-180 || lat>90 || lat<-90) return 0;
		
		if(!ini)ini(Plugin.instance.getDataFolder());
		
		//mindestWerte
		int plng = (int) Math.floor(lng*1201), plat = (int) Math.floor(lat*1201);
		double lg=lng*1201, lt=lat*1201;
		//Anteil von dem Wert an den Mindestwerten
		float px = (float) (lg-plng), py = (float) (lt-plat);
		return coslineInterpolate(coslineInterpolate(getRealHeight(plng, plat), getRealHeight(plng+1, plat), px), coslineInterpolate(getRealHeight(plng, plat+1), getRealHeight(plng+1, plat+1), px), py);
	}
	
	public static int getRealHeight(int plng, int plat){
		
		if(!ini)ini(Plugin.instance.getDataFolder());
		
		int k=getData(plng/1201, plat/1201)[abs(plng)%1201][abs(plat)%1201];
		while(k==0x8000){//fehlende Daten...
			plng+=Math.random()*6-3;
			plat+=Math.random()*6-3;
			k=getData(plng/1201, plat/1201)[abs(plng)%1201][abs(plat)%1201];
		}
		
		return recalculate(k);
	}
	
	private static int recalculate(int x) {//rechne die 16Bitzahl um: wenn minus, dann minus
		if(x>>15 == 1){//ist zwar falsch, aber nicht schlimm :P - es müsste -0x10000 sein
			return x-0xffff;
		}
		return x;
	}
	
	public static int abs(int i){
		return i<0?-i:i;
	}
	
	public static String intshift2(int i){
		return i<10?"0"+i:""+i;
	}
	
	public static String intshift3(int i){
		return i<100?i<10?"00"+i:"0"+i:""+i;
	}
	
	public static int[][] getData(int pt, int pg){
		if(!maps.containsKey(pg+"/"+pt)){
			System.out.println("loaded..."+pg+"."+pt);
			File f = new File(path+(pg<0?"S"+intshift2(-pg):"N"+intshift2(pg))+(pt<0?"W"+intshift3(-pt):"E"+intshift3(pt))+".png");
			System.out.println(f.getAbsolutePath());
			if(f.exists()){
				try {
					BufferedImage img = ImageIO.read(f);
					int[][] data = new int[1201][1201];
					
					for(int x=0;x<1201;x++){
						for(int y=0;y<1201;y++){
							data[x][y]=img.getRGB(x, 1200-y)&0xffff;
						}
					}
					
					maps.put(pg+"/"+pt, data);
					/*if(maps.size()>1000){ tritt nicht mehr auf :)
						maps = new HashMap<>();
						maps.put(pg+"/"+pt, data);
						return data;
					}*/
				} catch (IOException e) {e.printStackTrace();}
			} else {
				System.out.println("NasaHeight misses  "+f.getAbsolutePath());
				maps.put(pg+"/"+pt, voidArray);
				return voidArray;
			}
		}
		return maps.get(pg+"/"+pt);
	}
	static int[][] voidArray = new int[1201][1201];
	static float coslineInterpolate(float a, float b, float x){
		float f = (1.0f - cos(x * PI)) * 0.125f + x * 0.75f;
		return a * (1.0f - f) + b * f;
	}
	

}
