
package restassuredapi.pojo.fetchuserdetailsresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "inboundSuspensionRights",
    "lowBalanceAlertToOthers",
    "lowBalanceAlertToParent",
    "lowBalanceAlertToSelf",
    "ouboundSuspensionRights",
    "paymentDesc",
    "paymentModes",
    "paymentType",
    "paymentTypeList",
    "paymentTypes",
    "serviceInformation",
    "serviceList",
    "serviceTypes",
    "userWidgets",
    "voucherList",
    "voucherType"
})
public class PaymentAndServiceDetails {

    @JsonProperty("inboundSuspensionRights")
    private String inboundSuspensionRights;
    @JsonProperty("lowBalanceAlertToOthers")
    private String lowBalanceAlertToOthers;
    @JsonProperty("lowBalanceAlertToParent")
    private String lowBalanceAlertToParent;
    @JsonProperty("lowBalanceAlertToSelf")
    private String lowBalanceAlertToSelf;
    @JsonProperty("ouboundSuspensionRights")
    private String ouboundSuspensionRights;
    @JsonProperty("paymentDesc")
    private String paymentDesc;
    @JsonProperty("paymentModes")
    private List<String> paymentModes = null;
    @JsonProperty("paymentType")
    private String paymentType;
    @JsonProperty("paymentTypeList")
    private List<PaymentTypeList> paymentTypeList = null;
    @JsonProperty("paymentTypes")
    private List<String> paymentTypes = null;
    @JsonProperty("serviceInformation")
    private List<String> serviceInformation = null;
    @JsonProperty("serviceList")
    private List<ServiceList> serviceList = null;
    @JsonProperty("serviceTypes")
    private List<String> serviceTypes = null;
    @JsonProperty("userWidgets")
    private List<String> userWidgets = null;
    @JsonProperty("voucherList")
    private List<VoucherList> voucherList = null;
    @JsonProperty("voucherType")
    private List<String> voucherType = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("inboundSuspensionRights")
    public String getInboundSuspensionRights() {
        return inboundSuspensionRights;
    }

    @JsonProperty("inboundSuspensionRights")
    public void setInboundSuspensionRights(String inboundSuspensionRights) {
        this.inboundSuspensionRights = inboundSuspensionRights;
    }

    @JsonProperty("lowBalanceAlertToOthers")
    public String getLowBalanceAlertToOthers() {
        return lowBalanceAlertToOthers;
    }

    @JsonProperty("lowBalanceAlertToOthers")
    public void setLowBalanceAlertToOthers(String lowBalanceAlertToOthers) {
        this.lowBalanceAlertToOthers = lowBalanceAlertToOthers;
    }

    @JsonProperty("lowBalanceAlertToParent")
    public String getLowBalanceAlertToParent() {
        return lowBalanceAlertToParent;
    }

    @JsonProperty("lowBalanceAlertToParent")
    public void setLowBalanceAlertToParent(String lowBalanceAlertToParent) {
        this.lowBalanceAlertToParent = lowBalanceAlertToParent;
    }

    @JsonProperty("lowBalanceAlertToSelf")
    public String getLowBalanceAlertToSelf() {
        return lowBalanceAlertToSelf;
    }

    @JsonProperty("lowBalanceAlertToSelf")
    public void setLowBalanceAlertToSelf(String lowBalanceAlertToSelf) {
        this.lowBalanceAlertToSelf = lowBalanceAlertToSelf;
    }

    @JsonProperty("ouboundSuspensionRights")
    public String getOuboundSuspensionRights() {
        return ouboundSuspensionRights;
    }

    @JsonProperty("ouboundSuspensionRights")
    public void setOuboundSuspensionRights(String ouboundSuspensionRights) {
        this.ouboundSuspensionRights = ouboundSuspensionRights;
    }

    @JsonProperty("paymentDesc")
    public String getPaymentDesc() {
        return paymentDesc;
    }

    @JsonProperty("paymentDesc")
    public void setPaymentDesc(String paymentDesc) {
        this.paymentDesc = paymentDesc;
    }

    @JsonProperty("paymentModes")
    public List<String> getPaymentModes() {
        return paymentModes;
    }

    @JsonProperty("paymentModes")
    public void setPaymentModes(List<String> paymentModes) {
        this.paymentModes = paymentModes;
    }

    @JsonProperty("paymentType")
    public String getPaymentType() {
        return paymentType;
    }

    @JsonProperty("paymentType")
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @JsonProperty("paymentTypeList")
    public List<PaymentTypeList> getPaymentTypeList() {
        return paymentTypeList;
    }

    @JsonProperty("paymentTypeList")
    public void setPaymentTypeList(List<PaymentTypeList> paymentTypeList) {
        this.paymentTypeList = paymentTypeList;
    }

    @JsonProperty("paymentTypes")
    public List<String> getPaymentTypes() {
        return paymentTypes;
    }

    @JsonProperty("paymentTypes")
    public void setPaymentTypes(List<String> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    @JsonProperty("serviceInformation")
    public List<String> getServiceInformation() {
        return serviceInformation;
    }

    @JsonProperty("serviceInformation")
    public void setServiceInformation(List<String> serviceInformation) {
        this.serviceInformation = serviceInformation;
    }

    @JsonProperty("serviceList")
    public List<ServiceList> getServiceList() {
        return serviceList;
    }

    @JsonProperty("serviceList")
    public void setServiceList(List<ServiceList> serviceList) {
        this.serviceList = serviceList;
    }

    @JsonProperty("serviceTypes")
    public List<String> getServiceTypes() {
        return serviceTypes;
    }

    @JsonProperty("serviceTypes")
    public void setServiceTypes(List<String> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @JsonProperty("userWidgets")
    public List<String> getUserWidgets() {
        return userWidgets;
    }

    @JsonProperty("userWidgets")
    public void setUserWidgets(List<String> userWidgets) {
        this.userWidgets = userWidgets;
    }

    @JsonProperty("voucherList")
    public List<VoucherList> getVoucherList() {
        return voucherList;
    }

    @JsonProperty("voucherList")
    public void setVoucherList(List<VoucherList> voucherList) {
        this.voucherList = voucherList;
    }

    @JsonProperty("voucherType")
    public List<String> getVoucherType() {
        return voucherType;
    }

    @JsonProperty("voucherType")
    public void setVoucherType(List<String> voucherType) {
        this.voucherType = voucherType;
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
