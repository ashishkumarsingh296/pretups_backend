package restassuredapi.api.rechargebulkrestrictedlist;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import restassuredapi.api.BaseAPI;


public class CustomerRechargeRestrictedListApi extends BaseAPI {

	String apiPath = "/v1/c2cFileServices/downloadRCUserList";
	String contentType;
	String accessToken;

	public CustomerRechargeRestrictedListApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	

	protected void createRequest() {
		setRequestSpecifications();
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).relaxedHTTPSValidation().auth().oauth2(accessToken).get();
		String s = apiResponse.asString();
		Log.info(s);
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
	}

}
