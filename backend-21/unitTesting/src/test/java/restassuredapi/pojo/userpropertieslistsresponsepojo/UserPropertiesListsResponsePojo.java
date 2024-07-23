package restassuredapi.pojo.userpropertieslistsresponsepojo;

import java.util.ArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "messageCode",
    "message",
    "Group Roles List",
    "Services List",
    "SMSC Profile List",
    "Document Type List",
    "System Roles List",
    "Payment Type List",
    "Geography List",
    "Voucher Types List",
    "Notification Language List"
})
public class UserPropertiesListsResponsePojo {
    @JsonProperty("status")
    private Long status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("status")
    public Long getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Long status) {
        this.status = status;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("Group Roles List")
    private ArrayList<GroupRoleTypeVO> groupRolesList = new ArrayList<GroupRoleTypeVO>();
	@JsonProperty("Services List")
    private ArrayList<ServiceVO> servicesList = new ArrayList<ServiceVO>();
    @JsonProperty("SMSC Profile List")
    private ArrayList<SMSCProfileVO> sMSCProfileList = new ArrayList<SMSCProfileVO>();
    @JsonProperty("Document Type List")
    private ArrayList<DocumentTypeVO> documentTypeList = new ArrayList<DocumentTypeVO>();
    @JsonProperty("System Roles List")
    private ArrayList<SystemRoleTypeVO> systemRolesList = new ArrayList<SystemRoleTypeVO>();
	@JsonProperty("Payment Type List")
    private ArrayList<PaymentTypeVO> paymentTypeList = new ArrayList<PaymentTypeVO>();
    @JsonProperty("Geography List")
    private ArrayList<GeographyVO> geographyList = new ArrayList<GeographyVO>();
    @JsonProperty("Voucher Types List")
    private ArrayList<VoucherTypeVO> voucherTypesList = new ArrayList<VoucherTypeVO>();
    @JsonProperty("Notification Language List")
    private ArrayList<LanguageVO> notificationLanguageList = new ArrayList<LanguageVO>();
    
    @JsonProperty("Group Roles List")
    public ArrayList<GroupRoleTypeVO> getGroupRolesList() {
		return groupRolesList;
	}

    @JsonProperty("Group Roles List")
	public void setGroupRolesList(ArrayList<GroupRoleTypeVO> groupRolesList) {
		this.groupRolesList = groupRolesList;
	}

    @JsonProperty("System Roles List")
    public ArrayList<SystemRoleTypeVO> getSystemRolesList() {
		return systemRolesList;
	}

    @JsonProperty("System Roles List")
	public void setSystemRolesList(ArrayList<SystemRoleTypeVO> systemRolesList) {
		this.systemRolesList = systemRolesList;
	}
    
    @JsonProperty("Services List")
    public ArrayList<ServiceVO> getServicesList() {
        return servicesList;
    }

    @JsonProperty("Services List")
    public void setServicesList(ArrayList<ServiceVO> servicesList) {
        this.servicesList = servicesList;
    }

    @JsonProperty("SMSC Profile List")
    public ArrayList<SMSCProfileVO> getSMSCProfile() {
        return sMSCProfileList;
    }

    @JsonProperty("SMSC Profile List")
    public void setSMSCProfileList(ArrayList<SMSCProfileVO> sMSCProfileList) {
        this.sMSCProfileList = sMSCProfileList;
    }

    @JsonProperty("Document Type List")
    public ArrayList<DocumentTypeVO> getDocumentTypeList() {
        return documentTypeList;
    }

    @JsonProperty("Document Type List")
    public void setDocumentTypeList(ArrayList<DocumentTypeVO> documentTypeList) {
        this.documentTypeList = documentTypeList;
    }


    @JsonProperty("Payment Type List")
    public ArrayList<PaymentTypeVO> getPaymentTypeList() {
        return paymentTypeList;
    }

    @JsonProperty("Payment Type List")
    public void setPaymentTypeList(ArrayList<PaymentTypeVO> paymentTypeList) {
        this.paymentTypeList = paymentTypeList;
    }

    @JsonProperty("Geography List")
    public ArrayList<GeographyVO> getGeographyList() {
        return geographyList;
    }

    @JsonProperty("Geography List")
    public void setGeographyList(ArrayList<GeographyVO> geographyList) {
        this.geographyList = geographyList;
    }

    
    public ArrayList<VoucherTypeVO> getVoucherTypesList() {
		return voucherTypesList;
	}

	public void setVoucherTypesList(ArrayList<VoucherTypeVO> voucherTypesList) {
		this.voucherTypesList = voucherTypesList;
	}

	
	public ArrayList<LanguageVO> getNotificationLanguageList() {
		return notificationLanguageList;
	}

	public void setNotificationLanguageList(
			ArrayList<LanguageVO> notificationLanguageList) {
		this.notificationLanguageList = notificationLanguageList;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this).append("groupRolesList", groupRolesList).append("servicesList", servicesList).append("sMSCProfileList", sMSCProfileList).append("documentTypeList", documentTypeList).append("systemRolesList", systemRolesList).append("paymentTypeList", paymentTypeList).append("geographyList", geographyList).append("voucherTypesList", voucherTypesList).append("notificationLanguageList", notificationLanguageList).toString();
    }
}


