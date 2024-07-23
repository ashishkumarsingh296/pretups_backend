package restassuredapi.api.networkstockcreation;

import static io.restassured.RestAssured.given;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.networkstockcreationrequestpojo.NetworkStockCreationRequestPojo;
import restassuredapi.pojo.networkstockcreationresponsepojo.NetworkStockCreationResponsePojo;

public class NetworkStockCreationAPI extends BaseAPI {

	String apiPath="/networkStock/create";
	String contentType;
	NetworkStockCreationRequestPojo networkStockCreationRequestPojo = new NetworkStockCreationRequestPojo();
	NetworkStockCreationResponsePojo networkStockCreationResponsePojo = new NetworkStockCreationResponsePojo();
	public NetworkStockCreationAPI(String baseURI) {
		super(baseURI);
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(NetworkStockCreationRequestPojo networkStockCreationRequestPojo) {
			this.networkStockCreationRequestPojo =networkStockCreationRequestPojo;
			}
	
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		//requestSpecBuilder.setBody(bodyParam.toString());
		requestSpecBuilder.setBody(networkStockCreationRequestPojo);
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
