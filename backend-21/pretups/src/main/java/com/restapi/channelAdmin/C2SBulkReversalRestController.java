package com.restapi.channelAdmin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.spring.custom.action.Globals;
import com.btsl.util.MessageResources;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.Batchc2sRevEntryVO;
import com.btsl.user.businesslogic.NumberConstants;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.xl.ExcelRW;
import com.restapi.c2s.services.BulkC2SReverseProcessor;
import com.restapi.c2s.services.C2SRechargeReversalDetails;
import com.restapi.c2s.services.C2SRechargeReversalRequestVO;
import com.restapi.channelAdmin.requestVO.C2SBulkReversalRequestVO;
import com.restapi.channelAdmin.responseVO.C2SBulkReversalResponseVO;
import com.restapi.channelAdmin.service.C2SBulkReversalServiceImpl;
import com.restapi.channelAdmin.serviceI.C2SBulkReversalService;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2SBulkReversalRestController.name}", description = "${C2SBulkReversalRestController.desc}")//@Api(tags = "C2S Bulkreversal Services", defaultValue = "C2S Bulkreversal Services")
@RestController
@RequestMapping(value = "/v1/c2sbulkreverse")
public class C2SBulkReversalRestController {

	private static final Log LOG = LogFactory.getLog(C2SBulkReversalRestController.class.getName());

	@GetMapping(path = "/downloadtemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody



	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadtemplate.summary}", description="${downloadtemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SBulkReversalResponseVO.class))
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


	public C2SBulkReversalResponseVO downloadTemplate(HttpServletRequest request,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response)
			throws BTSLBaseException, IOException {
		final String METHOD_NAME = "downloadTemplate";
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered");
		String fileArr[][] = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		C2SBulkReversalResponseVO c2sBulkReversalResponse = new C2SBulkReversalResponseVO();
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		File fileNew=null;

		try {
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response);

			// Call the ExcelWrite Method.. & write in XLS file for Master Data Creation.
			String filePath = Constants.getProperty("DownloadBatchC2SReversalPath");
			try {
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory())
					fileDir.mkdirs();
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				LOG.error(METHOD_NAME, "Exception" + e.getMessage());
				throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.DIR_NOT_CREATED);
			}
			ExcelRW excelRW = new ExcelRW();
			String fileName = Constants.getProperty("DownloadBatchC2SREVNamePrefix")
					+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";
			fileArr = new String[1][1]; // ROW-COL
			fileArr[0][0] = "batchc2s.rev.xlsheading.label.txnID";

			excelRW.writeExcel(ExcelFileIDI.BATCH_C2S_TXN_REV, fileArr,
					((MessageResources) request.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(request),
					filePath + "" + fileName);

			 fileNew = new File(filePath + "" + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			c2sBulkReversalResponse.setFileName(fileNew.getName());
			c2sBulkReversalResponse.setFileType("xls");
			c2sBulkReversalResponse.setFileattachment(encodedString);
			c2sBulkReversalResponse.setStatus(200);
			return c2sBulkReversalResponse;
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				c2sBulkReversalResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				c2sBulkReversalResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

			String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), be.getMessage(), null);
			c2sBulkReversalResponse.setMessageCode(be.getMessage());
			c2sBulkReversalResponse.setMessage(resmsg);
			c2sBulkReversalResponse.setStatus(PretupsI.RESPONSE_SUCCESS);

		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			c2sBulkReversalResponse.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(METHOD_NAME, e);
			LOG.error(METHOD_NAME, "Unable to write data into a file Exception = " + e.getMessage());
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exited");
			}
			if(fileNew.exists()) {
				fileNew.delete();
			}
		}
		return c2sBulkReversalResponse;
	}

	@PostMapping(path = "/confirmUpload", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2S Bulk reversal VO", response = C2SBulkReversalResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2SBulkReversalResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${confirmUpload.summary}", description="${confirmUpload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SBulkReversalResponseVO.class))
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


	public C2SBulkReversalResponseVO confirmUploadBatchC2SReversal(HttpServletRequest request,
			HttpServletResponse response, @RequestBody C2SBulkReversalRequestVO req,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers) {
		final String METHOD_NAME = "confirmUploadBatchC2SReversal";
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered");

		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		C2SBulkReversalResponseVO c2sBulkReversalResponse = new C2SBulkReversalResponseVO();
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);

		try {
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response);
			C2SBulkReversalService service = new C2SBulkReversalServiceImpl();
			service.confirmUploadRequest(req);
		} catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				c2sBulkReversalResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				c2sBulkReversalResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

			String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), be.getMessage(), null);
			c2sBulkReversalResponse.setMessageCode(be.getMessage());
			c2sBulkReversalResponse.setMessage(resmsg);
		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			c2sBulkReversalResponse.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);

		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug(METHOD_NAME, "Exited");
		}

		return c2sBulkReversalResponse;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2S Bulk reversal VO", response = C2SBulkReversalResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = C2SBulkReversalResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${testendpoint.summary}", description="${testendpoint.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SBulkReversalResponseVO.class))
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



	public C2SBulkReversalResponseVO process(HttpServletRequest request, HttpServletResponse response,
			@RequestBody C2SBulkReversalRequestVO req,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers) {
		final String METHOD_NAME = "process";
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered");

		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		C2SBulkReversalResponseVO c2sBulkReversalResponse = new C2SBulkReversalResponseVO();
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);

		Connection con = null;
		MComConnectionI mcomCon = null;
		ProcessStatusVO processVO = null;
		boolean processRunning = true;
		int totalRecords = 0;
		int rejectedRecords = 0;
		int successRecords = 0;

		try {
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response);

			UserDAO userDao = new UserDAO();
			String loginId = oAuthUser.getData().getLoginid();
			String msisdn = oAuthUser.getData().getMsisdn();

