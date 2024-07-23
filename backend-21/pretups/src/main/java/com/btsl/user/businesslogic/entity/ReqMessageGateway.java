package com.btsl.user.businesslogic.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of MESSAGE_GATEWAY.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "req_message_gateway")
public class ReqMessageGateway implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "gateway_code")
    private String gatewayCode;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "password")
    private String password;
    
    @Column(name = "port")
    private String port;

    @Column(name = "service_port")
    private String servicePort;

    @Column(name = "encryption_level")
    private String encryptionLevel;

    @Column(name = "encryption_key")
    private String encryptionKey;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "auth_type")
    private String authType;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "modified_on")
    private Date modifiedOn;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "underprocess_check_reqd")
    private String underprocessCheckReqd;

    @Override
    public int hashCode() {
        return Objects.hash(this.getGatewayCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ReqMessageGateway other = (ReqMessageGateway) obj;
        return Objects.equals(this.getGatewayCode(), other.getGatewayCode());
    }

	public String getGatewayCode() {
		return gatewayCode;
	}

	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getUnderprocessCheckReqd() {
		return underprocessCheckReqd;
	}

	public void setUnderprocessCheckReqd(String underprocessCheckReqd) {
		this.underprocessCheckReqd = underprocessCheckReqd;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
    
}
