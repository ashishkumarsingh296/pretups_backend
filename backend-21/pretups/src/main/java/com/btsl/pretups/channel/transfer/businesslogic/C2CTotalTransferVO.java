package com.btsl.pretups.channel.transfer.businesslogic;

public class C2CTotalTransferVO {
	
	private String serviceType;
	private String serviceName;
	private String currentFrom;
	private String currentTo;
	private String currentValue;
	private String previousFrom;
	private String previousTo;
	private String previousValue;
	
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getCurrentFrom() {
		return currentFrom;
	}
	public void setCurrentFrom(String currentFrom) {
		this.currentFrom = currentFrom;
	}
	public String getCurrentTo() {
		return currentTo;
	}
	public void setCurrentTo(String currentTo) {
		this.currentTo = currentTo;
	}
	public String getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	public String getPreviousFrom() {
		return previousFrom;
	}
	public void setPreviousFrom(String previousFrom) {
		this.previousFrom = previousFrom;
	}
	public String getPreviousTo() {
		return previousTo;
	}
	public void setPreviousTo(String previousTo) {
		this.previousTo = previousTo;
	}
	public String getPreviousValue() {
		return previousValue;
	}
	public void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("C2CTotalTransferVO [serviceType=");
		sb.append(serviceType);
		sb.append(", serviceName=");
		sb.append(serviceName);
		sb.append(", currentFrom=");
		sb.append(currentFrom);
		sb.append(", currentTo=");
		sb.append(currentTo);
		sb.append(", currentValue=");
		sb.append(currentValue);
		sb.append(", previousFrom=");
		sb.append(previousFrom);
		sb.append(", previousTo=");
		sb.append(previousTo);
		sb.append( ", previousValue=");
		sb.append(previousValue);
		return sb.toString();
		}

	
	
	
}
