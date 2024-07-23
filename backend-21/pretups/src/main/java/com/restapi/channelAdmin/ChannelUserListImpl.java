package com.restapi.channelAdmin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
/*//import org.apache.struts.action.ActionForward;
import org.apache.struts.upload.FormFile;*/
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserDeletionBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.logging.UnregisterChUsersFileProcessLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.StringTokenizer;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.requestVO.BulkSusResCURequestVO;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

@Service("ChannelUserListI")
public class ChannelUserListImpl implements ChannelUserListI {

	public static final Log LOG = LogFactory.getLog(ChannelUserListImpl.class.getName());
	public static final String classname = "ChannelUserListImpl";

	@Override
	public ChannelUserListResponseVO getChannelUserListByAdmin(Connection con, String loginID,
			HttpServletResponse responseSwag, ChannelUserListRequestVO requestVO, String searchType)
			throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "ChannelUserListImpl";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ChannelUserListResponseVO response = new ChannelUserListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO channelUserVO = null;
		DomainDAO domainDao = new DomainDAO();
		GeographicalDomainDAO geoDomainDao = new GeographicalDomainDAO();
		UserVO loggedinUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

		try {

			final String status = ((String) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.ADMINISTRBLY_USER_STATUS_CHANG_NEW));
			Set<String> set = new HashSet<String>();
			String statusList[] = status.split(",");
			for (String statusObj : statusList) {
				String statusObjSplit = statusObj.split(":")[0];
				set.add(statusObjSplit);
			}

			if (searchType.equalsIgnoreCase("Msisdn")) {

				if (!BTSLUtil.isEmpty(requestVO.getMsisdn())) {

					final String msisdn = requestVO.getMsisdn();
					final String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
					final String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

					final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache
							.getObject(msisdnPrefix);
					if (networkPrefixVO == null) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND,
								0, null);
					}
					final String networkCode = networkPrefixVO.getNetworkCode();

					if (networkCode == null || !networkCode.equals(loggedinUserVO.getNetworkID())) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND,
								0, null);
					}

					channelUserVO = userDAO.loadUserDetailsByMsisdn(con, requestVO.getMsisdn());

					if (channelUserVO != null && channelUserVO.getUserCode() == null) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_IDENTIFIER_VALUE,
								0, null);
					}
					if (channelUserVO == null || !channelUserVO.getUserCode().equals(filteredMsisdn)) {
						throw new BTSLBaseException(PretupsErrorCodesI.MSISDN_INVALID);
					}
					ArrayList<ListValueVO> domainList = domainDao.loadDomainListByUserId(con,
							loggedinUserVO.getUserID());
					if (domainList != null && !domainList.isEmpty()) {
						ListValueVO listValueVO = null;
						boolean domainfound = false;

						for (int i = 0, j = domainList.size(); i < j; i++) {
							listValueVO = (ListValueVO) domainList.get(i);
							if (channelUserVO.getCategoryVO().getDomainCodeforCategory()
									.equals(listValueVO.getValue())) {
								domainfound = true;
								break;
							}
						}
						if (!domainfound) {
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_DOMAIN,
									0, null);
						}
					}

					final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
					if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(),
							loggedinUserVO.getUserID())) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN,
								0, null);
					}

				}

				response = userDAO.getChannelUsersList1(con, "ALL", "ALL", "ALL", channelUserVO.getUserID(), "ALL",
						true);
				ArrayList<GetChannelUsersMsg> channelUsersList1 = new ArrayList<GetChannelUsersMsg>();
				for (GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList()) {
					if (!BTSLUtil.isNullString(getChannelUsersMsg.getMsisdn())) {

						if (getChannelUsersMsg.getMsisdn().equals(requestVO.getMsisdn())) {

							if (set.contains(getChannelUsersMsg.getStatusCode())) {
								getChannelUsersMsg.setCanChangeStatus(true);
							} else {
								getChannelUsersMsg.setCanChangeStatus(false);
							}

							channelUsersList1.add(getChannelUsersMsg);

						}
					}

				}

				response.setChannelUsersList(channelUsersList1);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);

			}

			else if (searchType.equalsIgnoreCase("LoginId")) {

				if (!BTSLUtil.isEmpty(requestVO.getLoginID())) {

					channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, requestVO.getLoginID());

					if (channelUserVO == null) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_LOGIN_ID, 0,
								null);
					}

					ArrayList<ListValueVO> domainList = domainDao.loadDomainListByUserId(con,
							loggedinUserVO.getUserID());
					if (domainList != null && !domainList.isEmpty()) {
						ListValueVO listValueVO = null;
						boolean domainfound = false;

						for (int i = 0, j = domainList.size(); i < j; i++) {
							listValueVO = (ListValueVO) domainList.get(i);
							if (channelUserVO.getCategoryVO().getDomainCodeforCategory()
									.equals(listValueVO.getValue())) {
								domainfound = true;
								break;
							}
						}
						if (!domainfound) {
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_DOMAIN,
									0, null);

						}
					}

					boolean geoFound = false;
					List<UserGeographiesVO> userGeoDomains = channelUserVO.getGeographicalAreaList();
					if (userGeoDomains.size() == 0)
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN,
								0, null);
					for (UserGeographiesVO userGeoDomain : userGeoDomains) {
						if (geoDomainDao.isGeoDomainExistInHierarchy(con, userGeoDomain.getGraphDomainCode(),
								loggedinUserVO.getUserID())) {
							geoFound = true;
							break;
						}
					}

					if (!geoFound) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN,
								0, null);
					}

				}

				response = userDAO.getChannelUsersList1(con, "ALL", "ALL", "ALL", channelUserVO.getUserID(), "ALL",
						true);
				ArrayList<GetChannelUsersMsg> channelUsersList1 = new ArrayList<GetChannelUsersMsg>();
				for (GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList()) {
					if (!BTSLUtil.isNullString(getChannelUsersMsg.getLoginID())) {

						if (getChannelUsersMsg.getLoginID().equals(requestVO.getLoginID())) {

							if (set.contains(getChannelUsersMsg.getStatusCode())) {
								getChannelUsersMsg.setCanChangeStatus(true);
							} else {
								getChannelUsersMsg.setCanChangeStatus(false);
							}

							channelUsersList1.add(getChannelUsersMsg);

						}
					}

				}

				response.setChannelUsersList(channelUsersList1);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);

			}

			else if (searchType.equalsIgnoreCase("Advance")) {

				response = userDAO.getChannelUsersList1(con, requestVO.getDomain(), requestVO.getUserCategory(), "ALL",
						requestVO.getParentUserID(), requestVO.getStatus(), true);

				ArrayList<GetChannelUsersMsg> channelUsersList1 = new ArrayList<GetChannelUsersMsg>();
				for (GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList()) {
					if (set.contains(getChannelUsersMsg.getStatusCode())) {
						getChannelUsersMsg.setCanChangeStatus(true);
					} else {
						getChannelUsersMsg.setCanChangeStatus(false);
					}

					channelUsersList1.add(getChannelUsersMsg);

				}

				if (!BTSLUtil.isNullOrEmptyList(channelUsersList1)) {
					response.setChannelUsersList(channelUsersList1);
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.SUCCESS);

				}

				else {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_CHNL_USER_FOUND, 0, null);
				}

			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
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
					PretupsErrorCodesI.NO_CHNL_USER_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.NO_CHNL_USER_FOUND);
		}

		return response;
	}
	
	
	@Override
	public ChannelUserListByParentResponseVO getChannelUserListByParent(Connection con, String loginID,
			HttpServletResponse responseSwag, ChannelUserListByParntReqVO requestVO)
			throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "ChannelUserListImpl";
		ChannelUserListByParentResponseVO response = new ChannelUserListByParentResponseVO();
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		UserDAO userDAO = new UserDAO();
		try {
		
			response = userDAO.getChannelUsersListbyParent(con, requestVO);
			if(response.getChannelUsersList()!=null && response.getChannelUsersList().size()==0)  {
				 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND, 0, null);
			}
		
		}catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
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
					PretupsErrorCodesI.NO_CHNL_USER_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.NO_CHNL_USER_FOUND);
		}

		return response;
	}
	
	
	
	/**
     * This method is called just after the first jsp and is used to upload the
     * entered file on the server
     * and call the method for perocessing the file.
     * Method uploadAndProcessFile.
     * 
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ActionForward
     */
	@Override
	public BulkSusResCUResponseVO processBulkSusResCU(Connection con, HttpServletResponse response1,
			BulkSusResCURequestVO requestVO, String type, Locale locale, UserVO userVO) {
		final String METHOD_NAME = "processBulkSusResCU";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        //ActionForward forward = null;
//        final UnregisterChUsersInBulkForm unregisterChUserForm = (UnregisterChUsersInBulkForm) form;
        //FormFile file = null;
        boolean isFileUploaded = false;
        BulkSusResCUResponseVO response = new BulkSusResCUResponseVO();
        BufferedReader br = null;
        String line = null;
        InputStream is = null;
        InputStreamReader inputStreamReader = null;
        HashMap<String, String> fileDetailsMap = null;
        ReadGenericFileUtil fileUtil = null;
        try {
//            file = unregisterChUserForm.getFileName();
            final String dir = Constants.getProperty("UploadFileForUnRegChnlUserPath");
            final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
            String fileSize = Constants.getProperty("OTHER_FILE_SIZE");
            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = String.valueOf(0);
            }
            
            final String fileName = requestVO.getFileName();// accessing
            // name
            // of
            // the
            // file
            final boolean message = BTSLUtil.isValideFileName(fileName);// validating
            // name of the
            // file
            // if not a valid file name then throw exception
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
			
            final byte[] data = fileUtil.decodeFile(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT));
            is = new ByteArrayInputStream(data);
            inputStreamReader = new InputStreamReader(is);
            br = new BufferedReader(inputStreamReader);
            while ((line = br.readLine()) != null) {
                final boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
                if (!isFileContentValid) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT, 0,
							null);
				}
            }
            
            // Cross site scripting Removal
