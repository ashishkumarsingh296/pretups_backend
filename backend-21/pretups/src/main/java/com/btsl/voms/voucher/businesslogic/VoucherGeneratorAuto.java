package com.btsl.voms.voucher.businesslogic;

/**
 * @(#)VoucherGeneratorAuto.java
 *                           All Rights Reserved
 *                           voucher generator
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Yogesh.Keshari 05/10/2017 Initial Creation
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 */
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;

/**
 * This class will be used for voucher generation of scheduled batches; Creation
 * 
 * @author yogesh.keshari
 */
public class VoucherGeneratorAuto {

    private static Log logger = LogFactory.getLog(VoucherGenerator.class.getName());
    private static long starttime = System.currentTimeMillis();
    
    private VoucherGeneratorAuto() {
		// TODO Auto-generated constructor stub
	}

    public static void main(String args[]) {
        final String METHOD_NAME = "main";
        try {
            if (args.length < 2 || args.length > 3) {
                System.out.println("Usage : VoucherGenerator [Constants file] [LogConfig file] [Y/N]");
                return;
            }
            // load constants.props
            File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println("VoucherGenerator" + " Constants File Not Found .............");
                logger.error("VoucherGenerator[main]", "Constants file not found on location: " + constantsFile.toString());
                return;
            }
            // load log config file
            File logFile = new File(args[1]);
            if (!logFile.exists()) {
                System.out.println("VoucherGenerator" + " Logconfig File Not Found .............");
                logger.error("VoucherGenerator[main]", "Logconfig File not found on location: " + logFile.toString());
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logFile.toString());
        }// end of try block
        catch (Exception e) {
        	LogFactory.printLog(METHOD_NAME, " Error in Loading Files .......: " + e.getMessage(), logger);
            logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch block
        try {
        	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_VOUCHER_CRTN_ALWD))).booleanValue())
        		process();
        }// end of try block
        catch (BTSLBaseException be) {
            logger.errorTrace(METHOD_NAME, be);
            logger.error("main", "BTSLBaseException : " + be.getMessage());
            return;
        }// end of catch block
        catch (Exception e) {
        	LogFactory.printLog(METHOD_NAME,"" + e.getMessage() , logger);
            logger.errorTrace(METHOD_NAME, e);
            return;
        }// end of catch block
        finally {
            VomsBatchInfoLog.log("Total time taken:" + (System.currentTimeMillis() - starttime));
            if (logger.isDebugEnabled()) {
                logger.info("main", "Exiting");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }// end of finally
    }

    private static void process() throws BTSLBaseException {
    	final String METHOD_NAME = "process";
    	LogFactory.printLog(METHOD_NAME,"Entered" , logger);
    	PushMessage pushMessage = null;
        final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));  
        final String msisdnString = Constants.getProperty("adminmobile");
        String message=null;
        String processId = null;
        ProcessBL processBL = null;
        Connection con = null;
        int beforeInterval = 0;
        ProcessStatusVO processStatusVO = null;
        Date currentDate = null;
        Date processedUpto = null;
        int updateCount = 0; // check process details are updated or not
        Date startdate = null;
      
        try {
            processId = ProcessI.VOMS_GEN_AUTO;
            con = OracleUtil.getSingleConnection();
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            beforeInterval = BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval() / (60 * 24));
            if (processStatusVO.isStatusOkBool()) {    /* To check under process of Status Start*/
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {   /* to check process executed Start*/
                    currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
                    startdate = processedUpto;
                    con.commit();
                    processedUpto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
                    processedUpto = currentDate;       
                    List productlist = null;
                    VomsProductDAO vomsDAO = new VomsProductDAO();
                    List voucherTypeList = vomsDAO.loadVoucherDetails(con);   
                    int voucherTypeListSize = voucherTypeList.size();
                    for (int i=0;i< voucherTypeListSize;i++)   /* iterate this loop for voucher type start start*/
                      {            	
                    	VoucherTypeVO voucherVO =(VoucherTypeVO) voucherTypeList.get(i); 
                    	
                    	VomsCategoryWebDAO vomsCategorywebDAO = new VomsCategoryWebDAO();
                        List categoryList = vomsCategorywebDAO.loadCategoryList(con, voucherVO.getVoucherType(), VOMSI.VOMS_STATUS_ACTIVE, VOMSI.EVD_CATEGORY_TYPE_FIXED, true, null, null);
                        int categoryListSize = categoryList.size();
                        for (int j = 0; j < categoryListSize; j++)  /* iterate this loop for voucher category based on type start*/
                        {	
                        	VomsCategoryVO vomsCategoryVO = (VomsCategoryVO) categoryList.get(j);
                        	VomsProductDAO vomsProductDAO = new VomsProductDAO();
                            productlist = vomsProductDAO.loadProductsListbyCategoryAndAutoGenY(con, vomsCategoryVO.getCategoryID());
                            int sequence[] = new int[productlist.size()];
                            Arrays.fill(sequence, 0);
                            int vouchers[] = new int[productlist.size()];
                            Arrays.fill(vouchers, 0);
                            ArrayList batchlist = new ArrayList();
                            int productlistSize = productlist.size();
                            for (int k=0;k< productlistSize;k++){  /* iterate this loop for voucher product based on category id start*/
                            	 
                            	  VomsProductVO vomsProductVO = (VomsProductVO) productlist.get(k);
                          	   	  VomsBatchesDAO vomsBatchesDAO =new VomsBatchesDAO();
                          	   	  long totalNumberOfGeneratedVoucher = vomsBatchesDAO.totalNumberOfAvailableVouchers(con, vomsProductVO.getProductID());
                          	   	  long totalNumberOfUsedVoucher = vomsBatchesDAO.totalNumberOfUSedVouchers(con, vomsProductVO.getProductID());
                          	   	  LogFactory.printLog("AutoVoucherGenProcess","ProductID: "+vomsProductVO.getProductID()+ " AvailableVoucher: "+(totalNumberOfGeneratedVoucher-totalNumberOfUsedVoucher)+" ThresholdValue: "+vomsProductVO.getVoucherThreshold(), logger);	 
                          	   	  if(totalNumberOfGeneratedVoucher-totalNumberOfUsedVoucher <= Long.parseLong(vomsProductVO.getVoucherThreshold())){  
                          	   		  			VomsBatchVO vomsBatchVO = new VomsBatchVO();
                      	   		  				String batchNo = null;
                      	   		  				String batches = null;
                      	   		  				ProcessStatusVO processStatusvo = null;
                      	   		  				ProcessStatusDAO processStatusDAO =new ProcessStatusDAO();
                      	   		  				processStatusvo = processStatusDAO.loadProcessDetail(con, processId);
                      	   		  				processStatusvo.setNetworkCode(processStatusvo.getNetworkCode());
                      	   		  				UserVO uservo =new UserVO();
                      	   		  				uservo.setNetworkID(vomsProductVO.getNetworkCode());
                      	   		  				batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
                      	   		  				vomsBatchVO.setBatchType(VOMSI.BATCH_INTIATED);
                      	   		  				vomsBatchVO.setProductID(vomsProductVO.getProductID());
                      	   		  				vomsBatchVO.setNoOfVoucher(Long.parseLong((vomsProductVO.getVoucherGenerateQuantity())));
                      	   		  				vomsBatchVO.setFromSerialNo("");
                      	   		  				vomsBatchVO.setToSerialNo("");
                      	   		  				vomsBatchVO.setFailCount(0);
                      	   		  				vomsBatchVO.setOneTimeUsage(PretupsI.YES);
                      	   		  				vomsBatchVO.setSuccessCount(0);
                      	   		  				vomsBatchVO.setLocationCode(processStatusvo.getNetworkCode());
                      	   		  				vomsBatchVO.setCreatedBy("AUTO");
                      	   		  				vomsBatchVO.setCreatedOn(currentDate);
                      	   		  				vomsBatchVO.setModifiedBy("AUTO");
                                 				vomsBatchVO.setModifiedOn(currentDate);
                                 				vomsBatchVO.setDownloadCount(0);
                                 				vomsBatchVO.setStatus(VOMSI.BATCH_ACCEPTED);
                                 				vomsBatchVO.setCreatedDate(currentDate);
                                 				vomsBatchVO.setModifiedDate(currentDate);
                                 				vomsBatchVO.setProcess(VOMSI.BATCH_PROCESS_INITIATE);
                                 				vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batchNo));
                                 				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()) vomsBatchVO.setSeq_id(new VomsBatchesDAO().generateSequenceNumber(con,sequence,vouchers));
                                 					sequence[k]=vomsBatchVO.getSeq_id();
                                 					vouchers[k]=Integer.parseInt(vomsProductVO.getVoucherGenerateQuantity());
                                 					vomsBatchVO.setMessage(" Batch SuccessFully Initiated ...........");
                                 					if (batches == null) {
                                 	                    batches = vomsBatchVO.getBatchNo();
                                 	                } else {
                                 	                    batches = batches + "," + vomsBatchVO.getBatchNo();
                                 	                }
                                                    batchlist.add(vomsBatchVO);         		 
                          		  
                          	   	  			}
                                                   
                           
                     
                        
                  
                    
                            }/* iterate this loop for voucher product based on category id END*/
                    	    if(batchlist!=null && !batchlist.isEmpty()){
                                int recordCount = new VomsBatchesDAO().addBatch(con, batchlist);
                   	            if (recordCount <= 0) {
                   	            	logger.error("VoucherGenerationAction[orderInitiate]", " Not able to insert batches ");
                   	                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherGEnerationAction[orderInitiate]", "", "", "", " The batch entry could not be made into the table");
                   	                throw new BTSLBaseException("VoucherUploadAction ", " orderInitiate ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                 	            } else {
                   	                con.commit();
                   	                VomsBatchInfoLog.addBatchLog(batchlist);
                   	            }
                      	   	  
                      	  }
                        }/* iterate this loop for voucher category based on type END*/
                  
                      }/* iterate this loop for voucher type start END*/
                 
                    
                      
                    VoucherGenerator voucherGenerator =new VoucherGenerator();
                    String generationType =VOMSI.AUTO;
                    boolean isDataProcessed = voucherGenerator.generateVouchers(con, startdate, processedUpto,generationType);
                    if (isDataProcessed) {
                        processStatusVO.setExecutedUpto(BTSLUtil.addDaysInUtilDate(processedUpto, -beforeInterval));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherAutoGenerator[process]", "", "", "", " VoucherAutoGenerator process has been executed successfully.");
                        LogFactory.printLog(METHOD_NAME, " successfully", logger);
                        message=" VoucherAutoGenerator process has been executed successfully";
                       
                    }
                } /* to check process executed END*/
            else {
                    throw new BTSLBaseException("VoucherAutoGenerator", METHOD_NAME, PretupsErrorCodesI.ERROR_VOMS_GEN);
                }
            
            } /* To check under process of Status END*/
            else {
                throw new BTSLBaseException("VoucherAutoGenerator", METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
        }/* try block END*/ 
        catch (BTSLBaseException be) {
            logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            throw be;
        } catch (Exception e) {
            logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherAutoGenerator[process]", "", "", "", " VoucherAutoGenerator process could not be executed successfully.");
            throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_VOMS_GEN);
        } finally {
            try {
                if (processStatusVO.isStatusOkBool()) {
                    processStatusVO.setStartDate(currentDate);
                    processStatusVO.setExecutedOn(currentDate);
                    processStatusVO.setExecutedUpto(currentDate);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    updateCount = (new ProcessStatusDAO()).updateProcessDetail(con, processStatusVO);
                    if (updateCount > 0) {
                        con.commit();
                    }
                }
            } catch (Exception ex) {
                logger.errorTrace(METHOD_NAME, ex);
                LogFactory.printLog(METHOD_NAME,"Exception in closing connection ", logger);
              
            }
            OracleUtil.closeQuietly(con);
            LogFactory.printLog(METHOD_NAME, "Exiting..... ", logger);
            // send the message as SMS
            final String[] msisdn = msisdnString.split(",");
            for (int i = 0; i < msisdn.length; i++) {
                pushMessage = new PushMessage(msisdn[i], message, null,processStatusVO.getNetworkCode(), locale);
                pushMessage.push();
            }
        }
    } //end of process

   
 

 
    
} // end class

