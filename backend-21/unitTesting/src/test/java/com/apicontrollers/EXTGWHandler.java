package com.apicontrollers;

import java.io.IOException;
import java.util.ArrayList;

import com.apicontrollers.extgw.C2CConsentreversal.C2CConsentReversal;
import com.apicontrollers.extgw.C2SConsentreversal.C2SConsentReversal;
import com.apicontrollers.extgw.Loan.EXTGW_OptIn;
import com.apicontrollers.extgw.selfcareairtime.EXTGW_SELFCAREAIRTIME_Test;
import com.apicontrollers.extgw.selfcarebundle.EXTGW_SELFCAREBUNDLE_Test;
import org.testng.annotations.Factory;

import com.apicontrollers.extgw.AlternateNumber.EXTGW_AddAlternateNumber;
import com.apicontrollers.extgw.AlternateNumber.EXTGW_ModAlternateNumber;
import com.apicontrollers.extgw.C2SRechargeReversal.EXTGW_C2SReversal;
import com.apicontrollers.extgw.CreditTransfer.EXTGW_CreditTransfer;
import com.apicontrollers.extgw.GeographiesAPI.EXTGW_GeographiesAPI;
import com.apicontrollers.extgw.LastTransactionDetails.EXTGW_LastTransactionDetails;
import com.apicontrollers.extgw.LastTransactionDetails.EXTGW_LastXTransaction;
import com.apicontrollers.extgw.LendMeBalance.EXTGW_LendMeBalance;
import com.apicontrollers.extgw.O2CReturn.EXTGW_O2CReturn;
import com.apicontrollers.extgw.P2PAccountInfo.EXTGW_AccInfo;
import com.apicontrollers.extgw.P2PHistory.EXTGW_P2PHistory;
import com.apicontrollers.extgw.PrivateRecharge.EXTGW_PrivateRecharge;
import com.apicontrollers.extgw.PrivateRecharge.EXTGW_SID;
import com.apicontrollers.extgw.SOSFlagUpdate.EXTGW_SOSFlagUpdate;
import com.apicontrollers.extgw.SetNotificationLanguage.EXTGW_SetNotificationLanguage;
import com.apicontrollers.extgw.UserBalanceEnquiry.EXTGW_UserBalanceEnquiry;
import com.apicontrollers.extgw.UserBalanceEnquiryAgentBased.EXTGW_UserBalanceEnquiryAgentBased;
import com.apicontrollers.extgw.VMS.VoucherEnquiry.EXTGW_VoucherEnquiry;
import com.apicontrollers.extgw.VMS.VoucherExpiryExtension.EXTGW_VoucherExpiryExtension;
import com.apicontrollers.extgw.VMS.VoucherRecharge.EXTGW_VoucherRecharge;
import com.apicontrollers.extgw.VMS.VoucherReservation.EXTGW_VoucherReservation;
import com.apicontrollers.extgw.VMS.VoucherRollback.EXTGW_VoucherRollback;
import com.apicontrollers.extgw.VMS.VoucherStatusChange.EXTGW_VoucherStatusChange;
import com.apicontrollers.extgw.VMS.VoucherValidation.EXTGW_VoucherValidation;
import com.apicontrollers.extgw.VMS.voucherpinresend.EXTGW_VoucherPinResend;
import com.apicontrollers.extgw.c2ctransfer.EXTGW_C2CReturn;
import com.apicontrollers.extgw.c2ctransfer.EXTGW_C2CTransfer;
import com.apicontrollers.extgw.c2cwithdraw.EXTGW_C2CWithdraw;
import com.apicontrollers.extgw.c2sTransfer.EXTGW_FixLineRC.EXTGW_FIXLINERC;
import com.apicontrollers.extgw.c2sTransfer.EXTGW_InternetRC.EXTGW_INTERNETRC;
import com.apicontrollers.extgw.c2sTransfer.c2sGiftRecharge.EXTGW_GRC;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGW_C2STransfer;
import com.apicontrollers.extgw.c2sTransfer.liteRecharge.EXTGW_LITERC;
import com.apicontrollers.extgw.c2sTransfer.postpaidBillPayment.EXTGW_PPB;
import com.apicontrollers.extgw.c2sTransferStatus.EXTGW_C2STransferStatus;
import com.apicontrollers.extgw.cardGroupEnquiry.EXTGW_CardGroupEnquiry;
import com.apicontrollers.extgw.changePIN_EXC2SCPNREQ.EXTGW_CHANGEPIN;
import com.apicontrollers.extgw.channelusercreation_USERADDREQ.EXTGW_USERADD;
import com.apicontrollers.extgw.channelusermodify.EXTGW_USERMODIFY;
import com.apicontrollers.extgw.chnlReversal.XMLGW_C2CTXNREV;
import com.apicontrollers.extgw.chnlReversal.XMLGW_O2CTXNREV;
import com.apicontrollers.extgw.lastxtransferenquiry.EXTGW_LastXTransferEnquiry;
import com.apicontrollers.extgw.o2ctransfer.EXTGW_O2CTransfer;
import com.apicontrollers.extgw.o2cwithdraw.EXTGW_O2CWithdraw;
import com.apicontrollers.extgw.selfTPinReset_TPINRSETREQ.EXTGW_RESETPIN;
import com.apicontrollers.extgw.selfpinreset.EXTGW_SELFRESETPIN;
import com.apicontrollers.extgw.suspendResume_SRCUSRREQEX.EXTGW_SUSPENDRESUME;
import com.classes.BaseTest;
import com.commons.GatewayI;
import com.commons.ServicesControllerI;
import com.utils._APIUtil;

