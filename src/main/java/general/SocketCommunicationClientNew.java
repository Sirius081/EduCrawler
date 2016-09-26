package main.java.general;

import java.io.*;
import java.net.*;
public class SocketCommunicationClientNew {
	private static int port;
	private static String ip;
	private static int sourceID;
	private  Socket socket;
	private  PrintWriter os;

	public SocketCommunicationClientNew(String ip,int port,int sourceID) {
		this.ip = ip;
		this.port = port;
		this.sourceID = sourceID;
		try {
			socket = new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			os = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
			os.println(sourceID + "\t" + 0);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message){

		if(message!= null && !message.equals("") && !message.equals("bye")){
			os.println(message);
			os.flush();
		}
		else{
			os.close();
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}