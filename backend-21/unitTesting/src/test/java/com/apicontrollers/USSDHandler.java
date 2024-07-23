package com.apicontrollers;

import java.io.IOException;
import java.util.ArrayList;

import com.apicontrollers.ussd.CommissionEnquiry.USSD_ComEnquiry;
import com.apicontrollers.ussd.GMB.USSD_GMB;
import org.testng.annotations.Factory;

import com.apicontrollers.ussd.AddBuddy.USSD_AddBuddy;
import com.apicontrollers.ussd.BalanceEnquiry.USSD_BalanceEnquiry;
import com.apicontrollers.ussd.C2CO2CLastXTransferReport.USSD_C2CLastTransfer;
import com.apicontrollers.ussd.C2SLastXTransferReport.USSD_C2SLastTransfer;
import com.apicontrollers.ussd.CreditTransfer.USSD_CreditTransfer;
import com.apicontrollers.ussd.DailyStatusReport.USSD_DailyStatus;
import com.apicontrollers.ussd.DeRegisterP2P.USSD_P2PDREG;
import com.apicontrollers.ussd.EVD.USSD_EVD;
import com.apicontrollers.ussd.EVR.USSD_EVR;
import com.apicontrollers.ussd.FixedLineRecharge.USSD_FixedLine;
import com.apicontrollers.ussd.GRCTransfer.USSD_GRCTransfer;
import com.apicontrollers.ussd.InquiryByTxnID.USSD_InquiryByTxnID;
import com.apicontrollers.ussd.Last5Transactions.Last5Txn;
import com.apicontrollers.ussd.Last5TxnForParticularMSISDN.Last5TxnWidMSISDN;
import com.apicontrollers.ussd.LastTransferStatus_CP2P.USSD_LastTransferStatus_CP2P;
import com.apicontrollers.ussd.LastTransferStatus_RP2P.USSD_LastTransferStatus_RP2P;
import com.apicontrollers.ussd.MultipleEVD.USSD_MEVD;
import com.apicontrollers.ussd.P2PAccInfo.USSD_AccInfo;
import com.apicontrollers.ussd.P2PHistory.USSD_P2PHistory;
import com.apicontrollers.ussd.P2PRegistration.USSD_P2PREG;
import com.apicontrollers.ussd.PPB.PPB;
import com.apicontrollers.ussd.PrivateRechargeDeActivation.USSD_PrivateRechargeDeactivation;
import com.apicontrollers.ussd.PrivateRechargeEnquiry.USSD_PrivateRechargeEnquiry;
import com.apicontrollers.ussd.PrivateRechargeRegistration.USSD_PrivateRechargeRegistration;
import com.apicontrollers.ussd.Return.USSD_Return;
import com.apicontrollers.ussd.SelfBar.USSD_SelfBar;
import com.apicontrollers.ussd.SelfTPin.USSD_SelfTPin;
import com.apicontrollers.ussd.SetNotificationLanguage.USSD_SetNotificationLanguage;
import com.apicontrollers.ussd.SetNotificationLanguageCCLANGREQ.USSD_SetNotificationLanguage_CC;
import com.apicontrollers.ussd.SetPin.USSD_SETPIN;
import com.apicontrollers.ussd.SetPinCP2P.USSD_SP_CP2P;
import com.apicontrollers.ussd.Transfer.USSD_Transfer;
import com.apicontrollers.ussd.USSD_PromoC2S.USSD_PromoC2S;
import com.apicontrollers.ussd.Withdraw.USSD_Withdraw;
import com.apicontrollers.ussd.c2stransfer.USSD_C2STransfer;
import com.apicontrollers.ussd.internetRecharge.USSD_INTERNETRC;
import com.apicontrollers.ussd.resume.USSD_RESUME;
import com.apicontrollers.ussd.suspend.USSD_SUSPEND;
import com.classes.BaseTest;
import com.commons.ServicesControllerI;
import com.utils._APIUtil;

public class USSDHandler extends BaseTest {

