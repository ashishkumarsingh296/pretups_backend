package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.reports.businesslogic.UserClosingBalanceVO;

import java.util.ArrayList;

import org.junit.Test;

public class ClosingBalanceEnquiryResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ClosingBalanceEnquiryResponseVO}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#setBalanceList(ArrayList)}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#setDateColumnLabels(ArrayList)}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#setFileAttachment(String)}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#setFileName(String)}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#setFileType(String)}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#setModifiedData(ArrayList)}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#toString()}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#getBalanceList()}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#getDateColumnLabels()}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#getFileAttachment()}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#getFileName()}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#getFileType()}
     *   <li>{@link ClosingBalanceEnquiryResponseVO#getModifiedData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ClosingBalanceEnquiryResponseVO actualClosingBalanceEnquiryResponseVO = new ClosingBalanceEnquiryResponseVO();
        ArrayList<UserClosingBalanceVO> balanceList = new ArrayList<>();
        actualClosingBalanceEnquiryResponseVO.setBalanceList(balanceList);
        ArrayList<String> dateColumnLabels = new ArrayList<>();
        actualClosingBalanceEnquiryResponseVO.setDateColumnLabels(dateColumnLabels);
        actualClosingBalanceEnquiryResponseVO.setFileAttachment("File Attachment");
        actualClosingBalanceEnquiryResponseVO.setFileName("foo.txt");
        actualClosingBalanceEnquiryResponseVO.setFileType("File Type");
        ArrayList<ArrayList<String>> modifiedData = new ArrayList<>();
        actualClosingBalanceEnquiryResponseVO.setModifiedData(modifiedData);
        String actualToStringResult = actualClosingBalanceEnquiryResponseVO.toString();
        assertSame(balanceList, actualClosingBalanceEnquiryResponseVO.getBalanceList());
        assertSame(dateColumnLabels, actualClosingBalanceEnquiryResponseVO.getDateColumnLabels());
        assertEquals("File Attachment", actualClosingBalanceEnquiryResponseVO.getFileAttachment());
        assertEquals("foo.txt", actualClosingBalanceEnquiryResponseVO.getFileName());
        assertEquals("File Type", actualClosingBalanceEnquiryResponseVO.getFileType());
        assertSame(modifiedData, actualClosingBalanceEnquiryResponseVO.getModifiedData());
        assertEquals("ClosingBalanceEnquiryResponseVO [balanceList=[], modifiedData=[]]", actualToStringResult);
    }
}

