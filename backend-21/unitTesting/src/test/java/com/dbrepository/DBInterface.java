
package com.dbrepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.businesscontrollers.UserTransferCountsVO;
import com.pretupsControllers.commissionprofile.CommissionProfileDetailsVO;
import com.pretupsControllers.commissionprofile.UserOTFCountsVO;

import restassuredapi.pojo.addpromoRuleRespPojos.CardGroupVO;
import restassuredapi.pojo.userprofilethresholdresponsepojo.UserVO;

/**
 * DBInterface Class
 * @author krishan.chawla
 * This class creates an Interface which is further implemented by Oracle & PostGreSQL classes for DBHandler
 */
public interface DBInterface {



	public ResultSet fetchOperatorUsers();
	
	public ResultSet fetchOperatorUsersVMSNetworkAdmin();
	
	public String getSystemPreference(String Preference_Code);
	public String getSystemPreferenceDefaultValue(String Preference_Code) ;

	public String getProductIDOfVoucherProfile(String voucherProfile) ;

	public String getMVDTransactionID(String serialNumber) ;

	public String getNetworkPreference(String Network_Code, String Preference_Code);

	public ResultSet getGeographicalDomainTypes();

	public ResultSet getDomainandCategories();

	public String checkForUniqueLoginID(String LoginID);

	public String checkForUniqueMSISDN(String MSISDN);

	public String getCardGroupSetID(String cardGroupName);

	public String checkForUniqueExternalTxnNum(String externalTxnNum);

	public String checkForUniqueEXTCODE(String EXTCODE);

	public String fetchUserPassword(String Login_ID);

	public String fetchTCPID(String TCPName);

	public String checkUniqueDomain(String DomainCode, String DomainName, String DomainShortName);

	public String getGradeName(String CategoryName);

	public String[] getDefaultGeographicalDomain(String NetworkCode, String domainTypeName);

	public String[] fetchCategoryCodeAndGeographicalDomainType(String CategoryName);

	public ResultSet fetchC2SServicesAndSubServices(String NetworkCode);

	public ResultSet fetchP2PServicesAndSubServices(String NetworkCode);
	
	public ResultSet fetchP2PServicesAndSubServicesforVoucher(String NetworkCode);

	public ResultSet fetchProductType();

	public ArrayList<String> getVoucherProductCodeList();

	public String fetchUserPIN(String Login_ID, String msisdn);

	public String getMSISDN(String CategoryName);

	public String getCardGroupStatus(String CardGroupName, int StartRange, int EndRange);
	
	public String getCardGroupName(String ModifiedBy, String SubService, String ServiceType);

	public int getNetworkProductSize(String NetworkCode);

	public String checkNetworkStockTransactionsForNetworkStockID(String TransactionID);

	public String existingLoginID();

	public String existingMSISDN();

	public String getNetworkName(String network);

	public List<String> getModuleList();

	public List<String> getModuleDescription(String module);

	public ResultSet getCategoriesWithNoO2CTransferRules(String Domain);

	public int maxPasswordBlockCount(String ControlCode);

	public Object[][] getProductsDetails(String NetworkCode, String WalletCode);

	public String getUserNameByLogin(String LoginID);

	public String checkUniquePrefix(String prefix);

	public List<String> getInterfaceList(String network);

	public String pinPreferenceForTXN(String categoryName);

	public String[] o2cApprovalLimits(String categoryName, String networkCode);

	public String getProductNameByCode(String ProductType);
	
	public String getProductCodeByShortCode(String shortCode);	

	public String getCategoryDetail(String ColumnName, String CategoryName);

	public String checkForUniqueTCPName(String TCPNAME);

	public String checkForUniqueCardGroupName(String cardGroupName);

	public String checkForUniqueCommProfileName(String CPNAME);

	public ResultSet getCommissionProfileDetails(String MSISDN, String ProductCode, String requestedQuantity);

	public Long getProductUnitValue(String ProductCode);

	public String webInterface(String categoryName);

