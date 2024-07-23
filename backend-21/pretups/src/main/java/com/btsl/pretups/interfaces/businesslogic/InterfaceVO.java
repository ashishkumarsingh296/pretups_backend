/*
 * @# InterfaceVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * June 10, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.interfaces.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.btsl.common.ListValueVO;

@Component
@Scope(value = "request")
public class InterfaceVO implements Serializable {
    private String _interfaceName;
    private String _interfaceCategory;
    private String _interfaceCategoryCode;
    private String _interfaceId;
    private String _externalId;
    private String _interfaceDescription;
    private String _interfaceTypeId;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _status;
    private String _statusCode;
    private Date _closureDate;
    private String _language1Message;
    private String _language2Message;
    private int _concurrentConnection;
    private String _singleStateTransaction;
    // private long _validationTimeout;
    // private long _updateTimeOut;
    private int _radioIndex;
    private long _lastModified;
    private String _handlerClass;
    private String _statusType;// Will be either M or A.
    private long _valExpiryTime;
    private long _topUpExpiryTime;
    private String _uriReq;
    private int _maxNodes;
    private String _noOfNodes;
    
    //rest
    private String webServiceType;
	private ArrayList<ListValueVO> interfaceCategoryList;
	private String interfaceCategoryType;
	private String networkCode;
	private String selectedInterface;
	
	





public String getSelectedInterface() {
		return selectedInterface;
	}
	public void setSelectedInterface(String selectedInterface) {
		this.selectedInterface = selectedInterface;
	}
public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
public String getNoOfNodes() {
	return _noOfNodes;
}
public void setNoOfNodes(String _noOfNodes) {
	this._noOfNodes = _noOfNodes;
}
    public int getMaxNodes() {
        return _maxNodes;
    }

    public void setMaxNodes(int _maxNodes) {
        this._maxNodes = _maxNodes;
    }

    // for IN node details
    private String _port = null;
    private String _uri = null;
    private String _nodeStatus;
    private int rowIndex;
    private String _ip = null;
    private ArrayList _nodeStatusList;

    public InterfaceVO(InterfaceVO interfaceVO) {
        this._port = interfaceVO._port;
        this._uri = interfaceVO._uri;
        this._nodeStatus = interfaceVO._nodeStatus;
        this.rowIndex = interfaceVO.rowIndex;
        // this.rowIndex = interfaceVO.rowIndex;
        this._ip = interfaceVO._ip;
    }

    public ArrayList<String> getNodeStatusList() {
        return _nodeStatusList;
    }

    public void setNodeStatusList(ArrayList<String> _nodeStatusList) {
        this._nodeStatusList = _nodeStatusList;
    }

    public String getIp() {
        return _ip;
    }

    public String getUriReq() {
        return _uriReq;
    }

    public void setUriReq(String _uriReq) {
        this._uriReq = _uriReq;
    }

    public void setIp(String _ip) {
        this._ip = _ip;
    }

    public String getPort() {
        return _port;
    }

    public void setPort(String _port) {
        this._port = _port;
    }

    public String getUri() {
        return _uri;
    }

    public void setUri(String _uri) {
        this._uri = _uri;
    }

    public String getNodeStatus() {
        return _nodeStatus;
    }

    public void setNodeStatus(String _nodeStatus) {
        this._nodeStatus = _nodeStatus;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getInterfaceCategoryType() {
		return interfaceCategoryType;
	}

	public void setInterfaceCategoryType(String interfaceCategoryType) {
		this.interfaceCategoryType = interfaceCategoryType;
	}

	public ArrayList<ListValueVO> getInterfaceCategoryList() {
		return interfaceCategoryList;
	}

	public void setInterfaceCategoryList(ArrayList<ListValueVO> interfaceCategoryList) {
		this.interfaceCategoryList = interfaceCategoryList;
	}

	public String getWebServiceType() {
		return webServiceType;
	}

	public void setWebServiceType(String webServiceType) {
		this.webServiceType = webServiceType;
	}
    
    @Override
    public String toString() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("\n Interface Id=" + _interfaceId);
        strBuff.append("\n External Id=" + _externalId);
        strBuff.append("\n Interface Description=" + _interfaceDescription);
        strBuff.append("\n Interface Type Id=" + _interfaceTypeId);
        strBuff.append("\n Status =" + _status);
        strBuff.append("\n Status Code=" + _statusCode);
        strBuff.append("\n Language1 Message=" + _language1Message);
        strBuff.append("\n Language2 Message=" + _language2Message);
        strBuff.append("\n Concurrent Connection=" + _concurrentConnection);
        strBuff.append("\n Single Stage transaction=" + _singleStateTransaction);
        // strBuff.append("\n Validation Time Out=" +_validationTimeout );
        // strBuff.append("\n Update Time Out=" + _updateTimeOut);
        strBuff.append("\n _handlerClass=" + _handlerClass);
        strBuff.append("\n _statusType=" + _statusType);
        strBuff.append("\n Validation Expiry Time=" + _valExpiryTime);
        strBuff.append("\n Update Topup Time=" + _topUpExpiryTime);
        return strBuff.toString();
    }

    // **********Getter and Setter of Status Type of Interface***************

    public String getStatusType() {
        return _statusType;
    }

    public void setStatusType(String statusType) {
        _statusType = statusType;
    }

    public String getHandlerClass() {
        return _handlerClass;
    }

    public void setHandlerClass(String handlerClass) {
        _handlerClass = handlerClass;
    }

    public InterfaceVO() {
    }

    // **********Getter and Setter of interfaceCategoryCode***************

    public String getInterfaceCategoryCode() {
        return _interfaceCategoryCode;
    }

    public void setInterfaceCategoryCode(String interfaceCategoryCode) {
        _interfaceCategoryCode = interfaceCategoryCode;
    }

    // *************************************************************************

    // *************Getter and Setter of statusCode***************************

    public String getStatusCode() {
        return _statusCode;
    }

    public void setStatusCode(String statusCode) {
        _statusCode = statusCode;
    }

    // ***************************************************************************

    // ******************Getter and Setter of interfaceName*********************

    public String getInterfaceName() {
        return _interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        _interfaceName = interfaceName;
    }

    // ***************************************************************************

    // ***************Getter and Setter of interfaceCategory********************

    public String getInterfaceCategory() {
        return _interfaceCategory;
    }

    public void setInterfaceCategory(String interfaceCategory) {
        _interfaceCategory = interfaceCategory;
    }

    // ***************************************************************************

    // ***************Getter and Setter of closureDate**************************

    public Date getClosureDate() {
        return _closureDate;
    }

    public void setClosureDate(Date closureDate) {
        _closureDate = closureDate;
    }

    // ***************************************************************************

    // **************Getter and Setter of concurrentConnection******************

    public int getConcurrentConnection() {
        return _concurrentConnection;
    }

    public void setConcurrentConnection(int concurrentConnection) {
        _concurrentConnection = concurrentConnection;
    }

    // ***************************************************************************

    // *************Getter and Setter of
    // createdBy*********************************

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    // ***************************************************************************

    // ********************Getter and Setter of
    // createdOn*************************

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    // ***************************************************************************

    // **************Getter and setter of
    // externalId******************************

    public String getExternalId() {
        return _externalId;
    }

    public void setExternalId(String externalId) {
        _externalId = externalId;
    }

    // ***************************************************************************

    // ****************Getter and Setter of
    // interfaceDescription*****************

    public String getInterfaceDescription() {
        return _interfaceDescription;
    }

    public void setInterfaceDescription(String interfaceDescription) {
        _interfaceDescription = interfaceDescription;
    }

    // ***************************************************************************

    // **************Getter and Setter of
    // interfaceId*****************************

    public String getInterfaceId() {
        return _interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        _interfaceId = interfaceId;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // interfaceTypeId***********************

    public String getInterfaceTypeId() {
        return _interfaceTypeId;
    }

    public void setInterfaceTypeId(String interfaceTypeId) {
        _interfaceTypeId = interfaceTypeId;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // language1Message***********************

    public String getLanguage1Message() {
        return _language1Message;
    }

    public void setLanguage1Message(String language1Message) {
        _language1Message = language1Message;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // language2Message***********************

    public String getLanguage2Message() {
        return _language2Message;
    }

    public void setLanguage2Message(String language2Message) {
        _language2Message = language2Message;
    }

    // ***************************************************************************

    // ***************Getter and Setter of modifiedBy***********************

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    // ***************************************************************************

    // ***************Getter and Setter of modifiedOn***********************

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // singleStateTransaction***********************

    public String getSingleStateTransaction() {
        return _singleStateTransaction;
    }

    public void setSingleStateTransaction(String singleStateTransaction) {
        _singleStateTransaction = singleStateTransaction;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // status*********************************

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // updateTimeOut**************************

    /*
     * public long getUpdateTimeOut()
     * {
     * return _updateTimeOut;
     * }
     * 
     * public void setUpdateTimeOut(long updateTimeOut)
     * {
     * _updateTimeOut = updateTimeOut;
     * }
     * 
     * //************************************************************************
     * ***
     * 
     * //***************Getter and Setter of
     * validationTimeout**********************
     * 
     * public long getValidationTimeout()
     * {
     * return _validationTimeout;
     * }
     * 
     * public void setValidationTimeout(long validationTimeout)
     * {
     * _validationTimeout = validationTimeout;
     * }
     */

    // ***************************************************************************

    // ***************Getter and Setter of
    // radioIndex******************************

    public int getRadioIndex() {
        return _radioIndex;
    }

    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    // ***************************************************************************

    // ***************Getter and Setter of
    // lastModified******************************

    public long getLastModified() {
        return _lastModified;
    }

    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }

    // ***************************************************************************

    public long getTopUpExpiryTime() {
        return _topUpExpiryTime;
    }

    public void setTopUpExpiryTime(long topUpExpiryTime) {
        _topUpExpiryTime = topUpExpiryTime;
    }

    public long getValExpiryTime() {
        return _valExpiryTime;
    }

    public void setValExpiryTime(long valExpiryTime) {
        _valExpiryTime = valExpiryTime;
    }
}