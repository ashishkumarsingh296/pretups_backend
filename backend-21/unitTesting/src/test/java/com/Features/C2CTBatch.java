package com.Features;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.VMS.InitiateVoucherO2CPage2;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.o2ctransfer.*;
import com.pageobjects.channeluserspages.c2ctransfer.C2CDetailsPage;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferConfirmPage;
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferDetailsPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.sublinks.ChannelUserSubLinkPages;
import com.pageobjects.channeluserspages.c2ctransferbatch.C2CTrasnferBatchLinks ;
import com.pretupsControllers.BTSLUtil;
import com.utils.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;

public class C2CTBatch {

	WebDriver driver=null;
	static String PathOfFile = _masterVO.getProperty("C2CBulkTransferPath") ;


	C2CTrasnferBatchLinks c2ctransferbatchlinks ;
	C2CTransferDetailsPage C2CTransferDetailsPage;
	C2CDetailsPage C2CDetailsPage;
	C2CTransferConfirmPage C2CTransferConfirmPage;
	ChannelUserHomePage CHhomePage;
	ChannelAdminHomePage caHomepage;
	Login login;
	String voucherSegment =_masterVO.getProperty("segmentType");
	RandomGeneration randomNum;
	HashMap<String, String> c2cTransferMap;
	ChannelUserSubLinkPages chnlSubLink;
	ApproveLevel1Page approveLevel1Page;
	ApproveLevel1Page2 approveLevel1Page2;
	ApproveLevel1Page3 approveLevel1Page3;
	ApproveLevel1Page4 approveLevel1Page4;

	ApproveLevel2Page approveLevel2Page;
	ApproveLevel2Page2 approveLevel2Page2;
	ApproveLevel2Page3 approveLevel2Page3;
	ApproveLevel2Page4 approveLevel2Page4;

	ApproveLevel3Page approveLevel3Page;
	ApproveLevel3Page2 approveLevel3Page2;
	ApproveLevel3Page3 approveLevel3Page3;
	ApproveLevel3Page4 approveLevel3Page4;

	InitiateVoucherO2CPage2 initiateVoucherO2CPage2;


	public C2CTBatch(WebDriver driver){
	this.driver=driver;	
	caHomepage = new ChannelAdminHomePage(driver);
	C2CTransferDetailsPage = new C2CTransferDetailsPage(driver);
	C2CDetailsPage = new C2CDetailsPage(driver);
	C2CTransferConfirmPage = new C2CTransferConfirmPage(driver);
	CHhomePage = new ChannelUserHomePage(driver);
	c2ctransferbatchlinks = new C2CTrasnferBatchLinks(driver) ;
	login = new Login();
	randomNum = new RandomGeneration();
	c2cTransferMap=new HashMap<String, String>();
	chnlSubLink= new ChannelUserSubLinkPages(driver);
	approveLevel1Page = new ApproveLevel1Page(driver);
	approveLevel1Page2 = new ApproveLevel1Page2(driver);
	approveLevel1Page3 = new ApproveLevel1Page3(driver);
	approveLevel1Page4 = new ApproveLevel1Page4(driver);

	approveLevel2Page = new ApproveLevel2Page(driver);
	approveLevel2Page2 = new ApproveLevel2Page2(driver);
	approveLevel2Page3 = new ApproveLevel2Page3(driver);
	approveLevel2Page4 = new ApproveLevel2Page4(driver);

	approveLevel3Page = new ApproveLevel3Page(driver);
	approveLevel3Page2 = new ApproveLevel3Page2(driver);
	approveLevel3Page3 = new ApproveLevel3Page3(driver);
	approveLevel3Page4 = new ApproveLevel3Page4(driver);
	initiateVoucherO2CPage2 = new InitiateVoucherO2CPage2(driver);
	}

