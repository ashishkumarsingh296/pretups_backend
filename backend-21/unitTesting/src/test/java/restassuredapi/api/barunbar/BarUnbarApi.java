package restassuredapi.api.barunbar;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

import java.util.HashMap;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.barunbarchanneluserrequestpojo.BarUnbarChannelUserRequestPojo;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeRequestPojo;

import com.utils.Log;


public class BarUnbarApi extends BaseAPI {

	String apiPath = "/v1/userServices/barUnbarUser";
	String contentType;
	BarUnbarChannelUserRequestPojo barUnbarChannelUserRequestPojo = new BarUnbarChannelUserRequestPojo();
	String accessToken;
	String id;
	EncoderConfig encoderconfig = new EncoderConfig();

	public BarUnbarApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(BarUnbarChannelUserRequestPojo barUnbarChannelUserRequestPojo) {
		this.barUnbarChannelUserRequestPojo = barUnbarChannelUserRequestPojo;
	}
	public void addType(String id) {
		this.id = id;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(barUnbarChannelUserRequestPojo);
		requestSpecBuilder.addQueryParam("Type", id);
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
