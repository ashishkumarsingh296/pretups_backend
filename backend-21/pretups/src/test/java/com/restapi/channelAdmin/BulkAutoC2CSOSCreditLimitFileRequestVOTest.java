package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BulkAutoC2CSOSCreditLimitFileRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkAutoC2CSOSCreditLimitFileRequestVO}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileRequestVO#setFileAttachment(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileRequestVO#setFileName(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileRequestVO#setFileType(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileRequestVO#getFileAttachment()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileRequestVO#getFileName()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileRequestVO#getFileType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkAutoC2CSOSCreditLimitFileRequestVO actualBulkAutoC2CSOSCreditLimitFileRequestVO = new BulkAutoC2CSOSCreditLimitFileRequestVO();
        actualBulkAutoC2CSOSCreditLimitFileRequestVO.setFileAttachment("File Attachment");
        actualBulkAutoC2CSOSCreditLimitFileRequestVO.setFileName("foo.txt");
        actualBulkAutoC2CSOSCreditLimitFileRequestVO.setFileType("File Type");
        assertEquals("File Attachment", actualBulkAutoC2CSOSCreditLimitFileRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualBulkAutoC2CSOSCreditLimitFileRequestVO.getFileName());
        assertEquals("File Type", actualBulkAutoC2CSOSCreditLimitFileRequestVO.getFileType());
    }
}

