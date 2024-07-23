package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CAC2STransferReversalResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CAC2STransferReversalResponseVO}
     *   <li>{@link CAC2STransferReversalResponseVO#setTransferList(List)}
     *   <li>{@link CAC2STransferReversalResponseVO#toString()}
     *   <li>{@link CAC2STransferReversalResponseVO#getTransferList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CAC2STransferReversalResponseVO actualCac2sTransferReversalResponseVO = new CAC2STransferReversalResponseVO();
        ArrayList<ChannelTransferVO> transferList = new ArrayList<>();
        actualCac2sTransferReversalResponseVO.setTransferList(transferList);
        String actualToStringResult = actualCac2sTransferReversalResponseVO.toString();
        assertSame(transferList, actualCac2sTransferReversalResponseVO.getTransferList());
        assertEquals("CAC2STransferReversalResponseVO [transferList=[]]", actualToStringResult);
    }
}

