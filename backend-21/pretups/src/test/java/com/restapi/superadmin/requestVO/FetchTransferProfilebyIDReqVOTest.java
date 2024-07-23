package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FetchTransferProfilebyIDReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FetchTransferProfilebyIDReqVO}
     *   <li>{@link FetchTransferProfilebyIDReqVO#setCategoryCode(String)}
     *   <li>{@link FetchTransferProfilebyIDReqVO#setNetworkCode(String)}
     *   <li>{@link FetchTransferProfilebyIDReqVO#setProfileID(String)}
     *   <li>{@link FetchTransferProfilebyIDReqVO#getCategoryCode()}
     *   <li>{@link FetchTransferProfilebyIDReqVO#getNetworkCode()}
     *   <li>{@link FetchTransferProfilebyIDReqVO#getProfileID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FetchTransferProfilebyIDReqVO actualFetchTransferProfilebyIDReqVO = new FetchTransferProfilebyIDReqVO();
        actualFetchTransferProfilebyIDReqVO.setCategoryCode("Category Code");
        actualFetchTransferProfilebyIDReqVO.setNetworkCode("Network Code");
        actualFetchTransferProfilebyIDReqVO.setProfileID("Profile ID");
        assertEquals("Category Code", actualFetchTransferProfilebyIDReqVO.getCategoryCode());
        assertEquals("Network Code", actualFetchTransferProfilebyIDReqVO.getNetworkCode());
        assertEquals("Profile ID", actualFetchTransferProfilebyIDReqVO.getProfileID());
    }
}

