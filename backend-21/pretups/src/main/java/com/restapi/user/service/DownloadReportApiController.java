package com.restapi.user.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.transfer.businesslogic.BalanceVO;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.user.businesslogic.GetChannelUsersListResponseVo;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.ChannelUserListRequestVO;
import com.restapi.channelAdmin.ChannelUserListResponseVO;
import com.restapi.superadmin.responseVO.OperatorUserListResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${DownloadReportApiController.name}", description = "${DownloadReportApiController.desc}")//@Api(tags= "File Operations", value="Channel User Services")
@RestController
@RequestMapping(value = "/v1/c2cFileServices")
public class DownloadReportApiController {

	protected final Log log = LogFactory.getLog(getClass().getName());
	
	/**
	 * @author sarthak.saini
	 * @param httpServletRequest
	 * @param headers
	 * @param geography
	 * @param domain
	 * @param networkCode
	 * @param category
	 * @param status
	 * @param fileType
	 * @param isChannelAdmin
	 * @param type
	 * @param loginId
	 * @param msisdn
	 * @param channelUserId
	 * @param headerColumns
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@PostMapping(value= "/downloadUserReport", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Channel User Report Download",response = FileDownloadResponse.class,
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${downloadUserReport.summary}", description="${downloadUserReport.description}",

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


	public FileDownloadResponse getReportUser(HttpServletRequest httpServletRequest,@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.GEOGRAPHICAL_DOMAIN_CODE, example = "") 
	@RequestParam("geography") String geography,
			@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_DOMAIN, example = "") 
	@RequestParam("domain") String domain,
			@Parameter(description = SwaggerAPIDescriptionI.NETWORK_CODE, example = "") 
	@RequestParam("networkCode") String networkCode,
			@Parameter(description = SwaggerAPIDescriptionI.CATEGORY_CODE, example = "") 
	@RequestParam("category") String category,
			@Parameter(description = "STATUS", example = "") 
	@RequestParam("status") String status,
	@Parameter(description = SwaggerAPIDescriptionI.FILE_TYPE, example = "",required = true) 
	@RequestParam("fileType") String fileType,
	@Parameter(description = "isChannelAdmin ", example = "",required  = false)
			@RequestParam("isChannelAdmin") Optional<String> isChannelAdmin,
	@Parameter(description = "SearchType (For Channel Admin)", required = false)// allowableValues = "Msisdn,LoginId,Advance")
			@RequestParam("Type") Optional<String> type,
	@Parameter(description = "loginId (For Channel Admin)", example="rarya_dist",required = false)
			@RequestParam("lginId") Optional<String> lginId,
	@Parameter(description = "msisdn (For Channel Admin)", example="7252525",required=false)
			@RequestParam("msisdn") Optional<String> msisdn,
	@Parameter(description="channelUserId (For Channel Admin)", example ="NSB000002570",required=false)
			@RequestParam("channelUserId") Optional<String> channelUserId,
	@RequestBody List<HeaderColumn> headerColumns,
	
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getReportUser";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        Connection con = null;
	    MComConnectionI mcomCon = null;
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        ReadGenericFileUtil readGenericFileUtil = null;
        WebServiceKeywordCacheVO webServiceKeywordCacheVO = new WebServiceKeywordCacheVO();
        UriComponentsBuilder uriBuilder = null;
        HttpEntity <?>entity = null;
        ChannelUserListRequestVO requestVO = new ChannelUserListRequestVO();
        String isChanneladmin = SqlParameterEncoder.encodeParams(isChannelAdmin.map(Object::toString).orElse(" "));
        String typeString = SqlParameterEncoder.encodeParams(type.map(Object::toString).orElse(" "));
        try{
        	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
        	OAuthenticationUtil.validateTokenApi(headers);
        	if(isChanneladmin.equals("Y"))
        		 webServiceKeywordCacheVO=ServiceKeywordCache.getWebServiceTypeObject("DOWNLOADFILEUSERCA".trim());
        	else
       		 	webServiceKeywordCacheVO=ServiceKeywordCache.getWebServiceTypeObject("DOWNLOADFILEUSER".trim());

    			InstanceLoadVO instanceLoadVO = this.getInstanceLoadVOObject();
    			String targetUrl = null;
    			if (SystemPreferences.HTTPS_ENABLE) {
    				targetUrl = PretupsI.HTTPS_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON + instanceLoadVO.getHostPort() + PretupsI.FORWARD_SLASH + instanceLoadVO.getContext() + webServiceKeywordCacheVO.getServiceUrl();
                 }else{
                	targetUrl = PretupsI.HTTP_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON + instanceLoadVO.getHostPort() + PretupsI.FORWARD_SLASH + instanceLoadVO.getContext() + webServiceKeywordCacheVO.getServiceUrl(); 	
                 }
        	 final String uri = targetUrl;
        	    RestTemplate restTemplate = new RestTemplate();
        	    HttpHeaders headers1 = new HttpHeaders();
        	    String token = headers.get("authorization").get(0);
				if(token != null && token.contains("Bearer")) {
					token = token.substring(token.indexOf("Bearer")+6).trim();
				}
        	    headers1.add("Authorization", token);
			    headers1.add("Referer", "swagger-ui");
        	    headers1.add("Accept", MediaType.APPLICATION_JSON);
        	    
        	    if(isChanneladmin.equals("Y")){
        	    	
        	    	requestVO.setDomain(domain);
        	    	requestVO.setUserCategory(category);
        	    	requestVO.setGeography(geography);
        	    	requestVO.setStatus(status);
        	    	
        	    	if(typeString.equalsIgnoreCase("Advance"))
        	    		requestVO.setParentUserID(SqlParameterEncoder.encodeParams(channelUserId.map(Object::toString).orElse(null)));
        	    	else if(typeString.equalsIgnoreCase("msisdn"))
        	    		requestVO.setMsisdn(SqlParameterEncoder.encodeParams(msisdn.map(Object::toString).orElse(null)));
        	    	else
        	    		requestVO.setLoginID(SqlParameterEncoder.encodeParams(lginId.map(Object::toString).orElse(null)));
        	    	
        	    	BTSLUtil.modifyHeaders(headers1, (new Gson()).toJson(requestVO));
        	    	entity = new HttpEntity<>(requestVO,headers1);
        	    }else {
        	    	BTSLUtil.modifyHeaders(headers1, null);
        	    	entity = new HttpEntity<>(headers1);
        	    }
        	    if(isChanneladmin.equals("Y")) {
					 uriBuilder = UriComponentsBuilder.fromHttpUrl(uri)
							  .queryParam("Type", typeString);
				}else {
				  
				   uriBuilder = UriComponentsBuilder.fromHttpUrl(uri)
							  .queryParam("category", category) .queryParam("domain", domain)
							  .queryParam("externalNetworkCode", networkCode) .queryParam("geography",
							  geography) .queryParam("status", status);
				   }
				
						
        	   				if(isChanneladmin.equals("Y")) {
        	   					
					ResponseEntity<ChannelUserListResponseVO> responseEntity1  = restTemplate.exchange(uriBuilder.toUriString(),
	                        HttpMethod.POST,
	                        entity,
	                        ChannelUserListResponseVO.class);
					 
					 if(!(responseEntity1.getBody().getStatus()==200))
		        	    {
		        	    	fileDownloadResponse.setErrorMap(responseEntity1.getBody().getErrorMap());
		        	    	fileDownloadResponse.setMessageCode(responseEntity1.getBody().getMessageCode());
		        	    	fileDownloadResponse.setMessage(responseEntity1.getBody().getMessage());
		        			fileDownloadResponse.setStatus(Integer.valueOf(responseEntity1.getBody().getStatus()));
		        			return fileDownloadResponse;
		        	    }
		        	    for (GetChannelUsersMsg e : responseEntity1.getBody().getChannelUsersList()) {
		        			System.out.println(e);
		        		}
		        	    String fileName = "ChannelUserFile" + (System.currentTimeMillis());
		        	    String allowedFileType = fileType;
		    			if (BTSLUtil.isNullorEmpty(allowedFileType)) {
		    				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
		    			}
		    			if(BTSLUtil.isNullorEmpty(allowedFileType)) {
		    				allowedFileType = PretupsI.USER_ALLOW_CONTENT_TYPE;
		    			}
		    			
		        	    /**
		        	     * valide file type: file type allowed in system
		        	     */
		        	    readGenericFileUtil  = new ReadGenericFileUtil();
		        	    readGenericFileUtil.validateFileType(allowedFileType.toLowerCase());

		        	    
		        	    
