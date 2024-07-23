package com.btsl.user.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.btsl.common.TypesI;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of ResMessageGatewayVO.
 *
 * @author Venkatesans
 */
@Setter
@Getter
public class ReqMessageGatewayVO implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String gatewayCode;
    private String port;
    private String servicePort;
    private String loginID;
    private String password;
    private String encryptionLevel;
    private String encryptionKey;
    private String contentType;
    private String authType;
    private String status;
    private String confirmPassword;
    private String oldPassword;
    private String decryptedPassword;
    private Date modifiedOn;
    private Timestamp modifiedOnTimestamp;
    private String modifiedBy;
    private Date createdOn;
    private String createdBy;
    private long lastModifiedTime;
    private String underProcessCheckReqd = TypesI.YES;
    private String updatePassword;
	public String getGatewayCode() {
		return gatewayCode;
	}
	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getServicePort() {
		return servicePort;
	}
	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEncryptionLevel() {
		return encryptionLevel;
	}
	public void setEncryptionLevel(String encryptionLevel) {
		this.encryptionLevel = encryptionLevel;
	}
	public String getEncryptionKey() {
		return encryptionKey;
	}
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getAuthType() {
		return authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getDecryptedPassword() {
		return decryptedPassword;
	}
	public void setDecryptedPassword(String decryptedPassword) {
		this.decryptedPassword = decryptedPassword;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public Timestamp getModifiedOnTimestamp() {
		return modifiedOnTimestamp;
	}
	public void setModifiedOnTimestamp(Timestamp modifiedOnTimestamp) {
		this.modifiedOnTimestamp = modifiedOnTimestamp;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public String getUnderProcessCheckReqd() {
		return underProcessCheckReqd;
	}
	public void setUnderProcessCheckReqd(String underProcessCheckReqd) {
		this.underProcessCheckReqd = underProcessCheckReqd;
	}
	public String getUpdatePassword() {
		return updatePassword;
	}
	public void setUpdatePassword(String updatePassword) {
		this.updatePassword = updatePassword;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
    
}
