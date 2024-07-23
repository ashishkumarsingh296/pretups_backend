package com.restapi.o2c.service;



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
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.channel.userreturn.web.ChannelReturnAction;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.user.businesslogic.UserWebDAO;

/**
 * 
 * @author pankaj.rawat
 * This controller performs an O2C return (multi-product) by a channel user.
 * Also lists major errors(if any) in the response
 */

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2CReturnRestController.name}", description = "${O2CReturnRestController.desc}")//@Api(tags = "O2C Services", defaultValue = "O2C Return Request")
@RestController
@RequestMapping(value = "/v1/o2c")

public class O2CReturnRestController {
	private static Log _log = LogFactory.getLog(O2CReturnRestController.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/stockReturn", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "O2C Return Request for Stock"
			, response = BaseResponseMultiple.class, authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${stockReturn.summary}", description="${stockReturn.description}",

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
	 * This method is for an O2C stock return request for multiple products.
	 * @param headers
	 * @param response
	 * @param o2CReturnRequestVO
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public BaseResponseMultiple returnO2CRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response,
			@RequestBody O2CReturnRequestVO o2CReturnRequestVO) 
			throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException
	{
		final String METHOD_NAME = "returnO2CRequest";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered ");
		}
		final Date curDate = new Date();
		Connection con = null;
		MComConnectionI mcomCon = null;
		BaseResponseMultiple apiResponse = null;
		RequestVO p_requestVO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			apiResponse = new BaseResponseMultiple<>();
			p_requestVO = new RequestVO();
			ArrayList<MasterErrorList> masterErrorListMain = new ArrayList<MasterErrorList>();
			Locale locale= new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
			//Aunthenticate the token
			o2CReturnRequestVO.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(o2CReturnRequestVO, headers, apiResponse); 
			
