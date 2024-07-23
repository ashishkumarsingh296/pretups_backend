
package restassuredapi.pojo.getvoucherinforesponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "value",
    "displayValue",
    "segment"
})
public class DataObject {

    @JsonProperty("value")
    private String value;
    @JsonProperty("displayValue")
    private Object displayValue;
    @JsonProperty("segment")
    private List<Segment> segment = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("displayValue")
    public Object getDisplayValue() {
        return displayValue;
    }

    @JsonProperty("displayValue")
    public void setDisplayValue(Object displayValue) {
        this.displayValue = displayValue;
    }

    @JsonProperty("segment")
    public List<Segment> getSegment() {
        return segment;
    }

    @JsonProperty("segment")
    public void setSegment(List<Segment> segment) {
        this.segment = segment;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /*@Override
    public String toString() {
        return new ToStringBuilder(this).append("value", value).append("displayValue", displayValue).append("segment", segment).append("additionalProperties", additionalProperties).toString();
    }
*/
}
