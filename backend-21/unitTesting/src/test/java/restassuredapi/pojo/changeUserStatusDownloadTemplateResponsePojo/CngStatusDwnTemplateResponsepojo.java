package restassuredapi.pojo.changeUserStatusDownloadTemplateResponsePojo;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fileType",
    "fileName",
    "fileattachment",
    "status",
    "messageCode",
    "message",
    "errorMap"
})
@Generated("jsonschema2pojo")
public class CngStatusDwnTemplateResponsepojo {
	 @JsonProperty("fileType")
	    public String fileType;
	 @JsonProperty("fileName") 
	    public String fileName;
	 @JsonProperty("fileattachment") 
	    public String fileattachment;
	 @JsonProperty("status")
	    public int status;
	 @JsonProperty("messageCode")
	    public String messageCode;
	 @JsonProperty("message")  
	    public String message;
	 @JsonProperty("errorMap")
	    public Object errorMap;
	    
	    
	 @JsonProperty("fileType")
		public String getFileType() {
			return fileType;
		}
	 @JsonProperty("fileType")
		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
	 @JsonProperty("fileName")
		public String getFileName() {
			return fileName;
		}
	 @JsonProperty("fileName")
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	 @JsonProperty("fileattachment")
		public String getFileattachment() {
			return fileattachment;
		}
	 @JsonProperty("fileattachment")
		public void setFileattachment(String fileattachment) {
			this.fileattachment = fileattachment;
		}
	 @JsonProperty("status")
		public int getStatus() {
			return status;
		}
	 @JsonProperty("status")
		public void setStatus(int status) {
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
	 @JsonProperty("errorMap")
		public Object getErrorMap() {
			return errorMap;
		}
	 @JsonProperty("errorMap")
		public void setErrorMap(Object errorMap) {
			this.errorMap = errorMap;
		}
	    
	    
	    
	
}
