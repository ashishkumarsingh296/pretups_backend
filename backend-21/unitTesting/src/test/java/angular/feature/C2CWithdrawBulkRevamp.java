package angular.feature;

import angular.classes.LoginRevamp;
import angular.pageobjects.c2ctransfer.C2CTransfers;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class C2CWithdrawBulkRevamp extends BaseTest {
	static String PathOfFile = _masterVO.getProperty("C2CBulkWithdrawPath") ;

	public WebDriver driver;
	LoginRevamp login;
	C2CTransfers transfers;


	public C2CWithdrawBulkRevamp(WebDriver driver) {
		this.driver = driver;
		login = new LoginRevamp();
		transfers = new C2CTransfers(driver);
	}


	public void performC2CBulkWithdrawal(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkWithdrawal";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = null, transferStatus = null, trf_status = null;

		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);
		String latestFileName ;
		String FilePathAndName ;
		String arr[][] =null;
		

		ExcelUtility.deleteFiles(PathOfFile) ;
		for(int j=0;j<5;j++) {
			transfers.clickDownloadUserListIcon();
			int NoOfFilesAfter = transfers.noOfFilesInDownloadedDirectory(PathOfFile) ;
			int counter_msisdn = 0 ;
			int counter_login=0;
			
			if(NoOfFilesAfter == 1)
			{
				latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;
				FilePathAndName = PathOfFile + latestFileName ;
				ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;

				int noOfRows = ((ExcelUtility.getRowCount())/2) ;

				int availableBalance = 0 ;

				arr = new String[noOfRows][5] ;
				for(int i=1; i<noOfRows; i++)
				{
					counter_msisdn = 0 ;
					counter_login=0;
					arr[i][0] = ExcelUtility.getCellDataHSSF(i,0) ;
					if(arr[i][0] != null && !((arr[i][0]).equals(""))){counter_msisdn+=1 ;}
					arr[i][1] = ExcelUtility.getCellDataHSSF(i,1) ;
					if(arr[i][1] != null && !((arr[i][1]).equals(""))){counter_login+=1 ;}
					arr[i][2] = ExcelUtility.getCellDataHSSF(i,3) ;
					if(arr[i][2] != null && !((arr[i][2]).equals(""))){counter_login+=1 ;}
					availableBalance = Integer.valueOf(ExcelUtility.getCellDataHSSF(i,4)) ;
					arr[i][3]= transfers.enterC2CQty(availableBalance);
					if(availableBalance > 100) {counter_msisdn+=1; counter_login+=1 ;}
					arr[i][4] = ExcelUtility.getCellDataHSSF(i,6) ;
					if(arr[i][4] != null && !((arr[i][4]).equals(""))){counter_msisdn+=1; counter_login+=1 ;}
					if(counter_msisdn == 3 || counter_login==4)
					{
						Log.info("\n\n COUNTER_msisdn = "+counter_msisdn) ;
						Log.info("\n\n COUNTER_login = "+counter_login) ;
						arr[0][0] = arr[i][0] ;
						arr[0][1] = arr[i][1] ;
						arr[0][2] = arr[i][2] ;
						arr[0][3] = arr[i][3] ;
						arr[0][4] = arr[i][4] ;
						Log.info(" UNEMPTY STORED IN ARRAY TO wRITE IN EXCEL");
						Log.info("arr[0][0] "+arr[0][0]) ;
						Log.info("arr[0][1] "+arr[0][1]) ;
						Log.info("arr[0][2] "+arr[0][2]) ;
						Log.info("arr[0][3] "+arr[0][3]) ;
						Log.info("arr[0][4] "+arr[0][4]) ;
						Log.info("User available balance :  "+availableBalance) ;
						break ;
					}
				
				}
			break;
			}
		}

		ExcelUtility.deleteFiles(PathOfFile) ;
		transfers.clickDownloadBlankUserTemplateIcon();

		latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

		String filePath = transfers.getLatestFilefromDir(latestFileName);
		Log.info("File Path:" + filePath);
		Log.info("Writing to excel ....");

		FilePathAndName = PathOfFile + latestFileName ;
		Log.info(" PATHNAME : "+FilePathAndName) ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;

		for(int j=0; j<=4; j++)
		{
			ExcelUtility.setCellDataXLS(arr[0][j],1, j) ;
		}
		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));

		String uploadPath = System.getProperty("user.dir") + _masterVO.getProperty("C2CBulkWithdrawUpload") + latestFileName ;
		transfers.uploadFile(uploadPath);
		transfers.clickSubmitButtonC2CBulkTransfer();
		transfers.enterPin(PIN);
		transfers.clickPINConfirmButton() ;

		String expectedmessage = "Bulk Withdrawal Request Initiated";

		String actualMessage = null;
		boolean successPopUP = transfers.successPopUPVisibility();
		if (successPopUP == true) {
			actualMessage = transfers.actualMessage();

			if (actualMessage.contains(expectedmessage)) {
				ExtentI.Markup(ExtentColor.GREEN, "C2C Bulk Withdraw Transaction message Found as: " + actualMessage);
				transfers.printC2CSuccessBatchID() ;
				ExtentI.attachCatalinaLogsForSuccess();
			} else {
				String errorMEssageForFailure = transfers.getErrorMessageForFailure() ;
				ExtentI.Markup(ExtentColor.RED, "C2C Bulk Withdraw Failed Reason: " + errorMEssageForFailure);
				currentNode.log(Status.FAIL, "C2C Bulk Withdraw Transaction is not successful. Transfer message on WEB: " + actualMessage);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
			transfers.clickDoneButton();
		} else {
			String errorMEssageForFailure = transfers.getErrorMessageForFailure() ;
			ExtentI.Markup(ExtentColor.RED, "C2C Bulk Withdraw Fsiled : " + errorMEssageForFailure);
			currentNode.log(Status.FAIL, "C2C Bulk Withdraw Transaction is not successful. Transfer message on WEB: " + actualMessage);
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname) ;
	}




	public void performC2CBulkWithdrawalBlankCategory(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkWithdrawalBlankCategory";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));
		transfers.clickSubmitButtonC2CBulkTransfer();
		String errorMessageCaptured = transfers.blankCategoryMessages();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "User Category is required.";
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Category Error not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}




	public void performC2CBulkWithdrawalBlankBatchName(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkWithdrawalBlankBatchName";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);
		transfers.clickSubmitButtonC2CBulkTransfer();
		String errorMessageCaptured = transfers.batchNameErrorMessages();
		String expectedMessage = "Batch Name is required.";
		boolean flag = false;
		if (expectedMessage.equals(errorMessageCaptured)) {
			flag = true;
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			currentNode.log(Status.FAIL, "Blank Batch Name not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname);
	}



	public void performC2CBulkWithdrawalWithoutUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkWithdrawalWithoutUpload";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);

		//transfers.clickDownloadUserListIcon();
		//transfers.clickDownloadBlankUserTemplateIcon();
		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));
		transfers.clickSubmitButtonC2CBulkTransfer() ;
		List<WebElement> errorMessageCaptured = transfers.validationErrorsOnGUI() ;
		String actualMessage = null;
		String expectedMessage = "File is required" ;
		boolean flag = false;
		for (WebElement ele : errorMessageCaptured) {
			actualMessage = ele.getText();
			if (expectedMessage.equals(actualMessage)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			Assertion.assertContainsEquals(actualMessage, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			currentNode.log(Status.FAIL, "C2C is successful without uploading file");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}

	public void performC2CBulkWithdrawalResetButton(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {

		final String methodname = "performC2CBulkWithdrawalResetButton";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);

		transfers.clickDownloadUserListIcon();
		transfers.clickDownloadBlankUserTemplateIcon();
		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));
		transfers.clickResetButton();
		transfers.clickSubmitButtonC2CBulkTransfer();

		Boolean blankCategory = transfers.getblankCategory();
		String blankBatchName = transfers.getblankBatchName();

		Boolean checkBatchName = blankBatchName.equals("");
		if(!blankCategory&&checkBatchName)
		{
			ExtentI.Markup(ExtentColor.GREEN, "All fields are blank hence Reset button click successful");
			ExtentI.attachCatalinaLogsForSuccess();
		}
		else{
			currentNode.log(Status.FAIL, "Fields are not blank hence Reset button failed.");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}


	public void performC2CBulkWithdrawalDownloadUserListWithoutCategory(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkWithdrawalDownloadUserListWithoutCategory";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.clickDownloadUserListIcon() ;
		List<WebElement> errorMessageCaptured = transfers.validationErrorsOnGUI() ;
		String actualMessage = null;
		String expectedMessage =  "User Category is required." ;
		boolean flag = false;
		for (WebElement ele : errorMessageCaptured) {
			actualMessage = ele.getText();
			if (expectedMessage.equals(actualMessage)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			Assertion.assertContainsEquals(actualMessage, expectedMessage);
			ExtentI.Markup(ExtentColor.GREEN, "User List Downloaded Failed Without Category");
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			ExtentI.Markup(ExtentColor.GREEN, "User List Downloaded Without Category");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}

	public void performC2CBulkWithdrawalDownloadTemplateWithoutCategory(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkWithdrawalDownloadTemplateWithoutCategory";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		String downloadDirPath = _masterVO.getProperty("C2CBulkWithdrawPath") ;
		int NoOfFilesBefore = transfers.noOfFilesInDownloadedDirectory(downloadDirPath) ;

		transfers.clickDownloadBlankUserTemplateIcon();

		int NoOfFilesAfter = transfers.noOfFilesInDownloadedDirectory(downloadDirPath) ;

		Log.info("No Of Files Before Download = "+NoOfFilesBefore) ;
		Log.info("No Of Files After Download = "+NoOfFilesAfter) ;

		if(NoOfFilesBefore < NoOfFilesAfter)
		{
			ExtentI.Markup(ExtentColor.GREEN,"Template File Downloaded Successfully") ;
			ExtentI.attachCatalinaLogsForSuccess();
		}
		else
		{
			ExtentI.Markup(ExtentColor.RED,"Download Template File Failed") ;
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
}

	public void performC2CBulkWithdrawalFileUploadType(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) throws InterruptedException {
		final String methodname = "C2CBulkWithdrawalFileUploadType";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		String PNGPath = System.getProperty("user.dir")+_masterVO.getProperty("PNGFile");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);
		transfers.enterBatchName(RandomGeneration.randomNumeric(10));
		transfers.uploadFile(PNGPath);

		String errorMessageCaptured = transfers.fileUploadTypeErrorMessage();
		String expectedMessage = "Only CSV,XLS & XLSX Files are allowed";
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			currentNode.log(Status.FAIL, "File Uploaded is not of type CSV,XLS or XLSX");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}



	public void performC2CBulkWithdrawalBlankPIN(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkWithdrawBlankPIN";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();

		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);
		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


		ExcelUtility.createBlankExcelFile(PathOfFile);

		String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;
		String uploadFileName = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkWithdrawUpload") + latestFileName ;
		transfers.uploadFile(uploadFileName);
		transfers.clickSubmitButtonC2CBulkTransfer();
		transfers.enterC2CPIN("") ;
		//Boolean confirmButtonVisible = transfers.clickPINSubmitButton() ;
		Boolean confirmButtonDisabled = transfers.clickPINSubmitButton() ;
		if(confirmButtonDisabled)
		{
			ExtentI.Markup(ExtentColor.GREEN, "Confirm PIN button is disabled for blank pin");
			ExtentI.attachCatalinaLogsForSuccess();
		}
		else{
			ExtentI.Markup(ExtentColor.RED, "C2C PIN Confirm Button is not disabled successfully with blank pin");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}

	public void performC2CBulkWithdrawalBlankFileUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkWithdrawBlankPIN";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();

		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);
		transfers.clickDownloadUserListIcon();
		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


		int noOfFiles = transfers.noOfFilesInDownloadedDirectory(PathOfFile) ;

		if(noOfFiles > 0)
		{
			//	Log.info("\n\n\n\nFILES IN DIR = " + noOfFiles) ;
			ExcelUtility.deleteFiles(PathOfFile) ;
		}
		ExcelUtility.createBlankExcelFile(PathOfFile+"C2SBulkWithdraw.xls") ;
		Log.info("User template download Failed, Created an Empty Excel File") ;

		String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkWithdrawUpload") + latestFileName ;
		//System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
		transfers.uploadFile(uploadPath);

		transfers.clickSubmitButtonC2CBulkTransfer();

		transfers.enterC2CPIN(PIN) ;
		transfers.clickPINSubmitButton() ;
		String C2CFailMessagePopup = transfers.checkC2CFailMessage() ;
		Log.info("C2CFailMessagePopup : " + C2CFailMessagePopup);

		if (C2CFailMessagePopup.toUpperCase().contains("FAIL")) {
			String reasonForFail = transfers.getErrorMessageForFailure()  ;
			String actualMessage = "The number of errors in the file exceed the maximum number allowed" ;
			if(reasonForFail.equals(actualMessage))
			{
				Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.GREEN, "C2C with Blank Excel Failed for reason : "+reasonForFail);
				ExtentI.attachCatalinaLogsForSuccess();
			}
			else
			{
				currentNode.log(Status.FAIL, "Invalid message given.");
				ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}

		}
		else {
			currentNode.log(Status.FAIL, "C2C IS Successful with Blank Excel.");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname) ;
	}


	public void performC2CBulkWithdrawalInvalidMSISDN(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkWithdrawalInvalidMSISDN";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		String transferID = "CT", actualMessage = null, trf_status = null;
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transaction heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);

		transfers.clickDownloadUserListIcon();
		ExcelUtility.deleteFiles(PathOfFile) ;
		transfers.clickDownloadBlankUserTemplateIcon();


		String filePath = transfers.getLatestFilefromDir(PathOfFile);
		Log.info("File Path:" + filePath);

		String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;
		String FilePathAndName = PathOfFile + latestFileName ;

		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));
		String MSISDN2 = RandomGeneration.randomNumeric(12);
		Log.info("Writing to excel ....");
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1);
		ExcelUtility.setCellDataXLS(MSISDN2,1, 0);
		ExcelUtility.setCellDataXLS("03",1,3);
		ExcelUtility.setCellDataXLS("ETOPUP",1,4);
		Log.info("Written to Excel : " + MSISDN2 + ", " + "03, ETOPUP");


		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkWithdrawUpload") + latestFileName ;
		transfers.uploadFile(uploadPath);
		transfers.clickSubmitButtonC2CBulkTransfer();
		transfers.enterPin(PIN);
		transfers.clickPINConfirmButton() ;


		String failReason = "No such user exists, MSISDN is invalid.";


		boolean successPopUP = transfers.successPopUPVisibility();
		if (successPopUP == true) {
			actualMessage = transfers.C2CFailReason();

			if (actualMessage.contains(failReason)) {
				ExtentI.Markup(ExtentColor.GREEN, "C2C Bulk Withdrawal Transaction fails due to invalid MSISDN, error message Found as: " + actualMessage);
				ExtentI.attachCatalinaLogsForSuccess();
			} else {
				currentNode.log(Status.FAIL, "Error message due to invalid MSISDN not shown. Transfer message on WEB: " + actualMessage);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}

		} else {
			currentNode.log(Status.FAIL, "Error message due to invalid MSISDN not shown. Transfer message on WEB: " + actualMessage);
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname) ;
	}




	public void performC2CBulkWithdrawalBlankDataFileWithHeaderUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkWithdrawBlankDataFileWithHeaderUpload";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();

		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);

		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


		int noOfFiles = transfers.noOfFilesInDownloadedDirectory(PathOfFile) ;

		if(noOfFiles > 0)
		{
			//	Log.info("\n\n\n\nFILES IN DIR = " + noOfFiles) ;
			ExcelUtility.deleteFiles(PathOfFile) ;
		}
		transfers.clickDownloadBlankUserTemplateIcon() ;

		String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkWithdrawUpload") + latestFileName ;
		//System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
		transfers.uploadFile(uploadPath);

		transfers.clickSubmitButtonC2CBulkTransfer();

		transfers.enterC2CPIN(PIN) ;
		transfers.clickPINSubmitButton() ;
		String C2CFailMessagePopup = transfers.checkC2CFailMessage() ;
		Log.info("C2CFailMessagePopup : " + C2CFailMessagePopup);
		String actualMessage = "No record available." ;
		if (C2CFailMessagePopup.toUpperCase().contains("FAIL")) {
			String reasonForFail = transfers.getErrorMessageForFailure() ;
			if(reasonForFail.equals(actualMessage))
			{
				Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.GREEN, "C2C Failed with Blank data, error on GUI : "+reasonForFail);
				ExtentI.attachCatalinaLogsForSuccess();
			}
			else
			{
				Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
		}
		else {
			currentNode.log(Status.FAIL, "C2C IS Successful with Blank Data.");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname) ;
	}



	public void performC2CBulkWithdrawalInvalidHeaderFileUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkWithdrawInvaliDataFileUpload";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();

		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);
		transfers.clickDownloadUserListIcon();
		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


		int noOfFiles = transfers.noOfFilesInDownloadedDirectory(PathOfFile) ;

		if(noOfFiles > 0)
		{
			//	Log.info("\n\n\n\nFILES IN DIR = " + noOfFiles) ;
			ExcelUtility.deleteFiles(PathOfFile) ;
		}

		transfers.clickDownloadBlankUserTemplateIcon() ;

		String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

		String FilePathAndName = PathOfFile + latestFileName ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;
		for(int j = 0; j<ExcelUtility.getColumnCount() ; j++)
		{
			ExcelUtility.setCellDataXLS("Mobile", 0, j) ;
		}

		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkWithdrawUpload") + latestFileName ;
