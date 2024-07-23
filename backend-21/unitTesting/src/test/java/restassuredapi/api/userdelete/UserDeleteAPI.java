package restassuredapi.api.userdelete;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.userdeleterequestpojo.UserDeleteRequestPojo;
import restassuredapi.pojo.userdeleteresponsepojo.UserDeleteResponsePojo;

public class UserDeleteAPI extends BaseAPI{
	



	String apiPath = "/v1/channelUsers/{idValue}";

	String contentType;
	String idType;
	String idValue;
	String accessToken;
	String remarks;
	String extnwcode;
	
	
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public UserDeleteAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public void setIdType(String idType) {
		this.idType = idType;
	}

	public void setidValue(String idValue) {
		this.idValue = idValue;
	}

	


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("idType", idType);
		requestSpecBuilder.addPathParam("idValue", idValue);
		requestSpecBuilder.addQueryParam("extnwcode", extnwcode);
		requestSpecBuilder.addQueryParam("remarks", remarks);
		//requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("idType", idType);
		queryParams.put("extnwcode", extnwcode);
		queryParams.put("remarks", remarks);
		
		pathParams.put("idValue", idValue);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).delete();    /*.auth().oauth2(accessToken, OAuthSignature.HEADER)*/
	
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
