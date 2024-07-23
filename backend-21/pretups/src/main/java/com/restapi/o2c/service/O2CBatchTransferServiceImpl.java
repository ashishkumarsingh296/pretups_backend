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
import org.spring.custom.action.Globals;
import com.btsl.util.MessageResources;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchO2CProcessLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
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
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.web.pretups.channel.transfer.businesslogic.BatchO2CTransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;

@Service("O2CBatchServiceI")
public class O2CBatchTransferServiceImpl implements O2CBatchServiceI{
	
	protected final Log log = LogFactory.getLog(getClass().getName());
    private static OperatorUtilI calculatorI = null;
	private String filepathtemp;
	ArrayList<MasterErrorList> inputValidations=null;
	
	@Override
	public O2CBatchTransferResponse processRequest(
			O2CBatchTransferRequestVO o2CBatchTransferRequestVO, String serviceType,OperatorUtilI calculator ,
			String requestIDStr,Connection con , Locale locale,HttpServletRequest httprequest,
			MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException {
		final String methodName = "processRequest";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
	        calculatorI=calculator;
	        RestReceiver.updateRequestIdChannel();
	        LinkedHashMap<String, List<String>> bulkDataMap ;
	        ErrorMap errorMap = new ErrorMap();
	        O2CBatchTransferResponse response = new O2CBatchTransferResponse();
	        try {
	        	O2CBatchTransferDetails data = o2CBatchTransferRequestVO.getO2CBatchTransferDetails();
	        	inputValidations = new ArrayList<MasterErrorList>();
	        	final O2CBatchMasterVO batchMasterVO = new O2CBatchMasterVO();
				String msisdn = o2CBatchTransferRequestVO.getData().getMsisdn();
				ChannelUserVO senderVO=(ChannelUserVO)new UserDAO().loadUsersDetails(con, msisdn);
				 senderVO.setUserPhoneVO(new UserDAO().loadUserPhoneVO(con, senderVO.getUserID()));
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Entered Sender VO: " + senderVO);
				}
				
				if(PretupsI.YES.equals(senderVO.getCategoryVO().getSmsInterfaceAllowed())&& PretupsI.YES.equals(senderVO.getUserPhoneVO().getPinRequired()))
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
		                
		                if ((domainList == null || domainList.isEmpty()) && PretupsI.YES.equals(senderVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(senderVO
		                        .getCategoryVO().getFixedDomains())) {
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
				        if(validCategory==0){
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
				fileDetailsMap.put(PretupsI.FILE_TYPE1, o2CBatchTransferRequestVO.getO2CBatchTransferDetails().getFileType());
				fileDetailsMap.put(PretupsI.FILE_NAME,o2CBatchTransferRequestVO.getO2CBatchTransferDetails().getFileName());
				fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, o2CBatchTransferRequestVO.getO2CBatchTransferDetails().getFileAttachment());
				fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, serviceType);
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
	                        if (data.getChannelDomain().equals(domainTypeArr[i])) {
	                        	externalsTxnMandatory=PretupsI.YES;
	                            break;
	                        }
	                    }
	                }
	            }
	            
