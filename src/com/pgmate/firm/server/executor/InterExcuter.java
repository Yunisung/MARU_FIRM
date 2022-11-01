package com.pgmate.firm.server.executor;

import com.pgmate.firm.inter.FirmBean;

public interface InterExcuter {

    FirmBean proc0800(FirmBean firmBean);

    /**
     * 잔액조회
     * @param firmBean
     * @return
     */
    FirmBean proc0600300(FirmBean firmBean);

    /**
     * 성명조회
     * @param firmBean
     * @return
     */
    FirmBean proc0600400(FirmBean firmBean);

    /**
     * 집계
     * @param firmBean
     * @return
     */
    FirmBean proc0700100(FirmBean firmBean);

    /**
     * 송금이체
     * proc0100100
     * @param firmBean
     * @return
     */
    FirmBean proc0100100(FirmBean firmBean);

    /**
     * 처리결과조회
     * @param firmBean
     * @return
     */
    FirmBean proc0600101(FirmBean firmBean);

    /**
     * 가상계좌 출금정보 등록
     * @param firmBean
     * @return
     */
    FirmBean proc0900400(FirmBean firmBean);

}
