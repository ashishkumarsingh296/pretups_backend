package restassuredapi.pojo.c2sBulkReversalResponsePojo;

public class C2SBulkReversalResponsePojo {

	
	private Object fileType;
	private String fileName;
    private String fileattachment;
    private Object procStatus;
    private Object errorFlag;
    private int totalRecords;
    private int successRecords;
    private int rejectedRecords;
    private Object c2sreversalList;
    private Object fileErrorList;
    private int status;
    private String messageCode;
    private String message;
    private ErrorMap errorMap;
    
	public Object getFileType() {
		return fileType;
	}
	public void setFileType(Object fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileattachment() {
		return fileattachment;
	}
	public void setFileattachment(String fileattachment) {
		this.fileattachment = fileattachment;
	}
	public Object getProcStatus() {
		return procStatus;
	}
	public void setProcStatus(Object procStatus) {
		this.procStatus = procStatus;
	}
	public Object getErrorFlag() {
		return errorFlag;
	}
	public void setErrorFlag(Object errorFlag) {
		this.errorFlag = errorFlag;
	}
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public int getSuccessRecords() {
		return successRecords;
	}
	public void setSuccessRecords(int successRecords) {
		this.successRecords = successRecords;
	}
	public int getRejectedRecords() {
		return rejectedRecords;
	}
	public void setRejectedRecords(int rejectedRecords) {
		this.rejectedRecords = rejectedRecords;
	}
	public Object getC2sreversalList() {
		return c2sreversalList;
	}
	public void setC2sreversalList(Object c2sreversalList) {
		this.c2sreversalList = c2sreversalList;
	}
	public Object getFileErrorList() {
		return fileErrorList;
	}
	public void setFileErrorList(Object fileErrorList) {
		this.fileErrorList = fileErrorList;
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
	public ErrorMap getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(ErrorMap errorMap) {
		this.errorMap = errorMap;
	}
}
