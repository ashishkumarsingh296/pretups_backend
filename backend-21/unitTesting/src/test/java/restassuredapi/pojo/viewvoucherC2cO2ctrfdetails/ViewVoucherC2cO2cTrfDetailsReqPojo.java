
package restassuredapi.pojo.viewvoucherC2cO2ctrfdetails;

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
    "cbcflag",
    "commissionProfileID",
    "commissionProfileVersion",
    "dualCommission",
    "extnwcode",
    "language1",
    "requestType",
    "slablist",
    "transferSubType",
    "transferType"
})
public class ViewVoucherC2cO2cTrfDetailsReqPojo {

    @JsonProperty("cbcflag")
    private String cbcflag;
    @JsonProperty("commissionProfileID")
    private int commissionProfileID;
    @JsonProperty("commissionProfileVersion")
    private int commissionProfileVersion;
    @JsonProperty("dualCommission")
    private String dualCommission;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("language1")
    private int language1;
    @JsonProperty("requestType")
    private String requestType;
    @JsonProperty("slablist")
    private List<Slablist> slablist = null;
    @JsonProperty("transferSubType")
    private String transferSubType;
    @JsonProperty("transferType")
    private String transferType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public ViewVoucherC2cO2cTrfDetailsReqPojo() {
    }

    /**
     * 
     * @param commissionProfileID
     * @param dualCommission
     * @param requestType
     * @param cbcflag
     * @param language1
     * @param extnwcode
     * @param transferSubType
     * @param commissionProfileVersion
     * @param transferType
     * @param slablist
     */
    public ViewVoucherC2cO2cTrfDetailsReqPojo(String cbcflag, int commissionProfileID, int commissionProfileVersion, String dualCommission, String extnwcode, int language1, String requestType, List<Slablist> slablist, String transferSubType, String transferType) {
        super();
        this.cbcflag = cbcflag;
        this.commissionProfileID = commissionProfileID;
        this.commissionProfileVersion = commissionProfileVersion;
        this.dualCommission = dualCommission;
        this.extnwcode = extnwcode;
        this.language1 = language1;
        this.requestType = requestType;
        this.slablist = slablist;
        this.transferSubType = transferSubType;
        this.transferType = transferType;
    }

    @JsonProperty("cbcflag")
    public String getCbcflag() {
        return cbcflag;
    }

    @JsonProperty("cbcflag")
    public void setCbcflag(String cbcflag) {
        this.cbcflag = cbcflag;
    }

    @JsonProperty("commissionProfileID")
    public int getCommissionProfileID() {
        return commissionProfileID;
    }

    @JsonProperty("commissionProfileID")
    public void setCommissionProfileID(int commissionProfileID) {
        this.commissionProfileID = commissionProfileID;
    }

    @JsonProperty("commissionProfileVersion")
    public int getCommissionProfileVersion() {
        return commissionProfileVersion;
    }

    @JsonProperty("commissionProfileVersion")
    public void setCommissionProfileVersion(int commissionProfileVersion) {
        this.commissionProfileVersion = commissionProfileVersion;
    }

    @JsonProperty("dualCommission")
    public String getDualCommission() {
        return dualCommission;
    }

    @JsonProperty("dualCommission")
    public void setDualCommission(String dualCommission) {
        this.dualCommission = dualCommission;
    }

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("language1")
    public int getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(int language1) {
        this.language1 = language1;
    }

    @JsonProperty("requestType")
    public String getRequestType() {
        return requestType;
    }

    @JsonProperty("requestType")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @JsonProperty("slablist")
    public List<Slablist> getSlablist() {
        return slablist;
    }

    @JsonProperty("slablist")
    public void setSlablist(List<Slablist> slablist) {
        this.slablist = slablist;
    }

    @JsonProperty("transferSubType")
    public String getTransferSubType() {
        return transferSubType;
    }

    @JsonProperty("transferSubType")
    public void setTransferSubType(String transferSubType) {
        this.transferSubType = transferSubType;
    }

    @JsonProperty("transferType")
    public String getTransferType() {
        return transferType;
    }

    @JsonProperty("transferType")
    public void setTransferType(String transferType) {
        this.transferType = transferType;
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
