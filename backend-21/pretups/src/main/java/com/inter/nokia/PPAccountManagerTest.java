/*
 * Created on Jan 9, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.nokia;

import java.util.Properties;

import com.btsl.common.BTSLBaseException;

/**
 * @(#)PPAccountManagerTest
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
 *                          PPAccountManager, remote object for Nokia-CORBA
 *                          interface.
 * 
 */
public class PPAccountManagerTest {
    byte a = 3;
    byte b;

    public void printValue() {
        b = (byte) (a + (byte) 10);
        System.out.println("b:" + b);
    }

    public void setAccountInfo(String pi_msisdn, Properties po_commonProps) throws BTSLBaseException {
        int size = 0;
        try {
            size = po_commonProps.size();
            if (size == 2)
                chargeRequest(po_commonProps);
            else if (size == 6)
                rechargeRequest(po_commonProps);
            else
                throw new BTSLBaseException("UNKNOWN METHOD INVOCATION ACCESS");

        } catch (Exception e) {
            throw new BTSLBaseException("CORBA EXCEPTION");
        }

    }

    private void chargeRequest(Properties p_commProp) throws BTSLBaseException {
        String newValidity = "20-JAN-07";
        String newGrace = "30-JAN-07";
        String newBalance = "2000";
        String accStatus = "0";
        try {
            p_commProp.setProperty("NCEDate", newValidity);
            p_commProp.setProperty("NSEDate", newGrace);
            p_commProp.setProperty("Balance", newBalance);
            p_commProp.setProperty("Status", accStatus);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("CORBA EXCEPTION");
        }
    }

    private void rechargeRequest(Properties p_commProp) throws BTSLBaseException {
        String newBalance = "20000";
        String accStatus = "0";
        try {
            p_commProp.setProperty("Balance", newBalance);
            p_commProp.setProperty("Status", accStatus);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("CORBA EXCEPTION");
        }
    }

    public static void main(String[] args) {
        PPAccountManagerTest pp = new PPAccountManagerTest();
        pp.printValue();
    }
}
