
package restassuredapi.pojo.o2ctxnrevlistresponsepojo;

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
    "addnl",
    "adnlComOTFDetailId",
    "baseComOTFDetailId",
    "commType",
    "otfCount",
    "otfValue",
    "updateRecord",
    "userID"
})
@Generated("jsonschema2pojo")
public class UserOTFCountsVO__1 {

    @JsonProperty("addnl")
    private Boolean addnl;
    @JsonProperty("adnlComOTFDetailId")
    private String adnlComOTFDetailId;
    @JsonProperty("baseComOTFDetailId")
    private String baseComOTFDetailId;
    @JsonProperty("commType")
    private String commType;
    @JsonProperty("otfCount")
    private Integer otfCount;
    @JsonProperty("otfValue")
    private Integer otfValue;
    @JsonProperty("updateRecord")
    private Boolean updateRecord;
    @JsonProperty("userID")
    private String userID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("addnl")
    public Boolean getAddnl() {
        return addnl;
    }

    @JsonProperty("addnl")
    public void setAddnl(Boolean addnl) {
        this.addnl = addnl;
    }

    @JsonProperty("adnlComOTFDetailId")
    public String getAdnlComOTFDetailId() {
        return adnlComOTFDetailId;
    }

    @JsonProperty("adnlComOTFDetailId")
    public void setAdnlComOTFDetailId(String adnlComOTFDetailId) {
        this.adnlComOTFDetailId = adnlComOTFDetailId;
    }

    @JsonProperty("baseComOTFDetailId")
    public String getBaseComOTFDetailId() {
        return baseComOTFDetailId;
    }

    @JsonProperty("baseComOTFDetailId")
    public void setBaseComOTFDetailId(String baseComOTFDetailId) {
        this.baseComOTFDetailId = baseComOTFDetailId;
    }

    @JsonProperty("commType")
    public String getCommType() {
        return commType;
    }

    @JsonProperty("commType")
    public void setCommType(String commType) {
        this.commType = commType;
    }

    @JsonProperty("otfCount")
    public Integer getOtfCount() {
        return otfCount;
    }

    @JsonProperty("otfCount")
    public void setOtfCount(Integer otfCount) {
        this.otfCount = otfCount;
    }

    @JsonProperty("otfValue")
    public Integer getOtfValue() {
        return otfValue;
    }

    @JsonProperty("otfValue")
    public void setOtfValue(Integer otfValue) {
        this.otfValue = otfValue;
    }

    @JsonProperty("updateRecord")
    public Boolean getUpdateRecord() {
        return updateRecord;
    }

    @JsonProperty("updateRecord")
    public void setUpdateRecord(Boolean updateRecord) {
        this.updateRecord = updateRecord;
    }

    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(String userID) {
        this.userID = userID;
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
