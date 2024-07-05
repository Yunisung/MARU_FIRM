package com.pgmate.firm.coocon;

import java.util.List;

public class CooconResBean {
    private String RSLT_CD;
    private String RSLT_MSG;
    private List RESP_DATA;

    public String getRSLT_MSG() {
        return RSLT_MSG;
    }

    public String getRSLT_CD() {
        return RSLT_CD;
    }

    public List getRESP_DATA() {
        return RESP_DATA;
    }
}
