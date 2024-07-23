package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
import com.btsl.common.ErrorMap;
import com.btsl.common.FileWriteUtil;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.AddcommSummryDownloadReq;
import com.btsl.pretups.channel.transfer.businesslogic.AdditionalCommSummryC2SReqVO;
import com.btsl.pretups.channel.transfer.businesslogic.AdditionalCommissionSummryC2SResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtlnCommSummryDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddRptReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddRptReqVO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddStatusRptResp;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddStsDownlodReq;
import com.btsl.pretups.channel.transfer.businesslogic.BulkuserAddStsDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommissionReqVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransfercommDownloadReq;
import com.btsl.pretups.channel.transfer.businesslogic.C2CtransferCommisionResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransfercommDownloadReq;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.GetCommissionSlabReqVO;
import com.btsl.pretups.channel.transfer.businesslogic.GetCommissionSlabResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAckDTO;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAckDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAcknowledgeResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetParentOwnerProfileReq;
import com.btsl.pretups.channel.transfer.businesslogic.LookUpListResp;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldReportDTO;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldReportReq;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldRptResp;
import com.btsl.pretups.channel.transfer.businesslogic.LowThresholdDownloadReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.LowThresholdDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.LowthresholdDownloadReq;
import com.btsl.pretups.channel.transfer.businesslogic.O2CACknowledgePDFResponse;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransfAckDownloadReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferAckDownloadReq;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferDetDownloadReq;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferDetailDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferDetailsReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferdetailsSearchReqVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CtransferDetSearchResp;
import com.btsl.pretups.channel.transfer.businesslogic.OfflineFileDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.OfflineReportActionReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.OfflineReportActionResp;
import com.btsl.pretups.channel.transfer.businesslogic.PBDownloadReqdata;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookDownloadReq;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchInfoRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchInfoResponse;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchOthersRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistDownloadReq;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistSearchRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistSearchReqVO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistoryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistorySearchResp;
import com.btsl.pretups.channel.transfer.businesslogic.UserNameAutoSearchReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.UserStaffDetailsReqDTO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ChildUserVO;
import com.btsl.pretups.user.businesslogic.OfflineReportTaskIDInfo;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.OfflineReportDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.c2s.services.LowThreshHoldRptService;
import com.restapi.c2s.services.O2CTransferAckwledgePDFReportGen;
import com.restapi.c2s.services.PassBookDownloadService;
import com.restapi.c2s.services.PretupsUIReportsServiceI;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.ViewTxnDetailsResponseVO;
import com.txn.pretups.channel.transfer.businesslogic.CommonReportWriter;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${PretupsUIReportsController.name}", description = "${PretupsUIReportsController.desc}")//@Api(tags = "Pretups UI reports", defaultValue = "Pretups UI Reports")
@RestController
@RequestMapping(value = "/v1/pretupsUIReports")
public class PretupsUIReportsController {
//	protected final Log _log = LogFactory.getLog(getClass().getName());
public static final Log log = LogFactory.getLog(PretupsUIReportsController.class.getName());
	StringBuilder loggerValue = new StringBuilder();

	@Autowired
	private PretupsUIReportsServiceI pretupsUIReportsServiceI;

	@Autowired
	private PassBookDownloadService passBookDownloadService;
	
	@Autowired
	private LowThreshHoldRptService lowThreshHoldRptService;
	
	@Autowired
	private O2CTransferAckwledgePDFReportGen o2CTransferAckwledgePDFReportGen;
	
	
//	@Autowired
//	private PretupsServiceFactory PretupsServiceFactory;

	@PostMapping(value = "/passbookSearch", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Passbook search details", response = PassbookSearchInfoResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PassbookSearchInfoResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${passbookSearch.summary}", description="${passbookSearch.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PassbookSearchInfoResponse.class))
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




	public PassbookSearchInfoResponse passbookSearch(
			@RequestBody PassbookSearchInfoRequestVO passbookSearchInfoRequestVO,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "passbookSearch";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		PassbookSearchInfoResponse response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new PassbookSearchInfoResponse();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = passbookSearchInfoRequestVO.getData().getFromDate();
			String toDate = passbookSearchInfoRequestVO.getData().getToDate();
			String ExtNgCode = passbookSearchInfoRequestVO.getData().getExtnwcode();
			String productCode = passbookSearchInfoRequestVO.getData().getProductCode();

			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			
			

			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}

			if (BTSLUtil.isNullString(ExtNgCode) || !ExtNgCode.equals(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}

			pretupsUIReportsServiceI.getPassBookSearchInfo(msisdn, passbookSearchInfoRequestVO, locale, response) ;
			

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#passbookSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
	}

	@PostMapping(value = "/passbookDownload", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Passbook download", response = PassbookDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PassbookDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${passbookDownload.summary}", description="${passbookDownload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PassbookDownloadResp.class))
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




	public PassbookDownloadResp passbookDownload(@RequestBody PassbookDownloadReq passbookDownloadReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "passbookDownload";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		PassbookDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new PassbookDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = passbookDownloadReq.getData().getFromDate();
			String toDate = passbookDownloadReq.getData().getToDate();
			String extNtCode = passbookDownloadReq.getData().getExtnwcode();
			String productCode = passbookDownloadReq.getData().getProductCode();
			String fileType = passbookDownloadReq.getData().getFileType();
			List<DispHeaderColumn> listDispHeaderColumn = passbookDownloadReq.getData().getDispHeaderColumnList();

			PBDownloadReqdata pbDownloadReqdata = new PBDownloadReqdata();

			pbDownloadReqdata.setDispHeaderColumnList(listDispHeaderColumn);
			pbDownloadReqdata.setFromDate(fromDate);
			pbDownloadReqdata.setToDate(toDate);
			pbDownloadReqdata.setExtnwcode(extNtCode);
			pbDownloadReqdata.setProductCode(productCode);
			String allowedFileType = fileType;
			if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			pbDownloadReqdata.setFileType(allowedFileType);

			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));

			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}

