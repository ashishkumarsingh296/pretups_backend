package com.restapi.o2c.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ErrorMap;
import com.btsl.common.FileWriteUtil;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FocListValueVO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListController;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListService;
import com.btsl.pretups.channel.transfer.requesthandler.DownloadUserListServiceImpl;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchFocFileProcessLog;
import com.btsl.pretups.logging.DirectPayOutErrorLog;
import com.btsl.pretups.logging.DirectPayOutSuccessLog;
import com.btsl.pretups.logging.MessageSentLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.user.service.FileDownloadResponse;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.channel.transfer.businesslogic.FOCBatchTransferWebDAO;
import com.web.pretups.channel.transfer.web.FOCBatchTransferActionOne;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

@Service("FocBatchInitiateServiceI")
public class FocBatchInitiateServiceImpl implements FocBatchInitiateServiceI {
	protected final Log log = LogFactory.getLog(getClass().getName());
    private static OperatorUtilI calculatorI = null;
	private String filepathtemp;
	ArrayList<MasterErrorList> inputValidations=null;
	
	@Override
	public FOCBatchTransferResponse processRequest(FOCBatchTransferRequestVO requestVO, String serviceType,
			OperatorUtilI calculator, String requestIDStr, Connection con, Locale locale,
			HttpServletRequest httprequest, MultiValueMap<String, String> headers, HttpServletResponse responseSwag)
			throws BTSLBaseException {
		final String methodName = "processRequest";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        calculatorI=calculator;
        RestReceiver.updateRequestIdChannel();
        LinkedHashMap<String, List<String>> bulkDataMap ;
        ErrorMap errorMap = new ErrorMap();
        FOCBatchTransferResponse response = new FOCBatchTransferResponse();
        try {
        	FOCBatchTransferDetails data = requestVO.getFOCBatchTransferDetails();
        	inputValidations = new ArrayList<MasterErrorList>();
        	final FOCBatchMasterVO batchMasterVO = new FOCBatchMasterVO();
			String msisdn = requestVO.getData().getMsisdn();
			ChannelUserVO senderVO=(ChannelUserVO)new UserDAO().loadUsersDetails(con, msisdn);
			 senderVO.setUserPhoneVO(new UserDAO().loadUserPhoneVO(con, senderVO.getUserID()));
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered Sender VO: " + senderVO);
			}
			
			if(PretupsI.YES.equals(senderVO.getCategoryVO().getSmsInterfaceAllowed())&& senderVO.getUserPhoneVO()!=null&& PretupsI.YES.equals(senderVO.getUserPhoneVO().getPinRequired()) )
			{
                if(BTSLUtil.isNullString(data.getPin()))
                {
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN,null);
					  response.setStatus("400");
					  response.setService(serviceType + "RESP");
					  response.setMessage(msg);
	                  response.setReferenceId(0);
					  response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
					  responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					 return response;
                }
                else{
	                try {
						ChannelUserBL.validatePIN(con, senderVO, data.getPin());
					} catch (BTSLBaseException be) {
						if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
								|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
							OracleUtil.commit(con);
						}
						String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
						response.setStatus("400");
						  response.setService(serviceType + "RESP");
						  response.setMessage(msg);
						  response.setMessageCode(be.getMessageKey());
						  responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		                  response.setReferenceId(0);
						  return response;
					}
                }
			}
			
			 if(BTSLUtil.isNullString(data.getBatchName())){
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_BATCH_NAME,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_BATCH_NAME);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 else
			 {
				 
				if (BTSLUtil.isContains_UnderScore(data.getBatchName()) ) {
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UNDERSCORE_NOT_ALLOWED,new String [] { "Batch name" });
					MasterErrorList masterErrorList = new MasterErrorList();	
					masterErrorList.setErrorCode(PretupsErrorCodesI.UNDERSCORE_NOT_ALLOWED);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 }
			 
			 
			 
			 if(BTSLUtil.isNullString(data.getFileName())){
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_DATA_NOT_ALLOWED,new String [] { "File name" });
					masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_DATA_NOT_ALLOWED);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 else
			 {
				 
				if (BTSLUtil.isContains_UnderScore(data.getFileName()) ) {
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UNDERSCORE_NOT_ALLOWED,new String [] { "File name" });
					MasterErrorList masterErrorList = new MasterErrorList();	
					masterErrorList.setErrorCode(PretupsErrorCodesI.UNDERSCORE_NOT_ALLOWED);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}
			 }	 
	
			
				 if(BTSLUtil.isNullString(data.getProduct())){
					 MasterErrorList masterErrorList = new MasterErrorList();
						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BLANK_PRODUCTCODE,null);
						masterErrorList.setErrorCode(PretupsErrorCodesI.BLANK_PRODUCTCODE);
						masterErrorList.setErrorMsg(msg);
						inputValidations.add(masterErrorList);
					}
				 else
				 {		
					 	boolean isProd=false;
						ArrayList <C2sBalanceQueryVO>prodList1 =new ProductTypeDAO().getProductsDetails(con);
						for(C2sBalanceQueryVO prod1:prodList1) {
					    	  if(data.getProduct().equalsIgnoreCase(prod1.getProductCode())) {
					    		  batchMasterVO.setProductCode(prod1.getProductCode());
					              batchMasterVO.setProductMrp(Long.valueOf(prod1.getUnitValue()));
					              batchMasterVO.setProductCodeDesc(prod1.getProductName());
					              batchMasterVO.setProductShortName(prod1.getProductShortCode());
					              batchMasterVO.setProductType(prod1.getProductType());
					              data.setProduct(prod1.getProductCode());
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
				 
		 	 if(BTSLUtil.isNullString(data.getGeographicalDomain())){
		 		 MasterErrorList masterErrorList = new MasterErrorList();
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,new String[]{"Geographical Domain"});
				masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
		 	 }else{

				 ArrayList geoList = null;
				 ArrayList geoVList = null;
				UserGeographiesVO geographyVO = null;
				String geoDomain = data.getGeographicalDomain().toUpperCase();
                ArrayList userGeoList = new GeographicalDomainDAO().loadUserGeographyList(con, senderVO.getUserID(), senderVO .getNetworkID());
	            if (userGeoList != null) {
	                if (userGeoList.size()>= 1 ) {
	                    geoList = new ArrayList();
	                    geoVList = new ArrayList();
	                    for (int i = 0, k = userGeoList.size(); i < k; i++) {
	                        geographyVO = (UserGeographiesVO) userGeoList.get(i);
	                        geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
	                        geoVList.add(geographyVO.getGraphDomainCode());
	                    }
	                    batchMasterVO.setGeographyList(geoList);	
	                    senderVO.setGeographicalAreaList(userGeoList);
	 		           }
	            }
	            if(!geoVList.contains(geoDomain) &&!geoDomain.equals("ALL"))
	            {
		            MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY,null);
					masterErrorList.setErrorCode(PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
	            }else{
	            	data.setGeographicalDomain(data.getGeographicalDomain().toUpperCase());
	            }
				 
		 	 }
			 if(BTSLUtil.isNullString(data.getChannelDomain())){
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,new String[]{"Channel Domain"});
					masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}else{
					 // load the domain of the user that are associated with it
	                ArrayList<ListValueVO> domainList = new DomainDAO().loadDomainListByUserId(con, senderVO.getUserID());
	                
	                if ((domainList == null || domainList.isEmpty())) {
	                        domainList = new DomainDAO().loadCategoryDomainList(con);
	               }
	                    boolean loadFilteredCategory = false;
	                    domainList = BTSLUtil.displayDomainList(domainList);
		                senderVO.setDomainList(domainList);
	                    if (domainList.size() >= 1) {
	                    	for(ListValueVO listValueVO:domainList)
		                        if(listValueVO.getValue().equals(data.getChannelDomain().toUpperCase()))
			                        data.setChannelDomain(listValueVO.getValue());
		                        	loadFilteredCategory = true;
		               } 
	                    
	                    if(!loadFilteredCategory){
	     			            MasterErrorList masterErrorList = new MasterErrorList();
	     						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GRPH_INVALID_DOMAIN,null);
	     						masterErrorList.setErrorCode(PretupsErrorCodesI.GRPH_INVALID_DOMAIN);
	     						masterErrorList.setErrorMsg(msg);
	     						inputValidations.add(masterErrorList);
	     		            }
				}
			 if(BTSLUtil.isNullString(data.getUsercategory())){
				 MasterErrorList masterErrorList = new MasterErrorList();
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK, new String[]{"User Categry"});
					masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
				}else{
				    final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
	                /*
			         * Now load the list of categories for which the transfer rule is
			         * defined where FROM CATEGORY is the logged in user category.
			         */
	                final ArrayList catgList = channelTransferRuleWebDAO.loadTransferRulesCategoryList(con, senderVO.getNetworkID(), PretupsI.OPERATOR_TYPE_OPT);
	                ChannelTransferRuleVO rulesVO = null;
			        // Now filter the transfer rule list for which the Transfer allowed
			        // field is 'Y' or Transfer Channel by pass is Y
			        int validCategory=0;
			        senderVO.setCategoryList(catgList);
			        for (int i = 0, k = catgList.size(); i < k; i++) {
			            rulesVO = (ChannelTransferRuleVO) catgList.get(i);
			            if (PretupsI.YES.equals(rulesVO.getDirectTransferAllowed()) || PretupsI.YES.equals(rulesVO.getTransferChnlBypassAllowed())) {
			                // Validating whether category in req is valid or not
			            	if(data.getUsercategory().equalsIgnoreCase(rulesVO.getToCategory())) {
			            		data.setUsercategory(rulesVO.getToCategory());
			            		validCategory=1;
			            		break;
			            	}
			            }
			        }
			        //if not valid
			        if(validCategory==0 && !"ALL".equalsIgnoreCase(data.getUsercategory()) ){
		        	   MasterErrorList masterErrorList = new MasterErrorList();
 						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY,null);
 						masterErrorList.setErrorCode(PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY);
 						masterErrorList.setErrorMsg(msg);
 						inputValidations.add(masterErrorList);
			        }
		          
				}
			 
			 requestValidation(data, locale);
			 
			//throwing list of basic form errors
			 if(!BTSLUtil.isNullOrEmptyList(inputValidations)) {
			  response.setStatus("400");
			  response.setService(serviceType + "RESP");
			  response.setErrorMap(new ErrorMap());
			  response.getErrorMap().setMasterErrorList(inputValidations);
			  response.setMessageCode(PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS);;
			  response.setReferenceId(0);
			  response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS, null)); 
			  responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			  return response;
			 }
		
		  
            if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(senderVO.getOutSuspened())) {
	            if (log.isDebugEnabled()) {
	                log.debug("userSearch", "USER IS OUT SUSPENDED IN THE SYSTEM");
	            }

				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OUT_SUSPENDED, PretupsI.RESPONSE_FAIL, null);

	        }
            
	     	            
            ///code for read abd validate file content
			ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
			HashMap<String, String>  fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_TYPE1, requestVO.getFOCBatchTransferDetails().getFileType());
			fileDetailsMap.put(PretupsI.FILE_NAME,requestVO.getFOCBatchTransferDetails().getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getFOCBatchTransferDetails().getFileAttachment());
			fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, serviceType);
			 
			 Boolean multipleWalletApply=false;
			batchMasterVO.setWallet_type(null);
				batchMasterVO.setWallet_type(PretupsI.SALE_WALLET_TYPE);
				multipleWalletApply = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
				if(multipleWalletApply) {
					if( PretupsI.FOC_WALLET_TYPE.equals(requestVO.getFOCBatchTransferDetails().getOperatorWalletOption())) {
						batchMasterVO.setWallet_type(PretupsI.FOC_WALLET_TYPE);
					}else {
						batchMasterVO.setWallet_type(PretupsI.INCENTIVE_WALLET_TYPE);
					}
				}else {
					batchMasterVO.setWallet_type(PretupsI.SALE_WALLET_TYPE);
				}
				
				fileDetailsMap.put(PretupsI.OPERATOR_COMM_WALLET_OPTION, batchMasterVO.getWallet_type());
			
			
			
			
			
			
			ArrayList batchItemsList = new ArrayList();
			// Check external txn id for domain type
            final String externalTxnMandatoryDomainType = SystemPreferences.EXTERNAL_TXN_MANDATORY_DOMAINTYPE;
            final String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORFOC;
            final boolean externalCodeMandatory = SystemPreferences.EXTERNAL_CODE_MANDATORY_FORFOC;
            String externalsTxnMandatory =null;
            // load the selected domain type
            if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("0") != -1) {
                if (BTSLUtil.isNullString(externalTxnMandatoryDomainType)) {
                	externalsTxnMandatory=PretupsI.YES;
                } else {
                    final String domainTypeArr[] = externalTxnMandatoryDomainType.split(",");
                    int domainTypeArray=domainTypeArr.length;
                    for (int i = 0, j =domainTypeArray ; i < j; i++) {
                        if (data.getChannelDomain().equals(domainTypeArr[i])) {
                        	externalsTxnMandatory=PretupsI.YES;
                            break;
                        }
                    }
                }
            }
            final SubLookUpDAO subLookupDAO = new SubLookUpDAO();
            ArrayList<ListValueVO> bonusTypeList = subLookupDAO.loadSublookupByLookupType(con, PretupsI.BONUS_TYPE);
			//bulkDataMap = fileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 0, errorMap,externalCodeMandatory,externalsTxnMandatory,batchItemsList);
			bulkDataMap = fileUtil.uploadAndReadGenericFileFOC(fileDetailsMap, 0, errorMap,externalCodeMandatory,externalsTxnMandatory,batchItemsList,bonusTypeList,con);
            // If file does not contain record as entered by the user then
            // show the error message
            if(!BTSLUtil.isNullorEmpty(errorMap.getRowErrorMsgLists()))
			{
            	filedelete();
				response.setErrorMap(errorMap);
				writeFileForResponseFoc(response, errorMap,responseSwag,data);
				response.setStatus("400");
				response.setService(serviceType + "RESP");
				response.setMessageCode(PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS);;
				response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS, null)); 
				response.setReferenceId(0);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}else
			{
				List<String> filePath= bulkDataMap.get("filepathtemp");
				String filePaths=filePath.get(0);
				filepathtemp=filePaths;
				processRequestUpload(response, batchMasterVO, con, senderVO, data, httprequest, batchItemsList,responseSwag,serviceType);
				response.setService(serviceType + "RESP");
			}
            
     }catch(BTSLBaseException be){
    	filedelete();
        log.error("processRequest", "Exceptin:e=" + be);
        log.errorTrace(methodName, be);
   	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
        response.setMessageCode(be.getMessageKey());
        response.setMessage(msg);
		response.setService(serviceType + "RESP");
		response.setReferenceId(0);

    	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
    		responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	         response.setStatus("401");
        }
       else{
    	   responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
       		response.setStatus("400");
       }
    }catch (Exception e) {
    	filedelete();
        log.debug("processRequest", e);
        response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
				null);
        response.setMessage(resmsg);
		response.setService(serviceType + "RESP");
		responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
    	response.setStatus("400");
    	response.setReferenceId(0);
        log.error(methodName, "Exceptin:e=" + e);
	}finally {
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting:=" + methodName);
        }
    
    }
	return response;
    }

	private void filedelete() {
			if(!BTSLUtil.isNullString(filepathtemp))
			{File file = new File(filepathtemp);
			if (file.delete()) {
				log.debug("filedelete", "******** Method filedelete :: Got exception and deleted the file");
			}
			}
		}
	
	private void requestValidation(FOCBatchTransferDetails data,Locale locale) {
		 if(BTSLUtil.isNullString(data.getFileAttachment())){
			 MasterErrorList masterErrorList = new MasterErrorList();
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK, new String[]{"File Attachment"});
				masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
			}
		 if(BTSLUtil.isNullString(data.getFileName())){
			 MasterErrorList masterErrorList = new MasterErrorList();
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,new String[]{"File name"});
				masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
			}
		 if(BTSLUtil.isNullString(data.getFileType())){
			 MasterErrorList masterErrorList = new MasterErrorList();
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,new String[]{"File Type"});
				masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
			}
		 if(BTSLUtil.isNullString(data.getBatchName())){
			 MasterErrorList masterErrorList = new MasterErrorList();
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_NAME_EMPTY,null);
				masterErrorList.setErrorCode(PretupsErrorCodesI.BATCH_NAME_EMPTY);
				masterErrorList.setErrorMsg(msg);
				inputValidations.add(masterErrorList);
			}

		if (!BTSLUtil.isNullorEmpty(data.getLanguage2())&& data.getLanguage2().length()>30) {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.LANGUAGE2_LENGTH, null));
			masterErrorList.setErrorCode(PretupsErrorCodesI.LANGUAGE2_LENGTH);
			inputValidations.add(masterErrorList);
		}
		if (!BTSLUtil.isNullorEmpty(data.getLanguage1())&& data.getLanguage1().length()>30) {
			MasterErrorList masterErrorList = new MasterErrorList();
			masterErrorList.setErrorMsg(RestAPIStringParser.getMessage( new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.LANGUAGE1_LENGTH, null));
			masterErrorList.setErrorCode(PretupsErrorCodesI.LANGUAGE1_LENGTH);
			inputValidations.add(masterErrorList);
		}

	}
	
	
	
	private void writeFileForResponseFoc(FOCBatchTransferResponse response, ErrorMap errorMap, HttpServletResponse responseSwag,FOCBatchTransferDetails data)throws BTSLBaseException, IOException {
		if(errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
    		return ;
		
		ErrorFileRequestVO errorFileRequestVO = new ErrorFileRequestVO();
		errorFileRequestVO.setRowErrorMsgLists(errorMap.getRowErrorMsgLists());
		errorFileRequestVO.setFile(data.getFileAttachment());
		errorFileRequestVO.setFiletype(data.getFileType());
		ErrorFileResponse errorFileResponse = new ErrorFileResponse();
		DownloadUserListService downloadUserListService = new DownloadUserListServiceImpl();
		downloadUserListService.downloadErrorFile(errorFileRequestVO,  errorFileResponse,  responseSwag);
		response.setFileAttachment(errorFileResponse.getFileAttachment());
		response.setFileName(errorFileResponse.getFileName());
		
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
	
		/**
	  	 * @param response
	  	 * @param batchMasterVO
	  	 * @param con
	  	 * @param senderVO
	  	 * @param data
	  	 * @param httprequest
	  	 * @param batchItemsList
	  	 * @return
	  	 * @throws BTSLBaseException 
	  	 */
	  	private FOCBatchTransferResponse processRequestUpload(FOCBatchTransferResponse response, FOCBatchMasterVO batchMasterVO,Connection con, ChannelUserVO senderVO,FOCBatchTransferDetails data,HttpServletRequest httprequest,ArrayList batchItemsList,HttpServletResponse responseSwag,String serviceType) throws BTSLBaseException{
	  		String methodName = "processRequestUpload";
	  		ProcessStatusVO processVO = null;
	        boolean processRunning = true;
	        final Date currentDate = new Date();
	        ErrorMap errorMap = new ErrorMap();
	        Locale locale = BTSLUtil.getBTSLLocale(httprequest);
	  		try{
	  		   final ProcessBL processBL = new ProcessBL();
	            try {
	            	if("FOCBATCHTRF".equalsIgnoreCase(serviceType))
	            	 processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.FOC_BATCH_PROCESS_ID,senderVO.getNetworkID());
	            	else
	            	processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.DP_BATCH_PROCESS_ID,senderVO.getNetworkID());	          
	            	} catch (BTSLBaseException e) {
	                log.error(methodName, "Exception:e=" + e);
	                log.errorTrace(methodName, e);
	                processRunning = false;
	                if("FOCBATCHTRF".equalsIgnoreCase(serviceType))
	                  throw new BTSLBaseException(this, methodName, "batchfoc.processfile.error.alreadyexecution");
	                else
	                 throw new BTSLBaseException(this, methodName, "batchdirectpayout.processfile.error.alreadyexecution");
	            }
	            if (processVO != null && !processVO.isStatusOkBool()) {
	                processRunning = false;
	                if("FOCBATCHTRF".equalsIgnoreCase(serviceType))
		               throw new BTSLBaseException(this, methodName, "batchfoc.processfile.error.alreadyexecution");
		             else
		               throw new BTSLBaseException(this, methodName, "batchdirectpayout.processfile.error.alreadyexecution");
	            }
	            con.commit();
	            processVO.setNetworkCode(senderVO.getNetworkID());
	            // ends here

          // validate user's information
          String geoDomain = data.getGeographicalDomain().toUpperCase();
          if (geoDomain.equals(PretupsI.ALL)) {
              geoDomain = this.generateCommaString(batchMasterVO.getGeographyList());
          } else {
              geoDomain = "'" + geoDomain + "'";
          }
          String category = data.getUsercategory();
          String catArr[] = new String[1];
          if (category.equals(PretupsI.ALL)) {
              category = this.generateCommaStringAll((ArrayList)senderVO.getCategoryList());
          } else {
              catArr = category.split(":");
              category = "'" + category+ "'";
          }
          
          final Date curDate = new Date();
         
          // Construct o2cBatchMasterVO
          batchMasterVO.setNetworkCode(senderVO.getNetworkID());
          batchMasterVO.setNetworkCodeFor(senderVO.getNetworkID());
          batchMasterVO.setBatchName(data.getBatchName());
          if("FOCBATCHTRF".equalsIgnoreCase(serviceType))
          batchMasterVO.setStatus(PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);
          else
          batchMasterVO.setStatus(PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS);
          batchMasterVO.setCreatedBy(senderVO.getUserID());
          batchMasterVO.setCreatedOn(curDate);
          batchMasterVO.setModifiedBy(senderVO.getUserID());
          batchMasterVO.setModifiedOn(curDate);
          batchMasterVO.setDomainCode(data.getChannelDomain());
          batchMasterVO.setBatchFileName(data.getFileName()+"."+data.getFileType());
          batchMasterVO.setBatchDate(curDate);
          batchMasterVO.setDefaultLang(data.getLanguage1());
          batchMasterVO.setSecondLang(data.getLanguage2());
          
          if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
          	/*if(senderVO!=null){
              batchMasterVO.setWalletCode(theForm.getWalletCode());
          	}
          	else
          	{
          		 batchMasterVO.setWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
          	}*/
        	  batchMasterVO.setWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
          } else {
              batchMasterVO.setWalletCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
          }
          
          
          
          batchMasterVO.setCategoryCode(data.getUsercategory());
          if("FOCBATCHTRF".equalsIgnoreCase(serviceType))
          this.genrateFOCBatchMasterTransferID(batchMasterVO);
          else
          this.genrateDPBatchMasterTransferID(batchMasterVO);
          // 2D array processing starts here..
          long batchDetailID = 0;           for (int r = 0; r < batchItemsList.size(); r++) {
        	  FOCBatchItemsVO batchItemsVO = (FOCBatchItemsVO)batchItemsList.get(r);
        	     
                  batchItemsVO.setBatchId(batchMasterVO.getBatchId());
                  if("FOCBATCHTRF".equalsIgnoreCase(serviceType))
                  batchItemsVO.setBatchDetailId(this.genrateFOCBatchDetailTransferID(batchMasterVO.getBatchId(), ++batchDetailID));
                  else
                  batchItemsVO.setBatchDetailId(this.genrateDPBatchDetailTransferID(batchMasterVO.getBatchId(), ++batchDetailID));
                  batchItemsVO.setModifiedBy(senderVO.getUserID());

          } // end of for loop
          final ArrayList<FocListValueVO> focFileErrorList = new ArrayList<FocListValueVO>(50);
          ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
          // now validate the user's in the database.
          ArrayList fileErrorList = null;
          if("FOCBATCHTRF".equalsIgnoreCase(serviceType))
           fileErrorList = channelUserWebDAO.validateUsersForBatchFOCREST(con, batchItemsList, data.getChannelDomain(), category, senderVO.getNetworkID(), geoDomain,
          		curDate, locale,focFileErrorList);
          else
			fileErrorList=channelUserWebDAO.validateUsersForBatchDPREST(con,batchItemsList,data.getChannelDomain(),category,senderVO.getNetworkID(),
					geoDomain,curDate,locale);  
          if (fileErrorList != null && !fileErrorList.isEmpty()) {
              int errorListSize = fileErrorList.size();
              for (int i = 0, j = errorListSize; i < j; i++) {
              	ListValueVO errorVO = (ListValueVO) fileErrorList.get(i);
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
						rowErrorMsgLists.setRowValue("Line"+ String.valueOf(Long.parseLong(errorVO.getOtherInfo())));
						rowErrorMsgLists.setRowName(errorVO.getCodeName());
						if(errorMap.getRowErrorMsgLists() == null)
							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                  }
              }
          
              writeFileForResponseFoc(response, errorMap,responseSwag,data);
               // filedelete();
				response.setStatus("400");
	            response.setErrorMap(errorMap);
				response.setBatchID(String.valueOf( batchMasterVO.getBatchId()));
				response.setMessageCode(PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS);
          	    response.setReferenceId(0);
          	    responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_BATCH_TRF_NOT_SUCCESS, null)); 
              return response;
          }
      else{ 
          batchMasterVO.setBatchTotalRecord(batchItemsList.size());
          final FOCBatchTransferWebDAO focBatchTransferWebDAO = new FOCBatchTransferWebDAO();
          final FOCBatchTransferDAO focBatchDAO = new FOCBatchTransferDAO();
          final HashMap<String,ChannelTransferItemsVO> batchO2Cmap = new HashMap<String,ChannelTransferItemsVO>();
          if(!"FOCBATCHTRF".equalsIgnoreCase(serviceType))
          fileErrorList = focBatchDAO.initiateBatchDPTransferREST(con, batchMasterVO, batchItemsList ,locale);
          else
          fileErrorList = focBatchTransferWebDAO.initiateBatchFOCTransferREST(con, batchMasterVO, batchItemsList, locale, focFileErrorList);
          // Update the batch total records..
          final int fileErrSize = fileErrorList.size();
          int processedRecords = batchItemsList.size() - fileErrorList.size();
         
          if (fileErrorList == null || fileErrSize == 0) {
              // give success message on the first screen with batchID,
              // batchName, total number of records
              response.setMessageCode("");
				String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_O2C_TRF_SUCCESS, new String[]{ batchMasterVO.getBatchId(), data.getBatchName(), String.valueOf(processedRecords)});
				response.setStatus("200");
				response.setBatchID(String.valueOf( batchMasterVO.getBatchId()));
				response.setMessage(msg);
				response.setMessageCode(PretupsErrorCodesI.BATCH_O2C_TRF_SUCCESS);
          	    response.setReferenceId(processedRecords);
				responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
				if ("FOCBATCHTRF".equalsIgnoreCase(serviceType)) {
					BatchFocFileProcessLog.focBatchMasterLog(methodName, batchMasterVO,
							"PASS : Batch generated successfully",
							"TOTAL RECORDS=" + processedRecords + ", FILE NAME=" + batchMasterVO.getBatchFileName());
					MessageSentLog.log(senderVO.getMsisdn(), locale, "WEB", "WEB", "PASS : Batch generated successfully. "+"TOTAL RECORDS=" + processedRecords+", FILE NAME=" + batchMasterVO.getBatchFileName(), response.getStatus(), httprequest.getRequestURL().toString(), "Language1: "+ batchMasterVO.getDefaultLang() + ", Language2: "+batchMasterVO.getSecondLang());
				}
				else {
					DirectPayOutSuccessLog.dpBatchMasterLog("processDirectPayoutUploadedFile", batchMasterVO,
							"PASS : Batch generated successfully",
							"TOTAL RECORDS=" + processedRecords + ", FILE NAME=" + batchMasterVO.getBatchFileName());
					MessageSentLog.log(senderVO.getMsisdn(), locale, "WEB", "WEB", "PASS : Batch generated successfully. "+"TOTAL RECORDS=" + processedRecords+", FILE NAME=" + batchMasterVO.getBatchFileName(), response.getStatus(), httprequest.getRequestURL().toString(), "Language1: "+ batchMasterVO.getDefaultLang() + ", Language2: "+batchMasterVO.getSecondLang());

				}
				
          } else {
              // give success message on the confirmation screen with batchID,
              // batchName, total number of success records
              // and give link to view the errors.

              int errorListSize = fileErrorList.size();
	            final String[] arr = { batchMasterVO.getBatchId(), data.getBatchName(), String.valueOf(processedRecords) };
	            if (batchItemsList.size()  == fileErrorList.size()) // If all the records  contains error in db
              {
	            	MasterErrorList masterErrorList = new MasterErrorList();
					masterErrorList.setErrorCode("restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile");
					String msg = RestAPIStringParser.getMessage(locale, "restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile", null);
					masterErrorList.setErrorMsg(msg);
					errorMap.setMasterErrorList(new ArrayList<MasterErrorList> ());
					errorMap.getMasterErrorList().add(masterErrorList);
					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					 if("FOCBATCHTRF".equalsIgnoreCase(serviceType))
					 BatchFocFileProcessLog.focBatchMasterLog(methodName, batchMasterVO, "FAIL : All records contains DB error in batch",
                      "TOTAL RECORDS=0, FILE NAME=" + batchMasterVO.getBatchFileName());
					 else
					 DirectPayOutErrorLog.dpBatchMasterLog("processDirectPayoutUploadedFile", batchMasterVO, "FAIL : All records contains DB error in batch",
	                            "TOTAL RECORDS=0, FILE NAME=" + batchMasterVO.getBatchFileName());
              } else {
  	            String[] arg = { batchMasterVO.getBatchId(), data.getBatchName(), String.valueOf(processedRecords), String.valueOf(batchItemsList.size())};
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_O2C_TRF_PARTIAL_SUCCESS, arg);
              	response.setMessage(msg);
  				response.setMessageCode(PretupsErrorCodesI.BATCH_O2C_TRF_PARTIAL_SUCCESS);
					responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
					final BTSLMessages btslMessage = new BTSLMessages("batcho2c.processuploadedfile.msg.success", arr, "uploadBatchO2CResult");
					if ("FOCBATCHTRF".equalsIgnoreCase(serviceType)) {
						BatchFocFileProcessLog.focBatchMasterLog(methodName, batchMasterVO,
								"PASS : Batch generated successfully",
								"TOTAL RECORDS=" + arr[2] + ", FILE NAME=" + batchMasterVO.getBatchFileName());
						MessageSentLog.log(senderVO.getMsisdn(), locale, "WEB", "WEB", "PASS : Batch generated successfully, "+
								"TOTAL RECORDS=" + arr[2] + ", FILE NAME=" + batchMasterVO.getBatchFileName(), response.getStatus(), httprequest.getRequestURL().toString(), "Language1: "+ batchMasterVO.getDefaultLang() + ", Language2: "+batchMasterVO.getSecondLang());
	
					}
					else {
						DirectPayOutSuccessLog.dpBatchMasterLog("processDirectPayoutUploadedFile", batchMasterVO,
								"PASS : Batch generated successfully",
								"TOTAL RECORDS=" + arr[2] + ", FILE NAME=" + batchMasterVO.getBatchFileName());
					MessageSentLog.log(senderVO.getMsisdn(), locale, "WEB", "WEB", "PASS : Batch generated successfully, "+
							"TOTAL RECORDS=" + arr[2] + ", FILE NAME=" + batchMasterVO.getBatchFileName(), response.getStatus(), httprequest.getRequestURL().toString(), "Language1: "+ batchMasterVO.getDefaultLang() + ", Language2: "+batchMasterVO.getSecondLang());
}
              }
	            
	            for (int i = 0, j = errorListSize; i < j; i++) {
	                	ListValueVO errorVO = (ListValueVO) fileErrorList.get(i);
			            if(!BTSLUtil.isNullString(errorVO.getOtherInfo()))
	                    {
			            	RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
			            	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
			            	MasterErrorList masterErrorList = new MasterErrorList();
							masterErrorList.setErrorCode(errorVO.getOtherInfo());
							String msg = errorVO.getOtherInfo2();
							masterErrorList.setErrorMsg(msg);
							masterErrorLists.add(masterErrorList);
							rowErrorMsgLists.setMasterErrorList(masterErrorLists);
							if(errorVO.getIDValue() == null) {
								errorVO.setIDValue(String.valueOf(i));
							}
							rowErrorMsgLists.setRowValue("Line"+ errorVO.getOtherInfo());
							rowErrorMsgLists.setRowName(errorVO.getCodeName());
							if(errorMap.getRowErrorMsgLists() == null)
								errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
							(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
	                    }
	                }
	            writeFileForResponseFoc(response, errorMap,responseSwag,data);
	            if(response.getMessageCode().equals(PretupsErrorCodesI.BATCH_O2C_TRF_PARTIAL_SUCCESS)) {
	            	response.setStatus(String.valueOf(HttpStatus.SC_ACCEPTED)); // TO DIFFERENTIATE IN ui SIDE. 	
	            }else {
	            	response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
	            }
	            response.setErrorMap(errorMap);
          	response.setReferenceId(processedRecords);
				response.setBatchID(String.valueOf( batchMasterVO.getBatchId()));
          }
	  	  }
	  	}catch(BTSLBaseException be){
	  		if(log.isDebugEnabled())
	  			log.debug(methodName, "Exceptin:be=" + be);
	        log.error(methodName, "Exceptin:be=" + be);
     	    throw be;
      }catch (Exception e) {
      	 log.error(methodName, "Exceptin:e=" + e);
      	 if(log.isDebugEnabled())
	  			log.debug(methodName, "Exceptin:e=" + e);
      	throw new BTSLBaseException(PretupsErrorCodesI.REQ_NOT_PROCESS);
  	}finally {
          // as to make the status of the batch o2c process as complete into
          // the table so that only
          // one instance should be executed for batch o2c
          if (processRunning) {
              try {
                  processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                  final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                  if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                      con.commit();
                  } else {
                      con.rollback();
                  }
              } catch (Exception e) {
                  if (log.isDebugEnabled()) {
                      log.error(methodName, " Exception in update process detail for batch o2c initiation " + e.getMessage());
                  }
                  log.errorTrace(methodName, e);
              }
          }
          // ends here
          if (log.isDebugEnabled()) {
              log.debug(methodName, "Exiting:=" + methodName);
          }
      
      }
      return response;
	 }
	  	 private String generateCommaString(ArrayList p_list) {
		        final String methodName = "generateCommaString";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "Entered p_list=" + p_list);
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
		            log.error(methodName, "Exceptin:e=" + e);
		            log.errorTrace(methodName, e);
		        } finally {
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, "Exited commaStr=" + commaStr);
		            }
		        }
		        return commaStr;
		    }
	  	 
	  	 
	  	 
	  	 
	  	 private String generateCommaStringAll(ArrayList p_list) {
		        final String methodName = "generateCommaStringAll";
		        if (log.isDebugEnabled()) {
		            log.debug(methodName, "Entered p_list=" + p_list);
		        }
		        String commaStr = "";
		        String catArr[] = new String[1];
		        String listStr = null;
		        try {
		            final int size = p_list.size();
		            ChannelTransferRuleVO listVO = null;
		            for (int i = 0; i < size; i++) {
		                listVO = (ChannelTransferRuleVO) p_list.get(i);
		                listStr = listVO.getToCategory();
		                commaStr = commaStr + "'" + listStr + "',";
		            }
		            commaStr = commaStr.substring(0, commaStr.length() - 1);
		        } catch (Exception e) {
		            log.error(methodName, "Exceptin:e=" + e);
		            log.errorTrace(methodName, e);
		        } finally {
		            if (log.isDebugEnabled()) {
		                log.debug(methodName, "Exited commaStr=" + commaStr);
		            }
		        }
		        return commaStr;
		    }
	  	 
	  	 
	  	 
	  	 
	  	 /**
	      * Method genrateFOCBatchMasterTransferID.
	      * This method is called generate FOC batch master transferID
	      * 
	      * @param p_currentDate
	      *            Date
	      * @param p_networkCode
	      *            String
	      * @throws BTSLBaseException
	      * @return
	      */

	  	private void genrateFOCBatchMasterTransferID(FOCBatchMasterVO p_batchMasterVO) throws BTSLBaseException {
	         final String METHOD_NAME = "genrateFOCBatchMasterTransferID";
	         if (log.isDebugEnabled()) {
	             log.debug("genrateFOCBatchMasterTransferID", "Entered p_batchMasterVO=" + p_batchMasterVO);
	         }
	         try {
	             final long txnId = IDGenerator.getNextID(PretupsI.FOC_BATCH_TRANSACTION_ID, BTSLUtil.getFinancialYear(), p_batchMasterVO.getNetworkCode(), p_batchMasterVO
	                 .getCreatedOn());
	             p_batchMasterVO.setBatchId(calculatorI.formatFOCBatchMasterTxnID(p_batchMasterVO, txnId));
	         } catch (Exception e) {
	             log.error(METHOD_NAME, "Exception " + e.getMessage());
	             log.errorTrace(METHOD_NAME, e);
	             throw new BTSLBaseException("FOCBatchTransferAction", METHOD_NAME, PretupsErrorCodesI.GENERATE_FOC_BATCH_MASTER_TRANSFER_ID_FAILED);
	         } finally {
	             if (log.isDebugEnabled()) {
	                 log.debug(METHOD_NAME, "Exited  " + p_batchMasterVO.getBatchId());
	             }
	         }
	         return;
	     }

	     /**
	      * Method genrateFOCBatchDetailTransferID.
	      * This method is called generate FOC batch detail transferID
	      * 
	      * @param p_batchMasterID
	      *            String
	      * @param p_tempNumber
	      *            long
	      * @throws BTSLBaseException
	      * @return String
	      */

	     private String genrateFOCBatchDetailTransferID(String p_batchMasterID, long p_tempNumber) throws BTSLBaseException {
	         final String METHOD_NAME = "genrateFOCBatchDetailTransferID";
	         if (log.isDebugEnabled()) {
	             log.debug("genrateFOCBatchDetailTransferID", "Entered p_batchMasterID=" + p_batchMasterID + ", p_tempNumber= " + p_tempNumber);
	         }
	         String uniqueID = null;
	         try {
	             uniqueID = calculatorI.formatFOCBatchDetailsTxnID(p_batchMasterID, p_tempNumber);
	         } catch (Exception e) {
	             log.error(METHOD_NAME, "Exception " + e.getMessage());
	             log.errorTrace(METHOD_NAME, e);
	             throw new BTSLBaseException("FOCBatchTransferAction", METHOD_NAME, PretupsErrorCodesI.GENERATE_FOC_BATCH_DETAIL_TRANSFER_ID_FAILED);
	         } finally {
	             if (log.isDebugEnabled()) {
	                 log.debug(METHOD_NAME, "Exited  " + uniqueID);
	             }
	         }
	         return uniqueID;
	     }
	     
	     
	     /**
	      * Method genrateFOCBatchMasterTransferID.
	      * This method is called generate Direct Payout batch master transferID of
	      * "DP"
	      * 
	      * @param p_currentDate
	      *            Date
	      * @param p_networkCode
	      *            String
	      * @throws BTSLBaseException
	      * @return
	      */

	     private void genrateDPBatchMasterTransferID(FOCBatchMasterVO p_batchMasterVO) throws BTSLBaseException {
	         final String METHOD_NAME = "genrateDPBatchMasterTransferID";
	         if (log.isDebugEnabled()) {
	             log.debug(METHOD_NAME, "Entered p_batchMasterVO=" + p_batchMasterVO);
	         }
	         try {
	             final long txnId = IDGenerator.getNextID(PretupsI.DP_BATCH_TRANSACTION_ID, BTSLUtil.getFinancialYear(), PretupsI.ALL, p_batchMasterVO.getCreatedOn());
	             p_batchMasterVO.setBatchId(calculatorI.formatDPBatchMasterTxnID(p_batchMasterVO, txnId));
	         } catch (Exception e) {
	             log.error(METHOD_NAME, "Exception " + e.getMessage());
	             log.errorTrace(METHOD_NAME, e);
	             throw new BTSLBaseException("FOCBatchTransferAction", METHOD_NAME, PretupsErrorCodesI.GENERATE_DP_BATCH_MASTER_TRANSFER_ID_FAILED);
	         } finally {
	             if (log.isDebugEnabled()) {
	                 log.debug(METHOD_NAME, "Exited  " + p_batchMasterVO.getBatchId());
	             }
	         }
	         return;
	     }
	     
	     /**
	      * Method genrateDPBatchDetailTransferID.
	      * This method is called generate Direct Payout batch master transferID of
	      * "DP"
	      * 
	      * @param p_batchMasterID
	      *            String
	      * @param p_tempNumber
	      *            Long
	      * @throws BTSLBaseException
	      * @return uniqueID String
	      */

	     private String genrateDPBatchDetailTransferID(String p_batchMasterID, long p_tempNumber) throws BTSLBaseException {
	         final String METHOD_NAME = "genrateDPBatchDetailTransferID";
	         if (log.isDebugEnabled()) {
	             log.debug(METHOD_NAME, "Entered p_batchMasterID=" + p_batchMasterID + ", p_tempNumber= " + p_tempNumber);
	         }
	         String uniqueID = null;
	         try {
	             uniqueID = calculatorI.formatDPBatchDetailsTxnID(p_batchMasterID, p_tempNumber);
	         } catch (Exception e) {
	             log.error(METHOD_NAME, "Exception " + e.getMessage());
	             log.errorTrace(METHOD_NAME, e);
	             throw new BTSLBaseException("FOCBatchTransferAction", METHOD_NAME, PretupsErrorCodesI.GENERATE_DP_BATCH_DETAIL_TRANSFER_ID_FAILED);
	         } finally {
	             if (log.isDebugEnabled()) {
	                 log.debug(METHOD_NAME, "Exited  " + uniqueID);
	             }
	         }
	         return uniqueID;
	     }
	     
		@Override
		public FileDownloadResponse userListDownload(Connection con, BatchFOCFileDownloadRequestVO batchFOCFileDownloadRequestVO, UserVO userVO) throws Exception {
			
			LinkedHashMap userDetails = new LinkedHashMap(); 
			ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
			String filePath = "";
			String fileName = "";
			FOCBatchTransferActionOne focBatchTransferActionOne = new FOCBatchTransferActionOne();
			String methodName = "userListDownload";
			String domain = batchFOCFileDownloadRequestVO.getDomain();
			String category = batchFOCFileDownloadRequestVO.getCategory();
			String geoDomain = batchFOCFileDownloadRequestVO.getGeography();
			FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
			String networkID = userVO.getNetworkID();
			
			final Date currDate = new Date();
			String fileExt = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
			Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			
            filePath = Constants.getProperty("DownloadBatchFOCUserListFilePath");
            fileName = Constants.getProperty("DownloadBatchFOCUserListFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + ".xls";
			
			String fileArr[][] = null;
			 final SubLookUpDAO subLookupDAO = new SubLookUpDAO();
	         FileWriteUtil.p_bonusTypeList = subLookupDAO.loadSublookupByLookupType(con, PretupsI.BONUS_TYPE);
			
			if ("userList".equalsIgnoreCase(batchFOCFileDownloadRequestVO.getFileType())) {
				
	            if (geoDomain != null && geoDomain.equals(PretupsI.ALL)) {
	                geoDomain = this.generateCommaString(new DownloadUserListServiceImpl().loadGeoDomainList(con, (ChannelUserVO) userVO));
	            } else if (geoDomain != null) {
	                geoDomain = "'" + geoDomain + "'";
	            }
	            if (category != null && category.equals(PretupsI.ALL)) {
	                category = this.generateCommaString(new DownloadUserListServiceImpl().getCategoryList(con, domain, userVO.getNetworkID()));
	            }
	            else if(category != null)
	            {
	            	category = "'" + category + "'";
	            }
	         
	            
	         // This preference not required any more, ChannelAdmin got the control to select which Commission wallet through UI.
//				if (((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue()) {
	            
	       if( PretupsI.COMM_PAYOUT.equalsIgnoreCase(batchFOCFileDownloadRequestVO.getSelectedCommissionWallet())) {        
					userDetails = channelUserWebDAO.loadUsersForBatchDP(con, domain, category, networkID, geoDomain,
							currDate);
					
					if (userDetails!= null &&  userDetails.size()==0 ) {
						 //No users exists under selected category and domain
						 throw new BTSLBaseException(this, "userListDownload", PretupsErrorCodesI.NO_USER_EXIST_DOMCAT);
					 }

					filePath = Constants.getProperty("DownloadBatchDirectPayoutUserListFilePath");
					fileName = Constants.getProperty("DownloadBatchDirectPayoutUserListFileName")
							+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "."+ fileExt;

					final int cols = 10;
					final int rows = userDetails.size() + 1;
					fileArr = new String[rows][cols]; // ROW-COL
					fileArr[0][0] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.msisdn");
					fileArr[0][1] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.loginid");
					fileArr[0][2] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.usercategory");
					fileArr[0][3] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.usergrade");
					fileArr[0][4] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.exttxnnumber");
					fileArr[0][5] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.extntxndate");
					fileArr[0][6] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.externalcode");
					fileArr[0][7] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.quantity");
					fileArr[0][8] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.bonustype");
					fileArr[0][9] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.remarks");

					fileArr = focBatchTransferActionOne.convertTo2dArrayForDP(fileArr, userDetails, rows, currDate);

				} else {
					userDetails = channelUserWebDAO.loadUsersForBatchFOC(con, domain, category, networkID, geoDomain,
							currDate);
					
					 if (userDetails!= null &&  userDetails.size()==0 ) {
						 //No users exists under selected category and domain
						 throw new BTSLBaseException(this, "userListDownload", PretupsErrorCodesI.NO_USER_EXIST_DOMCAT);
					 }

					fileName = Constants.getProperty("DownloadBatchFOCUserListFileName")
							+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;

					final int cols = 9;
					final int rows = userDetails.size() + 1;
					fileArr = new String[rows][cols]; // ROW-COL
					fileArr[0][0] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.msisdn");
					fileArr[0][1] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.loginid");
					fileArr[0][2] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.usercategory");
					fileArr[0][3] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.usergrade");
					fileArr[0][4] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.exttxnnumber");
					fileArr[0][5] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.extntxndate");
					fileArr[0][6] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.externalcode");
					fileArr[0][7] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.quantity");
					fileArr[0][8] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.remarks");

					fileArr = focBatchTransferActionOne.convertTo2dArray(fileArr, userDetails, rows, currDate);

				}
			} else {
//				if (((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue()) {
				if( PretupsI.COMM_PAYOUT.equalsIgnoreCase(batchFOCFileDownloadRequestVO.getSelectedCommissionWallet())) {	
					filePath = Constants.getProperty("DownloadBatchDirectPayoutUserListFilePath");
					fileName = Constants.getProperty("DownloadBatchFOCUserTemplateListFileName")
							+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "."+ fileExt;

					final int cols = 7;
					final int rows = 1;
					fileArr = new String[rows][cols]; // ROW-COL
					fileArr[0][0] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.msisdn");
					fileArr[0][1] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.exttxnnumber");
					fileArr[0][2] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.extntxndate");
					fileArr[0][3] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.externalcode");
					fileArr[0][4] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.quantity");
					fileArr[0][5] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.bonustype");
					fileArr[0][6] = BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.remarks");

				} else {
					filePath = Constants.getProperty("DownloadBatchFOCUserListFilePath");
					fileName = Constants.getProperty("DownloadBatchFOCUserTemplateListFileName")
							+ BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." +fileExt;

					final int cols = 6;
					final int rows = 1;
					fileArr = new String[rows][cols]; // ROW-COL
					fileArr[0][0] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.msisdn");
					if (PretupsI.YES.equals(SystemPreferences.EXTERNAL_TXN_MANDATORY_FORFOC)) {
						fileArr[0][1] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.exttxnnumber.mandatory");
						fileArr[0][2] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.extntxndate.mandatory");
					} else {
						fileArr[0][1] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.exttxnnumber");
						fileArr[0][2] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.extntxndate");
					}
					if (SystemPreferences.EXTERNAL_CODE_MANDATORY_FORFOC) {
						fileArr[0][3] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.externalcode.mandatory");
					} else {
						fileArr[0][3] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.externalcode");
					}
					fileArr[0][4] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.quantity");
					fileArr[0][5] = BTSLUtil.getMessage(locale,"batchfoc.xlsheading.label.remarks");

				}

			}
			try {
				final File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
				log.error(methodName, "Exception" + e.getMessage());
				throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated",
						"selectCategoryForBatchFOC");

			}
						 
			String noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");
			if ("csv".equals(fileExt)) 
			{
				FileWriteUtil.writeinCSV(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
			} else if ("xls".equals(fileExt)) 
			{
				FileWriteUtil.writeinXLS(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName, noOfRowsInOneTemplate, 1);
			} else if ("xlsx".equals(fileExt)) 
			{
				FileWriteUtil.writeinXLSX(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName, noOfRowsInOneTemplate, 1);
			} else 
			{
				throw new BTSLBaseException( DownloadUserListController.class.getName(),methodName,PretupsErrorCodesI.FILE_FORMAT_NOT_SUPPORTED,new String[] { fileExt });
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
	        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
	        
	        fileDownloadResponse.setMessage(resmsg);

		

        	return fileDownloadResponse;
		}
		
}