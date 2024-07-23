package com.btsl.util;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.CustomExclusionTotalIncomeDetailedRespVO;
import com.btsl.user.businesslogic.TotalUserIncomeDetailsVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Base64;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class RestTagValueValidation {
    
	private static Log log = LogFactory.getFactory().getInstance(RestTagValueValidation.class.getName());
    public static final boolean MANDATORY = true;
    public static final String LANGUAGE1 = "language1";
    public static final String REQUESTTYPE ="requestType";
    public static final String TYPE = "type";
    public static final String EXTNWCODE = "extnwcode";
    public static final String MSISDN2 = "msisdn2";
    public static final String MSISDN3 = "msisdn3";
    public static final String MSISDNLIST = "msisdnlist";
    public static final String MSISDN1 = "primarymsisdn";
    public static final String LOGINID2 = "loginid2";
    public static final String EXTCODE2 = "extcode2";
    public static final String PRODUCTCODE="productcode";
    public static final String PRODUCTSHORTNAME = "PRODUCTSHORTNAME";
    public static final String BALANCE = "BALANCE";
    public static final String CONFIRMPIN = "confirmpin";
    public static final String NEWPIN = "newpin";
    public static final String REMARKS = "remarks";
    public static final String QTY="qty";
    public static final String TXNID="txnid";
    public static final String RECEIVERTRFVALUE="receivertrfvalue";
    public static final String RECEIVERACCESSVALUE="receiveraccessval";
    public static final String ERRORCODE="errorcode";
    public static final String BLANK="";
    public static final String AMOUNT="amount";
    public static final String LANGUAGE2 = "language2";
    public static final String SELECTOR = "selector";
    public static final String NOTIF_MSISDN="notifMsisdn";
    public static final String GIFTER_MSISDN="gifterMsisdn";
    public static final String GIFTER_NAME="gifterName";
    public static final String GIFTER_LANG="gifterLang";
    public static final String PRODUCTSRES = "productsres";
    public static final String PRODUCTS = "products";
    public static final String RECORDSRES = "recordsres";
    public static final String TXNTYPE = "txntype";
    public static final String TXNDATE = "txndate";
    public static final String TXNSTATUS = "txnstatus";
    public static final String RECORDS = "records";
    public static final String TRFCATEGORY = "trfcategory";
    public static final String REFNUM = "refnumber";
    public static final String PAYMENTTYPE = "paymenttype";
    public static final String PAYMENTDATE = "paymentdate";
    public static final String PAYMENTNUM = "paymentinstnumber";
    public static final String EXTTXNNUMBER = "exttxnnumber";
    public static final String EXTTXNDATE = "exttxndate";
    public static final boolean OPTIONAL = false;
    public static final String LASTTRFTYPE = "LASTTRFTYPE";
    public static final String GEOGRAPHYCODE = "geographycode";
    public static final String PARENTMSISDN = "parentmsisdn";
    public static final String PARENTEXTERNALCODE = "parentexternalcode";
    public static final String EXTERNALCODE = "externalcode";
    public static final String USERCATCODE = "usercatcode";
    public static final String USERNAME = "username";
    public static final String SHORTNAME = "shortname";
    public static final String USERNAMEPREFIX = "usernameprefix";
    public static final String SUBSCRIBERCODE = "subscribercode";
    public static final String CONTACTNUMBER = "contactnumber";
    public static final String SSN = "ssn";
    public static final String CONTACTPERSON = "contactperson";
    public static final String ADDRESS1 = "address1";
    public static final String ADDRESS2 = "address2";
    public static final String CITY = "city";
    public static final String USERID = "userid";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String EMAILID = "emailid";
    public static final String WEBLOGINID = "webloginid";
    public static final String WEBPSWD = "webpassword";
    public static final String EXTCODE = "extcode";
    public static final String USERMSISDN = "usermsisdn";
    public static final String USERLOGINID ="userloginid";
    public static final String NEWEXTERNALCODE ="newexternalcode";
    public static final String FROM_USER_MSISDN ="from_user_msisdn";
    public static final String FROM_USER_EXTCODE ="from_user_extcode";
    public static final String TO_PARENT_MSISDN ="to_parent_msisdn";
    public static final String TO_PARENT_EXTCODE ="to_parent_extcode";
    public static final String TO_USER_GEOGRAPHICAL_CODE ="to_user_geographical_code";
    public static final String TO_USER_CATEGORY_CODE ="to_user_category_code";
    public static final String ACTION ="action";
    public static final String USRLOGINID ="usrloginid";
    public static final String CATCODE ="catcode";
    public static final String FROMDATE ="fromdate";
    public static final String TODATE ="todate";
    public static final String TRANSACTIONID="transactionid";
    
    public static final String RECORDTYPE="recordtype";
    public static final String NETWORK="network";
    public static final String DOMNAME="domname";
    public static final String CATEGORY="category";
    public static final String GEONAME="geoname";    
    public static final String TRFTYPE="trftype";
    public static final String MSISDN="msisdn";
    public static final String INFO1="info1";
    public static final String INFO2="info2";
    public static final String INFO3="info3";
    public static final String INFO4="info4";
    public static final String INFO5="info5";
    public static final String CELLID="cellId";
    public static final String SWITCHID="switchId";
    public static final String COMMPRF="commprf";
    public static final String STATUS="status";
    public static final String PAYMENTINSTTYPE="paymentinsttype";
    public static final String PAYMENTINSTNUMBER="paymentinstnumber";
    public static final String PAYMENTINSTDATE="paymentinstdate";
    public static final String PAYMENTINSTAMT="paymentinstamt";
    public static final String FIRSTAPPREMARKS="firstappremarks";
    public static final String SECONDAPPREMARKS="secondappremarks";
    public static final String THIRDAPPREMARKS="thirdappremarks";
    public static final String REQSOURCE="reqsource";
    public static final String RECORDRES="recordres";
    public static final String PRODCODE="prodcode";
    public static final String MRP="mrp";
    public static final String REQVALUE="reqvalue";
    public static final String TAX1R="tax1r";
    public static final String TAX1A="tax1a";
    public static final String TAX2R="tax2r";
    public static final String TAX2A="tax2a";
    public static final String TAX3R="tax3r";
    public static final String TAX3A="tax3a";
    public static final String COMMR="commr";
    public static final String COMMA="comma";
    public static final String NETAMT="netamt";
    public static final String APPROVEDBY="approvedby";
    public static final String APPROVEDON="approvedon";
    public static final String DETAILS="DETAILS";
    public static final String SUMMARY="SUMMARY";
    public static final String RES_TYPE="RES_TYPE";
    public static final String NOOFPRODORDENO = "numberOfProdOrDeno";
    public static final String GEOGRAPHYTYPE = "geographytype";
    public static final String PARENTGEOGRAPHYCODE = "parentgeographycode";
    public static final String GEOCODE = "geocode";
    public static final String GEOGRAPHYNAME = "geographyname";
    public static final String GEOGRAPHYSHORTNAME = "geographyshortname";
    public static final String GEOGRAPHYDESC = "geographydescription";
    public static final String GEOGRAPHYDEFAULTFLAG = "geographydefaultflag";
    public static final String GEOGRAPHYACTION = "geographyaction";
    
    public static final String INSUSPEND = "insuspend";
    public static final String OUTSUSPEND = "outsuspend";
    public static final String ADDRESS = "address";
    public static final String USERGRADE = "usergrade";
    public static final String TRFPRF = "trfprf";
    public static final String SERVICES = "services";
    public static final String PARENTNAME = "parentname";
    public static final String PARENTCATEGORY = "parentcategory";
    public static final String OWNERNAME = "ownername";
    public static final String OWNERMSISDN = "ownermsisdn";
    public static final String OWNERCATEGORY = "ownercategory";
    public static final String DESIGNATION = "designation";
    public static final String DIVISION = "division";
    public static final String DEPARTMENT = "department";
    public static final String APPOINTMENTDATE = "appointmentdate";
    public static final String CREATEDBY = "createdby";
    public static final String CREATEDON = "createdon";
    public static final String ALLOWEDIP = "allowedip";
    public static final String ALLOWEDDAYS = "alloweddays";
    public static final String ALLOWEDTIMEFROM = "allowedtimefrom";
    public static final String ALLOWEDTIMETO = "allowedtimeto";
    public static final String COMPANY = "company";
    public static final String FAX = "fax";
    public static final String MOBILENUMBER = "mobilenumber";
    public static final String O2CSUMMARYRES = "o2csummaryres";
    public static final String O2CDETAILRES = "o2cdetailres";
    public static final String C2CSUMMARYRES = "c2csummaryres";
    public static final String C2CDETAILRES = "c2cdetailres";
    public static final String C2SSUMMARYRES = "c2ssummaryres";
    public static final String C2SDETAILRES = "c2sdetailres";
    public static final String CONTROLLED = "controlled";
    public static final String TRFSTYPE = "trfstype";
    public static final String FROM_MSISDN = "msisdn1";
    public static final String TO_MSISDN = "msisdn2";
    public static final String USERNAME1 = "username1";
    public static final String USERNAME2 = "username2";
    public static final String GEONAME1 = "geoName1";
    public static final String GEONAME2 = "geoName2";
    public static final String DOMNAME1 = "domain1";
    public static final String DOMNAME2 = "domain2";
    public static final String CATEGORY1 = "category1";
    public static final String CATEGORY2 = "category2";
    public static final String GRADE1 = "grade1";
    public static final String GRADE2 = "grade2";
    public static final String USERID1 = "userId1";
    public static final String USERID2 = "userId2";
    public static final String SENDERMSISDN = "sendermsisdn";
    public static final String SUBSERVICE = "subservice";
    public static final String TRFVALUE = "trfvalue";
    public static final String USRTYPE = "usrtype";
    public static final String ENTRYTYPE = "entrytype";
    public static final String TRANSVALUE = "transvalue";
    public static final String TRFDATE = "trfdate";
    public static final String PRVBAL = "prvbal";
    public static final String POSTBAL = "postbal";
    public static final String SUBTYPE = "subtype";
	public static final String ENTRYDATE = "entrydate";
	public static final String SRVCLCODE = "srvclcode";
	public static final String TRFSTATUS = "trfstatus";
	public static final String ACCSTATUS = "accstatus";
	public static final String REFID = "refid";
	public static final String SACCFEE = "saccfee";
	public static final String RACCFEE = "raccfee";
	public static final String EXTREFNUM ="extrefnum";
	 public static final String REGSTATUS = "regstatus";
	 public static final String PINSTATUS = "pinstatus";
	 public static final String MINREBAL = "minrebal";
	 public static final String MINAMT = "minamt";
	 public static final String MAXAMT = "maxamt";
	 public static final String MAXPCTBAL = "maxpctbal";
	 public static final String ACCNTBAL = "accntbal";
	 public static final String PIN = "pin";
	 public static final String VOUCHERCODE = "vouchercode";
	 public static final String SERIALNUMBER = "serialnumber";
	 public static final String SID = "sid";
	 public static final String NEWSID = "newsid";
	 public static final String FROM_SERIAL_NO ="from_serial_no";
	 public static final String TO_SERIAL_NO ="to_serial_no";
	 public static final String REQ_STATUS ="req_status";
	 public static final String PRE_STATUS ="pre_status";
	 public static final String TXN_STATUS ="txn_status";
	 public static final String MESSAGE ="message";
	 public static final String SERVICE_TYPE ="servicetype";	 
	 public static final String RECORD_TYPE ="recordtype";	
	 public static final String CG_DETAILES ="cgdetails";	
	 public static final String DETAILSS="details";
	 public static final String SELECTORNAME = "selectornme";
	 public static final String FROM_SLAB = "fromslab";
	 public static final String TOSLAB = "toslab";
	 public static final String CGDESC = "cgdesc";
	 public static final String CGID = "cgid";
	 public static final String VALIDITY = "validity";
	 public static final String REVERSALALLWOED = "reversalallowed";
	 public static final String VOUCHERTYPE = "vouchertype";
	 public static final String NEWEXPIRYDATE = "newexpirydate";
	 public static final String EXPIRYCHANGEREASON= "expiry_change_reason";
	 public static final String SUBSCRIBERMSISDN= "subscriberMsisdn";
	 public static final String ASSOCIATEDVOUCHERS="associatedVoucherres";
	 public static final String VOUCHERSEGMENT= "vouchersegment";
	 public static final String QUANTITY= "quantity";
	 public static final String VOUCHERPROFILE= "voucherprofile";
	 public static final String DENOMINATION= "denomination";
	 public static final String CURRENTSTATUS="currentstatus";
	 public static final String APPROVALLEVEL="approvalLevel";
	 public static final String GEOGRAPHICALDOMAIN="geographicalDomain";
	 public static final String DOMAIN = "domain";
	 public static final String CHANNELCATEGORY ="category";
	 public static final String TRANSACTIONLIST = "transactionList";
	 public static final String TRANSFERSUTYPE =  "transferType";
	 public static final String TXNBATCHID = "txnbatchid";
	 public static final String PAYMENTINSTCODE = "paymentinstcode";
	 public static final String PAYMENTINSTNUM = "paymentinstnum";
	 public static final String TRANSFERID = "transferId";
	 public static final String LASTNAME = "lastname";
	 public static final String FISTNAME = "firstname";
	 public static final String CATEGORYNAME = "categoryname";
	 public static final String CATEGORYCODE = "categorycode";
	 public static final String USRNAMEPREFIX = "usernameprefix";
	 public static final String TRANSFERLIST = "transferList";
	 public static final String SERVICETYPE = "serviceType";
	 public static final String FROM_DATE = "fromDate";
	 public static final String TO_DATE = "toDate";
	 public static final String LASTMONTHCOUNT = "lastMonthCount";
	 public static final String PASSBOOKVIEW = "passbook";
	 public static final String TXNCALCVIEW = "txncalcview";
	 public static final String PREVIOUSFROMDATE = "previousFromDate";
	 public static final String PREVIOUSTODATE = "previousToDate";
	 public static final String TOTALCOUNT = "totalCount";
	 public static final String TOTALVALUE = "totalValue";
	 public static final String TOTALTRNXCOUNT ="totlaTrnxCount";
	 public static final String LASTMONTHVALUE="lastMonthValue";
	 public static final String ARGUMENTS="arguments";
	 public static final String VALIDITYPERIOD="validityPeriod";
	 public static final String CURRENTDATA="currentData";
	 public static final String PREVIOUSDATA="previousData";
	 public static final String TOPPRODUCTS="topProducts";
	 
	 public static final String PREVIOUSTOTALINCOME = "previousTotalIncome";
	 public static final String TOTALBASECOM = "totalBaseCom" ;
	 public static final String PREVIOUSTOTALBASECOMM = "previousTotalBaseComm";
	 public static final String TOTALADDITIONLBASECOM ="totalAdditionalBaseCom";
	 public static final String PREVIOUSTOTALADDITIONALBASECOM = "previousTotalAdditionalBaseCom";
	 public static final String  TOTALCAC = "totalCac";
	 public static final String PREVIOUSTOTALCAC = "previousTotalCac";
	 public static final String TOTALCBC = "totalCbc";
	 public static final String PREVIOUSTOTALCBC = "previousTotalCbc";
	 public static final String  TOTALINCOME = "totalIncome";
	 public static final String  DETAILINFO = "detailedinfo";
	 public static final String FROMROW="fromRow";
	 public static final String TOROW="toRow";
	 public static final String PAGENUMBER="pageNumber";
	 public static final String ENTRIESPERPAGE="entriesPerPage";
	 public static final String TRANSACTIONDETAIL= "transactionDetails";
	 public static final String COMMISSIONPROFILEID="commissionProfileID";
	 public static final String COMMISSIONPROFILEVERSION="commissionProfileVersion";
	 public static final String TRANSACTIONTYPE="transactionType";
	 public static final String TRANSFERSUBTYPE="transferSubType";
	 public static final String TRANSFERTYPE="transferType";
	 public static final String CBCFLAG="cbcflag";
	 public static final String DUALCOMMISSION="dualCommission";
	 public static final String MSISDNTOSEARCH= "msisdnToSearch" ;
	 public static final String LOGINIDTOSEARCH ="loginidToSearch";
	 public static final String USERNAMETOSEARCH = "usernameToSearch";
	 public static final String USERINFO="userinfo";
	 public static final String CHNLTRANSFERDETAILS="chnlTransferDetails";
	 public static final String DOMAINCODE= "domainCode";
	 public static final String CATEGORYCODE1 = "categoryCode";
	 public static final String FILETYPE = "fileType";
	 public static final String FILENAME ="fileName";
	 public static final String FILEATTACHMENT = "fileAttachment";
	 public static final String FILEUPLOADED = "fileUploaded";
	 public static final String TXNIDS = "txnids";
	 public static final String TXNIDFILE = "txnIdFile";
	 public static boolean isTagManadatory() {

        return MANDATORY;
    }

    public static boolean isTagOptinal() {
        return OPTIONAL;
    }
    
    /**
     * Constructor for PretupsValidator.
     */
    private RestTagValueValidation() {
    }
    
    public static void  validateFixedFields(String mandField,JsonNode mandFields,JsonNode data,RequestVO requestVO,Map<String,Object> map,List<String> arr) throws BTSLBaseException {
        final String methodName= "validateFixedFields";
        
        Boolean isUserEventRemark = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS);
        Boolean isVoucherProfileOptional = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_PROFLE_IS_OPTIONAL);
        
        switch (mandField) {
        case LANGUAGE1:
            XMLTagValueValidation.validateLanguage1(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            String s=getNodeValue(data, getNodeValue(mandFields,mandField));
            if(BTSLUtil.isNullString(s))
            {
            	s="0";
            }
            map.put(mandField.toUpperCase(),s);
            
            
            break;
            
        case LOGINID2:
            XMLTagValueValidation.validateLoginId2(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setReceiverLoginID(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
			 case USRLOGINID:
        	XMLTagValueValidation.validateuserLoginId(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setReceiverLoginID(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
 
        case MSISDN2:
        	if(("RCTRF").equalsIgnoreCase(requestVO.getServiceKeyword()) || ("MVD").equalsIgnoreCase(requestVO.getServiceKeyword()) || ("GRC").equalsIgnoreCase(requestVO.getServiceKeyword()) || 
        			("PPB").equalsIgnoreCase(requestVO.getServiceKeyword()) || ("INTRC").equalsIgnoreCase(requestVO.getServiceKeyword()) || ("EVD").equalsIgnoreCase(requestVO.getServiceKeyword()) || ("DVD").equalsIgnoreCase(requestVO.getServiceKeyword())){
        		 XMLTagValueValidation.validateMsisdn2(getNodeValue(data, getNodeValue(mandFields,mandField)),map.get(EXTNWCODE.toUpperCase()).toString(),RestTagValueValidation.isTagManadatory());
        	}
        	else{
            XMLTagValueValidation.validateMsisdn2(getNodeValue(data, getNodeValue(mandFields,mandField)),map.get(EXTNWCODE.toUpperCase()).toString(),RestTagValueValidation.isTagOptinal());
        	}
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setReceiverMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case EXTCODE2:
            XMLTagValueValidation.validateExtCode2(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setReceiverExtCode(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
            
        case PRODUCTCODE:
            XMLTagValueValidation.validateProductCode(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
           arr.add(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case QTY:
            XMLTagValueValidation.validateQuantity(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            arr.add(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;

        case AMOUNT :
           XMLTagValueValidation.validateAmount(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
            
        case LANGUAGE2 :
            XMLTagValueValidation.validateLanguage2(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());

            String s1=getNodeValue(data, getNodeValue(mandFields,mandField));
            if(BTSLUtil.isNullString(s1))
            {
            	s1="0";
            }
            map.put(mandField.toUpperCase(),s1);
            break;
            
        case SELECTOR :
            String selector = getNodeValue(data, getNodeValue(mandFields,mandField));
            if(BTSLUtil.isNullString(selector))
                selector = validateSelector(data, mandFields, requestVO, map.get(TYPE.toUpperCase()).toString());
            XMLTagValueValidation.validateSelector(selector,RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),selector);
            break;
        case NOTIF_MSISDN :
            String notif_msisdn = getNodeValue(data, getNodeValue(mandFields,mandField));
            map.put(mandField.toUpperCase(), notif_msisdn);
            break;
        case GIFTER_MSISDN :
            String msisdn = getNodeValue(data, getNodeValue(mandFields,mandField));
            map.put(mandField.toUpperCase(),msisdn);
            break; 
        case GIFTER_NAME :
            String name = getNodeValue(data, getNodeValue(mandFields,mandField));
            map.put(mandField.toUpperCase(),name);
            break;  
        case GIFTER_LANG :
            String lang = getNodeValue(data, getNodeValue(mandFields,mandField));
            map.put(mandField.toUpperCase(),lang);
            break;  
        case CONFIRMPIN:
        case NEWPIN:
            XMLTagValueValidation.validatePin(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            
            break;
        case TRFCATEGORY:
            XMLTagValueValidation.validateTrfCategoryCode(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
            
        case REFNUM:
            XMLTagValueValidation.validateRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setExternalReferenceNum(getNodeValue(data, getNodeValue(mandFields,mandField)));
           break;
        case PAYMENTTYPE:
        case PAYMENTINSTCODE:
            XMLTagValueValidation.validatePaymentType(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            arr.add(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case PAYMENTDATE:
        case PAYMENTINSTDATE:
            XMLTagValueValidation.validatePaymentDate(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            arr.add(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case PAYMENTNUM:
        case PAYMENTINSTNUM:
            XMLTagValueValidation.validatePaymentNumber(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            arr.add(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case REMARKS:
        	if(isUserEventRemark){
            XMLTagValueValidation.validateRemark(getNodeValue(data, getNodeValue(mandFields,mandField)),XMLTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setRemarks(map.get(mandField.toUpperCase()).toString());
            }
            break;
        case EXTTXNNUMBER:
            XMLTagValueValidation.validateExtRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case EXTTXNDATE:
            XMLTagValueValidation.validateDate(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case MSISDN1:
            XMLTagValueValidation.validateMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)),map.get(EXTNWCODE.toUpperCase()).toString(),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case CONTACTNUMBER:
        case MSISDN3:
            XMLTagValueValidation.validateMsisdn2(getNodeValue(data, getNodeValue(mandFields,mandField)),map.get(EXTNWCODE.toUpperCase()).toString(),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case SSN:
            XMLTagValueValidation.validateSsn(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case ADDRESS1:
        case ADDRESS2:
            XMLTagValueValidation.validateAddress(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case CITY:
            XMLTagValueValidation.validateCity(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case STATE:
            XMLTagValueValidation.validateState(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case COUNTRY:
            XMLTagValueValidation.validateCountryName(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case CONTACTPERSON:
            XMLTagValueValidation.validateContactPerson(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case GEOGRAPHYCODE:
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case PARENTMSISDN:
            if(!BTSLUtil.isNullString(getNodeValue(data, getNodeValue(mandFields,mandField)))){
                XMLTagValueValidation.validateMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)),map.get(EXTNWCODE.toUpperCase()).toString(), XMLTagValueValidation.isTagOptinal());
            }
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case EXTERNALCODE:
            XMLTagValueValidation.validateExtCode(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case PARENTEXTERNALCODE:
            XMLTagValueValidation.validateParentExtCode(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case FROM_USER_EXTCODE:
        case TO_PARENT_EXTCODE:        	
            XMLTagValueValidation.validateExtCode2(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case USERCATCODE:
        case TO_USER_CATEGORY_CODE:
            XMLTagValueValidation.validateCategoryCode(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case USERNAME:
            XMLTagValueValidation.validateUserName(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case SHORTNAME:
            XMLTagValueValidation.validateShortName(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case USERNAMEPREFIX:
            XMLTagValueValidation.validateUserNamePrefix(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case SUBSCRIBERCODE:
            XMLTagValueValidation.validateSubscriberCode(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case EMAILID:
            XMLTagValueValidation.validateEmailId(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case WEBLOGINID:
            XMLTagValueValidation.validateLoginId(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case WEBPSWD:
            XMLTagValueValidation.validatePassword(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case USERMSISDN:
            XMLTagValueValidation.validateUserMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)),map.get(EXTNWCODE.toUpperCase()).toString(),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setReceiverMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case FROM_USER_MSISDN:
            XMLTagValueValidation.validateFromUserMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)),map.get(EXTNWCODE.toUpperCase()).toString(),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case TO_PARENT_MSISDN:
        	 XMLTagValueValidation.validateparentMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)),map.get(EXTNWCODE.toUpperCase()).toString(),RestTagValueValidation.isTagManadatory());
             map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
             break;
        case TO_USER_GEOGRAPHICAL_CODE:
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case ACTION:
            XMLTagValueValidation.validateSuspendResumeAction(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
             break;
        case FROMDATE:
        case TODATE:
            XMLTagValueValidation.validateDate(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case TRANSFERID:
        case TRANSACTIONID:
        	XMLTagValueValidation.validateTxnId(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
        	map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case GEOGRAPHYTYPE:
            XMLTagValueValidation.validateGeographyType(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case PARENTGEOGRAPHYCODE:
            XMLTagValueValidation.validateParentGeographyCode(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case GEOCODE:
            XMLTagValueValidation.validateGeographyCode(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case GEOGRAPHYNAME:
            XMLTagValueValidation.validateGeographyName(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case GEOGRAPHYSHORTNAME:
            XMLTagValueValidation.validateGeographyShortName(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case GEOGRAPHYDESC:
            XMLTagValueValidation.validateGeographyDescription(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case GEOGRAPHYDEFAULTFLAG:
            XMLTagValueValidation.validateGeographyDefaultFlag(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case GEOGRAPHYACTION:
                XMLTagValueValidation.validateGeographyAction(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
                map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
                break;
        
        case DESIGNATION:
                XMLTagValueValidation.validateDesignation(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
                map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
                break;
                
        case CATCODE:
                XMLTagValueValidation.validateCategoryCode(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
                map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
                break;
        case INSUSPEND:
            XMLTagValueValidation.validateInSuspend(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case OUTSUSPEND:
            XMLTagValueValidation.validateOutSuspend(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case COMPANY:
            XMLTagValueValidation.validateCompany(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case FAX:
            XMLTagValueValidation.validateFax(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case NEWEXTERNALCODE:
            XMLTagValueValidation.validateExtCode(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case MOBILENUMBER:
            XMLTagValueValidation.validateMobileNumber(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;	
        case EXTREFNUM:
            XMLTagValueValidation.validateRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setExternalReferenceNum(map.get(mandField.toUpperCase()).toString());    
            break;	
        case SERVICE_TYPE:
            XMLTagValueValidation.validateServiceType(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setEnquiryServiceType(map.get(mandField.toUpperCase()).toString());    
            break;	
                 
            
        case MSISDNLIST:
            List msisdnlist = new ArrayList<String>();
            msisdnlist.add(map.get(MSISDN1.toUpperCase()));
            msisdnlist.add(map.get(MSISDN2.toUpperCase()));
            msisdnlist.add(map.get(MSISDN3.toUpperCase()));
            map.put(mandField.toUpperCase(), msisdnlist);
            break;
        case FROM_SERIAL_NO:
        	XMLTagValueValidation.validateSerialNo(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case TO_SERIAL_NO:
        	XMLTagValueValidation.validateSerialNo(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
        case STATUS:
        	XMLTagValueValidation.validateStatus(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            break;
		case TXNID:
				XMLTagValueValidation.validateTxnId(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
				map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
				requestVO.setTransactionID(map.get(mandField.toUpperCase()).toString());
				break; 
		case VOUCHERTYPE:
    		XMLTagValueValidation.validateVoucherType(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
	      	  map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
	            break; 
		case NEWEXPIRYDATE:
			try {
				XMLTagValueValidation.validateExpiryDate(getNodeValue(data, getNodeValue(mandFields,mandField)),XMLTagValueValidation.isTagManadatory());
			} catch (ParseException e) {
				if (log.isDebugEnabled()) {
					log.debug("RestTagValueValidation" + methodName, e);
				}
			}
	      	  	map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
	            break; 
		case EXPIRYCHANGEREASON:
	        XMLTagValueValidation.validateExpiryChangeReason(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
	        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
	        requestVO.setExpiryChangeReason(getNodeValue(data, getNodeValue(mandFields,mandField)));
	        break;
		case VOUCHERSEGMENT:
    		XMLTagValueValidation.validateVoucherSegment(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
	      	map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
	        break;
		case QUANTITY:
    		XMLTagValueValidation.validateQuantity(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
	      	map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
	        break;
		 case VOUCHERPROFILE:
			 if("0".equals(getNodeValue(data, getNodeValue(mandFields,mandField)))){
					throw new BTSLBaseException("RestTagValueValidation", methodName, PretupsErrorCodesI.VOUCHER_PRODUCT_INVALID);
				}
		    	if (isVoucherProfileOptional && BTSLUtil.isNullString(getNodeValue(data, getNodeValue(mandFields,mandField)))){ 
		          	map.put(mandField.toUpperCase(),"0");

		    	}else{
		    		XMLTagValueValidation.validateVoucherProfile(getNodeValue(data, getNodeValue(mandFields,mandField)), RestTagValueValidation.isTagManadatory());
		          	map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
		    	}
		    break;
		case DENOMINATION:
			XMLTagValueValidation.validateDenomination(getNodeValue(data, getNodeValue(mandFields,mandField)), RestTagValueValidation.isTagManadatory());
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
		case APPROVALLEVEL:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
		case GEOGRAPHICALDOMAIN:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
		case DOMAIN:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
		case CATEGORY1:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
		case CHANNELCATEGORY:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
		case TRANSFERSUTYPE:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
	   case SERVICETYPE:
            XMLTagValueValidation.validateServiceType(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
            map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
            requestVO.setEnquiryServiceType(map.get(mandField.toUpperCase()).toString());    
            break;	
	   case FROM_DATE:
		    XMLTagValueValidation.validateFromToDate(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory(),FROM_DATE);
	           map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
	           break;
       case TO_DATE:
		    XMLTagValueValidation.validateFromToDate(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory(),TO_DATE);
           map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
           break;
       case TOPPRODUCTS:
		    XMLTagValueValidation.validateTopProductFlag(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory(),TOPPRODUCTS);
	           map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
	           break;
      case NOOFPRODORDENO:
		    XMLTagValueValidation.validateNoOfProd(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory(),NOOFPRODORDENO);
          map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
          break;
      case FROMROW:
          XMLTagValueValidation.validateDesignation(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
          map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
          break;
      case TOROW:
          XMLTagValueValidation.validateDesignation(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
          map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
          break;
      case PAGENUMBER:
          XMLTagValueValidation.validateDesignation(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
          map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
          break;
      case REQUESTTYPE:
    	  XMLTagValueValidation.validateDesignation(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
          map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
          break;
      case ENTRIESPERPAGE:
          XMLTagValueValidation.validateDesignation(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
          map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
          break;
      case COMMISSIONPROFILEID:
    	  map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
          break;
      case COMMISSIONPROFILEVERSION:
    	  map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
    	  break;
      case TRANSFERSUBTYPE:
    	  map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
    	  break;
      case TRANSACTIONTYPE:
    	  map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
    	  break;
      case CBCFLAG:
    	  map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
    	  break;
      case DUALCOMMISSION:
    	  map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
    	  break;
      case MSISDNTOSEARCH:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
      case LOGINIDTOSEARCH:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
      case USERNAMETOSEARCH:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
      case DOMAINCODE:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
	case CATEGORYCODE1:
			map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
			break;
	case FILETYPE:
		map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
		break;
	case FILENAME:
		map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
		break;
	case FILEATTACHMENT:
		map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
		break;
	case FILEUPLOADED:
		map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
		break;
	
		default:
            break;
        }
        LogFactory.printLog(methodName, "mandatoryfield"+mandField+"map.get(mandField.toUpperCase())"+map.get(mandField.toUpperCase()), log);
        
    }
    
    
	public static void generateMandFieldsResponse(String mandfield,JsonNode json,JsonObject obj,RequestVO requestVO,Object list,boolean arrList,JsonArray jsonarr)throws Exception {
       Map map = requestVO.getRequestMap();
       Object summaryList=null;   
       HashMap  responseMap=requestVO.getResponseMap();
       SenderVO senderVO=null;
       LogFactory.printLog("generateMandFieldsResponse", "Entered requestVO:" + requestVO, log);
       
       Boolean isTargetBasedCommission = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_COMMISSION);
       Boolean isTargetBasedBaseCommission = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TARGET_BASED_BASE_COMMISSION);
       Integer xmlMaxRcdSumResp = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.XML_MAX_RCD_SUM_RESP);
       Boolean isChannelTransferInfoRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
       
        switch (mandfield) {
         
        case   PREVIOUSTOTALINCOME :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalIncome()));
            	}
            	break;
        case   TOTALADDITIONLBASECOM :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalAdditionalBaseCom()));
            	}
            	break;
        case   PREVIOUSTOTALBASECOMM :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalBaseComm()));
            	}
        	break;
        case   TOTALBASECOM :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalBaseCom()));
            	}
        	break;
        case   PREVIOUSTOTALADDITIONALBASECOM :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalAdditionalBaseCom()));
            	} 	
        	break;
        case   TOTALCAC :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null && isTargetBasedCommission){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalCac()));
            	}
            	break; 	
        case   PREVIOUSTOTALCAC :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null && isTargetBasedCommission){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalCac()));
            	}
            	break; 
        case   TOTALCBC :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null && isTargetBasedBaseCommission){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalCbc()));
            	}
            	break; 
        case   PREVIOUSTOTALCBC :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null && isTargetBasedBaseCommission){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getPreviousTotalCbc()));
            	}
            	break; 
        case   TOTALINCOME :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null){
        		TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
            	obj.addProperty(mandfield,PretupsBL.getDisplayAmount(totalIncomeDetailsViewVO.getTotalIncome1()));
            	}
            	break;
        case   DETAILINFO :
        	if(responseMap!=null && responseMap.get("detailedinfo")!=null){
        	TotalUserIncomeDetailsVO totalIncomeDetailsViewVO = (TotalUserIncomeDetailsVO) responseMap.get("detailedinfo");
        	Gson gson = new GsonBuilder().setExclusionStrategies(new CustomExclusionTotalIncomeDetailedRespVO()).create();
       		 obj.add(mandfield,gson.toJsonTree(totalIncomeDetailsViewVO.getDetailedInfoList())); 
       	  }
      	  break; 	
        case LASTNAME:
        	if(map!=null && map.get("LASTNAME")!=null){
        	obj.addProperty(mandfield,(String) map.get("LASTNAME"));
        	}
        	break;
        case FISTNAME:
        	if(map!=null && map.get("FIRSTNAME")!=null){
        	obj.addProperty(mandfield,(String) map.get("FIRSTNAME"));
        	}
        	break;
        case CATEGORYNAME: 
        	if(map!=null && map.get("CATEGORYNAME")!=null){
        	obj.addProperty(mandfield,(String) map.get("CATEGORYNAME"));
        	}
        	break;
        case CATEGORYCODE:
        	if(map!=null && map.get("CATEGORYCODE")!=null){
        	obj.addProperty(mandfield,(String) map.get("CATEGORYCODE"));
        	}
        	break;
        case USRNAMEPREFIX:
        	if(map!=null && map.get("USERNAMEPREFIX")!=null){
        	obj.addProperty(mandfield,(String) map.get("USERNAMEPREFIX"));
        	}
        	break;
        case TRANSACTIONLIST:
        	obj.add(mandfield,new Gson().toJsonTree(requestVO.getChannelTransfersList()));
        	break;
        case ASSOCIATEDVOUCHERS:
        	 if(list instanceof VomsVoucherVO){
        		 VomsVoucherVO vomsVoucherVO = (VomsVoucherVO)list;
        		 generateMyVoucherEnquieryResp(mandfield,json,jsonarr,vomsVoucherVO);  
         }
             obj.add(mandfield, jsonarr);
        	break;
        case SERVICE_TYPE:
        	obj.addProperty(mandfield,requestVO.getEnquiryServiceType());
        	break;
        case TXNID:
            generateTxnIDResponse( mandfield,json, obj, requestVO,list);
            break;
        case RECEIVERTRFVALUE:
                 
                if(list instanceof C2STransferVO)
                {	
                C2STransferVO c2sTransferVO = (C2STransferVO)list;
                if(!BTSLUtil.isNullObject(c2sTransferVO))
                {obj.addProperty(mandfield, c2sTransferVO.getReceiverTransferValueAsString()); 
                LogFactory.printLog("generateMandFieldsResponse", "Entered RECEIVERTRFVALUE:" + c2sTransferVO.getReceiverTransferValueAsString(), log);
                }
                }
            break;
        case RECEIVERACCESSVALUE:
        	if(list instanceof C2STransferVO)
            {	
            C2STransferVO c2sTransferVO1 = (C2STransferVO)list; 
        	if(!BTSLUtil.isNullObject(c2sTransferVO1))
            {obj.addProperty(mandfield, c2sTransferVO1.getReceiverAccessFeeAsString()); 
            LogFactory.printLog("generateMandFieldsResponse", "Entered RECEIVERACCESSVALUE:" + c2sTransferVO1.getReceiverAccessFeeAsString(), log);
            }
            }
            break;    
        case ERRORCODE:
        	if(isEnquirySummaryReq(map)){
        		summaryList=getEnquirySummary(list);
	        	 if(summaryList instanceof C2STransferVO){
	        		obj.addProperty(getNodeValue(json, mandfield), (((C2STransferVO) summaryList).getErrorCode()!=null?((C2STransferVO) summaryList).getErrorCode():""));
	                 }
       	  }
        	if(!requestVO.isSuccessTxn()){
                obj.addProperty(getNodeValue(json, mandfield), requestVO.getMessageCode());                
            }else{
                obj.addProperty(getNodeValue(json, mandfield), BLANK);
            }
            break;
            
        case ARGUMENTS:
        	if(!requestVO.isSuccessTxn())
        	{
        		obj.add(mandfield,new Gson().toJsonTree(requestVO.getMessageArguments())); 
        	}
        	break;
        case PRODUCTSRES:
            if(list instanceof UserBalancesVO){
            UserBalancesVO balance = (UserBalancesVO)list;
            addDetailJsonArray(mandfield,json,jsonarr,balance.getProductCode(),balance.getProductShortName(),balance.getBalanceStr());
            }
            break;
        case RECORDS:
        case PRODUCTS:
            if(arrList){
                obj.add(mandfield, jsonarr);
            }
            break;
        case RECORDSRES:
                if(list instanceof C2STransferVO){
                    C2STransferVO c2stransferVO = (C2STransferVO)list;
                    addDetailJsonArray(mandfield,json,jsonarr,c2stransferVO.getProductShortCode(),c2stransferVO.getProductName(),PretupsBL.getDisplayAmount(c2stransferVO.getTransferValue()));  
            }else if(list instanceof ChannelTransferVO){
                ChannelTransferVO channneltransferVO = (ChannelTransferVO)list;
                ChannelTransferItemsVO channelTransferItemvo =(ChannelTransferItemsVO) channneltransferVO.getChannelTransferitemsVOList().get(0);
                addDetailJsonArray(mandfield,json,jsonarr,Long.toString(channelTransferItemvo.getProductShortCode()),channelTransferItemvo.getProductName(),PretupsBL.getDisplayAmount(channelTransferItemvo.getApprovedQuantity()));
        }
            break;
        case TXNTYPE:
            if(map!=null&&map.get(LASTTRFTYPE)!=null){
                obj.addProperty(getNodeValue(json, mandfield), map.get(LASTTRFTYPE).toString());
            }
            break;
         case TXNDATE:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
        	 }
             generateTxnDateResponse(mandfield, json, obj, list,summaryList);
            break;
         case TXNSTATUS:
             generateTxnStatusResponse(mandfield, json, obj, list);
             break;

        case EXTTXNDATE:
        	 if(map!=null){
             obj.addProperty(mandfield, map.get(mandfield.toUpperCase()).toString());
        	 }
             break;
         case USERID:
             if(list instanceof ChannelUserVO){
             obj.addProperty(mandfield, ((ChannelUserVO) list).getUserID());
             }
             break;
         case EXTCODE:
             if(list instanceof ChannelUserVO){
             obj.addProperty(mandfield, ((ChannelUserVO) list).getExternalCode());
             }
             break;
         case MSISDN1:
             if(list instanceof ChannelUserVO){
             obj.addProperty(mandfield, ((ChannelUserVO) list).getMsisdn());
             }
             break;
         case RECORDTYPE:
        	 if(map.get(TYPE.toUpperCase()).toString().equalsIgnoreCase("chcgenreq"))
        	 {
        		obj.addProperty(mandfield, CG_DETAILES);
        		String responseMessage=requestVO.getSenderReturnMessage();
        		if(!BTSLUtil.isNullString(responseMessage)){
        			String[] responseMessageArray=responseMessage.split(",");
     			
        			if(responseMessageArray!=null){
        			for(int i=0;i<responseMessageArray.length;i++)
     				{
     					String responseSubMessage=responseMessageArray[i];
     					String [] responseSubMessageArray =responseSubMessage.split(":");
     					addCGDetailsJsonArray(mandfield,json,jsonarr,responseSubMessageArray[5],responseSubMessageArray[4],responseSubMessageArray[1],responseSubMessageArray[2],responseSubMessageArray[3],responseSubMessageArray[0],responseSubMessageArray[6],responseSubMessageArray[7]);
     					
     				}
        			}
     					
        		}
        		obj.add(DETAILSS, jsonarr);
        	 }else{
        		 if(null!=map)
             	 	obj.addProperty("DETAILS", (String)map.get(RES_TYPE));
             	 	 
        	 }
        	 
        	 break;
         case TRANSACTIONID:   
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	        	 if(summaryList instanceof ChannelTransferVO){
	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getTransferID());
	                 }
        	 }
             break;
        case NETWORK:
        	if(isEnquirySummaryReq(map)){
        		summaryList=getEnquirySummary(list);
        		if(summaryList instanceof ChannelTransferVO){
                    obj.addProperty(mandfield, ((ChannelTransferVO)summaryList).getNetworkCode());
                    }
        	}            
             break;
         case DOMNAME:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getDomainCode());
	             }
        	 }
             break;
         case CATEGORY:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);         		
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getCategoryCode());
	             }
        	 }
             break;
         case GEONAME:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getGraphicalDomainCode());
	             }
        	 }
             break;
         case TRFCATEGORY:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getTransferCategory());
	             }
        	 }
             break;
         case TRFTYPE:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getTransferType());
	             }
        	 }
             break;
         case MSISDN:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getUserMsisdn());
	             }
        	 }
             break;
         case EXTTXNNUMBER:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getExternalTxnNum());
	             }
        	 }
             break;
         case REMARKS:
        	 if(isChannelTransferInfoRequired && isEnquirySummaryReq(map) )
             {	 summaryList=getEnquirySummary(list);
	        	 if(summaryList instanceof ChannelTransferVO){
		             obj.addProperty(mandfield, (((ChannelTransferVO) summaryList).getChannelRemarks()!=null?((ChannelTransferVO) summaryList).getChannelRemarks():""));
		             }
	        	 else if(summaryList instanceof C2STransferVO){
		             obj.addProperty(mandfield, (((C2STransferVO) summaryList).getInfo1()!=null?((C2STransferVO) summaryList).getInfo1():""));
		             }
             }
             break;
         case INFO1:
        	 if(isChannelTransferInfoRequired && isEnquirySummaryReq(map) )
             {		summaryList=getEnquirySummary(list);
		             if(summaryList instanceof ChannelTransferVO){
		            	 obj.addProperty(mandfield, (((ChannelTransferVO) summaryList).getInfo1()!=null?((ChannelTransferVO) summaryList).getInfo1():""));
		             }
		             else if(summaryList instanceof C2STransferVO){
		            	 obj.addProperty(mandfield, (((C2STransferVO) summaryList).getInfo2()!=null?((C2STransferVO) summaryList).getInfo2():""));
		             }
             }
             break;
         case INFO2:
        	 if(isChannelTransferInfoRequired && isEnquirySummaryReq(map))
             {  	summaryList=getEnquirySummary(list);
		             if(summaryList instanceof ChannelTransferVO){
		            	 obj.addProperty(mandfield, (((ChannelTransferVO) summaryList).getInfo2()!=null?((ChannelTransferVO) summaryList).getInfo2():""));
		             }
		             if(summaryList instanceof C2STransferVO){
		            	 obj.addProperty(mandfield, (((C2STransferVO) summaryList).getInfo3()!=null?((C2STransferVO) summaryList).getInfo3():""));
		             }
             }
             break;
         case COMMPRF:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	            	 obj.addProperty(mandfield, (((ChannelTransferVO) summaryList).getCommProfileName()!=null?((ChannelTransferVO) summaryList).getCommProfileName():""));
	             }
        	 }
        	 else if(list instanceof ChannelUserVO && ((ChannelUserVO) list).getUserType().equals(PretupsI.CHANNEL_USER_TYPE)){
                 obj.addProperty(mandfield, ((ChannelUserVO) list).getCommissionProfileSetName());
             }
             break;
         case STATUS:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getStatus());
	             }
	             else if(summaryList instanceof C2STransferVO){
		             obj.addProperty(mandfield, ((C2STransferVO) summaryList).getTransferStatus());
		             }
        	 }
        	 else if(list instanceof ChannelUserVO){
                 obj.addProperty(mandfield, ((ChannelUserVO) list).getStatusDesc());
             }
             break;
         case PAYMENTINSTTYPE:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getPayInstrumentType());
	             }
        	 }
             break;
         case PAYMENTINSTNUMBER:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
         		if(summaryList instanceof ChannelTransferVO){
         			obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getPayInstrumentNum());
         		}
        	 }
             break;
         case PAYMENTINSTDATE:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	            	 if(null!=((ChannelTransferVO) summaryList).getPayInstrumentDate()){
	            		  obj.addProperty(mandfield,BTSLUtil.getDateStringFromDate(((ChannelTransferVO) summaryList).getPayInstrumentDate())); 
	            	 }else           
	             	 obj.addProperty(mandfield,BLANK);
	             }
        	 }
             break;
         case PAYMENTINSTAMT:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, PretupsBL.getDisplayAmount(((ChannelTransferVO) summaryList).getPayInstrumentAmt()));
	             }
        	 }
             break;
         case FIRSTAPPREMARKS:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	            	 obj.addProperty(mandfield, (((ChannelTransferVO) summaryList).getFirstApprovalRemark()!=null?((ChannelTransferVO) summaryList).getFirstApprovalRemark():""));
	             }
        	 }
             break;
         case SECONDAPPREMARKS:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelUserVO){
	            	 obj.addProperty(mandfield, (((ChannelTransferVO) summaryList).getSecondApprovalRemark()!=null?((ChannelTransferVO) summaryList).getSecondApprovalRemark():""));
	             }
        	 }
             break;
         case THIRDAPPREMARKS:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelUserVO){
	            	 obj.addProperty(mandfield, (((ChannelTransferVO) summaryList).getThirdApprovalRemark()!=null?((ChannelTransferVO) summaryList).getThirdApprovalRemark():""));
	             }
        	 }
             break;
         case REQSOURCE:
        	 if(isEnquirySummaryReq(map)){
         		summaryList=getEnquirySummary(list);
	             if(summaryList instanceof ChannelTransferVO){
	             obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getSource());
	             }
	             else if(summaryList instanceof C2STransferVO){
		             obj.addProperty(mandfield, ((C2STransferVO) summaryList).getSourceType());
		             }
        	 }
             break;
             case USERNAME:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getUserName());
                 }
                 break;
              
             case USRLOGINID:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getLoginID());
                 }
                 break;
              
             case SHORTNAME:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getShortName());
                 }
                 break;
              
             case SUBSCRIBERCODE:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getEmpCode());
                 }
                 break;
              
             case INSUSPEND:
                 if(list instanceof ChannelUserVO && ((ChannelUserVO) list).getUserType().equals(PretupsI.CHANNEL_USER_TYPE)){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getInSuspend());
                 }
                 break;
              
             case OUTSUSPEND:
                 if(list instanceof ChannelUserVO && ((ChannelUserVO) list).getUserType().equals(PretupsI.CHANNEL_USER_TYPE)){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getOutSuspened());
                 }
                 break;
              
             case ADDRESS:
                 StringBuilder sbf = new StringBuilder(1024);
                 if(list instanceof ChannelUserVO){
                     if (!BTSLUtil.isNullString(((ChannelUserVO) list).getAddress1()) && ((ChannelUserVO) list).getAddress1() != null) {
                         sbf.append(((ChannelUserVO) list).getAddress1());
                     }
                     if (!BTSLUtil.isNullString(((ChannelUserVO) list).getAddress1()) && ((ChannelUserVO) list).getAddress1() != null
                             && !BTSLUtil.isNullString(((ChannelUserVO) list).getAddress2()) && ((ChannelUserVO) list)
                         .getAddress2() != null) {
                         sbf.append(", " + ((ChannelUserVO) list).getAddress2());
                     }
                     if (!BTSLUtil.isNullString(((ChannelUserVO) list).getAddress2()) && ((ChannelUserVO) list).getAddress2() != null) {
                         sbf.append(((ChannelUserVO) list).getAddress2());
                     }
                     obj.addProperty(mandfield, ""+sbf);
                 }
                 break;
              
              
              
              
             case USERGRADE:
                 if(list instanceof ChannelUserVO && ((ChannelUserVO) list).getUserType().equals(PretupsI.CHANNEL_USER_TYPE)){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getUserGradeName());
                 }
                 break;
              
          case TRFPRF:
                 if(list instanceof ChannelUserVO && ((ChannelUserVO) list).getUserType().equals(PretupsI.CHANNEL_USER_TYPE)){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getTransferProfileName());
                 }
                 break;
              
          case SERVICES:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getServiceTypes());
                 }else{
                	 obj.addProperty(mandfield, requestVO.getServiceType());
                 }
                 break;
              
          case PARENTNAME:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getParentName());
                 }
                 break;
              
          case PARENTMSISDN:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getParentMsisdn());
                 }
                 break;
             
          case PARENTCATEGORY:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getParentCategoryName());
                 }
                 break;
              
          case OWNERNAME:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getOwnerName());
                 }
                 break;
              
          case OWNERMSISDN:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getOwnerMsisdn());
                 }
                 break;
              
          case OWNERCATEGORY:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getOwnerCategoryName());
                 }
                 break;
              
          case DESIGNATION:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getDesignation());
                 }
                 break;
              
          case DIVISION:
                 if(list instanceof ChannelUserVO && ((ChannelUserVO) list).getUserType().equals(PretupsI.OPERATOR_USER_TYPE)){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getDivisionDesc());
                 }
                 break;
              
          case DEPARTMENT:
                 if(list instanceof ChannelUserVO && ((ChannelUserVO) list).getUserType().equals(PretupsI.OPERATOR_USER_TYPE)){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getDepartmentDesc());
                 }
                 break;
              
          case CITY:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getCity());
                 }
                 break;
              
          case STATE:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getState());
                 }
                 break;
              
          case COUNTRY:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getCountry());
                 }
                 break;
              
          case EMAILID:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getEmail());
                 }
                 break;
              
          case APPOINTMENTDATE:
                 if(list instanceof ChannelUserVO && ((ChannelUserVO) list).getAppointmentDate()!=null){
                     obj.addProperty(mandfield, BTSLUtil.getDateStringFromDate(((ChannelUserVO) list).getAppointmentDate()));
                 }else{
                     obj.addProperty(mandfield,""); 
                 }
                 break;
              
          case CREATEDBY:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getCreatedByUserName());
                 }
                 break;
              
          case CREATEDON:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, BTSLUtil.getDateStringFromDate(((ChannelUserVO) list).getCreatedOn()));
                 }
                 break;
              
          case ALLOWEDIP:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getAllowedIps());
                 }
                 break;
              
          case ALLOWEDDAYS:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getAllowedDays());
                 }
                 break;
              
          case ALLOWEDTIMEFROM:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getFromTime());
                 }
                 break;
              
          case ALLOWEDTIMETO:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getToTime());
                 }
                 break;
              
          case CONTACTPERSON:
                 if(list instanceof ChannelUserVO && ((ChannelUserVO) list).getUserType().equals(PretupsI.CHANNEL_USER_TYPE)){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getContactPerson());
                 }
                 break;
              
          case SSN:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getSsn());
                 }
                 break;
              
          case COMPANY:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getCompany());
                 }
                 break;
              
          case FAX:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getFax());
                 }
                 break;
              
          case GEOGRAPHYCODE:
                 if(list instanceof ChannelUserVO){
                     obj.addProperty(mandfield, ((ChannelUserVO) list).getGeographicalCode());
                 }
                 break;
              
          case GEOGRAPHYTYPE:
                 if(list instanceof GeographicalDomainVO){
                     obj.addProperty(mandfield, ((GeographicalDomainVO) list).getGrphDomainType());
                 }
                 break;
              
          case PARENTGEOGRAPHYCODE:
                 if(list instanceof GeographicalDomainVO){
                     obj.addProperty(mandfield, ((GeographicalDomainVO) list).getParentDomainCode());
                 }
                 break;
              
          case GEOCODE:
                 if(list instanceof GeographicalDomainVO){
                     obj.addProperty(mandfield, ((GeographicalDomainVO) list).getGrphDomainCode());
                 }
                 break;
              
          case GEOGRAPHYNAME:
                 if(list instanceof GeographicalDomainVO){
                     obj.addProperty(mandfield, ((GeographicalDomainVO) list).getGrphDomainName());
                 }
                 break;
              
          case GEOGRAPHYSHORTNAME:
                 if(list instanceof GeographicalDomainVO){
                     obj.addProperty(mandfield, ((GeographicalDomainVO) list).getGrphDomainShortName());
                 }
                 break;
              
          case GEOGRAPHYDESC:
                 if(list instanceof GeographicalDomainVO){
                     obj.addProperty(mandfield, ((GeographicalDomainVO) list).getDescription());
                 }
                 break;
              
          case GEOGRAPHYDEFAULTFLAG:
                 if(list instanceof GeographicalDomainVO){
                     obj.addProperty(mandfield, ((GeographicalDomainVO) list).getIsDefault());
                 }
                 break;
          case FROM_MSISDN:   
         	 if(isEnquirySummaryReq(map)){
          		summaryList=getEnquirySummary(list);
 	        	 if(summaryList instanceof ChannelTransferVO){
 	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getFromUserCode());
 	                 }
         	 }
              break;
          case MSISDN2:   
          	 if(isEnquirySummaryReq(map)){
           		summaryList=getEnquirySummary(list);
  	        	 if(summaryList instanceof ChannelTransferVO){
  	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getToUserCode());
  	                 }
  	        	if(summaryList instanceof C2STransferVO){
 	                 obj.addProperty(mandfield, ((C2STransferVO) summaryList).getReceiverMsisdn());
 	                 }
          	 }
               break;
          case USERNAME1:   
           	 if(isEnquirySummaryReq(map)){
            		summaryList=getEnquirySummary(list);
   	        	 if(summaryList instanceof ChannelTransferVO){
   	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getFromUserName());
   	                 }
           	 }
                break;
          case USERNAME2:   
           	 if(isEnquirySummaryReq(map)){
            		summaryList=getEnquirySummary(list);
   	        	 if(summaryList instanceof ChannelTransferVO){
   	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getToUserName());
   	                 }
           	 }
                break;
          case TRFSTYPE:   
            	 if(isEnquirySummaryReq(map)){
             		summaryList=getEnquirySummary(list);
    	        	 if(summaryList instanceof ChannelTransferVO){
    	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getTransferSubType());
    	                 }
            	 }
                 break;
          case GEONAME1:   
            	 if(isEnquirySummaryReq(map)){
             		summaryList=getEnquirySummary(list);
    	        	 if(summaryList instanceof ChannelTransferVO){
    	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getGraphicalDomainCode());
    	                 }
            	 }
                 break;
          case GEONAME2:   
            	 if(isEnquirySummaryReq(map)){
             		summaryList=getEnquirySummary(list);
    	        	 if(summaryList instanceof ChannelTransferVO){
    	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getReceiverGgraphicalDomainCode());
    	                 }
            	 }
                 break;

          case DOMNAME1:   
         	 if(isEnquirySummaryReq(map)){
          		summaryList=getEnquirySummary(list);
 	        	 if(summaryList instanceof ChannelTransferVO){
 	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getDomainCode());
 	                 }
         	 }
              break;
          case DOMNAME2:   
          	 if(isEnquirySummaryReq(map)){
           		summaryList=getEnquirySummary(list);
  	        	 if(summaryList instanceof ChannelTransferVO){
  	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getReceiverDomainCode());
  	                 }
          	 }
               break;
          case CATEGORY1:   
          	 if(isEnquirySummaryReq(map)){
           		summaryList=getEnquirySummary(list);
  	        	 if(summaryList instanceof ChannelTransferVO){
  	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getSenderCatName());
  	                 }
          	 }
               break;
          case CATEGORY2:   
          	 if(isEnquirySummaryReq(map)){
           		summaryList=getEnquirySummary(list);
  	        	 if(summaryList instanceof ChannelTransferVO){
  	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getReceiverCategoryDesc());
  	                 }
          	 }
               break;
          case GRADE1:   
          	 if(isEnquirySummaryReq(map)){
           		summaryList=getEnquirySummary(list);
  	        	 if(summaryList instanceof ChannelTransferVO){
  	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getSenderGradeCode());
  	                 }
          	 }
               break;
          case GRADE2:   
          	 if(isEnquirySummaryReq(map)){
           		summaryList=getEnquirySummary(list);
  	        	 if(summaryList instanceof ChannelTransferVO){
  	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getReceiverGradeCode());
  	                 }
          	 }
               break;
          case USERID1:   
          	 if(isEnquirySummaryReq(map)){
           		summaryList=getEnquirySummary(list);
  	        	 if(summaryList instanceof ChannelTransferVO){
  	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getFromUserID());
  	                 }
          	 }
               break;
          case USERID2:   
          	 if(isEnquirySummaryReq(map)){
           		summaryList=getEnquirySummary(list);
  	        	 if(summaryList instanceof ChannelTransferVO){
  	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getToUserID());
  	                 }
          	 }
               break;
          case CONTROLLED:   
          	 if(isEnquirySummaryReq(map)){
           		summaryList=getEnquirySummary(list);
  	        	 if(summaryList instanceof ChannelTransferVO){
  	                 obj.addProperty(mandfield, ((ChannelTransferVO) summaryList).getControlTransfer());
  	                 }
          	 }
               break;
          case SENDERMSISDN:   
           	 if(isEnquirySummaryReq(map)){
            		summaryList=getEnquirySummary(list);
   	        	 if(summaryList instanceof C2STransferVO){
   	                 obj.addProperty(mandfield, ((C2STransferVO) summaryList).getSenderMsisdn());
   	                 }
           	 }
                break;
          case SUBSERVICE:   
           	 if(isEnquirySummaryReq(map)){
            		summaryList=getEnquirySummary(list);
   	        	 if(summaryList instanceof C2STransferVO){
   	                 obj.addProperty(mandfield, ((C2STransferVO) summaryList).getSubService());
   	                 }
           	 }
                break;
          case TRFVALUE:   
           	 if(isEnquirySummaryReq(map)){
            		summaryList=getEnquirySummary(list);
   	        	 if(summaryList instanceof C2STransferVO){
   	                 obj.addProperty(mandfield, ((C2STransferVO) summaryList).getTransferValueStr());
   	                 }
           	 }
                break;
          case PRODUCTCODE:   
           	 if(isEnquirySummaryReq(map)){
            		summaryList=getEnquirySummary(list);
   	        	 if(summaryList instanceof C2STransferVO){
   	                 obj.addProperty(mandfield, ((C2STransferVO) summaryList).getProductCode());
   	                 }
           	 }
                break;
          case SACCFEE:   
           	 if(isEnquirySummaryReq(map)){
            		summaryList=getEnquirySummary(list);
   	        	 if(summaryList instanceof C2STransferVO){
   	                 obj.addProperty(mandfield, ((C2STransferVO) summaryList).getSenderAccessFeeAsString());
   	                 }
           	 }
                break;
          case RACCFEE:   
           	 if(isEnquirySummaryReq(map)){
            		summaryList=getEnquirySummary(list);
   	        	 if(summaryList instanceof C2STransferVO){
   	                 obj.addProperty(mandfield, ((C2STransferVO) summaryList).getReceiverAccessFeeAsString());
   	                 }
           	 }
                break;
         case RECORDRES:
         	 if(isEnquirySummaryReq(map)){         		
 	        	 if(SUMMARY.equalsIgnoreCase((String) map.get(RES_TYPE))){
 	        		 ArrayList summList = (ArrayList)(((HashMap) list).get(SUMMARY));
 	        		 final int listSize = summList.size() > (int)xmlMaxRcdSumResp ? (int)xmlMaxRcdSumResp : summList.size();
 	                 for (int i = 0; i < listSize; i++) {
 	                	 ChannelTransferVO transferVO = (ChannelTransferVO) summList.get(i);
 	                	 if(null!=transferVO)
 	                		 addO2CEnquirySummaryJsonArray(mandfield,json,jsonarr,transferVO);
 	                	 obj.add(mandfield, jsonarr);
 	        	     }
 	        	 }else if(DETAILS.equalsIgnoreCase((String) map.get(RES_TYPE))){
 	        		 ArrayList detailsList = (ArrayList)(((HashMap) list).get(DETAILS));
 	        		 ChannelTransferItemsVO transferItemVO = (ChannelTransferItemsVO)detailsList.get(0);
 	        		 if(null!=transferItemVO)
 	        			addO2CEnquiryDetailJsonArray(mandfield,json,jsonarr,transferItemVO);
 	            	 obj.add(mandfield, jsonarr);
 	        	   }
         	 }
              break;
          case C2CSUMMARYRES:  
        	  if(isEnquirySummaryReq(map)){         		
  	        	 if(SUMMARY.equalsIgnoreCase((String) map.get(RES_TYPE))){
  	        		 ArrayList summList = (ArrayList)(((HashMap) list).get(SUMMARY));
  	        		 final int listSize = summList.size() > (int)xmlMaxRcdSumResp ? (int)xmlMaxRcdSumResp : summList.size();
  	                 for (int i = 0; i < listSize; i++) {
  	                	 ChannelTransferVO transferVO = (ChannelTransferVO) summList.get(i);
  	                	 if(null!=transferVO)
  	                		 addC2CEnquirySummaryJsonArray(mandfield,json,jsonarr,transferVO);
  	                	 obj.add(mandfield, jsonarr);
  	        	     }
  	        	 }
          	 }
               break;
          case C2CDETAILRES:  
        	  if(isEnquirySummaryReq(map)){         		
        		  if(DETAILS.equalsIgnoreCase((String) map.get(RES_TYPE))){
  	        		 ArrayList detailsList = (ArrayList)(((HashMap) list).get(DETAILS));
  	        		 ChannelTransferItemsVO transferItemVO = (ChannelTransferItemsVO)detailsList.get(0);
  	        		 if(null!=transferItemVO)
  	        			addC2CEnquiryDetailJsonArray(mandfield,json,jsonarr,transferItemVO);
  	            	 obj.add(mandfield, jsonarr);
  	        	   }
          	 }
               break;
          case C2SSUMMARYRES:  
        	  if(isEnquirySummaryReq(map)){         		
  	        	 if(SUMMARY.equalsIgnoreCase((String) map.get(RES_TYPE))){
  	        		 ArrayList summList = (ArrayList)(((HashMap) list).get(SUMMARY));
  	        		 final int listSize = summList.size() > (int)xmlMaxRcdSumResp ? (int)xmlMaxRcdSumResp : summList.size();
  	                 for (int i = 0; i < listSize; i++) {
  	                	C2STransferVO transferVO = (C2STransferVO) summList.get(i);
  	                	 if(null!=transferVO)
  	                		 addC2SEnquirySummaryJsonArray(mandfield,json,jsonarr,transferVO);
  	                	 obj.add(mandfield, jsonarr);
  	        	     }
  	        	 }
          	 }
               break;
          case C2SDETAILRES:  
        	  if(isEnquirySummaryReq(map)){         		
        		  if(DETAILS.equalsIgnoreCase((String) map.get(RES_TYPE))){
  	        		 ArrayList detailsList = (ArrayList)(((HashMap) list).get(DETAILS));
  	        		C2STransferItemVO transferItemVO = (C2STransferItemVO)detailsList.get(0);
  	        		 if(null!=transferItemVO)
  	        			addC2SEnquiryDetailJsonArray(mandfield,json,jsonarr,transferItemVO);
  	            	 obj.add(mandfield, jsonarr);
  	        	   }
          	 }
               break;
               
          case REGSTATUS:
        	  senderVO = (SenderVO) requestVO.getSenderVO();
              obj.addProperty(mandfield,senderVO.getRegistered());
        	  break;       
          case PINSTATUS:
        	  senderVO = (SenderVO) requestVO.getSenderVO();
        	  String pinStatus= PretupsI.NO;
        	  if(PretupsI.YES.equalsIgnoreCase(senderVO.getRegistered())) {
                  pinStatus = PretupsI.YES;
        	  }
              obj.addProperty(mandfield,pinStatus);
        	  break;       
        	  
          case MINREBAL:
        	  senderVO = (SenderVO) requestVO.getSenderVO();
              obj.addProperty(mandfield,PretupsBL.convertExponential((senderVO.getMinResidualBalanceAllowed())));
        	  
        	  break;       
          case MINAMT:
        	  senderVO = (SenderVO) requestVO.getSenderVO();
              obj.addProperty(mandfield,PretupsBL.convertExponential((senderVO.getMinTxnAmountAllowed())));
        	  
        	  break;    	  
          case MAXAMT:
        	  senderVO = (SenderVO) requestVO.getSenderVO();
              obj.addProperty(mandfield,PretupsBL.convertExponential((senderVO.getMaxTxnAmountAllowed())));
        	  
        	  break;
        	  
          case FROM_SERIAL_NO:
        	 responseMap=requestVO.getResponseMap();
        	  responseMap=requestVO.getResponseMap();
        	  if(responseMap==null)
        	  {
        		  obj.addProperty(mandfield,"");
        	  }else{
        		  obj.addProperty(mandfield,(String)responseMap.get(VOMSI.FROM_SERIAL_NO));  
        	  }
        	  
        	  break;  	
          case TO_SERIAL_NO:
        	   responseMap=requestVO.getResponseMap();
        	  if(responseMap==null)
        	  {
        		  obj.addProperty(mandfield,"");
        	  }else{
        		  obj.addProperty(mandfield,(String)responseMap.get(VOMSI.TO_SERIAL_NO));  
        	  }
        	  
        	  
        	  break;  	
          case REQ_STATUS:
       	   responseMap=requestVO.getResponseMap();
     	  if(responseMap==null)
     	  {
     		  obj.addProperty(mandfield,"");
     	  }else{
     		  obj.addProperty(mandfield,(String)responseMap.get(VOMSI.REQ_STATUS));  
     	  }
     	  
        	  break;  	
          case PRE_STATUS:
        	  responseMap=requestVO.getResponseMap();
         	  if(responseMap==null)
         	  {
         		  obj.addProperty(mandfield,"");
         	  }else{
         		  obj.addProperty(mandfield,(String)responseMap.get(VOMSI.PRE_STATUS));  
         	  }
        	  break;  	
          case TXN_STATUS:
        	  responseMap=requestVO.getResponseMap();
         	  if(responseMap==null)
         	  {
         		  obj.addProperty(mandfield,"");
         	  }else{
         		  obj.addProperty(mandfield,(String)responseMap.get(VOMSI.TXNSTATUS_TAG));  
         	  }
        	  break;  	
          case MESSAGE:
        	  responseMap=requestVO.getResponseMap();
         	  if(responseMap==null)
         	  {
         		  obj.addProperty(mandfield,"");
         	  }else{
         		  obj.addProperty(mandfield,(String)responseMap.get(VOMSI.MESSAGE_TAG));  
         	  }
        	  break;  	
        	  	    	     	  	  
          case ACCNTBAL:
        	  senderVO = (SenderVO) requestVO.getSenderVO();
        	  if(senderVO!= null)
              {
              	if(senderVO.getAccountBalance() != null){
              		obj.addProperty(mandfield,senderVO.getAccountBalance());
              	}else{
              		obj.addProperty(mandfield,"0");
              	}
              	
              } else {
            	  obj.addProperty(mandfield,"");
              }
              break;  	
          case TXNBATCHID:
        	  obj.addProperty(mandfield,requestVO.getTxnBatchId());  
  			break;
  			
          case TRANSFERLIST:
        	    if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
           		obj.add(mandfield,new Gson().toJsonTree(responseMap.get("transferList")));
           	  }
          	  break;  
          case LASTMONTHCOUNT:
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
           		obj.addProperty(mandfield,(String)responseMap.get("lastMonthCount"));
           	  }
          	  break; 
          case PASSBOOKVIEW:
        	  responseMap=requestVO.getResponseMap();
         	  if(responseMap==null)
         	  {
         		  obj.addProperty(mandfield,"");
         	  }else{
         		 obj.add(mandfield,new Gson().toJsonTree(responseMap.get("map"))); 
         	  }
        	  break; 
          case TXNCALCVIEW:
        	  responseMap=requestVO.getResponseMap();
         	  if(responseMap==null)
         	  {
         		  obj.addProperty(mandfield,"");
         	  }else{
         		 obj.add(mandfield,new Gson().toJsonTree(responseMap.get("map"))); 
         	  }
        	  break; 

          case FROM_DATE:
          	
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else
           	  {
           		obj.addProperty(mandfield,(String)responseMap.get("fromDate"));
           	  }
          	  break; 
          case TO_DATE:
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
           		  obj.addProperty(mandfield,(String)responseMap.get("toDate"));
           	  }
          	  break; 
          case PREVIOUSFROMDATE:
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
        	  obj.addProperty(mandfield,(String)responseMap.get("previousFromDate"));
           	  }
          	  break; 	  
          	  
          case PREVIOUSTODATE:
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
        	  obj.addProperty(mandfield,(String)responseMap.get("previousToDate"));
           	  }
          	  break;
        	  
          case TOTALCOUNT:
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
           		  obj.addProperty(mandfield,(Long)responseMap.get("totalCount"));
           	  }
          	  break;
        	  
          case TOTALVALUE:
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
        	  obj.addProperty(mandfield,(Long)responseMap.get("totalValue"));
           	  }
          	  break;
          case TOTALTRNXCOUNT:
        	  if(!requestVO.isSuccessTxn()) {
        		  obj.addProperty(mandfield, "");
        	  }else {
        		  obj.addProperty(mandfield,requestVO.getC2sTotaltxnCount());
            	  break;
        		  }
        	 
          case LASTMONTHVALUE:
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
        	  obj.addProperty(mandfield,(String)responseMap.get("lastMonthValue"));
           	  }
          	  break; 
          case VALIDITYPERIOD:
        	  if(requestVO.isSuccessTxn()) {
        		  responseMap=requestVO.getResponseMap();
            	  if(responseMap==null)
               	  {
               		  obj.addProperty(mandfield,"");
               	  }else{
               		obj.addProperty(mandfield,(String)responseMap.get("validityPeriod"));
               	  }
        	  }
        	  
          	  break; 
          case CURRENTDATA:
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
        	  obj.add(mandfield,new Gson().toJsonTree(responseMap.get("currentData")));
           	  }
          	  break;
          case PREVIOUSDATA:
        	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
        	  obj.add(mandfield,new Gson().toJsonTree(responseMap.get("previousData")));
           	  }
          	  break;
          case TRANSACTIONDETAIL:
         	 if(requestVO.isSuccessTxn()) {
         		responseMap=requestVO.getResponseMap();
           	  if(responseMap==null)
           	  {
           		  obj.addProperty(mandfield,"");
           	  }else{
           		 obj.add(mandfield,new Gson().toJsonTree(responseMap.get("map"))); 
           	  }
       	  }
        	  break; 
        	  
          case USERINFO:
        	  if(requestVO.isSuccessTxn()) {
        		  responseMap=requestVO.getResponseMap();
        		  if(responseMap==null)
        			  obj.addProperty(mandfield, "");
        		  else
        			  obj.add(mandfield, new GsonBuilder().serializeNulls().create().toJsonTree(responseMap.get("userDetails"))); 
        	  }
        	  break;
          case CHNLTRANSFERDETAILS:
          	 if(requestVO.isSuccessTxn()) {
          		responseMap=requestVO.getResponseMap();
            	  if(responseMap==null)
            	  {
            		  obj.addProperty(mandfield,"");
            	  }else{
            		 obj.add(mandfield,new Gson().toJsonTree(responseMap.get("map"))); 
            	  }
        	  }
         	  break; 
          case TXNIDS:
        	  generateTxnIDArrResponse(mandfield,json, obj, requestVO,list);
        	  break;
          case TXNIDFILE:
        	  generateTxnIdFileResponse(mandfield,json, obj, requestVO,list);
        	  break;
          default:
             break;
    }
        
    }
    
    private static String validateSelector(JsonNode data,JsonNode mandFields,RequestVO requestVO,String type ){
         String selector = null;
         String defFrcxmlSelC2S = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEF_FRCXML_SEL_C2S);
        if(!BTSLUtil.isNullString(defFrcxmlSelC2S)){
            selector=defFrcxmlSelC2S;
            return selector;
        }else{
            Map seviceKeywordMap = new HashMap();
            seviceKeywordMap = ServiceKeywordCache.getServiceKeywordMap();
            final ServiceKeywordCacheVO serviceKeywordCacheVO = (ServiceKeywordCacheVO) seviceKeywordMap.get(type.toUpperCase() + "_" + requestVO.getModule() + "_" + requestVO.getRequestGatewayType() + "_" + requestVO.getServicePort());
            if(serviceKeywordCacheVO!=null){
                String selAction=serviceKeywordCacheVO.getServiceType();
                if(selAction.equalsIgnoreCase(PretupsI.SERVICE_TYPE_CHNL_RECHARGE)&&!BTSLUtil.isNullString(getNodeValue(data, getNodeValue(mandFields,PRODUCTCODE)))){
                    selAction=getNodeValue(data, getNodeValue(mandFields,PRODUCTCODE))+selAction;
                }
                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(selAction);
                if(serviceSelectorMappingVO!=null){
                selector = serviceSelectorMappingVO.getSelectorCode();
                
                }
                
            }
            return selector;   
    }
    }  
    private static String getNodeValue(JsonNode node,String value){
        if(node.get(value)!=null){
            return node.get(value).textValue();
        }else{
            return "";
        }
    }
    private static void addDetailJsonArray(String mandfield,JsonNode json,JsonArray jsonArray ,String productCode,String productname,String balance){
        JsonNode arr =json.get(mandfield);
        JsonObject record =  new JsonObject();
        record.addProperty(arr.get(PRODUCTCODE).textValue(), productCode);
        record.addProperty(arr.get(PRODUCTSHORTNAME.toLowerCase()).textValue(), productname);
        record.addProperty(arr.get(BALANCE.toLowerCase()).textValue(), balance);
        jsonArray.add(record);
    }
    
    
    
    
    
    private static void addO2CEnquirySummaryJsonArray(String mandfield,JsonNode json,JsonArray jsonArray ,ChannelTransferVO transferVO){
        JsonNode arr =json.get(mandfield);
        Boolean isChannelTransferInfoRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
        JsonObject record =  new JsonObject();
        record.addProperty(arr.get(TRANSACTIONID).textValue(), transferVO.getTransferID());
        record.addProperty(arr.get(TXNDATE).textValue(), transferVO.getTransferDateAsString());
        record.addProperty(arr.get(NETWORK).textValue(), transferVO.getNetworkCode());
        record.addProperty(arr.get(MSISDN).textValue(), transferVO.getUserMsisdn());
        record.addProperty(arr.get(TRFCATEGORY).textValue(), transferVO.getTransferCategory());
        record.addProperty(arr.get(APPROVEDBY).textValue(), transferVO.getFinalApprovedBy());
        record.addProperty(arr.get(APPROVEDON).textValue(), transferVO.getFinalApprovedDateAsString());
        record.addProperty(arr.get(TRFTYPE).textValue(), transferVO.getTransferType());
        record.addProperty(arr.get(REQVALUE).textValue(), PretupsBL.getDisplayAmount(transferVO.getRequestedQuantity()));
        record.addProperty(arr.get(AMOUNT).textValue(), PretupsBL.getDisplayAmount(transferVO.getPayableAmount()));
        record.addProperty(arr.get(STATUS).textValue(), transferVO.getStatus());
        if(isChannelTransferInfoRequired){
        	 record.addProperty(arr.get(REMARKS).textValue(), transferVO.getChannelRemarks()!=null?transferVO.getChannelRemarks():"");
             record.addProperty(arr.get(INFO1).textValue(), transferVO.getInfo1()!=null?transferVO.getInfo1():"");
             record.addProperty(arr.get(INFO2).textValue(), transferVO.getInfo2()!=null?transferVO.getInfo2():"");
        }
        jsonArray.add(record);
    }
    
    private static void addO2CEnquiryDetailJsonArray(String mandfield,JsonNode json,JsonArray jsonArray , ChannelTransferItemsVO transferItemVO){
        JsonNode arr =json.get(mandfield);
        JsonObject record =  new JsonObject();
        record.addProperty(arr.get(PRODCODE).textValue(), transferItemVO.getProductCode());
        record.addProperty(arr.get(MRP).textValue(), transferItemVO.getProductMrpStr());
        record.addProperty(arr.get(REQVALUE).textValue(), PretupsBL.getDisplayAmount(Double.parseDouble(transferItemVO.getRequestedQuantity())));
        record.addProperty(arr.get(TAX1R).textValue(), transferItemVO.getTax1Rate());
        record.addProperty(arr.get(TAX1A).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getTax1Value()));
        record.addProperty(arr.get(TAX2R).textValue(), transferItemVO.getTax2RateAsString());
        record.addProperty(arr.get(TAX2A).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getTax2Value()));
        record.addProperty(arr.get(TAX3R).textValue(), transferItemVO.getTax3Rate());
        record.addProperty(arr.get(TAX3A).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getTax3Value()));
        record.addProperty(arr.get(COMMR).textValue(),transferItemVO.getCommRate());
        record.addProperty(arr.get(COMMA).textValue(),PretupsBL.getDisplayAmount(transferItemVO.getCommValue()));
        record.addProperty(arr.get(AMOUNT).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getPayableAmount()));
        record.addProperty(arr.get(NETAMT).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getNetPayableAmount()));
        jsonArray.add(record);
    }
    
    private static void addC2CEnquirySummaryJsonArray(String mandfield,JsonNode json,JsonArray jsonArray ,ChannelTransferVO transferVO){
        JsonNode arr =json.get(mandfield);
        Boolean isChannelTransferInfoRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
        JsonObject record =  new JsonObject();
        record.addProperty(arr.get(TRANSACTIONID).textValue(), transferVO.getTransferID());
        record.addProperty(arr.get(TXNDATE).textValue(), transferVO.getTransferDateAsString());
        record.addProperty(arr.get(NETWORK).textValue(), transferVO.getNetworkCode());
        record.addProperty(arr.get(TRFSTYPE).textValue(), transferVO.getTransferSubType());
        record.addProperty(arr.get(REQVALUE).textValue(), PretupsBL.getDisplayAmount(transferVO.getRequestedQuantity()));
        record.addProperty(arr.get(AMOUNT).textValue(), PretupsBL.getDisplayAmount(transferVO.getPayableAmount()));
        record.addProperty(arr.get(FROM_MSISDN).textValue(), transferVO.getFromUserCode());
        record.addProperty(arr.get(MSISDN2).textValue(), transferVO.getToUserCode());
        record.addProperty(arr.get(TRFCATEGORY).textValue(), transferVO.getTransferCategoryCode());
        record.addProperty(arr.get(REQSOURCE).textValue(), transferVO.getSource());
        if(isChannelTransferInfoRequired){
        record.addProperty(arr.get(REMARKS).textValue(), transferVO.getChannelRemarks()!=null?transferVO.getChannelRemarks():"");
        record.addProperty(arr.get(INFO1).textValue(), transferVO.getInfo1()!=null?transferVO.getInfo1():"");
        record.addProperty(arr.get(INFO2).textValue(), transferVO.getInfo2()!=null?transferVO.getInfo2():"");
        }
        if (PretupsI.YES.equalsIgnoreCase(transferVO.getControlTransfer())) {
        	 record.addProperty(arr.get(CONTROLLED).textValue(), PretupsI.YES);
         } else {
        	 record.addProperty(arr.get(CONTROLLED).textValue(), PretupsI.NO);
        }
        jsonArray.add(record);
    }
    
    private static void addC2CEnquiryDetailJsonArray(String mandfield,JsonNode json,JsonArray jsonArray , ChannelTransferItemsVO transferItemVO){
        JsonNode arr =json.get(mandfield);
        JsonObject record =  new JsonObject();
        record.addProperty(arr.get(PRODCODE).textValue(), transferItemVO.getProductCode());
        record.addProperty(arr.get(REQVALUE).textValue(), PretupsBL.getDisplayAmount(Double.parseDouble(transferItemVO.getRequestedQuantity())));
        record.addProperty(arr.get(TAX1R).textValue(), transferItemVO.getTax1Rate());
        record.addProperty(arr.get(TAX1A).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getTax1Value()));
        record.addProperty(arr.get(TAX2R).textValue(), transferItemVO.getTax2RateAsString());
        record.addProperty(arr.get(TAX2A).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getTax2Value()));
        record.addProperty(arr.get(TAX3R).textValue(), transferItemVO.getTax3Rate());
        record.addProperty(arr.get(TAX3A).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getTax3Value()));
        record.addProperty(arr.get(COMMR).textValue(),transferItemVO.getCommRate());
        record.addProperty(arr.get(COMMA).textValue(),PretupsBL.getDisplayAmount(transferItemVO.getCommValue()));
        record.addProperty(arr.get(AMOUNT).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getPayableAmount()));
        record.addProperty(arr.get(NETAMT).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getNetPayableAmount()));
        jsonArray.add(record);
    }
    
    private static void addC2SEnquirySummaryJsonArray(String mandfield,JsonNode json,JsonArray jsonArray ,C2STransferVO transferVO){
        JsonNode arr =json.get(mandfield);
        Boolean isChannelTransferInfoRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
        JsonObject record =  new JsonObject();
        record.addProperty(arr.get(TRANSACTIONID).textValue(), transferVO.getTransferID());
        record.addProperty(arr.get(TXNDATE).textValue(), transferVO.getTransferDateStr());
        record.addProperty(arr.get(NETWORK).textValue(), transferVO.getNetworkCode());
        record.addProperty(arr.get(SENDERMSISDN).textValue(), transferVO.getSenderMsisdn());
        record.addProperty(arr.get(MSISDN2).textValue(), transferVO.getReceiverMsisdn());
        record.addProperty(arr.get(SUBSERVICE).textValue(), transferVO.getSubService());
        record.addProperty(arr.get(TRFVALUE).textValue(), transferVO.getTransferValueStr());
        record.addProperty(arr.get(PRODUCTCODE).textValue(), transferVO.getProductCode());
        record.addProperty(arr.get(REQSOURCE).textValue(), transferVO.getSourceType());
        record.addProperty(arr.get(STATUS).textValue(), transferVO.getTransferStatus());
        record.addProperty(arr.get(ERRORCODE).textValue(), transferVO.getErrorCode()!=null?transferVO.getErrorCode():"");
        if(isChannelTransferInfoRequired){
        record.addProperty(arr.get(REMARKS).textValue(), transferVO.getInfo1()!=null?transferVO.getInfo1():"");
        record.addProperty(arr.get(INFO1).textValue(), transferVO.getInfo2()!=null?transferVO.getInfo2():"");
        record.addProperty(arr.get(INFO2).textValue(), transferVO.getInfo3()!=null?transferVO.getInfo3():"");
        }        
        jsonArray.add(record);
    }
    
    private static void addC2SEnquiryDetailJsonArray(String mandfield,JsonNode json,JsonArray jsonArray , C2STransferItemVO transferItemVO) throws ParseException{
        JsonNode arr =json.get(mandfield);
        JsonObject record =  new JsonObject();
        record.addProperty(arr.get(MSISDN).textValue(), transferItemVO.getMsisdn());
        record.addProperty(arr.get(USRTYPE).textValue(),transferItemVO.getUserType());
        record.addProperty(arr.get(ENTRYTYPE).textValue(), transferItemVO.getEntryType());
        record.addProperty(arr.get(TRANSVALUE).textValue(),transferItemVO.getTransferValueStr());
        record.addProperty(arr.get(TRFDATE).textValue(), BTSLUtil.getDateStringFromDate(transferItemVO.getTransferDate()));
        record.addProperty(arr.get(PRVBAL).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getPreviousBalance()));
        record.addProperty(arr.get(POSTBAL).textValue(), PretupsBL.getDisplayAmount(transferItemVO.getPostBalance()));
        record.addProperty(arr.get(SUBTYPE).textValue(), transferItemVO.getSubscriberType()==null?"":transferItemVO.getSubscriberType());
        record.addProperty(arr.get(ENTRYDATE).textValue(), BTSLUtil.getDateTimeStringFromDate(transferItemVO.getEntryDateTime()));
        record.addProperty(arr.get(SRVCLCODE).textValue(), transferItemVO.getServiceClassCode()!=null?transferItemVO.getServiceClassCode():"");
        record.addProperty(arr.get(TRFSTATUS).textValue(), transferItemVO.getTransferStatus());
        record.addProperty(arr.get(ACCSTATUS).textValue(), transferItemVO.getAccountStatus()!=null?transferItemVO.getAccountStatus():"");
        record.addProperty(arr.get(REFID).textValue(), transferItemVO.getReferenceID()!=null?transferItemVO.getReferenceID():"");
        jsonArray.add(record);
    }
    
    private static void generateTxnIDResponse(String mandfield,JsonNode json,JsonObject obj,RequestVO requestVO,Object list){
        
        if(!BTSLUtil.isNullString(requestVO.getTransactionID())){
            obj.addProperty(getNodeValue(json, mandfield), requestVO.getTransactionID());                
            }else if(list instanceof C2STransferVO){
                C2STransferVO c2sTransferVO = (C2STransferVO)list; 
                obj.addProperty(getNodeValue(json, mandfield), c2sTransferVO.getTransferID()); 
            }else if(list instanceof ChannelTransferVO){
                ChannelTransferVO channelTransferVO = (ChannelTransferVO)list; 
                obj.addProperty(getNodeValue(json, mandfield), channelTransferVO.getTransferID()); 
            }else{
                obj.addProperty(getNodeValue(json, mandfield), BLANK);
            }
    }
   
  private static void generateTxnIDArrResponse(String mandfield,JsonNode json,JsonObject obj,RequestVO requestVO,Object list) throws BTSLBaseException{
	  JsonObject obj1 = null;
      JsonArray objArr = new JsonArray();
		if(!BTSLUtil.isNullObject(list) && list instanceof ArrayList<?>){
			try{
                ArrayList<VomsVoucherVO> vomsVoucherVOlist = (ArrayList<VomsVoucherVO>)list; 
                for(VomsVoucherVO vomsVoucherVO: vomsVoucherVOlist){
    	  			obj1 = new JsonObject();
	  				XMLTagValueValidation.validateTxnId(vomsVoucherVO.getTransactionID(),RestTagValueValidation.isTagManadatory());
	                obj1.addProperty("txnid",vomsVoucherVO.getTransactionID() ); 
	                objArr.add(obj1);
                }
        	}catch(Exception e){
        		ArrayList<String> txnList = (ArrayList<String>)list;
        		for(String txnid : txnList){
    	  			obj1 = new JsonObject();
	  				XMLTagValueValidation.validateTxnId(txnid,RestTagValueValidation.isTagManadatory());
	                obj1.addProperty("txnid",txnid); 
	                objArr.add(obj1);
        		}
        	}
            obj.add(getNodeValue(json, mandfield), objArr);
		}
  		else{
            obj.addProperty(getNodeValue(json, mandfield), BLANK);
        }
    }
  
  private static void generateTxnIdFileResponse(String mandfield,JsonNode json,JsonObject obj,RequestVO requestVO,Object list) throws BTSLBaseException, IOException{
		if(!BTSLUtil.isNullObject(list) && list instanceof ArrayList<?>){
          List<List<String>> rows = new ArrayList<>();
          try{
              ArrayList<VomsVoucherVO> vomsVoucherVOlist = (ArrayList<VomsVoucherVO>)list; 
        	  for(VomsVoucherVO vomsVoucherVO: vomsVoucherVOlist){
			    rows.add( Arrays.asList(vomsVoucherVO.getTransactionID()));
        	  }
           }catch(Exception ex){
      		  ArrayList<String> txnList = (ArrayList<String>)list;
        	  for(String txnid: txnList){
  			    rows.add( Arrays.asList(txnid));
            }
          }
			String filePathCons = Constants.getProperty("DownloadFilePathForMultipleVoucherDownload");
			String filePathConstemp = filePathCons + "temp/";        
			createDirectory(filePathConstemp);
			String fileName = "MVD" + (System.currentTimeMillis()); 
			writeCSV(rows,filePathConstemp+fileName+ ".csv");
			File error =new File(filePathConstemp+fileName+ ".csv");
			byte[] fileContent = FileUtils.readFileToByteArray(error);
	   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
	   		
	   		obj.addProperty(getNodeValue(json, mandfield), encodedString);
	   		obj.addProperty("fileName", fileName+".csv");
		}
		else{
          obj.addProperty(getNodeValue(json, mandfield), BLANK);
          obj.addProperty("fileName", BLANK);
      }
}

  public static void writeCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
  	FileWriter csvWriter = new FileWriter(excelFilePath);
  	csvWriter.append("Transaction ID");
  	csvWriter.append("\n");

  	for (List<String> rowData : listBook) {
  	    csvWriter.append(String.join(Constants.getProperty("C2S_TXN_LOG_SEPARATOR"), rowData));
  	    csvWriter.append("\n");
  	}

  	csvWriter.flush();
  	csvWriter.close();
      
  }
	private static void createDirectory(String filePathConstemp) throws BTSLBaseException {

		String methodName = "createDirectory";
		File fileTempDir = new File(filePathConstemp);
		if (!fileTempDir.isDirectory()) {
			fileTempDir.mkdirs();
		}
		if (!fileTempDir.exists()) {
			log.debug("Directory does not exist : ", fileTempDir);
			throw new BTSLBaseException("OAuthenticationUtil", methodName,
					PretupsErrorCodesI.BATCH_UPLOAD_DIRECTORY_DO_NOT_EXISTS, PretupsI.RESPONSE_FAIL, null); // provide
																											// your own
		}
	}
 private static void generateTxnDateResponse(String mandfield,JsonNode json,JsonObject obj,Object list,Object summaryList) throws Exception{
        
	 if(list instanceof C2STransferVO){
         C2STransferVO c2sTransferVO = (C2STransferVO)list; 
         obj.addProperty(getNodeValue(json, mandfield), BTSLUtil.getDateStringFromDate(c2sTransferVO.getTransferDateTime())); 
     }else if(list instanceof ChannelTransferVO){
         ChannelTransferVO channelTransferVO = (ChannelTransferVO)list; 
         obj.addProperty(getNodeValue(json, mandfield), BTSLUtil.getDateStringFromDate(channelTransferVO.getCreatedOn())); 
     }else if (summaryList instanceof ChannelTransferVO){
    	 ChannelTransferVO channelTransferVO = (ChannelTransferVO)summaryList; 
          obj.addProperty(getNodeValue(json, mandfield), channelTransferVO.getTransferDateAsString()); 
     }
     else{
    	 obj.addProperty(getNodeValue(json, mandfield),BLANK); 
     }
    }