			if (BTSLUtil.isNullString(extNtCode) || !extNtCode.equals(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
			pbDownloadReqdata.setMsisdn(msisdn);
			pbDownloadReqdata.setLocale(locale);
			//PassBookDownloadService passBookDownloadService =(PassBookDownloadService) PretupsServiceFactory.getPretupsServiceObject(PassBookDownloadService.class);
			passBookDownloadService.execute(pbDownloadReqdata, response);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessage(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#passbookSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
	}

	
	
	@PostMapping(value = "/lowthreshHoldRpt", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Low thresh hold report", response = LowThreshHoldRptResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LowThreshHoldRptResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/public LowThreshHoldRptResp lowthreshHoldRpt(@RequestBody LowThreshHoldReportReq lowThreshHoldReportReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "lowthreshHoldRpt";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		LowThreshHoldRptResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new LowThreshHoldRptResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = lowThreshHoldReportReq.getData().getFromDate();
			String toDate = lowThreshHoldReportReq.getData().getToDate();
			String extNtCode = lowThreshHoldReportReq.getData().getExtnwcode();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));

			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("HomeScreenTransactionCntroller", "lowthresholdsearch",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}

			if (BTSLUtil.isNullString(extNtCode) || !extNtCode.equals(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException("HomeScreenTransactionController", "lowthresholdsearch",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
		
			LowThreshHoldReportDTO lowThreshHoldReportDTO = new LowThreshHoldReportDTO();
			lowThreshHoldReportDTO.setFromDate(fromDate);
			lowThreshHoldReportDTO.setToDate(toDate);
			lowThreshHoldReportDTO.setExtnwcode(extNtCode);
			lowThreshHoldReportDTO.setCategory(lowThreshHoldReportReq.getData().getCategory());
			lowThreshHoldReportDTO.setDomain(lowThreshHoldReportReq.getData().getDomain());
			lowThreshHoldReportDTO.setGeography(lowThreshHoldReportReq.getData().getGeography());
			lowThreshHoldReportDTO.setMsisdn(msisdn);
			lowThreshHoldReportDTO.setLocale(locale);
			lowThreshHoldReportDTO.setThreshhold(lowThreshHoldReportReq.getData().getThreshhold());
			
			
			lowThreshHoldRptService.getLowThreshHoldReport(lowThreshHoldReportDTO, response);		
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#passbookSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
	}
	
	
	@PostMapping(value = "/lowThresholdDownload", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Low threshold download", response = LowThresholdDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = LowThresholdDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${lowThresholdDownload.summary}", description="${lowThresholdDownload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LowThresholdDownloadResp.class))
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



	public LowThresholdDownloadResp lowThresholdDownload(@RequestBody LowthresholdDownloadReq lowthresholdDownloadReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "lowThresholdDownload";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		LowThresholdDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new LowThresholdDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = lowthresholdDownloadReq.getData().getFromDate();
			String toDate = lowthresholdDownloadReq.getData().getToDate();
			String extNtCode = lowthresholdDownloadReq.getData().getExtnwcode();
			String categoryCode = lowthresholdDownloadReq.getData().getCategory();
			String domain = lowthresholdDownloadReq.getData().getDomain();
			String threshold =lowthresholdDownloadReq.getData().getThreshhold();
			String geography =     lowthresholdDownloadReq.getData().getGeography();
			String fileType = lowthresholdDownloadReq.getData().getFileType();
			List<DispHeaderColumn> listDispHeaderColumn = lowthresholdDownloadReq.getData().getDispHeaderColumnList();

			LowThresholdDownloadReqDTO lowthresholdDTO = new LowThresholdDownloadReqDTO();

			lowthresholdDTO.setDispHeaderColumnList(listDispHeaderColumn);
			lowthresholdDTO.setFromDate(fromDate);
			lowthresholdDTO.setToDate(toDate);
			lowthresholdDTO.setExtnwcode(extNtCode);
			lowthresholdDTO.setDomain(domain);
			lowthresholdDTO.setCategoryCode(categoryCode);
			lowthresholdDTO.setThreshold(threshold);
			lowthresholdDTO.setGeography(geography);
			String allowedFileType = fileType;
			if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			lowthresholdDTO.setFileType(allowedFileType);

			lowthresholdDTO.setMsisdn(msisdn);
			lowthresholdDTO.setLocale(locale);
			
			
			LowThreshHoldReportDTO lowThreshHoldReportDTO = new LowThreshHoldReportDTO();
			lowThreshHoldReportDTO.setFromDate(fromDate);
			lowThreshHoldReportDTO.setToDate(toDate);
			lowThreshHoldReportDTO.setExtnwcode(extNtCode);
			lowThreshHoldReportDTO.setCategory(lowthresholdDownloadReq.getData().getCategory());
			lowThreshHoldReportDTO.setDomain(lowthresholdDownloadReq.getData().getDomain());
			lowThreshHoldReportDTO.setGeography(lowthresholdDownloadReq.getData().getGeography());
			lowThreshHoldReportDTO.setMsisdn(msisdn);
			lowThreshHoldReportDTO.setLocale(locale);
			lowThreshHoldReportDTO.setThreshhold(lowthresholdDownloadReq.getData().getThreshhold());
			//lowThreshHoldRptService.validateInputs(con, lowThreshHoldReportDTO,channelUserVO.getUserName());
			
			//PassBookDownloadService passBookDownloadService =(PassBookDownloadService) PretupsServiceFactory.getPretupsServiceObject(PassBookDownloadService.class);
			lowThreshHoldRptService.execute(lowthresholdDTO, response);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#lowthresholddownload");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
	}


	@PostMapping(value = "/pinPassHistorySearch", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "pin password history search", response = PinPassHistorySearchResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PinPassHistorySearchResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${pinPassHistorySearch.summary}", description="${pinPassHistorySearch.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PinPassHistorySearchResp.class))
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



	public PinPassHistorySearchResp pinPassHistorySearch(@RequestBody PinPassHistSearchReqVO pinPassHistSearchReqVO,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "pinPassHistorySearch";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		PinPassHistorySearchResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new PinPassHistorySearchResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			UserDAO userDAO = new UserDAO();
			UserVO userVO = new UserVO();

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			String loginId = oAuthUser.getData().getLoginid();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);

			String fromDate = pinPassHistSearchReqVO.getData().getFromDate();
			String toDate = pinPassHistSearchReqVO.getData().getToDate();
			String extNtCode = pinPassHistSearchReqVO.getData().getExtnwcode();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			PinPassHistoryReqDTO  pinPassHistoryReqDTO = new PinPassHistoryReqDTO();
			pinPassHistoryReqDTO.setFromDate(fromDate);
			pinPassHistoryReqDTO.setToDate(toDate);
			pinPassHistoryReqDTO.setExtnwcode(extNtCode);
			pinPassHistoryReqDTO.setMsisdn(msisdn);
			pinPassHistoryReqDTO.setUserId(loginId);
			pinPassHistoryReqDTO.setDomain(pinPassHistSearchReqVO.getData().getDomain());
			pinPassHistoryReqDTO.setUserType(pinPassHistSearchReqVO.getData().getUserType());
			pinPassHistoryReqDTO.setReqType(pinPassHistSearchReqVO.getData().getReqType());
			pinPassHistoryReqDTO.setCategoryCode(pinPassHistSearchReqVO.getData().getCategoryCode());
//			pinPassHistoryReqDTO.setGeography(userVO.getGeographicalCode());
			List<PinPassHistSearchRecordVO> listPinPassHistSearchRecordVO =pretupsUIReportsServiceI.getPinPasshistSearchInfo(pinPassHistoryReqDTO, response);
			response.setPinPassHistSearchVOList(listPinPassHistSearchRecordVO);
			
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#passbookSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
	}

	@PostMapping(value = "/pinPassHistDownload", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Pin Password History download", response = PinPassHistDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PinPassHistDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${pinPassHistDownload.summary}", description="${pinPassHistDownload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PinPassHistDownloadResp.class))
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



	public PinPassHistDownloadResp pinPassHistDownload(@RequestBody PinPassHistDownloadReq pinPassHistDownloadReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "pinPassHistDownload";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		PinPassHistDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new PinPassHistDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = pinPassHistDownloadReq.getData().getFromDate();
			String toDate = pinPassHistDownloadReq.getData().getToDate();
			String extNtCode = pinPassHistDownloadReq.getData().getExtnwcode();
			String categoryCode = pinPassHistDownloadReq.getData().getCategoryCode();
			String domain = pinPassHistDownloadReq.getData().getDomain();
			String userType =pinPassHistDownloadReq.getData().getUserType();
			String reqType = pinPassHistDownloadReq.getData().getReqType();
			String fileType = pinPassHistDownloadReq.getData().getFileType();
			
			List<DispHeaderColumn> listDispHeaderColumn = pinPassHistDownloadReq.getData().getDispHeaderColumnList();
			PinPassHistoryReqDTO pinPassHistoryReqDTO = new PinPassHistoryReqDTO();

			pinPassHistoryReqDTO.setDispHeaderColumnList(listDispHeaderColumn);
			pinPassHistoryReqDTO.setFromDate(fromDate);
			pinPassHistoryReqDTO.setToDate(toDate);
			pinPassHistoryReqDTO.setExtnwcode(extNtCode);
			pinPassHistoryReqDTO.setDomain(domain);
			pinPassHistoryReqDTO.setCategoryCode(categoryCode);
			pinPassHistoryReqDTO.setReqType(reqType);
			pinPassHistoryReqDTO.setUserType(userType);
			pinPassHistoryReqDTO.setLocale(locale);
			
			String allowedFileType = fileType;
			if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			pinPassHistoryReqDTO.setFileType(allowedFileType);

			pinPassHistoryReqDTO.setMsisdn(msisdn);
			pinPassHistoryReqDTO.setGeography(channelUserVO.getGeographicalCode());
			pretupsUIReportsServiceI.downloadPinPassHistData(pinPassHistoryReqDTO, response);
			
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

			
			

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			//response.setMessageCode(be.getErrorCode());
			response.setMessage(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#lowthresholddownload");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
	}

	/*
	@PostMapping(value = "/c2StransferCommissionRpt", produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Channel to transfer commission  report", response = C2StransferCommisionResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2StransferCommisionResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	public C2StransferCommisionResp c2StransferCommissionRpt(@RequestBody C2STransferCommissionReqVO c2STransferCommissionReqVO,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "c2StransferCommissionRpt";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		C2StransferCommisionResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new C2StransferCommisionResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = c2STransferCommissionReqVO.getData().getReportDate() + " " +c2STransferCommissionReqVO.getData().getAllowedTimeFrom() + ":00";
			String toDate = c2STransferCommissionReqVO.getData().getReportDate() + " " +c2STransferCommissionReqVO.getData().getAllowedTimeTo() + ":59";
			String extNtCode = c2STransferCommissionReqVO.getData().getExtnwcode();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT));

			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
				
			} catch (Exception be) {
				throw new BTSLBaseException("HomeScreenTransactionCntroller", "passbookSearch",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}

			if (BTSLUtil.isNullString(extNtCode) || !extNtCode.equals(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException("HomeScreenTransactionController", "passbookSearch",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
		
			C2STransferCommReqDTO  c2STransferCommReqDTO =  commonC2SInputAssignments(c2STransferCommissionReqVO,channelUserVO);
			c2STransferCommReqDTO.setFromDate(fromDate);
			c2STransferCommReqDTO.setToDate(toDate);
			c2STransferCommReqDTO.setExtnwcode(extNtCode);
			c2STransferCommReqDTO.setMsisdn(msisdn);
			c2STransferCommReqDTO.setService(c2STransferCommissionReqVO.getData().getService());
			c2STransferCommReqDTO.setTransStatus(c2STransferCommissionReqVO.getData().getTransStatus());
			c2STransferCommReqDTO.setLocale(locale);
           pretupsUIReportsServiceI.getC2StransferCommissionInfo(c2STransferCommReqDTO, response);

			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#passbookSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
	} */

	
	@PostMapping(value = "/c2StransferCommissionRptDownload", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Channel to transfer commission  report download", notes = ("Api Info:") + ("\n")
			+ ("1. Domain , Category and Geography in Advanced tab .") + ("\n")
			+ ("2. Service type is applicable for both Tabs.") + ("\n")
			+ ("3. Transfer status is applicable for both Tabs.") + ("\n")
			+ ("4. Time range is applicable for both Tabs. ") + ("\n")
			+ ("5. User type is applicable for both Tabs. It should contain either STAFF/CHANNEL  ") + ("\n")
			+ ("6. Mobile number is required only for MobileNumber tab,Its channel Users mobile number.") + ("\n")
			+ ("6. Channel User  is requried only for Advanced tab,It should contain channel User userID while sending to API.") + ("\n")
			+ ("7. IF UserType is selected as STAFF,Then dropdown 'search Staff by' should contain 'Login ID'/'MSISDN' .") + ("\n")
			+ ("8. IF UserType is selected as CHANNEL,Then hide dropdown 'search Staff by'  and textbox 'Loginid/Msisdn' ") + ("\n"), response = C2STransferCommDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2STransferCommDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2StransferCommissionRptDownload.summary}", description="${c2StransferCommissionRptDownload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResponseEntity.class))
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



	public ResponseEntity<?> c2StransferCommissionRptDownload(@RequestBody C2STransfercommDownloadReq c2STransfercommDownloadReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		Instant start = Instant.now();
		Instant end=null;
		boolean reportOffline=  (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_OFFLINE);
		long stopTime=0l;
		final String methodName = "c2StransferCommissionRptDownload";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		C2STransferCommDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		Map<String,String> mp=null; 
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new C2STransferCommDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = c2STransfercommDownloadReq.getData().getReportDate() + " " +c2STransfercommDownloadReq.getData().getAllowedTimeFrom() + ":00";
			String toDate = c2STransfercommDownloadReq.getData().getReportDate() + " " +c2STransfercommDownloadReq.getData().getAllowedTimeTo() + ":59";
			
			String extNtCode = c2STransfercommDownloadReq.getData().getNetworkCode();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT));

			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("HomeScreenTransactionCntroller", "passbookSearch",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}

			if (BTSLUtil.isNullString(extNtCode) || !extNtCode.equals(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException("HomeScreenTransactionController", "passbookSearch",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
		
			
			C2STransferCommReqDTO  c2STransferCommReqDTO =  commonC2SInputAssignments(con,c2STransfercommDownloadReq,channelUserVO);
			c2STransferCommReqDTO.setUserId(channelUserVO.getUserID());
			c2STransferCommReqDTO.setDispHeaderColumnList(c2STransfercommDownloadReq.getData().getDispHeaderColumnList());
			c2STransferCommReqDTO.setLocale(locale);
			String fileType = c2STransfercommDownloadReq.getData().getFileType();
			String allowedFileType = fileType;
			if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			c2STransferCommReqDTO.setFileType(allowedFileType);
			c2STransferCommReqDTO.setMsisdn(msisdn);
        mp =    pretupsUIReportsServiceI.downloadC2StransferCommData(c2STransferCommReqDTO, response);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			String resmsg=null;
			
	           if(!reportOffline) {
	        	   if(response.getMessageCode().equals(PretupsErrorCodesI.SUCCESS)) {
				   resmsg = RestAPIStringParser.getMessage(c2STransferCommReqDTO.getLocale(),
	   					PretupsErrorCodesI.SUCCESS, null);
	        	   }else {
	        		   resmsg=response.getMessage();  
	        	   }
	           }else {
			 resmsg = RestAPIStringParser.getMessage(c2STransferCommReqDTO.getLocale(),
					PretupsErrorCodesI.OFFLINERPT_PROCESS_INITIATED, null);
	           }
			response.setMessage(resmsg); 
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(be.getMessage());
			response.setMessage(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#passbookSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}
		
		
		
		
        if(!reportOffline && (mp!=null && mp.get(PretupsRptUIConsts.ONLINE_FILE_PATH_KEY.getReportValues())!=null) ) {
        	String filepath =mp.get(PretupsRptUIConsts.ONLINE_FILE_PATH_KEY.getReportValues());
        	if(response.getStatus().equals(String.valueOf(HttpServletResponse.SC_OK))){
        		InputStream is;
        		InputStreamResource onlineFileResource=null;
        		HttpHeaders responseheaderdata=null;
				
        		boolean errorOccuredOnlineDownload =false;
				try {
					is = Files.newInputStream(Paths.get(filepath),StandardOpenOption.DELETE_ON_CLOSE);
		     		 onlineFileResource = new InputStreamResource(is);
		     		 responseheaderdata = CommonReportWriter.setDownloadFileHeaders(filepath);
     			} catch (IOException e) {
     				errorOccuredOnlineDownload=true;
				}finally {
					end= Instant.now();
					log.debug(methodName, "Execution time of C2S transfer commission service is ::: " + Duration.between(start, end));
				}
				
				if(!errorOccuredOnlineDownload) {
					
					return ResponseEntity.ok().headers(responseheaderdata).body(onlineFileResource);  // offline initia
				}else {
					String onlineDwldfail = Integer.toString(PretupsI.RESPONSE_FAIL);
					response.setStatus(onlineDwldfail);
					response.setMessageCode(PretupsErrorCodesI.FAILED);
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.FAILED, null);
					return ResponseEntity.ok().body(response);  // offline initia
				}
				
        	}else {
        		return ResponseEntity.ok().body(response);  // offline initia
        	}
        			
		} else { // offline scenario
			end= Instant.now();
			log.debug(methodName, "Execution time of C2S transfer commission service is ::: " + Duration.between(start, end));
			return ResponseEntity.ok().body(response);  // offline initia
		}
         
         
		
	} 

	@PostMapping(value = "/c2ctransferCommissionRpt", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Channel to Channel transfer commission  report", response = C2CtransferCommisionResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2CtransferCommisionResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2ctransferCommissionRpt.summary}", description="${c2ctransferCommissionRpt.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2CtransferCommisionResp.class))
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



	public C2CtransferCommisionResp c2ctransferCommissionRpt(@RequestBody C2CTransferCommissionReqVO c2CTransferCommissionReqVO,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "c2ctransferCommissionRpt";
		

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		C2CtransferCommisionResp response = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		ChannelUserDAO channelUserDAO=null;
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new C2CtransferCommisionResp();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO = new ChannelUserDAO();
			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
			String fromDate = c2CTransferCommissionReqVO.getData().getFromDate();
			String toDate = c2CTransferCommissionReqVO.getData().getToDate();
			String extNtCode = c2CTransferCommissionReqVO.getData().getExtnwcode();
			//DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			C2CTransferCommReqDTO  c2CTransferCommReqDTO = new C2CTransferCommReqDTO();
			c2CTransferCommReqDTO.setTransferInout(c2CTransferCommissionReqVO.getData().getTransferInout());
			c2CTransferCommReqDTO.setTransferSubType(c2CTransferCommissionReqVO.getData().getTransferSubType());
			c2CTransferCommReqDTO.setFromDate(fromDate);
			c2CTransferCommReqDTO.setToDate(toDate);
			c2CTransferCommReqDTO.setSenderMobileNumber(c2CTransferCommissionReqVO.getData().getSenderMobileNumber());
			c2CTransferCommReqDTO.setReceiverMobileNumber(c2CTransferCommissionReqVO.getData().getReceiverMobileNumber());
			c2CTransferCommReqDTO.setExtnwcode(extNtCode);
			c2CTransferCommReqDTO.setMsisdn(msisdn);
			c2CTransferCommReqDTO.setDomain(c2CTransferCommissionReqVO.getData().getDomain());
			c2CTransferCommReqDTO.setCategoryCode(c2CTransferCommissionReqVO.getData().getCategoryCode());
			c2CTransferCommReqDTO.setGeography(c2CTransferCommissionReqVO.getData().getGeography());
			c2CTransferCommReqDTO.setUser(c2CTransferCommissionReqVO.getData().getUser());
			c2CTransferCommReqDTO.setTransferUser(c2CTransferCommissionReqVO.getData().getTransferUser());
			c2CTransferCommReqDTO.setTransferCategory(c2CTransferCommissionReqVO.getData().getTransferCategory());
			c2CTransferCommReqDTO.setLocale(locale);
			c2CTransferCommReqDTO.setIncludeStaffUserDetails(c2CTransferCommissionReqVO.getData().getIncludeStaffDetails());
			c2CTransferCommReqDTO.setDistributionType(c2CTransferCommissionReqVO.getData().getDistributionType());
			c2CTransferCommReqDTO.setUser(c2CTransferCommissionReqVO.getData().getUser());
			c2CTransferCommReqDTO.setTransferUserCategory(c2CTransferCommissionReqVO.getData().getTransferUserCategory());
			
			
			
			if(!c2CTransferCommissionReqVO.getData().getReqTab().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ ) && !c2CTransferCommissionReqVO.getData().getReqTab().equals(PretupsI.C2C_ADVANCED_TAB_REQ)  ){
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVAID_C2C_TAB_REQ, 0, null);
			}
			
			if(c2CTransferCommReqDTO.getSenderMobileNumber()!= null && c2CTransferCommReqDTO.getSenderMobileNumber().trim().toUpperCase().equals(PretupsI.ALL)) {
			    c2CTransferCommReqDTO.setSenderMobileNumber("");
			}   
			if(c2CTransferCommReqDTO.getReceiverMobileNumber()!= null && c2CTransferCommReqDTO.getReceiverMobileNumber().trim().toUpperCase().equals(PretupsI.ALL)) {
				c2CTransferCommReqDTO.setReceiverMobileNumber("");
			}
			if(c2CTransferCommissionReqVO.getData().getReqTab().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ)){
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getDomain())){
					c2CTransferCommReqDTO.setDomain(channelUserVO.getDomainID());
				}
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getCategoryCode()) || (c2CTransferCommReqDTO.getCategoryCode()!=null && c2CTransferCommReqDTO.getCategoryCode().trim().toUpperCase().equals(PretupsI.ALL))) {
					c2CTransferCommReqDTO.setCategoryCode(PretupsI.ALL);
				}else {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EMPTY_MOBILE_TAB_CATEGORY, 0, null);
				}
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getGeography()) ||(c2CTransferCommReqDTO.getGeography()!=null && c2CTransferCommReqDTO.getGeography().trim().toUpperCase().equals(PretupsI.ALL))) {
					c2CTransferCommReqDTO.setGeography(PretupsI.ALL);
				}else {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EMPTY_MOBILE_TAB_GEOGRAPHY, 0, null);
				}
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getUser())  ||(c2CTransferCommReqDTO.getUser()!=null && c2CTransferCommReqDTO.getUser().trim().toUpperCase().equals(PretupsI.ALL))) {
				c2CTransferCommReqDTO.setUser(PretupsI.ALL);	
				}else {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EMPTY_MOBILE_TAB_USER, 0, null);
				}
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getTransferUser())  ||(c2CTransferCommReqDTO.getTransferUser()!=null && c2CTransferCommReqDTO.getTransferUser().trim().toUpperCase().equals(PretupsI.ALL)) ) {
					c2CTransferCommReqDTO.setTransferUser(PretupsI.ALL);
				}else {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EMPTY_MOBILE_TAB_TRANSFERUSER, 0, null);
				}
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getTransferCategory())  || (c2CTransferCommReqDTO.getTransferCategory()!=null && c2CTransferCommReqDTO.getTransferCategory().trim().toUpperCase().equals(PretupsI.ALL)) ) {
					c2CTransferCommReqDTO.setTransferCategory(PretupsI.ALL);
				}	
