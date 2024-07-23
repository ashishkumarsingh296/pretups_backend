package com.btsl.user.businesslogic.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of channel_transfers.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "channel_transfers")
public class ChannelTransfers implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "transfer_id")
	private String transferId;

	@Column(name = "network_code")
	private String networkCode;

	@Column(name = "network_code_for")
	private String networkCodeFor;

	@Column(name = "grph_domain_code")
	private String grphDomainCode;

	@Column(name = "domain_code")
	private String domainCode;

	@Column(name = "sender_category_code")
	private String senderCategoryCode;

	@Column(name = "sender_grade_code")
	private String senderGradeCode;

	@Column(name = "receiver_grade_code")
	private String receiverGradeCode;

	@Column(name = "from_user_id")
	private String fromUserId;

	@Column(name = "to_user_id")
	private String toUserId;

	@Column(name = "transfer_date")
	private Date transferDate;

	@Column(name = "reference_no")
	private String referenceNo;

	@Column(name = "ext_txn_no")
	private String extTxnNo;

	@Column(name = "ext_txn_date")
	private Date extTxnDate;

	@Column(name = "commission_profile_set_id")
	private String commissionProfileSetId;

	@Column(name = "commission_profile_ver")
	private String commissionProfileVer;

	@Column(name = "requested_quantity")
	private Long requestedQuantity;

	@Column(name = "channel_user_remarks")
	private String channelUserRemarks;

	@Column(name = "first_approver_remarks")
	private String firstApproverRemarks;

	@Column(name = "second_approver_remarks")
	private String secondApproverRemarks;

	@Column(name = "third_approver_remarks")
	private String thirdApproverRemarks;

	@Column(name = "first_approved_by")
	private String firstApprovedBy;

	@Column(name = "first_approved_on")
	private Date firstApprovedOn;

	@Column(name = "second_approved_by")
	private String secondApprovedBy;

	@Column(name = "second_approved_on")
	private Date secondApprovedOn;

	@Column(name = "third_approved_by")
	private String thirdApprovedBy;

	@Column(name = "third_approved_on")
	private Date thirdApprovedOn;

	@Column(name = "cancelled_by")
	private String cancelledBy;

	@Column(name = "cancelled_on")
	private Date cancelledOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "modified_on")
	private Date modifiedOn;

	@Column(name = "status")
	private String status;

	@Column(name = "type")
	private String type;

	@Column(name = "transfer_initiated_by")
	private String transferInitiatedBy;

	@Column(name = "transfer_mrp")
	private Long transferMrp;

	@Column(name = "first_approver_limit")
	private Long firstApproverLimit;

	@Column(name = "second_approver_limit")
	private Long secondApproverLimit;

	@Column(name = "payable_amount")
	private Long payableAmount;

	@Column(name = "net_payable_amount")
	private Long netPayableAmount;

	@Column(name = "pmt_inst_type")
	private String pmtInstType;

	@Column(name = "pmt_inst_no")
	private String pmtInstNo;

	@Column(name = "pmt_inst_date")
	private Date pmtInstDate;

	@Column(name = "pmt_inst_amount")
	private Long pmtInstAmount;

	@Column(name = "sender_txn_profile")
	private String senderTxnProfile;

	@Column(name = "receiver_txn_profile")
	private String receiverTxnProfile;

	@Column(name = "total_tax1")
	private Long totalTax1;

	@Column(name = "total_tax2")
	private Long totalTax2;

	@Column(name = "total_tax3")
	private Long totalTax3;

	@Column(name = "source")
	private String source;

	@Column(name = "receiver_category_code")
	private String receiverCategoryCode;

	@Column(name = "request_gateway_code")
	private String requestGatewayCode;

	@Column(name = "request_gateway_type")
	private String requestGatewayType;

	@Column(name = "pmt_inst_source")
	private String pmtInstSource;

	@Column(name = "product_type")
	private String productType;

	@Column(name = "transfer_category")
	private String transferCategory;

	@Column(name = "transfer_type")
	private String transferType;

	@Column(name = "transfer_sub_type")
	private String transferSubType;

	@Column(name = "close_date")
	private Date closeDate;

	@Column(name = "batch_no")
	private String batchNo;

	@Column(name = "batch_date")
	private Date batchDate;

	@Column(name = "control_transfer")
	private String controlTransfer;

	@Column(name = "msisdn")
	private String msisdn;

	@Column(name = "to_msisdn")
	private String toMsisdn;

	@Column(name = "to_domain_code")
	private String toDomainCode;

	@Column(name = "to_grph_domain_code")
	private String toGrphDomainCode;

	@Column(name = "sms_default_lang")
	private String smsDefaultLang;

	@Column(name = "sms_second_lang")
	private String smsSecondLang;

	@Column(name = "foc_bonus_batch_date")
	private Date focBonusBatchDate;

	@Column(name = "foc_bonus_batch_no")
	private String focBonusBatchNo;

	@Column(name = "first_level_approved_quantity")
	private Long firstLevelApprovedQuantity;

	@Column(name = "second_level_approved_quantity")
	private Long secondLevelApprovedQuantity;

	@Column(name = "third_level_approved_quantity")
	private Long thirdLevelApprovedQuantity;

	@Column(name = "txn_wallet")
	private String txnWallet;

	@Column(name = "owner_transfer_mrp")
	private Long ownerTransferMrp;

	@Column(name = "owner_debit_mrp")
	private Long ownerDebitMrp;

	@Column(name = "bonus_type")
	private String bonusType;

	@Column(name = "active_user_id")
	private String activeUserId;

	@Column(name = "transaction_mode")
	private String transactionMode;

	@Column(name = "ref_transfer_id")
	private String refTransferId;

	@Column(name = "cell_id")
	private String cellId;

	@Column(name = "switch_id")
	private String switchId;

	@Column(name = "lms_point_adjust_value")
	private Long lmsPointAdjustValue;

	@Column(name = "stock_updated")
	private String stockUpdated;

	@Column(name = "sos_status")
	private String sosStatus;

	@Column(name = "sos_settlement_date")
	private Date sosSettlementDate;

	@Column(name = "info1")
	private String info1;

	@Column(name = "info2")
	private String info2;

	@Column(name = "dual_comm_type")
	private String dualCommType;

	@Column(name = "oth_comm_prf_set_id")
	private String othCommPrfSetId;

	@Column(name = "info3")
	private String info3;

	@Column(name = "info4")
	private String info4;

	@Column(name = "info5")
	private String info5;

	@Column(name = "pmt_inst_status")
	private String pmtInstStatus;

	@Column(name = "reconciliation_by")
	private String reconciliationBy;

	@Column(name = "reconciliation_date")
	private Date reconciliationDate;

	@Column(name = "reconciliation_flag")
	private String reconciliationFlag;

	@Column(name = "reconciliation_remark")
	private String reconciliationRemark;

	@Column(name = "info6")
	private String info6;

	@Column(name = "info7")
	private String info7;

	@Column(name = "info8")
	private String info8;

	@Column(name = "info9")
	private String info9;

	@Column(name = "info10")
	private String info10;

	@Column(name = "approval_doc_file_path")
	private String approvalDocFilePath;

	@Column(name = "approval_doc_type")
	private String approvalDocType;

	@Column(name = "approval_doc")
	private byte[] approvalDoc;


    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getTransferId());
    }

    /**
     * Equals.
     *
     * @param obj
     *            the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChannelTransfers other = (ChannelTransfers) obj;
        return Objects.equals(this.getTransferId(), other.getTransferId());
    }

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getNetworkCodeFor() {
		return networkCodeFor;
	}

	public void setNetworkCodeFor(String networkCodeFor) {
		this.networkCodeFor = networkCodeFor;
	}

	public String getGrphDomainCode() {
		return grphDomainCode;
	}

	public void setGrphDomainCode(String grphDomainCode) {
		this.grphDomainCode = grphDomainCode;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getSenderCategoryCode() {
		return senderCategoryCode;
	}

	public void setSenderCategoryCode(String senderCategoryCode) {
		this.senderCategoryCode = senderCategoryCode;
	}

	public String getSenderGradeCode() {
		return senderGradeCode;
	}

	public void setSenderGradeCode(String senderGradeCode) {
		this.senderGradeCode = senderGradeCode;
	}

	public String getReceiverGradeCode() {
		return receiverGradeCode;
	}

	public void setReceiverGradeCode(String receiverGradeCode) {
		this.receiverGradeCode = receiverGradeCode;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getExtTxnNo() {
		return extTxnNo;
	}

	public void setExtTxnNo(String extTxnNo) {
		this.extTxnNo = extTxnNo;
	}

	public Date getExtTxnDate() {
		return extTxnDate;
	}

	public void setExtTxnDate(Date extTxnDate) {
		this.extTxnDate = extTxnDate;
	}

	public String getCommissionProfileSetId() {
		return commissionProfileSetId;
	}

	public void setCommissionProfileSetId(String commissionProfileSetId) {
		this.commissionProfileSetId = commissionProfileSetId;
	}

	public String getCommissionProfileVer() {
		return commissionProfileVer;
	}

	public void setCommissionProfileVer(String commissionProfileVer) {
		this.commissionProfileVer = commissionProfileVer;
	}

	public Long getRequestedQuantity() {
		return requestedQuantity;
	}

	public void setRequestedQuantity(Long requestedQuantity) {
		this.requestedQuantity = requestedQuantity;
	}

	public String getChannelUserRemarks() {
		return channelUserRemarks;
	}

	public void setChannelUserRemarks(String channelUserRemarks) {
		this.channelUserRemarks = channelUserRemarks;
	}

	public String getFirstApproverRemarks() {
		return firstApproverRemarks;
	}

	public void setFirstApproverRemarks(String firstApproverRemarks) {
		this.firstApproverRemarks = firstApproverRemarks;
	}

	public String getSecondApproverRemarks() {
		return secondApproverRemarks;
	}

	public void setSecondApproverRemarks(String secondApproverRemarks) {
		this.secondApproverRemarks = secondApproverRemarks;
	}

	public String getThirdApproverRemarks() {
		return thirdApproverRemarks;
	}

	public void setThirdApproverRemarks(String thirdApproverRemarks) {
		this.thirdApproverRemarks = thirdApproverRemarks;
	}

	public String getFirstApprovedBy() {
		return firstApprovedBy;
	}

	public void setFirstApprovedBy(String firstApprovedBy) {
		this.firstApprovedBy = firstApprovedBy;
	}

	public Date getFirstApprovedOn() {
		return firstApprovedOn;
	}

	public void setFirstApprovedOn(Date firstApprovedOn) {
		this.firstApprovedOn = firstApprovedOn;
	}

	public String getSecondApprovedBy() {
		return secondApprovedBy;
	}

	public void setSecondApprovedBy(String secondApprovedBy) {
		this.secondApprovedBy = secondApprovedBy;
	}

	public Date getSecondApprovedOn() {
		return secondApprovedOn;
	}

	public void setSecondApprovedOn(Date secondApprovedOn) {
		this.secondApprovedOn = secondApprovedOn;
	}

	public String getThirdApprovedBy() {
		return thirdApprovedBy;
	}

	public void setThirdApprovedBy(String thirdApprovedBy) {
		this.thirdApprovedBy = thirdApprovedBy;
	}

	public Date getThirdApprovedOn() {
		return thirdApprovedOn;
	}

	public void setThirdApprovedOn(Date thirdApprovedOn) {
		this.thirdApprovedOn = thirdApprovedOn;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public Date getCancelledOn() {
		return cancelledOn;
	}

	public void setCancelledOn(Date cancelledOn) {
		this.cancelledOn = cancelledOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTransferInitiatedBy() {
		return transferInitiatedBy;
	}

	public void setTransferInitiatedBy(String transferInitiatedBy) {
		this.transferInitiatedBy = transferInitiatedBy;
	}

	public Long getTransferMrp() {
		return transferMrp;
	}

	public void setTransferMrp(Long transferMrp) {
		this.transferMrp = transferMrp;
	}

	public Long getFirstApproverLimit() {
		return firstApproverLimit;
	}

	public void setFirstApproverLimit(Long firstApproverLimit) {
		this.firstApproverLimit = firstApproverLimit;
	}

	public Long getSecondApproverLimit() {
		return secondApproverLimit;
	}

	public void setSecondApproverLimit(Long secondApproverLimit) {
		this.secondApproverLimit = secondApproverLimit;
	}

	public Long getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(Long payableAmount) {
		this.payableAmount = payableAmount;
	}

	public Long getNetPayableAmount() {
		return netPayableAmount;
	}

	public void setNetPayableAmount(Long netPayableAmount) {
		this.netPayableAmount = netPayableAmount;
	}

	public String getPmtInstType() {
		return pmtInstType;
	}

	public void setPmtInstType(String pmtInstType) {
		this.pmtInstType = pmtInstType;
	}

	public String getPmtInstNo() {
		return pmtInstNo;
	}

	public void setPmtInstNo(String pmtInstNo) {
		this.pmtInstNo = pmtInstNo;
	}

	public Date getPmtInstDate() {
		return pmtInstDate;
	}

	public void setPmtInstDate(Date pmtInstDate) {
		this.pmtInstDate = pmtInstDate;
	}

	public Long getPmtInstAmount() {
		return pmtInstAmount;
	}

	public void setPmtInstAmount(Long pmtInstAmount) {
		this.pmtInstAmount = pmtInstAmount;
	}

	public String getSenderTxnProfile() {
		return senderTxnProfile;
	}

	public void setSenderTxnProfile(String senderTxnProfile) {
		this.senderTxnProfile = senderTxnProfile;
	}

	public String getReceiverTxnProfile() {
		return receiverTxnProfile;
	}

	public void setReceiverTxnProfile(String receiverTxnProfile) {
		this.receiverTxnProfile = receiverTxnProfile;
	}

	public Long getTotalTax1() {
		return totalTax1;
	}

	public void setTotalTax1(Long totalTax1) {
		this.totalTax1 = totalTax1;
	}

	public Long getTotalTax2() {
		return totalTax2;
	}

	public void setTotalTax2(Long totalTax2) {
		this.totalTax2 = totalTax2;
	}

	public Long getTotalTax3() {
		return totalTax3;
	}

	public void setTotalTax3(Long totalTax3) {
		this.totalTax3 = totalTax3;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getReceiverCategoryCode() {
		return receiverCategoryCode;
	}

	public void setReceiverCategoryCode(String receiverCategoryCode) {
		this.receiverCategoryCode = receiverCategoryCode;
	}

	public String getRequestGatewayCode() {
		return requestGatewayCode;
	}

	public void setRequestGatewayCode(String requestGatewayCode) {
		this.requestGatewayCode = requestGatewayCode;
	}

	public String getRequestGatewayType() {
		return requestGatewayType;
	}

	public void setRequestGatewayType(String requestGatewayType) {
		this.requestGatewayType = requestGatewayType;
	}

	public String getPmtInstSource() {
		return pmtInstSource;
	}

	public void setPmtInstSource(String pmtInstSource) {
		this.pmtInstSource = pmtInstSource;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getTransferCategory() {
		return transferCategory;
	}

	public void setTransferCategory(String transferCategory) {
		this.transferCategory = transferCategory;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getTransferSubType() {
		return transferSubType;
	}

	public void setTransferSubType(String transferSubType) {
		this.transferSubType = transferSubType;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Date getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(Date batchDate) {
		this.batchDate = batchDate;
	}

	public String getControlTransfer() {
		return controlTransfer;
	}

	public void setControlTransfer(String controlTransfer) {
		this.controlTransfer = controlTransfer;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getToMsisdn() {
		return toMsisdn;
	}

	public void setToMsisdn(String toMsisdn) {
		this.toMsisdn = toMsisdn;
	}

	public String getToDomainCode() {
		return toDomainCode;
	}

	public void setToDomainCode(String toDomainCode) {
		this.toDomainCode = toDomainCode;
	}

	public String getToGrphDomainCode() {
		return toGrphDomainCode;
	}

	public void setToGrphDomainCode(String toGrphDomainCode) {
		this.toGrphDomainCode = toGrphDomainCode;
	}

	public String getSmsDefaultLang() {
		return smsDefaultLang;
	}

	public void setSmsDefaultLang(String smsDefaultLang) {
		this.smsDefaultLang = smsDefaultLang;
	}

	public String getSmsSecondLang() {
		return smsSecondLang;
	}

	public void setSmsSecondLang(String smsSecondLang) {
		this.smsSecondLang = smsSecondLang;
	}

	public Date getFocBonusBatchDate() {
		return focBonusBatchDate;
	}

	public void setFocBonusBatchDate(Date focBonusBatchDate) {
		this.focBonusBatchDate = focBonusBatchDate;
	}

	public String getFocBonusBatchNo() {
		return focBonusBatchNo;
	}

	public void setFocBonusBatchNo(String focBonusBatchNo) {
		this.focBonusBatchNo = focBonusBatchNo;
	}

	public Long getFirstLevelApprovedQuantity() {
		return firstLevelApprovedQuantity;
	}

	public void setFirstLevelApprovedQuantity(Long firstLevelApprovedQuantity) {
		this.firstLevelApprovedQuantity = firstLevelApprovedQuantity;
	}

	public Long getSecondLevelApprovedQuantity() {
		return secondLevelApprovedQuantity;
	}

	public void setSecondLevelApprovedQuantity(Long secondLevelApprovedQuantity) {
		this.secondLevelApprovedQuantity = secondLevelApprovedQuantity;
	}

	public Long getThirdLevelApprovedQuantity() {
		return thirdLevelApprovedQuantity;
	}

	public void setThirdLevelApprovedQuantity(Long thirdLevelApprovedQuantity) {
		this.thirdLevelApprovedQuantity = thirdLevelApprovedQuantity;
	}

	public String getTxnWallet() {
		return txnWallet;
	}

	public void setTxnWallet(String txnWallet) {
		this.txnWallet = txnWallet;
	}

	public Long getOwnerTransferMrp() {
		return ownerTransferMrp;
	}

	public void setOwnerTransferMrp(Long ownerTransferMrp) {
		this.ownerTransferMrp = ownerTransferMrp;
	}

	public Long getOwnerDebitMrp() {
		return ownerDebitMrp;
	}

	public void setOwnerDebitMrp(Long ownerDebitMrp) {
		this.ownerDebitMrp = ownerDebitMrp;
	}

	public String getBonusType() {
		return bonusType;
	}

	public void setBonusType(String bonusType) {
		this.bonusType = bonusType;
	}

	public String getActiveUserId() {
		return activeUserId;
	}

	public void setActiveUserId(String activeUserId) {
		this.activeUserId = activeUserId;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getRefTransferId() {
		return refTransferId;
	}

	public void setRefTransferId(String refTransferId) {
		this.refTransferId = refTransferId;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getSwitchId() {
		return switchId;
	}

	public void setSwitchId(String switchId) {
		this.switchId = switchId;
	}

	public Long getLmsPointAdjustValue() {
		return lmsPointAdjustValue;
	}

	public void setLmsPointAdjustValue(Long lmsPointAdjustValue) {
		this.lmsPointAdjustValue = lmsPointAdjustValue;
	}

	public String getStockUpdated() {
		return stockUpdated;
	}

	public void setStockUpdated(String stockUpdated) {
		this.stockUpdated = stockUpdated;
	}

	public String getSosStatus() {
		return sosStatus;
	}

	public void setSosStatus(String sosStatus) {
		this.sosStatus = sosStatus;
	}

	public Date getSosSettlementDate() {
		return sosSettlementDate;
	}

	public void setSosSettlementDate(Date sosSettlementDate) {
		this.sosSettlementDate = sosSettlementDate;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getDualCommType() {
		return dualCommType;
	}

	public void setDualCommType(String dualCommType) {
		this.dualCommType = dualCommType;
	}

	public String getOthCommPrfSetId() {
		return othCommPrfSetId;
	}

	public void setOthCommPrfSetId(String othCommPrfSetId) {
		this.othCommPrfSetId = othCommPrfSetId;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public String getInfo4() {
		return info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
	}

	public String getInfo5() {
		return info5;
	}

	public void setInfo5(String info5) {
		this.info5 = info5;
	}

	public String getPmtInstStatus() {
		return pmtInstStatus;
	}

	public void setPmtInstStatus(String pmtInstStatus) {
		this.pmtInstStatus = pmtInstStatus;
	}

	public String getReconciliationBy() {
		return reconciliationBy;
	}

	public void setReconciliationBy(String reconciliationBy) {
		this.reconciliationBy = reconciliationBy;
	}

	public Date getReconciliationDate() {
		return reconciliationDate;
	}

	public void setReconciliationDate(Date reconciliationDate) {
		this.reconciliationDate = reconciliationDate;
	}

	public String getReconciliationFlag() {
		return reconciliationFlag;
	}

	public void setReconciliationFlag(String reconciliationFlag) {
		this.reconciliationFlag = reconciliationFlag;
	}

	public String getReconciliationRemark() {
		return reconciliationRemark;
	}

	public void setReconciliationRemark(String reconciliationRemark) {
		this.reconciliationRemark = reconciliationRemark;
	}

	public String getInfo6() {
		return info6;
	}

	public void setInfo6(String info6) {
		this.info6 = info6;
	}

	public String getInfo7() {
		return info7;
	}

	public void setInfo7(String info7) {
		this.info7 = info7;
	}

	public String getInfo8() {
		return info8;
	}

	public void setInfo8(String info8) {
		this.info8 = info8;
	}

	public String getInfo9() {
		return info9;
	}

	public void setInfo9(String info9) {
		this.info9 = info9;
	}

	public String getInfo10() {
		return info10;
	}

	public void setInfo10(String info10) {
		this.info10 = info10;
	}

	public String getApprovalDocFilePath() {
		return approvalDocFilePath;
	}

	public void setApprovalDocFilePath(String approvalDocFilePath) {
		this.approvalDocFilePath = approvalDocFilePath;
	}

	public String getApprovalDocType() {
		return approvalDocType;
	}

	public void setApprovalDocType(String approvalDocType) {
		this.approvalDocType = approvalDocType;
	}

	public byte[] getApprovalDoc() {
		return approvalDoc;
	}

	public void setApprovalDoc(byte[] approvalDoc) {
		this.approvalDoc = approvalDoc;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}
