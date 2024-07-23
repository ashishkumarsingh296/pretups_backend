package com.btsl.user.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of ResMessageGatewayVO.
 *
 * @author Venkatesans
 */
@Setter
@Getter
public class ResMessageGatewayVO implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String gatewayCode;
    private String port;
    private String servicePort;
    private String loginID;
    private String password;
    private String destNo;
    private String status;
    private String confirmPassword;
    private String oldPassword;
    private Date modifiedOn;
    private Timestamp modifiedOnTimestamp;
    private String modifiedBy;
    private Date createdOn;
    private String createdBy;
    private String decryptedPassword;
    private String path;
    private int timeOut;
    private long lastModifiedTime;
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
	public String getDestNo() {
		return destNo;
	}
	public void setDestNo(String destNo) {
		this.destNo = destNo;
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
	public String getDecryptedPassword() {
		return decryptedPassword;
	}
	public void setDecryptedPassword(String decryptedPassword) {
		this.decryptedPassword = decryptedPassword;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
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
