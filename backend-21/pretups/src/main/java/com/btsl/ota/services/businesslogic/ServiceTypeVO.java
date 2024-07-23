/*
 * #ServiceTypeVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * August ,2011 Ankur Dhawan Initial Creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.ota.services.businesslogic;

import java.io.Serializable;

public class ServiceTypeVO implements Runnable,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _serviceType;
	private String _serviceName;
	private String _type;

    /**
     * @return _serviceType
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * set _servicetype
     * 
     * @param serviceType
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return _serviceName
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * set _serviceName
     * 
     * @param serviceName
     */
    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }

    /**
     * @return _type
     */
    public String getType() {
        return _type;
    }

    /**
     * Set _type
     * 
     * @param type
     */
    public void setType(String type) {
        _type = type;
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
