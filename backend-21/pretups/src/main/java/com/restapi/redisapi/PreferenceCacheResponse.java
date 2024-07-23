package com.restapi.redisapi;

import java.util.Map;

import com.btsl.common.BaseResponse;

public class PreferenceCacheResponse extends BaseResponse {
	Map map;

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PreferenceCacheResponse [map=");
		builder.append(map);
		builder.append("]");
		return builder.toString();
	}	
	
	


}
