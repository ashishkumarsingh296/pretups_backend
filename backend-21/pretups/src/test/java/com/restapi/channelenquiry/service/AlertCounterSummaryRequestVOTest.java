package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AlertCounterSummaryRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AlertCounterSummaryRequestVO}
     *   <li>{@link AlertCounterSummaryRequestVO#setCatCode(String)}
     *   <li>{@link AlertCounterSummaryRequestVO#setDomainCode(String)}
     *   <li>{@link AlertCounterSummaryRequestVO#setGeoCode(String)}
     *   <li>{@link AlertCounterSummaryRequestVO#setReqDate(String)}
     *   <li>{@link AlertCounterSummaryRequestVO#setReqMonth(String)}
     *   <li>{@link AlertCounterSummaryRequestVO#setThresholdType(String)}
     *   <li>{@link AlertCounterSummaryRequestVO#toString()}
     *   <li>{@link AlertCounterSummaryRequestVO#getCatCode()}
     *   <li>{@link AlertCounterSummaryRequestVO#getDomainCode()}
     *   <li>{@link AlertCounterSummaryRequestVO#getGeoCode()}
     *   <li>{@link AlertCounterSummaryRequestVO#getReqDate()}
     *   <li>{@link AlertCounterSummaryRequestVO#getReqMonth()}
     *   <li>{@link AlertCounterSummaryRequestVO#getThresholdType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AlertCounterSummaryRequestVO actualAlertCounterSummaryRequestVO = new AlertCounterSummaryRequestVO();
        actualAlertCounterSummaryRequestVO.setCatCode("Cat Code");
        actualAlertCounterSummaryRequestVO.setDomainCode("Domain Code");
        actualAlertCounterSummaryRequestVO.setGeoCode("Geo Code");
        actualAlertCounterSummaryRequestVO.setReqDate("2020-03-01");
        actualAlertCounterSummaryRequestVO.setReqMonth("Req Month");
        actualAlertCounterSummaryRequestVO.setThresholdType("Threshold Type");
        String actualToStringResult = actualAlertCounterSummaryRequestVO.toString();
        assertEquals("Cat Code", actualAlertCounterSummaryRequestVO.getCatCode());
        assertEquals("Domain Code", actualAlertCounterSummaryRequestVO.getDomainCode());
        assertEquals("Geo Code", actualAlertCounterSummaryRequestVO.getGeoCode());
        assertEquals("2020-03-01", actualAlertCounterSummaryRequestVO.getReqDate());
        assertEquals("Req Month", actualAlertCounterSummaryRequestVO.getReqMonth());
        assertEquals("Threshold Type", actualAlertCounterSummaryRequestVO.getThresholdType());
        assertEquals(
                "AlertCounterSummaryRequestVO [reqDate=2020-03-01, reqMonth=Req Month, geoCode=Geo Code, domainCode=Domain"
                        + " Code, catCode=Cat Code, thresholdType=Threshold Type]",
                actualToStringResult);
    }
}

