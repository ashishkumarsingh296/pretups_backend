package com.restapi.o2c.service;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.spring.custom.action.Globals;
import com.btsl.util.MessageResources;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelRW;
import com.restapi.o2c.service.bulko2capprovalrequestvo.BulkO2CApprovalRequestVO;

@Service("O2CBatchApprovalServiceI")
public class O2CBatchApprovalServiceImpl implements O2CBatchApprovalServiceI {

	protected final Log log = LogFactory.getLog(getClass().getName());
	
	@Override
	public void getBulkO2CApprovalList(BulkO2CApprovalRequestVO bulkO2CApprovalRequestVO, String msisdn, Locale locale,
			O2CApprovalListVO response, HttpServletResponse responseSwag, Connection con) throws BTSLBaseException {
		
		final String methodName = "getBulkO2CApprovalList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		UserDAO userDAO = null;
		ArrayList<O2CBatchMasterVO> approvalListO2CTransfer = null;
		ArrayList<O2CBatchMasterVO> approvalListO2CWithdrawal = null;
		ArrayList<O2CBatchMasterVO> approvalListFOC = null;
		ArrayList<O2CBatchMasterVO> approvalListBulkCommission = null;
		BatchO2CTransferDAO transferDAO = new BatchO2CTransferDAO();
		HashMap<String, ArrayList<O2CBatchMasterVO>> approvalList = new HashMap<String, ArrayList<O2CBatchMasterVO>>();

		
		try {
			userDAO = new UserDAO();

			UserVO channelAdminVO= userDAO.loadUsersDetails(con,msisdn);
			String userId = channelAdminVO.getUserID();
			
			String approvalLevel = bulkO2CApprovalRequestVO.getApprovalLevel();
			String approvalType = bulkO2CApprovalRequestVO.getApprovalType();
			String category = bulkO2CApprovalRequestVO.getCategory();
			String domain = bulkO2CApprovalRequestVO.getDomain();
			String geoDomain = bulkO2CApprovalRequestVO.getGeographicalDomain();
			
			String level = null;
			if("1".equals(approvalLevel))
				level = PretupsI.CHANNEL_TRANSFER_ORDER_NEW;
			else if("2".equals(approvalLevel))
				level = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
			else if("3".equals(approvalLevel))
				level = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
			
			if(level == null){
				String args[] = {approvalLevel};
				throw new BTSLBaseException("BatchApprovalServiceImpl",methodName,PretupsErrorCodesI.INVALID_O2C_APPROVAL_LEVEL,args);
			}
			
			if(!PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(approvalType) && !PretupsI.O2C_MODULE.equalsIgnoreCase(approvalType)) {
				throw new BTSLBaseException("BatchApprovalServiceImpl",methodName,PretupsErrorCodesI.INVALID_APPROVAL_TYPE);
			}
			
			if(BTSLUtil.isNullString(domain) || BTSLUtil.isNullString(geoDomain) || BTSLUtil.isNullString(category))
				throw new BTSLBaseException("BatchApprovalServiceImpl",methodName,PretupsErrorCodesI.INVAILD_SEARCH_CRITERIA);
			
			if(PretupsI.O2C_MODULE.equalsIgnoreCase(approvalType)) {
				
				approvalListO2CTransfer = transferDAO.loadO2CTransferApprovalList(con, domain, category, geoDomain, userId, level);
				approvalListO2CWithdrawal= transferDAO.loadO2CWithdrawal_FOCApprovalList(con, domain, category, geoDomain, userId, level, PretupsI.O2C_BATCH_TRANSACTION_ID);
				
				if (log.isDebugEnabled()) {
	        		log.debug(methodName, "O2C Transfer Approval Count: " + approvalListO2CTransfer.size());
	        		log.debug(methodName, "O2C Withdrawal Approval Count: " + approvalListO2CWithdrawal.size());
	        		log.debug(methodName, "O2C Approval Count(T and W): " + (approvalListO2CTransfer.size() + approvalListO2CWithdrawal.size()));
	        	}
				
				approvalList.put("O2C_Purchase",approvalListO2CTransfer);
				approvalList.put("O2C_Withdrawal",approvalListO2CWithdrawal);
			}
			
			if(PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(approvalType)) {
				//Clubbing both the lists
					approvalListFOC = transferDAO.loadO2CWithdrawal_FOCApprovalList(con, domain, category, geoDomain, userId, level,PretupsI.FOC_BATCH_TRANSACTION_ID);
					log.debug(methodName, "FOC Approval Count: " + approvalListFOC.size());
					approvalListBulkCommission = transferDAO.loadO2CWithdrawal_FOCApprovalList(con, domain, category, geoDomain, userId, level,PretupsI.DP_BATCH_TRANSACTION_ID);
					log.debug(methodName, "Bulk Commision Approval Count: " + approvalListBulkCommission.size());
					approvalListBulkCommission.addAll(approvalListFOC);
					approvalList.put("BulkCom", approvalListBulkCommission);
//				}
				
				
				
			}
			
			response.setBulkApprovalList(approvalList);
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg  = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);
		}
		catch (BTSLBaseException be) {
       	 	log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            throw new BTSLBaseException("BatchApprovalServiceImpl",methodName,be.getMessageKey(),be.getArgs());
        }
        catch (Exception e) {
        	log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            throw new BTSLBaseException("BatchApprovalServiceImpl",methodName,e.getMessage());

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
}
	public O2CBatchApprovalDetailsResponse processO2CBatchApprovalDetails(Connection con ,O2CBatchApprovalDetailsRequestVO batchapprovalDetailsRequest,
			String msisdn, Locale locale, O2CBatchApprovalDetailsResponse response, HttpServletResponse response1,HttpServletRequest httprequest) throws BTSLBaseException, SQLException{
		final String methodName = "processO2CBatchApprovalDetails"; 
		if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
		
		BatchO2CMasterVO batchO2CMasterVO =null;
		 UserDAO userDAO = null;
		 BatchO2CTransferDAO batchO2CTransferDAO =null;
		 try {
			 batchO2CTransferDAO=new BatchO2CTransferDAO();
			 userDAO = new UserDAO();
			 batchO2CMasterVO = new BatchO2CMasterVO();
				
				
			 UserVO channelAdminVO= userDAO.loadUsersDetails(con,msisdn);
			 String userId = channelAdminVO.getUserID();
			 String approvalLevel = batchapprovalDetailsRequest.getData().getApprovalLevel();
			 String approvalType = batchapprovalDetailsRequest.getData().getApprovalType();
			 String batchId = batchapprovalDetailsRequest.getData().getBatchId();
			 String approvalSubType = batchapprovalDetailsRequest.getData().getApprovalSubType();
			 String focOrCommPayout =null;
			 
			 String level = null;
				if("1".equals(approvalLevel))
					level = PretupsI.CHANNEL_TRANSFER_ORDER_NEW;
				else if("2".equals(approvalLevel))
					level = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1;
				else if("3".equals(approvalLevel))
					level = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2;
				
				
				if(level == null){
					String args[] = {approvalLevel};
					throw new BTSLBaseException("O2CBatchTransferServiceImpl",methodName,PretupsErrorCodesI.INVALID_O2C_APPROVAL_LEVEL,args);
				}
				
				if(!PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(approvalType) && !PretupsI.O2C_MODULE.equalsIgnoreCase(approvalType)) {
					throw new BTSLBaseException("O2CBatchTransferServiceImpl",methodName,PretupsErrorCodesI.INVALID_APPROVAL_TYPE);
				}
				
				if(BTSLUtil.isNullString(batchId))
					throw new BTSLBaseException("O2CBatchTransferServiceImpl",methodName,PretupsErrorCodesI.EMPTY_BATCH_ID);
				
				if(BTSLUtil.isNullString(approvalSubType) && PretupsI.O2C_MODULE.equalsIgnoreCase(approvalType))
					throw new BTSLBaseException("O2CBatchTransferServiceImpl",methodName,PretupsErrorCodesI.INVAILD_TRANSFERSUB_TYPE);
			
				if(PretupsI.O2C_MODULE.equalsIgnoreCase(approvalType) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(approvalSubType))
					response = batchO2CTransferDAO.loadBatchTransferApprovalDetails(con, batchId, userId, level);
					
				if(PretupsI.O2C_MODULE.equalsIgnoreCase(approvalType) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equalsIgnoreCase(approvalSubType)) 
					response = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con, batchId, userId, level,approvalType);
			    FOCBatchTransferDAO fOCBatchTransferDAO = new FOCBatchTransferDAO();	
			    ArrayList<FOCBatchMasterVO> listFocBatchMasterVO =  fOCBatchTransferDAO.getComissionWalletType(con, batchId);
			    
			     if(listFocBatchMasterVO!=null && listFocBatchMasterVO.size()>0) {
			    	 FOCBatchMasterVO fOCBatchMasterVO =   listFocBatchMasterVO.get(0);
			    	 focOrCommPayout =  fOCBatchMasterVO.getFocOrCommPayout();
			     }
			    
				if(PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(approvalType)) {
					   if(PretupsI.FOC_TRANSFER.equals( focOrCommPayout)){
						   response = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con,batchId, userId, level,PretupsI.FOC_BATCH_TRANSACTION_ID);
					   }
					  else {
						   //Direct payout transfer
						  response = batchO2CTransferDAO.loadBatchWithdrawalorFOCApprovalDetails(con, batchId, userId, level,PretupsI.DP_BATCH_TRANSACTION_ID);
					  }
												
				}
				
