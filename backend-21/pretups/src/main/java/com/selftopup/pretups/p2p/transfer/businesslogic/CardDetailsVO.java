package com.selftopup.pretups.p2p.transfer.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CardDetailsVO implements Serializable {
    private String _userId = null;
    private String _nameOfEmbossing = null;
    private String _cardNumber = null;
    private String _cardType = null;
    private String _cardNickName = null;
    private String _bank = null;
    private String _expiryDate = null;
    private String _msisdn = null;
    // private Date _dob=null;
    private String _email = null;
    private String _address = null;
    private Date _createdOn = null;
    private String _status = null;
    private String _acceptTC = null;
    private String _decryptedCardNumber = null;
    private String _decryptedExpiryDate = null;
    private String _decryptedNameEmbossing = null;
    private String _cvv = null;
    private String _isDefault = null;
    private int _radioIndex;
    private String _originalCardNumber;

    // Shashi Start

    private String _bankName = null;
    private String _firstCardDigit = null;
    private String _secondCardDigit = null;
    private String _thirdCardDigit = null;
    private String _lastCardDigit = null;
    private String _monthExpiry = null;
    private String _yearExpiry = null;
    private String _cardExpiry = null;
    private Date _dateOfBirth = null;
    // private String _nickName;
    private ArrayList _monthList = null;
    private ArrayList _yearList = null;

    private ArrayList _cardDetailsList = null;
    // / private String _selectedCard=null;
    private int _selectedCard = 0;
    private String _oldNickName = null;
    private String _displayCardNumber = null;
    private String _scheduledDate = null;

    String btnBack = null;

    // Shashi End
    private double _autoTopupAmount;
    private ArrayList<String> _autoScheduleTypeList = null;
    private String _autoTopupStatus = null;
    private String _scheduleType = null;
    private boolean _autoEnableFlag = false;
    private String _endDate;

    // Shashi on 14th Aug
    private int _requestedDay = 0;
    private ArrayList<String> _numberOfDays = null;
    private ArrayList _cardTypeList = null;

    public void flush() {
        // _cardName=null;
        _firstCardDigit = null;
        _secondCardDigit = null;
        _thirdCardDigit = null;
        _lastCardDigit = null;
        _dateOfBirth = null;
        _email = null;
        _address = null;
        _acceptTC = null;
        _radioIndex = 0;
        _cardNumber = null;
        _monthExpiry = null;
        _yearExpiry = null;
        // _cardDetailList=null;
        _bankName = null;
        _cardNickName = null;
        _isDefault = null;
        _nameOfEmbossing = null;
        _cardDetailsList = null;
        _oldNickName = null;
        _expiryDate = null;
        _displayCardNumber = null;
        _selectedCard = 0;
        _autoEnableFlag = false;
        _scheduleType = null;
        _autoTopupAmount = 0;
        _endDate = null;
    }

    public void semiFlush() {
        // _cardName=null;
        _firstCardDigit = null;
        _secondCardDigit = null;
        _thirdCardDigit = null;
        _lastCardDigit = null;
        _dateOfBirth = null;
        _email = null;
        _address = null;
        _acceptTC = null;
        _radioIndex = 0;
        _cardNumber = null;
        _monthExpiry = null;
        _yearExpiry = null;
        // _bankName=null;
        // _nickName=null;
        _isDefault = null;
    }

    /**
     * @return the cvv
     */
    public String getCvv() {
        return _cvv;
    }

    /**
     * @param cvv
     *            the cvv to set
     */
    public void setCvv(String cvv) {
        _cvv = cvv;
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
     * @return the nameOfEmbossing
     */
    public String getNameOfEmbossing() {
        return _nameOfEmbossing;
    }

    /**
     * @param nameOfEmbossing
     *            the nameOfEmbossing to set
     */
    public void setNameOfEmbossing(String nameOfEmbossing) {
        _nameOfEmbossing = nameOfEmbossing;
    }

    /**
     * @return the cardNumber
     */
    public String getCardNumber() {
        return _cardNumber;
    }

    /**
     * @param cardNumber
     *            the cardNumber to set
     */
    public void setCardNumber(String cardNumber) {
        _cardNumber = cardNumber;
    }

    /**
     * @return the cardType
     */
    public String getCardType() {
        return _cardType;
    }

    /**
     * @param cardType
     *            the cardType to set
     */
    public void setCardType(String cardType) {
        _cardType = cardType;
    }

    /**
     * @return the cardNickName
     */
    public String getCardNickName() {
        return _cardNickName;
    }

    /**
     * @param cardNickName
     *            the cardNickName to set
     */
    public void setCardNickName(String cardNickName) {
        _cardNickName = cardNickName;
    }

    /**
     * @return the bank
     */
    public String getBank() {
        return _bank;
    }

    /**
     * @param bank
     *            the bank to set
     */

    public void setBank(String bank) {
        _bank = bank;
    }

    /**
     * @return the expiryDate
     */
    public String getExpiryDate() {
        return _expiryDate;
    }

    /**
     * @param expiryDate
     *            the expiryDate to set
     */
    public void setExpiryDate(String expiryDate) {
        _expiryDate = expiryDate;
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
     * @return the dob
     */
    /*
     * public Date getDob() {
     * return _dob;
     * }
     *//**
     * @param dob
     *            the dob to set
     */
    /*
     * public void setDob(Date dob) {
     * _dob = dob;
     * }
     */
    /**
     * @return the email
     */
    public String getEmail() {
        return _email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        _email = email;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return _address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(String address) {
        _address = address;
    }

    /**
     * @return the createdOn
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            the createdOn to set
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return the acceptTC
     */
    public String getAcceptTC() {
        return _acceptTC;
    }

    /**
     * @param acceptTC
     *            the acceptTC to set
     */
    public void setAcceptTC(String acceptTC) {
        _acceptTC = acceptTC;
    }

    /**
     * @return the decryptedCardNumber
     */
    public String getDecryptedCardNumber() {
        return _decryptedCardNumber;
    }

    /**
     * @param decryptedCardNumber
     *            the decryptedCardNumber to set
     */
    public void setDecryptedCardNumber(String decryptedCardNumber) {
        _decryptedCardNumber = decryptedCardNumber;
    }

    /**
     * @return the decryptedExpiryDate
     */
    public String getDecryptedExpiryDate() {
        return _decryptedExpiryDate;
    }

    /**
     * @param decryptedExpiryDate
     *            the decryptedExpiryDate to set
     */
    public void setDecryptedExpiryDate(String decryptedExpiryDate) {
        _decryptedExpiryDate = decryptedExpiryDate;
    }

    /**
     * @return the decryptedNameEmbossing
     */
    public String getDecryptedNameEmbossing() {
        return _decryptedNameEmbossing;
    }

    /**
     * @param decryptedNameEmbossing
     *            the decryptedNameEmbossing to set
     */
    public void setDecryptedNameEmbossing(String decryptedNameEmbossing) {
        _decryptedNameEmbossing = decryptedNameEmbossing;
    }

    /**
     * @return the isDefault
     */
    public String getIsDefault() {
        return _isDefault;
    }

    /**
     * @param isDefault
     *            the isDefault to set
     */
    public void setIsDefault(String isDefault) {
        _isDefault = isDefault;
    }

    public int getRadioIndex() {
        return _radioIndex;
    }

    public void setRadioIndex(int index) {
        _radioIndex = index;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        if (_cardNumber != null)
            sbf.append("Card number= ************" + _cardNumber.substring(_cardNumber.length() - 4));
        sbf.append("Nick name=" + _cardNickName);
        sbf.append("Embossed name=" + _nameOfEmbossing);
        // sbf.append("DOB="+_dob);
        sbf.append("DOB=" + _dateOfBirth);
        sbf.append("Email id=" + _email);
        sbf.append("Address=" + _address);
        sbf.append("User id=" + _userId);
        // sbf.append("card number=" + _cardNumber);
        // sbf.append("Is default="+_);
        return sbf.toString();
    }

    public String getOriginalCardNumber() {
        return _originalCardNumber;
    }

    public void setOriginalCardNumber(String cardNumber) {
        _originalCardNumber = cardNumber;
    }

    public String getBankName() {
        return _bankName;
    }

    public void setBankName(String name) {
        _bankName = name;
    }

    public String getFirstCardDigit() {
        return _firstCardDigit;
    }

    public void setFirstCardDigit(String cardDigit) {
        _firstCardDigit = cardDigit;
    }

    public String getSecondCardDigit() {
        return _secondCardDigit;
    }

    public void setSecondCardDigit(String cardDigit) {
        _secondCardDigit = cardDigit;
    }

    public String getThirdCardDigit() {
        return _thirdCardDigit;
    }

    public void setThirdCardDigit(String cardDigit) {
        _thirdCardDigit = cardDigit;
    }

    public String getLastCardDigit() {
        return _lastCardDigit;
    }

    public void setLastCardDigit(String cardDigit) {
        _lastCardDigit = cardDigit;
    }

    public String getMonthExpiry() {
        return _monthExpiry;
    }

    public void setMonthExpiry(String expiry) {
        _monthExpiry = expiry;
    }

    public String getYearExpiry() {
        return _yearExpiry;
    }

    public void setYearExpiry(String expiry) {
        _yearExpiry = expiry;
    }

    public String getCardExpiry() {
        return _cardExpiry;
    }

    public void setCardExpiry(String expiry) {
        _cardExpiry = expiry;
    }

    public Date getDateOfBirth() {
        return _dateOfBirth;
    }

    public void setDateOfBirth(Date ofBirth) {
        _dateOfBirth = ofBirth;
    }

    /*
     * public String getNickName() {
     * return _nickName;
     * }
     * public void setNickName(String name) {
     * _nickName = name;
     * }
     */
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

    public ArrayList getCardDetailsList() {
        return _cardDetailsList;
    }

    public void setCardDetailsList(ArrayList detailsList) {
        _cardDetailsList = detailsList;
    }

    public int getSelectedCard() {
        return _selectedCard;
    }

    public void setSelectedCard(int card) {
        _selectedCard = card;
    }

    public String getOldNickName() {
        return _oldNickName;
    }

    public void setOldNickName(String oldnickName) {
        _oldNickName = oldnickName;
    }

    public String getDisplayCardNumber() {
        return _displayCardNumber;
    }

    public void setDisplayCardNumber(String number) {
        _displayCardNumber = number;
    }

    public String getBtnBack() {
        return btnBack;
    }

    public void setBtnBack(String btnBack) {
        this.btnBack = btnBack;
    }

    public double getAutoTopupAmount() {
        return _autoTopupAmount;
    }

    public void setAutoTopupAmount(double topupAmount) {
        _autoTopupAmount = topupAmount;
    }

    public ArrayList<String> getAutoScheduleTypeList() {
        return _autoScheduleTypeList;
    }

    public void setAutoScheduleTypeList(ArrayList<String> scheduleTypeList) {
        _autoScheduleTypeList = scheduleTypeList;
    }

    public String getAutoTopupStatus() {
        return _autoTopupStatus;
    }

    public void setAutoTopupStatus(String topupStatus) {
        _autoTopupStatus = topupStatus;
    }

    public String getScheduleType() {
        return _scheduleType;
    }

    public void setScheduleType(String type) {
        _scheduleType = type;
    }

    public boolean isAutoEnableFlag() {
        return _autoEnableFlag;
    }

    public void setAutoEnableFlag(boolean enableFlag) {
        _autoEnableFlag = enableFlag;
    }

    public String getEndDate() {
        return _endDate;
    }

    public void setEndDate(String date) {
        _endDate = date;
    }

    // shashi on 14th aug

    public int getRequestedDay() {
        return _requestedDay;
    }

    public void setRequestedDay(int day) {
        _requestedDay = day;
    }

    public ArrayList<String> getNumberOfDays() {
        return _numberOfDays;
    }

    public void setNumberOfDays(ArrayList<String> scheduledays) {
        _numberOfDays = scheduledays;
    }

    public ArrayList getCardTypeList() {
        return _cardTypeList;
    }

    public void setCardTypeList(ArrayList list) {
        _cardTypeList = list;
    }

    public String getScheduledDate() {
        return _scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        _scheduledDate = scheduledDate;
    }

}