package com.pgmate.firm.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import com.pgmate.firm.util.FirmUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.inter.FirmBean;
import com.pgmate.lib.util.gson.GsonUtil;

/**
 * @author Administrator
 *
 */
public class FirmClient {

	private static Logger logger = LoggerFactory.getLogger( com.pgmate.firm.client.FirmClient.class );
//	private static String host 	= "10.100.100.13";
	private static String host 	= "10.100.200.10";
	private static int port 	= 10006;
	private static int timeout  = 40000;


	public FirmClient() {
	}


	public void resultCheck(String bankCd, String orgSeqNo) {
		// 처리결과조회
		logger.info("처리결과조회");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= "039";
		firmBean.msgType 	= "0600101";
		firmBean.userId		= "SYSTEM";
		firmBean.data.put("orgSeqNo", orgSeqNo);

		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
	}

	public void testCall(String bankCd){
		logger.info("테스트콜");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= bankCd;
		firmBean.msgType 	= "0800800";
		firmBean.userId		= "SYSTEM";

		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
	}

	public void open(String bankCd){
		logger.info("업무개시");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= bankCd;
		firmBean.msgType 	= "0800100";
		firmBean.userId		= "SYSTEM";

		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
	}

	public void close(String bankCd){
		logger.info("업무종료");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= bankCd;
		firmBean.msgType 	= "0800300";
		firmBean.userId		= "SYSTEM";

		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
	}


	public void balance(String bankCd, String mAccount){
		logger.info("모계좌잔액조회");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= bankCd;
		firmBean.msgType 	= "0600300";
		firmBean.userId		= "SYSTEM";
		firmBean.mAccnt     = mAccount;

		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
		logger.info("data : {}",GsonUtil.toJson(firmBean.data));
	}

	/**
	 * 잔액조회
	 * @param bankCd
	 * @param accntNo
	 * @return
	 */
	public FirmBean balance(String bankCd, String accntNo, String compCd){
		logger.info("잔액조회");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= bankCd;
		firmBean.msgType 	= "0600300";
		firmBean.userId		= "SYSTEM";

		firmBean.data.put("mAccnt",accntNo);
		firmBean.data.put("compCd",compCd);

		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
		logger.info("name : {}",firmBean.data.getString("name"));
		return firmBean;
	}

	/**
	 * 은행통한 예금주조회
	 * @param bankCd
	 * @param userBankCd
	 * @param userAccount
	 */
	public void holderFCS(String userBankCd,String userAccount){
		logger.info("예금주조회");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= "099";
		firmBean.msgType 	= "0600400";
		firmBean.userId		= "SYSTEM";
		firmBean.data.put("bankCd", userBankCd);
		firmBean.data.put("account", userAccount);
		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
		logger.info("name : {}",firmBean.data.getString("name"));
	}



	/**
	 * fcsCheck : (6)	신원확인번호 체크 : 계좌번호+신원확인번호 일치 여부 체크시 ‘99’ 세팅
	 * 예금주명+신원확인번호 일치 여부 체크시 ‘77’ 세팅 (실명 인증)
	 * @param userBankCd
	 * @param userAccount
	 * @param holder
	 * @param socialNumber
	 * @param fcsCheck
	 */
	public void holderFCS(String userBankCd,String userAccount,String holder,String socialNumber,String fcsCheck){
		logger.info("예금주조회");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= "099";
		firmBean.msgType 	= "0600400";
		firmBean.userId		= "SYSTEM";
		firmBean.data.put("bankCd", userBankCd);
		firmBean.data.put("account", userAccount);
		firmBean.data.put("name", holder);
		firmBean.data.put("socialNumber", socialNumber);
		firmBean.data.put("socialCheck", fcsCheck);
		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
		logger.info("name : {}",firmBean.data.getString("name"));
	}


	/**
	 * FCS 예금주조회
	 * @param bankCd
	 * @param userBankCd
	 * @param userAccount
	 */
	public void holder(String userBankCd,String userAccount){
		logger.info("예금주조회");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= "099";
		firmBean.msgType 	= "0600400";
		firmBean.userId		= "SYSTEM";
		firmBean.data.put("bankCd", userBankCd);
		firmBean.data.put("account", userAccount);
		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
		logger.info("name : {}",firmBean.data.getString("name"));
	}