				if(BTSLUtil.isNullString(response.getApprovalDetails().getBatchId())) {
					response.setStatus(PretupsI.RESPONSE_SUCCESS);
					response.setMessageCode(PretupsErrorCodesI.SUCCESS);
					response.setMessage("Batch ID not Found in DataBase.");
					response1.setStatus(PretupsI.RESPONSE_SUCCESS);
					return response;
				}
					String statusUsed = null;
		            String exelFileID = null;
		            boolean isNotApprovalLevel1 = true;
		            
		            if ("1".equals(approvalLevel)) {
		                 exelFileID = ExcelFileIDI.BATCH_O2C_APPRV1;
		                isNotApprovalLevel1 = false;
		                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "'";
		            }
		            if ("2".equals(approvalLevel)) {
		                exelFileID = ExcelFileIDI.BATCH_O2C_APPRV2;
		                statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "'";
		            }
		            if("3".equals(approvalLevel)) {
		            	exelFileID = ExcelFileIDI.BATCH_O2C_APPRV3;
		            	statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2  + "'";
		            }
				 
		           LinkedHashMap downloadDataMap = batchO2CTransferDAO.loadBatchO2CItemsMap(con, response.getApprovalDetails().getBatchId(), statusUsed,approvalType,
		                   approvalSubType);
		             batchO2CMasterVO.setDownLoadDataMap(downloadDataMap);
		          String name= response.getApprovalDetails().getBatchFileName();
		          String extension = getExtensionByApacheCommonLib(name);
		       		     
		         
		           String filePath = Constants.getProperty("DownloadFilePathForO2CApproval");
		           try {
		                final File fileDir = new File(filePath);
		                if (!fileDir.isDirectory()) {
		                    fileDir.mkdirs();
		                }
		            } catch (Exception e) {
		                log.errorTrace(methodName, e);
		                log.error("loadDownloadFile", "Exception" + e.getMessage());
		                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "error");
		            }
		           String fileType= null;
		           String fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;
		           if(extension.equals("csv")) {
		        	   if(PretupsI.O2C_MODULE.equalsIgnoreCase(approvalType) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(approvalSubType)) { 
		        	   fileType= "."+extension;
		        	    fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

			        	  writeToCsv(downloadDataMap, filePath+fileName,isNotApprovalLevel1,locale);
			        	  }
		        	   else {
		        		   fileType= "."+extension;
			        	    fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

				        	  writeToCsvforWithdrawalandFOC(downloadDataMap, filePath+fileName,isNotApprovalLevel1,locale,approvalLevel,approvalType);
		        	   }
			          }
		            else if (extension.equals("xlsx")) {
		            	 
		            	if(PretupsI.O2C_MODULE.equalsIgnoreCase(approvalType) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(approvalSubType)) {
		            	String fileArr[][] = constructFileArrForDownload(downloadDataMap, isNotApprovalLevel1,locale);
		            	
		            	fileType= "."+extension;
				        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

		            	 ExcelRW excelRW = new ExcelRW();
				            // Write the excel file for download
				            excelRW
				                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
		            	}
		            	else {
		            		String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevel,approvalType);
			            	
			            	fileType= "."+extension;
					        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

			            	 ExcelRW excelRW = new ExcelRW();
					            // Write the excel file for download
					            excelRW
					                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
			            	
		            	}
		            }
		            else {
		            	if(PretupsI.O2C_MODULE.equalsIgnoreCase(approvalType) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(approvalSubType)) {
			            	String fileArr[][] = constructFileArrForDownload(downloadDataMap, isNotApprovalLevel1,locale);
			            	
			            	fileType= ".xls";
					        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

			            	 ExcelRW excelRW = new ExcelRW();
					            // Write the excel file for download
					            excelRW
					                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
			            	}
			            	else {
			            		String fileArr[][] = constructFileArrForDownloadForFOCandWithdrawal(downloadDataMap, isNotApprovalLevel1,locale,approvalLevel,approvalType);
				            	
				            	fileType= ".xls";
						        fileName = Constants.getProperty("DownloadFileNameForO2CApproval") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + fileType;

				            	 ExcelRW excelRW = new ExcelRW();
						            // Write the excel file for download
						            excelRW
						                .writeExcel(exelFileID, fileArr, ((MessageResources) httprequest.getAttribute(Globals.MESSAGES_KEY)), BTSLUtil.getBTSLLocale(httprequest), filePath + "" + fileName);
				            	
			            	}
		            }
		            File download =new File(filePath+fileName);
					byte[] fileContent = FileUtils.readFileToByteArray(download);
			   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
				
				response.setFileAttachment(encodedString);
				response.setFileName(fileName);
				response.setFileType(fileType);
				response.setStatus(PretupsI.RESPONSE_SUCCESS);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				String resmsg  = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				response1.setStatus(PretupsI.RESPONSE_SUCCESS);
				
			
		 
		 }catch (BTSLBaseException be) {
	       	 	log.error(methodName, "Exception:e=" + be);
	            log.errorTrace(methodName, be);
	            throw new BTSLBaseException("O2CBatchApprovalServiceImpl",methodName,be.getMessageKey(),be.getArgs());
	        }
	        catch (Exception e) {
	        	log.error(methodName, "Exception:e=" + e);
	            log.errorTrace(methodName, e);
	            throw new BTSLBaseException("O2CBatchApprovalServiceImpl",methodName,e.getMessage());
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
		return response;
	}
	
	public String getExtensionByApacheCommonLib(String filename) {
	    return FilenameUtils.getExtension(filename);
	}
	private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    
    
	 private void writeToCsv(LinkedHashMap<String, Object> meterMap,String filename,boolean p_level2DetailsShown,Locale p_locale) {
		 String METHOD_NAME = "writeToCsv";
		 String FILE_HEADER =  BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchdetailno", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.msisdn", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.loginid", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchno", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usercategory", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usergrade", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.esternaltxnno", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.paymenttype", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatorby", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatoron", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externaltxndate", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externalcode", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.reqquantity", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.payableamount", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.netpayableamount", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissionprofilesetid", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissionprofileversion", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissionprofiledetail", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax1type", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax1rate", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax1value", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax2type", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax2rate", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax2value", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax3type", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax3rate", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax3value", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissiontype", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commisionrate", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissionvalue", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.apprv1by", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.apprv1on", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.reqquantity", null)+",";
		 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.apprv1qty", null)+",";
	        if (p_level2DetailsShown) {
	        	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null)+",";
	        	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null)+",";
	        	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
	        } else {
	        	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null)+",";
	        	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null)+",";
	        	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
	        }
	        
	        
	        
	        
	        try {
	        	PrintWriter writer = new PrintWriter(new File(filename));
	        	BatchO2CItemsVO batchO2CItemsVO = null;
	        	StringBuilder fw = new StringBuilder();
	            fw.append(FILE_HEADER);
	            fw.append(NEW_LINE_SEPARATOR);
	        	Iterator<String> iterator = meterMap.keySet().iterator();
	        	String key =null;
	        	String value=null;
	        	  while (iterator.hasNext()) {
	        		
	        		  key = (String) iterator.next();
	        		  batchO2CItemsVO = (BatchO2CItemsVO) meterMap.get(key);
	        		 
	        		  value = batchO2CItemsVO.getMsisdn()+",";
	        		  value +=  batchO2CItemsVO.getLoginID()+",";
	        		  value +=  batchO2CItemsVO.getBatchId()+",";
	        		  value +=  batchO2CItemsVO.getCategoryName()+",";
	        		  value +=  batchO2CItemsVO.getGradeName()+",";
	        		  value +=  batchO2CItemsVO.getExtTxnNo()+",";
	        		  value +=  batchO2CItemsVO.getPaymentType()+",";
	        		  value +=  batchO2CItemsVO.getInitiaterName()+",";
	        		  try {
	        			  value += BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getInitiatedOn()))+",";
	                  } catch (Exception e) {
	                	  value+= ""+",";
	                      log.errorTrace(METHOD_NAME, e);
	                  }
	                  try {
	                	  value += BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getExtTxnDate()))+",";	
	                  } catch (Exception e) {
	                	  value+= ""+",";
	                      log.errorTrace(METHOD_NAME, e);
	                  }
	                  value += batchO2CItemsVO.getExternalCode()+",";
	                  try {
	                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity())+",";
	                  } catch (Exception e) {
	                	  value+= ""+",";
	                      log.errorTrace(METHOD_NAME, e);
	                  }
	                  try {
	                	  
	                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getPayableAmount())+",";
	                  } catch (Exception e) {
	                	  value+= ""+",";
	                      log.errorTrace(METHOD_NAME, e);
	                  }
	                  try {
	                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getNetPayableAmount())+",";
	                  } catch (Exception e) {
	                	  value+= ""+",";
	                      log.errorTrace(METHOD_NAME, e);
	                  }
	                  value += batchO2CItemsVO.getCommissionProfileSetId()+",";
	                  value += batchO2CItemsVO.getCommissionProfileVer()+",";
	                  value += batchO2CItemsVO.getCommissionProfileDetailId()+",";
	                  value += batchO2CItemsVO.getTax1Type()+",";
	                  if(batchO2CItemsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
	                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax1Rate())+",";	
	                  else
	                	  value += String.valueOf(batchO2CItemsVO.getTax1Rate())+",";
	                  
	                  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax1Value())+",";
	                  value += batchO2CItemsVO.getTax2Type()+",";
	                  if(batchO2CItemsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
	                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax2Rate())+",";	
	                  else
	                	  value += String.valueOf(batchO2CItemsVO.getTax2Rate())+",";
	                  
	                  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax2Value())+",";
	                  value += batchO2CItemsVO.getTax3Type()+",";
	                  if(batchO2CItemsVO.getTax3Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
	                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax3Rate())+",";	
	                  else
	                	  value += String.valueOf(batchO2CItemsVO.getTax3Rate())+",";
	                  
	                  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax3Value())+",";
	                  value += batchO2CItemsVO.getCommissionType()+",";
	      			 if(batchO2CItemsVO.getCommissionType().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
	                  	value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getCommissionRate())+",";	
	                  else
	                	  value += String.valueOf(batchO2CItemsVO.getCommissionRate())+",";
	      			 	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getCommissionValue())+",";
	      			 	  value += batchO2CItemsVO.getFirstApproverName()+",";
	                  try {
	                	  value += BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getFirstApprovedOn()))+",";
	                  } catch (Exception e) {
	                	  value+=""+",";
	                	  log.errorTrace(METHOD_NAME, e);
	                  }
	                  try {
	                	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity())+",";
	                  } catch (Exception e) {
	                	  value+= ""+",";
	                      log.errorTrace(METHOD_NAME, e);
	                  }
	                  try {
	                      if(0 == batchO2CItemsVO.getFirstApprovedQuantity())
	                    	  value += ""+",";
	                  	else
	                  		value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getFirstApprovedQuantity())+",";
	                  	
	                  } catch (Exception e) {
	                	  value+= ""+",";
	                      log.errorTrace(METHOD_NAME, e);
	                  }
	                  
	                  value += ((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, batchO2CItemsVO.getStatus())).getLookupName();
	                 
	             
	        		  try {
		                    fw.append(key);
		                    fw.append(COMMA_DELIMITER);
		                    fw.append(value);
		                    fw.append(NEW_LINE_SEPARATOR);
		                    
		                } catch (Exception e) {
		                    e.printStackTrace();
		                } finally {
		                    iterator.remove();
		                }
	        		  
	        	  }
	        	
	           writer.write(fw.toString());
	         writer.close();
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	         	log.error("ne", "Exception:e=" + e);
	            log.errorTrace("new", e);
	        }
	     }
	
	
	
	private String[][] constructFileArrForDownload(LinkedHashMap p_dataMap, boolean p_level2DetailsShown,Locale p_locale) throws Exception {
		
        final String METHOD_NAME = "constructFileArrForDownload";
        if (log.isDebugEnabled()) {
            log.debug("constructFileArrForDownload", "Entered p_dataMap=" + p_dataMap + "p_level2DetailsShown=" + p_level2DetailsShown);
        }
        final int rows = p_dataMap.size();
        String fileArr[][];
            fileArr = new String[rows + 1][37];
        fileArr[0][0] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchdetailno", null);
        fileArr[0][1] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.msisdn", null);
        fileArr[0][2] =BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.loginid", null);
        fileArr[0][3] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchno", null);
        fileArr[0][4] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usercategory", null);
        fileArr[0][5] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usergrade", null);
        fileArr[0][6] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.esternaltxnno", null);
        fileArr[0][7] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.paymenttype", null);
        fileArr[0][8] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatorby", null);
        fileArr[0][9] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatoron", null);
        fileArr[0][10] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externaltxndate", null);
        fileArr[0][11] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externalcode", null);
        fileArr[0][12] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.reqquantity", null);
        fileArr[0][13] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.payableamount", null);
        fileArr[0][14] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.netpayableamount", null);
        fileArr[0][15] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissionprofilesetid", null);
        fileArr[0][16] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissionprofileversion", null);
        fileArr[0][17] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissionprofiledetail", null);
        fileArr[0][18] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax1type", null);
        fileArr[0][19] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax1rate", null);
        fileArr[0][20] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax1value", null);
        fileArr[0][21] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax2type", null);
        fileArr[0][22] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax2rate", null);
        fileArr[0][23] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax2value", null);
        fileArr[0][24] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax3type", null);
        fileArr[0][25] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax3rate", null);
        fileArr[0][26] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.tax3value", null);
        fileArr[0][27] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissiontype", null);
        fileArr[0][28] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commisionrate", null);
        fileArr[0][29] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.commissionvalue", null);
        fileArr[0][30] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.apprv1by", null);
        fileArr[0][31] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.apprv1on", null);
        fileArr[0][32] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.reqquantity", null);
        fileArr[0][33] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.apprv1qty", null);
        if (p_level2DetailsShown) {
            fileArr[0][34] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null);
            fileArr[0][35] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null);
            fileArr[0][36] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
        } else {
            fileArr[0][34] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null);
            fileArr[0][35] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null);
            fileArr[0][36] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
        }
        BatchO2CItemsVO batchO2CItemsVO = null;
        final Iterator iterator = p_dataMap.keySet().iterator();
        String key = null;
        int i = 0;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            batchO2CItemsVO = (BatchO2CItemsVO) p_dataMap.get(key);
            int col = 0;
            fileArr[i + 1][col++] = batchO2CItemsVO.getBatchDetailId();
            fileArr[i + 1][col++] = batchO2CItemsVO.getMsisdn();
            fileArr[i + 1][col++] = batchO2CItemsVO.getLoginID();
            fileArr[i + 1][col++] = batchO2CItemsVO.getBatchId();
            fileArr[i + 1][col++] = batchO2CItemsVO.getCategoryName();
            fileArr[i + 1][col++] = batchO2CItemsVO.getGradeName();
            fileArr[i + 1][col++] = batchO2CItemsVO.getExtTxnNo();
            fileArr[i + 1][col++] = batchO2CItemsVO.getPaymentType();
            fileArr[i + 1][col++] = batchO2CItemsVO.getInitiaterName();
            try {
                fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getInitiatedOn()));
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getExtTxnDate()));
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            fileArr[i + 1][col++] = batchO2CItemsVO.getExternalCode();
            try {
                fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity());
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getPayableAmount());
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getNetPayableAmount());
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            fileArr[i + 1][col++] = batchO2CItemsVO.getCommissionProfileSetId();
            fileArr[i + 1][col++] = batchO2CItemsVO.getCommissionProfileVer();
            fileArr[i + 1][col++] = batchO2CItemsVO.getCommissionProfileDetailId();
            fileArr[i + 1][col++] = batchO2CItemsVO.getTax1Type();
            if(batchO2CItemsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax1Rate());	
            else
            fileArr[i + 1][col++] = String.valueOf(batchO2CItemsVO.getTax1Rate());
            
            fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax1Value());
            fileArr[i + 1][col++] = batchO2CItemsVO.getTax2Type();
            if(batchO2CItemsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax2Rate());	
            else
            fileArr[i + 1][col++] = String.valueOf(batchO2CItemsVO.getTax2Rate());
            
            fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax2Value());
            fileArr[i + 1][col++] = batchO2CItemsVO.getTax3Type();
            if(batchO2CItemsVO.getTax3Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax3Rate());	
            else
            fileArr[i + 1][col++] = String.valueOf(batchO2CItemsVO.getTax3Rate());
            
            fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getTax3Value());
            fileArr[i + 1][col++] = batchO2CItemsVO.getCommissionType();
			 if(batchO2CItemsVO.getCommissionType().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getCommissionRate());	
            else
            fileArr[i + 1][col++] = String.valueOf(batchO2CItemsVO.getCommissionRate());
            fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getCommissionValue());
            fileArr[i + 1][col++] = batchO2CItemsVO.getFirstApproverName();
            try {
                fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getFirstApprovedOn()));
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
            	fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity());
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if(0 == batchO2CItemsVO.getFirstApprovedQuantity())
            		fileArr[i + 1][col++] = "";
            	else
            		fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getFirstApprovedQuantity());
            	
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            
            fileArr[i + 1][col++] = ((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, batchO2CItemsVO.getStatus())).getLookupName();
            i++;
        }
        if (log.isDebugEnabled()) {
            log.debug("constructFileArrForDownload", "Exiting fileArr:=" + fileArr);
        }
        return fileArr;
    }
