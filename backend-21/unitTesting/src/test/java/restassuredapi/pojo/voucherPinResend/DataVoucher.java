package restassuredapi.pojo.voucherPinResend;

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
"customerMsisdn",
"date",
"remarks",
"requestGatewayCode",
"serialNo",
"subscriberMsisdn",
"transactionid"
})
@Generated("jsonschema2pojo")
public class DataVoucher {

@JsonProperty("customerMsisdn")
private String customerMsisdn;
@JsonProperty("date")
private String date;
@JsonProperty("remarks")
private String remarks;
@JsonProperty("requestGatewayCode")
private String requestGatewayCode;
@JsonProperty("serialNo")
private String serialNo;
@JsonProperty("subscriberMsisdn")
private String subscriberMsisdn;
@JsonProperty("transactionid")
private String transactionid;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("customerMsisdn")
public String getCustomerMsisdn() {
return customerMsisdn;
}

@JsonProperty("customerMsisdn")
public void setCustomerMsisdn(String customerMsisdn) {
this.customerMsisdn = customerMsisdn;
}

@JsonProperty("date")
public String getDate() {
return date;
}

@JsonProperty("date")
public void setDate(String date) {
this.date = date;
}

@JsonProperty("remarks")
public String getRemarks() {
return remarks;
}

@JsonProperty("remarks")
public void setRemarks(String remarks) {
this.remarks = remarks;
}

@JsonProperty("requestGatewayCode")
public String getRequestGatewayCode() {
return requestGatewayCode;
}

@JsonProperty("requestGatewayCode")
public void setRequestGatewayCode(String requestGatewayCode) {
this.requestGatewayCode = requestGatewayCode;
}

@JsonProperty("serialNo")
public String getSerialNo() {
return serialNo;
}

@JsonProperty("serialNo")
public void setSerialNo(String serialNo) {
this.serialNo = serialNo;
}

@JsonProperty("subscriberMsisdn")
public String getSubscriberMsisdn() {
return subscriberMsisdn;
}

@JsonProperty("subscriberMsisdn")
public void setSubscriberMsisdn(String subscriberMsisdn) {
this.subscriberMsisdn = subscriberMsisdn;
}

@JsonProperty("transactionid")
public String getTransactionid() {
return transactionid;
}

@JsonProperty("transactionid")
public void setTransactionid(String transactionid) {
this.transactionid = transactionid;
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
