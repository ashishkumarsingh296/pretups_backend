package restassuredapi.test;

import static org.testng.Assert.ARRAY_MISMATCH_TEMPLATE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Assert;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.vouchercardgroupAPI.AddVoucherCardGroupApi;
import restassuredapi.pojo.addvouchercardgrouprequestpojo.AddVoucherCardGroupRequestPojo;
import restassuredapi.pojo.addvouchercardgrouprequestpojo.BonusAccList;
import restassuredapi.pojo.addvouchercardgrouprequestpojo.CardGroupDetails;
import restassuredapi.pojo.addvouchercardgrouprequestpojo.CardGroupList;
import restassuredapi.pojo.addvouchercardgrouprequestpojo.Data;
import restassuredapi.pojo.addvouchercardgroupresponsepojo.AddVoucherCardGroupResponsePojo;

@ModuleManager(name = Module.REST_ADD_VOUCHER_CARD_GROUP)
public class AddVoucherCardgroup extends BaseTest {
	
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     //current time
     SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
     String current_time_str = time_formatter.format(System.currentTimeMillis());
     
	static String moduleCode;
	
	Data data;
	CardGroupDetails cardGroupDetails; 
	CardGroupList cardGroupListobject;
	BonusAccList bonusAccListobj;
	
	AddVoucherCardGroupRequestPojo addVoucherCardGroupRequestPojo;
	
	ArrayList<CardGroupList> cardGroupList;
	
	ArrayList<BonusAccList> bonusAccList;
	
	
	HashMap<String,String> transfer_Details=new HashMap<String,String>(); 
	
	//RandomGeneration randomGeneration = new RandomGeneration();

