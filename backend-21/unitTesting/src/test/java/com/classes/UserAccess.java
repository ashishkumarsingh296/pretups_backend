package com.classes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.poiji.internal.annotation.ExcelCell;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;

public class UserAccess extends BaseTest{

	public static ArrayList<String> arr;
	
	@ExcelCell(0)               
    public String MainLinks; 
	
    @ExcelCell(1)               
    public String SubLinks;     
   
    @ExcelCell(2)
    public String PageCodes;

    @ExcelCell(3)
    public String RoleCodes;

    @ExcelCell(4)
	public String CategoryCodes;
    
    @ExcelCell(5)
    public String GroupRoleApplicable;

/*    @Override
    public String toString() {
        return SubLinks + "," + PageCodes + "," + RoleCodes + "," + CategoryCodes;
    }*/
        
    public static Map<String, String> getUserWithAccess(String RoleCode) {
    	Log.info("Trying to get User with Access: " + RoleCode);
    	ArrayList<String> Array = new ArrayList<String>();
    	Map<String, String> resultMap = new HashMap<String, String>();
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			Array.add(CONSTANT.USERACCESSDAO[i][4].toString());
    		}
    	}
    	
    	ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
    	int excelLimit = ExcelUtility.getRowCount();
    	for (int i = 0; i <= excelLimit; i++) {
    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
    		if (Array.contains(excelCategory) && !loginID.equals(null) && !loginID.equals("")) {
    			resultMap.put("PARENT_NAME", ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i));
    			resultMap.put("CATEGORY_NAME", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
    			resultMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
    			resultMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
    			resultMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
    			resultMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
    			break;
    		}
    	}
    	
    	if (resultMap.get("LOGIN_ID") != null) {
    	String UserName = DBHandler.AccessHandler.getUserNameByLogin(resultMap.get("LOGIN_ID"));
    	resultMap.put("USER_NAME", UserName);
    	Log.info("UserAccess Returns: PARENT_NAME(" + resultMap.get("PARENT_NAME") + ") | CATEGORY_NAME(" + resultMap.get("CATEGORY_NAME") + ") | USER_NAME(" + resultMap.get("USER_NAME") +") | LOGIN_ID(" + resultMap.get("LOGIN_ID") + ") | PASSWORD(" + resultMap.get("PASSWORD") + ")");
    	} else
    		ExtentI.Markup(ExtentColor.RED, "No User Found with " + RoleCode + " rolecode access.");
    	return resultMap;
    }
    
    
    public static Map<String, String> getUserWithAccesswithCategory(String RoleCode,String categoryCode) {
    	
    	
    	Log.info("Trying to get User with Access: " + RoleCode);
    	ArrayList<String> Array = new ArrayList<String>();
    	Map<String, String> resultMap = new HashMap<String, String>();
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			Array.add(CONSTANT.USERACCESSDAO[i][4].toString());
    		}
    	}
    	
    	
    	ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
    	int excelLimit = ExcelUtility.getRowCount();
    	for (int i = 0; i <= excelLimit; i++) {
    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
    		if (categoryCode.equals(excelCategory) && !loginID.equals(null) && !loginID.equals("")) {
    			resultMap.put("PARENT_NAME", ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i));
    			resultMap.put("CATEGORY_NAME", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
    			resultMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
    			resultMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
    			resultMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
    			resultMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
    			break;
    		}
    	}
    	
    	if (resultMap.get("LOGIN_ID") != null) {
    	String UserName = DBHandler.AccessHandler.getUserNameByLogin(resultMap.get("LOGIN_ID"));
    	resultMap.put("USER_NAME", UserName);
    	Log.info("UserAccess Returns: PARENT_NAME(" + resultMap.get("PARENT_NAME") + ") | CATEGORY_NAME(" + resultMap.get("CATEGORY_NAME") + ") | USER_NAME(" + resultMap.get("USER_NAME") +") | LOGIN_ID(" + resultMap.get("LOGIN_ID") + ") | PASSWORD(" + resultMap.get("PASSWORD") + ")");
    	} else
    		ExtentI.Markup(ExtentColor.RED, "No User Found with " + categoryCode + " category code access.");
    	return resultMap;
    }
    
    
