package restassuredapi.api.UpadateUserWidgetApi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.userwidgetapipojo.UserWidgetRequestPojo;

public class UpdateUserWidgetApi extends BaseAPI {
	String apiPath = "v1/c2sServices/userwidget";
	String contentType;
	UserWidgetRequestPojo userWidgetRequestPojo = new UserWidgetRequestPojo();
	String accessToken;
	EncoderConfig encoderconfig = new EncoderConfig();
	public UpdateUserWidgetApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(UserWidgetRequestPojo userWidgetRequestPojo) {
		this.userWidgetRequestPojo = userWidgetRequestPojo;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(userWidgetRequestPojo);
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
