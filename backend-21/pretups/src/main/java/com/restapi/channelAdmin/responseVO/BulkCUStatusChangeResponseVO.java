package com.restapi.channelAdmin.responseVO;

import java.util.List;

import com.btsl.common.ErrorMap;

public class BulkCUStatusChangeResponseVO {
	 	private String messageCode;
	    private String message;
		private String status;
		private String fileName;
		private String fileAttachment;
		private long numberOfRecords = 0;
		private List<ErrorLog> errorLogs;
		
		public long getNumberOfRecords() {
			return numberOfRecords;
		}
		public void setNumberOfRecords(long numberOfRecords) {
			this.numberOfRecords = numberOfRecords;
		}
		
		public String getFileAttachment() {
			return fileAttachment;
		}
		public void setFileAttachment(String fileAttachment) {
			this.fileAttachment = fileAttachment;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getStatus() {
			return status;
		}


		public void setStatus(String txnstatus) {
			this.status = txnstatus;
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
		public List<ErrorLog> getErrorLogs() {
			return errorLogs;
		}
		public void setErrorLogs(List<ErrorLog> errorLogs) {
			this.errorLogs = errorLogs;
		}


		

		@Override
		public String toString() {
			StringBuffer sbf = new StringBuffer();
			sbf.append("BulkCUStatusChangeResponseVO [")
			.append("messageCode=" + messageCode)
			.append("message=" + message)
			.append("status=" + status)
			.append("fileName=" + fileName)
			.append("fileAttachment=" + fileAttachment)
			.append("errorLogs= "+errorLogs);
			return sbf.toString();
		}


		
}


	
	
	