class SystemRoleTypeVO {

	@JsonProperty("System Role Type")
	private String systemRoleType = null;
    @JsonProperty("System Role List")
    private ArrayList<SystemRoleVO> systemRoleList = null;

    @JsonProperty("System Role List")
    public ArrayList<SystemRoleVO> getSystemRoleList() {
        return systemRoleList;
    }

    @JsonProperty("System Role List")
    public void setSystemRoleList(ArrayList<SystemRoleVO> systemRoleList) {
        this.systemRoleList = systemRoleList;
    }

    public String getSystemRoleType() {
		return systemRoleType;
	}

	public void setSystemRoleType(String systemRoleType) {
		this.systemRoleType = systemRoleType;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this).append("systemRoleType", systemRoleType).append("systemRoleList", systemRoleList).toString();
    }
}

class SystemRoleVO {

    @JsonProperty("defaultType")
    private String defaultType;
    @JsonProperty("status")
    private String status;
    @JsonProperty("roleCode")
    private String roleCode;
    @JsonProperty("roleType")
    private String roleType;
    @JsonProperty("roleName")
    private String roleName;
    @JsonProperty("domainType")
    private String domainType;

    @JsonProperty("defaultType")
    public String getDefaultType() {
        return defaultType;
    }

    @JsonProperty("defaultType")
    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("roleCode")
    public String getRoleCode() {
        return roleCode;
    }

    @JsonProperty("roleCode")
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @JsonProperty("roleType")
    public String getRoleType() {
        return roleType;
    }

    @JsonProperty("roleType")
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    @JsonProperty("roleName")
    public String getRoleName() {
        return roleName;
    }

    @JsonProperty("roleName")
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @JsonProperty("domainType")
    public String getDomainType() {
        return domainType;
    }

    @JsonProperty("domainType")
    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("defaultType", defaultType).append("status", status).append("roleCode", roleCode).append("roleType", roleType).append("roleName", roleName).append("domainType", domainType).toString();
    }

}

class SMSCProfileVO {

    @JsonProperty("smscProfileName")
    private String smscProfileName;
    @JsonProperty("smscProfileCode")
    private String smscProfileCode;

    @JsonProperty("smscProfileName")
    public String getSmscProfileName() {
        return smscProfileName;
    }

    @JsonProperty("smscProfileName")
    public void setSmscProfileName(String smscProfileName) {
        this.smscProfileName = smscProfileName;
    }

    @JsonProperty("smscProfileCode")
    public String getSmscProfileCode() {
        return smscProfileCode;
    }

    @JsonProperty("smscProfileCode")
    public void setSmscProfileCode(String smscProfileCode) {
        this.smscProfileCode = smscProfileCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("smscProfileName", smscProfileName).append("smscProfileCode", smscProfileCode).toString();
    }

}

class ServiceVO {

    @JsonProperty("serviceName")
    private String serviceName;
    @JsonProperty("serviceCode")
    private String serviceCode;

    @JsonProperty("serviceName")
    public String getServiceName() {
        return serviceName;
    }

    @JsonProperty("serviceName")
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @JsonProperty("serviceCode")
    public String getServiceCode() {
        return serviceCode;
    }

    @JsonProperty("serviceCode")
    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("serviceName", serviceName).append("serviceCode", serviceCode).toString();
    }

}

class PaymentTypeVO {

    @JsonProperty("paymentTypeName")
    private String paymentTypeName;
    @JsonProperty("paymentTypeCode")
    private String paymentTypeCode;

    @JsonProperty("paymentTypeName")
    public String getPaymentTypeName() {
        return paymentTypeName;
    }

    @JsonProperty("paymentTypeName")
    public void setPaymentTypeName(String paymentTypeName) {
        this.paymentTypeName = paymentTypeName;
    }

