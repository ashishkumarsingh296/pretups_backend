
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
    "commissionProfile",
    "commissionProfileList",
    "commissionProfileSetId",
    "commissionProfileSetIdDesc",
    "transferProfilIdDesc",
    "transferProfile",
    "transferProfileId",
    "transferProfileList",
    "transferRuleType",
    "transferRuleTypeId",
    "transferRuleTypeIdDesc",
    "transferRuleTypeList",
    "userGrade"
})
public class ProfileDetails {

    @JsonProperty("commissionProfile")
    private String commissionProfile;
    @JsonProperty("commissionProfileList")
    private List<CommissionProfileList> commissionProfileList = null;
    @JsonProperty("commissionProfileSetId")
    private String commissionProfileSetId;
    @JsonProperty("commissionProfileSetIdDesc")
    private String commissionProfileSetIdDesc;
    @JsonProperty("transferProfilIdDesc")
    private String transferProfilIdDesc;
    @JsonProperty("transferProfile")
    private String transferProfile;
    @JsonProperty("transferProfileId")
    private String transferProfileId;
    @JsonProperty("transferProfileList")
    private List<TransferProfileList> transferProfileList = null;
    @JsonProperty("transferRuleType")
    private String transferRuleType;
    @JsonProperty("transferRuleTypeId")
    private String transferRuleTypeId;
    @JsonProperty("transferRuleTypeIdDesc")
    private String transferRuleTypeIdDesc;
    @JsonProperty("transferRuleTypeList")
    private List<TransferRuleTypeList> transferRuleTypeList = null;
    @JsonProperty("userGrade")
    private String userGrade;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("commissionProfile")
    public String getCommissionProfile() {
        return commissionProfile;
    }

    @JsonProperty("commissionProfile")
    public void setCommissionProfile(String commissionProfile) {
        this.commissionProfile = commissionProfile;
    }

    @JsonProperty("commissionProfileList")
    public List<CommissionProfileList> getCommissionProfileList() {
        return commissionProfileList;
    }

    @JsonProperty("commissionProfileList")
    public void setCommissionProfileList(List<CommissionProfileList> commissionProfileList) {
        this.commissionProfileList = commissionProfileList;
    }

    @JsonProperty("commissionProfileSetId")
    public String getCommissionProfileSetId() {
        return commissionProfileSetId;
    }

    @JsonProperty("commissionProfileSetId")
    public void setCommissionProfileSetId(String commissionProfileSetId) {
        this.commissionProfileSetId = commissionProfileSetId;
    }

    @JsonProperty("commissionProfileSetIdDesc")
    public String getCommissionProfileSetIdDesc() {
        return commissionProfileSetIdDesc;
    }

    @JsonProperty("commissionProfileSetIdDesc")
    public void setCommissionProfileSetIdDesc(String commissionProfileSetIdDesc) {
        this.commissionProfileSetIdDesc = commissionProfileSetIdDesc;
    }

    @JsonProperty("transferProfilIdDesc")
    public String getTransferProfilIdDesc() {
        return transferProfilIdDesc;
    }

    @JsonProperty("transferProfilIdDesc")
    public void setTransferProfilIdDesc(String transferProfilIdDesc) {
        this.transferProfilIdDesc = transferProfilIdDesc;
    }

    @JsonProperty("transferProfile")
    public String getTransferProfile() {
        return transferProfile;
    }

    @JsonProperty("transferProfile")
    public void setTransferProfile(String transferProfile) {
        this.transferProfile = transferProfile;
    }

    @JsonProperty("transferProfileId")
    public String getTransferProfileId() {
        return transferProfileId;
    }

    @JsonProperty("transferProfileId")
    public void setTransferProfileId(String transferProfileId) {
        this.transferProfileId = transferProfileId;
    }

    @JsonProperty("transferProfileList")
    public List<TransferProfileList> getTransferProfileList() {
        return transferProfileList;
    }

    @JsonProperty("transferProfileList")
    public void setTransferProfileList(List<TransferProfileList> transferProfileList) {
        this.transferProfileList = transferProfileList;
    }

    @JsonProperty("transferRuleType")
    public String getTransferRuleType() {
        return transferRuleType;
    }

    @JsonProperty("transferRuleType")
    public void setTransferRuleType(String transferRuleType) {
        this.transferRuleType = transferRuleType;
    }

    @JsonProperty("transferRuleTypeId")
    public String getTransferRuleTypeId() {
        return transferRuleTypeId;
    }

    @JsonProperty("transferRuleTypeId")
    public void setTransferRuleTypeId(String transferRuleTypeId) {
        this.transferRuleTypeId = transferRuleTypeId;
    }

    @JsonProperty("transferRuleTypeIdDesc")
    public String getTransferRuleTypeIdDesc() {
        return transferRuleTypeIdDesc;
    }

    @JsonProperty("transferRuleTypeIdDesc")
    public void setTransferRuleTypeIdDesc(String transferRuleTypeIdDesc) {
        this.transferRuleTypeIdDesc = transferRuleTypeIdDesc;
    }

    @JsonProperty("transferRuleTypeList")
    public List<TransferRuleTypeList> getTransferRuleTypeList() {
        return transferRuleTypeList;
    }

    @JsonProperty("transferRuleTypeList")
    public void setTransferRuleTypeList(List<TransferRuleTypeList> transferRuleTypeList) {
        this.transferRuleTypeList = transferRuleTypeList;
    }

    @JsonProperty("userGrade")
    public String getUserGrade() {
        return userGrade;
    }

    @JsonProperty("userGrade")
    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
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