	/**
	 * 집계
	 * @param bankCd
	 */
	public void statistics(String bankCd){
		logger.info("모계좌집계");
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= bankCd;
		firmBean.msgType 	= "0700100";
		firmBean.userId		= "SYSTEM";
		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.get("resData"));
		logger.info("data : {}",GsonUtil.toJson(firmBean.data));


	}


	public void transfer(String bankCd, String recvBankCd,String recvAccount,long amount){
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= bankCd;
		firmBean.msgType 	= "0100100";
		firmBean.userId		= "SYSTEM";
		firmBean.data.put("amount",amount);
		firmBean.data.put("recvBankCd",recvBankCd);
		firmBean.data.put("recvAccount",recvAccount);
		firmBean.data.put("sender", "");
		firmBean.data.put("procType", "CS");

		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.getLong("balance"));
		logger.info("data : {}",GsonUtil.toJson(firmBean.data));
	}

	public void reTransfer(String trxId) {
		//이체 재시도
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= "089";
		firmBean.msgType 	= "0600102";
		firmBean.userId		= "SYSTEM";
		firmBean.data.put("trxId", trxId);

		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.getLong("balance"));
		logger.info("data : {}",GsonUtil.toJson(firmBean.data));
	}

	public void reg() {
		FirmBean firmBean = new FirmBean();
		firmBean.bankCd 	= "039";
		firmBean.msgType 	= "0900400";
		firmBean.userId		= "SYSTEM";
		firmBean.data.put("trxType", "1");         // (거래구분) '1':신규, '4':해지, '8':변경, '9':조회
		firmBean.data.put("companyCd", "MBR00246");
		firmBean.data.put("virtualAccount", "8008308817439");  // (가상계좌번호)
		firmBean.data.put("withdrawBankCd", "090");  // (출금은행코드) PG_CODE 테이블 참조
		firmBean.data.put("withdrawAccount", "3333064866100"); // (출금계좌번호)
		firmBean.data.put("customerName", "박윤성");    // (고객명)
		firmBean.data.put("regType", "1");
		firmBean.data.put("identity", "8901021");





		firmBean = comm(firmBean);
		logger.info("응답:{},{}",firmBean.resultCd,firmBean.resultMsg);
		logger.info("idx:{},{}",firmBean.idx,firmBean.data.getLong("balance"));
		logger.info("data : {}",GsonUtil.toJson(firmBean.data));
	}



	public FirmBean comm(FirmBean firmBean){

		Socket socket = null;
		OutputStream output = null;
		InputStream input = null;
		String reqJson = GsonUtil.toJson(firmBean);
		String resJson = "";
		long time = System.currentTimeMillis();
		try{
			socket = new Socket(host, port);
			socket.setSoTimeout(timeout);

			output = socket.getOutputStream();
			output.write(reqJson.getBytes(Charset.forName("EUC-KR")));
			output.flush();

			input = socket.getInputStream();

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int bcount = 0;
			byte[] buf = new byte[2048];
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
			byte[] res = bout.toByteArray();
			bout.close();

			firmBean = (FirmBean)GsonUtil.fromJson(new String(res), FirmBean.class);

		}catch(Exception e){
			firmBean.resultCd = "XXXX";
			firmBean.resultMsg = "펌뱅킹 시스템과의 통신장애 :"+e.getMessage();
		}finally{
			logger.info("-> FIRM : [{}]",reqJson);
			logger.info("<- FIRM : [{}],{}",resJson,(System.currentTimeMillis()-time));

			try{
				if(input != null){ input.close();}
				if(output != null){ output.close();}
				if(socket != null){ socket.close();}
			}catch(Exception ex){

			}
		}

		return firmBean;

	}


	public static void main(String[] args){
		FirmClient client = new FirmClient();

		//###########부국 테스트 #################
//		client.transfer("039", "081", "43591027511307", 100);
//		client.transfer("039", "039", "587220142117", 100);
//		client.balance("089", "70022000000008");
//		client.balance("039", "2070008840700");
//		client.holder("088", "110487944164");
//		client.holder("088", "100035419428");
//		client.resultCheck("039", "000537");
		client.reg();
//		client.reTransfer("CS221110037667");



		//#####################################
		//client.open("020");
		//client.testCall("020");
		//client.balance("020");
		//client.statistics("020");
		//client.holder("020", "94000006218719"); //가상계좌
		//client.trasfer("020","020", "27939792518629", 3000);
		//client.trasfer("020","088", "100001312970", 120000);//타행이체불능명세
		//client.trasfer("020","020", "1002735519320", 100);//가상계좌거래내역


		//logger.info("계좌조회");
		//client.holderFCS("088", "110311129095");
		//client.holderFCS("020", "");
//		client.balance("039","2070079982702","SDS00268"); //FCS용 테스트 계좌 . 850611 , 달나라가자
		/*
		logger.info("계좌 + 신원확인번호 ");
		client.holderFCS("011", "24202211712","","850611","77"); //FCS용 테스트 계좌 . 850611 , 달나라가자
		logger.info("계좌 + 예금주 + 신원확인번호 ");
		client.holderFCS("011", "24202211712","달나라가자","850611","99"); //FCS용 테스트 계좌 . 850611 , 달나라가자


		logger.info("----------\n");

		logger.info("계좌조회");
		client.holderFCS("004", "012211411610"); //FCS용 테스트 계좌 .730211 , 김련리
		logger.info("계좌 + 신원확인번호 ");
		client.holderFCS("004", "012211411610","","730211","77"); //FCS용 테스트 계좌 . 850611 , 달나라가자
		logger.info("계좌 + 예금주 + 신원확인번호 ");
		client.holderFCS("004", "012211411610","김련리","730211","99"); //FCS용 테스트 계좌 . 850611 , 달나라가자
	*/
	}







}
