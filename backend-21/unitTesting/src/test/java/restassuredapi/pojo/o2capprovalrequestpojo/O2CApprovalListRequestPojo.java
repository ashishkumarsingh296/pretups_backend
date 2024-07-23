
package restassuredapi.pojo.o2capprovalrequestpojo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "approvalLevel",
    "category",
    "domain",
    "geographicalDomain",
    "msisdn"
})
public class O2CApprovalListRequestPojo {

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

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("approvalLevel", approvalLevel).append("category", category).append("domain", domain).append("geographicalDomain", geographicalDomain).append("msisdn", msisdn).append("additionalProperties", additionalProperties).toString();
    }

}
