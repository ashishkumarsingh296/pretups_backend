package restassuredapi.api.getvoucherinfo;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.getvoucherinforequestpojo.GetVoucherInfoRequestPojo;;

public class GetVoucherInfoAPI extends BaseAPI {
	
	String apiPath = "v1/voucher/getvoucherinfo";
	String userId;
	String contentType;
	String accessToken;
//	GetVoucherInfoRequestPojo getVoucherInfoRequestPojo = new GetVoucherInfoRequestPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public GetVoucherInfoAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
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
		requestSpecBuilder.addQueryParam("userID", userId);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("userID", userId);
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
