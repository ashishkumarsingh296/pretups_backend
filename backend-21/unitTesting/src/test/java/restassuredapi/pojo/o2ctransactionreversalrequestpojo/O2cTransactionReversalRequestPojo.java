package restassuredapi.pojo.o2ctransactionreversalrequestpojo;

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
@JsonPropertyOrder({ "remarks", "transactionID" })
@Generated("jsonschema2pojo")
public class O2cTransactionReversalRequestPojo {

	@JsonProperty("remarks")
	private String remarks;
	@JsonProperty("transactionID")
	private String transactionID;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("remarks")
	public String getRemarks() {
		return remarks;
	}

	@JsonProperty("remarks")
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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