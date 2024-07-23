package com.selftopup.pretups.inter.module;

/**
 * @(#)InterfaceModuleVO.java
 *                            Copyright(c) 2005, Bharti Telesoft Int. Public
 *                            Ltd.
 *                            All Rights Reserved
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Abhijit Chauhan June 18,2005 Initial Creation
 *                            --------------------------------------------------
 *                            ----------------------------------------------
 */
import java.io.Serializable;
import java.util.Date;

public class InterfaceModuleVO implements Serializable {

    private String _txnID;
    private String _referenceID;
    private String _msisdn;
    private long _requestValue;
    private long _previousBalance;
    private long _postBalance;
    private int _validity;
    private String _interfaceType;
    private String _interfaceID;
    private String _interfaceResonseCode;
    private String _cardGroup;
    private String _serviceClass;
    private Date _msisdnPreviousExpiry;
    private Date _msisdnNewExpiry;
    private String _txnStatus;
    private String _txnType;
    private Date _txnDateTime;
    private String _txnResponseReceived;
    private long _txnStartTime;
    private long _txnEndTime;
    private String _urlID;
    private long _bonusValue;
    private int _bonusValidity;
    // added for logging information of interface transaction table (Manisha
    // 04/02/08)
    private double _bonusSMS;
    private double _bonusMMS;
    private String _userType;
    private String _serviceType;
    // added by vikask for updation card group
    private long _bonusSMSValidity;
    private long _bonusMMSValidity;
    private long _creditbonusValidity;

    private String _onLine;
    private String _both;

    public InterfaceModuleVO() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @return Returns the cardGroup.
     */
    public String getCardGroup() {
        return _cardGroup;
    }

    /**
     * @param cardGroup
     *            The cardGroup to set.
     */
    public void setCardGroup(String cardGroup) {
        _cardGroup = cardGroup;
    }

    /**
     * @return Returns the interfaceID.
     */
    public String getInterfaceID() {
        return _interfaceID;
    }

    /**
     * @param interfaceID
     *            The interfaceID to set.
     */
    public void setInterfaceID(String interfaceID) {
        _interfaceID = interfaceID;
    }

    /**
     * @return Returns the interfaceResonseCode.
     */
    public String getInterfaceResonseCode() {
        return _interfaceResonseCode;
    }

    /**
     * @param interfaceResonseCode
     *            The interfaceResonseCode to set.
     */
    public void setInterfaceResonseCode(String interfaceResonseCode) {
        _interfaceResonseCode = interfaceResonseCode;
    }

    /**
     * @return Returns the interfaceType.
     */
    public String getInterfaceType() {
        return _interfaceType;
    }

