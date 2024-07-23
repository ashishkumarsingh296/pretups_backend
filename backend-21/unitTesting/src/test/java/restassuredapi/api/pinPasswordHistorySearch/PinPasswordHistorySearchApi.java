package restassuredapi.api.pinPasswordHistorySearch;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

import java.util.HashMap;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.pinpasswordhistorysearchrequestpojo.PinPasswordHistorySearchRequestPojo;

import com.utils.Log;

public class PinPasswordHistorySearchApi extends BaseAPI {

	String apiPath = "/v1/pretupsUIReports/pinPassHistorySearch";
	String contentType;
	PinPasswordHistorySearchRequestPojo pinPasswordHistorySearchRequestPojo = new PinPasswordHistorySearchRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public PinPasswordHistorySearchApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(PinPasswordHistorySearchRequestPojo pinPasswordHistorySearchRequestPojo) {
		this.pinPasswordHistorySearchRequestPojo = pinPasswordHistorySearchRequestPojo;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(pinPasswordHistorySearchRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
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