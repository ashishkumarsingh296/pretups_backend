
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
/*import org.apache.commons.lang.builder.ToStringBuilder;*/

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "denominations",
    "segmentType",
    "segmentValue"
})
public class Segment {

    @JsonProperty("denominations")
    private List<String> denominations = null;
    @JsonProperty("segmentType")
    private String segmentType;
    @JsonProperty("segmentValue")
    private String segmentValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("denominations")
    public List<String> getDenominations() {
        return denominations;
    }

    @JsonProperty("denominations")
    public void setDenominations(List<String> denominations) {
        this.denominations = denominations;
    }

    @JsonProperty("segmentType")
    public String getSegmentType() {
        return segmentType;
    }

    @JsonProperty("segmentType")
    public void setSegmentType(String segmentType) {
        this.segmentType = segmentType;
    }

    @JsonProperty("segmentValue")
    public String getSegmentValue() {
        return segmentValue;
    }

    @JsonProperty("segmentValue")
    public void setSegmentValue(String segmentValue) {
        this.segmentValue = segmentValue;
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
        return new ToStringBuilder(this).append("denominations", denominations).append("segmentType", segmentType).append("segmentValue", segmentValue).append("additionalProperties", additionalProperties).toString();
    }
*/
}
