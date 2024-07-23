package restassuredapi.api.batchCommissionUserListDownloadApi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.o2CbatchCommissionUserListDownloadRequestpojo.O2CbatchCommissionUserListDownloadRequestpojo;

public class BatchCommissionUserListDownloadApi extends BaseAPI{
	
	String apiPath = "/v1/o2c/batchCommissionUserListDownload";

	String contentType;

	String accessToken;
	
	O2CbatchCommissionUserListDownloadRequestpojo o2CbatchCommissionUserListDownloadRequestpojo =new O2CbatchCommissionUserListDownloadRequestpojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	

	public BatchCommissionUserListDownloadApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	 public void addBodyParam(O2CbatchCommissionUserListDownloadRequestpojo o2CbatchCommissionUserListDownloadRequestpojo ) {
			this.o2CbatchCommissionUserListDownloadRequestpojo =o2CbatchCommissionUserListDownloadRequestpojo;
	}

	
	public O2CbatchCommissionUserListDownloadRequestpojo getO2CbatchCommissionUserListDownloadRequestpojo() {
		return o2CbatchCommissionUserListDownloadRequestpojo;
	}

	public void setO2CbatchCommissionUserListDownloadRequestpojo(
			O2CbatchCommissionUserListDownloadRequestpojo o2CbatchCommissionUserListDownloadRequestpojo) {
		this.o2CbatchCommissionUserListDownloadRequestpojo = o2CbatchCommissionUserListDownloadRequestpojo;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(o2CbatchCommissionUserListDownloadRequestpojo);
		requestSpecBuilder.addHeader("Authorization", "");
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
