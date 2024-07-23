package com.btsl.pretups.lms.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.loyalitystock.businesslogic.LoyalityStockVO;

public class LoyaltyPointsRedemptionVO implements Serializable {

    private String _userID;
    private String _userName;
    private String _networkID;
    private String _loginID;
    private String _categoryCode;
    private String _categoryName;
    private String _parentID;
    private String _ownerID;
    private String _empCode;
    private String _status;
    private String _msisdn;
    private String _userType;
    private String _externalCode;
    private String _userCode;
    private String _currentLoyaltyPoints;
    private String _previousLoyaltyPoints;
    private String _productCode;
    private String _productShortCode;
    private LoyalityStockVO _loyalityStockVO;;
    private ArrayList _giftItemList;

    private int _redempItemQuantity;
    private int _redempLoyaltyPoint;
    private String _redempLoyaltyAmount;
    private String _multFactor;
    private String _redempType;
    private Date _redemptionDate;
    private String _createdBy;
    private Date _createdOn;
    private String _redempStatus;
    private String _errorCode;

    private String _itemCode;
    private String _itemName;
    private int _itemStockAvailable;
    private int _perItemPoints;
    // private String _stockBuffer;
    private String _redemptionID;
    private Date _modifiedOn;
    private int _stockItemBuffer;
    private String _referenceNo;
    private String _previousLoyaltyPointsBuffer;

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String code) {
        _categoryCode = code;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String name) {
        _categoryName = name;
    }

    public String getCurrentLoyaltyPoints() {
        return _currentLoyaltyPoints;
    }

    public void setCurrentLoyaltyPoints(String loyaltyPoints) {
        _currentLoyaltyPoints = loyaltyPoints;
    }

    public String getEmpCode() {
        return _empCode;
    }

    public void setEmpCode(String code) {
        _empCode = code;
    }

    public String getExternalCode() {
        return _externalCode;
    }

    public void setExternalCode(String code) {
        _externalCode = code;
    }

    public ArrayList getGiftItemList() {
        return _giftItemList;
    }

    public void setGiftItemList(ArrayList itemList) {
        _giftItemList = itemList;
    }

    public String getLoginID() {
        return _loginID;
    }

    public void setLoginID(String _loginid) {
        _loginID = _loginid;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String _msisdn) {
        this._msisdn = _msisdn;
    }

    public String getNetworkID() {
        return _networkID;
    }

    public void setNetworkID(String _networkid) {
        _networkID = _networkid;
    }

    public String getOwnerID() {
        return _ownerID;
    }

    public void setOwnerID(String _ownerid) {
        _ownerID = _ownerid;
    }

    public String getParentID() {
        return _parentID;
    }

    public void setParentID(String _parentid) {
        _parentID = _parentid;
    }

    public String getPreviousLoyaltyPoints() {
        return _previousLoyaltyPoints;
    }

    public void setPreviousLoyaltyPoints(String loyaltyPoints) {
        _previousLoyaltyPoints = loyaltyPoints;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String _status) {
        this._status = _status;
    }

    public String getUserCode() {
        return _userCode;
    }

    public void setUserCode(String code) {
        _userCode = code;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String _userid) {
        _userID = _userid;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String name) {
        _userName = name;
    }

    public String getUserType() {
        return _userType;
    }

    public void setUserType(String type) {
        _userType = type;
    }

    public LoyalityStockVO getLoyalityStockVO() {
        return _loyalityStockVO;
    }

    public void setLoyalityStockVO(LoyalityStockVO stockVO) {
        _loyalityStockVO = stockVO;
    }

    public String getMultFactor() {
        return _multFactor;
    }

    public void setMultFactor(String factor) {
        _multFactor = factor;
    }

    public int getRedempItemQuantity() {
        return _redempItemQuantity;
    }

    public void setRedempItemQuantity(int itemQuantity) {
        _redempItemQuantity = itemQuantity;
    }

    public String getRedempLoyaltyAmount() {
        return _redempLoyaltyAmount;
    }

    public void setRedempLoyaltyAmount(String loyaltyAmount) {
        _redempLoyaltyAmount = loyaltyAmount;
    }

    public int getRedempLoyaltyPoint() {
        return _redempLoyaltyPoint;
    }

    public void setRedempLoyaltyPoint(int loyaltyPoint) {
        _redempLoyaltyPoint = loyaltyPoint;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date on) {
        _createdOn = on;
    }

    public String getErrorCode() {
        return _errorCode;
    }

    public void setErrorCode(String code) {
        _errorCode = code;
    }

    public String getRedempStatus() {
        return _redempStatus;
    }

    public void setRedempStatus(String status) {
        _redempStatus = status;
    }

    public Date getRedemptionDate() {
        return _redemptionDate;
    }

    public void setRedemptionDate(Date date) {
        _redemptionDate = date;
    }

    public String getRedempType() {
        return _redempType;
    }

    public void setRedempType(String type) {
        _redempType = type;
    }

    public String getItemCode() {
        return _itemCode;
    }

    public void setItemCode(String code) {
        _itemCode = code;
    }

    public String getItemName() {
        return _itemName;
    }

    public void setItemName(String name) {
        _itemName = name;
    }

    public int getItemStockAvailable() {
        return _itemStockAvailable;
    }

    public void setItemStockAvailable(int stockAvailable) {
        _itemStockAvailable = stockAvailable;
    }

    public int getPerItemPoints() {
        return _perItemPoints;
    }

    public void setPerItemPoints(int itemPoints) {
        _perItemPoints = itemPoints;
    }

    /*
     * public String getStockBuffer() {
     * return _stockBuffer;
     * }
     * public void setStockBuffer(String buffer) {
     * _stockBuffer = buffer;
     * }
     */
    public String getRedemptionID() {
        return _redemptionID;
    }

    public void setRedemptionID(String _redemptionid) {
        _redemptionID = _redemptionid;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    public int getStockItemBuffer() {
        return _stockItemBuffer;
    }

    public void setStockItemBuffer(int itemBuffer) {
        _stockItemBuffer = itemBuffer;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String code) {
        _productCode = code;
    }

    public String getProductShortCode() {
        return _productShortCode;
    }

    public void setProductShortCode(String shortCode) {
        _productShortCode = shortCode;
    }

    public String getReferenceNo() {
        return _referenceNo;
    }

    public void setReferenceNo(String no) {
        _referenceNo = no;
    }

    public String getPreviousLoyaltyPointsBuffer() {
        return _previousLoyaltyPointsBuffer;
    }

    public void setPreviousLoyaltyPointsBuffer(String loyaltyPointsBuffer) {
        _previousLoyaltyPointsBuffer = loyaltyPointsBuffer;
    }
}
