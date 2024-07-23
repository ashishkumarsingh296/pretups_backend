
package restassuredapi.pojo.selfprofilethresholdresponsepojo;

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
    "lastSOSTxnID",
    "lastSOSTxnStatus",
    "lastLrStatus",
    "lastLRTxnID",
    "sosAllowedAmount",
    "lastSOSProductCode",
    "lastTransferID",
    "lastTransferDate",
    "outsideLastInTime",
    "outsideLastOutTime",
    "userID",
    "lastInTime",
    "lastOutTime",
    "lastModifiedTime",
    "weeklyInCount",
    "dailyOutCount",
    "dailyInCount",
    "weeklyInValue",
    "updateRecord",
    "category",
    "profileName",
    "dailyOutValue",
    "isDefault",
    "isDefaultDesc",
    "dailyInValue",
    "networkCode",
    "description",
    "createdBy",
    "modifiedBy",
    "createdOn",
    "modifiedOn",
    "status",
    "dailyC2STransferOutCount",
    "dailyC2STransferOutValue",
    "weeklyC2STransferOutValue",
    "monthlyC2STransferOutCount",
    "monthlyC2STransferOutValue",
    "weeklyC2STransferOutCount",
    "unctrlMonthlyInCount",
    "unctrlMonthlyOutValue",
    "weeklyOutCount",
    "monthlyInCount",
    "unctrlWeeklyInValue",
    "dailyRoamAmount",
    "profileStatusName",
    "weeklyOutValue",
    "profileProductList",
    "unctrlWeeklyOutValue",
    "monthlyOutCount",
    "monthlyOutValue",
    "unctrlMonthlyInValue",
    "unctrlMonthlyOutCount",
    "unctrlWeeklyOutCount",
    "unctrlTransferFlag",
    "unctrlDailyOutValue",
    "unctrlWeeklyInCount",
    "monthlyInValue",
    "unctrlDailyInCount",
    "unctrlDailyInValue",
    "unctrlDailyOutCount",
    "weeklySubscriberOutValue",
    "dailyInAltCount",
    "dailyInAltValue",
    "monthlySubscriberOutCount",
    "dailyOutAltCount",
    "dailyOutAltValue",
    "dailySubscriberOutAltCount",
    "weeklySubscriberOutCount",
    "dailySubscriberOutCount",
    "monthlySubscriberOutValue",
    "dailySubscriberOutValue",
    "monthlySubscriberOutAltCount",
    "monthlyOutAltValue",
    "unctrlDailyOutAltValue",
    "unctrlMonthlyInAltValue",
    "unctrlMonthlyInAltCount",
    "unctrlMonthlyOutAltCount",
    "dailySubscriberOutAltValue",
    "monthlyInAltCount",
    "weeklySubscriberOutAltCount",
    "weeklyOutAltCount",
    "weeklyInAltCount",
    "monthlyInAltValue",
    "weeklySubscriberOutAltValue",
    "parentProfileID",
    "weeklyOutAltValue",
    "dailySubscriberInCount",
    "weeklySubscriberInValue",
    "monthlySubscriberInAltValue",
    "weeklySubscriberInAltValue",
    "unctrlWeeklyInAltValue",
    "unctrlWeeklyInAltCount",
    "dailySubscriberInValue",
    "weeklySubscriberInCount",
    "monthlySubscriberInValue",
    "dailySubscriberInAltValue",
    "dailySubscriberInAltCount",
    "unctrlMonthlyOutAltValue",
    "unctrlWeeklyOutAltCount",
    "unctrlDailyInAltValue",
    "monthlySubscriberOutAltValue",
    "unctrlWeeklyOutAltValue",
    "weeklyInAltValue",
    "unctrlDailyInAltCount",
    "monthlySubscriberInCount",
    "monthlyOutAltCount",
    "unctrlDailyOutAltCount",
    "weeklySubscriberInAltCount",
    "monthlySubscriberInAltCount",
    "categoryName",
    "shortName",
    "profileId"
})
public class UserTransferCountsVO {

