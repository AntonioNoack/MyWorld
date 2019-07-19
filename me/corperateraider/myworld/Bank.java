package me.corperateraider.myworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import me.corperateraider.generator.MathHelper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Bank {
	
	/**
	 * Prefixe
	 * */
	protected static final String prefix = "[§4Bank§f] ", syspre = "[B";
	
	/**
	 * Alle Konten
	 * */
	protected static HashMap<String, Konto> konten = new HashMap<>();
	
	/**
	 * Initialisierung:
	 * <br>liest alle Konten ein
	 * */
	public static void ini() throws IOException{
		File f = new File(Plugin.instance.getDataFolder(), "data/bank.sec");
		if(f.exists()){
			BufferedReader read = new BufferedReader(new FileReader(f));
			for(String s=read.readLine();s!=null;s=read.readLine()){
				if(s.contains(" ")){
					konten.put(s.split(" ")[0], new Konto(Long.parseLong(s.split(" ")[1])));
				}
			}
			read.close();
		}
	}
	
	/**
	 * Wann wurden zum letzen Mal Zinsen verteilt?
	 * */
	protected static long time = 0;
	
	/**
	 * Update die Zinsen.
	 * */
	public static void zins(){
		if(time==0){
			time = System.currentTimeMillis();
			return;
		}
		
		long delttime = -time+(time=System.currentTimeMillis());
		if(delttime<86400000){// kleiner als 1 Tag
			// 1.0019008376772348457892303014969 pro Tag (1Tag=86400s) = 100% pro Jahr
			// 1.0000219797944618315659002318303 pro 1000s
			// 1Mio s = 11,574074074074074074074074074074 Tage = 0,03170979198376458650431253170979 Jahre
			// 1,0222228827731472571363687420918 = 100% für 1 Jahr; eben für 1Mio s
			double zins = Math.pow(1.022223, delttime*1E-9)-1;
			for(String key:konten.keySet()){
				if(!key.equalsIgnoreCase("@king")){
					Konto k = konten.get(key);
					
					double delta = zins*k.amount;
					if(delta>0){
						if(MathHelper.random() < delta - (int) delta){
							delta++;
						}
						
						if((int)delta>0){
							System.out.println(syspre+"% +"+key+" "+(int)delta);
							k.amount+=delta;
						}
					}
				}
			}
		} else {
			System.out.println("Bank.delttime-error: "+delttime);
		}
	}
	
	/**
	 * Verpackt die reichsten drei Spieler in einem String. Dass der König Privatvermögen besitzt, wird nicht beachtet!
	 * */
	public static String richest3People(){
		String ret = "";
		String k1 = "x", k2 = "x", k3 = "x";
		long amount = 0, x;
		for(String k:konten.keySet()){
			if(amount<(x=konten.get(k).amount)){
				k1 = k;
				amount = x;
			}
		}
		ret = "   §c1. "+amount+": "+k1+"\n";
		amount = 0;
		for(String k:konten.keySet()){
			if(!k1.equalsIgnoreCase(k) && amount<(x=konten.get(k).amount)){
				k2 = k;
				amount = x;
			}
		}
		ret += "   §e2. "+amount+": "+k2+"\n";
		amount = 0;
		for(String k:konten.keySet()){
			if(!k1.equalsIgnoreCase(k) && !k2.equalsIgnoreCase(k) && amount<(x=konten.get(k).amount)){
				k3 = k;
				amount = x;
			}
		}
		return ret + "   §a3. "+amount+": "+k3+"\n";
	}
	
	/**
	 * Übertragt amount Goldnuggets vom Konto von zum Konto zu.
	 * */
	private static void transfer(String von, String zu, long amount){
		getBilance(von);
		getBilance(zu);
		konten.get(von).amount-=amount;
		konten.get(zu).amount+=amount;
		System.out.println(syspre+"+"+zu+"<"+von+" "+amount);
	}
	
	/**
	 * Gibt die Bilanz des Kontos von kontoname zurück
	 * */
	public static long getBilance(String kontoname) {
		if(!konten.containsKey(kontoname)){
			konten.put(kontoname, new Konto(15));
		}
		return konten.get(kontoname).amount;
	}

	/**
	 * Wenn der Nutzer mehr Geld hat, wird amount abgehoben. return true;
	 * <br>Wenn nicht, dann return false;
	 * */
	public static boolean substract(String name, long amount){
		if(konten.get(name.toLowerCase()).amount>=amount){
			konten.get(name.toLowerCase()).amount-=amount;
			System.out.println(syspre+"-"+name+" "+amount);
			return true;
		}
		return false;
	}
	
	/**
	 * Dem Nutzer n wird der Betrag amount auf sein Konto gegeben.
	 * */
	public static void add(String n, long amount){
		
		if(amount==0)return;
		
		getBilance(n);
		konten.get(n.toLowerCase()).amount+=amount;
		System.out.println(syspre+"+"+n+" "+amount);
	}
	
	/**
	 * Speichert alle Daten ab und verteilt letzte ausstehende Zinsen.
	 * */
	public static void save(File file) throws IOException{
		zins();
		File folder;
		File rename = new File(folder=new File(file, "/data"), "/bank.sec");
		try {
			rename.renameTo(new File(folder, "/bank."+(System.currentTimeMillis()/7200000)+".sec"));// sollte sicher speichern :) und zwar alle 2h
		} catch(Exception e){
			e.printStackTrace();
		}
		
		file = new File(file, "/data/bank.sec");
		if(!file.exists())file.createNewFile();
		FileWriter fw = new FileWriter(file);
		for(String name:konten.keySet()){
			fw.write(name.toLowerCase()+" "+konten.get(name).amount+"\n");
		}
		fw.flush();
		fw.close();
	}

	/**
	 * Gibt die Bilanz des Spielers p zurück.
	 * */
	public static long getBilance(Player p) {
		if(!konten.containsKey(p.getName().toLowerCase())){
			konten.put(p.getName().toLowerCase(), new Konto(15));
		}
		return konten.get(p.getName().toLowerCase()).amount;
	}

	/**
	 * Dem Spieler p wird seine Bilanz mitgeteilt.
	 * <br>Sonstige Vermögenswerte spielen keine Rolle.
	 * */
	public static void sendStatus(Player p) {
		if(Plugin.kingsname.equalsIgnoreCase(p.getName())){
			long king=getBilance("@king"), own=getBilance(p);
			p.sendMessage(prefix+Sprache.select(p.getName(),
					(Plugin.isQueen?"Queens":"Kings")+" balance is "+king+" goldnuggets. Your private one is "+own,
					(Plugin.isQueen?"Eurer Königin":"Des Königs")+" Kontostand beläuft sich auf "+king+". Dein privater auf "+own, null, null));
		} else {
			long l;
			p.sendMessage(prefix+Sprache.select(p.getName(),
					"Your balance is "+(l=getBilance(p))+" goldnuggets.",
					"Dein Kontostand beläuft sich auf: "+l+" Goldnuggets.", null, null));
		}
	}

	/**
	 * Die Verwaltung einer Bilanz
	 * */
	private static class Konto {
		long amount;
		public Konto(long l){
			amount = l;
		}
	}

	/**
	 * Der Spieler owner gibt dem Kontoinhaber sec amount Goldnuggets.
	 * */
	public static boolean pay(Player owner, String sec, long amount) {
		if(amount>0 && getBilance(owner)>=amount){
			if(!konten.containsKey(sec.toLowerCase())){
				owner.sendMessage(Sprache.select(owner.getName(),
						"WARN: "+sec+" didn't exists until now.",
						"Warnung: "+sec+" existierte bisher noch nicht!", null, null));
			}
			transfer(owner.getName().toLowerCase(), sec.toLowerCase(), amount);
			return true;
		}
		return false;
	}

	/**
	 * Wenn der Spieler name dem König mehr Geld gegeben hat, als er selber besitzt, wird:
	 * <br>1. Die Bilanz des Königs auf diesen Wert gesetzt.
	 * <br>2. Alle Spieler werden benachrichtigt, dass name der neue König ist.
	 * <br>3. Der neue König wird mit seinen Aufgaben vertraut gemacht.
	 * <br>4. Der König aus der Klasse Plugin wird offiziell geändert.
	 * */
	@SuppressWarnings("deprecation")
	public static void newKing(String name, long amount) throws InterruptedException {
		if(getBilance("@king")<=amount){
			Konto king = konten.get("@king");
			System.out.println(syspre+"K"+amount+"/"+king.amount+" "+Plugin.kingsname+"->"+name);
			king.amount=amount;
			Player old = Bukkit.getPlayer(Plugin.kingsname);
			if(old!=null){
				old.sendMessage(Plugin.prefix+Sprache.select(old.getName(),
						" §4You were overthrown by §f"+name+"§4 who paid "+amount+".",
						" §4Du wurdest von §f"+name+", der "+amount+" bezahlte, gestürzt!", null, null));
			}
			Plugin.kingsname=name;
			Plugin.isQueen=false;
			Bukkit.broadcastMessage("§c[§4KING§c]§f The King changed! The new king is §4"+name.toUpperCase());
			Plugin.sendKingsmessages(name);
		} else {
			Bukkit.getPlayer(name).setBanned(true);
			for(Player p:Bukkit.getOnlinePlayers()){
				p.kickPlayer(Sprache.select(p.getName(),
						"Invalid exception: sb wanted to become illegally King...\n"+name+" was banned for that!",
						name+" wollte illegal König werden und wurde dafür gebannt!", null, null));
			}
		}
	}
	
	/**
	 * Sende dem Spieler p die entsprechende Fehlermeldung...
	 * <br>0: Etwas ist schief gegangen - z.B. Schreiben oder Lesen auf der Festplatte
	 * <br>1: Nicht genug Geld.
	 * <br>2: Man kann keine negativen Geldwerte verschenken.
	 * <br>sonst unbekannter Fehler 0x[code in hex]
	 * */
	public static void sendErrMessage(Player p, int code){
		switch(code){
		case 0:
			p.sendMessage(prefix+Sprache.select(p.getName(),
					"Sth went wrong :(",
					"Etwas ist schief gelaufen :(", null, null));return;
		case 1:
			p.sendMessage(prefix+Sprache.select(p.getName(),
					"You don't have enought money!",
					"Du hast nicht genug Geld!",
					"Tu prends plus d'argent!", null));return;
		case 2:
			p.sendMessage(prefix+Sprache.select(p.getName(),
					"You can't send negative amounts of money!",
					"Du kannst keine negativen Geldbeträge senden!", null, null));return;
		default:
			p.sendMessage(prefix+Sprache.select(p.getName(),
					"Unknown error: 0x"+Integer.toBinaryString(code),
					"Unbekannter Fehler: 0x"+Integer.toBinaryString(code), null, null));
		}
	}
	/**
	 * Enthält in der Reihenfolge aller Materialien deren Wert...
	 * x = 0
	 * 0-9
	 * 0 = 1gn/100
	 * 1 = 1gn/20
	 * 2 = 1gn/4
	 * 3 = 1gn
	 * 4 = 1/2Barren
	 * 5 = 1 Barren
	 * 6 = 1/2 Block
	 * 7 = 1 Block
	 * 8 = 4,5 Block
	 * 9 = 9 Blöcke
	 * */
	public static final String GDP = "x000000xxxxx10554105356101x";
	public static long GDP(Player p){
		long ges = getBilance(p);
		if(Plugin.kingsname.equalsIgnoreCase(p.getName())){
			ges+=getBilance("@king");
		}
		// erstmal doch nur das was gesammelt wurde... alles andere wird auch schwer zu berechnen besonders bei Schenkungen und so...
		ges = 0;
		
		
		
		
		return ges;
	}
}
