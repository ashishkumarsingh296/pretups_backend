package com.restapi.c2sservices.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RescheduleBatchRechargeRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link RescheduleBatchRechargeRequestVO}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setBatchId(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setBatchType(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setCategoryCode(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setDomainCode(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setFileAttachment(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setFileName(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setFileType(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setFrequency(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setIteration(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setScheduleDate(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#setServiceCode(String)}
     *   <li>{@link RescheduleBatchRechargeRequestVO#toString()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getBatchId()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getBatchType()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getCategoryCode()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getDomainCode()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getFileAttachment()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getFileName()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getFileType()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getFrequency()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getIteration()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getScheduleDate()}
     *   <li>{@link RescheduleBatchRechargeRequestVO#getServiceCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        RescheduleBatchRechargeRequestVO actualRescheduleBatchRechargeRequestVO = new RescheduleBatchRechargeRequestVO();
        actualRescheduleBatchRechargeRequestVO.setBatchId("42");
        actualRescheduleBatchRechargeRequestVO.setBatchType("Batch Type");
        actualRescheduleBatchRechargeRequestVO.setCategoryCode("Category Code");
        actualRescheduleBatchRechargeRequestVO.setDomainCode("Domain Code");
        actualRescheduleBatchRechargeRequestVO.setFileAttachment("File Attachment");
        actualRescheduleBatchRechargeRequestVO.setFileName("foo.txt");
        actualRescheduleBatchRechargeRequestVO.setFileType("File Type");
        actualRescheduleBatchRechargeRequestVO.setFrequency("Frequency");
        actualRescheduleBatchRechargeRequestVO.setIteration("Iteration");
        actualRescheduleBatchRechargeRequestVO.setScheduleDate("2020-03-01");
        actualRescheduleBatchRechargeRequestVO.setServiceCode("Service Code");
        String actualToStringResult = actualRescheduleBatchRechargeRequestVO.toString();
        assertEquals("42", actualRescheduleBatchRechargeRequestVO.getBatchId());
        assertEquals("Batch Type", actualRescheduleBatchRechargeRequestVO.getBatchType());
        assertEquals("Category Code", actualRescheduleBatchRechargeRequestVO.getCategoryCode());
        assertEquals("Domain Code", actualRescheduleBatchRechargeRequestVO.getDomainCode());
        assertEquals("File Attachment", actualRescheduleBatchRechargeRequestVO.getFileAttachment());
        assertEquals("foo.txt", actualRescheduleBatchRechargeRequestVO.getFileName());
        assertEquals("File Type", actualRescheduleBatchRechargeRequestVO.getFileType());
        assertEquals("Frequency", actualRescheduleBatchRechargeRequestVO.getFrequency());
        assertEquals("Iteration", actualRescheduleBatchRechargeRequestVO.getIteration());
        assertEquals("2020-03-01", actualRescheduleBatchRechargeRequestVO.getScheduleDate());
        assertEquals("Service Code", actualRescheduleBatchRechargeRequestVO.getServiceCode());
        assertEquals(
                "RescheduleBatchRechargeRequestVO [batchId=42, categoryCode=Category Code, domainCode=Domain Code,"
                        + " serviceCode=Service Code, fileName=foo.txt, fileAttachment=File Attachment, fileType=File Type,"
                        + " iteration=Iteration, frequency=Frequency, scheduleDate=2020-03-01, batchType=Batch Type]",
                actualToStringResult);
    }
}

