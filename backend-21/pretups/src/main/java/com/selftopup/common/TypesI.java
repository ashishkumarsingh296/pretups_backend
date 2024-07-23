/*
 * Created on Mar 22, 2005
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.selftopup.common;

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
    // Added for new opertor user Sub super admin (Manisha 29/04/09)
    public String SUB_SUPER_ADMIN = "SSADM";
}
