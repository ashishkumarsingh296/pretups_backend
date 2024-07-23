package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TransferProfileSearchRespVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TransferProfileSearchRespVO}
     *   <li>{@link TransferProfileSearchRespVO#setNoCategoryLevel(boolean)}
     *   <li>{@link TransferProfileSearchRespVO#setTransferProfileList(List)}
     *   <li>{@link TransferProfileSearchRespVO#getTransferProfileList()}
     *   <li>{@link TransferProfileSearchRespVO#isNoCategoryLevel()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TransferProfileSearchRespVO actualTransferProfileSearchRespVO = new TransferProfileSearchRespVO();
        actualTransferProfileSearchRespVO.setNoCategoryLevel(true);
        ArrayList<TransferProfileVO> transferProfileList = new ArrayList<>();
        actualTransferProfileSearchRespVO.setTransferProfileList(transferProfileList);
        assertSame(transferProfileList, actualTransferProfileSearchRespVO.getTransferProfileList());
        assertTrue(actualTransferProfileSearchRespVO.isNoCategoryLevel());
    }
}