	public String checkForUniqueGradeName(String GRADENAME);

	public String checkForUniqueGradeCode(String GRADECODE);

	public String getCommProfileVersion(String profileName);

	public boolean checkAmbiguousTransactions(String fromDate, String toDate, String service);

	public boolean checkAmbiguousO2CPendingTransactions(String fromDate, String toDate);
	
	public String fetchAmbiguousTransactions(String fromDate, String toDate, String service);

	public String fetchAmbiguousO2CPendingTransactions(String fromDate, String toDate);
	
	public String fetchTransferStatus(String transactionID);

	public String deletedMSISDN();

	public ResultSet getProductNameByType(String lookupType);

	public String getCategoryName(String CategoryCode);
	
	public String getDomainCodeCatgories(String CategoryCode);
	
	public String getPreference(String Control_Code, String Network_Code, String Preference_Code);

	public String fetchDomainName(String domain, String Network_Code,String parentGeography);

	public String fetchDomainNameCode(String domainType);

	public String getUserBalance(String productCode, String loginID);

	public String getUserBalanceWithLoginID(String loginID) ;
	
	public HashMap<String, String> getUserBalances(String LoginID_OR_MSISDN);

	public Object[][] getProductDetails(String NetworkCode, String DomainCode, String fromCategoryCode, String toCategoryCode, String type);

	public String getDomainCode(String domainName);

	public String getLookUpCode(String LookUpName);

	public int getLookUpSize(String LookupType);

	public String getLookUpName(String LookUpCode, String LookupType);

	public String checkForUniqueSubsSID(String SID);

	public String getsubscriberSIDviaMSISDN(String subsMSISDN);

	public String fetchSubscriberMSISDNRandomAlias(String msisdnType);

	public Object[][] getProductDetailsForC2S(String login_id, String service_type);

	public String getNamefromSystemPreference(String preferenceCode);

	public Object[][] getTransferProfileDetails();

	public String getUserId(String userName);
	public String getUserIdFromMsisdn(String msisdn);

	public String getLoginidFromMsisdn(String msisdn) ;


	public String getUsernameFromMsisdn(String msisdn) ;
	
	public String getUserIdLoginID(String userName);
	
	public String getGrpDomainCode(String userID);
	
	public String getGrpDomainName(String getGrpDomainCode);

	public String getChannelUserStatus(String userID);

	public String getTransactionStatusByKey(String Key, String Type);

	public HashMap<String, String> getTCPDetails(String profileID, String parentProfileID, String productCode, String ... details);

	public String getExecutedDate(String processID);

	public String getCategoryCode(String categoryName);

	public String getUserThresholdStatus(String userID, String type, String productCode);

	public String checkDateExistinCurrentmonth(String date);

	public String checkDateExistinCurrentweek(String date);

	public String checkDateIsCurrentdate(String date);

	public String[] getusertransfercountvalues(String username, String type);
	
	public UserTransferCountsVO getUserTransferCounts(String username_loginid_msisdn);

	public CommissionProfileDetailsVO loadCommissionProfileDetailsForOTF(String commProfileDetailID);

	public UserOTFCountsVO loadUserOTFCounts(String sendermsisdn, String detailId, Boolean addnl);

	public List<CommissionProfileDetailsVO> getBaseCommOtfDetails(String baseComProDetailId, boolean order);

	public String getApplicableDualCommissioningType(String msisdn);
	
	public String checkForC2STRANSFER_ID(String TRANSFER_ID);
	
	public String[] checkForOTFApplicable(String TRANSFER_ID);
	
	public long getAdditionalTax1Value(String TRANSFER_ID);

	public HashMap<String, String> getTransactionCRDRDetails(String transactionid);

	public HashMap<String, String> getNetworkPrefixDetails(String series, String series_type);

	public String checkForUniqueSubLookUpName(String SubLookUpName);

	public String getInterfaceID(String extID, String interfaceName);
	
	public String getServiceClassID(String ServiceClassName);

	public String checkForUniqueGroupRoleName(String GroupRoleName);

