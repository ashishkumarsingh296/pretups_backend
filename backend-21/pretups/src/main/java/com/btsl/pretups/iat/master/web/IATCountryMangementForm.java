/*
 * @# IATCountryMangementForm.java
 * This Bean class is for IAT Country management Domain
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 07, 2009 Chetan Kothari Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2009 comviva technologies
 */
package com.btsl.pretups.iat.master.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * 
 */
public class IATCountryMangementForm  {

    
	private static final long serialVersionUID = 1L;

	public IATCountryMangementForm() {

    }

    /**
     * Method flush. This method is used to reset all the values of the form
     * bean.
     */
    public static final Log logger = LogFactory.getLog(IATCountryMangementForm.class.getName());
    private String _receiverCountryCode = null;
    private String _receiverCountryName = null;
    private String _receiverCountryShortName = null;
    private String _currency = null;
    private String _prefixLength = null;
    private String _minimumMsisdnLength = null;
    private String _maximumMsisdnLength = null;
    private String _iatStatusCode = null;
    private String _msgLanguage1 = null;
    private String _msgLanguage2 = null;
    private String _iatStatus = null;
    private String _action = null;
    private int _resultCount = 0;
    private String _receiverCountry = null;
    private String _countrySerialID;

    private ArrayList _iatStatusList = null;
    private ArrayList _iatCountryList = null;

    public void flush() {
        _receiverCountryCode = null;
        _receiverCountryName = null;
        _receiverCountryShortName = null;
        _currency = null;
        _prefixLength = null;
        _minimumMsisdnLength = null;
        _maximumMsisdnLength = null;
        _iatStatus = null;
        _iatStatusCode = null;
        _msgLanguage1 = null;
        _msgLanguage2 = null;
        _action = null;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer("");
        sbf.append("[Receiver country code :");
        sbf.append(_receiverCountryCode);
        sbf.append("]");
        sbf.append("[Receiver country Name :");
        sbf.append(_receiverCountryName);
        sbf.append("]");
        sbf.append("[Receiver country short name :");
        sbf.append(_receiverCountryShortName);
        sbf.append("]");
        sbf.append("[Currency :");
        sbf.append(_currency);
        sbf.append("]");
        sbf.append("[Prefix length :");
        sbf.append(_prefixLength);
        sbf.append("]");
        sbf.append("[minimum MSISDN length :");
        sbf.append(_minimumMsisdnLength);
        sbf.append("]");
        sbf.append("[maximum MSISDN length :");
        sbf.append(_maximumMsisdnLength);
        sbf.append("]");
        sbf.append("[iatStatus :");
        sbf.append(_iatStatus);
        sbf.append("]");
        sbf.append("[message language1 :");
        sbf.append(_msgLanguage1);
        sbf.append("]");
        sbf.append("[message language2 :");
        sbf.append(_msgLanguage2);
        sbf.append("]");
        return sbf.toString();
    }

    /**
     * Method semiFlush. This method is used to reset some of the values of the
     * form
     * bean.
     */
    public void semiFlush() {

    }

    /**
     * @return Returns the currency.
     */
    public String getCurrency() {
        return _currency;
    }

    /**
     * @param currency
     *            The currency to set.
     */
    public void setCurrency(String currency) {
        _currency = currency;
    }

    /**
     * @return Returns the iatStatus.
     */
    public String getIatStatus() {
        return _iatStatus;
    }

    /**
     * @param iatStatus
     *            The iatStatus to set.
     */
    public void setIatStatus(String iatStatus) {
        _iatStatus = iatStatus;
    }

    /**
     * @return Returns the maximumMsisdnLength.
     */
    public String getMaximumMsisdnLength() {
        return _maximumMsisdnLength;
    }

    /**
     * @param maximumMsisdnLength
     *            The maximumMsisdnLength to set.
     */
    public void setMaximumMsisdnLength(String maximumMsisdnLength) {
        _maximumMsisdnLength = maximumMsisdnLength;
    }

    /**
     * @return Returns the minimumMsisdnLength.
     */
    public String getMinimumMsisdnLength() {
        return _minimumMsisdnLength;
    }

