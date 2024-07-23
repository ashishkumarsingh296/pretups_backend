package com.restapi.channelAdmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class VoucherPinResendResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherPinResendResponseVO}
     *   <li>{@link VoucherPinResendResponseVO#setTransList(ArrayList)}
     *   <li>{@link VoucherPinResendResponseVO#getTransList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherPinResendResponseVO actualVoucherPinResendResponseVO = new VoucherPinResendResponseVO();
        ArrayList transList = new ArrayList();
        actualVoucherPinResendResponseVO.setTransList(transList);
        assertSame(transList, actualVoucherPinResendResponseVO.getTransList());
    }
}