//				else {
//					throw new BTSLBaseException("PretupsUIReportsController", methodName,
//							PretupsErrorCodesI.EMPTY_MOBILE_TAB_TRANSFER_CATEGORY, 0, null);
//				}
				
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getTransferUserCategory())  || (c2CTransferCommReqDTO.getTransferUserCategory()!=null && c2CTransferCommReqDTO.getTransferUserCategory().trim().toUpperCase().equals(PretupsI.ALL)) ) {
					c2CTransferCommReqDTO.setTransferUserCategory(PretupsI.ALL);
				}else {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EMPTY_MOBILE_TAB_TRANSFERUSER_CATEGORY, 0, null);
				}
				
				c2CTransferCommReqDTO.setReqTab(PretupsI.C2C_MOBILENUMBER_TAB_REQ);
			}else {
				c2CTransferCommReqDTO.setReqTab(PretupsI.C2C_ADVANCED_TAB_REQ);
			}
			
			
			c2CTransferCommReqDTO.setIncludeStaffUserDetails(c2CTransferCommissionReqVO.getData().getIncludeStaffDetails());
			
           pretupsUIReportsServiceI.getC2CtransferCommissionInfo(c2CTransferCommReqDTO, response);

			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#c2ctransferCommission");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
	} 

	
	@PostMapping(value = "/c2CtransferCommissionRptDownload", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Channel to Channel commission  report download", response = C2CTransferCommDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2CTransferCommDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${c2CtransferCommissionRptDownload.summary}", description="${c2CtransferCommissionRptDownload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResponseEntity.class))
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



	public ResponseEntity<?> c2CtransferCommissionRptDownload(@RequestBody C2CTransfercommDownloadReq c2CTransfercommDownloadReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "c2CtransferCommissionRptDownload";
		Instant start = Instant.now();
		Instant end=null;
		boolean reportOffline=  (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_OFFLINE);
		long stopTime=0l;
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		C2CTransferCommDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		Map<String,String> mp = null;

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new C2CTransferCommDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = c2CTransfercommDownloadReq.getData().getFromDate();
			String toDate = c2CTransfercommDownloadReq.getData().getToDate();
			String extNtCode = c2CTransfercommDownloadReq.getData().getExtnwcode();
			//DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
		
			if(!c2CTransfercommDownloadReq.getData().getReqTab().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ ) && !c2CTransfercommDownloadReq.getData().getReqTab().equals(PretupsI.C2C_ADVANCED_TAB_REQ)  ){
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVAID_C2C_TAB_REQ, 0, null);
			}
			
			C2CTransferCommReqDTO  c2CTransferCommReqDTO = new C2CTransferCommReqDTO();
			
			
			c2CTransferCommReqDTO.setTransferInout(c2CTransfercommDownloadReq.getData().getTransferInout());
			c2CTransferCommReqDTO.setTransferSubType(c2CTransfercommDownloadReq.getData().getTransferSubType());
			c2CTransferCommReqDTO.setFromDate(fromDate);
			c2CTransferCommReqDTO.setToDate(toDate);
			c2CTransferCommReqDTO.setSenderMobileNumber(c2CTransfercommDownloadReq.getData().getSenderMobileNumber());
			c2CTransferCommReqDTO.setReceiverMobileNumber(c2CTransfercommDownloadReq.getData().getReceiverMobileNumber());
			c2CTransferCommReqDTO.setExtnwcode(extNtCode);
			c2CTransferCommReqDTO.setMsisdn(msisdn);
			c2CTransferCommReqDTO.setDomain(c2CTransfercommDownloadReq.getData().getDomain());
			c2CTransferCommReqDTO.setCategoryCode(c2CTransfercommDownloadReq.getData().getCategoryCode());
			c2CTransferCommReqDTO.setGeography(c2CTransfercommDownloadReq.getData().getGeography());
			c2CTransferCommReqDTO.setUser(c2CTransfercommDownloadReq.getData().getUser());
			c2CTransferCommReqDTO.setTransferUser(c2CTransfercommDownloadReq.getData().getTransferUser());
			c2CTransferCommReqDTO.setTransferCategory(c2CTransfercommDownloadReq.getData().getTransferCategory());
			c2CTransferCommReqDTO.setLocale(locale);
			c2CTransferCommReqDTO.setDistributionType(c2CTransfercommDownloadReq.getData().getDistributionType());
			c2CTransferCommReqDTO.setIncludeStaffUserDetails(c2CTransfercommDownloadReq.getData().getIncludeStaffDetails());
			c2CTransferCommReqDTO.setTransferUserCategory(c2CTransfercommDownloadReq.getData().getTransferUserCategory());
			c2CTransferCommReqDTO.setLocale(locale);
			c2CTransferCommReqDTO.setUserId(channelUserVO.getUserID());
			c2CTransferCommReqDTO.setDistributionType(c2CTransfercommDownloadReq.getData().getDistributionType());
			
			c2CTransferCommReqDTO.setUser(c2CTransfercommDownloadReq.getData().getUser());
			if(!c2CTransfercommDownloadReq.getData().getReqTab().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ ) && !c2CTransfercommDownloadReq.getData().getReqTab().equals(PretupsI.C2C_ADVANCED_TAB_REQ)  ){
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVAID_C2C_TAB_REQ, 0, null);
			}
			if(c2CTransferCommReqDTO.getSenderMobileNumber()!= null && c2CTransferCommReqDTO.getSenderMobileNumber().trim().toUpperCase().equals(PretupsI.ALL)) {
			    c2CTransferCommReqDTO.setSenderMobileNumber("");
			}   
			if(c2CTransferCommReqDTO.getReceiverMobileNumber()!= null && c2CTransferCommReqDTO.getReceiverMobileNumber().trim().toUpperCase().equals(PretupsI.ALL)) {
				c2CTransferCommReqDTO.setReceiverMobileNumber("");
			}

			if(c2CTransfercommDownloadReq.getData().getReqTab().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ)){
				
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getDomain()) ) {
					c2CTransferCommReqDTO.setDomain(channelUserVO.getDomainID());
				}
				
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getCategoryCode()) ) {
					c2CTransferCommReqDTO.setCategoryCode(PretupsI.ALL);
				}
				
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getGeography()) ||(c2CTransferCommReqDTO.getGeography()!=null && c2CTransferCommReqDTO.getGeography().trim().toUpperCase().equals(PretupsI.ALL))) {
					c2CTransferCommReqDTO.setGeography(PretupsI.ALL);
				}else {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EMPTY_MOBILE_TAB_GEOGRAPHY, 0, null);
				}
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getUser())  ||(c2CTransferCommReqDTO.getUser()!=null && c2CTransferCommReqDTO.getUser().trim().toUpperCase().equals(PretupsI.ALL))) {
				c2CTransferCommReqDTO.setUser(PretupsI.ALL);	
				}else {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EMPTY_MOBILE_TAB_USER, 0, null);
				}
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getTransferUser())  ||(c2CTransferCommReqDTO.getTransferUser()!=null && c2CTransferCommReqDTO.getTransferUser().trim().toUpperCase().equals(PretupsI.ALL)) ) {
					c2CTransferCommReqDTO.setTransferUser(PretupsI.ALL);
				}else {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EMPTY_MOBILE_TAB_TRANSFERUSER, 0, null);
				}
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getTransferCategory())  || (c2CTransferCommReqDTO.getTransferCategory()!=null && c2CTransferCommReqDTO.getTransferCategory().trim().toUpperCase().equals(PretupsI.ALL)) ) {
					c2CTransferCommReqDTO.setTransferCategory(PretupsI.ALL);
				}
				
				if(BTSLUtil.isNullorEmpty(c2CTransferCommReqDTO.getTransferUserCategory())  || (c2CTransferCommReqDTO.getTransferUserCategory()!=null && c2CTransferCommReqDTO.getTransferUserCategory().trim().toUpperCase().equals(PretupsI.ALL)) ) {
					c2CTransferCommReqDTO.setTransferUserCategory(PretupsI.ALL);
				}else {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EMPTY_MOBILE_TAB_TRANSFERUSER_CATEGORY, 0, null);
				}
				c2CTransferCommReqDTO.setReqTab(PretupsI.C2C_MOBILENUMBER_TAB_REQ);
			}else {
				c2CTransferCommReqDTO.setReqTab(PretupsI.C2C_ADVANCED_TAB_REQ);
			}
			c2CTransferCommReqDTO.setIncludeStaffUserDetails(c2CTransfercommDownloadReq.getData().getIncludeStaffDetails());
			c2CTransferCommReqDTO.setDispHeaderColumnList(c2CTransfercommDownloadReq.getData().getDispHeaderColumnList());
			String fileType = c2CTransfercommDownloadReq.getData().getFileType();
			String allowedFileType = fileType;
			if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			c2CTransferCommReqDTO.setFileType(allowedFileType);
			c2CTransferCommReqDTO.setMsisdn(msisdn);
	        
            mp =    pretupsUIReportsServiceI.downloadC2CtransferCommData(c2CTransferCommReqDTO, response);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			String resmsg=null;
			
	           if(!reportOffline) {
	        	   if(response.getMessageCode().equals(PretupsErrorCodesI.SUCCESS)) {
	        		   resmsg = RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
	   					PretupsErrorCodesI.SUCCESS, null);
	        	   }else {
	        		    resmsg=response.getMessage();
	       				String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
	       				response.setStatus(fail);
	        	   }
	           }else {
			 resmsg = RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
					PretupsErrorCodesI.OFFLINERPT_PROCESS_INITIATED, null);
	           }
			response.setMessage(resmsg);
            
            
		
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);
			

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#passbookSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}
		
		
		
		
		
        if(!reportOffline && (mp!=null && mp.get(PretupsRptUIConsts.ONLINE_FILE_PATH_KEY.getReportValues())!=null) ) {
        	String filepath =mp.get(PretupsRptUIConsts.ONLINE_FILE_PATH_KEY.getReportValues());
        	if(response.getStatus().equals(String.valueOf(HttpServletResponse.SC_OK))){
        		InputStream is;
        		InputStreamResource onlineFileResource=null;
        		HttpHeaders responseheaderdata=null;
        		boolean errorOccuredOnlineDownload =false;
				try {
					is = Files.newInputStream(Paths.get(filepath),StandardOpenOption.DELETE_ON_CLOSE);
		     		 onlineFileResource = new InputStreamResource(is);
		     		 responseheaderdata = CommonReportWriter.setDownloadFileHeaders(filepath);
     			} catch (IOException e) {
     				errorOccuredOnlineDownload=true;
				}finally {
					end= Instant.now();
					log.debug(methodName, "Execution time of C2S transfer commission service is ::: " + Duration.between(start, end));
				}
				
				if(!errorOccuredOnlineDownload) {
					
					return ResponseEntity.ok().headers(responseheaderdata).body(onlineFileResource);  // offline initia
				}else {
					String onlineDwldfail = Integer.toString(PretupsI.RESPONSE_FAIL);
					response.setStatus(onlineDwldfail);
					response.setMessageCode(PretupsErrorCodesI.FAILED);
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.FAILED, null);
					return ResponseEntity.ok().body(response);  // offline initia
				}
				
        	}else {
        		return ResponseEntity.ok().body(response);  // offline initia
        	}
        			
		} else { // offline scenario
			end= Instant.now();
			log.debug(methodName, "Execution time of C2S transfer commission service is ::: " + Duration.between(start, end));
			return ResponseEntity.ok().body(response);  // offline initia
		}



	
	} 

	
	
	
	@GetMapping(value= "/fetchUserNameAutoSearch", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Fetch username Autosearch",
	           response = FetchUserNameAutoSearchRespVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = FetchUserNameAutoSearchRespVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchUserNameAutoSearch.summary}", description="${fetchUserNameAutoSearch.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FetchUserNameAutoSearchRespVO.class))
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




	public FetchUserNameAutoSearchRespVO fetchUserNameAutoSearch(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.USER_NAME, example = "",required = true)
			@RequestParam("userName") String userName,
			@Parameter(description = SwaggerAPIDescriptionI.DOMAIN, example = "",required = true)
			@RequestParam("domainCode") String domainCode,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_PARENT_CATEGORY, example = "",required = true)
			@RequestParam("categoryCode") String categoryCode,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_PARENT_GEOGRAPHY, example = "",required = true)
			@RequestParam("geography") String  geography,
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "fetchUserNameAutoSearch";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		FetchUserNameAutoSearchRespVO response = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		
		
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new FetchUserNameAutoSearchRespVO();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			CommonUtil commonUtil= new CommonUtil();
			commonUtil.validateCategoryCode(categoryCode, con);
			commonUtil.validateDomain(domainCode, con);
			commonUtil.validateGeography(geography, con);
			
			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
			
			//response.setService("STAFFUSERDETAILSDOWNLOADRESP");
			UserNameAutoSearchReqDTO userNameAutoSearchReqDTO = new UserNameAutoSearchReqDTO();
			userNameAutoSearchReqDTO.setCategoryCode(categoryCode);
			
			userNameAutoSearchReqDTO.setUserName(userName);
			userNameAutoSearchReqDTO.setDomainCode(domainCode);
			
			userNameAutoSearchReqDTO.setGeography(geography);
			userNameAutoSearchReqDTO.setMsisdn(channelUserVO.getMsisdn());
			userNameAutoSearchReqDTO.setUserId(channelUserVO.getUserID());  //Loggedin Userid.
			pretupsUIReportsServiceI.getUserNameAutoSearchData(userNameAutoSearchReqDTO,response);
			
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#fetchUserNameAutoSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("fetchUserNameAutoSearch", " Exited ");
			}
		}

		return response;
	}

	
	
	@GetMapping(value= "/getParentandOwnerProfileInfo", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Get parent & owner Info",
	           response = GetParentOwnerProfileRespVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GetParentOwnerProfileRespVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getParentandOwnerProfileInfo.summary}", description="${getParentandOwnerProfileInfo.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetParentOwnerProfileRespVO.class))
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



	public GetParentOwnerProfileRespVO getParentandOwnerProfileInfo(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.USER_ID, example = "",required = true)
			@RequestParam("userId") String userId, HttpServletResponse responseSwag )throws Exception{
		
		final String methodName = "getParentandOwnerProfileInfo";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		GetParentOwnerProfileRespVO response = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		
		
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new GetParentOwnerProfileRespVO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			UserDAO userDAO = new UserDAO();
			GetParentOwnerProfileReq  getParentOwnerProfileReq = new GetParentOwnerProfileReq();
			getParentOwnerProfileReq.setUserId(userId);
		    pretupsUIReportsServiceI.getParentOwnerProfileInfo(getParentOwnerProfileReq,response);
		    String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#fetchUserNameAutoSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("fetchUserNameAutoSearch", " Exited ");
			}
		}

		return response;
	}

	
	
	@GetMapping(value= "/getCommissionSlabDetails", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Get commission slab details",
	           response = GetCommissionSlabResp.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GetCommissionSlabResp.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${getCommissionSlabDetails.summary}", description="${getCommissionSlabDetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetCommissionSlabResp.class))
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




	public GetCommissionSlabResp getCommissionSlabDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.LOGIN_ID, example = "",required = true)
			@RequestParam("userId") String userId,
			@Parameter(description = SwaggerAPIDescriptionI.DOMAIN, example = "",required = true)
			@RequestParam("domainCode") String domainCode,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_PARENT_CATEGORY, example = "",required = true)
			@RequestParam("categoryCode") String categoryCode,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_PARENT_GEOGRAPHY, example = "",required = true)
			@RequestParam("geography") String geography,	
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "getCommissionSlabDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		GetCommissionSlabResp response = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		
		
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new GetCommissionSlabResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			userDao= new UserDAO();

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			CommonUtil commonUtil= new CommonUtil();
			commonUtil.validateCategoryCode(categoryCode, con);
			commonUtil.validateDomain(domainCode, con);
			commonUtil.validateGeography(geography, con);	
			

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
			
			ChannelUserVO inputUserDetails =userDao.loadUserDetailsByLoginId(con,userId);
			 if(inputUserDetails==null) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID, 0, null); 
			 }
			
			//response.setService("STAFFUSERDETAILSDOWNLOADRESP");
			GetCommissionSlabReqVO  gsetCommissionSlabReqVO = new GetCommissionSlabReqVO();
			gsetCommissionSlabReqVO.setCategoryCode(categoryCode);
			gsetCommissionSlabReqVO.setUserId(inputUserDetails.getUserID());
			gsetCommissionSlabReqVO.setDomainCode(domainCode);
			gsetCommissionSlabReqVO.setGeography(geography);
			gsetCommissionSlabReqVO.setMsisdn(channelUserVO.getMsisdn());
			gsetCommissionSlabReqVO.setLoggedInUserID(channelUserVO.getUserID());
			pretupsUIReportsServiceI.getCommissionSlabDetails(gsetCommissionSlabReqVO,response);
			
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#fetchUserNameAutoSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("fetchUserNameAutoSearch", " Exited ");
			}
		}

		return response;
	}

	
	
	
	@GetMapping(value= "/getO2CTransferAcknowledgement", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Opertor to channel transfer acknowledgement details",
	           response = GetO2CTransferAcknowledgeResp.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GetO2CTransferAcknowledgeResp.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getO2CTransferAcknowledgement.summary}", description="${getO2CTransferAcknowledgement.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetO2CTransferAcknowledgeResp.class))
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



	public GetO2CTransferAcknowledgeResp getO2CTransferAcknowledgement(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.TRANSACTION_ID, example = "",required = true)
			@RequestParam("transactionID") String transactionID,
			@Parameter(description = SwaggerAPIDescriptionI.DISTRIBUTION_TYPE, example = "",required = true)
			@RequestParam("distributionType") String distributionType,
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "getO2CTransferAcknowledgement";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		GetO2CTransferAcknowledgeResp response = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		
		
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new GetO2CTransferAcknowledgeResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
			
			//response.setService("STAFFUSERDETAILSDOWNLOADRESP");
			/*
			GetO2CTransfAcknReqVO  getO2CTransfAcknReqVO = new GetO2CTransfAcknReqVO();
			getO2CTransfAcknReqVO.setDistributionType(distributionType);
			getO2CTransfAcknReqVO.setTransactionID(transactionID);
			getO2CTransfAcknReqVO.setLocale(locale);
			getO2CTransfAcknReqVO.setExtnwcode(channelUserVO.getNetworkID());*/
			
			O2CTransfAckDownloadReqDTO o2CTransfAckDownloadReqDTO = new O2CTransfAckDownloadReqDTO();
			o2CTransfAckDownloadReqDTO.setDistributionType(distributionType);
			o2CTransfAckDownloadReqDTO.setTransactionID(transactionID);
			o2CTransfAckDownloadReqDTO.setLocale(locale);
			o2CTransfAckDownloadReqDTO.setExtnwcode(channelUserVO.getNetworkID());
			o2CTransfAckDownloadReqDTO.setUserId(channelUserVO.getUserID());
			ChannelUserWebDAO  channelUserWebDAO = new ChannelUserWebDAO();
			ArrayList hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, channelUserVO.getUserID(), false);
			if (BTSLUtil.isNullOrEmptyList(hierarchyList)) {
				if (log.isDebugEnabled()) {
					log.debug("enquirySearch", "Logged in user has no child user so there would be no transactions");
				}
				throw new BTSLBaseException(this, methodName, "o2cenquiry.transferlist.msg.nohierarchy");
			}
			pretupsUIReportsServiceI.getO2cTransferAcknowledgement(o2CTransfAckDownloadReqDTO,response);
			 HashMap<String, String> mp =(HashMap<String, String>) hierarchyList.stream()
				      .collect(Collectors.toMap(ChannelUserVO::getUserID,ChannelUserVO::getUserID));
			// check inquiry allowed for given transaction ID(user)
			boolean isMatched = false;
			List<GetO2CTransferAckDTO> o2cAcknoledgeResponseList = response.getListO2CTransferAckDTO(); 
			if (!o2cAcknoledgeResponseList.isEmpty() && !BTSLUtil.isNullOrEmptyList(hierarchyList)) {
			   for(GetO2CTransferAckDTO  o2cAcknowledresp : o2cAcknoledgeResponseList)		 {
					isMatched = false;
						if (mp.containsKey(o2cAcknowledresp.getFromUserID())  || mp.containsKey(o2cAcknowledresp.getToUserID())) {
							isMatched = true;
							break;
						}
					}
					if (!isMatched) {
						response.setListO2CTransferAckDTO(null);
						throw new BTSLBaseException(this, methodName, "o2cenquiry.viewo2ctransfers.msg.notauthorize");
					}
				}
			
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#fetchUserNameAutoSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("fetchUserNameAutoSearch", " Exited ");
			}
		}

		return response;
	}
		
	
	@PostMapping(value = "/o2cTransferAcknowldgeRptDownload", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Operator to channel transfer acknowledgement details download", response = GetO2CTransferAckDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = GetO2CTransferAckDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${o2cTransferAcknowldgeRptDownload.summary}", description="${o2cTransferAcknowldgeRptDownload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetO2CTransferAckDownloadResp.class))
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




	public GetO2CTransferAckDownloadResp o2cTransferAcknowldgeRptDownload(@RequestBody O2CTransferAckDownloadReq o2CTransferAckDownloadReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "o2cTransferAcknowldgeRptDownload";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		GetO2CTransferAckDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		O2CTransfAckDownloadReqDTO o2CTransfAckDownloadReqDTO = null;

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new GetO2CTransferAckDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			
			String extNtCode = channelUserVO.getNetworkID();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			o2CTransfAckDownloadReqDTO = new O2CTransfAckDownloadReqDTO(); // distibutionType input is not used any more.
			o2CTransfAckDownloadReqDTO.setTransactionID(o2CTransferAckDownloadReq.getData().getTransactionID());
			o2CTransfAckDownloadReqDTO.setLocale(locale);
			o2CTransfAckDownloadReqDTO.setExtnwcode(channelUserVO.getNetworkID());
			o2CTransfAckDownloadReqDTO.setUserId(channelUserVO.getUserID());
			o2CTransfAckDownloadReqDTO.setDispHeaderColumnList(o2CTransferAckDownloadReq.getData().getDispHeaderColumnList());
			 if(!o2CTransferAckDownloadReq.getData().getDistributionType().equals(PretupsI.STOCK) && !o2CTransferAckDownloadReq.getData().getDistributionType().equals(PretupsI.VOUCHER) ) {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.INVALID_DISTRIBUTION_TYPE, 0, null);
			 }
			
			o2CTransfAckDownloadReqDTO.setDistributionType(o2CTransferAckDownloadReq.getData().getDistributionType());
			String fileType = o2CTransferAckDownloadReq.getData().getFileType();
			String allowedFileType = fileType;
			if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			o2CTransfAckDownloadReqDTO.setFileType(allowedFileType);
			o2CTransfAckDownloadReqDTO.setMsisdn(msisdn);


			
           pretupsUIReportsServiceI.downloadO2CTransferAcknowlege(o2CTransfAckDownloadReqDTO, response);

			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#passbookSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
	} 
	
	
	
	
	
	@PostMapping(value = "/o2cTransferDetailsSearch", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Operator to Channel transfer detail search  report", response = O2CtransferDetSearchResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = O2CtransferDetSearchResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${o2cTransferDetailsSearch.summary}", description="${o2cTransferDetailsSearch.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CtransferDetSearchResp.class))
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



	public O2CtransferDetSearchResp o2cTransferDetailsSearch(@RequestBody O2CTransferdetailsSearchReqVO o2CTransferdetailsSearchReqVO,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		
		final String methodName = "o2cTransferDetailsSearch";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		O2CtransferDetSearchResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new O2CtransferDetSearchResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = o2CTransferdetailsSearchReqVO.getData().getFromDate();
			String toDate = o2CTransferdetailsSearchReqVO.getData().getToDate();
			String extNtCode = o2CTransferdetailsSearchReqVO.getData().getExtnwcode();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));

			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("HomeScreenTransactionCntroller", "C2CTransfercomm",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}

			if (BTSLUtil.isNullString(extNtCode) || !extNtCode.equals(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException("HomeScreenTransactionController", "C2CTransfercomm",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);			}
		
			O2CTransferDetailsReqDTO  o2cTransferDetailsReqDTO = new O2CTransferDetailsReqDTO();
			o2cTransferDetailsReqDTO.setTransferSubType(o2CTransferdetailsSearchReqVO.getData().getTransferSubType());
			o2cTransferDetailsReqDTO.setFromDate(fromDate);
			o2cTransferDetailsReqDTO.setToDate(toDate);
			o2cTransferDetailsReqDTO.setExtnwcode(extNtCode);
			o2cTransferDetailsReqDTO.setMsisdn(msisdn);
			o2cTransferDetailsReqDTO.setDomain(o2CTransferdetailsSearchReqVO.getData().getDomain());
			o2cTransferDetailsReqDTO.setCategoryCode(o2CTransferdetailsSearchReqVO.getData().getCategoryCode());
			o2cTransferDetailsReqDTO.setGeography(o2CTransferdetailsSearchReqVO.getData().getGeography());
			o2cTransferDetailsReqDTO.setUser(o2CTransferdetailsSearchReqVO.getData().getUser());
			o2cTransferDetailsReqDTO.setLocale(locale);
			o2cTransferDetailsReqDTO.setUser(o2CTransferdetailsSearchReqVO.getData().getUser());
			o2cTransferDetailsReqDTO.setUserId(channelUserVO.getUserID());
			o2cTransferDetailsReqDTO.setDistributionType(o2CTransferdetailsSearchReqVO.getData().getDistributionType());
			o2cTransferDetailsReqDTO.setTransferCategory(o2CTransferdetailsSearchReqVO.getData().getTransferCategory());
			pretupsUIReportsServiceI.getO2cTransferDetails(o2cTransferDetailsReqDTO, response);

			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#c2ctransferCommission");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response; 
		
	} 

	
	@PostMapping(value = "/downloadO2ctransferdetailsRpt", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Operator to Channel transfer details  report download", response = O2CTransferDetailDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = O2CTransferDetailDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadO2ctransferdetailsRpt.summary}", description="${downloadO2ctransferdetailsRpt.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CTransferDetailDownloadResp.class))
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



	public O2CTransferDetailDownloadResp downloadO2ctransferdetailsRpt(@RequestBody O2CTransferDetDownloadReq o2CTransferDetDownloadReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "c2CtransferCommissionRptDownload";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		O2CTransferDetailDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new O2CTransferDetailDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = o2CTransferDetDownloadReq.getData().getFromDate();
			String toDate = o2CTransferDetDownloadReq.getData().getToDate();
			String extNtCode = o2CTransferDetDownloadReq.getData().getExtnwcode();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));

			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("HomeScreenTransactionCntroller", "passbookSearch",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}

			if (BTSLUtil.isNullString(extNtCode) || !extNtCode.equals(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException("HomeScreenTransactionController", "passbookSearch",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
		
			
			
			O2CTransferDetailsReqDTO  o2CTransferDetailsReqDTO = new O2CTransferDetailsReqDTO();
			o2CTransferDetailsReqDTO.setTransferSubType(o2CTransferDetDownloadReq.getData().getTransferSubType());
			o2CTransferDetailsReqDTO.setFromDate(fromDate);
			o2CTransferDetailsReqDTO.setToDate(toDate);
			o2CTransferDetailsReqDTO.setExtnwcode(extNtCode);
			o2CTransferDetailsReqDTO.setMsisdn(msisdn);
			o2CTransferDetailsReqDTO.setDomain(o2CTransferDetDownloadReq.getData().getDomain());
			o2CTransferDetailsReqDTO.setCategoryCode(o2CTransferDetDownloadReq.getData().getCategoryCode());
			o2CTransferDetailsReqDTO.setGeography(o2CTransferDetDownloadReq.getData().getGeography());
			o2CTransferDetailsReqDTO.setUser(o2CTransferDetDownloadReq.getData().getUser());
			o2CTransferDetailsReqDTO.setLocale(locale);
			o2CTransferDetailsReqDTO.setDistributionType(o2CTransferDetDownloadReq.getData().getDistributionType());
			o2CTransferDetailsReqDTO.setTransferCategory(o2CTransferDetDownloadReq.getData().getTransferCategory());
			
			
			o2CTransferDetailsReqDTO.setUser(o2CTransferDetDownloadReq.getData().getUser());
			o2CTransferDetailsReqDTO.setDispHeaderColumnList(o2CTransferDetDownloadReq.getData().getDispHeaderColumnList());
			String fileType = o2CTransferDetDownloadReq.getData().getFileType();
			String allowedFileType = fileType;
			if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			o2CTransferDetailsReqDTO.setFileType(allowedFileType);
			o2CTransferDetailsReqDTO.setMsisdn(msisdn);
			o2CTransferDetailsReqDTO.setUserId(channelUserVO.getUserID());
			
            pretupsUIReportsServiceI.downloadO2CTransferDetails(o2CTransferDetailsReqDTO, response);
		
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#downloadO2ctransferdetailsRpt");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
		

	}
	
	
	public C2STransferCommReqDTO commonC2SInputAssignments(Connection con,C2STransfercommDownloadReq c2STransfercommDownloadReq,ChannelUserVO channelUserVO) throws BTSLBaseException, SQLException {
		
	 String methodName ="commonC2SInputAssignments";
	 UserDAO userDAO= new UserDAO();
		C2STransferCommReqDTO  c2STransferCommReqDTO = new C2STransferCommReqDTO();
		c2STransferCommReqDTO.setMobileNumber(c2STransfercommDownloadReq.getData().getMobileNumber());
		c2STransferCommReqDTO.setNetworkCode(c2STransfercommDownloadReq.getData().getNetworkCode());
		//c2CTransferCommReqDTO.setMsisdn(msisdn);
		c2STransferCommReqDTO.setDomain(c2STransfercommDownloadReq.getData().getDomain());
		c2STransferCommReqDTO.setCategoryCode(c2STransfercommDownloadReq.getData().getCategoryCode());
		c2STransferCommReqDTO.setGeography(c2STransfercommDownloadReq.getData().getGeography());
		c2STransferCommReqDTO.setAllowedFromTime(c2STransfercommDownloadReq.getData().getAllowedTimeFrom());
		c2STransferCommReqDTO.setAllowedToTime(c2STransfercommDownloadReq.getData().getAllowedTimeTo());
		c2STransferCommReqDTO.setReportDate(c2STransfercommDownloadReq.getData().getReportDate());
		c2STransferCommReqDTO.setService(c2STransfercommDownloadReq.getData().getService());
		c2STransferCommReqDTO.setTransStatus(c2STransfercommDownloadReq.getData().getTransStatus());
		c2STransferCommReqDTO.setChannelUserID(c2STransfercommDownloadReq.getData().getChannelUser());
		if(!c2STransfercommDownloadReq.getData().getReqTab().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ ) && !c2STransfercommDownloadReq.getData().getReqTab().equals(PretupsI.C2C_ADVANCED_TAB_REQ)  ){
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.INVAID_C2C_TAB_REQ, 0, null);
		}
		
		
		if(c2STransfercommDownloadReq.getData().getReqTab().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ)){
			if(BTSLUtil.isEmpty(c2STransferCommReqDTO.getDomain()) || (!BTSLUtil.isEmpty(c2STransferCommReqDTO.getDomain())) &&  c2STransferCommReqDTO.getDomain().trim().toUpperCase().equals(PretupsI.ALL)  ){
				c2STransferCommReqDTO.setDomain(channelUserVO.getDomainID());
			}
			if(BTSLUtil.isNullorEmpty(c2STransferCommReqDTO.getCategoryCode()) || (c2STransferCommReqDTO.getCategoryCode()!=null && c2STransferCommReqDTO.getCategoryCode().trim().toUpperCase().equals(PretupsI.ALL))) {
				c2STransferCommReqDTO.setCategoryCode(PretupsI.ALL);
			}		
//			}else {
//				throw new BTSLBaseException("PretupsUIReportsController", methodName,
//						PretupsErrorCodesI.EMPTY_MOBILE_TAB_CATEGORY, 0, null);
//			}
			if(BTSLUtil.isNullorEmpty(c2STransferCommReqDTO.getGeography()) ||(c2STransferCommReqDTO.getGeography()!=null && c2STransferCommReqDTO.getGeography().trim().toUpperCase().equals(PretupsI.ALL))) {
				c2STransferCommReqDTO.setGeography(PretupsI.ALL);
			}
//			else {
//				throw new BTSLBaseException("PretupsUIReportsController", methodName,
//						PretupsErrorCodesI.EMPTY_MOBILE_TAB_GEOGRAPHY, 0, null);
//			}
			
			if(BTSLUtil.isNullorEmpty(c2STransfercommDownloadReq.getData().getMobileNumber()) ||(c2STransfercommDownloadReq.getData().getMobileNumber()!=null && c2STransfercommDownloadReq.getData().getMobileNumber().trim().toUpperCase().equals(PretupsI.ALL))) {
				 c2STransferCommReqDTO.setMobileNumber(c2STransfercommDownloadReq.getData().getMobileNumber());
			}
			
			if( (c2STransfercommDownloadReq.getData().getUserType()!=null &&  !c2STransfercommDownloadReq.getData().getUserType().equals(PretupsI.ALL) ) &&   (!c2STransfercommDownloadReq.getData().getUserType().equals(PretupsI.USER_TYPE_CHANNEL) && !c2STransfercommDownloadReq.getData().getUserType().equals(PretupsI.USER_TYPE_STAFF) )) {
				 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
							PretupsErrorCodesI.INVALID_USER_TYPE);
	     	}
			
			c2STransferCommReqDTO.setReqTab(PretupsI.C2C_MOBILENUMBER_TAB_REQ);
		}else {
			c2STransferCommReqDTO.setReqTab(PretupsI.C2C_ADVANCED_TAB_REQ);
			if(!BTSLUtil.isNullorEmpty(c2STransfercommDownloadReq .getData().getUserType()) && !c2STransfercommDownloadReq.getData().getUserType().equals(PretupsI.USER_TYPE_STAFF) ) {
				 if (c2STransferCommReqDTO.getChannelUserID()!=null && !c2STransferCommReqDTO.getChannelUserID().equals(PretupsI.ALL)) { 
					 ChannelUserVO channelUserIDVO=  userDAO.loadAllUserDetailsByLoginID(con, c2STransferCommReqDTO.getChannelUserID());
					 if(BTSLUtil.isNullObject(channelUserIDVO)) {
						 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
									PretupsErrorCodesI.INVALID_USER_ID);
					 }
					 c2STransferCommReqDTO.setChannelUserID(channelUserIDVO.getUserID());
				 }
				 
		    	
			     	}

		}
	    if(!BTSLUtil.isNullorEmpty(c2STransfercommDownloadReq .getData().getUserType()) && c2STransfercommDownloadReq.getData().getUserType().equals(PretupsI.USER_TYPE_STAFF) ) {
	    	validateStaffDetails(con,c2STransferCommReqDTO,c2STransfercommDownloadReq);
	    	c2STransferCommReqDTO.setOptionStaff_LoginIDOrMsisdn(c2STransfercommDownloadReq.getData().getStaffOption());
	    	c2STransferCommReqDTO.setUserType(PretupsI.USER_TYPE_STAFF);
		     	} else if (!BTSLUtil.isNullorEmpty(c2STransfercommDownloadReq .getData().getUserType()) && c2STransfercommDownloadReq.getData().getUserType().equals(PretupsI.USER_TYPE_CHANNEL)) {
		     		c2STransferCommReqDTO.setUserType(PretupsI.USER_TYPE_CHANNEL); 		
		     	}else {
		     		c2STransferCommReqDTO.setUserType(PretupsI.ALL);
		     	}

		
       return c2STransferCommReqDTO;
	}
	

    public void validateStaffDetails(Connection con,C2STransferCommReqDTO c2STransferCommReqDTO,C2STransfercommDownloadReq c2STransfercommDownloadReq) throws BTSLBaseException{
    	final String methodName ="validateStaffDetails";
    	UserDAO userDAO = new UserDAO();
    	
    	 if(  BTSLUtil.isNullObject(c2STransfercommDownloadReq.getData().getStaffOption()) || (c2STransfercommDownloadReq.getData().getStaffOption()!=null && c2STransfercommDownloadReq.getData().getStaffOption().trim().length()==0  )) {
   		  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
					PretupsErrorCodesI.SEARCH_STAFF_BY);
    	 }
    	
    	
    	 if(c2STransfercommDownloadReq.getData().getStaffOption()!=null && c2STransfercommDownloadReq.getData().getStaffOption().equals(PretupsI.OPTION_LOGIN_ID) ) {
    		  if(BTSLUtil.isNullObject( c2STransfercommDownloadReq.getData().getLoginIDOrMSISDN())){
    			  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
							PretupsErrorCodesI.STAFF_LOGIN_ID_MANDATORY); 
    		  }
    		  if(!c2STransfercommDownloadReq.getData().getLoginIDOrMSISDN().equals(PretupsI.ALL)) {
	    		  ChannelUserVO channUserVO =userDAO.loadUserDetailsByLoginId(con, c2STransfercommDownloadReq.getData().getLoginIDOrMSISDN());
	    		  if(BTSLUtil.isNullObject(channUserVO)){
	    			  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
								PretupsErrorCodesI.INVALID_STAFF_LOGIN_ID); 
	    		  }
    		  }
    	 }else {
    	  if(BTSLUtil.isNullObject( c2STransfercommDownloadReq.getData().getLoginIDOrMSISDN())){
			  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.STAFF_MOBILE_NUM_MANDATORY); 
		  }
		  ChannelUserVO channUserVO =userDAO.loadUserDetailsByMsisdn(con, c2STransfercommDownloadReq.getData().getLoginIDOrMSISDN());
		  if(BTSLUtil.isNullObject(channUserVO)){
			  throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.INVALID_STAFF_MSISDN); 
		  }
		 }
		  c2STransferCommReqDTO.setLoginIDOrMsisdn(c2STransfercommDownloadReq.getData().getLoginIDOrMSISDN());
    	
    }
	
	@GetMapping(value = "/downloadOfflineReportByTaskID", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Offline report download", response = OfflineFileDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = OfflineFileDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
    */

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadOfflineReportByTaskID.summary}", description="${downloadOfflineReportByTaskID.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OfflineFileDownloadResp.class))
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



	public OfflineFileDownloadResp downloadOfflineReportByTaskID(
    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.REPORT_TASK_ID, example = "",required = true)
			@RequestParam("reportTaskID") String reportTaskID,
			 HttpServletResponse responseSwag
					)throws IOException{
		String offlineDownloadLocation = SystemPreferences.OFFLINERPT_DOWNLD_PATH;
		OfflineReportDAO offlineReportDAO = new OfflineReportDAO();
		final String methodName = "downloadOfflineReportByTaskID";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		OfflineFileDownloadResp offlineFileDownloadResp = new OfflineFileDownloadResp();

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			OfflineReportTaskIDInfo offlineReportTaskIDInfo =	offlineReportDAO.getOfflineReportTaskStatusInfo(con, reportTaskID);
			
			 if(offlineReportTaskIDInfo==null) {
				 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
							PretupsErrorCodesI.INVALID_REPORT_TASKID);
			 }
			 //offlineDownloadLocation="D:\\downloadedReports\\";
		File file = new File(offlineDownloadLocation +offlineReportTaskIDInfo.getFileName());
		
		
		   byte[] fileContent = Files.readAllBytes(file.toPath());
	        String fileContentString = Base64.getEncoder().encodeToString(fileContent);
		
	        offlineFileDownloadResp.setFileData(fileContentString);
	        
	        char dotchar ='.';
	        int dot = offlineReportTaskIDInfo.getFileName().lastIndexOf(dotchar);
	        String filenName = offlineReportTaskIDInfo.getFileName().substring(0, dot);
	        offlineFileDownloadResp.setFileName(filenName);
	        String fileExtension = offlineReportTaskIDInfo.getFileName().substring(dot+1, offlineReportTaskIDInfo.getFileName().length());
	        offlineFileDownloadResp.setFileType(fileExtension);
	        offlineReportDAO.updateOfflineReportTaskStatus(con, PretupsI.OFFLINE_STATUS_DOWNLOADED, reportTaskID, null, false);
	        String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
	        offlineFileDownloadResp.setStatus(success);
	        offlineFileDownloadResp.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			offlineFileDownloadResp.setMessage(resmsg);
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				offlineFileDownloadResp.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			offlineFileDownloadResp.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			offlineFileDownloadResp.setMessageCode(msg);
			offlineFileDownloadResp.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			offlineFileDownloadResp.setStatus(fail);
			offlineFileDownloadResp.setMessageCode("error.general.processing");
			offlineFileDownloadResp.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#downloadO2ctransferdetailsRpt");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}
		
		return offlineFileDownloadResp;
    }

	

	
	@GetMapping(value = "/offlineDownlaoadActionByTaskID", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Offline report download by task ID", response = OfflineReportActionResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = OfflineReportActionResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
    */

	@io.swagger.v3.oas.annotations.Operation(summary = "${offlineDownlaoadActionByTaskID.summary}", description="${offlineDownlaoadActionByTaskID.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OfflineReportActionResp.class))
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



	public OfflineReportActionResp offlineDownlaoadActionByTaskID(
    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.REPORT_TASK_ID, example = "",required = true)
			@RequestParam("reportTaskID") String reportTaskID,
			@Parameter(description = SwaggerAPIDescriptionI.OFFLINE_ACTION, example = "",required = true)
			@RequestParam("action") String action,
			 HttpServletResponse responseSwag
					)throws IOException{
		String offlineDownloadLocation = SystemPreferences.OFFLINERPT_DOWNLD_PATH;
		OfflineReportDAO offlineReportDAO = new OfflineReportDAO();
		final String methodName = "downloadOfflineReportByTaskID";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		OfflineReportActionResp offlineReportActionResp = new OfflineReportActionResp();

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			OfflineReportTaskIDInfo offlineReportTaskIDInfo =	offlineReportDAO.getOfflineReportTaskStatusInfo(con, reportTaskID);
			
			 if(offlineReportTaskIDInfo==null) {
				 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
							PretupsErrorCodesI.INVALID_REPORT_TASKID);
			 }
			 
			 if(!action.equals(PretupsI.OFFLINE_REPORTACTION_DELETE)  && !action.equals(PretupsI.OFFLINE_REPORTACTION_CANCEL) ) {
				 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
							PretupsErrorCodesI.INVALID_REPORT_ACTION);
			 }
			
			 if(action.equals(PretupsI.OFFLINE_REPORTACTION_DELETE)  ) {
				 if(offlineReportTaskIDInfo.getReportStatus()!=null) {
					  if(!offlineReportTaskIDInfo.getReportStatus().equals(PretupsI.OFFLINE_STATUS_NODATA)   &&  !offlineReportTaskIDInfo.getReportStatus().equals(PretupsI.OFFLINE_STATUS_CANCELLED) && !offlineReportTaskIDInfo.getReportStatus().equals(PretupsI.OFFLINE_STATUS_DOWNLOADED) &&  !offlineReportTaskIDInfo.getReportStatus().equals(PretupsI.OFFLINE_STATUS_COMPLETED)) {
						 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
									PretupsErrorCodesI.OFFLINE_FILE_DELETE_NOTALLOWED);
					  }
					 }
			 }
			 
			 OfflineReportActionReqDTO offlineReportActionReqDTO  = new OfflineReportActionReqDTO();
			 offlineReportActionReqDTO.setReportTaskID(reportTaskID);
			 offlineReportActionReqDTO.setReportAction(action);
			 offlineReportActionReqDTO.setFileName(offlineReportTaskIDInfo.getFileName());
			 offlineReportActionReqDTO.setLocale(locale);
			 offlineReportActionReqDTO.setOfflineDownloadPath(offlineDownloadLocation);
			 pretupsUIReportsServiceI.delegateOfflineAction(offlineReportActionReqDTO, offlineReportActionResp);
		
	        
	       
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				offlineReportActionResp.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				offlineReportActionResp.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			offlineReportActionResp.setMessageCode(msg);
			offlineReportActionResp.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			offlineReportActionResp.setStatus(fail);
			offlineReportActionResp.setMessageCode("error.general.processing");
			offlineReportActionResp.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#downloadO2ctransferdetailsRpt");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}
		
		return offlineReportActionResp;
    }

	
	
	@PostMapping(value = "/additionCommisionSummaryC2S", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Additional commision summary search  report", response = AdditionalCommissionSummryC2SResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AdditionalCommissionSummryC2SResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${additionCommisionSummaryC2S.summary}", description="${additionCommisionSummaryC2S.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AdditionalCommissionSummryC2SResp.class))
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



	public AdditionalCommissionSummryC2SResp additionCommisionSummaryC2S(@RequestBody AdditionalCommSummryC2SReqVO additionalCommSummryC2SReqVO,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		
		final String methodName = "additionCommisionSummaryC2S";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		AdditionalCommissionSummryC2SResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		UserDAO userDAO = new UserDAO();

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new AdditionalCommissionSummryC2SResp();
			channelUserDAO = new ChannelUserDAO();
			
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			String loginID = oAuthUser.getData().getLoginid();
			
			ChannelUserVO channelUserVO =  userDAO.loadAllUserDetailsByLoginID(con,loginID);
			
			if(!PretupsI.CATEGORY_TYPE_OPT.equals(channelUserVO.getDomainID())){
				channelUserVO= channelUserDAO.loadChannelUserDetails(con, msisdn);	
				
			}
			
			 
			
			 

			String fromDate = additionalCommSummryC2SReqVO.getData().getFromDate();
			String toDate = additionalCommSummryC2SReqVO.getData().getToDate();
			
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			if(additionalCommSummryC2SReqVO.getData().getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_DAILY)){
				try {
					patternDate.parse(fromDate);
					patternDate.parse(toDate);
				} catch (Exception be) {
					throw new BTSLBaseException("HomeScreenTransactionCntroller", "C2CTransfercomm",
							PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
				}
			}
			
		
			AddtnlCommSummryReqDTO  addtnlCommSummryReqDTO = new AddtnlCommSummryReqDTO();
			
			addtnlCommSummryReqDTO.setFromDate(fromDate);
			addtnlCommSummryReqDTO.setToDate(toDate);
			
			addtnlCommSummryReqDTO.setMsisdn(msisdn);
			addtnlCommSummryReqDTO.setDomain(additionalCommSummryC2SReqVO.getData().getDomain());
			addtnlCommSummryReqDTO.setCategoryCode(additionalCommSummryC2SReqVO.getData().getCategoryCode());
			addtnlCommSummryReqDTO.setGeography(additionalCommSummryC2SReqVO.getData().getGeography());
			addtnlCommSummryReqDTO.setService(additionalCommSummryC2SReqVO.getData().getService());
			addtnlCommSummryReqDTO.setFromDate(additionalCommSummryC2SReqVO.getData().getFromDate());
			addtnlCommSummryReqDTO.setToDate(additionalCommSummryC2SReqVO.getData().getToDate());
			addtnlCommSummryReqDTO.setDailyOrmonthlyOption(additionalCommSummryC2SReqVO.getData().getDailyOrmonthlyOption());
			addtnlCommSummryReqDTO.setFromMonthYear(additionalCommSummryC2SReqVO.getData().getFromMonthYear());
			addtnlCommSummryReqDTO.setToMonthYear(additionalCommSummryC2SReqVO.getData().getToMonthYear());
			addtnlCommSummryReqDTO.setLocale(locale);
			addtnlCommSummryReqDTO.setExtnwcode(toDate);
			addtnlCommSummryReqDTO.setUserId(channelUserVO.getUserID());
			addtnlCommSummryReqDTO.setExtnwcode(channelUserVO.getNetworkID());
			
			
			
           pretupsUIReportsServiceI.getAdditionCommSummryDetails(addtnlCommSummryReqDTO, response);

			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#c2ctransferCommission");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response; 
		
	} 
	
	@PostMapping(value = "/downloadAddtnlCommSummryRpt", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Additional commission summary report download", response = AddtlnCommSummryDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AddtlnCommSummryDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadAddtnlCommSummryRpt.summary}", description="${downloadAddtnlCommSummryRpt.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AddtlnCommSummryDownloadResp.class))
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



	public AddtlnCommSummryDownloadResp downloadAddtnlCommSummryRpt(@RequestBody AddcommSummryDownloadReq addcommSummryDownloadReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "downloadAddtnlCommSummryRpt";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		AddtlnCommSummryDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new AddtlnCommSummryDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = addcommSummryDownloadReq.getData().getFromDate();
			String toDate = addcommSummryDownloadReq.getData().getToDate();
			String extNtCode =channelUserVO.getNetworkID(); 
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			if(addcommSummryDownloadReq.getData().getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_DAILY)){
			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("HomeScreenTransactionCntroller", "downloadAddtnlCommSummryRpt",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}
			}

			if (BTSLUtil.isNullString(extNtCode) || !extNtCode.equals(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException("HomeScreenTransactionController", "downloadAddtnlCommSummryRpt",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
		
			
			
AddtnlCommSummryReqDTO  addtnlCommSummryReqDTO = new AddtnlCommSummryReqDTO();
			
			addtnlCommSummryReqDTO.setFromDate(fromDate);
			addtnlCommSummryReqDTO.setToDate(toDate);
			
			addtnlCommSummryReqDTO.setMsisdn(msisdn);
			addtnlCommSummryReqDTO.setDomain(addcommSummryDownloadReq.getData().getDomain());
			addtnlCommSummryReqDTO.setCategoryCode(addcommSummryDownloadReq.getData().getCategoryCode());
			addtnlCommSummryReqDTO.setGeography(addcommSummryDownloadReq.getData().getGeography());
			addtnlCommSummryReqDTO.setService(addcommSummryDownloadReq.getData().getService());
			addtnlCommSummryReqDTO.setFromDate(addcommSummryDownloadReq.getData().getFromDate());
			addtnlCommSummryReqDTO.setToDate(addcommSummryDownloadReq.getData().getToDate());
			addtnlCommSummryReqDTO.setDailyOrmonthlyOption(addcommSummryDownloadReq.getData().getDailyOrmonthlyOption());
			addtnlCommSummryReqDTO.setFromMonthYear(addcommSummryDownloadReq.getData().getFromMonthYear());
			addtnlCommSummryReqDTO.setToMonthYear(addcommSummryDownloadReq.getData().getToMonthYear());
			addtnlCommSummryReqDTO.setLocale(locale);
			addtnlCommSummryReqDTO.setUserId(channelUserVO.getUserID());
			addtnlCommSummryReqDTO.setDispHeaderColumnList(addcommSummryDownloadReq.getData().getDispHeaderColumnList());
			String fileType = addcommSummryDownloadReq.getData().getFileType();
			String allowedFileType = fileType;
			if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			addtnlCommSummryReqDTO.setFileType(allowedFileType);
			addtnlCommSummryReqDTO.setMsisdn(msisdn);
			addtnlCommSummryReqDTO.setUserId(channelUserVO.getUserID());
			addtnlCommSummryReqDTO.setExtnwcode(channelUserVO.getNetworkID());
			
            pretupsUIReportsServiceI.downloadAddntlCommSummry(addtnlCommSummryReqDTO, response);
		
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#downloadO2ctransferdetailsRpt");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
		

	}


	
	@GetMapping(value= "/getLookUPListbyType", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "getLookUPListbyType",
	           response = LookUpListResp.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = LookUpListResp.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getLookUPListbyType.summary}", description="${getLookUPListbyType.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LookUpListResp.class))
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



	public LookUpListResp getLookUPListbyType(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.Lookup_Type, example = "",required = true)
			@RequestParam("lookupType") String lookupType,
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "getLookUPListbyType";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;

		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		LookUpListResp response = null;
		
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new LookUpListResp();

			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			if (BTSLUtil.isEmpty(country)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_LOOKUP_TYPE, 0, null);
			}
			
			ArrayList lookUpList = LookupsCache.loadLookupDropDown(lookupType, true); //TRFT
			
			  if(lookUpList.isEmpty()) { 
				
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
				}
			
			  
			response.setListDetails(lookUpList);
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#fetchUserNameAutoSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("fetchUserNameAutoSearch", " Exited ");
			}
		}

		return response;
	}


	
	@GetMapping(value= "/fetchStaffUserDetails", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Fetch Staff users",
	           response = FetchStaffDetailsRespVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = FetchStaffDetailsRespVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchStaffUserDetails.summary}", description="${fetchStaffUserDetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FetchStaffDetailsRespVO.class))
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



	public FetchStaffDetailsRespVO fetchStaffUserDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.REQUEST_TAB, example = "",required = true)
			@RequestParam("reqTab") String reqTab,
			@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_USER, example = "",required = true)
			@RequestParam("channlUserIDOrMSISDN") String channlUserIDOrMSISDN,
			@Parameter(description = SwaggerAPIDescriptionI.DOMAIN, example = "",required = true)
			@RequestParam("domainCode") String domainCode,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_PARENT_CATEGORY, example = "",required = true)
			@RequestParam("categoryCode") String categoryCode,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_PARENT_GEOGRAPHY, example = "",required = true)
			@RequestParam("geography") String  geography,
			
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "fetchStaffUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		FetchStaffDetailsRespVO response = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		
		
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new FetchStaffDetailsRespVO();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			CommonUtil commonUtil= new CommonUtil();
			commonUtil.validateCategoryCode(categoryCode, con);
			commonUtil.validateDomain(domainCode, con);
			commonUtil.validateGeography(geography, con);
			
			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
			
			//response.setService("STAFFUSERDETAILSDOWNLOADRESP");
			UserStaffDetailsReqDTO userStaffDetailsReqDTO = new UserStaffDetailsReqDTO();
			userStaffDetailsReqDTO.setCategoryCode(categoryCode);
			if(reqTab!=null && (!reqTab.trim().toUpperCase().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ)  &&  !reqTab.trim().toUpperCase().equals(PretupsI.C2C_ADVANCED_TAB_REQ) )) {
				 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
							PretupsErrorCodesI.INVAID_TAB_REQ);
			}
			
			