//            unregisterChUserForm.setFileNameStr(requestVO.getFileName()+"."+requestVO.getFileType());
            // upload file to server
            if (requestVO.getFileType().equals("txt")) {
				requestVO.setFileType(BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT));
				isFileUploaded = BTSLUtil.uploadFileToServerWithHashMap(fileDetailsMap, dir, contentType, "uploadFileForChUserUnregisterBulk", Long.parseLong(fileSize),data, requestVO.getFileType());
            }
            // if file uploaded successfully to the server then process that
            // file for deleting or suspending the channel user as per the
            // request for.
            if (isFileUploaded) {
            	response = this.processUploadedFileForUnReg(con,requestVO, userVO,type,locale);
            } else {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_NOT_UPLOADED, 0, null);
                }
        } catch (BTSLBaseException be) {
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
					PretupsErrorCodesI.CHANGE_STATUS_NOT_PERFORMED, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CHANGE_STATUS_NOT_PERFORMED);
		}
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:return=" + METHOD_NAME);
            }
        }
        return response;
	}
	
	 

	    /**
	     * This method is used to process the MSISDN's file or Login ID file for
	     * unregistration of subscribers.
	     * Method processUploadedFileForUnReg.
	     * @param locale 
	     * 
	     * @param mapping
	     *            ActionMapping
	     * @param form
	     *            ActionForm
	     * @param request
	     *            HttpServletRequest
	     * @param response
	     *            HttpServletResponse
	     * @return ActionForward
	     */
	    private BulkSusResCUResponseVO processUploadedFileForUnReg(Connection con,BulkSusResCURequestVO requestVO,UserVO userVO, String type, Locale locale) throws BTSLBaseException {
	        final String METHOD_NAME = "processUploadedFileForUnReg";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("processUploadedFileForUnReg", "Entered");
	        }
	    
//	        final UnregisterChUsersInBulkForm unregisterChUserForm = (UnregisterChUsersInBulkForm) form;
	        final String delimiter = Constants.getProperty("DelimiterForUploadedFileForUnRegChnlUser");
	        final String filePath = Constants.getProperty("UploadFileForUnRegChnlUserPath");
	        final String contentsSize = Constants.getProperty("NO_OF_CONTENTS");
	        String fileName=requestVO.getFileName();
	        final String filePathAndFileName = filePath+fileName; // path if the
	                                                                // file
	        // with file name
	        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	        final UserDAO userDAO = new UserDAO();
	        FileReader fileReader = null; // file reader
	        BufferedReader bufferReader = null;
//	        Connection con = null;
//	        MComConnectionI mcomCon = null;
	        BulkSusResCUResponseVO response = new BulkSusResCUResponseVO();
	        File file = null;
	        final ArrayList validList = new ArrayList();
	        final StringBuffer invalidString = new StringBuffer();
	        String invalidStr;
	        int countStr = 0;
	        int forDisplayMsg = 0;
	        boolean invalidStringFromDao = false;
	        String tempStr = null;
	        String filteredMsisdn = null;
	        String msisdnPrefix;
	        NetworkPrefixVO networkPrefixVO = null;
	        String networkCode;
	        BTSLMessages btslMessage = null;
	        String msisdnOrLoginID;

	        final HashMap prepareStatementMap = new HashMap();
	        ChannelUserWebDAO channelUserWebDAO = null;
	        boolean fileMoved = false;

	        final ArrayList MobileOrId = new ArrayList(); // list to store the
	                                                      // contents of
	        // the file
	        try {
	            channelUserWebDAO = new ChannelUserWebDAO();
	            // check the DELIMITER defined in the Constant Property file for
	            // Blank
	            if (BTSLUtil.isNullString(delimiter)) {
	                if (LOG.isDebugEnabled()) {
	                    LOG.debug(METHOD_NAME, "Delimiter not defined in Constant Property file");
	                }
	                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DELIMITER_MISSING, 0, null);
	                } else {
	                // check the FILEPATH defined in the Constant Property file for
	                // Blank
	                if (BTSLUtil.isNullString(filePath)) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(METHOD_NAME, "File path not defined in Constant Property file");
	                    }
	                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_PATH_MISSING, 0, null);
	                     } else {
	                    // check the NO_OF_CONTENTS defined in the Constant Property
	                    // file for Blank
	                    if (BTSLUtil.isNullString(contentsSize)) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug(METHOD_NAME, "Contents size of the file not defined in Constant Property file");
	                        }
	                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CONTENTS_SIZE_MISSING, 0, null);
	                        
	                    }
	                }
	            }

	            StringTokenizer startparser = null;

	            // take out each string from the file & put it in a array list
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "Initializing the fileReader, filepath : " + filePathAndFileName);
	            }
	            fileReader = new FileReader("" + filePathAndFileName);
	            if (fileReader != null) {
	                bufferReader = new BufferedReader(fileReader);
	            } else {
	                bufferReader = null;
	            }

	            if (bufferReader != null && bufferReader.ready()) // If File Not
	            // Blank Read line
	            // by Line
	            {
	                while ((tempStr = bufferReader.readLine()) != null) // read the
	                // file
	                // until it
	                // reaches
	                // to end
	                {
	                    tempStr = tempStr.trim();
	                    if (tempStr.length() == 0) // check for the blank line b/w
	                    // the records of the file
	                    {
	                    	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BLANK_LINE_RECORDS, 0, null);
	            			
	                    }
	                    startparser = new StringTokenizer(tempStr, delimiter); // separate
	                    // each
	                    // string
	                    // in
	                    // a
	                    // line
	                    while (startparser.hasMoreTokens()) {
	                        msisdnOrLoginID = startparser.nextToken().trim();
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug(METHOD_NAME, "Fatching the MSISDN's from the file " + msisdnOrLoginID);
	                        }
	                        MobileOrId.add(msisdnOrLoginID); // add each string in
	                        // the list
	                    }

	                    // it can not be allowed to process the file if MSISDN's or
	                    // Logion ID's are more than the defined Limit
	                    if (MobileOrId.size() > Integer.parseInt(contentsSize)) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug(METHOD_NAME, "File contents size of the file is not valid in constant properties file : " + MobileOrId.size());
	                        }
	                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_CONTENT_EXCEED, 0, null);
	            			
	                     }

	                    startparser = null;
	                    tempStr = null;
	                }
	            }

	          
