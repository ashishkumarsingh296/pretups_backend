package com.web.pretups.channel.query.web;

import java.io.Serializable;
import java.util.ArrayList;

import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;


/**
 * Model Class
 * 
 * 
 *
 */
public class C2STransferEnquiryModel implements Serializable {
		@SuppressWarnings("unused")
		private static final long serialVersionUID = 1L;
	 	private String fromDate;
	    private String toDate;
	    private String transferID;
	    private String senderMsisdn;
	    private String receiverMsisdn;
	    private String serviceType;
	    private ArrayList c2sTransferVOList = null;
	    private ArrayList c2sTransferItemsVOList = null;
	    private ArrayList serviceTypeList = null;
	    private String tmpTransferID = null;
	    private C2STransferVO transferVO = null;
	    private String currentDateFlag = null;

	    private String loginId = null;
	    // added for validating popup screens
	    private long time;
	    private ArrayList userList = null;
	    private String userId = null;
	    private boolean isStaffEnquiry = false;
	    private ArrayList bonusVOList = null;

	    private String zoneCode;
	    private String domainCode;
	    private String parentCategoryCode;
	    private String parentUserID;
	    private ArrayList domainList = null;
	    private ArrayList parentCategoryList = null;
	    private ArrayList zoneList = null;
	    private String parentUserName;
	    private ArrayList parentUserList = null;
	    private String loginUserID = null;
	    private String categorySeqNo;
	    private String userType;
	    private String loggedInUserCategoryCode;
	    private String loggedInUserCategoryName;
	    private String loggedInUserName;
	    private String domainName;
	    private String zoneName;
	    private String userName;

		public ArrayList getBonusVOList() {
	        return bonusVOList;
	    }

	    public void setBonusVOList(ArrayList bonusvoList) {
	        bonusVOList = bonusvoList;
	    }


	    public int getTransferVOListSize() {
	        if (c2sTransferVOList != null) {
	            return c2sTransferVOList.size();
	        }
	        return 0;
	    }

	    public int getTransferItemVOListSize() {
	        if (c2sTransferItemsVOList != null) {
	            return c2sTransferItemsVOList.size();
	        }
	        return 0;
	    }

	    /**
	     * @return Returns the categorySeqNo.
	     */
	    public String getCategorySeqNo() {
	        return categorySeqNo;
	    }

	    /**
	     * @param categorySeqNo
	     *            The categorySeqNo to set.
	     */
	    public void setCategorySeqNo(String categorySeqNo) {
	    	this.categorySeqNo = categorySeqNo;
	    }


	    public ArrayList getC2sTransferItemsVOList() {
	        return c2sTransferItemsVOList;
	    }

	    public void setC2sTransferItemsVOList(ArrayList transferItemsVOList) {
	        this.c2sTransferItemsVOList = transferItemsVOList;
	    }

	    public ArrayList getC2sTransferVOList() {
	        return c2sTransferVOList;
	    }

	    public void setC2sTransferVOList(ArrayList transferVOList) {
	        this.c2sTransferVOList = transferVOList;
	    }

	    public String getFromDate() {
	        return fromDate;
	    }

	    public void setFromDate(String formDate) {
	        fromDate = formDate;
	    }

	    public String getReceiverMsisdn() {
	        return receiverMsisdn;
	    }

	    public void setReceiverMsisdn(String receiverMsisdn) {
	       this.receiverMsisdn = receiverMsisdn;
	    }

	    public String getSenderMsisdn() {
	        return senderMsisdn;
	    }

	    public void setSenderMsisdn(String senderMsisdn) {
	        this.senderMsisdn = senderMsisdn;
	    }

	    public String getToDate() {
	        return toDate;
	    }

	    public void setToDate(String toDate) {
	        this.toDate = toDate;
	    }

	    public String getTransferID() {
	        return transferID;
	    }

	    public void setTransferID(String transferID) {
	    	this.transferID = transferID;
	    }

	    public ArrayList getServiceTypeList() {
	        return serviceTypeList;
	    }

	    public void setServiceTypeList(ArrayList serviceTypeList) {
	        this.serviceTypeList = serviceTypeList;
	    }

