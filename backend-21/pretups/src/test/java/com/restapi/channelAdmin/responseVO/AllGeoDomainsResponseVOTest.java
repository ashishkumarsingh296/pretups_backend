package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.user.businesslogic.UserGeographiesVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class AllGeoDomainsResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AllGeoDomainsResponseVO}
     *   <li>{@link AllGeoDomainsResponseVO#setGeoDomains(List)}
     *   <li>{@link AllGeoDomainsResponseVO#toString()}
     *   <li>{@link AllGeoDomainsResponseVO#getGeoDomains()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AllGeoDomainsResponseVO actualAllGeoDomainsResponseVO = new AllGeoDomainsResponseVO();
        ArrayList<UserGeographiesVO> geoDomains = new ArrayList<>();
        actualAllGeoDomainsResponseVO.setGeoDomains(geoDomains);
        String actualToStringResult = actualAllGeoDomainsResponseVO.toString();
        assertSame(geoDomains, actualAllGeoDomainsResponseVO.getGeoDomains());
        assertEquals("AllGeoDomainsResponseVO [geoDomains=[]]", actualToStringResult);
    }
}

