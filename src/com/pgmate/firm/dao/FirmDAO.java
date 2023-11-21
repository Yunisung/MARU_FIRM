package com.pgmate.firm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.lib.util.db.DBFactory;
import com.pgmate.lib.util.db.DBManager;
import com.pgmate.lib.util.lang.CommonUtil;

public class FirmDAO{

	private static Logger logger = LoggerFactory.getLogger( com.pgmate.firm.dao.FirmDAO.class );
	
	public FirmDAO() {
	}

	
	public synchronized static String getSeqNO(){
		String returnVal = "";
		String query = "SELECT FN_BANKSEQ() as val";
		
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
				returnVal = rset.getString("val");
			}
			conn.commit();
		}catch(Exception t){
			logger.debug("sql error : {}, query : {}",t.getMessage(),query);
		}finally {
			db.close(conn, pstmt, rset);
		}
		return returnVal;
	}
	

	public static String getCodeDesc(String bankCd, String code){
		if(code.equals("XXXX")){
			return "통신장애";
		}
		String query = " SELECT message FROM PG_FIRM_CODE WHERE bankCd = ? AND `code` = ?";
		
		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String result			= "";
		
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,bankCd);
			pstmt.setString(2,code);
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = CommonUtil.nToB(rset.getString("message"));
			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	public static String getResultMsg(String code){
		if(code.equals("XXXX")){
			return "통신장애";
		}
		String query = " SELECT message FROM PG_FIRM_CODE WHERE `code` = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String result			= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1,code);
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = CommonUtil.nToB(rset.getString("message"));
			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	public static String getVactResultMsg(String bankCd, String code){
		if(code.equals("XXXX")){
			return "통신장애";
		}
		String query = " SELECT message FROM PG_VACT_CODE WHERE `bankCd` = ? AND `code` = ?";

		DBManager db 			= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		ResultSet rset			= null;
		String result			= "";

		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			pstmt.setString(1, bankCd);
			pstmt.setString(2, code);
			rset 	= pstmt.executeQuery();

			while(rset.next()){
				result = CommonUtil.nToB(rset.getString("message"));
			}
		}catch(Exception e){
			logger.info("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
		}finally{
			db.close(conn,pstmt,rset);
		}

		return result;
	}

	public synchronized static String getFirmSeqNO(){
		String returnVal = "";
		String query = "SELECT FN_FIRMSEQ() as val";

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
				returnVal = rset.getString("val");
			}
			conn.commit();
		}catch(Exception t){
			logger.debug("sql error : {}, query : {}",t.getMessage(),query);
		}finally {
			db.close(conn, pstmt, rset);
		}
		return returnVal;
	}

	public synchronized static String getKycSeqNO(){
		String returnVal = "";
		String query = "SELECT FN_DOZN_KYC_SEQ() as val";

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
				returnVal = rset.getString("val");
			}
			conn.commit();
		}catch(Exception t){
			logger.debug("sql error : {}, query : {}",t.getMessage(),query);
		}finally {
			db.close(conn, pstmt, rset);
		}
		return returnVal;
	}


	public static boolean resetFirmSeqNo() {
		String query = "UPDATE PG_SEQ SET curVal = '1' WHERE name = 'FIRM'";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			result = pstmt.executeUpdate();

			conn.commit();

		}catch(Exception e){
			logger.error("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
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

	public static boolean resetKycSeqNo() {
		String query = "UPDATE PG_SEQ SET curVal = '1' WHERE name = 'DOZN_KYC'";

		DBManager db 	= null;
		PreparedStatement pstmt	= null;
		Connection conn			= null;
		int result		=0;
		try{
			db 		= DBFactory.getInstance();
			conn	= db.getConnection();
			pstmt	= conn.prepareStatement(query);
			result = pstmt.executeUpdate();

			conn.commit();

		}catch(Exception e){
			logger.error("DB Error : {} , {} , [{}]",Thread.currentThread().getStackTrace()[1].getMethodName(),e.getMessage(),query);
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
}
