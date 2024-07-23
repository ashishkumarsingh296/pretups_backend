package restassuredapi.api.vouchercardgroupAPI;

import static io.restassured.RestAssured.given;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.deletevouchercardgrouprequestpojo.DeleteVoucherCardGroupRequestPojo;


public class DeleteVoucherCardGroupApi extends BaseAPI {

	String apiPath="/cardGroup/deleteCardGroup";
	String contentType;
	DeleteVoucherCardGroupRequestPojo deleteVoucherCardGroupRequestPojo= new DeleteVoucherCardGroupRequestPojo();
	
	public DeleteVoucherCardGroupApi(String baseURI) {
		super(baseURI);
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	public void addBodyParam(DeleteVoucherCardGroupRequestPojo deleteVoucherCardGroupRequestPojo) {
		this.deleteVoucherCardGroupRequestPojo =deleteVoucherCardGroupRequestPojo;
		}
	

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(deleteVoucherCardGroupRequestPojo);
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

