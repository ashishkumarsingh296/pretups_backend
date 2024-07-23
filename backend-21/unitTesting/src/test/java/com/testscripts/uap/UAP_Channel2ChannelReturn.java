package com.testscripts.uap;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CReturn;
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
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.commons.PretupsI;
@ModuleManager(name =Module.UAP_C2C_RETURN)
public class UAP_Channel2ChannelReturn extends BaseTest {

	HashMap<String, String> c2cMap=new HashMap<String, String>();
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-401") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void C2CReturn(String FromCategory, String ToCategory, String toMSISDN, String FromPIN) throws InterruptedException, IOException, ParseException, SQLException {
		C2CReturn c2cReturn= new C2CReturn(driver);
		SuspendChannelUser suspendCHNLUser = new SuspendChannelUser(driver);
		ResumeChannelUser resumeCHNLUser = new ResumeChannelUser(driver);
		final String methodName = "Test_C2CReturn";
        Log.startTestCase(methodName);
        
			
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("UCHNL2CHNLRETURN1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("UCHNL2CHNLRETURN2");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("UCHNL2CHNLRETURN3");
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("UCHNL2CHNLRETURN4");
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("UCHNL2CHNLRETURN5");
		
		
		/*
		 * Test Case Number 1: To initiate C2C Transfer
		 */
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), FromCategory,ToCategory));
		currentNode.assignCategory("UAP");
		if(CommonUtils.roleCodeExistInLinkSheet(RolesI.C2CRETURN, FromCategory)) {
			Login login = new Login();
			HashMap<String, String> fromUserloginDetails = login.getUserLoginDetails("ChannelUser", ToCategory,FromCategory);
			businessController businessController = new businessController(_masterVO.getProperty("C2CReturnCode"), fromUserloginDetails.get("MSISDN").toString(), toMSISDN);
			TransactionVO TransactionVO = businessController.preparePreTransactionVO();
			TransactionVO.setGatewayType(PretupsI.GATEWAY_TYPE_WEB);
			c2cMap=c2cReturn.channel2channelReturn(FromCategory, ToCategory, toMSISDN, FromPIN);
			HashMap<String, String> initiatedQuantities = toHashMap(c2cMap.get("InitiatedQuantities"));
	
			/*
			 * Test Case Number 2: Message Validation
			 */
			currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), FromCategory,ToCategory));
			currentNode.assignCategory("UAP");
			Assertion.assertEquals(c2cMap.get("actualMessage"), c2cMap.get("expectedMessage"));
			
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
			
			/*
			 * Test Case: If receiver user is suspended
			 */
			currentNode=test.createNode(CaseMaster5.getExtentCase());
			currentNode.assignCategory("UAP");
			suspendCHNLUser.suspendChannelUser_MSISDN(toMSISDN, "Automation Remarks");
			suspendCHNLUser.approveCSuspendRequest_MSISDN(toMSISDN, "Automation remarks");
			try{
				c2cReturn.channel2channelReturn(FromCategory, ToCategory, toMSISDN, FromPIN);
				Assertion.assertFail("C2C Transfer is successful.");
				currentNode.log(Status.FAIL, "C2C Transfer is successful.");}
			catch(Exception e){
				//String actualMessage = driver.findElement(By.xpath("//ul/li")).getText();
				String actualMessage = new AddChannelUserDetailsPage(driver).getActualMessage();
				Log.info(" Message fetched from WEB as : "+actualMessage);
				if(actualMessage==null){actualMessage="";}
				String expectedMessage = MessagesDAO.prepareMessageByKey("message.channeltransfer.usersuspended.msg", toMSISDN);
				
				int row = ExtentI.combinationExistAtRow(new String[]{ExcelI.MSISDN}, new String[]{toMSISDN}, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
				String name = ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.USER_NAME, row);
				String expectedMessage1 = MessagesDAO.prepareMessageByKey("message.channeltransfer.usersuspended.msg", name);
				
				if(!actualMessage.equals(expectedMessage)){
					Assertion.assertEquals(actualMessage, expectedMessage1);
				}
				else {Assertion.assertEquals(actualMessage, expectedMessage);
				}
				}
			resumeCHNLUser.resumeChannelUser_MSISDN(toMSISDN, "Auto Resume Remarks");
		} else {
			Assertion.assertSkip("C2C Return link is not allowed to category["+FromCategory+"].");
			currentNode.log(Status.SKIP, "C2C Return link is not allowed to category["+FromCategory+"].");
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
          String C2CReturnCode = _masterVO.getProperty("C2CReturnCode");
          String MasterSheetPath = _masterVO.getProperty("DataProvider");

          ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
          int rowCount = ExcelUtility.getRowCount();
/*
* Array list to store Categories for which C2C withdraw is allowed
*/
          ArrayList<String> alist1 = new ArrayList<String>();
          ArrayList<String> alist2 = new ArrayList<String>();
          ArrayList<String> categorySize = new ArrayList<String>();
          for (int i = 1; i <= rowCount; i++) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
                ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
                if (aList.contains(C2CReturnCode)) {
                      ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.TRANSFER_RULE_SHEET);
                      alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
                      alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
                }
          }

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

        /*
		 * Calculate the Count of Users for each category
		 */
          int totalObjectCounter = 0;
          for (int i=0; i<alist1.size(); i++) {
        	  int categorySizeCounter = 0;
        	  for (int excelCounter=0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
        		  if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,excelCounter).equals(alist1.get(i))){
        			  categorySizeCounter++;
        			  }
        	  }
        	  categorySize.add(""+categorySizeCounter);
        	  totalObjectCounter = totalObjectCounter + categorySizeCounter;
          }
                            
		/*
		* Counter to count number of users exists in channel users hierarchy sheet 
		* of Categories for which C2C Withdraw is allowed
		*/
          
          Object[][] Data = new Object[totalObjectCounter][4];
          
          for(int j=0, k=0;j<alist1.size();j++){
        	  
        	  ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
  	  		  int excelRowSize = ExcelUtility.getRowCount();
  	  		  String ChannelUserPIN = null; String parentCategory = "";
              for(int i=1;i<=excelRowSize;i++){
                  if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,i).equals(alist2.get(j))){
                	  	ChannelUserPIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                	  	parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                        break;
                        }
              }
        	  
        	  		for(int excelCounter=1; excelCounter <=excelRowSize; excelCounter++){
                        if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,excelCounter).equals(alist1.get(j))){
                              Data[k][0] = alist2.get(j);
                              String fromCode = DBHandler.AccessHandler.getCategoryCode(Data[k][0].toString());
                              String toCode = DBHandler.AccessHandler.getCategoryCode(parentCategory);
                              if(DBHandler.AccessHandler.getcontroltransferlevel("return", fromCode,toCode).equalsIgnoreCase("PARENT"))
                            	 {Data[k][1] = parentCategory;
                            	 int row = ExtentI.combinationExistAtRow(new String[]{ExcelI.PARENT_CATEGORY_NAME,ExcelI.CATEGORY_NAME}, new String[]{Data[k][1].toString(),Data[k][0].toString()}, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
                            	 String[] parentMSISDN = DBHandler.AccessHandler.getParentUserDetails(ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, row), "MSISDN");
                            	 Data[k][2]= parentMSISDN[0];}
                              else{Data[k][1] = alist1.get(j);
                            	  Data[k][2] = ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.MSISDN, excelCounter);}
                              Data[k][3] = ChannelUserPIN;
                              k++;
                              }
                        }

          }                       
            
          return Data;
    }
}
