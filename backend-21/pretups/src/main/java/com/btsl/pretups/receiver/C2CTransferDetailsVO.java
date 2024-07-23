package com.btsl.pretups.receiver;


public class C2CTransferDetailsVO {
	private String transferId = null; 
	private String networkCode = null;
	private String networkCodeFor = null;
	private String transferType = null;
	
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public String getTransferId() {
		return transferId;
	}
	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getNetworkCodeFor() {
		return networkCodeFor;
	}
	public void setNetworkCodeFor(String networkCodeFor) {
		this.networkCodeFor = networkCodeFor;
	}
	@Override
    public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("transferId:").append(transferId);
        strBuild.append("networkCode:").append(networkCode);
        strBuild.append("networkCodeFor:").append(networkCodeFor);
        strBuild.append("transferType:").append(transferType);
        return strBuild.toString();
	}
}