private static void generateTxnStatusResponse(String mandfield,JsonNode json,JsonObject obj,Object list) throws Exception{
    
    if(list instanceof C2STransferVO){
        C2STransferVO c2sTransferVO = (C2STransferVO)list; 
        obj.addProperty(getNodeValue(json, mandfield), c2sTransferVO.getValue()); 
    }else if(list instanceof ChannelTransferVO){
        ChannelTransferVO channelTransferVO = (ChannelTransferVO)list; 
        obj.addProperty(getNodeValue(json, mandfield),channelTransferVO.getStatus()); 
    }else{
        obj.addProperty(getNodeValue(json, mandfield),BLANK); 
    }
   }

private static boolean isEnquirySummaryReq(Map map) throws Exception{
	boolean isO2CEnqSummReq=false;
	if(map!=null){
	if(null!= map.get(RES_TYPE) && (DETAILS.equalsIgnoreCase((String) map.get(RES_TYPE)) || SUMMARY.equalsIgnoreCase((String) map.get(RES_TYPE)))){
			isO2CEnqSummReq= true;	      	 
	 	}
	}
	return isO2CEnqSummReq;
   }

private static Object getEnquirySummary(Object list) throws Exception{
	Object summList =null;
	summList = ((ArrayList)(((HashMap) list).get(SUMMARY))).get(0); 	
	return summList;
   }

