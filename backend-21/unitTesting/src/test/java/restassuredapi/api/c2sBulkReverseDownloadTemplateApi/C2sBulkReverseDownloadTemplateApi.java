package restassuredapi.api.c2sBulkReverseDownloadTemplateApi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import restassuredapi.api.BaseAPI;

public class C2sBulkReverseDownloadTemplateApi extends BaseAPI {

	String apiPath = "/v1/c2sbulkreverse/downloadtemplate";
	String accessToken;

	public C2sBulkReverseDownloadTemplateApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	@Override
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
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
