package com.btsl.pretups.iat.businesslogic;

public class IATCountryMasterVO {

    private int _recCountryCode;
    private String _recCountryShortName;
    private String _recCountryName;
    private String _currency;
    private int _prefixLength;
    private int _minMsisdnLength;
    private int _maxMsisdnLength;
    private String _countryStatus;
    private String _language1Message;
    private String _language2Message;
    private String _countrySerialID;

    public String toString() {
        StringBuffer sbf = new StringBuffer("");
        sbf.append("[Receiver country code :" + _recCountryCode + "]");
        sbf.append("[Receiver country Name :" + _recCountryName + "]");
        sbf.append("[Receiver country short name :" + _recCountryShortName + "]");
        sbf.append("[Currency :" + _currency + "]");
        sbf.append("[Prefix length :" + _prefixLength + "]");
        sbf.append("[minimum MSISDN length :" + _minMsisdnLength + "]");
        sbf.append("[maximum MSISDN length :" + _maxMsisdnLength + "]");
        sbf.append("[iatStatus :" + _countryStatus + "]");
        sbf.append("[message language1 :" + _language1Message + "]");
        sbf.append("[message language2 :" + _language2Message + "]");
        return sbf.toString();
    }

    public String getCountryStatus() {
        return _countryStatus;
    }

    public void setCountryStatus(String countryStatus) {
        _countryStatus = countryStatus;
    }

    public String getCurrency() {
        return _currency;
    }

    public void setCurrency(String currency) {
        _currency = currency;
    }

    public String getLanguage1Message() {
        return _language1Message;
    }

    public void setLanguage1Message(String language1Message) {
        _language1Message = language1Message;
    }

    public String getLanguage2Message() {
        return _language2Message;
    }

    public void setLanguage2Message(String language2Message) {
        _language2Message = language2Message;
    }

    public int getMaxMsisdnLength() {
        return _maxMsisdnLength;
    }

    public void setMaxMsisdnLength(int maxMsisdnLength) {
        _maxMsisdnLength = maxMsisdnLength;
    }

    public int getMinMsisdnLength() {
        return _minMsisdnLength;
    }

    public void setMinMsisdnLength(int minMsisdnLength) {
        _minMsisdnLength = minMsisdnLength;
    }

    public int getPrefixLength() {
        return _prefixLength;
    }

    public void setPrefixLength(int prefixLength) {
        _prefixLength = prefixLength;
    }

    public int getRecCountryCode() {
        return _recCountryCode;
    }

    public void setRecCountryCode(int recCountryCode) {
        _recCountryCode = recCountryCode;
    }

    public String getRecCountryName() {
        return _recCountryName;
    }

    public void setRecCountryName(String recCountryName) {
        _recCountryName = recCountryName;
    }

    public String getRecCountryShortName() {
        return _recCountryShortName;
    }

    public void setRecCountryShortName(String recCountryShortName) {
        _recCountryShortName = recCountryShortName;
    }

    /**
     * @return Returns the countrySerialID.
     */
    public String getCountrySerialID() {
        return _countrySerialID;
    }

    /**
     * @param countrySerialID
     *            The countrySerialID to set.
     */
    public void setCountrySerialID(String countrySerialID) {
        _countrySerialID = countrySerialID;
    }
}
