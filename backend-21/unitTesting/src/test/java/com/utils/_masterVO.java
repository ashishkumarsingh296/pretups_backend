package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.UserAccess;
import com.classes.UserAccessRevamp;
import com.commons.CacheController;
import com.commons.ExcelI;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.ServicesControllerI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.poiji.internal.Poiji;
import com.sshmanager.SSHService;

public class _masterVO {

	private static Map<String, String> PropertiesMap;
	private static Map<String, String> ClientLibrary;
	private static Map<String, String> MessagesMap;
	private static Map<String, String> C2SMessagesMap;
	private static HashMap<String, String> MasterSheetMap;
	public static HashMap<String, String[]> geoTypeMap;
	public static Object[][] gatewayObject;
	static String sheetName;
	static String MasterSheetPath;
	public static Map<String, String> p2pServicesMap;
	public static Map<String, String> c2sServicesMap;
	public static HashMap<String, CaseMaster> GatewayMaster = new HashMap<String, CaseMaster>();
	private static Map<String, String> TestCaseProp;

	private static ArrayList<String> EXTGWServicesList;
	private static ArrayList<String> USSDServicesList;

	private static String actionID = null;

	public static void loadVO() throws IOException {
		loadProperties();
		loadMasterSheet();
		loadClientLibrary();
		loadGeoDomains();
		generateUserAccessObject();
		generateUserAccessObjectRevamp();
		loadC2SServices();
		loadP2PServices();
		loadTestCaseFile();
		loadWebTestCases();

		// Creating Database Connection Pool for execution
		DBHandler DBDAO = new DBHandler();
		DBDAO.getDatabaseDAO();
		// Database Connection Pool ends

		CacheController CacheI = new CacheController();
		CacheI.getCacheDAO();

		loadPreferences();
		SSHService.getApplicationFiles();
		// Loading Messages file
		loadMessages();
		loadC2SMessages();
	//	loadExternalServices();
		SSHService.loadApplicationLogsPath();
	}

	public static void loadProperties() {
		Log.info("Loading Properties file from: .//src//test//resources//sysconfig//Constant.properties");
		Properties prop = new Properties();
		PropertiesMap = new HashMap<String, String>();
		try {
			FileInputStream inputStream = new FileInputStream(
					".//src//test//resources//sysconfig//Constant.properties");
			prop.load(inputStream);
		} catch (Exception e) {
			Log.info("Error while loading Properties File: ");
			Log.writeStackTrace(e);
		}
		for (final Entry<Object, Object> entry : prop.entrySet()) {
			PropertiesMap.put((String) entry.getKey(), (String) entry.getValue());
		}
		Log.info("Properties File Loaded successfully");
	}

