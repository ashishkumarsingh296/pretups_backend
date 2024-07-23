package com.web.pretups.channel.reports.web;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.ListValueVO;

public class ChannelUserReportsVO {

	private String domainCode;
	private String category;
	private String geography;
	private String TxnSubType;
	private String TxnSubTypeName;
	private String TransferInOrOut;
	private String TransferInOrOutName;
	private String _loggedInUserCategoryCode;
	private String _loggedInUserCategoryName;
	private String _loggedInUserName;
	private String _loggedInUserDomainID;
	private String _loggedInUserDomainName;
	private String userType;
	private String toDate;
	private String RpttoDate;
	private String RptfromDate;
	private String dailyDate;
	private String RptcurrentDate;
	private boolean _isReportingDB = false;
	private String _currentDateRptChkBox;
	private String ReportHeaderName;
	private String FromtransferCategoryName;
	private String UserName;
	private String TransferCategoryName;
	private String CategorySeqNo;
	private String networkCode = null;
	private String networkName;
	private String domainListString;
	private String loginUserID = null;
	private String fromtransferCategoryCode;
	private String totransferCategoryCode;

	private List<ListValueVO> _transferCategoryList = null;
	private List<ListValueVO> domainList = null;
	private List<ListValueVO> categoryList = null;
	private List<ListValueVO> TxnSubTypeList = null;
	private ArrayList zoneList = null;
	private ArrayList fromCategoryList = null;
	private ArrayList toCategoryList = null;

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getLoggedInUserCategoryCode() {
		return _loggedInUserCategoryCode;
	}

	public void setLoggedInUserCategoryCode(String _loggedInUserCategoryCode) {
		this._loggedInUserCategoryCode = _loggedInUserCategoryCode;
	}

	public String getLoggedInUserCategoryName() {
		return _loggedInUserCategoryName;
	}

	public void setLoggedInUserCategoryName(String _loggedInUserCategoryName) {
		this._loggedInUserCategoryName = _loggedInUserCategoryName;
	}

	public String getLoggedInUserName() {
		return _loggedInUserName;
	}

	public void setLoggedInUserName(String _loggedInUserName) {
		this._loggedInUserName = _loggedInUserName;
	}

	public String get_loggedInUserDomainID() {
		return _loggedInUserDomainID;
	}

	public void set_loggedInUserDomainID(String _loggedInUserDomainID) {
		this._loggedInUserDomainID = _loggedInUserDomainID;
	}

	public String get_loggedInUserDomainName() {
		return _loggedInUserDomainName;
	}

	public void set_loggedInUserDomainName(String _loggedInUserDomainName) {
		this._loggedInUserDomainName = _loggedInUserDomainName;
	}

	public boolean get_isReportingDB() {
		return _isReportingDB;
	}

	public void set_isReportingDB(boolean _isReportingDB) {
		this._isReportingDB = _isReportingDB;
	}

	public String get_currentDateRptChkBox() {
		return _currentDateRptChkBox;
	}

	public void set_currentDateRptChkBox(String _currentDateRptChkBox) {
		this._currentDateRptChkBox = _currentDateRptChkBox;
	}

	public String getTransferInOrOut() {
		return TransferInOrOut;
	}

	public void setTransferInOrOut(String transferInOrOut) {
		TransferInOrOut = transferInOrOut;
	}

	public String getTransferInOrOutName() {
		return TransferInOrOutName;
	}

	public void setTransferInOrOutName(String transferInOrOutName) {
		TransferInOrOutName = transferInOrOutName;
	}

	public String getTxnSubTypeName() {
		return TxnSubTypeName;
	}

	public void setTxnSubTypeName(String txnSubTypeName) {
		TxnSubTypeName = txnSubTypeName;
	}

	private String transferCategory;
	private String fromDate;

	public String getDailyDate() {
		return dailyDate;
	}

	public void setDailyDate(String dailyDate) {
		this.dailyDate = dailyDate;
	}

	public String getRptcurrentDate() {
		return RptcurrentDate;
	}

	public void setRptcurrentDate(String rptcurrentDate) {
		RptcurrentDate = rptcurrentDate;
	}

	public String getRpttoDate() {
		return RpttoDate;
	}

	public void setRpttoDate(String rpttoDate) {
		RpttoDate = rpttoDate;
	}

	public String getRptfromDate() {
		return RptfromDate;
	}

	public void setRptfromDate(String rptfromDate) {
		RptfromDate = rptfromDate;
	}

	public String getTotransferCategoryCode() {
		return totransferCategoryCode;
	}

	public void setTotransferCategoryCode(String totransferCategoryCode) {
		this.totransferCategoryCode = totransferCategoryCode;
	}

	private String userId;

	public String get_loggedInUserCategoryCode() {
		return _loggedInUserCategoryCode;
	}

	public void set_loggedInUserCategoryCode(String _loggedInUserCategoryCode) {
		this._loggedInUserCategoryCode = _loggedInUserCategoryCode;
	}

	public String get_loggedInUserCategoryName() {
		return _loggedInUserCategoryName;
	}

	public void set_loggedInUserCategoryName(String _loggedInUserCategoryName) {
		this._loggedInUserCategoryName = _loggedInUserCategoryName;
	}

	public String get_loggedInUserName() {
		return _loggedInUserName;
	}

	public void set_loggedInUserName(String _loggedInUserName) {
		this._loggedInUserName = _loggedInUserName;
	}

	public List<ListValueVO> get_transferCategoryList() {
		return _transferCategoryList;
	}

	public void set_transferCategoryList(List<ListValueVO> _transferCategoryList) {
		this._transferCategoryList = _transferCategoryList;
	}