//		        //Check the process status..
			ProcessBL processBL = new ProcessBL();
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				processVO = processBL.checkProcessUnderProcess(con, PretupsI.BATCH_C2S_REV_PROCESS_ID);
			} catch (BTSLBaseException e) {
				LOG.error(METHOD_NAME, "Exception:e=" + e);
				LOG.errorTrace(METHOD_NAME, e);
				processRunning = false;
			}
			// If the process is already running forward the control to waiting screen.
			if (processVO != null && !processVO.isStatusOkBool()) {
				processRunning = false;
				c2sBulkReversalResponse.setMessage("waitingProcess");
				c2sBulkReversalResponse.setProcStatus("underProcess");
				c2sBulkReversalResponse.setTransactionId(req.getBatchName());
				c2sBulkReversalResponse.setStatus(200);
				return c2sBulkReversalResponse;
			}
			// If The process is not running commit the connection to update Process status

			mcomCon.partialCommit();
			
			if (BTSLUtil.isNullString(req.getBatchName())) {
				throw new BTSLBaseException(this, METHOD_NAME,
						PretupsErrorCodesI.BATCH_NAME_EMPTY);
			}
			
			
			if(!BTSLUtil.isNullString(req.getBatchName()) && BTSLUtil.isContainsSpecialCharacters(req.getBatchName())) {
				throw new BTSLBaseException(this, METHOD_NAME,
						PretupsErrorCodesI.BATCH_NAME_SPECIALCHAR_NOT_ALLOWED);
			}

			if (req.getFileName().length() > 200)
			throw new BTSLBaseException(this, METHOD_NAME,
					PretupsErrorCodesI.FILE_CANNOT_UPLOAD);
			String dir = Constants.getProperty("DownloadBatchC2SReversal"); // Upload file path
			if (BTSLUtil.isNullString(dir))
				throw new BTSLBaseException(this, METHOD_NAME,
						PretupsErrorCodesI.BATCHREV_VAL_UPLOAD_PATH);
			String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLS);
			String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BATCH_C2S_REV");
			if (BTSLUtil.isNullString(fileSize))
				fileSize = String.valueOf(0);
			// upload file to server
			C2SBulkReversalService service = new C2SBulkReversalServiceImpl();
			boolean isFileUploaded = service.uploadFileToServer(req.getFileName() + "." + req.getFileType(), dir,
					contentType, "loadTempelate", Long.parseLong(fileSize), req.getAttachment());
			if (isFileUploaded) {
				ChannelUserVO senderVO = (ChannelUserVO) userDao.loadAllUserDetailsByLoginID(con, loginId);
				List<C2SRechargeReversalDetails> listC2sRechargeReverdetails = service.processUploadedFile(con,
						senderVO, BTSLUtil.getBTSLLocale(request), req,
						(MessageResources) request.getAttribute(Globals.MESSAGES_KEY));
				totalRecords = listC2sRechargeReverdetails.size();
				if (totalRecords == 0) {
					throw new BTSLBaseException(this, METHOD_NAME,
							PretupsErrorCodesI.BATCHREV_NO_RECORDS_FILE_PROCESS);
				}
				C2SRechargeReversalRequestVO requestVO = new C2SRechargeReversalRequestVO();
				// response = processRequestChannel(requestVO,
				// "rcrev".toUpperCase(),requestIDStr,headers, response1);
				requestVO.setDataRev(listC2sRechargeReverdetails);
				BulkC2SReverseProcessor bulkC2SReverseProcessor = new BulkC2SReverseProcessor();
				requestVO.setMsisdn(msisdn);
				
				ChannelUserVO senderData = new ChannelUserDAO().loadChannelUserDetails(con, msisdn);
				if(senderData!=null) {
					requestVO.setPin1(BTSLUtil.decryptText(senderData.getUserPhoneVO().getSmsPin()));
				}
				if(req.getBatchName()!=null && req.getBatchName().length()>NumberConstants.N30.getIntValue() ) {
					throw new BTSLBaseException(this, METHOD_NAME,
							PretupsErrorCodesI.BATCHNAME_EXCEED_LIMIT);
				}
				
				BaseResponseMultiple baseResponseMultiple = bulkC2SReverseProcessor.processRequestChannel(requestVO,
						"RCREV", "0", headers, response);

				if (baseResponseMultiple.getErrorMap()!=null &&   (baseResponseMultiple.getErrorMap().getRowErrorMsgLists() != null
						&& baseResponseMultiple.getErrorMap().getRowErrorMsgLists().size() > 0)) {
					rejectedRecords = baseResponseMultiple.getErrorMap().getRowErrorMsgLists().size();
					writeFileForResponse(c2sBulkReversalResponse, baseResponseMultiple.getErrorMap());
				} else {
					c2sBulkReversalResponse.setMessageCode(baseResponseMultiple.getMessageCode());
					c2sBulkReversalResponse.setMessage(baseResponseMultiple.getMessage());
					
				}
				c2sBulkReversalResponse.setStatus(Integer.parseInt(baseResponseMultiple.getStatus()));
				successRecords = totalRecords - rejectedRecords;
				String[] arr = null;
				arr= new String[1];
				arr[0]=req.getBatchName();
				if(totalRecords>0 && totalRecords==successRecords) {
					response.setStatus(HttpStatus.SC_OK);
					c2sBulkReversalResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
					c2sBulkReversalResponse.setMessageCode(PretupsErrorCodesI.BATCHC2S_REVERSAL_FULL_SUCCESS);
					String resmsg = RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
									(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
							PretupsErrorCodesI.BATCHC2S_REVERSAL_FULL_SUCCESS , arr);
					c2sBulkReversalResponse.setMessage(resmsg);
					
				}else if(successRecords>0 && successRecords<totalRecords) {
					response.setStatus(HttpStatus.SC_OK);
					c2sBulkReversalResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
					c2sBulkReversalResponse.setMessageCode(PretupsErrorCodesI.BATCHC2S_REVERSAL_PARTIAL_SUCCESS);
					String resmsg = RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
									(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
							PretupsErrorCodesI.BATCHC2S_REVERSAL_PARTIAL_SUCCESS , arr);
					c2sBulkReversalResponse.setMessage(resmsg);
				}else if (totalRecords==rejectedRecords) {
					
					response.setStatus(HttpStatus.SC_OK);
					c2sBulkReversalResponse.setStatus(PretupsI.RESPONSE_FAIL);
					c2sBulkReversalResponse.setMessageCode(PretupsErrorCodesI.BATCHC2S_REVERSAL_ALL_FAILURE);
					String resmsg = RestAPIStringParser.getMessage(
							new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
									(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
							PretupsErrorCodesI.BATCHC2S_REVERSAL_ALL_FAILURE , arr);
					c2sBulkReversalResponse.setMessage(resmsg);
					
					
				}
				
				c2sBulkReversalResponse.setTotalRecords(totalRecords);
				c2sBulkReversalResponse.setSuccessRecords(successRecords);
				c2sBulkReversalResponse.setRejectedRecords(rejectedRecords);
					Batchc2sRevEntryVO batchc2sRevEntryVO = new Batchc2sRevEntryVO();
					BatchUserWebDAO batchUserWebDAO  = new BatchUserWebDAO ();
					batchc2sRevEntryVO.setBatchSize(totalRecords);
					batchc2sRevEntryVO.setBatchName(req.getBatchName());
					batchc2sRevEntryVO.setCreatedBy(senderVO.getUserID());
					batchc2sRevEntryVO.setStatus(PretupsI.USR_BATCH_STATUS_OPEN);
					batchc2sRevEntryVO.setFileName(req.getFileName()+"." + req.getFileType());
					batchc2sRevEntryVO.setRejectedRecords(rejectedRecords);
					batchc2sRevEntryVO.setApprovedRecords(successRecords);
					batchUserWebDAO.addbatchRev(con, batchc2sRevEntryVO, senderVO);
			} else
			throw new BTSLBaseException(this, METHOD_NAME,
					PretupsErrorCodesI.FILE_CANNOT_UPLOAD);

		} catch (Exception be) {

			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				c2sBulkReversalResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				c2sBulkReversalResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			try {
				if (con != null) {
					mcomCon.partialRollback();
				}
			} catch (SQLException e1) {
				LOG.errorTrace(METHOD_NAME, e1);
			} catch (Exception e2) {
				LOG.errorTrace(METHOD_NAME, e2);
			}

			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), be.getMessage(), null);
			c2sBulkReversalResponse.setMessageCode(be.getMessage());
			c2sBulkReversalResponse.setMessage(resmsg);
		} finally {
			// At the end of the process Update the process status as C. The process ststus
			// will be
			// Under process: U only if the system crashes. Even if any exception occurs the
			// process
			// status will be updated as C.
			if (processRunning) {
				try {
					processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
					ProcessStatusDAO processDAO = new ProcessStatusDAO();
					if (processDAO.updateProcessDetail(con, processVO) > 0)

						mcomCon.finalCommit();
					else

						mcomCon.finalRollback();
				} catch (Exception e) {
					if (LOG.isDebugEnabled())
						LOG.error(METHOD_NAME,
								" Exception in update process detail for transfer rules creation " + e.getMessage());
					LOG.errorTrace(METHOD_NAME, e);
				}
			}
			if (mcomCon != null) {
				mcomCon.close("C2SBulkReversalRestController#process");
				mcomCon = null;
			}
			if (LOG.isDebugEnabled())
				LOG.debug(METHOD_NAME, "Exited");
		}

		return c2sBulkReversalResponse;
	}

	private void writeFileForResponse(C2SBulkReversalResponseVO response, ErrorMap errorMap)
			throws BTSLBaseException, IOException, ParseException {
		if (errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
			return;
		List<List<String>> rows = new ArrayList<>();
		for (int i = 0; i < errorMap.getRowErrorMsgLists().size(); i++) {
			RowErrorMsgLists rowErrorMsgList = errorMap.getRowErrorMsgLists().get(i);
			for (int col = 0; col < rowErrorMsgList.getMasterErrorList().size(); col++) {
				MasterErrorList masterErrorList = rowErrorMsgList.getMasterErrorList().get(col);
				rows.add((Arrays.asList(rowErrorMsgList.getRowName(), rowErrorMsgList.getRowValue(),
						masterErrorList.getErrorMsg())));
			}

		}
		String filePathCons = Constants.getProperty("DownloadErLogFilePath");
		C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
		c2CFileUploadApiControllerObject.validateFilePathCons(filePathCons);
		String filePathConstemp = filePathCons + "temp/";
		c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
		String filepathtemp = filePathConstemp;
		String logErrorFilename = Constants.getProperty("BatchReversalErLog")
				+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
		writeFileCSV(rows, filepathtemp + logErrorFilename);
		File error = new File(filepathtemp + logErrorFilename);
		byte[] fileContent = FileUtils.readFileToByteArray(error);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		response.setFileattachment(encodedString);
		response.setFileName(logErrorFilename);
		response.setErrorMap(errorMap);
	}

	public void writeFileCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
		try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
		csvWriter.append("Line number");
		csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
		csvWriter.append("Transaction ID");
		csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
		csvWriter.append("Reason");
		csvWriter.append("\n");

		for (List<String> rowData : listBook) {
			csvWriter.append(String.join(Constants.getProperty("FILE_SEPARATOR_C2C"), rowData));
			csvWriter.append("\n");
		}
	}
	}

}
