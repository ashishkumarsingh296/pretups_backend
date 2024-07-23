package com.btsl.pretups.channel.transfer.businesslogic;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionalDataResponseVO extends BaseResponse{
	
	@JsonProperty("InTransactionData")
	private TransactionalData inTransactionData;
	
	@JsonProperty("OutTransactionData")
	private TransactionalData outTransactionData;

	@JsonProperty("InTransactionData")
	public TransactionalData getInTransactionData() {
		return inTransactionData;
	}
	@JsonProperty("InTransactionData")
	public void setInTransactionData(TransactionalData inTransactionData) {
		this.inTransactionData = inTransactionData;
	}

	@JsonProperty("OutTransactionData")
	public TransactionalData getOutTransactionData() {
		return outTransactionData;
	}

	@JsonProperty("OutTransactionData")
	public void setOutTransactionData(TransactionalData outTransactionData) {
		this.outTransactionData = outTransactionData;
	}
	
	

}
