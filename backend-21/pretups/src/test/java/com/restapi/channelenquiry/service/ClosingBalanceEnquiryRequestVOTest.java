package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClosingBalanceEnquiryRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ClosingBalanceEnquiryRequestVO}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#setCatCode(String)}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#setDomainCode(String)}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#setFileType(String)}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#setFromAmount(String)}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#setFromDate(String)}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#setToAmount(String)}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#setToDate(String)}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#setUserName(String)}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#setZoneCode(String)}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#toString()}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#getCatCode()}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#getDomainCode()}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#getFileType()}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#getFromAmount()}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#getFromDate()}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#getToAmount()}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#getToDate()}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#getUserName()}
     *   <li>{@link ClosingBalanceEnquiryRequestVO#getZoneCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ClosingBalanceEnquiryRequestVO actualClosingBalanceEnquiryRequestVO = new ClosingBalanceEnquiryRequestVO();
        actualClosingBalanceEnquiryRequestVO.setCatCode("Cat Code");
        actualClosingBalanceEnquiryRequestVO.setDomainCode("Domain Code");
        actualClosingBalanceEnquiryRequestVO.setFileType("File Type");
        actualClosingBalanceEnquiryRequestVO.setFromAmount("10");
        actualClosingBalanceEnquiryRequestVO.setFromDate("2020-03-01");
        actualClosingBalanceEnquiryRequestVO.setToAmount("10");
        actualClosingBalanceEnquiryRequestVO.setToDate("2020-03-01");
        actualClosingBalanceEnquiryRequestVO.setUserName("janedoe");
        actualClosingBalanceEnquiryRequestVO.setZoneCode("Zone");
        String actualToStringResult = actualClosingBalanceEnquiryRequestVO.toString();
        assertEquals("Cat Code", actualClosingBalanceEnquiryRequestVO.getCatCode());
        assertEquals("Domain Code", actualClosingBalanceEnquiryRequestVO.getDomainCode());
        assertEquals("File Type", actualClosingBalanceEnquiryRequestVO.getFileType());
        assertEquals("10", actualClosingBalanceEnquiryRequestVO.getFromAmount());
        assertEquals("2020-03-01", actualClosingBalanceEnquiryRequestVO.getFromDate());
        assertEquals("10", actualClosingBalanceEnquiryRequestVO.getToAmount());
        assertEquals("2020-03-01", actualClosingBalanceEnquiryRequestVO.getToDate());
        assertEquals("janedoe", actualClosingBalanceEnquiryRequestVO.getUserName());
        assertEquals("Zone", actualClosingBalanceEnquiryRequestVO.getZoneCode());
        assertEquals("ClosingBalanceEnquiryRequestVO [fromDate=2020-03-01, toDate=2020-03-01, zone=Zone, domainCode=Domain"
                + " Code, catCode=Cat Code, userName=janedoe, fromAmount=10, toAmount=10]", actualToStringResult);
    }
}