private String[][] constructFileArrForDownloadForFOCandWithdrawal(LinkedHashMap p_dataMap, boolean p_level2DetailsShown,Locale p_locale,String approvedlevel,String approvalType) throws Exception {
		boolean bulkCommissionPayout=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue();
        final String METHOD_NAME = "constructFileArrForDownloadForFOCandWithdrawal";
        if (log.isDebugEnabled()) {
            log.debug("constructFileArrForDownload", "Entered p_dataMap=" + p_dataMap + "p_level2DetailsShown=" + p_level2DetailsShown);
        }
        int approvedLevel=Integer.parseInt(approvedlevel);
        int cols=15;
        if(PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(approvalType)) {
        	if(bulkCommissionPayout)
        		cols=16;
        }
        
        if (approvedLevel == 1) {
            cols = cols + 2;
        } else if (approvedLevel == 2|| approvedLevel==3) {
            cols = cols + 4;
        }
//        if (approvedLevel == 3) {
//            cols = cols + 6;
//        }

        final int rows = p_dataMap.size();
        String fileArr[][];
        fileArr = new String[rows+1][cols];
        int i=0;
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchdetailno", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.msisdn", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usercategory", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usergrade", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.loginid", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchno", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.reqquantity", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.esternaltxnno", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externaltxndate", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externalcode", null);
        
    	if(PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(approvalType)) {
    		if(bulkCommissionPayout) {
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.bonustype", null);
        }
    		}
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatorby", null);
        fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatoron", null);
       
    
        if (approvedLevel >= 1)// for first approval required then add
            // column in string array
            {
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.apprv1on", null);
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.apprv1by", null);
            }
            if (approvedLevel >= 2)// for second approval required then add
            // column in string array
            {
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.secondappron", null);
                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.secondapprby", null);
            }
