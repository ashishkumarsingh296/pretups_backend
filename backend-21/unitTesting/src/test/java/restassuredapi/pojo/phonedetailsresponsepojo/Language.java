
package restassuredapi.pojo.phonedetailsresponsepojo;

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
    "languageCode",
    "languageName",
    "languageCountry",
    "country"
})
@Generated("jsonschema2pojo")
public class Language {

    @JsonProperty("languageCode")
    private String languageCode;
    @JsonProperty("languageName")
    private String languageName;
    @JsonProperty("languageCountry")
    private String languageCountry;
    @JsonProperty("country")
    private String country;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("languageCode")
    public String getLanguageCode() {
        return languageCode;
    }

    @JsonProperty("languageCode")
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @JsonProperty("languageName")
    public String getLanguageName() {
        return languageName;
    }

    @JsonProperty("languageName")
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    @JsonProperty("languageCountry")
    public String getLanguageCountry() {
        return languageCountry;
    }

    @JsonProperty("languageCountry")
    public void setLanguageCountry(String languageCountry) {
        this.languageCountry = languageCountry;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
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