	public String checkForUniqueGroupRoleCode(String GroupRoleCODE);

	public Object[][] getChnlUserDetailsForRolecode(String rolecode, String domaincode);
	
	public Object[][] getChnlUserDetailsForRolecode(String rolecode, String domaincode, String categorycode);
	
	public String getCurrentServerDate(String dateFormat);

	public String checkForUniqueinterfaceExtID(String interfaceExtID);

	public String checkForUniqueinterfaceName(String interfaceName);

	public String checkForUniqueNetworkName(String NetworkName);

	public String checkForUniqueNetworkCode(String NetworkCode);
	
	public String checkForUniqueServiceClassName(String ServiceClassName);

	public String DeleteNetwork(String NetworkCode);
	
	public String[] getUserDetails(String loginID, String ... columnNames);
	
	public String[] getUserDetailsFromUserPhones(String loginID, String ... columnNames);
	
	public String[][] getO2CTransferDetails(String fromDate, String toDate, String dateformat, String domainCode, String geodomainCode, String ...columnNames);
	
	public String[][] getZeroBalSummRpt(java.sql.Date sqlStartDate, java.sql.Date sqlEndDate, String networkCode, String userID, String loggedInUserID, String parentCat, String userDomainCode, String geodomainCode, String ...columnNames);
	
	public String[][] getExternalRolesRpt( String networkCode, String userID, String loggedInUserID, String parentCat, String userDomainCode, String geodomainCode, String ...columnNames); 
	
	public String[][] getStaffSelfC2CRpt( java.sql.Date sqlStartDate, java.sql.Date sqlEndDate,String networkCode, String userID, String loggedInUserID, String parentCat, String userDomainCode, String geodomainCode, String ...columnNames); 
	
	public String[][] getAddCommDetailRpt(String fromDate, String toDate, String networkCode, String userID, String loggedInUserID, String parentCat, String userDomainCode, String geodomainCode, String ...columnNames);
	
	public String[][] getchnnlChnnlTrfDetailRpt(String fromDate, String toDate, String dateformat, String fromMSISDN, String toMSISDN, String ...columnNames);
	
	public String checkForUniqueGatewayCode(String GatewayCode);
	
	public String checkForUniqueSubscriberAliasMSISDN(String MSISDN);
	
	public String getCurrentServerTime(String timeFormat);
	
	public Object[][] getGatewayDetails(String gateways);
	
	public String getValuefromControlPreference(String Preference_Code);
	
	public String getTypefromSystemPreference(String Preference_Code);
	
	public String getValuefromNetworkPreference(String Preference_Code);
	
	public String getDailyLimitForAutoO2C(String userID);
	
	public String[][] getC2STransfer(String fromDate, String toDate, String dateformat, String domainCode, String geodomainCode, String ...columnNames);

	public String[][] getZeroBalanceCounterDetails(String thresholdType,String fromDate, String toDate, String msisdn, String dateformat,String geodomainCode, String domainCode, String parentCategoryCode,String userName, String[] columnNames);
	
	public String getProductName(String serviceType);
	
	public String checkForLangCode(String LANGCODE);

	public String checkForCountry(String language);
	
	public String getSelectorCode(String selectorName,String serviceType);
	
	public String checkForUniqueCommProfileShortCode(String ShortCode);
	
	public boolean deletionfrombarredMSISDN(String MSISDN);
	
	public String fetchDomainName(String domainCode);
	
	public String getDefaultGroupRoleName(String CategoryCode);
	
	public String getDefaultCardGroupStatus(String cardGroupName);
	
	public String[][] getUserBalanceMovementSummary(String parentCode,String zoneCode,String networkCode,String loginUserID,String domainCode,String fromDate, String toDate, String msisdn, String[] columnNames);

	public String getOTP(String MSISDN);
	
	public String getLatestOTP(String MSISDN);
	
	public String fetchdomainTypeName(String domainName);
	
	public String getServiceClass();
	
	public String[] getTypeOFPreference(String Control_Code, String Network_Code, String Preference_Code);
	
