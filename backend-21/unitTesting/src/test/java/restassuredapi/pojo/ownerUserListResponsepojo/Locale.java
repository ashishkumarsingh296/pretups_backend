
package restassuredapi.pojo.ownerUserListResponsepojo;

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
    "country",
    "displayCountry",
    "displayLanguage",
    "displayName",
    "displayScript",
    "displayVariant",
    "extensionKeys",
    "iso3Country",
    "iso3Language",
    "language",
    "script",
    "unicodeLocaleAttributes",
    "unicodeLocaleKeys",
    "variant"
})
public class Locale {

    @JsonProperty("country")
    private String country;
    @JsonProperty("displayCountry")
    private String displayCountry;
    @JsonProperty("displayLanguage")
    private String displayLanguage;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("displayScript")
    private String displayScript;
    @JsonProperty("displayVariant")
    private String displayVariant;
    @JsonProperty("extensionKeys")
    private List<ExtensionKey_> extensionKeys = null;
    @JsonProperty("iso3Country")
    private String iso3Country;
    @JsonProperty("iso3Language")
    private String iso3Language;
    @JsonProperty("language")
    private String language;
    @JsonProperty("script")
    private String script;
    @JsonProperty("unicodeLocaleAttributes")
    private List<String> unicodeLocaleAttributes = null;
    @JsonProperty("unicodeLocaleKeys")
    private List<String> unicodeLocaleKeys = null;
    @JsonProperty("variant")
    private String variant;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("displayCountry")
    public String getDisplayCountry() {
        return displayCountry;
    }

    @JsonProperty("displayCountry")
    public void setDisplayCountry(String displayCountry) {
        this.displayCountry = displayCountry;
    }

    @JsonProperty("displayLanguage")
    public String getDisplayLanguage() {
        return displayLanguage;
    }

    @JsonProperty("displayLanguage")
    public void setDisplayLanguage(String displayLanguage) {
        this.displayLanguage = displayLanguage;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("displayScript")
    public String getDisplayScript() {
        return displayScript;
    }

    @JsonProperty("displayScript")
    public void setDisplayScript(String displayScript) {
        this.displayScript = displayScript;
    }

    @JsonProperty("displayVariant")
    public String getDisplayVariant() {
        return displayVariant;
    }

    @JsonProperty("displayVariant")
    public void setDisplayVariant(String displayVariant) {
        this.displayVariant = displayVariant;
    }

    @JsonProperty("extensionKeys")
    public List<ExtensionKey_> getExtensionKeys() {
        return extensionKeys;
    }

    @JsonProperty("extensionKeys")
    public void setExtensionKeys(List<ExtensionKey_> extensionKeys) {
        this.extensionKeys = extensionKeys;
    }

    @JsonProperty("iso3Country")
    public String getIso3Country() {
        return iso3Country;
    }

    @JsonProperty("iso3Country")
    public void setIso3Country(String iso3Country) {
        this.iso3Country = iso3Country;
    }

    @JsonProperty("iso3Language")
    public String getIso3Language() {
        return iso3Language;
    }

    @JsonProperty("iso3Language")
    public void setIso3Language(String iso3Language) {
        this.iso3Language = iso3Language;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("script")
    public String getScript() {
        return script;
    }

    @JsonProperty("script")
    public void setScript(String script) {
        this.script = script;
    }

    @JsonProperty("unicodeLocaleAttributes")
    public List<String> getUnicodeLocaleAttributes() {
        return unicodeLocaleAttributes;
    }

    @JsonProperty("unicodeLocaleAttributes")
    public void setUnicodeLocaleAttributes(List<String> unicodeLocaleAttributes) {
        this.unicodeLocaleAttributes = unicodeLocaleAttributes;
    }

    @JsonProperty("unicodeLocaleKeys")
    public List<String> getUnicodeLocaleKeys() {
        return unicodeLocaleKeys;
    }

    @JsonProperty("unicodeLocaleKeys")
    public void setUnicodeLocaleKeys(List<String> unicodeLocaleKeys) {
        this.unicodeLocaleKeys = unicodeLocaleKeys;
    }

    @JsonProperty("variant")
    public String getVariant() {
        return variant;
    }

    @JsonProperty("variant")
    public void setVariant(String variant) {
        this.variant = variant;
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
