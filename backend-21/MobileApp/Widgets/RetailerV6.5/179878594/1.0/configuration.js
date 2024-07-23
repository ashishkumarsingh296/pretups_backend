// SERVER DETAIL CONFIGURATION

/*var PreTUPSSERVER = "http://172.16.7.194:1515/pretups/SelfTopUpReceiver";
var REQUEST_GATEWAY_CODE = "STUGW";
var REQUEST_GATEWAY_TYPE = "STUGW";
var LOGIN = "pretups";
var PASSWORD = "pretups123";
var SOURCE_TYPE = "plain";
var SERVICE_PORT = "190";
var EXTNWCODE = "NG";
var LANGUAGE1 = 1;
var LANGUAGE2 = 1;
var SELECTOR = 1;
var PRODUCT_CODE = 101;*/


// TYPE VALUE OF EACH SERVICE
/*
var RECHARGE_TYPE 		= "STURCTRFREQ";
var BILLPAY_TYPE 		= "STUPPBTRFREQ";
var TRANSFER_TYPE 		= "STUTRFREQ";	
var WITHDRAW_TYPE 		= "STUTRFREQ";
var GIFTRECHARGE_TYPE 	= "STUTRFREQ";
var RETURNSTOCK_TYPE 	= "O2CRETREQ";
var DAILYREPORT_TYPE 	= "STUDSRREQ";
var BALANCEENQ_TYPE 	= "STUBALREQ";
var LAST5TRANS_TYPE		= "STULTSREQ";
var CHANGEPIN_TYPE 		= "STURCPNREQ";
*/


// BYPASSING WEBAXN SERVER 
// true = bypassing the webaxn flow: webaxnclient --> application server
//and false = not bypassing webaxn server flow: webaxnclient-->webaxn server --> application server

var BYPASS_WEBAXN 	= false;

//SERVICE CONFIGURATION
//true = service Available
//false = service not available
var BILLPAYMENT		= true;
var RETURNSTOCK  	= true;
var GIFTRECHARGE 	= true;
var REPORTS			= true;
var TRANSFER	 	= true;
var WITHDRAW	 	= true;
var ELECVOUCHER		= true;
var RECHARGE		= true;
var OTHERBALANCE    = true;
var RECHARGESTATUS  = true;
// REPORT CONFIGURATION

//var LAST_5TRANS 	= true;
//var STOCK_BAL		= true;
//var DAILY_REPORT	= true;

// REPORT PINCHECK

//var PIN_CHECK = true;

//COUNTRY CODE CHECK

var COUNTRY_CODE = "+91";
var COUNTRYCODE_WITHOUTPLUS = "91";
var MOBILENO_STARTWITHZERO = "0";
var COUNTRY_CODE_CHECK = true;// if true mobileNo having countryCode.remove country code from mobileNo then send to server
							// if false mobileNo having country code may be send may not be sent to server;

// SEND REQUEST AS ENCRYPT FORMAT
// true == end request send with encrypted format
// false == end request send with plain format
var SEND_ENCRYPTREQ = false;

// REQUEST SEND AS XML

var SENDREQUEST_XML = false;
// SAVE PREMINENT

var SAVE_PERMINENT 	= true;

// demo//  1 for demo (endrequest hit demosimulator) otherthan 1 application server(endreq hit actual application server)
var DEMO_FLAG=2;
var DEMO_URL = "pretupsDEMOSIMULATOR:plugin.webaxn.comviva.com?";
var DEMO_MSISDN = "7200001111";
var DEMO_PIN= "1357";
var DEMO_IMEI = "1234567890";
var DEMO_EKEY = "490377053503954C";


// encryptUrl
var ENCRYPT_URL="AES128Plugin:plugin.webaxn.comviva.com?";
//Multi operator check
MULTI_OPERATOR=false;

// Alert title

var STR_TITLE = "PreTUPS";

// INPUTS MAXLENTH CONFIGURATION

var PIN_LENGTH = 4;
var MSISDN_LENGTH = 15;
var AMOUNT_LENGTH = 10;
var NAME_LEN = 50;
var EXIT_TEXT = "Do You want to Exit?";

// SERVER RESPONSE PARAMETERS

var STR_SUCCESS = "200";
var STR_TXNSTATUS = "TXNSTATUS";
var STR_TXNMESSAGE = "MESSAGE";
var STR_MSISDN = "MSISDN";
var STR_EKEY 	= "ENK"; 

//  SERVER ERROR MESSSAGE PROVIDED BY APP

var STR_SERVER_ERROR 	= "No Response from Server";
var STR_SERVICE_ERROR 	= "Service Unavailable";
var STR_NEWPIN_ERROR 	= "newPIN and confirmPIN InCorrect";


//ClientVersion

var clientVersion_config = "5615";
var COUNTRY_CODE = "+91";
var LAN_EN="0";
var LAN_FR="1";
var LAN_AR="2";