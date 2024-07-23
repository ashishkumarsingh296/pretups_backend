package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeleteTransferProfileDataReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DeleteTransferProfileDataReqVO}
     *   <li>{@link DeleteTransferProfileDataReqVO#setTransferProfileID(String)}
     *   <li>{@link DeleteTransferProfileDataReqVO#getTransferProfileID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DeleteTransferProfileDataReqVO actualDeleteTransferProfileDataReqVO = new DeleteTransferProfileDataReqVO();
        actualDeleteTransferProfileDataReqVO.setTransferProfileID("Transfer Profile ID");
        assertEquals("Transfer Profile ID", actualDeleteTransferProfileDataReqVO.getTransferProfileID());
    }
}

