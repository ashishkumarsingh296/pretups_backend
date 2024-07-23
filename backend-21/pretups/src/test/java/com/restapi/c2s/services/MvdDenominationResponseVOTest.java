package com.restapi.c2s.services;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class MvdDenominationResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link MvdDenominationResponseVO}
     *   <li>{@link MvdDenominationResponseVO#setVoucherDenomList(ArrayList)}
     *   <li>{@link MvdDenominationResponseVO#getVoucherDenomList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        MvdDenominationResponseVO actualMvdDenominationResponseVO = new MvdDenominationResponseVO();
        ArrayList<MvdResponseData> voucherDenomList = new ArrayList<>();
        actualMvdDenominationResponseVO.setVoucherDenomList(voucherDenomList);
        assertSame(voucherDenomList, actualMvdDenominationResponseVO.getVoucherDenomList());
    }
}

