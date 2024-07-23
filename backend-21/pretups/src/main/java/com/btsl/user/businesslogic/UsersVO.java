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
import java.util.Date;
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
public class UsersVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private Date lastLoginOn;
	private String userName;
	private String employeeCode;
	private String networkCode;
	private String status;
	private String loginId;
	private String email;
	private String pword;
	private Date pswdModifiedOn;
	private String categoryCode;
	private String contactPerson;
	private String parentId;
	private String contactNo;
	private String ownerId;
	private String designation;
	private String allowedIp;
	private String division;
	private String allowedDays;
	private String department;
	private String fromTime;
	private String msisdn;
	private String toTime;
	private String userType;
	private String createdBy;
	private Date createdOn;
	private String modifiedBy;
	private Date modifiedOn;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String ssn;
	private String pswdReset;
	private String remarks;
	private String longitude;
	private String creationType;
	private String latitude;
	private String batchId;
	private String company;
	private String previousStatus;
	private String fax;
	private Date passwordCountUpdatedOn;
	private String firstname;
	private Date appointmentDate;
	private String lastname;
	private Date level2ApprovedOn;
	private String rsaflag;
	private String level2ApprovedBy;
	private String barredDeletionBatchid;
	private Date level1ApprovedOn;
	private String authenticationAllowed;
	private String level1ApprovedBy;
	private String allowdUsrTypCreation;
	private Long invalidPasswordCount;
	private String migrationStatus;
	private String referenceId;
	private String documentType;
	private String shortName;
	private String documentNo;
	private String userCode;
	private String paymentType;
	private String externalCode;
	protected String validRequestURLs;
	private String userNamePrefix;
	private transient List<NetworksVO> commanNetworkList;
	private MessageGatewayVONew messageGateway;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getLastLoginOn() {
		return lastLoginOn;
	}
	public void setLastLoginOn(Date lastLoginOn) {
		this.lastLoginOn = lastLoginOn;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPword() {
		return pword;
	}
	public void setPword(String pword) {
		this.pword = pword;
	}
	public Date getPswdModifiedOn() {
		return pswdModifiedOn;
	}
	public void setPswdModifiedOn(Date pswdModifiedOn) {
		this.pswdModifiedOn = pswdModifiedOn;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getAllowedIp() {
		return allowedIp;
	}
	public void setAllowedIp(String allowedIp) {
		this.allowedIp = allowedIp;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getAllowedDays() {
		return allowedDays;
	}
	public void setAllowedDays(String allowedDays) {
		this.allowedDays = allowedDays;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getFromTime() {
		return fromTime;
	}
	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getToTime() {
		return toTime;
	}
	public void setToTime(String toTime) {
		this.toTime = toTime;
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
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getPswdReset() {
		return pswdReset;
	}
	public void setPswdReset(String pswdReset) {
		this.pswdReset = pswdReset;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getCreationType() {
		return creationType;
	}
	public void setCreationType(String creationType) {
		this.creationType = creationType;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPreviousStatus() {
		return previousStatus;
	}
	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public Date getPasswordCountUpdatedOn() {
		return passwordCountUpdatedOn;
	}
	public void setPasswordCountUpdatedOn(Date passwordCountUpdatedOn) {
		this.passwordCountUpdatedOn = passwordCountUpdatedOn;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public Date getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public Date getLevel2ApprovedOn() {
		return level2ApprovedOn;
	}
	public void setLevel2ApprovedOn(Date level2ApprovedOn) {
		this.level2ApprovedOn = level2ApprovedOn;
	}
	public String getRsaflag() {
		return rsaflag;
	}
	public void setRsaflag(String rsaflag) {
		this.rsaflag = rsaflag;
	}
	public String getLevel2ApprovedBy() {
		return level2ApprovedBy;
	}
	public void setLevel2ApprovedBy(String level2ApprovedBy) {
		this.level2ApprovedBy = level2ApprovedBy;
	}
	public String getBarredDeletionBatchid() {
		return barredDeletionBatchid;
	}
	public void setBarredDeletionBatchid(String barredDeletionBatchid) {
		this.barredDeletionBatchid = barredDeletionBatchid;
	}
	public Date getLevel1ApprovedOn() {
		return level1ApprovedOn;
	}
	public void setLevel1ApprovedOn(Date level1ApprovedOn) {
		this.level1ApprovedOn = level1ApprovedOn;
	}
	public String getAuthenticationAllowed() {
		return authenticationAllowed;
	}
	public void setAuthenticationAllowed(String authenticationAllowed) {
		this.authenticationAllowed = authenticationAllowed;
	}
	public String getLevel1ApprovedBy() {
		return level1ApprovedBy;
	}
	public void setLevel1ApprovedBy(String level1ApprovedBy) {
		this.level1ApprovedBy = level1ApprovedBy;
	}
	public String getAllowdUsrTypCreation() {
		return allowdUsrTypCreation;
	}
	public void setAllowdUsrTypCreation(String allowdUsrTypCreation) {
		this.allowdUsrTypCreation = allowdUsrTypCreation;
	}
	public Long getInvalidPasswordCount() {
		return invalidPasswordCount;
	}
	public void setInvalidPasswordCount(Long invalidPasswordCount) {
		this.invalidPasswordCount = invalidPasswordCount;
	}
	public String getMigrationStatus() {
		return migrationStatus;
	}
	public void setMigrationStatus(String migrationStatus) {
		this.migrationStatus = migrationStatus;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getExternalCode() {
		return externalCode;
	}
	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}
	public String getValidRequestURLs() {
		return validRequestURLs;
	}
	public void setValidRequestURLs(String validRequestURLs) {
		this.validRequestURLs = validRequestURLs;
	}
	public String getUserNamePrefix() {
		return userNamePrefix;
	}
	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}
	public MessageGatewayVONew getMessageGateway() {
		return messageGateway;
	}
	public void setMessageGateway(MessageGatewayVONew messageGateway) {
		this.messageGateway = messageGateway;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public List<NetworksVO> getCommanNetworkList() {
		return commanNetworkList;
	}
	public void setCommanNetworkList(List<NetworksVO> commanNetworkList) {
		this.commanNetworkList = commanNetworkList;
	}

	
	
}
