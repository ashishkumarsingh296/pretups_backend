package com.client.ldap;

public interface LDAPTypeI {

    public int NO_FILTER_CRITERIA = 2;
    public String FILTER_CRITERIA_1_ID = "objectClass";
    public String FILTER_CRITERIA_1_VALUE = "top";
    public String FILTER_CRITERIA_2_ID = "sAMAccountName";
    public String SEARCH_CRITERIA_1 = "sAMAccountName";
    public String INTERNAL_SEARCH_CRITERIA_REQUIRED = "Y";
    public int NO_HOST_TYPES = 2;
    public String LDAP_SERVER="LIVE";

}
