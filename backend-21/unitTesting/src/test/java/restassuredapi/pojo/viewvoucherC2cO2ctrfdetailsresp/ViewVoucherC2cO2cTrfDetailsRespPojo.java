
package restassuredapi.pojo.viewvoucherC2cO2ctrfdetailsresp;

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
    "status",
    "messageCode",
    "message",
    "totalMRP",
    "totalNetPayableAmount",
    "totalPayableAmount",
    "totalReqQty",
    "totalTransferedAmount",
    "totalOtfValue",
    "totalTax1",
    "totalTax2",
    "totalTax3",
    "totalComm",
    "commissionQuantity",
    "totalVoucherOrderQuantity",
    "totalVoucherOrderAmount",
    "tansferProductdetailList",
    "slabDetails",
    "senderDrQty",
    "receiverCrQty"
})
public class ViewVoucherC2cO2cTrfDetailsRespPojo {

    @JsonProperty("status")
    private int status;
    @JsonProperty("messageCode")
    private String messageCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("totalMRP")
    private String totalMRP;
    @JsonProperty("totalNetPayableAmount")
    private String totalNetPayableAmount;
    @JsonProperty("totalPayableAmount")
    private String totalPayableAmount;
    @JsonProperty("totalReqQty")
    private String totalReqQty;
    @JsonProperty("totalTransferedAmount")
    private String totalTransferedAmount;
    @JsonProperty("totalOtfValue")
    private String totalOtfValue;
    @JsonProperty("totalTax1")
    private String totalTax1;
    @JsonProperty("totalTax2")
    private String totalTax2;
    @JsonProperty("totalTax3")
    private String totalTax3;
    @JsonProperty("totalComm")
    private String totalComm;
    @JsonProperty("commissionQuantity")
    private String commissionQuantity;
    @JsonProperty("totalVoucherOrderQuantity")
    private String totalVoucherOrderQuantity;
    @JsonProperty("totalVoucherOrderAmount")
    private String totalVoucherOrderAmount;
    @JsonProperty("tansferProductdetailList")
    private List<TansferProductdetailList> tansferProductdetailList = null;
    @JsonProperty("slabDetails")
    private List<SlabDetail> slabDetails = null;
    @JsonProperty("senderDrQty")
    private String senderDrQty;
    @JsonProperty("receiverCrQty")
    private String receiverCrQty;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public ViewVoucherC2cO2cTrfDetailsRespPojo() {
    }

    /**
     * 
     * @param receiverCrQty
     * @param slabDetails
     * @param totalReqQty
     * @param tansferProductdetailList
     * @param totalTransferedAmount
     * @param totalVoucherOrderQuantity
     * @param message
     * @param totalMRP
     * @param totalVoucherOrderAmount
     * @param totalNetPayableAmount
     * @param totalPayableAmount
     * @param totalOtfValue
     * @param senderDrQty
     * @param messageCode
     * @param totalTax3
     * @param totalTax2
     * @param commissionQuantity
     * @param totalTax1
     * @param status
     * @param totalComm
     */
    public ViewVoucherC2cO2cTrfDetailsRespPojo(int status, String messageCode, String message, String totalMRP, String totalNetPayableAmount, String totalPayableAmount, String totalReqQty, String totalTransferedAmount, String totalOtfValue, String totalTax1, String totalTax2, String totalTax3, String totalComm, String commissionQuantity, String totalVoucherOrderQuantity, String totalVoucherOrderAmount, List<TansferProductdetailList> tansferProductdetailList, List<SlabDetail> slabDetails, String senderDrQty, String receiverCrQty) {
        super();
        this.status = status;
        this.messageCode = messageCode;
        this.message = message;
        this.totalMRP = totalMRP;
        this.totalNetPayableAmount = totalNetPayableAmount;
        this.totalPayableAmount = totalPayableAmount;
        this.totalReqQty = totalReqQty;
        this.totalTransferedAmount = totalTransferedAmount;
        this.totalOtfValue = totalOtfValue;
        this.totalTax1 = totalTax1;
        this.totalTax2 = totalTax2;
        this.totalTax3 = totalTax3;
        this.totalComm = totalComm;
        this.commissionQuantity = commissionQuantity;
        this.totalVoucherOrderQuantity = totalVoucherOrderQuantity;
        this.totalVoucherOrderAmount = totalVoucherOrderAmount;
        this.tansferProductdetailList = tansferProductdetailList;
        this.slabDetails = slabDetails;
        this.senderDrQty = senderDrQty;
        this.receiverCrQty = receiverCrQty;
    }

