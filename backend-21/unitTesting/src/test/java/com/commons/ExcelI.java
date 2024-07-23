package com.commons;

public class ExcelI {

    // Excel Sheets
    public static final String MASTER_SHEET_NAME = "Master Sheet";
    public static final String TRANSFER_MATRIX_SHEET = "Transfer Matrix";
    public static final String ACCESS_BEARER_MATRIX_SHEET = "Access Bearer Matrix";
    public static final String OPERATOR_USERS_HIERARCHY_SHEET = "Operator Users Hierarchy";
    public static final String OPERATOR_USERS_NETWORK_ADMIN_HIERARCHY_SHEET = "Network Admin Hierarchy Sheet";
    public static final String CHANNEL_USERS_HIERARCHY_SHEET = "Channel Users Hierarchy";
    public static final String GEOGRAPHY_DOMAIN_TYPES_SHEET = "Geography Domain Types";
    public static final String CHANNEL_USER_CATEGORY_SHEET = "Channel User Category Sheet";
    public static final String TRANSFER_RULE_SHEET = "Transfer Rule Sheet";
    public static final String C2S_SERVICES_SHEET = "C2S Services Sheet";
    public static final String P2P_SERVICES_SHEET = "P2P Services Sheet";
    public static final String P2P_SERVICES_SHEET_VOUCHER= "P2P Services Sheet Voucher";
    public static final String PRODUCT_SHEET = "Product Sheet";
    public static final String DIVISION_DEPT_SHEET = "DIVISIONDEPT";
    public static final String GEOGRAPHICAL_DOMAINS_SHEET = "Geographical Domains";
    public static final String STAFF_USERS_SHEET = "Staff Users Sheet";
    public static final String LINK_SHEET1 = "Sheet1";
    public static final String VOMS_DENOM_PROFILE = "Voms_Denom_Profile";
    public static final String VOMS_DENOM_PROFILE_API = "Voms_Denom_Profile_API";
    public static final String VOMS_DENOM_PROFILE_C2C = "Voms_Denom_Profile_C2C";
    public static final String PHYSICAL_VOMS_DENOM_PROFILE = "Physical_Voms_Denom_Profile";
    public static final String ELECTRONIC_VOMS_DENOM_PROFILE = "Electronic_Voms_Denom_Profile";
    public static final String EXTGW_CHANNEL_USERS_HIERARCHY_SHEET = "EXTGW Channel Users Hierarchy";
    public static final String PHY_OPERATOR_USERS_HIERARCHY_SHEET = "PHY Operator Users Hierarchy";
    public static final String ELC_OPERATOR_USERS_HIERARCHY_SHEET = "ELC Operator Users Hierarchy";
    public static final String BATCH_OPERATOR_USERS_HIERARCHY_SHEET = "BATCH Operator Users Hierarchy";
    public static final String VOMS_BUNDLES = "VOMS BUNDLES";
    public static final String BUNDLE_NAME = "BUNDLE_NAME";
    
    /* *** MasterSheet Columns *** */
    public static final String MASTER_DETAILS = "Master Details";
    public static final String VALUES = "Values";
    public static final String DESCRIPTION = "Description";
    
    
    /* *** Operator Users Hierarchy Sheet Columns *** */
    public static final String PARENT_CATEGORY_CODE = "PARENT_CATEGORY_CODE";
    public static final String PARENT_NAME = "PARENT_NAME";
    public static final String CATEGORY_CODE = "CATEGORY_CODE";
    public static final String CATEGORY_NAME = "CATEGORY_NAME";
    public static final String DOMAIN_CODE = "DOMAIN_CODE";
    public static final String SEQUENCE_NO = "SEQUENCE_NO";
    public static final String GRPH_DOMAIN_TYPE = "GRPH_DOMAIN_TYPE";
    public static final String LOGIN_ID = "LOGIN_ID";
    public static final String PASSWORD = "PASSWORD";
    public static final String MSISDN = "MSISDN";
    public static final String PIN = "PIN";
    