	public String getEmpCode(String loginID);
	
	public String checkForProfileName(String profileName);
	
	public String checkForUniqueDenominationName(String denominationName);
	
	public String checkForShortName(String shortName);
	
	public String checkForMRP(String MRP);
	
	public String checkForMRPFromProduct(String MRP);
	
	public Object[][] getVOMSDetails();
	
	public Object[][] getVOMSDetailsC2C(List l1);
	
	public int getSubServiceCount(String voucherType, String service);

	public Object[][] getServiceClassID(String serviceClassName, String serviceClassCode);
	
	public String fetchUserGeographyCount(String login_id);
	
	public String getCardGroupStartRange(String CardGroupSetID);
	
	public String getCardGroupEndRange(String CardGroupSetID);
	
	public String getNetworkCode(String network);
	
	public String getTransactionID(String MSISDN);

    public String fetchProductID(String productName);
    
    public String getVoucherSegment(String productID);
	
	public String fetchBatchType(String productID);
	
	public String[] getVoucherBatchDetails(String productID);
	
	public String fetchBatchTypeFromSerialNo(String productID, String fromSerialNo, String toSerialNo);

	public String getMinSerialNumber(String productID, String status);
	
	public String getMinSerialNumberuserID(String productID, String status, String userID);
	
	public String getMaxSerialNumber(String productID, String status);
	
	public String getMaxSerialNumberWithuserid(String productID, String status,String userid);

	public void changeStatusSerialNumber(String serialNumber, String status);
	
	public String getExpiryDate(String fromSerialNo);
	
	public String getPinFromSerialNumber(String serialNumber);
	
	public String getPinBlockCount(String msisdn);
	
	public String getVoucherStatus(String serialNumber);
	
	public String getVoucherSummaryDate();
	
	public String getcontroltransferlevel(String type,String fromCategoryCode, String toCategoryCode);
	
	public String[] getParentUserDetails(String childmsisdn, String ... columnNames);
	
	public String[] getOwnerUserDetails(String childmsisdn, String ... columnNames);
	
	public String getSerialNumberFromStatus(String status);
	
	public String getSerialNumberFromStatusAndVoucherType(String status,String voucherType);
	
	public String  getSerialNumberForExpiredDate(String status);
	
	public String getSubscriberMSISDN(String ProductCode);
	
	public String getSubscriberMSISDN();

	public String checkSubscriberMSISDNexist(String MSISDN);
	
	public String getSubscriberP2PPin(String MSISDN);
	
	public String getP2PSubscriberMSISDN(String SubType,String status);
	
	public String getP2PSubscriberMSISDNSeq(String SubType,String status,String seq);
	
	public boolean checkAmbiguousTransactionsforP2P(String fromDate, String toDate, String service);
	
	public String fetchAmbiguousTransactionsforP2P(String fromDate, String toDate, String selectorType);
	
	public String fetchTransferStatusforP2P(String transactionID);

	public String[] getP2PSubscriber(String ... columnNames);
	
	public String[] getP2PSubscriberWithStatusY(String ... columnNames);
	
	public String[] getP2PSubscriberWithStatusS(String ... columnNames);
	
	public String getSubscriberMSISDNFrombarredlist(String module);
	
	public String getSerialNumber(String productID);
	
	public String[] getVomsVoucherDetailsFromSerialNumber(String serialNumber, String ... columnNames);
	
	public String getProductNameFromVOMSProduct(String productID);
	
	public String getCategoryIDFromVOMSProduct(String productName);
	
	public String getProducTypeFromVOMSCategory(String categoryID);

	public String getBatchNumber(String productID, String status);

	public String[] getUserDetails_combined(String loginID,String MSISDN, String ... columnNames);
	
    public String getVoucherType(String typeCode);
	
    public String getType(String typeCode);
	
    public Object[][] getVOMSDetailsBasedOnVoucherType(String voucherType);

	public String getCommProfileDetailsID(String profileName, String productCode, String version);
	
