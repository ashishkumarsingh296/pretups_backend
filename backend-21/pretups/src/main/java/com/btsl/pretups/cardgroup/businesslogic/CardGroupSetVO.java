package com.btsl.pretups.cardgroup.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;

/**
 * @(#)CardGroupSetVO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Mohit Goel 26/08/2005 Initial Creation
 * 
 *                         This class is used for Card Group Set Data
 * 
 */

/**
 * @author deepa.shyam
 *
 */
/**
 * @author deepa.shyam
 *
 */
/**
 * @author deepa.shyam
 *
 */
/**
 * @author deepa.shyam
 *
 */
public class CardGroupSetVO implements Serializable {
    private String _cardGroupSetID;
    private String _cardGroupSetName;
    private String _networkCode;
    private Date _createdOn;
    private String _createdOnDate;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedOnDate;
    private String _modifiedBy;
    private String _lastVersion;
    private String _moduleCode;
    private String _status;
    private String _language1Message = null;
    private String _language2Message = null;
    private String _subServiceType;
    private String _subServiceTypeDesc;
    private long _lastModifiedOn;
    private String _serviceType;
    private String _serviceTypeDesc;
    private String _setType;
    private String _setTypeName;
    private String version;
    // added For default cardgroup
    private String _defaultCardGroup;
    private String _previousDefaultCardGroup;
    private String applicableFromDate;
    private String applicableFromHour;
    private String oldApplicableFromDate;
    private String oldApplicableFromHour;
    private String cardGroupID;
    private ArrayList<ListValueVO> amountTypeList = null;
    private ArrayList<ListValueVO> validityTypeList = null;
    private ArrayList<ServiceSelectorMappingVO> subServiceTypeList=null;
    private ArrayList<ListValueVO> setTypeList= null ;
    private ArrayList<CardGroupSetVO> cardGroupSetNameList= null;
	private ArrayList<CardGroupSetVersionVO> cardGroupSetVersionList = null;
    
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
        final StringBuilder sbd = new StringBuilder("CardGroupSetVO Data ");
        sbd.append("_cardGroupSetID=").append(_cardGroupSetID).append(",");
        sbd.append("_cardGroupSetName=").append(_cardGroupSetName).append(",");
        sbd.append("_networkCode=").append(_networkCode).append(",");
        sbd.append("_createdOn=").append(_createdOn).append(",");
        sbd.append("_createdBy=").append(_createdBy).append(",");
        sbd.append("_modifiedOn=").append(_modifiedOn).append(",");
        sbd.append("_modifiedBy=").append(_modifiedBy).append(",");
        sbd.append("_lastVersion=").append(_lastVersion).append(",");
        sbd.append("_moduleCode=").append(_moduleCode).append(",");
        sbd.append("_status=").append(_status).append(",");
        sbd.append("_lastModifiedOn=").append(_lastModifiedOn).append(",");
        sbd.append("_language1Message=").append(_language1Message).append(",");
        sbd.append("_language2Message=").append(_language2Message).append(",");
        sbd.append("_subServiceType=").append(_subServiceType).append(",");
        sbd.append("_serviceType=").append(_serviceType).append(",");
        sbd.append("_setType=").append(_setType).append(",");
        sbd.append("_defaultCardGroup=").append(_defaultCardGroup).append(",");
        sbd.append("_previousDefaultCardGroup=").append(_previousDefaultCardGroup).append(",");
        sbd.append("_subServiceTypeDesc=").append(_subServiceTypeDesc).append(",");
        sbd.append("_serviceTypeDesc=").append(_serviceTypeDesc).append(",");
        sbd.append("_setTypeName=").append(_setTypeName).append(",");
        sbd.append("applicableFromDate=").append(applicableFromDate).append(",");
        sbd.append("applicableFromHour=").append(applicableFromHour).append(",");
        sbd.append("oldApplicableFromDate=").append(oldApplicableFromDate).append(",");
        sbd.append("oldApplicableFromHour=").append(oldApplicableFromHour).append(",");
        sbd.append("amountTypeList=").append(amountTypeList).append(",");
        sbd.append("validityTypeList=").append(validityTypeList).append(",");
        sbd.append("setTypeList=").append(setTypeList).append(",");
        sbd.append("cardGroupSetNameList=").append(cardGroupSetNameList).append(",");
        sbd.append("cardGroupSetVersionList=").append(cardGroupSetVersionList).append(",");
        sbd.append("version=").append(version).append(",");
        return sbd.toString();
    }

    public String getCardGroupID() {
		return cardGroupID;
	}

	public void setCardGroupID(String cardGroupID) {
		this.cardGroupID = cardGroupID;
	}

	/**
     * @return
     */
    public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the cardGroupSetID.
     */
    public String getCardGroupSetID() {
        return _cardGroupSetID;
    }

    /**
     * @param cardGroupSetID
     *            The cardGroupSetID to set.
     */
    public void setCardGroupSetID(String cardGroupSetID) {
        _cardGroupSetID = cardGroupSetID;
    }

    /**
     * @return Returns the cardGroupSetName.
     */
    public String getCardGroupSetName() {
        return _cardGroupSetName;
    }

    /**
     * @param cardGroupSetName
     *            The cardGroupSetName to set.
     */
    public void setCardGroupSetName(String cardGroupSetName) {
        _cardGroupSetName = cardGroupSetName;
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the lastVersion.
     */
    public String getLastVersion() {
        return _lastVersion;
    }

    /**
     * @param lastVersion
     *            The lastVersion to set.
     */
    public void setLastVersion(String lastVersion) {
        _lastVersion = lastVersion;
    }

    /**
     * @return Returns the modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param modifiedBy
     *            The modifiedBy to set.
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * @return Returns the modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the moduleCode.
     */
    public String getModuleCode() {
        return _moduleCode;
    }

    /**
     * @param moduleCode
     *            The moduleCode to set.
     */
    public void setModuleCode(String moduleCode) {
        _moduleCode = moduleCode;
    }

    /**
     * @return Returns the lastModifiedOn.
     */
    public long getLastModifiedOn() {
        return _lastModifiedOn;
    }

    /**
     * @param lastModifiedOn
     *            The lastModifiedOn to set.
     */
    public void setLastModifiedOn(long lastModifiedOn) {
        _lastModifiedOn = lastModifiedOn;
    }

    /**
     * @return Returns the language1Message.
     */
    public String getLanguage1Message() {
        return _language1Message;
    }

    /**
     * @param language1Message
     *            The language1Message to set.
     */
    public void setLanguage1Message(String language1Message) {
        if (language1Message != null) {
            _language1Message = language1Message.trim();
        }
    }

    /**
     * @return Returns the language2Message.
     */
    public String getLanguage2Message() {
        return _language2Message;
    }

    /**
     * @param language2Message
     *            The language2Message to set.
     */
    public void setLanguage2Message(String language2Message) {
        if (language2Message != null) {
            _language2Message = language2Message.trim();
    }
    }

    /**
     * @return Returns the subServiceType.
     */
    public String getSubServiceType() {
        return _subServiceType;
    }

    /**
     * @param subServiceType
     *            The subServiceType to set.
     */
    public void setSubServiceType(String subServiceType) {
        _subServiceType = subServiceType;
    }

    /**
     * @return Returns the subServiceTypeDescription.
     */
    public String getSubServiceTypeDescription() {
        return _subServiceTypeDesc;
    }

    /**
     * @param subServiceTypeDescription
     *            The subServiceTypeDescription to set.
     */
    public void setSubServiceTypeDescription(String subServiceTypeDescription) {
        _subServiceTypeDesc = subServiceTypeDescription;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the serviveTypeDesc.
     */
    public String getServiceTypeDesc() {
        return _serviceTypeDesc;
    }

    /**
     * @param serviveTypeDesc
     *            The serviveTypeDesc to set.
     */
    public void setServiceTypeDesc(String serviveTypeDesc) {
        _serviceTypeDesc = serviveTypeDesc;
    }

    /**
     * @return Returns the setType.
     */
    public String getSetType() {
        return _setType;
    }

    /**
     * @param setType
     *            The setType to set.
     */
    public void setSetType(String setType) {
        _setType = setType;
    }

    /**
     * @return Returns the setTypeName.
     */
    public String getSetTypeName() {
        return _setTypeName;
    }

    /**
     * @param setTypeName
     *            The setTypeName to set.
     */
    public void setSetTypeName(String setTypeName) {
        _setTypeName = setTypeName;
    }

    /**
     * @return Returns the defaultCardGroup.
     */
    public String getDefaultCardGroup() {
        return _defaultCardGroup;
    }

    /**
     * @param defaultCardGroup
     *            The defaultCardGroup to set.
     */
    public void setDefaultCardGroup(String defaultCardGroup) {
        _defaultCardGroup = defaultCardGroup;
    }

    /**
     * @return Returns the previousDefaultCardGroup.
     */
    public String getPreviousDefaultCardGroup() {
        return _previousDefaultCardGroup;
    }

    /**
     * @param previousDefaultCardGroup
     *            The previousDefaultCardGroup to set.
     */
    public void setPreviousDefaultCardGroup(String previousDefaultCardGroup) {
        _previousDefaultCardGroup = previousDefaultCardGroup;
    }

	/**
	 * @return
	 */
	public String getApplicableFromDate() {
		return applicableFromDate;
	}

	/**
	 * @param applicableFromDate
	 */
	public void setApplicableFromDate(String applicableFromDate) {
		this.applicableFromDate = applicableFromDate;
	}

	/**
	 * @return
	 */
	public String getApplicableFromHour() {
		return applicableFromHour;
	}

	/**
	 * @param applicableFromHour
	 */
	public void setApplicableFromHour(String applicableFromHour) {
		this.applicableFromHour = applicableFromHour;
	}
	
	  /**
	 * @return
	 */
	public ArrayList<ListValueVO> getAmountTypeList() {
			return amountTypeList;
		}

		/**
		 * @param amountTypeList
		 */
		public void setAmountTypeList(ArrayList<ListValueVO> amountTypeList) {
			this.amountTypeList = amountTypeList;
		}

		/**
		 * @return
		 */
		public ArrayList<ListValueVO> getValidityTypeList() {
			return validityTypeList;
		}

		/**
		 * @param validityTypeList
		 */
		public void setValidityTypeList(ArrayList<ListValueVO> validityTypeList) {
			this.validityTypeList = validityTypeList;
		}

		/**
		 * @return
		 */
		public ArrayList<ServiceSelectorMappingVO> getSubServiceTypeList() {
			return subServiceTypeList;
		}

		/**
		 * @param subServiceTypeList
		 */
		public void setSubServiceTypeList(ArrayList<ServiceSelectorMappingVO> subServiceTypeList) {
			this.subServiceTypeList = subServiceTypeList;
		}

		/**
		 * @return
		 */
		public ArrayList<ListValueVO> getSetTypeList() {
			return setTypeList;
		}

		/**
		 * @param setTypeList
		 */
		public void setSetTypeList(ArrayList<ListValueVO> setTypeList) {
			this.setTypeList = setTypeList;
		}

	   /**
	 * @return
	 */
	public ArrayList<CardGroupSetVO> getCardGroupSetNameList() {
	 		return cardGroupSetNameList;
		}

		/**
		 * @param cardGroupSetNameList
		 */
		public void setCardGroupSetNameList(ArrayList<CardGroupSetVO> cardGroupSetNameList) {
			this.cardGroupSetNameList = cardGroupSetNameList;
		}

		/**
		 * @return
		 */
		public ArrayList<CardGroupSetVersionVO> getCardGroupSetVersionList() {
			return cardGroupSetVersionList;
		}

		/**
		 * @param cardGroupSetVersionList
		 */
		public void setCardGroupSetVersionList(ArrayList<CardGroupSetVersionVO> cardGroupSetVersionList) {
			this.cardGroupSetVersionList = cardGroupSetVersionList;
		}

		public String getOldApplicableFromDate() {
			return oldApplicableFromDate;
		}

		public void setOldApplicableFromDate(String oldApplicableFromDate) {
			this.oldApplicableFromDate = oldApplicableFromDate;
		}

		public String getOldApplicableFromHour() {
			return oldApplicableFromHour;
		}

		public void setOldApplicableFromHour(String oldApplicableFromHour) {
			this.oldApplicableFromHour = oldApplicableFromHour;
		}

		public String getCreatedOnStr() {
			return _createdOnDate;
		}

		public void setCreatedOnStr(String createdOn) {
			this._createdOnDate = createdOn;
		}

		public String getModifiedOnStr() {
			return _modifiedOnDate;
		}

		public void setModifiedOnStr(String modifiedOn) {
			this._modifiedOnDate = modifiedOn;
		}
		
		
}
