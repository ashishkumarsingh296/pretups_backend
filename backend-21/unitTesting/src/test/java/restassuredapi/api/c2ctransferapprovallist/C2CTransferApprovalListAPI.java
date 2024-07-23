package restassuredapi.api.c2ctransferapprovallist;
import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2ctransferapprovallistrequestpojo.C2CTransferApprovalListRequestPojo;
import restassuredapi.pojo.c2ctransferapprovallistresponsepojo.C2CTransferApprovalListResponsePojo;;

public class C2CTransferApprovalListAPI extends BaseAPI  {
	String apiPath="/v1/c2cReceiver/c2cvcrapplist";
	String accessToken;
	String contentType;
	C2CTransferApprovalListRequestPojo c2CTransferApprovalListRequestPojo = new C2CTransferApprovalListRequestPojo();
	C2CTransferApprovalListResponsePojo c2CTransferApprovalListResponsePojo = new C2CTransferApprovalListResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	public C2CTransferApprovalListAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(C2CTransferApprovalListRequestPojo c2CTransferApprovalListRequestPojo) {
			this.c2CTransferApprovalListRequestPojo =c2CTransferApprovalListRequestPojo;
			}
	 @Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2CTransferApprovalListRequestPojo);
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
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
