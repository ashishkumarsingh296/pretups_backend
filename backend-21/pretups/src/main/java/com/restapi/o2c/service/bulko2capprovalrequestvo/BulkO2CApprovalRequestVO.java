package com.restapi.o2c.service.bulko2capprovalrequestvo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"approvalLevel",
	"category",
	"domain",
	"geographicalDomain",
	"msisdn"
})
public class BulkO2CApprovalRequestVO {

	@JsonProperty("approvalLevel")
	private String approvalLevel;
	
	@JsonProperty("approvalType")
	private String approvalType;
	
	@JsonProperty("category")
	private String category;
	
	@JsonProperty("domain")
	private String domain;
	
	@JsonProperty("geographicalDomain")
	private String geographicalDomain;

	public String getApprovalLevel() {
		return approvalLevel;
	}

	public void setApprovalLevel(String approvalLevel) {
		this.approvalLevel = approvalLevel;
	}

	public String getApprovalType() {
		return approvalType;
	}

	public void setApprovalType(String approvalType) {
		this.approvalType = approvalType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getGeographicalDomain() {
		return geographicalDomain;
	}

	public void setGeographicalDomain(String geographicalDomain) {
		this.geographicalDomain = geographicalDomain;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BulkO2CApprovalRequestVO [approvalLevel=").append(approvalLevel).append(", approvalType=")
				.append(approvalType).append(", category=").append(category).append(", domain=").append(domain)
				.append(", geographicalDomain=").append(geographicalDomain).append("]");
		return builder.toString();
	}
	
	
}
