package restassuredapi.api.txnpinmg;

import java.util.HashMap;
import com.utils.Log;
import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class TxnPinManagementApi extends BaseAPI{
	String apiPath = "/v1/txPinManagement/isPinChangeOnTxRequired";
	String accessToken;
	String contentType;
	String  msisdn;
	
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public TxnPinManagementApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("msisdn", msisdn);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("msisdn", msisdn);
	
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		
	}@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
		String s = apiResponse.asString();
		Log.info(s);
		System.out.println(s);
		
	}@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
	}
}
