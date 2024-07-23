package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ServiceListFilterTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ServiceListFilter}
     *   <li>{@link ServiceListFilter#setName(String)}
     *   <li>{@link ServiceListFilter#setServiceType(String)}
     *   <li>{@link ServiceListFilter#setStatus(String)}
     *   <li>{@link ServiceListFilter#toString()}
     *   <li>{@link ServiceListFilter#getName()}
     *   <li>{@link ServiceListFilter#getServiceType()}
     *   <li>{@link ServiceListFilter#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ServiceListFilter actualServiceListFilter = new ServiceListFilter();
        actualServiceListFilter.setName("Name");
        actualServiceListFilter.setServiceType("Service Type");
        actualServiceListFilter.setStatus("Status");
        String actualToStringResult = actualServiceListFilter.toString();
        assertEquals("Name", actualServiceListFilter.getName());
        assertEquals("Service Type", actualServiceListFilter.getServiceType());
        assertEquals("Status", actualServiceListFilter.getStatus());
        assertEquals("ServiceListFilter [name=Name, status=Status, serviceType=Service Type]", actualToStringResult);
    }
}

