package restassuredapi.api.vouchercardgroupAPI;

import static io.restassured.RestAssured.given;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.modifyvouchercardgrouprequestpojo.ModifyVoucherCardGroupRequestPojo;

public class ModifyVoucherCardGroupApi extends BaseAPI 
{
	String apiPath="cardGroup/addCardgroup";
	String contentType;
	ModifyVoucherCardGroupRequestPojo modifyVoucherCardGroupRequestPojo= new ModifyVoucherCardGroupRequestPojo();
	
	public ModifyVoucherCardGroupApi(String baseURI) {
		super(baseURI);
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	public void addBodyParam(ModifyVoucherCardGroupRequestPojo modifyVoucherCardGroupRequestPojo) {
		this.modifyVoucherCardGroupRequestPojo =modifyVoucherCardGroupRequestPojo;
		}
	

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(modifyVoucherCardGroupRequestPojo);
		//requestSpecBuilder.setBody(Joiner.on("&").withKeyValueSeparator("=").join(bodyParams));
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).post();
		String s=apiResponse.asString();
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}
}
