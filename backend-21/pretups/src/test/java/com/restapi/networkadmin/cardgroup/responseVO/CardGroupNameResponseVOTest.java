package com.restapi.networkadmin.cardgroup.responseVO;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CardGroupNameResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CardGroupNameResponseVO}
     *   <li>{@link CardGroupNameResponseVO#setCardGroupNameList(List)}
     *   <li>{@link CardGroupNameResponseVO#getCardGroupNameList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CardGroupNameResponseVO actualCardGroupNameResponseVO = new CardGroupNameResponseVO();
        ArrayList<CardGroupSetVO> cardGroupNameList = new ArrayList<>();
        actualCardGroupNameResponseVO.setCardGroupNameList(cardGroupNameList);
        assertSame(cardGroupNameList, actualCardGroupNameResponseVO.getCardGroupNameList());
    }
}

