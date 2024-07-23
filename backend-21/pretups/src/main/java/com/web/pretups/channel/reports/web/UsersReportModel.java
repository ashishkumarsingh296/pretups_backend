package com.web.pretups.channel.reports.web;



import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.reports.businesslogic.C2STransferReportsUserVO;
import com.btsl.pretups.channel.reports.businesslogic.ChannelReportsUserVO;
import com.btsl.pretups.channel.reports.businesslogic.StaffSelfC2CReportVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelUserOperatorUserRolesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserZeroBalanceCounterSummaryVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;


/**
 * @author rahul.arya
 *
 */
public class UsersReportModel implements Serializable {
	 private String userID;
	    private String zoneCode;
	    private String domainCode;
	    private String ownerID;
	    private String parentCategoryCode;
	    private String parentUserID;
	    private String transferUserCategoryCode;
	    private String userTransferMode;
	    private String createdBy;
	    private Date createdOn = null;
	    private String modifiedBy;
	    private Date modifiedOn = null;
	    private String requestFor;
	    private String loginID;
	    private String _c2sMisToDate = null;
	    private String c2sMisToMonth = null;
	    private String c2sMisFromMonth = null;
	    private String thresholdName;
	    private transient List<StaffSelfC2CReportVO> staffSelfC2CList;
		private transient List<?> userstatusList=null;
	    private transient List<?> sorttypeList=null;
	    private transient List<ChannelUserOperatorUserRolesVO> externalUserReportList;
	    private String sorttypeName=null;
	    public String getThresholdName() {
			return thresholdName;
		}

		public void setThresholdName(String thresholdName) {
			this.thresholdName = thresholdName;
		}

		public String get_c2sMisToDate() {
			return _c2sMisToDate;
		}

		public void set_c2sMisToDate(String _c2sMisToDate) {
			this._c2sMisToDate = _c2sMisToDate;
		}

		public String get_c2sMisFromDate() {
			return _c2sMisFromDate;
		}

		public void set_c2sMisFromDate(String _c2sMisFromDate) {
			this._c2sMisFromDate = _c2sMisFromDate;
		}
		private String _c2sMisFromDate = null;
	  private Timestamp fromDateTimeStamp=null;
	  private Timestamp toDateTimeStamp=null;
	    public Timestamp getFromDateTimeStamp() {
		return fromDateTimeStamp;
	}

	public void setFromDateTimeStamp(Timestamp fromDateTimeStamp) {
		this.fromDateTimeStamp = fromDateTimeStamp;
	}

	public Timestamp getToDateTimeStamp() {
		return toDateTimeStamp;
	}

	public void setToDateTimeStamp(Timestamp toDateTimeStamp) {
		this.toDateTimeStamp = toDateTimeStamp;
	}
		private ArrayList domainList = null;
	    private ArrayList parentCategoryList = null;
	    private ArrayList transferUserCategoryList = null;
	    private ArrayList userTransferModeList = null;
	    private ArrayList userHierarchyList = null; 
	   
	    private ArrayList ownerList = null; 
	    private ArrayList<Object> parentUserList = null;
	    private ArrayList<Object> categoryList = null;
	    private ArrayList<Object> zoneList = null;
	    private ArrayList userList = null; 
	    private ArrayList statusList = null; 
	    private String zoneCodeDesc;
	    private String userName;

	    private String domainCodeDesc;
	    private String ownerName;
	    private String ownerCategory;
	    private String parentUserName;
	    private String transferUserCategoryDesc;
	    private String parentCategoryDesc;
	    public Timestamp getFromDateTimestamp() {
			return fromDateTimestamp;
		}

		public void setFromDateTimestamp(Timestamp fromDateTimestamp) {
			this.fromDateTimestamp = fromDateTimestamp;
		}

		public Timestamp getToDateTimestamp() {
			return toDateTimestamp;
		}

		public void setToDateTimestamp(Timestamp toDateTimestamp) {
			this.toDateTimestamp = toDateTimestamp;
		}

		public List<ChannelReportsUserVO> getZeroBalanceCounterDetailsList() {
			return zeroBalanceCounterDetailsList;
		}

		public void setZeroBalanceCounterDetailsList(
				List<ChannelReportsUserVO> zeroBalanceCounterDetailsList) {
			this.zeroBalanceCounterDetailsList = zeroBalanceCounterDetailsList;
		}
		private String status;
	    private boolean isChannelUser = false;
	    private Timestamp fromDateTimestamp = null;
		private Timestamp toDateTimestamp = null;
	    private List<ChannelReportsUserVO> zeroBalanceCounterDetailsList = null;
	    
	    private ArrayList<UserZeroBalanceCounterSummaryVO> userZeroBalanceCounterSummaryList;
	    private ArrayList<UserZeroBalanceCounterSummaryVO>  userZeroBalanceCounterSummaryListOne;
		public ArrayList<UserZeroBalanceCounterSummaryVO> getUserZeroBalanceCounterSummaryListOne() {
			return userZeroBalanceCounterSummaryListOne;
		}

		public void setUserZeroBalanceCounterSummaryListOne(
				ArrayList<UserZeroBalanceCounterSummaryVO> userZeroBalanceCounterSummaryListOne) {
			this.userZeroBalanceCounterSummaryListOne = userZeroBalanceCounterSummaryListOne;
		}
		private boolean isOperationNotAllow = false;

	
	    private ArrayList trnsfrdUsrHierList = null;
	    private String prevBalanceStr = null;
	    private String prevUserName = null;
	    private String prevParentname = null;
	    private String prevCategoryCode = null;
	    private String prevUserNameWithCat = null;
	    private String prevUserAndParentNameWithCat = null;
	    private boolean isHirDownloadAllow = false;
    private String zoneName = null;
    private String divisionName;
    private String departmentName;
    private String networkName = null;
    private String networkCode = null;
    private String reportHeaderName = null;
    private String fromDate = null;
    private String toDate = null;
    private String rptfromDate = null;
    private String rpttoDate = null;
    private String sortType = null;
    private String userStatus;
    private String userStatusName;
    private String divisionCode;
    private String departmentCode;
    private String domainName = null;
    private String categoryName = null;
    private String serviceType;
    private String serviceTypeName;
    private String userType;
    private String reportType;
    private String filterType;
    private String filterTypeName;
    private String reportTypeName;
    private String currentDate = null;
    private String rptcurrentDate = null;
    private String channelCategoryUser = null;
    private String transferStatus = null;
    private String transferCategory = null;
    private String transferStatusName;
    private String transferCategoryName;
    private String totransferCategoryCode;
    private String fromtransferCategoryCode;
    private String totransferCategoryName;
    private String fromtransferCategoryName;
    private String loginUserID = null;
    private String touserName;
    private String touserID;
    private String requestType;
    private String fromMonth;
    private String toMonth;
    private String tempfromDate;
    private String dailyDate;
    private String monthlyDate;
    private int domainListSize;
	private String mobileNo;
    private String dateType;
    private String loggedInUserCategoryCode;
    private String loggedInUserCategoryName;
    private String loggedInUserName;
    private String loggedInUserDomainID;
    private String loggedInUserDomainName;
    private String filePath;
    private String subDir;
    private String scheduleDate;
    private String hours;
    private String minute;
    private String endDate;
    private String module;
    private String moduleName;
    private String blackListStatus;
    private String blackListStatusName;
    private String batchID;
    private String domainListString;

    private int userListSize;
    private int zoneListSize;
    private int toUserListSize;

    private ArrayList userStatusList = null;
    private ArrayList divisionList = null;
    private ArrayList departmentList = null;
    private ArrayList serviceTypeList = null;
    private ArrayList reportTypeList = null;
    private ArrayList transferStatusList = null;
    private ArrayList transferCategoryList = null;
    private ArrayList toUserList = null;
   
