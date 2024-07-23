package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class AutoC2CSOSUpdateResponseVO extends BaseResponse {
	
	ArrayList<String> successMessages;

	public ArrayList<String> getSuccessMessages() {
		return successMessages;
	}

	public void setSuccessMessages(ArrayList<String> successMessages) {
		this.successMessages = successMessages;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AutoC2CSOSResponseVO [successMessages=");
		builder.append(successMessages);
		builder.append("]");
		return builder.toString();
	}
	
	

}
