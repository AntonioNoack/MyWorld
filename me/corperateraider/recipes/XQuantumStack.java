package me.corperateraider.recipes;

import java.util.ArrayList;

import me.corperateraider.generator.MathHelper;

import org.bukkit.inventory.ItemStack;

public class XQuantumStack {
	private ItemStack i;
	private double chance;
	public XQuantumStack(ItemStack i, double chance){
		this.i=i;
		this.chance=chance;
		
		
	}
	
	public void addMaybe(ArrayList<ItemStack> list){
		if(MathHelper.random()<chance){
			list.add(i);
		}
	}
}
