package com.restapi.channelenquiry.service;

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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
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
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.HeaderColumn;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ViewC2cAndO2cDownloadReport.name}", description = "${ViewC2cAndO2cDownloadReport.desc}")//@Api(tags = "Reports Download", defaultValue = "Reports Download")
@RestController
@RequestMapping(value = "/v1/reportDownload")
public class ViewC2cAndO2cDownloadReport {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	public static final Log log = LogFactory.getLog(ViewC2cAndO2cDownloadReport.class.getName());

	/**
	 * @author harshita.bajaj
	 * @param httpServletRequest
	 * @param headers
	 * @param enquiryType
	 * @param searchBy
	 * @param fileType
	 * @param headerColumns
	 * @param requestVO
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */

	@PostMapping(value = "/viewC2cAndO2cDownloadReport", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "View C2C And OC2 Enquiry Report Download", response = C2cAndO2cEnquiryResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${viewC2cAndO2cDownloadReport.summary}", description="${viewC2cAndO2cDownloadReport.description}",

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

	public FileDownloadResponse getC2cAndO2creport(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "enquiryType", required = true)// allowableValues = "O2C, C2C")
													   @RequestParam("enquiryType") String enquiryType,
			@Parameter(description = "searchBy", required = true)// allowableValues = "TRANSACTIONID, MSISDN, ADVANCE")
													   @RequestParam("searchBy") String searchBy,
			@Parameter(description = "fileType", required = true) @RequestParam("fileType") String fileType,

			@RequestBody List<HeaderColumn> headerColumns, C2cAndO2cEnquiryRequestVO requestVO,
			HttpServletResponse response)

			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException {
		final String methodName = "getC2cAndO2cReport";
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
					.getWebServiceTypeObject("VIEWC2CNO2CENQUIRY".trim());
			InstanceLoadVO instanceLoadVO = this.getInstanceLoadVOObject();
			String targetUrl = null;
			HashMap<String, String> headerMap = new HashMap<String, String>();

			if (searchBy.equals("ADVANCE") || searchBy.equals("MSISDN")) {
				headerMap.put(PretupsI.FROM_DATE, requestVO.getFromDate());
				headerMap.put(PretupsI.TO_DATE, requestVO.getToDate());
			}
			if (enquiryType.equals("C2C") && !BTSLUtil.isNullorEmpty(requestVO.getSenderMsisdn())) {
				headerMap.put(PretupsI.SENDER_MOBILE, requestVO.getSenderMsisdn());
			}
			if (enquiryType.equals("C2C") && !BTSLUtil.isNullorEmpty(requestVO.getReceiverMsisdn())) {
				headerMap.put(PretupsI.RECIEVER_MOBILE, requestVO.getReceiverMsisdn());
			}

		
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

			
			
			
			BTSLUtil.modifyHeaders(headers1, (new Gson()).toJson(requestVO));
			HttpEntity<?> entity = new HttpEntity<>(requestVO, headers1);
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(uri)
					.queryParam("enquiryType", enquiryType).queryParam("searchBy", searchBy);
			
			
			
			ResponseEntity<C2cAndO2cEnquiryResponseVO> responseEntity = restTemplate.exchange(uriBuilder.toUriString(),
					HttpMethod.POST, entity, C2cAndO2cEnquiryResponseVO.class);
			if (!responseEntity.getBody().getStatus().equals("200")) {
				fileDownloadResponse.setErrorMap(responseEntity.getBody().getErrorMap());
				fileDownloadResponse.setMessageCode(responseEntity.getBody().getMessageCode());
				fileDownloadResponse.setMessage(responseEntity.getBody().getMessage());
				fileDownloadResponse.setStatus(Integer.valueOf(responseEntity.getBody().getStatus()));
				return fileDownloadResponse;
			}
			String fileName = "ViewC2cAndO2cEnquiryDownload_" + (System.currentTimeMillis());
			String fileType1 = fileType.toUpperCase();
			String fileData = null;
			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(fileType1)) {
				fileData = createExcelXFile(fileName, responseEntity.getBody().getTransferList(), headerColumns,
						headerMap, enquiryType);
			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(fileType1)) {
				fileData = createCSVFile(responseEntity.getBody().getTransferList(), headerColumns, headerMap,enquiryType);
			} else
				fileData = createExcelFile(fileName, responseEntity.getBody().getTransferList(), headerColumns,
						headerMap, enquiryType);
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
				mcomCon.close("getC2cAndO2cReport");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("getC2cAndO2cReport", " Exited ");
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

