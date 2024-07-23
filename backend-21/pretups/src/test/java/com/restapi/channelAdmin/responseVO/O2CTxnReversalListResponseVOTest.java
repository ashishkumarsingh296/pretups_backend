package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CTxnReversalListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CTxnReversalListResponseVO}
     *   <li>{@link O2CTxnReversalListResponseVO#setO2CTxnReversalList(List)}
     *   <li>{@link O2CTxnReversalListResponseVO#setO2CTxnReversalListSize(int)}
     *   <li>{@link O2CTxnReversalListResponseVO#getO2CTxnReversalList()}
     *   <li>{@link O2CTxnReversalListResponseVO#getO2CTxnReversalListSize()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CTxnReversalListResponseVO actualO2cTxnReversalListResponseVO = new O2CTxnReversalListResponseVO();
        ArrayList<ChannelTransferVO> o2cTxnReversalList = new ArrayList<>();
        actualO2cTxnReversalListResponseVO.setO2CTxnReversalList(o2cTxnReversalList);
        actualO2cTxnReversalListResponseVO.setO2CTxnReversalListSize(3);
        assertSame(o2cTxnReversalList, actualO2cTxnReversalListResponseVO.getO2CTxnReversalList());
        assertEquals(3, actualO2cTxnReversalListResponseVO.getO2CTxnReversalListSize());
    }
}

