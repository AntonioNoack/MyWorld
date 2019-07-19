package me.corperateraider.generator;

import me.corperateraider.myworld.Plugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;


public class MathHelper {
	
	public static double[] cooOfNotherWorld(double x, double y, double z){
		tmx = (int) ((Math.abs(x + 10 * Plugin.width + Plugin.width/2)) / Plugin.width);
		tmz = (int) ((Math.abs(z + 10 * Plugin.width + Plugin.width/2)) / Plugin.width);
		
		int w = 2*Plugin.width;
		
		if(y < 32){
			//switch in welches Level er gehört, jenachdem, wo er ist
			y+=224;
			switch((tmx*8+tmz)/2-45){
			case  0:z+=w;break;
			case  1:x+=w;break;
			case  9:z-=w;break;
			case  8:z-=w;break;
			case  7:x-=w;break;
			case -1:x-=w;break;
			case -9:z+=w;break;
			case -8:z+=w;break;
			case -7:z+=w;break;
			case -6:x+=w;break;
			case  2:x+=w;break;
			case 10:x+=w;break;
			case 18:z-=w;break;
			case 17:z-=w;break;
			case 16:z-=w;break;
			case 15:z-=w;break;
			case 14:x-=w;break;
			case  6:x-=w;break;
			case -2:x-=w;break;
			case-10:x-=w;break;
			case-18:z+=w;break;
			case-17:z+=w;break;
			case-16:z+=w;break;
			case-15:z+=w;break;
			case-14:break;
			}
		}else if(y > 256-32){// wenn man nach oben geht...
			y-=224;
			switch((tmx*8+tmz)/2-45){
			case  0:break;
			case  1:z-=w;break;
			case  9:x-=w;break;
			case  8:z+=w;break;
			case  7:z+=w;break;
			case -1:x+=w;break;
			case -9:x+=w;break;
			case -8:z-=w;break;
			case -7:z-=w;break;
			case -6:z-=w;break;
			case  2:x-=w;break;
			case 10:x-=w;break;
			case 18:x-=w;break;
			case 17:z+=w;break;
			case 16:z+=w;break;
			case 15:z+=w;break;
			case 14:z+=w;break;
			case  6:x+=w;break;
			case -2:x+=w;break;
			case-10:x+=w;break;
			case-18:x+=w;break;
			case-17:z-=w;break;
			case-16:z-=w;break;
			case-15:z-=w;break;
			case-14:z-=w;break;
			}
		}
		return new double[]{x,y,z};
	}
	private static int tmx,tmz;
	
	public static int ori(int x){
		x+=Plugin.width*1001;
		x=x%(2*Plugin.width);
		return x-Plugin.width;
	}

	public static int basey(int x, int z){
		return baseY(0, x2wx(x), x2wx(z));
	}
	
	private static int x2wx(int x){
		x/=Plugin.width;
		if(x>0)x++;
		x=x >> 1;
		return x;
	}
	
	public static int baseY(int baseY, int wx, int wz){
		switch(wx*8+wz){
		case  0:baseY=24;break;
		case  1:baseY=23;break;
		case  9:baseY=22;break;
		case  8:baseY=21;break;
		case  7:baseY=20;break;
		case -1:baseY=19;break;
		case -9:baseY=18;break;
		case -8:baseY=17;break;
		case -7:baseY=16;break;
		case -6:baseY=15;break;
		case  2:baseY=14;break;
		case 10:baseY=13;break;
		case 18:baseY=12;break;
		case 17:baseY=11;break;
		case 16:baseY=10;break;
		case 15:baseY= 9;break;
		case 14:baseY= 8;break;
		case  6:baseY= 7;break;
		case -2:baseY= 6;break;
		case-10:baseY= 5;break;
		case-18:baseY= 4;break;
		case-17:baseY= 3;break;
		case-16:baseY= 2;break;
		case-15:baseY= 1;break;
		case-14:baseY= 0;break;
		default:
			baseY=-1;
		}
		baseY = baseY * 224;
		return baseY;
	}
	
	public static final short
		obsidian = 49,
		endstein=121,
		stein=1,
		sand=12,
		sandstein=24,
		wasser=8,
		netherrack=87,
		netherbrick=112,
		lava=10,
		gras=2,
		cobble=4,
		mooscobble=48,
		rotpilz=40,
		braunpilz=39,
		clay=82,
		erde=3,
		air=0,
		kaktus=81,
		kakao=127;
	
	private static float[] cos;
	public static final double
		PI = Math.PI,
		T = PI*2,
		fT = 1.0/T,
		Tf4 = T/4.0, Tf256 = T/256, f256=1/256.0, f3=1/3.0, Tf15=T/1.5;
	
	public static Random random = new Random(System.nanoTime()&0xffffff);
	
