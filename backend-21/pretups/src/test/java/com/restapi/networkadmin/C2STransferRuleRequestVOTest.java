package com.restapi.networkadmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class C2STransferRuleRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2STransferRuleRequestVO}
     *   <li>{@link C2STransferRuleRequestVO#setTransferList(ArrayList)}
     *   <li>{@link C2STransferRuleRequestVO#getTransferList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2STransferRuleRequestVO actualC2sTransferRuleRequestVO = new C2STransferRuleRequestVO();
        ArrayList<C2STransferRuleRequest> transferList = new ArrayList<>();
        actualC2sTransferRuleRequestVO.setTransferList(transferList);
        assertSame(transferList, actualC2sTransferRuleRequestVO.getTransferList());
    }
}

