package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class BulkAutoC2CSOSCreditLimitFileResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkAutoC2CSOSCreditLimitFileResponseVO}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#setFileAttachment(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#setFileName(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#setFileType(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#setMessage(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#setMessageCode(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#setTotalRecords(int)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#setValidRecords(int)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#set_errorFlag(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#set_errorList(ArrayList)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#set_noOfRecords(String)}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#getErrorMap()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#getFileAttachment()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#getFileName()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#getFileType()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#getMessage()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#getMessageCode()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#getTotalRecords()}
     *   <li>{@link BulkAutoC2CSOSCreditLimitFileResponseVO#getValidRecords()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkAutoC2CSOSCreditLimitFileResponseVO actualBulkAutoC2CSOSCreditLimitFileResponseVO = new BulkAutoC2CSOSCreditLimitFileResponseVO();
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.setErrorMap(errorMap);
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.setFileAttachment("File Attachment");
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.setFileName("foo.txt");
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.setFileType("File Type");
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.setMessage("Not all who wander are lost");
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.setMessageCode("Message Code");
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.setTotalRecords(1);
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.setValidRecords(1);
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.set_errorFlag("An error occurred");
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.set_errorList(new ArrayList());
        actualBulkAutoC2CSOSCreditLimitFileResponseVO.set_noOfRecords(" no Of Records");
        assertSame(errorMap, actualBulkAutoC2CSOSCreditLimitFileResponseVO.getErrorMap());
        assertEquals("File Attachment", actualBulkAutoC2CSOSCreditLimitFileResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualBulkAutoC2CSOSCreditLimitFileResponseVO.getFileName());
        assertEquals("File Type", actualBulkAutoC2CSOSCreditLimitFileResponseVO.getFileType());
        assertEquals("Not all who wander are lost", actualBulkAutoC2CSOSCreditLimitFileResponseVO.getMessage());
        assertEquals("Message Code", actualBulkAutoC2CSOSCreditLimitFileResponseVO.getMessageCode());
        assertEquals(1, actualBulkAutoC2CSOSCreditLimitFileResponseVO.getTotalRecords());
        assertEquals(1, actualBulkAutoC2CSOSCreditLimitFileResponseVO.getValidRecords());
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitFileResponseVO#get_errorFlag()}
     */
    @Test
    public void testGet_errorFlag() {
        assertNull((new BulkAutoC2CSOSCreditLimitFileResponseVO()).get_errorFlag());
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitFileResponseVO#get_errorList()}
     */
    @Test
    public void testGet_errorList() {
        assertNull((new BulkAutoC2CSOSCreditLimitFileResponseVO()).get_errorList());
    }

    /**
     * Method under test: {@link BulkAutoC2CSOSCreditLimitFileResponseVO#get_noOfRecords()}
     */
    @Test
    public void testGet_noOfRecords() {
        assertNull((new BulkAutoC2CSOSCreditLimitFileResponseVO()).get_noOfRecords());
    }
}