    /* *** Channel Users Hierarchy Sheet Columns *** */
    public static final String DOMAIN_NAME = "DOMAIN_NAME";
    public static final String PARENT_CATEGORY_NAME = "PARENT_CATEGORY_NAME";
    public static final String USER_NAME = "USER_NAME";
    public static final String EXTERNAL_CODE = "EXTERNAL_CODE";
    public static final String GEOGRAPHY = "GEOGRAPHY";
    public static final String GRADE = "GRADE";
    public static final String CARDGROUP_NAME = "CARDGROUP_NAME";
    public static final String LOGIN_ID_CHANNELUSER = "LOGIN_ID";
    public static final String PROMO_CARDGROUP_NAME = "PROMO_CARDGROUP";

    public static final String SA_TCP_NAME = "SA_TCP_NAME";
    public static final String SA_TCP_PROFILE_ID = "SA_TCP_PROFILE_ID";
    public static final String NA_TCP_NAME = "NA_TCP_NAME";
    public static final String NA_TCP_PROFILE_ID = "NA_TCP_PROFILE_ID";
    public static final String COMMISSION_PROFILE = "COMMISSION_PROFILE";
    public static final String DUAL_COMMISSION_TYPE = "DUAL_COMMISSION_TYPE";
    public static final String ADDITIONAL_COMMISSION = "ADDITIONAL_COMMISSION";
    public static final String GROUP_ROLE = "GROUP_ROLE";
    public static final String NETWORK_CODE = "NETWORK_CODE";
    public static final String OTHER_COMMISSION_TYPE = "OTHER_COMM_TYPE";
    public static final String OTHER_COMMISSION_VALUE = "OTHER_COMM_VALUE";
    public static final String OTHER_COMMISSION_PROFILE = "OTHER_COMM_PROF";
	public static final String LOAN_PROFILE = "LOAN_PROFILE";
    
    /* *** Geographical Domain Types Sheet Columns *** */
    public static final String GRPH_DOMAIN_TYPE_NAME = "GRPH_DOMAIN_TYPE_NAME";
    public static final String GRPH_DOMAIN_PARENT = "GRPH_DOMAIN_PARENT";
    
    /* *** Transfer Rule Sheet *** */
    public static final String INDEX = "INDEX";
    public static final String FROM_DOMAIN = "FROM_DOMAIN";
    public static final String FROM_CATEGORY = "FROM_CATEGORY";
    public static final String TO_DOMAIN = "TO_DOMAIN";
    public static final String TO_CATEGORY = "TO_CATEGORY";
    public static final String SERVICES = "SERVICES";
    public static final String STATUS = "STATUS";
    public static final String ACCESS_BEARER = "ACCESS_BEARER";
    public static final String TRF_RULE_TYPE = "C2C_TRF_RULE_TYPE";
    
    /* *** C2S Services Sheet *** */
    public static final String SERVICE_TYPE = "SERVICE_TYPE";
    public static final String NAME = "NAME";
    public static final String SELECTOR_NAME = "SELECTOR_NAME";
    public static final String CARDGROUP_SETID = "CARDGROUP_SETID";
    public static final String PROMO_CARDGROUP_SETID = "PROMO_CARDGROUP_SETID";
    
    /* *** Product Sheet *** */
    public static final String PRODUCT_CODE = "PRODUCT_CODE";
    public static final String PRODUCT_TYPE = "PRODUCT_TYPE";
    public static final String MODULE_CODE = "MODULE_CODE";
    public static final String PRODUCT_NAME = "PRODUCT_NAME";
    public static final String SHORT_NAME = "SHORT_NAME";
    public static final String PRODUCT_SHORT_CODE = "PRODUCT_SHORT_CODE";
    
    /* *** DIVSIONDEPT *** */
    public static final String DIVISION = "DIVISION";
    public static final String DEPARTMENT = "DEPARTMENT";
    
    /* *** Geographical Domains *** */
    public static final String DOMAIN_SHORT_NAME = "DOMAIN_SHORT_NAME";
    public static final String DOMAIN_TYPE_NAME = "DOMAIN_TYPE_NAME";
    
