package com.restapi.o2c.service;

import org.apache.commons.lang.builder.ToStringBuilder;

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
public class O2CApprovalListRequestVO {

	@JsonProperty("approvalLevel")
	private String approvalLevel;
    @JsonProperty("category")
    private String category;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("geographicalDomain")
    private String geographicalDomain;
    @JsonProperty("msisdn")
    private String msisdn;
	public String getApprovalLevel() {
		return approvalLevel;
	}
	public void setApprovalLevel(String approvalLevel) {
		this.approvalLevel = approvalLevel;
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
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	@Override
    public String toString() {
        return new ToStringBuilder(this).append("approvalLevel", approvalLevel)
        		.append("category", category)
        		.append("domain", domain)
        		.append("geographicalDomain", geographicalDomain)
        		.append("msisdn", msisdn)
        		.toString();
    }
}
