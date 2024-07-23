package com.web.pretups.channel.reports.web;

/*
 * @# UsersReportForm.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Sep 18, 2005 Ved.sharma Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
/*
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
*/

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.user.web.UserHierarchyForm;

public class UsersReportForm  extends UserHierarchyForm {
    private final Log _log = LogFactory.getLog(UsersReportForm.class.getName());
    private String _zoneName = null;
    private String _divisionName;
    private String _departmentName;
    private String _networkName = null;
    private String _networkCode = null;
    private String _reportHeaderName = null;
    private String _fromDate = null;
    private String _toDate = null;
    private String _rptfromDate = null;
    private String _rpttoDate = null;
    private String _sortType = null;
    private String _userStatus;
    private String _userStatusName;
    private String _divisionCode;
    private String _departmentCode;
    private String _domainName = null;
    private String _categoryName = null;
    private String _serviceType;
    private String _serviceTypeName;
    private String _userType;
    private String _reportType;
    private String _filterType;
    private String _filterTypeName;
    private String _reportTypeName;
    private String _currentDate = null;
    private String _rptcurrentDate = null;
    private String _channelCategoryUser = null;
    private String _transferStatus = null;
    private String _transferCategory = null;
    private String _transferStatusName;
    private String _transferCategoryName;
    private String _totransferCategoryCode;
    private String _fromtransferCategoryCode;
    private String _totransferCategoryName;
    private String _fromtransferCategoryName;
    private String _loginUserID = null;
    private String _touserName;
    private String _touserID;
    private String _requestType;
    private String _fromMonth;
    private String _toMonth;
    private String _tempfromDate;
    private String _dailyDate;
    private String _mobileNo;
    private String _dateType;
    private String _loggedInUserCategoryCode;
    private String _loggedInUserCategoryName;
    private String _loggedInUserName;
    private String _loggedInUserDomainID;
    private String _loggedInUserDomainName;
    private String _filePath;
    private String _subDir;
    private String _scheduleDate;
    private String _hours;
    private String _minute;
    private String _endDate;
    private String _module;
    private String _moduleName;
    private String _blackListStatus;
    private String _blackListStatusName;
    private String _batchID;
    private String _domainListString;

    private int _userListSize;
    private int _zoneListSize;
    private int _toUserListSize;

    private ArrayList _userStatusList = null;
    private ArrayList _divisionList = null;
    private ArrayList _departmentList = null;
    private ArrayList _serviceTypeList = null;
    private ArrayList _reportTypeList = null;
    private ArrayList _transferStatusList = null;
    private ArrayList _transferCategoryList = null;
    private ArrayList _toUserList = null;
    private ArrayList _reportList = null;
    private ArrayList _fromCategoryList = null;
    private ArrayList _toCategoryList = null;
    private ArrayList _transferRulCatList = null;
    private int _reportListSize;
    private ArrayList _moduleList = null;
    private ArrayList _batchIdList = null;
    private ArrayList _tempBatchIdList = null;

    // Added by Amit Singh.
    private String _serviceName;
    private int _serviceTypeListSize;
    private String _radioNetCode = null;
    private String _txnSubType;
    private String _txnSubTypeName;
    private ArrayList _txnSubTypeList = null;
    private int _txnSubTypeListSize;
    private String _noOfTxn;
    private String _transferInOrOut;
    private String _channelType;
    private String _transferInOrOutName;
    private String _batchIDText;
    private String _categorySeqNo;

    // added By Sourabh
    private ArrayList _roamNetworkList = null;
    private String _roamerType;
    private String _roamerTypeName;
    private String _roamnetworkName;
    private String _roamnetworkCode;
    private int _roamNetworkListSize;
    // add for operation summary report
    private String _agentCatCode;

    private String _temptoDate;
    private long _time;
    private String _loginId = null;

    // //5.1.3 start
    private String _msisdn;
    private String _fromMsisdn;
    private String _toMsisdn;

    // Added for separate database for reports on 26/02/2008
    private boolean _isReportingDB = false;
    private String _currentDateRptChkBox;

    // Date 24-Apr-2008
    // Field to take input from user for viewing number of non-transacting user
    // report
    private String _noOfNonTxnUsers = null;
    private String _radioCodeForTxn = "N";// whether greater than or less than
    // schedule top up batch type
    private String _fileType;

    // Date 24 June '08
    // added by: Zafar Abbas
    private String _productType;
    private String _userCategory;
    private String _geoDomainType;
    private String _orderType;
    private String _transferNumber;
    private ArrayList _productTypeList = null;

    private boolean _isCorporate = false;
    private boolean _isSoho = false;
    private boolean _isNormal = false;
    private Object _otherInfo = null;
    private String _reportInitials;
    private String _schudleDate;
    private String _rptSchDate = null;
    private String _scheduleType;
    private String _fromTime = null;
    private String _toTime = null;
    private Date _fromDateTime = null;
    private Date _toDateTime = null;

    // for Direct payout report
    private ArrayList _bonusTypeList;
    private String _bonusCode;
    private String _bonusName;
    private int _userStatusListSize;

    // for staff user changes
    private String _staffReport = "N";
    // For zero balance counter
    private ArrayList _thresholdTypeList = null;
    private String _thresholdType = null;
    // Added by Rajdeep for Aktel(User Closing Balance Report)
    private String _fromAmount;
    private String _toAmount;

    private double _rptFromAmount;
    private double _rptToAmount;

    private boolean _fromToTimeBlank = false;
    // Added By Babu Kunwar for UserEventRemarks
    private String _userMsisdn;
    private String _userEvent;
    private String _userEventDesc;
    private ArrayList _subEventTypeList;

    // Added By Babu Kunwar For C2S_Bonus_Enquiry
    private String _bundlesId;
    private String _bundlesName;
    private ArrayList _bundlesNameList = null;
    private ArrayList _bundleTypeList = null;
    private String _bundleType;
    private String _bundleTypeName;

    // for sub service in non txn retailers report
    private String _subService;
    private ArrayList _subServiceList;
    private String _subServiceName;

    // Added By Babu Kunwar For External User List Reports
    private String _externalCode;
    
    
    private String _cardGroupSubServiceID;
    private String _serviceTypeId;
    private String _cardGroupSubServiceName;
    private ArrayList _cardGroupSubServiceList;
    private ArrayList _cardGroupList;
    private String _serviceTypedesc;
	private String _cardGroupCode;
	private String _kindOfTransaction;
	
	
	private String _paymentInstCode;
	private ArrayList _paymentInstrumentList;
	private String _paymentInstDesc;
	
	
	
	private String _paymentGatewayType;
	private String _paymentGatewayTypeDesc;
	private ArrayList _paymentGatewayTypeList;

	
	 public String getPaymentInstCode() {
	        return _paymentInstCode;
	    }

	 public void setPaymentInstCode(String paymentInstCode) {
	        _paymentInstCode = paymentInstCode;
	 }
	 
	 
	 public ArrayList getPaymentInstrumentList() {
	        return _paymentInstrumentList;
	    }

