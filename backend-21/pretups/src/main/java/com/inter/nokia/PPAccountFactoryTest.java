package com.inter.nokia;

import java.util.HashMap;

/**
 * @(#)PPAccountFactoryTest
 *                          Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                          All Rights Reserved
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Ashish Kumar Jan 9, 2007 Initial Creation
 *                          ----------------------------------------------------
 *                          --------------------------------------------
 *                          This is class is used to simulate the
 *                          PPAccountFactory, remote object for Nokia-CORBA
 *                          interface.
 * 
 */
public class PPAccountFactoryTest {
    public PPAccountFactoryTest() {

    }

    public void getSubscriptionInfo(String pi_subscriberId, char pi_suppressOffFirstCall, byte po_statusInd, long po_balance, String po_lastCallDate, long po_lastCallCost, long po_expiryInd, String po_NCEdate, String po_CEdate, String po_NSEdate, String po_SEdate, long po_subscriberLanguage, long po_rechargeAllowed, long po_secondsBeforeClearing, long po_firstCallIndicator, long po_provisionId, long po_scpId, long po_providerId, String po_latestStateChange, long po_expiryServiceId, long po_profileId, String po_firstCallDate, HashMap p_map) {

        try {
            p_map.put("_statusInd", "0");
            // po_statusInd="0";
            p_map.put("_balance", "2000");
            p_map.put("_NCEdate", "20-JAN-07");
            p_map.put("_CEdate", "20-JAN-07");
            p_map.put("_NSEdate", "30-JAN-07");
            p_map.put("_SEdate", "30-JAN-07");
            p_map.put("_subscriberLanguage", "1");
            p_map.put("_rechargeAllowed", "1");// 1 means recharged allowed and
                                               // 0 means not allowed because of
                                               // max failed reached.
            p_map.put("_firstCallIndicator", "1");// 1 means it was not first
                                                  // call and 0 means it was a
                                                  // first call
            p_map.put("_profileId", "1"); // The subscriber’s prepaid expiry
                                          // profile identifier. Default value
                                          // is 0.
            p_map.put("_expiryServiceId", "1");// The subscriber’s prepaid
                                               // expiry service identifier.
                                               // Default value is 0.

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    }
}
