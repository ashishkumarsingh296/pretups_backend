
package restassuredapi.pojo.focInitiateRequestPojo;

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
    "focProducts",
    "language1",
    "language2",
    "msisdn2",
    "pin",
    "refnumber",
    "remarks"
})
public class Datum {

    @JsonProperty("focProducts")
    private List<FocProduct> focProducts = null;
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("language2")
    private String language2;
    @JsonProperty("msisdn2")
    private String msisdn2;
    @JsonProperty("pin")
    private int pin;
    @JsonProperty("refnumber")
    private int refnumber;
    @JsonProperty("remarks")
    private String remarks;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Datum() {
    }

    /**
     * 
     * @param pin
     * @param language2
     * @param language1
     * @param refnumber
     * @param msisdn2
     * @param focProducts
     * @param remarks
     */
    public Datum(List<FocProduct> focProducts, String language1, String language2, String msisdn2, int pin, int refnumber, String remarks) {
        super();
        this.focProducts = focProducts;
        this.language1 = language1;
        this.language2 = language2;
        this.msisdn2 = msisdn2;
        this.pin = pin;
        this.refnumber = refnumber;
        this.remarks = remarks;
    }

    @JsonProperty("focProducts")
    public List<FocProduct> getFocProducts() {
        return focProducts;
    }

    @JsonProperty("focProducts")
    public void setFocProducts(List<FocProduct> focProducts) {
        this.focProducts = focProducts;
    }

    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    @JsonProperty("msisdn2")
    public String getMsisdn2() {
        return msisdn2;
    }

    @JsonProperty("msisdn2")
    public void setMsisdn2(String msisdn2) {
        this.msisdn2 = msisdn2;
    }

    @JsonProperty("pin")
    public int getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(int pin) {
        this.pin = pin;
    }

    @JsonProperty("refnumber")
    public int getRefnumber() {
        return refnumber;
    }

    @JsonProperty("refnumber")
    public void setRefnumber(int refnumber) {
        this.refnumber = refnumber;
    }

    @JsonProperty("remarks")
    public String  getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
