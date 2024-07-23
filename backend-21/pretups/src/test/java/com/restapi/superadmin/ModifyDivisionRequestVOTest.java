package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ModifyDivisionRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ModifyDivisionRequestVO}
     *   <li>{@link ModifyDivisionRequestVO#setDivDeptId(String)}
     *   <li>{@link ModifyDivisionRequestVO#setDivDeptName(String)}
     *   <li>{@link ModifyDivisionRequestVO#setDivDeptShortCode(String)}
     *   <li>{@link ModifyDivisionRequestVO#setDivDeptType(String)}
     *   <li>{@link ModifyDivisionRequestVO#setParentId(String)}
     *   <li>{@link ModifyDivisionRequestVO#setStatus(String)}
     *   <li>{@link ModifyDivisionRequestVO#setStatusName(String)}
     *   <li>{@link ModifyDivisionRequestVO#toString()}
     *   <li>{@link ModifyDivisionRequestVO#getDivDeptId()}
     *   <li>{@link ModifyDivisionRequestVO#getDivDeptName()}
     *   <li>{@link ModifyDivisionRequestVO#getDivDeptShortCode()}
     *   <li>{@link ModifyDivisionRequestVO#getDivDeptType()}
     *   <li>{@link ModifyDivisionRequestVO#getParentId()}
     *   <li>{@link ModifyDivisionRequestVO#getStatus()}
     *   <li>{@link ModifyDivisionRequestVO#getStatusName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ModifyDivisionRequestVO actualModifyDivisionRequestVO = new ModifyDivisionRequestVO();
        actualModifyDivisionRequestVO.setDivDeptId("42");
        actualModifyDivisionRequestVO.setDivDeptName("Div Dept Name");
        actualModifyDivisionRequestVO.setDivDeptShortCode("Div Dept Short Code");
        actualModifyDivisionRequestVO.setDivDeptType("Div Dept Type");
        actualModifyDivisionRequestVO.setParentId("42");
        actualModifyDivisionRequestVO.setStatus("Status");
        actualModifyDivisionRequestVO.setStatusName("Status Name");
        String actualToStringResult = actualModifyDivisionRequestVO.toString();
        assertEquals("42", actualModifyDivisionRequestVO.getDivDeptId());
        assertEquals("Div Dept Name", actualModifyDivisionRequestVO.getDivDeptName());
        assertEquals("Div Dept Short Code", actualModifyDivisionRequestVO.getDivDeptShortCode());
        assertEquals("Div Dept Type", actualModifyDivisionRequestVO.getDivDeptType());
        assertEquals("42", actualModifyDivisionRequestVO.getParentId());
        assertEquals("Status", actualModifyDivisionRequestVO.getStatus());
        assertEquals("Status Name", actualModifyDivisionRequestVO.getStatusName());
        assertEquals(
                "ModifyDivisionRequestVO [divDeptType=Div Dept Type, divDeptId=42, divDeptShortCode=Div Dept Short Code,"
                        + " statusName=Status Name, divDeptName=Div Dept Name, status=Status, parentId=42]",
                actualToStringResult);
    }
}

