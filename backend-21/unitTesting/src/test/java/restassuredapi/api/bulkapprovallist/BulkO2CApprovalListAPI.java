package restassuredapi.api.bulkapprovallist;

import static io.restassured.RestAssured.given;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.bulko2capprovallistrequestpojo.BulkO2CApprovalListRequestPojo;
import java.util.HashMap;


public class BulkO2CApprovalListAPI extends BaseAPI {

	String apiPath = "/v1/o2c/getBulkApprvList";
	String contentType;
	String accessToken;
	BulkO2CApprovalListRequestPojo bulkO2CApprovalListRequestPojo = new BulkO2CApprovalListRequestPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public BulkO2CApprovalListAPI (String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(BulkO2CApprovalListRequestPojo bulkO2CApprovalListRequestPojo) {
		this.bulkO2CApprovalListRequestPojo = bulkO2CApprovalListRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(bulkO2CApprovalListRequestPojo);
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
