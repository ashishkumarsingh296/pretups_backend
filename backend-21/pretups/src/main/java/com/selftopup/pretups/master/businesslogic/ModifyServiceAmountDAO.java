package com.selftopup.pretups.master.businesslogic;

/**
 * @(#)ModifyServiceAmountDAO.java
 *                                 Copyright(c) 2011, Comviva technologies Ltd.
 *                                 All Rights Reserved
 * 
 * 
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Jasmine kaur FEB 3, 2011 Initital Creation
 * 
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;

public class ModifyServiceAmountDAO {
    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadSelectNameList.
     * This method is to Load all the record of the specified service type.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSelectNameList(Connection p_con, String p_serviceCode) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadSelectNameList()", "Entered Map" + p_serviceCode);

        ArrayList serviceAmountList = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SelectorAmountMappingVO selectorAmountMappingVO = null;

        StringBuffer strBuff = new StringBuffer("select sp.service_type ,sp.selector_code ");
        strBuff.append(" ,sp.selector_name,sp.amount,sp.modified_allowed from selector_amount_mapping sp,service_type_selector_mapping sv ");
        strBuff.append(" where sp.selector_code = sv.selector_code and sv.service_type=? and sv.status= ? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadSelectNameList", "QUERY sqlSelect=" + sqlSelect);

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_serviceCode);
            pstmt.setString(2, PretupsI.YES);
            rs = pstmt.executeQuery();
            int index = 0;
            while (rs.next()) {
                selectorAmountMappingVO = new SelectorAmountMappingVO();
                selectorAmountMappingVO.setServiceType(rs.getString("service_type"));
                selectorAmountMappingVO.setSelectorCode(rs.getString("selector_code"));
                selectorAmountMappingVO.setSelectorName(rs.getString("selector_name"));
                selectorAmountMappingVO.setAmount(Double.toString((rs.getDouble("amount") / 100)));
                selectorAmountMappingVO.setModifiedAllowed(rs.getString("modified_allowed"));
                if (selectorAmountMappingVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_YES)) {
                    selectorAmountMappingVO.setDisableAllow(false);
                    selectorAmountMappingVO.setAllowAction("Y");
                } else if (selectorAmountMappingVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_NO)) {
                    selectorAmountMappingVO.setDisableAllow(true);// to be
                                                                  // changed
                    selectorAmountMappingVO.setAllowAction("N");
                }
                selectorAmountMappingVO.setRowID("" + ++index);
                serviceAmountList.add(selectorAmountMappingVO);

            }
        } catch (SQLException sqe) {
            _log.error("loadSelectNameList", " SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyServiceAmountDAO[loadSelectNameList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSelectNameList", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadServicePreferences()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyServiceAmountDAO[loadSelectNameList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSelectNameList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadSelectNameList", "Exited: Map size=");

        }
        return serviceAmountList;
    }

    /**
     * Method updateSelectorAmount.
     * This method is to update the record of the Service Amount and modified
     * allowed status of the specified Sub service.
     * 
     * @param p_con
     *            Connection
     * @param p_selectorList
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSelectorAmount(Connection p_con, ArrayList p_selectorList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateSelectorAmount", "Entered:p_selectorList.size()=" + p_selectorList.size());
        PreparedStatement pstmtUpdate = null;
        SelectorAmountMappingVO selectorAmountMappingVO = null;
        int updateCount = 0;
        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("UPDATE selector_amount_mapping SET modified_allowed=?,amount=?");
            updateQuery.append("WHERE selector_code=?");
            String query = updateQuery.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateSelectorAmount", "Query=" + query);

            pstmtUpdate = p_con.prepareStatement(query);

            for (int i = 0; i < p_selectorList.size(); i++) {
                selectorAmountMappingVO = (SelectorAmountMappingVO) p_selectorList.get(i);
                if (_log.isDebugEnabled())
                    _log.debug("updateSelectorAmount", "Query=" + p_selectorList.get(i));
                pstmtUpdate.setString(1, selectorAmountMappingVO.getModifiedAllowed());
                pstmtUpdate.setDouble(2, Double.parseDouble(selectorAmountMappingVO.getAmount()) * 100);
                pstmtUpdate.setString(3, selectorAmountMappingVO.getSelectorCode());

                updateCount += pstmtUpdate.executeUpdate();
                pstmtUpdate.clearParameters();
            }

        }

        catch (SQLException sqe) {
            _log.error("updateSelectorAmount", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyServiceAmountDAO[updateSelectorAmount]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateSelectorAmount", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateSelectorAmount", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyServiceAmountDAO[updateSelectorAmount]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSelectorAmount", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateSelectorAmount", "Exiting:return=" + updateCount);
        }
        return updateCount;

    }

}