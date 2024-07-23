package com.restapi.superadmin;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ModifyDivisionRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "OPERATOR", required = true)
	@JsonProperty("divDeptType")
	private String divDeptType;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DIVID00609", required = true)
	@JsonProperty("divDeptId")
	private String divDeptId;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTDIV0611", required = true)
	@JsonProperty("divDeptShortCode")
	private String divDeptShortCode;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Active", required = true)
	@JsonProperty("statusName")
	private String statusName;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTDIV06114", required = true)
	@JsonProperty("divDeptName")
	private String divDeptName;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true)
	@JsonProperty("status")
	private String status;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DIVID00609", required = true)
	@JsonProperty("parentId")
	private String parentId;

	public String getDivDeptType() {
		return divDeptType;
	}

	public void setDivDeptType(String divDeptType) {
		this.divDeptType = divDeptType;
	}

	public String getDivDeptId() {
		return divDeptId;
	}

	public void setDivDeptId(String divDeptId) {
		this.divDeptId = divDeptId;
	}

	public String getDivDeptShortCode() {
		return divDeptShortCode;
	}

	public void setDivDeptShortCode(String divDeptShortCode) {
		this.divDeptShortCode = divDeptShortCode;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getDivDeptName() {
		return divDeptName;
	}

	public void setDivDeptName(String divDeptName) {
		this.divDeptName = divDeptName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModifyDivisionRequestVO [divDeptType=");
		builder.append(divDeptType);
		builder.append(", divDeptId=");
		builder.append(divDeptId);
		builder.append(", divDeptShortCode=");
		builder.append(divDeptShortCode);
		builder.append(", statusName=");
		builder.append(statusName);
		builder.append(", divDeptName=");
		builder.append(divDeptName);
		builder.append(", status=");
		builder.append(status);
		builder.append(", parentId=");
		builder.append(parentId);
		builder.append("]");
		return builder.toString();
	}

}