//		System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
		transfers.uploadFile(uploadPath);

		transfers.clickSubmitButtonC2CBulkTransfer();

		transfers.enterC2CPIN(PIN) ;
		transfers.clickPINSubmitButton() ;
		String C2CFailMessagePopup = transfers.checkC2CFailMessage() ;
		Log.info("C2CFailMessagePopup : " + C2CFailMessagePopup);

		String actualMessage = "Invalid column header given." ;
		if (C2CFailMessagePopup.toUpperCase().contains("FAIL")) {
			String reasonForFail = transfers.getErrorMessageForFailure() ;
			if(reasonForFail.equals(actualMessage))
			{
				Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.GREEN, "C2C Failed with Invalid header, error on GUI : "+reasonForFail);
				ExtentI.attachCatalinaLogsForSuccess();
			}
			else
			{
				Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}
		}
		else {
			currentNode.log(Status.FAIL, "C2C IS Successful with Invalid header.");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname) ;
	}




	public void performC2CBulkWithdrawalInvalidDataFileUpload(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "performC2CBulkWithdrawInvaliDataFileUpload";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();

		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transfer heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(ToCategory);
	//	transfers.clickDownloadUserListIcon();
		transfers.enterBatchName("AUTBN" + RandomGeneration.randomNumeric(5));


		int noOfFiles = transfers.noOfFilesInDownloadedDirectory(PathOfFile) ;

		if(noOfFiles > 0)
		{
			//	Log.info("\n\n\n\nFILES IN DIR = " + noOfFiles) ;
			ExcelUtility.deleteFiles(PathOfFile) ;
		}

		transfers.clickDownloadBlankUserTemplateIcon() ;

		String latestFileName = transfers.getLatestFileNamefromDir(PathOfFile) ;

		String FilePathAndName = PathOfFile + latestFileName ;
		ExcelUtility.setExcelFileXLS(FilePathAndName, ExcelI.SHEET1) ;
		for(int i = 1; i<=2; i++)
		{
			for(int j=0; j < ExcelUtility.getColumnCount() ; j++ )
			{
				ExcelUtility.setCellDataXLS("Mobile", i, j) ;
			}
		}

		String uploadPath = System.getProperty("user.dir")+_masterVO.getProperty("C2CBulkWithdrawUpload") + latestFileName ;
		//	System.out.println("\n\n\n\n UPLOAD PATH --- "+uploadPath) ;
		transfers.uploadFile(uploadPath);

		transfers.clickSubmitButtonC2CBulkTransfer();

		transfers.enterC2CPIN(PIN) ;
		transfers.clickPINSubmitButton() ;
		String C2CFailMessagePopup = transfers.checkC2CFailMessage() ;
		Log.info("C2CFailMessagePopup : " + C2CFailMessagePopup);
		if (C2CFailMessagePopup.toUpperCase().contains("FAIL")) {
			String reasonForFail = transfers.getErrorMessageForFailure()  ;
			String actualMessage = "ALL records contain error.Kindly check logs." ;
			if(reasonForFail.equals(actualMessage))
			{
				Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.GREEN, "C2C with Invalid Data Excel Failed for reason : "+reasonForFail);
				ExtentI.attachCatalinaLogsForSuccess();
			}
			else
			{
                Assertion.assertContainsEquals(reasonForFail, actualMessage);
				ExtentI.Markup(ExtentColor.RED, "Invalid message displayed on GUI : "+reasonForFail);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}

		}
		else {
			currentNode.log(Status.FAIL, "C2C IS Successful with Invalid Data Excel.");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}

		Log.methodExit(methodname) ;
	}




