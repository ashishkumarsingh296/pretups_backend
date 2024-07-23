package restassuredapi.pojo.o2creturnrequestpojo;

import java.util.List;

import restassuredapi.pojo.txncalculationvoucherstockrequestpojo.Products;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pin",
    "products",
    "remarks",
    "language"
})
public class O2CReturnReqData {
    @JsonProperty("products")
    private List<Products> products = null;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("language")
    private String language;
    @JsonProperty("pin")
    private String pin;


    @JsonProperty("products")
    public List<Products> getProducts() {
        return products;
    }

    @JsonProperty("products")
    public void setProducts(List<Products> products) {
        this.products = products;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language1) {
        this.language = language1;
    }

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

}
