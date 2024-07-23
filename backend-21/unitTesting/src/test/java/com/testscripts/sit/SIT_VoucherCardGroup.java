package com.testscripts.sit;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.P2PCardGroup;
import com.Features.P2PTransferRules;
import com.Features.VMS;
import com.Features.mapclasses.VMSMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SIT_VoucherCardGroup)
public class SIT_VoucherCardGroup extends BaseTest {
	
	 String cardGroupName;
	 HashMap<String, String> dataMap;
	 HashMap<String, String> initiateMap = new HashMap<String, String>();
	 Map<String, String> Map_CardGroup;
   	 VMSMap vmsmap = new VMSMap();
	HashMap<String, String> dataMap1;
	static boolean TestCaseCounter = false;
	String assignCategory="UAP";
	static String moduleCode;
	SIT_VMS vms = new SIT_VMS();
   	 
   	 public void initialiseVMSMap()
   	 {
   		vms.VOMSDenominationDP();
   		initiateMap = vmsmap.defaultMap(); 
   	 }
   	 
   	@Test 
	public void A_01_Test_fetchP2PServicesAndSubServices() throws SQLException {
		BuilderLogic ServicesAndsubServices = new BuilderLogic();
		ServicesAndsubServices.WriteP2PServiceAndSubServicesVoucher();
	}

