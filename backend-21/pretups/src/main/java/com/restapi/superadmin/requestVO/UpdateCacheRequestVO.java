package com.restapi.superadmin.requestVO;

import java.util.ArrayList;



public class UpdateCacheRequestVO {
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "[BONUSBUNDLE, CARDGROUP]", required = true, description="Cache Codes")
	private String[] cacheList;
	@io.swagger.v3.oas.annotations.media.Schema(example = "[1,12]", required = true, description="Instance ID")
	private String[] instanceList;
	
	public String[] getCacheList() {
		return cacheList;
	}

	public void setCacheList(String[] cacheList) {
		this.cacheList = cacheList;
	}

	public String[] getInstanceList() {
		return instanceList;
	}

	public void setInstanceList(String[] instanceList) {
		this.instanceList = instanceList;
	}

	@Override
	public String toString() {
		return "UpdateCacheRequestVO [cacheList=" + cacheList + ", instanceList=" + instanceList + "]";
	}
}
