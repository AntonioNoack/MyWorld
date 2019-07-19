package me.corperateraider.myworld;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.corperateraider.generator.MathHelper;
import me.corperateraider.generator.Random;
import me.corperateraider.reload.C;

public class EarthAnimation extends MathHelper implements Runnable {
	
	static ArrayList<Point> inWorld = new ArrayList<>();
	static ArrayList<Point> toDraw = new ArrayList<>();
	static ArrayList<Point> rotate = new ArrayList<>();
	
	public static World w;
	
	static double lng, lat, time;
	static final double PI2x1p2121 = Math.PI * 80.0 / 3.0;
	static boolean ini = false;
	static BufferedImage img;
	
	@SuppressWarnings("deprecation")
	private static void ini() {
		char[] s = null;
		try {
			if(Plugin.instance==null){
				img = ImageIO.read(new File("C:/Users/Antonio/Desktop/Plugins/_Server/plugins/MyWorld/erde.png"));
			} else {
				img = ImageIO.read(new File(Plugin.instance.getDataFolder(), "erde.png"));
				w = Bukkit.getWorld("myworld_spawn_the_end");
			}
			
			BufferedReader read = new BufferedReader(new FileReader(new File(Plugin.instance.getDataFolder(), "sphere.obj")));
			s = read.readLine().toCharArray();
			read.close();
		} catch (IOException e) {
			img = new BufferedImage(2048, 1024, 1);
			e.printStackTrace();
		}
		
		// die Erde hat einen Radius von 153.35...
		Random r = new Random(123456789);
		
		if(s==null){
			toDraw.add(new Point(1,0,0));
			toDraw.add(new Point(0,1,0));
			toDraw.add(new Point(0,0,1));
			toDraw.add(new Point(-1,0,0));
			toDraw.add(new Point(0,-1,0));
			toDraw.add(new Point(0,0,-1));
		} else {
			for(int i=0;i<162*7*3;i+=21){
				toDraw.add(new Point(getFloatByIndAtCharArray(s, i), getFloatByIndAtCharArray(s, i+7), getFloatByIndAtCharArray(s, i+14)));
			}
		}
		
		//Random
		r = new Random(123456789L);
		
		if(w.getBlockAt(0,0,0).getTypeId()==41){
			for(int i=-153;i<154;i++){
				for(int j=-153;j<154;j++){
					for(int k=116;k<154;k++){
						r.next();
						if(i*i+j*j+k*k<23516.5 && i*i+j*j+sq(k+1)>23516.5){
							if((i*i+j*j)*0.0002-0.2<r.next()){
								inWorld.add(new Point(i, k, j));
							}
						}
					}
				}
			}
		} else {
			for(int i=-153;i<154;i++){
				for(int j=-153;j<154;j++){
					for(int k=116;k<154;k++){
						r.next();
						if(i*i+j*j+k*k<23516.5 && i*i+j*j+sq(k+1)>23516.5){
							if((i*i+j*j)*0.0002-0.2<r.next()){
								inWorld.add(new Point(i, k, j));
							} else {
								w.getBlockAt(i,k-116,j).setTypeId(20);
								w.getBlockAt(i,k-117,j).setTypeIdAndData(35, (byte) 3, false);
							}
						}
					}
				}
			}
			w.getBlockAt(0,0,0).setTypeId(41);
		}
		ini = true;
	}
	
	static float getFloatByIndAtCharArray(char[] c, int index){
		String s = c[index++]+"0.";
		for(int i=0;i<6;i++){
			s+=c[index++];
		}
		return Float.parseFloat(s);
	}
	
	static final double
			x180fPI   =  180.0/Math.PI,
			x2048f360 = 2048.0/360.0,
			x1024f180 = 1024.0/180.0;
	
	// lat von 90 bis -90 und lng von -180 bis 180
	static int getColorByLatAndLng(double lat, double lng){
		while(lat>=90){
			lat = 180-lat;
			lng += 180;
		}
		
		while(lat<-90){
			lat += 180;
			lng -= 180;
		}
		
		if(lng >= 180){
			lng -= (int)(lng+180)/360*360;
		}
		if(lng < -180){
			lng -= (int)(lng-180)/360*360;
		}
		int x=(int) (x2048f360*(180-lng)), y=(int)(x1024f180*(90-lat));
		if(x<0 || y<0 || x>=2048 || y>=1024){
			return 0xff00ff;
		}
		return img.getRGB(x, y) & 0xffffff;
	}

	static class Point {
		int rx, ry, rz;
		float x, y, z, d;
		int index=-1, oldindex=-1;
		final double alpha, beta;
		public Point(float x, float y, float z, double alpha, double beta){
			this.x=x;this.y=y;this.z=z;this.alpha=alpha;this.beta=beta;
		}
		
		public Point(float x, float y, float z){// in der Kugel... y ist also danach 116 kleiner...
			this.x=x;this.y=y;this.z=z;
			
			beta  = Math.atan2(z, x);//lat
			alpha = Math.atan(y/Math.sqrt(x*x+z*z));//lng
			
			y -= 116;
		}
		
