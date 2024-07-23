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
package com.btsl.user.businesslogic.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of USERS.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "USERS")
public class Users implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "NETWORK_CODE")
    private String networkCode;

    @Column(name = "LOGIN_ID")
    private String loginId;

    @Column(name = "PASSWORD")
    private String pword;

    @Column(name = "CATEGORY_CODE")
    private String categoryCode;

    @Column(name = "PARENT_ID")
    private String parentId;

    @Column(name = "OWNER_ID")
    private String ownerId;

    @Column(name = "ALLOWED_IP")
    private String allowedIp;

    @Column(name = "ALLOWED_DAYS")
    private String allowedDays;

    @Column(name = "FROM_TIME")
    private String fromTime;

    @Column(name = "TO_TIME")
    private String toTime;

    @Column(name = "LAST_LOGIN_ON")
    private Date lastLoginOn;

    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PSWD_MODIFIED_ON")
    private Date pswdModifiedOn;

    @Column(name = "CONTACT_PERSON")
    private String contactPerson;

    @Column(name = "CONTACT_NO")
    private String contactNo;

    @Column(name = "DESIGNATION")
    private String designation;

    @Column(name = "DIVISION")
    private String division;

    @Column(name = "DEPARTMENT")
    private String department;

    @Column(name = "MSISDN")
    private String msisdn;

    @Column(name = "USER_TYPE")
    private String userType;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "ADDRESS1")
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "SSN")
    private String ssn;

    @Column(name = "USER_NAME_PREFIX")
    private String userNamePrefix;

    @Column(name = "EXTERNAL_CODE")
    private String externalCode;

    @Column(name = "USER_CODE")
    private String userCode;

    @Column(name = "SHORT_NAME")
    private String shortName;

    @Column(name = "REFERENCE_ID")
    private String referenceId;

    @Column(name = "INVALID_PASSWORD_COUNT")
    private Long invalidPasswordCount;

    @Column(name = "LEVEL1_APPROVED_BY")
    private String level1ApprovedBy;

    @Column(name = "LEVEL1_APPROVED_ON")
    private Date level1ApprovedOn;

    @Column(name = "LEVEL2_APPROVED_BY")
    private String level2ApprovedBy;

    @Column(name = "LEVEL2_APPROVED_ON")
    private Date level2ApprovedOn;

    @Column(name = "APPOINTMENT_DATE")
    private Date appointmentDate;

    @Column(name = "PASSWORD_COUNT_UPDATED_ON")
    private Date passwordCountUpdatedOn;

    @Column(name = "PREVIOUS_STATUS")
    private String previousStatus;

    @Column(name = "BATCH_ID")
    private String batchId;

    @Column(name = "CREATION_TYPE")
    private String creationType;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "PSWD_RESET")
    private String pswdReset;

    @Column(name = "LONGITUDE")
    private String longitude;

    @Column(name = "LATITUDE")
    private String latitude;

    @Column(name = "COMPANY")
    private String company;

    @Column(name = "FAX")
    private String fax;

    @Column(name = "FIRSTNAME")
    private String firstname;

    @Column(name = "LASTNAME")
    private String lastname;

    @Column(name = "RSAFLAG")
    private String rsaflag;

    @Column(name = "BARRED_DELETION_BATCHID")
    private String barredDeletionBatchid;

    @Column(name = "AUTHENTICATION_ALLOWED")
    private String authenticationAllowed;

    @Column(name = "ALLOWD_USR_TYP_CREATION")
    private String allowdUsrTypCreation;

    @Column(name = "MIGRATION_STATUS")
    private String migrationStatus;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @Column(name = "DOCUMENT_NO")
    private String documentNo;

    @Column(name = "PAYMENT_TYPE")
    private String paymentType;

    @Override
    public int hashCode() {
        return Objects.hash(this.getLoginId(), this.getUserId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Users other = (Users) obj;
        return Objects.equals(this.getLoginId(), other.getLoginId())
                && Objects.equals(this.getUserId(), other.getUserId());
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

	public Date getLastLoginOn() {
		return lastLoginOn;
	}

	public void setLastLoginOn(Date lastLoginOn) {
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

	public Date getPswdModifiedOn() {
		return pswdModifiedOn;
	}

	public void setPswdModifiedOn(Date pswdModifiedOn) {
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

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public Date getPasswordCountUpdatedOn() {
		return passwordCountUpdatedOn;
	}

	public void setPasswordCountUpdatedOn(Date passwordCountUpdatedOn) {
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	
    
}
