package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.apache.struts.upload.DiskFile;
import org.apache.struts.upload.FormFile;
import org.junit.Test;

public class DeRegisterSubscriberBatchVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DeRegisterSubscriberBatchVO}
     *   <li>{@link DeRegisterSubscriberBatchVO#setFileName(FormFile)}
     *   <li>{@link DeRegisterSubscriberBatchVO#setFileNameStr(String)}
     *   <li>{@link DeRegisterSubscriberBatchVO#getFileName()}
     *   <li>{@link DeRegisterSubscriberBatchVO#getFileNameStr()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DeRegisterSubscriberBatchVO actualDeRegisterSubscriberBatchVO = new DeRegisterSubscriberBatchVO();
        DiskFile name = new DiskFile("/directory/foo.txt");
        actualDeRegisterSubscriberBatchVO.setFileName(name);
        actualDeRegisterSubscriberBatchVO.setFileNameStr("Name Str");
        assertSame(name, actualDeRegisterSubscriberBatchVO.getFileName());
        assertEquals("Name Str", actualDeRegisterSubscriberBatchVO.getFileNameStr());
    }
}

