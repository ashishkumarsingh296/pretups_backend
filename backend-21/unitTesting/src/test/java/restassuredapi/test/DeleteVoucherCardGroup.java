package restassuredapi.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import groovy.ui.SystemOutputInterceptor;
import restassuredapi.api.vouchercardgroupAPI.DeleteVoucherCardGroupApi;
import restassuredapi.pojo.deletevouchercardgrouprequestpojo.Data;
import restassuredapi.pojo.deletevouchercardgrouprequestpojo.DeleteVoucherCardGroupRequestPojo;
import restassuredapi.pojo.deletevouchercardgroupresponsepojo.DeleteVoucherCardGroupResponsePojo;
@ModuleManager(name = Module.REST_DELETE_VOUCHER_CARD_GROUP)
public class DeleteVoucherCardGroup extends BaseTest{
	
	 private static int NetworkAdminDataSheetRowNum = 0;
	 private static String NetworkADM_Login = null;
	  private static String NetworkADM_Password = null;
	static String moduleCode;
	HashMap<String,String> transfer_Details=new HashMap<String,String>(); 


	DeleteVoucherCardGroupRequestPojo deleteVoucherCardGroupRequestPojo = new DeleteVoucherCardGroupRequestPojo();
	DeleteVoucherCardGroupResponsePojo deleteVoucherCardGroupResponsePojo = new DeleteVoucherCardGroupResponsePojo();
	Data data = new Data();
	public HashMap<String,String> getExcelData() {
       
		HashMap<String,String> tranferDetails=new HashMap<String,String>();    

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();

        while (NetworkAdminDataSheetRowNum <= rowCount) {
            String ParentCategoryCode = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_CODE, NetworkAdminDataSheetRowNum);
            String CategoryCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, NetworkAdminDataSheetRowNum);
            if (ParentCategoryCode.equals(PretupsI.SUPERADMIN_CATCODE) && CategoryCode.equals(PretupsI.NETWORKADMIN_CATCODE)) {
                NetworkADM_Login = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, NetworkAdminDataSheetRowNum);
                NetworkADM_Password = ExcelUtility.getCellData(0, ExcelI.PASSWORD, NetworkAdminDataSheetRowNum);
                break;
            }

            NetworkAdminDataSheetRowNum++;
        }

        tranferDetails.put("Login_Id",NetworkADM_Login );
	    tranferDetails.put("Password", NetworkADM_Password);
	    return tranferDetails;
    }
    
   

	

	
	
    public void setupData(){
    	transfer_Details=getExcelData();

		deleteVoucherCardGroupRequestPojo.setIdentifierType(transfer_Details.get("Login_Id"));
		deleteVoucherCardGroupRequestPojo.setIdentifierValue(transfer_Details.get("Password"));
		data.setServiceTypeDesc(_masterVO.getProperty("CardGroupServiceTypeDesc"));
		data.setSubServiceTypeDesc(_masterVO.getProperty("CardGroupSubServiceTypeDesc"));
		
		String CardGroupName=DBHandler.AccessHandler.getCardGroupName(_masterVO.getProperty("cardGroupModifiedBy"), "1", "VCN");
		
		data.setCardGroupSetName(CardGroupName);
		data.setVersion("1");
		data.setModifiedBy(_masterVO.getProperty("cardGroupModifiedBy"));
		data.setModuleCode(_masterVO.getProperty("CardGroupModuleCode"));
		data.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		//updating status of card group before deleting so that it can be deleted
		DBHandler.AccessHandler.updateAnyColumnValue("CARD_GROUP_SET", "STATUS", "Y", "CARD_GROUP_SET_NAME", CardGroupName);
		
		deleteVoucherCardGroupRequestPojo.setData(data);
		
		
	}
	
	// Positive scenario for card group deletion
	@Test
	public void A_01_Test_deleteVoucherCardGroup_Positive() throws Exception
	{  
		
		
		final String methodName = "Test_DeleteVoucherCardGroupAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG1");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
	

		DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
		deleteVoucherCardGroupApi.setExpectedStatusCode(200);
 		deleteVoucherCardGroupApi.perform();
		deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
		int statusCode =deleteVoucherCardGroupResponsePojo.getStatusCode();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);	
		}
	
	// Negative - Service Type is not given in the api
	@Test
	public void A_02_Test_SetServiceTypeDescBlank() throws Exception
	{   final String methodName = "Test_DeleteVoucherCardGroupAPI";
    	Log.startTestCase(methodName);
	
    	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG2");
    	moduleCode = CaseMaster.getModuleCode();
	
    	currentNode = test.createNode(CaseMaster.getExtentCase());
    	currentNode.assignCategory("REST");
    	setupData();
		DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		data.setServiceTypeDesc("");
		
		deleteVoucherCardGroupRequestPojo.setData(data);
		deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
		deleteVoucherCardGroupApi.setExpectedStatusCode(200);
		deleteVoucherCardGroupApi.perform();
		deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
		String message =deleteVoucherCardGroupResponsePojo.getGlobalError();
		Assert.assertEquals(message, "ServiceType is empty");
		Assertion.assertEquals(message, "ServiceType is empty");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// Negative - Sub-Service Type is not given in the api
	@Test
	public void A_03_Test_SubServiceTypeDescBlank() throws Exception
	{   final String methodName = "Test_DeleteVoucherCardGroupAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		data.setSubServiceTypeDesc("");		
		deleteVoucherCardGroupRequestPojo.setData(data);
		
		deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
		deleteVoucherCardGroupApi.setExpectedStatusCode(200);
		deleteVoucherCardGroupApi.perform();
		deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
		String message =deleteVoucherCardGroupResponsePojo.getGlobalError();
		Assert.assertEquals(message, "SubService Type is empty");
		Assertion.assertEquals(message, "SubService Type is empty");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// Negative - Card Group name is not given in the api
	@Test
	public void A_04_Test_CardGroupSetNameBlank() throws Exception
	{	final String methodName = "Test_DeleteVoucherCardGroupAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		data.setCardGroupSetName("");	
		deleteVoucherCardGroupRequestPojo.setData(data);
		
		deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
		deleteVoucherCardGroupApi.setExpectedStatusCode(200);
		deleteVoucherCardGroupApi.perform();
		deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
		String message =deleteVoucherCardGroupResponsePojo.getGlobalError();
		Assert.assertEquals(message, "CardGroup SetName is empty");
		Assertion.assertEquals(message, "CardGroup SetName is empty");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// Negative - Version of card group is not given in the api
	@Test
	public void A_05_Test_VrsionBlank() throws Exception
	{		final String methodName = "Test_DeleteVoucherCardGroupAPI";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG5");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
		setupData();
		DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		
		
		data.setVersion("");
		deleteVoucherCardGroupRequestPojo.setData(data);
		
		deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
		deleteVoucherCardGroupApi.setExpectedStatusCode(200);
		deleteVoucherCardGroupApi.perform();
		deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
		String message =deleteVoucherCardGroupResponsePojo.getGlobalError();
		Assert.assertEquals(message, "Card Group Version is empty");
		Assertion.assertEquals(message, "Card Group Version is empty");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// Negative - Details of a default card group is given
	@Test
	public void A_06_Test_ModifiedByBlank() throws Exception
	{	final String methodName = "Test_DeleteVoucherCardGroupAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
	
	
		data.setModifiedBy("");

		deleteVoucherCardGroupRequestPojo.setData(data);
		deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
		deleteVoucherCardGroupApi.setExpectedStatusCode(200);
		deleteVoucherCardGroupApi.perform();
		deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
		String message =deleteVoucherCardGroupResponsePojo.getGlobalError();
		Assert.assertEquals(message, "Modified By is empty");
		Assertion.assertEquals(message, "Modified By is empty");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// Negative - Details of a card group which has transfer rule associated with it.
	@Test
	public void A_07_Test_ModuleCodeBlank() throws Exception
	{final String methodName = "Test_DeleteVoucherCardGroupAPI";
	Log.startTestCase(methodName);

	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG7");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(CaseMaster.getExtentCase());
	currentNode.assignCategory("REST");
	setupData();
	DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
	deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));


	data.setModuleCode("");

	deleteVoucherCardGroupRequestPojo.setData(data);
	deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
	deleteVoucherCardGroupApi.setExpectedStatusCode(200);
	deleteVoucherCardGroupApi.perform();
	deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
	String message =deleteVoucherCardGroupResponsePojo.getGlobalError();
	Assert.assertEquals(message, "Module Code is empty");
	Assertion.assertEquals(message, "Module Code is empty");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	
	
	
	@Test
	public void A_08_Test_NetworkCodeBlank() throws Exception
	{final String methodName = "Test_DeleteVoucherCardGroupAPI";
	Log.startTestCase(methodName);

	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG8");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(CaseMaster.getExtentCase());
	currentNode.assignCategory("REST");
	setupData();
	DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
	deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));


	data.setNetworkCode("");

	deleteVoucherCardGroupRequestPojo.setData(data);
	deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
	deleteVoucherCardGroupApi.setExpectedStatusCode(200);
	deleteVoucherCardGroupApi.perform();
	deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
	String message =deleteVoucherCardGroupResponsePojo.getGlobalError();
	Assert.assertEquals(message, "Network Code is empty");
	Assertion.assertEquals(message, "Network Code is empty");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	
	@Test
	public void A_09_Test_IdentifierTypeBlank() throws Exception
	{final String methodName = "Test_DeleteVoucherCardGroupAPI";
	Log.startTestCase(methodName);

	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG9");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(CaseMaster.getExtentCase());
	currentNode.assignCategory("REST");
	setupData();
	DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
	deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));


	deleteVoucherCardGroupRequestPojo.setIdentifierType("");

	deleteVoucherCardGroupRequestPojo.setData(data);
	deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
	deleteVoucherCardGroupApi.setExpectedStatusCode(200);
	deleteVoucherCardGroupApi.perform();
	deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
	String message =deleteVoucherCardGroupResponsePojo.getFormError();
	Assert.assertEquals(message, "user.invalidloginid");
	Assertion.assertEquals(message, "user.invalidloginid");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	
	@Test
	public void A_10_Test_IdentifierValueBlank() throws Exception
	{final String methodName = "Test_DeleteVoucherCardGroupAPI";
	Log.startTestCase(methodName);

	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTDVCG10");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(CaseMaster.getExtentCase());
	currentNode.assignCategory("REST");
	setupData();
	DeleteVoucherCardGroupApi deleteVoucherCardGroupApi = new DeleteVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
	deleteVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));


	deleteVoucherCardGroupRequestPojo.setIdentifierValue("");

	deleteVoucherCardGroupRequestPojo.setData(data);
	deleteVoucherCardGroupApi.addBodyParam(deleteVoucherCardGroupRequestPojo);
	deleteVoucherCardGroupApi.setExpectedStatusCode(200);
	deleteVoucherCardGroupApi.perform();
	deleteVoucherCardGroupResponsePojo =deleteVoucherCardGroupApi.getAPIResponseAsPOJO(DeleteVoucherCardGroupResponsePojo.class);
	String message =deleteVoucherCardGroupResponsePojo.getFormError();
	Assert.assertEquals(message, "user.invalidpassword");
	Assertion.assertEquals(message, "user.invalidpassword");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	
		
	
	
	
}
