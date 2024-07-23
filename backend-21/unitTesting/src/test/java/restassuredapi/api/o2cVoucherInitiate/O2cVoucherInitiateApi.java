package restassuredapi.api.o2cVoucherInitiate;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2ctransferstockrequestpojo.C2CStockDetailsRequestPojo;
import restassuredapi.pojo.o2cvoucherinitiaterequestpojo.O2cVoucherInitiateRequestPojo;

public class O2cVoucherInitiateApi extends BaseAPI{

	String apiPath = "/v1/o2c/o2cvoucherInitiate";
	String contentType;
	O2cVoucherInitiateRequestPojo o2cVoucherInitiateRequestPojo = new O2cVoucherInitiateRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public O2cVoucherInitiateApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(O2cVoucherInitiateRequestPojo o2cVoucherInitiateRequestPojo) {
		this.o2cVoucherInitiateRequestPojo = o2cVoucherInitiateRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(o2cVoucherInitiateRequestPojo);
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
