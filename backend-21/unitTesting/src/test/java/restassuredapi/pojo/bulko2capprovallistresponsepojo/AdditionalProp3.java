package restassuredapi.pojo.bulko2capprovallistresponsepojo;

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
"batchDate",
"batchDateStr",
"batchFileName",
"batchId",
"batchName",
"batchTotalRecord",
"categoryCode",
"closedRecords",
"createdBy",
"createdOn",
"defaultLang",
"domainCode",
"domainCodeDesc",
"focBatchItemsVO",
"geographyList",
"level1ApprovedRecords",
"level2ApprovedRecords",
"modifiedBy",
"modifiedOn",
"networkCode",
"networkCodeFor",
"newRecords",
"productCode",
"productCodeDesc",
"productMrp",
"productMrpStr",
"productName",
"productShortName",
"productType",
"rejectedRecords",
"secondLang",
"status",
"statusDesc"
})
public class AdditionalProp3 {

@JsonProperty("batchDate")
private String batchDate;
@JsonProperty("batchDateStr")
private String batchDateStr;
@JsonProperty("batchFileName")
private String batchFileName;
@JsonProperty("batchId")
private String batchId;
@JsonProperty("batchName")
private String batchName;
@JsonProperty("batchTotalRecord")
private Integer batchTotalRecord;
@JsonProperty("categoryCode")
private String categoryCode;
@JsonProperty("closedRecords")
private Integer closedRecords;
@JsonProperty("createdBy")
private String createdBy;
@JsonProperty("createdOn")
private String createdOn;
@JsonProperty("defaultLang")
private String defaultLang;
@JsonProperty("domainCode")
private String domainCode;
@JsonProperty("domainCodeDesc")
private String domainCodeDesc;
@JsonProperty("focBatchItemsVO")
private FocBatchItemsVO__ focBatchItemsVO;
@JsonProperty("geographyList")
private List<GeographyList__> geographyList = null;
@JsonProperty("level1ApprovedRecords")
private Integer level1ApprovedRecords;
@JsonProperty("level2ApprovedRecords")
private Integer level2ApprovedRecords;
@JsonProperty("modifiedBy")
private String modifiedBy;
@JsonProperty("modifiedOn")
private String modifiedOn;
@JsonProperty("networkCode")
private String networkCode;
@JsonProperty("networkCodeFor")
private String networkCodeFor;
@JsonProperty("newRecords")
private Integer newRecords;
@JsonProperty("productCode")
private String productCode;
@JsonProperty("productCodeDesc")
private String productCodeDesc;
@JsonProperty("productMrp")
private Integer productMrp;
@JsonProperty("productMrpStr")
private String productMrpStr;
@JsonProperty("productName")
private String productName;
@JsonProperty("productShortName")
private String productShortName;
@JsonProperty("productType")
private String productType;
@JsonProperty("rejectedRecords")
private Integer rejectedRecords;
@JsonProperty("secondLang")
private String secondLang;
@JsonProperty("status")
private String status;
@JsonProperty("statusDesc")
private String statusDesc;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("batchDate")
public String getBatchDate() {
return batchDate;
}

@JsonProperty("batchDate")
public void setBatchDate(String batchDate) {
this.batchDate = batchDate;
}

@JsonProperty("batchDateStr")
public String getBatchDateStr() {
return batchDateStr;
}

@JsonProperty("batchDateStr")
public void setBatchDateStr(String batchDateStr) {
this.batchDateStr = batchDateStr;
}

@JsonProperty("batchFileName")
public String getBatchFileName() {
return batchFileName;
}

@JsonProperty("batchFileName")
public void setBatchFileName(String batchFileName) {
this.batchFileName = batchFileName;
}

@JsonProperty("batchId")
public String getBatchId() {
return batchId;
}

@JsonProperty("batchId")
public void setBatchId(String batchId) {
this.batchId = batchId;
}

@JsonProperty("batchName")
public String getBatchName() {
return batchName;
}

@JsonProperty("batchName")
public void setBatchName(String batchName) {
this.batchName = batchName;
}

@JsonProperty("batchTotalRecord")
public Integer getBatchTotalRecord() {
return batchTotalRecord;
}

@JsonProperty("batchTotalRecord")
public void setBatchTotalRecord(Integer batchTotalRecord) {
this.batchTotalRecord = batchTotalRecord;
}

@JsonProperty("categoryCode")
public String getCategoryCode() {
return categoryCode;
}

@JsonProperty("categoryCode")
public void setCategoryCode(String categoryCode) {
this.categoryCode = categoryCode;
}

@JsonProperty("closedRecords")
public Integer getClosedRecords() {
return closedRecords;
}

@JsonProperty("closedRecords")
public void setClosedRecords(Integer closedRecords) {
this.closedRecords = closedRecords;
}

@JsonProperty("createdBy")
public String getCreatedBy() {
return createdBy;
}

@JsonProperty("createdBy")
public void setCreatedBy(String createdBy) {
this.createdBy = createdBy;
}

@JsonProperty("createdOn")
public String getCreatedOn() {
return createdOn;
}

@JsonProperty("createdOn")
public void setCreatedOn(String createdOn) {
this.createdOn = createdOn;
}

@JsonProperty("defaultLang")
public String getDefaultLang() {
return defaultLang;
}

@JsonProperty("defaultLang")
public void setDefaultLang(String defaultLang) {
this.defaultLang = defaultLang;
}

@JsonProperty("domainCode")
public String getDomainCode() {
return domainCode;
}

@JsonProperty("domainCode")
public void setDomainCode(String domainCode) {
this.domainCode = domainCode;
}

@JsonProperty("domainCodeDesc")
public String getDomainCodeDesc() {
return domainCodeDesc;
}

@JsonProperty("domainCodeDesc")
public void setDomainCodeDesc(String domainCodeDesc) {
this.domainCodeDesc = domainCodeDesc;
}

@JsonProperty("focBatchItemsVO")
public FocBatchItemsVO__ getFocBatchItemsVO() {
return focBatchItemsVO;
}

@JsonProperty("focBatchItemsVO")
public void setFocBatchItemsVO(FocBatchItemsVO__ focBatchItemsVO) {
this.focBatchItemsVO = focBatchItemsVO;
}

@JsonProperty("geographyList")
public List<GeographyList__> getGeographyList() {
return geographyList;
}

@JsonProperty("geographyList")
public void setGeographyList(List<GeographyList__> geographyList) {
this.geographyList = geographyList;
}

@JsonProperty("level1ApprovedRecords")
public Integer getLevel1ApprovedRecords() {
return level1ApprovedRecords;
}

@JsonProperty("level1ApprovedRecords")
public void setLevel1ApprovedRecords(Integer level1ApprovedRecords) {
this.level1ApprovedRecords = level1ApprovedRecords;
}

@JsonProperty("level2ApprovedRecords")
public Integer getLevel2ApprovedRecords() {
return level2ApprovedRecords;
}

@JsonProperty("level2ApprovedRecords")
public void setLevel2ApprovedRecords(Integer level2ApprovedRecords) {
this.level2ApprovedRecords = level2ApprovedRecords;
}

@JsonProperty("modifiedBy")
public String getModifiedBy() {
return modifiedBy;
}

@JsonProperty("modifiedBy")
public void setModifiedBy(String modifiedBy) {
this.modifiedBy = modifiedBy;
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

@JsonProperty("networkCodeFor")
public String getNetworkCodeFor() {
return networkCodeFor;
}

@JsonProperty("networkCodeFor")
public void setNetworkCodeFor(String networkCodeFor) {
this.networkCodeFor = networkCodeFor;
}

@JsonProperty("newRecords")
public Integer getNewRecords() {
return newRecords;
}

@JsonProperty("newRecords")
public void setNewRecords(Integer newRecords) {
this.newRecords = newRecords;
}

@JsonProperty("productCode")
public String getProductCode() {
return productCode;
}

@JsonProperty("productCode")
public void setProductCode(String productCode) {
this.productCode = productCode;
}

@JsonProperty("productCodeDesc")
public String getProductCodeDesc() {
return productCodeDesc;
}

@JsonProperty("productCodeDesc")
public void setProductCodeDesc(String productCodeDesc) {
this.productCodeDesc = productCodeDesc;
}

@JsonProperty("productMrp")
public Integer getProductMrp() {
return productMrp;
}

@JsonProperty("productMrp")
public void setProductMrp(Integer productMrp) {
this.productMrp = productMrp;
}

@JsonProperty("productMrpStr")
public String getProductMrpStr() {
return productMrpStr;
}

@JsonProperty("productMrpStr")
public void setProductMrpStr(String productMrpStr) {
this.productMrpStr = productMrpStr;
}

@JsonProperty("productName")
public String getProductName() {
return productName;
}

@JsonProperty("productName")
public void setProductName(String productName) {
this.productName = productName;
}

@JsonProperty("productShortName")
public String getProductShortName() {
return productShortName;
}

@JsonProperty("productShortName")
public void setProductShortName(String productShortName) {
this.productShortName = productShortName;
}

@JsonProperty("productType")
public String getProductType() {
return productType;
}

@JsonProperty("productType")
public void setProductType(String productType) {
this.productType = productType;
}

@JsonProperty("rejectedRecords")
public Integer getRejectedRecords() {
return rejectedRecords;
}

@JsonProperty("rejectedRecords")
public void setRejectedRecords(Integer rejectedRecords) {
this.rejectedRecords = rejectedRecords;
}

@JsonProperty("secondLang")
public String getSecondLang() {
return secondLang;
}

@JsonProperty("secondLang")
public void setSecondLang(String secondLang) {
this.secondLang = secondLang;
}

@JsonProperty("status")
public String getStatus() {
return status;
}

@JsonProperty("status")
public void setStatus(String status) {
this.status = status;
}

@JsonProperty("statusDesc")
public String getStatusDesc() {
return statusDesc;
}

@JsonProperty("statusDesc")
public void setStatusDesc(String statusDesc) {
this.statusDesc = statusDesc;
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

