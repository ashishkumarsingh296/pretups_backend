package com.btsl.pretups.processes;

public class HandleUnsettledCombinedFailCasesLogger {

	/**
     * ensures no instantiation
     */
    private HandleUnsettledCombinedFailCasesLogger(){
    	
    } 
    public static void log(String p_recordNumber, String p_transactionID, String p_transactionStatus, String p_otherInfo) {
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("recordNumber: " + p_recordNumber);
        strBuff.append("transactionID: " + p_transactionID);
        strBuff.append("transactionStatus: " + p_transactionStatus);
        strBuff.append("otherInfo: " + p_otherInfo);

    }

}
