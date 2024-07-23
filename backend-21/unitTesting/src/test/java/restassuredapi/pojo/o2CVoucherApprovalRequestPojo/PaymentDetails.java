package restassuredapi.pojo.o2CVoucherApprovalRequestPojo;

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
"paymentdate",
"paymentgatewaytype",
"paymentinstnumber",
"paymenttype"
})
public class PaymentDetails {

@JsonProperty("paymentdate")
private String paymentdate;
@JsonProperty("paymentgatewaytype")
private String paymentgatewaytype;
@JsonProperty("paymentinstnumber")
private Integer paymentinstnumber;
@JsonProperty("paymenttype")
private String paymenttype;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("paymentdate")
public String getPaymentdate() {
return paymentdate;
}

@JsonProperty("paymentdate")
public void setPaymentdate(String paymentdate) {
this.paymentdate = paymentdate;
}

@JsonProperty("paymentgatewaytype")
public String getPaymentgatewaytype() {
return paymentgatewaytype;
}

@JsonProperty("paymentgatewaytype")
public void setPaymentgatewaytype(String paymentgatewaytype) {
this.paymentgatewaytype = paymentgatewaytype;
}

@JsonProperty("paymentinstnumber")
public Integer getPaymentinstnumber() {
return paymentinstnumber;
}

@JsonProperty("paymentinstnumber")
public void setPaymentinstnumber(Integer paymentinstnumber) {
this.paymentinstnumber = paymentinstnumber;
}

@JsonProperty("paymenttype")
public String getPaymenttype() {
return paymenttype;
}

@JsonProperty("paymenttype")
public void setPaymenttype(String paymenttype) {
this.paymenttype = paymenttype;
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
