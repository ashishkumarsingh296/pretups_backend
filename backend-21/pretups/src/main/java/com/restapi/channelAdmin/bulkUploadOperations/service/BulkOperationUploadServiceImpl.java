package com.restapi.channelAdmin.bulkUploadOperations.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.*;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.*;
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
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
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.channelAdmin.bulkUploadOperations.ResponseVO.BulkOperationResponseVO;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE,
                    proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BulkOperationUploadServiceImpl implements BulkOperationUploadService {
	
	public static final Log log = LogFactory.getLog(BulkOperationUploadServiceImpl.class.getName());
	private  String delimiter;
	private ArrayList childExistList;
	private  ArrayList validList;
	private ChannelUserVO loggedInUserVO;
	private String 	filePathAndFileName, fileName;
	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePathAndFileName() {
		return filePathAndFileName;
	}

	public void setFilePathAndFileName(String filePathAndFileName) {
		this.filePathAndFileName = filePathAndFileName;
	}

	BulkOperationUploadServiceImpl(){
		delimiter= Constants.getProperty("DelimiterForUploadedFileForUnRegChnlUser");
	}

	@Override
	public String  validateUploadedFile(String fileName) throws BTSLBaseException {
		final String METHOD_NAME="validateUploadedFile";
		delimiter= Constants.getProperty("DelimiterForUploadedFileForUnRegChnlUser");
        final String filePath = Constants.getProperty("UploadFileForUnRegChnlUserPath");
        final String contentsSize = Constants.getProperty("NO_OF_CONTENTS");
        this.fileName=fileName;
        this.filePathAndFileName=filePath;

        if (BTSLUtil.isNullString(delimiter)) {
            if (log.isDebugEnabled()) {
            	log.debug(METHOD_NAME, "Delimiter not defined in Constant Property file");
            }
            throw new BTSLBaseException(this, METHOD_NAME, "user.uploadFileForChUserUnregisterBulk.error.delimitermissingincons", "uploadFileForChUserUnregisterBulk");
        } else {
            // check the FILEPATH defined in the Constant Property file for
            // Blank
            if (BTSLUtil.isNullString(filePath)) {
                if (log.isDebugEnabled()) {
                	log.debug(METHOD_NAME, "File path not defined in Constant Property file");
                }
                throw new BTSLBaseException(this, METHOD_NAME, "user.uploadFileForChUserUnregisterBulk.error.filepathmissingincons", "uploadFileForChUserUnregisterBulk");
            } else {
                // check the NO_OF_CONTENTS defined in the Constant Property
                // file for Blank
                if (BTSLUtil.isNullString(contentsSize)) {
                    if (log.isDebugEnabled()) {
                    	log.debug(METHOD_NAME, "Contents size of the file not defined in Constant Property file");
                    }
                    throw new BTSLBaseException(this, METHOD_NAME, "user.uploadFileForChUserUnregisterBulk.error.contentssizehmissingincons",
                                    "uploadFileForChUserUnregisterBulk");
                }
            }


		return filePath+fileName;
	}
	
	
}


	@Override
	public ArrayList<String> scanUploadedFile(String filenamePath) throws BTSLBaseException {
		StringTokenizer startparser = null;
		final String method_Name="scanUploadedFile";
		final ArrayList<String> mobileOrIdList = new ArrayList(); // list to store the
		
		final String contentsSize = Constants.getProperty("NO_OF_CONTENTS");
		String tempStr = null;
		String msisdnOrLoginID;
		BufferedReader bufferReader = null;
		 // take out each string from the file & put it in a array list
        if (log.isDebugEnabled()) {
            log.debug(method_Name, "Initializing the fileReader, filepath : " + filenamePath);
        }
        FileReader fileReader=null;
		try {
			fileReader = new FileReader(filenamePath);
		
        if (fileReader != null) {
            bufferReader = new BufferedReader(fileReader);
        } else {
            bufferReader = null;
        }

        try {
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
			            throw new BTSLBaseException(this, method_Name, "user.uploadFileForChUserUnregisterBulk.error.blankline", "uploadFileForChUserUnregisterBulk");
			        }
			        startparser = new StringTokenizer(tempStr, delimiter); // separate
			        // each
			        // string
			        // in
			        // a
			        // line
			        while (startparser.hasMoreTokens()) {
			            msisdnOrLoginID = startparser.nextToken().trim();
			            if (log.isDebugEnabled()) {
			                log.debug(method_Name, "Fatching the MSISDN's from the file " + msisdnOrLoginID);
			            }
			            mobileOrIdList.add(msisdnOrLoginID); // add each string in
			            // the list
			        }

			        // it can not be allowed to process the file if MSISDN's or
			        // Logion ID's are more than the defined Limit
			        if (mobileOrIdList.size() > Integer.parseInt(contentsSize)) {
			            if (log.isDebugEnabled()) {
			                log.debug(method_Name, "File contents size of the file is not valid in constant properties file : " + mobileOrIdList.size());
			            }
			            throw new BTSLBaseException(this, method_Name, "user.uploadFileForChUserUnregisterBulk.error.novalidcontentssize", "uploadFileForChUserUnregisterBulk");
			        }

			        startparser = null;
			        tempStr = null;
			    }
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BTSLBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if(bufferReader!=null)bufferReader.close();
				if(fileReader!=null)fileReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
     return mobileOrIdList;

	}

	
	//@Async
	public BulkOperationResponseVO processBulkList(ArrayList<String> bulkListUsers, String userType, ChannelUserVO loggedInUserVO, String filePathName, String userAction, String srcfileName ) throws BTSLBaseException  {

        BulkOperationResponseVO bulkOperationUploadResp = new BulkOperationResponseVO();
		final String methodName= "ProcessBulkList";
			MComConnectionI mcomCon = new MComConnection();
			Connection con=null;
			
			 try {
				con = mcomCon.getConnection();
			} catch (SQLException e) {
				throw new  BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.TECHNICAL_ERROR);
			}
			
			
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
	        BTSLMessages sendBtslMessage=null;
	        String msisdnOrLoginID;
	        ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
	        ChannelUserVO channelUserVO = null;
	        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	        UserDAO userDAO = new UserDAO();
	        boolean isBalanceFlag = false;
	        final Date currentDate = new Date();
	        childExistList = new ArrayList();
	        final HashMap prepareStatementMap = new HashMap();
	         validList = new ArrayList();
	        PushMessage pushMessage=null;
	        String msisdn;
	        String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(lang, country);
			String message =null;
			try {
        while (bulkListUsers.size() != countStr) {
            msisdnOrLoginID = (String) bulkListUsers.get(countStr);
            countStr++;

            // ** Processing the Login ID's **
            if (userType.equals(PretupsI.LOOKUP_LOGIN_ID)) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Processing starts for Login ID's " + userType);
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
                        
                        if(channelUserVO!=null) {
                        boolean flag =  _geographyDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(), loggedInUserVO.getUserID());
						if (!flag) {
	
							UnregisterChUsersFileProcessLog.log("FILE PROCESSING", loggedInUserVO.getUserID(), msisdnOrLoginID,
									countStr, "Not a valid Login ID", "Fail",
									filePathName + "," + loggedInUserVO.getNetworkID());
							invalidString.append(msisdnOrLoginID);// append the
	
							invalidString.append(delimiter);
							continue;
	
						}
                        
	                        	
                        if (PretupsI.USER_STATUS_DELETE_REQUEST.equals(userAction)) {
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

                                    UserDeletionBL.updateBalNChnlTransfersNItemsC2C(con, fromChannelUserVO, toChannelUserVO, channelUserVO.getUserID(),
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
                    invalidStringFromDao = channelUserWebDAO.deleteOrSuspendChnlUsersInBulkForLoginID(con, msisdnOrLoginID, userAction, childExistList, loggedInUserVO.getUserID(), countStr, prepareStatementMap);// call
                    if (invalidStringFromDao) {
                        invalidString.append(msisdnOrLoginID); // append the
                        // invalid
                        // Login
                        // ID in the
                        // string
                        // invalidString
                        invalidString.append(delimiter);
                        // single line logger entry
                        UnregisterChUsersFileProcessLog.log("FILE PROCESSING", loggedInUserVO.getUserID(), msisdnOrLoginID, countStr, "Not a valid Login ID", "Fail",
                                        filePathName + "," + loggedInUserVO.getNetworkID());
                    } else {
                        validList.add(msisdnOrLoginID.trim()); // insert the
                        // valid
                        // Login ID
                        // in the
                        // validList
                    }
                }
            }
            }
            // ** Processing the MSISDN's **
            else {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Processing starts for MSISDN's " + userType);
                }
                filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdnOrLoginID); // before
                // process
                // MSISDN
                // filter
                // each-one

                // check for valid MSISDN
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    if (log.isDebugEnabled()) {
                        log.debug(methodName, "Not a valid MSISDN " + msisdnOrLoginID);
                    }
                    UnregisterChUsersFileProcessLog.log("FILE PROCESSING", loggedInUserVO.getUserID(), msisdnOrLoginID, countStr, "Not a valid MSISDN", "Fail",
                                    filePathName + "," + loggedInUserVO.getNetworkID());
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
                    if (log.isDebugEnabled()) {
                        log.debug(methodName, "Not Network prefix found " + msisdnOrLoginID);
                    }
                    UnregisterChUsersFileProcessLog.log("FILE PROCESSING", loggedInUserVO.getUserID(), msisdnOrLoginID, countStr, "Not Network prefix found", "Fail",
                                    filePathName + "," + loggedInUserVO.getNetworkID());
                    invalidString.append(msisdnOrLoginID);
                    invalidString.append(delimiter);
                    continue;
                }

                // check network support of the MSISDN
                networkCode = networkPrefixVO.getNetworkCode();
                if (!networkCode.equals(loggedInUserVO.getNetworkID())) {
                    if (log.isDebugEnabled()) {
                        log.debug(methodName, "Not supporting Network" + msisdnOrLoginID);
                    }
                    UnregisterChUsersFileProcessLog.log("FILE PROCESSING", loggedInUserVO.getUserID(), msisdnOrLoginID, countStr, "Not supporting Network", "Fail",
                                    filePathName + "," + loggedInUserVO.getNetworkID());
                    invalidString.append(msisdnOrLoginID);
                    invalidString.append(delimiter);
                    continue;
                }
                
                channelUserVO = channelUserDAO.loadUsersDetailsByMsisdnOrLogin(con, filteredMsisdn,null,PretupsI.STATUS_EQUAL,PretupsI.USER_STATUS_ACTIVE,loggedInUserVO.getNetworkID());
                if(channelUserVO != null){
                	 GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
                     
                     boolean flag =  _geographyDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(), loggedInUserVO.getUserID());
 						if (!flag) {

 						UnregisterChUsersFileProcessLog.log("FILE PROCESSING", loggedInUserVO.getUserID(), msisdnOrLoginID,
 								countStr, "Not a valid MSIDN ID", "Fail",
 								channelUserVO + "," + loggedInUserVO.getNetworkID());
 						invalidString.append(msisdnOrLoginID);// append the

 						invalidString.append(delimiter);
 						continue;

 					}
                }else{
                	UnregisterChUsersFileProcessLog.log("FILE PROCESSING", loggedInUserVO.getUserID(), msisdnOrLoginID,
								countStr, "Not a valid MSIDN", "Fail",
								filePathName + "," + loggedInUserVO.getNetworkID());
						invalidString.append(msisdnOrLoginID);

						invalidString.append(delimiter);
						continue;
                }
                // insert the valid MSISDN in the validMsisdnList
                validList.add(filteredMsisdn);

                // On user deletion action only balance should go
                // owner/operator
                if (PretupsI.USER_STATUS_DELETE_REQUEST.equals(userAction)) {
                    // If login id(user) to be deleted does not exists then
                    // invalidStringFromDao will be true
                    invalidStringFromDao = channelUserWebDAO.getUserMsisdnExists(con, filteredMsisdn, countStr);
                    if (invalidStringFromDao) {
                        invalidString.append(filteredMsisdn);
                        invalidString.append(delimiter);
                    } else {
                       // channelUserVO = channelUserDAO.loadChannelUserDetails(con, filteredMsisdn);
                        if(!SystemPreferences.USR_BTCH_SUS_DEL_APRVL)
                        balanceMoveForDel(con, channelUserVO.getUserID(), channelUserVO, currentDate, userDAO);
                    }
                }

                if (!invalidStringFromDao) {
                    invalidStringFromDao = channelUserWebDAO.deleteOrSuspendChnlUsersInBulkForMsisdn(con, filteredMsisdn, userAction, childExistList, loggedInUserVO.getUserID(), countStr, prepareStatementMap);
                    if (invalidStringFromDao) {
                        invalidString.append(filteredMsisdn);
                        invalidString.append(delimiter);
                    }
                }

            }// else end
            if (invalidStringFromDao) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Rollback the transaction for : " + msisdnOrLoginID);
                }
                con.rollback();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Commit the transaction for : " + msisdnOrLoginID);
                }
                con.commit();

                if (userType.equals(PretupsI.LOOKUP_LOGIN_ID) && channelUserVO != null) {
                    msisdn = channelUserVO.getMsisdn();
                } else {
                    msisdn = filteredMsisdn;
                }
                if (!BTSLUtil.isNullString(msisdn)) {

                    if (userAction.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST))// ||unregisterChUserForm.getDeleteOrSuspendorResume().equals(PretupsI.USER_STATUS_DELETED)
                    {
                        sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_STATUS_SUSPENDED);
                        pushMessage = new PushMessage(msisdn, sendBtslMessage, "", "", locale, loggedInUserVO.getNetworkID());
                        pushMessage.push();
                    } else if (userAction.equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
                        sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
                        pushMessage = new PushMessage(msisdn, sendBtslMessage, "", "", locale, loggedInUserVO.getNetworkID());
                        pushMessage.push();

                    }
                    // 6.4
                    else if (userAction.equals(PretupsI.USER_STATUS_RESUME_REQUEST)) {
                        sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_STATUS_RESUMED);
                        pushMessage = new PushMessage(msisdn, sendBtslMessage, "", "", locale, loggedInUserVO.getNetworkID());
                        pushMessage.push();
                    }
                }
                if (channelUserVO == null) {
                    channelUserVO = ChannelUserVO.getInstance();
                }
                channelUserVO.setModifiedOn(currentDate);
                channelUserVO.setMsisdn(msisdn);
                if (userAction.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
                    channelUserVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
                    ChannelUserLog.log("BLKSUSPCHNLUSR", channelUserVO, loggedInUserVO, true, null);
                } else if (userAction.equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
                    channelUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
                    ChannelUserLog.log("BLKDELCHNLUSR", channelUserVO, loggedInUserVO, true, null);
                } else if (PretupsI.USER_STATUS_RESUME_REQUEST.equals(userAction)) {
                    channelUserVO.setStatus(PretupsI.USER_STATUS_RESUMED);
                    ChannelUserLog.log("BLKRESCHNLUSR", channelUserVO, loggedInUserVO, true, null);
                }

            }

            
            
            if (prepareStatementMap.get("psmtIsExist") != null) {
            	if(!((PreparedStatement) prepareStatementMap.get("psmtIsExist")).isClosed()) {
            		PreparedStatement psmtIsExistVal = (PreparedStatement) prepareStatementMap.get("psmtIsExist");
            		psmtIsExistVal.clearParameters();
            		prepareStatementMap.put("psmtIsExist", psmtIsExistVal);
            	}
            }
            if (prepareStatementMap.get("psmtUserID") != null) {
            	if(!((PreparedStatement) prepareStatementMap.get("psmtUserID")).isClosed()) {
            		PreparedStatement psmtUserIDVal = (PreparedStatement) prepareStatementMap.get("psmtUserID");
            		psmtUserIDVal.clearParameters();
            		prepareStatementMap.put("psmtUserID", psmtUserIDVal);
            	}
            }
            if (prepareStatementMap.get("psmtDelete") != null) {
            	if(!((PreparedStatement) prepareStatementMap.get("psmtDelete")).isClosed()) {
            		PreparedStatement psmtDeleteVal = (PreparedStatement) prepareStatementMap.get("psmtDelete");
            		psmtDeleteVal.clearParameters();
            		prepareStatementMap.put("psmtDelete", psmtDeleteVal);
                
            	}
            }
            if (prepareStatementMap.get("psmtResumeExist") != null) {
            	if(!((PreparedStatement) prepareStatementMap.get("psmtResumeExist")).isClosed()) {
                PreparedStatement psmtResumeExistVal = (PreparedStatement) prepareStatementMap.get("psmtResumeExist");
            	psmtResumeExistVal.clearParameters();
        		prepareStatementMap.put("psmtResumeExist", psmtResumeExistVal);
            	}
            }
            
            if(prepareStatementMap.get("psmtChildExist")!= null) {
            	if(!((PreparedStatement) prepareStatementMap.get("psmtChildExist")).isClosed()) {
                    PreparedStatement psmtChildExist = (PreparedStatement) prepareStatementMap.get("psmtChildExist");
                    psmtChildExist.clearParameters();
            		prepareStatementMap.put("psmtChildExist", psmtChildExist);
                	}
            }
            
            
            
            Thread.sleep(50);
        }
        this.fileName=srcfileName;
        this.loggedInUserVO=loggedInUserVO;
        bulkOperationUploadResp=ChildUsersDeleteProcess(con,forDisplayMsg, invalidStringFromDao,userAction ,userType,invalidString);    
		}catch(BTSLBaseException be) {
	       throw be;		
		}
		catch(Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug("processBulkList", ex);
			}
				
			}finally {
				
				
				try {
		        if (prepareStatementMap.get("psmtIsExist") != null) {
		        	if(!((PreparedStatement) prepareStatementMap.get("psmtIsExist")).isClosed()) {
	            		((PreparedStatement) prepareStatementMap.get("psmtIsExist")).close();
	            	}
	            }
	            if (prepareStatementMap.get("psmtUserID") != null) {
	            	if(!((PreparedStatement) prepareStatementMap.get("psmtUserID")).isClosed()) {
	            		((PreparedStatement) prepareStatementMap.get("psmtUserID")).close();
	            	}
	            }
	            if (prepareStatementMap.get("psmtDelete") != null) {
	            	if(!((PreparedStatement) prepareStatementMap.get("psmtDelete")).isClosed()) {
	            		((PreparedStatement) prepareStatementMap.get("psmtDelete")).close();
	                
	            	}
	            }
	            if (prepareStatementMap.get("psmtResumeExist") != null) {
	            	if(!((PreparedStatement) prepareStatementMap.get("psmtResumeExist")).isClosed()) {
	            		((PreparedStatement) prepareStatementMap.get("psmtResumeExist")).close();
	            	}
	            }
	    
				}catch(Exception ex) {
					if (log.isDebugEnabled()) {
						log.debug("Error occured while closing prepared statements", ex);
					}	
				}
				
				
				if (mcomCon != null) {
					mcomCon.close("BulkOperationUploadServiceImpl#processBulkList");
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug("processBulkList", " Exited ");
				}
				
			}
			 
		
		
		return bulkOperationUploadResp;
	}
	
	
	
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

    public boolean deleteRetry(Connection p_con, String p_userID, String p_deleteOrSuspend, String p_modifiedBy, String p_preStatus, String p_MSISDN, String p_loginID, String userType, int p_countStr,ChannelUserVO loggedinUserVO) {
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

        if (userType.equals(PretupsI.LOOKUP_LOGIN_ID)) {
            MSISDNOrLoginIDForLog = p_loginID;
        } else {
            MSISDNOrLoginIDForLog = p_MSISDN;
        }

        try {
            channelUserWebDAO = new ChannelUserWebDAO();
            isChildFlag = userDAO.isChildUserActive(p_con, p_userID);
            if (isChildFlag) {
                if (log.isDebugEnabled()) {
                    log.debug(METHOD_NAME, "This user has childs down the hierarchy, so can't be deleted " + p_userID);
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
                       LogFactory.printLog(METHOD_NAME, "This user has pending SOS transaction, so can't be deleted " + p_userID, log);
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
                           LogFactory.printLog(METHOD_NAME, "This user has pending Last Recharge transaction, so can't be deleted " + p_userID, log);
                           UnregisterChUsersFileProcessLog
                                           .log("UNSETTLED LAST RECHARGE TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr, "Pending User's Last recharge credit request transaction", "Fail", "");
                       }else {
	                       // Checking O2C Pending transactions
	                       final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
	                       isO2CPendingFlag = transferDAO.isPendingTransactionExist(p_con, p_userID);
	                       if (isO2CPendingFlag) {
	                           if (log.isDebugEnabled()) {
	                               log.debug(METHOD_NAME, "This user has pending transactions, so can't be deleted " + p_userID);
	                           }
	                           UnregisterChUsersFileProcessLog
	                                           .log("IS PENDING TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr, "Pending User's transaction", "Fail", "");
	                       } else {
	                           // Checking Batch FOC Pending transactions - Ved
	                           // 07/08/06
	                           final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
	                           isBatchFOCTxnPendingFlag = batchTransferDAO.isPendingTransactionExist(p_con, p_userID);
	                           if (isBatchFOCTxnPendingFlag) {
	                               if (log.isDebugEnabled()) {
	                                   log.debug(METHOD_NAME, "This user has pending batch foc transactions, so can't be deleted " + p_userID);
	                               }
	                               UnregisterChUsersFileProcessLog.log("IS PENDING BATCH FOC TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr,
	                                               "Pending User's batch foc transaction", "Fail", "");
	                           } else {
	                        	   	//checking batch C2C transaction pending
	                        	   final C2CBatchTransferDAO c2cBatchTransferDAO = new C2CBatchTransferDAO();
	                               boolean isbatchc2cPendingTxn = c2cBatchTransferDAO.isPendingC2CTransactionExist(p_con, loggedinUserVO.getUserID());
	                               if (isbatchc2cPendingTxn) {
		                               if (log.isDebugEnabled()) {
		                                   log.debug(METHOD_NAME, "This user has pending batch c2c transactions, so can't be deleted " + p_userID);
		                               }
		                               UnregisterChUsersFileProcessLog.log("IS PENDING BATCH C2C TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr,
		                                               "Pending User's batch c2c transaction", "Fail", "");
		                           }else{
		                        	   //checking batch O2C transaction
		                        	   final BatchO2CTransferWebDAO o2cBatchTransferDAO = new BatchO2CTransferWebDAO();
		                               boolean isBatchO2CTxnPending = o2cBatchTransferDAO.isPendingO2CTransactionExist(p_con, loggedinUserVO.getUserID());
		                               if(isBatchO2CTxnPending){
		                            	  if (log.isDebugEnabled()) {
			                                   log.debug(METHOD_NAME, "This user has pending batch o2c transactions, so can't be deleted " + p_userID);
			                               }
			                               UnregisterChUsersFileProcessLog.log("IS PENDING BATCH O2C TRANSACTION EXISTS", p_userID, MSISDNOrLoginIDForLog, p_countStr,
			                                               "Pending User's batch o2c transaction", "Fail", "");
		                              }
		                               else{
		                            	   balanceMoveForDel(p_con, p_userID, loggedinUserVO, currentDate, userDAO);
			                               deletedError = channelUserWebDAO.deleteOrSuspendChnlUsers(p_con, p_userID, p_deleteOrSuspend, p_modifiedBy, p_preStatus);
		                               }
		                            }
	                        	  }
	                       	}
	                     }
                       }
               	}
        } catch (Exception ex) {
            log.errorTrace(METHOD_NAME, ex);
        }
        return deletedError;
    }

	
	
	
	
	private BulkOperationResponseVO  ChildUsersDeleteProcess(Connection con, int forDisplayMsg,boolean invalidStringFromDao,String userAction ,String userType,StringBuffer invalidString) throws BTSLBaseException {
        BulkOperationResponseVO bulkOperationUploadResp = new BulkOperationResponseVO();
		final String methodName ="ChildUsersDeleteProcess";
		ChannelUserVO channelUserVO = null;
		String msisdn=null;
		String loginID=null;
		boolean fileMoved=false;
		BTSLMessages btslMessage=null;
		String message=null;
		String invalidStr=null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);

		
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
                invalidStringFromDao = deleteRetry(con, channelUserVO.getUserID(), userAction, loggedInUserVO.getUserID(),
                                ((ChannelUserVO) childExistList.get(i)).getStatus(), ((ChannelUserVO) childExistList.get(i)).getMsisdn(), ((ChannelUserVO) childExistList
                                                .get(i)).getLoginID(), userType, ((ChannelUserVO) childExistList.get(i)).getCategoryVO().getSequenceNumber(),loggedInUserVO);
                msisdn=((ChannelUserVO) childExistList.get(i)).getMsisdn();
				loginID=((ChannelUserVO) childExistList.get(i)).getLoginID();
				if(invalidStringFromDao)
			    {    
					if(userType.equals(PretupsI.LOOKUP_LOGIN_ID))
				    {
						if(!(invalidString.toString().contains(loginID)))
						{
						invalidString.append(((ChannelUserVO)childExistList.get(i)).getLoginID());
					    invalidString.append(delimiter);	
						if(log.isDebugEnabled())
							log.debug("processUploadedFileForUnReg","Rollback the transaction for : "+((ChannelUserVO)childExistList.get(i)).getLoginID());
						}
				    }
					else
					{	
						if(!(invalidString.toString().contains(msisdn))){
						invalidString.append(((ChannelUserVO)childExistList.get(i)).getMsisdn());
					    invalidString.append(delimiter);	
						if(log.isDebugEnabled())
							log.debug("processUploadedFileForUnReg","Rollback the transaction for : "+((ChannelUserVO)childExistList.get(i)).getMsisdn());
						}

					}
				    try {
						con.rollback();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
                        log.error(methodName, "SQL Exception" + e.getMessage());
                        log.errorTrace(methodName, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChildUsersDeleteProcess", "", "", "", "SQL Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
					}
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
                    if (log.isDebugEnabled()) {
                        log.debug(methodName, "Commit the transaction");
                    }
                    try {
						con.commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
                        log.error(methodName, "SQL Exception" + e.getMessage());
                        log.errorTrace(methodName, e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChildUsersDeleteProcess", "", "", "", "SQL Exception:" + e.getMessage());
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
					}
                }
                	 }

            }
        }
        
        // It is for the displaying the message No valid MSISDN's in the
        // file
        if (forDisplayMsg == 0) {
            if (validList.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "No valid MSISDN in the file, size :" + validList.size());
                }
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_VALID_MSISDN_IN_FILE, "uploadFileForChUserUnregisterBulk");
            }
        }
        // It is for the displaying the message No valid Login ID's in the
        // file
        else {
        	 if(validList.isEmpty() && invalidString.toString().isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("processUploadedFileForUnReg", "No valid Login ID in the file, size :" + validList.size());
                }
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_VALID_LOGIN_IN_FILE, "uploadFileForChUserUnregisterBulk");
            }
        }

        filePathAndFileName= Constants.getProperty("UploadFileForUnRegChnlUserPath");
        // make Archive file on the server.
        fileMoved = this.moveFileToArchive(filePathAndFileName, fileName);

        if (!fileMoved) {
            throw new BTSLBaseException(this, "processUploadedFileForUnReg", PretupsErrorCodesI.FILE_CANNOT_BE_MOVED_BKP);
        }

        // if some MSISDN's are invalid then showing the list of these
        // invalid MSISDN's
        if (userAction.equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
            if (invalidString.length() == 0) {
                btslMessage = new BTSLMessages("user.uploadFileForChUserUnregisterBulk.message.success.delete", "uploadFileForChUserUnregisterBulkPage");
                //message = BTSLUtil.getMessage(locale, "user.uploadFileForChUserUnregisterBulk.message.success.delete",  null);
                bulkOperationUploadResp.setMessageCode(PretupsErrorCodesI.BULK_DELETE_SUCCESSFULL);
                message= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_DELETE_SUCCESSFULL,null);
                bulkOperationUploadResp.setStatus(PretupsI.RESPONSE_SUCCESS);
                bulkOperationUploadResp.setMessage(message);
                
            } else {
                if (forDisplayMsg == 0) {
                    invalidStr = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
                    btslMessage = new BTSLMessages("user.uploadFileForChUserUnregisterBulk.message.invalidmsisdnlist.delete", new String[] { invalidStr },
                                    "uploadFileForChUserUnregisterBulkPage");
                  //  message = BTSLUtil.getMessage(locale, "user.uploadFileForChUserUnregisterBulk.message.invalidmsisdnlist.delete", new String[] { invalidStr });
                    bulkOperationUploadResp.setStatus(PretupsI.RESPONSE_FAIL);
                    bulkOperationUploadResp.setMessageCode(PretupsErrorCodesI.DELETE_PARTIAL_SUCCESS_MSISDN);
                    message= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.DELETE_PARTIAL_SUCCESS_MSISDN,new String[] { invalidStr });
                    bulkOperationUploadResp.setMessage(message);
                    
                } else {
                    invalidStr = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
                    btslMessage = new BTSLMessages("user.uploadFileForChUserUnregisterBulk.message.invalidloginidlist.delete", new String[] { invalidStr },
                                    "uploadFileForChUserUnregisterBulkPage");
                   // message = BTSLUtil.getMessage(locale, "user.uploadFileForChUserUnregisterBulk.message.invalidloginidlist.delete", new String[] { invalidStr });
                    bulkOperationUploadResp.setStatus(PretupsI.RESPONSE_FAIL);
                    bulkOperationUploadResp.setMessageCode(PretupsErrorCodesI.DELETE_PARTIAL_SUCCESS_LOGIN_ID);
                    message= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.DELETE_PARTIAL_SUCCESS_LOGIN_ID,new String[] { invalidStr });
                    bulkOperationUploadResp.setMessage(message);
                    
                }
            }
        } else if (PretupsI.USER_STATUS_SUSPEND_REQUEST.equals(userAction)) {
            if (invalidString.length() == 0) {
                btslMessage = new BTSLMessages("user.uploadFileForChUserUnregisterBulk.message.success.suspend", "uploadFileForChUserUnregisterBulkPage");
                //message = BTSLUtil.getMessage(locale, "user.uploadFileForChUserUnregisterBulk.message.success.suspend", null);
                bulkOperationUploadResp.setStatus(PretupsI.RESPONSE_SUCCESS);
                bulkOperationUploadResp.setMessageCode(PretupsErrorCodesI.BULK_SUSPEND_SUCCESSFULL);
                message= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BULK_SUSPEND_SUCCESSFULL,null);
                bulkOperationUploadResp.setMessage(message);
            } else {
                if (forDisplayMsg == 0) {
                    invalidStr = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
                    btslMessage = new BTSLMessages("user.uploadFileForChUserUnregisterBulk.message.invalidmsisdnlist.suspend", new String[] { invalidStr },
                                    "uploadFileForChUserUnregisterBulkPage");
                    
                    //message = BTSLUtil.getMessage(locale, "user.uploadFileForChUserUnregisterBulk.message.invalidmsisdnlist.suspend",  new String[] { invalidStr });
                    message= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.UPLOADFILE_INVALIDMSISDNLIST_SUSPEND,new String[] { invalidStr });
                    bulkOperationUploadResp.setMessageCode(PretupsErrorCodesI.UPLOADFILE_INVALIDMSISDNLIST_SUSPEND);
                    bulkOperationUploadResp.setStatus(PretupsI.RESPONSE_FAIL);
                    bulkOperationUploadResp.setMessage(message);
                } else {
                    invalidStr = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
                    btslMessage = new BTSLMessages("user.uploadFileForChUserUnregisterBulk.message.invalidloginidlist.suspend", new String[] { invalidStr },
                                    "uploadFileForChUserUnregisterBulkPage");
                    //message = BTSLUtil.getMessage(locale, "user.uploadFileForChUserUnregisterBulk.message.invalidloginidlist.suspend",  new String[] { invalidStr });
                    bulkOperationUploadResp.setMessageCode(PretupsErrorCodesI.UPLOADFILE_INVALIDLOGINLIST_SUSPEND);
                    bulkOperationUploadResp.setStatus(PretupsI.RESPONSE_FAIL);
                    message= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.UPLOADFILE_INVALIDLOGINLIST_SUSPEND,new String[] { invalidStr });
                    bulkOperationUploadResp.setMessage(message);
                }
            }
        } else if (PretupsI.USER_STATUS_RESUME_REQUEST.equals(userAction)) {
            if (invalidString.length() == 0) {
                btslMessage = new BTSLMessages("user.uploadFileForChUserUnregisterBulk.message.success.resume", "uploadFileForChUserUnregisterBulkPage");
               // message = BTSLUtil.getMessage(locale, "user.uploadFileForChUserUnregisterBulk.message.success.resume", null);
                bulkOperationUploadResp.setMessageCode(PretupsErrorCodesI.RESUME_SUCCESS);
                bulkOperationUploadResp.setStatus(PretupsI.RESPONSE_SUCCESS);
                message= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.RESUME_SUCCESS,null);
                bulkOperationUploadResp.setMessage(message);
            } else {
                if (forDisplayMsg == 0) {
                    invalidStr = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
                    btslMessage = new BTSLMessages("user.uploadFileForChUserUnregisterBulk.message.invalidmsisdnlist.resume", new String[] { invalidStr },
                                    "uploadFileForChUserUnregisterBulkPage");
                    //message = BTSLUtil.getMessage(locale, "user.uploadFileForChUserUnregisterBulk.message.invalidmsisdnlist.resume",  new String[] { invalidStr });
                    bulkOperationUploadResp.setMessageCode(PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_MSISDN);
                    bulkOperationUploadResp.setStatus(PretupsI.RESPONSE_FAIL);
                    message= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_MSISDN,new String[] { invalidStr });
                    bulkOperationUploadResp.setMessageCode(message);
                } else {
                    invalidStr = invalidString.toString().substring(0, (invalidString.toString().length()) - 1);
                    btslMessage = new BTSLMessages("user.uploadFileForChUserUnregisterBulk.message.invalidloginidlist.resume", new String[] { invalidStr },
                                    "uploadFileForChUserUnregisterBulkPage");
                   // message = BTSLUtil.getMessage(locale, "user.uploadFileForChUserUnregisterBulk.message.invalidloginidlist.resume",  new String[] { invalidStr });
                    bulkOperationUploadResp.setMessageCode(PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_LOGIN_ID);
                    bulkOperationUploadResp.setStatus(PretupsI.RESPONSE_FAIL);
                    message= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.RESUME_PARTIAL_SUCCESS_LOGIN_ID,new String[] { invalidStr });
                    bulkOperationUploadResp.setMessageCode(message);
                }
            }
        }
        return bulkOperationUploadResp;
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
        if (log.isDebugEnabled()) {
        	log.debug(METHOD_NAME, "Entered");
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
		        		throw new BTSLBaseException(this, "balanceMoveForDel", "user.channeluser.deletion.parentsuspended");	
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
		 if (log.isDebugEnabled()) {
			 log.debug(METHOD_NAME, " Exiting");
	        }
	}
	
	
	/**
     * This method is used to make Archive file on the server.
//     * Method moveFileToArchive.
     * 
     * @param p_fileName
     *            String
     * @param p_file
     *            String
     * @return boolean
     */
    private boolean moveFileToArchive(String p_filePathAndFileName, String p_fileName) {
        final String METHOD_NAME = "moveFileToArchive";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, " Entered ");
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
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, " Exiting File Moved=" + flag);
        }
        return flag;
    }

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}