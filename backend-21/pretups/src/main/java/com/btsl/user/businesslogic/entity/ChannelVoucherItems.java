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
@Table(name = "channel_voucher_items")
public class ChannelVoucherItems implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "transfer_id")
	private String transferId;

	@Column(name = "transfer_date")
	private Date transferDate;

	@Column(name = "voucher_type")
	private String voucherType;

	@Column(name = "product_id")
	private String productId;

	@Column(name = "mrp")
	private Long mrp;

	@Column(name = "requested_quantity")
	private Long requestedQuantity;

	@Column(name = "from_serial_no")
	private String fromSerialNo;

	@Column(name = "to_serial_no")
	private String toSerialNo;

	@Column(name = "s_no")
	private Long sNo;

	@Column(name = "voucher_segment")
	private String voucherSegment;

	@Column(name = "network_code")
	private String networkCode;

	@Column(name = "type")
	private String type;

	@Column(name = "second_level_approved_quantity")
	private String secondLevelApprovedQuantity;

	@Column(name = "initiated_quantity")
	private String initiatedQuantity;

	@Column(name = "first_level_approved_quantity")
	private String firstLevelApprovedQuantity;

	@Column(name = "from_user")
	private String fromUser;

	@Column(name = "to_user")
	private String toUser;

	@Column(name = "modified_on")
	private Date modifiedOn;

	@Column(name = "bundle_id")
	private Long bundleId;

	@Column(name = "remarks")
	private String remarks;
	
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

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Long getMrp() {
		return mrp;
	}

	public void setMrp(Long mrp) {
		this.mrp = mrp;
	}

	public Long getRequestedQuantity() {
		return requestedQuantity;
	}

	public void setRequestedQuantity(Long requestedQuantity) {
		this.requestedQuantity = requestedQuantity;
	}

	public String getFromSerialNo() {
		return fromSerialNo;
	}

	public void setFromSerialNo(String fromSerialNo) {
		this.fromSerialNo = fromSerialNo;
	}

	public String getToSerialNo() {
		return toSerialNo;
	}

	public void setToSerialNo(String toSerialNo) {
		this.toSerialNo = toSerialNo;
	}

	public Long getsNo() {
		return sNo;
	}

	public void setsNo(Long sNo) {
		this.sNo = sNo;
	}

	public String getVoucherSegment() {
		return voucherSegment;
	}

	public void setVoucherSegment(String voucherSegment) {
		this.voucherSegment = voucherSegment;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSecondLevelApprovedQuantity() {
		return secondLevelApprovedQuantity;
	}

	public void setSecondLevelApprovedQuantity(String secondLevelApprovedQuantity) {
		this.secondLevelApprovedQuantity = secondLevelApprovedQuantity;
	}

	public String getInitiatedQuantity() {
		return initiatedQuantity;
	}

	public void setInitiatedQuantity(String initiatedQuantity) {
		this.initiatedQuantity = initiatedQuantity;
	}

	public String getFirstLevelApprovedQuantity() {
		return firstLevelApprovedQuantity;
	}

	public void setFirstLevelApprovedQuantity(String firstLevelApprovedQuantity) {
		this.firstLevelApprovedQuantity = firstLevelApprovedQuantity;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Long getBundleId() {
		return bundleId;
	}

	public void setBundleId(Long bundleId) {
		this.bundleId = bundleId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}



}