	    public String getServiceType() {
	        return serviceType;
	    }

	    public void setServiceType(String serviceType) {
	    	this.serviceType = serviceType;
	    }

	    public String getTmpTransferID() {
	        return tmpTransferID;
	    }

	    public void setTmpTransferID(String tmpTransferID) {
	    	this.tmpTransferID = tmpTransferID;
	    }

	    public C2STransferVO getTransferVO() {
	        return transferVO;
	    }

	    public void setTransferVO(C2STransferVO transferVO) {
	        this.transferVO = transferVO;
	    }

	    public void flush() {
	        fromDate = null;
	        toDate = null;
	        transferID = null;
	        senderMsisdn = null;
	        receiverMsisdn = null;
	        serviceType = null;
	        c2sTransferVOList = null;
	        c2sTransferItemsVOList = null;
	        serviceTypeList = null;
	        tmpTransferID = null;
	        transferVO = null;
	        currentDateFlag = null;
	        loginId = null;
	        userId = null;
	        zoneCode = null;
	        zoneList = null;
	        domainList = null;
	        domainCode = null;
	        parentCategoryCode = null;
	        parentCategoryList = null;
	        parentUserName = null;
	        parentUserID = null;
	        parentUserList = null;
	        loginUserID = null;
	        categorySeqNo = null;
	        userType = null;
	        loggedInUserCategoryCode = null;
	        loggedInUserCategoryName = null;
	        loggedInUserName = null;
	        domainName = null;
	        zoneName = null;
	        userName = null;
	    }

	    public void semiFlush() {
	        loginId = null;
	        userId = null;
	        parentUserName = null;
	        parentUserID = null;
	        userName = null;
	    }

	    public void flushPageData()
	    {
	    	this.fromDate = null;
	    	this.toDate = null;
	    	this.transferID = null;
	    	this.senderMsisdn = null;
	    	this.receiverMsisdn = null;
	    	this.serviceType = null;
	    }
	    public String getCurrentDateFlag() {
	        return currentDateFlag;
	    }

	    public void setCurrentDateFlag(String currentDateFlag) {
	        this.currentDateFlag = currentDateFlag;
	    }


	    public long getTime() {
	        return time;
	    }