    /**
     * @param interfaceType
     *            The interfaceType to set.
     */
    public void setInterfaceType(String interfaceType) {
        _interfaceType = interfaceType;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the msisdnNewExpiry.
     */
    public Date getMsisdnNewExpiry() {
        return _msisdnNewExpiry;
    }

    /**
     * @param msisdnNewExpiry
     *            The msisdnNewExpiry to set.
     */
    public void setMsisdnNewExpiry(Date msisdnNewExpiry) {
        _msisdnNewExpiry = msisdnNewExpiry;
    }

    /**
     * @return Returns the msisdnPreviousExpiry.
     */
    public Date getMsisdnPreviousExpiry() {
        return _msisdnPreviousExpiry;
    }

    /**
     * @param msisdnPreviousExpiry
     *            The msisdnPreviousExpiry to set.
     */
    public void setMsisdnPreviousExpiry(Date msisdnPreviousExpiry) {
        _msisdnPreviousExpiry = msisdnPreviousExpiry;
    }

    /**
     * @return Returns the postBalance.
     */
    public long getPostBalance() {
        return _postBalance;
    }

    /**
     * @param postBalance
     *            The postBalance to set.
     */
    public void setPostBalance(long postBalance) {
        _postBalance = postBalance;
    }

    /**
     * @return Returns the previousBalance.
     */
    public long getPreviousBalance() {
        return _previousBalance;
    }

    /**
     * @param previousBalance
     *            The previousBalance to set.
     */
    public void setPreviousBalance(long previousBalance) {
        _previousBalance = previousBalance;
    }

    /**
     * @return Returns the requestValue.
     */
    public long getRequestValue() {
        return _requestValue;
    }

    /**
     * @param requestValue
     *            The requestValue to set.
     */
    public void setRequestValue(long requestValue) {
        _requestValue = requestValue;
    }

    /**
     * @return Returns the serviceClass.
     */
    public String getServiceClass() {
        return _serviceClass;
    }

    /**
     * @param serviceClass
     *            The serviceClass to set.
     */
    public void setServiceClass(String serviceClass) {
        _serviceClass = serviceClass;
    }

    /**
     * @return Returns the txnStatus.
     */
    public String getTxnStatus() {
        return _txnStatus;
    }

    /**
     * @param txnStatus
     *            The txnStatus to set.
     */
    public void setTxnStatus(String txnStatus) {
        _txnStatus = txnStatus;
    }

    /**
     * @return Returns the txnType.
     */
    public String getTxnType() {
        return _txnType;
    }

    /**
     * @param txnType
     *            The txnType to set.
     */
    public void setTxnType(String txnType) {
        _txnType = txnType;
    }

    /**
     * @return Returns the validity.
     */
    public int getValidity() {
        return _validity;
    }

    /**
     * @param validity
     *            The validity to set.
     */
    public void setValidity(int validity) {
        _validity = validity;
    }

    /**
     * @return Returns the txnDateTime.
     */
    public Date getTxnDateTime() {
        return _txnDateTime;
    }

    /**
     * @param txnDateTime
     *            The txnDateTime to set.
     */
    public void setTxnDateTime(Date txnDateTime) {
        _txnDateTime = txnDateTime;
    }

    /**
     * @return Returns the referenceID.
     */
    public String getReferenceID() {
        return _referenceID;
    }

    /**
     * @param referenceID
     *            The referenceID to set.
     */
    public void setReferenceID(String referenceID) {
        _referenceID = referenceID;
    }

    /**
     * @return Returns the txnID.
     */
    public String getTxnID() {
        return _txnID;
    }

    /**
     * @param txnID
     *            The txnID to set.
     */
    public void setTxnID(String txnID) {
        _txnID = txnID;
    }

    public String getTxnResponseReceived() {
        return _txnResponseReceived;
    }

    public void setTxnResponseReceived(String txnResponseReceived) {
        _txnResponseReceived = txnResponseReceived;
    }

    /**
     * @return Returns the txnEndTime.
     */
    public long getTxnEndTime() {
        return _txnEndTime;
    }

    /**
     * @param txnEndTime
     *            The txnEndTime to set.
     */
    public void setTxnEndTime(long txnEndTime) {
        _txnEndTime = txnEndTime;
    }

    /**
     * @return Returns the txnStartTime.
     */
    public long getTxnStartTime() {
        return _txnStartTime;
    }

    /**
     * @param txnStartTime
     *            The txnStartTime to set.
     */
    public void setTxnStartTime(long txnStartTime) {
        _txnStartTime = txnStartTime;
    }

    public String getUrlID() {
        return _urlID;
    }

    public void setUrlID(String urlID) {
        _urlID = urlID;
    }

    public int getBonusValidity() {
        return _bonusValidity;
    }

    public void setBonusValidity(int bonusValidity) {
        _bonusValidity = bonusValidity;
    }

    public long getBonusValue() {
        return _bonusValue;
    }

    public void setBonusValue(long bonusValue) {
        _bonusValue = bonusValue;
    }

    /**
     * @return Returns the bonusMMS.
     */
    public double getBonusMMS() {
        return _bonusMMS;
    }

    /**
     * @param bonusMMS
     *            The bonusMMS to set.
     */
    public void setBonusMMS(double bonusMMS) {
        _bonusMMS = bonusMMS;
    }

    /**
     * @return Returns the bonusSMS.
     */
    public double getBonusSMS() {
        return _bonusSMS;
    }

    /**
     * @param bonusSMS
     *            The bonusSMS to set.
     */
    public void setBonusSMS(double bonusSMS) {
        _bonusSMS = bonusSMS;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the userType.
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * @param userType
     *            The userType to set.
     */
    public void setUserType(String userType) {
        _userType = userType;
    }

    // added by vikask for card group updation
    public long getBonusMMSValidity() {
        return _bonusMMSValidity;
    }

    public void setBonusMMSValidity(long validity) {
        _bonusMMSValidity = validity;
    }

    public long getBonusSMSValidity() {
        return _bonusSMSValidity;
    }

    public void setBonusSMSValidity(long validity) {
        _bonusSMSValidity = validity;
    }

    public long getCreditbonusValidity() {
        return _creditbonusValidity;
    }

    public void setCreditbonusValidity(long validity) {
        _creditbonusValidity = validity;
    }

    /**
     * @return Returns the both.
     */
    public String getBoth() {
        return _both;
    }

    /**
     * @param both
     *            The both to set.
     */
    public void setBoth(String both) {
        _both = both;
    }

    /**
     * @return Returns the onLine.
     */
    public String getOnLine() {
        return _onLine;
    }

    /**
     * @param onLine
     *            The onLine to set.
     */
    public void setOnLine(String onLine) {
        _onLine = onLine;
    }
}
