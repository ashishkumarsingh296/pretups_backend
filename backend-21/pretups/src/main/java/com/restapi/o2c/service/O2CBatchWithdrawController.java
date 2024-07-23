package com.restapi.o2c.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.spring.custom.action.Globals;
//import org.apache.struts.action.ActionForward;
import com.btsl.util.MessageResources;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchItemsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchO2CFileProcessLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.web.pretups.channel.transfer.businesslogic.O2CBatchWithdrawWebDAO;
import com.web.pretups.channel.transfer.web.O2CBatchWithdrawForm;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;


@Service("O2CBatchWithdrawServiceI")
public class O2CBatchWithdrawController implements O2CBatchWithdrawServiceI{
	
	protected final Log log = LogFactory.getLog(getClass().getName());
    private static OperatorUtilI calculatorI = null;
	private String filepathtemp;
	private O2CBatchWithdrawFileRequest request;
	O2CBatchWithdrawFileResponse response = null;
	 boolean processRunning = true;
	ProcessStatusVO processVO = null;
	C2CBatchItemsVO c2cBatchItemVO = null;
	 long batchDetailID = 0;
	 int errorSize=0;
	 ArrayList<MasterErrorList> inputValidations=null;
	 boolean fileExist=false;

	@Override
	public O2CBatchWithdrawFileResponse processRequest(
			O2CBatchWithdrawFileRequest o2CFileUploadApiRequest,String msisdn,OperatorUtilI calculator,Locale locale,Connection con,String geoDomain,String channelDomain,String userCategoryName,String product,String walletType, String serviceType,
			String requestIDStr, HttpServletRequest httprequest,
			MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException {

		final String methodName = "processRequest";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		String errorMessage=null;
		String errorMessageCode=null;
		int status;
		errorSize=0;
		processRunning = true;
        fileExist=false;
		this.request = o2CFileUploadApiRequest;
		LinkedHashMap<String, List<String>> bulkDataMap ;

		try {
			calculatorI = calculator;
			response = new O2CBatchWithdrawFileResponse();
			userCategoryName = SqlParameterEncoder.encodeParams(userCategoryName);
			UserGeographiesVO geographyVO = null;
			O2CBatchWithdrawForm theForm = new O2CBatchWithdrawForm();
			O2CBatchWithdrawController o2cBatchWithdrawController = new O2CBatchWithdrawController();
			UserVO userVO=o2cBatchWithdrawController.loadUserDetails(con,msisdn);
			//basic form validation at api level
        	inputValidations = new ArrayList<>();
			ProductTypeDAO productTypeDAO = new ProductTypeDAO();
		       String productShortCode = null;
        	ChannelUserVO channelUserVO=new ChannelUserVO();
			 
			channelUserVO = (ChannelUserVO) userVO;
				UserPhoneVO userPhoneVO = o2cBatchWithdrawController.loadUserPhoneVO(con, userVO.getUserID());
				channelUserVO.setUserPhoneVO(userPhoneVO);
			if(PretupsI.YES.equals(userVO.getCategoryVO().getSmsInterfaceAllowed())&& PretupsI.YES.equals(userPhoneVO.getPinRequired()))
			{
                if(BTSLUtil.isNullString(request.getPin()))
                {
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN,null);
					response.setStatus("400");
					  response.setService("o2cBatchWithdrawResp");
					  response.setMessage(msg);
					  response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
					 return response;
                }
                else
                	{
                	try {
					ChannelUserBL.validatePIN(con, channelUserVO,request.getPin());
				} catch (BTSLBaseException be) {
					if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
							|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
						OracleUtil.commit(con);
					}
					String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
					response.setStatus("400");
					  response.setService("o2cBatchWithdrawResp");
					  response.setMessage(msg);
					  response.setMessageCode(be.getMessageKey());
					  return response;
				}
                	}
			
			}
			
			
			
			 String pattern= "^[a-zA-Z]*$";
			 
			 if(BTSLUtil.isNullString(userCategoryName)) {
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 if(!BTSLUtil.isNullString(userCategoryName)) {
			 String noSpaceStr = userCategoryName.replaceAll("\\s", ""); // using built in method just to check for aplhanumeric  
			 if(!noSpaceStr.matches(pattern)){
				 MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_APLHA_CAT,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_APLHA_CAT);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 }
			 
		boolean isProd=false;
			 if(BTSLUtil.isNullString(product)){
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_PRODUCTCODE,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_PRODUCTCODE);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 else
			 {
					ArrayList <C2sBalanceQueryVO>prodList1 =productTypeDAO.getProductsDetails(con);
					for(C2sBalanceQueryVO prod1:prodList1) {
				    	  if(product.equalsIgnoreCase(prod1.getProductCode())) {
				    		  productShortCode=prod1.getProductShortCode();
				    		  theForm.setProductShortCode(productShortCode);
				    		  theForm.setProductMrp(Long.valueOf(prod1.getUnitValue()));
				    		  isProd=true;
				    		  break;
				    	  }
				       }
					if(!isProd)
					{

						 MasterErrorList masterErrorList = new MasterErrorList();
							String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PRODUCTS_NOT_FOUND,null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.PRODUCTS_NOT_FOUND);
							masterErrorList.setErrorMsg(msg);
							inputValidations.add(masterErrorList);
						
					}
			 }
			 if(BTSLUtil.isNullString(geoDomain)){
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 else
				 {
				 ArrayList <ListValueVO>geoList = null;
				 ArrayList<String> geoVList = null;
	            ArrayList userGeoList = new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
	            geoVList = new ArrayList<String>();
	            if (userGeoList != null) {
	                if (userGeoList.size() == 1) {
	                    geographyVO = (UserGeographiesVO) userGeoList.get(0);
	                    theForm.setGeographicalDomainCode(geographyVO.getGraphDomainCode());
	                    theForm.setGeographicalDomainCodeDesc(geographyVO.getGraphDomainName());
	                    geoVList.add(geographyVO.getGraphDomainCode());
	                    } else {
	                    geoList = new ArrayList<ListValueVO>();
	                    for (int i = 0, k = userGeoList.size(); i < k; i++) {
	                        geographyVO = (UserGeographiesVO) userGeoList.get(i);
	                        geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
	                        geoVList.add(geographyVO.getGraphDomainCode());
	                    }
	                    theForm.setGeographicalDomainList(geoList);
	                }
	            }
	            if(!geoVList.contains(geoDomain)&&!geoDomain.equals("ALL"))
	            {
	            MasterErrorList masterErrorList = new MasterErrorList();
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY,null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
	            }
				 }
			 if(BTSLUtil.isNullString(channelDomain)){
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_DOMAIN,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.INVALID_DOMAIN);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 if ( BTSLUtil.isNullString(walletType))  {
	                theForm.setWalletType(PretupsI.SALE_WALLET_TYPE);
	                walletType=PretupsI.SALE_WALLET_TYPE;
	            }
	 
				if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
					theForm.setWalletTypeList(LookupsCache.loadLookupDropDown(PretupsI.MULTIPLE_WALLET_TYPE, true));
					if (BTSLUtil.isNullString(walletType)) {
						MasterErrorList masterErrorList = new MasterErrorList();
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.WALLETTYPE_REQUIRED, null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.WALLETTYPE_REQUIRED);
						masterErrorList.setErrorMsg(msg);
						inputValidations.add(masterErrorList);
					} else {
						boolean flag2 = true;
						for (int k = 0; k < theForm.getWalletTypeList().size(); k++) {
							if (((ListValueVO) theForm.getWalletTypeList().get(k)).getValue().equals(walletType)) {
								flag2 = false;
							}
						}
						if (flag2 == true) {
							MasterErrorList masterErrorList = new MasterErrorList();
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.WALLETTYPE_REQUIRED,
									null);
							masterErrorList.setErrorCode(PretupsErrorCodesI.WALLETTYPE_REQUIRED);
							masterErrorList.setErrorMsg(msg);
							inputValidations.add(masterErrorList);
						}
					}
				}
			 if (!SystemPreferences.MULTIPLE_WALLET_APPLY) {
	                theForm.setWalletType(PretupsI.SALE_WALLET_TYPE);
	                walletType=PretupsI.SALE_WALLET_TYPE;
	            }
			 

			 requestValidation();
			
			 /*
				 * request input validation
				 */

				//throwing list of basic form errors
				 if(!BTSLUtil.isNullOrEmptyList(inputValidations)) {
				  response.setStatus("400");
				  response.setService("o2cBatchWithdrawResp");
				  response.setErrorMap(new ErrorMap());
				  response.getErrorMap().setMasterErrorList(inputValidations);
				  return response;
				 }

		      
		        if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO.getOutSuspened())) {
		            if (log.isDebugEnabled()) {
		                log.debug("userSearch", "USER IS OUT SUSPENDED IN THE SYSTEM");
		            }

					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OUT_SUSPENDED, PretupsI.RESPONSE_FAIL, null);

		        }
		       
			 
			/*
			 * Uploading and validating file
			 */
		   	//code for read file content
				ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
				ErrorMap errorMap = new ErrorMap();
				HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
				fileDetailsMap.put(PretupsI.FILE_TYPE1, o2CFileUploadApiRequest.getFileType());
				fileDetailsMap.put(PretupsI.FILE_NAME,o2CFileUploadApiRequest.getFileName());
				fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, o2CFileUploadApiRequest.getFileAttachment());
				fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, "o2cBatchWithdraw");
				ArrayList batchItemsList = new ArrayList();
				// Check external txn id for domain type
	            final String externalTxnMandatoryDomainType = SystemPreferences.EXTERNAL_TXN_MANDATORY_DOMAINTYPE;
	            final String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORO2C;
	            final boolean externalCodeMandatory = SystemPreferences.EXTERNAL_CODE_MANDATORY_FORO2C;
	            String externalsTxnMandatory =null;
	            // load the selected domain type
	            if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("0") != -1) {

	                if (BTSLUtil.isNullString(externalTxnMandatoryDomainType)) {
	                	externalsTxnMandatory=PretupsI.YES;
	                } else {
	                    final String domainTypeArr[] = externalTxnMandatoryDomainType.split(",");
	                    int domainTypeArray=domainTypeArr.length;
	                    for (int i = 0, j =domainTypeArray ; i < j; i++) {
	                        if (channelDomain.equals(domainTypeArr[i])) {
	                        	externalsTxnMandatory=PretupsI.YES;
	                            break;
	                        }
	                    }
	                }
	            }
	            
				bulkDataMap = fileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 0, errorMap,externalCodeMandatory,externalsTxnMandatory,batchItemsList);
				
				/*
				 * ArrayList fileContents = getFileContentsList(bulkDataMap); int records =
				 * fileContents.size();
				 */
				
				//response.setNumberOfRecords(totalRecords);
				// /
	            // If file does not contain record as entered by the user then
	            // show the error message
	            // (records excludes blank lines)
	            // /
				if(!BTSLUtil.isNullorEmpty(errorMap.getRowErrorMsgLists()))
				{
					response.setErrorMap(errorMap);
					writeToFileError(response);
					response.setStatus("400");
					response.setService("o2cBatchWithdrawResp");
//					response.setMessage("O2C Batch Withdraw not successful.Kindly correct error to proceed");
					response.setMessage(RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.O2C_BATCH_WITHDRAW_FAIL, null));
				}
				else
				{
					List<String> filePath= bulkDataMap.get("filepathtemp");
					String filePaths=filePath.get(0);
					filepathtemp=filePaths;
					processUploadedFile(con, theForm,userVO,geoDomain,channelDomain,userCategoryName,walletType,product, o2CFileUploadApiRequest, httprequest, batchItemsList);
					if(BTSLUtil.isNullString(response.getMessage()))
					{
					response.setStatus("200");
					response.setBatchID(theForm.getBatchId());
					response.setMessageCode(errorMessageCode);
					response.setMessage("BATCH "+theForm.getBatchId()+" generated .Total "+theForm.getTotalRecords()+" records processed successfully ");
					response.setReferenceId(theForm.getTotalRecords());
					response.setService("o2cBatchWithdrawResp");
					}
				}
          
		} catch (BTSLBaseException be) {
			if(!fileExist)
			filedelete();
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String [] args=null;
			if(!BTSLUtil.isNullorEmpty(be.getArgs())) {
				args = be.getArgs();
			}
			if(!BTSLUtil.isNullorEmpty(be.getMessageKey())) {
				errorMessage = RestAPIStringParser.getMessage(locale,
						be.getMessageKey(), args);
				errorMessageCode = be.getMessageKey();

				
			}
			if(!BTSLUtil.isNullorEmpty(be.getErrorCode()) &&  be.getErrorCode() !=0) {
				status = be.getErrorCode();
				} else {
					status= 400;
				}
		response.setStatus(String.valueOf(status));
		response.setMessageCode(errorMessageCode);
		response.setMessage(errorMessage);
		response.setService("o2cBatchWithdrawResp");
		
		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			response.setMessageCode("error.general.processing");
			response.setMessage("Check File Type supplied.");
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			 
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
		// log.debug(methodName, response);
		return response;

	}
	
	/**
	 * Validates the name of the file being uploaded
	 * 
	 * @param fileName
	 * @return boolean
	 * @throws BTSLBaseException 
	 */
	public UserVO loadUserDetails(Connection con,String msisdn) throws BTSLBaseException {
		return new UserDAO().loadUsersDetails(con, msisdn);
	}
	public UserPhoneVO loadUserPhoneVO(Connection con,String userID) throws BTSLBaseException {
		return new UserDAO().loadUserPhoneVO(con, userID);
	}

	public  static boolean isValideFileName(String fileName) {
		boolean isValidFileContent = true;
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			isValidFileContent = false;
		}
		return isValidFileContent;
	}

	 /**
     * Method processUploadedFile.
     * This method is called to validate/process the XLS fle
     * 
     * @param mapping
     *            ActionMapping
     * @param form
     *            ActionForm
     * @param request
     *            HttpServletRequest
     * @return ActionForward
     */

    private O2CBatchWithdrawFileResponse processUploadedFile(Connection p_con,O2CBatchWithdrawForm theForm, UserVO userVOreq,String geoDomainReq,String channelDomain,String userCategoryName,String walletType,String product,O2CBatchWithdrawFileRequest o2CFileUploadApiRequest ,HttpServletRequest request,ArrayList batchItemsList) {
        final String METHOD_NAME = "processUploadedFile";
        if (log.isDebugEnabled()) {
           log.debug("processUploadedFile", "Entered");
        }
        //ActionForward forward = null;
        ProcessStatusVO processVO = null;
        boolean processRunning = true;
        final HashMap<String, String> map = new HashMap<String, String>();
        ChannelUserWebDAO channelUserWebDAO = null;
        O2CBatchWithdrawWebDAO o2CBatchWithdrawDAO= null;
        ArrayList<RowErrorMsgLists> rowErrorMsgListsFinal = new ArrayList<>();
        try {
            channelUserWebDAO = new ChannelUserWebDAO();
            o2CBatchWithdrawDAO = new O2CBatchWithdrawWebDAO();
            Locale locale=new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

            // as to check the status of the batch O2C process into the table so
            // that only
            // one instance should be executed for batch O2C
            final ProcessBL processBL = new ProcessBL();
            UserVO userVO1 = userVOreq;
            try {
                processVO = processBL.checkProcessUnderProcessNetworkWise(p_con, PretupsI.O2C_BATCH_PROCESS_ID,userVO1.getNetworkID());
            } catch (BTSLBaseException e) {
                log.error("processUploadedFile", "Exception:e=" + e);
                log.errorTrace(METHOD_NAME, e);
                processRunning = false;
                throw new BTSLBaseException(this, "processUploadedFile", "batcho2c.processfile.error.alreadyexecution", "initiateBatchO2CWithdraw");
            }
            if (processVO != null && !processVO.isStatusOkBool()) {
                processRunning = false;
                throw new BTSLBaseException(this, "processUploadedFile", "batcho2c.processfile.error.alreadyexecution", "initiateBatchO2CWithdraw");
            }
            p_con.commit();
            processVO.setNetworkCode(userVO1.getNetworkID());
            // ends here


            final Date curDate = new Date();
            
            final UserVO userVO = userVOreq;
            
            final FOCBatchMasterVO batchMasterVO = new FOCBatchMasterVO();

            batchMasterVO.setNetworkCode(userVO.getNetworkID());
            batchMasterVO.setNetworkCodeFor(userVO.getNetworkID());
            batchMasterVO.setBatchName(o2CFileUploadApiRequest.getBatchName());
            batchMasterVO.setStatus(PretupsI.CT_BATCH_O2C_STATUS_UNDERPROCESS);
            batchMasterVO.setCreatedBy(userVO.getUserID());
            batchMasterVO.setCreatedOn(curDate);
            batchMasterVO.setModifiedBy(userVO.getUserID());
            batchMasterVO.setModifiedOn(curDate);
            batchMasterVO.setDomainCode(channelDomain);
            batchMasterVO.setProductCode(product);
            batchMasterVO.setProductMrp(theForm.getProductMrp());
            batchMasterVO.setBatchFileName(o2CFileUploadApiRequest.getFileName());
            batchMasterVO.setBatchDate(curDate);
            batchMasterVO.setDefaultLang(o2CFileUploadApiRequest.getLanguage1());
            batchMasterVO.setSecondLang(o2CFileUploadApiRequest.getLanguage2());
            batchMasterVO.setWallet_type(walletType);
            theForm.setWalletType(walletType);
            theForm.setGeographicalDomainCode(geoDomainReq);
            this.genrateO2CBatchMasterTransferID(batchMasterVO);

            O2CBatchItemsVO batchItemsVO = null;

            ListValueVO errorVO = null;
            boolean isValidationError = false;
            ArrayList<ListValueVO> fileErrorList = null;
            theForm.setErrorFlag("false");
            int blankLines = 0;
           
            long batchDetailID = 0;
            fileErrorList = new ArrayList<ListValueVO>();
                int rows = batchItemsList.size();
                // 2D array processing starts here..
                for (int r = 0; r < batchItemsList.size(); r++) {
                        batchItemsVO = (O2CBatchItemsVO)batchItemsList.get(r);
                        batchItemsVO.setBatchId(batchMasterVO.getBatchId());
                        batchItemsVO.setBatchDetailId(this.genrateO2CBatchDetailTransferID(batchMasterVO.getBatchId(), ++batchDetailID));
                        batchItemsVO.setModifiedBy(userVO.getUserID());

                } // end of for loop
                  // If fileValidations Error not exits call the DAO method else
                  // stop processing..
                  // by showing err logs
               
            // =====================Upto here file has been processed now do the
            // database operations================

            // validate user's information
            String geoDomain = geoDomainReq;

            if (geoDomain.equals(PretupsI.ALL)) {
                geoDomain = this.generateCommaString(theForm.getGeographicalDomainList());
            } else {
                geoDomain = "'" + geoDomain + "'";
            }

            String category = "'"+userCategoryName+"'";

            String catArr[] = new String[1];
            if (category.equals(PretupsI.ALL)) {
                category = this.generateCommaString(theForm.getCategoryList());
            } else {
				/*
				 * catArr = category.split(":"); category = "'" + catArr[1] + "'";
				 */
            }

            theForm.setCommPrfApplicableDate(curDate);
            
            // user geography validation
			fileErrorList=o2CBatchWithdrawDAO.validateUsersForBatchO2CWithdraw(p_con,batchItemsList,channelDomain,category,userVO.getNetworkID(),geoDomain,((MessageResources) request.getAttribute(Globals.MESSAGES_KEY)), locale);

            
            if (fileErrorList != null && !fileErrorList.isEmpty()) {
 
            	
            	
                for (int i = 0; i < fileErrorList.size(); i++) {
                	MasterErrorList masterErrorList = new MasterErrorList();
                	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                	errorVO = fileErrorList.get(i);
                	rowErrorMsgLists.setRowName(errorVO.getCodeName());
					rowErrorMsgLists.setRowValue(String.valueOf(Long.parseLong(errorVO.getOtherInfo()) + 1));
					masterErrorList.setErrorMsg(errorVO.getOtherInfo2());
					masterErrorLists.add(masterErrorList);
					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
					rowErrorMsgListsFinal.add(rowErrorMsgLists);
                    
                    errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                    fileErrorList.set(i, errorVO);
                }
 
                theForm.setErrorList(fileErrorList);
                theForm.setErrorFlag("true");
                theForm.setTotalRecords((rows - blankLines)); // total
                // records
                theForm.setNoOfRecords(String.valueOf(fileErrorList.size()));
               
                filedelete();
                ErrorMap p= new ErrorMap();
            	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
                response.setErrorMap(p);
                writeToFileError(response);
                response.setStatus("400");
                response.setMessage("Some Records contain error.Kindly correct them");
                response.setService("o2cBatchWithdrawResp");
                return response;
            }

            // Load the User Detail based on the MSISDN or LoginId
            ChannelUserVO channelUserVO = null;
            final List<O2CBatchItemsVO> batchO2CList = new ArrayList<O2CBatchItemsVO>();

            for (int i = 0, j = batchItemsList.size(); i < j; i++) {

                batchItemsVO = (O2CBatchItemsVO) batchItemsList.get(i);
                // added by harsh
                if (!BTSLUtil.isNullString(batchItemsVO.getMsisdn()) || !BTSLUtil.isNullString(batchItemsVO.getLoginId())) {
                    // {
                    channelUserVO = channelUserWebDAO.loadChannelUserDetailsByLoginIDANDORMSISDN(p_con, batchItemsVO.getMsisdn(), batchItemsVO.getLoginId());
                }
                // }
                // end added by
                // Load the User Detail based on the MSISDN or LoginId here

                
                // validate the user
                if (channelUserVO != null) {
                    // validate the domain list of user :: added by harsh
                    final ArrayList domainList = userVO.getDomainList();
                    boolean found = false;
                    int domainLists=domainList.size();
                    for (int l = 0; l <domainLists ; l++) {

                        final ListValueVO listVO = (ListValueVO) domainList.get(l);
                        if (listVO.getValue().equalsIgnoreCase(channelUserVO.getDomainID())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
        						.getMessageString("batchO2C.processuploadedfile.error.domainnotexist"));
                        fileErrorList.add(errorVO);
                        isValidationError = true;
                        BatchO2CFileProcessLog.o2cBatchItemLog("processUploadedFile", batchItemsVO, "FAIL : User is not active", "Batch O2C Initiate");
                    }
                    // end added by
                    if (!userVO.getNetworkID().equalsIgnoreCase(channelUserVO.getNetworkID())) {

                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
        						.getMessageString("batchO2C.processuploadedfile.error.usernotinnetwork"));
                        fileErrorList.add(errorVO);
                        isValidationError = true;
                        BatchO2CFileProcessLog.o2cBatchItemLog("processUploadedFile", batchItemsVO, "FAIL : User is not active", "Batch O2C Initiate");
                        continue;
                    }// checks whether the commission profile associated with
                     // the user is active with reason
                    else if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCommissionProfileStatus())) {
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(batchItemsVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(batchItemsVO.getRecordNumber()));

                        // which language message to be set is determined from
                        // the locale master table for the requested locale
                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(locale)).getMessage())) {
                            errorVO.setOtherInfo2(PretupsRestUtil
            						.getMessageString("batchfoc.processuploadedfile.error.comprfinactive",
                                new String[] {channelUserVO.getCommissionProfileLang1Msg()}));
                        } else {
                            errorVO.setOtherInfo2(PretupsRestUtil
            						.getMessageString("batchfoc.processuploadedfile.error.comprfinactive",
            								new String[] { channelUserVO.getCommissionProfileLang2Msg()}));
                        }
                        fileErrorList.add(errorVO);
                        isValidationError = true;
                        continue;

                    }// checks if the transfer profile associated with the user
                     // is active or not
                    else if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getTransferProfileStatus())) {

                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
        						.getMessageString("batcho2c.processuploadedfile.error.trfprfsuspended"));
                        fileErrorList.add(errorVO);
                        isValidationError = true;
                        continue;

                    } else if (!PretupsI.NO.equals(channelUserVO.getInSuspend())) {
                        // put error user is in suspended
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
        						.getMessageString("batcho2c.processuploadedfile.error.userinsuspend"));
                        fileErrorList.add(errorVO);
                        isValidationError = true;
                        continue;

                    }
                    // end of if-else-if
                    // End of validation if channelUserVO is not null
                    else {
                        final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
                        // the call to the method loads the transfer rule
                        // between the Operator and the passed category code for
                        // the domain and network.
                        final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(p_con, channelUserVO.getNetworkID(), channelUserVO
                            .getDomainID(), PretupsI.CATEGORY_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

                        if (channelTransferRuleVO == null) {
                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
            						.getMessageString("batcho2c.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                            fileErrorList.add(errorVO);
                            isValidationError = true;
                            continue;
                        }// checks if the withdraw is allowed or not
                        else {
                            if (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getWithdrawAllowed())) {

                                errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
                						.getMessageString("batcho2c.initiatebatchfoctransfer.msg.error.withnotallowed"));
                                fileErrorList.add(errorVO);
                                isValidationError = true;
                                continue;

                            } else if (channelTransferRuleVO.getProductVOList() == null || channelTransferRuleVO.getProductVOList().isEmpty()) {
                                errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
                						.getMessageString("batcho2c.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                                fileErrorList.add(errorVO);
                                isValidationError = true;
                                continue;
                            }
                        } // end of second if-else-if

                        batchItemsVO.setChannelUserVO(channelUserVO);
                        // added by harsh to process records with login id only
                        if (BTSLUtil.isNullString(batchItemsVO.getMsisdn())) {
                            if (!BTSLUtil.isNullString(channelUserVO.getMsisdn())) {
                                batchItemsVO.setMsisdn(channelUserVO.getMsisdn());
                            }
                        }
                        // end added by
                        // Construct the list with user details
                        batchO2CList.add(batchItemsVO);
                    }
                }// if channelUserVO is null
                else {
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
    						.getMessageString("batcho2c.processuploadedfile.error.usernotexist"));
                    fileErrorList.add(errorVO);
                    isValidationError = true;

                    continue;
                }
            }// End of loop

            // end of User Detail

            if (fileErrorList != null && !fileErrorList.isEmpty()) {

            	
            	
                for (int i = 0; i < fileErrorList.size(); i++) {
                	MasterErrorList masterErrorList = new MasterErrorList();
                	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                	errorVO = fileErrorList.get(i);
                	rowErrorMsgLists.setRowName(errorVO.getCodeName());
					rowErrorMsgLists.setRowValue("Line "+String.valueOf(Long.parseLong(errorVO.getOtherInfo()) + 1));
					masterErrorList.setErrorMsg(errorVO.getOtherInfo2());
					masterErrorLists.add(masterErrorList);
					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
					rowErrorMsgListsFinal.add(rowErrorMsgLists);
                    errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                    fileErrorList.set(i, errorVO);
                }
                theForm.setErrorList(fileErrorList);
                theForm.setErrorFlag("true");
                theForm.setTotalRecords((rows - blankLines) ); // total
                // records
                theForm.setNoOfRecords(String.valueOf(fileErrorList.size()));
                // Delete the file form the uploaded path if file validations
                // are failed.

                filedelete();
                ErrorMap p= new ErrorMap();
            	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
            	response.setErrorMap(p);
            	writeToFileError(response);
            	 response.setStatus("400");
                 response.setMessage("Some Records contain error.Kindly correct them");
                 response.setService("o2cBatchWithdrawResp");

            } else {
                batchMasterVO.setBatchTotalRecord(batchItemsList.size());

                final ArrayList geoList1 = new ArrayList();
                if (theForm.getGeographicalDomainList() != null && theForm.getGeographicalDomainList().size() > 1 && PretupsI.ALL.equals(theForm.getGeographicalDomainCode())) {
                    batchMasterVO.setGeographyList(theForm.getGeographicalDomainList());
                } else {
                    geoList1.add(new ListValueVO(theForm.getGeographicalDomainCodeDesc(), theForm.getGeographicalDomainCode()));
                    batchMasterVO.setGeographyList(geoList1);
                }

                fileErrorList = processBatchO2CWithdraw(errorVO, theForm, request, p_con, userVO, isValidationError, batchO2CList, batchMasterVO, batchItemsList,
                    ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY)), locale);

                // Update the batch total records..
                final int fileErrSize = fileErrorList.size();

                final String[] arr = { batchMasterVO.getBatchId(), theForm.getBatchName(), String.valueOf(batchItemsList.size() - fileErrorList.size()) };
                if (fileErrorList == null || fileErrSize == 0) {
                    // give success message on the first screen with batchID,
                    // batchName, total number of records
                    BatchO2CFileProcessLog.o2cBatchMasterLog("processUploadedFile", batchMasterVO, "PASS : Batch generated successfully",
                        "TOTAL RECORDS=" + arr[2] + ", FILE NAME=" + batchMasterVO.getBatchFileName());
                    theForm.setBatchId(batchMasterVO.getBatchId());
                    theForm.setTotalRecords(Integer.valueOf( String.valueOf(batchItemsList.size() - fileErrorList.size())));
                } else {
                    // give success message on the confirmation screen with
                    // batchID, batchName, total number of success records
                    // and give link to view the errors.
                	
                	
                	
                    for (int i = 0; i < fileErrorList.size(); i++) {
                    	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
                    	MasterErrorList masterErrorList = new MasterErrorList();
                    	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
                    	errorVO = fileErrorList.get(i);
                    	rowErrorMsgLists.setRowName(errorVO.getCodeName());
    					rowErrorMsgLists.setRowValue("Line "+String.valueOf(Long.parseLong(errorVO.getOtherInfo()) + 1));
    					masterErrorList.setErrorMsg(errorVO.getOtherInfo2());
    					masterErrorLists.add(masterErrorList);
    					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
    					rowErrorMsgListsFinal.add(rowErrorMsgLists);
                        //errorVO = fileErrorList.get(i);
                        errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                        fileErrorList.set(i, errorVO);
                    }
                    theForm.setBatchId(batchMasterVO.getBatchId());
                    theForm.setErrorList(fileErrorList);
                    theForm.setErrorFlag("true");
                    theForm.setTotalRecords((rows - blankLines));
                    theForm.setNoOfRecords(String.valueOf(fileErrorList.size())); // error
                    // recs
                    
                    
                    if (theForm.getTotalRecords() == fileErrorList.size()) // If
                    // all
                    // the
                    // records
                    // contains
                    // error
                    // in
                    // db
                    {
                        // Delete the file form the uploaded path if file
                        // validations are failed.
                       filedelete();

                        BatchO2CFileProcessLog.o2cBatchMasterLog("processUploadedFile", batchMasterVO, "FAIL : All records contains DB error in batch",
                            "TOTAL RECORDS=0, FILE NAME=" + batchMasterVO.getBatchFileName());
                        response.setMessage("FAIL : All records contains DB error in batch"+
                            "TOTAL RECORDS=0, FILE NAME=" + batchMasterVO.getBatchFileName());
                        ErrorMap p= new ErrorMap();
                    	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
                    	response.setErrorMap(p);
                    	writeToFileError(response);
                         response.setStatus("400");
                         response.setMessage("All Records contain error.Kindly correct them");
                         response.setService("o2cBatchWithdrawResp");
                         return response;
                    } else {
                        BatchO2CFileProcessLog.o2cBatchMasterLog("processUploadedFile", batchMasterVO, "PASS : Batch generated successfully",
                            "TOTAL RECORDS=" + arr[2] + ", FILE NAME=" + batchMasterVO.getBatchFileName());
                        int processRec=theForm.getTotalRecords()-Integer.valueOf(theForm.getNoOfRecords());
                        ErrorMap p= new ErrorMap();
                    	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
                    	response.setErrorMap(p);
                    	writeToFileError(response);
                        response.setStatus("400");
                        response.setBatchID(theForm.getBatchId());
    					response.setMessageCode("");
    					response.setMessage("BATCH "+theForm.getBatchId()+" generated.Records "+processRec+" out of "+String.valueOf(theForm.getTotalRecords())+" processed succesfully");
    					response.setService("o2cBatchWithdrawResp");
                    }
                }

            }

        } catch (BTSLBaseException be)
        {
			if(!fileExist)
			filedelete();
			log.error(METHOD_NAME, "BTSL BaseException:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			String [] args=null;
			if(!BTSLUtil.isNullorEmpty(be.getArgs())) {
				args = be.getArgs();
			}
			String errorMessage = null;
			String errorMessageCode = null;
			int status ;
			if(!BTSLUtil.isNullorEmpty(be.getMessageKey())) {
				errorMessage = PretupsRestUtil
						.getMessageString(
						be.getMessageKey(), args);
				errorMessageCode = be.getMessageKey();

				
			}
			if(!BTSLUtil.isNullorEmpty(be.getErrorCode()) &&  be.getErrorCode() !=0) {
				status = be.getErrorCode();
				} else {
					status= 400;
				}
		response.setStatus(String.valueOf(status));
		response.setMessageCode(errorMessageCode);
		response.setMessage(errorMessage);
		response.setService("o2cBatchWithdrawResp");
		
		}
        catch (Exception e) {
            // Delete the file form the uploaded path if file validations are
            // failed.
            filedelete();
            if(!BTSLUtil.isNullOrEmptyList(rowErrorMsgListsFinal))
            {
            	ErrorMap p= new ErrorMap();
            	p.setRowErrorMsgLists(rowErrorMsgListsFinal);
            	response.setErrorMap(p);
            }
            log.error("processUploadedFile", "Exceptin:e=" + e);
            log.errorTrace(METHOD_NAME, e);
        } finally {

            // as to make the status of the batch O2C process as complete into
            // the table so that only
            // one instance should be executed for batch O2C

            if (processRunning) {
                try {
                    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetailNetworkWise(p_con, processVO) > 0) {
                        p_con.commit();
                    } else {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.error("processUploadedFile", " Exception in update process detail for batch O2C initiation " + e.getMessage());
                    }
                    log.errorTrace(METHOD_NAME, e);
                }
            } else // delete the uploaded file
            {
                filedelete();
            }
            // ends here
            if (log.isDebugEnabled()) {
               // log.debug("processUploadedFile", "Exiting:forward=" + forward);
            }
        }
        return response;
    }

			
	    /**
	     * Method genrateO2CBatchMasterTransferID.
	     * This method is called generate O2C batch master transferID
	     * 
	     * @param p_currentDate
	     *            Date
	     * @param p_networkCode
	     *            String
	     * @throws BTSLBaseException
	     * @return
	     */

	    private void genrateO2CBatchMasterTransferID(FOCBatchMasterVO p_batchMasterVO) throws BTSLBaseException {
	        final String METHOD_NAME = "genrateO2CBatchMasterTransferID";
	        if (log.isDebugEnabled()) {
	            log.debug("genrateO2CBatchMasterTransferID", "Entered p_batchMasterVO=" + p_batchMasterVO);
	        }
	        try {
	            final long txnId = IDGenerator.getNextID(PretupsI.O2C_BATCH_TRANSACTION_ID, BTSLUtil.getFinancialYear(), p_batchMasterVO.getNetworkCode(), p_batchMasterVO
	                .getCreatedOn());
	            p_batchMasterVO.setBatchId(calculatorI.formatO2CBatchMasterTxnID(p_batchMasterVO, txnId));
	        } catch (Exception e) {
	            log.error("genrateO2CBatchMasterTransferID", "Exception " + e.getMessage());
	            log.errorTrace(METHOD_NAME, e);
	            throw new BTSLBaseException("O2CBatchWithdrawAction", "genrateO2CBatchMasterTransferID", PretupsErrorCodesI.ERROR_EXCEPTION);
	        } finally {
	            if (log.isDebugEnabled()) {
	                log.debug("genrateO2CBatchMasterTransferID", "Exited  " + p_batchMasterVO.getBatchId());
	            }
	        }
	        return;
	    }
	    /**
	     * 
	     * @param p_list
	     * @return
	     * @throws BTSLBaseException 
	     * @throws Exception
	     */

	    private String generateCommaString(ArrayList p_list) throws BTSLBaseException {
	        final String METHOD_NAME = "generateCommaString";
	        if (log.isDebugEnabled()) {
	            log.debug("generateCommaString", "Entered p_list=" + p_list);
	        }
	        String commaStr = "";
	        String catArr[] = new String[1];
	        String listStr = null;
	        try {
	            final int size = p_list.size();
	            ListValueVO listVO = null;
	            for (int i = 0; i < size; i++) {
	                listVO = (ListValueVO) p_list.get(i);
	                listStr = listVO.getValue();
	                if (listStr.indexOf(":") != -1) {
	                    catArr = listStr.split(":");
	                    listStr = catArr[1]; // for category code
	                }
	                commaStr = commaStr + "'" + listStr + "',";
	            }
	            commaStr = commaStr.substring(0, commaStr.length() - 1);
	        } catch (Exception e) {
	            log.error("generateCommaString", "Exceptin:e=" + e);
	            log.errorTrace(METHOD_NAME, e);
	            throw new BTSLBaseException(this, METHOD_NAME, "");
	        } finally {
	            if (log.isDebugEnabled()) {
	                log.debug("generateCommaString", "Exited commaStr=" + commaStr);
	            }
	        }
	        return commaStr;
	    }
	    private String genrateO2CBatchDetailTransferID(String p_batchMasterID, long p_tempNumber) throws BTSLBaseException {
	        final String METHOD_NAME = "genrateO2CBatchDetailTransferID";
	        if (log.isDebugEnabled()) {
	            log.debug("genrateO2CBatchDetailTransferID", "Entered p_batchMasterID=" + p_batchMasterID + ", p_tempNumber= " + p_tempNumber);
	        }
	        String uniqueID = null;
	        try {
	            uniqueID = calculatorI.formatFOCBatchDetailsTxnID(p_batchMasterID, p_tempNumber);
	        } catch (Exception e) {
	            log.error("genrateO2CBatchDetailTransferID", "Exception " + e.getMessage());
	            log.errorTrace(METHOD_NAME, e);
	            throw new BTSLBaseException("O2CBatchWithdrawAction", "genrateO2CBatchDetailTransferID", PretupsErrorCodesI.ERROR_EXCEPTION);
	        } finally {
	            if (log.isDebugEnabled()) {
	                log.debug("genrateO2CBatchDetailTransferID", "Exited  " + uniqueID);
	            }
	        }
	        return uniqueID;
	    }
	    /**
	     * 
	     * @param request
	     * @param p_con
	     * @param batchItemsList
	     * @param p_networkID
	     * @param p_userId
	     * @throws BTSLBaseException 
	     * @throws ParseException 
	     * @throws SQLException 
	     */

	    private ArrayList processBatchO2CWithdraw(ListValueVO errorVO, O2CBatchWithdrawForm p_theForm, HttpServletRequest request, Connection p_con, UserVO p_userVO, boolean p_isValidationError, List p_batchO2CList, FOCBatchMasterVO p_batchMasterVO, ArrayList p_batchItemsList, MessageResources p_messages, Locale p_locale) throws BTSLBaseException, SQLException, ParseException {

	        final String METHOD_NAME = "processBatchO2CWithdraw";
	        final boolean isValidationError = false;
	        ArrayList errorList = null;
			/*
			 * final Date currentDate = new Date(); ArrayList filteredPrdList = null;
			 * ChannelTransferItemsVO channelTransferItemsVO = null; ChannelTransferVO
			 * channelTransferVO = null; ChannelUserVO channelUserVO = null; O2CBatchItemsVO
			 * batchItemsVO = null;
			 * 
			 * ArrayList batchItemsList = null;
			 */

	        try {

	            final O2CBatchWithdrawWebDAO batchWithdrawWebDAO = new O2CBatchWithdrawWebDAO();
	            errorList = new ArrayList();
	            if (!isValidationError) {

	            	/*HashMap<Object, Object> productMap = null;
	                batchItemsList = new ArrayList();
					
					 * final int listSize = p_batchO2CList.size(); for (int m = 0, n = listSize; m <
					 * n; m++) {}// End of for loop
					 */
	               /* if (errorList == null || errorList.isEmpty()) {*/
	                    errorList = batchWithdrawWebDAO.initiateBatchO2CTransferRest(p_con,p_theForm,p_userVO, p_batchMasterVO, p_batchO2CList, p_messages, p_locale);
						/* } */
	                p_con.commit();

	            }// If end isValidationError

	        } catch (Exception e) {

	            log.error("processBatchO2CWithdraw", "Exception " + e.getMessage());
	            log.errorTrace(METHOD_NAME, e);
	            throw new BTSLBaseException(this, METHOD_NAME, "");

	        }

	        return errorList;

	    }
	    /**
	     * This method prepares the ChannelTransferVO from the arguments
	     * channelTransferVO, requestVO,
	     * channelUserVO, filteredPrdList and userVO
	     * 
	     * @param p_requestVO
	     * @param p_channelTransferVO
	     * @param p_curDate
	     * @param p_channelUserVO
	     * @param p_prdList
	     * @param p_userVO
	     * @return ChannelTransferVO
	     * @throws BTSLBaseException
	     */

	    public ChannelTransferVO prepareChannelTransferVO(ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList, UserVO p_userVO) throws BTSLBaseException {
	        ChannelTransferItemsVO channelTransferItemsVO = null;
	        String productType = null;
	        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
	        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;

	        if (log.isDebugEnabled()) {
	            log.debug("prepareChannelTransferVO", "Entering  : p_channelTransferVO" + p_channelTransferVO + "p_channelUserVO" + p_channelUserVO + "p_userVO" + p_userVO);
	        }

	        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
	        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
	        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
	        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
	        p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
	        p_channelTransferVO.setCategoryCode(p_channelUserVO.getCategoryCode());
	        p_channelTransferVO.setReceiverGradeCode("");
	        p_channelTransferVO.setSenderGradeCode(p_channelUserVO.getUserGrade());
	        p_channelTransferVO.setFromUserID(p_channelUserVO.getUserID());
	        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
	        p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
	        p_channelTransferVO.setToUserCode(p_userVO.getUserCode());
	        p_channelTransferVO.setTransferDate(p_curDate);
	        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
	        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
	        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType()); 
	        p_channelTransferVO.setCreatedOn(p_curDate);
	        p_channelTransferVO.setCreatedBy(p_userVO.getUserID());
	        p_channelTransferVO.setModifiedOn(p_curDate);
	        p_channelTransferVO.setModifiedBy(p_userVO.getUserID());
	        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
	        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
	        p_channelTransferVO.setReceiverTxnProfile("");
	        p_channelTransferVO.setSenderTxnProfile(p_channelUserVO.getTransferProfileID());
	        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_TYPE_SMS);
	        p_channelTransferVO.setActiveUserId(p_userVO.getUserID());
	        // adding the some additional information for sender/reciever

	        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
	        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
	        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
	        p_channelTransferVO.setRequestGatewayCode(PretupsI.GATEWAY_TYPE_WEB);
	        p_channelTransferVO.setRequestGatewayType(PretupsI.GATEWAY_TYPE_WEB);
	        p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_TYPE_SALE);

	        for (int i = 0, k = p_prdList.size(); i < k; i++) {
	            channelTransferItemsVO = (ChannelTransferItemsVO) p_prdList.get(i);
	            totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
	            if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
	                totMRP += (channelTransferItemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
	            } else {
	                totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());
	            }
	            totPayAmt += channelTransferItemsVO.getPayableAmount();
	            totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
	            totTax1 += channelTransferItemsVO.getTax1Value();
	            totTax2 += channelTransferItemsVO.getTax2Value();
	            totTax3 += channelTransferItemsVO.getTax3Value();
	            commissionQty += channelTransferItemsVO.getCommQuantity();
	            senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
	            receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
	            productType = channelTransferItemsVO.getProductType();
	        }// end of for
	        p_channelTransferVO.setRequestedQuantity(totRequestQty);
	        p_channelTransferVO.setTransferMRP(totMRP);
	        p_channelTransferVO.setPayableAmount(totPayAmt);
	        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);
	        p_channelTransferVO.setTotalTax1(totTax1);
	        p_channelTransferVO.setTotalTax2(totTax2);
	        p_channelTransferVO.setTotalTax3(totTax3);
	        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
	        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
	        p_channelTransferVO.setProductType(channelTransferItemsVO.getProductType());
	        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);
	        p_channelTransferVO.setProductType(productType);
	        p_channelTransferVO.setCommQty(commissionQty);
	        p_channelTransferVO.setSenderDrQty(senderDebitQty);
	        p_channelTransferVO.setReceiverCrQty(receiverCreditQty);

	        if (log.isDebugEnabled()) {
	            log.debug("prepareChannelTransferVO", "Exiting .....  :p_channelTransferVO" + p_channelTransferVO);
	        }

	        return p_channelTransferVO;

	    }
	    /**
		 * Method filedelete
		 */

		private void filedelete() {
			if(!BTSLUtil.isNullString(filepathtemp))
			{File file = new File(filepathtemp);
			if (file.delete()) {
				log.debug("filedelete", "******** Method uploadAndProcessFile :: Got exception and deleted the file");
			}
			}
		}
		
		private void writeToFileError(O2CBatchWithdrawFileResponse response) throws BTSLBaseException, IOException {
			List<List<String>> rows = new ArrayList<>();
			for(int i=0;i<response.getErrorMap().getRowErrorMsgLists().size();i++)
			{
				RowErrorMsgLists rowErrorMsgList = response.getErrorMap().getRowErrorMsgLists().get(i);
				for(int i1=0;i1<rowErrorMsgList.getMasterErrorList().size();i1++)
				{
					MasterErrorList masterErrorList=rowErrorMsgList.getMasterErrorList().get(i1);
				    rows.add(( Arrays.asList(rowErrorMsgList.getRowValue(), rowErrorMsgList.getRowName(), masterErrorList.getErrorMsg())));
				    
				}
				rowErrorMsgList.setRowErrorMsgList(null);//done to remove rowerrormsglist deliberately in response
			}
			String filePathCons = Constants.getProperty("ErrorBatchO2CUserListFilePath");
			validateFilePathCons(filePathCons);
			
			String filePathConstemp = filePathCons + "temp/";        
			createDirectory(filePathConstemp);
			

			filepathtemp = filePathConstemp ;   

			String logErrorFilename = "Errorlog_" + (System.currentTimeMillis()); 
			//writeExcel(rows,filepathtemp+logErrorFilename+ ".xls");
			writeCSV(rows,filepathtemp+logErrorFilename+ ".csv");
			File error =new File(filepathtemp+logErrorFilename+ ".csv");
			byte[] fileContent = FileUtils.readFileToByteArray(error);
	   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
	   		response.setFileAttachment(encodedString);
	   		response.setFileName(logErrorFilename+".csv");
	   		
		}
		public void validateFilePathCons(String filePathCons) throws BTSLBaseException {
			if (BTSLUtil.isNullorEmpty(filePathCons)) {
				 throw new BTSLBaseException(this, "validateFilePathCons", PretupsErrorCodesI.EMPTY_FILE_PATH_IN_CONSTANTS,
						 PretupsI.RESPONSE_FAIL,null); 
			}
		}
		/**
		 * Method createDirectory will create directory at specified path if direcry do not exists
		 * 
		 * @param filePathConstemp
		 * @throws BTSLBaseException
		 */

		public void createDirectory(String filePathConstemp) throws BTSLBaseException {

			String methodName = "createDirectory";
			File fileTempDir = new File(filePathConstemp);
			if (!fileTempDir.isDirectory()) {
				fileTempDir.mkdirs();
			}
			if (!fileTempDir.exists()) {
				log.debug("Directory does not exist : ", fileTempDir);
				throw new BTSLBaseException("OAuthenticationUtil", methodName,
						PretupsErrorCodesI.BATCH_UPLOAD_DIRECTORY_DO_NOT_EXISTS, PretupsI.RESPONSE_FAIL, null); // provide
																												// your own
			}
		}
		public void writeCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
			try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
	    	csvWriter.append("Line number");
	    	csvWriter.append(Constants.getProperty("FILE_SEPARATOR_O2C"));
	    	csvWriter.append("Mobile number/LoginId");
	    	csvWriter.append(Constants.getProperty("FILE_SEPARATOR_O2C"));
	    	csvWriter.append("Reason");
	    	csvWriter.append("\n");

	    	for (List<String> rowData : listBook) {
	    	    csvWriter.append(String.join(Constants.getProperty("FILE_SEPARATOR_O2C"), rowData));
	    	    csvWriter.append("\n");
	    	}

	    	}
		}
		//Validation for request input for file upload
		private boolean requestValidation() throws BTSLBaseException {
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
				isValid = false ;
			}
			if (BTSLUtil.isNullorEmpty(request.getFileType())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg("File type is empty.");
				masterErrorList.setErrorCode("");
				inputValidations.add(masterErrorList);
				isValid = false ;
			}
			if (BTSLUtil.isNullorEmpty(request.getBatchName())) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.BATCH_NAME_EMPTY, null));
				masterErrorList.setErrorCode(PretupsErrorCodesI.BATCH_NAME_EMPTY);
				inputValidations.add(masterErrorList);
				isValid = false ;
			}
			if (!BTSLUtil.isNullorEmpty(request.getLanguage2())&& request.getLanguage2().length()>30) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.LANGUAGE2_LENGTH, null));
				masterErrorList.setErrorCode(PretupsErrorCodesI.LANGUAGE2_LENGTH);
				inputValidations.add(masterErrorList);
				isValid = false ;
			}
			if (!BTSLUtil.isNullorEmpty(request.getLanguage1())&& request.getLanguage1().length()>30) {
				MasterErrorList masterErrorList = new MasterErrorList();
				masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.LANGUAGE1_LENGTH, null));
				masterErrorList.setErrorCode(PretupsErrorCodesI.LANGUAGE1_LENGTH);
				inputValidations.add(masterErrorList);
				isValid = false ;
			}
			return isValid;

		}

	    
}
