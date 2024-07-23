/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import java.io.Serializable;


import lombok.Getter;
import lombok.Setter;

/**
 * Get MessageGateway model
 * 
 * @author VENKATESAN.S
 */
@Getter
@Setter
public class MessageGatewayInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

   
    private String plainMsgAllowed;
    private String binaryMsgAllowed;
    private String gatewaySubTypeName;
    private String accessFrom;
    private String flowType;
    private String responseType;
    private long timeoutValue;
    /**
     * this field is to keep the last modified time for the transaction control
     * during the transaction
     */
    private String gatewayCode;
    private String gatewayName;
    private String gatewayType;
    private String gatewaySubType;
    private String host;
    private String protocol;
    private String handlerClass;
    private String networkCode;
    private String createdOn;
   
    private String status;
    private boolean userAuthorizationReqd = true;
    private String reqpaswrdtype = "Y";
    private String categoryCode;
    
    private ReqMessageGatewayVO reqMessageGatewayVO;
    private ResMessageGatewayVO resMessageGatewayVO;
    private ResMessageGatewayVO alternateGatewayVO;
    private String createdBy;
    private String modifiedOn;
    private String modifiedOnTimestamp;
    private String modifiedBy;
    private long lastModifiedTime;
    private String gatewayTypeDes;
    private String gatewaySubTypeDes;
	public String getPlainMsgAllowed() {
		return plainMsgAllowed;
	}
	public void setPlainMsgAllowed(String plainMsgAllowed) {
		this.plainMsgAllowed = plainMsgAllowed;
	}
	public String getBinaryMsgAllowed() {
		return binaryMsgAllowed;
	}
	public void setBinaryMsgAllowed(String binaryMsgAllowed) {
		this.binaryMsgAllowed = binaryMsgAllowed;
	}
	public String getGatewaySubTypeName() {
		return gatewaySubTypeName;
	}
	public void setGatewaySubTypeName(String gatewaySubTypeName) {
		this.gatewaySubTypeName = gatewaySubTypeName;
	}
	public String getAccessFrom() {
		return accessFrom;
	}
	public void setAccessFrom(String accessFrom) {
		this.accessFrom = accessFrom;
	}
	public String getFlowType() {
		return flowType;
	}
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public long getTimeoutValue() {
		return timeoutValue;
	}
	public void setTimeoutValue(long timeoutValue) {
		this.timeoutValue = timeoutValue;
	}
	public String getGatewayCode() {
		return gatewayCode;
	}
	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getGatewayType() {
		return gatewayType;
	}
	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}
	public String getGatewaySubType() {
		return gatewaySubType;
	}
	public void setGatewaySubType(String gatewaySubType) {
		this.gatewaySubType = gatewaySubType;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getHandlerClass() {
		return handlerClass;
	}
	public void setHandlerClass(String handlerClass) {
		this.handlerClass = handlerClass;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isUserAuthorizationReqd() {
		return userAuthorizationReqd;
	}
	public void setUserAuthorizationReqd(boolean userAuthorizationReqd) {
		this.userAuthorizationReqd = userAuthorizationReqd;
	}
	public String getReqpaswrdtype() {
		return reqpaswrdtype;
	}
	public void setReqpaswrdtype(String reqpaswrdtype) {
		this.reqpaswrdtype = reqpaswrdtype;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getModifiedOnTimestamp() {
		return modifiedOnTimestamp;
	}
	public void setModifiedOnTimestamp(String modifiedOnTimestamp) {
		this.modifiedOnTimestamp = modifiedOnTimestamp;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public String getGatewayTypeDes() {
		return gatewayTypeDes;
	}
	public void setGatewayTypeDes(String gatewayTypeDes) {
		this.gatewayTypeDes = gatewayTypeDes;
	}
	public String getGatewaySubTypeDes() {
		return gatewaySubTypeDes;
	}
	public void setGatewaySubTypeDes(String gatewaySubTypeDes) {
		this.gatewaySubTypeDes = gatewaySubTypeDes;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public ReqMessageGatewayVO getReqMessageGatewayVO() {
		return reqMessageGatewayVO;
	}
	public void setReqMessageGatewayVO(ReqMessageGatewayVO reqMessageGatewayVO) {
		this.reqMessageGatewayVO = reqMessageGatewayVO;
	}
	public ResMessageGatewayVO getResMessageGatewayVO() {
		return resMessageGatewayVO;
	}
	public void setResMessageGatewayVO(ResMessageGatewayVO resMessageGatewayVO) {
		this.resMessageGatewayVO = resMessageGatewayVO;
	}
	public ResMessageGatewayVO getAlternateGatewayVO() {
		return alternateGatewayVO;
	}
	public void setAlternateGatewayVO(ResMessageGatewayVO alternateGatewayVO) {
		this.alternateGatewayVO = alternateGatewayVO;
	}
    
    
   

}
