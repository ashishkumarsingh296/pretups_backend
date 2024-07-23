package com.restapi.reportsDownload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
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
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserZeroBalanceCounterSummaryVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsDAO;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.restapi.channelenquiry.service.AlertCounterSummaryRequestVO;
import com.restapi.channelenquiry.service.AlertCounterSummaryResponseVO;
import com.restapi.channelenquiry.service.C2SEnquiryRequestVO;
import com.restapi.channelenquiry.service.C2SEnquiryResponseVO;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.HeaderColumn;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ReportsDownloadController.name}", description = "${ReportsDownloadController.desc}")//@Api(tags = "Reports Download", defaultValue = "Reports Download")
@RestController
@RequestMapping(value = "/v1/reportDownload")

//C2S Download Enquiry Report 

public class ReportsDownloadController {

	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	public static final Log log = LogFactory.getLog(ReportsDownloadController.class.getName());

	/**
	 * @author harshita.bajaj
	 * @param httpServletRequest
	 * @param headers
	 * @param service
	 * @param fromDate
	 * @param toDate
	 * @param transferID
	 * @param senderMsisdn
	 * @param receiverMsisdn
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

	@PostMapping(value = "/viewC2SEnquiryReportDownload", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "View C2S Enquiry Report Download", response = C2SEnquiryResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${viewC2SEnquiryReportDownload.summary}", description="${viewC2SEnquiryReportDownload.description}",

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


	public FileDownloadResponse getC2Sreport(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "service", required = true) @RequestParam("service") String service,
			@Parameter(description = "fromDate", required = true) @RequestParam("fromDate") String fromDate,
			@Parameter(description = "toDate", required = true) @RequestParam("toDate") String toDate,
			@Parameter(description = "transferID", required = false) @RequestParam("transferID") String transferID,
			@Parameter(description = "senderMsisdn", required = false) @RequestParam("senderMsisdn") String senderMsisdn,
			@Parameter(description = "receiverMsisdn", required = false) @RequestParam("receiverMsisdn") String receiverMsisdn,
			@Parameter(description = "fileType", required = true) @RequestParam("fileType") String fileType,
			@RequestBody List<HeaderColumn> headerColumns, HttpServletResponse response)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException {
		final String methodName = "getC2Sreport";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthenticationUtil.validateTokenApi(headers);
			WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
					.getWebServiceTypeObject("VIEWC2SENQUIRYREPORT".trim());
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
			C2SEnquiryRequestVO requestVO = new C2SEnquiryRequestVO();
			HashMap<String, String> headerMap = new HashMap<String, String>();
			headerMap.put(PretupsI.FROM_DATE, fromDate);
			headerMap.put(PretupsI.TO_DATE, toDate);
			requestVO.setFromDate(fromDate);
			requestVO.setToDate(toDate);

			if (!BTSLUtil.isNullorEmpty(receiverMsisdn.toString())) {
				requestVO.setReceiverMsisdn(receiverMsisdn.toString());
				headerMap.put(PretupsI.RECIEVER_MOBILE, receiverMsisdn.toString());
			}
			if (!BTSLUtil.isNullorEmpty(senderMsisdn.toString())) {
				requestVO.setSenderMsisdn(senderMsisdn.toString());
				headerMap.put(PretupsI.SENDER_NETWORK1, senderMsisdn.toString());

			}
			if (!BTSLUtil.isNullorEmpty(transferID.toString())) {
				requestVO.setTransferID(transferID.toString());
			}
			requestVO.setService(service);
			BTSLUtil.modifyHeaders(headers1, null);
			HttpEntity<?> entity = new HttpEntity<>(requestVO, headers1);

			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(uri);
			
			
			ResponseEntity<C2SEnquiryResponseVO> responseEntity = restTemplate.exchange(uriBuilder.toUriString(),
					HttpMethod.POST, entity, C2SEnquiryResponseVO.class);
			if (!(Integer.toString(responseEntity.getBody().getStatus()).equals("200"))) {
				fileDownloadResponse.setErrorMap(responseEntity.getBody().getErrorMap());
				fileDownloadResponse.setMessageCode(responseEntity.getBody().getMessageCode());
				fileDownloadResponse.setMessage(responseEntity.getBody().getMessage());
				fileDownloadResponse.setStatus(Integer.valueOf(responseEntity.getBody().getStatus()));
				return fileDownloadResponse;
			}
			String fileName = "ViewC2SEnquiryDownload_" + (System.currentTimeMillis());
			String fileType1 = fileType.toUpperCase();
			String fileData = null;

			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(fileType1)) {
				fileData = createExcelXFile(fileName, responseEntity.getBody().getC2sEnquiryDetails(), headerColumns,
						headerMap);
			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(fileType1)) {
				fileData = createCSVFile(responseEntity.getBody().getC2sEnquiryDetails(), headerColumns, headerMap);
			} else
				fileData = createExcelFile(fileName, responseEntity.getBody().getC2sEnquiryDetails(), headerColumns,
						headerMap);
			fileDownloadResponse.setFileName(fileName + "." + fileType.toLowerCase());
			fileDownloadResponse.setFileType(fileType1.toLowerCase());
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
			if (mcomCon != null) {
				mcomCon.close("C2SEnquiryReport");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("getC2SEnquiry", " Exited ");
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

	public String createExcelFile(String fileName, List<C2STransferVO> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap) throws Exception {
		String fileDat = null;

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet(fileName);

			// Processing file header data
			List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
					.collect(Collectors.toList());
			int displayNameListSize = displayNamList.size();
			Font headerFontd = workbook.createFont();
			headerFontd.setColor(Font.COLOR_NORMAL);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(16));
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFontd);
			int rowNum = 0;
			Row serviceHeader = sheet.createRow(rowNum++);
			Cell serviceHeaderCell = serviceHeader.createCell(0);
			serviceHeaderCell.setCellValue("C2S Services Enquiry");
			serviceHeaderCell.setCellStyle(headerCellStyle);
			Row spacer1 = sheet.createRow(rowNum++);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
			List<String> headerMapKeyList = new ArrayList<String>(headerMap.keySet());
			for (String key : headerMapKeyList) {
				Row headerMapRow = sheet.createRow(rowNum++);
				Cell cell0 = headerMapRow.createCell(0);
				cell0.setCellValue(key);
				cell0.setCellStyle(headerCellStyle);
				headerMapRow.createCell(1).setCellValue(headerMap.get(key));
			}
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
				C2STransferVO record = List.get(i);
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

	public String createExcelXFile(String fileName, List<C2STransferVO> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap) throws Exception {
		String fileDat = null;

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet(fileName);

			// Processing file header data
			List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
					.collect(Collectors.toList());
			int displayNameListSize = displayNamList.size();
			Font headerFontd = workbook.createFont();
			headerFontd.setColor(Font.COLOR_NORMAL);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(16));
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFontd);
			int rowNum = 0;
			Row serviceHeader = sheet.createRow(rowNum++);
			Cell serviceHeaderCell = serviceHeader.createCell(0);
			serviceHeaderCell.setCellValue("C2S Services Enquiry");
			serviceHeaderCell.setCellStyle(headerCellStyle);
			Row spacer1 = sheet.createRow(rowNum++);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
			List<String> headerMapKeyList = new ArrayList<String>(headerMap.keySet());
			for (String key : headerMapKeyList) {
				Row headerMapRow = sheet.createRow(rowNum++);
				Cell cell0 = headerMapRow.createCell(0);
				cell0.setCellValue(key);
				cell0.setCellStyle(headerCellStyle);
				headerMapRow.createCell(1).setCellValue(headerMap.get(key));
			}
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
				C2STransferVO record = List.get(i);
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

	public String createCSVFile(List<C2STransferVO> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap) throws Exception {
		String fileData = null;
		try (StringWriter writer = new StringWriter();
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
			// Processing File Service Header
			csvWriter.writeNext(new String[]{"C2S Services Enquiry"});
			
			String spacer[] = { "" };
			csvWriter.writeNext(spacer);
			ArrayList<String> headerMapKeyList = new ArrayList<String>(headerMap.keySet());
			for (String key : headerMapKeyList) {
				String headerData[] = new String[2];
				headerData[0] = key;
				headerData[1] = headerMap.get(key);
				csvWriter.writeNext(headerData);
			}
		
			csvWriter.writeNext(spacer);
			// Processing file header data
			List<String> displayNameList = editColumns.stream().map(HeaderColumn::getDisplayName)
					.collect(Collectors.toList());
			csvWriter.writeNext(displayNameList.stream().toArray(String[]::new));

			// Processing file body data
			List<String> columnNameList = editColumns.stream().map(HeaderColumn::getColumnName)
					.collect(Collectors.toList());
			int columnNameListSize = columnNameList.size();
			for (C2STransferVO record : List) {
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

	private Map<String, String> getMappedColumnValue(C2STransferVO record) {
		Map<String, String> mappedColumnValue = new HashMap<>();
		mappedColumnValue.put(PretupsI.DATE_TIME, record.getTransferDateStr());
		mappedColumnValue.put(PretupsI.TRANSACTION_ID, record.getTransferID());
		mappedColumnValue.put(PretupsI.SUB_SERVICE, record.getSubService());
		mappedColumnValue.put(PretupsI.PRODUCT_NAME, record.getProductName());
		mappedColumnValue.put(PretupsI.SENDER_NAME, record.getSenderName());
		mappedColumnValue.put(PretupsI.SENDER_MOBILE, record.getSenderMsisdn());
		mappedColumnValue.put(PretupsI.SENDER_NETWORK_NAME, record.getNetworkName());//change
		mappedColumnValue.put(PretupsI.RECIEVER_MOBILE, record.getReceiverMsisdn());
		mappedColumnValue.put(PretupsI.STATUS, record.getTransferStatus());
		mappedColumnValue.put(PretupsI.ERROR_MESSAGE, record.getErrorMessage());
		String dA = null;
		if (record.getDifferentialApplicable().equals("N")) {
			dA = "No";
		} else if(record.getDifferentialApplicable().equals("Y")) {
			dA = "Yes";
		}
		String dG = null;
		if (record.getDifferentialApplicable().equals("N")) {
			dG = "No";
		} else if(record.getDifferentialApplicable().equals("Y")) {
			dG = "Yes";
		}

		mappedColumnValue.put(PretupsI.DIFFERENTIAL_APPLICABLE, dA);
		mappedColumnValue.put(PretupsI.DIFFERNTIAL_GIVEN, dG);
		mappedColumnValue.put(PretupsI.TRANSFER_VALUE, record.getTransferValueStr());
		mappedColumnValue.put(PretupsI.REQUEST_SOURCE, record.getSourceType());
		mappedColumnValue.put(PretupsI.REVERSAL_ID, record.getReverseTransferID());
		//External Reference Number ,Promo Bonus,Voucher serial number
		mappedColumnValue.put(PretupsI.VOUCHER_SERIAL_NUMBER, record.getSerialNumber());
		mappedColumnValue.put(PretupsI.EXTERNAL_REFERENCE_NUMBER, record.getReferenceID());
		mappedColumnValue.put(PretupsI.PROMO_BONUS,String.valueOf(record.getPromoBonus()));
		mappedColumnValue.put(PretupsI.ADD_CELL_ID, record.getCellId());
		mappedColumnValue.put(PretupsI.SWITCH_ID, record.getSwitchId());
		mappedColumnValue.put(PretupsI.SERVICE, record.getServiceName());
		return mappedColumnValue;
	}
	
	@PostMapping(value = "/alertCounterReportDownload", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Alert Counter Summary Report Download", response = AlertCounterSummaryResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${alertCounterReportDownload.summary}", description="${alertCounterReportDownload.description}",

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


	public FileDownloadResponse getAlertCounterReport(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "domain", required = true) @RequestParam("domain") String domain,
			@Parameter(description = "category", required = true) @RequestParam("category") String category,
			@Parameter(description = "geography", required = true) @RequestParam("geography") String geography,
			@Parameter(description = "thresholdType", required = false) @RequestParam("thresholdType") String thresholdType,
			@Parameter(description = "date", required = false) @RequestParam("date") String date,
			@Parameter(description = "month", required = false) @RequestParam("month") String month,
			@Parameter(description = "fileType", required = true) @RequestParam("fileType") String fileType,
			@RequestBody List<HeaderColumn> headerColumns, HttpServletResponse response)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException {
		final String methodName = "getAlertCounterReport";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			OAuthenticationUtil.validateTokenApi(headers);
			WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
					.getWebServiceTypeObject("ALERTCOUNTERREPORT".trim());
			InstanceLoadVO instanceLoadVO = this.getInstanceLoadVOObject();
			
			DomainDAO domainDao = new DomainDAO();
			CategoryDAO categoryDao = new CategoryDAO();
			LookupsDAO lookupDao = new LookupsDAO();
			GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
			
			ArrayList<CategoryVO> categoryList =null;
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
			AlertCounterSummaryRequestVO requestVO = new AlertCounterSummaryRequestVO();
			HashMap<String, String> headerMap = new HashMap<String, String>();

			if (!BTSLUtil.isNullorEmpty(domain.toString())) {
				
				ArrayList<DomainVO> domainList=domainDao.loadDomainDetails(con);
				
				for(DomainVO domains:domainList) {
					if(domains.getDomainCode().equals(domain)) {
						requestVO.setDomainCode(domain);
						headerMap.put(PretupsI.DOMAIN, domains.getDomainName());
						
						break;
					}
				}
			
			}
			if (!BTSLUtil.isNullorEmpty(category.toString())) {
				requestVO.setCatCode(category.toString());
				headerMap.put(PretupsI.CATEGORY, category);
				
				
				
				if(category.contentEquals(PretupsI.ALL)) {	
						requestVO.setCatCode(category);
						headerMap.put(PretupsI.CATEGORY, category);
				}else {
					categoryList = categoryDao.loadCategoryDetailsUsingCategoryCode(con, category);
					
					for(CategoryVO categoryVo: categoryList) {
						if(categoryVo.getCategoryCode().equals(category)){
							requestVO.setCatCode(category);
							headerMap.put(PretupsI.CATEGORY, categoryVo.getCategoryName());
						
							break;
						}
					}
				}
				
			}
			if (!BTSLUtil.isNullorEmpty(geography.toString())) {
				
				if(geography.contentEquals(PretupsI.ALL)) {	
					requestVO.setGeoCode(geography.toString());
					headerMap.put(PretupsI.GEOGRAPHY, geography);
				}else {
					String geographyName = geographicalDomainDAO.getGeographyName(con,geography,true);
					requestVO.setGeoCode(geography.toString());
					headerMap.put(PretupsI.GEOGRAPHY, geographyName);
				}
				
			}
			
			if (!BTSLUtil.isNullorEmpty(thresholdType.toString())) {
				
				if(category.contentEquals(PretupsI.ALL)) {	
					requestVO.setThresholdType(thresholdType.toString());
					headerMap.put(PretupsI.THRESHOLD, thresholdType);
				}else {
					ArrayList<LookupsVO> thresholds = lookupDao.loadLookupsFromLookupCode(con,thresholdType,PretupsI.THRESHOLD_COUNTER_TYPE);
					for(LookupsVO threshold: thresholds) {
						if(threshold.getLookupCode().equals(thresholdType)){
							requestVO.setThresholdType(thresholdType.toString());
							headerMap.put(PretupsI.THRESHOLD, threshold.getLookupName());
					
							break;
						}
					}
				}
				
			}
			if (!BTSLUtil.isNullorEmpty(date.toString())) {
				requestVO.setReqDate(date.toString());
				headerMap.put(PretupsI.DATE, date);
			}if (!BTSLUtil.isNullorEmpty(month.toString())) {
				requestVO.setReqMonth(month.toString());
				headerMap.put(PretupsI.MONTH, month);
			}
			
			BTSLUtil.modifyHeaders(headers1, (new Gson()).toJson(requestVO));
			HttpEntity<?> entity = new HttpEntity<>(requestVO, headers1);

			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(uri);
			
			ResponseEntity<AlertCounterSummaryResponseVO> responseEntity = restTemplate.exchange(uriBuilder.toUriString(),
					HttpMethod.POST, entity, AlertCounterSummaryResponseVO.class);
			if (!(responseEntity.getBody().getStatus()).equals("200")) {
				fileDownloadResponse.setErrorMap(responseEntity.getBody().getErrorMap());
				fileDownloadResponse.setMessageCode(responseEntity.getBody().getMessageCode());
				fileDownloadResponse.setMessage(responseEntity.getBody().getMessage());
				fileDownloadResponse.setStatus(Integer.valueOf(responseEntity.getBody().getStatus()));
				return fileDownloadResponse;
			}
			String fileName = "AlertCounterSummary_" + (System.currentTimeMillis());
			String fileType1 = fileType.toUpperCase();
			String fileData = null;

			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(fileType1)) {
				fileData = createExcelXFile_AlertCounter(fileName, responseEntity.getBody().getAlertList(), headerColumns,
						headerMap);
			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(fileType1)) {
				fileData = createCSVFile_AlertCounter(responseEntity.getBody().getAlertList(), headerColumns, headerMap);
			} else
				fileData = createExcelFile_AlertCounter(fileName, responseEntity.getBody().getAlertList(), headerColumns,
						headerMap);
			fileDownloadResponse.setFileName(fileName + "." + fileType.toLowerCase());
			fileDownloadResponse.setFileType(fileType1.toLowerCase());
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
			if (mcomCon != null) {
				mcomCon.close("getAlertCounterReport");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("getAlertCounterReport", " Exited ");
			}
		}

		return fileDownloadResponse;
	}
	
	public String createExcelFile_AlertCounter(String fileName, List<UserZeroBalanceCounterSummaryVO> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap) throws Exception {
		String fileDat = null;

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet(fileName);

			// Processing file header data
			List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
					.collect(Collectors.toList());
			int displayNameListSize = displayNamList.size();
			Font headerFontd = workbook.createFont();
			headerFontd.setColor(Font.COLOR_NORMAL);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(16));
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFontd);
			int rowNum = 0;
			Row serviceHeader = sheet.createRow(rowNum++);
			Cell serviceHeaderCell = serviceHeader.createCell(0);
			serviceHeaderCell.setCellValue("Alert Counter Summary");
			serviceHeaderCell.setCellStyle(headerCellStyle);
			Row spacer1 = sheet.createRow(rowNum++);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
			List<String> headerMapKeyList = new ArrayList<String>(headerMap.keySet());
			for (String key : headerMapKeyList) {
				Row headerMapRow = sheet.createRow(rowNum++);
				Cell cell0 = headerMapRow.createCell(0);
				cell0.setCellValue(key);
				cell0.setCellStyle(headerCellStyle);
				headerMapRow.createCell(1).setCellValue(headerMap.get(key));
			}
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
				UserZeroBalanceCounterSummaryVO record = List.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue_AlertCounter(record);
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



	
	public String createExcelXFile_AlertCounter(String fileName, List<UserZeroBalanceCounterSummaryVO> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap) throws Exception {
		String fileDat = null;

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet(fileName);

			// Processing file header data
			List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
					.collect(Collectors.toList());
			int displayNameListSize = displayNamList.size();
			Font headerFontd = workbook.createFont();
			headerFontd.setColor(Font.COLOR_NORMAL);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(16));
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFontd);
			int rowNum = 0;
			Row serviceHeader = sheet.createRow(rowNum++);
			Cell serviceHeaderCell = serviceHeader.createCell(0);
			serviceHeaderCell.setCellValue("Alert Counter Summary");
			serviceHeaderCell.setCellStyle(headerCellStyle);
			Row spacer1 = sheet.createRow(rowNum++);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
			List<String> headerMapKeyList = new ArrayList<String>(headerMap.keySet());
			for (String key : headerMapKeyList) {
				Row headerMapRow = sheet.createRow(rowNum++);
				Cell cell0 = headerMapRow.createCell(0);
				cell0.setCellValue(key);
				cell0.setCellStyle(headerCellStyle);
				headerMapRow.createCell(1).setCellValue(headerMap.get(key));
			}
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
				UserZeroBalanceCounterSummaryVO record = List.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue_AlertCounter(record);
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

	public String createCSVFile_AlertCounter(List<UserZeroBalanceCounterSummaryVO> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap) throws Exception {
		String fileData = null;
		try (StringWriter writer = new StringWriter();
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
			// Processing File Service Header
			csvWriter.writeNext(new String[]{"Alert Counter Summary"});
			String spacer[] = { "" };
			csvWriter.writeNext(spacer);
			ArrayList<String> headerMapKeyList = new ArrayList<String>(headerMap.keySet());
			for (String key : headerMapKeyList) {
				String headerData[] = new String[2];
				headerData[0] = key;
				headerData[1] = headerMap.get(key);
				csvWriter.writeNext(headerData);
			}
			csvWriter.writeNext(spacer);
			// Processing file header data
			List<String> displayNameList = editColumns.stream().map(HeaderColumn::getDisplayName)
					.collect(Collectors.toList());
			csvWriter.writeNext(displayNameList.stream().toArray(String[]::new));

			// Processing file body data
			List<String> columnNameList = editColumns.stream().map(HeaderColumn::getColumnName)
					.collect(Collectors.toList());
			int columnNameListSize = columnNameList.size();
			for (UserZeroBalanceCounterSummaryVO record : List) {
				Map<String, String> mappedColumnValue = getMappedColumnValue_AlertCounter(record);
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

	private Map<String, String> getMappedColumnValue_AlertCounter(UserZeroBalanceCounterSummaryVO record) {
		Map<String, String> mappedColumnValue = new HashMap<>();
		mappedColumnValue.put(PretupsI.USER_NAME, record.getUserName());
		mappedColumnValue.put(PretupsI.MOBILE_NUMBERR, record.getMsisdn());
		mappedColumnValue.put(PretupsI.USER_STATUS, record.getUserStatus());
		mappedColumnValue.put(PretupsI.PRODUCT_NAME, record.getProductName());
		mappedColumnValue.put(PretupsI.CATEGORY_NAME, record.getCategoryName());
		mappedColumnValue.put(PretupsI.RECORD_TYPE, record.getRecordType());
		mappedColumnValue.put(PretupsI.SUB_TYPE_COUNT, record.getThresholdCount());


		return mappedColumnValue;
	}
	
	
}
