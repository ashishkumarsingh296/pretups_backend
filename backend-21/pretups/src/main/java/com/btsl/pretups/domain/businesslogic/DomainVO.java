/*
 * #DomainVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 28, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.domain.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class DomainVO implements Serializable {
    private String _domainCodeforDomain;
    private String _domainName;
    private String _domainTypeCode;
    private String _domainTypeName;
    private String _ownerCategory;
    private String _ownerCategoryName;
    private String _domainStatus;
    private String _domainStatusName;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _numberOfCategories;
    private int _radioIndex;
    private long _lastModifiedTime;
    private String _category_type;
    private boolean addCategoryAllowedforThisDomain;
	// added for user default config
    private String _domainCode;
    private boolean agentAllowedCheckBoxDisable;

    public String toString() {
        StringBuffer strBuff = new StringBuffer("\n Domain Code=" + _domainCodeforDomain);
        strBuff.append("\n Domain Name=" + _domainName);
        strBuff.append("\n Domain Type Code=" + _domainTypeCode);
        strBuff.append("\n Domain Type Name=" + _domainTypeName);
        strBuff.append("\n Owner Category=" + _ownerCategory);
        strBuff.append("\n Owner Category Name=" + _ownerCategoryName);
        strBuff.append("\n Domain Status=" + _domainStatus);
        strBuff.append("\n Domain Status Name=" + _domainStatusName);
        strBuff.append("\n Domain Status Name=" + _category_type);
        strBuff.append("\n Domain Created On=" + _createdOn);
        strBuff.append("\n Domain Created By=" + _createdBy);
        strBuff.append("\n Domain Modified On=" + _modifiedOn);
        strBuff.append("\n Domain Modified By=" + _modifiedBy);
        strBuff.append("\n Number Of categories=" + _numberOfCategories);
        strBuff.append("\n Last Modified=" + _lastModifiedTime);
        strBuff.append("\n radio Index=" + _radioIndex);
        strBuff.append("\n Add category allowed=" + addCategoryAllowedforThisDomain);
        
        return strBuff.toString();
    }

    /**
     * @return Returns the category_type.
     */
    public String getCategory_type() {
        return _category_type;
    }

    /**
     * @param category_type
     *            The category_type to set.
     */
    public void setCategory_type(String category_type) {
        _category_type = category_type;
    }

    /**
     * To get the value of domainStatusName field
     * 
     * @return domainStatusName.
     */
    public String getDomainStatusName() {
        return _domainStatusName;
    }

    /**
     * To set the value of domainStatusName field
     */
    public void setDomainStatusName(String domainStatusName) {
        _domainStatusName = domainStatusName;
    }

    /**
     * To get the value of lastModifiedTime field
     * 
     * @return lastModifiedTime.
     */
    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    /**
     * To set the value of lastModifiedTime field
     */
    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
    }

    /**
     * To get the value of ownerCategoryName field
     * 
     * @return ownerCategoryName.
     */
    public String getOwnerCategoryName() {
        return _ownerCategoryName;
    }

    /**
     * To set the value of ownerCategoryName field
     */
    public void setOwnerCategoryName(String ownerCategoryName) {
        _ownerCategoryName = ownerCategoryName;
    }

    /**
     * To get the value of radioIndex field
     * 
     * @return radioIndex.
     */
    public int getRadioIndex() {
        return _radioIndex;
    }

    /**
     * To set the value of radioIndex field
     */
    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    /**
     * To get the value of domainCodeforDomain field
     * 
     * @return domainCodeforDomain.
     */
    public String getDomainCodeforDomain() {
        return _domainCodeforDomain;
    }

    /**
     * To set the value of domainCodeforDomain field
     */
    public void setDomainCodeforDomain(String domainCodeforDomain) {
        _domainCodeforDomain = domainCodeforDomain;
    }

    /**
     * To get the value of createdBy field
     * 
     * @return createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * To set the value of createdBy field
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * To get the value of createdOn field
     * 
     * @return createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * To set the value of createdOn field
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * To get the value of modifiedBy field
     * 
     * @return modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * To set the value of modifiedBy field
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * To get the value of modifiedOn field
     * 
     * @return modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * To set the value of modifiedOn field
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * To get the value of domainName field
     * 
     * @return domainName.
     */
    public String getDomainName() {
        return _domainName;
    }

    /**
     * To set the value of domainName field
     */
    public void setDomainName(String domainName) {
        _domainName = domainName;
    }

    /**
     * To get the value of domainTypeCode field
     * 
     * @return domainTypeCode.
     */
    public String getDomainTypeCode() {
        return _domainTypeCode;
    }

    /**
     * To set the value of domainTypeCode field
     */
    public void setDomainTypeCode(String domainTypeCode) {
        _domainTypeCode = domainTypeCode;
    }

    /**
     * To get the value of numberOfCategories field
     * 
     * @return numberOfCategories.
     */
    public String getNumberOfCategories() {
        return _numberOfCategories;
    }

    /**
     * To set the value of numberOfCategories field
     */
    public void setNumberOfCategories(String numberOfCategories) {
        _numberOfCategories = numberOfCategories;
    }

    /**
     * To get the value of ownerCategory field
     * 
     * @return ownerCategory.
     */
    public String getOwnerCategory() {
        return _ownerCategory;
    }

    /**
     * To set the value of ownerCategory field
     */
    public void setOwnerCategory(String ownerCategory) {
        _ownerCategory = ownerCategory;
    }

    /**
     * To get the value of domainStatus field
     * 
     * @return domainStatus.
     */
    public String getDomainStatus() {
        return _domainStatus;
    }

    /**
     * To set the value of domainStatus field
     */
    public void setDomainStatus(String domainStatus) {
        _domainStatus = domainStatus;
    }

    /**
     * To get the value of domainTypeName field
     * 
     * @return domainTypeName.
     */
    public String getDomainTypeName() {
        return _domainTypeName;
    }

    /**
     * To set the value of domainTypeName field
     */
    public void setDomainTypeName(String domainTypeName) {
        _domainTypeName = domainTypeName;
    }

    /**
     * To get the value of domainCode field
     * 
     * @return domainCode.
     */
    public String getDomainCode() {
        return _domainCode;
    }

    /**
     * To set the value of domainCode field
     */
    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }
    
    public boolean isAddCategoryAllowedforThisDomain() {
		return addCategoryAllowedforThisDomain;
	}

	public void setAddCategoryAllowedforThisDomain(boolean addCategoryAllowedforThisDomain) {
		this.addCategoryAllowedforThisDomain = addCategoryAllowedforThisDomain;
	}

	public boolean isAgentAllowedCheckBoxDisable() {
		return agentAllowedCheckBoxDisable;
	}

	public void setAgentAllowedCheckBoxDisable(boolean agentAllowedCheckBoxDisable) {
		this.agentAllowedCheckBoxDisable = agentAllowedCheckBoxDisable;
	}


	
}
