package com.btsl.pretups.channel.reports.businesslogic;

/*
 * @# ScheduledReportDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * mar 27, 2005 Ved.sharma Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

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

/**
 * @author 
 *
 */
public class ScheduledReportDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * by ved
     * Method :loadScheduledReports
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pModule
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadScheduledReports(Connection pCon, String pModule) throws BTSLBaseException {
        final String methodName = "loadScheduledReports";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: pModule : " + pModule);
        }

        final StringBuilder strBuff = new StringBuilder();
        ArrayList reportList = null;
        ScheduledReportVO scheduledReportVO = null;

        strBuff.append(" SELECT DSR.report_code, DSR.report_name, DSR.module, DSR.rpt_name, DSR.url, DSR.generated_file_name FROM daily_scheduled_report DSR ");
        strBuff.append(" WHERE DSR.module = ? AND DSR.display_allowed = ? ");
        strBuff.append(" ORDER BY DSR.module, DSR.report_name ");

        final String sqlSelect = strBuff.toString();
        int size=0;
        if (log.isDebugEnabled()) {
            log.debug("loadChannelUserList", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);) {
           
            int i = 0;
            pstmtSelect.setString(++i, pModule);
            pstmtSelect.setString(++i, PretupsI.YES);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            reportList = new ArrayList();
            while (rs.next()) {
                scheduledReportVO = new ScheduledReportVO();
                scheduledReportVO.setReportCode(rs.getString("report_code"));
                scheduledReportVO.setReportName(rs.getString("report_name"));
                scheduledReportVO.setModule(rs.getString("module"));
                scheduledReportVO.setRptName(rs.getString("rpt_name"));
                scheduledReportVO.setUrl(rs.getString("url"));
                scheduledReportVO.setGeneratedFileName(rs.getString("generated_file_name"));
                reportList.add(scheduledReportVO);
            }
            if(!reportList.isEmpty()){
            	size=reportList.size();
            }
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadScheduledReports]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadScheduledReports]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
          
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: reportList size =" + size);
            }
        }
        return reportList;
    }

    /**
     * by Rajdeep
     * Method :viewDownloadReports
     * 
     * @param pCon
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList viewDownloadReports(Connection pCon) throws BTSLBaseException {
        final String methodName = "viewDownloadReports";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: pModule : ");
        }

        final StringBuilder strBuff = new StringBuilder();
        ArrayList reportList = null;
        DownloadReportVO downloadReportVO = null;
        strBuff.append(" SELECT DSR.report_code, DSR.report_name, DSR.type,DSR.status,DSR.path_key,DSR.date_format FROM download_scheduled_reports DSR ");
        strBuff.append(" ORDER BY DSR.report_name ");
        final String sqlSelect = strBuff.toString();
        int size=0;
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try (PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);ResultSet rs = pstmtSelect.executeQuery();){
            
            reportList = new ArrayList();
            while (rs.next()) {
                downloadReportVO = new DownloadReportVO();
                downloadReportVO.setReportCode(rs.getString("report_code"));
                downloadReportVO.setReportName(rs.getString("report_name"));
                downloadReportVO.setType(rs.getString("type"));
                downloadReportVO.setStatus(rs.getString("status"));
                downloadReportVO.setPathKey(rs.getString("path_key"));
                downloadReportVO.setDateFormat(rs.getString("date_format"));
                reportList.add(downloadReportVO);
            }
            if(!reportList.isEmpty()){
            	size=reportList.size();
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledReportDAO[downloadReports]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("downloadReports", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledReportDAO[downloadReports]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: reportList size =" + size);
            }
        }
        return reportList;
    }

    /**
     * by Rajdeep
     * Method : updateStatus
     * 
     * @param pCon
     *            java.sql.Connection
     * @param p_statusCheckbox
     *            String[]
     * @return int
     * @throws BTSLBaseException
     */
    public int updateStatus(Connection pCon, ArrayList pDownloadReportVOList, String[] pStatusFlag) throws BTSLBaseException {
        final String methodName = "updateStatus";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:  statusCheckbox Size= " + pStatusFlag.length);
        }

        int updateCount = 0;
        final StringBuilder updateQueryBuff = new StringBuilder();
        updateQueryBuff.append("UPDATE download_scheduled_reports DSR ");
        updateQueryBuff.append("SET status=?");
        updateQueryBuff.append(" WHERE DSR.report_code=?");
        final String updateQueryString = updateQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Query sqlDelete:" + updateQueryString);
        }
        try (PreparedStatement updatePstmt = pCon.prepareStatement(updateQueryString);){
            final Iterator itr = pDownloadReportVOList.iterator();
            DownloadReportVO downloadReportVO = null;
            int i = 0;
            while (itr.hasNext()) {
                downloadReportVO = (DownloadReportVO) itr.next();
                if (pStatusFlag.length > i && downloadReportVO.getReportCode().equals(pStatusFlag[i])) {
                    updatePstmt.setString(1, "Y");
                    i++;
                } else {
                    updatePstmt.setString(1, "N");
                }
                updatePstmt.setString(2, downloadReportVO.getReportCode());
                updateCount += updatePstmt.executeUpdate();
                updatePstmt.clearParameters();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        } finally {
       	
            if (log.isDebugEnabled()) {
                log.debug("viewDownloadReports", "Exiting: reportList size =" + pDownloadReportVOList.size());
            }
        }

        return updateCount;
    }

    /**
     * by Rajdeep
     * Method : loadReportList
     * 
     * @param pCon
     *            java.sql.Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadReportsList(Connection pCon) {
        final String methodName = "loadReportsList";
        if (log.isDebugEnabled()) {
            log.debug("loadReportsList::", "Entered");
        }
        DownloadReportVO downloadReportVO = null;
       
        final ArrayList downloadReportVOList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" SELECT DSR.report_code, DSR.report_name, DSR.type,DSR.status,DSR.path_key,DSR.prefix,DSR.date_format,DSR.module FROM download_scheduled_reports DSR ");
        strBuff.append(" WHERE DSR.status='Y' ");
        strBuff.append(" ORDER BY DSR.display_seq ");
        final String selQuery = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "SQLQuery " + selQuery);
        }
        
        try(PreparedStatement selPstmt = pCon.prepareStatement(selQuery);ResultSet rs = selPstmt.executeQuery();) {
                while (rs.next()) {
                downloadReportVO = new DownloadReportVO();
                downloadReportVO.setReportCode(rs.getString("report_code"));
                downloadReportVO.setReportName(rs.getString("report_name"));
                downloadReportVO.setType(rs.getString("type"));
                downloadReportVO.setPathKey(rs.getString("path_key"));
                downloadReportVO.setStatus(rs.getString("status"));
                downloadReportVO.setPrefix(rs.getString("prefix"));
                downloadReportVO.setDateFormat(rs.getString("date_format"));
                downloadReportVO.setModule(rs.getString("module"));
                downloadReportVOList.add(downloadReportVO);
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }finally {
        	
        if (log.isDebugEnabled()) {
            log.debug("loadReportsList::", "Exiting");
          }
        }
        return downloadReportVOList;
    }

    /**
     * by vikas
     * Method :loadDownloadReports
     * 
     * @param pCon
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author vikas.yadav
     */
    public ArrayList loadDownloadReports(Connection pCon, String pReportCode, boolean pIsActive) throws BTSLBaseException {
        final String methodName = "loadDownloadReports";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: pReportCode :" + pReportCode + "pIsActive :" + pIsActive);
        }
        final StringBuilder strBuff = new StringBuilder();
        ArrayList reportList = null;
        
        DownloadReportVO downloadReportVO = null;
        int size=0;

        try {
            strBuff.append(" SELECT DSR.report_code, DSR.report_name, DSR.type,DSR.status,DSR.path_key,DSR.date_format,DSR.module FROM download_scheduled_reports DSR ");
            if (!BTSLUtil.isNullString(pReportCode)) {
                strBuff.append(" WHERE DSR.report_code=?");
            }
            if (pIsActive) {
                strBuff.append(" AND DSR.status ='Y' ");
            }

            strBuff.append(" ORDER BY DSR.display_seq ");
            final String sqlSelect = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
           try(PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);)
           {
            if (!BTSLUtil.isNullString(pReportCode)) {
                pstmtSelect.setString(1, pReportCode);
            }
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            reportList = new ArrayList();
            while (rs.next()) {
                downloadReportVO = new DownloadReportVO();
                downloadReportVO.setReportCode(rs.getString("report_code"));
                downloadReportVO.setReportName(rs.getString("report_name"));
                downloadReportVO.setType(rs.getString("type"));
                downloadReportVO.setStatus(rs.getString("status"));
                downloadReportVO.setPathKey(rs.getString("path_key"));
                downloadReportVO.setDateFormat(rs.getString("date_format"));
                downloadReportVO.setModule(rs.getString("module"));
                reportList.add(downloadReportVO);
            }
            if(!reportList.isEmpty()){
            	size=reportList.size();
            }
        } 
           }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledReportDAO[loadDownloadReports]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadDownloadReports", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledReportDAO[loadDownloadReports]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: reportList size =" + size);
            }
        }
        return reportList;
    }

}
