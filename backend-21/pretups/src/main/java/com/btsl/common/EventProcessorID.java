package com.btsl.common;

public enum EventProcessorID {
	
	
	OFFLINE_101("OFFLINE_101"),OFFLINE_102("OFFLINE_102"),OFFLINE_103("OFFLINE_103");
	
	
	private String eventProcessorID;
	
	EventProcessorID(String eventProcessorID){
		this.eventProcessorID=eventProcessorID;
	}

	public String getEventProcessorID() {
		return eventProcessorID;
	}

	public void setEventProcessorID(String eventProcessorID) {
		this.eventProcessorID = eventProcessorID;
	}
	
	

}
