package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.poiji.internal.Poiji;

public class _masterVO{

	private static Map<String, String> PropertiesMap;
	private static Map<String, String> MessagesMap;
	private static HashMap<String, String> MasterSheetMap;
	public static HashMap<String, String[]> geoTypeMap;
	static String sheetName;
	static String MasterSheetPath;
	public static Map<String, String> p2pServicesMap;
	public static Map<String, String> c2sServicesMap;

	public static void loadProperties() {
		Properties prop = new Properties();
		PropertiesMap = new HashMap<String, String>();
		try {
			FileInputStream inputStream = new FileInputStream(".//sysconfig//Constant.properties");
			prop.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Some issue finding or loading file....!!! " + e.getMessage());

		}
		for (final Entry<Object, Object> entry : prop.entrySet()) {
			PropertiesMap.put((String) entry.getKey(), (String) entry.getValue());
		}
	}
	
	public static void loadMessages() {
		Properties prop = new Properties();
		MessagesMap= new HashMap<String, String>();
		try {
			String LANG = _masterVO.getMasterValue("Language");
			FileInputStream inputStream = new FileInputStream(".//sysconfig//"+LANG+".properties");
			prop.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Some issue finding or loading file....!!! " + e.getMessage());

		}
		for (final Entry<Object, Object> entry : prop.entrySet()) {
			MessagesMap.put((String) entry.getKey().toString().trim(), (String) entry.getValue().toString().trim());
		}
	}
	

	@SuppressWarnings({ "deprecation", "resource" })
	public static HashMap<String, String> loadMasterSheet() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
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
		return MasterSheetMap;
	}
	
	public static void loadGeoDomains(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		geoTypeMap=new HashMap<String, String[]>();
		
		//Fetch values from "Geographical Domains" sheet
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		int countRow=ExcelUtility.getRowCount();System.out.println("Total Count for geodomain: "+countRow);
		for(int i=1;i<=countRow;i++)
		{
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		String value1 = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i);
		String value2=ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
		String name= ExcelUtility.getCellData(0, ExcelI.DOMAIN_TYPE_NAME, i);
		String rowNum = String.valueOf(i); 			//added on 05 September
		
		//Fetch values from "Geography Domain Types" sheet
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int j = 0;
		while (j <= rowCount) {
			String GrpDomainName = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE_NAME, j);
			
			//Comparing geography name from both sheets
			if (GrpDomainName.equals(name)) {
				
				break;
			}
			j++;
		}
		String key=ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE,j);
		
		String Value[]={value1,value2,rowNum};	
		geoTypeMap.put(key, Value);
		Log.info("loadGeoDomains Map returns ::"+ key+" - "+geoTypeMap.get(key)[0]+" - "+geoTypeMap.get(key)[1]);
		}
		
	} 
	
	public static void generateUserAccessObject() {
		try {
    	List<UserAccess> employees = Poiji.fromExcel(new File(".//config//LinksSheet.xlsx"), UserAccess.class);
    	CONSTANT.USERACCESSDAO = new Object[employees.size()][5];
        for (int i = 0; i<employees.size(); i++) {
        		UserAccess firstEmployee = employees.get(i);
        		firstEmployee.CategoryCodes.toString();
        		CONSTANT.USERACCESSDAO[i][0] = firstEmployee.MainLinks.toString();
        		CONSTANT.USERACCESSDAO[i][1] = firstEmployee.SubLinks.toString();
        		CONSTANT.USERACCESSDAO[i][2] = firstEmployee.PageCodes.toString();
        		CONSTANT.USERACCESSDAO[i][3] = firstEmployee.RoleCodes.toString();
        		CONSTANT.USERACCESSDAO[i][4] = firstEmployee.CategoryCodes.toString();
        	}
		}
		catch (Exception e) { 
			Log.info("Error While generating User Access Object");
			Log.writeStackTrace(e); 
			}
	}
	
	public static void loadC2SServices(){
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		c2sServicesMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		c2sServicesMap = new HashMap<String, String>();
		for (int i = 0; i < totalRow; i++)
			c2sServicesMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), ExcelUtility.getCellData(0, ExcelI.NAME, i));
	}
	
	public static void loadP2PServices(){
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		p2pServicesMap = new HashMap<String, String>();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		p2pServicesMap = new HashMap<String, String>();
		for (int i = 0; i < totalRow; i++)
			p2pServicesMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), ExcelUtility.getCellData(0, ExcelI.NAME, i));
	}
	
	public static void loadPreferences() {
		
		CONSTANT.NetworkName = DBHandler.AccessHandler.getNetworkName(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		SystemPreferences.MULTIPLICATIONFACTOR = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("AMOUNT_MULT_FACTOR"));
		SystemPreferences.MSISDN_PREFIX_LENGTH = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MSISDN_PREFIX_LENGTH"));
		SystemPreferences.MAX_MSISDN_LENGTH = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_MSISDN_LENGTH"));
		
		String localeLanguage = DBHandler.AccessHandler.getSystemPreference("DISPLAY_LANGUAGE");
		String localeCountry = DBHandler.AccessHandler.getSystemPreference("DISPLAY_COUNTRY");
		if (localeCountry != null && localeLanguage != null) {
			SystemPreferences.DISPLAY_LANGUAGE = localeLanguage;
			SystemPreferences.DISPLAY_COUNTRY = localeCountry;
			Log.info("Display Locale set to: " + SystemPreferences.DISPLAY_LANGUAGE + "_" + SystemPreferences.DISPLAY_COUNTRY);
		}
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
}
