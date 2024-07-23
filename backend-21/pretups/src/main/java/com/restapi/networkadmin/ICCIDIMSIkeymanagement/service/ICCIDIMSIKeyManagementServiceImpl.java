package com.restapi.networkadmin.ICCIDIMSIkeymanagement.service;

import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO.*;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO.*;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.iccidkeymgmt.businesslogic.ICCIDDeleteDAO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyDAO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.serviceI.ICCIDIMSIKeyManagementServiceI;
import com.web.pretups.iccidkeymgmt.businesslogic.PosKeyWebDAO;

@Service("AssociateMSISDNWithICCIDServiceI")
public class ICCIDIMSIKeyManagementServiceImpl implements ICCIDIMSIKeyManagementServiceI {
	public static final Log LOG = LogFactory.getLog(ICCIDIMSIKeyManagementServiceImpl.class.getName());
	public static final String CLASS_NAME = "AssociateMSISDNWithICCIDServiceImpl";

	@Override
	public AssociateMSISDNWithICCIDResponseVO associateMSISDNWithICCID(Connection con, UserVO userVO,
																	   MSISDNAndICCIDRequestVO requestVO, AssociateMSISDNWithICCIDResponseVO response)
			throws BTSLBaseException, Exception {
		final String METHOD_NAME = "associateMSISDNWithICCID";

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		if (requestVO.getIccID() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ICCID_IS_NULL, "");
		if (requestVO.getIccID().isEmpty())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ICCID_IS_EMPTY, "");
		if (requestVO.getMsisdn() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_IS_NULL, "");
		if (requestVO.getMsisdn().isEmpty())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_IS_EMPTY, "");
		if (!PretupsI.MSISDN_PATTERN.matcher(requestVO.getMsisdn()).matches())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_INVALID, "");
		String msisdnPrefix;
		NetworkPrefixVO networkPrefixVO = null;
		String networkCode;
		String filteredMsisdn;
		String newIccid;
		PosKeyVO posKeyVO = null;
		String[] errorArray;
		Date currentDate = new Date(System.currentTimeMillis());
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		filteredMsisdn = PretupsBL.getFilteredMSISDN(requestVO.getMsisdn());
		msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
		networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		if (networkPrefixVO == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.MOBILE_NUMBER_IS_NOT_SUPPORT_FROM_YOUR_NETWORK, "");
		}
		networkCode = networkPrefixVO.getNetworkCode();
		if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.MOBILE_NUMBER_IS_NOT_SUPPORT_FROM_YOUR_NETWORK, "");
		}
		try {
			newIccid = BTSLUtil.calcIccId(requestVO.getIccID(), userVO.getNetworkID());// br
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception " + e);
			LOG.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_INFORMATION_FOUND_FOR_ICCID_IMSI,
					"");
		}
		PosKeyDAO posKeyDAO = new PosKeyDAO();
		PosKeyWebDAO posKeywebDAO = new PosKeyWebDAO();

		int isIccExist = posKeywebDAO.isIccExist(con, newIccid.trim(), userVO.getNetworkID());
		errorArray = new String[2];
		if (isIccExist == 0) {
			posKeyVO = posKeywebDAO.mapIccToMsisdn(con, filteredMsisdn, userVO.getNetworkID(), newIccid,
					userVO.getUserID(), false);
		} else if (isIccExist == 1) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "ICCID already mapped=true");
			}
			errorArray[0] = requestVO.getIccID();
			String msg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.ICCID_IMSI_IS_ALREADY_MAPPED_WITH_MSISDN, errorArray);
			response.setMessageCode(PretupsErrorCodesI.ICCID_IMSI_IS_ALREADY_MAPPED_WITH_MSISDN);
			response.setMessage(msg);

			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.ICCID_IMSI_IS_ALREADY_MAPPED_WITH_MSISDN, errorArray);
		} else if (isIccExist == 7) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "ICCID is from unsupported network");
			}
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.ICC_ID_ASSOCIATED_WITH_MSISDN_IS_NOT_FROM_YOUR_NETWORK, "");
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "ICCID already mapped=true");
			}
			errorArray[0] = requestVO.getIccID();
			String msg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.NO_SUCH_RECORD_FOR_ICCID_IMSI_FOUND_IN_THE_DATABASE, errorArray);
			response.setMessageCode(PretupsErrorCodesI.NO_SUCH_RECORD_FOR_ICCID_IMSI_FOUND_IN_THE_DATABASE);
			response.setMessage(msg);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.NO_SUCH_RECORD_FOR_ICCID_IMSI_FOUND_IN_THE_DATABASE, errorArray);
		}

		if (con != null) {
			if (posKeyVO != null) {

				if (posKeyVO.isSuccessullyUpdated()) {
					con.commit();
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.ICCID_MSISDN_ASSOCIATED);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.MAPPING_DETAILS_ADDED_SUCCESSFULLY, null);
					adminOperationVO.setInfo(resmsg);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);
					response.setIsReAssociate(false);
					response.setStatus((HttpStatus.SC_OK));
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.SERVICE_TYPE_SELECTOR_MAPPING_ADDED_SUCCESSFULLY);
					return response;
				} else {
					posKeyVO = posKeyDAO.loadPosKeyByMsisdn(con, filteredMsisdn);
					if (posKeyVO != null && !posKeyVO.getNetworkCode().equals(userVO.getNetworkID())) {
						if (LOG.isDebugEnabled()) {
							LOG.debug(METHOD_NAME, "ICCID is from unsupported network");
						}
						throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
								PretupsErrorCodesI.ICC_ID_IS_NOT_FROM_YOUR_NETWORK, "");
					}

					if (posKeyVO != null) {
						response.setPreviousSwapedKeyIccID(
								BTSLUtil.calcIccId(posKeyVO.getIccId(), userVO.getNetworkID()));
						response.setMSISDN(requestVO.getMsisdn());
						response.setIccID(requestVO.getIccID());
						response.setIsReAssociate(true);
						response.setStatus((HttpStatus.SC_OK));
						String resmsg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.WARNING_ICCID_IMSI_IS_ALREADY_MAPPED_TO_MSISDN_AND_FOLLOWING_ARE_THE_DETAILS,
								null);
						response.setMessage(resmsg);
						response.setMessageCode(
								PretupsErrorCodesI.WARNING_ICCID_IMSI_IS_ALREADY_MAPPED_TO_MSISDN_AND_FOLLOWING_ARE_THE_DETAILS);

					}
				}

			} else {
				con.rollback();
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MAPPING_DETAILS_ADD_FAILED, "");

			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Exiting");
		}
		return response;

	}

	@Override
	public AssociateMSISDNWithICCIDResponseVO reAssociateMSISDNWithICCID(Connection con, UserVO userVO,
																		 MSISDNAndICCIDRequestVO requestVO, AssociateMSISDNWithICCIDResponseVO response)
			throws BTSLBaseException, Exception {
		final String METHOD_NAME = "reAssociateMSISDNWithICCID";

		if (requestVO.getIccID() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ICCID_IS_NULL, "");
		if (requestVO.getIccID().isEmpty())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ICCID_IS_EMPTY, "");
		if (requestVO.getMsisdn() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_IS_NULL, "");
		if (requestVO.getMsisdn().isEmpty())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_IS_EMPTY, "");
		if (!PretupsI.MSISDN_PATTERN.matcher(requestVO.getMsisdn()).matches())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_INVALID, "");
		String msisdnPrefix;
		NetworkPrefixVO networkPrefixVO = null;
		String networkCode;
		String filteredMsisdn;
		String newIccid;
		PosKeyVO posKeyVO = null;
		Date currentDate = new Date(System.currentTimeMillis());
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		filteredMsisdn = PretupsBL.getFilteredMSISDN(requestVO.getMsisdn());
		msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
		networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		if (networkPrefixVO == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.MOBILE_NUMBER_IS_NOT_SUPPORT_FROM_YOUR_NETWORK, "");
		}
		networkCode = networkPrefixVO.getNetworkCode();
		if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.MOBILE_NUMBER_IS_NOT_SUPPORT_FROM_YOUR_NETWORK, "");
		}
		try {
			newIccid = BTSLUtil.calcIccId(requestVO.getIccID(), userVO.getNetworkID());// br
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception " + e);
			LOG.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_INFORMATION_FOUND_FOR_ICCID_IMSI,
					"");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		PosKeyWebDAO posKeywebDAO = new PosKeyWebDAO();
		filteredMsisdn = PretupsBL.getFilteredMSISDN(requestVO.getMsisdn());
		posKeyVO = posKeywebDAO.mapIccToMsisdn(con, filteredMsisdn, userVO.getNetworkID(),
				BTSLUtil.calcIccId(requestVO.getIccID(), userVO.getNetworkID()), userVO.getUserID(), true);
		if (posKeyVO != null) {
			con.commit();
			final AdminOperationVO adminOperationVO = new AdminOperationVO();
			adminOperationVO.setSource(PretupsI.ICCID_MSISDN_ASSOCIATED);
			adminOperationVO.setDate(currentDate);
			adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.MAPPING_DETAILS_RE_ASSOCIATED_SUCCESSFULLY, null);
			adminOperationVO.setInfo(resmsg);
			adminOperationVO.setLoginID(userVO.getLoginID());
			adminOperationVO.setUserID(userVO.getUserID());
			adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			adminOperationVO.setNetworkCode(userVO.getNetworkID());
			adminOperationVO.setMsisdn(userVO.getMsisdn());
			AdminOperationLog.log(adminOperationVO);

			response.setStatus((HttpStatus.SC_OK));
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.MAPPING_DETAILS_RE_ASSOCIATED_SUCCESSFULLY);
			return response;

		} else {
			con.rollback();
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.MAPPING_DETAILS_RE_ASSOCIATED_FAILED, "");
		}

	}

	public DeleteICCIDResponseVO deleteICCID(Connection con, UserVO userVO, DeleteICCIDRequestVO requestVO,
											 DeleteICCIDResponseVO response) throws BTSLBaseException, Exception {

		final String METHOD_NAME = "deleteICCID";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		if (requestVO.getIccID() == null)
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ICCID_IS_NULL, "");
		if (requestVO.getIccID().isEmpty())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ICCID_IS_EMPTY, "");

		ICCIDDeleteDAO iccidDeleteDao = new ICCIDDeleteDAO();
		ArrayList delIccidList = new ArrayList();
		String iccId = requestVO.getIccID();// grt iccid from form
		String encryptedIccid;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		Date currentDate = new Date(System.currentTimeMillis());

		try {
			encryptedIccid = BTSLUtil.calcIccId(iccId, userVO.getNetworkID());// get encrypted value for iccid Br
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception " + e);
			LOG.errorTrace(METHOD_NAME, e);

			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_INFORMATION_FOUND_FOR_ICCID_IMSI,
					"");
		}
		// iccid
		delIccidList.add(encryptedIccid);
		ArrayList delIccidListVo = iccidDeleteDao.validateICCIDList(con, delIccidList);
		ListValueVO listValueVO = (ListValueVO) delIccidListVo.get(0);
		// this block checks if iccid can be deleted
		if (listValueVO.getLabel().equals(PretupsI.ICCID_DELETEABLE)) {
			iccidDeleteDao.deleteICCID(con, delIccidList);
			con.commit();
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_IMSI_DELETED_SUCCESSFULLY,
					new String[] { iccId });
			final AdminOperationVO adminOperationVO = new AdminOperationVO();
			adminOperationVO.setSource(PretupsI.ICCID_IMSI);
			adminOperationVO.setDate(currentDate);
			adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
			adminOperationVO.setInfo(resmsg);
			adminOperationVO.setLoginID(userVO.getLoginID());
			adminOperationVO.setUserID(userVO.getUserID());
			adminOperationVO.setCategoryCode(userVO.getCategoryCode());
			adminOperationVO.setNetworkCode(userVO.getNetworkID());
			adminOperationVO.setMsisdn(userVO.getMsisdn());
			AdminOperationLog.log(adminOperationVO);
			response.setStatus((HttpStatus.SC_OK));
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.ICCID_IMSI_DELETED_SUCCESSFULLY);

			return response;
		}
		// this block checks if iccid is associated to chnl user
		if (!BTSLUtil.isNullString(listValueVO.getValue())
				&& listValueVO.getLabel().equals(PretupsI.ICCID_NOT_EXISTING)) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.NO_SUCH_RECORD_FOR_ICCID_IMSI_FOUND_IN_THE_DATABASE, "");

		}
		// this block deletes iccids after pressing the delete
		// button at final confirmation page
		iccidDeleteDao.deleteICCID(con, delIccidList);
		con.commit();
		String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_IMSI_DELETED_SUCCESSFULLY,
				new String[] { iccId });
		final AdminOperationVO adminOperationVO = new AdminOperationVO();
		adminOperationVO.setSource(PretupsI.ICCID_IMSI);
		adminOperationVO.setDate(currentDate);
		adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
		adminOperationVO.setInfo(resmsg);
		adminOperationVO.setLoginID(userVO.getLoginID());
		adminOperationVO.setUserID(userVO.getUserID());
		adminOperationVO.setCategoryCode(userVO.getCategoryCode());
		adminOperationVO.setNetworkCode(userVO.getNetworkID());
		adminOperationVO.setMsisdn(userVO.getMsisdn());
		AdminOperationLog.log(adminOperationVO);
		response.setStatus((HttpStatus.SC_OK));
		response.setMessage(resmsg);
		response.setMessageCode(PretupsErrorCodesI.ICCID_IMSI_DELETED_SUCCESSFULLY);

		// end of if
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Exit:return=" + METHOD_NAME);
		}
		return response;
	}

	@Override
	public UploadMSISDNWithICCIDResponseVO uploadMSISDNWithICCID(Connection con, UserVO userVO,
																 MSISDNWithICCIDFileRequestVO requestVO, UploadMSISDNWithICCIDResponseVO responseVO)
			throws BTSLBaseException, Exception {

		final String METHOD_NAME = "uploadMSISDNWithICCID";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		if (requestVO.getFileAttachment() == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT, "");
		}

		if (requestVO.getFileName() == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME, "");
		}
		if (requestVO.getFileType() == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_TYPE, "");
		}

		if (requestVO.getFileAttachment().isEmpty()) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT, "");
		}

		if (requestVO.getFileName().isEmpty()) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME, "");
		}
		if (requestVO.getFileType().isEmpty()) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_TYPE, "");
		}
		boolean isFileUploaded = false;
		BufferedReader br = null;
		String line = null;
		InputStream is = null;
		InputStreamReader inputStreamReader = null;
		String delim = Constants.getProperty(PretupsI.DELIMITER_FOR_UPLOAD_ICCID);
		if (BTSLUtil.isNullString(delim))
			delim = PretupsI.COMMA;

		File requestToFile = null;

		if (!BTSLUtil.isNullString(requestVO.getFileAttachment()) && !BTSLUtil.isNullString(requestVO.getFileName())
				&& !BTSLUtil.isNullString(requestVO.getFileType())) {
			boolean message = BTSLUtil.isValideFileName(requestVO.getFileName());

			if (!message) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_NAME1, 0, null);
			}
		} else {
			LOG.error(METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_INPUT);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_INPUT,
					PretupsI.RESPONSE_FAIL, null);

		}

		if (!((requestVO.getFileType()).equalsIgnoreCase(PretupsI.TEXT_OR_PLAIN))) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPE, "");
		}

		ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
		try {

			String dir = Constants.getProperty(PretupsI.UPLOADPOSKEYSFILEPATH);
			String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
			// Cross site Scripting removal: akanksha
			byte[] data = fileUtil.decodeFile(requestVO.getFileAttachment());// ak
			is = new ByteArrayInputStream(data);
			inputStreamReader = new InputStreamReader(is);
			br = new BufferedReader(inputStreamReader);
			br.readLine();
			String rowsLimit = Constants.getProperty(PretupsI.BATCH_CORRECT_ICCID_MSISDN_MAPPING_FILE_ROWS);
			if (BTSLUtil.isNullString(rowsLimit)) {
				rowsLimit = PretupsI.UPLOAD_MSISDN_ROWS_LIMIT;
			}

			String fileSize = null;
			fileSize = Constants.getProperty("ICCID_MSISDN_FILE_SIZE");
			if (BTSLUtil.isNullString(fileSize)) {
				fileSize = Constants.getProperty("OTHER_FILE_SIZE");
			}
			requestToFile = new File(requestVO.getFileName());
			FileUtils.writeByteArrayToFile(requestToFile, data);
			isFileUploaded = BTSLUtil.uploadFileToServer(requestToFile, data, dir, contentType,
					Long.parseLong(fileSize));

			if (isFileUploaded) {

				String fileName = requestToFile.getName();// accessing name of the
				// file
				boolean message = BTSLUtil.isValideFileName(fileName);// validating
				// name of
				// the
				// file
				// if not a valid file name then throw exception
				if (!message) {
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_NAME1, "");

				}
				// now process uploaded file

				responseVO = this.processICCIDMSISDNFile(con, userVO, fileName, responseVO);
			}
		} catch (Exception e) {

			throw e;
		} finally {
			try {
				requestToFile.delete();
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				throw e;
			}
			try {
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
			} catch (Exception e) {
				throw e;
			}
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				throw e;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exit:return=" + METHOD_NAME);
			}
		}
		return responseVO;
	}

	private UploadMSISDNWithICCIDResponseVO processICCIDMSISDNFile(Connection con, UserVO userVO, String fileName,
																   UploadMSISDNWithICCIDResponseVO response) throws BTSLBaseException, Exception {

		final String METHOD_NAME = "processICCIDMSISDNFile";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		File file = null;
		int updateCount = 0;

		String filePath = Constants.getProperty(PretupsI.UPLOADPOSKEYSFILEPATH);
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		Date currentDate = new Date(System.currentTimeMillis());

		PosKeyWebDAO posKeywebDAO = new PosKeyWebDAO();

		ArrayList<ListValueVO> errorList = new ArrayList<>();
		int recordsCount = 0;
		try {

			updateCount = posKeywebDAO.writeIccMsisdnFileToDatabaseWithErrorResponse(con, filePath + fileName,
					userVO.getUserID(), userVO.getNetworkID(), fileName, errorList, recordsCount);
			if (con != null) {
				if (errorList.size() == 0) {
					con.commit();
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.FILE_UPLOADED_SUCCESSFULLY, null);
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.MSISDN);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
					adminOperationVO.setInfo(resmsg);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);
					response.setStatus((HttpStatus.SC_OK));
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.FILE_UPLOADED_SUCCESSFULLY);
					return response;
				}
				if (updateCount > 0 && errorList.size() > 0) {
					con.commit();
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL, null);
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.MSISDN);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
					adminOperationVO.setInfo(resmsg);
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setMessage(resmsg);
					response.setErrorFlag(PretupsI.TRUE);
					response.setErrorList(errorList);
					response.setMessageCode(PretupsErrorCodesI.FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL);
					downloadErrorLogFile(userVO, response);
					return response;

				} else {
					con.rollback();
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FILE_UPLOAD_FAILED, null);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setMessage(resmsg);
					response.setErrorFlag(PretupsI.TRUE);
					response.setErrorList(errorList);
					response.setMessageCode(PretupsErrorCodesI.FILE_UPLOAD_FAILED);
					downloadErrorLogFile(userVO, response);
					return response;
				}
			}
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			file = new File(filePath, fileName);
			boolean isDeleted = file.delete();
			if (isDeleted) {
				LOG.debug(METHOD_NAME, PretupsI.FILE_DELETED);
			}

			throw e;
		}
		return response;
	}

	public void downloadErrorLogFile(UserVO userVO, UploadMSISDNWithICCIDResponseVO response) throws Exception {
		final String METHOD_NAME = "downloadErrorLogFile";
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered");
		try {
			ArrayList errorList = response.getErrorList();

			String filePath = Constants.getProperty(PretupsI.UPLOADPOSKEYSFILEPATH);
			try {
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory())
					fileDir.mkdirs();
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				LOG.error(METHOD_NAME, "Exception" + e.getMessage());
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DIR_NOT_CREATED, 0, null);
			}
			String fileName = PretupsI.ERROR_LOG_FILE_NAME

					+ BTSLUtil.getFileNameStringFromDate(new Date(System.currentTimeMillis())) + ".csv";

			this.writeErrorLogFile(errorList, fileName, filePath, userVO.getNetworkID(), fileName, true);

			File error = new File(filePath + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			response.setFileAttachment(encodedString);
			response.setFileName(fileName);
			response.setFileType(PretupsI.CSV_EXT);
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			throw e;
		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug(METHOD_NAME, "Exiting:" + METHOD_NAME);
		}
	}

	public void writeErrorLogFile(ArrayList errorList, String _fileName, String filePath, String _networkCode,
								  String uploadedFileNamePath, Boolean headval) throws Exception

	{
		final String methodName = "writeListErrorLogFile";
		String[] splitFileName = uploadedFileNamePath.split("/");
		String uploadedFileName = splitFileName[(splitFileName.length) - 1];
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered: " + methodName);
		}
		Writer out = null;
		File newFile = null;
		File newFile1 = null;
		String fileHeader = null;
		String fileName = null;
		try {
			Date date = new Date(System.currentTimeMillis());
			newFile1 = new File(filePath);
			if (!newFile1.isDirectory())
				newFile1.mkdirs();
			fileName = filePath + _fileName;
			LOG.debug(methodName, "fileName := " + fileName);
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			if (headval) {
				fileHeader = Constants.getProperty(PretupsI.ERROR_FILE_HEADER_MOVEUSER);
				fileHeader = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.ERROR_FILE_HEADER_MOVEUSER, null);

			} else {
				fileHeader = Constants.getProperty(PretupsI.ERROR_FILE_HEADER_PAYOUT);
				fileHeader = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.ERROR_FILE_HEADER_PAYOUT, null);
			}
			newFile = new File(fileName);
			out = new OutputStreamWriter(new FileOutputStream(newFile));
			out.write(fileHeader + PretupsI.NEW_LINE_CHARACTER);
			List<ListValueVO> filterList = (List<ListValueVO>) errorList.stream()
					.sorted(Comparator.comparing(ListValueVO::getLabel)).collect(Collectors.toList());

			for (Iterator<ListValueVO> iterator = filterList.iterator(); iterator.hasNext();) {

				ListValueVO listValueVO = iterator.next();
				out.write(listValueVO.getLabel().concat(PretupsI.COMMA));
				out.write(listValueVO.getValue().concat(PretupsI.COMMA));

				out.write(PretupsI.COMMA);
				out.write(PretupsI.NEW_LINE_CHARACTER);
			}
			out.write(PretupsI.END);

		} catch (Exception e) {
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"writeDataInFile[writeDataInFile]", "", "", "", "Exception:= " + e.getMessage());
			throw e;
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting... ");
			}
			if (out != null)
				try {
					out.close();
				} catch (Exception e) {
					throw e;
				}

		}
	}

	public CorrectMSISDNWithICCIDResponseVO loadCorrectMSISDNICCIDMapping(Connection con, UserVO userVO,
																		  MSISDNAndICCIDRequestVO request, CorrectMSISDNWithICCIDResponseVO response)
			throws BTSLBaseException, Exception {
		final String METHOD_NAME = "loadCorrectMSISDNICCIDMapping";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		String msisdnPrefix;
		NetworkPrefixVO networkPrefixVO = null;
		String networkCode;
		String filteredMsisdn;
		String newIccid;
		PosKeyVO posKeyVO = null;
		ArrayList posKeyVOList = null;
		ArrayList tempList = null;
		boolean matchedFlag = false;
		boolean msisdnFlag = false;
		boolean iccIdFlag = false;

		filteredMsisdn = PretupsBL.getFilteredMSISDN(request.getMsisdn());

		msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

		networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		if (networkPrefixVO == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_SUPPORTING_NETWORK);
		}

		networkCode = networkPrefixVO.getNetworkCode();

		if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_SUPPORTING_NETWORK);
		}

		// get the swapped iccid value of the entered iccid
		try {
			newIccid = BTSLUtil.calcIccId(request.getIccID(), userVO.getNetworkID());// br
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception " + e);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.NO_INFORMATION_FOUND_FOR_ICCID_IMSI);
		}

		PosKeyDAO posKeyDAO = new PosKeyDAO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		// load msisdn information
		posKeyVO = posKeyDAO.loadPosKeyByMsisdn(con, filteredMsisdn);
		if (posKeyVO != null && !posKeyVO.getNetworkCode().equals(userVO.getNetworkID())) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "ICCID is from unsupported network");
			}
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.MOBILE_NUMBER_IS_NOT_SUPPORT_FROM_YOUR_NETWORK);
		}

		posKeyVOList = posKeyDAO.loadICCIDMsisdnDetails(con, filteredMsisdn, newIccid);
		response.setPosKeyList(posKeyVOList);
		if (posKeyVOList == null || posKeyVOList.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "MSISDN ICCID mapping list empty=true");
			}
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_INFORMATION_AVAILABLE);
		}
		tempList = new ArrayList();
		int size = posKeyVOList.size();

		// Case I if size==1 then mapping correct , do nothing
		if (size == 1) {
			posKeyVO = (PosKeyVO) posKeyVOList.get(0);
			if (!posKeyVO.getNetworkCode().equals(userVO.getNetworkID())) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(METHOD_NAME, "ICCID is from unsupported network");
				}
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
						PretupsErrorCodesI.ICC_ID_IS_NOT_FROM_YOUR_NETWORK);
			}
			if (filteredMsisdn.equalsIgnoreCase(BTSLUtil.NullToString(posKeyVO.getMsisdn()))
					&& newIccid.equalsIgnoreCase(BTSLUtil.NullToString(posKeyVO.getIccId()))) {
				matchedFlag = true;
			} else if (filteredMsisdn.equalsIgnoreCase(BTSLUtil.NullToString(posKeyVO.getMsisdn()))) {
				msisdnFlag = true;
			} else if (newIccid.equalsIgnoreCase(BTSLUtil.NullToString(posKeyVO.getIccId()))) {
				iccIdFlag = true;
			}
			if (matchedFlag) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CORRECT_MAPPING_EXISTS);

			} else if (msisdnFlag) {
				tempList.add(posKeyVO);
				posKeyVO = new PosKeyVO();
				tempList.add(posKeyVO);
				response.setPosKeyList(tempList);

			} else if (iccIdFlag) {
				if (BTSLUtil.isNullString(posKeyVO.getMsisdn())) {
					// Case - Ask user to use Assocaite Menu option
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.AS_BOTH_ENTERED_MSISDN_AND_ICCID_IMSI_ARE_FREE_FOR_USE, null);
					response.setMessage(resmsg);
					response.setStatus((HttpStatus.SC_OK));
					response.setFirstIccID(request.getIccID());
					response.setFirstMsisdn(request.getMsisdn());
					response.setMessageCode(PretupsErrorCodesI.AS_BOTH_ENTERED_MSISDN_AND_ICCID_IMSI_ARE_FREE_FOR_USE);
					response.setBothAvailableStatus(true);
					return response;

				} else {
					PosKeyVO temPosKeyVO = new PosKeyVO();
					temPosKeyVO.setIccId("");
					temPosKeyVO.setMsisdn(filteredMsisdn);
					temPosKeyVO.setRegistered(false);
					temPosKeyVO.setCreatedBy("");
					temPosKeyVO.setKey("");
					temPosKeyVO.setNewIccId("");
					temPosKeyVO.setModifiedBy("");
					temPosKeyVO.setSimProfile("");
					tempList.add(temPosKeyVO);
					tempList.add(posKeyVO);
					response.setPosKeyList(tempList);
					// scenario 4

					response.setFirstIccID("");
					response.setFirstMsisdn(filteredMsisdn);
					response.setSecondIccID(posKeyVO.getIccId());
					response.setSecondMsisdn(posKeyVO.getMsisdn());

					String resmsg1 = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_WILL_BE_ASSOCIATE,
							new String[] { temPosKeyVO.getMsisdn(),
									BTSLUtil.getByteSwappedKey(posKeyVO.getIccId(), userVO.getNetworkID()) });
					response.setMessage1(resmsg1);
					String resmsg2 = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.MSISDN_WILL_HAVE_TO_BE_RE_ASSOCIATED,
							new String[] { posKeyVO.getMsisdn() });
					response.setMessage2(resmsg2);

				}
			}

		} else // check which scenario is to be used
		{
			boolean isAdded = false;
			boolean isSecondAddedFirst = false;
			String firstIccId = null;
			String firstMsisdn = null;
			String secondIccId = null;
			String secondMsisdn = null;
			boolean firstRegistered = false;
			boolean secondRegistered = false;

			for (int i = 0; i < size; i++) {
				posKeyVO = (PosKeyVO) posKeyVOList.get(i);
				if (!posKeyVO.getNetworkCode().equals(userVO.getNetworkID()) && posKeyVO.getIccId().equals(newIccid)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(METHOD_NAME, "ICCID is from unsupported network");
					}
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
							PretupsErrorCodesI.ICC_ID_IS_NOT_FROM_YOUR_NETWORK, "");
				}

				if (filteredMsisdn.equalsIgnoreCase(posKeyVO.getMsisdn())) {
					firstIccId = posKeyVO.getIccId();
					firstMsisdn = posKeyVO.getMsisdn();
					firstRegistered = posKeyVO.isRegistered();
					if (isSecondAddedFirst) {
						tempList.remove(0);
					}
					tempList.add(0, posKeyVO);
					isAdded = true;
				} else {
					secondIccId = posKeyVO.getIccId();
					secondMsisdn = posKeyVO.getMsisdn();
					secondRegistered = posKeyVO.isRegistered();
					if (!isAdded) {
						tempList.add(0, new PosKeyVO());
						tempList.add(1, posKeyVO);
						isSecondAddedFirst = true;
					} else {
						tempList.add(1, posKeyVO);
					}
				}
			}
			response.setPosKeyList(tempList);
			response.setFirstIccID(firstIccId);
			response.setFirstMsisdn(filteredMsisdn);
			response.setSecondIccID(secondIccId);
			response.setSecondMsisdn(secondMsisdn);
			if (!(BTSLUtil.isNullString(secondMsisdn))) {
				// Case II
				// scenario 2

				String resmsg1 = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_WILL_BE_ASSOCIATE,
						new String[] { filteredMsisdn,
								BTSLUtil.getByteSwappedKey(secondIccId, userVO.getNetworkID()) });
				String resmsg2 = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_WILL_HAVE_REASSIGNED,
						new String[] { secondMsisdn });

				String resmsg3 = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_IMSI_WILL_BE_AVAILABLE,
						new String[] { BTSLUtil.getByteSwappedKey(firstIccId, userVO.getNetworkID()) });

				response.setMessage1(resmsg1);
				response.setMessage2(resmsg2);
				response.setMessage3(resmsg3);

			}

			else if (BTSLUtil.isNullString(secondMsisdn)) {
				// Case I
				// scenario 1

				String resmsg1 = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_WILL_BE_ASSOCIATE,
						new String[] { filteredMsisdn,
								BTSLUtil.getByteSwappedKey(secondIccId, userVO.getNetworkID()) });
				String resmsg2 = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_IMSI_WILL_BE_AVAILABLE,
						new String[] { BTSLUtil.getByteSwappedKey(firstIccId, userVO.getNetworkID()) });
				response.setMessage1(resmsg1);
				response.setMessage2(resmsg2);

			}

		}
		ArrayList poskeyVOList = null;
		// swapping the iccid key bytes for user view
		if (response.getPosKeyList() != null && !response.getPosKeyList().isEmpty()) {
			poskeyVOList = response.getPosKeyList();
			for (int i = 0, j = poskeyVOList.size(); i < j; i++) {
				posKeyVO = (PosKeyVO) poskeyVOList.get(i);
				posKeyVO.setIccId(BTSLUtil.getByteSwappedKey(posKeyVO.getIccId(), userVO.getNetworkID()));
			}
		}
		PosKeyVO firstPosKeyVO = (PosKeyVO) poskeyVOList.get(0);
		PosKeyVO secondPosKeyVO = (PosKeyVO) poskeyVOList.get(1);
		response.setFirstIccID(firstPosKeyVO.getIccId());
		response.setFirstCreatedOn(firstPosKeyVO.getCreatedOnStr());
		response.setFirstRegisteredOn(firstPosKeyVO.getRegistered());
		response.setFirstModifiedOn(firstPosKeyVO.getModifedOnStr());
		response.setFirstSimProfileID(firstPosKeyVO.getSimProfile());
		response.setFirstNewICCID(firstPosKeyVO.getNewIccId());
		response.setSecondIccID(secondPosKeyVO.getIccId());
		response.setSecondCreatedOn(secondPosKeyVO.getCreatedOnStr());
		response.setSecondRegisteredOn(secondPosKeyVO.getRegistered());
		response.setSecondModifiedOn(secondPosKeyVO.getModifedOnStr());
		response.setSecondSimProfileID(secondPosKeyVO.getSimProfile());
		response.setSecondNewICCID(secondPosKeyVO.getNewIccId());
		String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAD_CORRECT_MSISDN_DETAILS,
				new String[] { PretupsI.SUCCESS_MSG });
		response.setStatus((HttpStatus.SC_OK));
		response.setMessage(resmsg);
		response.setMessageCode(PretupsErrorCodesI.LOAD_CORRECT_MSISDN_DETAILS);
		return response;
	}

	@Override
	public DeleteICCIDResponseVO correctMSISDNICCIDMapping(Connection con, UserVO userVO,
														   CorrectMSISDNICCIDMappingRequestVO request, DeleteICCIDResponseVO response)
			throws BTSLBaseException, Exception {

		final String METHOD_NAME = "correctMSISDNICCIDMapping";

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}
		String filteredMsisdn = null;
		;
		Date currentDate = new Date(System.currentTimeMillis());
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String msisdnPrefix = null;

		int updateCount = 0;
		NetworkPrefixVO networkPrefixVO = null;
		String newIccid = null;
		String networkCode = null;
		PosKeyDAO posKeyDAO = new PosKeyDAO();
		PosKeyWebDAO posKeywebDAO = new PosKeyWebDAO();
		if (!BTSLUtil.isNullString(request.getFirstMSISDN())) {
			filteredMsisdn = PretupsBL.getFilteredMSISDN(request.getFirstMSISDN());
			msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

		} else if (!BTSLUtil.isNullString(request.getSecondMSISDN())) {
			filteredMsisdn = PretupsBL.getFilteredMSISDN(request.getSecondMSISDN());
			msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

		}
		networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		if (networkPrefixVO == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.MOBILE_NUMBER_IS_NOT_SUPPORT_FROM_YOUR_NETWORK);
		}
		networkCode = networkPrefixVO.getNetworkCode();
		if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.MOBILE_NUMBER_IS_NOT_SUPPORT_FROM_YOUR_NETWORK);
		}
		String newFirstIccid = null;
		String newSecondIccid = null;
		try {
			newFirstIccid = BTSLUtil.calcIccId(request.getFirstICCID(), userVO.getNetworkID());// br
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception " + e);
			LOG.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.NO_INFORMATION_FOUND_FOR_ICCID_IMSI);
		}
		try {
			newSecondIccid = BTSLUtil.calcIccId(request.getSecondICCID(), userVO.getNetworkID());// br
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception " + e);
			LOG.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
					PretupsErrorCodesI.NO_INFORMATION_FOUND_FOR_ICCID_IMSI);
		}

		if (request.getSecondMSISDN() == null || request.getSecondMSISDN().isEmpty()) {

			updateCount = posKeyDAO.reUtilizeIccId(con, newFirstIccid, userVO.getUserID());
			if (updateCount > 0) {
				updateCount = 0;

				updateCount = posKeyDAO.assignMsisdnWihIccId(con, filteredMsisdn, newSecondIccid, userVO.getUserID());
			}
		} else if (request.getFirstICCID() == null || request.getFirstICCID().isEmpty()) // Just change MSISDN against ICC ID
		{
			updateCount = posKeyDAO.assignMsisdnWihIccId(con, filteredMsisdn, newSecondIccid, userVO.getUserID());
		} else // Done Intentionally so that if tommorrow we need seperate
		// behaviour
		{
			updateCount = posKeyDAO.reUtilizeIccId(con, newFirstIccid, userVO.getUserID());
			if (updateCount > 0) {
				updateCount = 0;
				updateCount = posKeyDAO.assignMsisdnWihIccId(con, filteredMsisdn, newSecondIccid, userVO.getUserID());
			}
		}
		// for updating temp txn Id in user_phones in case MSISDN exists in
		// it.
		if (updateCount > 0) {
			posKeywebDAO.isMsisdnExistAndUpdateSimTxnId(con, filteredMsisdn, PretupsI.UPD_SIM_TXN_ID);
		}
		if (con != null) {
			if (updateCount > 0) {
				con.commit();
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MAPPING_DETAILS_MODIFIED,
						new String[] { PretupsI.SUCCESS_MSG });
				final AdminOperationVO adminOperationVO = new AdminOperationVO();
				adminOperationVO.setSource(PretupsI.MSISDN);
				adminOperationVO.setDate(currentDate);
				adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
				adminOperationVO.setInfo(resmsg);
				adminOperationVO.setLoginID(userVO.getLoginID());
				adminOperationVO.setUserID(userVO.getUserID());
				adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				adminOperationVO.setNetworkCode(userVO.getNetworkID());
				adminOperationVO.setMsisdn(userVO.getMsisdn());
				AdminOperationLog.log(adminOperationVO);
				response.setStatus((HttpStatus.SC_OK));
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.MAPPING_DETAILS_MODIFIED);
				return response;

			} else {
				con.rollback();
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MAPPING_DETAILS_MODIFIED,
						new String[] { PretupsI.FAILED_MSG });
			}
		}
		return response;
	}

	@Override
	public IccidImsiMsisdnListResponseVO iccidImsiMsisdnListFilterByIccid(Connection con, UserVO userVO,
																		  IccidImsiMsisdnListResponseVO response, String iccidImsi) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "iccidImsiMsisdnListFilterByIccid";

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		String newIccid = "";

		PosKeyVO posKeyVO = null;
		String filteredMsisdn = "";
		String msisdnPrefix;
		NetworkPrefixVO networkPrefixVO = null;
		String networkCode;
		String noSecUser = null;

		ChannelUserVO channelUserVO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		String allowMask=null;

		String tempMsisdn = null;
		try {

			if (iccidImsi != null && iccidImsi.length() > 20) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ICCID_LENGTH_INVALID);
			}

			if (!BTSLUtil.isNullString(Constants.getProperty("ALLOW_SMS_PIN_MASK_POSKEY"))) {
				allowMask = Constants.getProperty("ALLOW_SMS_PIN_MASK_POSKEY");
			}
			else {
				allowMask = PretupsI.NO;
			}

			try {
				newIccid = BTSLUtil.calcIccId(iccidImsi, userVO.getNetworkID());
			} catch (Exception e) {
				LOG.error(METHOD_NAME, "Exception " + e);
				LOG.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_INFO_FOUND_FOR_ICCID);
			}

			PosKeyDAO posKeyDAO = new PosKeyDAO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();

			String status = TypesI.NO;
			String statusUsed = PretupsI.STATUS_NOTEQUAL;

			if (!(BTSLUtil.isNullString(newIccid))) {

				posKeyVO = posKeyDAO.loadPosKey(con, newIccid, null);
				if (posKeyVO == null) {
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.RECORD_NOT_FOUND);
				}

				if (posKeyVO != null && !posKeyVO.getNetworkCode().equals(userVO.getNetworkID())) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(METHOD_NAME, PretupsI.ICCID_UNSUPPORTED_NETWORK);
					}
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
							PretupsErrorCodesI.ICCID_NOT_IN_SUPPORTED_NETWORK);
				}


				if ((BTSLUtil.isNullString(posKeyVO.getMsisdn()))) {
					response.setNoChannelUser(PretupsI.ICCID_NOT_ASSOCIATED);
				} else {
					filteredMsisdn = PretupsBL.getFilteredMSISDN(posKeyVO.getMsisdn());
					msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
					networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
					if (networkPrefixVO == null) {
						throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
								PretupsErrorCodesI.MOBILE_NOT_IN_SUPPORTED_NETWORK);
					}
					networkCode = networkPrefixVO.getNetworkCode();
					if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
						throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
								PretupsErrorCodesI.MOBILE_NOT_IN_SUPPORTED_NETWORK);
					}
				}

				if (BTSLUtil.isNullString(noSecUser)) {
					channelUserVO = channelUserDAO.loadUsersDetails(con, posKeyVO.getMsisdn(), null, statusUsed,
							status);
					if ((channelUserVO != null)) {
						UserDetailsVO userDetailsVO = new UserDetailsVO();
						userDetailsVO.setUserName(channelUserVO.getUserName());
						userDetailsVO.setAddress1(channelUserVO.getAddress1());
						userDetailsVO.setAddress2(channelUserVO.getAddress2());
						userDetailsVO.setCategoryName(channelUserVO.getCategoryName());
						userDetailsVO.setStatus(channelUserVO.getStatus());
						userDetailsVO.setParentName(channelUserVO.getParentName());
						userDetailsVO.setParentMobileNo(channelUserVO.getParentMsisdn());
						userDetailsVO.setParentCategoryName(channelUserVO.getParentCategoryName());
						userDetailsVO.setOwnerName(channelUserVO.getOwnerName());
						userDetailsVO.setOwnerMobileNo(channelUserVO.getOwnerMsisdn());
						userDetailsVO.setOwnerCategoryName(channelUserVO.getOwnerCategoryName());
						userDetailsVO.setSmsPin(channelUserVO.getSmsPin());
						userDetailsVO.setPinRequired(channelUserVO.getPinRequired());
						response.setUserDetailsVO(userDetailsVO);
					}
					if (channelUserVO == null) {
						response.setNoChannelUser(PretupsI.MSISDN_NOT_ASSIGNED);
					}
				}

				if (posKeyVO != null) {
					if (channelUserVO != null && BTSLUtil.isNullString(posKeyVO.getMsisdn())) {
						tempMsisdn = channelUserVO.getMsisdn();
					} else {
						tempMsisdn = posKeyVO.getMsisdn();
					}
				}

				String newStr = BTSLUtil.getByteSwappedKey(posKeyVO.getIccId(), userVO.getNetworkID());
				posKeyVO.setTmpIccID(newStr);

				posKeyVO.setTmpMsisdn(tempMsisdn);
				if (posKeyVO != null) {
					if(PretupsI.YES.equalsIgnoreCase(allowMask))
					{
						posKeyVO.setDecryptKeyMask(posKeyVO.getKey().substring(0, 4)+"**********"+posKeyVO.getKey().substring(((posKeyVO.getKey().length()) - 4),(posKeyVO.getKey().length()) ));
					}
					else {
						posKeyVO.setDecryptKeyMask(posKeyVO.getKey().substring(0, 8));
					}
				}

				response.setPosKeyVO(posKeyVO);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RECORD_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.RECORD_FOUND);
			}

		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

	@Override
	public IccidImsiMsisdnListResponseVO iccidImsiMsisdnListFilterByMsisdn(Connection con, UserVO userVO,
																		   IccidImsiMsisdnListResponseVO response, String msisdn) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "iccidImsiMsisdnListFilterByMsisdn";

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		PosKeyVO posKeyVO = null;
		String filteredMsisdn = "";
		String msisdnPrefix;
		NetworkPrefixVO networkPrefixVO = null;
		String networkCode;

		ChannelUserVO channelUserVO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		String allowMask=null;

		String tempMsisdn = null;

		try {

			if (!BTSLUtil.isNullString(Constants.getProperty("ALLOW_SMS_PIN_MASK_POSKEY"))) {
				allowMask = Constants.getProperty("ALLOW_SMS_PIN_MASK_POSKEY");
			}
			else {
				allowMask = PretupsI.NO;
			}

			if (msisdn != null && msisdn.length() > 0) {
				filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
				msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
				networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
				if (networkPrefixVO == null) {
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
							PretupsErrorCodesI.MOBILE_NOT_IN_SUPPORTED_NETWORK);
				}
				networkCode = networkPrefixVO.getNetworkCode();
				if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
							PretupsErrorCodesI.MOBILE_NOT_IN_SUPPORTED_NETWORK);
				}
			} else {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_IS_EMPTY);
			}

			if (msisdn != null && msisdn.length() > 15) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_LENGTH_INVALID);
			}

			PosKeyDAO posKeyDAO = new PosKeyDAO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();

			String status = TypesI.NO;
			String statusUsed = PretupsI.STATUS_NOTEQUAL;

			if (!(BTSLUtil.isNullString(msisdn))) {

				posKeyVO = posKeyDAO.loadPosKey(con, null, filteredMsisdn);
				channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMsisdn, null, statusUsed, status);

				if ((channelUserVO != null)) {
					UserDetailsVO userDetailsVO = new UserDetailsVO();
					userDetailsVO.setUserName(channelUserVO.getUserName());
					userDetailsVO.setAddress1(channelUserVO.getAddress1());
					userDetailsVO.setAddress2(channelUserVO.getAddress2());
					userDetailsVO.setCategoryName(channelUserVO.getCategoryName());
					userDetailsVO.setStatus(channelUserVO.getStatus());
					userDetailsVO.setParentName(channelUserVO.getParentName());
					userDetailsVO.setParentMobileNo(channelUserVO.getParentMsisdn());
					userDetailsVO.setParentCategoryName(channelUserVO.getParentCategoryName());
					userDetailsVO.setOwnerName(channelUserVO.getOwnerName());
					userDetailsVO.setOwnerMobileNo(channelUserVO.getOwnerMsisdn());
					userDetailsVO.setOwnerCategoryName(channelUserVO.getOwnerCategoryName());
					userDetailsVO.setSmsPin(channelUserVO.getSmsPin());
					userDetailsVO.setPinRequired(channelUserVO.getPinRequired());
					response.setUserDetailsVO(userDetailsVO);
				}
				// if msisdn not exists in both poskey and secUser table
				if ((posKeyVO == null) && (channelUserVO == null)) {
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_RECORD_FOUND);
				}

				if (posKeyVO != null && !posKeyVO.getNetworkCode().equals(userVO.getNetworkID())) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(METHOD_NAME, PretupsI.ICCID_UNSUPPORTED_NETWORK);
					}
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
							PretupsErrorCodesI.ICCID_ASSOCIATED_WITH_MSISDN_NOT_FROM_NETWORK);
				}

				else {
					if (posKeyVO == null) {
						response.setNoPosKey(PretupsI.MSISDN_NOT_ASSOCIATED);
					}
					if (channelUserVO == null) {
						response.setNoChannelUser(PretupsI.MSISDN_NOT_ASSIGNED);
					}
				}

				if ((posKeyVO == null)) {
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_MAPPING_EXISTS);
				}

				if (posKeyVO != null) {
					if (channelUserVO != null && BTSLUtil.isNullString(posKeyVO.getMsisdn())) {
						tempMsisdn = channelUserVO.getMsisdn();
					} else {
						tempMsisdn = posKeyVO.getMsisdn();
					}
				}

				String newStr = BTSLUtil.getByteSwappedKey(posKeyVO.getIccId(), userVO.getNetworkID());
				posKeyVO.setTmpIccID(newStr);

				posKeyVO.setTmpMsisdn(tempMsisdn);
				if (posKeyVO != null) {
					if(PretupsI.YES.equalsIgnoreCase(allowMask))
					{
						posKeyVO.setDecryptKeyMask(posKeyVO.getKey().substring(0, 4)+"**********"+posKeyVO.getKey().substring(((posKeyVO.getKey().length()) - 4),(posKeyVO.getKey().length()) ));
					}
					else {
						posKeyVO.setDecryptKeyMask(posKeyVO.getKey().substring(0, 8));
					}
				}

				response.setPosKeyVO(posKeyVO);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RECORD_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.RECORD_FOUND);
			}
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

	@Override
	public ICCIDMSISDNHistoryResponseVO iccidHistory(Connection con, UserVO userVO, String iccid, String msisdn,
													 ICCIDMSISDNHistoryResponseVO response) throws BTSLBaseException, Exception {

		final String METHOD_NAME = "iccidHistory";
		String msisdnPrefix;
		NetworkPrefixVO networkPrefixVO = null;
		String networkCode;
		String filteredMsisdn = "";
		String newIccid = "";
		ArrayList iccidList = null;
		ArrayList msisdnList = null;
		ArrayList msisdnHistoryList = null;
		ArrayList iccidHistoryList = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		if(BTSLUtil.isEmpty(iccid) && BTSLUtil.isEmpty(msisdn)){
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ICCID_AND_MSISDN_REQUIRED);
		}
		if (msisdn != null && msisdn.length() > 0) {
			filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
			msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
			networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			if (networkPrefixVO == null) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_SUPPORTING_NETWORK);
			}
			networkCode = networkPrefixVO.getNetworkCode();
			if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_SUPPORTING_NETWORK);
			}
		}

		boolean stkRegIccid = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID);
		if(!stkRegIccid) {
			if(! BTSLUtil.isEmpty(iccid) && iccid.length() > 15) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_ICCID_FOR_MAPPING);

			}

		}
		if(stkRegIccid){
			if(! BTSLUtil.isEmpty(iccid) && (iccid.length()<18 || iccid.length() > 20)) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_ICCID_FOR_MAPPING);

			}

		}
		if (! BTSLUtil.isEmpty(iccid) && !PretupsI.MSISDN_PATTERN.matcher(msisdn).matches())
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_INVALID, "");
		if (iccid != null && iccid.length() > 0) {
			try {
				newIccid = BTSLUtil.calcIccId(iccid, userVO.getNetworkID());
			} catch (Exception e) {
				LOG.error(METHOD_NAME, "Exception " + e);
				LOG.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
						PretupsErrorCodesI.NO_INFORMATION_FOUND_FOR_ICCID_IMSI);
			}
		}
		PosKeyDAO posKeyDAO = new PosKeyDAO();
		PosKeyWebDAO posKeywebDAO = new PosKeyWebDAO();
		PosKeyVO posKeyVO = null;
		if (!(BTSLUtil.isNullString(newIccid))) {
			posKeyVO = posKeyDAO.loadPosKey(con, newIccid, null);
			if (posKeyVO != null && !posKeyVO.getNetworkCode().equals(userVO.getNetworkID())) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(METHOD_NAME, "ICCID is from unsupported network");
				}
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
						PretupsErrorCodesI.ICC_ID_IS_NOT_FROM_YOUR_NETWORK);

			}
		}
		if (!(BTSLUtil.isNullString(newIccid)) && !(BTSLUtil.isNullString(msisdn)))// both
		// are
		// not
		// null
		{
			iccidList = this.msisdnInNetwork(
					posKeywebDAO.loadPosKeyDetailsForICCIDAndMsisdn(con, true, false, newIccid), userVO.getNetworkID());
			iccidHistoryList = this.msisdnInNetwork(
					posKeywebDAO.loadPosKeyDetailsForICCIDAndMsisdn(con, true, true, newIccid), userVO.getNetworkID());
			msisdnList = posKeywebDAO.loadPosKeyDetailsForICCIDAndMsisdn(con, false, false, filteredMsisdn);
			msisdnHistoryList = posKeywebDAO.loadPosKeyDetailsForICCIDAndMsisdn(con, false, true, filteredMsisdn);

		}
		// iccid not null and msisdn is null
		else if (!(BTSLUtil.isNullString(newIccid)) && (BTSLUtil.isNullString(msisdn))) {
			iccidList = this.msisdnInNetwork(
					posKeywebDAO.loadPosKeyDetailsForICCIDAndMsisdn(con, true, false, newIccid), userVO.getNetworkID());
			iccidHistoryList = this.msisdnInNetwork(
					posKeywebDAO.loadPosKeyDetailsForICCIDAndMsisdn(con, true, true, newIccid), userVO.getNetworkID());
			msisdnList = new ArrayList();
			msisdnHistoryList = new ArrayList();

		}
		// iccid id null and msisdn is not null
		else if ((BTSLUtil.isNullString(newIccid)) && !(BTSLUtil.isNullString(msisdn))) {
			iccidList = new ArrayList();
			iccidHistoryList = new ArrayList();
			msisdnList = posKeywebDAO.loadPosKeyDetailsForICCIDAndMsisdn(con, false, false, filteredMsisdn);
			msisdnHistoryList = posKeywebDAO.loadPosKeyDetailsForICCIDAndMsisdn(con, false, true, filteredMsisdn);

		}
		if (iccidList != null && iccidList.isEmpty() && iccidHistoryList != null && iccidHistoryList.isEmpty()
				&& msisdnList != null && msisdnList.isEmpty() && msisdnHistoryList != null
				&& msisdnHistoryList.isEmpty()) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_DATA_FOUND);
		}

		// swapping the icicid key bytes
		if (iccidList != null && !iccidList.isEmpty()) {
			for (int i = 0, j = iccidList.size(); i < j; i++) {
				posKeyVO = (PosKeyVO) iccidList.get(i);
				posKeyVO.setIccId(BTSLUtil.getByteSwappedKey(posKeyVO.getIccId(), userVO.getNetworkID()));
			}
		}
		if (iccidHistoryList != null && !iccidHistoryList.isEmpty()) {
			for (int i = 0, j = iccidHistoryList.size(); i < j; i++) {
				posKeyVO = (PosKeyVO) iccidHistoryList.get(i);
				posKeyVO.setIccId(BTSLUtil.getByteSwappedKey(posKeyVO.getIccId(), userVO.getNetworkID()));
			}

		}
		if (msisdnList != null && !msisdnList.isEmpty()) {
			for (int i = 0, j = msisdnList.size(); i < j; i++) {
				posKeyVO = (PosKeyVO) msisdnList.get(i);
				posKeyVO.setIccId(BTSLUtil.getByteSwappedKey(posKeyVO.getIccId(), userVO.getNetworkID()));
			}

		}
		if (msisdnHistoryList != null && !msisdnHistoryList.isEmpty()) {
			for (int i = 0, j = msisdnHistoryList.size(); i < j; i++) {
				posKeyVO = (PosKeyVO) msisdnHistoryList.get(i);
				posKeyVO.setIccId(BTSLUtil.getByteSwappedKey(posKeyVO.getIccId(), userVO.getNetworkID()));
			}
		}
		response.setIccidList(setHistorydetails(iccidList));
		response.setIccidHistoryList(setHistorydetails(iccidHistoryList));
		response.setMsisdnList(setHistorydetails(msisdnList));
		response.setMsisdnHistoryList(setHistorydetails(msisdnHistoryList));
		String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_LOAD_HISTORY_SUCCESSFULLY,
				new String[] { PretupsI.SUCCESS_MSG });
		response.setStatus((HttpStatus.SC_OK));
		response.setMessage(resmsg);
		response.setMessageCode(PretupsErrorCodesI.ICCID_LOAD_HISTORY_SUCCESSFULLY);
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
		}

		return response;

	}

	private List<ICCIDMSISDNHistoryDetailsVO> setHistorydetails(ArrayList list) {
		List<ICCIDMSISDNHistoryDetailsVO> iccidHistoryDetailsList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			PosKeyVO posKeyVO = (PosKeyVO) list.get(i);
			ICCIDMSISDNHistoryDetailsVO historyDetailsVO = new ICCIDMSISDNHistoryDetailsVO();
			historyDetailsVO.setIccid(posKeyVO.getIccId());
			historyDetailsVO.setNewIccID(posKeyVO.getNewIccId());
			historyDetailsVO.setLastTransactionID(posKeyVO.getLastTransaction());
			historyDetailsVO.setMobileNumber(posKeyVO.getMsisdn());
			historyDetailsVO.setModifiedBy(posKeyVO.getModifiedBy());
			historyDetailsVO.setModifiedOn(posKeyVO.getModifedOnStr());
			iccidHistoryDetailsList.add(historyDetailsVO);
		}
		return iccidHistoryDetailsList;
	}

	private ArrayList msisdnInNetwork(ArrayList listOfPosKeyVO, String networkID) throws BTSLBaseException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("msisdnInNetwork", "Entered : listOfPosKeyVO=" + listOfPosKeyVO + ",networkID=" + networkID);
		}
		final String METHOD_NAME = "msisdnInNetwork";
		PosKeyVO posKeyVO = null;
		ArrayList newList = new ArrayList();
		String filteredMsisdn, msisdnPrefix, networkCode;
		NetworkPrefixVO networkPrefixVO;
		int listOfPosKeyVOs = listOfPosKeyVO.size();
		for (int i = 0; i < listOfPosKeyVOs; i++) {
			posKeyVO = (PosKeyVO) listOfPosKeyVO.get(i);
			try {
				if (!(BTSLUtil.isNullString(posKeyVO.getMsisdn()))) {
					if (posKeyVO.getMsisdn() != null && posKeyVO.getMsisdn().length() > 0) {
						filteredMsisdn = PretupsBL.getFilteredMSISDN(posKeyVO.getMsisdn());
						msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
						networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
						if (networkPrefixVO == null) {
							continue;
						}
						networkCode = networkPrefixVO.getNetworkCode();
						if (networkCode == null || !networkCode.equals(networkID)) {
							continue;
						}
						newList.add(posKeyVO);
					}
				}
			} catch (Exception e) {
				LOG.error(METHOD_NAME, "Exceptin:e=" + e + ",for Msisdn=" + posKeyVO.getMsisdn());
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,
						PretupsErrorCodesI.MSISDN_IS_FROM_UNSUPPORTED_NETWORK);
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Exit : newList=" + newList);
		}

		return newList;
	}





	@Override
	public DeleteICCIDBulkResponseVO iccidDeleteBulk(Connection con, MComConnectionI mcomCon, UserVO userVO, DeleteICCIDBulkRequestVO requestVO, DeleteICCIDBulkResponseVO response) throws BTSLBaseException, IOException, ParseException, SQLException {
		final String METHOD_NAME = "iccidDeleteBulk";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		if (requestVO.getFileAttachment() == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT, "");
		}

		if (requestVO.getFileName() == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME, "");
		}
		if (requestVO.getFileType() == null) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_TYPE, "");
		}

		if (requestVO.getFileAttachment().isEmpty()) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT, "");
		}

		if (requestVO.getFileName().isEmpty()) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_NAME, "");
		}
		if (requestVO.getFileType().isEmpty()) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.EMPTY_FILE_TYPE, "");
		}
		boolean isFileUploaded = false;
		BufferedReader br = null;
		String line = null;
		InputStream is = null;
		InputStreamReader inputStreamReader = null;
		String delim = Constants.getProperty(PretupsI.DELIMITER_FOR_UPLOAD_ICCID);
		if (BTSLUtil.isNullString(delim))
			delim = PretupsI.COMMA;

		File requestToFile = null;

		if (!BTSLUtil.isNullString(requestVO.getFileAttachment()) && !BTSLUtil.isNullString(requestVO.getFileName())
				&& !BTSLUtil.isNullString(requestVO.getFileType())) {
			boolean message = BTSLUtil.isValideFileName(requestVO.getFileName());

			if (!message) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_NAME1, 0, null);
			}
		} else {
			LOG.error(METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_INPUT);
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_INPUT,
					PretupsI.RESPONSE_FAIL, null);

		}

		if (!((requestVO.getFileType()).equalsIgnoreCase(PretupsI.TEXT_OR_PLAIN))) {
			throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPE, "");
		}

		ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
		ICCIDDeleteDAO iccidDeleteDao = new ICCIDDeleteDAO();


		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		try {
			// check for valid name of the file to be uploaded

			String fileName = requestVO.getFileName();// accessing
			// name
			// of
			// the
			// file
			boolean message = BTSLUtil.isValideFileName(fileName);// validating
			// name of
			// the
			// file
			// if not a valid file name then throw exception

			// if not a valid file name then throw exception
			if (!message) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_NAME_UPLOAD_FAIL);
			}

			//
			//String dir = Constants.getProperty(PretupsI.UPLOADPOSKEYSFILEPATH);
			//String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
			// Cross site Scripting removal: akanksha
			byte[] data = fileUtil.decodeFile(requestVO.getFileAttachment());// ak
			//