	private ArrayList reportList = null;
    private ArrayList fromCategoryList = null;
    private ArrayList toCategoryList = null;
    private ArrayList transferRulCatList = null;
    private int reportListSize;
    private ArrayList moduleList = null;
    private ArrayList batchIdList = null;
    private ArrayList tempBatchIdList = null;

    private String serviceName;
    private int serviceTypeListSize;
    private String radioNetCode = null;
    private String txnSubType;
    private String txnSubTypeName;
    private ArrayList txnSubTypeList = null;
    private int txnSubTypeListSize;
    private String noOfTxn;
    private String transferInOrOut;
    private String channelType;
    private String transferInOrOutName;
    private String batchIDText;
    private String categorySeqNo;

    private ArrayList roamNetworkList = null;
    private String roamerType;
    private String roamerTypeName;
    private String roamnetworkName;
    private String roamnetworkCode;
    private int roamNetworkListSize;
    
    private String agentCatCode;

    private String temptoDate;
    private long time;
    private String loginId = null;

    private String msisdn;
    private String fromMsisdn;
    private String toMsisdn;

    private boolean isReportingDB = false;
    private String currentDateRptChkBox;

    private String noOfNonTxnUsers = null;
    private String radioCodeForTxn = "N";
    private String fileType;

    private String productType;
    private String userCategory;
    private String geoDomainType;
    private String orderType;
    private String transferNumber;
    private ArrayList productTypeList = null;

    private boolean isCorporate = false;
    private boolean isSoho = false;
    private boolean isNormal = false;
    private Object otherInfo = null;
    private String reportInitials;
    private String schudleDate;
    private String rptSchDate = null;
    private String scheduleType;
    private String fromTime = null;
    private String toTime = null;
    private Date fromDateTime = null;
    private Date toDateTime = null;
    private ArrayList bonusTypeList;
    private String bonusCode;
    private String bonusName;
    private int userStatusListSize;
    private String staffReport = "N";
    private ArrayList thresholdTypeList = null;
    private String thresholdType = null;
    private String fromAmount;
    private String toAmount;
    private double rptFromAmount;
    private double rptToAmount;
    private boolean fromToTimeBlank = false;
    private String userMsisdn;
    private String userEvent;
    private String userEventDesc;
    private ArrayList subEventTypeList;
    private String bundlesId;
    private String bundlesName;
    private ArrayList bundlesNameList = null;
    private String rptCode;
    private boolean otfStatus;
    private int transferListSize;
    private String usersName;
    private String prntCatCode;
    private String fromTrfCatCodeValue;
    private String toTrfCatCodeValue;
    private String searchUserName;
    private String searchToUserName;

    public String getSearchUserName() {
		return searchUserName;
	}

	public void setSearchUserName(String searchUserName) {
		this.searchUserName = searchUserName;
	}

	public String getSearchToUserName() {
		return searchToUserName;
	}

	public void setSearchToUserName(String searchToUserName) {
		this.searchToUserName = searchToUserName;
	}

	public String getFromTrfCatCodeValue() {
		return fromTrfCatCodeValue;
	}

	public void setFromTrfCatCodeValue(String fromTrfCatCodeValue) {
		this.fromTrfCatCodeValue = fromTrfCatCodeValue;
	}

	public String getToTrfCatCodeValue() {
		return toTrfCatCodeValue;
	}

	public void setToTrfCatCodeValue(String toTrfCatCodeValue) {
		this.toTrfCatCodeValue = toTrfCatCodeValue;
	}

	public String getPrntCatCode() {
		return prntCatCode;
	}

	public void setPrntCatCode(String prntCatCode) {
		this.prntCatCode = prntCatCode;
	}

	public String getUsersName() {
		return usersName;
	}

	public void setUsersName(String usersName) {
		this.usersName = usersName;
	}

	public String getMonthlyDate() {
		return monthlyDate;
	}

	public void setMonthlyDate(String monthlyDate) {
		this.monthlyDate = monthlyDate;
	}
    public int getTransferListSize() {
		return transferListSize;
	}

	public void setTransferListSize(int transferListSize) {
		this.transferListSize = transferListSize;
	}

	public boolean getOtfStatus() {
    	return otfStatus;
    }
    
    public void setOtfStatus(boolean otfStatus) {
    	this.otfStatus = otfStatus;
    }
    
    public String getrptCode() {
		return rptCode;
	}

	public void setrptCode(String rptCode) {
		this.rptCode = rptCode;
	}
private ArrayList<ChannelTransferVO> transferList;
    
	public ArrayList<ChannelTransferVO> getTransferList() {
		return transferList;
	}

	public void setTransferList(ArrayList<ChannelTransferVO> transferList) {
		this.transferList = transferList;
	}
    
	 public void setDomainListSize(int domainListSize) {
			this.domainListSize = domainListSize;
		}
    public int getDomainListSize() {
        if (domainList != null) {
            return domainList.size();
        }
        return 0;
    }

    public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	public String getParentCategoryCode() {
		return parentCategoryCode;
	}

	public void setParentCategoryCode(String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}

	public String getParentUserID() {
		return parentUserID;
	}

	public void setParentUserID(String parentUserID) {
		this.parentUserID = parentUserID;
	}

	public String getTransferUserCategoryCode() {
		return transferUserCategoryCode;
	}

	public void setTransferUserCategoryCode(String transferUserCategoryCode) {
		this.transferUserCategoryCode = transferUserCategoryCode;
	}

	public String getUserTransferMode() {
		return userTransferMode;
	}

	public void setUserTransferMode(String userTransferMode) {
		this.userTransferMode = userTransferMode;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getRequestFor() {
		return requestFor;
	}

	public void setRequestFor(String requestFor) {
		this.requestFor = requestFor;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public ArrayList getDomainList() {
		return domainList;
	}

	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
	}

	public ArrayList getParentCategoryList() {
		return parentCategoryList;
	}

	public void setParentCategoryList(ArrayList parentCategoryList) {
		this.parentCategoryList = parentCategoryList;
	}

	public ArrayList getTransferUserCategoryList() {
		return transferUserCategoryList;
	}

	public void setTransferUserCategoryList(ArrayList transferUserCategoryList) {
		this.transferUserCategoryList = transferUserCategoryList;
	}

	public ArrayList getUserTransferModeList() {
		return userTransferModeList;
	}

	public void setUserTransferModeList(ArrayList userTransferModeList) {
		this.userTransferModeList = userTransferModeList;
	}

	public ArrayList getUserHierarchyList() {
		return userHierarchyList;
	}

	public void setUserHierarchyList(ArrayList userHierarchyList) {
		this.userHierarchyList = userHierarchyList;
	}

	public ArrayList getOwnerList() {
		return ownerList;
	}

	public void setOwnerList(ArrayList ownerList) {
		this.ownerList = ownerList;
	}

	public ArrayList getParentUserList() {
		return parentUserList;
	}

	public void setParentUserList(ArrayList parentUserList) {
		this.parentUserList = parentUserList;
	}

	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList getZoneList() {
		return zoneList;
	}

	public void setZoneList(ArrayList zoneList) {
		this.zoneList = zoneList;
	}

	public ArrayList getUserList() {
		return userList;
	}

	public void setUserList(ArrayList userList) {
		this.userList = userList;
	}

	public ArrayList getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}

	public String getZoneCodeDesc() {
		return zoneCodeDesc;
	}

