package com.restapi.user.service;

import java.io.File;
import java.sql.Connection;
import java.util.Base64;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CFileController.name}", description = "${C2CFileController.desc}")//@Api(tags ="C2C File Operations", value="C2C Services")
@RestController
@RequestMapping(value = "/v1/c2cFileServices")
public class C2CFileController {

	 @Context
	 private HttpServletRequest httpServletRequest;
	 private final Log log = LogFactory.getLog(this.getClass().getName());
	 
	 	@GetMapping(value="/download", produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "C2C File Download ", response = FileDownloadResponse.class,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
	    		@ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	 	        @ApiResponse(code = 400, message = "Bad Request"),
	 	        @ApiResponse(code = 401, message = "Unauthorized"),
	 	        @ApiResponse(code = 404, message = "Not Found")
		        })
	    */
		@io.swagger.v3.oas.annotations.Operation(summary = "${download.summary}", description="${download.description}",

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

		public FileDownloadResponse processFileDownload(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	    		@Parameter(description = SwaggerAPIDescriptionI.C2C_TXN_ID, required = true)
	    		@RequestParam("transactionID") String transactionID, HttpServletResponse response1 ) throws Exception 
	 	{
	 		String methodName = "processFileDownload";
	 		if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
	 		Connection con = null;
			MComConnectionI mcomCon = null;
			File downloadedFile = null;
			String fileAttachment = null;
			FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
			try 
			{
				mcomCon = new MComConnection();
				con=mcomCon.getConnection();
				
				OAuthenticationUtil.validateTokenApi(headers);
				
	 			if(BTSLUtil.isNullString(transactionID))
	 			{
	 				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	 				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_BLANK_TXNID, 0, null, null);
	 			}
	 			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
	 			
	 			downloadedFile = channelTransferDAO.getChannelTransferFile(con, transactionID);
	 			
	 			if(downloadedFile == null || downloadedFile.getAbsolutePath() == null)
	 			{
	 				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	 				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FILE_NOT_AVAILABLE, 0, null, null);
	 			}
	 			else 
	 			{
	 				String fileExt = FilenameUtils.getExtension(downloadedFile.getAbsolutePath());
	 				log.debug(methodName, "File extension is: "+fileExt);
	 				log.debug(methodName, "File name is: "+downloadedFile.getName());
	 				fileAttachment = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(downloadedFile));
	 				fileDownloadResponse.setFileattachment(fileAttachment);
	 				fileDownloadResponse.setFileName(downloadedFile.getName());
	 				fileDownloadResponse.setFileType(fileExt);
	 				fileDownloadResponse.setMessage(RestAPIStringParser.getMessage(new Locale
	 						((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.SUCCESS, null));
	 				fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
	 				fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
	 			}
	 			
			}
			catch (BTSLBaseException be)
			{
				log.error(methodName, "Exception:e=" + be);
	             log.errorTrace(methodName, be);
	             if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
	             		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
	            	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
	            	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
	            	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	             }
	              else{
	            	  fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
	            	  response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	              }
	     	   String resmsg  = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessage(), null);
	     	   fileDownloadResponse.setMessageCode(be.getMessage());
	     	   fileDownloadResponse.setMessage(resmsg);
			}
			catch (Exception e) {
				fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
				fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	            log.error(methodName, "Exception:e=" + e);
	            log.errorTrace(methodName, e);
	         	  response1.setStatus(PretupsI.RESPONSE_FAIL);
	         	  
	        } finally {
	            try {
	            	if (mcomCon != null) {
	    				mcomCon.close("C2CFileController#"+methodName);
	    				mcomCon = null;
	    			}
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	            }
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, " Exited ");
	            }
	        }
	 				
	 			return fileDownloadResponse;
	 		
	 	}
	
}
