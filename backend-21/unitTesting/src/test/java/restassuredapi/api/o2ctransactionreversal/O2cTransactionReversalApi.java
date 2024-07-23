package restassuredapi.api.o2ctransactionreversal;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.o2ctransactionreversalrequestpojo.O2cTransactionReversalRequestPojo;
import restassuredapi.pojo.o2ctxnrevlistrequestpojo.O2cTxnRevListRequestPojo;


public class O2cTransactionReversalApi extends BaseAPI {

	String apiPath = "/v1/channeladmin/o2ctxnreversal";
	String contentType;
	O2cTransactionReversalRequestPojo o2cTransactionReversalRequestPojo = new O2cTransactionReversalRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public O2cTransactionReversalApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public void addBodyParam(O2cTransactionReversalRequestPojo o2cTransactionReversalRequestPojo) {
		this.o2cTransactionReversalRequestPojo = o2cTransactionReversalRequestPojo;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(o2cTransactionReversalRequestPojo);
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
