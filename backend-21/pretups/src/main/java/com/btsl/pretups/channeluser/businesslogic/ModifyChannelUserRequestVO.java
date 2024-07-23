package com.btsl.pretups.channeluser.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ModifyChannelUserRequestVO {


	
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(required =true)
	ModifyChannelUserDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public ModifyChannelUserDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(ModifyChannelUserDetails data) {
		this.data = data;
	}




}
