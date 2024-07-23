package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;

import java.util.ArrayList;

import org.junit.Test;

public class ViewC2SBulkRechargeDetailsResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ViewC2SBulkRechargeDetailsResponseVO}
     *   <li>{@link ViewC2SBulkRechargeDetailsResponseVO#setFile(String)}
     *   <li>{@link ViewC2SBulkRechargeDetailsResponseVO#setFileName(String)}
     *   <li>{@link ViewC2SBulkRechargeDetailsResponseVO#setMsisdnList(ArrayList)}
     *   <li>{@link ViewC2SBulkRechargeDetailsResponseVO#setScheduleBatchMasterVO(ScheduleBatchMasterVO)}
     *   <li>{@link ViewC2SBulkRechargeDetailsResponseVO#getFile()}
     *   <li>{@link ViewC2SBulkRechargeDetailsResponseVO#getFileName()}
     *   <li>{@link ViewC2SBulkRechargeDetailsResponseVO#getMsisdnList()}
     *   <li>{@link ViewC2SBulkRechargeDetailsResponseVO#getScheduleBatchMasterVO()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ViewC2SBulkRechargeDetailsResponseVO actualViewC2SBulkRechargeDetailsResponseVO = new ViewC2SBulkRechargeDetailsResponseVO();
        actualViewC2SBulkRechargeDetailsResponseVO.setFile("File");
        actualViewC2SBulkRechargeDetailsResponseVO.setFileName("foo.txt");
        ArrayList<ScheduleBatchDetailVO> msisdnList = new ArrayList<>();
        actualViewC2SBulkRechargeDetailsResponseVO.setMsisdnList(msisdnList);
        ScheduleBatchMasterVO scheduleBatchMasterVO = new ScheduleBatchMasterVO();
        actualViewC2SBulkRechargeDetailsResponseVO.setScheduleBatchMasterVO(scheduleBatchMasterVO);
        assertEquals("File", actualViewC2SBulkRechargeDetailsResponseVO.getFile());
        assertEquals("foo.txt", actualViewC2SBulkRechargeDetailsResponseVO.getFileName());
        assertSame(msisdnList, actualViewC2SBulkRechargeDetailsResponseVO.getMsisdnList());
        assertSame(scheduleBatchMasterVO, actualViewC2SBulkRechargeDetailsResponseVO.getScheduleBatchMasterVO());
    }
}

