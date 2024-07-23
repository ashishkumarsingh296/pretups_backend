package restassuredapi.pojo.modifychanneluserrequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyChannelUserRequestPojo {
	
	
	@JsonProperty("data")
	ModifyChannelUserDetails data;

	// Getter Methods

	

	@JsonProperty("data")
	public ModifyChannelUserDetails getData() {
		return data;
	}

	// Setter Methods

	

	public void setData(ModifyChannelUserDetails data) {
		this.data = data;
	}

}