public static void  validateOptionalFields(String optionField,JsonNode optionalFields,JsonNode data,RequestVO requestVO,Map<String,Object> map,List<String> arr) throws BTSLBaseException {
    final String methodName= "validateOptionalFields";
    switch (optionField) {
    case INFO1:
        //XMLTagValueValidation.validate(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
        map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
        requestVO.setInfo1(map.get(optionField.toUpperCase()).toString());    
        break;	
    case INFO2:
       // XMLTagValueValidation.validateRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
        map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
        requestVO.setInfo2(map.get(optionField.toUpperCase()).toString());    
        break;	
    case INFO3:
        // XMLTagValueValidation.validateRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
         map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
         requestVO.setInfo3(map.get(optionField.toUpperCase()).toString());    
         break;	
    case INFO4:
        // XMLTagValueValidation.validateRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
         map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
         requestVO.setInfo4(map.get(optionField.toUpperCase()).toString());    
         break;	
    case INFO5:
        // XMLTagValueValidation.validateRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
         map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
         requestVO.setInfo5(map.get(optionField.toUpperCase()).toString());    
         break;	
    case CELLID:
        // XMLTagValueValidation.validateRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
         map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
         requestVO.setCellId(map.get(optionField.toUpperCase()).toString());    
         break;	
    case SWITCHID:
        // XMLTagValueValidation.validateRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
         map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
         requestVO.setSwitchId(map.get(optionField.toUpperCase()).toString());    
         break;	
    case AMOUNT :
       XMLTagValueValidation.validateAmount(getNodeValue(data, getNodeValue(optionalFields,optionField)),RestTagValueValidation.isTagOptinal());
        map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
        break;
    case EXTREFNUM :
    	 XMLTagValueValidation.validateExtRefNum(getNodeValue(data, getNodeValue(optionalFields,optionField)), XMLTagValueValidation.isTagOptinal());
         map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
         break;
    case SERIALNUMBER :
   	 	XMLTagValueValidation.validateSerialNo(getNodeValue(data, getNodeValue(optionalFields,optionField)), XMLTagValueValidation.isTagOptinal());
        map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
        requestVO.setSerialnumber(getNodeValue(data, getNodeValue(optionalFields,optionField)));
        break;
    case MSISDN2 :
        XMLTagValueValidation.validateMsisdn2(getNodeValue(data, getNodeValue(optionalFields,optionField)),RestTagValueValidation.isTagOptinal());
        map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
        requestVO.setReceiverMsisdn(getNodeValue(data, getNodeValue(optionalFields,optionField)));
        break;
    case REMARKS :
        XMLTagValueValidation.validateRemark(getNodeValue(data, getNodeValue(optionalFields,optionField)),RestTagValueValidation.isTagOptinal());
        map.put(optionField.toUpperCase(),getNodeValue(data, getNodeValue(optionalFields,optionField)));
        requestVO.setRemarks(getNodeValue(data, getNodeValue(optionalFields,optionField)));
        break;
    default:
        break;
    }
    LogFactory.printLog(methodName, "optionField"+optionField+"map.get(mandField.toUpperCase())"+map.get(optionField.toUpperCase()), log);
    
}



