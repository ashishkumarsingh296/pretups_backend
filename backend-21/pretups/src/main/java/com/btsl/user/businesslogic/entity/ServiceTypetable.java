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
 * Entity of USER_SERVICES.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "SERVICE_TYPE")
public class ServiceTypetable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @Column(name = "MODULE")
    private String module;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "MESSAGE_FORMAT")
    private String messageFormat;

    @Column(name = "REQUEST_HANDLER")
    private String requestHandler;

    @Column(name = "ERROR_KEY")
    private String errorKey;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "FLEXIBLE")
    private String flexible;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EXTERNAL_INTERFACE")
    private String externalInterface;

    @Column(name = "UNREGISTERED_ACCESS_ALLOWED")
    private String unregisteredAccessAllowed;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "SEQ_NO")
    private Long seqNo;

    @Column(name = "USE_INTERFACE_LANGUAGE")
    private String useInterfaceLanguage;

    @Column(name = "GROUP_TYPE")
    private String groupType;

    @Column(name = "SUB_KEYWORD_APPLICABLE")
    private String subKeywordApplicable;

    @Column(name = "FILE_PARSER")
    private String fileParser;

    @Column(name = "ERP_HANDLER")
    private String erpHandler;

    @Column(name = "RECEIVER_USER_SERVICE_CHECK")
    private String receiverUserServiceCheck;

    @Column(name = "RESPONSE_PARAM")
    private String responseParam;

    @Column(name = "REQUEST_PARAM")
    private String requestParam;

    @Column(name = "UNDERPROCESS_CHECK_REQD")
    private String underprocessCheckReqd;

    @Override
    public int hashCode() {
        return Objects.hash(this.getServiceType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ServiceTypetable other = (ServiceTypetable) obj;
        return Objects.equals(this.getServiceType(), other.getServiceType());
    }

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	public String getRequestHandler() {
		return requestHandler;
	}

	public void setRequestHandler(String requestHandler) {
		this.requestHandler = requestHandler;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFlexible() {
		return flexible;
	}

	public void setFlexible(String flexible) {
		this.flexible = flexible;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalInterface() {
		return externalInterface;
	}

	public void setExternalInterface(String externalInterface) {
		this.externalInterface = externalInterface;
	}

	public String getUnregisteredAccessAllowed() {
		return unregisteredAccessAllowed;
	}

	public void setUnregisteredAccessAllowed(String unregisteredAccessAllowed) {
		this.unregisteredAccessAllowed = unregisteredAccessAllowed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public String getUseInterfaceLanguage() {
		return useInterfaceLanguage;
	}

	public void setUseInterfaceLanguage(String useInterfaceLanguage) {
		this.useInterfaceLanguage = useInterfaceLanguage;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getSubKeywordApplicable() {
		return subKeywordApplicable;
	}

	public void setSubKeywordApplicable(String subKeywordApplicable) {
		this.subKeywordApplicable = subKeywordApplicable;
	}

	public String getFileParser() {
		return fileParser;
	}

	public void setFileParser(String fileParser) {
		this.fileParser = fileParser;
	}

	public String getErpHandler() {
		return erpHandler;
	}

	public void setErpHandler(String erpHandler) {
		this.erpHandler = erpHandler;
	}

	public String getReceiverUserServiceCheck() {
		return receiverUserServiceCheck;
	}

	public void setReceiverUserServiceCheck(String receiverUserServiceCheck) {
		this.receiverUserServiceCheck = receiverUserServiceCheck;
	}

	public String getResponseParam() {
		return responseParam;
	}

	public void setResponseParam(String responseParam) {
		this.responseParam = responseParam;
	}

	public String getRequestParam() {
		return requestParam;
	}

	public void setRequestParam(String requestParam) {
		this.requestParam = requestParam;
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
