package me.corperateraider.recipes;

import me.corperateraider.generator.MathHelper;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class XMaterial {
	
	public Material m;
	public boolean useData;
	public int amount = 1;
	public int data = 0;
	
	public XMaterial t(){
		useData = true;
		return this;
	}
	
	public XMaterial f(){
		useData = false;
		return this;
	}
	
	public XMaterial(int amount, Material m){
		this.m = m;
		this.amount = amount;
	}
	
	public XMaterial(int amount, Material m, int data){
		this.m = m;
		this.amount = amount;
		useData = true;
		this.data=data;
	}
	
	public XMaterial(Material m){
		this.m = m;
	}
	
	public XMaterial(Material m, int data){
		this.m = m;
		useData = true;
		this.data=data;
	}
	
	@SuppressWarnings("deprecation")
	public XMaterial(ItemStack s) {
		this.m = s.getType();
		this.data = s.getData().getData();
		if(data>-1){
			useData = true;
		}
		this.amount = MathHelper.max(1, s.getAmount());
	}

	/**
	 * Construktor für ItemStacks, die eventuell nicht die richtigen Daten aber den richtigen Namen besitzen...
	 * */
	@SuppressWarnings("deprecation")
	public XMaterial(ItemStack s, String name) {
		m = s.getType();
		if(name==null || !name.startsWith("§")){
			this.data = s.getData().getData();
			if(data>-1){
				useData = true;
			}
		} else {
			char c = name.charAt(1);
			if(c>='0' && c<'9'){
				useData = true;
				data = c-48;
			} else if(c>='a' && c<='f'){
				useData = true;
				data = c-'a'+10;
			}
		}
		this.amount = MathHelper.max(1, s.getAmount());
	}

	@Override
	public String toString(){
		return (amount>1?amount+"x ":"")+m.name()+(useData?":"+data:"");
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public int hashCode(){
		return m.getId()*16+data+(useData?0:8192);
	}
	
	@Override
	public boolean equals(Object obj){
		return obj != null && obj.hashCode()==hashCode();
	}
	
	public XMaterial clone(){
		XMaterial ret = new XMaterial(amount, m);
		ret.useData=useData;
		ret.data=data;
		return ret;
	}
}