public static void  validateP2PFixedFields(String mandField,JsonNode mandFields,JsonNode data,RequestVO requestVO,Map<String,Object> map,List<String> arr) throws BTSLBaseException {
    final String methodName= "validateFixedFields";
    switch (mandField) {
    case LANGUAGE1:
        XMLTagValueValidation.validateLanguage1(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
        
        String s=getNodeValue(data, getNodeValue(mandFields,mandField));
        if(BTSLUtil.isNullString(s))
        {
        	s="0";
        }
        map.put(mandField.toUpperCase(),s);
        
        requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(s));
        break;
        
    case MSISDN2:
        XMLTagValueValidation.validateMsisdn2(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        requestVO.setReceiverMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)));
        break;
    case AMOUNT :
       XMLTagValueValidation.validateAmount(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
       
        break;
        
    case LANGUAGE2 :
        XMLTagValueValidation.validateLanguage2(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagOptinal());

        String s1=getNodeValue(data, getNodeValue(mandFields,mandField));
        if(BTSLUtil.isNullString(s1))
        {
        	s1="0";
        }
        map.put(mandField.toUpperCase(),s1);
        requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(s1));
        
        break;
        
    case SELECTOR :
        String selector = getNodeValue(data, getNodeValue(mandFields,mandField));
        if(BTSLUtil.isNullString(selector))
            selector = validateSelector(data, mandFields, requestVO, map.get(TYPE.toUpperCase()).toString());
        XMLTagValueValidation.validateSelector(selector,RestTagValueValidation.isTagManadatory());
        map.put(mandField.toUpperCase(),selector);
        break;
    case CONFIRMPIN:
    	XMLTagValueValidation.validatePin(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        break;
        
    case PIN:
        XMLTagValueValidation.validatePin(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        break;
        
    case NEWPIN:
        XMLTagValueValidation.validatePin(getNodeValue(data, getNodeValue(mandFields,mandField)),RestTagValueValidation.isTagManadatory());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        break;
        
    case REFNUM:
        XMLTagValueValidation.validateRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        requestVO.setExternalReferenceNum(getNodeValue(data, getNodeValue(mandFields,mandField)));
       break;
    case SID:
        XMLTagValueValidation.validateSID(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        requestVO.setSid(getNodeValue(data, getNodeValue(mandFields,mandField)));
       break;
    case NEWSID:
        XMLTagValueValidation.validateSID(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagOptinal());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        requestVO.setSid(getNodeValue(data, getNodeValue(mandFields,mandField)));
       break;
    case EXTTXNNUMBER:
        XMLTagValueValidation.validateExtRefNum(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        break;
    case VOUCHERCODE:
    	if(getNodeValue(data, SERIALNUMBER)!=null)
    	{
    		XMLTagValueValidation.validateVoucherCode(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory(),getNodeValue(data, SERIALNUMBER).toString());
    	}else{
    		XMLTagValueValidation.validateVoucherCode(getNodeValue(data, getNodeValue(mandFields,mandField)), XMLTagValueValidation.isTagManadatory(),"");
            
    	}
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        break;
    case MSISDN1:
        XMLTagValueValidation.validateMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)),map.get(EXTNWCODE.toUpperCase()).toString(),RestTagValueValidation.isTagManadatory());
        map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
        break;
       
       case SUBSCRIBERMSISDN:
           XMLTagValueValidation.validateMsisdn(getNodeValue(data, getNodeValue(mandFields,mandField)),null,RestTagValueValidation.isTagManadatory());
           map.put(mandField.toUpperCase(),getNodeValue(data, getNodeValue(mandFields,mandField)));
           requestVO.setRequestMSISDN(getNodeValue(data, getNodeValue(mandFields,mandField)));
           break;
       default:
           break;
    }
    LogFactory.printLog(methodName, "mandatoryfield"+mandField+"map.get(mandField.toUpperCase())"+map.get(mandField.toUpperCase()), log);
    
}


