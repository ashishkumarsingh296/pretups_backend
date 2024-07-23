
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
    "actionType",
    "bundleId",
    "bundleName",
    "bundleRemarks",
    "firstLevelApprovedQuantity",
    "fromSerialNum",
    "fromUser",
    "initiatedQuantity",
    "modifiedOn",
    "networkCode",
    "productId",
    "productName",
    "requiredQuantity",
    "secondLevelApprovedQuantity",
    "segment",
    "segmentDesc",
    "sno",
    "toSerialNum",
    "toUser",
    "transferDate",
    "transferId",
    "transferMrp",
    "type",
    "voucherType",
    "voucherTypeDesc"
})
@Generated("jsonschema2pojo")
public class ChannelVoucherItemsVo {

    @JsonProperty("actionType")
    private String actionType;
    @JsonProperty("bundleId")
    private Integer bundleId;
    @JsonProperty("bundleName")
    private String bundleName;
    @JsonProperty("bundleRemarks")
    private String bundleRemarks;
    @JsonProperty("firstLevelApprovedQuantity")
    private Integer firstLevelApprovedQuantity;
    @JsonProperty("fromSerialNum")
    private String fromSerialNum;
    @JsonProperty("fromUser")
    private String fromUser;
    @JsonProperty("initiatedQuantity")
    private Integer initiatedQuantity;
    @JsonProperty("modifiedOn")
    private String modifiedOn;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("productId")
    private String productId;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("requiredQuantity")
    private Integer requiredQuantity;
    @JsonProperty("secondLevelApprovedQuantity")
    private Integer secondLevelApprovedQuantity;
    @JsonProperty("segment")
    private String segment;
    @JsonProperty("segmentDesc")
    private String segmentDesc;
    @JsonProperty("sno")
    private Integer sno;
    @JsonProperty("toSerialNum")
    private String toSerialNum;
    @JsonProperty("toUser")
    private String toUser;
    @JsonProperty("transferDate")
    private String transferDate;
    @JsonProperty("transferId")
    private String transferId;
    @JsonProperty("transferMrp")
    private Integer transferMrp;
    @JsonProperty("type")
    private String type;
    @JsonProperty("voucherType")
    private String voucherType;
    @JsonProperty("voucherTypeDesc")
    private String voucherTypeDesc;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("actionType")
    public String getActionType() {
        return actionType;
    }

    @JsonProperty("actionType")
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @JsonProperty("bundleId")
    public Integer getBundleId() {
        return bundleId;
    }

    @JsonProperty("bundleId")
    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    @JsonProperty("bundleName")
    public String getBundleName() {
        return bundleName;
    }

    @JsonProperty("bundleName")
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    @JsonProperty("bundleRemarks")
    public String getBundleRemarks() {
        return bundleRemarks;
    }

    @JsonProperty("bundleRemarks")
    public void setBundleRemarks(String bundleRemarks) {
        this.bundleRemarks = bundleRemarks;
    }

    @JsonProperty("firstLevelApprovedQuantity")
    public Integer getFirstLevelApprovedQuantity() {
        return firstLevelApprovedQuantity;
    }

    @JsonProperty("firstLevelApprovedQuantity")
    public void setFirstLevelApprovedQuantity(Integer firstLevelApprovedQuantity) {
        this.firstLevelApprovedQuantity = firstLevelApprovedQuantity;
    }

    @JsonProperty("fromSerialNum")
    public String getFromSerialNum() {
        return fromSerialNum;
    }

    @JsonProperty("fromSerialNum")
    public void setFromSerialNum(String fromSerialNum) {
        this.fromSerialNum = fromSerialNum;
    }

    @JsonProperty("fromUser")
    public String getFromUser() {
        return fromUser;
    }

    @JsonProperty("fromUser")
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    @JsonProperty("initiatedQuantity")
    public Integer getInitiatedQuantity() {
        return initiatedQuantity;
    }

    @JsonProperty("initiatedQuantity")
    public void setInitiatedQuantity(Integer initiatedQuantity) {
        this.initiatedQuantity = initiatedQuantity;
    }

    @JsonProperty("modifiedOn")
    public String getModifiedOn() {
        return modifiedOn;
    }

    @JsonProperty("modifiedOn")
    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("productId")
    public String getProductId() {
        return productId;
    }

    @JsonProperty("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("requiredQuantity")
    public Integer getRequiredQuantity() {
        return requiredQuantity;
    }

    @JsonProperty("requiredQuantity")
    public void setRequiredQuantity(Integer requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    @JsonProperty("secondLevelApprovedQuantity")
    public Integer getSecondLevelApprovedQuantity() {
        return secondLevelApprovedQuantity;
    }

    @JsonProperty("secondLevelApprovedQuantity")
    public void setSecondLevelApprovedQuantity(Integer secondLevelApprovedQuantity) {
        this.secondLevelApprovedQuantity = secondLevelApprovedQuantity;
    }

    @JsonProperty("segment")
    public String getSegment() {
        return segment;
    }

    @JsonProperty("segment")
    public void setSegment(String segment) {
        this.segment = segment;
    }

    @JsonProperty("segmentDesc")
    public String getSegmentDesc() {
        return segmentDesc;
    }

    @JsonProperty("segmentDesc")
    public void setSegmentDesc(String segmentDesc) {
        this.segmentDesc = segmentDesc;
    }

    @JsonProperty("sno")
    public Integer getSno() {
        return sno;
    }

    @JsonProperty("sno")
    public void setSno(Integer sno) {
        this.sno = sno;
    }

    @JsonProperty("toSerialNum")
    public String getToSerialNum() {
        return toSerialNum;
    }

    @JsonProperty("toSerialNum")
    public void setToSerialNum(String toSerialNum) {
        this.toSerialNum = toSerialNum;
    }

    @JsonProperty("toUser")
    public String getToUser() {
        return toUser;
    }

    @JsonProperty("toUser")
    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    @JsonProperty("transferDate")
    public String getTransferDate() {
        return transferDate;
    }

    @JsonProperty("transferDate")
    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    @JsonProperty("transferId")
    public String getTransferId() {
        return transferId;
    }

    @JsonProperty("transferId")
    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    @JsonProperty("transferMrp")
    public Integer getTransferMrp() {
        return transferMrp;
    }

    @JsonProperty("transferMrp")
    public void setTransferMrp(Integer transferMrp) {
        this.transferMrp = transferMrp;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("voucherType")
    public String getVoucherType() {
        return voucherType;
    }

    @JsonProperty("voucherType")
    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    @JsonProperty("voucherTypeDesc")
    public String getVoucherTypeDesc() {
        return voucherTypeDesc;
    }

    @JsonProperty("voucherTypeDesc")
    public void setVoucherTypeDesc(String voucherTypeDesc) {
        this.voucherTypeDesc = voucherTypeDesc;
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
