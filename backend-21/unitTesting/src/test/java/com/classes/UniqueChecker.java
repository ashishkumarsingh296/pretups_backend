package com.classes;

import java.util.ArrayList;
import java.util.Random;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils._parser;

/**
 * @author lokesh.kontey This class utilizes Random Generation Utility to
 *         generate Random Strings and check their Uniqueness through Database.
 */
public class UniqueChecker extends BaseTest{

	static String EXTCODE;
	static String LoginID;
	static String MSISDN;
	static String data;
	static String TCPName;
	static String CardGroupName;
	static String CPName;
	static String ShortCode;
	static String GradeName;
	static String GifterName;
	static String GradeCode;
	static String SubsSID;
	static String SubLookUpName;
	static String GroupRoleName;
	static String GroupRoleCode;
	static String InterfaceName;
	static String InterfaceExtID;
	static String NetworkName;
	static String NetworkCode;
	static String ServiceClassName;
	static String GatewayCode;
	static String DenominationName;
	static String shortName;
	static String MRP;
	static String profileName;
	static String userIDPrefix;
	static String voucherSNO;
	static String serialNo;
	static String voucherBundleName;
	static String voucherBundlePrefix;
	static String LPName;

	
	static RandomGeneration randStr = new RandomGeneration();
	static GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	
	/*
	 * Check for unique value by table and column 
	 */
	public static String UC_Table_Column(String tableName, String columnName, String valueAppender, int len) {
		String value;
		while (true) {			
			value =	valueAppender + randStr.randomNumberWithoutZero(len);				
			Log.info("The value is ::" + value);
			Boolean StatusUnique = DBHandler.AccessHandler.checkForUniqueValueByColumn(tableName, columnName, value);
			if (StatusUnique)
				break;		
		}
		return value;
	}
	
	//jj
	public static String UC_VBPREFIX() {
		while (true) {
			voucherBundlePrefix = randStr.randomNumeric(4);
			Log.info("The generated Voucher Bundle Prefix is : " + voucherBundlePrefix);
			String voucherBundleNameChecker = DBHandler.AccessHandler.checkForUniqueVBPrefix(voucherBundlePrefix);
			if(!voucherBundleNameChecker.equals("Y"))
				break;
		}
		return voucherBundlePrefix;
	}
	//jj
	public static String UC_VBNAME() {
		while (true) {
			voucherBundleName = "VB" + "_" + randStr.randomNumeric(4);
			Log.info("The generated Voucher Bundle Name is : " + voucherBundleName);
			String voucherBundleNameChecker = DBHandler.AccessHandler.checkForUniqueVBName(voucherBundleName);
			if(!voucherBundleNameChecker.equals("Y"))
				break;
		}
		return voucherBundleName;
	}

	/*
	 * @author lokesh.kontey This function is created to generate Random Login
	 * ID for users and Return Unique Login ID after checking in users table.
	 * 
	 * @returns Unique LoginID
	 */
	public static String UC_LOGINID() {

		while (true) {

			LoginID = "AUT" + "_" + randStr.randomNumeric(5);
			Log.info("The generated Login ID is ::" + LoginID);
			String LoginIDChecker = DBHandler.AccessHandler.checkForUniqueLoginID(LoginID);
			if (!LoginIDChecker.equals("Y"))
				break;
		}

		return LoginID;
	}

	/*
	 * @author lokesh.kontey This function is created to generate Random MSISDN
	 * for users and Return Unique MSISDN after checking in users table.
	 * 
	 * @returns Unique MSISDN
	 */
	public static String UC_MSISDN() {

		//String MasterSheetPath = _masterVO.getProperty("DataProvider");
		//ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.MASTER_SHEET_NAME);
		String Prefix = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX);//ExcelUtility.getCellData(13, 1);

		while (true) {
			MSISDN = Prefix + randStr.randomNumeric(gnMsisdn.generateMSISDN());
			Log.info("The generated MSISDN is ::" + MSISDN);
			String MSISDNStatus = DBHandler.AccessHandler.checkForUniqueMSISDN(MSISDN);
			if (!MSISDNStatus.equals("Y"))
				break;
		}

