package restassuredapi.pojo.batchO2CCommissionUserListDownloadRequestpojo;


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
"category",
"domain",
"fileType",
"geography",
"product"
})
@Generated("jsonschema2pojo")
public class BatchO2CCommissionUserListDownloadRequestpojo {

@JsonProperty("category")
private String category;
@JsonProperty("domain")
private String domain;
@JsonProperty("fileType")
private String fileType;
@JsonProperty("geography")
private String geography;
@JsonProperty("product")
private String product;

@JsonProperty("category")
public String getCategory() {
return category;
}

@JsonProperty("category")
public void setCategory(String category) {
this.category = category;
}

@JsonProperty("domain")
public String getDomain() {
return domain;
}

@JsonProperty("domain")
public void setDomain(String domain) {
this.domain = domain;
}

@JsonProperty("fileType")
public String getFileType() {
return fileType;
}

@JsonProperty("fileType")
public void setFileType(String fileType) {
this.fileType = fileType;
}

@JsonProperty("geography")
public String getGeography() {
return geography;
}

@JsonProperty("geography")
public void setGeography(String geography) {
this.geography = geography;
}

@JsonProperty("product")
public String getProduct() {
return product;
}

@JsonProperty("product")
public void setProduct(String product) {
this.product = product;
}


}