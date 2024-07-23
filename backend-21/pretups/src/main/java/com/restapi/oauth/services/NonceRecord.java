package com.restapi.oauth.services;


import lombok.Data;

import jakarta.persistence.*;
import java.util.Date;

import static jakarta.persistence.TemporalType.TIMESTAMP;

@Entity
@Table(name = "NONCE_RECORD")
@Data
public class NonceRecord {

    @Id
    @Column(name = "NONCE_ID")
    private String nonceId;

    @Temporal(TIMESTAMP)
    @Column(name = "CREATED_ON")
    private Date createdOn;

	public String getNonceId() {
		return nonceId;
	}

	public void setNonceId(String nonceId) {
		this.nonceId = nonceId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

    
}
