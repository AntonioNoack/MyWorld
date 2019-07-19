package me.corperateraider.myworld;

import me.corperateraider.generator.MathHelper;

import org.bukkit.Location;

public class Position extends MathHelper {
	public int x, y, z;
	public Position(int x, int y, int z){
		this.x=x;this.y=y;this.z=z;
	}
	
	public Position(Location l){
		x=l.getBlockX();
		y=l.getBlockY();
		z=l.getBlockY();
	}
	
	public double distSQ(int px, int py, int pz){
		return sq(px-x)+sq(py-y)+sq(pz-z);
	}
	
	public double distSQ(Position p){
		return sq(p.x-x)+sq(p.y-y)+sq(p.z-z);
	}
	
	public double distSQy(Position p, int fy){
		return sq(p.x-x)+fy*sq(p.y-y)+sq(p.z-z);
	}
}