	public HashMap<String, String> channel2channelTransferBatch(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		Log.info("From category : "+FromCategory+" && To Category : "+ToCategory) ;
		Log.info("TO MSISDN : "+MSISDN) ;
		String arr[][] = null ;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		String parent = driver.getWindowHandle() ;

		String value ;
		if(FromCategory.equals("Distributor"))
		{
			value = "D" ;
		}else if(FromCategory.equals("Distributor Agent"))
		{
			value = "DA" ;
		}else if(FromCategory.equals("Super Dealer"))
		{
			value = "SD" ;
		}else if(FromCategory.equals("Super Dealer Agent"))
		{
			value = "SDA" ;
		}else if(FromCategory.equals("POS"))
		{
			value = "POS" ;
		}else
		{
			value = null ;
		}
		Log.info("C2C Trasnfer is to Category : "+value) ;
		c2ctransferbatchlinks.selectCategoryDropdown(value) ;
		String latestFileName ;
		String FilePathAndName ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadUserListLink() ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;
		int noOfRows = ExcelUtility.getRowCount() ;
		int counter_msisdn,counter_login ;
		arr = new String[noOfRows][4] ;
		for(int i=(noOfRows-5); i<noOfRows; i++)
		{
			counter_msisdn = 0 ;
			counter_login=0;
			arr[i][0] = ExcelUtility.getCellDataHSSF(i,0) ;  	//msisdn
			if(arr[i][0] != null && !((arr[i][0]).equals(""))){counter_msisdn+=1 ;}
			arr[i][1] = ExcelUtility.getCellDataHSSF(i,1) ;		//loginid
			if(arr[i][1] != null && !((arr[i][1]).equals(""))){counter_login+=1 ;}
			arr[i][2] = ExcelUtility.getCellDataHSSF(i,3) ;		//externalcode
			if(arr[i][2] != null && !((arr[i][2]).equals(""))){counter_login+=1 ;}
			if(arr[i][2]!=null && !((arr[i][2]).equals("")))
			{
				arr[i][3]= "50" ;		//qty
			}
			if(counter_msisdn ==1 || counter_login ==2)
			{
				Log.info("\n\n COUNTER_MSISDN = "+counter_msisdn) ;
				Log.info("\n\n COUNTER_Login = "+counter_login) ;
				arr[0][0] = arr[i][0] ;
				arr[0][1] = arr[i][1] ;
				arr[0][2] = arr[i][2] ;
				arr[0][3] = arr[i][3] ;
				Log.info("UNEMPTY STORED IN ARRAY TO WRITE IN EXCEL") ;
				Log.info("arr[0][0] "+arr[i][0]) ;
				Log.info("arr[0][1] "+arr[i][1]) ;
				Log.info("arr[0][2] "+arr[i][2]) ;
				Log.info("arr[0][3] "+arr[i][3]) ;
				break ;
			}else{
				Log.info("Data read from file is incomplete") ;
			}
		}
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String batchName = c2ctransferbatchlinks.enterBatchName("AUTBN" + randomNum.randomNumeric(5)) ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadFileTemplateLink() ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		Log.info("File path : "+FilePathAndName) ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;

		for(int j=0; j<=3; j++)
		{
			ExcelUtility.setCellDataXLS(arr[0][j],1, j) ;
		}
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String filepath=_masterVO.getProperty("C2CBulkTransferPath");
		String filename = c2ctransferbatchlinks.getLatestFileNamefromDir(filepath);
		String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2CBulkTransferUpload") + filename;
		Log.info("Upload Path :: "+uploadPath) ;
		c2ctransferbatchlinks.uploadFile(uploadPath) ;
		c2ctransferbatchlinks.clickSubmitButton() ;
		c2ctransferbatchlinks.enterDefaultLanguage() ;
		c2ctransferbatchlinks.enterPin(PIN) ;
		c2ctransferbatchlinks.clickConfirmButton() ;
		String actualMessage = c2ctransferbatchlinks.getSuccessMessage() ;
		String batchTrasnactionID =  c2ctransferbatchlinks.getBatchID(actualMessage) ;
		String expectedStatus = "Batch "+batchName+"("+batchTrasnactionID+") initiated successfully with 1 records." ;

		c2cTransferMap.put("TransactionID", batchTrasnactionID);
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltochannel.batch.transfer.msg.success", batchName,batchTrasnactionID));
		c2cTransferMap.put("actualMessage", actualMessage);

		/*String message = "Batch abcd(MOCB210916.002) initiated successfully with 1 records." ;
		String batchTrasnactionID =  c2ctransferbatchlinks.getBatchID(message) ;*/
		Log.info("Transaction ID :: "+batchTrasnactionID) ;
		ExtentI.attachScreenShot() ;
		Log.methodExit(methodname);
		return c2cTransferMap;
	}