	@Test(dataProvider = "serviceData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void A_02_Test_VoucherCardGroupCreation(int rowNum, String serviceName, String subService) throws InterruptedException {
        final String methodName = "Test_VoucherCardGroupCreation";
        Log.startTestCase(methodName);
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("VCNPCARDGRP1").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
        VMS vms = new VMS(driver);
        initialiseVMSMap();
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
		initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
		String mrp =  UniqueChecker.UC_VOMS_MRP();
		initiateMap.put("mrp", mrp);
		String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
		initiateMap.put("activeProfile", activeProfile);
		String denomination = mrp + ".0";
		initiateMap.put("denomination", denomination);
		String productID = activeProfile+"("+denomination+")";
		initiateMap.put("productID", productID);
		initiateMap.put("scenario", "");
		initiateMap.put("categoryName","SUADM");
		initiateMap.put("payableAmount", String.valueOf(10));
		initiateMap=vms.voucherDenominationNegative(initiateMap,"");
		initiateMap=vms.addVoucherProfileNegative(initiateMap);
		String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
			initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
			}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
				}
			}
        // Test Case - To create P2P Card Group through Network Admin as per the DataProvider
        HashMap<String, String> mapInfo = (HashMap<String, String>) p2pCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
        p2pCardGroup.writeVoucherCardGroupToExcel(mapInfo.get("CARDGROUPNAME"), mapInfo.get("CARDGROUP_SETID"), rowNum);

        Log.endTestCase(methodName);
    }
	
	/**
     *  
     * @param screen
     * @return
     */
    public static String[] getAllowedVoucherTypesForScreen(String screen) {
     
        HashMap<String, String[]> screenWiseAllowedVoucherTypeMap = new HashMap<String, String[]>();
        String[] allowedVoucherTypes = {PretupsI.VOUCHER_TYPE_DIGITAL, PretupsI.VOUCHER_TYPE_TEST_DIGITAL, 
        		PretupsI.VOUCHER_TYPE_ELECTRONIC, PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC, PretupsI.VOUCHER_TYPE_PHYSICAL, PretupsI.VOUCHER_TYPE_TEST_PHYSICAL}; 
        
        populateScreenWiseAllowedVoucherTypesMap(screen, screenWiseAllowedVoucherTypeMap);
        
        String[] tempAllowedVoucherTypes = screenWiseAllowedVoucherTypeMap.get(screen);
        if(tempAllowedVoucherTypes != null) {
            allowedVoucherTypes = tempAllowedVoucherTypes;
        }
                
        return allowedVoucherTypes;
    }
    
    /**
     * DENO:D,DT,E,ET,P,PT;PROF:D,DT,E,ET,P,PT;ACTIVE_PROF:E,ET;VOUC_GEN:D,DT,E,ET,P,PT;VOUC_APP:D,DT,E,ET,P,PT;VOUC_DOWN:P,PT;CHAN_STATUS:D,DT,E,ET,P,PT;O2C:D,DT,P,PT
     * @param screen
     * @param screenWiseAllowedVoucherTypeMap
     */
    public static void populateScreenWiseAllowedVoucherTypesMap(String screen, HashMap<String, String[]> screenWiseAllowedVoucherTypeMap) {
     
        String screenWiseAllowedVoucherTypePref = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
        if(BTSLUtil.isNullString(screenWiseAllowedVoucherTypePref)) {
                     return;
        }
       
        String[] screens = screenWiseAllowedVoucherTypePref.split(";");
        for (int i = 0; i < screens.length; i++) {
            if(BTSLUtil.isNullString(screens[i])) {
                  return;
            }
            String[] screenWiseAllowedVoucherType = screens[i].split(PretupsI.COLON);
            screenWiseAllowedVoucherTypeMap.put(screenWiseAllowedVoucherType[0], screenWiseAllowedVoucherType[1].split(PretupsI.COMMA));
        }
      
    }
	
	   @Test(dataProvider = "serviceDataVoucher")
       @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
       public void A_03_Test_P2PCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException {
       final String methodName = "Test_P2PCardGroupGroupCreation";
       Log.startTestCase(methodName, serviceName, subService);
       
       P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

       currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SP2PCARDGRP1").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.SMOKE);
       dataMap = (HashMap<String, String>) P2PCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
       cardGroupName = dataMap.get("CARDGROUPNAME");
       String actual = dataMap.get("ACTUALMESSAGE");
       String expected = MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");
       Assertion.assertEquals(actual, expected);

       Assertion.completeAssertions();
       Log.endTestCase(methodName);
   }
	
	@Test(dataProvider = "serviceDataVoucher")
    @TestManager(TestKey = "PRETUPS-568") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
   public void A_04_Test_ModifyP2PCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException {
       final String methodName = "Test_ModifyP2PCardGroup_EditCardGroup";
       Log.startTestCase(methodName, serviceName, subService);

       P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);
       currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SVCNCARDGRP1").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.SMOKE);
       String actual = P2PCardGroup.P2PCardGroupModification_EditCardGroupVoucher(serviceName, subService, cardGroupName);
       String expected = MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");
       Assertion.assertEquals(actual, expected);

       Assertion.completeAssertions();
       Log.endTestCase(methodName);
   }

   @Test(dataProvider = "serviceDataVoucher")
    @TestManager(TestKey = "PRETUPS-580") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
   public void A_05_Test_ModifyP2PCardGroup_DeleteCardGroup(String serviceName, String subService) throws InterruptedException {
       final String methodName = "Test_ModifyP2PCardGroup_DeleteCardGroup";
       Log.startTestCase(methodName, serviceName, subService);

       P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

       currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SVCNCARDGRP2").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.SMOKE);
       String actual = P2PCardGroup.P2PCardGroupDeletionVoucher(serviceName, subService, cardGroupName);

       currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SVCNCARDGRP3").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.SMOKE);
       String expected = MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successdeletemessage");
       Assertion.assertEquals(actual, expected);

       Assertion.completeAssertions();
       Log.endTestCase(methodName);
   }


   @Test(dataProvider="serviceDataUap")
	@TestManager(TestKey = "PRETUPS-323") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_06_Test_P2PCardGroupGroupCreation(String serviceName, String subService) throws InterruptedException{

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		final String methodName = "Test_P2PCardGroupGroupCreation";
       Log.startTestCase(methodName,serviceName,subService);

		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("UVCNCARDGRP1").getModuleCode();


		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("VCNPCARDGRP1").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	//UAP CardGroup TestCase2: View Card Group
	@Test(dataProvider="serviceDataUap")
	@TestManager(TestKey = "PRETUPS-324") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_07_Test_viewCardGroup(String serviceName, String subService)throws InterruptedException{

		final String methodName = "Test_viewCardGroup";
		Log.startTestCase(methodName);
		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVCNCARDGRP1").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =P2PCardGroup.viewP2PCardGroupVoucher (serviceName, subService,cardGroupName );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.voucher.view.heading");
		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}





	//UAP CardGroup TestCase3: Card Group Status
	@Test(dataProvider="serviceDataUap")
	@TestManager(TestKey = "PRETUPS-327") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_08_Test_statusCardGroup(String serviceName, String subService)throws InterruptedException{

		final String methodName = "Test_statusCardGroup";
       Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVCNCARDGRP2").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =P2PCardGroup.P2PCardGroupStatusVoucher(cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgrouplist.voucher.message.successsuspendmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);

	}
	
	//UAP CardGroup TestCase4: Modify Card Group

		@Test(dataProvider="serviceDataUap")
		@TestManager(TestKey = "PRETUPS-328") // TO BE UNCOMMENTED WITH JIRA TEST ID
		public void A_09_Test_modifyP2PCardGroup_EditCardGroup(String serviceName, String subService) throws InterruptedException{
			
			final String methodName = "Test_modifyP2PCardGroup_EditCardGroup";
	        Log.startTestCase(methodName);

			P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

			currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SVCNCARDGRP1").getExtentCase(), serviceName,subService));
			currentNode.assignCategory(assignCategory);

			String actual =P2PCardGroup.P2PCardGroupModification_EditCardGroupVoucher(serviceName, subService,cardGroupName );
			String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");
			
			
			Assertion.assertEquals(actual, expected);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	

		//UAP CardGroup TestCase5: Modify Card Group_Add New Slab
	
	@Test(dataProvider="serviceDataUap")
	@TestManager(TestKey = "PRETUPS-329") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_10_Test_modifyP2PCardGroup_AddNewSlab(String serviceName, String subService) throws InterruptedException{
		final String methodName = "Test_modifyP2PCardGroup_AddNewSlab";
       Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVCNCARDGRP3").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println("Card Group Name: " + cardGroupName);

		String actual =P2PCardGroup.P2PCardGroupModification_AddNewSlabVoucher(serviceName, subService, cardGroupName,initiateMap );
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	//UAP CardGroup TestCase6: Suspend Card Group Slab
	
	@Test(dataProvider="serviceDataUap")
	@TestManager(TestKey = "PRETUPS-330") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_11_Test_suspendedCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_suspendedCardGroupSlab";
       Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVCNCARDGRP4").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupCreationWithSuspendedSlabVoucher(serviceName, subService,initiateMap);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		String actual =dataMap.get("ACTUALMESSAGE");
		System.out.println("Card Group Name: " + cardGroupName);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}




	//UAP CardGroup TestCase7: Resume Card Group Slab
	
	@Test(dataProvider="serviceDataUap")
	@TestManager(TestKey = "PRETUPS-331") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_12_Test_resumeCardGroupSlab(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_resumeCardGroupSlab";
       Log.startTestCase(methodName);
		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVCNCARDGRP5").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		String actual =P2PCardGroup.P2PCardGroupModification_ResumeCardGroupSlabVoucher(serviceName,subService,cardGroupName,initiateMap);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successeditmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();

		Log.endTestCase(methodName);

	}


	//UAP CardGroup TestCase8: Delete Card Group 

	@Test(dataProvider="serviceDataUap")
	@TestManager(TestKey = "PRETUPS-333") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_13_Test_modifyP2PCardGroup_DeleteCardGroup(String serviceName, String subService) throws InterruptedException{

		final String methodName = "Test_modifyP2PCardGroup_DeleteCardGroup";
       Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVCNCARDGRP6").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		String actual =P2PCardGroup.P2PCardGroupDeletionVoucher(serviceName, subService, cardGroupName);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVCNCARDGRP7").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successdeletemessage");


		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	//UAP CardGroup TestCase9: Add a Future Date CardGroup

	@Test(dataProvider="serviceDataUap")
	@TestManager(TestKey = "PRETUPS-335") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_14_Test_P2PCardGroupGroupCreationFutureDate(String serviceName, String subService) throws InterruptedException {

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		final String methodName = "Test_P2PCardGroupGroupCreationFutureDate";
       Log.startTestCase(methodName);

		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVCNCARDGRP8").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);

		dataMap=(HashMap<String, String>) P2PCardGroup.P2PCardGroupFutureDateVoucher(serviceName, subService,initiateMap);
		cardGroupName=dataMap.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		String actual=dataMap.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage");

		Assertion.assertEquals(actual, expected);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	

	
	@Test(dataProvider="serviceDataUap")
	@TestManager(TestKey = "PRETUPS-336") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_15_Test_SetdefaultP2PCardGroupGroup(String serviceName, String subService) throws InterruptedException {

		//test = extent.createTest("P2P Card Group Creation: " +serviceName+" "+subService);
		final String methodName = "Test_SetdefaultP2PCardGroupGroup";
       Log.startTestCase(methodName);


		P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UVCNCARDGRP9").getExtentCase(), serviceName,subService));
		currentNode.assignCategory(assignCategory);
		
		boolean RoleExist = ExcelUtility.isRoleExists(RolesI.VOUCHER_CARD_GROUP_DEFAULT_ROLECODE);

		if (!RoleExist){
			currentNode.log(Status.SKIP, "Default Card Group Functionality is not available");
		}
		else{
		
		

		dataMap1=(HashMap<String, String>) P2PCardGroup.setDefaultP2PCardGroupVoucher(serviceName, subService);
		cardGroupName=dataMap1.get("CARDGROUPNAME");
		System.out.println(cardGroupName);
		
		String actual=dataMap1.get("ACTUALMESSAGE");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupp2pdetails.successdefaultmessage",cardGroupName);
		Assertion.assertEquals(actual, expected);
	}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test(dataProvider="serviceDataSIT")
	public void A_16_Test_P2PCardGroup_MandatoryDetailCheck_CardGroupSetName(String serviceName, String subService) throws InterruptedException{
			
		final String methodName = "Test_P2PCardGroup_MandatoryDetailCheck_CardGroupSetName";
	       Log.startTestCase(methodName);
		
	
		//Log.startTestCase("P2O Card Group Validation- Network Admin can not define P2P card group details if Card group set name is not entered.");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP1").getExtentCase());
		currentNode.assignCategory(assignCategory);


		String actual =p2pCardGroup.p2pCardGroupErrorValidation_BlankCardGroupSetNameVoucher(serviceName, subService,initiateMap);

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.voucherversionlist.label.cardgroupsetname.required.error"); 

		Validator.messageCompare(actual, expected);
		Log.endTestCase(methodName);
		
	}

	@Test(dataProvider="serviceDataSIT")
	public void A_17_Test_P2PCardGroup_MandatoryDetailCheck_SubService(String serviceName, String subService) throws InterruptedException{
			
		
		final String methodName = "Test_P2PCardGroup_MandatoryDetailCheck_SubService";
	       Log.startTestCase(methodName);
		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP2").getExtentCase());
		currentNode.assignCategory(assignCategory);


		String actual =p2pCardGroup.p2pCardGroupErrorValidation_BlankSubserviceVoucher(serviceName, subService,initiateMap);

		String expected= MessagesDAO.prepareMessageByKey("errors.required", 
				MessagesDAO.getLabelByKey("cardgroup.cardgroupdetailsview.label.subservice")); 

		Validator.messageCompare(actual, expected);
		Log.endTestCase(methodName);
	}
	
	

	
	@Test(dataProvider="serviceDataSIT")
	public void A_18_Test_P2PCardGroup_DeactivateCardGroup(String serviceName, String subService) throws InterruptedException{
				
		
		final String methodName = "Test_P2PCardGroup_DeactivateCardGroup";
	       Log.startTestCase(methodName);
		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP3").getExtentCase());
		currentNode.assignCategory(assignCategory);


		Map<String, String> map =p2pCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
		
		Thread.sleep(5000);
		
		String actual = map.get("ACTUALMESSAGE");
		String cardGroupName = map.get("CARDGROUPNAME");
		String cardGroupSetID = map.get("CARDGROUP_SETID");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage"); 

		Validator.messageCompare(actual, expected);
		
		String actual1 = p2pCardGroup.P2PCardGroupStatusDeativateVoucher(cardGroupName);
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.cardgrouplist.voucher.message.successsuspendmessage"); 

		Validator.messageCompare(actual1, expected1);
		Log.endTestCase(methodName);

	}
	
	
	
	
	
	
	
	@Test(dataProvider="serviceDataSIT")
	public void A_19_Test_P2PCardGroup_UniqueCardGroupName(String serviceName, String subService) throws InterruptedException{
		
		final String methodName = "Test_P2PCardGroup_UniqueCardGroupName";
	       Log.startTestCase(methodName);
		//Log.startTestCase("Card Group Set Name should be unique in System");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP4").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);
		int totalRow1 = ExcelUtility.getRowCount();

		int c=1;
		for( c=1; c<=totalRow1;c++)

		{			if((ExcelUtility.getCellData(0, ExcelI.NAME, c).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, c).matches(subService)))

			break;
		}

		
		
		String actual1 = p2pCardGroup.p2pCardGroupErrorValidation_UniqueCardGroupSetNameVoucher(serviceName,subService,ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, c),initiateMap);
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.error.cardgroupc2snamealreadyexist"); 

		Validator.messageCompare(actual1, expected1);
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider="serviceDataSIT")
	public void A_20_Test_P2PCardGroup_verifyVersion(String serviceName, String subService) throws InterruptedException{
		
		final String methodName = "Test_P2PCardGroup_verifyVersion";
	       Log.startTestCase(methodName);
		
		//Log.startTestCase("Newly created Card Group should have version 1 in System");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
       Map<String, String> map =p2pCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
		
		String actual = map.get("ACTUALMESSAGE");
		cardGroupName = map.get("CARDGROUPNAME");
		String cardGroupSetID = map.get("CARDGROUP_SETID");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage"); 

		Validator.messageCompare(actual, expected);
		
		String actual1 = DBHandler.AccessHandler.getCardGroupVersion(cardGroupName);
		
		

		Validator.messageCompare(actual1, "1");
		Log.endTestCase(methodName);
	}
	
	




	@Test(dataProvider="serviceDataSIT")
	public void A_21_Test_P2PCardGroup_DeleteNegativeCardGroupAssociatedWithTransferRule(String serviceName, String subService) throws InterruptedException{
			
		final String methodName = "Test_P2PCardGroup_DeleteNegativeCardGroupAssociatedWithTransferRule";
	       Log.startTestCase(methodName);
	//	Log.startTestCase("Network Admin cannot  delete P2P card group details if it is associated with transfer rule");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP6").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);
		int totalRow1 = ExcelUtility.getRowCount();

		int c=1;
		for( c=1; c<=totalRow1;c++)

		{			if((ExcelUtility.getCellData(0, ExcelI.NAME, c).matches(serviceName))&&(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, c).matches(subService)))

			break;
		}

		
		String actual1 = p2pCardGroup.P2PCardGroupDeletionVoucher(serviceName,subService,ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, c));
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.error.cardgroupc2snamealreadyexist"); 
		String expected2= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupp2pdetails.error.isdefault.nottodelete"); 
		if(actual1.equals(expected1))
			Assertion.assertEquals(actual1, expected1);
		else
			Assertion.assertSkip("Default Card Group can not be deleted");

		Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider="serviceDataSIT")
	public void A_22_Test_P2PCardGroup_ApplicableDateVerification(String serviceName, String subService) throws InterruptedException{
				
		final String methodName = "Test_P2PCardGroup_ApplicableDateVerification";
	       Log.startTestCase(methodName);
		//Log.startTestCase("To verify that card group can be set only for future date and time.");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP7").getExtentCase());
		currentNode.assignCategory(assignCategory);

				
		Map<String, String> map = p2pCardGroup.P2PCardGroupApplicableDateVerificationVoucher(serviceName,subService,initiateMap);
		
		
		String expected1= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetails.error.invalidapplicabledate"); 

		Validator.messageCompare(map.get("ACTUALMESSAGE"), expected1);

		Log.endTestCase(methodName);
	}
	
	
	/*
	@Test(dataProvider="serviceDataSIT")
	public void A_23_Test_P2PCardGroup_InvalidProcessingFeeMaxAmount(String serviceName, String subService) throws InterruptedException{
	
		final String methodName = "Test_P2PCardGroup_InvalidProcessingFeeMaxAmount";
	       Log.startTestCase(methodName);
		//Log.startTestCase("P2P Card Group Validation- To verify that Max Processing fee for the receiver should be within the start range and end range");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("ProcessingFeeMaxAmount", "101");
		Log.info("The entered ProcessingFeeMaxAmount is:" + Map_CardGroup.get("ProcessingFeeMaxAmount"));

		p2pCardGroup.p2pCardGroupSlabErrorValidationVoucher(Map_CardGroup, serviceName, subService,initiateMap);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidmaxsenderaccessfee","100");
 

	Validator.messageCompare(actual, expected);

	Log.endTestCase(methodName);

	}
	

	
	
	
	
	@Test(dataProvider="serviceDataSIT")
	public void A_24_Test_P2PCardGroup_FixValueCardGroup(String serviceName, String subService) throws InterruptedException{
			
		final String methodName = "Test_P2PCardGroup_FixValueCardGroup";
	       Log.startTestCase(methodName);
	//	Log.startTestCase("To verify that Max Processing fee for the receiver should be within the start range and end range");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
       Map<String, String> map =p2pCardGroup.FixValueP2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
		
		String actual = map.get("ACTUALMESSAGE");
		String cardGroupName = map.get("CARDGROUPNAME");
		String cardGroupSetID = map.get("CARDGROUP_SETID");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetailsview.successaddmessage"); 

		Validator.messageCompare(actual, expected);
		
		String actual1 = DBHandler.AccessHandler.getCardGroupVersion(cardGroupName);
		
		

		Validator.messageCompare(actual1, "1");
		Log.endTestCase(methodName);

	}
	

	
	@Test(dataProvider="serviceDataSIT")
	public void A_25_Test_P2PCardGroup_OverlappingRangeValidation(String serviceName, String subService) throws InterruptedException{
		
		final String methodName = "Test_P2PCardGroup_OverlappingRangeValidation";
	       Log.startTestCase(methodName);
		//Log.startTestCase("To verify that ranges defined in the card group should not overlap with other ranges defined in the same card group set.");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
       p2pCardGroup.p2pCardGroupOverLappingRangeValueValidationVoucher(Map_CardGroup,serviceName, subService,initiateMap);
		System.out.println("lllllllllllllllllllllllll" +Map_CardGroup.get("ACTUAL"));
   	String actual = Map_CardGroup.get("ACTUAL");
	


	
	String expected= MessagesDAO.prepareMessageByKey("cardgroup.cardgroupdetails.error.invalidslab",MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.startrange"),"2", 
			MessagesDAO.getLabelByKey("cardgroup.cardgroupdetails.label.endrange"),"1");

		Validator.messageCompare(actual, expected);
		Log.endTestCase(methodName);

	}
	
	
	
	
	
	
	@Test(dataProvider="serviceDataSIT")
	public void A_26_Test_P2PCardGroup_Tax1RateValidation(String serviceName, String subService) throws InterruptedException{
		
		final String methodName = "Test_P2PCardGroup_Tax1RateValidation";
	       Log.startTestCase(methodName);
		//Log.startTestCase("To verify that the Tax 1 Rate  should be within 0 to 100 (if in percent)");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax1Rate", "101");
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax1Rate"));

		p2pCardGroup.p2pCardGroupSlabErrorValidationVoucher(Map_CardGroup, serviceName, subService,initiateMap);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidsendertax1rate","100"); 

		Validator.messageCompare(actual, expected);
		Log.endTestCase(methodName);
	}

	
	
	@Test(dataProvider="serviceDataSIT")
	public void A_27_Test_P2PCardGroup_Tax2RateValidation(String serviceName, String subService) throws InterruptedException{
		
		final String methodName = "Test_P2PCardGroup_Tax2RateValidation";
	       Log.startTestCase(methodName);
		//Log.startTestCase("To verify that the Tax 2 Rate  should be within 0 to 100 (if in percent)");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP12").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax2Rate", "101");
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax2Rate"));

		p2pCardGroup.p2pCardGroupSlabErrorValidationVoucher(Map_CardGroup, serviceName, subService,initiateMap);

		String actual= Map_CardGroup.get("ACTUAL");

		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidsendertax2rate","100"); 

		Validator.messageCompare(actual, expected);
		Log.endTestCase(methodName);
	}
	
	

	
	
	@Test(dataProvider="serviceDataSIT")
	public void A_28_Test_P2PCardGroup_Tax1RateinAmountValidation(String serviceName, String subService) throws InterruptedException{
		
		final String methodName = "Test_P2PCardGroup_Tax1RateinAmountValidation";
	       Log.startTestCase(methodName);
		//Log.startTestCase("To verify that the Tax 1 Rate  should be within 0 to Start Range");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax1Type", (_masterVO.getProperty("Tax1TypeAmt")));
		Map_CardGroup.put("Tax1Rate", String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"))+1));
		Log.info("The entered Tax1Rate is:" + Map_CardGroup.get("Tax2Rate"));

		p2pCardGroup.p2pCardGroupSlabErrorValidationVoucher(Map_CardGroup, serviceName, subService,initiateMap);

		String actual= Map_CardGroup.get("ACTUAL");
		
		String startRange = String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"))+ 1.0);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidsendertax1rate",startRange+" (Start Range)"); 

		Validator.messageCompare(actual, expected);
		Log.endTestCase(methodName);
	}

	
	
	
	
	
	@Test(dataProvider="serviceDataSIT")
	public void A_29_Test_P2PCardGroup_Tax2RateinAmountValidation(String serviceName, String subService) throws InterruptedException{
		
		final String methodName = "Test_P2PCardGroup_Tax2RateinAmountValidation";
	       Log.startTestCase(methodName);
		//Log.startTestCase("To verify that the Tax 2 Rate  should be within 0 to Start Range");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		Map_CardGroup dataMap = new Map_CardGroup();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Map_CardGroup=dataMap.DataMap_CardGroup();
		Map_CardGroup.put("Tax2Type", (_masterVO.getProperty("Tax1TypeAmt")));
		Map_CardGroup.put("Tax2Rate", String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab2"))+1));
		Log.info("The entered Tax2Rate is:" + Map_CardGroup.get("Tax2Rate"));

		p2pCardGroup.p2pCardGroupSlabErrorValidationVoucher(Map_CardGroup, serviceName, subService,initiateMap);

		String actual= Map_CardGroup.get("ACTUAL");
		
		String startRange = String.valueOf(Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"))+ 1.0);
		String expected= MessagesDAO.prepareMessageByKey("cardgroup.addcardgroup.error.invalidsendertax2rate",startRange+" (Start Range)"); 

		Validator.messageCompare(actual, expected);
		Log.endTestCase(methodName);
	}*/
	
		@Test(dataProvider="serviceDataSIT")
      public void A_30_Test_P2PCardGroup_MultipleVersionsValidation(String serviceName, String subService) throws InterruptedException{
		
		final String methodName = "Test_P2PCardGroup_MultipleVersionsValidation";
	       Log.startTestCase(methodName);
		//Log.startTestCase("To verify that P2P card group set will have only one version applicable even if it has multiple versions");

		P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITVCNCARDGROUP15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Thread.sleep(30000);

		String actualMessage = p2pCardGroup.P2PCardGroupModification_EditCardGroupVoucher(serviceName, subService, cardGroupName);

		String expectedmessage= _masterVO.getMessage("cardgroup.cardgroupc2sdetailsview.successeditmessage");

		Thread.sleep(30000);
		
		Validator.messageCompare(actualMessage, expectedmessage);

		String v = DBHandler.AccessHandler.getCardGroupVersionActive(cardGroupName);


		Validator.messageCompare(v, "2");
		Log.endTestCase(methodName);

	}
      
   // To vaildate the error message when No Transfer rule associated with the Voucher Card Group created 
  	@Test(dataProvider="serviceDataVoucher")
  	public void A_31_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative(String serviceName, String subService) throws InterruptedException{
  		
  		final String methodName = "A_31_Test_VoucherCardGroup_CalculateVoucherCardgroup";
  		 Log.startTestCase(methodName);
  	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVCNCARDGROUP16").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
  	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
  	        VMS vms = new VMS(driver);
  	        initialiseVMSMap();
  			initiateMap.put("categoryName","SUADM");
  			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
  			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
  			String mrp =  UniqueChecker.UC_VOMS_MRP();
  			initiateMap.put("mrp", mrp);
  			String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
  			initiateMap.put("activeProfile", activeProfile);
  			String denomination = mrp + ".0";
  			initiateMap.put("denomination", denomination);
  			String productID = activeProfile+"("+denomination+")";
  			initiateMap.put("productID", productID);
  			initiateMap.put("scenario", "");
  			initiateMap.put("categoryName","SUADM");
  			initiateMap.put("payableAmount", String.valueOf(10));
  			initiateMap=vms.voucherDenominationNegative(initiateMap,"");
  			initiateMap=vms.addVoucherProfileNegative(initiateMap);
  			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
			if(BTSLUtil.isNullString(value)) {
			initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
			}
			else {
				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
				List al = Arrays.asList(type);
				if(al.contains(initiateMap.get("type"))) {
					initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
				}
			}
  			initiateMap.put("gateWayCode","EXTGW" );
  	        // Test Case - To create Voucher Card Group through Network Admin as per the DataProvider
  	        HashMap<String, String> mapInfo = (HashMap<String, String>) p2pCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
  	        HashMap<String, String> mapInfo1=(HashMap<String, String>) p2pCardGroup.calculateVoucherCardGroup(serviceName, subService,initiateMap);
  	        String expected=MessagesDAO.prepareMessageByKey("2071", mrp);
  	    	Validator.messageCompare(expected, mapInfo1.get("MESSAGE"));
  	        Log.endTestCase(methodName);
  	}
	
      // To calculate Voucher Cardgroup Positive scenario
    	@Test(dataProvider="serviceDataVoucher")
    	public void A_32_Test_VoucherCardGroup_CalculateVoucherCardgroupPositive(String serviceName, String subService) throws InterruptedException{
    		
    		final String methodName = "A_32_Test_VoucherCardGroup_CalculateVoucherCardgroup";
    		 Log.startTestCase(methodName);
    	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVCNCARDGROUP17").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
    	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
    	        VMS vms = new VMS(driver);
    	        initialiseVMSMap();
    			initiateMap.put("categoryName","SUADM");
    			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
    			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
    			String mrp =  UniqueChecker.UC_VOMS_MRP();
    			initiateMap.put("mrp", mrp);
    			String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
    			initiateMap.put("activeProfile", activeProfile);
    			String denomination = mrp + ".0";
    			initiateMap.put("denomination", denomination);
    			String productID = activeProfile+"("+denomination+")";
    			initiateMap.put("productID", productID);
    			initiateMap.put("scenario", "");
    			initiateMap.put("categoryName","SUADM");
    			initiateMap.put("payableAmount", String.valueOf(10));
    			initiateMap=vms.voucherDenominationNegative(initiateMap,"");
    			initiateMap=vms.addVoucherProfileNegative(initiateMap);
    			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
    			if(BTSLUtil.isNullString(value)) {
    			initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
    			}
    			else {
    				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
    				List al = Arrays.asList(type);
    				if(al.contains(initiateMap.get("type"))) {
    					initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
    				}
    			}
    			initiateMap.put("gateWayCode","ALL");
    			P2PTransferRules p2pTransferRules=new P2PTransferRules(driver);
      	        createVoucherCardGroup(initiateMap,serviceName,subService);
    	        HashMap<String, String> mapInfo1=(HashMap<String, String>) p2pCardGroup.calculateVoucherCardGroup(serviceName, subService,initiateMap);
    	       Assertion.assertEquals(mapInfo1.get("ACTUALMESSAGE"), "Y");
    	        Log.endTestCase(methodName);
    	}
    	// Calculate Voucher Card group when No cardgroup is defined with the associated Profile 
    	@Test(dataProvider="serviceDataVoucher")
    	public void A_33_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative(String serviceName, String subService) throws InterruptedException{
    		
    		final String methodName = "A_33_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative";
    		 Log.startTestCase(methodName);
    	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVCNCARDGROUP18").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
    	        int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
    	          			if( HCPT_VMS == 1 ) {
    	           				Assertion.assertSkip("Skipped for IN network");
    	         			}else {
    	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
    	        VMS vms = new VMS(driver);
    	        initialiseVMSMap();
    			initiateMap.put("categoryName","SUADM");
    			initiateMap.put("denominationName", UniqueChecker.UC_VOMS_DENOM_NAME());
    			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
    			String mrp =  UniqueChecker.UC_VOMS_MRP();
    			initiateMap.put("mrp", mrp);
    			String activeProfile = UniqueChecker.UC_VOMS_ProfileName();
    			initiateMap.put("activeProfile", activeProfile);
    			String denomination = mrp + ".0";
    			initiateMap.put("denomination", denomination);
    			String productID = activeProfile+"("+denomination+")";
    			initiateMap.put("productID", productID);
    			initiateMap.put("scenario", "");
    			initiateMap.put("categoryName","SUADM");
    			initiateMap.put("payableAmount", String.valueOf(10));
    			initiateMap=vms.voucherDenominationNegative(initiateMap,"");
    			initiateMap=vms.addVoucherProfileNegative(initiateMap);
    			String value = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
    			if(BTSLUtil.isNullString(value)) {
    			initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
    			}
    			else {
    				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
    				List al = Arrays.asList(type);
    				if(al.contains(initiateMap.get("type"))) {
    					initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
    				}
    			}
    			
    			
    			//initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
    			P2PTransferRules p2pTransferRules=new P2PTransferRules(driver);
    			createVoucherCardGroup(initiateMap,serviceName,subService);
    			// creating new profile with same denomination
    			String activeProfile1 = UniqueChecker.UC_VOMS_ProfileName();
    			initiateMap.put("activeProfile", activeProfile1);
    			String denomination1 = mrp + ".0";
    			initiateMap.put("denomination", denomination1);
    			String productID1 = activeProfile1+"("+denomination1+")";
    			initiateMap.put("productID", productID1);
    			initiateMap.put("shortName", UniqueChecker.UC_VOMS_SHORTNAME());
    			initiateMap.put("gateWayCode","ALL");
    			initiateMap=vms.addVoucherProfileNegative(initiateMap);
    			if(BTSLUtil.isNullString(value)) {
    			initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
    			}
    			else {
    				String type[] =getAllowedVoucherTypesForScreen(PretupsI.ACTIVE_PROF);
    				List al = Arrays.asList(type);
    				if(al.contains(initiateMap.get("type"))) {
    					initiateMap=vms.addActiveVoucherProfileNegative(initiateMap);
    				}
    			}
    			// Calculate Voucher Card Group For new created product
    	        HashMap<String, String> mapInfo1=(HashMap<String, String>) p2pCardGroup.calculateVoucherCardGroup(serviceName, subService,initiateMap);
    	        String expected= MessagesDAO.prepareMessageByKey("2071", mrp);
    	        Validator.messageCompare(expected, mapInfo1.get("MESSAGE"));
    	         			}
    	        Log.endTestCase(methodName);
    	}
	
    	// Calculate Voucher Card group - To check Validation for Voucher Type is not selected 
    	@Test(dataProvider="serviceDataVoucher")
    	public void A_34_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative(String serviceName, String subService) throws InterruptedException{
    		
    		final String methodName = "A_34_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative";
    		 Log.startTestCase(methodName);
    	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVCNCARDGROUP19").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
    	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
    	        initialiseVMSMap();
    	        initiateMap.put("voucherType","");
    	        initiateMap.put("segmentType","");
    			initiateMap.put("denomination","");
    			initiateMap.put("activeProfile", "");
    			// Calculate Voucher Card Group 
    	        HashMap<String, String> mapInfo1=(HashMap<String, String>) p2pCardGroup.calculateVoucherCardGroup(serviceName, subService,initiateMap);
    	        String expected= MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("cardgroup.viewtransferrule.label.vouchertype"));
    	        Validator.messageCompare(expected, mapInfo1.get("MESSAGE"));
    	        Log.endTestCase(methodName);
    	}
    	// Calculate Voucher Card group - To check Validation for Segment Type is not selected 
    	@Test(dataProvider="serviceDataVoucher")
    	public void A_35_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative(String serviceName, String subService) throws InterruptedException{
    		
    		final String methodName = "A_35_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative";
    		 Log.startTestCase(methodName);
    	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVCNCARDGROUP20").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
    	        int HCPT_VMS = Integer.parseInt(_masterVO.getClientDetail("HCPT_VMS"));
    	           			if( HCPT_VMS == 1 ) {
    	            				Assertion.assertSkip("skipped for IN network");
    	        		}else {
    	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
    	        initialiseVMSMap();
    	        initiateMap.put("segment","");
    	        initiateMap.put("denomination","");
    			initiateMap.put("activeProfile", "");
    			// Calculate Voucher Card Group 
    	        HashMap<String, String> mapInfo1=(HashMap<String, String>) p2pCardGroup.calculateVoucherCardGroup(serviceName, subService,initiateMap);
    	        String expected= MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("cardgroup.viewtransferrule.label.vouchersegment"));

    	        Validator.messageCompare(expected, mapInfo1.get("MESSAGE"));
    	        		}
    	        Log.endTestCase(methodName);
    	}
    	// Calculate Voucher Card group - To check Validation for Voucher Denomination is not selected 
    	@Test(dataProvider="serviceDataVoucher")
    	public void A_36_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative(String serviceName, String subService) throws InterruptedException{
    		
    		final String methodName = "A_36_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative";
    		 Log.startTestCase(methodName);
    	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVCNCARDGROUP21").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
    	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
    	        initialiseVMSMap();
    	        initiateMap.put("denomination","");
    			initiateMap.put("activeProfile", "");
    			// Calculate Voucher Card Group 
    	        HashMap<String, String> mapInfo1=(HashMap<String, String>) p2pCardGroup.calculateVoucherCardGroup(serviceName, subService,initiateMap);
    	        String expected= MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("cardgroup.viewtransferrule.label.denomination"));

    	        Validator.messageCompare(expected, mapInfo1.get("MESSAGE"));
    	        Log.endTestCase(methodName);
    	}
    	// To validate error message when SubService type is not selected 
    	@Test(dataProvider="serviceDataVoucher")
    	public void A_37_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative(String serviceName, String subService) throws InterruptedException{
    		
    		final String methodName = "A_37_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative";
    		 Log.startTestCase(methodName);
    	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVCNCARDGROUP22").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
    	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
    	        initialiseVMSMap();
    	        initiateMap.put("voucherType","");
    	        initiateMap.put("segmentType","");
    			initiateMap.put("denomination","");
    			initiateMap.put("activeProfile", "");
    			initiateMap.put("GatewayCode","ALL");
    			initiateMap.put("ReceiverType", "ALL");
    			initiateMap.put("ReceiverClass", "ALL");
    			// Calculate Voucher Card Group 
    			subService="";
    	        HashMap<String, String> mapInfo1=(HashMap<String, String>) p2pCardGroup.calculateVoucherCardGroupValidation(serviceName, subService,initiateMap);
    	        String expected= MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("cardgroup.c2scardgrouplist.label.subservice"));

    	        Validator.messageCompare(expected, mapInfo1.get("ACTUALMESSAGE"));
    	        Log.endTestCase(methodName);
    	}
    	// To validate error message when gatewaycode is not selected 
    	@Test(dataProvider="serviceDataVoucher")
    	public void A_38_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative(String serviceName, String subService) throws InterruptedException{
    		
    		final String methodName = "A_38_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative";
    		 Log.startTestCase(methodName);
    	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVCNCARDGROUP23").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
    	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
    	        initialiseVMSMap();
    	        initiateMap.put("voucherType","");
    	        initiateMap.put("segmentType","");
    			initiateMap.put("denomination","");
    			initiateMap.put("activeProfile", "");
    			// Calculate Voucher Card Group 
    			initiateMap.put("GatewayCode", "");
    			initiateMap.put("ReceiverType", "ALL");
    			initiateMap.put("ReceiverClass", "ALL");
    	        HashMap<String, String> mapInfo1=(HashMap<String, String>) p2pCardGroup.calculateVoucherCardGroupValidation(serviceName, subService,initiateMap);
    	        String expected= MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("cardgroup.cardgroupp2pdetails.label.gatewaycode"));

    	        Validator.messageCompare(expected, mapInfo1.get("ACTUALMESSAGE"));
    	        Log.endTestCase(methodName);
    	}
    	// To validate error message when ReceiverType is not selected 
    	@Test(dataProvider="serviceDataVoucher")
    	public void A_39_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative(String serviceName, String subService) throws InterruptedException{
    		
    		final String methodName = "A_39_Test_VoucherCardGroup_CalculateVoucherCardgroupNegative";
    		 Log.startTestCase(methodName);
    	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITVCNCARDGROUP24").getExtentCase(), serviceName, subService)).assignCategory(TestCategory.PREREQUISITE);
    	        P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
    	        initialiseVMSMap();
    	        initiateMap.put("voucherType","");
    	        initiateMap.put("segmentType","");
    			initiateMap.put("denomination","");
    			initiateMap.put("activeProfile", "");
    			// Calculate Voucher Card Group 
    			initiateMap.put("GatewayCode", "ALL");
    			initiateMap.put("ReceiverType", "");
    			initiateMap.put("ReceiverClass", "");
    	        HashMap<String, String> mapInfo1=(HashMap<String, String>) p2pCardGroup.calculateVoucherCardGroupValidation(serviceName, subService,initiateMap);
    	        String expected= MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("trfrule.modifyc2stransferrules.label.reveivertype"));

    	        Validator.messageCompare(expected, mapInfo1.get("ACTUALMESSAGE"));
    	        Log.endTestCase(methodName);
    	}
    	

    	public void createVoucherCardGroup(HashMap<String, String> initiateMap,String serviceName, String subService) throws InterruptedException{
    		//String serviceName=ExtentI.getValueofCorrespondingColumns(ExcelI.P2P_SERVICES_SHEET_VOUCHER, ExcelI.NAME, new String[]{ExcelI.SERVICE_TYPE}, new String[]{initiateMap.get("service")});
    		//String subService =initiateMap.get("subService");
            P2PCardGroup p2pCardGroup = new P2PCardGroup(driver);
            HashMap<String, String> mapInfo = (HashMap<String, String>) p2pCardGroup.P2PCardGroupCreationVoucher(serviceName, subService,initiateMap);
            boolean uap = true;
     		P2PTransferRules p2pTransferRules = new P2PTransferRules(driver);
     		String result[] = p2pTransferRules.addP2PTransferRules(serviceName, subService, mapInfo.get("CARDGROUPNAME"), uap, "ALL");
     		/*currentNode = test.createNode(_masterVO.getCaseMasterByID("UP2PTRFRULE2").getExtentCase());
     		currentNode.assignCategory(assignCategory);*/
     	 	String addP2PTransferRuleSuccessMsg = MessagesDAO.prepareMessageByKey("trfrule.addtrfrule.msg.success");
     		String p2pTransferRuleAlreadyExistsMsg = MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist","1");
     		if (p2pTransferRuleAlreadyExistsMsg.equals(result[0])) {
     			String result2[]=p2pTransferRules.modifyP2PTransferRulesVoucher("ALL",serviceName, subService,PretupsI.STATUS_ACTIVE_LOOKUPS, mapInfo.get("CARDGROUPNAME"), mapInfo.get("datetime"));
     			String modifyP2PTransferRuleSuccessMsg = MessagesDAO.prepareMessageByKey("trfrule.modtrfrule.msg.success");
     			Assertion.assertEquals(result2[0], modifyP2PTransferRuleSuccessMsg);
     		}
     		else {
     			Assertion.assertEquals(addP2PTransferRuleSuccessMsg, result[0]);
     		}
     		
     		Assertion.completeAssertions();
     		//Thread.sleep(120000);
    	}
	
	
    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

	
	
    @DataProvider(name = "serviceData")
    public Object[][] TestDataFeed() {
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);
        int rowCount = ExcelUtility.getRowCount();
        Object[][] categoryData = null;
        if (rowCount > 0) {
            categoryData = new Object[rowCount][3];
            for (int i = 1, j = 0; i <= rowCount; i++, j++) {
                categoryData[j][0] = i;
                categoryData[j][1] = ExcelUtility.getCellData(i, 1);
                categoryData[j][2] = ExcelUtility.getCellData(i, 2);
            }
        } else if (rowCount <= 0) {
            categoryData = new Object[][]{
                    {0, null, null}
            };
        }
        return categoryData;
    }

    @DataProvider(name = "serviceDataVoucher")
    public Object[][] TestDataFeedvoucher() {
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);
        int rowCount = ExcelUtility.getRowCount();
        Object[][] categoryData = null;
        if (rowCount > 0) {
            categoryData = new Object[1][2];
            for (int i = 1; i <= rowCount; i++) {
                String x = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i);
                if (x.equals(_masterVO.getProperty("VoucherConsumptionCode"))) {
                    categoryData[0][0] = ExcelUtility.getCellData(i, 1);
                    categoryData[0][1] = ExcelUtility.getCellData(i, 2);

                    break;
                }

            }

        } else if (rowCount <= 0) {
            categoryData = new Object[][]{
                    {null, null}
            };
        }


        return categoryData;

    }
    
    @DataProvider(name = "serviceDataUap")
	public Object[][] TestDataFeedVoucherUAP() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);

		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[1][2];
		for (int i = 1; i <= rowCount; i++) {

			String x = ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i);
			System.out.println("Service type is " +x);

			if (x.equals(_masterVO.getProperty("VoucherConsumptionCode"))){
				System.out.println(x.equals(_masterVO.getProperty("VoucherConsumptionCode")));


				
				categoryData[0][0] = ExcelUtility.getCellData(i, 1);
				System.out.println(categoryData[0][0]);
				categoryData[0][1] = ExcelUtility.getCellData(i, 2);
				System.out.println(categoryData[0][1]);

				break;

			}

		}
		return categoryData;
	}
    
    @DataProvider(name = "serviceDataSIT")
	public Object[][] TestDataFeedVoucherSIT() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);

		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[1][2];
		for (int i = 1; i <= rowCount; i++) {

			String x = ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i);
			System.out.println("Service type is " +x);

			if (x.equals(_masterVO.getProperty("VoucherConsumptionCode"))){
				System.out.println(x.equals(_masterVO.getProperty("VoucherConsumptionCode")));

				//categoryData[j][0] = i;
				categoryData[0][0] = ExcelUtility.getCellData(i, 1);
				System.out.println(categoryData[0][0]);
				categoryData[0][1] = ExcelUtility.getCellData(i, 2);
				System.out.println(categoryData[0][1]);

				break;
			}
		}
		return categoryData;
	}
    
    @DataProvider(name="VOMSDENOMINATIONS")
	public Object[][] VOMSDenominationDP() {
		
		int VOMS_DATA_COUNT = Integer.parseInt(_masterVO.getProperty("vms.voms.profiles.count"));
		Object[][] VOMSData = DBHandler.AccessHandler.getVOMSDetails();
		
		int objCounter = 0;
		ArrayList<String> categoryList =UserAccess.getCategoryWithAccess(RolesI.ADD_VOUCHER_DENOMINATION);
		
		if(categoryList.contains("SSADM"))
		{
			categoryList.remove(categoryList.indexOf("SSADM"));
		}
		
		if(categoryList.contains("SUNADM"))
		{
			categoryList.remove(categoryList.indexOf("SUNADM"));
		}
		
		int categorySize=categoryList.size();
		Object[][] dataObj = new Object[VOMS_DATA_COUNT * VOMSData.length*categorySize][2];
		
		for (int i = 0; i < VOMS_DATA_COUNT; i++) {
			for (int j = 0; j<VOMSData.length; j++) {
				for(int k=0;k<categorySize;k++) {
					
			HashMap<String, String> VomsData = new HashMap<String, String>();
			VomsData.put("voucherType", String.valueOf(VOMSData[j][0]));
			VomsData.put("type", String.valueOf(VOMSData[j][1]));
			VomsData.put("service", String.valueOf(VOMSData[j][2]));
			VomsData.put("subService", String.valueOf(VOMSData[j][3]));
			VomsData.put("categoryName",categoryList.get(k));
			VomsData.put("payableAmount", String.valueOf(10));
			VomsData.put("description", "Automation Testing");
			VomsData.put("minQuantity", "1");
			VomsData.put("maxQuantity", "60");
			VomsData.put("talkTime", "5");
			VomsData.put("validity", "80");
			VomsData.put("threshold", "10");
			VomsData.put("quantity", "10");
			VomsData.put("expiryPeriod", "90");
			dataObj[objCounter][0] = VomsData.clone();
			dataObj[objCounter][1] = ++objCounter;
			}
			}
		}
		
		BuilderLogic VOMSDenomSheetBuilder = new BuilderLogic();
		VOMSDenomSheetBuilder.prepareVOMSProfileSheet(dataObj);
		
		return dataObj;
	}
    
    /* ------------------------------------------------------------------------------------------------- */
	
}
