/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * UsersVO Model
 * 
 * @author VENKATESAN.S
 */

@Getter
@Setter
public class UsersInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    
    private String pswdModifiedOn;
    private String contactPerson;
    private String contactNo;
    private String designation;
    private String division;
    private String department;
    private String msisdn;
    private String userType;
    private String createdBy;
    private String createdOn;
    private String modifiedBy;
    private String modifiedOn;
    private String address1;
    private String address2;
    private String userId;
    private String userName;
    private String networkCode;
    private String loginId;
    private String pword;
    private String categoryCode;
    private String parentId;
    private String ownerId;
    private String allowedIp;
    private String allowedDays;
    private String fromTime;
    private String toTime;
    private Long invalidPasswordCount;
    private String level1ApprovedBy;
    private String level1ApprovedOn;
    private String level2ApprovedBy;
    private String level2ApprovedOn;
    private String appointmentDate;
    private String passwordCountUpdatedOn;
    private String previousStatus;
    private String batchId;
    private String creationType;
    private String remarks;
    private String pswdReset;
    private String lastLoginOn;
    private String employeeCode;
    private String status;
    private String email;
    private String city;
    private String state;
    private String country;
    private String ssn;
    private String userNamePrefix;
    private String externalCode;
    private String userCode;
    private String shortName;
    private String referenceId;

    private String longitude;

    private String latitude;

    private String company;

    private String fax;

    private String firstname;

    private String lastname;

    private String rsaflag;

    private String barredDeletionBatchid;

    private String authenticationAllowed;

    private String allowdUsrTypCreation;

    private String migrationStatus;

    private String documentType;

    private String documentNo;

    private String paymentType;
    
    protected String validRequestURLs;

	public String getPswdModifiedOn() {
		return pswdModifiedOn;
	}

	public void setPswdModifiedOn(String pswdModifiedOn) {
		this.pswdModifiedOn = pswdModifiedOn;
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

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPword() {
		return pword;
	}

	public void setPword(String pword) {
		this.pword = pword;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getAllowedIp() {
		return allowedIp;
	}

	public void setAllowedIp(String allowedIp) {
		this.allowedIp = allowedIp;
	}

	public String getAllowedDays() {
		return allowedDays;
	}

	public void setAllowedDays(String allowedDays) {
		this.allowedDays = allowedDays;
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

	public Long getInvalidPasswordCount() {
		return invalidPasswordCount;
	}

	public void setInvalidPasswordCount(Long invalidPasswordCount) {
		this.invalidPasswordCount = invalidPasswordCount;
	}

	public String getLevel1ApprovedBy() {
		return level1ApprovedBy;
	}

	public void setLevel1ApprovedBy(String level1ApprovedBy) {
		this.level1ApprovedBy = level1ApprovedBy;
	}

	public String getLevel1ApprovedOn() {
		return level1ApprovedOn;
	}

	public void setLevel1ApprovedOn(String level1ApprovedOn) {
		this.level1ApprovedOn = level1ApprovedOn;
	}

	public String getLevel2ApprovedBy() {
		return level2ApprovedBy;
	}

	public void setLevel2ApprovedBy(String level2ApprovedBy) {
		this.level2ApprovedBy = level2ApprovedBy;
	}

	public String getLevel2ApprovedOn() {
		return level2ApprovedOn;
	}

	public void setLevel2ApprovedOn(String level2ApprovedOn) {
		this.level2ApprovedOn = level2ApprovedOn;
	}

	public String getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getPasswordCountUpdatedOn() {
		return passwordCountUpdatedOn;
	}

	public void setPasswordCountUpdatedOn(String passwordCountUpdatedOn) {
		this.passwordCountUpdatedOn = passwordCountUpdatedOn;
	}

	public String getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getCreationType() {
		return creationType;
	}

	public void setCreationType(String creationType) {
		this.creationType = creationType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPswdReset() {
		return pswdReset;
	}

	public void setPswdReset(String pswdReset) {
		this.pswdReset = pswdReset;
	}

	public String getLastLoginOn() {
		return lastLoginOn;
	}

	public void setLastLoginOn(String lastLoginOn) {
		this.lastLoginOn = lastLoginOn;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getUserNamePrefix() {
		return userNamePrefix;
	}

	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
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

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getRsaflag() {
		return rsaflag;
	}

	public void setRsaflag(String rsaflag) {
		this.rsaflag = rsaflag;
	}

	public String getBarredDeletionBatchid() {
		return barredDeletionBatchid;
	}

	public void setBarredDeletionBatchid(String barredDeletionBatchid) {
		this.barredDeletionBatchid = barredDeletionBatchid;
	}

	public String getAuthenticationAllowed() {
		return authenticationAllowed;
	}

	public void setAuthenticationAllowed(String authenticationAllowed) {
		this.authenticationAllowed = authenticationAllowed;
	}

	public String getAllowdUsrTypCreation() {
		return allowdUsrTypCreation;
	}

	public void setAllowdUsrTypCreation(String allowdUsrTypCreation) {
		this.allowdUsrTypCreation = allowdUsrTypCreation;
	}

	public String getMigrationStatus() {
		return migrationStatus;
	}

	public void setMigrationStatus(String migrationStatus) {
		this.migrationStatus = migrationStatus;
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

	public String getValidRequestURLs() {
		return validRequestURLs;
	}

	public void setValidRequestURLs(String validRequestURLs) {
		this.validRequestURLs = validRequestURLs;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    private transient List<NetworksInfo> commanNetworkList;

	public List<NetworksInfo> getCommanNetworkList() {
		return commanNetworkList;
	}

	public void setCommanNetworkList(List<NetworksInfo> commanNetworkList) {
		this.commanNetworkList = commanNetworkList;
	}
    
    private MessageGatewayInfo messageGatewayinfo;

	public MessageGatewayInfo getMessageGatewayinfo() {
		return messageGatewayinfo;
	}

	public void setMessageGatewayinfo(MessageGatewayInfo messageGatewayinfo) {
		this.messageGatewayinfo = messageGatewayinfo;
	}

    
    
}
