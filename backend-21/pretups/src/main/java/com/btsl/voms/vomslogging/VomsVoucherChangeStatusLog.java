/*
 * Created on Jun 27, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.vomslogging;

import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

/**
 * @author vikas.yadav
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class VomsVoucherChangeStatusLog {

    private static Log _log = LogFactory.getLog(VomsVoucherChangeStatusLog.class.getName());

    private VomsVoucherChangeStatusLog() {
		// TODO Auto-generated constructor stub
	}
    public static void log(ArrayList voucherList) {
        VomsVoucherVO voucherVO = null;
        try {
        	
            if (voucherList != null && voucherList.size() > 0) {
            	int voucherListSize = voucherList.size();
                for (int ctr = 0; ctr < voucherListSize; ctr++) {
                    voucherVO = (VomsVoucherVO) voucherList.get(ctr);
                    log(voucherVO);
                }
            }
        }// end of try
        catch (Exception e) {
            _log.error("VoucherChangeStatusLog", " Not able to write in log file =" + e);
        }
    }// end of log

    public static void log(VomsVoucherVO voucherVO) {
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[BatchNo=" + BTSLUtil.NullToString(voucherVO.getEnableBatchNo()) + "] ");
            strBuff.append("[From SNo=" + BTSLUtil.NullToString(voucherVO.getSerialNo()) + "] ");
            strBuff.append("[To SNo=" + BTSLUtil.NullToString(voucherVO.getToSerialNo()) + "] ");
            strBuff.append("[NW=" + BTSLUtil.NullToString(voucherVO.getProductionLocationCode()) + "] ");
            strBuff.append("[Prev Status=" + BTSLUtil.NullToString(voucherVO.getPreviousStatus()) + "] ");
            strBuff.append("[Modified by=" + BTSLUtil.NullToString(voucherVO.getPrevStatusModifiedBy()) + "] ");
            strBuff.append("[Cur status=" + BTSLUtil.NullToString(voucherVO.getVoucherStatus()) + "] ");
            strBuff.append("[Source=" + BTSLUtil.NullToString(voucherVO.getStatusChangeSource()) + "] ");
            strBuff.append("[MRP=" + voucherVO.getMRP() + "] ");
            strBuff.append("[Expiry=" + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.NullToString(voucherVO.getExpiryDateStr())) + "] ");
            strBuff.append("[Modified on=" + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.NullToString(voucherVO.getPrevStatusModifiedOn())) + "] ");
            strBuff.append("[Process=" + BTSLUtil.NullToString(voucherVO.getProcess()) + "] ");
            strBuff.append("[OtherInfo=" + BTSLUtil.NullToString(voucherVO.getLastErrorMessage()) + "] ");
            _log.info("log", strBuff.toString());
        }// end of try
        catch (Exception e) {
            _log.error("VoucherChangeStatusLog ", "Not able to write in log file =" + e);
        }
    }// end of log

    public static void log(String p_transactionid, String p_serialNo, String p_oldStatus, String p_newStatus, String p_networkCode, String p_modifiedBy, String p_modifiedOn) {
        VomsVoucherVO voucherVO = new VomsVoucherVO();
        voucherVO.setEnableBatchNo(p_transactionid);
        voucherVO.setSerialNo(p_serialNo);
        voucherVO.setPreviousStatus(p_oldStatus);
        voucherVO.setPrevStatusModifiedBy(p_modifiedBy);
        voucherVO.setVoucherStatus(p_newStatus);
        voucherVO.setProductionLocationCode(p_networkCode);
        voucherVO.setPrevStatusModifiedOn(p_modifiedOn);
        voucherVO.setLastErrorMessage(p_transactionid);
        log(voucherVO);
    }// end of log

    public static void expiryLog(VomsVoucherVO voucherVO) {
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("[BatchNo=" + BTSLUtil.NullToString(voucherVO.getBatchNo()) + "] ");
            strBuff.append("[Batch Status=" + BTSLUtil.NullToString(voucherVO.getStatus()) + "] ");
            strBuff.append("[Network Code=" + BTSLUtil.NullToString(voucherVO.getUserNetworkCode()) + "] ");
            strBuff.append("[Voucher Type=" + BTSLUtil.NullToString(voucherVO.getVoucherType()) + "] ");
            strBuff.append("[From SNo=" + BTSLUtil.NullToString(voucherVO.get_fromSerialNo()) + "] ");
            strBuff.append("[To SNo=" + BTSLUtil.NullToString(voucherVO.getToSerialNo()) + "] ");
            strBuff.append("[Total vouchers=" + BTSLUtil.NullToString(Long.toString(voucherVO.get_totalVouchers())) + "] ");
            strBuff.append("[Expiry Date=" + BTSLUtil.NullToString(BTSLDateUtil.getLocaleDateTimeFromDate(voucherVO.getExpiryDate())) + "] ");
            strBuff.append("[Change Reason=" + BTSLUtil.NullToString(voucherVO.getExpiryChangeReason()) + "] ");
            
            _log.info("log", strBuff.toString());
        }// end of try
        catch (Exception e) {
            _log.error("VoucherChangeStatusLog ", "Not able to write in log file =" + e);
        }
    }// end of log
}
