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

import restassuredapi.api.redissublookupcacheapi.SubLookupCacheApi;
import restassuredapi.pojo.redissublookupcacheresponsepojo.SubLookupCacheResponsePojo;
@ModuleManager(name = Module.REST_GET_SUBLOOKUPCACHE)
public class SubLookupCacheApiTest extends BaseTest {
	static String moduleCode;
	
	SubLookupCacheResponsePojo subLookupCacheResponsePojo = new SubLookupCacheResponsePojo();
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][4];
		int j=0;
		for(int i=1;i<=rowCount;i++) {
			Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			
			j++;
		}
         return Data;
	}
	
	Map<String, Object> headerMap = new HashMap<String, Object>();
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
	
	@Test(dataProvider="userData")
	@TestManager(TestKey="PRETUPS-15679")
	public void A_01_Test_success(String loginID, String password, String PIN,String categoryName) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("REA3");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		SubLookupCacheApi subLookupCacheApi = new SubLookupCacheApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER));
		subLookupCacheApi.setContentType(_masterVO.getProperty("contentType"));
		subLookupCacheApi.setExpectedStatusCode(200);
		subLookupCacheApi.perform();
		subLookupCacheResponsePojo = subLookupCacheApi.getAPIResponseAsPOJO(SubLookupCacheResponsePojo.class);
		
		 String status = subLookupCacheResponsePojo.getStatus();
		 
		 Assert.assertEquals(200, Integer.parseInt(status));
		 Assertion.assertEquals(status, "200");
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
		
}
