package restassuredapi.api.o2Ctransferacknowledgementdetails;

import static io.restassured.RestAssured.given;
import java.util.HashMap;
import com.utils.Log;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import restassuredapi.api.BaseAPI;

public class O2CTransferAcknowledgementDetailsAPI extends BaseAPI {
	
	String apiPath = "/v1/pretupsUIReports/getO2CTransferAcknowledgement";
	String contentType;
	String accessToken;
	String distributionType;
	String transactionID;
			
	EncoderConfig encoderconfig = new EncoderConfig();

	public O2CTransferAcknowledgementDetailsAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
		
	public O2CTransferAcknowledgementDetailsAPI(String baseURI) {
		super(baseURI);
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	protected void createRequest() {
	requestSpecBuilder.setBaseUri(baseURI);
	requestSpecBuilder.setBasePath(apiPath);
	requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
	requestSpecBuilder.setContentType(ContentType.JSON);
	requestSpecBuilder.addHeader("Authorization", "");
	requestSpecBuilder.addQueryParam("distributionType", distributionType);
	requestSpecBuilder.addQueryParam("transactionID", transactionID);
			
	requestSpecification = requestSpecBuilder.build();
	HashMap<String, String> queryParams = new HashMap<String, String>();
	HashMap<String, String> pathParams = new HashMap<String, String>();
		
	queryParams.put("distributionType", distributionType);
	queryParams.put("transactionID", transactionID);
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