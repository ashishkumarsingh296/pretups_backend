package com.apicontrollers;

import java.io.IOException;
import java.util.ArrayList;

import org.testng.annotations.Factory;

import com.apicontrollers.ussd.AddBuddy.USSDPlain_AddBuddy;
import com.apicontrollers.ussd.BalanceEnquiry.USSDPlain_BalanceEnquiry;
import com.apicontrollers.ussd.C2CO2CLastXTransferReport.USSDPlain_C2CLastTransfer;
import com.apicontrollers.ussd.C2SLastXTransferReport.USSDPlain_C2SLastTransfer;
import com.apicontrollers.ussd.CreditTransfer.USSDPlain_CreditTransfer;
import com.apicontrollers.ussd.DailyStatusReport.USSDPlain_DailyStatus;
import com.apicontrollers.ussd.EVD.USSDPlain_EVD;
import com.apicontrollers.ussd.EVR.USSDPlain_EVR;
import com.apicontrollers.ussd.FixedLineRecharge.USSDPlain_FixedLine;
import com.apicontrollers.ussd.GRCTransfer.USSD_GRCTransfer;
import com.apicontrollers.ussd.InquiryByTxnID.USSDPlain_InquiryByTxnID;
import com.apicontrollers.ussd.Last5Transactions.USSDPlainLast5Txn;
import com.apicontrollers.ussd.Last5TxnForParticularMSISDN.USSDPlainLast5TxnWidMSISDN;
import com.apicontrollers.ussd.LastTransferStatus_CP2P.USSD_LastTransferStatus_CP2P;
import com.apicontrollers.ussd.LastTransferStatus_RP2P.USSD_LastTransferStatus_RP2P;
import com.apicontrollers.ussd.MultipleEVD.USSDPlain_MEVD;
import com.apicontrollers.ussd.P2PAccInfo.USSDPlain_AccInfo;
import com.apicontrollers.ussd.P2PHistory.USSDPlain_P2PHistory;
import com.apicontrollers.ussd.PPB.PPB;
import com.apicontrollers.ussd.PrivateRechargeDeActivation.USSD_PrivateRechargeDeactivation;
import com.apicontrollers.ussd.PrivateRechargeEnquiry.USSD_PrivateRechargeEnquiry;
import com.apicontrollers.ussd.PrivateRechargeRegistration.USSD_PrivateRechargeRegistration;
import com.apicontrollers.ussd.Return.USSDPlain_Return;
import com.apicontrollers.ussd.SelfBar.USSD_SelfBar;
import com.apicontrollers.ussd.SelfTPin.USSD_SelfTPin;
import com.apicontrollers.ussd.SetNotificationLanguage.USSD_SetNotificationLanguage;
import com.apicontrollers.ussd.SetNotificationLanguageCCLANGREQ.USSD_SetNotificationLanguage_CC;
import com.apicontrollers.ussd.SetPin.USSDPlain_SETPIN;
import com.apicontrollers.ussd.SetPinCP2P.USSDPlain_SP_CP2P;
import com.apicontrollers.ussd.Transfer.USSDPlain_Transfer;
import com.apicontrollers.ussd.USSD_PromoC2S.USSD_PromoC2S;
import com.apicontrollers.ussd.Withdraw.USSDPlain_Withdraw;
import com.apicontrollers.ussd.c2stransfer.USSDPlain_C2STransfer;
import com.apicontrollers.ussd.internetRecharge.USSDPlain_INTERNETRC;
import com.apicontrollers.ussd.resume.USSDPlain_RESUME;
import com.apicontrollers.ussd.suspend.USSDPlain_SUSPEND;
import com.classes.BaseTest;
import com.commons.ServicesControllerI;
import com.utils._APIUtil;

public class USSDPlainHandler extends BaseTest {

