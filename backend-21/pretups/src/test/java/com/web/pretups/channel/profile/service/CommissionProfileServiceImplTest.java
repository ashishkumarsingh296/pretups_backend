package com.web.pretups.channel.profile.service;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.web.pretups.channel.profile.web.CommissionProfileModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

@ContextConfiguration(classes = {CommissionProfileServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class CommissionProfileServiceImplTest {
    @Autowired
    private CommissionProfileServiceImpl commissionProfileServiceImpl;

    /**
     * Method under test: {@link CommissionProfileServiceImpl#commissionProfileStatus(String, BindingResult, ModelMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCommissionProfileStatus() throws Exception {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.btsl.common.PretupsRestClient.getInstanceLoadVOObject(PretupsRestClient.java:118)
        //       at com.btsl.common.PretupsRestClient.postJSONRequest(PretupsRestClient.java:56)
        //       at com.web.pretups.channel.profile.service.CommissionProfileServiceImpl.commissionProfileStatus(CommissionProfileServiceImpl.java:55)
        //   See https://diff.blue/R013 to resolve this issue.

        BindException bindingResult = new BindException("Target", "Object Name");

        commissionProfileServiceImpl.commissionProfileStatus("42", bindingResult, new ModelMap());
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#commissionProfileStatus(String, BindingResult, ModelMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCommissionProfileStatus2() throws Exception {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.btsl.common.PretupsRestClient.getInstanceLoadVOObject(PretupsRestClient.java:118)
        //       at com.btsl.common.PretupsRestClient.postJSONRequest(PretupsRestClient.java:56)
        //       at com.web.pretups.channel.profile.service.CommissionProfileServiceImpl.commissionProfileStatus(CommissionProfileServiceImpl.java:55)
        //   See https://diff.blue/R013 to resolve this issue.

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult("Target", "*+xxx");

        commissionProfileServiceImpl.commissionProfileStatus("42", bindingResult, new ModelMap());
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#commissionProfileStatus(String, BindingResult, ModelMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCommissionProfileStatus3() throws Exception {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.btsl.common.PretupsRestClient.getInstanceLoadVOObject(PretupsRestClient.java:118)
        //       at com.btsl.common.PretupsRestClient.postJSONRequest(PretupsRestClient.java:56)
        //       at com.web.pretups.channel.profile.service.CommissionProfileServiceImpl.commissionProfileStatus(CommissionProfileServiceImpl.java:55)
        //   See https://diff.blue/R013 to resolve this issue.

        BeanPropertyBindingResult bindingResult = mock(BeanPropertyBindingResult.class);
        commissionProfileServiceImpl.commissionProfileStatus("42", bindingResult, new ModelMap());
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#commissionProfileSaveSuspend(CommissionProfileModel, String, BindingResult, ModelMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCommissionProfileSaveSuspend() throws Exception {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.btsl.common.PretupsRestClient.getInstanceLoadVOObject(PretupsRestClient.java:118)
        //       at com.btsl.common.PretupsRestClient.postJSONRequest(PretupsRestClient.java:56)
        //       at com.web.pretups.channel.profile.service.CommissionProfileServiceImpl.commissionProfileSaveSuspend(CommissionProfileServiceImpl.java:116)
        //   See https://diff.blue/R013 to resolve this issue.

        CommissionProfileModel commissionProfileModel = new CommissionProfileModel();
        commissionProfileModel.setAddCommRateAsString("Add Comm Rate As String");
        commissionProfileModel.setAddCommType("Add Comm Type");
        commissionProfileModel.setAddRoamCommRateAsString("Add Roam Comm Rate As String");
        commissionProfileModel.setAddRoamCommType("Add Roam Comm Type");
        commissionProfileModel.setAdditionalCommissionTimeSlab("Additional Commission Time Slab");
        commissionProfileModel.setAdditionalProfileList(new ArrayList());
        commissionProfileModel.setAddtnlComStatusName("Addtnl Com Status Name");
        commissionProfileModel.setAmountTypeList(new ArrayList());
        commissionProfileModel.setApplicableFromAdditional("jane.doe@example.org");
        commissionProfileModel.setApplicableFromDate("2020-03-01");
        commissionProfileModel.setApplicableFromHour("jane.doe@example.org");
        commissionProfileModel.setApplicableToAdditional("Applicable To Additional");
        commissionProfileModel.setCategoryCode("Category Code");
        commissionProfileModel.setCategoryCodeDesc("Category Code Desc");
        commissionProfileModel.setCategoryList(new ArrayList<>());
        commissionProfileModel.setCode("Code");
        commissionProfileModel.setCommRateAsString("Comm Rate As String");
        commissionProfileModel.setCommType("Comm Type");
        commissionProfileModel.setCommissionProfileList(new ArrayList());
        commissionProfileModel.setCommissionProfileSetVOs(new ArrayList<>());
        commissionProfileModel.setDefaultProfileIndex(1);
        commissionProfileModel.setDeleteAllowed(true);
        commissionProfileModel.setDiffrentialFactorAsString("Diffrential Factor As String");
        commissionProfileModel.setDomainCode("Domain Code");
        commissionProfileModel.setDomainCodeDesc("Domain Code Desc");
        commissionProfileModel.setDomainList(new ArrayList<>());
        commissionProfileModel.setEndRangeAsString("End Range As String");
        commissionProfileModel.setGatewayCode("Gateway Code");
        commissionProfileModel.setGeographyList(new ArrayList<>());
        commissionProfileModel.setGradeCode("Grade Code");
        commissionProfileModel.setGradeCodeDesc("Grade Code Desc");
        commissionProfileModel.setGradeList(new ArrayList<>());
        commissionProfileModel.setGrphDomainCode("Grph Domain Code");
        commissionProfileModel.setGrphDomainCodeDesc("Grph Domain Code Desc");
        commissionProfileModel.setMaxTransferValueAsStringAdditional("42");
        commissionProfileModel.setMaxTransferValueAsStringCommission("42");
        commissionProfileModel.setMinTransferValueAsStringAdditional("42");
        commissionProfileModel.setMinTransferValueAsStringCommission("42");
        commissionProfileModel.setNetworkName("Network Name");
        commissionProfileModel.setOldCode("Old Code");
        commissionProfileModel.setProductCodeDesc("Product Code Desc");
        commissionProfileModel.setProfileName("foo.txt");
        commissionProfileModel.setRequestType("Request Type");
        commissionProfileModel.setResultCount(3);
        commissionProfileModel.setRoamRecharge("Roam Recharge");
        commissionProfileModel.setSelectCommProfileSetList(new ArrayList<>());
        commissionProfileModel.setServiceTypeDesc("Service Type Desc");
        commissionProfileModel.setShortCode("Short Code");
        commissionProfileModel.setShowAdditionalCommissionFlag("Show Additional Commission Flag");
        commissionProfileModel.setSlabsList(new ArrayList());
        commissionProfileModel.setStartRangeAsString("Start Range As String");
        commissionProfileModel.setSubServiceDesc("Sub Service Desc");
        commissionProfileModel.setTax1RateAsString("Tax1 Rate As String");
        commissionProfileModel.setTax1Type("Tax1 Type");
        commissionProfileModel.setTax2RateAsString("Tax2 Rate As String");
        commissionProfileModel.setTax2Type("Tax2 Type");
        commissionProfileModel.setTax3RateAsString("Tax3 Rate As String");
        commissionProfileModel.setTax3Type("Tax3 Type");
        commissionProfileModel.setTaxOnChannelTransfer("Tax On Channel Transfer");
        commissionProfileModel.setTaxOnFOCApplicable("Tax On FOCApplicable");
        commissionProfileModel.setTransferMultipleOffAsString("Transfer Multiple Off As String");
        commissionProfileModel.setVersion("1.0.2");
        BindException bindingResult = new BindException("Target", "Object Name");

        commissionProfileServiceImpl.commissionProfileSaveSuspend(commissionProfileModel, "42", bindingResult,
                new ModelMap());
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#commissionProfileSaveSuspend(CommissionProfileModel, String, BindingResult, ModelMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testCommissionProfileSaveSuspend2() throws Exception {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.btsl.common.PretupsRestClient.getInstanceLoadVOObject(PretupsRestClient.java:118)
        //       at com.btsl.common.PretupsRestClient.postJSONRequest(PretupsRestClient.java:56)
        //       at com.web.pretups.channel.profile.service.CommissionProfileServiceImpl.commissionProfileSaveSuspend(CommissionProfileServiceImpl.java:116)
        //   See https://diff.blue/R013 to resolve this issue.

        CommissionProfileModel commissionProfileModel = mock(CommissionProfileModel.class);
        doNothing().when(commissionProfileModel).setAddCommRateAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setAddCommType(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setAddRoamCommRateAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setAddRoamCommType(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setAdditionalCommissionTimeSlab(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setAdditionalProfileList(Mockito.<ArrayList<Object>>any());
        doNothing().when(commissionProfileModel).setAddtnlComStatusName(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setAmountTypeList(Mockito.<ArrayList<Object>>any());
        doNothing().when(commissionProfileModel).setApplicableFromAdditional(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setApplicableFromDate(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setApplicableFromHour(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setApplicableToAdditional(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setCategoryCode(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setCategoryCodeDesc(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setCategoryList(Mockito.<ArrayList<CategoryVO>>any());
        doNothing().when(commissionProfileModel).setCode(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setCommRateAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setCommType(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setCommissionProfileList(Mockito.<ArrayList<Object>>any());
        doNothing().when(commissionProfileModel).setCommissionProfileSetVOs(Mockito.<List<CommissionProfileSetVO>>any());
        doNothing().when(commissionProfileModel).setDefaultProfileIndex(Mockito.<Integer>any());
        doNothing().when(commissionProfileModel).setDeleteAllowed(anyBoolean());
        doNothing().when(commissionProfileModel).setDiffrentialFactorAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setDomainCode(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setDomainCodeDesc(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setDomainList(Mockito.<ArrayList<ListValueVO>>any());
        doNothing().when(commissionProfileModel).setEndRangeAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setGatewayCode(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setGeographyList(Mockito.<ArrayList<GeographicalDomainVO>>any());
        doNothing().when(commissionProfileModel).setGradeCode(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setGradeCodeDesc(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setGradeList(Mockito.<ArrayList<GradeVO>>any());
        doNothing().when(commissionProfileModel).setGrphDomainCode(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setGrphDomainCodeDesc(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setMaxTransferValueAsStringAdditional(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setMaxTransferValueAsStringCommission(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setMinTransferValueAsStringAdditional(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setMinTransferValueAsStringCommission(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setNetworkName(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setOldCode(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setProductCodeDesc(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setProfileName(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setRequestType(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setResultCount(anyInt());
        doNothing().when(commissionProfileModel).setRoamRecharge(Mockito.<String>any());
        doNothing().when(commissionProfileModel)
                .setSelectCommProfileSetList(Mockito.<ArrayList<CommissionProfileSetVO>>any());
        doNothing().when(commissionProfileModel).setServiceTypeDesc(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setShortCode(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setShowAdditionalCommissionFlag(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setSlabsList(Mockito.<ArrayList<Object>>any());
        doNothing().when(commissionProfileModel).setStartRangeAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setSubServiceDesc(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setTax1RateAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setTax1Type(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setTax2RateAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setTax2Type(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setTax3RateAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setTax3Type(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setTaxOnChannelTransfer(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setTaxOnFOCApplicable(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setTransferMultipleOffAsString(Mockito.<String>any());
        doNothing().when(commissionProfileModel).setVersion(Mockito.<String>any());
        commissionProfileModel.setAddCommRateAsString("Add Comm Rate As String");
        commissionProfileModel.setAddCommType("Add Comm Type");
        commissionProfileModel.setAddRoamCommRateAsString("Add Roam Comm Rate As String");
        commissionProfileModel.setAddRoamCommType("Add Roam Comm Type");
        commissionProfileModel.setAdditionalCommissionTimeSlab("Additional Commission Time Slab");
        commissionProfileModel.setAdditionalProfileList(new ArrayList());
        commissionProfileModel.setAddtnlComStatusName("Addtnl Com Status Name");
        commissionProfileModel.setAmountTypeList(new ArrayList());
        commissionProfileModel.setApplicableFromAdditional("jane.doe@example.org");
        commissionProfileModel.setApplicableFromDate("2020-03-01");
        commissionProfileModel.setApplicableFromHour("jane.doe@example.org");
        commissionProfileModel.setApplicableToAdditional("Applicable To Additional");
        commissionProfileModel.setCategoryCode("Category Code");
        commissionProfileModel.setCategoryCodeDesc("Category Code Desc");
        commissionProfileModel.setCategoryList(new ArrayList<>());
        commissionProfileModel.setCode("Code");
        commissionProfileModel.setCommRateAsString("Comm Rate As String");
        commissionProfileModel.setCommType("Comm Type");
        commissionProfileModel.setCommissionProfileList(new ArrayList());
        commissionProfileModel.setCommissionProfileSetVOs(new ArrayList<>());
        commissionProfileModel.setDefaultProfileIndex(1);
        commissionProfileModel.setDeleteAllowed(true);
        commissionProfileModel.setDiffrentialFactorAsString("Diffrential Factor As String");
        commissionProfileModel.setDomainCode("Domain Code");
        commissionProfileModel.setDomainCodeDesc("Domain Code Desc");
        commissionProfileModel.setDomainList(new ArrayList<>());
        commissionProfileModel.setEndRangeAsString("End Range As String");
        commissionProfileModel.setGatewayCode("Gateway Code");
        commissionProfileModel.setGeographyList(new ArrayList<>());
        commissionProfileModel.setGradeCode("Grade Code");
        commissionProfileModel.setGradeCodeDesc("Grade Code Desc");
        commissionProfileModel.setGradeList(new ArrayList<>());
        commissionProfileModel.setGrphDomainCode("Grph Domain Code");
        commissionProfileModel.setGrphDomainCodeDesc("Grph Domain Code Desc");
        commissionProfileModel.setMaxTransferValueAsStringAdditional("42");
        commissionProfileModel.setMaxTransferValueAsStringCommission("42");
        commissionProfileModel.setMinTransferValueAsStringAdditional("42");
        commissionProfileModel.setMinTransferValueAsStringCommission("42");
        commissionProfileModel.setNetworkName("Network Name");
        commissionProfileModel.setOldCode("Old Code");
        commissionProfileModel.setProductCodeDesc("Product Code Desc");
        commissionProfileModel.setProfileName("foo.txt");
        commissionProfileModel.setRequestType("Request Type");
        commissionProfileModel.setResultCount(3);
        commissionProfileModel.setRoamRecharge("Roam Recharge");
        commissionProfileModel.setSelectCommProfileSetList(new ArrayList<>());
        commissionProfileModel.setServiceTypeDesc("Service Type Desc");
        commissionProfileModel.setShortCode("Short Code");
        commissionProfileModel.setShowAdditionalCommissionFlag("Show Additional Commission Flag");
        commissionProfileModel.setSlabsList(new ArrayList());
        commissionProfileModel.setStartRangeAsString("Start Range As String");
        commissionProfileModel.setSubServiceDesc("Sub Service Desc");
        commissionProfileModel.setTax1RateAsString("Tax1 Rate As String");
        commissionProfileModel.setTax1Type("Tax1 Type");
        commissionProfileModel.setTax2RateAsString("Tax2 Rate As String");
        commissionProfileModel.setTax2Type("Tax2 Type");
        commissionProfileModel.setTax3RateAsString("Tax3 Rate As String");
        commissionProfileModel.setTax3Type("Tax3 Type");
        commissionProfileModel.setTaxOnChannelTransfer("Tax On Channel Transfer");
        commissionProfileModel.setTaxOnFOCApplicable("Tax On FOCApplicable");
        commissionProfileModel.setTransferMultipleOffAsString("Transfer Multiple Off As String");
        commissionProfileModel.setVersion("1.0.2");
        BindException bindingResult = new BindException("Target", "Object Name");

        commissionProfileServiceImpl.commissionProfileSaveSuspend(commissionProfileModel, "42", bindingResult,
                new ModelMap());
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#loadCommissionProfilePage(String, ModelMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadCommissionProfilePage() throws Exception {
        // TODO: Complete this test.
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.btsl.pretups.channel.profile.businesslogic.CommissionProfileBL.loadDomainListForSuspend(CommissionProfileBL.java:266)
        //       at com.web.pretups.channel.profile.service.CommissionProfileServiceImpl.loadCommissionProfilePage(CommissionProfileServiceImpl.java:136)

        // Arrange
        // TODO: Populate arranged inputs
        String loginId = "";
        ModelMap modelMap = null;

        // Act
        this.commissionProfileServiceImpl.loadCommissionProfilePage(loginId, modelMap);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CommissionProfileServiceImpl#populateCommissionProfilePage(String, CommissionProfileModel, ModelMap)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPopulateCommissionProfilePage() throws Exception {
        // TODO: Complete this test.
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.btsl.pretups.channel.profile.businesslogic.CommissionProfileBL.loadDomainListForSuspend(CommissionProfileBL.java:266)
        //       at com.web.pretups.channel.profile.service.CommissionProfileServiceImpl.populateCommissionProfilePage(CommissionProfileServiceImpl.java:142)

        // Arrange
        // TODO: Populate arranged inputs
        String loginId = "";
        CommissionProfileModel commissionProfileModel = null;
        ModelMap modelMap = null;

        // Act
        this.commissionProfileServiceImpl.populateCommissionProfilePage(loginId, commissionProfileModel, modelMap);

        // Assert
        // TODO: Add assertions on result
    }
}

