package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User Hierarchy Request Wrapper class
 * @author piyush.bansal
 *
 */


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class O2CChannelUserStockDetailRequestVO {

	@JsonProperty("data")
	O2CChannelUserStockRequest data;
	
	@JsonProperty("data")
	public O2CChannelUserStockRequest getData() {
		return data;
	}

	public void setData(O2CChannelUserStockRequest data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "O2CChannelUserStockDetailRequestVO [data=" + data + "]";
	}

}