	public String getOTFValue(String username , String profileDetailsID);
	
	public String fetchTransferStatusO2C(String transactionID);
	
	public String fetchTransferIdWithStatus(String status,String subType,String Type);
	
	public String[] preferenceModifyAllowed(String Preference_Code);

	public String[] defaultTCP(String categoryCode);
	
	public String[] defaultCommission(String categoryCode,String networkCode);
	
	public String[] defaultGrade(String categoryCode);
	
	public void updateAnyColumnValue(String tableName,String columntomodify,String valueColumntomodify,String columntorefer,String valueofcolumntorefer);
	
	public void updateAnyColumnDateValue(String tableName,String columntomodify,Date valueColumntomodify,String columntorefer,String valueofcolumntorefer);

	public String existingEXTCODE();
	
	public String SubscriberStatus(String subMSISDN);
	
	public String SubscriberBlacklistStatus(String subMSISDN);
	
	public String checkForSubscriberExistence(String subMSISDN);
	
	public String check_PIN_REQUIRED(String prefCode);
	
	public String get_post_balance(String TXNID);
	
	public String[] fetchRoleName(String pageCode);
	
	public String get_department_name(String loginID);
	
	public String get_division_name(String loginID);
	
	public String getNetworkPrefix(String seriesType, String status);
	
	public String getOtherNetworkPrefix(String operator, String status);
	
	public String getdivisionCode(String div_name);
	
	public String getdepartmentCode(String dept_name);
	
	public String getCardGroupVersion(String CardGroupName);
	
	public String getCardGroupVersionActive(String CardGroupName);
	
	public String getUserSumBalance(String loginID);
	
	public String[] getdetailsfromUsersTable(String loginID_OR_MSISDN, String ... columnNames);
	
	public String getCellGroupCode();
	
	public String checkForUniqueUserIDPrefix(String userIDPrefix);
	
	public String checkForUniqueVoucherSNO(String voucherSNO);
	
	public String checkForUniqueSNOForVoucherGen(String voucherSNO);
	
	public String[] getP2PSubscriberWithRequestStatusU(String ... columnName);
	
	public String getConsentOTP(String MSISDN1,String MSISN2, String TXNID);
	
	public ResultSet fetchVoucherBundleDetails(String VBName);
	
	public String fetchMRPforBundle(String VBName);
	
	public String checkForUniqueVBPrefix(String VBPrefix);
	
	public String checkForUniqueVBName(String VBName);

	public String getSerialNumberFromStatusAndUserId(String status, String msisdn);
	
	public String getSerialNumberAssignedToUser(String status,String networkCode);
	
	public String getVoucherTypeForUser (String msisdn);

	public int rollbackDateForProcess(String processId);
	
	public Boolean isMultipleNetworkEnabled();
	
	public Boolean isVomsBatchWithStatusPresent(String processId, String batchType, String status);
	
	public ResultSet fetchVouchersFromTxnId(String txnId);
	
	public String getCommProfileID(String id);
	
	public String getTransactionIDO2C(String status);
	
	public String getNetworkPrefixFromNetwork(String networkCode,String status);
	
	public String getLookUpCodeFromType(String lookupType);
	
	public String getTransferProfileID(String nwcode,String catcode,String parentid);
	
	public String getCategoryDetails(String column,String catCode);
    
	public String getGradeCode(String gradeName);
	
	public String getGrpDomainCodeFromName(String gradeCode);
	
	public String getTransactionIDStatus(String txnid);
	
	public String getC2CTransactionID(Boolean isFileRequired);
	
	public Boolean checkForUniqueValueByColumn(String tableName, String columnName, String value); 	
	
	public ResultSet  getLookupByType(String lookupType);
	
	public ResultSet getOtherCommissionProfileDetails(String MSISDN, String ProductCode, String requestedQuantity);
	
	public HashMap<String,String> getOptChannelTransferRule(String loginORmsisdn);
	
	public String getGeoCode(String geography);

	/* Added by yash.gupta */

