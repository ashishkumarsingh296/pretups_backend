package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FetchBarredListRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FetchBarredListRequestVO}
     *   <li>{@link FetchBarredListRequestVO#setBarredAs(String)}
     *   <li>{@link FetchBarredListRequestVO#setBarredtype(String)}
     *   <li>{@link FetchBarredListRequestVO#setCategory(String)}
     *   <li>{@link FetchBarredListRequestVO#setDomain(String)}
     *   <li>{@link FetchBarredListRequestVO#setFromDate(String)}
     *   <li>{@link FetchBarredListRequestVO#setGeography(String)}
     *   <li>{@link FetchBarredListRequestVO#setModule(String)}
     *   <li>{@link FetchBarredListRequestVO#setMsisdn(String)}
     *   <li>{@link FetchBarredListRequestVO#setTodate(String)}
     *   <li>{@link FetchBarredListRequestVO#setUserName(String)}
     *   <li>{@link FetchBarredListRequestVO#setUserType(String)}
     *   <li>{@link FetchBarredListRequestVO#toString()}
     *   <li>{@link FetchBarredListRequestVO#getBarredAs()}
     *   <li>{@link FetchBarredListRequestVO#getBarredtype()}
     *   <li>{@link FetchBarredListRequestVO#getCategory()}
     *   <li>{@link FetchBarredListRequestVO#getDomain()}
     *   <li>{@link FetchBarredListRequestVO#getFromDate()}
     *   <li>{@link FetchBarredListRequestVO#getGeography()}
     *   <li>{@link FetchBarredListRequestVO#getModule()}
     *   <li>{@link FetchBarredListRequestVO#getMsisdn()}
     *   <li>{@link FetchBarredListRequestVO#getTodate()}
     *   <li>{@link FetchBarredListRequestVO#getUserName()}
     *   <li>{@link FetchBarredListRequestVO#getUserType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FetchBarredListRequestVO actualFetchBarredListRequestVO = new FetchBarredListRequestVO();
        actualFetchBarredListRequestVO.setBarredAs("Barred As");
        actualFetchBarredListRequestVO.setBarredtype("Barredtype");
        actualFetchBarredListRequestVO.setCategory("Category");
        actualFetchBarredListRequestVO.setDomain("Domain");
        actualFetchBarredListRequestVO.setFromDate("2020-03-01");
        actualFetchBarredListRequestVO.setGeography("Geography");
        actualFetchBarredListRequestVO.setModule("Module");
        actualFetchBarredListRequestVO.setMsisdn("Msisdn");
        actualFetchBarredListRequestVO.setTodate("2020-03-01");
        actualFetchBarredListRequestVO.setUserName("janedoe");
        actualFetchBarredListRequestVO.setUserType("User Type");
        String actualToStringResult = actualFetchBarredListRequestVO.toString();
        assertEquals("Barred As", actualFetchBarredListRequestVO.getBarredAs());
        assertEquals("Barredtype", actualFetchBarredListRequestVO.getBarredtype());
        assertEquals("Category", actualFetchBarredListRequestVO.getCategory());
        assertEquals("Domain", actualFetchBarredListRequestVO.getDomain());
        assertEquals("2020-03-01", actualFetchBarredListRequestVO.getFromDate());
        assertEquals("Geography", actualFetchBarredListRequestVO.getGeography());
        assertEquals("Module", actualFetchBarredListRequestVO.getModule());
        assertEquals("Msisdn", actualFetchBarredListRequestVO.getMsisdn());
        assertEquals("2020-03-01", actualFetchBarredListRequestVO.getTodate());
        assertEquals("janedoe", actualFetchBarredListRequestVO.getUserName());
        assertEquals("User Type", actualFetchBarredListRequestVO.getUserType());
        assertEquals("FetchBarredListRequestVO [msisdn=Msisdn, userName=janedoe, fromDate=2020-03-01, todate=2020-03-01,"
                + " domain=Domain, category=Category, geography=Geography, userType=User Type, module=Module, barredAs=Barred"
                + " As, barredtype=Barredtype]", actualToStringResult);
    }
}

