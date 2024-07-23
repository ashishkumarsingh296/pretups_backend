package com.restapi.superadmin.responseVO;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;


public class UpdateCategoryOnlyResp extends BaseResponse{

	boolean agentExistUnderCategory;
	boolean agentAllowedTicked;
	@JsonProperty("warning")
	String warning;

	public boolean isAgentExistUnderCategory() {
		return agentExistUnderCategory;
	}

	public void setAgentExistUnderCategory(boolean agentExistUnderCategory) {
		this.agentExistUnderCategory = agentExistUnderCategory;
	}

	public boolean isAgentAllowedTicked() {
		return agentAllowedTicked;
	}

	public void setAgentAllowedTicked(boolean agentAllowedTicked) {
		this.agentAllowedTicked = agentAllowedTicked;
	}

	@JsonProperty("warning")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Success")
	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}
}
