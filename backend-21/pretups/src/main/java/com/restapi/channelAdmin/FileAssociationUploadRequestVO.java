package com.restapi.channelAdmin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAssociationUploadRequestVO {
    public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
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
	private String fileType;
    private String fileName;
    private String fileAttachment;
    private String categoryCode;
    public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getGradeCode() {
		return gradeCode;
	}
	public void setGradeCode(String gradeCode) {
		this.gradeCode = gradeCode;
	}
	public String getGeographyCode() {
		return geographyCode;
	}
	public void setGeographyCode(String geographyCode) {
		this.geographyCode = geographyCode;
	}
	public String getSetId() {
		return setId;
	}
	public void setSetId(String setId) {
		this.setId = setId;
	}
	private String domainCode;
    private String gradeCode;
    private String geographyCode;
    private String setId;
}
