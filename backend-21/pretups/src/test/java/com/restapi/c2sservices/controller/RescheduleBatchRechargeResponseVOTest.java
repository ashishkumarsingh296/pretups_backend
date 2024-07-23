package com.restapi.c2sservices.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RescheduleBatchRechargeResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link RescheduleBatchRechargeResponseVO}
     *   <li>{@link RescheduleBatchRechargeResponseVO#setBatchId(String)}
     *   <li>{@link RescheduleBatchRechargeResponseVO#setCancelledRecords(Integer)}
     *   <li>{@link RescheduleBatchRechargeResponseVO#setErrorFileAttachment(String)}
     *   <li>{@link RescheduleBatchRechargeResponseVO#setErrorFileName(String)}
     *   <li>{@link RescheduleBatchRechargeResponseVO#setFailedRecords(int)}
     *   <li>{@link RescheduleBatchRechargeResponseVO#setFileName(String)}
     *   <li>{@link RescheduleBatchRechargeResponseVO#setNoOfRecords(Integer)}
     *   <li>{@link RescheduleBatchRechargeResponseVO#setPreviousDate(String)}
     *   <li>{@link RescheduleBatchRechargeResponseVO#setRescheduleDate(String)}
     *   <li>{@link RescheduleBatchRechargeResponseVO#toString()}
     *   <li>{@link RescheduleBatchRechargeResponseVO#getBatchId()}
     *   <li>{@link RescheduleBatchRechargeResponseVO#getCancelledRecords()}
     *   <li>{@link RescheduleBatchRechargeResponseVO#getErrorFileAttachment()}
     *   <li>{@link RescheduleBatchRechargeResponseVO#getErrorFileName()}
     *   <li>{@link RescheduleBatchRechargeResponseVO#getFailedRecords()}
     *   <li>{@link RescheduleBatchRechargeResponseVO#getFileName()}
     *   <li>{@link RescheduleBatchRechargeResponseVO#getNoOfRecords()}
     *   <li>{@link RescheduleBatchRechargeResponseVO#getPreviousDate()}
     *   <li>{@link RescheduleBatchRechargeResponseVO#getRescheduleDate()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        RescheduleBatchRechargeResponseVO actualRescheduleBatchRechargeResponseVO = new RescheduleBatchRechargeResponseVO();
        actualRescheduleBatchRechargeResponseVO.setBatchId("42");
        actualRescheduleBatchRechargeResponseVO.setCancelledRecords(1);
        actualRescheduleBatchRechargeResponseVO.setErrorFileAttachment("An error occurred");
        actualRescheduleBatchRechargeResponseVO.setErrorFileName("An error occurred");
        actualRescheduleBatchRechargeResponseVO.setFailedRecords(-1);
        actualRescheduleBatchRechargeResponseVO.setFileName("foo.txt");
        actualRescheduleBatchRechargeResponseVO.setNoOfRecords(1);
        actualRescheduleBatchRechargeResponseVO.setPreviousDate("2020-03-01");
        actualRescheduleBatchRechargeResponseVO.setRescheduleDate("2020-03-01");
        String actualToStringResult = actualRescheduleBatchRechargeResponseVO.toString();
        assertEquals("42", actualRescheduleBatchRechargeResponseVO.getBatchId());
        assertEquals(1, actualRescheduleBatchRechargeResponseVO.getCancelledRecords().intValue());
        assertEquals("An error occurred", actualRescheduleBatchRechargeResponseVO.getErrorFileAttachment());
        assertEquals("An error occurred", actualRescheduleBatchRechargeResponseVO.getErrorFileName());
        assertEquals(-1, actualRescheduleBatchRechargeResponseVO.getFailedRecords());
        assertEquals("foo.txt", actualRescheduleBatchRechargeResponseVO.getFileName());
        assertEquals(1, actualRescheduleBatchRechargeResponseVO.getNoOfRecords().intValue());
        assertEquals("2020-03-01", actualRescheduleBatchRechargeResponseVO.getPreviousDate());
        assertEquals("2020-03-01", actualRescheduleBatchRechargeResponseVO.getRescheduleDate());
        assertEquals("RescheduleBatchRechargeResponseVO [noOfRecords=1, batchId=42, cancelledRecords=1, fileName=foo.txt,"
                + " failedRecords=-1, rescheduleDate=2020-03-01, previousDate=2020-03-01, errorFileAttachment=An error"
                + " occurred, errorFileName=An error occurred]", actualToStringResult);
    }
}