//			file1 = iccidDeleteForm.getFile();
//			// Cross site Scripting removal
//			byte[] data = file1.getFileData();// ak
			is = new ByteArrayInputStream(data);
			inputStreamReader = new InputStreamReader(is);
			br = new BufferedReader(inputStreamReader);


			while ((line = br.readLine()) != null) {
				boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
				if (!isFileContentValid) {
					//throw new BTSLBaseException(this, "confirmDeleteICCID", "routing.delete.msg.invalidfilecontent", "uploadiccidfilepage");
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT_ICCID);
				}
			}// Cross site scripting Removal
			// this block checks before uploading the file if it is a txt
			// file or not
			if (!(requestVO.getFileName()).endsWith(".txt")) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NOT_A_TXT_FILE);
			}
			// this block forward the requeste to appropriate page based on
			// input nature

			/////////////////////////////////////////////

			String dir = Constants.getProperty("UploadDeleteICCIDFilePath");
			String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
			String fileSize = Constants.getProperty("UploadDeleteICCIDFileSize");
			if (BTSLUtil.isNullString(fileSize)) {
				fileSize = String.valueOf(0);
			}


			//
			requestToFile = new File(requestVO.getFileName());
			FileUtils.writeByteArrayToFile(requestToFile, data);

			// upload file to server
			isFileUploaded = BTSLUtil.uploadFileToServer(requestToFile, data, dir, contentType,
					Long.parseLong(fileSize));
			//





			if (isFileUploaded) {
				// now process uploaded file
				HashMap allICCIDMap = this.processFileDeleteIccidBulk(con, userVO, fileName, response);
				ArrayList userList = (ArrayList) allICCIDMap.get(PretupsI.ICCID_USER_ASSOCIATED);
				ArrayList msisdnList = (ArrayList) allICCIDMap.get(PretupsI.ICCID_MSISDN_ASSOCIATED);
				ArrayList invalidList = (ArrayList) allICCIDMap.get(PretupsI.ICCID_NOT_EXISTING);
				if (invalidList != null && !invalidList.isEmpty()) {
					File file = new File(dir + requestVO.getFileName());
					boolean isDeleted = file.delete();
					if(isDeleted){
						LOG.debug(METHOD_NAME, "File deleted successfully");
					}


					response.setStatus((HttpStatus.SC_BAD_REQUEST));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_DELETE_FAIL, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.ICCID_DELETE_FAIL);
					response.setErrorFlag(PretupsI.TRUE);


				} else {
					String currentFileName = requestVO.getFileName();
					String renamedFileName = currentFileName.substring(0, currentFileName.length() - 4) + BTSLUtil.currentDateTimeFormatString();
					currentFileName = dir + currentFileName;
					renamedFileName = dir + renamedFileName + Constants.getProperty("IccidDeleteErrorFileExtension");
					if (new File(currentFileName).renameTo(new File(renamedFileName))) {
						LOG.debug(METHOD_NAME, "File renamed successfully");
					}
					ArrayList deletedICCIDList = (ArrayList) allICCIDMap.get(PretupsI.ICCID_DELETEABLE);
					//As discussed with Shishupal Sir
					if (userList != null && !userList.isEmpty()) {
						for (int i = 0; i < userList.size(); i++) {
							deletedICCIDList.add(userList.get(i));
						}

					}
					if (msisdnList != null && !msisdnList.isEmpty()) {
						for (int i = 0; i < msisdnList.size(); i++) {
							deletedICCIDList.add(msisdnList.get(i));
						}

					}

					iccidDeleteDao.deleteICCID(con, deletedICCIDList);
					mcomCon.finalCommit();


					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_DELETE_SUCCESS, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.ICCID_DELETE_SUCCESS);


				}
			} else {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ICCID_DELETE_ACTION_UNSUCCESS);
			}

		}
		finally{
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting");
			}
		}


		return response;
	}







	public HashMap processFileDeleteIccidBulk(Connection con, UserVO userVO, String fileName,
											  DeleteICCIDBulkResponseVO response) throws BTSLBaseException {
		final String METHOD_NAME = "processFileDeleteIccidBulk";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered");
		}

		FileReader fileReader = null;
		BufferedReader bufferReader = null;

		HashMap allICCIDMap = null;// create HashMap object
		PrintWriter fileWriter = null;

		ArrayList errorListFinal = new ArrayList();
		try {
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);


			String dir = Constants.getProperty("UploadDeleteICCIDFilePath"); // For
			// upload
			// directory
			ArrayList deleteableICCID = new ArrayList();// For ICCIDs not
			// associated to any
			// MSISDN
			ArrayList userAssociatedICCID = new ArrayList();// For ICCIDs
			// associated to any
			// CHNL User
			ArrayList msisdnAssociatedICCID = new ArrayList();// For ICCIDs
			// associated to
			// any MSISDN
			ArrayList invalidICCID = new ArrayList();// For ICCIDs that does not
			// exist
			fileReader = new FileReader(dir + fileName);
			bufferReader = new BufferedReader(fileReader);
			StringBuffer tempStr = new StringBuffer();
			String line = null;
			StringTokenizer startparser = null;
			String fileDelimit = Constants.getProperty("DelimiterforuploadICCIDDeleteFile");
			allICCIDMap = new HashMap();// create HashMap object
			do {
				line = bufferReader.readLine();
				if (line != null) {
					startparser = new StringTokenizer(line, fileDelimit); // separate
				}
				// each
				// string
				// in
				// a
				// line
				while (startparser.hasMoreTokens()) {
					tempStr.append(startparser.nextToken().trim());
					tempStr.append(fileDelimit);
				}
			} while (line != null); // end of do while
			try {
				if (bufferReader != null) {
					bufferReader.close();
				}
			} catch (Exception e1) {
				LOG.errorTrace(METHOD_NAME, e1);
			}
			try {
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (Exception e1) {
				LOG.errorTrace(METHOD_NAME, e1);
			}
			String iccidArray[] = tempStr.toString().split(fileDelimit);
			ArrayList iccidList = new ArrayList();
			String networkId = userVO.getNetworkID();
			int length;
			for (int i = 0, j = iccidArray.length; i < j; i++) {
				length = iccidArray[i].length();
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID))).booleanValue()) {
					if (length > 20) {
						throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ICCID_ISMIS);
					}
				} else {
					if (length > 15) {
						throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ICCID_ISMIS);
					}
				}
				try{
					iccidList.add(BTSLUtil.calcIccId(iccidArray[i],networkId));
				}catch(Exception e){
					LOG.errorTrace(METHOD_NAME, e);
					LOG.error(" processFile()"," Invalid ICCID: " + iccidArray[i]);
					throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_ICCID_ISMIS);
				}

			}
			ICCIDDeleteDAO iccidDeleteDao = new ICCIDDeleteDAO();
			ArrayList errorList = iccidDeleteDao.validateICCIDList(con, iccidList);
			ListValueVO listValueVO = null;
			for (int i = 0; i < errorList.size(); i++) {
				listValueVO = (ListValueVO) errorList.get(i);
				// add all deleteable iccids into arraylist if exists
				if (listValueVO.getLabel().equals(PretupsI.ICCID_DELETEABLE)) {
					deleteableICCID.add(listValueVO.getValue());
				} else if (listValueVO.getLabel().equals(PretupsI.ICCID_USER_ASSOCIATED)) {
					userAssociatedICCID.add(listValueVO.getValue());
				} else if (listValueVO.getLabel().equals(PretupsI.ICCID_MSISDN_ASSOCIATED)) {
					msisdnAssociatedICCID.add(listValueVO.getValue());
				} else if (listValueVO.getLabel().equals(PretupsI.ICCID_NOT_EXISTING)) {
					invalidICCID.add(listValueVO.getValue());
				}
			}// end of for loop
			allICCIDMap.put(PretupsI.ICCID_DELETEABLE, deleteableICCID); // put
			// the
			// arraylist
			// of
			// deleteable
			// iccids
			// in
			// HashMap
			allICCIDMap.put(PretupsI.ICCID_USER_ASSOCIATED, userAssociatedICCID); // put
			// the
			// arraylist
			// of
			// user
			// associated
			// iccids
			// in
			// HashMap
			allICCIDMap.put(PretupsI.ICCID_MSISDN_ASSOCIATED, msisdnAssociatedICCID); // put
			// the
			// arraylist
			// of
			// msisdn
			// associated
			// iccids
			// in
			// HashMap
			allICCIDMap.put(PretupsI.ICCID_NOT_EXISTING, invalidICCID); // put
			// the
			// arraylist
			// of
			// invalid
			// iccids
			// in
			// HashMap
