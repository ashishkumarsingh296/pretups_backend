/**
 * @(#)CustomerRechargeFileCreation.java
 *                                       Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                       All Rights Reserved
 *                                       This class is used for generating the
 *                                       Customer Recharge File.
 *                                       <description>
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Author Date History
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Pushkar Sharma Jan 12, 2015 Initital
 *                                       Creation
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       ------------------
 * 
 */

package com.client.pretups.processes.clientprocesses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.CustomerRechargeFileCreationDAO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;

public class CustomerRechargeFileCreation {

    private static String _customerRechargeFileName = null;
    private static String _customerRechargeFilePath = null;
    private static String _customerRechargeFileExt = null;
    private static String _customerRechargeEmptyFileGenerate = null;
    private static String _batchAllowedServices = null;
    private static String _processId = null;
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static boolean _statusOk;
    private static final Log LOG = LogFactory.getLog(CustomerRechargeFileCreation.class.getName());

    private CustomerRechargeFileCreation() {

    }

    /**
     * This method will generate the Customer Recharge File on continues basis .
     * Takes Constants.props and LogConfig.props as arguments.
     * 
     * @throws BTSLBaseException
     */
    public static void main(String[] args) throws BTSLBaseException {
        final String methodName = "main";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Enter ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            if (args.length < 1) {
                LOG.info(methodName, "Constants.props is not loaded");
                return;
            }
            if (args.length < 2) {
                LOG.info(methodName, "LogConfig.props is not loaded");
                return;
            }

            final File constantsFile = new File(args[0]);
            final File logconfigFile = new File(args[1]);
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            loadConstantParameters();
            final CustomerRechargeFileCreationDAO customerDao = new CustomerRechargeFileCreationDAO();

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, _processId);
            _statusOk = _processStatusVO.isStatusOkBool();

