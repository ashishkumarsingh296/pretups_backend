package restassuredapi.api.fixedrecharge;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.fixedrechargerequestpojo.FixedRechargeRequestPojo;

public class FixedRechargeApi extends BaseAPI {

	String apiPath = "/v1/c2sServices/fixlinerc";
	String contentType;
	FixedRechargeRequestPojo fixedRechargeRequestPojo = new FixedRechargeRequestPojo();
	String accessToken;

	public FixedRechargeApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(FixedRechargeRequestPojo fixedRechargeRequestPojo) {
		this.fixedRechargeRequestPojo = fixedRechargeRequestPojo;
	}
	protected void createRequest() {
		setRequestSpecifications();
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setBody(fixedRechargeRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).relaxedHTTPSValidation().auth().oauth2(accessToken).post();
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
