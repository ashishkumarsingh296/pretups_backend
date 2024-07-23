package com.btsl.user.businesslogic;

import java.util.ArrayList;

public class ReportTemplatesResponse {

	public ArrayList<ReportsTemplate> reportsTemplates;

	private String encodedBody;
	
	
	
	public String getEncodedBody() {
		return encodedBody;
	}

	public void setEncodedBody(String encodedBody) {
		this.encodedBody = encodedBody;
	}

	public ArrayList<ReportsTemplate> getReportsTemplates() {
		return reportsTemplates;
	}

	public void setReportsTemplates(ArrayList<ReportsTemplate> reportsTemplates) {
		this.reportsTemplates = reportsTemplates;
	}
	
	
	
}
