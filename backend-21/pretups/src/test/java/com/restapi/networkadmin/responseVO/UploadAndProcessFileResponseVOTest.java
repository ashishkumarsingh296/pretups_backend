package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ErrorMap;

import java.util.ArrayList;

import org.junit.Test;

public class UploadAndProcessFileResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UploadAndProcessFileResponseVO}
     *   <li>{@link UploadAndProcessFileResponseVO#setErrorFlag(String)}
     *   <li>{@link UploadAndProcessFileResponseVO#setErrorList(ArrayList)}
     *   <li>{@link UploadAndProcessFileResponseVO#setErrorMap(ErrorMap)}
     *   <li>{@link UploadAndProcessFileResponseVO#setFileAttachment(String)}
     *   <li>{@link UploadAndProcessFileResponseVO#setFileName(String)}
     *   <li>{@link UploadAndProcessFileResponseVO#setFileType(String)}
     *   <li>{@link UploadAndProcessFileResponseVO#setMessage(String)}
     *   <li>{@link UploadAndProcessFileResponseVO#setMessageCode(String)}
     *   <li>{@link UploadAndProcessFileResponseVO#setNoOfRecords(String)}
     *   <li>{@link UploadAndProcessFileResponseVO#setSubServiceTypeIdList(ArrayList)}
     *   <li>{@link UploadAndProcessFileResponseVO#setSubscriberStatus(String)}
     *   <li>{@link UploadAndProcessFileResponseVO#setSubscriberStatusList(ArrayList)}
     *   <li>{@link UploadAndProcessFileResponseVO#setTotalRecords(int)}
     *   <li>{@link UploadAndProcessFileResponseVO#setValidRecords(int)}
     *   <li>{@link UploadAndProcessFileResponseVO#toString()}
     *   <li>{@link UploadAndProcessFileResponseVO#getErrorFlag()}
     *   <li>{@link UploadAndProcessFileResponseVO#getErrorList()}
     *   <li>{@link UploadAndProcessFileResponseVO#getErrorMap()}
     *   <li>{@link UploadAndProcessFileResponseVO#getFileAttachment()}
     *   <li>{@link UploadAndProcessFileResponseVO#getFileName()}
     *   <li>{@link UploadAndProcessFileResponseVO#getFileType()}
     *   <li>{@link UploadAndProcessFileResponseVO#getMessage()}
     *   <li>{@link UploadAndProcessFileResponseVO#getMessageCode()}
     *   <li>{@link UploadAndProcessFileResponseVO#getNoOfRecords()}
     *   <li>{@link UploadAndProcessFileResponseVO#getSubServiceTypeIdList()}
     *   <li>{@link UploadAndProcessFileResponseVO#getSubscriberStatus()}
     *   <li>{@link UploadAndProcessFileResponseVO#getSubscriberStatusList()}
     *   <li>{@link UploadAndProcessFileResponseVO#getTotalRecords()}
     *   <li>{@link UploadAndProcessFileResponseVO#getValidRecords()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UploadAndProcessFileResponseVO actualUploadAndProcessFileResponseVO = new UploadAndProcessFileResponseVO();
        actualUploadAndProcessFileResponseVO.setErrorFlag("An error occurred");
        ArrayList errorList = new ArrayList();
        actualUploadAndProcessFileResponseVO.setErrorList(errorList);
        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        actualUploadAndProcessFileResponseVO.setErrorMap(errorMap);
        actualUploadAndProcessFileResponseVO.setFileAttachment("File Attachment");
        actualUploadAndProcessFileResponseVO.setFileName("foo.txt");
        actualUploadAndProcessFileResponseVO.setFileType("File Type");
        actualUploadAndProcessFileResponseVO.setMessage("Not all who wander are lost");
        actualUploadAndProcessFileResponseVO.setMessageCode("Message Code");
        actualUploadAndProcessFileResponseVO.setNoOfRecords("No Of Records");
        ArrayList subServiceTypeIdList = new ArrayList();
        actualUploadAndProcessFileResponseVO.setSubServiceTypeIdList(subServiceTypeIdList);
        actualUploadAndProcessFileResponseVO.setSubscriberStatus("Subscriber Status");
        ArrayList subscriberStatusList = new ArrayList();
        actualUploadAndProcessFileResponseVO.setSubscriberStatusList(subscriberStatusList);
        actualUploadAndProcessFileResponseVO.setTotalRecords(1);
        actualUploadAndProcessFileResponseVO.setValidRecords(1);
        String actualToStringResult = actualUploadAndProcessFileResponseVO.toString();
        assertEquals("An error occurred", actualUploadAndProcessFileResponseVO.getErrorFlag());
        assertSame(errorList, actualUploadAndProcessFileResponseVO.getErrorList());
        assertSame(errorMap, actualUploadAndProcessFileResponseVO.getErrorMap());
        assertEquals("File Attachment", actualUploadAndProcessFileResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualUploadAndProcessFileResponseVO.getFileName());
        assertEquals("File Type", actualUploadAndProcessFileResponseVO.getFileType());
        assertEquals("Not all who wander are lost", actualUploadAndProcessFileResponseVO.getMessage());
        assertEquals("Message Code", actualUploadAndProcessFileResponseVO.getMessageCode());
        assertEquals("No Of Records", actualUploadAndProcessFileResponseVO.getNoOfRecords());
        assertSame(subServiceTypeIdList, actualUploadAndProcessFileResponseVO.getSubServiceTypeIdList());
        assertEquals("Subscriber Status", actualUploadAndProcessFileResponseVO.getSubscriberStatus());
        assertSame(subscriberStatusList, actualUploadAndProcessFileResponseVO.getSubscriberStatusList());
        assertEquals(1, actualUploadAndProcessFileResponseVO.getTotalRecords());
        assertEquals(1, actualUploadAndProcessFileResponseVO.getValidRecords());
        assertEquals("UploadAndProcessFileResponseVO [subServiceTypeIdList=[], subscriberStatus=Subscriber Status,"
                + " subscriberStatusList=[], errorList=[]]", actualToStringResult);
    }
}

