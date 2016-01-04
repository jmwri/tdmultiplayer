package net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import server.Server;
import client.Game;

public class Listener implements Runnable {

	private boolean debugging;
	private boolean isRunning;
	private String name;
	private DatagramSocket socket;
	private Game game;
	private Server server;
	private String splitter = ":split:";
	
	public Listener(String name, DatagramSocket socket, Game game) {
		this.setName(name);
		this.setSocket(socket);
		this.setGame(game);
		this.setRunning(true);
	}
	
	public Listener(String name, DatagramSocket socket, Server server) {
		this.setName(name);
		this.setSocket(socket);
		this.setServer(server);
		this.setRunning(true);
	}

	@Override
	public void run() {
		if(this.isDebugging()) System.out.println(this.getName() + " listener started");
		byte[] receiveData = new byte[1024];
		while(this.isRunning()) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			try {
				this.getSocket().receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String data = "";
			try {
				data = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(this.isDebugging()) System.out.println(this.getName() + " listener received: " + data);
			String[] d = data.split(this.splitter);
			
			if(this.getGame() != null) this.getGame().dataReceived(d, receivePacket.getAddress(), receivePacket.getPort());
			if(this.getServer() != null) this.getServer().dataReceived(d, receivePacket.getAddress(), receivePacket.getPort());
		}
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDebugging() {
		return debugging;
	}

	public void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}

}