	static {
		cos = new float[65536];
		for(int i=0;i<65536;i++){
			cos[i] = (float) Math.cos(i * 0.0000958738);
		}
	}
	
	public static float sin(double al){
		return cos(al-1.5707963267948966192313216916398f);
	}
	
	public static float cos(double d){
		return cos[roundabs(d * 10430.378f) & 0xffff];
	}
	
	public static int roundabs(double d){
		return (int)(d<0?-d:d);
	}
	
	public static double abs(double d){
		return d<0?-d:d;
	}
	
	public static float sq(float f){
		return f*f;
	}
	
	public static double sq(double d){
		return d*d;
	}

	public static double cosineInterpolate(double d, double e, float x){
		double f = (1.0 - cos(x * PI)) * 0.5;
		return d * (1.0 - f) + e * f;
	}
	
	public static double random(){
		return random.next();
	}
	
	public static int floor(double d){
		return (int)(d<0?d-1:d);
	}
	
	public static double max(double i, double j) {
		return i>j?i:j;
	}

	public static int max(int i, int j) {
		return i>j?i:j;
	}
	
	public static double min(double a, double b){
		return a<b?a:b;
	}
	
	public static int min(int i, int j) {
		return i<j?i:j;
	}

	public static int stringToInt(String s, int other) {
		int ret = 0;
		if(s.startsWith("-")){
			s=s.substring(1);
			for(char c:s.toCharArray()){
				if(c>='0' && c<='9'){
					ret = ret*10+c-48;
				} else return other;
			}
			return -ret;
		} else {
			for(char c:s.toCharArray()){
				if(c>='0' && c<='9'){
					ret = ret*10+c-48;
				} else return other;
			}
			return ret;
		}
	}
	
	public static long stringToLong(String s, int other) {
		long ret = 0;
		if(s.startsWith("-")){
			s=s.substring(1);
			for(char c:s.toCharArray()){
				if(c>='0' && c<='9'){
					ret = ret*10+c-48;
				} else return other;
			}
			return -ret;
		} else {
			for(char c:s.toCharArray()){
				if(c>='0' && c<='9'){
					ret = ret*10+c-48;
				} else return other;
			}
			return ret;
		}
	}

	public static final int LOG=17, LEAVES=18, VINE=106, LEAVES2=161;
	public static void set(World w, int cx, int cz, Location l, int id, int data) {
		int x=l.getBlockX(), y=l.getBlockY(), z=l.getBlockZ();
		if(x<cx || y<0 || z<cz || x>cx+15 || y>255 || z>cz+15) return;
		if(id!=LEAVES || isAirOrLeaves(w.getBlockAt(x,y,z).getType()))
			Generator.sB(w, x, y, z, id, data);
	}
	
	public static void set(World w, int cx, int cz, int x, int y, int z, int id, int data) {
		if(x<cx || y<0 || z<cz || x>cx+15 || y>255 || z>cz+15) return;
		if(id!=LEAVES || isAirOrLeaves(w.getBlockAt(x,y,z).getType()))
			Generator.sB(w, x, y, z, id, data);
	}
	
	protected static boolean isAirOrLeaves(Material m){
		return m==null || m==Material.AIR || m==Material.LEAVES || m==Material.LEAVES_2;
	}
	
	public static boolean isName(String s) {
		if(s.equalsIgnoreCase("@king"))return true;
		if(s.length()==0) return false;
		s=s.toLowerCase();
		for(char c:s.toCharArray()){
			if(!"abcdefghijklmnopqrstuvwxyz01234567890_".contains(c+"")){
				return false;
			}
		}
		return true;
	}
	
	public static int isNumber(String s) {
		return isNumber(s, -1);
	}

	public static int isNumber(String s, int alternative) {
		s=s.replace(" ", "");
		for(char c:s.toCharArray()){
			if(c<'0' || c>'9') return -1;
		}
		return Integer.parseInt(s);
	}
	
	public static Chest getChest(World w, int x, int y, int z){
		if(w.getBlockAt(x, y, z).getState() instanceof Chest){
			return (Chest) w.getBlockAt(x,y,z).getState();
		}
		return null;
	}

	/**
	 * gibt die Anzahl in Sekunden in Tagen, Stunden, Minuten und Sekunden zurück
	 * */
	public static String secondsToTime(long last) {
		if(last<0)return secondsToTime(-last);
		if(last>86399){
			return last/86400+"d "+(last/3600)%24+"h "+(last/60)%60+"m "+last%60+"s";
		} else if(last>3599){
			return (last/3600)%24+"h "+(last/60)%60+"m "+last%60+"s";
		} else if(last>59){
			return (last/60)%60+"m "+last%60+"s";
		} else {
			return last+"s";
		}
	}
}
