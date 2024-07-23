package com.databuilder;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author krishan.chawla This class is created to fetch data from Database and
 *         write them to Excel before Pre-Requisites. New functions can be added
 *         in order to add any new Pre-Requisite data.
 */
public class BuilderLogic extends BaseTest {

	private static String MasterSheetPath;
	static int i;
	static int j = 1;
	int k;
	Properties prop;
	String MatrixValue;
	Object[][] accessBearerData;

	// This function is created to fetch Operator User hierarchy from database &
	// write them in Excel sheet
	public void WriteOperatorUserstoExcel() throws SQLException {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		String SuperAdminLoginID = _masterVO.getMasterValue(MasterI.SUPERADMIN_LOGINID);
		String SuperAdminPassword = _masterVO.getMasterValue(MasterI.SUPERADMIN_PASSWORD);
		ResultSet QueryResult = DBHandler.AccessHandler.fetchOperatorUsers();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int count = 1;
		ResultSetMetaData rsMetaData = QueryResult.getMetaData();
		for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
			ExcelUtility.createHeader(rsMetaData.getColumnName(i).toUpperCase(), j);
		}
		while (QueryResult.next()) {
			try {
				if (QueryResult.getString(1).toString() != null) {
					++count;
					for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
						ExcelUtility.setCellData(QueryResult.getString(i).toString(), count, j);
					}
				}
			} catch (Exception e) {
				Log.error("Error while creating Operator User Hierarchy sheet:");
				Log.writeStackTrace(e);
			}

		}
		String SuperAdminCatName = DBHandler.AccessHandler.getCategoryName(PretupsI.SUPERADMIN_CATCODE);
		ExcelUtility.createHeader(ExcelI.LOGIN_ID, 7);
		ExcelUtility.createHeader(ExcelI.PASSWORD, 8);
		ExcelUtility.createHeader(ExcelI.MSISDN, 9);
		ExcelUtility.createHeader(ExcelI.PIN, 10);
		ExcelUtility.createHeader(ExcelI.GROUP_ROLE, 11);
		ExcelUtility.setCellData(0, ExcelI.PARENT_CATEGORY_CODE, 1, PretupsI.SUPERADMIN_CATCODE);
		ExcelUtility.setCellData(0, ExcelI.PARENT_NAME, 1, SuperAdminCatName);
		ExcelUtility.setCellData(0, ExcelI.CATEGORY_CODE, 1, PretupsI.SUPERADMIN_CATCODE);
		ExcelUtility.setCellData(0, ExcelI.CATEGORY_NAME, 1, SuperAdminCatName);
		ExcelUtility.setCellData(0, ExcelI.LOGIN_ID, 1, SuperAdminLoginID);
		ExcelUtility.setCellData(0, ExcelI.PASSWORD, 1, SuperAdminPassword);
	}

	
	
	
	// This function is created to fetch Network Admin from database for VMS networks &
		// write them in Excel sheet
		public void WriteOperatorUserstoExcelVMSNetworkAdmin() throws SQLException {
			MasterSheetPath = _masterVO.getProperty("DataProvider");
			String SuperAdminLoginID = _masterVO.getMasterValue(MasterI.SUPERADMIN_LOGINID);
			String SuperAdminPassword = _masterVO.getMasterValue(MasterI.SUPERADMIN_PASSWORD);
			ResultSet QueryResult = DBHandler.AccessHandler.fetchOperatorUsersVMSNetworkAdmin();
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_NETWORK_ADMIN_HIERARCHY_SHEET);
			int count = 1;
			ResultSetMetaData rsMetaData = QueryResult.getMetaData();
			for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
				ExcelUtility.createHeader(rsMetaData.getColumnName(i).toUpperCase(), j);
			}
			while (QueryResult.next()) {
				try {
					if (QueryResult.getString(1).toString() != null) {
						++count;
						for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
							ExcelUtility.setCellData(QueryResult.getString(i).toString(), count, j);
						}
					}
				} catch (Exception e) {
					Log.error("Error while creating Operator User Hierarchy sheet:");
					Log.writeStackTrace(e);
				}

			}
			String SuperAdminCatName = DBHandler.AccessHandler.getCategoryName(PretupsI.SUPERADMIN_CATCODE);
			ExcelUtility.createHeader(ExcelI.LOGIN_ID, 7);
			ExcelUtility.createHeader(ExcelI.PASSWORD, 8);
			ExcelUtility.createHeader(ExcelI.MSISDN, 9);
			ExcelUtility.createHeader(ExcelI.PIN, 10);
			ExcelUtility.createHeader(ExcelI.GROUP_ROLE, 11);
			ExcelUtility.createHeader(ExcelI.NETWORK_CODE, 12);
			ExcelUtility.setCellData(0, ExcelI.PARENT_CATEGORY_CODE, 1, PretupsI.SUPERADMIN_CATCODE);
			ExcelUtility.setCellData(0, ExcelI.PARENT_NAME, 1, SuperAdminCatName);
			ExcelUtility.setCellData(0, ExcelI.CATEGORY_CODE, 1, PretupsI.SUPERADMIN_CATCODE);
			ExcelUtility.setCellData(0, ExcelI.CATEGORY_NAME, 1, SuperAdminCatName);
			ExcelUtility.setCellData(0, ExcelI.LOGIN_ID, 1, SuperAdminLoginID);
			ExcelUtility.setCellData(0, ExcelI.PASSWORD, 1, SuperAdminPassword);
		}
	
	
	/*
	 * This function writes Channel User's Hierarchy as per the Transfer Matrix.
	 */
	public void WriteChannelUsersHierarchy(String sheetName) {
		int j, i, k;
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_MATRIX_SHEET);
		int rowCount = ExcelUtility.getRowCount() - 3;
		System.out.println("RowCount is : " + rowCount);
		Object[][] MatrixArray = new Object[rowCount][4];
		for (i = 0, j = 3; i < rowCount; i++, j++) {
			MatrixArray[i][0] = ExcelUtility.getCellData(1, "Sr. No.", j).replaceAll("([0-9])\\.0+([^0-9]|$)", "$1$2");
			MatrixArray[i][1] = ExcelUtility.getCellData(1, "Channel Domain", j);
			MatrixArray[i][2] = ExcelUtility.getCellData(1, "Channel Categories", j);
			MatrixArray[i][3] = ExcelUtility.getCellData(1, "Allowed parent", j).toString();
		}
		ExcelUtility.setExcelFile(MasterSheetPath, sheetName);
		ExcelUtility.createHeader(ExcelI.DOMAIN_NAME, ExcelI.PARENT_CATEGORY_NAME, ExcelI.CATEGORY_CODE, ExcelI.CATEGORY_NAME,
				ExcelI.GRPH_DOMAIN_TYPE, ExcelI.SEQUENCE_NO, ExcelI.USER_NAME, ExcelI.LOGIN_ID, ExcelI.PASSWORD, ExcelI.MSISDN, ExcelI.PIN,
				ExcelI.EXTERNAL_CODE, ExcelI.GEOGRAPHY, ExcelI.GRADE, ExcelI.CARDGROUP_NAME, ExcelI.SA_TCP_NAME, ExcelI.SA_TCP_PROFILE_ID, ExcelI.NA_TCP_NAME,
				ExcelI.NA_TCP_PROFILE_ID, ExcelI.COMMISSION_PROFILE, ExcelI.ADDITIONAL_COMMISSION ,ExcelI.GROUP_ROLE,
				ExcelI.OTHER_COMMISSION_TYPE, ExcelI.OTHER_COMMISSION_VALUE, ExcelI.OTHER_COMMISSION_PROFILE, ExcelI.LOAN_PROFILE);
		for (i = 0, j = 1; i < MatrixArray.length; i++) {
			String IntArray = MatrixArray[i][3].toString();
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(IntArray.split("[ ]*,[ ]*")));
			for (k = 0; k < aList.size(); k++) {
				ExcelUtility.setCellData(0, ExcelI.DOMAIN_NAME, j, "" + MatrixArray[i][1]);
				ExcelUtility.setCellData(0, ExcelI.CATEGORY_NAME, j, "" + MatrixArray[i][2]);
				int parentCheck = 0;
				String ParentName = null;
				String ParentSrNo = (aList.get(k).toString()).replaceAll("([0-9])\\.0+([^0-9]|$)", "$1$2");
				while (parentCheck < MatrixArray.length) {
					if (((ParentSrNo).trim()).equals("0") || ((ParentSrNo).trim()).equals("1")) {
						ExcelUtility.setCellData(0, ExcelI.PARENT_CATEGORY_NAME, j, "Root");
						ExcelUtility.setCellData(0, ExcelI.SEQUENCE_NO, j, "1");
						break;
					}
					if ((((MatrixArray[parentCheck][0]).toString()).trim()).equals((ParentSrNo).trim())) {
						ParentName = MatrixArray[parentCheck][2].toString();
						ExcelUtility.setCellData(0, ExcelI.PARENT_CATEGORY_NAME, j, ParentName);
						break;
					}
					parentCheck++;
				}
				j++;
			}
		}
	}

	/**
	 * Fetches Geographical Domain Type & Category Code on the basis of Category
	 * Name from Transfer Matrix
	 * @dependency Channel User Hierarchy Sheet populated with Transfer Matrix data
	 */
	public void fetchCategoryCodeAndGeographicalDomain(String sheetName) {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, sheetName);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
			String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			String resultSet[] = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(CategoryName);
			ExcelUtility.setCellData(0, ExcelI.CATEGORY_CODE, i, resultSet[0]);
			ExcelUtility.setCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i, resultSet[1]);
		}
	}

	/**
	 * Writes Geographical Domain Types, fetched from query to Excel
	 * 
	 * @return:
	 * @author ayush.abhijeet
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void WriteGeographyDomainTypestoExcel() throws SQLException {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ResultSet QueryResult = DBHandler.AccessHandler.getGeographicalDomainTypes();
		ResultSetMetaData rsMetaData = QueryResult.getMetaData();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
		for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
			ExcelUtility.createHeader(rsMetaData.getColumnName(i).toUpperCase(), j);
		}
		int count = 0;
		while (QueryResult.next()) {
			try {
				if (QueryResult.getString(1).toString() != null) {
					++count;
					for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
						ExcelUtility.setCellData(QueryResult.getString(i).toString(), count, j);
					}
				}
			} catch (Exception e) {
				Log.info("Error while writing Georaphhy Domain Types Sheet");
				Log.writeStackTrace(e);
			}

		}

	}

	// Writes Channel Domains & Categories to Channel User Category Sheet
	public void WriteDomainCategoryForChannelUser() throws SQLException {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ResultSet QueryResult = DBHandler.AccessHandler.getDomainandCategories();
		ResultSetMetaData rsMetaData = QueryResult.getMetaData();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USER_CATEGORY_SHEET);
		for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
			ExcelUtility.createHeader(rsMetaData.getColumnName(i).toUpperCase(), j);
		}
		int count = 0;
		while (QueryResult.next()) {
			try {
				if (QueryResult.getString(1).toString() != null) {
					++count;
					for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
						ExcelUtility.setCellData(QueryResult.getString(i).toString(), count, j);
					}
				}
			} catch (Exception e) {
				Log.info("Error while creating Channel User Category Sheet");
				Log.writeStackTrace(e);
			}

		}
	}

	/**
	 * Reads Transfer Matrix Sheet from DataProvider sheet and write Transfer
	 * Rule Sheet for further usage.
	 * 
	 * @author krishan.chawla
	 * @Dependency Transfer Matrix Sheet
	 */
	public void writeTransferRuleSheet() {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_MATRIX_SHEET);
		int rowCount = ExcelUtility.getRowCount() - 1;
		Object[][] MatrixArray = new Object[rowCount][3];
		for (int i = 0, j = 2; i < rowCount; i++, j++) {
			MatrixArray[i][0] = j;
			MatrixArray[i][1] = ExcelUtility.getCellData(1, "Channel Domain", j);
			MatrixArray[i][2] = ExcelUtility.getCellData(1, "Channel Categories", j);
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		ExcelUtility.createHeader(ExcelI.INDEX, ExcelI.FROM_DOMAIN, ExcelI.FROM_CATEGORY, ExcelI.TO_DOMAIN, ExcelI.TO_CATEGORY, ExcelI.SERVICES, ExcelI.STATUS, ExcelI.ACCESS_BEARER,ExcelI.TRF_RULE_TYPE);
		for (i = 0; i < MatrixArray.length; i++) {

			for (k = 0; k < MatrixArray.length; k++) {

				int RowNum = Integer.parseInt(MatrixArray[i][0].toString());
				String ColumnName = MatrixArray[k][2].toString();
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_MATRIX_SHEET);
				MatrixValue = ExcelUtility.getCellData(1, ColumnName, RowNum);

				if (MatrixValue != null && MatrixValue != "") {
					ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
					ExcelUtility.setCellData("" + MatrixArray[i][0], j, 0);
					ExcelUtility.setCellData("" + MatrixArray[i][1], j, 1);
					ExcelUtility.setCellData("" + MatrixArray[i][2], j, 2);
					ExcelUtility.setCellData("" + MatrixArray[k][1], j, 3);
					ExcelUtility.setCellData("" + MatrixArray[k][2], j, 4);
					ExcelUtility.setCellData(MatrixValue, j, 5);
					j++;
				}
			}
		}
	}
	
	
	/**
     * Fetches Access Bearer the basis of Category Users from Access Bearer
     * matrix sheet
     * 
      * @dependency Access Bearer matrix Sheet populated with Access Bearer
     *             matrix data
     */
     public Object[][] fetchAcsessBearer() {
            MasterSheetPath = _masterVO.getProperty("DataProvider");
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.ACCESS_BEARER_MATRIX_SHEET);
            int rowCount = ExcelUtility.getRowCount();
            int columnCount = ExcelUtility.getColumnCount();
            String all = "ALL";
            accessBearerData = new Object[rowCount][2];
            for (int i = 1; i <= rowCount; i++) {
                   accessBearerData[i-1][0] = ExcelUtility.getCellData(0, "Category Users", i);
                   accessBearerData[i-1][1] = all;
                   for (int j = 2; j < columnCount; j++) {
                         if (ExcelUtility.getCellData(i, j).equals("Y"))
                                accessBearerData[i-1][1] += "," + ExcelUtility.getCellData(0, j);
                   }
            }

            return accessBearerData;
     }

     /**
     * Writes Access Bearer the basis of Category Users to Transfer Rule sheet
     * 
      */
     public void writeAcsessBearer(Object[][] accessBearerData) {
            MasterSheetPath = _masterVO.getProperty("DataProvider");
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            int rowCount = ExcelUtility.getRowCount();
            int size = accessBearerData.length;
            int j = 1;
            while (j <= rowCount) {
                   String fromCategory = ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, j);
                   for (int i = 0; i < size; i++) {
                         String catUser = (String) accessBearerData[i][0];
                         if (fromCategory.equals(catUser)) {
                                ExcelUtility.setCellData(0, ExcelI.ACCESS_BEARER, j, (String) accessBearerData[i][1]);
                         }
                   }
                   j++;
            }
     }

	// Writes C2S Services and sub services to service Sheet
	public void WriteC2SServiceAndSubServices() throws SQLException {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		String NetworkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		ResultSet QueryResult = DBHandler.AccessHandler.fetchC2SServicesAndSubServices(NetworkCode);
		ResultSetMetaData rsMetaData = QueryResult.getMetaData();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int count = 0;
		for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
			ExcelUtility.createHeader(rsMetaData.getColumnName(i).toUpperCase(), j);
			count++;
		}
		ExcelUtility.createHeader(ExcelI.CARDGROUP_NAME, count++);
		ExcelUtility.createHeader(ExcelI.CARDGROUP_SETID, count++);
		ExcelUtility.createHeader(ExcelI.PROMO_CARDGROUP_NAME, count++);
		ExcelUtility.createHeader(ExcelI.PROMO_CARDGROUP_SETID, count);
		count = 0;
		while (QueryResult.next()) {
			try {
				if (QueryResult.getString(1).toString() != null) {
					++count;
					for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
						ExcelUtility.setCellData(QueryResult.getString(i).trim(), count, j);
					}
				}
			} catch (Exception e) {
				Log.info("Error While writing C2S Services & Sub Services Sheet");
				Log.writeStackTrace(e);
			}
		}
	}

	// Writes P2P Services and sub services to service Sheet
	public void WriteP2PServiceAndSubServices() throws SQLException {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		String NetworkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		ResultSet QueryResult = DBHandler.AccessHandler.fetchP2PServicesAndSubServices(NetworkCode);
		ResultSetMetaData rsMetaData = QueryResult.getMetaData();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET);
		int count = 0;
		for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
			ExcelUtility.createHeader(rsMetaData.getColumnName(i).toUpperCase(), j);
			count++;
		}
		ExcelUtility.createHeader(ExcelI.CARDGROUP_NAME, count++);
		ExcelUtility.createHeader(ExcelI.CARDGROUP_SETID, count++);
		ExcelUtility.createHeader(ExcelI.PROMO_CARDGROUP_NAME, count++);
		ExcelUtility.createHeader(ExcelI.PROMO_CARDGROUP_SETID, count);
		
		String value = DBHandler.AccessHandler.getSystemPreference("VMS_SERVICES");
		count = 0;
		while (QueryResult.next()) {
			try {
				if(BTSLUtil.isNullString(value)) {
					if (QueryResult.getString(1).toString() != null) {
						++count;
						for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
							ExcelUtility.setCellData(QueryResult.getString(i).trim(), count, j);
						}
					}
				}
				else {
				if (QueryResult.getString(1).toString() != null && !(value.contains(QueryResult.getString(1).toString()))) {
					++count;
					for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
						ExcelUtility.setCellData(QueryResult.getString(i).trim(), count, j);
					}
				}
		}
			} catch (Exception e) {
				Log.info("Error while writing P2P Services & Sub Services sheet");
				Log.writeStackTrace(e);
			}

		}
	}
	
	public void WriteP2PServiceAndSubServicesVoucher() throws SQLException {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		String NetworkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		ResultSet QueryResult = DBHandler.AccessHandler.fetchP2PServicesAndSubServicesforVoucher(NetworkCode);
		ResultSetMetaData rsMetaData = QueryResult.getMetaData();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.P2P_SERVICES_SHEET_VOUCHER);
		int count = 0;
		for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
			ExcelUtility.createHeader(rsMetaData.getColumnName(i).toUpperCase(), j);
			count++;
		}
		ExcelUtility.createHeader(ExcelI.CARDGROUP_NAME, count++);
		ExcelUtility.createHeader(ExcelI.CARDGROUP_SETID, count++);
		ExcelUtility.createHeader(ExcelI.PROMO_CARDGROUP_NAME, count++);
		ExcelUtility.createHeader(ExcelI.PROMO_CARDGROUP_SETID, count);
		
	
		count = 0;
		while (QueryResult.next()) {
			try {
				if (QueryResult.getString(1).toString() != null) {
					++count;
					for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
						ExcelUtility.setCellData(QueryResult.getString(i).trim(), count, j);
					}
				}
			} catch (Exception e) {
				Log.info("Error while writing P2P Services & Sub Services sheet for Voucher");
				Log.writeStackTrace(e);
			}

		}
	}
	
	
	//Generate Product Sheet
	public void WriteProductType() throws SQLException {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ResultSet QueryResult = DBHandler.AccessHandler.fetchProductType();
		ResultSetMetaData rsMetaData = QueryResult.getMetaData();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int count = 0;
		for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
			ExcelUtility.createHeader(rsMetaData.getColumnName(i).toUpperCase(), j);
			count++;
		}
		count = 0;
		while (QueryResult.next()) {
			try {
				if (QueryResult.getString(1).toString() != null) {
					++count;
					for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
						ExcelUtility.setCellData(QueryResult.getString(i).trim(), count, j);
					}
				}
			} catch (Exception e) {
				Log.info("Error while writing Product sheet");
				Log.writeStackTrace(e);
			}

		}
	}
	
	//Generate Staff user sheet
		public void WriteStaffUser() throws SQLException {
			MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.STAFF_USERS_SHEET);
			ExcelUtility.createHeader(ExcelI.OWNER_USER_NAME,ExcelI.PARENT_USER_NAME,ExcelI.CHANNEL_USER_NAME,ExcelI.STAFF_PARENT_LOGIN_ID,ExcelI.STAFF_USER_NAME,ExcelI.STAFF_LOGINID,ExcelI.STAFF_PASSWORD,ExcelI.STAFF_MSISDN, ExcelI.STAFF_PIN,ExcelI.STAFF_EMAIL_ID);
		}
		
	//Write Transfer rule type in Transfer Rule Sheet	
		public void writeTransferRuleType() {
			MasterSheetPath = _masterVO.getProperty("DataProvider");
	        String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	        int rowCount = ExcelUtility.getRowCount();
	        for(int i=1;i<=rowCount;i++){
	        	String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
	        	if(services.contains(C2CTransferCode)){
	        		if(services.contains("[P]")||services.contains("[S]"))
			    		ExcelUtility.setCellData(0, ExcelI.TRF_RULE_TYPE, i, "P");
	        		if(services.contains("[O]"))
	        			ExcelUtility.setCellData(0, ExcelI.TRF_RULE_TYPE, i, "O");
	        		if(services.contains("[D]"))
	        			ExcelUtility.setCellData(0, ExcelI.TRF_RULE_TYPE, i, "D");
	            }
	        }
	 }
		//jj
		@SuppressWarnings("unchecked")
		public void writeVOMSBundleSheet(String[] bundleProfiles, String VBName, String VBPrefix) {
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
			Log.info("Inserting into Bundles Sheet");
//			int row = ExcelUtility.getRowCount() + 1 ;
			int row = 1;
			ExcelUtility.setCellData(0, ExcelI.VOMS_BUNDLE_NAME, row, VBName);
			ExcelUtility.setCellData(0, ExcelI.VOMS_BUNDLE_PREFIX, row, VBPrefix);
			for (int i = 0, j = 1 ; i < bundleProfiles.length ; i++, j++) {
				ExcelUtility.setCellData(bundleProfiles[i], row, i+3);
			}
		}
		
		
