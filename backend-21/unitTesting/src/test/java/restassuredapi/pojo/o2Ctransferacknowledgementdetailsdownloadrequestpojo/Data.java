
package restassuredapi.pojo.o2Ctransferacknowledgementdetailsdownloadrequestpojo;

import java.util.HashMap;
import java.util.List;
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
    "dispHeaderColumnList",
    "distributionType",
    "fileType",
    "transactionID"
})
@Generated("jsonschema2pojo")
public class Data {

    @JsonProperty("dispHeaderColumnList")
    private List<DispHeaderColumn> dispHeaderColumnList = null;
    @JsonProperty("distributionType")
    private String distributionType;
    @JsonProperty("fileType")
    private String fileType;
    @JsonProperty("transactionID")
    private String transactionID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("dispHeaderColumnList")
    public List<DispHeaderColumn> getDispHeaderColumnList() {
        return dispHeaderColumnList;
    }

    @JsonProperty("dispHeaderColumnList")
    public void setDispHeaderColumnList(List<DispHeaderColumn> dispHeaderColumnList) {
        this.dispHeaderColumnList = dispHeaderColumnList;
    }

    @JsonProperty("distributionType")
    public String getDistributionType() {
        return distributionType;
    }

    @JsonProperty("distributionType")
    public void setDistributionType(String distributionType) {
        this.distributionType = distributionType;
    }

    @JsonProperty("fileType")
    public String getFileType() {
        return fileType;
    }

    @JsonProperty("fileType")
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @JsonProperty("transactionID")
    public String getTransactionID() {
        return transactionID;
    }

    @JsonProperty("transactionID")
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
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
