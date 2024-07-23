package com.restapi.c2s.services;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GetReversalListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GetReversalListResponseVO}
     *   <li>{@link GetReversalListResponseVO#setResponseList(List)}
     *   <li>{@link GetReversalListResponseVO#toString()}
     *   <li>{@link GetReversalListResponseVO#getResponseList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GetReversalListResponseVO actualGetReversalListResponseVO = new GetReversalListResponseVO();
        ArrayList<ChannelTransferVO> responseList = new ArrayList<>();
        actualGetReversalListResponseVO.setResponseList(responseList);
        actualGetReversalListResponseVO.toString();
        assertSame(responseList, actualGetReversalListResponseVO.getResponseList());
    }
}

