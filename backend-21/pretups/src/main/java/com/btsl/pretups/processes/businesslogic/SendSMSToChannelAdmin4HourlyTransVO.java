package com.btsl.pretups.processes.businesslogic;

/**
 * @(#)SendSMSToChannelAdmin4HourlyTransVO.java
 *                                              Copyright(c) 2014, Comviva
 *                                              technologies Ltd.
 *                                              All Rights Reserved
 * 
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              -----------------------------
 *                                              Author Date History
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              -----------------------------
 *                                              Diwakar Jan 13 2014 Initial
 *                                              Creation
 *                                              This VO class will be used store
 *                                              the details related to
 *                                              SendSMSToChannelAdmin4HourlyTransDAO
 *                                              .
 * 
 */
import java.io.Serializable;

public class SendSMSToChannelAdmin4HourlyTransVO implements Serializable, Comparable {

    private static final long serialVersionUID = -8824134008423321350L;
    private String _domainName;
    private String _domainCode;// 11-MAR-2014
    private String _txnType;
    private String _trfType;
    private String _txnCount;
    private String _txnAmount;
    private String _networkCode; // 25-MAR-2014

    /**
     * @return the _txnType
     */
    public String getTxnType() {
        return _txnType;
    }

    /**
     * @param type
     *            the _txnType to set
     */
    public void setTxnType(String type) {
        _txnType = type;
    }

    /**
     * @return the _trfType
     */
    public String getTrfType() {
        return _trfType;
    }

    /**
     * @param type
     *            the _trfType to set
     */
    public void setTrfType(String type) {
        _trfType = type;
    }

    /**
     * @return the _txnCount
     */
    public String getTxnCount() {
        return _txnCount;
    }

    /**
     * @param count
     *            the _txnCount to set
     */
    public void setTxnCount(String count) {
        _txnCount = count;
    }

    /**
     * @return the _txnAmount
     */
    public String getTxnAmount() {
        return _txnAmount;
    }

    /**
     * @param amount
     *            the _txnAmount to set
     */
    public void setTxnAmount(String amount) {
        _txnAmount = amount;
    }

    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @return the _domainName
     */
    public String getDomainName() {
        return _domainName;
    }

    /**
     * @param name
     *            the _domainName to set
     */
    public void setDomainName(String name) {
        _domainName = name;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String code) {
        _domainCode = code;
    }

    /**
     * @return the networkCode
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            the networkCode to set
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

}
