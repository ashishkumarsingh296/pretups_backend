package restassuredapi.api.c2sBulkReversalApi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2sBulkReversalRequestPojo.C2SBulkReversalRequestPojo;

public class C2SBulkReversalAPI extends BaseAPI{

	String apiPath = "/v1/c2sbulkreverse";
	String accessToken;
	C2SBulkReversalRequestPojo c2SBulkReversalRequestPojo = new C2SBulkReversalRequestPojo();
	
	
	public C2SBulkReversalAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	public void addBodyParam(C2SBulkReversalRequestPojo c2SBulkReversalRequestPojo) {
		this.c2SBulkReversalRequestPojo = c2SBulkReversalRequestPojo;
	}

	@Override
	protected void createRequest() {
		setRequestSpecifications();
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setBody(c2SBulkReversalRequestPojo);
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
