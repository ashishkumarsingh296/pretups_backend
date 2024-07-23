package restassuredapi.pojo.bulko2capprovallistrequestpojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"approvalLevel",
"approvalType",
"category",
"domain",
"geographicalDomain"
})
public class BulkO2CApprovalListRequestPojo {

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
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("approvalLevel")
public String getApprovalLevel() {
return approvalLevel;
}

@JsonProperty("approvalLevel")
public void setApprovalLevel(String approvalLevel) {
this.approvalLevel = approvalLevel;
}

@JsonProperty("approvalType")
public String getApprovalType() {
return approvalType;
}

@JsonProperty("approvalType")
public void setApprovalType(String approvalType) {
this.approvalType = approvalType;
}

@JsonProperty("category")
public String getCategory() {
return category;
}

@JsonProperty("category")
public void setCategory(String category) {
this.category = category;
}

@JsonProperty("domain")
public String getDomain() {
return domain;
}

@JsonProperty("domain")
public void setDomain(String domain) {
this.domain = domain;
}

@JsonProperty("geographicalDomain")
public String getGeographicalDomain() {
return geographicalDomain;
}

@JsonProperty("geographicalDomain")
public void setGeographicalDomain(String geographicalDomain) {
this.geographicalDomain = geographicalDomain;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}