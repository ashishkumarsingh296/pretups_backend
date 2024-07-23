package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TransferProfileProductReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TransferProfileProductReqVO}
     *   <li>{@link TransferProfileProductReqVO#setAllowedMaxPercentage(String)}
     *   <li>{@link TransferProfileProductReqVO#setAllowedMaxPercentageInt(int)}
     *   <li>{@link TransferProfileProductReqVO#setAltBalance(String)}
     *   <li>{@link TransferProfileProductReqVO#setAltBalanceLong(long)}
     *   <li>{@link TransferProfileProductReqVO#setC2sMaxTxnAmt(String)}
     *   <li>{@link TransferProfileProductReqVO#setC2sMaxTxnAmtAsLong(long)}
     *   <li>{@link TransferProfileProductReqVO#setC2sMinTxnAmt(String)}
     *   <li>{@link TransferProfileProductReqVO#setC2sMinTxnAmtAsLong(long)}
     *   <li>{@link TransferProfileProductReqVO#setCurrentBalance(String)}
     *   <li>{@link TransferProfileProductReqVO#setMaxBalance(String)}
     *   <li>{@link TransferProfileProductReqVO#setMaxBalanceAsLong(long)}
     *   <li>{@link TransferProfileProductReqVO#setMinBalance(String)}
     *   <li>{@link TransferProfileProductReqVO#setMinResidualBalanceAsLong(long)}
     *   <li>{@link TransferProfileProductReqVO#setProductCode(String)}
     *   <li>{@link TransferProfileProductReqVO#setProductName(String)}
     *   <li>{@link TransferProfileProductReqVO#setProductShortCode(String)}
     *   <li>{@link TransferProfileProductReqVO#getAllowedMaxPercentage()}
     *   <li>{@link TransferProfileProductReqVO#getAllowedMaxPercentageInt()}
     *   <li>{@link TransferProfileProductReqVO#getAltBalance()}
     *   <li>{@link TransferProfileProductReqVO#getAltBalanceLong()}
     *   <li>{@link TransferProfileProductReqVO#getC2sMaxTxnAmt()}
     *   <li>{@link TransferProfileProductReqVO#getC2sMaxTxnAmtAsLong()}
     *   <li>{@link TransferProfileProductReqVO#getC2sMinTxnAmt()}
     *   <li>{@link TransferProfileProductReqVO#getC2sMinTxnAmtAsLong()}
     *   <li>{@link TransferProfileProductReqVO#getCurrentBalance()}
     *   <li>{@link TransferProfileProductReqVO#getMaxBalance()}
     *   <li>{@link TransferProfileProductReqVO#getMaxBalanceAsLong()}
     *   <li>{@link TransferProfileProductReqVO#getMinBalance()}
     *   <li>{@link TransferProfileProductReqVO#getMinResidualBalanceAsLong()}
     *   <li>{@link TransferProfileProductReqVO#getProductCode()}
     *   <li>{@link TransferProfileProductReqVO#getProductName()}
     *   <li>{@link TransferProfileProductReqVO#getProductShortCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TransferProfileProductReqVO actualTransferProfileProductReqVO = new TransferProfileProductReqVO();
        actualTransferProfileProductReqVO.setAllowedMaxPercentage("Allowed Max Percentage");
        actualTransferProfileProductReqVO.setAllowedMaxPercentageInt(1);
        actualTransferProfileProductReqVO.setAltBalance("Alt Balance");
        actualTransferProfileProductReqVO.setAltBalanceLong(42L);
        actualTransferProfileProductReqVO.setC2sMaxTxnAmt("C2s Max Txn Amt");
        actualTransferProfileProductReqVO.setC2sMaxTxnAmtAsLong(1L);
        actualTransferProfileProductReqVO.setC2sMinTxnAmt("C2s Min Txn Amt");
        actualTransferProfileProductReqVO.setC2sMinTxnAmtAsLong(1L);
        actualTransferProfileProductReqVO.setCurrentBalance("Current Balance");
        actualTransferProfileProductReqVO.setMaxBalance("Max Balance");
        actualTransferProfileProductReqVO.setMaxBalanceAsLong(42L);
        actualTransferProfileProductReqVO.setMinBalance("Min Balance");
        actualTransferProfileProductReqVO.setMinResidualBalanceAsLong(1L);
        actualTransferProfileProductReqVO.setProductCode("Product Code");
        actualTransferProfileProductReqVO.setProductName("Product Name");
        actualTransferProfileProductReqVO.setProductShortCode("Product Short Code");
        assertEquals("Allowed Max Percentage", actualTransferProfileProductReqVO.getAllowedMaxPercentage());
        assertEquals(1, actualTransferProfileProductReqVO.getAllowedMaxPercentageInt());
        assertEquals("Alt Balance", actualTransferProfileProductReqVO.getAltBalance());
        assertEquals(42L, actualTransferProfileProductReqVO.getAltBalanceLong());
        assertEquals("C2s Max Txn Amt", actualTransferProfileProductReqVO.getC2sMaxTxnAmt());
        assertEquals(1L, actualTransferProfileProductReqVO.getC2sMaxTxnAmtAsLong());
        assertEquals("C2s Min Txn Amt", actualTransferProfileProductReqVO.getC2sMinTxnAmt());
        assertEquals(1L, actualTransferProfileProductReqVO.getC2sMinTxnAmtAsLong());
        assertEquals("Current Balance", actualTransferProfileProductReqVO.getCurrentBalance());
        assertEquals("Max Balance", actualTransferProfileProductReqVO.getMaxBalance());
        assertEquals(42L, actualTransferProfileProductReqVO.getMaxBalanceAsLong());
        assertEquals("Min Balance", actualTransferProfileProductReqVO.getMinBalance());
        assertEquals(1L, actualTransferProfileProductReqVO.getMinResidualBalanceAsLong());
        assertEquals("Product Code", actualTransferProfileProductReqVO.getProductCode());
        assertEquals("Product Name", actualTransferProfileProductReqVO.getProductName());
        assertEquals("Product Short Code", actualTransferProfileProductReqVO.getProductShortCode());
    }
}

