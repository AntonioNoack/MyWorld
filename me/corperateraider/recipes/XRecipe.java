package me.corperateraider.recipes;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.v1_7_R1.IRecipe;
import net.minecraft.server.v1_7_R1.ShapedRecipes;
import net.minecraft.server.v1_7_R1.ShapelessRecipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class XRecipe extends MyRecipe {

	public XRecipe(ItemStack result, String rec, XEffect[] effects, XMaterial... toUse){
		boolean small = rec.length()==4;
		if(small){
			this.rec = new String[]{rec.substring(0, 2), rec.substring(2, 4)};
		} else {
			this.rec = new String[]{rec.substring(0, 3), rec.substring(3, 6), rec.substring(6, 9)};
		}
		this.toUse = toUse;
		this.result = result;
		this.effects=effects;
		if(small){
			mats = new XMaterial[4];
			for(int i=0;i<4;i++){
				if(rec.charAt(i)!=' '){
					mats[i]=toUse[rec.charAt(i)-48];
				}
			}
		} else {
			mats = new XMaterial[9];
			for(int i=0;i<9;i++){
				if(rec.charAt(i)!=' '){
					mats[i]=toUse[rec.charAt(i)-48];
				}
			}
		}
	}
	
	public XRecipe(ItemStack result, String rec, XMaterial... toUse){
		boolean small = rec.length()==4;
		if(small){
			this.rec = new String[]{rec.substring(0, 2), rec.substring(2, 4)};
		} else {
			this.rec = new String[]{rec.substring(0, 3), rec.substring(3, 6), rec.substring(6, 9)};
		}
		this.toUse = toUse;
		this.result = result;
		if(small){
			mats = new XMaterial[4];
			for(int i=0;i<4;i++){
				if(rec.charAt(i)!=' '){
					mats[i]=toUse[rec.charAt(i)-48];
				}
			}
		} else {
			mats = new XMaterial[9];
			for(int i=0;i<9;i++){
				if(rec.charAt(i)!=' '){
					mats[i]=toUse[rec.charAt(i)-48];
				}
			}
		}
	}
	
	public XRecipe(ItemStack result, String[] s, XMaterial... toUse) {
		
		this.toUse = toUse;
		this.result = result;
		
		// oje... baue das Gitter...
		matz = new XMaterial[s.length][];
		for(int i=0;i<s.length;i++){
			matz[i] = new XMaterial[s[i].length()];
			for(int j=0;j<s[i].length();j++){
				matz[i][j] = toUse[s[i].charAt(j)-48];
			}
		}
		
		this.rec = s;
	}
	
	/**
	 * Convertiert die Daten eines Vanilla-Rezeptes zu einem XRezept
	 * */
	public XRecipe(ItemStack result, String[] rec, Map<Character, ItemStack> map){
		
		notNew = true;
		
		// toUse...
		// das Rezept nutzt auch unterschiedliche Zeichen für das Gleiche...
		
		HashMap<XMaterial, String> materials = new HashMap<>();
		for(int i=0;i<map.size();i++){
			char c;
			ItemStack s = map.get(c = (char)(i+97));
			if(s!=null){
				XMaterial mat = new XMaterial(s);
				if(materials.containsKey(mat)){
					materials.put(mat, materials.get(mat)+c);
				} else {
					materials.put(mat, ""+c);
				}
			}
		}
		
		toUse = new XMaterial[materials.size()];
		int index = 0;
		for(XMaterial m:materials.keySet()){
			toUse[index++]=m;
		}
		
		HashMap<Character, XMaterial> chars = new HashMap<>();
		for(int i=0;i<toUse.length;i++){
			XMaterial x;
			for(char c:materials.get(x=toUse[i]).toCharArray()){
				chars.put(c, x);
			}
		}
		
		char[] work;
		char c;
		matz = new XMaterial[rec.length][];
		for(int i=0;i<rec.length;i++){
			matz[i] = new XMaterial[rec[i].length()];
			work = rec[i].toCharArray();
			for(int j=0;j<work.length;j++){
				matz[i][j] = chars.get(c = work[j]);
				work[j] = (char) (c-49);// 97-48
				// füge nun meinem Array das hinzu...
				
			}
			rec[i]=new String(work);
		}
		
		this.rec = rec;
		this.result = result;
	}
	
	protected XRecipe(){}

	public static XRecipe fromIRecipe(IRecipe recipe){
		XRecipe ret=null;
		if(recipe instanceof ShapedRecipes){
			
			ShapedRecipe bukk = ((ShapedRecipes) recipe).toBukkitRecipe();
			return new XRecipe(bukk.getResult(), bukk.getShape(), bukk.getIngredientMap());
			
		} else if(recipe instanceof ShapelessRecipes){
			
			ShapelessRecipe bukk = ((ShapelessRecipes) recipe).toBukkitRecipe();
			return new SRecipe(bukk.getResult(), bukk.getIngredientList());
			
		} else {
			System.out.println(recipe.getClass().getName());
			ret = null;
		}
		return ret;
	}

	@Override
	@SuppressWarnings("deprecation")
	public String show() {
		String ret = "§2"+result.getAmount()+"x "+getResultName()+" "+result.getTypeId()+":"+result.getData().getData()+"§f\n";
		HashMap<XMaterial, Character> show = new HashMap<>();
		char c;int alt=0;boolean first = true;
		for(XMaterial m:toUse){
			if(show.containsValue(c=m.m.name().charAt(0)) || c=='I'){
				if(show.containsValue(c=(c+"").toLowerCase().charAt(0)) || c=='i'){
					show.put(m, (char)(alt+48));
					alt++;
				} else {
					show.put(m, c);
				}
			} else {
				show.put(m, c);
			}
			if(first){
				first = false;
				ret+="   "+show.get(m)+" = "+m.toString();
			} else {
				ret+=", "+show.get(m)+" = "+m.toString();
			}
		}
		
		if(mats==null){
			int w=matz.length, h=matz[0].length;
			ret+="\n   ."+("___".substring(0, h))+".\n   |";
			for(int i=0;i<w;i++){
				if(i!=0)ret+="|\n   |";
				for(int j=0;j<h;j++){
					if(matz[i][j]!=null){
						ret+=show.get(matz[i][j]);
					} else {
						ret+="_";
					}
				}
			}
			ret+="|";
		} else {
			ret+="\n   .___.\n   |";
			int l = mats.length==4?2:3;
			
			for(int i=0;i<l;i++){
				if(i!=0)ret+="|\n   |";
				for(int j=0;j<l;j++){
					if(mats[i*l+j]!=null){
						ret+=show.get(mats[i*l+j]);
					} else {
						ret+="_";
					}
				}
			}
			ret+="|";
		}
		
		return ret;
	}
}
