package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.domain.businesslogic.CategoryVO;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.struts.upload.DiskFile;
import org.apache.struts.upload.FormFile;
import org.junit.Test;

public class BatchUploadAndProcessAssosiateAlertVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BatchUploadAndProcessAssosiateAlertVO}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setCategoryCode(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setCategoryList(ArrayList)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setCategoryName(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setCategoryVO(CategoryVO)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setDomainAllList(ArrayList)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setDomainCode(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setDomainList(ArrayList)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setDomainName(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setDomainType(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setFileName(FormFile)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setFileNameStr(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setGeographyCode(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setGeographyList(ArrayList)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#setGeographyName(String)}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getCategoryCode()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getCategoryList()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getCategoryName()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getCategoryVO()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getDomainAllList()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getDomainCode()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getDomainList()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getDomainName()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getDomainType()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getFileName()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getFileNameStr()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getGeographyCode()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getGeographyList()}
     *   <li>{@link BatchUploadAndProcessAssosiateAlertVO#getGeographyName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BatchUploadAndProcessAssosiateAlertVO actualBatchUploadAndProcessAssosiateAlertVO = new BatchUploadAndProcessAssosiateAlertVO();
        actualBatchUploadAndProcessAssosiateAlertVO.setCategoryCode("Category Code");
        ArrayList categoryList = new ArrayList();
        actualBatchUploadAndProcessAssosiateAlertVO.setCategoryList(categoryList);
        actualBatchUploadAndProcessAssosiateAlertVO.setCategoryName("Category Name");
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setAgentAgentAllowed("Agent Agent Allowed");
        categoryVO.setAgentAllowed("Agent Allowed");
        categoryVO.setAgentAllowedFlag("Agent Allowed Flag");
        categoryVO.setAgentCategoryCode("Agent Category Code");
        categoryVO.setAgentCategoryName("Agent Category Name");
        categoryVO.setAgentCategoryStatus("Agent Category Status");
        categoryVO.setAgentCategoryStatusList(new ArrayList());
        categoryVO.setAgentCategoryType("Agent Category Type");
        categoryVO.setAgentCheckArray(new String[]{"Agent Check Array"});
        categoryVO.setAgentCp2pPayee("Cp2p Payee");
        categoryVO.setAgentCp2pPayer("Cp2p Payer");
        categoryVO.setAgentCp2pWithinList("Cp2p Within List");
        categoryVO.setAgentDisplayAllowed("Agent Display Allowed");
        categoryVO.setAgentDomainCodeforCategory("Agent Domain Codefor Category");
        categoryVO.setAgentDomainName("Agent Domain Name");
        categoryVO.setAgentFixedRoles("Agent Fixed Roles");
        categoryVO.setAgentGatewayName("Agent Gateway Name");
        categoryVO.setAgentGatewayType("Agent Gateway Type");
        categoryVO.setAgentGeographicalDomainList("Agent Geographical Domain List");
        categoryVO.setAgentGrphDomainType("Agent Grph Domain Type");
        categoryVO.setAgentHierarchyAllowed("Agent Hierarchy Allowed");
        categoryVO.setAgentLowBalAlertAllow("Agent Low Bal Alert Allow");
        categoryVO.setAgentMaxLoginCount(3L);
        categoryVO.setAgentMaxTxnMsisdn("Agent Max Txn Msisdn");
        categoryVO.setAgentMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifiedMessageGatewayTypeList(new ArrayList());
        categoryVO.setAgentModifyAllowed("Agent Modify Allowed");
        categoryVO.setAgentMultipleGrphDomains("Agent Multiple Grph Domains");
        categoryVO.setAgentMultipleLoginAllowed("Agent Multiple Login Allowed");
        categoryVO.setAgentOutletsAllowed("Agent Outlets Allowed");
        categoryVO.setAgentParentOrOwnerRadioValue("42");
        categoryVO.setAgentProductTypeAssociationAllowed("Agent Product Type Association Allowed");
        categoryVO.setAgentRechargeByParentOnly("Recharge By Parent Only");
        categoryVO.setAgentRestrictedMsisdns("Agent Restricted Msisdns");
        categoryVO.setAgentRoleName("Agent Role Name");
        categoryVO.setAgentRoleTypeList(new ArrayList());
        categoryVO.setAgentRolesMapSelected(new HashMap());
        categoryVO.setAgentScheduledTransferAllowed("Agent Scheduled Transfer Allowed");
        categoryVO.setAgentServiceAllowed("Agent Service Allowed");
        categoryVO.setAgentSmsInterfaceAllowed("Agent Sms Interface Allowed");
        categoryVO.setAgentUnctrlTransferAllowed("Agent Unctrl Transfer Allowed");
        categoryVO.setAgentUserIdPrefix("Agent User Id Prefix");
        categoryVO.setAgentViewOnNetworkBlock("Agent View On Network Block");
        categoryVO.setAgentWebInterfaceAllowed("Agent Web Interface Allowed");
        categoryVO.setAllowedGatewayTypes(new ArrayList());
        categoryVO.setAuthenticationType("Type");
        categoryVO.setCategoryCode("Category Code");
        categoryVO.setCategoryName("Category Name");
        categoryVO.setCategorySequenceNumber(10);
        categoryVO.setCategoryStatus("Category Status");
        categoryVO.setCategoryType("Category Type");
        categoryVO.setCategoryTypeCode("Category Type Code");
        categoryVO.setCp2pPayee("Payee");
        categoryVO.setCp2pPayer("Payer");
        categoryVO.setCp2pWithinList("Within List");
        categoryVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        categoryVO.setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setDisplayAllowed("Display Allowed");
        categoryVO.setDomainAllowed("Domain Allowed");
        categoryVO.setDomainCodeforCategory("Domain Codefor Category");
        categoryVO.setDomainName("Domain Name");
        categoryVO.setDomainTypeCode("Domain Type Code");
        categoryVO.setFixedDomains("Fixed Domains");
        categoryVO.setFixedRoles("Fixed Roles");
        categoryVO.setGeographicalDomainSeqNo(1);
        categoryVO.setGrphDomainSequenceNo(1);
        categoryVO.setGrphDomainType("Grph Domain Type");
        categoryVO.setGrphDomainTypeName("Grph Domain Type Name");
        categoryVO.setHierarchyAllowed("Hierarchy Allowed");
        categoryVO.setLastModifiedTime(1L);
        categoryVO.setLowBalAlertAllow("Low Bal Alert Allow");
        categoryVO.setMaxLoginCount(3L);
        categoryVO.setMaxTxnMsisdn("Max Txn Msisdn");
        categoryVO.setMaxTxnMsisdnInt(3);
        categoryVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        categoryVO.setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        categoryVO.setModifyAllowed("Modify Allowed");
        categoryVO.setMultipleGrphDomains("Multiple Grph Domains");
        categoryVO.setMultipleLoginAllowed("Multiple Login Allowed");
        categoryVO.setNumberOfCategoryForDomain(10);
        categoryVO.setOutletsAllowed("Outlets Allowed");
        categoryVO.setParentCategoryCode("Parent Category Code");
        categoryVO.setParentOrOwnerRadioValue("42");
        categoryVO.setProductTypeAllowed("Product Type Allowed");
        categoryVO.setProductTypeAssociationAllowed("Product Type Association Allowed");
        categoryVO.setRadioIndex(1);
        categoryVO.setRechargeByParentOnly("By Parent Only");
        categoryVO.setRecordCount(3);
        categoryVO.setRestrictedMsisdns("Restricted Msisdns");
        categoryVO.setScheduledTransferAllowed("Scheduled Transfer Allowed");
        categoryVO.setSequenceNumber(10);
        categoryVO.setServiceAllowed("Service Allowed");
        categoryVO.setSmsInterfaceAllowed("Sms Interface Allowed");
        categoryVO.setTransferToListOnly("Transfer To List Only");
        categoryVO.setTxnOutsideHierchy("Txn Outside Hierchy");
        categoryVO.setUnctrlTransferAllowed("Unctrl Transfer Allowed");
        categoryVO.setUserIdPrefix("User Id Prefix");
        categoryVO.setViewOnNetworkBlock("View On Network Block");
        categoryVO.setWebInterfaceAllowed("Web Interface Allowed");
        actualBatchUploadAndProcessAssosiateAlertVO.setCategoryVO(categoryVO);
        ArrayList domainAllList = new ArrayList();
        actualBatchUploadAndProcessAssosiateAlertVO.setDomainAllList(domainAllList);
        actualBatchUploadAndProcessAssosiateAlertVO.setDomainCode("Domain Code");
        ArrayList domainList = new ArrayList();
        actualBatchUploadAndProcessAssosiateAlertVO.setDomainList(domainList);
        actualBatchUploadAndProcessAssosiateAlertVO.setDomainName("Domain Name");
        actualBatchUploadAndProcessAssosiateAlertVO.setDomainType("Domain Type");
        DiskFile name = new DiskFile("/directory/foo.txt");
        actualBatchUploadAndProcessAssosiateAlertVO.setFileName(name);
        actualBatchUploadAndProcessAssosiateAlertVO.setFileNameStr("Name Str");
        actualBatchUploadAndProcessAssosiateAlertVO.setGeographyCode("Geography Code");
        ArrayList geographyList = new ArrayList();
        actualBatchUploadAndProcessAssosiateAlertVO.setGeographyList(geographyList);
        actualBatchUploadAndProcessAssosiateAlertVO.setGeographyName("Geography Name");
        assertEquals("Category Code", actualBatchUploadAndProcessAssosiateAlertVO.getCategoryCode());
        assertSame(categoryList, actualBatchUploadAndProcessAssosiateAlertVO.getCategoryList());
        assertEquals("Category Name", actualBatchUploadAndProcessAssosiateAlertVO.getCategoryName());
        assertSame(categoryVO, actualBatchUploadAndProcessAssosiateAlertVO.getCategoryVO());
        assertSame(domainAllList, actualBatchUploadAndProcessAssosiateAlertVO.getDomainAllList());
        assertEquals("Domain Code", actualBatchUploadAndProcessAssosiateAlertVO.getDomainCode());
        assertSame(domainList, actualBatchUploadAndProcessAssosiateAlertVO.getDomainList());
        assertEquals("Domain Name", actualBatchUploadAndProcessAssosiateAlertVO.getDomainName());
        assertEquals("Domain Type", actualBatchUploadAndProcessAssosiateAlertVO.getDomainType());
        assertSame(name, actualBatchUploadAndProcessAssosiateAlertVO.getFileName());
        assertEquals("Name Str", actualBatchUploadAndProcessAssosiateAlertVO.getFileNameStr());
        assertEquals("Geography Code", actualBatchUploadAndProcessAssosiateAlertVO.getGeographyCode());
        assertSame(geographyList, actualBatchUploadAndProcessAssosiateAlertVO.getGeographyList());
        assertEquals("Geography Name", actualBatchUploadAndProcessAssosiateAlertVO.getGeographyName());
    }
}

