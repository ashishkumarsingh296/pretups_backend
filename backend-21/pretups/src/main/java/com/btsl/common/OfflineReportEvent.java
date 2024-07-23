package com.btsl.common;

import org.springframework.context.ApplicationEvent;

import com.btsl.pretups.channel.transfer.businesslogic.BaseRequestdata;

public class OfflineReportEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PretupsEventTypes eventType;
	
	

	public OfflineReportEvent(Object source, PretupsEventTypes eventType) {
		super(source);
		// TODO Auto-generated constructor stub
		this.eventType = eventType;
	

	}

	
	public PretupsEventTypes getEventType() {
		return eventType;
	}

	public void setEventType(PretupsEventTypes eventType) {
		this.eventType = eventType;
	}

	
}
