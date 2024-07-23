package restassuredapi.pojo.usersearchrequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSearchRequestPojo {
	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierValue")
	private String identifierValue;
	
	@JsonProperty("category")
	private int category;
	
	@JsonProperty("searchValue")
	private String searchValue;


	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	public String getIdentifierValue() {
		return identifierValue;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}
	

}
