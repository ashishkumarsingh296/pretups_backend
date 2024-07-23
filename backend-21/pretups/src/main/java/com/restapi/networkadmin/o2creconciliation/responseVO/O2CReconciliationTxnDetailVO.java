package com.restapi.networkadmin.o2creconciliation.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class O2CReconciliationTxnDetailVO extends BaseResponse {
    private String transferId;
    private String networkName;
    private String domain;
    private String category;
    private String name;
    private String mobileNumber;
    private String erpCode;
    private String grade;
    private String productType;
    private String transferDate;
    private String transactionNumber;
    private String transactionDate;
    private String commissionProfile;
    private String transferProfile;
    private String referenceNumber;
    private List<ProductDetailVO> productDetails;
    private List<VoucherDetailVO> voucherDetails;
    private String initiatorRemarks;
    private String paymentMode;
    private String instrumentNumber;
    private String paymentDate;
    private String reconciliationRemark;
}
