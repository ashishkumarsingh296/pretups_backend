package restassuredapi.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.classes.BaseTest;
import com.commons.MasterI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.Log;
import com.utils._masterVO;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class BaseAPI extends BaseTest{
	
	 	protected String baseURI;
	    protected RequestSpecBuilder requestSpecBuilder;
	    protected RequestSpecification requestSpecification;
	    protected ResponseSpecBuilder responseSpecBuilder;
	    protected ResponseSpecification responseSpecification;
	    protected Response apiResponse;
	    protected int expectedStatusCode;

	    public BaseAPI(String baseURI){
	        this.baseURI=baseURI;
	        requestSpecBuilder=new RequestSpecBuilder();
	        responseSpecBuilder=new ResponseSpecBuilder();
	    }

	    public void setRequestSpecifications() {
	    	requestSpecBuilder.setBaseUri(baseURI);
			requestSpecBuilder.setConfig(RestAssured.config()
					.encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)));
			requestSpecBuilder.setContentType(ContentType.JSON);
			requestSpecBuilder.addHeader("Referer", _masterVO.getMasterValue(MasterI.REQUEST_HEADER_REFERER));
	    }
	    
	    public void logRequestBody(Object value) {
			ObjectMapper mapper = new ObjectMapper();
			String reqBody = null;
			try {
				reqBody = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			Log.info("Request body: " + reqBody);
		}
	    
	    protected Response getApiResponse() {
	        return apiResponse;
	    }

	    public String getApiResponseAsString() {
	        return apiResponse.asString();
	    }

	    public <T> T getAPIResponseAsPOJO(Class<T> type) throws IOException {
	    	ObjectMapper objectMapper = new ObjectMapper();
	    	objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	        return objectMapper.readValue(getApiResponseAsString(),type);
	    }
	    
	    public int getExpectedStatusCode() {
	        return expectedStatusCode;
	    }

	    public void setExpectedStatusCode(int expectedStatusCode) {
	        this.expectedStatusCode = expectedStatusCode;
	    }

	    protected abstract void createRequest();
	    protected abstract void executeRequest();
	    protected abstract void validateResponse();

	    public void perform(){
	        createRequest();
	        executeRequest();
	        validateResponse();
	    }

	    public void logApiUrlAndParameters(String baseURI, String apiPath, HashMap<String, String> queryParams, HashMap<String, String> pathParams)
	    {
	    	String APIURL = baseURI + apiPath;
	    	Log.info("API URL: " + APIURL.toString());
	    	if(pathParams != null && pathParams.size() > 0)
	    	{
	    		Log.info("Path Params :\n");
	    		for (Entry<String, String> pathParam : pathParams.entrySet()) {
					String pathParamName = pathParam.getKey();
					String pathParamValue = pathParam.getValue();
					Log.info(pathParamName + " : " + pathParamValue + "\n");
				}
	    	}
	    	
	    	if(queryParams != null && queryParams.size() > 0)
	    	{
	    		Log.info("Query Params :\n");
	    		for (Entry<String, String> queryParam : queryParams.entrySet()) {
					String queryParamName = queryParam.getKey();
					String queryParamValue = queryParam.getValue();
					Log.info(queryParamName + " : " + queryParamValue + "\n");
				}
	    	}
	    }
}
