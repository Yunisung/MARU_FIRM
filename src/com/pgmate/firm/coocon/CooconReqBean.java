package com.pgmate.firm.coocon;

import java.util.ArrayList;
import java.util.List;

public class CooconReqBean {
    protected String SECR_KEY;      // 고객사 API KEY
    protected String KEY;           //  API 명
    protected List REQ_DATA;        // API 요청데이터

    public CooconReqBean(CooconBaseBean cooconBaseBean) {
        this.REQ_DATA = new ArrayList();
        this.REQ_DATA.add(cooconBaseBean);
    }

    public String getSECR_KEY() {
        return SECR_KEY;
    }

    public void setSECR_KEY(String SECR_KEY) {
        this.SECR_KEY = SECR_KEY;
    }

    public String getKEY() {
        return KEY;
    }

    public void setKEY(String KEY) {
        this.KEY = KEY;
    }

    public List getREQ_DATA() {
        return REQ_DATA;
    }

    public void setREQ_DATA(List REQ_DATA) {
        this.REQ_DATA = REQ_DATA;
    }
}
