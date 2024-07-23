package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.btsl.common.ListValueVO;
import com.web.pretups.channel.transfer.web.C2SReversalModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class C2SBulkReversalResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SBulkReversalResponseVO}
     *   <li>{@link C2SBulkReversalResponseVO#setC2sreversalList(List)}
     *   <li>{@link C2SBulkReversalResponseVO#setErrorFlag(Boolean)}
     *   <li>{@link C2SBulkReversalResponseVO#setFileErrorList(List)}
     *   <li>{@link C2SBulkReversalResponseVO#setProcStatus(String)}
     *   <li>{@link C2SBulkReversalResponseVO#setRejectedRecords(Integer)}
     *   <li>{@link C2SBulkReversalResponseVO#setSuccessRecords(Integer)}
     *   <li>{@link C2SBulkReversalResponseVO#setTotalRecords(Integer)}
     *   <li>{@link C2SBulkReversalResponseVO#toString()}
     *   <li>{@link C2SBulkReversalResponseVO#getC2sreversalList()}
     *   <li>{@link C2SBulkReversalResponseVO#getErrorFlag()}
     *   <li>{@link C2SBulkReversalResponseVO#getFileErrorList()}
     *   <li>{@link C2SBulkReversalResponseVO#getProcStatus()}
     *   <li>{@link C2SBulkReversalResponseVO#getRejectedRecords()}
     *   <li>{@link C2SBulkReversalResponseVO#getSuccessRecords()}
     *   <li>{@link C2SBulkReversalResponseVO#getTotalRecords()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SBulkReversalResponseVO actualC2sBulkReversalResponseVO = new C2SBulkReversalResponseVO();
        ArrayList<C2SReversalModel> c2sreversalList = new ArrayList<>();
        actualC2sBulkReversalResponseVO.setC2sreversalList(c2sreversalList);
        actualC2sBulkReversalResponseVO.setErrorFlag(true);
        ArrayList<ListValueVO> fileErrorList = new ArrayList<>();
        actualC2sBulkReversalResponseVO.setFileErrorList(fileErrorList);
        actualC2sBulkReversalResponseVO.setProcStatus("Proc Status");
        actualC2sBulkReversalResponseVO.setRejectedRecords(1);
        actualC2sBulkReversalResponseVO.setSuccessRecords(1);
        actualC2sBulkReversalResponseVO.setTotalRecords(1);
        String actualToStringResult = actualC2sBulkReversalResponseVO.toString();
        assertSame(c2sreversalList, actualC2sBulkReversalResponseVO.getC2sreversalList());
        assertTrue(actualC2sBulkReversalResponseVO.getErrorFlag());
        assertSame(fileErrorList, actualC2sBulkReversalResponseVO.getFileErrorList());
        assertEquals("Proc Status", actualC2sBulkReversalResponseVO.getProcStatus());
        assertEquals(1, actualC2sBulkReversalResponseVO.getRejectedRecords().intValue());
        assertEquals(1, actualC2sBulkReversalResponseVO.getSuccessRecords().intValue());
        assertEquals(1, actualC2sBulkReversalResponseVO.getTotalRecords().intValue());
        assertEquals("C2SBulkReversalResponseVO [procStatus=Proc Status, fileErrorList=[], errorFlag=true, totalRecords=1,"
                + " successRecords=1]", actualToStringResult);
    }
}

