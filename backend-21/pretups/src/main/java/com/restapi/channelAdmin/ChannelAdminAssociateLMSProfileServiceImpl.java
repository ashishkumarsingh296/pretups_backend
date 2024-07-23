package com.restapi.channelAdmin;

import static com.btsl.pretups.common.PretupsErrorCodesI.DIR_NOT_CREATED;
import static com.btsl.pretups.common.PretupsErrorCodesI.EMPTY_FILE_NAME;
import static com.btsl.pretups.common.PretupsErrorCodesI.INVALID_UPLOADFILE_MSG_UNSUCCESSUPLOAD;
import static com.btsl.pretups.common.PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_ERROR_FILENOTUPLOADED;
import static com.btsl.pretups.common.PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_ERROR_NORECORDINFILE;
import static com.btsl.pretups.common.PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_ERROR_PATHNOTDEFINED;
import static com.btsl.pretups.common.PretupsErrorCodesI.MESSAGE_MANAGEMENT_MULTIPLE_DATA_SHEET_NOT_ALLOWED;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.query.oracle.ActivationBonusLMSWebOracleQry;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.lms.businesslogic.LoyalityVO;
import com.btsl.pretups.loyalty.transaction.LoyaltyDAO;
import com.btsl.pretups.loyalty.transaction.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetLMSVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.messages.businesslogic.MessageArgumentVO;
import com.btsl.pretups.messages.businesslogic.MessagesDAO;
import com.btsl.pretups.messages.businesslogic.MessagesVO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelRW;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageUploadRequestVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessagesBulkResponseVO;
import com.restapi.superadmin.networkmanagement.responseVO.NetworkListResponseVO;
import com.restapi.superadmin.networkmanagement.service.NetworkManagmentServiceImpl;
import com.web.pretups.loyaltymgmt.businesslogic.ActivationBonusLMSWebDAO;
import com.web.pretups.loyaltymgmt.businesslogic.ActivationBonusLMSWebQry;
import com.web.pretups.loyaltymgmt.businesslogic.LMSExcelBL;

import jakarta.servlet.http.HttpServletResponse;

@Service("ChannelAdminAssociateLMSProfileService")
public class ChannelAdminAssociateLMSProfileServiceImpl implements ChannelAdminAssociateLMSProfileService {

