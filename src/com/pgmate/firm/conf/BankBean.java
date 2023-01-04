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
	
	public BankBean() {
		// TODO Auto-generated constructor stub
	}
		
}