public static Map<String, String> getUserWithAccesswithCategorywithDomain(String RoleCode,String categoryCode) {
    	
    	
    	Log.info("Trying to get User with Access: " + RoleCode);
    	ArrayList<String> Array = new ArrayList<String>();
    	Map<String, String> resultMap = new HashMap<String, String>();
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			Array.add(CONSTANT.USERACCESSDAO[i][4].toString());
    		}
    	}
    	
    	String domain = DBHandler.AccessHandler.getDomainCodeCatgories(categoryCode);
    	if(domain.equals("OPT")) {
    	ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
    	}
    	else if(domain.equals("DIST")) {
        	ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
    	}
    	int excelLimit = ExcelUtility.getRowCount();
    	for (int i = 0; i <= excelLimit; i++) {
    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
    		if (categoryCode.equals(excelCategory) && !loginID.equals(null) && !loginID.equals("")) {
    			resultMap.put("PARENT_NAME", ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i));
    			resultMap.put("CATEGORY_NAME", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
    			resultMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
    			resultMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
    			resultMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
    			resultMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
    			break;
    		}
    	}
    	
    	if (resultMap.get("LOGIN_ID") != null) {
    	String UserName = DBHandler.AccessHandler.getUserNameByLogin(resultMap.get("LOGIN_ID"));
    	resultMap.put("USER_NAME", UserName);
    	Log.info("UserAccess Returns: PARENT_NAME(" + resultMap.get("PARENT_NAME") + ") | CATEGORY_NAME(" + resultMap.get("CATEGORY_NAME") + ") | USER_NAME(" + resultMap.get("USER_NAME") +") | LOGIN_ID(" + resultMap.get("LOGIN_ID") + ") | PASSWORD(" + resultMap.get("PASSWORD") + ")");
    	} else
    		ExtentI.Markup(ExtentColor.RED, "No User Found with " + categoryCode + " category code access.");
    	return resultMap;
    }
    
