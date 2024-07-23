package restassuredapi.api.c2sgetamountservicewiselist;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2sgetamountservicewiselistrequestpojo.C2SGetAmountServiceWiseListRequestPojo;
import restassuredapi.pojo.c2sgetamountservicewiselistresponsepojo.C2SGetAmountServiceWiseListResponsePojo;

public class C2SGetAmountServiceWiseListAPI extends BaseAPI {
	
	
	String apiPath="/c2s-rest-receiver/c2ssrvtrfcnt";
	
	String contentType;
	C2SGetAmountServiceWiseListRequestPojo c2SGetAmountServiceWiseListRequestPojo = new C2SGetAmountServiceWiseListRequestPojo();
	C2SGetAmountServiceWiseListResponsePojo c2SGetAmountServiceWiseListResponsePojo = new C2SGetAmountServiceWiseListResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	public C2SGetAmountServiceWiseListAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(C2SGetAmountServiceWiseListRequestPojo c2SGetAmountServiceWiseListRequestPojo) {
			this.c2SGetAmountServiceWiseListRequestPojo =c2SGetAmountServiceWiseListRequestPojo;
			}
	 @Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2SGetAmountServiceWiseListRequestPojo);
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).post();
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
