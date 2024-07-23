package com.restapi.channelAdmin.bulkUploadOperations;


import com.btsl.common.*;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.transfer.requesthandler.FilesStorageService;
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.bulkUploadOperations.ResponseVO.BulkOperationResponseVO;
import com.restapi.channelAdmin.bulkUploadOperations.requestVO.BulkOperationRequestVO;
import com.restapi.channelAdmin.bulkUploadOperations.service.BulkOperationUploadService;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BulkOperationController.name}", description = "${BulkOperationController.desc}")//@Api(tags = "Bulk Operations", defaultValue = "Bulk Operations")
@RestController
@RequestMapping(value = "/v1/BulkOperations")
public class BulkOperationController {
//	protected final Log _log = LogFactory.getLog(getClass().getName());
public static final Log log = LogFactory.getLog(BulkOperationController.class.getName());
	StringBuilder loggerValue = new StringBuilder();

	@Autowired
	private FilesStorageService filesStorageService;
	@Autowired
	private BulkOperationUploadService bulkOperationUploadService ;
	
	
	
	@PostMapping(value = "/bulkUploadOperations" )
	/*@ApiOperation(value = "Bulk operation actions", response = BulkOperationUploadResp.class, authorizations = {
			@Authorization(value = "Authorization") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${bulkUploadOperations.summary}", description="${bulkUploadOperations.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BulkOperationResponseVO.class))
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

	public BulkOperationResponseVO uploadFile(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																@Parameter(description = "Type", required = true)//allowableValues = "MSISDN,LOGIN")
																@RequestParam("Type") String type,
																@RequestBody BulkOperationRequestVO requestVO, HttpServletResponse response1,
																HttpServletRequest httprequest) throws Exception {
		final String METHOD_NAME = "uploadFile";
		String message = "";
	    Connection con = null;
		MComConnectionI mcomCon = null;
		BulkOperationResponseVO response =	new BulkOperationResponseVO();
		final String methodName = "uploadFile";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser = null;
		MasterErrorList masterError = null;
		ErrorMap errorMap = null;
		ArrayList<MasterErrorList> masterErrorLists = null;
		Map<String,String> mp=null; 
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
	    try {
	    	
	    	errorMap = new ErrorMap();
			masterErrorLists = new ArrayList<>();
	    	mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());
			ChannelUserDAO  channelUserDAO = new ChannelUserDAO();
			UserDAO userDAO = new UserDAO();

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}
			
			
			String actionCode=null;
			
			actionCode=requestVO.getOperationType();
			
			
			String msisdn = oAuthUser.getData().getMsisdn();
			ChannelUserVO channelUserVO= userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());

	    	String bulkOperationTaskID =PretupsI.BULK_PROC_ID+Long.toString(IDGenerator.getNextID(con, TypesI.BULK_PROC_ID, TypesI.ALL, TypesI.ALL, null));
	    	String processFilename = bulkOperationTaskID+"."+  requestVO.getFileType();
	    	String fileNamePath = bulkOperationUploadService.validateUploadedFile(processFilename);
			final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
			HashMap<String, String> fileDetailsMap = null;
			fileDetailsMap = new HashMap<String, String>();
			ReadGenericFileUtil fileUtil = null;
			boolean isFileUploaded = false;
			String forwardpath = "uploadFileForChUserUnregisterBulk";
			fileUtil = new ReadGenericFileUtil();
			fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFile());
			fileDetailsMap.put(PretupsI.FILE_TYPE, contentType);
			final byte[] data = fileUtil.decodeFile(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT));
			final String dir = Constants.getProperty("UploadFileForUnRegChnlUserPath");
			String fileSize = Constants.getProperty("OTHER_FILE_SIZE");
			isFileUploaded = BTSLUtil.uploadFileToServerWithHashMap(fileDetailsMap, dir, contentType, forwardpath, Long.parseLong(fileSize),data, contentType);
	    	CommonUtil  commonUtil = new CommonUtil();
	    	byte[] base64Bytes = commonUtil.decodeFile(requestVO.getFile());
	    	filesStorageService.writeByteArrayToFile(fileNamePath,base64Bytes);
	    	ArrayList loginIDORMsisdnList = bulkOperationUploadService.scanUploadedFile(fileNamePath);
	    	
	     response	=bulkOperationUploadService.processBulkList(loginIDORMsisdnList, type, channelUserVO, fileNamePath, actionCode,processFilename);
	      
	    }catch(BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
	    	if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
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

	    }
	    catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.COULD_NOT_UPLOAD_THE_FILE, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.COULD_NOT_UPLOAD_THE_FILE);
			response1.setStatus(org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST);
	    }
	    finally {
			if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting" + METHOD_NAME);
			}
	  }
	    return response;
	}
	}

