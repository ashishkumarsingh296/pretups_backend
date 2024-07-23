package com.btsl.user.businesslogic.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of MessagesMaster.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "messages_master")
public class MessagesMaster implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "message_type")
    private String messageType;

    @Id
    @Column(name = "message_code")
    private String messageCode;

    @Column(name = "default_message")
    private String defaultMessage;

    @Column(name = "network_code")
    private String networkCode;

    @Column(name = "message1")
    private String message1;

    @Column(name = "message2")
    private String message2;

    @Column(name = "message3")
    private String message3;

    @Column(name = "message4")
    private String message4;

    @Column(name = "message5")
    private String message5;

    @Column(name = "mclass")
    private String mclass;

    @Column(name = "description")
    private String description;

    @Override
    public int hashCode() {
        return Objects.hash(this.getMessageCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MessagesMaster other = (MessagesMaster) obj;
        return Objects.equals(this.getMessageCode(), other.getMessageCode());
    }

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getMessage1() {
		return message1;
	}

	public void setMessage1(String message1) {
		this.message1 = message1;
	}

	public String getMessage2() {
		return message2;
	}

	public void setMessage2(String message2) {
		this.message2 = message2;
	}

	public String getMessage3() {
		return message3;
	}

	public void setMessage3(String message3) {
		this.message3 = message3;
	}

	public String getMessage4() {
		return message4;
	}

	public void setMessage4(String message4) {
		this.message4 = message4;
	}

	public String getMessage5() {
		return message5;
	}

	public void setMessage5(String message5) {
		this.message5 = message5;
	}

	public String getMclass() {
		return mclass;
	}

	public void setMclass(String mclass) {
		this.mclass = mclass;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}