//		@SuppressWarnings("unchecked")
//		public void prepareVOMSBundleSheetList(Object[] vomsDataObject) {
//			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
//			ExcelUtility.createHeader(ExcelI.VOMS_BUNDLE_NAME, ExcelI.VOMS_BUNDLE_PREFIX, ExcelI.VOMS_DENOMINATION_NAME, ExcelI.VOMS_PROFILE_NAME, ExcelI.VOMS_MRP);
//			Log.info("Inserting into Bundles Sheet");
//			for (int i = 0, j = 1 ; i < vomsDataObject.length ; i++, j++) {
//				HashMap<String,String> dataMap = (HashMap<String,String>) vomsDataObject[i];
//				ExcelUtility.setCellData(0, ExcelI.VOMS_BUNDLE_NAME, j, dataMap.get("voucherBundleName"));
//				ExcelUtility.setCellData(0, ExcelI.VOMS_BUNDLE_PREFIX, j, dataMap.get("voucherBundlePrefix"));
//				ExcelUtility.setCellData(0, ExcelI.VOMS_PROFILE_NAME, j, dataMap.get("activeProfile"));
//			}
//		}
		
		@SuppressWarnings("unchecked")
		public void prepareVOMSBundleSheet() {
			Log.info("Creating headers for " + ExcelI.VOMS_BUNDLES);
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
			ExcelUtility.createHeader(ExcelI.VOMS_BUNDLE_NAME, ExcelI.VOMS_BUNDLE_PREFIX, ExcelI.VOMS_MRP, ExcelI.VOMS_PROFILE_NAME);
		}
		
		@SuppressWarnings("unchecked")
		public void prepareVOMSProfileSheet(Object[][] vomsDataObject) {
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
			ExcelUtility.createHeader(ExcelI.VOMS_VOUCHER_TYPE, ExcelI.VOMS_TYPE, ExcelI.VOMS_SERVICE, ExcelI.VOMS_SUB_SERVICE, ExcelI.VOMS_USER_CATEGORY_NAME, ExcelI.VOMS_DENOMINATION_NAME, ExcelI.VOMS_SHORT_NAME, ExcelI.VOMS_MRP, ExcelI.VOMS_PAYABLE_AMOUNT, ExcelI.VOMS_PROFILE_NAME);
			for (int i = 0, j = 1; i < vomsDataObject.length; i++, j++) {
					HashMap<String, String> dataMap = (HashMap<String, String>) vomsDataObject[i][0];
					ExcelUtility.setCellData(0, ExcelI.VOMS_VOUCHER_TYPE, j, dataMap.get("voucherType"));
					ExcelUtility.setCellData(0, ExcelI.VOMS_TYPE, j, dataMap.get("type"));
					ExcelUtility.setCellData(0, ExcelI.VOMS_SERVICE, j, dataMap.get("service"));
					ExcelUtility.setCellData(0, ExcelI.VOMS_SUB_SERVICE, j, dataMap.get("subService"));
					ExcelUtility.setCellData(0, ExcelI.VOMS_USER_CATEGORY_NAME, j, dataMap.get("categoryName"));
			}
		}
		
		@SuppressWarnings("unchecked")
		public void prepareVOMSProfileSheetC2C(Object[][] vomsDataObject) {
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE_C2C);
			ExcelUtility.createHeader(ExcelI.VOMS_VOUCHER_TYPE, ExcelI.VOMS_TYPE, ExcelI.VOMS_SERVICE, ExcelI.VOMS_SUB_SERVICE, ExcelI.VOMS_USER_CATEGORY_NAME, ExcelI.VOMS_DENOMINATION_NAME, ExcelI.VOMS_SHORT_NAME, ExcelI.VOMS_MRP, ExcelI.VOMS_PAYABLE_AMOUNT, ExcelI.VOMS_PROFILE_NAME);
			for (int i = 0, j = 1; i < vomsDataObject.length; i++,j++) {
					HashMap<String, String> dataMap = (HashMap<String, String>) vomsDataObject[i][0];
					ExcelUtility.setCellData(0, ExcelI.VOMS_VOUCHER_TYPE, j, dataMap.get("voucherType"));
					ExcelUtility.setCellData(0, ExcelI.VOMS_TYPE, j, dataMap.get("type"));
					ExcelUtility.setCellData(0, ExcelI.VOMS_SERVICE, j, dataMap.get("service"));
					ExcelUtility.setCellData(0, ExcelI.VOMS_SUB_SERVICE, j, dataMap.get("subService"));
					ExcelUtility.setCellData(0, ExcelI.VOMS_USER_CATEGORY_NAME, j, dataMap.get("categoryName"));
			}
		}
		
		 public static String[] getAllowedVoucherTypesForScreen(String screen) {
		     
		        HashMap<String, String[]> screenWiseAllowedVoucherTypeMap = new HashMap<String, String[]>();
		        String[] allowedVoucherTypes = {PretupsI.VOUCHER_TYPE_DIGITAL, PretupsI.VOUCHER_TYPE_TEST_DIGITAL, 
		        		PretupsI.VOUCHER_TYPE_ELECTRONIC, PretupsI.VOUCHER_TYPE_TEST_ELECTRONIC, PretupsI.VOUCHER_TYPE_PHYSICAL, PretupsI.VOUCHER_TYPE_TEST_PHYSICAL}; 
		        
		        populateScreenWiseAllowedVoucherTypesMap(screen, screenWiseAllowedVoucherTypeMap);
		        
		        String[] tempAllowedVoucherTypes = screenWiseAllowedVoucherTypeMap.get(screen);
		        if(tempAllowedVoucherTypes != null) {
		            allowedVoucherTypes = tempAllowedVoucherTypes;
		        }
		                
		        return allowedVoucherTypes;
		    }
		 
		 public static void populateScreenWiseAllowedVoucherTypesMap(String screen, HashMap<String, String[]> screenWiseAllowedVoucherTypeMap) {
		     
		        String screenWiseAllowedVoucherTypePref = DBHandler.AccessHandler.getSystemPreference(PretupsI.SCREEN_WISE_ALLOWED_VOUCHER_TYPE);
		        if(BTSLUtil.isNullString(screenWiseAllowedVoucherTypePref)) {
		                     return;
		        }
		       
		        String[] screens = screenWiseAllowedVoucherTypePref.split(";");
		        for (int i = 0; i < screens.length; i++) {
		            if(BTSLUtil.isNullString(screens[i])) {
		                  return;
		            }
		            String[] screenWiseAllowedVoucherType = screens[i].split(PretupsI.COLON);
		            screenWiseAllowedVoucherTypeMap.put(screenWiseAllowedVoucherType[0], screenWiseAllowedVoucherType[1].split(PretupsI.COMMA));
		        }
		      
		    }
		@SuppressWarnings("unchecked")
		public void prepareVOMSProfileSheetForPhysical(Object[][] vomsDataObject) {
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PHYSICAL_VOMS_DENOM_PROFILE);
			ExcelUtility.createHeader(ExcelI.VOMS_VOUCHER_TYPE, ExcelI.VOMS_TYPE, ExcelI.VOMS_SERVICE, ExcelI.VOMS_SUB_SERVICE, ExcelI.VOMS_USER_CATEGORY_NAME, ExcelI.VOMS_DENOMINATION_NAME, ExcelI.VOMS_SHORT_NAME, ExcelI.VOMS_MRP, ExcelI.VOMS_PAYABLE_AMOUNT, ExcelI.VOMS_PROFILE_NAME);
			for (int i = 0, j = 1; i < vomsDataObject.length; i++, j++) {
				HashMap<String, String> dataMap = (HashMap<String, String>) vomsDataObject[i][0];
				ExcelUtility.setCellData(0, ExcelI.VOMS_VOUCHER_TYPE, j, dataMap.get("voucherType"));
				ExcelUtility.setCellData(0, ExcelI.VOMS_TYPE, j, dataMap.get("type"));
				ExcelUtility.setCellData(0, ExcelI.VOMS_SERVICE, j, dataMap.get("service"));
				ExcelUtility.setCellData(0, ExcelI.VOMS_SUB_SERVICE, j, dataMap.get("subService"));
				ExcelUtility.setCellData(0, ExcelI.VOMS_USER_CATEGORY_NAME, j, dataMap.get("categoryName"));
			}
		}
	
		@SuppressWarnings("unchecked")
		public void prepareVOMSProfileSheetForElectronic(Object[][] vomsDataObject) {
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.ELECTRONIC_VOMS_DENOM_PROFILE);
			ExcelUtility.createHeader(ExcelI.VOMS_VOUCHER_TYPE, ExcelI.VOMS_TYPE, ExcelI.VOMS_SERVICE, ExcelI.VOMS_SUB_SERVICE, ExcelI.VOMS_USER_CATEGORY_NAME, ExcelI.VOMS_DENOMINATION_NAME, ExcelI.VOMS_SHORT_NAME, ExcelI.VOMS_MRP, ExcelI.VOMS_PAYABLE_AMOUNT, ExcelI.VOMS_PROFILE_NAME);
			for (int i = 0, j = 1; i < vomsDataObject.length; i++, j++) {
				HashMap<String, String> dataMap = (HashMap<String, String>) vomsDataObject[i][0];
				ExcelUtility.setCellData(0, ExcelI.VOMS_VOUCHER_TYPE, j, dataMap.get("voucherType"));
				ExcelUtility.setCellData(0, ExcelI.VOMS_TYPE, j, dataMap.get("type"));
				ExcelUtility.setCellData(0, ExcelI.VOMS_SERVICE, j, dataMap.get("service"));
				ExcelUtility.setCellData(0, ExcelI.VOMS_SUB_SERVICE, j, dataMap.get("subService"));
				ExcelUtility.setCellData(0, ExcelI.VOMS_USER_CATEGORY_NAME, j, dataMap.get("categoryName"));
			}
		}
		
		public void WriteOperatorUserstoExcel(String type) throws SQLException {
			MasterSheetPath = _masterVO.getProperty("DataProvider");
			String SuperAdminLoginID = _masterVO.getMasterValue(MasterI.SUPERADMIN_LOGINID);
			String SuperAdminPassword = _masterVO.getMasterValue(MasterI.SUPERADMIN_PASSWORD);
			ResultSet QueryResult = DBHandler.AccessHandler.fetchOperatorUsers();
			if(type.equalsIgnoreCase("PHYSICAL")){
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PHY_OPERATOR_USERS_HIERARCHY_SHEET);
			}else if(type.equalsIgnoreCase("ELECTRONIC")){
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.ELC_OPERATOR_USERS_HIERARCHY_SHEET);
			}else if(type.equalsIgnoreCase("BATCH")){
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.BATCH_OPERATOR_USERS_HIERARCHY_SHEET);
			}
			int count = 1;
			ResultSetMetaData rsMetaData = QueryResult.getMetaData();
			for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
				ExcelUtility.createHeader(rsMetaData.getColumnName(i).toUpperCase(), j);
			}
			while (QueryResult.next()) {
				try {
					if (QueryResult.getString(1).toString() != null) {
						++count;
						for (int i = 1, j = 0; i <= rsMetaData.getColumnCount(); i++, j++) {
							ExcelUtility.setCellData(QueryResult.getString(i).toString(), count, j);
						}
					}
				} catch (Exception e) {
					Log.error("Error while creating Operator User Hierarchy sheet:");
					Log.writeStackTrace(e);
				}

			}
			String SuperAdminCatName = DBHandler.AccessHandler.getCategoryName(PretupsI.SUPERADMIN_CATCODE);
			ExcelUtility.createHeader(ExcelI.LOGIN_ID, 7);
			ExcelUtility.createHeader(ExcelI.PASSWORD, 8);
			ExcelUtility.createHeader(ExcelI.MSISDN, 9);
			ExcelUtility.createHeader(ExcelI.PIN, 10);
			ExcelUtility.createHeader(ExcelI.GROUP_ROLE, 11);
			ExcelUtility.setCellData(0, ExcelI.PARENT_CATEGORY_CODE, 1, PretupsI.SUPERADMIN_CATCODE);
			ExcelUtility.setCellData(0, ExcelI.PARENT_NAME, 1, SuperAdminCatName);
			ExcelUtility.setCellData(0, ExcelI.CATEGORY_CODE, 1, PretupsI.SUPERADMIN_CATCODE);
			ExcelUtility.setCellData(0, ExcelI.CATEGORY_NAME, 1, SuperAdminCatName);
			ExcelUtility.setCellData(0, ExcelI.LOGIN_ID, 1, SuperAdminLoginID);
			ExcelUtility.setCellData(0, ExcelI.PASSWORD, 1, SuperAdminPassword);
			
		}
		
		@SuppressWarnings("unchecked")
        public void writeVOMSBundleSheetForO2CTransfer(String[] bundleProfiles, String VBName, String VBPrefix, String mrp) {
               ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_BUNDLES);
               Log.info("Inserting into Bundles Sheet");
//             int row = ExcelUtility.getRowCount() + 1 ;
               int row = 1;
               ExcelUtility.setCellData(0, ExcelI.VOMS_BUNDLE_NAME, row, VBName);
               ExcelUtility.setCellData(0, ExcelI.VOMS_BUNDLE_PREFIX, row, VBPrefix);
               ExcelUtility.setCellData(0, ExcelI.VOMS_MRP, row, mrp);
               for (int i = 0, j = 1 ; i < bundleProfiles.length ; i++, j++) {
                     ExcelUtility.setCellData(bundleProfiles[i], row, i+3);
               }
        }

}
