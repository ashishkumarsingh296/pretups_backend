
package restassuredapi.pojo.commissionslabdetailsresponsepojo;

import java.util.HashMap;
import java.util.List;
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
    "addtnlCommProfileDetailID",
    "cACDetailsListSize",
    "cacApplicableFrom",
    "cacApplicableTo",
    "cacDetailRate",
    "cacDetailType",
    "cacDetailValue",
    "cacTimeSlab",
    "cacType",
    "commProfileServiceTypeId",
    "commStartRange",
    "commToRange",
    "commisionDiffFactor",
    "commisionRate",
    "commissionType",
    "listCACDetails",
    "ownerAddnlCommRate",
    "ownerAddnlCommType",
    "ownerTax1Rate",
    "ownerTax1Type",
    "ownerTax2Rate",
    "ownerTax2Type",
    "roamAdditionalCommRate",
    "roamAdditionalCommType",
    "status",
    "tax1Rate",
    "tax1Type",
    "tax2Rate",
    "tax2Type"
})
@Generated("jsonschema2pojo")
public class ListAdditionalCommSlabDetail {

    @JsonProperty("addtnlCommProfileDetailID")
    private String addtnlCommProfileDetailID;
    @JsonProperty("cACDetailsListSize")
    private String cACDetailsListSize;
    @JsonProperty("cacApplicableFrom")
    private String cacApplicableFrom;
    @JsonProperty("cacApplicableTo")
    private String cacApplicableTo;
    @JsonProperty("cacDetailRate")
    private String cacDetailRate;
    @JsonProperty("cacDetailType")
    private String cacDetailType;
    @JsonProperty("cacDetailValue")
    private String cacDetailValue;
    @JsonProperty("cacTimeSlab")
    private String cacTimeSlab;
    @JsonProperty("cacType")
    private String cacType;
    @JsonProperty("commProfileServiceTypeId")
    private String commProfileServiceTypeId;
    @JsonProperty("commStartRange")
    private String commStartRange;
    @JsonProperty("commToRange")
    private String commToRange;
    @JsonProperty("commisionDiffFactor")
    private String commisionDiffFactor;
    @JsonProperty("commisionRate")
    private String commisionRate;
    @JsonProperty("commissionType")
    private String commissionType;
    @JsonProperty("listCACDetails")
    private List<ListCACDetail> listCACDetails = null;
    @JsonProperty("ownerAddnlCommRate")
    private String ownerAddnlCommRate;
    @JsonProperty("ownerAddnlCommType")
    private String ownerAddnlCommType;
    @JsonProperty("ownerTax1Rate")
    private String ownerTax1Rate;
    @JsonProperty("ownerTax1Type")
    private String ownerTax1Type;
    @JsonProperty("ownerTax2Rate")
    private String ownerTax2Rate;
    @JsonProperty("ownerTax2Type")
    private String ownerTax2Type;
    @JsonProperty("roamAdditionalCommRate")
    private String roamAdditionalCommRate;
    @JsonProperty("roamAdditionalCommType")
    private String roamAdditionalCommType;
    @JsonProperty("status")
    private String status;
    @JsonProperty("tax1Rate")
    private String tax1Rate;
    @JsonProperty("tax1Type")
    private String tax1Type;
    @JsonProperty("tax2Rate")
    private String tax2Rate;
    @JsonProperty("tax2Type")
    private String tax2Type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("addtnlCommProfileDetailID")
    public String getAddtnlCommProfileDetailID() {
        return addtnlCommProfileDetailID;
    }

    @JsonProperty("addtnlCommProfileDetailID")
    public void setAddtnlCommProfileDetailID(String addtnlCommProfileDetailID) {
        this.addtnlCommProfileDetailID = addtnlCommProfileDetailID;
    }

    @JsonProperty("cACDetailsListSize")
    public String getcACDetailsListSize() {
        return cACDetailsListSize;
    }

    @JsonProperty("cACDetailsListSize")
    public void setcACDetailsListSize(String cACDetailsListSize) {
        this.cACDetailsListSize = cACDetailsListSize;
    }

    @JsonProperty("cacApplicableFrom")
    public String getCacApplicableFrom() {
        return cacApplicableFrom;
    }

    @JsonProperty("cacApplicableFrom")
    public void setCacApplicableFrom(String cacApplicableFrom) {
        this.cacApplicableFrom = cacApplicableFrom;
    }

    @JsonProperty("cacApplicableTo")
    public String getCacApplicableTo() {
        return cacApplicableTo;
    }

    @JsonProperty("cacApplicableTo")
    public void setCacApplicableTo(String cacApplicableTo) {
        this.cacApplicableTo = cacApplicableTo;
    }

    @JsonProperty("cacDetailRate")
    public String getCacDetailRate() {
        return cacDetailRate;
    }

    @JsonProperty("cacDetailRate")
    public void setCacDetailRate(String cacDetailRate) {
        this.cacDetailRate = cacDetailRate;
    }

    @JsonProperty("cacDetailType")
    public String getCacDetailType() {
        return cacDetailType;
    }

    @JsonProperty("cacDetailType")
    public void setCacDetailType(String cacDetailType) {
        this.cacDetailType = cacDetailType;
    }

    @JsonProperty("cacDetailValue")
    public String getCacDetailValue() {
        return cacDetailValue;
    }

    @JsonProperty("cacDetailValue")
    public void setCacDetailValue(String cacDetailValue) {
        this.cacDetailValue = cacDetailValue;
    }

    @JsonProperty("cacTimeSlab")
    public String getCacTimeSlab() {
        return cacTimeSlab;
    }

    @JsonProperty("cacTimeSlab")
    public void setCacTimeSlab(String cacTimeSlab) {
        this.cacTimeSlab = cacTimeSlab;
    }

