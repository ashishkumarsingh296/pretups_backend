package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2cAndO2cEnquiryRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2cAndO2cEnquiryRequestVO}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setCategory(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setDistributionType(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setDomain(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setFromDate(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setGeography(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setOrderStatus(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setReceiverMsisdn(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setSenderMsisdn(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setStaffLoginID(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setToDate(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setTransactionID(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setTransferCategory(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setTransferSubType(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setUserID(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#setUserType(String)}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#toString()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getCategory()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getDistributionType()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getDomain()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getFromDate()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getGeography()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getOrderStatus()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getReceiverMsisdn()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getSenderMsisdn()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getStaffLoginID()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getToDate()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getTransactionID()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getTransferCategory()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getTransferSubType()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getUserID()}
     *   <li>{@link C2cAndO2cEnquiryRequestVO#getUserType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2cAndO2cEnquiryRequestVO actualC2cAndO2cEnquiryRequestVO = new C2cAndO2cEnquiryRequestVO();
        actualC2cAndO2cEnquiryRequestVO.setCategory("Category");
        actualC2cAndO2cEnquiryRequestVO.setDistributionType("Distribution Type");
        actualC2cAndO2cEnquiryRequestVO.setDomain("Domain");
        actualC2cAndO2cEnquiryRequestVO.setFromDate("2020-03-01");
        actualC2cAndO2cEnquiryRequestVO.setGeography("Geography");
        actualC2cAndO2cEnquiryRequestVO.setOrderStatus("Order Status");
        actualC2cAndO2cEnquiryRequestVO.setReceiverMsisdn("Receiver Msisdn");
        actualC2cAndO2cEnquiryRequestVO.setSenderMsisdn("Sender Msisdn");
        actualC2cAndO2cEnquiryRequestVO.setStaffLoginID("Staff Login ID");
        actualC2cAndO2cEnquiryRequestVO.setToDate("2020-03-01");
        actualC2cAndO2cEnquiryRequestVO.setTransactionID("Transaction ID");
        actualC2cAndO2cEnquiryRequestVO.setTransferCategory("Transfer Category");
        actualC2cAndO2cEnquiryRequestVO.setTransferSubType("Transfer Sub Type");
        actualC2cAndO2cEnquiryRequestVO.setUserID("User ID");
        actualC2cAndO2cEnquiryRequestVO.setUserType("User Type");
        String actualToStringResult = actualC2cAndO2cEnquiryRequestVO.toString();
        assertEquals("Category", actualC2cAndO2cEnquiryRequestVO.getCategory());
        assertEquals("Distribution Type", actualC2cAndO2cEnquiryRequestVO.getDistributionType());
        assertEquals("Domain", actualC2cAndO2cEnquiryRequestVO.getDomain());
        assertEquals("2020-03-01", actualC2cAndO2cEnquiryRequestVO.getFromDate());
        assertEquals("Geography", actualC2cAndO2cEnquiryRequestVO.getGeography());
        assertEquals("Order Status", actualC2cAndO2cEnquiryRequestVO.getOrderStatus());
        assertEquals("Receiver Msisdn", actualC2cAndO2cEnquiryRequestVO.getReceiverMsisdn());
        assertEquals("Sender Msisdn", actualC2cAndO2cEnquiryRequestVO.getSenderMsisdn());
        assertEquals("Staff Login ID", actualC2cAndO2cEnquiryRequestVO.getStaffLoginID());
        assertEquals("2020-03-01", actualC2cAndO2cEnquiryRequestVO.getToDate());
        assertEquals("Transaction ID", actualC2cAndO2cEnquiryRequestVO.getTransactionID());
        assertEquals("Transfer Category", actualC2cAndO2cEnquiryRequestVO.getTransferCategory());
        assertEquals("Transfer Sub Type", actualC2cAndO2cEnquiryRequestVO.getTransferSubType());
        assertEquals("User ID", actualC2cAndO2cEnquiryRequestVO.getUserID());
        assertEquals("User Type", actualC2cAndO2cEnquiryRequestVO.getUserType());
        assertEquals("C2cAndO2cEnquiryRequestVO [transactionID=Transaction ID, transferSubType=Transfer Sub Type,"
                + " fromDate=2020-03-01, toDate=2020-03-01, senderMsisdn=Sender Msisdn, receiverMsisdn=Receiver Msisdn,"
                + " userID=User ID, distributionType=Distribution Type, orderStatus=Order Status, domain=Domain,"
                + " category=Category, geography=Geography, userType=User Type, staffLoginID=Staff Login ID, transferCategory"
                + "=Transfer Category]", actualToStringResult);
    }
}

