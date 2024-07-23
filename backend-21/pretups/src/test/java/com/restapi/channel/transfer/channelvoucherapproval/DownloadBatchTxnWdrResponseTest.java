package com.restapi.channel.transfer.channelvoucherapproval;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DownloadBatchTxnWdrResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DownloadBatchTxnWdrResponse}
     *   <li>{@link DownloadBatchTxnWdrResponse#setApprovedTransfers(String)}
     *   <li>{@link DownloadBatchTxnWdrResponse#setBatchName(String)}
     *   <li>{@link DownloadBatchTxnWdrResponse#setBatchNumber(String)}
     *   <li>{@link DownloadBatchTxnWdrResponse#setClosedTransfers(String)}
     *   <li>{@link DownloadBatchTxnWdrResponse#setNewTransfers(String)}
     *   <li>{@link DownloadBatchTxnWdrResponse#setProduct(String)}
     *   <li>{@link DownloadBatchTxnWdrResponse#setRejectedTransfers(String)}
     *   <li>{@link DownloadBatchTxnWdrResponse#setTotalTransfers(String)}
     *   <li>{@link DownloadBatchTxnWdrResponse#toString()}
     *   <li>{@link DownloadBatchTxnWdrResponse#getApprovedTransfers()}
     *   <li>{@link DownloadBatchTxnWdrResponse#getBatchName()}
     *   <li>{@link DownloadBatchTxnWdrResponse#getBatchNumber()}
     *   <li>{@link DownloadBatchTxnWdrResponse#getClosedTransfers()}
     *   <li>{@link DownloadBatchTxnWdrResponse#getNewTransfers()}
     *   <li>{@link DownloadBatchTxnWdrResponse#getProduct()}
     *   <li>{@link DownloadBatchTxnWdrResponse#getRejectedTransfers()}
     *   <li>{@link DownloadBatchTxnWdrResponse#getTotalTransfers()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DownloadBatchTxnWdrResponse actualDownloadBatchTxnWdrResponse = new DownloadBatchTxnWdrResponse();
        actualDownloadBatchTxnWdrResponse.setApprovedTransfers("Approved Transfers");
        actualDownloadBatchTxnWdrResponse.setBatchName("Batch Name");
        actualDownloadBatchTxnWdrResponse.setBatchNumber("42");
        actualDownloadBatchTxnWdrResponse.setClosedTransfers("Closed Transfers");
        actualDownloadBatchTxnWdrResponse.setNewTransfers("New Transfers");
        actualDownloadBatchTxnWdrResponse.setProduct("Product");
        actualDownloadBatchTxnWdrResponse.setRejectedTransfers("Rejected Transfers");
        actualDownloadBatchTxnWdrResponse.setTotalTransfers("Total Transfers");
        String actualToStringResult = actualDownloadBatchTxnWdrResponse.toString();
        assertEquals("Approved Transfers", actualDownloadBatchTxnWdrResponse.getApprovedTransfers());
        assertEquals("Batch Name", actualDownloadBatchTxnWdrResponse.getBatchName());
        assertEquals("42", actualDownloadBatchTxnWdrResponse.getBatchNumber());
        assertEquals("Closed Transfers", actualDownloadBatchTxnWdrResponse.getClosedTransfers());
        assertEquals("New Transfers", actualDownloadBatchTxnWdrResponse.getNewTransfers());
        assertEquals("Product", actualDownloadBatchTxnWdrResponse.getProduct());
        assertEquals("Rejected Transfers", actualDownloadBatchTxnWdrResponse.getRejectedTransfers());
        assertEquals("Total Transfers", actualDownloadBatchTxnWdrResponse.getTotalTransfers());
        assertEquals(
                "DownloadBatchTxnWdrResponse [batchNumber=42, batchName=Batch Name, product=Product, totalTransfers=Total"
                        + " Transfers, newTransfers=New Transfers, approvedTransfers=Approved Transfers, closedTransfers=Closed"
                        + " Transfers, rejectedTransfers=Rejected Transfers]",
                actualToStringResult);
    }
}

