package com.btsl.user.businesslogic.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of MESSAGE_GATEWAY_TYPES.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "MESSAGE_REQ_RESP_MAPPING")
public class MessageReqRespMapping implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "REQ_CODE")
    private String reqCode;

    @Column(name = "RES_CODE")
    private String resCode;

    @Column(name = "ALT_CODE")
    private String altCode;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Override
    public int hashCode() {
        return Objects.hash(this.getReqCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MessageReqRespMapping other = (MessageReqRespMapping) obj;
        return Objects.equals(this.getReqCode(), other.getReqCode());
    }

	public String getReqCode() {
		return reqCode;
	}

	public void setReqCode(String reqCode) {
		this.reqCode = reqCode;
	}

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getAltCode() {
		return altCode;
	}

	public void setAltCode(String altCode) {
		this.altCode = altCode;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
    
}