    @JsonProperty("lastSOSTxnID")
    private Object lastSOSTxnID;
    @JsonProperty("lastSOSTxnStatus")
    private Object lastSOSTxnStatus;
    @JsonProperty("lastLrStatus")
    private Object lastLrStatus;
    @JsonProperty("lastLRTxnID")
    private Object lastLRTxnID;
    @JsonProperty("sosAllowedAmount")
    private Long sosAllowedAmount;
    @JsonProperty("lastSOSProductCode")
    private Object lastSOSProductCode;
    @JsonProperty("lastTransferID")
    private String lastTransferID;
    @JsonProperty("lastTransferDate")
    private Long lastTransferDate;
    @JsonProperty("outsideLastInTime")
    private Object outsideLastInTime;
    @JsonProperty("outsideLastOutTime")
    private Object outsideLastOutTime;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("lastInTime")
    private Long lastInTime;
    @JsonProperty("lastOutTime")
    private Long lastOutTime;
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
    @JsonProperty("weeklyInCount")
    private Long weeklyInCount;
    @JsonProperty("dailyOutCount")
    private Long dailyOutCount;
    @JsonProperty("dailyInCount")
    private Long dailyInCount;
    @JsonProperty("weeklyInValue")
    private Long weeklyInValue;
    @JsonProperty("updateRecord")
    private Boolean updateRecord;
    @JsonProperty("category")
    private Object category;
    @JsonProperty("profileName")
    private Object profileName;
    @JsonProperty("dailyOutValue")
    private Long dailyOutValue;
    @JsonProperty("isDefault")
    private Object isDefault;
    @JsonProperty("isDefaultDesc")
    private String isDefaultDesc;
    @JsonProperty("dailyInValue")
    private Long dailyInValue;
    @JsonProperty("networkCode")
    private Object networkCode;
    @JsonProperty("description")
    private Object description;
    @JsonProperty("createdBy")
    private Object createdBy;
    @JsonProperty("modifiedBy")
    private Object modifiedBy;
    @JsonProperty("createdOn")
    private Object createdOn;
    @JsonProperty("modifiedOn")
    private Object modifiedOn;
    @JsonProperty("status")
    private Object status;
    @JsonProperty("dailyC2STransferOutCount")
    private Long dailyC2STransferOutCount;
    @JsonProperty("dailyC2STransferOutValue")
    private Long dailyC2STransferOutValue;
    @JsonProperty("weeklyC2STransferOutValue")
    private Long weeklyC2STransferOutValue;
    @JsonProperty("monthlyC2STransferOutCount")
    private Long monthlyC2STransferOutCount;
    @JsonProperty("monthlyC2STransferOutValue")
    private Long monthlyC2STransferOutValue;
    @JsonProperty("weeklyC2STransferOutCount")
    private Long weeklyC2STransferOutCount;
    @JsonProperty("unctrlMonthlyInCount")
    private Long unctrlMonthlyInCount;
    @JsonProperty("unctrlMonthlyOutValue")
    private Long unctrlMonthlyOutValue;
    @JsonProperty("weeklyOutCount")
    private Long weeklyOutCount;
    @JsonProperty("monthlyInCount")
    private Long monthlyInCount;
    @JsonProperty("unctrlWeeklyInValue")
    private Long unctrlWeeklyInValue;
    @JsonProperty("dailyRoamAmount")
    private Long dailyRoamAmount;
    @JsonProperty("profileStatusName")
    private Object profileStatusName;
    @JsonProperty("weeklyOutValue")
    private Long weeklyOutValue;
    @JsonProperty("profileProductList")
    private Object profileProductList;
    @JsonProperty("unctrlWeeklyOutValue")
    private Long unctrlWeeklyOutValue;
    @JsonProperty("monthlyOutCount")
    private Long monthlyOutCount;
    @JsonProperty("monthlyOutValue")
    private Long monthlyOutValue;
    @JsonProperty("unctrlMonthlyInValue")
    private Long unctrlMonthlyInValue;
    @JsonProperty("unctrlMonthlyOutCount")
    private Long unctrlMonthlyOutCount;
    @JsonProperty("unctrlWeeklyOutCount")
    private Long unctrlWeeklyOutCount;
    @JsonProperty("unctrlTransferFlag")
    private Boolean unctrlTransferFlag;
    @JsonProperty("unctrlDailyOutValue")
    private Long unctrlDailyOutValue;
    @JsonProperty("unctrlWeeklyInCount")
    private Long unctrlWeeklyInCount;
    @JsonProperty("monthlyInValue")
    private Long monthlyInValue;
    @JsonProperty("unctrlDailyInCount")
    private Long unctrlDailyInCount;
    @JsonProperty("unctrlDailyInValue")
    private Long unctrlDailyInValue;
    @JsonProperty("unctrlDailyOutCount")
    private Long unctrlDailyOutCount;
    @JsonProperty("weeklySubscriberOutValue")
    private Long weeklySubscriberOutValue;
    @JsonProperty("dailyInAltCount")
    private Long dailyInAltCount;
    @JsonProperty("dailyInAltValue")
    private Long dailyInAltValue;
    @JsonProperty("monthlySubscriberOutCount")
    private Long monthlySubscriberOutCount;
    @JsonProperty("dailyOutAltCount")
    private Long dailyOutAltCount;
    @JsonProperty("dailyOutAltValue")
    private Long dailyOutAltValue;
    @JsonProperty("dailySubscriberOutAltCount")
    private Long dailySubscriberOutAltCount;
    @JsonProperty("weeklySubscriberOutCount")
    private Long weeklySubscriberOutCount;
    @JsonProperty("dailySubscriberOutCount")
    private Long dailySubscriberOutCount;
    @JsonProperty("monthlySubscriberOutValue")
    private Long monthlySubscriberOutValue;
    @JsonProperty("dailySubscriberOutValue")
    private Long dailySubscriberOutValue;
    @JsonProperty("monthlySubscriberOutAltCount")
    private Long monthlySubscriberOutAltCount;
    @JsonProperty("monthlyOutAltValue")
    private Long monthlyOutAltValue;
    @JsonProperty("unctrlDailyOutAltValue")
    private Long unctrlDailyOutAltValue;
    @JsonProperty("unctrlMonthlyInAltValue")
    private Long unctrlMonthlyInAltValue;
    @JsonProperty("unctrlMonthlyInAltCount")
    private Long unctrlMonthlyInAltCount;
    @JsonProperty("unctrlMonthlyOutAltCount")
    private Long unctrlMonthlyOutAltCount;
    @JsonProperty("dailySubscriberOutAltValue")
    private Long dailySubscriberOutAltValue;
    @JsonProperty("monthlyInAltCount")
    private Long monthlyInAltCount;
    @JsonProperty("weeklySubscriberOutAltCount")
    private Long weeklySubscriberOutAltCount;
    @JsonProperty("weeklyOutAltCount")
    private Long weeklyOutAltCount;
    @JsonProperty("weeklyInAltCount")
    private Long weeklyInAltCount;
    @JsonProperty("monthlyInAltValue")
    private Long monthlyInAltValue;
    @JsonProperty("weeklySubscriberOutAltValue")
    private Long weeklySubscriberOutAltValue;
    @JsonProperty("parentProfileID")
    private Object parentProfileID;
    @JsonProperty("weeklyOutAltValue")
    private Long weeklyOutAltValue;
    @JsonProperty("dailySubscriberInCount")
    private Long dailySubscriberInCount;
    @JsonProperty("weeklySubscriberInValue")
    private Long weeklySubscriberInValue;
    @JsonProperty("monthlySubscriberInAltValue")
    private Long monthlySubscriberInAltValue;
    @JsonProperty("weeklySubscriberInAltValue")
    private Long weeklySubscriberInAltValue;
    @JsonProperty("unctrlWeeklyInAltValue")
    private Long unctrlWeeklyInAltValue;
    @JsonProperty("unctrlWeeklyInAltCount")
    private Long unctrlWeeklyInAltCount;
    @JsonProperty("dailySubscriberInValue")
    private Long dailySubscriberInValue;
    @JsonProperty("weeklySubscriberInCount")
    private Long weeklySubscriberInCount;
    @JsonProperty("monthlySubscriberInValue")
    private Long monthlySubscriberInValue;
    @JsonProperty("dailySubscriberInAltValue")
    private Long dailySubscriberInAltValue;
    @JsonProperty("dailySubscriberInAltCount")
    private Long dailySubscriberInAltCount;
    @JsonProperty("unctrlMonthlyOutAltValue")
    private Long unctrlMonthlyOutAltValue;
    @JsonProperty("unctrlWeeklyOutAltCount")
    private Long unctrlWeeklyOutAltCount;
    @JsonProperty("unctrlDailyInAltValue")
    private Long unctrlDailyInAltValue;
    @JsonProperty("monthlySubscriberOutAltValue")
    private Long monthlySubscriberOutAltValue;
    @JsonProperty("unctrlWeeklyOutAltValue")
    private Long unctrlWeeklyOutAltValue;
    @JsonProperty("weeklyInAltValue")
    private Long weeklyInAltValue;
    @JsonProperty("unctrlDailyInAltCount")
    private Long unctrlDailyInAltCount;
    @JsonProperty("monthlySubscriberInCount")
    private Long monthlySubscriberInCount;
    @JsonProperty("monthlyOutAltCount")
    private Long monthlyOutAltCount;
    @JsonProperty("unctrlDailyOutAltCount")
    private Long unctrlDailyOutAltCount;
    @JsonProperty("weeklySubscriberInAltCount")
    private Long weeklySubscriberInAltCount;
    @JsonProperty("monthlySubscriberInAltCount")
    private Long monthlySubscriberInAltCount;
    @JsonProperty("categoryName")
    private Object categoryName;
    @JsonProperty("shortName")
    private Object shortName;
    @JsonProperty("profileId")
    private Object profileId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("lastSOSTxnID")
    public Object getLastSOSTxnID() {
        return lastSOSTxnID;
    }

