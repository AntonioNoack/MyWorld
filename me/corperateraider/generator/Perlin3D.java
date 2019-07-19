package me.corperateraider.generator;


public class Perlin3D extends MathHelper {
	
	private long seed;
	private Random rand;
	private int frequency;
	private float period;
	
	public Perlin3D(long seed, int octave){
		this.seed = seed;
		frequency = octave;
		this.period = 1f/frequency;
		this.rand = new Random(0);
	}
	
	public double getNoiseAt(int x, int y, int z){
		
		int ymin = (int) Math.floor(y * period);
		int ymax = ymin + 1;
		
		return cosineInterpolate(getNoiseLevelAtPosition(x, ymin, z), getNoiseLevelAtPosition(x, ymax, z), (y - ymin * frequency) * period);
	}
	
	protected double getNoiseLevelAtPosition(int x, int y, int z){
		int xmin = (int) Math.floor(x * period);
		int xmax = xmin + 1;
		int zmin = (int) Math.floor(z * period);
		int zmax = zmin + 1;
		return cosineInterpolate(
			cosineInterpolate(
				getRandomAtPosition(xmin, y, zmin),
				getRandomAtPosition(xmax, y, zmin),
				(x - xmin * frequency) * period),
			cosineInterpolate(
				getRandomAtPosition(xmin, y, zmax),
				getRandomAtPosition(xmax, y, zmax),
				(x - xmin * frequency) * period),
			(z - zmin * frequency) * period
		);
	}
	
	private double getRandomAtPosition(int x, int y, int z){
		rand.seed = seed ^ (Random.l2 * x) ^ (Random.l3 * y) ^ (Random.l1 * z);
		return rand.next();
	}
}
