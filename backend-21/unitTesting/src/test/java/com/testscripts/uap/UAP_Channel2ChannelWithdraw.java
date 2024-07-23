/**
 * 
 */
package com.testscripts.uap;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CWithdraw;
import com.Features.ResumeChannelUser;
import com.Features.SuspendChannelUser;
import com.aventstack.extentreports.Status;
import com.businesscontrollers.BusinessValidator;
import com.businesscontrollers.TransactionVO;
import com.businesscontrollers.businessController;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.commons.PretupsI;

/**
 * @author lokesh.kontey
 *
 */
@ModuleManager(name =Module.UAP_C2C_WITHDRAW)
public class UAP_Channel2ChannelWithdraw extends BaseTest{
	
HashMap<String, String> c2cWithdrawMap=new HashMap<String, String>();
static boolean TestCaseCounter = false;	
	
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-400") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void C2CWithdraw1(String ToCategory, String FromCategory, String fromMSISDN, String toPIN) throws InterruptedException, ParseException, SQLException {
		C2CWithdraw c2cWithdraw= new C2CWithdraw(driver);
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		
		final String methodName = "Test_C2CWithdraw1";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SC2CWITHDRAW1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("SC2CWITHDRAW2");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("SC2CWITHDRAW3");
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("SC2CWITHDRAW4");
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("UC2CWITHDRAW1");
		
		//Test Case : C2C Withdraw
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),FromCategory,ToCategory));
		currentNode.assignCategory("UAP");
		Login login = new Login();
		HashMap<String, String> fromUserloginDetails = login.getUserLoginDetails("ChannelUser", ToCategory);
		businessController businessController = new businessController(_masterVO.getProperty("C2CWithdrawCode"), fromMSISDN, fromUserloginDetails.get("MSISDN").toString());
		TransactionVO TransactionVO = businessController.preparePreTransactionVO();
		TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
		if(CommonUtils.roleCodeExistInLinkSheet(RolesI.C2CWDL_ROLECODE, ToCategory))
		{c2cWithdrawMap=c2cWithdraw.channel2channelWithdraw(ToCategory, FromCategory, fromMSISDN, toPIN);
		HashMap<String, String> initiatedQuantities = toHashMap(c2cWithdrawMap.get("InitiatedQuantities"));
		//Test Case : Message validation
		currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(),FromCategory,ToCategory));
		currentNode.assignCategory("UAP");
		Assertion.assertEquals(c2cWithdrawMap.get("actualMessage"), c2cWithdrawMap.get("expectedMessage"));
		
		/*
		 * Test Case to validate Network Stocks after successful O2C Transfer
		 */
		currentNode = test.createNode(CaseMaster3.getExtentCase());
		currentNode.assignCategory("UAP");
		TransactionVO = businessController.preparePostTransactionVO(TransactionVO, initiatedQuantities);
		BusinessValidator.validateStocks(TransactionVO);
		
		/*
		 * Test Case to validate Channel User balance after successful O2C Transfer
		 */
		currentNode = test.createNode(CaseMaster4.getExtentCase());
		currentNode.assignCategory("UAP");
		BusinessValidator.validateUserBalances(TransactionVO);
		
		
		//Test Case: If Sender is suspended
		currentNode=test.createNode(CaseMaster5.getExtentCase());
		currentNode.assignCategory("UAP");
		suspendCHNLUser.suspendChannelUser_MSISDN(fromMSISDN, "Automation Remarks");
		suspendCHNLUser.approveCSuspendRequest_MSISDN(fromMSISDN, "Automation remarks");
		try{
			c2cWithdraw.channel2channelWithdraw(ToCategory, FromCategory, fromMSISDN, toPIN);
			Assertion.assertFail("C2C Withdraw is successful.");
			currentNode.log(Status.FAIL, "C2C Withdraw is successful.");}
		catch(Exception e){
			String actualMessage = driver.findElement(By.xpath("//ul/li")).getText();
			String expectedMessage = MessagesDAO.prepareMessageByKey("message.channeltransfer.usersuspended.msg", fromMSISDN);
			Log.info(" Message fetched from WEB as : "+actualMessage);
			Assertion.assertEquals(actualMessage, expectedMessage);
			}
		resumeCHNLUser.resumeChannelUser_MSISDN(fromMSISDN, "Auto Resume Remarks");
		}
		else{
			Assertion.assertSkip("C2C Withdraw link is not available to category["+ToCategory+"].");
			currentNode.log(Status.SKIP, "C2C Withdraw link is not available to category["+ToCategory+"].");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	private HashMap<String, String> toHashMap(String _initiatedQuantities) {
		HashMap<String, String> initiatedQuantities = new HashMap<String, String>();
		String[] Products = _initiatedQuantities.split("\\|");
			for (int i = 0; i < Products.length; i++) {
				String[] ProductQuantity = Products[i].split(":");
				initiatedQuantities.put(ProductQuantity[0], ProductQuantity[1]);
			}
		
		return initiatedQuantities;
	}
	
	@DataProvider(name = "categoryData")
    public Object[][] TestDataFeed1() {
          String C2CWithdrawCode = _masterVO.getProperty("C2CWithdrawCode");
          String MasterSheetPath = _masterVO.getProperty("DataProvider");

          ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
          int rowCount = ExcelUtility.getRowCount();
/*
* Array list to store Categories for which C2C withdraw is allowed
*/
          ArrayList<String> alist1 = new ArrayList<String>();
          ArrayList<String> alist2 = new ArrayList<String>();
          for (int i = 1; i <= rowCount; i++) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
                ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
                if (aList.contains(C2CWithdrawCode)) {
                      ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
                      alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
                      alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
                }
          }
                            
/*
* Counter to count number of users exists in channel users hierarchy sheet 
* of Categories for which C2C Withdraw is allowed
*/
          ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
          int chnlCount = ExcelUtility.getRowCount();
          
          Object[][] Data = new Object[alist1.size()][4];
          
          for(int j=0;j<alist1.size();j++){
                Data[j][0] = alist2.get(j);
                Data[j][1] = alist1.get(j);
                
                for(int i=1;i<=chnlCount;i++){
                      if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(Data[j][1])){
                            Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i); 
                            break;}
                      }
                
                for(int i=1;i<=chnlCount;i++){
                      if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(Data[j][0])){
                            Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                            break;}
                }
          }                       
                return Data;
    }
	
}