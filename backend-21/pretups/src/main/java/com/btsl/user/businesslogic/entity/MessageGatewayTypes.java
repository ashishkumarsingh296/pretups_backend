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
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "MESSAGE_GATEWAY_TYPES")
public class MessageGatewayTypes implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "GATEWAY_TYPE")
    private String gatewayType;

    @Column(name = "GATEWAY_TYPE_NAME")
    private String gatewayTypeName;

    @Column(name = "ACCESS_FROM")
    private String accessFrom;

    @Column(name = "PLAIN_MSG_ALLOWED")
    private String plainMsgAllowed;

    @Column(name = "BINARY_MSG_ALLOWED")
    private String binaryMsgAllowed;

    @Column(name = "FLOW_TYPE")
    private String flowType;

    @Column(name = "RESPONSE_TYPE")
    private String responsetype;

    @Column(name = "TIMEOUT_VALUE")
    private Long timeoutValue;

    @Column(name = "DISPLAY_ALLOWED")
    private String displayAllowed;

    @Column(name = "MODIFY_ALLOWED")
    private String modifyAllowed;

    @Column(name = "USER_AUTHORIZATION_REQD")
    private String userAuthorizationReqd;

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getGatewayType(), this.getGatewayTypeName());
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
        MessageGatewayTypes other = (MessageGatewayTypes) obj;
        return Objects.equals(this.getGatewayType(), other.getGatewayType())
                && Objects.equals(this.getGatewayTypeName(), other.getGatewayTypeName());
    }

	public String getGatewayType() {
		return gatewayType;
	}

	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}

	public String getGatewayTypeName() {
		return gatewayTypeName;
	}

	public void setGatewayTypeName(String gatewayTypeName) {
		this.gatewayTypeName = gatewayTypeName;
	}

	public String getAccessFrom() {
		return accessFrom;
	}

	public void setAccessFrom(String accessFrom) {
		this.accessFrom = accessFrom;
	}

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

	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	public String getResponsetype() {
		return responsetype;
	}

	public void setResponsetype(String responsetype) {
		this.responsetype = responsetype;
	}

	public Long getTimeoutValue() {
		return timeoutValue;
	}

	public void setTimeoutValue(Long timeoutValue) {
		this.timeoutValue = timeoutValue;
	}

	public String getDisplayAllowed() {
		return displayAllowed;
	}

	public void setDisplayAllowed(String displayAllowed) {
		this.displayAllowed = displayAllowed;
	}

	public String getModifyAllowed() {
		return modifyAllowed;
	}

	public void setModifyAllowed(String modifyAllowed) {
		this.modifyAllowed = modifyAllowed;
	}

	public String getUserAuthorizationReqd() {
		return userAuthorizationReqd;
	}

	public void setUserAuthorizationReqd(String userAuthorizationReqd) {
		this.userAuthorizationReqd = userAuthorizationReqd;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    

}
