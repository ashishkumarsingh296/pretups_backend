package restassuredapi.api.vouchercardgroupAPI;

import static io.restassured.RestAssured.given;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.viewvouchercardgrouprequestpojo.ViewVoucherCardGroupRequestPojo;

public class ViewVoucherCardGroupApi extends BaseAPI {

	String apiPath="/cardGroup/cardGroupDetails";
	String contentType;
	ViewVoucherCardGroupRequestPojo viewVoucherCardGroupRequestPojo= new ViewVoucherCardGroupRequestPojo();
	
	public ViewVoucherCardGroupApi(String baseURI) {
		super(baseURI);
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	public void addBodyParam(ViewVoucherCardGroupRequestPojo viewVoucherCardGroupRequestPojo) {
		this.viewVoucherCardGroupRequestPojo =viewVoucherCardGroupRequestPojo;
		}
	

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		//requestSpecBuilder.setBody(bodyParam.toString());
		requestSpecBuilder.setBody(viewVoucherCardGroupRequestPojo);
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
