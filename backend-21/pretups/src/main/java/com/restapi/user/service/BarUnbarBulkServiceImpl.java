package com.restapi.user.service;

import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BarFileProccesingLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.subscriber.web.BarredUserBulkForm;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;

@Service("BarUnbarBulkService")
public class BarUnbarBulkServiceImpl implements BarUnbarBulkService {

	public static final Log LOG = LogFactory.getLog(BarUnbarBulkServiceImpl.class.getName());
	public static final String classname = "BarUnbarBulkServiceImpl";

	@Override
	public BaseResponse barringUnbarringBulkByAdmin(Connection con, HttpServletResponse responseSwag,
			BulkBarredRequestVO requestVO, UserVO userVO, String type) throws BTSLBaseException {

		final String METHOD_NAME = "barUnbarBulkByAdminImpl";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		HashMap<String, String> fileDetailsMap = null;
		boolean isUploaded = false;
		ReadGenericFileUtil fileUtil = null;
		BufferedReader br = null;
		InputStream is = null;
		InputStreamReader inputStreamReader = null;
		String line = null;
		boolean isFileUploaded = false;

		try {

			// this section checks for the valid name for the file
			boolean message = BTSLUtil.isValideFileName(requestVO.getFileName());
			if (requestVO.getFileName().length() > 30) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LENGTH_EXCEED,
						new String[] { "File name", "30" });
			}

