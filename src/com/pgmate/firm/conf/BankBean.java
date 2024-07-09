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

	public String kyc_api_key = ""; //더즌 KYC KEY
	public String kyc_org_code = ""; //더즌 KYC 코드

	//쿠콘 추가
	//펌뱅킹
	public String coocon_firm_key = "3MfRBIdeGBMHVrOcn5Rs";
	public String coocon_firm_code = "20034306";
	//1원인증
	public String coocon_accountAuth_key = "82faff531e0f0830d6a098a0c6c14f6c";
	public String coocon_accountAuth_code = "07070001";
	//kyc
	public String coocon_kyc_key = "wJTPdsfLZZ77wiKQtxGX";
	public String coocon_kyc_code = "04847711";
	//예금주조회
	public String coocon_name_key = "PbZpPBwIrutWKM13oj49";
	public String coocon_realname_key = "cr6YGqD57Xu2r8cSz4a7";

	
	public BankBean() {
		// TODO Auto-generated constructor stub
	}
		
}
