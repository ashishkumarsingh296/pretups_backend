package com.restapi.channelenquiry.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;

import java.util.ArrayList;

import org.junit.Test;

public class C2SEnquiryResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SEnquiryResponseVO}
     *   <li>{@link C2SEnquiryResponseVO#setC2sEnquiryDetails(ArrayList)}
     *   <li>{@link C2SEnquiryResponseVO#toString()}
     *   <li>{@link C2SEnquiryResponseVO#getC2sEnquiryDetails()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SEnquiryResponseVO actualC2sEnquiryResponseVO = new C2SEnquiryResponseVO();
        ArrayList<C2STransferVO> c2sEnquiryDetails = new ArrayList<>();
        actualC2sEnquiryResponseVO.setC2sEnquiryDetails(c2sEnquiryDetails);
        String actualToStringResult = actualC2sEnquiryResponseVO.toString();
        assertSame(c2sEnquiryDetails, actualC2sEnquiryResponseVO.getC2sEnquiryDetails());
        assertEquals("C2SEnquiryResponseVO [c2sEnquiryDetails=[]]", actualToStringResult);
    }
}