		return MSISDN;
	}
	
	/*
	 * @author krishan.chawla 
	 * This function is created to generate Random External Transaction No.
	 * for Operator to Channel Transfer.
	 * 
	 * @returns Unique External Transaction No.
	 */
	public static String UC_EXT_TXN_NO() {

		String extTxnNo;
		while (true) {
			extTxnNo = randStr.randomNumeric(5);
			Log.info("The generated extTxnNo is ::" + extTxnNo);
			String ExtTxnNoStatus = DBHandler.AccessHandler.checkForUniqueExternalTxnNum(extTxnNo);
			if (!ExtTxnNoStatus.equals("Y"))
				break;
		}

		return extTxnNo;
	}

	/*
	 * @author krishan.chawla This function is created to generate Random MSISDN
	 * for users and Return Unique MSISDN after checking in users table.
	 * 
	 * @returns Unique MSISDN
	 */
	public static String generate_subscriber_MSISDN(String SubscriberType) {

		if (SubscriberType.equals("Prepaid")) {
			String Prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
			MSISDN = Prefix + randStr.randomNumeric(gnMsisdn.generateMSISDN());
			Log.info("The generated MSISDN is :: " + MSISDN);
		} else if (SubscriberType.equals("Postpaid")) {
			String Prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_POSTPAID_PREFIX);
			MSISDN = Prefix + randStr.randomNumeric(gnMsisdn.generateMSISDN());
			Log.info("The generated MSISDN is :: " + MSISDN);
		}
		else if (SubscriberType.equals("InvalidNumeric")) {
			MSISDN = randStr.randomNumeric(gnMsisdn.generateMSISDN());
			Log.info("The generated MSISDN is :: " + MSISDN);
		}

		else if (SubscriberType.equals("AlphaNumeric")) {
			MSISDN = randStr.randomAlphaNumeric(gnMsisdn.generateMSISDN());
			Log.info("The generated MSISDN is :: " + MSISDN);
		}

		return MSISDN;
	}

	/*
	 * @author lokesh.kontey This function is created to generate Random
	 * External Code for users and Return Unique External Code after checking in
	 * users table.
	 * 
	 * @return Unique External Code
	 */
	public static String UC_EXTCODE() {

		while (true) {
			EXTCODE = randStr.randomNumeric(9);
			Log.info("The generated External Code is ::" + EXTCODE);
			String ExternalCodeStatus = DBHandler.AccessHandler.checkForUniqueEXTCODE(EXTCODE);
			if (!ExternalCodeStatus.equals("Y"))
				break;

		}
		return EXTCODE;
	}

	/**
	 * <h1>Generates unique random values for Domain Name, Domain Code and Short
	 * Name</h1>
	 * 
	 * @return
	 * @author Ayush Abhijeet
	 */
	public String[] UC_DomainData() {

		String[] domainData = new String[3];

		while (true) {

			String DomainCode = "AUT" + randStr.randomAlphaNumeric(4);
			domainData[0] = DomainCode;
			Log.info("The generated Domain Code is ::" + DomainCode);

			String DomainName = "AUT" + randStr.randomAlphaNumeric(5);
			domainData[1] = DomainName;
			Log.info("The generated Domain Name is ::" + DomainName);

			String DomainShortName = "AUT" + randStr.randomAlphaNumeric(3);
			domainData[2] = DomainShortName;
			Log.info("The generated Domain Short Name is ::" + DomainShortName);
			data = DBHandler.AccessHandler.checkUniqueDomain(DomainCode, DomainName, DomainShortName);
			if (!data.equals("Y"))
				break;
		}
		return domainData;
	}

	/**
	 * <h1>Generates Prefix not existing in database on the basis<br>
	 * of MSISDN_PREFIX_LENGTH to avoid infinite looping.</h1>
	 * <h1>modified by:</h1> Lokesh 24/11/2017 
	 * @return String
	 * @author Ayush Abhijeet 
	 * 
	 */
	public String UC_PrefixData() {
		String prefix=null;
        Random rand = new Random();
        ArrayList<String> list = listAllPossibleNum(SystemPreferences.MSISDN_PREFIX_LENGTH);
		while (list.size()>0) {
			int index = rand.nextInt(list.size());
			prefix = list.remove(index);
			Log.info("The generated Prefix is : " + prefix);
			data = DBHandler.AccessHandler.checkUniquePrefix(prefix);
			if (!data.equals("Y"))
				break;
		}
		if(list.size()==0){
			Log.skip("All the network prefixes are already consumed.");
			prefix = null;
		}
		return prefix;
	}
	
	public String maxDigitNum(int digit){
		String initializer, maxNumber=null;
		 initializer = "9";
		 maxNumber = "";
		for(int i=1;i<=digit;i++)
		{maxNumber=maxNumber+initializer;}
		return maxNumber;
	}
	
	public ArrayList<String> listAllPossibleNum(int digit){
		int max=Integer.parseInt(maxDigitNum(digit));
		Log.info("Maximum "+digit+" digit number: "+ max);
		
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i <= max; i++) {
        	String a= String.format("%0"+digit+"d", i);
            list.add(a);
        }
        return list;
	}
	
	public static String UC_TCPName() {
		while (true) {
			TCPName ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated TCP Name is ::" + TCPName);
			String TCPNameStatus = DBHandler.AccessHandler.checkForUniqueTCPName(TCPName);
			if (!TCPNameStatus.equals("Y"))
				break;
			
		}
		return TCPName;
	}
	
	public static String UC_CardGroupName() {
		while (true) {
			CardGroupName ="AUT"+randStr.randomAlphaNumeric(6);
			Log.info("The generated CardGroup Name is ::" + CardGroupName);
			String CardGroupNameStatus = DBHandler.AccessHandler.checkForUniqueCardGroupName(CardGroupName);
			if (!CardGroupNameStatus.equals("Y"))
				break;
			
		}
		return CardGroupName;
	}
	
	/*
	 * Unique commission profile name
	 */
	public static String UC_CPName() {
		while (true) {
			CPName ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated Commission Profile Name is ::" + CPName);
			String CPNameStatus = DBHandler.AccessHandler.checkForUniqueCommProfileName(CPName);
			if (!CPNameStatus.equals("Y"))
				break;
			
		}
		return CPName;
	}
	
	/*
	 * Unique commission profile short code
	 */
	public static String UC_ShortCode() {
		while (true) {
			ShortCode ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated Commission Profile ShortCode is ::" + ShortCode);
			String CPNameStatus = DBHandler.AccessHandler.checkForUniqueCommProfileShortCode(ShortCode);
			if (!CPNameStatus.equals("Y"))
				break;
			
		}
		return ShortCode;
	}
	
	/*
	 * Unique Grade name
	 */
	public static String UC_GradeName() {
		while (true) {
			GradeName ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated Grade Name is ::" + GradeName);
			String GradeNameStatus = DBHandler.AccessHandler.checkForUniqueGradeName(GradeName);
			if (!GradeNameStatus.equals("Y"))
				break;
			
		}
		return GradeName;
	}

	public static String UC_GifterName() {
		GifterName ="AUT"+randStr.randomAlphabets(5);
		Log.info("The generated Gifter Name is ::" + GifterName);
		return GifterName;
	}

	

	/*
	 * Unique Grade Code
	 */
	public static String UC_GradeCode() {
		while (true) {
			GradeCode ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated Grade Code is ::" + GradeCode);
			String GradeCodeStatus = DBHandler.AccessHandler.checkForUniqueGradeCode(GradeCode);
			if (!GradeCodeStatus.equals("Y"))
				break;
			
		}
		return GradeCode;
	}

	
	/*
	 * Unique Sub LookUp
	 */
	
	public static String UC_SubLookUpName() {
		while (true) {
			SubLookUpName ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated Sub Look Up Name is ::" + SubLookUpName);
			String SubLookUpNameStatus = DBHandler.AccessHandler.checkForUniqueSubLookUpName(SubLookUpName);
			if (!SubLookUpNameStatus.equals("Y"))
				break;
			
		}
		return SubLookUpName;
	}
	
	
	
	/*
	 * Unique SID Generation
	 */
	public static String UC_SubsSID(){
		String sidPrefixes = DBHandler.AccessHandler.getSystemPreference(CONSTANT.PRVT_RC_MSISDN_PREFIX_LIST);
		String[] strs = sidPrefixes.split("[,\\,]");
		Log.info("Substrings length:"+strs.length);           
		int prefixindex= new Random().nextInt(strs.length);;
		String prefixSID = strs[prefixindex];        
		int PreSIDLEN=prefixSID.length();       
		int SIDMAXLen= Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.MAX_SID_LENGTH));
		int remSIDLEN = 0;
	       if(SIDMAXLen > PreSIDLEN)
	       {remSIDLEN= SIDMAXLen - PreSIDLEN;}
	       else{Log.info("-------Maximum SID length is less than or equal to the SID prefix length------");}
	
		while(true){
			SubsSID = prefixSID +randStr.randomNumeric(remSIDLEN);
			String subSIDStatus = DBHandler.AccessHandler.checkForUniqueSubsSID(SubsSID);
			if (!subSIDStatus.equals("Y"))
						break;
		}
		return SubsSID;
	}
	
	/*
	 * Unique Group Role Name
	 */
	public static String UC_GroupRoleName() {
		while (true) {
			GroupRoleName ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated GroupRoleName is ::" + GroupRoleName);
			String GroupRoleNameStatus = DBHandler.AccessHandler.checkForUniqueGroupRoleName(GroupRoleName);
			if (!GroupRoleNameStatus.equals("Y"))
				break;
			
		}
		return GroupRoleName;
	}
	
	/*
	 * Unique Group Role Code
	 */
	public static String UC_GroupRoleCode() {
		while (true) {
			GroupRoleCode ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated GroupRoleCode is ::" + GroupRoleCode);
			String GroupRoleCodeStatus = DBHandler.AccessHandler.checkForUniqueGroupRoleCode(GroupRoleCode);
			if (!GroupRoleCodeStatus.equals("Y"))
				break;
			
		}
		return GroupRoleCode;
	}
	
	
	
	/*
	 * Unique Interface Name
	 */
	public static String UC_InterfaceName() {
		while (true) {
			InterfaceName ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated InterfaceName is ::" + InterfaceName);
			String InterfaceNameStatus = DBHandler.AccessHandler.checkForUniqueinterfaceName(InterfaceName);
			if (!InterfaceNameStatus.equals("Y"))
				break;
			
		}
		return InterfaceName;
	}
	
	
	/*
	 * Unique Interface External ID
	 */
	public static String UC_InterfaceExtID() {
		while (true) {
			InterfaceExtID ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated InterfaceExtID is ::" + InterfaceExtID);
			String InterfaceExtIDStatus = DBHandler.AccessHandler.checkForUniqueinterfaceExtID(InterfaceExtID);
			if (!InterfaceExtIDStatus.equals("Y"))
				break;
			
		}
		return InterfaceExtID;
	}
	
	
	
	
	/*
	 * Unique NetworkName
	 */
	public static String UC_NetworkName() {
		while (true) {
			NetworkName ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated NetworkName is ::" + NetworkName);
			String NetworkNameStatus = DBHandler.AccessHandler.checkForUniqueNetworkName(NetworkName);
			if (!NetworkNameStatus.equals("Y"))
				break;
			
		}
		return NetworkName;
	}	
	
	
	/*
	 * Unique NetworkCode
	 */
	
	public static String UC_NetworkCode() {
		while (true) {
			NetworkCode =randStr.randomAlphabets(2).toUpperCase();
			Log.info("The generated NetworkCode is ::" + NetworkCode);
			String NetworkCodeStatus = DBHandler.AccessHandler.checkForUniqueNetworkCode(NetworkCode);
			if (!NetworkCodeStatus.equals("Y"))
				break;
			
		}
		return NetworkCode;
	}
	
	
	public static ArrayList<String> UC_CARDGROUPID(ArrayList<String> generatedCardGroupIDs) {
		String cardGroupID;
		while (true) {
			cardGroupID = "AUT" + randStr.randomNumeric(5);
			if (!generatedCardGroupIDs.contains(cardGroupID))
				generatedCardGroupIDs.add(cardGroupID);
				break;
		}
		return generatedCardGroupIDs;
	}
	
	
	public static String UC_ServiceClassName() {
		while (true) {
			ServiceClassName ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated ServiceClassName is ::" + ServiceClassName);
			String ServiceClassNameStatus = DBHandler.AccessHandler.checkForUniqueServiceClassName(ServiceClassName);
			if (!ServiceClassNameStatus.equals("Y"))
				break;
			
		}
		return ServiceClassName;
	}
	
	
	/*
	 * Unique Interface Name
	 */
	public static String UC_ExternalTXNNum() {
		String extTxnNo = null;
		RandomGeneration RandomGeneration = new RandomGeneration();
		while (true) {
			extTxnNo = RandomGeneration.randomNumeric(10);
			Log.info("The generated External Txn Number is ::" + extTxnNo);
			String GatewayCodeStatus = DBHandler.AccessHandler.checkForUniqueGatewayCode(GatewayCode);
			if (!GatewayCodeStatus.equals("Y"))
				break;
			
		}
		return GatewayCode;
	}
	
	/*
	 * Unique Interface Name
	 */
	public static String UC_GatewayCode() {
		while (true) {
			GatewayCode ="AUT"+randStr.randomNumeric(5);
			Log.info("The generated GatewayCode is ::" + GatewayCode);
			String GatewayCodeStatus = DBHandler.AccessHandler.checkForUniqueGatewayCode(GatewayCode);
			if (!GatewayCodeStatus.equals("Y"))
				break;
			
		}
		return GatewayCode;
	}
	
	public static String UC_MSISDN_ALIAS(String prefixType) {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.MASTER_SHEET_NAME);
		String Prefix=null;
		if(prefixType.equalsIgnoreCase("POST")){
		Prefix = _masterVO.getMasterValue("Subscriber Postpaid Prefix");}
		else if(prefixType.equalsIgnoreCase("PRE")){
			Prefix = _masterVO.getMasterValue("Subscriber Prepaid Prefix");
		}

		while (true) {
			MSISDN = Prefix + randStr.randomNumeric(gnMsisdn.generateMSISDN());
			Log.info("The generated MSISDN is ::" + MSISDN);
			String MSISDNStatus = DBHandler.AccessHandler.checkForUniqueSubscriberAliasMSISDN(MSISDN);
			if (!MSISDNStatus.equals("Y"))
				break;
		}

		return MSISDN;
	}
	
	/*
	 * Unique Denomination Name
	 */
	
	public static String UC_VOMS_DENOM_NAME() {
		while (true) {
			DenominationName ="VOMS"+randStr.randomNumeric(4);
			Log.info("The generated Denomination Name is ::" + DenominationName);
			String DenominationStatus = DBHandler.AccessHandler.checkForUniqueDenominationName(DenominationName);
			if (!DenominationStatus.equals("Y"))
				break;
			
		}
		return DenominationName;
	}
	
	/*
	 * Unique Short Name
	 */
	
	public static String UC_VOMS_SHORTNAME() {
		while (true) {
			shortName ="ST"+randStr.randomNumeric(3);
			Log.info("The generated Short Name is ::" + shortName);
			String shortNameStatus = DBHandler.AccessHandler.checkForShortName(shortName);
			if (!shortNameStatus.equals("Y"))
				break;
			
		}
		return shortName;
	}
	
	/*
	 * Unique MRP
	 */
	
	public static String UC_VOMS_MRP() {
		while (true) {
			MRP = randStr.randomNumeric(3);
			Log.info("The generated mrp is ::" + MRP);
			
			if (Integer.parseInt(MRP) > 100) {
				String MRPStatus = DBHandler.AccessHandler.checkForMRP(Long.toString(_parser.getSystemAmount(MRP)));
				String mrpStatus = DBHandler.AccessHandler.checkForMRPFromProduct(Long.toString(_parser.getSystemAmount(MRP)));
				if (!MRPStatus.equalsIgnoreCase("Y") && !mrpStatus.equalsIgnoreCase("Y"))
				break;
			}
			
		}
		return MRP;
	}
	
	/*
	 * Unique Profile Name
	 */
	
	public static String UC_VOMS_ProfileName() {
		while (true) {
			profileName = "VOMS"+randStr.randomNumeric(4)+"PROF";
			Log.info("The generated mrp is ::" + profileName);
			String profileStatus = DBHandler.AccessHandler.checkForProfileName(profileName);
			if (!profileStatus.equals("Y"))
				break;
			
		}
		return profileName;
	}
	
	/*
	 * Unique Denomination Name
	 */
	
	public static String UC_VOMS_DENOM_NAME_2NumericDigits() {
		while (true) {
			DenominationName ="VOMS"+randStr.randomNumeric(2);
			Log.info("The generated Denomination Name is ::" + DenominationName);
			String DenominationStatus = DBHandler.AccessHandler.checkForUniqueDenominationName(DenominationName);
			if (!DenominationStatus.equals("Y"))
				break;
			
		}
		return DenominationName;
	}
	
	/*
	 * Unique Short Name
	 */
	
	public static String UC_VOMS_SHORTNAME_2NumericDigits() {
		while (true) {
			shortName ="ST"+randStr.randomNumeric(2);
			Log.info("The generated Short Name is ::" + shortName);
			String shortNameStatus = DBHandler.AccessHandler.checkForShortName(shortName);
			if (!shortNameStatus.equals("Y"))
				break;
			
		}
		return shortName;
	}
	
	/*
	 * Unique MRP
	 */
	
	public static String UC_VOMS_MRP_2Digits() {
		while (true) {
			MRP = randStr.randomNumeric(3);
			Log.info("The generated mrp is ::" + MRP);
			
				String MRPStatus = DBHandler.AccessHandler.checkForMRP(Long.toString(_parser.getSystemAmount(MRP)));
				if (!MRPStatus.equals("Y"))
				break;
			
			
		}
		return MRP;
	}
	
	/*
	 * 
	 * Unique userIDPrefix for Channel domain mgmt.
	 */
	
	public static String UC_UserIDPrefix() {
		while (true) {
			userIDPrefix = randStr.randomAlphabets(2).toUpperCase();
			Log.info("The generated userIDPrefix is ::" + userIDPrefix);
			String UserIDPrefixStatus = DBHandler.AccessHandler.checkForUniqueUserIDPrefix(userIDPrefix);
			if (!UserIDPrefixStatus.equals("Y"))
				break;
			
		}
		return userIDPrefix;
	}
	
	public static String UC_VOMSSerialNumber() {
		while (true) {
			voucherSNO = randStr.randomNumeric(Integer.parseInt(DBHandler.AccessHandler.getSystemPreference(CONSTANT.VOMS_SNO_MAX_LENGTH)));
			Log.info("The generated voucher sno is ::" + voucherSNO);
			String VoucherStatusUnique = DBHandler.AccessHandler.checkForUniqueVoucherSNO(voucherSNO);
			if (!VoucherStatusUnique.equals("Y"))
				break;
			
		}
		return voucherSNO;
	}
	
	public static String UC_SerialNumber() {
		while (true) {
			serialNo = randStr.randomNumberWithoutZero(5);
			Log.info("The generated sno is ::" + serialNo);
			String StatusUnique = DBHandler.AccessHandler.checkForUniqueSNOForVoucherGen(serialNo);
			if (!StatusUnique.equals("Y"))
				break;
			
		}
		return serialNo;
	}
	
	
	/*
	 * Unique Loan Profile name
	 */
	public static String UC_LPName() {
		while (true) {
			LPName ="AUTLP"+randStr.randomNumeric(5);
			Log.info("The generated Commission Profile Name is ::" + LPName);
			String LPNameStatus = DBHandler.AccessHandler.checkForUniqueLoanProfileName(LPName);
			if (!LPNameStatus.equals("Y"))
				break;
		}
		return LPName;
	}

	
	
}