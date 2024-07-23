package com.restapi.reportsDownload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.opencsv.CSVWriter;
import com.restapi.user.service.BarredVo;
import com.restapi.user.service.FetchBarredListRequestVO;
import com.restapi.user.service.FetchBarredListResponseVO;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.HeaderColumn;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BarredUserlistDownload.name}", description = "${BarredUserlistDownload.desc}")//@Api(tags = "Reports Download", defaultValue = "Reports Download")
@RestController
@RequestMapping(value = "/v1/reportDownload")

public class BarredUserlistDownload {

	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	public static final Log log = LogFactory.getLog(BarredUserlistDownload.class.getName());

	/**
	 * 
	 * @param httpServletRequest
	 * @param headers
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

	@PostMapping(value = "/viewbarredUserList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "view barred user list", response = FetchBarredListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })

	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${viewbarredUserList.summary}", description="${viewbarredUserList.description}",

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


	public FileDownloadResponse barredList(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "fileType", required = true) @RequestParam("fileType") String fileType,
			@RequestBody BarredUserListRequestVO request, HttpServletResponse response)

			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException {
		final String methodName = "getbarredUserList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
		try {
			HashMap<String, String> headerMap = new HashMap<String, String>();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			FetchBarredListRequestVO requestVO = new FetchBarredListRequestVO();
			if (!(BTSLUtil.isNullString(request.getMsisdn()))) {
				requestVO.setMsisdn(request.getMsisdn());
				headerMap.put(PretupsI.MSISDN, requestVO.getMsisdn());
			}
			if (!(BTSLUtil.isNullString(request.getUserName()))) {
				requestVO.setUserName(request.getUserName());
				headerMap.put(PretupsI.USER_NAME, requestVO.getUserName());
			}

			if (!(BTSLUtil.isNullString(request.getFromDate()))) {
				requestVO.setFromDate(request.getFromDate());
				headerMap.put(PretupsI.FROM_DATE, requestVO.getFromDate());
			}

			if (!(BTSLUtil.isNullString(request.getTodate()))) {
				requestVO.setTodate(request.getTodate());
				headerMap.put(PretupsI.TO_DATE, requestVO.getTodate());
			}

			if (!(BTSLUtil.isNullString(request.getDomain()))) {
				requestVO.setDomain(request.getDomain());
				headerMap.put(PretupsI.DOMAIN, requestVO.getDomain());
			}

			if (!(BTSLUtil.isNullString(request.getCategory()))) {
				requestVO.setCategory(request.getCategory());
				headerMap.put(PretupsI.CATEGORY, requestVO.getCategory());
			}

			if (!(BTSLUtil.isNullString(request.getGeography()))) {
				requestVO.setGeography(request.getGeography());
				headerMap.put(PretupsI.GEOGRAPHY, requestVO.getGeography());
			}

			if (!(BTSLUtil.isNullString(request.getUserType()))) {
				requestVO.setUserType(request.getUserType());
				headerMap.put(PretupsI.USER_TYPE, requestVO.getUserType());
			}

			if (!(BTSLUtil.isNullString(request.getModule()))) {
				requestVO.setModule(request.getModule());
				headerMap.put(PretupsI.MODULE, requestVO.getModule());
			}

			if (!(BTSLUtil.isNullString(request.getBarredAs()))) {
				requestVO.setBarredAs(request.getBarredAs());
				headerMap.put(PretupsI.BARRED_AS, requestVO.getBarredAs());
			}

			if (!(BTSLUtil.isNullString(request.getBarredtype()))) {
				requestVO.setBarredtype(request.getBarredtype());
				headerMap.put(PretupsI.BARRED_TYPE, requestVO.getBarredtype());
			}

			OAuthenticationUtil.validateTokenApi(headers);
			WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
					.getWebServiceTypeObject("VIEWBARREDUSERLIST".trim());
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
			HttpEntity<?> entity = new HttpEntity<>(requestVO, headers1);
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(uri);
			ResponseEntity<FetchBarredListResponseVO> responseEntity = restTemplate.exchange(uriBuilder.toUriString(),
					HttpMethod.POST, entity, FetchBarredListResponseVO.class);
			if (!responseEntity.getBody().getStatus().equals("200")) {
				fileDownloadResponse.setErrorMap(responseEntity.getBody().getErrorMap());
				fileDownloadResponse.setMessageCode(responseEntity.getBody().getMessageCode());
				fileDownloadResponse.setMessage(responseEntity.getBody().getMessage());
				fileDownloadResponse.setStatus(Integer.valueOf(responseEntity.getBody().getStatus()));
				return fileDownloadResponse;
			}
			String fileName = "ViewBarredListDownload_" + (System.currentTimeMillis());
			String fileType1 = fileType.toUpperCase();
			String fileData = null;

			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(fileType1)) {
				fileData = createExcelXFile(fileName, responseEntity.getBody().getBarredList(),
						request.getHeaderColumns(), headerMap);
			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(fileType1)) {
				fileData = createCSVFile(responseEntity.getBody().getBarredList(), request.getHeaderColumns(),
						headerMap);
			} else
				fileData = createExcelFile(fileName, responseEntity.getBody().getBarredList(),
						request.getHeaderColumns(), headerMap);
			fileDownloadResponse.setFileName(fileName + "." + fileType.toLowerCase());
			fileDownloadResponse.setFileType(fileType1.toLowerCase());
			fileDownloadResponse.setFileattachment(fileData);
			fileDownloadResponse.setStatus(200);
			return fileDownloadResponse;
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
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
				mcomCon.close("getBarredListReport");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("getBarredListReport", " Exited ");
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

	public String createExcelFile(String fileName, HashMap<String, ArrayList<BarredVo>> list,
			List<HeaderColumn> editColumns, HashMap<String, String> headerMap) throws Exception {
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

			for (String i : list.keySet()) {
				ArrayList<BarredVo> record = list.get(i);
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

	public String createExcelXFile(String fileName, HashMap<String, ArrayList<BarredVo>> list,
			List<HeaderColumn> editColumns, HashMap<String, String> headerMap) throws Exception {
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
			serviceHeaderCell.setCellStyle(headerCellStyle);
			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
			List<String> headerMapKeyList = new ArrayList<String>(headerMap.keySet());
			for (String key : headerMapKeyList) {
				Row headerMapRow = sheet.createRow(rowNum++);
				Cell cell0 = headerMapRow.createCell(0);
				cell0.setCellValue(key);
				cell0.setCellStyle(headerCellStyle);
				headerMapRow.createCell(1).setCellValue(headerMap.get(key));
			}
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

			for (String i : list.keySet()) {

				ArrayList<BarredVo> record = list.get(i);
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

	public String createCSVFile(HashMap<String, ArrayList<BarredVo>> List, List<HeaderColumn> editColumns,
			HashMap<String, String> headerMap) throws Exception {
		String fileData = null;
		try (StringWriter writer = new StringWriter();
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			// Processing File Service Header

			ArrayList<String> headerMapKeyList = new ArrayList<String>(headerMap.keySet());
			for (String key : headerMapKeyList) {
				String headerData[] = new String[2];
				headerData[0] = key;
				headerData[1] = headerMap.get(key);
				csvWriter.writeNext(headerData);
			}
			// Processing file header data
			List<String> displayNameList = editColumns.stream().map(HeaderColumn::getDisplayName)
					.collect(Collectors.toList());
			csvWriter.writeNext(displayNameList.stream().toArray(String[]::new));

			// Processing file body data
			List<String> columnNameList = editColumns.stream().map(HeaderColumn::getColumnName)
					.collect(Collectors.toList());
			int columnNameListSize = columnNameList.size();
			for (String i : List.keySet()) {
				ArrayList<BarredVo> record = List.get(i);
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

	private Map<String, String> getMappedColumnValue(ArrayList<BarredVo> record) {
		Map<String, String> mappedColumnValue = new HashMap<>();
		int ListSize = record.size();
		for (int i = 0; i < ListSize; i++) {
			mappedColumnValue.put(PretupsI.NAME, record.get(i).getUserTypeName());
			mappedColumnValue.put(PretupsI.USER_MOBILE_NUMBER, record.get(i).getMsisdn());
			mappedColumnValue.put(PretupsI.MODULE, record.get(i).getModuleName());
			mappedColumnValue.put(PretupsI.BARRED_TIME_AND_DATE, record.get(i).getBarredDate());
			mappedColumnValue.put(PretupsI.BARRING_TYPE1, record.get(i).getBarredTypeName());
			mappedColumnValue.put(PretupsI.REASON_FOR_BARRING, record.get(i).getBarredReason());
			mappedColumnValue.put(PretupsI.BARRED_AS, record.get(i).getBarredAs());
			mappedColumnValue.put(PretupsI.BARRED_BY, record.get(i).getCreatedBy());
		}
		return mappedColumnValue;
	}

}
