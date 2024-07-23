package com.btsl.user.businesslogic;

public class ReportsTemplate {

	private String rptId;
    private String rptDesc;
    private String jsonTemplate;
	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}


	public String getJsonTemplate() {
		return jsonTemplate;
	}
	public void setJsonTemplate(String jsonTemplate) {
		this.jsonTemplate = jsonTemplate;
	}
	public String getRptId() {
		return rptId;
	}
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}
	public String getRptDesc() {
		return rptDesc;
	}
	public void setRptDesc(String rptDesc) {
		this.rptDesc = rptDesc;
	}
    
    
    
}
