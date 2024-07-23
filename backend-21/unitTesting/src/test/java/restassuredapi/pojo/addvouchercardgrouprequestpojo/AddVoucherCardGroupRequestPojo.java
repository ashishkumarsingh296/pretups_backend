
package restassuredapi.pojo.addvouchercardgrouprequestpojo;

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
	"identifierType",
    "identifierValue",
    "data"
})
public class AddVoucherCardGroupRequestPojo {
	
	@JsonProperty("identifierType")
    private String identifierType;
    @JsonProperty("identifierValue")
    private String identifierValue;
	
	@JsonProperty("data")
    private Data data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
   
    @JsonProperty("identifierType")
    public String getIdentifierType() {
        return identifierType;
    }

    @JsonProperty("identifierType")
    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    @JsonProperty("identifierValue")
    public String getIdentifierValue() {
		return identifierValue;
	}

    @JsonProperty("identifierValue")
    public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}
    @JsonProperty("data")
    public Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Data data) {
        this.data = data;
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
