package com.btsl.user.businesslogic;

import java.util.HashMap;
import java.util.Map;



import lombok.Getter;
import lombok.Setter;

/**
 * GenerateVoucherInitiateResp Model
 * 
 * @author Subesh.KCV
 *
 */
@Setter
@Getter
public class SysPreferenceResp extends BaseResponse {
    private  String moduleCode;
    private Map<String,String>  sysPrefMap = new HashMap<>();
	public String getModuleCode() {
		return moduleCode;
	}
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
	public Map<String, String> getSysPrefMap() {
		return sysPrefMap;
	}
	public void setSysPrefMap(Map<String, String> sysPrefMap) {
		this.sysPrefMap = sysPrefMap;
	}

    
}
