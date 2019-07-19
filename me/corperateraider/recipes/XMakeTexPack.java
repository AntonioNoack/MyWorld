package me.corperateraider.recipes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
Design of XBlock.src:
<br>	"folderAndImageName|cmt/random/horizontal/vertical/top/repeat/fixed|tiles|width|height|";
<br>
<br>Design of XBlockFace.properties:
<br>	"cmt/random/horizontal/..|tiles|width|height|"
<br>
<br>???:
<br>	connect
*/
public class XMakeTexPack {
	
	public static void oldmain(String[] args) throws IOException{
		// convert
		//	0 1 zu  1 2 3 4
		//	2 3		5 6 7 8
		//			9 a b c
		//			d e f g
		String[] todo = new String[]{"sandrepeat/redsand", "sandrepeat/sand/asand/", "Stone/andesite", "Stone/big", "stone/diorite", "stone/granite", "stone/refined_granite",
				"stone/refined_granite/dirty", "stone/refined_granite/left", "stone/refined_granite/right", "stone/refined_granite/single", "hard_materials/cobblestone/dark",
				"hard_materials/cobblestone/clayslate", "hard_materials/cobblestone/clayslate/top", "hard_materials/cobblestone/slate", "hard_materials/cobblestone/slate/top",
				"hard_materials/quarz/quarz_normal", "hard_materials/quarz/quarz_normal/new folder", "hard_materials/sandstone/top",
				"icerepeat/crystal/green", "icerepeat/crystal/lightblue", "icerepeat/crystal/pink", "icerepeat/crystal/red", "icerepeat/glacier", "icerepeat/icebricks", "icerepeat/packed_ice/ice top", "icerepeat/thinice",
				"nat_sandstone", "nat_sandstone/sandstonetop"};
		File outdir=new File("C:/Users/Antonio/Desktop/myWorldConquest/assets/minecraft/mcpatcher/ctm/converted/yellow");
		for(String name:todo){
			System.out.println(name);
			ArrayList<File> make = new ArrayList<>();
			File dir;
			for(File file:(dir=new File("C:/Users/Antonio/Desktop/myConquest/assets/minecraft/mcpatcher/ctm/"+name)).listFiles()){
				if(file.getName().endsWith(".png")){
					make.add(file);
				}
			}
			int size = (int)Math.floor(Math.sqrt(make.size()));
			System.out.println("\t"+size);
			BufferedImage load = new BufferedImage(32*size, 32*size, 2);
			for(int i=0;i<size*size;i++){
				int by=(i/size)*32, bx=(i%size)*32;
				BufferedImage loaded = ImageIO.read(new File(dir, (i+1)+".png"));
				for(int a=0;a<32;a++){
					for(int b=0;b<32;b++){
						load.setRGB(bx+a, by+b, loaded.getRGB(a, b));
					}
				}
			}
			File newdir = new File(outdir, name);
			if(!newdir.exists())newdir.mkdirs();
			ImageIO.write(load, "png", new File(newdir, "_.png"));
			for(int i=0;i<size*2;i++){
				for(int j=0;j<size*2;j++){
					BufferedImage out = new BufferedImage(16, 16, 2);
					int bx=16*i, by=16*j;
					for(int a=0;a<16;a++){
						for(int b=0;b<16;b++){
							out.setRGB(a, b, load.getRGB(a+bx, b+by));
						}
					}
					
					ImageIO.write(out, "png", new File(newdir, (i*2*size+j+1)+".png"));
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException{

		File main = new File((args!=null && args.length>0)?args[0]:"C:/Users/Antonio/Desktop/myWorldConquest/assets/minecraft/mcpatcher/ctm/");
		File source = new File((args!=null && args.length>1)?args[1]:"C:/Users/Antonio/Desktop/input4myWorldConquest/");
		if(!source.exists()){
			System.out.println("Couldn't find resources :(");
			System.exit(-1);
		}
		if(!main.exists())main.mkdirs();
		XBlock.init();
		
		for(XBlock b:XBlock.registered){
			String[] parts = b.src.split("\\|");
			
			File dir = new File(main, parts[0]);
			dir.mkdir();
			
			// wenn faces != null, aber die Länge von faces == 0, dann wird nichts an der Textur geändert... andere Dropeigenschaften und so bleiben aber erhalten
			XBlockFace[] faces = b.faces;
			if(faces==null){
				FileWriter write = new FileWriter(new File(dir, "block"+b.id+".properties"));
				
				write.write("metadata="+b.data+"\n");
				write.write("method="+parts[1]+"\n");
				write.write("tiles="+parts[2]+"\n");
				if(!parts[3].equalsIgnoreCase("null")){
					write.write("width="+parts[3]+"\n");
				}
				if(!parts[4].equalsIgnoreCase("null")){
					write.write("height="+parts[4]+"\n");
				}
				
				write.close();
				
				
				
			} else {
				for(int i=faces.length-1;i>=0;i--){
					
					parts = (parts[0]+"|"+faces[i].properties).split("\\|");
					
					FileWriter write = new FileWriter(new File(dir, "block"+b.id+".properties"));
					
					write.write("metadata="+b.data+"\n");
					write.write("method="+parts[1]+"\n");
					write.write("faces="+faces[i].side+"\n");
					write.write("tiles="+parts[2]+"\n");
					if(!parts[3].equalsIgnoreCase("null")){
						write.write("width="+parts[3]+"\n");
					}
					if(!parts[4].equalsIgnoreCase("null")){
						write.write("height="+parts[4]+"\n");
					}
					
					write.close();
					
				}
			}
			
			// kopiere alle Bilder aus dem Quellordner an den richtigen Platz :) - vllt später auch mit Fremdtexturpacksupport
			// dann wird das Texpack entpackt und es wird nach den speziellen Dateien gesucht, die diese gesuchte Textur hätten und andernfalls werden die eigenen genommen
			// die sind schon irgendwie mit im Ordner :D - wird eben als .jar + .neverreadme + /standart/.pngs abgeliefert :)
			for(File f:new File(source, parts[0]).listFiles()){
				if(f.getAbsolutePath().endsWith(".png")){
					ImageIO.write(ImageIO.read(f), "png", new File(dir, f.getName()));
				}
			}
		}
	}
}
