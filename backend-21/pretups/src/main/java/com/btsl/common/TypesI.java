/*
 * Created on Mar 22, 2005
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.btsl.common;

/**
 * @author abhijit.chauhan
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public interface TypesI {

    public String ALL = "ALL";
    public String NETWORK_ID_DEFAULT = "DL";// Define default id, used while
                                            // inserting Network Admin
    public String NETWORK_NAME_DEFAULT = "Not Applicable";// Define default id,
                                                          // used while
                                                          // inserting Network
                                                          // Admin
    public String SYSTEM_USER = "SYSTEM";
    public String NETWORK_ADMIN = "NWADM";
    public String SUPER_ADMIN = "SUADM";
    public String CUSTOMER_CARE = "CCE";
    public String BCU_USER = "BCU";
    public String USERID = "USERID";// for UserId generator
    public String GRPH_DOMAIN_CODE = "ROOT";
    public String GRPH_DOMAIN_TYPE_NETWORK = "NW";// type of graph domain type
                                                  // for network
    public String GEOGRAPHICAL_AREA_NETWORK = "NETWORK";// type of geographical
                                                        // area
    public String GEOGRAPHICAL_AREA_ZONE = "ZONE";// type of geographical area
    public String YES = "Y";
    public String NO = "N";
    public String SUSPEND = "S";

    public static final String ERROR_EXCEPTION = "0000";

    public String PERIOD_DAILY = "DAILY";
    public String PERIOD_WEEKLY = "WEEKLY";
    public String PERIOD_MONTHLY = "MONTHLY";
    public String PERIOD_YEARLY = "YEARLY";
    public String DIVDEPT_TYPE = "OPERATOR";
    public String DIVDEPT_DIVISION = "DIVISION";
    public String DIVDEPT_DEPARTMENT = "DEPARTMENT";

    public String OPERATOR_USER_TYPE = "OPERATOR";
    public String CHANNEL_USER_TYPE = "CHANNEL";

    public String LOG_TYPE_LOGIN = "LOGIN";
    public String LOG_TYPE_LOGOUT = "LOGOUT";
    public String LOG_TYPE_EXPIRED = "EXPIRED";

    // for Admin Operation Log
    public String LOGGER_NETWORK_SOURCE = "NETWORK";

    public String LOGGER_OPERATION_ACTIVATED = "ACTIVATED";
    public String LOGGER_OPERATION_SUSPENDED = "SUSPENDED";

    public String LOGGER_OPERATION_ADD = "ADD";
    public String LOGGER_OPERATION_MODIFY = "MODIFY";
    public String LOGGER_OPERATION_DELETE = "DELETE";

    public String LOGGER_STK_SERVICE_ADD = "ADD STK SERVICE";
    public String LOGGER_STK_SERVICE_MODIFY = "MODIFY STK SERVICE";
    public String LOGGER_STK_SERVICE_CATEGORY_ASSOCIATION = "ASSOCIATE STK SERVICE CATEGORIES";

    public String LOGGER_INTERFACE_SOURCE = "INTERFACE";

    public String LOGGER_SUBLOOKUP_SOURCE = "SUBLOOKUP";

    public String LOGGER_GRADE_SOURCE = "GRADE";

    public String LOGGER_SERVICE_CLASS_SOURCE = "SERVICE CLASS";

    public String LOGGER_DIVISION_SOURCE = "DIVISION";

    public String LOGGER_DEPATRTMENT_SOURCE = "DEPARTMENT";
    public String LOGGER_SERVICE_PRODUCT_AMOUNT_MAPPING="SERVICE PRODUCT AMOUNT MAPPING";

    public String LOGGER_DOMAIN_SOURCE = "DOMAIN";

    public String LOGGER_CATEGORY_SOURCE = "CATEGORY";

    public String LOGGER_MESSAGE_GATEWAY_MAPPING_SOURCE = "MESSAGE GATEWAY MAPPING";

    public String LOGGER_GROUPROLE_SOURCE = "GROUP_ROLE";
    public String LOGGER_NETWORK_SERVICE_SOURCE = "NETWORK_SERVICES";
    public String LOGGER_SERVICE_KEYWORD_SOURCE = "SERVICE_KEYWORDS";
    public String LOGGER_PREFERENCE_SYSTEM_SOURCE = "PREFERENCE_SYSTEM";
    public String LOGGER_PREFERENCE_NETWORK_SOURCE = "PREFERENCE_NETWORK";
    public String LOGGER_PREFERENCE_SERVICECLASS_SOURCE = "PREFERENCE_SERVICECLASS";
    public String LOGGER_PREFERENCE_CONTROLUNIT_SOURCE = "PREFERENCE_CONTROLUNIT";
    public String LOGGER_MESSAGE_GATEWAY_SOURCE = "MESSAGE_GATEWAY";
    public String STAFF_USER_TYPE = "STAFF";
    
    public String LOGGER_OPERATION_CHANGE_STATUS = "CHANGE_STATUS";
    public String LOGGER_OPERATION_MAKE_DEFAULT_CARDGROUP ="MAKE_DEFAULT_CARD_GROUP";
    public String LOGGER_OPERATION_MAKE_VOUCHER_CARDGROUP="VOUCHER_CARD_GROUP";
    public String LOGGER_OPERATION_MAKE_DEFAULT = "MAKE_DEFAULT";
    // Added for new opertor user Sub super admin (Manisha 29/04/09)
    public String SUB_SUPER_ADMIN = "SSADM";

    public String OPT_IN = "I";
    public String OPT_OUT = "O";
    public String NORMAL = "N";
	//Added for super users
    public String SUPER_NETWORK_ADMIN="SUNADM";
    public String SUPER_CHANNEL_ADMIN ="SUBCU";
    public String SUPER_CUSTOMER_CARE="SUCCE";
    public String MONITOR_SERVER="MONTR";
    public String VOMS_SERIAL_COUNTER="VMSSRLCNTR";
    public String  OFFLINE_REPORT_ID ="OFFLINERPT";
    public String BULK_PROC_ID="BULKPROCID";
    public String LOGGER_PROMO_TRF_RULE_SOURCE = "PROMO_TRANSFER_RULE";
    public String LOGGER_TRF_CONTROL_PROFILE_SOURCE = "TRANSFER_CONTROL_PROFILE";
    public String LOGGER_MODIFY_BULK_USER = "MODIFY_BULK_USER";
    public String LOGGER_SERVICE_DOMAIN_MGMT = "DOMAIN_MANAGEMENT";
    public String LOGGER_SERVICE_MGMT = "SERVICE_MANAGEMENT";
    public String LOGGER_OPERATION_SEARCH="SEARCH";
    public String LOGGER_OPERATION_PROCESS = "PROCESS";
    public String LOGGER_OPERATION_APPROVE = "APPROVE";
    public String LOGGER_OPERATION_REJECT = "REJECT";
    public String LOGGER_CELL_ID_ASSOCIATE = "CELL_ID_ASSOCIATE";
    public String LOGGER_CELL_ID_REASSOCIATE = "CELL_ID_REASSOCIATE";
    public String LOGGER_ADD_CELL_GROUP = "ADD_CELL_GROUP";
    public String LOGGER_MODIFY_CELL_GROUP = "MODIFY_CELL_GROUP";
    public String LOGGER_DEL_CELL_GROUP = "DELETE_CELL_GROUP";
    public String LOGGER_ADD_SUBSCRIBER_ROUTING = "ADD_SUBSCRIBER_ROUTING";
    public String LOGGER_DELETE_SUBSCRIBER_ROUTING = "DELETE_SUBSCRIBER_ROUTING";
}
