package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class AddAgentRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddAgentRequestVO}
     *   <li>{@link AddAgentRequestVO#setAgentAllowed(String)}
     *   <li>{@link AddAgentRequestVO#setAgentCategoryCode(String)}
     *   <li>{@link AddAgentRequestVO#setAgentCategoryName(String)}
     *   <li>{@link AddAgentRequestVO#setAllowLowBalanceAlert(String)}
     *   <li>{@link AddAgentRequestVO#setAllowedSources(String)}
     *   <li>{@link AddAgentRequestVO#setCp2pPayee(String)}
     *   <li>{@link AddAgentRequestVO#setCp2pPayer(String)}
     *   <li>{@link AddAgentRequestVO#setCp2pWithinList(String)}
     *   <li>{@link AddAgentRequestVO#setDomainCodeofCategory(String)}
     *   <li>{@link AddAgentRequestVO#setDomainName(String)}
     *   <li>{@link AddAgentRequestVO#setGeoDomainType(String)}
     *   <li>{@link AddAgentRequestVO#setHierarchyAllowed(String)}
     *   <li>{@link AddAgentRequestVO#setMaximumLoginCount(String)}
     *   <li>{@link AddAgentRequestVO#setMaximumTransMsisdn(String)}
     *   <li>{@link AddAgentRequestVO#setMultipleLoginAllowed(String)}
     *   <li>{@link AddAgentRequestVO#setOutletAllowed(String)}
     *   <li>{@link AddAgentRequestVO#setParentCategoryCode(String)}
     *   <li>{@link AddAgentRequestVO#setParentOrOwnerRadioValue(String)}
     *   <li>{@link AddAgentRequestVO#setRechargeThruParentOnly(String)}
     *   <li>{@link AddAgentRequestVO#setRestrictedMsisdn(String)}
     *   <li>{@link AddAgentRequestVO#setRoleFlag(String[])}
     *   <li>{@link AddAgentRequestVO#setRoleType(String)}
     *   <li>{@link AddAgentRequestVO#setScheduleTransferAllowed(String)}
     *   <li>{@link AddAgentRequestVO#setServicesAllowed(String)}
     *   <li>{@link AddAgentRequestVO#setTransferToListOnly(String)}
     *   <li>{@link AddAgentRequestVO#setUncontrolledTransferAllowed(String)}
     *   <li>{@link AddAgentRequestVO#setUserIDPrefix(String)}
     *   <li>{@link AddAgentRequestVO#setViewonNetworkBlock(String)}
     *   <li>{@link AddAgentRequestVO#getAgentAllowed()}
     *   <li>{@link AddAgentRequestVO#getAgentCategoryCode()}
     *   <li>{@link AddAgentRequestVO#getAgentCategoryName()}
     *   <li>{@link AddAgentRequestVO#getAllowLowBalanceAlert()}
     *   <li>{@link AddAgentRequestVO#getAllowedSources()}
     *   <li>{@link AddAgentRequestVO#getCp2pPayee()}
     *   <li>{@link AddAgentRequestVO#getCp2pPayer()}
     *   <li>{@link AddAgentRequestVO#getCp2pWithinList()}
     *   <li>{@link AddAgentRequestVO#getDomainCodeofCategory()}
     *   <li>{@link AddAgentRequestVO#getDomainName()}
     *   <li>{@link AddAgentRequestVO#getGeoDomainType()}
     *   <li>{@link AddAgentRequestVO#getHierarchyAllowed()}
     *   <li>{@link AddAgentRequestVO#getMaximumLoginCount()}
     *   <li>{@link AddAgentRequestVO#getMaximumTransMsisdn()}
     *   <li>{@link AddAgentRequestVO#getMultipleLoginAllowed()}
     *   <li>{@link AddAgentRequestVO#getOutletAllowed()}
     *   <li>{@link AddAgentRequestVO#getParentCategoryCode()}
     *   <li>{@link AddAgentRequestVO#getParentOrOwnerRadioValue()}
     *   <li>{@link AddAgentRequestVO#getRechargeThruParentOnly()}
     *   <li>{@link AddAgentRequestVO#getRestrictedMsisdn()}
     *   <li>{@link AddAgentRequestVO#getRoleFlag()}
     *   <li>{@link AddAgentRequestVO#getRoleType()}
     *   <li>{@link AddAgentRequestVO#getScheduleTransferAllowed()}
     *   <li>{@link AddAgentRequestVO#getServicesAllowed()}
     *   <li>{@link AddAgentRequestVO#getTransferToListOnly()}
     *   <li>{@link AddAgentRequestVO#getUncontrolledTransferAllowed()}
     *   <li>{@link AddAgentRequestVO#getUserIDPrefix()}
     *   <li>{@link AddAgentRequestVO#getViewonNetworkBlock()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AddAgentRequestVO actualAddAgentRequestVO = new AddAgentRequestVO();
        actualAddAgentRequestVO.setAgentAllowed("Agent Allowed");
        actualAddAgentRequestVO.setAgentCategoryCode("Agent Category Code");
        actualAddAgentRequestVO.setAgentCategoryName("Agent Category Name");
        actualAddAgentRequestVO.setAllowLowBalanceAlert("Allow Low Balance Alert");
        actualAddAgentRequestVO.setAllowedSources("Allowed Sources");
        actualAddAgentRequestVO.setCp2pPayee("Cp2p Payee");
        actualAddAgentRequestVO.setCp2pPayer("Cp2p Payer");
        actualAddAgentRequestVO.setCp2pWithinList("Cp2p Within List");
        actualAddAgentRequestVO.setDomainCodeofCategory("Domain Codeof Category");
        actualAddAgentRequestVO.setDomainName("Domain Name");
        actualAddAgentRequestVO.setGeoDomainType("Geo Domain Type");
        actualAddAgentRequestVO.setHierarchyAllowed("Hierarchy Allowed");
        actualAddAgentRequestVO.setMaximumLoginCount("3");
        actualAddAgentRequestVO.setMaximumTransMsisdn("Maximum Trans Msisdn");
        actualAddAgentRequestVO.setMultipleLoginAllowed("Multiple Login Allowed");
        actualAddAgentRequestVO.setOutletAllowed("Outlet Allowed");
        actualAddAgentRequestVO.setParentCategoryCode("Parent Category Code");
        actualAddAgentRequestVO.setParentOrOwnerRadioValue("42");
        actualAddAgentRequestVO.setRechargeThruParentOnly("Recharge Thru Parent Only");
        actualAddAgentRequestVO.setRestrictedMsisdn("Restricted Msisdn");
        String[] roleFlag = new String[]{"Role Flag"};
        actualAddAgentRequestVO.setRoleFlag(roleFlag);
        actualAddAgentRequestVO.setRoleType("Role Type");
        actualAddAgentRequestVO.setScheduleTransferAllowed("Schedule Transfer Allowed");
        actualAddAgentRequestVO.setServicesAllowed("Services Allowed");
        actualAddAgentRequestVO.setTransferToListOnly("Transfer To List Only");
        actualAddAgentRequestVO.setUncontrolledTransferAllowed("Uncontrolled Transfer Allowed");
        actualAddAgentRequestVO.setUserIDPrefix("User IDPrefix");
        actualAddAgentRequestVO.setViewonNetworkBlock("Viewon Network Block");
        assertEquals("Agent Allowed", actualAddAgentRequestVO.getAgentAllowed());
        assertEquals("Agent Category Code", actualAddAgentRequestVO.getAgentCategoryCode());
        assertEquals("Agent Category Name", actualAddAgentRequestVO.getAgentCategoryName());
        assertEquals("Allow Low Balance Alert", actualAddAgentRequestVO.getAllowLowBalanceAlert());
        assertEquals("Allowed Sources", actualAddAgentRequestVO.getAllowedSources());
        assertEquals("Cp2p Payee", actualAddAgentRequestVO.getCp2pPayee());
        assertEquals("Cp2p Payer", actualAddAgentRequestVO.getCp2pPayer());
        assertEquals("Cp2p Within List", actualAddAgentRequestVO.getCp2pWithinList());
        assertEquals("Domain Codeof Category", actualAddAgentRequestVO.getDomainCodeofCategory());
        assertEquals("Domain Name", actualAddAgentRequestVO.getDomainName());
        assertEquals("Geo Domain Type", actualAddAgentRequestVO.getGeoDomainType());
        assertEquals("Hierarchy Allowed", actualAddAgentRequestVO.getHierarchyAllowed());
        assertEquals("3", actualAddAgentRequestVO.getMaximumLoginCount());
        assertEquals("Maximum Trans Msisdn", actualAddAgentRequestVO.getMaximumTransMsisdn());
        assertEquals("Multiple Login Allowed", actualAddAgentRequestVO.getMultipleLoginAllowed());
        assertEquals("Outlet Allowed", actualAddAgentRequestVO.getOutletAllowed());
        assertEquals("Parent Category Code", actualAddAgentRequestVO.getParentCategoryCode());
        assertEquals("42", actualAddAgentRequestVO.getParentOrOwnerRadioValue());
        assertEquals("Recharge Thru Parent Only", actualAddAgentRequestVO.getRechargeThruParentOnly());
        assertEquals("Restricted Msisdn", actualAddAgentRequestVO.getRestrictedMsisdn());
        assertSame(roleFlag, actualAddAgentRequestVO.getRoleFlag());
        assertEquals("Role Type", actualAddAgentRequestVO.getRoleType());
        assertEquals("Schedule Transfer Allowed", actualAddAgentRequestVO.getScheduleTransferAllowed());
        assertEquals("Services Allowed", actualAddAgentRequestVO.getServicesAllowed());
        assertEquals("Transfer To List Only", actualAddAgentRequestVO.getTransferToListOnly());
        assertEquals("Uncontrolled Transfer Allowed", actualAddAgentRequestVO.getUncontrolledTransferAllowed());
        assertEquals("User IDPrefix", actualAddAgentRequestVO.getUserIDPrefix());
        assertEquals("Viewon Network Block", actualAddAgentRequestVO.getViewonNetworkBlock());
    }
}

