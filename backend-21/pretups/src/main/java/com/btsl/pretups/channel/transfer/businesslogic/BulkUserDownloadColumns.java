package com.btsl.pretups.channel.transfer.businesslogic;

public enum BulkUserDownloadColumns {
	
	
	PARENT_LOGINID("bulkuser.xlsfile.bulkuserenquirydetails.parentloginid"),
	PARENT_MSISDN("bulkuser.xlsfile.bulkuserenquirydetails.parentmsisdn"),
	USERNAME_PREFIX("bulkuser.xlsfile.bulkuserenquirydetails.usernameprefix"),
	FIRSTNAME("bulkuser.xlsfile.bulkuserenquirydetails.firstname"),
	LASTNAME("bulkuser.xlsfile.bulkuserenquirydetails.lastname"),
	//USERNAME("bulkuser.xlsfile.bulkuserenquirydetails.username"),

	SHORT_NAME("bulkuser.xlsfile.bulkuserenquirydetails.shortname"),
	CATEGORY_CODE("bulkuser.xlsfile.bulkuserenquirydetails.categorycode"),
	EXTERNAL_CODE("bulkuser.xlsfile.bulkuserenquirydetails.externalcode"),
	CONTACT_PERSON("bulkuser.xlsfile.bulkuserenquirydetails.contactperson"),
	ADDRESS1("bulkuser.xlsfile.bulkuserenquirydetails.address1"), CITY("bulkuser.xlsfile.bulkuserenquirydetails.city"),
	STATE("bulkuser.xlsfile.bulkuserenquirydetails.state"), SSN("bulkuser.xlsfile.bulkuserenquirydetails.ssn"),
	COUNTRY("bulkuser.xlsfile.bulkuserenquirydetails.country"),
	// added by deepika aggarwal
	COMPANY("bulkuser.xlsfile.bulkuserenquirydetails.company"), FAX("bulkuser.xlsfile.bulkuserenquirydetails.fax"),
	// end
	LOGINID("bulkuser.xlsfile.bulkuserenquirydetails.loginid"),
	MOBILE_NUMBER("bulkuser.xlsfile.bulkuserenquirydetails.mobilenumber"),
	GEOGRAPHYCODE("bulkuser.xlsfile.bulkuserenquirydetails.geographycode"),
	SERVIECS("bulkuser.xlsfile.bulkuserenquirydetails.services"),
	COMMISSION_PROFILE("bulkuser.xlsfile.bulkuserenquirydetails.commisionprofile"),
	TRANSFER_PROFILE("bulkuser.xlsfile.bulkuserenquirydetails.transferprofile"),
	// fileArr[0][i++]="bulkuser.xlsfile.bulkuserenquirydetails.grade";
	OUTLET("bulkuser.xlsfile.bulkuserenquirydetails.outlet"),
	SUBOUTLET_CODE("bulkuser.xlsfile.bulkuserenquirydetails.suboutletcode"),
	STATUS("bulkuser.bulkuserenquirydetails.xlsfile.status"),
	GROUP_ROLE_CODE("bulkuser.xlsfile.bulkuserenquirydetails.grouprolecode"),

	// for Zebra and Tango by Sanjeew date 09/07/07
	GRADE("bulkuser.xlsfile.bulkuserenquirydetails.grade"),
	mcomorceflag("bulkuser.xlsfile.bulkuserenquirydetails.mcomorceflag"),
	MPAYPROFILEID("bulkuser.xlsfile.bulkuserenquirydetails.mpayprofileid"),
	LOWBALALERTALLOW("bulkuser.xlsfile.bulkuserenquirydetails.lowbalalertallow"),
	REMARKS("bulkuser.xlsfile.bulkuserenquirydetails.remarks");
	// End Zebra and Tango


	
	
	private String columnName;

	public String getColumnName() {
		return columnName;
	}

	private BulkUserDownloadColumns(String columnName) {
		this.columnName = columnName;
	}
	
	
	
	
	

}
