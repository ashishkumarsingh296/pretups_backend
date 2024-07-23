package com.restapi.staffuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ServiceListResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ServiceListResponse}
     *   <li>{@link ServiceListResponse#setServiceList(List)}
     *   <li>{@link ServiceListResponse#toString()}
     *   <li>{@link ServiceListResponse#getServiceList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ServiceListResponse actualServiceListResponse = new ServiceListResponse();
        ArrayList<ListValueVO> serviceList = new ArrayList<>();
        actualServiceListResponse.setServiceList(serviceList);
        String actualToStringResult = actualServiceListResponse.toString();
        assertSame(serviceList, actualServiceListResponse.getServiceList());
        assertEquals("ServiceListResponse [serviceList=[]]", actualToStringResult);
    }
}