//            if (approvedLevel >= 3)// for third approval required then add
//            // column in string array
//            {
//                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.thirdappron", null);
//                fileArr[0][i++] = BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.thirdapprby", null);
//            }
        
        if (p_level2DetailsShown) {
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null);
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null);
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
        } else {
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null);
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null);
            fileArr[0][i++] = BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
        }
           
        BatchO2CItemsVO batchO2CItemsVO = null;
        final Iterator iterator = p_dataMap.keySet().iterator();
        String key = null;
         i = 0;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            batchO2CItemsVO = (BatchO2CItemsVO) p_dataMap.get(key);
            int col = 0;
            fileArr[i + 1][col++] = batchO2CItemsVO.getBatchDetailId();
            fileArr[i + 1][col++] = batchO2CItemsVO.getMsisdn();
            fileArr[i + 1][col++] = batchO2CItemsVO.getCategoryName();
            fileArr[i + 1][col++] = batchO2CItemsVO.getGradeName();
            fileArr[i + 1][col++] = batchO2CItemsVO.getLoginID();
            fileArr[i + 1][col++] = batchO2CItemsVO.getBatchId();
            try {
                fileArr[i + 1][col++] = PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity());
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            fileArr[i + 1][col++] = batchO2CItemsVO.getExtTxnNo();
            fileArr[i + 1][col++] = batchO2CItemsVO.getExtTxnDateStr();
            fileArr[i + 1][col++] = batchO2CItemsVO.getExternalCode();
            if(PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(approvalType)) {
            if(bulkCommissionPayout) {
            	fileArr[i + 1][col++] = batchO2CItemsVO.getBonusType();
                }
            }
            
            fileArr[i + 1][col++] = batchO2CItemsVO.getInitiaterName();
            try {
                fileArr[i + 1][col++] = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getInitiatedOn()));
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
           
            if (approvedLevel >= 1)// for first approval required then add
                // column in string array
                {
                    if (batchO2CItemsVO.getFirstApprovedOn() != null) {
                    	fileArr[i+1][col++] = BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getFirstApprovedOn()));
                    } else {
                    	fileArr[i+1][col++] = null;
                    }
                    fileArr[i+1][col++] = batchO2CItemsVO.getFirstApprovedBy();
                   
                }
                if (approvedLevel >= 2)// for second approval required then add
                // column in string array
                {
                    if (batchO2CItemsVO.getSecondApprovedOn() != null) {
                    	fileArr[i+1][col++] = BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getSecondApprovedOn()));
                    } else {
                    	fileArr[i+1][col++] = null;
                    }
                    fileArr[i+1][col++] = batchO2CItemsVO.getSecondApprovedBy();
                    
                }
