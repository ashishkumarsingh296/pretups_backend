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


/**
 * Repository of DenomProfileRepository interface.
 *
 * @author VENKATESAN.S
 */
@Component
@CacheConfig(cacheNames = "systemdata_cache")
public class DomainRepositoryImpl implements DomainRepository {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainRepositoryImpl.class);

    /** The entity manager. */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method for loading Domain List By UserId.
     * 
     * Used in (UserAction, ChannelUserAction)
     * 
     * @author VENKATESAN.S
     * 
     * @param userId
     *            String
     * @return ArrayList
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    @Cacheable
    public ArrayList<ListValues> loadDomainListByUserId(String userId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(MessageFormat.format("loadDomainListByUserId Entered userId:{0}", userId));
        }
        ArrayList<ListValues> list = new ArrayList<>();
        Query query;
        try {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append(" SELECT d.domainCode, d.domainName, dt.restrictedMsisdn, dt.displayAllowed");
            sqlQuery.append(" FROM Domains d, UserDomains ud, DomainTypes dt");
            sqlQuery.append(" WHERE d.status <> 'N' AND ud.userId =:userId");
            sqlQuery.append(" AND dt.domainTypeCode=d.domainTypeCode");
            sqlQuery.append(" AND d.domainCode = ud.domainCode");
            sqlQuery.append(" ORDER BY d.domainName");
            query = entityManager.createQuery(sqlQuery.toString());
            query.setParameter("userId", userId);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(MessageFormat.format("loadDomainListByUserId QUERY sqlQuery= {0}", query));
            }
            List<Object[]> domainObject = query.getResultList();
            if (!CommonUtils.isNullorEmpty(domainObject)) {
                for (Object[] obj : domainObject) {
                    ListValues listValues = constructDomainList(obj);
                    list.add(listValues);
                }
            }
        } catch (PersistenceException e) {
            LOGGER.error("Exception occurs at loadDomainListByUserId {}", e);
            throw new ApplicationException(MessageCodes.GENERIC_ERROR.getStrValue());
        }
        return list;
    }

    /**
     * Construct the MenuItem data
     */
    private static ListValues constructDomainList(Object[] objects) {
        ListValues listValues = new ListValues();
        listValues = new ListValues((String) objects[NumberConstants.ONE.getIntValue()],
                (String) objects[NumberConstants.ZERO.getIntValue()]);
        listValues.setType((String) objects[NumberConstants.TWO.getIntValue()]);
        listValues.setStatus((String) objects[NumberConstants.THREE.getIntValue()]);
        return listValues;
    }

}