		public Point(int x, int y, int z){
			this.x=rx=x;this.y=ry=y;this.z=rz=z;
			alpha = beta = 0;
		}
		
		public double getAlpha(){
			return alpha * x180fPI;
		}
		
		public double getBeta(){
			return beta * x180fPI;
		}
		
		public float sqdistance(int x2, int y2, int z2){
			return d=sq(x-x2)+sq(y-y2)+sq(z-z2);
		}
		
		public void multiply(float factor){
			x*=factor;
			y*=factor;
			z*=factor;
		}
		
		public boolean needsBlockUpdate(){
			return oldindex != (oldindex = index);
		}
	}
	
	public EarthAnimation(){
		ini();
	}
	
	static final double PI=Math.PI, T=PI*2;
	static final int[][] idsUndDatas = new int[][]{{1, 0}, {2, 0}, {3, 0}, {4, 0}};
	
	@Override
	@SuppressWarnings("deprecation")
	public void run() {
		if(true){
			boolean todo = false;
			players:for(Player p:Bukkit.getOnlinePlayers()){
				if(p.getWorld() == w){
					todo = true;
					break players;
				}
			}
			if(todo){
				for(int counter=0;counter<1;counter++){
					time = (0.3E-7) * (double) System.currentTimeMillis();
					lat  = 6.0/90.0 * PI * Math.sin(time * PI2x1p2121) + PI;
					lng  = (T*time)%T;//T * (time-(int)time);
					
					/*long l = System.currentTimeMillis();
					
					DrehMatrix r = new DrehMatrix();
					r=r.rotY(lng);
					r.rotZ(0.41);// Schräglage der Erde: ~23.5°
					
					// alles außer Blocksetzen braucht so 70ms, das Blocksetzen 200ms
					
					int ind=0;
					for(int i=-153;i<154;i++){
						for(int j=-153;j<154;j++){
							for(int k=-153;k<154;k++){
								if(i*i+j*j+k*k>=23409 && i*i+j*j+k*k<23716){
									int x=(int)r.x(i, j, k), y=(int)r.y(i, j, k)-116, z=(int)r.z(i, j, k);
									if(y>=0){
										w.getBlockAt(x, y, z).setTypeIdAndData(C.getID(earth[ind]), (byte) C.getData(earth[ind]), false);
									}
									ind++;
								}
							}
						}
					}
					
					System.out.println(System.currentTimeMillis()-l);*/
					
					rotate = new ArrayList<>();
					for(Point p:toDraw){
						double x=p.x, y=p.y, z=p.z, a, l;

						// 1. y um lng
						a = Math.atan2(z, x);
						a -= lng;
						
						l = Math.sqrt(x*x+z*z);
						x = sin(a)*l;
						z = cos(a)*l;
						
						// 2. z um lat
						a = Math.atan2(y, x);
						a += lat;
						
						x = sin(a)*l;
						y = cos(a)*l;
						
						//// 3. y um dreh
						//a = Math.atan2(z, x);
						//a += dreh;
						//
						//l = Math.sqrt(x*x+z*z);
						//x = Math.sin(a)*l;
						//z = Math.cos(a)*l;
						
						x*=153.35;
						y*=153.35;
						z*=153.35;
						
						rotate.add(new Point((float) x, (float) y, (float) z, p.alpha*x180fPI, p.beta*x180fPI));
					}
					
					for(Point block:inWorld){
						int i=block.rx, j=block.rz, k=block.ry;
						// finde die 3 am nähesten liegenden Punkte...
						Point p1 = rotate.get(0);
						p1.d=1000000;
						for(Point p:rotate){
							if(p1.d>p.sqdistance(i, k, j)){
								p1=p;
							}
						}
						
						Point p2 = rotate.get(0);
						p2.d=1000000;
						for(Point p:rotate){
							if(p!=p1 && p2.d>p.sqdistance(i, k, j)){
								p2 = p;
							}
						}
						Point p3 = rotate.get(0);
						p3.d=1000000;
						for(Point p:rotate){
							if(p!=p1 && p!=p2 && p3.d>p.sqdistance(i, k, j)){
								p3=p;
							}
						}
						
						if(p1==p2 || p2==p3){
							System.out.println("ERROR!");
						}
						
						double f1=1.0/p1.d, f2=1.0/p2.d, f3=1.0/p3.d;
						double fdissqrt = 1.0/(f1+f2+f3);
						
						f1*=fdissqrt;
						f2*=fdissqrt;
						f3*=fdissqrt;
						
						int rgb = getColorByLatAndLng((f1*p1.alpha + f2*p2.alpha + f3*p3.alpha), (f1*p1.beta + f2*p2.beta + f3*p3.beta));
						
						block.index = C.getIndexByColor((rgb>>16)&0xff, (rgb>>8)&0xff, rgb&0xff);
						if(block.needsBlockUpdate()){
							w.getBlockAt(i, k-116, j).setTypeIdAndData(C.getID(block.index), (byte) C.getData(block.index), false);
						}
					}
				}
			}
		}
	}
}
