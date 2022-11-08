package com.pgmate.firm.server;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.server.executor.InterExcuter;
import com.pgmate.firm.server.executor.InterHyphenExcuter;
import com.pgmate.firm.server.executor.InterKsnetExcuter;
import com.pgmate.firm.util.HyphenComm;
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

		if(firmBean.resultCd.equals("9999")){
			return GsonUtil.toJson(firmBean);
		}else{
			if(firmBean.msgType.startsWith("0800")){
				firmBean = interExcuter.proc0800(firmBean);
			}else if(firmBean.msgType.startsWith("0600300")){
				//�ܾ���ȸ
				firmBean = interExcuter.proc0600300(firmBean);
			}else if(firmBean.msgType.startsWith("0600400")){
				//������ȸ
				firmBean = interExcuter.proc0600400(firmBean);
			}else if(firmBean.msgType.startsWith("0700100")){
				//����
				firmBean = interExcuter.proc0700100(firmBean);
			}else if(firmBean.msgType.startsWith("0100100")){
				//��ü
				firmBean = interExcuter.proc0100100(firmBean);
			}else if(firmBean.msgType.startsWith("0600101")){
				//ó�������ȸ
				firmBean = interExcuter.proc0600101(firmBean);
			}else if(firmBean.msgType.startsWith("0900400")){
				//������� ������� ���
				firmBean = interExcuter.proc0900400(firmBean);
			}
		}


		return GsonUtil.toJson(firmBean);

	}

	public FirmBean valid(FirmBean firmBean){
		if(firmBean == null){
			return formatError(firmBean,"�޼��� ���� ����");
		}else{
			if(firmBean.msgType.length() !=7){
				return formatError(firmBean,"msgType ���� :"+firmBean.msgType);
			}
			if(firm.bank.get(firmBean.bankCd) == null){
				return formatError(firmBean,"bankCd ���� , �������� �ʴ� �����ڵ��Դϴ�. "+firmBean.bankCd);
			}
			if(firmBean.userId.equals("")){
				return formatError(firmBean,"userId ���� , userId �� �ʼ����Դϴ�. ");
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
		firmBean.resultMsg = "�޼��� ���� ����";
		return firmBean;
	}
}
