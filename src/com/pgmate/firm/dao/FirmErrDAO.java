package com.pgmate.firm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.ksnet.FB0400100Bean;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.lib.util.db.DBFactory;
import com.pgmate.lib.util.db.DBManager;
import com.pgmate.lib.util.lang.CommonUtil;

public class FirmErrDAO {
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(com.pgmate.firm.dao.FirmErrDAO.class);
	
	public FirmErrDAO() {
		
	}
	
	public boolean insertFirmErr(long index, FBHeaderBean fbHeaderBean, FB0400100Bean fb0400100Bean){
		
		String query = "INSERT INTO PG_FIRM_ERR (bankCd,	recvDate,	recvTime,	seqNo,	sendAccount,	recvBankCd,	recvAccount,	"
				+ " amount,	successAmount,	failAmount,	divCount,	divNo,	noticeAmount,	resultCd,	procGb,	stlId, resultMsg,regDay)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,'Y',?,(SELECT message from PG_FIRM_CODE where bankcd='ERR' and code=?) ,?)";
		
		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			int idx = 1;
			pstmt.setString(idx++, fbHeaderBean.getNewBankCode());
			pstmt.setString(idx++, fbHeaderBean.getTransactionTime().substring(0,8));
			pstmt.setString(idx++, fbHeaderBean.getTransactionTime().substring(8,14));
			pstmt.setString(idx++, fbHeaderBean.getSpecNumber());
			pstmt.setString(idx++, fb0400100Bean.getMAccount());
			pstmt.setString(idx++, fb0400100Bean.getReceiveNewBankCode());
			pstmt.setString(idx++, fb0400100Bean.getReceiveAccount());
			pstmt.setLong(idx++, CommonUtil.parseLong(fb0400100Bean.getAmount()));
			pstmt.setLong(idx++, CommonUtil.parseLong(fb0400100Bean.getSuccessAmount()));
			pstmt.setLong(idx++, CommonUtil.parseLong(fb0400100Bean.getFailureAmount()));
			pstmt.setString(idx++, fb0400100Bean.getDivisionCount());
			pstmt.setString(idx++, fb0400100Bean.getDivisionNumber());
			pstmt.setLong(idx++, CommonUtil.parseLong(fb0400100Bean.getErrorAmount()));
			pstmt.setString(idx++, fb0400100Bean.getErrorCode());
			pstmt.setLong(idx++, index);
			pstmt.setString(idx++, fb0400100Bean.getErrorCode());
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
			
			if(index > 0){
				FirmTrxDAO money = new FirmTrxDAO();
				
				//boolean success = money.deletetWallet(trnId);
				//logger.debug("타행이체 거래 삭제 처리 :trnid : {} , {}",trnId,success);
				
				//success = money.updateWalletOut(trnId, "지급실패", fb0400100Bean.getErrorCode(), SeqDAO.getCodeDesc(fbHeaderBean.getNewBankCode(),fb0400100Bean.getErrorCode())+"[타행불능]");
				//logger.debug("타행이체 WALLETOUT UDPATE :trnid : {} , {}",trnId,success);
			}
			
			return true;
		}else{
			return false;
		}
	}
}
