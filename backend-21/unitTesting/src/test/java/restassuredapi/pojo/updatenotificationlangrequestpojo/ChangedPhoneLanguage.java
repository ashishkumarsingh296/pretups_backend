
package restassuredapi.pojo.updatenotificationlangrequestpojo;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "country",
    "languageCode",
    "userMsisdn"
})
@Generated("jsonschema2pojo")
public class ChangedPhoneLanguage {

    @JsonProperty("country")
    private String country;
    @JsonProperty("languageCode")
    private String languageCode;
    @JsonProperty("userMsisdn")
    private String userMsisdn;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("languageCode")
    public String getLanguageCode() {
        return languageCode;
    }

    @JsonProperty("languageCode")
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @JsonProperty("userMsisdn")
    public String getUserMsisdn() {
        return userMsisdn;
    }

    @JsonProperty("userMsisdn")
    public void setUserMsisdn(String userMsisdn) {
        this.userMsisdn = userMsisdn;
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
