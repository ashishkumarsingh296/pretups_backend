
package restassuredapi.pojo.geodomaincatresponsepojo;

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
    "graphDomainCode",
    "graphDomainName",
    "graphDomainTypeName",
    "graphDomainType",
    "graphDomainSequenceNumber",
    "userId",
    "parentGraphDomainCode",
    "categoryCode",
    "networkName"
})
public class GeoList {

    @JsonProperty("graphDomainCode")
    private String graphDomainCode;
    @JsonProperty("graphDomainName")
    private String graphDomainName;
    @JsonProperty("graphDomainTypeName")
    private String graphDomainTypeName;
    @JsonProperty("graphDomainType")
    private String graphDomainType;
    @JsonProperty("graphDomainSequenceNumber")
    private Long graphDomainSequenceNumber;
    @JsonProperty("userId")
    private Object userId;
    @JsonProperty("parentGraphDomainCode")
    private Object parentGraphDomainCode;
    @JsonProperty("categoryCode")
    private Object categoryCode;
    @JsonProperty("networkName")
    private Object networkName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public GeoList() {
    }

    /**
     * 
     * @param graphDomainName
     * @param graphDomainType
     * @param graphDomainCode
     * @param networkName
     * @param graphDomainSequenceNumber
     * @param categoryCode
     * @param userId
     * @param parentGraphDomainCode
     * @param graphDomainTypeName
     */
    public GeoList(String graphDomainCode, String graphDomainName, String graphDomainTypeName, String graphDomainType, Long graphDomainSequenceNumber, Object userId, Object parentGraphDomainCode, Object categoryCode, Object networkName) {
        super();
        this.graphDomainCode = graphDomainCode;
        this.graphDomainName = graphDomainName;
        this.graphDomainTypeName = graphDomainTypeName;
        this.graphDomainType = graphDomainType;
        this.graphDomainSequenceNumber = graphDomainSequenceNumber;
        this.userId = userId;
        this.parentGraphDomainCode = parentGraphDomainCode;
        this.categoryCode = categoryCode;
        this.networkName = networkName;
    }

    @JsonProperty("graphDomainCode")
    public String getGraphDomainCode() {
        return graphDomainCode;
    }

    @JsonProperty("graphDomainCode")
    public void setGraphDomainCode(String graphDomainCode) {
        this.graphDomainCode = graphDomainCode;
    }

    @JsonProperty("graphDomainName")
    public String getGraphDomainName() {
        return graphDomainName;
    }

    @JsonProperty("graphDomainName")
    public void setGraphDomainName(String graphDomainName) {
        this.graphDomainName = graphDomainName;
    }

    @JsonProperty("graphDomainTypeName")
    public String getGraphDomainTypeName() {
        return graphDomainTypeName;
    }

    @JsonProperty("graphDomainTypeName")
    public void setGraphDomainTypeName(String graphDomainTypeName) {
        this.graphDomainTypeName = graphDomainTypeName;
    }

    @JsonProperty("graphDomainType")
    public String getGraphDomainType() {
        return graphDomainType;
    }

    @JsonProperty("graphDomainType")
    public void setGraphDomainType(String graphDomainType) {
        this.graphDomainType = graphDomainType;
    }

    @JsonProperty("graphDomainSequenceNumber")
    public Long getGraphDomainSequenceNumber() {
        return graphDomainSequenceNumber;
    }

    @JsonProperty("graphDomainSequenceNumber")
    public void setGraphDomainSequenceNumber(Long graphDomainSequenceNumber) {
        this.graphDomainSequenceNumber = graphDomainSequenceNumber;
    }

    @JsonProperty("userId")
    public Object getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(Object userId) {
        this.userId = userId;
    }

    @JsonProperty("parentGraphDomainCode")
    public Object getParentGraphDomainCode() {
        return parentGraphDomainCode;
    }

    @JsonProperty("parentGraphDomainCode")
    public void setParentGraphDomainCode(Object parentGraphDomainCode) {
        this.parentGraphDomainCode = parentGraphDomainCode;
    }

    @JsonProperty("categoryCode")
    public Object getCategoryCode() {
        return categoryCode;
    }

    @JsonProperty("categoryCode")
    public void setCategoryCode(Object categoryCode) {
        this.categoryCode = categoryCode;
    }

    @JsonProperty("networkName")
    public Object getNetworkName() {
        return networkName;
    }

    @JsonProperty("networkName")
    public void setNetworkName(Object networkName) {
        this.networkName = networkName;
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
