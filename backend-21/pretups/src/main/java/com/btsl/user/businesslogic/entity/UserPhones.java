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
 * UserPhone Entity
 * 
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "USER_PHONES")
public class UserPhones implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "USER_PHONES_ID")
    private Long userPhoneId;

    @Column(name = "MSISDN")
    private String msisdn;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRIMARY_NUMBER")
    private String primaryNumber;

    @Column(name = "SMS_PIN")
    private String smsPin;

    @Column(name = "PIN_REQUIRED")
    private String pinRequired;

    @Column(name = "PHONE_PROFILE")
    private String phoneProfile;

    @Column(name = "PHONE_LANGUAGE")
    private String phoneLanguage;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "INVALID_PIN_COUNT")
    private Long invalidPinCount;

    @Column(name = "LAST_TRANSACTION_STATUS")
    private String lastTransactionStatus;

    @Column(name = "LAST_TRANSACTION_ON")
    private Date lastTransactionOn;

    @Column(name = "PIN_MODIFIED_ON")
    private Date pinModifiedOn;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "LAST_TRANSFER_ID")
    private String lastTransferId;

    @Column(name = "LAST_TRANSFER_TYPE")
    private String lastTransferType;

    @Column(name = "PREFIX_ID")
    private Long prefixId;

    @Column(name = "TEMP_TRANSFER_ID")
    private String tempTransferId;

    @Column(name = "FIRST_INVALID_PIN_TIME")
    private Date firstInvalidPinTime;

    @Column(name = "ACCESS_TYPE")
    private String accessType;

    @Column(name = "FROM_TIME")
    private String fromTime;

    @Column(name = "TO_TIME")
    private String toTime;

    @Column(name = "ALLOWED_DAYS")
    private String allowedDays;

    @Column(name = "ALLOWED_IP")
    private String allowedIp;

    @Column(name = "LAST_LOGIN_ON")
    private Date lastLoginOn;

    @Column(name = "PIN_RESET")
    private String pinReset;

    @Column(name = "LAST_ACCESS_ON")
    private Date lastAccessOn;

    @Column(name = "IMEI")
    private String imei;

    @Column(name = "DECRYPTION_KEY")
    private String decryptionKey;

    @Column(name = "OTP")
    private String otp;

    @Column(name = "OTP_CREATED_ON")
    private Date otpCreatedOn;

    @Column(name = "TOKEN_LASTUSED_DATE")
    private Date tokenLastusedDate;

    @Column(name = "MHASH")
    private String mhash;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "STATUS_AUTO_C2C")
    private String statusAutoC2c;

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserPhoneId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserPhones other = (UserPhones) obj;
        return Objects.equals(this.getUserPhoneId(), other.getUserPhoneId());
    }

	public Long getUserPhoneId() {
		return userPhoneId;
	}

	public void setUserPhoneId(Long userPhoneId) {
		this.userPhoneId = userPhoneId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrimaryNumber() {
		return primaryNumber;
	}

	public void setPrimaryNumber(String primaryNumber) {
		this.primaryNumber = primaryNumber;
	}

	public String getSmsPin() {
		return smsPin;
	}

	public void setSmsPin(String smsPin) {
		this.smsPin = smsPin;
	}

	public String getPinRequired() {
		return pinRequired;
	}

	public void setPinRequired(String pinRequired) {
		this.pinRequired = pinRequired;
	}

	public String getPhoneProfile() {
		return phoneProfile;
	}

	public void setPhoneProfile(String phoneProfile) {
		this.phoneProfile = phoneProfile;
	}

	public String getPhoneLanguage() {
		return phoneLanguage;
	}

	public void setPhoneLanguage(String phoneLanguage) {
		this.phoneLanguage = phoneLanguage;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getInvalidPinCount() {
		return invalidPinCount;
	}

	public void setInvalidPinCount(Long invalidPinCount) {
		this.invalidPinCount = invalidPinCount;
	}

	public String getLastTransactionStatus() {
		return lastTransactionStatus;
	}

	public void setLastTransactionStatus(String lastTransactionStatus) {
		this.lastTransactionStatus = lastTransactionStatus;
	}

	public Date getLastTransactionOn() {
		return lastTransactionOn;
	}

	public void setLastTransactionOn(Date lastTransactionOn) {
		this.lastTransactionOn = lastTransactionOn;
	}

	public Date getPinModifiedOn() {
		return pinModifiedOn;
	}

	public void setPinModifiedOn(Date pinModifiedOn) {
		this.pinModifiedOn = pinModifiedOn;
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

	public String getLastTransferId() {
		return lastTransferId;
	}

	public void setLastTransferId(String lastTransferId) {
		this.lastTransferId = lastTransferId;
	}

	public String getLastTransferType() {
		return lastTransferType;
	}

	public void setLastTransferType(String lastTransferType) {
		this.lastTransferType = lastTransferType;
	}

	public Long getPrefixId() {
		return prefixId;
	}

	public void setPrefixId(Long prefixId) {
		this.prefixId = prefixId;
	}

	public String getTempTransferId() {
		return tempTransferId;
	}

	public void setTempTransferId(String tempTransferId) {
		this.tempTransferId = tempTransferId;
	}

	public Date getFirstInvalidPinTime() {
		return firstInvalidPinTime;
	}

	public void setFirstInvalidPinTime(Date firstInvalidPinTime) {
		this.firstInvalidPinTime = firstInvalidPinTime;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
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

	public String getAllowedDays() {
		return allowedDays;
	}

	public void setAllowedDays(String allowedDays) {
		this.allowedDays = allowedDays;
	}

	public String getAllowedIp() {
		return allowedIp;
	}

	public void setAllowedIp(String allowedIp) {
		this.allowedIp = allowedIp;
	}

	public Date getLastLoginOn() {
		return lastLoginOn;
	}

	public void setLastLoginOn(Date lastLoginOn) {
		this.lastLoginOn = lastLoginOn;
	}

	public String getPinReset() {
		return pinReset;
	}

	public void setPinReset(String pinReset) {
		this.pinReset = pinReset;
	}

	public Date getLastAccessOn() {
		return lastAccessOn;
	}

	public void setLastAccessOn(Date lastAccessOn) {
		this.lastAccessOn = lastAccessOn;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getDecryptionKey() {
		return decryptionKey;
	}

	public void setDecryptionKey(String decryptionKey) {
		this.decryptionKey = decryptionKey;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Date getOtpCreatedOn() {
		return otpCreatedOn;
	}

	public void setOtpCreatedOn(Date otpCreatedOn) {
		this.otpCreatedOn = otpCreatedOn;
	}

	public Date getTokenLastusedDate() {
		return tokenLastusedDate;
	}

	public void setTokenLastusedDate(Date tokenLastusedDate) {
		this.tokenLastusedDate = tokenLastusedDate;
	}

	public String getMhash() {
		return mhash;
	}

	public void setMhash(String mhash) {
		this.mhash = mhash;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getStatusAutoC2c() {
		return statusAutoC2c;
	}

	public void setStatusAutoC2c(String statusAutoC2c) {
		this.statusAutoC2c = statusAutoC2c;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
