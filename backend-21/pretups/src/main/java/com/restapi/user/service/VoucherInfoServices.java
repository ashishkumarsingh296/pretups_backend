package com.restapi.user.service;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


import jakarta.servlet.http.HttpServletResponse;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.http.HttpStatus;
////import org.apache.struts.action.ActionForward;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.CsvWriter;
import com.btsl.common.PretupsResponse;
import com.btsl.common.Reader;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsPrintBatchVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.o2c.service.ValidateVoucherInfoRequest;
import com.restapi.o2c.service.ValidateVoucherInfoResponseVO;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;
import com.web.voms.voucher.businesslogic.VomsBatchesWebDAO;
import com.web.voms.voucher.businesslogic.VomsVoucherWebDAO;
import com.web.voms.voucher.businesslogic.VomsVoucherWebQry;

import io.swagger.v3.oas.annotations.Parameter;


import io.swagger.v3.oas.annotations.Parameter;

/*@Path("/voucher")
@Api(value="Voucher Services")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${VoucherInfoServices.name}", description = "${VoucherInfoServices.desc}")//@Api(tags = "Voucher Services")
@RestController
@RequestMapping(value = "/v1/voucher")
public class VoucherInfoServices implements ServiceKeywordControllerI, Runnable {

	public static final Log log = LogFactory.getLog(VoucherInfoServices.class.getName());
	boolean o2cValidation = true;

	/**
	 * 
	 * @param voucherTypeRequestVO
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @POST
	 * 
	 * @Path("/getvouchertypes")
	 * 
	 * @Consumes(value=MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON)
	 * 
	 * 
	 * @PostMapping(value = "/getvouchertypes", consumes =
	 * MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	 * 
	 * @ResponseBody
	 * 
	 * @ApiOperation(value = "View available voucher types", response =
	 * PretupsResponse.class, authorizations = {
	 * 
	 * @Authorization(value = "Authorization") })
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response =
	 * PretupsResponse.class),
	 * 
	 * @ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400,
	 * message = "Bad Request"),
	 * 
	 * @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404,
	 * message = "Not Found") }) public
	 * PretupsResponse<List<C2CVoucherTypeResponse>> getVoucherTypeInfo(
	 * 
	 * @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	 * 
	 * @Parameter(description = SwaggerAPIDescriptionI.C2C_VOUCHER_TYPE, required =
	 * true) @RequestBody VoucherTypeRequestVO voucherTypeRequestVO,
	 * HttpServletResponse response1) throws JsonParseException,
	 * JsonMappingException, JsonProcessingException, IOException { final String
	 * methodName = "getVoucherTypeInfo"; if (log.isDebugEnabled()) {
	 * log.debug(methodName, "Entered "); } Connection con = null; MComConnectionI
	 * mcomCon = null; PretupsResponse<List<C2CVoucherTypeResponse>> response = new
	 * PretupsResponse<List<C2CVoucherTypeResponse>>();
	 * 
	 * final String identifierType = voucherTypeRequestVO.getIdentifierType(); final
	 * String identifierValue = voucherTypeRequestVO.getIdentifierValue(); final
	 * String voucherType = voucherTypeRequestVO.getData().getVoucherType(); if
	 * (voucherType == null) { response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "Invalid MEssage Format"); return response; } List<C2CVoucherTypeResponse>
	 * voucherTypes; UserDAO userDao = new UserDAO(); String[] allowedCategories =
	 * SystemPreferences.USER_ALLOWED_VINFO.split(","); String[] vouchersType =
	 * voucherType.split(","); ChannelUserVO channelUserVO = null;
	 * 
	 * try { OAuthenticationUtil.validateTokenApi(headers); mcomCon = new
	 * MComConnection(); con = mcomCon.getConnection();
	 * 
	 * PretupsRestUtil.validateLoggedInUser(identifierType, identifierValue, con,
	 * response, allowedCategories);
	 * 
	 * if (response.hasFormError()) { response.setStatus(false);
	 * response.setStatusCode(PretupsI.RESPONSE_FAIL); return response; }
	 * 
	 * if (!"".equals(voucherTypeRequestVO.getData().getMsisdn())) {
	 * log.debug(methodName, "Loading user by msisdn"); channelUserVO =
	 * userDao.loadUserDetailsByMsisdn(con,
	 * voucherTypeRequestVO.getData().getMsisdn()); } else if
	 * (!"".equals(voucherTypeRequestVO.getData().getLoginId())) {
	 * log.debug(methodName, "Loading user by loginID"); channelUserVO =
	 * userDao.loadAllUserDetailsByLoginID(con,
	 * voucherTypeRequestVO.getData().getLoginId()); } else {
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false, "no loginId or msisdn");
	 * return response; }
	 * 
	 * if (channelUserVO == null) { response.setResponse(PretupsI.RESPONSE_FAIL,
	 * false, "invalid loginId or msisdn"); return response; }
	 * 
	 * VomsCategoryWebDAO categoryWebDAO = new VomsCategoryWebDAO();
	 * ArrayList<VomsCategoryVO> voucherTypeList; voucherTypeList =
	 * categoryWebDAO.loadUserCategoryListVoucherType(con,
	 * channelUserVO.getUserID(), vouchersType); if (voucherTypeList.size() == 0) {
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false, "No Voucher Type found");
	 * return response; } voucherTypes = new ArrayList<C2CVoucherTypeResponse>();
	 * 
	 * if (voucherTypeList.size() > 0) {
	 * 
	 * for (VomsCategoryVO voms : voucherTypeList) { C2CVoucherTypeResponse
	 * responseObject = new C2CVoucherTypeResponse();
	 * 
	 * responseObject.setCode(voms.getVoucherType());
	 * responseObject.setValue(voms.getName());
	 * 
	 * voucherTypes.add(responseObject); } }
	 * response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, voucherTypes); return
	 * response; } catch (BTSLBaseException be) { log.error(methodName,
	 * "Exception:e=" + be); log.errorTrace(methodName, be); if
	 * (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	 * response1.setStatus(HttpStatus.SC_UNAUTHORIZED); } else {
	 * response1.setStatus(HttpStatus.SC_BAD_REQUEST); }
	 * 
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "Error while getting user details"); return response; } catch (Exception e) {
	 * log.debug(methodName, "In catch block"); log.error(methodName, "Exception:e="
	 * + e); response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "Error while getting user details"); return response; } finally { if (mcomCon
	 * != null) { mcomCon.close("VoucherInfoServices#" + methodName); mcomCon =
	 * null; } if (log.isDebugEnabled()) { log.debug(methodName, "Exiting"); } }
	 * 
	 * }
	 */

	/**
	 * 
	 * @param voucherSegmentRequestVO
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("unchecked")
	/*
	 * @POST
	 * 
	 * @Path("/getvouchersegments")
	 * 
	 * @Consumes(value=MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON)
	 */
	/*
	 * @PostMapping(value = "/getvouchersegments", consumes =
	 * MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	 * 
	 * @ResponseBody
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response =
	 * PretupsResponse.class),
	 * 
	 * @ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400,
	 * message = "Bad Request"),
	 * 
	 * @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404,
	 * message = "Not Found") })
	 * 
	 * @ApiOperation(value = "View available voucher segments", response =
	 * PretupsResponse.class, authorizations = {
	 * 
	 * @Authorization(value = "Authorization") }) public
	 * PretupsResponse<List<C2CVoucherSegmentResponse>> getVoucherSegmentInfo(
	 * 
	 * @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	 * 
	 * @Parameter(description = SwaggerAPIDescriptionI.C2C_VOUCHER_SEGMENT, required =
	 * true) @RequestBody VoucherSegmentRequestVO voucherSegmentRequestVO,
	 * HttpServletResponse response1) throws JsonParseException,
	 * JsonMappingException, JsonProcessingException, IOException { final String
	 * methodName = "getVoucherSegmentInfo"; if (log.isDebugEnabled()) {
	 * log.debug(methodName, "Entered "); } Connection con = null; MComConnectionI
	 * mcomCon = null; PretupsResponse<List<C2CVoucherSegmentResponse>> response =
	 * new PretupsResponse<List<C2CVoucherSegmentResponse>>();
	 * 
	 * final String identifierType = voucherSegmentRequestVO.getIdentifierType();
	 * final String identifierValue = voucherSegmentRequestVO.getIdentifierValue();
	 * List<ListValueVO> segmentList = null; List<C2CVoucherSegmentResponse>
	 * segmentListStr = null; UserDAO userDAO = new UserDAO(); String[]
	 * allowedCategories = SystemPreferences.USER_ALLOWED_VINFO.split(",");
	 * ChannelUserVO channelUserVO = null;
	 * 
	 * try { OAuthenticationUtil.validateTokenApi(headers); mcomCon = new
	 * MComConnection(); con = mcomCon.getConnection();
	 * 
	 * PretupsRestUtil.validateLoggedInUser(identifierType, identifierValue, con,
	 * response, allowedCategories);
	 * 
	 * if (response.hasFormError()) { response.setStatus(false);
	 * response.setStatusCode(PretupsI.RESPONSE_FAIL); return response; }
	 * 
	 * if (!"".equals(voucherSegmentRequestVO.getData().getMsisdn())) {
	 * log.debug(methodName, "Loading user by msisdn"); channelUserVO =
	 * userDAO.loadUserDetailsByMsisdn(con,
	 * voucherSegmentRequestVO.getData().getMsisdn()); } else if
	 * (!"".equals(voucherSegmentRequestVO.getData().getLoginId())) {
	 * log.debug(methodName, "Loading user by loginID"); channelUserVO =
	 * userDAO.loadAllUserDetailsByLoginID(con,
	 * voucherSegmentRequestVO.getData().getLoginId()); } else {
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false, "no loginId or msisdn");
	 * return response; }
	 * 
	 * if (channelUserVO == null) { response.setResponse(PretupsI.RESPONSE_FAIL,
	 * false, "invalid login credentials"); return response; } if
	 * ("".equals(voucherSegmentRequestVO.getData().getVoucherType())) {
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false, "voucher type is empty");
	 * return response; }
	 * 
	 * if (isVoucherTypeValid(con,
	 * voucherSegmentRequestVO.getData().getVoucherType(), channelUserVO) == false)
	 * { response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "voucher type is invalid"); return response; }
	 * 
	 * segmentListStr = new ArrayList<C2CVoucherSegmentResponse>();
	 * 
	 * segmentList =
	 * com.btsl.util.BTSLUtil.getSegmentList(voucherSegmentRequestVO.getData().
	 * getVoucherType(), LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT,
	 * true));
	 * 
	 * for (ListValueVO value : segmentList) { C2CVoucherSegmentResponse voucherObj
	 * = new C2CVoucherSegmentResponse();
	 * 
	 * voucherObj.setCode(value.getValue());
	 * 
	 * voucherObj.setValue(value.getLabel());
	 * 
	 * segmentListStr.add(voucherObj); }
	 * 
	 * response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, segmentListStr); }
	 * catch (BTSLBaseException be) { log.error(methodName, "Exception:e=" + be);
	 * log.errorTrace(methodName, be); if
	 * (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	 * response1.setStatus(HttpStatus.SC_UNAUTHORIZED); } else {
	 * response1.setStatus(HttpStatus.SC_BAD_REQUEST); }
	 * 
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "Error while getting user details"); return response; } catch (Exception e) {
	 * log.debug(methodName, "In catch block"); log.error(methodName, "Exception:e="
	 * + e); response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "Error while getting user details"); return response; } finally { if (mcomCon
	 * != null) { mcomCon.close("VoucherInfoServices#" + methodName); mcomCon =
	 * null; } if (log.isDebugEnabled()) { log.debug(methodName, "Exiting"); } }
	 * log.debug(methodName, response); return response; }
	 */

	/**
	 * 
	 * @param voucherDenominationRequestVO
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @POST
	 * 
	 * @Path("/getvoucherdenominations")
	 * 
	 * @Consumes(value=MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON)
	 * 
	 * @PostMapping(value = "/getvoucherdenominations", consumes
	 * =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	 * 
	 * @ResponseBody
	 * 
	 * @ApiResponses(value = {
	 * 
	 * @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
	 * 
	 * @ApiResponse(code = 201, message = "Created"),
	 * 
	 * @ApiResponse(code = 400, message = "Bad Request"),
	 * 
	 * @ApiResponse(code = 401, message = "Unauthorized"),
	 * 
	 * @ApiResponse(code = 404, message = "Not Found") })
	 * 
	 * @ApiOperation(value = "View available voucher denominations", response =
	 * PretupsResponse.class,authorizations = {
	 * 
	 * @Authorization(value = "Authorization")}) public
	 * PretupsResponse<List<String>>getVoucherDenominationInfo(
	 * 
	 * @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	 * 
	 * @Parameter(description=SwaggerAPIDescriptionI.C2C_VOUCHER_DENOMINATION, required
	 * =true)
	 * 
	 * @RequestBody VoucherDenominationRequestVO voucherDenominationRequestVO,
	 * HttpServletResponse response1) throws JsonParseException,
	 * JsonMappingException, JsonProcessingException, IOException { final String
	 * methodName = "getVoucherDenominationInfo"; if (log.isDebugEnabled()) {
	 * log.debug(methodName, "Entered "); } Connection con = null; MComConnectionI
	 * mcomCon = null; PretupsResponse<List<String>> response = new
	 * PretupsResponse<List<String>>();
	 * 
	 * final String identifierType =
	 * voucherDenominationRequestVO.getIdentifierType(); final String
	 * identifierValue = voucherDenominationRequestVO.getIdentifierValue(); UserDAO
	 * userDAO = new UserDAO(); String[] allowedCategories =
	 * SystemPreferences.USER_ALLOWED_VINFO.split(","); VomsCategoryWebDAO
	 * vomsCategorywebDAO = new VomsCategoryWebDAO(); ArrayList<String>
	 * denominationList = null; String mrp = null; List<VomsCategoryVO>
	 * categoryList= null; ChannelUserVO channelUserVO = null; String segment = "";
	 * 
	 * try { OAuthenticationUtil.validateTokenApi(headers); mcomCon = new
	 * MComConnection(); con=mcomCon.getConnection();
	 * 
	 * PretupsRestUtil.validateLoggedInUser(identifierType, identifierValue, con,
	 * response, allowedCategories);
	 * 
	 * if (response.hasFormError()) { response.setStatus(false);
	 * response.setStatusCode(PretupsI.RESPONSE_FAIL); return response; }
	 * 
	 * if(!"".equals(voucherDenominationRequestVO.getData().getMsisdn())) {
	 * log.debug(methodName, "Loading user by msisdn"); channelUserVO =
	 * userDAO.loadUserDetailsByMsisdn(con,
	 * voucherDenominationRequestVO.getData().getMsisdn()); } else
	 * if(!"".equals(voucherDenominationRequestVO.getData().getLoginId())) {
	 * log.debug(methodName, "Loading user by loginID"); channelUserVO =
	 * userDAO.loadAllUserDetailsByLoginID(con,
	 * voucherDenominationRequestVO.getData().getLoginId()); } else {
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false, "no loginId or msisdn");
	 * return response; }
	 * 
	 * if(channelUserVO == null) { response.setResponse(PretupsI.RESPONSE_FAIL,
	 * false, "invalid login credentials"); return response; }
	 * 
	 * if("".equals(voucherDenominationRequestVO.getData().getVoucherType())) {
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false, "voucher type is empty");
	 * return response; } if(isVoucherTypeValid(con,
	 * voucherDenominationRequestVO.getData().getVoucherType(), channelUserVO) ==
	 * false) { response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "voucher type is invalid"); return response; }
	 * 
	 * segment = voucherDenominationRequestVO.getData().getVoucherSegment();
	 * 
	 * if("".equals(segment)) { response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "voucher segment is empty"); return response; }
	 * if(segment.equalsIgnoreCase("NL")) { segment = "NL"; } else
	 * if(segment.equalsIgnoreCase("LC")) { segment = "LC"; } else {
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "voucher segment is invalid"); return response; }
	 * 
	 * categoryList = vomsCategorywebDAO.loadCategoryList(con,
	 * voucherDenominationRequestVO.getData().getVoucherType(),
	 * VOMSI.VOMS_STATUS_ACTIVE, VOMSI.EVD_CATEGORY_TYPE_FIXED, true,
	 * channelUserVO.getNetworkID(), segment);
	 * 
	 * if (categoryList.isEmpty()) { response.setResponse(PretupsI.RESPONSE_FAIL,
	 * false, "voms.download.noactive.mrpfor.voucher");
	 * 
	 * return response; }
	 * 
	 * denominationList = new ArrayList<String>();
	 * 
	 * for (int i = 0; i < categoryList.size(); i++) { VomsCategoryVO vomsCategoryVO
	 * = (VomsCategoryVO) categoryList.get(i);
	 * 
	 * mrp = Double.toString(vomsCategoryVO.getMrp()); denominationList.add(mrp); }
	 * response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, denominationList); }
	 * catch(BTSLBaseException be) { log.error(methodName, "Exception:e=" + be);
	 * log.errorTrace(methodName, be);
	 * if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	 * response1.setStatus(HttpStatus.SC_UNAUTHORIZED); } else{
	 * response1.setStatus(HttpStatus.SC_BAD_REQUEST); }
	 * 
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "Error while getting user details"); return response; } catch(Exception e) {
	 * log.debug(methodName, "In catch block"); log.error(methodName, "Exception:e="
	 * + e); response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	 * response.setResponse(PretupsI.RESPONSE_FAIL, false,
	 * "Error while getting user details"); return response; } finally { if(mcomCon
	 * != null) { mcomCon.close("VoucherInfoServices#"+methodName); mcomCon=null; }
	 * if (log.isDebugEnabled()) { log.debug(methodName, "Exiting"); } } return
	 * response; }
	 */

	/**
	 * 
	 * @param con
	 * @param voucherType
	 * @param channelUserVO
	 * @return
	 * @throws BTSLBaseException
	 */
	boolean isVoucherTypeValid(Connection con, String voucherType, ChannelUserVO channelUserVO)
			throws BTSLBaseException {
		final String methodName = "isVoucherTypeValid";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		boolean isVoucherFound = false;
		VomsCategoryWebDAO categoryWebDAO = new VomsCategoryWebDAO();

		@SuppressWarnings("unchecked")
		ArrayList<VomsCategoryVO> voucherTypeList = categoryWebDAO.loadUserCategoryList(con, channelUserVO.getUserID());

		String[] allowedVoucherType = VomsUtil.getAllowedVoucherTypesForScreen(PretupsI.SCREEN_O2C);
		ArrayList<VomsCategoryVO> filteredVoucherList = VomsUtil.getAllowedVoucherType(allowedVoucherType,
				voucherTypeList);

		if (voucherTypeList.size() > 0) {

			for (VomsCategoryVO voms : filteredVoucherList) {
				if (voms.getVoucherType().equals(voucherType)) {
					isVoucherFound = true;
					break;
				}
			}
		}
		return isVoucherFound;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void process(RequestVO p_requestVO) {
		String methodName = "process";
		log.debug(methodName, "Entered");

		Connection con = null;
		MComConnectionI mcomCon = null;

		HashMap responseMap = new HashMap();
		VoucherTypeRequestVO voucherTypeRequestVO = new VoucherTypeRequestVO();

		TypeData data = new TypeData();

		voucherTypeRequestVO.setData(data);

		ChannelUserVO channelUserVO = new ChannelUserVO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		StringBuffer responseStr = new StringBuffer("");

		HashMap reqMap = p_requestVO.getRequestMap();

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null)) {
				voucherTypeRequestVO.getData().setMsisdn((String) reqMap.get("MSISDN"));
				voucherTypeRequestVO.getData().setLoginId((String) reqMap.get("LOGINID"));
			}
			String[] vouchersType = null;
			if (reqMap != null) {
				if (reqMap.get("VOUCHERLIST") != null) {
					String voucherType = (String) reqMap.get("VOUCHERLIST");
					vouchersType = voucherType.split(",");
				}
			}
			log.debug(methodName, "Loading user by msisdn");
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, voucherTypeRequestVO.getData().getMsisdn());

			VomsCategoryWebDAO categoryWebDAO = new VomsCategoryWebDAO();
			ArrayList<VomsCategoryVO> voucherTypeList;

			voucherTypeList = categoryWebDAO.loadUserCategoryListVoucherType(con, channelUserVO.getUserID(),
					vouchersType);
			if (voucherTypeList.size() == 0) {
				p_requestVO.setSuccessTxn(false);
				p_requestVO.setSenderReturnMessage("No Voucher Type found!");
			}
			C2CVoucherTypeResponse responseObject = new C2CVoucherTypeResponse();

			String responseString = "";

			if (voucherTypeList.size() > 0) {
				responseStr.append("VOUCHERTYPES=[");
				for (VomsCategoryVO voms : voucherTypeList) {

					responseObject.setCode(voms.getVoucherType());
					responseObject.setValue(voms.getName());

					responseStr.append(responseObject.toString());
					responseStr.append(",");

				}
				responseString = responseStr.substring(0, responseStr.length() - 1);
				responseString = responseString + "]";
			}

			log.debug("response ", "Voucher Type responseStr  " + responseStr);

			responseMap.put("RESPONSE", responseString);

			p_requestVO.setResponseMap(responseMap);
			p_requestVO.setSuccessTxn(true);
			p_requestVO.setMessageCode("20000");
			p_requestVO.setSenderReturnMessage("Transaction has been completed!");
		} catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setSenderReturnMessage("Transaction has been failed!");
		} finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherInfoServices#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}

	}

	/**
	 * 
	 * @param voucherInfo
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	@GetMapping(value = "/getvoucherinfo", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found") })
	@ApiOperation(value = "View available voucher info", response = PretupsResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getvoucherinfo.summary}", description="${getvoucherinfo.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PretupsResponse.class))
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

	public PretupsResponse<List<C2CVoucherInfoResponseVO>> getVoucherInfo(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "User ID",  required = true) @RequestParam("userID") String userID,
			HttpServletResponse response1)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "getvoucherinfo";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<C2CVoucherInfoResponseVO>> response = new PretupsResponse<List<C2CVoucherInfoResponseVO>>();

		UserDAO userDAO = new UserDAO();
		VomsCategoryWebDAO vomsCategorywebDAO = new VomsCategoryWebDAO();
		ArrayList<C2CVoucherInfoResponseVO> denominationList = new ArrayList<C2CVoucherInfoResponseVO>();
		List<VomsCategoryVO> categoryList = null;
		ChannelUserVO channelUserVO = null;
		ChannelUserVO loggedInUserVO = null;

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());

			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers);

			channelUserVO = userDAO.loadUserDetailsFormUserID(con, userID);

			if (channelUserVO == null) {
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "invalid login credentials");
				return response;
			}

			
			loggedInUserVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUserData.getData().getLoginid());

			if(loggedInUserVO.getUserType().equals(PretupsI.USER_TYPE_CHANNEL))
			{
				o2cValidation = false;
			}
			
			categoryList = vomsCategorywebDAO.loadCategoryListForAllVoucherTypes(con, VOMSI.VOMS_STATUS_ACTIVE,
					VOMSI.EVD_CATEGORY_TYPE_FIXED, true, channelUserVO.getNetworkID());

			if (categoryList.isEmpty()) {
				response.setResponse(PretupsI.RESPONSE_FAIL, false, PretupsErrorCodesI.NO_VOUCHER_PRESENT);

				return response;
			}
			HashMap<String, C2CVoucherInfoResponseVO> responseObj = new HashMap<String, C2CVoucherInfoResponseVO>();

			for (int i = 0; i < categoryList.size(); i++) {
				VomsCategoryVO vomsCategoryVO = (VomsCategoryVO) categoryList.get(i);

				if (responseObj.get(vomsCategoryVO.getVoucherType()) == null) {
					C2CVoucherInfoResponseVO c2CVoucherInfoResponseVO = new C2CVoucherInfoResponseVO();
					VoucherSegmentResponse voucherSegmentResponse = new VoucherSegmentResponse();

					List<VoucherSegmentResponse> voucherSegmentList = new ArrayList<VoucherSegmentResponse>();

					// c2CVoucherInfoResponseVO.getSegment().add(voucherSegmentResponse);

					voucherSegmentList.add(voucherSegmentResponse);

					c2CVoucherInfoResponseVO.setSegment(voucherSegmentList);

					c2CVoucherInfoResponseVO.setValue(vomsCategoryVO.getVoucherType());

					voucherSegmentResponse.setSegmentType(vomsCategoryVO.getSegment());
					voucherSegmentResponse.setSegmentValue(BTSLUtil.getSegmentDesc(vomsCategoryVO.getSegment()));

					List<String> denominations = new ArrayList<String>();
					denominations.add(Double.toString(vomsCategoryVO.getMrp()));
					voucherSegmentResponse.setDenominations(denominations);

					responseObj.put(vomsCategoryVO.getVoucherType(), c2CVoucherInfoResponseVO);

				} else {
					C2CVoucherInfoResponseVO c2CVoucherInfoResponseVO = responseObj
							.get(vomsCategoryVO.getVoucherType());

					boolean segmentExists = false;
					List<VoucherSegmentResponse> segmentList = c2CVoucherInfoResponseVO.getSegment();

					for (VoucherSegmentResponse responseListObj : segmentList) {
						if (responseListObj.getSegmentType().equals(vomsCategoryVO.getSegment())) {
							segmentExists = true;
							responseListObj.getDenominations().add(Double.toString(vomsCategoryVO.getMrp()));
						}
					}
					if (!segmentExists) {
						VoucherSegmentResponse newElement = new VoucherSegmentResponse();

						newElement.setSegmentType(vomsCategoryVO.getSegment());
						newElement.setSegmentValue(BTSLUtil.getSegmentDesc(vomsCategoryVO.getSegment()));

						List<String> denoms = new ArrayList<String>();

						denoms.add(Double.toString(vomsCategoryVO.getMrp()));
						newElement.setDenominations(denoms);

						c2CVoucherInfoResponseVO.getSegment().add(newElement);

					}

				}

			}

			for (Entry<String, C2CVoucherInfoResponseVO> entry : responseObj.entrySet()) {
				denominationList.add(entry.getValue());
			}

			isVoucherTypeValid(con, denominationList, channelUserVO);

			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, denominationList);
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				String unauthorised = Integer.toString(HttpStatus.SC_UNAUTHORIZED);

				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setResponse(HttpStatus.SC_UNAUTHORIZED, false, unauthorised);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "Error while getting user details");

			}

			return response;
		} catch (Exception e) {
			log.debug(methodName, "In catch block");
			log.error(methodName, "Exception:e=" + e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setResponse(PretupsI.RESPONSE_FAIL, false, "Error while getting user details");
			return response;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherInfoServices#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}
		return response;
	}
	
	/**
	 * 
	 * @param voucherInfo
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	@PostMapping(value = "/validatetvoucherinfo", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ValidateVoucherInfoResponseVO.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found") })
	@ApiOperation(value = "Validate voucher info", response = BaseResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${validatetvoucherinfo.summary}", description="${validatetvoucherinfo.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ValidateVoucherInfoResponseVO.class))
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

	public BaseResponse validateVoucherInfo(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, @RequestBody ValidateVoucherInfoRequest validateVoucherInfoRequest,
			HttpServletResponse response1)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "validateVoucherInfo";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ValidateVoucherInfoResponseVO response = new ValidateVoucherInfoResponseVO();

	

		try 
		{
		
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());

			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers);
			
			Long from = Long.parseLong(validateVoucherInfoRequest.getFromSerialNumber());
			Long to = Long.parseLong(validateVoucherInfoRequest.getToSerialNumber());
			
			long diff =  (to - from)+1;
			
			if(diff <= validateVoucherInfoRequest.getCount())
			{	
				response.setVoucherCount(BTSLUtil.parseLongToInt(diff));
				response.setStatus(200);
				response.setMessage("Success");
				
				
			}
			else
			{
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setMessage("No of vouchers requested ("+validateVoucherInfoRequest.getCount() + ") is not same as diff of from and to serial numbers ("+ diff + ")");
			}
			
			

		}catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				String unauthorised = Integer.toString(HttpStatus.SC_UNAUTHORIZED);

				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setMessageCode(unauthorised);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setMessage("Error while getting user details");

			}
		} 
		catch (Exception e) {
			log.debug(methodName, "In catch block");
			log.error(methodName, "Exception:e=" + e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessage("There was an error processing your request");
		} finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherInfoServices#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}
		return response;
	}

	void isVoucherTypeValid(Connection con, List<C2CVoucherInfoResponseVO> voucherValues, ChannelUserVO channelUserVO)
			throws BTSLBaseException {
		final String methodName = "isVoucherTypeValid";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		VomsCategoryWebDAO categoryWebDAO = new VomsCategoryWebDAO();

		@SuppressWarnings("unchecked")
		ArrayList<VomsCategoryVO> voucherTypeList = categoryWebDAO.loadUserCategoryList(con, channelUserVO.getUserID());

		String[] allowedVoucherType = {VOMSI.VOUCHER_TYPE_DIGITAL, VOMSI.VOUCHER_TYPE_TEST_DIGITAL, 
                VOMSI.VOUCHER_TYPE_ELECTRONIC, VOMSI.VOUCHER_TYPE_TEST_ELECTRONIC, VOMSI.VOUCHER_TYPE_PHYSICAL, VOMSI.VOUCHER_TYPE_TEST_PHYSICAL}; ;
		
                
        if(o2cValidation)
		{
			allowedVoucherType = VomsUtil.getAllowedVoucherTypesForScreen(PretupsI.SCREEN_O2C);
		}
		
        ArrayList<VomsCategoryVO> filteredVoucherList = VomsUtil.getAllowedVoucherType(allowedVoucherType,
				voucherTypeList);
		
			boolean isFound = false;
			if (voucherTypeList.size() > 0 && voucherValues.size() > 0) {
				Iterator<C2CVoucherInfoResponseVO> voucherListItr = voucherValues.iterator();
				while (voucherListItr.hasNext()) {
					C2CVoucherInfoResponseVO responseObj = voucherListItr.next();
					isFound = false;

					for (VomsCategoryVO voms : filteredVoucherList) {
						if (voms.getVoucherType().equals(responseObj.getValue())&&!BTSLUtil.isNullorEmpty(voms.getName())) {
							responseObj.setDisplayValue(voms.getName());
							isFound = true;
							break;
						}
					}
					if (!isFound) {
						voucherListItr.remove();
					}

				}
			}
			else {
				voucherValues.clear();
			}
	}
	
	/**
	 * 
	 * @param voucherInfo
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	@GetMapping(value = "/getVoucherBatchList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found") })
	@ApiOperation(value = "View batch list available for voucher download", response = PretupsResponse.class, authorizations = {
			@Authorization(value = "Authorization") }, notes= SwaggerAPIDescriptionI.VoucherBatchListFetch)
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${getVoucherBatchList.summary}", description="${getVoucherBatchList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PretupsResponse.class))
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

	public PretupsResponse<List<VomsPrintBatchVO>> getVoucherBatchList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "batchType",  required = true) @RequestParam("batchType") String batchType,
			@Parameter(description = "From Date",  required = true) @RequestParam("fromDate") String fromDate,
			@Parameter(description = "To Date",  required = true) @RequestParam("toDate") String toDate,
			HttpServletResponse response1)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "getVoucherBatchList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<VomsPrintBatchVO>> response = new PretupsResponse<List<VomsPrintBatchVO>>();

		//ActionForward forward = null;
        UserVO sessionUserVO = null;
        String decrypkey = null;
        VomsVoucherWebDAO vomsVoucherWebDAO = null;
        final VomsUtil util;
        VomsBatchesWebDAO vomsBatcheswebDAO = null;
        ArrayList<VomsPrintBatchVO> printlist = null;
        UserDAO userDAO = null;

        Date _fromDate = null, _toDate = null;
        
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());

			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers);
			
			if(fromDate==null || toDate==null) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Date" });
			}
			
			if(batchType==null) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Batch type" });
			}
			
			if(!batchType.equals("Y") && !batchType.equals("N") && !batchType.equals("ALL")) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Batch type" });
			}
			userDAO = new UserDAO();
			sessionUserVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUserData.getData().getLoginid());
			util = new VomsUtil();
			
			
			vomsBatcheswebDAO = new VomsBatchesWebDAO();
			
			//setting decryp key
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_USER_KEY_REQD))).booleanValue()) {
                // check if key present in DB for logged in user then we have to
                // generate a new key
                vomsVoucherWebDAO = new VomsVoucherWebDAO();
                decrypkey = vomsVoucherWebDAO.getVomsDecKeyUser(con, sessionUserVO.getUserID());
                if (BTSLUtil.isNullString(decrypkey)) {
                    // generate new key for user and update the same in DB
                    decrypkey = sessionUserVO.getLoginID().substring(0, 1) + BTSLUtil.padZeroesToLeft(Integer.toString(new SecureRandom().nextInt()), 4);
                    int updatecount = vomsVoucherWebDAO.updateVomsDecKeyUser(con, sessionUserVO.getUserID(), decrypkey);
                    if (updatecount <= 0) {
                    	mcomCon.finalRollback();
                        throw new BTSLBaseException("voms.download.error.general", "loadvomsbatachfordownload");
                    } else {
                    	mcomCon.finalCommit();
                    }
                }
            } else {
                decrypkey = sessionUserVO.getLoginID().substring(0, 1) + BTSLUtil.padZeroesToLeft(Integer.toString(new SecureRandom().nextInt()), 4);
            }
			
			
			try{
				_fromDate = BTSLUtil.getDateFromDateString(fromDate);
			}catch(Exception e) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Date format" });
			}
			
			try{
				_toDate = BTSLUtil.getDateFromDateString(toDate);
			}catch(Exception e) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Date format" });
			}
			
			if(_fromDate.after(_toDate)) {
				throw new BTSLBaseException(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE);
			}
			
			Date currDate = new Date();
			
			if(_fromDate.after(currDate)) {
				throw new BTSLBaseException(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE);
			}
			
			if(_toDate.after(currDate)) {
				throw new BTSLBaseException(PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE);
			}
			
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue()) {
            	if (fromDate!=null && toDate!=null)
            	printlist = vomsBatcheswebDAO.getVomsPrinterBatchForUser(con, sessionUserVO, _fromDate, _toDate, batchType);
            else
            	printlist = vomsBatcheswebDAO.getVomsPrinterBatch(con, sessionUserVO, _fromDate, _toDate, batchType);
       		}
			
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, printlist);
			response.setParameters(new String[] {util.genEncDecKey(decrypkey)});
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				String unauthorised = Integer.toString(HttpStatus.SC_UNAUTHORIZED);

				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setResponse(HttpStatus.SC_UNAUTHORIZED, false, unauthorised);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);

			}

			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), be.getMessage(), be.getArgs());
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);
			
			return response;
		} catch (Exception e) {
			log.debug(methodName, "In catch block");
			log.error(methodName, "Exception:e=" + e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setResponse(PretupsI.RESPONSE_FAIL, false, "Error while getting user details");
			return response;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherInfoServices#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}
		return response;
	}
	
	
	/**
	 * 
	 * @param voucherInfo
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	@GetMapping(value = "/getVoucherDownloadFile", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found") })
	@ApiOperation(value = "Download voucher file", response = PretupsResponse.class, authorizations = {
			@Authorization(value = "Authorization") }, notes= SwaggerAPIDescriptionI.VoucherBatchListDownload)
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getVoucherDownloadFile.summary}", description="${getVoucherDownloadFile.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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

	public FileDownloadResponse getVoucherDownloadFile(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "jarFlag",  required = true) @RequestParam("jarFlag") String jarFlag,
			@Parameter(description = "batchNo",  required = true) @RequestParam("batchNo") String batchNo,
			@Parameter(description = "decKey",  required = true) @RequestParam("decKey") String decKey,
			HttpServletResponse response)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "getVoucherBatchList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();

		//ActionForward forward = null;
        UserVO sessionUserVO = null;
        String decrypkey = null;
        VomsVoucherWebDAO vomsVoucherWebDAO = null;
        final VomsUtil util;
        VomsBatchesWebDAO vomsBatcheswebDAO = null;
        ArrayList<VomsPrintBatchVO> printlist = null;
        UserDAO userDAO = null;
        VomsPrintBatchVO vomsPrintBatchVO = null;
        String filePath = null;
        String fileName = null;
        PreparedStatement preparedStatement = null;
        VomsBatchesWebDAO batcheswebDao = null;
        Date date = new Date();
        
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());

			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers);
			
			if(batchNo==null) {
            	throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Batch ID" });
            }
			if(decKey==null) {
            	throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Decryption key" });
            }
			if(jarFlag==null) {
            	throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Jar flag" });
            }
			
			if(jarFlag.equals("Y")) {
				fileName = Constants.getProperty("VOMS_DOWNLOAD_UTIL_NAME");
	            filePath = Constants.getProperty("VOMS_DOWNLOAD_UTIL_PATH");

	             File f  = new File(filePath + File.separator + fileName);
	             String fileData = new String(Base64.getEncoder().encode(FileUtils.readFileToByteArray(f)));
	             fileDownloadResponse.setFileName(fileName);
	 			 fileDownloadResponse.setFileType("jar");
	 			 fileDownloadResponse.setFileattachment(fileData);
	 			 fileDownloadResponse.setStatus(200);
				fileDownloadResponse.setStatus(200);
				return fileDownloadResponse;
			}else if(!jarFlag.equals("N")) {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Jar flag" });
			}
			
			userDAO = new UserDAO();
			sessionUserVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUserData.getData().getLoginid());
			util = new VomsUtil();
			
			//getting data for corresponding batchNo
			vomsBatcheswebDAO = new VomsBatchesWebDAO();
            vomsPrintBatchVO = vomsBatcheswebDAO.getVomsPrinterBatchByBatchID(con, sessionUserVO, "ALL", batchNo);
            
            if(vomsPrintBatchVO==null) {
            	throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Batch ID" });
            }
			
            //setting file name
            VomsProductDAO vomsProductDAO = new VomsProductDAO();
			StringBuilder sb = new StringBuilder();
            sb.append(Constants.getProperty("VOMS_DOWNLOAD_FILE_NAME"));
            sb.append("_");
            sb.append(sessionUserVO.getNetworkID());
            vomsPrintBatchVO.setNetwork(sessionUserVO.getNetworkID());
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue()) {
            	String type = vomsProductDAO.getTypeFromVoucherType(con, vomsPrintBatchVO.getVoucherType());
            	vomsPrintBatchVO.setVoucherName(vomsProductDAO.getNameFromVoucherType(con, vomsPrintBatchVO.getVoucherType()));
            	sb.append("_");
            	sb.append(type);
            }
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue()) {
            	sb.append("_");
            	sb.append(vomsPrintBatchVO.getVoucherSegment());
            }
            sb.append("_");
            sb.append(BTSLUtil.getFileNameStringFromDate(new Date()));
            sb.append(".csv");
            fileName = sb.toString();
            filePath = Constants.getProperty("VOMS_DOWNLOAD_FILE_PATH");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error(methodName, "Exception" + e.getMessage());
                throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchFOC");
            }
            
            //load voucher data and load to file
            vomsVoucherWebDAO = new VomsVoucherWebDAO();
            
            final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(Math.toIntExact(Long.parseLong(Constants.getProperty("VOMS_DOWNLOAD_QUEUE_SIZE"))));
            
            con.setAutoCommit(false);
            String tablename = null;
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                final boolean matchFound = BTSLUtil.validateTableName(vomsPrintBatchVO.getVoucherType());
                if (!matchFound) {
                    throw new BTSLBaseException(this, "download", "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + vomsPrintBatchVO.getVoucherType() + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }
            
            VomsVoucherWebQry vomsVoucherWebQry = (VomsVoucherWebQry)ObjectProducer.getObject(QueryConstants.VOMS_VOUCHER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
            
            final String sqlSelectBuf = vomsVoucherWebQry.selectVouchersOnSaleNo(tablename);
            
            preparedStatement = con.prepareStatement(sqlSelectBuf, ResultSet.TYPE_FORWARD_ONLY,
   				ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(Math.toIntExact(Integer.parseInt(Constants.getProperty("VOMS_DOWNLOAD_FETCH_SIZE"))));
   		
            preparedStatement.setLong(1, Long.parseLong(vomsPrintBatchVO.getStartSerialNo()));
            preparedStatement.setLong(2, Long.parseLong(vomsPrintBatchVO.getEndSerialNo()));    		
            preparedStatement.setString(3, batchNo);
            
            MutableBoolean mutableBoolean = new MutableBoolean(false);
            MutableBoolean filewritten = new MutableBoolean(false);
            CsvWriter csvWriter = new CsvWriter(queue, filePath, fileName, mutableBoolean, filewritten, vomsPrintBatchVO);
            Reader reader = new Reader(queue, preparedStatement, mutableBoolean, decKey);
            Thread w = new Thread(csvWriter, "Writer Thread");
            Thread r = new Thread(reader, "Reader Thread");
            w.setUncaughtExceptionHandler((thread, exception)-> {
            	 log.debug("Exception occurred in ", thread.getName());
				 log.errorTrace(methodName, exception);
            });
            r.setUncaughtExceptionHandler((thread, exception)-> {
            	log.debug("Exception occurred in ", thread.getName());
				 log.errorTrace(methodName, exception);
           });
            
            log.debug("Starting thread to strat writing on the file", w.getName());
            long startTime  = System.currentTimeMillis();
            w.start();
            log.debug("Starting thread to strat reading on the file", r.getName());
            r.start();
            w.join();
            long endTime  = System.currentTimeMillis();
            log.debug("Total time in reading data from DB and wrting data on csv file ", (endTime - startTime) + "miliseconds");
            if (filewritten.getValue()) {
                // update voms print batches
                batcheswebDao = new VomsBatchesWebDAO();
                int count = 0;
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_USER_KEY_REQD))).booleanValue()) {
                    count = batcheswebDao.updateVomsPrintBatchstatus(con, sessionUserVO.getUserID(), null, date, batchNo, Long.parseLong(vomsPrintBatchVO.getStartSerialNo()), Long.parseLong(vomsPrintBatchVO.getEndSerialNo()));
                } else {
                    count = batcheswebDao.updateVomsPrintBatchstatus(con, sessionUserVO.getUserID(), new VomsUtil().getDecKey(decKey), date, batchNo, Long.parseLong(vomsPrintBatchVO.getStartSerialNo()), Long.parseLong(vomsPrintBatchVO.getEndSerialNo()));
                }
                if (count > 0) {
                	mcomCon.finalCommit();
                } else {
                	mcomCon.finalRollback();
                }
            }
			
            File file = new File(filePath + fileName);
            String fileData = new String(Base64.getEncoder().encode(FileUtils.readFileToByteArray(file)));
            
            fileDownloadResponse.setFileName(fileName);
			fileDownloadResponse.setFileType("jar");
			fileDownloadResponse.setFileattachment(fileData);
			fileDownloadResponse.setStatus(200);
			return fileDownloadResponse;
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), be.getMessage(), be.getArgs());
			fileDownloadResponse.setMessageCode(be.getMessage());
			fileDownloadResponse.setMessage(resmsg);
			return fileDownloadResponse;
		} catch (Exception ex) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
			log.errorTrace(methodName, ex);
			log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
			return fileDownloadResponse;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherInfoServices#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}
	}


}
