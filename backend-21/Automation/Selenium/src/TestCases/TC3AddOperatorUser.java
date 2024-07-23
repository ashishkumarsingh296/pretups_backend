package testcases;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common.util.ClickButton;
import common.util.ClickLink;
import common.util.FillTextBox;
import common.util.LaunchDriver;
import common.util.ReadExcelFile;
import common.util.SelectFromDropDown;
import common.util.SelectRadioButton;
import common.util.SwitchWindow;
/**
 * Creating Network Admin
 */
public class TC3AddOperatorUser {

	private static Log _log = LogFactory.getFactory().getInstance(
			TC1LoginSuperadmin.class.getName());

	/**
	 * <h1>Reads Data from Sheet for Network Admin Creation</h1>
	 * 
	 * @return
	 */
	@DataProvider(name = "NetworkAdmin")
	public static String[][] excelRead() throws Exception {

		return ReadExcelFile.excelRead("NetworkAdmin.xlsx", "Network_Operator");
	}

	/**
	 * <h1>Test Case for adding Network Admin</h1>
	 * 
	 * @return
	 */
	@Test(dataProvider = "NetworkAdmin")
	public static void add_Network_Admin(String... lst) {

		// HashMap<String, String> cacheMap = LoadPropertiesFile.getCachemap();
		ClickLink.byText("Operator users");

		if (_log.isDebugEnabled()) {
			_log.debug("Clicked Operator Users Menu");
		}

		ClickLink.byText("Add operator user");

		if (_log.isDebugEnabled()) {
			_log.debug("Clicked Add Operator User Menu");
		}

		SelectFromDropDown.selectByName("categoryCode", lst[1]);
		ClickButton.clickByName("add");

		if (_log.isDebugEnabled()) {
			_log.debug("Clicked Add Button");
		}

		FillTextBox.addTextByName("firstName", lst[2]);
		FillTextBox.addTextByName("lastName", lst[3]);
		FillTextBox.addTextByName("shortName", lst[4]);
		SelectFromDropDown.selectByName("userNamePrefixCode", lst[5]);
		FillTextBox.addTextByName("externalCode", lst[6]);
		FillTextBox.addTextByName("empCode", lst[7]);
		FillTextBox.addTextByName("msisdn", lst[8]);
		FillTextBox.addTextByName("ssn", lst[9]);
		FillTextBox.addTextByName("contactNo", lst[10]);
		FillTextBox.addTextByName("designation", lst[12]);
		SelectFromDropDown.selectByName("divisionCode", lst[13]);
		SelectFromDropDown.selectByName("departmentCode", lst[14]);
		FillTextBox.addTextByName("address1", lst[15]);
		FillTextBox.addTextByName("address2", lst[16]);
		FillTextBox.addTextByName("city", lst[17]);
		FillTextBox.addTextByName("state", lst[18]);
		FillTextBox.addTextByName("country", lst[19]);
		FillTextBox.addTextByName("email", lst[20]);
		FillTextBox.addTextByName("appointmentDate", lst[21]);
		FillTextBox.addTextByName("allowedIPs", lst[22]);

		if (_log.isDebugEnabled()) {
			_log.debug("Filled all the details");
		}
		// WebDriverWait wait = new WebDriverWait(Launchdriver.driver, 6000);
		String currentWindowHandle = LaunchDriver.driver.getWindowHandle();
		ClickLink.byText("Assign roles");
		if (_log.isDebugEnabled()) {
			_log.debug("Clicked on Assign Roles link");
		}

		try {
			Thread.sleep(7000);
		} catch (InterruptedException e1) {
			_log.error("Exception:" + e1);
		}
		SwitchWindow.windowHandle();
		SelectRadioButton.selectButtonByName("checkall");
		ClickButton.clickByName("addRoles");

		/*
		 * try { Thread.sleep(3000); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */

		LaunchDriver.driver.switchTo().window(currentWindowHandle);
		ClickLink.byText("Assign geographies");

		// SelectRadioButton.selecbutton(lst[27]);
		SwitchWindow.windowHandle();
		SelectRadioButton.selectButtonByValue("NG");
		ClickButton.clickByName("addGeography");

		SwitchWindow.windowHandleClose();
		// Time_Wait.timewait(6000);

		// SelectCheckBox.selectByValue("allowedDays", lst[23]);
		// FillTextBox.addTextByName("allowedFromTime", lst[24]);
		// FillTextBox.addTextByName("allowedToTime", lst[25]);

	}

}
