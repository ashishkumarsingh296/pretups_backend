package com.restapi.networkadmin.cardgroup.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class C2SCardGroupSetNameListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SCardGroupSetNameListResponseVO}
     *   <li>{@link C2SCardGroupSetNameListResponseVO#setCardGroupSetNameList(ArrayList)}
     *   <li>{@link C2SCardGroupSetNameListResponseVO#setCurrentDefaultCardGroup(String)}
     *   <li>{@link C2SCardGroupSetNameListResponseVO#getCardGroupSetNameList()}
     *   <li>{@link C2SCardGroupSetNameListResponseVO#getCurrentDefaultCardGroup()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2SCardGroupSetNameListResponseVO actualC2sCardGroupSetNameListResponseVO = new C2SCardGroupSetNameListResponseVO();
        ArrayList cardGroupSetNameList = new ArrayList();
        actualC2sCardGroupSetNameListResponseVO.setCardGroupSetNameList(cardGroupSetNameList);
        actualC2sCardGroupSetNameListResponseVO.setCurrentDefaultCardGroup("Current Default Card Group");
        assertSame(cardGroupSetNameList, actualC2sCardGroupSetNameListResponseVO.getCardGroupSetNameList());
        assertEquals("Current Default Card Group", actualC2sCardGroupSetNameListResponseVO.getCurrentDefaultCardGroup());
    }
}

