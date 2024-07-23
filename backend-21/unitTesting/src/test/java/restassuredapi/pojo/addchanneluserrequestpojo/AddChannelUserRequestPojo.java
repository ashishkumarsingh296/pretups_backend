package restassuredapi.pojo.addchanneluserrequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AddChannelUserRequestPojo {
	
	
	
	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierType")
	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	@JsonProperty("identifierValue")
	public String getIdentifierValue() {
		return identifierValue;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}

	@JsonProperty("identifierValue")
	private String identifierValue;

	@JsonProperty("data")
	AddChannelUserDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public AddChannelUserDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(AddChannelUserDetails data) {
		this.data = data;
	}



}
