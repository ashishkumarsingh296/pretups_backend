package com.commons;

import java.util.ArrayList;
import java.util.Arrays;

public class ServicesControllerI {

    public final static String C2SReceiver = "C2SReceiver";
    public final static String OperatorReceiver = "OPTReceiver";
    public final static String P2PReceiver = "P2PReceiver";
    public final static String VomsReceiver = "VomsReciever";
    public final static String ChannelReceiver = "ChannelReceiver";
    public final static String SystemReceiver = "SystemReceiver";

    public final static String CHANGEPIN_REQ = "EXC2SCPNREQ";
    public final static String SELFTPIN_REQ = "TPINRSETREQ";
    public final static String O2CTRANSFER_REQ = "O2CINTREQ";
    public final static String USERBALANCE_REQ = "EXUSRBALREQ";
    public final static String O2CWITHDRAW_REQ = "O2CWDREQ";
    public final static String C2CTRANSFER_REQ = "EXC2CTRFREQ";
    public final static String C2CRETURN_REQ = "EXC2CRETREQ";
    public final static String O2CRETURN_REQ = "O2CRETREQ";
    public final static String C2SGIFTRECHARGE_REQ = "EXGFTRCREQ";
    public final static String C2SCUSTOMERRECHARGE_REQ = "EXRCTRFREQ";
    public final static String C2SBILLPAYMENT_REQ = "EXPPBREQ";
    public final static String C2STRANSFERSTATUS_REQ = "EXRCSTATREQ";
    public final static String GEOGRAPHY_REQ = "EXTGRPH";
    public final static String LASTTRANSACTION_REQ = "EXLSTTRFREQ";
    public final static String LMSPOINTENQUIRY_REQ = "LMSPTENQ";
    public final static String SETNOTIFICATIONLANGUAGE_REQ = "RCNLANGREQ";
    public final static String SUSPENDRESUME_REQ = "SRCUSRREQEX";
    public final static String OTHERBALANCEENQUIRY_REQ = "EXOTHUSRBALREQ";
    public final static String LASTXTRANSACTION_REQ = "EXLST3TRFREQ";
    public final static String C2CWITHDRAW_REQ = "EXC2CWDREQ";
    public final static String C2CTXNREV_REQ = "C2CREVREQ";
    public final static String O2CTXNREV_REQ = "O2CREVREQ";


    public final static String CHANGEPIN_RESP = "EXC2SCPNRESP";
    public final static String SELFTPIN_RESP = "TPINRSETREQRES";
    public final static String O2CTRANSFER_RESP = "O2CINTRESP";
    public final static String USERBALANCE_RESP = "EXUSRBALRESP";
    public final static String O2CWITHDRAW_RESP = "O2CWDRESP";
    public final static String C2CTRANSFER_RESP = "EXC2CTRFRESP";
    public final static String C2CRETURN_RESP = "EXC2CRETRESP";
    public final static String O2CRETURN_RESP = "OCRETRESP";
    public final static String C2SGIFTRECHARGE_RESP = "EXGFTRCRESP";
    public final static String C2SCUSTOMERRECHARGE_RESP = "EXRCTRFRESP";
    public final static String C2SBILLPAYMENT_RESP = "EXPPBRESP";
    public final static String C2STRANSFERSTATUS_RESP = "EXRCSTATRESP";
    public final static String GEOGRAPHY_RESP = "EXTGRPH";
    public final static String LASTTRANSACTION_RESP = "EXLSTTRFRESP";
    public final static String LMSPOINTENQUIRY_RESP = "LMSPTENQRES";
    public final static String SETNOTIFICATIONLANGUAGE_RESP = "RCLANGRESP";
    public final static String SUSPENDRESUME_RESP = "SRCUSRRESP";
    public final static String OTHERBALANCEENQUIRY_RESP = "EXOTHUSRBALRESP";
    public final static String LASTXTRANSACTION_RESP = "EXLST3TRFRESP";
    public final static String C2CWITHDRAW_RESP = "EXC2CWDRESP";
    public final static String C2S_REV_REQ = "RCREVREQ";
    public final static String C2S_REV_RESP  = "RCREVRESP";
    public final static String C2CTXNREV_RESP = "C2CREVRESP";
    public final static String O2CTXNREV_RESP = "O2CREVRESP";

    public final static String LOAN_OPTIN_REQ = "LOANOPTINREQ";
    public final static String LOAN_OPTIN_RESP  = "LOANOPTINRESP";

    public final static String LOAN_OPTOUT_REQ = "LOANOPTOUTREQ";
    public final static String LOAN_OPTOUT_RESP  = "LOANOPTOUTRESP";
    

