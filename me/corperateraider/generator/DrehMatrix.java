package me.corperateraider.generator;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DrehMatrix extends MathHelper {
	
	double
		xx=1, xy=0, xz=0,
		yx=0, yy=1, yz=0,
		zx=0, zy=0, zz=1;
	
	public DrehMatrix(){}
	
	public DrehMatrix(double x, double y, double z, double alpha){
		// Dank de.wikipedia.org/wiki/Drehmatrix
		double sin=Math.sin(alpha), cos=Math.cos(alpha);
		xx = x*x*(1-cos)+cos;xy = x*y*(1-cos)-z*sin;xz = x*z*(1-cos)+y*sin;
		yy = y*y*(1-cos)+cos;yx = y*x*(1-cos)+z*sin;yz = y*z*(1-cos)-x*sin;
		zz = z*z*(1-cos)+cos;zx = z*y*(1-cos)-y*sin;zy = z*y*(1-cos)+x*sin;
	}
	
	public DrehMatrix rotX(double alpha){
		//function rotX(v, a){return [v[0], cos(a)*v[1]-sin(a)*v[2], sin(a)*v[1]+cos(a)*v[2]];}
		DrehMatrix ret = new DrehMatrix();
		double sin=Math.sin(alpha), cos=Math.cos(alpha);
		// X
		ret.xx=xx;
		ret.xy=xy;
		ret.xz=xz;
		// Y
		ret.yx=cos*yx-sin*zx;
		ret.yy=cos*yy-sin*zy;
		ret.yz=cos*yz-sin*zz;
		// Z
		ret.zx=sin*yx+cos*zx;
		ret.zy=sin*yy+cos*zy;
		ret.zz=sin*yz+cos*zz;
		return ret;
	}
	
	public DrehMatrix rotY(double alpha){
		//function rotX(v, a){return [v[0], cos(a)*v[1]-sin(a)*v[2], sin(a)*v[1]+cos(a)*v[2]];}
		DrehMatrix ret = new DrehMatrix();
		double sin=Math.sin(alpha), cos=Math.cos(alpha);
		// Y
		ret.yx=yx;
		ret.yy=yy;
		ret.yz=yz;
		// X
		ret.xx=cos*xx-sin*zx;
		ret.xy=cos*xy-sin*zy;
		ret.xz=cos*xz-sin*zz;
		// Z
		ret.zx=sin*xx+cos*zx;
		ret.zy=sin*xy+cos*zy;
		ret.zz=sin*xz+cos*zz;
		return ret;
	}
	
	public DrehMatrix rotZ(double alpha){//function rotX(v, a){return [v[0], cos(a)*v[1]-sin(a)*v[2], sin(a)*v[1]+cos(a)*v[2]];}
		DrehMatrix ret = new DrehMatrix();
		double sin=Math.sin(alpha), cos=Math.cos(alpha);
		// Z
		ret.zx=zx;
		ret.zy=zy;
		ret.zz=zz;
		// X
		ret.xx=cos*xx-sin*yx;
		ret.xy=cos*xy-sin*yy;
		ret.xz=cos*xz-sin*yz;
		// Y
		ret.yx=sin*xx+cos*yx;
		ret.yy=sin*xy+cos*yy;
		ret.yz=sin*xz+cos*yz;
		return ret;
	}
	
	public Location add(Location loc, double x, double y, double z){
		return new Location(loc.getWorld(), Math.round(loc.getX()+x(x,y,z)), Math.round(loc.getY()+y(x,y,z)), Math.round(loc.getZ()+z(x,y,z)));
	}
	
	public double x(double x, double y, double z){
		return xx*x+xy*y+xz*z;
	}
	
	public double y(double x, double y, double z){
		return yx*x+yy*y+yz*z;
	}
	
	public double z(double x, double y, double z){
		return zx*x+zy*y+zz*z;
	}
	
	public void show(){
		System.out.println(r(xx,1000)+" "+r(xy,1000)+" "+r(xz,1000));
		System.out.println(r(yx,1000)+" "+r(yy,1000)+" "+r(yz,1000));
		System.out.println(r(zx,1000)+" "+r(zy,1000)+" "+r(zz,1000));
	}
	
	public void show(Player p){
		p.sendMessage(r(xx,1000)+" "+r(xy,1000)+" "+r(xz,1000));
		p.sendMessage(r(yx,1000)+" "+r(yy,1000)+" "+r(yz,1000));
		p.sendMessage(r(zx,1000)+" "+r(zy,1000)+" "+r(zz,1000));
	}
	
	public void show(double x, double y, double z){
		System.out.println(r(x(x,y,z),1000)+" "+r(y(x,y,z),1000)+" "+r(z(x,y,z),1000));
	}
	
	public String r(double d, double x){
		d=Math.round(d*x)/x;
		return d<0?""+d:"+"+d;
	}
}