	public static void loadGatewayCases(String Gateway) {
		Log.info("Loading Gateway Cases Master file from: .//src//test//resources//config//GatewayMaster.xlsx");

		if (Gateway.equalsIgnoreCase(GatewayI.EXTGW)) {
			ExcelUtility.setExcelFile(".//src//test//resources//config//GatewayMaster.xlsx", "EXTGW");
			int rowCount = ExcelUtility.getRowCount();
			for (int i = 1; i <= rowCount; i++) {
				CaseMaster CaseMaster = new CaseMaster();
				CaseMaster.setDataRow(i);
				CaseMaster.setTestCaseCode(ExcelUtility.getCellData(0, "Test Case Code", i));
				CaseMaster.setModuleCode(ExcelUtility.getCellData(0, "Module Code", i));
				CaseMaster.setDescription(ExcelUtility.getCellData(0, "Test Case Description", i));
				CaseMaster.setErrorCode(ExcelUtility.getCellData(0, "Error Code", i));
				GatewayMaster.put(ExcelUtility.getCellData(i, 0), CaseMaster);
			}
		} else if (Gateway.equalsIgnoreCase(GatewayI.USSD)) {
			ExcelUtility.setExcelFile(".//src//test//resources//config//GatewayMaster.xlsx", "USSD");
			int rowCountUSSD = ExcelUtility.getRowCount();
			for (int i = 1; i <= rowCountUSSD; i++) {
				CaseMaster CaseMaster = new CaseMaster();
				CaseMaster.setDataRow(i);
				CaseMaster.setTestCaseCode(ExcelUtility.getCellData(0, "Test Case Code", i));
				CaseMaster.setModuleCode(ExcelUtility.getCellData(0, "Module Code", i));
				CaseMaster.setDescription(ExcelUtility.getCellData(0, "Test Case Description", i));
				CaseMaster.setErrorCode(ExcelUtility.getCellData(0, "Error Code", i));
				GatewayMaster.put(ExcelUtility.getCellData(i, 0), CaseMaster);
			}
		} else if (Gateway.equalsIgnoreCase(GatewayI.USSD_PLAIN)) {
			ExcelUtility.setExcelFile(".//src//test//resources//config//GatewayMaster.xlsx", "USSD_PLAIN");
			int rowCountUSSDPlain = ExcelUtility.getRowCount();
			for (int i = 1; i <= rowCountUSSDPlain; i++) {
				CaseMaster CaseMaster = new CaseMaster();
				CaseMaster.setDataRow(i);
				CaseMaster.setTestCaseCode(ExcelUtility.getCellData(0, "Test Case Code", i));
				CaseMaster.setModuleCode(ExcelUtility.getCellData(0, "Module Code", i));
				CaseMaster.setDescription(ExcelUtility.getCellData(0, "Test Case Description", i));
				CaseMaster.setErrorCode(ExcelUtility.getCellData(0, "Error Code", i));
				GatewayMaster.put(ExcelUtility.getCellData(i, 0), CaseMaster);
			}
		}

		_APIUtil.buildGatewayMasterFile();
		Log.info("Gateway Cases Master File Loaded successfully");
	}

	public static void loadClientLibrary() {
		Log.info(
				"Loading Client Library for Client Versioning from: .//src//test//resources//sysconfig//ClientLib.properties");
		Properties prop = new Properties();
		ClientLibrary = new HashMap<String, String>();
		try {
			FileInputStream inputStream = new FileInputStream(
					".//src//test//resources//sysconfig//ClientLib.properties");
			prop.load(inputStream);
		} catch (Exception e) {
			Log.info("Error while loading Client Library File: ");
			Log.writeStackTrace(e);
		}
		for (final Entry<Object, Object> entry : prop.entrySet()) {
			ClientLibrary.put((String) entry.getKey(), (String) entry.getValue());
		}
		Log.info("Client Library Loaded successfully");
	}

	public static void loadMessages() {
		String LANG = _masterVO.getMasterValue("Language");
		Log.info("Loading Messages Resource properties File from: .//src//test//resources//sysconfig//" + LANG
				+ ".properties");
		Properties prop = new Properties();
		MessagesMap = new HashMap<String, String>();
		try {
			FileInputStream inputStream = new FileInputStream(
					".//src//test//resources//sysconfig//" + LANG + ".properties");
			prop.load(inputStream);
		} catch (Exception e) {
			Log.info("Error while loading Messages Resource properties file: ");
			Log.writeStackTrace(e);
		}
		for (final Entry<Object, Object> entry : prop.entrySet()) {
			MessagesMap.put((String) entry.getKey().toString().trim(), (String) entry.getValue().toString().trim());
		}
		Log.info("Messages Resource Properties file Loaded successfully");
	}

	@SuppressWarnings({ "deprecation", "resource" })
	public static HashMap<String, String> loadMasterSheet() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		Log.info("Loading Master Sheet from: " + MasterSheetPath);
		MasterSheetMap = new HashMap<String, String>();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(MasterSheetPath);
			XSSFWorkbook workBook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workBook.getSheet(ExcelI.MASTER_SHEET_NAME);

			Iterator<?> rows = sheet.rowIterator();

			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				Iterator<?> cells = row.cellIterator();

