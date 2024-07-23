
package restassuredapi.test;
 
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
 
@ModuleManager(name = Module.O_AUTHENTICATION)
public class OAuthenticationTest extends BaseTest {
 
       static String moduleCode;
       OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
       OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
      
      
HashMap<String, String> returnMap = new HashMap<String, String>();
Map<String, Object> headerMap = new HashMap<String, Object>();
 
protected static String accessToken;
      
 
 
       @DataProvider(name = "userData")
       public Object[][] TestDataFeed1() {
              String MasterSheetPath = _masterVO.getProperty("DataProvider");
              ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
              int rowCountoperator = ExcelUtility.getRowCount();
              ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
              int rowCount = ExcelUtility.getRowCount();
              Object[][] Data = new Object[rowCount+rowCountoperator][7];
              int j=0;
              for (int i = 1; i <= rowCount; i++) {
                     Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                     Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                     Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                     Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                     Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                     Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                     Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                     j++;
                     }
      
              ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
              for (int i = 1; i <=rowCountoperator; i++) {
                     Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                     Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                     Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                     Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                     Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
                     Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                     Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                     j++;
                     }            
 
              return Data;
       }
 
      
       public void setHeaders() {
              headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
              headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
              headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
              headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
              headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
              headerMap.put("requestGatewayType",_masterVO.getProperty("requestGatewayType") );
              headerMap.put("scope", _masterVO.getProperty("scope"));
              headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
 
       }
       public void setupData(String data1, String data2) {
              oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
              oAuthenticationRequestPojo.setIdentifierValue(data1);
              oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
             
             
       }
      
       @Test(dataProvider = "userData")
       @TestManager(TestKey="PRETUPS-6455")
       public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
              final String methodName = "Test_OAuthenticationTest";
              Log.startTestCase(methodName);
 
              CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
              moduleCode = CaseMaster.getModuleCode();
              currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
              currentNode.assignCategory("REST");
              setHeaders();
              if(_masterVO.getProperty("identifierType").equals("loginid"))
              setupData(loginID, password);
              else if(_masterVO.getProperty("identifierType").equals("msisdn"))
              setupData(msisdn, PIN);
              OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),headerMap);
              oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
              oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
              oAuthenticationAPI.setExpectedStatusCode(200);
              oAuthenticationAPI.perform();
              oAuthenticationResponsePojo = oAuthenticationAPI
                           .getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
              long statusCode = oAuthenticationResponsePojo.getStatus();
             
              accessToken = oAuthenticationResponsePojo.getToken();
              Assert.assertEquals(statusCode, 200);
              Assertion.assertEquals(Long.toString(statusCode), "200");
              Assertion.completeAssertions();
              Log.endTestCase(methodName);
             
             
       }
      
}