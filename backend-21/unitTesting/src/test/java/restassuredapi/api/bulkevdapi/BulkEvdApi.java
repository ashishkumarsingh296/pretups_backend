package restassuredapi.api.bulkevdapi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.bulkevdrequestpojo.BulkEVDRequestPojo;

public class BulkEvdApi extends BaseAPI{
	
	String apiPath = "/v1/c2sServices/c2sbulkEVD";
	String accessToken;
	BulkEVDRequestPojo bulkDVDRequestPojo = new BulkEVDRequestPojo();
	
	
	public BulkEvdApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	public void addBodyParam(BulkEVDRequestPojo bulkDVDRequestPojo) {
		this.bulkDVDRequestPojo = bulkDVDRequestPojo;
	}

	@Override
	protected void createRequest() {
		setRequestSpecifications();
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setBody(bulkDVDRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);

	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).relaxedHTTPSValidation().auth().oauth2(accessToken)
				.post();

		String s = apiResponse.asString();
		Log.info(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