public static Map<String, String> getUserWithAccesswithCategoryOtherNetworkAdmin(String RoleCode,String categoryCode) {
    	
    	
    	Log.info("Trying to get User with Access: " + RoleCode);
    	ArrayList<String> Array = new ArrayList<String>();
    	Map<String, String> resultMap = new HashMap<String, String>();
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			Array.add(CONSTANT.USERACCESSDAO[i][4].toString());
    		}
    	}
    	
    	
    	ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.OPERATOR_USERS_NETWORK_ADMIN_HIERARCHY_SHEET);
    	int excelLimit = ExcelUtility.getRowCount();
    	for (int i = 0; i <= excelLimit; i++) {
    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
    		if (categoryCode.equals(excelCategory) && !loginID.equals(null) && !loginID.equals("")) {
    			resultMap.put("PARENT_NAME", ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i));
    			resultMap.put("CATEGORY_NAME", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
    			resultMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
    			resultMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
    			resultMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
    			resultMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
    			break;
    		}
    	}
    	
    	if (resultMap.get("LOGIN_ID") != null) {
    	String UserName = DBHandler.AccessHandler.getUserNameByLogin(resultMap.get("LOGIN_ID"));
    	resultMap.put("USER_NAME", UserName);
    	Log.info("UserAccess Returns: PARENT_NAME(" + resultMap.get("PARENT_NAME") + ") | CATEGORY_NAME(" + resultMap.get("CATEGORY_NAME") + ") | USER_NAME(" + resultMap.get("USER_NAME") +") | LOGIN_ID(" + resultMap.get("LOGIN_ID") + ") | PASSWORD(" + resultMap.get("PASSWORD") + ")");
    	} else
    		ExtentI.Markup(ExtentColor.RED, "No User Found with " + categoryCode + " category code access.");
    	return resultMap;
    }
    
    public static Map<String, String> getUserWithAccessVoucher(String RoleCode,String vouchertype ) {
    	Log.info("Trying to get User with Access: " + RoleCode);
    	ArrayList<String> Array = new ArrayList<String>();
    	Map<String, String> resultMap = new HashMap<String, String>();
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			Array.add(CONSTANT.USERACCESSDAO[i][4].toString());
    		}
    	}
    	if(vouchertype.equalsIgnoreCase("electronic"))
    		ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.ELC_OPERATOR_USERS_HIERARCHY_SHEET);
    	else if(vouchertype.equalsIgnoreCase("physical")){
    		ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.PHY_OPERATOR_USERS_HIERARCHY_SHEET);
    	}
    	int excelLimit = ExcelUtility.getRowCount();
    	for (int i = 0; i <= excelLimit; i++) {
    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
    		if (Array.contains(excelCategory) && !loginID.equals(null) && !loginID.equals("")) {
    			resultMap.put("PARENT_NAME", ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i));
    			resultMap.put("CATEGORY_NAME", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
    			resultMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
    			resultMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
    			resultMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
    			resultMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
    			break;
    		}
    	}
    	
    	if (resultMap.get("LOGIN_ID") != null) {
    	String UserName = DBHandler.AccessHandler.getUserNameByLogin(resultMap.get("LOGIN_ID"));
    	resultMap.put("USER_NAME", UserName);
    	Log.info("UserAccess Returns: PARENT_NAME(" + resultMap.get("PARENT_NAME") + ") | CATEGORY_NAME(" + resultMap.get("CATEGORY_NAME") + ") | USER_NAME(" + resultMap.get("USER_NAME") +") | LOGIN_ID(" + resultMap.get("LOGIN_ID") + ") | PASSWORD(" + resultMap.get("PASSWORD") + ")");
    	} else
    		ExtentI.Markup(ExtentColor.RED, "No User Found with " + RoleCode + " rolecode access.");
    	return resultMap;
    }
    
    public static Map<String, String> getUserWithAccessForVoucherTypeCategory(String RoleCode, String voucherType,String categoryCode) {
    	Log.info("Trying to get User with Access: " + RoleCode);
    	ArrayList<String> Array = new ArrayList<String>();
    	Map<String, String> resultMap = new HashMap<String, String>();
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			Array.add(CONSTANT.USERACCESSDAO[i][4].toString());
    		}
    	}
    	if(voucherType.equalsIgnoreCase("physical"))
    	ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.PHY_OPERATOR_USERS_HIERARCHY_SHEET);
    	else if(voucherType.equalsIgnoreCase("electronic"))
    		ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.ELC_OPERATOR_USERS_HIERARCHY_SHEET);
    	int excelLimit = ExcelUtility.getRowCount();
    	for (int i = 0; i <= excelLimit; i++) {
    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
    		if (categoryCode.equals(excelCategory) && !loginID.equals(null) && !loginID.equals("")) {
    			resultMap.put("PARENT_NAME", ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i));
    			resultMap.put("CATEGORY_NAME", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
    			resultMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
    			resultMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
    			resultMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
    			resultMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
    			break;
    		}
    	}
    	
    	if (resultMap.get("LOGIN_ID") != null) {
    	String UserName = DBHandler.AccessHandler.getUserNameByLogin(resultMap.get("LOGIN_ID"));
    	resultMap.put("USER_NAME", UserName);
    	Log.info("UserAccess Returns: PARENT_NAME(" + resultMap.get("PARENT_NAME") + ") | CATEGORY_NAME(" + resultMap.get("CATEGORY_NAME") + ") | USER_NAME(" + resultMap.get("USER_NAME") +") | LOGIN_ID(" + resultMap.get("LOGIN_ID") + ") | PASSWORD(" + resultMap.get("PASSWORD") + ")");
    	} else
    		ExtentI.Markup(ExtentColor.RED, "No User Found with " + RoleCode + " rolecode access.");
    	return resultMap;
    }
    
    public static Map<String, String> getUserWithAccessForVoucherType(String RoleCode, String voucherType) {
    	Log.info("Trying to get User with Access: " + RoleCode);
    	ArrayList<String> Array = new ArrayList<String>();
    	Map<String, String> resultMap = new HashMap<String, String>();
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			Array.add(CONSTANT.USERACCESSDAO[i][4].toString());
    		}
    	}
    	if(voucherType.equalsIgnoreCase("physical"))
    	ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.PHY_OPERATOR_USERS_HIERARCHY_SHEET);
    	else if(voucherType.equalsIgnoreCase("electronic"))
    		ExcelUtility.setExcelFile(".//src//test//resources//config//DataProvider.xlsx", ExcelI.ELC_OPERATOR_USERS_HIERARCHY_SHEET);
    	int excelLimit = ExcelUtility.getRowCount();
    	for (int i = 0; i <= excelLimit; i++) {
    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
    		if (Array.contains(excelCategory) && !loginID.equals(null) && !loginID.equals("")) {
    			resultMap.put("PARENT_NAME", ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i));
    			resultMap.put("CATEGORY_NAME", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
    			resultMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
    			resultMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
    			resultMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
    			resultMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
    			break;
    		}
    	}
    	
    	if (resultMap.get("LOGIN_ID") != null) {
    	String UserName = DBHandler.AccessHandler.getUserNameByLogin(resultMap.get("LOGIN_ID"));
    	resultMap.put("USER_NAME", UserName);
    	Log.info("UserAccess Returns: PARENT_NAME(" + resultMap.get("PARENT_NAME") + ") | CATEGORY_NAME(" + resultMap.get("CATEGORY_NAME") + ") | USER_NAME(" + resultMap.get("USER_NAME") +") | LOGIN_ID(" + resultMap.get("LOGIN_ID") + ") | PASSWORD(" + resultMap.get("PASSWORD") + ")");
    	} else
    		ExtentI.Markup(ExtentColor.RED, "No User Found with " + RoleCode + " rolecode access.");
    	return resultMap;
    }
    public static HashMap<String, String> getUserDetailsForCategory(String Category) {
    	HashMap<String, String> returnMap = new HashMap<String, String>();
    	String MasterSheetPath = _masterVO.getProperty("DataProvider");
    	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
    	int OperatorRowCount = ExcelUtility.getRowCount();
    	for (int i =1; i<OperatorRowCount; i++) {
    		String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
    		String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
    		if (CategoryName.equals(Category) && (!LoginID.equals(null) || !LoginID.equals(""))) {
    			returnMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
    			returnMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
    			returnMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
    			returnMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
    			break;
    		}
    	}

    	if (returnMap.get("LOGIN_ID") == null) {

        	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        	int ChannelRowCount = ExcelUtility.getRowCount();
        	for (int i = 1; i<ChannelRowCount; i++) {
        		String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
        		String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
        		if (CategoryName.equals(Category) && (!LoginID.equals(null) || !LoginID.equals(""))) {
        			returnMap.put("USER_NAME", ExcelUtility.getCellData(0, ExcelI.USER_NAME, i));
        			returnMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
        			returnMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
        			returnMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
        			returnMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
        			returnMap.put("EXTERNAL_CODE", ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i));
        			break;
        		}
        	}
    	}
    	Log.info("getUserDetailsForCategory Returns - USER_NAME: " + returnMap.get("USER_NAME") + " | LOGIN_ID: " + returnMap.get("LOGIN_ID") + " | PASSWORD: " + returnMap.get("PASSWORD") + " | PIN: " + returnMap.get("PIN") + " | MSISDN: " + returnMap.get("MSISDN") + " | EXTERNAL_CODE: " + returnMap.get("EXTERNAL_CODE"));
    	return returnMap;
    }
    
    public static HashMap<String, String> getChannelUserDetails(String parentCategory, String Category) {
    	HashMap<String, String> returnMap = new HashMap<String, String>();
    	String MasterSheetPath = _masterVO.getProperty("DataProvider");
    	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
    	int ChannelRowCount = ExcelUtility.getRowCount();
    	for (int i = 1; i<=ChannelRowCount; i++) {
    		String ParentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
    		String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
    		if (ParentCategory.equals(parentCategory) && CategoryName.equals(Category)) {
    			returnMap.put("CATEGORY", Category);
    			returnMap.put("USER_NAME", ExcelUtility.getCellData(0, ExcelI.USER_NAME, i));
    			returnMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
    			returnMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
    			returnMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
    			returnMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
    			returnMap.put("EXTERNAL_CODE", ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i));
    			break;
    		}
    	}
		return returnMap;
    }
    
    /**
     * @author krishan.chawla
     * @param RoleCode
     * @return Object[][]
     * @throws InterruptedException 
     */
    public static Object[][] getUsersWithAccess(String RoleCode) {
    	int accessCounter = 0;
    	String MasterSheetPath = _masterVO.getProperty("DataProvider");
    	
    	Log.info("Trying to get Users with Access: " + RoleCode);
    	for (int i=0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			accessCounter++;
    		}
    	}
    	Object[][] usersObject = new Object[accessCounter][7];
    	accessCounter = 0;
    	
    	/*
    	 * Loading Object with operator users details
    	 */
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			String categorycode = CONSTANT.USERACCESSDAO[i][4].toString();
    			
    			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
    	    	int excelLimit = ExcelUtility.getRowCount();
    	    	for (int j = 1; j <= excelLimit; j++) {
    	    		String excelCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, j);
    	    		String loginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
    	    		if (categorycode.equals(excelCategory)) {
    	    			usersObject[accessCounter][0] = categorycode;
    	    			usersObject[accessCounter][1] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, j);
    	    			usersObject[accessCounter][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
    	    			usersObject[accessCounter][3] = DBHandler.AccessHandler.getUserNameByLogin(loginID);
    	    			usersObject[accessCounter][4] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, j);
    	    			usersObject[accessCounter][5] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
    	    			usersObject[accessCounter][6] = ExcelUtility.getCellData(0, ExcelI.PIN, j);
    	    			accessCounter++;
    	    			break;
    	    		}
    	    	}
    			
    		}
    	}
    	
    	return usersObject;
    }
    
    public static ArrayList<String> getCategoryWithAccess(String RoleCode) {
    	Log.info("Trying to get Category with Access: " + RoleCode);
    	ArrayList<String> Array = new ArrayList<String>();
    	Map<String, String> resultMap = new HashMap<String, String>();
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			Array.add(CONSTANT.USERACCESSDAO[i][4].toString());
    		}
    	}
    	return Array;
    }
    
    /**
     * This method is used to get the status of RoleCode from Link Sheet.
     * @param RoleCode
     * @return
     */
    public static boolean getRoleStatus(String RoleCode) {
    	boolean roleStatus = false;
    	Log.info("Trying to get Status of Role: " + RoleCode);
    	for (int i = 0; i<CONSTANT.USERACCESSDAO.length; i++) {
    		if (CONSTANT.USERACCESSDAO[i][3].equals(RoleCode)) {
    			roleStatus = true;
    			Log.info("Role: " + RoleCode + " is available in application as per Link Sheet");
    			break;
    		}
    	}
    	return roleStatus;
    }
  
    public static String userapplevelpreference(){
    	String pref_code=null;
    	if(_masterVO.getClientDetail("USERAPPROVALEVEL").equals("0"))
    		pref_code = "USER_APPROVAL_LEVEL";
    	else if(_masterVO.getClientDetail("USERAPPROVALEVEL").equals("1"))
    		pref_code = "USRLEVELAPPROVAL";
    	return pref_code;
    }
    
    public static Object[] getApplicableRolesForCategory(String categoryCode) {
    	final String methodname = "getApplicableRolesForCategory";
    	Log.info("Entered " + methodname + "(" + categoryCode + ")");
    	int dataLength = CONSTANT.USERACCESSDAO.length;
    	int objSize = 0;
    	for (int i = 0; i < dataLength; i++) {
    		if (CONSTANT.USERACCESSDAO[i][4].equals(categoryCode) && CONSTANT.USERACCESSDAO[i][5].equals(PretupsI.YES)) {
    			objSize++;
    		}
    	}
    	
    	Object[] grpRoleObj = new Object[objSize];
    	int objCounter = 0;

    	for (int counter = 0; counter < dataLength; counter++) {	
    		if (CONSTANT.USERACCESSDAO[counter][4].equals(categoryCode) && CONSTANT.USERACCESSDAO[counter][5].equals(PretupsI.YES)) {					
    			grpRoleObj[objCounter] = CONSTANT.USERACCESSDAO[counter][3];
    			objCounter ++;
    		}
    	}
    	Log.info(methodname, grpRoleObj);
    	return grpRoleObj;
    }
}