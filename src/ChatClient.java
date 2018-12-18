import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class ChatClient {
	
	JTextField outgoing;
	JTextArea incoming;
	Socket socket;
	PrintWriter writer;
	BufferedReader reader;
	
	public void go(String serverAdress) {
		//initialise, call setUpNetworking(), add listener
		
		JFrame frame = new JFrame("Chat Client");
		JPanel mainPanel = new JPanel();
		outgoing = new JTextField(20);
		incoming = new JTextArea(15,30);
		incoming.setLineWrap(true);
		incoming.setEditable(false);
		incoming.setWrapStyleWord(true);
		JScrollPane qScroll = new JScrollPane(incoming);
		qScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		qScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		JButton sendButton = new JButton("Send");
		
		sendButton.addActionListener(new SendButtonListener());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel.add(qScroll);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		
		frame.getContentPane().add(BorderLayout.CENTER,mainPanel);
		
		frame.setSize(400,400);
		frame.setVisible(true);
		
		setUpNetworking(serverAdress);
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}
	
	//Establishing connection and setting up output stream.
	public void setUpNetworking(String serverAdress) {
		try {
			socket = new Socket(serverAdress, 5000);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer= new PrintWriter(socket.getOutputStream());
			System.out.println("Connection established.");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	class SendButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			//send data on click
			try {
				writer.println(outgoing.getText());
				writer.flush();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
				outgoing.setText("");
				outgoing.requestFocus();
		}
	}
	
	class IncomingReader implements Runnable {
		public void run() {
			String text;
			try {
				while((text=reader.readLine()) != null) {
					System.out.println("read: "+ text);
					incoming.append(text+"\n");
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		ChatClient cc = new ChatClient();
		String serverAdress;
		if(args.length>0) {
			serverAdress=args[0];
		}
		else {
			serverAdress="127.0.0.1";
		}
		cc.go(serverAdress);
	}
}
