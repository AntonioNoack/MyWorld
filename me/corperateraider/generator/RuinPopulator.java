package me.corperateraider.generator;

import me.corperateraider.myworld.Plugin;
import me.corperateraider.myworld.Tree;
import me.corperateraider.myworld.Tree.TreeType;
import me.corperateraider.recipes.XBlock;
import me.corperateraider.reload.Jena;
import me.corperateraider.weather.Weather;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.metadata.FixedMetadataValue;

import converter.Converter;
import converter.MetaString;

/**
 * f�gt dem Untergrund Ruinen im entsprechendem Gebiet hinzu...
 * <br>dazu: Stadt-r = 10k
 * <br>Berge-r = 30k, also bis 40k
 * <br>Sandw�ste-r = 50k, also bis 90k
 * <br>Ozean-r = 200k, also bis 290k
 * <br>Versuch, normal zu generieren, wird aber wohl der Wasserfall...
 * */
public class RuinPopulator extends BlockPopulator {
	
	public RuinPopulator(){
		manager = new OreManager();
	}
	
	public static double sq(double d){
		return d*d;
	}
	
	World w;
	OreManager manager;
	
	@Override
	public void populate(World w, java.util.Random neveruse, Chunk c) {
		
		this.w=w;
		int cx=c.getX()*16, cz = c.getZ()*16;
		long time = System.currentTimeMillis();
		
		int basey = Generator.basey(cx, cz);
		
		int[][] dataOfMe = new int[48][48];
		// H�he als x				Br�cken-"H�he"
		int[][] hs = new int[16][16];
		// H�he als x.y					Urspr�ngliche H�he
		double[][] rh = new double[16][16], hh = new double[16][16];
		//double lng, lat;
		
		if(Jena.type(cx, cz)<2 && Jena.type(cx+15, cz)<2 && Jena.type(cx, cz+15)<2 && Jena.type(cx+15, cz+15)<2 && basey > 4600 && (basey+256+96 > Jena.h(cx, cz) || cx*cx+cz*cz < 3000)){
			Random r = new Random(cx, basey, cz);
			for(int i=0;i<48;i++){
				for(int j=0;j<48;j++){
					
					dataOfMe[i][j] = Converter.getCubeID(cx+i-16, cz+j-16);//MapsInterpreter.getIdByLongAndLat(lng, lat);
					
					if(i>15 && j>15 && i<32 && j<32){
						hs[i-16][j-16] = (int) (rh[i-16][j-16] = Jena.h(cx+i-16, cz+j-16) - basey);// hs = rs bei i, j
						if(dataOfMe[i][j]==30){// im Flussgebiet ist hs der Flussboden und rh die Flussh�he
							int d = 1;// Entfernung zum Ufer; nicht gr��er als 16
							for(;d<10;d++){
								if(not12or30(Converter.getCubeID(cx+i-16+d,cz+j-16))) break;
								if(not12or30(Converter.getCubeID(cx+i-16,cz+j-16+d))) break;
								if(not12or30(Converter.getCubeID(cx+i-16-d,cz+j-16))) break;
								if(not12or30(Converter.getCubeID(cx+i-16,cz+j-16-d))) break;
								if(not12or30(Converter.getCubeID(cx+i-16+d,cz+j-16+d))) break;
								if(not12or30(Converter.getCubeID(cx+i-16+d,cz+j-16-d))) break;
								if(not12or30(Converter.getCubeID(cx+i-16-d,cz+j-16+d))) break;
								if(not12or30(Converter.getCubeID(cx+i-16-d,cz+j-16-d))) break;
							}
							hs[i-16][j-16] = (int) MathHelper.cosineInterpolate((float) (hh[i-16][j-16]=rh[i-16][j-16]), (float)((rh[i-16][j-16]=(cz+j-16)*0.0016+4799-basey)-2-Jena.getOzeanType(cx+i, cz+j)), 1f*d/10);
						}
					}
				}
			}
			
			for(int i=0;i<16;i++){
				for(int j=0;j<16;j++){
					if(r.next()>Jena.type(cx+i, cz+j)-1){// Stadt 100%
						if(dataOfMe[i+16][j+16]==12 || (dataOfMe[i+15][j+16]==12?1:0) + (dataOfMe[i+16][j+15]==12?1:0) + (dataOfMe[i+17][j+16]==12?1:0) + (dataOfMe[i+16][j+17]==12?1:0)>0){// an einer Stra�e
							if(rh[i][j]-hs[i][j]+r.next()*0.2>0.6){
								set(i+cx, hs[i][j], j+cz, 44, 3);
							} else set(i+cx, hs[i][j], j+cz, 0, 0);
							if(r.next()<0.8){
								set(i+cx, hs[i][j]-1, j+cz, 43, 3);
							} else {
								set(i+cx, hs[i][j]-1, j+cz, 48, 0);
							}
						} else
						switch(dataOfMe[i+16][j+16]){
						case 0:// no Data
							set(cx+i, hs[i][j], cz+j, 49, 0);
							break;
						case 1://Nadelwald(sandiger)
							if(r.next()<0.3){
								set(i+cx, hs[i][j], j+cz, 3, 1);
							} else if(r.next()<0.25){
								set(i+cx, hs[i][j], j+cz, 12, 2);
							}
							break;
						case 4://Feld
							if(r.next()<0.2){
								set(i+cx, hs[i][j], j+cz, 60, r.next()<0.4?7:r.nextInt(6));
								if(r.next()<0.7){
									set(i+cx, hs[i][j]+1, j+cz, 59, r.nextInt(8));
								} else if(r.next()<0.1){
									set(i+cx, hs[i][j]+1, j+cz, r.nextBoolean()?141:142, r.nextInt(8));
								}
							} else if(r.next()<0.02){
								set(i+cx+1, hs[i][j], j+cz, 3, 1);
								set(i+cx-1, hs[i][j], j+cz, 3, 1);
								set(i+cx, hs[i][j], j+cz+1, 3, 1);
								set(i+cx, hs[i][j], j+cz-1, 3, 1);
								set(i+cx, hs[i][j], j+cz, 8, 0);
							}
							break;
						case 38://Kulturarena -> B�hne :)
							set(cx+i, hs[i][j]+1, cz+j, 5, XBlock.PlankenBühne.data);
							break;
						case 44:
							// Platz/Stra�e
							if(rh[i][j]-hs[i][j]+r.next()*0.2>0.6){
								set(i+cx, hs[i][j], j+cz, 44, 3);
							} else set(i+cx, hs[i][j], j+cz, 0, 0);
							if(r.next()<0.8){
								set(i+cx, hs[i][j]-1, j+cz, 43, 3);
							} else {
								set(i+cx, hs[i][j]-1, j+cz, 48, 0);
							}
							break;
						case 64://botanischer Garten
							// Melonen...
							if(r.next()<0.3){
								set(i+cx, hs[i][j]-r.nextInt(10), j+cz, 103, 0);
							} else if(r.next()<0.02){
								// exotische Setzlinge :)
								set(cx+i, hs[i][j]+1, cz+j, 6, 5-r.nextIntSQ(6));
							} else if(r.next()<0.5){
								set(cx+i, hs[i][j]+1, cz+j, blume(r));
							} else if(r.next()<0.09){
								set(cx+i, hs[i][j]-1, cz+j, 106, r.nextInt(4));
							}
							break;
						case 10:// Haus
							int cs = (dataOfMe[i+15][j+16]==10?1:0) + (dataOfMe[i+16][j+15]==10?1:0) + (dataOfMe[i+17][j+16]==10?1:0) + (dataOfMe[i+16][j+17]==10?1:0);
							if(cs!=0 && cs!=4){// Wand
								for(int y = (int) wallNoise(i+cx, j+cz, r);y>-2;y--){
									set(i+cx, y+hs[i][j], j+cz, stone(r));
								}
							} else {// Boden + Decken
								int maxy = (int) (floorNoise(i+cx,j+cz, r)*0.95)-3;
								for(int y=0;y<maxy;y+=4){
									if(r.next()<0.9){
										if(r.next()<0.2){
											set(i+cx, hs[i][j]+y, j+cz, 43, 0);
										} else if(r.next()<0.9){
											set(i+cx, hs[i][j]+y, j+cz, 44, 0);
										} else {
											set(i+cx, hs[i][j]+y, j+cz, 44, 8);
										}
									}
								}
							}
							if(r.next()<0.01 && r.next()<0.07){
								Generator.setChest(w, cx+i, hs[i][j], cz+j, WorldGen.trashChest(new Random(cz+j, basey+hs[i][j], cx+i)));
							}
							break;
						case 11:// Garten/Park
							if(r.next()<0.1){
								set(i+cx, hs[i][j]+1, j+cz, blume(r));
							}
							break;
						case 13:// Schulgel�nde / Parkhaus^^
							if(r.next()<0.9){
								set(i+cx, hs[i][j]+1, j+cz, 44, 0);
							}
							break;
						case 15:// Parkplatz, privat
							if(rh[i][j]-hs[i][j]>0.5){
								set(i+cx, hs[i][j], j+cz, 126, 5);
							} else set(i+cx, hs[i][j], j+cz, 0, 0);
							set(i+cx, hs[i][j]-1, j+cz, 5, 5);
							set(i+cx, hs[i][j]-2, j+cz, 13, 0);
							
							break;
						case 16://Garten naja...
							set(i+cx, hs[i][j]-1, j+cz, 3, 0);
							if(r.next()<0.1){
								set(i+cx, hs[i][j]+1, j+cz, blume(r));
							} else if(r.next()<0.1){
								set(i+cx, hs[i][j], j+cz, 12, 0);
							}
							break;
						case 25:// Parkplatz, f.Gesch�fte
							if(rh[i][j]-hs[i][j]>0.5){
								set(i+cx, hs[i][j], j+cz, 126, 1);
							} else set(i+cx, hs[i][j], j+cz, 0, 0);
							set(i+cx, hs[i][j]-1, j+cz, 5, 1);
							set(i+cx, hs[i][j]-2, j+cz, 13, 0);
							break;
						case 30:
							// Flussgebiet wird aufgrund des Laggs vom MapsGenerator �bernommen
							for(int a=(int) hh[i][j];a>=rh[i][j];a--){
								set(cx+i, a, cz+j, 0, 0);
							}
							// im Flussgebiet ist hs der Flussboden und rh die Flussh�he -> diese werden oben berechnet
							
							if(r.next()<1.0/(hh[i][j]-hs[i][j])){
								set(cx+i, hs[i][j]-1, cz+j, 2, 0);
							} else {
								set(cx+i, hs[i][j]-1, cz+j, r.next()<0.3?12:82, 0);
							}
							
							if(r.next()<0.4){
								set(cx+i, hs[i][j]-2, cz+j, r.next()<0.4?12:r.next()<0.5?82:3, 0);
								if(r.next()<0.4){
									set(cx+i, hs[i][j]-3, cz+j, r.next()<0.4?12:r.next()<0.6?82:3, 0);
									if(r.next()<0.4){
										set(cx+i, hs[i][j]-4, cz+j, r.next()<0.4?12:r.next()<0.7?82:3, 0);
										if(r.next()<0.4){
											set(cx+i, hs[i][j]-5, cz+j, r.next()<0.4?12:r.next()<0.7?82:3, 0);
										} else {
											set(cx+i, hs[i][j]-5, cz+j, 1, 0);
										}
									} else {
										set(cx+i, hs[i][j]-4, cz+j, 1, 0);
										set(cx+i, hs[i][j]-5, cz+j, 1, 0);
									}
								} else {
									set(cx+i, hs[i][j]-3, cz+j, 1, 0);
									set(cx+i, hs[i][j]-4, cz+j, 1, 0);
									set(cx+i, hs[i][j]-5, cz+j, 1, 0);
								}
							} else {
								set(cx+i, hs[i][j]-2, cz+j, 1, 0);
								set(cx+i, hs[i][j]-3, cz+j, 1, 0);
								set(cx+i, hs[i][j]-4, cz+j, 1, 0);
								set(cx+i, hs[i][j]-5, cz+j, 1, 0);
							}
							
							for(int a=hs[i][j];a<rh[i][j];a++){
								set(cx+i, a, cz+j, 8, 0);
							}
							
							if(hs[i][j]<rh[i][j]){
								if(r.next()<0.03){
									set(cx+i, (int) rh[i][j]+1, cz+j, 111, 0);
								}
								
							} else {
								if(r.next()<0.03){
									int h=r.nextInt(3)+2;{
										for(;h>0;h--){
											set(cx+i, (int) rh[i][j]+h, cz+j, 83, 0);
										}
										set(cx+i, (int) rh[i][j], cz+j, 12, 0);
									}
								}
							}
							
							break;
						case 32://Sportplatz
							set(i+cx, hs[i][j], j+cz, 159, 5);
							break;
						case 36://Friedhof
							cs = (dataOfMe[i+15][j+16]==36?1:0) + (dataOfMe[i+16][j+15]==36?1:0) + (dataOfMe[i+17][j+16]==36?1:0) + (dataOfMe[i+16][j+17]==36?1:0) + 
								 (dataOfMe[i+15][j+16]==10?1:0) + (dataOfMe[i+16][j+15]==10?1:0) + (dataOfMe[i+17][j+16]==10?1:0) + (dataOfMe[i+16][j+17]==10?1:0);
							if(cs>2){// auf dem Friedhof/an einer Mauer
								set(cx+i, hs[i][j], cz+j, 110, 0);//Myzeluntergrund :)
								if(r.next()<0.0005){// gro�es Grab
									int y = hs[i][j];
									// Boden
									if(r.next()<0.3){
										for(int a=-1+r.next()<0.5?1:r.next()<0.3?2:0;a<7;a++){
											for(int b=-3;b<4;b++){
												set(cx+i+a, y-7, cz+j+b, stone(r));
												for(int d=3;d<7;d++){
													if(a==-1 || a==6 || b==-3 || b==3){
														set(cx+i+a, y-d, cz+j+b, stone(r));
													} else {
														if(d==6 && r.next()<0.3){// die 17 ist ok, da sie unterirdisch ist :)
															set(cx+i+a, y-d, cz+j+b, r.next()<0.7?17:r.next()<0.3?41:42, 0);
														} else set(cx+i+a, y-d, cz+j+b, 0, 0);
													}
												}
												set(cx+i+a, y-2, cz+j+b, stone(r));
												set(cx+i+a, y-1, cz+j+b, 2, 0);
												set(cx+i+a, y, cz+j+b, 110, 0);
											}
										}
									}
									y++;
									// das eigentliche Grab
									set(cx+i-2, y, cz+j-2, 44, 5);set(cx+i-2, y, cz+j-1, 44, 5);
									set(cx+i-2, y, cz+j+2, 44, 5);set(cx+i-2, y, cz+j+1, 44, 5);
									
									set(cx+i, y, cz+j-2, 44, 5);set(cx+i+1, y, cz+j-2, 44, 5);
									set(cx+i, y, cz+j, 44, 5);set(cx+i+1, y, cz+j, 44, 5);
									set(cx+i, y, cz+j+2, 44, 5);set(cx+i+1, y, cz+j+2, 44, 5);
									
									set(cx+i+3, y, cz+j-2, 44, 5);set(cx+i+4, y, cz+j-2, 44, 5);
									set(cx+i+3, y, cz+j, 44, 5);set(cx+i+4, y, cz+j, 44, 5);
									set(cx+i+3, y, cz+j+2, 44, 5);set(cx+i+4, y, cz+j+2, 44, 5);
									y--;
									set(cx+i+5, y, cz+j-3, guMau(r));set(cx+i+5, y, cz+j-2, guMau(r));set(cx+i+5, y, cz+j-1, guMau(r));set(cx+i+5, y, cz+j, guMau(r));
									set(cx+i+5, y, cz+j+3, guMau(r));set(cx+i+5, y, cz+j+2, guMau(r));set(cx+i+5, y, cz+j+1, guMau(r));
									y++;
									set(cx+i+5, y, cz+j-3, guMau(r));set(cx+i+5, y, cz+j-2, guMau(r));set(cx+i+5, y, cz+j-1, guMau(r));set(cx+i+5, y, cz+j, guMau(r));
									set(cx+i+5, y, cz+j+3, guMau(r));set(cx+i+5, y, cz+j+2, guMau(r));set(cx+i+5, y, cz+j+1, guMau(r));
									y++;
									set(cx+i+5, y, cz+j-2, guMau(r));set(cx+i+5, y, cz+j-1, guMau(r));set(cx+i+5, y, cz+j, guMau(r));
									set(cx+i+5, y, cz+j+2, guMau(r));set(cx+i+5, y, cz+j+1, guMau(r));
									y++;
									set(cx+i+5, y, cz+j-1, guMau(r));set(cx+i+5, y, cz+j, guMau(r));
									set(cx+i+5, y, cz+j+1, guMau(r));
									y++;
									set(cx+i+5, y++, cz+j, guMau(r));
									set(cx+i+5, y++, cz+j, guMau(r));
									set(cx+i+5, y++, cz+j, guMau(r));
									
									set(cx+i+5, y, cz+j-1, guMau(r));set(cx+i+5, y, cz+j, guMau(r));
									set(cx+i+5, y, cz+j+1, guMau(r));
									set(cx+i+5, ++y, cz+j, guMau(r));
									
								} else if(r.next()<0.1){//kleines Grab
									if(i%4==0){//oberer Grabteil
										set(cx+i, hs[i][j]+1, cz+j, stone(r));
										if(r.next()<0.4){
											set(cx+i, hs[i][j]+2, cz+j, stone(r));
											if(r.next()<0.3){
												set(cx+i, hs[i][j]+3, cz+j, stone(r));
											}
										}else if(r.next()<0.3){
											set(cx+i, hs[i][j]+2, cz+j, 44, r.next()<0.67?r.next()<0.5?0:3:5);
										}
									} else if(i%4==1){// unterer Grabteil
										set(cx+i, hs[i][j]+1, cz+j, 44, r.next()<0.67?r.next()<0.5?0:3:5);
									}
								}
							} else if(cs!=0){
								set(cx+i, hs[i][j]-1, cz+j, stone(r));
								set(cx+i, hs[i][j], cz+j, stone(r));
								set(cx+i, hs[i][j]+1, cz+j, stone(r));
								if(r.next()<0.25){// Pfeiler in der Mauer
									set(cx+i, hs[i][j]+2, cz+j, stone(r));
									set(cx+i, hs[i][j]+3, cz+j, stone(r));
								} else {
									set(cx+i, hs[i][j]+2, cz+j, 101, 0);
									set(cx+i, hs[i][j]+3, cz+j, 101, 0);
								}
							}
							break;
						case 41:// Gewerbegebiet(Rasen mit Erde drunter)
							set(i+cx, hs[i][j], j+cz, 44, 0);
							break;
						case 43:// Kirche zumindest manchmal ^^
							set(i+cx, hs[i][j], j+cz, 159, 0);
							break;
						case 50:// Kraftwerksgebiet - naja vllt manchmal
							if(r.next()<0.97){
								set(i+cx, hs[i][j], j+cz, 13, 0);
							}
							break;
						case 60://Wiese -> kA welche Art...
							if(r.next()<0.3){
								set(i+cx, hs[i][j], j+cz, 31, 1);
							}
							if(r.next()<0.01){
								set(i+cx, hs[i][j]+1, j+cz, blume(r));
							}
							break;
						case 2://Mischwald
						case 61://Wald
						case 161:
						case 261:
						case 75:
							if(r.next()<0.9){
								set(cx+i, hs[i][j]-1, cz+j, 3, 0);
							}
							if(r.next()<0.003){
								set(cx+i, hs[i][j], cz+j, 106, r.nextInt(4));
							}
							break;
						case 63:// "Palmenallee"
							break;
						case 70:// F��g�ngerzone/Steinplatz
							if(rh[i][j]-hs[i][j]>0.5){
								set(i+cx, hs[i][j]+1, j+cz, 44, 5);
							} else {
								set(i+cx, hs[i][j], j+cz, stone(r));
							}
							break;
						case 71:// ---Bunter-Blumenrand--- nicht mehr, sondern ein paar Bl�mchen, Hecken weniger und halt so...
							cs = (dataOfMe[i+15][j+16]==71?1:0) + (dataOfMe[i+16][j+15]==71?1:0) + (dataOfMe[i+17][j+16]==71?1:0) + (dataOfMe[i+16][j+17]==71?1:0);
							if(cs==0){//mache nichts, denn einfach ne Hecke so macht nix Sinn
								if(r.next()<0.02){
									// Setzlinge
									set(cx+i, hs[i][j]+1, cz+j, 6, r.nextIntSQ(6));
								}
							} else if(cs==4){// Bl�mchen
								if(r.next()<0.1){
									set(cx+i, hs[i][j]-1, cz+j, 3, 0);
									if(r.next()<0.03){
										set(cx+i, hs[i][j]+1, cz+j, 31, 1);
									} else {
										set(cx+i, hs[i][j]+1, cz+j, blume(r));
									}
								} else if(r.next()<0.1){
									set(cx+i, hs[i][j], cz+j, 3, 1);
								}
							} else if(r.next()<0.2){// am Rand -> Hecke :)
								if(r.next()<0.3){
									set(i+cx, hs[i][j]-1, j+cz,  3, 0);
									set(i+cx, hs[i][j]  , j+cz, 17, 0);// unter der Hecke, also ok :) -> wird sp�ter nen Busch
								}
								set(i+cx, hs[i][j]+1, j+cz, 18, 4);
								if(r.next()<0.2)set(i+cx, hs[i][j]+2, j+cz, 18, 4);
							}
							break;
						case 72:// eigentlich genauso
						case 73:// B�ume, wenig Dreck
							if(r.next()<0.1){
								set(i+cx, hs[i][j], j+cz, 3, 1);
							} else if(r.next()<0.1){
								set(i+cx, hs[i][j], j+cz, 12, 2);
							} else if(r.next()<0.1){
								set(cx+i, hs[i][j]+1, cz+j, 31, 1);
							}
							break;
						case 74:// Baume, mehr Dreck
							if(r.next()<0.3){
								set(i+cx, hs[i][j], j+cz, 3, 1);
							} else if(r.next()<0.15){
								set(i+cx, hs[i][j], j+cz, 12, 2);
							}
							break;
						case 76://Hohes Gras
							if(r.next()<0.01){
								set(cx+i, hs[i][j]+1, cz+j, blume(r));break;
							} else if(r.next()<0.4){
								//Hohes Gras
								set(cx+i, hs[i][j]+1, cz+j, 175, 2);
								set(cx+i, hs[i][j]+2, cz+j, 175, 10);
							} else if(r.next()<0.4){
								// flaches Gras
								set(cx+i, hs[i][j]+1, cz+j, 31, 1);
							}
							break;
						case 77:// Bauhof -> Abstellplatz f�r Baumaschienen
							if(r.next()<0.3){
								set(i+cx, hs[i][j], j+cz, 3, 1);
							} else if(r.next()<0.35){
								set(i+cx, hs[i][j], j+cz, 12, 2);
							}
							if(r.next()<0.01){
								set(i+cx, hs[i][j]-1, j+cz, 155, r.next()<0.2?3:r.next()<0.2?1:0);
							} else if(r.next()<0.003){
								set(i+cx, hs[i][j]-1, j+cz, 42, 0);
							} else if(r.next()<0.001){
								set(i+cx, hs[i][j]-1, j+cz, 121, 0);
							}
							break;
						case 121:
							if(rh[i][j]-hs[i][j]>0.5 && r.next()<0.3){
								set(i+cx, hs[i][j]+1, j+cz, 44, 1);
							} else {
								if(r.next()<0.3){
									set(i+cx, hs[i][j], j+cz, 24, 0);
								} else {
									set(i+cx, hs[i][j], j+cz, 12, 2);
								}
							}
							break;
						case 130://0xc6e0d1 Bach
							set(i+cx, hs[i][j], j+cz, 8, 0);
							break;
						case 3://entwaldetes Gebiet...
							// B�sche im 2. Teil
							if(r.next()<0.2){
								set(i+cx, hs[i][j], j+cz, 3, r.next()<0.2?1:r.next()<0.3?2:0);
							} else if(r.next()<0.14){
								set(i+cx, hs[i][j], j+cz, 12, 2);
							}
							break;
						default:
							//if(dataOfMe[i+16][j+16]+15<256)
							//	set(i+cx, hs[i][j], j+cz, dataOfMe[i+16][j+16]+15, 0);
						}
						
						
					} else {// Berge 100%
						// was gibts da eigentlich zu machen? Schnee kommt von allein
					}
				}
			}
			
			for(int i=0;i<48;i++){
				for(int j=0;j<48;j++){
					dataOfMe[i][j] = Converter.getCubeID(cx+i-16,cz+j-16);
				}
			}
			for(int a=-1;a<2;a++){
				for(int b=-1;b<2;b++){
					
					Random r2 = new Random(cx+a*16, 0, cz+b*16);
					
					for(int i=0;i<16;i++){
						for(int j=0;j<16;j++){
							switch(dataOfMe[16+i+16*a][16+j+16*b]){
							case 3://entwaldetes Gebiet -> braucht B�sche :)
								if(r2.next()<0.01){
									Tree.littleBush(w, cx+i, (int) Jena.h(cx+i+a*16, cz+j+b*16)-1-basey, cz+j, cx, basey, cz, r.nextInt(4), r.nextInt(3)-1);
								}
								break;
							case 63:// "Palmenallee"
								if(r2.next()<0.07){
									tree(cx+i+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, cz+j+b*16, cx, basey, cz, Tree.TreeType.PALME);
								}
								break;
							case 71:// ---Bunter-Blumenrand--- nicht mehr, sondern ein paar Bl�mchen, Hecken weniger und halt so...
							case 72:// eigentlich genauso
							case 73:// B�ume, wenig Dreck
							case 74:// B�ume, mehr Dreck
								if(r2.next()<0.003){
									tree(cx+i+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, cz+j+b*16, cx, basey, cz, r2.next()<0.4?Tree.TreeType.BIRCH:r2.next()<0.2?Tree.TreeType.BIGTREE:Tree.TreeType.TREE);
								}
								break;
							case 61://Wald
							case 75:
								if(r2.next()<0.01){
									tree(cx+i+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, cz+j+b*16, cx, basey, cz, r2.next()<0.4?Tree.TreeType.BIRCH:r2.next()<0.2?Tree.TreeType.BIGTREE:Tree.TreeType.TREE);
									
									// Pilze 39Braun/40Rot -> werden ab und zu nicht gesetzt, ist aber doch eigentlich ziemlich egal...
									if(r2.next()<0.004){set(i+cx+1+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz+b*16, r2.next()<0.5?39:40, 0);}
									if(r2.next()<0.004){set(i+cx-1+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz+b*16, r2.next()<0.5?39:40, 0);}
									if(r2.next()<0.004){set(i+cx+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz+1+b*16, r2.next()<0.5?39:40, 0);}
									if(r2.next()<0.004){set(i+cx+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz-1+b*16, r2.next()<0.5?39:40, 0);}
								}
								break;
							case 1://Nadelwald
								if(r2.next()<0.01){
									tree(cx+i+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, cz+j+b*16, cx, basey, cz, r2.next()<0.1?Tree.TreeType.FALLENFICHTE:Tree.TreeType.FICHTE);
									
									// Pilze 39Braun/40Rot -> werden ab und zu nicht gesetzt, ist aber doch eigentlich ziemlich egal...
									if(r2.next()<0.004){set(i+cx+1+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz+b*16, r2.next()<0.5?39:40, 0);}
									if(r2.next()<0.004){set(i+cx-1+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz+b*16, r2.next()<0.5?39:40, 0);}
									if(r2.next()<0.004){set(i+cx+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz+1+b*16, r2.next()<0.5?39:40, 0);}
									if(r2.next()<0.004){set(i+cx+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz-1+b*16, r2.next()<0.5?39:40, 0);}
								}
								break;
							case 2://Mischwald
								if(r2.next()<0.01){
									tree(cx+i+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, cz+j+b*16, cx, basey, cz, r2.next()<0.27?Tree.TreeType.BIRCH:r2.next()<0.6?r2.next()<0.2?Tree.TreeType.BIGTREE:Tree.TreeType.TREE:r2.next()<0.1?Tree.TreeType.FALLENFICHTE:Tree.TreeType.FICHTE);
									
									// Pilze 39Braun/40Rot -> werden ab und zu nicht gesetzt, ist aber doch eigentlich ziemlich egal...
									if(r2.next()<0.004){set(i+cx+1+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz+b*16, r2.next()<0.5?39:40, 0);}
									if(r2.next()<0.004){set(i+cx-1+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz+b*16, r2.next()<0.5?39:40, 0);}
									if(r2.next()<0.004){set(i+cx+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz+1+b*16, r2.next()<0.5?39:40, 0);}
									if(r2.next()<0.004){set(i+cx+a*16, (int) Jena.h(cx+i+a*16, cz+j+b*16)+1-basey, j+cz-1+b*16, r2.next()<0.5?39:40, 0);}
								}
								break;
							}
						}
					}
				}
			}
			c.getBlock(0, 0, 0).setMetadata(MetaString.weatherTree, new FixedMetadataValue(Plugin.instance, Weather.jetztTime()));
		} else {
			// Schnee sollte schon dekoriert sein: also Kakteen f�r die W�ste
			double jenah;
			for(int a=-5;a<6;a++){
				for(int b=-5;b<6;b++){
					// baue alle Geb�ude...
					if(Jena.type4gen(cx+16*a, cz+16*b)>2.1){
						Random r = new Random(cx+16*a, basey, cz+16*b);
						if(r.next()<0.001 && r.next()>0.75){// 1:4k
							WüstenDeko.generateOase(w, cx+16*a+r.nextInt(16), 0, cz+16*b+r.nextInt(16), cx, basey, cz);
						} else if(r.next()<0.001){// 1:1k
							WüstenDeko.generateObelisk(w, cx+16*a+r.nextInt(16), 0, cz+16*b+r.nextInt(16), cx, basey, cz);
						} else if(r.next()<0.001){// 1:1k
							WüstenDeko.generatePyramide(w, cx+16*a+r.nextInt(16), 0, cz+16*b+r.nextInt(16), cx, basey, cz);
						}
					}
				}
			}
			for(int a=-1;a<2;a++){
				for(int b=-1;b<2;b++){
					Random r = new Random(cx+16*a, basey, cz+16*b);
					for(int i=0;i<16;i++){
						for(int j=0;j<16;j++){
							if(r.next()<0.001 && sq(cx+16*a)+sq(cz+16*b)>3025000000.0 && (jenah=Jena.h(a*16+i+cx, b*16+j+cz))>=4800 && jenah<4812 && r.nextInt(12)>jenah-4800){
								Tree.generateTree(w, cx+a*16+i, (int) (jenah-basey), cz+b*16+j, cx, basey, cz, TreeType.PALME);
							} else if(r.next()<0.001 && a==0 && b==0){
								int y = (int) (Jena.h(cx+i, cz+j)-basey);
								if(y+basey>4800 && w.getBlockAt(cx+i, y, cz+j).getType()==Material.SAND){
									for(int h=r.nextInt(3)+2;h>0;h--){
										set(cx+i, y+h, cz+j, MathHelper.kaktus, 0);
									}
								}
							} else if(r.next()<0.001 && a==0 && b==0){
								set(cx+i, (int) (Jena.h(cx+i, cz+j)-basey)+1, cz+j, 32, 0);
							}
						}
					}
				}
			}
		}
		
		generated++;
		
		if(generated%100==0){
			System.out.println(" +");
		}
		
		timeused+=System.currentTimeMillis()-time;
		if(timeused>10000){
			System.out.println("_ "+generated+" in "+timeused);
			timeused = generated = 0;
		}
	}
	
