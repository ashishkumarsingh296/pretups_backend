/**
 * ServicePortalBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmWebService.stub;

public class ServicePortalBindingImpl implements com.inter.righttel.crmWebService.stub.ServicePortalPortType{
    public void setSupService(java.lang.String MSISDN, java.lang.String serviceCode, java.lang.String action) throws java.rmi.RemoteException {
    }

    public void checkRightelSubscriber(java.lang.String MSISDN) throws java.rmi.RemoteException {
    }

    public void updateLanguage(java.lang.String MSISDN, java.lang.String defLang) throws java.rmi.RemoteException {
    }

    public void MCAQuery(java.lang.String MSISDN) throws java.rmi.RemoteException {
    }

    public void authenticationWithMSISDN(java.lang.String MSISDN, java.lang.String userPwd, java.lang.String pwdType) throws java.rmi.RemoteException {
    }

    public void resetUserPassword(java.lang.String MSISDN, java.lang.String newPassword, java.lang.String pwdType) throws java.rmi.RemoteException {
    }

    public void newQueryProfile2(javax.xml.rpc.holders.StringHolder MSISDN, javax.xml.rpc.holders.StringHolder brandName, javax.xml.rpc.holders.StringHolder SIMStatus, javax.xml.rpc.holders.StringHolder defLang, javax.xml.rpc.holders.StringHolder SIMSubStatus, javax.xml.rpc.holders.StringHolder custGrade, javax.xml.rpc.holders.StringHolder fixContact, javax.xml.rpc.holders.StringHolder docNum, javax.xml.rpc.holders.StringHolder docType, javax.xml.rpc.holders.StringHolder customerName, javax.xml.rpc.holders.StringHolder ISPREPAID, javax.xml.rpc.holders.StringHolder brandCode, javax.xml.rpc.holders.StringHolder custType, javax.xml.rpc.holders.StringHolder VCBlackList) throws java.rmi.RemoteException {
        brandName.value = new java.lang.String();
        SIMStatus.value = new java.lang.String();
        defLang.value = new java.lang.String();
        SIMSubStatus.value = new java.lang.String();
        custGrade.value = new java.lang.String();
        fixContact.value = new java.lang.String();
        docNum.value = new java.lang.String();
        docType.value = new java.lang.String();
        customerName.value = new java.lang.String();
        ISPREPAID.value = new java.lang.String();
        brandCode.value = new java.lang.String();
        custType.value = new java.lang.String();
        VCBlackList.value = new java.lang.String();
    }

    public void querySubsAvailableOfferList(java.lang.String MSISDN, java.lang.String channelID, java.lang.String offerPrice, java.lang.String speedValue, java.lang.String duration, java.lang.String volume, java.lang.String smsNum, java.lang.String pageIndex, java.lang.String rowPerPage, com.inter.righttel.crmWebService.stub.holders.OfferDtoListimplArrayHolder offerDtoList, com.inter.righttel.crmWebService.stub.holders.StringArrayHolder brandName) throws java.rmi.RemoteException {
        offerDtoList.value = new com.inter.righttel.crmWebService.stub.OfferDtoListimpl[0];
        brandName.value = new java.lang.String[0];
    }

    public void queryPricePlanOfferChargeFee(java.lang.String MSISDN, java.lang.String channelID, java.lang.String offerCode, java.lang.String payFlag, javax.xml.rpc.holders.StringHolder offerFee, javax.xml.rpc.holders.StringHolder tax, javax.xml.rpc.holders.StringHolder maximumDiscount) throws java.rmi.RemoteException {
        offerFee.value = new java.lang.String();
        tax.value = new java.lang.String();
        maximumDiscount.value = new java.lang.String();
    }

    public com.inter.righttel.crmWebService.stub.AllBalanceDtoListimpl[] queryAllBalance(java.lang.String MSISDN) throws java.rmi.RemoteException {
        return null;
    }

    public void queryRechargeResult(java.lang.String MSISDN, java.lang.String requestID, javax.xml.rpc.holders.StringHolder result, javax.xml.rpc.holders.StringHolder exceptionCode) throws java.rmi.RemoteException {
        result.value = new java.lang.String();
        exceptionCode.value = new java.lang.String();
    }

    public java.lang.String orderPricePlanOffer(java.lang.String MSISDN, java.lang.String offerCode, java.lang.String channelID, java.lang.String payFlag, java.lang.String AU, java.lang.String amount, java.lang.String discountFee, java.lang.String bankID, java.lang.String callerID) throws java.rmi.RemoteException {
        return null;
    }

    public void rechargePPSNew(java.lang.String REQUEST_ID, java.lang.String MSISDN, java.lang.String amount, java.lang.String bankId, java.lang.String AU, java.lang.String paymentType, com.inter.righttel.crmWebService.stub.FaceValueDtoimpl[] faceValueDtoList, java.lang.String callerID, java.lang.String paymentMethod, javax.xml.rpc.holders.StringHolder balance, javax.xml.rpc.holders.StringHolder expDate, javax.xml.rpc.holders.StringHolder addBalance, com.inter.righttel.crmWebService.stub.holders.BenefitBalDtoListimplArrayHolder benefitBalDtoList) throws java.rmi.RemoteException {
        balance.value = new java.lang.String();
        expDate.value = new java.lang.String();
        addBalance.value = new java.lang.String();
        benefitBalDtoList.value = new com.inter.righttel.crmWebService.stub.BenefitBalDtoimpl[0][0];
    }

    public void checkCreditLimit(java.lang.String MSISDN, javax.xml.rpc.holders.StringHolder balance, javax.xml.rpc.holders.StringHolder creditLimit, javax.xml.rpc.holders.StringHolder defaultCL, javax.xml.rpc.holders.StringHolder nonDefaultCL, javax.xml.rpc.holders.StringHolder creditUsed, javax.xml.rpc.holders.StringHolder creditAvailable) throws java.rmi.RemoteException {
        balance.value = new java.lang.String();
        creditLimit.value = new java.lang.String();
        defaultCL.value = new java.lang.String();
        nonDefaultCL.value = new java.lang.String();
        creditUsed.value = new java.lang.String();
        creditAvailable.value = new java.lang.String();
    }

}