public class EXTGWHandler extends BaseTest {
    @Factory
    public Object[] factoryMethod() throws IOException {

        ArrayList<String> EXTGWServicesList = _APIUtil.getAPIServices(GatewayI.EXTGW);
        EXTGWServicesList.removeAll(ServicesControllerI.serviceResponseList);

        int totalElements = EXTGWServicesList.size();
        Object[] factoryObjTmp = new Object[totalElements];
        for (int index = 0; index < totalElements; index++) {

            String ServiceCode = EXTGWServicesList.get(index);

            switch (ServiceCode) {


            	/**
            	 * C2C Transfer Module Reversal
            	 */

            	case ServicesControllerI.C2CTXNREV_REQ:
                factoryObjTmp[index] = new XMLGW_C2CTXNREV();
                break;

            
              	/**
            	 * O2C Transfer Module Reversal
            	 */

            	case ServicesControllerI.O2CTXNREV_REQ:
                factoryObjTmp[index] = new XMLGW_O2CTXNREV();
                break;

            
                
                /**
                 * O2C Transfer Module Controller
                 */

                case ServicesControllerI.O2CTRANSFER_REQ:
                    factoryObjTmp[index] = new EXTGW_O2CTransfer();
                    break;

                /**
                 * O2C Withdraw Module Controller
                 */

                case ServicesControllerI.O2CWITHDRAW_REQ:
                    factoryObjTmp[index] = new EXTGW_O2CWithdraw();
                    break;

                /**
                 * O2C Return Module Controller
                 */

                case ServicesControllerI.O2CRETURN_REQ:
                    factoryObjTmp[index] = new EXTGW_O2CReturn();
                    break;

                /**
                 * C2C Transfer Module Controller
                 */
                case ServicesControllerI.C2CTRANSFER_REQ:
                    factoryObjTmp[index] = new EXTGW_C2CTransfer();
                    break;

                /**
                 * C2C Return Module Controller
                 */
                case ServicesControllerI.C2CRETURN_REQ:
                    factoryObjTmp[index] = new EXTGW_C2CReturn();
                    break;

                /**
                 * Change PIN Module Controller
                 */
                case ServicesControllerI.CHANGEPIN_REQ:
                    factoryObjTmp[index] = new EXTGW_CHANGEPIN();
                    break;

                /**
                 * Change Self TPIN Module Controller
                 */
                case ServicesControllerI.SELFTPIN_REQ:
                    factoryObjTmp[index] = new EXTGW_RESETPIN();
                    break;

                /**
                 * User Balance Enquiry Module Controller
                 */
                case ServicesControllerI.USERBALANCE_REQ:
                    factoryObjTmp[index] = new EXTGW_UserBalanceEnquiry();
                    break;

                /**
                 * C2S Gift Recharge Module Controller
                 */
                case ServicesControllerI.C2SGIFTRECHARGE_REQ:
                    factoryObjTmp[index] = new EXTGW_GRC();
                    break;

                /**
                 * C2S Customer Recharge Module Controller
                 */
                case ServicesControllerI.C2SCUSTOMERRECHARGE_REQ:
                    factoryObjTmp[index] = new EXTGW_C2STransfer();
                    break;

                /**
                 * C2S Postpaid Bill Payment Module Controller
                 */
                case ServicesControllerI.C2SBILLPAYMENT_REQ:
                    factoryObjTmp[index] = new EXTGW_PPB();
                    break;

                /**
                 * C2S Transfer Status Module Controller
                 */
                case ServicesControllerI.C2STRANSFERSTATUS_REQ:
                    factoryObjTmp[index] = new EXTGW_C2STransferStatus();
                    break;

                /**
                 * Geography Module Controller
                 */
                case ServicesControllerI.GEOGRAPHY_REQ:
                    factoryObjTmp[index] = new EXTGW_GeographiesAPI();
                    break;

                /**
                 * Last Transaction Module Controller
                 */
                case ServicesControllerI.LASTTRANSACTION_REQ:
                    factoryObjTmp[index] = new EXTGW_LastTransactionDetails();
                    break;


                /**
                 * Last X Transaction Module Controller
                 */
                case ServicesControllerI.LASTXTRANSACTION_REQ:
                    factoryObjTmp[index] = new EXTGW_LastXTransaction();
                    break;


                /**
                 * Set Notification Language Module Controller
                 */
                case ServicesControllerI.SETNOTIFICATIONLANGUAGE_REQ:
                    factoryObjTmp[index] = new EXTGW_SetNotificationLanguage();
                    break;

                /**
                 * Suspend Resume Module Controller
                 */
                case ServicesControllerI.SUSPENDRESUME_REQ:
                    factoryObjTmp[index] = new EXTGW_SUSPENDRESUME();
                    break;

                /**
                 * User Other Balance Enquiry Module Controller
                 */
                case ServicesControllerI.OTHERBALANCEENQUIRY_REQ:
                    factoryObjTmp[index] = new EXTGW_UserBalanceEnquiryAgentBased();
                    break;


                /**
                 * C2CWithdraw Module Controller
                 */
                case ServicesControllerI.C2CWITHDRAW_REQ:
                    factoryObjTmp[index] = new EXTGW_C2CWithdraw();
                    break;


                /**
                 * P2P CREDITTRANSFER Module Controller
                 */
                case ServicesControllerI.CREDIT_TRANSFER_REQ:
                    factoryObjTmp[index] = new EXTGW_CreditTransfer();
                    break;

                /**
                 * P2P ACCOUNTINFO Module Controller
                 */
                case ServicesControllerI.ACC_INFO_REQ:
                    factoryObjTmp[index] = new EXTGW_AccInfo();
                    break;

                /**
                 * P2P HISTORY Module Controller
                 */
                case ServicesControllerI.P2PHISTORY_REQ:
                    factoryObjTmp[index] = new EXTGW_P2PHistory();
                    break;

                /**
                 * Internet Recharge
                 */
                case ServicesControllerI.EXTGW_INTRC_REQ:
                    factoryObjTmp[index] = new EXTGW_INTERNETRC();
                    break;

                /**
                 * Fix Line Recharge
                 */
                case ServicesControllerI.EXTGW_FIXLINERC_REQ:
                    factoryObjTmp[index] = new EXTGW_FIXLINERC();
                    break;

                /**
                 * Lend Me Balance Module Controller
                 */
                case ServicesControllerI.EXTGW_LMB_REQ:
                    factoryObjTmp[index] = new EXTGW_LendMeBalance();
                    break;

                case ServicesControllerI.C2S_REV_REQ:
                    factoryObjTmp[index] = new EXTGW_C2SReversal();
                    break;

                case ServicesControllerI.EXTGW_CRDGRPENQ_REQ:
                    factoryObjTmp[index] = new EXTGW_CardGroupEnquiry();
                    break;

                case ServicesControllerI.EXTGW_LITERC_REQ:
                    factoryObjTmp[index] = new EXTGW_LITERC();
                    break;

                case ServicesControllerI.EXTGW_PRIVATERECHARGE_REGISTRATION_REQ:
                    factoryObjTmp[index] = new EXTGW_PrivateRecharge();
                    break;
                case ServicesControllerI.EXTGW_PRENQUIRY_REQ:
                    factoryObjTmp[index] = new EXTGW_SID();
                    break;

                case ServicesControllerI.EXTGW_USERADD_REQ:
                    factoryObjTmp[index] = new EXTGW_USERADD();
                    break;

                case ServicesControllerI.EXTGW_USERMOD_REQ:
                    factoryObjTmp[index] = new EXTGW_USERMODIFY();
                    break;

                case ServicesControllerI.EXTGW_SOSFLAGUPDATE_REQ:
                    factoryObjTmp[index] = new EXTGW_SOSFlagUpdate();
                    break;
                    
                case ServicesControllerI.EXTGW_VOMSVAL_REQ:
                    factoryObjTmp[index] = new EXTGW_VoucherValidation();
                    break;
                
                case ServicesControllerI.EXTGW_VOMSRSV_REQ:
                    factoryObjTmp[index] = new EXTGW_VoucherReservation();
                    break;
                    
                case ServicesControllerI.EXTGW_VOMSDCONS_REQ:
                    factoryObjTmp[index] = new EXTGW_VoucherRecharge();
                    break;
                    
                case ServicesControllerI.EXTGW_VOUQRY_REQ:
                    factoryObjTmp[index] = new EXTGW_VoucherEnquiry();
                    break;
                    
            /*    case ServicesControllerI.EXTGW_VOMSSTCHG_REQ:
                    factoryObjTmp[index] = new EXTGW_VoucherStatusChange();
                    break;*/
                    
                case ServicesControllerI.EXTGW_VOMSROLLBACK_REQ:
                    factoryObjTmp[index] = new EXTGW_VoucherRollback();
                    break;
                    
                case ServicesControllerI.EXTGW_VOMSEXPIRY_REQ:
                    factoryObjTmp[index] = new EXTGW_VoucherExpiryExtension();
                    break;

                case ServicesControllerI.EXTGW_ADDALTERNATEMSISDN_REQ:
                    factoryObjTmp[index] = new EXTGW_AddAlternateNumber();
                    break;

                case ServicesControllerI.EXTGW_MODALTERNATEMSISDN_REQ:
                    factoryObjTmp[index] = new EXTGW_ModAlternateNumber();
                    break;

                case ServicesControllerI.EXTGW_VOUCHERPINRESEND_REQ:
                    factoryObjTmp[index] = new EXTGW_VoucherPinResend();
                    break;

                case ServicesControllerI.EXTGW_LASTXTRANSACTION_REQ:
                    factoryObjTmp[index] = new EXTGW_LastXTransferEnquiry();
                    break;

                case ServicesControllerI.EXTGW_SELFPINRESET_REQ:
                    factoryObjTmp[index] = new EXTGW_SELFRESETPIN();
                    break;    
                    
                /**
                 * VOMS Module Controller
                 */
			/* case ServicesControllerI.EXTGW_VMS_REQ:
				 factoryObjTmp [index] = new EXTGW_P2PHistory();
				 break;
				*/
                case ServicesControllerI.EXTGW_C2C_CONSENT_REQ:
                    factoryObjTmp[index] = new C2CConsentReversal();
                    break;

                case ServicesControllerI.EXTGW_C2S_CONSENT_REQ:
                    factoryObjTmp[index] = new C2SConsentReversal();
                    break;

                case ServicesControllerI.EXTGW_SELF_CARE_AIRTIME_REQ:
                    factoryObjTmp[index] = new EXTGW_SELFCAREAIRTIME_Test();
                    break;

                case ServicesControllerI.EXTGW_SELF_CARE_BUNDLE_REQ:
                    factoryObjTmp[index] = new EXTGW_SELFCAREBUNDLE_Test();
                    break;


                case ServicesControllerI.LOAN_OPTIN_REQ:
                    factoryObjTmp[index] = new EXTGW_OptIn();
                    break;


                case ServicesControllerI.LOAN_OPTOUT_REQ:
                    factoryObjTmp[index] = new EXTGW_OptIn();
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