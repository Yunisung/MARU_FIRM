package com.pgmate.firm.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.lib.util.lang.CommonUtil;


/**
 * @author Administrator
 *
 */
public class Daemon extends Thread{

	private static Logger logger = LoggerFactory.getLogger(com.pgmate.firm.main.Daemon.class);
	private static int COUNTER	= 1;
	private Firm firm  = null;
	
	
	public Daemon(Firm firm){
		this.firm = firm;
	}
	
	public void run(){
		logger.info("firmbanking daemon start");
		
		try {
			while(true){
				try {
					
					long currentTime = CommonUtil.parseLong(CommonUtil.getCurrentDate("HHmmss"));
					
					if(firm.daemon.startTime < currentTime && currentTime < firm.daemon.stopTime){
						//logger.info("firm daemon running");
						FirmMain main = new FirmMain(firm);
						main.transMaster();
						main.wireTransfer();
//						main.firmMaster();
//						main.firmTrx();

					}
				
					Thread.sleep(firm.daemon.interval);
					COUNTER++;
					if(COUNTER % 200 == 0){
						logger.info("firm daemon running");
						COUNTER=1;
					}
	
				}catch(Exception e) {
					logger.info("firm daemon error : {}",CommonUtil.getExceptionMessage(e));
				}
			}
			
		} finally {
			logger.info("firm daemon stop");
		}
	}
	
	
	

	
	
	
	public static void main(String[] args){
		
		Firm firm = FirmLoader.getConfig();
		if(firm.daemon == null){
			System.out.println("firm.json is not found");
			System.exit(1);
		}else{
			Daemon t = new Daemon(firm);
			t.setDaemon(false);
			t.setName("FirmDaemon");
			t.start();
		}
	}
	

}