    //USSD Section
    public final static String BALANCEENQUIRY_REQ = "BALREQ";
    public final static String BALANCEENQUIRY_RESP = "BALRESP";
    public final static String REG_REQ = "REGREQ";
    public final static String DREG_REQ = "DREGREQ";
    public final static String LASTXTRANSFERREPORT_REQ = "LXTSREQ";
    public final static String LASTXTRANSFERREPORT_RESP = "LXTSRESP";

    public final static String C2SLASTXTRANSFERREPORT_REQ = "LXC2STSREQ";
    public final static String C2SLASTXTRANSFERREPORT_RESP = "LXC2STSRESP";

    public final static String C2STRANSFER_REQ = "RCTRFREQ";
    public final static String C2STRANSFER_RESP = "RCTRFRESP";

    public final static String C2SFIXLINE_REQ = "PSTNRCTRFREQ";
    public final static String C2SFIXLINE_RESP = "PSTNRCTRFRESP";

    public final static String DAILYSTATUSREPORT_REQ = "DSRREQ";
    public final static String DAILYSTATUSREPORT_RESP = "DSRRESP";

    public final static String EVD_REQ = "EVDREQ";
    public final static String EVD_RESP = "EVDRESP";

    public final static String EVR_REQ = "EVRTRFREQ";
    public final static String EVR_RESP = "EVRTRFRESP";

    public final static String LASTTRANSFERSTATUS_RP2P_REQ = "LTSREQ";
    public final static String LASTTRANSFERSTATUS_RP2P_RESP = "LTSRESP";

    public final static String LASTTRANSFERSTATUS_CP2P_REQ = "PLTREQ";
    public final static String LASTTRANSFERSTATUS_CP2P_RESP = "PLTRESP";

    public final static String MULTIPLEEVD_REQ = "MVDREQ";
    public final static String MULTIPLEEVD_RESP = "EVDRESP";

    public final static String PRDEACTIVATION_REQ = "DELSID";
    public final static String PRDEACTIVATION_RESP = "SIDDELRESP";

    public final static String PRENQUIRY_REQ = "ENQSID";
    public final static String PRENQUIRY_RESP = "SIDENQRESP";
    
    public final static String EXTGW_PRENQUIRY_REQ = "ENQSID";
    public final static String EXTGW_PRENQUIRY_RESP = "SIDENQRESP";

    public final static String RETURN_REQ = "RETREQ";
    public final static String RETURN_RESP = "RETRESP";

    public final static String SELFBAR_REQ = "BARREQ";
    public final static String SELFBAR_RESP = "BARRESP";

    public final static String USSD_SELFTPIN_REQ = "INPRESET";
    public final static String USSD_SELFTPIN_RESP = "INPRESET";

    public final static String SETNOTIFICATIONLANGUAGE_CCLANG_REQ = "CCLANGREQ";
    public final static String SETNOTIFICATIONLANGUAGE_CCLANG_RESP = "CCLANGRESP";

    public final static String SETPIN_REQ = "RCPNREQ";
    public final static String SETPIN_RESP = "RCPNRESP";

    public final static String SETPIN_CP2P_REQ = "CCPNREQ";
    public final static String SETPIN_CP2P_RESP = "CCPNRESP";

    public final static String TRANSFER_REQ = "TRFREQ";
    public final static String TRANSFER_RESP = "TRFRESP";

    public final static String WITHDRAW_REQ = "WDTHREQ";
    public final static String WITHDRAW_RESP = "WDTHRESP";

    public final static String INQUIRYBYTXNID_REQ = "TXENQREQ";
    public final static String INQUIRYBYTXNID_RESP = "TXENQRESP";

    public final static String LAST5TXN_REQ = "EXLST3TRFREQ";
    public final static String LAST5TXN_RESP = "EXLST3TRFREP";

    public final static String LAST5TXNWITHMSISDN_REQ = "CUSTXTRFREQ";
    public final static String LAST5TXNWITHMSISDN_RESP = "TOTC2CINREP";

    public final static String PPB_REQ = "PPBTRFREQ";
    public final static String PPB_RESP = "PPBTRFRESP";

    public final static String USSD_PROMOC2S_REQ = "PRRCTRFREQ";
    public final static String USSD_PROMOC2S_RESP = "PRRCTRFRESP";

    public final static String CREDIT_TRANSFER_REQ = "CCTRFREQ";
    public final static String CREDIT_TRANSFER_RESP = "CCTRFRESP";

