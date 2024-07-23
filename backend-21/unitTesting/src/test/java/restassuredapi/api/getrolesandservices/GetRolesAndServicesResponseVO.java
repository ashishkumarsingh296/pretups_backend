package restassuredapi.api.getrolesandservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import restassuredapi.pojo.c2CFileUploadApiResponsepojo.ErrorMap;
import restassuredapi.pojo.c2ctransferstockresponsepojo.BaseResponse;

public class GetRolesAndServicesResponseVO {

	Map systemRole = new LinkedHashMap<>();
	Map groupRole = new HashMap<>();
	List<String> servicesList = new ArrayList<String>();
	List<LocaleMasterVO> languagesList = new ArrayList<LocaleMasterVO>();

	public List<LocaleMasterVO> getLanguagesList() {
		return languagesList;
	}

	public void setLanguagesList(List<LocaleMasterVO> languagesList) {
		this.languagesList = languagesList;
	}

	List<String> voucherList = new ArrayList<String>();
	public List<String> getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(List<String> voucherList) {
		this.voucherList = voucherList;
	}

	public List<String> getServicesList() {
		return servicesList;
	}

	public void setServicesList(List<String> serviceTypeList) {
		this.servicesList = serviceTypeList;
	}

	
	public Map getSystemRole() {
		return systemRole;
	}

	public void setSystemRole(Map systemRole) {
		this.systemRole = systemRole;
	}

	public Map getGroupRole() {
		return groupRole;
	}

	public void setGroupRole(Map groupRole) {
		this.groupRole = groupRole;
	}

	

	


    private String service;
    private Integer referenceId;
    private String status;
    private String messageCode;
    private String message;
    private ErrorMap errorMap;
    private ArrayList<BaseResponse> successList = new ArrayList();

	public ArrayList<BaseResponse> getSuccessList() {
		return successList;
	}

	public void setSuccessList(ArrayList<BaseResponse> successList) {
		this.successList = successList;
	}

	public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public ErrorMap getErrorMap() {
        return errorMap;
    }

    public void setErrorMap(ErrorMap errorMap) {
        this.errorMap = errorMap;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("service = ").append(service)
        		.append("referenceId").append( referenceId)
        		.append("status").append( status)
        		.append("messageCode").append( messageCode)
        		.append("message").append( message)
        		.append("errorMap").append( errorMap)
        		).toString();
    }


}
