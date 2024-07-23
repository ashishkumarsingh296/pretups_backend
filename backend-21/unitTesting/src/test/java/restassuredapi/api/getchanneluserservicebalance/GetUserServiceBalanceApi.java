package restassuredapi.api.getchanneluserservicebalance;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.getuserservicebalancepojo.GetUserserviceBalanceResponsePojo;


public class GetUserServiceBalanceApi extends BaseAPI {
	
	String apiPath="/v1/c2sServices/userservicebal/{servicecode}";
	String contentType;
	String servicecode;
	String accessToken;
	
	GetUserserviceBalanceResponsePojo getUserserviceBalanceResponsePojo = new GetUserserviceBalanceResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	

	public GetUserServiceBalanceApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public String getApiPath() {
		return apiPath;
	}

	public String getServicecode() {
		return servicecode;
	}

	public void setServicecode(String servicecode) {
		this.servicecode = servicecode;
	}


	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public GetUserserviceBalanceResponsePojo getGetUserserviceBalanceResponsePojo() {
		return getUserserviceBalanceResponsePojo;
	}

	public void setGetUserserviceBalanceResponsePojo(GetUserserviceBalanceResponsePojo getUserserviceBalanceResponsePojo) {
		this.getUserserviceBalanceResponsePojo = getUserserviceBalanceResponsePojo;
	}

	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}

	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}
	
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addPathParam("servicecode", servicecode);
		requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		pathParams.put("servicecode", "servicecode");
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
		String s = apiResponse.asString();
		System.out.println(s);
		Log.info(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}




}
