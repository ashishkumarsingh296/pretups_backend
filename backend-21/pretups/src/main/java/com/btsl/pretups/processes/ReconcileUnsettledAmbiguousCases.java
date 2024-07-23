package com.btsl.pretups.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.p2p.reconciliation.businesslogic.ReconciliationBL;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.HandleUnsettledAmbiguousCaseVO;
import com.btsl.pretups.processes.businesslogic.HandleUnsettledCombinedCasesQueries;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

/**
 * @(#)HandleUnsettledCases.java
 *                               Copyright(c) 2006, Bharti Telesoft Ltd.
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
 *                               Gurjeet Bedi Sep 26, 2006 Initial Creation
 * 
 */

public class ReconcileUnsettledAmbiguousCases {
    private static ArrayList _failList = null;
    private static ArrayList _successList = null;
    private static Log _log = LogFactory.getLog(ReconcileUnsettledAmbiguousCases.class.getName());

    // To write the failure cases.
    private static File _fileObjectFail = null;
    private static FileWriter _fwriterFail = null;
    private static String failOutPutFile = "ERROR_NotProcessed";
    private static int _totalRecord = 0;
    private static int _notProcessedRecords = 0;
    private static int _totalProcessedRecords = 0;
    private static PreparedStatement _pstmtSelect = null;
    // private static PreparedStatement _pstmtC2STransferItemsSelect=null;
    private static PreparedStatement _markC2SReceiverAmbiguous = null;
    private static PreparedStatement _pstmtReconcilationStatusUpdate = null;
    // private static PreparedStatement _pstmtInsertC2STransferItems=null;
    private static PreparedStatement _pstmtpstmtVomsVOSelect = null;
    private static PreparedStatement _pstmtVoucherStatusUpdate = null;
    private static PreparedStatement _pstmtVoucherAuditStatusUpdate = null;
    private static PreparedStatement _pstmtRestrictedSubDetailSelect = null;
    private static PreparedStatement _pstmtRestrictedSubDetailSelectLock = null;
    private static PreparedStatement _pstmtRestrictedMSISDNtxnCountUpdate = null;
    private static PreparedStatement _pstmtChannelUserServiceListSelect = null;
    private static PreparedStatement _pstmtChannelUserDetailSelect = null;
    private static PreparedStatement _pstmtUserBalanceSelect = null;
    private static PreparedStatement _pstmtUserDailyBalanceInsert = null;
    private static PreparedStatement _pstmtUserDailyBalancesUpdate = null;
    private static PreparedStatement _pstmtMessageGatewayTypeSelect = null;
    private static PreparedStatement _pstmtUserProdBalanceSelect = null;
    private static PreparedStatement _pstmtUserBalanceUpdate = null;
    private static PreparedStatement _pstmtUserThreshHoldCountInsert = null;
    private static PreparedStatement _pstmtTransferProfileProductSelect = null;
    private static PreparedStatement _pstmtTransferCountsSelect = null;
    private static PreparedStatement _pstmtTransferCountsWithLockSelect = null;
    private static PreparedStatement _pstmtUserTransferCountsUpdate = null;
    private static PreparedStatement _pstmtUserTransferCountsInsert = null;
    private static PreparedStatement _pstmtTransferProfileSelect = null;
    private static PreparedStatement _pstmtdEffTrfProfileProductListSelect = null;

    private static PreparedStatement _pstmtP2PSelect = null;
    private static PreparedStatement _pstmtP2PTransferItemsSelect = null;
    private static PreparedStatement _pstmtMarkP2PReceiverAmbiguous = null;
    private static PreparedStatement _pstmtP2PReconcilationStatusUpdate = null;
    private static PreparedStatement _pstmtP2PInsertTransferItems = null;