    @Factory()
    public Object[] factoryMethod() throws IOException {

        ArrayList<String> USSDServicesList = _APIUtil.getAPIServices("USSD");
        USSDServicesList.removeAll(ServicesControllerI.serviceResponseList);

        int totalElements = USSDServicesList.size();
        Object[] factoryObjTmp = new Object[totalElements];
        for (int index = 0; index < totalElements; index++) {

            String ServiceCode = USSDServicesList.get(index);

            switch (ServiceCode) {

                case ServicesControllerI.BALANCEENQUIRY_REQ:
                    factoryObjTmp[index] = new USSD_BalanceEnquiry();
                    break;

                case ServicesControllerI.LASTXTRANSFERREPORT_REQ:
                    factoryObjTmp[index] = new USSD_C2CLastTransfer();
                    break;

                case ServicesControllerI.C2SLASTXTRANSFERREPORT_REQ:
                    factoryObjTmp[index] = new USSD_C2SLastTransfer();
                    break;

                case ServicesControllerI.C2STRANSFER_REQ:
                    factoryObjTmp[index] = new USSD_C2STransfer();
                    break;

                case ServicesControllerI.C2SFIXLINE_REQ:
                    factoryObjTmp[index] = new USSD_FixedLine();
                    break;

                case ServicesControllerI.DAILYSTATUSREPORT_REQ:
                    factoryObjTmp[index] = new USSD_DailyStatus();
                    break;

                case ServicesControllerI.EVD_REQ:
                    factoryObjTmp[index] = new USSD_EVD();
                    break;

                case ServicesControllerI.EVR_REQ:
                    factoryObjTmp[index] = new USSD_EVR();
                    break;

                case ServicesControllerI.INQUIRYBYTXNID_REQ:
                    factoryObjTmp[index] = new USSD_InquiryByTxnID();
                    break;

                case ServicesControllerI.LAST5TXN_REQ:
                    factoryObjTmp[index] = new Last5Txn();
                    break;

                case ServicesControllerI.LAST5TXNWITHMSISDN_REQ:
                    factoryObjTmp[index] = new Last5TxnWidMSISDN();
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

                case ServicesControllerI.MULTIPLEEVD_REQ:
                    factoryObjTmp[index] = new USSD_MEVD();
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

                case ServicesControllerI.RETURN_REQ:
                    factoryObjTmp[index] = new USSD_Return();
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

                case ServicesControllerI.SETPIN_REQ:
                    factoryObjTmp[index] = new USSD_SETPIN();
                    break;

                case ServicesControllerI.SETPIN_CP2P_REQ:
                    factoryObjTmp[index] = new USSD_SP_CP2P();
                    break;

                case ServicesControllerI.TRANSFER_REQ:
                    factoryObjTmp[index] = new USSD_Transfer();
                    break;

                case ServicesControllerI.WITHDRAW_REQ:
                    factoryObjTmp[index] = new USSD_Withdraw();
                    break;

                case ServicesControllerI.USSDCREDIT_TRANSFER_REQ:
                    factoryObjTmp[index] = new USSD_CreditTransfer();
                    break;

                case ServicesControllerI.USSD_GIFTRECHARGE_REQ:
                    factoryObjTmp[index] = new USSD_GRCTransfer();
                    break;

                case ServicesControllerI.USSD_CUSTOMERRECHARGE_INTERNET_REQ:
                    factoryObjTmp[index] = new USSD_INTERNETRC();
                    break;

                case ServicesControllerI.USSD_RESUME_REQ:
                    factoryObjTmp[index] = new USSD_RESUME();
                    break;

                case ServicesControllerI.USSD_SUSPEND_REQ:
                    factoryObjTmp[index] = new USSD_SUSPEND();
                    break;

                case ServicesControllerI.USSDACC_INFO_REQ:
                    factoryObjTmp[index] = new USSD_AccInfo();
                    break;

                case ServicesControllerI.USSD_P2PHISTORY_REQ:
                    factoryObjTmp[index] = new USSD_P2PHistory();
                    break;
                
                case ServicesControllerI.USSD_ADDBUDDY_REQ:
                    factoryObjTmp[index] = new USSD_AddBuddy();
                    break;
                    
                case ServicesControllerI.DREG_REQ:
                    factoryObjTmp[index] = new USSD_P2PDREG();
                    break;
                case ServicesControllerI.REG_REQ:
                    factoryObjTmp[index] = new USSD_P2PREG();
                    break;

                case ServicesControllerI.USSD_ComEnquiry_REQ:
                    factoryObjTmp[index] = new USSD_ComEnquiry();
                    break;

                case ServicesControllerI.USSD_GMB_REQ:
                    factoryObjTmp[index] = new USSD_GMB();
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