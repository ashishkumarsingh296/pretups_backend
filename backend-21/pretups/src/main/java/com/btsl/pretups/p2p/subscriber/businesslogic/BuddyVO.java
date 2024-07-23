package com.btsl.pretups.p2p.subscriber.businesslogic;

/*
 * BuddyVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 21/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;

public class BuddyVO extends ReceiverVO implements Serializable {
    private String _name;
    private String _buddyMsisdn;
    private String _ownerUser;
    private String _ownerName;
    private long _preferredAmount;
    private int _seqNumber;
    private long _buddyTotalTransfers;
    private long _buddyTotalTransferAmount;
    private String _listName; // added by harsh 23 Aug12
    private String _selectorCode;

    // sonali garg

    private String _status;
    private Date _createdOn;
    private Date _modifiedOn;
    private BuddyVO _buddyVO;
    private long _successiveFailCount;
    private String _senderMSISDN;
    private ArrayList _buddyListVO;
    private Locale _senderLocale;
    private String _parentID;
    private String _transId;

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public long getPreferredAmount() {
        return _preferredAmount;
    }

    public void setPreferredAmount(long preferredAmount) {
        _preferredAmount = preferredAmount;
    }

    public String getOwnerUser() {
        return _ownerUser;
    }

    public void setOwnerUser(String ownerUser) {
        _ownerUser = ownerUser;
    }

    public int getSeqNumber() {
        return _seqNumber;
    }

    public void setSeqNumber(int seqNumber) {
        _seqNumber = seqNumber;
    }

    public String toString() {
        final StringBuffer sbf = new StringBuffer();
        sbf.append("_name=" + _name);
        sbf.append(",_ownerUser=" + _ownerUser);
        sbf.append(",_preferredAmount=" + _preferredAmount);
        sbf.append(",_seqNumber=" + _seqNumber);
        sbf.append(",msisdn=" + super.getMsisdn());
        sbf.append("," + super.toString());
        return sbf.toString();
    }

    public long getBuddyTotalTransferAmount() {
        return _buddyTotalTransferAmount;
    }

    public void setBuddyTotalTransferAmount(long buddyTotalTransferAmount) {
        _buddyTotalTransferAmount = buddyTotalTransferAmount;
    }

    public long getBuddyTotalTransfers() {
        return _buddyTotalTransfers;
    }

    public void setBuddyTotalTransfers(long buddyTotalTransfers) {
        _buddyTotalTransfers = buddyTotalTransfers;
    }

    /**
     * @return Returns the _buddyMsisdn.
     */
    public String getBuddyMsisdn() {
        return _buddyMsisdn;
    }

    /**
     * @param msisdn
     *            The _buddyMsisdn to set.
     */
    public void setBuddyMsisdn(String msisdn) {
        _buddyMsisdn = msisdn;
    }

    /**
     * @return Returns the _ownerName.
     */
    public String getOwnerName() {
        return _ownerName;
    }

    /**
     * @param name
     *            The _ownerName to set.
     */
    public void setOwnerName(String name) {
        _ownerName = name;
    }

    /**
     * @return Returns the _listName. //added by harsh 23 Aug12
     */
    public String getListName() {
        return _listName;
    }

    /**
     * @param name
     *            The _listName to set.
     */
    public void setListName(String name) {
        _listName = name;
    }

    /**
     * @return Returns the _selectorCode.
     */
    public String getSelectorCode() {
        return _selectorCode;
    }

    /**
     * @param code
     *            The _selectorCode to set.
     */
    public void setSelectorCode(String code) {
        _selectorCode = code;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public BuddyVO getBuddyVO() {
        return _buddyVO;
    }

    public void setBuddyVO(BuddyVO buddyVO) {
        _buddyVO = buddyVO;
    }

    public long getSuccessiveFailCount() {
        return _successiveFailCount;
    }

    public void setSuccessiveFailCount(long successiveFailCount) {
        _successiveFailCount = successiveFailCount;
    }

    public String getSenderMSISDN() {
        return _senderMSISDN;
    }

    public void setSenderMSISDN(String senderMSISDN) {
        _senderMSISDN = senderMSISDN;
    }

    public ArrayList getBuddyListVO() {
        return _buddyListVO;
    }

    public void setBuddyListVO(ArrayList buddyListVO) {
        _buddyListVO = buddyListVO;
    }

    public Locale getSenderLocale() {
        return _senderLocale;
    }

    public void setSenderLocale(Locale senderLocale) {
        _senderLocale = senderLocale;
    }

    public String getParentID() {
        return _parentID;
    }

    public void setParentID(String parentID) {
        _parentID = parentID;
    }

    public String getTransId() {
        return _transId;
    }

    public void setTransId(String transId) {
        _transId = transId;
    }
}
