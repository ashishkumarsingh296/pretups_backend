/**
 * @(#)ScheduledBatchesDAO.java
 *                              Name Date History
 *                              ------------------------------------------------
 *                              ------------------------
 *                              Ashish Kumar 22/04/2006 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------
 *                              Copyright (c) 2006 Bharti Telesoft Ltd. This
 *                              class used to implement
 *                              the process related business logics.
 */
package com.btsl.pretups.processes.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
// commented for DB2
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class ProcessStatusDAO {
    private static final Log _log = LogFactory.getLog(ProcessStatusDAO.class.getName());

    public ProcessStatusDAO() {

    }

    /**
     * This method is used to load the Process Status detail(processID,date-time
     * and status) from PROCESS_STATUS
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_processID
     * @throws BTSLBaseException
     * @return ProcessStatusVO processStatusVO
     */
    public ProcessStatusVO loadProcessDetail(Connection p_con, String p_processID) throws BTSLBaseException {
        final String METHOD_NAME = "loadProcessDetail";
        if (_log.isDebugEnabled())
            _log.debug("loadProcessDetail", "Entered p_processID: " + p_processID);
        
         
        ProcessStatusVO processStatusVO = null;
        String sqlSelect = "SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval,network_code FROM process_status WHERE process_id=?";
        if (_log.isDebugEnabled())
            _log.debug("loadProcessDetail", "QUERY sqlSelect:" + sqlSelect);
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);){
            
            pstmtSelect.setString(1, p_processID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                processStatusVO = new ProcessStatusVO();
                processStatusVO.setProcessID(rs.getString("process_id"));
                processStatusVO.setStartDate(rs.getTimestamp("start_date"));
                processStatusVO.setProcessStatus(rs.getString("scheduler_status"));

                if (rs.getDate("executed_upto") != null)
                    processStatusVO.setExecutedUpto(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("executed_upto")));
                processStatusVO.setExecutedOn(rs.getDate("executed_on"));
                processStatusVO.setExpiryTime(rs.getLong("expiry_time"));
                processStatusVO.setBeforeInterval(rs.getLong("before_interval"));
                processStatusVO.setNetworkCode(rs.getString("network_code"));
            }
        } 
        }catch (SQLException sqe) {
            _log.error("loadProcessDetail", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[loadProcessDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "loadProcessDetail", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadProcessDetail", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[loadProcessDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "loadProcessDetail", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug("loadProcessDetail", "Exiting processStatusVO=" + processStatusVO);
        }
        return processStatusVO;
    }

    /**
     * This method is used to update the scheduler start date and status
     * 
     * @param Connection
     *            p_con
     * @param ProcessStatusVO
     *            p_processStatusVO
     * @return int updateCount
     * @throws BTSLBaseException
     */
    public int updateProcessDetail(Connection p_con, ProcessStatusVO p_processStatusVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateProcessDetail";
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetail Entered ", "p_processStatusVO=" + p_processStatusVO + " con is  " + p_con);
        int updateCount = 0;
        
        String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=?,executed_upto=?,executed_on=? WHERE process_id=? ";
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetail", "QUERY sqlUpdate=" + sqlUpdate);
        try ( PreparedStatement pstmtUpdate = p_con.prepareStatement(sqlUpdate);){
           
            int i = 1;
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getExecutedUpto()));
            pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedOn()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessID());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("updateProcessDetail", "SQLException : " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetail]", "", "", "", "SQL Exception while updating the process_status table" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetail", "Error in updating the process_status table");
        } catch (Exception e) {
            _log.error("updateProcessDetail", "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetail]", "", "", "", "Exception while updating the process_status table" + e.getMessage());
            throw new BTSLBaseException(this, "updateProcessDetail", "Error in updating the process_status table");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug("updateProcessDetail", "Exiting updateCount:" + updateCount);
        }
        return updateCount;
    }

    /**
     * This method is used to update the scheduler start date and status for MIS
     * 
     * @param Connection
     *            p_con
     * @param ProcessStatusVO
     *            p_processStatusVO
     * @return int updateCount
     * @throws BTSLBaseException
     */
    public int updateProcessDetailForMis(Connection p_con, ProcessStatusVO p_processStatusVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateProcessDetailForMis";
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetailForMis Entered ", "p_processStatusVO=" + p_processStatusVO + " con is  " + p_con);
        int updateCount = 0;
         
        String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=? WHERE process_id=? ";
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetailForMis", "QUERY sqlUpdate=" + sqlUpdate);
        try(PreparedStatement pstmtUpdate = p_con.prepareStatement(sqlUpdate);) {
            
            int i = 1;
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessStatus());
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessID());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("updateProcessDetail", "SQLException : " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetailForMis]", "", "", "", "SQL Exception while updating the process_status table" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetailForMis", "Error in updating the process_status table");
        } catch (Exception e) {
            _log.error("updateProcessDetail", "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetailForMis]", "", "", "", "Exception while updating the process_status table" + e.getMessage());
            throw new BTSLBaseException(this, "updateProcessDetailForMis", "Error in updating the process_status table");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug("updateProcessDetail", "Exiting updateCount:" + updateCount);
        }
        return updateCount;
    }

    /**
     * This method is used to update the scheduler start date and status
     * 
     * @param Connection
     *            p_con
     * @param ProcessStatusVO
     *            p_processStatusVO
     * @return int updateCount
     * @throws BTSLBaseException
     */
    public int updateProcessDetailNetworkWise(Connection p_con, ProcessStatusVO p_processStatusVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateProcessDetailNetworkWise";
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetailNetworkWise: ", "Entered p_processStatusVO=" + p_processStatusVO + " Process_ID=" + p_processStatusVO.getProcessID() + " Network_Code=" + p_processStatusVO.getNetworkCode());
        int updateCount = 0;
         
        String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=?,executed_upto=?,executed_on=? WHERE process_id=? and network_code=?";
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetailNetworkWise", " QUERY sqlUpdate=" + sqlUpdate);
        try(PreparedStatement pstmtUpdate = p_con.prepareStatement(sqlUpdate);) {
            
            int i = 1;
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getExecutedUpto()));
            pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedOn()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessID());
            pstmtUpdate.setString(i++, p_processStatusVO.getNetworkCode());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("updateProcessDetailNetworkWise", " SQLException : " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetailNetworkWise]", "", "", "", "SQL Exception while updating the process_status table" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetailNetworkWise", "Error in updating the process_status table");
        } catch (Exception e) {
            _log.error("updateProcessDetailNetworkWise ", "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetailNetworkWise]", "", "", "", "Exception while updating the process_status table" + e.getMessage());
            throw new BTSLBaseException(this, "updateProcessDetail", "Error in updating the process_status table");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug("updateProcessDetailNetworkWise ", "Exiting updateCount:" + updateCount);
        }
        return updateCount;
    }

    /**
     * This method is used to load the Process Status detail(processID,date-time
     * and status etc) from PROCESS_STATUS
     * by process_id and network_code.
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_processID
     * @param String
     *            p_networkCode
     * @throws BTSLBaseException
     * @return ProcessStatusVO processStatusVO
     * @author Vinay Singh
     */
    public ProcessStatusVO loadProcessDetailNetworkWise(Connection p_con, String p_processID, String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadProcessDetailNetworkWise";
        if (_log.isDebugEnabled())
            _log.debug("loadProcessDetailNetworkWise", " Entered Process Id=: " + p_processID + " Network code=" + p_networkCode);
        
         
        ProcessStatusVO processStatusVO = null;
        String sqlSelect = "SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval,network_code,record_count FROM process_status WHERE process_id=? and network_code=?";
        if (_log.isDebugEnabled())
            _log.debug("loadProcessDetailNetworkWise", " QUERY sqlSelect:" + sqlSelect);
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_processID);
            pstmtSelect.setString(2, p_networkCode);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                processStatusVO = new ProcessStatusVO();
                processStatusVO.setProcessID(rs.getString("process_id"));
                processStatusVO.setStartDate(rs.getTimestamp("start_date"));
                processStatusVO.setProcessStatus(rs.getString("scheduler_status"));
                if (rs.getDate("executed_upto") != null)
                    processStatusVO.setExecutedUpto(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("executed_upto")));
                processStatusVO.setExecutedOn(rs.getDate("executed_on"));
                processStatusVO.setExpiryTime(rs.getLong("expiry_time"));
                processStatusVO.setBeforeInterval(rs.getLong("before_interval"));
                processStatusVO.setNetworkCode(rs.getString("network_code"));
                processStatusVO.setRecordCount(Integer.parseInt(rs.getString("record_count")));
            }
        } 
        }
    catch (SQLException sqe) {
            _log.error("loadProcessDetailNetworkWise ", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[loadProcessDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", " loadProcessDetailNetworkWise", " error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadProcessDetailNetworkWise", " Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[loadProcessDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", " loadProcessDetailNetworkWise", " error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug("loadProcessDetailNetworkWise", " Exiting processStatusVO=" + processStatusVO);
        }
        return processStatusVO;
    }

    /**
     * @param p_con
     * @param p_processID
     * @return
     * @throws BTSLBaseException
     */
    public ProcessStatusVO lockProcessStatusTable(Connection p_con, String p_processID) throws BTSLBaseException {
        final String METHOD_NAME = "lockProcessStatusTable";
        if (_log.isDebugEnabled())
            _log.debug("lockProcessStatusTable", "Entered p_processID: " + p_processID);
         
        
        ProcessStatusVO processStatusVO = null;
        String sqlSelect = null;
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
            sqlSelect = "SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval,record_count,network_code FROM process_status WHERE process_id=? for update WITH RS";
        else
            sqlSelect = "SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval,record_count,network_code FROM process_status WHERE process_id=? for update NOWAIT";

        if (_log.isDebugEnabled())
            _log.debug("lockProcessStatusTable", "QUERY sqlSelect:" + sqlSelect);
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_processID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                processStatusVO = new ProcessStatusVO();
                processStatusVO.setProcessID(rs.getString("process_id"));
                processStatusVO.setStartDate(rs.getTimestamp("start_date"));
                processStatusVO.setProcessStatus(rs.getString("scheduler_status"));

                if (rs.getDate("executed_upto") != null)
                    processStatusVO.setExecutedUpto(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("executed_upto")));
                processStatusVO.setExecutedOn(rs.getDate("executed_on"));
                processStatusVO.setExpiryTime(rs.getLong("expiry_time"));
                processStatusVO.setBeforeInterval(rs.getLong("before_interval"));
                processStatusVO.setRecordCount(Integer.parseInt(rs.getString("record_count")));
                processStatusVO.setNetworkCode(rs.getString("network_code"));
            }
        }
        }catch (SQLException sqe) {
            _log.error("lockProcessStatusTable", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[lockProcessStatusTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "lockProcessStatusTable", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
        } catch (Exception ex) {
            _log.error("lockProcessStatusTable", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[lockProcessStatusTable]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "lockProcessStatusTable", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled())
                _log.debug("lockProcessStatusTable", "Exiting processStatusVO=" + processStatusVO);
        }
        return processStatusVO;
    }
    public int updateProcessDetailNetworkWiseDP(Connection p_con, ProcessStatusVO p_processStatusVO)throws BTSLBaseException
	{
		final String METHOD_NAME = "updateProcessDetailNetworkWise";
		if(_log.isDebugEnabled())_log.debug("updateProcessDetailNetworkWise: ","Entered p_processStatusVO="+p_processStatusVO + " Process_ID=" + p_processStatusVO.getProcessID()+" Network_Code="+p_processStatusVO.getNetworkCode());
		int updateCount=0;
		 
		String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=?,executed_upto=?,executed_on=?,record_count=? WHERE process_id=? and network_code=?";
		if (_log.isDebugEnabled())_log.debug("updateProcessDetailNetworkWise", " QUERY sqlUpdate=" +sqlUpdate);
		try(PreparedStatement pstmtUpdate= p_con.prepareStatement(sqlUpdate);)
		{
			
			int i=1;
			pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
			pstmtUpdate.setString(i++,p_processStatusVO.getProcessStatus());
			pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getExecutedUpto()));
			pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedOn()));
			pstmtUpdate.setInt(i++,p_processStatusVO.getRecordCount());
			pstmtUpdate.setString(i++,p_processStatusVO.getProcessID());
			pstmtUpdate.setString(i++,p_processStatusVO.getNetworkCode());
			
			updateCount = pstmtUpdate.executeUpdate();
		}
		catch (SQLException sqe)
		{
		    _log.error("updateProcessDetailNetworkWise", " SQLException : " + sqe.getMessage());
		    _log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetailNetworkWise]","","","","SQL Exception while updating the process_status table"+sqe.getMessage());
			throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetailNetworkWise", "Error in updating the process_status table");
		} 
		catch (Exception e)
		{
		    _log.error("updateProcessDetailNetworkWise ", "Exception : " + e);
		    _log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetailNetworkWise]","","","","Exception while updating the process_status table"+e.getMessage());
			throw new BTSLBaseException(this, "updateProcessDetail", "Error in updating the process_status table");
		}
		finally{
			
			if (_log.isDebugEnabled()) 	_log.debug("updateProcessDetailNetworkWise ", "Exiting updateCount:" + updateCount);
		}
		return updateCount;
	}
    public int updateProcessDetailDP(Connection p_con, ProcessStatusVO p_processStatusVO)throws BTSLBaseException
	{
		final String METHOD_NAME = "updateProcessDetail";
		if(_log.isDebugEnabled())_log.debug("updateProcessDetail Entered ","p_processStatusVO="+p_processStatusVO + " con is  " + p_con);
		int updateCount=0;
		 
		String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=?,executed_upto=?,executed_on=?,record_count =? WHERE process_id=? ";
		if (_log.isDebugEnabled())_log.debug("updateProcessDetail", "QUERY sqlUpdate=" +sqlUpdate);
		try(PreparedStatement pstmtUpdate= p_con.prepareStatement(sqlUpdate);)
		{
			
			int i=1;
			pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
			pstmtUpdate.setString(i++,p_processStatusVO.getProcessStatus());
			pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getExecutedUpto()));
			pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedOn()));
			pstmtUpdate.setInt(i++,p_processStatusVO.getRecordCount());
			pstmtUpdate.setString(i++,p_processStatusVO.getProcessID());
			
			updateCount = pstmtUpdate.executeUpdate();
		}
		catch (SQLException sqe)
		{
		    _log.error("updateProcessDetail", "SQLException : " + sqe.getMessage());
		    _log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetail]","","","","SQL Exception while updating the process_status table"+sqe.getMessage());
			throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetail", "Error in updating the process_status table");
		} 
		catch (Exception e)
		{
		    _log.error("updateProcessDetail", "Exception : " + e);
		    _log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetail]","","","","Exception while updating the process_status table"+e.getMessage());
			throw new BTSLBaseException(this, "updateProcessDetail", "Error in updating the process_status table");
		}
		finally{
			
			if (_log.isDebugEnabled()) 	_log.debug("updateProcessDetail", "Exiting updateCount:" + updateCount);
		}
		return updateCount;
	}
    
    /**
	 * Method loadProcessDetailNetworkWiseWithWait
	 * @param p_con
	 * @param p_processID
	 * @param p_networkCode
	 * @return processStatusVO
	 * @throws BTSLBaseException
	 */
	
	public ProcessStatusVO loadProcessDetailNetworkWiseWithWait(Connection p_con,String p_processID,String p_networkCode) throws BTSLBaseException
	{
		final String METHOD_NAME = "loadProcessDetailNetworkWiseWithWait";
		if(_log.isDebugEnabled())_log.debug("loadProcessDetailNetworkWiseWithWait"," Entered Process Id=: "+p_processID+" Network code="+p_networkCode);
		
		 
		ProcessStatusVO processStatusVO = null;
		ProcessStatusQry processStatusQry = (ProcessStatusQry)ObjectProducer.getObject(QueryConstants.PROCESS_STATUS_QRY, QueryConstants.QUERY_PRODUCER);
        String sqlSelect = processStatusQry.loadProcessDetailNetworkWiseWithWaitQry();
	    if (_log.isDebugEnabled())_log.debug("loadProcessDetailNetworkWiseWithWait", " QUERY sqlSelect:" +sqlSelect);
 		try(PreparedStatement pstmtSelect= p_con.prepareStatement(sqlSelect);)
		{
		   
		    pstmtSelect.setString(1,p_processID);
		    pstmtSelect.setString(2,p_networkCode);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			if(rs.next())
			{
			    processStatusVO = new ProcessStatusVO();
				processStatusVO.setProcessID(rs.getString("process_id"));
				processStatusVO.setStartDate(rs.getTimestamp("start_date"));
				processStatusVO.setProcessStatus(rs.getString("scheduler_status"));
				if(rs.getDate("executed_upto")!=null)
				    processStatusVO.setExecutedUpto(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("executed_upto")));
				processStatusVO.setExecutedOn(rs.getDate("executed_on"));
				processStatusVO.setExpiryTime(rs.getLong("expiry_time"));
				processStatusVO.setBeforeInterval(rs.getLong("before_interval"));
				processStatusVO.setNetworkCode(rs.getString("network_code"));
				processStatusVO.setRecordCount(Integer.parseInt(rs.getString("record_count")));
			}
			}
		}
		catch (SQLException sqe)
		{
			_log.error("loadProcessDetailNetworkWiseWithWait ", "SQLException : " + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[loadProcessDetailNetworkWiseWithWait]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException("ProcessStatusDAO", " loadProcessDetailNetworkWiseWithWait", " error.general.sql.processing");
		}
		catch (Exception ex)
		{
			_log.error("loadProcessDetailNetworkWiseWithWait", " Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[loadProcessDetailNetworkWiseWithWait]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException("ProcessStatusDAO", " loadProcessDetailNetworkWiseWithWait", " error.general.processing");
		}
		finally
		{
			
			if (_log.isDebugEnabled()) 	_log.debug("loadProcessDetailNetworkWiseWithWait", " Exiting processStatusVO="+processStatusVO);
		}
		return processStatusVO;	
	}
	
	
	/**
     * This method is used to load the aggregate of the record counts for a processId
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_processID
     * @throws BTSLBaseException
     * @return ProcessStatusVO processStatusVO
     */
    public int resetProcessRecordCounts(Connection p_con, String p_processID) throws BTSLBaseException {
            final String METHOD_NAME = "resetProcessRecordCounts";
            if (_log.isDebugEnabled())
                _log.debug("resetProcessRecordCounts Entered ", "p_processID=" + p_processID + " con is  " + p_con);
            int updateCount = 0;
            
            String sqlUpdate = " UPDATE process_status SET record_count = ? WHERE process_id=? ";
            if (_log.isDebugEnabled())
                _log.debug("resetProcessRecordCounts", "QUERY sqlUpdate=" + sqlUpdate);
            try ( PreparedStatement pstmtUpdate = p_con.prepareStatement(sqlUpdate);){
                int i = 1;
                pstmtUpdate.setInt(i++, 0);
                pstmtUpdate.setString(i++, p_processID);
                updateCount = pstmtUpdate.executeUpdate();
            } catch (SQLException sqe) {
                _log.error("resetProcessRecordCounts", "SQLException : " + sqe.getMessage());
                _log.errorTrace(METHOD_NAME, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetail]", "", "", "", "SQL Exception while updating the process_status table" + sqe.getMessage());
                throw new BTSLBaseException("ProcessStatusDAO", "resetProcessRecordCounts", "Error in updating the process_status table");
            } catch (Exception e) {
                _log.error("resetProcessRecordCounts", "Exception : " + e);
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetail]", "", "", "", "Exception while updating the process_status table" + e.getMessage());
                throw new BTSLBaseException(this, "resetProcessRecordCounts", "Error in updating the process_status table");
            } finally {
            	
                if (_log.isDebugEnabled())
                    _log.debug("resetProcessRecordCounts", "Exiting updateCount:" + updateCount);
            }
            return updateCount;
    }
    
    public int updateProcessDetailNetworkWiseOnlineVMSGen(Connection p_con, ProcessStatusVO p_processStatusVO)throws BTSLBaseException
	{
		final String METHOD_NAME = "updateProcessDetailNetworkWiseOnlineVMSGen";
		if(_log.isDebugEnabled())_log.debug("updateProcessDetailNetworkWiseOnlineVMSGen: ","Entered p_processStatusVO="+p_processStatusVO + " Process_ID=" + p_processStatusVO.getProcessID()+" Network_Code="+p_processStatusVO.getNetworkCode());
		int updateCount=0;
		 
		String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=?,executed_upto=?,executed_on=?,record_count=? WHERE process_id=? and network_code=?";
		if (_log.isDebugEnabled())_log.debug("updateProcessDetailNetworkWise", " QUERY sqlUpdate=" +sqlUpdate);
		try(PreparedStatement pstmtUpdate = p_con.prepareStatement(sqlUpdate);)
		{
			int i=1;
			pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
			pstmtUpdate.setString(i++,p_processStatusVO.getProcessStatus());
			pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getExecutedUpto()));
			pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedOn()));
			pstmtUpdate.setInt(i++,p_processStatusVO.getRecordCount() + 1);
			pstmtUpdate.setString(i++,p_processStatusVO.getProcessID());
			pstmtUpdate.setString(i++,p_processStatusVO.getNetworkCode());
			
			updateCount = pstmtUpdate.executeUpdate();
		}
		catch (SQLException sqe)
		{
		    _log.error("updateProcessDetailNetworkWise", " SQLException : " + sqe.getMessage());
		    _log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetailNetworkWise]","","","","SQL Exception while updating the process_status table"+sqe.getMessage());
			throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetailNetworkWiseOnlineVMSGen", "Error in updating the process_status table");
		} 
		catch (Exception e)
		{
		    _log.error("updateProcessDetailNetworkWise ", "Exception : " + e);
		    _log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetailNetworkWise]","","","","Exception while updating the process_status table"+e.getMessage());
			throw new BTSLBaseException(this, "updateProcessDetailNetworkWiseOnlineVMSGen", "Error in updating the process_status table");
		}
		finally{
			
			if (_log.isDebugEnabled()) 	_log.debug("updateProcessDetailNetworkWiseOnlineVMSGen ", "Exiting updateCount:" + updateCount);
		}
		return updateCount;
	}
    
    /**
     * This method returns the sum of record counts(for different networks) for a particular processID
     * @param p_con
     * @param p_processId
     * @return
     * @throws BTSLBaseException
     */
    
    public int getRecordCountSumForProcess(Connection p_con, String processId) throws BTSLBaseException
	{
		final String METHOD_NAME = "getRecordCountSum";
		if(_log.isDebugEnabled())
			_log.debug("getRecordCountSum Entered ","processId="+processId + " con is  " + p_con);
		int recordCount = 0;
		ResultSet rs = null;
		PreparedStatement pstmtSelect = null;
		String sqlSelect = " Select sum(record_count) AS RECORD_COUNT_SUM from process_status WHERE process_id= ?";
		if (_log.isDebugEnabled())
			_log.debug(METHOD_NAME, "QUERY sqlUpdate=" +sqlSelect);
		try
		{
			pstmtSelect = p_con.prepareStatement(sqlSelect);
			pstmtSelect.setString(1,processId);
			rs = pstmtSelect.executeQuery();
			while(rs.next())
			{
				recordCount=rs.getInt("RECORD_COUNT_SUM");
			}
		}
		catch (SQLException sqe)
		{
		    _log.error(METHOD_NAME, "SQLException : " + sqe.getMessage());
		    _log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetail]","","","","SQL Exception while updating the process_status table"+sqe.getMessage());
			throw new BTSLBaseException("ProcessStatusDAO", METHOD_NAME, "Error in getting data from the process_status table");
		} 
		catch (Exception e)
		{
		    _log.error(METHOD_NAME, "Exception : " + e);
		    _log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetail]","","","","Exception while updating the process_status table"+e.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "Error in getting data from the process_status table");
		}
		finally{
			if (_log.isDebugEnabled()) 
				_log.debug(METHOD_NAME, "Exiting updateCount:" + recordCount);
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
		}
		return recordCount;
	}
    public int updateProcessDetailNetworkWiseOnlineChangeOthStatus(Connection p_con, ProcessStatusVO p_processStatusVO,int numberOfVouchers)throws BTSLBaseException
   	{
   		final String METHOD_NAME = "updateProcessDetailNetworkWiseOnlineChangeOthStatus";
   		if(_log.isDebugEnabled())_log.debug("updateProcessDetailNetworkWiseOnlineChangeOthStatus: ","Entered p_processStatusVO="+p_processStatusVO + " Process_ID=" + p_processStatusVO.getProcessID()+" Network_Code="+p_processStatusVO.getNetworkCode());
   		int updateCount=0;
   		 
   		String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=?,executed_upto=?,executed_on=?,record_count=? WHERE process_id=? and network_code=?";
   		if (_log.isDebugEnabled())_log.debug("updateProcessDetailNetworkWise", " QUERY sqlUpdate=" +sqlUpdate);
   		try(PreparedStatement pstmtUpdate = p_con.prepareStatement(sqlUpdate);)
   		{
   			int i=1;
   			pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
   			pstmtUpdate.setString(i++,p_processStatusVO.getProcessStatus());
   			pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getExecutedUpto()));
   			pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedOn()));
   			pstmtUpdate.setInt(i++,p_processStatusVO.getRecordCount() + numberOfVouchers);
   			pstmtUpdate.setString(i++,p_processStatusVO.getProcessID());
   			pstmtUpdate.setString(i++,p_processStatusVO.getNetworkCode());
   			
   			updateCount = pstmtUpdate.executeUpdate();
   		}
   		catch (SQLException sqe)
   		{
   		    _log.error("updateProcessDetailNetworkWiseOnlineChangeOthStatus", " SQLException : " + sqe.getMessage());
   		    _log.errorTrace(METHOD_NAME, sqe);
   			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetailNetworkWise]","","","","SQL Exception while updating the process_status table"+sqe.getMessage());
   			throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetailNetworkWiseOnlineVMSGen", "Error in updating the process_status table");
   		} 
   		catch (Exception e)
   		{
   		    _log.error("updateProcessDetailNetworkWiseOnlineChangeOthStatus ", "Exception : " + e);
   		    _log.errorTrace(METHOD_NAME, e);
   			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProcessStatusDAO[updateProcessDetailNetworkWise]","","","","Exception while updating the process_status table"+e.getMessage());
   			throw new BTSLBaseException(this, "updateProcessDetailNetworkWiseOnlineChangeOthStatus", "Error in updating the process_status table");
   		}
   		finally{
   			
   			if (_log.isDebugEnabled()) 	_log.debug("updateProcessDetailNetworkWiseOnlineChangeOthStatus ", "Exiting updateCount:" + updateCount);
   		}
   		return updateCount;
   	}
       
    
}