	public HashMap<String, String> getExcelData() {

		HashMap<String, String> returnMap = new HashMap<String, String>();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int OperatorRowCount = ExcelUtility.getRowCount();
		for (int i = 1; i < OperatorRowCount; i++) {
			String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			if (CategoryName.equals("NWADM") && (!LoginID.equals(null) || !LoginID.equals(""))) {
				returnMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
				returnMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
				returnMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				returnMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
				break;
			}
		}
		return returnMap;
	}
	
	
    public void setupData() {
    	data = new Data();
    	cardGroupDetails=new CardGroupDetails();
    	cardGroupListobject= new CardGroupList();
    	bonusAccListobj = new BonusAccList();
    	cardGroupList =new ArrayList<CardGroupList>();
    	 addVoucherCardGroupRequestPojo=new AddVoucherCardGroupRequestPojo();
    	 
    	 bonusAccList= new ArrayList<BonusAccList>();
    	
		transfer_Details= getExcelData();
		
		RandomGeneration randStr = new RandomGeneration();
		randStr.randomNumeric(5);
		
		addVoucherCardGroupRequestPojo.setIdentifierType(transfer_Details.get("LOGIN_ID"));
		addVoucherCardGroupRequestPojo.setIdentifierValue(transfer_Details.get("PASSWORD"));
		
		cardGroupDetails.setStatus(_masterVO.getProperty("status"));
		cardGroupDetails.setCreatedBy(_masterVO.getProperty("vCardCreatedBy"));
		cardGroupDetails.setModuleCode("P2P");
		cardGroupDetails.setModifiedBy(_masterVO.getProperty("vCardModifiedBy"));
		cardGroupDetails.setServiceTypeDesc(_masterVO.getProperty("serviceType"));
		cardGroupDetails.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		cardGroupDetails.setCardGroupSetName("VCNnewTest"+ randStr.randomNumeric(5));
		cardGroupDetails.setSubServiceTypeDescription(_masterVO.getProperty("subServiceType"));
		cardGroupDetails.setDefaultCardGroup(_masterVO.getProperty("setN"));
		cardGroupDetails.setSetTypeName(_masterVO.getProperty("cardType"));
		cardGroupDetails.setApplicableFromDate(currentDate);
		cardGroupDetails.setApplicableFromHour(current_time_str);
		data.setCardGroupDetails(cardGroupDetails);
		//creating cardGroupList
		
		int j=1,k=1;
		for(int i=0;i<j;i++)
		{
			
			cardGroupListobject.setStartRange("");
			cardGroupListobject.setEndRange("");
			cardGroupListobject.setValidityPeriodTypeDesc(_masterVO.getProperty("validityPeriodType"));
			cardGroupListobject.setValidityPeriod(_masterVO.getProperty("validityPeriod"));
			cardGroupListobject.setGracePeriod(_masterVO.getProperty("validityPeriod"));
			cardGroupListobject.setSenderTax1Name("");
			cardGroupListobject.setSenderTax1Type("");
			cardGroupListobject.setSenderTax1RateAsString("");
			cardGroupListobject.setSenderTax2Name("");
			cardGroupListobject.setSenderTax2Type("");
			cardGroupListobject.setSenderTax2RateAsString("");
			cardGroupListobject.setReceiverTax1Name(_masterVO.getProperty("tax1Name"));
			cardGroupListobject.setReceiverTax1Type(_masterVO.getProperty("tax1Type"));
			cardGroupListobject.setReceiverTax1RateAsString(_masterVO.getProperty("tax1Rate"));
			cardGroupListobject.setReceiverTax2Name(_masterVO.getProperty("tax2Name"));
			cardGroupListobject.setReceiverTax2Type(_masterVO.getProperty("tax1Type"));
			cardGroupListobject.setReceiverTax2RateAsString(_masterVO.getProperty("tax1Rate"));
			cardGroupListobject.setSenderAccessFeeType("");
			cardGroupListobject.setSenderAccessFeeRateAsString("");
			cardGroupListobject.setMinSenderAccessFeeAsString("");
			cardGroupListobject.setMaxSenderAccessFeeAsString("");
			cardGroupListobject.setReceiverAccessFeeType(_masterVO.getProperty("taxType"));
			cardGroupListobject.setReceiverAccessFeeRateAsString(_masterVO.getProperty("tax1Rate"));
			cardGroupListobject.setMinReceiverAccessFeeAsString(_masterVO.getProperty("minAccess"));
			cardGroupListobject.setMaxReceiverAccessFeeAsString(_masterVO.getProperty("maxaccess"));
			cardGroupListobject.setMultipleOf("");
			cardGroupListobject.setBonusValidityValue("");
            cardGroupListobject.setOnline(_masterVO.getProperty("setN"));
            cardGroupListobject.setBoth(_masterVO.getProperty("setN"));
            cardGroupListobject.setReceiverConvFactor("1");
            cardGroupListobject.setStatus(_masterVO.getProperty("status"));
            cardGroupListobject.setCosRequired(_masterVO.getProperty("setN"));
            cardGroupListobject.setInPromoAsString("0");
            cardGroupListobject.setCardName(_masterVO.getProperty("cardName"));
            cardGroupListobject.setCardGroupCode(_masterVO.getProperty("cardGroupCode"));
            cardGroupListobject.setReversalPermitted("");
            cardGroupListobject.setReversalModifiedDate("");
            cardGroupListobject.setVoucherTypeDesc(_masterVO.getProperty("voucherTypeDigitalDes"));
            cardGroupListobject.setVoucherSegmentDesc(_masterVO.getProperty("voucherSegmentN"));
            cardGroupListobject.setVoucherDenomination(_masterVO.getProperty("enquiryVoucherDenomination"));
            cardGroupListobject.setProductName(_masterVO.getProperty("voucherProfile"));
            cardGroupListobject.setReceiverTax3Name("");
            cardGroupListobject.setReceiverTax3Type("");
            cardGroupListobject.setReceiverTax3Rate("");
            cardGroupListobject.setReceiverTax4Name("");
            cardGroupListobject.setReceiverTax4Type("");
            cardGroupListobject.setReceiverTax4Rate("");
            for(int l=0;l<k;l++)
            {
            	
 
            	bonusAccListobj.setMultFactor(_masterVO.getProperty("bonusFactor"));
            	bonusAccListobj.setBonusValidity(_masterVO.getProperty("bonusValidity"));
            	bonusAccListobj.setBonusValue(_masterVO.getProperty("bonusValue"));
            	bonusAccListobj.setType(_masterVO.getProperty("tax1Type"));
            	bonusAccListobj.setBonusName(_masterVO.getProperty("bonusName"));
            	bonusAccList.add(bonusAccListobj);
                
            }
            cardGroupListobject.setBonusAccList(bonusAccList);
			
		cardGroupList.add(cardGroupListobject)	;
		}
		//setting both to the data
		
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
	
		
	}
	
