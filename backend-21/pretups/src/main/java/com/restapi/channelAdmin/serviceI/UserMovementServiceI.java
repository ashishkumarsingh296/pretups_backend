package com.restapi.channelAdmin.serviceI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import com.btsl.common.FileWriteUtil;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListController;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserCategoryVO;
import com.btsl.pretups.user.businesslogic.UserGeoDomainVO;
import com.btsl.pretups.user.businesslogic.UserMessageVO;
import com.btsl.pretups.user.businesslogic.UserMigrationDAO;
import com.btsl.pretups.user.businesslogic.UserMigrationVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.requestVO.UserMigrationRequestVO;
import com.restapi.channelAdmin.responseVO.UserMigrationResponseVO;
import com.restapi.channelAdmin.service.UserMovementService;
import com.restapi.user.service.FileDownloadResponse;
import com.web.pretups.user.web.UserMoveCreationExcelRWPOI;

@Service
public class UserMovementServiceI implements UserMovementService {
	protected static final Log LOG = LogFactory.getLog(CAC2STransferReversalServiceI.class.getName());
	private static final String PATH_FOR="selectDomainForInitiate";
	private String messageR="downloadfile.error.dirnotcreated";
	private HashMap<String, HashMap<String, UserMessageVO>> profileGradeMap = null;
	private HashMap<String, String> migrationDetailMap = null;
	private HashMap<String, UserCategoryVO> catCodeMap = null;
	private HashMap<String, UserGeoDomainVO> userGeoDomCodeMap = null;
	private String filepathtemp;
	@Override
	public FileDownloadResponse getUserMovementTemplate(OAuthUser oAuthUser, HttpServletResponse responseSwag,
			Locale locale,String domainCode)throws SQLException {
		final String methodName = "getUserMovementTemplate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        Connection con = null;
        MComConnectionI mcomCon = null;
        HashMap<String, Object> masterDataMap = null;
        UserMigrationDAO userMigDAO = null;
        String domainName = null;
       try {
    	   mcomCon = new MComConnection();
           con=mcomCon.getConnection();
           final UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
           String filePath = Constants.getProperty("DownloadUserMigrationPath");
           makeDirectory(methodName, filePath);
           final String fileName = domainCode + Constants.getProperty("DownloadUserMigrationFileNamePrefix") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xlsx";
           final ArrayList domainList = new DomainDAO().loadCategoryDomainList(con);
           ListValueVO listValueVO;
           if (domainList != null && domainList.size() == 1) {
   		    listValueVO = (ListValueVO) domainList.get(0);
//   		    theForm.setDomainCode(listValueVO.getValue());
   		    domainName = listValueVO.getLabel();
   		} else if (domainList != null && domainList.size() > 1) {
   			listValueVO = BTSLUtil.getOptionDesc(domainCode, domainList);
            domainName = listValueVO.getLabel();
        }
           userMigDAO = new UserMigrationDAO();
           masterDataMap = new <String, Object>HashMap();

           // For Master Data Creation
           masterDataMap.put(PretupsI.BATCH_USR_CREATED_BY, userVO.getUserName());
           masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_NAME, userVO.getGeographicalAreaList().get(0).getGraphDomainName());
           masterDataMap.put(PretupsI.BATCH_USR_DOMAIN_NAME, domainName);
           masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_LIST, userMigDAO.loadMasterGeographyList(con, userVO.getUserID()));
           masterDataMap.put(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST, userMigDAO.loadMasterCategoryList(con));
           final UserMoveCreationExcelRWPOI excelRW = new UserMoveCreationExcelRWPOI();
           excelRW.writeExcel(ExcelFileIDI.MIG_USER_INIT, masterDataMap,null, locale,
                           filePath + fileName);
       	File fileNew = new File(filePath + fileName);
		byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		String file1 = fileNew.getName();
		 fileDownloadResponse.setFileattachment(encodedString);
		 fileDownloadResponse.setFileType("xlsx");
		 fileDownloadResponse.setFileName(file1);
		 fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
		 fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
		 String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
		 fileDownloadResponse.setMessage(resmsg);
		 responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
		
       }catch (BTSLBaseException be) {
    	   if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
					|| be.getMessage().equalsIgnoreCase("241018")) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
			fileDownloadResponse.setMessageCode(be.getMessage());
			fileDownloadResponse.setMessage(resmsg);
       }catch (Exception ex) {
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
			LOG.errorTrace(methodName, ex);
			LOG.error(methodName, "Exception = " + ex.getMessage());
		}finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if(con != null)
				con.close();
		}
       
		return fileDownloadResponse;
	}
	
	
	
	private void makeDirectory(final String methodName, String filePath)
			throws BTSLBaseException {
		try {
			boolean flag;
		    final File fileDir = new File(filePath);
		    if (!fileDir.isDirectory()) {
		        flag=fileDir.mkdirs();
		        
		        if(!flag){
			    	throw new BTSLBaseException(this, methodName, messageR, PATH_FOR);
			    }
		    }
		} catch (Exception e) {
		    LOG.errorTrace(methodName, e);
		    LOG.error(methodName, " Exception" + e.getMessage());
		    throw new BTSLBaseException(this, methodName, messageR, PATH_FOR);
		}
	}



	@Override
	public UserMigrationResponseVO confirmUserMigration(Locale locale, UserMigrationRequestVO requestVO,
			HttpServletResponse responseSwag, OAuthUser oAuthUser,String domainCode) throws SQLException {
		final String methodName = "confirmUserMigration";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        ErrorMap errorMap = new ErrorMap();
        UserMigrationResponseVO response = new UserMigrationResponseVO();
    	ArrayList<MasterErrorList> inputValidations=null;
    	
         LinkedHashMap<String, List<String>> bulkDataMap;
         ArrayList<ListValueVO> fileErrorList = null;
 		ArrayList<ListValueVO> usermigrationVOList = null;
 		String filteredMsisdn = null;
 		
 		String filteredParentMsisdn = null;
 		String msisdnPrefix = null;
 		ListValueVO errorVO = null;
 		UserMigrationDAO userMigrationDAO = null;
		ArrayList<UserMigrationVO> userMigrationList = new ArrayList<UserMigrationVO>();
		boolean fileValidationErrorExists = false;
		NetworkPrefixVO networkPrefixVO = null;
		UserMigrationVO userMigrationVO = new UserMigrationVO();
        try {
        	mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final UserVO userVO = new UserDAO().loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
            
        	inputValidations = new ArrayList<MasterErrorList>();
        	ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
			HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_TYPE1, requestVO.getFileType());
			fileDetailsMap.put(PretupsI.FILE_NAME,requestVO.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFileAttachment());
			fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, "UserMig");
			final String dir = Constants.getProperty("DownloadUserMigrationPath"); // Upload
			if (BTSLUtil.isNullString(dir)) {
				throw new BTSLBaseException(this, methodName,
						"user.migration.uploadAndValidateMigUserFile.error.pathnotdefined", PATH_FOR);
			}
			final String fileName =requestVO.getFileName();// accessing
			final boolean message = BTSLUtil.isValideFileName(fileName);
			if(!message) {
				throw new BTSLBaseException(this.getClass().getName(),methodName,PretupsErrorCodesI.INVALID_FILE_NAME1,0,null );	
			}
			List<String> listfinal;
			validateFileDetailsMap(fileDetailsMap);
			listfinal = fileUtil.uploadAndReadGenericFileForUserMovement(fileDetailsMap, 4, errorMap);
			int totalRecords = listfinal.size()-1;
			

			
			fileErrorList = new <ListValueVO>ArrayList();
			usermigrationVOList = new <ListValueVO>ArrayList();

			
			userMigrationDAO = new UserMigrationDAO();
			profileGradeMap = new <String, HashMap<String, UserMessageVO>>HashMap();
			migrationDetailMap = new <String, String>HashMap();

			catCodeMap = userMigrationDAO.loadCategoryMap(con, profileGradeMap, userVO.getNetworkID());
			userGeoDomCodeMap = userMigrationDAO.loadGeoDomainCode(con);

			for(int i=1;i<listfinal.size();i++) {
				fileValidationErrorExists = false;
				String temp = listfinal.get(i);
				List<String> value = Arrays.asList(temp.split("\\s*,\\s*"));
				if(value.size()==0)
					continue;
				// From User Msisdn Validation
				
				String cellValue0 = value.get(0);
				if (!BTSLUtil.isNullString(cellValue0)) 
					// From
					// user
					// MSISDN
					{
						cellValue0 = cellValue0.trim();
						filteredParentMsisdn = PretupsBL.getFilteredMSISDN(cellValue0);
						cellValue0 = filteredParentMsisdn;
						if (!BTSLUtil.isValidMSISDN(filteredParentMsisdn)) {
							errorVO = new ListValueVO(cellValue0, String.valueOf(i),
									PretupsRestUtil.getMessageString("user.migration.processuploadedfile.error.frmusermsisdnisinvalid"));
							errorVO.setIDValue("user.migration.processuploadedfile.error.frmusermsisdnisinvalid");
							fileErrorList.add(errorVO);
							fileValidationErrorExists = true;
							continue;
						}
					}
				
				//To Parent Msisdn Validation
				String cellValue1 = value.get(1);
				if (BTSLUtil.isNullString(cellValue1)) {
					errorVO = new ListValueVO(cellValue0, String.valueOf(i),
							PretupsRestUtil.getMessageString("user.migration.processuploadedfile.error.msisdnreqforsmserr",
									new String[] { cellValue1 }));
					errorVO.setIDValue("user.migration.processuploadedfile.error.msisdnreqforsmserr");
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				} else {
					try {
						cellValue1 = cellValue1.trim();
						filteredMsisdn = PretupsBL.getFilteredMSISDN(cellValue1);
						cellValue1 = filteredMsisdn;
					} catch (Exception ee) {
						LOG.errorTrace(methodName, ee);
						errorVO = new ListValueVO(cellValue0, String.valueOf(i),
								PretupsRestUtil.getMessageString("user.migration.processuploadedfile.error.toparentmsisdnisinvalid"));
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
						errorVO = new ListValueVO(cellValue0, String.valueOf(i),
								PretupsRestUtil.getMessageString("user.migration.xls.error.toparmsisdnisinvalid"));
						errorVO.setIDValue("user.migration.xls.error.toparmsisdnisinvalid");
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					// Check for network prefix
					msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
					// the
					// prefix
					// of
					// the
					// MSISDN
					networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
					if (networkPrefixVO == null
							|| (!networkPrefixVO.getNetworkCode().equals(userVO.getNetworkID()))) {
						errorVO = new ListValueVO(cellValue0, String.valueOf(i),
								PretupsRestUtil.getMessageString("user.migration.processuploadedfile.error.nonetworkprefixfound",
										new String[] { filteredMsisdn }));
						errorVO.setIDValue("user.migration.processuploadedfile.error.nonetworkprefixfound");
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
					
			}
				// To user geographical domain code validation starts
				// here.
				String cellValue2 = value.get(2);
				if (BTSLUtil.isNullString(cellValue2)) {
					errorVO = new ListValueVO(cellValue0, String.valueOf(i),
							PretupsRestUtil.getMessageString("user.migration.processuploadedfile.error.tousergeocode"));
					errorVO.setIDValue("user.migration.processuploadedfile.error.tousergeocode");
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				} else {
					// To user geographical domain code must be
					// validated from the master sheet.
					if (!userGeoDomCodeMap.containsKey(cellValue2)) {
						errorVO = new ListValueVO(cellValue0, String.valueOf(i),
								PretupsRestUtil.getMessageString("user.migration.xls.error.tousrgeocodeinvalid"));
						errorVO.setIDValue("user.migration.xls.error.tousrgeocodeinvalid");
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
				} 
				// To user category code validation starts here.
				String cellValue3 = value.get(3);
				if (BTSLUtil.isNullString(cellValue3)) {
					errorVO = new ListValueVO(cellValue0, String.valueOf(i),
							PretupsRestUtil.getMessageString("user.migration.processuploadedfile.error.catcodeinvalid"));
					errorVO.setIDValue("user.migration.processuploadedfile.error.catcodeinvalid");
					fileErrorList.add(errorVO);
					fileValidationErrorExists = true;
					continue;
				} else {
					// To user category code must be validated from the
					// master sheet.
					if (!catCodeMap.containsKey(cellValue3)) {
						errorVO = new ListValueVO(cellValue0, String.valueOf(i),
								PretupsRestUtil.getMessageString("user.migration.xls.error.tousrcatcodeinvalid"));
						fileErrorList.add(errorVO);
						fileValidationErrorExists = true;
						continue;
					}
				} 
				
				if (!fileValidationErrorExists) { 
					userMigrationVO = new UserMigrationVO();
					userMigrationVO.setRecordNumber(String.valueOf(i));
					userMigrationVO.setDomainID(domainCode);
					userMigrationVO.setNetworkID(userVO.getNetworkID());
					userMigrationVO.setFromUserMsisdn(cellValue0);// From User MSISDN
					userMigrationVO.setToParentMsisdn(cellValue1);// To Parent MSISDN
					userMigrationVO.setToUserGeoCode(cellValue2);// To user geographical code
					userMigrationVO.setToUserCatCode(cellValue3);// To user category code.
					userMigrationList.add(userMigrationVO);

					// bring data to the database
					
				}
		}    
			
			int validRecords = userMigrationList.size();
			if (userMigrationList != null) {
				UpdateRecordsInDB(con,userMigrationList, fileErrorList, userMigrationDAO,errorMap,response);
			}
			int successfulRecords = totalRecords - fileErrorList.size();
			 if (fileErrorList != null && !fileErrorList.isEmpty()) {
				 int errorListSize = fileErrorList.size();
				 for (int i = 0, j = errorListSize; i < j; i++) {
	                	errorVO =  fileErrorList.get(i);
			            if(!BTSLUtil.isNullString(errorVO.getOtherInfo2()))
	                    {
			            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
			            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
			            	MasterErrorList masterErrorList = new MasterErrorList();
							masterErrorList.setErrorCode(errorVO.getIDValue());
							String msg = errorVO.getOtherInfo2();
							masterErrorList.setErrorMsg(msg);
							masterErrorLists.add(masterErrorList);
							rowErrorMsgLists.setMasterErrorList(masterErrorLists);
							rowErrorMsgLists.setRowValue("Line" + String.valueOf(Long.parseLong(errorVO.getOtherInfo())+5));
							rowErrorMsgLists.setRowName(errorVO.getCodeName());
							if(errorMap.getRowErrorMsgLists() == null)
								errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
							(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
	                    }
	                }
			 	}
				 writeFileForResponse(response, errorMap);
				 filedelete();
				 if(successfulRecords !=  totalRecords && successfulRecords != 0) {
					 response.setStatus(Integer.parseInt(PretupsErrorCodesI.PARTIAL_SUCCESS));
				 	 response.setErrorMap(errorMap);
					  String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.USER_MIG_PARTIAL_SUCESS,new String[]{String.valueOf(successfulRecords), String.valueOf(totalRecords)});
				 	 response.setMessage(msg);
				 	 responseSwag.setStatus(HttpStatus.SC_OK);}
				 else if(successfulRecords == totalRecords) {
					 response.setStatus(PretupsI.RESPONSE_SUCCESS);
					 response.setErrorMap(errorMap);
					 String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.USER_MIG_PARTIAL_SUCESS,new String[]{String.valueOf(successfulRecords), String.valueOf(totalRecords)});

					 response.setMessage(msg);
					 responseSwag.setStatus(HttpStatus.SC_OK);
				 }
				 else {
					 response.setStatus(PretupsI.RESPONSE_FAIL);
					 response.setErrorMap(errorMap);
					 String msg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.USER_MIG_PARTIAL_SUCESS,new String[]{String.valueOf(successfulRecords), String.valueOf(totalRecords)});
					 response.setMessage(msg);
					 responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					 
				 }
			 
       }
        catch (Exception e) {
			LOG.error(methodName, "Exception:e=" + e);
			LOG.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					e.getMessage(), null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CHANGE_STATUS_NOT_PERFORMED);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
        finally {
        	if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if(con != null)
				con.close();
	
        }
		return response;
	}
	
	



	private void UpdateRecordsInDB(Connection con, ArrayList<UserMigrationVO> userMigrationList,
			ArrayList<ListValueVO> fileErrorList, UserMigrationDAO userMigrationDAO,ErrorMap errorMap,UserMigrationResponseVO response) {
		ArrayList<UserMigrationVO> channelUserList = userMigrationList;
		
		try {
		ArrayList successUserList = userMigrationDAO.validateFromUsersMigration(con, channelUserList,
				fileErrorList);
		
		final ArrayList<UserMigrationVO> npusers = new <UserMigrationVO>ArrayList();
		if (!successUserList.isEmpty()) {
			UserMigrationVO marknpuserMigrationVO = null;
			int successUserListSize = successUserList.size();
			for (int j = 0; j < successUserListSize; j++) {
				marknpuserMigrationVO = (UserMigrationVO) successUserList.get(j);
				userMigrationDAO.markNpUsers(con, marknpuserMigrationVO.getFromUserID(),
						marknpuserMigrationVO.getFromUserMsisdn()); // Marking
				// Users
				// for
				// NP
				// status.
				npusers.add(marknpuserMigrationVO);
			}

			final List<ListValueVO> errorListMig = migrateUsers(con, successUserList);// Calling
			con.commit();
			// Migration
			// Process
			if (errorListMig != null && !errorListMig.isEmpty()) {
					fileErrorList.addAll(errorListMig);
				}
			
		}
		}catch (Exception e) {
			LOG.error("", "Exception:e=" + e);
			LOG.errorTrace("", e);
			
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
	public void validateFileName(String fileName) throws BTSLBaseException {
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			throw new BTSLBaseException(this, "validateFileName", PretupsErrorCodesI.INVALID_FILE_NAME1,
					PretupsI.RESPONSE_FAIL, null);
		}
	}

	public List<ListValueVO> migrateUsers(Connection pCon, List<UserMigrationVO> pUserMigrationList)
			throws BTSLBaseException {
		final String methodName = "migrateUsers";
		LOG.debug(methodName, "", "Entered with User List Size=" + pUserMigrationList.size());

		ArrayList<ListValueVO> errorListAfterMig = new <ListValueVO>ArrayList();
		final ArrayList<UserMigrationVO> finalMigList = (ArrayList<UserMigrationVO>) pUserMigrationList;
		UserMigrationDAO userMigDAO = null;

		try {
			userMigDAO = new UserMigrationDAO();
			errorListAfterMig = userMigDAO.userMigrationProcess(pCon, finalMigList, catCodeMap, userGeoDomCodeMap,
					profileGradeMap, migrationDetailMap);
		} catch (BTSLBaseException be) {
			LOG.debug(methodName, "", "BTSLBaseException error be: " + be);
			LOG.errorTrace(methodName, be);
			throw be;
		} finally {
			LOG.debug(methodName, "", "Exiting with error List size=" + errorListAfterMig.size());
		}
		return errorListAfterMig;
	}
	  private void writeFileForResponse(UserMigrationResponseVO response, ErrorMap errorMap)throws BTSLBaseException, IOException{
	    	if(errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
	    		return ;
	    	List<List<String>> rows = new ArrayList<>();
			for(int i=0;i<errorMap.getRowErrorMsgLists().size();i++)
			{
				RowErrorMsgLists rowErrorMsgList = errorMap.getRowErrorMsgLists().get(i);
				for(int col= 0; col< rowErrorMsgList.getMasterErrorList().size(); col++)
				{
					MasterErrorList masterErrorList=rowErrorMsgList.getMasterErrorList().get(col);
				    rows.add(( Arrays.asList(rowErrorMsgList.getRowValue(),rowErrorMsgList.getRowName(), masterErrorList.getErrorMsg())));
				}
				
			}
			String filePathCons = Constants.getProperty("DownloadUserMigrationPath");
			C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
			c2CFileUploadApiControllerObject.validateFilePathCons(filePathCons);
			
			String filePathConstemp = filePathCons + "temp/";        
			c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
			

			filepathtemp = filePathConstemp ;   

			String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
			writeFileCSV(rows, filepathtemp + logErrorFilename + ".csv");
			File error =new File(filepathtemp+logErrorFilename+ ".csv");
			byte[] fileContent = FileUtils.readFileToByteArray(error);
	   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
	   		response.setFileAttachment(encodedString);
	   		response.setFileName(logErrorFilename+".csv");
	    }
		
	  public void writeFileCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
		  try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
			  csvWriter.append("Line number");
			  csvWriter.append(Constants.getProperty("FILE_SEPARATOR_O2C"));
			  csvWriter.append("Mobile number/login ID");
			  csvWriter.append(Constants.getProperty("FILE_SEPARATOR_O2C"));
			  csvWriter.append("Reason");
			  csvWriter.append("\n");

			  for (List<String> rowData : listBook) {
				  csvWriter.append(String.join(Constants.getProperty("FILE_SEPARATOR_O2C"), rowData));
				  csvWriter.append("\n");
			  }

		  }
	  }

	  private void filedelete() {
			if(!BTSLUtil.isNullString(filepathtemp))
			{File file = new File(filepathtemp);
			if (file.delete()) {
				LOG.debug("filedelete", "******** Method filedelete :: Got exception and deleted the file");
			}
			}
		}



	@Override
	public FileDownloadResponse getNpUserList(OAuthUser oAuthUser, HttpServletResponse responseSwag, Locale locale,String domain) throws SQLException {
		final String methodName = "getNpUserList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        Connection con = null;
        MComConnectionI mcomCon = null;
        ArrayList<String> npList = new ArrayList<String>();
        HashMap<String, Object> masterDataMap = null;
        UserMigrationDAO userMigDAO = null;
        String filePath;
        String fileName;
        String fileArr[][] = null;
        String finalFileArr[][] = null;
        String fileExt =(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
        UserDAO userDAO = new UserDAO();
        UserVO userVO = new UserVO();
        final Date currDate = new Date();
        try {
        	mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            userMigDAO = new UserMigrationDAO();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
            masterDataMap = new <String, Object>HashMap();
            masterDataMap =    userMigDAO.getNpUsersRevamp(con, null, null,domain);
            
            try {
            	filePath = Constants.getProperty("DownloadUserMigrationPath");
            	 try {
                     final File fileDir = new File(filePath);
                     if (!fileDir.isDirectory()) {
                         fileDir.mkdirs();
                     }
                 } catch (Exception e) {
                     LOG.errorTrace(methodName, e);
                     LOG.error(methodName, "Exception" + e.getMessage());
                     throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "initiateBatchO2CWithdraw");

                 }
            	 fileName = Constants.getProperty("DownloadUserMigrationFileNamePrefix") + 
                 		BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".xls";
                 final String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORO2C;
                 final int cols= 12;
                 final int rows = masterDataMap.size()+5;
                 fileArr = new String[rows][cols];
                 fileArr[0][0] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.npheading",null);
                 fileArr[1][0] = RestAPIStringParser.getMessage(locale,PretupsI.BATCH_USR_CREATED_BY,null);
                 fileArr[1][1] = userVO.getUserName();
                 fileArr[3][0] = RestAPIStringParser.getMessage(locale,PretupsI.BATCH_USR_GEOGRAPHY_NAME,null);
                 fileArr[3][1] = userVO.getGeographicalAreaList().get(0).getGraphDomainName();
                 fileArr[2][0] = RestAPIStringParser.getMessage(locale,PretupsI.DOMAIN_NAME_L,null);
                 fileArr[2][1] = domain;
                 fileArr[4][0] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.usermsisdn",null);
                 fileArr[4][1] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.username",null);
                 fileArr[4][2] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.userdomain",null);
                 fileArr[4][3] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.usercategory",null);
                 fileArr[4][4] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.usergeo",null);
                 fileArr[4][5] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.parentmsisdn",null);
                 fileArr[4][6] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.parentname",null);
                 fileArr[4][7] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.parentcat",null);
                 fileArr[4][8] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.parentgeo",null);
                 fileArr[4][9] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.ownermsisdn",null);
                 fileArr[4][10] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.ownercat",null);
                 fileArr[4][11] = RestAPIStringParser.getMessage(locale,"usermig.xlsheading.label.ownergeo",null);
                 
                 int[] finalRowCount = {5};
                 this.convertTo2dArrayForNPUsers(fileArr, masterDataMap, rows, null, finalRowCount);
                 finalFileArr = new String[finalRowCount[0]][cols];
                 System.arraycopy(fileArr, 0, finalFileArr, 0, finalRowCount[0]);
                 String noOfRowsInOneTemplate;
 				 noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");
 				if ("csv".equals(fileExt)) 
				{
					FileWriteUtil.writeinCSV(ExcelFileIDI.MIG_USER_INIT, finalFileArr, filePath + "" + fileName);
				} else if ("xls".equals(fileExt)) 
				{
					FileWriteUtil.writeinXLS(ExcelFileIDI.MIG_USER_INIT, finalFileArr, filePath + "" + fileName, noOfRowsInOneTemplate, 5);
				} else if ("xlsx".equals(fileExt)) 
				{
					FileWriteUtil.writeinXLSX(ExcelFileIDI.MIG_USER_INIT, finalFileArr, filePath + "" + fileName, noOfRowsInOneTemplate, 5);
				} else 
				{
					throw new BTSLBaseException( DownloadUserListController.class.getName(),methodName,PretupsErrorCodesI.FILE_FORMAT_NOT_SUPPORTED,new String[] { fileExt });
				}
                		 
                 
            } catch (Exception ex) {
				LOG.errorTrace(methodName, ex);
				LOG.error(
						methodName,
						"Unable to write data into a file Exception = "
								+ ex.getMessage());
				throw new BTSLBaseException(
						DownloadUserListController.class.getName(), methodName,
						PretupsErrorCodesI.FILE_WRITE_ERROR);

			}
            File fileNew = new File(filePath + "" + fileName);
	        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
	        String encodedString = Base64.getEncoder().encodeToString(fileContent);
	        String file1 = fileNew.getName();
	        fileDownloadResponse.setFileattachment(encodedString);
	        fileDownloadResponse.setFileType(fileExt);
	        fileDownloadResponse.setFileName(file1);
	        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
	        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
	        
            
        }
        catch (BTSLBaseException be) {
     	   if (be.getMessage().equalsIgnoreCase("1080001") || be.getMessage().equalsIgnoreCase("1080002")
 					|| be.getMessage().equalsIgnoreCase("1080003") || be.getMessage().equalsIgnoreCase("241023")
 					|| be.getMessage().equalsIgnoreCase("241018")) {
 				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
 				fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
 			} else {
 				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
 				fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
 			}
 			String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
 			fileDownloadResponse.setMessageCode(be.getMessage());
 			fileDownloadResponse.setMessage(resmsg);
        }catch (Exception ex) {
 			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
 			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
 			LOG.errorTrace(methodName, ex);
 			LOG.error(methodName, "Exception = " + ex.getMessage());
 		}finally {
 			if (mcomCon != null) {
 				mcomCon.close("");
 				mcomCon = null;
 			}
 			if(con != null)
 				con.close();
 		}
        
        return fileDownloadResponse;
	}
	  private String[][] convertTo2dArrayForNPUsers(String[][] p_fileArr, Map p_hashMap, int p_rows, Date p_currDate, int[] finalRowCount) throws BTSLBaseException {
	        final String METHOD_NAME = "convertTo2dArrayForNPUsers";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(METHOD_NAME, "Entered p_fileArr=" + p_fileArr + "p_hashMap=" + p_hashMap + "p_currDate=" + p_currDate);
	        }
	        try {
	            // first row is already generated,and the number of cols are fixed
	            // to eight
	            final Iterator iterator = p_hashMap.keySet().iterator();
	            String key = null;
	            UserVO userVO = new UserVO();
	            int rows = 4;
	            int cols;
	            while (iterator.hasNext()) {
	                key = (String) iterator.next();
	                userVO = (UserVO) p_hashMap.get(key);
	                
	               
	                        rows++;
	                        if (rows >= p_rows) {
	                            break;
	                        }
	                        cols = 0;
	                        p_fileArr[rows][cols++] = key;
	                        p_fileArr[rows][cols++] = userVO.getUserName();
	                        p_fileArr[rows][cols++] = userVO.getDomainName();
	                        p_fileArr[rows][cols++] = userVO.getCategoryCode();
	                        p_fileArr[rows][cols++] = userVO.getGeographicalAreaList().get(0).getGraphDomainCode();
	                        p_fileArr[rows][cols++] = userVO.getParentMsisdn();
	                        p_fileArr[rows][cols++] = userVO.getParentName();
	                        p_fileArr[rows][cols++] = userVO.getParentCategoryName();
	                        p_fileArr[rows][cols++] = userVO.getGeographicalAreaList().get(0).getParentGraphDomainCode();  
	                        p_fileArr[rows][cols++] = userVO.getOwnerMsisdn();
	                        p_fileArr[rows][cols++] = userVO.getOwnerCategoryName();
	                        p_fileArr[rows][cols++] = userVO.getGeographicalAreaList().get(0).getGraphDomainName();
	                      
	                        
	                        finalRowCount[0] += 1; 

	            }
	                

	            

	        } catch (Exception e) {
	            LOG.error(METHOD_NAME, "Exceptin:e=" + e);
	            LOG.errorTrace(METHOD_NAME, e);
	            throw new BTSLBaseException(this, METHOD_NAME, "");
	        } finally {
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(METHOD_NAME, "Exited p_fileArr=" + p_fileArr);
	            }
	        }
	        return p_fileArr;
	    }
	    
	
}
