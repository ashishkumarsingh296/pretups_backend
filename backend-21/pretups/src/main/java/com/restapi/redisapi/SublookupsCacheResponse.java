package com.restapi.redisapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;

public class SublookupsCacheResponse extends BaseResponse {
	HashMap map;

	public HashMap getMap() {
		return map;
	}

	public void setMap(HashMap map) {
		this.map = map;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SublookupsCacheResponse [map=");
		builder.append(map);
		builder.append("]");
		return builder.toString();
	}	
	
	

}
