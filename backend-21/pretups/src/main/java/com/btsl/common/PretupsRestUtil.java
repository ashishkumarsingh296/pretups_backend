package com.btsl.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelFileConstants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * This class provides basic utility methods
 */

@Component
public class PretupsRestUtil {

	public static final Log _log = LogFactory.getLog(PretupsRestUtil.class.getName());

	/**
	 * This method convert any object into JSON string
	 * 
	 * @param object
	 *            A generic object
	 * @return String JSON string of object
	 * @throws JsonProcessingException
	 */
	public static String convertObjectToJSONString(Object object)
			throws JsonProcessingException {
		final String methodName = "convertObjectToJSONString";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return mapper.writeValueAsString(object).replace("\\/", "/");
	}

	/**
	 * This method convert any JSON string into any Object
	 * 
	 * @param jsonString
	 *            A JSON String
	 * @param type
	 *            A TypeReference object
	 * @return
	 * @return Generic Object
	 * @throws JsonParseException
	 *             , JsonMappingException, IOException
	 */
	public static <T> Object convertJSONToObject(String jsonString,
			TypeReference<T> type) throws JsonParseException,
			JsonMappingException, IOException {
		final String methodName = "convertJSONToObject";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.configure(
				DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return mapper.readValue(jsonString, type);
	}

	/**
	 * This method reads value from property files
	 * 
	 * @param key
	 *            A key
	 * @param type
	 *            A TypeReference object
	 * @return String Value of key
	 * @throws Exception
	 */
	public static String getMessageString(String key)
			throws NoSuchMessageException {
		final String methodName = "#getMessageString";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();
		ApplicationContext applicationContext = applicationContextProvider
				.getApplicationContext();
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		if (null == LocaleContextHolder.getLocale())
			return applicationContext.getMessage(key, null, getSystemLocal());
		else
			return applicationContext.getMessage(key, null,
					LocaleContextHolder.getLocale());

	}

	/**
	 * This method reads value from property files
	 * 
	 * @param key
	 *            A key
	 * @param type
	 *            A TypeReference object
	 * @return String Value of key
	 * @throws Exception
	 */
	public static String getMessageString(String key, Object[] object)
			throws NoSuchMessageException {
		final String methodName = "#getMessageString";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();
		ApplicationContext applicationContext = applicationContextProvider
				.getApplicationContext();

		LogFactory.printLog(methodName, PretupsI.EXITED, _log);

		if (null == LocaleContextHolder.getLocale())
			return applicationContext.getMessage(key, object, getSystemLocal());
		else
			return applicationContext.getMessage(key, object,
					LocaleContextHolder.getLocale());

	}

	/**
	 * Return default system Local
	 * 
	 * @return Locale local of the system
	 */
	public static Locale getSystemLocal() {
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		return new Locale(defaultLanguage,defaultCountry);
	}

	/**
	 * This method get UserVO object either by loginId or extrnalCode
	 * 
	 * @param requestData
	 *            JsonNode
	 * @param connection
	 *            Connection object
	 * @return userVO UserVO object
	 * @throws SQLException
	 *             , Exception
	 * @throws BTSLBaseException
	 */
	public UserVO getUserVOByLoginIdOrExternalCode(JsonNode requestData,
			Connection connection) throws SQLException, BTSLBaseException {
		final String methodName = "getUserVOByLoginIdOrExternalCode";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		UserVO userVO = null;
		if (requestData.has("loginId")
				&& requestData.get("loginId").textValue() != null
				&& !requestData.get("loginId").textValue().equalsIgnoreCase("")) {
			String loginId = requestData.get("loginId").textValue();
			UserDAO userDAO = new UserDAO();
			userVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(connection,
					loginId);

		} else {
			String externalCode = requestData.get("externalCode").textValue();
			UserDAO userDAO = new UserDAO();
			userVO = (UserVO) userDAO.loadAllUserDetailsByExternalCode(
					connection, externalCode);
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return userVO;

	}
	
	/**
	 * This method will get the details of user from the passed JSON request
	 * @param requestData
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	public static UserVO getUserVOByLoginId(JsonNode requestData,
			Connection connection) throws SQLException, BTSLBaseException {
		final String methodName = "getUserVOByLoginId";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		UserVO userVO = null;
		if (requestData.has(PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE)
				&& requestData.get(PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE).textValue() != null
				&& !requestData.get(PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE).textValue().equalsIgnoreCase("")) {
			String loggedInUserId = requestData.get(PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE).textValue();
			UserDAO userDAO = new UserDAO();
			userVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(connection,
					loggedInUserId);
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return userVO;

	}
	
	/**
	 * This method will get the details of user from the passed model object
	 * @param loggedInIdentifierType
	 * @param connection
	 */
	public static UserVO getUserVOByLoginId(String loggedInIdentifierType,
			Connection connection) throws SQLException, BTSLBaseException {
		final String methodName = "getUserVOByLoginId";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		UserVO userVO = null;
		if (BTSLUtil.isEmpty(loggedInIdentifierType) == false) {
			UserDAO userDAO = new UserDAO();
			userVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(connection,
					loggedInIdentifierType);
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return userVO;

	}
	
	/**
	 * This method will validate Login details of user in REST API
	 * @param requestData
	 * @param connection
	 * @param pretupsResponse
	 * @param categoryList
	 * @return
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	public static PretupsResponse<?> validateLoggedInUser(JsonNode requestData,
			Connection connection, PretupsResponse<?> pretupsResponse, String[] categoryList) throws SQLException, BTSLBaseException {
		final String methodName = "validateLoggedInUser";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		if(!requestData.has(PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE)) {
				pretupsResponse.setFormError("request.loggedin.loginid.notpresent");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.REQUEST_LOGGEdIN_LOGINID_NOTPRESENT);
		} else if(!requestData.has(PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE)) {
			pretupsResponse.setFormError("request.loggedin.password.notpresent");
			pretupsResponse.setMessageCode(PretupsErrorCodesI.REQUEST_LOGGEdIN_PASSWORD_NOTPRESENT);
		}
		UserVO userVO = getUserVOByLoginId(requestData, connection);
		if(!pretupsResponse.hasFormError()) {
			if(userVO == null) {
				pretupsResponse.setFormError("user.invalidloginid");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.USER_INVALID_LOGINID);			
			} else if(!BTSLUtil.decryptText(userVO.getPassword()).equals(requestData.get(PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE).textValue())) {
				pretupsResponse.setFormError("user.invalidpassword");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.USER_INVALID_PSWD);
			} else if(!Arrays.asList(categoryList).contains(userVO.getCategoryCode())) {
				pretupsResponse.setFormError("user.notauthorized");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.USER_UNAUTHORIZED);
			}
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return pretupsResponse;

	}

	/**
	 * This method will validate Login details of user in REST API
	 * @param loggedInIdentifierType
	 * @param loggedInIdentifierValue
	 * @param connection
	 * @param pretupsResponse
	 * @param categoryList
	 * @return
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	
	public static PretupsResponse<?> validateLoggedInUser(String loggedInIdentifierType, String loggedInIdentifierValue,
			Connection connection, PretupsResponse<?> pretupsResponse, String[] categoryList) throws SQLException, BTSLBaseException {
		final String methodName = "validateLoggedInUser";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		if(BTSLUtil.isEmpty(loggedInIdentifierType)) {
				pretupsResponse.setFormError("request.loggedin.loginid.notpresent");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.REQUEST_LOGGEdIN_LOGINID_NOTPRESENT);
		} else if(BTSLUtil.isEmpty(loggedInIdentifierValue)) {
			pretupsResponse.setFormError("request.loggedin.password.notpresent");
			pretupsResponse.setMessageCode(PretupsErrorCodesI.REQUEST_LOGGEdIN_PASSWORD_NOTPRESENT);
		}
		UserVO userVO = getUserVOByLoginId(loggedInIdentifierType, connection);
		if(!pretupsResponse.hasFormError()) {
			if(userVO == null) {
				pretupsResponse.setFormError("user.invalidloginid");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.USER_INVALID_LOGINID);			
			} else if(!BTSLUtil.decryptText(userVO.getPassword()).equals(loggedInIdentifierValue)) {
				pretupsResponse.setFormError("user.invalidpassword");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.USER_INVALID_PSWD);
			} else if(!Arrays.asList(categoryList).contains(userVO.getCategoryCode())) {
				pretupsResponse.setFormError("user.notauthorized");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.USER_UNAUTHORIZED);
			}
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return pretupsResponse;

	}

	/**
	 * This method process List of object according its type
	 * 
	 * @param pretupsResponse
	 *            Object of PretupsResponse<JsonNode>
	 * @param bindingResult
	 *            Object of BindingResult
	 * @return Boolean
	 */
	public Boolean processFormAndFieldError(PretupsResponse<?> pretupsResponse,
			BindingResult bindingResult) {
		final String methodName = "processFormAndFieldError";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		if (pretupsResponse.hasFieldError()) {
			Map<String, String> errorMessage = pretupsResponse.getFieldError();
			errorMessage.forEach((key, value) -> {
				bindingResult.rejectValue(key, value);
			});
			return false;
		}

		if (pretupsResponse.hasFormError()) {
			if (pretupsResponse.getParameters() != null) {
				bindingResult.reject(pretupsResponse.getFormError(),
						pretupsResponse.getParameters(), "global");
			} else {
				bindingResult.reject(pretupsResponse.getFormError(), null,
						"global");
			}
			return false;
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return true;
	}

	/**
	 * This method process List of object for getting label
	 * 
	 * @param code
	 *            String of code
	 * @param list
	 *            List of options
	 * @return Boolean
	 */
	public static ListValueVO getOptionDescription(String code,
			List<ListValueVO> list) {

		final String methodName = "getOptionDescription";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		ListValueVO vo = null;
		if (list != null) {
			for (int i = 0, j = list.size(); i < j; i++) {
				vo = list.get(i);
				if (vo.getValue().equalsIgnoreCase(code)) {
					break;
				}
			}
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return vo;
	}

	/**
	 * This method returns actual amount
	 * 
	 * @param amount
	 *            actual amount
	 * @return String
	 */
	public static String getActualAmount(String amount)
			throws BTSLBaseException {
		final String methodName = "getActualAmount";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);

		try {

			if (_log.isDebugEnabled()) {
				_log.debug("Actual Amount:::", amount);
			}
			int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
			
			if ((Integer.parseInt(amount) % amountMultFactor) > 0) {
				double rechargeAmount = Double.parseDouble(amount)
						/ amountMultFactor;
				return Double.toString(rechargeAmount);
			} else {
				long rechargeAmount = Long.parseLong(amount)
						/ amountMultFactor;
				return Long.toString(rechargeAmount);
			}
		} catch (NumberFormatException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}

	}

	/**
	 * Check file at specific location
	 * @param fileNameConstants
	 * @param fileLocationConstants
	 * @return
	 * @throws BTSLBaseException
	 */
	public static File getFileForTemplate(String fileNameConstants,
			String fileLocationConstants) throws BTSLBaseException {
		String methodName = "getFileForTemplate";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		if (BTSLUtil.isNullString(fileLocationConstants)
				|| BTSLUtil.isNullString(fileNameConstants)) {
			throw new BTSLBaseException(PretupsRestUtil.getMessageString("invalid.filename.or.location"));
		}
		String fileLocation;
		if (Constants.getProperty(fileLocationConstants).endsWith("/")
				|| Constants.getProperty(fileLocationConstants).endsWith("\\"))
		{
			StringBuffer msg=new StringBuffer("");        	
        	msg.append(Constants.getProperty(fileLocationConstants));        	
        	msg.append(Constants.getProperty(fileNameConstants));        	       	
        	msg.append(BTSLUtil.getTimestampFromUtilDate(new Date()).getTime());        	
        	msg.append(PretupsI.CSV_EXT);
        	
			fileLocation = msg.toString();
		} 
		
		else 
		{
			StringBuffer msg=new StringBuffer("");        	
        	msg.append(Constants.getProperty(fileLocationConstants));
        	msg.append(File.pathSeparator);
        	msg.append(Constants.getProperty(fileNameConstants));
        	msg.append(BTSLUtil.getTimestampFromUtilDate(new Date()).getTime());        	
        	msg.append(PretupsI.CSV_EXT);
			fileLocation = msg.toString();
		}

		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return new File(fileLocation);
	}
	
	
	
	public static File getFileForTemplate(String fileNameConstants,
			String fileLocationConstants, String fileRefName) throws BTSLBaseException {
		String methodName = "getFileForTemplate";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		if (BTSLUtil.isNullString(fileLocationConstants)
				|| BTSLUtil.isNullString(fileNameConstants)) {
			throw new BTSLBaseException(PretupsRestUtil.getMessageString("invalid.filename.or.location"));
		}
		String fileLocation;
		if (Constants.getProperty(fileLocationConstants).endsWith("/")
				|| Constants.getProperty(fileLocationConstants).endsWith("\\"))
		{
			StringBuffer msg=new StringBuffer("");        	
        	msg.append(Constants.getProperty(fileLocationConstants));        	
        	msg.append(Constants.getProperty(fileNameConstants));        	
        	msg.append("_");        	
        	msg.append(fileRefName);
        	msg.append("_");        	
        	msg.append(BTSLUtil.getTimestampFromUtilDate(new Date()).getTime());        	
        	msg.append(PretupsI.CSV_EXT);
        	
			fileLocation = msg.toString();
		} 
		
		else 
		{
			StringBuffer msg=new StringBuffer("");        	
        	msg.append(Constants.getProperty(fileLocationConstants));
        	msg.append(File.pathSeparator);
        	msg.append(Constants.getProperty(fileNameConstants));        	
        	msg.append("_");        	
        	msg.append(fileRefName);
        	msg.append("_");        	
        	msg.append(BTSLUtil.getTimestampFromUtilDate(new Date()).getTime());        	
        	msg.append(PretupsI.CSV_EXT);
			fileLocation = msg.toString();
		}

		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return new File(fileLocation);
	}
	

	/**
	 * Create file at specific location
	 * @param fileNameConstants
	 * @param fileLocationConstants
	 * @return
	 * @throws BTSLBaseException
	 */
	public static File getFile(String fileNameConstants,
			String fileLocationConstants) throws BTSLBaseException {
		String methodName = "getFile";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		if (BTSLUtil.isNullString(fileLocationConstants)
				|| BTSLUtil.isNullString(fileNameConstants)) {
			throw new BTSLBaseException(
					PretupsRestUtil
							.getMessageString("invalid.filename.or.location"));
		}
		String fileLocation;
		if (Constants.getProperty(fileLocationConstants).endsWith("/")
				|| Constants.getProperty(fileLocationConstants).endsWith("\\")) {
			fileLocation = Constants.getProperty(fileLocationConstants)
					+ fileNameConstants;
		} else {
			fileLocation = Constants.getProperty(fileLocationConstants)
					+ File.pathSeparator
					+ fileNameConstants;
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return new File(fileLocation);
	}

	/**
	 * Check for file location
	 * @param fileLocation
	 * @throws BTSLBaseException
	 */
	public static void checkForLocation(String fileLocation)
			throws BTSLBaseException {
		String methodName = "checkForLocation";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		try {
			File file = new File(fileLocation);
			if (!file.isDirectory()) {
				file.mkdirs();
			}
		} catch (SecurityException e) {
			throw new BTSLBaseException(e);
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
	}
	
	/**
	 * Check for ampty array
	 * @param dataArray
	 * @return
	 */
	public static Boolean checkIfEmpty(Object[] dataArray){
		String methodName = "checkIfEmpty";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		Boolean isEmpty = new Boolean(true);
		if(dataArray.length == 0){
			return isEmpty;
		}
		for (Object value : dataArray) {
			if(value != null){
				isEmpty = false;
				break;
			}
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return isEmpty;
	}
	
	/**
	 * Read csv file and return processor for further processing
	 * 
	 * @param filePath
	 * @return processor
	 * @throws BTSLBaseException
	 */
	public static RowListProcessor readCsvFile(String filePath, Integer noOfLineToSkip) throws BTSLBaseException {
		String methodName = "readCsvFile";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		try (FileReader fileReader = new FileReader(filePath)) {
			CsvParserSettings parserSettings = new CsvParserSettings();
			parserSettings.setLineSeparatorDetectionEnabled(true);

			RowListProcessor processor = new RowListProcessor();
			parserSettings.setProcessor(processor);
//			parserSettings.setHeaderExtractionEnabled(true);
			parserSettings.setSkipEmptyLines(false);
			parserSettings.setNumberOfRowsToSkip(noOfLineToSkip);

			CsvParser parser = new CsvParser(parserSettings);
			parser.parse(fileReader);

			return processor;
		} catch (IOException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}
	}
	
	
	/**
	 * Write csv file at given location
	 * 
	 * @param dataMap
	 * @param file
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("unchecked")
	public static void writeCSVFile(Map<String, Object> dataMap, File file)
			throws BTSLBaseException {
		String methodName = "writeCSVFile";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		FileOutputStream fos =null;
		Writer writer = null;
		CsvWriter csvWriter = null;
		try {
		fos = new FileOutputStream(file);
		writer = new BufferedWriter(new OutputStreamWriter(fos));
			CsvWriterSettings settings = new CsvWriterSettings();
               
			csvWriter = new CsvWriter(writer, settings);
			StringBuilder row = new StringBuilder();
			if(dataMap.containsKey(PretupsI.HEADER_MESSAGE)){
				csvWriter.writeRow(PretupsRestUtil.getMessageString(dataMap.get(PretupsI.HEADER_MESSAGE).toString()));
			}
			
			if(dataMap.containsKey(PretupsI.COLUMN_HEADER_KEY)){
				String[] columns = (String[]) dataMap
						.get(PretupsI.COLUMN_HEADER_KEY);
				for (String column : columns) {
					if (row.length() > 0) {
						row.append(",");
					}
					row.append(column);
				}
				csvWriter.writeRow(row.toString());
			}
			
			List<String> list = (List<String>) dataMap.get(PretupsI.DATA);
			for (String object : list) {
				csvWriter.writeRow(object);
			}
		} catch (IOException e) {
			throw new BTSLBaseException(e);
		} finally {
			try{			
				if (fos !=null) {
					fos.close();
				}
				
				if(csvWriter!=null){
					csvWriter.close();
				}
			} catch(Exception e) {
				_log.error(methodName, "Exception:e=" + e);
	            _log.errorTrace(methodName, e);
			}
			try {
				if(writer!=null){
					writer.close();
				}
			} catch(Exception e) {
				_log.error(methodName, "Exception:e=" + e);
	            _log.errorTrace(methodName, e);
			}
			try {
				closeOutputStreams(fos);
			} catch(Exception e) {
				_log.error(methodName, "Exception:e=" + e);
	            _log.errorTrace(methodName, e);
			}			
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}
	}

	/**
	 * Close Input stream
	 * @param in
	 * @throws IOException
	 */
	public static void closeInputStreams(InputStream in) throws BTSLBaseException{
		if(in != null){
			try {
				in.close();
			} catch (IOException e) {
				throw new BTSLBaseException(e);
			}
		}
	}
	
	/**
	 * Close output stream
	 * @param out
	 * @throws IOException
	 */
	public static void closeOutputStreams(OutputStream out) throws BTSLBaseException{
		if(out != null){
			try {
				out.notify();
				out.close();
			} catch (IOException e) {
				throw new BTSLBaseException(e);
			}
		}
	}
	
	
		/**
		 * Read csv file with limited rows and return processor for further processing
		 * 
		 * @param filePath
		 * @return processor
		 * @throws BTSLBaseException
		 */
	 	
		public static RowListProcessor readCsvFileWithLimitedRowNum(String filePath, Integer numberOfRecordsToRead) throws BTSLBaseException {
			String methodName = "readCsvFile";
			LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
			try (FileReader fileReader = new FileReader(filePath)) {
			CsvParserSettings parserSettings = new CsvParserSettings();
				parserSettings.setLineSeparatorDetectionEnabled(true);
	
				RowListProcessor processor = new RowListProcessor();
				parserSettings.setProcessor(processor);
				parserSettings.setSkipEmptyLines(false);
				parserSettings.setNumberOfRecordsToRead(numberOfRecordsToRead);
	
				CsvParser parser = new CsvParser(parserSettings);
				parser.parse(fileReader);
	
				return processor;
			} catch (IOException e) {
				throw new BTSLBaseException(e);
			} finally {
				LogFactory.printLog(methodName, PretupsI.EXITED, _log);
			}
		}
		
		/**
         * This method process form error for spring module
         * @param  errors {@link Map}
         * @param  bindingResult {@link BindingResult}
         * @return {@link Boolean}
         */
         public Boolean processFieldError(Map<String, String> errors, BindingResult bindingResult) {
             final String methodName = "processFieldError";
             LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
             
             if(!errors.isEmpty()){
                  errors.forEach((field, message) -> {
                       bindingResult.rejectValue(field, message);
                  });
                  return false;
             }
             return true;
         }
         
         public void writeExcel(String p_excelID, String[][] p_strArr, String[][] p_headerArray, String p_heading, int p_margeCont, Locale p_locale, String p_fileName) throws IOException, WriteException, BTSLBaseException {
		        if (_log.isDebugEnabled()) {
		            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr.toString() + " p_headerArray=" + p_headerArray + " p_heading=" + p_heading + " p_margeCont=" + p_margeCont + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
		        }
		        final String METHOD_NAME = "writeExcel";
		        WritableWorkbook workbook = null;
		        WritableSheet worksheet = null;
		        try {
		            String fileName = p_fileName;
		            workbook = Workbook.createWorkbook(new File(fileName));
		            worksheet = workbook.createSheet("First Sheet", 0);
		            String key = null;
		            String keyName = null;
		            Label label = null;

		            WritableFont times16font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
		            WritableCellFormat times16format = new WritableCellFormat(times16font);


		            WritableFont times12font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);


		            WritableFont data = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true);
		            WritableCellFormat dataFormat = new WritableCellFormat(data);


		            WritableFont times20font = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD, true);
		            WritableCellFormat times20format = new WritableCellFormat(times20font);

		            times20format.setAlignment(jxl.format.Alignment.CENTRE);

		            int colnum = p_strArr[0].length;
		            int[] indexMapArray = new int[colnum];

                 keyName = PretupsRestUtil.getMessageString(p_heading);
		            
		            label = new Label(0, 0, keyName, times20format);// Haeding
		            worksheet.mergeCells(0, 0, colnum - 1, 0);
		            worksheet.addCell(label);

		            int cols = p_headerArray[0].length;
		            for (int row = 0; row < cols; row++)// Header Headings
		            {
		                key = p_headerArray[0][row];
		                keyName = null;
		                keyName = PretupsRestUtil.getMessageString(key);
		                label = new Label(0, row + 1, keyName, times16format);
		                worksheet.mergeCells(0, row + 1, p_margeCont, row + 1);
		                worksheet.addCell(label);
		            }
		            for (int row = 0; row < cols; row++)// Header Headings value
		            {
		                keyName = null;
		                keyName = p_headerArray[1][row];
		                label = new Label(p_margeCont + 1, row + 1, keyName, dataFormat);
		                worksheet.mergeCells(p_margeCont + 1, row + 1, 3 * p_margeCont, row + 1);
		                worksheet.addCell(label);
		            }
		            int length = p_strArr[0].length;
		            String indexStr = null;
		            for (int col = 0; col < length; col++)// column heading
		            {
		                indexStr = null;
		                key = p_strArr[0][col];
		                keyName = null;
		                keyName = PretupsRestUtil.getMessageString(key);
		                indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
		                if (indexStr == null) {
		                    indexStr = String.valueOf(col);
		                }
		                indexMapArray[col] = Integer.parseInt(indexStr);
		                label = new Label(indexMapArray[col], cols + 2, keyName, times16format);
		                worksheet.addCell(label);
		            }
		            // setting for the vertical freeze panes
		            SheetSettings sheetSetting = new SheetSettings();
		            sheetSetting = worksheet.getSettings();
		            sheetSetting.setVerticalFreeze(cols + 3);

		            // setting for the horizontal freeze panes
		            SheetSettings sheetSetting1 = new SheetSettings();
		            sheetSetting1 = worksheet.getSettings();
		            sheetSetting1.setHorizontalFreeze(6);
		            int len = p_strArr.length;
		            int lenInt = 0;
		            for (int row = 1; row < len; row++)// column value
		            {
		                lenInt = p_strArr[row].length;
		                for (int col = 0; col < lenInt; col++) {
		                    label = new Label(indexMapArray[col], row + cols + 2, p_strArr[row][col], dataFormat);
		                    worksheet.addCell(label);
		                }
		            }
		            workbook.write();
		        } catch (Exception e) {
		            _log.errorTrace(METHOD_NAME, e);
		            _log.error("writeExcel", " Exception e: " + e.getMessage());
		            throw new BTSLBaseException(e);
		        } finally {
		            try {
		                if (workbook != null) {
		                    workbook.close();
		                }
		            } catch (Exception e) {
		                _log.errorTrace(METHOD_NAME, e);
		            }
		            worksheet = null;
		            workbook = null;
		            if (_log.isDebugEnabled()) {
		                _log.debug("writeExcel", " Exiting");
		            }


		        }
		    }
		/**
		 * This method will return the cardGroupSetId
		 * @param con
		 * @param dataNode
		 * @return
		 * @throws BTSLBaseException
		 */
        public static CardGroupSetVO getCardGroupSet(Connection con, JsonNode dataNode) throws BTSLBaseException {
     		CardGroupDAO cardGroupDAO = new CardGroupDAO();
     		ArrayList<CardGroupSetVO> cardGroupList = cardGroupDAO.loadCardGroupSet(con, dataNode.get("networkCode").textValue(), dataNode.get("moduleCode").textValue());
     		return getCardGroupSet(dataNode, cardGroupList);
     	}
         
        public static CardGroupSetVO getCardGroupSet(JsonNode dataNode, ArrayList<CardGroupSetVO> cardGroupList) throws BTSLBaseException {
        	final String methodName = "getCardGroupSet";
        	if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered");
            }
      		HashMap<String, CardGroupSetVO> cardGroupSetMap = new HashMap<String, CardGroupSetVO>();
      		StringBuilder key = new StringBuilder();
      		for(CardGroupSetVO cardGroupSetVO : cardGroupList) {
      			key.setLength(0);
      			key.append(cardGroupSetVO.getCardGroupSetName()).append("_").append(cardGroupSetVO.getServiceTypeDesc()).
      				append("_").append(cardGroupSetVO.getSubServiceTypeDescription());
      			cardGroupSetMap.put(key.toString(), cardGroupSetVO);
      		}
      		CardGroupSetVO cardGroupSetVO = null;
      		key = new StringBuilder();
      		key.append(dataNode.get("cardGroupSetName").textValue()).append("_").append(dataNode.get("serviceTypeDesc").textValue()).
      		append("_").append(dataNode.get("subServiceTypeDesc").textValue());
      		if(cardGroupSetMap.containsKey(key.toString())) {
      			cardGroupSetVO = cardGroupSetMap.get(key.toString());
      		}
      		if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting, cardGroupSetVO = " + cardGroupSetVO);
            }
      		return cardGroupSetVO;
      	}
        
        public static void setLoginDetailsInRequest(Map<String, Object> requestObject, UserVO userVo) {
        	final String methodName = "getCardGroupSet";
        	if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered");
            }
        	if(userVo == null) {
        		return;
        	}
        	if(requestObject == null) {
        		requestObject = new HashMap<String, Object>();
        	}
        	requestObject.put(PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE, userVo.getLoginID());
        	requestObject.put(PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE, userVo.getPassword());
        }
		
        public boolean validateUser(String identifierType,String identifierValue,String networkCode,Connection con) throws BTSLBaseException{
        	final String methodName = "validateIdentifierValue";
    		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
    		if(BTSLUtil.isEmpty(identifierType)) {
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BLANK_IDENTIFIER_TYPE, 0,null,null);
    		} else if(BTSLUtil.isEmpty(identifierValue)) {
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BLANK_IDENTIFIER_VALUE, 0,null,null);
    		}
    		UserDAO userDAO = new UserDAO();
    		boolean  valid =false;
    		if(PretupsI.MSISDN.equalsIgnoreCase(identifierType)){
    		valid = userDAO.validateUserLoginIdorMsisdn(null, identifierValue, networkCode, con);
    		}
    		else if(PretupsI.LOGINID.equalsIgnoreCase(identifierType)){
        		valid = userDAO.validateUserLoginIdorMsisdn(identifierValue,null , networkCode, con);
        		}
    		if(valid == false){
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_IDENTIFIER_VALUE, 0,null,null);
    		}
    		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
    		return valid;

        }
        
        public boolean validateUserForActOrPreAct(String identifierType,String identifierValue,String networkCode,Connection con) throws BTSLBaseException{
        	final String methodName = "validateIdentifierValue";
    		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
    		if(BTSLUtil.isEmpty(identifierType)) {
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BLANK_IDENTIFIER_TYPE, 0,null,null);
    		} else if(BTSLUtil.isEmpty(identifierValue)) {
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BLANK_IDENTIFIER_VALUE, 0,null,null);
    		}
    		UserDAO userDAO = new UserDAO();
    		boolean  valid =false;
    		if(PretupsI.MSISDN.equalsIgnoreCase(identifierType)){
    		valid = userDAO.validateUserForActOrPreActLoginIdorMsisdn(null, identifierValue, networkCode, con);
    		}
    		else if(PretupsI.LOGINID.equalsIgnoreCase(identifierType)){
        		valid = userDAO.validateUserForActOrPreActLoginIdorMsisdn(identifierValue,null , networkCode, con);
        		}
    		if(valid == false){
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0,null,null);
    		}
    		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
    		return valid;

        }
}
