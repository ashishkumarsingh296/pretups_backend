package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User Hierarchy Request Wrapper class
 * @author akhilesh.mittal1
 *
 */


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class C2SAllTransactionDetailViewRequestVO {

	@JsonProperty("data")
	C2SAllTransactionRequest data;
	
	@JsonProperty("data")
	public C2SAllTransactionRequest getData() {
		return data;
	}

	public void setData(C2SAllTransactionRequest data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "C2SAllTransactionDetailViewRequestVO [data=" + data + "]";
	}

}
