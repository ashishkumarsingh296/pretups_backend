package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ModifyDepartmentRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ModifyDepartmentRequestVO}
     *   <li>{@link ModifyDepartmentRequestVO#setDivDeptId(String)}
     *   <li>{@link ModifyDepartmentRequestVO#setDivDeptName(String)}
     *   <li>{@link ModifyDepartmentRequestVO#setDivDeptShortCode(String)}
     *   <li>{@link ModifyDepartmentRequestVO#setDivDeptType(String)}
     *   <li>{@link ModifyDepartmentRequestVO#setParentId(String)}
     *   <li>{@link ModifyDepartmentRequestVO#setStatus(String)}
     *   <li>{@link ModifyDepartmentRequestVO#getDivDeptId()}
     *   <li>{@link ModifyDepartmentRequestVO#getDivDeptName()}
     *   <li>{@link ModifyDepartmentRequestVO#getDivDeptShortCode()}
     *   <li>{@link ModifyDepartmentRequestVO#getDivDeptType()}
     *   <li>{@link ModifyDepartmentRequestVO#getParentId()}
     *   <li>{@link ModifyDepartmentRequestVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ModifyDepartmentRequestVO actualModifyDepartmentRequestVO = new ModifyDepartmentRequestVO();
        actualModifyDepartmentRequestVO.setDivDeptId("42");
        actualModifyDepartmentRequestVO.setDivDeptName("Div Dept Name");
        actualModifyDepartmentRequestVO.setDivDeptShortCode("Div Dept Short Code");
        actualModifyDepartmentRequestVO.setDivDeptType("Div Dept Type");
        actualModifyDepartmentRequestVO.setParentId("42");
        actualModifyDepartmentRequestVO.setStatus("Status");
        assertEquals("42", actualModifyDepartmentRequestVO.getDivDeptId());
        assertEquals("Div Dept Name", actualModifyDepartmentRequestVO.getDivDeptName());
        assertEquals("Div Dept Short Code", actualModifyDepartmentRequestVO.getDivDeptShortCode());
        assertEquals("Div Dept Type", actualModifyDepartmentRequestVO.getDivDeptType());
        assertEquals("42", actualModifyDepartmentRequestVO.getParentId());
        assertEquals("Status", actualModifyDepartmentRequestVO.getStatus());
    }
}