	static long timeused;
	static int generated;

	private void tree(int x, int y, int z, int cx, int basey, int cz, Tree.TreeType type) {
		Tree.generateTree(w, x, y, z, cx, basey, cz, type);
	}

	public static boolean not12or30(int i){return !(i==12 || i==30);}
	
	private int[] guMau(Random r){// gute Mauer
		return new int[]{98, r.next()<0.7?0:r.next()<0.4?1:2};
	}

	public static int[] blume(Random r) {
		if(r.next()<0.07){
			return new int[]{175, r.nextInt(6), 175, 10};
		} else {
			switch((int) (r.next()*6)){
			case 0:return new int[]{r.next()<0.5?37:38, 0};
			case 1:return new int[]{38, r.next()<0.5?1:2};
			case 2:return new int[]{38, r.next()<0.5?3:8};
			case 3:return new int[]{38, r.next()<0.5?r.next()<0.5?4:5:r.next()<0.5?6:7};
			case 4:return new int[]{38, r.next()<0.333?9:r.next()<0.5?10:11};
			case 5:return new int[]{38, r.next()<0.5?12:13};
			default:return new int[]{57, 0};// wenn das auftritt, habe ich etwas falsch gemacht oder r.next() ist kaputt
			}
		}
	}

	private void set(int i, int j, int k, int[] data) {
		set(i, j, k, data[0], data[1]);
		if(data.length==4){
			set(i, j+1, k, data[2], data[3]);
		}
	}

