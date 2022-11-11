package com.pgmate.firm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.ksnet.FB0200300Bean;
import com.pgmate.firm.ksnet.FB0900100Bean;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.lib.util.db.DBFactory;
import com.pgmate.lib.util.db.DBManager;
import com.pgmate.lib.util.lang.CommonUtil;
import com.pgmate.lib.util.map.SharedMap;

public class VactDAO{

	private static Logger logger = LoggerFactory.getLogger( com.pgmate.firm.dao.VactDAO.class );

	public VactDAO() {
	}

	public synchronized static String getVactId(){
		String returnVal = "";
		String query 	 = "SELECT FN_NEXTVAL2('VACT') as val";

		DBManager db 			= null;
		PreparedStatement pstmt = null;
		Connection 	conn		= null;
		ResultSet rset			= null;

		try {
			db 			= DBFactory.getInstance();
			conn		= db.getConnection();
			pstmt		= conn.prepareStatement(query);
			rset		= pstmt.executeQuery();

			while(rset.next()){
				returnVal = "V"+rset.getString("val");
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getVactId ERROR : {}, query : {}", e.getMessage(), query);
		}finally {
			db.close(conn, pstmt, rset);
		}
		return returnVal;
	}

	public boolean insertVactIO(FBHeaderBean header,FB0900100Bean fbBean, String resData,String resultCd,String resultMsg){
		String query = "INSERT INTO PG_VACT_IO (msgCd,seqNo,recvDay,recvTime,bankCd,maccount,trxType,account,reqData,resData,sendDay,sendTime,resultCd,resultMsg,regDay	)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result				= 0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			int idx = 1;
			pstmt.setString(idx++, header.getSpecCode());
			pstmt.setString(idx++, header.getSpecNumber());
			pstmt.setString(idx++, header.getTransactionTime().substring(0,8));
			pstmt.setString(idx++, header.getTransactionTime().substring(8,14));
			pstmt.setString(idx++, header.getNewBankCode());

			if(fbBean == null){
				pstmt.setString(idx++, "");
				pstmt.setString(idx++, "");
				pstmt.setString(idx++, "");
			}else{
				pstmt.setString(idx++, "");
				pstmt.setString(idx++, fbBean.getClassificationCode());
				pstmt.setString(idx++, fbBean.getVirtualAccount());
			}
			pstmt.setString(idx++, header.getTransactionIndex());
			pstmt.setString(idx++, new String(resData));
			pstmt.setString(idx++, CommonUtil.getCurrentDate("yyyyMMdd"));
			pstmt.setString(idx++, CommonUtil.getCurrentDate("HHmmss"));
			pstmt.setString(idx++, resultCd);
			pstmt.setString(idx++, resultMsg);
			pstmt.setString(idx++, CommonUtil.getCurrentDate("yyyyMMdd"));

			result = pstmt.executeUpdate();

			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertVactIO ERROR : {}, query : {}", e.getMessage(), query);
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

	public boolean insertVactTrx(SharedMap<String,Object> trxMap){
		String query = "INSERT INTO PG_VACT_TRX ( vactId , issueId , mchtId , bankCd , account , seqNo , vactType , amount , sender , trxType , rootVactId , trxDay , trxTime  ,"
				+" stlType , stlDay  , stlId, stlFee , stlFeeVat , stlDistType , stlDistDay  , stlDistId  , stlDistFee , stlDistFeeVat , stlAgencyType , stlAgencyDay  , stlAgencyId  , stlAgencyFee , stlAgencyFeeVat , stlSalesType , stlSalesDay  ,  stlSalesId  , stlSalesFee , stlSalesFeeVat , vanFee , vanDay , hookType , hookAddr ,trackId , udf1 , udf2 , regId , regDay)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result				= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			int idx = 1;

			pstmt.setString(idx++, trxMap.getString("vactId"));
			pstmt.setString(idx++, trxMap.getString("issueId"));
			pstmt.setString(idx++, trxMap.getString("mchtId"));
			pstmt.setString(idx++, trxMap.getString("bankCd"));
			pstmt.setString(idx++, trxMap.getString("account"));
			pstmt.setString(idx++, trxMap.getString("seqNo"));
			pstmt.setString(idx++, trxMap.getString("vactType"));
			pstmt.setLong(idx++	, trxMap.getLong("amount"));
			pstmt.setString(idx++, trxMap.getString("sender"));
			pstmt.setString(idx++, trxMap.getString("trxType"));
			pstmt.setString(idx++, trxMap.getString("rootVactId"));
			pstmt.setString(idx++, trxMap.getString("trxDay"));
			pstmt.setString(idx++, trxMap.getString("trxTime"));
			pstmt.setString(idx++, trxMap.getString("stlType"));
			pstmt.setString(idx++, trxMap.getString("stlDay"));
			pstmt.setString(idx++, "");
			pstmt.setLong(idx++, trxMap.getLong("stlFee"));
			pstmt.setLong(idx++, trxMap.getLong("stlFeeVat"));
			pstmt.setString(idx++, trxMap.getString("stlDistType"));
			pstmt.setString(idx++, trxMap.getString("stlDistDay"));
			pstmt.setString(idx++, "");
			pstmt.setLong(idx++, trxMap.getLong("stlDistFee"));
			pstmt.setLong(idx++, trxMap.getLong("stlDistFeeVat"));
			pstmt.setString(idx++, trxMap.getString("stlAgencyType"));
			pstmt.setString(idx++, trxMap.getString("stlAgencyDay"));
			pstmt.setString(idx++, "");
			pstmt.setLong(idx++, trxMap.getLong("stlAgencyFee"));
			pstmt.setLong(idx++, trxMap.getLong("stlAgencyFeeVat"));
			pstmt.setString(idx++, trxMap.getString("stlSalesType"));
			pstmt.setString(idx++, trxMap.getString("stlSalesDay"));
			pstmt.setString(idx++, "");
			pstmt.setLong(idx++, trxMap.getLong("stlSalesFee"));
			pstmt.setLong(idx++, trxMap.getLong("stlSalesFeeVat"));
			pstmt.setLong(idx++, trxMap.getLong("vanFee"));
			pstmt.setString(idx++, trxMap.getString("vanDay"));
			pstmt.setString(idx++, trxMap.getString("hookType"));
			pstmt.setString(idx++, trxMap.getString("hookAddr"));
			pstmt.setString(idx++, trxMap.getString("trackId"));
			pstmt.setString(idx++, trxMap.getString("udf1"));
			pstmt.setString(idx++, trxMap.getString("udf2"));
			pstmt.setString(idx++, "KSNET");

			pstmt.setString(idx++, CommonUtil.getCurrentDate("yyyyMMdd"));

			result = pstmt.executeUpdate();

			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertVactTrx ERROR : {}, query : {}", e.getMessage(), query);
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

	public SharedMap<String,Object> getAccount(String account,String trxType){
		SharedMap<String,Object> result = null;

		String query = " SELECT A.*,B.issuerBank,B.bankCd,B.trxFee,B.issueDay,B.companyCd,B.pisp FROM PG_VACT_DTL A LEFT JOIN PG_VACT B ON A.account = B.account WHERE A.account =? ";

		if(!trxType.equals("40")){
			query = query+" AND status =?";
		}

		query = query+" order by A.issueId desc limit 1";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,account);
			if(!trxType.equals("40")){
				pstmt.setString(2,"발행");
			}else{

			}
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();
				result.put("issueId",rset.getString("issueId"));
				result.put("account",rset.getString("account"));
				result.put("vactType",rset.getString("vactType"));
				result.put("status",rset.getString("status"));
				result.put("mchtId",rset.getString("mchtId"));
				result.put("holderName",rset.getString("holderName"));
				result.put("amount",rset.getLong("amount"));
				result.put("oper",rset.getString("oper"));
				result.put("trackId",rset.getString("trackId"));
				result.put("expireAt",rset.getString("expireAt"));
				Timestamp expireDate = rset.getTimestamp("expireDate");

				if(expireDate != null) {
					result.put("expireDate",expireDate);
				}
				result.put("udf1",rset.getString("udf1"));
				result.put("udf2",rset.getString("udf2"));
				result.put("issuerBank",rset.getString("issuerBank"));
				result.put("trxFee",rset.getInt("trxFee"));
				result.put("issueDay",rset.getString("issueDay"));
				result.put("companyCd",rset.getString("companyCd"));
				result.put("pisp",rset.getString("pisp"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getAccount ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	public SharedMap<String,Object> getAccountDtl(String account){
		SharedMap<String,Object> result = null;

		String query = " SELECT A.*,B.issuerBank,B.bankCd,B.trxFee,B.issueDay,B.companyCd,B.pisp FROM PG_VACT_DTL A LEFT JOIN PG_VACT B ON A.account = B.account WHERE A.account =? order by A.issueId desc limit 1";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,account);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();
				result.put("issueId",rset.getString("issueId"));
				result.put("account",rset.getString("account"));
				result.put("vactType",rset.getString("vactType"));
				result.put("status",rset.getString("status"));
				result.put("mchtId",rset.getString("mchtId"));
				result.put("holderName",rset.getString("holderName"));
				result.put("amount",rset.getLong("amount"));
				result.put("oper",rset.getString("oper"));
				result.put("trackId",rset.getString("trackId"));
				result.put("expireAt",rset.getString("expireAt"));
				Timestamp expireDate = rset.getTimestamp("expireDate");
				if(expireDate != null) {
					result.put("expireDate",expireDate);
				}
				result.put("udf1",rset.getString("udf1"));
				result.put("udf2",rset.getString("udf2"));
				result.put("issuerBank",rset.getString("issuerBank"));
				result.put("trxFee",rset.getInt("trxFee"));
				result.put("issueDay",rset.getString("issueDay"));
				result.put("companyCd",rset.getString("companyCd"));
				result.put("pisp",rset.getString("pisp"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getAccountDtl ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 가상계좌 입금취소에 대한 원거래건 정보 조회
	 * @param trxDay
	 * @param account
	 * @param bankCd
	 * @param seqNo
	 * @return
	 */
	public SharedMap<String,Object> getHisTrxMap(String trxDay , String account, String seqNo ){
		SharedMap<String,Object> result = null;

		String query = " SELECT * FROM PG_VACT_TRX WHERE trxDay =? and account =? and seqNo = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxDay);
			pstmt.setString(2,account);
			pstmt.setString(3,seqNo);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();
				result.put("vactId",rset.getString("vactId"));
				result.put("issueId",rset.getString("issueId"));
				result.put("mchtId",rset.getString("mchtId"));
				result.put("account",rset.getString("account"));
				result.put("seqNo",rset.getString("seqNo"));
				result.put("vactType",rset.getString("vactType"));
				result.put("amount",rset.getLong("amount"));
				result.put("sender",rset.getString("sender"));
				result.put("stlAmount",rset.getString("stlAmount"));
				result.put("stlType",rset.getString("stlType"));
				result.put("stlFee",rset.getLong("stlFee"));
				result.put("stlFeeVat",rset.getLong("stlFeeVat"));
				result.put("stlDistType",rset.getString("stlDistType"));
				result.put("stlDistFee",rset.getLong("stlDistFee"));
				result.put("stlDistFeeVat",rset.getLong("stlDistFeeVat"));
				result.put("stlAgencyType",rset.getString("stlAgencyType"));
				result.put("stlAgencyFee",rset.getLong("stlAgencyFee"));
				result.put("stlAgencyFeeVat",rset.getLong("stlAgencyFeeVat"));
				result.put("stlSalesType",rset.getString("stlSalesType"));
				result.put("stlSalesFee",rset.getLong("stlSalesFee"));
				result.put("stlSalesFeeVat",rset.getLong("stlSalesFeeVat"));
				result.put("vanFee",rset.getLong("vanFee"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getHisTrxMap ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * PG_MCHT_MNG_VACT테이블 가맹점 가상계좌 관리정보 조회
	 * @param mchtId
	 * @return
	 */
	public SharedMap<String,Object> getMchtMngVact(String mchtId){
		SharedMap<String,Object> result = null;

		String query = " SELECT * FROM PG_MCHT_MNG_VACT WHERE mchtId =?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,mchtId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();

				result.put("mchtId",rset.getString("mchtId"));
				result.put("holderName",rset.getString("holderName"));
				result.put("status",rset.getString("status"));
				result.put("issueType",rset.getString("issueType"));
				result.put("holderName",rset.getString("holderName"));
				result.put("expireSet",rset.getInt("expireSet"));
				result.put("quantity",rset.getInt("quantity"));
				result.put("startDay",rset.getString("startDay"));
				result.put("settleType",rset.getString("settleType"));
				result.put("fee",rset.getLong("fee"));
				result.put("distSettleType",rset.getString("distSettleType"));
				result.put("distFee",rset.getLong("distFee"));
				result.put("agencySettleType",rset.getString("agencySettleType"));
				result.put("agencyFee",rset.getLong("agencyFee"));
				result.put("salesSettleType",rset.getString("salesSettleType"));
				result.put("salesFee",rset.getLong("salesFee"));
				result.put("hookType",rset.getString("hookType"));
				result.put("hookAddr",rset.getString("hookAddr"));
				result.put("feeType",rset.getString("feeType"));
				result.put("rate",rset.getString("rate"));
				result.put("distRate",rset.getString("distRate"));
				result.put("agencyRate",rset.getString("agencyRate"));
				result.put("salesRate",rset.getString("salesRate"));
				result.put("transferInterval",rset.getString("transferInterval"));
				result.put("payOutFee",rset.getLong("payOutFee"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getMchtMngVact ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	public String getSettleDay(String today,int term) {
		String start = CommonUtil.toString(term-1);
		String q = "SELECT days FROM PG_CODE_HOLIDAY WHERE days > '"+today+"' AND status ='no' limit "+start+",1";
		return getQuery(q);
	}

	public String getSettleDay(String today) {
		String q = "SELECT days FROM PG_CODE_HOLIDAY WHERE days >= '"+today+"' AND status ='no' limit 1";
		return getQuery(q);
	}

	public String getVanDay(String today) {
		String q = "SELECT days FROM PG_CODE_HOLIDAY WHERE days > '"+today+"' AND status ='no' limit 0,1";
		return getQuery(q);
	}

	private String getQuery(String query){
		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String result 			= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = rset.getString("days");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getQuery ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		return result;
	}

	public boolean vactDtlSetExpired(String issueId,String vactId){
		String query = "UPDATE PG_VACT_DTL set status ='사용만료' , reason= ? ,expireDate = now() WHERE issueId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result				= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1, vactId);
			pstmt.setString(2, issueId);
			result = pstmt.executeUpdate();

			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("vactDtlSetExpired ERROR : {}, query : {}", e.getMessage(), query);
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

	public boolean vactDtlSetIssue(String issueId,String vactId){
		String query = "UPDATE PG_VACT_DTL set status ='발행' , reason= ? ,expireDate = null WHERE issueId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result				= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1, vactId);
			pstmt.setString(2, issueId);
			result = pstmt.executeUpdate();

			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("vactDtlSetIssue ERROR : {}, query : {}", e.getMessage(), query);
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

	public boolean updateVactTrx(String vactId , String hookStatus,String hookResponse){
		int result = 0;
		String query = "UPDATE PG_VACT_TRX set hookStatus = ? , hookResponse = ? , hookSentDate = now() WHERE vactId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,hookStatus);
			pstmt.setString(2,hookResponse);
			pstmt.setString(3,vactId);
			result = pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("updateVactTrx ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}
		if(result > 0){
			return true;
		}else{
			return false;
		}
	}

	public String isDuplicatedTrx(FBHeaderBean header,FB0200300Bean fbBean){
		String result = "";
		String query = " SELECT vactId FROM PG_VACT_TRX WHERE trxDay =? and account =? and seqNo = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,header.getTransactionTime().substring(0, 8));
			pstmt.setString(2,fbBean.getVirtualAccount());
			pstmt.setString(3,header.getSpecNumber());

			logger.info("isDuplicatedTrx : {},{},{}",header.getSpecNumber(), header.getTransactionTime().substring(0, 8),fbBean.getVirtualAccount());
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = rset.getString("vactId");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("isDuplicatedTrx ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 타행이체 불능 관련 실시간 출금 결과 업데이트
	 * @param trxId : 거래번호
	 * @return
	 */
	public boolean updateRealTimePayOutRes(String trxId, String errCd){
		String query = "UPDATE PG_REALTIME_PAYOUT "
				+ "    SET resultCd=?, resultMsg=(SELECT concat('타행불능:',message) from PG_FIRM_CODE where bankcd='ERR' and code=?), sendCheck='N', sendCnt='3'"
				+ "	 WHERE trxid = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result				= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,errCd);
			pstmt.setString(2,errCd);
			pstmt.setString(3,trxId);

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("updateRealTimePayOutRes ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			logger.info("타행이체 불능 관련 실시간 출금 결과 UPDATE 성공 IDX=["+trxId+"]",this);
			return true;
		}else{
			logger.info("타행이체 불능 관련 실시간 출금 결과 UPDATE 실패 IDX=["+trxId+"]",this);
			return false;
		}
	}

	/**
	 * 타행이체 불능 관련 실시간 출금 매입 상태 정산보류로 업데이트
	 * @param trxId : 거래번호
	 * @return
	 */
	public boolean updatePayOutCancelCapUpdate(String trxId){
		String query = "UPDATE PG_TRX_CAP_DTL "
				+ "    SET stlStatus = '정산보류'"
				+ "	 WHERE capId = "
				+ "		(SELECT b.capId "
				+ "		   FROM PG_TRX_CAP a inner join PG_TRX_CAP_DTL b on a.capId = b.capId"
				+ "		  WHERE a.trxId = ?"
				+ "		)";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result				= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxId);

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("updatePayOutCancelCapUpdate ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			logger.info("타행이체 불능 관련 실시간 출금 매입데이터 UPDATE 성공 IDX=["+trxId+"]",this);
			return true;
		}else{
			logger.info("타행이체 불능 관련 실시간 출금 매입데이터 UPDATE 실패 IDX=["+trxId+"]",this);
			return false;
		}
	}

	/**
	 * 가상계좌 충전 출금대상거래에서 출금하지 않은 데이터중에 전송시도가 남은 거래건들 조회
	 * @return
	 */
	public ResultSet getVaPay(String trxId){
		String query = "SELECT * "
				+"	  FROM VA_TRX "
				+"   WHERE trxId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		ResultSet result 		= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = rset;
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getVaPay ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 가상계좌 충전 출금 결과 업데이트
	 * @param trxId : 거래번호
	 * @return
	 */
	public boolean updateVaPayOutRes(String trxId, String errCd){
		String query = "UPDATE VA_TRX_FIRM "
				+ "    SET resultCd=?, resultMsg=(SELECT concat('타행불능:',message) from PG_FIRM_CODE where bankcd='ERR' and code=?), retry='3', status='실패'"
				+ "	 WHERE trxid = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result				= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,errCd);
			pstmt.setString(2,errCd);
			pstmt.setString(3,trxId);

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("updateVaPayOutRes ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			logger.info("타행이체 불능관련 가상계좌 대행서비스 출금 결과 UPDATE 성공 IDX=["+trxId+"]",this);
			return true;
		}else{
			logger.info("타행이체 불능관련 가상계좌 대행서비스 출금 결과 UPDATE 실패 IDX=["+trxId+"]",this);
			return false;
		}
	}

	/**
	 * VA_PTN 데이터 조회
	 * @param ptnId
	 * @return
	 */
	public long getPtnWithdrawFee(String ptnId){
		String query = "SELECT withdrawFee "
				+"	  FROM VA_PTN "
				+"   WHERE ptnId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		long netFee 			= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,ptnId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				netFee = rset.getLong("withdrawFee");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getPtnWithdrawFee ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return netFee;
	}

	/**
	 * VA_TRX_ERR에 최종 실패 건 추가
	 * @param trxMap
	 * @return
	 */
	public boolean insertTrxErr(SharedMap<String, Object> trxMap) {
		int result = 0;
		String query = "INSERT INTO VA_TRX_ERR  (`trxId`,`id`,`ptnId`,`userId`,`trxType`,`trxUnit`,`trxDay`,`trxTime`,`amount`,`feeType`,`feeRate`,`fee`,`feeVat`,`ptnFeeRate`,`ptnFee`,`ptnFeeVat`,`bankFee`,`stlAmount`,`balance`,`trackId`,`refId`,`bankCd`,`account`,`holder`,`resultCd`,`resultMsg`,`regDay`) VALUES "
				+" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxMap.getString("trxId"));
			pstmt.setString(2,trxMap.getString("id"));
			pstmt.setString(3,trxMap.getString("ptnId"));
			pstmt.setString(4,trxMap.getString("userId"));
			pstmt.setString(5,trxMap.getString("trxType"));
			pstmt.setString(6,trxMap.getString("trxUnit"));
			pstmt.setString(7,trxMap.getString("trxDay"));
			pstmt.setString(8,trxMap.getString("trxTime"));
			pstmt.setLong(9,trxMap.getLong("amount"));
			pstmt.setString(10,trxMap.getString("feeType"));
			pstmt.setDouble(11,trxMap.getDouble("feeRate"));
			pstmt.setLong(12,trxMap.getLong("fee"));
			pstmt.setLong(13,trxMap.getLong("feeVat"));
			pstmt.setDouble(14,trxMap.getDouble("ptnFeeRate"));
			pstmt.setLong(15,trxMap.getLong("ptnFee"));
			pstmt.setLong(16,trxMap.getLong("ptnFeeVat"));
			pstmt.setLong(17,trxMap.getLong("bankFee"));
			pstmt.setLong(18,trxMap.getLong("stlAmount"));
			pstmt.setLong(19,trxMap.getLong("balance"));
			pstmt.setString(20,trxMap.getString("trackId"));
			pstmt.setString(21,trxMap.getString("refId"));
			pstmt.setString(22,trxMap.getString("bankCd"));
			pstmt.setString(23,trxMap.getString("account"));
			pstmt.setString(24,trxMap.getString("holder"));
			pstmt.setString(25,trxMap.getString("resultCd"));
			pstmt.setString(26,trxMap.getString("resultMsg"));
			pstmt.setInt(27,trxMap.getInt("regDay"));

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertTrxErr ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		if(result > 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * PG_CHARGE_SETTLE_ERR에 최종 실패 건 추가
	 * @param trxMap
	 * @return
	 */
	public boolean insertChargeErr(SharedMap<String, Object> trxMap) {
		int result = 0;
		String query = "INSERT INTO PG_CHARGE_SETTLE_ERR  (`trxId`,`mchtId`,`trxType`,`trxUnit`,`trxDay`,`trxTime`,`amount`,`fee`,`feeVat`,`bankFee`,`netAmount`,`balance`,`trackId`,`refId`,`bankCd`,'bankName',`account`,`holder`,'recordInfo','summary',`resultCd`,`resultMsg`,'regId',`regDay`) VALUES "
				+" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxMap.getString("trxId"));
			pstmt.setString(2,trxMap.getString("mchtId"));
			pstmt.setString(3,trxMap.getString("trxType"));
			pstmt.setString(4,trxMap.getString("trxUnit"));
			pstmt.setString(5,trxMap.getString("trxDay"));
			pstmt.setString(6,trxMap.getString("trxTime"));
			pstmt.setLong(7,trxMap.getLong("amount"));
			pstmt.setLong(8,trxMap.getLong("fee"));
			pstmt.setLong(9,trxMap.getLong("feeVat"));
			pstmt.setLong(10,trxMap.getLong("bankFee"));
			pstmt.setLong(11,trxMap.getLong("netAmount"));
			pstmt.setLong(12,trxMap.getLong("balance"));
			pstmt.setString(13,trxMap.getString("trackId"));
			pstmt.setString(14,trxMap.getString("refId"));
			pstmt.setString(15,trxMap.getString("bankCd"));
			pstmt.setString(16,trxMap.getString("bankName"));
			pstmt.setString(17,trxMap.getString("account"));
			pstmt.setString(18,trxMap.getString("holder"));
			pstmt.setString(19,trxMap.getString("recordInfo"));
			pstmt.setString(20,trxMap.getString("summary"));
			pstmt.setString(21,trxMap.getString("resultCd"));
			pstmt.setString(22,trxMap.getString("resultMsg"));
			pstmt.setString(23,trxMap.getString("regId"));
			pstmt.setInt(24,trxMap.getInt("regDay"));

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertChargeErr ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		if(result > 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 취소건의 실출금액 조회
	 * @param trxId
	 * @return
	 */
	public long getStlAmt(String trxId){
		String query = "SELECT stlAmount "
				+"	  FROM VA_TRX "
				+"   WHERE trxId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		long stlAmount 			= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				stlAmount = rset.getLong("stlAmount");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getStlAmt ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return stlAmount;
	}

	/**
	 * 출금 실패한 거래 건의 출금예정액만큼 이후거래건들 잔액 추가
	 * @param trxId
	 * @param id
	 * @param stlAmount
	 * @return
	 */
	public boolean updateVaBalance(String trxId, String id, long stlAmount){
		String query = "UPDATE VA_TRX "
				+ "    SET balance = balance + ? "
				+ "  WHERE trxId in "
				+ "  ("
				+ "		SELECT trxId "
				+ "   	  FROM VA_TRX"
				+ "		  WHERE regDate > "
				+ "			("
				+ "		 		SELECT regDate "
				+ "		   	  	  FROM VA_TRX "
				+ "		     	 WHERE trxId = ?"
				+ "			) and id = ?"
				+ "	 )";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result = 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setLong(1,stlAmount);
			pstmt.setString(2,trxId);
			pstmt.setString(3,id);

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("updateVaBalance ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			logger.info("타행이체 불능관련 가상계좌 대행서비스 잔액원복 UPDATE 성공 IDX=["+trxId+"]",this);
			return true;
		}else{
			logger.info("타행이체 불능관련 가상계좌 대행서비스 잔액원복 UPDATE 실패 IDX=["+trxId+"]",this);
			return false;
		}
	}

	/**
	 * 출금 실패한 거래건 VA_TRX테이블 데이터 삭제
	 * @param trxId
	 */
	public boolean deleteVaTrx(String trxId){
		String query = " DELETE FROM VA_TRX WHERE trxId =?";
		int result = 0;

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxId);
			result 	= pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("deleteVaTrx ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 해당 계좌가 가상계좌 대행서비스 계좌인지 조회한다.
	 * @param account
	 * @return
	 */
	public boolean vactCheck(String account){
		String query = "select * from VA_PTN_VACCNT where account = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result				=0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1, account);
			result 	= pstmt.executeUpdate();

			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("vactCheck ERROR : {}, query : {}", e.getMessage(), query);
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
	 * 충전정산 출금 결과 업데이트
	 * @param trxId : 거래번호
	 * @return
	 */
	public boolean updateChargeSettleRes(String trxId, String errCd){
		String query = "UPDATE PG_CHARGE_SETTLE_FIRM "
				+ "    SET resultCd=?, resultMsg=(SELECT concat('타행불능:',message) from PG_FIRM_CODE where bankcd='ERR' and code=?), retry='3', status='실패'"
				+ "	 WHERE trxid = ?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,errCd);
			pstmt.setString(2,errCd);
			pstmt.setString(3,trxId);

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("updateChargeSettleRes ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			logger.info("타행이체 불능관련 충전정산 서비스 출금 결과 UPDATE 성공 IDX=["+trxId+"]",this);
			return true;
		}else{
			logger.info("타행이체 불능관련 충전정산 서비스 출금 결과 UPDATE 실패 IDX=["+trxId+"]",this);
			return false;
		}
	}

	/**
	 * 충전정산 원거래 데이터
	 * @return
	 */
	public ResultSet getChargeSettle(String trxId){
		String query = "SELECT * "
				+"	  FROM PG_CHARGE_SETTLE_FIRM "
				+"   WHERE trxId = ?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		ResultSet result 		= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = rset;
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getChargeSettle ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 충전정산 망취소 원거래 데이터
	 * @return
	 */
	public ResultSet getCancelChargeSettle(String trxId){
		String query = "SELECT * "
				+"	  FROM PG_CHARGE_SETTLE "
				+"   WHERE trxId = ?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		ResultSet result 		= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = rset;
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getCancelChargeSettle ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 충전정산 출금 실패한 거래 건의 출금예정액만큼 이후거래건들 잔액 추가
	 * @param trxId
	 * @param mchtId
	 * @param netAmount
	 * @return
	 */
	public boolean updateChargeSettleBalance(String trxId, String mchtId, long netAmount){
		String query = "UPDATE PG_CHARGE_SETTLE "
				+ "    SET balance = balance + ? "
				+ "  WHERE trxId in "
				+ "  ("
				+ "		SELECT trxId "
				+ "   	  FROM PG_CHARGE_SETTLE"
				+ "		  WHERE regDate > "
				+ "			("
				+ "		 		SELECT regDate "
				+ "		   	  	  FROM PG_CHARGE_SETTLE "
				+ "		     	 WHERE trxId = ?"
				+ "			) and mchtId = ?"
				+ "	 )";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result = 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setLong(1,netAmount);
			pstmt.setString(2,trxId);
			pstmt.setString(3,mchtId);

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("updateChargeSettleBalance ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0){
			logger.info("타행이체 불능관련 충전정산 서비스 잔액원복 UPDATE 성공 IDX=["+trxId+"]",this);
			return true;
		}else{
			logger.info("타행이체 불능관련 충전정산 서비스 잔액원복 UPDATE 실패 IDX=["+trxId+"]",this);
			return false;
		}
	}

	/**
	 * VA_TRX_ERR에 최종 실패 건 추가
	 * @param trxMap
	 * @return
	 */
	public boolean insertChargeSettleErr(SharedMap<String, Object> trxMap) {
		int result = 0;
		String query = "INSERT INTO VA_TRX_ERR  (`trxId`,`id`,`ptnId`,`userId`,`trxType`,`trxUnit`,`trxDay`,`trxTime`,`amount`,`feeType`,`feeRate`,`fee`,`feeVat`,`ptnFeeRate`,`ptnFee`,`ptnFeeVat`,`bankFee`,`stlAmount`,`balance`,`trackId`,`refId`,`bankCd`,`account`,`holder`,`resultCd`,`resultMsg`,`regDay`) VALUES "
				+" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxMap.getString("trxId"));
			pstmt.setString(2,trxMap.getString("id"));
			pstmt.setString(3,trxMap.getString("ptnId"));
			pstmt.setString(4,trxMap.getString("userId"));
			pstmt.setString(5,trxMap.getString("trxType"));
			pstmt.setString(6,trxMap.getString("trxUnit"));
			pstmt.setString(7,trxMap.getString("trxDay"));
			pstmt.setString(8,trxMap.getString("trxTime"));
			pstmt.setLong(9,trxMap.getLong("amount"));
			pstmt.setString(10,trxMap.getString("feeType"));
			pstmt.setDouble(11,trxMap.getDouble("feeRate"));
			pstmt.setLong(12,trxMap.getLong("fee"));
			pstmt.setLong(13,trxMap.getLong("feeVat"));
			pstmt.setDouble(14,trxMap.getDouble("ptnFeeRate"));
			pstmt.setLong(15,trxMap.getLong("ptnFee"));
			pstmt.setLong(16,trxMap.getLong("ptnFeeVat"));
			pstmt.setLong(17,trxMap.getLong("bankFee"));
			pstmt.setLong(18,trxMap.getLong("stlAmount"));
			pstmt.setLong(19,trxMap.getLong("balance"));
			pstmt.setString(20,trxMap.getString("trackId"));
			pstmt.setString(21,trxMap.getString("refId"));
			pstmt.setString(22,trxMap.getString("bankCd"));
			pstmt.setString(23,trxMap.getString("account"));
			pstmt.setString(24,trxMap.getString("holder"));
			pstmt.setString(25,trxMap.getString("resultCd"));
			pstmt.setString(26,trxMap.getString("resultMsg"));
			pstmt.setString(27,trxMap.getString("regDay"));

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertChargeSettleErr ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		if(result > 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 출금 실패한 거래건 PG_CHARGE_SETTLE 테이블 데이터 삭제
	 * @param trxId
	 */
	public boolean deleteChargeSettle(String trxId){
		String query = " DELETE FROM PG_CHARGE_SETTLE WHERE trxId =?";
		int result = 0;

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxId);
			result 	= pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("deleteChargeSettle ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}
		if(result > 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 충전정산 hookAddr 조회
	 * @param trxId
	 * @return
	 */
	public String getchargeSettleHookAddr(String mchtId){
		String query = "SELECT hookAddr "
				+"	  FROM PG_MCHT_CHARGE_MNG "
				+"   WHERE mchtId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String hookAddr 		= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,mchtId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				hookAddr = rset.getString("hookAddr");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getchargeSettleHookAddr ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return hookAddr;
	}

	public boolean insertChargeSettleNoti(SharedMap<String, Object> ntsMap) {
		int result = 0;
		String query = "INSERT INTO `PG_CHARGE_SETTLE_NOTI` (`trxId`, `trxType`, `mchtId`, `trackId`, `hookAddr`, `retry`, `status`, `code`, `payLoad`, `resData`, `sentDate`, `regDay`, `regTime`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,ntsMap.getString("trxId"));
			pstmt.setString(2,ntsMap.getString("trxType"));
			pstmt.setString(3,ntsMap.getString("mchtId"));
			pstmt.setString(4,ntsMap.getString("trackId"));
			pstmt.setString(5,ntsMap.getString("hookAddr"));
			pstmt.setInt(6,ntsMap.getInt("retry"));
			pstmt.setString(7,ntsMap.getString("status"));
			pstmt.setInt(8,ntsMap.getInt("code"));
			pstmt.setString(9,ntsMap.getString("payLoad"));
			pstmt.setString(10,ntsMap.getString("resData"));
			pstmt.setTimestamp(11,ntsMap.getTimestamp("sentDate"));
			pstmt.setString(12,ntsMap.getString("regDay"));
			pstmt.setString(13,ntsMap.getString("regTime"));

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertChargeSettleNoti ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		if(result > 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 가상계좌 대행서비스 사용자 아이디 조회
	 * @param trxId
	 * @return
	 */
	public String getVaUserId(String accont){
		String query = "SELECT id "
				+"	  FROM VA_USER_VACCNT "
				+"   WHERE account = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String id 				= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,accont);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				id = rset.getString("id");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getVaUserId ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return id;
	}

	/**
	 * 가상계좌 대행서비스의 입금 최소,최대금액 조회
	 * @param id
	 * @return
	 */
	public SharedMap<String,Object> getDepositLimit(String id){
		SharedMap<String,Object> result = null;

		String query = "SELECT depositMinAmt,depositMaxAmt FROM VA_USER WHERE id =? ";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,id);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();
				result.put("depositMinAmt",rset.getLong("depositMinAmt"));
				result.put("depositMaxAmt",rset.getLong("depositMaxAmt"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getDepositLimit ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 가상계좌 대행서비스 3분이내 입금 거래건 조회
	 * @param trxDay : 어저꼐 거래일자
	 * @param id : 계정아이디
	 * @return
	 */
	public int getDuplicateTrans(String trxDay, String id){
		String query = "SELECT COUNT(1) AS cnt"
				+"	  FROM VA_TRX "
				+"	 WHERE trxDay >= ? AND trxType = '입금' AND id = ? AND  regDate > DATE_ADD(NOW(), INTERVAL -3 MINUTE)";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		int cnt 				= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxDay);
			pstmt.setString(2,id);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				cnt = rset.getInt("cnt");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getDuplicateTrans ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return cnt;
	}

	/**
	 * 해당 가상계좌 가맹점의 정산구분 조회
	 * @param mchtId
	 * @return
	 */
	public String getRealTimeMchtSvc(String mchtId){
		String q = "SELECT settle FROM PG_MCHT_SVC WHERE mchtId = '"+mchtId+"'";
		return getQuery(q);
	}

	/**
	 * 실시간 정산 승인거래 원장 (PG_TRX_REALTIME_PAY) 테이블 저장
	 * @param sharedMap
	 * @param response
	 */
	public boolean insertTrxRealTimePay(SharedMap<String, Object> trxPayMap, SharedMap<String,Object> mchtMngMap) {
		int result = 0;
		String curDate = CommonUtil.getCurrentDate("yyyyMMddHHmmss");

		String query = "INSERT INTO `PG_TRX_REALTIME_PAY` (`trxId`, `mchtId`, `tmnId`, `trackId`, `amount`, `authCd`, `trxType`, `payType`, `trxDay`, `trxTime`, `resultCd`, `resultMsg`, `van`, `vanId`, `vanTrxId`, `sendYn`, `transferInterval`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxPayMap.getString("vactId"));
			pstmt.setString(2,trxPayMap.getString("mchtId"));
			pstmt.setString(3,"");
			pstmt.setString(4,trxPayMap.getString("trackId"));
			pstmt.setLong(5,trxPayMap.getLong("amount"));
			pstmt.setString(6,"");

			if("입금".equals(trxPayMap.getString("trxType"))) {
				pstmt.setString(7,"0");
			}else {
				pstmt.setString(7,"1");
			}
			pstmt.setString(8,"V");
			pstmt.setString(9,curDate.substring(0, 8));
			pstmt.setString(10,curDate.substring(8));
			pstmt.setString(11,"0000");
			pstmt.setString(12,trxPayMap.getString("trxType"));
			pstmt.setString(13,"");
			pstmt.setString(14,"");
			pstmt.setString(15,trxPayMap.getString("issueId"));
			pstmt.setString(16,"N");
			pstmt.setString(17,mchtMngMap.getString("transferInterval"));

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertTrxRealTimePay ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		if(result > 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 입금취소 시 해당 취소의 원거래건이 실시간 정산 거래건 인지 확인
	 * @param vactId
	 * @return
	 */
	public SharedMap<String,Object> getRealtimeTrx(String vactId){
		SharedMap<String,Object> result = null;

		String query = " SELECT * FROM PG_TRX_REALTIME_PAY WHERE trxId =? ";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,vactId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();

				result.put("trxId",rset.getString("trxId"));
				result.put("mchtId",rset.getString("mchtId"));
				result.put("tmnId",rset.getString("tmnId"));
				result.put("trackId",rset.getString("trackId"));
				result.put("amount",rset.getLong("amount"));
				result.put("authCd",rset.getString("authCd"));
				result.put("trxType",rset.getString("trxType"));
				result.put("trxDay",rset.getString("trxDay"));
				result.put("trxTime",rset.getString("trxTime"));
				result.put("resultCd",rset.getString("resultCd"));
				result.put("resultMsg",rset.getString("resultMsg"));
				result.put("van",rset.getString("van"));
				result.put("vanId",rset.getString("vanId"));
				result.put("vanTrxId",rset.getString("vanTrxId"));
				result.put("sendYn",rset.getString("sendYn"));
				result.put("transferInterval",rset.getString("transferInterval"));
				result.put("sendDate",rset.getString("sendDate"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getRealtimeTrx ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 실시간 정산 승인거래 원장 (PG_TRX_REALTIME_PAY) 테이블 전송여부와 전송일시 업데이트
	 * 원장 출금전에 취소가 들어왔을 시 출금이 안되도록 업데이트
	 * @param trxId : 거래번호
	 * @return
	 */
	public int updateRealtimeSendCheck(String trxId){
		String query = "UPDATE PG_TRX_REALTIME_PAY "
				+ "    SET sendYn='Y', sendDate = NOW() "
				+ "	 WHERE trxid = ?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result = 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxId);

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("updateRealtimeSendCheck ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(pstmt);
			db.close(conn);
		}

		return result;
	}

	/**
	 * 실시간 정산 승인거래 원장 (PG_REALTIME_PAYOUT) 테이블 저장
	 * @param sharedMap
	 * @param response
	 */
	public boolean insertPgRealtimePayout(SharedMap<String, Object> payOutData) {
		int result = 0;

		String query = "INSERT INTO `PG_REALTIME_PAYOUT` (`trxId`, `mchtId`, `tmnId`, `trackId`, `trxDay`, `trxTime`, `authCd`, `trxType`, `payType`, `amount`, `stlFee`, `stlFeeVat`, `stlAmount`, `payOutFee`, `payOutFeeVat`, `payOutAmount`, `bankCd`, `bankName`, `account`, `accntHolder`, `payOutDay`, `payOutTime`, `resultCd`, `resultMsg`, `sendCnt`, `sendCheck`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,payOutData.getString("trxId"));
			pstmt.setString(2,payOutData.getString("mchtId"));
			pstmt.setString(3,payOutData.getString("tmnId"));
			pstmt.setString(4,payOutData.getString("trackId"));
			pstmt.setString(5,payOutData.getString("trxDay"));
			pstmt.setString(6,payOutData.getString("trxTime"));
			pstmt.setString(7,payOutData.getString("authCd"));
			pstmt.setString(8,payOutData.getString("trxType"));
			pstmt.setString(9,payOutData.getString("payType"));
			pstmt.setLong(10,payOutData.getLong("amount"));
			pstmt.setLong(11,payOutData.getLong("stlFee"));
			pstmt.setLong(12,payOutData.getLong("stlFeeVat"));
			pstmt.setLong(13,payOutData.getLong("stlAmount"));
			pstmt.setLong(14,payOutData.getLong("payOutFee"));
			pstmt.setLong(15,payOutData.getLong("payOutFeeVat"));
			pstmt.setLong(16,payOutData.getLong("payOutAmount"));
			pstmt.setString(17,payOutData.getString("bankCd"));
			pstmt.setString(18,payOutData.getString("bankName"));
			pstmt.setString(19,payOutData.getString("account"));
			pstmt.setString(20,payOutData.getString("accntHolder"));
			pstmt.setString(21,payOutData.getString("payOutDay"));
			pstmt.setString(22,payOutData.getString("payOutTime"));
			pstmt.setString(23,payOutData.getString("resultCd"));
			pstmt.setString(24,payOutData.getString("resultMsg"));
			pstmt.setString(25,payOutData.getString("sendCnt"));
			pstmt.setString(26,payOutData.getString("sendCheck"));

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertPgRealtimePayout ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		if(result > 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 데이터 암호화
	 */
	public String getAESEnc(String value){
		String query 			= "SELECT FN_AES_ENC(?) pw";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String pw 				= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,value);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				pw = rset.getString("pw");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getAESEnc ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return pw;
	}

	/**
	 * 가맹점 TAX 정보조회
	 * @param mchtId
	 * @return
	 */
	public SharedMap<String,Object> getMchtTaxByMchtId(String mchtId){
		SharedMap<String,Object> result = null;

		String query = " SELECT * FROM PG_MCHT_TAX WHERE mchtId =?";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,mchtId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();
				result.put("bankCd",rset.getString("bankCd"));
				result.put("bankName",rset.getString("bankName"));
				result.put("account",rset.getString("account"));
				result.put("accntHolder",rset.getString("accntHolder"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getMchtTaxByMchtId ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 가상계좌 대행서비스 파트너 일한도금액 조회
	 * @param accont
	 * @return
	 */
	public SharedMap<String,Object> getPtnLimitData(String accont){
		String query = "SELECT b.ptnId, b.limitDayAmtType, b.limitDayAmt "
				+ "	  FROM VA_PTN_VACCNT a, VA_PTN b "
				+ "  WHERE a.ptnId = b.ptnId AND a.account = ?";

		DBManager db 					= null;
		PreparedStatement pstmt			= null;
		Connection conn					= null;
		ResultSet rset					= null;

		SharedMap<String,Object> result = null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,accont);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();
				result.put("ptnId",rset.getString("ptnId"));
				result.put("limitDayAmtType",rset.getString("limitDayAmtType"));
				result.put("limitDayAmt",rset.getLong("limitDayAmt"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getPtnLimitData ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 가상계좌 대행서비스 계정 일입금 합계금액 조회
	 * @param ptnId
	 * @return
	 */
	public long getPtnDaySumAmt(String ptnId){
		long sumAmt = 0;

		String query = "SELECT IFNULL(SUM(amount) , 0) AS amount"
				+ "  FROM VA_TRX  "
				+ " WHERE ptnId = ? AND trxType = '입금' AND trxDay = date_format(NOW(),'%Y%m%d')";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,ptnId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				sumAmt = rset.getLong("amount");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getPtnDaySumAmt ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return sumAmt;
	}

	/**
	 * 가맹점 충전정산 거래내역 (PG_CHARGE_SETTLE) 테이블 저장
	 * @param sharedMap
	 * @param response
	 */
	public boolean insertChargeSettle(SharedMap<String, Object> trxPayMap, SharedMap<String,Object> mchtMngMap, Long bankFee) {
		String query = "insert into PG_CHARGE_SETTLE (trxId, mchtId, trxType, trxUnit, trxDay, trxTime, amount, fee, feeVat, bankFee, netAmount, balance, trackId, refId, bankCd, bankName, account, holder, recordInfo, summary, regId, regDay)  values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		int result 				= 0;
		long balance			= 0;
		long netAmount			= 0;

		String curDate 			= CommonUtil.getCurrentDate("yyyyMMddHHmmss");
		String type 			= "";
		String refId 			= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);

			pstmt.setString(1	, trxPayMap.getString("vactId"));
			pstmt.setString(2	, trxPayMap.getString("mchtId"));

			if("입금".equals(trxPayMap.getString("trxType"))) {
				type = "입금";
				netAmount = trxPayMap.getLong("amount") - trxPayMap.getLong("stlFee") - trxPayMap.getLong("stlFeeVat");
				balance = getMchtBalance(trxPayMap.getString("mchtId")) + netAmount;
				refId = trxPayMap.getString("vactId");
			}else if("취소".equals(trxPayMap.getString("trxType"))) {
				type = "출금";
				netAmount = trxPayMap.getLong("amount") - trxPayMap.getLong("stlFee") - trxPayMap.getLong("stlFeeVat");
				balance = getMchtBalance(trxPayMap.getString("mchtId")) - netAmount;
				refId = trxPayMap.getString("rootVactId");
			}

			pstmt.setString(3	, type);
			pstmt.setString(4	, "가상계좌정산");
			pstmt.setString(5	, curDate.substring(0, 8));
			pstmt.setString(6	, curDate.substring(8));
			pstmt.setLong(7  	, trxPayMap.getLong("amount"));
			pstmt.setLong(8  	, trxPayMap.getLong("stlFee"));
			pstmt.setLong(9  	, trxPayMap.getLong("stlFeeVat"));
			pstmt.setLong(10  	, bankFee);
			pstmt.setLong(11  	, netAmount);
			pstmt.setLong(12  	, balance);
			pstmt.setString(13	, trxPayMap.getString("trackId"));
			pstmt.setString(14	, refId);
			pstmt.setString(15	, "");
			pstmt.setString(16	, "");
			pstmt.setString(17	, "");
			pstmt.setString(18	, "");
			pstmt.setString(19	, "");
			pstmt.setString(20	, "가상계좌 실시간충전 정산금 지금");
			pstmt.setString(21	, trxPayMap.getString("mchtId"));
			pstmt.setString(22	, curDate.substring(0, 8));

			result = pstmt.executeUpdate();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("insertChargeSettle ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		if(result > 0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 충전정산 잔액조회
	 * @param mchtId
	 * @return
	 */
	public long getMchtBalance(String mchtId){
		String query = "SELECT balance"
				+"	  FROM PG_MCHT_BALANCE "
				+"	 WHERE mchtId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		long balance 			= 0;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,mchtId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				balance = rset.getLong("balance");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getMchtBalance ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return balance;
	}

	/**
	 * 해당일자가 휴일인지 체크
	 * @param today
	 * @return
	 */
	public String getHolidayCheck(String today) {
		String query 			= "SELECT status FROM PG_CODE_HOLIDAY WHERE days = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String status 			= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,today);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				status = rset.getString("status");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getHolidayCheck ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return status;
	}

	/**
	 * 가상계좌 1일한도, 1회한도 데이터 조회
	 * @param accont
	 * @return
	 */
	public SharedMap<String,Object> getVactLimitData(String account){
		SharedMap<String,Object> result = null;

		String query = "SELECT b.mchtId,b.limitOnce, b.limitDay"
				+ "  FROM PG_VACT_DTL a, PG_MCHT_MNG_VACT b "
				+ " WHERE a.mchtId = b.mchtId AND a.account = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,account);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();
				result.put("mchtId",rset.getString("mchtId"));
				result.put("limitOnce",rset.getLong("limitOnce"));
				result.put("limitDay",rset.getLong("limitDay"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getVactLimitData ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	/**
	 * 가상계좌 1일합계 조회
	 * @param trxDay
	 * @return
	 */
	public long getVactDaySum(String trxDay, String mchtId){
		long sumAmt = 0;

		String query = "SELECT SUM(amount) as sumAmt"
				+ "  FROM PG_VACT_TRX "
				+ " WHERE trxDay = ? and trxType = '입금' AND mchtId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,trxDay);
			pstmt.setString(2,mchtId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				sumAmt = rset.getLong("sumAmt");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getVactDaySumData ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return sumAmt;
	}

	/**
	 * PG_MCHT, PG_MCHT_MNG_VACT테이블의 상태체크
	 * @param mchtId
	 * @return
	 */
	public SharedMap<String,Object> getVactStatus(String mchtId){
		SharedMap<String,Object> result = null;

		String query = " SELECT A.status, B.status as vactStatus "
				+ "FROM PG_MCHT A, PG_MCHT_MNG_VACT B "
				+"WHERE A.mchtId = B.mchtId and A.mchtId = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,mchtId);

			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = new SharedMap<String,Object>();

				result.put("status",rset.getString("status"));
				result.put("vactStatus",rset.getString("vactStatus"));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("getVactStatus ERROR : {}, query : {}", e.getMessage(), query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}
}
