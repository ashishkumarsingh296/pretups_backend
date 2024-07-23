package com.restapi.reportsDownload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.restapi.user.service.HeaderColumn;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BarredUserListRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BarredUserListRequestVO}
     *   <li>{@link BarredUserListRequestVO#setBarredAs(String)}
     *   <li>{@link BarredUserListRequestVO#setBarredtype(String)}
     *   <li>{@link BarredUserListRequestVO#setCategory(String)}
     *   <li>{@link BarredUserListRequestVO#setDomain(String)}
     *   <li>{@link BarredUserListRequestVO#setFromDate(String)}
     *   <li>{@link BarredUserListRequestVO#setGeography(String)}
     *   <li>{@link BarredUserListRequestVO#setHeaderColumns(List)}
     *   <li>{@link BarredUserListRequestVO#setModule(String)}
     *   <li>{@link BarredUserListRequestVO#setMsisdn(String)}
     *   <li>{@link BarredUserListRequestVO#setTodate(String)}
     *   <li>{@link BarredUserListRequestVO#setUserName(String)}
     *   <li>{@link BarredUserListRequestVO#setUserType(String)}
     *   <li>{@link BarredUserListRequestVO#toString()}
     *   <li>{@link BarredUserListRequestVO#getBarredAs()}
     *   <li>{@link BarredUserListRequestVO#getBarredtype()}
     *   <li>{@link BarredUserListRequestVO#getCategory()}
     *   <li>{@link BarredUserListRequestVO#getDomain()}
     *   <li>{@link BarredUserListRequestVO#getFromDate()}
     *   <li>{@link BarredUserListRequestVO#getGeography()}
     *   <li>{@link BarredUserListRequestVO#getHeaderColumns()}
     *   <li>{@link BarredUserListRequestVO#getModule()}
     *   <li>{@link BarredUserListRequestVO#getMsisdn()}
     *   <li>{@link BarredUserListRequestVO#getTodate()}
     *   <li>{@link BarredUserListRequestVO#getUserName()}
     *   <li>{@link BarredUserListRequestVO#getUserType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BarredUserListRequestVO actualBarredUserListRequestVO = new BarredUserListRequestVO();
        actualBarredUserListRequestVO.setBarredAs("Barred As");
        actualBarredUserListRequestVO.setBarredtype("Barredtype");
        actualBarredUserListRequestVO.setCategory("Category");
        actualBarredUserListRequestVO.setDomain("Domain");
        actualBarredUserListRequestVO.setFromDate("2020-03-01");
        actualBarredUserListRequestVO.setGeography("Geography");
        ArrayList<HeaderColumn> headerColumns = new ArrayList<>();
        actualBarredUserListRequestVO.setHeaderColumns(headerColumns);
        actualBarredUserListRequestVO.setModule("Module");
        actualBarredUserListRequestVO.setMsisdn("Msisdn");
        actualBarredUserListRequestVO.setTodate("2020-03-01");
        actualBarredUserListRequestVO.setUserName("janedoe");
        actualBarredUserListRequestVO.setUserType("User Type");
        String actualToStringResult = actualBarredUserListRequestVO.toString();
        assertEquals("Barred As", actualBarredUserListRequestVO.getBarredAs());
        assertEquals("Barredtype", actualBarredUserListRequestVO.getBarredtype());
        assertEquals("Category", actualBarredUserListRequestVO.getCategory());
        assertEquals("Domain", actualBarredUserListRequestVO.getDomain());
        assertEquals("2020-03-01", actualBarredUserListRequestVO.getFromDate());
        assertEquals("Geography", actualBarredUserListRequestVO.getGeography());
        assertSame(headerColumns, actualBarredUserListRequestVO.getHeaderColumns());
        assertEquals("Module", actualBarredUserListRequestVO.getModule());
        assertEquals("Msisdn", actualBarredUserListRequestVO.getMsisdn());
        assertEquals("2020-03-01", actualBarredUserListRequestVO.getTodate());
        assertEquals("janedoe", actualBarredUserListRequestVO.getUserName());
        assertEquals("User Type", actualBarredUserListRequestVO.getUserType());
        assertEquals("BarredUserListRequestVO [msisdn=Msisdn, userName=janedoe, fromDate=2020-03-01, todate=2020-03-01,"
                + " domain=Domain, category=Category, geography=Geography, userType=User Type, module=Module, barredAs=Barred"
                + " As, barredtype=Barredtype, headerColumns=[]]", actualToStringResult);
    }
}

