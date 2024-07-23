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
 * ChannelUsers Entity
 * 
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "CHANNEL_USERS")
public class ChannelUsers implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_GRADE")
    private String userGrade;

    @Column(name = "CONTACT_PERSON")
    private String contactPerson;

    @Column(name = "TRANSFER_PROFILE_ID")
    private String transferProfileId;

    @Column(name = "COMM_PROFILE_SET_ID")
    private String commProfileSetId;

    @Column(name = "IN_SUSPEND")
    private String inSuspend;

    @Column(name = "OUT_SUSPEND")
    private String outSuspend;

    @Column(name = "OUTLET_CODE")
    private String outletCode;

    @Column(name = "SUBOUTLET_CODE")
    private String suboutletCode;

    @Column(name = "ACTIVATED_ON")
    private Date activatedOn;

    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @Column(name = "MPAY_PROFILE_ID")
    private String mpayProfileId;

    @Column(name = "USER_PROFILE_ID")
    private String userProfileId;

    @Column(name = "IS_PRIMARY")
    private String isPrimary;

    @Column(name = "MCOMMERCE_SERVICE_ALLOW")
    private String mcommerceServiceAllow;

    @Column(name = "LOW_BAL_ALERT_ALLOW")
    private String lowBalAlertAllow;

    @Column(name = "MCATEGORY_CODE")
    private String mcategoryCode;

    @Column(name = "ALERT_MSISDN")
    private String alertMsisdn;

    @Column(name = "ALERT_TYPE")
    private String alertType;

    @Column(name = "ALERT_EMAIL")
    private String alertEmail;

    @Column(name = "AUTO_FOC_ALLOW")
    private String autoFocAllow;

    @Column(name = "VOMS_DECRYP_KEY")
    private String vomsDecrypKey;

    @Column(name = "TRF_RULE_TYPE")
    private String trfRuleType;

    @Column(name = "AUTO_O2C_ALLOW")
    private String autoO2cAllow;

    @Column(name = "LMS_PROFILE")
    private String lmsProfile;

    @Column(name = "LMS_PROFILE_UPDATED_ON")
    private Date lmsProfileUpdatedOn;

    @Column(name = "ASSOCIATED_MSISDN")
    private String associatedMsisdn;

    @Column(name = "ASSOCIATED_MSISDN_TYPE")
    private String associatedMsisdnType;

    @Column(name = "ASSOCIATED_MSISDN_CDATE")
    private Date associatedMsisdnCdate;

    @Column(name = "ASSOCIATED_MSISDN_MDATE")
    private Date associatedMsisdnMdate;

    @Column(name = "AUTO_C2C_ALLOW")
    private String autoC2cAllow;

    @Column(name = "AUTO_C2C_QUANTITY")
    private float autoC2cQuantity;

    @Column(name = "LAST_ALERT_DATE")
    private Date lastAlertDate;

    @Column(name = "CONTROL_GROUP")
    private String controlGroup;

    @Column(name = "OPT_IN_OUT_STATUS")
    private String optInOutStatus;

    @Column(name = "REF_BASED")
    private String refBased;

    @Column(name = "OPT_IN_OUT_NOTIFY_DATE")
    private Date optInOutNotifyDate;

    @Column(name = "OPT_IN_OUT_RESPONSE_DATE")
    private Date optInOutResponseDate;

    @Column(name = "SOS_ALLOWED")
    private String sosAllowed;

    @Column(name = "SOS_ALLOWED_AMOUNT")
    private Long sosAllowedAmount;

    @Column(name = "SOS_THRESHOLD_LIMIT")
    private Long sosThresholdLimit;

    @Column(name = "LR_ALLOWED")
    private String lrAllowed;

    @Column(name = "LR_MAX_AMOUNT")
    private String lrMaxAmount;

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChannelUsers other = (ChannelUsers) obj;
        return Objects.equals(this.getUserId(), other.getUserId());
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserGrade() {
		return userGrade;
	}

	public void setUserGrade(String userGrade) {
		this.userGrade = userGrade;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getTransferProfileId() {
		return transferProfileId;
	}

	public void setTransferProfileId(String transferProfileId) {
		this.transferProfileId = transferProfileId;
	}

	public String getCommProfileSetId() {
		return commProfileSetId;
	}

	public void setCommProfileSetId(String commProfileSetId) {
		this.commProfileSetId = commProfileSetId;
	}

	public String getInSuspend() {
		return inSuspend;
	}

	public void setInSuspend(String inSuspend) {
		this.inSuspend = inSuspend;
	}

	public String getOutSuspend() {
		return outSuspend;
	}

	public void setOutSuspend(String outSuspend) {
		this.outSuspend = outSuspend;
	}

	public String getOutletCode() {
		return outletCode;
	}

	public void setOutletCode(String outletCode) {
		this.outletCode = outletCode;
	}

	public String getSuboutletCode() {
		return suboutletCode;
	}

	public void setSuboutletCode(String suboutletCode) {
		this.suboutletCode = suboutletCode;
	}

	public Date getActivatedOn() {
		return activatedOn;
	}

	public void setActivatedOn(Date activatedOn) {
		this.activatedOn = activatedOn;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getMpayProfileId() {
		return mpayProfileId;
	}

	public void setMpayProfileId(String mpayProfileId) {
		this.mpayProfileId = mpayProfileId;
	}

	public String getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(String userProfileId) {
		this.userProfileId = userProfileId;
	}

	public String getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(String isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String getMcommerceServiceAllow() {
		return mcommerceServiceAllow;
	}

	public void setMcommerceServiceAllow(String mcommerceServiceAllow) {
		this.mcommerceServiceAllow = mcommerceServiceAllow;
	}

	public String getLowBalAlertAllow() {
		return lowBalAlertAllow;
	}

	public void setLowBalAlertAllow(String lowBalAlertAllow) {
		this.lowBalAlertAllow = lowBalAlertAllow;
	}

	public String getMcategoryCode() {
		return mcategoryCode;
	}

	public void setMcategoryCode(String mcategoryCode) {
		this.mcategoryCode = mcategoryCode;
	}

	public String getAlertMsisdn() {
		return alertMsisdn;
	}

	public void setAlertMsisdn(String alertMsisdn) {
		this.alertMsisdn = alertMsisdn;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public String getAlertEmail() {
		return alertEmail;
	}

	public void setAlertEmail(String alertEmail) {
		this.alertEmail = alertEmail;
	}

	public String getAutoFocAllow() {
		return autoFocAllow;
	}

	public void setAutoFocAllow(String autoFocAllow) {
		this.autoFocAllow = autoFocAllow;
	}

	public String getVomsDecrypKey() {
		return vomsDecrypKey;
	}

	public void setVomsDecrypKey(String vomsDecrypKey) {
		this.vomsDecrypKey = vomsDecrypKey;
	}

	public String getTrfRuleType() {
		return trfRuleType;
	}

	public void setTrfRuleType(String trfRuleType) {
		this.trfRuleType = trfRuleType;
	}

	public String getAutoO2cAllow() {
		return autoO2cAllow;
	}

	public void setAutoO2cAllow(String autoO2cAllow) {
		this.autoO2cAllow = autoO2cAllow;
	}

	public String getLmsProfile() {
		return lmsProfile;
	}

	public void setLmsProfile(String lmsProfile) {
		this.lmsProfile = lmsProfile;
	}

	public Date getLmsProfileUpdatedOn() {
		return lmsProfileUpdatedOn;
	}

	public void setLmsProfileUpdatedOn(Date lmsProfileUpdatedOn) {
		this.lmsProfileUpdatedOn = lmsProfileUpdatedOn;
	}

	public String getAssociatedMsisdn() {
		return associatedMsisdn;
	}

	public void setAssociatedMsisdn(String associatedMsisdn) {
		this.associatedMsisdn = associatedMsisdn;
	}

	public String getAssociatedMsisdnType() {
		return associatedMsisdnType;
	}

	public void setAssociatedMsisdnType(String associatedMsisdnType) {
		this.associatedMsisdnType = associatedMsisdnType;
	}

	public Date getAssociatedMsisdnCdate() {
		return associatedMsisdnCdate;
	}

	public void setAssociatedMsisdnCdate(Date associatedMsisdnCdate) {
		this.associatedMsisdnCdate = associatedMsisdnCdate;
	}

	public Date getAssociatedMsisdnMdate() {
		return associatedMsisdnMdate;
	}

	public void setAssociatedMsisdnMdate(Date associatedMsisdnMdate) {
		this.associatedMsisdnMdate = associatedMsisdnMdate;
	}

	public String getAutoC2cAllow() {
		return autoC2cAllow;
	}

	public void setAutoC2cAllow(String autoC2cAllow) {
		this.autoC2cAllow = autoC2cAllow;
	}

	public float getAutoC2cQuantity() {
		return autoC2cQuantity;
	}

	public void setAutoC2cQuantity(float autoC2cQuantity) {
		this.autoC2cQuantity = autoC2cQuantity;
	}

	public Date getLastAlertDate() {
		return lastAlertDate;
	}

	public void setLastAlertDate(Date lastAlertDate) {
		this.lastAlertDate = lastAlertDate;
	}

	public String getControlGroup() {
		return controlGroup;
	}

	public void setControlGroup(String controlGroup) {
		this.controlGroup = controlGroup;
	}

	public String getOptInOutStatus() {
		return optInOutStatus;
	}

	public void setOptInOutStatus(String optInOutStatus) {
		this.optInOutStatus = optInOutStatus;
	}

	public String getRefBased() {
		return refBased;
	}

	public void setRefBased(String refBased) {
		this.refBased = refBased;
	}

	public Date getOptInOutNotifyDate() {
		return optInOutNotifyDate;
	}

	public void setOptInOutNotifyDate(Date optInOutNotifyDate) {
		this.optInOutNotifyDate = optInOutNotifyDate;
	}

	public Date getOptInOutResponseDate() {
		return optInOutResponseDate;
	}

	public void setOptInOutResponseDate(Date optInOutResponseDate) {
		this.optInOutResponseDate = optInOutResponseDate;
	}

	public String getSosAllowed() {
		return sosAllowed;
	}

	public void setSosAllowed(String sosAllowed) {
		this.sosAllowed = sosAllowed;
	}

	public Long getSosAllowedAmount() {
		return sosAllowedAmount;
	}

	public void setSosAllowedAmount(Long sosAllowedAmount) {
		this.sosAllowedAmount = sosAllowedAmount;
	}

	public Long getSosThresholdLimit() {
		return sosThresholdLimit;
	}

	public void setSosThresholdLimit(Long sosThresholdLimit) {
		this.sosThresholdLimit = sosThresholdLimit;
	}

	public String getLrAllowed() {
		return lrAllowed;
	}

	public void setLrAllowed(String lrAllowed) {
		this.lrAllowed = lrAllowed;
	}

	public String getLrMaxAmount() {
		return lrMaxAmount;
	}

	public void setLrMaxAmount(String lrMaxAmount) {
		this.lrMaxAmount = lrMaxAmount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
