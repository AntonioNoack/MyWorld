package me.corperateraider.generator;


public class WorldGenCaves extends WorldGen {
	
	protected int a = 8;
	
	@Override
	public void a(long seed, int x, int basey, int z, short[][] ret){
		
		int i = this.a;
		
		for (int j = x - i; j <= x + i; j++) {
			for (int k = z - i; k <= z + i; k++){
				for(int l = -1; l<2;l++){
					if(l*224+basey>1344){// wenn über dem Nether und über der Grenzschicht
						b = new Random(j*16, basey+l*224, k*16);
						a(j, k, x, l*224, basey, z, ret);
					}
				}
			}
		}
	}
	
	protected void a(long paramLong, int paramInt1, int basey, int paramInt2, short[][] ret, double paramDouble1, double paramDouble2, double paramDouble3){
		a(paramLong, paramInt1, basey, paramInt2, ret, paramDouble1, paramDouble2, paramDouble3, 1.0F + this.b.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
	}

	protected void a(long seed, int paramInt1, int basey, int paramInt2, short[][] ret, double paramDouble1, double paramDouble2, double paramDouble3, double paramFloat1, double paramFloat2, double paramFloat3, int paramInt3, int paramInt4, double paramDouble4){
		double d1 = paramInt1 * 16 + 8;
		double d2 = paramInt2 * 16 + 8;

		float f1 = 0;
		float f2 = 0;
		Random localRandom = new Random((int) paramDouble1, (int) paramDouble2, (int) paramDouble3);
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
		int j = localRandom.nextInt(paramInt4 / 2) + paramInt4 / 4;
		int k = localRandom.nextInt(6) == 0 ? 1 : 0;
		for (; paramInt3 < paramInt4; paramInt3++){
			double d3 = 1.5 + sin(paramInt3 * PI / paramInt4) * paramFloat1;
			double d4 = d3 * paramDouble4;

			float sin = cos(paramFloat3);
			float cos = sin(paramFloat3);
			paramDouble1 += cos(paramFloat2) * sin;
			paramDouble2 += cos;
			paramDouble3 += sin(paramFloat2) * sin;
			if (k != 0) {
				paramFloat3 *= 0.92F;
			} else {
				paramFloat3 *= 0.7F;
			}
			paramFloat3 += f2 * 0.1;
			paramFloat2 += f1 * 0.1;

			f2 *= 0.9F;
			f1 *= 0.75F;
			f2 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 2;
			f1 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 4;
			if ((i == 0) && (paramInt3 == j) && (paramFloat1 > 1) && (paramInt4 > 0)){
				a(localRandom.nextLong(), paramInt1, basey, paramInt2, ret, paramDouble1, paramDouble2, paramDouble3, localRandom.nextFloat() * 0.5 + 0.5, paramFloat2 - 1.570796F, paramFloat3/3, paramInt3, paramInt4, 1.0D);
				a(localRandom.nextLong(), paramInt1, basey, paramInt2, ret, paramDouble1, paramDouble2, paramDouble3, localRandom.nextFloat() * 0.5 + 0.5, paramFloat2 + 1.570796F, paramFloat3/3, paramInt3, paramInt4, 1.0D);
				return;
			}
			if ((i != 0) || (localRandom.nextInt(4) != 0)){
				double d5 = paramDouble1 - d1;
				double d6 = paramDouble3 - d2;
				double d7 = paramInt4 - paramInt3;
				double d8 = paramFloat1 + 2 + 16;
				if (d5 * d5 + d6 * d6 - d7 * d7 > d8 * d8) {
					return;
				}
				if ((paramDouble1 >= d1 - 16.0D - d3 * 2) && (paramDouble3 >= d2 - 16 - d3 * 2) && (paramDouble1 <= d1 + 16 + d3 * 2) && (paramDouble3 <= d2 + 16 + d3 * 2)){
					int sx = floor(paramDouble1 - d3) - paramInt1 * 16 - 1;
					int ex = floor(paramDouble1 + d3) - paramInt1 * 16 + 1;

					int sy = floor(paramDouble2 - d4) - 1;
					int ey = floor(paramDouble2 + d4) + 1;

					int sz = floor(paramDouble3 - d3) - paramInt2 * 16 - 1;
					int ez = floor(paramDouble3 + d3) - paramInt2 * 16 + 1;
					if (sx < 0) {
						sx = 0;
					}
					if (ex > 16) {
						ex = 16;
					}
					if (sy < 0) {
						sy = 0;
					}
					if (ey > 255) {
						ey = 255;
					}
					if (sz < 0) {
						sz = 0;
					}
					if (ez > 16) {
						ez = 16;
					}
					int i5 = 0;
					int z;
					/*for (int i6 = m; (i5 == 0) && (i6 < n); i6++) {
						for (int i7 = i3; (i5 == 0) && (i7 < i4); i7++) {
							for (int i8 = i2 + 1; (i5 == 0) && (i8 >= i1 - 1); i8--){
								i9 = (i6 * 16 + i7) * 256 + i8;
								if ((i8 >= 0) && (i8 < 256)){
									Block localBlock1 = paramArrayOfBlock[i9];
									if ((localBlock1 == Blocks.WATER) || (localBlock1 == Blocks.STATIONARY_WATER)) {
										i5 = 1;
									}
									if ((i8 != i1 - 1) && (i6 != m) && (i6 != n - 1) && (i7 != i3) && (i7 != i4 - 1)) {
										i8 = i1;
									}
								}
							}
						}
					}*/
					double xzsq = sq(d3), d3fd4=d3/d4;
					if (i5 == 0){
						for (int x = sx; x < ex; x++){
							double d9 = (x + paramInt1 * 16 + 0.5D - paramDouble1);
							for (z = sz; z < ez; z++){
								double d10 = (z + paramInt2 * 16 + 0.5D - paramDouble3);
								//int i10 = (x * 16 + z) * 256 + i2;
								//int i11 = 0;
								if (d9 * d9 + d10 * d10 < xzsq) {
									/*if(getBlock(ret, x, ey+1, z)==sand){
										setBlock(ret, x, ey+1, z, sandstein);
									}*/
									for (int y = ey; y >= sy; y--){
										double d11 = (y + 0.5 - paramDouble2);
										if ((d11 > -0.7*d4) && (sq(d9) + sq(d10) + sq(d11 * d3fd4) < xzsq)){
											int b;
											if((b=getBlock(ret, x, y, z))==gras){
												if(x<15 && getBlock(ret, x+1, y, z)==clay){setBlock(ret, x+1, y, z, air);}
												if(x>0 && getBlock(ret, x-1, y, z)==clay){setBlock(ret, x-1, y, z, air);}
												if(z<15 && getBlock(ret, x, y, z+1)==clay){setBlock(ret, x, y, z+1, air);}
												if(z>0 && getBlock(ret, x, y, z-1)==clay){setBlock(ret, x, y, z-1, air);}
												
												if(x<15 && getBlock(ret, x+1, y-1, z)==clay){setBlock(ret, x+1, y-1, z, air);}
												if(x>0 && getBlock(ret, x-1, y-1, z)==clay){setBlock(ret, x-1, y-1, z, air);}
												if(z<15 && getBlock(ret, x, y-1, z+1)==clay){setBlock(ret, x, y-1, z+1, air);}
												if(z>0 && getBlock(ret, x, y-1, z-1)==clay){setBlock(ret, x, y-1, z-1, air);}
												
												setBlock(ret, x, y-1, z, gras);
											}
											if(b==sand || b==sandstein){
												if((b=getBlock(ret, x, y+1, z))==sand || b==wasser){
													setBlock(ret, x, y+1, z, sandstein);
												}
												
												if(x<15 && ((b=getBlock(ret, x+1, y, z))==sand || b==wasser)){setBlock(ret, x+1, y, z, sandstein);}
												if(x>0 && ((b=getBlock(ret, x-1, y, z))==sand || b==wasser)){setBlock(ret, x-1, y, z, sandstein);}
												if(y<15 && ((b=getBlock(ret, x, y+1, z))==sand || b==wasser)){setBlock(ret, x, y+1, z, sandstein);}
												if(y>0 && ((b=getBlock(ret, x, y-1, z))==sand || b==wasser)){setBlock(ret, x, y-1, z, sandstein);}
												
												setBlock(ret, x, y-1, z, sandstein);
												setBlock(ret, x, y, z, air);
											} else if(b!=wasser){
												setBlock(ret, x, y, z, air);
											} else {
												while(getBlock(ret, x, --y, z)==wasser);
												setBlock(ret, x, y--, z, sandstein);
												setBlock(ret, x, y--, z, sandstein);
											}
										}
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

	protected void a(int paramInt1, int paramInt2, int paramInt3, int deltay, int basey, int paramInt4, short[][] ret){
		int i = this.b.nextInt(this.b.nextInt(this.b.nextInt(15) + 1) + 1);
		if (this.b.nextInt(2) != 0) {// eigentlich 7 -> mache Höhlen damit hoffentlich etwas häufiger
			i = 0;
		}
		for (int j = 0; j < i; j++){
			double d1 = paramInt1 * 16 + this.b.nextInt(16);
			double d2 = b.nextInt(224)+16+deltay;
			double d3 = paramInt2 * 16 + this.b.nextInt(16);
	
			int k = 1;
			if (this.b.nextInt(4) == 0){
				a(this.b.nextLong(), paramInt3, basey, paramInt4, ret, d1, d2, d3);
				k += this.b.nextInt(4);
			}
			for (int m = 0; m < k; m++){
				double
					f1 = this.b.next() * T,
					f2 = (this.b.next() - 0.5F) * 0.25,
					f3 = this.b.next() * 2 + this.b.next();
				if (this.b.nextInt(10) == 0) {
					f3 *= (this.b.next() * this.b.next() * 3 + 1);
				}
				a(this.b.nextLong(), paramInt3, basey, paramInt4, ret, d1, d2, d3, f3, f1, f2, 0, 0, 1);
			}
		}
	}
}
