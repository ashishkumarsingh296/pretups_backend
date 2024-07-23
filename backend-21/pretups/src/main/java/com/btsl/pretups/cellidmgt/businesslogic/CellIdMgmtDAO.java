/*
 * @# CellIdMgmtDAO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Rajdeep Deb September 25, 2009 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2009 Comviva Technologies.
 */
package com.btsl.pretups.cellidmgt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.MessageResources;

// import oracle.jdbc.PreparedStatement;
import com.btsl.common.BTSLBaseException;
//import com.btsl.common.BTSLDispatchAction;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

/**
 * @author 
 *
 */
public class CellIdMgmtDAO  {

	 private static final Log log = LogFactory.getLog(CellIdMgmtDAO.class.getName());

    /*
     * this method inserts the new cell group and returns the number of rows
     * inserted
     * 
     * @param pCon
     * 
     * @param pCellidmgmtvo
     * 
     * @return addCount
     * 
     * @author Ashish T
     */
    /**
     * @param pCon
     * @param pCellidmgmtvo
     * @return addCount
     * @throws BTSLBaseException
     */
    public int addCellGroup(Connection pCon, CellIdVO pCellidmgmtvo) throws BTSLBaseException {

        final String methodName = "addCellGroup";

        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:pCellidmgmtvo=");
        	loggerValue.append(pCellidmgmtvo);
        	log.debug(methodName,loggerValue);
        
        }
         
