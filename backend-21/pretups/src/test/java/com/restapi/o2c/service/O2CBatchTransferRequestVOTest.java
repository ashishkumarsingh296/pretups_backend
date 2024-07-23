package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class O2CBatchTransferRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBatchTransferRequestVO}
     *   <li>{@link O2CBatchTransferRequestVO#setO2CBatchTransferDetails(O2CBatchTransferDetails)}
     *   <li>{@link O2CBatchTransferRequestVO#toString()}
     *   <li>{@link O2CBatchTransferRequestVO#getO2CBatchTransferDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBatchTransferRequestVO actualO2cBatchTransferRequestVO = new O2CBatchTransferRequestVO();
        O2CBatchTransferDetails o2cBatchTransferDetails = new O2CBatchTransferDetails();
        actualO2cBatchTransferRequestVO.setO2CBatchTransferDetails(o2cBatchTransferDetails);
        String actualToStringResult = actualO2cBatchTransferRequestVO.toString();
        assertSame(o2cBatchTransferDetails, actualO2cBatchTransferRequestVO.getO2CBatchTransferDetails());
        assertEquals(
                "O2CBatchTransferRequestVO [o2CBatchTransferDetails=O2CBatchTransferDetails [language1=null, language2=null,"
                        + " geoDomain=null, channelDomain=null, usercategory=null, product=null, pin=null, batchName=null,"
                        + " fileAttachment=null, fileName=null, fileType=null]]",
                actualToStringResult);
    }
}

