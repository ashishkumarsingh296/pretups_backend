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

package com.btsl.pretups.subscriber.businesslogic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // commented for DB2 import
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// oracle.jdbc.OraclePreparedStatement;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.BarFileProccesingLog;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.restapi.user.service.BarredVo;

/**
 * 
 */
public class BarredUserDAO {

    /**
     * Field _log.
     */
    private static final Log _log = LogFactory.getFactory().getInstance(BarredUserDAO.class.getName());
    
    private BarredUserQry barredUserQry = (BarredUserQry)
    		ObjectProducer.getObject(QueryConstants.BARRED_USER_QRY, QueryConstants.QUERY_PRODUCER);

    /**
     * Method addBarredUser. This method is used to add the Details of user in
     * the barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_barredUserVO
     *            BarredUserVO
     * @return int
     * @throws BTSLBaseException
     */

    public int addBarredUser(Connection p_con, BarredUserVO p_barredUserVO) throws BTSLBaseException {
        final String methodName = "addBarredUser";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering p_barredUserVO " + p_barredUserVO);
        }

        int addCount = -1;
        

        
        StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO barred_msisdns ");
        if(!(PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE.equals(p_barredUserVO.getServiceType()))){
        insertQueryBuff.append("(module,network_code,msisdn,name,barred_reason,");
        insertQueryBuff.append("user_type,barred_type,created_on,created_by,");
        insertQueryBuff.append("modified_on,modified_by,created_date)");
        insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
        }
        else{
        	insertQueryBuff.append("(module,network_code,msisdn,name,barred_reason,");
        	 insertQueryBuff.append("user_type,barred_type,created_on,created_by,");
             insertQueryBuff.append("modified_on,modified_by,created_date,for_msisdn)");
             insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
        }
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addBarredUser()", "Insert Query= " + insertQuery);
        }
        try(PreparedStatement pstmtInsert = p_con.prepareStatement(insertQuery);) {
            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            
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
            if((PretupsI.SERVICE_TYPE_BAR_GIVE_ME_BALANCE.equals(p_barredUserVO.getServiceType()))){
            	pstmtInsert.setString(13, p_barredUserVO.getForMsisdn());
            }
            
            addCount = pstmtInsert.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + insertQuery);
            }
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[addBarredUser]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[addBarredUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
         
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting  addCount" + addCount);
            }
        }

        return addCount;
    }

    /**
     * Method insertIntoHistory. This method is used to copy the record from
     * barred_msisdn table from barred_msisdn_history table if the
     * networkcode,module type and msisdn matched.
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
     * p_barredUserVOList) throws BTSLBaseException { if (_log.isDebugEnabled())
     * _log.debug("insertIntoHistory","Entering p_barredUserVOList.size()=" +
     * p_barredUserVOList.size());
     * 
     * int addCount=-1; int deleteCount=-1; PreparedStatement pstmtInsert =
     * null; StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO
     * barred_msisdn_history");
     * insertQueryBuff.append("(module,network_code,msisdn,name,barred_reason,");
     * insertQueryBuff.append("user_type,barred_type,created_on,created_by,");
     * insertQueryBuff.append("modified_on,modified_by,created_date)(");
     * insertQueryBuff.append("SELECT
     * module,network_code,msisdn,name,barred_reason,");
     * insertQueryBuff.append("user_type,barred_type,created_on,created_by,");
     * insertQueryBuff.append("modified_on,modified_by,created_date FROM
     * barred_msisdns"); insertQueryBuff.append(" WHERE UPPER(module)=UPPER(?)
     * AND UPPER(network_code)=UPPER(?)"); insertQueryBuff.append(" AND msisdn=?
     * AND UPPER(user_type)=UPPER(?) AND UPPER(barred_type)=UPPER(?))"); String
     * insertQuery = insertQueryBuff.toString(); if (_log.isDebugEnabled())
     * _log.debug("insertIntoHistory","Insert Query= "+ insertQuery); try {
     * BarredUserVO barredUserVO=null; pstmtInsert =
     * p_con.prepareStatement(insertQuery); for(int
     * i=0,j=p_barredUserVOList.size();i<j;i++) {
     * barredUserVO=(BarredUserVO)p_barredUserVOList.get(i);
     * if(!barredUserVO.getMultiBox().equals(PretupsI.SELECT_CHECKBOX))
     * continue; if (_log.isDebugEnabled())
     * _log.debug("insertIntoHistory","barredUserVO = "+ barredUserVO);
     * 
     * pstmtInsert.setString(1,barredUserVO.getModule());
     * pstmtInsert.setString(2,barredUserVO.getNetworkCode());
     * pstmtInsert.setString(3,barredUserVO.getMsisdn());
     * pstmtInsert.setString(4,barredUserVO.getUserType());
     * pstmtInsert.setString(5,barredUserVO.getBarredType()); addCount =
     * pstmtInsert.executeUpdate(); if(addCount<=0) return addCount; //If
     * successfully inserted in history table delete the corresponding //record
     * from the barred_msisdn table.
     * 
     * deleteCount=this.deleteFromBarredMsisdn(p_con,barredUserVO);
     * if(deleteCount<=0) return deleteCount; pstmtInsert.clearParameters(); } }
     * catch (SQLException sqle) {
     * _log.error("insertIntoHistory","SQLException " +
     * sqle.getMessage()); sql_log.errorTrace(methodName,e);
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"BarredUserDAO[insertIntoHistory]","","","","SQL
     * Exception:"+sqle.getMessage()); throw new
     * BTSLBaseException(this,"insertIntoHistory","error.general.sql.processing")
     * ; }
     * catch (Exception e) { _log.error("addBarredUser"," Exception " +
     * e.getMessage()); _log.errorTrace(methodName,e);
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .RAISED,EventLevelI.FATAL,"BarredUserDAO[insertIntoHistory]","","","",
     * "Exception:"+e.getMessage());
     * throw new
     * BTSLBaseException(this,"insertIntoHistory","error.general.processing"); }
     * finally { try {if (pstmtInsert != null) pstmtInsert.close();}catch
     * (Exception e){} if (_log.isDebugEnabled())
     * _log.debug("insertIntoHistory"," Exiting addCount "+ addCount); } return
     * deleteCount; }
     */
    /**
     * Method deleteFromBarredMsisdn. This method is used to delete the record
     * from barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_barredUserVO
     *            BarredUserVO
     * @return int
     * @throws BTSLBaseException
     */

    public int deleteFromBarredMsisdn(Connection p_con, ArrayList p_barredUserVOList) throws BTSLBaseException {
        final String methodName = "deleteFromBarredMsisdn";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering p_barredUserVOList.size() " + p_barredUserVOList.size());
        }

        int deleteCount = -1;
        
        StringBuilder deleteQueryBuff = new StringBuilder("DELETE FROM barred_msisdns WHERE");
        deleteQueryBuff.append(" UPPER(module)=UPPER(?) AND UPPER(network_code)=UPPER(?)");
        deleteQueryBuff.append(" AND msisdn=? AND UPPER(user_type)=UPPER(?)AND UPPER(barred_type)=UPPER(?)");
        String deleteQuery = deleteQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Delete Query= " + deleteQuery);
        }
        try {
            BarredUserVO barredUserVO = null;
            try(PreparedStatement pstmtDelete = p_con.prepareStatement(deleteQuery);)
            {
            for (int i = 0, j = p_barredUserVOList.size(); i < j; i++) {
                barredUserVO = (BarredUserVO) p_barredUserVOList.get(i);
                if (!barredUserVO.getMultiBox().equals(PretupsI.SELECT_CHECKBOX)) {
                    continue;
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("insertIntoHistory", "barredUserVO = " + barredUserVO);
                }

                pstmtDelete.setString(1, barredUserVO.getModule());
                pstmtDelete.setString(2, barredUserVO.getNetworkCode());
                pstmtDelete.setString(3, barredUserVO.getMsisdn());
                pstmtDelete.setString(4, barredUserVO.getUserType());
                pstmtDelete.setString(5, barredUserVO.getBarredType());
                deleteCount = pstmtDelete.executeUpdate();
                if (deleteCount <= 0) {
                    return deleteCount;
                }
                pstmtDelete.clearParameters();
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteFromBarredMsisdn]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteFromBarredMsisdn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting deleteCount " + deleteCount);
            }
        }
        return deleteCount;
    }
    
    
    /**
     * Method deleteFromBarredMsisdn. This method is used to delete the record
     * from barred_msisdn table. This is copy of above method but for spring development 
     * we had to change some logic.
     * 
     * @param p_con
     *            Connection
     * @param p_barredUserVO
     *            BarredUserVO
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteFromBarredMsisdnTable(Connection p_con, List<BarredUserVO> p_barredUserVOList) throws BTSLBaseException {
        final String methodName = "deleteFromBarredMsisdn";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering p_barredUserVOList.size() " + p_barredUserVOList.size());
        }

        int deleteCount = -1;
        int totalCount = 0;
        
        StringBuilder deleteQueryBuff = new StringBuilder("DELETE FROM barred_msisdns WHERE");
        deleteQueryBuff.append(" UPPER(module)=UPPER(?) AND UPPER(network_code)=UPPER(?)");
        deleteQueryBuff.append(" AND msisdn=? AND UPPER(user_type)=UPPER(?)AND UPPER(barred_type)=UPPER(?)");
        String deleteQuery = deleteQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Delete Query= " + deleteQuery);
        }
        try {
            BarredUserVO barredUserVO = null;
            try(PreparedStatement pstmtDelete = p_con.prepareStatement(deleteQuery);)
            {
            for (int i = 0, j = p_barredUserVOList.size(); i < j; i++) {
                barredUserVO = p_barredUserVOList.get(i);
                pstmtDelete.setString(1, barredUserVO.getModule());
                pstmtDelete.setString(2, barredUserVO.getNetworkCode());
                pstmtDelete.setString(3, barredUserVO.getMsisdn());
                pstmtDelete.setString(4, barredUserVO.getUserType());
                pstmtDelete.setString(5, barredUserVO.getBarredType());
                deleteCount = pstmtDelete.executeUpdate();
                if (deleteCount <= 0) {
                	totalCount--;
                    return totalCount;
                }
                totalCount++;
                pstmtDelete.clearParameters();
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteFromBarredMsisdn]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteFromBarredMsisdn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting deleteCount " + deleteCount);
            }
        }
        return totalCount;
    }


    /**
     * Method loadInfoOfBarredUser This method used to load the details of
     * barred user from barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_barredVO
     *            BarredUserVO
     * @return boolean
     * @throws BTSLBaseException
     * 
     */

    public ArrayList loadInfoOfBarredUser(Connection p_con, BarredUserVO p_barredVO) throws BTSLBaseException {

        final String methodName = "loadInfoOfBarredUser";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "p_barredVO " + p_barredVO);
        }
        
        
        StringBuilder sqlBuff = new StringBuilder();
        sqlBuff.append("SELECT distinct BM.name,BM.barred_type,BM.barred_reason,");
        sqlBuff.append("BM.created_on,BM.created_by,BM.modified_on,BM.modified_by,BM.created_date,BM.for_msisdn, ");
        sqlBuff.append("SL.sub_lookup_name,L.lookup_name ");
        sqlBuff.append("FROM barred_msisdns BM,sub_lookups SL , lookups L ");
        sqlBuff.append("WHERE UPPER(module)=UPPER(?) AND network_code=? AND msisdn=? ");
        sqlBuff.append("AND UPPER(user_type)=UPPER(?) AND SL.sub_lookup_code=BM.barred_type AND SL.lookup_type=? ");
        sqlBuff.append("AND L.lookup_type=? AND L.lookup_code=BM.user_type ");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }
        ArrayList barredUserList = new ArrayList();
        BarredUserVO barredUserVO = null;
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);){
            
            pstmtSelect.setString(1, p_barredVO.getModule());
            pstmtSelect.setString(2, p_barredVO.getNetworkCode());
            pstmtSelect.setString(3, p_barredVO.getMsisdn());
            pstmtSelect.setString(4, p_barredVO.getUserType());
            pstmtSelect.setString(5, PretupsI.BARRING_TYPE);
            pstmtSelect.setString(6, PretupsI.BARRED_USER_TYPE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
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
                barredUserVO.setBarredDate(BTSLDateUtil.getLocaleTimeStamp((BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")))));
                barredUserVO.setUserTypeDesc(rs.getString("lookup_name"));
                barredUserVO.setForMsisdn(rs.getString("for_msisdn"));
                barredUserList.add(barredUserVO);
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadInfoOfBarredUser]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadInfoOfBarredUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: barredUserList.size()" + barredUserList.size());
            }
        }

        return barredUserList;
    }

    /**
     * Method loadBarredUserList This method used to load the information of
     * barred user
     * 
     * @param p_con
     *            Connection
     * @param p_barredVO
     *            BarredUserVO
     * @return ArrayList
     * @throws BTSLBaseException
     * 
     */

    public ArrayList loadBarredUserList(Connection p_con, BarredUserVO p_barredVO) throws BTSLBaseException {
        final String methodName = "loadBarredUserList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_barredVO " + p_barredVO);
        }

         
        
        ArrayList barredList = new ArrayList();
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT distinct bm.module,n.network_name,bm.msisdn,name,bm.user_type,bm.barred_type,bm.created_on,( CASE  WHEN  U.user_id = 'SU0001' THEN  N'SYSTEM' else U.user_name END) created_by, ");
        strBuff.append("bm.barred_reason,SL.sub_lookup_name,BM.for_msisdn FROM barred_msisdns bm,networks n ,sub_lookups SL,users U ");
        strBuff.append("WHERE bm.network_code=n.network_code AND SL.sub_lookup_code=BM.barred_type AND SL.lookup_type=? AND  U.user_id= ( CASE  WHEN bm.created_by in ('SYSTEM') THEN  'SU0001' else bm.created_by END) ");
        if (!BTSLUtil.isNullString(p_barredVO.getMsisdn())) {
            strBuff.append(" AND bm.msisdn=? ");
        } else {
            strBuff.append(" AND bm.created_date >= ? AND bm.created_date <= ? ");
            strBuff.append(" AND bm.module=? ");
            strBuff.append(" AND bm.user_type = case ? when ? then bm.user_type else ? end ");
            strBuff.append(" AND bm.barred_type = case ? when ? then bm.barred_type else ? end  AND bm.network_code= ? ");
        }
        strBuff.append(" ORDER BY bm.created_on DESC");
        
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());) {
            
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.BARRING_TYPE);

            if (!BTSLUtil.isNullString(p_barredVO.getMsisdn())) {
                pstmtSelect.setString(i++, p_barredVO.getMsisdn());
            } else {
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query " + selectQuery);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
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
                barredUserVO.setBarredDate(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on"))));
                barredUserVO.setBarredReason(rs.getString("barred_reason"));
                barredUserVO.setBarredTypeName(rs.getString("sub_lookup_name"));
                barredUserVO.setForMsisdn(rs.getString("for_msisdn"));
                barredList.add(barredUserVO);
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting size= " + barredList.size());
            }
        }
        return barredList;
    }
    
    
    /**
     * This method is checks the user information before adding the record in
     * the barred_msisdn table and returns true if the record is found
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
     * 
     */

    public boolean isExists(Connection p_con, String p_module, String p_networkCode, String p_msisdn, String p_userType, String p_barredType) throws BTSLBaseException {

        final String methodName = "isExists";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered params Module:: p_module=" + p_module + ",p_networkCode=" + p_networkCode + ",p_msisdn=" + p_msisdn + ",p_userType=" + p_userType + ",p_barredType=" + p_barredType);
        }

        
        
        boolean found = false;
      
        String selectQuery = barredUserQry.isExistsQry(p_barredType);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }
    

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
           
            int i = 1;
            pstmtSelect.setString(i++, p_module);
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_userType);
            if (!BTSLUtil.isNullString(p_barredType)) {
                pstmtSelect.setString(i++, p_barredType);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[isExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[isExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: isExists found=" + found);
            }
        }
        return found;
    }

    /**
     * Method addBarredUserBulk. This method is used to add the Details of user
     * in the barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_msisdnList
     *            ArrayList
     * @param p_sessionUserVO
     * @return int
     * @throws BTSLBaseException
     */

    public String addBarredUserBulk(Connection p_con, ArrayList p_msisdnList, UserVO p_sessionUserVO) throws BTSLBaseException {
        final String methodName = "addBarredUserBulk";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering p_msisdnList.size(): " + p_msisdnList.size() + " p_sessionUserVO :" + p_sessionUserVO.toString());
        }
        ResultSet rsselectBarred = null;
        ResultSet rsselectUser = null;
        ResultSet rsselectGeography = null;
        int addCount = -1;
        StringBuilder unProcessedMsisdn = new StringBuilder();// commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtSelectBarred = null;
        PreparedStatement pstmtSelectUsers = null;
        PreparedStatement pstmtSelectGeography = null;
        BarredUserVO barredUserVO = null;
        boolean isChDomainCheckRequired = false;
        boolean isGeoDomainCheckRequired = false;
        ListValueVO vo = null;
        ArrayList domainList = null;
        ArrayList domainListStr = new ArrayList();

        StringBuilder selectBuff = new StringBuilder();

        StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO barred_msisdns ");
        insertQueryBuff.append("(module,network_code,msisdn,name,barred_reason,");
        insertQueryBuff.append("user_type,barred_type,created_on,created_by,");
        insertQueryBuff.append("modified_on,modified_by,created_date)");
        insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
      
        String selectQuery = barredUserQry.addBarredUserBulkFromBarredMsisdns();

        StringBuilder selectQueryBuffUser = new StringBuilder(" SELECT u.user_id, cat.domain_code ");
        selectQueryBuffUser.append(" FROM users u,categories cat,user_phones uphones ");
        selectQueryBuffUser.append(" WHERE uphones.msisdn=? AND uphones.user_id=u.user_id AND u.status <> 'N' AND u.status <>'C' ");
        selectQueryBuffUser.append(" AND u.category_code = cat.category_code ");
        String selectQueryUsers = selectQueryBuffUser.toString();

        String selectGeography = barredUserQry.addBarredUserBulkRecordExistGeography();
        if (_log.isDebugEnabled()) {
            _log.debug("addBarredUserBulk()", "selectGeography Query= " + selectGeography);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addBarredUserBulk()", "selectQueryUsers Query= " + selectQueryUsers);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addBarredUserBulk()", "selectQuery Query= " + selectQuery);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addBarredUserBulk()", "Insert Query= " + insertQuery);
        }
        try {
            if (PretupsI.BCU_USER.equals(p_sessionUserVO.getCategoryVO().getCategoryCode())) {
                isGeoDomainCheckRequired = true;
            }
            if (PretupsI.DOMAINS_ASSIGNED.equals(p_sessionUserVO.getCategoryVO().getFixedDomains())) {
                domainList = BTSLUtil.displayDomainList(p_sessionUserVO.getDomainList());
                isChDomainCheckRequired = true;
                for (int i = 0, j = domainList.size(); i < j; i++) {
                    vo = (ListValueVO) domainList.get(i);
                    domainListStr.add(vo.getValue());
                }
            }
            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtSelectBarred = p_con.prepareStatement(selectQuery);
            pstmtSelectUsers = p_con.prepareStatement(selectQueryUsers);
            pstmtSelectGeography = p_con.prepareStatement(selectGeography);
            int i;
            String userID = null;
            String domainID = null;
            if (!p_msisdnList.isEmpty()) {
                for (int index = 0, j = p_msisdnList.size(); index < j; index++) {
                    i = 1;
                    barredUserVO = (BarredUserVO) p_msisdnList.get(index);
                    pstmtSelectBarred.setString(i++, barredUserVO.getModule());
                    pstmtSelectBarred.setString(i++, barredUserVO.getNetworkCode());
                    pstmtSelectBarred.setString(i++, barredUserVO.getMsisdn());
                    pstmtSelectBarred.setString(i++, barredUserVO.getUserType());
                    if (!BTSLUtil.isNullString(barredUserVO.getBarredType())) {
                        pstmtSelectBarred.setString(i++, barredUserVO.getBarredType());
                    }
                    rsselectBarred = pstmtSelectBarred.executeQuery();
                    pstmtSelectBarred.clearParameters();
                    if (rsselectBarred.next()) {
                        BarFileProccesingLog.log("BULKBARRED", barredUserVO.getCreatedBy(), barredUserVO.getMsisdn(), (index + 1), "Fail", "This mobile number is already bar", null);
                        unProcessedMsisdn.append(barredUserVO.getMsisdn() + ", ");
                        continue;
                    } else if (PretupsI.C2S_MODULE.equals(barredUserVO.getModule()) && PretupsI.USER_TYPE_SENDER.equals(barredUserVO.getUserType())) {
                        {
                            pstmtSelectUsers.setString(1, barredUserVO.getMsisdn());
                            rsselectUser = pstmtSelectUsers.executeQuery();
                            pstmtSelectUsers.clearParameters();
                            if (rsselectUser.next()) {
                                userID = rsselectUser.getString("user_id");
                                domainID = rsselectUser.getString("domain_code");
                                if (isChDomainCheckRequired) {
                                    if (!domainListStr.contains(domainID))// Channel
                                    // user
                                    // does
                                    // not
                                    // in
                                    // domain
                                    {
                                        BarFileProccesingLog.log("BULKBARRED", barredUserVO.getCreatedBy(), barredUserVO.getMsisdn(), (index + 1), "Fail", "Channel user does not in domain", null);
                                        unProcessedMsisdn.append(barredUserVO.getMsisdn() + ", ");
                                        continue;
                                    }
                                }
                                if (isGeoDomainCheckRequired) {
                                    pstmtSelectGeography.setString(1, p_sessionUserVO.getUserID());
                                    pstmtSelectGeography.setString(2, userID);
                                    rsselectGeography = pstmtSelectGeography.executeQuery();
                                    pstmtSelectGeography.clearParameters();
                                    if (!rsselectGeography.next())// Channel
                                    // user not
                                    // in
                                    // Geography
                                    // Herirarchy
                                    {
                                        BarFileProccesingLog.log("BULKBARRED", barredUserVO.getCreatedBy(), barredUserVO.getMsisdn(), (index + 1), "Fail", "Channel user not in Geography Herirarchy", null);
                                        unProcessedMsisdn.append(barredUserVO.getMsisdn() + ", ");
                                        continue;
                                    }
                                }
                            } else// channel user not exist
                            {
                                BarFileProccesingLog.log("BULKBARRED", barredUserVO.getCreatedBy(), barredUserVO.getMsisdn(), (index + 1), "Fail", "Channel user does not Exist", null);
                                unProcessedMsisdn.append(barredUserVO.getMsisdn() + ", ");
                                continue;
                            }
                        }
                    }
                    pstmtInsert.setString(1, barredUserVO.getModule());
                    pstmtInsert.setString(2, barredUserVO.getNetworkCode());
                    pstmtInsert.setString(3, barredUserVO.getMsisdn());
                    // commented for DB2
                    // pstmtInsert.setFormOfUse(4,OraclePreparedStatement.FORM_NCHAR);
                    pstmtInsert.setString(4, barredUserVO.getName());
                    // commented for DB2
                    // pstmtInsert.setFormOfUse(5,OraclePreparedStatement.FORM_NCHAR);
                    pstmtInsert.setString(5, barredUserVO.getBarredReason());
                    pstmtInsert.setString(6, barredUserVO.getUserType());
                    pstmtInsert.setString(7, barredUserVO.getBarredType());
                    pstmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(new Date(System.currentTimeMillis())));
                    pstmtInsert.setString(9, barredUserVO.getCreatedBy());
                    pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(new Date(System.currentTimeMillis())));
                    pstmtInsert.setString(11, barredUserVO.getModifiedBy());
                    pstmtInsert.setDate(12, BTSLUtil.getSQLDateFromUtilDate(new Date(System.currentTimeMillis())));
                    addCount = pstmtInsert.executeUpdate();
                    if (_log.isDebugEnabled()) {
                        _log.debug("addBarredUser", "Query Executed= " + insertQuery);
                    }
                    barredUserVO.setBar(true);
                    pstmtInsert.clearParameters();
                }
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[addBarredUser]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addBarredUser", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[addBarredUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (rsselectGeography!= null){
            		rsselectGeography.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtSelectGeography!= null){
        			pstmtSelectGeography.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (rsselectUser!= null){
            		rsselectUser.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtSelectUsers!= null){
        			pstmtSelectUsers.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (rsselectBarred!= null){
            		rsselectBarred.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtSelectBarred!= null){
        			pstmtSelectBarred.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting  addCount" + addCount);
            }
        }
        return unProcessedMsisdn.toString();
    }

    /**
     * Method deleteBarredUserBulk. This method is used to add the Details of
     * user in the barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_msisdnList
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     */

    public String deleteBarredUserBulk(Connection p_con, ArrayList p_msisdnList) throws BTSLBaseException {
        final String methodName = "deleteBarredUserBulk";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering p_msisdnList " + p_msisdnList);
        }
        ResultSet rsselect = null;
        int deleteCount = -1;
        StringBuilder unProcessedMsisdn = new StringBuilder();
        
        PreparedStatement pstmtSelect = null;
        BarredUserVO barredUserVO = null;
        StringBuilder selectBuff = null;
        StringBuilder deleteQueryBuff = null;
        String deleteQuery = null;
        String selectQuery = null;
        boolean isRemoveStaff = false;
        try {
            if (!p_msisdnList.isEmpty()) {
                // check whether user is barred for other types
                selectBuff = new StringBuilder("SELECT 1 FROM barred_msisdns WHERE ");
                selectBuff.append(" module=? AND network_code=?");
                selectBuff.append("  AND msisdn=? AND UPPER(user_type)=UPPER(?)");
                selectBuff.append(" AND barred_type != ?");
                selectQuery = selectBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug("deleteFromBarredMsisdn", "Select Query::" + selectQuery);
                }
                int msisdnListSize = p_msisdnList.size();
                for (int index = 0; index < msisdnListSize; index++) {
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
                        if (!BTSLUtil.isNullString(barredUserVO.getBarredTypeName())) {
                            pstmtSelect.setString(i++, barredUserVO.getBarredType());
                        }
                        rsselect = pstmtSelect.executeQuery();
                        pstmtSelect.clearParameters();
                        if (!rsselect.next()) {
                            isRemoveStaff = true; // if not
                            // barred
                            // for the
                            // other
                            // types
                            // remove
                            // the staff
                            // from
                            // barred.
                        }
                    }
                    // remove both user and staff
                    if (!barredUserVO.getBarredTypeName().equals(PretupsI.ALL) || isRemoveStaff) {
                        do {
                            deleteQueryBuff = new StringBuilder("DELETE FROM barred_msisdns WHERE");
                            deleteQueryBuff.append(" UPPER(module)=UPPER(?) AND UPPER(network_code)=UPPER(?)");
                            deleteQueryBuff.append(" AND msisdn=? AND UPPER(user_type)=UPPER(?)");
                            if (!barredUserVO.getBarredTypeName().equals(PretupsI.ALL) || barredUserVO.getBarredTypeName().equals(PretupsI.BAR_TYPE_PARENT_BARRED)) {
                                deleteQueryBuff.append(" AND UPPER(barred_type)=UPPER(?)");
                            }
                            deleteQuery = deleteQueryBuff.toString();
                            if (_log.isDebugEnabled()) {
                                _log.debug("deleteFromBarredMsisdn", "Delete Query= " + deleteQuery);
                            }

                            int j = 1;
                            try(PreparedStatement pstmtDelete = p_con.prepareStatement(deleteQuery);)
                            {
                            pstmtDelete.setString(j++, barredUserVO.getModule());
                            pstmtDelete.setString(j++, barredUserVO.getNetworkCode());
                            pstmtDelete.setString(j++, barredUserVO.getMsisdn());
                            pstmtDelete.setString(j++, barredUserVO.getUserType());
                            if ((!barredUserVO.getBarredTypeName().equals(PretupsI.ALL)) || barredUserVO.getBarredTypeName().equals(PretupsI.BAR_TYPE_PARENT_BARRED)) {
                                if (!BTSLUtil.isNullString(barredUserVO.getBarredType())) {
                                    pstmtDelete.setString(j++, barredUserVO.getBarredType());
                                }
                            }
                            deleteCount = pstmtDelete.executeUpdate();
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Query Executed= " + deleteQuery);
                            }
                            if (deleteCount > 0) {
                                barredUserVO.setBar(true);
                            }
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
                        } 
                        }while (index < p_msisdnList.size() - 1);
                    }
  
                }
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteBarredUserBulk]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteBarredUserBulk]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	try{
            	if (rsselect!= null){
            		rsselect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting  deleteCount" + deleteCount);
            }
        }
        return unProcessedMsisdn.toString();
    }

    /**
     * Method loadBarredUserListForXMLAPI This method used to load the
     * information of barred user
     * 
     * @param p_con
     *            Connection
     * @param p_barredVO
     *            BarredUserVO
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadBarredUserListForXMLAPI(Connection p_con, BarredUserVO p_barredVO) throws BTSLBaseException {
        final String methodName = "loadBarredUserListForXMLAPI";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_barredVO " + p_barredVO);
        }

        
         
        ArrayList barredList = new ArrayList();
        final String strBuff  = barredUserQry.loadBarredUserListForXMLAPIQry(p_barredVO.getModule(), p_barredVO.getMsisdn());
        
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff);) {
            
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.BARRING_TYPE);
            pstmtSelect.setString(i++, p_barredVO.getNetworkCode());
            if (!BTSLUtil.isNullString(p_barredVO.getMsisdn())) {
                pstmtSelect.setString(i++, p_barredVO.getMsisdn());
            }
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query " + selectQuery);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
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
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserListForXMLAPI]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserListForXMLAPI]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting size= " + barredList.size());
            }
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

        final String methodName = "loadSingleBarredMsisdnDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered params Module:: p_module=" + p_module + ",p_networkCode=" + p_networkCode + ",p_msisdn=" + p_msisdn + ",p_userType=" + p_userType + ",p_barredType=" + p_barredType);
        }

         
         
        StringBuilder sqlBuff = new StringBuilder("SELECT barred_type,created_on FROM barred_msisdns WHERE ");
        sqlBuff.append("module=? AND network_code=? ");
        sqlBuff.append("AND msisdn=? AND (user_type=? )");// OR
        // user_type='BOTH'
        if (!BTSLUtil.isNullString(p_barredType)) {
            sqlBuff.append("AND barred_type=? ");
        }
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }
        ArrayList barredUserList = new ArrayList();
        BarredUserVO barredUserVO = null;
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
            
            int i = 1;
            pstmtSelect.setString(i++, p_module);
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_userType);
            if (!BTSLUtil.isNullString(p_barredType)) {
                pstmtSelect.setString(i++, p_barredType);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
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
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadSingleBarredMsisdnDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExists", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadSingleBarredMsisdnDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
          
          
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: barredUserList.size()" + barredUserList.size());
            }
        }
        return barredUserList;
    }

    /**
     * Method deleteSingleBarredMsisdn.(For Mali implementation) This method is
     * used to delete thesingle record from barred_msisdn table
     * 
     * @param p_con
     *            Connection
     * @param p_module
     *            String
     * @return int
     * @throws BTSLBaseException
     */

    public int deleteSingleBarredMsisdn(Connection p_con, String p_module, String p_networkCode, String p_msisdn, String p_userType, String p_barredType) throws BTSLBaseException {
        final String methodName = "deleteSingleBarredMsisdn";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered filteredMSISDN:" + p_msisdn + " networkCode:" + p_networkCode + " module:" + p_module + " type:" + p_userType + " type:" + p_barredType);
        }
        int deleteCount = -1;
         
        StringBuilder deleteQueryBuff = new StringBuilder("DELETE FROM barred_msisdns WHERE");
        deleteQueryBuff.append(" UPPER(module)=UPPER(?) AND UPPER(network_code)=UPPER(?)");
        deleteQueryBuff.append(" AND msisdn=? AND UPPER(user_type)=UPPER(?)AND UPPER(barred_type)=UPPER(?)");
        String deleteQuery = deleteQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("deleteFromBarredMsisdn", "Delete Query= " + deleteQuery);
        }
        try(PreparedStatement pstmtDelete = p_con.prepareStatement(deleteQuery);) {
            
            pstmtDelete.setString(1, p_module);
            pstmtDelete.setString(2, p_networkCode);
            pstmtDelete.setString(3, p_msisdn);
            pstmtDelete.setString(4, p_userType);
            pstmtDelete.setString(5, p_barredType);
            deleteCount = pstmtDelete.executeUpdate();
            if (deleteCount <= 0) {
                return deleteCount;
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteSingleBarredMsisdn]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[deleteSingleBarredMsisdn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
            if (_log.isDebugEnabled()) {
                _log.debug("deleteFromBarredMsisdn", " Exiting deleteCount " + deleteCount);
            }
        }
        return deleteCount;
    }
    public int barredDataExistsForGMB(Connection p_con,String msisdn1, String msisdn2, String serviceType ) throws BTSLBaseException{
    	   PreparedStatement pstmtSelect = null;
           ResultSet rs = null;
           PreparedStatement pstmtSelect1 = null;
           ResultSet rs1 = null;
        try{
           StringBuilder sqlBuff = new StringBuilder("SELECT barred_type,for_msisdn, msisdn FROM barred_msisdns WHERE ");
           sqlBuff.append("msisdn = ? AND for_msisdn = ? AND barred_type = ? ");
           
           String selectQuery = sqlBuff.toString();
           if (_log.isDebugEnabled()) {
               _log.debug("barredDataExistsForGMB", "Select Query::" + selectQuery);
           }
           pstmtSelect = p_con.prepareStatement(selectQuery);
           int i = 1;
           pstmtSelect.setString(i++, msisdn1);
           pstmtSelect.setString(i++, msisdn2);
           pstmtSelect.setString(i++, serviceType);
           rs = pstmtSelect.executeQuery();
           if(rs.next())
           {
        	   return 1;
           }
           else{
               pstmtSelect1 = p_con.prepareStatement(selectQuery);
               i=1;
               pstmtSelect1.setString(i++, msisdn2);
               pstmtSelect1.setString(i++, msisdn2);
               pstmtSelect1.setString(i++, serviceType);
               rs1 = pstmtSelect1.executeQuery();
               if(rs1.next())
               {
            	   return 2;
               }
           }
        
        } catch (SQLException sqle) {
        _log.error("barredDataExistsForGMB", "SQLException " + sqle.getMessage());
        _log.errorTrace("barredDataExistsForGMB", sqle);
         throw new BTSLBaseException(this, "barredDataExistsForGMB", "error.general.sql.processing");
        } catch (Exception e) {
        _log.error("barredDataExistsForGMB", "Exception " + e.getMessage());
        _log.errorTrace("barredDataExistsForGMB", e);
        throw new BTSLBaseException(this, "barredDataExistsForGMB", "error.general.sql.processing");
        }	
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	try{
            	if (rs1!= null){
            		rs1.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtSelect1!= null){
        			pstmtSelect1.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
          OracleUtil.closeQuietly(p_con);
        }
        return 0;  
    }
    
    /**
     * @param p_con
     * @param p_barredVO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList<BarredVo> fetchBarredUserList(Connection p_con, BarredVo p_barredVO) throws BTSLBaseException {
        final String methodName = "fetchBarredUserList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_barredVO " + p_barredVO);
        }

        ArrayList<BarredVo> barredList = new ArrayList<BarredVo>();
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT distinct bm.module,n.network_name,bm.msisdn,us.user_name,us.login_id,bm.user_type barred_As,bm.barred_type,bm.created_on,( CASE  WHEN  U.user_id = 'SU0001' THEN  N'SYSTEM' else U.user_name END) created_by, ");
        strBuff.append("bm.barred_reason,SL.sub_lookup_name,BM.for_msisdn,us.CATEGORY_CODE,us.USER_TYPE,d.DOMAIN_CODE,c.CATEGORY_NAME,d.DOMAIN_NAME FROM barred_msisdns bm,networks n ,sub_lookups SL,users U,users us,CATEGORIES c,DOMAINS d ");
        strBuff.append("WHERE bm.network_code=n.network_code AND SL.sub_lookup_code=BM.barred_type AND SL.lookup_type=? AND  U.user_id= ( CASE  WHEN bm.created_by in ('SYSTEM') THEN  'SU0001' else bm.created_by END) ");
        strBuff.append("AND us.MSISDN = bm.msisdn AND c.CATEGORY_CODE=us.CATEGORY_CODE AND c.DOMAIN_CODE=d.DOMAIN_CODE ");
        if (!BTSLUtil.isNullString(p_barredVO.getMsisdn())) {
            strBuff.append(" AND bm.msisdn=? AND us.status=? ");
        } else if(!BTSLUtil.isNullString(p_barredVO.getName())){
        	strBuff.append(" AND bm.msisdn IN (SELECT MSISDN FROM USERS WHERE USER_NAME=? ) AND us.status=? ");
        } else {
            strBuff.append(" AND bm.created_date >= ? AND bm.created_date <= ? ");
            strBuff.append(" AND bm.module=? ");
            strBuff.append(" AND bm.user_type = case ? when ? then bm.user_type else ? end ");
            strBuff.append(" AND bm.barred_type = case ? when ? then bm.barred_type else ? end  AND bm.network_code= ? ");
            strBuff.append(" AND us.USER_TYPE = case ? when ? then us.USER_TYPE else ? end ");
            strBuff.append(" AND us.CATEGORY_CODE=case ? when ? then us.CATEGORY_CODE else ? end AND us.STATUS = ? ");
        }
        strBuff.append(" ORDER BY bm.created_on DESC");
        
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());) {
            
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.BARRING_TYPE);

            if (!BTSLUtil.isNullString(p_barredVO.getMsisdn())) {
                pstmtSelect.setString(i++, p_barredVO.getMsisdn());
                pstmtSelect.setString(i++, PretupsI.YES);
            } else if(!BTSLUtil.isNullString(p_barredVO.getName())){
                pstmtSelect.setString(i++, p_barredVO.getName());
                pstmtSelect.setString(i++, PretupsI.YES);
            } else {
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_barredVO.getFromDate())));
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_barredVO.getToDate())));
                pstmtSelect.setString(i++, p_barredVO.getModule());
                pstmtSelect.setString(i++, p_barredVO.getBarredAs());
                pstmtSelect.setString(i++, PretupsI.ALL);
                pstmtSelect.setString(i++, p_barredVO.getBarredAs());
                pstmtSelect.setString(i++, p_barredVO.getBarredType());
                pstmtSelect.setString(i++, PretupsI.ALL);
                pstmtSelect.setString(i++, p_barredVO.getBarredType());
                pstmtSelect.setString(i++, p_barredVO.getNetworkCode());
               	pstmtSelect.setString(i++, p_barredVO.getUserType());
                pstmtSelect.setString(i++, PretupsI.ALL);
            	pstmtSelect.setString(i++, p_barredVO.getUserType());
               	pstmtSelect.setString(i++, p_barredVO.getCategoryCode());
                pstmtSelect.setString(i++, PretupsI.ALL);
            	pstmtSelect.setString(i++, p_barredVO.getCategoryCode());
            	pstmtSelect.setString(i++, PretupsI.YES);
            }

            String selectQuery = strBuff.toString();
            BarredVo barredUserVO = null;
            LookupsVO lookupVO = null;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query " + selectQuery);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                barredUserVO = new BarredVo();
                lookupVO = (LookupsVO) LookupsCache.getObject(PretupsI.MODULE_TYPE, rs.getString("module"));
                barredUserVO.setModuleName(lookupVO.getLookupName());
                barredUserVO.setNetworkName(rs.getString("network_name"));
                barredUserVO.setMsisdn(rs.getString("msisdn"));
                barredUserVO.setName(rs.getString("user_name"));
                barredUserVO.setUserType(rs.getString("user_type"));
                barredUserVO.setBarredType(rs.getString("barred_type"));
                barredUserVO.setCreatedOn(rs.getDate("created_on"));
                barredUserVO.setCreatedBy(rs.getString("created_by"));
                barredUserVO.setBarredDate(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on"))));
                barredUserVO.setBarredReason(rs.getString("barred_reason"));
                barredUserVO.setBarredTypeName(rs.getString("sub_lookup_name"));
                barredUserVO.setForMsisdn(rs.getString("for_msisdn"));
                barredUserVO.setBarredAs(rs.getString("barred_As"));
                barredUserVO.setDomainCode(rs.getString("domain_code"));
                barredUserVO.setDomainName(rs.getString("domain_name"));
                barredUserVO.setCategoryCode(rs.getString("category_code"));
                barredUserVO.setCategoryName(rs.getString("category_name"));
                barredUserVO.setModule(rs.getString("module"));
                barredUserVO.setLoginId(rs.getString("login_id"));
                barredList.add(barredUserVO);
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting size= " + barredList.size());
            }
        }
        return barredList;
    }
    
    /**
     * 
     * @param p_con
     * @param p_barredVO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList fetchBarredUserListAdmin(Connection p_con, BarredVo p_barredVO) throws BTSLBaseException {
        final String methodName = "fetchBarredUserListAdmin";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_barredVO " + p_barredVO);
        }

        ArrayList barredList = new ArrayList();
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT distinct bm.module,n.network_name,bm.msisdn,name,bm.user_type,bm.barred_type,bm.created_on,( CASE  WHEN  U.user_id = 'SU0001' THEN  N'SYSTEM' else U.user_name END) created_by, ");
        strBuff.append("bm.barred_reason,SL.sub_lookup_name,BM.for_msisdn FROM barred_msisdns bm,networks n ,sub_lookups SL,users U , users us ");
        strBuff.append("WHERE bm.network_code=n.network_code AND SL.sub_lookup_code=BM.barred_type AND SL.lookup_type=? AND  U.user_id= ( CASE  WHEN bm.created_by in ('SYSTEM') THEN  'SU0001' else bm.created_by END) ");
        if (!BTSLUtil.isNullString(p_barredVO.getMsisdn())) {
            strBuff.append(" AND bm.msisdn=? ");
        } else {
            strBuff.append(" AND bm.created_date >= ? AND bm.created_date <= ? ");
            strBuff.append(" AND bm.module=? ");
            strBuff.append(" AND bm.user_type = case ? when ? then bm.user_type else ? end ");
            strBuff.append(" AND bm.barred_type = case ? when ? then bm.barred_type else ? end  AND bm.network_code= ? ");
        }
        strBuff.append(" ORDER BY bm.created_on DESC");
        
        // Taking a map that contains the username based on msisdn to avoid duplicate dao calls in result set
        Map<String, String> userMap = new HashMap<String, String>();

        UserDAO userDao = new UserDAO();
        
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());) {
            
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.BARRING_TYPE);

            if (!BTSLUtil.isNullString(p_barredVO.getMsisdn())) {
                pstmtSelect.setString(i++, p_barredVO.getMsisdn());
            } else {
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_barredVO.getFromDate())));
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_barredVO.getToDate())));
                pstmtSelect.setString(i++, p_barredVO.getModule());
                pstmtSelect.setString(i++, p_barredVO.getBarredAs());
                pstmtSelect.setString(i++, PretupsI.ALL);
                pstmtSelect.setString(i++, p_barredVO.getBarredAs());
                pstmtSelect.setString(i++, p_barredVO.getBarredType());
                pstmtSelect.setString(i++, PretupsI.ALL);
                pstmtSelect.setString(i++, p_barredVO.getBarredType());
                pstmtSelect.setString(i++, p_barredVO.getNetworkCode());
            }

            String selectQuery = strBuff.toString();
            BarredVo barredUserVO = null;
            LookupsVO lookupVO = null;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query " + selectQuery);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                barredUserVO = new BarredVo();
                lookupVO = (LookupsVO) LookupsCache.getObject(PretupsI.MODULE_TYPE, rs.getString("module"));
                barredUserVO.setModuleName(lookupVO.getLookupName());
                barredUserVO.setNetworkName(rs.getString("network_name"));
                barredUserVO.setMsisdn(rs.getString("msisdn"));
                barredUserVO.setName(""); //overriding incase msisdn is not null in next if block
                if(!BTSLUtil.isNullString(rs.getString("msisdn"))){
                    if(!userMap.containsKey(rs.getString("msisdn"))){
                        userMap.put(rs.getString("msisdn"),
                                userDao.loadUserNamebyMsisdn(p_con, rs.getString("msisdn")));
                    }
                    barredUserVO.setName(userMap.get(rs.getString("msisdn")));
                }
                barredUserVO.setUserType(rs.getString("user_type"));
                barredUserVO.setBarredType(rs.getString("barred_type"));
                barredUserVO.setCreatedOn(rs.getDate("created_on"));
                barredUserVO.setCreatedBy(rs.getString("created_by"));
                barredUserVO.setBarredDate(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on"))));
                barredUserVO.setBarredReason(rs.getString("barred_reason"));
                barredUserVO.setBarredTypeName(rs.getString("sub_lookup_name"));
                barredUserVO.setForMsisdn(rs.getString("for_msisdn"));
                barredUserVO.setBarredAs(rs.getString("user_type"));
                barredUserVO.setModule(rs.getString("module"));
//                barredUserVO.setLoginId(rs.getString("login_id"));
                barredList.add(barredUserVO);
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredUserDAO[loadBarredUserList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting size= " + barredList.size());
            }
        }
        return barredList;
    }

}
