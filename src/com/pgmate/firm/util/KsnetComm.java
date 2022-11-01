package com.pgmate.firm.util;

import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.ServerBean;
import com.pgmate.firm.dao.FirmDAO;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.lib.util.lang.CommonUtil;


/**
 * @author Administrator
 *
 */
public class KsnetComm {

	private static final Logger logger 	= (Logger) LoggerFactory.getLogger(com.pgmate.firm.util.KsnetComm.class);
	private ServerBean conf 		= null;
	private static String SVC_CODE	= "1000";
	private static String REG_SVC_CODE	= "4000";
	private static String FCS_SVC_CODE	= "5000";
	
	private SmsGw smsGw = null;
	
	//KSNET 정보 운영 : 121.138.30.10  19237, 테스트 : 210.181.28.103  19238 KSNET 공인 IP 전달 요망 
	
	public KsnetComm(ServerBean conf){
		this.conf = conf;
	
	}
	
	
	public FBHeaderBean ksnet(FBHeaderBean headerBean) throws UnsupportedEncodingException {
		long time 					= System.currentTimeMillis();
		
		FBHeaderBean resHeaderBean 	= null;
		String reqMsg  				= headerBean.getTransaction()+headerBean.getTransactionIndex();
		byte[] request				= null;
		byte[] response 			= null;
		String message				= "";
    	Socket socket 				= null;
    	OutputStream output 		= null;
    	InputStream input			= null;
    	byte[] key					= null;
    	
     	smsGw = new SmsGw();
 
     	logger.info("specCode [{}]",headerBean.getSpecCode());
     	
     	try{
    		socket = new Socket();
    		message= "소켓 오픈 오류";
    		
    		socket.connect(new InetSocketAddress(conf.ksnetIp,conf.ksnetPort));
    		socket.setSoTimeout(conf.timeout);
    		
    		
    		output = socket.getOutputStream();
    		message= "데이터 전송 실패";
//    		key = generateKey();
//    		request = encrypt(key,reqMsg);
    		logger.info("-> KSNET [{}]",reqMsg);
    		//logger.debug("-> KSNET [{}],{}",CommonUtil.toString(request),request.length);
    		output.write(reqMsg.getBytes("euc-kr"));
    		output.flush();
    		
    		message= "데이터 수신 오류";
    		input = socket.getInputStream();
    		
    		
    		ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int bcount = 0;
            byte[] buf = new byte[8192];
            int read_retry_count = 0;
            while(true) {
    			int n = input.read(buf);
                if ( n > 0 ) { bcount += n; bout.write(buf,0,n); }
                else if (n == -1) break;
                else  { // n == 0
                    if (++read_retry_count >= 5)
                      throw new IOException("inputstream-read-retry-count(5) exceed !");
                }
                if(input.available() == 0){ break; }
            }
            bout.flush();
            response = bout.toByteArray();
            bout.close();
    		
            if(response != null){
//            	logger.debug("<- KSNET [{}],{}",CommonUtil.toString(response),response.length);
            	byte[] resBuf = new byte[response.length-4];
            	System.arraycopy(response, 4, resBuf, 0, response.length-4);
//            	response  = decrypt(key, resBuf);
            }else{
            	logger.info("<- KSNET [null]");
            }
            
    		/**
    		int receivedLength = 0;
    		boolean run = true;
    		while(run){
    			byte[] temp = new byte[SPEC_LEN-receivedLength];
    			int read = input.read(temp);
    			CommonUtil.arrayCopy(response, receivedLength,temp,read);
    			receivedLength += read;
    			if(receivedLength >= SPEC_LEN){
    				run = false;
    			}
    		}**/
   
		}catch(Exception e){
			//logger.info("KSNET COMM ERROR : {}",CommonUtil.getExceptionMessage(e));
			logger.info("KSNET COMM ERROR [{}], [{}]", e.getMessage(), e.getStackTrace());
			
			logger.info("COMM MESSAGE : {} ",message);
			headerBean.setBankResponseCode("XXXX");
			headerBean.setKsnetResponseCode("XXXX");
			headerBean.setTransactionTime(CommonUtil.getCurrentDate("yyyyMMddHHmmss"));
			response = (headerBean.getTransaction()+headerBean.getTransactionIndex()).getBytes("UTF-8");
			if(headerBean.getSpecCode().equals("0100")) {
				String msgBody = "KSNET 펌뱅킹 출금 장애 발생 [" + message + "] ";
				smsGw.sendMessage("1", "1", msgBody);
			}
			
		}finally{
			try{
				if(input != null){ input.close();}
				if(output != null){ output.close();}
				if(socket != null){socket.close();}
			}catch(IOException io){
			}
			resHeaderBean = new FBHeaderBean(response);
			resHeaderBean.setIndex(headerBean.getIndex());
			resHeaderBean.setProcId(headerBean.getProcId());
			resHeaderBean.setProcType(headerBean.getProcType());
			resHeaderBean.setTransactionIndex(CommonUtil.toString(response,100));
			
			logger.info("<- KSNET [{}],{},{}",CommonUtil.toString(response),response.length,(System.currentTimeMillis()-time));
			
			//여유 필드에 응답코드에 해당하는 메세지를 기입한다.
			resHeaderBean.setMessage(FirmDAO.getCodeDesc(resHeaderBean.getNewBankCode(),resHeaderBean.getBankResponseCode()));
		}
		
		return resHeaderBean;
	}

