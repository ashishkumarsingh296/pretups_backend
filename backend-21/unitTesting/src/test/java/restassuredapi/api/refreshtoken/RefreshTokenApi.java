package restassuredapi.api.refreshtoken;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.refreshtokenrequestpojo.RefreshTokenRequestPojo;

public class RefreshTokenApi extends BaseAPI {


	String apiPath = "/v1/refreshTokenApi";
	String contentType;
	Map<String, Object> headerMap;
	
	RefreshTokenRequestPojo refreshTokenRequestPojo = new RefreshTokenRequestPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public RefreshTokenApi(String baseURI,Map<String, Object> headerMap) {
		super(baseURI);
		this.headerMap=headerMap;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(RefreshTokenRequestPojo refreshTokenRequestPojo) {
		this.refreshTokenRequestPojo = refreshTokenRequestPojo;
		}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(refreshTokenRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).headers(headerMap).post();
		String s = apiResponse.asString();
		Log.info(s);
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
