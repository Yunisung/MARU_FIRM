package com.pgmate.firm.util;

import com.pgmate.lib.util.lang.CommonUtil;

/**
 * @author Administrator
 *
 */
public class WooriSign {

	
	public WooriSign() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static String getSign(String account,String amount,String bankCode,String MAccount){
		
		String sign ="";
		
		String transactionDate 	= CommonUtil.getCurrentDate("yyMMdd");
		
		
		String signTemp = transactionDate + rightZeroFill(account,15) + CommonUtil.zerofill(amount,13) 
		       + CommonUtil.zerofill(bankCode,3) + rightZeroFill(MAccount,15);
		
		int fSign = 0;
		for(int i=0 ; i< signTemp.length();i++){
			fSign += Character.getNumericValue(signTemp.charAt(i));
		}
		
		sign = CommonUtil.zerofill(fSign,3)+CommonUtil.zerofill(CommonUtil.parseInt(amount) % fSign,3);

		return sign;
		
	}
	
	public static String rightZeroFill(String str,int len){
		int strLen = str.length();
		
		if(len < strLen){
			return str.substring(0,len);
		}else if(len == strLen){
			return str;
		}else{
			String temp = "";
			for(int i=0;i< (len-strLen) ; i++){
				temp += "0";
			}
			return str+temp;
		}
	}

}
