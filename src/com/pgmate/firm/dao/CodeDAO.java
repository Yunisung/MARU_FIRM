package com.pgmate.firm.dao;

import com.pgmate.app.util.CPUtil;
import com.pgmate.lib.dao.DAO;
import com.pgmate.lib.util.map.SharedMap;

public class CodeDAO extends DAO{

	private static final String TABLE = "PG_CODE";
	private static final String COLUMNS = "`idx`, `alias`, `code`, `codeName`";
	
	public CodeDAO() {
		super(TABLE,CPUtil.CP_DEBUG);
		super.setColumns(CodeDAO.COLUMNS);
	}
	
	public String getInfoBankSmsKey() {
		String smsKey = "";
		super.setTable("PG_SMS_TOKEN");
		super.setColumns("*");
		SharedMap<String, Object> map = super.search().getRowFirst();
		smsKey = map.getString("schema") + " " + map.getString("accessToken");
		super.initRecord();
		return smsKey;
	}
}
