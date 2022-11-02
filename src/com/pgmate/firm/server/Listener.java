
package com.pgmate.firm.server;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.util.FirmUtil;
import com.pgmate.lib.util.comm.TcpSocket;
import com.pgmate.lib.util.lang.CommonUtil;

public class Listener {
	
	private Logger logger = LoggerFactory.getLogger( getClass() );
	private TcpSocket	socket	= null;
	private String source 		= "";	
	private String clientIp		= "";
	private Firm firm   = null;
	private long startTime			= System.currentTimeMillis();
	

	
	public Listener(Firm firm,Socket receiveSocket)throws IOException {
		this.firm = firm;
		socket = new TcpSocket(receiveSocket);
		clientIp = socket.getClientIp().replaceAll("[/]", "");

		logger.info("Connect Client IP : [{}]", clientIp);

		if(!firm.server.monitorIp.equals("") && clientIp.indexOf(firm.server.monitorIp) > -1){
			source = FirmUtil.MONITOR;
			socket.socketClose();
		}
		else if(!firm.server.internalIp.equals("") && clientIp.indexOf(firm.server.internalIp) > -1){
			source = FirmUtil.INTERNAL;
		}else if(clientIp.indexOf("1.212.11.242") > -1 || clientIp.indexOf("175.209.131.216") > -1 ||
				clientIp.indexOf("127.0.0.1") > -1 || clientIp.indexOf("10.100.200.10") > -1 ||
				clientIp.indexOf("192.168.") > -1){
			source = FirmUtil.INTERNAL;
		}else{
			source = FirmUtil.KSNET;
		}
//		else {
//			source = FirmUtil.HYPHEN;
//		}

		if(!source.equals(FirmUtil.MONITOR)){
			process();
		}
		
	}
	
	public void process(){
		byte[] recv = null;
		byte[] send = null;
		try{
			logger.debug("-> {},{}",source,clientIp);
			recv = socket.recvAll();
			//logger.debug("-> {} [{}],{}",source,CommonUtil.toString(recv),recv.length);
			//logger.debug("-> {} [{}],{}",source,CommonUtil.toString(recv),recv.length);

			if(source.equals(FirmUtil.KSNET)){	//KSNET 수신 프로세스

				logger.debug("-> {} [{}]",source,CommonUtil.toString(recv));

				recv = FirmUtil.udecode_3des(firm.server.serverAuthKey.getBytes(),recv);
				logger.info("-> {} [{}]",source,CommonUtil.toString(recv));


				send = new BankProcess(firm).execute(recv);
				logger.info("-> {} resultCd : [{}]",source,CommonUtil.toString(send,51,4));
				logger.info("<- {} [{}]",source,CommonUtil.toString(send));
				send = FirmUtil.uencode_3des(firm.server.serverAuthKey.getBytes(),send);
				logger.debug("<- {} [{}]",source,CommonUtil.toString(send));



			}else{								//내부망 프로세스

				//recv = FirmUtil.udecode_3des(firm.server.encryptKey.getBytes(),recv);

				logger.info("-> {} [{}]",source,CommonUtil.toString(recv));

				send = new InterProcess(firm).execute(new String(recv)).getBytes();

				logger.info("<- {} [{}]",source,CommonUtil.toString(send));
				//send = FirmUtil.uencode_3des(firm.server.encryptKey.getBytes(),send);

			}

			// 소켓 통신으로 펌뱅킹만 사용함. 가상계좌는 MARU_VACT_HYPHEN에서 사용
//			if(source.equals(FirmUtil.HYPHEN)) {
//				logger.info("-> {} [{}]", source, CommonUtil.toString(recv));
//				send = new InterProcess(firm).execute(new String(recv)).getBytes();
//				logger.info("<- {} [{}]", source, CommonUtil.toString(send));
//			}
			
			socket.send(send);
			socket.ioClose();
		}catch(Exception e){
			e.printStackTrace();
			logger.info("<- {} [RESPONSE ERROR] : {} ",source,CommonUtil.getExceptionMessage(e));
		}finally{
			logger.debug("<- {} , {}sec",source,(System.currentTimeMillis()-startTime));
			logger.debug("\n\n");
			socket.socketClose();
		}
	}

	
	
}