		    			String fileData = null;
		    			ProductTypeDAO productTypeDAO = new ProductTypeDAO();
		    			ArrayList productsLookupList =LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true);
		    			ArrayList<C2sBalanceQueryVO> list =productTypeDAO.getProductsDetailsFromProductType(con, ((ListValueVO)productsLookupList.get(0)).getValue());
		    			
		    			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(allowedFileType) || PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(allowedFileType) ) {
		    				fileData = createExcelFile(fileName, responseEntity1.getBody().getChannelUsersList(), headerColumns,list, allowedFileType);
		    			} else {
		    				fileData = createCSVFile(responseEntity1.getBody().getChannelUsersList(), headerColumns,list);
		    			}
		    			
		    			fileDownloadResponse.setFileName(fileName+"."+allowedFileType.toLowerCase());
		    			fileDownloadResponse.setFileType(allowedFileType.toLowerCase());
		    			fileDownloadResponse.setFileattachment(fileData);
		    			fileDownloadResponse.setStatus(200);
		    			return fileDownloadResponse;
				} else {
					BTSLUtil.modifyHeaders(headers1, null);
					 ResponseEntity<GetChannelUsersListResponseVo> responseEntity = restTemplate.exchange(uriBuilder.toUriString(),
		                        HttpMethod.GET,
		                        entity,
		                        GetChannelUsersListResponseVo.class);
						
						 if(!(responseEntity.getBody().getStatus().equals("200")))
			        	    {
			        	    	fileDownloadResponse.setErrorMap(responseEntity.getBody().getErrorMap());
			        	    	fileDownloadResponse.setMessageCode(responseEntity.getBody().getMessageCode());
			        	    	fileDownloadResponse.setMessage(responseEntity.getBody().getMessage());
			        			fileDownloadResponse.setStatus(Integer.valueOf(responseEntity.getBody().getStatus()));
			        			return fileDownloadResponse;
			        	    }
			        	    for (GetChannelUsersMsg e : responseEntity.getBody().getChannelUsersList()) {
			        			System.out.println(e);
			        		}
			        	    String fileName = "ChannelUserFile" + (System.currentTimeMillis());
			        	    String allowedFileType = fileType;
			    			if (BTSLUtil.isNullorEmpty(allowedFileType)) {
			    				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
			    			}
			    			if(BTSLUtil.isNullorEmpty(allowedFileType)) {
			    				allowedFileType = PretupsI.USER_ALLOW_CONTENT_TYPE;
			    			}
			    			
			        	    /**
			        	     * valide file type: file type allowed in system
			        	     */
			        	    readGenericFileUtil = new ReadGenericFileUtil();
			        	    readGenericFileUtil.validateFileType(allowedFileType.toLowerCase());

			        	    
			        	    
			    			String fileData = null;
			    			ProductTypeDAO productTypeDAO = new ProductTypeDAO();
			    			ArrayList productsLookupList =LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true);
			    			ArrayList<C2sBalanceQueryVO> list =productTypeDAO.getProductsDetailsFromProductType(con, ((ListValueVO)productsLookupList.get(0)).getValue());
			    			
			    			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(allowedFileType) || PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(allowedFileType) ) {
			    				fileData = createExcelFile(fileName, responseEntity.getBody().getChannelUsersList(), headerColumns,list, allowedFileType);
			    			} else {
			    				fileData = createCSVFile(responseEntity.getBody().getChannelUsersList(), headerColumns,list);
			    			}
			    			
			    			fileDownloadResponse.setFileName(fileName+"."+allowedFileType.toLowerCase());
			    			fileDownloadResponse.setFileType(allowedFileType.toLowerCase());
			    			fileDownloadResponse.setFileattachment(fileData);
			    			fileDownloadResponse.setStatus(200);
			    			return fileDownloadResponse;
		        }

        	           
        }
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
               		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
        	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        finally {
	    	if (mcomCon != null) {
				mcomCon.close("DownloadReportApiController#getReportUser");
				mcomCon = null;
			}
	        if (log.isDebugEnabled()) {
	            log.debug("getReportUser", " Exited ");
	        }
	    }

        
	
        return fileDownloadResponse;
	}
        private InstanceLoadVO getInstanceLoadVOObject() throws RuntimeException{
    		InstanceLoadVO instanceLoadVO, instanceLoadVORest;
    		instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(Constants.getProperty("INSTANCE_ID") + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
    		if(instanceLoadVO != null){
    			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_REST);
    		}else{
    			throw new RuntimeException(PretupsRestUtil.getMessageString("no.mapping.found.for.web.sms.or.rest.configuration"));
    		}
    		
    		if(instanceLoadVORest == null){
    			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
    		}
    		
    		if(instanceLoadVORest == null){
    			instanceLoadVORest = instanceLoadVO;
    		}
    		
    		return instanceLoadVORest;
    	}
        
        public String createExcelFile(String fileName, List<GetChannelUsersMsg> voucherAvailList,
    			List<HeaderColumn> editColumns,ArrayList<C2sBalanceQueryVO> list, String allowedFileType) throws Exception {
    		String fileDat = null;

    		Workbook workbook = null;
    		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
    			
    			if(PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(allowedFileType.toUpperCase())) {
    				workbook = new XSSFWorkbook();
    			} else {
    				workbook = new HSSFWorkbook();
    			}
    			
    			Sheet sheet = workbook.createSheet(fileName);

    			// Processing file header data
    			List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
    					.collect(Collectors.toList());
    			int displayNameListSize = displayNamList.size();
    			Font headerFontd = workbook.createFont();
    			headerFontd.setColor(Font.COLOR_NORMAL);
    			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
    			CellStyle headerCellStyle = workbook.createCellStyle();
    			headerCellStyle.setFont(headerFontd);
    			Row headerRow = sheet.createRow(0);
    			for (int col = 0; col < displayNameListSize; col++) {
    				Cell cell = headerRow.createCell(col);
    				cell.setCellValue(displayNamList.get(col));
    				cell.setCellStyle(headerCellStyle);
    				try {
    					sheet.autoSizeColumn(col);
    				}catch (Exception e) {
    					log.error("createExcelFile", "Error occurred while autosizing columns");
    					e.printStackTrace();
					}
    			}

    			// Processing file body data
    			List<String> columnNameLis = editColumns.stream().map(HeaderColumn::getColumnName)
    					.collect(Collectors.toList());
    			int columnNameListSize = columnNameLis.size();
    			int voucherAvailListSize = voucherAvailList.size();
    			for (int i = 0; i < voucherAvailListSize; i++) {
    				GetChannelUsersMsg record = voucherAvailList.get(i);
    				Map<String, String> mappedColumnValue = getMappedColumnValue(record,list);
    				Row dataRow = sheet.createRow(i + 1);

    				for (int col = 0; col < columnNameListSize; col++) {
    					dataRow.createCell(col).setCellValue(mappedColumnValue.get(columnNameLis.get(col)));
    				}
    			}

    			workbook.write(outputStream);
    			fileDat = new String(Base64.getEncoder().encode(outputStream.toByteArray()));
    			workbook.close();
    		} catch (IOException e) {
    			log.error("Error occurred while generating excel file report.", e);
    			throw new Exception("423423");
    		}

    		return fileDat;
    	}
        
        
        public String createCSVFile(List<GetChannelUsersMsg> voucherAvailList, List<HeaderColumn> editColumns,ArrayList<C2sBalanceQueryVO> list) throws Exception {
    		String fileData = null;

    		try (StringWriter writer = new StringWriter();
    				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
    						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
    			// Processing file header data
    			List<String> displayNameList = editColumns.stream().map(HeaderColumn::getDisplayName)
    					.collect(Collectors.toList());
    			csvWriter.writeNext(displayNameList.stream().toArray(String[]::new));

    			// Processing file body data
    			List<String> columnNameList = editColumns.stream().map(HeaderColumn::getColumnName)
    					.collect(Collectors.toList());
    			int columnNameListSize = columnNameList.size();
    			for (GetChannelUsersMsg record : voucherAvailList) {
    				
    				Map<String, String> mappedColumnValue = getMappedColumnValue(record,list);
    				String[] dataRow = new String[columnNameListSize];

    				for (int col = 0; col < columnNameListSize; col++) {
    					dataRow[col] = mappedColumnValue.get(columnNameList.get(col));
    				}

    				csvWriter.writeNext(dataRow);
    			}

    			String output = writer.toString();
    			fileData = new String(Base64.getEncoder().encode(output.getBytes()));
    		} catch (IOException e) {
    			log.error("Error occurred while generating csv file report.", e);
    			throw new Exception("423423");
    		}

    		return fileData;
    	}
        private Map<String, String> getMappedColumnValue(GetChannelUsersMsg record,ArrayList<C2sBalanceQueryVO> list) {
    		Map<String, String> mappedColumnValue = new HashMap<>();
    		mappedColumnValue.put(PretupsI.
					USER_NAME, record.getUserName());
    		mappedColumnValue.put(PretupsI.MOBILE_NUMBERR
					, record.getMsisdn());
    		
    		
    		if(!BTSLUtil.isNullObject(record.getBalanceList()))
    		for(int i=0;i<record.getBalanceList().size();i++)
    		{
    			BalanceVO balanceVO = record.getBalanceList().get(i);
    			
    			mappedColumnValue.put(balanceVO.getProductName().toUpperCase()+" BALANCE",balanceVO.getBalance()==null?"":balanceVO.getBalance().toString());
    		}
    		//handling for single product
    		for(int i=0;i<list.size();i++)
    		{
    			C2sBalanceQueryVO y =list.get(i);
    			if(!mappedColumnValue.containsKey(y.getProductName().toUpperCase()+" BALANCE"))
    			{
    				mappedColumnValue.put(y.getProductName().toUpperCase() + " BALANCE","");
    			}
    		}
    		mappedColumnValue.put(PretupsI.STATUS, record.getStatus());
    		mappedColumnValue.put(PretupsI.DOMAIN, record.getDomain());
    		mappedColumnValue.put(PretupsI.CATEGORY,
    				record.getCategory());
    		mappedColumnValue.put(PretupsI.PARENT_NAME, record.getParentName());
    		mappedColumnValue.put(PretupsI.GEOGRAPHY, record.getGeography());
    		mappedColumnValue.put(PretupsI.LOGINIDD, record.getLoginID());
    		mappedColumnValue.put(PretupsI.CONTACT_PERSON_NAME, record.getContactPerson());
    		mappedColumnValue.put(PretupsI.GRADE, record.getGrade());
    		mappedColumnValue.put(PretupsI.REGISTERED_DATE_TIME, BTSLUtil.isNullObject(record.getRegisteredDateTime())?"":record.getRegisteredDateTime().toString());
    		mappedColumnValue.put(PretupsI.LAST_MODIFIED_ON, BTSLUtil.isNullObject(record.getLastModifiedDateTime())?"":record.getLastModifiedDateTime().toString());
    		mappedColumnValue.put(PretupsI.LAST_MODIFIED_BY,
    				record.getLastModifiedBy());
    		mappedColumnValue.put(PretupsI.TRANSACTION_PROFILE,
    				record.getTransactionProfile());
    		mappedColumnValue.put(PretupsI.COMMISSION_PROFILE,
    				record.getCommissionProfile());
    		mappedColumnValue.put(PretupsI.LAST_TXN_DATE_TIME,
    				BTSLUtil.isNullObject(record.getLastTxnDatTime())?"":record.getLastTxnDatTime().toString());


    		return mappedColumnValue;
    	}
        /**
    	 * @author sarthak.saini
    	 * @param httpServletRequest
    	 * @param headers
    	 * @param category
    	 * @param fileType
    	 * @param headerColumns
    	 * @param response
    	 * @return
    	 * @throws BTSLBaseException
    	 * @throws SQLException
    	 * @throws JsonParseException
    	 * @throws JsonMappingException
    	 * @throws IOException
    	 */
    	@PostMapping(value= "/downloadOperatorUserReport", produces = MediaType.APPLICATION_JSON)
    	@ResponseBody
    	/*@ApiOperation(value = "Operator User Report Download",response = FileDownloadResponse.class,
    				  authorizations = {
    		    	            @Authorization(value = "Authorization")})
    	@ApiResponses(value = {
    	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
    	        @ApiResponse(code = 400, message = "Bad Request"),
    	        @ApiResponse(code = 401, message = "Unauthorized"),
    	        @ApiResponse(code = 404, message = "Not Found")
    	        })
    	*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${downloadOperatorUserReport.summary}", description="${downloadOperatorUserReport.description}",

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


    	public FileDownloadResponse getReportForOperatorUser(
    	   HttpServletRequest httpServletRequest,
    	   @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, 
    	    @RequestParam("category") String category,	
         	@RequestParam("fileType") String fileType,
            @RequestBody List<HeaderColumn> headerColumns,
    	
    			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
    		final String methodName = "getReportForOperatorUser";
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Entered");
            }
            Connection con = null;
    	    MComConnectionI mcomCon = null;
            FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
            ReadGenericFileUtil readGenericFileUtil = null;
            WebServiceKeywordCacheVO webServiceKeywordCacheVO = new WebServiceKeywordCacheVO();
            UriComponentsBuilder uriBuilder = null;
            HttpEntity <?>entity = null;
            ChannelUserListRequestVO requestVO = new ChannelUserListRequestVO();
           
            try{
            	mcomCon = new MComConnection();
    	    	con=mcomCon.getConnection();
            	OAuthenticationUtil.validateTokenApi(headers);
                webServiceKeywordCacheVO=ServiceKeywordCache.getWebServiceTypeObject("DOWNLOADFILEOPERATORUSR".trim());
        	    InstanceLoadVO instanceLoadVO = this.getInstanceLoadVOObject();
        	    String targetUrl = null;
        	    List<GetChannelUsersMsg> listMsg=new ArrayList();
        	    if (SystemPreferences.HTTPS_ENABLE) {
        		  targetUrl = PretupsI.HTTPS_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON + instanceLoadVO.getHostPort() + PretupsI.FORWARD_SLASH + instanceLoadVO.getContext() + webServiceKeywordCacheVO.getServiceUrl();
                 }else{
                   targetUrl = PretupsI.HTTP_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON + instanceLoadVO.getHostPort() + PretupsI.FORWARD_SLASH + instanceLoadVO.getContext() + webServiceKeywordCacheVO.getServiceUrl(); 	
                }
            	 final String uri = targetUrl;
            	    RestTemplate restTemplate = new RestTemplate();
            	    HttpHeaders headers1 = new HttpHeaders();
            	    String token = headers.get("authorization").get(0);
    				if(token != null && token.contains("Bearer")) {
    					token = token.substring(token.indexOf("Bearer")+6).trim();
    				}
            	    headers1.add("Authorization", token);
				    headers1.add("Referer", "swagger-ui");
            	    headers1.add("Accept", MediaType.APPLICATION_JSON);
            	    BTSLUtil.modifyHeaders(headers1, null);
            	    entity = new HttpEntity<>(requestVO,headers1);
    				uriBuilder = UriComponentsBuilder.fromHttpUrl(uri)
    							  .queryParam("category", category);
    			
					ResponseEntity<OperatorUserListResponse> responseEntity1  = restTemplate.exchange(uriBuilder.toUriString(),
	                        HttpMethod.GET,
	                        entity,
	                        OperatorUserListResponse.class);
    					 
    					 if(!(responseEntity1.getBody().getStatus()==200))
    		        	    {
    		        	    	fileDownloadResponse.setErrorMap(responseEntity1.getBody().getErrorMap());
    		        	    	fileDownloadResponse.setMessageCode(responseEntity1.getBody().getMessageCode());
    		        	    	fileDownloadResponse.setMessage(responseEntity1.getBody().getMessage());
    		        			fileDownloadResponse.setStatus(Integer.valueOf(responseEntity1.getBody().getStatus()));
    		        			return fileDownloadResponse;
    		        	    }
    		        	    for (Object e : responseEntity1.getBody().getViewOperatorUser())
    		        	    {
    		        	    	HashMap<Object, Object> obj=(HashMap)e;
    		        	    	GetChannelUsersMsg msg=new GetChannelUsersMsg();
    		        	    	msg.setUserName((String)obj.get("userName"));
    		        	    	msg.setLoginID((String)obj.get("loginID"));
    		        	    	msg.setMsisdn((String)obj.get("msisdn"));
    		        	    	msg.setRegisteredDateTime(new Date((long)obj.get("createdOn")));
    		        	    	msg.setLastModifiedDateTime(new Date((long)obj.get("modifiedOn")));
    		        	    	msg.setLastModifiedBy((String)obj.get("modifiedBy"));
    		        	    	msg.setStatus((String)obj.get("statusDesc"));
    		        	    	listMsg.add(msg);
    		        		}
    		        	    //String fileName = "OperatorUsersFile" + (System.currentTimeMillis());
    		        	    String fileName = category + (System.currentTimeMillis());
    		        	    String allowedFileType = fileType;
    		    			if (BTSLUtil.isNullorEmpty(allowedFileType)) {
    		    				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
    		    			}
    		    			if(BTSLUtil.isNullorEmpty(allowedFileType)) {
    		    				allowedFileType = PretupsI.USER_ALLOW_CONTENT_TYPE;
    		    			}
    		    			
    		        	    /**
    		        	     * valide file type: file type allowed in system
    		        	     */
    		        	    readGenericFileUtil  = new ReadGenericFileUtil();
    		        	    readGenericFileUtil.validateFileType(allowedFileType.toLowerCase());

    		        	    
    		        	    
    		    			String fileData = null;
    		    			ProductTypeDAO productTypeDAO = new ProductTypeDAO();
    		    			ArrayList productsLookupList =LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true);
    		    			ArrayList<C2sBalanceQueryVO> list =productTypeDAO.getProductsDetailsFromProductType(con, ((ListValueVO)productsLookupList.get(0)).getValue());
    		    			
    		    			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(allowedFileType) || PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(allowedFileType) ) {
    		    				fileData = createExcelFile(fileName, listMsg, headerColumns,list, allowedFileType);
    		    			} else {
    		    				fileData = createCSVFile(listMsg, headerColumns,list);
    		    			}
    		    			