//			if ((userAssociatedICCID != null && !userAssociatedICCID.isEmpty()) || (msisdnAssociatedICCID != null && !msisdnAssociatedICCID.isEmpty()) || (invalidICCID != null && !invalidICCID.isEmpty())) {
			if (invalidICCID != null && !invalidICCID.isEmpty()) {

				int j = invalidICCID.size();
				for (int i = 0; i < j; i++) {

					listValueVO = new ListValueVO();
					listValueVO.setValue(BTSLUtil.getByteSwappedKey((String) invalidICCID.get(i), userVO.getNetworkID()));
					String temp = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_NOT_EXIST, null);

					listValueVO.setOtherInfo(temp);
					errorListFinal.add(listValueVO);

				}

				response.setErrorListFinal(errorListFinal);

				downloadErrorLogFileDeleteIccidBulk(userVO, response);

			}
		}// end of try
		catch (Exception e) {
			LOG.error("processFile", "Exception " + e.getMessage());
			LOG.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(this, "processFile", e.getMessage(), "uploadiccidfile");
		}// end of catch
		finally {

			try {
				if (bufferReader != null) {
					bufferReader.close();
				}
			} catch (Exception e1) {
				LOG.errorTrace(METHOD_NAME, e1);
			}
			try {
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (Exception e1) {
				LOG.errorTrace(METHOD_NAME, e1);
			}
			if (LOG.isDebugEnabled()) {
				LOG.error(METHOD_NAME, " Exiting ");
			}
		}// end of finally
		return allICCIDMap;
	}


	public void downloadErrorLogFileDeleteIccidBulk(UserVO userVO, DeleteICCIDBulkResponseVO response) throws Exception {
		final String METHOD_NAME = "downloadErrorLogFile";
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Entered");
		try {
//			ArrayList errorList = response.getErrorList();
			ArrayList errorList = response.getErrorListFinal();

			String filePath = Constants.getProperty(PretupsI.UPLOAD_DELETE_ICCID_FILE_PATH);
			try {
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory())
					fileDir.mkdirs();
			} catch (Exception e) {
				LOG.errorTrace(METHOD_NAME, e);
				LOG.error(METHOD_NAME, "Exception" + e.getMessage());
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DIR_NOT_CREATED, 0, null);
			}
			String fileName = PretupsI.ERROR_LOG_FILE_NAME

					+ BTSLUtil.getFileNameStringFromDate(new Date(System.currentTimeMillis())) + ".csv";

			this.writeErrorLogFileDeleteIccidBulk(errorList, fileName, filePath, userVO.getNetworkID(), fileName, true);

			File error = new File(filePath + fileName);
			byte[] fileContent = FileUtils.readFileToByteArray(error);
			String encodedString = Base64.getEncoder().encodeToString(fileContent);
			response.setFileAttachment(encodedString);
			response.setFileName(fileName);
			response.setFileType(PretupsI.CSV_EXT);
		} catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			throw e;
		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug(METHOD_NAME, "Exiting:" + METHOD_NAME);
		}
	}

	public void writeErrorLogFileDeleteIccidBulk(ArrayList errorList, String _fileName, String filePath, String _networkCode,
												 String uploadedFileNamePath, Boolean headval) throws Exception

	{
		final String methodName = "writeListErrorLogFile";
		String[] splitFileName = uploadedFileNamePath.split("/");
		String uploadedFileName = splitFileName[(splitFileName.length) - 1];
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered: " + methodName);
		}
		Writer out = null;
		File newFile = null;
		File newFile1 = null;
		String fileHeader = null;
		String fileName = null;
		try {
			Date date = new Date(System.currentTimeMillis());
			newFile1 = new File(filePath);
			if (!newFile1.isDirectory())
				newFile1.mkdirs();
			fileName = filePath + _fileName;
			LOG.debug(methodName, "fileName := " + fileName);
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			if (headval) {
				fileHeader = Constants.getProperty(PretupsI.ICCID_ERROR_FILE_HEADER);
				fileHeader = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.ICCID_ERROR_FILE_HEADER, null);

			} else {
				fileHeader = Constants.getProperty(PretupsI.ERROR_FILE_HEADER_PAYOUT);
				fileHeader = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.ERROR_FILE_HEADER_PAYOUT, null);

			}
			newFile = new File(fileName);
			out = new OutputStreamWriter(new FileOutputStream(newFile));
			out.write(fileHeader + PretupsI.NEW_LINE_CHARACTER);