        Date date = new Date();
        int addCount = 0;
        try {
            boolean isnameexist = isGroupNameExist(pCon, pCellidmgmtvo.getGroupName(), PretupsI.CELL_ID_MAPPING_ADD, pCellidmgmtvo.getGroupId());
            boolean iscodeexist = isGroupCodeExist(pCon, pCellidmgmtvo.getGroupCode(), PretupsI.CELL_ID_MAPPING_ADD, pCellidmgmtvo.getGroupId());
            if (isnameexist) {
                return -2;
            }
            if (iscodeexist) {
                return -3;
            }

            if ((!isnameexist) && (!iscodeexist)) {
                StringBuilder insertQuery = new StringBuilder();
                insertQuery.append("INSERT INTO cell_groups(group_id, group_name, group_code,");
                insertQuery.append("status, created_on, created_by,modified_by,network_code) ");
                insertQuery.append("VALUES(?,?,?,?,?,?,?,?)");
                String query = insertQuery.toString();
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Query=" + query);
                }
               try(PreparedStatement pstmtInsert = (PreparedStatement) pCon.prepareStatement(query);)
               {
                pstmtInsert.setString(1, pCellidmgmtvo.getGroupId());
                pstmtInsert.setString(2, pCellidmgmtvo.getGroupName());
                pstmtInsert.setString(3, pCellidmgmtvo.getGroupCode());
                pstmtInsert.setString(4, pCellidmgmtvo.getStatus());
                pstmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(date));
                pstmtInsert.setString(6, pCellidmgmtvo.getCreatedBy());
//                pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(date));
                pstmtInsert.setString(7, pCellidmgmtvo.getModifiedBy());
                pstmtInsert.setString(8, pCellidmgmtvo.getNetworkCode());

                addCount = pstmtInsert.executeUpdate();

            }
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[addCellGroup]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[addCellGroup]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:return=" );
            	loggerValue.append(addCount);
            	log.debug(methodName,loggerValue);
            
            }
        }
        return addCount;
    }

    /*
     * this method modify an existing cell group in the system
     * 
     * @param pCon
     * 
     * @param pCellidmgmtvo
     * 
     * @return modifyCount
     * 
     * @author Ashish T
     */
    /**
     * @param pCon
     * @param pCellidmgmtvo
     * @return
     * @throws BTSLBaseException
     */
    public int modifyCellGroup(Connection pCon, CellIdVO pCellidmgmtvo) throws BTSLBaseException {
        final String methodName = "modifyCellGroup";
        StringBuilder str=new StringBuilder();
        if (log.isDebugEnabled()) {
        	str.setLength(0);
        	str.append( "Entered:pCellidmgmtvo=");
        	str.append( pCellidmgmtvo );
            log.debug(methodName, str );
        }
         
        int modifyCount = 0;
        Date date = new Date();
        try {
            boolean isnameexist = isGroupNameExist(pCon, pCellidmgmtvo.getGroupName(), PretupsI.CELL_ID_MAPPING_MODIFY, pCellidmgmtvo.getGroupId());
            boolean iscodeexist = isGroupCodeExist(pCon, pCellidmgmtvo.getGroupCode(), PretupsI.CELL_ID_MAPPING_MODIFY, pCellidmgmtvo.getGroupId());
            if (isnameexist) {
                return -2;
            }
            if (iscodeexist) {
                return -3;
            }
            if ((!isnameexist) && (!iscodeexist)) {
                StringBuilder insertQuery = new StringBuilder();
                insertQuery.append("update cell_groups set group_name=? , group_code=? , status=? ,");
                insertQuery.append("modified_on=? , modified_by=?  where group_id=? ");
                String query = insertQuery.toString();
                if (log.isDebugEnabled()) {
                	str.setLength(0);
                	str.append("Query=");
                	str.append(query);
                    log.debug(methodName, str );
                }
                try(PreparedStatement pstmtInsert =  pCon.prepareStatement(query);)
                {
                pstmtInsert.setString(1, pCellidmgmtvo.getGroupName());
                pstmtInsert.setString(2, pCellidmgmtvo.getGroupCode());
                pstmtInsert.setString(3, pCellidmgmtvo.getStatus());
                pstmtInsert.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                pstmtInsert.setString(5, pCellidmgmtvo.getModifiedBy());
                pstmtInsert.setString(6, pCellidmgmtvo.getGroupId());

                modifyCount = pstmtInsert.executeUpdate();
            }
            }

        } catch (SQLException sqe) {
        	str.setLength(0);
        	str.append("SQLException:");
        	str.append(sqe.getMessage());
            log.error(methodName,  str);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[modifyCellGroup]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	str.setLength(0);
        	str.append("Exception:");
        	str.append(e.getMessage());
            log.error(methodName, str  );
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[modifyCellGroup]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
           
            if (log.isDebugEnabled()) {
            	str.setLength(0);
            	str.append("Exiting:return=");
            	str.append(modifyCount);
               
                log.debug(methodName,  str);
            }
        }
        return modifyCount;
    }

    /*
     * this method deletes an existing cell group in the system
     * 
     * @param pCon
     * 
     * @param pCellidmgmtvo
     * 
     * @return deleteCount
     * 
     * @author Ashish T
     */
    /**
     * @param pCon
     * @param pCellidmgmtvo
     * @return
     * @throws BTSLBaseException
     */
    public int deleteCellGroup(Connection pCon, CellIdVO pCellidmgmtvo) throws BTSLBaseException {
        final String methodName = "deleteCellGroup";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:pCellidmgmtvo=" + pCellidmgmtvo);
        }
       
        int deleteCount = 0;
        boolean iscellgroupactive = isCellGroupExist(pCon, pCellidmgmtvo.getGroupId());
        try {
            if (iscellgroupactive) {
                return -2;
            }
            if (!iscellgroupactive) {
                StringBuilder insertQuery = new StringBuilder();
                insertQuery.append("UPDATE cell_groups SET status = 'N' WHERE group_id=?");
                String query = insertQuery.toString();
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Query=" + query);
                }
                try( PreparedStatement pstmtInsert =  pCon.prepareStatement(query);)
                {
                pstmtInsert.setString(1, pCellidmgmtvo.getGroupId());
                deleteCount = pstmtInsert.executeUpdate();
            }
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[deleteCellGroup]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[deleteCellGroup]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:return=" + deleteCount);
            }
        }
        return deleteCount;
    }

    /*
     * this method check the association of a cell group to any cell id
     * 
     * @param pCon
     * 
     * @param pGroupid
     * 
     * @return isExist
     * 
     * @author Ashish T
     */
    /**
     * @param pCon
     * @param pGroupid
     * @return isExist
     * @throws BTSLBaseException
     */
    public boolean isCellGroupExist(Connection pCon, String pGroupid) throws BTSLBaseException {
        final String methodName = "isCellGroupExist";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:p_grphDomainCode=" + pGroupid);
        }
        
        boolean isExist = false;
        StringBuilder sqlRecordExist = new StringBuilder();
        try {
            sqlRecordExist.append("SELECT 1 FROM  CELL_IDS ");
            sqlRecordExist.append("WHERE group_id=? AND status <> 'N'");
           try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlRecordExist.toString());)
           {
            pstmtSelect.setString(1, pGroupid);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
                isExist = true;
            }
        }
           }
        }// end of try
        catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("isCellGroupActive", "Exititng:isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }

    /*
     * this method checks the existence of the group name in the system
     * 
     * @param pCon
     * 
     * @param pGroupName
     * 
     * @return isExist
     * 
     * @author Ashish T
     */
    /**
     * @param pCon
     * @param pGroupName
     * @param optmode
     * @param pGroupid
     * @return isExist
     * @throws BTSLBaseException
     */
    public boolean isGroupNameExist(Connection pCon, String pGroupName, String optmode, String pGroupid) throws BTSLBaseException {
        final String methodName = "isGroupNameExist";
        StringBuilder str=new StringBuilder();
        if (log.isDebugEnabled()) {
        	str.setLength(0);
        	str.append("Entered:pGroupName=");
        	str.append(pGroupName);
            log.debug(methodName, str );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs1 = null;
        boolean isExist = false;
        StringBuilder sqlRecordExist = new StringBuilder();
        try {
            if (PretupsI.CELL_ID_MAPPING_ADD.equals(optmode)) {
                sqlRecordExist.append("SELECT 1 FROM  cell_groups ");
                sqlRecordExist.append("WHERE group_name=? ");
                sqlRecordExist.append("AND status <> 'N'");
                if (log.isDebugEnabled()) {
                	str.setLength(0);
                	str.append("QUERY=");
                	str.append(sqlRecordExist);
                    log.debug(methodName,  str );
                }
                pstmtSelect = pCon.prepareStatement(sqlRecordExist.toString());
                pstmtSelect.setString(1, pGroupName);
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    isExist = true;
                }
            }
            if (PretupsI.CELL_ID_MAPPING_MODIFY.equals(optmode)) {
                sqlRecordExist.append("SELECT 1 FROM  cell_groups ");
                sqlRecordExist.append("WHERE group_name=? ");
                sqlRecordExist.append("AND status <> 'N' AND group_id <> ?");
                if (log.isDebugEnabled()) {
                	str.setLength(0);
                	str.append("QUERY=");
                	str.append(sqlRecordExist);
                    log.debug(methodName,  str );
                   
                }
                pstmtSelect1 = pCon.prepareStatement(sqlRecordExist.toString());
                pstmtSelect1.setString(1, pGroupName);
                pstmtSelect1.setString(2, pGroupid);
                rs1 = pstmtSelect1.executeQuery();
                if (rs1.next()) {
                    isExist = true;
                }
            }
            
        }// end of try
        catch (SQLException sqe) {
        	str.setLength(0);
        	str.append("SQLException:");
        	str.append(sqe.getMessage());
            log.error(methodName,  str );
            log.errorTrace(methodName, sqe);
        }// end of catch
        catch (Exception e) {
        	str.setLength(0);
        	str.append("Exception:");
        	str.append(e.getMessage());
            log.error(methodName,  str );
            log.errorTrace(methodName, e);
        } finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (rs1!= null){
                	rs1.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmtSelect1!= null){
                	pstmtSelect1.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
            	str.setLength(0);
            	str.append("Exititng:isExist=");
            	str.append(isExist);
                log.debug(methodName,  str );
            }
        }// end of finally
        return isExist;
    }

    /*
     * this method is to check the existence of the group code in the system
     * 
     * @param pCon
     * 
     * @param pGroupCode
     * 
     * @return isExist
     * 
     * @author Ashish T
     */
    /**
     * @param pCon
     * @param pGroupCode
     * @param optmode
     * @param pGroupid
     * @return isExist
     * @throws BTSLBaseException
     */
    public boolean isGroupCodeExist(Connection pCon, String pGroupCode, String optmode, String pGroupid) throws BTSLBaseException {
        final String methodName = "isGroupCodeExist";
        StringBuilder str=new StringBuilder();
        if (log.isDebugEnabled()) {
        	str.setLength(0);
        	str.append("Entered:pGroupCode=");
        	str.append(pGroupCode);
            log.debug(methodName,  str);
        }
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        boolean isExist = false;
        StringBuilder sqlRecordExist = new StringBuilder();
        try {
            if (PretupsI.CELL_ID_MAPPING_ADD.equals(optmode)) {
                sqlRecordExist.append("SELECT 1 FROM  cell_groups ");
                sqlRecordExist.append("WHERE group_code=? ");
                sqlRecordExist.append("AND status <> 'N'");

                if (log.isDebugEnabled()) {
                	str.setLength(0);
                	str.append("QUERY=" );
                	str.append(sqlRecordExist);
                    log.debug(methodName,  str);
                   
                }

                pstmtSelect = pCon.prepareStatement(sqlRecordExist.toString());
                pstmtSelect.setString(1, pGroupCode);
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    isExist = true;
                }
            }
            if (PretupsI.CELL_ID_MAPPING_MODIFY.equals(optmode)) {
                sqlRecordExist.append("SELECT 1 FROM  cell_groups ");
                sqlRecordExist.append("WHERE group_code=? ");
                sqlRecordExist.append("AND status <> 'N' AND group_id <> ?");

                if (log.isDebugEnabled()) {
                	str.setLength(0);
                	str.append("QUERY=" );
                	str.append(sqlRecordExist);
                    log.debug(methodName,  str);
                   
                }

                pstmtSelect1 = pCon.prepareStatement(sqlRecordExist.toString());
                pstmtSelect1.setString(1, pGroupCode);
                pstmtSelect1.setString(2, pGroupid);
                rs1 = pstmtSelect1.executeQuery();
                if (rs1.next()) {
                    isExist = true;
                }
            }
        }// end of try
        catch (SQLException sqe) {
        	str.setLength(0);
        	str.append("SQLException:");
        	str.append(sqe.getMessage());
            log.error(methodName,  str);
            log.errorTrace(methodName, sqe);
        }// end of catch
        catch (Exception e) {
        	str.setLength(0);
        	str.append("Exception:");
        	str.append(e.getMessage());
            log.error(methodName,  str);
            log.errorTrace(methodName, e);
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (rs1!= null){
        			rs1.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmtSelect1!= null){
                	pstmtSelect1.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
            	str.setLength(0);
            	str.append("Exititng:isExist=");
            	str.append(isExist);
                log.debug(methodName,  str);
               
            }
        }// end of finally
        return isExist;
    }

    /*
     * this method loads the cell group list ..
     * 
     * @param pCon
     * 
     * @return cellGroupList
     * 
     * @author Ashsih T
     */
    /**
     * @param pCon
     * @param p_networkCode
     * @return cellGroupList
     * @throws BTSLBaseException
     */
    public ArrayList getCellGroupList(Connection pCon, String p_networkCode) throws BTSLBaseException {
        final String methodName = "getCellGroupList";
        StringBuilder str=new StringBuilder();
        if (log.isDebugEnabled()) {
        	str.setLength(0);
        	str.append("Entered:");
        	log.debug(methodName,str);
           
        }
        ArrayList cellGroupList = new ArrayList();
        
        CellIdVO cellmgmtVO = null;
      
        try {
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT group_id,group_name, group_code,status,");
            selectQuery.append("created_on,created_by,modified_on,modified_by,network_code");
            selectQuery.append(" FROM cell_groups");
            selectQuery.append(" WHERE status <> 'N' AND network_code=? order by UPPER(group_name)");
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query=" + selectQuery);
            }
           try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery.toString());)
           {
            pstmtSelect.setString(1, p_networkCode);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                cellmgmtVO = new CellIdVO();
                cellmgmtVO.setGroupId(rs.getString("group_id"));
                cellmgmtVO.setGroupName(rs.getString("group_name"));
                cellmgmtVO.setGroupCode(rs.getString("group_code"));
                cellmgmtVO.setStatus(rs.getString("status"));
                if (PretupsI.STATUS_ACTIVE.equals(rs.getString("status"))) {
                    cellmgmtVO.setStatusDescription(PretupsI.CELL_ACTIVE_STATUS);
                }
                if (PretupsI.STATUS_SUSPEND.equals(rs.getString("status"))) {
                    cellmgmtVO.setStatusDescription(PretupsI.CELL_SUSPEND_STATUS);
                }
                cellmgmtVO.setCreatedOn(BTSLUtil.getDateStringFromDate(rs.getDate("created_on")));
                cellmgmtVO.setCreatedBy(rs.getString("created_by"));
                if (!BTSLUtil.isNullObject(rs.getDate("modified_on")))
                    cellmgmtVO.setModifiedOn(BTSLUtil.getDateStringFromDate(rs.getDate("modified_on")));
                if (!BTSLUtil.isNullString(rs.getString("modified_by")))
                    cellmgmtVO.setModifiedBy(rs.getString("modified_by"));
                cellmgmtVO.setNetworkCode(rs.getString("network_code"));
                //
                cellmgmtVO.setRadioIndex("0");
                cellGroupList.add(cellmgmtVO);
            }
        }
           }
        }catch (SQLException sqe) {
        	str.setLength(0);
        	str.append("SQLException:");
        	str.append(sqe.getMessage());
            log.error(methodName,  str);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[getCellGroupList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	str.setLength(0);
        	str.append("Exception:");
        	str.append( e.getMessage());
            log.error(methodName,  str);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[getCellGroupList]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
            	str.setLength(0);
            	str.append("loadCellGroupList" );
            	str.append("Exiting:list size=");
            	str.append(cellGroupList.size());
		        log.debug(methodName,str);
               
            }
        }

        return cellGroupList;
    }

   

    /**
     * Method :loadCellidDeatilsVOList
     * This method check the data base validation
     * and after validation insert into channel user related tables.
     * 
     * @param pCon
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Rajdeep
     */

    /**
     * @param pCon
     * @param p_networkCode
     * @return detailsList
     * @throws BTSLBaseException
     */
    public ArrayList loadCellidDeatilsVOList(Connection pCon, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadCellidDeatilsVOList ";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered ");
        }
        ArrayList detailsList = null;
         
        CellIdVO cellVO = null;
        try {
            StringBuilder strBuff = new StringBuilder("SELECT cid.GROUP_ID,cid.CELL_ID,cg.GROUP_NAME,cid.STATUS,cid.CREATED_BY,cid.CREATED_ON,cid.MODIFIED_BY,cid.MODIFIED_ON,cid.FILE_NAME,cid.site_id,cid.site_name ");
            strBuff.append("FROM cell_ids cid,cell_groups cg ");
            strBuff.append("WHERE cid.network_code=? AND cid.GROUP_ID=cg.GROUP_ID AND cid.STATUS <> 'N' ORDER BY cg.GROUP_ID");
            if (log.isDebugEnabled()) {
                log.debug(methodName, " SQL Query " + strBuff.toString());
            }
            try(PreparedStatement pstmt = pCon.prepareStatement(strBuff.toString());)
            {
            pstmt.setString(1, p_networkCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            detailsList = new ArrayList();
            while (rs.next()) {
                cellVO = new CellIdVO();
                cellVO.setGroupId(SqlParameterEncoder.encodeParams(rs.getString("group_id")));
                cellVO.setCellId(SqlParameterEncoder.encodeParams(rs.getString("cell_id")));
                cellVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                cellVO.setCreatedBy(SqlParameterEncoder.encodeParams(rs.getString("created_by")));
                cellVO.setCreatedOn(BTSLUtil.getDateStringFromDate(rs.getDate("created_on")));
                if (rs.getString("file_name") != null && rs.getDate("modified_on") != null) {
                    cellVO.setModifiedBy(SqlParameterEncoder.encodeParams(rs.getString("modified_by")));
                    cellVO.setModifiedOn(BTSLUtil.getDateStringFromDate(rs.getDate("modified_on")));
                }
                cellVO.setFileName(SqlParameterEncoder.encodeParams(rs.getString("file_name")));
                cellVO.setSiteId(SqlParameterEncoder.encodeParams(rs.getString("site_id")));
                cellVO.setSiteName(SqlParameterEncoder.encodeParams(rs.getString("site_name")));

                detailsList.add(cellVO);
            }
        }
            }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        } finally {
        	log.debug(methodName,"inside finally");
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Exiting " + detailsList);
        }
        return detailsList;
    }

    /**
     * @param pCon
     * @param pCellId
     * @return cellGroup
     * @throws BTSLBaseException
     */
    /**
     * @param pCon
     * @param pCellId
     * @return
     * @throws BTSLBaseException
     */
    public String getCellGroupFromCellId(Connection pCon, String pCellId) throws BTSLBaseException {
        final String methodName = "getCellGroupFromCellId";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pCellId=");
        	loggerValue.append(pCellId);
            log.debug(methodName, loggerValue);
        }
         
      
        String cellGroup = null;
        StringBuilder sqlStmt = new StringBuilder();
        try {
            sqlStmt.append("SELECT cg.group_id FROM  CELL_IDS ci, CELL_GROUPS cg ");
            sqlStmt.append("WHERE ci.group_id= cg.group_id and ci.cell_id=? AND ci.status=? and cg.status=?");
            try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlStmt.toString());)
            {
            pstmtSelect.setString(1, pCellId);
            pstmtSelect.setString(2, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(3, PretupsI.STATUS_ACTIVE);

            try( ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                cellGroup = rs.getString("group_id");
            }
        }
            
            }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException:");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[getCellGroupFromCellId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[getCellGroupFromCellId]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exititng: cellGroup=");
            	loggerValue.append(cellGroup);
            
                log.debug(methodName,  loggerValue );
            }
        }
        return cellGroup;
    }

    /**
     * @author nand.sahu
     * @param pCon
     * @param p_cellVOLstMap
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    /**
     * @param con
     * @param cellVOLstMap
     * @param locale
     * @param fileName
     * @return errorList
     * @throws BTSLBaseException
     */
    public ArrayList reAssociateCellGroupIdWithCellId(Connection con, HashMap cellVOLstMap, Locale locale, String fileName) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("reAssociateCellGroupIdWithCellId ", " Entered ");
        }

        ArrayList errorList = null;
        ArrayList tempErrorList = null;
        ArrayList modifyCellIdStatusList = null;
        ArrayList reAssociateCellGroupIdList = null;
        try {
            if (cellVOLstMap.containsKey(PretupsI.CELL_ID_MODIFY_STATUS)) {
                modifyCellIdStatusList = (ArrayList) cellVOLstMap.get(PretupsI.CELL_ID_MODIFY_STATUS);
            }
            if (cellVOLstMap.containsKey(PretupsI.CELL_ID_REASSOCIATE_CELLGRPID)) {
                reAssociateCellGroupIdList = (ArrayList) cellVOLstMap.get(PretupsI.CELL_ID_REASSOCIATE_CELLGRPID);
            }
            tempErrorList = new ArrayList();
            errorList = new ArrayList();
            if (modifyCellIdStatusList != null && !modifyCellIdStatusList.isEmpty()) {
                tempErrorList = this.modifyCellIdStatus(con, modifyCellIdStatusList, locale, fileName);
                if (!(tempErrorList == null) && !tempErrorList.isEmpty()) {
                    errorList.addAll(tempErrorList);
                    tempErrorList = null;
                }
            }
            if (reAssociateCellGroupIdList != null && !reAssociateCellGroupIdList.isEmpty()) {
                tempErrorList = this.reAssociateCellGroupId(con, reAssociateCellGroupIdList, locale, fileName);
                if (!(tempErrorList == null) && !tempErrorList.isEmpty()) {
                    errorList.addAll(tempErrorList);
                    tempErrorList = null;
                }
            }

        } catch (Exception e) {
            final String methodName = "reAssociateCellGroupIdWithCellId";
            log.error(methodName, "Errors" + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }
        if (log.isDebugEnabled()) {
            log.debug("reAssociateCellGroupIdWithCellId ", "Exiting ");
        }
        return errorList;
    }

    /**
     * @author nand.sahu
     * @param pCon
     * @param p_modifyCellIdStatusList
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    /**
     * @param con
     * @param modifyCellIdStatusList
     * @param locale
     * @param fileName
     * @return errorList
     * @throws BTSLBaseException
     */
    private ArrayList modifyCellIdStatus(Connection con, ArrayList modifyCellIdStatusList, Locale locale, String fileName) throws BTSLBaseException {
        final String methodName = "modifyCellIdStatus ";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered modifyCellIdStatusList size = " + modifyCellIdStatusList.size());
        }
        ArrayList errorList = new ArrayList();
        
        CellIdVO cellIdVO = null;
        ListValueVO errorVO = null;
        int modStatusCount = 0;
        Date date = new Date();
        StringBuilder modStatusQuery = new StringBuilder("UPDATE cell_ids CID SET status=?, modified_on=?,modified_by=?, file_name=? WHERE CID.cell_id=? AND CID.group_id=? AND CID.status=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Modify Status Query  =  " + modStatusQuery);
        }
        try(PreparedStatement pstmt = con.prepareStatement(modStatusQuery.toString());) {
            
            int modifyCellIdStatusLists=modifyCellIdStatusList.size();
            for (int i = 0; i < modifyCellIdStatusLists; i++) {
                cellIdVO = (CellIdVO) modifyCellIdStatusList.get(i);
                pstmt.setString(1, cellIdVO.getModstatus());
                pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                pstmt.setString(3, cellIdVO.getModifiedBy());
                pstmt.setString(4, fileName);
                pstmt.setString(5, cellIdVO.getCellId());
                pstmt.setString(6, cellIdVO.getGroupId());
                pstmt.setString(7, cellIdVO.getStatus());

                int update = pstmt.executeUpdate();
                if (update > 0) {
                    modStatusCount++;
                } else {
                    errorVO = new ListValueVO("",cellIdVO.getRecordNumber() , RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.REASSOCIATE_NO_MATCH_FOUND,null));
                    errorList.add(errorVO);
                    continue;
                }
            }
            // Commit the DB if any statement updated into DB.
            if (modStatusCount > 0) {
                con.commit();
            }
        } catch (SQLException sqe) {
            log.error(methodName, "Cell Id status could not modified " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, "modifyCellIdStatus", "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Cell Id status could not modified " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, "modifyCellIdStatus", "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting " + modStatusCount);
            }
        }
        return errorList;
    }

    /**
     * @param con
     * @param reAssociateCellGroupIdList
     * @param locale
     * @return
     * @throws BTSLBaseException
     */
    private ArrayList reAssociateCellGroupId(Connection con, ArrayList reAssociateCellGroupIdList, Locale locale, String fileName) throws BTSLBaseException {
        final String methodName = "reAssociateCellGroupId ";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered reAssociateCellGroupIdList size = " + reAssociateCellGroupIdList.size());
        }
        ArrayList errorList = new ArrayList();
        
        CellIdVO cellIdVO = null;
        ListValueVO errorVO = null;
        int reAssociateCount = 0;
        Date date = new Date();
        StringBuilder modStatusQuery = new StringBuilder("UPDATE cell_ids CID SET group_id=?, modified_on=?,modified_by=?, file_name=? WHERE CID.cell_id=? AND CID.group_id=? AND CID.status<>'N'");
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Modify Status Query  =  " + modStatusQuery);
        }
        try( PreparedStatement pstmt = con.prepareStatement(modStatusQuery.toString());) {
           
            int reAssociateCellGroupIdLists=reAssociateCellGroupIdList.size();
            for (int i = 0; i <reAssociateCellGroupIdLists ; i++) {
                cellIdVO = (CellIdVO) reAssociateCellGroupIdList.get(i);
                pstmt.setString(1, cellIdVO.getNewGroupId());
                pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                pstmt.setString(3, cellIdVO.getModifiedBy());
                pstmt.setString(4, fileName);
                pstmt.setString(5, cellIdVO.getCellId());
                pstmt.setString(6, cellIdVO.getGroupId());

                int update = pstmt.executeUpdate();
                if (update > 0) {
                    reAssociateCount++;
                } else {
                    errorVO = new ListValueVO("",  cellIdVO.getRecordNumber() , RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.REASSOCIATE_NO_MATCH_FOUND,null));
                    errorList.add(errorVO);
                    continue;
                }
            }
            // Commit the DB if any statement updated into DB.
            if (reAssociateCount > 0) {
                con.commit();
            }
        } catch (SQLException sqe) {
            log.error(methodName, "Cell Id could not re-associated " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Cell Id could not re-associated " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting " + reAssociateCount);
            }
        }
        return errorList;
    }
    
    
    /**
     * @param pCon
     * @param p_networkCode
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadCellGroupID(Connection pCon, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadCellidDeatilsVOList ";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered ");
        }
        ArrayList detailsList = null;
         
        ListValueVO listValueVO = null;
        try {
            StringBuilder strBuff = new StringBuilder("SELECT cid.GROUP_ID,cid.CELL_ID,cg.GROUP_NAME,cid.STATUS,cid.CREATED_BY,cid.CREATED_ON,cid.MODIFIED_BY,cid.MODIFIED_ON,cid.FILE_NAME,cid.site_id,cid.site_name ");
            strBuff.append("FROM cell_ids cid,cell_groups cg ");
            strBuff.append("WHERE cid.network_code=? AND cid.GROUP_ID=cg.GROUP_ID AND cid.STATUS <> 'N' ORDER BY cg.GROUP_ID");
            if (log.isDebugEnabled()) {
                log.debug(methodName, " SQL Query " + strBuff.toString());
            }
            try(PreparedStatement pstmt = pCon.prepareStatement(strBuff.toString());)
            {
            pstmt.setString(1, p_networkCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            detailsList = new ArrayList();
            while (rs.next()) {

                listValueVO =new ListValueVO (rs.getString("GROUP_NAME"),rs.getString("group_id"));
                detailsList.add(listValueVO);
            }
        }
            }
        }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        } finally {
        	log.debug(methodName,"inside finally");
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Exiting " + detailsList);
        }
        return detailsList;
    }

    /**
     * @param pCon
     * @param pCellIdVOList
     * @param p_messages
     * @param p_locale
     * @param p_userVO
     * @param p_fileName
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList addCellGroupAndCellIdMapping(Connection con, ArrayList cellIdVOList, Locale locale, UserVO userVO, String fileName) throws BTSLBaseException {
        final  String methodName="addCellGroupAndCellIdMappingAngular";
        StringBuilder str=new StringBuilder();
        if (log.isDebugEnabled()) {
            str.setLength(0);
            str.append("addCellGroupAndCellIdMappingAngular ");
            str.append( " Entered ");
            str.append( fileName);
            log.debug(methodName,str);

        }
        ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        int commitCounter = 0;


        // cell id validation of uniqueness
        StringBuilder selectCellIdDuplicate = new StringBuilder("SELECT 1 from cell_ids CID ");
        selectCellIdDuplicate.append(" WHERE CID.cell_id = ? AND CID.status <> 'N'");

        // Insert into the database
        StringBuilder insertCellIdTable = new StringBuilder("INSERT INTO cell_ids (group_id, cell_id, status, created_on,");
        insertCellIdTable.append("created_by, network_code, file_name,site_id, site_name) VALUES(?,?,?,?,?,?,?,?,?)");

        if (log.isDebugEnabled()) {
            str.setLength(0);
            str.append("SelectCellID Query =");
            str.append(selectCellIdDuplicate);
            log.debug(methodName,str);
            str.setLength(0);
            str.append("InsertCellID Query =");
            str.append(insertCellIdTable);
            log.debug(methodName,str);
        }
        try(PreparedStatement pstmtSelectCellID = con.prepareStatement(selectCellIdDuplicate.toString());PreparedStatement pstmtInsertCellID = con.prepareStatement(insertCellIdTable.toString());) {


            ListSorterUtil sort = new ListSorterUtil();
            cellIdVOList = (ArrayList) sort.doSort("cellId", null, cellIdVOList);
            CellIdVO cellIdVO = null;

            for (int i = 0, length = cellIdVOList.size(); i < length; i++) {
                cellIdVO = (CellIdVO) cellIdVOList.get(i);
                Date date = new Date();
                pstmtSelectCellID.setString(1, cellIdVO.getCellId());
                try( ResultSet rsCellID = pstmtSelectCellID.executeQuery();)
                {
                    if (rsCellID.next()) {
                        errorVO = new ListValueVO("", cellIdVO.getRecordNumber(), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_ALREADY_MAPPED, new String[] { cellIdVO.getCellId() }));
                        errorList.add(errorVO);
                        continue;
                    }
                    pstmtInsertCellID.clearParameters();
                    pstmtInsertCellID.setString(1, cellIdVO.getGroupId());
                    pstmtInsertCellID.setString(2, cellIdVO.getCellId());
                    pstmtInsertCellID.setString(3, cellIdVO.getStatus());
                    pstmtInsertCellID.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                    pstmtInsertCellID.setString(5, userVO.getActiveUserID());
                    pstmtInsertCellID.setString(6, userVO.getNetworkID());
                    pstmtInsertCellID.setString(7, cellIdVO.getFileName());
                    pstmtInsertCellID.setString(8, cellIdVO.getSiteId());
                    pstmtInsertCellID.setString(9, cellIdVO.getSiteName());
                    if (pstmtInsertCellID.executeUpdate() > 0) {
                        con.commit();
                        commitCounter++;
                        continue;
                    } else {
                        con.rollback();
                        continue;
                    }
                }
            }
        }catch (SQLException sqe) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[addCellGroupAndCellIdMappingAngular]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CellIdMgmtDAO[addCellGroupAndCellIdMappingAngular]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            log.debug(methodName, "Exiting..");

        }
        if (log.isDebugEnabled()) {
            str.setLength(0);
            str.append("addCellGroupAndCellIdMapping ");
            str.append(" Exiting count for inserted cell id mapping = ");
            str.append(commitCounter);
            log.debug(methodName,str);
        }
        return errorList;
    }




}
