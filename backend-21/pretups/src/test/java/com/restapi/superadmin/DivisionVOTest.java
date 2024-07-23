package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

public class DivisionVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DivisionVO}
     *   <li>{@link DivisionVO#setCreatedBy(String)}
     *   <li>{@link DivisionVO#setCreatedOn(Date)}
     *   <li>{@link DivisionVO#setDivDept(String)}
     *   <li>{@link DivisionVO#setDivDeptId(String)}
     *   <li>{@link DivisionVO#setDivDeptName(String)}
     *   <li>{@link DivisionVO#setDivDeptShortCode(String)}
     *   <li>{@link DivisionVO#setDivDeptType(String)}
     *   <li>{@link DivisionVO#setDivDeptTypeName(String)}
     *   <li>{@link DivisionVO#setLastModified(long)}
     *   <li>{@link DivisionVO#setModifiedBy(String)}
     *   <li>{@link DivisionVO#setModifiedOn(Date)}
     *   <li>{@link DivisionVO#setNetworkCode(String)}
     *   <li>{@link DivisionVO#setNetworkName(String)}
     *   <li>{@link DivisionVO#setParentId(String)}
     *   <li>{@link DivisionVO#setRadioIndex(int)}
     *   <li>{@link DivisionVO#setStatus(String)}
     *   <li>{@link DivisionVO#setStatusName(String)}
     *   <li>{@link DivisionVO#setUserId(String)}
     *   <li>{@link DivisionVO#toString()}
     *   <li>{@link DivisionVO#getCreatedBy()}
     *   <li>{@link DivisionVO#getCreatedOn()}
     *   <li>{@link DivisionVO#getDivDept()}
     *   <li>{@link DivisionVO#getDivDeptId()}
     *   <li>{@link DivisionVO#getDivDeptName()}
     *   <li>{@link DivisionVO#getDivDeptShortCode()}
     *   <li>{@link DivisionVO#getDivDeptType()}
     *   <li>{@link DivisionVO#getDivDeptTypeName()}
     *   <li>{@link DivisionVO#getLastModified()}
     *   <li>{@link DivisionVO#getModifiedBy()}
     *   <li>{@link DivisionVO#getModifiedOn()}
     *   <li>{@link DivisionVO#getNetworkCode()}
     *   <li>{@link DivisionVO#getNetworkName()}
     *   <li>{@link DivisionVO#getParentId()}
     *   <li>{@link DivisionVO#getRadioIndex()}
     *   <li>{@link DivisionVO#getStatus()}
     *   <li>{@link DivisionVO#getStatusName()}
     *   <li>{@link DivisionVO#getUserId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DivisionVO actualDivisionVO = new DivisionVO();
        actualDivisionVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        Date createdOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualDivisionVO.setCreatedOn(createdOn);
        actualDivisionVO.setDivDept("Div Dept");
        actualDivisionVO.setDivDeptId("42");
        actualDivisionVO.setDivDeptName("Div Dept Name");
        actualDivisionVO.setDivDeptShortCode("Div Dept Short Code");
        actualDivisionVO.setDivDeptType("Div Dept Type");
        actualDivisionVO.setDivDeptTypeName("Div Dept Type Name");
        actualDivisionVO.setLastModified(1L);
        actualDivisionVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Date modifiedOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualDivisionVO.setModifiedOn(modifiedOn);
        actualDivisionVO.setNetworkCode("Network Code");
        actualDivisionVO.setNetworkName("Network Name");
        actualDivisionVO.setParentId("42");
        actualDivisionVO.setRadioIndex(1);
        actualDivisionVO.setStatus("Status");
        actualDivisionVO.setStatusName("Status Name");
        actualDivisionVO.setUserId("42");
        actualDivisionVO.toString();
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualDivisionVO.getCreatedBy());
        assertSame(createdOn, actualDivisionVO.getCreatedOn());
        assertEquals("Div Dept", actualDivisionVO.getDivDept());
        assertEquals("42", actualDivisionVO.getDivDeptId());
        assertEquals("Div Dept Name", actualDivisionVO.getDivDeptName());
        assertEquals("Div Dept Short Code", actualDivisionVO.getDivDeptShortCode());
        assertEquals("Div Dept Type", actualDivisionVO.getDivDeptType());
        assertEquals("Div Dept Type Name", actualDivisionVO.getDivDeptTypeName());
        assertEquals(1L, actualDivisionVO.getLastModified());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualDivisionVO.getModifiedBy());
        assertSame(modifiedOn, actualDivisionVO.getModifiedOn());
        assertEquals("Network Code", actualDivisionVO.getNetworkCode());
        assertEquals("Network Name", actualDivisionVO.getNetworkName());
        assertEquals("42", actualDivisionVO.getParentId());
        assertEquals(1, actualDivisionVO.getRadioIndex());
        assertEquals("Status", actualDivisionVO.getStatus());
        assertEquals("Status Name", actualDivisionVO.getStatusName());
        assertEquals("42", actualDivisionVO.getUserId());
    }
}

