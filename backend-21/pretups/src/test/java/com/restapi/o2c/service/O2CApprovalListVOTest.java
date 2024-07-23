package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class O2CApprovalListVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CApprovalListVO}
     *   <li>{@link O2CApprovalListVO#setBulkApprovalList(HashMap)}
     *   <li>{@link O2CApprovalListVO#setO2cApprovalList(ArrayList)}
     *   <li>{@link O2CApprovalListVO#toString()}
     *   <li>{@link O2CApprovalListVO#getBulkApprovalList()}
     *   <li>{@link O2CApprovalListVO#getO2cApprovalList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CApprovalListVO actualO2cApprovalListVO = new O2CApprovalListVO();
        HashMap<String, ArrayList<O2CBatchMasterVO>> bulkApprovalList = new HashMap<>();
        actualO2cApprovalListVO.setBulkApprovalList(bulkApprovalList);
        ArrayList<HashMap<String, ArrayList<ChannelTransferVO>>> o2cApprovalList = new ArrayList<>();
        actualO2cApprovalListVO.setO2cApprovalList(o2cApprovalList);
        String actualToStringResult = actualO2cApprovalListVO.toString();
        assertSame(bulkApprovalList, actualO2cApprovalListVO.getBulkApprovalList());
        assertSame(o2cApprovalList, actualO2cApprovalListVO.getO2cApprovalList());
        assertEquals("O2CApprovalListVO [o2cApprovalList=[], bulkApprovalList={}]", actualToStringResult);
    }
}

