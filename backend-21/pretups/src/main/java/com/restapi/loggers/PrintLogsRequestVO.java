package com.restapi.loggers;

import java.util.ArrayList;

public class PrintLogsRequestVO {

	ArrayList<LogVO> logData;

	public ArrayList<LogVO> getLogData() {
		return logData;
	}

	public void setLogData(ArrayList<LogVO> logData) {
		this.logData = logData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PrintLogsRequestVO [logData=");
		builder.append(logData);
		builder.append("]");
		return builder.toString();
	}
	
}


