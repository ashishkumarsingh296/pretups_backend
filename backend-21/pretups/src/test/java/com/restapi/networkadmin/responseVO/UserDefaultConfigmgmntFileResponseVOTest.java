package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class UserDefaultConfigmgmntFileResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserDefaultConfigmgmntFileResponseVO}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setErrorFlag(String)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setErrorList(ArrayList)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setFileAttachment(String)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setFileName(String)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setFileType(String)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setMessage(String)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setMessageCode(String)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setNoOfRecords(String)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setTotalRecords(int)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#setValidRecords(int)}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getErrorFlag()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getErrorList()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getErrorMap()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getFileAttachment()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getFileName()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getFileType()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getMessage()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getMessageCode()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getNoOfRecords()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getTotalRecords()}
     *   <li>{@link UserDefaultConfigmgmntFileResponseVO#getValidRecords()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserDefaultConfigmgmntFileResponseVO actualUserDefaultConfigmgmntFileResponseVO = new UserDefaultConfigmgmntFileResponseVO();
        actualUserDefaultConfigmgmntFileResponseVO.setErrorFlag("An error occurred");
        ArrayList errorList = new ArrayList();
        actualUserDefaultConfigmgmntFileResponseVO.setErrorList(errorList);
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualUserDefaultConfigmgmntFileResponseVO.setErrorMap(errorMap);
        actualUserDefaultConfigmgmntFileResponseVO.setFileAttachment("File Attachment");
        actualUserDefaultConfigmgmntFileResponseVO.setFileName("foo.txt");
        actualUserDefaultConfigmgmntFileResponseVO.setFileType("File Type");
        actualUserDefaultConfigmgmntFileResponseVO.setMessage("Not all who wander are lost");
        actualUserDefaultConfigmgmntFileResponseVO.setMessageCode("Message Code");
        actualUserDefaultConfigmgmntFileResponseVO.setNoOfRecords("No Of Records");
        actualUserDefaultConfigmgmntFileResponseVO.setTotalRecords(1);
        actualUserDefaultConfigmgmntFileResponseVO.setValidRecords(1);
        assertEquals("An error occurred", actualUserDefaultConfigmgmntFileResponseVO.getErrorFlag());
        assertSame(errorList, actualUserDefaultConfigmgmntFileResponseVO.getErrorList());
        assertSame(errorMap, actualUserDefaultConfigmgmntFileResponseVO.getErrorMap());
        assertEquals("File Attachment", actualUserDefaultConfigmgmntFileResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualUserDefaultConfigmgmntFileResponseVO.getFileName());
        assertEquals("File Type", actualUserDefaultConfigmgmntFileResponseVO.getFileType());
        assertEquals("Not all who wander are lost", actualUserDefaultConfigmgmntFileResponseVO.getMessage());
        assertEquals("Message Code", actualUserDefaultConfigmgmntFileResponseVO.getMessageCode());
        assertEquals("No Of Records", actualUserDefaultConfigmgmntFileResponseVO.getNoOfRecords());
        assertEquals(1, actualUserDefaultConfigmgmntFileResponseVO.getTotalRecords());
        assertEquals(1, actualUserDefaultConfigmgmntFileResponseVO.getValidRecords());
    }
}

