package converter;

import me.corperateraider.generator.Random;
import me.corperateraider.weather.WeatherTree;

import org.bukkit.Bukkit;

public class TPS implements Runnable {
	
	public static void main(String[] args){
		Random r = new Random(System.currentTimeMillis());
		int[] d = new int[0xffff+1];
		final int tests=1000000000;
		double min=0.9*tests/0xffff, max=1.1*tests/0xffff;
		System.out.println(min+" "+max);
		for(int i=0;i<tests;i++){
			d[r.rawNext()&0xffff]++;
		}
		for(int i=0;i<0xffff;i++){
			if(d[i]<min){
				System.out.println("< "+i);
			} else if(d[i]>max){
				System.out.println("> "+i);
			}
		}
	}
	
	public static int TICK_COUNT = 0;
	public static long[] TICKS = new long[600];
	public static long LAST_TICK = 0L;
	
	public static double getTPS(){
		return getTPS(100);
	}
	
	public static double getTPS(int ticks){
		try {
			if (TICK_COUNT < ticks) {
				return 20.0D;
			}
			int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
			long elapsed = System.currentTimeMillis() - TICKS[target];
	
			return ticks / (elapsed / 1000.0D);
		} catch (Exception e) {
			if ((e instanceof ArrayIndexOutOfBoundsException)) {
				return 20.0D;
			}
			Bukkit.getLogger().severe("[AntiLag] An error occured whilst retrieving the TPS");
		}
		return 20.0D;
	}
	
	public static long getElapsed(int tickID) {
		long time = TICKS[(tickID % TICKS.length)];
		return System.currentTimeMillis() - time;
	}
	
	public void run(){
		TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
		TICK_COUNT += 1;
		
		WeatherTree.tick(getTPS());
	}
	
	public static String memory(){
		
		int mb = 1<<20;
		
		Runtime runtime = Runtime.getRuntime();
		return "(" + (runtime.totalMemory() - runtime.freeMemory()) / mb + "MB/" + runtime.totalMemory() / mb + ")MB/" + runtime.maxMemory() / mb;
	}
}
