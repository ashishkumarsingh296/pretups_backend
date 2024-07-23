package com.restapi.channelAdmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class VoucherPinResetDetailResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherPinResetDetailResponseVO}
     *   <li>{@link VoucherPinResetDetailResponseVO#setTransferItemsVOList(ArrayList)}
     *   <li>{@link VoucherPinResetDetailResponseVO#setTransferVOList(ArrayList)}
     *   <li>{@link VoucherPinResetDetailResponseVO#getTransferItemsVOList()}
     *   <li>{@link VoucherPinResetDetailResponseVO#getTransferVOList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherPinResetDetailResponseVO actualVoucherPinResetDetailResponseVO = new VoucherPinResetDetailResponseVO();
        ArrayList transferItemsVOList = new ArrayList();
        actualVoucherPinResetDetailResponseVO.setTransferItemsVOList(transferItemsVOList);
        ArrayList transferVOList = new ArrayList();
        actualVoucherPinResetDetailResponseVO.setTransferVOList(transferVOList);
        assertSame(transferItemsVOList, actualVoucherPinResetDetailResponseVO.getTransferItemsVOList());
        assertSame(transferVOList, actualVoucherPinResetDetailResponseVO.getTransferVOList());
    }
}

