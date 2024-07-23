package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FOCBatchTransferDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FOCBatchTransferDetails}
     *   <li>{@link FOCBatchTransferDetails#setBatchName(String)}
     *   <li>{@link FOCBatchTransferDetails#setChannelDomain(String)}
     *   <li>{@link FOCBatchTransferDetails#setFileAttachment(String)}
     *   <li>{@link FOCBatchTransferDetails#setFileName(String)}
     *   <li>{@link FOCBatchTransferDetails#setFileType(String)}
     *   <li>{@link FOCBatchTransferDetails#setGeographicalDomain(String)}
     *   <li>{@link FOCBatchTransferDetails#setLanguage1(String)}
     *   <li>{@link FOCBatchTransferDetails#setLanguage2(String)}
     *   <li>{@link FOCBatchTransferDetails#setOperatorWalletOption(String)}
     *   <li>{@link FOCBatchTransferDetails#setPin(String)}
     *   <li>{@link FOCBatchTransferDetails#setProduct(String)}
     *   <li>{@link FOCBatchTransferDetails#setUsercategory(String)}
     *   <li>{@link FOCBatchTransferDetails#toString()}
     *   <li>{@link FOCBatchTransferDetails#getBatchName()}
     *   <li>{@link FOCBatchTransferDetails#getChannelDomain()}
     *   <li>{@link FOCBatchTransferDetails#getFileAttachment()}
     *   <li>{@link FOCBatchTransferDetails#getFileName()}
     *   <li>{@link FOCBatchTransferDetails#getFileType()}
     *   <li>{@link FOCBatchTransferDetails#getGeographicalDomain()}
     *   <li>{@link FOCBatchTransferDetails#getLanguage1()}
     *   <li>{@link FOCBatchTransferDetails#getLanguage2()}
     *   <li>{@link FOCBatchTransferDetails#getOperatorWalletOption()}
     *   <li>{@link FOCBatchTransferDetails#getPin()}
     *   <li>{@link FOCBatchTransferDetails#getProduct()}
     *   <li>{@link FOCBatchTransferDetails#getUsercategory()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FOCBatchTransferDetails actualFocBatchTransferDetails = new FOCBatchTransferDetails();
        actualFocBatchTransferDetails.setBatchName("Batch Name");
        actualFocBatchTransferDetails.setChannelDomain("Channel Domain");
        actualFocBatchTransferDetails.setFileAttachment("File Attachment");
        actualFocBatchTransferDetails.setFileName("foo.txt");
        actualFocBatchTransferDetails.setFileType("File Type");
        actualFocBatchTransferDetails.setGeographicalDomain("Geo Domain");
        actualFocBatchTransferDetails.setLanguage1("en");
        actualFocBatchTransferDetails.setLanguage2("en");
        actualFocBatchTransferDetails.setOperatorWalletOption("Operator Wallet Option");
        actualFocBatchTransferDetails.setPin("Pin");
        actualFocBatchTransferDetails.setProduct("Product");
        actualFocBatchTransferDetails.setUsercategory("Usercategory");
        String actualToStringResult = actualFocBatchTransferDetails.toString();
        assertEquals("Batch Name", actualFocBatchTransferDetails.getBatchName());
        assertEquals("Channel Domain", actualFocBatchTransferDetails.getChannelDomain());
        assertEquals("File Attachment", actualFocBatchTransferDetails.getFileAttachment());
        assertEquals("foo.txt", actualFocBatchTransferDetails.getFileName());
        assertEquals("File Type", actualFocBatchTransferDetails.getFileType());
        assertEquals("Geo Domain", actualFocBatchTransferDetails.getGeographicalDomain());
        assertEquals("en", actualFocBatchTransferDetails.getLanguage1());
        assertEquals("en", actualFocBatchTransferDetails.getLanguage2());
        assertEquals("Operator Wallet Option", actualFocBatchTransferDetails.getOperatorWalletOption());
        assertEquals("Pin", actualFocBatchTransferDetails.getPin());
        assertEquals("Product", actualFocBatchTransferDetails.getProduct());
        assertEquals("Usercategory", actualFocBatchTransferDetails.getUsercategory());
        assertEquals(
                "O2CBatchTransferDetails [language1=en, language2=en, geoDomain=Geo Domain, channelDomain=Channel Domain,"
                        + " usercategory=Usercategory, product=Product, pin=Pin, batchName=Batch Name, fileAttachment=File"
                        + " Attachment, fileName=foo.txt, fileType=File Type]",
                actualToStringResult);
    }
}