    @JsonProperty("lastSOSTxnID")
    public void setLastSOSTxnID(Object lastSOSTxnID) {
        this.lastSOSTxnID = lastSOSTxnID;
    }

    @JsonProperty("lastSOSTxnStatus")
    public Object getLastSOSTxnStatus() {
        return lastSOSTxnStatus;
    }

    @JsonProperty("lastSOSTxnStatus")
    public void setLastSOSTxnStatus(Object lastSOSTxnStatus) {
        this.lastSOSTxnStatus = lastSOSTxnStatus;
    }

    @JsonProperty("lastLrStatus")
    public Object getLastLrStatus() {
        return lastLrStatus;
    }

    @JsonProperty("lastLrStatus")
    public void setLastLrStatus(Object lastLrStatus) {
        this.lastLrStatus = lastLrStatus;
    }

    @JsonProperty("lastLRTxnID")
    public Object getLastLRTxnID() {
        return lastLRTxnID;
    }

    @JsonProperty("lastLRTxnID")
    public void setLastLRTxnID(Object lastLRTxnID) {
        this.lastLRTxnID = lastLRTxnID;
    }

    @JsonProperty("sosAllowedAmount")
    public Long getSosAllowedAmount() {
        return sosAllowedAmount;
    }

    @JsonProperty("sosAllowedAmount")
    public void setSosAllowedAmount(Long sosAllowedAmount) {
        this.sosAllowedAmount = sosAllowedAmount;
    }

    @JsonProperty("lastSOSProductCode")
    public Object getLastSOSProductCode() {
        return lastSOSProductCode;
    }

    @JsonProperty("lastSOSProductCode")
    public void setLastSOSProductCode(Object lastSOSProductCode) {
        this.lastSOSProductCode = lastSOSProductCode;
    }

    @JsonProperty("lastTransferID")
    public String getLastTransferID() {
        return lastTransferID;
    }

    @JsonProperty("lastTransferID")
    public void setLastTransferID(String lastTransferID) {
        this.lastTransferID = lastTransferID;
    }

    @JsonProperty("lastTransferDate")
    public Long getLastTransferDate() {
        return lastTransferDate;
    }

    @JsonProperty("lastTransferDate")
    public void setLastTransferDate(Long lastTransferDate) {
        this.lastTransferDate = lastTransferDate;
    }

    @JsonProperty("outsideLastInTime")
    public Object getOutsideLastInTime() {
        return outsideLastInTime;
    }

    @JsonProperty("outsideLastInTime")
    public void setOutsideLastInTime(Object outsideLastInTime) {
        this.outsideLastInTime = outsideLastInTime;
    }

    @JsonProperty("outsideLastOutTime")
    public Object getOutsideLastOutTime() {
        return outsideLastOutTime;
    }

    @JsonProperty("outsideLastOutTime")
    public void setOutsideLastOutTime(Object outsideLastOutTime) {
        this.outsideLastOutTime = outsideLastOutTime;
    }

    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @JsonProperty("lastInTime")
    public Long getLastInTime() {
        return lastInTime;
    }

    @JsonProperty("lastInTime")
    public void setLastInTime(Long lastInTime) {
        this.lastInTime = lastInTime;
    }

    @JsonProperty("lastOutTime")
    public Long getLastOutTime() {
        return lastOutTime;
    }

    @JsonProperty("lastOutTime")
    public void setLastOutTime(Long lastOutTime) {
        this.lastOutTime = lastOutTime;
    }

    @JsonProperty("lastModifiedTime")
    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    @JsonProperty("lastModifiedTime")
    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @JsonProperty("weeklyInCount")
    public Long getWeeklyInCount() {
        return weeklyInCount;
    }

    @JsonProperty("weeklyInCount")
    public void setWeeklyInCount(Long weeklyInCount) {
        this.weeklyInCount = weeklyInCount;
    }

    @JsonProperty("dailyOutCount")
    public Long getDailyOutCount() {
        return dailyOutCount;
    }

    @JsonProperty("dailyOutCount")
    public void setDailyOutCount(Long dailyOutCount) {
        this.dailyOutCount = dailyOutCount;
    }

    @JsonProperty("dailyInCount")
    public Long getDailyInCount() {
        return dailyInCount;
    }

    @JsonProperty("dailyInCount")
    public void setDailyInCount(Long dailyInCount) {
        this.dailyInCount = dailyInCount;
    }

    @JsonProperty("weeklyInValue")
    public Long getWeeklyInValue() {
        return weeklyInValue;
    }

    @JsonProperty("weeklyInValue")
    public void setWeeklyInValue(Long weeklyInValue) {
        this.weeklyInValue = weeklyInValue;
    }

