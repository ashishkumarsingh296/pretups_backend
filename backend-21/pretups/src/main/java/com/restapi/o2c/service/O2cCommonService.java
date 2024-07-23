package com.restapi.o2c.service;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelVoucherItemsVO;
import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class O2cCommonService {
	
	private static Log log = LogFactory.getLog(O2cCommonService.class.getName());
	public static OperatorUtilI _operatorUtil = null;

	
	/**
	 * @param paymentDetails
	 * @param masterErrorListMain
	 * @throws BTSLBaseException
	 */
	public static boolean validatePaymentDetails(PaymentDetailsO2C paymentDetails,ArrayList<MasterErrorList> masterErrorListMain) throws BTSLBaseException
	{
		StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
         	loggerValue.append("Entering : paymentDetails ");
         	loggerValue.append(paymentDetails);
            log.debug("validatePaymentDetails",loggerValue);
        }
    	boolean error = false;
		ArrayList<ListValueVO> instTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
        ArrayList<ListValueVO> paymentGatewayList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_GATEWAY_TYPE, true);
		Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		if(BTSLUtil.isNullString(paymentDetails.getPaymenttype())){
			error= true;
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
	    }
		else
		{
			boolean isPaymentTypeValid = false;
			for(ListValueVO lvo : instTypeList)
			{	if(lvo.getValue().equalsIgnoreCase(paymentDetails.getPaymenttype()))
				{	
					isPaymentTypeValid = true;
					break;
				}
			}
			if(!isPaymentTypeValid)
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		if(!BTSLUtil.isNullString(paymentDetails.getPaymenttype()) && (!(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(paymentDetails.getPaymenttype())) 
				&& !(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(paymentDetails.getPaymenttype()))))
		{
			if(BTSLUtil.isNullString(paymentDetails.getPaymentinstnumber()))
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK, new String[]{"Payment Instrument Number"});
				masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		if(!BTSLUtil.isNullString(paymentDetails.getPaymenttype()) 
				&& PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(paymentDetails.getPaymenttype()))
		{
			if(BTSLUtil.isNullString(paymentDetails.getPaymentgatewayType()))
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
			boolean isPaymentGatewayValid = false;
			for(ListValueVO lvo : paymentGatewayList)
			{	if(lvo.getLabel().equalsIgnoreCase(paymentDetails.getPaymentgatewayType()))
				{	
					isPaymentGatewayValid = true;
					break;
				}
			}
			if(!isPaymentGatewayValid)
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_GATEWAY, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_GATEWAY);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		if(!PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(paymentDetails.getPaymenttype()) && BTSLUtil.isNullString(paymentDetails.getPaymentdate()))
		{
			error= true;
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		else
		{
			if(!PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(paymentDetails.getPaymenttype()) && !BTSLUtil.isValidDatePattern(paymentDetails.getPaymentdate()))
			{
				error= true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DATE_FORMAT_INVALID, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.DATE_FORMAT_INVALID);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		if (log.isDebugEnabled()) {
            log.debug("validatePaymentDetails", "Exiting");
        }
		return error;
	}


	
	
	/**
	 * @param o2CVoucherTransferReqData
	 * @param con
	 * @param p_senderVO
	 * @param p_receiverVO
	 * @param channelTransferItemsList
	 * @param vomsBatchList
	 * @return
	 * @throws BTSLBaseException
	 */
	public static   ChannelTransferVO prepareChannelTransferProfileVO(List<PaymentDetailsO2C> paymentDetails, Connection con, ChannelUserVO p_receiverVO,
			 ArrayList<ChannelTransferItemsVO> channelTransferItemsList,ArrayList<VomsBatchVO> vomsBatchList) throws BTSLBaseException {

		if (log.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
       	loggerValue.append("Entered p_receiverVO: ");
       	loggerValue.append(p_receiverVO);
     	loggerValue.append("paymentDetails: ");
       	loggerValue.append(paymentDetails);
       	loggerValue.append("channelTransferItemsList.size(): ");
       	loggerValue.append(channelTransferItemsList.size());
       	loggerValue.append("vomsBatchList.size(): ");
       	loggerValue.append(vomsBatchList.size());

           log.debug("prepareTransferProfileVO",loggerValue );
		}

		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		String paymentCode = null;
		String paymentinstnum = null;
		Date paymentDate = null;
		Date p_curDate = new Date();

		paymentCode = paymentDetails.get(0).getPaymenttype();
		paymentinstnum = paymentDetails.get(0).getPaymentinstnumber();
		String paymentDateStr = paymentDetails.get(0).getPaymentdate();
		String paymentGateway = paymentDetails.get(0).getPaymentgatewayType();
		String paymentType = paymentDetails.get(0).getPaymentgatewayType();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		try {
			paymentDate = sdf.parse(paymentDateStr);
		} catch (ParseException pe) {
			log.error("prepareTransferProfileVO", "Exception while parsing payment date " + pe);
		}

		channelTransferVO.setPayInstrumentType(paymentCode);
		if(!PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(paymentType)) {
			channelTransferVO.setPayInstrumentDate(BTSLUtil.getSQLDateFromUtilDate(paymentDate));
		}
		channelTransferVO.setPayInstrumentNum(paymentinstnum);
		channelTransferVO.setPaymentInstSource(paymentGateway);
		channelTransferVO.setNetworkCode(p_receiverVO.getNetworkID());
		channelTransferVO.setNetworkCodeFor(p_receiverVO.getNetworkID());
		channelTransferVO.setGraphicalDomainCode(p_receiverVO.getGeographicalCode());
		channelTransferVO.setDomainCode(p_receiverVO.getDomainID());
		channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
		channelTransferVO.setReceiverGradeCode(p_receiverVO.getUserGrade());
		channelTransferVO.setFromUserID(PretupsI.CATEGORY_TYPE_OPT);
		channelTransferVO.setToUserID(p_receiverVO.getUserID());
		channelTransferVO.setTransferDate(p_curDate);
		channelTransferVO.setCommProfileSetId(p_receiverVO.getCommissionProfileSetID());
		channelTransferVO.setCommProfileVersion(p_receiverVO.getCommissionProfileSetVersion());
		channelTransferVO.setDualCommissionType(p_receiverVO.getDualCommissionType());
		channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
		channelTransferVO.setReceiverTxnProfile(p_receiverVO.getTransferProfileID());
		channelTransferVO.setReceiverCategoryCode(p_receiverVO.getCategoryCode());
		channelTransferVO.setReceiverGgraphicalDomainCode(p_receiverVO.getGeographicalCode());
		channelTransferVO.setReceiverDomainCode(p_receiverVO.getDomainID());
		channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
		channelTransferVO.setToUserID(p_receiverVO.getUserID());
		channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);

		long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
		long commissionQty = 0;
		String productCode = null;
		String productType = null;
		for(ChannelTransferItemsVO channelTransferItemsVO :channelTransferItemsList){
			totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
			if (PretupsI.COMM_TYPE_POSITIVE.equals(p_receiverVO.getDualCommissionType())) {
				totMRP += (channelTransferItemsVO.getReceiverCreditQty())
						* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
			} else {
				totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity())
						* channelTransferItemsVO.getUnitValue());
			}
			totPayAmt += channelTransferItemsVO.getPayableAmount();
			totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
			totTax1 += channelTransferItemsVO.getTax1Value();
			totTax2 += channelTransferItemsVO.getTax2Value();
			totTax3 += channelTransferItemsVO.getTax3Value();
			commissionQty += channelTransferItemsVO.getCommQuantity();
			productCode = channelTransferItemsVO.getProductCode();
			productType = channelTransferItemsVO.getProductType();
		}
	    channelTransferVO.setPayInstrumentAmt(totNetPayAmt);
        channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
		channelTransferVO.setRequestedQuantity(totRequestQty);
		channelTransferVO.setTransferMRP(totMRP);
		channelTransferVO.setPayableAmount(totPayAmt);
		channelTransferVO.setNetPayableAmount(totNetPayAmt);
		channelTransferVO.setTotalTax1(totTax1);
		channelTransferVO.setTotalTax2(totTax2);
		channelTransferVO.setTotalTax3(totTax3);
		channelTransferVO.setProductCode(productCode);
		channelTransferVO.setChannelTransferitemsVOList(channelTransferItemsList);
		channelTransferVO.setCreatedOn(p_curDate);
		channelTransferVO.setModifiedOn(p_curDate);
		channelTransferVO.setProductType(productType);
		if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()) 
				 ){ 
			 ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO); 
			 channelTransferVO.setOtfFlag(true);
		}else{
			channelTransferVO.setOtfFlag(false);
		}
		
		StringBuilder pattern = new StringBuilder();
		final ArrayList<ChannelVoucherItemsVO> channelVoucherItemsVOList = new ArrayList<ChannelVoucherItemsVO>();
		int i =0;
		for(VomsBatchVO vomsBatchVO : vomsBatchList){
	        ChannelVoucherItemsVO channelVoucherItemsVO = new ChannelVoucherItemsVO();
			channelVoucherItemsVO.setTransferId(channelTransferVO.getTransferID());
			channelVoucherItemsVO.setTransferDate(p_curDate);
			channelVoucherItemsVO.setTransferMRP((long)Double.parseDouble(vomsBatchVO.getDenomination()));
			channelVoucherItemsVO.setRequiredQuantity((long)Double.parseDouble(vomsBatchVO.getQuantity()));
			channelVoucherItemsVO.setFromSerialNum(vomsBatchVO.getFromSerialNo());
			channelVoucherItemsVO.setToSerialNum(vomsBatchVO.getToSerialNo());
			
			if(vomsBatchVO.getProductID() != null) {
				channelVoucherItemsVO.setProductId(vomsBatchVO.getProductID());
			}else {				
				String productId =  (new ChannelTransferDAO()).retreiveProductId(con, vomsBatchVO.getFromSerialNo());
				channelVoucherItemsVO.setProductId(productId);				
			}
			channelVoucherItemsVO.setProductName(vomsBatchVO.getProductName());
			channelVoucherItemsVO.setNetworkCode(channelTransferVO.getNetworkCode());
			channelVoucherItemsVO.setFromUser(PretupsI.CATEGORY_TYPE_OPT);
			channelVoucherItemsVO.setToUser(p_receiverVO.getUserID());
			channelVoucherItemsVO.setType(PretupsI.CHANNEL_TYPE_O2C);
			channelVoucherItemsVO.setVoucherType(vomsBatchVO.getVoucherType());
			channelVoucherItemsVO.setSegment(vomsBatchVO.getVouchersegment());
			channelVoucherItemsVO.setRequiredQuantity(vomsBatchVO.getQuantityLong());
			channelVoucherItemsVOList.add(channelVoucherItemsVO);
			if(i<vomsBatchList.size()-1){
				pattern.append(vomsBatchVO.getDenomination()).append("=").append(vomsBatchVO.getQuantity()).append(",");
			}
			else {
				pattern.append(vomsBatchVO.getDenomination()).append("=").append(vomsBatchVO.getQuantity());
			}
		}
		channelTransferVO.setInfo1(pattern.toString());
		channelTransferVO.setChannelVoucherItemsVoList(channelVoucherItemsVOList);
		channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
		channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER);
		channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
		if(SystemPreferences.PG_INTEFRATION_ALLOWED && PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(paymentCode)) {
  			channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);				
  		} else {
  			channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
  		}
		if (log.isDebugEnabled()) {
			log.debug("prepareTransferProfileVO", " Exited: channelTransferVO=  " + channelTransferVO);
		}

		return channelTransferVO;
	}

	
	
	/**
	 * @param con
	 * @param channelTransferVO
	 * @return
	 */
	public static Object[] prepareSMSMessageListForVoucher(Connection con, ChannelTransferVO channelTransferVO) {
		final String methodName = "prepareSMSMessageListForVoucher";
		StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered channelTransferVO =  : ");
			loggerValue.append(channelTransferVO);
			log.debug(methodName,  loggerValue);
		}
		final ArrayList<KeyArgumentVO> txnSmsMessageList = new ArrayList<KeyArgumentVO>();
		KeyArgumentVO keyArgumentVO = null;
		String argsArr[] = null;
		final ArrayList<ChannelVoucherItemsVO> channelVoucherItemsVOList = channelTransferVO.getChannelVoucherItemsVoList();
		ChannelVoucherItemsVO channelVoucherItemsVO = null;
		for (int i = 0, k = channelVoucherItemsVOList.size(); i < k; i++) {
			channelVoucherItemsVO = (ChannelVoucherItemsVO)channelVoucherItemsVOList.get(i);
			keyArgumentVO = new KeyArgumentVO();
			argsArr = new String[2];
			argsArr[0] = String.valueOf(channelVoucherItemsVO.getTransferMrp());
			argsArr[1] = String.valueOf(channelVoucherItemsVO.getRequiredQuantity());
			keyArgumentVO.setKey(PretupsErrorCodesI.C2S_OPT_CHNL_TRANSFER_SMS2);
			keyArgumentVO.setArguments(argsArr);
			txnSmsMessageList.add(keyArgumentVO);
		}
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exited txnSmsMessageList.size() = ");
			loggerValue.append(txnSmsMessageList.size());
			log.debug(methodName,  loggerValue );
		}

		return (new Object[] { txnSmsMessageList });
	}

	
	
		
	
		
	/**
	 * @param p_productList
	 * @param p_productListWithXfrRule
	 * @return
	 */
	public static ArrayList filterProductWithTransferRule(ArrayList p_productList, ArrayList p_productListWithXfrRule) {
		final String methodName = "filterProductWithTransferRule";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered p_productList: " + p_productList.size() + " p_productListWithXfrRule: " + p_productListWithXfrRule.size());
		}
		ChannelTransferItemsVO channelTransferItemsVO = null;
		ListValueVO listValueVO = null;
		final ArrayList tempList = new ArrayList();
		for (int m = 0, n = p_productList.size(); m < n; m++) {
			channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(m);
			for (int i = 0, k = p_productListWithXfrRule.size(); i < k; i++) {
				listValueVO = (ListValueVO) p_productListWithXfrRule.get(i);
				if (channelTransferItemsVO.getProductCode().equals(listValueVO.getValue())) {
					tempList.add(channelTransferItemsVO);
					break;
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting tempList: " + tempList.size());
		}

		return tempList;
	}
	
	/**
	 * @param masterErrorListMain
	 * @param locale
	 * @param reqData
	 * @return
	 */
	public static boolean validateRequestData(ArrayList<MasterErrorList> masterErrorListMain,Locale locale,HashMap<String, Object> reqData){
		Boolean error = false;

		Boolean userEventRemarksEnabled = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS);
		if (userEventRemarksEnabled != null && userEventRemarksEnabled.booleanValue()) {
			if (BTSLUtil.isNullString((String) reqData.get("remarks"))) {
				error = true;
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_REMARKS_REQUIRED, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.O2C_REMARKS_REQUIRED);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
		}
		if(!BTSLUtil.isNullString((String)reqData.get("refNumber")) && !BTSLUtil.isNumeric((String)reqData.get("refNumber"))){
			error = true;
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_REFNO_NOT_NUMERIC, null);
			masterErrorList.setErrorCode(PretupsErrorCodesI.O2C_REFNO_NOT_NUMERIC);
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
		}
		
    	try{
			List<PaymentDetailsO2C> paymentDetails = (List<PaymentDetailsO2C>)reqData.get("paymentDetails");
			if(validatePaymentDetails(paymentDetails.get(0), masterErrorListMain))
				error= true;
    	}catch(BTSLBaseException be){
    		error = true;
			MasterErrorList masterErrorList = new MasterErrorList();
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			masterErrorList.setErrorCode(be.getMessageKey());
			masterErrorList.setErrorMsg(msg);
			masterErrorListMain.add(masterErrorList);
    	}
     return error;
	}
	
	
	/**
	 * @param p_con
	 * @param p_channelTransferVO
	 * @param request
	 * @param p_channelTransferDAO
	 * @param p_roleCode
	 * @param p_subject
	 */
	public static void sendEmailNotification(Connection p_con, ChannelTransferVO p_channelTransferVO, ChannelTransferDAO p_channelTransferDAO, String p_roleCode, String p_subject) {
		final String methodName = "sendEmailNotification";
        final Locale locale = BTSLUtil.getSystemLocaleForEmail();
        
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		try {
			
			final String from = RestAPIStringParser.getMessage(locale, "o2c.email.notification.from",null);
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			String subject = "";
			String message1 = "";
			String notifyContent = RestAPIStringParser.getMessage(locale, p_subject,null);
			boolean isHeaderAdded = false;
			// modified by nilesh
			
			//For getting name and msisdn of initiator
            ArrayList arrayList = new ArrayList();
            ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
            arrayList = channelUserWebDAO.loadUserNameAndEmail(p_con, p_channelTransferVO.getCreatedBy());

			String message = notifyContent + 
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.channeluser.details",null) + 
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.transferid",null) + " " + p_channelTransferVO.getTransferID() + 
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.channeluser.name",null) + " " + p_channelTransferVO.getToUserName() + 
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.channeluser.msisdn",null) + " " + p_channelTransferVO.getToUserMsisdn() + 
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.transfer.mrp",null) + " " + p_channelTransferVO.getTransferMRPAsString() + 
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.notification.content.req.amount",null)+" " + PretupsBL.getDisplayAmount(p_channelTransferVO.getRequestedQuantity())+
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.notification.content.net.payable.amount",null)+ " " + PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount())+
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.transfer.type",null) + " " + p_channelTransferVO.getType() + 
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.initiator.name",null)+ " " + arrayList.get(0) + 
					"<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.initiator.msisdn",null) + " " + arrayList.get(1);
	
			
			if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(p_channelTransferVO.getTransferSubType()))
            {
                  String totalCommission = PretupsBL.getDisplayAmount(((ChannelTransferItemsVO)p_channelTransferVO.getChannelTransferitemsVOList().get(0)).getCommQuantity());
                  message = message + "<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.total.commission",null) + " " + totalCommission;
                  if(PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                        message = message + "<br>" + RestAPIStringParser.getMessage(locale, "o2c.email.offline.settlement",null);
                  }
                  if(p_channelTransferVO.getChannelVoucherItemsVoList() != null)
                  {
                        for(int i=0 ;i < p_channelTransferVO.getChannelVoucherItemsVoList().size();i++)
                        {
                              if(((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getToSerialNum() != null && ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getFromSerialNum() != null)
                              {
                                    if(!isHeaderAdded) {
                                          isHeaderAdded = true;
                                          message1 = "<table><tr>"
                                        		  	  + "<td style='width: 5%;'>"+ RestAPIStringParser.getMessage(locale, "o2c.email.notification.serialNumber",null) + "</td>"
                                                      + "   <td style='width: 10%;'>"+ RestAPIStringParser.getMessage(locale, "o2c.email.notification.denomination",null) + "</td>"
                                                      + " <td style='width: 10%;'>"+ RestAPIStringParser.getMessage(locale, "o2c.email.notification.quantity",null) + "</td>"
                                                      + " <td style='width: 25%;'>"+ RestAPIStringParser.getMessage(locale, "o2c.email.notification.fromSerialNo",null) + "</td>"
                                                      + " <td style='width: 25%;'>"+ RestAPIStringParser.getMessage(locale, "o2c.email.notification.toSerialNo",null) + "</td>"
                                                      + " <td style='width: 13%;'>"+ RestAPIStringParser.getMessage(locale, "o2c.email.notification.product",null) + "</td>"
                                                      + " <td style='width: 12%;'>"+ RestAPIStringParser.getMessage(locale, "o2c.email.notification.voucherType",null) + "</td>"
                                                      + "</tr>";
                                    }
                                    message1 = message1 + "<tr><td style='width: 5%;'>" +(i + 1) + "</td>" + 
                                                   "<td style='width: 10%;'>" +((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getTransferMrp() + "</td>" +  
                                                   "<td style='width: 10%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getRequiredQuantity() + "</td>" +  
                                                   "<td style='width: 25%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getFromSerialNum() + "</td>" + 
                                                   "<td style='width: 25%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getToSerialNum() + "</td>" +
                                                   "<td style='width: 13%;'>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getProductName() + "</td>" +
                                                   "<td style='width: 12%;'>" + new VomsProductDAO().getNameFromVoucherType(p_con,((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getVoucherType()) + "</td>" +
                                                   "</tr>";
                              }
                              else
                              {
                                    if(!isHeaderAdded) {
                                          isHeaderAdded = true;
                                          message1 = "<table><tr>"
                                                      + "   <td> S.No. </td>"
                                                      + "   <td>"+ RestAPIStringParser.getMessage(locale, "o2c.email.notification.denomination",null) + "</td>"
                                                      + " <td>"+ RestAPIStringParser.getMessage(locale, "o2c.email.notification.quantity",null) + "</td>"
                                                      + "</tr>";
                                    }
                                    message1 = message1 + "<tr><td>" +(i + 1) + "</td>" + 
                                                   "<td>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getTransferMrp() + "</td>" +  
                                                   "<td>" + ((ChannelVoucherItemsVO)p_channelTransferVO.getChannelVoucherItemsVoList().get(i)).getRequiredQuantity() + "</td>" +
                                                   "</tr>";
                              }
                        }
                        
                        message = message + message1 + "</table>";
                  }
            }
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			// new : added by nilesh
			String to = "";
			if (!BTSLUtil.isNullString(p_roleCode)) {
				to = p_channelTransferDAO.getEmailIdOfApprover(p_con, p_roleCode, p_channelTransferVO.getToUserID());

			} else {
				to = p_channelTransferVO.getEmail();
			}
			
			subject = RestAPIStringParser.getMessage(locale, p_subject,null).toString();

			// Send email
			if (!BTSLUtil.isNullString(to)) {
				EMailSender.sendMail(to, from, bcc, cc, subject, message, isAttachment, pathofFile, fileNameTobeDisplayed);
			}
			if (log.isDebugEnabled()) {
				log.debug("MAIL CONTENT ", message);
			}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.error("sendEmailNotification ", " Email sending failed" + e.getMessage());
			}
			log.errorTrace(methodName, e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting ....");
		}
	}

}