    @JsonProperty("cacType")
    public String getCacType() {
        return cacType;
    }

    @JsonProperty("cacType")
    public void setCacType(String cacType) {
        this.cacType = cacType;
    }

    @JsonProperty("commProfileServiceTypeId")
    public String getCommProfileServiceTypeId() {
        return commProfileServiceTypeId;
    }

    @JsonProperty("commProfileServiceTypeId")
    public void setCommProfileServiceTypeId(String commProfileServiceTypeId) {
        this.commProfileServiceTypeId = commProfileServiceTypeId;
    }

    @JsonProperty("commStartRange")
    public String getCommStartRange() {
        return commStartRange;
    }

    @JsonProperty("commStartRange")
    public void setCommStartRange(String commStartRange) {
        this.commStartRange = commStartRange;
    }

    @JsonProperty("commToRange")
    public String getCommToRange() {
        return commToRange;
    }

    @JsonProperty("commToRange")
    public void setCommToRange(String commToRange) {
        this.commToRange = commToRange;
    }

    @JsonProperty("commisionDiffFactor")
    public String getCommisionDiffFactor() {
        return commisionDiffFactor;
    }

    @JsonProperty("commisionDiffFactor")
    public void setCommisionDiffFactor(String commisionDiffFactor) {
        this.commisionDiffFactor = commisionDiffFactor;
    }

    @JsonProperty("commisionRate")
    public String getCommisionRate() {
        return commisionRate;
    }

    @JsonProperty("commisionRate")
    public void setCommisionRate(String commisionRate) {
        this.commisionRate = commisionRate;
    }

    @JsonProperty("commissionType")
    public String getCommissionType() {
        return commissionType;
    }

    @JsonProperty("commissionType")
    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }

    @JsonProperty("listCACDetails")
    public List<ListCACDetail> getListCACDetails() {
        return listCACDetails;
    }

    @JsonProperty("listCACDetails")
    public void setListCACDetails(List<ListCACDetail> listCACDetails) {
        this.listCACDetails = listCACDetails;
    }

    @JsonProperty("ownerAddnlCommRate")
    public String getOwnerAddnlCommRate() {
        return ownerAddnlCommRate;
    }

    @JsonProperty("ownerAddnlCommRate")
    public void setOwnerAddnlCommRate(String ownerAddnlCommRate) {
        this.ownerAddnlCommRate = ownerAddnlCommRate;
    }

    @JsonProperty("ownerAddnlCommType")
    public String getOwnerAddnlCommType() {
        return ownerAddnlCommType;
    }

    @JsonProperty("ownerAddnlCommType")
    public void setOwnerAddnlCommType(String ownerAddnlCommType) {
        this.ownerAddnlCommType = ownerAddnlCommType;
    }

    @JsonProperty("ownerTax1Rate")
    public String getOwnerTax1Rate() {
        return ownerTax1Rate;
    }

    @JsonProperty("ownerTax1Rate")
    public void setOwnerTax1Rate(String ownerTax1Rate) {
        this.ownerTax1Rate = ownerTax1Rate;
    }

    @JsonProperty("ownerTax1Type")
    public String getOwnerTax1Type() {
        return ownerTax1Type;
    }

    @JsonProperty("ownerTax1Type")
    public void setOwnerTax1Type(String ownerTax1Type) {
        this.ownerTax1Type = ownerTax1Type;
    }

    @JsonProperty("ownerTax2Rate")
    public String getOwnerTax2Rate() {
        return ownerTax2Rate;
    }

    @JsonProperty("ownerTax2Rate")
    public void setOwnerTax2Rate(String ownerTax2Rate) {
        this.ownerTax2Rate = ownerTax2Rate;
    }

    @JsonProperty("ownerTax2Type")
    public String getOwnerTax2Type() {
        return ownerTax2Type;
    }

    @JsonProperty("ownerTax2Type")
    public void setOwnerTax2Type(String ownerTax2Type) {
        this.ownerTax2Type = ownerTax2Type;
    }

    @JsonProperty("roamAdditionalCommRate")
    public String getRoamAdditionalCommRate() {
        return roamAdditionalCommRate;
    }

    @JsonProperty("roamAdditionalCommRate")
    public void setRoamAdditionalCommRate(String roamAdditionalCommRate) {
        this.roamAdditionalCommRate = roamAdditionalCommRate;
    }

    @JsonProperty("roamAdditionalCommType")
    public String getRoamAdditionalCommType() {
        return roamAdditionalCommType;
    }

    @JsonProperty("roamAdditionalCommType")
    public void setRoamAdditionalCommType(String roamAdditionalCommType) {
        this.roamAdditionalCommType = roamAdditionalCommType;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("tax1Rate")
    public String getTax1Rate() {
        return tax1Rate;
    }

    @JsonProperty("tax1Rate")
    public void setTax1Rate(String tax1Rate) {
        this.tax1Rate = tax1Rate;
    }

    @JsonProperty("tax1Type")
    public String getTax1Type() {
        return tax1Type;
    }

    @JsonProperty("tax1Type")
    public void setTax1Type(String tax1Type) {
        this.tax1Type = tax1Type;
    }

    @JsonProperty("tax2Rate")
    public String getTax2Rate() {
        return tax2Rate;
    }

    @JsonProperty("tax2Rate")
    public void setTax2Rate(String tax2Rate) {
        this.tax2Rate = tax2Rate;
    }

    @JsonProperty("tax2Type")
    public String getTax2Type() {
        return tax2Type;
    }

    @JsonProperty("tax2Type")
    public void setTax2Type(String tax2Type) {
        this.tax2Type = tax2Type;
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
