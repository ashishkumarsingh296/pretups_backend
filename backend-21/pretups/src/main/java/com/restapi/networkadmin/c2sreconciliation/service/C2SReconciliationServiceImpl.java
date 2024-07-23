package com.restapi.networkadmin.c2sreconciliation.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.event.*;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.iat.businesslogic.IATDAO;
import com.btsl.pretups.iat.processes.CheckIATStatus;
import com.btsl.pretups.iat.transfer.businesslogic.IATInterfaceVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATTransferItemVO;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.restapi.networkadmin.c2sreconciliation.requestVO.C2SRreconciliationActionRequestVO;
import com.restapi.networkadmin.c2sreconciliation.requestVO.C2SRreconciliationRequestVO;
import com.restapi.networkadmin.c2sreconciliation.responseVO.*;
import com.web.pretups.channel.transfer.businesslogic.C2STransferWebDAO;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

@Service("C2SReconciliationService")
public class C2SReconciliationServiceImpl implements C2SReconciliationService {

    public static final Log LOG = LogFactory.getLog(C2SReconciliationServiceImpl.class.getName());

    @Override
    public C2SReconciliationResponseListVO loadC2SReconciliationList(Connection con, UserVO userVO, C2SRreconciliationRequestVO requestVO) throws ParseException, BTSLBaseException {
        final String METHOD_NAME = "loadC2SReconciliationList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        C2SReconciliationResponseListVO response = new C2SReconciliationResponseListVO();
        C2STransferWebDAO c2STransferwebDAO = new C2STransferWebDAO();

        final ArrayList transferList = c2STransferwebDAO.loadC2SReconciliationList(
                con,
                BTSLUtil.getDateFromDateString(requestVO.getFromDate()),
                BTSLUtil.getDateFromDateString(requestVO.getToDate()),
                userVO.getNetworkID(), requestVO.getServiceTypeCode()
        );

        if (transferList == null || transferList.isEmpty()) {
            //throw new BTSLBaseException(this, "loadC2SReconciliationList is empty", "");
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_REC_NO_LIST_FOUND, 0, null);
        }



