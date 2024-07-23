package com.btsl.pretups.processes.businesslogic;

/**
 * @(#)MonthlyReport4PosDAO.java
 *                               Copyright(c) 2014, Comviva technologies Ltd.
 *                               All Rights Reserved
 * 
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Diwakar Jan 08 2014 Initial Creation
 *                               This DAO class will be used fetch the data from
 *                               configured database related to generate the
 *                               report on monthly wise.
 * 
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class MonthlyReport4PosDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * @description : This method will be used to list down the POS details for
     *              transaction for O2C & C2C on monthly wise.
     * @author :diwakar
     * @param : p_con - connection with database
     * @param : Date - start date of previous month from current month
     * @param : Date - end date of previous month from current month
     * @param : String - end date of previous month from current month
     * @param : String - type of transaction report
     * @return : ArrayList<SendSMSToChannelAdmin4HourlyTransVO>
     * @throws BTSLBaseException
     */
    public ArrayList<MonthlyReport4PosVO> fetchPosDetailsBasedOnUserProfile(Connection p_con, Date p_fromDate, Date p_toDate, String p_moduleType) throws BTSLBaseException {
        final String METHOD_NAME = "fetchPosDetailsBasedOnUserProfile";
        if (_log.isDebugEnabled())
            _log.debug("fetchPosDetailsBasedOnUserProfile", "Entered ");

        ArrayList<MonthlyReport4PosVO> posDetailsBased = new ArrayList<MonthlyReport4PosVO>(1000);
        PreparedStatement prepSelect = null;
        ResultSet rs = null;
        StringBuffer qrySelect = null;
        try {
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);

            if (PretupsI.O2C_MODULE.equalsIgnoreCase(p_moduleType)) {
                qrySelect = new StringBuffer(" SELECT DCTM.USER_ID ,DCTM.CATEGORY_CODE ,DCTM.GRPH_DOMAIN_CODE,DCTM.SENDER_DOMAIN_CODE,DCTM.EVENT_MONTH,DCTM.ACTIVE_DAYS,DCTM.TRF_AMOUNT,DCTM.TRF_COUNT,DCTM.DAILY_AVG_TRAN_AMOUNT,DCTM.DAILY_AVG_TRAN_COUNT,UPC.PROFILE_ID,UPC.FROM_COUNT,UPC.TO_COUNT,UPC.FROM_AMOUNT,UPC.TO_AMOUNT, UG.GRPH_DOMAIN_CODE AREA_CODE,GD.GRPH_DOMAIN_NAME AREA_NAME,GD2.GRPH_DOMAIN_CODE REGION_CODE, GD2.GRPH_DOMAIN_NAME REGION_NAME , U.USER_NAME, U.MSISDN,U.PARENT_ID ,U.OWNER_ID,UPC.CLASS_TYPE ");
                qrySelect.append("  FROM ( ");
                qrySelect.append("  SELECT DISTINCT DCTM.USER_ID ,DCTM.CATEGORY_CODE ,DCTM.GRPH_DOMAIN_CODE,DCTM.SENDER_DOMAIN_CODE ,TO_CHAR(DCTM.TRANS_DATE,'MON/YYYY') EVENT_MONTH ,  COUNT(DCTM.USER_ID) ACTIVE_DAYS ,SUM(DCTM.O2C_TRANSFER_IN_AMOUNT) TRF_AMOUNT,SUM(DCTM.O2C_TRANSFER_IN_COUNT) TRF_COUNT, ROUND((SUM(DCTM.O2C_TRANSFER_IN_AMOUNT)/COUNT(DCTM.USER_ID)),2) DAILY_AVG_TRAN_AMOUNT ,ROUND((SUM(DCTM.O2C_TRANSFER_IN_COUNT)/COUNT(DCTM.USER_ID)),2) DAILY_AVG_TRAN_COUNT ");
                qrySelect.append("  FROM   DAILY_CHNL_TRANS_MAIN DCTM ");
                qrySelect.append("  WHERE  DCTM.TRANS_DATE >= TO_DATE('" + fromDate + "','YYYY-MM-DD') AND DCTM.TRANS_DATE <= TO_DATE('" + toDate + "','YYYY-MM-DD')  ");
                qrySelect.append("  GROUP  BY DCTM.USER_ID,DCTM.CATEGORY_CODE ,DCTM.GRPH_DOMAIN_CODE ,DCTM.SENDER_DOMAIN_CODE ,TO_CHAR(DCTM.TRANS_DATE,'MON/YYYY') ");
                qrySelect.append("  ) DCTM, USER_PROFILE_CLASSIFICATION UPC, USERS U ,USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD , GEOGRAPHICAL_DOMAINS GD2 ");
                qrySelect.append("  WHERE UPC.TXN_TYPE = 'O2C' ");
                qrySelect.append("  AND DCTM.TRF_COUNT >= UPC.FROM_COUNT  AND DCTM.TRF_COUNT <= UPC.TO_COUNT ");
                qrySelect.append("  AND DCTM.TRF_AMOUNT >= UPC.FROM_AMOUNT AND DCTM.TRF_AMOUNT <= UPC.TO_AMOUNT ");
                qrySelect.append("  AND DCTM.ACTIVE_DAYS >= UPC.FROM_ACTIVE_DAYS AND DCTM.ACTIVE_DAYS <= UPC.TO_ACTIVE_DAYS ");
                qrySelect.append("  AND DCTM.CATEGORY_CODE  = UPC.CATEGORY_CODE ");
                qrySelect.append("  AND UPC.DOMAIN_CODE = DCTM.SENDER_DOMAIN_CODE ");
                qrySelect.append("  AND DCTM.USER_ID = U.USER_ID ");
                qrySelect.append("  AND DCTM.CATEGORY_CODE  = U.CATEGORY_CODE ");
                qrySelect.append("  AND U.USER_ID = UG.USER_ID ");
                qrySelect.append("  AND DCTM.GRPH_DOMAIN_CODE = GD.GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND UPC.GEOLEVEL2 = GD.GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND UPC.GEOLEVEL1 = GD.PARENT_GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND UG.GRPH_DOMAIN_CODE = GD.GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND GD.PARENT_GRPH_DOMAIN_CODE = GD2.GRPH_DOMAIN_CODE ");

            } else if (PretupsI.C2C_MODULE.equalsIgnoreCase(p_moduleType)) {
                qrySelect = new StringBuffer(" SELECT DCTM.USER_ID ,DCTM.CATEGORY_CODE ,DCTM.GRPH_DOMAIN_CODE,DCTM.SENDER_DOMAIN_CODE,DCTM.EVENT_MONTH,DCTM.ACTIVE_DAYS,DCTM.TRF_AMOUNT,DCTM.TRF_COUNT,DCTM.DAILY_AVG_TRAN_AMOUNT,DCTM.DAILY_AVG_TRAN_COUNT,UPC.PROFILE_ID,UPC.FROM_COUNT,UPC.TO_COUNT,UPC.FROM_AMOUNT,UPC.TO_AMOUNT, UG.GRPH_DOMAIN_CODE AREA_CODE,GD.GRPH_DOMAIN_NAME AREA_NAME,GD2.GRPH_DOMAIN_CODE REGION_CODE, GD2.GRPH_DOMAIN_NAME REGION_NAME , U.USER_NAME, U.MSISDN,U.PARENT_ID ,U.OWNER_ID,UPC.CLASS_TYPE ");
                qrySelect.append("  FROM ( ");
                qrySelect.append("  SELECT DISTINCT DCTM.USER_ID ,DCTM.CATEGORY_CODE ,DCTM.GRPH_DOMAIN_CODE,DCTM.SENDER_DOMAIN_CODE ,TO_CHAR(DCTM.TRANS_DATE,'MON/YYYY') EVENT_MONTH ,  COUNT(DCTM.USER_ID) ACTIVE_DAYS ,SUM(DCTM.C2C_TRANSFER_IN_AMOUNT-DCTM.C2C_TRANSFER_OUT_AMOUNT) TRF_AMOUNT,SUM(DCTM.C2C_TRANSFER_IN_COUNT-DCTM.C2C_TRANSFER_OUT_COUNT) TRF_COUNT, ROUND((SUM(DCTM.C2C_TRANSFER_IN_AMOUNT-DCTM.C2C_TRANSFER_OUT_AMOUNT)/COUNT(DCTM.USER_ID)),2) DAILY_AVG_TRAN_AMOUNT ,ROUND((SUM(DCTM.C2C_TRANSFER_IN_COUNT-DCTM.C2C_TRANSFER_OUT_COUNT)/COUNT(DCTM.USER_ID)),2) DAILY_AVG_TRAN_COUNT ");
                qrySelect.append("  FROM   DAILY_CHNL_TRANS_MAIN DCTM ");
                qrySelect.append("  WHERE  DCTM.TRANS_DATE >= TO_DATE('" + fromDate + "','YYYY-MM-DD') AND DCTM.TRANS_DATE <= TO_DATE('" + toDate + "','YYYY-MM-DD')  ");
                qrySelect.append("  GROUP  BY DCTM.USER_ID,DCTM.CATEGORY_CODE ,DCTM.GRPH_DOMAIN_CODE ,DCTM.SENDER_DOMAIN_CODE ,TO_CHAR(DCTM.TRANS_DATE,'MON/YYYY') ");
                qrySelect.append("  ) DCTM, USER_PROFILE_CLASSIFICATION UPC, USERS U ,USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD , GEOGRAPHICAL_DOMAINS GD2 ");
                qrySelect.append("  WHERE UPC.TXN_TYPE = 'C2C' ");
                qrySelect.append("  AND DCTM.TRF_COUNT >= UPC.FROM_COUNT  AND DCTM.TRF_COUNT <= UPC.TO_COUNT ");
                qrySelect.append("  AND DCTM.TRF_AMOUNT  >=  UPC.FROM_AMOUNT AND DCTM.TRF_AMOUNT <= UPC.TO_AMOUNT ");
                qrySelect.append("  AND DCTM.ACTIVE_DAYS >=  UPC.FROM_ACTIVE_DAYS AND DCTM.ACTIVE_DAYS <= UPC.TO_ACTIVE_DAYS ");
                qrySelect.append("  AND DCTM.CATEGORY_CODE  = UPC.CATEGORY_CODE ");
                qrySelect.append("  AND UPC.DOMAIN_CODE = DCTM.SENDER_DOMAIN_CODE ");
                qrySelect.append("  AND DCTM.USER_ID = U.USER_ID ");
                qrySelect.append("  AND DCTM.CATEGORY_CODE  = U.CATEGORY_CODE ");
                qrySelect.append("  AND U.USER_ID = UG.USER_ID ");
                qrySelect.append("  AND DCTM.GRPH_DOMAIN_CODE = GD.GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND UPC.GEOLEVEL2 = GD.GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND UPC.GEOLEVEL1 = GD.PARENT_GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND UG.GRPH_DOMAIN_CODE = GD.GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND GD.PARENT_GRPH_DOMAIN_CODE = GD2.GRPH_DOMAIN_CODE ");

            } else if (PretupsI.C2S_MODULE.equalsIgnoreCase(p_moduleType)) {
                qrySelect = new StringBuffer(" SELECT DCTM.USER_ID ,DCTM.CATEGORY_CODE ,DCTM.GRPH_DOMAIN_CODE,DCTM.SENDER_DOMAIN_CODE,DCTM.EVENT_MONTH,DCTM.ACTIVE_DAYS,DCTM.TRF_AMOUNT,DCTM.TRF_COUNT,DCTM.DAILY_AVG_TRAN_AMOUNT,DCTM.DAILY_AVG_TRAN_COUNT,UPC.PROFILE_ID,UPC.FROM_COUNT,UPC.TO_COUNT,UPC.FROM_AMOUNT,UPC.TO_AMOUNT, UG.GRPH_DOMAIN_CODE AREA_CODE,GD.GRPH_DOMAIN_NAME AREA_NAME,GD2.GRPH_DOMAIN_CODE REGION_CODE, GD2.GRPH_DOMAIN_NAME REGION_NAME , U.USER_NAME, U.MSISDN,U.PARENT_ID ,U.OWNER_ID,UPC.CLASS_TYPE ");
                qrySelect.append("  FROM ( ");
                qrySelect.append("  SELECT DISTINCT DCTM.USER_ID ,DCTM.CATEGORY_CODE ,DCTM.GRPH_DOMAIN_CODE,DCTM.SENDER_DOMAIN_CODE ,TO_CHAR(DCTM.TRANS_DATE,'MON/YYYY') EVENT_MONTH ,  COUNT(DCTM.USER_ID) ACTIVE_DAYS ,SUM(DCTM.C2S_TRANSFER_OUT_AMOUNT) TRF_AMOUNT,SUM(DCTM.C2S_TRANSFER_OUT_COUNT) TRF_COUNT, ROUND((SUM(DCTM.C2S_TRANSFER_OUT_AMOUNT)/COUNT(DCTM.USER_ID)),2) DAILY_AVG_TRAN_AMOUNT ,ROUND((SUM(DCTM.C2S_TRANSFER_OUT_COUNT)/COUNT(DCTM.USER_ID)),2) DAILY_AVG_TRAN_COUNT ");
                qrySelect.append("  FROM   DAILY_CHNL_TRANS_MAIN DCTM ");
                qrySelect.append("  WHERE  DCTM.TRANS_DATE >= TO_DATE('" + fromDate + "','YYYY-MM-DD') AND DCTM.TRANS_DATE <= TO_DATE('" + toDate + "','YYYY-MM-DD')  ");
                qrySelect.append("  GROUP  BY DCTM.USER_ID,DCTM.CATEGORY_CODE ,DCTM.GRPH_DOMAIN_CODE ,DCTM.SENDER_DOMAIN_CODE ,TO_CHAR(DCTM.TRANS_DATE,'MON/YYYY') ");
                qrySelect.append("  ) DCTM, USER_PROFILE_CLASSIFICATION UPC, USERS U ,USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD , GEOGRAPHICAL_DOMAINS GD2 ");
                qrySelect.append("  WHERE UPC.TXN_TYPE = 'C2S' ");
                qrySelect.append("  AND DCTM.TRF_COUNT >=  UPC.FROM_COUNT  AND DCTM.TRF_COUNT <= UPC.TO_COUNT ");
                qrySelect.append("  AND DCTM.TRF_AMOUNT >= UPC.FROM_AMOUNT AND DCTM.TRF_AMOUNT <= UPC.TO_AMOUNT ");
                qrySelect.append("  AND DCTM.ACTIVE_DAYS >= UPC.FROM_ACTIVE_DAYS AND DCTM.ACTIVE_DAYS <= UPC.TO_ACTIVE_DAYS ");
                qrySelect.append("  AND DCTM.CATEGORY_CODE  = UPC.CATEGORY_CODE ");
                qrySelect.append("  AND UPC.DOMAIN_CODE = DCTM.SENDER_DOMAIN_CODE ");
                qrySelect.append("  AND DCTM.USER_ID = U.USER_ID ");
                qrySelect.append("  AND DCTM.CATEGORY_CODE  = U.CATEGORY_CODE ");
                qrySelect.append("  AND U.USER_ID = UG.USER_ID ");
                qrySelect.append("  AND DCTM.GRPH_DOMAIN_CODE = GD.GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND UPC.GEOLEVEL2 = GD.GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND UPC.GEOLEVEL1 = GD.PARENT_GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND UG.GRPH_DOMAIN_CODE = GD.GRPH_DOMAIN_CODE ");
                qrySelect.append("  AND GD.PARENT_GRPH_DOMAIN_CODE = GD2.GRPH_DOMAIN_CODE ");

            }
            if (qrySelect != null && _log.isDebugEnabled()) {
                _log.debug("fetchPosDetailsBasedOnUserProfile", "Select qrySelect:" + qrySelect.toString());
            } else if (_log.isDebugEnabled())
                _log.debug("fetchPosDetailsBasedOnUserProfile", "Select qrySelect=null:");
            if (qrySelect != null) {
                prepSelect = p_con.prepareStatement(qrySelect.toString());
                rs = prepSelect.executeQuery();
            }
            MonthlyReport4PosVO monthlyReport4PosVO = null;

            while (rs != null && rs.next()) {
                monthlyReport4PosVO = new MonthlyReport4PosVO();
                monthlyReport4PosVO.set_eventMonth(rs.getString("EVENT_MONTH"));
                monthlyReport4PosVO.set_resion(rs.getString("REGION_NAME"));
                monthlyReport4PosVO.set_area(rs.getString("AREA_NAME"));
                monthlyReport4PosVO.set_retailerName(rs.getString("USER_NAME"));
                monthlyReport4PosVO.set_posMsisdn(rs.getLong("MSISDN"));
                monthlyReport4PosVO.set_activeDays(rs.getLong("ACTIVE_DAYS"));
                monthlyReport4PosVO.set_amount(rs.getLong("TRF_AMOUNT"));
                monthlyReport4PosVO.set_count(rs.getLong("TRF_COUNT"));
                monthlyReport4PosVO.set_dailyAvgTxnAmount(rs.getDouble("DAILY_AVG_TRAN_AMOUNT"));
                monthlyReport4PosVO.set_dailyAvgTxnCount(rs.getDouble("DAILY_AVG_TRAN_COUNT"));
                monthlyReport4PosVO.set_classType(rs.getString("CLASS_TYPE"));
                posDetailsBased.add(monthlyReport4PosVO);
            }
        } catch (SQLException sql) {
            _log.error("fetchPosDetailsBasedOnUserProfile", "SQLException:=" + sql.getMessage());
            _log.errorTrace(METHOD_NAME, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyReport4PosDAO[fetchPosDetailsBasedOnUserProfile]", "", "", "", "SQLException:" + sql.getMessage());
            throw new BTSLBaseException(this, "fetchPosDetailsBasedOnUserProfile", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("fetchPosDetailsBasedOnUserProfile", "Exception:=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyReport4PosDAO[fetchPosDetailsBasedOnUserProfile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "fetchPosDetailsBasedOnUserProfile", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("fetchPosDetailsBasedOnUserProfile", "Exit failRecharge " + posDetailsBased.size());
            try {
                if (rs != null)
                    rs.close();
                if (prepSelect != null)
                    prepSelect.close();
            } catch (SQLException e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }
        }
        return posDetailsBased;
    }

}