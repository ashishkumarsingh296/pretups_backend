package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.user.businesslogic.TotalDailyUserIncomeResponseVO;

import java.util.LinkedList;

import org.junit.Test;

public class TotalUserIncomeDetailViewResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TotalUserIncomeDetailViewResponse}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setDetailedInfoList(LinkedList)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setFromDate(String)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setPreviousFromDate(String)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setPreviousToDate(String)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setPreviousTotalAdditionalBaseCom(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setPreviousTotalBaseComm(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setPreviousTotalCac(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setPreviousTotalCbc(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setPreviousTotalIncome(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setToDate(String)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalAdditionalBaseCom(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalAdditionalBaseComPercentage(String)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalBaseCom(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalBaseComPercentage(String)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalCac(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalCacPercentage(String)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalCbc(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalCbcPercentage(String)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalIncome(double)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#setTotalIncomePercentage(String)}
     *   <li>{@link TotalUserIncomeDetailViewResponse#toString()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getDetailedInfoList()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getFromDate()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getPreviousFromDate()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getPreviousToDate()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getPreviousTotalAdditionalBaseCom()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getPreviousTotalBaseComm()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getPreviousTotalCac()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getPreviousTotalCbc()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getPreviousTotalIncome()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getToDate()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalAdditionalBaseCom()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalAdditionalBaseComPercentage()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalBaseCom()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalBaseComPercentage()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalCac()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalCacPercentage()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalCbc()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalCbcPercentage()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalIncome()}
     *   <li>{@link TotalUserIncomeDetailViewResponse#getTotalIncomePercentage()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TotalUserIncomeDetailViewResponse actualTotalUserIncomeDetailViewResponse = new TotalUserIncomeDetailViewResponse();
        LinkedList<TotalDailyUserIncomeResponseVO> detailedInfoList = new LinkedList<>();
        actualTotalUserIncomeDetailViewResponse.setDetailedInfoList(detailedInfoList);
        actualTotalUserIncomeDetailViewResponse.setFromDate("2020-03-01");
        actualTotalUserIncomeDetailViewResponse.setPreviousFromDate("2020-03-01");
        actualTotalUserIncomeDetailViewResponse.setPreviousToDate("2020-03-01");
        actualTotalUserIncomeDetailViewResponse.setPreviousTotalAdditionalBaseCom(10.0d);
        actualTotalUserIncomeDetailViewResponse.setPreviousTotalBaseComm(10.0d);
        actualTotalUserIncomeDetailViewResponse.setPreviousTotalCac(10.0d);
        actualTotalUserIncomeDetailViewResponse.setPreviousTotalCbc(10.0d);
        actualTotalUserIncomeDetailViewResponse.setPreviousTotalIncome(10.0d);
        actualTotalUserIncomeDetailViewResponse.setToDate("2020-03-01");
        actualTotalUserIncomeDetailViewResponse.setTotalAdditionalBaseCom(10.0d);
        actualTotalUserIncomeDetailViewResponse.setTotalAdditionalBaseComPercentage("Total Additional Base Com Percentage");
        actualTotalUserIncomeDetailViewResponse.setTotalBaseCom(10.0d);
        actualTotalUserIncomeDetailViewResponse.setTotalBaseComPercentage("Total Base Com Percentage");
        actualTotalUserIncomeDetailViewResponse.setTotalCac(10.0d);
        actualTotalUserIncomeDetailViewResponse.setTotalCacPercentage("Total Cac Percentage");
        actualTotalUserIncomeDetailViewResponse.setTotalCbc(10.0d);
        actualTotalUserIncomeDetailViewResponse.setTotalCbcPercentage("Total Cbc Percentage");
        actualTotalUserIncomeDetailViewResponse.setTotalIncome(10.0d);
        actualTotalUserIncomeDetailViewResponse.setTotalIncomePercentage("Total Income Percentage");
        String actualToStringResult = actualTotalUserIncomeDetailViewResponse.toString();
        assertSame(detailedInfoList, actualTotalUserIncomeDetailViewResponse.getDetailedInfoList());
        assertEquals("2020-03-01", actualTotalUserIncomeDetailViewResponse.getFromDate());
        assertEquals("2020-03-01", actualTotalUserIncomeDetailViewResponse.getPreviousFromDate());
        assertEquals("2020-03-01", actualTotalUserIncomeDetailViewResponse.getPreviousToDate());
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getPreviousTotalAdditionalBaseCom(), 0.0);
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getPreviousTotalBaseComm(), 0.0);
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getPreviousTotalCac(), 0.0);
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getPreviousTotalCbc(), 0.0);
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getPreviousTotalIncome(), 0.0);
        assertEquals("2020-03-01", actualTotalUserIncomeDetailViewResponse.getToDate());
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getTotalAdditionalBaseCom(), 0.0);
        assertEquals("Total Additional Base Com Percentage",
                actualTotalUserIncomeDetailViewResponse.getTotalAdditionalBaseComPercentage());
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getTotalBaseCom(), 0.0);
        assertEquals("Total Base Com Percentage", actualTotalUserIncomeDetailViewResponse.getTotalBaseComPercentage());
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getTotalCac(), 0.0);
        assertEquals("Total Cac Percentage", actualTotalUserIncomeDetailViewResponse.getTotalCacPercentage());
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getTotalCbc(), 0.0);
        assertEquals("Total Cbc Percentage", actualTotalUserIncomeDetailViewResponse.getTotalCbcPercentage());
        assertEquals(10.0d, actualTotalUserIncomeDetailViewResponse.getTotalIncome(), 0.0);
        assertEquals("Total Income Percentage", actualTotalUserIncomeDetailViewResponse.getTotalIncomePercentage());
        assertEquals("TotalUserIncomeDetailViewResponse [detailedInfoList=[], totalIncome=10.0, previousTotalIncome=10.0,"
                + " totalBaseCom=10.0, previousTotalBaseComm=10.0, totalAdditionalBaseCom=10.0, previousTotalAdditionalBaseCom"
                + "=10.0, totalCac=10.0, previousTotalCac=10.0, totalCbc=10.0, previousTotalCbc=10.0, fromDate=2020-03-01,"
                + " toDate=2020-03-01, previousFromDate=2020-03-01, previousToDate=2020-03-01, totalIncomePercentage=Total"
                + " Income Percentage, totalBaseComPercentage=Total Base Com Percentage, totalAdditionalBaseComPercentage=Total"
                + " Additional Base Com Percentage, totalCacPercentage=Total Cac Percentage, totalCbcPercentage=Total Cbc"
                + " Percentage]", actualToStringResult);
    }
}