    public static void main(String args[]) {
        BufferedReader in = null;
        Connection con = null;
        _totalRecord = 0;
        _notProcessedRecords = 0;
        _totalProcessedRecords = 0;
        _failList = null;
        _successList = null;
        final String methodName = "main";
        if (args.length != 3) {
            _log.info(methodName, "Usage : ReconcileUnsettledAmbiguousCases [Constants file] [LogConfig file] [File Name]");
            return;
        }
        final File constantsFile = new File(args[0]);
        if (!constantsFile.exists()) {
            _log.debug(methodName, "ReconcileUnsettledAmbiguousCases main() Constants file not found on location:: " + constantsFile.toString());
            return;
        }
        final File logconfigFile = new File(args[1]);
        if (!logconfigFile.exists()) {
            _log.debug(methodName, "ReconcileUnsettledAmbiguousCases main() Logconfig file not found on location:: " + logconfigFile.toString());
            return;
        }
        final File sourceFile = new File(args[2]);
        if (!sourceFile.exists()) {
            _log.debug(methodName, "ReconcileUnsettledAmbiguousCases main() sourceFile file for C2S Settlement not found on location:: " + sourceFile.toString());
            return;
        }
        try {
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            NetworkProductServiceTypeCache.refreshNetworkProductMapping();
            NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();
            final java.util.Date currentDate = new Date();
            failOutPutFile = failOutPutFile + "_" + BTSLUtil.getFileNameStringFromDate(currentDate) + ".log";
            String path = sourceFile.getPath();
            path = path.replaceFirst(".txt", "_");
            _fileObjectFail = new File(path + failOutPutFile);
            if (_fileObjectFail == null) {
                throw new BTSLBaseException("ReconcileUnsettledAmbiguousCases", "main[]", "Error creating the failure transaction file ouput file");
            }
            _fwriterFail = new FileWriter(_fileObjectFail);
            if (_fwriterFail == null) {
                throw new BTSLBaseException("ReconcileUnsettledAmbiguousCases", "main[]", "Error creating the failure transaction file ouput file");
            }
        } catch (Exception e) {
            _log.info(methodName, "ReconcileUnsettledAmbiguousCases main() Not able to load Process Cache");
            _log.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            _totalRecord = 0;
            _notProcessedRecords = 0;
            _totalProcessedRecords = 0;
            _failList = new ArrayList();
            _successList = new ArrayList();

            final ReconcileUnsettledAmbiguousCases reconcileUnsettledAmbiguousCases = new ReconcileUnsettledAmbiguousCases();
            in = new BufferedReader(new FileReader(args[2]));
            reconcileUnsettledAmbiguousCases.readDataAndPutInObject(in, args[2], ",");
            in.close();
            con = OracleUtil.getSingleConnection();
            _totalRecord = _failList.size() + _successList.size();
            if (sourceFile.getName().startsWith("RP2P")) {
                reconcileUnsettledAmbiguousCases.handleChannelAmbigousCasesRP2P(con, _failList, _successList, PretupsI.C2S_MODULE);
            }
            if (sourceFile.getName().startsWith("CP2P")) {
                reconcileUnsettledAmbiguousCases.handleChannelAmbigousCasesCP2P(con, _failList, _successList, PretupsI.P2P_MODULE);
            }
            _notProcessedRecords = _totalRecord - _totalProcessedRecords;

            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            final String arr[] = { String.valueOf(_totalRecord), String.valueOf(_totalProcessedRecords), String.valueOf(_notProcessedRecords) };
            // String
            // senderMessage=BTSLUtil.getMessage(locale,PretupsErrorCodesI.P2P_AMBIGUOUS_CASE_ALERT_MSG,arr);
            final String senderMessage = "Ambigous file: " + sourceFile.getName() + " is processed successfully Total: " + _totalRecord + ", records, Processed :" + _totalProcessedRecords + " records, Not processed:" + _notProcessedRecords + " records";
            final String msisdnString = new String(Constants.getProperty("adminmobile"));
            final String[] msisdn = msisdnString.split(",");

            for (int i = 0; i < msisdn.length; i++) {
                final PushMessage pushMessage = new PushMessage(msisdn[i], senderMessage, null, null, locale);
                pushMessage.push();
            }
        } catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);

        } catch (IOException e) {
            _log.errorTrace(methodName, e);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
        } finally {
        	try {
        		if (_fwriterFail!= null) {
        			_fwriterFail.close();
				}
				
			} catch (IOException e) {
				 _log.errorTrace(methodName, e);
			}

            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                in = null;
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Method to read the file and store in the list
     * 
     * @param p_br
     * @param p_fileName
     * @param p_separator
     */
    public void readDataAndPutInObject(BufferedReader p_br, String p_fileName, String p_separator) {
        final String methodName = "readDataAndPutInObject";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_fileName=" + p_fileName + "p_separator=" + p_separator);
        }
        String str = null;
        String transID = null;
        String status = null;
        HandleUnsettledAmbiguousCaseVO handleUnsettledCombinedAmbiguousVO = null;
        int recordCount = 0;
        try {
            while (p_br.ready()) {
                str = p_br.readLine();
                if (BTSLUtil.isNullString(str)) {
                    continue;
                }
                if (str.indexOf(p_separator) == -1) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Skipping entry (" + str + ") from " + p_fileName + " as separator (" + p_separator + ") not found");
                    }
                    continue;
                }
                if (new StringTokenizer(str, p_separator).countTokens() < 2) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Skipping entry (" + str + ") from " + p_fileName + " as Less than 2 tokens found");
                    }
                    continue;
                }
                recordCount++;
                transID = str.substring(0, str.indexOf(p_separator));
                status = str.substring(str.lastIndexOf(p_separator) + 1, str.trim().length());
                if (!"F".equalsIgnoreCase(status) && !"S".equalsIgnoreCase(status)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Skipping entry (" + str + ") from " + p_fileName + " as status can be only S or F");
                    }
                    continue;
                }
                if ("F".equalsIgnoreCase(status.trim())) {
                    handleUnsettledCombinedAmbiguousVO = new HandleUnsettledAmbiguousCaseVO();
                    handleUnsettledCombinedAmbiguousVO.setTransctionID(transID.trim());
                    handleUnsettledCombinedAmbiguousVO.setRecordCount(String.valueOf(recordCount));
                    handleUnsettledCombinedAmbiguousVO.setStatus("F");
                    _failList.add(handleUnsettledCombinedAmbiguousVO);

                } else {
                    handleUnsettledCombinedAmbiguousVO = new HandleUnsettledAmbiguousCaseVO();
                    handleUnsettledCombinedAmbiguousVO.setTransctionID(transID.trim());
                    handleUnsettledCombinedAmbiguousVO.setRecordCount(String.valueOf(recordCount));
                    handleUnsettledCombinedAmbiguousVO.setStatus("S");
                    _successList.add(handleUnsettledCombinedAmbiguousVO);
                }

            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
    }

    /**
     * Method to handle Cases RP2P : Success as well as Fail
     * 
     * @param p_con
     * @param p_failList
     * @param p_successList
     */
    public void handleChannelAmbigousCasesRP2P(Connection p_con, ArrayList p_failList, ArrayList p_successList, String p_moduleCode) {
        final String methodName = "handleChannelAmbigousCases";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_failList.size=" + p_failList.size() + " p_successList.size()=" + p_successList.size());
        }
        try {
            final HandleUnsettledCombinedCasesQueries handleUnsettledCombinedCasesQueries = new HandleUnsettledCombinedCasesQueries();
            _pstmtSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadC2STransferVO());
            // _pstmtC2STransferItemsSelect =
            // handleUnsettledCombinedCasesQueries.loadC2STransferItemsVOList(p_con);
            _markC2SReceiverAmbiguous =  p_con.prepareStatement(handleUnsettledCombinedCasesQueries.markC2SReceiverAmbiguous());
            _pstmtReconcilationStatusUpdate =  p_con.prepareStatement(handleUnsettledCombinedCasesQueries.updateReconcilationStatus());
            // _pstmtInsertC2STransferItems=handleUnsettledCombinedCasesQueries.addC2STransferItemDetailsQuery(p_con);
            _pstmtpstmtVomsVOSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadC2SVOMSDetail());
            _pstmtVoucherStatusUpdate = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.updateVoucherQuery());
            _pstmtVoucherAuditStatusUpdate = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.updateVoucherAuditQuery());
            _pstmtRestrictedSubDetailSelectLock = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadRestrictedSubscriberDetailsQueryLock());
            _pstmtRestrictedSubDetailSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadRestrictedSubscriberDetailsQuery());
            _pstmtRestrictedMSISDNtxnCountUpdate = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.updateRestrictedMSISDNtxnCounQuery());
            _pstmtChannelUserServiceListSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadUserServiceListQuery());
            _pstmtChannelUserDetailSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadChannelUserDetailQuery());
            _pstmtUserBalanceSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadUserBalanceQuery());
            _pstmtUserDailyBalanceInsert = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.addUserDailyBalancesQuery());
            _pstmtUserDailyBalancesUpdate = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.updateUserDailyBalancesQuery());
            _pstmtMessageGatewayTypeSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadMessageGatewayTypeQuery());
            _pstmtUserProdBalanceSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadUserProductBalanceQuery());
            _pstmtUserBalanceUpdate = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.updateUserBalanceQuery());
            _pstmtUserThreshHoldCountInsert = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.addUserThresholdCounterQuery());
            _pstmtTransferProfileProductSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadTransferProfileProductsQuery());
            _pstmtTransferCountsSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadTransferCountsQuery());
            _pstmtTransferCountsWithLockSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadTransferCountsWithLockQuery());
            _pstmtUserTransferCountsUpdate = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.updateUserTransferCountsQuery());
            _pstmtUserTransferCountsInsert = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.addUserTransferCountsQuery());
            _pstmtTransferProfileSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadTransferProfileQuery());
            _pstmtdEffTrfProfileProductListSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadEffTrfProfileProductListQuery());

            reconcileTransactionListRP2P(p_con, PretupsErrorCodesI.TXN_STATUS_FAIL, "Fail", p_failList, p_moduleCode);
            reconcileTransactionListRP2P(p_con, PretupsErrorCodesI.TXN_STATUS_SUCCESS, "Success", p_successList, p_moduleCode);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (_pstmtSelect != null) {
                    _pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            // try{if(_pstmtC2STransferItemsSelect!=null)
            // _pstmtC2STransferItemsSelect.close();}catch(Exception e){}
            try {
                if (_markC2SReceiverAmbiguous != null) {
                    _markC2SReceiverAmbiguous.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtReconcilationStatusUpdate != null) {
                    _pstmtReconcilationStatusUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            // try{if(_pstmtInsertC2STransferItems!=null)
            // _pstmtInsertC2STransferItems.close();}catch(Exception e){}
            try {
                if (_pstmtpstmtVomsVOSelect != null) {
                    _pstmtpstmtVomsVOSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtVoucherStatusUpdate != null) {
                    _pstmtVoucherStatusUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtVoucherAuditStatusUpdate != null) {
                    _pstmtVoucherAuditStatusUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtRestrictedSubDetailSelect != null) {
                    _pstmtRestrictedSubDetailSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtRestrictedSubDetailSelectLock != null) {
                    _pstmtRestrictedSubDetailSelectLock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtRestrictedMSISDNtxnCountUpdate != null) {
                    _pstmtRestrictedMSISDNtxnCountUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtChannelUserServiceListSelect != null) {
                    _pstmtChannelUserServiceListSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtChannelUserDetailSelect != null) {
                    _pstmtChannelUserDetailSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtUserBalanceSelect != null) {
                    _pstmtUserBalanceSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtUserDailyBalanceInsert != null) {
                    _pstmtUserDailyBalanceInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtUserDailyBalancesUpdate != null) {
                    _pstmtUserDailyBalancesUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtMessageGatewayTypeSelect != null) {
                    _pstmtMessageGatewayTypeSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtUserProdBalanceSelect != null) {
                    _pstmtUserProdBalanceSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtUserBalanceUpdate != null) {
                    _pstmtUserBalanceUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtUserThreshHoldCountInsert != null) {
                    _pstmtUserThreshHoldCountInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtTransferProfileProductSelect != null) {
                    _pstmtTransferProfileProductSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtTransferCountsSelect != null) {
                    _pstmtTransferCountsSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtTransferCountsWithLockSelect != null) {
                    _pstmtTransferCountsWithLockSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtUserTransferCountsUpdate != null) {
                    _pstmtUserTransferCountsUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtUserTransferCountsInsert != null) {
                    _pstmtUserTransferCountsInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtTransferProfileSelect != null) {
                    _pstmtTransferProfileSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtdEffTrfProfileProductListSelect != null) {
                    _pstmtdEffTrfProfileProductListSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("handleChannelAmbigousCasesRP2P", "Exiting ");
            }
        }// end of finally
    }

    /**
     * Method to handle Cases CP2P : Success as well as Fail
     * 
     * @param p_con
     * @param p_failList
     * @param p_successList
     */
    public void handleChannelAmbigousCasesCP2P(Connection p_con, ArrayList p_failList, ArrayList p_successList, String p_moduleCode) {
        final String methodName = "handleChannelAmbigousCases";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_failList.size=" + p_failList.size() + " p_successList.size()=" + p_successList.size());
        }
        try {
            final HandleUnsettledCombinedCasesQueries handleUnsettledCombinedCasesQueries = new HandleUnsettledCombinedCasesQueries();
            _pstmtP2PSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadP2PReconciliationVO());
            _pstmtP2PTransferItemsSelect = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.loadP2PReconciliationItemsList());
            _pstmtMarkP2PReceiverAmbiguous = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.markP2PReceiverAmbiguous());
            _pstmtP2PReconcilationStatusUpdate = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.updateP2PReconcilationStatus());
            _pstmtP2PInsertTransferItems = p_con.prepareStatement(handleUnsettledCombinedCasesQueries.addP2PTransferItemDetailsQuery());

            reconcileTransactionListCP2P(p_con, PretupsErrorCodesI.TXN_STATUS_FAIL, "Fail", p_failList, p_moduleCode);
            reconcileTransactionListCP2P(p_con, PretupsErrorCodesI.TXN_STATUS_SUCCESS, "Success", p_successList, p_moduleCode);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (_pstmtP2PSelect != null) {
                    _pstmtP2PSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtP2PTransferItemsSelect != null) {
                    _pstmtP2PTransferItemsSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtMarkP2PReceiverAmbiguous != null) {
                    _pstmtMarkP2PReceiverAmbiguous.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtP2PReconcilationStatusUpdate != null) {
                    _pstmtP2PReconcilationStatusUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (_pstmtP2PInsertTransferItems != null) {
                    _pstmtP2PInsertTransferItems.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("handleChannelAmbigousCasesCP2P", "Exiting ");
            }
        }// end of finally
    }

    /**
     * Method to perform reconcilation process
     * 
     * @param p_con
     * @param p_status
     * @param p_list
     */
    public void reconcileTransactionListRP2P(Connection p_con, String p_status, String p_statusTxt, ArrayList p_list, String p_moduleCode) {
        final String methodName = "reconcileTransactionListRP2P";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_status=" + p_status + " p_statusTxt=" + p_statusTxt + " p_list.size=" + p_list.size());
        }
        try {
            String transactionID = null;
            C2STransferVO c2sTransferVO = null;
            C2STransferItemVO receiverItemVO = null;
            int updateCount = 0;
            final java.util.Date currentDate = new Date();
            final HandleUnsettledCombinedCasesQueries handleUnsettledCombinedCasesQueries = new HandleUnsettledCombinedCasesQueries();

            HandleUnsettledAmbiguousCaseVO handleUnsettledCombinedAmbiguousVO = null;
            long t = 0;
            long p = 0;
            int listSizes = p_list.size();
            for (int i = 0; i < listSizes ; i++) {
                handleUnsettledCombinedAmbiguousVO = (HandleUnsettledAmbiguousCaseVO) p_list.get(i);
                transactionID = handleUnsettledCombinedAmbiguousVO.get_transctionID();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Got transactionID=" + transactionID);
                }
                updateCount = 0;
                receiverItemVO = null;
                t = System.currentTimeMillis();
                try {
                    c2sTransferVO = loadC2STransferVO(transactionID, _pstmtSelect);
                    if (c2sTransferVO == null) {
                        writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[reconcileTransactionListRP2P]", transactionID,
                            "No information available in transfers table or already settled");
                        _log.info(methodName, "For transactionID=" + transactionID + " No information available in transfers table or already settled");
                        continue;
                        // end of logic
                    }
                    if (PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equals(c2sTransferVO.getTxnStatus())) {
                        updateCount = markC2SReceiverAmbiguous(c2sTransferVO.getTransferID(), _markC2SReceiverAmbiguous);
                        receiverItemVO = (C2STransferItemVO) c2sTransferVO.getTransferItemList().get(1);
                        receiverItemVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
                    }
                    c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    c2sTransferVO.setTransferStatus(p_status);
                    c2sTransferVO.setModifiedOn(currentDate);
                    final ArrayList newEntries = handleUnsettledCombinedCasesQueries.prepareNewC2SReconList(p_con, c2sTransferVO, p_statusTxt, null,
                        _pstmtRestrictedSubDetailSelectLock, _pstmtRestrictedSubDetailSelect, _pstmtRestrictedMSISDNtxnCountUpdate, _pstmtChannelUserDetailSelect,
                        _pstmtChannelUserServiceListSelect, _pstmtUserBalanceSelect, _pstmtUserDailyBalanceInsert, _pstmtUserDailyBalancesUpdate,
                        _pstmtMessageGatewayTypeSelect, _pstmtUserProdBalanceSelect, _pstmtUserBalanceUpdate, _pstmtUserThreshHoldCountInsert,
                        _pstmtTransferProfileProductSelect, _pstmtTransferCountsSelect, _pstmtTransferCountsWithLockSelect, _pstmtUserTransferCountsUpdate,
                        _pstmtUserTransferCountsInsert, _pstmtTransferProfileSelect, _pstmtdEffTrfProfileProductListSelect);
                    c2sTransferVO.setTransferItemList(newEntries);

                    if (c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                        final VomsVoucherVO vomsVoucherVO = loadVomsVoucherVO(c2sTransferVO, _pstmtpstmtVomsVOSelect);
                        updateCount = updateVoucherStatus(p_statusTxt, c2sTransferVO, vomsVoucherVO, _pstmtVoucherStatusUpdate, _pstmtVoucherAuditStatusUpdate);
                    }

                    updateCount = updateReconcilationStatus(c2sTransferVO, _pstmtReconcilationStatusUpdate);
                    
                    if (updateCount > 0) {
                        p_con.commit();
                        _totalProcessedRecords++;
                        if (c2sTransferVO.getOtherInfo1() != null) {
                            BalanceLogger.log((UserBalancesVO) c2sTransferVO.getOtherInfo1());
                        }
                        // if differential commission is given by the
                        // reconciliation then add the balance logger into the
                        // system.
                        if (c2sTransferVO.getOtherInfo2() != null) {
                            BalanceLogger.log((UserBalancesVO) c2sTransferVO.getOtherInfo2());
                        }
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "TransactionID=" + transactionID + " Succesfully Settled to status=" + p_status);
                        }
                    } else {
                        p_con.rollback();
                        _log.error(methodName, "TransactionID=" + transactionID + " Not able Settled to status=" + p_status);
                        writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[reconcileTransactionListRP2P]", transactionID,
                            "Not able Settled to status=" + p_status);
                    }
                    p_con.rollback();
                    p = System.currentTimeMillis();
                    _log.debug(methodName,
                        "TIME_TAKEN Transaction_ID:" + transactionID + ", Status:" + p_statusTxt + ", Start Time:" + t + ", End Time:" + p + ", Total Time:" + (p - t));
                } catch (BTSLBaseException be) {
                    _log.errorTrace(methodName, be);
                    p_con.rollback();
                    writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[reconcileTransactionListRP2P]", transactionID,
                        " Not able Settled to status=" + p_status + " getting Exception=" + be.getMessage());
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    p_con.rollback();
                    writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[reconcileTransactionListRP2P]", transactionID,
                        " Not able Settled to status=" + p_status + " getting Exception=" + e.getMessage());
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }// end of finally
    }

    /**
     * Method to perform reconcilation process
     * 
     * @param p_con
     * @param p_status
     * @param p_list
     */

    public void reconcileTransactionListCP2P(Connection p_con, String p_status, String p_statusTxt, ArrayList p_list, String p_moduleCode) {
        final String methodName = "reconcileTransactionListRP2P";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_status=" + p_status + " p_statusTxt=" + p_statusTxt + " p_list.size=" + p_list.size());
        }
        try {
            String transactionID = null;
            P2PTransferVO p2pTransferVO = null;
            TransferItemVO receiverItemVO = null;
            int updateCount = 0;
            final java.util.Date currentDate = new Date();
            HandleUnsettledAmbiguousCaseVO handleUnsettledCombinedAmbiguousVO = null;
            long t = 0;
            long p = 0;
            int listSizes = p_list.size();
            for (int i = 0; i < listSizes; i++) {
                handleUnsettledCombinedAmbiguousVO = (HandleUnsettledAmbiguousCaseVO) p_list.get(i);
                transactionID = handleUnsettledCombinedAmbiguousVO.get_transctionID();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Got transactionID=" + transactionID);
                }
                updateCount = 0;
                receiverItemVO = null;
                try {
                    t = System.currentTimeMillis();
                    p2pTransferVO = loadP2PReconciliationVO(transactionID, _pstmtP2PSelect, _pstmtP2PTransferItemsSelect);
                    if (p2pTransferVO == null) {
                        _log.info(methodName, "For transactionID=" + transactionID + " No information available in transfers table or already settled");
                        writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[reconcileTransactionListCP2P]", transactionID,
                            "No information available in transfers table or already settled");
                        continue;
                    }
                    if (PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equals(p2pTransferVO.getTxnStatus())) {
                        updateCount = markP2PReceiverAmbiguous(transactionID, _pstmtMarkP2PReceiverAmbiguous);
                        receiverItemVO = (TransferItemVO) p2pTransferVO.getTransferItemList().get(1);
                        receiverItemVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
                    }

                    p2pTransferVO.setTransferStatus(p_status);
                    p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    p2pTransferVO.setModifiedOn(currentDate);

                    final ArrayList newEntries = ReconciliationBL.prepareNewList(p2pTransferVO, p2pTransferVO.getTransferItemList(), p_statusTxt, null);
                    p2pTransferVO.setTransferItemList(newEntries);
                    
                    if (p2pTransferVO.getServiceType().equals("VCN")) {
                        final VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
                        final VomsVoucherVO vomsVoucherVO = vomsVoucherDAO.loadVomsVoucherVO(p_con, p2pTransferVO);
                        updateCount = vomsVoucherDAO.updateVoucherStatus(p_con, p_statusTxt, p2pTransferVO, vomsVoucherVO);
                    }
                    
                    updateCount = updateP2PReconcilationStatus(p2pTransferVO, _pstmtP2PReconcilationStatusUpdate, _pstmtP2PInsertTransferItems);
                    if (updateCount > 0) {
                        p_con.commit();
                        _totalProcessedRecords++;
                        _log.info(methodName, "TransactionID=" + transactionID + " Succesfully Settled to status=" + p_status);
                    } else {
                        p_con.rollback();
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "TransactionID=" + transactionID + " Not able Settled to status=" + p_status);
                        }
                        writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[reconcileTransactionListCP2P]", transactionID,
                            "Not able Settled to status=" + p_status);
                    }
                    p_con.rollback();
                    p = System.currentTimeMillis();
                    _log.debug(methodName,
                        "TIME_TAKEN Transaction_ID:" + transactionID + ", Status:" + p_statusTxt + ", Start Time:" + t + ", End Time:" + p + ", Total Time:" + (p - t));
                } catch (BTSLBaseException be) {
                    _log.errorTrace(methodName, be);
                    p_con.rollback();
                    writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[reconcileTransactionListCP2P]", transactionID, "getting Exception=" + be.getMessage());
                    _log.info(methodName, "TransactionID=" + transactionID + " Not able Settled to status=" + p_status + " getting BTSL Exception=" + be.getMessage());
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    p_con.rollback();
                    _log.info(methodName, "TransactionID=" + transactionID + " Not able Settled to status=" + p_status + " getting Exception=" + e.getMessage());
                    writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[reconcileTransactionListCP2P]", transactionID, "getting Exception=" + e.getMessage());
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }// end of finally
    }

    /**
     * Method to load the C2STransferVO based on transfer ID
     * 
     * @param p_con
     * @param p_transferID
     * @return
     */
    public C2STransferVO loadC2STransferVO(String p_transferID, PreparedStatement pstmtSelect) throws BTSLBaseException {
        final String methodName = "loadC2STransferVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID=" + p_transferID);
        }
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        ChannelUserVO channelUserVO = null;
        UserPhoneVO userPhoneVO = null;
        try {
            int i = 1;
            if (pstmtSelect != null) {
                pstmtSelect.clearParameters();
                pstmtSelect.setString(i++, p_transferID);
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
                pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
                pstmtSelect.setString(i++, PretupsI.KEY_VALUE_TYPE_REOCN);
                pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
                pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
                rs = pstmtSelect.executeQuery();
            }
            if (rs != null && rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setProductName(rs.getString("short_name"));
                c2sTransferVO.setServiceName(rs.getString("name"));
                c2sTransferVO.setSenderName(rs.getString("user_name"));
                c2sTransferVO.setOwnerUserID(rs.getString("owner_id"));
                c2sTransferVO.setErrorMessage(rs.getString("value"));
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
                c2sTransferVO.setProductCode(rs.getString("product_code"));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setSenderNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                c2sTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
                c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                c2sTransferVO.setErrorCode(rs.getString("error_code"));
                c2sTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                c2sTransferVO.setReferenceID(rs.getString("reference_id"));
                c2sTransferVO.setServiceType(rs.getString("service_type"));
                c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
                c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                c2sTransferVO.setLanguage(rs.getString("language"));
                c2sTransferVO.setCountry(rs.getString("country"));
                c2sTransferVO.setSkey(rs.getLong("skey"));
                c2sTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                c2sTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                c2sTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                c2sTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                c2sTransferVO.setQuantity(rs.getLong("quantity"));
                c2sTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                c2sTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                c2sTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                c2sTransferVO.setCreatedOn(rs.getDate("created_on"));
                c2sTransferVO.setCreatedBy(rs.getString("created_by"));
                c2sTransferVO.setModifiedOn(rs.getDate("modified_on"));
                c2sTransferVO.setModifiedBy(rs.getString("modified_by"));
                c2sTransferVO.setTransferStatus(rs.getString("txn_status"));
                c2sTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                c2sTransferVO.setVersion(rs.getString("version"));
                c2sTransferVO.setCardGroupID(rs.getString("card_group_id"));
                c2sTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                c2sTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                c2sTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                c2sTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                c2sTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                c2sTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                c2sTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                c2sTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                c2sTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                c2sTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                c2sTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                c2sTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                c2sTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                c2sTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                c2sTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                c2sTransferVO.setDifferentialGiven(rs.getString("differential_given"));
                c2sTransferVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                c2sTransferVO.setTxnStatus(rs.getString("transfer_status"));
                c2sTransferVO.setSourceType(rs.getString("source_type"));
                channelUserVO = new ChannelUserVO();
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("commission_profile_id"));
                channelUserVO.setCategoryCode(rs.getString("sender_category"));
                userPhoneVO = new UserPhoneVO();
                userPhoneVO.setCountry(rs.getString("phcountry"));
                userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
                userPhoneVO.setMsisdn(rs.getString("msisdn"));
                userPhoneVO.setLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                c2sTransferVO.setSenderVO(channelUserVO);
                c2sTransferVO.setSubService(rs.getString("sub_service"));
                // loading items
                final ArrayList c2sTransferItemsVOList = new ArrayList();
                final C2STransferItemVO senderItemVO = new C2STransferItemVO();
                senderItemVO.setTransferID(rs.getString("transfer_id"));
                senderItemVO.setMsisdn(rs.getString("sender_msisdn"));
                senderItemVO.setEntryDate(rs.getDate("created_on"));
                senderItemVO.setRequestValue(rs.getLong("transfer_value"));
                senderItemVO.setPreviousBalance(rs.getLong("sender_previous_balance"));
                senderItemVO.setPostBalance(rs.getLong("sender_post_balance"));
                senderItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
                senderItemVO.setTransferType(rs.getString("transfer_type_value"));
                senderItemVO.setEntryType(PretupsI.DEBIT);
                // c2sTransferItemVO.setValidationStatus(rs.getString("validation_status"));
                senderItemVO.setUpdateStatus(rs.getString("debit_status"));
                senderItemVO.setTransferValue(rs.getLong("transfer_value"));
                senderItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                // c2sTransferItemVO.setInterfaceType(rs.getString("interface_type"));
                // c2sTransferItemVO.setInterfaceID(rs.getString("interface_id"));
                // c2sTransferItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                // c2sTransferItemVO.setInResponseCodeDesc(rs.getString("in_response_code_desc"));
                // c2sTransferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                // c2sTransferItemVO.setSubscriberType(rs.getString("subscriber_type"));
                // c2sTransferItemVO.setServiceClassCode(rs.getString("service_class_code"));
                // c2sTransferItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                // c2sTransferItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                senderItemVO.setTransferStatus(rs.getString("debit_status"));
                // c2sTransferItemVO.setTransferDate(rs.getDate("transfer_date"));
                // c2sTransferItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                senderItemVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                // c2sTransferItemVO.setFirstCall(rs.getString("first_call"));
                senderItemVO.setSNo(1);
                // c2sTransferItemVO.setPrefixID(rs.getLong("prefix_id"));
                // c2sTransferItemVO.setServiceClass(rs.getString("service_class_id"));
                // c2sTransferItemVO.setProtocolStatus(rs.getString("protocol_status"));
                // c2sTransferItemVO.setAccountStatus(rs.getString("account_status"));
                senderItemVO.setReferenceID(rs.getString("reference_id"));
                senderItemVO.setLanguage(rs.getString("language"));
                senderItemVO.setCountry(rs.getString("country"));
                senderItemVO.setTransferStatusMessage(rs.getString("txn_status"));
                c2sTransferItemsVOList.add(senderItemVO);

                final C2STransferItemVO receiverItemVO = new C2STransferItemVO();
                receiverItemVO.setTransferID(rs.getString("transfer_id"));
                receiverItemVO.setMsisdn(rs.getString("receiver_msisdn"));
                receiverItemVO.setEntryDate(rs.getDate("created_on"));
                receiverItemVO.setRequestValue(rs.getLong("transfer_value"));
                receiverItemVO.setPreviousBalance(rs.getLong("receiver_previous_balance"));
                receiverItemVO.setPostBalance(rs.getLong("receiver_post_balance"));
                receiverItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
                receiverItemVO.setTransferType(rs.getString("transfer_type_value"));
                receiverItemVO.setEntryType(PretupsI.CREDIT);
                receiverItemVO.setValidationStatus(rs.getString("validation_status"));
                receiverItemVO.setUpdateStatus(rs.getString("credit_status"));
                receiverItemVO.setTransferValue(rs.getLong("transfer_value"));
                receiverItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                receiverItemVO.setInterfaceType(rs.getString("interface_type"));
                receiverItemVO.setInterfaceID(rs.getString("interface_id"));
                receiverItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                receiverItemVO.setInResponseCodeDesc(rs.getString("in_response_code_desc"));
                receiverItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                receiverItemVO.setSubscriberType(rs.getString("subscriber_type"));
                receiverItemVO.setServiceClassCode(rs.getString("service_class_code"));
                receiverItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                receiverItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                receiverItemVO.setTransferStatus(rs.getString("credit_status"));
                receiverItemVO.setTransferDate(rs.getDate("transfer_date"));
                receiverItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                receiverItemVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                // receiverItemVO.setFirstCall(rs.getString("first_call"));
                receiverItemVO.setSNo(2);
                receiverItemVO.setPrefixID(rs.getLong("prefix_id"));
                receiverItemVO.setServiceClass(rs.getString("service_class_id"));
                receiverItemVO.setProtocolStatus(rs.getString("protocol_status"));
                receiverItemVO.setAccountStatus(rs.getString("account_status"));
                receiverItemVO.setReferenceID(rs.getString("reference_id"));
                receiverItemVO.setLanguage(rs.getString("language"));
                receiverItemVO.setCountry(rs.getString("country"));
                receiverItemVO.setTransferStatusMessage(rs.getString("txn_status"));
                c2sTransferItemsVOList.add(receiverItemVO);

                final String crdt_bk_status = rs.getString("credit_back_status");

                if (!BTSLUtil.isNullString(crdt_bk_status)) {

                    final C2STransferItemVO creditBackItemVO = new C2STransferItemVO();
                    creditBackItemVO.setTransferID(rs.getString("transfer_id"));
                    creditBackItemVO.setMsisdn(rs.getString("sender_msisdn"));
                    creditBackItemVO.setEntryDate(rs.getDate("created_on"));
                    creditBackItemVO.setRequestValue(rs.getLong("transfer_value"));
                    creditBackItemVO.setPreviousBalance(rs.getLong("SENDER_CR_BK_PREV_BAL"));
                    creditBackItemVO.setPostBalance(rs.getLong("SENDER_CR_BK_POST_BAL"));
                    creditBackItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
                    creditBackItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
                    creditBackItemVO.setEntryType(PretupsI.CREDIT);
                    // c2sTransferItemVO.setValidationStatus(rs.getString("validation_status"));
                    creditBackItemVO.setUpdateStatus(rs.getString("credit_back_status"));
                    creditBackItemVO.setTransferValue(rs.getLong("transfer_value"));
                    creditBackItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                    // c2sTransferItemVO.setInterfaceType(rs.getString("interface_type"));
                    // c2sTransferItemVO.setInterfaceID(rs.getString("interface_id"));
                    // c2sTransferItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                    // c2sTransferItemVO.setInResponseCodeDesc(rs.getString("in_response_code_desc"));
                    // c2sTransferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                    // c2sTransferItemVO.setSubscriberType(rs.getString("subscriber_type"));
                    // c2sTransferItemVO.setServiceClassCode(rs.getString("service_class_code"));
                    // c2sTransferItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                    // c2sTransferItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                    creditBackItemVO.setTransferStatus(rs.getString("credit_back_status"));
                    // c2sTransferItemVO.setTransferDate(rs.getDate("transfer_date"));
                    // c2sTransferItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                    creditBackItemVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                    // c2sTransferItemVO.setFirstCall(rs.getString("first_call"));
                    creditBackItemVO.setSNo(3);
                    // c2sTransferItemVO.setPrefixID(rs.getLong("prefix_id"));
                    // c2sTransferItemVO.setServiceClass(rs.getString("service_class_id"));
                    // c2sTransferItemVO.setProtocolStatus(rs.getString("protocol_status"));
                    // c2sTransferItemVO.setAccountStatus(rs.getString("account_status"));
                    creditBackItemVO.setReferenceID(rs.getString("reference_id"));
                    creditBackItemVO.setLanguage(rs.getString("language"));
                    creditBackItemVO.setCountry(rs.getString("country"));
                    creditBackItemVO.setTransferStatusMessage(rs.getString("txn_status"));
                    c2sTransferItemsVOList.add(creditBackItemVO);

                }

                // c2sTransferVO.setTransferItemList(loadC2STransferItemsVOList(p_transferID,pstmtC2STransferItemsSelect));
                c2sTransferVO.setTransferItemList(c2sTransferItemsVOList);

            }
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[loadC2STransferVO]", p_transferID, " SQL Exception:" + sqle.getMessage());
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[loadC2STransferVO]", p_transferID, " Exception:" + e.getMessage());
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }// end of finally

        return c2sTransferVO;
    }

    /**
     * Method to load the P2PTransferVO based on transfer ID
     * 
     * @param p_con
     * @param p_transferID
     * @return
     */

    public P2PTransferVO loadP2PReconciliationVO(String p_transferID, PreparedStatement p_pstmtSelect, PreparedStatement p_pstmtP2PTransferItemsSelect) throws BTSLBaseException {
        final String methodName = "loadP2PReconciliationVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID=" + p_transferID);
        }
        ResultSet rs = null;
        P2PTransferVO p2pTransferVO = null;
        try {
            int i = 1;
            if (p_pstmtSelect != null) {
                p_pstmtSelect.clearParameters();
                p_pstmtSelect.setString(i++, PretupsI.P2P_ERRCODE_VALUS);
                p_pstmtSelect.setString(i++, PretupsI.KEY_VALUE_TYPE_REOCN);
                p_pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
                p_pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
                p_pstmtSelect.setString(i++, p_transferID);
                rs = p_pstmtSelect.executeQuery();
            }
            while (rs != null && rs.next()) {
                p2pTransferVO = new P2PTransferVO();
                p2pTransferVO.setProductName(rs.getString("short_name"));
                p2pTransferVO.setServiceName(rs.getString("name"));
                p2pTransferVO.setSenderName(rs.getString("user_name"));
                p2pTransferVO.setErrorMessage(rs.getString("value"));
                p2pTransferVO.setTransferID(rs.getString("transfer_id"));
                p2pTransferVO.setTransferDate(rs.getDate("transfer_date"));
                p2pTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                p2pTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                p2pTransferVO.setNetworkCode(rs.getString("network_code"));
                p2pTransferVO.setSenderID(rs.getString("sender_id"));
                p2pTransferVO.setProductCode(rs.getString("product_code"));
                p2pTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                p2pTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                p2pTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                p2pTransferVO.setTransferValue(rs.getLong("transfer_value"));
                p2pTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                p2pTransferVO.setErrorCode(rs.getString("error_code"));
                p2pTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                p2pTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                p2pTransferVO.setReferenceID(rs.getString("reference_id"));
                p2pTransferVO.setPaymentMethodType(rs.getString("payment_method_type"));
                p2pTransferVO.setServiceType(rs.getString("service_type"));
                p2pTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                p2pTransferVO.setLanguage(rs.getString("language"));
                p2pTransferVO.setCountry(rs.getString("country"));
                p2pTransferVO.setSkey(rs.getLong("skey"));
                p2pTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                p2pTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                p2pTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                p2pTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                p2pTransferVO.setQuantity(rs.getLong("quantity"));
                p2pTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                p2pTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                p2pTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                p2pTransferVO.setCreatedOn(rs.getDate("created_on"));
                p2pTransferVO.setCreatedBy(rs.getString("created_by"));
                p2pTransferVO.setModifiedOn(rs.getDate("modified_on"));
                p2pTransferVO.setModifiedBy(rs.getString("modified_by"));
                p2pTransferVO.setTransferStatus(rs.getString("txn_status"));
                p2pTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                p2pTransferVO.setVersion(rs.getString("version"));
                p2pTransferVO.setCardGroupID(rs.getString("card_group_id"));
                p2pTransferVO.setSenderAccessFee(rs.getLong("sender_access_fee"));
                p2pTransferVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                p2pTransferVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                p2pTransferVO.setSenderTax1Value(rs.getLong("sender_tax1_value"));
                p2pTransferVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                p2pTransferVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                p2pTransferVO.setSenderTax2Value(rs.getLong("sender_tax2_value"));
                p2pTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                p2pTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                p2pTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                p2pTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                p2pTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                p2pTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                p2pTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                p2pTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                p2pTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                p2pTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                p2pTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                p2pTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                p2pTransferVO.setTransferCategory(rs.getString("transfer_category"));
                p2pTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                p2pTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                p2pTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                p2pTransferVO.setTxnStatus(rs.getString("transfer_status"));
                p2pTransferVO.setSerialNumber(rs.getString("VOUCHER_SERIAL_NUMBER"));
                p2pTransferVO.setServiceType(rs.getString("SERVICE_TYPE"));
                p2pTransferVO.setTransferItemList(loadP2PReconciliationItemsList(p_transferID, p_pstmtP2PTransferItemsSelect));
            }

        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[loadP2PReconciliationVO]", p_transferID, "SQLException: " + sqle.getMessage());
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[loadP2PReconciliationVO]", p_transferID, "Exception: " + e.getMessage());
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }// end of finally

        return p2pTransferVO;

    }

    public int markC2SReceiverAmbiguous(String p_transferID, PreparedStatement p_markC2SReceiverAmbiguous) throws BTSLBaseException {
        final String methodName = "markC2SReceiverAmbiguous";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID:" + p_transferID);
        }

        int updateCount = 0;
        try {
            int i = 1;
            if (p_markC2SReceiverAmbiguous != null) {
                p_markC2SReceiverAmbiguous.clearParameters();
                p_markC2SReceiverAmbiguous.setString(i++, InterfaceErrorCodesI.AMBIGOUS);
                p_markC2SReceiverAmbiguous.setString(i++, p_transferID);
                p_markC2SReceiverAmbiguous.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
                // p_markC2SReceiverAmbiguous.setString(i++,
                // PretupsI.USER_TYPE_RECEIVER);
                updateCount = p_markC2SReceiverAmbiguous.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with updatation in partitioned table in postgres
            }
            if (updateCount <= 0) {
                writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[markC2SReceiverAmbiguous]", p_transferID, "SQL Processing Error");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[markC2SReceiverAmbiguous]", p_transferID, "SQLException " + sqle.getMessage());
            updateCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[markC2SReceiverAmbiguous]", p_transferID, "Exception " + e.getMessage());
            updateCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            // try{if (markC2SReceiverAmbiguous !=
            // null)markC2SReceiverAmbiguous.close();} catch (Exception e){}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    // public ArrayList loadC2STransferItemsVOList(String
    // p_transferID,PreparedStatement pstmtC2STransferItemsSelect) throws
    // BTSLBaseException
    // {
    //
    // if(_log.isDebugEnabled())
    // _log.debug("loadC2STransferItemsVOList","Entered p_transferID="+p_transferID);
    // ResultSet rs=null;
    // C2STransferItemVO c2sTransferItemVO=null;
    // ArrayList c2sTransferItemsVOList=new ArrayList();
    // try
    // {
    // int i=1;
    // if (pstmtC2STransferItemsSelect != null)
    // pstmtC2STransferItemsSelect.clearParameters();
    // pstmtC2STransferItemsSelect.setString(i++, p_transferID);
    // pstmtC2STransferItemsSelect.setString(i++,
    // PretupsI.KEY_VALUE_C2C_STATUS);
    // pstmtC2STransferItemsSelect.setString(i++,
    // PretupsI.KEY_VALUE_C2C_STATUS);
    // pstmtC2STransferItemsSelect.setString(i++,
    // PretupsI.KEY_VALUE_IN_RESPONSE_CODE);
    // rs = pstmtC2STransferItemsSelect.executeQuery();
    // while(rs.next())
    // {
    // c2sTransferItemVO=new C2STransferItemVO();
    // c2sTransferItemVO.setTransferID(rs.getString("transfer_id"));
    // c2sTransferItemVO.setMsisdn(rs.getString("msisdn"));
    // c2sTransferItemVO.setEntryDate(rs.getDate("entry_date"));
    // c2sTransferItemVO.setRequestValue(rs.getLong("request_value"));
    // c2sTransferItemVO.setPreviousBalance(rs.getLong("previous_balance"));
    // c2sTransferItemVO.setPostBalance(rs.getLong("post_balance"));
    // c2sTransferItemVO.setUserType(rs.getString("user_type"));
    // c2sTransferItemVO.setTransferType(rs.getString("transfer_type_value"));
    // c2sTransferItemVO.setEntryType(rs.getString("entry_type"));
    // c2sTransferItemVO.setValidationStatus(rs.getString("validation_status"));
    // c2sTransferItemVO.setUpdateStatus(rs.getString("update_status"));
    // c2sTransferItemVO.setTransferValue(rs.getLong("transfer_value"));
    // c2sTransferItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
    // c2sTransferItemVO.setInterfaceType(rs.getString("interface_type"));
    // c2sTransferItemVO.setInterfaceID(rs.getString("interface_id"));
    // c2sTransferItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
    // c2sTransferItemVO.setInResponseCodeDesc(rs.getString("in_response_code_desc"));
    // c2sTransferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
    // c2sTransferItemVO.setSubscriberType(rs.getString("subscriber_type"));
    // c2sTransferItemVO.setServiceClassCode(rs.getString("service_class_code"));
    // c2sTransferItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
    // c2sTransferItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
    // c2sTransferItemVO.setTransferStatus(rs.getString("transfer_status"));
    // c2sTransferItemVO.setTransferDate(rs.getDate("transfer_date"));
    // c2sTransferItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
    // c2sTransferItemVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("entry_date_time")));
    // c2sTransferItemVO.setFirstCall(rs.getString("first_call"));
    // c2sTransferItemVO.setSNo(rs.getInt("sno"));
    // c2sTransferItemVO.setPrefixID(rs.getLong("prefix_id"));
    // c2sTransferItemVO.setServiceClass(rs.getString("service_class_id"));
    // c2sTransferItemVO.setProtocolStatus(rs.getString("protocol_status"));
    // c2sTransferItemVO.setAccountStatus(rs.getString("account_status"));
    // c2sTransferItemVO.setReferenceID(rs.getString("reference_id"));
    // c2sTransferItemVO.setLanguage(rs.getString("language"));
    // c2sTransferItemVO.setCountry(rs.getString("country"));
    // c2sTransferItemVO.setTransferStatusMessage(rs.getString("value"));
    // c2sTransferItemsVOList.add(c2sTransferItemVO);
    // }
    //
    // }//end of try
    // catch (SQLException sqle)
    // {
    // _log.error("loadC2STransferItemsVOList","SQLException "+sqle.getMessage());
    // writeOutPutFile(_fwriterFail,"ReconcileUnsettledAmbiguousCases[loadC2STransferItemsVOList]",p_transferID," SQL Exception:"+sqle.getMessage());
    // sqle.printStackTrace();
    // throw new BTSLBaseException(this, "loadC2STransferItemsVOList",
    // "error.general.sql.processing");
    // }//end of catch
    // catch (Exception e)
    // {
    // _log.error("loadC2STransferItemsVOList","Exception "+e.getMessage());
    // writeOutPutFile(_fwriterFail,"ReconcileUnsettledAmbiguousCases[loadC2STransferItemsVOList]",p_transferID," Exception:"+e.getMessage());
    // e.printStackTrace();
    // throw new BTSLBaseException(this, "loadC2STransferItemsVOList",
    // "error.general.processing");
    // }//end of catch
    // finally
    // {
    // try{if(rs!=null) rs.close();}catch(Exception e){}
    // //try{if(pstmtC2STransferItemsSelect!=null)
    // pstmtC2STransferItemsSelect.close();}catch(Exception e){}
    // if(_log.isDebugEnabled())_log.debug("loadC2STransferItemsVOList","Exiting c2sTransferItemsVOList.size()="+c2sTransferItemsVOList.size());
    // }//end of finally
    //
    // return c2sTransferItemsVOList;
    // }

    public int updateReconcilationStatus(C2STransferVO p_c2sTransferVO, PreparedStatement p_pstmtReconcilationStatusUpdate) throws BTSLBaseException {
        final String methodName = "updateReconcilationStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_c2sTransferVO=" + p_c2sTransferVO);
        }
        int updateCount = 0;
        C2STransferItemVO reconcileVO = null;
        try {

            int i = 1;
            if (p_pstmtReconcilationStatusUpdate != null) {
                p_pstmtReconcilationStatusUpdate.clearParameters();
                p_pstmtReconcilationStatusUpdate.setString(i++, p_c2sTransferVO.getTransferStatus());
                p_pstmtReconcilationStatusUpdate.setString(i++, p_c2sTransferVO.getModifiedBy());
                p_pstmtReconcilationStatusUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
                p_pstmtReconcilationStatusUpdate.setString(i++, p_c2sTransferVO.getModifiedBy());
                p_pstmtReconcilationStatusUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
                p_pstmtReconcilationStatusUpdate.setString(i++, p_c2sTransferVO.getCreditBackStatus());

                // C2STransferItemVO reconcileVO=
                // (C2STransferItemVO)p_c2sTransferVO.getTransferItemList().get(0);
                if (p_c2sTransferVO.getTransferItemList().size() > 0) {
                    reconcileVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0);
                }
                if (reconcileVO != null) {
                    p_pstmtReconcilationStatusUpdate.setLong(i++, reconcileVO.getPreviousBalance());
                    p_pstmtReconcilationStatusUpdate.setLong(i++, reconcileVO.getPostBalance());
                    p_pstmtReconcilationStatusUpdate.setString(i++, reconcileVO.getEntryType());
                } else {
                    p_pstmtReconcilationStatusUpdate.setNull(i++, Types.NUMERIC);
                    p_pstmtReconcilationStatusUpdate.setNull(i++, Types.NUMERIC);
                    p_pstmtReconcilationStatusUpdate.setNull(i++, Types.CHAR);
                }
                //Handling of Adjustment on transactions
                if(p_c2sTransferVO.getDifferentialApplicable() != null ){
                	p_pstmtReconcilationStatusUpdate.setString(i++, p_c2sTransferVO.getDifferentialApplicable());
                } else {
                	p_pstmtReconcilationStatusUpdate.setString(i++, PretupsI.NO);
                }
                p_pstmtReconcilationStatusUpdate.setString(i++, p_c2sTransferVO.getTransferID());
                p_pstmtReconcilationStatusUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_c2sTransferVO.getTransferID())));
                p_pstmtReconcilationStatusUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
                p_pstmtReconcilationStatusUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);

                updateCount = p_pstmtReconcilationStatusUpdate.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with updatation in partitioned table in postgres

            }
            if (updateCount <= 0) {
                writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateReconcilationStatus]", p_c2sTransferVO.getTransferID(), " Record is already modified");
            }
            // else //if(updateCount>=1)
            // {
            // if(p_c2sTransferVO.getTransferItemList()!=null &&
            // !p_c2sTransferVO.getTransferItemList().isEmpty())
            // {
            // updateCount=0;
            // updateCount=addC2STransferItemDetails(p_c2sTransferVO.getTransferItemList(),p_c2sTransferVO.getTransferID(),p_pstmtInsertC2STransferItems);
            // }
            // }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateReconcilationStatus]", p_c2sTransferVO.getTransferID(), "SQLException: " + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateReconcilationStatus]", p_c2sTransferVO.getTransferID(), "Exception: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            // try{if (pstmtReconcilationStatusUpdate !=
            // null)pstmtReconcilationStatusUpdate.close();}catch (Exception
            // ex){}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:return=" + updateCount);
            }
        }
        return updateCount;
    }

    public int addC2STransferItemDetails(ArrayList transferItemsList, String p_transferID, PreparedStatement p_pstmtInsertC2STransferItems) throws BTSLBaseException {
        final String methodName = "addC2STransferItemDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID:" + p_transferID + " transferItemsList Size=" + transferItemsList.size());
        }
        int addCount = 0;
        try {
            C2STransferItemVO cs2TransferItemVO = null;
            int i = 1;
            int itemCount = 1;
            if (transferItemsList != null && transferItemsList.size() > 0) {
                for (int j = 0, k = transferItemsList.size(); j < k; j++) {
                    cs2TransferItemVO = (C2STransferItemVO) transferItemsList.get(j);
                    i = 1;
                    if (p_pstmtInsertC2STransferItems != null) {
                        p_pstmtInsertC2STransferItems.clearParameters();
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getTransferID());
                        p_pstmtInsertC2STransferItems.setInt(i++, cs2TransferItemVO.getSNo());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getMsisdn());
                        p_pstmtInsertC2STransferItems.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(cs2TransferItemVO.getEntryDate()));
                        p_pstmtInsertC2STransferItems.setLong(i++, cs2TransferItemVO.getRequestValue());
                        p_pstmtInsertC2STransferItems.setLong(i++, cs2TransferItemVO.getPreviousBalance());
                        p_pstmtInsertC2STransferItems.setLong(i++, cs2TransferItemVO.getPostBalance());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getUserType());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getTransferType());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getEntryType());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getValidationStatus());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getUpdateStatus());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getServiceClass());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getProtocolStatus());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getAccountStatus());
                        p_pstmtInsertC2STransferItems.setLong(i++, cs2TransferItemVO.getTransferValue());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getInterfaceType());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getInterfaceID());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getInterfaceResponseCode());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getInterfaceReferenceID());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getSubscriberType());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getServiceClassCode());
                        p_pstmtInsertC2STransferItems.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(cs2TransferItemVO.getPreviousExpiry()));
                        p_pstmtInsertC2STransferItems.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(cs2TransferItemVO.getNewExpiry()));
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getTransferStatus());
                        p_pstmtInsertC2STransferItems.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(cs2TransferItemVO.getTransferDate()));
                        p_pstmtInsertC2STransferItems.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(cs2TransferItemVO.getTransferDateTime()));
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getFirstCall());
                        p_pstmtInsertC2STransferItems.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(cs2TransferItemVO.getEntryDateTime()));
                        p_pstmtInsertC2STransferItems.setLong(i++, cs2TransferItemVO.getPrefixID());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getReferenceID());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getLanguage());
                        p_pstmtInsertC2STransferItems.setString(i++, cs2TransferItemVO.getCountry());

                        addCount = p_pstmtInsertC2STransferItems.executeUpdate();
                    }
                    if (addCount < 0) {
                        writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[addC2STransferItemDetails]", p_transferID, " SQL Exception General processing error");
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                    itemCount = itemCount + 1;
                }
            }

            return addCount;
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[addC2STransferItemDetails]", p_transferID, " SQLException " + sqle.getMessage());
            addCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[addC2STransferItemDetails]", p_transferID, " Exception " + e.getMessage());
            addCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            // try{if (p_pstmtInsertC2STransferItems !=
            // null)p_pstmtInsertC2STransferItems.close();} catch (Exception
            // e){}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting addCount=" + addCount);
            }
        }// end of finally
    }

    /**
     * Method loadP2PTransferItemsVOList.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PReconciliationItemsList(String p_transferID, PreparedStatement pstmtP2PTransferItemsSelect) throws BTSLBaseException {

        final String methodName = "loadP2PReconciliationItemsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID=" + p_transferID);
        }
        ResultSet rs = null;
        TransferItemVO p2pTransferItemVO = null;
        final ArrayList p2pTransferItemsVOList = new ArrayList();
        try {
            int i = 1;
            if (pstmtP2PTransferItemsSelect != null) {
            	String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
            	pstmtP2PTransferItemsSelect.clearParameters();
            	if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            		pstmtP2PTransferItemsSelect.setString(i++, PretupsI.KEY_VALUE_P2P_STATUS);
            		pstmtP2PTransferItemsSelect.setString(i++, p_transferID);
            	}else{
            		pstmtP2PTransferItemsSelect.setString(i++, p_transferID);
            		pstmtP2PTransferItemsSelect.setString(i++, PretupsI.KEY_VALUE_P2P_STATUS);
            	}
                
                
                rs = pstmtP2PTransferItemsSelect.executeQuery();
            }
            while (rs != null && rs.next()) {
                p2pTransferItemVO = new TransferItemVO();

                p2pTransferItemVO.setTransferID(rs.getString("transfer_id"));
                p2pTransferItemVO.setMsisdn(rs.getString("msisdn"));
                p2pTransferItemVO.setEntryDate(rs.getDate("entry_date"));
                p2pTransferItemVO.setRequestValue(rs.getLong("request_value"));
                p2pTransferItemVO.setPreviousBalance(rs.getLong("previous_balance"));
                p2pTransferItemVO.setPostBalance(rs.getLong("post_balance"));
                p2pTransferItemVO.setUserType(rs.getString("user_type"));
                p2pTransferItemVO.setTransferType(rs.getString("transfer_type"));
                p2pTransferItemVO.setEntryType(rs.getString("entry_type"));
                p2pTransferItemVO.setValidationStatus(rs.getString("validation_status"));
                p2pTransferItemVO.setUpdateStatus(rs.getString("update_status"));
                p2pTransferItemVO.setTransferValue(rs.getLong("transfer_value"));
                p2pTransferItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                p2pTransferItemVO.setInterfaceType(rs.getString("interface_type"));
                p2pTransferItemVO.setInterfaceID(rs.getString("interface_id"));
                p2pTransferItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                p2pTransferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                p2pTransferItemVO.setSubscriberType(rs.getString("subscriber_type"));
                p2pTransferItemVO.setServiceClassCode(rs.getString("service_class_code"));
                p2pTransferItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                p2pTransferItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                p2pTransferItemVO.setTransferStatus(rs.getString("transfer_status"));
                p2pTransferItemVO.setTransferDate(rs.getDate("transfer_date"));
                p2pTransferItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                p2pTransferItemVO.setEntryDateTime(rs.getDate("entry_date_time"));
                p2pTransferItemVO.setFirstCall(rs.getString("first_call"));
                p2pTransferItemVO.setSNo(rs.getInt("sno"));
                p2pTransferItemVO.setPrefixID(rs.getLong("prefix_id"));
                p2pTransferItemVO.setServiceClass(rs.getString("service_class_id"));
                p2pTransferItemVO.setProtocolStatus(rs.getString("protocol_status"));
                p2pTransferItemVO.setAccountStatus(rs.getString("account_status"));
                p2pTransferItemVO.setTransferStatusMessage(rs.getString("value"));
                p2pTransferItemVO.setReferenceID(rs.getString("reference_id"));
                p2pTransferItemsVOList.add(p2pTransferItemVO);
            }
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[loadP2PReconciliationItemsList]", p_transferID, " SQLException " + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[loadP2PReconciliationItemsList]", p_transferID, " Exception " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting p2pTransferItemsVOList.size()=" + p2pTransferItemsVOList.size());
            }
        }// end of finally

        return p2pTransferItemsVOList;
    }

    /**
     * 
     * method markP2PReceiverAmbiguous
     * This method is used in the C2S Reconciliation module, by this method
     * receiver's transfer status is updated
     * as ambigous and previous transfer status is assigned to the update
     * status.
     * 
     * @param p_con
     * @param p_transferID
     * @return
     * @throws BTSLBaseException
     *             int
     */
    public int markP2PReceiverAmbiguous(String p_transferID, PreparedStatement p_markC2SReceiverAmbiguous) throws BTSLBaseException {
        final String methodName = "markP2PReceiverAmbiguous";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID:" + p_transferID);
        }
        int updateCount = 0;
        try {
            int i = 1;
            if (p_markC2SReceiverAmbiguous != null) {
                p_markC2SReceiverAmbiguous.clearParameters();
                p_markC2SReceiverAmbiguous.setString(i++, InterfaceErrorCodesI.AMBIGOUS);
                p_markC2SReceiverAmbiguous.setString(i++, p_transferID);
                p_markC2SReceiverAmbiguous.setString(i++, PretupsI.USER_TYPE_RECEIVER);
                updateCount = p_markC2SReceiverAmbiguous.executeUpdate();
            }
            if (updateCount <= 0) {
                writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[markP2PReceiverAmbiguous]", p_transferID, "QSL processing error");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[markP2PReceiverAmbiguous]", p_transferID, "SQLException " + sqle.getMessage());
            updateCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[markP2PReceiverAmbiguous]", p_transferID, "Exception " + e.getMessage());
            updateCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    /**
     * Method updateReconcilationStatus.
     * 
     * @param p_con
     *            Connection
     * @param p_reconciliationVO
     *            ReconciliationVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateP2PReconcilationStatus(P2PTransferVO p_p2pTransferVO, PreparedStatement pstmtReconcilationStatusUpdate, PreparedStatement pstmtInsertTransferItems) throws BTSLBaseException {
        final String methodName = "updateP2PReconcilationStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p2pTransferVO=" + p_p2pTransferVO);
        }
        int updateCount = 0;
        try {
            int i = 1;
            if (pstmtReconcilationStatusUpdate != null) {
                pstmtReconcilationStatusUpdate.clearParameters();
                pstmtReconcilationStatusUpdate.setString(i++, p_p2pTransferVO.getTransferStatus());
                pstmtReconcilationStatusUpdate.setString(i++, p_p2pTransferVO.getModifiedBy());
                pstmtReconcilationStatusUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_p2pTransferVO.getModifiedOn()));
                pstmtReconcilationStatusUpdate.setString(i++, p_p2pTransferVO.getModifiedBy());
                pstmtReconcilationStatusUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_p2pTransferVO.getModifiedOn()));
                pstmtReconcilationStatusUpdate.setString(i++, p_p2pTransferVO.getTransferID());
                // to perform the check "is Already modify"
                pstmtReconcilationStatusUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
                pstmtReconcilationStatusUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
                updateCount = pstmtReconcilationStatusUpdate.executeUpdate();
            }
            if (updateCount <= 0) {
                writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateP2PReconcilationStatus]", p_p2pTransferVO.getTransferID(), "Record is already modified");
            } else // if(updateCount>=1)
            {
                if (p_p2pTransferVO.getTransferItemList() != null && !p_p2pTransferVO.getTransferItemList().isEmpty()) {
                    updateCount = 0;
                    updateCount = addP2PTransferItemDetails(p_p2pTransferVO.getTransferID(), p_p2pTransferVO.getTransferItemList(), pstmtInsertTransferItems);
                }
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, "updateReconcilationStatus", "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, "updateReconcilationStatus", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:return=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method to add the transfer items details
     * 
     * @param p_con
     * @param p_transferID
     *            String
     * @param transferItemsList
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     * @throws SQLException
     */
    public int addP2PTransferItemDetails(String p_transferID, ArrayList p_transferItemsList, PreparedStatement p_pstmtInsertTransferItems) throws BTSLBaseException {
        final String methodName = "addP2PTransferItemDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, p_transferID, "Entered p_transferItemList:" + p_transferItemsList);
        }
        int addCount = 0;
        try {
            TransferItemVO transferItemVO = null;
            int i = 1;
            int itemCount = 1;
            if (p_transferItemsList != null && p_transferItemsList.size() > 0) {
                for (int j = 0, k = p_transferItemsList.size(); j < k; j++) {
                    transferItemVO = (TransferItemVO) p_transferItemsList.get(j);
                    addCount = 0;
                    i = 1;
                    if (p_pstmtInsertTransferItems != null) {
                        p_pstmtInsertTransferItems.clearParameters();
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "transferItemVO:" + transferItemVO.toString());
                        }
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getTransferID());
                        p_pstmtInsertTransferItems.setInt(i++, transferItemVO.getSNo());
                        p_pstmtInsertTransferItems.setLong(i++, transferItemVO.getPrefixID());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getMsisdn());
                        p_pstmtInsertTransferItems.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getEntryDate()));
                        p_pstmtInsertTransferItems.setLong(i++, transferItemVO.getRequestValue());
                        p_pstmtInsertTransferItems.setLong(i++, transferItemVO.getPreviousBalance());
                        p_pstmtInsertTransferItems.setLong(i++, transferItemVO.getPostBalance());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getUserType());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getTransferType());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getEntryType());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getValidationStatus());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getUpdateStatus());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getServiceClass());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getProtocolStatus());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getAccountStatus());
                        p_pstmtInsertTransferItems.setLong(i++, transferItemVO.getTransferValue());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getInterfaceType());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getInterfaceID());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getInterfaceResponseCode());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getInterfaceReferenceID());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getSubscriberType());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getServiceClassCode());
                        p_pstmtInsertTransferItems.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getPreviousExpiry()));
                        p_pstmtInsertTransferItems.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getNewExpiry()));
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getTransferStatus());
                        p_pstmtInsertTransferItems.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getTransferDate()));
                        p_pstmtInsertTransferItems.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getTransferDateTime()));
                        p_pstmtInsertTransferItems.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getEntryDateTime()));
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getFirstCall());
                        p_pstmtInsertTransferItems.setString(i++, transferItemVO.getReferenceID());
                        addCount = p_pstmtInsertTransferItems.executeUpdate();
                        addCount = BTSLUtil.getInsertCount(addCount); // added to make code compatible with insertion in partitioned table in postgres
                    }
                    itemCount = itemCount + 1;
                    if (addCount <= 0) {
                        writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[addP2PTransferItemDetails]", p_transferID, "SQL general processing error");
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[addP2PTransferItemDetails]", p_transferID, "SQLException " + sqle.getMessage());
            addCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            addCount = 0;
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[addP2PTransferItemDetails]", p_transferID, "Exception " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting addCount=" + addCount);
            }
        }// end of finally
    }

    /**
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public VomsVoucherVO loadVomsVoucherVO(TransferVO p_c2sTransferVO, PreparedStatement p_pstmtC2SVOMSSelect) throws BTSLBaseException {
        final String methodName = "loadVomsVoucherVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered voucher serial no.=" + p_c2sTransferVO.getSerialNumber());
        }
        ResultSet rs = null;
        VomsVoucherVO voucherVO = null;
        try {
            if (p_pstmtC2SVOMSSelect != null) {
                p_pstmtC2SVOMSSelect.clearParameters();
                p_pstmtC2SVOMSSelect.setString(1, p_c2sTransferVO.getSerialNumber());
                rs = p_pstmtC2SVOMSSelect.executeQuery();
            }
            while (rs != null && rs.next()) {
                voucherVO = new VomsVoucherVO();
                voucherVO.setSerialNo(rs.getString("SERIALNO"));
                voucherVO.setPreviousStatus(rs.getString("PREVSTAT"));
                voucherVO.setCurrentStatus(rs.getString("CURRENTSTAT"));
                voucherVO.setVoucherStatus(rs.getString("STAT"));
                voucherVO.setPinNo(rs.getString("PIN_NO"));
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "After executing the query loadVomsVoucherVO method VomsVoucherVO=" + voucherVO);
            }
            return voucherVO;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[loadVomsVoucherVO]", p_c2sTransferVO.getTransferID(), "SQLException " + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[loadVomsVoucherVO]", p_c2sTransferVO.getTransferID(), "Exception " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing rs ex=" + ex);
            }
            try {
                _log.debug(methodName, " Exiting.. VomsVoucherVO=" + voucherVO);
            } catch (Exception e) {
                _log.error(methodName, " Exception while closing rs ex=" + e);
            }
            ;
        }
    }

    /**
     * This method is called at the time of reconciliation to change status of
     * voucher
     * vom_batch_summary
     * 
     * @param p_con
     * @param p_Operation
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     */
    public int updateVoucherStatus(String p_Operation, TransferVO p_transferVO, VomsVoucherVO p_vomsVoucherVO, PreparedStatement p_pstmtVoucherStatusUpdate, PreparedStatement p_pstmtVoucherAuditStatusUpdate) throws BTSLBaseException {
        final String methodName = "updateVoucherStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered for voucher serial no.=" + p_vomsVoucherVO.getSerialNo() + " p_Operation=" + p_Operation);
        }
        int updateCount = 0;
        try {
            int i = 1;
            if (p_pstmtVoucherStatusUpdate != null) {
                p_pstmtVoucherStatusUpdate.clearParameters();
            }
            if ("Success".equals(p_Operation)) {
                if (VOMSI.VOUCHER_UNPROCESS.equals(p_vomsVoucherVO.getCurrentStatus()) || VOMSI.VOUCHER_REP_ENABLE.equals(p_vomsVoucherVO.getCurrentStatus())) {
                    p_pstmtVoucherStatusUpdate.setString(i++, VOMSI.VOUCHER_USED);
                    p_pstmtVoucherStatusUpdate.setString(i++, p_vomsVoucherVO.getCurrentStatus());
                    p_pstmtVoucherStatusUpdate.setString(i++, VOMSI.VOUCHER_USED);
                } else {
                    writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateVoucherStatus]", p_transferVO.getTransferID(), "Invalid Voucher Status");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOMS_RECON_INVALID_VOUCHER_STATUS);
                }
            } else if ("Fail".equals(p_Operation)) {
                if (VOMSI.VOUCHER_UNPROCESS.equals(p_vomsVoucherVO.getCurrentStatus()) || VOMSI.VOUCHER_REP_ENABLE.equals(p_vomsVoucherVO.getCurrentStatus())) {
                    p_pstmtVoucherStatusUpdate.setString(i++, VOMSI.VOUCHER_REP_ENABLE);
                    p_pstmtVoucherStatusUpdate.setString(i++, p_vomsVoucherVO.getCurrentStatus());
                    p_pstmtVoucherStatusUpdate.setString(i++, VOMSI.VOUCHER_REP_ENABLE);
                } else {
                    writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateVoucherStatus]", p_transferVO.getTransferID(), "Invalid Voucher Status");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOMS_RECON_INVALID_VOUCHER_STATUS);
                }
            }
            p_pstmtVoucherStatusUpdate.setString(i++, p_transferVO.getModifiedBy());
            p_pstmtVoucherStatusUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            p_pstmtVoucherStatusUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            p_pstmtVoucherStatusUpdate.setString(i++, p_transferVO.getSerialNumber());
            updateCount = p_pstmtVoucherStatusUpdate.executeUpdate();
            if (updateCount > 0) {
                updateCount = 0;
                updateCount = updateVoucherAuditStatus(p_Operation, p_transferVO, p_vomsVoucherVO, p_pstmtVoucherAuditStatusUpdate);
            }
            return updateCount;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateVoucherStatus]", p_transferVO.getTransferID(), "SQLException " + sqle.getMessage());
            updateCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateVoucherStatus]", p_transferVO.getTransferID(), "Exception " + e.getMessage());
            updateCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    /**
     * 
     * @param p_con
     * @param p_Operation
     * @param p_transferVO
     * @param p_vomsVoucherVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateVoucherAuditStatus(String p_Operation, TransferVO p_transferVO, VomsVoucherVO p_vomsVoucherVO, PreparedStatement p_pstmtVoucherAuditStatusUpdate) throws BTSLBaseException {
        final String methodName = "updateVoucherAuditStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered for voucher serial no.=" + p_vomsVoucherVO.getSerialNo() + " p_Operation=" + p_Operation);
        }
        int updateCount = 0;
        try {
            int i = 1;
            if (p_pstmtVoucherAuditStatusUpdate != null) {
                p_pstmtVoucherAuditStatusUpdate.clearParameters();
            }

            if ("Success".equals(p_Operation)) {
                if (VOMSI.VOUCHER_UNPROCESS.equals(p_vomsVoucherVO.getCurrentStatus()) || VOMSI.VOUCHER_REP_ENABLE.equals(p_vomsVoucherVO.getCurrentStatus())) {
                    p_pstmtVoucherAuditStatusUpdate.setString(i++, VOMSI.VOUCHER_USED);
                    p_pstmtVoucherAuditStatusUpdate.setString(i++, p_vomsVoucherVO.getCurrentStatus());
                } else {
                    writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateVoucherAuditStatus]", p_transferVO.getTransferID(), "Invalid Voucher Status");
                    throw new BTSLBaseException(this, "updateVoucherStatus", PretupsErrorCodesI.VOMS_RECON_INVALID_VOUCHER_STATUS);
                }
            } else if ("Fail".equals(p_Operation)) {
                if (VOMSI.VOUCHER_UNPROCESS.equals(p_vomsVoucherVO.getCurrentStatus()) || VOMSI.VOUCHER_REP_ENABLE.equals(p_vomsVoucherVO.getCurrentStatus())) {
                    p_pstmtVoucherAuditStatusUpdate.setString(i++, VOMSI.VOUCHER_REP_ENABLE);
                    p_pstmtVoucherAuditStatusUpdate.setString(i++, p_vomsVoucherVO.getCurrentStatus());
                } else {
                    writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateVoucherAuditStatus]", p_transferVO.getTransferID(), "Invalid Voucher Status");
                    throw new BTSLBaseException(this, "updateVoucherStatus", PretupsErrorCodesI.VOMS_RECON_INVALID_VOUCHER_STATUS);
                }
            }
            p_pstmtVoucherAuditStatusUpdate.setString(i++, p_transferVO.getModifiedBy());
            p_pstmtVoucherAuditStatusUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            p_pstmtVoucherAuditStatusUpdate.setString(i++, p_transferVO.getSerialNumber());
            updateCount = p_pstmtVoucherAuditStatusUpdate.executeUpdate();
            return updateCount;
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateVoucherAuditStatus]", p_transferVO.getTransferID(), "SQLException " + sqle.getMessage());
            updateCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            writeOutPutFile(_fwriterFail, "ReconcileUnsettledAmbiguousCases[updateVoucherAuditStatus]", p_transferVO.getTransferID(), "Exception " + e.getMessage());
            updateCount = 0;
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    /**
     * Method to write the failure records to the files.
     * 
     * @param p_filewriter
     * @param p_failList
     * @throws BTSLBaseException
     */
    private void writeOutPutFile(FileWriter p_filewriter, String p_ref, String p_transactionID, String p_message) throws BTSLBaseException {
        String fileMessage = null;
        fileMessage = "\n" + p_ref + " " + "For transactionID=" + p_transactionID + " " + p_message;
        try {
            p_filewriter.append(fileMessage);
            p_filewriter.flush();
        } catch (Exception e) {
            _log.errorTrace("writeOutPutFile", e);
        }
    }
}
