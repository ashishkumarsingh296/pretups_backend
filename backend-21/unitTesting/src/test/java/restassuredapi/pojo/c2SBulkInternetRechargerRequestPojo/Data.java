
package restassuredapi.pojo.c2SBulkInternetRechargerRequestPojo;

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
    "batchType",
    "extnwcode",
    "file",
    "fileName",
    "fileType",
    "noOfDays",
    "occurence",
    "pin",
    "scheduleDate",
    "scheduleNow"
})
public class Data {

    @JsonProperty("batchType")
    private String batchType;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("file")
    private String file;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("fileType")
    private String fileType;
    @JsonProperty("noOfDays")
    private String noOfDays;
    @JsonProperty("occurence")
    private String occurence;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("scheduleDate")
    private String scheduleDate;
    @JsonProperty("scheduleNow")
    private String scheduleNow;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("batchType")
    public String getBatchType() {
        return batchType;
    }

    @JsonProperty("batchType")
    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("file")
    public String getFile() {
        return file;
    }

    @JsonProperty("file")
    public void setFile(String file) {
        this.file = file;
    }

    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }

    @JsonProperty("fileName")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("fileType")
    public String getFileType() {
        return fileType;
    }

    @JsonProperty("fileType")
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @JsonProperty("noOfDays")
    public String getNoOfDays() {
        return noOfDays;
    }

    @JsonProperty("noOfDays")
    public void setNoOfDays(String noOfDays) {
        this.noOfDays = noOfDays;
    }

    @JsonProperty("occurence")
    public String getOccurence() {
        return occurence;
    }

    @JsonProperty("occurence")
    public void setOccurence(String occurence) {
        this.occurence = occurence;
    }

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String string) {
        this.pin = string;
    }

    @JsonProperty("scheduleDate")
    public String getScheduleDate() {
        return scheduleDate;
    }

    @JsonProperty("scheduleDate")
    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    @JsonProperty("scheduleNow")
    public String getScheduleNow() {
        return scheduleNow;
    }

    @JsonProperty("scheduleNow")
    public void setScheduleNow(String scheduleNow) {
        this.scheduleNow = scheduleNow;
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