	public void channel2channelTransferBatchEmptyCategory(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		c2ctransferbatchlinks.clickSubmitButton() ;
		List<WebElement> errorMessages = c2ctransferbatchlinks.getBlankFieldErrors() ;
		String actualMessage = null ;
		String expectedMessage = "Category is required." ;
		boolean flag = false ;
		for(WebElement el : errorMessages){
			actualMessage = el.getText() ;
			if(actualMessage.equals(expectedMessage)){
				flag = true ;
				break ;
			}
		}
		if (flag) {
			Assertion.assertEquals(actualMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void channel2channelTransferBatchEmptyBatchName(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		c2ctransferbatchlinks.clickSubmitButton() ;
		List<WebElement> errorMessages = c2ctransferbatchlinks.getBlankFieldErrors() ;
		String actualMessage = null ;
		String expectedMessage = "Batch name is required." ;
		boolean flag = false ;
		for(WebElement el : errorMessages){
			actualMessage = el.getText() ;
			if(actualMessage.equals(expectedMessage)){
				flag = true ;
				break ;
			}
		}
		if (flag) {
			Assertion.assertEquals(actualMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void channel2channelTransferBatchNoFileUploaded(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		c2ctransferbatchlinks.clickSubmitButton() ;
		List<WebElement> errorMessages = c2ctransferbatchlinks.getBlankFieldErrors() ;
		String actualMessage = null ;
		String expectedMessage = "Uploaded file path is required." ;
		boolean flag = false ;
		for(WebElement el : errorMessages){
			actualMessage = el.getText() ;
			if(actualMessage.equals(expectedMessage)){
				flag = true ;
				break ;
			}
		}
		if (flag) {
			Assertion.assertEquals(actualMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void channel2channelTransferBatchAlphanumericPIN(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		Log.info("From category : "+FromCategory+" && To Category : "+ToCategory) ;
		Log.info("TO MSISDN : "+MSISDN) ;
		String arr[][] = null ;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		String parent = driver.getWindowHandle() ;

		String value ;
		if(FromCategory.equals("Distributor"))
		{
			value = "D" ;
		}else if(FromCategory.equals("Distributor Agent"))
		{
			value = "DA" ;
		}else if(FromCategory.equals("Super Dealer"))
		{
			value = "SD" ;
		}else if(FromCategory.equals("Super Dealer Agent"))
		{
			value = "SDA" ;
		}else if(FromCategory.equals("POS"))
		{
			value = "POS" ;
		}else
		{
			value = null ;
		}
		Log.info("C2C Trasnfer is to Category : "+value) ;
		c2ctransferbatchlinks.selectCategoryDropdown(value) ;
		String latestFileName ;
		String FilePathAndName ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadUserListLink() ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;
		int noOfRows = ExcelUtility.getRowCount() ;
		int counter_msisdn,counter_login ;
		arr = new String[noOfRows][4] ;
		for(int i=(noOfRows-5); i<noOfRows; i++)
		{
			counter_msisdn = 0 ;
			counter_login=0;
			arr[i][0] = ExcelUtility.getCellDataHSSF(i,0) ;  	//msisdn
			if(arr[i][0] != null && !((arr[i][0]).equals(""))){counter_msisdn+=1 ;}
			arr[i][1] = ExcelUtility.getCellDataHSSF(i,1) ;		//loginid
			if(arr[i][1] != null && !((arr[i][1]).equals(""))){counter_login+=1 ;}
			arr[i][2] = ExcelUtility.getCellDataHSSF(i,3) ;		//externalcode
			if(arr[i][2] != null && !((arr[i][2]).equals(""))){counter_login+=1 ;}
			if(arr[i][2]!=null && !((arr[i][2]).equals("")))
			{
				arr[i][3]= "50" ;		//qty
			}
			if(counter_msisdn ==1 || counter_login ==2)
			{
				Log.info("\n\n COUNTER_MSISDN = "+counter_msisdn) ;
				Log.info("\n\n COUNTER_Login = "+counter_login) ;
				arr[0][0] = arr[i][0] ;
				arr[0][1] = arr[i][1] ;
				arr[0][2] = arr[i][2] ;
				arr[0][3] = arr[i][3] ;
				Log.info("UNEMPTY STORED IN ARRAY TO WRITE IN EXCEL") ;
				Log.info("arr[0][0] "+arr[i][0]) ;
				Log.info("arr[0][1] "+arr[i][1]) ;
				Log.info("arr[0][2] "+arr[i][2]) ;
				Log.info("arr[0][3] "+arr[i][3]) ;
				break ;
			}else{
				Log.info("Could not read data from Downloaded User List") ;
				break ;
			}
		}
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String batchName = c2ctransferbatchlinks.enterBatchName("AUTBN" + randomNum.randomNumeric(5)) ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadFileTemplateLink() ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		Log.info("File path : "+FilePathAndName) ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;

		for(int j=0; j<=3; j++)
		{
			ExcelUtility.setCellDataXLS(arr[0][j],1, j) ;
		}
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String filepath=_masterVO.getProperty("C2CBulkTransferPath");
		String filename = c2ctransferbatchlinks.getLatestFileNamefromDir(filepath);
		String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2CBulkTransferUpload") + filename;
		Log.info("Upload Path :: "+uploadPath) ;
		c2ctransferbatchlinks.uploadFile(uploadPath) ;
		c2ctransferbatchlinks.clickSubmitButton() ;
		c2ctransferbatchlinks.enterDefaultLanguage() ;
		String alphanumericPIN = randomNum.randomAlphaNumeric(4) ;
		c2ctransferbatchlinks.enterPin(alphanumericPIN) ;
		c2ctransferbatchlinks.clickConfirmButton() ;
		String errorMessage = c2ctransferbatchlinks.getAlphanumericPINMessage() ;
		String expectedMessage = "Invalid PIN" ;
		boolean flag = false ;
		if(errorMessage.equals(expectedMessage)){
			flag = true ;
		}
		if (flag) {
			Assertion.assertEquals(errorMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void channel2channelTransferBatchZeroPIN(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		Log.info("From category : "+FromCategory+" && To Category : "+ToCategory) ;
		Log.info("TO MSISDN : "+MSISDN) ;
		String arr[][] = null ;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		String parent = driver.getWindowHandle() ;

		String value ;
		if(FromCategory.equals("Distributor"))
		{
			value = "D" ;
		}else if(FromCategory.equals("Distributor Agent"))
		{
			value = "DA" ;
		}else if(FromCategory.equals("Super Dealer"))
		{
			value = "SD" ;
		}else if(FromCategory.equals("Super Dealer Agent"))
		{
			value = "SDA" ;
		}else if(FromCategory.equals("POS"))
		{
			value = "POS" ;
		}else
		{
			value = null ;
		}
		Log.info("C2C Trasnfer is to Category : "+value) ;
		c2ctransferbatchlinks.selectCategoryDropdown(value) ;
		String latestFileName ;
		String FilePathAndName ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadUserListLink() ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;
		int noOfRows = ExcelUtility.getRowCount() ;
		int counter_msisdn,counter_login ;
		arr = new String[noOfRows][4] ;
		for(int i=(noOfRows-5); i<noOfRows; i++)
		{
			counter_msisdn = 0 ;
			counter_login=0;
			arr[i][0] = ExcelUtility.getCellDataHSSF(i,0) ;  	//msisdn
			if(arr[i][0] != null && !((arr[i][0]).equals(""))){counter_msisdn+=1 ;}
			arr[i][1] = ExcelUtility.getCellDataHSSF(i,1) ;		//loginid
			if(arr[i][1] != null && !((arr[i][1]).equals(""))){counter_login+=1 ;}
			arr[i][2] = ExcelUtility.getCellDataHSSF(i,3) ;		//externalcode
			if(arr[i][2] != null && !((arr[i][2]).equals(""))){counter_login+=1 ;}
			if(arr[i][2]!=null && !((arr[i][2]).equals("")))
			{
				arr[i][3]= "50" ;		//qty
			}
			if(counter_msisdn ==1 || counter_login ==2)
			{
				Log.info("\n\n COUNTER_MSISDN = "+counter_msisdn) ;
				Log.info("\n\n COUNTER_Login = "+counter_login) ;
				arr[0][0] = arr[i][0] ;
				arr[0][1] = arr[i][1] ;
				arr[0][2] = arr[i][2] ;
				arr[0][3] = arr[i][3] ;
				Log.info("UNEMPTY STORED IN ARRAY TO WRITE IN EXCEL") ;
				Log.info("arr[0][0] "+arr[i][0]) ;
				Log.info("arr[0][1] "+arr[i][1]) ;
				Log.info("arr[0][2] "+arr[i][2]) ;
				Log.info("arr[0][3] "+arr[i][3]) ;
				break ;
			}else{
				Log.info("Could not read data from Downloaded User List") ;
				break ;
			}
		}
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String batchName = c2ctransferbatchlinks.enterBatchName("AUTBN" + randomNum.randomNumeric(5)) ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadFileTemplateLink() ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		Log.info("File path : "+FilePathAndName) ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;

		for(int j=0; j<=3; j++)
		{
			ExcelUtility.setCellDataXLS(arr[0][j],1, j) ;
		}
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String filepath=_masterVO.getProperty("C2CBulkTransferPath");
		String filename = c2ctransferbatchlinks.getLatestFileNamefromDir(filepath);
		String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2CBulkTransferUpload") + filename;
		Log.info("Upload Path :: "+uploadPath) ;
		c2ctransferbatchlinks.uploadFile(uploadPath) ;
		c2ctransferbatchlinks.clickSubmitButton() ;
		c2ctransferbatchlinks.enterDefaultLanguage() ;
		c2ctransferbatchlinks.enterPin("0000") ;
		c2ctransferbatchlinks.clickConfirmButton() ;
		String errorMessage = c2ctransferbatchlinks.getAlphanumericPINMessage() ;
		String expectedMessage = "Invalid PIN" ;
		boolean flag = false ;
		if(errorMessage.equals(expectedMessage)){
			flag = true ;
		}
		if (flag) {
			Assertion.assertEquals(errorMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}


	public void channel2channelTransferBatchUploadEmptyFile(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		Log.info("From category : "+FromCategory+" && To Category : "+ToCategory) ;
		Log.info("TO MSISDN : "+MSISDN) ;
		String arr[][] = null ;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		String parent = driver.getWindowHandle() ;

		String value ;
		if(FromCategory.equals("Distributor"))
		{
			value = "D" ;
		}else if(FromCategory.equals("Distributor Agent"))
		{
			value = "DA" ;
		}else if(FromCategory.equals("Super Dealer"))
		{
			value = "SD" ;
		}else if(FromCategory.equals("Super Dealer Agent"))
		{
			value = "SDA" ;
		}else if(FromCategory.equals("POS"))
		{
			value = "POS" ;
		}else
		{
			value = null ;
		}
		Log.info("C2C Trasnfer is to Category : "+value) ;
		c2ctransferbatchlinks.selectCategoryDropdown(value) ;
		String latestFileName ;
		String FilePathAndName ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadFileTemplateLink() ;
		String batchName = c2ctransferbatchlinks.enterBatchName("AUTBN" + randomNum.randomNumeric(5)) ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		Log.info("File path : "+FilePathAndName) ;
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String filepath=_masterVO.getProperty("C2CBulkTransferPath");
		String filename = c2ctransferbatchlinks.getLatestFileNamefromDir(filepath);
		String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2CBulkTransferUpload") + filename;
		Log.info("Upload Path :: "+uploadPath) ;
		c2ctransferbatchlinks.uploadFile(uploadPath) ;
		c2ctransferbatchlinks.clickSubmitButton() ;
		c2ctransferbatchlinks.enterDefaultLanguage() ;
		String alphanumericPIN = randomNum.randomAlphaNumeric(4) ;
		c2ctransferbatchlinks.enterPin(PIN) ;
		c2ctransferbatchlinks.clickConfirmButton() ;
		String errorMessage = c2ctransferbatchlinks.getEmptyFileMessage() ;
		String expectedMessage = "There are no records in the file to be processed" ;
		boolean flag = false ;
		if(errorMessage.equals(expectedMessage)){
			flag = true ;
		}
		if (flag) {
			Assertion.assertEquals(errorMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}
	
	public HashMap<String, String> channel2channelTransfer(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransfer";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferLink();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		C2CTransferDetailsPage.enterMobileNo(MSISDN);
		C2CTransferDetailsPage.clickSubmit();
		C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		c2cTransferMap.put("InitiatedQuantities", C2CTransferDetailsPage.enterQuantityforC2C());
		C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CDetailsPage.enterSmsPin(PIN);
		if (isPaymentMethodMandatory()) {
			C2CDetailsPage.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			C2CDetailsPage.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			C2CDetailsPage.enterPaymentInstDate(caHomepage.getDate());
		}
		C2CDetailsPage.clickSubmit();
	//	if(!C2CDetailsPage.checkSMSPINEmpty()) {
		//C2CDetailsPage.enterSmsPin(PIN);
			C2CTransferConfirmPage.clickConfirm();
	//	}
		String message = C2CDetailsPage.getMessage();
		String transactionID = _parser.getTransactionID(message, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID);
		c2cTransferMap.put("TransactionID", transactionID);
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltochannel.transfer.msg.success", transactionID));
		c2cTransferMap.put("actualMessage", message);

		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> channel2channelVocuherTransfer(String FromCategory,String ToCategory, String MSISDN, String PIN,Object[][] dataObj, String loginID) throws InterruptedException {
		final String methodname = "channel2channelTransfer";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		HashMap<String, String> initiateMap = (HashMap<String, String>) dataObj[0][0];
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CVoucherTransferLink();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);

		C2CTransferDetailsPage.enterMobileNo(MSISDN);
		C2CTransferDetailsPage.clickSubmit();
		C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		//c2cTransferMap.put("InitiatedQuantities", C2CTransferDetailsPage.enterQuantityforC2C());
		initiateVoucherO2CPage2.SelectVoucherType(initiateMap.get("voucherType"));
		initiateVoucherO2CPage2.SelectVoucherSegment(voucherSegment);
		initiateVoucherO2CPage2.SelectDenomination(initiateMap.get("denomination"));
		String productID = DBHandler.AccessHandler.fetchProductID(initiateMap.get("activeProfile"));
		String status ="EN";
		String userID =DBHandler.AccessHandler.getUserIdLoginID(loginID);
		String SerialNumber = DBHandler.AccessHandler.getMinSerialNumberuserID(productID,status,userID);
		initiateMap.put("fromSerialNumber", SerialNumber);
		initiateMap.put("toSerialNumber", SerialNumber);
		initiateVoucherO2CPage2.EnterFromSerialNumber(initiateMap.get("fromSerialNumber"));
		initiateVoucherO2CPage2.EnterToSerialNumber(initiateMap.get("toSerialNumber"));
		C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CDetailsPage.enterSmsPin(PIN);
		if (isPaymentMethodMandatoryVoucher()) {
			C2CDetailsPage.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			C2CDetailsPage.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			C2CDetailsPage.enterPaymentInstDate(caHomepage.getDate());
		}
		
		C2CDetailsPage.clickVocuherSubmit();
		C2CTransferConfirmPage.clickConfirm();
		String message = C2CDetailsPage.getMessage();
		String transactionID = _parser.getTransactionID(message, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID);
		c2cTransferMap.put("TransactionID", transactionID);
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltochannel.transfer.msg.success", transactionID));
		c2cTransferMap.put("actualMessage", message);

		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	private boolean isPaymentMethodMandatory() {
		Log.info("Entered :: isPaymentMethodMandatory()");
		int isPaymentDetailsMandate =0;
		String value = DBHandler.AccessHandler.getSystemPreference("PAYMENTDETAILSMANDATE_C2C");
	//	int isPaymentDetailsMandate = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("PAYMENTDETAILSMANDATE_C2C"));
		if(BTSLUtil.isNullString(value)) {
			 isPaymentDetailsMandate=-1;
        }
        else
        	 isPaymentDetailsMandate = Integer.parseInt(value);
		if (isPaymentDetailsMandate != -1 && isPaymentDetailsMandate == 0) {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=true");
			return true;
		} else {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=false");
			return false;
		}
	}
	
	private boolean isPaymentMethodMandatoryVoucher() {
		Log.info("Entered :: isPaymentMethodMandatory()");
		int isPaymentDetailsMandate = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("PAYMENTDETAILSMANDATEVOUCHER_C2C"));
		if (isPaymentDetailsMandate != -1 && isPaymentDetailsMandate == 0) {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=true");
			return true;
		} else {
			Log.info("Exiting :: isPaymentMethodMandatory() with value=false");
			return false;
		}
	}
	
	//Log.methodEntry(productType, quantity, FromCategory, ToCategory, MSISDN,PIN,multiproduct);
	public HashMap<String,String> channel2channelTransfer(String... var) throws InterruptedException {	
		final String methodname = "channel2channelTransfer";
		
		String quantity=var[1], productType=var[0], FromCategory=var[2], ToCategory=var[3], MSISDN=var[4], PIN=var[5],multiproduct=var[6];
		Log.methodEntry(productType, quantity, FromCategory, ToCategory, MSISDN,PIN,multiproduct);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferLink();
		C2CTransferDetailsPage.enterMobileNo(MSISDN);
		C2CTransferDetailsPage.clickSubmit();
		C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		C2CTransferDetailsPage.enterQuantityforC2C(productType, quantity,multiproduct);
		C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CDetailsPage.enterSmsPin(PIN);
		if (isPaymentMethodMandatory()) {
			C2CDetailsPage.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			C2CDetailsPage.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			C2CDetailsPage.enterPaymentInstDate(caHomepage.getDate());
		}
		C2CDetailsPage.clickSubmit();
		C2CTransferConfirmPage.clickConfirm();

		String message = C2CDetailsPage.getMessage();
		String transactionID = _parser.getTransactionID(message, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID);
		c2cTransferMap.put("TransactionID", transactionID);
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltochannel.transfer.msg.success", transactionID));
		c2cTransferMap.put("actualMessage", message);

		Log.methodExit(methodname);
		return c2cTransferMap;
		
	}
	
	public HashMap<String, String> performingLevel1Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval1";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer() ;
		c2ctransferbatchlinks.clickApproveC2CBatchButton() ;
		c2ctransferbatchlinks.selectC2CBatchForApproval(TransactionID) ;
		c2ctransferbatchlinks.clickSubmitButtonAtApproval() ;
		c2ctransferbatchlinks.clickBatchApprove() ;
		c2ctransferbatchlinks.enterRemarksForApproval() ;
		c2ctransferbatchlinks.clickApproveButton() ;
		c2ctransferbatchlinks.handleAlertBox() ;
		String expectedMessage = "Batch is processed successfully, 1 records approved." ;
		String actualMessage = c2ctransferbatchlinks.getBatchSuccessMessage() ;

		Assertion.assertEquals(actualMessage, expectedMessage) ;
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}

	public HashMap<String, String> performingLevel1Reject(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval1";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer() ;
		c2ctransferbatchlinks.clickApproveC2CBatchButton() ;
		c2ctransferbatchlinks.selectC2CBatchForApproval(TransactionID) ;
		c2ctransferbatchlinks.clickSubmitButtonAtApproval() ;
		c2ctransferbatchlinks.clickBatchReject() ;
		c2ctransferbatchlinks.enterRemarksForApproval() ;
		c2ctransferbatchlinks.clickRejectButton() ;
		c2ctransferbatchlinks.handleAlertBox() ;
		String expectedMessage = "Batch is processed successfully, 1 records rejected." ;
		String actualMessage = c2ctransferbatchlinks.getBatchRejectMessage() ;

		Assertion.assertEquals(actualMessage, expectedMessage) ;
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}

	public HashMap<String, String> channel2channelUserListDownloaded(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		Log.info("From category : "+FromCategory+" && To Category : "+ToCategory) ;
		Log.info("TO MSISDN : "+MSISDN) ;
		String arr[][] = null ;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		String parent = driver.getWindowHandle() ;

		String value ;
		if(FromCategory.equals("Distributor"))
		{
			value = "D" ;
		}else if(FromCategory.equals("Distributor Agent"))
		{
			value = "DA" ;
		}else if(FromCategory.equals("Super Dealer"))
		{
			value = "SD" ;
		}else if(FromCategory.equals("Super Dealer Agent"))
		{
			value = "SDA" ;
		}else if(FromCategory.equals("POS"))
		{
			value = "POS" ;
		}else
		{
			value = null ;
		}
		Log.info("C2C Trasnfer is to Category : "+value) ;
		c2ctransferbatchlinks.selectCategoryDropdown(value) ;
		String latestFileName ;
		String FilePathAndName ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadUserListLink() ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String downloadDirPath = _masterVO.getProperty("C2CBulkTransferPath") ;
		int NoOfFilesAfter = c2ctransferbatchlinks.noOfFilesInDownloadedDirectory(downloadDirPath) ;

		Log.info("No Of Files After Download = "+NoOfFilesAfter) ;

		boolean flag = false ;
		if(NoOfFilesAfter == 1)
		{ flag = true  ; }
		Log.methodExit(methodname);
		return c2cTransferMap;
	}

	public void channel2channelDownloadUserlistWithoutCategory(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		String parent = driver.getWindowHandle() ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadUserListLink() ;
		String messageFromAlert = c2ctransferbatchlinks.getAlertPopupMessage() ;
		String expectedMessage = "First select channel user category" ;
		boolean flag = false ;
		if(messageFromAlert.equals(expectedMessage)){
			flag = true ;
		}
		if (flag) {
			Assertion.assertEquals(messageFromAlert, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "File downlaoded without selecting Category");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void channel2channelDownloadTemplateWithoutCategory(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		String parent = driver.getWindowHandle() ;
		ExcelUtility.deleteFiles(PathOfFile);
		String downloadDirPath = _masterVO.getProperty("C2CBulkTransferPath") ;

		c2ctransferbatchlinks.clickdownloadFileTemplateLink() ;
		c2ctransferbatchlinks.closeBrowser(parent) ;
		int NoOfFilesAfter = c2ctransferbatchlinks.noOfFilesInDownloadedDirectory(downloadDirPath) ;

		Log.info("No Of Files After Download = "+NoOfFilesAfter) ;

		boolean flag = false ;
		if(NoOfFilesAfter == 1)
		{ flag = true  ; }
		if (flag) {
			ExtentI.Markup(ExtentColor.GREEN, "File downlaoded without selecting Category Successfully");
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "File didn't downlaoded without selecting Category");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void channel2channelBlankPIN(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		Log.info("From category : "+FromCategory+" && To Category : "+ToCategory) ;
		Log.info("TO MSISDN : "+MSISDN) ;
		String arr[][] = null ;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		String parent = driver.getWindowHandle() ;

		String value ;
		if(FromCategory.equals("Distributor"))
		{
			value = "D" ;
		}else if(FromCategory.equals("Distributor Agent"))
		{
			value = "DA" ;
		}else if(FromCategory.equals("Super Dealer"))
		{
			value = "SD" ;
		}else if(FromCategory.equals("Super Dealer Agent"))
		{
			value = "SDA" ;
		}else if(FromCategory.equals("POS"))
		{
			value = "POS" ;
		}else
		{
			value = null ;
		}
		Log.info("C2C Trasnfer is to Category : "+value) ;
		c2ctransferbatchlinks.selectCategoryDropdown(value) ;
		String latestFileName ;
		String FilePathAndName ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadUserListLink() ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String batchName = c2ctransferbatchlinks.enterBatchName("AUTBN" + randomNum.randomNumeric(5)) ;
		Log.info("File path : "+FilePathAndName) ;
		String filepath=_masterVO.getProperty("C2CBulkTransferPath");
		String filename = c2ctransferbatchlinks.getLatestFileNamefromDir(filepath);
		String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2CBulkTransferUpload") + filename;
		Log.info("Upload Path :: "+uploadPath) ;
		c2ctransferbatchlinks.uploadFile(uploadPath) ;
		c2ctransferbatchlinks.clickSubmitButton() ;
		c2ctransferbatchlinks.enterDefaultLanguage() ;
		c2ctransferbatchlinks.enterPin("") ;
		c2ctransferbatchlinks.clickConfirmButton() ;
		String errorMessage = c2ctransferbatchlinks.getBlankPINMessage() ;
		String expectedMessage = "PIN is required." ;
		boolean flag = false ;
		if(errorMessage.equals(expectedMessage)){
			flag = true ;
		}
		if (flag) {
			Assertion.assertEquals(errorMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public void channel2channelInvalidHeader(String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {
		final String methodname = "channel2channelTransferBatch";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		Log.info("From category : "+FromCategory+" && To Category : "+ToCategory) ;
		Log.info("TO MSISDN : "+MSISDN) ;
		String arr[][] = null ;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		c2ctransferbatchlinks.clickInitiateC2CBatch() ;
		String parent = driver.getWindowHandle() ;

		String value ;
		if(FromCategory.equals("Distributor"))
		{
			value = "D" ;
		}else if(FromCategory.equals("Distributor Agent"))
		{
			value = "DA" ;
		}else if(FromCategory.equals("Super Dealer"))
		{
			value = "SD" ;
		}else if(FromCategory.equals("Super Dealer Agent"))
		{
			value = "SDA" ;
		}else if(FromCategory.equals("POS"))
		{
			value = "POS" ;
		}else
		{
			value = null ;
		}
		Log.info("C2C Trasnfer is to Category : "+value) ;
		c2ctransferbatchlinks.selectCategoryDropdown(value) ;
		String latestFileName ;
		String FilePathAndName ;
		String batchName = c2ctransferbatchlinks.enterBatchName("AUTBN" + randomNum.randomNumeric(5)) ;
		ExcelUtility.deleteFiles(PathOfFile);
		c2ctransferbatchlinks.clickdownloadFileTemplateLink() ;
		latestFileName = c2ctransferbatchlinks.getLatestFileNamefromDir(PathOfFile) ;
		FilePathAndName = PathOfFile + latestFileName ;
		Log.info("File path : "+FilePathAndName) ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;
		for(int j = 0; j<ExcelUtility.getColumnCount() ; j++)
		{
			ExcelUtility.setCellDataXLS("Mobile", 0, j) ;
		}
		c2ctransferbatchlinks.closeBrowser(parent) ;
		String filepath=_masterVO.getProperty("C2CBulkTransferPath");
		String filename = c2ctransferbatchlinks.getLatestFileNamefromDir(filepath);
		String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2CBulkTransferUpload") + filename;
		Log.info("Upload Path :: "+uploadPath) ;
		c2ctransferbatchlinks.uploadFile(uploadPath) ;
		c2ctransferbatchlinks.clickSubmitButton() ;
		c2ctransferbatchlinks.enterDefaultLanguage() ;
		c2ctransferbatchlinks.enterPin(PIN) ;
		c2ctransferbatchlinks.clickConfirmButton() ;
		String errorMessage = c2ctransferbatchlinks.getInvalidHeaderMessage() ;
		String expectedMessage = "There are no records in the file to be processed" ;
		boolean flag = false ;
		if(errorMessage.equals(expectedMessage)){
			flag = true ;
		}
		if (flag) {
			Assertion.assertEquals(errorMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}

	public HashMap<String, String> performingLevel2Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,int level) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval2";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr2();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		if(level==2) {
			c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", TransactionID));
			}
			else {
				c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltransfer.approval.leveltwo.msg.success", TransactionID));
			}Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel3Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,int level) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval3";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr3();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltransfer.approval.msg.success", TransactionID));
		Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel1Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode, String... msgParameter) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval1";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr1();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey(msgCode, msgParameter));
		Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel2Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode, String... msgParameter) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval2";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr2();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey(msgCode, msgParameter));
		Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel3Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode, String... msgParameter) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval3";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr3();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey(msgCode, msgParameter));
		Assertion.assertEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel1Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval1";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr1();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage().replaceAll("\\r|\\n", ""));
		c2cTransferMap.put("expectedMessage", msgCode);
		Assertion.assertContainsEqualsSet(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel2Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval2";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr2();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage().replaceAll("\\r|\\n", ""));
		c2cTransferMap.put("expectedMessage",msgCode);
		Assertion.assertContainsEqualsSet(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel3Approval(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID,String msgCode) throws InterruptedException {
		final String methodname = "channel2channelTransferApproval3";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferApr3();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel1Page3.enterExternalTxnNum(UniqueChecker.UC_EXT_TXN_NO());
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickApproveBtn();

		approveLevel1Page4.clickConfirmButton();
		c2cTransferMap.put("TransactionID", TransactionID);
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage().replaceAll("\\r|\\n", ""));
		c2cTransferMap.put("expectedMessage",msgCode);
		Assertion.assertContainsEqualsSet(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		Assertion.completeAssertions();
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel1ApprovalVoucher(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID) throws InterruptedException {
		final String methodname = "channel2channelTransferApprovalVocuher1";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferVoucApr1();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		approveLevel2Page2.enterRemarks1("Automation Test");
			//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickSubmitBtn();

		approveLevel1Page4.clickConfirmButtonVoucher();
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	
	
	public HashMap<String, String> performingLevel2ApprovalVoucher(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID) throws InterruptedException {
		final String methodname = "channel2channelTransferApprovalVocuher2";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferVoucApr2();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		
		approveLevel2Page2.enterRemarks2("Automation Test");
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickSubmitBtn();

		approveLevel1Page4.clickConfirmButtonVoucher();
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
	public HashMap<String, String> performingLevel3ApprovalVoucher(String FromCategory,String ToCategory, String MSISDN, String PIN, String TransactionID) throws InterruptedException {
		final String methodname = "channel2channelTransferApprovalVocuher3";
		Log.methodEntry(methodname, FromCategory, ToCategory, MSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferVoucApr3();
		approveLevel1Page.enterMobileNumber(MSISDN);
		approveLevel1Page.clickSubmitBtn();
		approveLevel1Page2.selectTransferNum(TransactionID);
		approveLevel2Page2.clickSubmitBtn();
		approveLevel2Page2.enterRemarks3("Automation Test");
	//	approveLevel1Page3.enterExternalTxnDate(caHomepage.getDate());
		approveLevel1Page3.clickSubmitBtn();
		approveLevel1Page4.clickConfirmButtonVoucher();
		c2cTransferMap.put("actualMessage", approveLevel1Page.getMessage());
		Log.methodExit(methodname);
		return c2cTransferMap;
	}
	
public HashMap<String, String> channel2channelTransfer(String[] quantity,String[] productType,String FromCategory,String ToCategory, String MSISDN, String PIN) throws InterruptedException {	
		
		login.UserLogin(driver, "ChannelUser", FromCategory);
		CHhomePage.clickC2CTransfer();
		chnlSubLink.clickC2CTransferLink();
		C2CTransferDetailsPage.enterMobileNo(MSISDN);
		C2CTransferDetailsPage.clickSubmit();
		C2CDetailsPage.enterRefNum(randomNum.randomNumeric(6));
		C2CTransferDetailsPage.enterQuantityforC2C(quantity,productType);
		C2CDetailsPage.enterRemarks("Remarks entered for C2C to: "+ToCategory);
		C2CDetailsPage.enterSmsPin(PIN);
		if (isPaymentMethodMandatory()) {
			C2CDetailsPage.selectPaymentInstrumntType(PretupsI.PMTYP_CASH_LOOKUP);
			C2CDetailsPage.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
			C2CDetailsPage.enterPaymentInstDate(caHomepage.getDate());
		}
		C2CDetailsPage.clickSubmit();
		C2CTransferConfirmPage.clickConfirm();
		String message = C2CDetailsPage.getMessage();
		String transactionID = _parser.getTransactionID(message, PretupsI.CHANNEL_TO_CHANNEL_TRANSFER_ID);
		c2cTransferMap.put("TransactionID", transactionID);
		c2cTransferMap.put("expectedMessage",MessagesDAO.prepareMessageByKey("channeltochannel.transfer.msg.success", transactionID));
		c2cTransferMap.put("actualMessage", message);
		return c2cTransferMap;

	}

	
	
}
