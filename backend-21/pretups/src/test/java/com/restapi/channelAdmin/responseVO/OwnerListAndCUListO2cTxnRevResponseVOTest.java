package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class OwnerListAndCUListO2cTxnRevResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link OwnerListAndCUListO2cTxnRevResponseVO}
     *   <li>{@link OwnerListAndCUListO2cTxnRevResponseVO#setList(List)}
     *   <li>{@link OwnerListAndCUListO2cTxnRevResponseVO#setSize(int)}
     *   <li>{@link OwnerListAndCUListO2cTxnRevResponseVO#getList()}
     *   <li>{@link OwnerListAndCUListO2cTxnRevResponseVO#getSize()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        OwnerListAndCUListO2cTxnRevResponseVO actualOwnerListAndCUListO2cTxnRevResponseVO = new OwnerListAndCUListO2cTxnRevResponseVO();
        ArrayList<ListValueVO> list = new ArrayList<>();
        actualOwnerListAndCUListO2cTxnRevResponseVO.setList(list);
        actualOwnerListAndCUListO2cTxnRevResponseVO.setSize(3);
        assertSame(list, actualOwnerListAndCUListO2cTxnRevResponseVO.getList());
        assertEquals(3, actualOwnerListAndCUListO2cTxnRevResponseVO.getSize());
    }
}

