package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class C2SBulkEvdRechargeRequestVOTest {
    /**
     * Method under test: {@link C2SBulkEvdRechargeRequestVO#getData()}
     */
    @Test
    public void testGetData() {
        assertNull((new C2SBulkEvdRechargeRequestVO()).getData());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SBulkEvdRechargeRequestVO}
     *   <li>{@link C2SBulkEvdRechargeRequestVO#setData(C2SBulkRechargeDetails)}
     *   <li>{@link C2SBulkEvdRechargeRequestVO#toString()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SBulkEvdRechargeRequestVO actualC2sBulkEvdRechargeRequestVO = new C2SBulkEvdRechargeRequestVO();
        C2SBulkRechargeDetails data = new C2SBulkRechargeDetails();
        data.setBatchType("Batch Type");
        data.setExtcode("Extcode");
        data.setExtnwcode("Extnwcode");
        data.setFile("File");
        data.setFileName("foo.txt");
        data.setFileType("File Type");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setNoOfDays("No Of Days");
        data.setOccurence("Occurence");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setScheduleDate("2020-03-01");
        data.setScheduleNow("Schedule Now");
        data.setUserid("Userid");
        actualC2sBulkEvdRechargeRequestVO.setData(data);
        String actualToStringResult = actualC2sBulkEvdRechargeRequestVO.toString();
        assertSame(data, actualC2sBulkEvdRechargeRequestVO.getData());
        assertEquals("C2SBulkRechargeRequestVO [data=C2SBulkRechargeDetails [extnwcode=Extnwcode, msisdn=Msisdn, pin=Pin,"
                + " loginid=Loginid, password=iloveyou, extcode=Extcode, file=File, fileName=foo.txt, fileType=File Type,"
                + " batchType=Batch Type, scheduleNow=Schedule Now, scheduleDate=2020-03-01, occurence=Occurence, noOfDays=No"
                + " Of Days]]", actualToStringResult);
    }
}

