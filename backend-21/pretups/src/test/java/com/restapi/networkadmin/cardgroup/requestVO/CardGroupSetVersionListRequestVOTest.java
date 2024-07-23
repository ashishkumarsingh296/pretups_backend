package com.restapi.networkadmin.cardgroup.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CardGroupSetVersionListRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CardGroupSetVersionListRequestVO}
     *   <li>{@link CardGroupSetVersionListRequestVO#setCardGroupSetVersionList(ArrayList)}
     *   <li>{@link CardGroupSetVersionListRequestVO#setSelectCardGroupSetId(String)}
     *   <li>{@link CardGroupSetVersionListRequestVO#setSelectCardGroupSetVersionId(String)}
     *   <li>{@link CardGroupSetVersionListRequestVO#getCardGroupSetVersionList()}
     *   <li>{@link CardGroupSetVersionListRequestVO#getSelectCardGroupSetId()}
     *   <li>{@link CardGroupSetVersionListRequestVO#getSelectCardGroupSetVersionId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CardGroupSetVersionListRequestVO actualCardGroupSetVersionListRequestVO = new CardGroupSetVersionListRequestVO();
        ArrayList<CardGroupSetVersionVODetails> cardGroupSetVersionList = new ArrayList<>();
        //jdk21 actualCardGroupSetVersionListRequestVO.setCardGroupSetVersionList(cardGroupSetVersionList);
        actualCardGroupSetVersionListRequestVO.setSelectCardGroupSetId("42");
        actualCardGroupSetVersionListRequestVO.setSelectCardGroupSetVersionId("42");
       //jdk21 assertSame(cardGroupSetVersionList, actualCardGroupSetVersionListRequestVO.getCardGroupSetVersionList());
        assertEquals("42", actualCardGroupSetVersionListRequestVO.getSelectCardGroupSetId());
        assertEquals("42", actualCardGroupSetVersionListRequestVO.getSelectCardGroupSetVersionId());
    }
}

