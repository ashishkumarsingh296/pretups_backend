package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class C2SEnquiryRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SEnquiryRequestVO}
     *   <li>{@link C2SEnquiryRequestVO#setFromDate(String)}
     *   <li>{@link C2SEnquiryRequestVO#setParentUserID(String)}
     *   <li>{@link C2SEnquiryRequestVO#setReceiverMsisdn(String)}
     *   <li>{@link C2SEnquiryRequestVO#setSenderMsisdn(String)}
     *   <li>{@link C2SEnquiryRequestVO#setService(String)}
     *   <li>{@link C2SEnquiryRequestVO#setStaffSearch(boolean)}
     *   <li>{@link C2SEnquiryRequestVO#setStaffUserID(String)}
     *   <li>{@link C2SEnquiryRequestVO#setToDate(String)}
     *   <li>{@link C2SEnquiryRequestVO#setTransferID(String)}
     *   <li>{@link C2SEnquiryRequestVO#toString()}
     *   <li>{@link C2SEnquiryRequestVO#getFromDate()}
     *   <li>{@link C2SEnquiryRequestVO#getParentUserID()}
     *   <li>{@link C2SEnquiryRequestVO#getReceiverMsisdn()}
     *   <li>{@link C2SEnquiryRequestVO#getSenderMsisdn()}
     *   <li>{@link C2SEnquiryRequestVO#getService()}
     *   <li>{@link C2SEnquiryRequestVO#getStaffUserID()}
     *   <li>{@link C2SEnquiryRequestVO#getToDate()}
     *   <li>{@link C2SEnquiryRequestVO#getTransferID()}
     *   <li>{@link C2SEnquiryRequestVO#isStaffSearch()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SEnquiryRequestVO actualC2sEnquiryRequestVO = new C2SEnquiryRequestVO();
        actualC2sEnquiryRequestVO.setFromDate("2020-03-01");
        actualC2sEnquiryRequestVO.setParentUserID("Parent User ID");
        actualC2sEnquiryRequestVO.setReceiverMsisdn("Reciever Msisdn");
        actualC2sEnquiryRequestVO.setSenderMsisdn("Sender Msisdn");
        actualC2sEnquiryRequestVO.setService("Service");
        actualC2sEnquiryRequestVO.setStaffSearch(true);
        actualC2sEnquiryRequestVO.setStaffUserID("Staff User ID");
        actualC2sEnquiryRequestVO.setToDate("2020-03-01");
        actualC2sEnquiryRequestVO.setTransferID("Transfer ID");
        String actualToStringResult = actualC2sEnquiryRequestVO.toString();
        assertEquals("2020-03-01", actualC2sEnquiryRequestVO.getFromDate());
        assertEquals("Parent User ID", actualC2sEnquiryRequestVO.getParentUserID());
        assertEquals("Reciever Msisdn", actualC2sEnquiryRequestVO.getReceiverMsisdn());
        assertEquals("Sender Msisdn", actualC2sEnquiryRequestVO.getSenderMsisdn());
        assertEquals("Service", actualC2sEnquiryRequestVO.getService());
        assertEquals("Staff User ID", actualC2sEnquiryRequestVO.getStaffUserID());
        assertEquals("2020-03-01", actualC2sEnquiryRequestVO.getToDate());
        assertEquals("Transfer ID", actualC2sEnquiryRequestVO.getTransferID());
        assertTrue(actualC2sEnquiryRequestVO.isStaffSearch());
        assertEquals("C2SEnquiryRequestVO [service=Service, fromDate=2020-03-01, toDate=2020-03-01, transferID=Transfer ID,"
                + " senderMsisdn=Sender Msisdn, receiverMsisdn=Reciever Msisdn, isStaffSearch=true, staffUserID=Staff"
                + " User ID]", actualToStringResult);
    }
}

