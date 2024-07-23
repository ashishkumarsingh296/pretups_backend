package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class DownloadFileResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DownloadFileResponseVO}
     *   <li>{@link DownloadFileResponseVO#setCardGroupIdList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setCategoryList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setCellGroupList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setExelMasterData(Map)}
     *   <li>{@link DownloadFileResponseVO#setFileAttachment(String)}
     *   <li>{@link DownloadFileResponseVO#setFileName(String)}
     *   <li>{@link DownloadFileResponseVO#setFileType(String)}
     *   <li>{@link DownloadFileResponseVO#setGeoDomainCodeList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setGeoTypeDesc(String)}
     *   <li>{@link DownloadFileResponseVO#setGradeList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setServiceGroupList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setServiceTypeList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setSubServiceTypeIdList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setSubscriberServiceTypeList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setSubscriberStatus(String)}
     *   <li>{@link DownloadFileResponseVO#setSubscriberStatusList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#setSubscriberTypeList(ArrayList)}
     *   <li>{@link DownloadFileResponseVO#toString()}
     *   <li>{@link DownloadFileResponseVO#getCardGroupIdList()}
     *   <li>{@link DownloadFileResponseVO#getCategoryList()}
     *   <li>{@link DownloadFileResponseVO#getCellGroupList()}
     *   <li>{@link DownloadFileResponseVO#getExelMasterData()}
     *   <li>{@link DownloadFileResponseVO#getFileAttachment()}
     *   <li>{@link DownloadFileResponseVO#getFileName()}
     *   <li>{@link DownloadFileResponseVO#getFileType()}
     *   <li>{@link DownloadFileResponseVO#getGeoDomainCodeList()}
     *   <li>{@link DownloadFileResponseVO#getGeoTypeDesc()}
     *   <li>{@link DownloadFileResponseVO#getGradeList()}
     *   <li>{@link DownloadFileResponseVO#getServiceGroupList()}
     *   <li>{@link DownloadFileResponseVO#getServiceTypeList()}
     *   <li>{@link DownloadFileResponseVO#getSubServiceTypeIdList()}
     *   <li>{@link DownloadFileResponseVO#getSubscriberServiceTypeList()}
     *   <li>{@link DownloadFileResponseVO#getSubscriberStatus()}
     *   <li>{@link DownloadFileResponseVO#getSubscriberStatusList()}
     *   <li>{@link DownloadFileResponseVO#getSubscriberTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DownloadFileResponseVO actualDownloadFileResponseVO = new DownloadFileResponseVO();
        ArrayList cardGroupIdList = new ArrayList();
        actualDownloadFileResponseVO.setCardGroupIdList(cardGroupIdList);
        ArrayList categoryList = new ArrayList();
        actualDownloadFileResponseVO.setCategoryList(categoryList);
        ArrayList cellGroupList = new ArrayList();
        actualDownloadFileResponseVO.setCellGroupList(cellGroupList);
        HashMap<Object, Object> exelMasterData = new HashMap<>();
        actualDownloadFileResponseVO.setExelMasterData(exelMasterData);
        actualDownloadFileResponseVO.setFileAttachment("File Attachment");
        actualDownloadFileResponseVO.setFileName("foo.txt");
        actualDownloadFileResponseVO.setFileType("File Type");
        ArrayList geoDomainCodeList = new ArrayList();
        actualDownloadFileResponseVO.setGeoDomainCodeList(geoDomainCodeList);
        actualDownloadFileResponseVO.setGeoTypeDesc("Geo Type Desc");
        ArrayList gradeList = new ArrayList();
        actualDownloadFileResponseVO.setGradeList(gradeList);
        ArrayList serviceGroupList = new ArrayList();
        actualDownloadFileResponseVO.setServiceGroupList(serviceGroupList);
        ArrayList serviceTypeList = new ArrayList();
        actualDownloadFileResponseVO.setServiceTypeList(serviceTypeList);
        ArrayList subServiceTypeIdList = new ArrayList();
        actualDownloadFileResponseVO.setSubServiceTypeIdList(subServiceTypeIdList);
        ArrayList subscriberServiceTypeList = new ArrayList();
        actualDownloadFileResponseVO.setSubscriberServiceTypeList(subscriberServiceTypeList);
        actualDownloadFileResponseVO.setSubscriberStatus("Subscriber Status");
        ArrayList subscriberStatusList = new ArrayList();
        actualDownloadFileResponseVO.setSubscriberStatusList(subscriberStatusList);
        ArrayList subscriberTypeList = new ArrayList();
        actualDownloadFileResponseVO.setSubscriberTypeList(subscriberTypeList);
        String actualToStringResult = actualDownloadFileResponseVO.toString();
        assertSame(cardGroupIdList, actualDownloadFileResponseVO.getCardGroupIdList());
        assertSame(categoryList, actualDownloadFileResponseVO.getCategoryList());
        assertSame(cellGroupList, actualDownloadFileResponseVO.getCellGroupList());
        assertSame(exelMasterData, actualDownloadFileResponseVO.getExelMasterData());
        assertEquals("File Attachment", actualDownloadFileResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualDownloadFileResponseVO.getFileName());
        assertEquals("File Type", actualDownloadFileResponseVO.getFileType());
        assertSame(geoDomainCodeList, actualDownloadFileResponseVO.getGeoDomainCodeList());
        assertEquals("Geo Type Desc", actualDownloadFileResponseVO.getGeoTypeDesc());
        assertSame(gradeList, actualDownloadFileResponseVO.getGradeList());
        assertSame(serviceGroupList, actualDownloadFileResponseVO.getServiceGroupList());
        assertSame(serviceTypeList, actualDownloadFileResponseVO.getServiceTypeList());
        assertSame(subServiceTypeIdList, actualDownloadFileResponseVO.getSubServiceTypeIdList());
        assertSame(subscriberServiceTypeList, actualDownloadFileResponseVO.getSubscriberServiceTypeList());
        assertEquals("Subscriber Status", actualDownloadFileResponseVO.getSubscriberStatus());
        assertSame(subscriberStatusList, actualDownloadFileResponseVO.getSubscriberStatusList());
        assertSame(subscriberTypeList, actualDownloadFileResponseVO.getSubscriberTypeList());
        assertEquals("DownloadFileResponseVO [subscriberTypeList=[], subscriberServiceTypeList=[], cardGroupIdList=[],"
                + " serviceTypeList=[], subServiceTypeIdList=[], subscriberStatus=Subscriber Status, subscriberStatusList=[],"
                + " gradeList=[], categoryList=[], geoDomainCodeList=[], geoTypeDesc=Geo Type Desc, cellGroupList=[],"
                + " serviceGroupList=[], exelMasterData={}]", actualToStringResult);
    }
}

