package converter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import me.corperateraider.myworld.Plugin;

/**
 * a class to test web services if they are allowed :)
 * */
public class WebServer {
	
	/**
	 * Der Plan, mehrere Versionen zu unterstützen...
	 * @throws InterruptedException 
	 * @throws IOException 
	 * */
	public static void main(String[] args) throws InterruptedException, IOException {
		new WebServer(25565);
		while(true){Thread.sleep(10);}
	}

	static String paypal = "<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\" target=\"_top\">"
		+ "<input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\">"
		+ "<input type=\"hidden\" name=\"hosted_button_id\" value=\"Q63TPHRFTENB4\">"
		+ "<input type=\"image\" src=\"https://www.paypalobjects.com/de_DE/DE/i/btn/btn_donateCC_LG.gif\" border=\"0\" name=\"submit\" alt=\"Jetzt einfach, schnell und sicher online bezahlen – mit PayPal.\">"
		+ "<img alt=\"\" border=\"0\" src=\"https://www.paypalobjects.com/de_DE/i/scr/pixel.gif\" width=\"1\" height=\"1\"></form>";
	
	static String html =
			  "<!DOCTYPE html>"
			+ "<html>"
				+ "<body>"
					+ "<h1>City Hardcore RPG Server - Donation site</h1>"
					+ "<h2>Hardcore Rollenspiel Stadtserver von Antonio Noack - Spendenseite; Vielen Dank an alle Unterstützer!</h2>"
					+ "<img src='https://lh4.googleusercontent.com/-UnshOliYkv0/VnUR5KOh7SI/AAAAAAAACFE/5RoBI3K-S8U/w1594-h845-no/2015-12-19_09.12.45.png'></img>"
					+ paypal
				+ "</body>"
			+ "</html>";
	
	ArrayList<Socket> clients = new ArrayList<>();
	ServerSocket server;
	final static byte[] nextLine = new byte[]{0xd,0xa};
	public Thread t;
	public WebServer(int port) throws IOException{
		
		server = new ServerSocket(port);
		t = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					try {
						final Socket socket=server.accept();
						clients.add(socket);
						
						final InputStreamReader read = new InputStreamReader(socket.getInputStream());// in der Zeile mit get steht dann auch die Websiteunterseite :)
						final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						
						new Thread(new Runnable(){

							@Override
							public void run() {
								try {
									while(true){
										int c = read.read();
										if(c!=-1){
											if(c=='\n')System.out.println("\\n");
											else System.out.print((char)c);
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
						}).start();
						
						new Thread(new Runnable(){

							@Override
							public void run() {
								try {
									out.write(StringToASCII("HTTP/1.1 200 OK"));
									out.write(nextLine);
									out.write(StringToASCII("Content-Type: text/html"));
									out.write(nextLine);
									out.write(StringToASCII("Connection: close"));
									out.write(nextLine);
									out.write(nextLine);
									out.write(StringToASCII(html));
									out.close();
									socket.close();
								} catch(IOException e){
									e.printStackTrace();
								}
							}
							
						}).start();
						
					} catch (IOException e) {System.out.println("web."+e.getMessage());}
				}
			}
		});
		t.start();
		Plugin.threads.add(t);
	}
	
	public void close() throws IOException{
		server.close();
	}
	
	static byte[] StringToASCII(String s){
		byte[] ret = new byte[s.length()];
		
		for(int i=0;i<ret.length;i++){
			ret[i]=(byte)(int) s.charAt(i);
		}
		
		return ret;
	}
}
