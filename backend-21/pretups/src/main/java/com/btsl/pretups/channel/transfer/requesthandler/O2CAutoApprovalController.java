package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.common.PretupsResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.gateway.razorpay.OnlinePaymentRequestVO;
import com.btsl.gateway.razorpay.OnlinePaymentResponseVO;
import com.btsl.gateway.razorpay.RazorPay;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelVoucherItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.PGTransactionLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2CAutoApprovalController.name}", description = "${O2CAutoApprovalController.desc}")//@Api(tags = "O2C Services", defaultValue = "Handle O2C Online Payment Request for Stock")
@RestController
@RequestMapping(value = "/v1/o2c")
public class O2CAutoApprovalController /* implements ServiceKeywordControllerI */ {

	private static Log _log = LogFactory.getLog(O2CAutoApprovalController.class.getName());

	/*
	 * public static OperatorUtilI _operatorUtilI = null; static { try {
	 * _operatorUtilI = (OperatorUtilI) Class.forName((String)
	 * PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).
	 * newInstance(); } catch (Exception e) {
	 * 
	 * _log.errorTrace("static", e); EventHandler.handle(EventIDI.SYSTEM_ERROR,v
	 * EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	 * "O2CTransferApprovalController", "", "", "",
	 * "Exception while loading the operator util class in class :" +
	 * ChannelTransferDAO.class.getName() + ":" + e.getMessage()); } }
	 */

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/gatewayPaymentHandler", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "Handle Online Payment", response = BaseResponseMultiple.class, authorizations = {
			@Authorization(value = "Authorization") })

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = OnlinePaymentResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${gatewayPaymentHandler.summary}", description="${gatewayPaymentHandler.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OnlinePaymentResponseVO.class))
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



	public OnlinePaymentResponseVO handleOnlinePayment(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response, @RequestBody OnlinePaymentRequestVO onlinePaymentRequestVO) {
		final String METHOD_NAME = "handleOnlinePayment";
		Connection con = null;
		MComConnectionI mcomCon = null;
		OnlinePaymentResponseVO onlinePaymentResponseVO = new OnlinePaymentResponseVO();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

			// Authenticate the token
			onlinePaymentRequestVO.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(onlinePaymentRequestVO, headers);
			if (BTSLUtil.isNullString(onlinePaymentRequestVO.getPaymentGatewayStatus())) {
				throw new BTSLBaseException(O2CAutoApprovalController.class.getName(), METHOD_NAME,
						PretupsErrorCodesI.GATEWAY_STATUS_REQUIRED);
			}
			if (BTSLUtil.isNullString(onlinePaymentRequestVO.getTransferID())) {
				throw new BTSLBaseException(O2CAutoApprovalController.class.getName(), METHOD_NAME,
						PretupsErrorCodesI.TRANSFER_ID_REQUIRED);
			}
			if (PretupsI.SUCCESS.equalsIgnoreCase(onlinePaymentRequestVO.getPaymentGatewayStatus())) {
				if (BTSLUtil.isNullString(onlinePaymentRequestVO.getPaymentId())) {
					throw new BTSLBaseException(O2CAutoApprovalController.class.getName(), METHOD_NAME,
							PretupsErrorCodesI.PAYMENT_ID_REQUIRED);
				}
				if (BTSLUtil.isNullString(onlinePaymentRequestVO.getSignature())) {
					throw new BTSLBaseException(O2CAutoApprovalController.class.getName(), METHOD_NAME,
							PretupsErrorCodesI.PAYMENT_SIGNATURE_REQUIRED);
				}
				if (BTSLUtil.isNullString(onlinePaymentRequestVO.getOrderId())) {
					throw new BTSLBaseException(O2CAutoApprovalController.class.getName(), METHOD_NAME,
							PretupsErrorCodesI.ORDER_ID_REQUIRED);
				}
			}
			final String msisdn = onlinePaymentRequestVO.getData().getMsisdn();
			final ChannelUserVO receiverUserVO = (ChannelUserVO) ((new ChannelUserDAO()).loadChannelUserDetails(con,
					msisdn));
			ChannelTransferDAO channelTransferDao = new ChannelTransferDAO();
			ChannelTransferVO channelTransferVO = new ChannelTransferVO();
			channelTransferVO.setNetworkCode(receiverUserVO.getNetworkID());
			channelTransferVO.setNetworkCodeFor(receiverUserVO.getNetworkID());
			channelTransferVO.setTransferID(onlinePaymentRequestVO.getTransferID());
			channelTransferDao.loadChannelTransferDetail(con, channelTransferVO,
					PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
			boolean paymentSignatureVerified = false;
			if (PretupsI.SUCCESS.equalsIgnoreCase(onlinePaymentRequestVO.getPaymentGatewayStatus())) {
				paymentSignatureVerified = RazorPay.verifySignature(onlinePaymentRequestVO.getPaymentId(),
						onlinePaymentRequestVO.getOrderId(), onlinePaymentRequestVO.getSignature());
				if (paymentSignatureVerified) {
					RazorPay.fetchPaymentById(con, onlinePaymentRequestVO.getPaymentId(), channelTransferVO);
					con.commit();
					RequestVO requestVO = new RequestVO();
					requestVO.setTransactionID(channelTransferVO.getTransferID());
					requestVO.setRequestGatewayType(channelTransferVO.getRequestGatewayType());
					process(requestVO, channelTransferVO, PretupsI.SUCCESS);
					if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus())) {
						onlinePaymentResponseVO.setMessage(RestAPIStringParser.getMessage(
								new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
								PretupsErrorCodesI.TRANSACTION_AUTO_APPROVED_BY_SYSTEM, null));
					} else if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(channelTransferVO.getStatus())) {
						onlinePaymentResponseVO.setMessage(RestAPIStringParser.getMessage(
								new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
								PretupsErrorCodesI.TRANSACTION_SENT_FOR_APPROVAL, null));
					}
					onlinePaymentResponseVO.setStatus(200);
					response.setStatus(200);
				} else {
					throw new BTSLBaseException(O2CAutoApprovalController.class.getName(), METHOD_NAME,
							PretupsErrorCodesI.INVALID_PAYMENT_DETAILS_GATEWAY);
				}
			} else if (PretupsI.FAIL.equalsIgnoreCase(onlinePaymentRequestVO.getPaymentGatewayStatus())) {
				RequestVO requestVO = new RequestVO();
				requestVO.setTransactionID(channelTransferVO.getTransferID());
				requestVO.setRequestGatewayType(channelTransferVO.getRequestGatewayType());
				process(requestVO, channelTransferVO, PretupsI.FAIL);
				if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(channelTransferVO.getStatus())) {
					onlinePaymentResponseVO.setMessage(RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
							PretupsErrorCodesI.PAYMENT_FAILURE_GATEWAY, null));
				}
				onlinePaymentResponseVO.setStatus(200);
				response.setStatus(200);
			} else {
				throw new BTSLBaseException(O2CAutoApprovalController.class.getName(), METHOD_NAME,
						PretupsErrorCodesI.INVALID_PAYMENT_GATEWAY_STATUS);
			}

			onlinePaymentResponseVO.setTransactionId(onlinePaymentRequestVO.getTransferID());
			ArrayList<OnlinePaymentResponseVO> finalList = new ArrayList<OnlinePaymentResponseVO>();
			finalList.add(onlinePaymentResponseVO);
		} catch (BTSLBaseException be) {
			_log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
			_log.errorTrace(METHOD_NAME, be);
			onlinePaymentResponseVO.setStatus(400);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					be.getMessageKey(), null);
			onlinePaymentResponseVO.setMessageCode(be.getMessageKey());
			onlinePaymentResponseVO.setMessage(resmsg);
			return onlinePaymentResponseVO;

		} catch (Exception e) {

			PretupsResponse<JsonNode> baseResponse = new PretupsResponse<JsonNode>();
			_log.error(METHOD_NAME, "Exception " + e);
			_log.errorTrace(METHOD_NAME, e);
			onlinePaymentResponseVO.setStatus(400);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			onlinePaymentResponseVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.REQ_NOT_PROCESS, null);
			onlinePaymentResponseVO.setMessage(resmsg);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("O2CAutoApprovalController#" + METHOD_NAME);
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " Exited ");
			}
		}
		return onlinePaymentResponseVO;
	}

	public void process(RequestVO p_requestVO, ChannelTransferVO channelTransferVO, String p_pgStatus)
			throws BTSLBaseException {
		final String METHOD_NAME = "process";
		StringBuilder loggerValue = new StringBuilder();
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered p_requestVO: ");
			loggerValue.append(p_requestVO);
			_log.debug(METHOD_NAME, loggerValue);
		}
