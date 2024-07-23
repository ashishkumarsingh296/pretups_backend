package com.restapi.user.service;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(name = "COMMAND")
@XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
public class VoucherConResponseVO {


    @io.swagger.v3.oas.annotations.media.Schema(description = "External Reference Id : Reference Id from Request originator", required = false/* , position = 1 */, example = "WEB01.001")
    @XmlElement(name = "EXTREFNUM")
    protected String externalRefId;

    /** The status. */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Response Status", required = true/* , position = 2 */, example = "200")
    protected int status;

    /** The message code. */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Message Code", required = true/* , position = 3 */, example = "25052")
    @XmlElement(name = "TXNSTATUS")
    protected String messageCode;

    /** The message. */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Response Message", required = true/* , position = 4 */, example = "Success")
    @XmlElement(name = "MESSAGE")
    protected String message;
    
    /** The txnid. */
    @io.swagger.v3.oas.annotations.media.Schema(description = "Transaction ID", required = true/* , position = 5 */, example = "C970418.1454.150001")
    @XmlElement(name = "TXNID")
    protected String txnid;

	public String getExternalRefId() {
		return externalRefId;
	}

	public void setExternalRefId(String externalRefId) {
		this.externalRefId = externalRefId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTxnid() {
		return txnid;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

	@Override
	public String toString() {
		return "VoucherConResponseVO [externalRefId=" + externalRefId + ", status=" + status + ", messageCode="
				+ messageCode + ", message=" + message + ", txnid=" + txnid + "]";
	}

    
    
    
	
}
