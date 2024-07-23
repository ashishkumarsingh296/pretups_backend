package com.restapi.c2s.services;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.*;
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
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.opencsv.CSVWriter;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.HeaderColumn;

import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ViewBatchDownloadReport.name}", description = "${ViewBatchDownloadReport.desc}")//@Api(tags= "File Operations", value="File Operations")
@RestController
@RequestMapping(value = "/v1/c2sServices")
public class ViewBatchDownloadReport {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	 StringBuilder loggerValue= new StringBuilder(); 


public static final Log log = LogFactory.getLog(ViewBatchDownloadReport.class.getName());	

@GetMapping(value= "/downloadViewBatchReport/batchID/msisdn/fileType", produces = MediaType.APPLICATION_JSON)
@ResponseBody
/*
@ApiOperation(value = "View Batch Report API", response = FileDownloadResponse.class,
          authorizations = {
              @Authorization(value = "Authorization")})
@ApiResponses(value = {
     @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
     @ApiResponse(code = 400, message = "Bad Request" ),
     @ApiResponse(code = 401, message = "Unauthorized"),
     @ApiResponse(code = 404, message = "Not Found")
     })
*/

@io.swagger.v3.oas.annotations.Operation(summary = "${downloadViewBatchReport.summary}", description="${downloadViewBatchReport.description}",

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



public FileDownloadResponse downloadViewBatch(HttpServletRequest httpServletRequest,@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
@RequestParam("batchID") String batchID,
@RequestParam("msisdn") String msisdn,
@RequestParam("fileType") String fileType,
		HttpServletResponse response)throws IOException, SQLException, BTSLBaseException{
	final String methodName = "downloadViewBatch";
    if (log.isDebugEnabled()) {
        log.debug(methodName, "Entered");
    }
    List<HeaderColumn> headerColumns = null;
    List<HeaderColumn> headerColumns1 = null;
    FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
    try{
    	//1st portion data
    	headerColumns1 = new ArrayList<HeaderColumn>();
    	headerColumns1.add(new HeaderColumn(PretupsI.DOMAIN,PretupsI.DOMAIN));
    	headerColumns1.add(new HeaderColumn(PretupsI.CATEGORY,PretupsI.CATEGORY));
    	headerColumns1.add(new HeaderColumn(PretupsI.GEOGRAPHY,PretupsI.GEOGRAPHY));
    	headerColumns1.add(new HeaderColumn(PretupsI.USER_NAME,PretupsI.USER_NAME));
    	headerColumns1.add(new HeaderColumn(PretupsI.BATCH_ID,PretupsI.BATCH_ID));
    	headerColumns1.add(new HeaderColumn(PretupsI.BATCH_CREATION_DATE,PretupsI.BATCH_CREATION_DATE));
    	headerColumns1.add(new HeaderColumn(PretupsI.BATCH_TYPE,PretupsI.BATCH_TYPE));
    	headerColumns1.add(new HeaderColumn(PretupsI.SERVICE_TYPE,PretupsI.SERVICE_TYPE));
    	headerColumns1.add(new HeaderColumn(PretupsI.NEXTSCHEDULEON,PretupsI.NEXTSCHEDULEON));
    	headerColumns1.add(new HeaderColumn(PretupsI.TOTAL_RECORDS_UPLOADED,PretupsI.TOTAL_RECORDS_UPLOADED));
    	headerColumns1.add(new HeaderColumn(PretupsI.UPLOAD_FAILED_COUNT,PretupsI.UPLOAD_FAILED_COUNT));
    	headerColumns1.add(new HeaderColumn(PretupsI.BATCH_SIZE,PretupsI.BATCH_SIZE));
    	headerColumns1.add(new HeaderColumn(PretupsI.SCHEDULED_SIZE,PretupsI.SCHEDULED_SIZE));
    	headerColumns1.add(new HeaderColumn(PretupsI.CANCELLED_SIZE,PretupsI.CANCELLED_SIZE));
    	
    	//2nd portion data
    	headerColumns = new ArrayList<>();
    	headerColumns.add(new HeaderColumn(PretupsI.MSISDN,PretupsI.MSISDN));
    	headerColumns.add(new HeaderColumn(PretupsI.SCHEDULEDAMOUNT,PretupsI.SCHEDULEDAMOUNT));
    	headerColumns.add(new HeaderColumn(PretupsI.MOBILENOSTATUS,PretupsI.MOBILENOSTATUS));
    	headerColumns.add(new HeaderColumn(PretupsI.LAST_MODIFIED_ON,PretupsI.LAST_MODIFIED_ON));
    	headerColumns.add(new HeaderColumn(PretupsI.LASTTRANSACTIONSTATUS,PretupsI.LASTTRANSACTIONSTATUS));
    	
    	OAuthenticationUtil.validateTokenApi(headers);
    	WebServiceKeywordCacheVO webServiceKeywordCacheVO=ServiceKeywordCache.getWebServiceTypeObject("VIEWBATCHDOWNLOAD".trim());
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
    	    headers1.add("Accept", MediaType.APPLICATION_JSON);
    	    BTSLUtil.modifyHeaders(headers1, null);
    	    HttpEntity <?>entity = new HttpEntity<>(headers1);
			
			  UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(uri)
			  .path("/"+batchID+"/") .path(msisdn)
			  ;
			  
    	    ResponseEntity<ViewC2SBulkRechargeDetailsResponseVO> responseEntity = restTemplate.exchange(uriBuilder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    ViewC2SBulkRechargeDetailsResponseVO.class);
    	    if(!(responseEntity.getBody().getStatus() == 200))
    	    {
    	    	fileDownloadResponse.setErrorMap(responseEntity.getBody().getErrorMap());
    	    	fileDownloadResponse.setMessageCode(responseEntity.getBody().getMessageCode());
    	    	fileDownloadResponse.setMessage(responseEntity.getBody().getMessage());
    			fileDownloadResponse.setStatus(Integer.valueOf(responseEntity.getBody().getStatus()));
    			return fileDownloadResponse;
    	    }
//    	    for (GetChannelUsersMsg e : responseEntity.getBody().getChannelUsersList()) {
//    			System.out.println(e);
//    		}
    	    String fileName = "ViewBatchDetails_" + (System.currentTimeMillis());
    	    String fileType1 = fileType.toUpperCase();
			String fileData = null;
				
			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(fileType1)) {
				fileData = createExcelXFile(fileName, responseEntity.getBody().getMsisdnList(),responseEntity.getBody().getScheduleBatchMasterVO(),headerColumns,headerColumns1);
			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(fileType1)) {
				fileData = createCSVFile(headerColumns,responseEntity.getBody().getMsisdnList());
			} else
				fileData = createExcelFile(fileName,headerColumns,responseEntity.getBody().getMsisdnList(),headerColumns1,responseEntity.getBody().getScheduleBatchMasterVO());
			fileDownloadResponse.setFileName(fileName+"."+fileType.toLowerCase());
			fileDownloadResponse.setFileType(fileType1.toLowerCase());
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
    	
        if (log.isDebugEnabled()) {
            log.debug("downloadViewBatch", " Exited ");
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
public String createExcelXFile(String fileName, ArrayList<ScheduleBatchDetailVO> listMaster,ScheduleBatchMasterVO scheduleBatchMasterVO,
		List<HeaderColumn> editColumns,List<HeaderColumn> topColumns) throws Exception {
	String fileDat = null;

	try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(fileName);
		int rowNum=0;
		// Processing file header data
		List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
				.collect(Collectors.toList());
		List<String> topNamList = topColumns.stream().map(HeaderColumn::getDisplayName)
				.collect(Collectors.toList());
		int displayNameListSize = displayNamList.size();
		Font headerFontd = workbook.createFont();
		headerFontd.setColor(Font.COLOR_NORMAL);
		headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFontd);
		Row headerRow = sheet.createRow(rowNum);
		Cell cell = headerRow.createCell(0);
		cell.setCellValue("View Batch Details");
		Row headerRow1 = sheet.createRow(++rowNum);
		for (int col = 0; col < topNamList.size(); col++) {
			Cell cell1 = headerRow1.createCell(col);
			cell1.setCellValue(topNamList.get(col));
			cell1.setCellStyle(headerCellStyle);
			try {
				sheet.autoSizeColumn(col);
			}catch (Exception e) {
				log.error("", "Error occurred while autosizing columns");
				e.printStackTrace();
			}
		}
		    Row dataRow = sheet.createRow(++rowNum);

			dataRow.createCell(0).setCellValue(scheduleBatchMasterVO.getParentDomain());
			dataRow.createCell(1).setCellValue(scheduleBatchMasterVO.getParentCategory());
			dataRow.createCell(2).setCellValue(scheduleBatchMasterVO.getUserGeo());
			dataRow.createCell(3).setCellValue(scheduleBatchMasterVO.getActiveUserName());
			dataRow.createCell(4).setCellValue(scheduleBatchMasterVO.getBatchID());
			dataRow.createCell(5).setCellValue(scheduleBatchMasterVO.getCreatedOnStr());
			dataRow.createCell(6).setCellValue(scheduleBatchMasterVO.getBatchType());
			dataRow.createCell(7).setCellValue(scheduleBatchMasterVO.getServiceName());
			dataRow.createCell(8).setCellValue(scheduleBatchMasterVO.getScheduledDateStr());
			dataRow.createCell(9).setCellValue(scheduleBatchMasterVO.getTotalCount());
			dataRow.createCell(10).setCellValue(scheduleBatchMasterVO.getUploadFailedCount());
			dataRow.createCell(11).setCellValue(scheduleBatchMasterVO.getNoOfRecords());
			dataRow.createCell(12).setCellValue(scheduleBatchMasterVO.getSuccessfulCount());
			dataRow.createCell(13).setCellValue(scheduleBatchMasterVO.getCancelledCount());
			//Processing lower portion of data
		Row headerRow2 = sheet.createRow(++rowNum);
		headerRow2 = sheet.createRow(++rowNum);
		for (int col = 0; col < displayNameListSize; col++) {
			Cell cell1 = headerRow2.createCell(col);
			cell1.setCellValue(displayNamList.get(col));
			cell1.setCellStyle(headerCellStyle);
			sheet.autoSizeColumn(col);
		}
		++rowNum;
		// Processing file body data
		List<String> columnNameLis = editColumns.stream().map(HeaderColumn::getColumnName)
				.collect(Collectors.toList());
		int columnNameListSize = columnNameLis.size();
		int voucherAvailListSize = listMaster.size();
		for (int i = 0; i < voucherAvailListSize; i++) {
			ScheduleBatchDetailVO record = listMaster.get(i);
			Map<String, String> mappedColumnValue = getMappedColumnValue(record);
			Row dataRow1 = sheet.createRow(i + rowNum);

			for (int col = 0; col < columnNameListSize; col++) {
				dataRow1.createCell(col).setCellValue(mappedColumnValue.get(columnNameLis.get(col)));
			}
		}

		workbook.write(outputStream);
		fileDat = new String(Base64.getEncoder().encode(outputStream.toByteArray()));
	} catch (IOException e) {
		log.error("Error occurred while generating excel file report.", e);
		throw new Exception("423423");
	}

	return fileDat;
}

public String createExcelFile(String fileName,
		List<HeaderColumn> editColumns,ArrayList<ScheduleBatchDetailVO> listMaster,List<HeaderColumn> topColumns,ScheduleBatchMasterVO scheduleBatchMasterVO) throws Exception {
	String fileDat = null;

	try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(fileName);
		int rowNum=0;
		// Processing file header data
		List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
				.collect(Collectors.toList());
		List<String> topNamList = topColumns.stream().map(HeaderColumn::getDisplayName)
				.collect(Collectors.toList());
		int displayNameListSize = displayNamList.size();
		Font headerFontd = workbook.createFont();
		headerFontd.setColor(Font.COLOR_NORMAL);
		headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFontd);
		Row headerRow = sheet.createRow(rowNum);
		Cell cell = headerRow.createCell(0);
		cell.setCellValue("View Batch Details");
		Row headerRow1 = sheet.createRow(++rowNum);
		for (int col = 0; col < topNamList.size(); col++) {
			Cell cell1 = headerRow1.createCell(col);
			cell1.setCellValue(topNamList.get(col));
			cell1.setCellStyle(headerCellStyle);
			try {
				sheet.autoSizeColumn(col);
			}catch (Exception e) {
				log.error("", "Error occurred while autosizing columns");
				e.printStackTrace();
			}
		}
		    Row dataRow = sheet.createRow(++rowNum);

			dataRow.createCell(0).setCellValue(scheduleBatchMasterVO.getParentDomain());
			dataRow.createCell(1).setCellValue(scheduleBatchMasterVO.getParentCategory());
			dataRow.createCell(2).setCellValue(scheduleBatchMasterVO.getUserGeo());
			dataRow.createCell(3).setCellValue(scheduleBatchMasterVO.getActiveUserName());
			dataRow.createCell(4).setCellValue(scheduleBatchMasterVO.getBatchID());
			dataRow.createCell(5).setCellValue(scheduleBatchMasterVO.getCreatedOnStr());
			dataRow.createCell(6).setCellValue(scheduleBatchMasterVO.getBatchType());
			dataRow.createCell(7).setCellValue(scheduleBatchMasterVO.getServiceName());
			dataRow.createCell(8).setCellValue(scheduleBatchMasterVO.getScheduledDateStr());
			dataRow.createCell(9).setCellValue(scheduleBatchMasterVO.getTotalCount());
			dataRow.createCell(10).setCellValue(scheduleBatchMasterVO.getUploadFailedCount());
			dataRow.createCell(11).setCellValue(scheduleBatchMasterVO.getNoOfRecords());
			dataRow.createCell(12).setCellValue(scheduleBatchMasterVO.getSuccessfulCount());
			dataRow.createCell(13).setCellValue(scheduleBatchMasterVO.getCancelledCount());
			//Processing lower portion of data
		Row headerRow2 = sheet.createRow(++rowNum);
		headerRow2 = sheet.createRow(++rowNum);
		for (int col = 0; col < displayNameListSize; col++) {
			Cell cell1 = headerRow2.createCell(col);
			cell1.setCellValue(displayNamList.get(col));
			cell1.setCellStyle(headerCellStyle);
			sheet.autoSizeColumn(col);
		}
		++rowNum;
		// Processing file body data
		List<String> columnNameLis = editColumns.stream().map(HeaderColumn::getColumnName)
				.collect(Collectors.toList());
		int columnNameListSize = columnNameLis.size();
		int voucherAvailListSize = listMaster.size();
		for (int i = 0; i < voucherAvailListSize; i++) {
			ScheduleBatchDetailVO record = listMaster.get(i);
			Map<String, String> mappedColumnValue = getMappedColumnValue(record);
			Row dataRow1 = sheet.createRow(i + rowNum);

			for (int col = 0; col < columnNameListSize; col++) {
				dataRow1.createCell(col).setCellValue(mappedColumnValue.get(columnNameLis.get(col)));
			}
		}

		workbook.write(outputStream);
		fileDat = new String(Base64.getEncoder().encode(outputStream.toByteArray()));
	} catch (IOException e) {
		log.error("Error occurred while generating excel file report.", e);
		throw new Exception("423423");
	}

	return fileDat;
}

public String createCSVFile(List<HeaderColumn> editColumns,ArrayList<ScheduleBatchDetailVO> list) throws Exception {
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
		for (ScheduleBatchDetailVO record : list) {
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
private Map<String, String> getMappedColumnValue(ScheduleBatchDetailVO record) throws ParseException {
	Map<String, String> mappedColumnValue = new HashMap<>();
	mappedColumnValue.put(PretupsI.
			MSISDN, record.getMsisdn());
	mappedColumnValue.put(PretupsI.SCHEDULEDAMOUNT
			, String.valueOf(record.getAmount()));
	mappedColumnValue.put(PretupsI.MOBILENOSTATUS, String.valueOf(record.getStatusDes()));

	mappedColumnValue.put(PretupsI.LAST_MODIFIED_ON,BTSLUtil.getDateStringFromDate(record.getModifiedOn()));
	mappedColumnValue.put(PretupsI.LASTTRANSACTIONSTATUS,
			record.getTransactionStatus());
	return mappedColumnValue;
}


}
