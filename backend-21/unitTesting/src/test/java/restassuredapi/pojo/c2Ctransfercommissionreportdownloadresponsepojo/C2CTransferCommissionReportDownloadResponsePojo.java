
package restassuredapi.pojo.c2Ctransfercommissionreportdownloadresponsepojo;

import java.util.List;

public class C2CTransferCommissionReportDownloadResponsePojo {

	private String service;
	private String referenceId;
	private String status;
	private String messageCode;
	private String message;
	private ErrorMap errorMap;
	private List<SuccessList> successList;
	private String fileName;
	private String fileType;
	private String fileData;
	private String totalRecords;

	public String getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
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

	public List<SuccessList> getSuccessList() {
		return successList;
	}

	public void setSuccessList(List<SuccessList> successList) {
		this.successList = successList;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

}
