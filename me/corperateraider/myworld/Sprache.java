package me.corperateraider.myworld;

import java.util.HashMap;

public class Sprache {
	
	public static String select(String name, String en, String de, String fr, String es){
		return getPls(name).get(en, de, fr, es);
	}
	
	public static HashMap<String, Info> txt = new HashMap<>();
	public static HashMap<String, Used> pls = new HashMap<>();
	public static Used getPls(String name){
		if(pls.containsKey(name)){
			return pls.get(name);
		} else return Used.English;
	}
	public static enum Used {
		   English("en"){
			@Override public String get(String key){
				return txt.get(key).en;
			}
			@Override public String get(String en, String de, String fr, String es) {
				return en;
			}
		}, Deutsch("de"){
			@Override public String get(String key){
				return txt.get(key).de;
			}
			@Override public String get(String en, String de, String fr, String es) {
				return de==null?en:de;
			}
		}, Francais("fr"){
			@Override public String get(String key){
				Info i = txt.get(key);
				return i.fr==null?i.en:i.fr;
			}
			@Override public String get(String en, String de, String fr, String es) {
				return fr==null?en:fr;
			}
		}, Espagnol("es"){
			@Override public String get(String key){
				Info i = txt.get(key);
				return i.fr==null?i.en:i.es;
			}
			@Override public String get(String en, String de, String fr, String es) {
				return es==null?en:es;
			}
		};
		public abstract String get(String key);
		public abstract String get(String en, String de, String fr, String es);
		public static Used byShortcut(String sh){
			for(Used u:Used.values()){
				if(u.shortcut.equalsIgnoreCase(sh) || u.name().equalsIgnoreCase(sh))
					return u;
			}
			return English;
		}
		public String shortcut;
		Used(String sh){
			shortcut=sh;
		}
	}
	static class Info {
		String en, de, fr, es;
		public Info(String e, String d, String f, String sp){
			en=e;de=d;fr=f;es=sp;
		}
	}
	//private static void i(String k, String a, String b, String c, String d){txt.put(k, new Info(a,b,c,d));}
	public static void ini(){}
}
