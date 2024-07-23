package restassuredapi.api.o2CSearchDetailsApi;

import com.utils.Log;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.o2CSearchDetailsRequestpojo.SearchDetailsRequestPojo;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class O2CSearchDetailsApi extends BaseAPI {

    String apiPath = "/v1/o2c/getSearchDetails";
    String contentType;
    String accessToken;
    String identifierType;
    String identifierValue;
    SearchDetailsRequestPojo searchDetailsRequestPojo = new SearchDetailsRequestPojo();
    EncoderConfig encoderconfig = new EncoderConfig();
    public void addBodyParam (SearchDetailsRequestPojo searchDetailsRequestPojo){
        this.searchDetailsRequestPojo=searchDetailsRequestPojo;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public O2CSearchDetailsApi(String baseURI, String accessToken){
        super(baseURI);
        this.accessToken=accessToken;
    }
    protected void createRequest() {
        requestSpecBuilder.setBaseUri(baseURI);
        requestSpecBuilder.setBasePath(apiPath);
        requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
        requestSpecBuilder.addQueryParam("identifierType", identifierType);
        requestSpecBuilder.addQueryParam("identifierValue", identifierValue);
        requestSpecBuilder.setContentType(contentType);
        requestSpecBuilder.setBody(searchDetailsRequestPojo);
        requestSpecification = requestSpecBuilder.build();
        HashMap<String, String> queryParams = new HashMap<String, String>();
        HashMap<String, String> pathParams = new HashMap<String, String>();
        queryParams.put("identifierType", identifierType);
        queryParams.put("identifierValue", identifierValue);
        logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
    }

    @Override
    protected void executeRequest() {
        apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
        String s = apiResponse.asString();
        Log.info(s);
        System.out.println(s);
    }

    @Override
    protected void validateResponse() {
        responseSpecBuilder.expectStatusCode(expectedStatusCode);
        responseSpecification = responseSpecBuilder.build();
    }
}
