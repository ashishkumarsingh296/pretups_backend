package com.btsl.pretups.channeluser.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class AddChannelUserRequestVO {

	
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(required =true)
	ChannelUserDetails data;

	@JsonProperty("data")
	public ChannelUserDetails getData() {
		return data;
	}

	public void setData(ChannelUserDetails data) {
		this.data = data;
	}


}