			final String loginID = o2CReturnRequestVO.getData().getLoginid();
			final String msisdn = o2CReturnRequestVO.getData().getMsisdn();
			final ChannelUserVO loggedUserVO = (ChannelUserVO) ((new ChannelUserDAO()).loadChannelUserDetails(con, msisdn));
			String args[] = null;
			String arguments = loggedUserVO.getUserName();
			final ChannelTransferRuleVO channelTransferRuleVO = new ChannelTransferRuleDAO().loadTransferRule(con, loggedUserVO.getNetworkID(), loggedUserVO.getDomainID(),
					PretupsI.CATEGORY_TYPE_OPT, loggedUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

			if (channelTransferRuleVO == null) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.USER_TRANSFER_RULE_NOT_EXIST);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			} else if (PretupsI.NO.equals(channelTransferRuleVO.getReturnAllowed())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_RETURN_NOT_ALLOWED, new String[] {loggedUserVO.getUserName()});
				masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_USER_RETURN_NOT_ALLOWED);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			} else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH, new String[] {loggedUserVO.getUserName()});
				masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RULE_NOTMATCH);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
			
			// to check bar status of user
            final BarredUserDAO barredUserDAO = new BarredUserDAO();
			
			if (!BTSLUtil.isValidMSISDN(msisdn)) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
				throw new BTSLBaseException("initiateStockO2CRequest", "process",
						PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
			}
			final String msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn);
			final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), msisdn,
					PretupsI.USER_TYPE_SENDER, null)) {
				throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.ERROR_USER_TRANSFER_CHNL_SENDER_BAR , new String[] {msisdn});
			}
			
			UserStatusVO senderStatusVO = null;
            boolean senderAllowed = false;
            if (loggedUserVO != null) {
                senderAllowed = false;
                senderStatusVO = (UserStatusVO) UserStatusCache.getObject(loggedUserVO.getNetworkID(), loggedUserVO.getCategoryCode(), loggedUserVO.getUserType(), "REST");
                if (senderStatusVO != null) {
                    final String senderStatusAllowed = senderStatusVO.getUserSenderAllowed();
                    final String status[] = senderStatusAllowed.split(",");
                    for (int i = 0; i < status.length; i++) {
                        if (status[i].equals(loggedUserVO.getStatus())) {
                            senderAllowed = true;
                        }
                    }
                }
            }

            if (senderStatusVO == null) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
            } else if (!senderAllowed) {
            	MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
            }
            else if (loggedUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE, null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
			else if (!PretupsI.YES.equals(loggedUserVO.getCommissionProfileStatus())) {
                args = new String[] { arguments, loggedUserVO.getCommissionProfileLang2Msg() };
                final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    args = new String[] { arguments, loggedUserVO.getCommissionProfileLang1Msg() };
                }
                MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, "commissionprofile.notactive.msg", args);
				masterErrorList.setErrorCode("commissionprofile.notactive.msg");
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
            } else if (!PretupsI.YES.equals(loggedUserVO.getTransferProfileStatus())) {
                args = new String[] { arguments };
                MasterErrorList masterErrorList = new MasterErrorList();
				String msg = RestAPIStringParser.getMessage(locale, "transferprofile.notactive.msg", args);
				masterErrorList.setErrorCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
				masterErrorList.setErrorMsg(msg);
				masterErrorListMain.add(masterErrorList);
			}
			
			String _serviceType="O2CRETMUL";
			ErrorMap errorMap = new ErrorMap();
			ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal = new ArrayList<>();
			if(!BTSLUtil.isNullOrEmptyList(masterErrorListMain))
			{
				RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
				rowErrorMsgLists.setMasterErrorList(masterErrorListMain);
				rowErrorMsgLists.setRowValue(String.valueOf(1));
				rowErrorMsgLists.setRowName("Data" + rowErrorMsgLists.getRowValue());
				rowErrorMsgLists.setRowErrorMsgList(new ArrayList<>());
				rowErrorMsgListsFinal.add(rowErrorMsgLists);
				errorMap.setRowErrorMsgLists(rowErrorMsgListsFinal);
				apiResponse.setStatus("400");
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				apiResponse.setService(_serviceType + "RESP");
				apiResponse.setErrorMap(errorMap);
				return apiResponse;
			}
			ArrayList<BaseResponse> baseResponseFinalSucess = new ArrayList<>();
			p_requestVO.setServiceType(_serviceType);
			p_requestVO.setRequestGatewayType(o2CReturnRequestVO.getReqGatewayType());
			p_requestVO.setLocale(locale);
			
			List<O2CReturnReqData> dataSets = o2CReturnRequestVO.getO2CReturnReqData();
			//Beginning parsing the data sets
			try
			{
				if (PretupsI.YES.equals(loggedUserVO.getCategoryVO().getProductTypeAllowed())) {
					loggedUserVO.setAssociatedProductTypeList(new ProductTypeDAO().loadUserProductsListForLogin(con, loggedUserVO.getUserID()));
	            } else {
	            	loggedUserVO.setAssociatedProductTypeList(LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true));
	            }
				final ArrayList prodTypListtemp = loggedUserVO.getAssociatedProductTypeList();
				HashMap<String, ArrayList> productsAssociated = new HashMap<String, ArrayList>();
				for(int i=0;i<prodTypListtemp.size();i++)
				{
					ListValueVO lvVO = (ListValueVO)(prodTypListtemp.get(i));
					final ArrayList prod = ChannelTransferBL.loadC2CXfrProductsWithXfrRule(con, loggedUserVO.getUserID(), loggedUserVO.getNetworkID(), loggedUserVO
			                .getCommissionProfileSetID(), curDate, channelTransferRuleVO.getTransferRuleID(), null, false, arguments, locale, lvVO.getValue(), PretupsI.TRANSFER_TYPE_O2C);
					//ArrayList prod = ChannelTransferBL.loadO2CXfrProductList(con, lvVO.getValue(), loggedUserVO.getNetworkID(), loggedUserVO.getCommissionProfileSetID(), curDate, "");
					for(int item=0;item<prod.size();item++){
						ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO)(prod.get(item));
						ArrayList value = new ArrayList();
						value.add(channelTransferItemsVO);
						productsAssociated.put(String.valueOf(channelTransferItemsVO.getProductShortCode()), value);
					}
				}
				
	            for(int i=0;i<dataSets.size();i++)
				{
					BaseResponse baseResponse = new BaseResponse();
					RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
					ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
					O2CReturnReqData dataSet = dataSets.get(i);
					ChannelTransferVO channelTransferVO = new ChannelTransferVO();
					boolean isContinue = false;
					List<Products> products = (BTSLUtil.isNullorEmpty(dataSet.getProducts())) ? null : dataSet.getProducts();
					String pin = (BTSLUtil.isNullorEmpty(dataSet.getPin())) ? null: dataSet.getPin();
					String remarks = (BTSLUtil.isNullorEmpty(dataSet.getRemarks())) ? null : dataSet.getRemarks();
					String language = (BTSLUtil.isNullorEmpty(dataSet.getRemarks())) ? null : dataSet.getRemarks();
					if (products == null || products.size() == 0) {
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_PRODUCT_DETAILS, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_PRODUCT_DETAILS);
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						isContinue = true;
					}
					
					if(validateProductDetails(products, productsAssociated, channelTransferVO, rowErrorMsgLists))
						isContinue = true;
					if (remarks == null) {
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REMARKS_REQUIRED, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.REMARKS_REQUIRED);
						masterErrorList.setErrorMsg(msg);
						masterErrorLists.add(masterErrorList);
						isContinue = true;
					}

					UserPhoneVO userPhoneVO = null;
					if (!loggedUserVO.isStaffUser()) {
						userPhoneVO = loggedUserVO.getUserPhoneVO();
					} else {
						userPhoneVO = loggedUserVO.getStaffUserDetails().getUserPhoneVO();
					}
					if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
						try {
							ChannelUserBL.validatePIN(con, loggedUserVO, pin);
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
							channelTransferVO.setOtfFlag(true);
							channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
							channelTransferVO.setChannelRemarks(remarks);
							channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
							
							ChannelTransferBL.loadAndCalculateTaxOnProducts(con, loggedUserVO.getCommissionProfileSetID(), loggedUserVO.getCommissionProfileSetVersion(),
			                    channelTransferVO, true, null, PretupsI.TRANSFER_TYPE_O2C);
							prepareChannelTransferVO(p_requestVO, channelTransferVO, new Date(), loggedUserVO, channelTransferVO.getChannelTransferitemsVOList());
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
		            ChannelTransferBL.genrateReturnID(channelTransferVO);
		            new ChannelReturnAction().orderReturnedProcessStart(con, channelTransferVO, loggedUserVO.getUserID(), curDate, "myRestO2CReturn");
		            ArrayList prdList = channelTransferVO.getChannelTransferitemsVOList();
		            
		            // set the transfer ID in each ChannelTransferItemsVO of productList
		            for (int l = 0, j = prdList.size(); l < j; l++) {
		            	ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) prdList.get(l);
		                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
		            }

		            channelTransferVO.setControlTransfer(PretupsI.YES);

		            final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();

		            channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_WEB);
		            channelTransferVO.setRequestGatewayCode(PretupsI.GATEWAY_TYPE_WEB);
		            channelTransferVO.setRequestGatewayType(PretupsI.GATEWAY_TYPE_WEB);
		            // insert the channelTransferVO in the database
		            int insertCount = 0; 
		            insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
		            if (insertCount < 0) {
		                con.rollback();
		                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
		                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
		            }
		            else
		            {
		            	if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue())
		    	           {  
		            		 UserEventRemarksVO remarkVO = null;
		                     ArrayList<UserEventRemarksVO> withDrawReturnRemarkList = null;
		                     UserDAO userDAO = null;
		 	                withDrawReturnRemarkList = new ArrayList();
		 	                remarkVO = new UserEventRemarksVO();
		 	                remarkVO.setCreatedBy(loggedUserVO.getCreatedBy());
		 	                remarkVO.setCreatedOn(new Date());
		 	                remarkVO.setEventType(PretupsI.CHUSER_WITHDRAW);
		 	                remarkVO.setMsisdn(loggedUserVO.getUserCode());
		 	                remarkVO.setRemarks(channelTransferVO.getChannelRemarks());
		 	                remarkVO.setUserID(loggedUserVO.getUserID());
		 	                remarkVO.setUserType(loggedUserVO.getUserType());
		 	                remarkVO.setModule(PretupsI.C2S_MODULE);
		 	                withDrawReturnRemarkList.add(remarkVO);
		 	                userDAO = new UserDAO();
		 	                UserWebDAO userWebDAO = new UserWebDAO();
		 	                insertCount = userWebDAO.insertEventRemark(con, withDrawReturnRemarkList);
		 	                if (insertCount <= 0) {
		 	                    con.rollback();
		 	                    _log.error("saveDeleteSuspend", "Error: while inserting into userEventRemarks Table");
		 	                    throw new BTSLBaseException(this, "save", "error.general.processing");
		 	                }
		    	           }
		            }
		            con.commit();
		            p_requestVO.setMessageCode(PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS);
                	Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO, PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS_TXNSUBKEY, PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS_BALSUBKEY);
		            p_requestVO.setMessageArguments(new String[]{BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]) , BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]),channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()) });
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
		            ChannelTransferItemsVO channelTrfItemsVO = null;
		            KeyArgumentVO keyArgumentVO = null;

		            final int lSize = itemsList.size();
		            for (int inc = 0; inc < lSize; inc++) {
		                channelTrfItemsVO = (ChannelTransferItemsVO) itemsList.get(inc);
		                keyArgumentVO = new KeyArgumentVO();
		                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS_TXNSUBKEY);
		                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), channelTrfItemsVO.getRequestedQuantity() };
		                keyArgumentVO.setArguments(args);
		                txnList.add(keyArgumentVO);

		                keyArgumentVO = new KeyArgumentVO();
		                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS_BALSUBKEY);
		                args = new String[] { String.valueOf(channelTrfItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTrfItemsVO.getBalance() + channelTrfItemsVO
		                    .getRequiredQuantity()) };
		                keyArgumentVO.setArguments(args);
		                balList.add(keyArgumentVO);
		            }// end of for
