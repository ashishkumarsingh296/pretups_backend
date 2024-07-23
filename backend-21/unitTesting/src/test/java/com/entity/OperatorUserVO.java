package com.entity;

import java.util.ArrayList;

import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class OperatorUserVO {

	private String categoryCode,parentName, firstName, lastName, subscriberCode, MSISDN, EXTCODE,
	contactNo, address1, address2, city, state, country, email, 
	LOGINID, PASSWORD, designation, SSN, voucherType, geodomaincode, 
	userNamePrefix, statusCode, divisionCode, deptCode, networkcode, 
	roleType, domainCode, productType, roleCode;
	
	private int batchRow, numberOfOperatorUsers;
	
	public OperatorUserVO(String catCode, String prntName) {
		
		categoryCode = catCode;
		parentName = prntName;
		//Assigning role codes
		ArrayList<String> roles=ExtentI.columnbasedfilter(_masterVO.getProperty("RolesSheet"),ExcelI.LINK_SHEET1, ExcelI.CATEGORY_CODES, catCode, ExcelI.ROLE_CODES);
		boolean multipleroles=false;
		roleCode="";
		for(int i=0;i<roles.size();i++){
			if(roles.size()>0){
				roleCode=roleCode+roles.get(i)+",";
				multipleroles=true;}
			else if(roles.size()==0)
				roleCode=roles.get(i);
			else Log.info("No roles found.");
		}
		if(multipleroles){
			roleCode=roleCode.substring(0,roleCode.length()-1);}
		
		//Selecting geography to be associated with the operator user
		String[] column=new String[]{ExcelI.CATEGORY_CODE};
		String[] values=new String[]{catCode};
		int combinationAtRow = ExtentI.combinationExistAtRow(column, values, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		String geography = ExtentI.fetchValuefromDataProviderSheet(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.GRPH_DOMAIN_TYPE, combinationAtRow);
		if(geography.equals("NW"))
			geodomaincode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
		else
		{String[] ncolumn = new String[]{ExcelI.GRPH_DOMAIN_TYPE};
		String[] nvalues = new String[]{geography};
		int grphRow =ExtentI.combinationExistAtRow(ncolumn, nvalues, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
		String grphTypeName = ExtentI.fetchValuefromDataProviderSheet(ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET, ExcelI.GRPH_DOMAIN_TYPE_NAME, grphRow);
		int grphRow1 =ExtentI.combinationExistAtRow(new String[]{ExcelI.DOMAIN_TYPE_NAME}, new String[]{grphTypeName}, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		String grphCode = ExtentI.fetchValuefromDataProviderSheet(ExcelI.GEOGRAPHICAL_DOMAINS_SHEET, ExcelI.DOMAIN_CODE, grphRow1);
		geodomaincode=grphCode.toUpperCase();
		}
		
		setCommonData();
	}
	
	
	private void setCommonData() {
		RandomGeneration randStr = new RandomGeneration();
		
		batchRow = 5;
		numberOfOperatorUsers = Integer.parseInt(_masterVO.getProperty("NumberOfUsersForEachOptCategory"));
		firstName = "AUTFN" + randStr.randomNumeric(4);
		lastName =  "AUTLN" + randStr.randomNumeric(4);
		subscriberCode =  ""+ randStr.randomNumeric(6);
		MSISDN = UniqueChecker.UC_MSISDN();
		EXTCODE =  UniqueChecker.UC_EXTCODE();
		contactNo =  "" + randStr.randomNumeric(6);
		address1 = "Add1" + randStr.randomNumeric(4);
		address2 = "Add2" + randStr.randomNumeric(4);
		city =  "City" + randStr.randomNumeric(4);
		state = "State" + randStr.randomNumeric(4);
		country = "Country"+ randStr.randomNumeric(2);
		email = randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com";
		LOGINID = UniqueChecker.UC_LOGINID();
		PASSWORD = _masterVO.getProperty("Password");
		designation="";
		SSN="";
		voucherType="electronic";
		userNamePrefix="MR";
		statusCode="Y";
		divisionCode=DBHandler.AccessHandler.getdivisionCode(ExtentI.fetchValuefromDataProviderSheet(ExcelI.DIVISION_DEPT_SHEET, ExcelI.DIVISION, 1));
		deptCode=DBHandler.AccessHandler.getdepartmentCode(ExtentI.fetchValuefromDataProviderSheet(ExcelI.DIVISION_DEPT_SHEET, ExcelI.DEPARTMENT, 1));
		networkcode=_masterVO.getMasterValue(MasterI.NETWORK_CODE);
		roleType="N";
		
		domainCode = "";
		ArrayList<String> list = ExtentI.fetchUniqueValuesFromColumn(
				ExcelI.CHANNEL_USER_CATEGORY_SHEET, ExcelI.DOMAIN_CODE);
		for (String x : list) {
			domainCode = domainCode + x + ",";
		}
		domainCode = domainCode.substring(0, domainCode.length() - 1);

		productType = "";
		int rowC = ExcelUtility.getRowCount(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		for (int rowNum = 1; rowNum <= rowC; rowNum++) {
			productType = productType
					+ ExtentI.fetchValuefromDataProviderSheet(ExcelI.PRODUCT_SHEET, ExcelI.PRODUCT_TYPE, rowNum)+ ",";
		}
		productType = productType.substring(0, productType.length() - 1);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSubscriberCode() {
		return subscriberCode;
	}

	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}

	public String getMSISDN() {
		return MSISDN;
	}

	public void setMSISDN(String mSISDN) {
		MSISDN = mSISDN;
	}

	public String getEXTCODE() {
		return EXTCODE;
	}

	public void setEXTCODE(String eXTCODE) {
		EXTCODE = eXTCODE;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLOGINID() {
		return LOGINID;
	}

	public void setLOGINID(String lOGINID) {
		LOGINID = lOGINID;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getSSN() {
		return SSN;
	}

	public void setSSN(String sSN) {
		SSN = sSN;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getGeodomaincode() {
		return geodomaincode;
	}

	public void setGeodomaincode(String geodomaincode) {
		this.geodomaincode = geodomaincode;
	}

	public String getUserNamePrefix() {
		return userNamePrefix;
	}

	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getNetworkcode() {
		return networkcode;
	}

	public void setNetworkcode(String networkcode) {
		this.networkcode = networkcode;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}


	public String getCategoryCode() {
		return categoryCode;
	}


	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}


	public int getBatchRow() {
		return batchRow;
	}


	public void setBatchRow(int batchRow) {
		this.batchRow = batchRow;
	}


	public String getParentName() {
		return parentName;
	}


	public void setParentName(String parentName) {
		this.parentName = parentName;
	}


	public int getNumberOfOperatorUsers() {
		return numberOfOperatorUsers;
	}


	public void setNumberOfOperatorUsers(int numberOfOperatorUsers) {
		this.numberOfOperatorUsers = numberOfOperatorUsers;
	}

	
}