	@Test
	public void A_01_Test_addVoucherCardGroup_Positive() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getSuccessMsg();
		Assert.assertEquals(successMessage, "cardgroup.cardgroupdetailsview.successaddmessage");
		Assertion.assertEquals(successMessage, "cardgroup.cardgroupdetailsview.successaddmessage");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	// when existing cardgroup name exist 
	@Test
	public void A_02_Test_BlankLoginId() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		addVoucherCardGroupRequestPojo.setIdentifierType("");
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getFormError();
		Assert.assertEquals(successMessage, "user.invalidloginid");
		Assertion.assertEquals(successMessage, "user.invalidloginid");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	@Test
	public void A_03_Test_BlankPassword() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		addVoucherCardGroupRequestPojo.setIdentifierValue("");
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getFormError();
		Assert.assertEquals(successMessage, "user.invalidpassword");
		Assertion.assertEquals(successMessage, "user.invalidpassword");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	
	@Test
	public void A_04_Test_BlankServiceTypeDescription() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		cardGroupDetails.setServiceTypeDesc("");
		data.setCardGroupDetails(cardGroupDetails);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "9007");
		Assertion.assertEquals(successMessage, "9007");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
}
	@Test
	public void A_05_Test_BlankSubServiceTypeDescription() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		cardGroupDetails.setSubServiceTypeDescription("");
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "2002");
		Assertion.assertEquals(successMessage, "2002");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	@Test
	public void A_06_Test_BlankVoucherTypeDescription() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		cardGroupList.get(0).setVoucherTypeDesc("");
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "cardgroup.cardgroupdetails.err.msg.novouchertypefound");
		Assertion.assertEquals(successMessage, "cardgroup.cardgroupdetails.err.msg.novouchertypefound");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	@Test
	public void A_07_Test_BlankVoucherDenomination() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		cardGroupList.get(0).setVoucherDenomination("");
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "");
		Assertion.assertEquals(successMessage, "");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	
	@Test
	public void A_08_Test_BlankVoucherDenomination() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		cardGroupList.get(0).setProductName("");
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "cardgroup.cardgroupdetails.err.msg.noproductfound");
		Assertion.assertEquals(successMessage, "cardgroup.cardgroupdetails.err.msg.noproductfound");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	
	@Test
	public void A_09_Test_BlankReceiverTax1Type() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		cardGroupList.get(0).setReceiverTax1Type("");
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "error.invalid.ratetype");
		Assertion.assertEquals(successMessage, "error.invalid.ratetype");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	
	@Test
	public void A_10_Test_BlankReceiverTax2Type() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		cardGroupList.get(0).setReceiverTax2Type("");
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "error.invalid.ratetype");
		Assertion.assertEquals(successMessage, "error.invalid.ratetype");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	
	
	@Test
	public void A_11_Test_BlankBonusValue() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG11");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		//cardGroupList.get(0).getBonusAccList()
		cardGroupList.get(0).getBonusAccList().get(0).setBonusValue("");
		//bonusAccList.get(0).setBonusValue("");
		//cardGroupListobject.setBonusAccList(bonusAccList);
		//cardGroupList.add(cardGroupListobject)	;
		
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		
		//addVoucherCardGroupRequestPojo.getData().set
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "cardgroup.cp2p.error.bonusvalue.required");
		Assertion.assertEquals(successMessage, "cardgroup.cp2p.error.bonusvalue.required");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	@Test
	public void A_12_Test_BlankBonusValidity() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG12");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		cardGroupList.get(0).getBonusAccList().get(0).setBonusValidity("");
		//bonusAccList.get(0).setBonusValidity("");
		//cardGroupListobject.setBonusAccList(bonusAccList);
		//cardGroupList.add(cardGroupListobject)	;
		
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "cardgroup.cp2p.error.bonusvalidity.required");
		Assertion.assertEquals(successMessage, "cardgroup.cp2p.error.bonusvalidity.required");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	
	@Test
	public void A_13_Test_BlankBonusMultFactor() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG13");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		cardGroupList.get(0).getBonusAccList().get(0).setMultFactor("");
		//bonusAccList.get(0).setMultFactor("");
		//cardGroupListobject.setBonusAccList(bonusAccList);
		//cardGroupList.add(cardGroupListobject)	;
		
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "cardgroup.cp2p.error.bonusconvfactor.required");
		Assertion.assertEquals(successMessage, "cardgroup.cp2p.error.bonusconvfactor.required");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	
	@Test
	public void A_14_Test_BlankApplicableFromDate() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG14");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		data.getCardGroupDetails().setApplicableFromDate("");
		//cardGroupDetails.setApplicableFromDate("");
		//data.setCardGroupDetails(cardGroupDetails);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "promotrfrule.addpromoc2stransferrules.error.invalidformat");
		Assertion.assertEquals(successMessage, "promotrfrule.addpromoc2stransferrules.error.invalidformat");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	
	@Test
	public void A_15_Test_BlankApplicableFromHour() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG15");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		data.getCardGroupDetails().setApplicableFromHour("");
		//cardGroupDetails.setApplicableFromHour("");
		//data.setCardGroupDetails(cardGroupDetails);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "promotrfrule.addpromoc2stransferrules.error.invalidformat");
		Assertion.assertEquals(successMessage, "promotrfrule.addpromoc2stransferrules.error.invalidformat");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
	@Test
	public void A_16_Test_BlankNetworkCode() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG16");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		cardGroupDetails.setNetworkCode("");
		data.setCardGroupDetails(cardGroupDetails);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "9007");
		Assertion.assertEquals(successMessage, "9007");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
}
	@Test
	public void A_17_Test_BlankCardGroupSetName() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG17");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		
		cardGroupDetails.setCardGroupSetName("");
		data.setCardGroupDetails(cardGroupDetails);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		String successMessage  = addVoucherCardGroupResponsePojo.getMessageCode();
		Assert.assertEquals(successMessage, "");
		Assertion.assertEquals(successMessage, "");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
}
	
	@Test
	public void A_18_Test_BlankValidityPeriodTypeDesc() throws Exception
	{
		
		final String methodName = "Test_AddVoucherCardGroupApi";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTADDVCG18");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		addVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		cardGroupList.get(0).setValidityPeriod("");
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		
		
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
	    
		int successMessage  = addVoucherCardGroupResponsePojo.getStatusCode();
		Assert.assertEquals(successMessage, 400);
		Assertion.assertEquals(Integer.toString(successMessage) , Integer.toString(400));
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

}
}