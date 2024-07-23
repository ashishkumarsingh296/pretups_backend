/**
 * @(#)FOCBatchTransferActionOne.java
 *                                    This class is the controller class of the
 *                                    Initiate Batch FOC transfer Module.
 *                                    Copyright(c) 2006, Bharti Telesoft Ltd.
 *                                    All Rights Reserved
 * 
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Amit Ruwali 22/06/2006 Initial Creation
 * 
 */

package com.web.pretups.channel.transfer.web;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
//import com.btsl.common.BTSLDispatchAction;
import com.btsl.common.BTSLMessages;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FocListValueVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.logging.BatchFocFileProcessLog;
import com.btsl.pretups.logging.DirectPayOutErrorLog;
import com.btsl.pretups.logging.DirectPayOutSuccessLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelRW;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.channel.transfer.businesslogic.FOCBatchTransferWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class FOCBatchTransferActionOne  {



    protected static final Log LOG = LogFactory.getLog(FOCBatchTransferActionOne.class.getName());


    /**
     * Field calculatorI.
     */
    private static final Log logger = LogFactory.getLog(FOCBatchTransferActionOne.class.getName());
    public static OperatorUtilI calculatorI = null;
    // calculate the tax
    static {
        final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            logger.errorTrace("static block", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferAction[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("genrateFOCBatchMasterTransferID", "Entered p_batchMasterVO=" + p_batchMasterVO);
        }
        try {
            final long txnId = IDGenerator.getNextID(PretupsI.FOC_BATCH_TRANSACTION_ID, BTSLUtil.getFinancialYear(), p_batchMasterVO.getNetworkCode(), p_batchMasterVO
                .getCreatedOn());
            p_batchMasterVO.setBatchId(calculatorI.formatFOCBatchMasterTxnID(p_batchMasterVO, txnId));
        } catch (Exception e) {
            LOG.error("genrateFOCBatchMasterTransferID", "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("FOCBatchTransferAction", "genrateFOCBatchMasterTransferID", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("genrateFOCBatchMasterTransferID", "Exited  " + p_batchMasterVO.getBatchId());
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("genrateFOCBatchDetailTransferID", "Entered p_batchMasterID=" + p_batchMasterID + ", p_tempNumber= " + p_tempNumber);
        }
        String uniqueID = null;
        try {
            uniqueID = calculatorI.formatFOCBatchDetailsTxnID(p_batchMasterID, p_tempNumber);
        } catch (Exception e) {
            LOG.error("genrateFOCBatchDetailTransferID", "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("FOCBatchTransferAction", "genrateFOCBatchDetailTransferID", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("genrateFOCBatchDetailTransferID", "Exited  " + uniqueID);
            }
        }
        return uniqueID;
    }


    /**
     * Method generateCommaString.
     * This method is used to generate comma seperated string from arraylist
     * 
     * @param p_list
     *            ArrayList
     * @return commaStr String
     */

    private String generateCommaString(ArrayList p_list) throws Exception {
        final String METHOD_NAME = "generateCommaString";
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateCommaString", "Entered p_list=" + p_list);
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
            LOG.error("generateCommaString", "Exceptin:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateCommaString", "Exited commaStr=" + commaStr);
            }
        }
        return commaStr;
    }

    /**
     * Method convertTo2dArray.
     * This method is used to convert linked hash map to 2D array
     * 
     * @param p_fileArr
     *            String[][]
     * @param LinkedHashMap
     *            p_hashMap
     * @param int p_rows
     * @param Date
     *            p_currDate
     * @return p_fileArr String[][]
     */

    public String[][] convertTo2dArray(String[][] p_fileArr, LinkedHashMap p_hashMap, int p_rows, Date p_currDate) throws Exception {
        final String METHOD_NAME = "convertTo2dArray";
        if (LOG.isDebugEnabled()) {
            LOG.debug("convertTo2dArray", "Entered p_fileArr=" + p_fileArr + "p_hashMap=" + p_hashMap + "p_currDate=" + p_currDate);
        }
        
        int rows = 0;
        int cols;
        int finalCols=0;
        try {
            // first row is already generated,and the number of cols are fixed
            // to eight
            final Iterator iterator = p_hashMap.keySet().iterator();
            String key = null;
            ChannelUserVO channelUserVO = null;
            
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                channelUserVO = (ChannelUserVO) p_hashMap.get(key);
                // Only those records are written into the xls file for which
                // status='Y' and insuspend='N'
                if (channelUserVO.getInSuspend().equals(PretupsI.NO) && channelUserVO.getTransferProfileStatus().equals(PretupsI.YES) && PretupsI.YES.equals(channelUserVO
                    .getCommissionProfileStatus())) {
                    if (!channelUserVO.getCommissionProfileApplicableFrom().after(p_currDate)) {
                        rows++;
                        if (rows >= p_rows) {
                            break;
                        }
                        cols = 0;
                        p_fileArr[rows][cols++] = key;
                        p_fileArr[rows][cols++] = channelUserVO.getLoginID();
                        p_fileArr[rows][cols++] = channelUserVO.getCategoryName();
                        p_fileArr[rows][cols++] = channelUserVO.getUserGradeName();
                        p_fileArr[rows][cols++] = "";// extnum
                        p_fileArr[rows][cols++] = ""; // extndate
                        p_fileArr[rows][cols++] = channelUserVO.getExternalCode();
                        p_fileArr[rows][cols++] = "";// quantity
                        p_fileArr[rows][cols++] = ""; // remarks
                        finalCols=cols;
                    }else {
                    	LOG.info("convertTo2dArray","Current date greater than Applicable From for row" + rows );
                    }
                }else {
                	LOG.info("convertTo2dArray","Invalid record at row " + rows  );
                	LOG.info("convertTo2dArray","Invalid record at row " + rows  );
                }
            }
            
            
            
        } catch (Exception e) {
            LOG.error("convertTo2dArray", "Exceptin:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("convertTo2dArray", "Exited p_fileArr=" + p_fileArr);
            }
        }
        if(rows==1) {
   		 rows=rows+1;
   	 	}
        String finalArrayData[][]=  new String[rows][finalCols];
        try {
        	
        System.arraycopy(p_fileArr, 0, finalArrayData,0 , rows);
        }catch(Exception ex) {
        	throw new BTSLBaseException(ex);	
        }
        
        return finalArrayData;
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
     * @author Lohit Audhkhasi
     * @return
     */

    private void genrateDPBatchMasterTransferID(FOCBatchMasterVO p_batchMasterVO) throws BTSLBaseException {
        final String METHOD_NAME = "genrateDPBatchMasterTransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug("genrateDPBatchMasterTransferID", "Entered p_batchMasterVO=" + p_batchMasterVO);
        }
        try {
            final long txnId = IDGenerator.getNextID(PretupsI.DP_BATCH_TRANSACTION_ID, BTSLUtil.getFinancialYear(), PretupsI.ALL, p_batchMasterVO.getCreatedOn());
            p_batchMasterVO.setBatchId(calculatorI.formatDPBatchMasterTxnID(p_batchMasterVO, txnId));
        } catch (Exception e) {
            LOG.error("genrateDPBatchMasterTransferID", "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("FOCBatchTransferAction", "genrateDPBatchMasterTransferID", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("genrateDPBatchMasterTransferID", "Exited  " + p_batchMasterVO.getBatchId());
            }
        }
        return;
    }

    /**
     * Method convertTo2dArrayForDP.
     * This method is used to convert linked hash map to 2D array
     * 
     * @param p_fileArr
     *            String[][]
     * @param LinkedHashMap
     *            p_hashMap
     * @param int p_rows
     * @param Date
     *            p_currDate
     * @return p_fileArr String[][]
     * @author Lohit Audhkhasi
     */

    public String[][] convertTo2dArrayForDP(String[][] p_fileArr, LinkedHashMap p_hashMap, int p_rows, Date p_currDate) throws Exception {
        final String METHOD_NAME = "convertTo2dArrayForDP";
        if (LOG.isDebugEnabled()) {
            LOG.debug("convertTo2dArrayForDP", "Entered p_fileArr=" + p_fileArr + "p_hashMap=" + p_hashMap + "p_currDate=" + p_currDate);
        }
        int rows = 0;
        int cols;
        int finalCols=0;
        try {
            // first row is already generated,and the number of cols are fixed
            // to eight
            final Iterator iterator = p_hashMap.keySet().iterator();
            String key = null;
            ChannelUserVO channelUserVO = null;
            
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                channelUserVO = (ChannelUserVO) p_hashMap.get(key);
                // Only those records are written into the xls file for which
                // status='Y' and insuspend='N'
                if (channelUserVO.getStatus().equals(PretupsI.YES) && channelUserVO.getInSuspend().equals(PretupsI.NO) && channelUserVO.getTransferProfileStatus().equals(
                    PretupsI.YES) && PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                    if (!channelUserVO.getCommissionProfileApplicableFrom().after(p_currDate)) {
                        rows++;
                        if (rows >= p_rows) {
                            break;
                        }
                        cols = 0;
                        p_fileArr[rows][cols++] = key;
                        p_fileArr[rows][cols++] = channelUserVO.getLoginID();
                        p_fileArr[rows][cols++] = channelUserVO.getCategoryName();
                        p_fileArr[rows][cols++] = channelUserVO.getUserGradeName();
                        p_fileArr[rows][cols++] = "";// extnum
                        p_fileArr[rows][cols++] = ""; // extndate
                        p_fileArr[rows][cols++] = channelUserVO.getExternalCode();
                        p_fileArr[rows][cols++] = "";// quantity
                        p_fileArr[rows][cols++] = "";// bonus
                        p_fileArr[rows][cols++] = ""; // remarks
                        finalCols=cols;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("convertTo2dArrayForDP", "Exceptin:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("convertTo2dArrayForDP", "Exited p_fileArr=" + p_fileArr);
            }
        }
        
        if(rows==1) {
      	  rows=rows+1;
      	 }
        
        String finalArrayData[][]=  new String[rows][finalCols];
        try {
        
        System.arraycopy(p_fileArr, 0, finalArrayData,0 , rows);
        }catch(Exception ex) {
        	throw new BTSLBaseException(ex);	
        }
        
        return finalArrayData;
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
     * @author Lohit Audhkhasi
     * @return uniqueID String
     */

    private String genrateDPBatchDetailTransferID(String p_batchMasterID, long p_tempNumber) throws BTSLBaseException {
        final String METHOD_NAME = "genrateDPBatchDetailTransferID";
        if (LOG.isDebugEnabled()) {
            LOG.debug("genrateFOCBatchDetailTransferID", "Entered p_batchMasterID=" + p_batchMasterID + ", p_tempNumber= " + p_tempNumber);
        }
        String uniqueID = null;
        try {
            uniqueID = calculatorI.formatDPBatchDetailsTxnID(p_batchMasterID, p_tempNumber);
        } catch (Exception e) {
            LOG.error("genrateFOCBatchDetailTransferID", "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("FOCBatchTransferAction", "genrateFOCBatchDetailTransferID", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("genrateFOCBatchDetailTransferID", "Exited  " + uniqueID);
            }
        }
        return uniqueID;
    }

}