			if (!message) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_NAME1, 0, null);
			}

			// Cross site Scripting removal
			fileDetailsMap = new HashMap<String, String>();
			fileUtil = new ReadGenericFileUtil();
			fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFile());
			fileDetailsMap.put(PretupsI.FILE_TYPE, requestVO.getFileType());
			validateFileDetailsMap(fileDetailsMap);

			byte[] data = fileUtil.decodeFile(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT));
			is = new ByteArrayInputStream(data);
			inputStreamReader = new InputStreamReader(is);
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
				if (!isFileContentValid) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT, 0,
							null);

				}
			}
			String dir = Constants.getProperty("UploadBarFilePath");

			String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
			String fileSize = Constants.getProperty("OTHER_FILE_SIZE");
			if (BTSLUtil.isNullString(fileSize)) {
				fileSize = String.valueOf(0);
			}

			// upload file to server
			if (requestVO.getFileType().equals("txt")) {
				requestVO.setFileType(BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT));
				isFileUploaded = BTSLUtil.uploadFileToServerWithHashMap(fileDetailsMap, dir, contentType,
						"barredUserBulk", Long.parseLong(fileSize), data, requestVO.getFileType());
			}

			if (isFileUploaded) {

				// now process uploaded file2
				if (type.equalsIgnoreCase("Bar")) {
					response = this.processFile(requestVO, userVO);
				}

				else {
					response = this.processUnbarFile(requestVO, userVO, con);
				}

			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (BTSLUtil.isNullString(response.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.BARRING_ACTION_NOT_PERFORMED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.BARRING_ACTION_NOT_PERFORMED);
		}

		return response;

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

	public void validateFileDetailsMap(HashMap<String, String> fileDetailsMap) throws BTSLBaseException {

		if (!BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_NAME))
				&& !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT))) {
			validateFileName(fileDetailsMap.get(PretupsI.FILE_NAME)); // throw exception
		} else {
			LOG.error("validateFileInput", "FILENAME/FILEATTACHMENT IS NULL");
			throw new BTSLBaseException(this, "validateFileInput", PretupsErrorCodesI.INVALID_FILE_INPUT,
					PretupsI.RESPONSE_FAIL, null);

		}

	}

	public BaseResponse processFile(BulkBarredRequestVO requestVO, UserVO userVO) throws BTSLBaseException, IOException {
		final String METHOD_NAME = "procesFile";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		FileReader fileReader = null; // file reader
		BufferedReader bufferReader = null;
		String tempStr = null;
		ArrayList totMsisdn = new ArrayList();
		String msisdnStr = null;
		StringTokenizer parser = null;
		String msisdnPrefix = null;
		NetworkPrefixVO networkPrefixVO = null;
		BarredUserVO barredUserVO = null;
		StringBuffer invalidMsisdn = new StringBuffer();
		int count = 0;
		int index = 0;
		ArrayList msisdnList = new ArrayList();
		int listindex = 0;
		boolean barreduserdetails = false;
		String contentsSize = Constants.getProperty("NO_OF_CONTENTS");
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		BarredUserBulkForm theForm = new BarredUserBulkForm();

		// read the upload path from the constant.props file.
		String filePath = Constants.getProperty("UploadBarFilePath");
		// read the delemeter for the file.
		String delim = Constants.getProperty("DelimiterforuploadBarFile");
		// if delimeter is null then set it to blank space
		if (BTSLUtil.isNullString(delim)) {
			delim = " ";
		}
		try {
			fileReader = new FileReader("" + filePath + requestVO.getFileName());
			bufferReader = new BufferedReader(fileReader);
			if (bufferReader.ready()) {
				// read the file line by line until line read is null
				while (!BTSLUtil.isNullString(tempStr = bufferReader.readLine())) {
					parser = new StringTokenizer(tempStr, delim);
					while (parser.hasMoreTokens()) {
						msisdnStr = parser.nextToken().trim();
						totMsisdn.add(msisdnStr);
					}
					parser = null;
					tempStr = null;
				}
			}
			if (totMsisdn.size() > Integer.parseInt(contentsSize)) {
				BarFileProccesingLog.log("Process File", null, null, 1, "Fail", "Too many numbers in file", null);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_SIZE_LARGE, 0, null);
			}

			else {
				while (totMsisdn.size() != count) {
					msisdnStr = (String) totMsisdn.get(count);
					count++;

					// check if the msisdn is valid or not. If valid then enter
					// it into msisdn array
					// otherwise add it to invalid msisdn string
					// Change ID=ACCOUNTID
					// FilteredMSISDN is replaced by
					// getFilteredIdentificationNumber
					// isValidMsisdn is replaced by isValidIdentificationNumber
					// This is done because this field can contains msisdn or
					// account id

					if (!BTSLUtil.isValidIdentificationNumber(PretupsBL.getFilteredIdentificationNumber(msisdnStr))) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("processFile", "Not a valid MSISDN" + msisdnStr);
						}
						invalidMsisdn.append(msisdnStr);
						invalidMsisdn.append(", ");
					} else {

						// Change ID=ACCOUNTID
						// FilteredMSISDN is replaced by
						// getFilteredIdentificationNumber
						// This is done because this field can contains msisdn
						// or account id

						msisdnPrefix = PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredIdentificationNumber(msisdnStr));
						networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
						if (networkPrefixVO != null) {

							// check if mobile number is from the network
							// supported by session user

							if (userVO.getNetworkID().equalsIgnoreCase(networkPrefixVO.getNetworkCode())) {
								barredUserVO = new BarredUserVO();
								String networkCode = userVO.getNetworkID();
								barredUserVO.setCreatedBy(userVO.getUserID());
								barredUserVO.setModifiedBy(userVO.getUserID());
								barredUserVO.setModule(requestVO.getModule());
								barredUserVO.setNetworkCode(networkCode);
								barredUserVO.setMsisdn(PretupsBL.getFilteredIdentificationNumber(msisdnStr));
								barredUserVO.setBarredReason(requestVO.getBarringReason());
								barredUserVO.setUserType(requestVO.getUserType());
								barredUserVO.setBarredType(requestVO.getBarringType());
								barredUserVO.setBarredTypeName(requestVO.getBarringTypeName());

								// check if mobile number is already bar
								msisdnList.add(listindex, barredUserVO);
								barreduserdetails = true;
								listindex++;
							} else {

								if (LOG.isDebugEnabled()) {
									LOG.debug("Process File", "MSISDN not supported network" + msisdnStr);
								}
								invalidMsisdn.append(msisdnStr + ", ");

							}
						}
					}
					index++;
					barreduserdetails = false;
				}
			}

			boolean fileMoved = false;

