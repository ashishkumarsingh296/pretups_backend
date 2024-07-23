package restassuredapi.api.c2cstock;
import static io.restassured.RestAssured.given;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.C2CBuyStockInitiateRequestPojo;

import java.util.HashMap;


public class C2CBuyStockTransferInitiateAPI extends BaseAPI{
	

		
		String apiPath="/v1/c2cReceiver/c2ctrfini";
		String contentType;
		String accessToken;
		C2CBuyStockInitiateRequestPojo c2CBuyStockInitiateRequestPojo = new C2CBuyStockInitiateRequestPojo();
		
		EncoderConfig encoderconfig = new EncoderConfig();
		public C2CBuyStockTransferInitiateAPI(String baseURI, String accessToken) {
			super(baseURI);
			this.accessToken = accessToken;
		}
		 public void setContentType(String contentType) {
		        this.contentType = contentType;
		    }
		 
		 public void addBodyParam(C2CBuyStockInitiateRequestPojo c2CBuyStockInitiateRequestPojo) {
				this.c2CBuyStockInitiateRequestPojo =c2CBuyStockInitiateRequestPojo;
				}
		
		protected void createRequest() {
			requestSpecBuilder.setBaseUri(baseURI);
			requestSpecBuilder.setBasePath(apiPath);
			requestSpecBuilder.setConfig(RestAssured.config()
	                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
			requestSpecBuilder.setContentType(contentType);
			requestSpecBuilder.setBody(c2CBuyStockInitiateRequestPojo);
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



