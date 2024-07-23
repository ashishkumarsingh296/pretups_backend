package restassuredapi.api.getdomaincategory;

import static io.restassured.RestAssured.given;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.getdomaincategoryrequestpojo.GetDomainCategoryRequestPojo;

import java.util.HashMap;

public class GetDomainCategoryAPI extends BaseAPI {
	
	String apiPath = "/v1/c2sReceiver/getdomaincategory";
	String contentType;
	String accessToken;
	GetDomainCategoryRequestPojo getDomainCategoryRequestPojo = new GetDomainCategoryRequestPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public GetDomainCategoryAPI(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(GetDomainCategoryRequestPojo getDomainCategoryRequestPojo) {
		this.getDomainCategoryRequestPojo = getDomainCategoryRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		Log.info("Base URI = " + baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		Log.info("API Path = " + apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(getDomainCategoryRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		String response = apiResponse.asString();
		Log.info(response);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();

	}



}
