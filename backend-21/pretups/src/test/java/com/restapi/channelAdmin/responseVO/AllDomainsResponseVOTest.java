package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class AllDomainsResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AllDomainsResponseVO}
     *   <li>{@link AllDomainsResponseVO#setDomains(List)}
     *   <li>{@link AllDomainsResponseVO#toString()}
     *   <li>{@link AllDomainsResponseVO#getDomains()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AllDomainsResponseVO actualAllDomainsResponseVO = new AllDomainsResponseVO();
        ArrayList<ListValueVO> domains = new ArrayList<>();
        actualAllDomainsResponseVO.setDomains(domains);
        String actualToStringResult = actualAllDomainsResponseVO.toString();
        assertSame(domains, actualAllDomainsResponseVO.getDomains());
        assertEquals("AllDomainsResponseVO [domains=[]]", actualToStringResult);
    }
}

