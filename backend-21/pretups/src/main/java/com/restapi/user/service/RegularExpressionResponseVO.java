package com.restapi.user.service;

import java.util.HashMap;

import com.btsl.common.BaseResponseMultiple;

public class RegularExpressionResponseVO extends BaseResponseMultiple {

	
	HashMap<String, Object> regMap = new HashMap<>();

	public HashMap<String, Object> getRegMap() {
		return regMap;
	}

	public void setRegMap(HashMap<String, Object> regMap) {
		this.regMap = regMap;
	}
}
