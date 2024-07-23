
package com.btsl.pretups.channel.transfer.businesslogic;

public class O2CTransfAckDownloadReqDTO extends CommonDownloadReqDTO {

	private String distributionType; // This is not used any more...
	private String transactionID;
	
	
 		public String getDistributionType() {
		return distributionType;
	}

	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	
		@Override
		public String toString() {
			return "O2CTransfAckDownloadReqDTO [distributionType=" + distributionType + ",transactionID=" + transactionID + "fileType=" + fileType + "]";
		}

    

}
