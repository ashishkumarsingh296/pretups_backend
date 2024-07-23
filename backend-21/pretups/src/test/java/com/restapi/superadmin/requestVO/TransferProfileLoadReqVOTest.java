package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TransferProfileLoadReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TransferProfileLoadReqVO}
     *   <li>{@link TransferProfileLoadReqVO#setCategoryCode(String)}
     *   <li>{@link TransferProfileLoadReqVO#setDomainCode(String)}
     *   <li>{@link TransferProfileLoadReqVO#setNetworkCode(String)}
     *   <li>{@link TransferProfileLoadReqVO#setStatus(String)}
     *   <li>{@link TransferProfileLoadReqVO#getCategoryCode()}
     *   <li>{@link TransferProfileLoadReqVO#getDomainCode()}
     *   <li>{@link TransferProfileLoadReqVO#getNetworkCode()}
     *   <li>{@link TransferProfileLoadReqVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TransferProfileLoadReqVO actualTransferProfileLoadReqVO = new TransferProfileLoadReqVO();
        actualTransferProfileLoadReqVO.setCategoryCode("Category Code");
        actualTransferProfileLoadReqVO.setDomainCode("Domain Code");
        actualTransferProfileLoadReqVO.setNetworkCode("Network Code");
        actualTransferProfileLoadReqVO.setStatus("Status");
        assertEquals("Category Code", actualTransferProfileLoadReqVO.getCategoryCode());
        assertEquals("Domain Code", actualTransferProfileLoadReqVO.getDomainCode());
        assertEquals("Network Code", actualTransferProfileLoadReqVO.getNetworkCode());
        assertEquals("Status", actualTransferProfileLoadReqVO.getStatus());
    }
}

