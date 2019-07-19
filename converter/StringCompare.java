package converter;

public class StringCompare {
	
	public static void main(String[] args){
		System.out.println(computeLevenshteinDistance("RECORD_12", "GLAS"));
	}
	
	final static int[] wert = new int[]{0,1,2,3,0,1,2,0,0,2,2,4,5,5,0,1,2,6,2,3,0,1,0,2,0,2};
	
	public static String convertIntoChat(String message){
		
		int c1=0, c2=1;
		for(char c:message.toCharArray()){
			if(c=='*')c1++;
			else if(c=='_')c2++;
		}
		
		message = message.replace("*_*", "X-X").replace("*-*", "YY-ZZ");
		
		String m = "";						// ignoriere nicht * / _
		boolean unterstrich=false, fett=false, nig1=c1!=1, nig_=c2!=1;
		/*
		 *  §l	Fetter Text
			§m	Durchgestrichener Text
			§n	Unterstrichener Text
		 * */
		char c;
		for(int i=0;i<message.length();i++){
			switch(c=message.charAt(i)){
			case '_':
				if(nig_){
					if(unterstrich){
						m+=fett?"§r§l":"§r";
						unterstrich = false;
					} else {
						m+="§n";
						unterstrich = true;
					}
				}
				break;
			case '#':
				if(unterstrich){
					m+="§r§l#";
				} else m+="§l#";
				
				for(++i;i<message.length() && (c=message.charAt(i))!=' ';i++){
					m+=c;
				}
				if(c==' '){// der String geht weiter...
					if(!fett){// Fettheit löschen
						m+="§r";
					}
					if(unterstrich){// Unterstreichung wieder hinzufügen
						m+="§n";
					}
					m+=" ";
				}
				break;
			case '*':
				if(nig1){
					if(fett){
						m+=unterstrich?"§r§n":"§r";
						fett = false;
					} else {
						m+="§l";
						fett = true;
					}
				}
				break;
			default:
				m+=c;
			}
		}
		return m.replace("X-X", "*_*").replace("YY-ZZ", "*-*");
	}
	
	/**
	 * Soundex
	 * */
	public static String soundex(String s){
		
		s=s.toUpperCase().replaceAll("[^A-Z]", "")+"I";
		
		String u=s.charAt(0)+"";
		
		for(int i=1;i<s.length();i++){
			int c = s.charAt(i)-65;
			if(wert[c]!=0 && wert[c]+48!=u.charAt(u.length()-1)){
				u+=(char)(wert[c]+48);
			}
		}
		
		return u;
	}
	
	
	
	/**
	 * https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
	 * */
	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}
	 
	public static int computeLevenshteinDistance(String lhs, String rhs) {
		int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];
		
		for (int i = 0; i <= lhs.length(); i++){
			distance[i][0] = i;
		}
			
		for (int j = 1; j <= rhs.length(); j++){
			distance[0][j] = j;
		}
		
		for (int i = 1; i <= lhs.length(); i++){
			for (int j = 1; j <= rhs.length(); j++){
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));
			}
		}
		
		return distance[lhs.length()][rhs.length()];
	}
}