//	            mcomCon = new MComConnection();
//	            con=mcomCon.getConnection();
	            final ArrayList childExistList = new ArrayList();
	            BTSLMessages sendBtslMessage = null;
	            PushMessage pushMessage = null;
	            String msisdn = null;
	            String loginID=null;
	            ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
	            final Date currentDate = new Date();
	            boolean isBalanceFlag = false;
	            while (MobileOrId.size() != countStr) {
	                msisdnOrLoginID = (String) MobileOrId.get(countStr);
	                countStr++;

	                // ** Processing the Login ID's **
	                if (type.equals(PretupsI.LOOKUP_LOGIN_ID)) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(METHOD_NAME, "Processing starts for Login ID's " + type);
	                    }
	                    forDisplayMsg = 1;

	                    // On user deletion action only balance should go
	                    // owner/operator
	                    
	                        // If login id(user) to be deleted does not exists then
	                        // invalidStringFromDao will be true
	                        invalidStringFromDao = channelUserWebDAO.getUserLoginIdExists(con, msisdnOrLoginID, countStr);
	                        if (invalidStringFromDao) {
	                            invalidString.append(msisdnOrLoginID);
	                            invalidString.append(delimiter);
	                        } else {

	                            channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, msisdnOrLoginID, null, PretupsI.STATUS_NOTIN, "'N','C'");
	                        }
	                            // before user deletion if the balance exists then
	                            // we need to move it to owner/operator stock,
	                            // according to the preference
		                        GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
		                        
		                        boolean flag =  _geographyDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(), userVO.getUserID());
								if (!flag) {
			
									UnregisterChUsersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdnOrLoginID,
											countStr, "Not a valid Login ID", "Fail",
											filePathAndFileName + "," + userVO.getNetworkID());
									invalidString.append(msisdnOrLoginID);// append the
			
									invalidString.append(delimiter);
									continue;
			
								}
			                        	
	                            if (PretupsI.USER_STATUS_DELETE_REQUEST.equals(requestVO.getOperationType())) {
	                            if(!SystemPreferences.USR_BTCH_SUS_DEL_APRVL)
	                            isBalanceFlag = userDAO.isUserBalanceExist(con, channelUserVO.getUserID());
	                            else
	                            	isBalanceFlag=false;
	                            if (isBalanceFlag) {
	                                final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
	                                ArrayList<UserBalancesVO> userBal = null;
	                                UserBalancesVO userBalancesVO = null;
	                                final ChannelUserVO fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, channelUserVO.getUserID(), false, currentDate,false);
	                                fromChannelUserVO.setGateway(PretupsI.REQUEST_SOURCE_TYPE_WEB);
	                                final ChannelUserVO toChannelUserVO = channelUserDAO
	                                                .loadChannelUserDetailsForTransfer(con, fromChannelUserVO.getOwnerID(), false, currentDate,false);
	                                userBal = userBalancesDAO.loadUserBalanceForDelete(con, fromChannelUserVO.getUserID());// user
	                                // to
	                                // be
	                                // deleted
	                                Iterator<UserBalancesVO> itr = userBal.iterator();
	                                itr = userBal.iterator();
	                                boolean sendMsgToOwner = false;
	                                long totBalance = 0;
	                                while (itr.hasNext()) {
	                                    userBalancesVO = itr.next();
	                                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RETURN_TO_OPERATOR_STOCK)).booleanValue() || fromChannelUserVO
	                                                    .getOwnerID().equals(fromChannelUserVO.getUserID())) {
	                                        UserDeletionBL.updateBalNChnlTransfersNItemsO2C(con, fromChannelUserVO, toChannelUserVO, PretupsI.REQUEST_SOURCE_TYPE_WEB,
	                                                        PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
	                                    } else {

	                                        UserDeletionBL.updateBalNChnlTransfersNItemsC2C(con, fromChannelUserVO, toChannelUserVO, userVO.getUserID(),
	                                                        PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
	                                        sendMsgToOwner = true; 
	                                        totBalance += userBalancesVO.getBalance();
	                                    }
	                                }
	                                //ASHU
	                                if(sendMsgToOwner) {
	                                	   ChannelUserVO chnlUserVO = new ChannelUserDAO().loadUsersDetails(con, fromChannelUserVO.getMsisdn(), null, PretupsI.STATUS_IN, "'" + PretupsI.USER_STATUS_ACTIVE + "'");
	                                       String msgArr [] = {fromChannelUserVO.getMsisdn(),Long.toString(totBalance)};
	                                       final BTSLMessages sendBtslMessageToOwner = new BTSLMessages(PretupsErrorCodesI.OWNER_USR_BALCREDIT,msgArr);
	                                       final PushMessage pushMessageToOwner = new PushMessage(chnlUserVO.getParentMsisdn(), sendBtslMessageToOwner, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
	                                                       (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), fromChannelUserVO.getNetworkID());
	                                       pushMessageToOwner.push();    
	                                	
	                                }  
	                                
	                            }
	                        }
	                    

	                    // if from deletion action user does not exists i.e.
	                    // invalidStringFromDao=true the deletion of the user will
	                    // not happen
	                    if (!invalidStringFromDao) {
	                        invalidStringFromDao = channelUserWebDAO.deleteOrSuspendChnlUsersInBulkForLoginID(con, msisdnOrLoginID, requestVO.getOperationType(), childExistList, userVO.getUserID(), countStr, prepareStatementMap);// call
	                        if (invalidStringFromDao) {
	                            invalidString.append(msisdnOrLoginID); // append the
	                            // invalid
	                            // Login
	                            // ID in the
	                            // string
	                            // invalidString
	                            invalidString.append(delimiter);
	                            // single line logger entry
	                            UnregisterChUsersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdnOrLoginID, countStr, "Not a valid Login ID", "Fail",
	                                            filePathAndFileName + "," + userVO.getNetworkID());
	                        } else {
	                            validList.add(msisdnOrLoginID.trim()); // insert the
	                            // valid
	                            // Login ID
	                            // in the
	                            // validList
	                        }
	                    }
	                }

	                // ** Processing the MSISDN's **
	                else {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(METHOD_NAME, "Processing starts for MSISDN's " + type);
	                    }
	                    filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdnOrLoginID); // before
	                    // process
	                    // MSISDN
	                    // filter
	                    // each-one

	                    // check for valid MSISDN
	                    if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug(METHOD_NAME, "Not a valid MSISDN " + msisdnOrLoginID);
	                        }
	                        UnregisterChUsersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdnOrLoginID, countStr, "Not a valid MSISDN", "Fail",
	                                        filePathAndFileName + "," + userVO.getNetworkID());
	                        invalidString.append(msisdnOrLoginID);// append the
	                        // invalid MSISDN
	                        // in the string
	                        // invalidString
	                        invalidString.append(delimiter);
	                        continue;
	                    }

	                    // check prefix of the MSISDN
	                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
	                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

	                    if (networkPrefixVO == null) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug(METHOD_NAME, "Not Network prefix found " + msisdnOrLoginID);
	                        }
	                        UnregisterChUsersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdnOrLoginID, countStr, "Not Network prefix found", "Fail",
	                                        filePathAndFileName + "," + userVO.getNetworkID());
	                        invalidString.append(msisdnOrLoginID);
	                        invalidString.append(delimiter);
	                        continue;
	                    }

	                    // check network support of the MSISDN
	                    networkCode = networkPrefixVO.getNetworkCode();
	                    if (!networkCode.equals(userVO.getNetworkID())) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug(METHOD_NAME, "Not supporting Network" + msisdnOrLoginID);
	                        }
	                        UnregisterChUsersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdnOrLoginID, countStr, "Not supporting Network", "Fail",
	                                        filePathAndFileName + "," + userVO.getNetworkID());
	                        invalidString.append(msisdnOrLoginID);
	                        invalidString.append(delimiter);
	                        continue;
	                    }
	                    
	                    channelUserVO = channelUserDAO.loadChannelUserDetails(con, filteredMsisdn);
	                    if(channelUserVO != null){
	                    	 GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
	                         
	                         boolean flag =  _geographyDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(), userVO.getUserID());
	     						if (!flag) {

	     						UnregisterChUsersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdnOrLoginID,
	     								countStr, "Not a valid MSIDN ID", "Fail",
	     								filePathAndFileName + "," + userVO.getNetworkID());
	     						invalidString.append(msisdnOrLoginID);// append the

	     						invalidString.append(delimiter);
	     						continue;

	     					}
	                    }else{
	                    	UnregisterChUsersFileProcessLog.log("FILE PROCESSING", userVO.getUserID(), msisdnOrLoginID,
	 								countStr, "Not a valid MSIDN", "Fail",
	 								filePathAndFileName + "," + userVO.getNetworkID());
	 						invalidString.append(msisdnOrLoginID);

	 						invalidString.append(delimiter);
	 						continue;
	                    }
	                    // insert the valid MSISDN in the validMsisdnList
	                    validList.add(filteredMsisdn);

	                    // On user deletion action only balance should go
	                    // owner/operator
	                    if (PretupsI.USER_STATUS_DELETE_REQUEST.equals(requestVO.getOperationType())) {
	                        // If login id(user) to be deleted does not exists then
	                        // invalidStringFromDao will be true
	                        invalidStringFromDao = channelUserWebDAO.getUserMsisdnExists(con, filteredMsisdn, countStr);
	                        if (invalidStringFromDao) {
	                            invalidString.append(filteredMsisdn);
	                            invalidString.append(delimiter);
	                        } else {
	                            channelUserVO = channelUserDAO.loadChannelUserDetails(con, filteredMsisdn);
	                            if(!SystemPreferences.USR_BTCH_SUS_DEL_APRVL)
	                            balanceMoveForDel(con, channelUserVO.getUserID(), channelUserVO, currentDate, userDAO);
	                        }
	                    }

	                    if (!invalidStringFromDao) {
	                        invalidStringFromDao = channelUserWebDAO.deleteOrSuspendChnlUsersInBulkForMsisdn(con, filteredMsisdn, requestVO.getOperationType(), childExistList, userVO.getUserID(), countStr, prepareStatementMap);
	                        if (invalidStringFromDao) {
	                            invalidString.append(filteredMsisdn);
	                            invalidString.append(delimiter);
	                        }
	                    }

	                }// else end
	                if (invalidStringFromDao) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(METHOD_NAME, "Rollback the transaction for : " + msisdnOrLoginID);
	                    }
	                    con.rollback();
	                } else {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(METHOD_NAME, "Commit the transaction for : " + msisdnOrLoginID);
	                    }
	                    con.commit();

	                    if (type.equals(PretupsI.LOOKUP_LOGIN_ID) && channelUserVO != null) {
	                        msisdn = channelUserVO.getMsisdn();
	                    } else {
	                        msisdn = filteredMsisdn;
	                    }
	                    if (!BTSLUtil.isNullString(msisdn)) {

	                        if (requestVO.getOperationType().equals(PretupsI.USER_STATUS_SUSPEND_REQUEST))// ||unregisterChUserForm.getDeleteOrSuspendorResume().equals(PretupsI.USER_STATUS_DELETED)
	                        {
	                            sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_STATUS_SUSPENDED);
	                            pushMessage = new PushMessage(msisdn, sendBtslMessage, "", "", locale, userVO.getNetworkID());
	                            pushMessage.push();
	                        } else if (requestVO.getOperationType().equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
	                            sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
	                            pushMessage = new PushMessage(msisdn, sendBtslMessage, "", "", locale, userVO.getNetworkID());
	                            pushMessage.push();

	                        }
	                        // 6.4
	                        else if (requestVO.getOperationType().equals(PretupsI.USER_STATUS_RESUME_REQUEST)) {
	                            sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_STATUS_RESUMED);
	                            pushMessage = new PushMessage(msisdn, sendBtslMessage, "", "", locale, userVO.getNetworkID());
	                            pushMessage.push();
	                        }
	                    }
	                    if (channelUserVO == null) {
	                        channelUserVO = ChannelUserVO.getInstance();
	                    }
	                    channelUserVO.setModifiedOn(currentDate);
	                    channelUserVO.setMsisdn(msisdn);
	                    if (requestVO.getOperationType().equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
	                        channelUserVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
	                        ChannelUserLog.log("BLKSUSPCHNLUSR", channelUserVO, userVO, true, null);
	                    } else if (requestVO.getOperationType().equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
	                        channelUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
	                        ChannelUserLog.log("BLKDELCHNLUSR", channelUserVO, userVO, true, null);
	                    } else if (PretupsI.USER_STATUS_RESUME_REQUEST.equals(requestVO.getOperationType())) {
	                        channelUserVO.setStatus(PretupsI.USER_STATUS_RESUMED);
	                        ChannelUserLog.log("BLKRESCHNLUSR", channelUserVO, userVO, true, null);
	                    }

	                }

	                try{
	                
	                if (prepareStatementMap.get("psmtIsExist") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtIsExist")).clearParameters();
	                }
	                if (prepareStatementMap.get("psmtUserID") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtUserID")).clearParameters();
	                }
	                if (prepareStatementMap.get("psmtDelete") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtDelete")).clearParameters();
	                }
	                if (prepareStatementMap.get("psmtResumeExist") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtResumeExist")).clearParameters();
	                }
	                
	                }catch(Exception e){
	                	LOG.errorTrace(METHOD_NAME, e);	
	                }
	                Thread.sleep(50);
	            } // While end
	            if (childExistList != null && !(childExistList.isEmpty())) {
	                ChannelUserVO chnlUserVO = new ChannelUserVO();
	                final int length = childExistList.size();
	                for (int i = 0; i < length; i++) {
	                    for (int j = i + 1; j < length; j++) {
	                        if (((ChannelUserVO) childExistList.get(i)).getCategoryVO().getCategorySequenceNumber() > ((ChannelUserVO) childExistList.get(j)).getCategoryVO()
	                                        .getCategorySequenceNumber()) {
	                            chnlUserVO = (ChannelUserVO) childExistList.get(i);
	                            childExistList.set(i, childExistList.get(j));
	                            childExistList.set(j, chnlUserVO);
	                        }
	                    }
	                }
	                int childExistListSize = childExistList.size() - 1;
	                for (int i = childExistListSize; i >= 0; i--) {
	                    channelUserVO = (ChannelUserVO) childExistList.get(i);
	                    invalidStringFromDao = deleteRetry(con, channelUserVO.getUserID(), requestVO.getOperationType(), userVO.getUserID(),
	                                    ((ChannelUserVO) childExistList.get(i)).getStatus(), ((ChannelUserVO) childExistList.get(i)).getMsisdn(), ((ChannelUserVO) childExistList
	                                                    .get(i)).getLoginID(), type, ((ChannelUserVO) childExistList.get(i)).getCategoryVO().getSequenceNumber(),userVO);
	                    msisdn=((ChannelUserVO) childExistList.get(i)).getMsisdn();
						loginID=((ChannelUserVO) childExistList.get(i)).getLoginID();
						if(invalidStringFromDao)
					    {    
							if(type.equals(PretupsI.LOOKUP_LOGIN_ID))
						    {
								if(!(invalidString.toString().contains(loginID)))
								{
								invalidString.append(((ChannelUserVO)childExistList.get(i)).getLoginID());
							    invalidString.append(delimiter);	
								if(LOG.isDebugEnabled())
									LOG.debug("processUploadedFileForUnReg","Rollback the transaction for : "+((ChannelUserVO)childExistList.get(i)).getLoginID());
								}
						    }
							else
							{	
								if(!(invalidString.toString().contains(msisdn))){
								invalidString.append(((ChannelUserVO)childExistList.get(i)).getMsisdn());
							    invalidString.append(delimiter);	
								if(LOG.isDebugEnabled())
									LOG.debug("processUploadedFileForUnReg","Rollback the transaction for : "+((ChannelUserVO)childExistList.get(i)).getMsisdn());
								}

							}
						    con.rollback();
					    }
	                     else {
	                    	 /* here for removal of msisdn from invalidString*/
	                    	 String msisdnorlog;
								if(msisdn==null){
									msisdnorlog=loginID;
								}
								else{
									msisdnorlog=msisdn;
								}
	                    	 if(invalidString.toString().contains(msisdnorlog)){
	                    		 int indextoremov = invalidString.indexOf(msisdnorlog);
	                    	    if (indextoremov != -1) {
	                    	    	if(indextoremov==0){
	                    	    		invalidString.delete(indextoremov, indextoremov + msisdnorlog.length()+1);
	                    	    	}
	                    	    	else if(indextoremov+msisdnorlog.length()<invalidString.length())
	                    	    	{
	                    	    		invalidString.delete(indextoremov, indextoremov + msisdnorlog.length()+1);	
	                    	    	}
	                    	    	else{
	                    	    		invalidString.delete(indextoremov-1, indextoremov + msisdnorlog.length()+1);
	                    	    	}
	                    	    }
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug(METHOD_NAME, "Commit the transaction");
	                        }
	                        con.commit();
	                    }
	                    	 }

	                }
	            }
	            bufferReader.close();
	            fileReader.close();
	            // It is for the displaying the message No valid MSISDN's in the
	            // file
	            if (forDisplayMsg == 0) {
	                if (validList.isEmpty()) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(METHOD_NAME, "No valid MSISDN in the file, size :" + validList.size());
	                    }
	                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_MSISDN_LIST, 0, null);
	        			
	                 }
	            }
	            // It is for the displaying the message No valid Login ID's in the
	            // file
	            else {
	            	 if(validList.isEmpty() && invalidString.toString().isEmpty()) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("processUploadedFileForUnReg", "No valid Login ID in the file, size :" + validList.size());
	                    }
	                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_LOGIN_ID_LIST, 0, null);
	        			
	                }
	            }

	            // make Archive file on the server.
