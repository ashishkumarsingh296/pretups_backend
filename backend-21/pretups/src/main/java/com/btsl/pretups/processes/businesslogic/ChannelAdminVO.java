package com.btsl.pretups.processes.businesslogic;

/**
 * @(#)ChannelAdminVO.java
 *                         Copyright(c) 2014, Comviva technologies Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         -----------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         -----------------------------------------
 *                         Diwakar Jan 14 2014 Initial Creation
 *                         This VO class will be used store the details related
 *                         to SendSMSToChannelAdmin4HourlyTransDAO.
 * 
 */
import java.io.Serializable;

public class ChannelAdminVO implements Serializable, Comparable {
    private static final long serialVersionUID = -8824134008423321350L;
    private String _msisdn;
    private String _networkcode;
    // 11-MAr-2014
    private String _userId;
    private String _emailId;

    // Ended Here

    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            the msisdn to set
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return the networkcode
     */
    public String getNetworkcode() {
        return _networkcode;
    }

    /**
     * @param networkcode
     *            the networkcode to set
     */
    public void setNetworkcode(String networkcode) {
        _networkcode = networkcode;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(String userId) {
        _userId = userId;
    }

    /**
     * @return the emailId
     */
    public String getEmailId() {
        return _emailId;
    }

    /**
     * @param emailId
     *            the emailId to set
     */
    public void setEmailId(String emailId) {
        _emailId = emailId;
    }

}
