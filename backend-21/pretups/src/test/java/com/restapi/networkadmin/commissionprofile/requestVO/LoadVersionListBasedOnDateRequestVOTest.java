package com.restapi.networkadmin.commissionprofile.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LoadVersionListBasedOnDateRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LoadVersionListBasedOnDateRequestVO}
     *   <li>{@link LoadVersionListBasedOnDateRequestVO#setCategoryCode(String)}
     *   <li>{@link LoadVersionListBasedOnDateRequestVO#setCommProfileSetId(String)}
     *   <li>{@link LoadVersionListBasedOnDateRequestVO#setDate(String)}
     *   <li>{@link LoadVersionListBasedOnDateRequestVO#getCategoryCode()}
     *   <li>{@link LoadVersionListBasedOnDateRequestVO#getCommProfileSetId()}
     *   <li>{@link LoadVersionListBasedOnDateRequestVO#getDate()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LoadVersionListBasedOnDateRequestVO actualLoadVersionListBasedOnDateRequestVO = new LoadVersionListBasedOnDateRequestVO();
        actualLoadVersionListBasedOnDateRequestVO.setCategoryCode("Category Code");
        actualLoadVersionListBasedOnDateRequestVO.setCommProfileSetId("42");
        actualLoadVersionListBasedOnDateRequestVO.setDate("2020-03-01");
        assertEquals("Category Code", actualLoadVersionListBasedOnDateRequestVO.getCategoryCode());
        assertEquals("42", actualLoadVersionListBasedOnDateRequestVO.getCommProfileSetId());
        assertEquals("2020-03-01", actualLoadVersionListBasedOnDateRequestVO.getDate());
    }
}

