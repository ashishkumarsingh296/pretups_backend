package restassuredapi.api.c2sgettransactionedetail;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2sgetamountservicewiselistrequestpojo.C2SGetAmountServiceWiseListRequestPojo;
import restassuredapi.pojo.c2sgettransactiondetailrequestpojo.C2SGetTransactionDetailsRequestPojo;
import restassuredapi.pojo.c2sgettransactiondetailresponsepojo.C2SGetTransactionDetailsResponsePojo;

public class C2SGetTransactionDetailsAPI extends BaseAPI {
	

	
	
	String apiPath="/v1/c2sReceiver/c2sprodtxndetails";
	
	String contentType;
	String accessToken;
	C2SGetTransactionDetailsRequestPojo c2SGetTransactionDetailsRequestPojo = new C2SGetTransactionDetailsRequestPojo();

	EncoderConfig encoderconfig = new EncoderConfig();
	public C2SGetTransactionDetailsAPI(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(C2SGetTransactionDetailsRequestPojo c2SGetTransactionDetailsRequestPojo) {
			this.c2SGetTransactionDetailsRequestPojo =c2SGetTransactionDetailsRequestPojo;
			}
	
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2SGetTransactionDetailsRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		String s=apiResponse.asString();
		Log.info(s);
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
	}



}



