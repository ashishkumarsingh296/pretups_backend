/*
 * #BarredUserDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jun 23, 2005 amit.ruwali Initial creation
 * Feb 22, 2008 santanu Mohanty Modified for pin unblock automatically
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.selftopup.pretups.subscriber.businesslogic;

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
import com.selftopup.pretups.master.businesslogic.LookupsCache;
import com.selftopup.pretups.master.businesslogic.LookupsVO;
import com.selftopup.util.BTSLUtil;

/**
 * 
 */
public class BarredUserDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getFactory().getInstance(BarredUserDAO.class.getName());

    /**
     * Method addBarredUser.
     * This method is used to add the Details of user in the barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_barredUserVO
     *            BarredUserVO
     * @return int
     * @throws BTSLBaseException
     */

    public int addBarredUser(Connection p_con, BarredUserVO p_barredUserVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addBarredUser", "Entering p_barredUserVO " + p_barredUserVO);

        int addCount = -1;
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO barred_msisdns ");
        insertQueryBuff.append("(module,network_code,msisdn,name,barred_reason,");
        insertQueryBuff.append("user_type,barred_type,created_on,created_by,");
        insertQueryBuff.append("modified_on,modified_by,created_date)");
        insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("addBarredUser()", "Insert Query= " + insertQuery);
        try {
            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(1, p_barredUserVO.getModule());
            pstmtInsert.setString(2, p_barredUserVO.getNetworkCode());
            pstmtInsert.setString(3, p_barredUserVO.getMsisdn());
            // commented for DB2
            // pstmtInsert.setFormOfUse(4,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(4, p_barredUserVO.getName());
            // commented for DB2
            // pstmtInsert.setFormOfUse(5,OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(5, p_barredUserVO.getBarredReason());
            pstmtInsert.setString(6, p_barredUserVO.getUserType());
            pstmtInsert.setString(7, p_barredUserVO.getBarredType());
            pstmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_barredUserVO.getCreatedOn()));
            pstmtInsert.setString(9, p_barredUserVO.getCreatedBy());
            pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_barredUserVO.getModifiedOn()));
            pstmtInsert.setString(11, p_barredUserVO.getModifiedBy());
            pstmtInsert.setDate(12, BTSLUtil.getSQLDateFromUtilDate(p_barredUserVO.getCreatedOn()));
            addCount = pstmtInsert.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug("addBarredUser", "Query Executed= " + insertQuery);
        }

        catch (SQLException sqle) {
            _log.error("addBarredUser", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[addBarredUser]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addBarredUser", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("addBarredUser", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[addBarredUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addBarredUser", "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addBarredUser", " Exiting  addCount" + addCount);
        }

        return addCount;
    }

    /**
     * Method insertIntoHistory.
     * This method is used to copy the record from barred_msisdn table from
     * barred_msisdn_history table if the networkcode,module type and msisdn
     * matched.
     * 
     * @param p_con
     *            Connection
     * @param p_barredUserVOList
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     */

    /*
     * public int insertIntoHistory(Connection p_con,ArrayList
     * p_barredUserVOList)
     * throws BTSLBaseException
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("insertIntoHistory","Entering p_barredUserVOList.size()=" +
     * p_barredUserVOList.size());
     * 
     * int addCount=-1;
     * int deleteCount=-1;
     * PreparedStatement pstmtInsert = null;
     * StringBuffer insertQueryBuff = new
     * StringBuffer("INSERT INTO barred_msisdn_history");
     * insertQueryBuff.append("(module,network_code,msisdn,name,barred_reason,");
     * insertQueryBuff.append("user_type,barred_type,created_on,created_by,");
     * insertQueryBuff.append("modified_on,modified_by,created_date)(");
     * insertQueryBuff.append(
     * "SELECT module,network_code,msisdn,name,barred_reason,");
     * insertQueryBuff.append("user_type,barred_type,created_on,created_by,");
     * insertQueryBuff.append(
     * "modified_on,modified_by,created_date FROM barred_msisdns");
     * insertQueryBuff.append(
     * " WHERE UPPER(module)=UPPER(?) AND UPPER(network_code)=UPPER(?)");
     * insertQueryBuff.append(
     * " AND msisdn=? AND UPPER(user_type)=UPPER(?) AND UPPER(barred_type)=UPPER(?))"
     * );
     * String insertQuery = insertQueryBuff.toString();
     * if (_log.isDebugEnabled())
     * _log.debug("insertIntoHistory","Insert Query= "+ insertQuery);
     * try
     * {
     * BarredUserVO barredUserVO=null;
     * pstmtInsert = p_con.prepareStatement(insertQuery);
     * for(int i=0,j=p_barredUserVOList.size();i<j;i++)
     * {
     * barredUserVO=(BarredUserVO)p_barredUserVOList.get(i);
     * if(!barredUserVO.getMultiBox().equals(PretupsI.SELECT_CHECKBOX))
     * continue;
     * if (_log.isDebugEnabled())
     * _log.debug("insertIntoHistory","barredUserVO = "+ barredUserVO);
     * 
     * pstmtInsert.setString(1,barredUserVO.getModule());
     * pstmtInsert.setString(2,barredUserVO.getNetworkCode());
     * pstmtInsert.setString(3,barredUserVO.getMsisdn());
     * pstmtInsert.setString(4,barredUserVO.getUserType());
     * pstmtInsert.setString(5,barredUserVO.getBarredType());
     * addCount = pstmtInsert.executeUpdate();
     * if(addCount<=0)
     * return addCount;
     * //If successfully inserted in history table delete the corresponding
     * //record from the barred_msisdn table.
     * 
     * deleteCount=this.deleteFromBarredMsisdn(p_con,barredUserVO);
     * if(deleteCount<=0)
     * return deleteCount;
     * pstmtInsert.clearParameters();
     * }
     * }
     * catch (SQLException sqle)
     * {
     * _log.error("insertIntoHistory","SQLException " + sqle.getMessage());
     * sqle.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .RAISED,EventLevelI.FATAL,"BarredUserDAO[insertIntoHistory]","","","",
     * "SQL Exception:"+sqle.getMessage());
     * throw new
     * BTSLBaseException(this,"insertIntoHistory","error.general.sql.processing"
     * );
     * }
     * catch (Exception e)
     * {
     * _log.error("addBarredUser"," Exception " + e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .RAISED,EventLevelI.FATAL,"BarredUserDAO[insertIntoHistory]","","","",
     * "Exception:"+e.getMessage());
     * throw new
     * BTSLBaseException(this,"insertIntoHistory","error.general.processing");
     * }
     * finally
     * {
     * try {if (pstmtInsert != null) pstmtInsert.close();}catch (Exception e){}
     * if (_log.isDebugEnabled())
     * _log.debug("insertIntoHistory"," Exiting addCount "+ addCount);
     * }
     * return deleteCount;
     * }
     */
    /**
     * Method deleteFromBarredMsisdn.
     * This method is used to delete the record from barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_barredUserVO
     *            BarredUserVO
     * @return int
     * @throws BTSLBaseException
     */

    public int deleteFromBarredMsisdn(Connection p_con, ArrayList p_barredUserVOList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteFromBarredMsisdn", "Entering p_barredUserVOList.size() " + p_barredUserVOList.size());

        int deleteCount = -1;
        PreparedStatement pstmtDelete = null;
        StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM barred_msisdns WHERE");
        deleteQueryBuff.append(" UPPER(module)=UPPER(?) AND UPPER(network_code)=UPPER(?)");
        deleteQueryBuff.append(" AND msisdn=? AND UPPER(user_type)=UPPER(?)AND UPPER(barred_type)=UPPER(?)");
        String deleteQuery = deleteQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("deleteFromBarredMsisdn", "Delete Query= " + deleteQuery);
        try {
            BarredUserVO barredUserVO = null;
            pstmtDelete = p_con.prepareStatement(deleteQuery);
            for (int i = 0, j = p_barredUserVOList.size(); i < j; i++) {
                barredUserVO = (BarredUserVO) p_barredUserVOList.get(i);
                if (!barredUserVO.getMultiBox().equals(PretupsI.SELECT_CHECKBOX))
                    continue;
                if (_log.isDebugEnabled())
                    _log.debug("insertIntoHistory", "barredUserVO = " + barredUserVO);

                pstmtDelete.setString(1, barredUserVO.getModule());
                pstmtDelete.setString(2, barredUserVO.getNetworkCode());
                pstmtDelete.setString(3, barredUserVO.getMsisdn());
                pstmtDelete.setString(4, barredUserVO.getUserType());
                pstmtDelete.setString(5, barredUserVO.getBarredType());
                deleteCount = pstmtDelete.executeUpdate();
                if (deleteCount <= 0)
                    return deleteCount;
                pstmtDelete.clearParameters();
            }
        } catch (SQLException sqle) {
            _log.error("deleteFromBarredMsisdn", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteFromBarredMsisdn]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteFromBarredMsisdn", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("deleteFromBarredMsisdn", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteFromBarredMsisdn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteFromBarredMsisdn", "error.general.processing");
        } finally {
            try {
                if (pstmtDelete != null)
                    pstmtDelete.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteFromBarredMsisdn", " Exiting deleteCount " + deleteCount);
        }
        return deleteCount;
    }

    /**
     * Method loadInfoOfBarredUser
     * This method used to load the details of barred user from
     * barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_barredVO
     *            BarredUserVO
     * @return boolean
     * @throws BTSLBaseException
     */

    public ArrayList loadInfoOfBarredUser(Connection p_con, BarredUserVO p_barredVO) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadInfoOfBarredUser", "p_barredVO " + p_barredVO);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("SELECT distinct BM.name,BM.barred_type,BM.barred_reason,");
        sqlBuff.append("BM.created_on,BM.created_by,BM.modified_on,BM.modified_by,BM.created_date,");
        sqlBuff.append("SL.sub_lookup_name,L.lookup_name ");
        sqlBuff.append("FROM barred_msisdns BM,sub_lookups SL , lookups L ");
        sqlBuff.append("WHERE UPPER(module)=UPPER(?) AND network_code=? AND msisdn=? ");
        sqlBuff.append("AND UPPER(user_type)=UPPER(?) AND SL.sub_lookup_code=BM.barred_type AND SL.lookup_type=? ");
        sqlBuff.append("AND L.lookup_type=? AND L.lookup_code=BM.user_type ");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadInfoOfBarredUser", "Select Query::" + selectQuery);
        ArrayList barredUserList = new ArrayList();
        BarredUserVO barredUserVO = null;
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_barredVO.getModule());
            pstmtSelect.setString(2, p_barredVO.getNetworkCode());
            pstmtSelect.setString(3, p_barredVO.getMsisdn());
            pstmtSelect.setString(4, p_barredVO.getUserType());
            pstmtSelect.setString(5, PretupsI.BARRING_TYPE);
            pstmtSelect.setString(6, PretupsI.BARRED_USER_TYPE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                barredUserVO = new BarredUserVO();
                barredUserVO.setModule(p_barredVO.getModule());
                barredUserVO.setNetworkCode(p_barredVO.getNetworkCode());
                barredUserVO.setMsisdn(p_barredVO.getMsisdn());
                barredUserVO.setUserType(p_barredVO.getUserType());

                barredUserVO.setName(rs.getString("name"));
                barredUserVO.setBarredType(rs.getString("barred_type"));
                barredUserVO.setBarredReason(rs.getString("barred_reason"));
                barredUserVO.setCreatedBy(rs.getString("created_by"));
                barredUserVO.setCreatedOn(rs.getTimestamp("created_on"));
                barredUserVO.setModifiedBy(rs.getString("modified_by"));
                barredUserVO.setModifiedOn(rs.getTimestamp("modified_on"));
                barredUserVO.setBarredTypeName(rs.getString("sub_lookup_name"));
                barredUserVO.setBarredDate(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                barredUserVO.setUserTypeDesc(rs.getString("lookup_name"));
                barredUserList.add(barredUserVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadInfoOfBarredUser", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadInfoOfBarredUser]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadInfoOfBarredUser", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadInfoOfBarredUser", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadInfoOfBarredUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadInfoOfBarredUser", "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInfoOfBarredUser", "Exiting: barredUserList.size()" + barredUserList.size());
        }

        return barredUserList;
    }

    /**
     * Method loadBarredUserList
     * This method used to load the information of barred user
     * 
     * @param p_con
     *            Connection
     * @param p_barredVO
     *            BarredUserVO
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadBarredUserList(Connection p_con, BarredUserVO p_barredVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadBarredUserList", "Entered p_barredVO " + p_barredVO);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList barredList = new ArrayList();
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT distinct bm.module,n.network_name,bm.msisdn,name,bm.user_type,bm.barred_type,bm.created_on,U.user_name created_by, ");
        strBuff.append("bm.barred_reason,SL.sub_lookup_name FROM barred_msisdns bm,networks n ,sub_lookups SL,users U ");
        strBuff.append("WHERE bm.network_code=n.network_code AND SL.sub_lookup_code=BM.barred_type AND SL.lookup_type=? AND bm.created_by=U.user_id ");
        if (!BTSLUtil.isNullString(p_barredVO.getMsisdn()))
            strBuff.append(" AND bm.msisdn=? ");
        else {
            strBuff.append(" AND bm.created_date >= ? AND bm.created_date <= ? ");
            strBuff.append(" AND bm.module=? ");
            strBuff.append(" AND bm.user_type = DECODE(?,?,bm.user_type,?) ");
            strBuff.append(" AND bm.barred_type = DECODE(?,?,bm.barred_type,?) AND bm.network_code= ? ");
        }
        strBuff.append(" ORDER BY bm.created_on DESC");
        try {
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.BARRING_TYPE);

            if (!BTSLUtil.isNullString(p_barredVO.getMsisdn()))
                pstmtSelect.setString(i++, p_barredVO.getMsisdn());
            else {
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_barredVO.getFromDate())));
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_barredVO.getToDate())));
                pstmtSelect.setString(i++, p_barredVO.getModule());
                pstmtSelect.setString(i++, p_barredVO.getUserType());
                pstmtSelect.setString(i++, PretupsI.ALL);
                pstmtSelect.setString(i++, p_barredVO.getUserType());
                pstmtSelect.setString(i++, p_barredVO.getBarredType());
                pstmtSelect.setString(i++, PretupsI.ALL);
                pstmtSelect.setString(i++, p_barredVO.getBarredType());
                pstmtSelect.setString(i++, p_barredVO.getNetworkCode());
            }

            String selectQuery = strBuff.toString();
            BarredUserVO barredUserVO = null;
            LookupsVO lookupVO = null;
            if (_log.isDebugEnabled())
                _log.debug("loadBarredUserList", "Select Query " + selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                barredUserVO = new BarredUserVO();
                lookupVO = (LookupsVO) LookupsCache.getObject(PretupsI.MODULE_TYPE, rs.getString("module"));
                barredUserVO.setModule(lookupVO.getLookupName());
                barredUserVO.setNetworkName(rs.getString("network_name"));
                barredUserVO.setMsisdn(rs.getString("msisdn"));
                barredUserVO.setName(rs.getString("name"));
                barredUserVO.setUserType(rs.getString("user_type"));
                barredUserVO.setBarredType(rs.getString("barred_type"));
                barredUserVO.setCreatedOn(rs.getDate("created_on"));
                barredUserVO.setCreatedBy(rs.getString("created_by"));
                barredUserVO.setBarredDate(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                barredUserVO.setBarredReason(rs.getString("barred_reason"));
                barredUserVO.setBarredTypeName(rs.getString("sub_lookup_name"));
                barredList.add(barredUserVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadBarredUserList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBarredUserList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadBarredUserList", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadBarredUserList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadBarredUserList", "Exiting size= " + barredList.size());
        }
        return barredList;
    }

    /**
     * This method is checks the user information before adding the record
     * in the barred_msisdn table and returns true if the record is found
     * 
     * @param p_con
     *            Connection
     * @param p_module
     *            String
     * @param p_networkCode
     *            String
     * @param p_msisdn
     *            String
     * @param p_userType
     *            String
     * @param p_barredType
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExists(Connection p_con, String p_module, String p_networkCode, String p_msisdn, String p_userType, String p_barredType) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("isExists", "Entered params Module:: p_module=" + p_module + ",p_networkCode=" + p_networkCode + ",p_msisdn=" + p_msisdn + ",p_userType=" + p_userType + ",p_barredType=" + p_barredType);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM barred_msisdns WHERE ");
        sqlBuff.append("module=? AND network_code=? ");
        sqlBuff.append("AND msisdn=? AND (user_type=? )");// OR user_type='BOTH'
        if (!BTSLUtil.isNullString(p_barredType))
            sqlBuff.append("AND barred_type=? ");
        sqlBuff.append("AND rownum =1 ");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isExists", "Select Query::" + selectQuery);

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, p_module);
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_userType);
            if (!BTSLUtil.isNullString(p_barredType))
                pstmtSelect.setString(i++, p_barredType);
            rs = pstmtSelect.executeQuery();
            if (rs.next())
                found = true;
        } catch (SQLException sqle) {
            _log.error("isExists", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[isExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("isExists", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isExists", "Exiting: isExists found=" + found);
        }
        return found;
    }

    /**
     * Method addBarredUserBulk.
     * This method is used to add the Details of user in the barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_msisdnList
     *            ArrayList
     * @param p_sessionUserVO
     *            TODO
     * @return int
     * @throws BTSLBaseException
     */
    /*
     * public String addBarredUserBulk(Connection p_con,ArrayList p_msisdnList,
     * UserVO p_sessionUserVO) throws
     * BTSLBaseException
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("addBarredUserBulk","Entering p_msisdnList.size(): " +
     * p_msisdnList.size()+" p_sessionUserVO :"+p_sessionUserVO.toString());
     * ResultSet rsselectBarred=null;
     * ResultSet rsselectUser=null;
     * ResultSet rsselectGeography=null;
     * int addCount=-1;
     * StringBuffer unProcessedMsisdn=new StringBuffer ();
     * //commented for DB2 OraclePreparedStatement pstmtInsert = null;
     * PreparedStatement pstmtInsert = null;
     * PreparedStatement pstmtSelectBarred=null;
     * PreparedStatement pstmtSelectUsers=null;
     * PreparedStatement pstmtSelectGeography=null;
     * BarredUserVO barredUserVO=null;
     * boolean isChDomainCheckRequired=false;
     * boolean isGeoDomainCheckRequired=false;
     * ListValueVO vo = null;
     * ArrayList domainList = null;
     * ArrayList domainListStr = new ArrayList();
     * 
     * StringBuffer selectBuff=new StringBuffer() ;
     * 
     * StringBuffer insertQueryBuff = new
     * StringBuffer("INSERT INTO barred_msisdns ");
     * insertQueryBuff.append("(module,network_code,msisdn,name,barred_reason,");
     * insertQueryBuff.append("user_type,barred_type,created_on,created_by,");
     * insertQueryBuff.append("modified_on,modified_by,created_date)");
     * insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
     * String insertQuery = insertQueryBuff.toString();
     * 
     * selectBuff=new StringBuffer("SELECT 1 FROM barred_msisdns WHERE ");
     * selectBuff.append(" module=? AND network_code=?");
     * selectBuff.append("  AND msisdn=? AND user_type=?");
     * selectBuff.append(" AND barred_type=NVL(?,barred_type)");
     * String selectQuery=selectBuff.toString();
     * 
     * StringBuffer selectQueryBuffUser = new
     * StringBuffer(" SELECT u.user_id, cat.domain_code ");
     * selectQueryBuffUser.append(
     * " FROM users u,categories cat,user_phones uphones ");
     * selectQueryBuffUser.append(
     * " WHERE uphones.msisdn=? AND uphones.user_id=u.user_id AND u.status <> 'N' AND u.status <>'C' "
     * );
     * selectQueryBuffUser.append(" AND u.category_code = cat.category_code ");
     * String selectQueryUsers = selectQueryBuffUser.toString();
     * 
     * StringBuffer sqlRecordExistGeography = new StringBuffer();
     * sqlRecordExistGeography.append(
     * "SELECT 1 FROM geographical_domains WHERE grph_domain_code  IN ");
     * sqlRecordExistGeography.append(
     * "(SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' "
     * );
     * sqlRecordExistGeography.append(
     * "CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code   ");
     * sqlRecordExistGeography.append("START WITH grph_domain_code IN ");
     * sqlRecordExistGeography.append(
     * "(SELECT grph_domain_code FROM user_geographies WHERE user_id=?)) ");
     * sqlRecordExistGeography.append(
     * "AND grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES where user_id=?) "
     * );
     * String selectGeography = sqlRecordExistGeography.toString();
     * if (_log.isDebugEnabled())
     * _log.debug("addBarredUserBulk()","selectGeography Query= "+
     * selectGeography);
     * if (_log.isDebugEnabled())
     * _log.debug("addBarredUserBulk()","selectQueryUsers Query= "+
     * selectQueryUsers);
     * if (_log.isDebugEnabled())
     * _log.debug("addBarredUserBulk()","selectQuery Query= "+ selectQuery);
     * if (_log.isDebugEnabled())
     * _log.debug("addBarredUserBulk()","Insert Query= "+ insertQuery);
     * try
     * {
     * if(PretupsI.BCU_USER.equals(p_sessionUserVO.getCategoryVO().getCategoryCode
     * ()))
     * isGeoDomainCheckRequired=true;
     * if(PretupsI.DOMAINS_ASSIGNED.equals(p_sessionUserVO.getCategoryVO().
     * getFixedDomains()))
     * {
     * domainList = BTSLUtil.displayDomainList(p_sessionUserVO.getDomainList());
     * isChDomainCheckRequired=true;
     * for(int i=0,j=domainList.size();i<j;i++)
     * {
     * vo=(ListValueVO)domainList.get(i);
     * domainListStr.add(vo.getValue());
     * }
     * }
     * //commented for DB2 pstmtInsert =
     * (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
     * pstmtInsert = (PreparedStatement)p_con.prepareStatement(insertQuery);
     * pstmtSelectBarred=p_con.prepareStatement(selectQuery);
     * pstmtSelectUsers=p_con.prepareStatement(selectQueryUsers);
     * pstmtSelectGeography=p_con.prepareStatement(selectGeography);
     * int i;
     * String userID=null;
     * String domainID=null;
     * if(p_msisdnList.size()!=0)
     * {
     * for (int index=0, j=p_msisdnList.size();index<j;index++)
     * {
     * i=1;
     * barredUserVO =(BarredUserVO)p_msisdnList.get(index);
     * pstmtSelectBarred.setString(i++,barredUserVO.getModule());
     * pstmtSelectBarred.setString(i++,barredUserVO.getNetworkCode());
     * pstmtSelectBarred.setString(i++,barredUserVO.getMsisdn());
     * pstmtSelectBarred.setString(i++,barredUserVO.getUserType());
     * if(!BTSLUtil.isNullString(barredUserVO.getBarredType()))
     * pstmtSelectBarred.setString(i++,barredUserVO.getBarredType());
     * rsselectBarred=pstmtSelectBarred.executeQuery();
     * pstmtSelectBarred.clearParameters() ;
     * if(rsselectBarred.next())
     * {
     * BarFileProccesingLog.log("BULKBARRED",barredUserVO.getCreatedBy(),
     * barredUserVO.getMsisdn()
     * ,(index+1),"Fail","This mobile number is already bar",null);
     * unProcessedMsisdn.append(barredUserVO.getMsisdn()+",");
     * continue;
     * }
     * else if(PretupsI.C2S_MODULE.equals(barredUserVO.getModule()) &&
     * PretupsI.USER_TYPE_SENDER.equals(barredUserVO.getUserType()))
     * {
     * //if(isChDomainCheckRequired || isGeoDomainCheckRequired)
     * {
     * pstmtSelectUsers.setString(1, barredUserVO.getMsisdn());
     * rsselectUser=pstmtSelectUsers.executeQuery();
     * pstmtSelectUsers.clearParameters() ;
     * if(rsselectUser.next())
     * {
     * userID=rsselectUser.getString("user_id");
     * domainID=rsselectUser.getString("domain_code");
     * if(isChDomainCheckRequired)
     * {
     * if(!domainListStr.contains(domainID))//Channel user does not in domain
     * {
     * BarFileProccesingLog.log("BULKBARRED",barredUserVO.getCreatedBy(),
     * barredUserVO.getMsisdn()
     * ,(index+1),"Fail","Channel user does not in domain",null);
     * unProcessedMsisdn.append(barredUserVO.getMsisdn()+",");
     * continue;
     * }
     * }
     * if(isGeoDomainCheckRequired)
     * {
     * pstmtSelectGeography.setString(1, p_sessionUserVO.getUserID());
     * pstmtSelectGeography.setString(2, userID);
     * rsselectGeography=pstmtSelectGeography.executeQuery();
     * pstmtSelectGeography.clearParameters() ;
     * if(!rsselectGeography.next())//Channel user not in Geography Herirarchy
     * {
     * BarFileProccesingLog.log("BULKBARRED",barredUserVO.getCreatedBy(),
     * barredUserVO.getMsisdn()
     * ,(index+1),"Fail","Channel user not in Geography Herirarchy",null);
     * unProcessedMsisdn.append(barredUserVO.getMsisdn()+",");
     * continue;
     * }
     * }
     * }
     * else// channel user not exist
     * {
     * BarFileProccesingLog.log("BULKBARRED",barredUserVO.getCreatedBy(),
     * barredUserVO.getMsisdn()
     * ,(index+1),"Fail","Channel user does not Exist",null);
     * unProcessedMsisdn.append(barredUserVO.getMsisdn()+",");
     * continue;
     * }
     * }
     * }
     * pstmtInsert.setString(1,barredUserVO.getModule());
     * pstmtInsert.setString(2,barredUserVO.getNetworkCode());
     * pstmtInsert.setString(3,barredUserVO.getMsisdn());
     * //commented for DB2
     * pstmtInsert.setFormOfUse(4,OraclePreparedStatement.FORM_NCHAR);
     * pstmtInsert.setString(4,barredUserVO.getName());
     * //commented for DB2
     * pstmtInsert.setFormOfUse(5,OraclePreparedStatement.FORM_NCHAR);
     * pstmtInsert.setString(5,barredUserVO.getBarredReason());
     * pstmtInsert.setString(6,barredUserVO.getUserType());
     * pstmtInsert.setString(7,barredUserVO.getBarredType());
     * pstmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(new
     * Date(System.currentTimeMillis())));
     * pstmtInsert.setString(9,barredUserVO.getCreatedBy());
     * pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(new
     * Date(System.currentTimeMillis())));
     * pstmtInsert.setString(11,barredUserVO.getModifiedBy());
     * pstmtInsert.setDate(12,BTSLUtil.getSQLDateFromUtilDate(new
     * Date(System.currentTimeMillis())));
     * addCount = pstmtInsert.executeUpdate();
     * if (_log.isDebugEnabled())
     * _log.debug("addBarredUser","Query Executed= "+ insertQuery);
     * barredUserVO.setBar(true);
     * pstmtInsert.clearParameters() ;
     * }
     * }
     * }
     * catch (SQLException sqle)
     * {
     * _log.error("addBarredUserBulk","SQLException " + sqle.getMessage());
     * sqle.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .RAISED,EventLevelI.FATAL,"BarredUserDAO[addBarredUser]","","","",
     * "SQL Exception:"+sqle.getMessage());
     * throw new
     * BTSLBaseException(this,"addBarredUser","error.general.sql.processing");
     * }
     * catch (Exception e)
     * {
     * _log.error("addBarredUserBulk"," Exception " + e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"BarredUserDAO[addBarredUser]","","","","Exception:"
     * +e.getMessage());
     * throw new
     * BTSLBaseException(this,"addBarredUserBulk","error.general.processing");
     * }
     * finally
     * {
     * try {if (pstmtInsert != null) pstmtInsert.close();}catch (Exception e){}
     * try {if (pstmtSelectGeography != null)
     * pstmtSelectGeography.close();}catch (Exception e){}
     * try {if (pstmtSelectUsers != null) pstmtSelectUsers.close();}catch
     * (Exception e){}
     * try {if (pstmtSelectBarred != null) pstmtSelectBarred.close();}catch
     * (Exception e){}
     * 
     * try {if (rsselectBarred != null) rsselectBarred.close();}catch (Exception
     * e){}
     * try {if (rsselectUser != null) rsselectUser.close();}catch (Exception
     * e){}
     * try {if (rsselectGeography != null) rsselectGeography.close();}catch
     * (Exception e){}
     * 
     * if (_log.isDebugEnabled())
     * _log.debug("addBarredUserBulk"," Exiting  addCount" + addCount);
     * }
     * return unProcessedMsisdn.toString();
     * }
     * /**
     * Method deleteBarredUserBulk.
     * This method is used to add the Details of user in the barred_msisdn table
     * 
     * @param p_con Connection
     * 
     * @param p_msisdnList ArrayList
     * 
     * @return int
     * 
     * @throws BTSLBaseException
     */

    public String deleteBarredUserBulk(Connection p_con, ArrayList p_msisdnList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteBarredUserBulk", "Entering p_msisdnList " + p_msisdnList);
        ResultSet rsselect = null;
        int deleteCount = -1;
        StringBuffer unProcessedMsisdn = new StringBuffer();
        PreparedStatement pstmtDelete = null;
        PreparedStatement pstmtSelect = null;
        BarredUserVO barredUserVO = null;
        StringBuffer selectBuff = null;
        StringBuffer deleteQueryBuff = null;
        String deleteQuery = null;
        String selectQuery = null;
        boolean isRemoveStaff = false;
        try {
            if (p_msisdnList.size() != 0) {
                // check whether user is barred for other types
                selectBuff = new StringBuffer("SELECT 1 FROM barred_msisdns WHERE ");
                selectBuff.append(" module=? AND network_code=?");
                selectBuff.append("  AND msisdn=? AND UPPER(user_type)=UPPER(?)");
                selectBuff.append(" AND barred_type != ?");
                selectQuery = selectBuff.toString();
                if (_log.isDebugEnabled())
                    _log.debug("deleteFromBarredMsisdn", "Select Query::" + selectQuery);

                for (int index = 0; index < p_msisdnList.size(); index++) {
                    int i = 1;
                    barredUserVO = (BarredUserVO) p_msisdnList.get(index);
                    if (barredUserVO.getBarredTypeName().equals(PretupsI.ALL) || barredUserVO.getBarredTypeName().equals(PretupsI.BAR_TYPE_PARENT_BARRED)) {
                        isRemoveStaff = true;
                    }

                    if (!barredUserVO.getBarredTypeName().equals(PretupsI.ALL)) {
                        pstmtSelect = p_con.prepareStatement(selectQuery);
                        pstmtSelect.setString(i++, barredUserVO.getModule());
                        pstmtSelect.setString(i++, barredUserVO.getNetworkCode());
                        pstmtSelect.setString(i++, barredUserVO.getMsisdn());
                        pstmtSelect.setString(i++, barredUserVO.getUserType());
                        if (!BTSLUtil.isNullString(barredUserVO.getBarredTypeName()))
                            pstmtSelect.setString(i++, barredUserVO.getBarredType());
                        rsselect = pstmtSelect.executeQuery();
                        pstmtSelect.clearParameters();
                        if (!rsselect.next()) {
                            isRemoveStaff = true; // if not barred for the other
                                                  // types remove the staff from
                                                  // barred.
                        }
                    }
                    // remove both user and staff
                    if (!barredUserVO.getBarredTypeName().equals(PretupsI.ALL) || isRemoveStaff) {
                        do {
                            deleteQueryBuff = new StringBuffer("DELETE FROM barred_msisdns WHERE");
                            deleteQueryBuff.append(" UPPER(module)=UPPER(?) AND UPPER(network_code)=UPPER(?)");
                            deleteQueryBuff.append(" AND msisdn=? AND UPPER(user_type)=UPPER(?)");
                            if (!barredUserVO.getBarredTypeName().equals(PretupsI.ALL) || barredUserVO.getBarredTypeName().equals(PretupsI.BAR_TYPE_PARENT_BARRED))
                                deleteQueryBuff.append(" AND UPPER(barred_type)=UPPER(?)");
                            deleteQuery = deleteQueryBuff.toString();
                            if (_log.isDebugEnabled())
                                _log.debug("deleteFromBarredMsisdn", "Delete Query= " + deleteQuery);

                            int j = 1;
                            pstmtDelete = p_con.prepareStatement(deleteQuery);
                            pstmtDelete.setString(j++, barredUserVO.getModule());
                            pstmtDelete.setString(j++, barredUserVO.getNetworkCode());
                            pstmtDelete.setString(j++, barredUserVO.getMsisdn());
                            pstmtDelete.setString(j++, barredUserVO.getUserType());
                            if ((!barredUserVO.getBarredTypeName().equals(PretupsI.ALL)) || barredUserVO.getBarredTypeName().equals(PretupsI.BAR_TYPE_PARENT_BARRED)) {
                                if (!BTSLUtil.isNullString(barredUserVO.getBarredType()))
                                    pstmtDelete.setString(j++, barredUserVO.getBarredType());
                            }
                            deleteCount = pstmtDelete.executeUpdate();
                            if (_log.isDebugEnabled())
                                _log.debug("deleteBarredUserBulk", "Query Executed= " + deleteQuery);

                            barredUserVO.setBar(true);
                            pstmtDelete.clearParameters();
                            index++;
                            if (index >= p_msisdnList.size()) {
                                index--;
                                break;
                            }
                            barredUserVO = (BarredUserVO) p_msisdnList.get(index);
                            if (isRemoveStaff) {
                                index--;
                                isRemoveStaff = false;
                                break;
                            } else {
                                while (barredUserVO.getBarredTypeName().equals(PretupsI.BAR_TYPE_PARENT_BARRED)) {
                                    index++;
                                    if (index >= p_msisdnList.size()) {
                                        index--;
                                        break;
                                    }
                                    barredUserVO = (BarredUserVO) p_msisdnList.get(index);
                                }
                            }
                        } while (index < p_msisdnList.size() - 1);
                    }
                    // remove only channel users.
                    // else
                    // {

                    // }
                }
            }
        } catch (SQLException sqle) {
            _log.error("deleteBarredUserBulk", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteBarredUserBulk]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteBarredUserBulk", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("deleteBarredUserBulk", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteBarredUserBulk]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteBarredUserBulk", "error.general.processing");
        } finally {
            try {
                if (pstmtDelete != null)
                    pstmtDelete.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteBarredUserBulk", " Exiting  deleteCount" + deleteCount);
        }
        return unProcessedMsisdn.toString();
    }

    /**
     * Method loadBarredUserListForXMLAPI
     * This method used to load the information of barred user
     * 
     * @param p_con
     *            Connection
     * @param p_barredVO
     *            BarredUserVO
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadBarredUserListForXMLAPI(Connection p_con, BarredUserVO p_barredVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadBarredUserListForXMLAPI", "Entered p_barredVO " + p_barredVO);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList barredList = new ArrayList();
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT DISTINCT bm.module,n.network_name,bm.msisdn,name,bm.user_type,bm.barred_type,bm.created_on,U.user_name created_by, ");
        strBuff.append("bm.barred_reason,SL.sub_lookup_name FROM barred_msisdns bm,networks n ,sub_lookups SL,users U ");
        strBuff.append("WHERE bm.network_code=n.network_code AND SL.sub_lookup_code=BM.barred_type  AND SL.lookup_type=? ");
        strBuff.append("AND bm.created_by=U.user_id AND bm.network_code=? ");
        if (!BTSLUtil.isNullString(p_barredVO.getMsisdn()))
            strBuff.append(" AND bm.msisdn=?");
        if (!BTSLUtil.isNullString(p_barredVO.getModule())) {
            strBuff.append("AND bm.module=? ");
            strBuff.append("AND bm.user_type = DECODE(?,?,bm.user_type,?) ");
            strBuff.append("AND bm.barred_type = DECODE(?,?,bm.barred_type,?) ");
            strBuff.append("AND TRUNC(bm.created_on) >= ? AND TRUNC(bm.created_date) <= ? ");
        }
        strBuff.append("ORDER BY bm.msisdn ");
        try {
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.BARRING_TYPE);
            pstmtSelect.setString(i++, p_barredVO.getNetworkCode());
            if (!BTSLUtil.isNullString(p_barredVO.getMsisdn()))
                pstmtSelect.setString(i++, p_barredVO.getMsisdn());
            if (!BTSLUtil.isNullString(p_barredVO.getModule())) {
                pstmtSelect.setString(i++, p_barredVO.getModule());
                pstmtSelect.setString(i++, p_barredVO.getUserType());
                pstmtSelect.setString(i++, PretupsI.ALL);
                pstmtSelect.setString(i++, p_barredVO.getUserType());
                pstmtSelect.setString(i++, p_barredVO.getBarredType());
                pstmtSelect.setString(i++, PretupsI.ALL);
                pstmtSelect.setString(i++, p_barredVO.getBarredType());
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_barredVO.getFromDate())));
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_barredVO.getToDate())));

            }
            String selectQuery = strBuff.toString();
            BarredUserVO barredUserVO = null;
            if (_log.isDebugEnabled())
                _log.debug("loadBarredUserListForXMLAPI", "Select Query " + selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                barredUserVO = new BarredUserVO();
                LookupsVO lookupVO = (LookupsVO) LookupsCache.getObject(PretupsI.MODULE_TYPE, rs.getString("module"));
                barredUserVO.setModule(lookupVO.getLookupName());
                barredUserVO.setNetworkName(rs.getString("network_name"));
                barredUserVO.setMsisdn(rs.getString("msisdn"));
                barredUserVO.setName(rs.getString("name"));
                barredUserVO.setUserType(rs.getString("user_type"));
                barredUserVO.setBarredType(rs.getString("barred_type"));
                barredUserVO.setCreatedOn(rs.getDate("created_on"));
                barredUserVO.setCreatedBy(rs.getString("created_by"));
                barredUserVO.setBarredDate(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                barredUserVO.setBarredReason(rs.getString("barred_reason"));
                barredUserVO.setBarredTypeName(rs.getString("sub_lookup_name"));
                barredList.add(barredUserVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadBarredUserListForXMLAPI", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserListForXMLAPI]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadBarredUserListForXMLAPI", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadBarredUserListForXMLAPI", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserListForXMLAPI]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadBarredUserListForXMLAPI", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadBarredUserListForXMLAPI", "Exiting size= " + barredList.size());
        }
        return barredList;
    }

    /**
     * loadSingleBarredMsisdnDetails loads barredDetails from Barred_MSISDN
     * table for a single Msisdn.
     * 
     * @param p_con
     * @param p_module
     * @param p_networkCode
     * @param p_msisdn
     * @param p_userType
     * @param p_barredType
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSingleBarredMsisdnDetails(Connection p_con, String p_module, String p_networkCode, String p_msisdn, String p_userType, String p_barredType) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadSingleBarredMsisdnDetails", "Entered params Module:: p_module=" + p_module + ",p_networkCode=" + p_networkCode + ",p_msisdn=" + p_msisdn + ",p_userType=" + p_userType + ",p_barredType=" + p_barredType);

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer sqlBuff = new StringBuffer("SELECT barred_type,created_on FROM barred_msisdns WHERE ");
        sqlBuff.append("module=? AND network_code=? ");
        sqlBuff.append("AND msisdn=? AND (user_type=? )");// OR user_type='BOTH'
        if (!BTSLUtil.isNullString(p_barredType))
            sqlBuff.append("AND barred_type=? ");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadSingleBarredMsisdnDetails", "Select Query::" + selectQuery);
        ArrayList barredUserList = new ArrayList();
        BarredUserVO barredUserVO = null;
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, p_module);
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_userType);
            if (!BTSLUtil.isNullString(p_barredType))
                pstmtSelect.setString(i++, p_barredType);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                barredUserVO = new BarredUserVO();
                barredUserVO.setBarredType(rs.getString("barred_type"));
                barredUserVO.setCreatedOn(rs.getTimestamp("created_on"));
                barredUserVO.setModule(p_module);
                barredUserVO.setNetworkCode(p_networkCode);
                barredUserVO.setMsisdn(p_msisdn);
                barredUserVO.setUserType(p_userType);
                barredUserList.add(barredUserVO);

            }
        } catch (SQLException sqle) {
            _log.error("loadSingleBarredMsisdnDetails", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadSingleBarredMsisdnDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadSingleBarredMsisdnDetails", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadSingleBarredMsisdnDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSingleBarredMsisdnDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadSingleBarredMsisdnDetails", "Exiting: barredUserList.size()" + barredUserList.size());
        }
        return barredUserList;
    }

    /**
     * Method deleteSingleBarredMsisdn.(For Mali implementation)
     * This method is used to delete thesingle record from barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_module
     *            String
     * @return int
     * @throws BTSLBaseException
     */

    public int deleteSingleBarredMsisdn(Connection p_con, String p_module, String p_networkCode, String p_msisdn, String p_userType, String p_barredType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteSingleBarredMsisdn", "Entered filteredMSISDN:" + p_msisdn + " networkCode:" + p_networkCode + " module:" + p_module + " type:" + p_userType + " type:" + p_barredType);
        int deleteCount = -1;
        PreparedStatement pstmtDelete = null;
        StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM barred_msisdns WHERE");
        deleteQueryBuff.append(" UPPER(module)=UPPER(?) AND UPPER(network_code)=UPPER(?)");
        deleteQueryBuff.append(" AND msisdn=? AND UPPER(user_type)=UPPER(?)AND UPPER(barred_type)=UPPER(?)");
        String deleteQuery = deleteQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("deleteFromBarredMsisdn", "Delete Query= " + deleteQuery);
        try {
            pstmtDelete = p_con.prepareStatement(deleteQuery);
            pstmtDelete.setString(1, p_module);
            pstmtDelete.setString(2, p_networkCode);
            pstmtDelete.setString(3, p_msisdn);
            pstmtDelete.setString(4, p_userType);
            pstmtDelete.setString(5, p_barredType);
            deleteCount = pstmtDelete.executeUpdate();
            if (deleteCount <= 0)
                return deleteCount;
        } catch (SQLException sqle) {
            _log.error("deleteSingleBarredMsisdn", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteSingleBarredMsisdn]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteSingleBarredMsisdn", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("deleteSingleBarredMsisdn", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteSingleBarredMsisdn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteSingleBarredMsisdn", "error.general.processing");
        } finally {
            try {
                if (pstmtDelete != null)
                    pstmtDelete.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteFromBarredMsisdn", " Exiting deleteCount " + deleteCount);
        }
        return deleteCount;
    }

}
