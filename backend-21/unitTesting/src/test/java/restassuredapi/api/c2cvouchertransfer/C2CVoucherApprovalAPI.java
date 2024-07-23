package restassuredapi.api.c2cvouchertransfer;
import static io.restassured.RestAssured.given;

import java.util.HashMap;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cvoucherapprovalrequestpojo.C2CVoucherApprovalRequestPojo;
import restassuredapi.pojo.c2cvoucherapprovalresponsepojo.C2CVoucherApprovalResponsePojo;


public class C2CVoucherApprovalAPI extends BaseAPI{
	

		
		String apiPath="/v1/c2cReceiver/c2cvoucherapproval";
		String contentType;
		String accessToken;
		
		C2CVoucherApprovalRequestPojo c2cVoucherApprovalRequestPojo = new C2CVoucherApprovalRequestPojo();
		C2CVoucherApprovalResponsePojo c2cVoucherApprovalResponsePojo = new C2CVoucherApprovalResponsePojo();
		EncoderConfig encoderconfig = new EncoderConfig();
		public C2CVoucherApprovalAPI(String baseURI, String accessToken) {
			super(baseURI);
			this.accessToken=accessToken;
		}
		 public void setContentType(String contentType) {
		        this.contentType = contentType;
		    }
		 
		 public void addBodyParam(C2CVoucherApprovalRequestPojo c2cVoucherApprovalRequestPojo) {
				this.c2cVoucherApprovalRequestPojo =c2cVoucherApprovalRequestPojo;
				}
		 @Override
		protected void createRequest() {
			requestSpecBuilder.setBaseUri(baseURI);
			requestSpecBuilder.setBasePath(apiPath);
			requestSpecBuilder.setConfig(RestAssured.config()
	                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
			requestSpecBuilder.setContentType(contentType);
			requestSpecBuilder.setBody(c2cVoucherApprovalRequestPojo);
			requestSpecification = requestSpecBuilder.build();
			HashMap<String, String> queryParams = new HashMap<String, String>();
			HashMap<String, String> pathParams = new HashMap<String, String>();
			logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		}

		@Override
		protected void executeRequest() {
			apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
			String s=apiResponse.asString();
			System.out.println(s);
		}

		@Override
		protected void validateResponse() {
			responseSpecBuilder.expectStatusCode(expectedStatusCode);
			responseSpecification = responseSpecBuilder.build();
			apiResponse.then().spec(responseSpecification);
		}


	}



