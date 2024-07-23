package com.btsl.user.businesslogic;

import java.io.Serializable;

/**
 * @(#)UserGeographiesVO.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Mohit Goel 24/06/2005 Initial Creation
 * 
 *                            This class is used for User Geographies Info
 * 
 */
public class UserGeographiesVO implements Serializable {

    private String _userId;
    private String _graphDomainCode;
    private String _graphDomainName;
    private String _parentGraphDomainCode;
    private String _graphDomainTypeName;
    private int _graphDomainSequenceNumber;
    private String _graphDomainType;
    private String _categoryCode;
    private String _networkName;
    private String isDefault;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getNetworkName() {
		return _networkName;
	}

	public void setNetworkName(String _networkName) {
		this._networkName = _networkName;
	}

	/**
     * @return Returns the geographicalCode.
     */
    public String getGraphDomainCode() {
        return _graphDomainCode;
    }

    /**
     * @param geographicalCode
     *            The geographicalCode to set.
     */
    public void setGraphDomainCode(String geographicalCode) {
        _graphDomainCode = geographicalCode;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        _userId = userId;
    }

    /**
     * @return Returns the graphDomainName.
     */
    public String getGraphDomainName() {
        return _graphDomainName;
    }

    /**
     * @param graphDomainName
     *            The graphDomainName to set.
     */
    public void setGraphDomainName(String graphDomainName) {
        _graphDomainName = graphDomainName;
    }

    /**
     * @return Returns the graphDomainTypeName.
     */
    public String getGraphDomainTypeName() {
        return _graphDomainTypeName;
    }

    /**
     * @param graphDomainTypeName
     *            The graphDomainTypeName to set.
     */
    public void setGraphDomainTypeName(String graphDomainTypeName) {
        _graphDomainTypeName = graphDomainTypeName;
    }

    /**
     * @return Returns the parentGraphDomainCode.
     */
    public String getParentGraphDomainCode() {
        return _parentGraphDomainCode;
    }

    /**
     * @param parentGraphDomainCode
     *            The parentGraphDomainCode to set.
     */
    public void setParentGraphDomainCode(String parentGraphDomainCode) {
        _parentGraphDomainCode = parentGraphDomainCode;
    }

    /**
     * @return Returns the graphDomainType.
     */
    public String getGraphDomainType() {
        return _graphDomainType;
    }

    /**
     * @param graphDomainType
     *            The graphDomainType to set.
     */
    public void setGraphDomainType(String graphDomainType) {
        _graphDomainType = graphDomainType;
    }

    /**
     * @return Returns the graphDomainSequenceNumber.
     */
    public int getGraphDomainSequenceNumber() {
        return _graphDomainSequenceNumber;
    }

    /**
     * @param graphDomainSequenceNumber
     *            The graphDomainSequenceNumber to set.
     */
    public void setGraphDomainSequenceNumber(int graphDomainSequenceNumber) {
        _graphDomainSequenceNumber = graphDomainSequenceNumber;
    }

    public void setCategoryCode(String catCode) {
        _categoryCode = catCode;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }
}
