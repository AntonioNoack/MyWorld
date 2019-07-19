package me.corperateraider.generator;


public class WorldGenCanyon extends WorldGen {
	
	private float[] d = new float[1024];
	protected int a = 8;
	
	@Override
	public void a(long seed, int cx, int basey, int cz, short[][] ret){
		
		int i = this.a;
		
		for (int j = cx - i; j <= cx + i; j++) {
			for (int k = cz - i; k <= cz + i; k++){
				for(int l=-1;l<2;l++){
					b = new Random(j*16, basey+l*224, k*16);
					a(j, k, cx, l*224, cz, ret);
				}
			}
		}
	}
	
	protected void a(long seed, int paramInt1, int basey, int paramInt2, short[][] ret, double paramDouble1, double paramDouble2, double paramDouble3, double paramFloat1, double paramFloat2, double paramFloat3, int paramInt3, int paramInt4, double paramDouble4){
		Random localRandom = new Random(paramInt1, basey, paramInt2);

		double d1 = paramInt1 * 16 + 8;
		double d2 = paramInt2 * 16 + 8;

		float f1 = 0.0F;
		float f2 = 0.0F;
		int i;
		if (paramInt4 <= 0){
			i = this.a * 16 - 16;
			paramInt4 = i - localRandom.nextInt(i / 4);
		}
		i = 0;
		if (paramInt3 == -1){
			paramInt3 = paramInt4 / 2;
			i = 1;
		}
		float f3 = 1.0F;
		for (int j = 0; j < 256; j++){
			if ((j == 0) || (localRandom.nextInt(3) == 0)) {
				f3 = 1.0F + localRandom.nextFloat() * localRandom.nextFloat() * 1.0F;
			}
			this.d[j] = (f3 * f3);
		}
		for (; paramInt3 < paramInt4; paramInt3++){
			double d3 = 1.5 + sin(paramInt3 * PI / paramInt4) * paramFloat1 * 1.0F;
			double d4 = d3 * paramDouble4;

			d3 *= (localRandom.nextFloat() * 0.25 + 0.75);
			d4 *= (localRandom.nextFloat() * 0.25 + 0.75);

			float f4 = cos(paramFloat3);
			float f5 = sin(paramFloat3);
			paramDouble1 += cos(paramFloat2) * f4;
			paramDouble2 += f5;
			paramDouble3 += sin(paramFloat2) * f4;

			paramFloat3 *= 0.7F;

			paramFloat3 += f2 * 0.05F;
			paramFloat2 += f1 * 0.05F;

			f2 *= 0.8F;
			f1 *= 0.5F;
			f2 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 2.0F;
			f1 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 4.0F;
			if ((i != 0) || (localRandom.nextInt(4) != 0)){
				double d5 = paramDouble1 - d1;
				double d6 = paramDouble3 - d2;
				double d7 = paramInt4 - paramInt3;
				double d8 = paramFloat1 + 2.0F + 16.0F;
				if (d5 * d5 + d6 * d6 - d7 * d7 > d8 * d8) {
					return;
				}
				if ((paramDouble1 >= d1 - 16.0D - d3 * 2.0D) && (paramDouble3 >= d2 - 16.0D - d3 * 2.0D) && (paramDouble1 <= d1 + 16.0D + d3 * 2.0D) && (paramDouble3 <= d2 + 16.0D + d3 * 2.0D)){
					int k = floor(paramDouble1 - d3) - paramInt1 * 16 - 1;
					int m = floor(paramDouble1 + d3) - paramInt1 * 16 + 1;

					int n = floor(paramDouble2 - d4) - 1;
					int i1 = floor(paramDouble2 + d4) + 1;

					int i2 = floor(paramDouble3 - d3) - paramInt2 * 16 - 1;
					int i3 = floor(paramDouble3 + d3) - paramInt2 * 16 + 1;
					if (k < 0) {
						k = 0;
					}
					if (m > 16) {
						m = 16;
					}
					if (n < 1) {
						n = 1;
					}
					if (i1 > 256) {
						i1 = 256;
					}
					if (i2 < 0) {
						i2 = 0;
					}
					if (i3 > 16) {
						i3 = 16;
					}
					int i4 = 0;
					int i8;
					/*for (int i5 = k; (i4 == 0) && (i5 < m); i5++) {
						for (int i6 = i2; (i4 == 0) && (i6 < i3); i6++) {
							for (int i7 = i1 + 1; (i4 == 0) && (i7 >= n - 1); i7--)
							{
								i8 = (i5 * 16 + i6) * 256 + i7;
								if ((i7 >= 0) && (i7 < 256)){
									Block localBlock1 = paramArrayOfBlock[i8];
									if ((localBlock1 == Blocks.WATER) || (localBlock1 == Blocks.STATIONARY_WATER)) {
										i4 = 1;
									}
									if ((i7 != n - 1) && (i5 != k) && (i5 != m - 1) && (i6 != i2) && (i6 != i3 - 1)) {
										i7 = n;
									}
								}
							}
						}
					}*/
					if (i4 == 0){
						for (int i5 = k; i5 < m; i5++){
							double d9 = (i5 + paramInt1 * 16 + 0.5D - paramDouble1) / d3;
							for (i8 = i2; i8 < i3; i8++){
								double d10 = (i8 + paramInt2 * 16 + 0.5D - paramDouble3) / d3;
								//int i9 = (i5 * 16 + i8) * 256 + i1;
								//int i10 = 0;
								if (d9 * d9 + d10 * d10 < 1.0D) {
									for (int i11 = i1 - 1; i11 >= n; i11--){
										double d11 = (i11 + 0.5D - paramDouble2) / d4;
										if ((d9 * d9 + d10 * d10) * this.d[i11] + d11 * d11 / 6.0D < 1.0D){
											/*Block localBlock2 = paramArrayOfBlock[i9];
											if (localBlock2 == Blocks.GRASS) {
												i10 = 1;
											}
											if ((localBlock2 == Blocks.STONE) || (localBlock2 == Blocks.DIRT) || (localBlock2 == Blocks.GRASS)) {
												if (i11 < 10){
													paramArrayOfBlock[i9] = Blocks.LAVA;
												} else {
													paramArrayOfBlock[i9] = null;
													if ((i10 != 0) && (paramArrayOfBlock[(i9 - 1)] == Blocks.DIRT)) {
														paramArrayOfBlock[(i9 - 1)] = this.c.getBiome(i5 + paramInt1 * 16, i8 + paramInt2 * 16).ai;
													}
												}
											}*/
											setBlock(ret, i5, i11, i8, (short) 0);
										}
										//i9--;
									}
								}
							}
						}
						if (i != 0) {
							break;
						}
					}
				}
			}
		}
	}

	protected void a(int paramInt1, int deltay, int paramInt2, int paramInt3, int paramInt4, short[][] ret){
		if (this.b.nextInt(50) != 0) {
			return;
		}
		double d1 = paramInt1 * 16 + this.b.nextInt(16);
		double d2 = b.nextInt(224)+16+deltay;//this.b.nextInt(this.b.nextInt(40) + 8) + 20;
		double d3 = paramInt2 * 16 + this.b.nextInt(16);
		
		int i = 3;
		for (int j = 0; j < i; j++){
			double
				f1 = this.b.next() * T,
				f2 = (this.b.next() - 0.5F) * 0.25,
				f3 = (this.b.next() * 2.0F + this.b.next()) * 2.0F;
			
			a(this.b.nextLong(), paramInt3, deltay, paramInt4, ret, d1, d2, d3, f3, f1, f2, 0, 0, 3);
		}
	}
}