    public final static String ACC_INFO_REQ = "CACINFREQ";
    public final static String ACC_INFO_RESP = "CACINFRESP";

    public final static String P2PHISTORY_REQ = "CCHISREQ";
    public final static String P2PHISTORY_RESP = "CCHISRESP";

    public final static String USSDCREDIT_TRANSFER_REQ = "CCTRFREQ";
    public final static String USSDCREDIT_TRANSFER_RESP = "CCTRFRESP";

    public final static String USSDACC_INFO_REQ = "CACINFREQ";
    public final static String USSDACC_INFO_RESP = "CACINFRESP";

    public final static String USSD_P2PHISTORY_REQ = "CCHISREQ";
    public final static String USSD_P2PHISTORY_RESP = "CCHISRESP";
    
    public final static String USSD_ADDBUDDY_REQ = "ADDBUDDYREQ";
    public final static String USSD_ADDBUDDY_RESP = "ADDBUDDYRESP";

    public final static String EXTGW_VMS_REQ = "VOMSCONSREQ";
    public final static String EXTGW_VMS_RESP = "VOMSCONSRES";

    public final static String EXTGW_LMB_REQ = "LMBREQ";
    public final static String EXTGW_LMB_RESP = "LMBRESP";

    public final static String EXTGW_INTRC_REQ = "EXINTRRCREQ";
    public final static String EXTGW_INTRC_RESP = "EXINTRRCRESP";

    public final static String EXTGW_FIXLINERC_REQ = "EXPSTNRCREQ";
    public final static String EXTGW_FIXLINERC_RESP = "EXPSTNRCRESP";
    
    public final static String EXTGW_LITERC_REQ = "RCTRFSERREQ";
    public final static String EXTGW_LITERC_RESP = "RCTRFSERRESP";
    
    public final static String EXTGW_PRIVATERECHARGE_REGISTRATION_REQ = "SIDREQ";
    public final static String EXTGW_PRIVATERECHARGE_REGISTRATION_RESP = "SIDRESP";
    
    public final static String EXTGW_USERADD_REQ = "USERADDREQ";
    public final static String EXTGW_USERADD_RESP  = "USERADDRESP";
    
    public final static String EXTGW_USERMOD_REQ = "USERMODREQ";
    public final static String EXTGW_USERMOD_RESP  = "USERMODREQ";
    
    public final static String EXTGW_CRDGRPENQ_REQ = "CGENQREQ";
    public final static String EXTGW_CRDGRPENQ_RESP = "CGENQRESP";

    public final static String USSD_GIFTRECHARGE_REQ = "GFTRCREQ";
    public final static String USSD_GIFTRECHARGE_RESP = "GFTRCRESP";

    public final static String USSD_CUSTOMERRECHARGE_INTERNET_REQ = "INTRRCTRFREQ";
    public final static String USSD_CUSTOMERRECHARGE_INTERNET_RESP = "INTRNTRCRESP";

    public final static String USSD_PRIVATERECHARGE_REGISTRATION_REQ = "SIDREQ";
    public final static String USSD_PRIVATERECHARGE_REGISTRATION_RESP = "SIDRESP";

    public final static String USSD_RESUME_REQ = "RESREQ";
    public final static String USSD_RESUME_RESP = "RESRESP";

    public final static String USSD_SUSPEND_REQ = "SUSREQ";
    public final static String USSD_SUSPEND_RESP = "SUSRESP";

    public final static String EXTGW_SOSFLAGUPDATE_REQ = "SOSFLAGUPDATEREQ";
    public final static String EXTGW_SOSFLAGUPDATE_RESP = "SOSFLAGUPDATERESP";
    
    public final static String EXTGW_VOMSVAL_REQ = "VOMSVALREQ";
    public final static String EXTGW_VOMSVAL_RESP  = "VOMSVALRES";
    
    public final static String EXTGW_VOMSRSV_REQ  = "VOMSRSVREQ";
    public final static String EXTGW_VOURSV_RESP = "VOURSVRESP";
    
    public final static String EXTGW_VOMSDCONS_REQ = "VOMSDCONSREQ";
    public final static String EXTGW_VOMSDCONS_RESP = "VOMSDCONSRESP";
    
    public final static String EXTGW_VOUQRY_REQ = "VOUQRYREQ";
    public final static String EXTGW_VOUQRY_RESP = "VOUQRYRESP";
    
    public final static String EXTGW_VOMSSTCHG_REQ = "VOMSSTCHGREQ";
    public final static String EXTGW_VOMSSTCHG_RESP = "VOMSSTCHGRES";
    
