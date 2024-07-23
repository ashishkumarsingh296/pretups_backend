package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BatchFOCFileDownloadRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchFOCFileDownloadRequestVO}
     *   <li>{@link BatchFOCFileDownloadRequestVO#setCategory(String)}
     *   <li>{@link BatchFOCFileDownloadRequestVO#setDomain(String)}
     *   <li>{@link BatchFOCFileDownloadRequestVO#setFileType(String)}
     *   <li>{@link BatchFOCFileDownloadRequestVO#setGeography(String)}
     *   <li>{@link BatchFOCFileDownloadRequestVO#setProduct(String)}
     *   <li>{@link BatchFOCFileDownloadRequestVO#setSelectedCommissionWallet(String)}
     *   <li>{@link BatchFOCFileDownloadRequestVO#toString()}
     *   <li>{@link BatchFOCFileDownloadRequestVO#getCategory()}
     *   <li>{@link BatchFOCFileDownloadRequestVO#getDomain()}
     *   <li>{@link BatchFOCFileDownloadRequestVO#getFileType()}
     *   <li>{@link BatchFOCFileDownloadRequestVO#getGeography()}
     *   <li>{@link BatchFOCFileDownloadRequestVO#getProduct()}
     *   <li>{@link BatchFOCFileDownloadRequestVO#getSelectedCommissionWallet()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchFOCFileDownloadRequestVO actualBatchFOCFileDownloadRequestVO = new BatchFOCFileDownloadRequestVO();
        actualBatchFOCFileDownloadRequestVO.setCategory("Category");
        actualBatchFOCFileDownloadRequestVO.setDomain("Domain");
        actualBatchFOCFileDownloadRequestVO.setFileType("File Type");
        actualBatchFOCFileDownloadRequestVO.setGeography("Geography");
        actualBatchFOCFileDownloadRequestVO.setProduct("Product");
        actualBatchFOCFileDownloadRequestVO.setSelectedCommissionWallet("Selected Commission Wallet");
        String actualToStringResult = actualBatchFOCFileDownloadRequestVO.toString();
        assertEquals("Category", actualBatchFOCFileDownloadRequestVO.getCategory());
        assertEquals("Domain", actualBatchFOCFileDownloadRequestVO.getDomain());
        assertEquals("File Type", actualBatchFOCFileDownloadRequestVO.getFileType());
        assertEquals("Geography", actualBatchFOCFileDownloadRequestVO.getGeography());
        assertEquals("Product", actualBatchFOCFileDownloadRequestVO.getProduct());
        assertEquals("Selected Commission Wallet", actualBatchFOCFileDownloadRequestVO.getSelectedCommissionWallet());
        assertEquals("BatchFOCUserDownloadListController [domain=Domain, category=Category, geography=Geography,"
                + " product=Product]", actualToStringResult);
    }
}