	private int[] stone(Random r) {
		if(r.next()<0.5){
			return new int[]{r.next()<0.05?97:98, 0};
		} else {
			double t=r.next();
			if(t<0.3){
				return new int[]{1, 0};
			} else if(t<0.5){
				return new int[]{13, 0};
			} else if(t<0.7){
				return new int[]{r.next()<0.05?97:98, 1};
			} else if(t<0.9){
				return new int[]{r.next()<0.05?97:98, 2};
			} else {
				return new int[]{109, (int) (r.next()*8)};
			}
		}
	}

	double wallNoise(int x, int z, Random r) {
		x = MathHelper.ori(x);
		z = MathHelper.ori(z);
		if(x*x+z*z<900){
			return Jena.getDry(x*12, z*12)*0.167+110+r.next()*7;
		} else {
			return Jena.getDry(x*7, z*7)*(800/(x*x+z*z+100)+1)*0.1+r.next()*4;// getDry[0, 180]
		}
	}
	
	double floorNoise(int x, int z, Random r) {
		x = MathHelper.ori(x);
		z = MathHelper.ori(z);
		if(x*x+z*z<257){
			return Jena.getDry(x*12, z*12)*0.167+110+r.next()*7;
		} else if(x*x+z*z<1000){
			return 10;
		} else {
			return Jena.getDry(x*7, z*7)*(800/(x*x+z*z+100)+1)*0.1+r.next()*4;// getDry[0, 180]
		}
	}

	@SuppressWarnings("deprecation")
	void set(int x, int y, int z, int id, int data) {
		if(y>-1 && y<256){
			w.getBlockAt(x,y,z).setTypeIdAndData(id, (byte) data, true);
			//Generator.sB(w, x, y, z, id, data); ist zwar schneller, macht aber Schatten nicht richtig.. solange es nur wenige Bl�cke sind machts nichts...
		}
	}
}
