package me.corperateraider.generator;


public class WorldGenDecorator extends WorldGen {

	@Override
	public void a(long seed, int cx, int basey, int cz, short[][] ret) {
		
		b = new Random(cx*16, basey, cz*16);
		
		if(basey>=1120){
			int o;
			for(int i=0;i<16;i++){
				for(int j=0;j<16;j++){
					int y=255, block;
					while((block=getBlock(ret, i, y, j))!=0 && y>0){y--;}
					if(y>0 && block==sand){
						Random r = new Random(cx*16+i, basey, cz*16+j);
						if(r.next()<0.001){
							for(int h = (int)(sq(r.next())*12)+2;h>0;h--){
								setBlock(ret, i, h+y, j, kaktus);
							}
						}
					}
					
					for(y=255;y>0;y--){
						if(y==255 && basey>4703){// wenn ich auf Stadthöhe bin, werden auf die Häuser keine Pilze gesetzt :)
							y=4800-basey;
						}
						
						while(getBlock(ret, i, y, j)!=0 && y>0){
							y--;
						}
						if(y!=0){
							while(getBlock(ret, i, y, j)==0 && y>0){
								y--;
							}
							if(y!=0){
								// wir sind am Boden einer Höhle
								// oder auf Gras oder auf Sand oder auf Wasser...
								if(b.next()<0.001){
									if((o=getBlock(ret, i, y, j))!=wasser && o!=sand && o!=gras){
										setBlock(ret, i, y, j, b.next()<0.00025*(basey+y)-0.25?wasser:lava);
									}
								} else if(b.next()-(basey+y)*0.00002<0.07){
									if(basey+y>4000 && b.next()<(basey+y-4000)*0.0003){
										if((o=getBlock(ret, i, y, j))!=wasser && o!=sand && o!=gras){
											setBlock(ret, i, y, j, mooscobble);
											if(b.next()<0.01){
												setBlock(ret, i, y+1, j, b.nextBoolean()?rotpilz:braunpilz);
											}
										}
									} else {
										if((o=getBlock(ret, i, y, j))!=wasser && o!=sand && o!=gras){
											setBlock(ret, i, y, j, cobble);
										}
									}
								}
							}
						}
					}
				}
			}
		} else if(basey>223) {
			for(int x=4;x<16;x+=8){
				for(int z=4;z<16;z+=8){
					if(b.next()<0.03){
						int y = b.nextInt(256);
						if(getBlock(ret, x, y, z)<1){
							circle(ret,x,y,z);
						}
					}
				}
			}
		}
	}
	
	private void circle(short[][] ret, int x, int y, int z){
		for(int i=-5;i<6;i++){
			for(int j=-5;j<6;j++){
				for(int k=-5;k<6;k++){
					if((i*i+j*j+k*k)<36*sq(b.next())){
						setBlock(ret, x+i, y+j, z+k, (short) 89);
					}
				}
			}
		}
	}
}