/*		            if ((PretupsI.TRANSFER_CATEGORY_SALE).equalsIgnoreCase(channelTransferVO.getTransferCategory())) {
		                final String[] msgArray = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
		                    .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()) };
		                p_requestVO.setMessageArguments(msgArray);
		                smsKey = PretupsErrorCodesI.O2C_INITIATE_TRANSFER_RECEIVER;
		            } else if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_TRANSFER))) {
		                final String[] msgArray = { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
		                    .getTransferID() };
		                p_requestVO.setMessageArguments(msgArray);
		                smsKey = PretupsErrorCodesI.FOC_INITIATE_TRANSFER_EXTGW_RECEIVER;
		            }*/
		            p_requestVO.setMessageCode(smsKey);
		            p_requestVO.setTransactionID(channelTransferVO.getTransferID());
		            if (SystemPreferences.SECONDARY_NUMBER_ALLOWED && SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
		                if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(p_requestVO.getServiceType()) && p_requestVO.isSenderMessageRequired()) {
		                    if (userPhoneVO != null) {
		                    	smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO, PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS_TXNSUBKEY, PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS_BALSUBKEY);
		                        String array1[] = {BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]) , BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]),channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()) };
		                        BTSLMessages messages1 = new BTSLMessages(PretupsErrorCodesI.O2C_CHNL_RETURN_SUCCESS, array1);
		                        PushMessage pushMessages1 = new PushMessage(loggedUserVO.getMsisdn(), messages1, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
		                        pushMessages1.push();
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
							"O2CReturnRestController " + METHOD_NAME, "", "", "", "Exception:" + e.getMessage());
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
						mcomCon.close("O2CReturnRestController#" + METHOD_NAME);
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
						be.getArgs());
	        	apiResponse.setMessageCode(be.getMessageKey());
	        	apiResponse.setMessage(resmsg);
	        	apiResponse.setService("O2CRETMULRESP");
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
				mcomCon.close("O2CReturnRestController#" + METHOD_NAME);
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " Exited ");
			}
		}
		return apiResponse;
	}
	
	
	/**
	 * This method prepares channel transfer VO for o2c return transaction
	 * @param p_requestVO
	 * @param p_channelTransferVO
	 * @param p_curDate
	 * @param p_channelUserVO
	 * @param p_prdList
	 * @param p_userVO
	 * @throws BTSLBaseException
	 */
	public void prepareChannelTransferVO(RequestVO p_requestVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList) throws BTSLBaseException {
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
            _log.debug("prepareChannelTransferVO",loggerValue );
        }

    	p_channelTransferVO.setSenderGradeCode(p_channelUserVO.getUserGrade());
        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        // who initaite the order.
        p_channelTransferVO.setFromUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setToUserCode(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(p_channelUserVO.getUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(p_channelUserVO.getUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setTransferInitatedBy(p_channelUserVO.getUserID());
        p_channelTransferVO.setSenderTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_SMS);
        p_channelTransferVO.setReceiverTxnProfile(null);
        // adding the some additional information of sender/reciever
        p_channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        p_channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        p_channelTransferVO.setActiveUserId(p_channelUserVO.getUserID());
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelTransferVO.getGraphicalDomainCode());
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
        if(totTax3 < 0)
		{
        	p_channelTransferVO.setTotalTax3(0);
		}
        else {
        	p_channelTransferVO.setTotalTax3(totTax3);
        }
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);
        p_channelTransferVO.setPayInstrumentAmt(totNetPayAmt);
        p_channelTransferVO.setProductType(productType);
        if(PretupsBL.getSystemAmount(commissionQty) <0) {
        	 p_channelTransferVO.setCommQty(0);
        }
        else {
        	 p_channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        }
        p_channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        p_channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));

        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Exiting : ");
        }
    }
	
	
	/**
	 * This method validates the product details
	 * @param products
	 * @param productsAssociated
	 * @param channelTransferVO
	 * @param rowErrorMsgLists
	 * @return
	 */
	public boolean validateProductDetails(List<Products> products, HashMap<String, ArrayList> productsAssociated, ChannelTransferVO channelTransferVO, RowErrorMsgLists rowErrorMsgLists)
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
//				if(!BTSLUtil.isAlphaNumeric(products.get(k).getQty()))
				if(!BTSLUtil.isDecimalValue(products.get(k).getQty()))
				{
					error=true;
					MasterErrorList masterErrorListss = new MasterErrorList();
					masterErrorListss.setErrorCode(PretupsErrorCodesI.QUANTITY_NOT_NUMERIC);
					masterErrorListss.setErrorMsg(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.QUANTITY_NOT_NUMERIC, new String[] {String.valueOf(row)}));
					masterErrorLists1.add(masterErrorListss);
				}
				else if(Double.valueOf(products.get(k).getQty())<=0)
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
					double totalReqAmount = 0;
					if(!(BTSLUtil.isNullString(channelTransferItemsVO1.getRequestedQuantity())))
						totalReqAmount += Double.parseDouble(channelTransferItemsVO1.getRequestedQuantity());
					totalReqAmount += Double.parseDouble(products.get(k).getQty());
					if(totalReqAmount > 0)
					{
						channelTransferItemsVO1.setRequestedQuantity(Double.toString(totalReqAmount));
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
			if(rowErrorMsgLists.getRowErrorMsgList() == null)
				rowErrorMsgLists.setRowErrorMsgList(new ArrayList<RowErrorMsgList> ());
			rowErrorMsgLists.getRowErrorMsgList().add(rowErrorMsgList2Products);
		}
		channelTransferVO.setChannelTransferitemsVOList(itemsList);
		
		if (_log.isDebugEnabled()) {
            _log.debug("validateProductDetails", "Exiting");
        }
		return error;
	}
}
