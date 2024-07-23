// created by Ashutosh for reinventing the voucher management module

package com.btsl.voms.vomsproduct.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class VoucherTypeVO implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private String _voucherType;
    private String _voucherName;
    private String _serviceTypeMapping;
    private String _statusName;
    private String _status;
    private ArrayList _servicesList;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _parentId;
    private String _networkCode;
    private String _networkName;
    private String _userId;
    private long _lastModified;
    private int _radioIndex;
    private String type;

    /*
     * public String toString()
     * {
     * StringBuffer strBuff=new StringBuffer("\n _voucherType :"+ _voucherType);
     * strBuff.append("\n _voucherName :" +_voucherName);
     * strBuff.append("\n _serviceTypeMapping:" +_serviceTypeMapping);
     * strBuff.append("\n status:" +_status);
     * strBuff.append("\n status Name:" +_statusName);
     * strBuff.append("\n _statusName:" +_statusName);
     * strBuff.append("\n Created On:" +_createdOn);
     * strBuff.append("\n Created By:" +_createdBy);
     * strBuff.append("\n Modified On:" +_modifiedOn);
     * strBuff.append("\n Modified By:" +_modifiedBy);
     * strBuff.append("\n Network Code:" +_networkCode);
     * strBuff.append("\n Network Name:" +_networkName);
     * strBuff.append("\n User ID:" +_userId);
     * strBuff.append("\n Last Modified:" +_lastModified);
     * return strBuff.toString();
     * }
     */

    public String getVoucherType() {
        return _voucherType;
    }

    public void setVoucherType(String type) {
        _voucherType = type;
    }

    public String getVoucherName() {
        return _voucherName;
    }

    public void setVoucherName(String name) {
        _voucherName = name;
    }

    public String getServiceTypeMapping() {
        return _serviceTypeMapping;
    }

    public void setServiceTypeMapping(String typeMapping) {
        _serviceTypeMapping = typeMapping;
    }

    public String getStatusName() {
        return _statusName;
    }

    public void setStatusName(String name) {
        _statusName = name;
    }

    public String getStatus() {
        return _status;
    }

    /**
     * To set the value of status field
     */
    public void setStatus(String status) {
        _status = status;
    }

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

    public long getLastModified() {
        return _lastModified;
    }

    /**
     * To set the value of lastModified field
     */
    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
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
     * To get the value of networkCode field
     * 
     * @return networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * To set the value of networkCode field
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * To get the value of parentId field
     * 
     * @return parentId.
     */
    public String getParentId() {
        return _parentId;
    }

    /**
     * To set the value of parentId field
     */
    public void setParentId(String parentId) {
        _parentId = parentId;
    }

    public String getUserId() {
        return _userId;
    }

    /**
     * To set the value of userId field
     */
    public void setUserId(String userId) {
        _userId = userId;
    }

    public int getRadioIndex() {
        return _radioIndex;
    }

    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    public ArrayList getServicesList() {
        return _servicesList;
    }

    public void setServicesList(ArrayList list) {
        _servicesList = list;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
    

}
