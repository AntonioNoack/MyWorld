package me.corperateraider.recipes;

import org.bukkit.potion.PotionEffect;

public class XEffect {
	double chance = 1;
	PotionEffect effect;
	public XEffect(PotionEffect potionEffect){
		effect = potionEffect;
	}
	public XEffect(PotionEffect e, double c){
		effect = e;
		chance = c;
	}
}
