/*
 * #UserCombinedServiceVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 26, 2005 Amit Ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.ota.services.businesslogic;

import java.io.Serializable;

public class UserCombinedServiceVO implements Serializable {
    private String _selectedService;
    private UserServicesVO _userServicesVO;
    private String _operation;
    private int _position;
    private String _serviceId;
    private String _majorVersion;
    private String _minorVersion;
    private String _status;

    public UserCombinedServiceVO() {
    }

    public UserCombinedServiceVO(UserServicesVO p_userServicesVO) {
        this._userServicesVO = new UserServicesVO();
        this._userServicesVO = p_userServicesVO;
        /*
         * this._userServicesVO.setServiceID(p_userServicesVO.getServiceID());
         * this._userServicesVO.setVersion(p_userServicesVO.getVersion());
         * this._userServicesVO.setLabel1(p_userServicesVO.getLabel1());
         * this._userServicesVO.setLabel2(p_userServicesVO.getLabel2());
         * this._userServicesVO.setStatus(p_userServicesVO.getStatus());
         * this._userServicesVO.setDescription(p_userServicesVO.getDescription())
         * ;
         */
    }

    /**
     * To get the value of status field
     * 
     * @return status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * To set the value of status field
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * To get the value of majorVersion field
     * 
     * @return majorVersion.
     */
    public String getMajorVersion() {
        return _majorVersion;
    }

    /**
     * To set the value of majorVersion field
     */
    public void setMajorVersion(String majorVersion) {
        _majorVersion = majorVersion;
    }

    /**
     * To get the value of minorVersion field
     * 
     * @return minorVersion.
     */
    public String getMinorVersion() {
        return _minorVersion;
    }

    /**
     * To set the value of minorVersion field
     */
    public void setMinorVersion(String minorVersion) {
        _minorVersion = minorVersion;
    }

    /**
     * To get the value of serviceId field
     * 
     * @return serviceId.
     */
    public String getServiceId() {
        return _serviceId;
    }

    /**
     * To set the value of serviceId field
     */
    public void setServiceId(String serviceId) {
        _serviceId = serviceId;
    }

    /**
     * To get the value of position field
     * 
     * @return position.
     */
    public int getPosition() {
        return _position;
    }

    /**
     * To set the value of position field
     */
    public void setPosition(int position) {
        _position = position;
    }

    /**
     * To get the value of operation field
     * 
     * @return operation.
     */
    public String getOperation() {
        return _operation;
    }

    /**
     * To set the value of operation field
     */
    public void setOperation(String operation) {
        _operation = operation;
    }

    /**
     * To get the value of selectedService field
     * 
     * @return selectedService.
     */
    public String getSelectedService() {
        return _selectedService;
    }

    /**
     * To set the value of selectedService field
     */
    public void setSelectedService(String selectedService) {
        _selectedService = selectedService;
    }

    /**
     * To get the value of userServicesVO field
     * 
     * @return userServicesVO.
     */
    public UserServicesVO getUserServicesVO() {
        return _userServicesVO;
    }

    /**
     * To set the value of userServicesVO field
     */
    public void setUserServicesVO(UserServicesVO userServicesVO) {
        _userServicesVO = userServicesVO;
    }
}
