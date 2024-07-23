package com.restapi.preferences.responseVO;

import com.btsl.common.BaseResponse;

import java.util.List;
public class ApiConfigurationResponseVO extends BaseResponse {
	private List<String> apiConfigurationList;
	public List<String> getApiConfigurationList() {
		return apiConfigurationList;
	}
	public void setApiConfigurationList(List<String> apiConfigurationList) {
		this.apiConfigurationList = apiConfigurationList;
	}
	@Override
	public String toString() {
		return "ApiConfigurationResponseVO [apiConfigurationList=" + apiConfigurationList + "]";
	}

}
