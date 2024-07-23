package restassuredapi.pojo.c2cbuyvouchersegmentinforequestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class C2CBuyVoucherSegmentInfoRequestPojo {

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
	
	public SegmentData data;

	public SegmentData getData() {
		return data;
	}

	public void setData(SegmentData data) {
		this.data = data;
	}
	
	
}