    /**
     * @param minimumMsisdnLength
     *            The minimumMsisdnLength to set.
     */
    public void setMinimumMsisdnLength(String minimumMsisdnLength) {
        _minimumMsisdnLength = minimumMsisdnLength;
    }

    /**
     * @return Returns the msgLanguage1.
     */
    public String getMsgLanguage1() {
        return _msgLanguage1;
    }

    /**
     * @param msgLanguage1
     *            The msgLanguage1 to set.
     */
    public void setMsgLanguage1(String msgLanguage1) {
        _msgLanguage1 = msgLanguage1;
    }

    /**
     * @return Returns the msgLanguage2.
     */
    public String getMsgLanguage2() {
        return _msgLanguage2;
    }

    /**
     * @param msgLanguage2
     *            The msgLanguage2 to set.
     */
    public void setMsgLanguage2(String msgLanguage2) {
        _msgLanguage2 = msgLanguage2;
    }

    /**
     * @return Returns the prefixLength.
     */
    public String getPrefixLength() {
        return _prefixLength;
    }

    /**
     * @param prefixLength
     *            The prefixLength to set.
     */
    public void setPrefixLength(String prefixLength) {
        _prefixLength = prefixLength;
    }

    /**
     * @return Returns the receiverCountryCode.
     */
    public String getReceiverCountryCode() {
        return _receiverCountryCode;
    }

    /**
     * @param receiverCountryCode
     *            The receiverCountryCode to set.
     */
    public void setReceiverCountryCode(String receiverCountryCode) {
        _receiverCountryCode = receiverCountryCode;
    }

    /**
     * @return Returns the receiverCountryName.
     */
    public String getReceiverCountryName() {
        return _receiverCountryName;
    }

    /**
     * @param receiverCountryName
     *            The receiverCountryName to set.
     */
    public void setReceiverCountryName(String receiverCountryName) {
        _receiverCountryName = receiverCountryName;
    }

    /**
     * @return Returns the receiverCountryShortName.
     */
    public String getReceiverCountryShortName() {
        return _receiverCountryShortName;
    }

    /**
     * @param receiverCountryShortName
     *            The receiverCountryShortName to set.
     */
    public void setReceiverCountryShortName(String receiverCountryShortName) {
        _receiverCountryShortName = receiverCountryShortName;
    }

    /**
     * @return Returns the iatStatusList.
     */
    public ArrayList getIatStatusList() {
        return _iatStatusList;
    }

    /**
     * @param iatStatusList
     *            The iatStatusList to set.
     */
    public void setIatStatusList(ArrayList iatStatusList) {
        this._iatStatusList = iatStatusList;
    }

    /**
     * @return Returns the iatStatusCode.
     */
    public String getIatStatusCode() {
        return _iatStatusCode;
    }

    /**
     * @param iatStatusCode
     *            The iatStatusCode to set.
     */
    public void setIatStatusCode(String iatStatusCode) {
        _iatStatusCode = iatStatusCode;
    }

    /**
     * @return Returns the action.
     */
    public String getAction() {
        return _action;
    }

    /**
     * @param action
     *            The action to set.
     */
    public void setAction(String action) {
        _action = action;
    }

    /**
     * @return Returns the iatCountryList.
     */
    public ArrayList getIatCountryList() {
        return _iatCountryList;
    }

    /**
     * @param iatCountryList
     *            The iatCountryList to set.
     */
    public void setIatCountryList(ArrayList iatCountryList) {
        _iatCountryList = iatCountryList;
    }

    /**
     * @return Returns the resultCount.
     */
    public int getResultCount() {
        return _resultCount;
    }

    /**
     * @param resultCount
     *            The resultCount to set.
     */
    public void setResultCount(int resultCount) {
        _resultCount = resultCount;
    }

    /**
     * @return Returns the receiverCountry.
     */
    public String getReceiverCountry() {
        return _receiverCountry;
    }

    /**
     * @param receiverCountry
     *            The receiverCountry to set.
     */
    public void setReceiverCountry(String receiverCountry) {
        _receiverCountry = receiverCountry;
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
