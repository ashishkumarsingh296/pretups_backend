package restassuredapi.api.c2cstockwithdraw;
import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cstockwithdrawrequestpojo.C2CStockWithdrawRequestPojo;


public class C2CStockWithdrawAPI extends BaseAPI{
	

		
		String apiPath="v1/c2cReceiver/c2cwithdraw";
		String contentType;
		String accessToken;
		C2CStockWithdrawRequestPojo c2CStockWithdrawRequestPojo = new C2CStockWithdrawRequestPojo();
		
		EncoderConfig encoderconfig = new EncoderConfig();
		public C2CStockWithdrawAPI(String baseURI,String accessToken) {
			super(baseURI);
			this.accessToken=accessToken;
			
		}
		 public void setContentType(String contentType) {
		        this.contentType = contentType;
		    }
		 
		 public void addBodyParam(C2CStockWithdrawRequestPojo c2CStockWithdrawRequestPojo) {
				this.c2CStockWithdrawRequestPojo =c2CStockWithdrawRequestPojo;
				}
		
		protected void createRequest() {
			requestSpecBuilder.setBaseUri(baseURI);
			requestSpecBuilder.setBasePath(apiPath);
			requestSpecBuilder.setConfig(RestAssured.config()
	                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
			requestSpecBuilder.setContentType(contentType);
			requestSpecBuilder.setBody(c2CStockWithdrawRequestPojo);
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



