/**
 * CDRGeneratorProcess.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Ruwali 24/05/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * Main class for CDR Generation process.
 */

package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.filetransfer.FTPUtility;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.post.cdr.CDRRecordGeneratorI;
import com.btsl.pretups.inter.postqueue.QueueTableDAO;
import com.btsl.pretups.processes.businesslogic.IntervalTimeVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class CDRGeneratorProcess {
    private static final Log LOGGER = LogFactory.getLog(CDRGeneratorProcess.class.getName());

    /**
     * Method main
     * This is the Main by which the execution of process will start
     * 
     * @param args
     *            String[]
     * @return void
     */

    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 4) {
                System.out.println("Usage : CDRGeneratorProcess [Constants file] [LogConfig file] [Interface ID] [Process ID]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println("CDRGeneratorProcess main() Constants file not found on location:: " + constantsFile.toString());
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println("CDRGeneratorProcess main() Logconfig file not found on location:: " + logconfigFile.toString());
                return;
            }
            final String interfaceID = args[2];
            final String processID = args[3];
            if (BTSLUtil.isNullString(interfaceID)) {
                System.out.println("CDRGeneratorProcess main() " + "Interface ID should be given as a input parameter");
                return;
            }
            if (BTSLUtil.isNullString(processID)) {
                System.out.println("CDRGeneratorProcess main() " + "Process ID should be given as a input parameter");
                return;
            }
            // To load the process & constants file cache.
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            try {
                final String fileExtn = Constants.getProperty("INTERFACE_FILE_EXTENSION");
                final File inFile = new File(Constants.getProperty("INTERFACE_DIRECTORY") + interfaceID + "." + fileExtn);
                if (!inFile.exists()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("CDRGeneratorProcess[main]",
                            "CDRGeneratorProcess main() " + "Interface File " + args[2] + "." + fileExtn + " not found in the Interface directory defined in Constants file");
                    }
                    ConfigServlet.destroyProcessCache();
                    return;
                }
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CDRGeneratorProcess[main]", "", "", "",
                    "Exception: File path is invalid" + e.getMessage());
                LOGGER.errorTrace(METHOD_NAME, e);
                ConfigServlet.destroyProcessCache();
                return;
            }
            FileCache.loadAtStartUp();
            // Check The CDR file path [FILE_PATH] Exists in Constants file if
            // not create the path
            boolean success = false;
            final String filePath = FileCache.getValue(interfaceID, "FILE_PATH");
            final File f = new File(filePath);
            if (!f.exists()) {
                success = f.mkdirs();
                if (!success) {
                    LOGGER.error("CDRGeneratorProcess[main]", "CDRGeneratorProcess main() " + "Unable to create the CDR FILE_PATH=" + f);
                    throw new BTSLBaseException("CDRGeneratorProcess", "main", "Unable to create the CDR FILE_PATH=" + f);
                }
            }
            final CDRGeneratorProcess CDRProcess = new CDRGeneratorProcess();
            CDRProcess.process(processID, interfaceID);

            String ftpRequired = FileCache.getValue(interfaceID, "FILE_POST2PRE_FTP_REQUIRED");
            if (BTSLUtil.isNullString(ftpRequired)) {
                ftpRequired = "N";
            }
            if ("Y".equalsIgnoreCase(ftpRequired)) {
                new FTPUtility().ftpFilesToRemoteSystem(interfaceID);
            }

        } catch (BTSLBaseException be) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CDRGeneratorProcess[main]", "CDRGeneratorProcess main() BTSLBaseException be=" + be.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CDRGeneratorProcess[main]", "CDRGeneratorProcess main() Exception e=" + e.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CDRGeneratorProcess[main]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Method process
     * This is the method by which the process will start
     * 
     * @param p_processID
     *            String
     * @param p_interfaceID
     *            String
     * @return void
     * @throws BTSLBaseException
     */

    private void process(String p_processID, String p_interfaceID) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        Connection con = null;
        ProcessStatusVO processVO = null;
        CDRRecordGeneratorI handlerObj = null;
        ProcessStatusDAO processDAO = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CDRGeneratorProcess[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            final ProcessBL processBL = new ProcessBL();
            processVO = processBL.checkProcessUnderProcess(con, p_processID);
            if (processVO.isStatusOkBool()) {
                con.commit();
                // The Interface class object will be dynamically picked from
                // the file cache &
                // all the constants related to CDR file generation will be
                // loaded
                handlerObj = (CDRRecordGeneratorI) Class.forName(FileCache.getValue(p_interfaceID, "INTERFACE_CLASS")).newInstance();                                
                handlerObj.loadConstants(p_interfaceID);
                // Generate the Time Intervals corresponding to the ExecutetUpto
                // time in Process status table
                final ArrayList intervalList = processBL.generateInterval(processVO, p_interfaceID);
                this.processIntervals(con, intervalList, p_interfaceID, processVO, handlerObj);
            } else {
                throw new BTSLBaseException("CDRGeneratorProcess", "process", "Process is already running..");
            }
        } catch (BTSLBaseException be) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("process", "BTSLBaseException: " + be.getMessage());
            }
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
            LOGGER.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("process", "Exception: " + e.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e1) {
                LOGGER.errorTrace(METHOD_NAME, e1);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CDRGeneratorProcess[process]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("CDRGeneratorProcess", "process", "Exception :" + e.getMessage());
        } finally {
            if (processVO != null) {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
            }
            try {
                processDAO = new ProcessStatusDAO();
                if (processDAO.updateProcessDetail(con, processVO) > 0) {
                    con.commit();
                } else {
                    con.rollback();
                }
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("process", " Exception in update process detail" + e.getMessage());
                }
                LOGGER.errorTrace(METHOD_NAME, e);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("process", " Exiting");
            }
            try {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e1) {
                        LOGGER.errorTrace(METHOD_NAME, e1);
                    }
                }
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
        }
    }

    /**
     * Method processIntervals
     * This method is used to genererate the CDR record files and update the
     * info in DB
     * 
     * @param p_con
     *            Connection
     * @param p_timeIntervalList
     *            ArrayList
     * @param p_interfaceId
     *            String
     * @param p_cdrGenerator
     *            CDRRecordGeneratorI
     * @return void
     * @throws BTSLBaseException
     */

    private void processIntervals(Connection p_con, ArrayList p_timeIntervalList, String p_interfaceId, ProcessStatusVO p_processVO, CDRRecordGeneratorI p_cdrGenerator) throws BTSLBaseException {
        final String METHOD_NAME = "processIntervals";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processIntervals", " Entered p_timeIntervalList=" + p_timeIntervalList + "p_interfaceId" + p_interfaceId + "p_cdrGenerator=" + p_cdrGenerator);
        }
        String fileNames[] = null;
        long intervalCount = 0;
        try {
            final String serviceType = FileCache.getValue(p_interfaceId, "SERVICE_TYPE");
            if (BTSLUtil.isNullString(serviceType)) {
                throw new BTSLBaseException("CDRGeneratorProcess", "processIntervals", "Service Type Not defined in IN File");
            }
            final QueueTableDAO queueDAO = new QueueTableDAO();
            IntervalTimeVO intervalVO = null;
            ArrayList queueVOList = null;
            final ProcessStatusDAO processDAO = new ProcessStatusDAO();
            final int size = p_timeIntervalList.size();
            int updateCount = -1;

            final String fileCreationSleepTimeStr = FileCache.getValue(p_interfaceId, "FILE_CREATE_SLEEP_TIME");
            long fileCreationSleepTime = 60000;
            if (!BTSLUtil.isNullString(fileCreationSleepTimeStr) && BTSLUtil.isNumeric(fileCreationSleepTimeStr)) {
                fileCreationSleepTime = Long.parseLong(fileCreationSleepTimeStr);
            }

            for (int i = 0; i < size; i++) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("processIntervals", "Process will continue after " + fileCreationSleepTime + "milliseconds to avoid overwriting");
                }
                // Below sleep is added to avoid overwrite of the file of the
                // previous part if file name is upto the minute
                Thread.sleep(fileCreationSleepTime);

                queueVOList = null;
                intervalVO = (IntervalTimeVO) p_timeIntervalList.get(i);
                queueVOList = queueDAO.getQueueDataForCDRGenerationProcess(p_con, serviceType, p_interfaceId, intervalVO.getStartTime(), intervalVO.getEndTime());
                if (queueVOList != null && queueVOList.size() > 0) {
                    fileNames = p_cdrGenerator.generateCDRRecords(queueVOList, p_interfaceId);
                    updateCount = queueDAO.updateQueueDataForCDR(p_con, queueVOList);
                    if (queueVOList.size() == updateCount) {
                        p_processVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
                        p_processVO.setExecutedUpto(intervalVO.getEndTime());
                        p_processVO.setExecutedOn(new Date());
                        if (processDAO.updateProcessDetail(p_con, p_processVO) > 0) {
                            p_con.commit();
                        } else {
                            p_con.rollback();
                            if (fileNames != null) {
                                this.deleteFiles(fileNames);
                            }
                        }
                    } else {
                        p_con.rollback();
                        if (fileNames != null) {
                            this.deleteFiles(fileNames);
                        }
                    }
                } else {
                    // to show only header and trailer in file :: Added by Zafar
                    // Abbas
                    if (PretupsI.YES.equals(Constants.getProperty("BLANK_CDR_FILE_REQUIRED"))) {
                        fileNames = p_cdrGenerator.generateCDRRecords(queueVOList, p_interfaceId);
                    }

                    // No Data found in queue VO corresponding to the intervals
                    // generated
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("processIntervals",
                            "No Data found in queue VO corresponding to the intervals between " + intervalVO.getStartTime() + " and " + intervalVO.getEndTime());
                    }
                    intervalCount++;
                    continue;
                }
                /*
                 * if (_logger.isDebugEnabled())
                 * _logger.debug("processIntervals","Process will continue after "
                 * +fileCreationSleepTime +"milliseconds to avoid overwriting");
                 * //Below sleep is added to avoid overwrite of the file of the
                 * previous part if file name is upto the minute
                 * Thread.sleep(fileCreationSleepTime);
                 */
            }
            if (intervalCount == size) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("processIntervals", "No Data found in queue VO corresponding to the whole intervals generated");
                }
            }

            // update executed on & executed upto
            // p_processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
            p_processVO.setExecutedOn(new Date());
            if (size != 0) {
                intervalVO = (IntervalTimeVO) p_timeIntervalList.get(size - 1);
                p_processVO.setExecutedUpto(intervalVO.getEndTime());
            } else {
                p_processVO.setExecutedUpto(new Date());
            }

            /*
             * if(processDAO.updateProcessDetail(p_con,p_processVO)>0)
             * p_con.commit();
             * else
             * p_con.rollback();
             */

            // If the process is successfull generate the ALARM
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CDRGeneratorProcess[processIntervals]", "", "", "",
                " CDRGeneratorProcess process has been executed successfully.");
        } catch (BTSLBaseException be) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
            // Remove the files defined in string array
            // if(fileNames!=null)
            // try{this.deleteFiles(fileNames);}catch(Exception e1){}
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("processIntervals", " " + be.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception ex) {
                LOGGER.errorTrace(METHOD_NAME, ex);
            }
            // Remove the files defined in string array
            if (fileNames != null) {
                try {
                    this.deleteFiles(fileNames);
                } catch (Exception ex1) {
                    LOGGER.errorTrace(METHOD_NAME, ex1);
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("processIntervals", " " + e.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("CDRGeneratorProcess", "processIntervals", PretupsErrorCodesI.CDR_FILE_GENERATION_ERROR);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("processIntervals", " Exiting");
            }
        }
    }

    /**
     * Method deleteFiles
     * This method is used to delete the cdr files generated if any exception is
     * encountered
     * 
     * @param p_files
     *            String[]
     * @return void
     * @throws BTSLBaseException
     */

    private void deleteFiles(String[] p_files) throws BTSLBaseException {
        final String METHOD_NAME = "deleteFiles";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleteFiles", " Entered p_files=" + p_files);
        }
        File file = null;
        try {
            if (p_files != null) // If any file in the array delete it one by
            // one
            {
                for (int i = 0; i < p_files.length; i++) {
                    file = new File(p_files[i]);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            file = null;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deleteFiles", " " + e.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("CDRGeneratorProcess", "deleteFiles", "Execption :" + e.getMessage());
        } finally {
            file = null;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deleteFiles", " Exited");
            }
        }
    }
}