//    		    			fileDownloadResponse.setFileName(fileName+"."+allowedFileType.toLowerCase());
    		    			fileDownloadResponse.setFileName(fileName);
    		    			fileDownloadResponse.setFileType(allowedFileType.toLowerCase());
    		    			fileDownloadResponse.setFileattachment(fileData);
    		    			fileDownloadResponse.setStatus(200);
    		    			return fileDownloadResponse;
    				
            	           
            }
            catch (BTSLBaseException be) {
              	 log.error(methodName, "Exception:e=" + be);
                   log.errorTrace(methodName, be);
                   if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
                   		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
                   	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                   	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
                   }
                    else{
                    response.setStatus(HttpStatus.SC_BAD_REQUEST);
                    fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                    }
                   String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                   String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
           	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
           	   fileDownloadResponse.setMessageCode(be.getMessage());
           	   fileDownloadResponse.setMessage(resmsg);
           	   
            	}
            catch (Exception ex) {
            	response.setStatus(HttpStatus.SC_BAD_REQUEST);
    			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
    	        log.errorTrace(methodName, ex);
    	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
    	    }
            finally {
    	    	if (mcomCon != null) {
    				mcomCon.close("DownloadReportApiController#getReportForOperatorUser");
    				mcomCon = null;
    			}
    	        if (log.isDebugEnabled()) {
    	            log.debug("getReportForOperatorUser", " Exited ");
    	        }
    	    }
            return fileDownloadResponse;
    	}
}
