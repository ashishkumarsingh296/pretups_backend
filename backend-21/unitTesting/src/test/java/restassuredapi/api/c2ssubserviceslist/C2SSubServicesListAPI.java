package restassuredapi.api.c2ssubserviceslist;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2ssubservicesresponsepojo.C2SSubServicesResponsePojo;

public class C2SSubServicesListAPI extends BaseAPI {
	String apiPath = "/v1/c2sServices/subServices/{serviceName}";
	String contentType;
	String serviceName;
	String accessToken;

	C2SSubServicesResponsePojo c2SSubServicesResponsePojo = new C2SSubServicesResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2SSubServicesListAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getServiceTypeCode() {
		return serviceName;
	}

	public void setServiceTypeCode(String serviceName) {
		this.serviceName = serviceName;
	}
	
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addPathParam("serviceName", serviceName);
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
