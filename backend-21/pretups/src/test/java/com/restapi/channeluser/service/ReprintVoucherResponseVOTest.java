package com.restapi.channeluser.service;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ReprintVoucherResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ReprintVoucherResponseVO}
     *   <li>{@link ReprintVoucherResponseVO#setVoucherList(List)}
     *   <li>{@link ReprintVoucherResponseVO#getVoucherList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ReprintVoucherResponseVO actualReprintVoucherResponseVO = new ReprintVoucherResponseVO();
        ArrayList<VoucherVO> voucherList = new ArrayList<>();
        actualReprintVoucherResponseVO.setVoucherList(voucherList);
        assertSame(voucherList, actualReprintVoucherResponseVO.getVoucherList());
    }
}

