package com.restapi.networkadmin.networkinterfaces;

public class NetworkInterfaceRequestVO {
    
    private String networkInterfaceId = null;
	private String interfaceCategoryID = null;
    private String interfaceID = null;
    private String queueSize = null;
    private String queueTimeOut = null;
    private String requestTimeOut = null;
    private String quereyRetryInterval = null;
    private long lastModifiedOn;
    
	public String getInterfaceCategoryID() {
		return interfaceCategoryID;
	}
	public void setInterfaceCategoryID(String interfaceCategoryID) {
		this.interfaceCategoryID = interfaceCategoryID;
	}
	public String getInterfaceID() {
		return interfaceID;
	}
	public void setInterfaceID(String interfaceID) {
		this.interfaceID = interfaceID;
	}
	public String getQueueSize() {
		return queueSize;
	}
	public void setQueueSize(String queueSize) {
		this.queueSize = queueSize;
	}
	public String getQueueTimeOut() {
		return queueTimeOut;
	}
	public void setQueueTimeOut(String queueTimeOut) {
		this.queueTimeOut = queueTimeOut;
	}
	public String getRequestTimeOut() {
		return requestTimeOut;
	}
	public void setRequestTimeOut(String requestTimeOut) {
		this.requestTimeOut = requestTimeOut;
	}
	public String getQuereyRetryInterval() {
		return quereyRetryInterval;
	}
	public void setQuereyRetryInterval(String quereyRetryInterval) {
		this.quereyRetryInterval = quereyRetryInterval;
	}
	public String getNetworkInterfaceId() {
		return networkInterfaceId;
	}
	public void setNetworkInterfaceId(String networkInterfaceId) {
		this.networkInterfaceId = networkInterfaceId;
	}
	public long getLastModifiedOn() {
		return lastModifiedOn;
	}
	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
	
	
    

}
