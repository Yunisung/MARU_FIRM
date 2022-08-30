package com.pgmate.firm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.hyphen.BalanceBean;
import com.pgmate.firm.hyphen.HolderBean;
import com.pgmate.firm.hyphen.HyphenBaseBean;
import com.pgmate.firm.hyphen.HyphenBean;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.lib.util.db.DBFactory;
import com.pgmate.lib.util.db.DBManager;
import com.pgmate.lib.util.gson.GsonUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import com.pgmate.lib.util.map.SharedMap;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 *
 */
public class FirmMasterDAO {
	private final static Logger logger = (Logger) LoggerFactory.getLogger(com.pgmate.firm.dao.FirmMasterDAO.class);
	private SharedMap<String,BankBean> map = null;
	
	public FirmMasterDAO(){
	}
	
	public FirmMasterDAO(SharedMap<String,BankBean> map) {
		this.map = map;
	}

	public long setMasterbyHyphen(String msgCd,String jobGb,String bankCd, String sendUrl,String reqData){
		String query = "INSERT INTO PG_FIRM_MASTER (bankCd,msgCd,jobGb,seqNo,sendDate,sendTime,procGb, sendUrl,reqData) "
				+" VALUES (?,?,?,FN_BANKSEQ(), DATE_FORMAT(now(), '%Y%m%d'), DATE_FORMAT(now(), '%H%i%s'),'R',?,?)";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset 			= null;
		long result				= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			pstmt.setString(1,bankCd);
			pstmt.setString(2,msgCd);
			pstmt.setString(3,jobGb);
			pstmt.setString(4,sendUrl);
			pstmt.setString(5,reqData);
			result = pstmt.executeUpdate();
			rset		= pstmt.executeQuery("SELECT LAST_INSERT_ID() ");
			while(rset.next()){
				result = rset.getLong(1);
			}
			conn.commit();

		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		return result;
	}
	