    @JsonProperty("updateRecord")
    public Boolean getUpdateRecord() {
        return updateRecord;
    }

    @JsonProperty("updateRecord")
    public void setUpdateRecord(Boolean updateRecord) {
        this.updateRecord = updateRecord;
    }

    @JsonProperty("category")
    public Object getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(Object category) {
        this.category = category;
    }

    @JsonProperty("profileName")
    public Object getProfileName() {
        return profileName;
    }

    @JsonProperty("profileName")
    public void setProfileName(Object profileName) {
        this.profileName = profileName;
    }

    @JsonProperty("dailyOutValue")
    public Long getDailyOutValue() {
        return dailyOutValue;
    }

    @JsonProperty("dailyOutValue")
    public void setDailyOutValue(Long dailyOutValue) {
        this.dailyOutValue = dailyOutValue;
    }

    @JsonProperty("isDefault")
    public Object getIsDefault() {
        return isDefault;
    }

    @JsonProperty("isDefault")
    public void setIsDefault(Object isDefault) {
        this.isDefault = isDefault;
    }

    @JsonProperty("isDefaultDesc")
    public String getIsDefaultDesc() {
        return isDefaultDesc;
    }

    @JsonProperty("isDefaultDesc")
    public void setIsDefaultDesc(String isDefaultDesc) {
        this.isDefaultDesc = isDefaultDesc;
    }

    @JsonProperty("dailyInValue")
    public Long getDailyInValue() {
        return dailyInValue;
    }

    @JsonProperty("dailyInValue")
    public void setDailyInValue(Long dailyInValue) {
        this.dailyInValue = dailyInValue;
    }

    @JsonProperty("networkCode")
    public Object getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(Object networkCode) {
        this.networkCode = networkCode;
    }

