package me.corperateraider.recipes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class SRecipe extends XRecipe {
	public SRecipe(ItemStack result, XMaterial... m){
		this.result = result;
		effects = new XEffect[]{};
		mats = m;
	}

	public SRecipe(ItemStack result, XEffect[] xEffects, XMaterial... m) {
		this.result = result;
		effects = xEffects;
		mats = m;
	}
	
	public SRecipe(ItemStack result, List<ItemStack> ingredientList) {
		
		this.notNew = true;
		
		this.result = result;

		ArrayList<XMaterial> used = new ArrayList<>();
		
		for(ItemStack s:ingredientList){
			//mats[index++] = new XMaterial(s);
			XMaterial m = new XMaterial(s);
			if(used.contains(m)){
				for(XMaterial mat:used){
					if(m.equals(mat)){
						mat.amount+=m.amount;
						break;
					}
				}
			} else {
				used.add(m);
			}
		}
		
		mats = new XMaterial[used.size()];
		int index = 0;
		for(XMaterial m:used){
			mats[index++]=m;
		}
		
	}

	@Override
	@SuppressWarnings("deprecation")
	public String show() {
		String ret = "§2"+result.getAmount()+"x "+getResultName()+" "+result.getTypeId()+":"+result.getData().getData()+"§f", s;
		for(XMaterial m:mats){
			s=m.toString();
			if(s.charAt(1)=='x'){
				ret+="\n   "+s;
			} else {
				ret+="\n   1x "+s;
			}
		}
		return ret;
	}
}