	public static final Log LOG = LogFactory.getLog(ChannelAdminAssociateLMSProfileServiceImpl.class.getName());
	public static final String classname = "ChannelAdminAssociateLMSProfileServiceImpl";
	//private ActivationBonusLMSWebQry activationBonusLMSWebQry = (ActivationBonusLMSWebQry)ObjectProducer.getObject(QueryConstants.ACTIVATION_BONUS_LMS_WEB_QRY, QueryConstants.QUERY_PRODUCER);	
    /**
     * This method load the LMS Profile list
     *
     * @param con
     * @param loginId
     * @param responseSwag
     * @return ProfileListResponseVO
     * @throws BTSLBaseException , SQLException
     */
	@Override
	public ProfileListResponseVO viewProfileList(Connection con, String loginId, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {
		final String methodName = "viewProfileList";
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered:=" + methodName);
		}
		
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ProfileListResponseVO response = new ProfileListResponseVO();
		ActivationBonusLMSWebDAO bonusDAO = new ActivationBonusLMSWebDAO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		ProfileSetLMSVO profileSetLMSVO = new ProfileSetLMSVO();
		ArrayList profileList = new ArrayList();
		
		try {
			
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			profileList = bonusDAO.loadpromotionList(con,userVO.getNetworkID());
		    //change error code
		    if (profileList.isEmpty()) {
				throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.DIV_FAIL, 0, null);
			} else {
				response.setProfileList(profileList);
				response.setStatus((HttpStatus.SC_OK));
				//change status code
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DIV_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.DIV_SUCCESS);
			}
			
		}catch(BTSLBaseException baseException) {
			LOG.error(methodName, "Exception:e=" + baseException);
			LOG.errorTrace(methodName, baseException);
			if (!BTSLUtil.isNullString(baseException.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, baseException.getMessage(), null);
				response.setMessageCode(baseException.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}catch(Exception exception) {
			LOG.error(methodName, "Exception:e=" + exception);
			LOG.errorTrace(methodName, exception);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.DIV_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DIV_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Exited := Profile List " + profileList);
		}
		
		return response;
	}
	
	/**
     * This method download Profile list
     *
     * @param con
     * @param categoryCode,domainCode
     * @param gradeCode,geographyCode
     * @param locale,loginID,responseSwagger
     * @return FileAssocationResponseVO
     * @throws BTSLBaseException , SQLException
     */
	@Override
    public FileAssocationResponseVO downloadFileAssocation(Connection con,String categoryCode,String domainCode,String gradeCode,String geographyCode, Locale locale, String loginID, HttpServletResponse responseSwagger) throws Exception, BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("downloadFileAssocation", "Entered");
        }
        final String methodname = "downloadFileAssocation";
        String filePath = null;
        String fileName = null;
        FileAssocationResponseVO response = new FileAssocationResponseVO();

        Map<String, String> msisdnUserIDMap = null;
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO = null;
        int localeListSize = 0;
        String localeArr[] = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        String controlGroup = "Y";
		try {
			controlGroup = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
		} catch (RuntimeException e1) {
			controlGroup = "Y";
		}
        try {
            userDAO = new UserDAO();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();

            ArrayList dataList = loadUserForLMSAssociation(con,domainCode,geographyCode,categoryCode,userVO.getUserID(),gradeCode,msisdnUserIDMap );
            localeListSize = dataList.size();
            // The writing process is not for the file template..
            String fileArr[][] = null;
            fileName = Constants.getProperty("DownloadBatchFOCUserListFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".xls";
            try {
                filePath = Constants.getProperty("DownloadBatchFOCUserListFilePath");
                try {
                    final File fileDir = new File(filePath);
                    if (!fileDir.isDirectory()) {
                        fileDir.mkdirs();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodname, e);
                    log.error(methodname, "Exception" + e.getMessage());
                    throw new BTSLBaseException(this, methodname, DIR_NOT_CREATED);
                }

            ExcelRW excelRW = new ExcelRW();
            int cols =  4;
            int rows = localeListSize + 1;
            fileArr = new String[rows][cols]; // ROW-COL
            localeArr = new String[localeListSize];

            fileArr[0][0] = RestAPIStringParser.getMessage(locale, "lmsprofile.xlsheading.label.msisdn", null);
            fileArr[0][1] = RestAPIStringParser.getMessage(locale, "lmsprofile.xlsheading.label.associate.currently", null);
            fileArr[0][2] = RestAPIStringParser.getMessage(locale, "lmsprofile.xlsheading.label.associate.required", null);
            if (PretupsI.YES.equals(controlGroup)) {
            	fileArr[0][3] = RestAPIStringParser.getMessage(locale, "lmsprofile.xlsheading.label.controlgroup.required", null);
    		}
            
            fileArr = this.convertTo2dArray(fileArr, rows, controlGroup,dataList);
            excelRW.writeMessagesToExcel(ExcelFileIDI.MESSAGES_LIST, fileArr, locale, filePath + "" + fileName, localeArr);
            File fileNew = new File(filePath + "" + fileName);
            byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            response.setFileAttachment(encodedString);
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS, null);
            response.setMessage(resmsg);
            response.setFileName(fileName.toString());
            response.setFileType("xls");
            response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS);
        } catch(Exception e){
        	throw e;
        }
        finally {
            if (log.isDebugEnabled()) {
                log.debug(methodname, "Exiting");
            }
        }}catch (Exception e) {
        	throw e;
		}
        return response;
    }

	/**
     * This method load Users for LMS Association
     *
     * @param con
     * @param domainCode,categoryCode
     * @param gradeCode,geographyCode
     * @param p_user_id,p_gradeCode,p_msisdnUserIDMap
     * @return ArrayList
     * @throws BTSLBaseException , SQLException
     */
      public ArrayList loadUserForLMSAssociation(Connection p_con, String p_domainCode, String p_geographyCode,
              String p_category_code, String p_user_id, String p_gradeCode, Map<String, String> p_msisdnUserIDMap) throws BTSLBaseException
        {
    	  
    	  final String methodName = "loadUserForLMSAssociation";
    	  PreparedStatement pstmt = null;
          ResultSet rs = null;
          ArrayList batchList = new ArrayList();

          ChannelUserVO channelUserVO = null;
          final UserPhoneVO userPhoneVO = null;
          int sheetNO = 0;
          boolean written = false;
          final LMSExcelBL associateLMSAction = new LMSExcelBL();
          
          try {
              ActivationBonusLMSWebQry activationBonusLMSWebQry = (ActivationBonusLMSWebQry)ObjectProducer.getObject(QueryConstants.ACTIVATION_BONUS_LMS_WEB_QRY, QueryConstants.QUERY_PRODUCER);
              pstmt = activationBonusLMSWebQry.loadUserForLMSAssociationQry(p_con, p_domainCode, p_geographyCode,p_category_code, p_user_id, p_gradeCode);
              rs = pstmt.executeQuery();
              final String password = null;
              while (rs.next()) {
                  channelUserVO = new ChannelUserVO();
                  channelUserVO.setUserID(rs.getString("user_id"));
                  channelUserVO.setMsisdn(rs.getString("msisdn"));
                  channelUserVO.setUserProfileID(rs.getString("set_name"));
                  channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
                  channelUserVO.setAssType(PretupsI.YES);
                  batchList.add(channelUserVO);
              }
          } catch (SQLException sqe) {
              LOG.error(methodName, "SQLException : " + sqe);
              LOG.errorTrace(methodName, sqe);
              throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
          } catch (Exception ex) {
              LOG.error(methodName, "Exception : " + ex);
              LOG.errorTrace(methodName, ex);
              throw new BTSLBaseException(this, methodName, "error.general.processing");
          } finally {
              try {
                  if (rs != null) {
                      rs.close();
                  }
              } catch (Exception e) {
                  LOG.errorTrace(methodName, e);
              }
              try {
                  if (pstmt != null) {
                      pstmt.close();
                  }
              } catch (Exception e) {
                  LOG.errorTrace(methodName, e);
              }
              if (LOG.isDebugEnabled()) {
                  LOG.debug(methodName, "Exiting: ");
              }
          }

          return batchList;
        }
      
  	/**
       * This method convert Arraylist To 2D Array
       *
       * @param p_fileArr
       * @param p_rows
       * @param p_controlGroup
       * @param dataList
       * @return String
       * @throws BTSLBaseException
       */
      
      private String[][] convertTo2dArray(String[][] p_fileArr,int p_rows,String p_controlGroup, ArrayList dataList) throws BTSLBaseException {
          if (log.isDebugEnabled()) {
              log.debug("convertTo2dArray", "Entered");
          }
          final String methodName = "convertTo2dArray";
          try {
              if (log.isDebugEnabled()) {
                  log.debug(methodName, "Entered p_fileArr length=" + p_fileArr.length + "dataList size=" + dataList.size());
              }
              String key = null;
              MessagesVO messagesVO = null;
              int rows = 0;
              int cols;
              StringBuffer arguments = null;
              cols = p_fileArr[0].length;
              ChannelUserVO channelUserVO = null;
              
                     for (int i = 0;i < dataList.size();i++) {
                    	  channelUserVO = (ChannelUserVO) dataList.get(i);

                          rows++;
                          if (rows >= p_rows) {
                              break;
                          }
                          cols = 0;
                          p_fileArr[rows][cols++] = channelUserVO.getMsisdn();
                          p_fileArr[rows][cols++] = channelUserVO.getUserProfileID();
                          p_fileArr[rows][cols++] = "";
                          if (PretupsI.YES.equals(p_controlGroup)) {
      						if (PretupsI.YES.equalsIgnoreCase(channelUserVO.getControlGroup())) {
      							p_fileArr[rows][cols++] = channelUserVO.getControlGroup();// Y or N (for control group to be associated or not)
      						} else {
      							p_fileArr[rows][cols++] = "";
      						}
      					}

                      }
          } catch (Exception e) {
              log.error("convertTo2dArray", "Exceptin:e=" + e);
              log.errorTrace(methodName, e);
              throw new BTSLBaseException(this, methodName, "MESSAGES_MESSAGESMANAGEMENT_ERROR_CONVERT_2D_ARRAY");
          } finally {
              if (log.isDebugEnabled()) {
                  log.debug(methodName, "Exited p_fileArr=" + p_fileArr);
              }
          }
          return p_fileArr;
      }
      
      /**
       * This method is used to upload and Associate LMS profile
       *
       * @param con
       * @param locale,loginID
       * @param responseSwagger,FileAssociationUploadRequestVO
       * @param setID,categoryCode
       * @param gradeCode,geographyCode
       * @return FileAssocationResponseVO
       * @throws BTSLBaseException , Exception
       */
    
      @Override
      public FileAssocationResponseVO uploadFileAssocation(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, FileAssociationUploadRequestVO request,String setID,String categoryCode,String geographyCode,String gradeCode) throws Exception, BTSLBaseException {
          final String methodname = "uploadFileAssocation";
    	  if (log.isDebugEnabled()) {
              log.debug(methodname, "Entered");
          }
          FileAssocationResponseVO response = new FileAssocationResponseVO();
          HashMap<String, String> map = new HashMap<String, String>();
          String[][] excelArr = null;
          ListValueVO errorVO = null;
          HashMap<String, String> fileDetailsMap = null;
          ReadGenericFileUtil fileUtil = null;
          ErrorMap errorMap = new ErrorMap();
          ArrayList requiredList = null;
          ArrayList notRequiredLIST = null;
          LoyaltyVO loyaltyVO = null;
          ChannelUserVO channelUserVO;
          ActivationBonusLMSWebDAO activationBonusLMSWebDAO = null;
          final String[] arg = new String[2];

          try {
              String fileStr = request.getFileName();
              final String filePathAndFileName = (fileStr + ".xls");
              final File file = new File(fileStr);
              boolean message = BTSLUtil.isValideFileName(fileStr);
              UserDAO userDAO = new UserDAO();
              UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
              fileDetailsMap = new HashMap<String, String>();
              fileUtil = new ReadGenericFileUtil();
              fileDetailsMap.put(PretupsI.FILE_NAME, request.getFileName());
              fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, request.getFileAttachment());
              fileDetailsMap.put(PretupsI.FILE_TYPE, request.getFileType());
              // if not a valid file name then throw exception
              if (!message) {
                  throw new BTSLBaseException(classname, methodname, INVALID_UPLOADFILE_MSG_UNSUCCESSUPLOAD);
              }
              String dir = Constants.getProperty("UploadMessageListFilePath");

              // Validate Message list file path which should present in
              if (BTSLUtil.isNullString(dir)) {
                  throw new BTSLBaseException(classname, methodname, MESSAGES_MESSAGESMANAGEMENT_ERROR_PATHNOTDEFINED);
              }
              if (BTSLUtil.isNullorEmpty(request.getFileName())) {
                  throw new BTSLBaseException(classname, methodname, EMPTY_FILE_NAME);
              }
              if (request.getFileType().isEmpty() || request.getFileType().isBlank() || !PretupsI.FILE_TYPE_PATTERN.matcher(request.getFileType()).matches()) {
                  throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.INVALID_FILE_TYPES);
              }
              if (request.getFileAttachment().isEmpty() || request.getFileAttachment().isBlank()) {
                  throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.INVALID_FILE_ATTACHMENT);
              }
              String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLS);
              String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE");
              if (BTSLUtil.isNullString(fileSize)) {
                  fileSize = String.valueOf(0);
              }
              
              Map<String, String> msisdnUserIdMap = new HashMap<String, String>();
              activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
              //msisdnUserIdMap = activationBonusLMSWebDAO.loadMapForLMSAssociation(con, geographyCode, categoryCode, userVO.getUserID(),gradeCode, msisdnUserIdMap);
              
              // upload file to server
              boolean isFileUploaded = BTSLUtil.uploadFileToServer(fileDetailsMap, dir, contentType, Long.parseLong(fileSize));

              if (isFileUploaded) {
                  ExcelRW excelRW = new ExcelRW();
                  try {
                      excelArr = excelRW.readMultipleExcel(ExcelFileIDI.MESSAGES_INITIATE, dir + filePathAndFileName, true, 1, map);
                  } catch (Exception e) {
                      log.errorTrace(methodname, e);
                      throw new BTSLBaseException(classname, methodname, MESSAGE_MANAGEMENT_MULTIPLE_DATA_SHEET_NOT_ALLOWED);
                  }
                  // If there is no data in XLS file
                  if (excelArr.length == 1) {
                      throw new BTSLBaseException(classname, methodname, PretupsErrorCodesI.N0_RECORD);
                  }
                  ArrayList dataList = this.conver2DToArrayList(excelArr);
                  ArrayList listValueVO = this.validateAndUploadData(dataList, setID);
                  boolean isAssociationRequiredFlag = false;
                  requiredList = new ArrayList();
                  notRequiredLIST = new ArrayList();
                  String controlGroupRequired = "Y";
                  try {
                      controlGroupRequired = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
                  } catch (RuntimeException e1) {
                  	log.error(methodname, "RuntimeException" + e1);
          			 log.errorTrace(methodname,e1);
                      controlGroupRequired = "Y";
                  }

                  if (dataList != null && !dataList.isEmpty()) {
                	 for(int i = 0; i < dataList.size(); i++){
                		 loyaltyVO = new LoyaltyVO();
                         channelUserVO = new ChannelUserVO();
                         loyaltyVO = (LoyaltyVO) dataList.get(i);
                         if (!BTSLUtil.isNullString(loyaltyVO.getReciverMsisdn())) {
                        	 if (msisdnUserIdMap != null) {
                                 if (msisdnUserIdMap.containsKey(loyaltyVO.getReciverMsisdn())) {
                                     channelUserVO.setUserID(msisdnUserIdMap.get(loyaltyVO.getReciverMsisdn()));
                                 }
                             }
                             if (loyaltyVO.getTxnStatus().equals("Y") && !loyaltyVO.getErrorFlag()) {
                                 isAssociationRequiredFlag = true;
                                 channelUserVO.setAssType(loyaltyVO.getTxnStatus());
                                 channelUserVO.setControlGroup(loyaltyVO.getControlGroup());
                                 requiredList.add(channelUserVO);
                             } else if ("N".equalsIgnoreCase(loyaltyVO.getTxnStatus()) && !loyaltyVO.getErrorFlag()) {
                                 channelUserVO.setAssType(loyaltyVO.getTxnStatus());
                                 channelUserVO.setControlGroup(loyaltyVO.getControlGroup());
                                 requiredList.add(channelUserVO);
                             } else {
                                 channelUserVO.setMessage(loyaltyVO.getComments());
                                 notRequiredLIST.add(channelUserVO);
                             }
                         }
                	  }
                	 response.setErrorList(notRequiredLIST);
                     response.setErrorFlag(Boolean.TRUE.toString());
                     response.setTotalRecords(excelArr.length - 1);
                     response.setFailCount(notRequiredLIST.size());
                     response.setFileType(PretupsI.FILE_TYPE_XLS_);
                     
                     int errorListSize = notRequiredLIST.size();
                     for (int i = 0, j = errorListSize; i < j; i++) {
                    	 channelUserVO = (ChannelUserVO) notRequiredLIST.get(i);
                    	 if(!BTSLUtil.isNullString(channelUserVO.getMessage())) {
                    		 RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                             ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                             MasterErrorList masterErrorList = new MasterErrorList();
                             String msg = channelUserVO.getMessage();
                             masterErrorList.setErrorMsg(msg);
                             masterErrorLists.add(masterErrorList);
                             rowErrorMsgLists.setMasterErrorList(masterErrorLists);
                             rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(channelUserVO.getMessage())));
                             rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
                             if (errorMap.getRowErrorMsgLists() == null)
                                 errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
                             (errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                    	 }
                         
                     }
                     writeErrorDataInFile(locale,notRequiredLIST, fileStr, dir, response);
                  }else {
                      log.error("process", " Error in data validation and uploading process");
                      throw new BTSLBaseException(methodname, "process", "lms.associate.profile.error.processing.file");
                  }
                  if(BTSLUtil.isNullOrEmptyList(notRequiredLIST)) {
                	  int[] retAssociateDeassociateCount = null;
                	  activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
                      retAssociateDeassociateCount = activationBonusLMSWebDAO.addLmsProfileMapping(requiredList, setID, categoryCode, geographyCode, gradeCode, userVO.getUserID(), userVO.getNetworkID(), isAssociationRequiredFlag);
                      
                      if (PretupsI.YES.equals(controlGroupRequired)) {
                          final Map<String, Double> countOfUsersInTargetControlGroup = new ChannelUserDAO().countOfUsersInTargetControlGroup(con, setID);
                          if (countOfUsersInTargetControlGroup != null) {
                              final double targetCount = countOfUsersInTargetControlGroup.get("target_count");
			        			final double controlCount = (double)countOfUsersInTargetControlGroup.get("control_count");
                              if (log.isDebugEnabled()) {
			    					log.debug(methodname,"Finally After operation perform for selected lms profile = "+setID+", targetCount = "+targetCount+",controlCount = "+controlCount);
                              }
			        	if(isAssociationRequiredFlag){
                              if (targetCount == 0 && controlCount >0) {
                              	con.rollback();
                                String resmsg = RestAPIStringParser.getMessage(locale,"user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup", null);
                                response.setStatus(PretupsI.RESPONSE_SUCCESS);
                                response.setMessage(resmsg);
                                response.setMessageCode(PretupsErrorCodesI.LMS_ASSOCIATION_SUCCESS_MSG);
                                responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
                              }
                      }
			        	else {			        		
				        	if(targetCount==0 && controlCount >0){
				        		con.rollback();
				        		String resmsg = RestAPIStringParser.getMessage(locale,"user.associatechanneluser.targetgroup.oneuserstillexistsintocontrolgroup", null);
                                response.setStatus(PretupsI.RESPONSE_SUCCESS);
                                response.setMessage(resmsg);
                                response.setMessageCode(PretupsErrorCodesI.LMS_ASSOCIATION_SUCCESS_MSG);
                                responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
				        	}
			        	 }
                        }
                        }
                        con.commit();
                        arg[0] = String.valueOf(retAssociateDeassociateCount[0]);
                        arg[1] = String.valueOf(retAssociateDeassociateCount[1]);

                        if (retAssociateDeassociateCount.length > 0) {
                			String resmsg = RestAPIStringParser.getMessage(locale,"lms.associate.deassociate.profile.success",
                					new String[] { arg[0],arg[1] });
                            response.setStatus(PretupsI.RESPONSE_SUCCESS);
                            response.setMessage(resmsg);
                            response.setMessageCode(PretupsErrorCodesI.LMS_ASSOCIATION_SUCCESS_MSG);
                            responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
                        } else {
                        	File f = new File(fileStr);
                            if (f.exists()) {
                                try {
                                    boolean isDeleted = f.delete();
                                    if (isDeleted) {
                                        log.debug(methodname, "File deleted successfully");
                                    }
                                } catch (Exception e) {
                                    log.error(methodname, "Error in uploaded file" + f.getName() + " as file validations are failed Exception::" + e);
                                }
                            }
                            String resmsg = RestAPIStringParser.getMessage(locale,"lms.associate.profile.fail", null);
                            response.setStatus(PretupsI.RESPONSE_SUCCESS);
                            response.setMessage(resmsg);
                            response.setMessageCode(PretupsErrorCodesI.LMS_ASSOCIATION_SUCCESS_MSG);
                            responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
                        }
                  }
              } else {
                  throw new BTSLBaseException(this, methodname, PretupsErrorCodesI.FILE_NOT_UPLOADED);
              }
          } finally {
              if (log.isDebugEnabled()) {
                  log.debug(methodname, "Exiting");
              }
          }
          return response;
      }   
      
      /**
       * This method process the uploaded file and validate
       *
       * @param p_uploadedData
       * @param setId
       * @return ArrayList
       * @throws BTSLBaseException
       */
      
      public ArrayList validateAndUploadData(ArrayList p_uploadedData, String setId) throws BTSLBaseException {
          final String METHOD_NAME = "validateAndUploadData";
          if (log.isDebugEnabled()) {
              log.debug(METHOD_NAME, "Entered:  p_uploadedData size:" + p_uploadedData.size());
          }
          ArrayList dataList = null;
          LoyaltyVO dataProcessVO = null;
          ActivationBonusLMSWebDAO activationBonusLMSWebDAO = null;
          ChannelUserDAO channelUserDAO = null;
          ChannelUserVO chVO = null;
          Connection con = null;MComConnectionI mcomCon = null;
          String controlGroupRequired = "Y";
          try {
              controlGroupRequired = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
          } catch (RuntimeException e1) {
          	log.error(METHOD_NAME, "RuntimeException" + e1);
  			log.errorTrace(METHOD_NAME,e1);
              controlGroupRequired = "Y";
          }
          String errorComment = null;
          try {
              channelUserDAO = new ChannelUserDAO();
              mcomCon = new MComConnection();con=mcomCon.getConnection();
              double targetCount = 0d;
              double controlCount = 0d;
              int retval4Association = 0;
              int retval4deAssociation = 0;
              int retvalTargetControl = 0;
              int targetExcelCount = 0;
              int controlExcelCount = 0;
              final int totalUsersinExcel = p_uploadedData.size();
              boolean isValidForAss = false;
              activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
  			  int targetExcelCountDeassociate = 0;
              final Map<String, Double> countOfUsersInTargetControlGroup = channelUserDAO.countOfUsersInTargetControlGroup(con, setId);
              if (countOfUsersInTargetControlGroup != null) {
                  controlCount = countOfUsersInTargetControlGroup.get("control_count");
                  targetCount = countOfUsersInTargetControlGroup.get("target_count");
                  retval4Association = Double.compare(targetCount, 0d);
                  retval4deAssociation = Double.compare(controlCount, 0d);
                  retvalTargetControl = Double.compare(targetCount, controlCount);
                  if (log.isDebugEnabled()) {
                      log.debug(METHOD_NAME,
                                      "control_count = " + controlCount + ", target_count = " + targetCount + ", retval4Association = " + retval4Association + ", retval4deAssociation = " + retval4deAssociation + ", retvalTargetControl = " + retvalTargetControl);
                  }
              }
              dataList = new ArrayList();
              for (int i = 0; i < p_uploadedData.size(); i++) {
                  dataProcessVO = (LoyaltyVO) p_uploadedData.get(i);
                  if (PretupsI.YES.equals(controlGroupRequired)) {
                      if (PretupsI.YES.equals(dataProcessVO.getTxnStatus()) && PretupsI.NO.equalsIgnoreCase(dataProcessVO.getControlGroup())) {
                          targetExcelCount++;
  					} else if(PretupsI.NO.equals(dataProcessVO.getTxnStatus()) && PretupsI.NO.equalsIgnoreCase(dataProcessVO.getControlGroup())){
  						targetExcelCountDeassociate++;
                      }
                      if (PretupsI.YES.equals(dataProcessVO.getControlGroup()) && PretupsI.YES.equalsIgnoreCase(dataProcessVO.getTxnStatus())) {
                          controlExcelCount++;
                      }
                  }
                  // Check for MSISDN
                  if (BTSLUtil.isNullString(dataProcessVO.getReciverMsisdn())) {
                      dataProcessVO.setErrorFlag(true);
                      dataProcessVO.setComments("Please enter MSISDN");
                  } else if (!BTSLUtil.isValidMSISDN(dataProcessVO.getReciverMsisdn())) {
                      dataProcessVO.setErrorFlag(true);
                      dataProcessVO.setComments("MSISDN should be Numeric value");

                  } else if ((!("Y").equals(dataProcessVO.getTxnStatus()) && !("N").equals(dataProcessVO.getTxnStatus()) && !BTSLUtil.isNullString(dataProcessVO.getTxnStatus()))) {
                      dataProcessVO.setErrorFlag(true);
                      dataProcessVO.setComments("Invalid Association Type");
                  }

                  else if (!BTSLUtil.isNumeric(dataProcessVO.getLoyaltyPoint())) {
                      if (!BTSLUtil.isNullString(dataProcessVO.getComments())) {
                          errorComment = dataProcessVO.getComments() + " ,Loyalty points should be Numeric value";
                          dataProcessVO.setComments(errorComment);
                          dataProcessVO.setErrorFlag(true);
                      } else {
                          dataProcessVO.setComments(" ,Loyalty points should be Numeric value");
                          dataProcessVO.setErrorFlag(true);
                      }
                  }
                  // Handling of controlled profile
                  else {
                      if (BTSLUtil.isNullString(dataProcessVO.getTxnStatus())) {
                          if (BTSLUtil.isNullString(dataProcessVO.getComments())) {
                              errorComment = "The value of association is missing";
                          } else {
                              errorComment = dataProcessVO.getComments() + " ,The value of association is missing";
                          }
                          dataProcessVO.setComments(errorComment);
                          dataProcessVO.setErrorFlag(true);
                      } else if (BTSLUtil.isNullString(dataProcessVO.getControlGroup())) {
                          if (BTSLUtil.isNullString(dataProcessVO.getComments())) {
                              errorComment = "The value of control group is missing";
                          } else {
                              errorComment = dataProcessVO.getComments() + " ,The value of control group is missing";
                          }
                          dataProcessVO.setComments(errorComment);
                          dataProcessVO.setErrorFlag(true);
                      } else if (PretupsI.NO.equalsIgnoreCase(dataProcessVO.getTxnStatus()) && (PretupsI.YES.equalsIgnoreCase(dataProcessVO.getControlGroup()))) {
                          if (BTSLUtil.isNullString(dataProcessVO.getComments())) {
                              errorComment = "Control group is not possible during de-association of profile";
                          } else {
                              errorComment = dataProcessVO.getComments() + " ,Control group is not possible during de-association of profile";
                          }
                          dataProcessVO.setComments(errorComment);
                          dataProcessVO.setErrorFlag(true);
                      } else if (PretupsI.YES.equalsIgnoreCase(dataProcessVO.getTxnStatus()) && (PretupsI.YES.equalsIgnoreCase(dataProcessVO.getControlGroup()))) {
                          if (activationBonusLMSWebDAO.isProfileActive(dataProcessVO.getReciverMsisdn(), setId)) {
                              if (BTSLUtil.isNullString(dataProcessVO.getComments()) || "null".equalsIgnoreCase(dataProcessVO.getComments())) {
                                  errorComment = "This operation is not allowed, User assocition is not allowded into control group profile as profile is active";
                              } else {
                                  errorComment = dataProcessVO.getComments() + " ,This operation is not allowed, User assocition is not allowded into control group profile as profile is active";
                              }
                              dataProcessVO.setComments(errorComment);
                              dataProcessVO.setErrorFlag(true);
                          } else {
                              if (PretupsI.YES.equalsIgnoreCase(dataProcessVO.getTxnStatus())) {
                                  /*
                                   * if(isControlledProfileAlreadyAssociated(
                                   * dataProcessVO.getReciverMsisdn())){
                                   * if(BTSLUtil.isNullString(dataProcessVO.
                                   * getComments()) ||
                                   * "null".equalsIgnoreCase(dataProcessVO
                                   * .getComments()))
                                   * errorComment =
                                   * " This operation is not allowed, User is already associated with control group profile"
                                   * ;
                                   * else
                                   * errorComment = dataProcessVO.getComments() +
                                   * " ,This operation is not allowed, User is already associated with control group profile"
                                   * ;
                                   * dataProcessVO.setComments(errorComment);
                                   * dataProcessVO.setErrorFlag(true);
                                   * }else {
                                   * dataProcessVO.setErrorFlag(false);
                                   * }
                                   */
                              }
                          }
                      }
  				if(PretupsI.YES.equalsIgnoreCase(controlGroupRequired)){
                      if (!BTSLUtil.isNullString(dataProcessVO.getTxnStatus()) && !BTSLUtil.isNullString(dataProcessVO.getControlGroup())) {
                          chVO = channelUserDAO.loadChannelUserDetails(con, dataProcessVO.getReciverMsisdn());
                          if (chVO != null) {
                              double targetUserCount = 0d;
                              double controlUserCount = 0d;
                              final Map<String, Double> countOfUsersInTargetControl = channelUserDAO.countOfUsersInTargetControlGroup(con, chVO.getLmsProfile());
                              if (countOfUsersInTargetControl != null) {
                                  controlUserCount = countOfUsersInTargetControl.get("control_count");
                                  targetUserCount = countOfUsersInTargetControl.get("target_count");
  								if(BTSLUtil.isNullString(chVO.getControlGroup())){
  									chVO.setControlGroup(PretupsI.NO);
  								}
                                  isValidForAss = checkForAssociation(dataProcessVO, chVO.getLmsProfile(), setId, controlUserCount, targetUserCount, chVO.getControlGroup());
                              }
                          }
                      }
  				}
                  }
                  dataList.add(dataProcessVO);
              }
              if (log.isDebugEnabled()) {
                  log.debug(METHOD_NAME,
                                  "retval4Association = " + retval4Association + ", retval4deAssociation = " + retval4deAssociation + ", targetExcelCount = " + targetExcelCount + ", controlExcelCount = " + controlExcelCount + ", targetCount = " + targetCount + ", isValidForAss = " + isValidForAss+", targetExcelCountDeassociate = "+targetExcelCountDeassociate);
              }
              if (PretupsI.YES.equals(controlGroupRequired)) {
                  if (retval4Association == 0 && retval4deAssociation == 0) {
  					if(targetCount == 0 && controlCount == 0 && totalUsersinExcel == targetExcelCountDeassociate && isValidForAss){
  						log.debug(METHOD_NAME,"Validation is OK as it is being considered for de-association from expired profile.");
  					} else if(targetCount == 0 && controlExcelCount>=1 && totalUsersinExcel >=1 && isValidForAss){
  						log.debug(METHOD_NAME,"Validation is OK as it is being considered for association/de-association.");
  					} else if (targetCount == 0 && targetExcelCount < 1) {
  						if(targetExcelCountDeassociate>=1)
                          throw new BTSLBaseException("user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup");
                      } else if (targetCount == 0 && totalUsersinExcel == controlExcelCount) {
                          throw new BTSLBaseException("user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup");
                      }
                      else if (!isValidForAss) {
                          throw new BTSLBaseException("user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup");
                      }
                  } else {
                      if (targetCount <= 0 && controlExcelCount == p_uploadedData.size()) {
                          throw new BTSLBaseException("user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup");
                      }
                  }
              }
          } catch (Exception ex) {
              if (ex.getMessage().equals("user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup")) {
                  throw new BTSLBaseException("user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup");
              } else {
                  log.errorTrace(METHOD_NAME, ex);
              }
          } finally {
  			if (mcomCon != null) {
  				mcomCon.close("AssociateLMSAction#validateAndUploadData");
  				mcomCon = null;
  			}
              if (log.isDebugEnabled()) {
                  log.debug(METHOD_NAME, "Exiting: dataList Size=" + dataList.size());
              }
          }
          return dataList;
      }
      
      /**
       * This method checks is users are already associated or not
       *
       * @param dataProcessVO
       * @param curAssoLmsProf,assoLMSProf
       * @param controlUserCount,targetUserCount,controlGroup
       * @return boolean
       */
      
      public boolean checkForAssociation(LoyaltyVO dataProcessVO, String curAssoLmsProf, String assoLMSProf, double controlUserCount, double targetUserCount, String controlGroup) {
          String errorComment = null;
          boolean isValidForAss = true;
  		final String METHOD_NAME="checkForAssociation";
  		if (log.isDebugEnabled()) {
          	log.debug(METHOD_NAME,"MSISDN = "+dataProcessVO.getReciverMsisdn()+", curAssoLmsProf = "+curAssoLmsProf+", assoLMSProf = "+assoLMSProf+", controlUserCount = "+controlUserCount+", targetUserCount = "+targetUserCount+", controlGroup = "+controlGroup);
          }
  		if(curAssoLmsProf != null && !BTSLUtil.isNullString(curAssoLmsProf)){
              if (PretupsI.YES.equals(dataProcessVO.getTxnStatus()) && PretupsI.NO.equals(dataProcessVO.getControlGroup())) {
                  isValidForAss = true;
                  if (!curAssoLmsProf.equals(assoLMSProf)) {
                      if (controlUserCount >= 1 && targetUserCount == 1) {
                          if (PretupsI.NO.equalsIgnoreCase(controlGroup)) {
                              errorComment = "User association/deassocaition is not possible, Atleast one user has to be there in the target group with the current LMS profile";
                              dataProcessVO.setComments(errorComment);
                              dataProcessVO.setErrorFlag(true);
                              isValidForAss = false;
                          }
                      }
                  }
  			} else if(PretupsI.NO.equals(dataProcessVO.getTxnStatus()) && PretupsI.NO.equals(dataProcessVO.getControlGroup())){
  				isValidForAss=true;
  				if(curAssoLmsProf.equals(assoLMSProf)){
  					if(controlUserCount==1 && targetUserCount ==1){
  						if(PretupsI.NO.equalsIgnoreCase(controlGroup)) {
  							errorComment = "User deassocaition is not possible from target group as still one user exists into the control group";
  							dataProcessVO.setComments(errorComment);
  							dataProcessVO.setErrorFlag(true);
  							isValidForAss = false;
  						}
  					}
  				}
  			}
  		}
  		if (log.isDebugEnabled()) {
          	log.debug(METHOD_NAME,"isValidForAss = "+isValidForAss);
          }
          return isValidForAss;
      }

      
      /**
       * This method is used write Errors in file
       *
       * @param locale
       * @param errorList,_fileName
       * @param filePath,FileAssociationUploadRequestVO
       * @return void
       * @throws Exception
       */
      
      public void writeErrorDataInFile(Locale locale, ArrayList errorList, String _fileName, String filePath, FileAssocationResponseVO response) throws  Exception {

          final String METHOD_NAME = "writeErrorDataInFile";
          Writer out = null;
          File newFile = null;
          File newFile1 = null;
          String fileHeader = null;
          Date date = new Date();
          if (log.isDebugEnabled())
              log.debug(METHOD_NAME, "Entered");
          try {
              File fileDir = new File(filePath);
              if (!fileDir.isDirectory())
                  fileDir.mkdirs();

              String _fileName1 = filePath + _fileName + "_"
                      + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
              newFile1 = new File(filePath);
              if (!newFile1.isDirectory())
                  newFile1.mkdirs();
              String absolutefileName = _fileName1;

              fileHeader= RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_LINENO_LABEL, null) + "," + RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_MESSAGE_LABEL, null);
              newFile = new File(absolutefileName);
              out = new OutputStreamWriter(new FileOutputStream(newFile));
              out.write(RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.BATCH_COMM_ERROR_LOG, null) + _fileName + "\n\n");
              out.write(fileHeader + "\n");
              int lengthOfErroList =  errorList.size();
              for(int i =0; i < lengthOfErroList;i++) {
            	  ChannelUserVO channelUserVO = (ChannelUserVO) errorList.get(i);
            	  out.write(channelUserVO.getMessage() + ",");
                  out.write(",");
                  out.write("\n");
              }
              out.close();
              File error = new File(absolutefileName);
              byte[] fileContent = FileUtils.readFileToByteArray(error);
              String encodedString = Base64.getEncoder().encodeToString(fileContent);
              response.setFileAttachment(encodedString);
              response.setFileName(
                      _fileName + "_" + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv");
              response.setFileType("csv");
          } finally {

              try {
                  if (out != null) {
                      out.close();
                  }
              } catch (Exception e) {

                  log.error(METHOD_NAME, "Exception" + e.getMessage());
                  log.errorTrace(METHOD_NAME, e);
                  throw e;
              }
              if (log.isDebugEnabled()) {
                  log.debug(METHOD_NAME, "Exiting... ");
              }
          }
      }
      
    	/**
       * This method convert 2D Array To ArrayList
       *
       * @param excelArr
       * @return ArrayList
       */
      public ArrayList conver2DToArrayList(String[][] excelArr) {
    	  final String METHOD_NAME = "conver2DToArrayList";
          if (log.isDebugEnabled()) {
              log.debug(METHOD_NAME, " Entered");
          }
          int skipHeader = 0;
    	  ArrayList<LoyaltyVO> dataList = new ArrayList<>();
    	  LoyaltyVO loyaltyVO = null;
    	  String controlGroup = "Y";
          try {
              controlGroup = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
          } catch (RuntimeException e1) {
          	log.error(METHOD_NAME, "RuntimeException" + e1);
  			 log.errorTrace(METHOD_NAME,e1);
              controlGroup = "Y";
          }

    	  for(String[] arr : excelArr) {
    		  if(skipHeader == 0) {
    			  skipHeader++;
    			  continue;
    		  }
    		  loyaltyVO = new LoyaltyVO();
    		  loyaltyVO.setReciverMsisdn(arr[0]); // MSISDN
              loyaltyVO.setTxnStatus(arr[2]); // Association/De-association
              // Required
              if (PretupsI.YES.equals(controlGroup)) {
                  if (BTSLUtil.isNullString(arr[3])) {
                      arr[3] = PretupsI.NO;
                  }
                  loyaltyVO.setControlGroup(arr[3]); // Control group
                  // Required
              } else {
                  loyaltyVO.setControlGroup(PretupsI.NO);
              }
              dataList.add(loyaltyVO);

    		  
    	  }
    	  return dataList;
      }

      /**
       * This method is used to Associate and De-Associate LMS profile
       *
       * @param con
       * @param locale,loginID
       * @param responseSwagger,FileAssociationUploadRequestVO
       * @param setID,domainCode,categoryCode
       * @param gradeCode,geographyCode
       * @return FileAssocationResponseVO
       * @throws BTSLBaseException
       */
      
      @Override
      public FileAssocationResponseVO addAssociatePromotions(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, FileAssociationUploadRequestVO request,String setID,String domainCode,String categoryCode,String geographyCode,String gradeCode) throws Exception, BTSLBaseException {
    	  final String methodname = "addAssociatePromotions";
    	  if (log.isDebugEnabled()) {
              log.debug(methodname, "Entered");
          }
          FileAssocationResponseVO response = new FileAssocationResponseVO();
  		  MComConnectionI mcomCon = null;
	  	  int insertcount1 = 0;
	      int[] retAssociateDeassociateCount = null;

          try {
        	  
        	  final ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
              LoyaltyDAO loyaltyDAO= new LoyaltyDAO();
        	  final String[] arg = new String[1];
              UserDAO userDAO = new UserDAO();
              UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
              mcomCon = new MComConnection();con=mcomCon.getConnection();

              final Map<String, String> msisdnUserIDMap = new HashMap<String, String>();
                 
              ArrayList dataList = loadUserForLMSAssociation(con,domainCode,geographyCode,categoryCode,userVO.getUserID(),gradeCode,msisdnUserIDMap );
             
		      final Iterator dataIterator = dataList.iterator();
		      boolean isAssociationFound = false;
		      ChannelUserVO channelUserVO = null;
		      while (dataIterator.hasNext()) {
		          channelUserVO = (ChannelUserVO) dataIterator.next();
		          if (activationBonusLMSWebDAO.isControlledProfileAlreadyAssociated(channelUserVO.getMsisdn())) {
		              if (log.isDebugEnabled()) {
		                  log.debug(methodname, "The user having the MSISDN " + channelUserVO.getMsisdn() + " is already with associated with control group profile.");
		              }
		              final String arr[] = { channelUserVO.getMsisdn() };
		              String resmsg = RestAPIStringParser.getMessage(locale,
		            		  "user.associatechanneluser.updatecontrolledalreadyassociatedmessageforall ",
		  					new String[] {arr.toString()});
                      response.setStatus(PretupsI.RESPONSE_SUCCESS);
                      response.setMessage(resmsg);
		              isAssociationFound = true;
		              break;
		
		          }
		          
		      }
		      
		      if (!isAssociationFound){
                  retAssociateDeassociateCount = activationBonusLMSWebDAO.addLmsProfileMapping(dataList,setID,categoryCode,geographyCode,gradeCode,userVO.getUserID(),userVO.getNetworkID(),true);
              }
              insertcount1 = retAssociateDeassociateCount[0];

              arg[0] = String.valueOf(insertcount1);
              if (insertcount1 > 0) {
      			  String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.LMS_ASSOCIATION_SUCCESS_MSG,new String[] { arg[0] });
                  response.setStatus(PretupsI.RESPONSE_SUCCESS);
                  response.setMessage(resmsg);
                  response.setMessageCode(PretupsErrorCodesI.LMS_ASSOCIATION_SUCCESS_MSG);
                  responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
              } else {
            	 String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.LMS_ASSOCIATION_FAIL_MSG,null);
                 response.setStatus(PretupsI.RESPONSE_SUCCESS);
                 response.setMessage(resmsg);
                 response.setMessageCode(PretupsErrorCodesI.LMS_ASSOCIATION_FAIL_MSG);
                 responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
              }

          }
          catch (Exception e) {
        	  String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.LMS_ASSOCIATION_FAIL_MSG,null);
              response.setStatus(PretupsI.RESPONSE_SUCCESS);
              response.setMessage(resmsg);
              response.setMessageCode(PretupsErrorCodesI.LMS_ASSOCIATION_FAIL_MSG);
              responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
          } finally {
  			if (mcomCon != null) {
  				mcomCon.close(methodname);
  				mcomCon = null;
  			}
              if (log.isDebugEnabled()) {
                  log.debug(methodname, "Exiting");
              }
          }

          return response;
      }

}
