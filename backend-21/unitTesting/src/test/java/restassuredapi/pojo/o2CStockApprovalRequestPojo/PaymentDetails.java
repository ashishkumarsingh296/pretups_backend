
package restassuredapi.pojo.o2CStockApprovalRequestPojo;

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
    "paymentDate",
    "paymentInstNumber",
    "paymentType"
})
public class PaymentDetails {

    @JsonProperty("paymentDate")
    private String paymentDate;
    @JsonProperty("paymentInstNumber")
    private String paymentInstNumber;
    @JsonProperty("paymentType")
    private String paymentType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("paymentDate")
    public String getPaymentDate() {
        return paymentDate;
    }

    @JsonProperty("paymentDate")
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    @JsonProperty("paymentInstNumber")
    public String getPaymentInstNumber() {
        return paymentInstNumber;
    }

    @JsonProperty("paymentInstNumber")
    public void setPaymentInstNumber(String paymentInstNumber) {
        this.paymentInstNumber = paymentInstNumber;
    }

    @JsonProperty("paymentType")
    public String getPaymentType() {
        return paymentType;
    }

    @JsonProperty("paymentType")
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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