	private byte[] generateKey(){
		return new StringBuffer().append(System.currentTimeMillis()).append(Long.MAX_VALUE).substring(0,16).getBytes();
	}
	
	private byte[] encrypt(byte[] key , String req){
		byte[] buf = null;
		
		if(req.substring(19,23).equals("0900") && req.substring(23,26).equals("400")){
			//가상계좌 출금정보 등록시 서비스코드 4000
			logger.info("ENCRYPT : {}",KsnetComm.REG_SVC_CODE);
			buf = (KsnetComm.REG_SVC_CODE+conf.sendAuthKey+req).getBytes();
		}else {
			if(req.substring(9,12).equals("FCS")){
				logger.info("ENCRYPT : {}",KsnetComm.FCS_SVC_CODE);
				buf = (KsnetComm.FCS_SVC_CODE+conf.sendAuthKey+req).getBytes();
			}else{
				logger.info("ENCRYPT : {}",KsnetComm.SVC_CODE);
				buf = (KsnetComm.SVC_CODE+conf.sendAuthKey+req).getBytes();
			}
		}
		
		return kscms_encrypt(key,buf);
	}
	
	private  byte[] decrypt(byte[] key, byte[] buf){
		try{
			return ks_seed_decrypt(key,buf);
		}catch(Exception e){
			logger.error("kscms_decrypt error : {}",e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	private byte[] kscms_encrypt(byte[] p_kbuf, byte[] p_dbuf){
		try{
			byte[] e_kbuf	= ks_rsa_encrypt(p_kbuf);

			byte[] e_dbuf	= ks_seed_encrypt(p_kbuf, p_dbuf);
			byte[] e_sbuf	= new byte[4+1+e_kbuf.length+4+e_dbuf.length];

			int midx = 0;
			System.arraycopy("01292".getBytes(), 0	,e_sbuf	,midx,  5); midx+= 5;
			System.arraycopy(e_kbuf, 0 , e_sbuf,midx, e_kbuf.length); midx+=e_kbuf.length	;

			System.arraycopy(("0000".substring(String.valueOf(e_dbuf.length).length(),4) + e_dbuf.length).getBytes()	,0	,e_sbuf	,midx,  4			); midx+= 4				;
			System.arraycopy(e_dbuf, 0, e_sbuf,midx, e_dbuf.length); midx+=e_dbuf.length	;

			return e_sbuf;
		}catch(Exception e){
			logger.error("kscms_encrypt error : {}",e.getMessage());
		}

		return null;
	}
	
	
	private byte[] ks_rsa_encrypt(byte[] sbuf) throws java.security.NoSuchProviderException,javax.crypto.NoSuchPaddingException,javax.crypto.BadPaddingException,java.security.NoSuchAlgorithmException,java.security.spec.InvalidKeySpecException,java.security.InvalidKeyException,javax.crypto.IllegalBlockSizeException{
		BigInteger modulus			= new BigInteger("d4846c2b8228dddfab9e614da2a324c1cc7b29d848cc005624d3a09667a2aab9073290bace6aa536ddceb3c47ddda78d9954da06c83aa65b939c5ec773a3787e71bec5a1c077bb446c06b393d2537967645d386b4b0b4ec21372fdc728c56693028c1c3915c1c4279793eb3dccefd6bf49b86cc7d88a47b0d44aba9e73750fcd",16);
		BigInteger publicExponent	= new BigInteger("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010001",16);

		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, publicExponent);

		KeyFactory keyfactory = KeyFactory.getInstance("RSA");
		java.security.PublicKey publickey = keyfactory.generatePublic(pubKeySpec);

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

		cipher.init(Cipher.ENCRYPT_MODE, publickey);

		byte[] rbuf = cipher.doFinal(sbuf);

		return rbuf;
	}
	
	
	public byte[] ks_seed_encrypt(byte[] kbuf, byte[] mbuf) throws java.security.NoSuchProviderException,javax.crypto.BadPaddingException,javax.crypto.NoSuchPaddingException,java.security.InvalidAlgorithmParameterException,java.security.NoSuchAlgorithmException,java.security.InvalidKeyException,javax.crypto.IllegalBlockSizeException{
		byte tdata[]	= new  KSBankSeed(kbuf).cbc_encrypt(mbuf) ;
		return tdata;
	}
	
	
	
	
	private byte[] ks_seed_decrypt(byte[] kbuf, byte[] mbuf) throws java.security.NoSuchProviderException,javax.crypto.NoSuchPaddingException,java.security.InvalidAlgorithmParameterException,javax.crypto.BadPaddingException,java.security.NoSuchAlgorithmException,java.security.InvalidKeyException,javax.crypto.IllegalBlockSizeException{
		byte tdata[]	= new  KSBankSeed(kbuf).cbc_decrypt(mbuf) ;
		return tdata;
	}
	
	
	
}
