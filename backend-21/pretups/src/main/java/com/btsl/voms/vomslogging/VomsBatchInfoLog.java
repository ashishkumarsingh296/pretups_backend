/*
 * Created on Jun 27, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.vomslogging;

import java.util.ArrayList;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;

/**
 * @author vikas.yadav
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class VomsBatchInfoLog {

    private static Log _log = LogFactory.getLog(VomsBatchInfoLog.class.getName());

    /**
	 * 
	 */
    private VomsBatchInfoLog() {
        
    }

    /**
     * Add inserted batch information into the log.
     * 
     * @param batchList
     * @return void
     * 
     */
    public static void addBatchLog(ArrayList batchList) {
        try {
        	
            if (batchList != null && batchList.size() > 0) {
            	int batchListSize = batchList.size();
                for (int ctr = 0; ctr < batchListSize; ctr++) {
                    VomsBatchVO batchVO = (VomsBatchVO) batchList.get(ctr);
                    if (batchVO.getCreatedDate() != null) {
                        _log.info("addBatchLog", "Batch added ::" + "  " + BTSLUtil.NullToString(batchVO.getBatchNo()) + "  " + BTSLUtil.NullToString(batchVO.getBatchType()) + "  " + BTSLUtil.NullToString(batchVO.getLocationCode()) + "  " + BTSLUtil.NullToString(batchVO.getFromSerialNo()) + "   " + BTSLUtil.NullToString(batchVO.getToSerialNo()) + "   " + BTSLUtil.NullToString(batchVO.getProductID()) + "  " + BTSLUtil.NullToString(batchVO.getReferenceNo()) + "  " + BTSLUtil.NullToString(batchVO.getReferenceType()) + "  " + batchVO.getNoOfVoucher() + "  " + BTSLDateUtil.getLocaleDateTimeFromDate(batchVO.getCreatedDate()) + "  " + BTSLUtil.NullToString(batchVO.getCreatedBy()) + "  " + BTSLUtil.NullToString(batchVO.getStatus()) + "  " + BTSLUtil.NullToString(batchVO.getProcess()));
                    } else {
                        _log.info("addBatchLog", "Batch added ::" + "  " + BTSLUtil.NullToString(batchVO.getBatchNo()) + "  " + BTSLUtil.NullToString(batchVO.getBatchType()) + "  " + BTSLUtil.NullToString(batchVO.getLocationCode()) + "  " + BTSLUtil.NullToString(batchVO.getFromSerialNo()) + "   " + BTSLUtil.NullToString(batchVO.getToSerialNo()) + "   " + BTSLUtil.NullToString(batchVO.getProductID()) + "  " + BTSLUtil.NullToString(batchVO.getReferenceNo()) + "  " + BTSLUtil.NullToString(batchVO.getReferenceType()) + "  " + batchVO.getNoOfVoucher() + "  " + BTSLDateUtil.getLocaleDateTimeFromDate(new Date()) + "  " + BTSLUtil.NullToString(batchVO.getCreatedBy()) + "  " + BTSLUtil.NullToString(batchVO.getStatus()) + "  " + BTSLUtil.NullToString(batchVO.getProcess()));
                    }
                }
            }
        } catch (Exception e) {
            _log.error("addBatchLog", " log() Exception e=" + e);
        }
    }

    /**
     * add modified batch information into log
     * 
     * @param batchList
     * @return void
     */
    public static void modifyBatchLog(ArrayList batchList) {
        try {
        	
            if (batchList != null && batchList.size() > 0) {
            	int batchListSize = batchList.size();
                for (int ctr = 0; ctr < batchListSize; ctr++) {
                    VomsBatchVO batchVO = (VomsBatchVO) batchList.get(ctr);
                    modifyBatchLog(batchVO);
                }
            }
        } catch (Exception e) {

            _log.error("modifyBatchLog ", "log() Exception e=" + e);
        }
    }

    /**
     * for modify Batch Log
     * 
     * @param batchvo
     * @return void
     */
    public static void modifyBatchLog(VomsBatchVO batchVO) {
        try {
            if (batchVO != null) {
                if (batchVO.getModifiedOn() != null) {
                    _log.info("modifyBatchLog", "Batch modified ::" + "  " + BTSLUtil.NullToString(batchVO.getBatchNo()) + "  " + BTSLUtil.NullToString(batchVO.getBatchType()) + "  " + BTSLUtil.NullToString(batchVO.getLocationCode()) + "  " + BTSLUtil.NullToString(batchVO.getFromSerialNo()) + "   " + BTSLUtil.NullToString(batchVO.getToSerialNo()) + "   " + BTSLUtil.NullToString(batchVO.getProductID()) + "  " + BTSLUtil.NullToString(batchVO.getReferenceNo()) + "  " + BTSLUtil.NullToString(batchVO.getReferenceType()) + "  " + batchVO.getNoOfVoucher() + "  " + batchVO.getSuccessCount() + "  " + batchVO.getFailCount() + "  " + BTSLDateUtil.getLocaleDateTimeFromDate(batchVO.getModifiedOn()) + "  " + BTSLUtil.NullToString(batchVO.getModifiedBy()) + "  " + BTSLUtil.NullToString(batchVO.getStatus()) + "  " + BTSLUtil.NullToString(batchVO.getMessage()) + "  " + BTSLUtil.NullToString(batchVO.getProcess()));
                } else {
                    _log.info("modifyBatchLog", "Batch modified ::" + "  " + BTSLUtil.NullToString(batchVO.getBatchNo()) + "  " + BTSLUtil.NullToString(batchVO.getBatchType()) + "  " + BTSLUtil.NullToString(batchVO.getLocationCode()) + "  " + BTSLUtil.NullToString(batchVO.getFromSerialNo()) + "   " + BTSLUtil.NullToString(batchVO.getToSerialNo()) + "   " + BTSLUtil.NullToString(batchVO.getProductID()) + "  " + BTSLUtil.NullToString(batchVO.getReferenceNo()) + "  " + BTSLUtil.NullToString(batchVO.getReferenceType()) + "  " + batchVO.getNoOfVoucher() + "  " + batchVO.getSuccessCount() + "  " + batchVO.getFailCount() + "  " + BTSLDateUtil.getLocaleDateTimeFromDate(new Date()) + "  " + BTSLUtil.NullToString(batchVO.getModifiedBy()) + "  " + BTSLUtil.NullToString(batchVO.getStatus()) + "  " + BTSLUtil.NullToString(batchVO.getMessage()) + "  " + BTSLUtil.NullToString(batchVO.getProcess()));
                }
            }
        } catch (Exception e) {
            _log.error("modifyBatchLog", " log() Exception1 e=" + e);
        }
    }

    /**
     * for download Batch Log
     * 
     * @param p_batchVO
     * @return void
     */
    public static void downloadBatchLog(VomsBatchVO p_batchVO) {
        try {
            if (p_batchVO.getDownloadDate() != null) {
                _log.info("downloadBatchLog", "Batch downloaded :: " + BTSLUtil.NullToString(p_batchVO.getBatchNo()) + "  " + BTSLUtil.NullToString(p_batchVO.getBatchType()) + "  " + BTSLUtil.NullToString(p_batchVO.getProductID()) + "  " + p_batchVO.getNoOfVoucher() + "  " + BTSLUtil.NullToString(p_batchVO.getFromSerialNo()) + "  " + "  " + BTSLUtil.NullToString(p_batchVO.getToSerialNo()) + "  " + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_batchVO.getDownloadDate())) + "  " + BTSLUtil.NullToString(p_batchVO.getCreatedBy()));
            } else {
                _log.info("downloadBatchLog", "Batch downloaded :: " + BTSLUtil.NullToString(p_batchVO.getBatchNo()) + "  " + BTSLUtil.NullToString(p_batchVO.getBatchType()) + "  " + BTSLUtil.NullToString(p_batchVO.getProductID()) + "  " + p_batchVO.getNoOfVoucher() + "  " + BTSLUtil.NullToString(p_batchVO.getFromSerialNo()) + "  " + "  " + BTSLUtil.NullToString(p_batchVO.getToSerialNo()) + "  " + BTSLDateUtil.getSystemLocaleCurrentDate() + "  " + BTSLUtil.NullToString(p_batchVO.getCreatedBy()));
            }
        } catch (Exception e) {
            _log.error("downloadBatchLog()", " Exception2 e=" + e);
        }
    }

    public static void genVoucherBatchLog(VomsBatchVO p_batchVO) {
        try {
            if (p_batchVO.getDownloadDate() != null) {
                _log.info("genVoucherBatchLog", "Batch generated :: " + BTSLUtil.NullToString(p_batchVO.getBatchNo()) + "  " + BTSLUtil.NullToString(p_batchVO.getBatchType()) + "  " + BTSLUtil.NullToString(p_batchVO.getProductID()) + "  " + p_batchVO.getNoOfVoucher() + "  " + BTSLUtil.NullToString(p_batchVO.getFromSerialNo()) + "  " + "  " + BTSLUtil.NullToString(p_batchVO.getToSerialNo()) + "  " + BTSLDateUtil.getLocaleDateTimeFromDate(p_batchVO.getDownloadDate()) + "  " + BTSLUtil.NullToString(p_batchVO.getCreatedBy()) + " " + BTSLUtil.NullToString(p_batchVO.getLocationCode() + " " + BTSLUtil.NullToString(p_batchVO.getStatus())));
            } else {
                _log.info("genVoucherBatchLog", "Batch generated :: " + BTSLUtil.NullToString(p_batchVO.getBatchNo()) + "  " + BTSLUtil.NullToString(p_batchVO.getBatchType()) + "  " + BTSLUtil.NullToString(p_batchVO.getProductID()) + "  " + p_batchVO.getNoOfVoucher() + "  " + BTSLUtil.NullToString(p_batchVO.getFromSerialNo()) + "  " + "  " + BTSLUtil.NullToString(p_batchVO.getToSerialNo()) + "  " + BTSLDateUtil.getLocaleDateTimeFromDate(new Date()) + "  " + BTSLUtil.NullToString(p_batchVO.getCreatedBy() + " " + BTSLUtil.NullToString(p_batchVO.getLocationCode() + " " + BTSLUtil.NullToString(p_batchVO.getStatus()))));
            }
        } catch (Exception e) {
            _log.error("genVoucherBatchLog()", " Exception2 e=" + e);
        }
    }

    /**
     * For normal log
     * 
     * @param p_string
     */
    public static void log(String p_string) {
        try {
            _log.info("log", "Info : Log" + p_string);
        } catch (Exception e) {
            _log.error("log()", " Exception2 e=" + e);
        }
    }
}
