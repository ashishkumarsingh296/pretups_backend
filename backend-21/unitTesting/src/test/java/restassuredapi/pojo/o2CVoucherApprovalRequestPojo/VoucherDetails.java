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
"denomination",
"fromSerialNo",
"reqQuantity",
"toSerialNo",
"voucherProfileId",
"voucherType",
"vouchersegment"
})
public class VoucherDetails {

@JsonProperty("denomination")
private Integer denomination;
@JsonProperty("fromSerialNo")
private String fromSerialNo;
@JsonProperty("reqQuantity")
private String reqQuantity;
@JsonProperty("toSerialNo")
private String toSerialNo;
@JsonProperty("voucherProfileId")
private String voucherProfileId;
@JsonProperty("voucherType")
private String voucherType;
@JsonProperty("vouchersegment")
private String vouchersegment;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("denomination")
public Integer getDenomination() {
return denomination;
}

@JsonProperty("denomination")
public void setDenomination(Integer denomination) {
this.denomination = denomination;
}

@JsonProperty("fromSerialNo")
public String getFromSerialNo() {
return fromSerialNo;
}

@JsonProperty("fromSerialNo")
public void setFromSerialNo(String fromSerialNo) {
this.fromSerialNo = fromSerialNo;
}

@JsonProperty("reqQuantity")
public String getReqQuantity() {
return reqQuantity;
}

@JsonProperty("reqQuantity")
public void setReqQuantity(String reqQuantity) {
this.reqQuantity = reqQuantity;
}

@JsonProperty("toSerialNo")
public String getToSerialNo() {
return toSerialNo;
}

@JsonProperty("toSerialNo")
public void setToSerialNo(String toSerialNo) {
this.toSerialNo = toSerialNo;
}

@JsonProperty("voucherProfileId")
public String getVoucherProfileId() {
return voucherProfileId;
}

@JsonProperty("voucherProfileId")
public void setVoucherProfileId(String voucherProfileId) {
this.voucherProfileId = voucherProfileId;
}

@JsonProperty("voucherType")
public String getVoucherType() {
return voucherType;
}

@JsonProperty("voucherType")
public void setVoucherType(String voucherType) {
this.voucherType = voucherType;
}

@JsonProperty("vouchersegment")
public String getVouchersegment() {
return vouchersegment;
}

@JsonProperty("vouchersegment")
public void setVouchersegment(String vouchersegment) {
this.vouchersegment = vouchersegment;
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