package restassuredapi.pojo.bulkevdresponsepojo;

public class BulkEVDResponsePojo {

	private String messageCode;
	private String message;
	private ErrorMap errorMap;
	private String scheduleBatchId;
	private String status;
	private String fileName;
	private String fileAttachment;
	private int numberOfRecords;

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

	public String getScheduleBatchId() {
		return scheduleBatchId;
	}

	public void setScheduleBatchId(String scheduleBatchId) {
		this.scheduleBatchId = scheduleBatchId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileAttachment() {
		return fileAttachment;
	}

	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}

	public int getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

}
