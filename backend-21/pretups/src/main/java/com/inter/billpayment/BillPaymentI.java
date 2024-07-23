package com.inter.billpayment;

/**
 * @author vipan.kumar
 * @date 25 Oct 2010
 *       BillPayment interface
 */
public interface BillPaymentI {
    public int ACTION_PAY_BILL = 1;
    public int ACTION_RETRY_PAYMENT = 2;
    public int ACTION_ROLLBACK_PAYMENT = 3;
    public int ACTION_VALIDATE = 4;
    public int ACTION_CREDIT_ADJUST = 5;
    public int ACTION_DEBIT_ADJUST = 6;
    public int ACTION_DEPOSIT = 7;
}
