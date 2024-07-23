package com.restapi.superadmin;

import com.fasterxml.jackson.annotation.JsonProperty;



public class DeleteOperatorRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "userId", required = true)
	@JsonProperty("userId")
	private String userId;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "lastModified", required = true)
	@JsonProperty("lastModified")
	private String lastModified;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "status", required = true)
	@JsonProperty("status")
	private String status;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "id", required = true)
	@JsonProperty("id")
	private String id;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "userName", required = true)
	@JsonProperty("userName")
	private String userName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeleteOperatorRequestVO [userId=");
		builder.append(userId);
		builder.append(", lastModified=");
		builder.append(lastModified);
		builder.append(", status=");
		builder.append(status);
		builder.append(", id=");
		builder.append(id);
		builder.append(", userName=");
		builder.append(userName);
		builder.append("]");
		return builder.toString();
	}
	
}
