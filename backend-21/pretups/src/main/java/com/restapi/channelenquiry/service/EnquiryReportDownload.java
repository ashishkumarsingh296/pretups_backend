package com.restapi.channelenquiry.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.opencsv.CSVWriter;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.HeaderColumn;
import com.web.pretups.channel.transfer.businesslogic.C2CBatchTransferWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${EnquiryReportDownload.name}", description = "${EnquiryReportDownload.desc}")//@Api(tags = "Reports Download", defaultValue = "Reports Download")
@RestController
@RequestMapping(value = "/v1/reportDownload")
public class EnquiryReportDownload {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	public static final Log log = LogFactory.getLog(EnquiryReportDownload.class.getName());
	
	@PostMapping(value = "/bulkc2cReportDownload", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "C2C bulk Enquiry Report Download", response = FileDownloadResponse.class, authorizations = {
			@Authorization(value = "Authorization") })

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${bulkc2cReportDownload.summary}", description="${bulkc2cReportDownload.description}",

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

	public FileDownloadResponse getC2cBulkReport(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "batchId", required = true) @RequestParam("batchId") String batchId,
			@Parameter(description = "fileType", required = true) @RequestParam("fileType") String fileType,
			@RequestBody List<HeaderColumn> headerColumns,
			HttpServletResponse response)

			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException {
		final String methodName = "getC2cBulkReport";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		final C2CBatchTransferWebDAO c2cBatchTransferWebDAO = new C2CBatchTransferWebDAO();
		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
		List<C2CBatchMasterVO> batchDetalsList=null;
		 try {
			 if(BTSLUtil.isNullorEmpty(batchId.trim())) {
				 fileDownloadResponse.setMessage("Batch Id can't be empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BAD_REQUEST);
			 }
			 if(BTSLUtil.isNullorEmpty(fileType.trim())) {
				 fileDownloadResponse.setMessage("FileType can't be empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BAD_REQUEST);
			 }
			 OAuthenticationUtil.validateTokenApi(headers);
			 mcomCon = new MComConnection();
			 con=mcomCon.getConnection();	 
			 batchDetalsList = c2cBatchTransferWebDAO.loadBatchDetailsListDownload(con, batchId);
	         String fileName = "C2CBatchTransfeDetails_" + (System.currentTimeMillis());
			 String fileType1 = fileType.toUpperCase();
			 String fileData = null;
	           if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileType1)||PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileType1)) {
					fileData = createExcelFileOrXlsx(fileName,batchDetalsList, headerColumns,fileType1);
				} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase(fileType1)) {
					fileData = createCSVFile(fileName,batchDetalsList, headerColumns);
				}else {
					fileDownloadResponse.setMessage(" Invalid FileType");
					 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BAD_REQUEST);
				}
			 fileDownloadResponse.setFileName(fileName + "." + fileType.toLowerCase());
		     fileDownloadResponse.setFileType(fileType1.toLowerCase());
		     fileDownloadResponse.setFileattachment(fileData);
			 fileDownloadResponse.setStatus(200);
		 }
		 catch (BTSLBaseException be) {
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
				if(BTSLUtil.isNullorEmpty(fileDownloadResponse.getMessage())){
					fileDownloadResponse.setMessage(resmsg);
				}
				fileDownloadResponse.setMessageCode(be.getMessage());
			}
		 catch (Exception ex) {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
				log.errorTrace(methodName, ex);
				log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
			} 
		 finally {
				if (mcomCon != null) {
					mcomCon.close("getC2cBulkReport");
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug("getC2cBulkReport", " Exited ");
				}
			}
    	return fileDownloadResponse;
	
	}
	
	public String createExcelFileOrXlsx(String fileName, List<C2CBatchMasterVO> List, List<HeaderColumn> editColumns, String fileType) throws Exception {
		String methodName="createExcelFileOrXlsx";
		String fileDat = null;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			
			Workbook workbook =null;
			  if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileType)) {
				//xls
					 workbook=new HSSFWorkbook();
			  }else if(PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileType)) {
					//xlsx
				    workbook=new XSSFWorkbook();
			  }
			  else {
				  throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BAD_REQUEST);
			  }
			Sheet sheet = workbook.createSheet(fileName);

			// Processing file header data
			List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
					.collect(Collectors.toList());
			int displayNameListSize = displayNamList.size();
			Font headerFontd = workbook.createFont();
			headerFontd.setColor(Font.COLOR_NORMAL);
			headerFontd.setFontHeightInPoints((short) 16);
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFontd);
			int rowNum = 0;
			Row serviceHeader = sheet.createRow(rowNum++);
			Cell serviceHeaderCell = serviceHeader.createCell(0);
		    serviceHeaderCell.setCellValue("Batch C2C Deatails");
			serviceHeaderCell.setCellStyle(headerCellStyle);
			Row spacer1 = sheet.createRow(rowNum++);
			headerFontd.setFontHeightInPoints((short) 14);
			Row spacer = sheet.createRow(rowNum++);
			Row headerRow = sheet.createRow(rowNum++);
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
			int voucherAvailListSize = List.size();
			for (int i = 0; i < voucherAvailListSize; i++) {
				C2CBatchMasterVO record = List.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
				Row dataRow = sheet.createRow(rowNum++);

				for (int col = 0; col < columnNameListSize; col++) {
					dataRow.createCell(col).setCellValue(mappedColumnValue.get(columnNameLis.get(col)));
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
	
	public String createCSVFile(String fileName,List<C2CBatchMasterVO> List, List<HeaderColumn> editColumns) throws Exception {
		String fileData = null;
		try (StringWriter writer = new StringWriter();
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
			
			// Processing File Service Header
			csvWriter.writeNext(new String[]{"Batch C2C Deatails"});
			String spacer[] = { "" };
			csvWriter.writeNext(spacer);
			// Processing file header data
			List<String> displayNameList = editColumns.stream().map(HeaderColumn::getDisplayName)
					.collect(Collectors.toList());
			csvWriter.writeNext(displayNameList.stream().toArray(String[]::new));

			// Processing file body data
			List<String> columnNameList = editColumns.stream().map(HeaderColumn::getColumnName)
					.collect(Collectors.toList());
			int columnNameListSize = columnNameList.size();
			for (C2CBatchMasterVO record : List) {
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
	
	private Map<String, String> getMappedColumnValue(C2CBatchMasterVO record) {
		Map<String, String> mappedColumnValue = new HashMap<>();
		
		
		mappedColumnValue.put(PretupsI.BATCHID_L,record.getBatchId());
		mappedColumnValue.put(PretupsI.BATCH_NAME_L,record.getBatchName());
		mappedColumnValue.put(PretupsI.DOMAIN_NAME_L,record.getDomainCodeDesc());
		mappedColumnValue.put(PretupsI.PRODUCT_NAME_L,record.getProductCodeDesc());
		mappedColumnValue.put(PretupsI.BATCH_DATE_L,record.getBatchDateStr());
	
		if (record.getCreatedOn() != null) {
			try {
			mappedColumnValue.put(PretupsI.INITIATED_ON_L,
					BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(record.getCreatedOn())));
			mappedColumnValue.put(PretupsI.APPROVED_ON_L,
					BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(record.getC2cBatchItemsVO().getApprovedOn())));
			}catch (Exception e) {
				
			}
		}
	
		mappedColumnValue.put(PretupsI.INITIATED_BY_L,record.getCreatedBy());
		mappedColumnValue.put(PretupsI.STATUS_L,record.getStatus());
		mappedColumnValue.put(PretupsI.DETAILS_BATCHID_L,record.getC2cBatchItemsVO().getBatchDetailId());
		mappedColumnValue.put(PretupsI.USERNAME_L,record.getC2cBatchItemsVO().getUserName());
		mappedColumnValue.put(PretupsI.MSISDN_L,record.getC2cBatchItemsVO().getMsisdn());
		mappedColumnValue.put(PretupsI.CATEGORY_L,record.getC2cBatchItemsVO().getCategoryName());
		mappedColumnValue.put(PretupsI.USERGRADE_L,record.getC2cBatchItemsVO().getUserGradeCode());
		mappedColumnValue.put(PretupsI.EXTUSRCODE_L,record.getC2cBatchItemsVO().getExternalCode());
		mappedColumnValue.put(PretupsI.REQUESTEDQNTY_L,String.valueOf(BTSLUtil.getDisplayAmount(record.getC2cBatchItemsVO().getRequestedQuantity())));
		mappedColumnValue.put(PretupsI.TRFMRP_L,BTSLUtil.getDisplayAmount(record.getC2cBatchItemsVO().getTransferMrp())+"");
		mappedColumnValue.put(PretupsI.INITIATORREMARK_L,record.getC2cBatchItemsVO().getInitiatorRemarks());
		mappedColumnValue.put(PretupsI.APPROVED_BY_L,record.getC2cBatchItemsVO().getApprovedBy());
		mappedColumnValue.put(PretupsI.APPROVED_REMARK_L,record.getC2cBatchItemsVO().getApproverRemarks());
		
		return mappedColumnValue;
	}
	
}
