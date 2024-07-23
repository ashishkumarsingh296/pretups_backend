package restassuredapi.api.c2cviewtransferdetails;

import static io.restassured.RestAssured.given;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cviewtransferdetailsrequestpojo.C2CViewTransferDetailsRequestPojo;
import restassuredapi.pojo.c2cviewtransferdetailsresponsepojo.C2CViewTransferDetailsResponsePojo;

import java.util.HashMap;

public class C2CViewTransferDetailsAPI extends BaseAPI {

	String apiPath="/v1/c2cReceiver/c2cviewvc";
	
	String contentType;
	String accessToken;
	C2CViewTransferDetailsRequestPojo c2CViewTransferDetailsRequestPojo = new C2CViewTransferDetailsRequestPojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	public C2CViewTransferDetailsAPI(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(C2CViewTransferDetailsRequestPojo c2CViewTransferDetailsRequestPojo) {
			this.c2CViewTransferDetailsRequestPojo =c2CViewTransferDetailsRequestPojo;
			}
	 @Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2CViewTransferDetailsRequestPojo);
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
		apiResponse.then().spec(responseSpecification);
	}
}
