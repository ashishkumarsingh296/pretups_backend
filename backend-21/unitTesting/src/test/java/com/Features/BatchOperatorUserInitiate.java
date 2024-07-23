package com.Features;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.entity.OperatorUserVO;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.superadminpages.addoperatoruser.BatchOperatorUserPage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.NewExcelUtility;
import com.utils.SwitchWindow;
import com.utils._masterVO;

public class BatchOperatorUserInitiate {

	WebDriver driver =null;
	BatchOperatorUserPage batchOptUsrPage;
	Login login;
	SuperAdminHomePage superhomepage;
	SelectNetworkPage selectnetwork;

	public String[] allColumns= new String[]{
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.usernameprefix"), //UserNamePrefix
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.firstname"), //FirstName
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.lastname"), //LastName
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.loginid"), //WEBLOGINID
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.password"), //WEBPASSWORD
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.mobilenumber"), //MOBILENUMBER
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.subscribercode"), //SUBSCRIBERCODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.status"), //STATUSCODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.division"), //DIVISIONCODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.department"), //DEPTCODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.geographicaldomain"), //GEODOMAINCODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.networkcode"), //NETWORKCODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.products"), //PRODUCTCODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.roletype"), //ROLETYPE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.roles"), //ROLECODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.domain"), //DOMAINCODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.designation"), //DESIGNATION
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.externalcode"), //EXTCODE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.contactnumber"), //CONTACTNO
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.ssn"), //SSN
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.address1"), //ADDRESS1
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.address2"), //ADDRESS2
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.city"), //CITY
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.state"), //STATE
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.country"), //COUNTRY
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.xlsfile.details.email"), //EMAIL
			MessagesDAO.getLabelByKey("user.initiatebatchoperatoruser.mastersheet.vouchertype"), //VOUCHERTYPE
			};

	
	
	public BatchOperatorUserInitiate(WebDriver driver){
		this.driver = driver;
		batchOptUsrPage=new BatchOperatorUserPage(driver);
		login=new Login();
		superhomepage=new SuperAdminHomePage(driver);
		selectnetwork=new SelectNetworkPage(driver);
	}
	
	public void loginwithoptusr(){
		login.LoginAsUser(driver, _masterVO.getMasterValue(MasterI.SUPERADMIN_LOGINID), _masterVO.getMasterValue(MasterI.SUPERADMIN_PASSWORD));
		selectnetwork.selectNetwork();
	}
	public String downloadBatchfile(String Category){
		superhomepage.clickOperatorUsers();
		batchOptUsrPage.clickbatchoptinitiatelink();
		batchOptUsrPage.selectCategory(Category);
		batchOptUsrPage.clicktodownloadtemplate();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String filepath=String.valueOf(CommonUtils.lastFileModified(System.getProperty("user.dir")+"\\Output\\BatchFiles\\OperatorInitiate"));
		
		String regexe="[0-9A-Za-z_]*.xls$";
		Pattern pattern = Pattern.compile(regexe);
		Matcher matcher = pattern.matcher(filepath);
		String filename = null;
		while(matcher.find()){
		filename=String.valueOf(matcher.group());
		}
		Log.info("Last downloaded file:"+filename);
		return filepath;
	}
	
	public Object[] uploadfile(String path,String name){
		batchOptUsrPage.choosefiletoupload(path);
		batchOptUsrPage.enterbatchName(name);
		batchOptUsrPage.clickSubmitBtn();
		batchOptUsrPage.clickConfirmBtn();
		boolean exist=batchOptUsrPage.checkiferroroccured();
		String msgInfo = "";
		Object[] data;
		if(exist){
		batchOptUsrPage.clickViewErrorlogs();
		try {
			SwitchWindow.switchwindow(driver);
			ExtentI.attachScreenShot();
			msgInfo = getErrorInfo();
			SwitchWindow.backwindow(driver);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		else{msgInfo = new AddChannelUserDetailsPage(driver).getActualMessage();}
		data = new Object[]{exist,msgInfo};
		return data;
		
	}
	
	public void filldetailsinfile(String path, int usersCountPerCategory, int excelRowIndex, ArrayList<OperatorUserVO> OperatorVO) {
		NewExcelUtility.setExcelFile(path, "Template Sheet");
		if (DBHandler.AccessHandler.getSystemPreference(
				CONSTANT.USER_VOUCHERTYPE_ALLOWED).equalsIgnoreCase("false")) {
			allColumns = ArrayUtils.remove(allColumns, 26);
		}

		Object[] requiredFileData;
		for (OperatorUserVO optData : OperatorVO) {
			requiredFileData = setDataInFile(optData);
			for (int i = 0; i < allColumns.length; i++) {
				NewExcelUtility.setCellData(4, String.valueOf(allColumns[i]), excelRowIndex, String.valueOf(requiredFileData[i]));
			}
			excelRowIndex++;
		}
	}

	public Object[] setDataInFile(OperatorUserVO operatorVO) {
		Object[] data = new Object[] { operatorVO.getUserNamePrefix(),
				operatorVO.getFirstName(), operatorVO.getLastName(),
				operatorVO.getLOGINID(), operatorVO.getPASSWORD(),
				operatorVO.getMSISDN(), operatorVO.getSubscriberCode(),
				operatorVO.getStatusCode(), operatorVO.getDivisionCode(),
				operatorVO.getDeptCode(), operatorVO.getGeodomaincode(),
				operatorVO.getNetworkcode(), operatorVO.getProductType(),
				operatorVO.getRoleType(), operatorVO.getRoleCode(),
				operatorVO.getDomainCode(), operatorVO.getDesignation(),
				operatorVO.getEXTCODE(), operatorVO.getContactNo(),
				operatorVO.getSSN(), operatorVO.getAddress1(),
				operatorVO.getAddress2(), operatorVO.getCity(),
				operatorVO.getState(), operatorVO.getCountry(),
				operatorVO.getEmail(), operatorVO.getVoucherType() };
		return data;
	}
	
	public void writeOperatorUserData(int RowNum,String sheetname,OperatorUserVO optdata){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, sheetname);
		
		ExcelUtility.setCellData(0, ExcelI.LOGIN_ID, RowNum, optdata.getLOGINID());
		ExcelUtility.setCellData(0, ExcelI.PASSWORD, RowNum, optdata.getPASSWORD());
		ExcelUtility.setCellData(0, ExcelI.MSISDN, RowNum,optdata.getMSISDN());
	}
	
	public String getErrorInfo(){
		return driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]")).getText();
	}

}
