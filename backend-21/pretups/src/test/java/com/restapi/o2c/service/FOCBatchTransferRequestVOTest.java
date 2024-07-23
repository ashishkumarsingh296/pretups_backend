package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class FOCBatchTransferRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FOCBatchTransferRequestVO}
     *   <li>{@link FOCBatchTransferRequestVO#setFOCBatchTransferDetails(FOCBatchTransferDetails)}
     *   <li>{@link FOCBatchTransferRequestVO#toString()}
     *   <li>{@link FOCBatchTransferRequestVO#getFOCBatchTransferDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FOCBatchTransferRequestVO actualFocBatchTransferRequestVO = new FOCBatchTransferRequestVO();
        FOCBatchTransferDetails focBatchTransferDetails = new FOCBatchTransferDetails();
        actualFocBatchTransferRequestVO.setFOCBatchTransferDetails(focBatchTransferDetails);
        String actualToStringResult = actualFocBatchTransferRequestVO.toString();
        assertSame(focBatchTransferDetails, actualFocBatchTransferRequestVO.getFOCBatchTransferDetails());
        assertEquals(
                "FOCBatchTransferRequestVO [FOCBatchTransferDetails=O2CBatchTransferDetails [language1=null, language2=null,"
                        + " geoDomain=null, channelDomain=null, usercategory=null, product=null, pin=null, batchName=null,"
                        + " fileAttachment=null, fileName=null, fileType=null]]",
                actualToStringResult);
    }
}

