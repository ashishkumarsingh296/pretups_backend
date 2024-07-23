package com.restapi.superadminVO;

import java.io.Serializable;

import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;

public class AddMessGatewayVO{
	
	private MessGatewayVO _messGatewayVO;
	
	private String _reqDetailCheckbox;
    private String _pushDetailCheckbox;
    // fields for disable/enable the check box
    private boolean _reqDetailDisable = false;
    private boolean _pushDetailDisable = false;
    
 // this field is to take the value of timeout and perform validation.
    private String _timeOut;
    
    private String _handlerClassDescription;

	
 //added for update Message gateway starts ***
    private String _gatewayCode;// field to get the selected gateway form the
    								// list of the all gateways of the specified
    								// network.
    	// changes done for updating messagegateway password during hashing
    	// implementaion.
    private String _updatePassword = "N";
    
  //added for update Message gateway ends ***
    
	public MessGatewayVO getMessGatewayVO() {
        return _messGatewayVO;
    }

    public void setMessGatewayVO(MessGatewayVO messGatewayVO) {
        _messGatewayVO = messGatewayVO;
    }
    
    public String getPushDetailCheckbox() {
        return _pushDetailCheckbox;
    }

    public void setPushDetailCheckbox(String pushDetailCheckbox) {
        _pushDetailCheckbox = pushDetailCheckbox;
    }

    public String getReqDetailCheckbox() {
        return _reqDetailCheckbox;
    }

    public void setReqDetailCheckbox(String reqDetailCheckbox) {
        _reqDetailCheckbox = reqDetailCheckbox;
    }
    
    
    public void setPushDetailDisable(boolean pushDetailDisable) {
        _pushDetailDisable = pushDetailDisable;
    }

    public void setReqDetailDisable(boolean reqDetailDisable) {
        _reqDetailDisable = reqDetailDisable;
    }

    public boolean getPushDetailDisable() {
        return _pushDetailDisable;
    }

    public boolean getReqDetailDisable() {
        return _reqDetailDisable;
    }
    
    public String getTimeOut() {
        return _timeOut;
    }

    public void setTimeOut(String timeOut) {
        _timeOut = timeOut;
    }
    
    public String getHandlerClassDescription() {
        return _handlerClassDescription;
    }

    public void setHandlerClassDescription(String handlerClassDescription) {
        _handlerClassDescription = handlerClassDescription;
    }
    
    
    public String getGatewayCode() {
        return _gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        _gatewayCode = gatewayCode;
    }
    
    public String getUpdatePassword() {
        return _updatePassword;
    }

    public void setUpdatePassword(String password) {
        _updatePassword = password;
    }
    
}