        generateReconciliationListResponse(response,transferList);
        return response;

    }

    @Override
    public C2SReconciliationTransferDetailsVO loadC2SreconciliationTransferDetails(Connection con, String transferID) throws BTSLBaseException {
        final String METHOD_NAME = "loadC2SreconciliationTransferDetails";
        C2SReconciliationTransferDetailsVO response = null;
        C2STransferDAO c2STransferDAO = new C2STransferDAO();
        ArrayList transferDetails = c2STransferDAO.loadC2STransferItemsVOList(con, transferID);
        if (transferDetails == null || transferDetails.isEmpty()) {
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_REC_TRF_DETAILS_NOT_FOUND, 0, null);
        }
        response = new C2SReconciliationTransferDetailsVO();
        response.setTransferID(transferID);
        generateC2SreconciliationTransferDetailsResponse(response,transferDetails);
        return response;
    }

    @Override
    public C2SRreconciliationActionResponseVO performreconciliationaction(Connection con, UserVO userVO, C2SRreconciliationActionRequestVO c2SRreconciliationActionRequestVO) throws BTSLBaseException, SQLException {


        String METHOD_NAME = "performreconciliationaction";
        C2SRreconciliationActionResponseVO response = new C2SRreconciliationActionResponseVO();
        boolean isNewExceptionReq = true;
        final C2STransferVO c2sTransferVO = new C2STransferWebDAO().loadC2SReconciliation(con, c2SRreconciliationActionRequestVO.getTransferId());
        C2STransferDAO c2STransferDAO = new C2STransferDAO();
        ArrayList transferItemList = c2STransferDAO.loadC2STransferItemsVOList(con, c2SRreconciliationActionRequestVO.getTransferId());
        c2sTransferVO.setTransferItemList(transferItemList);
        boolean isIATtransaction = false;
        String iatrStatus = null;

        IATInterfaceVO iatInterfaceVO  =  null;
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_IAT_RUNNING))).booleanValue()) {
            final StringBuffer newStatus = new StringBuffer();
            final StringBuffer message = new StringBuffer();
            isIATtransaction = checkIATTransaction(con, newStatus, c2sTransferVO, message,iatInterfaceVO);
            if (isIATtransaction && !BTSLUtil.isNullString(message.toString())) {
                throw new BTSLBaseException(this, "loadC2SReconciliationItemsList", message.toString(), "displayrecondetails");
            }
        }




        String operation = null, newStatus = null;
        if ("S".equalsIgnoreCase(c2SRreconciliationActionRequestVO.getAction())) {
            operation = "Success";
            newStatus = PretupsErrorCodesI.TXN_STATUS_SUCCESS;
        } else if ("F".equalsIgnoreCase(c2SRreconciliationActionRequestVO.getAction())) {
            operation = "Fail";
            newStatus = PretupsErrorCodesI.TXN_STATUS_FAIL;
        }

        Date currentDate = new Date();
        int updateCount = 0;

        if (c2SRreconciliationActionRequestVO.getServiceTypeCode().equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
            if ("Success".equals(operation)) {
                operation = "Fail";
                newStatus = PretupsErrorCodesI.TXN_STATUS_SUCCESS;
            } else {
                operation = "Success";
                newStatus = PretupsErrorCodesI.TXN_STATUS_FAIL;
            }

        }
        //
        if (PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equals(c2sTransferVO.getTxnStatus())) {
            updateCount = c2STransferDAO.markC2SReceiverAmbiguous(con, c2sTransferVO.getTransferID());
            final C2STransferItemVO receiverItemVO = (C2STransferItemVO) c2sTransferVO.getTransferItemList().get(1);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ReconcilationAction[confirm]", c2sTransferVO
                    .getTransferID(), "", "", "Receiver transfer status changed to '250' from " + receiverItemVO.getTransferStatus());
            receiverItemVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
        }

        c2sTransferVO.setModifiedBy(userVO.getUserID());
        c2sTransferVO.setTransferStatus(newStatus);
        c2sTransferVO.setModifiedOn(currentDate);

        RequestVO requestVO = new RequestVO();
        requestVO.setSenderVO(c2sTransferVO.getSenderVO());
        c2sTransferVO.setRequestVO(requestVO);
        /*
         * now load the list of new transactions which will be make as
         * making whole txn success or fail
         */

        final ArrayList newEntries = ChannelUserBL.prepareNewC2SReconList(con, c2sTransferVO, operation, "");
        c2sTransferVO.setTransferItemList(newEntries);
        /*
         * now do the actual reconciliation of the txn
         */
        if (c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
            final VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
            final VomsVoucherVO vomsVoucherVO = vomsVoucherDAO.loadVomsVoucherVO(con, c2sTransferVO);
            updateCount = vomsVoucherDAO.updateVoucherStatus(con, operation, c2sTransferVO, vomsVoucherVO);
        }

        updateCount = c2STransferDAO.updateReconcilationStatus(con, c2sTransferVO);
        // to update the Iat transfer item vo table
        IATDAO iatDao = null;
        if (isIATtransaction) {
            if ((PretupsI.IAT_TRANSACTION_TYPE).equalsIgnoreCase(c2sTransferVO.getExtCreditIntfceType())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME,
                            "Updating : c2s_iat_transfer_items table for sender, Service type:" + c2sTransferVO.getServiceType() + ", Transaction id:" + c2sTransferVO
                                    .getTransferID() + ", newStatus:" + newStatus);
                }
                iatDao = new IATDAO();
                if (updateCount > 0) {
                    updateCount = iatDao.updateIATTransferItemForAbgTxns(con, iatInterfaceVO , newStatus);
                }
            } else if ((updateCount > 0) && ((PretupsI.REQUEST_SOURCE_TYPE_EXTGW).equalsIgnoreCase(c2sTransferVO.getRequestGatewayType()))) {
                try {
                    writeReceiverSettledAbgTxnInFile(c2sTransferVO);
                } catch (Exception e) {
                    LOG.errorTrace(METHOD_NAME, e);
                    LOG.error(METHOD_NAME, "not able to write receiver ambiguous txns in file ");
                }
            }
        }
        if (con != null) {
            if (updateCount > 0) {
                con.commit();

                // By Sandeep Goel ID RECON001
                // if user's account is credited back or debited then
                // add the balance logger into the system.
                if (c2sTransferVO.getOtherInfo1() != null) {
                    BalanceLogger.log((UserBalancesVO) c2sTransferVO.getOtherInfo1());
                }
                // if differential commission is given by the
                // reconciliation then add the balance logger into the
                // system.
                if (c2sTransferVO.getOtherInfo2() != null) {
                    BalanceLogger.log((UserBalancesVO) c2sTransferVO.getOtherInfo2());
                }
                // ends here
                PushMessage pushMessage = new PushMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), c2sTransferVO.getSenderReturnMessage(), null, null, ((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale());
                pushMessage.push();
                if (c2sTransferVO.getSenderRoamReconDebitMessage() != null) {
                    pushMessage = new PushMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), c2sTransferVO.getSenderRoamReconDebitMessage(), null, null, ((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale());
                    pushMessage.push();
                }
                if (c2sTransferVO.getSenderRoamReconCreditMessage() != null) {
                    pushMessage = new PushMessage(((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), c2sTransferVO.getSenderRoamReconCreditMessage(), null, null, ((ChannelUserVO) c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale());
                    pushMessage.push();
                }

                if (c2sTransferVO.getSenderOwnerRoamReconDebitMessage() != null) {
                    Locale ownerLocale = new Locale(((ChannelUserVO) c2sTransferVO.getOwnerUserVO()).getLanguage(), ((ChannelUserVO) c2sTransferVO.getOwnerUserVO()).getCountryCode());
                    pushMessage = new PushMessage(((ChannelUserVO) c2sTransferVO.getOwnerUserVO()).getMsisdn(), c2sTransferVO.getSenderOwnerRoamReconDebitMessage(), null, null, ownerLocale);
                    pushMessage.push();
                }
                if (c2sTransferVO.getSenderOwnerRoamReconCreditMessage() != null) {
                    Locale ownerLocale = new Locale(((ChannelUserVO) c2sTransferVO.getOwnerUserVO()).getLanguage(), ((ChannelUserVO) c2sTransferVO.getOwnerUserVO()).getCountryCode());
                    pushMessage = new PushMessage(((ChannelUserVO) c2sTransferVO.getOwnerUserVO()).getMsisdn(), c2sTransferVO.getSenderOwnerRoamReconCreditMessage(), null, null, ownerLocale);
                    pushMessage.push();
                }
                //final BTSLMessages btslMessage = new BTSLMessages("c2s.reconciliation.displaydetail.updatemsg.success", "displayreconlistpage");
                response.setMessage("Reconciliation Updated Succesfully");

            } else {
                con.rollback();
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2S_REC_UNSUCCESSFULL, 0, null);
            }
        }

        return response;
    }

    private void generateC2SreconciliationTransferDetailsResponse(C2SReconciliationTransferDetailsVO response, ArrayList transferDetails) {
        Iterator iterator = transferDetails.iterator();
        while (iterator.hasNext()){
            C2STransferDetailsVO c2STransferDetailsVO = new C2STransferDetailsVO();
            C2STransferItemVO c2STransferItemVO = (C2STransferItemVO) iterator.next();
            c2STransferDetailsVO.setMsisdn(c2STransferItemVO.getMsisdn());
            c2STransferDetailsVO.setTransferStatus(c2STransferItemVO.getTransferStatusMessage());
            c2STransferDetailsVO.setAccountStatus(c2STransferItemVO.getAccountStatus());
            c2STransferDetailsVO.setEntryType(c2STransferItemVO.getEntryType());
            c2STransferDetailsVO.setInterfaceResponseCode(c2STransferItemVO.getInterfaceResponseCode());
            c2STransferDetailsVO.setPostBalance(PretupsBL.getDisplayAmount(c2STransferItemVO.getPostBalance()));
            c2STransferDetailsVO.setPreviousBalance(PretupsBL.getDisplayAmount(c2STransferItemVO.getPreviousBalance()));
            c2STransferDetailsVO.setProtocolStatus(c2STransferItemVO.getProtocolStatus());
            c2STransferDetailsVO.setServiceClassCode(c2STransferItemVO.getServiceClassCode());
            c2STransferDetailsVO.setReferenceID(c2STransferItemVO.getInterfaceReferenceID());
            c2STransferDetailsVO.setTransferValue(PretupsBL.getDisplayAmount(c2STransferItemVO.getTransferValue()));
            if(c2STransferItemVO.getSNo() == 1)
                response.setSenderDetails(c2STransferDetailsVO);
            if(c2STransferItemVO.getSNo() == 2)
                response.setReceiverDetails(c2STransferDetailsVO);
            if(c2STransferItemVO.getSNo() == 3)
                response.setCreditBackDetails(c2STransferDetailsVO);
            if(c2STransferItemVO.getSNo() == 4)
                response.setReconcileDetails(c2STransferDetailsVO);

        }
    }

    private void generateReconciliationListResponse(C2SReconciliationResponseListVO response, ArrayList transferList) {

        final ArrayList c2SReconciliationVOS = new ArrayList();

        Iterator iterator = transferList.iterator();
        while (iterator.hasNext()){
            C2SReconciliationVO c2SReconciliationVO = new C2SReconciliationVO();
            C2STransferVO c2STransferVO = (C2STransferVO) iterator.next();
            c2SReconciliationVO.setTransferID(c2STransferVO.getTransferID());
            c2SReconciliationVO.setReceiverMsisdn(c2STransferVO.getReceiverMsisdn());
            c2SReconciliationVO.setSenderName(c2STransferVO.getSenderName());
            c2SReconciliationVO.setProductName(c2STransferVO.getProductName());
            c2SReconciliationVO.setSenderMsisdn(c2STransferVO.getSenderMsisdn());
            c2SReconciliationVO.setTransferDate(c2STransferVO.getTransferDate().toString());
            c2SReconciliationVO.setTransferDate(c2STransferVO.getTransferDateStr());
            c2SReconciliationVO.setTransferValue(c2STransferVO.getTransferValueStr());
            c2SReconciliationVO.setTxnStatus(c2STransferVO.getTxnStatus());
            c2SReconciliationVO.setSenderNetworkCode(c2STransferVO.getSenderNetworkCode());
            c2SReconciliationVO.setReceiverNetworkCode(c2STransferVO.getReceiverNetworkCode());
            c2SReconciliationVO.setCardGroupId(c2STransferVO.getCardGroupID());
            c2SReconciliationVO.setGatewayType(c2STransferVO.getRequestGatewayType());
            c2SReconciliationVO.setCardGroupSetId(c2STransferVO.getCardGroupSetID());
            c2SReconciliationVO.setCardGroupVersion(c2STransferVO.getVersion());
            c2SReconciliationVOS.add(c2SReconciliationVO);
        }

        response.setC2SRreconciliationItemsVOList(c2SReconciliationVOS);
    }

    private boolean checkIATTransaction(Connection p_con, StringBuffer p_newStatus, C2STransferVO p_c2sTransferVO, StringBuffer p_message, IATInterfaceVO iatInterfaceVO) throws BTSLBaseException {
        final String METHOD_NAME = "checkIATTransaction";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered p_newStatus" + p_newStatus);
        }
        boolean isIATTransaction = false;
        String newStatus = null;
        try {
            IATTransferItemVO iatTransferItemVO = null;
            final IATDAO iadDao = new IATDAO();
            final CheckIATStatus checkIATStatus = new CheckIATStatus();
            if ((PretupsI.IAT_TRANSACTION_TYPE).equalsIgnoreCase(p_c2sTransferVO.getExtCreditIntfceType())) {
                isIATTransaction = true;
            } else if (((PretupsI.REQUEST_SOURCE_TYPE_EXTGW).equalsIgnoreCase(p_c2sTransferVO.getRequestGatewayType()))) {
                isIATTransaction = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHECK_REC_TXN_AT_IAT))).booleanValue();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                        METHOD_NAME,
                        "isIATTransaction:" + isIATTransaction + ", ExtCreditInterfaceType:" + p_c2sTransferVO.getExtCreditIntfceType() + ", RequestGatewayType:" + p_c2sTransferVO
                                .getRequestGatewayType());
            }
            if (isIATTransaction) {
                iatTransferItemVO = iadDao.loadIATTransferVO(p_con, p_c2sTransferVO.getTransferID());
                iatInterfaceVO = new IATInterfaceVO();
                iatInterfaceVO = populateInterfaceVO(p_c2sTransferVO, iatTransferItemVO, iatInterfaceVO);
                p_c2sTransferVO.setIatTransferItemVO(iatTransferItemVO);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Checking IAT txn status at IAT TransferID():" + p_c2sTransferVO.getTransferID());
                }
                checkIATStatus.checkIATTxnStatus(iatInterfaceVO, p_c2sTransferVO.getExtCreditIntfceType(), p_c2sTransferVO.getSourceType());
                newStatus = iatInterfaceVO.getIatINTransactionStatus();
                if (!BTSLUtil.isNullString(newStatus)) {
                    p_newStatus.append(newStatus);
                }
                iatTransferItemVO.setTransferStatus(newStatus);

                if ((PretupsErrorCodesI.TXN_STATUS_FAIL).equalsIgnoreCase(newStatus) || (PretupsErrorCodesI.TXN_STATUS_SUCCESS).equalsIgnoreCase(newStatus)) {
                    p_message = null;
                } else {
                    p_message.append("c2s.reconciliation.displaydetail.updatemsg.iat.unsuccess");
                }
            }
        } catch (BTSLBaseException be) {
            LOG.error(METHOD_NAME, "Exceptin:e=" + be);
            LOG.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exceptin:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            p_message.append("c2s.reconciliation.displaydetail.updatemsg.iat.exception");
        } finally {
            if (isIATTransaction && (BTSLUtil.isNullString(newStatus) && BTSLUtil.isNullString(p_message.toString()))) {
                p_message.append("c2s.reconciliation.displaydetail.updatemsg.iat.unsuccess");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting p_newStatus" + p_newStatus + ", isIATTransaction:" + isIATTransaction);
            }
        }
        return isIATTransaction;
    }

    private IATInterfaceVO populateInterfaceVO(C2STransferVO p_c2sTransferVO, IATTransferItemVO p_iatTransferItemVO, IATInterfaceVO p_iatInterfaceVO) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("populateInterfaceVO", " entered");
        }
        final C2STransferItemVO reciverTransferItemVO = (C2STransferItemVO) (p_c2sTransferVO.getTransferItemList().get(1));

        p_iatInterfaceVO.setIatGatewayCode(p_c2sTransferVO.getRequestGatewayCode());
        p_iatInterfaceVO.setIatInterfaceId(reciverTransferItemVO.getInterfaceID());
        p_iatInterfaceVO.setIatSenderNWID(p_c2sTransferVO.getNetworkCode());
        p_iatInterfaceVO.setIatSenderNWTRXID(p_c2sTransferVO.getTransferID());
        p_iatInterfaceVO.setIatServiceType(p_c2sTransferVO.getServiceType());
        p_iatInterfaceVO.setIatSourceType(p_c2sTransferVO.getSourceType());
        if (p_iatTransferItemVO != null) {
            p_iatInterfaceVO.setIatTRXID(p_iatTransferItemVO.getIatTxnId());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("populateInterfaceVO", " exited");
        }
        return p_iatInterfaceVO;
    }

    private void writeReceiverSettledAbgTxnInFile(C2STransferVO p_c2sTransferVO) throws BTSLBaseException {
        final String methodName = "writeReceiverSettledAbgTxnInFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered: p_c2sTransferVO");
        }
        String iatAbgFileNameForTransaction = null;
        String iatRecAbgDirectoryPathAndName = null;
        String fileData = null;
        PrintWriter out = null;
        File newFile = null;
        try {
            iatAbgFileNameForTransaction = Constants.getProperty("IAT_REC_AMBIGUOUS_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(iatAbgFileNameForTransaction)) {
                LOG.error(methodName, "IAT_REC_AMBIGUOUS_TRANSACTION_FILE_NAME: Could not find file name for transaction data in the Constants file.");
            } else {
                LOG.debug(methodName, " iatAbgFileNameForTransaction=" + iatAbgFileNameForTransaction);
            }

            iatRecAbgDirectoryPathAndName = Constants.getProperty("IAT_REC_AMBIGUOUS_FILE_DIRECTORY");
            if (BTSLUtil.isNullString(iatRecAbgDirectoryPathAndName)) {
                LOG.error(methodName, "IAT_REC_AMBIGUOUS_FILE_DIRECTORY: Could not find directory path in the Constants file.");
            } else {
                LOG.debug(methodName, " iatRecAbgDirectoryPathAndName=" + iatRecAbgDirectoryPathAndName);
            }

            // checking that none of the required parameters should be null
            if (BTSLUtil.isNullString(iatAbgFileNameForTransaction) || BTSLUtil.isNullString(iatRecAbgDirectoryPathAndName)) {
                throw new BTSLBaseException("ReconciliationAction", methodName, PretupsErrorCodesI.IAT_ABG_PROCESS_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            LOG.debug(methodName, " Required information successfuly loaded from Constants.props...............: ");
            // creating file directory if not exisrt
            final File parentDir = new File(iatRecAbgDirectoryPathAndName);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            iatAbgFileNameForTransaction = iatRecAbgDirectoryPathAndName + File.separator + iatAbgFileNameForTransaction;
            newFile = new File(iatAbgFileNameForTransaction);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true)));
            fileData = p_c2sTransferVO.getTransferID() + "," + p_c2sTransferVO.getNetworkCode() + "," + p_c2sTransferVO.getTransferValueStr() + "," + p_c2sTransferVO
                    .getReceiverMsisdn() + "," + p_c2sTransferVO.getSenderMsisdn();
            out.write(fileData + "\n");
        } catch (BTSLBaseException be) {
            LOG.error(methodName, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ReconciliationAction[writeReceiverSettledAbgTxnInFile]", "", "", "", "Message:" + be.getMessage());
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.error(methodName, "Exception : " + e.getMessage());
            LOG.errorTrace(methodName, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ReconciliationAction[writeReceiverSettledAbgTxnInFile]", "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("ReconciliationAction", methodName, PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
    }
}
