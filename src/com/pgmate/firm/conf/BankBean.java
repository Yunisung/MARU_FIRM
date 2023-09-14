package com.pgmate.firm.conf;

/**
 * @author Administrator
 *
 */
public class BankBean {
	public String trCd		= "";	
	public String compCd	= "";	
	public String bankCd	= "";	
	public String account	= "";	
	public String newAccount	= "";	

	//PYS : 하이픈 때문에 추가
	public String ekey		= "";
	public String msalt 	= "";
	public String kscode	= "";

	//230104_PYS : 계좌인증때문에 추가
	public String auth_key	= "";
	public String fcs_cd = "";

	//230405_PYS : 경남은행추가
	public String firmBankCode = "";

	//230911_PYS : 더즌 추가
	public String api_key = ""; //더즌에서 발급한 KEY
	public String org_code = ""; //더즌에서 발급한 코드
	public String crypto = ""; //더즌 암호화 유무
	
	public BankBean() {
		// TODO Auto-generated constructor stub
	}
		
}
