package com.selftopup.pretups.p2p.reconciliation.businesslogic;

/*
 * @(#)ReconciliationBL.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 19/11/2005 Initial Creation
 * Sandeep Goel 04/08/2006 Modification ID REC001
 * ------------------------------------------------------------------------
 * Class for handling Reconciliation Cases
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.util.ArrayList;
import java.util.Date;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.inter.module.InterfaceErrorCodesI;
import com.selftopup.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.selftopup.pretups.transfer.businesslogic.TransferItemVO;

public class ReconciliationBL {

    private static Log _log = LogFactory.getLog(ReconciliationBL.class.getName());

    /**
     * Method to prepare the new List that needs to be added in the items table
     * 
     * @param p_p2pTransferVO
     * @param p_oldItemsList
     * @param p_status
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public static ArrayList prepareNewList(P2PTransferVO p_p2pTransferVO, ArrayList p_oldItemsList, String p_status, String p_forwardPath) throws BTSLBaseException {
        ArrayList newList = null;
        if (_log.isDebugEnabled())
            _log.debug("prepareNewList", " Entered Transfer ID:" + p_p2pTransferVO.getTransferID() + " p_status to be made=" + p_status + ", p_forwardPath=" + p_forwardPath);
        try {
            int listSize = p_oldItemsList.size();
            TransferItemVO senderItemVO = null;
            TransferItemVO receiverItemVO = null;
            TransferItemVO senderCreditBackItemVO = null;
            String senderStatus = null;
            String receiverStatus = null;
            String senderCreditStatus = null;
            Date currentDate = new Date();
            boolean creditedBackInAmb = false;
            /*
             * here we check if orginal list size is 2 then the creditBack is
             * not done yet other wise if list size
             * is 3 then sender's creditBack is done at the time of txn.
             */
            switch (listSize) {
            case 2: {
                senderItemVO = (TransferItemVO) p_oldItemsList.get(0);
                receiverItemVO = (TransferItemVO) p_oldItemsList.get(1);
                senderStatus = senderItemVO.getTransferStatus();
                receiverStatus = receiverItemVO.getTransferStatus();
                /*
                 * here we are checking only AMBIGOUS case only in future
                 * UNDERPROCESS case may be considered
                 */
                if (senderStatus.equals(InterfaceErrorCodesI.AMBIGOUS)) {
                    /*
                     * here the txn can not be make success according to the
                     * business rule only txn can be
                     * made as fail txn
                     */
                    if (p_status.equals("Success"))
                        throw new BTSLBaseException("ReconciliationBL", "prepareNewList", "p2p.reconciliation.displaydetail.senderdbamb.notdonesuccess", p_forwardPath);
                    else if (p_status.equals("Fail")) {
                        newList = handleSenderAmbigousCase(p_p2pTransferVO.getTransferID(), p_oldItemsList, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, listSize, currentDate);
                    }
                } else if (receiverStatus.equals(InterfaceErrorCodesI.AMBIGOUS)) {
                    creditedBackInAmb = false;
                    newList = handleReceiverAmbigousCase(p_p2pTransferVO.getTransferID(), p_oldItemsList, p_status, listSize, currentDate, creditedBackInAmb);
                }
            }
                break;
            case 3: {
                senderItemVO = (TransferItemVO) p_oldItemsList.get(0);
                receiverItemVO = (TransferItemVO) p_oldItemsList.get(1);
                senderCreditBackItemVO = (TransferItemVO) p_oldItemsList.get(2);
                senderStatus = senderItemVO.getTransferStatus();
                receiverStatus = receiverItemVO.getTransferStatus();
                senderCreditStatus = senderCreditBackItemVO.getTransferStatus();
                creditedBackInAmb = true;
                if (senderCreditStatus.equals(InterfaceErrorCodesI.AMBIGOUS)) {
                    if (receiverStatus.equals(InterfaceErrorCodesI.AMBIGOUS)) {
                        /*
                         * here the txn can not be make success according to the
                         * business rule only txn can be
                         * made as fail txn
                         */
                        if (p_status.equals("Success"))
                            throw new BTSLBaseException("ReconciliationBL", "prepareNewList", "p2p.reconciliation.displaydetail.sendercreditbackambrecamb.notdonesuccess", p_forwardPath);
                        creditedBackInAmb = false;
                        newList = handleReceiverAmbigousCase(p_p2pTransferVO.getTransferID(), p_oldItemsList, p_status, listSize, currentDate, creditedBackInAmb);
                    } else {
                        /*
                         * here the txn can not be make success according to the
                         * business rule only txn can be
                         * made as fail txn
                         */
                        if (p_status.equals("Success"))
                            throw new BTSLBaseException("ReconciliationBL", "prepareNewList", "p2p.reconciliation.displaydetail.sendercreditbackamb.notdonesuccess", p_forwardPath);
                        else if (p_status.equals("Fail")) {
                            newList = handleCreditBackAmbigousCase(p_p2pTransferVO.getTransferID(), p_oldItemsList, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, listSize, currentDate);
                        }
                    }

                } else if (receiverStatus.equals(InterfaceErrorCodesI.AMBIGOUS)) {
                    /*
                     * here the txn can not be make success according to the
                     * business rule only txn can be
                     * made as fail txn
                     */
                    if (p_status.equals("Success"))
                        throw new BTSLBaseException("ReconciliationBL", "prepareNewList", "p2p.reconciliation.displaydetail.sendercrbackreccramb.notdonesuccess", p_forwardPath);
                    else if (p_status.equals("Fail")) {
                        newList = handleReceiverAmbigousCase(p_p2pTransferVO.getTransferID(), p_oldItemsList, p_status, listSize, currentDate, creditedBackInAmb);
                    }
                }
            }
                break;
            }

        } catch (BTSLBaseException be) {
            _log.error("prepareNewList", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("prepareNewList", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationBL[prepareNewList]", p_p2pTransferVO.getTransferID(), "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationBL", "prepareNewList", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("prepareNewList", " Exiting for Transfer ID:" + p_p2pTransferVO.getTransferID() + " new list=" + newList);
        return newList;
    }

    /**
     * Method to handle the Sender Ambigous case
     * here we are forming the new list of TransferItemsVOs for sender only if
     * status is 206
     * 
     * @param p_transferID
     * @param p_transferItemList
     * @param p_status
     * @param p_size
     * @param p_date
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private static ArrayList handleSenderAmbigousCase(String p_transferID, ArrayList p_transferItemList, String p_status, int p_size, Date p_date) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("handleSenderAmbigousCase", " Entered Transfer ID:" + p_transferID + " p_status to be made=" + p_status + " p_size=" + p_size + " p_date=" + p_date);
        ArrayList transList = null;
        int listSize = p_transferItemList.size();
        try {
            if (p_status.equals(SelfTopUpErrorCodesI.TXN_STATUS_FAIL)) {
                transList = new ArrayList();
                TransferItemVO transferItemVO = prepareTransferItemsVO((TransferItemVO) p_transferItemList.get(0), SelfTopUpErrorCodesI.TXN_STATUS_FAIL, listSize, p_date, PretupsI.DEBIT);
                transList.add(transferItemVO);
            }
        } catch (Exception e) {
            _log.error("handleSenderAmbigousCase", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationBL[handleSenderAmbigousCase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationBL", "handleSenderAmbigousCase", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("handleSenderAmbigousCase", " Exiting Transfer ID:" + p_transferID + " transList=" + transList);
        return transList;
    }

    /**
     * Method to handle the Receiver Ambigous case
     * here we are forming the new list of TransferItemsVOs for receiver on the
     * basis of the request as to
     * make Success or Fail
     * 
     * @param p_transferID
     * @param p_transferItemList
     * @param p_status
     * @param p_size
     * @param p_date
     * @param p_isAlreadyCreditBack
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private static ArrayList handleReceiverAmbigousCase(String p_transferID, ArrayList p_transferItemList, String p_status, int p_size, Date p_date, boolean p_isAlreadyCreditBack) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("handleReceiverAmbigousCase", " Entered Transfer ID:" + p_transferID + " p_status to be made=" + p_status + " p_size=" + p_size + " p_date=" + p_date + "p_isAlreadyCreditBack=" + p_isAlreadyCreditBack);
        ArrayList transList = null;
        int listSize = p_transferItemList.size();
        try {
            if (p_status.equals("Fail")) {
                transList = new ArrayList();
                TransferItemVO transferItemVO = prepareTransferItemsVO((TransferItemVO) p_transferItemList.get(1), SelfTopUpErrorCodesI.TXN_STATUS_FAIL, listSize, p_date, PretupsI.CREDIT);
                transList.add(transferItemVO);
                if (!p_isAlreadyCreditBack) {
                    listSize = listSize + 1;
                    transferItemVO = prepareTransferItemsVO((TransferItemVO) p_transferItemList.get(0), SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, listSize, p_date, PretupsI.CREDIT);
                    transList.add(transferItemVO);
                }
            } else if (p_status.equals("Success")) {
                transList = new ArrayList();
                TransferItemVO transferItemVO = prepareTransferItemsVO((TransferItemVO) p_transferItemList.get(1), SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, listSize, p_date, PretupsI.CREDIT);
                transList.add(transferItemVO);
            }
        } catch (Exception e) {
            _log.error("handleReceiverAmbigousCase", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationBL[handleReceiverAmbigousCase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationBL", "handleReceiverAmbigousCase", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        if (_log.isDebugEnabled())
            _log.debug("handleReceiverAmbigousCase", " Exiting Transfer ID:" + p_transferID + " transList=" + transList);
        return transList;
    }

    /**
     * Method to handle the Sender Credit Back Ambigous case
     * here we are forming the new list of TransferItemsVOs only for creditback
     * case and if status is 206
     * 
     * @param p_transferID
     * @param p_transferItemList
     * @param p_status
     * @param p_size
     * @param p_date
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private static ArrayList handleCreditBackAmbigousCase(String p_transferID, ArrayList p_transferItemList, String p_status, int p_size, Date p_date) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("handleCreditBackAmbigousCase", " Entered Transfer ID:" + p_transferID + " p_status to be made=" + p_status + " p_size=" + p_size + " p_date=" + p_date);
        ArrayList transList = null;
        int listSize = p_transferItemList.size();
        try {
            if (p_status.equals(SelfTopUpErrorCodesI.TXN_STATUS_FAIL)) {
                transList = new ArrayList();
                TransferItemVO transferItemVO = prepareTransferItemsVO((TransferItemVO) p_transferItemList.get(0), SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, listSize, p_date, PretupsI.CREDIT);
                transList.add(transferItemVO);
            }
        } catch (Exception e) {
            _log.error("handleCreditBackAmbigousCase", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationBL[handleCreditBackAmbigousCase]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationBL", "handleCreditBackAmbigousCase", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        if (_log.isDebugEnabled())
            _log.debug("handleCreditBackAmbigousCase", " Exiting Transfer ID:" + p_transferID + " transList=" + transList);
        return transList;
    }

    /**
     * Method to prepare the VO that needs to be inserted in database
     * 
     * @param p_transferItemVO
     * @param p_status
     * @param p_size
     * @param p_date
     * @param p_entryType
     * @return TransferItemVO
     */
    private static TransferItemVO prepareTransferItemsVO(TransferItemVO p_transferItemVO, String p_status, int p_size, Date p_date, String p_entryType) {
        TransferItemVO transferItemVO = new TransferItemVO();
        transferItemVO.setTransferID(p_transferItemVO.getTransferID());
        transferItemVO.setMsisdn(p_transferItemVO.getMsisdn());
        transferItemVO.setEntryDate(p_date);
        transferItemVO.setRequestValue(p_transferItemVO.getRequestValue());
        transferItemVO.setUserType(p_transferItemVO.getUserType());
        transferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_RECON);
        transferItemVO.setEntryType(p_entryType);
        transferItemVO.setTransferValue(p_transferItemVO.getTransferValue());
        transferItemVO.setSubscriberType(p_transferItemVO.getSubscriberType());
        transferItemVO.setServiceClassCode(p_transferItemVO.getServiceClassCode());
        transferItemVO.setTransferStatus(p_status);
        transferItemVO.setTransferDate(p_transferItemVO.getTransferDate());
        transferItemVO.setTransferDateTime(p_transferItemVO.getTransferDateTime());
        transferItemVO.setEntryDateTime(p_date);
        transferItemVO.setSNo(p_size + 1);
        transferItemVO.setPrefixID(p_transferItemVO.getPrefixID());
        transferItemVO.setServiceClass(p_transferItemVO.getServiceClass());
        // By sandeep goel ID REC001
        // while reconciliation sender and receiver previous balance is going as
        // blank into the database
        // since we are not setting these values. So following two lines are
        // added
        transferItemVO.setPreviousBalance(p_transferItemVO.getPreviousBalance());
        transferItemVO.setPostBalance(p_transferItemVO.getPostBalance());
        // ends her e

        return transferItemVO;
    }
}
