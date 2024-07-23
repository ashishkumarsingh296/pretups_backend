package com.selftopup.pretups.domain.businesslogic;

/*
 * @# CategoryDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 5, 2005 Amit Ruwali Initial creation
 * Aug 22,2005 Manoj Kumar Modified
 * Aug 10,2006 Sandeep Goel Modification ID CAT001
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.common.TypesI;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.gateway.businesslogic.MessageGatewayVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

/**
 * 
 */
public class CategoryDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getFactory().getInstance(CategoryDAO.class.getName());

    /**
     * Constructor for CategoryDAO.
     */
    public CategoryDAO() {
        super();
    }

    /*
     * This method is used to delete(set the status to 'N') the category
     * 
     * @param p_con Connection
     * 
     * @param p_categoryVO CategoryVO
     * 
     * @return updateCount int
     * 
     * @throws BTSLBaseException
     */

    public int deleteStkProfile(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("deleteStkProfile", "Entering VO " + p_categoryVO.toString());

        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;

        try {
            StringBuffer updateQueryBuff = new StringBuffer("UPDATE stk_profiles SET status=?,");
            updateQueryBuff.append(" modified_on=?,modified_by=? WHERE profile_code=?");
            String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, PretupsI.CATEGORY_STATUS_DELETE);
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_categoryVO.getModifiedBy());
            pstmtUpdate.setString(4, p_categoryVO.getCategoryCode().toUpperCase());
            /*
             * boolean modified =
             * this.recordModified(p_con,p_categoryVO.getCategoryCode
             * (),p_categoryVO.getLastModifiedTime(),"deleteStkProfile");
             * if (modified)
             * throw new
             * BTSLBaseException(this,"deleteStkProfile","error.modify.true");
             */
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
            _log.error("deleteStkProfile", " SQLException " + sqle.getMessage());
            _log.errorTrace("deleteStkProfile: Exception print stack trace:", sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[deleteStkProfile]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteStkProfile", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("deleteStkProfile", " Exception " + e.getMessage());
            _log.errorTrace("deleteStkProfile: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[deleteStkProfile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteStkProfile", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteStkProfile", "Exiting updateCount " + updateCount);
        }
        return updateCount;
    }

    public ArrayList loadMessageGatewayTypeListForCategory(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadMessageGatewayTypeListForCategory", "Entered: with p_categoryCode=" + p_categoryCode);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList messageGatewayTypeList = null;
        try {
            StringBuffer selectQuery = new StringBuffer(" SELECT gateway_type  ");
            selectQuery.append(" FROM category_req_gtw_types  ");
            selectQuery.append(" WHERE category_code=? ");
            if (_log.isDebugEnabled())
                _log.debug("loadMessageGatewayTypeListForCategory", "Query=" + selectQuery);

            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_categoryCode);
            rs = pstmtSelect.executeQuery();
            messageGatewayTypeList = new ArrayList();
            while (rs.next()) {
                messageGatewayTypeList.add(rs.getString("gateway_type"));
            }
        } catch (SQLException sqe) {
            _log.error("loadMessageGatewayTypeListForCategory", "SQLException:" + sqe.getMessage());
            _log.errorTrace("loadMessageGatewayTypeListForCategory: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadMessageGatewayTypeListForCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadMessageGatewayTypeListForCategory", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadMessageGatewayTypeListForCategory", "Exception:" + e.getMessage());
            _log.errorTrace("loadMessageGatewayTypeListForCategory: Exception print stack trace:", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadMessageGatewayTypeListForCategory]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadMessageGatewayTypeListForCategory", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadMessageGatewayTypeListForCategory", "Exiting:messageGatewayTypeList size=" + messageGatewayTypeList.size());
        }
        return messageGatewayTypeList;
    }

}
