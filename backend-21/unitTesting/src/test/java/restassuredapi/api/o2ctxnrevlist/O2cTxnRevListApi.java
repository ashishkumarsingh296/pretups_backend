package restassuredapi.api.o2ctxnrevlist;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.o2ctxnrevlistrequestpojo.O2cTxnRevListRequestPojo;


public class O2cTxnRevListApi extends BaseAPI {

	String apiPath = "/v1/channeladmin/o2CTxnReversalList";
	String contentType;
	O2cTxnRevListRequestPojo o2cTxnRevListRequestPojo = new O2cTxnRevListRequestPojo();
	String accessToken;
	String searchBy;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public O2cTxnRevListApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	public void addBodyParam(O2cTxnRevListRequestPojo o2cTxnRevListRequestPojo) {
		this.o2cTxnRevListRequestPojo = o2cTxnRevListRequestPojo;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(o2cTxnRevListRequestPojo);
		requestSpecBuilder.addQueryParam("searchBy", searchBy);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("searchBy", searchBy);
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
