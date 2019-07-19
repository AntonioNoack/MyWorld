package me.corperateraider.generator;


public class Perlin2D extends MathHelper {
	
	private long seed;
	private Random rand;
	private float frequency;
	private float period;
	
	public Perlin2D(long seed, float octave){
		this.seed = seed;
		this.frequency = octave;
		this.period = 1f/this.frequency;
		this.rand = new Random(0);
	}

	public double getNoiseAt(float x, float z){
		int xmin = (int) Math.floor(x * period);
		int xmax = xmin + 1;
		int zmin = (int) Math.floor(z * period);
		int zmax = zmin + 1;
		return cosineInterpolate(
			cosineInterpolate(
				(float) getRandomAtPosition(xmin, zmin),
				(float) getRandomAtPosition(xmax, zmin),
				(x - xmin * frequency) * period
			),
			cosineInterpolate(
				(float) getRandomAtPosition(xmin, zmax),
				(float) getRandomAtPosition(xmax, zmax),
				(x - xmin * frequency) * period
			),
			(z - zmin * frequency) * period
		);
	}
	
	private double getRandomAtPosition(int x, int z){
		rand.seed = seed ^ (Random.l3*x) ^ (Random.l2*z);
		return rand.next();
	}
	
	/*public double getNoiseAt(int x, int z, float frequency){
		period=1f/frequency;
		int xmin = (int) Math.floor(x * period);
		int xmax = xmin + 1;
		int zmin = (int) Math.floor(z * period);
		int zmax = zmin + 1;
		return cosineInterpolate(
			cosineInterpolate(
				getRandomAtPosition(xmin, zmin),
				getRandomAtPosition(xmax, zmin),
				(x - xmin * frequency) * period
			),
			cosineInterpolate(
				getRandomAtPosition(xmin, zmax),
				getRandomAtPosition(xmax, zmax),
				(x - xmin * frequency) * period
			),
			(z - zmin * frequency) * period
		);
	}*/
}
