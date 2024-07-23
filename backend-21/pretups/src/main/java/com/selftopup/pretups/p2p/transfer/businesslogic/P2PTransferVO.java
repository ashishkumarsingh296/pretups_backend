package com.selftopup.pretups.p2p.transfer.businesslogic;

/*
 * @(#)P2PTransferVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 05/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.io.Serializable;
import java.util.ArrayList;

import com.selftopup.pretups.transfer.businesslogic.TransferVO;

public class P2PTransferVO extends TransferVO implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private String _value;
    private String _rechargeAmount;
    private String _cardName;
    private String _bankName;
    private String _cardNumber;
    private String _firstCardDigit;
    private String _secondCardDigit;
    private String _thirdCardDigit;
    private String _lastCardDigit;
    private String _monthExpiry;
    private String _yearExpiry;
    private String _cardExpiry;
    private String _emailId;
    private String _msisdn;
    private String _address;
    private String _typeOfCard;
    private String _dateOfBirth;
    private String _nickName;
    private String _acceptTerms;
    private ArrayList<String> _cardDetailsList;
    private ArrayList _monthList;
    private ArrayList _yearList;
    private CardDetailsVO _cardDetailsVO;
    private String _cvv;
    private String _pin;
    private String _receiverMsisdn;

    /**
     * @return Returns the _value.
     */
    public String getValue() {
        return _value;
    }

    /**
     * @param _value
     *            The _value to set.
     */
    public void setValue(String _value) {
        this._value = _value;
    }

    public String getRechargeAmount() {
        return _rechargeAmount;
    }

    public void setRechargeAmount(String amount) {
        _rechargeAmount = amount;
    }

    public ArrayList<String> getCardDetailsList() {
        return _cardDetailsList;
    }

    public void setCardDetailsList(ArrayList<String> detailsList) {
        _cardDetailsList = detailsList;
    }

    public CardDetailsVO getCardDetailsVO() {
        return _cardDetailsVO;
    }

    public void setCardDetailsVO(CardDetailsVO detailsVO) {
        _cardDetailsVO = detailsVO;
    }

    public String getEmailId() {
        return _emailId;
    }

    public void setEmailId(String id) {
        _emailId = id;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String _msisdn) {
        this._msisdn = _msisdn;
    }

    public String getAddress() {
        return _address;
    }

    public void setAddress(String _address) {
        this._address = _address;
    }

    public String getTypeOfCard() {
        return _typeOfCard;
    }

    public void setTypeOfCard(String ofCard) {
        _typeOfCard = ofCard;
    }

    public String getDateOfBirth() {
        return _dateOfBirth;
    }

    public void setDateOfBirth(String ofBirth) {
        _dateOfBirth = ofBirth;
    }

    public String getNickName() {
        return _nickName;
    }

    public void setNickName(String name) {
        _nickName = name;
    }

    public String getAcceptTerms() {
        return _acceptTerms;
    }

    public void setAcceptTerms(String terms) {
        _acceptTerms = terms;
    }

    public String getCardName() {
        return _cardName;
    }

    public String getBankName() {
        return _bankName;
    }

    public String getCardNumber() {
        return _cardNumber;
    }

    public String getFirstCardDigit() {
        return _firstCardDigit;
    }

    public String getSecondCardDigit() {
        return _secondCardDigit;
    }

    public String getThirdCardDigit() {
        return _thirdCardDigit;
    }

    public String getLastCardDigit() {
        return _lastCardDigit;
    }

    public String getMonthExpiry() {
        return _monthExpiry;
    }

    public String getYearExpiry() {
        return _yearExpiry;
    }

    public String getCardExpiry() {
        return _cardExpiry;
    }

    public void setCardName(String name) {
        _cardName = name;
    }

    public void setBankName(String name) {
        _bankName = name;
    }

    public void setCardNumber(String number) {
        _cardNumber = number;
    }

    public void setFirstCardDigit(String cardDigit) {
        _firstCardDigit = cardDigit;
    }

    public void setSecondCardDigit(String cardDigit) {
        _secondCardDigit = cardDigit;
    }

    public void setThirdCardDigit(String cardDigit) {
        _thirdCardDigit = cardDigit;
    }

    public void setLastCardDigit(String cardDigit) {
        _lastCardDigit = cardDigit;
    }

    public void setMonthExpiry(String expiry) {
        _monthExpiry = expiry;
    }

    public void setYearExpiry(String expiry) {
        _yearExpiry = expiry;
    }

    public void setCardExpiry(String expiry) {
        _cardExpiry = expiry;
    }

    public void flush() {
        _cardName = null;
        _firstCardDigit = null;
        _secondCardDigit = null;
        _thirdCardDigit = null;
        _lastCardDigit = null;
        _dateOfBirth = null;
        _emailId = null;
        _address = null;
        _acceptTerms = null;
        _cardNumber = null;
        _monthExpiry = null;
        _yearExpiry = null;
        _cardDetailsList = null;
        _bankName = null;
        _nickName = null;
        _rechargeAmount = null;
    }

    public void semiFlush() {
        _cardName = null;
        _firstCardDigit = null;
        _secondCardDigit = null;
        _thirdCardDigit = null;
        _lastCardDigit = null;
        _dateOfBirth = null;
        _emailId = null;
        _address = null;
        _acceptTerms = null;
        _cardNumber = null;
        _monthExpiry = null;
        _yearExpiry = null;
        // _bankName=null;
        _nickName = null;
        _cvv = null;
    }

    public ArrayList getMonthList() {
        return _monthList;
    }

    public void setMonthList(ArrayList list) {
        _monthList = list;
    }

    public ArrayList getYearList() {
        return _yearList;
    }

    public void setYearList(ArrayList list) {
        _yearList = list;
    }

    public String getCvv() {
        return _cvv;
    }

    public void setCvv(String _cvv) {
        this._cvv = _cvv;
    }

    public String getPin() {
        return _pin;
    }

    public void setPin(String _pin) {
        this._pin = _pin;
    }

    public String getReceiverMsisdn() {
        return _receiverMsisdn;
    }

    public void setReceiverMsisdn(String msisdn) {
        _receiverMsisdn = msisdn;
    }

}
