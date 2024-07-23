package restassuredapi.api.validatevoucherinfo;
import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cstockreturnrequestpojo.C2CStockReturnRequestPojo;
import restassuredapi.pojo.vouchervalidateinforequestpojo.VoucherValidateInfoRequestPojo;


public class ValidateVoucherInfoAPI extends BaseAPI{
		
		String apiPath="v1/voucher/validatetvoucherinfo";
		String contentType;
		String accessToken;
		VoucherValidateInfoRequestPojo voucherValidateInfoRequestPojo = new VoucherValidateInfoRequestPojo();
		
		EncoderConfig encoderconfig = new EncoderConfig();
		public ValidateVoucherInfoAPI(String baseURI,String accessToken) {
			super(baseURI);
			this.accessToken=accessToken;
			
		}
		 public void setContentType(String contentType) {
		        this.contentType = contentType;
		    }
		 
		 public void addBodyParam(VoucherValidateInfoRequestPojo voucherValidateInfoRequestPojo) {
				this.voucherValidateInfoRequestPojo =voucherValidateInfoRequestPojo;
				}
		
		protected void createRequest() {
			requestSpecBuilder.setBaseUri(baseURI);
			requestSpecBuilder.setBasePath(apiPath);
			requestSpecBuilder.setConfig(RestAssured.config()
	                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
			requestSpecBuilder.setContentType(contentType);
			requestSpecBuilder.setBody(voucherValidateInfoRequestPojo);
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