private static void addCGDetailsJsonArray(String mandfield,JsonNode json,JsonArray jsonArray ,String string1,String string2,String string3,String string4,String string5,String string6,String string7,String string8){
    
    JsonObject record =  new JsonObject();
    
    record.addProperty(SELECTOR, string1);
    record.addProperty(SELECTORNAME, string2);
    record.addProperty(FROM_SLAB, string3);
    record.addProperty(TOSLAB, string4);
    record.addProperty(CGDESC, string5);
    record.addProperty(CGID, string6);
    record.addProperty(VALIDITY, string6);
    record.addProperty(REVERSALALLWOED, string8);
    jsonArray.add(record);
}

private static void generateMyVoucherEnquieryResp(String mandfield,JsonNode json,JsonArray jsonArray ,VomsVoucherVO vomsVoucherVO){
	 JsonNode arr =json.get(mandfield);
	 Boolean isSubscriberVoucherPinRequired = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_VOUCHER_PIN_REQUIRED);
     JsonObject record =  new JsonObject();
     LogFactory.printLog("generateMyVoucherEnquieryResp", "Entered vomsVoucherVO:" + vomsVoucherVO, log);
     record.addProperty(arr.get("productname").textValue(), vomsVoucherVO.getProductName());
     record.addProperty(arr.get("serialno").textValue(), vomsVoucherVO.getSerialNo());
     record.addProperty(arr.get("vouchertype").textValue(), vomsVoucherVO.getVoucherType());
     record.addProperty(arr.get("vouchersegment").textValue(), vomsVoucherVO.getVoucherSegment());
     record.addProperty(arr.get("voucherdenominaton").textValue(), vomsVoucherVO.getMRP());
     if(isSubscriberVoucherPinRequired)
     record.addProperty(arr.get("voucherpin").textValue(), vomsVoucherVO.getPinNo());
     record.addProperty(arr.get("voucherexpirydate").textValue(), BTSLDateUtil.getSystemLocaleDate(vomsVoucherVO.getExpiryDate().toString()));
     record.addProperty(arr.get("userid").textValue(), vomsVoucherVO.getUserID());
     jsonArray.add(record);
}


}
