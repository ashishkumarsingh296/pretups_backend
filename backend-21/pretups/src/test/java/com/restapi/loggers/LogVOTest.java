package com.restapi.loggers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LogVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LogVO}
     *   <li>{@link LogVO#setCategoryCode(String)}
     *   <li>{@link LogVO#setCategoryName(String)}
     *   <li>{@link LogVO#setComponentName(String)}
     *   <li>{@link LogVO#setDomainCode(String)}
     *   <li>{@link LogVO#setDomainName(String)}
     *   <li>{@link LogVO#setDomainType(String)}
     *   <li>{@link LogVO#setElementCode(String)}
     *   <li>{@link LogVO#setLoginID(String)}
     *   <li>{@link LogVO#setMsisdn(String)}
     *   <li>{@link LogVO#setNetworkCode(String)}
     *   <li>{@link LogVO#setNetworkName(String)}
     *   <li>{@link LogVO#setTimeStamp(String)}
     *   <li>{@link LogVO#setUserID(String)}
     *   <li>{@link LogVO#setUserName(String)}
     *   <li>{@link LogVO#toString()}
     *   <li>{@link LogVO#getCategoryCode()}
     *   <li>{@link LogVO#getCategoryName()}
     *   <li>{@link LogVO#getComponentName()}
     *   <li>{@link LogVO#getDomainCode()}
     *   <li>{@link LogVO#getDomainName()}
     *   <li>{@link LogVO#getDomainType()}
     *   <li>{@link LogVO#getElementCode()}
     *   <li>{@link LogVO#getLoginID()}
     *   <li>{@link LogVO#getMsisdn()}
     *   <li>{@link LogVO#getNetworkCode()}
     *   <li>{@link LogVO#getNetworkName()}
     *   <li>{@link LogVO#getTimeStamp()}
     *   <li>{@link LogVO#getUserID()}
     *   <li>{@link LogVO#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LogVO actualLogVO = new LogVO();
        actualLogVO.setCategoryCode("Category Code");
        actualLogVO.setCategoryName("Category Name");
        actualLogVO.setComponentName("Component Name");
        actualLogVO.setDomainCode("Domain Code");
        actualLogVO.setDomainName("Domain Name");
        actualLogVO.setDomainType("Domain Type");
        actualLogVO.setElementCode("Element Code");
        actualLogVO.setLoginID("Login ID");
        actualLogVO.setMsisdn("Msisdn");
        actualLogVO.setNetworkCode("Network Code");
        actualLogVO.setNetworkName("Network Name");
        actualLogVO.setTimeStamp("Time Stamp");
        actualLogVO.setUserID("User ID");
        actualLogVO.setUserName("janedoe");
        String actualToStringResult = actualLogVO.toString();
        assertEquals("Category Code", actualLogVO.getCategoryCode());
        assertEquals("Category Name", actualLogVO.getCategoryName());
        assertEquals("Component Name", actualLogVO.getComponentName());
        assertEquals("Domain Code", actualLogVO.getDomainCode());
        assertEquals("Domain Name", actualLogVO.getDomainName());
        assertEquals("Domain Type", actualLogVO.getDomainType());
        assertEquals("Element Code", actualLogVO.getElementCode());
        assertEquals("Login ID", actualLogVO.getLoginID());
        assertEquals("Msisdn", actualLogVO.getMsisdn());
        assertEquals("Network Code", actualLogVO.getNetworkCode());
        assertEquals("Network Name", actualLogVO.getNetworkName());
        assertEquals("Time Stamp", actualLogVO.getTimeStamp());
        assertEquals("User ID", actualLogVO.getUserID());
        assertEquals("janedoe", actualLogVO.getUserName());
        assertEquals("LogVO [userID=User ID, loginID=Login ID, userName=janedoe, msisdn=Msisdn, timeStamp=Time Stamp,"
                + " networkCode=Network Code, networkName=Network Name, domainCode=Domain Code, domainName=Domain Name,"
                + " categoryCode=Category Code, categoryName=Category Name, elementCode=Element Code, componentName=Component"
                + " Name, domainType=Domain Type]", actualToStringResult);
    }
}

