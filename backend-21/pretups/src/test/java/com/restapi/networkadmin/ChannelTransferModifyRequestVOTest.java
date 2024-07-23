package com.restapi.networkadmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ChannelTransferModifyRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelTransferModifyRequestVO}
     *   <li>{@link ChannelTransferModifyRequestVO#setTransferList(ArrayList)}
     *   <li>{@link ChannelTransferModifyRequestVO#getTransferList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelTransferModifyRequestVO actualChannelTransferModifyRequestVO = new ChannelTransferModifyRequestVO();
        ArrayList<C2STransferRuleRequest1> transferList = new ArrayList<>();
        actualChannelTransferModifyRequestVO.setTransferList(transferList);
        assertSame(transferList, actualChannelTransferModifyRequestVO.getTransferList());
    }
}

