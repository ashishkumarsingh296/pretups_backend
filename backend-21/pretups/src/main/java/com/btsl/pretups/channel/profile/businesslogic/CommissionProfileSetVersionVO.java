/**
 * @(#)CommissionProfileSetVersionVO.java
 *                                        Copyright(c) 2005, Bharti Telesoft
 *                                        Ltd.
 *                                        All Rights Reserved
 * 
 *                                        <description>
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Author Date History
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        avinash.kamthan Aug 3, 2005 Initital
 *                                        Creation
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 * 
 */

package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class CommissionProfileSetVersionVO implements Serializable

{
    private String _commProfileSetId;
    private String _commProfileSetVersion;
    private Date _applicableFrom;
    private long _oldApplicableFrom;
    private String _modifiedBy;
    private String _createdBy;
    private Date _modifiedOn;
    private Date _createdOn;
    private String dualCommissionType;
    private String dualCommissionTypeDesc;
	private String _otherCommissionProfileSetID;
	private String _commissionType;
	private String _commissionTypeValue;
	private String _otherCommissionName;
	private String _source;
	private String isDefault;
	private String geoCode;
	private String comm_set_name;
	private String created_on;
	private String status;
	private String rowId;
	private String servicesAllowed;
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getServicesAllowed() {
		return servicesAllowed;
	}

	public void setServicesAllowed(String servicesAllowed) {
		this.servicesAllowed = servicesAllowed;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getGeoCode() {
		return geoCode;
	}

	public void setGeoCode(String geoCode) {
		this.geoCode = geoCode;
	}

	public String getComm_set_name() {
		return comm_set_name;
	}

	public void setComm_set_name(String comm_set_name) {
		this.comm_set_name = comm_set_name;
	}

	public String getCreated_on() {
		return created_on;
	}

	public void setCreated_on(String created_on) {
		this.created_on = created_on;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDualCommissionTypeDesc() {
		return dualCommissionTypeDesc;
	}

	public void setDualCommissionTypeDesc(String dualCommissionTypeDesc) {
		this.dualCommissionTypeDesc = dualCommissionTypeDesc;
	}

	public String getDualCommissionType() {
		return dualCommissionType;
	}

	public void setDualCommissionType(String dualCommissionType) {
		this.dualCommissionType = dualCommissionType;
	}

	
	

    @Override
	public String toString() {
		return "CommissionProfileSetVersionVO [_commProfileSetId=" + _commProfileSetId + ", _commProfileSetVersion="
				+ _commProfileSetVersion + ", _applicableFrom=" + _applicableFrom + ", _oldApplicableFrom="
				+ _oldApplicableFrom + ", _modifiedBy=" + _modifiedBy + ", _createdBy=" + _createdBy + ", _modifiedOn="
				+ _modifiedOn + ", _createdOn=" + _createdOn + ", dualCommissionType=" + dualCommissionType
				+ ", dualCommissionTypeDesc=" + dualCommissionTypeDesc + ", _otherCommissionProfileSetID="
				+ _otherCommissionProfileSetID + ", _commissionType=" + _commissionType + ", _commissionTypeValue="
				+ _commissionTypeValue + ", _otherCommissionName=" + _otherCommissionName + ", _source=" + _source
				+ ", isDefault=" + isDefault + ", geoCode=" + geoCode + ", comm_set_name=" + comm_set_name
				+ ", created_on=" + created_on + ", status=" + status + ", rowId=" + rowId + ", servicesAllowed="
				+ servicesAllowed + "]";
	}

	/**
     * @return Returns the commProfileSetId.
     */
    public String getCommProfileSetId() {
        return _commProfileSetId;
    }

    /**
     * @param commProfileSetId
     *            The commProfileSetId to set.
     */
    public void setCommProfileSetId(String commProfileSetId) {
        _commProfileSetId = commProfileSetId;
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

    public Date getApplicableFrom() {
        return _applicableFrom;
    }

    public void setApplicableFrom(Date applicableFrom) {
        _applicableFrom = applicableFrom;
    }

    public String getApplicableFromAsString() {
        if (_applicableFrom != null) {
            try {
                return BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(_applicableFrom));
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }

    }

    public String getCommProfileSetVersion() {
        return _commProfileSetVersion;
    }

    public void setCommProfileSetVersion(String commProfileSetVersion) {
        _commProfileSetVersion = commProfileSetVersion;
    }

    /**
     * @return Returns the oldApplicableFrom.
     */
    public long getOldApplicableFrom() {
        return _oldApplicableFrom;
    }

    /**
     * @param oldApplicableFrom
     *            The oldApplicableFrom to set.
     */
    public void setOldApplicableFrom(long oldApplicableFrom) {
        _oldApplicableFrom = oldApplicableFrom;
    }

    public String getCommSetCombinedID() {
        return _commProfileSetId + ":" + _commProfileSetVersion;
    }
	public String getOtherCommissionProfileSetID() {
                return _otherCommissionProfileSetID;
    }
    public void setOtherCommissionProfileSetID(String otherCommissionProfileSetID) {
                _otherCommissionProfileSetID = otherCommissionProfileSetID;
    }
	public String getCommissionTypeValue() {
                return _commissionTypeValue;
    }
	public void setCommissionTypeValue(String _commissionTypeValue) {
			this._commissionTypeValue = _commissionTypeValue;
	}
	public String getCommissionType() {
			return _commissionType;
	}
	public void setCommissionType(String _commissionType) {
            this._commissionType = _commissionType;
    }
	public String getOtherCommissionName() {
            return _otherCommissionName;
    }
	public void setOtherCommissionName(String _otherCommissionName) {
            this._otherCommissionName = _otherCommissionName;
    }
	public String getSource() {
            return _source;
    }
    public void setSource(String _source) {
            this._source = _source;
    }
}