	public String createExcelFile(String fileName, List<ChannelTransferVO> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap, String enquiryType) throws Exception {
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
			if (enquiryType.equals("C2C")) {
				serviceHeaderCell.setCellValue("C2C Services Enquiry");
			} else if (enquiryType.equals("O2C")) {
				serviceHeaderCell.setCellValue("O2C Services Enquiry");
			}
			serviceHeaderCell.setCellStyle(headerCellStyle);
			Row spacer1 = sheet.createRow(rowNum++);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14) );
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
				ChannelTransferVO record = List.get(i);
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

	public String createExcelXFile(String fileName, List<ChannelTransferVO> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap, String enquiryType) throws Exception {
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
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(16) );
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFontd);
			int rowNum = 0;
			Row serviceHeader = sheet.createRow(rowNum++);
			Cell serviceHeaderCell = serviceHeader.createCell(0);
			if (enquiryType.equals("C2C")) {
				serviceHeaderCell.setCellValue("C2C Services Enquiry");
			} else if (enquiryType.equals("O2C")) {
				serviceHeaderCell.setCellValue("O2C Services Enquiry");
			}
			serviceHeaderCell.setCellStyle(headerCellStyle);
			Row spacer1 = sheet.createRow(rowNum++);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14) );
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
				ChannelTransferVO record = List.get(i);
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

	public String createCSVFile(List<ChannelTransferVO> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap,String enquiryType) throws Exception {
		String fileData = null;
		try (StringWriter writer = new StringWriter();
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
			
			
			// Processing File Service Header
			
			if (enquiryType.equals("C2C")) {
				csvWriter.writeNext(new String[]{"C2C Services Enquiry"});
			} else if (enquiryType.equals("O2C")) {
				csvWriter.writeNext(new String[]{"O2C Services Enquiry"});
			}
			ArrayList<String> headerMapKeyList = new ArrayList<String>(headerMap.keySet());
			for (String key : headerMapKeyList) {
				String headerData[] = new String[2];
				headerData[0] = key;
				headerData[1] = headerMap.get(key);
				csvWriter.writeNext(headerData);
			}
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
			for (ChannelTransferVO record : List) {
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

	private Map<String, String> getMappedColumnValue(ChannelTransferVO record) {
		Map<String, String> mappedColumnValue = new HashMap<>();
		mappedColumnValue.put(PretupsI.DATE_TIME, record.getCreatedOnAsString());
		mappedColumnValue.put(PretupsI.TRANSACTION_ID, record.getTransferID());
		mappedColumnValue.put(PretupsI.APPROVED_BY, record.getFinalApprovedBy());
		mappedColumnValue.put(PretupsI.APPROVED_ON, record.getFinalApprovedDateAsString());
		mappedColumnValue.put(PretupsI.STATUS, record.getStatusDesc());
		mappedColumnValue.put(PretupsI.TRANSACTION_MODE, record.getTransactionMode());
		mappedColumnValue.put(PretupsI.PAYMENT_MODE, record.getPayInstrumentName());
		mappedColumnValue.put(PretupsI.TRANSACTION_CONTROLLING_TYPE, record.getControlTransferDesc());
		mappedColumnValue.put(PretupsI.REFERENCE_NUMBER, record.getReferenceNum());
		mappedColumnValue.put(PretupsI.REQUEST_SOURCE, record.getSource());
		mappedColumnValue.put(PretupsI.SENDER_MOBILE, record.getFromMsisdn());
		mappedColumnValue.put(PretupsI.SENDER_NAME,record.getFromUserName());
		mappedColumnValue.put(PretupsI.USER_TYPE,record.getActiveUsersUserType());
		mappedColumnValue.put(PretupsI.INITIATOR_USER,record.getActiveUserName());
		mappedColumnValue.put(PretupsI.AMOUNT,record.getPayableAmountAsString());
		mappedColumnValue.put(PretupsI.RECIEVER_MOBILE, record.getToMsisdn());
		String distNew = null;
		if (record.getTransferSubType().equals("V")) {
			distNew = "Voucher";
		} else {
			distNew = "Stock";
		}
		mappedColumnValue.put(PretupsI.DISTRIBUTION_T, distNew);
		mappedColumnValue.put(PretupsI.TRANSFER_CAT, record.getTransferCategoryCodeDesc());
		String tS = null;
		if (record.getTransferSubType().equals("V")) {
			tS = "Transfer";
		} else {
			tS = record.getTransferSubTypeValue();
		}
		mappedColumnValue.put(PretupsI.TRANSFER_SUB_TYPE, tS);
		mappedColumnValue.put(PretupsI.REQUESTED_QUANTITY, record.getRequestedQuantityAsString());
		mappedColumnValue.put(PretupsI.PAYABLE_AMOUNT, record.getPayableAmountAsString());
		mappedColumnValue.put(PretupsI.MOBILE_NUM, record.getUserMsisdn());
		mappedColumnValue.put(PretupsI.PRODUCT_NAME1, record.getProduct_name());

		return mappedColumnValue;
	}
	
}
