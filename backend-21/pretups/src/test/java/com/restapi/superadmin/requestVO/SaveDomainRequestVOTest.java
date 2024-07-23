package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.domain.web.DomainForm;

import java.util.ArrayList;

import org.junit.Test;

public class SaveDomainRequestVOTest {
    /**
     * Method under test: {@link SaveDomainRequestVO#setForm(SaveDomainRequestVO)}
     */
    @Test
    public void testSetForm() {
        SaveDomainRequestVO request = new SaveDomainRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategoryStatus("Category Status");
        request.setCheckArray(new String[]{"Check Array"});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforDomain("Domain Codefor Domain");
        request.setDomainName("Domain Name");
        request.setDomainTypeCode("Domain Type Code");
        request.setFixedRoles("Fixed Roles");
        request.setGrphDomainType("Grph Domain Type");
        request.setHierarchyAllowed("Hierarchy Allowed");
        request.setLastModifiedTime(1L);
        request.setListLevelCode("List Level Code");
        request.setLowBalanceAlertAllow("Low Balance Alert Allow");
        request.setMaxLoginCount("3");
        request.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        request.setMaxTxnMsisdns("Max Txn Msisdns");
        request.setMessageGatewayList(new ArrayList());
        ArrayList modifiedMessageGatewayList = new ArrayList();
        request.setModifiedMessageGatewayList(modifiedMessageGatewayList);
        request.setModifyAllowed("Modify Allowed");
        request.setMultipleGrphDomains("Multiple Grph Domains");
        request.setMultipleLoginAllowed("Multiple Login Allowed");
        request.setNumberOfCategories("42");
        request.setOutletsAllowed("Outlets Allowed");
        request.setParentCategoryCode("Parent Category Code");
        request.setProductTypeAssociationAllowed("Product Type Association Allowed");
        request.setRechargeByParentOnly("Recharge By Parent Only");
        request.setRestrictedMsisdns("Restricted Msisdns");
        request.setRoleFlag(new String[]{"Role Flag"});
        request.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        request.setServiceAllowed("Service Allowed");
        request.setTransferToListOnly("Transfer To List Only");
        request.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        request.setUserIdPrefix("User Id Prefix");
        request.setViewOnNetworkBlock("View On Network Block");
        DomainForm actualSetFormResult = SaveDomainRequestVO.setForm(request);
        assertSame(modifiedMessageGatewayList, actualSetFormResult.getAgentModifiedMessageGatewayTypeList());
        assertEquals("Agent Category Code", actualSetFormResult.getAgentCategoryCode());
        assertEquals("Agent Allowed", actualSetFormResult.getAgentAllowed());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SaveDomainRequestVO}
     *   <li>{@link SaveDomainRequestVO#setAgentAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setAgentCategoryCode(String)}
     *   <li>{@link SaveDomainRequestVO#setCategoryCode(String)}
     *   <li>{@link SaveDomainRequestVO#setCategoryName(String)}
     *   <li>{@link SaveDomainRequestVO#setCategoryStatus(String)}
     *   <li>{@link SaveDomainRequestVO#setCheckArray(String[])}
     *   <li>{@link SaveDomainRequestVO#setCp2pPayee(String)}
     *   <li>{@link SaveDomainRequestVO#setCp2pPayer(String)}
     *   <li>{@link SaveDomainRequestVO#setCp2pWithinList(String)}
     *   <li>{@link SaveDomainRequestVO#setDisplayAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setDomainCodeforDomain(String)}
     *   <li>{@link SaveDomainRequestVO#setDomainName(String)}
     *   <li>{@link SaveDomainRequestVO#setDomainTypeCode(String)}
     *   <li>{@link SaveDomainRequestVO#setFixedRoles(String)}
     *   <li>{@link SaveDomainRequestVO#setGrphDomainType(String)}
     *   <li>{@link SaveDomainRequestVO#setHierarchyAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setLastModifiedTime(long)}
     *   <li>{@link SaveDomainRequestVO#setListLevelCode(String)}
     *   <li>{@link SaveDomainRequestVO#setLowBalanceAlertAllow(String)}
     *   <li>{@link SaveDomainRequestVO#setMaxLoginCount(String)}
     *   <li>{@link SaveDomainRequestVO#setMaxTxnMsisdnOld(String)}
     *   <li>{@link SaveDomainRequestVO#setMaxTxnMsisdns(String)}
     *   <li>{@link SaveDomainRequestVO#setMessageGatewayList(ArrayList)}
     *   <li>{@link SaveDomainRequestVO#setModifiedMessageGatewayList(ArrayList)}
     *   <li>{@link SaveDomainRequestVO#setModifyAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setMultipleGrphDomains(String)}
     *   <li>{@link SaveDomainRequestVO#setMultipleLoginAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setNumberOfCategories(String)}
     *   <li>{@link SaveDomainRequestVO#setOutletsAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setParentCategoryCode(String)}
     *   <li>{@link SaveDomainRequestVO#setProductTypeAssociationAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setRechargeByParentOnly(String)}
     *   <li>{@link SaveDomainRequestVO#setRestrictedMsisdns(String)}
     *   <li>{@link SaveDomainRequestVO#setRoleFlag(String[])}
     *   <li>{@link SaveDomainRequestVO#setScheduledTransferAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setServiceAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setTransferToListOnly(String)}
     *   <li>{@link SaveDomainRequestVO#setUnctrlTransferAllowed(String)}
     *   <li>{@link SaveDomainRequestVO#setUserIdPrefix(String)}
     *   <li>{@link SaveDomainRequestVO#setViewOnNetworkBlock(String)}
     *   <li>{@link SaveDomainRequestVO#getAgentAllowed()}
     *   <li>{@link SaveDomainRequestVO#getAgentCategoryCode()}
     *   <li>{@link SaveDomainRequestVO#getCategoryCode()}
     *   <li>{@link SaveDomainRequestVO#getCategoryName()}
     *   <li>{@link SaveDomainRequestVO#getCategoryStatus()}
     *   <li>{@link SaveDomainRequestVO#getCheckArray()}
     *   <li>{@link SaveDomainRequestVO#getCp2pPayee()}
     *   <li>{@link SaveDomainRequestVO#getCp2pPayer()}
     *   <li>{@link SaveDomainRequestVO#getCp2pWithinList()}
     *   <li>{@link SaveDomainRequestVO#getDisplayAllowed()}
     *   <li>{@link SaveDomainRequestVO#getDomainCodeforDomain()}
     *   <li>{@link SaveDomainRequestVO#getDomainName()}
     *   <li>{@link SaveDomainRequestVO#getDomainTypeCode()}
     *   <li>{@link SaveDomainRequestVO#getFixedRoles()}
     *   <li>{@link SaveDomainRequestVO#getGrphDomainType()}
     *   <li>{@link SaveDomainRequestVO#getHierarchyAllowed()}
     *   <li>{@link SaveDomainRequestVO#getLastModifiedTime()}
     *   <li>{@link SaveDomainRequestVO#getListLevelCode()}
     *   <li>{@link SaveDomainRequestVO#getLowBalanceAlertAllow()}
     *   <li>{@link SaveDomainRequestVO#getMaxLoginCount()}
     *   <li>{@link SaveDomainRequestVO#getMaxTxnMsisdnOld()}
     *   <li>{@link SaveDomainRequestVO#getMaxTxnMsisdns()}
     *   <li>{@link SaveDomainRequestVO#getMessageGatewayList()}
     *   <li>{@link SaveDomainRequestVO#getModifiedMessageGatewayList()}
     *   <li>{@link SaveDomainRequestVO#getModifyAllowed()}
     *   <li>{@link SaveDomainRequestVO#getMultipleGrphDomains()}
     *   <li>{@link SaveDomainRequestVO#getMultipleLoginAllowed()}
     *   <li>{@link SaveDomainRequestVO#getNumberOfCategories()}
     *   <li>{@link SaveDomainRequestVO#getOutletsAllowed()}
     *   <li>{@link SaveDomainRequestVO#getParentCategoryCode()}
     *   <li>{@link SaveDomainRequestVO#getProductTypeAssociationAllowed()}
     *   <li>{@link SaveDomainRequestVO#getRechargeByParentOnly()}
     *   <li>{@link SaveDomainRequestVO#getRestrictedMsisdns()}
     *   <li>{@link SaveDomainRequestVO#getRoleFlag()}
     *   <li>{@link SaveDomainRequestVO#getScheduledTransferAllowed()}
     *   <li>{@link SaveDomainRequestVO#getServiceAllowed()}
     *   <li>{@link SaveDomainRequestVO#getTransferToListOnly()}
     *   <li>{@link SaveDomainRequestVO#getUnctrlTransferAllowed()}
     *   <li>{@link SaveDomainRequestVO#getUserIdPrefix()}
     *   <li>{@link SaveDomainRequestVO#getViewOnNetworkBlock()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SaveDomainRequestVO actualSaveDomainRequestVO = new SaveDomainRequestVO();
        actualSaveDomainRequestVO.setAgentAllowed("Agent Allowed");
        actualSaveDomainRequestVO.setAgentCategoryCode("Agent Category Code");
        actualSaveDomainRequestVO.setCategoryCode("Category Code");
        actualSaveDomainRequestVO.setCategoryName("Category Name");
        actualSaveDomainRequestVO.setCategoryStatus("Category Status");
        String[] checkArray = new String[]{"Check Array"};
        actualSaveDomainRequestVO.setCheckArray(checkArray);
        actualSaveDomainRequestVO.setCp2pPayee("Cp2p Payee");
        actualSaveDomainRequestVO.setCp2pPayer("Cp2p Payer");
        actualSaveDomainRequestVO.setCp2pWithinList("Cp2p Within List");
        actualSaveDomainRequestVO.setDisplayAllowed("Display Allowed");
        actualSaveDomainRequestVO.setDomainCodeforDomain("Domain Codefor Domain");
        actualSaveDomainRequestVO.setDomainName("Domain Name");
        actualSaveDomainRequestVO.setDomainTypeCode("Domain Type Code");
        actualSaveDomainRequestVO.setFixedRoles("Fixed Roles");
        actualSaveDomainRequestVO.setGrphDomainType("Grph Domain Type");
        actualSaveDomainRequestVO.setHierarchyAllowed("Hierarchy Allowed");
        actualSaveDomainRequestVO.setLastModifiedTime(1L);
        actualSaveDomainRequestVO.setListLevelCode("List Level Code");
        actualSaveDomainRequestVO.setLowBalanceAlertAllow("Low Balance Alert Allow");
        actualSaveDomainRequestVO.setMaxLoginCount("3");
        actualSaveDomainRequestVO.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        actualSaveDomainRequestVO.setMaxTxnMsisdns("Max Txn Msisdns");
        ArrayList messageGatewayList = new ArrayList();
        actualSaveDomainRequestVO.setMessageGatewayList(messageGatewayList);
        ArrayList modifiedMessageGatewayList = new ArrayList();
        actualSaveDomainRequestVO.setModifiedMessageGatewayList(modifiedMessageGatewayList);
        actualSaveDomainRequestVO.setModifyAllowed("Modify Allowed");
        actualSaveDomainRequestVO.setMultipleGrphDomains("Multiple Grph Domains");
        actualSaveDomainRequestVO.setMultipleLoginAllowed("Multiple Login Allowed");
        actualSaveDomainRequestVO.setNumberOfCategories("42");
        actualSaveDomainRequestVO.setOutletsAllowed("Outlets Allowed");
        actualSaveDomainRequestVO.setParentCategoryCode("Parent Category Code");
        actualSaveDomainRequestVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        actualSaveDomainRequestVO.setRechargeByParentOnly("Recharge By Parent Only");
        actualSaveDomainRequestVO.setRestrictedMsisdns("Restricted Msisdns");
        String[] roleFlag = new String[]{"Role Flag"};
        actualSaveDomainRequestVO.setRoleFlag(roleFlag);
        actualSaveDomainRequestVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        actualSaveDomainRequestVO.setServiceAllowed("Service Allowed");
        actualSaveDomainRequestVO.setTransferToListOnly("Transfer To List Only");
        actualSaveDomainRequestVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        actualSaveDomainRequestVO.setUserIdPrefix("User Id Prefix");
        actualSaveDomainRequestVO.setViewOnNetworkBlock("View On Network Block");
        assertEquals("Agent Allowed", actualSaveDomainRequestVO.getAgentAllowed());
        assertEquals("Agent Category Code", actualSaveDomainRequestVO.getAgentCategoryCode());
        assertEquals("Category Code", actualSaveDomainRequestVO.getCategoryCode());
        assertEquals("Category Name", actualSaveDomainRequestVO.getCategoryName());
        assertEquals("Category Status", actualSaveDomainRequestVO.getCategoryStatus());
        assertSame(checkArray, actualSaveDomainRequestVO.getCheckArray());
        assertEquals("Cp2p Payee", actualSaveDomainRequestVO.getCp2pPayee());
        assertEquals("Cp2p Payer", actualSaveDomainRequestVO.getCp2pPayer());
        assertEquals("Cp2p Within List", actualSaveDomainRequestVO.getCp2pWithinList());
        assertEquals("Display Allowed", actualSaveDomainRequestVO.getDisplayAllowed());
        assertEquals("Domain Codefor Domain", actualSaveDomainRequestVO.getDomainCodeforDomain());
        assertEquals("Domain Name", actualSaveDomainRequestVO.getDomainName());
        assertEquals("Domain Type Code", actualSaveDomainRequestVO.getDomainTypeCode());
        assertEquals("Fixed Roles", actualSaveDomainRequestVO.getFixedRoles());
        assertEquals("Grph Domain Type", actualSaveDomainRequestVO.getGrphDomainType());
        assertEquals("Hierarchy Allowed", actualSaveDomainRequestVO.getHierarchyAllowed());
        assertEquals(1L, actualSaveDomainRequestVO.getLastModifiedTime());
        assertEquals("List Level Code", actualSaveDomainRequestVO.getListLevelCode());
        assertEquals("Low Balance Alert Allow", actualSaveDomainRequestVO.getLowBalanceAlertAllow());
        assertEquals("3", actualSaveDomainRequestVO.getMaxLoginCount());
        assertEquals("Max Txn Msisdn Old", actualSaveDomainRequestVO.getMaxTxnMsisdnOld());
        assertEquals("Max Txn Msisdns", actualSaveDomainRequestVO.getMaxTxnMsisdns());
        assertSame(messageGatewayList, actualSaveDomainRequestVO.getMessageGatewayList());
        assertSame(modifiedMessageGatewayList, actualSaveDomainRequestVO.getModifiedMessageGatewayList());
        assertEquals("Modify Allowed", actualSaveDomainRequestVO.getModifyAllowed());
        assertEquals("Multiple Grph Domains", actualSaveDomainRequestVO.getMultipleGrphDomains());
        assertEquals("Multiple Login Allowed", actualSaveDomainRequestVO.getMultipleLoginAllowed());
        assertEquals("42", actualSaveDomainRequestVO.getNumberOfCategories());
        assertEquals("Outlets Allowed", actualSaveDomainRequestVO.getOutletsAllowed());
        assertEquals("Parent Category Code", actualSaveDomainRequestVO.getParentCategoryCode());
        assertEquals("Product Type Association Allowed", actualSaveDomainRequestVO.getProductTypeAssociationAllowed());
        assertEquals("Recharge By Parent Only", actualSaveDomainRequestVO.getRechargeByParentOnly());
        assertEquals("Restricted Msisdns", actualSaveDomainRequestVO.getRestrictedMsisdns());
        assertSame(roleFlag, actualSaveDomainRequestVO.getRoleFlag());
        assertEquals("Scheduled Transfer Allowed", actualSaveDomainRequestVO.getScheduledTransferAllowed());
        assertEquals("Service Allowed", actualSaveDomainRequestVO.getServiceAllowed());
        assertEquals("Transfer To List Only", actualSaveDomainRequestVO.getTransferToListOnly());
        assertEquals("Unctrl Transfer Allowed", actualSaveDomainRequestVO.getUnctrlTransferAllowed());
        assertEquals("User Id Prefix", actualSaveDomainRequestVO.getUserIdPrefix());
        assertEquals("View On Network Block", actualSaveDomainRequestVO.getViewOnNetworkBlock());
    }
}

