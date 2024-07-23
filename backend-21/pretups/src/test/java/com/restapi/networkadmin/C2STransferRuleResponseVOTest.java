package com.restapi.networkadmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class C2STransferRuleResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2STransferRuleResponseVO}
     *   <li>{@link C2STransferRuleResponseVO#setResultList(ArrayList)}
     *   <li>{@link C2STransferRuleResponseVO#getResultList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2STransferRuleResponseVO actualC2sTransferRuleResponseVO = new C2STransferRuleResponseVO();
        ArrayList resultList = new ArrayList();
        actualC2sTransferRuleResponseVO.setResultList(resultList);
        assertSame(resultList, actualC2sTransferRuleResponseVO.getResultList());
    }
}

