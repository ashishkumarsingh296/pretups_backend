
package restassuredapi.pojo.viewselfcommenquiryresponsepojo;

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
    "serviceAllowed",
    "commissionProfileSetVO",
    "commissionProfileSetVersionVO",
    "commissionList",
    "addProfileDetailList",
    "additionalProfileDeatilsVO",
    "additionalProfileServicesVO",
    "additionalList",
    "otfProfileList",
    "sequenceNo"
})
public class DataObject {

    @JsonProperty("serviceAllowed")
    private String serviceAllowed;
    @JsonProperty("commissionProfileSetVO")
    private CommissionProfileSetVO commissionProfileSetVO;
    @JsonProperty("commissionProfileSetVersionVO")
    private Object commissionProfileSetVersionVO;
    @JsonProperty("commissionList")
    private List<CommissionList> commissionList = null;
    @JsonProperty("addProfileDetailList")
    private Object addProfileDetailList;
    @JsonProperty("additionalProfileDeatilsVO")
    private Object additionalProfileDeatilsVO;
    @JsonProperty("additionalProfileServicesVO")
    private Object additionalProfileServicesVO;
    @JsonProperty("additionalList")
    private Object additionalList;
    @JsonProperty("otfProfileList")
    private List<OtfProfileList> otfProfileList = null;
    @JsonProperty("sequenceNo")
    private String sequenceNo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("serviceAllowed")
    public String getServiceAllowed() {
        return serviceAllowed;
    }

    @JsonProperty("serviceAllowed")
    public void setServiceAllowed(String serviceAllowed) {
        this.serviceAllowed = serviceAllowed;
    }

    @JsonProperty("commissionProfileSetVO")
    public CommissionProfileSetVO getCommissionProfileSetVO() {
        return commissionProfileSetVO;
    }

    @JsonProperty("commissionProfileSetVO")
    public void setCommissionProfileSetVO(CommissionProfileSetVO commissionProfileSetVO) {
        this.commissionProfileSetVO = commissionProfileSetVO;
    }

    @JsonProperty("commissionProfileSetVersionVO")
    public Object getCommissionProfileSetVersionVO() {
        return commissionProfileSetVersionVO;
    }

    @JsonProperty("commissionProfileSetVersionVO")
    public void setCommissionProfileSetVersionVO(Object commissionProfileSetVersionVO) {
        this.commissionProfileSetVersionVO = commissionProfileSetVersionVO;
    }

    @JsonProperty("commissionList")
    public List<CommissionList> getCommissionList() {
        return commissionList;
    }

    @JsonProperty("commissionList")
    public void setCommissionList(List<CommissionList> commissionList) {
        this.commissionList = commissionList;
    }

    @JsonProperty("addProfileDetailList")
    public Object getAddProfileDetailList() {
        return addProfileDetailList;
    }

    @JsonProperty("addProfileDetailList")
    public void setAddProfileDetailList(Object addProfileDetailList) {
        this.addProfileDetailList = addProfileDetailList;
    }

    @JsonProperty("additionalProfileDeatilsVO")
    public Object getAdditionalProfileDeatilsVO() {
        return additionalProfileDeatilsVO;
    }

    @JsonProperty("additionalProfileDeatilsVO")
    public void setAdditionalProfileDeatilsVO(Object additionalProfileDeatilsVO) {
        this.additionalProfileDeatilsVO = additionalProfileDeatilsVO;
    }

    @JsonProperty("additionalProfileServicesVO")
    public Object getAdditionalProfileServicesVO() {
        return additionalProfileServicesVO;
    }

    @JsonProperty("additionalProfileServicesVO")
    public void setAdditionalProfileServicesVO(Object additionalProfileServicesVO) {
        this.additionalProfileServicesVO = additionalProfileServicesVO;
    }

    @JsonProperty("additionalList")
    public Object getAdditionalList() {
        return additionalList;
    }

    @JsonProperty("additionalList")
    public void setAdditionalList(Object additionalList) {
        this.additionalList = additionalList;
    }

    @JsonProperty("otfProfileList")
    public List<OtfProfileList> getOtfProfileList() {
        return otfProfileList;
    }

    @JsonProperty("otfProfileList")
    public void setOtfProfileList(List<OtfProfileList> otfProfileList) {
        this.otfProfileList = otfProfileList;
    }

    @JsonProperty("sequenceNo")
    public String getSequenceNo() {
        return sequenceNo;
    }

    @JsonProperty("sequenceNo")
    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
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
