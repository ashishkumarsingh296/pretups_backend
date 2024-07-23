package restassuredapi.api.getrolesandservices;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeRequestPojo;

public class GetRolesAndServicesApi extends BaseAPI {


	String apiPath = "/v1/channelUsers/rolesServices";
	String contentType;
	String accessToken;
	String networkCode;
	String userCategoryCode;
	EncoderConfig encoderconfig = new EncoderConfig();

	public GetRolesAndServicesApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addNetworkCode(String networkCode)
	{
		this.networkCode = networkCode;
	}
	public void addCategoryCode(String categoryCode)
	{
		this.userCategoryCode = categoryCode;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("networkCode", networkCode);
		requestSpecBuilder.addQueryParam("userCategeryCode", userCategoryCode);
		//requestSpecBuilder.setBody(internetRechargeRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
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