    @JsonProperty("status")
    public int getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = status;
    }

    @JsonProperty("messageCode")
    public String getMessageCode() {
        return messageCode;
    }

    @JsonProperty("messageCode")
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("totalMRP")
    public String getTotalMRP() {
        return totalMRP;
    }

    @JsonProperty("totalMRP")
    public void setTotalMRP(String totalMRP) {
        this.totalMRP = totalMRP;
    }

    @JsonProperty("totalNetPayableAmount")
    public String getTotalNetPayableAmount() {
        return totalNetPayableAmount;
    }

    @JsonProperty("totalNetPayableAmount")
    public void setTotalNetPayableAmount(String totalNetPayableAmount) {
        this.totalNetPayableAmount = totalNetPayableAmount;
    }

    @JsonProperty("totalPayableAmount")
    public String getTotalPayableAmount() {
        return totalPayableAmount;
    }

    @JsonProperty("totalPayableAmount")
    public void setTotalPayableAmount(String totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    @JsonProperty("totalReqQty")
    public String getTotalReqQty() {
        return totalReqQty;
    }

    @JsonProperty("totalReqQty")
    public void setTotalReqQty(String totalReqQty) {
        this.totalReqQty = totalReqQty;
    }

    @JsonProperty("totalTransferedAmount")
    public String getTotalTransferedAmount() {
        return totalTransferedAmount;
    }

    @JsonProperty("totalTransferedAmount")
    public void setTotalTransferedAmount(String totalTransferedAmount) {
        this.totalTransferedAmount = totalTransferedAmount;
    }

    @JsonProperty("totalOtfValue")
    public String getTotalOtfValue() {
        return totalOtfValue;
    }

    @JsonProperty("totalOtfValue")
    public void setTotalOtfValue(String totalOtfValue) {
        this.totalOtfValue = totalOtfValue;
    }

    @JsonProperty("totalTax1")
    public String getTotalTax1() {
        return totalTax1;
    }

    @JsonProperty("totalTax1")
    public void setTotalTax1(String totalTax1) {
        this.totalTax1 = totalTax1;
    }

    @JsonProperty("totalTax2")
    public String getTotalTax2() {
        return totalTax2;
    }

    @JsonProperty("totalTax2")
    public void setTotalTax2(String totalTax2) {
        this.totalTax2 = totalTax2;
    }

    @JsonProperty("totalTax3")
    public String getTotalTax3() {
        return totalTax3;
    }

    @JsonProperty("totalTax3")
    public void setTotalTax3(String totalTax3) {
        this.totalTax3 = totalTax3;
    }

    @JsonProperty("totalComm")
    public String getTotalComm() {
        return totalComm;
    }

    @JsonProperty("totalComm")
    public void setTotalComm(String totalComm) {
        this.totalComm = totalComm;
    }

    @JsonProperty("commissionQuantity")
    public String getCommissionQuantity() {
        return commissionQuantity;
    }

    @JsonProperty("commissionQuantity")
    public void setCommissionQuantity(String commissionQuantity) {
        this.commissionQuantity = commissionQuantity;
    }

    @JsonProperty("totalVoucherOrderQuantity")
    public String getTotalVoucherOrderQuantity() {
        return totalVoucherOrderQuantity;
    }

    @JsonProperty("totalVoucherOrderQuantity")
    public void setTotalVoucherOrderQuantity(String totalVoucherOrderQuantity) {
        this.totalVoucherOrderQuantity = totalVoucherOrderQuantity;
    }

    @JsonProperty("totalVoucherOrderAmount")
    public String getTotalVoucherOrderAmount() {
        return totalVoucherOrderAmount;
    }

    @JsonProperty("totalVoucherOrderAmount")
    public void setTotalVoucherOrderAmount(String totalVoucherOrderAmount) {
        this.totalVoucherOrderAmount = totalVoucherOrderAmount;
    }

    @JsonProperty("tansferProductdetailList")
    public List<TansferProductdetailList> getTansferProductdetailList() {
        return tansferProductdetailList;
    }

    @JsonProperty("tansferProductdetailList")
    public void setTansferProductdetailList(List<TansferProductdetailList> tansferProductdetailList) {
        this.tansferProductdetailList = tansferProductdetailList;
    }

    @JsonProperty("slabDetails")
    public List<SlabDetail> getSlabDetails() {
        return slabDetails;
    }

    @JsonProperty("slabDetails")
    public void setSlabDetails(List<SlabDetail> slabDetails) {
        this.slabDetails = slabDetails;
    }

    @JsonProperty("senderDrQty")
    public String getSenderDrQty() {
        return senderDrQty;
    }

    @JsonProperty("senderDrQty")
    public void setSenderDrQty(String senderDrQty) {
        this.senderDrQty = senderDrQty;
    }

    @JsonProperty("receiverCrQty")
    public String getReceiverCrQty() {
        return receiverCrQty;
    }

    @JsonProperty("receiverCrQty")
    public void setReceiverCrQty(String receiverCrQty) {
        this.receiverCrQty = receiverCrQty;
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
