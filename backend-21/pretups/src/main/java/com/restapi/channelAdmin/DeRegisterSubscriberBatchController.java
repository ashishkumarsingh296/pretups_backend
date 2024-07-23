package com.restapi.channelAdmin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.requestVO.DeRegisterSubscriberBatchRequestVO;
import com.restapi.channelAdmin.responseVO.DeRegisterSubscriberBatchResponseVO;
import com.restapi.channelAdmin.service.DeRegisterSubscriberBatchServiceI;

import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${DeRegisterSubscriberBatchController.name}", description = "${DeRegisterSubscriberBatchController.desc}")//@Api(tags ="Channel Admin", value="Channel Admin")
@RestController	
@RequestMapping(value = "/v1/channeladmin")
public class DeRegisterSubscriberBatchController {

	
	public static final Log log = LogFactory.getLog(DeRegisterSubscriberBatchController.class.getName());
	
	
	@Autowired
	private DeRegisterSubscriberBatchServiceI deRegisterSubscriberBatchServiceI;
	
	@PostMapping(value = "/deRegisterSubscriberBatch", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Upload Base64 encoded file for Batch DeRegistration of Subscribers",
					response = DeRegisterSubscriberBatchResponseVO.class,
					authorizations = {
					@Authorization(value = "Authorization") })

	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = DeRegisterSubscriberBatchResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), 
			@ApiResponse(code = 404, message = "Not Found") })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${deRegisterSubscriberBatch.summary}", description="${deRegisterSubscriberBatch.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DeRegisterSubscriberBatchResponseVO.class))
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
	public DeRegisterSubscriberBatchResponseVO deRegisterSubscriberBatch(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestBody DeRegisterSubscriberBatchRequestVO request,
			 HttpServletRequest requestSwag,
			 HttpServletResponse responseSwag
			) throws Exception{
		
		final String methodName =  "deRegisterSubscriberBatch";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		
		DeRegisterSubscriberBatchResponseVO response = new DeRegisterSubscriberBatchResponseVO();
		DeRegisterSubscriberBatchVO deRegisterSubscriberBatchVO = new DeRegisterSubscriberBatchVO();
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		//
		
	 	final String file = request.getFileName();
        BufferedReader br = null;
        String line = null;
        InputStream is = null;
        InputStreamReader inputStreamReader = null;
        
        ReadGenericFileUtil fileUtil = null;
        HashMap<String, String> fileDetailsMap = null;
        boolean isFileUploaded = false;
        boolean isFileProcessed = false;
        Locale locale = null;
        try {
        	
        	mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
        	
			locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			final UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, OAuthUserData.getData().getLoginid());;
			
        	
        	fileUtil = new ReadGenericFileUtil();
        	//adding code for file upload
        	fileDetailsMap = new HashMap<String, String>();
			fileUtil = new ReadGenericFileUtil();
			fileDetailsMap.put(PretupsI.FILE_NAME, request.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, request.getFileAttachment());
			fileDetailsMap.put(PretupsI.FILE_TYPE, request.getFileType());
			validateFileDetailsMap(fileDetailsMap);
        	
        	//adding code for file upload  - ends
        	
            // this section checks for the valid name for the file
            final String fileName = request.getFileName();// accessing
            // name
            // of
            // the
            // file
            final boolean message = BTSLUtil.isValideFileName(fileName);// validating
            // name of the
            // file
            // if not a valid file name then throw exception
            if (!message) {
//                throw new BTSLBaseException(this, "confirmFileUploadForUnReg", "invalid.uploadfile.msg.unsuccessupload", "uploadSubscriberFileForUnReg");
            		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE, PretupsI.RESPONSE_FAIL, null);
            }// akanksha ends
             // Cross site Scripting removal
            //
            //String fileData = request.getFileAttachment();
            //
            
            //final byte[] data = fileData.getBytes();// ak
            
            final byte[] data = fileUtil.decodeFile(request.getFileAttachment());
            is = new ByteArrayInputStream(data);
            inputStreamReader = new InputStreamReader(is);
            br = new BufferedReader(inputStreamReader);
            while ((line = br.readLine()) != null) {
                final boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
                if (!isFileContentValid) {
//                    throw new BTSLBaseException(this, "confirmFileUploadForUnReg", "routing.delete.msg.invalidfilecontent", "uploadSubscriberFileForUnReg");
                	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_CONTENT, PretupsI.RESPONSE_FAIL, null);
                }
            }
            
            deRegisterSubscriberBatchVO.setFileNameStr(request.getFileName());
            
            
            //new function uploadAndProcessFile from struts file
            final String dir = Constants.getProperty("UploadFileForUnRegPath");
            
            
            if (request.getFileType().equals("txt")) {
				request.setFileType(BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT));
				final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
				String fileSize = Constants.getProperty("OTHER_FILE_SIZE");
				isFileUploaded = BTSLUtil.uploadFileToServerWithHashMap(fileDetailsMap, dir, contentType, "uploadSubscriberFileForUnReg", Long.parseLong(fileSize),data, request.getFileType());
				
				if (isFileUploaded) {
                    // now process uploaded file
                   // forward = this.processUploadedFileForUnReg(mapping, form, request);
					//isFileProcessed = deRegisterSubscriberBatchServiceI.processUploadedFileForUnRegSubscriber(fileDetailsMap,request,requestSwag,userVO,con,deRegisterSubscriberBatchVO,response,responseSwag);
					response = deRegisterSubscriberBatchServiceI.processUploadedFileForUnRegSubscriber(fileDetailsMap,request,requestSwag,userVO,con,deRegisterSubscriberBatchVO,response,responseSwag);
                } else {
//                    throw new BTSLBaseException(this, "uploadAndProcessFile", "p2p.subscriber.uploadsubscriberfileforunreg.error.filenotuploaded",
//                        "uploadSubscriberFileForUnReg");
                	throw new BTSLBaseException(this, methodName , PretupsErrorCodesI.FILE_UPLOAD_ERROR_ON_SERVER, PretupsI.RESPONSE_FAIL, null);
                }
            }
            
            
            
        }
        catch(BTSLBaseException be) {
			log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
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
        	//response.setStatus(400);
        	log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			responseSwag.setStatus(HttpStatus.SC_OK);
			response.setMessageCode(e.toString());
			response.setMessage(e.toString() + " : " + e.getMessage());
            e.printStackTrace();
        } finally {
        	
        	if (mcomCon != null) {
				mcomCon.close("UnregisterSubscribersAction#processUploadedFileForUnReg");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("processUploadedFileForUnReg", "Exit:forward=");
            }
            System.out.println("In finally block....................");
        }
	            
	            
	            
		//
		
		//response.setStatus("200");
		return response;
		
	}
	
	
	
	
	
	public void validateFileDetailsMap(HashMap<String, String> fileDetailsMap) throws BTSLBaseException {
		if (!BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_NAME))
				&& !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT))) {
			validateFileName(fileDetailsMap.get(PretupsI.FILE_NAME)); // throw exception
		} else {
			log.error("validateFileInput", "FILENAME/FILEATTACHMENT IS NULL");
			throw new BTSLBaseException(this, "validateFileInput", PretupsErrorCodesI.INVALID_FILE_INPUT,
					PretupsI.RESPONSE_FAIL, null);

		}
	}
	
	
	public void validateFileName(String fileName) throws BTSLBaseException {
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			throw new BTSLBaseException(this, "validateFileName", PretupsErrorCodesI.INVALID_FILE_NAME1,
					PretupsI.RESPONSE_FAIL, null);
		}
	}
	
	
	
	
	
	
	
	
	
	
}
