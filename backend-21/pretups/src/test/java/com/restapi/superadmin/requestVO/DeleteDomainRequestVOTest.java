package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.btsl.pretups.domain.businesslogic.DomainVO;
import org.junit.Test;

public class DeleteDomainRequestVOTest {
    /**
     * Method under test: {@link DeleteDomainRequestVO#convertObject(DeleteDomainRequestVO)}
     */
    @Test
    public void testConvertObject() {
        DeleteDomainRequestVO request = new DeleteDomainRequestVO();
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setIsSuspend(true);
        request.setLastModifiedTime(1L);
        DomainVO actualConvertObjectResult = DeleteDomainRequestVO.convertObject(request);
        assertEquals(1L, actualConvertObjectResult.getLastModifiedTime());
        assertEquals("Domain Codefor Domain", actualConvertObjectResult.getDomainCodeforDomain());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DeleteDomainRequestVO}
     *   <li>{@link DeleteDomainRequestVO#setDomainCodeforDomain(String)}
     *   <li>{@link DeleteDomainRequestVO#setIsSuspend(Boolean)}
     *   <li>{@link DeleteDomainRequestVO#setLastModifiedTime(long)}
     *   <li>{@link DeleteDomainRequestVO#toString()}
     *   <li>{@link DeleteDomainRequestVO#getDomainCodeforDomain()}
     *   <li>{@link DeleteDomainRequestVO#getIsSuspend()}
     *   <li>{@link DeleteDomainRequestVO#getLastModifiedTime()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DeleteDomainRequestVO actualDeleteDomainRequestVO = new DeleteDomainRequestVO();
        actualDeleteDomainRequestVO.setDomainCodeforDomain("Domain Codefor Domain");
        actualDeleteDomainRequestVO.setIsSuspend(true);
        actualDeleteDomainRequestVO.setLastModifiedTime(1L);
        String actualToStringResult = actualDeleteDomainRequestVO.toString();
        assertEquals("Domain Codefor Domain", actualDeleteDomainRequestVO.getDomainCodeforDomain());
        assertTrue(actualDeleteDomainRequestVO.getIsSuspend());
        assertEquals(1L, actualDeleteDomainRequestVO.getLastModifiedTime());
        assertEquals(
                "DeleteDomainRequestVO [domainCodeforDomain=Domain Codefor Domain, lastModifiedTime=1," + " isSuspend=true]",
                actualToStringResult);
    }
}

