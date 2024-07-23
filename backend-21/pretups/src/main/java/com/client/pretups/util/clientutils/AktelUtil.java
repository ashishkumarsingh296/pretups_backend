/**
 * @(#)AktelUtil.java
 *                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                    All Rights Reserved
 * 
 *                    <description>
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    avinash.kamthan Aug 5, 2005 Initital Creation
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.Constants;

public class AktelUtil extends OperatorUtil {
    /**
     * Field _log.
     */

    /**
     * This method will convert operator specific msisdn to system specific
     * msisdn.
     * 
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     */
    public String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException {
        String msisdn = super.getSystemFilteredMSISDN(p_msisdn);
        if (msisdn.length() < 10) {
            msisdn = "0" + msisdn;
        }
        return msisdn;
    }

    /**
     * This method will convert system specific msisdn to operater specific
     * msisdn
     * 
     * @param p_msisdn
     * @return
     */
    public String getOperatorFilteredMSISDN(String p_msisdn) {
        if (p_msisdn.length() == 10 && p_msisdn.startsWith("0")) {
            p_msisdn = p_msisdn.substring(1);
        }
        return Constants.getProperty("COUNTRY_CODE") + p_msisdn;
    }

    /**
     * Method processPostBillPayment.
     * 
     * @param p_requestedAmt
     *            long
     * @param p_prevBal
     *            long
     * @return boolean
     */
    // This is commented during migration from Pretups5002 to Pretups521,
    // because in Aktel OmaUtil was running and this method was not implemented
    // in that.
    // Now AktelUtil is running.

    /*
     * public boolean processPostBillPayment(long p_requestedAmt,long p_prevBal)
     * {
     * boolean processBillPay=true;
     * if(p_requestedAmt > p_prevBal)
     * processBillPay=false;
     * return processBillPay;
     * }
     */

}
