package restassuredapi.api.c2cstocktransfermult;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2ctransferstockrequestpojo.C2CStockDetailsRequestPojo;

public class C2CStockTransferMultApi extends BaseAPI{

	String apiPath = "v1/c2cReceiver/c2ctrfmul";
	String contentType;
	C2CStockDetailsRequestPojo c2CStockDetailsRequestPojo = new C2CStockDetailsRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2CStockTransferMultApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(C2CStockDetailsRequestPojo c2CStockDetailsRequestPojo) {
		this.c2CStockDetailsRequestPojo = c2CStockDetailsRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2CStockDetailsRequestPojo);
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
