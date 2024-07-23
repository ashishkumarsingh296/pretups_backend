package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

import java.util.ArrayList;

import org.junit.Test;

public class C2cAndO2cEnquiryResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2cAndO2cEnquiryResponseVO}
     *   <li>{@link C2cAndO2cEnquiryResponseVO#setTransferList(ArrayList)}
     *   <li>{@link C2cAndO2cEnquiryResponseVO#setTransferListSize(int)}
     *   <li>{@link C2cAndO2cEnquiryResponseVO#toString()}
     *   <li>{@link C2cAndO2cEnquiryResponseVO#getTransferList()}
     *   <li>{@link C2cAndO2cEnquiryResponseVO#getTransferListSize()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2cAndO2cEnquiryResponseVO actualC2cAndO2cEnquiryResponseVO = new C2cAndO2cEnquiryResponseVO();
        ArrayList<ChannelTransferVO> transferList = new ArrayList<>();
        actualC2cAndO2cEnquiryResponseVO.setTransferList(transferList);
        actualC2cAndO2cEnquiryResponseVO.setTransferListSize(3);
        String actualToStringResult = actualC2cAndO2cEnquiryResponseVO.toString();
        assertSame(transferList, actualC2cAndO2cEnquiryResponseVO.getTransferList());
        assertEquals(3, actualC2cAndO2cEnquiryResponseVO.getTransferListSize());
        assertEquals("C2cAndO2cEnquiryResponseVO [transferList=[], transferListSize=3]", actualToStringResult);
    }
}

