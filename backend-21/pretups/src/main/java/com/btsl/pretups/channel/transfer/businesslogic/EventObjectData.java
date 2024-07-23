
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Locale;

/**
 * 
 * @author Subesh KCV This class contains eventprocesingID and request Data , It can be a report request object Or It can be
 * Bussiness event Request */

public class EventObjectData {

	private String eventProcessingID;  // This ID is from Master table, like Report_master or Event_master
	private String eventProcessBeanName;// No need to set this attribute, This will added by the offline service process
	private Object requestData; // This can be either Report request object nor Business Event Request Object
	private String fileExtension;
	private String fileName;
	private String eventInitiatedBy;
	private String process_taskID;  // This is from OFFLINE_REPORT_PROCESS TABLE PRIMARY KEY ID
	
	public void EventObjectData(String eventProcessingID,Object requestData ) {
		this.eventProcessingID=eventProcessingID;
		this.requestData=requestData;
	}

	public String getEventProcessingID() {
		return eventProcessingID;
	}

	public void setEventProcessingID(String eventProcessingID) {
		this.eventProcessingID = eventProcessingID;
	}

	public Object getRequestData() {
		return requestData;
	}

	public void setRequestData(Object requestData) {
		this.requestData = requestData;
	}

	public String getEventProcessBeanName() {
		return eventProcessBeanName;
	}

	public void setEventProcessBeanName(String eventProcessBeanName) {
		this.eventProcessBeanName = eventProcessBeanName;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getEventInitiatedBy() {
		return eventInitiatedBy;
	}

	public void setEventInitiatedBy(String eventInitiatedBy) {
		this.eventInitiatedBy = eventInitiatedBy;
	}

	public String getProcess_taskID() {
		return process_taskID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setProcess_taskID(String process_taskID) {
		this.process_taskID = process_taskID;
	}

}
