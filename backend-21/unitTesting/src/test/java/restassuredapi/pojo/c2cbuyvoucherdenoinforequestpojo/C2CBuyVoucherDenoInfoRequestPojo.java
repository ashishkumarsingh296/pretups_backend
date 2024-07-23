package restassuredapi.pojo.c2cbuyvoucherdenoinforequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class C2CBuyVoucherDenoInfoRequestPojo {

	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierValue")
	private String identifierValue;
	
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
	
	private DenominationData data;

	public DenominationData getData() {
		return data;
	}

	public void setData(DenominationData data) {
		this.data = data;
	}
	
	
	

}