//			fileMoved = moveFileToArchive(filePath + requestVO.getFileName(), requestVO.getFileName());
			if (LOG.isDebugEnabled()) {
				LOG.debug("Process File", "file moved status" + fileMoved);
			}

			if (invalidMsisdn != null) {
				theForm.setInvalidMsisdnStr(invalidMsisdn.toString());
			}
			if (msisdnList != null && !msisdnList.isEmpty()) {
				theForm.setMsisdnList(msisdnList);

				// valiadte the msisdn

				response = this.addBarredUser(requestVO, userVO, msisdnList,theForm);

			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT, 0, null);
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (BTSLUtil.isNullString(response.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.USERS_CANNOT_BE_BARRED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.USERS_CANNOT_BE_BARRED);
		} finally {
			if(fileReader!=null) fileReader.close();
			if(bufferReader!=null) bufferReader.close();
		}

		return response;

	}

	public boolean moveFileToArchive(String p_fileName, String p_file) {
		final String METHOD_NAME = "moveFileToArchive";
		if (LOG.isDebugEnabled()) {
			LOG.debug("moveFileToArchive", " Entered ");
		}
		File fileRead = new File(p_fileName);
		File fileArchive = new File("" + Constants.getProperty("UploadBarFilePath"));
		if (!fileArchive.isDirectory()) {
			fileArchive.mkdirs();
		}

		fileArchive = new File("" + Constants.getProperty("UploadBarFilePath") + p_file + "."
				+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime());
		boolean flag = fileRead.renameTo(fileArchive);
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, " Exiting File Moved=" + flag);
		}
		return flag;
	}

	public BaseResponse addBarredUser(BulkBarredRequestVO requestVO, UserVO userVO, ArrayList msisdnList,BarredUserBulkForm theForm)
			throws BTSLBaseException {
		final String METHOD_NAME = "addBarredUser";
		if (LOG.isDebugEnabled()) {
			LOG.debug("METHOD_NAME", "Entered");
		}

		String networkCode = null;
		int count = 0;
		BarredUserDAO barredUserDAO = new BarredUserDAO();
		BarredUserVO barredUserVO = new BarredUserVO();
		BaseResponse response = new BaseResponse();
		Connection con = null;
		MComConnectionI mcomCon = null;
		BTSLMessages BTSLMessage = null;

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			if (PretupsI.DOMAINS_ASSIGNED.equals(userVO.getCategoryVO().getFixedDomains())) {
				ArrayList domainList = userVO.getDomainList();
				if (domainList == null || domainList.isEmpty()) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DOMAIN_ASSIGNED, 0, null);
				}
			}

			String unProcessedMsisdn = barredUserDAO.addBarredUserBulk(con, msisdnList, userVO);
			mcomCon.finalCommit();
			if (unProcessedMsisdn != null && unProcessedMsisdn.length() > 0) {
				if (!BTSLUtil.isNullString(theForm.getInvalidMsisdnStr())) {
					theForm.setInvalidMsisdnStr(theForm.getInvalidMsisdnStr() + unProcessedMsisdn);
				} else {
					theForm.setInvalidMsisdnStr(unProcessedMsisdn);
				}
			}
			BTSLMessage = new BTSLMessages(PretupsErrorCodesI.CHANNEL_USER_BARRED);
			networkCode = userVO.getNetworkID();
			boolean messageSendReq = false;
			if (msisdnList != null && !msisdnList.isEmpty()) {
				barredUserVO = (BarredUserVO) msisdnList.get(0);
			}
			if (PretupsI.MSISDN_VALIDATION.equals(
					((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IDENTIFICATION_NUMBER_VAL_TYPE)))) {
				messageSendReq = true;
			}
			for (int index = 0; index < msisdnList.size(); index++) {
				barredUserVO = (BarredUserVO) msisdnList.get(index);
				if (barredUserVO.isBar()) {
					count++;
					if (messageSendReq) {
						try {
							PushMessage pushMessage = new PushMessage(barredUserVO.getMsisdn(), BTSLMessage, null, null,
									locale, networkCode);
							pushMessage.push();
						} catch (Exception e) {
							LOG.error(METHOD_NAME, "Exception SENDING SMS: " + e.getMessage());
							LOG.errorTrace(METHOD_NAME, e);
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FAILED_TO_SEND_SMS,
									0, null);
						}
					}
				}
			}

			if (count == 0 && !BTSLUtil.isNullString(theForm.getInvalidMsisdnStr())) {
				String arr[] = new String[1];
				arr[0] = theForm.getInvalidMsisdnStr().substring(0, theForm.getInvalidMsisdnStr().length() - 2);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USERS_CANNOT_BE_BARRED, 0, null);
			} else if (count == 0) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USERS_CANNOT_BE_BARRED, 0, null);
			} else if (!BTSLUtil.isNullString(theForm.getInvalidMsisdnStr())) {
				String arr[] = new String[1];
				arr[0] = theForm.getInvalidMsisdnStr().substring(0, theForm.getInvalidMsisdnStr().length() - 2);
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PARTIAL_BARRED,
						new String[] { arr[0] });
			} else {
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.USER_BARRED_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.USER_BARRED_SUCCESS);
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (BTSLUtil.isNullString(response.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.USERS_CANNOT_BE_BARRED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.USERS_CANNOT_BE_BARRED);
		}

		return response;

	}

	public BaseResponse processUnbarFile(BulkBarredRequestVO requestVO, UserVO userVO, Connection con)
			throws BTSLBaseException, IOException {
		final String METHOD_NAME = "procesUnbarFile";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		FileReader fileReader = null; // file reader
		BufferedReader bufferReader = null;
		String tempStr = null;
		ArrayList totMsisdn = new ArrayList();
		String msisdnStr = null;
		StringTokenizer parser = null;
		String msisdnPrefix = null;
		NetworkPrefixVO networkPrefixVO = null;
		BarredUserVO barredUserVO = null;
		StringBuffer invalidMsisdn = new StringBuffer();
		int count = 0;
		int index = 0;
		ArrayList msisdnList = new ArrayList();
		int listindex = 0;
		boolean barreduserdetails = false;
		String contentsSize = Constants.getProperty("NO_OF_CONTENTS");
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BaseResponse response = new BaseResponse();
		BarredUserBulkForm theForm = new BarredUserBulkForm();

		// read the upload path from the constant.props file.
		String filePath = Constants.getProperty("UploadBarFilePath");
		// read the delemeter for the file.
		String delim = Constants.getProperty("DelimiterforuploadBarFile");
		// if delimeter is null then set it to blank space
		if (BTSLUtil.isNullString(delim)) {
			delim = " ";
		}

		try {

			fileReader = new FileReader("" + filePath + requestVO.getFileName());
			bufferReader = new BufferedReader(fileReader);
			if (bufferReader.ready()) {
				// read the file line by line until line read is null
				while (!BTSLUtil.isNullString(tempStr = bufferReader.readLine())) {
					parser = new StringTokenizer(tempStr, delim);
					while (parser.hasMoreTokens()) {
						msisdnStr = parser.nextToken().trim();
						totMsisdn.add(msisdnStr);
					}
					parser = null;
					tempStr = null;
				}
			}

			if (totMsisdn.size() > Integer.parseInt(contentsSize)) {
				BarFileProccesingLog.log("Process File", null, null, 1, "Fail", "Too many numbers in file", null);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_SIZE_LARGE, 0, null);
			} else {
				while (totMsisdn.size() != count) {
					msisdnStr = (String) totMsisdn.get(count);
					count++;

					// check if the msisdn is valid or not. If valid then enter
					// it into msisdn array
					// otherwise add it to invalid msisdn string
					// check if the msisdn is valid or not. If valid then enter
					// it into msisdn array
					// otherwise add it to invalid msisdn string
					// Change ID=ACCOUNTID
					// FilteredMSISDN is replaced by
					// getFilteredIdentificationNumber
					// isValidMsisdn is replaced by isValidIdentificationNumber
					// This is done because this field can contains msisdn or
					// account id

					if (!BTSLUtil.isValidIdentificationNumber(PretupsBL.getFilteredIdentificationNumber(msisdnStr))) {
						if (LOG.isDebugEnabled()) {
							LOG.debug(METHOD_NAME, "Not a valid MSISDN" + msisdnStr);
						}
						invalidMsisdn.append(msisdnStr);
						invalidMsisdn.append(", ");
					} else {
						// Change ID=ACCOUNTID
						// FilteredMSISDN is replaced by
						// getFilteredIdentificationNumber
						// This is done because this field can contains msisdn
						// or account id
						msisdnPrefix = PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredIdentificationNumber(msisdnStr));
						if (((Boolean) (PreferenceCache
								.getSystemPreferenceValue(PreferenceI.NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR)))
										.booleanValue()) {
							networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
							if (networkPrefixVO != null) {
								// check if mobile number is from the network
								// supported by session user
								if (userVO.getNetworkID().equalsIgnoreCase(networkPrefixVO.getNetworkCode())) {
									theForm.setNetworkCode(networkPrefixVO.getNetworkCode());
									barredUserVO = new BarredUserVO();
									String networkCode = userVO.getNetworkID();
									barredUserVO.setCreatedBy(userVO.getUserID());
									barredUserVO.setModifiedBy(userVO.getUserID());
									barredUserVO.setModule(requestVO.getModule());
									barredUserVO.setNetworkCode(networkCode);
									barredUserVO.setMsisdn(PretupsBL.getFilteredIdentificationNumber(msisdnStr));
									barredUserVO.setBarredReason(requestVO.getBarringReason());
									barredUserVO.setUserType(requestVO.getUserType());
									barredUserVO.setBarredType(requestVO.getBarringType());
									barredUserVO.setBarredTypeName(requestVO.getBarringTypeName());

									// check if mobile number is already bar
									msisdnList.add(listindex, barredUserVO);
									listindex++;
									barreduserdetails = true;
								} else {
									if (LOG.isDebugEnabled()) {
										LOG.debug(METHOD_NAME, "MSISDN not supported network" + msisdnStr);
									}
									invalidMsisdn.append(msisdnStr + ", ");
								}
							}
						} else {
							barredUserVO = new BarredUserVO();
							String networkCode = userVO.getNetworkID();
							barredUserVO.setCreatedBy(userVO.getUserID());
							barredUserVO.setModifiedBy(userVO.getUserID());
							barredUserVO.setModule(requestVO.getModule());
							barredUserVO.setNetworkCode(networkCode);
							barredUserVO.setMsisdn(PretupsBL.getFilteredIdentificationNumber(msisdnStr));
							barredUserVO.setBarredReason(requestVO.getBarringReason());
							barredUserVO.setUserType(requestVO.getUserType());
							barredUserVO.setBarredType(requestVO.getBarringType());
							barredUserVO.setBarredTypeName(requestVO.getBarringTypeName());

							// check if mobile number is already bar
							msisdnList.add(listindex, barredUserVO);
							listindex++;
							barreduserdetails = true;
						}

						if (barreduserdetails) {
							BarredUserDAO barredUserDAO = null;
							barredUserDAO = new BarredUserDAO();
							String barType = barredUserVO.getBarredType();
							if (barredUserVO.getBarredType() != null && "ALL".equals(barType))
								barType = null;

							if (!barredUserDAO.isExists(con, barredUserVO.getModule(), barredUserVO.getNetworkCode(),
									barredUserVO.getMsisdn(), barredUserVO.getUserType(), barType)) {
								invalidMsisdn.append(msisdnStr + ",");
							}
						}
					}
					index++;
					barreduserdetails = false;
				}
			}
			boolean fileMoved = false;
