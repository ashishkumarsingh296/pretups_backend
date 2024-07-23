package com.commons;

public class Caches_701 implements CacheI {

    //Added By Krishan for Single Cache Feature
    public static final String CACHE_ALL = "0";
    public static final String CACHE_NETWORK = "1";
    public static final String CACHE_LOOKUPS = "2";
    public static final String CACHE_PreferenceCache = "3";
    public static final String CACHE_NetworkPrefixCache = "4";
    public static final String CACHE_ServiceKeywordCache = "5";
    //public static final int CACHE_SystemPreferences = 6;
    public static final String CACHE_MSISDNPrefixInterfaceMappingCache = "7";
    public static final String CACHE_NetworkInterfaceModuleCache = "8";
    public static final String CACHE_ServicePaymentMappingCache = "9";
    public static final String CACHE_TransferRulesCache = "10";
    public static final String CACHE_MessageGatewayCache = "11";
    public static final String CACHE_RequestInterfaceCache = "12";
    public static final String CACHE_FileCache = "13";
/*    public static final int CACHE_LoadControllerCache_INSTANCE = 14;
    public static final int CACHE_LoadControllerCache_NETWORK = 15;
    public static final int CACHE_LoadControllerCache_INTERFACE = 16;
    public static final int CACHE_LoadControllerCache_TRANSACTION = 17;*/
    public static final String CACHE_SIM_PROFILE = "18";
    public static final String CACHE_NETWORK_SERVICE_CACHE = "19";
    public static final String CACHE_NETWORK_PRODUCT_SERVICE_TYPE = "20";
    public static final String CACHE_ROUTING_CONTROL = "21";
    public static final String CACHE_REGISTRATION_CONTROL = "22";
    public static final String CACHE_CONSTANT_PROPS = "23";
    public static final String CACHE_LOGGER_CONFIG = "24";
    public static final String CACHE_MESSAGE = "25";
    public static final String CACHE_MESSAGE_RESOURCES = "26";
    public static final String CACHE_SERVICE_ROUTING = "27";
    public static final String CACHE_GROUP_TYPE_PROFILE = "28";
    public static final String CACHE_NETWORK_INTERFACE_MODULE = "29";
    public static final String CACHE_INTERFACE_ROUTING_CONTROL = "30";
    public static final String CACHE_PAYMENT_METHOD = "31";
    public static final String CACHE_SERVICE_SELECTOR_MAPPING = "32";
    public static final String CACHE_BONUS_BUNDLES = "33";
    public static final String CACHE_IAT_COUNTRY_MASTER = "34";
    public static final String CACHE_IAT_NETWORK = "35";
    public static final String CACHE_USER_DEFAULT = "36";
    public static final String CACHE_USER_SERVICES = "37";
    public static final String CACHE_SERVICE_INTERFACE_MAPPING = "38";
    public static final String CACHE_NETWORK_PRODUCT = "39";
    public static final String CACHE_CARD_GROUP = "40";
    public static final String CACHE_MESSAGE_GATEWAY_CATEGORY = "41";
    public static final String CACHE_SERVICE_CLASS_CODE = "42";
    public static final String CACHE_TRANSFER_PROFILE = "43";
    public static final String CACHE_TRANSFER_PROFILE_PRODUCT = "44";
    //public static final int CACHE_SERVICE_TYPE_SUBSCRIBER_ENQUIRY = 45;
    public static final String CACHE_COMMISSION_PROFILE = "46";
    public static final String CACHE_USER_ALLOWED_STATUS = "47";
    public static final String CACHE_USER_WALLET_MAPPING = "48";
    public static final String CACHE_LMS_PROFILE = "49";
    public static final String CACHE_CURRENCY = "50";
    public static final String CACHE_CELL_ID = "51";
    
    public String ALL() {
    	return CACHE_ALL;
    }
    
    public String NETWORK() {
    	return CACHE_NETWORK;
    }
    
    public String LOOKUPS() {
    	return CACHE_LOOKUPS;
    }
    
    public String PreferenceCache() {
    	return CACHE_PreferenceCache;
    }
    
    public String NetworkPrefixCache() {
    	return CACHE_NetworkPrefixCache;
    }
    
    public String ServiceKeywordCache() {
    	return CACHE_ServiceKeywordCache;
    }
    
    public String MSISDNPrefixInterfaceMappingCache() {
    	return CACHE_MSISDNPrefixInterfaceMappingCache;
    }
    
