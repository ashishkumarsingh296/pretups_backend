package restassuredapi.test;
import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.classes.BaseTest;
import com.dbrepository.DBHandler;
import com.utils.Assertion;
import com.classes.CaseMaster;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.selfvoucherenquirysubscriber.SelfVoucherEnquirySubscriberAPI;

import restassuredapi.pojo.selfvoucherenquirysubscriberrequestpojo.Data;
import restassuredapi.pojo.selfvoucherenquirysubscriberrequestpojo.SelfVoucherEnquirySubscriberRequestPojo;
import restassuredapi.pojo.selfvoucherenquirysubscriberresponsepojo.DataObject;
import restassuredapi.pojo.selfvoucherenquirysubscriberresponsepojo.SelfVoucherEnquirySubscriberResponsePojo;

@ModuleManager(name = Module.REST_SELF_VOUCHER_ENQUIRY)
public class SelfVoucherEnquirySubscriber extends BaseTest{

	static String moduleCode;
	SelfVoucherEnquirySubscriberRequestPojo selfVoucherEnquirySubscriberRequestPojo= new SelfVoucherEnquirySubscriberRequestPojo();
	SelfVoucherEnquirySubscriberResponsePojo selfVoucherEnquirySubscriberResponsePojo= new SelfVoucherEnquirySubscriberResponsePojo();
	Data data =  new Data();
	
	
    public void setupData(){
			
		selfVoucherEnquirySubscriberRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		selfVoucherEnquirySubscriberRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		selfVoucherEnquirySubscriberRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		selfVoucherEnquirySubscriberRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		selfVoucherEnquirySubscriberRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		selfVoucherEnquirySubscriberRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));
		String subId=DBHandler.AccessHandler.getSubscriberMSISDN();
		data.setSubscriberMsisdn(subId);
		selfVoucherEnquirySubscriberRequestPojo.setData(data);
    }
	
	
	// with valid data.
	@Test
	public void A_01_Test_SelfVoucherEnquirySubscriberAPIPositive() throws Exception
	{
		final String methodName = "A_01_Test_SelfVoucherEnquirySubscriberAPIPositive";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSVES1");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
		selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
		selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
		selfVoucherEnquirySubscriberAPI.perform();
		
		selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI
				.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class);
		int statusCode = Integer.parseInt(selfVoucherEnquirySubscriberResponsePojo.getDataObject().getTxnstatus());
		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
		
	}
	// msisdn of other network is enterd 
	@Test
	public void A_02_SubscriberMsisdnInvalid() throws Exception   //msisdn not present
	{
		final String methodName = "A_02_Test_SelfVoucherEnquirySubscriberAPINegative";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSVES2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setSubscriberMsisdn("84583845345");
		selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
		selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
		selfVoucherEnquirySubscriberAPI.perform();
		selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
		String message =selfVoucherEnquirySubscriberResponsePojo.getDataObject().getMessage();
		Assert.assertEquals("No Vouchers are associated with the Subscriber.", message);
		Assertion.assertEquals(message, "No Vouchers are associated with the Subscriber.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test
	public void A_03_Test_SubscriberMsisdnBlank() throws Exception   //msisdn not present
	{
		final String methodName = "A_03_Test_SelfVoucherEnquirySubscriberAPINegative";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSVES3");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setSubscriberMsisdn("");
		selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
		selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
		selfVoucherEnquirySubscriberAPI.perform();
		selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
		String message =selfVoucherEnquirySubscriberResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "MSISDN can not be blank.");
		Assertion.assertEquals(message, "MSISDN can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test
	public void A_04_Test_SubscriberMsisdnNotNumeric() throws Exception
	{
		final String methodName = "A_04_Test_SelfVoucherEnquirySubscriberAPINegative";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSVES4");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setSubscriberMsisdn("WEFF333312");
		selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
		selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
		selfVoucherEnquirySubscriberAPI.perform();
		selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
		String message =selfVoucherEnquirySubscriberResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "MSISDN is not numeric.");
		Assertion.assertEquals(message, "MSISDN is not numeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	// suspended Subscriber
	@Test
	public void A_05_Test_SubscriberMsisdnSuspended() throws Exception
	{
		final String methodName = "A_05_Test_SelfVoucherEnquirySubscriberAPINegative";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE5");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
		String columnName ="msisdn";
		String[] Msisdnarr= DBHandler.AccessHandler.getP2PSubscriberWithStatusS(columnName);
		if(Msisdnarr==null)	
		{
			Assertion.assertSkip("No subscriber suspended");
			
		}
		else
		{	
		data.setSubscriberMsisdn(Msisdnarr[0]);
		selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
		selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
		selfVoucherEnquirySubscriberAPI.perform();
		selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
		String message =selfVoucherEnquirySubscriberResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Sorry, you are temporarily suspended from using this service. Please contact customer service.");
		Assertion.assertEquals(message, "Sorry, you are temporarily suspended from using this service. Please contact customer service.");
		Assertion.completeAssertions();
		}
		Log.endTestCase(methodName);
	}
	// last request underprocessDBHandler.AccessHandler.updateAnyColumnValue("users", "alternate_msisdn","", "msisdn", apiData.get(EXTGWADDALTNUMERAPI.MSISDN));
	@Test
	public void A_06_Test_SubscriberMsisdnUnderprocess() throws Exception
	{
		final String methodName = "A_06_Test_SelfVoucherEnquirySubscriberAPINegative";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE6");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
		String columnName ="msisdn";
		String[] Msisdnarr= DBHandler.AccessHandler.getP2PSubscriberWithRequestStatusU(columnName);
		if(Msisdnarr==null)	
		{
			Assertion.assertSkip("No subscriber request under process");
		}
		else
		{	
		data.setSubscriberMsisdn(Msisdnarr[0]);
		selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
		selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
		selfVoucherEnquirySubscriberAPI.perform();
		selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
		DataObject dataObject = selfVoucherEnquirySubscriberResponsePojo.getDataObject();
		String statuscode = dataObject.getTxnstatus();
		org.testng.Assert.assertNotEquals("200", statuscode);
		}
		//Log.endTestCase(methodName);
	}
	// sys pref SUBSCRIBER_VOUCHER_PIN_REQUIRED false then vcr pin not displayed
	@Test
	public void A_07_Test_SubscriberVoucherPinInvalid() throws Exception
	{
		final String methodName = "A_07_Test_SelfVoucherEnquirySubscriberAPINegative";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE7");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
		DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE","false", "PREFERENCE_CODE","SUBSCRIBER_VOUCHER_PIN_REQUIRED");
		selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
		selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
		selfVoucherEnquirySubscriberAPI.perform();
		selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
		DataObject dataObject = selfVoucherEnquirySubscriberResponsePojo.getDataObject();
		if(dataObject.getAssociatedVoucherres().isEmpty())
		{
			Assertion.assertSkip("No Voucher asociated ");
		}
		else if(dataObject.getAssociatedVoucherres().get(0).getVoucherpin()!=null)
		{
			org.testng.Assert.fail();
		
		}
		else
		{
			String statuscode = dataObject.getTxnstatus();
		
			org.testng.Assert.assertNotEquals("200", statuscode);
		}
		
		
		//Log.endTestCase(methodName);
	}
	// sys pref SUBSCRIBER_VOUCHER_PIN_REQUIRED TRUE then vcr pin  displayed
		@Test
		public void A_08_Test_SubscriberVoucherPinValid() throws Exception
		{
			final String methodName = "A_08_Test_SelfVoucherEnquirySubscriberAPINegative";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSVES8");
			moduleCode = CaseMaster.getModuleCode();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
			DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE","true", "PREFERENCE_CODE","SUBSCRIBER_VOUCHER_PIN_REQUIRED");
			selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
			selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
			selfVoucherEnquirySubscriberAPI.perform();
			selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
			DataObject dataObject = selfVoucherEnquirySubscriberResponsePojo.getDataObject();
			if(dataObject.getAssociatedVoucherres()==null)
			{
				Assertion.assertSkip("No Voucher asociated ");
			}
			else if(dataObject.getAssociatedVoucherres().get(0).getVoucherpin()!=null)
			{
			String statuscode = dataObject.getTxnstatus();
			org.testng.Assert.assertNotEquals("200", statuscode);
			}
			else
			{
				org.testng.Assert.fail();
			}
			Log.endTestCase(methodName);
		}
		
		/*@Test
		public void A_09_Test_RequestGatewayLoginIdBlank() throws Exception
		{
			final String methodName = "A_09_Test_SelfVoucherEnquirySubscriberAPINegative";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSVES9");
			moduleCode = CaseMaster.getModuleCode();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
			
			selfVoucherEnquirySubscriberRequestPojo.setReqGatewayCode("");

			selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
			selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
			selfVoucherEnquirySubscriberAPI.perform();
			selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
			String message =selfVoucherEnquirySubscriberResponsePojo.getDataObject().getMessage();

			Assert.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
			Assertion.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
*/
		@Test
		public void A_10_Test_ServicePortBlank() throws Exception
		{
			final String methodName = "A_10_Test_SelfVoucherEnquirySubscriberAPINegative";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSVES10");
			moduleCode = CaseMaster.getModuleCode();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
			
			selfVoucherEnquirySubscriberRequestPojo.setServicePort("");

			selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
			selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
			selfVoucherEnquirySubscriberAPI.perform();
			selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
			String message =selfVoucherEnquirySubscriberResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
			Assertion.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}

		
		@Test
		public void A_11_Test_SourceTypeBlank() throws Exception
		{
			final String methodName = "A_04_Test_SelfVoucherEnquirySubscriberAPINegative";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE11");
			moduleCode = CaseMaster.getModuleCode();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			SelfVoucherEnquirySubscriberAPI selfVoucherEnquirySubscriberAPI=new SelfVoucherEnquirySubscriberAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			selfVoucherEnquirySubscriberAPI.setContentType(_masterVO.getProperty("contentType"));
			
			selfVoucherEnquirySubscriberRequestPojo.setSourceType("");

			selfVoucherEnquirySubscriberAPI.addBodyParam(selfVoucherEnquirySubscriberRequestPojo);
			selfVoucherEnquirySubscriberAPI.setExpectedStatusCode(200);
			selfVoucherEnquirySubscriberAPI.perform();
			selfVoucherEnquirySubscriberResponsePojo = selfVoucherEnquirySubscriberAPI.getAPIResponseAsPOJO(SelfVoucherEnquirySubscriberResponsePojo.class); 
			String message =selfVoucherEnquirySubscriberResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "Source Type is blank.");
			Assertion.assertEquals(message, "Source Type is blank.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}

		
		
}
