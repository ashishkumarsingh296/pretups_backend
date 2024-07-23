package com.web.pretups.restrictedsubs.web;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Model Class
 * 
 * @author lalit.chattar
 *
 */
public class RestrictedSubscriberModel implements Serializable {

	private List<UserGeographiesVO> geoDomainList;
	private String geoDomainListSize;
	private String geoDomainCode;
	private String geoDomainName;
	private String categoryCode;
	private String categoryName;
	private String ownerCategoryName;
	private String userID;
	private String userName;
	private List<Object> categoryList;
	private String domainCode;
	private String domainName;
	private List<UserVO> userList;
	private int userListSize;
	private List<Object> domainList;
	private String loginUserType;
	private String loginUserCatCode;
	private String loginUserCatName;
	private String loginUserID;
	private String loginUserName;
	private boolean selfAllow = false;
	private String ownerID;
	private String ownerName;
	private boolean ownerOnly = false;
	private boolean isOperationPerformed = false;
	private long time = 0;
	private boolean isCorporate = false;
	private boolean isSoho = false;
	private boolean isNormal = false;
	private String fileType;
	private boolean isScheduled = false;
	private boolean isRestricted = false;
	private List<Object> serviceList;
	private String serviceCode;
	private String serviceName;
	private Date processedOn = null;
	@JsonIgnore
	private MultipartFile multipartFile;
	private String scheduleDate;
	private Integer noOfRecords;
	private String scheduleNow = "off";
	private String uploadedFileLocation;
	private String downloadTemplateLocation;
	private String requestFor;
	private String batchID;
	private String loginId;
	private List<ListValueVO> frequency;
	private Integer iterations = 0;
	private String frequencyCode = "DAILY";
    private List<ScheduleBatchMasterVO> scheduleList = null;
    private ScheduleBatchMasterVO scheduleBatchMasterVO = null;
    private List<ScheduleBatchDetailVO> scheduleDetailList;
    private List<ScheduleBatchDetailVO> scheduleMasterVOList;
    private String mobileNumbers;
    private String scheduleStatus;
    private List<String> checklist =null;
    private List<ScheduleBatchDetailVO> deleteList;
    private String msisdn;
    private String scheduleFromDate = null;
	private String scheduleToDate = null;
	private  List<ListValueVO> scheduleStatusList = null;
	
	public void setScheduleList(List<ScheduleBatchMasterVO> scheduleList) {
		this.scheduleList = scheduleList;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	public List<ListValueVO> getFrequency() {
		return frequency;
	}

	public void setFrequency(List<ListValueVO> frequency) {
		this.frequency = frequency;
	}

	public String getFrequencyCode() {
		return frequencyCode;
	}

	public void setFrequencyCode(String frequencyCode) {
		this.frequencyCode = frequencyCode;
	}

	public Integer getIterations() {
		return iterations;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}

	public String getBatchID() {
		return batchID;
	}

	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}

