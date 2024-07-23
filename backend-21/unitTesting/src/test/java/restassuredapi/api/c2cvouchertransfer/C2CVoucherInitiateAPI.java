
package restassuredapi.api.c2cvouchertransfer;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cvoucherinitiaterequestpojo.C2CVoucherInitiateRequestPojo;
import restassuredapi.pojo.c2cvoucherinitiateresponsepojo.C2CVoucherInitiateResponsePojo;

import java.util.HashMap;

public class C2CVoucherInitiateAPI extends BaseAPI {

	String apiPath = "/v1/c2cReceiver/c2cvomstrfini";
	String contentType;
	String accessToken;
	C2CVoucherInitiateRequestPojo c2CVoucherInitiateRequestPojo = new C2CVoucherInitiateRequestPojo();
	C2CVoucherInitiateResponsePojo c2CVoucherInitiateResponsePojo = new C2CVoucherInitiateResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2CVoucherInitiateAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
			}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(C2CVoucherInitiateRequestPojo c2CVoucherInitiateRequestPojo) {
		this.c2CVoucherInitiateRequestPojo = c2CVoucherInitiateRequestPojo;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2CVoucherInitiateRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		String s = apiResponse.asString();
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
