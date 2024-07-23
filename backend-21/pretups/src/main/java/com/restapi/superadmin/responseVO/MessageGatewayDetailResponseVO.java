package com.restapi.superadmin.responseVO;
import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class MessageGatewayDetailResponseVO extends BaseResponse{
	
	private ArrayList _handlerClassList = null;
	private String _handlerClassDescription;
	
	public String getHandlerClassDescription() {
        return _handlerClassDescription;
    }

    public void setHandlerClassDescription(String handlerClassDescription) {
        _handlerClassDescription = handlerClassDescription;
    }
    
    public ArrayList getHandlerClassList() {
        return _handlerClassList;
    }

    public void setHandlerClassList(ArrayList handlerClassList) {
        _handlerClassList = handlerClassList;
    }

}
