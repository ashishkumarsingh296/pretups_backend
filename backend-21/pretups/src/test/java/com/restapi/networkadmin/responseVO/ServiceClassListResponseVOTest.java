package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ServiceClassListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ServiceClassListResponseVO}
     *   <li>{@link ServiceClassListResponseVO#setNetworkDescription(String)}
     *   <li>{@link ServiceClassListResponseVO#setServiceClassList(ArrayList)}
     *   <li>{@link ServiceClassListResponseVO#getNetworkDescription()}
     *   <li>{@link ServiceClassListResponseVO#getServiceClassList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ServiceClassListResponseVO actualServiceClassListResponseVO = new ServiceClassListResponseVO();
        actualServiceClassListResponseVO.setNetworkDescription("Network Description");
        ArrayList serviceClassList = new ArrayList();
        actualServiceClassListResponseVO.setServiceClassList(serviceClassList);
        assertEquals("Network Description", actualServiceClassListResponseVO.getNetworkDescription());
        assertSame(serviceClassList, actualServiceClassListResponseVO.getServiceClassList());
    }
}