//                if (approvedLevel >= 3)// for third approval required then add
//                // column in string array
//                {
//                    if (batchO2CItemsVO.getThirdApprovedOn() != null) {
//                    	fileArr[i+1][col++] = BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getThirdApprovedOn()));
//                    } else {
//                    	fileArr[i+1][col++] = null;
//                    }
//                    fileArr[i+1][col++] = batchO2CItemsVO.getThirdApprovedBy();
//                  
//                }
                fileArr[i + 1][col++] = ((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, batchO2CItemsVO.getStatus())).getLookupName();
                
                i++;
            
        }
        if (log.isDebugEnabled()) {
            log.debug("constructFileArrForDownload", "Exiting fileArr:=" + fileArr);
        }
        return fileArr;
    }
private void writeToCsvforWithdrawalandFOC(LinkedHashMap<String, Object> meterMap,String filename,boolean p_level2DetailsShown,Locale p_locale,String approvedlevel,String approvalType) {
	 String METHOD_NAME = "writeToCsvforWithdrawalandFOC";	
	 boolean bulkCommissionPayout=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue();
	 int approvedLevel=Integer.parseInt(approvedlevel);
	 String FILE_HEADER =  BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchdetailno", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.msisdn", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usercategory", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.usergrade", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.loginid", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.batchno", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.reqquantity", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.esternaltxnno", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externaltxndate", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.externalcode", null)+",";
	 	if(PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(approvalType)) {
    		if(bulkCommissionPayout) {
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.bonustype", null)+",";
    		}
    		}
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatorby", null)+",";
	 	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.initiatoron", null)+",";
	 	
	 	
	 	 if (approvedLevel >= 1)// for first approval required then add
	            // column in string array
	            {
	 		FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.apprv1on", null)+",";
	 		FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.apprv1by", null)+",";
	            }
	            if (approvedLevel >= 2)// for second approval required then add
	            // column in string array
	            {
	            	FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.secondappron", null)+",";
	            	FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.secondapprby", null)+",";
	            }
