package com.btsl.voms.vomscategory.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;

/**
 * @(#)VomsCategoryDAO.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Amit Singh 19/06/2006 Initial Creation
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          This class is used for categogry management(Add,
 *                          modify, delete) of EVD
 * 
 */

public class VomsCategoryDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method: isMRPAssociatedWithVoucherForMdfy
     * This method is used for checking if the mrp is associated with any
     * Voucher or not.
     * 
     * @author nitin.rohilla
     * @param p_con
     *            java.sql.Connection,p_categoryID String
     * @return boolean
     * @throws BTSLBaseException
     */

    /*
     * public boolean isMRPAssociatedWithVoucherForMdfy(Connection p_con,String
     * p_categoryID) throws BTSLBaseException
     * {
     * if (_log.isDebugEnabled())
     * {
     * _log.debug("isMRPAssociatedWithVoucherForMdfy", "Entered.. ");
     * }
     * boolean mrpAssociation=false;
     * PreparedStatement pstmt = null;
     * ResultSet rs = null;
     * String sqlSelect = new String(
     * " SELECT 1 FROM voms_vouchers WHERE product_id IN(SELECT product_id FROM voms_products WHERE category_id =?)"
     * );
     * 
     * 
     * if (_log.isDebugEnabled())
     * _log.debug("isMRPAssociatedWithVoucherForMdfy", "QUERY sqlSelect=" +
     * sqlSelect);
     * try
     * {
     * pstmt = p_con.prepareStatement(sqlSelect);
     * pstmt.setString(1, p_categoryID);
     * rs = pstmt.executeQuery();
     * if (rs.next())
     * mrpAssociation=true;
     * }
     * catch (SQLException sqe)
     * {
     * _log.error("isMRPAssociatedWithVoucherForMdfy", "SQLException : " + sqe);
     * sqe.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"VomsCategoryDAO[isMRPAssociatedWithVoucherForMdfy]"
     * ,"","","","SQL Exception:"+sqe.getMessage());
     * throw new BTSLBaseException(this, "isMRPAssociatedWithVoucherForMdfy",
     * "error.general.sql.processing");
     * }
     * catch (Exception ex)
     * {
     * _log.error("isMRPAssociatedWithVoucherForMdfy", "Exception : " + ex);
     * ex.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"VomsCategoryDAO[isMRPAssociatedWithVoucherForMdfy]"
     * ,"","","","Exception:"+ex.getMessage());
     * throw new BTSLBaseException(this, "isMRPAssociatedWithVoucherForMdfy",
     * "error.general.processing");
     * }
     * finally
     * {
     * try{if (rs != null){rs.close();}} catch (Exception e){}
     * try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
     * if (_log.isDebugEnabled())
     * _log.debug("isMRPAssociatedWithVoucherForMdfy",
     * "Exiting: mrpAssociation =" + mrpAssociation);
     * }
     * return mrpAssociation;
     * }
     */

    public String[] loadDownloadedSerialno(Connection p_con, String product_ID, String Start_seq, String End_seq, String type, String vtype) throws BTSLBaseException {
        final String methodName = "loadDownloadedSerialno";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. product_ID=" + product_ID);
        }
        String tablename = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] seq_no = new String[2];
        VomsCategoryVO vomsCategoryVO = null;
        try {
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                boolean matchFound = BTSLUtil.validateTableName(vtype);
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + vtype + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }
            StringBuffer strBuff = new StringBuffer(" ");
            strBuff.append("SELECT MIN (SERIAL_NO) AS MIN_SEQ,MAX (SERIAL_NO) AS MAX_SEQ ");
            strBuff.append(" FROM " + tablename + "");
            strBuff.append("  WHERE PRODUCT_ID=? AND SEQ_NO BETWEEN ? AND ?  AND CURRENT_STATUS=?");
            String sqlSelect = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadCategoryList", "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, product_ID);
            pstmt.setString(2, Start_seq);
            pstmt.setString(3, End_seq);
            if ("printing".equals(type)) {
                pstmt.setString(4, VOMSI.VOMS_PRINT_ENABLE_STATUS);
            } else {
                pstmt.setString(4, VOMSI.VOUCHER_NEW);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                seq_no[0] = rs.getString("MIN_SEQ");
                seq_no[1] = rs.getString("MAX_SEQ");
            }
        } catch (SQLException sqe) {

            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: min seq no=" + seq_no[0]);
            }
        }
        return seq_no;
    }
}
