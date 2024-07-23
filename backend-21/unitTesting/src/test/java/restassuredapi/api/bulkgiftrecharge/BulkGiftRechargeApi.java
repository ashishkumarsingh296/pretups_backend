package restassuredapi.api.bulkgiftrecharge;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.bulkgiftrechargerequestpojo.C2CBulkGiftRechargeRequestPojo;

public class BulkGiftRechargeApi extends BaseAPI {

	String apiPath = "/v1/c2sServices/c2sbulkgrc";
	String contentType;
	
	C2CBulkGiftRechargeRequestPojo c2CBulkGiftRechargeRequestPojo = new C2CBulkGiftRechargeRequestPojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	String accessToken;
	
	public BulkGiftRechargeApi(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	public void setContentType(String contentType) {
	        this.contentType = contentType;
	}
	 
	 public void addBodyParam(C2CBulkGiftRechargeRequestPojo c2CBulkGiftRechargeRequestPojo ) {
			this.c2CBulkGiftRechargeRequestPojo =c2CBulkGiftRechargeRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecification = requestSpecBuilder.build();
		requestSpecBuilder.setBody(c2CBulkGiftRechargeRequestPojo );
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