    @JsonProperty("paymentTypeCode")
    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }

    @JsonProperty("paymentTypeCode")
    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("paymentTypeName", paymentTypeName).append("paymentTypeCode", paymentTypeCode).toString();
    }

}


class GroupRoleTypeVO {

	@JsonProperty("Group Role Type")
	private String groupRoleType = null;
    @JsonProperty("Group Role List")
    private ArrayList<GroupRoleVO> groupRoleList = null;


    @JsonProperty("Group Role List")
    public ArrayList<GroupRoleVO> getGroupRoleList() {
		return groupRoleList;
	}

    @JsonProperty("Group Role List")
	public void setGroupRoleList(ArrayList<GroupRoleVO> groupRoleList) {
		this.groupRoleList = groupRoleList;
    }
    
	public String getGroupRoleType() {
		return groupRoleType;
	}

	public void setGroupRoleType(String groupRoleType) {
		this.groupRoleType = groupRoleType;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this).append("groupRoleType", groupRoleType).append("groupRoleList", groupRoleList).toString();
    }

}


class GroupRoleVO {
    @JsonProperty("status")
    private String status;
    @JsonProperty("roleCode")
    private String roleCode;
    @JsonProperty("roleName")
    private String roleName;
    @JsonProperty("groupName")
    private String groupName;

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("roleCode")
    public String getRoleCode() {
        return roleCode;
    }

    @JsonProperty("roleCode")
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @JsonProperty("roleName")
    public String getRoleName() {
        return roleName;
    }

    @JsonProperty("roleName")
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @JsonProperty("groupName")
    public String getGroupName() {
        return groupName;
    }

    @JsonProperty("groupName")
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("roleCode", roleCode).append("roleName", roleName).append("groupName", groupName).toString();
    }

}

class GeographyVO {

    @JsonProperty("graphDomainCode")
    private String graphDomainCode;
    @JsonProperty("graphDomainName")
    private String graphDomainName;
    @JsonProperty("graphDomainTypeName")
    private String graphDomainTypeName;
    @JsonProperty("parentGraphDomainCode")
    private String parentGraphDomainCode;

    @JsonProperty("graphDomainCode")
    public String getGraphDomainCode() {
        return graphDomainCode;
    }

    @JsonProperty("graphDomainCode")
    public void setGraphDomainCode(String graphDomainCode) {
        this.graphDomainCode = graphDomainCode;
    }

    @JsonProperty("graphDomainName")
    public String getGraphDomainName() {
        return graphDomainName;
    }

    @JsonProperty("graphDomainName")
    public void setGraphDomainName(String graphDomainName) {
        this.graphDomainName = graphDomainName;
    }

    @JsonProperty("graphDomainTypeName")
    public String getGraphDomainTypeName() {
        return graphDomainTypeName;
    }

    @JsonProperty("graphDomainTypeName")
    public void setGraphDomainTypeName(String graphDomainTypeName) {
        this.graphDomainTypeName = graphDomainTypeName;
    }

    @JsonProperty("parentGraphDomainCode")
    public String getParentGraphDomainCode() {
        return parentGraphDomainCode;
    }

    @JsonProperty("parentGraphDomainCode")
    public void setParentGraphDomainCode(String parentGraphDomainCode) {
        this.parentGraphDomainCode = parentGraphDomainCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("graphDomainCode", graphDomainCode).append("graphDomainName", graphDomainName).append("graphDomainTypeName", graphDomainTypeName).append("parentGraphDomainCode", parentGraphDomainCode).toString();
    }

}

class DocumentTypeVO {

    @JsonProperty("documentName")
    private String documentName;
    @JsonProperty("documentCode")
    private String documentCode;

    @JsonProperty("documentName")
    public String getDocumentName() {
        return documentName;
    }

    @JsonProperty("documentName")
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    @JsonProperty("documentCode")
    public String getDocumentCode() {
        return documentCode;
    }

    @JsonProperty("documentCode")
    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("documentName", documentName).append("documentCode", documentCode).toString();
    }

}

class VoucherTypeVO {

    @JsonProperty("voucherName")
    private String voucherName;
    @JsonProperty("voucherCode")
    private String voucherCode;

    public String getVoucherName() {
		return voucherName;
	}

	public void setVoucherName(String voucherName) {
		this.voucherName = voucherName;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this).append("voucherName", voucherName).append("voucherCode", voucherCode).toString();
    }
}

class LanguageVO {
    @JsonProperty("languageName")
    private String languageName;
    @JsonProperty("languageCode")
    private String languageCode;
    
	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this).append("languageName", languageName).append("languageCode", languageCode).toString();
    }
}