//	            fileMoved = this.moveFileToArchive(filePathAndFileName, fileName);
//
//	            if (!fileMoved) {
//	            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_CANNOT_MOVE, 0, null);
//	    			
//	            }

	            // if some MSISDN's are invalid then showing the list of these
	            // invalid MSISDN's
	            String resmsg="";
	            String arr[] = new String[1];
	            if (requestVO.getOperationType().equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
	                if (invalidString.length() == 0) {
	                	resmsg=RestAPIStringParser.getMessage(
	    						locale,
	    						PretupsErrorCodesI.DELETE_SUCCESS, null);
	                	response.setMessageCode(PretupsErrorCodesI.DELETE_SUCCESS);
	                } else {
	                    if (forDisplayMsg == 0) {
	                    	arr[0] = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
	                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DELETE_PARTIAL_SUCCESS_MSISDN,
	        						new String[] { arr[0] });
	                    } else {
	                    	arr[0] = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
	                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DELETE_PARTIAL_SUCCESS_LOGIN_ID,
	        						new String[] { arr[0] });
	                    }
	                }
	            } else if (PretupsI.USER_STATUS_SUSPEND_REQUEST.equals(requestVO.getOperationType())) {
	                if (invalidString.length() == 0) {
	                	resmsg=RestAPIStringParser.getMessage(
	    						locale,
	    						PretupsErrorCodesI.SUSPEND_SUCCESS, null);	  
	                	response.setMessageCode(PretupsErrorCodesI.SUSPEND_SUCCESS);
	                } else {
	                    if (forDisplayMsg == 0) {
	                    	arr[0] = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
	                    	/*
	                        throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SUSPEND_PARTIAL_SUCCESS_MSISDN,
	        						new String[] { arr[0] });
	        				*/
	                        
	                        
	                        //anand code starts
	                    	//String[] strAr1=new String[] {"Ani", "Sam", "Joe"}; 
	                        String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUSPEND_PARTIAL_SUCCESS_MSISDN,new String[] { arr[0] });
	                        
	                        String filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
	                        C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
	                        String filePathConstemp = filePathCons + "temp/";        
	            			c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
	            			
	            			String filepathtemp = filePathConstemp ;   

	            			String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
	            			writeFileTXT(error, filepathtemp + logErrorFilename + ".txt");
	                        
	            			File error1 =new File(filepathtemp+logErrorFilename+ ".txt");
	            			byte[] fileContent = FileUtils.readFileToByteArray(error1);
	            			String encodedString = Base64.getEncoder().encodeToString(fileContent);
	            	   		response.setFileAttachment(encodedString);
	            	   		response.setFileName(logErrorFilename+".txt");
	            			
	            	   		
	            	   		String[] strAr1=new String[] {"msisdn"};
	            	   		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SUSPEND_PARTIAL_SUCCESS_MSISDN_MESSAGE,
	            	   				strAr1);
	            	   		
//	            	   		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SUSPEND_PARTIAL_SUCCESS_MSISDN,
//	        						new String[] { });
	                        //anand code ends
	                        
	                    } else {
	                    	arr[0] = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
	                    	
	                    	

	                        //anand code starts
	                        String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUSPEND_PARTIAL_SUCCESS_LOGIN_ID,new String[] { arr[0] });
	                        
	                        String filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
	                        C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
	                        String filePathConstemp = filePathCons + "temp/";        
	            			c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
	            			
	            			String filepathtemp = filePathConstemp ;   

	            			String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
	            			writeFileTXT(error, filepathtemp + logErrorFilename + ".txt");
	                        
	            			File error1 =new File(filepathtemp+logErrorFilename+ ".txt");
	            			byte[] fileContent = FileUtils.readFileToByteArray(error1);
	            			String encodedString = Base64.getEncoder().encodeToString(fileContent);
	            	   		response.setFileAttachment(encodedString);
	            	   		response.setFileName(logErrorFilename+".txt");
	            	   		
	            	   		
	            	   		String[] strAr1=new String[] {"loginid"};
	            	   		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SUSPEND_PARTIAL_SUCCESS_LOGIN_ID_MESSAGE,
	            	   				strAr1);
	            	   		//anand code ends
	                    	
	                        //throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SUSPEND_PARTIAL_SUCCESS_LOGIN_ID,new String[] { });
	                    }
	                }
	            } else if (PretupsI.USER_STATUS_RESUME_REQUEST.equals(requestVO.getOperationType())) {
	                if (invalidString.length() == 0) {
	                	resmsg=RestAPIStringParser.getMessage(
	    						locale,
	    						PretupsErrorCodesI.RESUME_SUCCESS, null);
	                	response.setMessageCode(PretupsErrorCodesI.RESUME_SUCCESS);
	                } else {
	                    if (forDisplayMsg == 0) {
	                    	arr[0] = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
	                    	
	                    	 //anand code starts
	                        String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_MSISDN,new String[] { arr[0] });
	                        
	                        String filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
	                        C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
	                        String filePathConstemp = filePathCons + "temp/";        
	            			c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
	            			
	            			String filepathtemp = filePathConstemp ;   

	            			String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
	            			writeFileTXT(error, filepathtemp + logErrorFilename + ".txt");
	                        
	            			File error1 =new File(filepathtemp+logErrorFilename+ ".txt");
	            			byte[] fileContent = FileUtils.readFileToByteArray(error1);
	            			String encodedString = Base64.getEncoder().encodeToString(fileContent);
	            	   		response.setFileAttachment(encodedString);
	            	   		response.setFileName(logErrorFilename+".txt");
	            	   		
	            	   		
	            	   		String[] strAr1=new String[] {"msisdn"};
	            	   		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_MSISDN_MESSAGE,
	            	   				strAr1);
	            	   		//anand code ends
	                    	
	                    	
	                        //throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_MSISDN,new String[] { });
	                    } else {
	                    	arr[0] = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
	                    	
	                    	
	                    	//anand code starts
	                        String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_LOGIN_ID,new String[] { arr[0] });
	                        
	                        String filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
	                        C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
	                        String filePathConstemp = filePathCons + "temp/";        
	            			c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
	            			
	            			String filepathtemp = filePathConstemp ;   

	            			String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
	            			writeFileTXT(error, filepathtemp + logErrorFilename + ".txt");
	                        
	            			File error1 =new File(filepathtemp+logErrorFilename+ ".txt");
	            			byte[] fileContent = FileUtils.readFileToByteArray(error1);
	            			String encodedString = Base64.getEncoder().encodeToString(fileContent);
	            	   		response.setFileAttachment(encodedString);
	            	   		response.setFileName(logErrorFilename+".txt");
	            	   		
	            	   		String[] strAr1=new String[] {"loginid"};
	            	   		throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_LOGIN_ID_MESSAGE,
	            	   				strAr1);
	            	   		//anand code ends
	                    	
	                        //throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_LOGIN_ID,new String[] { });
	                    }
	                }
	            }
	            
	            response.setStatus((HttpStatus.SC_OK));
				response.setMessage(resmsg);
	        }// Try end
	        catch (BTSLBaseException be) {
				LOG.error(METHOD_NAME, "Exception:e=" + be);
				LOG.errorTrace(METHOD_NAME, be);
				if (BTSLUtil.isNullString(response.getMessage())) {
					String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
					response.setMessageCode(be.getMessage());
					response.setMessage(msg);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
				}

			}
	        catch (Exception e) {
	        	LOG.error(METHOD_NAME, "Exception:e=" + e);
	            LOG.errorTrace(METHOD_NAME, e);
	            file = new File(filePath, fileName);
	            boolean isDeleted = file.delete();
	            if(isDeleted){
	             LOG.debug(METHOD_NAME, "File deleted successfully");
	            }
	            response.setStatus((HttpStatus.SC_BAD_REQUEST));
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.CHANGE_STATUS_NOT_PERFORMED, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.CHANGE_STATUS_NOT_PERFORMED);
			
	            
	        } finally {
	        	try {
	                if (bufferReader != null) {
	                    bufferReader.close();
	                }
	            } catch (Exception e1) {
	                LOG.errorTrace(METHOD_NAME, e1);
	                bufferReader = null;
	            }
	        	try {
	                if (fileReader != null) {
	                    fileReader.close();
	                }
	            } catch (Exception e1) {
	                LOG.errorTrace(METHOD_NAME, e1);
	                fileReader = null;
	            }
	            try {
	                if (prepareStatementMap.get("psmtIsExist") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtIsExist")).close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (prepareStatementMap.get("psmtUserID") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtUserID")).close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (prepareStatementMap.get("psmtDelete") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtDelete")).close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (prepareStatementMap.get("psmtResumeExist") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtResumeExist")).close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (prepareStatementMap.get("psmtChildExist") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtChildExist")).close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (prepareStatementMap.get("psmtUserBalanceExist") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtUserBalanceExist")).close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (prepareStatementMap.get("psmtChnlTrnsfrPendingTransactionExist") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtChnlTrnsfrPendingTransactionExist")).close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (prepareStatementMap.get("psmtfocPendingTransactionExist") != null) {
	                    ((PreparedStatement) prepareStatementMap.get("psmtfocPendingTransactionExist")).close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
				
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "Exit:return : " + METHOD_NAME);
	            }
	        }
	        return response;
	    }

	    //anand code starts
	    private void writeFileTXT(String error, String excelFilePath) throws IOException {
			try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
		    	csvWriter.write(error);
	    }
		}
	    
	    
	    
	    
	    //anand code ends
	    
	    /**
	     * This method is used to make Archive file on the server.
	     * Method moveFileToArchive.
	     * 
	     * @param p_fileName
	     *            String
	     * @param p_file
	     *            String
	     * @return boolean
	     */
	    private boolean moveFileToArchive(String p_filePathAndFileName, String p_fileName) {
	        final String METHOD_NAME = "moveFileToArchive";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(METHOD_NAME, " Entered ");
	        }
	        final File fileRead = new File(p_filePathAndFileName);
	        File fileArchive = new File("" + Constants.getProperty("ArchiveFilePathForUnRegChnlUser"));
	        if (!fileArchive.isDirectory()) {
	            fileArchive.mkdirs();
	        }
	        fileArchive = new File("" + Constants.getProperty("ArchiveFilePathForUnRegChnlUser") + p_fileName + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
	        // make
	        // the
	        // new
	        // file
	        // name
	        final boolean flag = fileRead.renameTo(fileArchive);
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(METHOD_NAME, " Exiting File Moved=" + flag);
	        }
	        return flag;
	    }// end of moveFileToArchive

	    /**
	     * This method is used to delete the parent after child are deleted.
	     * Method deleteRetry.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_userID
	     *            String
	     * @param p_deleteOrSuspend
	     *            String
	     * 
	     * @return boolean
	     */

	    public boolean deleteRetry(Connection p_con, String p_userID, String p_deleteOrSuspend, String p_modifiedBy, String p_preStatus, String p_MSISDN, String p_loginID, String type, int p_countStr,UserVO userVO) {
	        final String METHOD_NAME = "deleteRetry";
	        boolean isBalanceFlag = false;
	        boolean isO2CPendingFlag = false;
	        boolean isBatchFOCTxnPendingFlag = false;
	        boolean isChildFlag = false;
	        ChannelUserWebDAO channelUserWebDAO = null;
	        final Date currentDate = new Date();
	        boolean deletedError = true;
	        String MSISDNOrLoginIDForLog;
	        final UserDAO userDAO = new UserDAO();

	        if (type.equals(PretupsI.LOOKUP_LOGIN_ID)) {
	            MSISDNOrLoginIDForLog = p_loginID;
	        } else {
	            MSISDNOrLoginIDForLog = p_MSISDN;
	        }

	        try {
	            channelUserWebDAO = new ChannelUserWebDAO();
	            isChildFlag = userDAO.isChildUserActive(p_con, p_userID);
	            if (isChildFlag) {
	                if (LOG.isDebugEnabled()) {
	                    LOG.debug(METHOD_NAME, "This user has childs down the hierarchy, so can't be deleted " + p_userID);
	                }
	                UnregisterChUsersFileProcessLog.log("CHILD EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr, "Child exists for this user", "Fail", "");
	            }
	            else {
	            	   boolean isSOSPendingFlag = false;
	            	   if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())
						{
					        // Checking SOS Pending transactions
					        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
					        isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(p_con, p_userID);
						}
	            	   if(isSOSPendingFlag){
	                       LogFactory.printLog(METHOD_NAME, "This user has pending SOS transaction, so can't be deleted " + p_userID, LOG);
	                       UnregisterChUsersFileProcessLog
	                                       .log("UNSETTLED SOS TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr, "Pending User's transaction", "Fail", "");
	                   }else {
	                	   boolean isLRPendingFlag = false;
	                	   // checking Pending Last recharge transaction
	                	   if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue()){
	                		   UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
	               				UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
	               				userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(p_userID, p_con, false, null);
	               				if (userTrfCntVO!=null) 
	               					isLRPendingFlag = true;
	    					}
	                	   if(isLRPendingFlag){
	                           LogFactory.printLog(METHOD_NAME, "This user has pending Last Recharge transaction, so can't be deleted " + p_userID, LOG);
	                           UnregisterChUsersFileProcessLog
	                                           .log("UNSETTLED LAST RECHARGE TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr, "Pending User's Last recharge credit request transaction", "Fail", "");
	                       }else {
		                       // Checking O2C Pending transactions
		                       final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
		                       isO2CPendingFlag = transferDAO.isPendingTransactionExist(p_con, p_userID);
		                       if (isO2CPendingFlag) {
		                           if (LOG.isDebugEnabled()) {
		                               LOG.debug(METHOD_NAME, "This user has pending transactions, so can't be deleted " + p_userID);
		                           }
		                           UnregisterChUsersFileProcessLog
		                                           .log("IS PENDING TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr, "Pending User's transaction", "Fail", "");
		                       } else {
		                           // Checking Batch FOC Pending transactions - Ved
		                           // 07/08/06
		                           final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
		                           isBatchFOCTxnPendingFlag = batchTransferDAO.isPendingTransactionExist(p_con, p_userID);
		                           if (isBatchFOCTxnPendingFlag) {
		                               if (LOG.isDebugEnabled()) {
		                                   LOG.debug(METHOD_NAME, "This user has pending batch foc transactions, so can't be deleted " + p_userID);
		                               }
		                               UnregisterChUsersFileProcessLog.log("IS PENDING BATCH FOC TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr,
		                                               "Pending User's batch foc transaction", "Fail", "");
		                           } else {
		                        	   	//checking batch C2C transaction pending
		                        	   final C2CBatchTransferDAO c2cBatchTransferDAO = new C2CBatchTransferDAO();
		                               boolean isbatchc2cPendingTxn = c2cBatchTransferDAO.isPendingC2CTransactionExist(p_con, userVO.getUserID());
		                               if (isbatchc2cPendingTxn) {
			                               if (LOG.isDebugEnabled()) {
			                                   LOG.debug(METHOD_NAME, "This user has pending batch c2c transactions, so can't be deleted " + p_userID);
			                               }
			                               UnregisterChUsersFileProcessLog.log("IS PENDING BATCH C2C TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr,
			                                               "Pending User's batch c2c transaction", "Fail", "");
			                           }else{
			                        	   //checking batch O2C transaction
			                        	   final BatchO2CTransferWebDAO o2cBatchTransferDAO = new BatchO2CTransferWebDAO();
			                               boolean isBatchO2CTxnPending = o2cBatchTransferDAO.isPendingO2CTransactionExist(p_con, userVO.getUserID());
			                               if(isBatchO2CTxnPending){
			                            	  if (LOG.isDebugEnabled()) {
				                                   LOG.debug(METHOD_NAME, "This user has pending batch o2c transactions, so can't be deleted " + p_userID);
				                               }
				                               UnregisterChUsersFileProcessLog.log("IS PENDING BATCH O2C TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr,
				                                               "Pending User's batch o2c transaction", "Fail", "");
			                              }
			                               else{
			                            	   balanceMoveForDel(p_con, p_userID, userVO, currentDate, userDAO);
				                               deletedError = channelUserWebDAO.deleteOrSuspendChnlUsers(p_con, p_userID, p_deleteOrSuspend, p_modifiedBy, p_preStatus);
			                               }
			                            }
		                        	  }
		                       	}
		                     }
	                       }
	               	}
	        } catch (Exception ex) {
	            LOG.errorTrace(METHOD_NAME, ex);
	        }
	        return deletedError;
	    }

		/**
		 * @param p_con
		 * @param p_userID
		 * @param userVO
		 * @param currentDate
		 * @param userDAO
		 * @throws BTSLBaseException
		 * @throws Exception
		 */
		private void balanceMoveForDel(Connection p_con, String p_userID, UserVO userVO, final Date currentDate, final UserDAO userDAO) throws BTSLBaseException, Exception {
			final String METHOD_NAME = "balanceMoveForDel";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(METHOD_NAME, "Entered");
	        }
			
			boolean isBalanceFlag;
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			isBalanceFlag = userDAO.isUserBalanceExist(p_con, p_userID);
			if (isBalanceFlag) {
			    // to implement
				 boolean sendMsgToOwner = false;
	             long totBalance = 0;
			    final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
			    ArrayList<UserBalancesVO> userBal = null;
			    UserBalancesVO userBalancesVO = null;
			    final ChannelUserVO fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(p_con, p_userID, false, currentDate,false);
			    fromChannelUserVO.setGateway(PretupsI.REQUEST_SOURCE_TYPE_WEB);
			    final ChannelUserVO toChannelUserVO = channelUserDAO
			                    .loadChannelUserDetailsForTransfer(p_con, fromChannelUserVO.getOwnerID(), false, currentDate,false);
			    userBal = userBalancesDAO.loadUserBalanceForDelete(p_con, fromChannelUserVO.getUserID());// user
			    // to
			    // be
			    // deleted
			    Iterator<UserBalancesVO> itr = userBal.iterator();
			    itr = userBal.iterator();
			    while (itr.hasNext()) {
			        userBalancesVO = itr.next();
			        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RETURN_TO_OPERATOR_STOCK)).booleanValue() || fromChannelUserVO
			                        .getOwnerID().equals(fromChannelUserVO.getUserID())) {
			            UserDeletionBL.updateBalNChnlTransfersNItemsO2C(p_con, fromChannelUserVO, toChannelUserVO, PretupsI.REQUEST_SOURCE_TYPE_WEB,
			                            PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
			        } else {
			        	if(!PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(toChannelUserVO.getStatus()))
	                	{
			            UserDeletionBL.updateBalNChnlTransfersNItemsC2C(p_con, fromChannelUserVO, toChannelUserVO, userVO.getUserID(),
			                            PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
			            sendMsgToOwner = true; 
	                    totBalance += userBalancesVO.getBalance();
	                	}
			        	else
			        		throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PARENT_USER_SUSPENDED, 0, null);
						}
			    }
			  //ASHU
	            if(sendMsgToOwner) {
	            		ChannelUserVO prntChnlUserVO = new ChannelUserDAO().loadChannelUserByUserID(p_con, fromChannelUserVO.getParentID());
	            		String msgArr [] = {fromChannelUserVO.getMsisdn(),PretupsBL.getDisplayAmount(totBalance)};
	                   final BTSLMessages sendBtslMessageToOwner = new BTSLMessages(PretupsErrorCodesI.OWNER_USR_BALCREDIT,msgArr);
	                   final PushMessage pushMessageToOwner = new PushMessage(prntChnlUserVO.getParentMsisdn(), sendBtslMessageToOwner, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
	                                   (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), fromChannelUserVO.getNetworkID());
	                   pushMessageToOwner.push();    
	            	
	            }  
			}
			 if (LOG.isDebugEnabled()) {
		            LOG.debug(METHOD_NAME, " Exiting");
		        }
		}
		
		/**
		 * 
		 * @param fileName
		 * @throws BTSLBaseException
		 */
		public void validateFileName(String fileName) throws BTSLBaseException {
			final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
			final Pattern r = Pattern.compile(pattern);
			final Matcher m = r.matcher(fileName);
			if (!m.find()) {
				throw new BTSLBaseException(this, "validateFileName", PretupsErrorCodesI.INVALID_FILE_NAME1,
						PretupsI.RESPONSE_FAIL, null);
			}
		}
		
		/**
		 * 
		 * @param fileDetailsMap
		 * @throws BTSLBaseException
		 */
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
		

		@Override
		public StaffUserListByParentResponseVO getstaffUserListByParent(Connection con, String loginID,
				HttpServletResponse responseSwag, StaffUserListByParntReqVO requestVO)
				throws BTSLBaseException, SQLException {
			final String METHOD_NAME = "ChannelUserListImpl";
			StaffUserListByParentResponseVO response = new StaffUserListByParentResponseVO();
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
			}
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			UserDAO userDAO = new UserDAO();
			try {
				
				  if(!BTSLUtil.isNullString(requestVO.getLoginID())) {
					  requestVO.setMsisdn(PretupsI.ALL);
					  setCommonstaffRequestVO(requestVO);
				  }else if (!BTSLUtil.isNullString(requestVO.getMsisdn())) {
					  requestVO.setLoginID(PretupsI.ALL);
					  setCommonstaffRequestVO(requestVO);
				  }else {
					  requestVO.setMsisdn(PretupsI.ALL);
					  requestVO.setLoginID(PretupsI.ALL);
				  }
				
				
			
				response = userDAO.getStaffUsersListbyParent(con, requestVO);
				if(response.getStaffuserList()!=null && response.getStaffuserList().size()==0)  {
					 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_RECORDS_FOUND, 0, null);
				}
			
			}catch (BTSLBaseException be) {
				LOG.error(METHOD_NAME, "Exception:e=" + be);
				LOG.errorTrace(METHOD_NAME, be);
				if (!BTSLUtil.isNullString(be.getMessage())) {
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
						PretupsErrorCodesI.NO_CHNL_USER_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.NO_CHNL_USER_FOUND);
			}

			return response;
		}
		
		
		
	private void setCommonstaffRequestVO(StaffUserListByParntReqVO requestVO) {

		  requestVO.setUserCategory("All");
		  requestVO.setStatus("ALL");
		  requestVO.setDomain("ALL");
		  requestVO.setUserCategory("ALL");
		  requestVO.setOwnerUserID("ALL");
		  requestVO.setParentUserID("ALL");
		  requestVO.setGeography("ALL");
		  requestVO.setUserName("ALL");
		
	}


	@Override
	public CommissionProfileResponseVO commissionProfileBy(Connection con, String geographyLoc, String networkCode, String userGrade,String categoryCode
			) throws BTSLBaseException, SQLException {
		final String methodName ="commissionProfileBy";
		CommissionProfileResponseVO commissionResponseVO = new CommissionProfileResponseVO();
		UserWebDAO userWebDAO = new UserWebDAO();
		List<CommissionProfileSetVO> commissionProfileList =userWebDAO.loadCommisionProfileListByGradeGeography(con,categoryCode,networkCode,geographyLoc,userGrade);
	       	if(commissionProfileList!=null && commissionProfileList.isEmpty()) {
	       		throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.NO_RECORDS_FOUND, 0, null);
	       	 }
		commissionResponseVO.setCommissionProfileList(commissionProfileList);
		return commissionResponseVO;
	}
	
	
	
	@Override
	public ChannelUserListResponseVO getChannelUserListByAdmin2(Connection con, String loginID,
			HttpServletResponse responseSwag, ChannelUserListRequestVO requestVO, String searchType)
			throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "getChannelUserListByAdmin2";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ChannelUserListResponseVO response = new ChannelUserListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO channelUserVO = null;
		DomainDAO domainDao = new DomainDAO();
		GeographicalDomainDAO geoDomainDao = new GeographicalDomainDAO();
		UserVO loggedinUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
		boolean onlyChanneluser=true;
		
		if(loggedinUserVO.getDomainID().equals(PretupsI.OPERATOR_TYPE_OPT)) {
			onlyChanneluser=false;
		}
		if(BTSLUtil.isNullString(requestVO.getLoginID()) && searchType.equalsIgnoreCase(PretupsI.LOGIN_ID))
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LOGIN_ID_BLANK);
		if(BTSLUtil.isNullString(requestVO.getMsisdn()) && searchType.equalsIgnoreCase(PretupsI.MSISDN))
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MSISDN_NULL);
		if(BTSLUtil.isNullString(requestVO.getExtCode()) && searchType.equalsIgnoreCase(PretupsI.EXTCODE))
			throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BULK_UMOD_EXTERNAL_CODE_COMMENT);
		try {

			final String status = ((String) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.ADMINISTRBLY_USER_STATUS_CHANG_NEW));
			Set<String> set = new HashSet<String>();
			String statusList[] = status.split(",");
			for (String statusObj : statusList) {
				String statusObjSplit = statusObj.split(":")[0];
				set.add(statusObjSplit);
			}

			if (searchType.equalsIgnoreCase(PretupsI.INPUT_MSISDN)) {

				if(!BTSLUtil.isNumeric(requestVO.getMsisdn())){
					throw new BTSLBaseException(classname,METHOD_NAME,PretupsErrorCodesI.MSISDN_INVALID);
				}

				if (!BTSLUtil.isEmpty(requestVO.getMsisdn())) {

					final String msisdn = requestVO.getMsisdn();
					final String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
					final String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

					final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache
							.getObject(msisdnPrefix);
					if (networkPrefixVO == null) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND,
								0, null);
					}
					final String networkCode = networkPrefixVO.getNetworkCode();

					if (networkCode == null || !networkCode.equals(loggedinUserVO.getNetworkID())) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND,
								0, null);
					}

					channelUserVO = userDAO.loadUserDetailsByMsisdn(con, requestVO.getMsisdn());

					if (channelUserVO != null && channelUserVO.getUserCode() == null) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_IDENTIFIER_VALUE,
								0, null);
					}
