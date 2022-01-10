package com.pgmate.firm.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.ServerBean;
import com.pgmate.lib.util.lang.CommonUtil;


/**
 * @author Administrator
 *
 */
public class SocketThread {
	private ServerSocket serverSocket 	= null;
	private Thread serviceThread 		= null;
	private boolean running 			= false;
	private LinkedList<Thread> threads			= new LinkedList<Thread>();
	private Logger logger = LoggerFactory.getLogger( getClass() );
	private Firm firm = null;

	public SocketThread(Firm firm,ServerSocket serverSocket) throws Exception{
		this.firm = firm;
		this.serverSocket = serverSocket;
		
		serviceThread 	= new Thread( new Runnable() {
			public void run() {
				serviceThread();
			}
		});

		serviceThread.start();
	}

	public void close() throws Exception {
		waitForServiceThreadToStart();
		running = false;
		serverSocket.close();
		serviceThread.join();
		waitForServerThreads();
	}

	private void waitForServiceThreadToStart() {
		while(running == false)
			Thread.yield();
	}

	private void serviceThread() {
		running = true;
		while(running) {
			try {
				Socket socket = serverSocket.accept();
				startServerThread(socket);
			}catch(IOException e) {
				logger.debug("error : {}",CommonUtil.getExceptionMessage(e));
			}
		}
	}

	private void startServerThread(Socket socket) {
		Thread serverThread = new Thread(new ServerRunner(socket));
		synchronized(threads) {
			threads.add(serverThread);
		}
		serverThread.start();
	}

	private void waitForServerThreads() throws InterruptedException {
		while(threads.size() > 0) {
			Thread t;
			synchronized(threads) {
				t = (Thread)threads.getFirst();
			}
			t.join();
		}
	}



	public class ServerRunner implements Runnable {

		private Socket itsSocket = null;

		ServerRunner(Socket socket){
			itsSocket = socket;
		}

		public void run() {
			try{
				new Listener(firm,itsSocket);
				
				synchronized(threads) {
					threads.remove(Thread.currentThread());
				}
				itsSocket.close();
			}catch(IOException e){
				logger.debug("error : {}",CommonUtil.getExceptionMessage(e));
			}
		}
	}
	
	
	
}
