package me.corperateraider.reload;

public class C {
	public static double lng(String worldname, int x, int z){
		return 0.000012*x;
	}
	
	public static double lat(String worldname, int x, int z){
		return -0.000012*z;
	}
	
	public static int[] choseColor(int r, int g, int b){
		double db = 2000;
		int id = 0, data = 0;
		for(int i=0;i<cO.wool.length;i++){
			if(qu(db)>qu(cO.wool[i].r-r)+qu(cO.wool[i].g-g)+qu(cO.wool[i].b-b)){
				db=Math.sqrt(qu(cO.wool[i].r-r)+qu(cO.wool[i].g-g)+qu(cO.wool[i].b-b));
				id=cO.wool[i].id;
				data=cO.wool[i].data;
			}
		}
		if(id==0){id=1;}
		
		return new int[]{id,data};
	}
	
	public static int getIndexByColor(int r, int g, int b){
		double distance = 1E300, d;
		int index = 0;

		for(int i=0;i<cO.wool.length;i++){
			if(distance > (d=qu(cO.wool[i].r-r)+qu(cO.wool[i].g-g)+qu(cO.wool[i].b-b))){
				distance = d;
				index = i;
			}
		}
		
		return index;
	}
	
	public static cO byID(int index){
		return cO.wool[index];
	}
	
	public static int getID(int index){
		return cO.wool[index].id;
	}
	
	public static int getData(int index){
		return cO.wool[index].data;
	}
	
	public static double qu(double i){return i*i;}
	
	static class cO {//colorObject
		int r,g,b,id,data;
		static cO[] wool = {
		//Wollfarben
			new cO(218,218,218,0),new cO(218,124,60,1),new cO(177,68,186,2),new cO(105,137,201,3),
			new cO(189,177,43,4),new cO(67,174,56,5),new cO(207,131,152,6),new cO(65,65,65,7),
			new cO(160,160,160,8),new cO(46,110,136,9),new cO(124,60,180,10),new cO(45,56,139,11),
			new cO(79,50,31,12),new cO(53,71,27,13),new cO(150,51,48,14),new cO(21,17,17,15),
		//Stein
			new cO(170,160,160,1,0),//Clean Stone
			new cO(116,116,116,4,0),//Cobble
			new cO(109,109,109,98,0),new cO(102,109,89,98,1),new cO(99,99,99,98,2),new cO(103,103,103,98,3),
			// 1.8: new cO(142,104,89,1,1),new cO(155,122,109,1,2),new cO(164,164,164,1,3),new cO(188,188,188,1,4),new cO(122,122,122,1,5),new cO(147,147,147,1,6),
			
			new cO(100,160,55,2,0),	new cO(120,85,58,3,0),/*Gras doch, weil man nur von oben guckt!*/
			
			//Holzbretter	
			new cO(115,94,56,5,0),new cO(102,79,47,5,1),new cO(216,204,141,5,2),new cO(184,135,100,5,3),new cO(200,104,52,5,4),new cO(85,52,17,5,5),
			
			new cO(51,51,51,7,0),//new cO(0,0,255,9,0),<- Wasser
			new cO(189,159,107,14,0),new cO(160,142,131,15,0),new cO(102,102,102,16,0),
			new cO(103,82,50,17,12),new cO(50,30,10,17,13),new cO(187,184,176,17,14),new cO(70,53,19,17,15),//new cO(102,93,82,162,12),new cO(42,31,13,162,13),
			new cO(193,193,57,19,0),
			new cO(95,113,147,21,0),
			new cO(34,77,161,22,0),
			new cO(216,208,157,24,0),new cO(202,194,134,24,1),new cO(219,210,161,24,2),
			new cO(255,242,68,41,0),new cO(234,234,234,42,0),
			new cO(155,86,67,45,0),
			new cO(94,114,13,47,0),//bissl schwierig mit den vielen Farbe
			new cO(34,58,35,48,0),
			//new cO(23,16,35,49,0),//Obsidian ist theoretisch nett doch nur im Standartpack
			new cO(157,189,191,56,0),new cO(129,228,224,57,0),
			new cO(191,124,124,73,0),//rederz
			new cO(238,255,255,80,0),//Schnee
			new cO(200,124,20,86,0),//kürbisdunkel -> meistgesehene Seite
			new cO(136,53,53,87,0),//netherrack
			new cO(73,55,44,88,0),//soulsand
			//new cO(91,0),//kürbishell
			new cO(124,123,25,103,0),//melone
			new cO(47,17,21,112,0),//netherbrick
			new cO(212,211,148,121,0),//endstone
			new cO(115,164,134,129,0),//smaerz
			new cO(62,216,106,133,0),//smablock
			new cO(168,30,9,152,0),//redblock
			new cO(237,235,229,155,0),new cO(227,223,213,155,1),new cO(225,220,211,155,2),//Streifen
			//new cO(170,0),//ab 1.6, heu
			new cO(30,28,28,173,0),//coalblock
			
			
			
		//Stained Clay
			new cO(164,170,181,82,0),
			new cO(166,139,128,159,0),new cO(128,64,32,159,1),new cO(128,64,96,159,2),new cO(96,96,128,159,3),
			new cO(148,108,32,159,4),new cO(81,92,42,159,5),new cO(127,60,60,159,6),new cO(46,35,28,159,7),
			new cO(105,83,76,159,8),new cO(73,73,73,159,9),new cO(91,54,67,159,10),new cO(57,46,71,159,11),
			new cO(60,39,28,159,12),new cO(59,64,33,159,13),new cO(111,46,35,159,14),new cO(29,17,13,159,15),
		
			
			
			//1,1.1,1.2,1.3,1.4,1.5,1.6
			//3,4,5-5.5,14,15,16,17-17.5,18-18.3,21,22,24-24.2,25,35-35.15,41,42,45,47,48,49,56,57,
			//58,61,73,79,80,82,86,87,88,91,168-168.2,170,98-98.3,103,112,121,123,129,133,155-155.2,
			//159-159.15,162,162.1,165,172,173,174(?)
		};	
		public cO(int x, int y, int z, int d, int d2){
			r=x;
			g=y;
			b=z;
			id=d;
			data=d2;
		}
		public cO(int x, int y, int z, int d2){
			r=x;
			g=y;
			b=z;
			id=35;
			data=d2;
		}
	}
}