            /*
             * Check the status of the process in Process_Status table.
             * Find the last executed time and job interval
             * and generate the file till (executed time + interval) < current
             * time.
             */
            if (_statusOk) {
                final long ONE_MINUTE_IN_MILLIS = 60000;
                final Calendar calender = Calendar.getInstance();
                final Date currentTime = calender.getTime();
                Date executedUpto = _processStatusVO.getExecutedUpto();
                final long beforeInterval = _processStatusVO.getBeforeInterval();
                long executedTime = _processStatusVO.getExecutedUpto().getTime();
                Date nextExecutionTime = new Date(executedTime + (beforeInterval * ONE_MINUTE_IN_MILLIS));
                ProcessStatusVO processStatusUpdateVO = null;

                while (nextExecutionTime.before(currentTime)) {
                    final List<C2STransferVO> detailList = customerDao.getCustomerRechargeDetail(con, executedUpto, nextExecutionTime);
                    if ((detailList.isEmpty() == false) || "y".equalsIgnoreCase(_customerRechargeEmptyFileGenerate)) {
                        createFile(detailList);
                        final int count = customerDao.updateProcessStatus(con, executedUpto, nextExecutionTime, _processId);
                        if (count > 0) {
                            processStatusUpdateVO = new ProcessStatusVO();
                            processStatusUpdateVO.setExecutedOn(nextExecutionTime);
                            processStatusUpdateVO.setExecutedUpto(nextExecutionTime);
                            processStatusUpdateVO.setStartDate(executedUpto);
                            customerDao.markProcessStatusAsComplete(con, _processId, processStatusUpdateVO);
                           mcomCon.finalCommit();
                        }
                    }

                    executedUpto = nextExecutionTime;
                    executedTime = nextExecutionTime.getTime();
                    nextExecutionTime = new Date(executedTime + (beforeInterval * ONE_MINUTE_IN_MILLIS));
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CustomerRechargeFileCreation[" + methodName + "]", "",
                "", "", " CustomerRechargeFileCreation process could not be executed successfully.");
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Error in main() method" + e.getMessage());
            }
            throw new BTSLBaseException("CustomerRechargeFileCreation", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("CustomerRechargeFileCreation#main");
				mcomCon = null;
			}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }
    }

    /**
     * createFile()
     * 
     * @param detailList
     * @throws Exception
     *             Generate the file at specified location.
     */
    private static void createFile(List<C2STransferVO> detailList) throws BTSLBaseException {
        final String methodName = "createFile()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Enter --> List size =" + detailList.size());
        }
        try {
            final File file = new File(getFileFullPath());
            if (!file.exists()) {
                final boolean createFile = file.createNewFile();
                if (createFile == true) {
                    LOG.info(methodName, "File location created.");
                }
            }
            StringBuilder sBuilder = null;
            final List<String> serviceList = new ArrayList<String>(Arrays.asList(_batchAllowedServices.split(",")));
            final FileWriter fw = new FileWriter(file.getAbsoluteFile());
            final BufferedWriter bw = new BufferedWriter(fw);

            for (final C2STransferVO c2sTransferVO : detailList) {
                final boolean serviceType = serviceList.contains(c2sTransferVO.getServiceType());
                sBuilder = new StringBuilder(c2sTransferVO.getTransferDateStr() + "|" + "|" + PretupsI.POS_VALUE + "|" + c2sTransferVO.getSenderMsisdn() + "|");
                sBuilder.append(c2sTransferVO.getReferenceID() + "|" + c2sTransferVO.getSenderName() + "|" + c2sTransferVO.getReceiverMsisdn() + "|");
                if (serviceType == true) {
                    sBuilder.append("|" + "|" + "|");
                } else {
                    sBuilder.append(c2sTransferVO.getBonusBundleIdS() + "|" + c2sTransferVO.getBonusBundleTypes() + "|" + c2sTransferVO.getBonusBundleValues() + "|");
                }
                sBuilder.append(c2sTransferVO.getTransferDateStr().substring(0, 10) + "|" + c2sTransferVO.getTransferValue() + "|");
                if (PretupsI.SLAVE_VALUE.equalsIgnoreCase(c2sTransferVO.getSenderCategoryCode())) {
                    sBuilder.append(c2sTransferVO.getSenderMsisdn() + "|");
                } else {
                    sBuilder.append("|");
                }
                sBuilder.append(c2sTransferVO.getTransferValueStr() + "|");
                if (serviceType == true) {
                    sBuilder.append(PretupsI.SERVICE_TYPE_VALUE + "|" + c2sTransferVO.getServiceType() + "|" + PretupsI.SERVICE_TYPE_VALUE + "|");
                } else {
                    sBuilder.append(PretupsI.RECHARGE_TYPE_VALUE + "|" + "|" + "|");
                }
                sBuilder.append(c2sTransferVO.getCellId() + "|");
                bw.write(sBuilder.toString());
                bw.newLine();
            }

            bw.close();
        } catch (IOException ex) {
            LOG.errorTrace(methodName, ex);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Error in createFile() method" + ex.getMessage());
            }
            throw new BTSLBaseException("CustomerRechargeFileCreation", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Error in createFile() method" + e.getMessage());
            }
            throw new BTSLBaseException("CustomerRechargeFileCreation", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }

    }

    /**
     * getFileFullPath()
     * 
     * @throws Exception
     *             get the path of the file where file needs to be generated.
     */
    private static String getFileFullPath() throws BTSLBaseException {
        final String methodName = "getFileFullPath";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Enter ");
        }
        String fullPath = null;
        try {
            final Calendar rightNow = Calendar.getInstance();
            fullPath = _customerRechargeFilePath + _customerRechargeFileName + rightNow.get(Calendar.YEAR) + getDoubleDigitValue(rightNow.get(Calendar.MONTH) + 1) + getDoubleDigitValue(rightNow
                .get(Calendar.DAY_OF_MONTH)) + getDoubleDigitValue(rightNow.get(Calendar.HOUR_OF_DAY)) + getDoubleDigitValue(rightNow.get(Calendar.MINUTE)) + getDoubleDigitValue(rightNow
                .get(Calendar.SECOND)) + _customerRechargeFileExt;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("CustomerRechargeFileCreation", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "File Path = " + fullPath);
            }
        }
        return fullPath;
    }

    private static String getDoubleDigitValue(int value) {
        String valueStr;
        if (value < 10) {
            valueStr = "0" + value;
        } else {
            valueStr = String.valueOf(value);
        }
        return valueStr;
    }

    /**
     * loadConstantParameters()
     * 
     * @throws Exception
     *             Load all the required parametes from the property file.
     */
    private static void loadConstantParameters() throws BTSLBaseException {
        final String methodName = "loadConstantParameters";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered: ");
        }
        try {
            _processId = Constants.getProperty("CUSTOMER_RECHARGE_PROCESS_ID");
            if (BTSLUtil.isNullString(_processId)) {
                LOG.info(methodName, " Could not find file label for transaction data in the Constants file.");
            }
            _customerRechargeFileName = Constants.getProperty("CUSTOMER_RECHARGE_FILE_NAME");
            if (BTSLUtil.isNullString(_customerRechargeFileName)) {
                LOG.info(methodName, " Could not find file label for transaction data in the Constants file.");
            }

            _customerRechargeFilePath = Constants.getProperty("CUSTOMER_RECHARGE_FILE_PATH");
            if (BTSLUtil.isNullString(_customerRechargeFilePath)) {
                LOG.info(methodName, " Could not find file label for master data in the Constants file.");
            }

            _customerRechargeFileExt = Constants.getProperty("CUSTOMER_RECHARGE_FILE_EXT");
            if (BTSLUtil.isNullString(_customerRechargeFileExt)) {
                LOG.info(methodName, " Could not find file name for transaction data in the Constants file.");
            }

            _customerRechargeEmptyFileGenerate = Constants.getProperty("CUSTOMER_RECHARGE_EMPTY_FILE_GENERATEE");
            if (BTSLUtil.isNullString(_customerRechargeEmptyFileGenerate)) {
                LOG.info(methodName, " Could not find file name for master data in the Constants file.");
            }

            _batchAllowedServices = Constants.getProperty("BATCH_ALLOWED_SERVICES_2_6_1");
            if (BTSLUtil.isNullString(_batchAllowedServices)) {
                LOG.info(methodName, " Could not find file name for master data in the Constants file.");
            }

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CustomerRechargeFileCreation.java[main]", "", "", "",
                " CustomerRechargeFileCreation process could not be executed successfully.");
            throw new BTSLBaseException("CustomerRechargeFileCreation", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }

    }

}
