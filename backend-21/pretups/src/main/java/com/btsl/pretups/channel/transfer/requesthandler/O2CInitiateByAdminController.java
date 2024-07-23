package com.btsl.pretups.channel.transfer.requesthandler;



import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsResponse;
import com.btsl.common.RowErrorMsgList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.XmlTagValueConstant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * @author pankaj.rawat
 * This controller initiates an O2C stock request(multi-product) by a channel admin.
 * Also lists major errors(if any) in the response
 */

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2CInitiateByAdminController.name}", description = "${O2CInitiateByAdminController.desc}")//@Api(tags = "O2C Services", defaultValue = "Initiate O2C Request for Stock By Operator")
@RestController
@RequestMapping(value = "/v1/o2c")

public class O2CInitiateByAdminController {
	private static Log _log = LogFactory.getLog(O2CInitiateByAdminController.class.getName());
	ChannelTransferRuleVO channelTransferRuleVO = null;
	public static OperatorUtilI _operatorUtil = null;
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/stockInitiateByOpt", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "Initiate O2C Request for Stock"
			, response = BaseResponseMultiple.class, authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${stockInitiateByOpt.summary}", description="${stockInitiateByOpt.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponseMultiple.class))
							)
					}

					),


					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

									 , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.BAD_REQUEST)}
									 
							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.UNAUTH)}
									 
									 )
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.NOT_FOUND)}
					
							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)}
					)})
			}
	)




	/**
	 * This method initiates an O2C stock request for multiple products.
	 * @param headers
	 * @param response
	 * @param o2CInitiateOptReqVO
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public BaseResponseMultiple initiateStockO2C(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response,
			@RequestBody O2CInitiateOptReqVO o2CInitiateOptReqVO) 
			throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException
	{
		final String METHOD_NAME = "initiateStockO2C";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered ");
		}
		final Date curDate = new Date();
		Connection con = null;
		MComConnectionI mcomCon = null;
		BaseResponseMultiple apiResponse = null;
		RequestVO p_requestVO = null;
		ArrayList<String> arguments = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			apiResponse = new BaseResponseMultiple<>();
			p_requestVO = new RequestVO();
			ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();

			//Aunthenticate the token
			o2CInitiateOptReqVO.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(o2CInitiateOptReqVO, headers, apiResponse); 
			final String initiatorMsisdn = o2CInitiateOptReqVO.getData().getMsisdn();
			UserVO senderVO = (new UserDAO()).loadUsersDetails(con, initiatorMsisdn);
			String _serviceType="O2CSTKINIMUL";
			ErrorMap errorMap = new ErrorMap();
			errorMap.setMasterErrorList(masterErrorListMain);
			ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal = new ArrayList<>();
			ArrayList<BaseResponse> baseResponseFinalSucess = new ArrayList<>();
			p_requestVO.setServiceType(_serviceType);
			p_requestVO.setRequestGatewayType(o2CInitiateOptReqVO.getReqGatewayType());
			Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			p_requestVO.setLocale(locale);
			
			List<O2CInitiateOptReqData> dataSets = o2CInitiateOptReqVO.getO2CInitiateOptReqData();
			//Beginning parsing the data sets
			try
			{
	            for(int i=0;i<dataSets.size();i++)
				{
					BaseResponse baseResponse = new BaseResponse();
					RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
					O2CInitiateOptReqData dataSet = dataSets.get(i);
					ChannelTransferVO channelTransferVO = new ChannelTransferVO();
					ArrayList prodTypListtemp = null;
					HashMap<String, ArrayList> productsAssociated = new HashMap<String, ArrayList> ();
					boolean isContinue = false;
					List<PaymentDetails> paymentDetails = (BTSLUtil.isNullorEmpty(dataSet.getPaymentdetails())) ? null: dataSet.getPaymentdetails();
					List<Products> products = (BTSLUtil.isNullorEmpty(dataSet.getProducts())) ? null : dataSet.getProducts();
					String pin = (BTSLUtil.isNullorEmpty(dataSet.getPin())) ? null: dataSet.getPin();
					String remarks = (BTSLUtil.isNullorEmpty(dataSet.getRemarks())) ? null : dataSet.getRemarks();
					String language = (BTSLUtil.isNullorEmpty(dataSet.getRemarks())) ? null : dataSet.getRemarks();
					String refnumber = (BTSLUtil.isNullorEmpty(dataSet.getRefnumber())) ? null : dataSet.getRefnumber();
					final String msisdn = (BTSLUtil.isNullorEmpty(dataSet.getMsisdn())) ? null : dataSet.getMsisdn();
					ChannelUserVO receiverUserVO = null;
					if(!BTSLUtil.isNullString(msisdn))
					{	
						receiverUserVO = (ChannelUserVO) ((new ChannelUserDAO()).loadChannelUserDetails(con, msisdn));
						if(receiverUserVO == null || !(receiverUserVO.getNetworkID().equals(senderVO.getNetworkID()))){
							MasterErrorList masterErrorList = new MasterErrorList();
							String args[] = {msisdn};
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, args);
							masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
							masterErrorList.setErrorMsg(msg);
							masterErrorLists.add(masterErrorList);
							isContinue = true;
						}
						else if(validateChannelUserForTransfer(con, receiverUserVO, masterErrorLists))
							isContinue = true;
						if(receiverUserVO != null){
							if (PretupsI.YES.equals(receiverUserVO.getCategoryVO().getProductTypeAllowed())) {
								receiverUserVO.setAssociatedProductTypeList(new ProductTypeDAO().loadUserProductsListForLogin(con, receiverUserVO.getUserID()));
				            } else {
				            	receiverUserVO.setAssociatedProductTypeList(LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true));
				            }
							prodTypListtemp = receiverUserVO.getAssociatedProductTypeList();
							
							for(int itr=0;itr<prodTypListtemp.size();itr++)
							{
								ListValueVO lvVO = (ListValueVO)(prodTypListtemp.get(itr));
								ArrayList prod = ChannelTransferBL.loadO2CXfrProductList(con, lvVO.getValue(), receiverUserVO.getNetworkID(), receiverUserVO.getCommissionProfileSetID(), curDate, "");
								for(int item=0;item<prod.size();item++){
									ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO)(prod.get(item));
									ArrayList value = new ArrayList();
									value.add(channelTransferItemsVO);
									productsAssociated.put(String.valueOf(channelTransferItemsVO.getProductShortCode()), value);
								}
							}
						}
					}
					else{
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.P2P_ERROR_BLANK_MSISDN);
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						isContinue = true;
					}
		
					if (paymentDetails == null || paymentDetails.size() == 0) {
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_PAYMENT_DETAILS, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_PAYMENT_DETAILS);
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						isContinue = true;
					}
					if (paymentDetails.size() > 1) {
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_DETAILS, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_DETAILS);
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						isContinue = true;
					}
					
					if(validatePaymentDetails(paymentDetails, rowErrorMsgLists))
						isContinue = true;
					if (products == null || products.size() == 0) {
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_PRODUCT_DETAILS, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_PRODUCT_DETAILS);
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						isContinue = true;
					}
					boolean userEventRemark = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS);
					if(validateProductDetails(products, productsAssociated, channelTransferVO, rowErrorMsgLists))
						isContinue = true;
					if (remarks == null && userEventRemark ) {
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REMARKS_REQUIRED, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.REMARKS_REQUIRED);
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						isContinue = true;
					}

					UserPhoneVO userPhoneVO = null;
					if (!receiverUserVO.isStaffUser()) {
						userPhoneVO = receiverUserVO.getUserPhoneVO();
					} else {
						userPhoneVO = receiverUserVO.getStaffUserDetails().getUserPhoneVO();
					}
					
					if 	(PretupsI.YES.equals(senderVO.getCategoryVO().getSmsInterfaceAllowed())) {
						try {
							UserPhoneVO senderUserPhoneVO = new UserDAO().loadUserPhoneVO(con, senderVO.getUserID());
							senderVO.setUserPhoneVO(senderUserPhoneVO);
							ChannelUserBL.validatePIN(con,(ChannelUserVO) senderVO, pin);
						} catch (BTSLBaseException be) {
							MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
							masterErrorList.setErrorCode(be.getMessageKey());
							masterErrorList.setErrorMsg(msg);
							masterErrorLists.add(masterErrorList);
							isContinue = true;
						}
					}
					
					if(!isContinue)
					{
						try
						{
							channelTransferVO.setFirstApproverLimit(channelTransferRuleVO.getFirstApprovalLimit());
							channelTransferVO.setSecondApprovalLimit(channelTransferRuleVO.getSecondApprovalLimit());
							channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
							channelTransferVO.setOtfFlag(false);
							channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
							channelTransferVO.setReferenceNum(refnumber);
							channelTransferVO.setPayInstrumentType(paymentDetails.get(0).getPaymenttype());
							channelTransferVO.setPayInstrumentNum(paymentDetails.get(0).getPaymentinstnumber());
							channelTransferVO.setPayInstrumentDate(BTSLUtil.getDateFromDateString(paymentDetails.get(0).getPaymentdate(), PretupsI.DATE_FORMAT));
							channelTransferVO.setChannelRemarks(remarks);
							channelTransferVO.setDualCommissionType(receiverUserVO.getDualCommissionType());
							ChannelTransferBL.loadAndCalculateTaxOnProducts(con, receiverUserVO.getCommissionProfileSetID(), receiverUserVO.getCommissionProfileSetVersion(),
			                    channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_O2C);
							if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(paymentDetails.get(0).getPaymenttype()))
							{
								ChannelTransferBL.calculateTotalMRPFromTaxAndDiscount(channelTransferVO.getChannelTransferitemsVOList(), PretupsI.TRANSFER_TYPE_O2C, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1,
									channelTransferVO);
							}
							prepareChannelTransferVO(p_requestVO, channelTransferVO, new Date(), receiverUserVO, channelTransferVO.getChannelTransferitemsVOList(), senderVO);
						}
						catch(BTSLBaseException be)
						{
							_log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
							_log.errorTrace(METHOD_NAME, be);
							String msg = RestAPIStringParser.getMessage(
									new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
									null);
							MasterErrorList masterErrorList = new MasterErrorList();
							masterErrorList.setErrorCode(be.getMessageKey());
							masterErrorList.setErrorMsg(msg);
							masterErrorLists.add(masterErrorList);
							isContinue = true;
						}
					}
					if(isContinue)
					{
						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
						rowErrorMsgLists.setRowValue(String.valueOf(i+1));
						rowErrorMsgLists.setRowName("Data" + rowErrorMsgLists.getRowValue());
						rowErrorMsgListsFinal.add(rowErrorMsgLists);
						continue;
					}
		            // generate transfer ID for the O2C transfer
		            ChannelTransferBL.genrateTransferID(channelTransferVO);

		            ArrayList prdList = channelTransferVO.getChannelTransferitemsVOList();
		            
		            // set the transfer ID in each ChannelTransferItemsVO of productList
		            for (int l = 0, j = prdList.size(); l < j; l++) {
		            	ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) prdList.get(l);
		                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
		            }

		            channelTransferVO.setControlTransfer(PretupsI.YES);

		            final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();

		            // insert the channelTransferVO in the database
		            int insertCount = 0; 
		            insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
		            if (insertCount < 0) {
		                con.rollback();
		                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
		                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
		            }

		            con.commit();
		            p_requestVO.setMessageCode(PretupsErrorCodesI.O2C_INITIATE_SUCCESS);
		            p_requestVO.setMessageArguments(new String[]{channelTransferVO.getTransferID()});
		            baseResponse.setMessage(RestAPIStringParser.getMessage(
							locale, p_requestVO.getMessageCode(),
					p_requestVO.getMessageArguments()));
					baseResponse.setMessageCode(p_requestVO.getMessageCode());
					baseResponse.setStatus(200);
					baseResponse.setTransactionId(channelTransferVO.getTransferID());
					baseResponseFinalSucess.add(baseResponse);
		            p_requestVO.setSuccessTxn(true);
		            OneLineTXNLog.log(channelTransferVO, null);
					
					ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);

		            // sending msg to receiver
		            final ArrayList itemsList = channelTransferVO.getChannelTransferitemsVOList();
		            // String smsKey=PretupsErrorCodesI.O2C_INITIATE_TRANSFER_RECEIVER;
		            String smsKey = null;
		            final ArrayList txnList = new ArrayList();
		            final ArrayList balList = new ArrayList();
		            String args[] = null;
		            ChannelTransferItemsVO channelTrfItemsVO = null;
		            KeyArgumentVO keyArgumentVO = null;

		            final int lSize = itemsList.size();
		            for (int inc = 0; inc < lSize; inc++) {
		                channelTrfItemsVO = (ChannelTransferItemsVO) itemsList.get(inc);
		                keyArgumentVO = new KeyArgumentVO();
		                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_INITIATE_TRANSFER_SUCCESS_TXNSUBKEY);
		                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), channelTrfItemsVO.getRequestedQuantity() };
		                keyArgumentVO.setArguments(args);
		                txnList.add(keyArgumentVO);

		                keyArgumentVO = new KeyArgumentVO();
		                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_INITIATE_TRANSFER_SUCCESS_BALSUBKEY);
		                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTrfItemsVO.getBalance() + channelTrfItemsVO
		                    .getRequiredQuantity()) };
		                keyArgumentVO.setArguments(args);
		                balList.add(keyArgumentVO);
		            }// end of for
		            if ((PretupsI.TRANSFER_CATEGORY_SALE).equalsIgnoreCase(channelTransferVO.getTransferCategory())) {
		                final String[] msgArray = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
		                    .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()) };
		                p_requestVO.setMessageArguments(msgArray);
		                smsKey = PretupsErrorCodesI.O2C_INITIATE_TRANSFER_RECEIVER;
		            } else if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_TRANSFER))) {
		                final String[] msgArray = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
		                    .getTransferID() };
		                p_requestVO.setMessageArguments(msgArray);
		                smsKey = PretupsErrorCodesI.FOC_INITIATE_TRANSFER_EXTGW_RECEIVER;
		            }
		            p_requestVO.setMessageCode(smsKey);
		            p_requestVO.setTransactionID(channelTransferVO.getTransferID());
		            if (SystemPreferences.SECONDARY_NUMBER_ALLOWED && SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
		                if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(p_requestVO.getServiceType()) && p_requestVO.isSenderMessageRequired()) {
		                    if (userPhoneVO != null) {
		                        final Locale locale1 = new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry());
		                        final String senderMessage = BTSLUtil.getMessage(locale1, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
		                        final PushMessage pushMessage = new PushMessage(userPhoneVO.getMsisdn(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO
		                            .getRequestGatewayCode(), locale1);
		                        pushMessage.push();
		                    }
		                }
		            }
				}
				if(!BTSLUtil.isNullOrEmptyList(rowErrorMsgListsFinal))
				{
					if(BTSLUtil.isNullOrEmptyList(baseResponseFinalSucess))
					{	
						apiResponse.setStatus("400");
						response.setStatus(HttpStatus.SC_BAD_REQUEST);
					}
					else
						apiResponse.setStatus("200");
					apiResponse.setService(_serviceType + "RESP");
					errorMap.setRowErrorMsgLists(rowErrorMsgListsFinal);
					apiResponse.setSuccessList(baseResponseFinalSucess);
					apiResponse.setErrorMap(errorMap);
					return apiResponse;
				}
				apiResponse.setSuccessList(baseResponseFinalSucess);
				apiResponse.setMessageCode(p_requestVO.getMessageCode());
				apiResponse.setMessage(PretupsI.RECORD_SUCESS);
				apiResponse.setStatus("200");
				apiResponse.setService(_serviceType + "RESP");
				return apiResponse;
			} catch (Exception e) {
					_log.error(METHOD_NAME, "Exception " + e);
					_log.errorTrace(METHOD_NAME, e);
					p_requestVO.setSuccessTxn(false);
					try {
						if (mcomCon != null) {
							mcomCon.finalRollback();
						}
					} 
					
					catch (SQLException esql) {
						_log.error(METHOD_NAME,"SQLException : ", esql.getMessage());
					}
					_log.error(METHOD_NAME, "BTSLBaseException " + e.getMessage());
					_log.errorTrace(METHOD_NAME, e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
							"O2CInitiateByAdminController " + METHOD_NAME, "", "", "", "Exception:" + e.getMessage());
					apiResponse.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
					apiResponse.setMessage(RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
							null));
					apiResponse.setService(_serviceType + "RESP");
					apiResponse.setStatus("400");
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					return apiResponse;
				} finally {
					if (mcomCon != null) {
						mcomCon.close("O2CInitiateByAdminController#" + METHOD_NAME);
						mcomCon = null;
					}
					if (_log.isDebugEnabled()) {
						_log.debug(METHOD_NAME, " Exited ");
					}
				}
		} catch (BTSLBaseException be) {
			
			_log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
	        _log.errorTrace(METHOD_NAME, be);
			apiResponse.setStatus("400");
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
						null);
	        	apiResponse.setMessageCode(be.getMessageKey());
	        	apiResponse.setMessage(resmsg);
	        	apiResponse.setService("O2CINIMULRESP");
	            return apiResponse;
	        
	    } catch (Exception e) {
	    	
	    	PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
	    	_log.error(METHOD_NAME, "Exception " + e);
			_log.errorTrace(METHOD_NAME, e);
			apiResponse.setStatus("400");
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			apiResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	        String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
					null);
	        apiResponse.setMessage(resmsg);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2CInitiateByAdminController#" + METHOD_NAME);
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " Exited ");
			}
		}
		return apiResponse;
	}
	
	
	/**
	 * This method prepares channel transfer VO for o2c transaction
	 * @param p_requestVO
	 * @param p_channelTransferVO
	 * @param p_curDate
	 * @param p_channelUserVO
	 * @param p_prdList
	 * @param p_userVO
	 * @throws BTSLBaseException
	 */
	private void prepareChannelTransferVO(RequestVO p_requestVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList, UserVO p_userVO) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
         	loggerValue.append("Entering  : requestVO ");
         	loggerValue.append(p_requestVO);
         	loggerValue.append("p_channelTransferVO:" );
         	loggerValue.append(p_channelTransferVO);
         	loggerValue.append("p_curDate:");
         	loggerValue.append(p_curDate);
         	loggerValue.append( "p_channelUserVO:");
         	loggerValue.append(p_channelUserVO);
         	loggerValue.append("p_prdList:" );
         	loggerValue.append(p_prdList);
         	loggerValue.append("p_userVO:" );
         	loggerValue.append(p_userVO);
            _log.debug("prepareChannelTransferVO",loggerValue );
        }

        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);

        // who initaite the order.
        p_channelTransferVO.setReceiverGradeCode(p_channelUserVO.getUserGrade());
        p_channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setToUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(p_userVO.getUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(p_userVO.getUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_SMS);

        // adding the some additional information of sender/reciever
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setToUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        p_channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        p_channelTransferVO.setActiveUserId(p_userVO.getUserID());
        ChannelTransferItemsVO channelTransferItemsVO = null;
        String productType = null;
        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        for (int i = 0, k = p_prdList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_prdList.get(i);
            totRequestQty += channelTransferItemsVO.getRequiredQuantity();
            if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelUserVO.getDualCommissionType())) {
                totMRP += (channelTransferItemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
            } else {
                totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());
            }
            totPayAmt += channelTransferItemsVO.getPayableAmount();
            totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
            totTax1 += channelTransferItemsVO.getTax1Value();
            totTax2 += channelTransferItemsVO.getTax2Value();
            totTax3 += channelTransferItemsVO.getTax3Value();

            productType = channelTransferItemsVO.getProductType();
            commissionQty += channelTransferItemsVO.getCommQuantity();
            senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
            receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
        }

        p_channelTransferVO.setRequestedQuantity(totRequestQty);
        p_channelTransferVO.setTransferMRP(totMRP);
        p_channelTransferVO.setPayableAmount(totPayAmt);
        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);
        p_channelTransferVO.setTotalTax1(totTax1);
        p_channelTransferVO.setTotalTax2(totTax2);
        p_channelTransferVO.setTotalTax3(totTax3);
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);
        p_channelTransferVO.setPayInstrumentAmt(totNetPayAmt);
        p_channelTransferVO.setProductType(productType);
        p_channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        p_channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        p_channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));

        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Exiting : ");
        }
    }
	
	
	/**
	 * This method validates the payment details
	 * @param paymentDetails
	 * @param rowErrorMsgLists
	 * @return
	 */
	private boolean validatePaymentDetails(List<PaymentDetails> paymentDetails, RowErrorMsgLists rowErrorMsgLists)
	{
		StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
         	loggerValue.append("Entering : paymentDetails ");
         	loggerValue.append(paymentDetails);
            _log.debug("validatePaymentDetails",loggerValue);
        }
		ArrayList<ListValueVO> instTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
        ArrayList<ListValueVO> paymentGatewayList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_GATEWAY_TYPE, true);
		RowErrorMsgList rowErrorMsgListPayment = new RowErrorMsgList();
		ArrayList<RowErrorMsgLists> rowErrorMsgLists1Payment= new ArrayList<RowErrorMsgLists>();
		Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		boolean error = false;
		for(int i=0;i<paymentDetails.size();i++)	
		{
			int row=i+1;
			RowErrorMsgLists rowErrorMsgListsPayment = new RowErrorMsgLists();
			rowErrorMsgListsPayment.setRowValue(String.valueOf(row));
			rowErrorMsgListsPayment.setRowName("Pay"+row);
			ArrayList<MasterErrorList> masterErrorLists1Payment = new ArrayList<MasterErrorList>();
			if(BTSLUtil.isNullString(paymentDetails.get(i).getPaymenttype())){
				error=true;
				MasterErrorList masterErrorListssPayment = new MasterErrorList();
				masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
				masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, new String[] {XmlTagValueConstant.TAG_PAYMENTTYPE}));
				masterErrorLists1Payment.add(masterErrorListssPayment);
		    }
			else
			{
				boolean isPaymentTypeValid = false;
				for(ListValueVO lvo : instTypeList)
				{	if(lvo.getValue().equalsIgnoreCase(paymentDetails.get(i).getPaymenttype()))
					{	
						isPaymentTypeValid = true;
						break;
					}
				}
				if(!isPaymentTypeValid)
				{
					MasterErrorList masterErrorListssPayment = new MasterErrorList();
					masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE);
					masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, new String[] {XmlTagValueConstant.TAG_PAYMENTTYPE}));
					masterErrorLists1Payment.add(masterErrorListssPayment);
				}
			}
			if(!BTSLUtil.isNullString(paymentDetails.get(0).getPaymenttype()) && (!(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(paymentDetails.get(0).getPaymenttype())) 
					&& !(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equalsIgnoreCase(paymentDetails.get(0).getPaymenttype()))))
			{
				if(BTSLUtil.isNullString(paymentDetails.get(0).getPaymentinstnumber()))
				{
					error=true;
					MasterErrorList masterErrorListssPayment = new MasterErrorList();
					masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
					masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK, new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER}));
					masterErrorLists1Payment.add(masterErrorListssPayment);
				}
			}
			
			if(BTSLUtil.isNullString(paymentDetails.get(0).getPaymentdate()))
			{
				error=true;
				MasterErrorList masterErrorListssPayment = new MasterErrorList();
				masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
				masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID, null));
				masterErrorLists1Payment.add(masterErrorListssPayment);
			}
			else
			{
				if(!BTSLUtil.isValidDatePattern(paymentDetails.get(0).getPaymentdate()))
				{
					error=true;
					MasterErrorList masterErrorListssPayment = new MasterErrorList();
					masterErrorListssPayment.setErrorCode(PretupsErrorCodesI.DATE_FORMAT_INVALID);
					masterErrorListssPayment.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DATE_FORMAT_INVALID, null));
					masterErrorLists1Payment.add(masterErrorListssPayment);
				}
			}
			if(masterErrorLists1Payment.size() > 0)
			{
				rowErrorMsgListsPayment.setMasterErrorList(masterErrorLists1Payment);
				rowErrorMsgLists1Payment.add(rowErrorMsgListsPayment);
			}
		}
		if(rowErrorMsgLists.getRowErrorMsgList() == null)
			rowErrorMsgLists.setRowErrorMsgList(new ArrayList<RowErrorMsgList> ());

		if(rowErrorMsgLists1Payment.size() > 0)
		{
			rowErrorMsgListPayment.setRowErrorMsgLists(rowErrorMsgLists1Payment);
			rowErrorMsgLists.getRowErrorMsgList().add(rowErrorMsgListPayment);
		}
		
		if (_log.isDebugEnabled()) {
            _log.debug("validatePaymentDetails", "Exiting");
        }
		return error;
	}
	
	
	/**
	 * This method validates the product details
	 * @param products
	 * @param productsAssociated
	 * @param channelTransferVO
	 * @param rowErrorMsgLists
	 * @return
	 */
	private boolean validateProductDetails(List<Products> products, HashMap<String, ArrayList> productsAssociated, ChannelTransferVO channelTransferVO, RowErrorMsgLists rowErrorMsgLists)
	{
		StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
         	loggerValue.append("Entering : products ");
         	loggerValue.append(products);
         	loggerValue.append(", channelTransferVO ");
         	loggerValue.append(channelTransferVO);
            _log.debug("validateProductDetails",loggerValue);
        }
		
		boolean error = false;
		Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		ArrayList<RowErrorMsgLists> rowErrorMsgLists2Products = new ArrayList<RowErrorMsgLists>();
		RowErrorMsgList rowErrorMsgList2Products = new RowErrorMsgList();
		ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<ChannelTransferItemsVO>();
		for(int k=0;k<products.size();k++)
		{
			int row = k+1;
			RowErrorMsgLists rowErrorMsgListssProducts = new RowErrorMsgLists();
			rowErrorMsgListssProducts.setRowValue(String.valueOf(row));
			rowErrorMsgListssProducts.setRowName("Products "+row);
			
			ArrayList<MasterErrorList> masterErrorLists1 = new ArrayList<MasterErrorList>();
			if(BTSLUtil.isNullString(products.get(k).getQty()))
			{
				error=true;
				MasterErrorList masterErrorListss = new MasterErrorList();
				masterErrorListss.setErrorCode(PretupsErrorCodesI.QUANTITY_NULL);
				masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.QUANTITY_NULL, new String[] {String.valueOf(row)}));
				masterErrorLists1.add(masterErrorListss);
			}
			else
			{
				if(!BTSLUtil.isAlphaNumeric(products.get(k).getQty()))
				{
					error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.QUANTITY_NOT_NUMERIC);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.QUANTITY_NOT_NUMERIC, new String[] {String.valueOf(row)}));
					masterErrorLists1.add(masterErrorListss);
				}
				else if(Long.valueOf(products.get(k).getQty())<=0)
				{
					error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.QUANTITY_NEGATIVE);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.QUANTITY_NEGATIVE, new String[] {String.valueOf(row)}));
					masterErrorLists1.add(masterErrorListss);
				}
			}
			if(!error)
			{
				if(!productsAssociated.containsKey(products.get(k).getProductcode()))
				{
					error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.INVALID_PRODUCT_CODE);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_PRODUCT_CODE, new String[] {String.valueOf(row)}));
					masterErrorLists1.add(masterErrorListss);
				}
				else
				{
					ChannelTransferItemsVO channelTransferItemsVO1 = (ChannelTransferItemsVO)(productsAssociated.get(products.get(k).getProductcode()).get(0));
					int totalReqAmount = 0;
					if(!(BTSLUtil.isNullString(channelTransferItemsVO1.getRequestedQuantity())))
						totalReqAmount += Integer.parseInt(channelTransferItemsVO1.getRequestedQuantity());
					totalReqAmount += Integer.parseInt(products.get(k).getQty());
					if(totalReqAmount > 0)
					{
						channelTransferItemsVO1.setRequestedQuantity(Integer.toString(totalReqAmount));
						itemsList.add(channelTransferItemsVO1);
					}
				}
			}
			if(masterErrorLists1.size() > 0)
			{
				rowErrorMsgListssProducts.setMasterErrorList(masterErrorLists1);
				rowErrorMsgLists2Products.add(rowErrorMsgListssProducts);
			}
		}
		if(rowErrorMsgLists2Products.size() > 0)
		{
			rowErrorMsgList2Products.setRowErrorMsgLists(rowErrorMsgLists2Products);
			rowErrorMsgLists.getRowErrorMsgList().add(rowErrorMsgList2Products);
		}
		channelTransferVO.setChannelTransferitemsVOList(itemsList);
		
		if (_log.isDebugEnabled()) {
            _log.debug("validateProductDetails", "Exiting");
        }
		return error;
	}
	
	/**
	 * This method validates the channel user for transfer
	 * @param con
	 * @param receiverUserVO
	 * @param masterErrorLists
	 * @return
	 */
	private boolean validateChannelUserForTransfer(Connection con, ChannelUserVO receiverUserVO, ArrayList<MasterErrorList> masterErrorLists) throws BTSLBaseException
	{
		boolean isError = false;
		Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		String METHOD_NAME = "validateChannelUserForTransfer";
		try{
			channelTransferRuleVO = new ChannelTransferRuleDAO().loadTransferRule(con, receiverUserVO.getNetworkID(), receiverUserVO.getDomainID(),
					PretupsI.CATEGORY_TYPE_OPT, receiverUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);
		}
		catch(BTSLBaseException be)
		{
			_log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
			_log.errorTrace(METHOD_NAME, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorCode(be.getMessageKey());
			masterErrorList.setErrorMsg(msg);
			masterErrorLists.add(masterErrorList);
			isError = true;
		}
			if (channelTransferRuleVO == null) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST);
				masterErrorList.setErrorMsg(msg);
				masterErrorLists.add(masterErrorList);
				isError = true;
			} else if (PretupsI.NO.equals(channelTransferRuleVO.getTransferAllowed())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_RETURN_NOT_ALLOWED, new String[] {receiverUserVO.getUserName()});
				masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_USER_RETURN_NOT_ALLOWED);
				masterErrorList.setErrorMsg(msg);
				masterErrorLists.add(masterErrorList);
				isError = true;
			} else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH, new String[] {receiverUserVO.getUserName()});
				masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH);
				masterErrorList.setErrorMsg(msg);
				masterErrorLists.add(masterErrorList);
				isError = true;
			}
			
			UserStatusVO receiverStatusVO = null;
            boolean receiverAllowed = false;
            if (receiverUserVO != null) {
                receiverAllowed = false;
                receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(receiverUserVO.getNetworkID(), receiverUserVO.getCategoryCode(), receiverUserVO.getUserType(), "REST");
                if (receiverStatusVO != null) {
                    final String receiverStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
                    final String status[] = receiverStatusAllowed.split(",");
                    for (int i = 0; i < status.length; i++) {
                        if (status[i].equals(receiverUserVO.getStatus())) {
                            receiverAllowed = true;
                        }
                    }
                }
            }

            if (receiverStatusVO == null) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
				masterErrorList.setErrorMsg(msg);
				masterErrorLists.add(masterErrorList);
				isError = true;
            } else if (!receiverAllowed) {
            	MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
				masterErrorList.setErrorMsg(msg);
				masterErrorLists.add(masterErrorList);
				isError = true;
            }
			if (receiverUserVO.getCommissionProfileApplicableFrom().after(new Date())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
				masterErrorList.setErrorMsg(msg);
				masterErrorLists.add(masterErrorList);
				isError = true;
			}
			if (_log.isDebugEnabled()) {
	            _log.debug(METHOD_NAME, "Exiting");
	        }
			return isError;
	}
}
