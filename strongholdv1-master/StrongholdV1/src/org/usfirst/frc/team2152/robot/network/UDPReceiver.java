package org.usfirst.frc.team2152.robot.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPReceiver extends Thread {
    public final static String threadName  = "UDPReceiver";
    public final static int    PORT_NUMBER = 5807; 
    
    private boolean bContinue = true;
    private DatagramSocket socket = null;
    private int listenPort = PORT_NUMBER;
    
	public UDPReceiver (int port) {
		super(threadName);
		listenPort = port;
		
		try {
			socket = new DatagramSocket(listenPort);
		} catch (Exception e) {
			System.err.println("UDPReceiver: Error creating Datagram Socket: " + listenPort + ": " + e.toString());
	    }
	}
	
	public void run() {
		byte[] buf = new byte[10];
		buf[0] = 0;
		buf[1] = 0;
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		System.out.println("UDPReceiver: starting to listen on: " + listenPort);
		while (bContinue) {
			try {
	            socket.receive(packet);
	            System.out.println("UDPReceiver: Packet: " + new String(packet.getData(), 0, buf.length));
			} catch (Exception e) {
				System.err.println("UDPReceiver: Error receiving data: " + e.toString());
			}
		}
		cleanUp();
	}
	
	public void cleanUp() {
		if (socket != null) {
			socket.close();
			socket = null;
		    System.out.println("UDPReceiver: Closed socket on port " + listenPort);
		}
	}
	
	public void stopListening() {
		bContinue = false;
	}
	
	
}