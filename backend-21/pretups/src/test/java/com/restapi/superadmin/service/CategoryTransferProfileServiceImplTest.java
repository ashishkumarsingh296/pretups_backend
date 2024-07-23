package com.restapi.superadmin.service;

import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.OAuthUser;
import com.restapi.superadmin.requestVO.CatTrfProfileRequestVO;
import com.restapi.superadmin.responseVO.CatTrfProfileListResponseVO;
import com.restapi.superadmin.responseVO.DomainManagmentResponseVO;

import java.sql.SQLException;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {CategoryTransferProfileServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class CategoryTransferProfileServiceImplTest {
    @Autowired
    private CategoryTransferProfileServiceImpl categoryTransferProfileServiceImpl;

    /**
     * Method under test: {@link CategoryTransferProfileServiceImpl#getCatTrfProfileList(OAuthUser, HttpServletResponse, Locale, String, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetCatTrfProfileList() throws SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.superadmin.service.CategoryTransferProfileServiceImpl.getCatTrfProfileList(CategoryTransferProfileServiceImpl.java:76)

        // Arrange
        // TODO: Populate arranged inputs
        OAuthUser oAuthUser = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;
        String domainCode = "";
        String categoryCode = "";
        String networkCode = "";

        // Act
        CatTrfProfileListResponseVO actualCatTrfProfileList = this.categoryTransferProfileServiceImpl
                .getCatTrfProfileList(oAuthUser, responseSwag, locale, domainCode, categoryCode, networkCode);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryTransferProfileServiceImpl#addCatTrfProfile(Locale, CatTrfProfileRequestVO, HttpServletResponse, OAuthUser)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddCatTrfProfile() throws SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.superadmin.service.CategoryTransferProfileServiceImpl.addCatTrfProfile(CategoryTransferProfileServiceImpl.java:141)

        // Arrange
        // TODO: Populate arranged inputs
        Locale locale = null;
        CatTrfProfileRequestVO requestVO = null;
        HttpServletResponse responseSwag = null;
        OAuthUser oAuthUser = null;

        // Act
        BaseResponse actualAddCatTrfProfileResult = this.categoryTransferProfileServiceImpl.addCatTrfProfile(locale,
                requestVO, responseSwag, oAuthUser);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryTransferProfileServiceImpl#modifyCatTrfProfile(Locale, CatTrfProfileRequestVO, HttpServletResponse, OAuthUser)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testModifyCatTrfProfile() throws SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.superadmin.service.CategoryTransferProfileServiceImpl.modifyCatTrfProfile(CategoryTransferProfileServiceImpl.java:322)

        // Arrange
        // TODO: Populate arranged inputs
        Locale locale = null;
        CatTrfProfileRequestVO requestVO = null;
        HttpServletResponse responseSwag = null;
        OAuthUser oAuthUser = null;

        // Act
        BaseResponse actualModifyCatTrfProfileResult = this.categoryTransferProfileServiceImpl.modifyCatTrfProfile(locale,
                requestVO, responseSwag, oAuthUser);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryTransferProfileServiceImpl#deleteCatTrfProfile(Locale, HttpServletResponse, OAuthUser, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteCatTrfProfile() throws SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.superadmin.service.CategoryTransferProfileServiceImpl.deleteCatTrfProfile(CategoryTransferProfileServiceImpl.java:507)

        // Arrange
        // TODO: Populate arranged inputs
        Locale locale = null;
        HttpServletResponse responseSwag = null;
        OAuthUser oAuthUser = null;
        String profileId = "";

        // Act
        BaseResponse actualDeleteCatTrfProfileResult = this.categoryTransferProfileServiceImpl.deleteCatTrfProfile(locale,
                responseSwag, oAuthUser, profileId);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CategoryTransferProfileServiceImpl#getdomainManagmentList(Locale, HttpServletResponse, OAuthUser, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetdomainManagmentList() throws SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.superadmin.service.CategoryTransferProfileServiceImpl.getdomainManagmentList(CategoryTransferProfileServiceImpl.java:598)

        // Arrange
        // TODO: Populate arranged inputs
        Locale locale = null;
        HttpServletResponse responseSwag = null;
        OAuthUser oAuthUser = null;
        String domainType = "";

        // Act
        DomainManagmentResponseVO actualGetdomainManagmentListResult = this.categoryTransferProfileServiceImpl
                .getdomainManagmentList(locale, responseSwag, oAuthUser, domainType);

        // Assert
        // TODO: Add assertions on result
    }
}

