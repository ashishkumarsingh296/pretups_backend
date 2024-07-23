package com.btsl.pretups.channel.logging;

	/*
	 * @(#)UserLoanRequestProcessLogger.java
	 * Name Date History
	 * ------------------------------------------------------------------------
	 * Akanksha 18/10/2021 Initial Creation
	 * ------------------------------------------------------------------------
	 * Copyright (c) 2021 Bharti Telesoft Ltd.
	 * Class for logging all the balance related Logs for user loan
	 */

	import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserLoanVO;

	public class UserLoanRequestProcessLogger {

	    private static Log log = LogFactory.getFactory().getInstance(UserLoanRequestProcessLogger.class.getName());

	    public static void log(UserLoanVO userLoanVO, String msisdn,String reqType) {
	        final String METHODNAME = "log";
	        final StringBuilder strBuff = new StringBuilder();
	        try {
	            strBuff.append(" [ UserID :" + userLoanVO.getUser_id()+ "]");
	            strBuff.append(" [ MSISDN :" + msisdn+ "]");
	            if("L".equals(reqType)) {
	            strBuff.append(" [ Loan Given :" + userLoanVO.getLoan_given()+ "]");
	            strBuff.append(" [ Loan Given Amount :" + PretupsBL.getDisplayAmount(userLoanVO.getLoan_given_amount())+ "]");
	            strBuff.append(" [ Loan Threshold Amount :" + PretupsBL.getDisplayAmount(userLoanVO.getLoan_threhold())+ "]");
	            strBuff.append(" [ Loan Given On :" + userLoanVO.getLast_loan_date()+ "]");
	            strBuff.append(" [ Loan Txn ID :" + userLoanVO.getLast_loan_txn_id()+ "]");
	           }
	            else if("S".equals(reqType)) {
	            strBuff.append(" [ Settlement Date :" + userLoanVO.getSettlement_date()+ "]");
	            strBuff.append(" [ Premium Amount :" + userLoanVO.getSettlement_loan_interest()+ "]");
	            strBuff.append(" [ Total loan due amount :" + userLoanVO.getSettlement_loan_amount()+ "]");
	            }
	            
	            
	            log.info("", strBuff.toString());
	        } catch (Exception e) {
	            log.errorTrace(METHODNAME, e);
	            log.error("log", msisdn, " Exception :" + e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanRequestProcessLogger[log]", msisdn,
	                "", "", "Not able to log info getting Exception=" + e.getMessage());
	        }
	    }
	}


