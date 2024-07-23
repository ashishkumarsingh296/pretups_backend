package restassuredapi.api.o2cownerUserListApi;

import com.utils.Log;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class O2cownerUserListApi extends BaseAPI {
    String apiPath = "/v1/o2c/getOwnerUserDetails";
    String contentType;
    String DomainCode;
    String channelOwnerCategory;
    String geoDomainCode;
    String userName;
    String accessToken;
    EncoderConfig encoderconfig = new EncoderConfig();

    public O2cownerUserListApi (String baseURI,String accessToken){
        super(baseURI);
        this.accessToken=accessToken;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDomainCode() {
        return DomainCode;
    }

    public void setDomainCode(String domainCode) {
        DomainCode = domainCode;
    }

    public String getChannelOwnerCategory() {
        return channelOwnerCategory;
    }

    public void setChannelOwnerCategory(String channelOwnerCategory) {
        this.channelOwnerCategory = channelOwnerCategory;
    }

    public String getGeoDomainCode() {
        return geoDomainCode;
    }

    public void setGeoDomainCode(String geoDomainCode) {
        this.geoDomainCode = geoDomainCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    @Override
    protected void createRequest() {
        requestSpecBuilder.setBaseUri(baseURI);
        requestSpecBuilder.setBasePath(apiPath);
        requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
        requestSpecBuilder.setContentType(contentType);
        requestSpecBuilder.addQueryParam("DomainCode", DomainCode);
        requestSpecBuilder.addQueryParam("channelOwnerCategory", channelOwnerCategory);
        requestSpecBuilder.addQueryParam("geoDomainCode", geoDomainCode);
        requestSpecBuilder.addQueryParam("userName", userName);
        requestSpecification = requestSpecBuilder.build();
        HashMap<String, String> queryParams = new HashMap<String, String>();
        HashMap<String, String> pathParams = new HashMap<String, String>();
        queryParams.put("DomainCode", DomainCode);
        queryParams.put("channelOwnerCategory", channelOwnerCategory);
        queryParams.put("geoDomainCode", geoDomainCode);
        queryParams.put("userName", userName);
        logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
    }

    @Override
    protected void executeRequest() {
        apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
        String s = apiResponse.asString();
        System.out.println(s);
        Log.info(s);
    }

    @Override
    protected void validateResponse() {
        responseSpecBuilder.expectStatusCode(expectedStatusCode);
        responseSpecification = responseSpecBuilder.build();
        apiResponse.then().spec(responseSpecification);
    }
}
