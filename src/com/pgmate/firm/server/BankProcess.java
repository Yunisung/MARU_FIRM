package com.pgmate.firm.server;

import java.io.Serializable;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.dao.FirmTrxDAO;
import com.pgmate.firm.dao.VactDAO;
import com.pgmate.firm.hook.ChargeSettleHook;
import com.pgmate.firm.hook.VactHook;
import com.pgmate.firm.ksnet.FB0200300Bean;
import com.pgmate.firm.ksnet.FB0400100Bean;
import com.pgmate.firm.ksnet.FB0900100Bean;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.firm.util.SmsGw;
import com.pgmate.firm.util.VAUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import com.pgmate.lib.util.map.SharedMap;

/**
 * @author Administrator
 *
 */
public class BankProcess implements Serializable {

	private Logger logger = LoggerFactory.getLogger( getClass() );
	private Firm firm = null;
	private SmsGw smsGw = null;
	
	public BankProcess(Firm firm) {
		this.firm = firm;
	}
	
	
	public byte[] execute(byte[] recv){
		
		FBHeaderBean headerBean = new FBHeaderBean(recv);
		headerBean.setTransactionIndex(CommonUtil.toString(recv, 100));
		
		String msgType = headerBean.getSpecCode()+headerBean.getClassificationCode();
		logger.info("seqNo : {}, msgType : {}, bankCd : {}",headerBean.getSpecNumber(),msgType,headerBean.getNewBankCode());
		
		
		if(msgType.equals("0200300")){			//가상계좌거래내역
			return vactTrx(headerBean);
		}else if(msgType.equals("0900100")){	//가상계좌수취조회
			return getSearchVact(headerBean);
		}else if(msgType.equals("0400100")){	//타행이체불능명세
			return getErrorProcess(headerBean);
		}else{
			logger.info("skip processs");
			logger.info("data : [{}]",headerBean.getTransactionIndex());
			
		}
		
		
		return recv;
		  
	}
	
	
	public byte[] vactTrx(FBHeaderBean headerBean){
		String resData = "";

		try {
			FB0200300Bean fbBean = new FB0200300Bean(headerBean.getTransactionIndex());
			
			logger.info("seqNo : {}, account : {}, amount : {}, trxType : {}",headerBean.getSpecNumber(), fbBean.getVirtualAccount(),CommonUtil.parseLong(fbBean.getAmount()),fbBean.getClassificationCode());
			logger.info("seqNo : {}, bankCd : {}",headerBean.getSpecNumber(), headerBean.getNewBankCode());
			
			VactDAO vactDAO = new VactDAO();
	
			//중복거래조회
			String dupTrxId = vactDAO.isDuplicatedTrx(headerBean, fbBean);
			
			logger.info("seqNo : {}, dupTrxId : {}", headerBean.getSpecNumber(), dupTrxId);
			
			if("".equals(dupTrxId)){
				SharedMap<String,Object> vactDtlMap = vactDAO.getAccountDtl(fbBean.getVirtualAccount());
				
				if(fbBean.getClassificationCode().equals("20")){	//가상계좌입금
					if(vactDtlMap == null){
						logger.info("가상계좌발행내역없음");
					}else{
						logger.info("가상계좌 입금 정보확인 : [{}][{}]",headerBean.getSpecNumber(), fbBean.getVirtualAccount());
						
						String vactId = VactDAO.getVactId();
						
						logger.info("가상계좌 입금 vactId : [{}][{}][{}]",headerBean.getSpecNumber(), vactId, fbBean.getVirtualAccount());
						
						SharedMap<String,Object> vactMngMap = vactDAO.getMchtMngVact(vactDtlMap.getString("mchtId"));
						SharedMap<String,Object> trxMap = new SharedMap<String,Object>();
						trxMap.put("vactId"		, vactId);
						trxMap.put("issueId"	, vactDtlMap.getString("issueId"));
						trxMap.put("mchtId"		, vactDtlMap.getString("mchtId"));
						trxMap.put("bankCd"		, headerBean.getNewBankCode());
						trxMap.put("account"	, fbBean.getVirtualAccount());
						trxMap.put("seqNo"		, headerBean.getSpecNumber());
						trxMap.put("vactType"	, vactDtlMap.getString("vactType"));
						trxMap.put("amount"		, CommonUtil.parseLong(fbBean.getAmount()));
						trxMap.put("sender"		, fbBean.getName().replaceAll("\\p{Z}", ""));
						trxMap.put("trxType"	, "입금");
						trxMap.put("rootVactId"	, "");	//취소시만 사용
						trxMap.put("trxDay"		, headerBean.getTransactionTime().substring(0,8));
						trxMap.put("trxTime"	, headerBean.getTransactionTime().substring(8,14));
						
						logger.info("가상계좌 입금 feeType : [{}][{}]",headerBean.getSpecNumber(), vactMngMap.getString("feeType"));
						
						if("0".equals(vactMngMap.getString("feeType"))){
							//수수료타입이 정액인 경우
							trxMap.put("stlFee"					, vactMngMap.getLong("fee"));
							trxMap.put("stlFeeVat"				, calcVat(vactMngMap.getLong("fee")));
							trxMap.put("stlDistFee"				, vactMngMap.getLong("distFee"));
							trxMap.put("stlDistFeeVat"			, calcVat(vactMngMap.getLong("distFee")));
							trxMap.put("stlAgencyFee"			, vactMngMap.getLong("agencyFee"));
							trxMap.put("stlAgencyFeeVat"		, calcVat(vactMngMap.getLong("agencyFee")));
							trxMap.put("stlSalesFee"			, vactMngMap.getLong("salesFee"));
							trxMap.put("stlSalesFeeVat"			, calcVat(vactMngMap.getLong("salesFee")));
						}else {
							//수수료타입이 정률인 경우
							long amt = CommonUtil.parseLong(fbBean.getAmount());
							
							if(vactMngMap.getDouble("rate") > 0){
								trxMap.put("stlFee"				, calcFee(amt, vactMngMap.getDouble("rate")));
								trxMap.put("stlFeeVat"			, calcVat(trxMap.getLong("stlFee")));
							}else {
								trxMap.put("stlFee"				, 0);
								trxMap.put("stlFeeVat"			, 0);
							}
							
							if(vactMngMap.getDouble("rate") - vactMngMap.getDouble("agencyRate") > 0){
								trxMap.put("stlAgencyFee"		, calcFee(amt, vactMngMap.getDouble("rate") - vactMngMap.getDouble("agencyRate")));
								trxMap.put("stlAgencyFeeVat"	, calcVat(trxMap.getLong("stlAgencyFee")));
							}else {
								trxMap.put("stlAgencyFee"	, 0);
								trxMap.put("stlAgencyFeeVat"	, 0);
							}
							
							if(vactMngMap.getDouble("salesRate") > 0){
								trxMap.put("stlSalesFee"		, calcFee(trxMap.getLong("stlAgencyFee"), vactMngMap.getDouble("salesRate")));
								trxMap.put("stlSalesFeeVat"		, calcVat(trxMap.getLong("stlSalesFee")));
							}else {
								trxMap.put("stlSalesFee"		, 0);
								trxMap.put("stlSalesFeeVat"		, 0);
							}
							
							if(vactMngMap.getDouble("agencyRate") - vactMngMap.getDouble("distRate") > 0){
								trxMap.put("stlDistFee"			, calcFee(amt, vactMngMap.getDouble("agencyRate") - vactMngMap.getDouble("distRate")));
								trxMap.put("stlDistFeeVat"		, calcVat(trxMap.getLong("stlDistFee")));
							}else {
								trxMap.put("stlDistFee"			, 0);
								trxMap.put("stlDistFeeVat"		, 0);
							}
							
							// 에이전시 최종 수수료 : 에이전시 수수료 - 지사 수수료
							trxMap.put("stlAgencyFee", trxMap.getLong("stlAgencyFee")-trxMap.getLong("stlSalesFee"));
						}
						
						trxMap.put("stlType"	, vactMngMap.getString("settleType"));
						trxMap.put("stlDay"		, calcDay(vactDAO,trxMap.getString("stlType"),headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("stlDistType"	, vactMngMap.getString("distSettleType"));
						trxMap.put("stlDistDay"		, calcDay(vactDAO,trxMap.getString("stlDistType"),headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("stlAgencyType"	, vactMngMap.getString("agencySettleType"));
						trxMap.put("stlAgencyDay"		, calcDay(vactDAO,trxMap.getString("stlAgencyType"),headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("stlSalesType"		, vactMngMap.getString("salesSettleType"));
						trxMap.put("stlSalesDay"		, calcDay(vactDAO,trxMap.getString("stlSalesType"),headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("vanFee"		, vactDtlMap.getLong("trxFee"));
						trxMap.put("vanDay"		, vactDAO.getVanDay(headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("hookType"	, vactMngMap.getString("hookType"));
						trxMap.put("hookAddr"	, vactMngMap.getString("hookAddr"));
						trxMap.put("trackId"	, vactDtlMap.getString("trackId"));
						trxMap.put("udf1"		, vactDtlMap.getString("udf1"));
						trxMap.put("udf2"		, vactDtlMap.getString("udf2"));

						logger.info("가상계좌 입금 udf1 : [{}][{}]",headerBean.getSpecNumber(), vactDtlMap.getString("udf1"));
						logger.info("가상계좌 입금 udf2 : [{}][{}]",headerBean.getSpecNumber(), vactDtlMap.getString("udf2"));
						logger.info("가상계좌 입금 PG_VACT_TRX INSERT : [{}][{}][{}]",headerBean.getSpecNumber(), vactId, vactDAO.insertVactTrx(trxMap));
						
						if(!vactMngMap.isNullOrSpace("hookAddr")){
							new VactHook(trxMap, vactDAO).start();
						}
						
						if(vactDtlMap.getString("vactType").equals("임시")){
							logger.info("가상계좌 입금 임시 계좌 만료처리 : [{}][{}]",headerBean.getSpecNumber(), vactDAO.vactDtlSetExpired(vactDtlMap.getString("issueId"),vactId));
						}
						
						if("D+0".equals(vactMngMap.getString("settleType"))){
							//가상계좌 입금 건인데 실시간 전송 가맹점의 거래건일 경우 실시간 정산 승인거래 원장에 저장
							logger.info("===================================================");
							logger.info("가상계좌 입금 실시간정산 처리 PG_TRX_REALTIME_PAY INSERT : [{}][{}][{}][{}]",headerBean.getSpecNumber(), vactId, fbBean.getVirtualAccount(), vactDAO.insertTrxRealTimePay(trxMap, vactMngMap));
							logger.info("===================================================");
						}
						
						if("C+0".equals(vactMngMap.getString("settleType"))){
							//정상결제 완료 건인데 충전정산 실시간 전송 가맹점의 거래건일 경우 가맹점 충전정산 거래내역 테이블 저장
							logger.info("===================================================");
							logger.info("가상계좌 입금 충전정산 실시간정산 처리 PG_CHARGE_SETTLE INSERT : [{}][{}][{}][{}]",headerBean.getSpecNumber(), vactId, fbBean.getVirtualAccount(), vactDAO.insertChargeSettle(trxMap, vactMngMap, vactDtlMap.getLong("trxFee")));
							logger.info("===================================================");
						}
					}
				}else if(fbBean.getClassificationCode().equals("51")){	//가상계좌입금취소
					logger.info("가상계좌 입금취소 seqNo : {}, orgTrxDay: {}, orgSeqNo : {}",headerBean.getSpecNumber(), headerBean.getInquiryDay(), headerBean.getInqueryNumber());
					
					SharedMap<String,Object> hisTrxMap = vactDAO.getHisTrxMap(headerBean.getInquiryDay(), fbBean.getVirtualAccount(), headerBean.getInqueryNumber());
					
					if(hisTrxMap != null) {
						String vactId = VactDAO.getVactId();
						
						logger.info("가상계좌 입금취소 vactId : [{}][{}][{}][{}]", headerBean.getSpecNumber(), headerBean.getInqueryNumber(), vactId, fbBean.getVirtualAccount());
						
						SharedMap<String,Object> vactMngMap = vactDAO.getMchtMngVact(hisTrxMap.getString("mchtId"));
						
						SharedMap<String,Object> trxMap = new SharedMap<String,Object>();
						trxMap.put("vactId"		, vactId);
						trxMap.put("issueId"	, hisTrxMap.getString("issueId"));
						trxMap.put("mchtId"		, hisTrxMap.getString("mchtId"));
						trxMap.put("bankCd"		, headerBean.getNewBankCode());
						trxMap.put("account"	, fbBean.getVirtualAccount());
						trxMap.put("seqNo"		, headerBean.getSpecNumber());
						trxMap.put("vactType"	, hisTrxMap.getString("vactType"));
						trxMap.put("amount"		, -CommonUtil.parseLong(fbBean.getAmount()));
						trxMap.put("sender"		, fbBean.getName().replaceAll("\\p{Z}", ""));
						trxMap.put("trxType"	, "취소");
						trxMap.put("rootVactId"	, hisTrxMap.getString("vactId"));	//취소시만 사용
						trxMap.put("trxDay"		, headerBean.getTransactionTime().substring(0,8));
						trxMap.put("trxTime"	, headerBean.getTransactionTime().substring(8,14));
						trxMap.put("stlType"	, hisTrxMap.getString("stlType"));
						trxMap.put("stlDay"		, calcDay(vactDAO,trxMap.getString("stlType"),headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("stlFee"		, -hisTrxMap.getLong("stlFee"));
						trxMap.put("stlFeeVat"	, -hisTrxMap.getLong("stlFeeVat"));
						trxMap.put("stlDistType"	, hisTrxMap.getString("stlDistType"));
						trxMap.put("stlDistDay"		, calcDay(vactDAO,trxMap.getString("stlDistType"),headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("stlDistFee"		, -hisTrxMap.getLong("stlDistFee"));
						trxMap.put("stlDistFeeVat"	, -hisTrxMap.getLong("stlDistFeeVat"));
						trxMap.put("stlAgencyType"	, hisTrxMap.getString("stlAgencyType"));
						trxMap.put("stlAgencyDay"		, calcDay(vactDAO,trxMap.getString("stlAgencyType"),headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("stlAgencyFee"		, -hisTrxMap.getLong("stlAgencyFee"));
						trxMap.put("stlAgencyFeeVat"	, -hisTrxMap.getLong("stlAgencyFeeVat"));
						trxMap.put("stlSalesType"	, hisTrxMap.getString("stlSalesType"));
						trxMap.put("stlSalesDay"		, calcDay(vactDAO,trxMap.getString("stlSalesType"),headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("stlSalesFee"		, -hisTrxMap.getLong("stlSalesFee"));
						trxMap.put("stlSalesFeeVat"	, -hisTrxMap.getLong("stlSalesFeeVat"));
						trxMap.put("vanFee"		, -hisTrxMap.getLong("vanFee"));
						trxMap.put("vanDay"		, vactDAO.getVanDay(headerBean.getTransactionTime().substring(0,8)));
						trxMap.put("hookType"	, vactMngMap.getString("hookType"));
						trxMap.put("hookAddr"	, vactMngMap.getString("hookAddr"));
						trxMap.put("trackId"	, vactMngMap.getString("trackId"));
						trxMap.put("udf1"		, vactMngMap.getString("udf1"));
						trxMap.put("udf2"		, vactMngMap.getString("udf2"));
						
						logger.info("가상계좌 입금취소 PG_VACT_TRX INSERT : [{}][{}][{}][{}]",headerBean.getSpecNumber(), headerBean.getInqueryNumber(), vactId, vactDAO.insertVactTrx(trxMap));

						if(!vactMngMap.isNullOrSpace("hookAddr")){
							new VactHook(trxMap, vactDAO).start();
						}
						if(hisTrxMap.getString("vactType").equals("임시")){
							logger.info("입금취소 임시계좌 만료 to 발행 : [{}][{}][{}][{}]",headerBean.getSpecNumber(), headerBean.getInqueryNumber(), vactId, vactDAO.vactDtlSetIssue(hisTrxMap.getString("issueId"),vactId));
						}
						
						//취소건이 실시간 전송 거래건일 경우 실시간 정산 테이블에 취소거래 저장
						SharedMap<String, Object> realtimeTrx = vactDAO.getRealtimeTrx(hisTrxMap.getString("vactId"));
		
						if(realtimeTrx != null) {
							logger.info("===================================================");
							logger.info("가상계좌 입금취소 실시간정산 처리 : [{}][{}][{}][{}][{}]",headerBean.getSpecNumber(), headerBean.getInqueryNumber(), vactId, fbBean.getVirtualAccount(), realtimeTrx.get("sendYn"));
							
							//펌출금전에 자동취소 요청이 들어온경우
							if("N".equals(realtimeTrx.get("sendYn"))) {
								logger.info("가상계좌 입금취소 실시간정산 출금 전 취소발생 : [{}][{}][{}]",headerBean.getSpecNumber(), headerBean.getInqueryNumber(), hisTrxMap.getString("vactId"));
								
								//펌출금 전에 자동취소가 들어왔기 때문에 원거래 입금 건 출금안되도록 PG_TRX_REALTIME_PAY테이블의 sendYn 업데이트
								int updateRes = vactDAO.updateRealtimeSendCheck(hisTrxMap.getString("vactId"));
								
								if(updateRes > 0) {
									logger.info("가상계좌 입금취소 실시간정산 업데이트 성공 : [{}][{}][{}][{}]", headerBean.getSpecNumber(), headerBean.getInqueryNumber(), hisTrxMap.getString("vactId"), updateRes);
								}else {
									logger.info("가상계좌 입금취소 실시간정산 업데이트 실패 : [{}][{}][{}][{}]", headerBean.getSpecNumber(), headerBean.getInqueryNumber(), hisTrxMap.getString("vactId"), updateRes);
								}
							}else {
								logger.info("가상계좌 입금취소 PG_TRX_REALTIME_PAY INSERT : [{}][{}][{}][{}]", headerBean.getSpecNumber(), headerBean.getInqueryNumber(), vactId, vactDAO.insertTrxRealTimePay(trxMap, vactMngMap));
							}
							
							logger.info("===================================================");
						}
						
						//취소건이 충전정산 거래건일 경우 충전정산 테이블에 취소거래 저장
						ResultSet chargeSettle = vactDAO.getChargeSettle(hisTrxMap.getString("vactId"));
						
						if(chargeSettle != null) {
							logger.info("===================================================");
							logger.info("가상계좌 입금취소 충전정산 처리 : [{}][{}][{}][{}][{}]", headerBean.getSpecNumber(), headerBean.getInqueryNumber(), vactId, chargeSettle.getString("trxId"), fbBean.getVirtualAccount());
							chargeSettleCancel(chargeSettle.getString("trxId"));
							logger.info("===================================================");
						}
					}else {
						logger.info("===================================================");
						logger.info("가상계좌 취소 원거래 건 없음 seqNo : {}, orgTrxDay: {}, orgSeqNo : {}",headerBean.getSpecNumber(), headerBean.getInquiryDay(), headerBean.getInqueryNumber());
						logger.info("===================================================");
					}
				}else{
					String intype = "";
					if(fbBean.getClassificationCode().equals("30") || fbBean.getClassificationCode().equals("32")){
						intype = "출금";
					}else if(fbBean.getClassificationCode().equals("52")){
						intype = "출금취소";
					}else if(fbBean.getClassificationCode().equals("31")){
						intype = "부도지급";
					}else if(fbBean.getClassificationCode().equals("53")){
						intype = "부도입금취소";
					}else if(fbBean.getClassificationCode().equals("40")){
						intype = "입금이자";
					}else if(fbBean.getClassificationCode().equals("54")){
						intype = "부도지급취소";
					}else if(fbBean.getClassificationCode().equals("41")){
						intype = "출금이자";
					}else if(fbBean.getClassificationCode().equals("21") || fbBean.getClassificationCode().equals("60")){
						intype = "수탁만기입금";
					}else{
						intype = "기타";
					}
	
					logger.info("가상계좌 Unknown trxType : {},{},amount : {}",fbBean.getClassificationCode(),intype,fbBean.getAmount());
				}
			}else{
				logger.info("uplicated trasaction : seqNo : {}, dupIdx : {}", headerBean.getSpecNumber(), dupTrxId);
			}
	
			headerBean.setSpecCode("0210");
			headerBean.setTransactionTime(CommonUtil.getCurrentDate("yyyyMMddHHmmss"));
			headerBean.setBankResponseCode("0000");
			headerBean.setKsnetResponseCode("0000");
			
			resData = headerBean.getTransaction() +fbBean.getTransaction();
		}catch(Exception e) {
			logger.error("vactTrx Error : [{}]", e.getMessage());
			e.printStackTrace();
		}
		
		return resData.getBytes();
	}
	
	
	/*
	 * 가상계좌 수취조회
	 */
	public byte[] getSearchVact(FBHeaderBean headerBean){
		FB0900100Bean fbBean = new FB0900100Bean(headerBean.getTransactionIndex());
		logger.info("account : {},amount : {},trxType : {}",fbBean.getVirtualAccount(),CommonUtil.parseLong(fbBean.getAmount()),fbBean.getTransactionType());
		VactDAO vactDAO = new VactDAO();
		SharedMap<String,Object> vactMap = vactDAO.getAccount(fbBean.getVirtualAccount(),fbBean.getTransactionType()) ;
		
		//해당입금 거래가 가상계좌 대행서비스 거래인지 확인
		//PYS : 필요없는 로직 관련로직 삭제진행
		//String vactUserId = vactDAO.getVaUserId(fbBean.getVirtualAccount());
		
		String resultCd = "0000";
		String resultMsg = "정상";
		
		if(!fbBean.getTransactionType().equals("51")) {		//입금취소는 무조건 수취
			if(vactMap == null){
				resultCd = VAUtil.vaResponseCode("8",headerBean.getNewBankCode());
				resultMsg = "발행취소 또는 원장없음";
				logger.info("status : {}", "vactMap is null");
			}else{
				SharedMap<String,Object> getVactStatus = vactDAO.getVactStatus(vactMap.getString("mchtId"));
				
				if(!"사용".equals(getVactStatus.getString("status")) || !"사용".equals(getVactStatus.getString("vactStatus"))){
					resultCd = VAUtil.vaResponseCode("9",headerBean.getNewBankCode());
					resultMsg = "가상계좌 미사용 가맹점 입니다.";
					logger.info("getVactStatus status : [{}][{}]", getVactStatus.getString("status"), getVactStatus.getString("vactStatus"));
				}else {
					logger.info("issueId : {}",vactMap.getString("issueId"));
					logger.info("status : {}",vactMap.getString("status"));
					logger.info("expireAt : {}",vactMap.getString("expireAt"));
					logger.info("amount : {},{}",vactMap.getString("oper"),vactMap.getString("amount"));
					
					long currentTime = CommonUtil.parseLong(CommonUtil.getCurrentDate("yyyyMMddHH"));
					if(currentTime > vactMap.getLong("expireAt")){
						resultCd = VAUtil.vaResponseCode("4",headerBean.getNewBankCode());
						resultMsg = "입금 기간 초과 또는 거래 시간 초과";
					}else{
						
						long amount = CommonUtil.parseLong(fbBean.getAmount());
						long limitAmount = vactMap.getLong("amount");
						
						if(limitAmount != 0){
							if(vactMap.isEquals("oper", "eq")){
								if(amount != limitAmount){
									resultCd = VAUtil.vaResponseCode("3",headerBean.getNewBankCode());
									resultMsg = "거래금액 불일치 eq";	
								}
							}else if(vactMap.isEquals("oper", "gt")){
								if(amount >= limitAmount){
									resultCd = VAUtil.vaResponseCode("3",headerBean.getNewBankCode());
									resultMsg = "거래금액 불일치 gt";	
								}
							}else if(vactMap.isEquals("oper", "ge")){
								if(amount > limitAmount){
									resultCd = VAUtil.vaResponseCode("3",headerBean.getNewBankCode());
									resultMsg = "거래금액 불일치 ge";	
								}
							}else if(vactMap.isEquals("oper", "le")){
								if(amount < limitAmount){
									resultCd = VAUtil.vaResponseCode("3",headerBean.getNewBankCode());
									resultMsg = "거래금액 불일치 le";	
								}
							}else if(vactMap.isEquals("oper", "lt")){
								if(amount <= limitAmount){
									resultCd = VAUtil.vaResponseCode("3",headerBean.getNewBankCode());
									resultMsg = "거래금액 불일치 lt";	
								}
							}
						}

						if(resultCd.equals("0000")){
							SharedMap<String,Object> vactLimitData = vactDAO.getVactLimitData(fbBean.getVirtualAccount());

							//가상계좌 1회한도 체크
							if(vactLimitData.getLong("limitOnce") > 0 && vactLimitData.getLong("limitOnce") < amount){
								resultCd = "V713";
								resultMsg = "가상계좌 1회 한도초과";

								logger.info("가상계좌 1회 한도초과 : [{}][{}][{}][{}]",  fbBean.getVirtualAccount(), vactLimitData.getString("mchtId"), vactLimitData.getLong("limitOnce"), amount);
							}

							//가상계좌 1일한도 체크
							if(vactLimitData.getDouble("limitDay") > 0 ){
								long vactDaySum = vactDAO.getVactDaySum(CommonUtil.getCurrentDate("yyyyMMdd"), vactLimitData.getString("mchtId"));

								if(vactLimitData.getDouble("limitDay") < vactDaySum + amount){
									resultCd = "V713";
									resultMsg = "가맹점 1일 한도초과";

									logger.info("가맹점 1일 한도초과 : [{}][{}][{}][{}][{}]",  fbBean.getVirtualAccount(), vactLimitData.getString("mchtId"), vactLimitData.getLong("limitDay"), vactDaySum, amount);
								}
							}
						}

						//아래로직은 가상계좌 대행서비스 할때 적용할것. 현재는 위에껄로 적용

						/*if(resultCd.equals("0000")){
							if(!"".equals(vactUserId)) {
								String ptnId = "";
								//가상계좌 대행서비스 입금거래 건의 경우 3분이내에 동일한 가상계좌번호로 입금 시 입금 실패처리 
								//int dupleCnt = vactDAO.getDuplicateTrans(yesterDay, vactUserId);
								
								//if(dupleCnt == 0) {
								//입금이 가상계좌 대행서비스 입금건이면 VA_USER(가상계좌 충전 계정 관리) 테이블에서 입금 최소,최대금액 조회
								SharedMap<String,Object> depositLimit = vactDAO.getDepositLimit(vactUserId);
								
								//입금 최소금액 이하 입금요청 건이거나 최대금액 이상 입금요청 건일경우 오류 처리
								if(amount < depositLimit.getLong("depositMinAmt")){
									resultCd = VAUtil.vaResponseCode("3",headerBean.getNewBankCode());
									resultMsg = "입금 최소금액 이하 거래";	
								}else if(amount > depositLimit.getLong("depositMaxAmt")){
									resultCd = VAUtil.vaResponseCode("3",headerBean.getNewBankCode());
									resultMsg = "입금 최대금액 이상 거래";	
								}else {
									SharedMap<String,Object> ptnLimitData = vactDAO.getPtnLimitData(fbBean.getVirtualAccount());
									ptnId = ptnLimitData.getString("ptnId");
									
									//해당 파트너가 1일 한도 사용여부가 설정인경우 1일 한도체크
									if(ptnLimitData.getString("limitDayAmtType").equals("설정")) {
										long sumAmt = vactDAO.getPtnDaySumAmt(ptnId);
										
										//1일 한도초과 체크
										if(ptnLimitData.getLong("limitDayAmt") < sumAmt + amount){
											resultCd = VAUtil.vaResponseCode("3",headerBean.getNewBankCode());
											resultMsg = "파트너 1일 입금한도 초과";	
											
											logger.info("파트너 1일 입금한도 초과 : [{}][{}][{}][{}][{}]", ptnId, vactUserId, ptnLimitData.getLong("limitDayAmt"), sumAmt, amount);
										}
									}
								}
								
								logger.info("가상계좌 대행서비스 입금 금액체크 : [{}][{}][{}][{}][{}][{}]", fbBean.getVirtualAccount(), ptnId, vactUserId, amount, depositLimit.getLong("depositMinAmt"), depositLimit.getLong("depositMaxAmt"));
								*//*
								 * }else { resultCd = VAUtil.vaResponseCode("9",headerBean.getNewBankCode());
								 * resultMsg = "3분이내 거래 발생";
								 * 
								 * logger.info("가상계좌 대행서비스 입금 3분이내 거래 : [{}][{}][{}][{}]",
								 * fbBean.getVirtualAccount(), vactUserId, amount, dupleCnt); }
								 *//*
							}else {
								SharedMap<String,Object> vactLimitData = vactDAO.getVactLimitData(fbBean.getVirtualAccount());
								
								//가상계좌 1회한도 체크
								if(vactLimitData.getLong("limitOnce") > 0 && vactLimitData.getLong("limitOnce") < amount){
									resultCd = "V713";
									resultMsg = "가상계좌 1회 한도초과";
									
									logger.info("가상계좌 1회 한도초과 : [{}][{}][{}][{}]",  fbBean.getVirtualAccount(), vactLimitData.getString("mchtId"), vactLimitData.getLong("limitOnce"), amount);
								}

								//가상계좌 1일한도 체크
								if(vactLimitData.getDouble("limitDay") > 0 ){
									long vactDaySum = vactDAO.getVactDaySum(CommonUtil.getCurrentDate("yyyyMMdd"), vactLimitData.getString("mchtId"));
									
									if(vactLimitData.getDouble("limitDay") < vactDaySum + amount){
										resultCd = "V713";
										resultMsg = "가맹점 1일 한도초과";

										logger.info("가맹점 1일 한도초과 : [{}][{}][{}][{}][{}]",  fbBean.getVirtualAccount(), vactLimitData.getString("mchtId"), vactLimitData.getLong("limitDay"), vactDaySum, amount);
									}
								}
							}
						}*/
					}
				}
			}
		}
		
		
		if(resultCd.equals("0000")){
			VAUtil util = new VAUtil();
			
			String holderName = util.cut2(vactMap.getString("holderName"), 30);
			fbBean.setCompanyName(holderName);
			
			logger.info("holderName : {}", holderName);
		}
		
		logger.info("virtualAccnt :{}, resultCd :{}, resultMsg : {}", fbBean.getVirtualAccount(), resultCd, resultMsg);
		
		headerBean.setSpecCode("0910");
		headerBean.setTransactionTime(CommonUtil.getCurrentDate("yyyyMMddHHmmss"));
		headerBean.setBankResponseCode(resultCd);
		headerBean.setKsnetResponseCode(resultCd);
		

		String resData = headerBean.getTransaction() +fbBean.getTransaction();
		vactDAO.insertVactIO(headerBean,fbBean, fbBean.getTransaction(), resultCd, resultMsg);
		return resData.getBytes();
		
	}
	
	
	
	
	/**
	 * 타행이체 불능명세 통지 처리
	 * @param headerBean
	 * @return
	 */
	public byte[] getErrorProcess(FBHeaderBean headerBean){
		FB0400100Bean fb0400100Bean = new FB0400100Bean(headerBean.getTransactionIndex());
		//FBK_TRANS_MONEY 에서 검색 후 //FBK_TRANS_ERR 테이블에 INSERT 한다.
		//검색 가져오고 WHERE BANK_CD = headerBean.getNewBankCd() AND SEND_DATE = headerBean.getTransactionTime().substring(0,8) AND SEQ_NO = fb0400100Bean.getRootSpecNumber() 
		smsGw = new SmsGw();
		
		logger.info("타행이체 불능통지 확인 : [{}][{}]", fb0400100Bean.getRootSpecNumber(), fb0400100Bean.getReceiveAccount().trim());
		
		FirmTrxDAO transMoneyDAO = new FirmTrxDAO();
		long idx = transMoneyDAO.selectFirmIdx(fb0400100Bean.getRootSpecNumber());
		logger.info("타행이체 불능통지 TRANSFER_ERR INDEX : {}", idx);
		//인서트 하고 TRANSFER_ERR.ROOT_IDX 에 TRANSFER_MONEY IDX  를 넣는다.
		//logger.info("타행이체 불능통지 PG_FIRM_ERR INSERT : {}", new FirmErrDAO().insertFirmErr(idx, headerBean, fb0400100Bean));
		
		//TRANSFER_MONEY 업데이트
		logger.info("TRANSFER_MONEY PG_FIRM_TRX UPDATE TO ERROR = {}", transMoneyDAO.updateError(idx, fb0400100Bean.getErrorCode()));
		
		String trxId = transMoneyDAO.selectTrxId(idx);
		
		//타행이체 불능통지 실시간정산 거래 건 처리
		realTimeProc(trxId, fb0400100Bean.getErrorCode());
		
		//타행이체 불능통지 가상계좌 대행서비스 거래 건 처리
		vaPayOutProc(trxId, fb0400100Bean.getErrorCode());
		
		//타행이체 불능통지 충전정산 서비스 거래 건 처리
		chargeSettleProc(trxId, fb0400100Bean.getErrorCode());
		
		String msgBody = "KSNET 펌뱅킹 타행이체 불능통지 발생 [" + trxId + "] ";
		smsGw.sendMessage("0", "4", msgBody);
		
		headerBean.setSpecCode("0410");
		headerBean.setTransactionTime(CommonUtil.getCurrentDate("yyyyMMddHHmmss"));
		headerBean.setKsnetResponseCode("0000");
		headerBean.setBankResponseCode("0000");
		return (headerBean.getTransaction()+fb0400100Bean.getTransaction()).getBytes();
		
	}
	
	/**
	 * 타행이체 불능통지 시 실시간정산 거래 건 처리
	 * @param trxId
	 * @param errCd
	 */
	private void realTimeProc(String trxId, String errCd) {
		VactDAO vactDAO = new VactDAO();
		
		//타행이체 불능통지 거래 건이 실시간정산 거래 건일 경우
		if(vactDAO.updateRealTimePayOutRes(trxId, errCd)) {
			vactDAO.updatePayOutCancelCapUpdate(trxId);	
		}
	}
	
	/**
	 * 타행이체 불능통지 시 가상계좌 대행서비스 거래 건 처리
	 * @param idx
	 * @param errCd
	 */
	private void vaPayOutProc(String trxId, String errCd) {
		VactDAO vactDAO = new VactDAO();

		try {
			//타행이체 불능통지 거래 건이 가상계좌 대행서비스 거래 건일 경우
			if(vactDAO.updateVaPayOutRes(trxId, errCd)) {
				logger.info("타행이체 불능통지 거래 가상계좌 대행서비스 거래 건 확인 : [{}]", trxId);
				
				ResultSet vaPayData = vactDAO.getVaPay(trxId);
				
				SharedMap<String,Object> errData = getTrxErrData(vaPayData, errCd);
				vactDAO.insertTrxErr(errData);
				
				//실패거래건의 실출금액 조회
				long stlAmt = vactDAO.getStlAmt(vaPayData.getString("trxId"));
				//실패거래건의 실출금액 만큼 해당 계정의 이후 결제건의 잔액에 더해줌 
				vactDAO.updateVaBalance(vaPayData.getString("trxId"), vaPayData.getString("id"), stlAmt);
				//실패건 VA_TRX테이블에서 삭제
				vactDAO.deleteVaTrx(vaPayData.getString("trxId"));
			}
		}catch(Exception e) {
			logger.error("타행이체 불능통지 vaPayOutProc Error : [{}]", e.getMessage());
			e.getStackTrace();
		}	
	}
	
	/**
	 * 충전정산 망취소
	 * @param idx
	 * @param errCd
	 */
	private void chargeSettleCancel(String trxId) {
		VactDAO vactDAO = new VactDAO();
		
		try {
			logger.info("가상계좌 취소 충전정산 서비스 망취소 거래 건 확인 : [{}]", trxId);
			
			ResultSet chargeSettleData = vactDAO.getCancelChargeSettle(trxId);
			
			SharedMap<String,Object> errData = getChargeSettleErrData(chargeSettleData, "C");
			logger.info("가상계좌 취소 충전정산 서비스 PG_CHARGE_SETTLE_ERR INSERT : [{}][{}]", trxId, vactDAO.insertChargeErr(errData));
			
			//실패거래건의 실출금액 조회
			long netAmount = errData.getLong("netAmount");
			//실패거래건의 실출금액 만큼 해당 계정의 이후 결제건의 잔액에 더해줌 
			logger.info("가상계좌 취소 충전정산 서비스 잔액복구 : [{}][{}]", trxId, vactDAO.updateChargeSettleBalance(chargeSettleData.getString("trxId"), chargeSettleData.getString("mchtId"), netAmount));
			//실패건 PG_CHARGE_SETTLE 테이블에서 삭제
			logger.info("가상계좌 취소 충전정산 서비스 PG_CHARGE_SETTLE DELETE : [{}][{}]", trxId, vactDAO.deleteChargeSettle(chargeSettleData.getString("trxId")));
		}catch(Exception e) {
			logger.error("충전정산 서비스 가상계좌 입금 망취소 chargeSettleCancel Error : [{}]" + e.getMessage());
			e.getStackTrace();
		}	
	}
	
	/**
	 * 타행이체 불능통지 시 충전정산 서비스 거래 건 처리
	 * @param idx
	 * @param errCd
	 */
	private void chargeSettleProc(String trxId, String errCd) {
		VactDAO vactDAO = new VactDAO();
		
		try {
			//타행이체 불능통지 거래 건이 가상계좌 대행서비스 거래 건일 경우
			if(vactDAO.updateChargeSettleRes(trxId, errCd)) {
				logger.info("타행이체 불능통지 거래 충전정산 서비스 거래 건 확인 : [{}]" + trxId);
				
				ResultSet chargeSettleData = vactDAO.getChargeSettle(trxId);
				
				SharedMap<String,Object> errData = getChargeSettleErrData(chargeSettleData, "T");
				vactDAO.insertChargeErr(errData);
				
				//실패거래건의 실출금액 조회
				long netAmount = errData.getLong("netAmount");
				//실패거래건의 실출금액 만큼 해당 계정의 이후 결제건의 잔액에 더해줌 
				vactDAO.updateChargeSettleBalance(chargeSettleData.getString("trxId"), chargeSettleData.getString("mchtId"), netAmount);
				//실패건 PG_CHARGE_SETTLE 테이블에서 삭제
				vactDAO.deleteChargeSettle(chargeSettleData.getString("trxId"));
				// 출금결과 noti 발송
				String hookAddr = vactDAO.getchargeSettleHookAddr(errData.getString("mchtId"));
				if(!CommonUtil.isNullOrSpace(hookAddr)) {
					String payLoad = setPayLoad(errData, "출금실패");
					errData.put("payLoad", payLoad);
					errData.put("trxType", "출금");
					new ChargeSettleHook(hookAddr, errData, vactDAO, "0").start();
				}
			}
		}catch(Exception e) {
			logger.error("타행이체 불능통지 chargeSettleProc Error : [{}]" + e.getMessage());
			e.getStackTrace();
		}	
	}
	
	public long calcFee(long amount,double rate){
		rate = rateFormat(rate);
		long decimal = 10000;
		if(amount < 0){
			return -new Double(Math.round(-amount*(rate *decimal))).longValue()/decimal;
		}else{
			return new Double(Math.round(amount*(rate *decimal))).longValue()/decimal;
			
		}
	}
	
	public long calcVat(long amount){
		if(amount < 0){
			return -new Double(-amount *10 /100).longValue();
		}else{
			return new Double(amount *10 /100).longValue();
		}
	}
	
	public double rateFormat(double rate){
		String pattern = "#.#####";
		DecimalFormat format = new DecimalFormat(pattern);
		return new Double(format.format(rate)).doubleValue();
	}
	
	public String calcDay(VactDAO vactDAO , String settleType,String today){
		try {
			if(settleType.equals("D+0") || settleType.equals("C+0")) {
				return today;
			}
			int term = 1;
			if(settleType.startsWith("D")){
				term = CommonUtil.parseInt(settleType.replaceAll("D[+]", ""));
				String day =  vactDAO.getSettleDay(today, term);
				/*
				//오늘 정산 예정일이지만 8시 이후에 요청된 거래는 자동으로 내일로 정산일정이 밀린다.
				if(day.equals(currentDay) && CommonUtil.parseInt(CommonUtil.getCurrentDate("HH")) > 8){
					day =  trxDAO.getSettleDay(currentDay,1);
				}*/
				return day;
			}else if(settleType.startsWith("C")){
					term = CommonUtil.parseInt(settleType.replaceAll("C[+]", ""));
					String day =  vactDAO.getSettleDay(today, term);
					/*
				//오늘 정산 예정일이지만 8시 이후에 요청된 거래는 자동으로 내일로 정산일정이 밀린다.
				if(day.equals(currentDay) && CommonUtil.parseInt(CommonUtil.getCurrentDate("HH")) > 8){
					day =  trxDAO.getSettleDay(currentDay,1);
				}*/
					return day;
			}else if(settleType.startsWith("A")){
				term = CommonUtil.parseInt(settleType.replaceAll("A[+]", ""));
				String day = "";
				
				if(term == 0) {
					day = today;
					
					String status = vactDAO.getHolidayCheck(today);
					
					//휴일이면 다음영업일로 정산예정일 세팅
					if("yes".equals(status)) {
						day =  vactDAO.getSettleDay(today, 1);
					}else {
						//A+0은 당일정산으로 00~15시는 17시정산, 15~00시는 다음영업일 10시정산
						if(CommonUtil.parseInt(CommonUtil.getCurrentDate("HH")) >= 15){
							day =  vactDAO.getSettleDay(today, 1);
						}
					}
				}else {
					day = CommonUtil.getOpDate(GregorianCalendar.DATE,term,today);
				}

				return day;
			}else if(settleType.startsWith("B")){
				term = CommonUtil.parseInt(settleType.replaceAll("B[+]", ""));
				
				String day = CommonUtil.getOpDate(GregorianCalendar.DATE,term,today);
				return day;
			}else if(settleType.startsWith("M")){
				term = CommonUtil.parseInt(settleType.replaceAll("M[+]", ""));
				String nextMonth = CommonUtil.getOpDate(GregorianCalendar.MONTH,1,today).substring(0,6);
				return vactDAO.getSettleDay(nextMonth+CommonUtil.zerofill(term,2));
			}else if(settleType.startsWith("W")){	//주 정산 "W+1";\
				term = CommonUtil.parseInt(settleType.replaceAll("W[+]", ""));

				LocalDate localDate = LocalDate.parse(today, DateTimeFormatter.ofPattern("yyyyMMdd"));
				localDate = localDate.plusWeeks(1).with(DayOfWeek.MONDAY).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(term)));

				return vactDAO.getSettleDay(localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
				
			}else if(settleType.startsWith("2W")){	//주 정산 "2W+1";\
				term = CommonUtil.parseInt(settleType.replaceAll("2W[+]", ""));
				LocalDate localDate = LocalDate.parse(today, DateTimeFormatter.ofPattern("yyyyMMdd"));
				localDate = localDate.plusWeeks(2).with(DayOfWeek.MONDAY).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(term)));
				return vactDAO.getSettleDay(localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
				
			}else{
				return "";
			}
		}catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * VA_TRX_ERR 테이블에 넣을 데이터 세팅
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	private SharedMap<String,Object> getTrxErrData(ResultSet dataSet, String errCd) {
		VactDAO vactDAO = new VactDAO();
		SharedMap<String,Object> data = new SharedMap<String, Object>();
		
		try {
			long netFee = vactDAO.getPtnWithdrawFee(dataSet.getString("ptnId"));
			long netFeeVat = calcVat(netFee);
				
			data.put("trxId",dataSet.getString("trxId"));
			data.put("id",dataSet.getString("id"));
			data.put("ptnId",dataSet.getString("ptnId"));
			data.put("userId",dataSet.getString("userId"));
			data.put("trxType","출금");
			data.put("trxUnit","펌뱅킹");
			data.put("trxDay",dataSet.getString("trxDay"));
			data.put("trxTime",dataSet.getString("trxTime"));
			data.put("amount",dataSet.getLong("amount"));
			
			String regDate = CommonUtil.getCurrentDate("yyyyMMddHHmmss");
			data.put("feeType", "고정액");
			data.put("feeRate", "0");
			data.put("fee", netFee);
			data.put("feeVat", netFeeVat);
			
			//파트너 수수료는 일단 없으므로 0으로 세팅한다....
			data.put("ptnFeeRate", 0);
			data.put("ptnFee", 0);
			data.put("ptnFeeVat", 0);
			
			// 은행수수료 - 우리은행: 50, 타행:100
			if("020".equals(dataSet.getString("bankCd"))) {
				data.put("bankFee", 50);
			}else {
				data.put("bankFee", 100);
			}
			
			data.put("stlAmount", dataSet.getLong("amount") + data.getLong("fee") + data.getLong("feeVat"));
			data.put("balance", dataSet.getLong("balance") - dataSet.getLong("stlAmount"));
			data.put("trackId", "");
			data.put("refId", dataSet.getString("refId"));
			data.put("bankCd", dataSet.getString("bankCd"));
			data.put("account", dataSet.getString("account"));
			data.put("holder", dataSet.getString("holder"));
			data.put("regDay", regDate.substring(0, 8));
			data.put("resultCd", errCd);
			data.put("resultMsg", "타행이체 불능통지");
		}catch(Exception e) {
			e.getStackTrace();
			logger.error("타행이체 불능관련 가상계좌 대행서비스 출금오류 데이터 세팅 오류 : [{}]", e.getMessage());
		}
		
		return data;
	}
	
	/**
	 * PG_CHARGE_SETTLE_ERR 테이블에 넣을 데이터 세팅
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	private SharedMap<String,Object> getChargeSettleErrData(ResultSet dataSet, String type) {
		SharedMap<String,Object> data = new SharedMap<String, Object>();
		
		try {
			data.put("trxId",dataSet.getString("trxId"));
			data.put("mchtId",dataSet.getString("mchtId"));
			data.put("trxType",dataSet.getString("trxType"));
			data.put("trxUnit",dataSet.getString("trxUnit"));
			data.put("trxDay",dataSet.getString("trxDay"));
			data.put("trxTime",dataSet.getString("trxTime"));
			data.put("amount",dataSet.getLong("amount"));
			data.put("fee",dataSet.getLong("fee"));
			data.put("feeVat",dataSet.getLong("feeVat"));
			data.put("bankFee",dataSet.getLong("bankFee"));
			data.put("netAmount",dataSet.getLong("netAmount"));
			data.put("balance",dataSet.getLong("balance"));
			data.put("trackId",dataSet.getString("trackId"));
			data.put("refId",dataSet.getString("refId"));
			data.put("bankCd", dataSet.getString("bankCd"));
			data.put("bankName", dataSet.getString("bankName"));
			data.put("account", dataSet.getString("account"));
			data.put("holder", dataSet.getString("holder"));
			data.put("recordInfo", dataSet.getString("recordInfo"));
			
			if("T".equals(type)) {
				data.put("summary", "타행이체 불능통지로 인한 출금실패건");
			}else if("C".equals(type)) {
				data.put("summary", "가상계좌 입금 취소건");	
			}
			
			String regDate = CommonUtil.getCurrentDate("yyyyMMddHHmmss");
			data.put("regDay", regDate.substring(0, 8));
			data.put("regId", dataSet.getString("regId"));
			data.put("resultCd", dataSet.getString("resultCd"));
			data.put("resultMsg", dataSet.getString("resultMsg"));
		}catch(Exception e) {
			e.getStackTrace();
			logger.error("타행이체 불능관련 충전정산 서비스 출금오류 데이터 세팅 오류 : [{}]", e.getMessage());
		}
		
		return data;
	}
	
	 /*
     * 충전정산 노티 데이터 설정
     */
    private String setPayLoad(SharedMap<String, Object> sharedMap, String status){
		SharedMap<String, String> payLoadMap = new SharedMap<String, String>();
		
		payLoadMap.put("mchtId",sharedMap.getString("mchtId"));
		payLoadMap.put("trxId",sharedMap.getString("trxId"));
		payLoadMap.put("trxDay",sharedMap.getString("trxDay"));
		payLoadMap.put("trxTime",sharedMap.getString("trxTime"));
		payLoadMap.put("status",status);
		payLoadMap.put("trackId",sharedMap.getString("trackId"));
		payLoadMap.put("resultCd",sharedMap.getString("resultCd"));
		payLoadMap.put("resultMsg",sharedMap.getString("resultMsg"));
		payLoadMap.put("amount",sharedMap.getString("amount"));
		String payLoad = CommonUtil.toQueryString(payLoadMap,"UTF-8");
		return payLoad;
	}
}