	public void setZoneCodeDesc(String zoneCodeDesc) {
		this.zoneCodeDesc = zoneCodeDesc;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDomainCodeDesc() {
		return domainCodeDesc;
	}

	public void setDomainCodeDesc(String domainCodeDesc) {
		this.domainCodeDesc = domainCodeDesc;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerCategory() {
		return ownerCategory;
	}

	public void setOwnerCategory(String ownerCategory) {
		this.ownerCategory = ownerCategory;
	}

	public String getParentUserName() {
		return parentUserName;
	}

	public void setParentUserName(String parentUserName) {
		this.parentUserName = parentUserName;
	}

	public String getTransferUserCategoryDesc() {
		return transferUserCategoryDesc;
	}

	public void setTransferUserCategoryDesc(String transferUserCategoryDesc) {
		this.transferUserCategoryDesc = transferUserCategoryDesc;
	}

	public String getParentCategoryDesc() {
		return parentCategoryDesc;
	}

	public void setParentCategoryDesc(String parentCategoryDesc) {
		this.parentCategoryDesc = parentCategoryDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isChannelUser() {
		return isChannelUser;
	}

	public void setChannelUser(boolean isChannelUser) {
		this.isChannelUser = isChannelUser;
	}

	public boolean isOperationNotAllow() {
		return isOperationNotAllow;
	}

	public void setOperationNotAllow(boolean isOperationNotAllow) {
		this.isOperationNotAllow = isOperationNotAllow;
	}

	public ArrayList getTrnsfrdUsrHierList() {
		return trnsfrdUsrHierList;
	}

	public void setTrnsfrdUsrHierList(ArrayList trnsfrdUsrHierList) {
		this.trnsfrdUsrHierList = trnsfrdUsrHierList;
	}

	public String getPrevBalanceStr() {
		return prevBalanceStr;
	}

	public void setPrevBalanceStr(String prevBalanceStr) {
		this.prevBalanceStr = prevBalanceStr;
	}

	public String getPrevUserName() {
		return prevUserName;
	}

	public void setPrevUserName(String prevUserName) {
		this.prevUserName = prevUserName;
	}

	public String getPrevParentname() {
		return prevParentname;
	}

	public void setPrevParentname(String prevParentname) {
		this.prevParentname = prevParentname;
	}

	public String getPrevCategoryCode() {
		return prevCategoryCode;
	}

	public void setPrevCategoryCode(String prevCategoryCode) {
		this.prevCategoryCode = prevCategoryCode;
	}

	public String getPrevUserNameWithCat() {
		return prevUserNameWithCat;
	}

	public void setPrevUserNameWithCat(String prevUserNameWithCat) {
		this.prevUserNameWithCat = prevUserNameWithCat;
	}

	public String getPrevUserAndParentNameWithCat() {
		return prevUserAndParentNameWithCat;
	}

	public void setPrevUserAndParentNameWithCat(String prevUserAndParentNameWithCat) {
		this.prevUserAndParentNameWithCat = prevUserAndParentNameWithCat;
	}

	public boolean isHirDownloadAllow() {
		return isHirDownloadAllow;
	}

	public void setHirDownloadAllow(boolean isHirDownloadAllow) {
		this.isHirDownloadAllow = isHirDownloadAllow;
	}

    public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getReportHeaderName() {
		return reportHeaderName;
	}

	public void setReportHeaderName(String reportHeaderName) {
		this.reportHeaderName = reportHeaderName;
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

	public String getRptfromDate() {
		return rptfromDate;
	}

	public void setRptfromDate(String rptfromDate) {
		this.rptfromDate = rptfromDate;
	}

	public String getRpttoDate() {
		return rpttoDate;
	}

	public void setRpttoDate(String rpttoDate) {
		this.rpttoDate = rpttoDate;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserStatusName() {
		return userStatusName;
	}

	public void setUserStatusName(String userStatusName) {
		this.userStatusName = userStatusName;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceTypeName() {
		return serviceTypeName;
	}

	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFilterTypeName() {
		return filterTypeName;
	}

	public void setFilterTypeName(String filterTypeName) {
		this.filterTypeName = filterTypeName;
	}

	public String getReportTypeName() {
		return reportTypeName;
	}

	public void setReportTypeName(String reportTypeName) {
		this.reportTypeName = reportTypeName;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public String getRptcurrentDate() {
		return rptcurrentDate;
	}

	public void setRptcurrentDate(String rptcurrentDate) {
		this.rptcurrentDate = rptcurrentDate;
	}

	public String getChannelCategoryUser() {
		return channelCategoryUser;
	}

	public void setChannelCategoryUser(String channelCategoryUser) {
		this.channelCategoryUser = channelCategoryUser;
	}

	public String getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}

	public String getTransferCategory() {
		return transferCategory;
	}

	public void setTransferCategory(String transferCategory) {
		this.transferCategory = transferCategory;
	}

	public String getTransferStatusName() {
		return transferStatusName;
	}

	public void setTransferStatusName(String transferStatusName) {
		this.transferStatusName = transferStatusName;
	}

	public String getTransferCategoryName() {
		return transferCategoryName;
	}

	public void setTransferCategoryName(String transferCategoryName) {
		this.transferCategoryName = transferCategoryName;
	}

	public String getTotransferCategoryCode() {
		return totransferCategoryCode;
	}

	public void setTotransferCategoryCode(String totransferCategoryCode) {
		this.totransferCategoryCode = totransferCategoryCode;
	}

	public String getFromtransferCategoryCode() {
		return fromtransferCategoryCode;
	}

	public void setFromtransferCategoryCode(String fromtransferCategoryCode) {
		this.fromtransferCategoryCode = fromtransferCategoryCode;
	}

	public String getTotransferCategoryName() {
		return totransferCategoryName;
	}

	public void setTotransferCategoryName(String totransferCategoryName) {
		this.totransferCategoryName = totransferCategoryName;
	}

	public String getFromtransferCategoryName() {
		return fromtransferCategoryName;
	}

	public void setFromtransferCategoryName(String fromtransferCategoryName) {
		this.fromtransferCategoryName = fromtransferCategoryName;
	}

	public String getLoginUserID() {
		return loginUserID;
	}

	public void setLoginUserID(String loginUserID) {
		this.loginUserID = loginUserID;
	}

	public String getTouserName() {
		return touserName;
	}

	public void setTouserName(String touserName) {
		this.touserName = touserName;
	}

	public String getTouserID() {
		return touserID;
	}

	public void setTouserID(String touserID) {
		this.touserID = touserID;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getFromMonth() {
		return fromMonth;
	}

	public void setFromMonth(String fromMonth) {
		this.fromMonth = fromMonth;
	}

	public String getToMonth() {
		return toMonth;
	}

	public void setToMonth(String toMonth) {
		this.toMonth = toMonth;
	}

	public String getTempfromDate() {
		return tempfromDate;
	}

	public void setTempfromDate(String tempfromDate) {
		this.tempfromDate = tempfromDate;
	}

	public String getDailyDate() {
		return dailyDate;
	}

	public void setDailyDate(String dailyDate) {
		this.dailyDate = dailyDate;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public String getLoggedInUserCategoryCode() {
		return loggedInUserCategoryCode;
	}

	public void setLoggedInUserCategoryCode(String loggedInUserCategoryCode) {
		this.loggedInUserCategoryCode = loggedInUserCategoryCode;
	}

	public String getLoggedInUserCategoryName() {
		return loggedInUserCategoryName;
	}

	public void setLoggedInUserCategoryName(String loggedInUserCategoryName) {
		this.loggedInUserCategoryName = loggedInUserCategoryName;
	}

	public String getLoggedInUserName() {
		return loggedInUserName;
	}

	public void setLoggedInUserName(String loggedInUserName) {
		this.loggedInUserName = loggedInUserName;
	}

	public String getLoggedInUserDomainID() {
		return loggedInUserDomainID;
	}

	public void setLoggedInUserDomainID(String loggedInUserDomainID) {
		this.loggedInUserDomainID = loggedInUserDomainID;
	}

	public String getLoggedInUserDomainName() {
		return loggedInUserDomainName;
	}

	public void setLoggedInUserDomainName(String loggedInUserDomainName) {
		this.loggedInUserDomainName = loggedInUserDomainName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSubDir() {
		return subDir;
	}

	public void setSubDir(String subDir) {
		this.subDir = subDir;
	}

	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getBlackListStatus() {
		return blackListStatus;
	}

	public void setBlackListStatus(String blackListStatus) {
		this.blackListStatus = blackListStatus;
	}

	public String getBlackListStatusName() {
		return blackListStatusName;
	}

	public void setBlackListStatusName(String blackListStatusName) {
		this.blackListStatusName = blackListStatusName;
	}

	public String getBatchID() {
		return batchID;
	}

	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}

	public String getDomainListString() {
		return domainListString;
	}

	public void setDomainListString(String domainListString) {
		this.domainListString = domainListString;
	}

	public int getUserListSize() {
		return userListSize;
	}

	public void setUserListSize(ArrayList userList) {
		 if (userList != null) {
	            userListSize = userList.size();
	        } else {
	            userListSize = 0;
	        }
	}
	 
	    public int getZoneListSize() {
	        if (getZoneList() != null) {
	            return  (getZoneList().size());
	        } else {
	            return 0;
	        }
	    }

	public void setZoneListSize(int zoneListSize) {
		this.zoneListSize = zoneListSize;
	}

	public int getToUserListSize() {
		return toUserListSize;
	}

	public void setToUserListSize(int toUserListSize) {
		this.toUserListSize = toUserListSize;
	}

	public ArrayList getUserStatusList() {
		return userStatusList;
	}

	public void setUserStatusList(ArrayList userStatusList) {
		this.userStatusList = userStatusList;
	}

	public ArrayList getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(ArrayList divisionList) {
		this.divisionList = divisionList;
	}

	public ArrayList getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(ArrayList departmentList) {
		this.departmentList = departmentList;
	}

	public ArrayList getServiceTypeList() {
		return serviceTypeList;
	}

	public void setServiceTypeList(ArrayList serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}

	public ArrayList getReportTypeList() {
		return reportTypeList;
	}

	public void setReportTypeList(ArrayList reportTypeList) {
		this.reportTypeList = reportTypeList;
	}

	public ArrayList getTransferStatusList() {
		return transferStatusList;
	}

	public void setTransferStatusList(ArrayList transferStatusList) {
		this.transferStatusList = transferStatusList;
	}

	public ArrayList getTransferCategoryList() {
		return transferCategoryList;
	}

	public void setTransferCategoryList(ArrayList transferCategoryList) {
		this.transferCategoryList = transferCategoryList;
	}

	public ArrayList getToUserList() {
		return toUserList;
	}

	public void setToUserList(ArrayList toUserList) {
		this.toUserList = toUserList;
	}

	public ArrayList getReportList() {
		return reportList;
	}

	public void setReportList(ArrayList reportList) {
		this.reportList = reportList;
	}

	public ArrayList getFromCategoryList() {
		return fromCategoryList;
	}

	public void setFromCategoryList(ArrayList fromCategoryList) {
		this.fromCategoryList = fromCategoryList;
	}

	public ArrayList getToCategoryList() {
		return toCategoryList;
	}

	public void setToCategoryList(ArrayList toCategoryList) {
		this.toCategoryList = toCategoryList;
	}

	public ArrayList getTransferRulCatList() {
		return transferRulCatList;
	}

	public void setTransferRulCatList(ArrayList transferRulCatList) {
		this.transferRulCatList = transferRulCatList;
	}

	public int getReportListSize() {
		return reportListSize;
	}

	public void setReportListSize(int reportListSize) {
		this.reportListSize = reportListSize;
	}

	public ArrayList getModuleList() {
		return moduleList;
	}

	public void setModuleList(ArrayList moduleList) {
		this.moduleList = moduleList;
	}

	public ArrayList getBatchIdList() {
		return batchIdList;
	}

	public void setBatchIdList(ArrayList batchIdList) {
		this.batchIdList = batchIdList;
	}

	public ArrayList getTempBatchIdList() {
		return tempBatchIdList;
	}

	public void setTempBatchIdList(ArrayList tempBatchIdList) {
		this.tempBatchIdList = tempBatchIdList;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getServiceTypeListSize() {
		return serviceTypeListSize;
	}

	public void setServiceTypeListSize(int serviceTypeListSize) {
		this.serviceTypeListSize = serviceTypeListSize;
	}

	public String getRadioNetCode() {
		return radioNetCode;
	}

	public void setRadioNetCode(String radioNetCode) {
		this.radioNetCode = radioNetCode;
	}

	public String getTxnSubType() {
		return txnSubType;
	}

	public void setTxnSubType(String txnSubType) {
		this.txnSubType = txnSubType;
	}

	public String getTxnSubTypeName() {
		return txnSubTypeName;
	}

	public void setTxnSubTypeName(String txnSubTypeName) {
		this.txnSubTypeName = txnSubTypeName;
	}

	public ArrayList getTxnSubTypeList() {
		return txnSubTypeList;
	}

	public void setTxnSubTypeList(ArrayList txnSubTypeList) {
		this.txnSubTypeList = txnSubTypeList;
	}

	public int getTxnSubTypeListSize() {
		return txnSubTypeListSize;
	}

	public void setTxnSubTypeListSize(int txnSubTypeListSize) {
		this.txnSubTypeListSize = txnSubTypeListSize;
	}

	public String getNoOfTxn() {
		return noOfTxn;
	}

	public void setNoOfTxn(String noOfTxn) {
		this.noOfTxn = noOfTxn;
	}

	public String getTransferInOrOut() {
		return transferInOrOut;
	}

	public void setTransferInOrOut(String transferInOrOut) {
		this.transferInOrOut = transferInOrOut;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getTransferInOrOutName() {
		return transferInOrOutName;
	}

	public void setTransferInOrOutName(String transferInOrOutName) {
		this.transferInOrOutName = transferInOrOutName;
	}

	public String getBatchIDText() {
		return batchIDText;
	}

	public void setBatchIDText(String batchIDText) {
		this.batchIDText = batchIDText;
	}

	public String getCategorySeqNo() {
		return categorySeqNo;
	}

	public void setCategorySeqNo(String categorySeqNo) {
		this.categorySeqNo = categorySeqNo;
	}

	public ArrayList getRoamNetworkList() {
		return roamNetworkList;
	}

	public void setRoamNetworkList(ArrayList roamNetworkList) {
		this.roamNetworkList = roamNetworkList;
	}

	public String getRoamerType() {
		return roamerType;
	}

	public void setRoamerType(String roamerType) {
		this.roamerType = roamerType;
	}

	public String getRoamerTypeName() {
		return roamerTypeName;
	}

	public void setRoamerTypeName(String roamerTypeName) {
		this.roamerTypeName = roamerTypeName;
	}

	public String getRoamnetworkName() {
		return roamnetworkName;
	}

	public void setRoamnetworkName(String roamnetworkName) {
		this.roamnetworkName = roamnetworkName;
	}

	public String getRoamnetworkCode() {
		return roamnetworkCode;
	}

	public void setRoamnetworkCode(String roamnetworkCode) {
		this.roamnetworkCode = roamnetworkCode;
	}

	public int getRoamNetworkListSize() {
		return roamNetworkListSize;
	}

	public void setRoamNetworkListSize(int roamNetworkListSize) {
		this.roamNetworkListSize = roamNetworkListSize;
	}

	public String getAgentCatCode() {
		return agentCatCode;
	}

	public void setAgentCatCode(String agentCatCode) {
		this.agentCatCode = agentCatCode;
	}

	public String getTemptoDate() {
		return temptoDate;
	}

	public void setTemptoDate(String temptoDate) {
		this.temptoDate = temptoDate;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getFromMsisdn() {
		return fromMsisdn;
	}

	public void setFromMsisdn(String fromMsisdn) {
		this.fromMsisdn = fromMsisdn;
	}

	public String getToMsisdn() {
		return toMsisdn;
	}

	public void setToMsisdn(String toMsisdn) {
		this.toMsisdn = toMsisdn;
	}

	public boolean isReportingDB() {
		return isReportingDB;
	}

	public void setReportingDB(boolean isReportingDB) {
		this.isReportingDB = isReportingDB;
	}

	public String getCurrentDateRptChkBox() {
		return currentDateRptChkBox;
	}

	public void setCurrentDateRptChkBox(String currentDateRptChkBox) {
		this.currentDateRptChkBox = currentDateRptChkBox;
	}

	public String getNoOfNonTxnUsers() {
		return noOfNonTxnUsers;
	}

	public void setNoOfNonTxnUsers(String noOfNonTxnUsers) {
		this.noOfNonTxnUsers = noOfNonTxnUsers;
	}

	public String getRadioCodeForTxn() {
		return radioCodeForTxn;
	}

	public void setRadioCodeForTxn(String radioCodeForTxn) {
		this.radioCodeForTxn = radioCodeForTxn;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getUserCategory() {
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public String getGeoDomainType() {
		return geoDomainType;
	}

	public void setGeoDomainType(String geoDomainType) {
		this.geoDomainType = geoDomainType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getTransferNumber() {
		return transferNumber;
	}

	public void setTransferNumber(String transferNumber) {
		this.transferNumber = transferNumber;
	}

	public ArrayList getProductTypeList() {
		return productTypeList;
	}

	public void setProductTypeList(ArrayList productTypeList) {
		this.productTypeList = productTypeList;
	}

	public boolean isCorporate() {
		return isCorporate;
	}

	public void setCorporate(boolean isCorporate) {
		this.isCorporate = isCorporate;
	}

	public boolean isSoho() {
		return isSoho;
	}

	public void setSoho(boolean isSoho) {
		this.isSoho = isSoho;
	}

	public boolean isNormal() {
		return isNormal;
	}

	public void setNormal(boolean isNormal) {
		this.isNormal = isNormal;
	}

	public Object getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(Object otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getReportInitials() {
		return reportInitials;
	}

	public void setReportInitials(String reportInitials) {
		this.reportInitials = reportInitials;
	}

	public String getSchudleDate() {
		return schudleDate;
	}

	public void setSchudleDate(String schudleDate) {
		this.schudleDate = schudleDate;
	}

	public String getRptSchDate() {
		return rptSchDate;
	}

	public void setRptSchDate(String rptSchDate) {
		this.rptSchDate = rptSchDate;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	public Date getFromDateTime() {
		return fromDateTime;
	}

	public void setFromDateTime(Date fromDateTime) {
		this.fromDateTime = fromDateTime;
	}

	public Date getToDateTime() {
		return toDateTime;
	}

	public void setToDateTime(Date toDateTime) {
		this.toDateTime = toDateTime;
	}

	public ArrayList getBonusTypeList() {
		return bonusTypeList;
	}

	public void setBonusTypeList(ArrayList bonusTypeList) {
		this.bonusTypeList = bonusTypeList;
	}

	public String getBonusCode() {
		return bonusCode;
	}

	public void setBonusCode(String bonusCode) {
		this.bonusCode = bonusCode;
	}

	public String getBonusName() {
		return bonusName;
	}

	public void setBonusName(String bonusName) {
		this.bonusName = bonusName;
	}

	public int getUserStatusListSize() {
		return userStatusListSize;
	}

	public void setUserStatusListSize(int userStatusListSize) {
		this.userStatusListSize = userStatusListSize;
	}

	public String getStaffReport() {
		return staffReport;
	}

	public void setStaffReport(String staffReport) {
		this.staffReport = staffReport;
	}

	public ArrayList getThresholdTypeList() {
		return thresholdTypeList;
	}

	public void setThresholdTypeList(ArrayList thresholdTypeList) {
		this.thresholdTypeList = thresholdTypeList;
	}

	public String getThresholdType() {
		return thresholdType;
	}

	public void setThresholdType(String thresholdType) {
		this.thresholdType = thresholdType;
	}

	public String getFromAmount() {
		return fromAmount;
	}

	public void setFromAmount(String fromAmount) {
		this.fromAmount = fromAmount;
	}

	public String getToAmount() {
		return toAmount;
	}

	public void setToAmount(String toAmount) {
		this.toAmount = toAmount;
	}

	public double getRptFromAmount() {
		return rptFromAmount;
	}

	public void setRptFromAmount(double rptFromAmount) {
		this.rptFromAmount = rptFromAmount;
	}

	public double getRptToAmount() {
		return rptToAmount;
	}

	public void setRptToAmount(double rptToAmount) {
		this.rptToAmount = rptToAmount;
	}

	public boolean isFromToTimeBlank() {
		return fromToTimeBlank;
	}

	public void setFromToTimeBlank(boolean fromToTimeBlank) {
		this.fromToTimeBlank = fromToTimeBlank;
	}

	public String getUserMsisdn() {
		return userMsisdn;
	}

	public void setUserMsisdn(String userMsisdn) {
		this.userMsisdn = userMsisdn;
	}

	public String getUserEvent() {
		return userEvent;
	}

	public void setUserEvent(String userEvent) {
		this.userEvent = userEvent;
	}

	public String getUserEventDesc() {
		return userEventDesc;
	}

	public void setUserEventDesc(String userEventDesc) {
		this.userEventDesc = userEventDesc;
	}

	public ArrayList getSubEventTypeList() {
		return subEventTypeList;
	}

	public void setSubEventTypeList(ArrayList subEventTypeList) {
		this.subEventTypeList = subEventTypeList;
	}

	public String getBundlesId() {
		return bundlesId;
	}

	public void setBundlesId(String bundlesId) {
		this.bundlesId = bundlesId;
	}

	public String getBundlesName() {
		return bundlesName;
	}

	public void setBundlesName(String bundlesName) {
		this.bundlesName = bundlesName;
	}

	public ArrayList getBundlesNameList() {
		return bundlesNameList;
	}

	public void setBundlesNameList(ArrayList bundlesNameList) {
		this.bundlesNameList = bundlesNameList;
	}

	public ArrayList getBundleTypeList() {
		return bundleTypeList;
	}

	public void setBundleTypeList(ArrayList bundleTypeList) {
		this.bundleTypeList = bundleTypeList;
	}

	public String getBundleType() {
		return bundleType;
	}

	public void setBundleType(String bundleType) {
		this.bundleType = bundleType;
	}

	public String getBundleTypeName() {
		return bundleTypeName;
	}

	public void setBundleTypeName(String bundleTypeName) {
		this.bundleTypeName = bundleTypeName;
	}

	public String getSubService() {
		return subService;
	}

	public void setSubService(String subService) {
		this.subService = subService;
	}

	public ArrayList getSubServiceList() {
		return subServiceList;
	}

	public void setSubServiceList(ArrayList subServiceList) {
		this.subServiceList = subServiceList;
	}

	public String getSubServiceName() {
		return subServiceName;
	}

	public void setSubServiceName(String subServiceName) {
		this.subServiceName = subServiceName;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	private ArrayList bundleTypeList = null;
    private String bundleType;
    private String bundleTypeName;

    private String subService;
    private ArrayList subServiceList;
    private String subServiceName;

    private String externalCode;

	public ArrayList<UserZeroBalanceCounterSummaryVO> getUserZeroBalanceCounterSummaryList() {
		return userZeroBalanceCounterSummaryList;
	}

	public void setUserZeroBalanceCounterSummaryList(
			ArrayList<UserZeroBalanceCounterSummaryVO> userZeroBalanceCounterSummaryList) {
		this.userZeroBalanceCounterSummaryList = userZeroBalanceCounterSummaryList;
	}
	private String date;
	private int c2STransferReportsListSize;
	private List<C2STransferReportsUserVO> c2STransferReportsList=null;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getC2STransferReportsListSize() {
		return c2STransferReportsListSize;
	}

	public void setC2STransferReportsListSize(int c2sTransferReportsListSize) {
		c2STransferReportsListSize = c2sTransferReportsListSize;
	}

	public List<C2STransferReportsUserVO> getC2STransferReportsList() {
		return c2STransferReportsList;
	}

	public void setC2STransferReportsList(
			List<C2STransferReportsUserVO> c2sTransferReportsList) {
		c2STransferReportsList = c2sTransferReportsList;
	}
	
    
	/**
     * @return
     */
    public String getc2sMisToMonth() {
		return c2sMisToMonth;
	}

    
    
    
	
	/**
	 * @param c2sMisToMonth
	 */
	public void setc2sMisToMonth(String c2sMisToMonth) {
		this.c2sMisToMonth = c2sMisToMonth;
	}

	/**
	 * @return
	 */
	public String getc2sMisFromMonth() {
		return c2sMisFromMonth;
	}

	/**
	 * @param c2sMisFromMonth
	 */
	public void setc2sMisFromMonth(String c2sMisFromMonth) {
		this.c2sMisFromMonth = c2sMisFromMonth;
	}
	public List getSorttypeList() {
		return sorttypeList;
	}

	public void setSorttypeList(List sorttypeList) {
		this.sorttypeList = sorttypeList;
	}

	public List getUserstatusList() {
		return userstatusList;
	}

	public void setUserstatusList(List userstatusList) {
		this.userstatusList = userstatusList;
	}
	
	public List<ChannelUserOperatorUserRolesVO> getExternalUserReportList() {
		return externalUserReportList;
	}

	public void setExternalUserReportList(
			List<ChannelUserOperatorUserRolesVO> externalUserReportList) {
		this.externalUserReportList = externalUserReportList;
	}
	   
	    public String getSorttypeName() {
			return sorttypeName;
		}

		public void setSorttypeName(String sorttypeName) {
			this.sorttypeName = sorttypeName;
		}
   
		public List<StaffSelfC2CReportVO> getStaffSelfC2CList() {
			return staffSelfC2CList;
		}

		public void setStaffSelfC2CList(List<StaffSelfC2CReportVO> staffSelfC2CList) {
			this.staffSelfC2CList = staffSelfC2CList;
		}



		private List channelUserTypeList=null ;
		private List selectDomainList=null;
		private List origCategoryList=null;
		private List<ChannelTransferRuleVO> origParentCategoryList=null;
		private List associatedGeographicalList=null;
		private List<CategoryVO> catList =null;
		private String parentDomainTypeDesc;
		private String parentDomainCode;
		private String parentDomainDesc;
		private boolean domainShowFlag ;
		private int selectDomainListSize;
		private int associatedGeographicalListSize;
		private int origCategoryListSize;
		
		
//      User Information   add channel user//
		private String firstName;
		private String lastName;
		private String shortName;
		private String userNamePrefixCode;
		private List userNamePrefixList;
		private String empCode;
		private String insuspend;
		private String outsuspend;
		private String contactPerson;
		private String contactNo;
		private String ssn;
		private String designation;
		private String address1;
		private String address2;
		private String city;
		private String state;
		private String country;
		private String email;
		private String company;
		private String fax;
		private String appointmentDate;
		private String userLanguage;  
		private List userLanguageList;
		private String otherEmail;
		private String longitude;
		private String latitude;
		private String documentType;
		private String documentNo;
		private String paymentType;
		private String webLoginID;
		private String showPassword;
		private String confirmPassword;
		private String allowedIPs;     
       // private String allowedDays;
        private String[] allowedDays;
        private String allowedFormTime;
        private String allowedToTime;
        
        //Transction Information
        private String  primarynumber;
		private String number;
		private String smspin;
		private String confirmsmspin;
		private String profile;
		private String description;
        
        private String roleType;
        private String lowBalAlertToSelf;
        private String lowBalAlertToParent;
        private String lowBalAlertToOther;
        private List phoneProfileList;
        private List<UserPhoneVO> msisdnList;
        private String primaryRadio;
        private CategoryVO  categoryVO;;
        private String categoryCode;
        
        private String grphDomainTypeName;
        private List<UserGeographiesVO> geographicalList;
        private String geographicalCode;
        private List<CommissionProfileSetVO> commissionProfileList;
        
        //Assign Role       
        private Map<String, List> rolesMap;
        private Map<String, List> rolesMapSelected;   
        
         //Assign Services       
        private List<ListValueVO> servicesList;
        private List<ListValueVO> servicesListSelected;
            
        //Other fields
        
        private String parentID;
        private boolean userCodeFlag;
        private String userCode;
             
        private String userGradeId ;
        private String trannferProfileId;
        private String trannferRuleTypeId;
        private String lmsProfileId;
        private String rsaAuthentication;
        private String authTypeAllowed;
        private String mcommerceServiceAllow;
        private String mpayProfileIDWithGrad;
        private String subOutletCode;        
        private String userId;
        private String channelUserName;
        private	long lastModified;
        private String	pwdGenerateAllow;
        private String	webPassword;
    	private	Date passwordModifiedOn;
    	private String 	previousStatus;
    	private String 	level1ApprovedBy;
    	private Date level1ApprovedOn;
    	private String	level2ApprovedBy;
    	private	Date level2ApprovedOn;
    	private	List<UserPhoneVO> oldMsisdnList;
    	private String	outletCode;   	
    	private String	controlGroup;
    	private String	oldWebLoginID ;
        
    	private String []roleFlag;
    	private String []servicesTypes;
        private String []geographicalCodeArray;
    	
        private List<GradeVO>  userGradeList;
        private List<ListValueVO> trannferProfileList;
        private List<ListValueVO> trannferRuleTypeList;
        private List<ListValueVO> lmsProfileList;
        
        private String gradeCode;
        private String combinedKey;
        private String value;
        
        private boolean successFlag;
        private String successMsg;
        
        private String fromtransferParentCategoryCode;
        
        
        
        public String getFromtransferParentCategoryCode() {
			return fromtransferParentCategoryCode;
		}

		public void setFromtransferParentCategoryCode(
				String fromtransferParentCategoryCode) {
			this.fromtransferParentCategoryCode = fromtransferParentCategoryCode;
		}

		public boolean isSuccessFlag() {
			return successFlag;
		}

		public void setSuccessFlag(boolean successFlag) {
			this.successFlag = successFlag;
		}

		public String getSuccessMsg() {
			return successMsg;
		}

		public void setSuccessMsg(String successMsg) {
			this.successMsg = successMsg;
		}

		public String getGradeCode() {
			return gradeCode;
		}

		public void setGradeCode(String gradeCode) {
			this.gradeCode = gradeCode;
		}

		public String getCombinedKey() {
			return combinedKey;
		}

		public void setCombinedKey(String combinedKey) {
			this.combinedKey = combinedKey;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public List<ListValueVO> getLmsProfileList() {
			return lmsProfileList;
		}

		public void setLmsProfileList(List<ListValueVO> lmsProfileList) {
			this.lmsProfileList = lmsProfileList;
		}

		public List<ListValueVO> getTrannferRuleTypeList() {
			return trannferRuleTypeList;
		}

		public void setTrannferRuleTypeList(List<ListValueVO> trannferRuleTypeList) {
			this.trannferRuleTypeList = trannferRuleTypeList;
		}

		public List<ListValueVO> getTrannferProfileList() {
			return trannferProfileList;
		}

		public void setTrannferProfileList(List<ListValueVO> trannferProfileList) {
			this.trannferProfileList = trannferProfileList;
		}

		public List<GradeVO> getUserGradeList() {
			return userGradeList;
		}

		public void setUserGradeList(List<GradeVO> userGradeList) {
			this.userGradeList = userGradeList;
		}
        
        public String[] getGeographicalCodeArray() {
			return geographicalCodeArray;
		}

		public void setGeographicalCodeArray(String[] geographicalCodeArray) {
			this.geographicalCodeArray = geographicalCodeArray;
		}

		public String[] getRoleFlag() {
			return roleFlag;
		}

		public void setRoleFlag(String[] roleFlag) {
			this.roleFlag = roleFlag;
		}

		public String[] getServicesTypes() {
			return servicesTypes;
		}

		public void setServicesTypes(String[] servicesTypes) {
			this.servicesTypes = servicesTypes;
		}

		public String getUserGradeId() {
			return userGradeId;
		}

		public void setUserGradeId(String userGradeId) {
			this.userGradeId = userGradeId;
		}

		public String getTrannferProfileId() {
			return trannferProfileId;
		}

		public void setTrannferProfileId(String trannferProfileId) {
			this.trannferProfileId = trannferProfileId;
		}

		public String getTrannferRuleTypeId() {
			return trannferRuleTypeId;
		}

		public void setTrannferRuleTypeId(String trannferRuleTypeId) {
			this.trannferRuleTypeId = trannferRuleTypeId;
		}

		public String getLmsProfileId() {
			return lmsProfileId;
		}

		public void setLmsProfileId(String lmsProfileId) {
			this.lmsProfileId = lmsProfileId;
		}

		public String getRsaAuthentication() {
			return rsaAuthentication;
		}

		public void setRsaAuthentication(String rsaAuthentication) {
			this.rsaAuthentication = rsaAuthentication;
		}

		public String getAuthTypeAllowed() {
			return authTypeAllowed;
		}

		public void setAuthTypeAllowed(String authTypeAllowed) {
			this.authTypeAllowed = authTypeAllowed;
		}

		public String getMcommerceServiceAllow() {
			return mcommerceServiceAllow;
		}

		public void setMcommerceServiceAllow(String mcommerceServiceAllow) {
			this.mcommerceServiceAllow = mcommerceServiceAllow;
		}

		public String getMpayProfileIDWithGrad() {
			return mpayProfileIDWithGrad;
		}

		public void setMpayProfileIDWithGrad(String mpayProfileIDWithGrad) {
			this.mpayProfileIDWithGrad = mpayProfileIDWithGrad;
		}

		public String getSubOutletCode() {
			return subOutletCode;
		}

		public void setSubOutletCode(String subOutletCode) {
			this.subOutletCode = subOutletCode;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getChannelUserName() {
			return channelUserName;
		}

		public void setChannelUserName(String channelUserName) {
			this.channelUserName = channelUserName;
		}

		public long getLastModified() {
			return lastModified;
		}

		public void setLastModified(long lastModified) {
			this.lastModified = lastModified;
		}

		public String getPwdGenerateAllow() {
			return pwdGenerateAllow;
		}

		public void setPwdGenerateAllow(String pwdGenerateAllow) {
			this.pwdGenerateAllow = pwdGenerateAllow;
		}

		public String getWebPassword() {
			return webPassword;
		}

		public void setWebPassword(String webPassword) {
			this.webPassword = webPassword;
		}

		public Date getPasswordModifiedOn() {
			return passwordModifiedOn;
		}

		public void setPasswordModifiedOn(Date passwordModifiedOn) {
			this.passwordModifiedOn = passwordModifiedOn;
		}

		public String getPreviousStatus() {
			return previousStatus;
		}

		public void setPreviousStatus(String previousStatus) {
			this.previousStatus = previousStatus;
		}

		public String getLevel1ApprovedBy() {
			return level1ApprovedBy;
		}

		public void setLevel1ApprovedBy(String level1ApprovedBy) {
			this.level1ApprovedBy = level1ApprovedBy;
		}

		public Date getLevel1ApprovedOn() {
			return level1ApprovedOn;
		}

		public void setLevel1ApprovedOn(Date level1ApprovedOn) {
			this.level1ApprovedOn = level1ApprovedOn;
		}

		public String getLevel2ApprovedBy() {
			return level2ApprovedBy;
		}

		public void setLevel2ApprovedBy(String level2ApprovedBy) {
			this.level2ApprovedBy = level2ApprovedBy;
		}

		public Date getLevel2ApprovedOn() {
			return level2ApprovedOn;
		}

		public void setLevel2ApprovedOn(Date level2ApprovedOn) {
			this.level2ApprovedOn = level2ApprovedOn;
		}

		public List<UserPhoneVO> getOldMsisdnList() {
			return oldMsisdnList;
		}

		public void setOldMsisdnList(List<UserPhoneVO> oldMsisdnList) {
			this.oldMsisdnList = oldMsisdnList;
		}

		public String getOutletCode() {
			return outletCode;
		}

		public void setOutletCode(String outletCode) {
			this.outletCode = outletCode;
		}

		public String getControlGroup() {
			return controlGroup;
		}

		public void setControlGroup(String controlGroup) {
			this.controlGroup = controlGroup;
		}

		public String getOldWebLoginID() {
			return oldWebLoginID;
		}

		public void setOldWebLoginID(String oldWebLoginID) {
			this.oldWebLoginID = oldWebLoginID;
		}
		       	
		public String getUserCode() {
			return userCode;
		}

		public void setUserCode(String userCode) {
			this.userCode = userCode;
		}

		public boolean isUserCodeFlag() {
			return userCodeFlag;
		}

		public void setUserCodeFlag(boolean userCodeFlag) {
			this.userCodeFlag = userCodeFlag;
		}

		public String getParentID() {
			return parentID;
		}

		public void setParentID(String parentID) {
			this.parentID = parentID;
		}

		public List<ListValueVO> getServicesListSelected() {
			return servicesListSelected;
		}

		public void setServicesListSelected(List<ListValueVO> servicesListSelected) {
			this.servicesListSelected = servicesListSelected;
		}

		public List<ListValueVO> getServicesList() {
			return servicesList;
		}

		public void setServicesList(List<ListValueVO> servicesList) {
			this.servicesList = servicesList;
		}

		public Map<String, List> getRolesMapSelected() {
			return rolesMapSelected;
		}

		public void setRolesMapSelected(Map<String, List> rolesMapSelected) {
			this.rolesMapSelected = rolesMapSelected;
		}

		public Map<String, List> getRolesMap() {
			return rolesMap;
		}

		public void setRolesMap(Map<String, List> rolesMap) {
			this.rolesMap = rolesMap;
		}

		public List<CommissionProfileSetVO> getCommissionProfileList() {
			return commissionProfileList;
		}

		public void setCommissionProfileList(
				List<CommissionProfileSetVO> commissionProfileList) {
			this.commissionProfileList = commissionProfileList;
		}

		public String getGeographicalCode() {
			return geographicalCode;
		}

		public void setGeographicalCode(String geographicalCode) {
			this.geographicalCode = geographicalCode;
		}

		public List<UserGeographiesVO> getGeographicalList() {
			return geographicalList;
		}

		public void setGeographicalList(List<UserGeographiesVO> geographicalList) {
			this.geographicalList = geographicalList;
		}

		public String getGrphDomainTypeName() {
			return grphDomainTypeName;
		}

		public void setGrphDomainTypeName(String grphDomainTypeName) {
			this.grphDomainTypeName = grphDomainTypeName;
		}

		public String getUserLanguage() {
			return userLanguage;
		}

		public void setUserLanguage(String userLanguage) {
			this.userLanguage = userLanguage;
		}

		public String getUserNamePrefixCode() {
			return userNamePrefixCode;
		}

		public void setUserNamePrefixCode(String userNamePrefixCode) {
			this.userNamePrefixCode = userNamePrefixCode;
		}

		public List getUserLanguageList() {
			return userLanguageList;
		}

		public void setUserLanguageList(List userLanguageList) {
			this.userLanguageList = userLanguageList;
		}

		public List getUserNamePrefixList() {
			return userNamePrefixList;
		}

		public void setUserNamePrefixList(List userNamePrefixList) {
			this.userNamePrefixList = userNamePrefixList;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getShortName() {
			return shortName;
		}

		public void setShortName(String shortName) {
			this.shortName = shortName;
		}

		public String getEmpCode() {
			return empCode;
		}

		public void setEmpCode(String empCode) {
			this.empCode = empCode;
		}

		public String getInsuspend() {
			return insuspend;
		}

		public void setInsuspend(String insuspend) {
			this.insuspend = insuspend;
		}

		public String getOutsuspend() {
			return outsuspend;
		}

		public void setOutsuspend(String outsuspend) {
			this.outsuspend = outsuspend;
		}

		public String getContactPerson() {
			return contactPerson;
		}

		public void setContactPerson(String contactPerson) {
			this.contactPerson = contactPerson;
		}

		public String getContactNo() {
			return contactNo;
		}

		public void setContactNo(String contactNo) {
			this.contactNo = contactNo;
		}

		public String getSsn() {
			return ssn;
		}

		public void setSsn(String ssn) {
			this.ssn = ssn;
		}

		public String getDesignation() {
			return designation;
		}

		public void setDesignation(String designation) {
			this.designation = designation;
		}

		public String getAddress1() {
			return address1;
		}

		public void setAddress1(String address1) {
			this.address1 = address1;
		}

		public String getAddress2() {
			return address2;
		}

		public void setAddress2(String address2) {
			this.address2 = address2;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getCompany() {
			return company;
		}

		public void setCompany(String company) {
			this.company = company;
		}

		public String getFax() {
			return fax;
		}

		public void setFax(String fax) {
			this.fax = fax;
		}

		public String getAppointmentDate() {
			return appointmentDate;
		}

		public void setAppointmentDate(String appointmentDate) {
			this.appointmentDate = appointmentDate;
		}

		public String getOtherEmail() {
			return otherEmail;
		}

		public void setOtherEmail(String otherEmail) {
			this.otherEmail = otherEmail;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		public String getLatitude() {
			return latitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public String getWebLoginID() {
			return webLoginID;
		}

		public void setWebLoginID(String webLoginID) {
			this.webLoginID = webLoginID;
		}

		public String getShowPassword() {
			return showPassword;
		}

		public void setShowPassword(String showPassword) {
			this.showPassword = showPassword;
		}

		public String getConfirmPassword() {
			return confirmPassword;
		}

		public void setConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
		}

		public String getAllowedIPs() {
			return allowedIPs;
		}

		public void setAllowedIPs(String allowedIPs) {
			this.allowedIPs = allowedIPs;
		}

		/*public String getAllowedDays() {
			return allowedDays;
		}

		public void setAllowedDays(String allowedDays) {
			this.allowedDays = allowedDays;
		}*/

		public String getAllowedFormTime() {
			return allowedFormTime;
		}

		public String[] getAllowedDays() {
			return allowedDays;
		}

		public void setAllowedDays(String[] allowedDays) {
			this.allowedDays = allowedDays;
		}

		public void setAllowedFormTime(String allowedFormTime) {
			this.allowedFormTime = allowedFormTime;
		}

		public String getAllowedToTime() {
			return allowedToTime;
		}

		public void setAllowedToTime(String allowedToTime) {
			this.allowedToTime = allowedToTime;
		}

		public String getRoleType() {
			return roleType;
		}

		public void setRoleType(String roleType) {
			this.roleType = roleType;
		}

		public String getLowBalAlertToSelf() {
			return lowBalAlertToSelf;
		}

		public void setLowBalAlertToSelf(String lowBalAlertToSelf) {
			this.lowBalAlertToSelf = lowBalAlertToSelf;
		}

		public String getLowBalAlertToParent() {
			return lowBalAlertToParent;
		}

		public void setLowBalAlertToParent(String lowBalAlertToParent) {
			this.lowBalAlertToParent = lowBalAlertToParent;
		}

		public String getLowBalAlertToOther() {
			return lowBalAlertToOther;
		}

		public void setLowBalAlertToOther(String lowBalAlertToOther) {
			this.lowBalAlertToOther = lowBalAlertToOther;
		}

		public String getPrimarynumber() {
			return primarynumber;
		}

		public void setPrimarynumber(String primarynumber) {
			this.primarynumber = primarynumber;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getSmspin() {
			return smspin;
		}

		public void setSmspin(String smspin) {
			this.smspin = smspin;
		}

		public String getConfirmsmspin() {
			return confirmsmspin;
		}

		public void setConfirmsmspin(String confirmsmspin) {
			this.confirmsmspin = confirmsmspin;
		}

		public String getProfile() {
			return profile;
		}

		public void setProfile(String profile) {
			this.profile = profile;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public List getPhoneProfileList() {
			return phoneProfileList;
		}

		public void setPhoneProfileList(List phoneProfileList) {
			this.phoneProfileList = phoneProfileList;
		}

		public List<UserPhoneVO> getMsisdnList() {
			return msisdnList;
		}

		public void setMsisdnList(List<UserPhoneVO> msisdnList) {
			this.msisdnList = msisdnList;
		}

		public String getPrimaryRadio() {
			return primaryRadio;
		}

		public void setPrimaryRadio(String primaryRadio) {
			this.primaryRadio = primaryRadio;
		}

		public CategoryVO getCategoryVO() {
			return categoryVO;
		}

		public void setCategoryVO(CategoryVO categoryVO) {
			this.categoryVO = categoryVO;
		}

		public String getCategoryCode() {
			return categoryCode;
		}

		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}

		
		
		
		public void setChannelUserTypeList(List channelUserTypeList) {			
				this.channelUserTypeList=channelUserTypeList;
			}
		public List getChannelUserTypeList() {
			return channelUserTypeList;
		}

		public void setSelectDomainList(ArrayList displayDomainList) {
			this.selectDomainList=displayDomainList;
			
		}
		public List getSelectDomainList() {
			return  selectDomainList;
			
		}

		public void setDomainShowFlag(boolean b) {
			this.domainShowFlag=b;
			
		}
		public boolean getDomainShowFlag(boolean b) {
			return domainShowFlag;
			
		}

		public void setOrigCategoryList(ArrayList loadOtherCategorList) {
			this.origCategoryList=loadOtherCategorList;		
		}
		public List getOrigCategoryList() {
			return origCategoryList;		
		}

		public void setOrigParentCategoryList(ArrayList loadC2SRulesListForChannelUserAssociation) {
			this.origParentCategoryList=loadC2SRulesListForChannelUserAssociation;
			
		}
		public List  getOrigParentCategoryList() {
			return origParentCategoryList;
			
		}

		public void setAssociatedGeographicalList(ArrayList list) {
			this.associatedGeographicalList=list;
			
		}
		public List getAssociatedGeographicalList() {
			return associatedGeographicalList;
			
		}

		public void setParentDomainTypeDesc(String graphDomainTypeName) {
			this.parentDomainTypeDesc=graphDomainTypeName;
			
		}
		public String getParentDomainTypeDesc() {
			return parentDomainTypeDesc;
			
		}

		public void setParentDomainCode(String graphDomainCode) {
			this.parentDomainCode=graphDomainCode;
			
		}
		public String getParentDomainCode() {
			return parentDomainCode;
			
		}

		public void setParentDomainDesc(String graphDomainName) {
			this.parentDomainDesc=graphDomainName;
			
		}
		public String  getParentDomainDesc() {
			return parentDomainDesc;
			
		}

		public void setSelectDomainListSize(int size) {
			this.selectDomainListSize=size;
			
		}
		public int getSelectDomainListSize() {
			return selectDomainListSize;
			
		}

		public void setAssociatedGeographicalListSize(int size) {
			this.associatedGeographicalListSize=size;
			
		}
		public int getAssociatedGeographicalListSize() {
			return associatedGeographicalListSize;
			
		}

		public void setOrigCategoryListSize(int size) {
			this.origCategoryListSize=size;
			
		}
		public int getOrigCategoryListSize() {
			return origCategoryListSize;
			
		}

		public void setCatList(ArrayList<CategoryVO> catList) {
			this.catList=catList;	
		}
		public List getCatList() {
			return catList;		
		}


		public String getDocumentType() {
			return documentType;
		}
		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}
		public String getDocumentNo() {
			return documentNo;
		}
		public void setDocumentNo(String documentNo) {
			this.documentNo = documentNo;
		}
		public String getPaymentType() {
			return paymentType;
		}
		public void setPaymentType(String paymentType) {
			this.paymentType = paymentType;
		}



}

