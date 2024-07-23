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
@Table(name = "MESSAGE_GATEWAY")
public class MessageGateway implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "GATEWAY_CODE")
    private String gatewayCode;

    @Column(name = "GATEWAY_NAME")
    private String gatewayName;

    @Column(name = "GATEWAY_TYPE")
    private String gatewayType;

    @Column(name = "GATEWAY_SUBTYPE")
    private String gatewaySubtype;

    @Column(name = "PROTOCOL")
    private String protocol;

    @Column(name = "HANDLER_CLASS")
    private String handlerClass;

    @Column(name = "NETWORK_CODE")
    private String networkCode;

    @Column(name = "CREATED_ON")
    private Date createdON;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "HOST")
    private String host;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REQ_PASSWORD_PLAIN")
    private String reqPasswordPlain;

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getGatewayCode());
    }

    /**
     * Equals.
     *
     * @param obj
     *            the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MessageGateway other = (MessageGateway) obj;
        return Objects.equals(this.getGatewayCode(), other.getGatewayCode());
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

	public String getGatewaySubtype() {
		return gatewaySubtype;
	}

	public void setGatewaySubtype(String gatewaySubtype) {
		this.gatewaySubtype = gatewaySubtype;
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

	public Date getCreatedON() {
		return createdON;
	}

	public void setCreatedON(Date createdON) {
		this.createdON = createdON;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReqPasswordPlain() {
		return reqPasswordPlain;
	}

	public void setReqPasswordPlain(String reqPasswordPlain) {
		this.reqPasswordPlain = reqPasswordPlain;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
}