	private String ZoneCode;
	private String ZoneName;

	public String getZoneName() {
		return ZoneName;
	}

	public void setZoneName(String zoneName) {
		ZoneName = zoneName;
	}

	private String domainName;

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public List<ListValueVO> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<ListValueVO> categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList getToCategoryList() {
		return toCategoryList;
	}

	public void setToCategoryList(ArrayList toCategoryList) {
		this.toCategoryList = toCategoryList;
	}

	public ArrayList getFromCategoryList() {
		return fromCategoryList;
	}

	public void setFromCategoryList(ArrayList fromCategoryList) {
		this.fromCategoryList = fromCategoryList;
	}

	public ArrayList getZoneList() {
		return zoneList;
	}

	public void setZoneList(ArrayList zoneList) {
		this.zoneList = zoneList;
	}

	public List<ListValueVO> getTxnSubTypeList() {
		return TxnSubTypeList;
	}

	public void setTxnSubTypeList(List<ListValueVO> txnSubTypeList) {
		TxnSubTypeList = txnSubTypeList;
	}

	public List<ListValueVO> getDomainList() {
		return domainList;
	}

	public void setDomainList(List<ListValueVO> domainList) {
		this.domainList = domainList;
	}

	public List<ListValueVO> getTransferCategoryList() {
		return _transferCategoryList;
	}

	public void setTransferCategoryList(List<ListValueVO> _transferCategoryList) {
		this._transferCategoryList = _transferCategoryList;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getDomainListString() {
		return domainListString;
	}

	public void setDomainListString(String domainListString) {
		this.domainListString = domainListString;
	}

	public String getLoginUserID() {
		return loginUserID;
	}

	public void setLoginUserID(String loginUserID) {
		this.loginUserID = loginUserID;
	}

	public String getTransferCategory() {
		return transferCategory;
	}

	public void setTransferCategory(String transferCategory) {
		this.transferCategory = transferCategory;
	}

	public String getFromtransferCategoryCode() {
		return fromtransferCategoryCode;
	}

	public void setFromtransferCategoryCode(String fromtransferCategoryCode) {
		this.fromtransferCategoryCode = fromtransferCategoryCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTxnSubType() {
		return TxnSubType;
	}

	public void setTxnSubType(String txnSubType) {
		TxnSubType = txnSubType;
	}

	public String getZoneCode() {
		return ZoneCode;
	}

	public void setZoneCode(String zoneCode) {
		ZoneCode = zoneCode;
	}

	public String getReportHeaderName() {
		return ReportHeaderName;
	}

	public void setReportHeaderName(String reportHeaderName) {
		ReportHeaderName = reportHeaderName;
	}

	public String getFromtransferCategoryName() {
		return FromtransferCategoryName;
	}

	public void setFromtransferCategoryName(String fromtransferCategoryName) {
		FromtransferCategoryName = fromtransferCategoryName;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getTransferCategoryName() {
		return TransferCategoryName;
	}

	public void setTransferCategoryName(String transferCategoryName) {
		TransferCategoryName = transferCategoryName;
	}

	public String getCategorySeqNo() {
		return CategorySeqNo;
	}

	public void setCategorySeqNo(String categorySeqNo) {
		CategorySeqNo = categorySeqNo;
	}

	@Override
	public String toString() {
		return "ChannelUserReportsVO [domainCode=" + domainCode + ", category=" + category + ", geography=" + geography
				+ ", TxnSubType=" + TxnSubType + ", TxnSubTypeName=" + TxnSubTypeName + ", TransferInOrOut="
				+ TransferInOrOut + ", TransferInOrOutName=" + TransferInOrOutName + ", userType=" + userType
				+ ", _loggedInUserCategoryCode=" + _loggedInUserCategoryCode + ", _loggedInUserCategoryName="
				+ _loggedInUserCategoryName + ", _loggedInUserName=" + _loggedInUserName + ", _loggedInUserDomainID="
				+ _loggedInUserDomainID + ", _loggedInUserDomainName=" + _loggedInUserDomainName + ", transferCategory="
				+ transferCategory + ", fromDate=" + fromDate + ", toDate=" + toDate + ", RpttoDate=" + RpttoDate
				+ ", RptfromDate=" + RptfromDate + ", dailyDate=" + dailyDate + ", RptcurrentDate=" + RptcurrentDate
				+ ", _isReportingDB=" + _isReportingDB + ", _currentDateRptChkBox=" + _currentDateRptChkBox
				+ ", networkCode=" + networkCode + ", networkName=" + networkName + ", domainListString="
				+ domainListString + ", loginUserID=" + loginUserID + ", fromtransferCategoryCode="
				+ fromtransferCategoryCode + ", totransferCategoryCode=" + totransferCategoryCode + ", userId=" + userId
				+ ", ZoneCode=" + ZoneCode + ", ZoneName=" + ZoneName + ", domainName=" + domainName
				+ ", _transferCategoryList=" + _transferCategoryList + ", domainList=" + domainList
				+ ", TxnSubTypeList=" + TxnSubTypeList + ", zoneList=" + zoneList + ", fromCategoryList="
				+ fromCategoryList + ", toCategoryList=" + toCategoryList + ", ReportHeaderName=" + ReportHeaderName
				+ ", FromtransferCategoryName=" + FromtransferCategoryName + ", UserName=" + UserName
				+ ", TransferCategoryName=" + TransferCategoryName + ", isReportingDB=" + _isReportingDB
				+ ", CategorySeqNo=" + CategorySeqNo + "]";
	}

}
