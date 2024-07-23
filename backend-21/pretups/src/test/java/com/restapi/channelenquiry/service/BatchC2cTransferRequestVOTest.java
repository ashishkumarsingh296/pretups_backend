package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BatchC2cTransferRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchC2cTransferRequestVO}
     *   <li>{@link BatchC2cTransferRequestVO#setBatchId(String)}
     *   <li>{@link BatchC2cTransferRequestVO#setCategoryCode(String)}
     *   <li>{@link BatchC2cTransferRequestVO#setDomainCode(String)}
     *   <li>{@link BatchC2cTransferRequestVO#setFromDate(String)}
     *   <li>{@link BatchC2cTransferRequestVO#setGeographyCode(String)}
     *   <li>{@link BatchC2cTransferRequestVO#setProductCode(String)}
     *   <li>{@link BatchC2cTransferRequestVO#setToDate(String)}
     *   <li>{@link BatchC2cTransferRequestVO#setUserId(String)}
     *   <li>{@link BatchC2cTransferRequestVO#toString()}
     *   <li>{@link BatchC2cTransferRequestVO#getBatchId()}
     *   <li>{@link BatchC2cTransferRequestVO#getCategoryCode()}
     *   <li>{@link BatchC2cTransferRequestVO#getDomainCode()}
     *   <li>{@link BatchC2cTransferRequestVO#getFromDate()}
     *   <li>{@link BatchC2cTransferRequestVO#getGeographyCode()}
     *   <li>{@link BatchC2cTransferRequestVO#getProductCode()}
     *   <li>{@link BatchC2cTransferRequestVO#getToDate()}
     *   <li>{@link BatchC2cTransferRequestVO#getUserId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchC2cTransferRequestVO actualBatchC2cTransferRequestVO = new BatchC2cTransferRequestVO();
        actualBatchC2cTransferRequestVO.setBatchId("42");
        actualBatchC2cTransferRequestVO.setCategoryCode("Category Code");
        actualBatchC2cTransferRequestVO.setDomainCode("Domain Code");
        actualBatchC2cTransferRequestVO.setFromDate("2020-03-01");
        actualBatchC2cTransferRequestVO.setGeographyCode("Geography Code");
        actualBatchC2cTransferRequestVO.setProductCode("Product Code");
        actualBatchC2cTransferRequestVO.setToDate("2020-03-01");
        actualBatchC2cTransferRequestVO.setUserId("42");
        String actualToStringResult = actualBatchC2cTransferRequestVO.toString();
        assertEquals("42", actualBatchC2cTransferRequestVO.getBatchId());
        assertEquals("Category Code", actualBatchC2cTransferRequestVO.getCategoryCode());
        assertEquals("Domain Code", actualBatchC2cTransferRequestVO.getDomainCode());
        assertEquals("2020-03-01", actualBatchC2cTransferRequestVO.getFromDate());
        assertEquals("Geography Code", actualBatchC2cTransferRequestVO.getGeographyCode());
        assertEquals("Product Code", actualBatchC2cTransferRequestVO.getProductCode());
        assertEquals("2020-03-01", actualBatchC2cTransferRequestVO.getToDate());
        assertEquals("42", actualBatchC2cTransferRequestVO.getUserId());
        assertEquals(
                "BatchC2cTransferRequestVO [batchId=42, domainCode=Domain Code, categoryCode=Category Code, geographyCode"
                        + "=Geography Code, userId=42, productCode=Product Code, fromDate=2020-03-01, toDate=2020-03-01]",
                actualToStringResult);
    }
}

