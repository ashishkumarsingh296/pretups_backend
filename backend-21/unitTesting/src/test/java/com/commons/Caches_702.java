package com.commons;

public class Caches_702 implements CacheI {

	 //Added By Krishan for Single Cache Feature
    public static final String CACHE_ALL = "0";
    public static final String CACHE_NETWORK = "NETWORK";
    public static final String CACHE_LOOKUPS = "LOOKUP";
    public static final String CACHE_PreferenceCache = "PREFERENCE";
    public static final String CACHE_NetworkPrefixCache = "NWPREFIX";
    public static final String CACHE_ServiceKeywordCache = "SRVKEYWORD";
    //public static final String CACHE_SystemPreferences = 6;
    public static final String CACHE_MSISDNPrefixInterfaceMappingCache = "MOBILENOPRFINTR";
    public static final String CACHE_NetworkInterfaceModuleCache = "NWINTRFCMOD";
    public static final String CACHE_ServicePaymentMappingCache = "SRVPAYMENTMAPP";
    public static final String CACHE_TransferRulesCache = "TRANSFERRULE";
    public static final String CACHE_MessageGatewayCache = "MSGGTW";
    public static final String CACHE_RequestInterfaceCache = "REQINTFC";
    public static final String CACHE_FileCache = "FILE";
/*    public static final String CACHE_LoadControllerCache_INSTANCE = 14;
    public static final String CACHE_LoadControllerCache_NETWORK = 15;
    public static final String CACHE_LoadControllerCache_INTERFACE = 16;
    public static final String CACHE_LoadControllerCache_TRANSACTION = 17;*/
    public static final String CACHE_SIM_PROFILE = "SIMPRF";
    public static final String CACHE_NETWORK_SERVICE_CACHE = "NWSERVICE";
    public static final String CACHE_NETWORK_PRODUCT_SERVICE_TYPE = "NWPRDSRVTYPE";
    public static final String CACHE_ROUTING_CONTROL = "ROUTINGCONTL";
    public static final String CACHE_REGISTRATION_CONTROL = "REGCONTRL";
    public static final String CACHE_CONSTANT_PROPS = "CONSTANTS";
    public static final String CACHE_LOGGER_CONFIG = "LOGGER";
    public static final String CACHE_MESSAGE = "MESSAGE";
    public static final String CACHE_MESSAGE_RESOURCES = "MESSAGERESOURCE";
    public static final String CACHE_SERVICE_ROUTING = "SRVINTFCROUTING";
    public static final String CACHE_GROUP_TYPE_PROFILE = "GROUPTYPEPRF";
    public static final String CACHE_NETWORK_INTERFACE_MODULE = "NWINTRFCMOD";
    public static final String CACHE_INTERFACE_ROUTING_CONTROL = "INTRROUTINGCONTL";
    public static final String CACHE_PAYMENT_METHOD = "PAYMNTMETH";
    public static final String CACHE_SERVICE_SELECTOR_MAPPING = "SRVSLTRMAPP";
    public static final String CACHE_BONUS_BUNDLES = "BONUSBUNDLE";
    public static final String CACHE_IAT_COUNTRY_MASTER = "IATCONTRYMAST";
    public static final String CACHE_IAT_NETWORK = "IATNW";
    public static final String CACHE_USER_DEFAULT = "USERDEFAULTCONFIG";
    public static final String CACHE_USER_SERVICES = "USERSERVICE";
    public static final String CACHE_SERVICE_INTERFACE_MAPPING = "SRVINTMAPP";
    public static final String CACHE_NETWORK_PRODUCT = "NWPRD";
    public static final String CACHE_CARD_GROUP = "CARDGROUP";
    public static final String CACHE_MESSAGE_GATEWAY_CATEGORY = "MESSAGEGTWCAT";
    public static final String CACHE_SERVICE_CLASS_CODE = "SRVCLASSINFO";
    public static final String CACHE_TRANSFER_PROFILE = "TRFPRF";
    public static final String CACHE_TRANSFER_PROFILE_PRODUCT = "TRFPRFPRD";
    //public static final String CACHE_SERVICE_TYPE_SUBSCRIBER_ENQUIRY = 45;
    public static final String CACHE_COMMISSION_PROFILE = "COMMPRF";
    public static final String CACHE_USER_ALLOWED_STATUS = "USERALLWDSTATUS";
    public static final String CACHE_USER_WALLET_MAPPING = "USERWALLET";
    public static final String CACHE_LMS_PROFILE = "LMSPRF";
    public static final String CACHE_CURRENCY = "CURRENCY";
    public static final String CACHE_CELL_ID = "CELLID";
    
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
