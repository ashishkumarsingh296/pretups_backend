
package restassuredapi.pojo.c2Ctransfercommissionreportresponsepojo;

import java.util.List;

public class C2CTransferCommissionReportResponsePojo {

    private List<C2CTransferCommission> c2ctransferCommissionList;
    private ErrorMap errorMap;
    private String message;
    private String messageCode;
    private Integer referenceId;
    private String service;
    private String status;
    private List<Success> successList;
    
	public List<C2CTransferCommission> getC2ctransferCommissionList() {
		return c2ctransferCommissionList;
	}
	public void setC2ctransferCommissionList(List<C2CTransferCommission> c2ctransferCommissionList) {
		this.c2ctransferCommissionList = c2ctransferCommissionList;
	}
	public ErrorMap getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(ErrorMap errorMap) {
		this.errorMap = errorMap;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public Integer getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Success> getSuccessList() {
		return successList;
	}
	public void setSuccessList(List<Success> successList) {
		this.successList = successList;
	}
  
}