	public String fetchScheduleStatus(String transactionID);

	public String fetchSoldStatus(String C2SStatus);

	public String fetchBulkSoldStatus(String batchID);

	public String getLookUpNameByCode(String LookUpCode);

	public String getVomsProductExpiry(String productName);

	public String getParentGeoDomCode(String geoDomName);

	/* Added by Ashmeet.Saggu */

	public String getSystemPreferenceMAXValue(String Preference_Code) ;

	public String getSystemPreferenceMINValue(String Preference_Code) ;
	
	public int getNetPayableAmt(String transferId);
	

	public boolean checkEnabledElectronicVoucherAvailable(String mrp) ;

	public List<String> getMultipleEnabledVoucherSerialNumber(String mrp ,int numberOfVocuhers) ;


	public String getVoucherName(String voucherType);
	
	public Object[][] fetchCurrencyCodes();

	public String fetchTransferStatusFOC(String transactionID);

	public String getParentGeographicDomainCode(String geographicalDomainName) ;

	public String getGeographicDomainName(String geographicalDomainCode) ;
	
	public String getExternalCodeFromMsisdn(String msisdn);
	
	public String getOfflineDownloadedReportTaskID();
	
	public String getChannelTransfersTxnId(String senderCategoryCode , String msisdn);
	
	public String checkForUniqueLoanProfileName(String LPNAME);
	
	public String getLoanGiven(String userID);

	public String getUserIDFromMSISDN(String MSISDN);

	public String getValuefromControlCodeControlPreference(String Preference_Code, String ControlCode);
	
	public List<String> getUserRoles(String userId, String catCode);
	
	public List<String> fetchUserServicesTypes(String userId);
	
	public String getFirstNameByLoginId(String loginId);
	
	public String getShortNameByLoginId(String loginId);
	
	public String getEmailIdByLoginId(String loginId);
	
	public List<String> getParentLoginIdsHavingActiveChildUsers();
	
	public List<String> getLoginIdsHavingPendingTransactions();
	
	public List<String> getLoginIdsHavingAssociatedRestrictedMsisdnList();
	
	public List<String> getLoginIdsHavingPendingFOCtransactions();
	
	public List<String> getMsisdnHavingOnGoingBatchRechargeScheduled();
	
	public List<String> getMsisdnWithPendingBatchFOCApproval();
	
	public String getUserInfo(String columnName, String categoryCode, String userType);
	
	public void deleteChannelUser(String userId);
	
	public List<String> fetchParentServicesTypes(String parentUserId);
	
	public List<String> getOperatorRoles(String categoryCode);
	
	public List<String> fetchOperatorServices(String senderNtwCode , String receiverNtwCode);
	
	public List<String> fetchSuperChannelAdminServices(String moduleCode, String senderNtwCode, String receiverNtwCode, 
			String catCode);
	
	public List<String> getGeographicalDomainCodeListBasedOnGeoType(String geoDomainType);
	
	public List<String> getDomainCodes(String userType);
	
	public String getColumnValueFromTable(String columnName, String tableName, String columnToRefer, String columnValue);
	
	public boolean insertDomain(String domainCode,String domainName);
	public boolean insertCategory(String domainCode,String categoryCode, String categoryName);
	public boolean deleteCategory(String categoryCode);
	public boolean deleteDomain(String domainCode);
	public String getUserNetworkByLoginID(String loginID);
	public String getTProfileIDbyProfileName(String profieName);
	public UserVO loadValidUerIDs(String geographicalDomainCode, String networkCode, String categoryCode)  ;
	public CardGroupVO getCardGroupSetVO( String networkCode, String moduleCode,String setType) ;
	public boolean deleteTransferRule(String networkCode,String sendSubcType,String receiverSubcType,String senderServiceClassID,
			String receiverServiceClassID,String subservice,String serviceType,String ruleLevel);
	public String getServiceCardGroupid(String networkCode);
	public String getServiceClassIDByInterface(String p_interfaceCategory);
	public String getServiceType(String networkCode) ;
	
}


