package restassuredapi.api.c2cvouchertxndetailapi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import com.utils.*;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.transactionaldatarequestpojo.TransactionalDataRequestPojo;
import restassuredapi.pojo.viewpassbookrequestpojo.ViewPassBookRequestPojo;

public class C2CVoucherTxnDetailApi extends BaseAPI {

	String apiPath = "/v1/c2sReceiver/c2cvoucherdetails";
	String contentType;
	TransactionalDataRequestPojo transactionalDataRequestPojo = new TransactionalDataRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2CVoucherTxnDetailApi(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(TransactionalDataRequestPojo transactionalDataRequestPojo) {
		this.transactionalDataRequestPojo = transactionalDataRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(transactionalDataRequestPojo);
		requestSpecification = requestSpecBuilder.build();
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
