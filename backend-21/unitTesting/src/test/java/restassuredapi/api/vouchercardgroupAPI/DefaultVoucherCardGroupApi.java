package restassuredapi.api.vouchercardgroupAPI;

import static io.restassured.RestAssured.given;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.defaultvouchercardgrouprequestpojo.DefaultVoucherCardGroupRequestPojo;


public class DefaultVoucherCardGroupApi extends BaseAPI {

	String apiPath="/cardGroup/setDefaultCardGroupSet";
	String contentType;
	DefaultVoucherCardGroupRequestPojo defaultVoucherCardGroupRequestPojo= new DefaultVoucherCardGroupRequestPojo();
	
	public DefaultVoucherCardGroupApi(String baseURI) {
		super(baseURI);
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	public void addBodyParam(DefaultVoucherCardGroupRequestPojo defaultVoucherCardGroupRequestPojo) {
		this.defaultVoucherCardGroupRequestPojo =defaultVoucherCardGroupRequestPojo;
		}
	

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(defaultVoucherCardGroupRequestPojo);
		//requestSpecBuilder.setBody(Joiner.on("&").withKeyValueSeparator("=").join(bodyParams));
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

