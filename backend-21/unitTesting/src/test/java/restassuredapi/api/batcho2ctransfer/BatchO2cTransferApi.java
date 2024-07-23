package restassuredapi.api.batcho2ctransfer;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

import java.util.HashMap;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.batcho2ctrfrequestpojo.BatchO2CTransferRequestVO;

import com.utils.Log;

public class BatchO2cTransferApi extends BaseAPI {

	String apiPath = "/v1/o2c/o2cBatchStockTrf";
	String contentType;
	
	BatchO2CTransferRequestVO batchO2CTransferRequestVO = new BatchO2CTransferRequestVO();
	EncoderConfig encoderconfig = new EncoderConfig();
	String accessToken;
	
	public BatchO2cTransferApi(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	public void setContentType(String contentType) {
	        this.contentType = contentType;
	}
	 
	 public void addBodyParam(BatchO2CTransferRequestVO batchO2CTransferRequestVO ) {
			this.batchO2CTransferRequestVO =batchO2CTransferRequestVO;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecification = requestSpecBuilder.build();
		requestSpecBuilder.setBody(batchO2CTransferRequestVO );
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
