package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BulkAutoC2CSOSCreditLimitResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkAutoC2CSOSCreditLimitResponseVO}
     *   <li>{@link BulkAutoC2CSOSCreditLimitResponseVO#setFileName(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitResponseVO#setFileType(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitResponseVO#setFileattachment(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitResponseVO#getFileName()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitResponseVO#getFileType()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitResponseVO#getFileattachment()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkAutoC2CSOSCreditLimitResponseVO actualBulkAutoC2CSOSCreditLimitResponseVO = new BulkAutoC2CSOSCreditLimitResponseVO();
        actualBulkAutoC2CSOSCreditLimitResponseVO.setFileName("foo.txt");
        actualBulkAutoC2CSOSCreditLimitResponseVO.setFileType("File Type");
        actualBulkAutoC2CSOSCreditLimitResponseVO.setFileattachment("Fileattachment");
        assertEquals("foo.txt", actualBulkAutoC2CSOSCreditLimitResponseVO.getFileName());
        assertEquals("File Type", actualBulkAutoC2CSOSCreditLimitResponseVO.getFileType());
        assertEquals("Fileattachment", actualBulkAutoC2CSOSCreditLimitResponseVO.getFileattachment());
    }
}