	    public void setTime(long time) {
	    	this.time = time;
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

	    public int getResultCount() {
	        if (userList != null && !userList.isEmpty()) {
	            return userList.size();
	        } else {
	            return 0;
	        }
	    }

	    /**
	     * @return the loginId
	     */
	    public String getLoginId() {
	        return loginId;
	    }

	    /**
	     * @param loginId
	     *            the loginId to set
	     */
	    public void setLoginId(String loginId) {
	        this.loginId = loginId;
	    }

	    /**
	     * @return Returns the userId.
	     */
	    public String getUserId() {
	        if (userId != null) {
	            return userId.trim();
	        }

	        return userId;
	    }

	    /**
	     * @param userId
	     *            The userId to set.
	     */
	    public void setUserId(String userId) {
	    	this.userId = userId;
	    }

	    /**
	     * @return the isStaffEnquiry
	     */
	    public boolean isStaffEnquiry() {
	        return isStaffEnquiry;
	    }

	    /**
	     * @param isStaffEnquiry
	     *            the isStaffEnquiry to set
	     */
	    public void setStaffEnquiry(boolean isStaffEnquiry) {
	        this.isStaffEnquiry = isStaffEnquiry;
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
	     * @return Returns the parentCategoryCode.
	     */
	    public String getParentCategoryCode() {
	        return parentCategoryCode;
	    }

	    /**
	     * @param parentCategoryCode
	     *            The parentCategoryCode to set.
	     */
	    public void setParentCategoryCode(String parentCategoryCode) {
	    	this.parentCategoryCode = parentCategoryCode;
	    }

	    /**
	     * @return Returns the parentUserID.
	     */
	    public String getParentUserID() {
	        return parentUserID;
	    }

	    /**
	     * @param parentUserID
	     *            The parentUserID to set.
	     */
	    public void setParentUserID(String parentUserID) {
	    	this.parentUserID = parentUserID;
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
	        return domainList;
	    }

	    /**
	     * @param domainList
	     *            The domainList to set.
	     */
	    public void setDomainList(ArrayList domainList) {
	    	this.domainList = domainList;
	    }

	    public int getDomainListSize() {
	        if (domainList != null) {
	            return domainList.size();
	        }
	        return 0;
	    }

	    /**
	     * @return Returns the parentCategoryList.
	     */
	    public ArrayList getParentCategoryList() {
	        return parentCategoryList;
	    }

	    /**
	     * @param parentCategoryList
	     *            The parentCategoryList to set.
	     */
	    public void setParentCategoryList(ArrayList parentCategoryList) {
	    	this.parentCategoryList = parentCategoryList;
	    }

	    /**
	     * @return Returns the parentUserName.
	     */
	    public String getParentUserName() {
	        return parentUserName;
	    }

	    /**
	     * @param parentUserName
	     *            The parentUserName to set.
	     */
	    public void setParentUserName(String parentUserName) {
	    	this.parentUserName = parentUserName;
	    }

	    /**
	     * @return Returns the zoneList.
	     */
	    public ArrayList getZoneList() {
	        return zoneList;
	    }

	    /**
	     * @param zoneList
	     *            The zoneList to set.
	     */
	    public void setZoneList(ArrayList zoneList) {
	    	this.zoneList = zoneList;
	    }

	    /**
	     * @return Returns the zoneListSize.
	     */
	    public int getZoneListSize() {
	        if (getZoneList() != null) {
	            return (getZoneList().size());
	        } else {
	            return 0;
	        }
	    }

	    /**
	     * @return Returns the parentUserList.
	     */
	    public ArrayList getParentUserList() {
	        return parentUserList;
	    }

	    /**
	     * @param parentUserList
	     *            The parentUserList to set.
	     */
	    public void setParentUserList(ArrayList parentUserList) {
	    	this.parentUserList=parentUserList;
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
	     * @return Returns the domainName.
	     */
	    public String getDomainName() {
	        return domainName;
	    }

	    /**
	     * @param domainName
	     *            The domainName to set.
	     */
	    public void setDomainName(String domainName) {
	    	this.domainName = domainName;
	    }

	    /**
	     * @return Returns the loggedInUserCategoryCode.
	     */
	    public String getLoggedInUserCategoryCode() {
	        return loggedInUserCategoryCode;
	    }

	    /**
	     * @param loggedInUserCategoryCode
	     *            The loggedInUserCategoryCode to set.
	     */
	    public void setLoggedInUserCategoryCode(String loggedInUserCategoryCode) {
	    	this.loggedInUserCategoryCode = loggedInUserCategoryCode;
	    }

	    /**
	     * @return Returns the loggedInUserCategoryName.
	     */
	    public String getLoggedInUserCategoryName() {
	        return loggedInUserCategoryName;
	    }

	    /**
	     * @param loggedInUserCategoryName
	     *            The loggedInUserCategoryName to set.
	     */
	    public void setLoggedInUserCategoryName(String loggedInUserCategoryName) {
	    	this.loggedInUserCategoryName = loggedInUserCategoryName;
	    }

	    /**
	     * @return Returns the loggedInUserName.
	     */
	    public String getLoggedInUserName() {
	        return loggedInUserName;
	    }

	    /**
	     * @param loggedInUserName
	     *            The loggedInUserName to set.
	     */
	    public void setLoggedInUserName(String loggedInUserName) {
	    	this.loggedInUserName = loggedInUserName;
	    }

	    /**
	     * @return Returns the userType.
	     */
	    public String getUserType() {
	        return userType;
	    }

	    /**
	     * @param userType
	     *            The userType to set.
	     */
	    public void setUserType(String userType) {
	        this.userType = userType;
	    }

	    /**
	     * @return Returns the zoneName.
	     */
	    public String getZoneName() {
	        return zoneName;
	    }

	    /**
	     * @param zoneName
	     *            The zoneName to set.
	     */
	    public void setZoneName(String zoneName) {
	        this.zoneName = zoneName;
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

	    public int getParentUserListSize() {
	        if (parentUserList != null) {
	            return parentUserList.size();
	        }
	        return 0;
	    }
}