				bulkDataMap = fileUtil.uploadAndReadGenericFileO2CWithdraw(fileDetailsMap, 0, errorMap,externalCodeMandatory,externalsTxnMandatory,batchItemsList);
	            // If file does not contain record as entered by the user then
	            // show the error message
	            if(!BTSLUtil.isNullorEmpty(errorMap.getRowErrorMsgLists()))
				{
	            	filedelete();
					response.setErrorMap(errorMap);
		            writeFileForResponse(response, errorMap);
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
					processRequestUpload(response, batchMasterVO, con, senderVO, data, httprequest, batchItemsList,responseSwag);
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

	
	
	  private void genrateO2CBatchMasterTransferID(O2CBatchMasterVO p_batchMasterVO) throws BTSLBaseException {
	        final String methodName = "genrateO2CBatchMasterTransferID";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered p_batchMasterVO=" + p_batchMasterVO);
	        }
	        try {
	            final long txnId = IDGenerator.getNextID(PretupsI.O2C_BATCH_TRANSACTION_ID, BTSLUtil.getFinancialYear(), p_batchMasterVO.getNetworkCode(), p_batchMasterVO
	                .getCreatedOn());
	            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(txnId), 3);
	            String newStr = p_batchMasterVO.getNetworkCode()+PretupsI.O2C_BATCH_TRANSACTION_ID + BTSLDateUtil.getSystemLocaleDate(p_batchMasterVO.getCreatedOn(), false) + "." + paddedTransferIDStr;
	            p_batchMasterVO.setBatchId(newStr);
	        } catch (Exception e) {
	            log.error(methodName, "Exception " + e.getMessage());
	            log.errorTrace(methodName, e);
	            throw new BTSLBaseException("O2CBatchTransferAction", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
	        } finally {
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exited  " + p_batchMasterVO.getBatchId());
	            }
	        }
	        return;
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

	  
	  private String genrateO2CBatchDetailTransferID(String p_batchMasterID, long p_tempNumber) throws BTSLBaseException {
	        final String methodName = "genrateO2CBatchDetailTransferID";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered p_batchMasterID=" + p_batchMasterID + ", p_tempNumber= " + p_tempNumber);
	        }
	        String uniqueID = null;
	        try {
	            uniqueID = calculatorI.formatO2CBatchDetailsTxnID(p_batchMasterID, p_tempNumber);
	        } catch (Exception e) {
	            log.error(methodName, "Exception " + e.getMessage());
	            log.errorTrace(methodName, e);
	            throw new BTSLBaseException("O2CBatchTransferController", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
	        } finally {
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exited  " + uniqueID);
	            }
	        }
	        return uniqueID;
	    }

	  
	  private void writeFileForResponse(O2CBatchTransferResponse response, ErrorMap errorMap)throws BTSLBaseException, IOException{
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
			String filePathCons = Constants.getProperty("ErrorBatchO2CUserListFilePath");
			C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
			c2CFileUploadApiControllerObject.validateFilePathCons(filePathCons);
			
			String filePathConstemp = filePathCons + "temp/";        
			c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);
			

			String filepathtemp = filePathConstemp ;   

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
	  	private O2CBatchTransferResponse processRequestUpload(O2CBatchTransferResponse response, O2CBatchMasterVO batchMasterVO,Connection con, ChannelUserVO senderVO,O2CBatchTransferDetails data,HttpServletRequest httprequest,ArrayList batchItemsList,HttpServletResponse responseSwag ) throws BTSLBaseException{
	  		String methodName = "processRequestUpload";
	  		ProcessStatusVO processVO = null;
	        boolean processRunning = true;
	        final Date currentDate = new Date();
	        ErrorMap errorMap = new ErrorMap();
	        Locale locale = BTSLUtil.getBTSLLocale(httprequest);
	  		try{
	  		   final ProcessBL processBL = new ProcessBL();
	            try {
	            	 processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.O2C_BATCH_PROCESS_ID,senderVO.getNetworkID());
	            } catch (BTSLBaseException e) {
	                log.error(methodName, "Exception:e=" + e);
	                log.errorTrace(methodName, e);
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, "batcho2c.processRequest.error.alreadyexecution");
	            }
	            if (processVO != null && !processVO.isStatusOkBool()) {
	                processRunning = false;
	                throw new BTSLBaseException(this, methodName, "batcho2c.processRequest.error.alreadyexecution");
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
                category = this.generateCommaString((ArrayList)senderVO.getCategoryList());
            } else {
                catArr = category.split(":");
                category = "'" + category+ "'";
            }
            
            final Date curDate = new Date();
           
            // Construct o2cBatchMasterVO
            batchMasterVO.setNetworkCode(senderVO.getNetworkID());
            batchMasterVO.setNetworkCodeFor(senderVO.getNetworkID());
            batchMasterVO.setBatchName(data.getBatchName());
            batchMasterVO.setStatus(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_UNDERPROCESS);
            batchMasterVO.setCreatedBy(senderVO.getUserID());
            batchMasterVO.setCreatedOn(curDate);
            batchMasterVO.setModifiedBy(senderVO.getUserID());
            batchMasterVO.setModifiedOn(curDate);
            batchMasterVO.setDomainCode(data.getChannelDomain());
            batchMasterVO.setBatchFileName(data.getFileName());
            batchMasterVO.setBatchDate(curDate);
            batchMasterVO.setDefaultLang(data.getLanguage1());
            batchMasterVO.setSecondLang(data.getLanguage2());
            batchMasterVO.setCategoryCode(data.getUsercategory());
            this.genrateO2CBatchMasterTransferID(batchMasterVO);

            // 2D array processing starts here..
            long batchDetailID = 0; 
            for (int r = 0; r < batchItemsList.size(); r++) {
            	BatchO2CItemsVO batchItemsVO = (BatchO2CItemsVO)batchItemsList.get(r);
                    batchItemsVO.setBatchId(batchMasterVO.getBatchId());
                    batchItemsVO.setBatchDetailId(this.genrateO2CBatchDetailTransferID(batchMasterVO.getBatchId(), ++batchDetailID));
                    batchItemsVO.setModifiedBy(senderVO.getUserID());

            } // end of for loop
                  
            final BatchO2CTransferWebDAO batchO2CTransferwebDAO = new BatchO2CTransferWebDAO();
            // now validate the user's in the database.
            ArrayList fileErrorList = batchO2CTransferwebDAO.validateUsersForBatchO2C(con, batchItemsList, data.getChannelDomain(), category, senderVO.getNetworkID(), geoDomain,
            		curDate, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), locale);
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
						rowErrorMsgLists.setRowValue("Line" + String.valueOf(Long.parseLong(errorVO.getOtherInfo()) + 1));
						rowErrorMsgLists.setRowName(errorVO.getCodeName());
						if(errorMap.getRowErrorMsgLists() == null)
							errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
						(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
                    }
                }
            
                writeFileForResponse(response, errorMap);
                filedelete();
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
            final HashMap<String,ChannelTransferItemsVO> batchO2Cmap = new HashMap<String,ChannelTransferItemsVO>();
            fileErrorList = batchO2CTransferwebDAO.initiateO2CBatchTransfer(con, batchMasterVO, batchItemsList, locale,PretupsI.TRANSFER_TYPE_O2C, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER, batchO2Cmap);
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
				BatchO2CProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "PASS : Batch generated successfully",
                    "TOTAL RECORDS=" + processedRecords + ", FILE NAME=" + batchMasterVO.getBatchFileName());
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
                    BatchO2CProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "FAIL : All records contains DB error in batch",
                        "TOTAL RECORDS=0, FILE NAME=" + batchMasterVO.getBatchFileName());
                } else {
    	            String[] arg = { batchMasterVO.getBatchId(), data.getBatchName(), String.valueOf(processedRecords), String.valueOf(batchItemsList.size())};
 					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_O2C_TRF_PARTIAL_SUCCESS, arg);
                	response.setMessage(msg);
    				response.setMessageCode(PretupsErrorCodesI.BATCH_O2C_TRF_PARTIAL_SUCCESS);
					responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
					final BTSLMessages btslMessage = new BTSLMessages("batcho2c.processuploadedfile.msg.success", arr, "uploadBatchO2CResult");
                    BatchO2CProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "PASS : Batch generated successfully",
                        "TOTAL RECORDS=" + arr[2] + ", FILE NAME=" + batchMasterVO.getBatchFileName());
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
							rowErrorMsgLists.setRowValue("Line" + String.valueOf(Long.parseLong(errorVO.getIDValue()) + 1));
							rowErrorMsgLists.setRowName(errorVO.getCodeName());
							if(errorMap.getRowErrorMsgLists() == null)
								errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
							(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
	                    }
	                }
	            writeFileForResponse(response, errorMap);
				response.setStatus("400");
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

    /**
  		 * Method filedelete
  		 */

  		private void filedelete() {
  			if(!BTSLUtil.isNullString(filepathtemp))
  			{File file = new File(filepathtemp);
  			if (file.delete()) {
  				log.debug("filedelete", "******** Method filedelete :: Got exception and deleted the file");
  			}
  			}
  		}

  		
		//Validation for request input 
		private void requestValidation(O2CBatchTransferDetails data,Locale locale) {
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
}