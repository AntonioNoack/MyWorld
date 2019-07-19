package me.corperateraider.myworld;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import me.corperateraider.generator.Generator;
import me.corperateraider.generator.MathHelper;
import me.corperateraider.reload.Jena;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import converter.Converter;

public class Grundstück extends MathHelper {
	
	public static void main(String[] args) throws IOException{
		
		ini(new File("C:/Users/Antonio/Desktop/Plugins/_Server/plugins/MyWorld/"));
		
		index = 0;
		Grundstück g = place.get(0);
		
		final JFrame f = new JFrame(g.owner+": "+g.name);
		f.setBounds(0,0,500,500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(null);
		
		BufferedImage img = new BufferedImage(500,500,1);
		
		for(int i=0;i<g.r21;i++){
			for(int j=0;j<g.r21;j++){
				if(g.dat[i][j])
					img.setRGB(i, j, 0xff0000);
				else if(i+1==g.r21 || j+1==g.r21)
					img.setRGB(i, j, 0x00ff00);
			}
		}
		
		final JLabel l = new JLabel(new ImageIcon(img));
		l.setBounds(1,1,500,500);
		
		f.add(l);
		f.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(e.isControlDown()){
					try {
						ini(new File("C:/Users/Antonio/Desktop/Plugins/_Server/plugins/MyWorld/"));
					} catch (IOException e1) {}
				} else {
					index = (index+1)%place.size();
				}
				
				Grundstück g = place.get(index);
				
				BufferedImage img = new BufferedImage(500,500,1);
				int k = 0;
				for(int i=0;i<g.r21;i++){
					for(int j=0;j<g.r21;j++){
						if(g.dat[i][j]){
							img.setRGB(i, j, 0xff0000);
							k++;
						} else if(i+1==g.r21 || j+1==g.r21)
							img.setRGB(i, j, 0x00ff00);
					}
				}
				
				l.setIcon(new ImageIcon(img));
				f.setTitle(g.owner+": "+g.name+" "+k+"m�");
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			
		});
		f.setVisible(true);
	}
	static int index;
	
	private boolean changed = false;
	private String name;
	private String owner;// immer lowerCase!
	final int x, z, r, r21;// Grundst�cke gehen von -64 bis ganz nach oben
	
	private boolean[][] dat;//r21�
	boolean contains(int x, int z) {
		x+=r;z+=r;
		if(x<0 || z<0 || x>=r21 || z>=r21) return false;
		return dat[x][z];
	}
	
	static ArrayList<Grundstück> place = new ArrayList<>();
	static boolean isPartOfGrndstck(int x, int z){return Converter.getCubeID(x, z)==10;}
	static boolean isNearGrundstück(int x, int z){
		return Converter.getCubeID(x, z)==10 || Converter.getCubeID(x+1, z)==10 || Converter.getCubeID(x-1, z)==10 || Converter.getCubeID(x, z+1)==10 || Converter.getCubeID(x, z-1)==10;}
	static boolean isGrundstück(int x, int z){
		return Converter.getCubeID(x, z)==10 && Converter.getCubeID(x+1, z)==10 && Converter.getCubeID(x-1, z)==10 && Converter.getCubeID(x, z+1)==10 && Converter.getCubeID(x, z-1)==10;}
	
	/**
	 * if player ownes a nearby plot: false
	 * </br>if another player ownes a plot: true
	 * </br>else: false
	 * */
	static boolean isNearProtectedGrundstück(String owner, int x, int z){
		// wenn du der K�nig bist...
		if(owner.equalsIgnoreCase(Plugin.kingsname)){
			for(Grundstück g:place){
				if(sq(g.x-x)+sq(g.z-z)<sq(g.r+100) && (g.owner.equalsIgnoreCase("@king") || g.owner.equalsIgnoreCase(owner))){
					return false;
				}
			}
		} else {// wenn du ein B�rger bist...
			for(Grundstück g:place){// geh�rt dir ein anliegendes Grundst�ck?
				if(sq(g.x-x)+sq(g.z-z)<sq(g.r+100) && (g.owner.equalsIgnoreCase(owner) || (g.owner.equalsIgnoreCase("@king") && Rank.hasPermisson(owner, "build "+g.name)))){
					return false;
				}
			}
		}
		// bist du in der N�he eines fremden Grundst�ckes?
		for(Grundstück g:place){
			if(sq(g.x-x)+sq(g.z-z)<sq(g.r+100)){
				return true;
			}
		}
		return false;
	}
	
	public String getOwner(){
		return owner;
	}
	
	public String getName(){
		return name;
	}
	
	
	/**
	 * returns the name of the owner
	 * </br>if no owner aviable: null
	 * */
	public static String isOwned(int px, int pz) {
		Grundstück g = get(px, pz);
		if(g==null) return null;
		else		return g.owner;
	}
	
	public static Grundstück get(int px, int pz){
		for(Grundstück that:place){
			if(sq(that.x-px)+sq(that.z-pz)<sq(that.r+2)){
				if(that.contains(px-that.x, pz-that.z)){
					return that;
				}
			}
		}
		return null;
	}
	
	/**
	 * Ist das Grundst�ck k�niglich, und wenn ja, hat der Spieler darauf Rechte?
	 * Davor wurde schon abgefragt, dass das Grundst�ck ganz sicher eines ist...
	 * */
	public boolean isChangeAllowed(String name) {
		return owner.equals("@king") && Rank.hasPermisson(name, "build "+this.name);
	}
	
	public static boolean rename(String altname, String newname, String ownername) throws IOException{
		if(!saved){
			save(Plugin.instance.getDataFolder());
		}
		newname = newname.replace(' ', '_');
		for(Grundstück g:place){
			if(g.owner.equalsIgnoreCase(ownername) && altname.equalsIgnoreCase(g.name)){
				new File(Plugin.instance.getDataFolder(), "data/plots/"+g.name+"."+g.owner+".dat").renameTo(new File(Plugin.instance.getDataFolder(), "data/plots/"+newname+"."+g.owner+".dat"));
				g.name=newname;
				return true;
			}
		}
		return false;
	}

	public static void ini(File parent) throws IOException{
		
		place = new ArrayList<>();
		
		File uf = new File(parent, "data/plots");
		if(!uf.exists())uf.mkdirs();
		File[] fs = uf.listFiles();
		for(File f:fs){
			String s = f.getName().replace('\\', '/');
			s = s.split("/")[s.split("/").length-1];
			if(s.split("\\.").length>1){//name.owner.dat
				String name = s.split("\\.")[0], owner = s.split("\\.")[1];
				FileInputStream stream = new FileInputStream(f);// Instantiate 
				byte[] arr= new byte[(int) f.length()];
				stream.read(arr, 0, arr.length);
				stream.close();

				if(arr[0]=='0' || arr[0]=='1'){
					int i=1;
					String sx="", sz="", sr="";
					int x, z, r, r21;
					while(arr[i]!=' '){
						sx+=(char) arr[i++];
					}
					i++;
					while(arr[i]!=' '){
						sz+=(char) arr[i++];
					}
					i++;
					while(arr[i]!=' '){
						sr+=(char) arr[i++];
					}
					
					x = Integer.parseInt(sx);
					z = Integer.parseInt(sz);
					r = Integer.parseInt(sr);
					r21=r*2+1;
					
					int size = 0;
					// r ist der Radius...
					for(int a=-r;a<=r;a++){
						for(int b=-r;b<=r;b++){
							if(a*a+b*b<sq(r+1)) size++;
						}
					}
					
					boolean[] dat = booleansFromBytes(arr, i+1);
					
					if(arr[0]=='1'){
						i++;
						boolean[] ret = new boolean[size*2];
						boolean is;
						int pointer=0,j;
						for(;i<arr.length;i++){
							is = arr[i]<0;
							
							//System.out.print((is?"1":"0")+Integer.toHexString(arr[i]<0?256-arr[i]:arr[i]));
							
							j=(int)arr[i]&0x7f;
							for(;j>1;j--){
								ret[pointer++]=is;
							}
						}
						//System.out.println();
						//System.out.println(name);
						//System.out.println(size);
						//System.out.println(pointer);
						dat = ret;
					}
					
					if(dat.length>=size){
						boolean[][] data = new boolean[r21][r21];
						int k=0;
						for(i=0;i<r21;i++){
							for(int j=0;j<r21;j++){
								if(sq(i-r)+sq(j-r)<sq(r+1)){
									data[i][j]=dat[k++];
								}
							}
						}
						new Grundstück(owner, name, x, z, r, data).makeBeautiful();
					} else System.out.println("!Formaterror 0x03 \"Wrong filelenght\" while reading "+f.getAbsolutePath()+" "+dat.length+"/"+size);
				} else System.out.println("!Formaterror 0x02 \"Unknown format\" while reading "+f.getAbsolutePath());
			} else System.out.println("!Formaterror 0x01 \"Wrong filename\" while reading "+f.getAbsolutePath());
		}
	}
	
	public Grundstück(String owner, String name, int x, int z, int r, boolean[][] data){
		this.owner=owner.toLowerCase();
		this.name=name;
		this.x=x;
		this.z=z;
		this.r=r;
		r21=r*2+1;
		dat=data;
		place.add(this);
	}
	
	private static boolean[] booleansFromBytes(byte[] b, int startIndex) {
		boolean[] ret = new boolean[(b.length-startIndex)*8];
		int k = 0;
		for(int i=startIndex;i<b.length;i++){
			for(int j=0;j<8;j++){
				ret[k*8+j] = ((b[i]>>(7-j))&0b1) == 1;
			}
			k++;
		}
		return ret;
	}

	private static boolean saved, lauf=false;
	public static void save(File uf) throws IOException{
		int z = 0;
		for(Grundstück g:place){
			if(g.changed){
				z++;
				File f = new File(uf, "data/plots/"+g.name+"."+g.owner.toLowerCase()+".dat");
				FileWriter fw = new FileWriter(f);
				fw.write(lauf?"1":"0");//Speichertyp
				fw.write(g.x+" "+g.z+" "+g.r+" ");
				String dat = "";
				int b=0;int k=0;
				for(int i=0;i<g.r21;i++){
					for(int j=0;j<g.r21;j++){
						if(sq(i-g.r)+sq(j-g.r)<sq(g.r+1)){
							if(k%8==0 && k>0){
								dat += (char) b;
								b = 0;
							}
							if(g.dat[i][j]){
								b|=1<<7-k%8;
							}
							k++;
						}
					}
				}
				
				dat += (char) b;
				
				// nun die Laufl�ngenkodierung, wenn Schl�ssel auf 2 steht :)
				// max 128 lang, also immer true/false und dann die L�nge...
				
				if(lauf){// entweder hier oder oben ist ein Fehler
					boolean[] read = booleansFromBytes(dat.getBytes(), 0);
					String ret="";
					k=0;
					boolean is=read[0];
					int l=read.length;
					for(int i=0;i<l;i++){
						while(k<127 && i+k<l && is==read[i+k]){k++;}
						ret+=(char)((is?128:0)+k);
						i+=k;
						k=0;
						if(i<l){
							is=read[i];
						}
					}
					dat = ret;
				}
				
				fw.write(dat);
				fw.flush();
				fw.close();
				
				g.changed=false;
			}
		}
		if(z>0){
			System.out.println("Saved "+z+" plots.");
		}
		saved = true;
	}

	public static int getCosts(Player p, String name, int x, int z, int amount, boolean wantbuy) {// gibt die Gr��e des Grundst�cks * denPositionsfaktor zur�ck
		boolean king = p.getName().equalsIgnoreCase(Plugin.kingsname);
		for(Grundstück g:place){
			if(g.owner.equalsIgnoreCase(king?"@king":p.getName()) && g.name.equalsIgnoreCase(name)){
				if(wantbuy){
					p.sendMessage(Plugin.prefix+"�4You already have a plot with the name "+name+" at "+g.x+" and "+g.z+"! You can�t buy two with the same name.");
				} else {
					p.sendMessage(Plugin.prefix+"�4You already have a plot with the name "+name+" at "+g.x+" and "+g.z+"!");
				}
				
				return 0;
			}
		}
		
		
		double posfac = 0.7;
		if(x*x+z*z<1E6){// Innenstadt
			posfac = 2.7;
		} else if(x*x+z*z<25E6){// 1-5km vom Stadtzentrum entfernt
			posfac = 2.7-2*(x*x+z*z-1E6)/24E6;
		}
		
		int mx=101;
		
		int[][] is = new int[2*mx+1][2*mx+1];
		int i;boolean last;
		int a2;double sqi1;
		if(x*x+z*z<25E6){
			for(i=0;i<mx;i++){//100 ist der maximale Radius: wer mehr will, muss mehrere Grundst�cke kaufen -> bei Pi100� braucht man 32000 Gold -> sozusagen unbezahlbar
				last = true;
				sqi1 = sq(i+1);
				for(int a=-i;a<=i;a++){
					a2=a*a;
					for(int b=-i;b<=i;b++){
						if(a2+b*b<sqi1){
							if(is[mx+a][mx+b]==i && isPartOfGrndstck(a+x, b+z)){
								last=false;
								if(is[mx+a+1][mx+b]==0 && isOwned(x+a+1, z+b)==null){is[mx+a+1][mx+b]=i+1;}
								if(is[mx+a-1][mx+b]==0 && isOwned(x+a-1, z+b)==null){is[mx+a-1][mx+b]=i+1;}
								if(is[mx+a][mx+b+1]==0 && isOwned(x+a, z+b+1)==null){is[mx+a][mx+b+1]=i+1;}
								if(is[mx+a][mx+b-1]==0 && isOwned(x+a, z+b-1)==null){is[mx+a][mx+b-1]=i+1;}
							}
						}
					}
				}
				if(last) break;
			}
		} else {
			for(i=0;i<mx;i++){//100 ist der maximale Radius: wer mehr will, muss mehrere Grundst�cke kaufen -> bei Pi100� braucht man 32000 Gold -> sozusagen unbezahlbar
				last = true;
				sqi1 = sq(i+1);
				for(int a=-i;a<=i;a++){
					a2 = a*a;
					for(int b=-i;b<=i;b++){
						if(a2+b*b<sqi1){
							if(is[mx+a][mx+b]==i && isNearGrundstück(a+x, b+z)){
								last=false;
								if(is[mx+a+1][mx+b]==0 && isOwned(x+a+1, z+b)==null){is[mx+a+1][mx+b]=i+1;}
								if(is[mx+a-1][mx+b]==0 && isOwned(x+a-1, z+b)==null){is[mx+a-1][mx+b]=i+1;}
								if(is[mx+a][mx+b+1]==0 && isOwned(x+a, z+b+1)==null){is[mx+a][mx+b+1]=i+1;}
								if(is[mx+a][mx+b-1]==0 && isOwned(x+a, z+b-1)==null){is[mx+a][mx+b-1]=i+1;}
							}
						}
					}
				}
				if(last) break;
			}
		}
		
		if(i!=mx)i--;
		if(i<1)i=1;
		int i21 = 2*i+1;
		boolean[][] dat = new boolean[i21][i21];
		int c = 0;
		for(int a=0;a<i21;a++){
			for(int b=0;b<i21;b++){
				if(is[mx+a-i][mx+b-i]>0){
					dat[a][b]=true;
					c++;
				}
			}
		}
		
		if(wantbuy && Bank.substract(p.getName().toLowerCase(), (long) (posfac*c+0.999))){
			new Grundstück(king?"@king":p.getName().toLowerCase(), name, x, z, i, dat).changed=true;
			p.sendMessage(Plugin.prefix+"Congratulations! You buyed \""+name+"\" for "+(int)(posfac*c+0.999)+" goldnuggets.\n"
					+ (king?" Everyone who has the right permissions, can build here :). Use it also as a chance to allow marriages :D":" This is now yours and only yours. Its area is up to the sky and down to -64.\n As an extra you get the permission to design with your neightbors the streets around you (100m)."));
			try {
				save(Plugin.instance.getDataFolder());
			} catch (IOException e) {e.printStackTrace();}
			return -1;
		}
		
		return (int) (posfac*c+0.999);
	}

	public static boolean isPlotnameByOwner(String plotname, String owner) {
		owner = owner.toLowerCase();
		for(Grundstück g:place){
			if(g.owner.equals(owner) && g.name.equals(plotname)){
				return true;
			}
		}
		return false;
	}

	public static boolean isPlotnameByOwnerAndRealplot(String plotname, String owner, int x, int z) {
		owner = owner.toLowerCase();
		for(Grundstück g:place){
			if(g.owner.equals(owner) && g.name.equals(plotname)){
				return g.contains(x-g.x, z-g.z);
			}
		}
		return false;
	}
	
	public static boolean changeOwner(String owner, String newowner, String plotname) throws IOException {
		if(!saved){
			save(Plugin.instance.getDataFolder());
		}
		owner = owner.toLowerCase();
		newowner = newowner.toLowerCase();
		
		for(Grundstück g:place){
			if(g.name.equalsIgnoreCase(plotname) && g.owner.equalsIgnoreCase(owner)){
				
				if(g.x==0 && g.z==0) return false;// der Goldene Turm wird niemals verkauft!
				
				boolean ret = new File(Plugin.instance.getDataFolder(), "data/plots/"+g.name+"."+g.owner+".dat").renameTo(new File(Plugin.instance.getDataFolder(), "data/plots/"+g.name+"."+newowner+".dat"));
				if(ret)
					g.owner = newowner;
				
				return ret;
			}
		}
		return false;
	}

	public static String getInfoAbout(String plotname, int costs) {
		for(Grundstück g:place){
			if(g.name.equalsIgnoreCase(plotname)){
				int c = 0;
				for(int i=0;i<g.r21;i++){
					for(int j=0;j<g.r21;j++){
						if(g.dat[i][j])c++;
					}
				}
				if(c==0){
					return "It seems like this plot doesn�t exists... :(";
				}
				return ""
						+ "   "+(int)Math.sqrt(sq(g.x)+sq(g.z))+"m distance from spawn\n"
						+ "   "+c+"m� area\n"
						+ "   "+costs+"g\n"
						+ "   "+(100*costs/c)+"g/100m�";
			}
		}
		return "This plot doesn�t exist. If you think it is an error, please report it! /ticket <your message>";
	}
	
	public static String getMyPlots(String n){
		n = n.toLowerCase();
		int k = 1;
		String s = "";
		for(Grundstück g:place){
			if(g.owner.equals(n)){
				s+="\n   �a"+g.name+"�f "+g.x+" "+g.z+" ("+(k++)+")";
			}
		}
		return s;
	}
	
	@SuppressWarnings("deprecation")
	public void showYourself(Player p, int deltaY) {
		int y=p.getLocation().getBlockY(), bx=p.getLocation().getBlockX(), bz=p.getLocation().getBlockZ(), ox=ori(bx), oz=ori(bz);
		
		for(int i=-101;i<102;i++){
			for(int j=-101;j<102;j++){
				if(contains(ox+i-x, oz+j-z)){
					p.sendBlockChange(new Location(p.getWorld(), bx+i, y+deltaY, bz+j), 20, (byte)0);
				}
			}
		}
	}
	
	private void makeBeautiful() {
		for(int i=0;i<r21;i++){
			for(int j=0;j<r21;j++){
				// if am Rand, aber ein Grundst�cksblock
				if(dat[i][j] && (i==0 || j==0 || i==r21-1 || j==r21-1 || !dat[i+1][j] || !dat[i-1][j] || !dat[i][j+1] || !dat[i][j-1])){
					int y = (int) Jena.h(x-r, z-r) - 5600;
					Generator.sB(Plugin.world, x, y++, z, 98, 0);
					Generator.sB(Plugin.world, x, y++, z, 98, 0);
					for(int k=0;k<20;k++){
						Generator.sB(Plugin.world, x, y++, z, 0, 0);
					}
				}
			}
		}
	}
	
	public boolean is(String name) {
		return this.name.equalsIgnoreCase(name);
	}
}
