package com.restapi.o2c.service;



import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author pankaj.rawat
 * This controller fetches the an O2C/FOC approval lists.
 */

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2CApprovalListController.name}", description = "${O2CApprovalListController.desc}")//@Api(tags = "O2C Services", defaultValue = "O2C Approval List")
@RestController
@RequestMapping(value = "/v1/o2c")
public class O2CApprovalListController {
	private static Log _log = LogFactory.getLog(O2CApprovalListController.class.getName());
	public static OperatorUtilI _operatorUtil = null;
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getApprovalList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody

	/*@ApiOperation(value = "O2C Approval List", response = O2CApprovalListVO.class, authorizations = {
					@Authorization(value = "Authorization") 
					})

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = O2CApprovalListVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getApprovalList.summary}", description="${getApprovalList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CApprovalListVO.class))
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
	 * This method is for fetching o2c approval list
	 * @param headers
	 * @param o2cApprovalListResponse
	 * @param o2CApprovalListRequestVO
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public O2CApprovalListVO getO2cApprovalList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response,
			@RequestBody O2CApprovalListRequestVO o2CApprovalListRequestVO) 
			throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException
	{
		final String METHOD_NAME = "getO2cApprovalList";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		O2CApprovalListVO o2cApprovalListResponse = null;
		ChannelTransferDAO transferDAO = new ChannelTransferDAO();
		Locale locale = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response); 
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			final String loggedMsisdn = oAuthUser.getData().getMsisdn();
			UserVO senderVO = (new UserDAO()).loadUsersDetails(con, loggedMsisdn);
			o2cApprovalListResponse = new O2CApprovalListVO();
			ArrayList<ChannelTransferVO> approvalListO2C = null;
			ArrayList<ChannelTransferVO> approvalListFOC = null;
			String approvalLevel = o2CApprovalListRequestVO.getApprovalLevel();
			String userMsisdn = o2CApprovalListRequestVO.getMsisdn();
			String category = o2CApprovalListRequestVO.getCategory();
			String domainCode = o2CApprovalListRequestVO.getDomain();
			String geoDomain = o2CApprovalListRequestVO.getGeographicalDomain();
			String level = null;
			if("1".equals(approvalLevel))
				level = PretupsI.CHANNEL_TRANSFER_ORDER_NEW;
			else if("2".equals(approvalLevel))
				level = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
			else if("3".equals(approvalLevel))
				level = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
			
			if(level == null)
			{
				String args[] = {approvalLevel};
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_O2C_APPROVAL_LEVEL, args);
				o2cApprovalListResponse.setMessage(msg);
				o2cApprovalListResponse.setMessageCode(PretupsErrorCodesI.INVALID_O2C_APPROVAL_LEVEL);
				return o2cApprovalListResponse;
			}
			if(!(BTSLUtil.isNullString(userMsisdn)))
			{
				final ChannelUserVO selectedUser = (ChannelUserVO) ((new ChannelUserDAO()).loadChannelUserDetails(con, userMsisdn));
				if(selectedUser == null || !(senderVO.getNetworkID().equals(selectedUser.getNetworkID())))
				{
					String args[] = {userMsisdn};
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, args);
					o2cApprovalListResponse.setMessage(msg);
					o2cApprovalListResponse.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
					return o2cApprovalListResponse;
				}
				
				approvalListO2C = transferDAO.loadChannelTransfersList(con, selectedUser.getUserID(), level, senderVO.getNetworkID(), 
						senderVO.getNetworkID(), selectedUser.getDomainID(), null, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, selectedUser.getCategoryVO().getCategoryCode(), "ALL");
				approvalListFOC = transferDAO.loadChannelTransfersList(con, selectedUser.getUserID(), level, senderVO.getNetworkID(), 
						senderVO.getNetworkID(), selectedUser.getDomainID(), null, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_TRANSFER, selectedUser.getCategoryVO().getCategoryCode(), "ALL");
			}
			else
			{
				if(BTSLUtil.isNullString(domainCode) || BTSLUtil.isNullString(geoDomain) || BTSLUtil.isNullString(category))
				{
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVAILD_SEARCH_CRITERIA, null);
					o2cApprovalListResponse.setMessage(msg);
					o2cApprovalListResponse.setMessageCode(PretupsErrorCodesI.INVAILD_SEARCH_CRITERIA);
					return o2cApprovalListResponse;
				}
				approvalListO2C = transferDAO.loadChannelTransfersList(con, "ALL", level, senderVO.getNetworkID(), 
						senderVO.getNetworkID(), domainCode, geoDomain, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, category, "ALL");
				approvalListFOC = transferDAO.loadChannelTransfersList(con, "ALL", level, senderVO.getNetworkID(), 
						senderVO.getNetworkID(), domainCode, geoDomain, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_TRANSFER, category, "ALL");
			}
			HashMap<String, ArrayList<ChannelTransferVO>> stockTransfersApprovalList = new HashMap<String, ArrayList<ChannelTransferVO>>();
			HashMap<String, ArrayList<ChannelTransferVO>> voucherTransfersApprovalList = new HashMap<String, ArrayList<ChannelTransferVO>>();
			stockTransfersApprovalList.put("FOC", new ArrayList<ChannelTransferVO>());
			stockTransfersApprovalList.put("O2C", new ArrayList<ChannelTransferVO>());
			voucherTransfersApprovalList.put("O2C", new ArrayList<ChannelTransferVO>());
			if(!BTSLUtil.isNullOrEmptyList(approvalListO2C)){
					for(ChannelTransferVO channelTransferVO : approvalListO2C)
					{
						if("V".equals(channelTransferVO.getTransferSubType()))
							voucherTransfersApprovalList.get("O2C").add(channelTransferVO);
						else if("T".equals(channelTransferVO.getTransferSubType()))
							stockTransfersApprovalList.get("O2C").add(channelTransferVO);
							
					}
			}
			if(!BTSLUtil.isNullOrEmptyList(approvalListFOC)){
				for(ChannelTransferVO channelTransferVO : approvalListFOC)
				{
					if("T".equals(channelTransferVO.getTransferSubType()))
						stockTransfersApprovalList.get("FOC").add(channelTransferVO);
				}
			}
			o2cApprovalListResponse.getO2cApprovalList().add(stockTransfersApprovalList);
			o2cApprovalListResponse.getO2cApprovalList().add(voucherTransfersApprovalList);
			if (_log.isDebugEnabled()) {
        		_log.debug(METHOD_NAME, "Stock Approvals Count(O2C + FOC): " + stockTransfersApprovalList.get("O2C").size() + " + " + stockTransfersApprovalList.get("FOC").size());
        		_log.debug(METHOD_NAME, "Voucher Approvals Count: " + voucherTransfersApprovalList.get("O2C").size());
        	}
			o2cApprovalListResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
			o2cApprovalListResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg  = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			o2cApprovalListResponse.setMessage(resmsg);
		}
		catch (BTSLBaseException be) {
       	 	_log.error(METHOD_NAME, "Exception:e=" + be);
            _log.errorTrace(METHOD_NAME, be);
            o2cApprovalListResponse.setStatus(400);
            String resmsg  = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
    	    o2cApprovalListResponse.setMessageCode(be.getMessage());
    	    o2cApprovalListResponse.setMessage(resmsg);
        }
        catch (Exception e) {
        	_log.error(METHOD_NAME, "Exception:e=" + e);
            _log.errorTrace(METHOD_NAME, e);
            o2cApprovalListResponse.setStatus(400);
			o2cApprovalListResponse.setMessageCode(e.getMessage());
			o2cApprovalListResponse.setMessage(e.getMessage());
        } finally {
        	try {
        		if (mcomCon != null) {
        			mcomCon.close("O2CApprovalListController#" + METHOD_NAME);
        			mcomCon = null;
        		}
        	} 
        	catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        	}
        	if (_log.isDebugEnabled()) {
        		_log.debug(METHOD_NAME, " Exited ");
        	}
        }
        return o2cApprovalListResponse;
	}
	
}