//			if(OptionMobileOrLoginID!=null && (!OptionMobileOrLoginID.trim().toUpperCase().equals(PretupsI.OPTION_MSISDN)  &&  !OptionMobileOrLoginID.trim().toUpperCase().equals(PretupsI.OPTION_LOGIN_ID) )) {
//				 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
//							PretupsErrorCodesI.INVALID_OPTION_PROVIDED);
//			}
			UserDAO userDAO = new UserDAO();
			String channelUserID=channlUserIDOrMSISDN;
			ChannelUserVO channelUserIDVO=null;
			
			if(channlUserIDOrMSISDN!=null && !channlUserIDOrMSISDN.trim().equals(PretupsI.ALL)) {
				if(reqTab.trim().toUpperCase().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ) ) {
					 if(BTSLUtil.isNullString(channlUserIDOrMSISDN)) {
						 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
									PretupsErrorCodesI.EMPTY_CHANNELUSER_MOBILENUM);
					 }
					 channelUserIDVO=  userDAO.loadUserDetailsByMsisdn(con, channlUserIDOrMSISDN);
					 if(BTSLUtil.isNullObject(channelUserIDVO)) {
						 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
									PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
					 }
				}else {
					 if(BTSLUtil.isNullString(channlUserIDOrMSISDN)) {
						 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
									PretupsErrorCodesI.EMPTY_CHANNELUSER_LOGINID);
					 }
					 channelUserIDVO=  userDAO.loadUserDetailsByLoginId(con, channlUserIDOrMSISDN);
					 if(BTSLUtil.isNullObject(channelUserIDVO)) {
						 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
									PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID);
					 }
				}
			channelUserID=channelUserIDVO.getUserID();
			}else { //ALL
				channelUserID=channelUserVO.getUserID();	
			}
			userStaffDetailsReqDTO.setChannelUserID(channelUserID);
