package restassuredapi.api.internetrecharge;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeRequestPojo;


public class InternetRechargeApi extends BaseAPI {

	String apiPath = "/v1/c2sServices/c2sintrrc";
	String contentType;
	InternetRechargeRequestPojo internetRechargeRequestPojo = new InternetRechargeRequestPojo();
	String accessToken;

	public InternetRechargeApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(InternetRechargeRequestPojo internetRechargeRequestPojo) {
		this.internetRechargeRequestPojo = internetRechargeRequestPojo;
	}
	protected void createRequest() {
		setRequestSpecifications();
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setBody(internetRechargeRequestPojo);
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