    @Factory()
    public Object[] factoryMethod() throws IOException {

        ArrayList<String> USSDServicesList = _APIUtil.getAPIServices("USSD_PLAIN");
        USSDServicesList.removeAll(ServicesControllerI.serviceResponseList);

        int totalElements = USSDServicesList.size();
        Object[] factoryObjTmp = new Object[totalElements];
        for (int index = 0; index < totalElements; index++) {

            String ServiceCode = USSDServicesList.get(index);

            switch (ServiceCode) {

                case ServicesControllerI.USSD_PLAIN_BALANCEENQUIRY_REQ:
                    factoryObjTmp[index] = new USSDPlain_BalanceEnquiry();//Done
                    break;

                case ServicesControllerI.USSD_PLAIN_LASTXTRANSFERREPORT_REQ:
                    factoryObjTmp[index] = new USSDPlain_C2CLastTransfer();//Done
                    break;

                case ServicesControllerI.C2SLASTXTRANSFERREPORT_REQ:
                    factoryObjTmp[index] = new USSDPlain_C2SLastTransfer();
                    break;

                case ServicesControllerI.USSD_PLAIN_C2STRANSFER_REQ:
                    factoryObjTmp[index] = new USSDPlain_C2STransfer();//Need to correct
                    break;

                case ServicesControllerI.C2SFIXLINE_REQ:
                    factoryObjTmp[index] = new USSDPlain_FixedLine();
                    break;

                case ServicesControllerI.DAILYSTATUSREPORT_REQ:
                    factoryObjTmp[index] = new USSDPlain_DailyStatus();
                    break;

                case ServicesControllerI.EVD_REQ:
                    factoryObjTmp[index] = new USSDPlain_EVD();
                    break;

                case ServicesControllerI.EVR_REQ:
                    factoryObjTmp[index] = new USSDPlain_EVR();
                    break;

                case ServicesControllerI.INQUIRYBYTXNID_REQ:
                    factoryObjTmp[index] = new USSDPlain_InquiryByTxnID();
                    break;

                case ServicesControllerI.LAST5TXN_REQ:
                    factoryObjTmp[index] = new USSDPlainLast5Txn();
                    break;

                case ServicesControllerI.LAST5TXNWITHMSISDN_REQ:
                    factoryObjTmp[index] = new USSDPlainLast5TxnWidMSISDN();
                    break;

                case ServicesControllerI.PPB_REQ:
                    factoryObjTmp[index] = new PPB();
                    break;

                case ServicesControllerI.USSD_PROMOC2S_REQ:
                    factoryObjTmp[index] = new USSD_PromoC2S();
                    break;

                case ServicesControllerI.LASTTRANSFERSTATUS_RP2P_REQ:
                    factoryObjTmp[index] = new USSD_LastTransferStatus_CP2P();
                    break;

                case ServicesControllerI.LASTTRANSFERSTATUS_CP2P_REQ:
                    factoryObjTmp[index] = new USSD_LastTransferStatus_RP2P();
                    break;

                case ServicesControllerI.USSD_PLAIN_MULTIPLEEVD_RESP:
                    factoryObjTmp[index] = new USSDPlain_MEVD();//Done
                    break;

                case ServicesControllerI.USSD_PRIVATERECHARGE_REGISTRATION_REQ:
                    factoryObjTmp[index] = new USSD_PrivateRechargeRegistration();
                    break;

                case ServicesControllerI.PRDEACTIVATION_REQ:
                    factoryObjTmp[index] = new USSD_PrivateRechargeDeactivation();
                    break;

                case ServicesControllerI.PRENQUIRY_REQ:
                    factoryObjTmp[index] = new USSD_PrivateRechargeEnquiry();
                    break;

                case ServicesControllerI.USSD_PLAIN_RETURN_REQ:
                    factoryObjTmp[index] = new USSDPlain_Return();//Done
                    break;

                case ServicesControllerI.SELFBAR_REQ:
                    factoryObjTmp[index] = new USSD_SelfBar();
                    break;

                case ServicesControllerI.USSD_SELFTPIN_REQ:
                    factoryObjTmp[index] = new USSD_SelfTPin();
                    break;

                case ServicesControllerI.SETNOTIFICATIONLANGUAGE_REQ:
                    factoryObjTmp[index] = new USSD_SetNotificationLanguage();
                    break;

                case ServicesControllerI.SETNOTIFICATIONLANGUAGE_CCLANG_REQ:
                    factoryObjTmp[index] = new USSD_SetNotificationLanguage_CC();
                    break;

                case ServicesControllerI.USSD_PLAIN_SETPIN_REQ:
                    factoryObjTmp[index] = new USSDPlain_SETPIN();//1 scenarios failing
                    break;

                case ServicesControllerI.USSD_PLAIN_SETPIN_CP2P_REQ:
                    factoryObjTmp[index] = new USSDPlain_SP_CP2P();//Done
                    break;

                case ServicesControllerI.USSD_PLAIN_TRANSFER_REQ:
                    factoryObjTmp[index] = new USSDPlain_Transfer();//Done
                    break;

                case ServicesControllerI.USSD_PLAIN_WITHDRAW_REQ:
                    factoryObjTmp[index] = new USSDPlain_Withdraw();//Done
                    break;

                case ServicesControllerI.USSDCREDIT_TRANSFER_REQ:
                    factoryObjTmp[index] = new USSDPlain_CreditTransfer();//Need to fix
                    break;

                case ServicesControllerI.USSD_GIFTRECHARGE_REQ:
                    factoryObjTmp[index] = new USSD_GRCTransfer();
                    break;

                case ServicesControllerI.USSD_CUSTOMERRECHARGE_INTERNET_REQ:
                    factoryObjTmp[index] = new USSDPlain_INTERNETRC();//Need to make changes in classes(FilteredMsisdn)
                    break;

                case ServicesControllerI.USSD_RESUME_REQ:
                    factoryObjTmp[index] = new USSDPlain_RESUME();
                    break;

                case ServicesControllerI.USSD_SUSPEND_REQ:
                    factoryObjTmp[index] = new USSDPlain_SUSPEND();
                    break;

                case ServicesControllerI.USSDACC_INFO_REQ:
                    factoryObjTmp[index] = new USSDPlain_AccInfo();
                    break;

                case ServicesControllerI.USSD_P2PHISTORY_REQ:
                    factoryObjTmp[index] = new USSDPlain_P2PHistory();
                    break;
                
                case ServicesControllerI.USSD_ADDBUDDY_REQ:
                    factoryObjTmp[index] = new USSDPlain_AddBuddy();
                    break;
                    
            }
        }

        int objCounter = 0;
        for (int i = 0; i < factoryObjTmp.length; i++) {
            if (factoryObjTmp[i] != null)
                objCounter++;
        }

        Object[] facobj = new Object[objCounter];
        int newobjcounter = 0;
        for (int i = 0; i < factoryObjTmp.length; i++) {
            if (factoryObjTmp[i] != null) {
                facobj[newobjcounter] = factoryObjTmp[i];
                newobjcounter++;
            }
        }

        return facobj;
    }

}