	public String getRequestFor() {
		return requestFor;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public void setRequestFor(String requestFor) {
		this.requestFor = requestFor;
	}

	public String getUploadedFileLocation() {
		return uploadedFileLocation;
	}

	public void setUploadedFileLocation(String uploadedFileLocation) {
		this.uploadedFileLocation = uploadedFileLocation;
	}

	public String getDownloadTemplateLocation() {
		return downloadTemplateLocation;
	}

	public void setDownloadTemplateLocation(String downloadTemplateLocation) {
		this.downloadTemplateLocation = downloadTemplateLocation;
	}

	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public Integer getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(Integer noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

	public String getScheduleNow() {
		return scheduleNow;
	}

	public void setScheduleNow(String scheduleNow) {
		this.scheduleNow = scheduleNow;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<Object> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<Object> serviceList) {
		this.serviceList = serviceList;
	}

	public List<UserGeographiesVO> getGeoDomainList() {
		return geoDomainList;
	}

	public void setGeoDomainList(List<UserGeographiesVO> geoDomainList) {
		this.geoDomainList = geoDomainList;
	}

	public String getGeoDomainListSize() {
		return geoDomainListSize;
	}

	public void setGeoDomainListSize(String geoDomainListSize) {
		this.geoDomainListSize = geoDomainListSize;
	}

	public String getGeoDomainCode() {
		return geoDomainCode;
	}

	public void setGeoDomainCode(String geoDomainCode) {
		this.geoDomainCode = geoDomainCode;
	}

	public String getGeoDomainName() {
		return geoDomainName;
	}

	public void setGeoDomainName(String geoDomainName) {
		this.geoDomainName = geoDomainName;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getOwnerCategoryName() {
		return ownerCategoryName;
	}

	public void setOwnerCategoryName(String ownerCategoryName) {
		this.ownerCategoryName = ownerCategoryName;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<Object> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Object> categoryList) {
		this.categoryList = categoryList;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public List<UserVO> getUserList() {
		return userList;
	}

	public void setUserList(List<UserVO> userList) {
		this.userList = userList;
	}

	public int getUserListSize() {
		return userListSize;
	}

	public void setUserListSize(int userListSize) {
		this.userListSize = userListSize;
	}

	public List<Object> getDomainList() {
		return domainList;
	}

	public void setDomainList(List<Object> domainList) {
		this.domainList = domainList;
	}

	public String getLoginUserType() {
		return loginUserType;
	}

	public void setLoginUserType(String loginUserType) {
		this.loginUserType = loginUserType;
	}

	public String getLoginUserCatCode() {
		return loginUserCatCode;
	}

	public void setLoginUserCatCode(String loginUserCatCode) {
		this.loginUserCatCode = loginUserCatCode;
	}

	public String getLoginUserCatName() {
		return loginUserCatName;
	}

	public void setLoginUserCatName(String loginUserCatName) {
		this.loginUserCatName = loginUserCatName;
	}

	public String getLoginUserID() {
		return loginUserID;
	}

	public void setLoginUserID(String loginUserID) {
		this.loginUserID = loginUserID;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public boolean isSelfAllow() {
		return selfAllow;
	}

	public void setSelfAllow(boolean selfAllow) {
		this.selfAllow = selfAllow;
	}

	public String getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public boolean isOwnerOnly() {
		return ownerOnly;
	}

	public void setOwnerOnly(boolean ownerOnly) {
		this.ownerOnly = ownerOnly;
	}

	public boolean isOperationPerformed() {
		return isOperationPerformed;
	}

	public void setOperationPerformed(boolean isOperationPerformed) {
		this.isOperationPerformed = isOperationPerformed;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
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

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public boolean isScheduled() {
		return isScheduled;
	}

	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	public boolean isRestricted() {
		return isRestricted;
	}

	public void setRestricted(boolean isRestricted) {
		this.isRestricted = isRestricted;
	}

	public List<ScheduleBatchMasterVO> getScheduleList() {
		return scheduleList;
	}
	
    public int getScheduleListSize() {
        if (scheduleList != null) {
            return scheduleList.size();
        }
        return 0;
    }

	public ScheduleBatchMasterVO getScheduleBatchMasterVO() {
		return scheduleBatchMasterVO;
	}

	public void setScheduleBatchMasterVO(ScheduleBatchMasterVO scheduleBatchMasterVO) {
		this.scheduleBatchMasterVO = scheduleBatchMasterVO;
	}

	public List<ScheduleBatchDetailVO> getScheduleDetailList() {
		return scheduleDetailList;
	}

	public void setScheduleDetailList(List<ScheduleBatchDetailVO> scheduleDetailList) {
		this.scheduleDetailList = scheduleDetailList;
	}

	public List<ScheduleBatchDetailVO> getScheduleMasterVOList() {
		return scheduleMasterVOList;
	}

	public void setScheduleMasterVOList(List<ScheduleBatchDetailVO> scheduleMasterVOList) {
		this.scheduleMasterVOList = scheduleMasterVOList;
	}

	public String getMobileNumbers() {
		return mobileNumbers;
	}

	public void setMobileNumbers(String mobileNumbers) {
		this.mobileNumbers = mobileNumbers;
	}

	public String getScheduleStatus() {
		return scheduleStatus;
	}

	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}

	public List<String> getChecklist() {
		return checklist;
	}

	public void setChecklist(List<String> checklist) {
		this.checklist = checklist;
	}

	public List<ScheduleBatchDetailVO> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(List<ScheduleBatchDetailVO> deleteList) {
		this.deleteList = deleteList;
	}
	public String getMsisdn(){
		return msisdn;
	}

	public List<ListValueVO> getScheduleStatusList() {
        return scheduleStatusList;
	}
	
	public void setScheduleStatusList(List<ListValueVO> scheduleStatusList) {
	        this.scheduleStatusList = scheduleStatusList;
	}
	
	public String getScheduleToDate() {
	    return scheduleToDate;
	}
	
	public void setScheduleToDate(String scheduleToDate) {
	    this.scheduleToDate = scheduleToDate;
	}
	
	public String getScheduleFromDate() {
	    return scheduleFromDate;
	}

	public void setScheduleFromDate(String scheduleFromDate) {
	    this.scheduleFromDate = scheduleFromDate;
	}
	
	private String serviceTypeCode;
	
	public String getServiceTypeCode() {
		return serviceTypeCode;
	}
	
	public void setServiceTypeCode(String serviceTypeCode) {
		this.serviceTypeCode = serviceTypeCode;
	}
	
	
	private String  downloadFilePath;
	private String  createdBy;
	private String  modifiedBy;
	private Date modifiedOn;
	public String getDownloadFilePath() {
		return downloadFilePath;
	}
	
	public void setDownloadFilePath(String downloadFilePath) {
		this.downloadFilePath = downloadFilePath;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	
	
	private Date createdOn;
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	private String uploadFilePath;
	public String getUploadFilePath() {
		return uploadFilePath;
	}
	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}
	
	private String executedIterations;
	public String getExecutedIterations() {
		return executedIterations;
	}
	
	public void setExecutedIterations(String executedIterations) {
		this.executedIterations = executedIterations;
	}

	public Date getProcessedOn() {
		return processedOn;
	}
	
	public void setProcessedOn(Date processedOn) {
		this.processedOn = processedOn;
	}
	
	@Override
	public String toString() {
		return "RestrictedSubscriberModel [geoDomainList=" + geoDomainList
				+ ", geoDomainListSize=" + geoDomainListSize
				+ ", geoDomainCode=" + geoDomainCode + ", geoDomainName="
				+ geoDomainName + ", categoryCode=" + categoryCode
				+ ", categoryName=" + categoryName + ", ownerCategoryName="
				+ ownerCategoryName + ", userID=" + userID + ", userName="
				+ userName + ", categoryList=" + categoryList + ", domainCode="
				+ domainCode + ", domainName=" + domainName + ", userList="
				+ userList + ", userListSize=" + userListSize + ", domainList="
				+ domainList + ", loginUserType=" + loginUserType
				+ ", loginUserCatCode=" + loginUserCatCode
				+ ", loginUserCatName=" + loginUserCatName + ", loginUserID="
				+ loginUserID + ", loginUserName=" + loginUserName
				+ ", selfAllow=" + selfAllow + ", ownerID=" + ownerID
				+ ", ownerName=" + ownerName + ", ownerOnly=" + ownerOnly
				+ ", isOperationPerformed=" + isOperationPerformed + ", time="
				+ time + ", isCorporate=" + isCorporate + ", isSoho=" + isSoho
				+ ", isNormal=" + isNormal + ", fileType=" + fileType
				+ ", isScheduled=" + isScheduled + ", isRestricted="
				+ isRestricted + ", serviceList=" + serviceList
				+ ", serviceCode=" + serviceCode + ", serviceName="
				+ serviceName + ", scheduleDate=" + scheduleDate
				+ ", noOfRecords=" + noOfRecords + ", scheduleNow="
				+ scheduleNow + ", uploadedFileLocation="
				+ uploadedFileLocation + ", downloadTemplateLocation="
				+ downloadTemplateLocation + ", requestFor=" + requestFor
				+ ", batchID=" + batchID + ", loginId=" + loginId
				+ ", frequency=" + frequency + ", iterations=" + iterations
				+ "]";
	}
	
}
