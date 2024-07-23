package com.web.pretups.channel.reports.web;



import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.pretups.lms.businesslogic.LmsRedemptionDetailsVO;


/**
 * 
 * @author sweta.verma
 *
 */
 
public class LmsRedemptionReportModel implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 private String userID;
	    private String zoneCode;
	    private String zoneName;
	    private String networkName;
	    private String networkCode;
	    private String reportHeaderName;
	    private String fromDate;
	    private String toDate;
	    private String userName;
	    private String loginUserID;
	    private Date rptfromDate;
	    private String domainCode;
	    private String domainName;
	    private String categoryName;
	    private String currentDate;
	    private String rptcurrentDate;
	    private String categoryCode;
	    private ArrayList userList = null;
	    private ArrayList zoneList = null;
	    private ArrayList domainList = null;
	    private ArrayList categoryList = null;
	    private Date rpttoDate;
	    private boolean isReportingDB = false;
	    private String currentDateRptChkBox;
	    private String requestType;
	    private String serviceType;
	    private ArrayList redemptionTypeList = null;
	    private String msisdn;
	    private String redemptionType;
	    private String loginID;
	    private String userType;
	    private ArrayList lmsProfileList = null;
	    private String lmsProfileName;
	    private String setId;
	    private String setNamewithSetId;
	    private String optStatus;
	    private ArrayList optList = null;
	    private String optStatusName;
	    private String profileSetID;
	    private ArrayList optInList = null;
	    private HashMap setIdNameMap;
	    private int nameListCount;
	    private String optIn;
	    private ArrayList promotionTypeList = null;
	    private String promotionType;
	    private String promotionStatus;
	    private String fromDateFromStart;
	    private String fromDateToEnd;
	    private String toDateFromStart;
	    private String toDateToEnd;
	    private String rptfromDatefromStart;
	    private String rpttoDatefromStart;
	    private String rptfromDatetoEnd;
	    private String rpttoDatetoEnd;
	    private String pointTxnType;
	    private String pointTxnStatus;
	    private String promotionName;
	    private String optInValue;
	    private String rptCode;
	    private ArrayList<LmsRedemptionDetailsVO> lmsRedemptionDetailsVoList;
	    private int lmsRedemptionDetailsListSize;
   
	            
	    /**
	     * @return Returns the userId.
	     */
	    public String getUserID() {
	        return userID;
	    }

	    /**
	     * @param userId
	     *            The userId to set.
	     */
	    public void setUserID(String userId) {
	        userID = userId;
	    }

	    /**
	     * @return Returns the categoryName.
	     */
	    public String getCategoryName() {
	        return categoryName;
	    }

	    /**
	     * @param categoryName
	     *            The categoryName to set.
	     */
	    public void setCategoryName(String categoryName) {
	        this.categoryName = categoryName;
	    }

	    /**
	     * @return Returns the currentDate.
	     */
	    public String getCurrentDate() {
	        return currentDate;
	    }

	    /**
	     * @param currentDate
	     *            The currentDate to set.
	     */
	    public void setCurrentDate(String currentDate) {
	        this.currentDate = currentDate;
	    }

	    /**
	     * @return Returns the currentDateRptChkBox.
	     */
	    public String getCurrentDateRptChkBox() {
	        return currentDateRptChkBox;
	    }

	    /**
	     * @param currentDateRptChkBox
	     *            The currentDateRptChkBox to set.
	     */
	    public void setCurrentDateRptChkBox(String currentDateRptChkBox) {
	        this.currentDateRptChkBox = currentDateRptChkBox;
	    }

	    /**
	     * @return Returns the domainName.
	     */
	    public String getDomainName() {
	        return this.domainName;
	    }

	    /**
	     * @param domainName
	     *            The domainName to set.
	     */
	    public void setDomainName(String domainName) {
	        this.domainName = domainName;
	    }

	    /**
	     * @return Returns the fromDate.
	     */
	    public String getFromDate() {
	        return fromDate;
	    }

	    /**
	     * @param fromDate
	     *            The fromDate to set.
	     */
	    public void setFromDate(String fromDate) {
	        this.fromDate = fromDate;
	    }

	    /**
	     * @return Returns the isReportingDB.
	     */
	    public boolean isReportingDB() {
	        return isReportingDB;
	    }

	    /**
	     * @param isReportingDB
	     *            The isReportingDB to set.
	     */
	    public void setReportingDB(boolean isReportingDB) {
	        this.isReportingDB = isReportingDB;
	    }

	    /**
	     * @return Returns the networkCode.
	     */
	    public String getNetworkCode() {
	        return networkCode;
	    }

	    /**
	     * @param networkCode
	     *            The networkCode to set.
	     */
	    public void setNetworkCode(String networkCode) {
	        this.networkCode = networkCode;
	    }

	    /**
	     * @return Returns the networkName.
	     */
	    public String getNetworkName() {
	        return networkName;
	    }

	    /**
	     * @param networkName
	     *            The networkName to set.
	     */
	    public void setNetworkName(String networkName) {
	        this.networkName = networkName;
	    }

	    /**
	     * @return Returns the reportHeaderName.
	     */
	    public String getReportHeaderName() {
	        return reportHeaderName;
	    }

	    /**
	     * @param reportHeaderName
	     *            The reportHeaderName to set.
	     */
	    public void setReportHeaderName(String reportHeaderName) {
	        this.reportHeaderName = reportHeaderName;
	    }

	    /**
	     * @return Returns the rptcurrentDate.
	     */
	    public String getRptcurrentDate() {
	        return rptcurrentDate;
	    }

	    /**
	     * @param rptcurrentDate
	     *            The rptcurrentDate to set.
	     */
	    public void setRptcurrentDate(String rptcurrentDate) {
	        this.rptcurrentDate = rptcurrentDate;
	    }

	    /**
	     * @return Returns the rptfromDate.
	     */
	    public Date getRptfromDate() {
	        return rptfromDate;
	    }

	    /**
	     * @param rptfromDate
	     *            The rptfromDate to set.
	     */
	    public void setRptfromDate(Date rptfromDate) {
	        this.rptfromDate = rptfromDate;
	    }

	    /**
	     * @return Returns the rpttoDate.
	     */
	    public Date getRpttoDate() {
	        return rpttoDate;
	    }

	    /**
	     * @param rpttoDate
	     *            The rpttoDate to set.
	     */
	    public void setRpttoDate(Date rpttoDate) {
	        this.rpttoDate = rpttoDate;
	    }

	    /**
	     * @return Returns the toDate.
	     */
	    public String getToDate() {
	        return toDate;
	    }

	    /**
	     * @param toDate
	     *            The toDate to set.
	     */
	    public void setToDate(String toDate) {
	        this.toDate = toDate;
	    }

	    /**
	     * @return Returns the zoneName.
	     */
	    public String getZoneName() {
	        return this.zoneName;
	    }

	    /**
	     * @param zoneName
	     *            The zoneName to set.
	     */
	    public void setZoneName(String zoneName) {
	        this.zoneName = zoneName;
	    }

	    /**
	     * @return Returns the domainCode.
	     */
	    public String getDomainCode() {
	        return domainCode;
	    }

	    /**
	     * @param domainCode
	     *            The domainCode to set.
	     */
	    public void setDomainCode(String domainCode) {
	        this.domainCode = domainCode;
	    }

	    /**
	     * @return Returns the zoneCode.
	     */
	    public String getZoneCode() {
	        return zoneCode;
	    }

	    /**
	     * @param zoneCode
	     *            The zoneCode to set.
	     */
	    public void setZoneCode(String zoneCode) {
	        this.zoneCode = zoneCode;
	    }

	    /**
	     * @return Returns the domainList.
	     */
	    public ArrayList getDomainList() {
	        return this.domainList;
	    }

	    /**
	     * @param domainList
	     *            The domainList to set.
	     */
	    public void setDomainList(ArrayList domainList) {
	        this.domainList = domainList;
	    }

	    /**
	     * @return Returns the zoneList.
	     */
	    public ArrayList getZoneList() {
	        return this.zoneList;
	    }

	    /**
	     * @param zoneList
	     *            The zoneList to set.
	     */
	    public void setZoneList(ArrayList zoneList) {
	        this.zoneList = zoneList;
	    }

	    /**
	     * @return Returns the categoryList.
	     */
	    public ArrayList getCategoryList() {
	        return categoryList;
	    }

	    public int getCategoryListSize() {
	        return categoryList.size();
	    }

	    /**
	     * @param categoryList
	     *            The categoryList to set.
	     */
	    public void setCategoryList(ArrayList categoryList) {
	        this.categoryList = categoryList;
	    }

	    /**
	     * @return Returns the parentCategoryCode.
	     */
	    public String getCategoryCode() {
	        return categoryCode;
	    }

	    /**
	     * @param parentCategoryCode
	     *            The parentCategoryCode to set.
	     */
	    public void setCategoryCode(String categoryCode) {
	        this.categoryCode = categoryCode;
	    }

	    /**
	     * @return Returns the userName.
	     */
	    public String getUserName() {
	        return userName;
	    }

	    /**
	     * @param userName
	     *            The userName to set.
	     */
	    public void setUserName(String userName) {
	        this.userName = userName;
	    }

	   
	    /**
	     * @return Returns the userList.
	     */
	    public ArrayList getUserList() {
	        return userList;
	    }

	    /**
	     * @param userList
	     *            The userList to set.
	     */
	    public void setUserList(ArrayList userList) {
	        this.userList = userList;
	    }

	    /**
	     * @return Returns the userListSize.
	     */
	    public int getUserListSize() {
	        if (userList != null) {
	            return userList.size();
	        } else {
	            return 0;
	        }
	    }

	    /**
	     * @return Returns the loginUserID.
	     */
	    public String getLoginUserID() {
	        return loginUserID;
	    }

	    /**
	     * @param loginUserID
	     *            The loginUserID to set.
	     */
	    public void setLoginUserID(String loginUserID) {
	        this.loginUserID = loginUserID;
	    }

	    /**
	     * @return Returns the domainListSize.
	     */
	    public int getDomainListSize() {
	        if (domainList != null) {
	            return domainList.size();
	        } else {
	            return 0;
	        }
	    }

	    /**
	     * @return Returns the zoneListSize.
	     */
	    public int getZoneListSize() {
	        if (zoneList != null) {
	            return zoneList.size();
	        } else {
	            return 0;
	        }
	    }

	    public void flush() {
	        userID = null;
	        zoneCode = null;
	        zoneName = null;
	        networkName = null;
	        networkCode = null;
	        reportHeaderName = null;
	        fromDate = null;
	        toDate = null;
	        userName = null;
	        loginUserID = null;
	        rptfromDate = null;
	        domainCode = null;
	        domainName = null;
	        categoryName = null;
	        currentDate = null;
	        rptcurrentDate = null;
	        categoryCode = null;
	        rpttoDate = null;
	        currentDateRptChkBox = null;
	        requestType = null;
	        userList = null;
	        zoneList = null;
	        domainList = null;
	        categoryList = null;
	        isReportingDB = false;
	    }

	    public void semiFlush() {
	        userID = null;
	        userName = null;
	        userList = null;
	    }


	    /**
	     * @return Returns the requestType.
	     */
	    public String getRequestType() {
	        if (requestType != null) {
	            return requestType.trim();
	        }
	        return requestType;
	    }

	    /**
	     * @param requestType
	     *            The requestType to set.
	     */
	    public void setRequestType(String requestType) {
	        this.requestType = requestType;
	    }

	    public String getServiceType() {
	        return serviceType;
	    }

	    public void setServiceType(String type) {
	        serviceType = type;
	    }

	    public ArrayList getRedemptionTypeList() {
	        return redemptionTypeList;
	    }

	    public void setRedemptionTypeList(ArrayList typeList) {
	        redemptionTypeList = typeList;
	    }

	    public String getMsisdn() {
	        return msisdn;
	    }

	    public void setMsisdn(String msisdn) {
	        this.msisdn = msisdn;
	    }

	    public String getRedemptionType() {
	        return redemptionType;
	    }

	    public void setRedemptionType(String type) {
	        redemptionType = type;
	    }

	    public int getRedemptionTypeListSize() {
	        if (redemptionTypeList != null) {
	            return redemptionTypeList.size();
	        }
	        return 0;
	    }

	    public String getLoginID() {
	        return loginID;
	    }

	    public void setLoginID(String loginid) {
	        loginID = loginid;
	    }

	    public String getUserType() {
	        return userType;
	    }

	    public void setUserType(String userType) {
	        this.userType = userType;
	    }

	    public ArrayList getLmsProfileList() {
	        return lmsProfileList;
	    }

	    public void setLmsProfileList(ArrayList profileList) {
	        lmsProfileList = profileList;
	    }

	    public String getLmsProfileName() {
	        return lmsProfileName;
	    }

	    public void setLmsProfileName(String profileName) {
	        lmsProfileName = profileName;
	    }

	    public int getLmsProfileListSize() {
	        if (lmsProfileList != null) {
	            return lmsProfileList.size();
	        } else {
	            return 0;
	        }
	    }

	    public String getSetId() {
	        return setId;
	    }

	    public void setSetId(String setid) {
	        setId = setid;
	    }

	    public String getSetNamewithSetId() {
	        return setNamewithSetId;
	    }

	    public void setSetNamewithSetId(String namewithSetId) {
	        setNamewithSetId = namewithSetId;
	    }

	    public String getOptStatus() {
	        return optStatus;
	    }

	    public void setOptStatus(String status) {
	        optStatus = status;
	    }

	    public ArrayList getOptList() {
	        return optList;
	    }

	    public void setOptList(ArrayList list) {
	        optList = list;
	    }

	    public int getOptListSize() {
	        if (getOptList() != null && !getOptList().isEmpty()) {
	            return getOptList().size();
	        } else {
	            return 0;
	        }
	    }

	    public String getOptStatusName() {
	        return optStatusName;
	    }

	    public void setOptStatusName(String statusName) {
	        optStatusName = statusName;
	    }

	    public String getProfileSetID() {
	        return profileSetID;
	    }

	    public void setProfileSetID(String setID) {
	        profileSetID = setID;
	    }

	    public ArrayList getOptInList() {
	        return optInList;
	    }

	    public void setOptInList(ArrayList inList) {
	        optInList = inList;
	    }

	    public HashMap getSetIdNameMap() {
	        return setIdNameMap;
	    }

	    public void setSetIdNameMap(HashMap idNameMap) {
	        setIdNameMap = idNameMap;
	    }

	    public int getNameListCount() {
	        return nameListCount;
	    }

	    public void setNameListCount(int listCount) {
	        nameListCount = listCount;
	    }

	    public String getOptIn() {
	        return optIn;
	    }

	    public void setOptIn(String in) {
	        optIn = in;
	    }

	    public ArrayList getPromotionTypeList() {
	        return promotionTypeList;
	    }

	    public void setPromotionTypeList(ArrayList typeList) {
	        promotionTypeList = typeList;
	    }

	    public String getPromotionType() {
	        return promotionType;
	    }

	    public void setPromotionType(String type) {
	        promotionType = type;
	    }

	    public String getPromotionStatus() {
	        return promotionStatus;
	    }

	    public void setPromotionStatus(String status) {
	        promotionStatus = status;
	    }

	    public String getFromDateFromStart() {
	        return fromDateFromStart;
	    }

	    public void setFromDateFromStart(String fromDateFromStart) {
	        this.fromDateFromStart = fromDateFromStart;
	    }

	    public String getFromDateToEnd() {
	        return fromDateToEnd;
	    }

	    public void setFromDateToEnd(String fromDateToEnd) {
	        this.fromDateToEnd = fromDateToEnd;
	    }

	    public String getToDateFromStart() {
	        return toDateFromStart;
	    }

	    public void setToDateFromStart(String toDateFromStart) {
	        this.toDateFromStart = toDateFromStart;
	    }

	    public String getToDateToEnd() {
	        return toDateToEnd;
	    }

	    public void setToDateToEnd(String toDateToEnd) {
	        this.toDateToEnd = toDateToEnd;
	    }

	    public String getRptfromDatefromStart() {
	        return rptfromDatefromStart;
	    }

	    public void setRptfromDatefromStart(String rptfromDatefromStart) {
	        this.rptfromDatefromStart = rptfromDatefromStart;
	    }

	    public String getRpttoDatefromStart() {
	        return rpttoDatefromStart;
	    }

	    public void setRpttoDatefromStart(String rpttoDatefromStart) {
	        this.rpttoDatefromStart = rpttoDatefromStart;
	    }

	    public String getRptfromDatetoEnd() {
	        return rptfromDatetoEnd;
	    }

	    public void setRptfromDatetoEnd(String rptfromDateToEnd) {
	        rptfromDatetoEnd = rptfromDateToEnd;
	    }

	    public String getRpttoDatetoEnd() {
	        return rpttoDatetoEnd;
	    }

	    public void setRpttoDatetoEnd(String rpttoDatetoEnd) {
	        this.rpttoDatetoEnd = rpttoDatetoEnd;
	    }

	    public String getPointTxnType() {
	        return pointTxnType;
	    }

	    public void setPointTxnType(String pointTxnType) {
	        this.pointTxnType = pointTxnType;
	    }

	    public String getPointTxnStatus() {
	        return pointTxnStatus;
	    }

	    public void setPointTxnStatus(String pointTxnStatus) {
	        this.pointTxnStatus = pointTxnStatus;
	    }

	    /**
	     * @return the promotionName
	     */
	    public String getPromotionName() {
	        return promotionName;
	    }

	    /**
	     * @param promotionName
	     *            the promotionName to set
	     */
	    public void setPromotionName(String promotionName) {
	        this.promotionName = promotionName;
	    }

	    public String getOptInValue() {
	        return optInValue;
	    }

	    public void setOptInValue(String inValue) {
	        optInValue = inValue;
	    }

		
		public String getRptCode() {
			return rptCode;
		}

		public void setRptCode(String rptCode) {
			this.rptCode = rptCode;
		}

		public ArrayList<LmsRedemptionDetailsVO> getLmsRedemptionDetailsVoList() {
			return lmsRedemptionDetailsVoList;
		}

		public void setLmsRedemptionDetailsVoList(
				ArrayList<LmsRedemptionDetailsVO> lmsRedemptionDetailsVoList) {
			this.lmsRedemptionDetailsVoList = lmsRedemptionDetailsVoList;
		}

		public int getLmsRedemptionDetailsListSize() {
			return lmsRedemptionDetailsListSize;
		}

		public void setLmsRedemptionDetailsListSize(
				int lmsRedemptionDetailsListSize) {
			this.lmsRedemptionDetailsListSize = lmsRedemptionDetailsListSize;
		}
	    
}