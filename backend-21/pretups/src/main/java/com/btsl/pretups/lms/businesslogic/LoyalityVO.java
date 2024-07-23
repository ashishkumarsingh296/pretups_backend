package com.btsl.pretups.lms.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class LoyalityVO implements Serializable {

    private String _promotionName = null;
    private String _fromDate = null;
    private String _toDate = null;
    private String _serviceType = null;
    private String[] _applicableTo = null;
    private String _payer = "N";
    private String _payerHierarchy = "N";
    private String _registeresPayer = "N";
    private String _payee = "N";
    private String _payeeHierarchy = "N";
    private String _registeresPayee = "N";
    private ArrayList _serviceList;
    private long _time = 0;
    private String _diff;
    private String _applicableFromHour = null;
    private Date _applicableFromDate;
    private String _applicableToHour;
    private Date _applicableToDate;
    private String _createdBy;
    private String _ModifiedBy;
    private String _approvalStatus;
    private String _promotionType;
    private String _promotionID;
    private ArrayList _applicableList;
    private String _approvalremarks;
    private String _status;
    private int _index;

    private String _itemCode;
    private String _itemName;
    private long _itemQuantity;
    private long _itemPoints;
    private String _itemQuantityAsString = null;
    private String _itemPointsAsString = null;

    public String getItemQuantityAsString() {
        return _itemQuantityAsString;
    }

    public void setItemQuantityAsString(String itemQuantityAsString) {
        _itemQuantityAsString = itemQuantityAsString;
    }

    public String getItemPointsAsString() {
        return _itemPointsAsString;
    }

    public void setItemPointsAsString(String itemPointsAsString) {
        _itemPointsAsString = itemPointsAsString;
    }

    public String getDiff() {
        return _diff;
    }

    public void setDiff(String diff) {
        _diff = diff;
    }

    public String getPromotionName() {
        return _promotionName;
    }

    public void setPromotionName(String name) {
        _promotionName = name;
    }

    public String getFromDate() {
        return _fromDate;
    }

    public void setFromDate(String date) {
        _fromDate = date;
    }

    public String getToDate() {
        return _toDate;
    }

    public void setToDate(String date) {
        _toDate = date;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String type) {
        _serviceType = type;
    }

    public String[] getApplicableTo() {
        return _applicableTo;
    }

    /**
     * @param allowedDays
     *            The allowedDays to set.
     */
    public void setApplicableTo(String[] allowedTo) {
        this._applicableTo = allowedTo;
    }

    public String getPayer() {
        return _payer;
    }

    public void setPayer(String _payer) {
        this._payer = _payer;
    }

    public String getPayerHierarchy() {
        return _payerHierarchy;
    }

    public void setPayerHierarchy(String hierarchy) {
        _payerHierarchy = hierarchy;
    }

    public String getRegisteresPayer() {
        return _registeresPayer;
    }

    public void setRegisteresPayer(String payer) {
        _registeresPayer = payer;
    }

    public String getPayee() {
        return _payee;
    }

    public void setPayee(String _payee) {
        this._payee = _payee;
    }

    public String getPayeeHierarchy() {
        return _payeeHierarchy;
    }

    public void setPayeeHierarchy(String hierarchy) {
        _payeeHierarchy = hierarchy;
    }

    public String getRegisteresPayee() {
        return _registeresPayee;
    }

    public void setRegisteresPayee(String payee) {
        _registeresPayee = payee;
    }

    public ArrayList getServiceList() {
        return _serviceList;
    }

    /**
     * @param userNamePrefixList
     *            The userNamePrefixList to set.
     */
    public void setServiceList(ArrayList serviceList) {
        _serviceList = serviceList;
    }

    public long getTime() {
        return _time;
    }

    /**
     * @param time
     *            The time to set.
     */
    public void setTime(long time) {
        _time = time;
    }

    public String getApplicableFromHour() {
        return _applicableFromHour;
    }

    /**
     * @param applicableFromHour
     *            The applicableFromHour to set.
     */
    public void setApplicableFromHour(String applicableFromHour) {
        if (applicableFromHour != null) {
            _applicableFromHour = applicableFromHour.trim();
        }
    }

    public Date getApplicableFromDate() {
        return _applicableFromDate;
    }

    /**
     * @param applicableFromDate
     *            The applicableFromDate to set.
     */
    public void setApplicableFromDate(Date applicableFromDate) {

        _applicableFromDate = applicableFromDate;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public String getModifiedBy() {
        return _ModifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _ModifiedBy = modifiedBy;
    }

    public String getAPPROVAL_STATUS() {
        return _approvalStatus;
    }

    public void setAPPROVAL_STATUS(String approval_status) {
        _approvalStatus = approval_status;
    }

    public String getPromotionType() {
        return _promotionType;
    }

    public void setPromotionType(String promotionType) {
        _promotionType = promotionType;
    }

    public String getApplicableToHour() {
        return _applicableToHour;
    }

    public void setApplicableToHour(String applicableToHour) {
        _applicableToHour = applicableToHour;
    }

    public Date getApplicableToDate() {
        return _applicableToDate;
    }

    public void setApplicableToDate(Date applicableToDate) {
        _applicableToDate = applicableToDate;
    }

    public String getPromotionID() {
        return _promotionID;
    }

    public void setPromotionID(String promotionID) {
        _promotionID = promotionID;
    }

    public int getIndex() {
        return _index;
    }

    public void setIndex(int index) {
        _index = index;
    }

    public ArrayList getApplicableList() {
        return _applicableList;
    }

    public void setApplicableList(ArrayList applicableList) {
        _applicableList = applicableList;
    }

    public String getApprovalremarks() {
        return _approvalremarks;
    }

    public void setApprovalremarks(String approvalremarks) {
        _approvalremarks = approvalremarks;
    }

    public String getItemCode() {
        return _itemCode;
    }

    public void setItemCode(String itemCode) {
        _itemCode = itemCode;
    }

    public String getItemName() {
        return _itemName;
    }

    public void setItemName(String itemName) {
        _itemName = itemName;
    }

    public long getItemQuantity() {
        return _itemQuantity;
    }

    public void setItemQuantity(long itemQuantity) {
        _itemQuantity = itemQuantity;
    }

    public long getItemPoints() {
        return _itemPoints;
    }

    public void setItemPoints(long itemPoints) {
        _itemPoints = itemPoints;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

}