	    public void setPaymentInstrumentList(ArrayList paymentInstrumentList) {
	        _paymentInstrumentList = paymentInstrumentList;
	    }
	 
	    public String getPaymentGatewayType() {
			return _paymentGatewayType;
		}

		public void setPaymentGatewayType(String paymentGatewayType) {
			_paymentGatewayType = paymentGatewayType;
		}

		public ArrayList getPaymentGatewayTypeList() {
			return _paymentGatewayTypeList;
		}

		public void setPaymentGatewayTypeList(ArrayList paymentGatewayTypeList) {
			_paymentGatewayTypeList = paymentGatewayTypeList;
		}

		public String getPaymentGatewayTypeDesc() {
			return _paymentGatewayTypeDesc;
		}

		public void setPaymentGatewayTypeDesc(String paymentGatewayTypeDesc) {
			_paymentGatewayTypeDesc = paymentGatewayTypeDesc;
		}  


    public String getExternalCode() {
        return _externalCode;
    }

    public void setExternalCode(String externalCode) {
        _externalCode = externalCode;
    }

    public String getBundleTypeName() {
        return _bundleTypeName;
    }

    public void setBundleTypeName(String bundleTypeName) {
        _bundleTypeName = bundleTypeName;
    }

    public String getBundlesId() {
        return _bundlesId;
    }

    public void setBundlesId(String bundlesId) {
        _bundlesId = bundlesId;
    }

    /**
     * @return the bundlesName
     */
    public String getBundlesName() {
        return _bundlesName;
    }

    /**
     * @param bundlesName
     *            the bundlesName to set
     */
    public void setBundlesName(String bundlesName) {
        _bundlesName = bundlesName;
    }

    /**
     * @return the bundlesNameList
     */
    public ArrayList getBundlesNameList() {
        return _bundlesNameList;
    }

    /**
     * @param bundlesNameList
     *            the bundlesNameList to set
     */
    public void setBundlesNameList(ArrayList bundlesNameList) {
        _bundlesNameList = bundlesNameList;
    }

    /**
     * @return the bundleType
     */
    public String getBundleType() {
        return _bundleType;
    }

    /**
     * @param bundleType
     *            the bundleType to set
     */
    public void setBundleType(String bundleType) {
        _bundleType = bundleType;
    }

    /**
     * @return the bundleTypeList
     */
    public ArrayList getBundleTypeList() {
        return _bundleTypeList;
    }

    /**
     * @param bundleTypeList
     *            the bundleTypeList to set
     */
    public void setBundleTypeList(ArrayList bundleTypeList) {
        _bundleTypeList = bundleTypeList;
    }

    /**
     * @return the subEventTypeList
     */
    public ArrayList getSubEventTypeList() {
        return _subEventTypeList;
    }

    /**
     * @param subEventTypeList
     *            the subEventTypeList to set
     */
    public void setSubEventTypeList(ArrayList subEventTypeList) {
        _subEventTypeList = subEventTypeList;
    }

    /**
     * @return the userEventDesc
     */
    public String getUserEventDesc() {
        return _userEventDesc;
    }

    /**
     * @param userEventDesc
     *            the userEventDesc to set
     */
    public void setUserEventDesc(String userEventDesc) {
        _userEventDesc = userEventDesc;
    }

    /**
     * @return the userEvent
     */
    public String getUserEvent() {
        return _userEvent;
    }

    /**
     * @param userEvent
     *            the userEvent to set
     */
    public void setUserEvent(String userEvent) {
        _userEvent = userEvent.trim();
    }

    /**
     * @return the userMsisdn
     */
    public String getUserMsisdn() {
        return _userMsisdn;
    }

    /**
     * @param userMsisdn
     *            the userMsisdn to set
     */
    public void setUserMsisdn(String userMsisdn) {
        _userMsisdn = userMsisdn;
    }

    /**
     * @return the fromToTimeBlank
     */
    public boolean isFromToTimeBlank() {
        return _fromToTimeBlank;
    }

    /**
     * @param fromToTimeBlank
     *            the fromToTimeBlank to set
     */
    public void setFromToTimeBlank(boolean fromToTimeBlank) {
        _fromToTimeBlank = fromToTimeBlank;
    }

    /**
     * @return Returns the isCorporate.
     */
    public boolean isCorporate() {
        return _isCorporate;
    }

    /**
     * @param isCorporate
     *            The isCorporate to set.
     */
    public void setCorporate(boolean isCorporate) {
        _isCorporate = isCorporate;
    }

    /**
     * @return Returns the isNormal.
     */
    public boolean isNormal() {
        return _isNormal;
    }

    /**
     * @param isNormal
     *            The isNormal to set.
     */
    public void setNormal(boolean isNormal) {
        _isNormal = isNormal;
    }

    /**
     * @return Returns the isSoho.
     */
    public boolean isSoho() {
        return _isSoho;
    }

    /**
     * @param isSoho
     *            The isSoho to set.
     */
    public void setSoho(boolean isSoho) {
        _isSoho = isSoho;
    }

    /**
     * @return Returns the _msisdn.
     */
    //@Override
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param _msisdn
     *            The _msisdn to set.
     */
    //@Override
    public void setMsisdn(String p_msisdn) {
        _msisdn = p_msisdn;
    }

    /**
     * @return Returns the _fromMsisdn.
     */
    public String getFromMsisdn() {
        return _fromMsisdn;
    }

    /**
     * @param msisdn
     *            The _fromMsisdn to set.
     */
    public void setFromMsisdn(String msisdn) {
        _fromMsisdn = msisdn;
    }

    /**
     * @return Returns the _toMsisdn.
     */
    public String getToMsisdn() {
        return _toMsisdn;
    }

    /**
     * @param msisdn
     *            The _toMsisdn to set.
     */
    public void setToMsisdn(String msisdn) {
        _toMsisdn = msisdn;
    }

    // //5.1.3 end

    //@Override
    public long getTime() {
        return _time;
    }

    //@Override
    public void setTime(long p_time) {
        _time = p_time;
    }

    /**
     * @return Returns the roamerTypeName.
     */
    public String getRoamerTypeName() {
        return this._roamerTypeName;
    }

    /**
     * @param roamerTypeName
     *            The roamerTypeName to set.
     */
    public void setRoamerTypeName(String roamerTypeName) {
        this._roamerTypeName = roamerTypeName;
    }

    /**
     * @return Returns the categorySeqNo.
     */
    public String getCategorySeqNo() {
        return _categorySeqNo;
    }

    /**
     * @param categorySeqNo
     *            The categorySeqNo to set.
     */
    public void setCategorySeqNo(String categorySeqNo) {
        _categorySeqNo = categorySeqNo;
    }

    /**
     * @return Returns the domainListString.
     */
    public String getDomainListString() {
        return _domainListString;
    }

