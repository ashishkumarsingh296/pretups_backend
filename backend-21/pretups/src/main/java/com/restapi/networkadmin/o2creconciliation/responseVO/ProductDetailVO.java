package com.restapi.networkadmin.o2creconciliation.responseVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailVO {
    private long productShortCode;
    private String productName;
    private long denomination;
    private String networkStock;
    private String requestedQuantity;
    private String tax1;
    private String tax1Type;
    private String tax1Rate;
    private String tax2;
    private String tax2Type;
    private String tax2Rate;
    private String commission;
    private String commissionType;
    private String commissionRate;
    private String cbc;
    private String cbcType;
    private String cbcRate;
    private String tds;
    private String tdsType;
    private String tdsRate;
    private String payableAmount;
    private String netPayableAmount;
    private String netCommissionQuantity;
    private String receiverCreditQuantity;
}
