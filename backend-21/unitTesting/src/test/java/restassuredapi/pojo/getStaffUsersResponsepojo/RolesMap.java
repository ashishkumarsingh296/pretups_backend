
package restassuredapi.pojo.getStaffUsersResponsepojo;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "additionalProp1",
    "additionalProp2",
    "additionalProp3"
})
@Generated("jsonschema2pojo")
public class RolesMap {

    @JsonProperty("additionalProp1")
    private AdditionalProp1__1 additionalProp1;
    @JsonProperty("additionalProp2")
    private AdditionalProp2__1 additionalProp2;
    @JsonProperty("additionalProp3")
    private AdditionalProp3__1 additionalProp3;

    @JsonProperty("additionalProp1")
    public AdditionalProp1__1 getAdditionalProp1() {
        return additionalProp1;
    }

    @JsonProperty("additionalProp1")
    public void setAdditionalProp1(AdditionalProp1__1 additionalProp1) {
        this.additionalProp1 = additionalProp1;
    }

    @JsonProperty("additionalProp2")
    public AdditionalProp2__1 getAdditionalProp2() {
        return additionalProp2;
    }

    @JsonProperty("additionalProp2")
    public void setAdditionalProp2(AdditionalProp2__1 additionalProp2) {
        this.additionalProp2 = additionalProp2;
    }

    @JsonProperty("additionalProp3")
    public AdditionalProp3__1 getAdditionalProp3() {
        return additionalProp3;
    }

    @JsonProperty("additionalProp3")
    public void setAdditionalProp3(AdditionalProp3__1 additionalProp3) {
        this.additionalProp3 = additionalProp3;
    }

}
