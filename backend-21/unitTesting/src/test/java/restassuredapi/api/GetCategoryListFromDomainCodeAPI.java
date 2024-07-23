package restassuredapi.api;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class GetCategoryListFromDomainCodeAPI extends BaseAPI {
	String apiPath = "v1/c2sServices/domainCode";
	String contentType;
	String accessToken;
	String domainCode;

	EncoderConfig encoderconfig = new EncoderConfig();

	public GetCategoryListFromDomainCodeAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		Log.info("Base URI = " + baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		Log.info("API Path = " + apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("domainCode", domainCode);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("domainCode", domainCode);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
		String response = apiResponse.asString();
		Log.info(response);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
