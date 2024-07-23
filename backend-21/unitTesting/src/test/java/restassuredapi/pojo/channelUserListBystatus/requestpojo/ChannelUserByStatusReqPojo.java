package restassuredapi.pojo.channelUserListBystatus.requestpojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "category",
    "domain",
    "geography",
    "loggedUserNeworkCode",
    "loginID",
    "mobileNumber",
    "searchType",
    "userStatus"
})
public class ChannelUserByStatusReqPojo {
	    @JsonProperty("category")
	    public String category;
	    @JsonProperty("domain")
	    public String domain;
	    @JsonProperty("geography")
	    public String geography;
	    @JsonProperty("loggedUserNeworkCode")
	    public String loggedUserNeworkCode;
	    @JsonProperty("loginID")
	    public String loginID;
	    @JsonProperty("mobileNumber")
	    public String mobileNumber;
	    @JsonProperty("searchType")
	    public String searchType;
	    @JsonProperty("userStatus")
	    public String userStatus;
	    
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
	    @JsonProperty("geography")
		public String getGeography() {
			return geography;
		}
	    @JsonProperty("geography")
		public void setGeography(String geography) {
			this.geography = geography;
		}
	    @JsonProperty("loggedUserNeworkCode")
		public String getLoggedUserNeworkCode() {
			return loggedUserNeworkCode;
		}
	    @JsonProperty("loggedUserNeworkCode")
		public void setLoggedUserNeworkCode(String loggedUserNeworkCode) {
			this.loggedUserNeworkCode = loggedUserNeworkCode;
		}
	    @JsonProperty("loginID")
		public String getLoginID() {
			return loginID;
		}
	    @JsonProperty("loginID")
		public void setLoginID(String loginID) {
			this.loginID = loginID;
		}
	    @JsonProperty("mobileNumber")
		public String getMobileNumber() {
			return mobileNumber;
		}
	    @JsonProperty("mobileNumber")
		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}
	    @JsonProperty("searchType")
		public String getSearchType() {
			return searchType;
		}
	    @JsonProperty("searchType")
		public void setSearchType(String searchType) {
			this.searchType = searchType;
		}
	    @JsonProperty("userStatus")
		public String getUserStatus() {
			return userStatus;
		}
	    @JsonProperty("userStatus")
		public void setUserStatus(String userStatus) {
			this.userStatus = userStatus;
		}
	    
	    
	    
}