//        final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Date currentDate = null;
		int updateCount = 0;
		ChannelUserVO channelUserVO = null;
		ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
		HashMap responseMap = new HashMap();
		HashMap reqMap = p_requestVO.getRequestMap();
		try {
			currentDate = new Date();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			final String[] messageArr = p_requestVO.getRequestMessageArray();
			final boolean isFromPaymentGateway = true;
			final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();
//            channelTransferVO.setTransferID(messageArr[1]);
			channelTransferVO.setTransferID(p_requestVO.getTransactionID());
//            String pgStatus = messageArr[2];
			String pgStatus = p_pgStatus;
			// channelTrfDAO.loadChannelTransfersVO(con, channelTransferVO); // new method
			// need to write with transfer_id, transfer_date & PENDING status
//            channelTrfDAO.loadChannelTransferDetail(con, channelTransferVO,PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
			channelTransferVO.setChannelTransferitemsVOList(
					channelTrfDAO.loadChannelTransferItems(con, channelTransferVO.getTransferID()));
			if (BTSLUtil.isNullString(channelTransferVO.getUserMsisdn())) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_TXN_ID);
			}
			if (!PretupsI.SUCCESS.equalsIgnoreCase(pgStatus) && !PretupsI.FAIL.equalsIgnoreCase(pgStatus)) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.INVALID_STATUS);
			}
			// Validate the Interface Amount
			if (mcomCon != null) {
				mcomCon.close("O2CAutoApprovalController#process");
				mcomCon = null;
			}
			/*
			 * if (SystemPreferences.PAYMENT_VERIFICATION_ALLOWED &&
			 * pgStatus.equalsIgnoreCase(PretupsI.SUCCESS)) {
			 * 
			 * Map validateRefmap=_operatorUtilI.validatePaymentRefId(channelTransferVO);
			 * pgStatus=validateRefmap.get("STATUS").toString(); try {
			 * channelTransferVO.setInfo6(validateRefmap.get("VALIDATION_STATUS").toString()
			 * ); }catch(Exception e) { e.getMessage(); } try {
			 * channelTransferVO.setInfo7(validateRefmap.get("REVERSE_STATUS").toString());
			 * }catch(Exception e) { e.getMessage(); } }
			 */

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			channelTransferVO.setModifiedOn(currentDate);
			channelTransferVO.setModifiedBy("SYSTEM");
			channelTransferVO.setOtfFlag(true);
			if (SystemPreferences.AUTO_O2C_APPROVAL_ALLOWED
					&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(channelTransferVO.getTransferSubType())) {
				channelTransferVO.setExternalTxnNum(String.valueOf((new Date()).getTime()));// Need to change
				channelTransferVO.setExternalTxnDate(currentDate);
			}

			channelTransferVO.setInfo1(p_requestVO.getInfo3());
			channelTransferVO.setInfo2(p_requestVO.getInfo2());
			channelTransferVO.setInfo3(p_requestVO.getInfo4());
			channelTransferVO.setInfo4(p_requestVO.getInfo5());
			channelTransferVO.setInfo5(p_requestVO.getInfo6());
			if ("SUCCESS".equals(pgStatus)) {
				channelUserVO = (new UserDAO()).loadUserDetailsByMsisdn(con, channelTransferVO.getUserMsisdn());
				channelTransferVO.setPayInstrumentStatus("PAID");
				if (SystemPreferences.AUTO_O2C_APPROVAL_ALLOWED
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(channelTransferVO.getTransferSubType())) {
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
					String p_userId = channelTransferVO.getToUserID();
					ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(con, channelTransferVO, p_userId,
							currentDate, true);
					ChannelTransferBL.updateNetworkStockTransactionDetails(con, channelTransferVO, p_userId,
							currentDate);
					if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
						ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(con, channelTransferVO,
								p_userId, currentDate, true);
						ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(con, channelTransferVO,
								p_userId, currentDate);
					}
					final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
					userBalancesDAO.updateUserDailyBalances(con, currentDate,
							constructBalanceVOFromTxnVO(channelTransferVO));
					final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
					channelTransferVO.setTransferDate(currentDate);
					if (SystemPreferences.USER_PRODUCT_MULTIPLE_WALLET) {
						channelUserDAO.creditUserBalancesForMultipleWallet(con, channelTransferVO, false, null);
					} else {
						channelUserDAO.creditUserBalances(con, channelTransferVO, false, null);
					}
					channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_O2C);

				} else {
					if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(channelTransferVO.getTransferSubType())) {
						channelTransferVO.setChannelTransferitemsVOList(
								channelTrfDAO.loadChannelTransferItems(con, channelTransferVO.getTransferID()));
					} else {
						channelTransferVO.setChannelVoucherItemsVoList(channelTrfDAO.loadChannelVoucherItemsList(con,
								channelTransferVO.getTransferID(), channelTransferVO.getTransferDate()));
					}
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
					channelTransferVO.setStatusDesc(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
				}
				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
						channelTransferVO.getNetworkCode())
						&& PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus())) {
					ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO);
				}
				if (_log.isDebugEnabled()) {
					loggerValue.append("UserTransferCountsVO: ");
					_log.debug(METHOD_NAME, channelTransferVO.getUserOTFCountsVO());
				}
				ChannelTransferBL.updateOptToChannelUserInCounts(con, channelTransferVO, null, currentDate);
			} else if ("FAIL".equals(pgStatus)) {
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				channelTransferVO.setStatusDesc(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				channelTransferVO.setPayInstrumentStatus("REJECT");
			} else {
				channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
				channelTransferVO.setStatusDesc(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
				channelTransferVO.setPayInstrumentStatus(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
			}
			updateCount = channelTrfDAO.updateChannelTransferApproval(con, channelTransferVO, true,
					isFromPaymentGateway);
			if (updateCount < 0) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace("O2CAutoApprovalController#process", sqle);
				}
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			}
			final String email = channelUserWebDAO.loadUserEmail(con, channelTransferVO.getToUserID());
			channelTransferVO.setEmail(email);
			mcomCon.finalCommit();
			if (SystemPreferences.O2C_EMAIL_NOTIFICATION) {
				if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(channelTransferVO.getStatus()))
					sendEmailNotification(con, channelTransferVO, channelUserVO, channelTrfDAO, "APV1O2CTRF",
							"o2c.email.notification.content");
				else if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus()))
					sendEmailNotification(con, channelTransferVO, channelUserVO, channelTrfDAO, "",
							"o2c.email.notification.content.transfer.completed");
			}
			if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
					channelTransferVO.getNetworkCode())) {
				if (channelTransferVO.isTargetAchieved()
						&& PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus())) {
					// Message handling for OTF
					TargetBasedCommissionMessages tbcm = new TargetBasedCommissionMessages();
					tbcm.loadBaseCommissionProfileDetailsForTargetMessages(con, channelTransferVO.getToUserID(),
							channelTransferVO.getMessageArgumentList());
				}
			}
			p_requestVO.setSuccessTxn(true);
			p_requestVO.setTransactionID(channelTransferVO.getTransferID());
			p_requestVO.setExternalReferenceNum(channelTransferVO.getPayInstrumentNum());
			p_requestVO.setMessageArguments(
					new String[] { channelTransferVO.getTransferID(), p_requestVO.getExternalReferenceNum(),
							PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()) });

			if (p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.GATEWAY_TYPE_SMSC)) {
				if ("SUCCESS".equals(pgStatus)) {
					p_requestVO.setSenderReturnMessage(PretupsI.SUCCESS);
				} else if ("FAIL".equals(pgStatus)) {
					p_requestVO.setSenderReturnMessage(PretupsI.FAIL);
				} else {
					p_requestVO.setSenderReturnMessage(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
				}
			}

			if ("SUCCESS".equals(pgStatus) && SystemPreferences.LMS_APPL) {
				try {
					if (p_requestVO.isSuccessTxn()) {
						final LoyaltyBL _loyaltyBL = new LoyaltyBL();
						final Date date = new Date();
						final LoyaltyVO loyaltyVO = new LoyaltyVO();
						PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
						final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
						final ArrayList arr = new ArrayList();
						loyaltyVO.setServiceType(PretupsI.O2C_MODULE);
						loyaltyVO.setModuleType(PretupsI.O2C_MODULE);
						if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {
							loyaltyVO.setTransferamt(channelTransferVO.getSenderDrQty());
						} else {
							loyaltyVO.setTransferamt(channelTransferVO.getTransferMRP());
						}
						loyaltyVO.setCategory(channelTransferVO.getCategoryCode());
						loyaltyVO.setUserid(channelTransferVO.getToUserID());
						loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());
						loyaltyVO.setSenderMsisdn(channelTransferVO.getToUserCode());
						loyaltyVO.setTxnId(channelTransferVO.getTransferID());
						loyaltyVO.setCreatedOn(date);
						loyaltyVO.setProductCode(channelTransferVO.getProductCode());
						arr.add(loyaltyVO.getUserid());
						promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr);
						loyaltyVO.setSetId(promotionDetailsVO.get_setId());
						if (loyaltyVO.getSetId() == null) {
							_log.error(METHOD_NAME, "Exception durign LMS Module Profile Details are not found");
						} else {
							_loyaltyBL.distributeLoyaltyPoints(PretupsI.O2C_MODULE, channelTransferVO.getTransferID(),
									loyaltyVO);
						}
					}
				} catch (Exception ex) {
					loggerValue.setLength(0);
					loggerValue.append("Exception durign LMS Module ");
					loggerValue.append(ex.getMessage());
					_log.error(METHOD_NAME, loggerValue);
					_log.errorTrace(METHOD_NAME, ex);
				}
			}
			if (p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.MOBILE_APP_GATEWAY)) {
				String resType = null;
				if (PretupsI.FAIL.equalsIgnoreCase(pgStatus)) {
					p_requestVO.setMessageArguments(new String[] { channelTransferVO.getTransferID(),
							PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()) });
					p_requestVO.setMessageCode(PretupsErrorCodesI.O2C_TRANSFER_REJECT_MAPP);
				}
				if (PretupsI.SUCCESS.equalsIgnoreCase(pgStatus)) {
					p_requestVO.setMessageCode(PretupsErrorCodesI.ONLINE_O2C_TRANSFER_SUCCESS);
				}
				StringBuffer responseStr = new StringBuffer("");
				resType = reqMap.get("TYPE") + "RES";
				responseStr.append("{ \"type\": \"" + resType + "\" ,");
				responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
				responseStr.append(" \"txnId\": \"" + messageArr[1] + "\",");
				responseStr.append(" \"message\": \"" + RestAPIStringParser.getMessage(p_requestVO.getLocale(),
						p_requestVO.getMessageCode(), p_requestVO.getMessageArguments()) + "\"}");
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);
			}
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			try {
				mcomCon.finalRollback();
			} catch (SQLException sqle) {
				_log.errorTrace("O2CAutoApprovalController#process", sqle);
			}
			loggerValue.setLength(0);
			loggerValue.append("BTSLBaseException ");
			loggerValue.append(be.getMessage());
			_log.error(METHOD_NAME, loggerValue);
			_log.errorTrace(METHOD_NAME, be);
			if (be.getMessageList() != null && !be.getMessageList().isEmpty()) {
				final String[] array = {
						BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
				p_requestVO.setMessageArguments(array);
			}
			if (be.getArgs() != null) {
				p_requestVO.setMessageArguments(be.getArgs());
			}
			if (be.getMessageKey() != null) {
				p_requestVO.setMessageCode(be.getMessageKey());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			}
			throw be;
//            return;
		} catch (Exception ex) {
			p_requestVO.setSuccessTxn(false);
			try {
				mcomCon.finalRollback();
			} catch (SQLException sqle) {
				_log.errorTrace("O2CAutoApprovalController#process", sqle);
			}
			loggerValue.setLength(0);
			loggerValue.append("BTSLBaseException ");
			loggerValue.append(ex.getMessage());
			_log.error(METHOD_NAME, loggerValue);
			_log.errorTrace(METHOD_NAME, ex);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(ex.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"O2CAutoApprovalController[process]", "", "", "", loggerValue.toString());
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			//throw ex;
			throw new BTSLBaseException(ex.getMessage());
//            return;
		} finally {
			PGTransactionLog.log(p_requestVO.getTransactionID(), channelTransferVO.getPayInstrumentNum(),
					channelTransferVO.getReceiverMsisdn(), channelTransferVO.getNetworkCode(),
					PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_DEBIT, p_requestVO.getRequestMessage(),
					PretupsI.EMPTY,
					p_requestVO.isSuccessTxn() ? PretupsI.TXN_LOG_STATUS_SUCCESS : PretupsI.TXN_LOG_STATUS_FAIL, "");
			if (mcomCon != null) {
				mcomCon.close("O2CAutoApprovalController#process");
				mcomCon = null;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " Exited.. ");
			}
		}
	}

	private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
		if (_log.isDebugEnabled()) {
			_log.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
		}
		final UserBalancesVO userBalancesVO = new UserBalancesVO();
		userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
		userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
		userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
		userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());
		userBalancesVO.setUserMSISDN(p_channelTransferVO.getUserMsisdn());
		if (_log.isDebugEnabled()) {
			_log.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
		}
		return userBalancesVO;
	}

	private void sendEmailNotification(Connection p_con, ChannelTransferVO p_channelTransferVO, ChannelUserVO userVO,
			ChannelTransferDAO p_channelTransferDAO, String p_roleCode, String p_content) {
		final String methodName = "sendEmailNotification";
		final Locale locale = BTSLUtil.getSystemLocaleForEmail();

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}

		try {
			final String from = BTSLUtil.getMessage(locale, "o2c.email.notification.from");
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			String subject = "";
			String message1 = "";

			// For getting name and msisdn of initiator
			ArrayList arrayList = new ArrayList();
			ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
			arrayList = channelUserWebDAO.loadUserNameAndEmail(p_con, p_channelTransferVO.getCreatedBy());

			String message = BTSLUtil.getMessage(locale, p_content) + "<br>"
					+ BTSLUtil.getMessage(locale, "o2c.email.channeluser.details") + "<br>"
					+ BTSLUtil.getMessage(locale, "o2c.email.transferid") + " " + p_channelTransferVO.getTransferID()
					+ "<br>" + BTSLUtil.getMessage(locale, "o2c.email.channeluser.name") + " "
					+ p_channelTransferVO.getToUserName() + "<br>"
					+ BTSLUtil.getMessage(locale, "o2c.email.transfer.mrp") + " "
					+ p_channelTransferVO.getTransferMRPAsString() + "<br>"
					+ BTSLUtil.getMessage(locale, "o2c.email.notification.content.req.amount") + " "
					+ PretupsBL.getDisplayAmount(p_channelTransferVO.getRequestedQuantity()) + "<br>"
					+ BTSLUtil.getMessage(locale, "o2c.email.notification.content.net.payable.amount") + " "
					+ PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount()) + "<br>"
					+ BTSLUtil.getMessage(locale, "o2c.email.transfer.type") + " " + p_channelTransferVO.getType()
					+ "<br>" + BTSLUtil.getMessage(locale, "o2c.email.initiator.name") + " " + arrayList.get(0) + "<br>"
					+ BTSLUtil.getMessage(locale, "o2c.email.initiator.msisdn") + " " + arrayList.get(1);
			if (p_channelTransferVO.getChannelVoucherItemsVoList() != null
					&& !p_channelTransferVO.getChannelVoucherItemsVoList().isEmpty()) {
				message1 = "<table><tr>" + "<td style='width: 5%;'>"
						+ BTSLUtil.getMessage(locale, "o2c.email.notification.serialNumber") + "</td>"
						+ "<td style='width: 10%;'>"
						+ BTSLUtil.getMessage(locale, "o2c.email.notification.denomination") + "</td>"
						+ "<td style='width: 10%;'>" + BTSLUtil.getMessage(locale, "o2c.email.notification.quantity")
						+ "</td>" + "<td style='width: 10%;'>"
						+ BTSLUtil.getMessage(locale, "o2c.email.notification.voucherType") + "</td>" + "</tr>";
				for (int i = 1; i <= p_channelTransferVO.getChannelVoucherItemsVoList().size(); i++) {
					message1 = message1 + "<tr><td>" + i + "</td>" + "<td>"
							+ ((ChannelVoucherItemsVO) p_channelTransferVO.getChannelVoucherItemsVoList().get(i - 1))
									.getTransferMrp()
							+ "</td>" + "<td>"
							+ ((ChannelVoucherItemsVO) p_channelTransferVO.getChannelVoucherItemsVoList()
									.get(i - 1)).getRequiredQuantity()
							+ "</td>" + "<td>"
							+ new VomsProductDAO().getNameFromVoucherType(p_con,
									((ChannelVoucherItemsVO) p_channelTransferVO.getChannelVoucherItemsVoList()
											.get(i - 1)).getVoucherType())
							+ "</td>" + "</tr>";
				}

				message = message + message1 + "</table>";
			}
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String to = "";
			if (!BTSLUtil.isNullString(p_roleCode)) {
				to = p_channelTransferDAO.getEmailIdOfApprover(p_con, p_roleCode, p_channelTransferVO.getToUserID());
				subject = BTSLUtil.getMessage(locale, "o2c.email.notification.subject.approver").toString();
			} else {
				to = p_channelTransferVO.getEmail();
				subject = BTSLUtil.getMessage(locale, "o2c.email.notification.subject.user").toString();
			}

			if (!BTSLUtil.isNullString(to)) {
				EMailSender.sendMail(to, from, bcc, cc, subject, message, isAttachment, pathofFile,
						fileNameTobeDisplayed);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("MAIL CONTENT ", message);
			}
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error("sendEmailNotification ", " Email sending failed" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting ....");
		}
	}

}
