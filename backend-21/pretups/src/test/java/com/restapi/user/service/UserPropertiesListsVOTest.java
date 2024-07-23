package com.restapi.user.service;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class UserPropertiesListsVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserPropertiesListsVO}
     *   <li>{@link UserPropertiesListsVO#setDocumentTypeList(ArrayList)}
     *   <li>{@link UserPropertiesListsVO#setGeographyList(ArrayList)}
     *   <li>{@link UserPropertiesListsVO#setGroupRolesList(ArrayList)}
     *   <li>{@link UserPropertiesListsVO#setNotificationLanguageList(ArrayList)}
     *   <li>{@link UserPropertiesListsVO#setPaymentTypeList(ArrayList)}
     *   <li>{@link UserPropertiesListsVO#setSMSCProfileList(ArrayList)}
     *   <li>{@link UserPropertiesListsVO#setServicesList(ArrayList)}
     *   <li>{@link UserPropertiesListsVO#setSystemRolesList(ArrayList)}
     *   <li>{@link UserPropertiesListsVO#setVoucherTypesList(ArrayList)}
     *   <li>{@link UserPropertiesListsVO#toString()}
     *   <li>{@link UserPropertiesListsVO#getDocumentTypeList()}
     *   <li>{@link UserPropertiesListsVO#getGeographyList()}
     *   <li>{@link UserPropertiesListsVO#getGroupRolesList()}
     *   <li>{@link UserPropertiesListsVO#getNotificationLanguageList()}
     *   <li>{@link UserPropertiesListsVO#getPaymentTypeList()}
     *   <li>{@link UserPropertiesListsVO#getSMSCProfileList()}
     *   <li>{@link UserPropertiesListsVO#getServicesList()}
     *   <li>{@link UserPropertiesListsVO#getSystemRolesList()}
     *   <li>{@link UserPropertiesListsVO#getVoucherTypesList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserPropertiesListsVO actualUserPropertiesListsVO = new UserPropertiesListsVO();
        ArrayList<DocumentTypeVO> documentTypeList = new ArrayList<>();
        actualUserPropertiesListsVO.setDocumentTypeList(documentTypeList);
        ArrayList<GeographyVO> geographyList = new ArrayList<>();
        actualUserPropertiesListsVO.setGeographyList(geographyList);
        ArrayList<GroupRoleTypeVO> groupRolesList = new ArrayList<>();
        actualUserPropertiesListsVO.setGroupRolesList(groupRolesList);
        ArrayList<LanguageVO> notificationLanguageList = new ArrayList<>();
        actualUserPropertiesListsVO.setNotificationLanguageList(notificationLanguageList);
        ArrayList<PaymentTypeVO> paymentTypeList = new ArrayList<>();
        actualUserPropertiesListsVO.setPaymentTypeList(paymentTypeList);
        ArrayList<SMSCProfileVO> sMSCProfileList = new ArrayList<>();
        actualUserPropertiesListsVO.setSMSCProfileList(sMSCProfileList);
        ArrayList<ServiceVO> servicesList = new ArrayList<>();
        actualUserPropertiesListsVO.setServicesList(servicesList);
        ArrayList<SystemRoleTypeVO> systemRolesList = new ArrayList<>();
        actualUserPropertiesListsVO.setSystemRolesList(systemRolesList);
        ArrayList<VoucherTypeVO> voucherTypesList = new ArrayList<>();
        actualUserPropertiesListsVO.setVoucherTypesList(voucherTypesList);
        actualUserPropertiesListsVO.toString();
        assertSame(documentTypeList, actualUserPropertiesListsVO.getDocumentTypeList());
        assertSame(geographyList, actualUserPropertiesListsVO.getGeographyList());
        assertSame(groupRolesList, actualUserPropertiesListsVO.getGroupRolesList());
        assertSame(notificationLanguageList, actualUserPropertiesListsVO.getNotificationLanguageList());
        assertSame(paymentTypeList, actualUserPropertiesListsVO.getPaymentTypeList());
        assertSame(sMSCProfileList, actualUserPropertiesListsVO.getSMSCProfileList());
        assertSame(servicesList, actualUserPropertiesListsVO.getServicesList());
        assertSame(systemRolesList, actualUserPropertiesListsVO.getSystemRolesList());
        assertSame(voucherTypesList, actualUserPropertiesListsVO.getVoucherTypesList());
    }
}

