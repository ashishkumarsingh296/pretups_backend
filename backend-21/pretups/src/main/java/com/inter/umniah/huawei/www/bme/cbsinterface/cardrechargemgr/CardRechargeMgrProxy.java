package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public class CardRechargeMgrProxy implements com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgr {
  private String _endpoint = null;
  private com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgr cardRechargeMgr = null;
  
  public CardRechargeMgrProxy() {
    _initCardRechargeMgrProxy();
  }
  
  public CardRechargeMgrProxy(String endpoint) {
    _endpoint = endpoint;
    _initCardRechargeMgrProxy();
  }
  
  private void _initCardRechargeMgrProxy() {
    try {
      cardRechargeMgr = (new com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrServiceLocator()).getCardRechargeMgrServicePort();
      if (cardRechargeMgr != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cardRechargeMgr)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cardRechargeMgr)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cardRechargeMgr != null)
      ((javax.xml.rpc.Stub)cardRechargeMgr)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgr getCardRechargeMgr() {
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr;
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg voucherRecharge(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeRequestMsg voucherRechargeRequestMsg) throws java.rmi.RemoteException{
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr.voucherRecharge(voucherRechargeRequestMsg);
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryResultMsg voucherRechargeEnquiry(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryRequestMsg voucherRechargeEnquiryRequestMsg) throws java.rmi.RemoteException{
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr.voucherRechargeEnquiry(voucherRechargeEnquiryRequestMsg);
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqResultMsg voucherEnquiryBySeq(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqRequestMsg voucherEnquiryBySeqRequestMsg) throws java.rmi.RemoteException{
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr.voucherEnquiryBySeq(voucherEnquiryBySeqRequestMsg);
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINResultMsg voucherEnquiryByPIN(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINRequestMsg voucherEnquiryByPINRequestMsg) throws java.rmi.RemoteException{
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr.voucherEnquiryByPIN(voucherEnquiryByPINRequestMsg);
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateResultMsg modifyVoucherState(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateRequestMsg modifyVoucherStateRequestMsg) throws java.rmi.RemoteException{
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr.modifyVoucherState(modifyVoucherStateRequestMsg);
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqResultMsg voucherRechargeBySeq(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqRequestMsg voucherRechargeBySeqRequestMsg) throws java.rmi.RemoteException{
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr.voucherRechargeBySeq(voucherRechargeBySeqRequestMsg);
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackResultMsg deleteRechageBlack(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackRequestMsg deleteRechageBlackRequestMsg) throws java.rmi.RemoteException{
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr.deleteRechageBlack(deleteRechageBlackRequestMsg);
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackResultMsg batchDeleteRechageBlack(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackRequestMsg batchDeleteRechageBlackRequestMsg) throws java.rmi.RemoteException{
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr.batchDeleteRechageBlack(batchDeleteRechageBlackRequestMsg);
  }
  
  public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackResultMsg queryRechargeBlack(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackRequestMsg queryRechargeBlackRequestMsg) throws java.rmi.RemoteException{
    if (cardRechargeMgr == null)
      _initCardRechargeMgrProxy();
    return cardRechargeMgr.queryRechargeBlack(queryRechargeBlackRequestMsg);
  }
  
  
}