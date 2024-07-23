package restassuredapi.api.c2cvoucherdeno;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import jline.internal.Log;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cbuyvoucherdenoinforequestpojo.C2CBuyVoucherDenoInfoRequestPojo;


public class C2CVoucherDenoAPI extends BaseAPI {

	String apiPath = "/v1/voucher/getvoucherdenominations";
	String contentType;
	C2CBuyVoucherDenoInfoRequestPojo c2cBuyVoucherDenoInfoRequestPojo = new C2CBuyVoucherDenoInfoRequestPojo();
	String accessToken;
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2CVoucherDenoAPI(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;

	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(C2CBuyVoucherDenoInfoRequestPojo c2cBuyVoucherDenoInfoRequestPojo) {
		this.c2cBuyVoucherDenoInfoRequestPojo = c2cBuyVoucherDenoInfoRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2cBuyVoucherDenoInfoRequestPojo);
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
		apiResponse.then().spec(responseSpecification);
	}

}