//			userStaffDetailsReqDTO.setMobileOrLoginIDOption(optionMobileORLoginID);
			userStaffDetailsReqDTO.setDomainCode(domainCode);
			userStaffDetailsReqDTO.setGeography(geography);
			userStaffDetailsReqDTO.setMsisdn(channelUserVO.getMsisdn());
			userStaffDetailsReqDTO.setUserId(channelUserVO.getUserID());  //Loggedin Userid.
			pretupsUIReportsServiceI.getStaffUserDetailsData(userStaffDetailsReqDTO,response);
			
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#fetchUserNameAutoSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("fetchUserNameAutoSearch", " Exited ");
			}
		}

		return response;
	}



	
	@GetMapping(value= "/viewOfflineRptProcessStatusList", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "View all offline Report process status",
	           response = ViewAllOfflineRptStatusRespVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = ViewAllOfflineRptStatusRespVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${viewOfflineRptProcessStatusList.summary}", description="${viewOfflineRptProcessStatusList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewAllOfflineRptStatusRespVO.class))
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



	public ViewAllOfflineRptStatusRespVO viewOfflineRptProcessList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag
					)throws Exception{
		
		final String methodName = "fetchStaffUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		ViewAllOfflineRptStatusRespVO response = null;
		UserDAO userDao = null;
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		
		
		
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new ViewAllOfflineRptStatusRespVO();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
				String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
			
			//response.setService("STAFFUSERDETAILSDOWNLOADRESP");
			
			pretupsUIReportsServiceI.getAllOfflineReportProcessStatus(channelUserVO.getUserID(),response,locale);
			
			// final response message
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#fetchUserNameAutoSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("fetchUserNameAutoSearch", " Exited ");
			}
		}

		return response;
	}
	
	@PostMapping(value = "/o2cAcknowledgePDFDownload", consumes =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "O2C acknowledge dowload in PDF format", response = ViewTxnDetailsResponseVO.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = ViewTxnDetailsResponseVO.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${o2cAcknowledgePDFDownload.summary}", description="${o2cAcknowledgePDFDownload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = O2CACknowledgePDFResponse.class))
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



	public O2CACknowledgePDFResponse o2cAcknowledgePDFDownload(
	    		HttpServletRequest httpServletRequest,
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	    		@Parameter(description = SwaggerAPIDescriptionI.O2C_ACKKNOWLEDGE_DETAILS)
	    		@RequestBody C2CTransferDetailsVO c2CTransferDetailsVO, HttpServletResponse response1) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final String methodName = "o2cAcknowledgePDFDownload";
		RequestVO response;
        RestReceiver restReceiver;
        RestReceiver.updateRequestIdChannel();
        final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
        restReceiver = new RestReceiver();
       	ViewTxnDetailsResponseVO responsenew = new ViewTxnDetailsResponseVO();
        OAuthUser oAuthUser= null;
        OAuthUserData oAuthUserData =null;
        O2CACknowledgePDFResponse o2CACknowledgePDFResponse = new O2CACknowledgePDFResponse();
        
        try {
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
			//OAuthenticationUtil.validateTokenApi(headers);
        	
        	oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			c2CTransferDetailsVO.setServicePort(oAuthUser.getServicePort());
			c2CTransferDetailsVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			c2CTransferDetailsVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			c2CTransferDetailsVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			c2CTransferDetailsVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			c2CTransferDetailsVO.setSourceType(oAuthUser.getSourceType());
			
			//setting password in requestVO from oAuthUser
			c2CTransferDetailsVO.getData().setPassword( oAuthUser.getData().getPassword() );
			
			RequestVO requestVO = new RequestVO();
			requestVO.setRequestGatewayType(PretupsI.REQUEST_SOURCE_TYPE_REST);
			requestVO.setRequestGatewayCode(PretupsI.REQUEST_SOURCE_TYPE_REST);
		    requestVO.setRequestMessageOrigStr(PretupsRestUtil.convertObjectToJSONString(c2CTransferDetailsVO));
		    requestVO.setUserLoginId(oAuthUserData.getLoginid());
			  C2CVoucherTransferDetailsController c2CVoucherTransferDetailsController = new C2CVoucherTransferDetailsController();
			  response =  c2CVoucherTransferDetailsController.process1(requestVO);
           if(response.getMessageCode()!=PretupsErrorCodesI.TXN_SUCCESSFUL)
           { 
        	responsenew.setStatus(HttpStatus.SC_BAD_REQUEST);
        	responsenew.setMessageCode(response.getMessageCode());
        	responsenew.setDataObj(response.getChannelTransferVO());
        	String resmsg = RestAPIStringParser.getMessage(
 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), response.getMessageCode(),
 					null);
        	responsenew.setMessage(resmsg);
        	o2CACknowledgePDFResponse.setMessage(resmsg);
        	o2CACknowledgePDFResponse.setStatus(HttpStatus.SC_BAD_REQUEST+"");
        	o2CACknowledgePDFResponse.setMessageCode(response.getMessageCode());
           }else {
        	responsenew.setStatus(200);
        	responsenew.setMessage(response.getSenderReturnMessage());
        	responsenew.setMessageCode(response.getMessageCode());
        	responsenew.setDataObj(response.getChannelTransferVO());
        	o2CACknowledgePDFResponse=	o2CTransferAckwledgePDFReportGen.generatePDF(response.getChannelTransferVO());
        	o2CACknowledgePDFResponse.setMessage(response.getSenderReturnMessage());
        	o2CACknowledgePDFResponse.setMessageCode(response.getMessageCode());
        	o2CACknowledgePDFResponse.setStatus(200+"");
           }
        	
        return o2CACknowledgePDFResponse;
   
    }catch (BTSLBaseException be) {
		//PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
		log.error(methodName, "BTSLBaseException " + be.getMessage());
        log.errorTrace(methodName, be);
        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
          	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
          }
           else{
           response1.setStatus(HttpStatus.SC_BAD_REQUEST);
           }
        responsenew.setMessageCode(be.getMessageKey());
        	String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
					null);

        	responsenew.setStatus(be.getErrorCode());
        	responsenew.setMessage(resmsg);
            return o2CACknowledgePDFResponse;
        
    } catch (Exception e) {
    	//PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
    	log.error(methodName, "Exception " + e.getMessage());
        log.errorTrace(methodName, e);
        responsenew.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
				null);
        responsenew.setStatus(PretupsI.UNABLE_TO_PROCESS_REQUEST);
        responsenew.setMessage(resmsg);
    	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        return o2CACknowledgePDFResponse;
    }
	
	finally {
        LogFactory.printLog(methodName, " Exited ", log);
    }
	}


	@PostMapping(value = "/passbookOthersDownload", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Passbook others  details", response = PassbookOthersInfoResponse.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PassbookOthersInfoResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${passbookOthersDownload.summary}", description="${passbookOthersDownload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResponseEntity.class))
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



	public ResponseEntity<?> passbookOthersDownload(
			@RequestBody PassbookSearchOthersRequestVO passbookSearchOthersRequestVO,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "passbookOthersDownload";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		PassbookOthersDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new PassbookOthersDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = passbookSearchOthersRequestVO.getData().getFromDate();
			String toDate = passbookSearchOthersRequestVO.getData().getToDate();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	
			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookOthers download",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}
			
			UserDAO userDAO = new UserDAO();
			PassbookOthersReqDTO passbookOthersReqDTO = new  PassbookOthersReqDTO(); 
			passbookOthersReqDTO.setCategoryCode(passbookSearchOthersRequestVO.getData().getCategory());
			passbookOthersReqDTO.setGeography(passbookSearchOthersRequestVO.getData().getGeography());
			passbookOthersReqDTO.setDomain(passbookSearchOthersRequestVO.getData().getDomain());
			passbookOthersReqDTO.setUser(PretupsI.ALL); // Filter User ID
			if(!passbookSearchOthersRequestVO.getData().getUser().trim().equals(PretupsI.ALL)) {
					ChannelUserVO inpuUserLoginIDVO =    userDAO.loadUserDetailsByLoginId(con, passbookSearchOthersRequestVO.getData().getUser());
					 if(BTSLUtil.isNullObject(inpuUserLoginIDVO)  ) {
						 throw new BTSLBaseException("PretupsUIReportsController", "passbookOthers download",
									PretupsErrorCodesI.INVALID_USER_LOGINID, 0, null);
						 
					 }
					 passbookOthersReqDTO.setUser(inpuUserLoginIDVO.getUserID()); // Filter User ID
			}
			
			if(passbookOthersReqDTO.getUser()!=null && !passbookOthersReqDTO.getUser().equals(PretupsI.ALL)){
			ChildUserVO childUserVO =	userDAO.checkUserUnderLoggedInUserCategory(con, channelUserVO.getUserID(), passbookOthersReqDTO.getUser());
			   if(null==childUserVO) {
					 throw new BTSLBaseException("PretupsUIReportsController", "passbookOthers download",
								PretupsErrorCodesI.USER_NOT_UNDER_LOGGEDINCATEGORY, 0, null);
			   }
			   
			   if(passbookSearchOthersRequestVO.getData().getCategory()!=null && !passbookSearchOthersRequestVO.getData().getCategory().equals(PretupsI.ALL) && !childUserVO.getUserCategory().equals(passbookSearchOthersRequestVO.getData().getCategory()) ) {
					 throw new BTSLBaseException("PretupsUIReportsController", "passbookOthers download",
								PretupsErrorCodesI.USER_NOT_UNDER_SELECT_CATEGORY, 0, null);
			   }
			
			
			}
			
					
			
			passbookOthersReqDTO.setProduct(passbookSearchOthersRequestVO.getData().getProduct());
			passbookOthersReqDTO.setFromDate(passbookSearchOthersRequestVO.getData().getFromDate());
			passbookOthersReqDTO.setToDate(passbookSearchOthersRequestVO.getData().getToDate());
			passbookOthersReqDTO.setDispHeaderColumnList(passbookSearchOthersRequestVO.getData().getDispHeaderColumnList());
			passbookOthersReqDTO.setUserId(channelUserVO.getUserID()); // Logged in USerID
			passbookOthersReqDTO.setNetworkCode(channelUserVO.getNetworkID());
			passbookOthersReqDTO.setMsisdn(msisdn);
			String fileType = passbookSearchOthersRequestVO.getData().getFileType();
			String allowedFileType = fileType;
		if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			passbookOthersReqDTO.setFileType(allowedFileType);
			
			pretupsUIReportsServiceI.downloadPassbookOthersData(passbookOthersReqDTO,response);
			

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#passbookSearch");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}
		
		boolean reportOffline=  (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_OFFLINE);
		
        if(!reportOffline) {
        	if(response.getStatus().equals(String.valueOf(HttpServletResponse.SC_OK))){
        		InputStream is;
        		InputStreamResource onlineFileResource=null;
        		HttpHeaders responseheaderdata=null;
        		boolean errorOccuredOnlineDownload =false;
				try {
					is = Files.newInputStream(Paths.get(response.getFilePath()),StandardOpenOption.DELETE_ON_CLOSE);
		     		 onlineFileResource = new InputStreamResource(is);
		     		 responseheaderdata = CommonReportWriter.setDownloadFileHeaders(response.getFilePath());
     			} catch (IOException e) {
     				errorOccuredOnlineDownload=true;
				}finally {
					response.setFilePath(null);
				}
				if(!errorOccuredOnlineDownload) {
					return ResponseEntity.ok().headers(responseheaderdata).body(onlineFileResource);  // offline initia
				}else {
					String onlineDwldfail = Integer.toString(PretupsI.RESPONSE_FAIL);
					response.setStatus(onlineDwldfail);
					response.setMessageCode(PretupsErrorCodesI.FAILED);
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.FAILED, null);
					return ResponseEntity.ok().body(response);  // offline initia
				}
				
        	}else {
        		return ResponseEntity.ok().body(response);  // offline initia
        	}
        			
		} else { // offline scenario
			return ResponseEntity.ok().body(response);  // offline initia
		}
		
		
		
		
	    
	}
	
	
	
	
	@PostMapping(value = "/bulkuserAdditionStatusRpt", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Bulk user addition status report", response = BulkUserAddStatusRptResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BulkUserAddStatusRptResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${bulkuserAdditionStatusRpt.summary}", description="${bulkuserAdditionStatusRpt.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BulkUserAddStatusRptResp.class))
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



	public BulkUserAddStatusRptResp bulkuserAdditionStatusRpt(@RequestBody BulkUserAddRptReqVO bulkUserAddRptReqVO,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		
		final String methodName = "bulkuserAdditionStatusRpt";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		BulkUserAddStatusRptResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		UserDAO userDAO= new UserDAO();

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new BulkUserAddStatusRptResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ChannelUserVO channelUserVO=null;
			String msisdn = oAuthUser.getData().getMsisdn();
			String loginID = oAuthUser.getData().getLoginid();
			
			
			channelUserVO= userDAO.loadAllUserDetailsByLoginID(con, loginID);
	    	if(!PretupsI.CATEGORY_TYPE_OPT.equals(channelUserVO.getDomainID())){
	    		channelUserVO= channelUserDAO.loadChannelUserDetails(con, msisdn);
	    	}
			
			
			
			
			String extNtCode =channelUserVO.getNetworkID();
			String fromDate = bulkUserAddRptReqVO.getData().getFromDate();
			String toDate = bulkUserAddRptReqVO.getData().getToDate();
			if(bulkUserAddRptReqVO.getData().getReqTab().equals(PretupsI.BULKUSER_ADVANCEDTAB_REQ)){
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));

			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("HomeScreenTransactionCntroller", "C2CTransfercomm",
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}

			}
			if (BTSLUtil.isNullString(extNtCode) || !extNtCode.equals(channelUserVO.getNetworkID())) {
				throw new BTSLBaseException("HomeScreenTransactionController", "C2CTransfercomm",
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);			
				}
		
			BulkUserAddRptReqDTO  bulkUserAddRptReqDTO = new BulkUserAddRptReqDTO();
			bulkUserAddRptReqDTO.setLocale(locale);
			bulkUserAddRptReqDTO.setExtnwcode(extNtCode);
			if(bulkUserAddRptReqVO.getData().getReqTab().equals(PretupsI.BULKUSER_ADVANCEDTAB_REQ)){	
			bulkUserAddRptReqDTO.setFromDate(fromDate);
			bulkUserAddRptReqDTO.setToDate(toDate);
			bulkUserAddRptReqDTO.setDomain(bulkUserAddRptReqVO.getData().getDomain());
			bulkUserAddRptReqDTO.setGeography(bulkUserAddRptReqVO.getData().getGeography());
			}else {
				bulkUserAddRptReqDTO.setBatchNo(bulkUserAddRptReqVO.getData().getBatchNo());
			}
			bulkUserAddRptReqDTO.setReqTab(bulkUserAddRptReqVO.getData().getReqTab());
			bulkUserAddRptReqDTO.setUserId(channelUserVO.getUserID());
			/*
			List<BulkUserAddRptRecordVO> bulkUserAddStatusRptList = new ArrayList<BulkUserAddRptRecordVO>();
			BulkUserAddRptRecordVO bulkUserAddRptRecordVO = new BulkUserAddRptRecordVO();
			bulkUserAddRptRecordVO.setBatchNo("NGCB200403.001");
			bulkUserAddRptRecordVO.setBatchStatus("Open");
			bulkUserAddRptRecordVO.setTotalRecords("1");
			bulkUserAddRptRecordVO.setNewRecords("1");
			bulkUserAddRptRecordVO.setActiveRecords("0");
			bulkUserAddRptRecordVO.setRejectedRecords("0");
			bulkUserAddStatusRptList.add(bulkUserAddRptRecordVO);
			bulkUserAddRptRecordVO.setBatchNo("NGCB200403.002");
			bulkUserAddRptRecordVO.setBatchStatus("Open");
			bulkUserAddRptRecordVO.setTotalRecords("1");
			bulkUserAddRptRecordVO.setNewRecords("1");
			bulkUserAddRptRecordVO.setActiveRecords("0");
			bulkUserAddRptRecordVO.setRejectedRecords("0");
			bulkUserAddStatusRptList.add(bulkUserAddRptRecordVO);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			//response.setBulkUserAddStatusRptList(bulkUserAddStatusRptList);
			String resmsg = RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg); */
			
			pretupsUIReportsServiceI.searchBulkUserAddStatus(bulkUserAddRptReqDTO,channelUserVO, response);

			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#c2ctransferCommission");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response; 
		
	} 





	@PostMapping(value = "/downloadBulkUserStatusRpt", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Bulk user addition status  report download", response = O2CTransferDetailDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = O2CTransferDetailDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadBulkUserStatusRpt.summary}", description="${downloadBulkUserStatusRpt.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BulkuserAddStsDownloadResp.class))
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



	public BulkuserAddStsDownloadResp downloadBulkUserStatusRpt(@RequestBody BulkUserAddStsDownlodReq bulkUserAddStsDownlodReq,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag) {

		final String methodName = "c2CtransferCommissionRptDownload";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		BulkuserAddStsDownloadResp response = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			response = new BulkuserAddStsDownloadResp();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String extNtCode = channelUserVO.getExternalCode();
			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			BulkUserAddRptReqDTO  bulkUserAddRptReqDTO = new BulkUserAddRptReqDTO();
			bulkUserAddRptReqDTO.setFileType(bulkUserAddStsDownlodReq.getData().getFileType());
			bulkUserAddRptReqDTO.setBatchNo(bulkUserAddStsDownlodReq.getData().getBatchNo());
			String fileType = bulkUserAddRptReqDTO.getFileType();
			String allowedFileType = fileType;
			if (BTSLUtil.isNullorEmpty(fileType)) {
				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			}
			bulkUserAddRptReqDTO.setFileType(allowedFileType);
			bulkUserAddRptReqDTO.setMsisdn(msisdn);
			bulkUserAddRptReqDTO.setUserId(channelUserVO.getUserID());
			bulkUserAddRptReqDTO.setLocale(locale);
            pretupsUIReportsServiceI.downloadBulkUserAddStsDetails(bulkUserAddRptReqDTO,channelUserVO, response);
		
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			response.setMessageCode(msg);
			response.setErrorMap(errorMap);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#downloadO2ctransferdetailsRpt");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}

		return response;
		

	}
	
	

	
	
	@GetMapping(value = "/downloadLargeOfflineReportByTaskID", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Offline report download", response = OfflineFileDownloadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = OfflineFileDownloadResp.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
    */

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadLargeOfflineReportByTaskID.summary}", description="${downloadLargeOfflineReportByTaskID.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResponseEntity.class))
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



	public ResponseEntity<Resource> downloadLargeOfflineReportByTaskID(
    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.REPORT_TASK_ID, example = "",required = true)
			@RequestParam("reportTaskID") String reportTaskID,
			 HttpServletResponse responseSwag
					)throws IOException{
	//public ResponseEntity<String> downloadFile(@RequestBody Input input) throws IOException {
		String offlineDownloadLocation = SystemPreferences.OFFLINERPT_DOWNLD_PATH;
		OfflineReportDAO offlineReportDAO = new OfflineReportDAO();
		final String methodName = "downloadOfflineReportByTaskID";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		ChannelUserDAO channelUserDAO = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		OfflineFileDownloadResp offlineFileDownloadResp = new OfflineFileDownloadResp();
		InputStream is =null;
		try {
			errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			OfflineReportTaskIDInfo offlineReportTaskIDInfo =	offlineReportDAO.getOfflineReportTaskStatusInfo(con, reportTaskID);
			
			 if(offlineReportTaskIDInfo==null) {
				 throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
							PretupsErrorCodesI.INVALID_REPORT_TASKID);
			 }
//			 offlineDownloadLocation="D:\\downloadedReports\\";
		//File file = new File(offlineDownloadLocation +offlineReportTaskIDInfo.getFileName());
			 Path file = null;
			 Resource resource=null;
		try {
//             file = Paths.get(offlineDownloadLocation)
//                             .resolve(offlineReportTaskIDInfo.getFileName());
          file = Paths.get(offlineDownloadLocation)
          .resolve(offlineReportTaskIDInfo.getFileName());
             resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
            
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }

	  	Path newPath = resource.getFile().toPath();
		        char dotchar ='.';
	        int dot = offlineReportTaskIDInfo.getFileName().lastIndexOf(dotchar);
	        String filenName = offlineReportTaskIDInfo.getFileName().substring(0, dot);
	        offlineFileDownloadResp.setFileName(filenName);
	        String fileExtension = offlineReportTaskIDInfo.getFileName().substring(dot+1, offlineReportTaskIDInfo.getFileName().length());
	        offlineFileDownloadResp.setFileType(fileExtension);
	        offlineReportDAO.updateOfflineReportTaskStatus(con, PretupsI.OFFLINE_STATUS_DOWNLOADED, reportTaskID, null, false);
	        String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
	        offlineFileDownloadResp.setStatus(success);
	        offlineFileDownloadResp.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			offlineFileDownloadResp.setMessage(resmsg);
			 is = Files.newInputStream(Paths.get(offlineDownloadLocation+filenName+"."+fileExtension));
			    //Do something with is
				  InputStreamResource csvSummaryResource = new InputStreamResource(is);
		            HttpHeaders csvSummaryHeaders = CommonReportWriter.setDownloadFileHeaders(offlineDownloadLocation+filenName+"."+fileExtension);
		            return ResponseEntity.ok().headers(csvSummaryHeaders).body(csvSummaryResource);
			
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				offlineFileDownloadResp.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			offlineFileDownloadResp.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}

			masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
			String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
			errorMap.setMasterErrorList(masterErrorLists);

			offlineFileDownloadResp.setMessageCode(msg);
			offlineFileDownloadResp.setErrorMap(errorMap);
			
			

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			String fail = Integer.toString(PretupsI.RESPONSE_FAIL);
			offlineFileDownloadResp.setStatus(fail);
			offlineFileDownloadResp.setMessageCode("error.general.processing");
			offlineFileDownloadResp.setMessage(
					"Due to some technical reasons, your request could not be processed at this time. Please try later");

			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("PretupsUIReportsController#downloadO2ctransferdetailsRpt");
				mcomCon = null;
			}