				List<XSSFCell> data = new LinkedList<XSSFCell>();
				while (cells.hasNext()) {
					XSSFCell cell = (XSSFCell) cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					data.add(cell);

				}
				if (data.size() > 0)
					MasterSheetMap.put(data.get(0).toString(), data.get(1).toString());
			}

		} catch (IOException e) {
			Log.writeStackTrace(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.info("Master Sheet Loaded successfully");
		return MasterSheetMap;
	}

	public static void loadGeoDomains() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		geoTypeMap = new HashMap<String, String[]>();

		// Fetch values from "Geographical Domains" sheet
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		int countRow = ExcelUtility.getRowCount();
		System.out.println("Total Count for geodomain: " + countRow);
		for (int i = 1; i <= countRow; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
			String value1 = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i);
			String value2 = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			String name = ExcelUtility.getCellData(0, ExcelI.DOMAIN_TYPE_NAME, i);
			String rowNum = String.valueOf(i); // added on 05 September

			// Fetch values from "Geography Domain Types" sheet
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			int j = 0;
			while (j <= rowCount) {
				String GrpDomainName = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE_NAME, j);

				// Comparing geography name from both sheets
				if (GrpDomainName.equals(name)) {

					break;
				}
				j++;
			}
			String key = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, j);

			String Value[] = { value1, value2, rowNum };
			geoTypeMap.put(key, Value);
			Log.info("loadGeoDomains Map returns ::" + key + " - " + geoTypeMap.get(key)[0] + " - "
					+ geoTypeMap.get(key)[1]);
		}

	}

	public static void generateUserAccessObject() {
		try {
			List<UserAccess> employees = Poiji.fromExcel(new File(".//src//test//resources//config//LinksSheet.xlsx"),
					UserAccess.class);
			CONSTANT.USERACCESSDAO = new Object[employees.size()][6];
			for (int i = 0; i < employees.size(); i++) {
				UserAccess firstEmployee = employees.get(i);
				firstEmployee.CategoryCodes.toString();
				CONSTANT.USERACCESSDAO[i][0] = firstEmployee.MainLinks.toString();
				CONSTANT.USERACCESSDAO[i][1] = firstEmployee.SubLinks.toString();
				CONSTANT.USERACCESSDAO[i][2] = firstEmployee.PageCodes.toString();
				CONSTANT.USERACCESSDAO[i][3] = firstEmployee.RoleCodes.toString();
				CONSTANT.USERACCESSDAO[i][4] = firstEmployee.CategoryCodes.toString();
				CONSTANT.USERACCESSDAO[i][5] = firstEmployee.GroupRoleApplicable.toString();
			}
		} catch (Exception e) {
			Log.info("Error While generating User Access Object");
			Log.writeStackTrace(e);
		}
	}

	public static void generateUserAccessObjectRevamp() {
		try {
			List<UserAccessRevamp> employees = Poiji.fromExcel(
					new File(".//src//test//resources//config//LinksSheet_Revamp.xlsx"), UserAccessRevamp.class);
			CONSTANT.USERACCESSDAOREVAMP = new Object[employees.size()][6];
			for (int i = 0; i < employees.size(); i++) {
				UserAccessRevamp firstEmployee = employees.get(i);
				firstEmployee.CategoryCodes.toString();
				CONSTANT.USERACCESSDAOREVAMP[i][0] = firstEmployee.MainLinks.toString();
				CONSTANT.USERACCESSDAOREVAMP[i][1] = firstEmployee.SubLinks.toString();

				CONSTANT.USERACCESSDAOREVAMP[i][2] = firstEmployee.RoleCodes.toString();
				CONSTANT.USERACCESSDAOREVAMP[i][3] = firstEmployee.CategoryCodes.toString();
				CONSTANT.USERACCESSDAOREVAMP[i][4] = firstEmployee.EventCodes.toString();
				CONSTANT.USERACCESSDAOREVAMP[i][5] = firstEmployee.GroupRoleApplicable.toString();
			}
		} catch (Exception e) {
			Log.info("Error While generating User Access Object");
			Log.writeStackTrace(e);
		}
	}

	public static void loadC2SServices() {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		c2sServicesMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		c2sServicesMap = new HashMap<String, String>();
		for (int i = 0; i <= totalRow; i++)
			c2sServicesMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i),
					ExcelUtility.getCellData(0, ExcelI.NAME, i));
	}

	public static void loadP2PServices() {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		p2pServicesMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		p2pServicesMap = new HashMap<String, String>();
		for (int i = 0; i <= totalRow; i++)
			p2pServicesMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i),
					ExcelUtility.getCellData(0, ExcelI.NAME, i));
	}

	public static void loadPreferences() {

		CONSTANT.NetworkName = DBHandler.AccessHandler.getNetworkName(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		SystemPreferences.MULTIPLICATIONFACTOR = Integer
				.parseInt(DBHandler.AccessHandler.getSystemPreference("AMOUNT_MULT_FACTOR"));
		SystemPreferences.MSISDN_PREFIX_LENGTH = Integer
				.parseInt(DBHandler.AccessHandler.getSystemPreference("MSISDN_PREFIX_LENGTH"));
		SystemPreferences.MAX_MSISDN_LENGTH = Integer
				.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_MSISDN_LENGTH"));
		SystemPreferences.USER_PRODUCT_MULTIPLE_WALLET = Boolean
				.parseBoolean(DBHandler.AccessHandler.getSystemPreference("USER_PRODUCT_MULTIPLE_WALLET"));
		SystemPreferences.EXTERNAL_DATE_FORMAT = DBHandler.AccessHandler.getSystemPreference("EXTERNAL_DATE_FORMAT");
		SystemPreferences.DATE_FORMAT_CAL_JAVA = DBHandler.AccessHandler.getSystemPreference("DATE_FORMAT_CAL_JAVA");
		SystemPreferences.OTH_COM_ENABLED = ("true"
				.equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) ? true
						: false;
		String DWAllowedGateways = DBHandler.AccessHandler.getSystemPreference("DW_ALLOWED_GATEWAYS");
		SystemPreferences.DW_ALLOWED_GATEWAYS = (DWAllowedGateways != null) ? DWAllowedGateways : "";

		String localeLanguage = DBHandler.AccessHandler.getSystemPreference("DEFAULT_LANGUAGE");
		String localeCountry = DBHandler.AccessHandler.getSystemPreference("DEFAULT_COUNTRY");
		if (localeCountry != null && localeLanguage != null) {
			SystemPreferences.DISPLAY_LANGUAGE = localeLanguage;
			SystemPreferences.DISPLAY_COUNTRY = localeCountry;
			Log.info("Display Locale set to: " + SystemPreferences.DISPLAY_LANGUAGE + "_"
					+ SystemPreferences.DISPLAY_COUNTRY);
		}
	}

	public static void loadC2SMessages() {
		String LANG = _masterVO.getMasterValue(MasterI.LANGUAGE);
		Log.info("Loading Messages Resource properties File from: .//src//test//resources//sysconfig//C2S_" + LANG
				+ ".properties");
		Properties prop = new Properties();
		C2SMessagesMap = new HashMap<String, String>();
		try {
			FileInputStream inputStream = new FileInputStream(
					".//src//test//resources//sysconfig//C2S_" + LANG + ".properties");
			prop.load(inputStream);
		} catch (Exception e) {
			Log.info("Error while loading Messages Resource properties file: ");
			Log.writeStackTrace(e);
		}
		for (final Entry<Object, Object> entry : prop.entrySet()) {
			C2SMessagesMap.put((String) entry.getKey().toString().trim(), (String) entry.getValue().toString().trim());
		}
		Log.info("Messages Resource Properties file Loaded successfully");
	}

	public static void loadGatewayDetails() {
		String Gateways[] = _masterVO.getMasterValue(MasterI.GATEWAYCODES).split(",");
		String gatewayCodes = "";
		for (int i = 0; i < Gateways.length; i++) {
			if (i != Gateways.length - 1)
				gatewayCodes = gatewayCodes + "'" + Gateways[i].trim() + "',";
			else
				gatewayCodes = gatewayCodes + "'" + Gateways[i].trim() + "'";
		}
		gatewayObject = DBHandler.AccessHandler.getGatewayDetails(gatewayCodes);
	}

	public static void loadTestCaseFile() {
		Log.info("Loading test case file file from: .//src//test//resources//sysconfig//testCaseFile.properties");
		Properties prop = new Properties();
		TestCaseProp = new HashMap<String, String>();
		try {
			FileInputStream inputStream = new FileInputStream(
					".//src//test//resources//sysconfig//testCaseFile.properties");
			prop.load(inputStream);
		} catch (Exception e) {
			Log.info("Error while loading test cases File: ");
			Log.writeStackTrace(e);
		}
		for (final Entry<Object, Object> entry : prop.entrySet()) {
			TestCaseProp.put((String) entry.getKey(), (String) entry.getValue());
		}
		Log.info("Test cases file Loaded successfully");
	}

	public static void loadWebTestCases() {
		Log.info("Loading Web Cases Master file from: .//src//test//resources//config//testCaseFile.properties");
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(".//src//test//resources//sysconfig//testCaseFile.properties");
			prop.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Iterator it = prop.entrySet().iterator();
		while (it.hasNext()) {
			CaseMaster CaseMaster = new CaseMaster();
			Map.Entry pair = (Map.Entry) it.next();
			CaseMaster.setTestCaseCode(pair.getKey().toString());
			String[] testdetails = pair.getValue().toString().split("\\|");
			CaseMaster.setJiraID(testdetails[0]);
			CaseMaster.setModuleCode(testdetails[1]);
			CaseMaster.setDescription(testdetails[2]);
			GatewayMaster.put(pair.getKey().toString(), CaseMaster);
		}

		Log.info("Gateway Cases Master File Loaded successfully");
	}

	public static void loadExternalServices() throws IOException {
		if (!_masterVO.getMasterValue(MasterI.GATEWAYCODES).isEmpty()) {

			loadGatewayDetails();

			for (int i = 0; i < gatewayObject.length; i++) {
				if (gatewayObject[i][1].equals(GatewayI.EXTGW)) {
					EXTGWServicesList = _APIUtil.getAPIServices(GatewayI.EXTGW);
					EXTGWServicesList.removeAll(ServicesControllerI.serviceResponseList);
					loadGatewayCases(GatewayI.EXTGW);
				} else if (gatewayObject[i][1].equals(GatewayI.USSD)) {
					USSDServicesList = _APIUtil.getAPIServices(GatewayI.USSD);
					USSDServicesList.removeAll(ServicesControllerI.serviceResponseList);
					loadGatewayCases(GatewayI.USSD);
					loadGatewayCases(GatewayI.USSD_PLAIN);
				}
			}
		} else {
			Log.info("External Services not available in system as per Data Provider, hence no document loaded.");
		}
	}

	public static String getTestCases(String Key) {
		return TestCaseProp.get(Key);
	}

	public static String getProperty(String Key) {
		return PropertiesMap.get(Key);
	}

	public static String getMessage(String Key) {
		return MessagesMap.get(Key);
	}

	public static String getMasterValue(String Key) {
		return MasterSheetMap.get(Key);
	}

	public static String getClientDetail(String Key) {
		return ClientLibrary.get(Key);
	}

	public static String getC2SMessage(String Key) {
		return C2SMessagesMap.get(Key);
	}

	public static CaseMaster getCaseMasterByID(String Key) {
		return GatewayMaster.get(Key);
	}

	public static ArrayList<String> getEXTGWServices() {
		return EXTGWServicesList;
	}

	public static ArrayList<String> getUSSDServices() {
		return USSDServicesList;
	}
}