    public String NetworkInterfaceModuleCache() {
    	return CACHE_NetworkInterfaceModuleCache;
    }
    
    public String ServicePaymentMappingCache() {
    	return CACHE_ServicePaymentMappingCache;
    }
    
    public String TransferRulesCache() {
    	return CACHE_TransferRulesCache;
    }
    
    public String MessageGatewayCache() {
    	return CACHE_NETWORK;
    }
    
    public String RequestInterfaceCache() {
    	return CACHE_RequestInterfaceCache;
    }
    
    public String FileCache() {
    	return CACHE_FileCache;
    }
    
    public String SIM_PROFILE() {
    	return CACHE_SIM_PROFILE;
    }
    
    public String NETWORK_SERVICE_CACHE() {
    	return CACHE_NETWORK_SERVICE_CACHE;
    }
    
    public String NETWORK_PRODUCT_SERVICE_TYPE() {
    	return CACHE_NETWORK_PRODUCT_SERVICE_TYPE;
    }
    
    public String ROUTING_CONTROL() {
    	return CACHE_ROUTING_CONTROL;
    }
    
    public String REGISTRATION_CONTROL() {
    	return CACHE_REGISTRATION_CONTROL;
    }
    
    public String CONSTANT_PROPS() {
    	return CACHE_CONSTANT_PROPS;
    }
    
    public String LOGGER_CONFIG() {
    	return CACHE_LOGGER_CONFIG;
    }
    
    public String MESSAGE() {
    	return CACHE_MESSAGE;
    }
    
    public String MESSAGE_RESOURCES() {
    	return CACHE_MESSAGE_RESOURCES;
    }
    
    public String SERVICE_ROUTING() {
    	return CACHE_SERVICE_ROUTING;
    }
    
    public String GROUP_TYPE_PROFILE() {
    	return CACHE_GROUP_TYPE_PROFILE;
    }
    
    public String NETWORK_INTERFACE_MODULE() {
    	return CACHE_NETWORK_INTERFACE_MODULE;
    }
    
    public String INTERFACE_ROUTING_CONTROL() {
    	return CACHE_INTERFACE_ROUTING_CONTROL;
    }
    
    public String PAYMENT_METHOD() {
    	return CACHE_PAYMENT_METHOD;
    }
    
    public String SERVICE_SELECTOR_MAPPING() {
    	return CACHE_SERVICE_SELECTOR_MAPPING;
    }
    
    public String BONUS_BUNDLES() {
    	return CACHE_BONUS_BUNDLES;
    }
    
    public String IAT_COUNTRY_MASTER() {
    	return CACHE_IAT_COUNTRY_MASTER;
    }
    
    public String IAT_NETWORK() {
    	return CACHE_IAT_NETWORK;
    }
    
    public String USER_DEFAULT() {
    	return CACHE_USER_DEFAULT;
    }
    
    public String USER_SERVICES() {
    	return CACHE_USER_SERVICES;
    }
    
    public String SERVICE_INTERFACE_MAPPING() {
    	return CACHE_SERVICE_INTERFACE_MAPPING;
    }
    
    public String NETWORK_PRODUCT() {
    	return CACHE_NETWORK_PRODUCT;
    }
    
    public String CARD_GROUP() {
    	return CACHE_CARD_GROUP;
    }
 
    public String MESSAGE_GATEWAY_CATEGORY() {
    	return CACHE_MESSAGE_GATEWAY_CATEGORY;
    }
    
    public String SERVICE_CLASS_CODE() {
    	return CACHE_SERVICE_CLASS_CODE;
    }
    
    public String TRANSFER_PROFILE() {
    	return CACHE_TRANSFER_PROFILE;
    }
    
    public String TRANSFER_PROFILE_PRODUCT() {
    	return CACHE_TRANSFER_PROFILE_PRODUCT;
    }
    
    public String COMMISSION_PROFILE() {
    	return CACHE_COMMISSION_PROFILE;
    }
    
    public String USER_ALLOWED_STATUS() {
    	return CACHE_USER_ALLOWED_STATUS;
    }
    
    public String USER_WALLET_MAPPING() {
    	return CACHE_USER_WALLET_MAPPING;
    }
    
    public String LMS_PROFILE() {
    	return CACHE_LMS_PROFILE;
    }
    
    public String CURRENCY() {
    	return CACHE_CURRENCY;
    }
    
    public String CELL_ID() {
    	return CACHE_CELL_ID;
    }
}
