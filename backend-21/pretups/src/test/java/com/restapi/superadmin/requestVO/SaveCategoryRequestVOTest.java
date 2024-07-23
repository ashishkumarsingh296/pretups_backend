package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.domain.web.DomainForm;

import java.util.ArrayList;

import org.junit.Rule;

import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SaveCategoryRequestVOTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link SaveCategoryRequestVO#setForm(SaveCategoryRequestVO)}
     */
    @Test
    public void testSetForm() throws BTSLBaseException {
        SaveCategoryRequestVO request = new SaveCategoryRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setAuthType("Auth Type");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategorySequenceNumber(10);
        request.setCategoryStatus("Category Status");
        request.setCategoryType("Category Type");
        request.setCheckArray(new String[]{"Check Array"});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforCategory("Domain Codefor Category");
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
        DomainForm actualSetFormResult = SaveCategoryRequestVO.setForm(request);
        assertSame(modifiedMessageGatewayList, actualSetFormResult.getAgentModifiedMessageGatewayTypeList());
        assertEquals("Agent Category Code", actualSetFormResult.getAgentCategoryCode());
        assertEquals("Agent Allowed", actualSetFormResult.getAgentAllowed());
    }

    /**
     * Method under test: {@link SaveCategoryRequestVO#setForm(SaveCategoryRequestVO)}
     */
    @Test
    public void testSetForm2() throws BTSLBaseException {
        SaveCategoryRequestVO request = new SaveCategoryRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setAuthType("Auth Type");
        request.setCategoryCode("Category Code");
        request.setCategorySequenceNumber(10);
        request.setCategoryStatus("Category Status");
        request.setCategoryType("Category Type");
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforCategory("Domain Codefor Category");
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
        request.setCheckArray(null);
        request.setDomainCodeforDomain(null);
        request.setDomainName(null);
        request.setCategoryName(null);
        DomainForm actualSetFormResult = SaveCategoryRequestVO.setForm(request);
        assertSame(modifiedMessageGatewayList, actualSetFormResult.getAgentModifiedMessageGatewayTypeList());
        assertEquals("Agent Category Code", actualSetFormResult.getAgentCategoryCode());
        assertEquals("Agent Allowed", actualSetFormResult.getAgentAllowed());
    }

    /**
     * Method under test: {@link SaveCategoryRequestVO#setForm(SaveCategoryRequestVO)}
     */
    @Test
    public void testSetForm3() throws BTSLBaseException {
        SaveCategoryRequestVO request = new SaveCategoryRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setAuthType("Auth Type");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategorySequenceNumber(10);
        request.setCategoryStatus("Category Status");
        request.setCategoryType("Category Type");
        request.setCheckArray(new String[]{null});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforCategory("Domain Codefor Category");
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
        request.setModifiedMessageGatewayList(new ArrayList());
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
        thrown.expect(BTSLBaseException.class);
        SaveCategoryRequestVO.setForm(request);
    }

    /**
     * Method under test: {@link SaveCategoryRequestVO#setForm(SaveCategoryRequestVO)}
     */
    @Test
    public void testSetForm4() throws BTSLBaseException {
        SaveCategoryRequestVO request = new SaveCategoryRequestVO();
        request.setAgentAllowed("Agent Allowed");
        request.setAgentCategoryCode("Agent Category Code");
        request.setAuthType("Auth Type");
        request.setCategoryCode("Category Code");
        request.setCategoryName("Category Name");
        request.setCategorySequenceNumber(10);
        request.setCategoryStatus("Category Status");
        request.setCategoryType("Category Type");
        request.setCheckArray(new String[]{""});
        request.setCp2pPayee("Cp2p Payee");
        request.setCp2pPayer("Cp2p Payer");
        request.setCp2pWithinList("Cp2p Within List");
        request.setDisplayAllowed("Display Allowed");
        request.setDomainCodeforCategory("Domain Codefor Category");
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
        request.setModifiedMessageGatewayList(new ArrayList());
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
        thrown.expect(BTSLBaseException.class);
        SaveCategoryRequestVO.setForm(request);
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SaveCategoryRequestVO}
     *   <li>{@link SaveCategoryRequestVO#setAgentAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setAgentCategoryCode(String)}
     *   <li>{@link SaveCategoryRequestVO#setAuthType(String)}
     *   <li>{@link SaveCategoryRequestVO#setCategoryCode(String)}
     *   <li>{@link SaveCategoryRequestVO#setCategoryName(String)}
     *   <li>{@link SaveCategoryRequestVO#setCategorySequenceNumber(int)}
     *   <li>{@link SaveCategoryRequestVO#setCategoryStatus(String)}
     *   <li>{@link SaveCategoryRequestVO#setCategoryType(String)}
     *   <li>{@link SaveCategoryRequestVO#setCheckArray(String[])}
     *   <li>{@link SaveCategoryRequestVO#setCp2pPayee(String)}
     *   <li>{@link SaveCategoryRequestVO#setCp2pPayer(String)}
     *   <li>{@link SaveCategoryRequestVO#setCp2pWithinList(String)}
     *   <li>{@link SaveCategoryRequestVO#setDisplayAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setDomainCodeforCategory(String)}
     *   <li>{@link SaveCategoryRequestVO#setDomainCodeforDomain(String)}
     *   <li>{@link SaveCategoryRequestVO#setDomainName(String)}
     *   <li>{@link SaveCategoryRequestVO#setDomainTypeCode(String)}
     *   <li>{@link SaveCategoryRequestVO#setFixedRoles(String)}
     *   <li>{@link SaveCategoryRequestVO#setGrphDomainType(String)}
     *   <li>{@link SaveCategoryRequestVO#setHierarchyAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setLastModifiedTime(long)}
     *   <li>{@link SaveCategoryRequestVO#setListLevelCode(String)}
     *   <li>{@link SaveCategoryRequestVO#setLowBalanceAlertAllow(String)}
     *   <li>{@link SaveCategoryRequestVO#setMaxLoginCount(String)}
     *   <li>{@link SaveCategoryRequestVO#setMaxTxnMsisdnOld(String)}
     *   <li>{@link SaveCategoryRequestVO#setMaxTxnMsisdns(String)}
     *   <li>{@link SaveCategoryRequestVO#setMessageGatewayList(ArrayList)}
     *   <li>{@link SaveCategoryRequestVO#setModifiedMessageGatewayList(ArrayList)}
     *   <li>{@link SaveCategoryRequestVO#setModifyAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setMultipleGrphDomains(String)}
     *   <li>{@link SaveCategoryRequestVO#setMultipleLoginAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setNumberOfCategories(String)}
     *   <li>{@link SaveCategoryRequestVO#setOutletsAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setParentCategoryCode(String)}
     *   <li>{@link SaveCategoryRequestVO#setProductTypeAssociationAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setRechargeByParentOnly(String)}
     *   <li>{@link SaveCategoryRequestVO#setRestrictedMsisdns(String)}
     *   <li>{@link SaveCategoryRequestVO#setRoleFlag(String[])}
     *   <li>{@link SaveCategoryRequestVO#setScheduledTransferAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setServiceAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setTransferToListOnly(String)}
     *   <li>{@link SaveCategoryRequestVO#setUnctrlTransferAllowed(String)}
     *   <li>{@link SaveCategoryRequestVO#setUserIdPrefix(String)}
     *   <li>{@link SaveCategoryRequestVO#setViewOnNetworkBlock(String)}
     *   <li>{@link SaveCategoryRequestVO#getAgentAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getAgentCategoryCode()}
     *   <li>{@link SaveCategoryRequestVO#getAuthType()}
     *   <li>{@link SaveCategoryRequestVO#getCategoryCode()}
     *   <li>{@link SaveCategoryRequestVO#getCategoryName()}
     *   <li>{@link SaveCategoryRequestVO#getCategorySequenceNumber()}
     *   <li>{@link SaveCategoryRequestVO#getCategoryStatus()}
     *   <li>{@link SaveCategoryRequestVO#getCategoryType()}
     *   <li>{@link SaveCategoryRequestVO#getCheckArray()}
     *   <li>{@link SaveCategoryRequestVO#getCp2pPayee()}
     *   <li>{@link SaveCategoryRequestVO#getCp2pPayer()}
     *   <li>{@link SaveCategoryRequestVO#getCp2pWithinList()}
     *   <li>{@link SaveCategoryRequestVO#getDisplayAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getDomainCodeforCategory()}
     *   <li>{@link SaveCategoryRequestVO#getDomainCodeforDomain()}
     *   <li>{@link SaveCategoryRequestVO#getDomainName()}
     *   <li>{@link SaveCategoryRequestVO#getDomainTypeCode()}
     *   <li>{@link SaveCategoryRequestVO#getFixedRoles()}
     *   <li>{@link SaveCategoryRequestVO#getGrphDomainType()}
     *   <li>{@link SaveCategoryRequestVO#getHierarchyAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getLastModifiedTime()}
     *   <li>{@link SaveCategoryRequestVO#getListLevelCode()}
     *   <li>{@link SaveCategoryRequestVO#getLowBalanceAlertAllow()}
     *   <li>{@link SaveCategoryRequestVO#getMaxLoginCount()}
     *   <li>{@link SaveCategoryRequestVO#getMaxTxnMsisdnOld()}
     *   <li>{@link SaveCategoryRequestVO#getMaxTxnMsisdns()}
     *   <li>{@link SaveCategoryRequestVO#getMessageGatewayList()}
     *   <li>{@link SaveCategoryRequestVO#getModifiedMessageGatewayList()}
     *   <li>{@link SaveCategoryRequestVO#getModifyAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getMultipleGrphDomains()}
     *   <li>{@link SaveCategoryRequestVO#getMultipleLoginAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getNumberOfCategories()}
     *   <li>{@link SaveCategoryRequestVO#getOutletsAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getParentCategoryCode()}
     *   <li>{@link SaveCategoryRequestVO#getProductTypeAssociationAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getRechargeByParentOnly()}
     *   <li>{@link SaveCategoryRequestVO#getRestrictedMsisdns()}
     *   <li>{@link SaveCategoryRequestVO#getRoleFlag()}
     *   <li>{@link SaveCategoryRequestVO#getScheduledTransferAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getServiceAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getTransferToListOnly()}
     *   <li>{@link SaveCategoryRequestVO#getUnctrlTransferAllowed()}
     *   <li>{@link SaveCategoryRequestVO#getUserIdPrefix()}
     *   <li>{@link SaveCategoryRequestVO#getViewOnNetworkBlock()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SaveCategoryRequestVO actualSaveCategoryRequestVO = new SaveCategoryRequestVO();
        actualSaveCategoryRequestVO.setAgentAllowed("Agent Allowed");
        actualSaveCategoryRequestVO.setAgentCategoryCode("Agent Category Code");
        actualSaveCategoryRequestVO.setAuthType("Auth Type");
        actualSaveCategoryRequestVO.setCategoryCode("Category Code");
        actualSaveCategoryRequestVO.setCategoryName("Category Name");
        actualSaveCategoryRequestVO.setCategorySequenceNumber(10);
        actualSaveCategoryRequestVO.setCategoryStatus("Category Status");
        actualSaveCategoryRequestVO.setCategoryType("Category Type");
        String[] checkArray = new String[]{"Check Array"};
        actualSaveCategoryRequestVO.setCheckArray(checkArray);
        actualSaveCategoryRequestVO.setCp2pPayee("Cp2p Payee");
        actualSaveCategoryRequestVO.setCp2pPayer("Cp2p Payer");
        actualSaveCategoryRequestVO.setCp2pWithinList("Cp2p Within List");
        actualSaveCategoryRequestVO.setDisplayAllowed("Display Allowed");
        actualSaveCategoryRequestVO.setDomainCodeforCategory("Domain Codefor Category");
        actualSaveCategoryRequestVO.setDomainCodeforDomain("Domain Codefor Domain");
        actualSaveCategoryRequestVO.setDomainName("Domain Name");
        actualSaveCategoryRequestVO.setDomainTypeCode("Domain Type Code");
        actualSaveCategoryRequestVO.setFixedRoles("Fixed Roles");
        actualSaveCategoryRequestVO.setGrphDomainType("Grph Domain Type");
        actualSaveCategoryRequestVO.setHierarchyAllowed("Hierarchy Allowed");
        actualSaveCategoryRequestVO.setLastModifiedTime(1L);
        actualSaveCategoryRequestVO.setListLevelCode("List Level Code");
        actualSaveCategoryRequestVO.setLowBalanceAlertAllow("Low Balance Alert Allow");
        actualSaveCategoryRequestVO.setMaxLoginCount("3");
        actualSaveCategoryRequestVO.setMaxTxnMsisdnOld("Max Txn Msisdn Old");
        actualSaveCategoryRequestVO.setMaxTxnMsisdns("Max Txn Msisdns");
        ArrayList messageGatewayList = new ArrayList();
        actualSaveCategoryRequestVO.setMessageGatewayList(messageGatewayList);
        ArrayList modifiedMessageGatewayList = new ArrayList();
        actualSaveCategoryRequestVO.setModifiedMessageGatewayList(modifiedMessageGatewayList);
        actualSaveCategoryRequestVO.setModifyAllowed("Modify Allowed");
        actualSaveCategoryRequestVO.setMultipleGrphDomains("Multiple Grph Domains");
        actualSaveCategoryRequestVO.setMultipleLoginAllowed("Multiple Login Allowed");
        actualSaveCategoryRequestVO.setNumberOfCategories("42");
        actualSaveCategoryRequestVO.setOutletsAllowed("Outlets Allowed");
        actualSaveCategoryRequestVO.setParentCategoryCode("Parent Category Code");
        actualSaveCategoryRequestVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        actualSaveCategoryRequestVO.setRechargeByParentOnly("Recharge By Parent Only");
        actualSaveCategoryRequestVO.setRestrictedMsisdns("Restricted Msisdns");
        String[] roleFlag = new String[]{"Role Flag"};
        actualSaveCategoryRequestVO.setRoleFlag(roleFlag);
        actualSaveCategoryRequestVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        actualSaveCategoryRequestVO.setServiceAllowed("Service Allowed");
        actualSaveCategoryRequestVO.setTransferToListOnly("Transfer To List Only");
        actualSaveCategoryRequestVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        actualSaveCategoryRequestVO.setUserIdPrefix("User Id Prefix");
        actualSaveCategoryRequestVO.setViewOnNetworkBlock("View On Network Block");
        assertEquals("Agent Allowed", actualSaveCategoryRequestVO.getAgentAllowed());
        assertEquals("Agent Category Code", actualSaveCategoryRequestVO.getAgentCategoryCode());
        assertEquals("Auth Type", actualSaveCategoryRequestVO.getAuthType());
        assertEquals("Category Code", actualSaveCategoryRequestVO.getCategoryCode());
        assertEquals("Category Name", actualSaveCategoryRequestVO.getCategoryName());
        assertEquals(10, actualSaveCategoryRequestVO.getCategorySequenceNumber());
        assertEquals("Category Status", actualSaveCategoryRequestVO.getCategoryStatus());
        assertEquals("Category Type", actualSaveCategoryRequestVO.getCategoryType());
        assertSame(checkArray, actualSaveCategoryRequestVO.getCheckArray());
        assertEquals("Cp2p Payee", actualSaveCategoryRequestVO.getCp2pPayee());
        assertEquals("Cp2p Payer", actualSaveCategoryRequestVO.getCp2pPayer());
        assertEquals("Cp2p Within List", actualSaveCategoryRequestVO.getCp2pWithinList());
        assertEquals("Display Allowed", actualSaveCategoryRequestVO.getDisplayAllowed());
        assertEquals("Domain Codefor Category", actualSaveCategoryRequestVO.getDomainCodeforCategory());
        assertEquals("Domain Codefor Domain", actualSaveCategoryRequestVO.getDomainCodeforDomain());
        assertEquals("Domain Name", actualSaveCategoryRequestVO.getDomainName());
        assertEquals("Domain Type Code", actualSaveCategoryRequestVO.getDomainTypeCode());
        assertEquals("Fixed Roles", actualSaveCategoryRequestVO.getFixedRoles());
        assertEquals("Grph Domain Type", actualSaveCategoryRequestVO.getGrphDomainType());
        assertEquals("Hierarchy Allowed", actualSaveCategoryRequestVO.getHierarchyAllowed());
        assertEquals(1L, actualSaveCategoryRequestVO.getLastModifiedTime());
        assertEquals("List Level Code", actualSaveCategoryRequestVO.getListLevelCode());
        assertEquals("Low Balance Alert Allow", actualSaveCategoryRequestVO.getLowBalanceAlertAllow());
        assertEquals("3", actualSaveCategoryRequestVO.getMaxLoginCount());
        assertEquals("Max Txn Msisdn Old", actualSaveCategoryRequestVO.getMaxTxnMsisdnOld());
        assertEquals("Max Txn Msisdns", actualSaveCategoryRequestVO.getMaxTxnMsisdns());
        assertSame(messageGatewayList, actualSaveCategoryRequestVO.getMessageGatewayList());
        assertSame(modifiedMessageGatewayList, actualSaveCategoryRequestVO.getModifiedMessageGatewayList());
        assertEquals("Modify Allowed", actualSaveCategoryRequestVO.getModifyAllowed());
        assertEquals("Multiple Grph Domains", actualSaveCategoryRequestVO.getMultipleGrphDomains());
        assertEquals("Multiple Login Allowed", actualSaveCategoryRequestVO.getMultipleLoginAllowed());
        assertEquals("42", actualSaveCategoryRequestVO.getNumberOfCategories());
        assertEquals("Outlets Allowed", actualSaveCategoryRequestVO.getOutletsAllowed());
        assertEquals("Parent Category Code", actualSaveCategoryRequestVO.getParentCategoryCode());
        assertEquals("Product Type Association Allowed", actualSaveCategoryRequestVO.getProductTypeAssociationAllowed());
        assertEquals("Recharge By Parent Only", actualSaveCategoryRequestVO.getRechargeByParentOnly());
        assertEquals("Restricted Msisdns", actualSaveCategoryRequestVO.getRestrictedMsisdns());
        assertSame(roleFlag, actualSaveCategoryRequestVO.getRoleFlag());
        assertEquals("Scheduled Transfer Allowed", actualSaveCategoryRequestVO.getScheduledTransferAllowed());
        assertEquals("Service Allowed", actualSaveCategoryRequestVO.getServiceAllowed());
        assertEquals("Transfer To List Only", actualSaveCategoryRequestVO.getTransferToListOnly());
        assertEquals("Unctrl Transfer Allowed", actualSaveCategoryRequestVO.getUnctrlTransferAllowed());
        assertEquals("User Id Prefix", actualSaveCategoryRequestVO.getUserIdPrefix());
        assertEquals("View On Network Block", actualSaveCategoryRequestVO.getViewOnNetworkBlock());
    }
}

