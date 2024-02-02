package com.pgmate.firm.server;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.server.executor.*;
import com.pgmate.lib.util.gson.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 *
 */
public class InterProcess implements java.io.Serializable{

	private Logger logger = LoggerFactory.getLogger( getClass() );
	private Firm firm = null;

	public InterProcess(Firm firm) {
		this.firm = firm;
	}

	public String execute(String json){
		FirmBean firmBean = (FirmBean)GsonUtil.fromJson(json, FirmBean.class);

		firmBean = valid(firmBean);

		InterExcuter interExcuter = new InterKsnetExcuter(firm);
//		InterExcuter interExcuter = new InterHyphenExcuter(firm);
		InterExcuter interHyphenFirmExcuter = new InterHyphenFirmExcuter(firm);
		InterExcuter doznExcuter = new DoznExcuter(firm);

		if(firmBean.resultCd.equals("9999")){
			return GsonUtil.toJson(firmBean);
		}else{
			if(firmBean.bankCd.equals("034") || firmBean.bankCd.equals("007")) {
				//더즌은 따로 예외처리
				if(firmBean.msgType.startsWith("0600300")) {
					//잔액조회
					firmBean = doznExcuter.proc0600300(firmBean);
				}else if(firmBean.msgType.startsWith("0600400")){
					//성명조회
					firmBean = doznExcuter.proc0600400(firmBean);
				}else if(firmBean.msgType.startsWith("0700100")){
					//집계
					firmBean = doznExcuter.proc0700100(firmBean);
				}else if(firmBean.msgType.startsWith("0100100")){
					//이체
					firmBean = doznExcuter.proc0100100(firmBean);
				}else if(firmBean.msgType.startsWith("0600101")){
					//처리결과조회
					firmBean = doznExcuter.proc0600101(firmBean);
				}else if(firmBean.msgType.startsWith("0900400")){
					//가상계좌 출금정보 등록
					firmBean = doznExcuter.proc0900400(firmBean);
				}else if(firmBean.msgType.startsWith("ARSAUTH")) {
					//ARS 인증
					firmBean = doznExcuter.procArsAuth(firmBean);
				}else if(firmBean.msgType.startsWith("0600102")){
					//이체 재시도
					firmBean = doznExcuter.proc0600102(firmBean);
				}else if(firmBean.msgType.startsWith("ACCAUTH")) {
					//계좌점유인증(1원인증)
					firmBean = doznExcuter.procAccAuth(firmBean);
				}else if(firmBean.msgType.startsWith("ARSCHCK")) {
					//ARS인증 체크
					firmBean = doznExcuter.procArsChck(firmBean);
				}else if(firmBean.msgType.startsWith("ACCCHCK")) {
					firmBean = doznExcuter.procAccChck(firmBean);
				}

			} else {
				if(firmBean.msgType.startsWith("0800")){
					firmBean = interExcuter.proc0800(firmBean);
				}else if(firmBean.msgType.startsWith("0600300")){
					//잔액조회
					if(firmBean.bankCd.equals("089")) {
						firmBean = interExcuter.proc0600300(firmBean);
					} else {
						//하이픈 대행 사용
						firmBean = interHyphenFirmExcuter.proc0600300(firmBean);
					}
				}else if(firmBean.msgType.startsWith("0600400")){
					//성명조회
					firmBean = interHyphenFirmExcuter.proc0600400(firmBean);
				}else if(firmBean.msgType.startsWith("0700100")){
					//집계
					firmBean = interExcuter.proc0700100(firmBean);
				}else if(firmBean.msgType.startsWith("0100100")){
					//이체
					if(firmBean.bankCd.equals("089")) {
						firmBean = interExcuter.proc0100100(firmBean);
					} else {
						//하이픈 대행 사용
						firmBean = interHyphenFirmExcuter.proc0100100(firmBean);
					}
				}else if(firmBean.msgType.startsWith("0600101")){
					//처리결과조회
					if(firmBean.bankCd.equals("089")) {
						firmBean = interExcuter.proc0600101(firmBean);
					} else {
						//하이픈 대행 사용
						firmBean = interHyphenFirmExcuter.proc0600101(firmBean);
					}
				}else if(firmBean.msgType.startsWith("0900400")){
					//가상계좌 출금정보 등록
					firmBean = interExcuter.proc0900400(firmBean);
				}else if(firmBean.msgType.startsWith("ARSAUTH")) {
					//ARS 인증
					firmBean = interHyphenFirmExcuter.procArsAuth(firmBean);
				}else if(firmBean.msgType.startsWith("0600102")){
					//이체 재시도
					firmBean = interExcuter.proc0600102(firmBean);
				}
			}
		}


		return GsonUtil.toJson(firmBean);

	}

	public FirmBean valid(FirmBean firmBean){
		if(firmBean == null){
			return formatError(firmBean,"메세지 포맷 오류");
		}else{
			if(firmBean.msgType.length() !=7){
				return formatError(firmBean,"msgType 오류 :"+firmBean.msgType);
			}
			if(firm.bank.get(firmBean.bankCd) == null){
				return formatError(firmBean,"bankCd 오류 , 지원하지 않는 은행코드입니다. "+firmBean.bankCd);
			}
			if(firmBean.userId.equals("")){
				return formatError(firmBean,"userId 오류 , userId 는 필수값입니다. ");
			}
			firmBean.resultCd = "";
		}
		return firmBean;
	}


	public FirmBean formatError(FirmBean firmBean,String resultMsg){
		if(firmBean == null){
			firmBean = new FirmBean();
		}
		firmBean.resultCd = "9999";
		firmBean.resultMsg = "메세지 포맷 오류";
		return firmBean;
	}
}
