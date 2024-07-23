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
 * Entity of CATEGORIES.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "CATEGORIES")
public class Categories implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CATEGORY_CODE")
    private String categoryCode;

    @Column(name = "CATEGORY_NAME")
    private String categoryName;

    @Column(name = "DOMAIN_CODE")
    private String domainCode;

    @Column(name = "SEQUENCE_NO")
    private Integer sequenceNo;

    @Column(name = "GRPH_DOMAIN_TYPE")
    private String grphDomainType;

    @Column(name = "MULTIPLE_GRPH_DOMAINS")
    private String multipleGrphDomains;

    @Column(name = "WEB_INTERFACE_ALLOWED")
    private String webInterfaceAllowed;

    @Column(name = "SMS_INTERFACE_ALLOWED")
    private String smsInterfaceAllowed;

    @Column(name = "FIXED_ROLES")
    private String fixedRoles;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "MULTIPLE_LOGIN_ALLOWED")
    private String multipleLoginAllowed;

    @Column(name = "VIEW_ON_NETWORK_BLOCK")
    private String viewOnNetworkBlock;

    @Column(name = "MAX_LOGIN_COUNT")
    private Long maxLoginCount;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "UNCNTRL_TRANSFER_ALLOWED")
    private String uncntrlTransferAllowed;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "USER_ID_PREFIX")
    private String userIdPrefix;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "DOMAIN_ALLOWED")
    private String domainAllowed;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "PARENT_CATEGORY_CODE")
    private String parentCategoryCode;

    @Column(name = "DISPLAY_ALLOWED")
    private String displayAllowed;

    @Column(name = "MODIFY_ALLOWED")
    private String modifiedAllowed;

    @Column(name = "MAX_TXN_MSISDN")
    private Long maxTxnMsisdn;

    @Column(name = "SCHEDULED_TRANSFER_ALLOWED")
    private String scheduledTransferAllowed;

    @Column(name = "RESTRICTED_MSISDNS")
    private String restrictedMsisdns;

    @Column(name = "SERVICES_ALLOWED")
    private String serviceAllowed;

    @Column(name = "PRODUCT_TYPES_ALLOWED")
    private String productTypesAllowed;

    @Column(name = "FIXED_DOMAINS")
    private String fixedDomains;

    @Column(name = "OUTLETS_ALLOWED")
    private String outletsAllowed;

    @Column(name = "AGENT_ALLOWED")
    private String agentAllowed;

    @Column(name = "HIERARCHY_ALLOWED")
    private String hierarchyAllowed;

    @Column(name = "CATEGORY_TYPE")
    private String categoryType;

    @Column(name = "TRANSFERTOLISTONLY")
    private String transfertolistonly;

    @Column(name = "LOW_BAL_ALERT_ALLOW")
    private String lowBalAlertAllow;

    @Column(name = "CP2P_PAYEE_STATUS")
    private String cp2pPayeeStatus;

    @Column(name = "CP2P_PAYER_STATUS")
    private String cp2pPayerStatus;

    @Column(name = "C2S_PAYEE_STATUS")
    private String c2PayeeStatus;

    @Column(name = "CP2P_WITHIN_LIST")
    private String cp2pWithinList;

    @Column(name = "CP2P_WITHIN_LIST_LEVEL")
    private String cp2pWithinListLevel;

    @Column(name = "FIXED_PROFILE_ALLOWED")
    private String fixedProfileAllowed;

    @Column(name = "REG_CHARGES_APPLICABLE")
    private String regChargesApplicable;

    @Column(name = "AUTHENTICATION_TYPE")
    private String authenticationType;

    @Override
    public int hashCode() {
        return Objects.hash(this.getCategoryCode(), this.getCategoryName(), this.getDomainCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Categories other = (Categories) obj;
        return Objects.equals(this.getCategoryCode(), other.getCategoryCode())
                && Objects.equals(this.getCategoryName(), other.getCategoryName())
                && Objects.equals(this.getDomainCode(), other.getDomainCode());
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

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getGrphDomainType() {
		return grphDomainType;
	}

	public void setGrphDomainType(String grphDomainType) {
		this.grphDomainType = grphDomainType;
	}

	public String getMultipleGrphDomains() {
		return multipleGrphDomains;
	}

	public void setMultipleGrphDomains(String multipleGrphDomains) {
		this.multipleGrphDomains = multipleGrphDomains;
	}

	public String getWebInterfaceAllowed() {
		return webInterfaceAllowed;
	}

	public void setWebInterfaceAllowed(String webInterfaceAllowed) {
		this.webInterfaceAllowed = webInterfaceAllowed;
	}

	public String getSmsInterfaceAllowed() {
		return smsInterfaceAllowed;
	}

	public void setSmsInterfaceAllowed(String smsInterfaceAllowed) {
		this.smsInterfaceAllowed = smsInterfaceAllowed;
	}

	public String getFixedRoles() {
		return fixedRoles;
	}

	public void setFixedRoles(String fixedRoles) {
		this.fixedRoles = fixedRoles;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMultipleLoginAllowed() {
		return multipleLoginAllowed;
	}

	public void setMultipleLoginAllowed(String multipleLoginAllowed) {
		this.multipleLoginAllowed = multipleLoginAllowed;
	}

	public String getViewOnNetworkBlock() {
		return viewOnNetworkBlock;
	}

	public void setViewOnNetworkBlock(String viewOnNetworkBlock) {
		this.viewOnNetworkBlock = viewOnNetworkBlock;
	}

	public Long getMaxLoginCount() {
		return maxLoginCount;
	}

	public void setMaxLoginCount(Long maxLoginCount) {
		this.maxLoginCount = maxLoginCount;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getUncntrlTransferAllowed() {
		return uncntrlTransferAllowed;
	}

	public void setUncntrlTransferAllowed(String uncntrlTransferAllowed) {
		this.uncntrlTransferAllowed = uncntrlTransferAllowed;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUserIdPrefix() {
		return userIdPrefix;
	}

	public void setUserIdPrefix(String userIdPrefix) {
		this.userIdPrefix = userIdPrefix;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getDomainAllowed() {
		return domainAllowed;
	}

	public void setDomainAllowed(String domainAllowed) {
		this.domainAllowed = domainAllowed;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getParentCategoryCode() {
		return parentCategoryCode;
	}

	public void setParentCategoryCode(String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}

	public String getDisplayAllowed() {
		return displayAllowed;
	}

	public void setDisplayAllowed(String displayAllowed) {
		this.displayAllowed = displayAllowed;
	}

	public String getModifiedAllowed() {
		return modifiedAllowed;
	}

	public void setModifiedAllowed(String modifiedAllowed) {
		this.modifiedAllowed = modifiedAllowed;
	}

	public Long getMaxTxnMsisdn() {
		return maxTxnMsisdn;
	}

	public void setMaxTxnMsisdn(Long maxTxnMsisdn) {
		this.maxTxnMsisdn = maxTxnMsisdn;
	}

	public String getScheduledTransferAllowed() {
		return scheduledTransferAllowed;
	}

	public void setScheduledTransferAllowed(String scheduledTransferAllowed) {
		this.scheduledTransferAllowed = scheduledTransferAllowed;
	}

	public String getRestrictedMsisdns() {
		return restrictedMsisdns;
	}

	public void setRestrictedMsisdns(String restrictedMsisdns) {
		this.restrictedMsisdns = restrictedMsisdns;
	}

	public String getServiceAllowed() {
		return serviceAllowed;
	}

	public void setServiceAllowed(String serviceAllowed) {
		this.serviceAllowed = serviceAllowed;
	}

	public String getProductTypesAllowed() {
		return productTypesAllowed;
	}

	public void setProductTypesAllowed(String productTypesAllowed) {
		this.productTypesAllowed = productTypesAllowed;
	}

	public String getFixedDomains() {
		return fixedDomains;
	}

	public void setFixedDomains(String fixedDomains) {
		this.fixedDomains = fixedDomains;
	}

	public String getOutletsAllowed() {
		return outletsAllowed;
	}

	public void setOutletsAllowed(String outletsAllowed) {
		this.outletsAllowed = outletsAllowed;
	}

	public String getAgentAllowed() {
		return agentAllowed;
	}

	public void setAgentAllowed(String agentAllowed) {
		this.agentAllowed = agentAllowed;
	}

	public String getHierarchyAllowed() {
		return hierarchyAllowed;
	}

	public void setHierarchyAllowed(String hierarchyAllowed) {
		this.hierarchyAllowed = hierarchyAllowed;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getTransfertolistonly() {
		return transfertolistonly;
	}

	public void setTransfertolistonly(String transfertolistonly) {
		this.transfertolistonly = transfertolistonly;
	}

	public String getLowBalAlertAllow() {
		return lowBalAlertAllow;
	}

	public void setLowBalAlertAllow(String lowBalAlertAllow) {
		this.lowBalAlertAllow = lowBalAlertAllow;
	}

	public String getCp2pPayeeStatus() {
		return cp2pPayeeStatus;
	}

	public void setCp2pPayeeStatus(String cp2pPayeeStatus) {
		this.cp2pPayeeStatus = cp2pPayeeStatus;
	}

	public String getCp2pPayerStatus() {
		return cp2pPayerStatus;
	}

	public void setCp2pPayerStatus(String cp2pPayerStatus) {
		this.cp2pPayerStatus = cp2pPayerStatus;
	}

	public String getC2PayeeStatus() {
		return c2PayeeStatus;
	}

	public void setC2PayeeStatus(String c2PayeeStatus) {
		this.c2PayeeStatus = c2PayeeStatus;
	}

	public String getCp2pWithinList() {
		return cp2pWithinList;
	}

	public void setCp2pWithinList(String cp2pWithinList) {
		this.cp2pWithinList = cp2pWithinList;
	}

	public String getCp2pWithinListLevel() {
		return cp2pWithinListLevel;
	}

	public void setCp2pWithinListLevel(String cp2pWithinListLevel) {
		this.cp2pWithinListLevel = cp2pWithinListLevel;
	}

	public String getFixedProfileAllowed() {
		return fixedProfileAllowed;
	}

	public void setFixedProfileAllowed(String fixedProfileAllowed) {
		this.fixedProfileAllowed = fixedProfileAllowed;
	}

	public String getRegChargesApplicable() {
		return regChargesApplicable;
	}

	public void setRegChargesApplicable(String regChargesApplicable) {
		this.regChargesApplicable = regChargesApplicable;
	}

	public String getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(String authenticationType) {
		this.authenticationType = authenticationType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