    @JsonProperty("description")
    public Object getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Object description) {
        this.description = description;
    }

    @JsonProperty("createdBy")
    public Object getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("modifiedBy")
    public Object getModifiedBy() {
        return modifiedBy;
    }

    @JsonProperty("modifiedBy")
    public void setModifiedBy(Object modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("createdOn")
    public Object getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("modifiedOn")
    public Object getModifiedOn() {
        return modifiedOn;
    }

    @JsonProperty("modifiedOn")
    public void setModifiedOn(Object modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("status")
    public Object getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Object status) {
        this.status = status;
    }

    @JsonProperty("dailyC2STransferOutCount")
    public Long getDailyC2STransferOutCount() {
        return dailyC2STransferOutCount;
    }

    @JsonProperty("dailyC2STransferOutCount")
    public void setDailyC2STransferOutCount(Long dailyC2STransferOutCount) {
        this.dailyC2STransferOutCount = dailyC2STransferOutCount;
    }

    @JsonProperty("dailyC2STransferOutValue")
    public Long getDailyC2STransferOutValue() {
        return dailyC2STransferOutValue;
    }

    @JsonProperty("dailyC2STransferOutValue")
    public void setDailyC2STransferOutValue(Long dailyC2STransferOutValue) {
        this.dailyC2STransferOutValue = dailyC2STransferOutValue;
    }

    @JsonProperty("weeklyC2STransferOutValue")
    public Long getWeeklyC2STransferOutValue() {
        return weeklyC2STransferOutValue;
    }

    @JsonProperty("weeklyC2STransferOutValue")
    public void setWeeklyC2STransferOutValue(Long weeklyC2STransferOutValue) {
        this.weeklyC2STransferOutValue = weeklyC2STransferOutValue;
    }

    @JsonProperty("monthlyC2STransferOutCount")
    public Long getMonthlyC2STransferOutCount() {
        return monthlyC2STransferOutCount;
    }

    @JsonProperty("monthlyC2STransferOutCount")
    public void setMonthlyC2STransferOutCount(Long monthlyC2STransferOutCount) {
        this.monthlyC2STransferOutCount = monthlyC2STransferOutCount;
    }

    @JsonProperty("monthlyC2STransferOutValue")
    public Long getMonthlyC2STransferOutValue() {
        return monthlyC2STransferOutValue;
    }

    @JsonProperty("monthlyC2STransferOutValue")
    public void setMonthlyC2STransferOutValue(Long monthlyC2STransferOutValue) {
        this.monthlyC2STransferOutValue = monthlyC2STransferOutValue;
    }

    @JsonProperty("weeklyC2STransferOutCount")
    public Long getWeeklyC2STransferOutCount() {
        return weeklyC2STransferOutCount;
    }

    @JsonProperty("weeklyC2STransferOutCount")
    public void setWeeklyC2STransferOutCount(Long weeklyC2STransferOutCount) {
        this.weeklyC2STransferOutCount = weeklyC2STransferOutCount;
    }

    @JsonProperty("unctrlMonthlyInCount")
    public Long getUnctrlMonthlyInCount() {
        return unctrlMonthlyInCount;
    }

    @JsonProperty("unctrlMonthlyInCount")
    public void setUnctrlMonthlyInCount(Long unctrlMonthlyInCount) {
        this.unctrlMonthlyInCount = unctrlMonthlyInCount;
    }

    @JsonProperty("unctrlMonthlyOutValue")
    public Long getUnctrlMonthlyOutValue() {
        return unctrlMonthlyOutValue;
    }

    @JsonProperty("unctrlMonthlyOutValue")
    public void setUnctrlMonthlyOutValue(Long unctrlMonthlyOutValue) {
        this.unctrlMonthlyOutValue = unctrlMonthlyOutValue;
    }

    @JsonProperty("weeklyOutCount")
    public Long getWeeklyOutCount() {
        return weeklyOutCount;
    }

    @JsonProperty("weeklyOutCount")
    public void setWeeklyOutCount(Long weeklyOutCount) {
        this.weeklyOutCount = weeklyOutCount;
    }

    @JsonProperty("monthlyInCount")
    public Long getMonthlyInCount() {
        return monthlyInCount;
    }

    @JsonProperty("monthlyInCount")
    public void setMonthlyInCount(Long monthlyInCount) {
        this.monthlyInCount = monthlyInCount;
    }

    @JsonProperty("unctrlWeeklyInValue")
    public Long getUnctrlWeeklyInValue() {
        return unctrlWeeklyInValue;
    }

    @JsonProperty("unctrlWeeklyInValue")
    public void setUnctrlWeeklyInValue(Long unctrlWeeklyInValue) {
        this.unctrlWeeklyInValue = unctrlWeeklyInValue;
    }

    @JsonProperty("dailyRoamAmount")
    public Long getDailyRoamAmount() {
        return dailyRoamAmount;
    }

    @JsonProperty("dailyRoamAmount")
    public void setDailyRoamAmount(Long dailyRoamAmount) {
        this.dailyRoamAmount = dailyRoamAmount;
    }

    @JsonProperty("profileStatusName")
    public Object getProfileStatusName() {
        return profileStatusName;
    }

    @JsonProperty("profileStatusName")
    public void setProfileStatusName(Object profileStatusName) {
        this.profileStatusName = profileStatusName;
    }

    @JsonProperty("weeklyOutValue")
    public Long getWeeklyOutValue() {
        return weeklyOutValue;
    }

    @JsonProperty("weeklyOutValue")
    public void setWeeklyOutValue(Long weeklyOutValue) {
        this.weeklyOutValue = weeklyOutValue;
    }

    @JsonProperty("profileProductList")
    public Object getProfileProductList() {
        return profileProductList;
    }

    @JsonProperty("profileProductList")
    public void setProfileProductList(Object profileProductList) {
        this.profileProductList = profileProductList;
    }

    @JsonProperty("unctrlWeeklyOutValue")
    public Long getUnctrlWeeklyOutValue() {
        return unctrlWeeklyOutValue;
    }

    @JsonProperty("unctrlWeeklyOutValue")
    public void setUnctrlWeeklyOutValue(Long unctrlWeeklyOutValue) {
        this.unctrlWeeklyOutValue = unctrlWeeklyOutValue;
    }

    @JsonProperty("monthlyOutCount")
    public Long getMonthlyOutCount() {
        return monthlyOutCount;
    }

    @JsonProperty("monthlyOutCount")
    public void setMonthlyOutCount(Long monthlyOutCount) {
        this.monthlyOutCount = monthlyOutCount;
    }

    @JsonProperty("monthlyOutValue")
    public Long getMonthlyOutValue() {
        return monthlyOutValue;
    }

    @JsonProperty("monthlyOutValue")
    public void setMonthlyOutValue(Long monthlyOutValue) {
        this.monthlyOutValue = monthlyOutValue;
    }

    @JsonProperty("unctrlMonthlyInValue")
    public Long getUnctrlMonthlyInValue() {
        return unctrlMonthlyInValue;
    }

    @JsonProperty("unctrlMonthlyInValue")
    public void setUnctrlMonthlyInValue(Long unctrlMonthlyInValue) {
        this.unctrlMonthlyInValue = unctrlMonthlyInValue;
    }

    @JsonProperty("unctrlMonthlyOutCount")
    public Long getUnctrlMonthlyOutCount() {
        return unctrlMonthlyOutCount;
    }

    @JsonProperty("unctrlMonthlyOutCount")
    public void setUnctrlMonthlyOutCount(Long unctrlMonthlyOutCount) {
        this.unctrlMonthlyOutCount = unctrlMonthlyOutCount;
    }

    @JsonProperty("unctrlWeeklyOutCount")
    public Long getUnctrlWeeklyOutCount() {
        return unctrlWeeklyOutCount;
    }

    @JsonProperty("unctrlWeeklyOutCount")
    public void setUnctrlWeeklyOutCount(Long unctrlWeeklyOutCount) {
        this.unctrlWeeklyOutCount = unctrlWeeklyOutCount;
    }

    @JsonProperty("unctrlTransferFlag")
    public Boolean getUnctrlTransferFlag() {
        return unctrlTransferFlag;
    }

    @JsonProperty("unctrlTransferFlag")
    public void setUnctrlTransferFlag(Boolean unctrlTransferFlag) {
        this.unctrlTransferFlag = unctrlTransferFlag;
    }

    @JsonProperty("unctrlDailyOutValue")
    public Long getUnctrlDailyOutValue() {
        return unctrlDailyOutValue;
    }

    @JsonProperty("unctrlDailyOutValue")
    public void setUnctrlDailyOutValue(Long unctrlDailyOutValue) {
        this.unctrlDailyOutValue = unctrlDailyOutValue;
    }

    @JsonProperty("unctrlWeeklyInCount")
    public Long getUnctrlWeeklyInCount() {
        return unctrlWeeklyInCount;
    }

    @JsonProperty("unctrlWeeklyInCount")
    public void setUnctrlWeeklyInCount(Long unctrlWeeklyInCount) {
        this.unctrlWeeklyInCount = unctrlWeeklyInCount;
    }

    @JsonProperty("monthlyInValue")
    public Long getMonthlyInValue() {
        return monthlyInValue;
    }

    @JsonProperty("monthlyInValue")
    public void setMonthlyInValue(Long monthlyInValue) {
        this.monthlyInValue = monthlyInValue;
    }

    @JsonProperty("unctrlDailyInCount")
    public Long getUnctrlDailyInCount() {
        return unctrlDailyInCount;
    }

    @JsonProperty("unctrlDailyInCount")
    public void setUnctrlDailyInCount(Long unctrlDailyInCount) {
        this.unctrlDailyInCount = unctrlDailyInCount;
    }

    @JsonProperty("unctrlDailyInValue")
    public Long getUnctrlDailyInValue() {
        return unctrlDailyInValue;
    }

    @JsonProperty("unctrlDailyInValue")
    public void setUnctrlDailyInValue(Long unctrlDailyInValue) {
        this.unctrlDailyInValue = unctrlDailyInValue;
    }

    @JsonProperty("unctrlDailyOutCount")
    public Long getUnctrlDailyOutCount() {
        return unctrlDailyOutCount;
    }

    @JsonProperty("unctrlDailyOutCount")
    public void setUnctrlDailyOutCount(Long unctrlDailyOutCount) {
        this.unctrlDailyOutCount = unctrlDailyOutCount;
    }

    @JsonProperty("weeklySubscriberOutValue")
    public Long getWeeklySubscriberOutValue() {
        return weeklySubscriberOutValue;
    }

    @JsonProperty("weeklySubscriberOutValue")
    public void setWeeklySubscriberOutValue(Long weeklySubscriberOutValue) {
        this.weeklySubscriberOutValue = weeklySubscriberOutValue;
    }

    @JsonProperty("dailyInAltCount")
    public Long getDailyInAltCount() {
        return dailyInAltCount;
    }

    @JsonProperty("dailyInAltCount")
    public void setDailyInAltCount(Long dailyInAltCount) {
        this.dailyInAltCount = dailyInAltCount;
    }

    @JsonProperty("dailyInAltValue")
    public Long getDailyInAltValue() {
        return dailyInAltValue;
    }

    @JsonProperty("dailyInAltValue")
    public void setDailyInAltValue(Long dailyInAltValue) {
        this.dailyInAltValue = dailyInAltValue;
    }

    @JsonProperty("monthlySubscriberOutCount")
    public Long getMonthlySubscriberOutCount() {
        return monthlySubscriberOutCount;
    }

    @JsonProperty("monthlySubscriberOutCount")
    public void setMonthlySubscriberOutCount(Long monthlySubscriberOutCount) {
        this.monthlySubscriberOutCount = monthlySubscriberOutCount;
    }

    @JsonProperty("dailyOutAltCount")
    public Long getDailyOutAltCount() {
        return dailyOutAltCount;
    }

    @JsonProperty("dailyOutAltCount")
    public void setDailyOutAltCount(Long dailyOutAltCount) {
        this.dailyOutAltCount = dailyOutAltCount;
    }

    @JsonProperty("dailyOutAltValue")
    public Long getDailyOutAltValue() {
        return dailyOutAltValue;
    }

    @JsonProperty("dailyOutAltValue")
    public void setDailyOutAltValue(Long dailyOutAltValue) {
        this.dailyOutAltValue = dailyOutAltValue;
    }

    @JsonProperty("dailySubscriberOutAltCount")
    public Long getDailySubscriberOutAltCount() {
        return dailySubscriberOutAltCount;
    }

    @JsonProperty("dailySubscriberOutAltCount")
    public void setDailySubscriberOutAltCount(Long dailySubscriberOutAltCount) {
        this.dailySubscriberOutAltCount = dailySubscriberOutAltCount;
    }

    @JsonProperty("weeklySubscriberOutCount")
    public Long getWeeklySubscriberOutCount() {
        return weeklySubscriberOutCount;
    }

    @JsonProperty("weeklySubscriberOutCount")
    public void setWeeklySubscriberOutCount(Long weeklySubscriberOutCount) {
        this.weeklySubscriberOutCount = weeklySubscriberOutCount;
    }

    @JsonProperty("dailySubscriberOutCount")
    public Long getDailySubscriberOutCount() {
        return dailySubscriberOutCount;
    }

    @JsonProperty("dailySubscriberOutCount")
    public void setDailySubscriberOutCount(Long dailySubscriberOutCount) {
        this.dailySubscriberOutCount = dailySubscriberOutCount;
    }

    @JsonProperty("monthlySubscriberOutValue")
    public Long getMonthlySubscriberOutValue() {
        return monthlySubscriberOutValue;
    }

    @JsonProperty("monthlySubscriberOutValue")
    public void setMonthlySubscriberOutValue(Long monthlySubscriberOutValue) {
        this.monthlySubscriberOutValue = monthlySubscriberOutValue;
    }

    @JsonProperty("dailySubscriberOutValue")
    public Long getDailySubscriberOutValue() {
        return dailySubscriberOutValue;
    }

    @JsonProperty("dailySubscriberOutValue")
    public void setDailySubscriberOutValue(Long dailySubscriberOutValue) {
        this.dailySubscriberOutValue = dailySubscriberOutValue;
    }

    @JsonProperty("monthlySubscriberOutAltCount")
    public Long getMonthlySubscriberOutAltCount() {
        return monthlySubscriberOutAltCount;
    }

    @JsonProperty("monthlySubscriberOutAltCount")
    public void setMonthlySubscriberOutAltCount(Long monthlySubscriberOutAltCount) {
        this.monthlySubscriberOutAltCount = monthlySubscriberOutAltCount;
    }

    @JsonProperty("monthlyOutAltValue")
    public Long getMonthlyOutAltValue() {
        return monthlyOutAltValue;
    }

    @JsonProperty("monthlyOutAltValue")
    public void setMonthlyOutAltValue(Long monthlyOutAltValue) {
        this.monthlyOutAltValue = monthlyOutAltValue;
    }

    @JsonProperty("unctrlDailyOutAltValue")
    public Long getUnctrlDailyOutAltValue() {
        return unctrlDailyOutAltValue;
    }

    @JsonProperty("unctrlDailyOutAltValue")
    public void setUnctrlDailyOutAltValue(Long unctrlDailyOutAltValue) {
        this.unctrlDailyOutAltValue = unctrlDailyOutAltValue;
    }

    @JsonProperty("unctrlMonthlyInAltValue")
    public Long getUnctrlMonthlyInAltValue() {
        return unctrlMonthlyInAltValue;
    }

    @JsonProperty("unctrlMonthlyInAltValue")
    public void setUnctrlMonthlyInAltValue(Long unctrlMonthlyInAltValue) {
        this.unctrlMonthlyInAltValue = unctrlMonthlyInAltValue;
    }

    @JsonProperty("unctrlMonthlyInAltCount")
    public Long getUnctrlMonthlyInAltCount() {
        return unctrlMonthlyInAltCount;
    }

    @JsonProperty("unctrlMonthlyInAltCount")
    public void setUnctrlMonthlyInAltCount(Long unctrlMonthlyInAltCount) {
        this.unctrlMonthlyInAltCount = unctrlMonthlyInAltCount;
    }

    @JsonProperty("unctrlMonthlyOutAltCount")
    public Long getUnctrlMonthlyOutAltCount() {
        return unctrlMonthlyOutAltCount;
    }

    @JsonProperty("unctrlMonthlyOutAltCount")
    public void setUnctrlMonthlyOutAltCount(Long unctrlMonthlyOutAltCount) {
        this.unctrlMonthlyOutAltCount = unctrlMonthlyOutAltCount;
    }

    @JsonProperty("dailySubscriberOutAltValue")
    public Long getDailySubscriberOutAltValue() {
        return dailySubscriberOutAltValue;
    }

    @JsonProperty("dailySubscriberOutAltValue")
    public void setDailySubscriberOutAltValue(Long dailySubscriberOutAltValue) {
        this.dailySubscriberOutAltValue = dailySubscriberOutAltValue;
    }

    @JsonProperty("monthlyInAltCount")
    public Long getMonthlyInAltCount() {
        return monthlyInAltCount;
    }

    @JsonProperty("monthlyInAltCount")
    public void setMonthlyInAltCount(Long monthlyInAltCount) {
        this.monthlyInAltCount = monthlyInAltCount;
    }

    @JsonProperty("weeklySubscriberOutAltCount")
    public Long getWeeklySubscriberOutAltCount() {
        return weeklySubscriberOutAltCount;
    }

    @JsonProperty("weeklySubscriberOutAltCount")
    public void setWeeklySubscriberOutAltCount(Long weeklySubscriberOutAltCount) {
        this.weeklySubscriberOutAltCount = weeklySubscriberOutAltCount;
    }

    @JsonProperty("weeklyOutAltCount")
    public Long getWeeklyOutAltCount() {
        return weeklyOutAltCount;
    }

    @JsonProperty("weeklyOutAltCount")
    public void setWeeklyOutAltCount(Long weeklyOutAltCount) {
        this.weeklyOutAltCount = weeklyOutAltCount;
    }

    @JsonProperty("weeklyInAltCount")
    public Long getWeeklyInAltCount() {
        return weeklyInAltCount;
    }

    @JsonProperty("weeklyInAltCount")
    public void setWeeklyInAltCount(Long weeklyInAltCount) {
        this.weeklyInAltCount = weeklyInAltCount;
    }

    @JsonProperty("monthlyInAltValue")
    public Long getMonthlyInAltValue() {
        return monthlyInAltValue;
    }

    @JsonProperty("monthlyInAltValue")
    public void setMonthlyInAltValue(Long monthlyInAltValue) {
        this.monthlyInAltValue = monthlyInAltValue;
    }

    @JsonProperty("weeklySubscriberOutAltValue")
    public Long getWeeklySubscriberOutAltValue() {
        return weeklySubscriberOutAltValue;
    }

    @JsonProperty("weeklySubscriberOutAltValue")
    public void setWeeklySubscriberOutAltValue(Long weeklySubscriberOutAltValue) {
        this.weeklySubscriberOutAltValue = weeklySubscriberOutAltValue;
    }

    @JsonProperty("parentProfileID")
    public Object getParentProfileID() {
        return parentProfileID;
    }

    @JsonProperty("parentProfileID")
    public void setParentProfileID(Object parentProfileID) {
        this.parentProfileID = parentProfileID;
    }

    @JsonProperty("weeklyOutAltValue")
    public Long getWeeklyOutAltValue() {
        return weeklyOutAltValue;
    }

    @JsonProperty("weeklyOutAltValue")
    public void setWeeklyOutAltValue(Long weeklyOutAltValue) {
        this.weeklyOutAltValue = weeklyOutAltValue;
    }

    @JsonProperty("dailySubscriberInCount")
    public Long getDailySubscriberInCount() {
        return dailySubscriberInCount;
    }

    @JsonProperty("dailySubscriberInCount")
    public void setDailySubscriberInCount(Long dailySubscriberInCount) {
        this.dailySubscriberInCount = dailySubscriberInCount;
    }

    @JsonProperty("weeklySubscriberInValue")
    public Long getWeeklySubscriberInValue() {
        return weeklySubscriberInValue;
    }

    @JsonProperty("weeklySubscriberInValue")
    public void setWeeklySubscriberInValue(Long weeklySubscriberInValue) {
        this.weeklySubscriberInValue = weeklySubscriberInValue;
    }

    @JsonProperty("monthlySubscriberInAltValue")
    public Long getMonthlySubscriberInAltValue() {
        return monthlySubscriberInAltValue;
    }

    @JsonProperty("monthlySubscriberInAltValue")
    public void setMonthlySubscriberInAltValue(Long monthlySubscriberInAltValue) {
        this.monthlySubscriberInAltValue = monthlySubscriberInAltValue;
    }

    @JsonProperty("weeklySubscriberInAltValue")
    public Long getWeeklySubscriberInAltValue() {
        return weeklySubscriberInAltValue;
    }

    @JsonProperty("weeklySubscriberInAltValue")
    public void setWeeklySubscriberInAltValue(Long weeklySubscriberInAltValue) {
        this.weeklySubscriberInAltValue = weeklySubscriberInAltValue;
    }

    @JsonProperty("unctrlWeeklyInAltValue")
    public Long getUnctrlWeeklyInAltValue() {
        return unctrlWeeklyInAltValue;
    }

    @JsonProperty("unctrlWeeklyInAltValue")
    public void setUnctrlWeeklyInAltValue(Long unctrlWeeklyInAltValue) {
        this.unctrlWeeklyInAltValue = unctrlWeeklyInAltValue;
    }

    @JsonProperty("unctrlWeeklyInAltCount")
    public Long getUnctrlWeeklyInAltCount() {
        return unctrlWeeklyInAltCount;
    }

    @JsonProperty("unctrlWeeklyInAltCount")
    public void setUnctrlWeeklyInAltCount(Long unctrlWeeklyInAltCount) {
        this.unctrlWeeklyInAltCount = unctrlWeeklyInAltCount;
    }

    @JsonProperty("dailySubscriberInValue")
    public Long getDailySubscriberInValue() {
        return dailySubscriberInValue;
    }

    @JsonProperty("dailySubscriberInValue")
    public void setDailySubscriberInValue(Long dailySubscriberInValue) {
        this.dailySubscriberInValue = dailySubscriberInValue;
    }

    @JsonProperty("weeklySubscriberInCount")
    public Long getWeeklySubscriberInCount() {
        return weeklySubscriberInCount;
    }

    @JsonProperty("weeklySubscriberInCount")
    public void setWeeklySubscriberInCount(Long weeklySubscriberInCount) {
        this.weeklySubscriberInCount = weeklySubscriberInCount;
    }

    @JsonProperty("monthlySubscriberInValue")
    public Long getMonthlySubscriberInValue() {
        return monthlySubscriberInValue;
    }

    @JsonProperty("monthlySubscriberInValue")
    public void setMonthlySubscriberInValue(Long monthlySubscriberInValue) {
        this.monthlySubscriberInValue = monthlySubscriberInValue;
    }

    @JsonProperty("dailySubscriberInAltValue")
    public Long getDailySubscriberInAltValue() {
        return dailySubscriberInAltValue;
    }

    @JsonProperty("dailySubscriberInAltValue")
    public void setDailySubscriberInAltValue(Long dailySubscriberInAltValue) {
        this.dailySubscriberInAltValue = dailySubscriberInAltValue;
    }

    @JsonProperty("dailySubscriberInAltCount")
    public Long getDailySubscriberInAltCount() {
        return dailySubscriberInAltCount;
    }

    @JsonProperty("dailySubscriberInAltCount")
    public void setDailySubscriberInAltCount(Long dailySubscriberInAltCount) {
        this.dailySubscriberInAltCount = dailySubscriberInAltCount;
    }

    @JsonProperty("unctrlMonthlyOutAltValue")
    public Long getUnctrlMonthlyOutAltValue() {
        return unctrlMonthlyOutAltValue;
    }

    @JsonProperty("unctrlMonthlyOutAltValue")
    public void setUnctrlMonthlyOutAltValue(Long unctrlMonthlyOutAltValue) {
        this.unctrlMonthlyOutAltValue = unctrlMonthlyOutAltValue;
    }

    @JsonProperty("unctrlWeeklyOutAltCount")
    public Long getUnctrlWeeklyOutAltCount() {
        return unctrlWeeklyOutAltCount;
    }

    @JsonProperty("unctrlWeeklyOutAltCount")
    public void setUnctrlWeeklyOutAltCount(Long unctrlWeeklyOutAltCount) {
        this.unctrlWeeklyOutAltCount = unctrlWeeklyOutAltCount;
    }

    @JsonProperty("unctrlDailyInAltValue")
    public Long getUnctrlDailyInAltValue() {
        return unctrlDailyInAltValue;
    }

    @JsonProperty("unctrlDailyInAltValue")
    public void setUnctrlDailyInAltValue(Long unctrlDailyInAltValue) {
        this.unctrlDailyInAltValue = unctrlDailyInAltValue;
    }

    @JsonProperty("monthlySubscriberOutAltValue")
    public Long getMonthlySubscriberOutAltValue() {
        return monthlySubscriberOutAltValue;
    }

    @JsonProperty("monthlySubscriberOutAltValue")
    public void setMonthlySubscriberOutAltValue(Long monthlySubscriberOutAltValue) {
        this.monthlySubscriberOutAltValue = monthlySubscriberOutAltValue;
    }

    @JsonProperty("unctrlWeeklyOutAltValue")
    public Long getUnctrlWeeklyOutAltValue() {
        return unctrlWeeklyOutAltValue;
    }

    @JsonProperty("unctrlWeeklyOutAltValue")
    public void setUnctrlWeeklyOutAltValue(Long unctrlWeeklyOutAltValue) {
        this.unctrlWeeklyOutAltValue = unctrlWeeklyOutAltValue;
    }

    @JsonProperty("weeklyInAltValue")
    public Long getWeeklyInAltValue() {
        return weeklyInAltValue;
    }

    @JsonProperty("weeklyInAltValue")
    public void setWeeklyInAltValue(Long weeklyInAltValue) {
        this.weeklyInAltValue = weeklyInAltValue;
    }

    @JsonProperty("unctrlDailyInAltCount")
    public Long getUnctrlDailyInAltCount() {
        return unctrlDailyInAltCount;
    }

    @JsonProperty("unctrlDailyInAltCount")
    public void setUnctrlDailyInAltCount(Long unctrlDailyInAltCount) {
        this.unctrlDailyInAltCount = unctrlDailyInAltCount;
    }

    @JsonProperty("monthlySubscriberInCount")
    public Long getMonthlySubscriberInCount() {
        return monthlySubscriberInCount;
    }

    @JsonProperty("monthlySubscriberInCount")
    public void setMonthlySubscriberInCount(Long monthlySubscriberInCount) {
        this.monthlySubscriberInCount = monthlySubscriberInCount;
    }

    @JsonProperty("monthlyOutAltCount")
    public Long getMonthlyOutAltCount() {
        return monthlyOutAltCount;
    }

    @JsonProperty("monthlyOutAltCount")
    public void setMonthlyOutAltCount(Long monthlyOutAltCount) {
        this.monthlyOutAltCount = monthlyOutAltCount;
    }

    @JsonProperty("unctrlDailyOutAltCount")
    public Long getUnctrlDailyOutAltCount() {
        return unctrlDailyOutAltCount;
    }

    @JsonProperty("unctrlDailyOutAltCount")
    public void setUnctrlDailyOutAltCount(Long unctrlDailyOutAltCount) {
        this.unctrlDailyOutAltCount = unctrlDailyOutAltCount;
    }

    @JsonProperty("weeklySubscriberInAltCount")
    public Long getWeeklySubscriberInAltCount() {
        return weeklySubscriberInAltCount;
    }

    @JsonProperty("weeklySubscriberInAltCount")
    public void setWeeklySubscriberInAltCount(Long weeklySubscriberInAltCount) {
        this.weeklySubscriberInAltCount = weeklySubscriberInAltCount;
    }

    @JsonProperty("monthlySubscriberInAltCount")
    public Long getMonthlySubscriberInAltCount() {
        return monthlySubscriberInAltCount;
    }

    @JsonProperty("monthlySubscriberInAltCount")
    public void setMonthlySubscriberInAltCount(Long monthlySubscriberInAltCount) {
        this.monthlySubscriberInAltCount = monthlySubscriberInAltCount;
    }

    @JsonProperty("categoryName")
    public Object getCategoryName() {
        return categoryName;
    }

    @JsonProperty("categoryName")
    public void setCategoryName(Object categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("shortName")
    public Object getShortName() {
        return shortName;
    }

    @JsonProperty("shortName")
    public void setShortName(Object shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("profileId")
    public Object getProfileId() {
        return profileId;
    }

    @JsonProperty("profileId")
    public void setProfileId(Object profileId) {
        this.profileId = profileId;
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
