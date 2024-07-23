package restassuredapi.api.c2SAdditionalCommissionSummarySearch;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2Sadditionalcommissionsummarysearchrequestpojo.C2SAdditionalCommissionSummarySearchRequestPojo;

public class C2SAdditionalCommissionSummarySearchApi extends BaseAPI {

	String apiPath = "/v1/pretupsUIReports/additionCommisionSummaryC2S";
	String contentType;
	C2SAdditionalCommissionSummarySearchRequestPojo c2SAdditionalCommissionSummarySearchRequestPojo  = new C2SAdditionalCommissionSummarySearchRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2SAdditionalCommissionSummarySearchApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(C2SAdditionalCommissionSummarySearchRequestPojo c2SAdditionalCommissionSummarySearchRequestPojo) {
		this.c2SAdditionalCommissionSummarySearchRequestPojo = c2SAdditionalCommissionSummarySearchRequestPojo;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2SAdditionalCommissionSummarySearchRequestPojo);
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