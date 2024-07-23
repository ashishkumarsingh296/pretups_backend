package restassuredapi.api.O2CBulkTrfWidAppRej;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.bulktrfwidrawapprejPojo.BulktrfwidrawapprejPojo;

public class O2CBulkAppRejTrfWidrawApi extends BaseAPI {
	String apiPath="/v1/o2c/o2CBatchAppRej";
	String contentType;
	
	EncoderConfig encoderconfig = new EncoderConfig();
	String accessToken;
	BulktrfwidrawapprejPojo bulktrfwidrawapprejPojo = new BulktrfwidrawapprejPojo();
	public O2CBulkAppRejTrfWidrawApi(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	 public void addBodyParam(BulktrfwidrawapprejPojo bulktrfwidrawapprejPojo ) {
			this.bulktrfwidrawapprejPojo =bulktrfwidrawapprejPojo;
	}


	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecification = requestSpecBuilder.build();
		requestSpecBuilder.setBody(bulktrfwidrawapprejPojo);
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
