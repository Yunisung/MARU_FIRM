package com.pgmate.firm.server;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.dao.FirmDAO;
import com.pgmate.firm.main.Daemon;
import com.pgmate.lib.util.comm.TcpSocket;
import com.pgmate.lib.util.lang.CommonUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankProcessTest {
    BankProcess bankProcess;
    Firm firm;
    private static Logger logger = LoggerFactory.getLogger(BankProcess.class);

    @Before
    public void setup() throws Exception {
        firm = FirmLoader.getConfig();
        bankProcess = new BankProcess(firm);

        Daemon.main(null);
    }

    @Test
    public void PayIn() {
        logger.info("--- 가상계좌 입금 (0200/300) ---");

        //여기만 수정
        String virtualAccount = "70019000000094"; //가상계좌 번호
        String amount = "1004";
        String name = "bkwinners"; //입금한 사람 이름

        String mAccount = "70110001999557"; //모계좌번호
        String count = "";
        String classificationCode = "20"; //20: 입금, 30: 출금, 51: 입금취소
        String bankCode = "";
        String remainAmount = "";
        String giroCode = "";
        String supyoNumber = "";
        String cash = "";
        String otherBankSupyoAmount = "";
        String otherSupyo = "";
        String transactionTime = CommonUtil.getCurrentDate("yyyyMMddHHmmss");
        String transactionNumber = "";
        String newBankCode = "089"; //은행코드
        String branchCode = "";
        String extra = "";

        StringBuffer transaction = new StringBuffer();
        transaction.append(CommonUtil.byteFiller(mAccount,15));
        transaction.append(CommonUtil.zerofill(count,2));
        transaction.append(CommonUtil.byteFiller(classificationCode,2));
        transaction.append(CommonUtil.byteFiller(bankCode,2));
        transaction.append(CommonUtil.zerofill(amount,13));
        transaction.append(CommonUtil.zerofill(remainAmount,13));
        transaction.append(CommonUtil.zerofill(giroCode,6));
        transaction.append(CommonUtil.byteFiller(name,14));
        transaction.append(CommonUtil.byteFiller(supyoNumber,10));
        transaction.append(CommonUtil.zerofill(cash,13));
        transaction.append(CommonUtil.zerofill(otherBankSupyoAmount,13));
        transaction.append(CommonUtil.zerofill(otherSupyo,13));
        transaction.append(CommonUtil.byteFiller(virtualAccount,16));
        transaction.append(CommonUtil.byteFiller(transactionTime,14));
        transaction.append(CommonUtil.zerofill(transactionNumber,6));
        transaction.append(CommonUtil.byteFiller(newBankCode,3));
        transaction.append(CommonUtil.byteFiller(branchCode,7));
        transaction.append(CommonUtil.byteFiller(extra,38));

        //헤더 세팅
        String specNumber = FirmDAO.getSeqNO();

        StringBuffer header = new StringBuffer();
        header.append(CommonUtil.byteFiller("KSNETVR",9));
        header.append(CommonUtil.byteFiller("70019",8));
        header.append(CommonUtil.byteFiller(bankCode,2));
        header.append(CommonUtil.byteFiller("0200",4));
        header.append(CommonUtil.zerofill("300",3));
        header.append(CommonUtil.zerofill("1",1));
        header.append(CommonUtil.zerofill(specNumber,6));
        header.append(CommonUtil.byteFiller(transactionTime,14));
        header.append(CommonUtil.byteFiller("",4));
        header.append(CommonUtil.byteFiller("",4));
        header.append(CommonUtil.byteFiller("",8));
        header.append(CommonUtil.byteFiller("",6));
        header.append(CommonUtil.byteFiller("",15));
        header.append(CommonUtil.byteFiller(newBankCode,3));
        header.append(CommonUtil.byteFiller(extra,13));


        String head = header.toString();
        String body = transaction.toString();
        String msg = head+body;
        logger.info("=> [" + msg + "]");

        byte[] recv = bankProcess.execute(msg.getBytes());
        logger.info("<= ["+recv+"]");
        String result = new String(recv);
        logger.info("<= ["+result+"]");
    }


    @Test
    public void PayOut() {
        logger.info("--- 가상계좌 입금취소 (0200/300) ---");

        //여기만 수정정
        String virtualAccount = "70019000000094"; //가상계좌 번호
        String amount = "1004"; //취소금액
        String inquiryDay = "20221102"; //입금날짜
        String inqueryNumber = "000239"; //입금했던 seqNo

        String mAccount = "70110001999557"; //모계좌번호
        String count = "";
        String classificationCode = "51"; //20: 입금, 30: 출금, 51: 입금취소
        String bankCode = ""; //사용안함
        String remainAmount = "";
        String giroCode = ""; //사용안함
        String name = ""; //입금한 사람 이름
        String supyoNumber = "";
        String cash = "";
        String otherBankSupyoAmount = "";
        String otherSupyo = "";
        String transactionTime = CommonUtil.getCurrentDate("yyyyMMddHHmmss");
        String transactionNumber = "";
        String newBankCode = "089"; //은행코드
        String branchCode = "";
        String extra = "";

        //헤더 세팅
        String specNumber = FirmDAO.getSeqNO();

        StringBuffer header = new StringBuffer();
        header.append(CommonUtil.byteFiller("KSNETVR",9));
        header.append(CommonUtil.byteFiller("70019",8));
        header.append(CommonUtil.byteFiller(bankCode,2));
        header.append(CommonUtil.byteFiller("0200",4));
        header.append(CommonUtil.zerofill("300",3));
        header.append(CommonUtil.zerofill("1",1));
        header.append(CommonUtil.zerofill(specNumber,6));
        header.append(CommonUtil.byteFiller(transactionTime,14));
        header.append(CommonUtil.byteFiller("",4));
        header.append(CommonUtil.byteFiller("",4));
        header.append(CommonUtil.byteFiller(inquiryDay,8));
        header.append(CommonUtil.byteFiller(inqueryNumber,6));
        header.append(CommonUtil.byteFiller("",15));
        header.append(CommonUtil.byteFiller(newBankCode,3));
        header.append(CommonUtil.byteFiller(extra,13));

        StringBuffer transaction = new StringBuffer();
        transaction.append(CommonUtil.byteFiller(mAccount,15));
        transaction.append(CommonUtil.zerofill(count,2));
        transaction.append(CommonUtil.byteFiller(classificationCode,2));
        transaction.append(CommonUtil.byteFiller(bankCode,2));
        transaction.append(CommonUtil.zerofill(amount,13));
        transaction.append(CommonUtil.zerofill(remainAmount,13));
        transaction.append(CommonUtil.zerofill(giroCode,6));
        transaction.append(CommonUtil.byteFiller(name,14));
        transaction.append(CommonUtil.byteFiller(supyoNumber,10));
        transaction.append(CommonUtil.zerofill(cash,13));
        transaction.append(CommonUtil.zerofill(otherBankSupyoAmount,13));
        transaction.append(CommonUtil.zerofill(otherSupyo,13));
        transaction.append(CommonUtil.byteFiller(virtualAccount,16));
        transaction.append(CommonUtil.byteFiller(transactionTime,14));
        transaction.append(CommonUtil.zerofill(transactionNumber,6));
        transaction.append(CommonUtil.byteFiller(newBankCode,3));
        transaction.append(CommonUtil.byteFiller(branchCode,7));
        transaction.append(CommonUtil.byteFiller(extra,38));


        String head = header.toString();
        String body = transaction.toString();
        String msg = head+body;
        logger.info("=> [" + msg + "]");

        byte[] recv = bankProcess.execute(msg.getBytes());
        logger.info("<= ["+recv+"]");
        String result = new String(recv);
        logger.info("<= ["+result+"]");
    }

    @Test
    public void SearchVact() {
        logger.info("---가상계좌 수취조회---");
        String virtualAccount = "70019000000008";
        String companyName = "";
        String bankCode = "89";
        String startDay = "";
        String endTime = "";
        String amount = "1004";
        String classificationCode = "";
        String transactionType = "10"; //10 : 수취, 20: 입금, 51: 취소
        String requestorName = "test";
        String newBankCode = "";
        String extra = "";

        StringBuffer transaction = new StringBuffer();
        transaction.append(CommonUtil.byteFiller(virtualAccount,16));
        transaction.append(CommonUtil.byteFiller(companyName,30));
        transaction.append(CommonUtil.byteFiller(bankCode,2));
        transaction.append(CommonUtil.byteFiller(startDay,8));
        transaction.append(CommonUtil.byteFiller(endTime,14));
        transaction.append(CommonUtil.zerofill(amount,13));
        transaction.append(CommonUtil.byteFiller(classificationCode,2));
        transaction.append(CommonUtil.byteFiller(transactionType,2));
        transaction.append(CommonUtil.byteFiller(requestorName,20));
        transaction.append(CommonUtil.byteFiller(newBankCode,3));
        transaction.append(CommonUtil.byteFiller(extra,90));

        String head = "KSNETVR  70019     0900100130000120221101141323                                     089             ";
        String body = transaction.toString();
        String msg = head+body;
        logger.info("=> [" + msg + "]");

        byte[] recv = bankProcess.execute(msg.getBytes());
        String result = new String(recv);
        logger.info("<= ["+result+"]");
    }
}
