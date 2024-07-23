package com.Features;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.homepages.O2CTransferSubLink;
import com.pageobjects.channeluserspages.o2creturn.O2CReturn_Page_1;
import com.pageobjects.channeluserspages.o2creturn.O2CReturn_Page_2;
import com.pageobjects.channeluserspages.o2creturn.O2CReturn_Page_3;
import com.utils.ExcelUtility;
import com.utils._masterVO;

public class O2CReturn extends BaseTest{
	WebDriver driver;

	Login login1;
	ChannelUserHomePage HomePage;
	O2CReturn_Page_1 O2CReturnPage1;
	O2CReturn_Page_2 O2CReturnPage2;
	O2CReturn_Page_3 O2CReturnPage3;
	O2CTransferSubLink O2CTransferSubLink;
	
	public O2CReturn(WebDriver driver){
		this.driver = driver;
		login1= new Login();
		HomePage = new ChannelUserHomePage(driver);
		O2CTransferSubLink = new O2CTransferSubLink(driver);
		O2CReturnPage1 = new O2CReturn_Page_1(driver);
		O2CReturnPage2= new O2CReturn_Page_2(driver);
		O2CReturnPage3= new O2CReturn_Page_3(driver);
	}
	
	public void performO2CReturn(String parentCategory, String category, String userMSISDN, String productType, String quantity, String Remarks){
		
		login1.UserLogin(driver, "ChannelUser", parentCategory, category);
		HomePage.clickO2CTransfer();
		O2CTransferSubLink.clickReturnLink();
		
		boolean selectDropdownVisible = O2CReturnPage1.isSelectProductTypeVisible() ;
		if(selectDropdownVisible==true) {
			O2CReturnPage1.selectProductType(productType);
		}
		
		
		O2CReturnPage1.clickSubmitButton();
		
		O2CReturnPage2.enterQuantity(quantity);
		O2CReturnPage2.enterRemarks(Remarks);
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCnt=ExcelUtility.getRowCount();
		String PIN=null;
		
		/*
		 * Preference to check if pin need to be entered for O2C Withdraw
		 */
		String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(category);
		
		if(pinAllowed.equals("Y")){
			for(int x=1; x<=rowCnt;x++){
				if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, x).equals(category) && ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, x).equals(parentCategory))
				{
					PIN= ExcelUtility.getCellData(0, ExcelI.PIN, x);
					break;
				}
			}
		O2CReturnPage2.enterPIN(PIN);
		}
		
		O2CReturnPage2.clickSubmitButton();
		O2CReturnPage3.clickConfirmButton();
	}
	
	public String performO2CReturnPinIncorrect(String parentCategory, String category, String userMSISDN, String productType, String quantity, String Remarks){
		
		login1.UserLogin(driver, "ChannelUser", parentCategory, category);
		HomePage.clickO2CTransfer();
		O2CTransferSubLink.clickReturnLink();
		
		boolean selectDropdownVisible = O2CReturnPage1.isSelectProductTypeVisible() ;
		if(selectDropdownVisible==true) {
			O2CReturnPage1.selectProductType(productType);
		}
		
		
		O2CReturnPage1.clickSubmitButton();
		
		O2CReturnPage2.enterQuantity(quantity);
		O2CReturnPage2.enterRemarks(Remarks);

		String PIN = "9090";
		O2CReturnPage2.enterPIN(PIN);
		O2CReturnPage2.clickSubmitButton();
		return  O2CReturnPage1.getMessage();
	}
}
