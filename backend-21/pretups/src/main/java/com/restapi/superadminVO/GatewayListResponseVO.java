package com.restapi.superadminVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class GatewayListResponseVO extends BaseResponse{

	private ArrayList _gatewayTypeList = null;
    private ArrayList _gatewaySubTypeList = null;
    private String classHandler;
    
    
    

	public String getClassHandler() {
		return classHandler;
	}

	public void setClassHandler(String classHandler) {
		this.classHandler = classHandler;
	}

	public ArrayList getGatewayTypeList() {
        return _gatewayTypeList;
    }

    public void setGatewayTypeList(ArrayList gatewayTypeList) {
        _gatewayTypeList = gatewayTypeList;
    }
    
    public ArrayList getGatewaySubTypeList() {
        return _gatewaySubTypeList;
    }

    public void setGatewaySubTypeList(ArrayList gatewaySubTypeList) {
        _gatewaySubTypeList = gatewaySubTypeList;
    }
    
}
