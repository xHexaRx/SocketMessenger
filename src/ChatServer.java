import java.util.*;
import java.io.*;
import java.net.*;


public class ChatServer {
	
	ArrayList clientOutputStreams;
	
	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket socket;
		
		public ClientHandler(Socket clientSocket) {
			try {
				socket=clientSocket;
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			String text;
			try {
				while((text=reader.readLine())!=null) {
					System.out.println("Read: "+text);
					tellEveryone(text);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void go() {
		clientOutputStreams = new ArrayList();
		try {
			ServerSocket serverSocket=new ServerSocket(5000);
			while(true) {
				System.out.println("Waiting for connection");
				Socket clientSocket = serverSocket.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("Client connected");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void tellEveryone(String text) {
		Iterator it = clientOutputStreams.iterator();
		while(it.hasNext()){
			try {
				PrintWriter currentStream=(PrintWriter)it.next();
				currentStream.println(text);
				currentStream.flush();
				System.out.println("Send: "+text);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new ChatServer().go();
	}
	
}
