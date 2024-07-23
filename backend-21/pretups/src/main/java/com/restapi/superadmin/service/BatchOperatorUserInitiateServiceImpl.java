package com.restapi.superadmin.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import jxl.read.biff.BiffException;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.BatchOPTUserDAO;
import com.btsl.user.businesslogic.BatchOPTUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.xl.BatchOPTUserCreateXL;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.superadmin.requestVO.BatchOperatorUserInitiateRequestVO;
import com.restapi.superadmin.responseVO.BatchOperatorUserInitiateResponseVO;
import com.restapi.superadmin.serviceI.BatchOperatorUserInitiateServiceI;
import com.restapi.superadminVO.BatchOperatorUserInitiateVO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.roles.businesslogic.UserRolesWebDAO;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@Service("BatchOperatorUserInitiateService")
public class BatchOperatorUserInitiateServiceImpl implements BatchOperatorUserInitiateServiceI{
    public static final String classname = "BatchOperatorUserInitiateServiceImpl";
	
	public static final Log log = LogFactory.getLog(BatchOperatorUserInitiateServiceImpl.class.getName());
	
	@Override
	public BatchOperatorUserInitiateResponseVO downloadFileTemplate(Connection con, MComConnectionI mcomCon,
			Locale locale, String categoryType, ChannelUserVO userVO, BatchOperatorUserInitiateResponseVO response,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException, ParseException, RowsExceededException, WriteException, IOException {
		
		final String methodName = "downloadFileTemplate";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        
        BatchOperatorUserInitiateVO batchOPTVO = new BatchOperatorUserInitiateVO();
        CategoryDAO _categoryDAO = new CategoryDAO();
        
        batchOPTVO.setCategoryList(_categoryDAO.loadCategoryList(con, PretupsI.OPERATOR_TYPE_OPT, userVO.getCategoryCode()));
        

        if (batchOPTVO.getCategoryListSize() == 1) {
            CategoryVO categoryVO = (CategoryVO) batchOPTVO.getCategoryList().get(0);
            batchOPTVO.setCategoryCode(categoryVO.getCategoryCode());
            batchOPTVO.setCategoryCodeDesc(categoryVO.getCategoryName());
        }

        if(categoryType.isEmpty() || categoryType == null) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_CATEGORY_OR_EMTPY);
        }

        batchOPTVO.setCategoryCode(categoryType);
        
        ArrayList list = userVO.getGeographicalAreaList();
        
        if (list != null && list.size() > 1) {
        	batchOPTVO.setAssociatedGeographicalList(list);
            UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
            batchOPTVO.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
        } else if (list != null && list.size() == 1) {
        	batchOPTVO.setAssociatedGeographicalList(null);
            UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
            batchOPTVO.setParentDomainCode(vo.getGraphDomainCode());
            batchOPTVO.setParentDomainDesc(vo.getGraphDomainName());
            batchOPTVO.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
        } else {
        	batchOPTVO.setAssociatedGeographicalList(null);
        }
        
