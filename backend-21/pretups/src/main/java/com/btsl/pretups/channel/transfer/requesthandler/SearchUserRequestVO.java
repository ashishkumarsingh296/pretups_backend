package com.btsl.pretups.channel.transfer.requesthandler;


import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class SearchUserRequestVO extends OAuthUser{
	
	
	
	@JsonProperty("data")
    private UserNameDataFields data = null;
	@JsonProperty("data")
	    public UserNameDataFields getDataF() {
		return data;
	}
	@JsonProperty("data")
	public void setDataF(UserNameDataFields data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "UserNameDataFields [UserNameDataFields]=" + data;
	}
}

	 class UserNameDataFields
	{
		 @io.swagger.v3.oas.annotations.media.Schema(example = "RET", required = true/* , defaultValue = "" */)
	        @JsonProperty("userCategory")
		public String getUserCategory() {
			return userCategory;
		}
		public void setUserCategory(String userCategory) {
			this.userCategory = userCategory;
		}
		@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */)
        @JsonProperty("channelOwnerCategory")
		public String getChannelOwnerCategory() {
			return channelOwnerCategory;
		}
		public void setChannelOwnerCategory(String channelOwnerCategory) {
			this.channelOwnerCategory = channelOwnerCategory;
		}
		@io.swagger.v3.oas.annotations.media.Schema(example = "rarya_dist", required = true/* , defaultValue = "" */)
        @JsonProperty("channelOwnerName")
		public String getChannelOwnerName() {
			return channelOwnerName;
		}
		public void setChannelOwnerName(String channelOwnerName) {
			this.channelOwnerName = channelOwnerName;
		}
		@JsonProperty("userCategory")
	    private String userCategory;
		@JsonProperty("channelOwnerCategory")
	    private String channelOwnerCategory;
		@JsonProperty("channelOwnerName")
	    private String channelOwnerName;
		@JsonProperty("geoDomainCode")
	    private String geoDomainCode;
		@JsonProperty("channelDomain")
	    private String channelDomain;
		@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */)
        @JsonProperty("channelDomain")
		public String getChannelDomain() {
			return channelDomain;
		}
		public void setChannelDomain(String channelDomain) {
			this.channelDomain = channelDomain;
		}
		@io.swagger.v3.oas.annotations.media.Schema(example = "DELHI", required = true/* , defaultValue = "" */)
        @JsonProperty("geoDomainCode")
		public String getGeoDomainCode() {
			return geoDomainCode;
		}
		public void setGeoDomainCode(String geoDomainCode) {
			this.geoDomainCode = geoDomainCode;
		}
		@JsonProperty("channelUserID")
	    private String channelUserID;
		@io.swagger.v3.oas.annotations.media.Schema(example = "NGD000003837", required = true/* , defaultValue = "" */)
        @JsonProperty("channelUserID")
		public String getChannelUserID() {
			return channelUserID;
		}
		public void setChannelUserID(String channelUserID) {
			this.channelUserID = channelUserID;
		}
		@io.swagger.v3.oas.annotations.media.Schema(example = "NGD000003837", required = true/* , defaultValue = "" */)
        @JsonProperty("channelOwnerUserID")
		public String getChannelOwnerUserID() {
			return channelOwnerUserID;
		}
		public void setChannelOwnerUserID(String channelOwnerUserID) {
			this.channelOwnerUserID = channelOwnerUserID;
		}
		@JsonProperty("channelOwnerUserID")
	    private String channelOwnerUserID;
	}
	
	

	
	