	public long setMaster(String msgCd,String jobGb,String bankCd,String reqData){
		String query = "INSERT INTO PG_FIRM_MASTER (bankCd,msgCd,jobGb,seqNo,sendDate,sendTime,procGb,reqData) "
				+" VALUES (?,?,?,FN_BANKSEQ(), DATE_FORMAT(now(), '%Y%m%d'), DATE_FORMAT(now(), '%H%i%s'),'R',?)";
		
		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset 			= null;
		long result				= 0;
		
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			
			pstmt.setString(1,bankCd);
			pstmt.setString(2,msgCd);
			pstmt.setString(3,jobGb);
			pstmt.setString(4,reqData);
			result = pstmt.executeUpdate();
			rset		= pstmt.executeQuery("SELECT LAST_INSERT_ID() ");
			while(rset.next()){
				result = rset.getLong(1);
			}
			conn.commit();
			
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		return result;
	}
	
	
	public FirmBean checkResult(long idx,FirmBean firmBean){
		String query = " SELECT resultCd,resultMsg,resData FROM PG_FIRM_MASTER WHERE idx =?  AND procGb in ('N','Y') ";
		
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
				firmBean.data.put("resData", rset.getString("resData"));	
			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{ 
			db.close(conn,pstmt,rset);
		}
		return firmBean;
	}
	
	
	public boolean insert0800100(String bankCd,String seqNo){
		
		String query = "INSERT INTO PG_FIRM_MASTER (bankCd,msgCd,jobGb,seqNo,sendDate,sendTime,procGb) "
				+" VALUES (?,'0800','100',?, DATE_FORMAT(now(), '%Y%m%d'), DATE_FORMAT(now(), '%H%i%s'),'R')";
		
		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			
			pstmt.setString(1,bankCd);
			pstmt.setString(2,seqNo);
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
	
	public boolean insert0800300(String bankCd,String seqNo){
		
		String query = "INSERT INTO PG_FIRM_MASTER (bankCd,msgCd,jobGb,seqNo,sendDate,sendTime,procGb) "
				+" VALUES (?,'0800','300',?, DATE_FORMAT(now(), '%Y%m%d'), DATE_FORMAT(now(), '%H%i%s'),'R')";
		
		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			
			pstmt.setString(1,bankCd);
			pstmt.setString(2,seqNo);
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

	/**
	 * PYS : �����¿� PG_FIRM_MASTER����
	 * @return
	 */
	public List<HyphenBean> selectbyHyphen() {
		String query = " SELECT idx,bankCd,msgCd,jobGb,seqNo,sendDate,sendTime,searchDate,searchNo,bankSeqNo,filler,sendUrl,reqData FROM PG_FIRM_MASTER WHERE sendDate = DATE_FORMAT(now(), '%Y%m%d') AND procGb='R' AND filler IS null ORDER BY idx ASC";

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
				BankBean configBean = map.get(rset.getString("bankCd"));

				if(configBean != null) {
					HyphenBean hyphenBean = new HyphenBean();
					hyphenBean.setIndex(rset.getLong("idx"));
					hyphenBean.setKscode(configBean.kscode);
					hyphenBean.setEkey(configBean.ekey);
					hyphenBean.setMsalt(configBean.msalt);
					hyphenBean.setSendurl(rset.getString("sendUrl"));
					
                    HyphenBaseBean baseBean = null;
                    if(rset.getString("sendUrl").equals("rfb/retail/inquiry/balance")) {
                        baseBean = new BalanceBean(rset.getString("seqNo"), configBean.account);
                        baseBean.setCompCode(configBean.compCd);
                        baseBean.setBankCode(configBean.bankCd);
                        
                        hyphenBean.setReqdata(baseBean);
                    } else if(rset.getString("sendUrl").equals("rfb/retail/account/accountname")) {
                    	String reqJson = rset.getString("reqData");
                    	
                    	JSONParser parser = new JSONParser();
                    	JSONObject jsonobj = (JSONObject) parser.parse(reqJson);
                    	String reqData = jsonobj.get("reqdata").toString();
                    	reqData = reqData.substring(1, reqData.length()-1);
                    	HolderBean holderBean = (HolderBean) GsonUtil.fromJson(reqData, HolderBean.class);
                    	holderBean.setSeqNo(rset.getString("seqNo"));
                    	
                    	hyphenBean.setReqdata(holderBean);
                    }
					list.add(hyphenBean);
				} else {
					logger.info("�ش������ڵ忡 �ش��ϴ� config���� �����ϴ�. : [{}]", rset.getString("bankCd"));
				}
			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		return list;
	}
	
	public List<FBHeaderBean> select(){
		
		String query = " SELECT idx,bankCd,msgCd,jobGb,seqNo,sendDate,sendTime,searchDate,searchNo,bankSeqNo,filler,reqData FROM PG_FIRM_MASTER WHERE sendDate = DATE_FORMAT(now(), '%Y%m%d') AND procGb='R' AND filler IS null ORDER BY idx ASC";
		
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
				headerBean.setNewBankCode(rset.getString("bankCd"));
				BankBean configBean = map.get(headerBean.getNewBankCode());
				
				if(configBean != null) {
					headerBean.setIdentificationCode(configBean.trCd);
					headerBean.setCompanyCode(configBean.compCd);
					
					headerBean.setSpecCode(rset.getString("msgCd"));
					headerBean.setClassificationCode(rset.getString("jobGb"));
					headerBean.setSpecNumber(rset.getString("seqNo"));
					headerBean.setTransactionTime(CommonUtil.getCurrentDate("yyyyMMddHHmmss"));
					headerBean.setFrequency("1");
					headerBean.setInquiryDay(CommonUtil.nToB(rset.getString("searchDate")));
					headerBean.setInqueryNumber(CommonUtil.nToB(rset.getString("searchNo")));
					headerBean.setExtra(CommonUtil.nToB(rset.getString("filler")));
					headerBean.setTransactionIndex(CommonUtil.byteFiller(CommonUtil.nToB(rset.getString("reqData")), 200)); // 200?
					list.add(headerBean);
				}					
			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		return list;
	}
	
	public boolean updateStatus(long idx,String status){
		
		String query = "UPDATE PG_FIRM_MASTER SET procGb =? WHERE idx =?";
		
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
			logger.info("UPDATE PG_FIRM_MASTER SET procGb='{}' WHERE idx ={};",status,idx);
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
		String query = "UPDATE PG_FIRM_MASTER SET procGb =? , recvDate=DATE_FORMAT(now(), '%Y%m%d'), recvTime=DATE_FORMAT(now(), '%H%i%s'), resultCd=?, resultMsg=?, resData=? , modDt = now() WHERE idx =?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			if(hyphenBean.getReplyCode().equals("0000")){
				pstmt.setString(1,"Y");
			}else if(hyphenBean.getReplyCode().equals("XXXX")){
				pstmt.setString(1,"X");
			}else{
				pstmt.setString(1,"N");
			}

//			pstmt.setString(2,headerBean.getTransactionTime().substring(0,8));
//			pstmt.setString(3,headerBean.getTransactionTime().substring(8,14));
			pstmt.setString(2,hyphenBean.getReplyCode());
			pstmt.setString(3,hyphenBean.getSuccessYn());
			pstmt.setString(4,hyphenBean.getResData());
			pstmt.setLong(5,hyphenBean.getIndex());

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
		
		String query = "UPDATE PG_FIRM_MASTER SET procGb =? , recvDate=?, recvTime=?, resultCd=?, resultMsg=?, resData=? , modDt = now() WHERE idx =?";
		
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
			pstmt.setString(4,headerBean.getBankResponseCode());
			pstmt.setString(5,headerBean.getMessage());
			pstmt.setString(6,headerBean.getTransactionIndex());
			pstmt.setLong(7,headerBean.getIndex());
			
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
	
	public boolean insert(FBHeaderBean headerBean,String reqData){
		String query = "INSERT INTO PG_FIRM_MASTER (bankCd,msgCd,JobGb,seqNo,sendDate,sendTime,recvDate,recvTime,resultCd,"
				+" resultMsg,searchDate,searchNo,bankSeqNo,filler,reqData,resData,procGb) "
				+" VALUES (?,?,?,?,?,?,DATE_FORMAT(now(), '%Y%m%d'), DATE_FORMAT(now(), '%H%i%s'),?,?,?,?,?,?,?,?,?)";
		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			
			int idx = 1;
			pstmt.setString(idx++,headerBean.getNewBankCode());
			pstmt.setString(idx++,headerBean.getSpecCode());
			pstmt.setString(idx++,headerBean.getClassificationCode());
			pstmt.setString(idx++,headerBean.getSpecNumber());
			pstmt.setString(idx++,headerBean.getTransactionTime().substring(0,8));
			pstmt.setString(idx++,headerBean.getTransactionTime().substring(8,14));
			
			pstmt.setString(idx++,headerBean.getBankResponseCode());
			pstmt.setString(idx++,headerBean.getMessage());
			pstmt.setString(idx++,headerBean.getInquiryDay());
			pstmt.setString(idx++,headerBean.getInqueryNumber());
			pstmt.setString(idx++,headerBean.getBankSpecNumber());
			pstmt.setString(idx++,headerBean.getExtra());
			pstmt.setString(idx++,reqData);
			pstmt.setString(idx++,headerBean.getTransactionIndex());
			if(headerBean.getBankResponseCode().equals("0000")){
				pstmt.setString(idx++,"Y");
			}else if(headerBean.getBankResponseCode().equals("XXXX")){
				pstmt.setString(idx++,"X");
			}else{
				pstmt.setString(idx++,"N");
			}
			
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
	
	public boolean insertBalance(String bankCd,String accntNo,String balance){
		String query = "INSERT INTO PG_FIRM_BLC (bankCd,account, REGDAY, REGDATE, AMOUNT)"
				+" VALUES (?,?, DATE_FORMAT(now(), '%Y%m%d'), NOW(), ?)";
		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			
			
			int idx = 1;
			pstmt.setString(idx++,bankCd);
			pstmt.setString(idx++,accntNo);
			pstmt.setLong(idx++,CommonUtil.parseLong(balance));
			
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
	
	public boolean insertAccnt(String bankCd,String account,String accntHolder){
		String query = "INSERT INTO PG_FIRM_ACCNT (bankCd,account,accntHolder,accntYn,regId,regDay)"
				+" VALUES (?,?,?,?,'SYSTEM',?)";
		
		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			
			int idx = 1;
			pstmt.setString(idx++, bankCd);
			pstmt.setString(idx++, account);
			pstmt.setString(idx++, accntHolder);
			pstmt.setString(idx++, "Ȯ��");
			pstmt.setString(idx++, CommonUtil.getCurrentDate("yyyyMMdd"));
			
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
	
	public String selectAccnt(String bankCd,String accntNo){
		
		String query = " SELECT accntHolder FROM PG_FIRM_ACCNT WHERE bankCd = ? AND account = ? AND accntYn='Ȯ��' and regDay > DATE_FORMAT(NOW()- INTERVAL 3 MONTH,'%Y%m%d')";
		
		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		
		String holder = "";
		
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			
			pstmt.setString(1, bankCd);
			pstmt.setString(2, accntNo);
			
			rset 	= pstmt.executeQuery();
			
			while(rset.next()){
				holder = CommonUtil.nToB(rset.getString("accntHolder"));
			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		return holder;
	}
	
	
	

}
