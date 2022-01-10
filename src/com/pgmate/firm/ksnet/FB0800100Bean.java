/* 
 * Project Name : 
 * Project      : 
 * File Name    : FB0800100Bean.java
 * Date	        : Jul 15, 2008
 * Version      : 1.0
 * Author       : 
 * Comment      :  
 */

package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;

public class FB0800100Bean extends CommBean{
	
	public FB0800100Bean(){
		
	}
	
	public FB0800100Bean(String transaction){
		this(transaction.getBytes());
	}
	
	public FB0800100Bean(byte[] transaction){
		super.rootTransaction = transaction;
	}
	
	public String getTransaction(){
		return CommonUtil.setFiller(200);
	}
	
}
