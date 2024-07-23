package com.btsl.pretups.lms.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.lms.businesslogic.RewardDetailsVO;
import com.btsl.pretups.loyalty.transaction.LoyaltyVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class LMSForm {
    private final Log _log = LogFactory.getLog(LMSForm.class.getName());
    private String _promotionName = null;
    private String _fromDate = null;
    private String _toDate = null;
    private String _serviceType = null;
    private String _applicableTo = null;
    private String _payer = "N";
    private String _payerHierarchy = "N";
    private String _registeresPayer = "N";
    private String _payee = "N";
    private String _payeeHierarchy = "N";
    private String _registeresPayee = "N";
    private ArrayList _serviceList;
    private ArrayList _promotionNameList;
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
    private ArrayList _promotionTypeList;
    private String _promotionID;
    private String _selectedPromotion = null;
    private int _listSize;
    private ArrayList _domainList;
    private ArrayList _categoryList;
    private String _networkName;
    private String _categoryCode;
    private String _domainCode;
    private ArrayList _applicableList;
    private String _itemCode;
    private String _itemName;
    private long _itemQuantity;
    private long _itemPoints;
    private String _itemQuantityAsString = null;
    private String _itemPointsAsString = null;
    private String _rewardID;
    private String _promotionAsscId;
    private String _domainName;
    private String _categoryName;
    private String _statusType;
    private ArrayList _amountTypeList;
    private int _locationIndex;
    private ArrayList _payeeSlabsList;
    private ArrayList _payeeHierSlabsList;
    private ArrayList _payeeRegSlabsList;
    private ArrayList _payerSlabsList;
    private ArrayList _payerHierSlabsList;
    private ArrayList _payerRegSlabsList;
    private ArrayList _promtionAssociationList;
    private ArrayList _rewardRangeList;
    private ArrayList _itemList;
    private ArrayList _statusTypeList;
    private ArrayList _viewList;

    private String remarks1;
    private String remarks2;

    private String Daily;
    private String Weekly;
    private String Monthly;
    private String _approvalremarks;

    public String getRemarks1() {
        return remarks1;
    }

    public void setRemarks1(String remarks1) {
        this.remarks1 = remarks1;
    }

    public String getRemarks2() {
        return remarks2;
    }

    public void setRemarks2(String remarks2) {
        this.remarks2 = remarks2;
    }

    public String getDaily() {
        return Daily;
    }

    public void setDaily(String daily) {
        Daily = daily;
    }

    public String getWeekly() {
        return Weekly;
    }

    public void setWeekly(String weekly) {
        Weekly = weekly;
    }

    public String getMonthly() {
        return Monthly;
    }

    public void setMonthly(String monthly) {
        Monthly = monthly;
    }

    public String getDiff() {
        return _diff;
    }

    public void setDiff(String diff) {
        _diff = diff;
    }

    public void flush() {
        _promotionName = null;
        _fromDate = null;
        _toDate = null;
        _serviceType = null;
        _applicableTo = null;
        _payer = "N";
        _payerHierarchy = "N";
        _registeresPayer = "N";
        _payee = "N";
        _payeeHierarchy = "N";
        _registeresPayee = "N";

        _diff = null;
        _applicableFromHour = null;
        _applicableFromDate = null;
        _applicableToHour = null;
        _applicableToDate = null;
        _createdBy = null;
        _ModifiedBy = null;
        _approvalStatus = null;
        _promotionType = null;
        _promotionID = null;
        _itemCode = null;
        _itemName = null;
        _itemQuantity = 0l;
        _itemPoints = 0l;
        _itemQuantityAsString = null;
        _itemPointsAsString = null;
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

    public String getApplicableTo() {
        return _applicableTo;
    }

    /**
     * @param allowedDays
     *            The allowedDays to set.
     */
    public void setApplicableTo(String allowedTo) {
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

    public ArrayList getPromotionNameList() {
        return _promotionNameList;
    }

    /**
     * @param userNamePrefixList
     *            The userNamePrefixList to set.
     */
    public void setPromotionNameList(ArrayList promotionNameList) {
        _promotionNameList = promotionNameList;
    }

    public String getSelectedPromotion() {
        return _selectedPromotion;
    }

    public void setSelectedPromotion(String selectedPromotion) {
        _selectedPromotion = selectedPromotion;
    }

    public int getListSize() {
        return _listSize;
    }

    public void setListSize(int listSize) {
        _listSize = listSize;
    }

    public ArrayList getDomainList() {
        return _domainList;
    }

    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }

    public ArrayList getCategoryList() {
        return _categoryList;
    }

    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    public String getNetworkName() {
        return _networkName;
    }

    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public ArrayList getApplicableList() {

        return _applicableList;
    }

    public void setApplicableList(ArrayList applicableList) {
        _applicableList = applicableList;

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

    public ArrayList getAmountTypeList() {
        return _amountTypeList;
    }

    /**
     * @param amountTypeList
     *            The amountTypeList to set.
     */
    public void setAmountTypeList(ArrayList amountTypeList) {
        _amountTypeList = amountTypeList;
    }

    public int getLocationIndex() {
        return _locationIndex;
    }

    /**
     * @param locationIndex
     *            The locationIndex to set.
     */
    public void setLocationIndex(int locationIndex) {
        _locationIndex = locationIndex;
    }

    /**
     * @param slabsList
     *            The slabsList to set.
     */

    public ArrayList getPayeeSlabsList() {
        return _payeeSlabsList;
    }

    /**
     * @param slabsList
     *            The slabsList to set.
     */
    public void setPayeeSlabsList(ArrayList slabsList) {
        _payeeSlabsList = slabsList;
    }

    public ArrayList getPayeeHierSlabsList() {
        return _payeeHierSlabsList;
    }

    public void setPayeeHierSlabsList(ArrayList payeeHierSlabsList) {
        _payeeHierSlabsList = payeeHierSlabsList;
    }

    public ArrayList getPayeeRegSlabsList() {
        return _payeeRegSlabsList;
    }

    public void setPayeeRegSlabsList(ArrayList payeeRegSlabsList) {
        _payeeRegSlabsList = payeeRegSlabsList;
    }

    public ArrayList getPayerSlabsList() {
        return _payerSlabsList;
    }

    public void setPayerSlabsList(ArrayList payerSlabsList) {
        _payerSlabsList = payerSlabsList;
    }

    public ArrayList getPayerHierSlabsList() {
        return _payerHierSlabsList;
    }

    public void setPayerHierSlabsList(ArrayList payerHierSlabsList) {
        _payerHierSlabsList = payerHierSlabsList;
    }

    public ArrayList getPayerRegSlabsList() {
        return _payerRegSlabsList;
    }

    public void setPayerRegSlabsList(ArrayList payerRegSlabsList) {
        _payerRegSlabsList = payerRegSlabsList;
    }

    public void setPayeeSlabsListIndexed(int i, RewardDetailsVO commissionProfileDeatilsVO) {
        _payeeSlabsList.set(i, commissionProfileDeatilsVO);
    }

    public RewardDetailsVO getPayeeSlabsListIndexed(int i) {
        return (RewardDetailsVO) _payeeSlabsList.get(i);
    }

    public void setPayeeHierSlabsListIndexed(int i, RewardDetailsVO commissionProfileDeatilsVO) {
        _payeeHierSlabsList.set(i, commissionProfileDeatilsVO);
    }

    public RewardDetailsVO getPayeeHierSlabsListIndexed(int i) {
        return (RewardDetailsVO) _payeeHierSlabsList.get(i);
    }

    public void setPayeeRegSlabsListIndexed(int i, RewardDetailsVO commissionProfileDeatilsVO) {
        _payeeRegSlabsList.set(i, commissionProfileDeatilsVO);
    }

    public RewardDetailsVO getPayeeRegSlabsListIndexed(int i) {
        return (RewardDetailsVO) _payeeRegSlabsList.get(i);
    }

    public void setPayerSlabsListIndexed(int i, RewardDetailsVO commissionProfileDeatilsVO) {
        _payerSlabsList.set(i, commissionProfileDeatilsVO);
    }

    public RewardDetailsVO getPayerSlabsListIndexed(int i) {
        return (RewardDetailsVO) _payerSlabsList.get(i);
    }

    public void setPayerHierSlabsListIndexed(int i, RewardDetailsVO commissionProfileDeatilsVO) {
        _payerHierSlabsList.set(i, commissionProfileDeatilsVO);
    }

    public RewardDetailsVO getPayerHierSlabsListIndexed(int i) {
        return (RewardDetailsVO) _payerHierSlabsList.get(i);
    }

    public void setPayerRegSlabsListIndexed(int i, RewardDetailsVO commissionProfileDeatilsVO) {
        _payerRegSlabsList.set(i, commissionProfileDeatilsVO);
    }

    public RewardDetailsVO getPayerRegSlabsListIndexed(int i) {
        return (RewardDetailsVO) _payerRegSlabsList.get(i);
    }

    public ArrayList getPromotionTypeList() {
        return _promotionTypeList;
    }

    public void setPromotionTypeList(ArrayList promotionTypeList) {
        _promotionTypeList = promotionTypeList;
    }

    public String getRewardID() {
        return _rewardID;
    }

    public void setRewardID(String rewardID) {
        _rewardID = rewardID;
    }

    public String getPromotionAsscId() {
        return _promotionAsscId;
    }

    public void setPromotionAsscId(String promotionAsscId) {
        _promotionAsscId = promotionAsscId;
    }

    public ArrayList getPromtionAssociationList() {
        return _promtionAssociationList;
    }

    public void setPromtionAssociationList(ArrayList promtionAssociationList) {
        _promtionAssociationList = promtionAssociationList;
    }

    public ArrayList getRewardRangeList() {
        return _rewardRangeList;
    }

    public void setRewardRangeList(ArrayList rewardRangeList) {
        _rewardRangeList = rewardRangeList;
    }

    public String getDomainName() {
        return _domainName;
    }

    public void setDomainName(String domainName) {
        _domainName = domainName;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    public ArrayList getItemList() {
        return _itemList;
    }

    public void setItemList(ArrayList itemList) {
        _itemList = itemList;
    }

    public ArrayList getStatusTypeList() {
        return _statusTypeList;
    }

    public void setStatusTypeList(ArrayList statusTypeList) {
        _statusTypeList = statusTypeList;
    }

    public String getStatusType() {
        return _statusType;
    }

    public void setStatusType(String statusType) {
        _statusType = statusType;
    }

    public ArrayList getViewList() {
        return _viewList;
    }

    public void setViewList(ArrayList viewList) {
        _viewList = viewList;
    }

    public String getApprovalremarks() {
        return _approvalremarks;
    }

    public void setApprovalremarks(String _approvalremarks) {
        this._approvalremarks = _approvalremarks;
    }

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

}
