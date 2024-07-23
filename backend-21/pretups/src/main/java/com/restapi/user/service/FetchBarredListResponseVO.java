package com.restapi.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Generated;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"barredList"
})
@Generated("jsonschema2pojo")
	public class FetchBarredListResponseVO extends BaseResponseMultiple {
	
	@JsonProperty("barredList")
	private HashMap<String,ArrayList<BarredVo>> barredList = null;
	
	@JsonProperty("barredList")
	public HashMap<String,ArrayList<BarredVo>> getBarredList() {
	return barredList;
	}
	
	@JsonProperty("barredList")
	public void setBarredList(HashMap<String,ArrayList<BarredVo>> barredList) {
	this.barredList = barredList;
	}

	@Override
	public String toString() {
		return "FetchBarredListResponseVO [barredList=" + barredList + "]";
	}
	
}