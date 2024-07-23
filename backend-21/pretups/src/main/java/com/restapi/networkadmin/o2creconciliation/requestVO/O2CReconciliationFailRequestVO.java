package com.restapi.networkadmin.o2creconciliation.requestVO;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class O2CReconciliationFailRequestVO {
    @NotNull
    String transferID;
    String externalTxnDate;
    String externalTxnNum;
    String paymentInstrumentCode;
    String paymentInstrumentNumber;
    String paymentInstrumentDate;
    String referenceNum;
    String reconciliationRemarks;
}