//			fileMoved = moveFileToArchive(filePath + requestVO.getFileName(), requestVO.getFileName());
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "file moved status" + fileMoved);
			}
			if (invalidMsisdn != null) {
				theForm.setInvalidMsisdnStr(invalidMsisdn.toString());
			}
			if (msisdnList != null && !msisdnList.isEmpty()) {
				theForm.setMsisdnList(msisdnList);

				// valiadte the msisdn
				response = this.deleteUnBarredUser(requestVO, userVO, msisdnList, theForm);

			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT, 0, null);
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (BTSLUtil.isNullString(response.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.USERS_CANNOT_BE_BARRED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.USERS_CANNOT_BE_BARRED);
		}
		finally {
			if(fileReader!=null) fileReader.close();
			if(bufferReader!=null) bufferReader.close();
		}
		return response;
	}

	public BaseResponse deleteUnBarredUser(BulkBarredRequestVO requestVO, UserVO userVO, ArrayList msisdnList,
			BarredUserBulkForm theForm) throws BTSLBaseException {
		final String METHOD_NAME = "deleteUnBarredUser";
		if (LOG.isDebugEnabled()) {
			LOG.debug("METHOD_NAME", "Entered");
		}

		BaseResponse response = new BaseResponse();
		BarredUserDAO barredUserDAO = new BarredUserDAO();
		BarredUserVO barredUserVO = new BarredUserVO();
		String networkCode = null;
		int count = 0;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		Connection con = null;
		MComConnectionI mcomCon = null;
		BTSLMessages BTSLMessage = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String unProcessedMsisdn = barredUserDAO.deleteBarredUserBulk(con, msisdnList);
			mcomCon.finalCommit();
			if (unProcessedMsisdn != null && unProcessedMsisdn.length() > 0) {
				if (!BTSLUtil.isNullString(theForm.getInvalidMsisdnStr())) {
					theForm.setInvalidMsisdnStr(theForm.getInvalidMsisdnStr() + unProcessedMsisdn);
				} else {
					theForm.setInvalidMsisdnStr(unProcessedMsisdn);
				}
			}
			BTSLMessage = new BTSLMessages(PretupsErrorCodesI.CHANNEL_USER_UNBARRED);
			networkCode = userVO.getNetworkID();
			boolean messageSendReq = false;
			if (msisdnList != null && !msisdnList.isEmpty()) {
				barredUserVO = (BarredUserVO) msisdnList.get(0);
			}
			if (PretupsI.MSISDN_VALIDATION.equals(
					((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IDENTIFICATION_NUMBER_VAL_TYPE)))) {
				messageSendReq = true;
			}
			for (int index = 0; index < msisdnList.size(); index++) {
				barredUserVO = (BarredUserVO) msisdnList.get(index);
				if (barredUserVO.isBar()) {
					count++;
					if (messageSendReq) {
						try {
							PushMessage pushMessage = new PushMessage(barredUserVO.getMsisdn(), BTSLMessage, null, null,
									locale, networkCode);
							pushMessage.push();
						} catch (Exception e) {
							LOG.error(METHOD_NAME, "Exception SENDING SMS: " + e.getMessage());
							LOG.errorTrace(METHOD_NAME, e);
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FAILED_TO_SEND_SMS,
									0, null);
						}
					}
				}
			}

			if (count == 0 && !BTSLUtil.isNullString(theForm.getInvalidMsisdnStr())) {
				String arr[] = new String[1];
				arr[0] = theForm.getInvalidMsisdnStr().substring(0, theForm.getInvalidMsisdnStr().length() - 2);
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USERS_CANNOT_BE_UNBARRED, 0,
						null);
			} else if (count == 0) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USERS_CANNOT_BE_UNBARRED, 0,
						null);
			} else if (!BTSLUtil.isNullString(theForm.getInvalidMsisdnStr())) {
				String arr[] = new String[1];
				arr[0] = theForm.getInvalidMsisdnStr().substring(0, theForm.getInvalidMsisdnStr().length() - 2);
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.PARTIAL_UNBARRED,
						new String[] { arr[0] });
			} else {
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_UNBARRED_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.USER_UNBARRED_SUCCESS);
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (BTSLUtil.isNullString(response.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.USERS_CANNOT_BE_BARRED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.USERS_CANNOT_BE_BARRED);
		}

		return response;
	}

}