//					Removed after discussion with akhilesh & ashish as no use case found
//					if (channelUserVO == null || !channelUserVO.getUserCode().equals(filteredMsisdn)) {
//						throw new BTSLBaseException(PretupsErrorCodesI.MSISDN_INVALID);
//					}
					ArrayList<ListValueVO> domainList = domainDao.loadDomainListByUserId(con,
							loggedinUserVO.getUserID());
					if (domainList != null && !domainList.isEmpty()) {
						ListValueVO listValueVO = null;
						boolean domainfound = false;

						for (int i = 0, j = domainList.size(); i < j; i++) {
							listValueVO = (ListValueVO) domainList.get(i);
							if (channelUserVO.getCategoryVO().getDomainCodeforCategory()
									.equals(listValueVO.getValue())) {
								domainfound = true;
								break;
							}
						}
						if (!domainfound) {
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_DOMAIN,
									0, null);
						}
					}

					final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
					if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(),
							loggedinUserVO.getUserID())) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN,
								0, null);
					}

				}

				response = userDAO.getChannelUsersList2(con,PretupsI.ALL, PretupsI.ALL, PretupsI.ALL, channelUserVO.getUserID(), PretupsI.ALL,
						true,onlyChanneluser);
				ArrayList<GetChannelUsersMsg> channelUsersList1 = new ArrayList<GetChannelUsersMsg>();
				for (GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList()) {
					if (!BTSLUtil.isNullString(getChannelUsersMsg.getMsisdn())) {

						if (getChannelUsersMsg.getMsisdn().equals(requestVO.getMsisdn())) {

							if (set.contains(getChannelUsersMsg.getStatusCode())) {
								getChannelUsersMsg.setCanChangeStatus(true);
							} else {
								getChannelUsersMsg.setCanChangeStatus(false);
							}

							channelUsersList1.add(getChannelUsersMsg);

						}
					}

				}

				response.setChannelUsersList(channelUsersList1);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);

			}

			else if (searchType.equalsIgnoreCase(PretupsI.INPUT_LOGIN_ID)) {

				if (!BTSLUtil.isEmpty(requestVO.getLoginID())) {

					channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, requestVO.getLoginID());

					if (channelUserVO == null) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_LOGIN_ID, 0,
								null);
					}

					ArrayList<ListValueVO> domainList = domainDao.loadDomainListByUserId(con,
							loggedinUserVO.getUserID());
					if (domainList != null && !domainList.isEmpty()) {
						ListValueVO listValueVO = null;
						boolean domainfound = false;

						for (int i = 0, j = domainList.size(); i < j; i++) {
							listValueVO = (ListValueVO) domainList.get(i);
							if (channelUserVO.getCategoryVO().getDomainCodeforCategory()
									.equals(listValueVO.getValue())) {
								domainfound = true;
								break;
							}
						}
						if (!domainfound) {
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_DOMAIN,
									0, null);

						}
					}

					boolean geoFound = false;
					List<UserGeographiesVO> userGeoDomains = channelUserVO.getGeographicalAreaList();
					if (userGeoDomains.size() == 0)
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN,
								0, null);
					for (UserGeographiesVO userGeoDomain : userGeoDomains) {
						if (geoDomainDao.isGeoDomainExistInHierarchy(con, userGeoDomain.getGraphDomainCode(),
								loggedinUserVO.getUserID())) {
							geoFound = true;
							break;
						}
					}

					if (!geoFound) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN,
								0, null);
					}

				}

				response = userDAO.getChannelUsersList2(con, PretupsI.ALL ,  PretupsI.ALL ,  PretupsI.ALL , channelUserVO.getUserID(),  PretupsI.ALL ,
						true,onlyChanneluser);
				ArrayList<GetChannelUsersMsg> channelUsersList1 = new ArrayList<GetChannelUsersMsg>();
				for (GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList()) {
					if (!BTSLUtil.isNullString(getChannelUsersMsg.getLoginID())) {

						if (getChannelUsersMsg.getLoginID().equals(requestVO.getLoginID())) {

							if (set.contains(getChannelUsersMsg.getStatusCode())) {
								getChannelUsersMsg.setCanChangeStatus(true);
							} else {
								getChannelUsersMsg.setCanChangeStatus(false);
							}

							channelUsersList1.add(getChannelUsersMsg);

						}
					}

				}

				response.setChannelUsersList(channelUsersList1);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);

			}

			else if (searchType.equalsIgnoreCase(PretupsI.INPUT_ADVANCE)) {

				response = userDAO.getChannelUsersList2(con, requestVO.getDomain(), requestVO.getUserCategory(), PretupsI.ALL,
						requestVO.getParentUserID(), requestVO.getStatus(), true,onlyChanneluser);

				ArrayList<GetChannelUsersMsg> channelUsersList1 = new ArrayList<GetChannelUsersMsg>();
				for (GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList()) {
					if (set.contains(getChannelUsersMsg.getStatusCode())) {
						getChannelUsersMsg.setCanChangeStatus(true);
					} else {
						getChannelUsersMsg.setCanChangeStatus(false);
					}

					channelUsersList1.add(getChannelUsersMsg);

				}

				if (!BTSLUtil.isNullOrEmptyList(channelUsersList1)) {
					response.setChannelUsersList(channelUsersList1);
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.SUCCESS);

				}

				else {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_CHNL_USER_FOUND, 0, null);
				}

			}else if(searchType.equalsIgnoreCase(PretupsI.EXTCODE)){
				if (!BTSLUtil.isEmpty(requestVO.getExtCode())) {

					final String extCode = requestVO.getExtCode();
					channelUserVO = userDAO.loadAllUserDetailsByExternalCode(con,requestVO.getExtCode());
					String statusUsed =  PretupsI.STATUS_NOTIN;

//					channelUserVO = new ChannelUserDAO().loadUserDetailsByExtCode(con, requestVO.getExtCode(), null, PretupsI.STATUS_NOTIN, PretupsBL.userStatusNotIn());


					if (channelUserVO != null && channelUserVO.getUserCode() == null) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.EXTERNAL_CODE_GEO_ERROR,
								0, null);
					}

					ArrayList<ListValueVO> domainList = domainDao.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE);
					if (domainList != null && !domainList.isEmpty()) {
						ListValueVO listValueVO = null;
						boolean domainfound = false;

						for (int i = 0, j = domainList.size(); i < j; i++) {
							listValueVO = (ListValueVO) domainList.get(i);
							if (channelUserVO.getCategoryVO().getDomainCodeforCategory()
									.equals(listValueVO.getValue())) {
								domainfound = true;
								break;
							}
						}
						if (!domainfound) {
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_DOMAIN,
									0, null);
						}
					}

					final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();

					boolean flag = new UserWebDAO().isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
							loggedinUserVO.getUserID(), loggedinUserVO.getCategoryVO().getGrphDomainType());
					if (!flag) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NOT_IN_GEO_DOMAIN,
								0, null);
					}

				}
				if (!loggedinUserVO.getCategoryVO().getCategoryCode().equalsIgnoreCase(PretupsI.CUSTOMER_CARE))
					response = userDAO.getChannelUsersList2(con, PretupsI.ALL, PretupsI.ALL, PretupsI.ALL, channelUserVO.getUserID(), PretupsI.ALL,
							true, onlyChanneluser);
				else
					response = userDAO.getChannelUsersListCCE(con, PretupsI.ALL, PretupsI.ALL, PretupsI.ALL, channelUserVO.getUserID(), PretupsI.ALL,
							true, onlyChanneluser);
				ArrayList<GetChannelUsersMsg> channelUsersList1 = new ArrayList<GetChannelUsersMsg>();
				for (GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList()) {
					if (set.contains(getChannelUsersMsg.getStatusCode())) {
						getChannelUsersMsg.setCanChangeStatus(true);
					} else {
						getChannelUsersMsg.setCanChangeStatus(false);
					}

					channelUsersList1.add(getChannelUsersMsg);

				}

				response.setChannelUsersList(channelUsersList1);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);

			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
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
					PretupsErrorCodesI.NO_CHNL_USER_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.NO_CHNL_USER_FOUND);
		}

		return response;
	}

		

}
