package restassuredapi.api.vouchercardgroupAPI;

import static io.restassured.RestAssured.given;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.addvouchercardgrouprequestpojo.AddVoucherCardGroupRequestPojo;


public class AddVoucherCardGroupApi extends BaseAPI  {

	String apiPath="cardGroup/addCardgroup";
	String contentType;
	AddVoucherCardGroupRequestPojo addVoucherCardGroupRequestPojo= new AddVoucherCardGroupRequestPojo();
	
	public AddVoucherCardGroupApi(String baseURI) {
		super(baseURI);
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	public void addBodyParam(AddVoucherCardGroupRequestPojo addVoucherCardGroupRequestPojo) {
		this.addVoucherCardGroupRequestPojo =addVoucherCardGroupRequestPojo;
		}
	

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(addVoucherCardGroupRequestPojo);
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