    public final static String EXTGW_VOMSROLLBACK_REQ = "VOMSROLLBACKREQ";
    public final static String EXTGW_VOMSROLLBACK_RESP = "VOMSROLLBACKRES";
   
    public final static String EXTGW_VOMSEXPIRY_REQ = "VMSPINEXT";
    public final static String EXTGW_VOMSEXPIRY_RESP = "VMSPINEXT";


    public final static String USSD_ComEnquiry_REQ = "LTCOMREQ";
    public final static String USSD_ComEnquiry_RESP = "LTCOMRESP";
    public final static String USSD_GMB_REQ = "CGMBALREQ";
    public final static String USSD_GMB_RESP = "CGMBALRESP";
    
    
    //USSD Plain
    public final static String USSD_PLAIN_BALANCEENQUIRY_REQ = "BALREQ";
    public final static String USSD_PLAIN_BALANCEENQUIRY_RESP = "BALRESP";
    
    public final static String USSD_PLAIN_LASTXTRANSFERREPORT_REQ = "LXTSREQ";
    public final static String USSD_PLAIN_LASTXTRANSFERREPORT_RESP = "LXTSRESP";
    
    public final static String USSD_PLAIN_C2STRANSFER_REQ = "RCTRFREQ";
    public final static String USSD_PLAIN_C2STRANSFER_RESP = "RCTRFRESP";
    
    public final static String USSD_PLAIN_RETURN_REQ = "RETREQ";
    public final static String USSD_PLAIN_RETURN_RESP = "RETRESP";
    
    public final static String USSD_PLAIN_SETPIN_REQ = "RCPNREQ";
    public final static String USSD_PLAIN_SETPIN_RESP = "RCPNRESP";
    
    public final static String USSD_PLAIN_SETPIN_CP2P_REQ = "CCPNREQ";
    public final static String USSD_PLAIN_SETPIN_CP2P_RESP = "CCPNRESP";
    
    public final static String USSD_PLAIN_TRANSFER_REQ = "TRFREQ";
    public final static String USSD_PLAIN_TRANSFER_RESP = "TRFRESP";
    
    public final static String USSD_PLAIN_WITHDRAW_REQ = "WDTHREQ";
    public final static String USSD_PLAIN_WITHDRAW_RESP = "WDTHRESP";
    
    public final static String USSD_PLAIN_MULTIPLEEVD_REQ = "MVDREQ";
    public final static String USSD_PLAIN_MULTIPLEEVD_RESP = "EVDRESP";
    
    
    public final static String EXTGW_ADDALTERNATEMSISDN_REQ = "ADDALTNUMBER";
    public final static String EXTGW_ADDALTERNATEMSISDN_RESP = "ADDALTRNMBRRESP";
    
    
    public final static String EXTGW_MODALTERNATEMSISDN_REQ = "MODALTNUMBER";
    public final static String EXTGW_MODALTERNATEMSISDN_RESP = "MODALTRNMBRRESP";
    
    
    public final static String EXTGW_SELFPINRESET_REQ = "SELFPINRESETREQ";
    public final static String EXTGW_SELFPINRESET_RESP = "SELFPINRESETRESP";
    
    
    public final static String EXTGW_VOUCHERPINRESEND_REQ = "EXVOUPINRESEND";
    public final static String EXTGW_VOUCHERPINRESEND_RESP = "EXVOUPINRESENDRES";
    
    
    public final static String EXTGW_LASTXTRANSACTION_REQ = "LASTXTRANS";
    public final static String EXTGW_LASTXTRANSACTION_RESP = "LASTXTRANSRES";

    public final static String EXTGW_C2C_CONSENT_REQ = "C2CCONSENTREVREQ";
    public final static String EXTGW_C2C_CONSENT_RESP = "C2CCONSENTREVRESP";

    public final static String EXTGW_C2S_CONSENT_REQ ="C2SCONSENTREVREQ";
    public final static String EXTGW_C2S_CONSENT_RESP ="C2SCONSENTREVRESP";

    public final static String EXTGW_SELF_CARE_AIRTIME_REQ ="Airtime";
    public final static String EXTGW_SELF_CARE_AIRTIME_RESP ="AIRTIMEBUNDLERESP";

    public final static String EXTGW_SELF_CARE_BUNDLE_REQ ="Bundle";
    public final static String EXTGW_SELF_CARE_BUNDLE_RESP ="AIRTIMEBUNDLERESP";


    

