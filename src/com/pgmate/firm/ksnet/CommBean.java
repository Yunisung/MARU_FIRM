
package com.pgmate.firm.ksnet;

public class CommBean {

	public byte[] rootTransaction 	= null;
	private long	index				= 0;
	private String procType		= "";
	private String procId		= "";
	private String message	= "";
	private String transactionIndex = "";
	
	
	public CommBean(){	
	}

	public byte[] getRootTransaction() {
		return rootTransaction;
	}

	public void setRootTransaction(byte[] rootTransaction) {
		this.rootTransaction = rootTransaction;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	/**
	 * @return the procType
	 */
	public String getProcType() {
		return procType;
	}

	/**
	 * @param procType the procType to set
	 */
	public void setProcType(String procType) {
		this.procType = procType;
	}

	/**
	 * @return the procId
	 */
	public String getProcId() {
		return procId;
	}

	/**
	 * @param procId the procId to set
	 */
	public void setProcId(String procId) {
		this.procId = procId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the transactionIndex
	 */
	public String getTransactionIndex() {
		return transactionIndex;
	}

	/**
	 * @param transactionIndex the transactionIndex to set
	 */
	public void setTransactionIndex(String transactionIndex) {
		this.transactionIndex = transactionIndex;
	}

	

	
	
	
}
