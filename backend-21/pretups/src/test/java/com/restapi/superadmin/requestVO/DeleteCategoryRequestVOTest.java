package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;

import com.btsl.pretups.domain.businesslogic.CategoryVO;
import org.junit.Test;

public class DeleteCategoryRequestVOTest {
    /**
     * Method under test: {@link DeleteCategoryRequestVO#setCategoryObj(DeleteCategoryRequestVO)}
     */
    @Test
    public void testSetCategoryObj() {
        DeleteCategoryRequestVO request = new DeleteCategoryRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setDomainCode("Domain Code");
        request.setFixedRoles("Fixed Roles");
        request.setLastModifiedTime(1L);
        request.setSequenceNumber(10);
        CategoryVO actualSetCategoryObjResult = DeleteCategoryRequestVO.setCategoryObj(request);
        assertEquals("Category Name", actualSetCategoryObjResult.getCategoryName());
        assertEquals("Category Code", actualSetCategoryObjResult.getCategoryCode());
        assertEquals("Agent Allowed", actualSetCategoryObjResult.getAgentAllowed());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DeleteCategoryRequestVO}
     *   <li>{@link DeleteCategoryRequestVO#setAgentAllowed(String)}
     *   <li>{@link DeleteCategoryRequestVO#setCategoryCode(String)}
     *   <li>{@link DeleteCategoryRequestVO#setCategoryName(String)}
     *   <li>{@link DeleteCategoryRequestVO#setDomainCode(String)}
     *   <li>{@link DeleteCategoryRequestVO#setFixedRoles(String)}
     *   <li>{@link DeleteCategoryRequestVO#setLastModifiedTime(long)}
     *   <li>{@link DeleteCategoryRequestVO#setSequenceNumber(int)}
     *   <li>{@link DeleteCategoryRequestVO#getAgentAllowed()}
     *   <li>{@link DeleteCategoryRequestVO#getCategoryCode()}
     *   <li>{@link DeleteCategoryRequestVO#getCategoryName()}
     *   <li>{@link DeleteCategoryRequestVO#getDomainCode()}
     *   <li>{@link DeleteCategoryRequestVO#getFixedRoles()}
     *   <li>{@link DeleteCategoryRequestVO#getLastModifiedTime()}
     *   <li>{@link DeleteCategoryRequestVO#getSequenceNumber()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DeleteCategoryRequestVO actualDeleteCategoryRequestVO = new DeleteCategoryRequestVO();
        actualDeleteCategoryRequestVO.setAgentAllowed("Agent Allowed");
        actualDeleteCategoryRequestVO.setCategoryCode("Category Code");
        actualDeleteCategoryRequestVO.setCategoryName("Category Name");
        actualDeleteCategoryRequestVO.setDomainCode("Domain Code");
        actualDeleteCategoryRequestVO.setFixedRoles("Fixed Roles");
        actualDeleteCategoryRequestVO.setLastModifiedTime(1L);
        actualDeleteCategoryRequestVO.setSequenceNumber(10);
        assertEquals("Agent Allowed", actualDeleteCategoryRequestVO.getAgentAllowed());
        assertEquals("Category Code", actualDeleteCategoryRequestVO.getCategoryCode());
        assertEquals("Category Name", actualDeleteCategoryRequestVO.getCategoryName());
        assertEquals("Domain Code", actualDeleteCategoryRequestVO.getDomainCode());
        assertEquals("Fixed Roles", actualDeleteCategoryRequestVO.getFixedRoles());
        assertEquals(1L, actualDeleteCategoryRequestVO.getLastModifiedTime());
        assertEquals(10, actualDeleteCategoryRequestVO.getSequenceNumber());
    }
}

