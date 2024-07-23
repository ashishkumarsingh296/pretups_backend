package com.restapi.networkadmin.cardgroup.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVersionVO;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

public class VersionDetailsAndDeleteStatusVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VersionDetailsAndDeleteStatusVO}
     *   <li>{@link VersionDetailsAndDeleteStatusVO#setCardGroupSetVersionVO(CardGroupSetVersionVO)}
     *   <li>{@link VersionDetailsAndDeleteStatusVO#setDeleteStatus(boolean)}
     *   <li>{@link VersionDetailsAndDeleteStatusVO#setStatus(String)}
     *   <li>{@link VersionDetailsAndDeleteStatusVO#getCardGroupSetVersionVO()}
     *   <li>{@link VersionDetailsAndDeleteStatusVO#getStatus()}
     *   <li>{@link VersionDetailsAndDeleteStatusVO#isDeleteStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VersionDetailsAndDeleteStatusVO actualVersionDetailsAndDeleteStatusVO = new VersionDetailsAndDeleteStatusVO();
        CardGroupSetVersionVO cardGroupSetVersionVO = new CardGroupSetVersionVO();
        cardGroupSetVersionVO
                .setApplicableFrom(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVersionVO.setCardGroupSetID("Card Group Set ID");
        cardGroupSetVersionVO
                .setCreadtedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVersionVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        cardGroupSetVersionVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        cardGroupSetVersionVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        cardGroupSetVersionVO.setOldApplicableFrom(1L);
        cardGroupSetVersionVO.setVersion("1.0.2");
        actualVersionDetailsAndDeleteStatusVO.setCardGroupSetVersionVO(cardGroupSetVersionVO);
        actualVersionDetailsAndDeleteStatusVO.setDeleteStatus(true);
        actualVersionDetailsAndDeleteStatusVO.setStatus("Status");
        assertSame(cardGroupSetVersionVO, actualVersionDetailsAndDeleteStatusVO.getCardGroupSetVersionVO());
        assertEquals("Status", actualVersionDetailsAndDeleteStatusVO.getStatus());
        assertTrue(actualVersionDetailsAndDeleteStatusVO.isDeleteStatus());
    }
}