//	            if (approvedLevel >= 3)// for third approval required then add
//	            // column in string array
//	            {
//	            	FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.thirdappron", null)+",";
//	            	FILE_HEADER += BTSLUtil.getMessage(p_locale, "batcho2c.downloadfileforo2cbatch.label.thirdapprby", null)+",";
//	            }
	        
       if (p_level2DetailsShown) {
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null)+",";
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null)+",";
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
       } else {
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.currentstatus", null)+",";
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.requiredaction", null)+",";
       	FILE_HEADER += BTSLUtil.getMessage(p_locale,"batcho2c.downloadfileforo2cbatch.label.remarks", null);
       }
       
       
       
       
       try {
       	PrintWriter writer = new PrintWriter(new File(filename));
       	BatchO2CItemsVO batchO2CItemsVO = null;
       	StringBuilder fw = new StringBuilder();
           fw.append(FILE_HEADER);
           fw.append(NEW_LINE_SEPARATOR);
       	Iterator<String> iterator = meterMap.keySet().iterator();
       	String key =null;
       	String value=null;
       	  while (iterator.hasNext()) {
       		
       		  key = (String) iterator.next();
       		  batchO2CItemsVO = (BatchO2CItemsVO) meterMap.get(key);
       		 
       		  value = batchO2CItemsVO.getMsisdn()+",";
       		  value +=  batchO2CItemsVO.getCategoryName()+",";
       		  value +=  batchO2CItemsVO.getGradeName()+",";
       		  value +=  batchO2CItemsVO.getLoginID()+",";
       		  value +=  batchO2CItemsVO.getBatchId()+",";
       		 try {
              	  value += PretupsBL.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity())+",";
                } catch (Exception e) {
              	  value+= ""+",";
                    log.errorTrace(METHOD_NAME, e);
                }
       		  value +=  batchO2CItemsVO.getExtTxnNo()+",";
       		 try {
              	  value += BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getExtTxnDate()))+",";	
                } catch (Exception e) {
              	  value+= ""+",";
                    log.errorTrace(METHOD_NAME, e);
                }
       		 value += batchO2CItemsVO.getExternalCode()+",";
       		if(PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(approvalType)) {
        		if(bulkCommissionPayout) {
       		 value += batchO2CItemsVO.getBonusType()+",";
        		}
        		}
       		 value +=  batchO2CItemsVO.getInitiaterName()+",";
       		  try {
       			  value += BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getInitiatedOn()))+",";
                 } catch (Exception e) {
               	  value+= ""+",";
                     log.errorTrace(METHOD_NAME, e);
                 }
                
       		  if (approvedLevel >= 1)// for first approval required then add
                  // column in string array
                  {
                      if (batchO2CItemsVO.getFirstApprovedOn() != null) {
                    	  value += BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getFirstApprovedOn()))+",";
                      } else {
                    	  value += " "+",";
                      }
                      value += batchO2CItemsVO.getFirstApprovedBy()+",";
                     
                  }
                  if (approvedLevel >= 2)// for second approval required then add
                  // column in string array
                  {
                      if (batchO2CItemsVO.getSecondApprovedOn() != null) {
                    	  value += BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getSecondApprovedOn()))+",";
                      } else {
                      	 value += ""+",";
                      }
                      value += batchO2CItemsVO.getSecondApprovedBy()+",";
                      
                  }
//                  if (approvedLevel >= 3)// for third approval required then add
//                  // column in string array
//                  {
//                      if (batchO2CItemsVO.getThirdApprovedOn() != null) {
//                    	  value += BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchO2CItemsVO.getThirdApprovedOn()))+",";
//                      } else {
//                    	  value += ""+",";
//                      }
//                      value += batchO2CItemsVO.getThirdApprovedBy()+",";
//                    
//                  }
                
                
                 
                
                 
                 value += ((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, batchO2CItemsVO.getStatus())).getLookupName();
                
            
       		  try {
	                    fw.append(key);
	                    fw.append(COMMA_DELIMITER);
	                    fw.append(value);
	                    fw.append(NEW_LINE_SEPARATOR);
	                    
	                } catch (Exception e) {
	                    e.printStackTrace();
	                } finally {
	                    iterator.remove();
	                }
       		  
       	  }
       	
          writer.write(fw.toString());
        writer.close();
       }
       catch (Exception e) {
           e.printStackTrace();
        	log.error("ne", "Exception:e=" + e);
           log.errorTrace("new", e);
       }
    }

}
