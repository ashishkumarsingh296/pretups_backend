package com.btsl.user.businesslogic;

import java.util.ArrayList;

public class CommonReportRequest {

	private String download;
	private String fileType;
	private String report_template;
	private ArrayList<Param> params;
	private String activePanelId;
	private String language;
	private String country;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getActivePanelId() {
		return activePanelId;
	}
	public void setActivePanelId(String activePanelId) {
		this.activePanelId = activePanelId;
	}
	public String getDownload() {
		return download;
	}
	public void setDownload(String download) {
		this.download = download;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getReport_template() {
		return report_template;
	}
	public void setReport_template(String report_template) {
		this.report_template = report_template;
	}
	public ArrayList<Param> getParams() {
		return params;
	}
	public void setParams(ArrayList<Param> params) {
		this.params = params;
	}
    
    
}
