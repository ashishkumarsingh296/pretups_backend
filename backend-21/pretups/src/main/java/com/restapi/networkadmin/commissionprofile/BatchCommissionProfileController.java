package com.restapi.networkadmin.commissionprofile;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.commissionprofile.requestVO.BatchAddCommisionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommProfRespVO;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommisionProfileResponseVO;
import com.restapi.networkadmin.commissionprofile.service.BatchCommissionProfileServiceI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BatchCommissionProfileController.name}", description = "${BatchCommissionProfileController.desc}")//@Api(tags = "Network Admin", defaultValue = "Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin/commissionProfile")
public class BatchCommissionProfileController {

	public static final Log log = LogFactory.getLog(BatchCommissionProfileController.class.getName());
	public static final String classname = "BatchCommissionProfileController";

	@Autowired
	BatchCommissionProfileServiceI batchCommissionProfileServiceI;

	/**
	 * 
	 * @param headers
	 * @param response1
	 * @param domain
	 * @param category
	 * @param batchName
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/batchAddCommisionProfileUploadProcess", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Batch Add CommisionProfile", response = BatchAddCommisionProfileResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = BatchAddCommisionProfileResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${batchAddCommisionProfileUploadProcess.summary}", description="${batchAddCommisionProfileUploadProcess.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchAddCommisionProfileResponseVO.class))
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


	public BatchAddCommisionProfileResponseVO batchAddCommisionProfileUploadProcess(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "batchName", required = true) @RequestParam("batchName") String batchName,
			@RequestBody BatchAddCommisionProfileRequestVO request) throws Exception {

		final String METHOD_NAME = "batchAddCommisionProfileUploadProcess";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}

		BatchAddCommisionProfileResponseVO response = null;
		BatchAddCommisionProfileRequestVO batchAddCommisionProfileRequestVO = request;

		ArrayList<MasterErrorList> inputValidations = new ArrayList();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		boolean isflag = true;
		try {

			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */

