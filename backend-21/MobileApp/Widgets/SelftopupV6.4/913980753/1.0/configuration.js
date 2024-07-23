// SERVER DETAIL CONFIGURATION
var PreTUPSSERVER =  "http://172.16.1.121:1515/selftopup/SelfTopUpReceiver";//http://172.16.7.194:1515/selftopup/SelfTopUpReceiver
var REQUEST_GATEWAY_CODE = "STUGW";
var REQUEST_GATEWAY_TYPE = "STUGW";
var LOGIN = "pretups";
var PASSWORD = "pretups123";

var SOURCE_TYPE = "plain";
var SERVICE_PORT = "190";

var SUBS_REG_TYPE = "STPREGREQ2";
var SUBS_RECH_TYPE = "STPRCREQ";
var SUBS_P2P_TYPE = "STPPRCREQ";
var SUBS_CARD_RECH_TYPE = "ADHOCRCREG";
var SUBS_ADHOC_RECH_TYPE = "ADHOCRC";
var SUBS_CHANGEPIN_TYPE = "STPCPNREQ";
var BUDDY_RECH_TYPE = "PRCREQ";
var SUB_TYPE= "STPREGREQ"
var ADDCARD_TYPE = "STPACREQ";
var EDITCARD_TYPE = "STPMCREQ";
var VIEWCARDS_TYPE = "STPVCREQ";
var DELETECARD_TYPE = "STPDCREQ";

var ADDBUDDY_TYPE = "PADDREQ";
var VIEWBUDDY_TYPE = "PLISTREQ";
var DELETEBUDDY_TYPE = "PDELREQ";

var EXTNWCODE = "NG";
var LANGUAGE1 = 0;
var LANGUAGE2 = 1;
var SELECTOR = 1;
var EMAIL= "abc@xyz.com";

// AES128 plugin Url
var ENCRYPT_URL="AES128Plugin:plugin.webaxn.comviva.com?";

//default values
var DEF_MSISDN  = "7200000000";
var DEF_PIN		= "0000";

//demo server details  -- 1= demo server other than 1 application server
var DEMO_FLAG = 2;
var DEMO_URL = "pretupsDEMOSIMULATOR:plugin.webaxn.comviva.com?";

// data sending to application server true= encryptformat false = normal format

var SENDENCRYPTREQ = false;

// INPUT MAX LENGTH CONFIGURAION

var PIN_LENGTH		 = 4;
var CVV_LENGTH 		 = 3;
var MSISDN_LENGTH	 = 15;
var AMOUNT_LENGTH	 = 10;
var NAME_LENTH		 = 50;
var CARDNO_LENGTH 	 = 16;


//*********************** PIN CONFIGURATION ********************************

var PIN_CHECK_ALL	 		= false;
var PIN_CHECK_FIN	 		= true;
var PIN_CHECK_NONFIN 		= true;

// FINANCIAL TRANSACTION

var PINCHECK_SELF_AND_ANOTHER 	= true;
var PIN_CHECK_TALKMEUP			= true;
var PIN_CHECK_SOS				= true;
var PIN_CHECK_BUDDYRECH 		= false;

// NON -FINANCIAL TRANSACTION

var PIN_CHECK_ADDCARD		= true;
var PIN_CHECK_EDITCARD		= true;
var PIN_CHECK_VIEWCARD		= false;
var PIN_CHECK_DELCARD		= true;
var PIN_CHECK_ADDBUDDY		= true;
var PIN_CHECK_VIEWBUDDY		= false;
var PIN_CHECK_BUDDYEDIT		= true;
var PIN_CHECK_DELBUDDY		= true;
var PIN_CHECK 				= true;

//****************** END ****************************************************** 
// SERVICE ENABLE/DISABLE TO ALL THE USERS

var P2P_RECHARGE 		= true;
var SOS_RECHARGE 		= true;
var CARD_MANAGEMENT 	= true;// its enable/disable for card related recharge and card Related transaction(ADD/MODIFY/VIEW/DELETE)
var BUDDY_MANAGEMENT 	= true;

// cvv configure at addcard time

var CVV_CONFIG = true;

// PAGE SAVE CONFIGURATION

var SAVE_PERMINENT = true;// true = some of the pages are saved.if want to modify configure as false and clear cache.

// COUNTRY CODE CHECK

var COUNTRY_CODE = "+91";
var COUNTRYCODE_WITHOUTPLUS = "91";
var MOBILENO_STARTWITHZERO = "0";
var COUNTRY_CODE_CHECK = true;// if true mobileNo having countryCode.remove country code from mobileNo then send to server
								
// SERVER RESPONSE TAGS

var STR_TXNSTATUS = "TXNSTATUS";
var STR_TXNMESSAGE = "MESSAGE";
var STR_EKEY 	= "ENK"; 
var STR_CARDDETAILS = "CARDDETAILS";
var STR_BUDDYLIST = "BUDDYLIST";
var STR_CARDCOUNT = "REGISTEREDCARDS";
var STR_MSISDN = "MSISDN";
var STR_SUCCESS_CODE = "200";
var STR_SECLEVELREG_RES_CODE = "220";
var STR_REINSTALL_RES_CODE = "230";
var STR_NOCARD_RES_CODE = "210";
var STR_NOBUDDIES = "210";
var STR_NOBUDDY_RES = "nobuddy";
var STR_NOCARD_RES	= "nocards";
var STR_SERVER_ERROR 	= "No Response from Server";
var STR_SERVICE_ERROR 	= "Service Unavailable";
var STR_PINCHECK 	= "newPIN and confirmPIN InCorrect";
var BUDDY_MODIFY = "MODIFY";
var BUDDYMODIFY_MSG = "Your Buddy has been updated";

//ClientVersion
//var clientVersion_config = "6555";
var clientVersion_config = "6000";


