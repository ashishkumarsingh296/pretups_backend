package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class O2CBatchTransferDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CBatchTransferDetails}
     *   <li>{@link O2CBatchTransferDetails#setBatchName(String)}
     *   <li>{@link O2CBatchTransferDetails#setChannelDomain(String)}
     *   <li>{@link O2CBatchTransferDetails#setFileAttachment(String)}
     *   <li>{@link O2CBatchTransferDetails#setFileName(String)}
     *   <li>{@link O2CBatchTransferDetails#setFileType(String)}
     *   <li>{@link O2CBatchTransferDetails#setGeographicalDomain(String)}
     *   <li>{@link O2CBatchTransferDetails#setLanguage1(String)}
     *   <li>{@link O2CBatchTransferDetails#setLanguage2(String)}
     *   <li>{@link O2CBatchTransferDetails#setPin(String)}
     *   <li>{@link O2CBatchTransferDetails#setProduct(String)}
     *   <li>{@link O2CBatchTransferDetails#setUsercategory(String)}
     *   <li>{@link O2CBatchTransferDetails#toString()}
     *   <li>{@link O2CBatchTransferDetails#getBatchName()}
     *   <li>{@link O2CBatchTransferDetails#getChannelDomain()}
     *   <li>{@link O2CBatchTransferDetails#getFileAttachment()}
     *   <li>{@link O2CBatchTransferDetails#getFileName()}
     *   <li>{@link O2CBatchTransferDetails#getFileType()}
     *   <li>{@link O2CBatchTransferDetails#getGeographicalDomain()}
     *   <li>{@link O2CBatchTransferDetails#getLanguage1()}
     *   <li>{@link O2CBatchTransferDetails#getLanguage2()}
     *   <li>{@link O2CBatchTransferDetails#getPin()}
     *   <li>{@link O2CBatchTransferDetails#getProduct()}
     *   <li>{@link O2CBatchTransferDetails#getUsercategory()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CBatchTransferDetails actualO2cBatchTransferDetails = new O2CBatchTransferDetails();
        actualO2cBatchTransferDetails.setBatchName("Batch Name");
        actualO2cBatchTransferDetails.setChannelDomain("Channel Domain");
        actualO2cBatchTransferDetails.setFileAttachment("File Attachment");
        actualO2cBatchTransferDetails.setFileName("foo.txt");
        actualO2cBatchTransferDetails.setFileType("File Type");
        actualO2cBatchTransferDetails.setGeographicalDomain("Geo Domain");
        actualO2cBatchTransferDetails.setLanguage1("en");
        actualO2cBatchTransferDetails.setLanguage2("en");
        actualO2cBatchTransferDetails.setPin("Pin");
        actualO2cBatchTransferDetails.setProduct("Product");
        actualO2cBatchTransferDetails.setUsercategory("Usercategory");
        String actualToStringResult = actualO2cBatchTransferDetails.toString();
        assertEquals("Batch Name", actualO2cBatchTransferDetails.getBatchName());
        assertEquals("Channel Domain", actualO2cBatchTransferDetails.getChannelDomain());
        assertEquals("File Attachment", actualO2cBatchTransferDetails.getFileAttachment());
        assertEquals("foo.txt", actualO2cBatchTransferDetails.getFileName());
        assertEquals("File Type", actualO2cBatchTransferDetails.getFileType());
        assertEquals("Geo Domain", actualO2cBatchTransferDetails.getGeographicalDomain());
        assertEquals("en", actualO2cBatchTransferDetails.getLanguage1());
        assertEquals("en", actualO2cBatchTransferDetails.getLanguage2());
        assertEquals("Pin", actualO2cBatchTransferDetails.getPin());
        assertEquals("Product", actualO2cBatchTransferDetails.getProduct());
        assertEquals("Usercategory", actualO2cBatchTransferDetails.getUsercategory());
        assertEquals(
                "O2CBatchTransferDetails [language1=en, language2=en, geoDomain=Geo Domain, channelDomain=Channel Domain,"
                        + " usercategory=Usercategory, product=Product, pin=Pin, batchName=Batch Name, fileAttachment=File"
                        + " Attachment, fileName=foo.txt, fileType=File Type]",
                actualToStringResult);
    }
}

