package com.pgmate.firm.server;

import java.net.InetAddress;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.main.Daemon;


public class Server {
	private Logger logger = LoggerFactory.getLogger( getClass() );
	private Firm firm = null;
	
	public Server() throws Exception {
		firm = FirmLoader.getConfig();
	}
	
	public boolean start() throws Exception {
		
		
		ServerSocket server = null;
		if(firm.server.serverIp.equals("")){
			server = new ServerSocket(firm.server.serverPort);
		}else{
			server = new ServerSocket(firm.server.serverPort, 0, InetAddress.getByName(firm.server.serverIp));
		}
		logger.info("server start bind:{},{}",firm.server.serverIp,firm.server.serverPort);
		new SocketThread(firm,server);
		return true;
	}
	

	
	public static void main(String [] args){
		try{
			Daemon.main(null);
			Server ss = new Server();
			if(ss.start()){
				System.out.println("Started ... ^^ ");
			}else{
				System.out.println("Start fail");
			}
		}catch(Exception e){System.out.println("오류발생 : "+e.getMessage());}
	}

}
