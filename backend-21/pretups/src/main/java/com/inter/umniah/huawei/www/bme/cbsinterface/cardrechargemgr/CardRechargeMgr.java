/**
 * CardRechargeMgr.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public interface CardRechargeMgr extends java.rmi.Remote {
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeResultMsg voucherRecharge(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeRequestMsg voucherRechargeRequestMsg) throws java.rmi.RemoteException;
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryResultMsg voucherRechargeEnquiry(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeEnquiryRequestMsg voucherRechargeEnquiryRequestMsg) throws java.rmi.RemoteException;
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqResultMsg voucherEnquiryBySeq(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryBySeqRequestMsg voucherEnquiryBySeqRequestMsg) throws java.rmi.RemoteException;
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINResultMsg voucherEnquiryByPIN(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherEnquiryByPINRequestMsg voucherEnquiryByPINRequestMsg) throws java.rmi.RemoteException;
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateResultMsg modifyVoucherState(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.ModifyVoucherStateRequestMsg modifyVoucherStateRequestMsg) throws java.rmi.RemoteException;
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqResultMsg voucherRechargeBySeq(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.VoucherRechargeBySeqRequestMsg voucherRechargeBySeqRequestMsg) throws java.rmi.RemoteException;
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackResultMsg deleteRechageBlack(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.DeleteRechageBlackRequestMsg deleteRechageBlackRequestMsg) throws java.rmi.RemoteException;
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackResultMsg batchDeleteRechageBlack(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.BatchDeleteRechageBlackRequestMsg batchDeleteRechageBlackRequestMsg) throws java.rmi.RemoteException;
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackResultMsg queryRechargeBlack(com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.QueryRechargeBlackRequestMsg queryRechargeBlackRequestMsg) throws java.rmi.RemoteException;
}