/*public void performC2CBulkWithdrawalSymbolBatchName(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkTransferSymbolBatchName";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		RandomGeneration RandomGeneration = new RandomGeneration();
		if(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transaction heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		transfers.selectCategory(FromCategory);
		transfers.clickDownloadUserListIcon();
		transfers.isFileDownloaded();
		transfers.clickDownloadBlankUserTemplateIcon();
		transfers.enterBatchName("%$#@^&");
		transfers.clickSubmitButtonC2CBulkTransfer();
		String errorMessageCaptured = transfers.batchNameErrorMessages();
		String expectedMessage = "Batch Name is invalid";
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		} else {
			currentNode.log(Status.FAIL, "Batch name entered is Invalid or Symbolic");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;
	}*/





	/*public void performC2CBulkWithdrawalBlankProduct(String FromCategory, String ToCategory, String toMSISDN, String PIN, String catCode) {
		final String methodname = "C2CBulkTransferBlankProduct";
		Log.methodEntry(methodname, FromCategory, ToCategory, toMSISDN, PIN);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver, "ChannelUser", FromCategory);
		tif(!transfers.isC2CVisible()) {
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Transaction heading is clicked");
		}
		else {
			transfers.clickC2CHeading();
			transfers.clickC2CTransactionHeading();
			Log.info("C2C Heading and Transaction Heading is clicked");
		}
		transfers.clickC2CBulkOperationHeading();
		transfers.clickBulkWithdrawalHeading();
		String errorMessageCaptured = transfers.blankCategoryMessages();
		Log.info("errorMessageCaptured : " + errorMessageCaptured);
		String expectedMessage = "Product is required.";
		if (expectedMessage.equals(errorMessageCaptured)) {
			Assertion.assertContainsEquals(errorMessageCaptured, expectedMessage);
			ExtentI.attachCatalinaLogsForSuccess();
		}
		else {
			currentNode.log(Status.FAIL, "Blank Category Error not displayed on GUI");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		Log.methodExit(methodname) ;

	}

*/

}