//			List<ListValueVO> filterList = (List<ListValueVO>) errorList.stream()
//					.sorted(Comparator.comparing(ListValueVO::getLabel)).collect(Collectors.toList());

			int sNo=0;
			for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {

				ListValueVO listValueVO = iterator.next();
				//out.write(listValueVO.getLabel().concat(PretupsI.COMMA));
				++sNo;
				out.write(String.valueOf(sNo).concat(PretupsI.COMMA));
				out.write(listValueVO.getValue().concat(PretupsI.COMMA));
				out.write(listValueVO.getOtherInfo().concat(PretupsI.COMMA));
				//out.write(listValueVO.getLabelWithValue().concat(PretupsI.COMMA));

				out.write(PretupsI.COMMA);
				out.write(PretupsI.NEW_LINE_CHARACTER);
			}
			out.write(PretupsI.END);

		} catch (Exception e) {
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"writeDataInFile[writeDataInFile]", "", "", "", "Exception:= " + e.getMessage());
			throw e;
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting... ");
			}
			if (out != null)
				try {
					out.close();
				} catch (Exception e) {
					throw e;
				}

		}
	}
	@Override
	public IccidImsiMsisdnListResponseVO iccidImsiMsisdnListFilterByDate(Connection con, UserVO userVO, IccidImsiMsisdnListResponseVO response, String dateRange) throws ParseException, Exception {
		final String METHOD_NAME = "iccidImsiMsisdnListFilterByDate";

		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}


		NetworkPrefixVO networkPrefixVO = null;
		String networkCode;
		String noSecUser = null;

		ChannelUserVO channelUserVO = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		String allowMask = null;
		String tempMsisdn = null;
		final String dateValidation= (String)PreferenceCache.getSystemPreferenceValue(PretupsI.SYSTEM_DATE_FORMAT_VALIDATE);
		try {
			String dateArr[] = dateRange.split(PretupsI.HYPHEN);
			if (dateArr.length < 2 || !dateArr[0].matches(dateValidation) || !dateArr[1].matches(dateValidation)) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_DATE_RANGE);
			}
			final String format= (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
			java.util.Date fromDate = BTSLUtil.getDateFromDateString(dateArr[0], format);
			java.util.Date toDate = BTSLUtil.getDateFromDateString(dateArr[1], format);
			try {
				LocalDate.parse(dateArr[0], DateTimeFormatter.ofPattern(format));
			} catch (Exception ex) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLEFROM_DATE, new String[]{PretupsI.INVALID});
			}
			try {
				LocalDate.parse(dateArr[1], DateTimeFormatter.ofPattern(format));
			} catch (Exception ex) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLEFROM_DATE, new String[]{PretupsI.INVALID});
			}

			if (!BTSLUtil.isNullString(Constants.getProperty("ALLOW_SMS_PIN_MASK_POSKEY"))) {
				allowMask = Constants.getProperty("ALLOW_SMS_PIN_MASK_POSKEY");
			} else {
				allowMask = PretupsI.NO;
			}


			PosKeyDAO posKeyDAO = new PosKeyDAO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();

			String status = TypesI.NO;
			String statusUsed = PretupsI.STATUS_NOTEQUAL;


			List<PosKeyVO> posKeyVOList = posKeyDAO.loadPosKey(con);

			if (posKeyVOList == null || posKeyVOList.isEmpty()) {
				throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.RECORD_NOT_FOUND);
			}
			ArrayList<PosKeyVO> finalList = new ArrayList<>();
			ArrayList<PosKeyVOList> posKeyVOLists= new ArrayList<>();
			for(PosKeyVO vo: posKeyVOList){
				long createdFromTime=	BTSLUtil.getDateFromDateString(vo.get_createdOnStr(), format).getTime();
				long fromDateTime= fromDate.getTime();
				long createdToTime = BTSLUtil.getDateFromDateString(vo.get_createdOnStr(), format).getTime();
				long toDateTime = toDate.getTime();
				if(createdFromTime>=fromDateTime && createdToTime <= toDateTime){
					finalList.add(vo);
				}
			}
			for (PosKeyVO vo : finalList) {
				PosKeyVOList finalPoskey= new PosKeyVOList();
				String newStr = BTSLUtil.getByteSwappedKey(vo.getIccId(), userVO.getNetworkID());
				vo.setTmpIccID(newStr);
				if (vo != null) {
					if (PretupsI.YES.equalsIgnoreCase(allowMask)) {
						vo.setDecryptKeyMask(vo.getKey().substring(0, 4) + "**********" + vo.getKey().substring(((vo.getKey().length()) - 4), (vo.getKey().length())));
					} else {
						vo.setDecryptKeyMask(vo.getKey().substring(0, 8));
					}
				}
				if ((BTSLUtil.isNullString(vo.getMsisdn()))) {
					finalPoskey.setNoChannelUser(PretupsI.ICCID_NOT_ASSOCIATED);
				} else {
					String filteredMsisdn = PretupsBL.getFilteredMSISDN(vo.getMsisdn());
					String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
					networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
				}

					channelUserVO = channelUserDAO.loadUsersDetails(con, vo.getMsisdn(), null, statusUsed,
							status);
					if ((channelUserVO != null)) {
						UserDetailsVO userDetailsVO = new UserDetailsVO();
						userDetailsVO.setUserName(channelUserVO.getUserName());
						userDetailsVO.setAddress1(channelUserVO.getAddress1());
						userDetailsVO.setAddress2(channelUserVO.getAddress2());
						userDetailsVO.setCategoryName(channelUserVO.getCategoryName());
						userDetailsVO.setStatus(channelUserVO.getStatus());
						userDetailsVO.setParentName(channelUserVO.getParentName());
						userDetailsVO.setParentMobileNo(channelUserVO.getParentMsisdn());
						userDetailsVO.setParentCategoryName(channelUserVO.getParentCategoryName());
						userDetailsVO.setOwnerName(channelUserVO.getOwnerName());
						userDetailsVO.setOwnerMobileNo(channelUserVO.getOwnerMsisdn());
						userDetailsVO.setOwnerCategoryName(channelUserVO.getOwnerCategoryName());
						userDetailsVO.setSmsPin(channelUserVO.getSmsPin());
						userDetailsVO.setPinRequired(channelUserVO.getPinRequired());
						userDetailsVO.setLoginId(channelUserVO.getLoginID());
						finalPoskey.setUserDetailsVO(userDetailsVO);
					}
					if (channelUserVO == null) {
						finalPoskey.setNoChannelUser(PretupsI.MSISDN_NOT_ASSIGNED);
					}

				finalPoskey.setPosKeyVO(vo);
				posKeyVOLists.add(finalPoskey);
			}

			response.setPosKeyVOList(posKeyVOLists);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ICCID_ENQUIRY_BASED_ON_DATE_SUCCESSFULLY, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.ICCID_ENQUIRY_BASED_ON_DATE_SUCCESSFULLY);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;

	}

}
