
package restassuredapi.pojo.loadreversallistresponsepojo;

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
    "codeName",
    "label",
    "labelWithValue",
    "value"
})
public class VoucherDenomList {

    @JsonProperty("codeName")
    private String codeName;
    @JsonProperty("label")
    private String label;
    @JsonProperty("labelWithValue")
    private String labelWithValue;
    @JsonProperty("value")
    private String value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("codeName")
    public String getCodeName() {
        return codeName;
    }

    @JsonProperty("codeName")
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("labelWithValue")
    public String getLabelWithValue() {
        return labelWithValue;
    }

    @JsonProperty("labelWithValue")
    public void setLabelWithValue(String labelWithValue) {
        this.labelWithValue = labelWithValue;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
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
