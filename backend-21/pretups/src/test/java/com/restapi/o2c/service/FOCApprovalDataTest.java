package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FOCApprovalDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FOCApprovalData}
     *   <li>{@link FOCApprovalData#setCurrentStatus(String)}
     *   <li>{@link FOCApprovalData#setExtNwCode(String)}
     *   <li>{@link FOCApprovalData#setExtTxnDate(String)}
     *   <li>{@link FOCApprovalData#setExtTxnNumber(String)}
     *   <li>{@link FOCApprovalData#setLanguage1(String)}
     *   <li>{@link FOCApprovalData#setLanguage2(String)}
     *   <li>{@link FOCApprovalData#setRefNumber(String)}
     *   <li>{@link FOCApprovalData#setRemarks(String)}
     *   <li>{@link FOCApprovalData#setStatus(String)}
     *   <li>{@link FOCApprovalData#setToMsisdn(String)}
     *   <li>{@link FOCApprovalData#setTxnId(String)}
     *   <li>{@link FOCApprovalData#toString()}
     *   <li>{@link FOCApprovalData#getCurrentStatus()}
     *   <li>{@link FOCApprovalData#getExtNwCode()}
     *   <li>{@link FOCApprovalData#getExtTxnDate()}
     *   <li>{@link FOCApprovalData#getExtTxnNumber()}
     *   <li>{@link FOCApprovalData#getLanguage1()}
     *   <li>{@link FOCApprovalData#getLanguage2()}
     *   <li>{@link FOCApprovalData#getRefNumber()}
     *   <li>{@link FOCApprovalData#getRemarks()}
     *   <li>{@link FOCApprovalData#getStatus()}
     *   <li>{@link FOCApprovalData#getToMsisdn()}
     *   <li>{@link FOCApprovalData#getTxnId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FOCApprovalData actualFocApprovalData = new FOCApprovalData();
        actualFocApprovalData.setCurrentStatus("Current Status");
        actualFocApprovalData.setExtNwCode("Ext Nw Code");
        actualFocApprovalData.setExtTxnDate("2020-03-01");
        actualFocApprovalData.setExtTxnNumber("42");
        actualFocApprovalData.setLanguage1("en");
        actualFocApprovalData.setLanguage2("en");
        actualFocApprovalData.setRefNumber("42");
        actualFocApprovalData.setRemarks("Remarks");
        actualFocApprovalData.setStatus("Status");
        actualFocApprovalData.setToMsisdn("To Msisdn");
        actualFocApprovalData.setTxnId("42");
        String actualToStringResult = actualFocApprovalData.toString();
        assertEquals("Current Status", actualFocApprovalData.getCurrentStatus());
        assertEquals("Ext Nw Code", actualFocApprovalData.getExtNwCode());
        assertEquals("2020-03-01", actualFocApprovalData.getExtTxnDate());
        assertEquals("42", actualFocApprovalData.getExtTxnNumber());
        assertEquals("en", actualFocApprovalData.getLanguage1());
        assertEquals("en", actualFocApprovalData.getLanguage2());
        assertEquals("42", actualFocApprovalData.getRefNumber());
        assertEquals("Remarks", actualFocApprovalData.getRemarks());
        assertEquals("Status", actualFocApprovalData.getStatus());
        assertEquals("To Msisdn", actualFocApprovalData.getToMsisdn());
        assertEquals("42", actualFocApprovalData.getTxnId());
        assertEquals("FOCApprovalData [currentStatus=Current Status, extNwCode=Ext Nw Code, status=Status, txnId=42,"
                + " remarks=Remarks, extTxnNumber=42, extTxnDate=2020-03-01, refNumber=42, toMsisdn=To Msisdn, language1=en,"
                + " language2=en]", actualToStringResult);
    }
}

