package com.pgmate.firm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.pgmate.firm.hyphen.DepositBean;
import com.pgmate.firm.hyphen.HyphenBean;
import com.pgmate.firm.hyphen.TransferBean;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.ksnet.FB0100100Bean;
import com.pgmate.firm.ksnet.FB0600101Bean;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.firm.util.FirmUtil;
import com.pgmate.firm.util.WooriSign;
import com.pgmate.lib.util.db.DBFactory;
import com.pgmate.lib.util.db.DBManager;
import com.pgmate.lib.util.lang.CommonUtil;
import com.pgmate.lib.util.map.SharedMap;

/**
 * @author Administrator
 *
 */
public class FirmTrxDAO {
	private final static Logger logger = (Logger) LoggerFactory.getLogger(com.pgmate.firm.dao.FirmTrxDAO.class);
	private SharedMap<String,BankBean> map = null;


	public FirmTrxDAO(){
	}

	public FirmTrxDAO(SharedMap<String,BankBean> map) {
		this.map = map;
	}

	public List<HyphenBean> selectbyHyphen(){
		String query = " SELECT idx,bankCd,seqNo,amount,recvBank,recvAccount,checkDigit,recvHolder,recordInfo,procType,procId	FROM PG_FIRM_TRX WHERE sendDate = DATE_FORMAT(now(), '%Y%m%d') 	AND procGb='R' AND procType != 'BT' ORDER BY idx ASC LIMIT 10";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		List<HyphenBean> list = new ArrayList<HyphenBean>();


		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			rset 	= pstmt.executeQuery();

			while(rset.next()){

				BankBean configBean = map.get(CommonUtil.nToB(rset.getString("bankCd")));

				if(configBean != null) {

					DepositBean depositBean = new DepositBean();
					depositBean.setCompCode(configBean.compCd);
					depositBean.setBankCode(configBean.bankCd);

					depositBean.setSeqNo(CommonUtil.nToB(rset.getString("seqNo")));
					depositBean.setOutAccount(configBean.account);
					depositBean.setAmount(rset.getLong("amount"));
					depositBean.setInBankCode(rset.getString(("recvBank")));
					depositBean.setInAccount(rset.getString("recvAccount"));
					depositBean.setInPrintContent(rset.getString("recvHolder"));

					HyphenBean hyphenBean = new HyphenBean();
					hyphenBean.setIndex(rset.getLong("idx"));
					hyphenBean.setKscode(configBean.kscode);
					hyphenBean.setEkey(configBean.ekey);
					hyphenBean.setMsalt(configBean.msalt);
					hyphenBean.setSendurl("rfb/retail/deposit");
					hyphenBean.setReqdata(depositBean);


					list.add(hyphenBean);
				} else {
					logger.info("해당은행코드에 해당하는 config값이 없습니다. : [{}]", rset.getString("bankCd"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		return list;
	}

	public List<FBHeaderBean> select(){
		String query = " SELECT idx,bankCd,sendTime,seqNo,amount,recvBank,recvAccount,checkDigit,recvHolder,recordInfo,procType,procId	FROM PG_FIRM_TRX WHERE sendDate = DATE_FORMAT(now(), '%Y%m%d') 	AND procGb='R' AND procType != 'BT' ORDER BY idx ASC LIMIT 10";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		List<FBHeaderBean> list = new ArrayList<FBHeaderBean>();


		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			rset 	= pstmt.executeQuery();

			while(rset.next()){

				FBHeaderBean headerBean = new FBHeaderBean();
				headerBean.setIndex(rset.getLong("idx"));
				headerBean.setNewBankCode(CommonUtil.nToB(rset.getString("bankCd")));
				BankBean configBean = map.get(headerBean.getNewBankCode());

				if(configBean != null) {
					headerBean.setIdentificationCode(configBean.trCd);
					headerBean.setCompanyCode(configBean.compCd);
					headerBean.setSpecCode("0100");
					headerBean.setClassificationCode("100");
					headerBean.setFrequency("1");
					headerBean.setSpecNumber(CommonUtil.nToB(rset.getString("seqNo")));
					headerBean.setTransactionTime(CommonUtil.getCurrentDate("yyyyMMddHHmmss"));
					FB0100100Bean fb0100100Bean = new FB0100100Bean();

					//가상계좌 대행서비스일 경우 새로운 모계좌에서 출금되도록 변경(2021.05.11)
					if("VA".equals(rset.getString("procType"))){
						//가상계좌 대행서비스 펌출금
						fb0100100Bean.setMAccount(configBean.newAccount);
					}else {
						fb0100100Bean.setMAccount(configBean.account);
					}

					fb0100100Bean.setMAccountPassword("");
					fb0100100Bean.setTrasferTime(CommonUtil.nToB(rset.getString("sendTime")));
					fb0100100Bean.setAmount(CommonUtil.toString(rset.getLong("amount")));
					fb0100100Bean.setReceiveNewBankCode(CommonUtil.nToB(rset.getString("recvBank")));
					fb0100100Bean.setReceiveAccount(CommonUtil.nToB(rset.getString("recvAccount")));
					fb0100100Bean.setSign(CommonUtil.nToB(rset.getString("checkDigit")));
					fb0100100Bean.setSenderName(CommonUtil.nToB(FirmUtil.changeCharset(rset.getString("recvHolder"),"MS949")));
					fb0100100Bean.setReceiverName(CommonUtil.nToB(rset.getString("recordInfo")));

					headerBean.setProcType(CommonUtil.nToB(rset.getString("procType")));
					headerBean.setProcId(CommonUtil.nToB(rset.getString("procId")));

					//PYS: 안쓰는 로직
//					if(headerBean.getNewBankCode().equals("020") && CommonUtil.isNullOrSpace(fb0100100Bean.getSign())){
//						String sign = 	WooriSign.getSign(fb0100100Bean.getReceiveAccount(), CommonUtil.getAmountFormat(fb0100100Bean.getAmount()), fb0100100Bean.getReceiveNewBankCode(), fb0100100Bean.getMAccount());
//						fb0100100Bean.setSign(sign);
//					}

					headerBean.setTransactionIndex(fb0100100Bean.getTransaction());
					list.add(headerBean);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		return list;
	}

	public long selectFirmIdx(String rootSpecNumber){

		String query = " SELECT idx FROM PG_FIRM_TRX WHERE seqNo = ?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		long idx = 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1, rootSpecNumber);
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				idx = rset.getLong("idx");
			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		return idx;
	}

	public boolean updateStatus(long idx,String status){

		String query = "UPDATE PG_FIRM_TRX SET procGb =? WHERE idx =?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,status);
			pstmt.setLong(2,idx);
			result = pstmt.executeUpdate();

			conn.commit();

		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
			logger.info("UPDATE PG_FIRM_TRX SET procGb ={} WHERE idx ={}",status,idx);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean updatebyHyphen(HyphenBean hyphenBean) {
		String query = "UPDATE PG_FIRM_TRX SET procGb =?, recvDate=DATE_FORMAT(now(), '%Y%m%d'), recvTime=DATE_FORMAT(now(), '%H%i%s'), balance=?, fee=?,transferTime=?,resultCd=?, resultMsg=?, modDt=now() WHERE idx =?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			if(hyphenBean.getReplyCode().equals("0000")) {
				pstmt.setString(1, "Y");
			} else if(hyphenBean.getReplyCode().equals("XXXX")) {
				pstmt.setString(1, "X");
			} else {
				pstmt.setString(1,"N");
			}


//			pstmt.setString(2,headerBean.getTransactionTime().substring(0,8));
//			pstmt.setString(3,headerBean.getTransactionTime().substring(8,14));
			JSONParser jsonParser = new JSONParser();
			JSONObject resJson = (JSONObject) jsonParser.parse(hyphenBean.getResData());

			String balance = resJson.get("sign").toString() + resJson.get("balance").toString();
			String fee = resJson.get("svcCharge").toString();
			String transferTime = resJson.get("tradeTime").toString();

			pstmt.setLong(2,CommonUtil.parseLong(CommonUtil.parseLong(balance.trim())));
			pstmt.setLong(3,CommonUtil.parseLong(CommonUtil.parseLong(fee.trim())));
			pstmt.setString(4,transferTime);
			pstmt.setString(5, hyphenBean.getReplyCode());
			pstmt.setString(6,hyphenBean.getSuccessYn());
			pstmt.setLong(7,hyphenBean.getIndex());

			result = pstmt.executeUpdate();

			conn.commit();

		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean update(FBHeaderBean headerBean){
		FB0100100Bean fb0100100Bean = new FB0100100Bean(headerBean.getTransactionIndex());
		String query = "UPDATE PG_FIRM_TRX SET procGb =?, recvDate=?, recvTime=?, balance=?, fee=?,transferTime=?,resultCd=?, resultMsg=?, modDt=now() WHERE idx =?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			if(headerBean.getBankResponseCode().equals("0000")){
				pstmt.setString(1,"Y");
			}else if(headerBean.getBankResponseCode().equals("XXXX")){
				pstmt.setString(1,"X");
			}else{
				pstmt.setString(1,"N");
			}

			pstmt.setString(2,headerBean.getTransactionTime().substring(0,8));
			pstmt.setString(3,headerBean.getTransactionTime().substring(8,14));
			pstmt.setLong(4,CommonUtil.parseLong(fb0100100Bean.getRemainAmountSign()+fb0100100Bean.getRemainAmount()));
			pstmt.setLong(5,CommonUtil.parseLong(fb0100100Bean.getCommission()));
			pstmt.setString(6,fb0100100Bean.getTrasferTime());
			pstmt.setString(7,headerBean.getBankResponseCode());
			pstmt.setString(8,headerBean.getMessage());
			pstmt.setLong(9,headerBean.getIndex());

			result = pstmt.executeUpdate();

			conn.commit();

		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){

			if(headerBean.getBankResponseCode().equals("0000")){
				new FirmMasterDAO().insertBalance(headerBean.getNewBankCode(),fb0100100Bean.getMAccount(), fb0100100Bean.getRemainAmountSign()+fb0100100Bean.getRemainAmount());
			}

			if(headerBean.getProcType().startsWith("F")){
				//wallet(headerBean.getPrimaryKey(),headerBean.getBankResponseCode(),headerBean.getForeignKey());
			}

			return true;
		}else{
			return false;
		}
	}

	public boolean updateResultCheckbyHyphen(HyphenBean hyphenBean, String resCode, String resMsg) {
		TransferBean transferBean = (TransferBean) hyphenBean.getReqData(0);
		logger.debug("TRANSFER CHECK RESULT [{}][{}] ",transferBean.getOriSeqNo(), hyphenBean.getReplyCode());

		String query = "UPDATE PG_FIRM_TRX SET procGb =?, fee=?,transferTime=?, resultCd=?, resultMsg=?, MODDT=now() WHERE seqNo =?";

		DBManager db = null;
		PreparedStatement pstmt	= null;
		Connection conn	= null;
		int result = 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			if(transferBean.getResultCode().equals("0000")){
				if(hyphenBean.getReplyCode().equals("0000")){
					pstmt.setString(1,"Y");
				}else{
					pstmt.setString(1,"N");
				}
			}else{
				pstmt.setString(1,"X");
			}

			pstmt.setLong(2,CommonUtil.parseLong(transferBean.getSvcCharge()));
			pstmt.setString(3,transferBean.getTradeTime());
			pstmt.setString(4,resCode);
			pstmt.setString(5,resMsg);
			pstmt.setString(6,transferBean.getOriSeqNo());

			result = pstmt.executeUpdate();

			conn.commit();

		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			return true;
		}else{
			return false;
		}
	}


	public boolean updateResultCheck(FBHeaderBean headerBean, String resCode, String resMsg){
		FB0600101Bean fb06001001bean = new FB0600101Bean(headerBean.getTransactionIndex());
		logger.debug("TRANSFER CHECK RESULT [{}][{}] ",fb06001001bean.getRootSpecNumber(),fb06001001bean.getResultCd());

		String query = "UPDATE PG_FIRM_TRX SET procGb =?, fee=?,transferTime=?, resultCd=?, resultMsg=?, MODDT=now() WHERE seqNo =?";

		DBManager db = null;
		PreparedStatement pstmt	= null;
		Connection conn	= null;
		int result = 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			if(headerBean.getBankResponseCode().equals("0000")){
				if(fb06001001bean.getResultCd().equals("0000")){
					pstmt.setString(1,"Y");
				}else{
					pstmt.setString(1,"N");
				}
			}else{
				pstmt.setString(1,"X");
			}

			pstmt.setLong(2,CommonUtil.parseLong(fb06001001bean.getCommission()));
			pstmt.setString(3,fb06001001bean.getTransferTime());
			pstmt.setString(4,resCode);
			pstmt.setString(5,resMsg);
			pstmt.setString(6,fb06001001bean.getRootSpecNumber());

			result = pstmt.executeUpdate();

			conn.commit();

		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){

			if(headerBean.getProcType().startsWith("F") && headerBean.getBankResponseCode().equals("0000")){
				logger.debug("이체처리결과조회 반영 : {} ",headerBean.getProcType());
			}
			return true;
		}else{
			return false;
		}
	}

	public boolean updateError(long idx, String errorCode) {
		String query = "UPDATE PG_FIRM_TRX SET resultCd=?, procGb=?, modDt = now(),resultMsg=(SELECT concat('타행불능:',message) from PG_FIRM_CODE where bankcd='ERR' and code=?) WHERE idx =?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			pstmt.setString(1,errorCode);
			pstmt.setString(2,"N");
			pstmt.setString(3,errorCode);
			pstmt.setLong(4,idx);

			result = pstmt.executeUpdate();

			conn.commit();
		}catch(Exception e){
			logger.error("UPDATE RESULT ERROR : {}",CommonUtil.getExceptionMessage(e));
			logger.error("UPDATE PG_FIRM_TRX SET recultCd='{}',procGb='{}',modDt=now() WHERE idx ={};",errorCode,"N",idx);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			logger.info("log.day","타행이체 불능 지급이체 UPDATE 성공  IDX=["+idx+"]",this);
			return true;
		}else{
			logger.info("log.day","타행이체 불능 지급이체 UPDATE IDX=["+idx+"]",this);
			return false;
		}
	}

	public String getBankName(String bankCd){

		String query = " SELECT codeName FROM PG_CODE WHERE `alias` ='BANK' AND code = ? ";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String result	= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,bankCd);
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = rset.getString("codeName");
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	public long insertTrx(String bankCd,long amount,String recvBankCd,String recvAccount,String sender,String recordInfo,String procType, String seqNo){
		String query = "INSERT INTO PG_FIRM_TRX  (bankCd,sendDate,sendTime,seqNo,amount,recvBank,recvAccount,recordInfo,recvHolder,procType,filler ) values (?,DATE_FORMAT(now(),'%Y%m%d') , DATE_FORMAT(now(),'%H%i%s'),?,?,?,?,?,?,?,?)";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset 			= null;
		long result				= 0;
		String sendMemo			= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			if("RS".equals(procType)) {
				sendMemo = "실시간";
			}else if("VA".equals(procType)) {
				sendMemo = "대행";
			}else if("AS".equals(procType)) {
				sendMemo = "자동";
			}else if("CS".equals(procType)) {
				sendMemo = "충전";
			}else if("MT".equals(procType)) {
				sendMemo = "모계좌";
			}else if("WT".equals(procType)) {
				sendMemo = "월렛";
			}

			if("".equals(sender)) {
				sender = "(주)부국위너스";
			}

			pstmt.setString(1,bankCd);
			pstmt.setString(2, seqNo);
			pstmt.setLong(3,amount);
			pstmt.setString(4,recvBankCd);
			pstmt.setString(5,recvAccount);
			pstmt.setString(6,sendMemo);
			pstmt.setString(7,sender);
			pstmt.setString(8,procType);
			pstmt.setString(9,recordInfo);

			result = pstmt.executeUpdate();
			rset		= pstmt.executeQuery("SELECT LAST_INSERT_ID() ");
			while(rset.next()){
				result = rset.getLong(1);
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertTrx Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		return result;
	}

	public long insertTrx(String bankCd,long amount,String recvBankCd,String recvAccount,String sender,String recordInfo,String procType){
		String query = "INSERT INTO PG_FIRM_TRX  (bankCd,sendDate,sendTime,seqNo,amount,recvBank,recvAccount,recordInfo,recvHolder,procType,filler ) values (?,DATE_FORMAT(now(),'%Y%m%d') , DATE_FORMAT(now(),'%H%i%s'),FN_BANKSEQ(),?,?,?,?,?,?,?)";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset 			= null;
		long result				= 0;
		String sendMemo			= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			if("RS".equals(procType)) {
				sendMemo = "실시간";
			}else if("VA".equals(procType)) {
				sendMemo = "대행";
			}else if("AS".equals(procType)) {
				sendMemo = "자동";
			}else if("CS".equals(procType)) {
				sendMemo = "충전";
			}else if("MT".equals(procType)) {
				sendMemo = "모계좌";
			}else if("WT".equals(procType)) {
				sendMemo = "월렛";
			}

			if("".equals(sender)) {
				sender = "(주)부국위너스";
			}

			pstmt.setString(1,bankCd);
			pstmt.setLong(2,amount);
			pstmt.setString(3,recvBankCd);
			pstmt.setString(4,recvAccount);
			pstmt.setString(5,sendMemo);
			pstmt.setString(6,sender);
			pstmt.setString(7,procType);
			pstmt.setString(8,recordInfo);

			result = pstmt.executeUpdate();
			rset		= pstmt.executeQuery("SELECT LAST_INSERT_ID() ");
			while(rset.next()){
				result = rset.getLong(1);
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertTrx Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		return result;
	}


	public FirmBean checkResult(long idx,FirmBean firmBean){
		String query = " SELECT resultCd,resultMsg,recvHolder,balance,fee,transferTime FROM PG_FIRM_TRX WHERE idx =?  AND procGb in ('N','Y') ";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;


		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setLong(1, idx);
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				firmBean.resultCd = rset.getString("resultCd");
				firmBean.resultMsg = rset.getString("resultMsg");
				firmBean.idx  = idx;
				if(firmBean.data == null){
					firmBean.data = new SharedMap<String,Object>();
				}
				firmBean.data.put("recvHolder", rset.getString("recvHolder"));
				firmBean.data.put("balance", rset.getString("balance"));
				firmBean.data.put("fee", rset.getLong("fee"));
				firmBean.data.put("transferTime", rset.getString("transferTime"));

			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		return firmBean;
	}

	/**
	 * 펌 처리결과 확인 전문에서 필요한 전문번호 호출
	 * @return
	 */
	public String getBankSeq(){
		String query = "SELECT FN_BANKSEQ() as seq";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String result	= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = rset.getString("seq");
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 펌 처리결과 확인 전문에서 필요한 전문번호 호출
	 * @return
	 */
	public String getTranDate(String seqNo){
		String query = "SELECT sendDate from PG_FIRM_TRX where seqNo = ?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String result	= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,seqNo);
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = rset.getString("sendDate");
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 인덱스 번호를 통해 trxId를 조회한다.
	 * @param idx
	 * @return
	 */
	public String selectTrxId(long idx){
		String query = "SELECT recordInfo FROM PG_FIRM_TRX WHERE idx = ?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		String recordInfo = "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setLong (1, idx);
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				recordInfo = rset.getString("recordInfo");
			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		return recordInfo;
	}
}