    /* *** Staff Users *** */
    public static final String OWNER_USER_NAME = "OWNER_USER_NAME";
    public static final String PARENT_USER_NAME = "PARENT_USER_NAME";
    public static final String CHANNEL_USER_NAME = "CHANNEL_USER_NAME";
    public static final String STAFF_USER_NAME = "STAFF_USER_NAME";
    public static final String STAFF_LOGINID = "STAFF_LOGINID";
    public static final String STAFF_PASSWORD = "STAFF_PASSWORD";
    public static final String STAFF_MSISDN = "STAFF_MSISDN";
    public static final String STAFF_PIN = "STAFF_PIN";
    public static final String STAFF_EMAIL_ID = "STAFF_EMAIL_ID";
    public static final String STAFF_PARENT_LOGIN_ID = "STAFF_PARENT_LOGIN_ID";
    
    /* *** Access Bearer Matrix *** */
    public static final String SERIAL_NO = "Sr. No.";
    public static final String CATEGORY_USERS = "Category Users";
    public static final String WEB = "Web";
    public static final String USSD = "USSD";
    public static final String EXTGW = "EXTGW";
    
    /* *** Link Sheet *** */
    public static final String PAGE_CODES="PageCodes";
    public static final String ROLE_CODES="RoleCodes";
    public static final String EVENT_CODES="EventCodes";
    public static final String CATEGORY_CODES="CategoryCodes";
    public static final String GRP_ROLE_APPLICABLE = "GroupRoleApplicable";
    
    /*	***	VOMS_DENOMINATION_SHEET	*** */
    public static final String VOMS_VOUCHER_TYPE = "VOUCHER_TYPE";
    public static final String VOMS_SEGMENT ="VOMS_SEGMENT";
    public static final String VOMS_TYPE = "TYPE";
    public static final String VOMS_SERVICE = "SERVICE";
    public static final String VOMS_SUB_SERVICE = "SUB_SERVICE";
    public static final String VOMS_SELECTORNAME = "SELECTORNAME";
    public static final String VOMS_SERVICEID = "SERVICE_ID";
    public static final String VOMS_USER_CATEGORY_NAME = "CATEGORY_NAME";
    public static final String VOMS_STATUS = "STATUS";
    public static final String VOMS_DENOMINATION_NAME = "DENOMINATION_NAME";
    public static final String VOMS_SHORT_NAME = "SHORT_NAME";
    public static final String VOMS_MRP = "MRP";
    public static final String VOMS_PAYABLE_AMOUNT = "PAYABLE_AMOUNT";
    public static final String VOMS_DESCRIPTION = "DESCRIPTION";
    public static final String VOMS_PROFILE_NAME = "PROFILE_NAME";
    public static final String VOMS_MIN_QUANTITY = "MIN_QUANTITY";
    public static final String VOMS_PERFORMANCE_INDICATOR_QUANTITY = "PERFORMANCE_INDICATOR_QUANTITY";
    public static final String VOMS_TALK_TIME = "TALK_TIME";
    public static final String VOMS_VALIDITY = "VALIDITY";
    public static final String VOMS_EXPIRY_PERIOD = "EXPIRY_PERIOD";
    public static final String VOMS_APPLICABLE_FROM = "APPLICABLE_FROM";
    public static final String VOMS_TOTAL_VOUCHERS = "TOTAL_VOUCHERS";
    public static final String VOMS_THRESHOLD = "THRESHOLD";
    public static final String VOMS_QUANTITY = "QUANTITY";
    public static final String VOMS_NETWORK_CODE = "VOMS_NETWORK_CODE";
    
    /* *** VOMS_BUNDLES *** */
    public static final String VOMS_BUNDLE_NAME = "BUNDLE_NAME";
    public static final String VOMS_BUNDLE_QUANTITY = "QUANTITY";
    public static final String VOMS_BUNDLE_PREFIX = "BUNDLE_PREFIX";

    /* C2C Bulk Revamped */
    public static final String SHEET1 = "SHEET1";

    /* DVD Recharge Bulk Revamped */
    public static final String DVD_TEMPLATE = "Template Sheet 1";
	
	    /* BATCH O2C TRANSFER STRUTS */
    public static final String O2C_TEMPLATE = "First Sheet";

    /* BATCH FOC TRANSFER STRUTS */
    public static final String FOC_TEMPLATE="Data Sheet1";

    /* Batch GRADE MANAGEMENT STRUTS */
    public static final String BGM_TEMPLATE = "Template Sheet";
    public static final String BGM_MASTER_SHEET = "Master Sheet";


}