    /**
     * @param domainListString
     *            The domainListString to set.
     */
    public void setDomainListString(String domainListString) {
        _domainListString = domainListString;
    }

    /**
     * @return Returns the tempBatchIdList.
     */
    public ArrayList getTempBatchIdList() {
        return _tempBatchIdList;
    }

    /**
     * @param tempBatchIdList
     *            The tempBatchIdList to set.
     */
    public void setTempBatchIdList(ArrayList tempBatchIdList) {
        _tempBatchIdList = tempBatchIdList;
    }

    /**
     * @return Returns the batchID.
     */
    public String getBatchID() {
        return _batchID;
    }

    /**
     * @param batchID
     *            The batchID to set.
     */
    public void setBatchID(String batchID) {
        _batchID = batchID;
    }

    /**
     * @return Returns the batchIdList.
     */
    public ArrayList getBatchIdList() {
        return _batchIdList;
    }

    /**
     * @param batchIdList
     *            The batchIdList to set.
     */
    public void setBatchIdList(ArrayList batchIdList) {
        _batchIdList = batchIdList;
    }

    public int getSizeOfBatchIdList() {
        if (_batchIdList != null) {
            return _batchIdList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the blackListStatusName.
     */
    public String getBlackListStatusName() {
        return _blackListStatusName;
    }

    /**
     * @param blackListStatusName
     *            The blackListStatusName to set.
     */
    public void setBlackListStatusName(String blackListStatusName) {
        _blackListStatusName = blackListStatusName;
    }

    /**
     * @return Returns the blackListStatus.
     */
    public String getBlackListStatus() {
        return _blackListStatus;
    }

    /**
     * @param blackListStatus
     *            The blackListStatus to set.
     */
    public void setBlackListStatus(String blackListStatus) {
        _blackListStatus = blackListStatus;
    }

    public UsersReportForm() {
    }

    public void semiFlushs() {
        _touserID = null;
        _touserName = null;
        _toUserList = null;
        _loginId = null;
    }

    public void semiFlush() {
        setOwnerName(null);
        setUserID(null);
        setOwnerList(null);
        _serviceName = null;
        _serviceTypeListSize = 0;
        _radioNetCode = null;
        _userStatusList = null;
        _sortType = null;
        _userStatus = null;
        _staffReport = "N";
        _subServiceList = null;
    }

    

    /**
     * @return Returns the loggedInUserCategoryName.
     */
    public String getLoggedInUserCategoryName() {
        return _loggedInUserCategoryName;
    }

    /**
     * @param loggedInUserCategoryName
     *            The loggedInUserCategoryName to set.
     */
    public void setLoggedInUserCategoryName(String loggedInUserCategoryName) {
        _loggedInUserCategoryName = loggedInUserCategoryName;
    }

    public int getSizeOfModuleList() {
        if (_moduleList != null) {
            return _moduleList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the moduleList.
     */
    public ArrayList getModuleList() {
        return _moduleList;
    }

    /**
     * @param moduleList
     *            The moduleList to set.
     */
    public void setModuleList(ArrayList moduleList) {
        _moduleList = moduleList;
    }

    /**
     * @return Returns the module.
     */
    public String getModule() {
        return _module;
    }

    /**
     * @param module
     *            The module to set.
     */
    public void setModule(String module) {
        _module = module;
    }

    /**
     * @return Returns the endDate.
     */
    public String getEndDate() {
        return _endDate;
    }

    /**
     * @param endDate
     *            The endDate to set.
     */
    public void setEndDate(String endDate) {
        _endDate = endDate;
    }

    /**
     * @return Returns the hours.
     */
    public String getHours() {
        return _hours;
    }

    /**
     * @param hours
     *            The hours to set.
     */
    public void setHours(String hours) {
        _hours = hours;
    }

    /**
     * @return Returns the minute.
     */
    public String getMinute() {
        return _minute;
    }

    /**
     * @param minute
     *            The minute to set.
     */
    public void setMinute(String minute) {
        _minute = minute;
    }

    /**
     * @return Returns the subDir.
     */
    public String getSubDir() {
        return _subDir;
    }

    /**
     * @param subDir
     *            The subDir to set.
     */
    public void setSubDir(String subDir) {
        _subDir = subDir;
    }

    /**
     * @return Returns the toMonth.
     */
    public String getToMonth() {
        return _toMonth;
    }

    /**
     * @param toMonth
     *            The toMonth to set.
     */
    public void setToMonth(String toMonth) {
        _toMonth = toMonth;
    }

    /**
     * @return Returns the filterTypeName.
     */
    public String getFilterTypeName() {
        return _filterTypeName;
    }

    /**
     * @param filterTypeName
     *            The filterTypeName to set.
     */
    public void setFilterTypeName(String filterTypeName) {
        _filterTypeName = filterTypeName;
    }

    /**
     * @return Returns the transferInOrOutName.
     */
    public String getTransferInOrOutName() {
        return _transferInOrOutName;
    }

    /**
     * @param transferInOrOutName
     *            The transferInOrOutName to set.
     */
    public void setTransferInOrOutName(String transferInOrOutName) {
        _transferInOrOutName = transferInOrOutName;
    }

    /**
     * @return Returns the channelType.
     */
    public String getChannelType() {
        return _channelType;
    }

    /**
     * @param channelType
     *            The channelType to set.
     */
    public void setChannelType(String channelType) {
        _channelType = channelType;
    }

    /**
     * @return Returns the transferInOrOut.
     */
    public String getTransferInOrOut() {
        return _transferInOrOut;
    }

    /**
     * @param transferInOrOut
     *            The transferInOrOut to set.
     */
    public void setTransferInOrOut(String transferInOrOut) {
        _transferInOrOut = transferInOrOut;
    }

    /**
     * @return Returns the transferRulCatList.
     */
    public ArrayList getTransferRulCatList() {
        return _transferRulCatList;
    }

    /**
     * @param transferRulCatList
     *            The transferRulCatList to set.
     */
    public void setTransferRulCatList(ArrayList transferRulCatList) {
        _transferRulCatList = transferRulCatList;
    }

    /**
     * @return Returns the fromCategoryList.
     */
    public ArrayList getFromCategoryList() {
        return _fromCategoryList;
    }

    /**
     * @param fromCategoryList
     *            The fromCategoryList to set.
     */
    public void setFromCategoryList(ArrayList fromCategoryList) {
        _fromCategoryList = fromCategoryList;
    }

    /**
     * @return Returns the toCategoryList.
     */
    public ArrayList getToCategoryList() {
        return _toCategoryList;
    }

    /**
     * @param toCategoryList
     *            The toCategoryList to set.
     */
    public void setToCategoryList(ArrayList toCategoryList) {
        _toCategoryList = toCategoryList;
    }

    /**
     * @return Returns the scheduleDate.
     */
    public String getScheduleDate() {
        return _scheduleDate;
    }

    /**
     * @param scheduleDate
     *            The scheduleDate to set.
     */
    public void setScheduleDate(String scheduleDate) {
        _scheduleDate = scheduleDate;
    }

    /**
     * @return Returns the filePath.
     */
    public String getFilePath() {
        return _filePath;
    }

    /**
     * @param filePath
     *            The filePath to set.
     */
    public void setFilePath(String filePath) {
        _filePath = filePath;
    }

    /**
     * @return Returns the reportListSize.
     */
    public int getReportListSize() {
        return _reportListSize;
    }

    /**
     * @param reportListSize
     *            The reportListSize to set.
     */
    public void setReportListSize(int reportListSize) {
        _reportListSize = reportListSize;
    }

    /**
     * @return Returns the reportList.
     */
    public ArrayList getReportList() {
        return _reportList;
    }

    /**
     * @param reportList
     *            The reportList to set.
     */
    public void setReportList(ArrayList reportList) {
        _reportList = reportList;
    }

    /**
     * @return Returns the loggedInUserDomainID.
     */
    public String getLoggedInUserDomainID() {
        return _loggedInUserDomainID;
    }

    /**
     * @param loggedInUserDomainID
     *            The loggedInUserDomainID to set.
     */
    public void setLoggedInUserDomainID(String loggedInUserDomainID) {
        _loggedInUserDomainID = loggedInUserDomainID;
    }

    /**
     * @return Returns the loggedInUserDomainName.
     */
    public String getLoggedInUserDomainName() {
        return _loggedInUserDomainName;
    }

    /**
     * @param loggedInUserDomainName
     *            The loggedInUserDomainName to set.
     */
    public void setLoggedInUserDomainName(String loggedInUserDomainName) {
        _loggedInUserDomainName = loggedInUserDomainName;
    }

    /**
     * @return Returns the loggedInUserName.
     */
    public String getLoggedInUserName() {
        return _loggedInUserName;
    }

    /**
     * @param loggedInUserName
     *            The loggedInUserName to set.
     */
    public void setLoggedInUserName(String loggedInUserName) {
        _loggedInUserName = loggedInUserName;
    }

    /**
     * @return Returns the loggedInUserCategoryCode.
     */
    public String getLoggedInUserCategoryCode() {
        return _loggedInUserCategoryCode;
    }

    /**
     * @param loggedInUserCategoryCode
     *            The loggedInUserCategoryCode to set.
     */
    public void setLoggedInUserCategoryCode(String loggedInUserCategoryCode) {
        _loggedInUserCategoryCode = loggedInUserCategoryCode;
    }

    /**
     * @return Returns the dateType.
     */
    public String getDateType() {
        return _dateType;
    }

    /**
     * @param dateType
     *            The dateType to set.
     */
    public void setDateType(String dateType) {
        _dateType = dateType;
    }

    /**
     * @return Returns the noOfTxn.
     */
    public String getNoOfTxn() {
        return _noOfTxn;
    }

    /**
     * @param noOfTxn
     *            The noOfTxn to set.
     */
    public void setNoOfTxn(String noOfTxn) {
        _noOfTxn = noOfTxn;
    }

    /**
     * @return Returns the mobileNo.
     */
    public String getMobileNo() {
        return _mobileNo;
    }

    /**
     * @param mobileNo
     *            The mobileNo to set.
     */
    public void setMobileNo(String mobileNo) {
        _mobileNo = mobileNo;
    }

    /**
     * @return Returns the dailyDate.
     */
    public String getDailyDate() {
        return _dailyDate;
    }

    /**
     * @param dailyDate
     *            The dailyDate to set.
     */
    public void setDailyDate(String dailyDate) {
        _dailyDate = dailyDate;
    }

    /**
     * @return Returns the fromMonth.
     */
    public String getFromMonth() {
        return _fromMonth;
    }

    /**
     * @param fromMonth
     *            The fromMonth to set.
     */
    public void setFromMonth(String fromMonth) {
        _fromMonth = fromMonth;
    }

    /**
     * @return Returns the tempfromDate.
     */
    public String getTempfromDate() {
        return _tempfromDate;
    }

    /**
     * @param tempfromDate
     *            The tempfromDate to set.
     */
    public void setTempfromDate(String tempfromDate) {
        _tempfromDate = tempfromDate;
    }

    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }

    /**
     * @return Returns the txnSubTypeName.
     */
    public String getTxnSubTypeName() {
        return _txnSubTypeName;
    }

    /**
     * @param txnSubTypeName
     *            The txnSubTypeName to set.
     */
    public void setTxnSubTypeName(String txnSubTypeName) {
        _txnSubTypeName = txnSubTypeName;
    }

    /**
     * @return Returns the touserID.
     */
    public String getTouserID() {
        return _touserID;
    }

    /**
     * @param touserID
     *            The touserID to set.
     */
    public void setTouserID(String touserID) {
        _touserID = touserID;
    }

    /**
     * @return Returns the toUserList.
     */
    public ArrayList getToUserList() {
        return _toUserList;
    }

    /**
     * @param toUserList
     *            The toUserList to set.
     */
    public void setToUserList(ArrayList toUserList) {
        _toUserList = toUserList;
    }

    /**
     * @return Returns the touserName.
     */
    public String getTouserName() {
        return _touserName;
    }

    /**
     * @param touserName
     *            The touserName to set.
     */
    public void setTouserName(String touserName) {
        _touserName = touserName;
    }

    /**
     * @return Returns the fromtransferCategoryCode.
     */
    public String getFromtransferCategoryCode() {
        return _fromtransferCategoryCode;
    }

    /**
     * @param fromtransferCategoryCode
     *            The fromtransferCategoryCode to set.
     */
    public void setFromtransferCategoryCode(String fromtransferCategoryCode) {
        _fromtransferCategoryCode = fromtransferCategoryCode;
    }

    /**
     * @return Returns the fromtransferCategoryName.
     */
    public String getFromtransferCategoryName() {
        return _fromtransferCategoryName;
    }

    /**
     * @param fromtransferCategoryName
     *            The fromtransferCategoryName to set.
     */
    public void setFromtransferCategoryName(String fromtransferCategoryName) {
        _fromtransferCategoryName = fromtransferCategoryName;
    }

    /**
     * @return Returns the totransferCategoryCode.
     */
    public String getTotransferCategoryCode() {
        return _totransferCategoryCode;
    }

    /**
     * @param totransferCategoryCode
     *            The totransferCategoryCode to set.
     */
    public void setTotransferCategoryCode(String totransferCategoryCode) {
        _totransferCategoryCode = totransferCategoryCode;
    }

    /**
     * @return Returns the totransferCategoryName.
     */
    public String getTotransferCategoryName() {
        return _totransferCategoryName;
    }

    /**
     * @param totransferCategoryName
     *            The totransferCategoryName to set.
     */
    public void setTotransferCategoryName(String totransferCategoryName) {
        _totransferCategoryName = totransferCategoryName;
    }

    /**
     * @return Returns the userStatusName.
     */
    public String getUserStatusName() {
        return _userStatusName;
    }

    /**
     * @param userStatusName
     *            The userStatusName to set.
     */
    public void setUserStatusName(String userStatusName) {
        _userStatusName = userStatusName;
    }

    /**
     * @return Returns the transferCategoryName.
     */
    public String getTransferCategoryName() {
        return _transferCategoryName;
    }

    /**
     * @param transferCategoryName
     *            The transferCategoryName to set.
     */
    public void setTransferCategoryName(String transferCategoryName) {
        _transferCategoryName = transferCategoryName;
    }

    /**
     * @return Returns the transferStatusName.
     */
    public String getTransferStatusName() {
        return _transferStatusName;
    }

    /**
     * @param transferStatusName
     *            The transferStatusName to set.
     */
    public void setTransferStatusName(String transferStatusName) {
        _transferStatusName = transferStatusName;
    }

    /**
     * @return Returns the serviceTypeName.
     */
    public String getServiceTypeName() {
        return _serviceTypeName;
    }

    /**
     * @param serviceTypeName
     *            The serviceTypeName to set.
     */
    public void setServiceTypeName(String serviceTypeName) {
        _serviceTypeName = serviceTypeName;
    }

    /**
     * @return Returns the reportTypeName.
     */
    public String getReportTypeName() {
        return _reportTypeName;
    }

    /**
     * @param reportTypeName
     *            The reportTypeName to set.
     */
    public void setReportTypeName(String reportTypeName) {
        _reportTypeName = reportTypeName;
    }

    /**
     * @return Returns the loginUserID.
     */
    public String getLoginUserID() {
        return _loginUserID;
    }

    /**
     * @param loginUserID
     *            The loginUserID to set.
     */
    public void setLoginUserID(String loginUserID) {
        if (loginUserID != null) {
            _loginUserID = loginUserID.trim();
        }
    }

    /**
     * @return Returns the transferCategory.
     */
    public String getTransferCategory() {
        return _transferCategory;
    }

    /**
     * @param transferCategory
     *            The transferCategory to set.
     */
    public void setTransferCategory(String transferCategory) {
        if (transferCategory != null) {
            _transferCategory = transferCategory.trim();
        }
    }

    /**
     * @return Returns the transferCategoryList.
     */
    public ArrayList getTransferCategoryList() {
        return _transferCategoryList;
    }

    /**
     * @param transferCategoryList
     *            The transferCategoryList to set.
     */
    public void setTransferCategoryList(ArrayList transferCategoryList) {
        _transferCategoryList = transferCategoryList;
    }

    /**
     * @return Returns the serviceTypeListSize.
     */
    public int getServiceTypeListSize() {
        if (getServiceTypeList() != null) {
            return (getServiceTypeList().size());
        } else {
            return 0;
        }
    }

    /**
     * @param serviceTypeListSize
     *            The serviceTypeListSize to set.
     */
    public void setServiceTypeListSize(int serviceTypeListSize) {
        _serviceTypeListSize = serviceTypeListSize;
    }

    /**
     * @return Returns the toUserListSize.
     */
    public int getToUserListSize() {
        if (getToUserList() != null) {
            return (getToUserList().size());
        } else {
            return 0;
        }
    }

    /**
     * @param toUserListSize
     *            The toUserListSize to set.
     */
    public void setToUserListSize(int toUserListSize) {
        _toUserListSize = toUserListSize;
    }

    /**
     * @return Returns the zoneListSize.
     */
    //@Override
    public int getZoneListSize() {
        if (getZoneList() != null) {
            return (getZoneList().size());
        } else {
            return 0;
        }
    }

    /**
     * @param zoneListSize
     *            The zoneListSize to set.
     */
    public void setZoneListSize(int zoneListSize) {
        _zoneListSize = zoneListSize;
    }

    /**
     * @return Returns the currentDate.
     */
    public String getCurrentDate() {
        return _currentDate;
    }

    /**
     * @param currentDate
     *            The currentDate to set.
     */
    public void setCurrentDate(String currentDate) {
        if (currentDate != null) {
            _currentDate = currentDate.trim();
        }
    }

    /**
     * @return Returns the rptcurrentDate.
     */
    public String getRptcurrentDate() {
        return _rptcurrentDate;
    }

    /**
     * @param rptcurrentDate
     *            The rptcurrentDate to set.
     */
    public void setRptcurrentDate(String rptcurrentDate) {
        if (rptcurrentDate != null) {
            _rptcurrentDate = rptcurrentDate.trim();
        }
    }

    /**
     * @return Returns the userListSize.
     */
    //@Override
    public int getUserListSize() {
        if (getUserList() != null && !getUserList().isEmpty()) {
            return getUserList().size();
        } else {
            return 0;
        }
    }

    /**
     * @param userListSize
     *            The userListSize to set.
     */
    public void setUserListSize(ArrayList userList) {
        if (userList != null) {
            _userListSize = userList.size();
        } else {
            _userListSize = 0;
        }
    }

    /**
     * @return Returns the rptfromDate.
     */
    public String getRptfromDate() {
        return _rptfromDate;
    }

    /**
     * @param rptfromDate
     *            The rptfromDate to set.
     */
    public void setRptfromDate(String rptfromDate) {
        if (rptfromDate != null) {
            _rptfromDate = rptfromDate.trim();
        }
    }

    /**
     * @return Returns the rpttoDate.
     */
    public String getRpttoDate() {
        return _rpttoDate;
    }

    /**
     * @param rpttoDate
     *            The rpttoDate to set.
     */
    public void setRpttoDate(String rpttoDate) {
        if (rpttoDate != null) {
            _rpttoDate = rpttoDate.trim();
        }
    }

    /**
     * @return Returns the transferStatus.
     */
    public String getTransferStatus() {
        return _transferStatus;
    }

    /**
     * @param transferStatus
     *            The transferStatus to set.
     */
    public void setTransferStatus(String transferStatus) {
        if (transferStatus != null) {
            _transferStatus = transferStatus.trim();
        }
    }

    /**
     * @return Returns the transferStatusList.
     */
    public ArrayList getTransferStatusList() {
        return _transferStatusList;
    }

    /**
     * @param transferStatusList
     *            The transferStatusList to set.
     */
    public void setTransferStatusList(ArrayList transferStatusList) {
        _transferStatusList = transferStatusList;
    }

    /**
     * @return Returns the categoryName.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * @param categoryName
     *            The categoryName to set.
     */
    public void setCategoryName(String categoryName) {
        if (categoryName != null) {
            _categoryName = categoryName.trim();
        }
    }

    /**
     * @return Returns the channelCategoryUser.
     */
    public String getChannelCategoryUser() {
        return _channelCategoryUser;
    }

    /**
     * @param channelCategoryUser
     *            The channelCategoryUser to set.
     */
    public void setChannelCategoryUser(String channelCategoryUser) {
        if (channelCategoryUser != null) {
            _channelCategoryUser = channelCategoryUser.trim();
        }
    }

    /**
     * @return Returns the domainName.
     */
    public String getDomainName() {
        return _domainName;
    }

    /**
     * @param domainName
     *            The domainName to set.
     */
    public void setDomainName(String domainName) {
        if (domainName != null) {
            _domainName = domainName.trim();
        }
    }

    /**
     * @return Returns the fromDate.
     */
    //@Override
    public String getFromDate() {
        return _fromDate;
    }

    /**
     * @param fromDate
     *            The fromDate to set.
     */
    //@Override
    public void setFromDate(String fromDate) {
        if (fromDate != null) {
            _fromDate = fromDate.trim();
        }
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        if (networkCode != null) {
            _networkCode = networkCode.trim();
        }
    }

    /**
     * @return Returns the networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * @param networkName
     *            The networkName to set.
     */
    public void setNetworkName(String networkName) {
        if (networkName != null) {
            _networkName = networkName.trim();
        }
    }

    /**
     * @return Returns the reportHeaderName.
     */
    public String getReportHeaderName() {
        return _reportHeaderName;
    }

    /**
     * @param reportHeaderName
     *            The reportHeaderName to set.
     */
    public void setReportHeaderName(String reportHeaderName) {
        if (reportHeaderName != null) {
            _reportHeaderName = reportHeaderName.trim();
        }
    }

    /**
     * @return Returns the sortType.
     */
    public String getSortType() {
        return _sortType;
    }

    /**
     * @param sortType
     *            The sortType to set.
     */
    public void setSortType(String sortType) {
        if (sortType != null) {
            _sortType = sortType.trim();
        }
    }

    /**
     * @return Returns the toDate.
     */
    //@Override
    public String getToDate() {
        return _toDate;
    }

    /**
     * @param toDate
     *            The toDate to set.
     */
    //@Override
    public void setToDate(String toDate) {
        if (toDate != null) {
            _toDate = toDate.trim();
        }
    }

    /**
     * @return Returns the zoneName.
     */
    public String getZoneName() {
        return _zoneName;
    }

    /**
     * @param zoneName
     *            The zoneName to set.
     */
    public void setZoneName(String zoneName) {
        if (zoneName != null) {
            _zoneName = zoneName.trim();
        }
    }

    /**
     * @return Returns the departmentCode.
     */
    public String getDepartmentCode() {
        return _departmentCode;
    }

    /**
     * @param departmentCode
     *            The departmentCode to set.
     */
    public void setDepartmentCode(String departmentCode) {
        _departmentCode = departmentCode;
    }

    /**
     * @return Returns the departmentList.
     */
    public ArrayList getDepartmentList() {
        return _departmentList;
    }

    /**
     * @param departmentList
     *            The departmentList to set.
     */
    public void setDepartmentList(ArrayList departmentList) {
        _departmentList = departmentList;
    }

    /**
     * @return Returns the departmentName.
     */
    public String getDepartmentName() {
        return _departmentName;
    }

    /**
     * @param departmentName
     *            The departmentName to set.
     */
    public void setDepartmentName(String departmentName) {
        _departmentName = departmentName;
    }

    /**
     * @return Returns the divisionCode.
     */
    public String getDivisionCode() {
        return _divisionCode;
    }

    /**
     * @param divisionCode
     *            The divisionCode to set.
     */
    public void setDivisionCode(String divisionCode) {
        _divisionCode = divisionCode;
    }

    /**
     * @return Returns the divisionList.
     */
    public ArrayList getDivisionList() {
        return _divisionList;
    }

    /**
     * @param divisionList
     *            The divisionList to set.
     */
    public void setDivisionList(ArrayList divisionList) {
        _divisionList = divisionList;
    }

    /**
     * @return Returns the divisionName.
     */
    public String getDivisionName() {
        return _divisionName;
    }

    /**
     * @param divisionName
     *            The divisionName to set.
     */
    public void setDivisionName(String divisionName) {
        _divisionName = divisionName;
    }

    /**
     * @return Returns the filterType.
     */
    public String getFilterType() {
        return _filterType;
    }

    /**
     * @param filterType
     *            The filterType to set.
     */
    public void setFilterType(String filterType) {
        _filterType = filterType;
    }

    /**
     * @return Returns the reportType.
     */
    public String getReportType() {
        return _reportType;
    }

    /**
     * @param reportType
     *            The reportType to set.
     */
    public void setReportType(String reportType) {
        _reportType = reportType;
    }

    /**
     * @return Returns the reportTypeList.
     */
    public ArrayList getReportTypeList() {
        return _reportTypeList;
    }

    /**
     * @param reportTypeList
     *            The reportTypeList to set.
     */
    public void setReportTypeList(ArrayList reportTypeList) {
        _reportTypeList = reportTypeList;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the serviceTypeList.
     */
    public ArrayList getServiceTypeList() {
        return _serviceTypeList;
    }

    /**
     * @param serviceTypeList
     *            The serviceTypeList to set.
     */
    public void setServiceTypeList(ArrayList serviceTypeList) {
        _serviceTypeList = serviceTypeList;
    }

    /**
     * @return Returns the userStatus.
     */
    public String getUserStatus() {
        return _userStatus;
    }

    /**
     * @param userStatus
     *            The userStatus to set.
     */
    public void setUserStatus(String userStatus) {
        _userStatus = userStatus;
    }

    /**
     * @return Returns the userStatusList.
     */
    public ArrayList getUserStatusList() {
        return _userStatusList;
    }

    /**
     * @param userStatusList
     *            The userStatusList to set.
     */
    public void setUserStatusList(ArrayList userStatusList) {
        _userStatusList = userStatusList;
    }

    /**
     * @return Returns the userType.
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * @param userType
     *            The userType to set.
     */
    public void setUserType(String userType) {
        _userType = userType;
    }

    /**
     * @return Returns the serviceName.
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * @param ServiceName
     *            The ServiceName to set.
     */
    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }

    /**
     * @return Returns the radioNetCode.
     */
    public String getRadioNetCode() {
        return _radioNetCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setRadioNetCode(String radioNetCode) {
        if (radioNetCode != null) {
            _radioNetCode = radioNetCode.trim();
        }
    }

    /**
     * @return Returns the _txnSubType.
     */
    public String getTxnSubType() {
        return _txnSubType;
    }

    /**
     * @param subType
     *            The _txnSubType to set.
     */
    public void setTxnSubType(String subType) {
        _txnSubType = subType;
    }

    /**
     * @return Returns the TxnSubTypeListSize.
     */
    public int getTxnSubTypeListSize() {
        if (getTxnSubTypeList() != null) {
            return (getTxnSubTypeList().size());
        } else {
            return 0;
        }
    }

    /**
     * @param TxnSubTypeListSize
     *            The serviceTypeListSize to set.
     */
    public void setTxnSubTypeListSize(int txnSubTypeListSize) {
        _txnSubTypeListSize = txnSubTypeListSize;
    }

    /**
     * @return Returns the _txnSubTypeList.
     */
    public ArrayList getTxnSubTypeList() {
        return _txnSubTypeList;
    }

    /**
     * @param subTypeList
     *            The _txnSubTypeList to set.
     */
    public void setTxnSubTypeList(ArrayList subTypeList) {
        _txnSubTypeList = subTypeList;
    }

    /**
     * @return Returns the batchIDText.
     */
    public String getBatchIDText() {
        return _batchIDText;
    }

    /**
     * @param batchIDText
     *            The batchIDText to set.
     */
    public void setBatchIDText(String batchIDText) {
        _batchIDText = batchIDText;
    }

    /**
     * @return
     */
    public String getModuleName() {
        return this._moduleName;
    }

    /**
     * @param moduleName
     */
    public void setModuleName(String moduleName) {
        this._moduleName = moduleName;
    }

    public String getRoamerType() {
        return _roamerType;
    }

    public void setRoamerType(String roamerType) {
        _roamerType = roamerType;
    }

    public String getRoamnetworkCode() {
        return _roamnetworkCode;
    }

    public void setRoamnetworkCode(String roamnetworkCode) {
        _roamnetworkCode = roamnetworkCode;
    }

    public ArrayList getRoamNetworkList() {
        return _roamNetworkList;
    }

    public void setRoamNetworkList(ArrayList roamNetworkList) {
        _roamNetworkList = roamNetworkList;
    }

    public int getRoamNetworkListSize() {
        if (getRoamNetworkList() != null && !getRoamNetworkList().isEmpty()) {
            return getRoamNetworkList().size();
        } else {
            return 0;
        }

    }

    public void setRoamNetworkListSize(int roamNetworkListSize) {
        _roamNetworkListSize = roamNetworkListSize;
    }

    public String getRoamnetworkName() {
        return _roamnetworkName;
    }

    public void setRoamnetworkName(String roamnetworkName) {
        _roamnetworkName = roamnetworkName;
    }

    public void setUserListSize(int userListSize) {
        _userListSize = userListSize;
    }

    public String getAgentCatCode() {
        return _agentCatCode;
    }

    public void setAgentCatCode(String agentCatCode) {
        _agentCatCode = agentCatCode;
    }

    public String getTemptoDate() {
        return _temptoDate;
    }

    public void setTemptoDate(String temptoDate) {
        _temptoDate = temptoDate;
    }

    /**
     * This getter method will return the check box value.
     * 
     * @return Returns the currentDateRptChkBox.
     */
    public String getCurrentDateRptChkBox() {
        return _currentDateRptChkBox;
    }

    /**
     * This setter method will set the check box value.
     * 
     * @param currentDateRptChkBox
     *            The currentDateRptChkBox to set.
     */
    public void setCurrentDateRptChkBox(String currentDateRptChkBox) {
        _currentDateRptChkBox = currentDateRptChkBox;
    }

    /**
     * @return Returns the isReportingDB.
     */
    public boolean isReportingDB() {
        return _isReportingDB;
    }

    /**
     * @param isReportingDB
     *            The isReportingDB to set.
     */
    public void setReportingDB(boolean isReportingDB) {
        _isReportingDB = isReportingDB;
    }


    /**
     * This getter method will return the number of non transacting users
     * 
     * @return Returns the noOfNonTxnUsers.
     */
    public String getNoOfNonTxnUsers() {
        return _noOfNonTxnUsers;
    }

    /**
     * This setter method will set the number of non transacting users
     * 
     * @param noOfNonTxnUsers
     *            The noOfNonTxnUsers to set.
     */
    public void setNoOfNonTxnUsers(String noOfNonTxnUsers) {
        _noOfNonTxnUsers = noOfNonTxnUsers;
    }

    /**
     * @return Returns the noOfUsers.
     */
    public String getRadioCodeForTxn() {
        return _radioCodeForTxn;
    }

    /**
     * @param noOfUsers
     *            The noOfUsers to set.
     */
    public void setRadioCodeForTxn(String noOfUsers) {
        _radioCodeForTxn = noOfUsers;
    }

    /**
     * @return Returns the fileType.
     */
    public String getFileType() {
        return _fileType;
    }

    /**
     * @param fileType
     *            The fileType to set.
     */
    public void setFileType(String fileType) {
        _fileType = fileType;
    }

    /**
     * @return Returns the _geoDomainType.
     */
    public String getGeoDomainType() {
        return _geoDomainType;
    }

    /**
     * @param domainType
     *            The _geoDomainType to set.
     */
    public void setGeoDomainType(String domainType) {
        _geoDomainType = domainType;
    }

    /**
     * @return Returns the _transferNumber.
     */
    public String getTransferNumber() {
        return _transferNumber;
    }

    /**
     * @param number
     *            The _transferNumber to set.
     */
    public void setTransferNumber(String number) {
        _transferNumber = number;
    }

    /**
     * @return Returns the _productType.
     */
    public String getProductType() {
        return _productType;
    }

    /**
     * @param type
     *            The _productType to set.
     */
    public void setProductType(String type) {
        _productType = type;
    }

    /**
     * @return Returns the _productTypeList.
     */
    public ArrayList getProductTypeList() {
        return _productTypeList;
    }

    /**
     * @param typeList
     *            The _productTypeList to set.
     */
    public void setProductTypeList(ArrayList typeList) {
        _productTypeList = typeList;
    }

    public Object getOtherInfo() {
        return _otherInfo;
    }

    public void setOtherInfo(Object otherInfo) {
        _otherInfo = otherInfo;
    }

    public String getReportInitials() {
        return _reportInitials;
    }

    public void setReportInitials(String reportInitials) {
        _reportInitials = reportInitials;
    }

    public String getSchudleDate() {
        return _schudleDate;
    }

    public void setSchudleDate(String schudleDate) {
        _schudleDate = schudleDate;
    }

    /**
     * @return Returns the rptfromDate.
     */
    public String getRptSchDate() {
        return _rptSchDate;
    }

    /**
     * @param rptfromDate
     *            The rptfromDate to set.
     */
    public void setRptSchDate(String rptSchDate) {
        if (rptSchDate != null) {
            _rptSchDate = rptSchDate.trim();
        }
    }

    public String getScheduleType() {
        return _scheduleType;
    }

    public void setScheduleType(String type) {
        _scheduleType = type;
    }

    /**
     * @return Returns the loginID.
     */
    public String getLoginID() {
        return _loginId;
    }

    /**
     * @param loginID
     *            The loginID to set.
     */
    public void setLoginID(String loginId) {
        _loginId = loginId;
    }

    /**
     * @return Returns the fromTime.
     */
    public String getFromTime() {
        return _fromTime;
    }

    /**
     * @param fromTime
     *            The fromTime to set.
     */
    public void setFromTime(String fromTime) {
        _fromTime = fromTime;
    }

    /**
     * @return Returns the toTime.
     */
    public String getToTime() {
        return _toTime;
    }

    /**
     * @param toTime
     *            The toTime to set.
     */
    public void setToTime(String toTime) {
        _toTime = toTime;
    }

    /**
     * @return Returns the fromDateTime.
     */
    public Date getFromDateTime() {
        return _fromDateTime;
    }

    /**
     * @param fromDateTime
     *            The fromDateTime to set.
     */
    public void setFromDateTime(Date fromDateTime) {
        _fromDateTime = fromDateTime;
    }

    /**
     * @return Returns the toDateTime.
     */
    public Date getToDateTime() {
        return _toDateTime;
    }

    /**
     * @param toDateTime
     *            The toDateTime to set.
     */
    public void setToDateTime(Date toDateTime) {
        _toDateTime = toDateTime;
    }

    public String getBonusCode() {
        return _bonusCode;
    }

    public void setBonusCode(String code) {
        _bonusCode = code;
    }

    public String getBonusName() {
        return _bonusName;
    }

    public void setBonusName(String name) {
        _bonusName = name;
    }

    public ArrayList getBonusTypeList() {
        return _bonusTypeList;
    }

    public void setBonusTypeList(ArrayList typeList) {
        _bonusTypeList = typeList;
    }

    /**
     * @return the staffReport
     */
    public String getStaffReport() {
        return _staffReport;
    }

    /**
     * @param staffReport
     *            the staffReport to set
     */
    public void setStaffReport(String staffReport) {
        _staffReport = staffReport;
    }

    public int getUserStatusListSize() {
        if (getUserStatusList() != null && !getUserStatusList().isEmpty()) {
            return getUserStatusList().size();
        } else {
            return 0;
        }
    }

    /**
     * @return the thresholdTypeList
     */
    public ArrayList getThresholdTypeList() {
        return _thresholdTypeList;
    }

    /**
     * @param thresholdTypeList
     *            the thresholdTypeList to set
     */
    public void setThresholdTypeList(ArrayList thresholdTypeList) {
        _thresholdTypeList = thresholdTypeList;
    }

    /**
     * @return the thresholdType
     */
    public String getThresholdType() {
        return _thresholdType;
    }

    /**
     * @param thresholdType
     *            the thresholdType to set
     */
    public void setThresholdType(String thresholdType) {
        _thresholdType = thresholdType;
    }

    public String getFromAmount() {
        return _fromAmount;
    }

    public void setFromAmount(String amount) {
        _fromAmount = amount;
    }

    public String getToAmount() {
        return _toAmount;
    }

    public void setToAmount(String amount) {
        _toAmount = amount;
    }

    public double getSystemFromAmount() {
        return _rptFromAmount;
    }

    public void setSystemFromAmount(double fromAmount) {
        _rptFromAmount = fromAmount;
    }

    public double getSystemToAmount() {
        return _rptToAmount;
    }

    public void setSystemToAmount(double toAmount) {
        _rptToAmount = toAmount;
    }

    public String getSubService() {
        return _subService;
    }

    public void setSubService(String subService) {
        _subService = subService;
    }

    public ArrayList getSubServiceList() {
        return _subServiceList;
    }

    public void setSubServiceList(ArrayList subServiceList) {
        _subServiceList = subServiceList;
    }

    public String getSubServiceName() {
        return _subServiceName;
    }

    public void setSubServiceName(String subServiceName) {
        _subServiceName = subServiceName;
    }
    
    
    /**
     * @return Returns the serviceTypeId.
     */
    public String getServiceTypeId() {
        return _serviceTypeId;
    }

    /**
     * @param serviceTypeId
     *            The serviceTypeId to set.
     */
    public void setServiceTypeId(String serviceTypeId) {
        _serviceTypeId = serviceTypeId;
    }
	
	
	
	
	
	    /**
     * @return Returns the cardGroupSubServiceID.
     */
    public String getCardGroupSubServiceID() {
        return _cardGroupSubServiceID;
    }

    /**
     * @param cardGroupSubServiceID
     *            The cardGroupSubServiceID to set.
     */
    public void setCardGroupSubServiceID(String cardGroupSubServiceID) {
        _cardGroupSubServiceID = cardGroupSubServiceID;
    }
	
	
	
    /**
     * @return Returns the cardGroupSubServiceName.
     */
    public String getCardGroupSubServiceName() {
        return _cardGroupSubServiceName;
    }

    /**
     * @param cardGroupSubServiceName
     *            The cardGroupSubServiceName to set.
     */
    public void setCardGroupSubServiceName(String cardGroupSubServiceName) {
        _cardGroupSubServiceName = cardGroupSubServiceName;
    }
	
	
	/**
     * @return Returns the cardGroupSubServiceList.
     */
    public ArrayList getCardGroupSubServiceList() {
        return _cardGroupSubServiceList;
    }

    /**
     * @param cardGroupSubServiceList
     *            The cardGroupSubServiceList to set.
     */
    public void setCardGroupSubServiceList(ArrayList cardGroupSubServiceList) {
        _cardGroupSubServiceList = cardGroupSubServiceList;
    }
	
	
	
	 public int getCardGroupSubserviceListSize() {
        if (_cardGroupSubServiceList != null && !_cardGroupSubServiceList.isEmpty()) {
            return _cardGroupSubServiceList.size();
        } else {
            return 0;
        }
    }
	
	
	 /**
     * @return Returns the cardGroupList.
     */
    public ArrayList getCardGroupList() {
        return _cardGroupList;
    }

    /**
     * @param cardGroupList
     *            The cardGroupList to set.
     */
    public void setCardGroupList(ArrayList cardGroupList) {
        this._cardGroupList = cardGroupList;
    }
	
	
	/**
     * @return Returns the serviceTypedesc.
     */
    public String getServiceTypedesc() {
        return _serviceTypedesc;
    }

    /**
     * @param serviceTypedesc
     *            The serviceTypedesc to set.
     */
    public void setServiceTypedesc(String serviceTypedesc) {
        _serviceTypedesc = serviceTypedesc;
    }
    
    
    /**
     * @return Returns the cardGroupCode.
     */
    public String getCardGroupCode() {
        return _cardGroupCode;
    }

    /**
     * @param cardGroupCode
     *            The cardGroupCode to set.
     */
    public void setCardGroupCode(String cardGroupCode) {
        _cardGroupCode = cardGroupCode;
    }
    
    /**
     * @return Returns the cardGroupCode.
     */
    public String getKindOfTransaction() {
        return _kindOfTransaction;
    }

    /**
     * @param cardGroupCode
     *            The cardGroupCode to set.
     */
    public void setKindOfTransaction(String kindOfTransaction) {
    	_kindOfTransaction = kindOfTransaction;
    }
    
    public String getPaymentInstDesc() {
        return _paymentInstDesc;
    }

    public void setPaymentInstDesc(String paymentInstDesc) {
        _paymentInstDesc = paymentInstDesc;
    }
}