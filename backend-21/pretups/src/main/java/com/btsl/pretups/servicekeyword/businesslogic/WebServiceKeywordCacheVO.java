/**
* @(#)ServiceKeywordCacheVO.java
* Copyright(c) 2005, Bharti Telesoft Ltd.
* All Rights Reserved
* 
* <description>
*-------------------------------------------------------------------------------------------------
* Author                        Date            History
*-------------------------------------------------------------------------------------------------
* avinash.kamthan                       Mar 16, 2005         Initital Creation
*-------------------------------------------------------------------------------------------------
*
*/

package com.btsl.pretups.servicekeyword.businesslogic;

import java.io.Serializable;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author avinash.kamthan
 *
 */
public class WebServiceKeywordCacheVO implements Serializable{

     private String _webSeviceKeyword;
     private String _resourceName;
     private String _validatorName;
     private String _messageResource;
     private String _beanName;
     private String _requestHandlerClass;


     private String _serviceUrl;
     private String _isRBARequired;
     private String _isDataValidationRequired;
     private String roleCode;
	 
     public WebServiceKeywordCacheVO(){
         super();
     }
     
     public WebServiceKeywordCacheVO(WebServiceKeywordCacheVO cache){
         _webSeviceKeyword = cache.getWebSeviceKeyword();
         _resourceName = cache.getResourceName();
         _validatorName = cache.getValidatorName();
         _messageResource = cache.getMessageResource();
         _beanName = cache.getBeanName();
         _requestHandlerClass = cache.getRequestHandlerClass();


         _serviceUrl = cache.getServiceUrl();
         _isRBARequired = cache.getIsRBARequired();
         _isDataValidationRequired = cache.getIsDataValidationRequired();
     }
     
    public String getIsDataValidationRequired() {
		return _isDataValidationRequired;
	}

	public void setIsDataValidationRequired(String _isDataValidationRequired) {
		this._isDataValidationRequired = _isDataValidationRequired;
	}

	public String getWebSeviceKeyword() {
        return _webSeviceKeyword;
    }
    public void setWebSeviceKeyword(String webSeviceKeyword) {
        _webSeviceKeyword = webSeviceKeyword;
    }
    public String getBeanName() {
        return _beanName;
    }
    public void setBeanName(String beanName) {
        _beanName = beanName;
    }
    public String getRequestHandlerClass() {
        return _requestHandlerClass;
    }
    public void setRequestHandlerClass(String requestHandlerClass) {
        _requestHandlerClass = requestHandlerClass;
    }
    public String getResourceName() {
        return _resourceName;
    }
    public void setResourceName(String resourceName) {
        _resourceName = resourceName;
    }
    public String getValidatorName() {
        return _validatorName;
    }
    public void setValidatorName(String validatorName) {
        _validatorName = validatorName;
    }
    public String getMessageResource() {
        return _messageResource;
    }
    public void setMessageResource(String messageResource) {
        _messageResource = messageResource;
    }
     
      
    public String getServiceUrl() {
		return _serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this._serviceUrl = serviceUrl;
	}

	public String getIsRBARequired() {
		return _isRBARequired;
	}

	public void setIsRBARequired(String isRBARequired) {
		this._isRBARequired = isRBARequired;
	}

	public String getServiceTypeKeyword(){
            StringBuffer sbf = new StringBuffer();

            sbf.append(this.getMessageResource());
            sbf.append("_");
            sbf.append(this.getWebSeviceKeyword());
            
            return sbf.toString();
    }
	
	
    
     
    public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String toString(){
        
        StringBuffer sbf = new StringBuffer(200);

        sbf.append(" webSeviceKeyword  "+_webSeviceKeyword);
        sbf.append(" module  "+_beanName);
        sbf.append(" _resourceName  "+_resourceName);
        sbf.append(" _validatorName  "+_validatorName);
        sbf.append(" _messageResource  "+_messageResource);
        sbf.append(" resourceName  "+_resourceName);
        sbf.append(" validatorName  "+_validatorName);
        sbf.append(" messageResource  "+_messageResource);
        sbf.append(" requestHandlerClass  "+_requestHandlerClass);
        sbf.append(" serviceUrl  "+_serviceUrl);
        sbf.append(" isRBARequired  "+_isRBARequired);
        return sbf.toString();
    }
     

	@Override
    public native int hashCode();

	public String differences(ServiceKeywordCacheVO p_serviceCacheVO){
        
        StringBuffer sbf = new StringBuffer(400);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        if(!BTSLUtil.isNullString(this.getMessageResource())&& ! this.getMessageResource().equals(p_serviceCacheVO.getServiceType())){
            sbf.append(startSeperator);
            sbf.append("Service Type");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getServiceType());
            sbf.append(middleSeperator);
            sbf.append(this.getMessageResource());
        }
        
        if(!BTSLUtil.isNullString(this.getRequestHandlerClass())&& ! this.getRequestHandlerClass().equals(p_serviceCacheVO.getRequestHandlerClass())){
            sbf.append(startSeperator);
            sbf.append("Request Handler Class");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getRequestHandlerClass());
            sbf.append(middleSeperator);
            sbf.append(this.getRequestHandlerClass());
        }
		 
        
        return sbf.toString();        
    }

	

	@Override
	public native boolean equals(Object obj);


}
