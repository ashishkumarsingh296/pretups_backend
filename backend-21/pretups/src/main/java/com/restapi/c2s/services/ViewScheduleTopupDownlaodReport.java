package com.restapi.c2s.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsRestUtil;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.opencsv.CSVWriter;
import com.restapi.c2sservices.controller.ViewScheduleDetailsListResponseVO;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.HeaderColumn;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${ViewScheduleTopupDownlaodReport.name}", description = "${ViewScheduleTopupDownlaodReport.desc}")//@Api(tags = "File Operations", defaultValue = "File Operations")
@RestController
@RequestMapping(value = "/v1/c2sServices")

public class ViewScheduleTopupDownlaodReport {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	public static final Log log = LogFactory.getLog(ViewScheduleTopupDownlaodReport.class.getName());

	 @GetMapping(value = "/viewScheduleTopupReportDownlaod",produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "View Schedule Topup", response = ViewScheduleDetailsListResponseVO.class,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
	    		@ApiResponse(code = 200, message = "OK", response =  FileDownloadResponse.class),
		        @ApiResponse(code = 400, message = "Bad Request" ),
		        @ApiResponse(code = 401, message = "Unauthorized"),
		        @ApiResponse(code = 404, message = "Not Found") })
	*/

	 @io.swagger.v3.oas.annotations.Operation(summary = "${viewScheduleTopupReportDownlaod.summary}", description="${viewScheduleTopupReportDownlaod.description}",

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


	 public FileDownloadResponse downloadViewScheduledTopup(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			
			@Parameter(description = "loginId", required = true) @RequestParam("searchLoginId") String loginId,
			@Parameter(description = "msisdn", required = true) @RequestParam("msisdn") String msisdn,
			@Parameter(description = "staffFlag", required = true)	@RequestParam("staffFlag") String staffFlag,
			@Parameter(description = "dateRange", required = true)	@RequestParam("dateRange") String dateRange,
			@Parameter(description = "fileType", required = true)	@RequestParam("fileType") String fileType,
			
			HttpServletResponse response) throws IOException, SQLException, BTSLBaseException {
		final String methodName = "downloadViewScheduleTopup";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		List<HeaderColumn> headerColumns1 = null;
		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();

		try {
			// 1st portion data
			headerColumns1 = new ArrayList<HeaderColumn>();
			headerColumns1.add(new HeaderColumn(PretupsI.BATCH_ID, PretupsI.BATCH_ID));
			headerColumns1.add(new HeaderColumn(PretupsI.BATCH_TYPE, PretupsI.BATCH_TYPE));
			headerColumns1.add(new HeaderColumn(PretupsI.MOBILE_NUMBER, PretupsI.MOBILE_NUMBER));
			headerColumns1.add(new HeaderColumn(PretupsI.SCHEDULEDAMOUNT, PretupsI.SCHEDULEDAMOUNT));
			headerColumns1.add(new HeaderColumn(PretupsI.SCHEDULED_BY, PretupsI.SCHEDULED_BY));
			headerColumns1.add(new HeaderColumn(PretupsI.BATCH_CREATION_DATE, PretupsI.BATCH_CREATION_DATE));
			headerColumns1.add(new HeaderColumn(PretupsI.MOBILENOSTATUS, PretupsI.MOBILENOSTATUS));
			headerColumns1.add(new HeaderColumn(PretupsI.NEXTSCHEDULEDATE, PretupsI.NEXTSCHEDULEDATE));
			headerColumns1.add(new HeaderColumn(PretupsI.LASTTRANSACTIONSTATUS, PretupsI.LASTTRANSACTIONSTATUS));
			headerColumns1.add(new HeaderColumn(PretupsI.FREQ, PretupsI.FREQ));
			headerColumns1.add(new HeaderColumn(PretupsI.SCHEDULED_ITERATIONS, PretupsI.SCHEDULED_ITERATIONS));
			headerColumns1.add(new HeaderColumn(PretupsI.EXECUTED_ITERATIONS, PretupsI.EXECUTED_ITERATIONS));

			OAuthenticationUtil.validateTokenApi(headers);
			WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
					.getWebServiceTypeObject("VIEWSCHEDULETOPUP".trim());
			InstanceLoadVO instanceLoadVO = this.getInstanceLoadVOObject();
			String targetUrl = null;
			if (SystemPreferences.HTTPS_ENABLE) {
				targetUrl = PretupsI.HTTPS_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON
						+ instanceLoadVO.getHostPort() + PretupsI.FORWARD_SLASH + instanceLoadVO.getContext()
						+ webServiceKeywordCacheVO.getServiceUrl();
			} else {
				targetUrl = PretupsI.HTTP_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON
						+ instanceLoadVO.getHostPort() + PretupsI.FORWARD_SLASH + instanceLoadVO.getContext()
						+ webServiceKeywordCacheVO.getServiceUrl();
			}
			final String uri = targetUrl;
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers1 = new HttpHeaders();
			String token = headers.get("authorization").get(0);
			if (token != null && token.contains("Bearer")) {
				token = token.substring(token.indexOf("Bearer") + 6).trim();
			}
			headers1.add("Authorization", token);
			headers1.add("Accept", MediaType.APPLICATION_JSON);
			BTSLUtil.modifyHeaders(headers1, null);
			HttpEntity<?> entity = new HttpEntity<>(headers1);

			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(uri).queryParam("dateRange", dateRange).queryParam("staffFlag", staffFlag).queryParam("msisdn",  msisdn).queryParam("searchLoginId", loginId);
			
			
			ResponseEntity<ViewScheduleDetailsListResponseVO> responseEntity = restTemplate.exchange(
					uriBuilder.toUriString(), HttpMethod.GET, entity, ViewScheduleDetailsListResponseVO.class);
			log.error(methodName,uriBuilder.toUriString() );
			
			if (!(responseEntity.getBody().getStatus().equals( "200"))) {
				fileDownloadResponse.setErrorMap(responseEntity.getBody().getErrorMap());
				fileDownloadResponse.setMessageCode(responseEntity.getBody().getMessageCode());
				fileDownloadResponse.setMessage(responseEntity.getBody().getMessage());
				fileDownloadResponse.setStatus(Integer.valueOf(responseEntity.getBody().getStatus()));
				return fileDownloadResponse;
			}
			String fileName = "ViewScheduledTopup_" + (System.currentTimeMillis());
			
			String allowedFileType = fileType;
 			if (BTSLUtil.isNullorEmpty(allowedFileType)) {
 				allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
 			}
 			if(BTSLUtil.isNullorEmpty(allowedFileType)) {
 				allowedFileType = PretupsI.USER_ALLOW_CONTENT_TYPE;
 			}
     	    // valide file type: file type allowed in system
     	    ReadGenericFileUtil readGenericFileUtil  = new ReadGenericFileUtil();
     	    readGenericFileUtil.validateFileType(allowedFileType.toLowerCase());
     	    
			
			String fileData = null;

			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(allowedFileType) || PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(allowedFileType)) {
				fileData = createExcelFile(fileName, responseEntity.getBody().getScheduleDetailList(), headerColumns1, allowedFileType);
			} else {
				fileData = createCSVFile(responseEntity.getBody().getScheduleDetailList(), headerColumns1);
			} 

			
			fileDownloadResponse.setFileName(fileName + "." + allowedFileType.toLowerCase());
			fileDownloadResponse.setFileType(allowedFileType.toLowerCase());
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
			String resmsg = RestAPIStringParser.getMessage(new Locale(lang, country), be.getMessage(), null);
			fileDownloadResponse.setMessageCode(be.getMessage());
			fileDownloadResponse.setMessage(resmsg);

		} catch (Exception ex) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
			log.errorTrace(methodName, ex);
			log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
		} finally {

			if (log.isDebugEnabled()) {
				log.debug("viewScheduleTopup", " Exited ");
			}
		}

		return fileDownloadResponse;
	}

	private InstanceLoadVO getInstanceLoadVOObject() throws RuntimeException {
		InstanceLoadVO instanceLoadVO, instanceLoadVORest;
		instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(
				Constants.getProperty("INSTANCE_ID") + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (instanceLoadVO != null) {
			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(
					instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_REST);
		} else {
			throw new RuntimeException(
					PretupsRestUtil.getMessageString("no.mapping.found.for.web.sms.or.rest.configuration"));
		}

		if (instanceLoadVORest == null) {
			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(
					instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
		}

		if (instanceLoadVORest == null) {
			instanceLoadVORest = instanceLoadVO;
		}

		return instanceLoadVORest;
	}

	public String createExcelFile(String fileName, ArrayList<ScheduleBatchDetailVO> listMaster,
			List<HeaderColumn> editColumns, String allowedFileType) throws Exception {
		String fileDat = null;
		Workbook workbook = null;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			
			if(PretupsI.FILE_CONTENT_TYPE_XLSX.equals(allowedFileType.toUpperCase())) {
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
					log.error("", "Error occurred while autosizing columns");
					e.printStackTrace();
				}
			}

			// Processing file body data
			List<String> columnNameLis = editColumns.stream().map(HeaderColumn::getColumnName)
					.collect(Collectors.toList());
			int columnNameListSize = columnNameLis.size();
			int voucherAvailListSize = listMaster.size();
			for (int i = 0; i < voucherAvailListSize; i++) {
				ScheduleBatchDetailVO record = listMaster.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
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
	
	
	public String createCSVFile(List<ScheduleBatchDetailVO> scheduleList, List<HeaderColumn> editColumns)
			throws Exception {
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
			for (ScheduleBatchDetailVO record : scheduleList) {
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
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

	private Map<String, String> getMappedColumnValue(ScheduleBatchDetailVO record) {
		Map<String, String> mappedColumnValue = new HashMap<>();
		mappedColumnValue.put(PretupsI.BATCH_ID, record.getBatchID());
		mappedColumnValue.put(PretupsI.BATCH_TYPE, String.valueOf(record.getBatchType()));
		mappedColumnValue.put(PretupsI.MOBILE_NUMBER, String.valueOf(record.getMsisdn()));
		mappedColumnValue.put(PretupsI.SCHEDULEDAMOUNT, String.valueOf(record.getAmountForDisp()));
		mappedColumnValue.put(PretupsI.SCHEDULED_BY, record.getCreatedBy());
		mappedColumnValue.put(PretupsI.BATCH_CREATION_DATE, record.getCreatedOnAsString());
		mappedColumnValue.put(PretupsI.MOBILENOSTATUS, record.getStatusDes());
		mappedColumnValue.put(PretupsI.NEXTSCHEDULEDATE, record.getScheduleDateStr());
		mappedColumnValue.put(PretupsI.LASTTRANSACTIONSTATUS, record.getTransactionStatus());
		mappedColumnValue.put(PretupsI.FREQ, record.getFrequency());
		mappedColumnValue.put(PretupsI.SCHEDULED_ITERATIONS, record.getIterations().toString());
		mappedColumnValue.put(PretupsI.EXECUTED_ITERATIONS, String.valueOf(record.getExecutedIterations()));
		return mappedColumnValue;
	}
}