    public static ArrayList<String> serviceResponseList = new ArrayList<String>(Arrays.asList(

            //EXTGW
            CHANGEPIN_RESP, SELFTPIN_RESP, O2CTRANSFER_RESP, USERBALANCE_RESP,
            O2CWITHDRAW_RESP, C2CTRANSFER_RESP, C2CRETURN_RESP, O2CRETURN_RESP,
            C2SGIFTRECHARGE_RESP, C2SCUSTOMERRECHARGE_RESP, C2SBILLPAYMENT_RESP,
            C2STRANSFERSTATUS_RESP, LASTTRANSACTION_RESP, LMSPOINTENQUIRY_RESP,
            SETNOTIFICATIONLANGUAGE_RESP, SUSPENDRESUME_RESP, OTHERBALANCEENQUIRY_RESP,
            LASTXTRANSACTION_RESP, C2CWITHDRAW_RESP, CREDIT_TRANSFER_RESP, ACC_INFO_RESP,
            P2PHISTORY_RESP, EXTGW_VMS_RESP, EXTGW_LMB_RESP, EXTGW_INTRC_RESP,
            EXTGW_FIXLINERC_RESP, C2S_REV_RESP, EXTGW_CRDGRPENQ_RESP, EXTGW_PRENQUIRY_RESP,
            EXTGW_PRIVATERECHARGE_REGISTRATION_RESP, EXTGW_USERADD_RESP, EXTGW_USERMOD_RESP,
            EXTGW_SOSFLAGUPDATE_RESP,EXTGW_VOMSVAL_RESP,EXTGW_VOURSV_RESP,EXTGW_VOMSDCONS_RESP,
            EXTGW_VOUQRY_RESP,EXTGW_VOMSSTCHG_RESP,EXTGW_VOMSROLLBACK_RESP,EXTGW_VOMSEXPIRY_RESP,
            EXTGW_ADDALTERNATEMSISDN_RESP,EXTGW_MODALTERNATEMSISDN_RESP,EXTGW_SELFPINRESET_RESP,
            EXTGW_VOUCHERPINRESEND_RESP,EXTGW_LASTXTRANSACTION_RESP,EXTGW_C2C_CONSENT_RESP,EXTGW_C2S_CONSENT_RESP,
            EXTGW_SELF_CARE_BUNDLE_RESP,EXTGW_SELF_CARE_AIRTIME_RESP,LOAN_OPTIN_RESP,LOAN_OPTOUT_RESP,

            //USSD
            BALANCEENQUIRY_RESP, LASTXTRANSFERREPORT_RESP, C2SLASTXTRANSFERREPORT_RESP,
            C2STRANSFER_RESP, C2SFIXLINE_RESP, DAILYSTATUSREPORT_RESP, EVD_RESP, EVR_RESP,
            LASTTRANSFERSTATUS_CP2P_RESP, LASTTRANSFERSTATUS_RP2P_RESP, MULTIPLEEVD_RESP,
            PRDEACTIVATION_RESP, PRENQUIRY_RESP, RETURN_RESP, SELFBAR_RESP,
            SETNOTIFICATIONLANGUAGE_CCLANG_RESP, SETPIN_RESP, SETPIN_CP2P_RESP, TRANSFER_RESP,
            WITHDRAW_RESP, INQUIRYBYTXNID_RESP, LAST5TXN_RESP, LAST5TXNWITHMSISDN_RESP, PPB_RESP,
            USSD_PROMOC2S_RESP, USSDCREDIT_TRANSFER_RESP, USSDACC_INFO_RESP, USSD_P2PHISTORY_RESP,
            EXTGW_VMS_RESP, USSD_GIFTRECHARGE_RESP, USSD_CUSTOMERRECHARGE_INTERNET_RESP,
            USSD_PRIVATERECHARGE_REGISTRATION_RESP, USSD_RESUME_RESP,USSD_ADDBUDDY_RESP, /*USSD_SELFTPIN_RESP,*/
            USSD_SUSPEND_RESP,
            
    		//USSD Plain
            USSD_PLAIN_BALANCEENQUIRY_RESP, USSD_PLAIN_LASTXTRANSFERREPORT_RESP, USSD_PLAIN_C2STRANSFER_RESP, USSD_PLAIN_RETURN_RESP, 
            USSD_PLAIN_SETPIN_RESP, USSD_PLAIN_SETPIN_CP2P_RESP, USSD_PLAIN_TRANSFER_RESP, USSD_PLAIN_WITHDRAW_RESP, USSD_PLAIN_MULTIPLEEVD_RESP,
            
            //XMLGW
            C2CTXNREV_RESP,O2CTXNREV_RESP
    		)
    );


}
