package com.restapi.superadmin;

import java.util.Date;

public class DivisionVO {
	
	    private String divDeptId;
	    private String divDeptName;
	    private String divDeptShortCode;
	    private String status;
	    private String statusName;
	    private String divDeptType;
	    private String divDeptTypeName;
	    private String divDept;
	    private Date createdOn;
	    private String createdBy;
	    private Date modifiedOn;
	    private String modifiedBy;
	    private String parentId;
	    private String networkCode;
	    private String networkName;
	    private String userId;
	    private long lastModified;
	    private int radioIndex;
		public String getDivDeptId() {
			return divDeptId;
		}
		public void setDivDeptId(String divDeptId) {
			this.divDeptId = divDeptId;
		}
		public String getDivDeptName() {
			return divDeptName;
		}
		public void setDivDeptName(String divDeptName) {
			this.divDeptName = divDeptName;
		}
		public String getDivDeptShortCode() {
			return divDeptShortCode;
		}
		public void setDivDeptShortCode(String divDeptShortCode) {
			this.divDeptShortCode = divDeptShortCode;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getStatusName() {
			return statusName;
		}
		public void setStatusName(String statusName) {
			this.statusName = statusName;
		}
		public String getDivDeptType() {
			return divDeptType;
		}
		public void setDivDeptType(String divDeptType) {
			this.divDeptType = divDeptType;
		}
		public String getDivDeptTypeName() {
			return divDeptTypeName;
		}
		public void setDivDeptTypeName(String divDeptTypeName) {
			this.divDeptTypeName = divDeptTypeName;
		}
		public String getDivDept() {
			return divDept;
		}
		public void setDivDept(String divDept) {
			this.divDept = divDept;
		}
		public Date getCreatedOn() {
			return createdOn;
		}
		public void setCreatedOn(Date createdOn) {
			this.createdOn = createdOn;
		}
		public String getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}
		public Date getModifiedOn() {
			return modifiedOn;
		}
		public void setModifiedOn(Date modifiedOn) {
			this.modifiedOn = modifiedOn;
		}
		public String getModifiedBy() {
			return modifiedBy;
		}
		public void setModifiedBy(String modifiedBy) {
			this.modifiedBy = modifiedBy;
		}
		public String getParentId() {
			return parentId;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		public String getNetworkCode() {
			return networkCode;
		}
		public void setNetworkCode(String networkCode) {
			this.networkCode = networkCode;
		}
		public String getNetworkName() {
			return networkName;
		}
		public void setNetworkName(String networkName) {
			this.networkName = networkName;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public long getLastModified() {
			return lastModified;
		}
		public void setLastModified(long lastModified) {
			this.lastModified = lastModified;
		}
		public int getRadioIndex() {
			return radioIndex;
		}
		public void setRadioIndex(int radioIndex) {
			this.radioIndex = radioIndex;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DivisionVO [divDeptId=");
			builder.append(divDeptId);
			builder.append(", divDeptName=");
			builder.append(divDeptName);
			builder.append(", divDeptShortCode=");
			builder.append(divDeptShortCode);
			builder.append(", status=");
			builder.append(status);
			builder.append(", statusName=");
			builder.append(statusName);
			builder.append(", divDeptType=");
			builder.append(divDeptType);
			builder.append(", divDeptTypeName=");
			builder.append(divDeptTypeName);
			builder.append(", divDept=");
			builder.append(divDept);
			builder.append(", createdOn=");
			builder.append(createdOn);
			builder.append(", createdBy=");
			builder.append(createdBy);
			builder.append(", modifiedOn=");
			builder.append(modifiedOn);
			builder.append(", modifiedBy=");
			builder.append(modifiedBy);
			builder.append(", parentId=");
			builder.append(parentId);
			builder.append(", networkCode=");
			builder.append(networkCode);
			builder.append(", networkName=");
			builder.append(networkName);
			builder.append(", userId=");
			builder.append(userId);
			builder.append(", lastModified=");
			builder.append(lastModified);
			builder.append(", radioIndex=");
			builder.append(radioIndex);
			builder.append("]");
			return builder.toString();
		}
	    

}