			response = new BatchAddCommisionProfileResponseVO();
			response.setFileAttachment(request.getFileAttachment());
			response.setSheetName(request.getFileName());
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			ArrayList<MasterErrorList> inputVal = batchCommissionProfileServiceI
					.basicFileValidations(batchAddCommisionProfileRequestVO, response, locale, inputValidations);
			if (!BTSLUtil.isNullOrEmptyList(inputVal)) {
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setErrorMap(new ErrorMap());
				response.getErrorMap().setMasterErrorList(inputVal);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_NOT_VALID,new String[]{(request.getFileName())});

			} else {

				boolean fileUpload = false;
				fileUpload = batchCommissionProfileServiceI.uploadAndValidateFile(con, mcomCon, loginID, request,
						response, domainCode, categoryCode);
				if (fileUpload) {
					log.debug(METHOD_NAME, PretupsI.FILE_UPLOADED_SUCCESS);

					if (fileUpload) {
						final String dir = Constants.getProperty("UploadBatchModifyCommProfileFilePath");
						String file = request.getFileAttachment();

						response = batchCommissionProfileServiceI.processBulkAddCommissionProf(con, response1,
								request, dir + file, domainCode, categoryCode, batchName, loginID,locale);

					} else {
						throw new BTSLBaseException(classname, METHOD_NAME,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFIL_ERROR_TRYLATER);

					}
				}

			}

		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}

		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close(METHOD_NAME);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

	@GetMapping(value= "/downloadBatchModifyCommissionProfileTemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = PretupsErrorCodesI.DOWNLOAD_BATCH_COMMISSION_PROFILE_TEMPLATE,
			response = BatchAddCommisionProfileResponseVO.class,
			authorizations = {
					@Authorization(value = "Authorization")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = BatchAddCommisionProfileResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request" ),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
	})*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadBatchModifyCommissionProfileTemplate.summary}", description="${downloadBatchModifyCommissionProfileTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchAddCommisionProfileResponseVO.class))
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


	public BatchAddCommisionProfileResponseVO downloadModifyCommissionProfileTemplate(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
																					  HttpServletResponse responseSwag, HttpServletRequest httpServletRequest, @Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
																					  @Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode)throws Exception {

		final String METHOD_NAME =  "DownloadModifyCommissionProfileTemplate";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}
		BatchAddCommisionProfileResponseVO response = null;
		response = new BatchAddCommisionProfileResponseVO();

		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginUserID = null;

		try {

			/*
			 * Authentication
			 *
			 * @throws BTSLBaseException
			 */
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginUserID = OAuthUserData.getData().getLoginid();
			response = batchCommissionProfileServiceI.downloadFileTemplate(con, locale, loginUserID,categoryCode,domainCode,httpServletRequest, responseSwag);

		} catch (BTSLBaseException be) {
			log.error("", "Exception=" + be);
			log.errorTrace(METHOD_NAME, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}finally {
			if (mcomCon != null) {
				mcomCon.close("DownloadModifyCommissionProfileTemplate");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}
	@PostMapping(value= "/uploadFileForBatchModifyCommissionProfile", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = PretupsErrorCodesI.UPLOAD_FILE_FOR_BATCH_MODIFY_COMMISSION_PROFILE,
			response = BatchAddCommisionProfileResponseVO.class,
			authorizations = {
					@Authorization(value = "Authorization")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = BatchAddCommisionProfileResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request" ),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
	})*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${uploadFileForBatchModifyCommissionProfile.summary}", description="${uploadFileForBatchModifyCommissionProfile.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchAddCommisionProfileResponseVO.class))
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


	public BatchAddCommisionProfileResponseVO uploadFileForBatchModifyCommissionProfile(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag,
			@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode,
			@RequestBody BatchAddCommisionProfileRequestVO request){

		final String METHOD_NAME = "uploadFileForBatchModifyCommissionProfile";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}

		BatchAddCommisionProfileResponseVO response = null;
		BatchAddCommisionProfileRequestVO batchAddCommisionProfileRequestVO = request;

		ArrayList<MasterErrorList> inputValidations = new ArrayList();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		boolean isflag = true;

		try {

			/*
			 * Authentication
			 *
			 * @throws BTSLBaseException
			 */

			response = new BatchAddCommisionProfileResponseVO();
			response.setFileAttachment(request.getFileAttachment());
			response.setSheetName(request.getFileName());
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();

			ArrayList<MasterErrorList> inputVal = batchCommissionProfileServiceI
					.basicFileValidations(batchAddCommisionProfileRequestVO, response, locale, inputValidations);

			if (!BTSLUtil.isNullOrEmptyList(inputVal)) {
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setErrorMap(new ErrorMap());
				response.getErrorMap().setMasterErrorList(inputVal);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				final String[] args = {String.valueOf(inputVal.get(0).getErrorMsg())};
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_NOT_VALID,args);
			} else {

				boolean fileUpload = false;
				fileUpload = batchCommissionProfileServiceI.uploadAndValidateFile(con, mcomCon, loginID, request,
						response, domainCode, categoryCode);

				if (fileUpload) {
					log.debug(METHOD_NAME, "file uploaded successfully");

					if (fileUpload) {
						final String dir = Constants.getProperty("UploadBatchModifyCommProfileFilePath");
						String file = request.getFileAttachment();

						response = batchCommissionProfileServiceI.processUploadedFileForCommProfile(con, responseSwag,
								request, dir + file, domainCode, categoryCode, loginID);

					} else {
						throw new BTSLBaseException(classname, METHOD_NAME,
								PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_MISSMATCH_PROFILENAME);

					}
				}

			}


		} catch (BTSLBaseException be) {
			log.error("", "Exception=" + be);
			log.errorTrace(METHOD_NAME, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}
	
	@GetMapping(value = "/downloadBatchAddCommProfTemplate", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Download Commission Profile Template", response = BatchAddCommProfRespVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BatchAddCommProfRespVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadBatchAddCommProfTemplate.summary}", description="${downloadBatchAddCommProfTemplate.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchAddCommProfRespVO.class))
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


	public BatchAddCommProfRespVO downloadDomainListBatchAdd(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseSwag,
			HttpServletRequest request,
			@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode)
			
			throws Exception {

		final String methodName = "downloadDomainListBatchAdd";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		BatchAddCommProfRespVO response = new BatchAddCommProfRespVO();
		UserDAO userDAO = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		try {
			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			loginID = OAuthUserData.getData().getLoginid();

			response = batchCommissionProfileServiceI.downloadFileTemplateBatchAdd(con, locale, loginID, domainCode, categoryCode, request, responseSwag);

		} catch (BTSLBaseException be) {
			log.error("", "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				// String badReq=Integer.toString(HttpStatus.SC_BAD_REQUEST) ;
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);

			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

			}

		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));

			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		} finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting:=" + methodName);
		}

		return response;
	}
}