//			if(is!=null) {
//				is.close();	
//			}
			if (log.isDebugEnabled()) {
				log.debug("passbookView", " Exited ");
			}
		}
		
		return null;

	}		
	 
	

@GetMapping(value= "/CUbulkStatusChangeTemplate", produces = MediaType.APPLICATION_JSON)	
	@ResponseBody
	/*@ApiOperation(value = "Channel User Bulk Status Change Template File Download",
	           response = FileDownloadResponse.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response =FileDownloadResponse.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
*/

@io.swagger.v3.oas.annotations.Operation(summary = "${CUbulkStatusChangeTemplate.summary}", description="${CUbulkStatusChangeTemplate.description}",

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
						)
				}),
				@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
						@io.swagger.v3.oas.annotations.media.Content(
								mediaType = "application/json",
								array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
						)
				}),
				@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
						@io.swagger.v3.oas.annotations.media.Content(
								mediaType = "application/json",
								array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
						)
				}),
				@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
						@io.swagger.v3.oas.annotations.media.Content(
								mediaType = "application/json",
								array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
						)
				})
		}
)



public FileDownloadResponse bulkStatusChangeTemplateDownload(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag,HttpServletRequest request) throws  Exception {
	
			final String methodName = "bulkStatusChangeTemplateDownload";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			
			 String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	         String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	         Locale locale = new Locale(lang, country);
			
			 String fileExt = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.STATUS_CHANGE_BATCH_FILEEXT);
			 FileDownloadResponse fileDownloadResponse=new FileDownloadResponse();
			 try {
				   
					OAuthenticationUtil.validateTokenApi(headers);
					final ArrayList dataList = new ArrayList();
			        String filePath = null;
			        String fileName = null;
		            String fileArr[][] = null;
			        try {
	                  filePath = Constants.getProperty("DownloadBatchChangeStatusFilePath");
		              try {
		                final File fileDir = new File(filePath);
	                    if (!fileDir.isDirectory()) {
 	                        fileDir.mkdirs();
	                    }
		                } catch (Exception e) {
		                	log.errorTrace("loadDownloadFile", e);
		                	log.error("loadDownloadFile", "Exception" + e.getMessage());
		                    throw new BTSLBaseException(PretupsUIReportsController.class.getName(),
		                    		methodName,
									PretupsErrorCodesI.CANNOT_BE_PROCESSED);
		               }
			           fileName = Constants.getProperty("DownloadBatchChangeUserListFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() +"."+fileExt;
			           LookupsVO lookUpVO = null;
			           List<LookupsVO> lookUpCodeList = new ArrayList<LookupsVO>();
	                   lookUpCodeList = LookupsCache.getLookupList(PretupsI.ALLOWED_USER_STATUS);
	                   
	                   int k =0,l=-1,m=-1;
	                   for (k=0; k < lookUpCodeList.size();k++){
							if (((LookupsVO) lookUpCodeList.get(k)) != null) {
								if (((LookupsVO) lookUpCodeList.get(k)).getLookupCode()
										.equals(PretupsI.USER_STATUS_DEACTIVATED))
									l = k;
								if (((LookupsVO) lookUpCodeList.get(k)).getLookupCode()
										.equals(PretupsI.USER_STATUS_PREACTIVE))
									m = k;
							}
			          }
			                  
	                  if(l>=0) lookUpCodeList.remove(l);
	                  
	                  if(m>=0) lookUpCodeList.remove(m);
			                 
	                int i = 0;

	                final int filelength = lookUpCodeList.size();

	                final int cols = 3;
	                final int rows = filelength + 3;
	                fileArr = new String[rows][cols]; // ROW-COL
	                fileArr[i][0] = "Status Code";
	                fileArr[i][1] = "Status Name";
	                fileArr[i][2] = "";
	                i++;
	                final Iterator itr1 = lookUpCodeList.iterator();
	                while (itr1.hasNext()) {
	                    lookUpVO = (LookupsVO) itr1.next();
	                    if ("Y".equals(lookUpVO.getStatus())) {
	                        fileArr[i][0] = lookUpVO.getLookupCode();
	                        fileArr[i][1] = lookUpVO.getLookupName();
	                    }
	                    i++;
	                }
	                fileArr[i][0] = "";
	                fileArr[i][1] = "";
	                i++;
	                fileArr[i][0] =  "Mobile No.*";
	                fileArr[i][1] =  "Status*";
	                fileArr[i][2] =  "Remarks*";
	                fileArr = this.convertTo2dArray(fileArr, dataList, rows);
					String noOfRowsInOneTemplate;
					noOfRowsInOneTemplate = Constants
							.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");
					if ("csv".equals(fileExt)) {
					FileWriteUtil.writeinCSV(ExcelFileIDI.STATUS_CHANGE_BATCH_FILEEXT,
							fileArr, filePath + "" + fileName);
					} else if ("xls".equals(fileExt)) {
					FileWriteUtil.writeinXLS(ExcelFileIDI.STATUS_CHANGE_BATCH_FILEEXT,
							fileArr, filePath + "" + fileName,
								noOfRowsInOneTemplate, 1);
					} else if ("xlsx".equals(fileExt)) {
					FileWriteUtil.writeinXLSX(ExcelFileIDI.STATUS_CHANGE_BATCH_FILEEXT,
							fileArr, filePath + "" + fileName,
								noOfRowsInOneTemplate, 1);
					} else {
						throw new BTSLBaseException(
								PretupsUIReportsController.class.getName(),
								methodName,
								PretupsErrorCodesI.FILE_FORMAT_NOT_SUPPORTED,
								new String[] { fileExt });
					}
					
					File fileNew = new File(filePath + "" + fileName);
					byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
					String encodedString = Base64.getEncoder().encodeToString(fileContent);
					String file1 = fileNew.getName();
					fileDownloadResponse.setFileattachment(encodedString);
					fileDownloadResponse.setFileType(fileExt);
					fileDownloadResponse.setFileName(file1);
					fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
					fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
					String resmsg  = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
					fileDownloadResponse.setMessage(resmsg);
                    return fileDownloadResponse;
			        }catch (Exception e) {
			        	log.errorTrace("writing to a file", e);
	                	log.error("writing to a file", "Exception" + e.getMessage());
			        	throw new BTSLBaseException(PretupsUIReportsController.class.getName(),
	                    		methodName,
								PretupsErrorCodesI.CANNOT_BE_PROCESSED);
			        }
				}catch (BTSLBaseException be) {
					log.error(methodName, "Exception:e=" + be);
					log.errorTrace(methodName, be);
					if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
						responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
						fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
					} else {
						responseSwag.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
						fileDownloadResponse.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
					}
					fileDownloadResponse.setMessageCode(be.getMessageKey());
					fileDownloadResponse.setMessage(RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs()));
						
			   } 
			 return fileDownloadResponse;
    }
	private String[][] convertTo2dArray(String[][] p_fileArr, ArrayList dataList, int p_rows) {
	    if (log.isDebugEnabled()) {
	    	log.debug("convertTo2dArray", "Entered p_fileArr=" + p_fileArr + "dataList=" + dataList);
	    }
	    try {
	
	        String key = null;
	        ChannelUserVO channelUserVO = null;
	        int rows = 0;
	        int cols;
	        int dataListSize = dataList.size();
	        for (int i = 0; i < dataListSize; i++) {
	            key = (String.valueOf(i + 1));
	            channelUserVO = (ChannelUserVO) dataList.get(i);
	
	            {
	                rows++;
	                if (rows >= p_rows) {
	                    break;
	                }
	                cols = 0;
	                p_fileArr[rows][cols++] = channelUserVO.getMsisdn();
	
	                p_fileArr[rows][cols++] = channelUserVO.getUserProfileID();
	                p_fileArr[rows][cols++] = "";
	            }
	
	        }
	    } catch (Exception e) {
	        log.error("convertTo2dArray", "Exceptin:e=" + e);
	        log.errorTrace("convertTo2dArray", e);
	    } finally {
	        if (log.isDebugEnabled()) {
	        	log.debug("convertTo2dArray", "Exited p_fileArr=" + p_fileArr);
	        }
	    }
	    return p_fileArr;
	}

}

