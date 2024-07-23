/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.btsl.user.businesslogic.entity.Lookups;



/**
 * Repository of ProductTypeRepositoryImpl interface.
 *
 * @author VENKATESAN.S
 */
@Component
@CacheConfig(cacheNames = "systemdata_cache")
public class ProductTypeRepositoryImpl implements ProductTypeRepository {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductTypeRepositoryImpl.class);

    /** The entity manager. */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to load the product type list associated for the user (Used For
     * Login User)
     * 
     * @param p_con
     * @param p_userId
     * @return ArrayList
     * 
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    @Override
    @Cacheable
    public ArrayList loadUserProductsListForLogin(String userId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(MessageFormat.format("loadUserProductsListForLogin, Entered userId={0}", userId));
        }

        ArrayList list = new ArrayList();
        Query query;
        try {
            StringBuilder sqlQueryBuild = new StringBuilder();
            sqlQueryBuild.append(" SELECT UPT.productType, LK.lookupName");
            sqlQueryBuild.append(" FROM UserProductTypes UPT, Lookups LK");
            sqlQueryBuild.append(
                    " WHERE UPT.userId =:userid AND UPT.productType=LK.lookupCode AND LK.lookupType=:productType");
            sqlQueryBuild.append(" AND LK.status <> 'N'");
            query = entityManager.createQuery(sqlQueryBuild.toString());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(MessageFormat.format("loadUserProductsListForLogin, QUERY query={0}", query));
            }
            query.setParameter("userid", userId);
            query.setParameter("productType", Constants.PRODUCT_TYPE.getStrValue());
            List<Object[]> productsListObject = query.getResultList();
            if (!CommonUtils.isNullorEmpty(productsListObject)) {
                for (Object[] obj : productsListObject) {
                    ListValues listValues = constructProductData(obj);
                    list.add(listValues);
                }
            }
        } catch (PersistenceException e) {
            LOGGER.error("Exception occurs at loadUserProductsListForLogin {}", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return list;
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    @Override
    public ArrayList<Lookups> loadLookupsList(String lookupType) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(MessageFormat.format("loadLookupsList, Entered userId={0}", lookupType));
        }

        ArrayList list = new ArrayList();
        Query query;
        try {
            StringBuilder sqlQueryBuild = new StringBuilder();
            sqlQueryBuild.append("SELECT L.lookupCode, L.lookupName, L.lookupType, L.modifiedOn, L.status ");
            sqlQueryBuild.append(
                    "FROM Lookups L, LookupTypes LT WHERE LT.lookupType =:lookupType AND L.lookupType=LT.lookupType AND  L.status <> 'N'");
            query = entityManager.createQuery(sqlQueryBuild.toString());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(MessageFormat.format("loadLookupsList, QUERY query={0}", query));
            }
            query.setParameter("lookupType", lookupType);
            List<Object[]> productsListObject = query.getResultList();
            if (!CommonUtils.isNullorEmpty(productsListObject)) {
                for (Object[] obj : productsListObject) {
                    Lookups listValues = constructLookupData(obj);
                    list.add(listValues);
                }
            }
        } catch (PersistenceException e) {
            LOGGER.error("Exception occurs at loadUserProductsListForLogin {}", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return list;

    }

    /**
     * Construct the Product data
     */
    private static ListValues constructProductData(Object[] objects) {
        ListValues listValues = new ListValues();
        listValues = new ListValues((String) objects[NumberConstants.ONE.getIntValue()],
                (String) objects[NumberConstants.ZERO.getIntValue()]);
        return listValues;
    }

    private static Lookups constructLookupData(Object[] objects) {
        Lookups listValues = new Lookups();
        listValues.setLookupCode((String) objects[NumberConstants.ZERO.getIntValue()]);
        listValues.setLookupName((String) objects[NumberConstants.ONE.getIntValue()]);
        listValues.setLookupType((String) objects[NumberConstants.TWO.getIntValue()]);
        listValues.setModifiedOn((Date) objects[NumberConstants.THREE.getIntValue()]);
        listValues.setStatus((String) objects[NumberConstants.FOUR.getIntValue()]);
        return listValues;
    }
}
