package com.btsl.pretups.master.businesslogic;

import java.io.Serializable;

public class ServiceInstancePriorityVO implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String _serviceType;
    private String _instanceID;
    private int _priority;
    private long _requestTimeout;

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String type) {
        _serviceType = type;
    }

    public String getInstanceID() {
        return _instanceID;
    }

    public void setInstanceID(String _instanceid) {
        _instanceID = _instanceid;
    }

    public int getPriority() {
        return _priority;
    }

    public void setPriority(int _priority) {
        this._priority = _priority;
    }

    public long getRequestTimeout() {
        return _requestTimeout;
    }

    public void setRequestTimeout(long _timeout) {
        this._requestTimeout = _timeout;
    }
}