        HashMap masterDataMap = new HashMap();
        batchOPTVO.setProductsList(LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true));
        ArrayList categoryList = batchOPTVO.getCategoryList();
        CategoryVO categoryVO = null;
        
        if (batchOPTVO.getCategoryCode().equals(PretupsI.ALL)) {
            StringBuffer categoryCode = new StringBuffer();
            String tempCategoryCode = "";
            for (int i = 0, j = categoryList.size(); i < j; i++) {
                categoryVO = (CategoryVO) categoryList.get(i);
                categoryCode.append(categoryVO.getCategoryName());
                categoryCode.append(",");
            }
            tempCategoryCode = categoryCode.toString().substring(0, categoryCode.toString().length() - 1);
            batchOPTVO.setCategoryStr(tempCategoryCode);
        }
        
        if (categoryList != null && categoryList.size() == 1) {
            categoryVO = (CategoryVO) categoryList.get(0);
            batchOPTVO.setCategoryVO(categoryVO);
            batchOPTVO.setCategoryStr(categoryVO.getCategoryName());
        }
        
        if (categoryList != null && categoryList.size() > 1) {
            categoryVO = null;
            for (int i = 0, j = categoryList.size(); i < j; i++) {
                categoryVO = (CategoryVO) categoryList.get(i);
                if (categoryVO.getCategoryCode().equalsIgnoreCase(batchOPTVO.getCategoryCode())) {
                	batchOPTVO.setCategoryVO(categoryVO);
                    break;
                }
            }
        }

        if(batchOPTVO.getCategoryVO() == null){
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_CATEGORY_OR_EMTPY);
        }

        UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
        BatchOPTUserDAO batchOPTUserDAO = new BatchOPTUserDAO();
        DomainDAO domainDAO = new DomainDAO();
        VomsProductDAO vomsProductDAO = new VomsProductDAO();
        
        masterDataMap.put(PretupsI.BATCH_OPT_USR_CREATED_BY, userVO.getUserName());
        masterDataMap.put(PretupsI.BATCH_OPT_USR_CATEGORY_NAME, batchOPTVO.getCategoryStr());
        masterDataMap.put(PretupsI.BATCH_OPT_USER_PREFIX_LIST, LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
        masterDataMap.put(PretupsI.BATCH_OPT_USR_STATUS_LIST, LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
        masterDataMap.put(PretupsI.BATCH_OPT_USR_DIVDEPT_LIST, batchOPTUserDAO.loadDivisionDeptList(con, TypesI.DIVDEPT_TYPE, PretupsI.USER_STATUS_ACTIVE));
        masterDataMap.put(PretupsI.BATCH_OPT_USR_ASSIGN_ROLES, rolesWebDAO.loadRolesList(con, batchOPTVO.getCategoryCode()));// set
                                                                                                                          // into
                                                                                                                          // HashMap
        masterDataMap.put(PretupsI.BATCH_OPT_USR_GEOGRAPHY_LIST, loadGeographyList(con, batchOPTVO, userVO));
        masterDataMap.put(PretupsI.BATCH_OPT_USR_DOMAIN_LIST, domainDAO.loadDomainDetails(con));
        masterDataMap.put(PretupsI.BATCH_OPT_USR_PRODUCT_LIST, LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true));
        
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        masterDataMap.put(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST, vomsProductDAO.loadVoucherTypeList(con));
        masterDataMap.put(PretupsI.BATCH_OPT_USR_VOUCHERSEGMENT_LIST, LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true));
        
        batchOPTVO.setBatchOPTUserMasterMap(masterDataMap);
        
        String filePath = Constants.getProperty("DownloadBatchOperatorUserPath");
        try {
            File fileDir = new File(filePath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error("downloadFileTemplate", "Exception" + e.getMessage());
            throw new BTSLBaseException(this, "downloadFileTemplate", "downloadfile.error.dirnotcreated", "selectDomainForInitiate");
        }
        
        String fileName = batchOPTVO.getCategoryCode() + Constants.getProperty("DownloadBatchOPTUserFileNamePrefix") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";
        BatchOPTUserCreateXL excelRW = new BatchOPTUserCreateXL();
        excelRW.BatchOperatorUserInitiateWriteExcel(ExcelFileIDI.BATCH_OPT_USER_INITIATE, batchOPTVO.getCategoryVO(), masterDataMap, locale, filePath + fileName);

		File fileNew = new File(filePath + "" + fileName);
		byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
		String encodedString = Base64.getEncoder().encodeToString(
				fileContent);
		String file1 = fileNew.getName();
		response.setFileAttachment(encodedString);
		response.setFileType("xls");
		response.setFileName(file1);
		String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
		response.setStatus(success);
		response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		String resmsg = RestAPIStringParser.getMessage(locale,
				PretupsErrorCodesI.SUCCESS, null);
		response.setMessage(resmsg);

        filePath = BTSLUtil.encrypt3DesAesText(filePath);
        
        return response;
	}
	
    public ArrayList loadGeographyList(Connection con, BatchOperatorUserInitiateVO batchOPTVO, ChannelUserVO userSessionVO) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadGeographyList", "Entered");
        }
        ArrayList geographyList = null;
        ArrayList<UserGeographiesVO> networkList =null;
        final String METHOD_NAME = "loadGeographyList";
        try {
            GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
            
            /*
             * 1)if graph_domain_type of the session user and added user are
             * same
             * then the geography list of the new user = session user
             * geographies list
             * 2)if sequence no is 1 means category is Network Admin
             * load the session network details
             * 3)load the list of all geographies on the basis of parent domain
             * code
             * 4)need to perform search, so prepare the list for search
             */
            // 1
            if (userSessionVO.getCategoryVO().getGrphDomainType().equals(batchOPTVO.getCategoryVO().getGrphDomainType())) {
            	//the case when we are adding operator users in batch: super network admin or super customer care from super admin. Instead of assigning geographies , networks are assigned.
            	if((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(batchOPTVO.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(batchOPTVO.getCategoryCode())))
                {
                	networkList = geographicalDomainWebDAO.loadNetworkList(con);
                	batchOPTVO.setNetworkList(networkList);
                	
                }
            	else if(TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(batchOPTVO.getCategoryCode()))
            	{
            		//the case when we are adding operator user in batch: super channel admin from super admin. Geographies are assigned irrespective of network code
            		 geographyList = geographicalDomainWebDAO.loadGeographyListForSuperChannelAdmin(con,batchOPTVO.getCategoryVO().getGrphDomainType());
            		 batchOPTVO.setGeographicalList(geographyList);
            		 
            		 if (geographyList != null && !geographyList.isEmpty())
            		 {
                      /*
                       * set the grphDoaminTypeName on the form GrphDomainTypeName
                       * is same for all VO's in list
                       */
            			 UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
            			 batchOPTVO.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
            		 }
            	}
            	else
            	{
                geographyList = userSessionVO.getGeographicalAreaList();
                batchOPTVO.setGeographicalList(geographyList);
                if (geographyList != null && !geographyList.isEmpty()) {
                    /*
                     * set the grphDoaminTypeName on the form
                     * GrphDomainTypeName is same for all VO's in list
                     */
                    UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
                    batchOPTVO.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                }
            	}
            }
            // 2
            else if (batchOPTVO.getCategoryVO().getGrphDomainSequenceNo() == 1) {
                UserGeographiesVO geographyVO = null;
                geographyList = new ArrayList();
                geographyVO = new UserGeographiesVO();
                geographyVO.setGraphDomainCode(userSessionVO.getNetworkID());
                geographyVO.setGraphDomainName(userSessionVO.getNetworkName());
                geographyVO.setGraphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
                batchOPTVO.setGrphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
                geographyList.add(geographyVO);

                batchOPTVO.setGeographicalList(geographyList);
            }
            // 3
            if ((userSessionVO.getCategoryVO().getGrphDomainSequenceNo() + 1) == batchOPTVO.getCategoryVO().getGrphDomainSequenceNo()) {
                geographyList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(), batchOPTVO.getParentDomainCode(), "%");
                batchOPTVO.setGeographicalList(geographyList);
                if (geographyList != null && !geographyList.isEmpty()) {
                    /*
                     * set the grphDoaminTypeName on the form
                     * GrphDomainTypeName is same for all VO's in list
                     */
                    UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
                    batchOPTVO.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                }
            }
            // 4
            else {
                ArrayList list = geographicalDomainWebDAO.loadDomainTypes(con, userSessionVO.getCategoryVO().getGrphDomainSequenceNo(), batchOPTVO.getCategoryVO().getGrphDomainSequenceNo());
                if (list != null && !list.isEmpty()) {
                    batchOPTVO.setDomainSearchList(list);
                    batchOPTVO.setSearchDomainTextArrayCount();
                    batchOPTVO.setSearchDomainCodeCount();
                }
            }

        } catch (Exception ex) {
            log.error("loadGeographyList", "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchOPTUserDAO[geographyList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadGeographyList", "error.general.processing",ex);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("loadGeographyList", "Exiting: geographyList ");
            }
        }
        if(!((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(batchOPTVO.getCategoryCode())) || (TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(batchOPTVO.getCategoryCode()))))
        return geographyList;
        else
        return networkList;
    }

	@Override
	public ArrayList<MasterErrorList> basicFileValidations(BatchOperatorUserInitiateRequestVO request,
			BatchOperatorUserInitiateResponseVO response, String categoryType,
			Locale locale, ArrayList<MasterErrorList> inputValidations)throws BTSLBaseException, SQLException  {
		    final String METHOD_NAME = "basicFileValidations";

		String pattern= "^[a-zA-Z]*$";
		 
		 if(BTSLUtil.isNullString(categoryType)) {
			 MasterErrorList masterErrorList = new MasterErrorList();
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
				response.setMessage(msg);
			}
		 if(!BTSLUtil.isNullString(categoryType)) {
		 String noSpaceStr = categoryType.replaceAll("\\s", ""); // using built in method just to check for aplhanumeric  
		 if(!noSpaceStr.matches(pattern)){
			 MasterErrorList masterErrorList = new MasterErrorList();
			 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
			 masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
			}
		 }
		 
		 if (!BTSLUtil.isNullorEmpty(request.getFileName()) &&  !BTSLUtil.isNullorEmpty(request.getFileAttachment())
					&& !BTSLUtil.isNullorEmpty(request.getFileType())) {
				String base64val = request.getFileAttachment();
				String requestFileName = request.getFileName();

		        boolean isValid = true;
		          
		        if (request.getFileName().length() > 30) {
		        	MasterErrorList masterErrorListFileName = new MasterErrorList();
					String resmsg = RestAPIStringParser.getMessage(
							new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
							PretupsErrorCodesI.SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAME_LENGTH, null);
					masterErrorListFileName.setErrorMsg(resmsg);
                    masterErrorListFileName.setErrorCode(PretupsErrorCodesI.SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAME_LENGTH);
					inputValidations.add(masterErrorListFileName);
					isValid = false ;
                    throw new BTSLBaseException(null, METHOD_NAME, PretupsErrorCodesI.SERVICEGROUP_UPLOAD_VALIDATE_FILE_FILENAME_LENGTH);
		  	    }
		        
		  		if (!C2CFileUploadApiController.isValideFileName(request.getFileName())) {
		  			MasterErrorList masterErrorList = new MasterErrorList();
		  			masterErrorList.setErrorMsg("Invalid file name.");
		  			masterErrorList.setErrorCode("");
		  			inputValidations.add(masterErrorList);
		  			isValid = false ;
		  		}
		  		if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(request.getFileType().toUpperCase())) {
		  			String fileNamewithextention = requestFileName + ".csv";
		  		} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(request.getFileType().toUpperCase())) {
		  			String fileNamewithextention = requestFileName + ".xls";
		  		} else if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(request.getFileType().toUpperCase())) {
		  			String fileNamewithextention = requestFileName + ".xlsx";
		  		} else {
		  			MasterErrorList masterErrorList = new MasterErrorList();
		  			masterErrorList.setErrorMsg("Invalid file type.");
		  			masterErrorList.setErrorCode("");
		  			inputValidations.add(masterErrorList);
		  			isValid = false ;
		  		}		
		} else {
			boolean isValid = true;

			if (BTSLUtil.isNullorEmpty(request.getFileName())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File name is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false ;
			}
			if (BTSLUtil.isNullorEmpty(request.getFileAttachment())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File attachment is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				response.setMessage("File is Empty");
				isValid = false ;
			}
			if (BTSLUtil.isNullorEmpty(request.getFileType())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File type is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false ;
			}
		}
			
		return inputValidations;

	}

	@Override
	public boolean uploadAndValidateFile(Connection con, MComConnectionI mcomCon, ChannelUserVO userVO,
			BatchOperatorUserInitiateRequestVO request, BatchOperatorUserInitiateResponseVO response)
			throws BTSLBaseException, SQLException {

		final String methodName = "uploadAndValidateFile";
	    if (log.isDebugEnabled()) {
	    	log.debug(methodName, "Entered");
	    }

	    ProcessStatusVO processVO = null;
        boolean processRunning = true;
        boolean isUploaded = false;
	    
        try {
            final ProcessBL processBL = new ProcessBL();
            try {
                processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.BATCH_OPT_USR_PROCESS_ID, userVO.getNetworkID());
            } catch (BTSLBaseException e) {
                log.error("uploadAndValidateFile", "Exception:e=" + e);
                log.errorTrace(methodName, e);
                processRunning = false;
                throw e;
            }
            if (processVO != null && !processVO.isStatusOkBool()) {
                processRunning = false;
                throw new BTSLBaseException(PretupsErrorCodesI.OPT_BATCH_ALREADY_RUNNING);
            }
            
            // If The process is not running commit the connection to update
            // Process status
        
            mcomCon.partialCommit()	;
            processVO.setNetworkCode(userVO.getNetworkID());
            
            String dir = Constants.getProperty("UploadBatchOPTUserFilePath"); // Upload File Path

            if (BTSLUtil.isNullString(dir)) {
                throw new BTSLBaseException(this, methodName, "user.initiatebatchoperatoruser.error.filenamelength");
            }
            
            final String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_XLS);
            String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BATCH_OPT_USER");
            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = String.valueOf(0);
            }
            
			ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
	    	ErrorMap errorMap = new ErrorMap();
	        LinkedHashMap<String, List<String>> bulkDataMap = null; ;
            String file = request.getFileAttachment();
            String filePath = Constants.getProperty("UploadBatchOPTUserFilePath");
			HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_TYPE1, request.getFileType());
			fileDetailsMap.put(PretupsI.FILE_NAME,request.getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, file);
			fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, PretupsI.BATCH_OPT_USR_INITIATION_SERVICE);	
			bulkDataMap = fileUtil.uploadAndReadGenericFileBatchOperatorUserInitiate(fileDetailsMap, 0, 4, errorMap);
			bulkDataMap = getMapInFileFormat(bulkDataMap);
			isUploaded = true;
			
        }   finally {
				try {
	            	if(mcomCon != null){
	            		mcomCon.partialRollback();
	            	}
	            } catch (Exception ee) {
	                log.errorTrace(methodName, ee);
	            }
	        if (processRunning) {
	            try {
	                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
	                final ProcessStatusDAO processDAO = new ProcessStatusDAO();
	                if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {              
	                	mcomCon.finalCommit();
	                } else {
	                 
	                	mcomCon.finalRollback();
	                }
	            } catch (Exception e) {
	
	                if (log.isDebugEnabled()) {
	                    log.error(methodName, " Exception in update process detail for bulk user modification" + e.getMessage());
	                }
	                log.errorTrace(methodName, e);
	            }
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Exiting");
	        }
        }   
        return isUploaded;
        
	}

	@Override
	public void processUploadedFile(Connection con, MComConnectionI mcomCon, ChannelUserVO userVO,
			String categoryType, BatchOperatorUserInitiateRequestVO request,
			BatchOperatorUserInitiateResponseVO response, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException, FileNotFoundException, IOException, BiffException {
        if (log.isDebugEnabled()) {
            log.debug("processUploadedFile", "Entered");
        }
        
        int rows = 0;
        int cols = 0;
        Date currentDate = new Date();
        final int rowOffset = 5;
        String[][] excelArr = null;
        boolean fileValidationErrorExists;
        BatchOPTUserDAO batchOPTUserDAO = new BatchOPTUserDAO();
        final String methodName = "processUploadedFile";
		
        try {
            BatchOperatorUserInitiateVO batchOPTVO = new BatchOperatorUserInitiateVO();
            
            String filteredMsisdn = null;
            CategoryDAO _categoryDAO = new CategoryDAO();
            batchOPTVO.setCategoryList(_categoryDAO.loadCategoryList(con, PretupsI.OPERATOR_TYPE_OPT, userVO.getCategoryCode()));

            if (batchOPTVO.getCategoryListSize() == 1) {
                CategoryVO categoryVO = (CategoryVO) batchOPTVO.getCategoryList().get(0);
                batchOPTVO.setCategoryCode(categoryVO.getCategoryCode());
                batchOPTVO.setCategoryCodeDesc(categoryVO.getCategoryName());
            }
            
            batchOPTVO.setCategoryCode(categoryType);
            
            ArrayList list = userVO.getGeographicalAreaList();
            
            if (list != null && list.size() > 1) {
            	batchOPTVO.setAssociatedGeographicalList(list);
                UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
                batchOPTVO.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
            } else if (list != null && list.size() == 1) {
            	batchOPTVO.setAssociatedGeographicalList(null);
                UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
                batchOPTVO.setParentDomainCode(vo.getGraphDomainCode());
                batchOPTVO.setParentDomainDesc(vo.getGraphDomainName());
                batchOPTVO.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
            } else {
            	batchOPTVO.setAssociatedGeographicalList(null);
            }
            
            HashMap masterDataMap = new HashMap();
            batchOPTVO.setProductsList(LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true));
            ArrayList categoryList = batchOPTVO.getCategoryList();
            CategoryVO catVO = null;
            
            if (batchOPTVO.getCategoryCode().equals(PretupsI.ALL)) {
                StringBuffer categoryCode = new StringBuffer();
                String tempCategoryCode = "";
                for (int i = 0, j = categoryList.size(); i < j; i++) {
                    catVO = (CategoryVO) categoryList.get(i);
                    categoryCode.append(catVO.getCategoryName());
                    categoryCode.append(",");
                }
                tempCategoryCode = categoryCode.toString().substring(0, categoryCode.toString().length() - 1);
                batchOPTVO.setCategoryStr(tempCategoryCode);
            }
            
            if (categoryList != null && categoryList.size() == 1) {
                catVO = (CategoryVO) categoryList.get(0);
                batchOPTVO.setCategoryVO(catVO);
                batchOPTVO.setCategoryStr(catVO.getCategoryName());
            }
            
            if (categoryList != null && categoryList.size() > 1) {
                catVO = null;
                for (int i = 0, j = categoryList.size(); i < j; i++) {
                    catVO = (CategoryVO) categoryList.get(i);
                    if (catVO.getCategoryCode().equalsIgnoreCase(batchOPTVO.getCategoryCode())) {
                    	batchOPTVO.setCategoryVO(catVO);
                        break;
                    }
                }
            }
            
            UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
            DomainDAO domainDAO = new DomainDAO();
            VomsProductDAO vomsProductDAO = new VomsProductDAO();
            
            masterDataMap.put(PretupsI.BATCH_OPT_USR_CREATED_BY, userVO.getUserName());
            masterDataMap.put(PretupsI.BATCH_OPT_USR_CATEGORY_NAME, batchOPTVO.getCategoryStr());
            masterDataMap.put(PretupsI.BATCH_OPT_USER_PREFIX_LIST, LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
            masterDataMap.put(PretupsI.BATCH_OPT_USR_STATUS_LIST, LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
            masterDataMap.put(PretupsI.BATCH_OPT_USR_DIVDEPT_LIST, batchOPTUserDAO.loadDivisionDeptList(con, TypesI.DIVDEPT_TYPE, PretupsI.USER_STATUS_ACTIVE));
            masterDataMap.put(PretupsI.BATCH_OPT_USR_ASSIGN_ROLES, rolesWebDAO.loadRolesList(con, batchOPTVO.getCategoryCode()));// set
                                                                                                                              // into
                                                                                                                              // HashMap
            masterDataMap.put(PretupsI.BATCH_OPT_USR_GEOGRAPHY_LIST, loadGeographyList(con, batchOPTVO, userVO));
            masterDataMap.put(PretupsI.BATCH_OPT_USR_DOMAIN_LIST, domainDAO.loadDomainDetails(con));
            masterDataMap.put(PretupsI.BATCH_OPT_USR_PRODUCT_LIST, LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true));
            
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
            masterDataMap.put(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST, vomsProductDAO.loadVoucherTypeList(con));
            masterDataMap.put(PretupsI.BATCH_OPT_USR_VOUCHERSEGMENT_LIST, LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true));
            
            batchOPTVO.setBatchOPTUserMasterMap(masterDataMap);
            
            // Open the uploaded XLS file parse row by row and validate the file
            BatchOPTUserCreateXL excelRW = new BatchOPTUserCreateXL();
        	String fileName = request.getFileName();
            final String dir = Constants.getProperty("UploadBatchOPTUserFilePath") + "temp/" ; // Upload File Path
            File file = new File(dir+fileName+"."+request.getFileType());
            excelArr = excelRW.readExcel(ExcelFileIDI.BATCH_OPT_USER_INITIATE, dir+fileName+"."+request.getFileType());
            
            // Check The Validity of the XLS file Uploaded, reject the file if
            // the file is not in the proper format.
            // Check 1: If there is not a single Record as well as Header in the
            // file
            try {
                cols = excelArr[0].length;
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                boolean isDeleted = file.delete();
                if(isDeleted){
                 log.debug(methodName, "File deleted successfully");
                }
                throw new BTSLBaseException(PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC);
            }
            
            rows = excelArr.length; // rows include the headings
            int maxRowSize = 0;
            
            if (rows <= rowOffset) {
            	boolean isDeleted = file.delete();
                if(isDeleted){
                 log.debug(methodName, "File deleted successfully");
                }
                throw new BTSLBaseException(PretupsErrorCodesI.BULK_OPT_USER_INIT_NO_REC);
            }
            
            // Check 2: the Max Row Size of the XLS file. if it is greater than
            // the specified size throw err.
            try {
                maxRowSize = Integer.parseInt(Constants.getProperty("maxRecordsInBatchOPTUserInitiate"));
            } catch (Exception e) {
                maxRowSize = 1000;
                log.error("processUploadedFile", "Exception:e=" + e);
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserInitiateAction[processUploadedFile]", "", "", "", "Exception:" + e.getMessage());
            }
            if (rows > maxRowSize) {
                file.delete();
                String resmsg = RestAPIStringParser.getMessage(
                        new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                        PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED,
                        new String[] { String.valueOf(maxRowSize) });
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED);
                throw new BTSLBaseException(classname, methodName,
                        PretupsErrorCodesI.BULK_OPT_USER_INIT_MAX_REC_REACHED,
                        new String[] { String.valueOf(maxRowSize) });
            }
            
            // Get the rows and columns from the XLS file
            // Check the validity of the file including its rows and columns..
            // 2D array processing starts from the 6th Row of XLS file.Fifth row
            // will contains the label.
            /*
             * ROW 5 COL 1 : User name prefix
             * ROW 5 COL 2 : User name
             * ROW 5 COL 3 : Web login id
             * ROW 5 COL 4 : Web login password
             * ROW 5 COL 5 : Mobile number
             * ROW 5 COL 6 : Subscriber code
             * ROW 5 COL 7 : Status
             * ROW 5 COL 8 : Division
             * ROW 5 COL 9 : Department
             * ROW 5 COL 10 : Geographical domains
             * ROW 5 COL 11 : Product s
             * ROW 5 COL 12 : Roles (comma separated)
             * ROW 5 COL 13 : Domains (comma separated)
             * ROW 5 COL 14 : Services (comma separated)
             * ROW 5 COL 15 : Designation
             * ROW 5 COL 16 : External code
             * ROW 5 COL 17 : Contact number
             * ROW 5 COL 18 : SSN
             * ROW 5 COL 19 : Address1
             * ROW 5 COL 20 : Address2
             * ROW 5 COL 21 : City
             * ROW 5 COL 22 : State
             * ROW 5 COL 23 : Country
             * ROW 5 COL 24 : E-mail
             */

         // For OCI changes
            HashMap error_messageMap = null;
            String errorMessage = "";
            Set passwordErrSetKey = null;
            Iterator itr = null;
            String rowVal = null;
            int reptRowNo = 0;
            OperatorUtilI operatorUtili = null;
            
            try {
                String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserUpdateAction[processUploadedFile]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
            }
            // end of OCI changes
            
        	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            
            final int blankLines = 0;
            ErrorMap errorMap = new ErrorMap();
            ArrayList fileErrorList = new ArrayList();
            ArrayList batchOPTUserVOList = new ArrayList();
            BatchOPTUserVO batchOPTUserVO = null;
            ListValueVO errorVO = null;
            ListValueVO listVO = null;
            CategoryVO categoryVO = batchOPTVO.getCategoryVO();
            ArrayList voucherList=null;
            ArrayList segmentList = null;
            // Get the prefix list from Master Data & prepare the service array
            ArrayList prefixList = (ArrayList) masterDataMap.get(PretupsI.BATCH_OPT_USER_PREFIX_LIST);
            ArrayList statusList = (ArrayList) masterDataMap.get(PretupsI.BATCH_OPT_USR_STATUS_LIST);
            ArrayList divDepList = (ArrayList) masterDataMap.get(PretupsI.BATCH_OPT_USR_DIVDEPT_LIST);
            ArrayList geographyList = (ArrayList) masterDataMap.get(PretupsI.BATCH_OPT_USR_GEOGRAPHY_LIST);
            ArrayList productList = (ArrayList) masterDataMap.get(PretupsI.BATCH_OPT_USR_PRODUCT_LIST);
            ArrayList rolesList = this.getRolesList((HashMap) masterDataMap.get(PretupsI.BATCH_OPT_USR_ASSIGN_ROLES));
            ArrayList domainList = (ArrayList) masterDataMap.get(PretupsI.BATCH_OPT_USR_DOMAIN_LIST);
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
            {
            	voucherList = (ArrayList) masterDataMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
            }
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
            {
            	segmentList = (ArrayList) masterDataMap.get(PretupsI.BATCH_OPT_USR_VOUCHERSEGMENT_LIST);
            }
            HashMap<String, String> msisdnMap = new HashMap<String, String>();
            
            Start: for (int r = rowOffset; r < rows; r++) {

                fileValidationErrorExists = false;
                String[] geographyArr = null;
                String[] productArr = null;
                String[] roleArr = null;
                String[] domainArr = null;
                String[] voucherArr = null;
                String[] segmentArr = null;
                ListValueVO listValueVO = null;
                int count = 0;

                // ===================== Field Number 1: User Name Prefix
                // validation =====================
                if (BTSLUtil.isNullString(excelArr[r][count])) // User Name
                                                               // Prefix is
                                                               // Mandatory
                                                               // field
                {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unameprefixmissing", null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else // Validate Prefixes Mr/Miss etc from the List
                {
                    excelArr[r][count] = excelArr[r][count].trim();
                    listVO = BTSLUtil.getOptionDesc(excelArr[r][count], prefixList);
                    if (BTSLUtil.isNullString(listVO.getValue())) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unameprefixinvalid", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 1: End of User Name Prefix
                // validation =====================

                // ===================== Field Number 2: User Name validation
                // starts here=====================

                // Modified by deepika aggarwal
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
                    ++count;
                    if (BTSLUtil.isNullString(excelArr[r][count])) // Operator
                                                                   // User Name
                                                                   // is
                                                                   // Mandatory
                                                                   // field
                    {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamemissing", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else {

                        excelArr[r][count] = excelArr[r][count].trim();
                        if (excelArr[r][count].length() > 80)// Check Operator
                                                             // User Name length
                        {
                        	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamelengtherr", null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                } else {
                    ++count;
                    if (BTSLUtil.isNullString(excelArr[r][count])) // Operator
                                                                   // User First
                                                                   // Name is
                                                                   // Mandatory
                                                                   // field
                    {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.fnamemissing", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else {

                        excelArr[r][count] = excelArr[r][count].trim();
                        if (excelArr[r][count].length() > 40)// Check Operator
                                                             // User First Name
                                                             // length
                        {
                        	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.fname.lengtherr", null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    ++count;
                    if (!BTSLUtil.isNullString(excelArr[r][count])) {
                        excelArr[r][count] = excelArr[r][count].trim();
                        // Check User Name length
                        if (excelArr[r][count].length() > 40) {
                        	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.lastnamelengtherr", null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                }
                // end modified by deepika aggarwal

                // ===================== Field Number 2: End of User Name
                // validation starts here=====================

                // ===================== Field Number 3: Login id validation
                // starts here=====================
                ++count;
                if (BTSLUtil.isNullString(excelArr[r][count])) {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.loginidreqforweb", null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 20) {
                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.loginlenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SPACE_ALLOW_IN_LOGIN)).booleanValue() && excelArr[r][count].contains(" ")) {
                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.loginspacenotallowed", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 3: End Login id validation
                // here=====================

                // ===================== Field Number 4: Password related
                // validation starts here=====================
                // If Password is blank then system default Password(0000) will
                // be allocated
                ++count;
                String password = excelArr[r][count];
                if (BTSLUtil.isNullString(password)) {
                    password = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD));
                    excelArr[r][count] = password = password.trim();
                } else {
                    excelArr[r][count] = password = password.trim();

                    // For OCI changes By sanjeew Date 13/07/07
                    error_messageMap = null;
                    error_messageMap = operatorUtili.validatePassword(excelArr[r][count - 1], password);
                    if (!error_messageMap.isEmpty()) {
                        errorMessage = "";
                        passwordErrSetKey = null;
                        itr = null;
                        rowVal = null;
                        passwordErrSetKey = error_messageMap.keySet();
                        itr = passwordErrSetKey.iterator();
                        rowVal = String.valueOf(r + 1);
                        while (itr.hasNext()) {
                            String error = null;
                            errorMessage = (String) itr.next();
                            if (error_messageMap.get(errorMessage) instanceof ArrayList){
                                ArrayList<String> al = new ArrayList<String>();
                                al = (ArrayList<String>) error_messageMap.get(errorMessage);
                                if (al != null) {
                                    Iterator<String> itr_k = al.iterator(); // Initialize the iterator here

                                    while (itr_k.hasNext()) {
                                        String[] args = new String[] { itr_k.next() };
                                        error = RestAPIStringParser.getMessage(locale, errorMessage, args);
                                        errorVO = new ListValueVO("", rowVal, error);
                                        fileErrorList.add(errorVO);
                                        reptRowNo++;
                                    }
                                }
                            }
                            else {
                                error = RestAPIStringParser.getMessage(locale, errorMessage, (String[]) error_messageMap.get(errorMessage));
                                errorVO = new ListValueVO("", rowVal, error);
                                fileErrorList.add(errorVO);
                                reptRowNo++;
                            }
                        }
                        reptRowNo = reptRowNo - 1;
                        fileValidationErrorExists = true;
                        continue;
                    }
                    // End of OCI changes

                }
                // ===================== Field Number 4: End Password related
                // validation=====================

                // ===================== Field Number 5: MSISDN related
                // validations starts here=====================
                ++count;
                if (BTSLUtil.isNullString(excelArr[r][count])) {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.msisdnreqforsmserr", new String[] { excelArr[r][count] });
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    try {
                        excelArr[r][count] = excelArr[r][count].trim();
                        filteredMsisdn = PretupsBL.getFilteredMSISDN(excelArr[r][count]);
                        excelArr[r][count] = filteredMsisdn;
                    } catch (Exception ee) {
                        log.errorTrace(methodName, ee);
                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnisinvalid", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnisinvalid", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    if (excelArr[r][count].length() > 15) {
                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.msisdnlenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }

                }
                if(!userVO.getCategoryCode().equalsIgnoreCase(PretupsI.CATEGORY_CODE_NETWORK_ADMIN)) {
                    if (new UserDAO().isMSISDNExistOptUser(con, excelArr[r][count])) {
                        String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, new String[]{excelArr[r][count]});
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                if (msisdnMap.containsKey(excelArr[r][count])) {
                	String error = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, new String[] { excelArr[r][count] });
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                else {
                	msisdnMap.put(excelArr[r][count], rowVal);
                }

                // ===================== Field Number 5: End MSISDN related
                // validations here =====================

                // ===================== Field Number 6: Subscriber Code
                // validation starts here =====================
                ++count;
                if (BTSLUtil.isNullString(excelArr[r][count])) // Subscriber
                                                               // Code Name is
                                                               // Mandatory
                                                               // field
                {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.scodemissing", null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 12)// Check Subscriber
                                                         // Code length
                    {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.scodelengtherr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 6: End of Subscriber Code
                // validation starts here =====================

                // ===================== Field Number 7: Status validation
                // =====================
                ++count;
                if (BTSLUtil.isNullString(excelArr[r][count])) // Status is
                                                               // Mandatory
                                                               // field
                {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamestatusmissing", null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else // Validate Status Y/N/W etc from the List
                {
                    excelArr[r][count] = excelArr[r][count].trim();
                    listVO = BTSLUtil.getOptionDesc(excelArr[r][count], statusList);
                    if (BTSLUtil.isNullString(listVO.getValue())) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamestatusinvalid", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 7: End of Status
                // validation =====================

                // ===================== Field Number 8: Division validation
                // =====================
                BatchOPTUserVO batchOPTUserVO1 = null;
                boolean flag = false;
                ++count;
                if (BTSLUtil.isNullString(excelArr[r][count])) // Division is
                                                               // Mandatory
                                                               // field
                {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamedivisionmissing", null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else // Validate Division
                {
                    excelArr[r][count] = excelArr[r][count].trim();
                    for (int i = 0, j = divDepList.size(); i < j; i++) {
                        batchOPTUserVO1 = (BatchOPTUserVO) divDepList.get(i);
                        if (batchOPTUserVO1.getDivdeptID().equalsIgnoreCase(excelArr[r][count]) && batchOPTUserVO1.getDivdeptID().equalsIgnoreCase(batchOPTUserVO1.getParentID())) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamedivisioninvalid", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 8: End of Division
                // validation =====================

                // ===================== Field Number 9: Department validation
                // =====================
                ++count;
                flag = false;
                if (BTSLUtil.isNullString(excelArr[r][count])) // Division is
                                                               // Mandatory
                                                               // field
                {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamedepartmentmissing", null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue Start;
                } else // Validate Department
                {
                    excelArr[r][count] = excelArr[r][count].trim();
                    for (int i = 0, j = divDepList.size(); i < j; i++) {
                        batchOPTUserVO1 = (BatchOPTUserVO) divDepList.get(i);
                        if (batchOPTUserVO1.getDivdeptID().equalsIgnoreCase(excelArr[r][count]) && excelArr[r][count - 1].equalsIgnoreCase(batchOPTUserVO1.getParentID())) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamedepartmentinvalid", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue Start;
                    }
                }
                // ===================== Field Number 9: End of Department
                // validation =====================

                // ===================== Field Number 10: Geographies Domain
                // validation starts here =====================
                UserGeographiesVO userGeographiesVO = null;
                ++count;
                if ((BTSLUtil.isNullString(excelArr[r][count]))&& (!(TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(batchOPTVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(batchOPTVO.getCategoryCode())) )) // Geographies Domain is Mandatory field
                {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamegeographymissing", null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                else if((BTSLUtil.isNullString(excelArr[r][count]))&& (TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(batchOPTVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(batchOPTVO.getCategoryCode())) ){
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamenetworkmissing", null);
                	errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
                
                else // Validate Geographies Domain
                {
                    excelArr[r][count] = excelArr[r][count].trim();
                    boolean geographyFlag = true;
                    geographyArr = BTSLUtil.removeDuplicatesString(excelArr[r][count].split(","));
                    for (int i = 0, j = geographyArr.length; i < j; i++) {
                        geographyArr[i] = geographyArr[i].trim();
                        geographyFlag = false;
                        Iterator geoItr = geographyList.iterator();
                        while (geoItr.hasNext()) {
                            userGeographiesVO = (UserGeographiesVO) geoItr.next();
                            if (!BTSLUtil.isNullString(geographyArr[i]) && userGeographiesVO.getGraphDomainCode().equalsIgnoreCase(geographyArr[i])) {
                                geographyFlag = true;
                                break;
                            }
                        }
                        if ((!geographyFlag) && (!(TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(batchOPTVO.getCategoryCode())||TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(batchOPTVO.getCategoryCode())))) {
                        	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamegeographyinvalid", null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue Start;
                        }
                        else if((!geographyFlag) && ((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(batchOPTVO.getCategoryCode())||TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(batchOPTVO.getCategoryCode()))))
                        {
                        	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamenetworkinvalid", null);
                        	errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue Start;
                        	
                        }
                    }
                }
                // ===================== Field Number 10: End of Geographies
                // Domain validation =====================

                // ===================== Field Number 11: Products validation
                // starts here =====================
                if (categoryVO.getProductTypeAllowed().equalsIgnoreCase(PretupsI.YES)) {
                    ++count;
                    if (BTSLUtil.isNullString(excelArr[r][count])) // Products
                                                                   // is
                                                                   // Mandatory
                                                                   // field
                    {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unameproductmissing", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else // Validate Products
                    {
                        excelArr[r][count] = excelArr[r][count].trim();
                        productArr = BTSLUtil.removeDuplicatesString(excelArr[r][count].split(","));
                        for (int i = 0, j = productArr.length; i < j; i++) {
                            listVO = BTSLUtil.getOptionDesc(productArr[i].trim(), productList);
                            if (!BTSLUtil.isNullString(productArr[i]) && BTSLUtil.isNullString(listVO.getValue())) {
                            	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unameproductinvalid", null);
                                errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue Start;
                            }
                        }
                    }
                }
                // ===================== Field Number 11: End of Products
                // validation =====================

                // ===================== Field Number 12: RolesType validation
                // starts here =====================
                boolean roleTypeFlag = false;
                ++count;
                if (BTSLUtil.isNullString(excelArr[r][count])) // RoleType is
                                                               // Mandatory
                                                               // field
                {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamerolestypemissing", null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else // Validate RoleType Y/N.
                {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (!("Y".equalsIgnoreCase(excelArr[r][count]) || "N".equalsIgnoreCase(excelArr[r][count]))) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unameroletypeinvalid", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }

                // ===================== Field Number 12: End of RolesType
                // validation =====================

                // ===================== Field Number 13: Roles validation
                // starts here =====================
                boolean roleFlag = false;
                UserRolesVO userRolesVO = null;
                ++count;
                if (BTSLUtil.isNullString(excelArr[r][count])) // Roles is
                                                               // Mandatory
                                                               // field
                {
                	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamerolesmissing", null);
                    errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                    fileErrorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                } else // Validate Roles
                {
                    excelArr[r][count] = excelArr[r][count].trim();
                    roleArr = BTSLUtil.removeDuplicatesString(excelArr[r][count].split(","));
                    if ("Y".equalsIgnoreCase(excelArr[r][count - 1]) && roleArr.length > 1) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamemultiplegrouprole", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    for (int i = 0, j = roleArr.length; i < j; i++) {
                        roleFlag = false;
                        Iterator listItr = rolesList.iterator();
                        while (listItr.hasNext()) {
                            roleArr[i] = roleArr[i].trim();
                            userRolesVO = (UserRolesVO) listItr.next();
                            if (!BTSLUtil.isNullString(roleArr[i]) && userRolesVO.getRoleCode().equalsIgnoreCase(roleArr[i]) && userRolesVO.getGroupRole().equalsIgnoreCase(excelArr[r][count - 1])) {
                                roleFlag = true;
                                break;
                            }
                        }
                        if (!roleFlag) {
                        	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamerolesinvalid", null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue Start;
                        }
                    }
                }
                // ===================== Field Number 13: End of Roles
                // validation =====================
                // ===================== Field Number 14:Authentication Allowed
                // or Not ================
                
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ))).booleanValue()) {
                	++count;
                    if (!BTSLUtil.isNullString((excelArr[r][count]).trim())) {
                        excelArr[r][count] = (excelArr[r][count]).trim();
                    } else {
                        excelArr[r][count] = PretupsI.NO;
                    }
                }
                // ===================== Field Number 15: Domains validation
                // starts here =====================
                // Domains will be assigned to those users where domainAllowed
                // Flag = Y and fixedDomains Flag = A(Assigned)
                if (PretupsI.YES.equals(categoryVO.getDomainAllowed()) && PretupsI.DOMAINS_ASSIGNED.equals(categoryVO.getFixedDomains())) {
                    ++count;
                    if (BTSLUtil.isNullString(excelArr[r][count])) // Domains is
                                                                   // Mandatory
                                                                   // field
                    {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamedomainmissing", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue Start;
                    } else {
                        excelArr[r][count] = excelArr[r][count].trim();
                        domainArr = BTSLUtil.removeDuplicatesString(excelArr[r][count].split(","));
                        boolean domainFlag;
                        DomainVO domainVO = null;
                        for (int i = 0, j = domainArr.length; i < j; i++) {
                            domainFlag = false;
                            Iterator listItr = domainList.iterator();
                            while (listItr.hasNext()) {
                                domainArr[i] = domainArr[i].trim();
                                domainVO = (DomainVO) listItr.next();
                                if (!BTSLUtil.isNullString(domainArr[i]) && domainVO.getDomainCodeforDomain().equalsIgnoreCase(domainArr[i])) {
                                    domainFlag = true;
                                    break;
                                }
                            }
                            if (!domainFlag) {
                            	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.unamedomaininvalid", null);
                                errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                                fileErrorList.add(errorVO);
                                fileValidationErrorExists = true;
                                continue Start;
                            }
                        }
                    }
                }
                // ===================== Field Number 16: End of Domains
                // validation =====================

                // ===================== Field Number 17: Designation validation
                // starts here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 30) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.designationlenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 16: End of Designation
                // validation =====================

                // ===================== Field Number 17: External Code
                // validation starts here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 20) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.externalcodelenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 17: End of External Code
                // validation =====================

                // ===================== Field Number 18: Contact Number
                // validation starts here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (!BTSLUtil.isNumeric(excelArr[r][count])) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.contactnumberinvalid", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else if (excelArr[r][count].length() > 15) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.contactnumberlenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 18: End of Contact Number
                // validation =====================

                // ===================== Field Number 19: SSN validation starts
                // here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 15) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.ssnlenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 19: End of SSN validation
                // =====================

                // ===================== Field Number 20: Address1 validation
                // starts here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 50) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.add1lenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 20: End of Address1
                // validation =====================

                // ===================== Field Number 21: Address2 validation
                // starts here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 50) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.add2lenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 21: End of Address2
                // validation =====================

                // ===================== Field Number 22: City validation starts
                // here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 30) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.citylenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 22: End of City validation
                // =====================

                // ===================== Field Number 23: State validation
                // starts here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 30) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.statelenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 23: End of State
                // validation =====================

                // ===================== Field Number 24: Country validation
                // starts here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 20) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.countrylenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 24: End of Country
                // validation =====================

                // ===================== Field Number 25: E-mail validation
                // starts here =====================
                ++count;
                if (!BTSLUtil.isNullString(excelArr[r][count])) {
                    excelArr[r][count] = excelArr[r][count].trim();
                    if (excelArr[r][count].length() > 60) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.emaillenerr", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else if (!BTSLUtil.validateEmailID(excelArr[r][count])) {
                    	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.invalidemailformat", null);
                        errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                }
                // ===================== Field Number 24: End of E-mail
                // validation =====================
                
                
             // ===================== Field Number 25: voucher type
                // validation starts here =====================
               
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
                {
                ++count;
                if ((!BTSLUtil.isNullString(excelArr[r][count]))) // validate Voucher type
                {
                 
                    excelArr[r][count] = excelArr[r][count].trim();
                    boolean voucherFlag = true;
                    voucherArr = BTSLUtil.removeDuplicatesString(excelArr[r][count].split(","));
                    for (int i = 0, j = voucherArr.length; i < j; i++) {
                        voucherArr[i] = voucherArr[i].trim();
                        voucherFlag = false;
                        Iterator vochrItr = voucherList.iterator();
                        while (vochrItr.hasNext()) {
                            listValueVO = (ListValueVO) vochrItr.next();
                            if (!BTSLUtil.isNullString(voucherArr[i]) && listValueVO.getValue().equalsIgnoreCase(voucherArr[i])) {
                                voucherFlag = true;
                                break;
                            }
                        }
                        if (!voucherFlag) {
                        	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.vouchertypeinvalid", null);
                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue Start;
                        }
                       
                    }
                }
                
                }
             // ===================== Field Number 25: End of Voucher type validation =====================
                
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue()) {
	                ++count;
	                if ((!BTSLUtil.isNullString(excelArr[r][count])))
	                {
	                    excelArr[r][count] = excelArr[r][count].trim();
	                    boolean segmentFlag = true;
	                    segmentArr = BTSLUtil.removeDuplicatesString(excelArr[r][count].split(","));
	                    for (int i = 0, j = segmentArr.length; i < j; i++) {
	                    	segmentArr[i] = segmentArr[i].trim();
	                        segmentFlag = false;
	                        Iterator segmentItr = segmentList.iterator();
	                        while (segmentItr.hasNext()) {
	                            listValueVO = (ListValueVO) segmentItr.next();
	                            if (!BTSLUtil.isNullString(segmentArr[i]) && listValueVO.getValue().equalsIgnoreCase(segmentArr[i])) {
	                            	segmentFlag = true;
	                                break;
	                            }
	                        }
	                        if (!segmentFlag) {
	                        	String error = RestAPIStringParser.getMessage(locale, "user.batchoptuser.processuploadedfile.error.vouchersegmentinvalid", null);
	                            errorVO = new ListValueVO("", String.valueOf(r + 1), error);
	                            fileErrorList.add(errorVO);
	                            fileValidationErrorExists = true;
	                            continue Start;
	                        }
	                    }
	                }
                }
                // ===================== Field Number 26: End of Voucher segment validation =====================
                

                if (!fileValidationErrorExists) {
                    int index = 0;
                    batchOPTUserVO = new BatchOPTUserVO();
                    batchOPTUserVO.setRecordNumber(String.valueOf(r + 1));
                    batchOPTUserVO.setCategoryVO(batchOPTVO.getCategoryVO());
                    batchOPTUserVO.setNetworkID(userVO.getNetworkID());
                    batchOPTUserVO.setCategoryCode(batchOPTVO.getCategoryCode());
                    batchOPTUserVO.setParentID(userVO.getUserID());
                    batchOPTUserVO.setOwnerID(userVO.getOwnerID());
                    batchOPTUserVO.setCreatedBy(userVO.getUserID());
                    batchOPTUserVO.setCreatedOn(currentDate);
                    batchOPTUserVO.setModifiedBy(userVO.getUserID());
                    batchOPTUserVO.setModifiedOn(currentDate);
                    batchOPTUserVO.setUserType(PretupsI.OPERATOR_USER_TYPE);
                    batchOPTUserVO.setCreationType(PretupsI.BATCH_USR_CREATION_TYPE);
                    batchOPTUserVO.setBatchName(batchOPTVO.getBatchName());

                    batchOPTUserVO.setUserNamePrefix(excelArr[r][index].toUpperCase()); // Field
                                                                                        // Number
                                                                                        // 1:

                    // modified by deepika aggarwal
                    if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
                        batchOPTUserVO.setUserName(excelArr[r][++index]); // Field
                                                                          // Number
                                                                          // 2:
                    } else {
                        batchOPTUserVO.setFirstName(excelArr[r][++index]); // Field
                                                                           // Number
                                                                           // 2:
                        batchOPTUserVO.setLastName(excelArr[r][++index]); // Field
                                                                          // Number
                                                                          // 3:
                        if (!BTSLUtil.isNullString(batchOPTUserVO.getLastName())) {
                            batchOPTUserVO.setUserName(batchOPTUserVO.getFirstName() + " " + batchOPTUserVO.getLastName());
                        } else {
                            batchOPTUserVO.setUserName(batchOPTUserVO.getFirstName());
                        }
                    }
                    // end modified by deepika aggarwal

                    batchOPTUserVO.setLoginID(excelArr[r][++index]); // Field
                                                                     // Number
                                                                     // 3:
                    batchOPTUserVO.setPassword(BTSLUtil.encryptText(excelArr[r][++index])); // Field
                                                                                            // Number
                                                                                            // 4:
                    batchOPTUserVO.setMsisdn(excelArr[r][++index]); // Field
                                                                    // Number 5:
                    batchOPTUserVO.setEmpCode(excelArr[r][++index]); // Field
                                                                     // Number
                                                                     // 6:
                                                                     // Subscriber
                                                                     // Code
                    batchOPTUserVO.setStatus(excelArr[r][++index].toUpperCase()); // Field
                                                                                  // Number
                                                                                  // 7:
                    batchOPTUserVO.setDivisionCode(excelArr[r][++index].toUpperCase()); // Field
                                                                                        // Number
                                                                                        // 8:
                    batchOPTUserVO.setDepartmentCode(excelArr[r][++index].toUpperCase()); // Field
                                                                                          // Number
                                                                                          // 9:
                    ++index;
                    batchOPTUserVO.setGeographyArrList(geographyArr); // Field
                                                                      // Number
                                                                      // 10:
                    if (PretupsI.YES.equalsIgnoreCase(categoryVO.getProductTypeAllowed())) {
                        ++index;
                        batchOPTUserVO.setProductArrList(productArr); // Field
                                                                      // Number
                                                                      // 11:
                    }
                    ++index;
                    ++index;
                    batchOPTUserVO.setRolesArrList(roleArr); // Field Number 13:
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ))).booleanValue())
                    batchOPTUserVO.setAuthTypeAllowed(excelArr[r][++index]);
                    if (PretupsI.YES.equals(categoryVO.getDomainAllowed()) && PretupsI.DOMAINS_ASSIGNED.equals(categoryVO.getFixedDomains())) {
                        ++index;
                        batchOPTUserVO.setDomainArrList(domainArr); // Field
                                                                    // Number
                                                                    // 14:
                    }
                    batchOPTUserVO.setDesignation(excelArr[r][++index]); // Field
                                                                         // Number
                                                                         // 15:
                    batchOPTUserVO.setExternalCode(excelArr[r][++index]); // Field
                                                                          // Number
                                                                          // 16:
                    batchOPTUserVO.setContactNo(excelArr[r][++index]); // Field
                                                                       // Number
                                                                       // 17:
                    batchOPTUserVO.setSsn(excelArr[r][++index]); // Field Number
                                                                 // 18:
                    batchOPTUserVO.setAddress1(excelArr[r][++index]); // Field
                                                                      // Number
                                                                      // 19:
                    batchOPTUserVO.setAddress2(excelArr[r][++index]); // Field
                                                                      // Number
                                                                      // 20:
                    batchOPTUserVO.setCity(excelArr[r][++index]); // Field
                                                                  // Number 21:
                    batchOPTUserVO.setState(excelArr[r][++index]); // Field
                                                                   // Number 22:
                    batchOPTUserVO.setCountry(excelArr[r][++index]); // Field
                                                                     // Number
                                                                     // 23:
                    batchOPTUserVO.setEmail(excelArr[r][++index]); // Field
                                                                   // Number 24:
                    
                    if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
                    {
                    	++index;
                    	batchOPTUserVO.setVouchertypeArrList(voucherArr); // Field
                    }                                                 // Number
                                                                      // 25:
                    if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
                    {
                    	++index;
                    	batchOPTUserVO.setSegmentArrList(segmentArr); // Field Number 26
                    }
                    batchOPTUserVOList.add(batchOPTUserVO);
                }
            
            } // for loop end
            
            ArrayList dbErrorList = null;
            if (batchOPTUserVOList != null && batchOPTUserVOList.size() > 0) {
                dbErrorList = batchOPTUserDAO.addBatchOperatorUserList(con, batchOPTUserVOList, locale, userVO, fileName, fileName+"."+request.getFileType(), rows - rowOffset);
                con.commit();
            }
            String batchID = null;

            if (dbErrorList != null && dbErrorList.size() > 0) {
                int size = dbErrorList.size();
                ListValueVO errVO = (ListValueVO) dbErrorList.get(size - 1);
                batchID = errVO.getOtherInfo2();
                for(int i =0 ;i<dbErrorList.size();i++){
                	ListValueVO errVO1 = (ListValueVO) dbErrorList.get(i);
                	if(errVO1.getOtherInfo2().equals(batchID)){
                		dbErrorList.remove(i);
                	}
                }
//                dbErrorList.remove(size - 1);
                fileErrorList.addAll(dbErrorList);
            }
            
            Collections.sort(fileErrorList);
            response.setErrorList(fileErrorList);
            response.setErrorMap(errorMap);
            
            if (!fileErrorList.isEmpty()) {
            	response.setErrorList(fileErrorList);
                response.setTotalRecords(rows - rowOffset); // total
                // records
                int errorListSize = fileErrorList.size();
                response.setNoOfRecords(String.valueOf(rows - rowOffset - errorListSize + reptRowNo));
                for (int i = 0, j = errorListSize; i < j; i++) {
                	ListValueVO errorvo = (ListValueVO) fileErrorList.get(i);
  		            if(!BTSLUtil.isNullString(errorvo.getOtherInfo2()))
                    {
  		            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
  		            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
  		            	MasterErrorList masterErrorList = new MasterErrorList();
  						String msg = errorvo.getOtherInfo2();
  						masterErrorList.setErrorMsg(msg);
  						masterErrorLists.add(masterErrorList);
  						rowErrorMsgLists.setMasterErrorList(masterErrorLists);
  						rowErrorMsgLists.setRowValue("Line " + String.valueOf(Long.parseLong(errorvo.getOtherInfo())));
  						rowErrorMsgLists.setRowName(rowErrorMsgLists.getRowName());
  						if(errorMap.getRowErrorMsgLists() == null)
  							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
  						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
  					    
                    }
                }
            }
            
            Integer invalidRecordCount = fileErrorList.size() - reptRowNo;
    		ErrorFileResponse errorResponse = new ErrorFileResponse();
    		if(invalidRecordCount>0) {
        		downloadErrorLogFile(fileErrorList, userVO, response, responseSwag);
    		}
    		
    		//setting response
    		response.setBatchID(batchID);
            response.setTotalRecords(rows - rowOffset);
    		response.setValidRecords(rows - rowOffset - invalidRecordCount);
            response.setNoOfRecords(String.valueOf(rows - rowOffset - fileErrorList.size() + reptRowNo));
    		if(invalidRecordCount>0) {
    			if (invalidRecordCount<rows - rowOffset) { //partial failure
                    String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_PROCESSUPLOADEDFILE_MSG_PARTIAL_SUCCESS, new String[] { batchID });
        			response.setMessage(msg);
    				response.setStatus("400");
    				responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
    				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
    			}
    			else if(invalidRecordCount == rows - rowOffset) { //total failure
    				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS,new String[] {""});
        			response.setMessage(msg);
    				response.setStatus("400");
    				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
    				response.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
    			}
    		}
    		else {
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_PROCESSUPLOADEDFILE_MSG_SUCCESS, new String[] { batchID });
                response.setStatus("200");
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.USER_PROCESSUPLOADEDFILE_MSG_SUCCESS);
                responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
    			
    		}    		
            
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("processUploadedFile", "Exiting");
            }
        }
         

	}
	
	private LinkedHashMap<String, List<String>> getMapInFileFormat(LinkedHashMap<String, List<String>> bulkDataMap){
		
		LinkedHashMap<String, List<String>> fileDetailsMap = new LinkedHashMap<String, List<String>>();
		List<String> fileHeader = new ArrayList(bulkDataMap.keySet());
		fileDetailsMap.put("0", fileHeader);
		List<List<String>> listGroup = bulkDataMap.values().stream().collect(Collectors.toList());
		
		for(int row = 0; row < listGroup.get(0).size(); row++) {
			ArrayList<String> rows = new ArrayList<String>();
			
			for(int col =0;  col< fileHeader.size(); col++) {
				rows.add(listGroup.get(col).get(row));
			}
			String key = String.valueOf(row + 1);
			fileDetailsMap.put(key, rows);
		}
		return fileDetailsMap;
	}
	
    private ArrayList getRolesList(HashMap p_rolesMap) {

        if (log.isDebugEnabled()) {
            log.debug("getRolesList", "Entered: p_rolesMap size=" + p_rolesMap.size());
        }
        ArrayList userRolesVOList = new ArrayList();
        Set set = p_rolesMap.keySet();
        UserRolesVO userRolesVO = null;
        Iterator setItr = set.iterator();
        String key = null;
        ArrayList rolesList = null;
        while (setItr.hasNext()) {
            key = (String) setItr.next();
            rolesList = (ArrayList) p_rolesMap.get(key);
            Iterator listItr = rolesList.iterator();
            while (listItr.hasNext()) {
                userRolesVO = (UserRolesVO) listItr.next();
                userRolesVOList.add(userRolesVO);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("getRolesList", "Exited: userRolesVOList size()=" + userRolesVOList.size());
        }
        return userRolesVOList;
    }
    
	 public void downloadErrorLogFile(ArrayList errorList, UserVO userVO, BatchOperatorUserInitiateResponseVO response, HttpServletResponse responseSwag)
		{
		    final String METHOD_NAME = "downloadErrorLogFile";
		    Writer out =null;
		    File newFile = null;
	        File newFile1 = null;
	        String fileHeader=null;
	        Date date= new Date();
			if (log.isDebugEnabled())
				log.debug(METHOD_NAME, "Entered");
			try
			{
				String filePath = Constants.getProperty("DownloadErLogFilePath");
				try
				{
					File fileDir = new File(filePath);
					if(!fileDir.isDirectory())
						fileDir.mkdirs();
				}
				catch(Exception e)
				{			
					log.errorTrace(METHOD_NAME,e);
					log.error(METHOD_NAME,"Exception" + e.getMessage());
					throw new BTSLBaseException(this,METHOD_NAME,"bulkuser.processuploadedfile.downloadfile.error.dirnotcreated");
				}
				
				String _fileName = Constants.getProperty("BatchUSerCreationErLog")+BTSLUtil.getFileNameStringFromDate(new Date())+".csv";
			    String networkCode = userVO.getNetworkID();
			    newFile1=new File(filePath);
	            if(! newFile1.isDirectory())
	         	 newFile1.mkdirs();
	             String absolutefileName=filePath+_fileName;
	             fileHeader=Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");
	             
	             newFile = new File(absolutefileName);
	             out = new OutputStreamWriter(new FileOutputStream(newFile));
	             out.write(fileHeader +"\n");
	             for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {
	 				
	             	ListValueVO listValueVO =iterator.next();
	             		out.write(listValueVO.getOtherInfo()+",");
	                 	out.write(listValueVO.getOtherInfo2()+",");
	             	
	             	out.write(",");
	             	out.write("\n");
	             }
	 			out.write("End");
	 			out.close();
	 			File error =new File(absolutefileName);
				byte[] fileContent = FileUtils.readFileToByteArray(error);
		   		String encodedString = Base64.getEncoder().encodeToString(fileContent);	   		
		   		response.setFileAttachment(encodedString);
		   		response.setFileName(_fileName);
		   		response.setFileType("csv");
	 			
			}
			catch (Exception e)
			{
				log.error(METHOD_NAME,"Exception:e="+e);
				log.errorTrace(METHOD_NAME,e);
			}
			finally
	         {
	         	if (log.isDebugEnabled()){
	         		log.debug(METHOD_NAME,"Exiting... ");
	         	}
	             if (out!=null)
	             	try{
	             		out.close();
	             		}
	             catch(Exception e){
	            	 log.errorTrace(METHOD_NAME, e);
	             }
	             	
	         }
		}

}
